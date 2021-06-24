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
	var controller = $contextpath.val()+"/controllers/pocpedidosautoriza";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Autorizaci&oacute;n de Pedidos de Clientes');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
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
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
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
	
	
	//esta funcion carga los datos para el buscador del paginado
	$cargar_datos_buscador_principal= function(){
		var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_lineas,$arreglo,function(data){
			//Alimentando los campos select_agente
			$busqueda_select_agente.children().remove();
			var agente_hmtl = '<option value="0">[-Seleccionar Agente-]</option>';
			$.each(data['Agentes'],function(entryIndex,agente){
				agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
			});
			$busqueda_select_agente.append(agente_hmtl);
		});
	}
	
	
	//llamada a funcion
	$cargar_datos_buscador_principal();
	
	$limpiar.click(function(event){
		$busqueda_folio.val('');
		$busqueda_cliente.val('');
		$busqueda_codigo.val('');
		$busqueda_producto.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		//llamada a funcion al limpiar campos
		$cargar_datos_buscador_principal();
		
		$busqueda_folio.focus();
		$get_datos_grid();
		
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
		$busqueda_folio.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
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
            var $select_prod_tipo = $('#forma-pocpedidosautoriza-window').find('select[name=prodtipo]');
            $('#forma-pocpedidosautoriza-window').find('#submit').mouseover(function(){
                $('#forma-pocpedidosautoriza-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
            })
            $('#forma-pocpedidosautoriza-window').find('#submit').mouseout(function(){
                $('#forma-pocpedidosautoriza-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
            })
            $('#forma-pocpedidosautoriza-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-pocpedidosautoriza-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-pocpedidosautoriza-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-pocpedidosautoriza-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-pocpedidosautoriza-window').find('#close').mouseover(function(){
                $('#forma-pocpedidosautoriza-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-pocpedidosautoriza-window').find('#close').mouseout(function(){
                $('#forma-pocpedidosautoriza-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-pocpedidosautoriza-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-pocpedidosautoriza-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-pocpedidosautoriza-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-pocpedidosautoriza-window').find("ul.pestanas li").click(function() {
                $('#forma-pocpedidosautoriza-window').find(".contenidoPes").hide();
                $('#forma-pocpedidosautoriza-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-pocpedidosautoriza-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	//funcion para hacer que un campo solo acepte numeros
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
	
	//carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, elemento_seleccionado){
		$campo_select.children().remove();
		var select_html = '';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				if(parseInt(elemento_seleccionado)==0){
					select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
				}
			}
		}
		$campo_select.append(select_html);
	}
	
	
	
	var carga_formapocpedidosautoriza00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_pedido':id_to_show,
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
			$('#forma-pocpedidosautoriza-window').remove();
			$('#forma-pocpedidosautoriza-overlay').remove();
            
			var form_to_show = 'formapocpedidosautoriza00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_pocpedidosautoriza();
			
			$('#forma-pocpedidosautoriza-window').css({"margin-left": -340, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-pocpedidosautoriza-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $total_tr = $('#forma-pocpedidosautoriza-window').find('input[name=total_tr]');
			var $id_pedido = $('#forma-pocpedidosautoriza-window').find('input[name=id_pedido]');
			var $accion_proceso = $('#forma-pocpedidosautoriza-window').find('input[name=accion_proceso]');
			var $folio = $('#forma-pocpedidosautoriza-window').find('input[name=folio]');
			
			var $busca_cliente = $('#forma-pocpedidosautoriza-window').find('a[href*=busca_cliente]');
			var $id_cliente = $('#forma-pocpedidosautoriza-window').find('input[name=id_cliente]');
			var $nocliente = $('#forma-pocpedidosautoriza-window').find('input[name=nocliente]');
			var $razon_cliente = $('#forma-pocpedidosautoriza-window').find('input[name=razoncliente]');
			var $empresa_immex = $('#forma-pocpedidosautoriza-window').find('input[name=empresa_immex]');
			var $tasa_ret_immex = $('#forma-pocpedidosautoriza-window').find('input[name=tasa_ret_immex]');
			
			var $select_moneda = $('#forma-pocpedidosautoriza-window').find('select[name=select_moneda]');
			var $select_moneda_original = $('#forma-pocpedidosautoriza-window').find('input[name=select_moneda_original]');
			var $tipo_cambio = $('#forma-pocpedidosautoriza-window').find('input[name=tipo_cambio]');
			var $tipo_cambio_original = $('#forma-pocpedidosautoriza-window').find('input[name=tipo_cambio_original]');
			var $orden_compra = $('#forma-pocpedidosautoriza-window').find('input[name=orden_compra]');
			
			var $id_impuesto = $('#forma-pocpedidosautoriza-window').find('input[name=id_impuesto]');
			var $valor_impuesto = $('#forma-pocpedidosautoriza-window').find('input[name=valorimpuesto]');
			
			var $observaciones = $('#forma-pocpedidosautoriza-window').find('textarea[name=observaciones]');
			var $select_condiciones = $('#forma-pocpedidosautoriza-window').find('select[name=select_condiciones]');
			var $select_vendedor = $('#forma-pocpedidosautoriza-window').find('select[name=vendedor]');
			var $transporte = $('#forma-pocpedidosautoriza-window').find('input[name=transporte]');
			var $lugar_entrega = $('#forma-pocpedidosautoriza-window').find('input[name=lugar_entrega]');
			var $fecha_compromiso = $('#forma-pocpedidosautoriza-window').find('input[name=fecha_compromiso]');
			var $select_metodo_pago = $('#forma-pocpedidosautoriza-window').find('select[name=select_metodo_pago]');
			
			//var $select_almacen = $('#forma-pocpedidosautoriza-window').find('select[name=almacen]');
			var $sku_producto = $('#forma-pocpedidosautoriza-window').find('input[name=sku_producto]');
			var $nombre_producto = $('#forma-pocpedidosautoriza-window').find('input[name=nombre_producto]');
			
			//buscar producto
			var $busca_sku = $('#forma-pocpedidosautoriza-window').find('a[href*=busca_sku]');
			//href para agregar producto al grid
			var $agregar_producto = $('#forma-pocpedidosautoriza-window').find('a[href*=agregar_producto]');
			
			var $autorizar = $('#forma-pocpedidosautoriza-window').find('#autorizar');
			var $descargarpdf = $('#forma-pocpedidosautoriza-window').find('#descargarpdf');
			var $cancelar_pedido = $('#forma-pocpedidosautoriza-window').find('#cancelar_pedido');
			var $cancelado = $('#forma-pocpedidosautoriza-window').find('input[name=cancelado]');
			
			//grid de productos
			var $grid_productos = $('#forma-pocpedidosautoriza-window').find('#grid_productos');
			//grid de errores
			var $grid_warning = $('#forma-pocpedidosautoriza-window').find('#div_warning_grid').find('#grid_warning');
			
			//var $flete = $('#forma-pocpedidosautoriza-window').find('input[name=flete]');
			var $importe_subtotal = $('#forma-pocpedidosautoriza-window').find('input[name=importe_subtotal]');
			var $monto_descuento = $('#forma-pocpedidosautoriza-window').find('input[name=monto_descuento]');
			
			var $subtotal = $('#forma-pocpedidosautoriza-window').find('input[name=subtotal]');
			var $ieps = $('#forma-pocpedidosautoriza-window').find('input[name=ieps]');
			var $impuesto = $('#forma-pocpedidosautoriza-window').find('input[name=impuesto]');
			var $campo_impuesto_retenido = $('#forma-pocpedidosautoriza-window').find('input[name=impuesto_retenido]');
			var $total = $('#forma-pocpedidosautoriza-window').find('input[name=total]');
			
			var $cerrar_plugin = $('#forma-pocpedidosautoriza-window').find('#close');
			var $cancelar_plugin = $('#forma-pocpedidosautoriza-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-pocpedidosautoriza-window').find('#submit');
			
			$('#forma-pocpedidosautoriza-window').find('#tr_importe_subtotal').hide();
			$('#forma-pocpedidosautoriza-window').find('#tr_descto').hide();
			$('#forma-pocpedidosautoriza-window').find('#tr_ieps').hide();
			$('#forma-pocpedidosautoriza-window').find('#tr_retencion').hide();
			$busca_cliente.hide();
			$empresa_immex.val('false');
			$tasa_ret_immex.val('0');
			$busca_cliente.hide();
			$cancelado.hide();
			$autorizar.hide();
			
			if(accion_mode == 'edit'){
				$accion_proceso.attr({'value' : "autorizar"});
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPedido.json';
				$arreglo = {'id_pedido':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-pocpedidosautoriza-window').find('div.interrogacion').css({'display':'none'});
						
						if($accion_proceso.val() == 'cancelar'){
							if ( data['actualizo'] == "1" ){
								jAlert("El Pedido se Cancel&oacute; con &eacute;xito", 'Atencion!');
								var remove = function() {$(this).remove();};
								$('#forma-pocpedidosautoriza-overlay').fadeOut(remove);
							}else{
								jAlert(data['actualizo'], 'Atencion!');
							}
						}else{
							if ( data['actualizo'] == "1" ){
								jAlert("El Pedido fue Autorizado con &eacute;xito", 'Atencion!');
								var remove = function() {$(this).remove();};
								$('#forma-pocpedidosautoriza-overlay').fadeOut(remove);
							}else{
								jAlert(data['actualizo'], 'Atencion!');
							}
						}
						
						//ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
						$submit_actualizar.hide();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-pocpedidosautoriza-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-pocpedidosautoriza-window').find('.pocpedidosautoriza_div_one').css({'height':'568px'});//con errores
						$('#forma-pocpedidosautoriza-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-pocpedidosautoriza-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-pocpedidosautoriza-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-pocpedidosautoriza-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
											$('#forma-pocpedidosautoriza-window').find('.pocpedidosautoriza_div_one').css({'height':'568px'});
											$('#forma-pocpedidosautoriza-window').find('#div_warning_grid').css({'display':'block'});
											
											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
												$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
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
										
										if(tmp.split(':')[0]=='checkauth'){
											jAlert(tmp.split(':')[1], 'Atencion!', function(r) { $autorizar.focus(); });
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
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$tasa_ret_immex.val(entry['datosPedido'][0]['tasa_retencion_immex']);
					$id_pedido.val(entry['datosPedido'][0]['id']);
					$folio.val(entry['datosPedido'][0]['folio']);
					$id_cliente.val(entry['datosPedido'][0]['cliente_id']);
					$nocliente.val(entry['datosPedido'][0]['numero_control']);
					$razon_cliente.val(entry['datosPedido'][0]['razon_social']);
					
					$observaciones.text(entry['datosPedido'][0]['observaciones']);
                    $orden_compra.val(entry['datosPedido'][0]['orden_compra']);
					$transporte.val(entry['datosPedido'][0]['transporte']);
					$lugar_entrega.val(entry['datosPedido'][0]['lugar_entrega']);
					$fecha_compromiso.val(entry['datosPedido'][0]['fecha_compromiso']);
					$tipo_cambio.val(entry['datosPedido'][0]['tipo_cambio']);
					
					var importe_subtotal = parseFloat(parseFloat(entry['datosPedido'][0]['subtotal'])+parseFloat(entry['datosPedido'][0]['monto_descto'])).toFixed(2);
					$importe_subtotal.val(importe_subtotal);
					$monto_descuento.val(entry['datosPedido'][0]['monto_descto']);
					
					$subtotal.val(entry['datosPedido'][0]['subtotal']);
					$ieps.val(entry['datosPedido'][0]['monto_ieps']);
					$impuesto.val(entry['datosPedido'][0]['impuesto']);
					$campo_impuesto_retenido.val(entry['datosPedido'][0]['retencion']);
					$total.val(entry['datosPedido'][0]['total']);
					
					
					var countDisplay=0;
					
					if(parseFloat(entry['datosPedido'][0]['monto_descto'])>0){
						$('#forma-pocpedidosautoriza-window').find('#tr_importe_subtotal').show();
						$('#forma-pocpedidosautoriza-window').find('#tr_descto').show();
						countDisplay++;
					}
					
					if(parseFloat(entry['datosPedido'][0]['monto_ieps'])>0){
						$('#forma-pocpedidosautoriza-window').find('#tr_ieps').show();
						countDisplay++;
					}
					if(parseFloat(entry['datosPedido'][0]['retencion'])>0){
						$('#forma-pocpedidosautoriza-window').find('#tr_retencion').show();
						countDisplay++;
					}
					
					if(parseInt(countDisplay)==1){
						$('#forma-pocpedidosautoriza-window').find('.pocpedidosautoriza_div_one').css({'height':'540px'});
					}
					
					if(parseInt(countDisplay)==2){
						$('#forma-pocpedidosautoriza-window').find('.pocpedidosautoriza_div_one').css({'height':'560px'});
					}
			
					if(parseInt(countDisplay)==3){
						$('#forma-pocpedidosautoriza-window').find('.pocpedidosautoriza_div_one').css({'height':'590px'});
					}
					

					
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosPedido'][0]['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])==4){
							//	moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							//}
						}
					});
					$select_moneda.append(moneda_hmtl);
                    
					
					$id_impuesto.val(entry['iva'][0]['id_impuesto']);
					$valor_impuesto.val(entry['iva'][0]['valor_impuesto']);
					
					//carga select de vendedores
					$select_vendedor.children().remove();
					var hmtl_vendedor;
					$.each(entry['Vendedores'],function(entryIndex,vendedor){
						if(entry['datosPedido'][0]['cxc_agen_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_agente'] + '</option>';
						}else{
							//if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])==4){
							//	hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_agente'] + '</option>';
							//}
						}
					});
					$select_vendedor.append(hmtl_vendedor);
					
					//carga select de condiciones
					$select_condiciones.children().remove();
					var hmtl_condiciones;
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['datosPedido'][0]['cxp_prov_credias_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							//if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])==4){
							//	hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
							//}
						}
					});
					$select_condiciones.append(hmtl_condiciones);
					
					
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo="";
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(entry['datosPedido'][0]['metodo_pago_id'] == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '" selected="yes" >' + metodo['titulo'] + '</option>';
						}else{
							//if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])==4){
							//	hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
							//}
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					
					
					
					
					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prod){
							
							//obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
									trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
									trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
									trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';//este es el id del registro que ocupa el producto en la tabla pocpedidosautoriza_detalles
									//trr += '<span id="elimina">1</span>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['inv_prod_id'] +'">';
									trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';

                                                            if (prod['inv_prod_alias'] !== '') {
                                                                trr += '<INPUT TYPE="text" name="nombre' + tr + '" value="' + prod['inv_prod_alias'] + '" id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
                                                            } else {
                                                                trr += '<INPUT TYPE="text" name="nombre' + tr + '" value="' + prod['titulo'].split('|')[1] + '" id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
                                                            }

							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<INPUT type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
									trr += '<INPUT TYPE="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<INPUT TYPE="text" 	name="cantidad" value="'+  prod['cantidad'] +'" id="cant" class="borde_oculto" style="width:76px;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" class="borde_oculto" style="width:86px; text-align:right;">';
								trr += '<INPUT type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 	id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
								
								trr += '<INPUT type="hidden"    name="id_imp_prod"  value="'+  prod['gral_imp_id'] +'" 		id="idimppord">';
								trr += '<INPUT type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" 	id="ivalorimp">';
								
								trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+parseFloat(prod['importe']) * parseFloat( prod['valor_imp'] )+'">';
							trr += '</td>';
							trr += '</tr>';
							$grid_productos.append(trr);
                            
						});
					}
					
					
					var proceso_flujo = entry['datosPedido']['0']['proceso_flujo_id'];
					
					//proceso_flujo_id=4 :Pedido
					if(parseInt(proceso_flujo)==4){
						$autorizar.show();
					}
					
					//proceso_flujo_id=3 :Facturado
					if(parseInt(proceso_flujo)==3 || parseInt(proceso_flujo)==5){
						$cancelar_pedido.hide();
					}
					
					//si es refacturacion, no se puede cambiar los datos del grid, solo el header de la factura
					if(entry['datosPedido'][0]['cancelado']=="true"){
						$cancelar_pedido.hide();
						$submit_actualizar.hide();
						$busca_sku.hide();
						$agregar_producto.hide();
						$cancelado.show();
						$autorizar.hide();
						
						//Deshabilitar
						$folio.attr('disabled','-1');
						$sku_producto.attr('disabled','-1');
						$nombre_producto.attr('disabled','-1');
						$nocliente.attr('disabled','-1');
						$razon_cliente.attr('disabled','-1');
						$observaciones.attr('disabled','-1');
						$tipo_cambio.attr('disabled','-1');
						$orden_compra.attr('disabled','-1');
						$transporte.attr('disabled','-1');
						$lugar_entrega.attr('disabled','-1');
						$fecha_compromiso.attr('disabled','-1');
						$select_moneda.attr('disabled','-1');
						$select_condiciones.attr('disabled','-1');
						$select_vendedor.attr('disabled','-1');
						
						//$grid_productos.find('a[href*=elimina_producto]').hide();
						$grid_productos.find('#cant').attr('disabled','-1');
						$grid_productos.find('#cost').attr('disabled','-1');
						$grid_productos.find('#import').attr('disabled','-1');
						
						$subtotal.attr('disabled','-1');
						$ieps.attr('disabled','-1');
						$impuesto.attr('disabled','-1');
						$campo_impuesto_retenido.attr('disabled','-1');
						$total.attr('disabled','-1');
					}
					
					$submit_actualizar.hide();
					$busca_sku.hide();
					$agregar_producto.hide();
					$sku_producto.hide();
					$nombre_producto.hide();
					$observaciones.attr("readonly", true);
					$tipo_cambio.attr("readonly", true);
					$orden_compra.attr("readonly", true);
					$transporte.attr("readonly", true);
					$lugar_entrega.attr("readonly", true);
					$grid_productos.find('a[href*=elimina_producto]').hide();
					$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
					$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
					
				});//termina llamada json
                
								
				$tipo_cambio.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				
				//buscador de clientes
				$busca_cliente.click(function(event){
					event.preventDefault();
					$busca_clientes();
				});
				
				
				//buscador de productos
				$busca_sku.click(function(event){
					event.preventDefault();
					$busca_productos($sku_producto.val());
				});
				
				//agregar producto al grid
				$agregar_producto.click(function(event){
					event.preventDefault();
					$buscador_presentaciones_producto($id_cliente, $nocliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio);
				});
				
				
				//ejecutar clic del href Agregar producto al pulsar enter en el campo sku del producto
				$sku_producto.keypress(function(e){
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
				});
				
				
				
				
				$autorizar.click(function(e){
					$accion_proceso.attr({'value' : "autorizar"});
					jConfirm('Confirmar Autorizacion de Pedido?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.parents("FORM").submit();
						}else{
							$accion_proceso.attr({'value' : "autorizar"});
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				
				
				$cancelar_pedido.click(function(e){
					$accion_proceso.attr({'value' : "cancelar"});
					jConfirm('Desea Cancelar el Pedido?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.parents("FORM").submit();
						}else{
							$accion_proceso.attr({'value' : "autorizar"});
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				
                
				//click generar reporte de pedidos 
				$descargarpdf.click(function(event){
					event.preventDefault();
					var id_pedido = $id_pedido.val();
					if($id_pedido.val() != 0 ){
						var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
						var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_pedido/'+id_pedido+'/'+iu+'/out.json';
						window.location.href=input_json;
						
					}else{
						jAlert("Nose esta enviandoel identificador  del pedido","Atencion!!!")
					}
				 });
				
                
                
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
					if(parseInt(trCount) > 0){
						$grid_productos.find('tr').each(function (index){
							$(this).find('#cost').val(quitar_comas( $(this).find('#cost').val() ));
						});
						return true;
					}else{
						jAlert("No hay datos para actualizar", 'Atencion!');
						return false;
					}
				});
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-pocpedidosautoriza-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-pocpedidosautoriza-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllPedidos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllPedidos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formapocpedidosautoriza00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



