$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	//Arreglo los tipos de Periodicidad
	var arrayTiposPeriod;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/facnomina";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $nuevo = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('N&oacute;mina');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_no_periodo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_no_periodo]');
	var $busqueda_titulo_periodo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_titulo_periodo]');
	var $busqueda_select_tipo_periodo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_tipo_periodo]');
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
		valor_retorno += "no_periodo" + signo_separador + $busqueda_no_periodo.val() + "|";
		valor_retorno += "titulo_periodo" + signo_separador + $busqueda_titulo_periodo.val() + "|";
		valor_retorno += "tipo_periodo" + signo_separador + $busqueda_select_tipo_periodo.val()+ "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
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
	
	
	//Esta funcion obtiene datos para el buscador principal
	$cargar_datos_buscador_principal= function(){
		var input_json_buscador = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosParaBuscador.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_buscador,$arreglo,function(data){
			$busqueda_select_tipo_periodo.children().remove();
			var select_hmtl = '<option value="0">[-Seleccionar Tipo-]</option>';
			$.each(data['TiposPeriodicidadBusqueda'],function(entryIndex,data){
				select_hmtl += '<option value="' + data['id'] + '" >' + data['titulo'] + '</option>';
			});
			$busqueda_select_tipo_periodo.append(select_hmtl);
		
		});
	}
	
	//Llamada a funcion
	$cargar_datos_buscador_principal();
	
	var $limpiar_campos_busqueda= function(){
		$busqueda_no_periodo.val('');
		$busqueda_titulo_periodo.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		
		//Llamada a funcion
		$cargar_datos_buscador_principal();
		
		$get_datos_grid();
	}
	
	
	

	
	$limpiar.click(function(event){
		$limpiar_campos_busqueda();
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
		$busqueda_no_periodo.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_no_periodo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_titulo_periodo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_tipo_periodo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	
	
	//----------------------------------------------------------------
	//valida la fecha seleccionada
	function fecha_mayor(fecha, fecha2){
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
        
	//Valida la fecha seleccionada
	function fecha_mayor_igual(fecha, fecha2){
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
					if (xDia >= yDia){
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
    
	//Muestra la fecha actual
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
	
	
	$add_calendar = function($campo, $fecha, $condicion){
		$campo.click(function (s){
			//$campo.val(null);
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$campo.DatePicker({
			format:'Y-m-d',
			date: $campo.val(),
			current: $campo.val(),
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
				$campo.val(formated);
				if (formated.match(patron) ){
					switch($condicion){
						case '>':
							var valida_fecha=fecha_mayor($campo.val(),mostrarFecha());
							if (valida_fecha==true){
								$campo.DatePickerHide();
							}else{
								jAlert("Fecha no valida. Debe ser mayor a la actual",'! Atencion');
								$campo.val($fecha);
							}
							break;
						case '>=':
							var valida_fecha=fecha_mayor_igual($campo.val(),mostrarFecha());
							if (valida_fecha==true){
								$campo.DatePickerHide();
							}else{
								jAlert("Fecha no valida. Debe ser mayor o igual a la actual",'! Atencion');
								$campo.val($fecha);
							}
							break;
						case '==':
							//code;
							break;
						case '<':
							//code;
							break;
						case '<=':
							//code;
							break;
						default:
							//para cunado no se le pasan parametros de condicion de fecha
							var valida_fecha=fecha_mayor($campo.val(),mostrarFecha());
							$campo.DatePickerHide();
							break;
					}
				}
			}
		});
	}
	
    //Campos del Buscador
	$add_calendar($busqueda_fecha_inicial, " ", "");
	$add_calendar($busqueda_fecha_final, " ", "");
	
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-facnomina-window').find('select[name=prodtipo]');
		$('#forma-facnomina-window').find('#submit').mouseover(function(){
			$('#forma-facnomina-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-facnomina-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-facnomina-window').find('#submit').mouseout(function(){
			$('#forma-facnomina-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-facnomina-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-facnomina-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-facnomina-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-facnomina-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-facnomina-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-facnomina-window').find('#close').mouseover(function(){
			$('#forma-facnomina-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-facnomina-window').find('#close').mouseout(function(){
			$('#forma-facnomina-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-facnomina-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-facnomina-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-facnomina-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-facnomina-window').find("ul.pestanas li").click(function() {
			$('#forma-facnomina-window').find(".contenidoPes").hide();
			$('#forma-facnomina-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-facnomina-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	$tabs_li_funxionalidad_nominaempleado = function(){
		$('#forma-nominaempleado-window').find('#boton_actualizar_forma_consignacion').mouseover(function(){
			$('#forma-nominaempleado-window').find('#boton_actualizar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-nominaempleado-window').find('#boton_actualizar_forma_consignacion').mouseout(function(){
			$('#forma-nominaempleado-window').find('#boton_actualizar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		
		
		$('#forma-nominaempleado-window').find('#boton_cancelar_forma_consignacion').mouseover(function(){
			$('#forma-nominaempleado-window').find('#boton_cancelar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-nominaempleado-window').find('#boton_cancelar_forma_consignacion').mouseout(function(){
			$('#forma-nominaempleado-window').find('#boton_cancelar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-nominaempleado-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-nominaempleado-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-nominaempleado-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-nominaempleado-window').find("ul.pestanas li").click(function() {
			$('#forma-nominaempleado-window').find(".contenidoPes").hide();
			$('#forma-nominaempleado-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-nominaempleado-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	//Funcion para hacer que un campo solo acepte numeros
	$permitir_solo_numeros = function($campo){
		$campo.keypress(function(e){
			//Permitir  numeros, borrar, suprimir, TAB, puntos
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	$aplicar_evento_focus_input_numeric = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val().trim()=='' || parseInt($(this).val())==0){
				$(this).val('');
			}
			$campo_input.css({'background' : '#ffffff'});
		});
	}
	
	$aplicar_evento_focus_input_text = function( $campo_input ){
		$campo_input.focus(function(e){
			$campo_input.css({'background' : '#ffffff'});
		});
	}
	
	$aplicar_evento_blur_input_numeric = function($campo_input, ejecutar_calculo){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if($campo_input.val().trim()==''){
				$campo_input.val(0);
			}
			$campo_input.val(parseFloat($campo_input.val()).toFixed(2));
			
			if(ejecutar_calculo){
				$calcula_totales();
			}
			
			if(parseFloat($campo_input.val())>0){
				$campo_input.css({'background' : '#ffffff'});
			}
			
		});
	}
	
	$aplicar_evento_blur_input_text = function( $campo_input ){
		$campo_input.blur(function(e){
			if($campo_input.val().trim()!=''){
				$campo_input.css({'background' : '#ffffff'});
			}
		});
	}
	


	
	//Agregar datos del remitente
	$agregar_datos_remitente = function($rem_id, $nombre_remitente, $noremitente, $dir_remitente, $busca_remitente, rem_id, rem_nombre, rem_numero, rem_dir){
		$rem_id.val(rem_id);
		$nombre_remitente.val(rem_nombre);
		$noremitente.val(rem_numero);
		$dir_remitente.val(rem_dir);
		
		//Aplicar solo lectura una vez que se ha escogido un remitente
		$aplicar_readonly_input($nombre_remitente);
		
		//Oculta link buscar remitente
		$busca_remitente.hide();
		
		$noremitente.focus();
	}
	
	//Buscador de Remitentes
	$busca_remitentes= function($rem_id, $nombre_remitente, $noremitente, $dir_remitente, $id_cliente, $busca_remitente){
		$(this).modalPanel_buscaremitente();
		var $dialogoc =  $('#forma-buscaremitente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_remitentes').find('table.formaBusqueda_remitentes').clone());
		$('#forma-buscaremitente-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscaremitente-window').find('#tabla_resultado');
		
		var $boton_buscaremitente = $('#forma-buscaremitente-window').find('#boton_buscaremitente');
		var $cancelar_busqueda = $('#forma-buscaremitente-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscaremitente-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscaremitente-window').find('select[name=filtropor]');
		
		//funcionalidad botones
		$boton_buscaremitente.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_buscaremitente.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$boton_buscaremitente.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$boton_buscaremitente.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		var html = '';		
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		
		if($noremitente.val() !='' && $nombre_remitente.val()==''){
			html+='<option value="1" selected="yes">No. de control</option>';
			$cadena_buscar.val($noremitente.val());
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if($nombre_remitente.val()!=''){
			$cadena_buscar.val($nombre_remitente.val());
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		if($noremitente.val() =='' && $nombre_remitente.val()==''){
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		$select_filtro_por.append(html);
		
		//click buscar clientes
		$boton_buscaremitente.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorRemitentes.json';
			$arreglo = {'cadena':$cadena_buscar.val(),
						 'filtro':$select_filtro_por.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Remitentes'],function(entryIndex,remitente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="id" value="'+remitente['id']+'">';
							trr += '<input type="hidden" id="dir" value="'+remitente['dir']+'">';
							trr += '<span class="no_control">'+remitente['folio']+'</span>';
						trr += '</td>';
						trr += '<td width="145"><span class="rfc">'+remitente['rfc']+'</span></td>';
						trr += '<td width="375"><span class="razon">'+remitente['razon_social']+'</span></td>';
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
				
				//Seleccionar un elemento del resultado
				$tabla_resultados.find('tr').click(function(){
					var rem_id = $(this).find('#id').val();
					var rem_nombre = $(this).find('span.razon').html();
					var rem_numero = $(this).find('span.no_control').html();
					var rem_dir = $(this).find('#dir').val();
					
					$agregar_datos_remitente($rem_id, $nombre_remitente, $noremitente, $dir_remitente, $busca_remitente, rem_id, rem_nombre, rem_numero, rem_dir);
					
					//Elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaremitente-overlay').fadeOut(remove);

					$nombre_remitente.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$boton_buscaremitente.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $boton_buscaremitente);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $boton_buscaremitente);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-buscaremitente-overlay').fadeOut(remove);
			
			//$('#forma-clientsdf-window').find('input[name=cliente]').focus();
		});		
		$cadena_buscar.focus();
	}//Termina buscador de Remitentes
	

    
    //funcion para aplicar evento a trs de una tabla para permitir seleccionar elemento desde el teclado
    $aplicarEventoSeleccionarTrkeypress = function($grid){
		var tr = $("tr", $grid).size();
		tr;
		
		//$('tr:first', $grid).css({background : '#FBD850'});
		$('tr:eq(0)', $grid).find('td').css({background : '#FBD850'});
		
		$('tr:eq(0)', $grid).focus();
		
		$campo_sku.onkeyup(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
	}
	
	
	
	

	
	
	
	
	

    
	
	//Calcula totales(subtotal, impuesto, total)
	$calcula_totales = function(){
		var $concepto_cantidad = $('#forma-nominaempleado-window').find('input[name=concepto_cantidad]');
		var $concepto_valor_unitario = $('#forma-nominaempleado-window').find('input[name=concepto_valor_unitario]');
		var $concepto_importe = $('#forma-nominaempleado-window').find('input[name=concepto_importe]');
		
		var $descuento = $('#forma-nominaempleado-window').find('input[name=descuento]');
		
		var $importe_retencion = $('#forma-nominaempleado-window').find('input[name=importe_retencion]');
		
		var $comp_subtotal = $('#forma-nominaempleado-window').find('input[name=comp_subtotal]');
		var $comp_descuento = $('#forma-nominaempleado-window').find('input[name=comp_descuento]');
		var $comp_retencion = $('#forma-nominaempleado-window').find('input[name=comp_retencion]');
		var $comp_total = $('#forma-nominaempleado-window').find('input[name=comp_total]');
		
		var $grid_percepciones = $('#forma-nominaempleado-window').find('#grid_percepciones');
		var $percep_total_gravado = $('#forma-nominaempleado-window').find('input[name=percep_total_gravado]');
		var $percep_total_excento = $('#forma-nominaempleado-window').find('input[name=percep_total_excento]');
		
		var $grid_deducciones = $('#forma-nominaempleado-window').find('#grid_deducciones');
		var $deduc_total_gravado = $('#forma-nominaempleado-window').find('input[name=deduc_total_gravado]');
		var $deduc_total_excento = $('#forma-nominaempleado-window').find('input[name=deduc_total_excento]');
		
		var $grid_horas_extras = $('#forma-nominaempleado-window').find('#grid_horas_extras');
		
		var $grid_incapacidades = $('#forma-nominaempleado-window').find('#grid_incapacidades');
		
		
		var suma_percep_gravado=0;
		var suma_percep_excento=0;
		$grid_percepciones.find('tr').each(function (index){
			if($(this).find('input[name=percep_monto_gravado]').val().trim()!=''){
				suma_percep_gravado = parseFloat(suma_percep_gravado) + parseFloat($(this).find('input[name=percep_monto_gravado]').val());
			}
			if($(this).find('input[name=percep_monto_excento]').val().trim()!=''){
				suma_percep_excento = parseFloat(suma_percep_excento) + parseFloat($(this).find('input[name=percep_monto_excento]').val());
			}
		});
		$percep_total_gravado.val(parseFloat(suma_percep_gravado).toFixed(2));
		$percep_total_excento.val(parseFloat(suma_percep_excento).toFixed(2));
		
		
		var suma_deduc_gravado=0;
		var suma_deduc_excento=0;
		var monto_isr_gravado=0;
		var monto_isr_excento=0;
		$grid_deducciones.find('tr').each(function (index){
			if($(this).find('input[name=deduc_monto_gravado]').val().trim()!=''){
				if(parseInt($(this).find('#tdeduc_id').val().trim())==2){
					monto_isr_gravado = parseFloat(monto_isr_gravado) + parseFloat($(this).find('input[name=deduc_monto_gravado]').val());
				}else{
					suma_deduc_gravado = parseFloat(suma_deduc_gravado) + parseFloat($(this).find('input[name=deduc_monto_gravado]').val());
				}
			}
			if($(this).find('input[name=deduc_monto_excento]').val().trim()!=''){
				if(parseInt($(this).find('#tdeduc_id').val().trim())==2){
					monto_isr_excento = parseFloat(monto_isr_excento) + parseFloat($(this).find('input[name=deduc_monto_excento]').val());
				}else{
					suma_deduc_excento = parseFloat(suma_deduc_excento) + parseFloat($(this).find('input[name=deduc_monto_excento]').val());
				}
			}
		});
		$deduc_total_gravado.val(parseFloat(parseFloat(suma_deduc_gravado) + parseFloat(monto_isr_gravado)).toFixed(2));
		$deduc_total_excento.val(parseFloat(parseFloat(suma_deduc_excento) + parseFloat(monto_isr_excento)).toFixed(2));
		
		$descuento.val(parseFloat(parseFloat(suma_deduc_gravado) + parseFloat(suma_deduc_excento)).toFixed(2));
		$importe_retencion.val(parseFloat(parseFloat(monto_isr_gravado) + parseFloat(monto_isr_excento)).toFixed(2));
		
		/*
		$grid_horas_extras.find('tr').each(function (index){
			$(this).find('input[name=id_he]').val()
			$(this).find('input[name=noTrhe]').val()
			$(this).find('select[name=select_tipo_he]').val()
			$(this).find('input[name=he_no_dias]').val()
			$(this).find('input[name=he_no_horas]').val()
			$(this).find('input[name=he_importe]').val()
		});
		
		$grid_incapacidades.find('tr').each(function (index){
			cadena = $(this).find('input[name=id_incapacidad]').val()
			$(this).find('input[name=noTrIncapacidad]').val()
			$(this).find('select[name=select_tipo_incapacidad]').val()
			$(this).find('input[name=incapacidad_no_dias]').val()
			$(this).find('input[name=incapacidad_importe]').val()
		});
		*/
		
		$concepto_valor_unitario.val(parseFloat(parseFloat($percep_total_gravado.val()) + parseFloat($percep_total_excento.val())).toFixed(2));
		if($concepto_cantidad.val().trim()==''){
			$concepto_cantidad.val(parseFloat(1).toFixed(2));
		}
		$concepto_importe.val(parseFloat(parseFloat($concepto_cantidad.val()) * parseFloat($concepto_valor_unitario.val())).toFixed(2));
		
		$comp_subtotal.val(parseFloat($concepto_importe.val()).toFixed(2));
		$comp_descuento.val(parseFloat($descuento.val()).toFixed(2));
		$comp_retencion.val(parseFloat($importe_retencion.val()).toFixed(2));
		
		$comp_total.val(parseFloat(parseFloat($comp_subtotal.val()) - parseFloat($comp_descuento.val()) -parseFloat($comp_retencion.val())).toFixed(2));
	}
	//Termina calcular totales
	
	
	
	
	
	
	
	$aplicar_readonly_input = function($input){
		$input.css({'background' : '#f0f0f0'});
		$input.attr('readonly',true);
	}
	
	$quitar_readonly_input = function($input){
		$input.css({'background' : '#ffffff'});
		$input.attr('readonly',false);
	}
	
	//Carga select con arreglo Indice=Valor
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, fijo){
		//la variable fijo indica que solo se mostrará el elemento seleccionado sin dar opción a cambiar
		
		$campo_select.children().remove();
		var select_html = '';
		
		if(texto_elemento_cero.trim()!=''){
			select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		}
		$.each(arreglo_elementos,function(entryIndex,data){
			if(parseInt(elemento_seleccionado)==parseInt(data[campo_indice])){
				select_html += '<option value="' + data[campo_indice] + '" selected="yes">' + data[campo_valor] + '</option>';
			}else{
				if(!fijo){
					select_html += '<option value="' + data[campo_indice] + '" >' + data[campo_valor] + '</option>';
				}
			}
		});
		$campo_select.children().remove();
		$campo_select.append(select_html);
	}
	
	
	
	
	
	
	
	
	
	$agregar_tr_percepcion = function($grid_percepciones, id_percep, tipo_agrupador, percepcion, monto_gravado, monto_excento){
		//Obtiene numero de trs
		var tr = $("tr", $grid_percepciones).size();
		tr++;
		
		var trr = '';
		trr = '<tr>';
		trr += '<td class="grid3" width="60">';
			trr += '<a href="#" class="delete_percep'+ tr +'">Eliminar</a>';
			trr += '<input type="hidden" name="id_percep" id="id_percep" value="'+ id_percep +'">';
			trr += '<input type="hidden" name="noTrPercep" value="'+ tr +'">';
		trr += '</td>';
		trr += '<td class="grid3" width="200">';
			trr += '<input type="text" name="tpercep'+ tr +'" value="'+tipo_agrupador+'" id="tpercep" class="borde_oculto" readOnly="true" style="width:196px;">';
		trr += '</td>';
		trr += '<td class="grid3" width="300">';
			trr += '<input type="text" name="percepcion'+ tr +'" value="'+percepcion+'" id="tdeduc" class="borde_oculto" readOnly="true" style="width:296px;">';
		trr += '</td>';
		trr += '<td class="grid3" width="110">';
			trr += '<input type="text" name="percep_monto_gravado" value="'+monto_gravado+'" id="pmg" class="percep_monto_gravado'+ tr +'" style="width:106px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid3" width="120">';
			trr += '<input type="text" name="percep_monto_excento" value="'+monto_excento+'" id="pme" class="percep_monto_excento'+ tr +'" style="width:116px; text-align:right;">';
		trr += '</td>';
		trr += '</tr>';
		$grid_percepciones.append(trr);
		
		
		$permitir_solo_numeros($grid_percepciones.find('input.percep_monto_gravado'+ tr));
		$permitir_solo_numeros($grid_percepciones.find('input.percep_monto_excento'+ tr));
		
		$aplicar_evento_focus_input_numeric($grid_percepciones.find('input.percep_monto_gravado'+ tr));
		$aplicar_evento_focus_input_numeric($grid_percepciones.find('input.percep_monto_excento'+ tr));
		
		$aplicar_evento_blur_input_numeric($grid_percepciones.find('input.percep_monto_gravado'+ tr), true);
		$aplicar_evento_blur_input_numeric($grid_percepciones.find('input.percep_monto_excento'+ tr), true);
		
		
		$grid_percepciones.find('a.delete_percep'+ tr).click(function(event){
			event.preventDefault();
			$fila=$(this).parent().parent();
			
			//Eliminar el tr
			$fila.remove();
			
			//Recalcular totales
			$calcula_totales();
		});
	}
	
	

	$agregar_tr_deduccion = function($grid_deducciones, id_deduc, tipo_deduc_id, tipo_agrupador, deduccion, monto_gravado, monto_excento){
		//Obtiene numero de trs
		var tr = $("tr", $grid_deducciones).size();
		tr++;
		
		var trr = '';
		trr = '<tr>';
		trr += '<td class="grid3" width="60">';
			trr += '<a href="#" class="delete_deduc'+ tr +'">Eliminar</a>';
			trr += '<input type="hidden" name="id_deduc" id="id_deduc" value="'+ id_deduc +'">';
			trr += '<input type="hidden" name="tdeduc_id" id="tdeduc_id" value="'+ tipo_deduc_id +'">';
			trr += '<input type="hidden" name="noTrDeduc" value="'+ tr +'">';
		trr += '</td>';
		trr += '<td class="grid3" width="200">';
			trr += '<input type="text" name="tdeduc'+ tr +'" value="'+tipo_agrupador+'" id="tpercep" class="borde_oculto" readOnly="true" style="width:196px;">';
		trr += '</td>';
		trr += '<td class="grid3" width="300">';
			trr += '<input type="text" name="deduccion'+ tr +'" value="'+deduccion+'" id="tdeduc" class="borde_oculto" readOnly="true" style="width:296px;">';
		trr += '</td>';
		trr += '<td class="grid3" width="110">';
			trr += '<input type="text" name="deduc_monto_gravado" value="'+monto_gravado+'" id="pmg" class="deduc_monto_gravado'+ tr +'" style="width:106px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid3" width="120">';
			trr += '<input type="text" name="deduc_monto_excento" value="'+monto_excento+'" id="pme" class="deduc_monto_excento'+ tr +'" style="width:116px; text-align:right;">';
		trr += '</td>';
		trr += '</tr>';
		$grid_deducciones.append(trr);
		
		
		$permitir_solo_numeros($grid_deducciones.find('input.deduc_monto_gravado'+ tr));
		$permitir_solo_numeros($grid_deducciones.find('input.deduc_monto_excento'+ tr));
		
		$aplicar_evento_focus_input_numeric($grid_deducciones.find('input.deduc_monto_gravado'+ tr));
		$aplicar_evento_focus_input_numeric($grid_deducciones.find('input.deduc_monto_excento'+ tr));
		
		$aplicar_evento_blur_input_numeric($grid_deducciones.find('input.deduc_monto_gravado'+ tr), true);
		$aplicar_evento_blur_input_numeric($grid_deducciones.find('input.deduc_monto_excento'+ tr), true);
		
		$grid_deducciones.find('a.delete_deduc'+ tr).click(function(event){
			event.preventDefault();
			$fila=$(this).parent().parent();
			//Eliminar el tr
			$fila.remove();
			
			//Recalcular totales
			$calcula_totales();
		});
	}
	//Termina agregar tr deduccion
	
	
	//Crea tr para grid de Horas Estras
	$agregar_tr_hora_extra = function($grid_horas_extras, id_reg, id_tipo_hr, arrayTiposHrsExtra, no_dias, no_hrs, importe){
		//Obtiene numero de trs
		var tr = $("tr", $grid_horas_extras).size();
		tr++;
		
		var trr = '';
		trr = '<tr>';
		trr += '<td class="grid3" width="60">';
			trr += '<a href="#" class="delete_he'+ tr +'">Eliminar</a>';
			trr += '<input type="hidden" name="id_he" id="id_he" value="'+ id_reg +'">';
			trr += '<input type="hidden" name="noTrhe" value="'+ tr +'">';
		trr += '</td>';
		trr += '<td class="grid3" width="150">';
			trr += '<select name="select_tipo_he" class="select_tipo_he'+ tr +'" style="width:146px;"></select>';
			trr += '<input type="hidden" name="selected_he" class="selected_he'+ tr +'" value="0">';
		trr += '</td>';
		trr += '<td class="grid3" width="110">';
			trr += '<input type="text" name="he_no_dias" value="'+no_dias+'" id="he_no_dias" class="he_no_dias'+ tr +'" style="width:106px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid3" width="110">';
			trr += '<input type="text" name="he_no_horas" value="'+no_hrs+'" id="he_no_horas" class="he_no_horas'+ tr +'" style="width:106px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid3" width="110">';
			trr += '<input type="text" name="he_importe" value="'+importe+'" id="he_importe" class="he_importe'+ tr +'" style="width:106px; text-align:right;">';
		trr += '</td>';
		trr += '<td width="240"></td>';
		trr += '</tr>';
		$grid_horas_extras.append(trr);
		
		elemento_seleccionado = id_tipo_hr;
		texto_elemento_cero = '[--Seleccionar--]';
		campo_indice = 'id';
		campo_valor = 'titulo';
		$carga_campos_select($grid_horas_extras.find('select.select_tipo_he'+ tr), arrayTiposHrsExtra, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
		$grid_horas_extras.find('input.selected_he'+ tr).val(id_tipo_hr);
		
		$permitir_solo_numeros($grid_horas_extras.find('input.he_no_dias'+ tr));
		$permitir_solo_numeros($grid_horas_extras.find('input.he_no_horas'+ tr));
		$permitir_solo_numeros($grid_horas_extras.find('input.he_importe'+ tr));
		
		$aplicar_evento_focus_input_numeric($grid_horas_extras.find('input.he_no_dias'+ tr));
		$aplicar_evento_focus_input_numeric($grid_horas_extras.find('input.he_no_horas'+ tr));
		$aplicar_evento_focus_input_numeric($grid_horas_extras.find('input.he_importe'+ tr));
		
		$aplicar_evento_blur_input_numeric($grid_horas_extras.find('input.he_no_dias'+ tr), false);
		$aplicar_evento_blur_input_numeric($grid_horas_extras.find('input.he_no_horas'+ tr), false);
		$aplicar_evento_blur_input_numeric($grid_horas_extras.find('input.he_importe'+ tr), false);
		
		$grid_horas_extras.find('a.delete_he'+ tr).click(function(event){
			event.preventDefault();
			$fila=$(this).parent().parent();
			//Eliminar el tr
			$fila.remove();
		});
		
		//Cambiar el Tipo de Horas Extras
		$grid_horas_extras.find('select.select_tipo_he'+ tr).change(function(){
			var valor = $(this).val();
			var noTrhe = $(this).parent().parent().find('input[name=noTrhe]').val();
			var encontrado=0;
			
			if(parseInt(valor)>0){
				$grid_horas_extras.find('tr').each(function (index){
					if( parseInt($(this).find('select[name=select_tipo_he]').val()) == parseInt(valor) ){
						if( parseInt($(this).find('input[name=noTrhe]').val()) != parseInt(noTrhe) ){
							encontrado++;
						}
					}
				});
				
				if(parseInt(encontrado)>0){
					jAlert('Ya existe un registro con el tipo: '+$grid_horas_extras.find('select.select_tipo_he'+ tr).find('option:selected').text(), 'Atencion!', function(r) { 
						$grid_horas_extras.find('select.select_tipo_he'+ tr).focus();
					});
					
					var html_select='';
					$grid_horas_extras.find('select.select_tipo_he'+ tr).find('option').each(function(){
						if(parseInt($(this).val())==parseInt($grid_horas_extras.find('input.selected_he'+ tr).val())){
							html_select += '<option value="'+ $(this).val() +'" selected="yes">'+ $(this).text() +'</option>';
						}else{
							html_select += '<option value="'+ $(this).val() +'">'+ $(this).text() +'</option>';
						}
					});
					$grid_horas_extras.find('select.select_tipo_he'+ tr).children().remove();
					$grid_horas_extras.find('select.select_tipo_he'+ tr).append(html_select);
					$grid_horas_extras.find('select.select_tipo_he'+ tr).focus();
				}else{
					$grid_horas_extras.find('input.selected_he'+ tr).val(valor);
				}
			}
		});
	}
	//Termina crear Tr Horas Extras
	
	
	//Cear tr para Incapacidades
	$agregar_tr_incapacidad = function($grid_incapacidades, id_reg, id_tipo, arrayTiposIncapacidad, no_dias, importe){
		//Obtiene numero de trs
		var tr = $("tr", $grid_incapacidades).size();
		tr++;
		
		var trr = '';
		trr = '<tr>';
		trr += '<td class="grid3" width="60">';
			trr += '<a href="#" class="delete_incapacidad'+ tr +'">Eliminar</a>';
			trr += '<input type="hidden" name="id_incapacidad" id="id_incapacidad" value="'+ id_reg +'">';
			trr += '<input type="hidden" name="noTrIncapacidad" value="'+ tr +'">';
		trr += '</td>';
		trr += '<td class="grid3" width="200">';
			trr += '<select name="select_tipo_incapacidad" class="select_tipo_incapacidad'+ tr +'" style="width:196px;"></select>';
			trr += '<input type="hidden" name="selected_incapacidad" class="selected_incapacidad'+ tr +'" value="0">';
		trr += '</td>';
		trr += '<td class="grid3" width="140">';
			trr += '<input type="text" name="incapacidad_no_dias" value="'+no_dias+'" id="incapacidad_no_dias" class="incapacidad_no_dias'+ tr +'" style="width:136px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid3" width="150">';
			trr += '<input type="text" name="incapacidad_importe" value="'+importe+'" id="incapacidad_importe" class="incapacidad_importe'+ tr +'" style="width:146px; text-align:right;">';
		trr += '</td>';
		trr += '<td width="240"></td>';
		trr += '</tr>';
		$grid_incapacidades.append(trr);
		
		elemento_seleccionado = id_tipo;
		texto_elemento_cero = '[--Seleccionar--]';
		campo_indice = 'id';
		campo_valor = 'titulo';
		$carga_campos_select($grid_incapacidades.find('select.select_tipo_incapacidad'+ tr), arrayTiposIncapacidad, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
		$grid_incapacidades.find('input.selected_incapacidad'+ tr).val(id_tipo);
		
		$permitir_solo_numeros($grid_incapacidades.find('input.incapacidad_no_dias'+ tr));
		$permitir_solo_numeros($grid_incapacidades.find('input.incapacidad_importe'+ tr));
		
		$aplicar_evento_focus_input_numeric($grid_incapacidades.find('input.incapacidad_no_dias'+ tr));
		$aplicar_evento_focus_input_numeric($grid_incapacidades.find('input.incapacidad_importe'+ tr));
		
		$aplicar_evento_blur_input_numeric($grid_incapacidades.find('input.incapacidad_no_dias'+ tr), false);
		$aplicar_evento_blur_input_numeric($grid_incapacidades.find('input.incapacidad_importe'+ tr), false);
		
		$grid_incapacidades.find('a.delete_incapacidad'+ tr).click(function(event){
			event.preventDefault();
			$fila=$(this).parent().parent();
			//Eliminar el tr
			$fila.remove();
		});
		
		//Cambiar el Tipo de Incapacidad
		$grid_incapacidades.find('select.select_tipo_incapacidad'+ tr).change(function(){
			var valor = $(this).val();
			var noTrIncapacidad = $(this).parent().parent().find('input[name=noTrIncapacidad]').val();
			var encontrado=0;
			
			if(parseInt(valor)>0){
				$grid_incapacidades.find('tr').each(function (index){
					if( parseInt($(this).find('select[name=select_tipo_incapacidad]').val()) == parseInt(valor) ){
						if( parseInt($(this).find('input[name=noTrIncapacidad]').val()) != parseInt(noTrIncapacidad) ){
							encontrado++;
						}
					}
				});
				
				if(parseInt(encontrado)>0){
					jAlert('Ya existe un registro con el tipo: '+$grid_incapacidades.find('select.select_tipo_incapacidad'+ tr).find('option:selected').text(), 'Atencion!', function(r) { 
						$grid_incapacidades.find('select.select_tipo_incapacidad'+ tr).focus();
					});
					
					var html_select='';
					$grid_incapacidades.find('select.select_tipo_incapacidad'+ tr).find('option').each(function(){
						if(parseInt($(this).val())==parseInt($grid_incapacidades.find('input.selected_incapacidad'+ tr).val())){
							html_select += '<option value="'+ $(this).val() +'" selected="yes">'+ $(this).text() +'</option>';
						}else{
							html_select += '<option value="'+ $(this).val() +'">'+ $(this).text() +'</option>';
						}
					});
					$grid_incapacidades.find('select.select_tipo_incapacidad'+ tr).children().remove();
					$grid_incapacidades.find('select.select_tipo_incapacidad'+ tr).append(html_select);
					$grid_incapacidades.find('select.select_tipo_incapacidad'+ tr).focus();
				}else{
					$grid_incapacidades.find('input.selected_incapacidad'+ tr).val(valor);
				}
			}
		});
	}
	//Termina crear TR para Incapacidades
	
	
	
	//Ventana para la Nomina de cada Empleado
	$forma_nomina_empleado = function(id_empleado, id_periodicidad_pago, $id_reg, $total_percep, $total_deduc, $neto_pagar, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc, arrayTiposHrsExtra, arrayTiposIncapacidad, facturado){
		$('#forma-nominaempleado-window').remove();
		$('#forma-nominaempleado-overlay').remove();
		$(this).modalPanel_nominaempleado();
		var $dialogoc =  $('#forma-nominaempleado-window');
		$dialogoc.append($('div.nominaempleado').find('table.formaNominaEmpleado').clone());
		
		//$('#forma-nominaempleado-window').css({ "margin-left": -320, 	"margin-top": -235  });
		$('#forma-nominaempleado-window').css({ "margin-left": -320, 	"margin-top": -255  });
		$tabs_li_funxionalidad_nominaempleado();
		
		var $id_nom_det = $('#forma-nominaempleado-window').find('input[name=id_nom_det]');
		var $id_empleado = $('#forma-nominaempleado-window').find('input[name=id_empleado]');
		var $no_empleado = $('#forma-nominaempleado-window').find('input[name=no_empleado]');
		var $rfc_empleado = $('#forma-nominaempleado-window').find('input[name=rfc_empleado]');
		var $nombre_empleado = $('#forma-nominaempleado-window').find('input[name=nombre_empleado]');
		var $fecha_contrato = $('#forma-nominaempleado-window').find('input[name=fecha_contrato]');
		var $antiguedad = $('#forma-nominaempleado-window').find('input[name=antiguedad]');
		var $curp = $('#forma-nominaempleado-window').find('input[name=curp]');
		var $clabe = $('#forma-nominaempleado-window').find('input[name=clabe]');
		var $imss = $('#forma-nominaempleado-window').find('input[name=imss]');
		var $reg_patronal = $('#forma-nominaempleado-window').find('input[name=reg_patronal]');
		var $salario_base = $('#forma-nominaempleado-window').find('input[name=salario_base]');
		var $fecha_ini_pago = $('#forma-nominaempleado-window').find('input[name=fecha_ini_pago]');
		var $fecha_fin_pago = $('#forma-nominaempleado-window').find('input[name=fecha_fin_pago]');
		var $salario_integrado = $('#forma-nominaempleado-window').find('input[name=salario_integrado]');
		var $no_dias_pago = $('#forma-nominaempleado-window').find('input[name=no_dias_pago]');
		
		var $concepto_descripcion = $('#forma-nominaempleado-window').find('input[name=concepto_descripcion]');
		var $concepto_unidad = $('#forma-nominaempleado-window').find('input[name=concepto_unidad]');
		var $concepto_cantidad = $('#forma-nominaempleado-window').find('input[name=concepto_cantidad]');
		var $concepto_valor_unitario = $('#forma-nominaempleado-window').find('input[name=concepto_valor_unitario]');
		var $concepto_importe = $('#forma-nominaempleado-window').find('input[name=concepto_importe]');
		
		var $descuento = $('#forma-nominaempleado-window').find('input[name=descuento]');
		var $motivo_descuento = $('#forma-nominaempleado-window').find('input[name=motivo_descuento]');
		
		var $select_impuesto_retencion = $('#forma-nominaempleado-window').find('select[name=select_impuesto_retencion]');
		var $importe_retencion = $('#forma-nominaempleado-window').find('input[name=importe_retencion]');
		
		var $comp_subtotal = $('#forma-nominaempleado-window').find('input[name=comp_subtotal]');
		var $comp_descuento = $('#forma-nominaempleado-window').find('input[name=comp_descuento]');
		var $comp_retencion = $('#forma-nominaempleado-window').find('input[name=comp_retencion]');
		var $comp_total = $('#forma-nominaempleado-window').find('input[name=comp_total]');
		
		var $select_lista_percepciones = $('#forma-nominaempleado-window').find('select[name=select_lista_percepciones]');
		var $agregar_percepcion = $('#forma-nominaempleado-window').find('#agregar_percepcion');
		var $grid_percepciones = $('#forma-nominaempleado-window').find('#grid_percepciones');
		var $percep_total_gravado = $('#forma-nominaempleado-window').find('input[name=percep_total_gravado]');
		var $percep_total_excento = $('#forma-nominaempleado-window').find('input[name=percep_total_excento]');
		
		var $select_lista_deducciones = $('#forma-nominaempleado-window').find('select[name=select_lista_deducciones]');
		var $agregar_deduccion = $('#forma-nominaempleado-window').find('#agregar_deduccion');
		var $grid_deducciones = $('#forma-nominaempleado-window').find('#grid_deducciones');
		var $deduc_total_gravado = $('#forma-nominaempleado-window').find('input[name=deduc_total_gravado]');
		var $deduc_total_excento = $('#forma-nominaempleado-window').find('input[name=deduc_total_excento]');
		
		var $agregar_hora_extra = $('#forma-nominaempleado-window').find('#agregar_hora_extra');
		var $grid_horas_extras = $('#forma-nominaempleado-window').find('#grid_horas_extras');
		
		var $agregar_incapacidad = $('#forma-nominaempleado-window').find('#agregar_incapacidad');
		var $grid_incapacidades = $('#forma-nominaempleado-window').find('#grid_incapacidades');
		
		var $select_departamento = $('#forma-nominaempleado-window').find('select[name=select_departamento]');
		var $select_puesto = $('#forma-nominaempleado-window').find('select[name=select_puesto]');
		var $select_reg_contratacion = $('#forma-nominaempleado-window').find('select[name=select_reg_contratacion]');
		var $select_tipo_contrato = $('#forma-nominaempleado-window').find('select[name=select_tipo_contrato]');
		var $select_tipo_jornada = $('#forma-nominaempleado-window').find('select[name=select_tipo_jornada]');
		var $select_preriodo_pago = $('#forma-nominaempleado-window').find('select[name=select_preriodo_pago]');
		var $select_banco = $('#forma-nominaempleado-window').find('select[name=select_banco]');
		var $select_riesgo_puesto = $('#forma-nominaempleado-window').find('select[name=select_riesgo_puesto]');
		
		var $cierra_forma_nominaempleado = $('#forma-nominaempleado-window').find('#cierra_forma_nominaempleado');
		var $boton_cancelar_forma_nominaempleado = $('#forma-nominaempleado-window').find('#boton_cancelar_forma_nominaempleado');
		var $boton_actualizar_forma_nominaempleado = $('#forma-nominaempleado-window').find('#boton_actualizar_forma_nominaempleado');
		
		
		//VARIABLES QUE SE TOMAN DE LA VENTANA PRINCIPAL
		//Id del Periodo para obtener la fecha inicial y fecha final
		var id_periodo = $('#forma-facnomina-window').find('input[name=no_periodo_selec]').val();
		var $accion = $('#forma-facnomina-window').find('input[name=accion]');
		var $identificador = $('#forma-facnomina-window').find('input[name=identificador]');
		//Id del empleado seleccionado desde donde se está  haciendo clic
		var $id_generar = $('#forma-facnomina-window').find('input[name=id_generar]');
		
		$permitir_solo_numeros($antiguedad);
		$permitir_solo_numeros($no_dias_pago);
		$permitir_solo_numeros($salario_base);
		$permitir_solo_numeros($salario_integrado);
		$permitir_solo_numeros($concepto_cantidad);
		$permitir_solo_numeros($concepto_valor_unitario);
		$permitir_solo_numeros($concepto_importe);
		$permitir_solo_numeros($descuento);
		$permitir_solo_numeros($importe_retencion);
		$permitir_solo_numeros($comp_subtotal);
		$permitir_solo_numeros($comp_descuento);
		$permitir_solo_numeros($comp_retencion);
		$permitir_solo_numeros($comp_total);
		

		
		$aplicar_readonly_input($comp_subtotal);
		$aplicar_readonly_input($comp_descuento);
		$aplicar_readonly_input($comp_retencion);
		$aplicar_readonly_input($comp_total);
		$aplicar_readonly_input($percep_total_gravado);
		$aplicar_readonly_input($percep_total_excento);
		$aplicar_readonly_input($deduc_total_gravado);
		$aplicar_readonly_input($deduc_total_excento);
		
		$aplicar_readonly_input($id_empleado);
		$aplicar_readonly_input($no_empleado);
		$aplicar_readonly_input($rfc_empleado);
		$aplicar_readonly_input($curp);
		$aplicar_readonly_input($nombre_empleado);
		$aplicar_readonly_input($fecha_contrato);
		$aplicar_readonly_input($fecha_ini_pago);
		$aplicar_readonly_input($fecha_fin_pago);
		
		

		
		
		$id_nom_det.val($id_reg.val());
		
		var elemento_seleccionado = 0;
		var texto_elemento_cero = '';
		var campo_indice = '';
		var campo_valor = '';
		var elemento_fijo=false;
		
		if(facturado=='true'){
			//Ocultar boton Actualizar cuando ya fue facturado
			$boton_actualizar_forma_nominaempleado.hide();
		}else{
			$add_calendar($fecha_ini_pago, " ", "");
			$add_calendar($fecha_fin_pago, " ", "");
			$aplicar_evento_focus_input_text( $concepto_descripcion );
			$aplicar_evento_focus_input_text( $concepto_unidad );
			
			$aplicar_evento_blur_input_text( $concepto_descripcion );
			$aplicar_evento_blur_input_text( $concepto_unidad );
			
			$aplicar_evento_focus_input_numeric($salario_base);
			$aplicar_evento_focus_input_numeric($salario_integrado);
			$aplicar_evento_focus_input_numeric($concepto_cantidad);
			$aplicar_evento_focus_input_numeric($concepto_valor_unitario);
			$aplicar_evento_focus_input_numeric($concepto_importe);
			$aplicar_evento_focus_input_numeric($descuento);
			$aplicar_evento_focus_input_numeric($importe_retencion);
			
			$aplicar_evento_blur_input_numeric($salario_base, false);
			$aplicar_evento_blur_input_numeric($salario_integrado, false);
			$aplicar_evento_blur_input_numeric($concepto_cantidad, true);
			$aplicar_evento_blur_input_numeric($concepto_valor_unitario, false);
			$aplicar_evento_blur_input_numeric($concepto_importe, false);
			$aplicar_evento_blur_input_numeric($descuento, true);
			$aplicar_evento_blur_input_numeric($importe_retencion, false);
		}
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataNominaEmpleado.json';
		$arreglo = {'identificador':$identificador.val(), 'accion':$accion.val(), 'id_reg':$id_nom_det.val(), 'id_empleado':id_empleado, 'id_periodo':id_periodo, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			if(entry['Data'][0]['validado']=='false'){
				//NUEVO
				
				$id_empleado.val(entry['Data'][0]['empleado_id']);
				$no_empleado.val(entry['Data'][0]['clave']);
				$rfc_empleado.val(entry['Data'][0]['rfc']);
				$nombre_empleado.val(entry['Data'][0]['empleado']);
				$fecha_contrato.val(entry['Data'][0]['fecha_ingreso']);
				//$antiguedad.val(entry['Data'][0]['']);
				$curp.val(entry['Data'][0]['curp']);
				$clabe.val(entry['Data'][0]['clabe']);
				$imss.val(entry['Data'][0]['imss']);
				$reg_patronal.val(entry['Data'][0]['reg_patronal']);
				
				$salario_base.val(entry['Data'][0]['salario_base']);
				$salario_integrado.val(entry['Data'][0]['salario_int']);
				//$no_dias_pago.val(entry['Data'][0]['']);
				
				if(parseInt(id_periodo)>0){
					$fecha_ini_pago.val(entry['Periodo'][0]['fecha_ini']);
					$fecha_fin_pago.val(entry['Periodo'][0]['fecha_fin']);
					$concepto_descripcion.val(entry['Periodo'][0]['periodo']);
				}
				
				$concepto_unidad.val(arrayPar[0]['concepto_unidad']);
				$concepto_cantidad.val(parseFloat(1).toFixed(2));
				$concepto_valor_unitario.val(parseFloat(0).toFixed(2));
				$concepto_importe.val(parseFloat(0).toFixed(2));
				
				$descuento.val(parseFloat(0).toFixed(2));
				$motivo_descuento.val(arrayPar[0]['motivo_descuento']);
				
				$importe_retencion.val(parseFloat(0).toFixed(2));
				
				$comp_subtotal.val(parseFloat(0).toFixed(2));
				$comp_descuento.val(parseFloat(0).toFixed(2));
				$comp_retencion.val(parseFloat(0).toFixed(2));
				$comp_total.val(parseFloat(0).toFixed(2));
				
				$percep_total_gravado.val(parseFloat(0).toFixed(2));
				$percep_total_excento.val(parseFloat(0).toFixed(2));
				
				$deduc_total_gravado.val(parseFloat(0).toFixed(2));
				$deduc_total_excento.val(parseFloat(0).toFixed(2));
				
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar Departamento--]';
				}
				elemento_seleccionado = entry['Data'][0]['depto_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_departamento, arrayDeptos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar Puesto--]';
				}
				elemento_seleccionado = entry['Data'][0]['puesto_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_puesto, arrayPuestos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar Regimen--]';
				}
				elemento_seleccionado = entry['Data'][0]['regimen_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_reg_contratacion, arrayRegimenContrato, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_seleccionado = entry['Data'][0]['tipo_contrato_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_tipo_contrato, arrayTipoContrato, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_seleccionado = entry['Data'][0]['tipo_jornada_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_tipo_jornada, arrayTipoJornada, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				
				if(parseInt($id_reg.val())==0){
					elemento_seleccionado = id_periodicidad_pago;
				}else{
					elemento_seleccionado = entry['Data'][0]['periodo_pago_id'];
				}
				if(facturado=='true'){
					texto_elemento_cero = '';
				}else{
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_fijo=true;
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_preriodo_pago, arrayPeriodicidad, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_seleccionado = entry['Data'][0]['banco_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_banco, arrayBancos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_seleccionado = entry['Data'][0]['riesgo_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_riesgo_puesto, arrayRiesgos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_seleccionado = arrayPar[0]['isr_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_impuesto_retencion, arrayImpuestoRet, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);			
				
				if(facturado=='true'){
					elemento_fijo=true;
				}else{
					elemento_fijo=false;
				}
				elemento_seleccionado = 0;
				texto_elemento_cero = '[--Seleccionar--]';
				campo_indice = 'id';
				campo_valor = 'percepcion';
				$carga_campos_select($select_lista_percepciones, arrayPercep, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				
				if(facturado=='true'){
					elemento_fijo=true;
				}else{
					elemento_fijo=false;
				}
				elemento_seleccionado = 0;
				texto_elemento_cero = '[--Seleccionar--]';
				campo_indice = 'id';
				campo_valor = 'deduccion';
				$carga_campos_select($select_lista_deducciones, arrayDeduc, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
				
			}else{
				//EDIT
				$id_nom_det.val(entry['Data'][0]['id']);
				$id_empleado.val(entry['Data'][0]['empleado_id']);
				$no_empleado.val(entry['Data'][0]['no_empleado']);
				$rfc_empleado.val(entry['Data'][0]['rfc']);
				$nombre_empleado.val(entry['Data'][0]['nombre']);
				$fecha_contrato.val(entry['Data'][0]['fecha_contrato']);
				$antiguedad.val(entry['Data'][0]['antiguedad']);
				$curp.val(entry['Data'][0]['curp']);
				$clabe.val(entry['Data'][0]['clabe']);
				$imss.val(entry['Data'][0]['imss']);
				$reg_patronal.val(entry['Data'][0]['reg_patronal']);
				$salario_base.val(entry['Data'][0]['salario_base']);
				$salario_integrado.val(entry['Data'][0]['salario_integrado']);
				$no_dias_pago.val(entry['Data'][0]['no_dias_pago']);
				
				$fecha_ini_pago.val(entry['Data'][0]['f_ini_pago']);
				$fecha_fin_pago.val(entry['Data'][0]['f_fin_pago']);
				$concepto_descripcion.val(entry['Data'][0]['concepto_descripcion']);
                
				$concepto_unidad.val(entry['Data'][0]['concepto_unidad']);
				$concepto_cantidad.val(entry['Data'][0]['concepto_cantidad']);
				$concepto_valor_unitario.val(entry['Data'][0]['concepto_valor_unitario']);
				$concepto_importe.val(entry['Data'][0]['concepto_importe']);
				
				$descuento.val(entry['Data'][0]['descuento']);
				$motivo_descuento.val(entry['Data'][0]['motivo_descuento']);
				
				$importe_retencion.val(entry['Data'][0]['importe_retencion']);
				
				$comp_subtotal.val(entry['Data'][0]['comp_subtotal']);
				$comp_descuento.val(entry['Data'][0]['comp_descuento']);
				$comp_retencion.val(entry['Data'][0]['comp_retencion']);
				$comp_total.val(entry['Data'][0]['comp_total']);
				
				$percep_total_gravado.val(entry['Data'][0]['percep_total_gravado']);
				$percep_total_excento.val(entry['Data'][0]['percep_total_excento']);
				
				$deduc_total_gravado.val(entry['Data'][0]['deduc_total_gravado']);
				$deduc_total_excento.val(entry['Data'][0]['deduc_total_excento']);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar Departamento--]';
				}
				elemento_seleccionado = entry['Data'][0]['depto_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_departamento, arrayDeptos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar Puesto--]';
				}
				elemento_seleccionado = entry['Data'][0]['puesto_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_puesto, arrayPuestos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar Regimen--]';
				}
				elemento_seleccionado = entry['Data'][0]['regimen_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_reg_contratacion, arrayRegimenContrato, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
					texto_elemento_cero = '';
				}else{
					elemento_fijo=false;
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_seleccionado = entry['Data'][0]['tipo_contrato_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_tipo_contrato, arrayTipoContrato, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					texto_elemento_cero = '';
				}else{
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_fijo=true;
				elemento_seleccionado = entry['Data'][0]['tipo_jornada_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_tipo_jornada, arrayTipoJornada, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					texto_elemento_cero = '';
				}else{
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_fijo=true;
				elemento_seleccionado = entry['Data'][0]['periodicidad_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_preriodo_pago, arrayPeriodicidad, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					texto_elemento_cero = '';
					elemento_fijo=true;
				}else{
					texto_elemento_cero = '[--Seleccionar--]';
					elemento_fijo=false;
				}
				elemento_seleccionado = entry['Data'][0]['banco_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_banco, arrayBancos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					texto_elemento_cero = '';
					elemento_fijo=true;
				}else{
					texto_elemento_cero = '[--Seleccionar--]';
					elemento_fijo=false;
				}
				elemento_seleccionado = entry['Data'][0]['riesgo_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_riesgo_puesto, arrayRiesgos, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					texto_elemento_cero = '';
				}else{
					texto_elemento_cero = '[--Seleccionar--]';
				}
				elemento_fijo=true;
				elemento_seleccionado = entry['Data'][0]['isr_id'];
				campo_indice = 'id';
				campo_valor = 'titulo';
				$carga_campos_select($select_impuesto_retencion, arrayImpuestoRet, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);			
				
				if(facturado=='true'){
					elemento_fijo=true;
				}else{
					elemento_fijo=false;
				}
				elemento_seleccionado = 0;
				texto_elemento_cero = '[--Seleccionar--]';
				campo_indice = 'id';
				campo_valor = 'percepcion';
				$carga_campos_select($select_lista_percepciones, arrayPercep, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				if(facturado=='true'){
					elemento_fijo=true;
				}else{
					elemento_fijo=false;
				}
				elemento_seleccionado = 0;
				texto_elemento_cero = '[--Seleccionar--]';
				campo_indice = 'id';
				campo_valor = 'deduccion';
				$carga_campos_select($select_lista_deducciones, arrayDeduc, elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
				
				
				
				$.each(entry['HrsExtraEmpleado'],function(entryIndex,data){
					var id_tipo_hr = data['tipo_he_id'];
					var id_reg = data['id'];
					var no_dias = data['no_dias'];
					var no_hrs = data['no_hrs'];
					var importe = data['importe'];
					$agregar_tr_hora_extra($grid_horas_extras, id_reg, id_tipo_hr, arrayTiposHrsExtra, no_dias, no_hrs, importe);
				});
				
				$.each(entry['IncapaEmpleado'],function(entryIndex,data){
					var id_tipo = data['tipo_incapa_id'];
					var id_reg = data['id'];
					var no_dias = data['no_dias'];
					var importe = data['importe'];
					$agregar_tr_incapacidad($grid_incapacidades, id_reg, id_tipo, arrayTiposIncapacidad, no_dias, importe)
				});
				
				/*
                row.put("fac_nom_id",rs.getInt("fac_nom_id"));
                */
				//TERMINA EDIT
			}
			
			$.each(entry['PercepEmpleado'],function(entryIndex,data){
				var id_percep = data['id'];
				var tipo_agrupador = data['tipo_percep'];
				var percepcion = data['percepcion'];
				var monto_gravado = data['m_gravado'];
				var monto_excento = data['m_excento'];
				$agregar_tr_percepcion($grid_percepciones, id_percep, tipo_agrupador, percepcion, monto_gravado, monto_excento);
			});
			
			$.each(entry['DeducEmpleado'],function(entryIndex,data){
				var id_deduc = data['id'];
				var tipo_deduc_id = data['tipo_deduc_id'];
				var tipo_agrupador = data['tipo_deduc'];
				var deduccion = data['deduccion'];
				var monto_gravado = data['m_gravado'];
				var monto_excento = data['m_excento'];
				$agregar_tr_deduccion($grid_deducciones, id_deduc, tipo_deduc_id, tipo_agrupador, deduccion, monto_gravado, monto_excento);
			});
			
			
				
			if(facturado=='true'){
				$aplicar_readonly_input($antiguedad);
				$aplicar_readonly_input($clabe);
				$aplicar_readonly_input($imss);
				$aplicar_readonly_input($reg_patronal);
				$aplicar_readonly_input($salario_base);
				$aplicar_readonly_input($salario_integrado);
				$aplicar_readonly_input($fecha_ini_pago);
				$aplicar_readonly_input($fecha_fin_pago);
				$aplicar_readonly_input($no_dias_pago);
				$aplicar_readonly_input($concepto_descripcion);
				$aplicar_readonly_input($concepto_unidad);
				$aplicar_readonly_input($concepto_cantidad);
				$aplicar_readonly_input($concepto_valor_unitario);
				$aplicar_readonly_input($concepto_importe);
				$aplicar_readonly_input($descuento);
				$aplicar_readonly_input($motivo_descuento);
				$aplicar_readonly_input($importe_retencion);
				
				$agregar_percepcion.hide();
				$agregar_deduccion.hide();
				$agregar_hora_extra.hide();
				$agregar_incapacidad.hide();
				$aplicar_readonly_input($grid_percepciones.find('tr').find('input'));
				$grid_percepciones.find('tr').find('a').hide();
				$aplicar_readonly_input($grid_deducciones.find('tr').find('input'));
				$grid_deducciones.find('tr').find('a').hide();
				$aplicar_readonly_input($grid_horas_extras.find('tr').find('input'));
				$grid_horas_extras.find('tr').find('a').hide();
				$aplicar_readonly_input($grid_incapacidades.find('tr').find('input'));
				$grid_incapacidades.find('tr').find('a').hide();
			}
			
			
			//Agregar un Nuevo Concepto de Percepcion
			$agregar_percepcion.click(function(event){
				var encontrado=0;
				if(parseInt($select_lista_percepciones.val())>0){
					//Busca el id del concepto de decuccion que se quiere agregar
					$grid_percepciones.find('tr').each(function (index){
						if( parseInt($(this).find('input[name=id_percep]').val()) == parseInt($select_lista_percepciones.val()) ){
							encontrado++;
						}
					});
					if(parseInt(encontrado)<=0){
						$.each(arrayPercep,function(entryIndex,data){
							if(parseInt($select_lista_percepciones.val())==parseInt(data['id'])){
								var id_percep = data['id'];
								var tipo_agrupador = data['tipo_percep'];
								var percepcion = data['percepcion'];
								var monto_gravado = data['m_gravado'];
								var monto_excento = data['m_excento'];
								$agregar_tr_percepcion($grid_percepciones, id_percep, tipo_agrupador, percepcion, monto_gravado, monto_excento);
							}
						});
					}else{
						jAlert('El Concepto de Percepci&oacute;n ya se encuentra en la lista, seleccione una diferente.', 'Atencion!', function(r) { 
							$select_lista_percepciones.focus();
						});
					}
				}else{
					jAlert('Seleccione un Concepto de Percepci&oacute;n para agregar.', 'Atencion!', function(r) { 
						$select_lista_percepciones.focus();
					});
				}
			});
			
			
			
			//Agregar un Nuevo Concepto de Deduccion
			$agregar_deduccion.click(function(event){
				var encontrado=0;
				if(parseInt($select_lista_deducciones.val())>0){
					//Busca el id del concepto de decuccion que se quiere agregar
					$grid_deducciones.find('tr').each(function (index){
						if( parseInt($(this).find('input[name=id_deduc]').val()) == parseInt($select_lista_deducciones.val()) ){
							encontrado++;
						}
					});
					
					if(parseInt(encontrado)<=0){
						$.each(arrayDeduc,function(entryIndex,data){
							if(parseInt($select_lista_deducciones.val())==parseInt(data['id'])){
								var id_deduc = data['id'];
								var tipo_deduc_id = data['tipo_deduc_id'];
								var tipo_agrupador = data['tipo_deduc'];
								var deduccion = data['deduccion'];
								var monto_gravado = data['m_gravado'];
								var monto_excento = data['m_excento'];
								$agregar_tr_deduccion($grid_deducciones, id_deduc, tipo_deduc_id, tipo_agrupador, deduccion, monto_gravado, monto_excento);
							}
						});
					}else{
						jAlert('El Concepto de Deducci&oacute;n ya se encuentra en la lista, seleccione una diferente.', 'Atencion!', function(r) { 
							$select_lista_deducciones.focus();
						});
					}
				}else{
					jAlert('Seleccione un Concepto de Deducci&oacute;n para agregar.', 'Atencion!', function(r) { 
						$select_lista_deducciones.focus();
					});
				}
			});
			
			
			
			//Agregar un Nuevo Concepto para Horas Extras
			$agregar_hora_extra.click(function(event){
				var id_tipo_hr=0;
				var id_reg=0;
				var no_dias="0.00";
				var no_hrs="0.00";
				var importe="0.00";
				$agregar_tr_hora_extra($grid_horas_extras, id_reg, id_tipo_hr, arrayTiposHrsExtra, no_dias, no_hrs, importe);
			});
			 
			
			//Agregar un Nuevo Concepto para Horas Extras
			$agregar_incapacidad.click(function(event){
				var id_tipo=0;
				var id_reg=0;
				var no_dias="0.00";
				var importe="0.00";
				$agregar_tr_incapacidad($grid_incapacidades, id_reg, id_tipo, arrayTiposIncapacidad, no_dias, importe)
			});
			 
		},"json");
		
		
		
		$boton_actualizar_forma_nominaempleado.bind('click',function(){
			var confirma = '';
			
			//Redefinir accion click del boton actualizar de la ventana principal
			$('#forma-facnomina-window').find( "#submit" ).bind('click',function(){
				//Aqui se establece el nivel de ejecucion 2, porque se ejecita desde la ventana de nomina de Empleado
				$('#forma-facnomina-window').find('input[name=nivel_ejecucion]').val(2);
				if(parseInt($('#forma-facnomina-window').find('#grid_empleados').size()) > 0){
					return true;
				}else{
					alert('No hay datos para actualizar');
					return false;
				}
			});
			
			
			//Aqui se establece el nivel de ejecucion 2, porque se ejecita desde la ventana de nomina de Empleado
			$('#forma-facnomina-window').find('input[name=nivel_ejecucion]').val(2);
			
			
			//Ejecutar submit del Formulario proncipal
			$('#forma-facnomina-window').find( "#submit" ).trigger( "click" );
							
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/edit_nomina_empleado.json';
				$arreglo = {
					'identificador':$('#forma-facnomina-window').find('input[name=identificador]').val(),
					'id_reg':$id_nom_det.val(),
					'id_empleado':$id_empleado.val(),
					'no_empleado':$no_empleado.val(),
					'rfc_empleado':$rfc_empleado.val(),
					'nombre_empleado':$nombre_empleado.val(),
					'select_departamento':$select_departamento.val(),
					'select_puesto':$select_puesto.val(),
					'fecha_contrato':$fecha_contrato.val(),
					'antiguedad':$antiguedad.val(),
					'curp':$curp.val(),
					'select_reg_contratacion':$select_reg_contratacion.val(),
					'select_tipo_contrato':$select_tipo_contrato.val(),
					'select_tipo_jornada':$select_tipo_jornada.val(),
					'select_preriodo_pago':$select_preriodo_pago.val(),
					'clabe':$clabe.val(),
					'select_banco':$select_banco.val(),
					'select_riesgo_puesto':$select_riesgo_puesto.val(),
					'imss':$imss.val(),
					'reg_patronal':$reg_patronal.val(),
					'salario_base':$salario_base.val(),
					'fecha_ini_pago':$fecha_ini_pago.val(),
					'fecha_fin_pago':$fecha_fin_pago.val(),
					'salario_integrado':$salario_integrado.val(),
					'no_dias_pago':$no_dias_pago.val(),
					'concepto_descripcion':$concepto_descripcion.val(),
					'concepto_unidad':$concepto_unidad.val(),
					'concepto_cantidad':$concepto_cantidad.val(),
					'concepto_valor_unitario':$concepto_valor_unitario.val(),
					'concepto_importe':$concepto_importe.val(),
					'descuento':$descuento.val(),
					'motivo_descuento':$motivo_descuento.val(),
					'select_impuesto_retencion':$select_impuesto_retencion.val(),
					'importe_retencion':$importe_retencion.val(),
					'comp_subtotal':$comp_subtotal.val(),
					'comp_descuento':$comp_descuento.val(),
					'comp_retencion':$comp_retencion.val(),
					'comp_total':$comp_total.val(),
					'percep_total_gravado':$percep_total_gravado.val(),
					'percep_total_excento':$percep_total_excento.val(),
					'deduc_total_gravado':$deduc_total_gravado.val(),
					'deduc_total_excento':$deduc_total_excento.val(),
					'percepciones':$crear_cadena($grid_percepciones, 'percepciones'),
					'deducciones':$crear_cadena($grid_deducciones, 'deducciones'),
					'hrs_extras':$crear_cadena($grid_horas_extras, 'hrs_extras'),
					'incapacidades':$crear_cadena($grid_incapacidades, 'incapacidades'),
					'accion':$accion.val(),
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				
				$.post(input_json,$arreglo,function(entry){
					confirma = entry['success'];
					
					if ( confirma == "true" ){
						$id_reg.val(entry['id']);
						var mensaje='';
						if(parseInt(entry['valor'])>0){
							//Aquí se toma el codigo del error
							mensaje = ': '+entry['valor'];
						}
						mensaje = mensaje + '' + entry['msj'];
						
						jAlert(mensaje, 'Atencion!');
						
						if(parseInt(entry['actualizo'])==1){
							//Solo se debe cerrar la ventana cuando actualizo sea igual a 1
							var remove = function() { $(this).remove(); };
							$('#forma-nominaempleado-overlay').fadeOut(remove);
						}
						
						//Aqui se redefine el clic al boton submit
						$('#forma-facnomina-window').find( "#submit" ).bind('click',function(){
							//Aqui se establece el nivel de ejecucion 1, para regresar el control a la ventana principal
							$('#forma-facnomina-window').find('input[name=nivel_ejecucion]').val(1);
							if(parseInt($("tr", $('#forma-facnomina-window').find('#grid_empleados')).size()) > 0){
								return true;
							}else{
								alert('No hay datos para actualizar');
								return false;
							}
						});
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-nominaempleado-window').find('div.interrogacion').css({'display':'none'});
						var valor = confirma.split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = confirma.split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-nominaempleado-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
								
								//Para input name
								if(tmp.split(':')[0].split('__')[0]=='grid'){
									var campo = tmp.split(':')[0].split('__')[1];
									//alert(campo);
									var $campo_input = $('#forma-nominaempleado-window').find('input[name='+campo+']');
									$campo_input.css({'background' : '#d41000'});
									$campo_input.attr({'title': tmp.split(':')[1] }); 
								}
								
								//Para input class
								if(tmp.split(':')[0].split('__')[0]=='grid2'){
									var campo = tmp.split(':')[0].split('__')[1];
									//alert(campo);
									var $campo_input = $('#forma-nominaempleado-window').find('.'+campo+']');
									$campo_input.css({'background' : '#d41000'});
									$campo_input.attr({'title': tmp.split(':')[1] }); 
								}
		
							}
						}
					}
				},"json");
		});
		
		
		$boton_cancelar_forma_nominaempleado.click(function(event){
			event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-nominaempleado-overlay').fadeOut(remove);
		});
		
		$cierra_forma_nominaempleado.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-nominaempleado-overlay').fadeOut(remove);
		});
	}
	//Termina forma para nominaempleado
	
	
	
	//Crear cadena
	$crear_cadena = function($grid, tipo){
		var cadena="";
		if(tipo=='percepciones'){
			$grid.find('tr').each(function (index){
				if(cadena!=""){
					cadena = cadena + "&&&";
				}
				cadena = cadena + $(this).find('input[name=id_percep]').val()+"|"+$(this).find('input[name=noTrPercep]').val()+"|"+$(this).find('input[name=percep_monto_gravado]').val()+"|"+$(this).find('input[name=percep_monto_excento]').val();
			});
		}
		
		if(tipo=='deducciones'){
			$grid.find('tr').each(function (index){
				if(cadena!=""){
					cadena = cadena + "&&&";
				}
				cadena = cadena + $(this).find('input[name=id_deduc]').val()+"|"+$(this).find('input[name=noTrDeduc]').val()+"|"+$(this).find('input[name=deduc_monto_gravado]').val()+"|"+$(this).find('input[name=deduc_monto_excento]').val();
			});
		}
		
		if(tipo=='hrs_extras'){
			$grid.find('tr').each(function (index){
				if(cadena!=""){
					cadena = cadena + "&&&";
				}
				cadena = cadena + $(this).find('input[name=id_he]').val()+"|"+$(this).find('input[name=noTrhe]').val()+"|"+$(this).find('select[name=select_tipo_he]').val()+"|"+$(this).find('input[name=he_no_dias]').val()+"|"+$(this).find('input[name=he_no_horas]').val()+"|"+$(this).find('input[name=he_importe]').val();
			});
		}
		
		if(tipo=='incapacidades'){
			$grid.find('tr').each(function (index){
				if(cadena!=""){
					cadena = cadena + "&&&";
				}
				cadena = cadena + $(this).find('input[name=id_incapacidad]').val()+"|"+$(this).find('input[name=noTrIncapacidad]').val()+"|"+$(this).find('select[name=select_tipo_incapacidad]').val()+"|"+$(this).find('input[name=incapacidad_no_dias]').val()+"|"+$(this).find('input[name=incapacidad_importe]').val();
			});
		}
		
		return cadena;
	}
	
	
	
	
	
	
	
	
	
	
	
	$agrega_empleado_grid = function($grid_empleados, id_periodicidad_pago, id_reg, id_empleado, nombre_empleado, t_percep, t_deduc, pago_neto, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc, arrayTiposHrsExtra, arrayTiposIncapacidad, facturado, serie_folio, cancelado){
		//Obtiene numero de trs
		var tr = $("tr", $grid_empleados).size();
		tr++;
		
		var trr = '';
		trr = '<tr class="gral">';
		trr += '<td class="grid3" width="40">';
			trr += '<a href="#" id="delete_empleado'+ tr +'">Eliminar</a>';
			trr += '<input type="hidden" name="id_reg" id="id_reg" class="id_reg'+ tr +'" value="'+ id_reg +'">';
			trr += '<input type="hidden" name="id_emp" id="id_emp" value="'+ id_empleado +'">';
			trr += '<input type="hidden" name="elim" class="elim'+ tr +'" value="1">';
			trr += '<input type="hidden" name="noTr" value="'+ tr +'">';
			trr += '<input type="hidden" name="no_nom" class="no_nom'+ tr +'" value="'+ serie_folio +'">';
		trr += '</td>';
		trr += '<td class="grid3" width="50">';
			if(facturado=='true'){
				trr += '<a href="#" id="editar'+ tr +'">'+ serie_folio +'</a>';
			}else{
				trr += '<a href="#" id="editar'+ tr +'">Editar</a>';
			}
		trr += '</td>';
		trr += '<td class="grid4" width="300">'+ nombre_empleado +'</td>';
		trr += '<td class="grid3" width="95">';
			trr += '<input type="text" name="tpercep" value="'+ t_percep +'" id="tpercep" class="tpercep'+ tr +' borde_oculto" readOnly="true" style="width:91px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid3" width="95">';
			trr += '<input type="text" name="tdeduc" value="'+ t_deduc +'" id="tdeduc" class="tdeduc'+ tr +' borde_oculto" readOnly="true" style="width:91px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid3" width="85">';
			trr += '<input type="text" name="pago_neto" value="'+ pago_neto +'" id="pago_neto" class="pago_neto'+ tr +' borde_oculto" readOnly="true" style="width:81px; text-align:right;">';
		trr += '</td>';
		trr += '<td class="grid3" width="40">';
			if(facturado=='true'){
				trr += '<input type="button" id="xml" class="xml'+ tr +'" value="XML" style="width:35px; font-weight:bold;">';
			}
		trr += '</td>';
		trr += '<td class="grid3" width="40">';
			if(facturado=='true'){
				trr += '<input type="button" id="pdf" class="pdf'+ tr +'" value="PDF" style="width:35px; font-weight:bold;">';
			}
		trr += '</td>';
		trr += '<td class="grid3" width="60">';
			if(facturado=='true'){
				trr += '<input type="button" id="cancel" class="cancel'+ tr +'" value="Cancelar" style="width:55px; font-weight:bold;">';
			}
		trr += '</td>';
		trr += '</tr>';
		$grid_empleados.append(trr);
		
		
		if(facturado=='true'){
			$grid_empleados.find('#delete_empleado'+ tr).hide();
			
			$grid_empleados.find('.xml'+ tr).click(function(){
				var id_reg = $grid_empleados.find('.id_reg'+ tr).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getVerificaArchivo.json';
				$arreglo = {'id_reg':id_reg,'ext':'xml', 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
				$.post(input_json,$arreglo,function(entry){
					var descargar  = entry['descargar'];
					if(descargar == 'true'){
						var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
						var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getXml/'+id_reg+'/'+iu+'/out.json';
						window.location.href=input_json;
					}else{
						jAlert("El xml de la nomina "+$grid_empleados.find('.no_nom'+ tr).val()+" no esta disponible para descarga.", 'Atencion!');
					}
				});//termina llamada json
			});
			
			
			$grid_empleados.find('.pdf'+ tr).click(function(){
				var id_reg = $grid_empleados.find('.id_reg'+ tr).val();
				var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
				var id_empleado = $(this).parent().parent().find('#id_emp').val();
				var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPDF/'+id_reg+'/'+id_empleado+'/'+iu+'/out.json';
				window.location.href=input_json;
			});
			
			
			$grid_empleados.find('.cancel'+ tr).click(function(){
				var id_reg = $grid_empleados.find('.id_reg'+ tr).val();
				var id_empleado = $(this).parent().parent().find('#id_emp').val();
				var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
				
				//Confirmar Cancelacion
				jConfirm('Confirmar cancelaci&oacute;n?', 'Dialogo de Confirmacion', function(r) {
					// If they confirmed, manually trigger a form submission
					if (r) {
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCancelaNomina.json';
						$arreglo = {'id_reg':id_reg,'id_emp':id_empleado, 'iu':iu }
						$.post(input_json,$arreglo,function(entry){
							if(entry['valor'].trim() == 'true'){
								$grid_empleados.find('.cancel'+ tr).attr('disabled','-1');
								$grid_empleados.find('.xml'+ tr).attr('disabled','-1');
							}
							jAlert(entry['msj'], 'Atencion!');
						});//termina llamada json
					}
				});
			});
		}
		
		
		
		if(cancelado=='true'){
			$grid_empleados.find('.cancel'+ tr).attr('disabled','-1');
			$grid_empleados.find('.xml'+ tr).attr('disabled','-1');
		}
		
		
		
		//Editar un empleado
		$grid_empleados.find('#editar'+ tr).click(function(){
			$fila=$(this).parent().parent();
			//alert($fila.find('#id_emp').val());
			
			if(parseInt($('#forma-facnomina-window').find('select[name=select_no_periodo]').val())>0){
				var idEmpleado = $fila.find('#id_emp').val();
				var $id_reg = $fila.find('#id_reg');
				var $total_percep = $fila.find('#tpercep');
				var $total_deduc = $fila.find('#tdeduc');
				var $neto_pagar = $fila.find('#pago_neto');
				
				//Llamada a la función que crea la ventana para la nomina del empleado
				$forma_nomina_empleado(idEmpleado, id_periodicidad_pago, $id_reg, $total_percep, $total_deduc, $neto_pagar, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc, arrayTiposHrsExtra, arrayTiposIncapacidad, facturado);
			}else{
				jAlert('Es necesario seleccionar un periodo para la Nomina.', 'Atencion!', function(r) { 
					$('#forma-facnomina-window').find('select[name=select_no_periodo]').focus();
				});
			}
		});
		
		//Eliminar un empleado del Grid
		$grid_empleados.find('#delete_empleado'+ tr).click(function(){
			$fila=$(this).parent().parent();
			valor_id_reg = $grid_empleados.find('input.id_reg'+ tr).val();
			if(parseInt(valor_id_reg)>0){
				$fila.find('input').val('');
				$grid_empleados.find('input.id_reg'+ tr).val(valor_id_reg);
				$grid_empleados.find('input.elim'+ tr).val(0);
				$fila.hide();
			}else{
				$fila.remove();
			}
		});
	}
	
	
	$get_empleados = function(id_periodicidad_pago, $grid_empleados, $select_comp_periodicidad, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc, arrayTiposHrsExtra, arrayTiposIncapacidad){
		$grid_empleados.children().remove();
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEmpleados.json';
		$arreglo = {'id':id_periodicidad_pago, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
		$.post(input_json,$arreglo,function(entry){
			if (entry['Empleados'].length > 0){
				$.each(entry['Empleados'],function(entryIndex,data){
					var id_reg = data['id_reg'];
					var id_empleado = data['id'];
					var nombre_empleado = data['nombre'];
					var t_percep="0.00";
					var t_deduc="0.00";
					var pago_neto="0.00"
					var facturado = 'false';
					var serie_folio = '';
					var cancelado='false';
					$agrega_empleado_grid($grid_empleados, id_periodicidad_pago, id_reg, id_empleado, nombre_empleado, t_percep, t_deduc, pago_neto, arrayPar, arrayDeptos, arrayPuestos, arrayRegimenContrato, arrayTipoContrato, arrayTipoJornada, arrayPeriodicidad, arrayBancos, arrayRiesgos, arrayImpuestoRet, arrayPercep, arrayDeduc, arrayTiposHrsExtra, arrayTiposIncapacidad, facturado, serie_folio, cancelado);
				});
			}else{
				jAlert('No hay empleados que se le pague con la Periodicidad seleccionada.', 'Atencion!', function(r) { 
					$select_comp_periodicidad.focus();
				});
			}
		},"json");
	}
	
	
	
	$get_periodos_por_tipo_periodicidad = function(id_periodicidad_pago, id_periodo_selec, $select_no_periodo, status){
		$select_no_periodo.children().remove();
		var identificador=0;
		if(parseInt(status)>0){
			identificador = $('#forma-facnomina-window').find('input[name=identificador]').val();
		}
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPeriodosPorTipoPeridicidad.json';
		$arreglo = {'id':id_periodicidad_pago, 'identificador':identificador, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
		$.post(input_json,$arreglo,function(entry){
			var elemento_fijo=false;
			var elemento_seleccionado = id_periodo_selec;
			var texto_elemento_cero = '[-Seleccionar-]';
			var campo_indice = 'id';
			var campo_valor = 'periodo';
			if(parseInt(status)>0){
				texto_elemento_cero='';
				elemento_fijo=true;
			}
			$carga_campos_select($select_no_periodo, entry['Periodos'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
			
			if (entry['Periodos'].length <= 0){
				jAlert('No hay periodos configurados para la Periodicidad seleccionada.', 'Atencion!', function(r) { 
					$select_no_periodo.focus();
				});
			}
		},"json");
	}
	
	
	
	
	
	
	//Nuevo 
	$nuevo.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_facnomina();
		
		var form_to_show = 'formafacnomina00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-facnomina-window').css({"margin-left": -340, 	"margin-top": -220});
		
		$forma_selected.prependTo('#forma-facnomina-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getNomina.json';
		$arreglo = {'identificador':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
        
		var $identificador = $('#forma-facnomina-window').find('input[name=identificador]');
		var $accion = $('#forma-facnomina-window').find('input[name=accion]');
		var $nivel_ejecucion = $('#forma-facnomina-window').find('input[name=nivel_ejecucion]');
		var $generar = $('#forma-facnomina-window').find('input[name=generar]');
		var $id_generar = $('#forma-facnomina-window').find('input[name=id_generar]');
		
		var $emisor_rfc = $('#forma-facnomina-window').find('input[name=emisor_rfc]');
		var $emisor_nombre = $('#forma-facnomina-window').find('input[name=emisor_nombre]');
		var $emisor_regimen = $('#forma-facnomina-window').find('input[name=emisor_regimen]');
		var $emisor_dir = $('#forma-facnomina-window').find('input[name=emisor_dir]');
		var $comp_tipo = $('#forma-facnomina-window').find('input[name=comp_tipo]');
		var $comp_forma_pago = $('#forma-facnomina-window').find('input[name=comp_forma_pago]');
		var $comp_tc = $('#forma-facnomina-window').find('input[name=comp_tc]');
		var $comp_no_cuenta = $('#forma-facnomina-window').find('input[name=comp_no_cuenta]');
		var $fecha_pago = $('#forma-facnomina-window').find('input[name=fecha_pago]');
		
		var $select_comp_metodo_pago = $('#forma-facnomina-window').find('select[name=select_comp_metodo_pago]');
		var $select_comp_moneda = $('#forma-facnomina-window').find('select[name=select_comp_moneda]');
		var $select_comp_periodicidad = $('#forma-facnomina-window').find('select[name=select_comp_periodicidad]');
		var $periodicidad_selec = $('#forma-facnomina-window').find('input[name=periodicidad_selec]');
		var $select_no_periodo = $('#forma-facnomina-window').find('select[name=select_no_periodo]');
		var $no_periodo_selec = $('#forma-facnomina-window').find('input[name=no_periodo_selec]');
		
		var $genera_nomina = $('#forma-facnomina-window').find('#genera_nomina');
		
		
		//Grid de productos
		var $grid_empleados = $('#forma-facnomina-window').find('#grid_empleados');
		
		
		//Grid de errores
		var $grid_warning = $('#forma-facnomina-window').find('#div_warning_grid').find('#grid_warning');
		
		
		var $cerrar_plugin = $('#forma-facnomina-window').find('#close');
		var $cancelar_plugin = $('#forma-facnomina-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-facnomina-window').find('#submit');
		
		//Para nuevo el identificador siempre es 0
		$identificador.val(id_to_show);
		$accion.val("new");
		$nivel_ejecucion.val(1);
		$generar.val("false");
		$id_generar.val(0);
		
		$permitir_solo_numeros($comp_tc);
		
		$aplicar_readonly_input($emisor_rfc);
		$aplicar_readonly_input($emisor_nombre);
		$aplicar_readonly_input($emisor_regimen);
		$aplicar_readonly_input($emisor_dir);
		$aplicar_readonly_input($comp_tipo);
		$aplicar_readonly_input($comp_forma_pago);
		$aplicar_readonly_input($fecha_pago);
		
		$genera_nomina.hide();
		
		//Quitar enter a todos los campos input
		$('#forma-facnomina-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				$accion.val("edit");
				if(parseInt($nivel_ejecucion.val())==1){
					//Solo se debe mostrar el alert cuando se actualiza desde la ventana principal
					jAlert("Los datos se guardaron con &eacute;xito", 'Atencion!');
					var remove = function() {$(this).remove();};
					$('#forma-facnomina-overlay').fadeOut(remove);
					$get_datos_grid();
				}
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-facnomina-window').find('.facnomina_div_one').css({'height':'580px'});//con errores
				
				$('#forma-facnomina-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-facnomina-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
					}
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			$identificador.val(entry['Extra'][0]['identificador']);
			$emisor_rfc.val(entry['Extra'][0]['emp_rfc']);
			$emisor_nombre.val(entry['Extra'][0]['emp_razon_social']);
			$emisor_regimen.val(entry['Extra'][0]['emp_regimen_fiscal']);
			$emisor_dir.val(entry['Extra'][0]['emp_direccion']);
			
			$comp_tipo.val(entry['Par'][0]['tipo_comprobante']);
			$comp_forma_pago.val(entry['Par'][0]['forma_pago']);
			$comp_no_cuenta.val(entry['Par'][0]['no_cuenta_pago']);
			$comp_tc.val(entry['Par'][0]['tc']);
			
			
			var elemento_seleccionado=0;
			var texto_elemento_cero = '[--Seleccionar--]';
			var campo_indice = 'id';
			var campo_valor = 'titulo';
			$carga_campos_select($select_comp_metodo_pago, entry['MetodosPago'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
			
			
			elemento_seleccionado=entry['Par'][0]['mon_id'];
			texto_elemento_cero = '';
			campo_indice = 'id';
			campo_valor = 'descripcion';
			$carga_campos_select($select_comp_moneda, entry['Monedas'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, true);
			
			
			elemento_seleccionado=0;
			texto_elemento_cero = '[-Seleccionar-]';
			campo_indice = 'id';
			campo_valor = 'titulo';
			$carga_campos_select($select_comp_periodicidad, entry['Periodicidad'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
			
			//entry['Par'], entry['Deptos'], entry['Puestos'], entry['RegimenContrato'], entry['TipoContrato'], entry['TipoJornada'], entry['Periodicidad'], entry['Bancos'], entry['Riesgos'], entry['ImpuestoRet'], entry['Percepciones'], entry['Deducciones'], entry['TiposHrsExtra'], entry['TiposIncapacidad']
			
			
			
			//Cambiar la periodicidad
			$select_comp_periodicidad.change(function(){
				var valor = $(this).val();
				var id_periodicidad_pago=0;
				if(parseInt(valor)>0){
					if(parseInt($periodicidad_selec.val())>0){
						if(parseInt($("tr", $grid_empleados).size())>0){
							jConfirm('Hay empleados en la Lista. Al cambiar la Periodicidad de Pago, se sustituir&aacute; el listado actual por una nueva lista.\nEst&aacute; seguro que desea cambiar los empleados del listado actual?', 'Dialogo de Confirmacion', function(r) {
								if (r){
									id_periodicidad_pago=0;
									$get_empleados(valor, $grid_empleados, $select_comp_periodicidad, entry['Par'], entry['Deptos'], entry['Puestos'], entry['RegimenContrato'], entry['TipoContrato'], entry['TipoJornada'], entry['Periodicidad'], entry['Bancos'], entry['Riesgos'], entry['ImpuestoRet'], entry['Percepciones'], entry['Deducciones'], entry['TiposHrsExtra'], entry['TiposIncapacidad']);
									$get_periodos_por_tipo_periodicidad(valor, id_periodicidad_pago, $select_no_periodo, 0);
									$periodicidad_selec.val(valor);
									$no_periodo_selec.val(id_periodicidad_pago);
								}else{
									//Volvemos a cargar el select para dejar seleccionado la Periodicidad anterior
									elemento_seleccionado=$periodicidad_selec.val();
									texto_elemento_cero = '[-Seleccionar-]';
									campo_indice = 'id';
									campo_valor = 'titulo';
									$carga_campos_select($select_comp_periodicidad, entry['Periodicidad'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
									$get_periodos_por_tipo_periodicidad($periodicidad_selec.val(), $no_periodo_selec.val(), $select_no_periodo, 0);
								}
							});
						}else{
							id_periodicidad_pago=0;
							$get_empleados(valor, $grid_empleados, $select_comp_periodicidad, entry['Par'], entry['Deptos'], entry['Puestos'], entry['RegimenContrato'], entry['TipoContrato'], entry['TipoJornada'], entry['Periodicidad'], entry['Bancos'], entry['Riesgos'], entry['ImpuestoRet'], entry['Percepciones'], entry['Deducciones'], entry['TiposHrsExtra'], entry['TiposIncapacidad']);
							$get_periodos_por_tipo_periodicidad(valor, id_periodicidad_pago, $select_no_periodo, 0);
							$periodicidad_selec.val(valor);
							$no_periodo_selec.val(id_periodicidad_pago);
						}
					}else{
						id_periodicidad_pago=0;
						$get_empleados(valor, $grid_empleados, $select_comp_periodicidad, entry['Par'], entry['Deptos'], entry['Puestos'], entry['RegimenContrato'], entry['TipoContrato'], entry['TipoJornada'], entry['Periodicidad'], entry['Bancos'], entry['Riesgos'], entry['ImpuestoRet'], entry['Percepciones'], entry['Deducciones'], entry['TiposHrsExtra'], entry['TiposIncapacidad']);
						$get_periodos_por_tipo_periodicidad(valor, id_periodicidad_pago, $select_no_periodo,0);
						$periodicidad_selec.val(valor);
						$no_periodo_selec.val(id_periodicidad_pago);
					}
				}else{
					jAlert('Opcion no valido.', 'Atencion!', function(r) {
						//Volvemos a cargar el select para dejar seleccionado la Periodicidad anterior
						elemento_seleccionado = $periodicidad_selec.val();
						texto_elemento_cero = '[-Seleccionar-]';
						campo_indice = 'id';
						campo_valor = 'titulo';
						$carga_campos_select($select_comp_periodicidad, entry['Periodicidad'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, false);
						$get_periodos_por_tipo_periodicidad($periodicidad_selec.val(), $no_periodo_selec.val(), $select_no_periodo, 0);
						$select_comp_periodicidad.focus();
					});
				}
			});
			
			
			//Cambiar el periodo
			$select_no_periodo.change(function(){
				var valor = $(this).val();
				$no_periodo_selec.val(valor);
			});
			
		},"json");//termina llamada json
		
		$fecha_pago.val(mostrarFecha());
		$add_calendar($fecha_pago, " ", "");
		
		
		
		
		
		
		$submit_actualizar.bind('click',function(){
			$nivel_ejecucion.val(1);
			if(parseInt($("tr", $grid_empleados).size()) > 0){
				return true;
			}else{
				jAlert('No hay datos para actualizar', 'Atencion!', function(r) { $select_comp_periodicidad.focus(); });
				return false;
			}
		});
		
		$genera_nomina.bind('click',function(){
			$generar.val("true");
			$id_generar.val(0);
			$nivel_ejecucion.val(1);
			
			//Redefinir click del boton actualizar
			$submit_actualizar.bind('click',function(){
				$nivel_ejecucion.val(1);
				if(parseInt($("tr", $grid_empleados).size()) > 0){
					return true;
				}else{
					jAlert('No hay datos para actualizar', 'Atencion!', function(r) { $select_comp_periodicidad.focus(); });
					return false;
				}
			});
			
			//Ejecutar submit del Formulario proncipal
			$submit_actualizar.trigger( "click" );
		});
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-facnomina-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-facnomina-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	var carga_formafacnomina00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  el pedido?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El registro fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El registro no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-facnomina-window').remove();
			$('#forma-facnomina-overlay').remove();
            
			var form_to_show = 'formafacnomina00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_facnomina();
			
			$('#forma-facnomina-window').css({"margin-left": -340, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-facnomina-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $identificador = $('#forma-facnomina-window').find('input[name=identificador]');
			var $accion = $('#forma-facnomina-window').find('input[name=accion]');
			var $nivel_ejecucion = $('#forma-facnomina-window').find('input[name=nivel_ejecucion]');
			var $generar = $('#forma-facnomina-window').find('input[name=generar]');
			var $id_generar = $('#forma-facnomina-window').find('input[name=id_generar]');
			
			var $emisor_rfc = $('#forma-facnomina-window').find('input[name=emisor_rfc]');
			var $emisor_nombre = $('#forma-facnomina-window').find('input[name=emisor_nombre]');
			var $emisor_regimen = $('#forma-facnomina-window').find('input[name=emisor_regimen]');
			var $emisor_dir = $('#forma-facnomina-window').find('input[name=emisor_dir]');
			var $comp_tipo = $('#forma-facnomina-window').find('input[name=comp_tipo]');
			var $comp_forma_pago = $('#forma-facnomina-window').find('input[name=comp_forma_pago]');
			var $comp_tc = $('#forma-facnomina-window').find('input[name=comp_tc]');
			var $comp_no_cuenta = $('#forma-facnomina-window').find('input[name=comp_no_cuenta]');
			var $fecha_pago = $('#forma-facnomina-window').find('input[name=fecha_pago]');
			
			var $select_comp_metodo_pago = $('#forma-facnomina-window').find('select[name=select_comp_metodo_pago]');
			var $select_comp_moneda = $('#forma-facnomina-window').find('select[name=select_comp_moneda]');
			var $select_comp_periodicidad = $('#forma-facnomina-window').find('select[name=select_comp_periodicidad]');
			var $periodicidad_selec = $('#forma-facnomina-window').find('input[name=periodicidad_selec]');
			var $select_no_periodo = $('#forma-facnomina-window').find('select[name=select_no_periodo]');
			var $no_periodo_selec = $('#forma-facnomina-window').find('input[name=no_periodo_selec]');
			
			
			var $genera_nomina = $('#forma-facnomina-window').find('#genera_nomina');
			
			
			//Grid de productos
			var $grid_empleados = $('#forma-facnomina-window').find('#grid_empleados');
			
			
			//Grid de errores
			var $grid_warning = $('#forma-facnomina-window').find('#div_warning_grid').find('#grid_warning');
			
			
			var $cerrar_plugin = $('#forma-facnomina-window').find('#close');
			var $cancelar_plugin = $('#forma-facnomina-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-facnomina-window').find('#submit');
			
			//Para nuevo el identificador siempre es 0
			$identificador.val(id_to_show);
			$accion.val("edit");
			$generar.val("false");
			$id_generar.val(0);
			$permitir_solo_numeros($comp_tc);
			
			$aplicar_readonly_input($emisor_rfc);
			$aplicar_readonly_input($emisor_nombre);
			$aplicar_readonly_input($emisor_regimen);
			$aplicar_readonly_input($emisor_dir);
			$aplicar_readonly_input($comp_tipo);
			$aplicar_readonly_input($comp_forma_pago);
			$aplicar_readonly_input($fecha_pago);
			
			$genera_nomina.hide();
			
			//quitar enter a todos los campos input
			$('#forma-facnomina-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			if(accion_mode == 'edit'){
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getNomina.json';
				$arreglo = {'identificador':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					//alert('Nivel: '+$nivel_ejecucion.val());
					if(parseInt($nivel_ejecucion.val())==1){
						//Solo se debe mostrar el alert cuando se actualiza desde la ventana principal
						
						if ( data['success'] == 'true' ){
							$('#forma-facnomina-window').find('div.interrogacion').css({'display':'none'});
							
							jAlert(data['msj'], 'Atencion!');
							
							var remove = function() {$(this).remove();};
							$('#forma-facnomina-overlay').fadeOut(remove);
							
							//Ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
							$submit_actualizar.hide();
							$get_datos_grid();
						}else{
							// Desaparece todas las interrogaciones si es que existen
							//$('#forma-facnomina-window').find('.div_one').css({'height':'545px'});//sin errores
							
							var valor = data['success'].split('___');
							//muestra las interrogaciones
							for (var element in valor){
								tmp = data['success'].split('___')[element];
								longitud = tmp.split(':');
								
								if( longitud.length > 1 ){
									$('#forma-facnomina-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
									.parent()
									.css({'display':'block'})
									.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
									
									//alert(tmp.split(':')[0]);
								}
							}
						}
					}
				}
				
				var options = {dataType:'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.val(entry['Datos'][0]['id']);
					$emisor_rfc.val(entry['Datos'][0]['emisor_rfc']);
					$emisor_nombre.val(entry['Datos'][0]['emisor_nombre']);
					$emisor_regimen.val(entry['Datos'][0]['emisor_reg_fis']);
					$emisor_dir.val(entry['Datos'][0]['emisor_dir']);
					$comp_tipo.val(entry['Datos'][0]['tipo_comprobante']);
					$comp_forma_pago.val(entry['Datos'][0]['forma_pago']);
					$comp_no_cuenta.val(entry['Datos'][0]['no_cuenta']);
					$comp_tc.val(entry['Datos'][0]['tipo_cambio']);
					$fecha_pago.val(entry['Datos'][0]['fecha_pago']);
					
					var texto_elemento_cero = '';
					var elemento_fijo=false;
					if(parseInt(entry['Datos'][0]['status'])>0){
						elemento_fijo=true;
					}else{
						elemento_fijo=false;
						texto_elemento_cero = '[--Seleccionar--]';
					}
					var elemento_seleccionado=entry['Datos'][0]['metodo_pago_id'];
					var campo_indice = 'id';
					var campo_valor = 'titulo';
					$carga_campos_select($select_comp_metodo_pago, entry['MetodosPago'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
					
					
					elemento_fijo=true;
					elemento_seleccionado=entry['Datos'][0]['mon_id'];
					texto_elemento_cero = '';
					campo_indice = 'id';
					campo_valor = 'descripcion';
					$carga_campos_select($select_comp_moneda, entry['Monedas'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
					
					if(parseInt(entry['Datos'][0]['status'])>0){
						texto_elemento_cero = '';
					}else{
						texto_elemento_cero = '[--Seleccionar--]';
					}
					elemento_fijo=true;
					elemento_seleccionado=entry['Datos'][0]['periodicidad_pago_id'];
					campo_indice = 'id';
					campo_valor = 'titulo';
					$carga_campos_select($select_comp_periodicidad, entry['Periodicidad'], elemento_seleccionado, texto_elemento_cero, campo_indice, campo_valor, elemento_fijo);
					
					
					$get_periodos_por_tipo_periodicidad(entry['Datos'][0]['periodicidad_pago_id'], entry['Datos'][0]['no_periodo_id'], $select_no_periodo, entry['Datos'][0]['status']);
					$no_periodo_selec.val(entry['Datos'][0]['no_periodo_id']);
					
					
					//Cambiar el periodo
					$select_no_periodo.change(function(){
						var valor = $(this).val();
						$no_periodo_selec.val(valor);
					});
					
					$.each(entry['datosGrid'],function(entryIndex,data){
						var id_reg = data['id_reg'];
						var id_empleado = data['empleado_id'];
						var nombre_empleado = data['nombre'];
						var t_percep = data['total_percep'];
						var t_deduc = data['total_deduc'];
						var pago_neto = data['total_pago'];
						var facturado = data['facturado'];
						var serie_folio = data['no_nom'];
						var cancelado = data['cancelado'];
						//alert(data['facturado']);
						$agrega_empleado_grid($grid_empleados, entry['Datos'][0]['periodicidad_pago_id'], id_reg, id_empleado, nombre_empleado, t_percep, t_deduc, pago_neto, entry['Par'], entry['Deptos'], entry['Puestos'], entry['RegimenContrato'], entry['TipoContrato'], entry['TipoJornada'], entry['Periodicidad'], entry['Bancos'], entry['Riesgos'], entry['ImpuestoRet'], entry['Percepciones'], entry['Deducciones'], entry['TiposHrsExtra'], entry['TiposIncapacidad'], facturado, serie_folio, cancelado);
					});
					
					
					if(parseInt(entry['Datos'][0]['status'])>=0){
						if(parseInt(entry['Datos'][0]['status'])==0 || parseInt(entry['Datos'][0]['status'])==1){
							$genera_nomina.show();
						}
						if(parseInt(entry['Datos'][0]['status'])==2){
							$genera_nomina.hide();
						}
					}else{
						$add_calendar($fecha_pago, " ", "");
					}
					
				});//termina llamada json
                
                





				
				
				$submit_actualizar.bind('click',function(){
					$nivel_ejecucion.val(1);
					var trCount = $("tr", $grid_empleados).size();
					if(parseInt(trCount) > 0){
						return true;
					}else{
						jAlert('No hay datos para actualizar', 'Atencion!', function(r) { $select_comp_periodicidad.focus(); });
						return false;
					}
				});
				
				
				$genera_nomina.bind('click',function(){
					$nivel_ejecucion.val(1);
					$generar.val("true");
					$id_generar.val(0);
					
					//Redefinir click del boton actualizar
					$submit_actualizar.bind('click',function(){
						$nivel_ejecucion.val(1);
						if(parseInt($("tr", $grid_empleados).size()) > 0){
							return true;
						}else{
							jAlert('No hay datos para actualizar', 'Atencion!', function(r) { $select_comp_periodicidad.focus(); });
							return false;
						}
					});
					
					//alert($generar.val());
					//Ejecutar submit del Formulario proncipal
					$submit_actualizar.trigger( "click" );
				});
				
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-facnomina-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-facnomina-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllNominas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllNominas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formafacnomina00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});
