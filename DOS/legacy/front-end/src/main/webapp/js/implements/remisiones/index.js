$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select tipo de documento
    var array_select_documento = {
				1:"Factura", 
				2:"Remisi&oacute;n"
			};
			
	var arrayAgentes;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/remisiones";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    //var $new_prefactura = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Remisiones de Clientes');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
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
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "folio_pedido" + signo_separador + $busqueda_folio_pedido.val() + "|";
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
			agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
		});
		$busqueda_select_agente.append(agente_hmtl);
		
		//asignamos el arreglo a una variable para utilizarla mas adelante
		arrayAgentes = data['Agentes'];
	});
	
	
	$limpiar.click(function(event){
		$busqueda_folio.val('');
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
			agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
		});
		$busqueda_select_agente.append(agente_hmtl);
		
		//asignar el enfoque al limpiar campos
		$busqueda_folio.focus();
		
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
		
		//asignar el enfoque al visualizar el buscador
		$busqueda_folio.focus();
	});
	
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio_pedido, $buscar);
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
		var $select_prod_tipo = $('#forma-remisiones-window').find('select[name=prodtipo]');
		$('#forma-remisiones-window').find('#submit').mouseover(function(){
			$('#forma-remisiones-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-remisiones-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-remisiones-window').find('#submit').mouseout(function(){
			$('#forma-remisiones-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-remisiones-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-remisiones-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-remisiones-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-remisiones-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-remisiones-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-remisiones-window').find('#close').mouseover(function(){
			$('#forma-remisiones-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-remisiones-window').find('#close').mouseout(function(){
			$('#forma-remisiones-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-remisiones-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-remisiones-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-remisiones-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-remisiones-window').find("ul.pestanas li").click(function() {
			$('#forma-remisiones-window').find(".contenidoPes").hide();
			$('#forma-remisiones-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-remisiones-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
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


	
	//calcula totales(subtotal, impuesto, total)
	$calcula_totales = function(){
		var $campo_subtotal = $('#forma-remisiones-window').find('input[name=subtotal]');
		var $campo_ieps = $('#forma-remisiones-window').find('input[name=ieps]');
		var $campo_impuesto = $('#forma-remisiones-window').find('input[name=impuesto]');
		var $campo_impuesto_retenido = $('#forma-remisiones-window').find('input[name=impuesto_retenido]');
		var $campo_total = $('#forma-remisiones-window').find('input[name=total]');
		var $empresa_immex = $('#forma-remisiones-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-remisiones-window').find('input[name=tasa_ret_immex]');
		
		
		
		
		var $grid_productos = $('#forma-remisiones-window').find('#grid_productos');
		
		//Suma de todos los importes
		var sumaSubTotal = 0;
		//Suma del iva
		var sumaImpuesto = 0;
		//Monto del iva retenido de acuerdo a la tasa de retencion immex
		var impuestoRetenido = 0;
		//Suma del subtotal + totalImpuesto
		var sumaTotal = 0;
		//Suma de todos los importes del IEPS
		var sumaIeps = 0;
		
		$grid_productos.find('tr').each(function (index){
			if($(this).find('#cost').val().trim()!='' && $(this).find('#cant').val().trim()!=''){
				//Acumula los importes en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($(this).find('#import').val()));
				
				if($(this).find('#importeIeps').val().trim()!=''){
					//Acumula los importes del IEPS
					sumaIeps = parseFloat(sumaIeps) + parseFloat($(this).find('#importeIeps').val());
				}
				
				if($(this).find('#totimp').val().trim()!=''){
					sumaImpuesto = parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
		//calcular  la tasa de retencion IMMEX
		impuestoRetenido = parseFloat(sumaSubTotal) * parseFloat(parseFloat($tasa_ret_immex.val()));
		
		//calcula el total sumando el subtotal y el impuesto
		sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaIeps) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
		
		//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
		$campo_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
		
		$campo_ieps.val($(this).agregar_comas(  parseFloat(sumaIeps).toFixed(2)  ));

		//redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val($(this).agregar_comas(  parseFloat(sumaImpuesto).toFixed(2)  ));
		
		//Redondea a dos digitos el impuesto y lo asigna al campo retencion
		$campo_impuesto_retenido.val($(this).agregar_comas(  parseFloat(impuestoRetenido).toFixed(2)  ));
		
		//redondea a dos digitos la suma  total y se asigna al campo total
		$campo_total.val($(this).agregar_comas(  parseFloat(sumaTotal).toFixed(2)  ));
		
		
		//Ocultar campos si tienen valor menor o igual a cero
		if(parseFloat(sumaIeps)<=0){
			$('#forma-remisiones-window').find('#tr_ieps').hide();
		}else{
			$('#forma-remisiones-window').find('#tr_ieps').show();
		}
		if(parseFloat(impuestoRetenido)<=0){
			$('#forma-remisiones-window').find('#tr_retencion').hide();
		}else{
			$('#forma-remisiones-window').find('#tr_retencion').show();
		}
		
		if(parseFloat(sumaIeps)<=0 && parseFloat(impuestoRetenido)<=0){
			$('#forma-remisiones-window').find('.remisiones_div_one').css({'height':'500px'});
		}
		
		if(parseFloat(sumaIeps)>0 && parseFloat(impuestoRetenido)<=0){
			$('#forma-remisiones-window').find('.remisiones_div_one').css({'height':'525px'});
		}
		
		if(parseFloat(sumaIeps)<=0 && parseFloat(impuestoRetenido)>0){
			$('#forma-remisiones-window').find('.remisiones_div_one').css({'height':'525px'});
		}
		
		if(parseFloat(sumaIeps)>0 && parseFloat(impuestoRetenido)>0){
			$('#forma-remisiones-window').find('.remisiones_div_one').css({'height':'555px'});
		}
		
	}//termina calcular totales
	
	
	
	
	
	
	var carga_formaremisiones00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_remision':id_to_show,
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
			$('#forma-remisiones-window').remove();
			$('#forma-remisiones-overlay').remove();
            
			var form_to_show = 'formaremisiones00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_remisiones();
			
			$('#forma-remisiones-window').css({"margin-left": -400, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-remisiones-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRemision.json';
				$arreglo = {'id_remision':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var $folio_remision = $('#forma-remisiones-window').find('input[name=folio_remision]');
				var $folio_pedido = $('#forma-remisiones-window').find('input[name=folio_pedido]');
				var $pagar_remision = $('#forma-remisiones-window').find('#pagar_remision');
				
				var $total_tr = $('#forma-remisiones-window').find('input[name=total_tr]');
				var $id_remision = $('#forma-remisiones-window').find('input[name=id_remision]');
				
				var $id_cliente = $('#forma-remisiones-window').find('input[name=id_cliente]');
				var $no_cliente = $('#forma-remisiones-window').find('input[name=nocliente]');
				var $razon_cliente = $('#forma-remisiones-window').find('input[name=razoncliente]');
				var $tasa_ret_immex = $('#forma-remisiones-window').find('input[name=tasa_ret_immex]');
				var $empresa_immex = $('#forma-remisiones-window').find('input[name=empresa_immex]');
				
				var $select_moneda = $('#forma-remisiones-window').find('select[name=moneda]');
				var $tipo_cambio = $('#forma-remisiones-window').find('input[name=tipo_cambio]');
				var $orden_compra = $('#forma-remisiones-window').find('input[name=orden_compra]');
				
				var $select_metodo_pago = $('#forma-remisiones-window').find('select[name=select_metodo_pago]');
				var $etiqueta_digit = $('#forma-remisiones-window').find('input[name=digit]');
				//var $digitos = $('#forma-remisiones-window').find('input[name=digitos]');
				var $no_cuenta = $('#forma-remisiones-window').find('input[name=no_cuenta]');
				
				var $id_impuesto = $('#forma-remisiones-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-remisiones-window').find('input[name=valorimpuesto]');
				
				var $observaciones = $('#forma-remisiones-window').find('textarea[name=observaciones]');
				var $select_condiciones = $('#forma-remisiones-window').find('select[name=condiciones]');
				var $select_vendedor = $('#forma-remisiones-window').find('select[name=vendedor]');
				
				var $cancelar_remision = $('#forma-remisiones-window').find('#cancelar_remision');
				var $genera_pdf = $('#forma-remisiones-window').find('#genera_pdf');
				
				//grid de productos
				var $grid_productos = $('#forma-remisiones-window').find('#grid_productos');
				//grid de errores
				var $grid_warning = $('#forma-remisiones-window').find('#div_warning_grid').find('#grid_warning');
				
				var $subtotal = $('#forma-remisiones-window').find('input[name=subtotal]');
				var $impuesto = $('#forma-remisiones-window').find('input[name=impuesto]');
				var $total = $('#forma-remisiones-window').find('input[name=total]');
				var $campo_ieps = $('#forma-remisiones-window').find('input[name=ieps]');
				var $campo_impuesto_retenido = $('#forma-remisiones-window').find('input[name=impuesto_retenido]');
				
				var $cerrar_plugin = $('#forma-remisiones-window').find('#close');
				var $cancelar_plugin = $('#forma-remisiones-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-remisiones-window').find('#submit');
				
				//ocultar boton actualizar porque ya esta facturado, ya no se puede guardar cambios
				//$submit_actualizar.hide();
				//$digitos.hide();
				//$no_cuenta.hide();
				//$digitos.attr('disabled','-1');
				$etiqueta_digit.attr('disabled','-1');
				//$('#forma-remisiones-window').find('#tr_ieps').hide();
				//$('#forma-remisiones-window').find('#tr_retencion').hide();
				
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-remisiones-window').find('div.interrogacion').css({'display':'none'});
						jAlert("La Remisi&oacute;n se guard&oacute; con &eacute;xito", 'Atencion!');
						
						var remove = function() {$(this).remove();};
						$('#forma-remisiones-overlay').fadeOut(remove);
						
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-remisiones-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-remisiones-window').find('.remisiones_div_one').css({'height':'548px'});//con errores
						$('#forma-remisiones-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-remisiones-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-remisiones-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');

							if( longitud.length > 1 ){
								$('#forma-remisiones-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
											$('#forma-remisiones-window').find('.remisiones_div_one').css({'height':'548px'});
											$('#forma-remisiones-window').find('#div_warning_grid').css({'display':'block'});
											
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
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					var flujo_proceso = entry['datosRemision']['0']['proceso_flujo_id'];
					$folio_remision.val(entry['datosRemision']['0']['folio']);
					$folio_pedido.val(entry['datosRemision']['0']['folio_pedido']);
					$id_remision.val(entry['datosRemision']['0']['id']);
					$id_cliente.val(entry['datosRemision']['0']['cliente_id']);
					$no_cliente.val(entry['datosRemision']['0']['numero_control']);
					$razon_cliente.val(entry['datosRemision']['0']['razon_social']);
					$observaciones.text(entry['datosRemision']['0']['observaciones']);
                    $orden_compra.val(entry['datosRemision']['0']['orden_compra']);
                    $tasa_ret_immex.val(entry['datosRemision']['0']['tasa_retencion_immex']);
                    $empresa_immex.val(entry['datosRemision']['0']['empresa_immex']);
                    $tipo_cambio.val(entry['datosRemision']['0']['tipo_cambio']);
					$no_cuenta.val(entry['datosRemision']['0']['no_cuenta']);


					$subtotal.val(entry['datosRemision']['0']['subtotal']);
					$impuesto.val(entry['datosRemision']['0']['impuesto']);
					$total.val(entry['datosRemision']['0']['total']);
					$campo_ieps.val(entry['datosRemision']['0']['monto_ieps']);
					$campo_impuesto_retenido.val(entry['datosRemision']['0']['monto_retencion']);
					
					
					
					var valorHeight=510;
					
					//Ocultar campos si tienen valor menor o igual a cero
					if(parseFloat($campo_ieps.val())<=0){
						$('#forma-remisiones-window').find('#tr_ieps').hide();
					}else{
						$('#forma-remisiones-window').find('#tr_ieps').show();
						valorHeight = parseFloat(valorHeight) + 15;
					}
					
					if(parseFloat($campo_impuesto_retenido.val())<=0){
						$('#forma-remisiones-window').find('#tr_retencion').hide();
					}else{
						$('#forma-remisiones-window').find('#tr_retencion').show();
						valorHeight = parseFloat(valorHeight) + 15;
					}
					
					$('#forma-remisiones-window').find('.remisiones_div_one').css({'height':valorHeight+'px'});
					
					
					
					
					
                    if( entry['datosRemision']['0']['cancelado'] == 'true' ){
						$cancelar_remision.hide();
						$submit_actualizar.hide();
						$orden_compra.attr("readonly", true);
					}
                    if( parseInt(entry['datosRemision']['0']['estatus']) != 0 ){
						$pagar_remision.hide();
						$cancelar_remision.hide();
					}
					
                    if( entry['datosRemision']['0']['facturado'] == 'true' ){
						$cancelar_remision.hide();
					}
                    
					//cargar select de tipos de movimiento
					var elemento_seleccionado = entry['datosRemision']['0']['tipo_documento'];
					$carga_select_con_arreglo_fijo($folio_remision, array_select_documento, elemento_seleccionado);
					
                    
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosRemision']['0']['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							if(parseInt(flujo_proceso)==2){
								moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							}
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					$id_impuesto.val(entry['iva']['0']['id_impuesto']);
					$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
					
					//carga select de vendedores
					$select_vendedor.children().remove();
					var hmtl_vendedor;
					$.each(entry['Vendedores'],function(entryIndex,vendedor){
						if(entry['datosRemision']['0']['cxc_agen_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_agente'] + '</option>';
						}else{
							if(parseInt(flujo_proceso)==2){
								hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_agente'] + '</option>';
							}
						}
					});
					$select_vendedor.append(hmtl_vendedor);
					
					
					//carga select de condiciones
					$select_condiciones.children().remove();
					var hmtl_condiciones;
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['datosRemision']['0']['terminos_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							if(parseInt(flujo_proceso)==2){
								hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
							}
						}
					});
					$select_condiciones.append(hmtl_condiciones);
					
					
					var valor_metodo = entry['datosRemision']['0']['fac_metodos_pago_id'];
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo="";
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(valor_metodo == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '"  selected="yes">' + metodo['titulo'] + '</option>';
						}else{
							if(parseInt(flujo_proceso)==2){
								hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
							}
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					
					
					
					
					
					
					//metodo pago 2=Tarjeta de credito, 3=tarjeta de debito
					if(parseInt(valor_metodo)==2 || parseInt(valor_metodo)==3){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura de los digitos de la tarjeta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						//quitar propiedad de solo lectura
						//$no_cuenta.removeAttr('readonly');
						
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						$etiqueta_digit.val('Ingrese los ultimos 4 Digitos de la Tarjeta');
					}
					
					
					if(parseInt(valor_metodo)==4 || parseInt(valor_metodo)==5){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
					if(parseInt(valor_metodo)==1 || parseInt(valor_metodo)==6){
						$no_cuenta.val('');
						if(!$no_cuenta.is(':disabled')) {
							$no_cuenta.attr('disabled','-1');
						}
						if(!$etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.attr('disabled','-1');
						}
					}
					
					var tasaIeps="";
					var importeIeps="";
					var valorImporteIeps=0;
					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prod){
							
							//obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							tasaIeps="";
							importeIeps="";
							valorImporteIeps=0;
							
							if(parseInt(prod['id_ieps'])>0){
								tasaIeps=prod['tasa_ieps'];
								importeIeps=prod['importe_ieps'];
								valorImporteIeps=prod['importe_ieps'];
							}
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
									trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
									trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
									trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';//este es el id del registro que ocupa el producto en la tabla remisiones_detalles
									//trr += '<span id="elimina">1</span>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="116">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['producto_id'] +'">';
									trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
								trr += '<INPUT TYPE="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<INPUT type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
									trr += '<INPUT TYPE="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<INPUT TYPE="text" 	name="cantidad" value="'+  prod['cantidad'] +'" 		id="cant" class="borde_oculto" style="width:76px;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" class="borde_oculto" style="width:86px; text-align:right;">';
								trr += '<INPUT type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 	id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
								trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+parseFloat(parseFloat(prod['importe']) + parseFloat(valorImporteIeps)) * parseFloat(prod['valor_imp'])+'">';
								trr += '<INPUT type="hidden"    name="id_imp_prod"  value="'+  prod['tipo_impuesto_id'] +'" id="idimppord">';
								trr += '<INPUT type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" 		id="ivalorimp">';
							trr += '</td>';
							
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="50">';
								trr += '<input type="hidden" name="idIeps"     value="'+ prod['id_ieps'] +'" id="idIeps">';
								trr += '<input type="text" name="tasaIeps" value="'+ tasaIeps +'" class="borde_oculto" id="tasaIeps" style="width:46px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70">';
								trr += '<input type="text" name="importeIeps" value="'+ importeIeps +'" class="borde_oculto" id="importeIeps" style="width:66px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '</tr>';
							$grid_productos.append(trr);
							
						});
					}
					
					
					
					
					
					
					//$calcula_totales();//llamada a la funcion que calcula totales 
					
					
					//si es refacturacion, no se puede cambiar los datos del grid, solo el header de la factura
					if(entry['datosRemision']['0']['refacturar']=='true'){
						$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
						$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
					}
					
					//flujo_proceso 2=Prefactura
					if(parseInt(flujo_proceso)==2){
						$boton_facturar.show();
					}
					
					//proceso_flujo_id 3=Facturado,5=Remision
					if(parseInt(flujo_proceso)==3 || parseInt(flujo_proceso)==5){
						$tipo_cambio.val(entry['datosRemision']['0']['tipo_cambio']);
					}
					
					$observaciones.attr("readonly", true);
					$tipo_cambio.attr("readonly", true);
					//$orden_compra.attr("readonly", true);
					//$digitos.attr("readonly", true);
					
					$grid_productos.find('a[href*=elimina_producto]').hide();
					$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
					$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
					
				});//termina llamada json
                
				
                
                
				//descargar pdf de la Remision
				$genera_pdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfRemision/'+$id_remision.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
                
                
                
                
                
				$pagar_remision.click(function(e){
					
					jConfirm('Desea Pagar la Remision?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
								var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPagaRemision.json';
								$arreglo = {	'id_remision':$id_remision.val(),
												'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
											}
								
								$.post(input_json,$arreglo,function(entry){
									var cancelado  = entry['success'];
									if(cancelado == 'true'){
										var remove = function() {$(this).remove();};
										$('#forma-remisiones-overlay').fadeOut(remove);
										jAlert("La Remision: "+$folio_remision.val()+" ha  sido Pagada con exito.", 'Atencion!');
									}else{
										jAlert( entry['success'] , 'Atencion!');
									}
								});//termina llamada json
								
						}else{
							//Aaqui no hace nada
						}
					});
				});

                
                
                
                
				$cancelar_remision.click(function(e){
					
					jConfirm('Desea Cancelar la Remision?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
								var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCancelaRemision.json';
								$arreglo = {	'id_remision':$id_remision.val(),
												'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
											}
								
								$.post(input_json,$arreglo,function(entry){
									var cancelado  = entry['success'];
									if(cancelado == 'true'){
										var remove = function() {$(this).remove();};
										$('#forma-remisiones-overlay').fadeOut(remove);
										jAlert("La Remision: "+$folio_remision.val()+" ha  sido Cancelada con exito.", 'Atencion!');
									}else{
										jAlert( entry['success'] , 'Atencion!');
									}
								});//termina llamada json
								
						}else{
							//Aaqui no hace nada
						}
					});
				});
				
				
                
                
                /*
				//cancelar factura
				$cancelar_remision.click(function(event){
					event.preventDefault();
					var id_to_show = 0;
					$(this).modalPanel_cancelaemision();
					var form_to_show = 'formaCancelaEmision';
					$('#' + form_to_show).each (function(){this.reset();});
					var $forma_selected = $('#' + form_to_show).clone();
					$forma_selected.attr({id : form_to_show + id_to_show});
					$('#forma-cancelaemision-window').css({"margin-left": -100,"margin-top": -180});
					$forma_selected.prependTo('#forma-cancelaemision-window');
					$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
					
					var $select_tipo_cancelacion = $('#forma-cancelaemision-window').find('select[name=tipo_cancelacion]');
					var $motivo_cancelacion = $('#forma-cancelaemision-window').find('textarea[name=motivo_cancel]');
					
					var $cancelar_factura = $('#forma-cancelaemision-window').find('a[href*=cancelfact]');
					var $salir = $('#forma-cancelaemision-window').find('a[href*=salir]');
					
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
							$arreglo = {'id_remision':$id_remision.val(),
										'tipo_cancelacion':$select_tipo_cancelacion.val(),
										'motivo':$motivo_cancelacion.val(),
										'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
										
										}
							
							$.post(input_json,$arreglo,function(entry){
								var cad = entry['success'].split(":");
								if(cad[1]=='false'){
									jAlert("La factura "+cad[0]+" tiene pagos aplicados. Es necesario cancelar primeramente los pagos y despues cancelar la factura.", 'Atencion!');
								}else{
									$boton_descargarpdf.hide();
									$boton_cancelarfactura.hide();
									$boton_descargarxml.hide();
									jAlert("La factura "+cad[0]+"  se ha cancelado con &eacute;xito", 'Atencion!');
									$get_datos_grid();
								}
								
								var remove = function() {$(this).remove();};
								$('#forma-cancelaemision-overlay').fadeOut(remove);
							});//termina llamada json
						}else{
							jAlert("Es necesario ingresar el motivo de la cancelaci&oacute;n", 'Atencion!');
						}
					});
					
					
					$salir.click(function(event){
						event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-cancelaemision-overlay').fadeOut(remove);
					});
					
				});//termina cancelar factura
                */
                
                
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-remisiones-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-remisiones-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllRemisiones.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllRemisiones.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaremisiones00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});
