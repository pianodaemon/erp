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
	var controller = $contextpath.val()+"/controllers/faccancelacionv2";
    
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
	$('#barra_titulo').find('#td_titulo').append('Cancelaci&oacute;n de Facturas(No cancela CFDI ante el SAT)');
	
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
	
	
	
	var TriggerClickVisializaBuscador=0;
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
		$('#forma-faccancelacionv2-window').find('#submit').mouseover(function(){
			$('#forma-faccancelacionv2-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-faccancelacionv2-window').find('#submit').mouseout(function(){
			$('#forma-faccancelacionv2-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-faccancelacionv2-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-faccancelacionv2-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-faccancelacionv2-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-faccancelacionv2-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-faccancelacionv2-window').find('#close').mouseover(function(){
			$('#forma-faccancelacionv2-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-faccancelacionv2-window').find('#close').mouseout(function(){
			$('#forma-faccancelacionv2-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-faccancelacionv2-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-faccancelacionv2-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-faccancelacionv2-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-faccancelacionv2-window').find("ul.pestanas li").click(function() {
			$('#forma-faccancelacionv2-window').find(".contenidoPes").hide();
			$('#forma-faccancelacionv2-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-faccancelacionv2-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	/*
	var carga_formaPagos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para generar pdf del pago
		if(accion_mode == 'cancel'){
			
					var id_to_show = 0;
					$(this).modalPanel_faccancelacionv2();
					var form_to_show = 'formaCancelaEmisionV2';
					$('#' + form_to_show).each (function(){this.reset();});
					var $forma_selected = $('#' + form_to_show).clone();
					$forma_selected.attr({id : form_to_show + id_to_show});
					$('#forma-faccancelacionv2-window').css({"margin-left": -100,"margin-top": -180});
					$forma_selected.prependTo('#forma-faccancelacionv2-window');
					$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
					
					var $select_tipo_cancelacion = $('#forma-faccancelacionv2-window').find('select[name=tipo_cancelacion]');
					var $motivo_cancelacion = $('#forma-faccancelacionv2-window').find('textarea[name=motivo_cancel]');
					
					var $cancelar_factura = $('#forma-faccancelacionv2-window').find('a[href*=cancelfact]');
					var $salir = $('#forma-faccancelacionv2-window').find('a[href*=salir]');
					
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTiposCancelacion.json';
					$arreglo = {}
					
					$.post(input_json,$arreglo,function(entry){
						$select_tipo_cancelacion.children().remove();
						var tipo_hmtl = '';
						$.each(entry['Tipos'],function(entryIndex,tipo){
								tipo_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
						});
						$select_tipo_cancelacion.append(tipo_hmtl);
					});
					
					
					//generar informe mensual
					$cancelar_factura.click(function(event){
						event.preventDefault();
						if($motivo_cancelacion.val()!=null && $motivo_cancelacion.val()!=""){
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/cancelar_factura.json';
							$arreglo = {'id_factura':$id_factura.val(),
										'tipo_cancelacion':$select_tipo_cancelacion.val(),
										'motivo':$motivo_cancelacion.val(),
										'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
										}
							
							$.post(input_json,$arreglo,function(entry){
								var cad = entry['success'].split(":");
								
								if(entry['success'].trim()=='false'){
									jAlert(entry['msj'], 'Atencion!');
								}else{
									if(entry['valor'].trim()=='false'){
										//jAlert("La factura "+$serie_folio.val()+" tiene pagos aplicados. Es necesario cancelar primeramente los pagos y despues cancelar la factura.", 'Atencion!');
										jAlert(entry['msj'], 'Atencion!');
									}else{
										$boton_cancelarfactura.hide();
										//jAlert("La factura "+$serie_folio.val()+"  se ha cancelado con &eacute;xito", 'Atencion!');
										jAlert(entry['msj'], 'Atencion!');
										$get_datos_grid();
										
										var remove = function() {$(this).remove();};
										$('#forma-faccancelacionv2-overlay').fadeOut(remove);
									}
								}
								
							});//termina llamada json
						}else{
							jAlert("Es necesario ingresar el motivo de la cancelaci&oacute;n", 'Atencion!');
						}
					});
					
					
					$salir.click(function(event){
						event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-faccancelacionv2-overlay').fadeOut(remove);
					});

			
		}
	}
    */
    
    
    
    
    
    
    
    
    
	//Eventos del grid edicion,borrar!
	var carga_formaCC00_for_datagrid00Cancel = function(id_to_show, accion_mode){
		//aqui  entra para editar un registro
		var form_to_show = 'formaCancelaEmisionV2';
		
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$(this).modalPanel_faccancelacionv2();
					
		$('#forma-faccancelacionv2-window').css({"margin-left": -420, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-faccancelacionv2-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $id_factura = $('#forma-faccancelacionv2-window').find('input[name=id_factura]');
		var $select_tipo_cancelacion = $('#forma-faccancelacionv2-window').find('select[name=tipo_cancelacion]');
		var $motivo_cancelacion = $('#forma-faccancelacionv2-window').find('textarea[name=motivo_cancel]');
		
		var $cancelar_factura = $('#forma-faccancelacionv2-window').find('#cancelar');
		var $salir = $('#forma-faccancelacionv2-window').find('#salir');
		
		//botones                        
		var $cerrar_plugin = $('#forma-faccancelacionv2-window').find('#close');
		
		
		$id_factura.val(id_to_show);
		
		
		if(accion_mode == 'cancel'){
			
			
			var respuestaProcesada = function(data){
				if ( data['success'] == 'true' ){
					var remove = function() {$(this).remove();};
					$('#forma-faccancelacionv2-overlay').fadeOut(remove);
					jAlert("Los datos se han actualizado.", 'Atencion!');
					$get_datos_grid();
				}else{
					// Desaparece todas las interrogaciones si es que existen
					$('#forma-faccancelacionv2-window').find('div.interrogacion').css({'display':'none'});
					
					var valor = data['success'].split('___');
					//muestra las interrogaciones
					for (var element in valor){
						tmp = data['success'].split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
							$('#forma-faccancelacionv2-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						}
					}
				}
			}
			
			var options = {dataType :  'json', success : respuestaProcesada};
			$forma_selected.ajaxForm(options);
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTiposCancelacion.json';
			$arreglo = {}
			
			$.post(input_json,$arreglo,function(entry){
				$select_tipo_cancelacion.children().remove();
				var tipo_hmtl = '';
				$.each(entry['Tipos'],function(entryIndex,tipo){
						tipo_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
				});
				$select_tipo_cancelacion.append(tipo_hmtl);
			});
			
			
			
			//generar informe mensual
			$cancelar_factura.click(function(event){
				event.preventDefault();
				if($motivo_cancelacion.val()!=null && $motivo_cancelacion.val()!=""){
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/cancelar_factura.json';
					$arreglo = {'id_factura':$id_factura.val(),
								'tipo_cancelacion':$select_tipo_cancelacion.val(),
								'motivo':$motivo_cancelacion.val(),
								'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
								}
					
					$.post(input_json,$arreglo,function(entry){
						if(entry['success'].trim()=='false'){
							jAlert(entry['msj'], 'Atencion!');
						}else{
							if(entry['valor'].trim()=='false'){
								jAlert(entry['msj'], 'Atencion!');
							}else{
								jAlert(entry['msj'], 'Atencion!');
								$get_datos_grid();
								
								var remove = function() {$(this).remove();};
								$('#forma-faccancelacionv2-overlay').fadeOut(remove);
							}
						}
					});//termina llamada json
				}else{
					jAlert("Es necesario ingresar el motivo de la cancelaci&oacute;n", 'Atencion!');
				}
			});
			
			
			$salir.click(function(event){
				event.preventDefault();
				var remove = function() {$(this).remove();};
				$('#forma-faccancelacionv2-overlay').fadeOut(remove);
			});
			
			//cerrar plugin
			$cerrar_plugin.bind('click',function(){
				var remove = function() {$(this).remove();};
				$('#forma-faccancelacionv2-overlay').fadeOut(remove);
			});
		
		
			$motivo_cancelacion.focus();
		}
		
	}

    
    
    
    
    
    
    
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllFacturas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllFacturas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenableCancel(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00Cancel);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



