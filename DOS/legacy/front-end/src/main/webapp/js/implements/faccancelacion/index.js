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
	var controller = $contextpath.val()+"/controllers/faccancelacion";
    
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
	$('#barra_titulo').find('#td_titulo').append('Cancelaci&oacute;n de Facturas');
	
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
	

	
	TriggerClickVisializaBuscador = 0;
	
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		$('#barra_genera_informe').find('.tabla_genera_informe').css({'display':'none'});
		//$('#barra_descarga_xml').find('.tabla_decarga_xml').css({'display':'none'});
		
		var alto=0;
		if(TriggerClickVisializaBuscador==0){
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
            var $select_prod_tipo = $('#forma-faccancelacion-window').find('select[name=prodtipo]');
            $('#forma-faccancelacion-window').find('#submit').mouseover(function(){
                $('#forma-faccancelacion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                //$('#forma-faccancelacion-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
            })
            $('#forma-faccancelacion-window').find('#submit').mouseout(function(){
                $('#forma-faccancelacion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                //$('#forma-faccancelacion-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
            })
            $('#forma-faccancelacion-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-faccancelacion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-faccancelacion-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-faccancelacion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-faccancelacion-window').find('#close').mouseover(function(){
                $('#forma-faccancelacion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-faccancelacion-window').find('#close').mouseout(function(){
                $('#forma-faccancelacion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-faccancelacion-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-faccancelacion-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-faccancelacion-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-faccancelacion-window').find("ul.pestanas li").click(function() {
                $('#forma-faccancelacion-window').find(".contenidoPes").hide();
                $('#forma-faccancelacion-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-faccancelacion-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
    
    //convertir costos en dolar y pesos
	var $convertir_costos = function($tipo_cambio,moneda_id,$campo_subtotal,$campo_impuesto,$campo_total,$valor_impuesto,$grid_productos){
		var sumaSubTotal = 0; //es la suma de todos los importes
		var sumaImpuesto = 0; //valor del iva
		var sumaTotal = 0; //suma del subtotal + totalImpuesto
		var $moneda_original = $('#forma-faccancelacion-window').find('input[name=moneda_original]');
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		$grid_productos.find('tr').each(function (index){
			var precio_cambiado=0;
			var importe_cambiado=0;
			if(( $(this).find('#cost').val() != ' ') && ( $(this).find('#cant').val() != ' ' )){
				if( parseInt($moneda_original.val()) != parseInt(moneda_id) ){
					if(parseInt($moneda_original.val())==1){
						//si la moneda original es pesos, calculamos su equivalente a dolares
						precio_cambiado = parseFloat(quitar_comas($(this).find('#costor').val())) / parseFloat($tipo_cambio.val());
					}else{
						//si la moneda original es dolar, calculamos su equivalente a pesos
						precio_cambiado = parseFloat(quitar_comas($(this).find('#costor').val())) * parseFloat($tipo_cambio.val());
					}
					
					$(this).find('#cost').val($(this).agregar_comas(parseFloat(precio_cambiado).toFixed(4)));
					//calcula el nuevo importe
					importe_cambiado = parseFloat($(this).find('#cant').val()) * parseFloat(precio_cambiado).toFixed(4);
					//asignamos el nuevo laor del importe
					$(this).find('#import').val($(this).agregar_comas(parseFloat(importe_cambiado).toFixed(4) ) );
				}else{
					//aqui entra si la moneda seleccionada es la moneda original. Le devolvemos al campo costo su valor original
					$(this).find('#cost').val( $(this).find('#costor').val()  );
					//calcula el nuevo importe
					importe_cambiado = parseFloat($(this).find('#cant').val()) * parseFloat($(this).find('#cost').val()).toFixed(2);
					//asignamos el nuevo laor del importe
					$(this).find('#import').val($(this).agregar_comas(parseFloat(importe_cambiado).toFixed(2) ) );
				}
				
				//acumula los importes en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($(this).find('#import').val()));
				if($(this).find('#totimp').val() != ''){
					$(this).find('#totimp').val(parseFloat( quitar_comas($(this).find('#import').val()) ) * parseFloat($valor_impuesto.val()));
					sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
		if( parseInt($moneda_original.val()) != parseInt(moneda_id) ){
			//calcula el total sumando el subtotal y el impuesto
			sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
			//redondea a 4 digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(parseFloat(sumaSubTotal).toFixed(4)));
			//redondea a 4 digitos el impuesto y lo asigna al campo impuesto
			$campo_impuesto.val($(this).agregar_comas(parseFloat(sumaImpuesto).toFixed(4)));
			//redondea a 4 digitos la suma  total y se asigna al campo total
			$campo_total.val($(this).agregar_comas(parseFloat(sumaTotal).toFixed(4)));
		}else{
			//calcula el total sumando el subtotal y el impuesto
			sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
			//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(parseFloat(sumaSubTotal).toFixed(2)));
			//redondea a dos digitos el impuesto y lo asigna al campo impuesto
			$campo_impuesto.val($(this).agregar_comas(parseFloat(sumaImpuesto).toFixed(2)));
			//redondea a dos digitos la suma  total y se asigna al campo total
			$campo_total.val($(this).agregar_comas(parseFloat(sumaTotal).toFixed(2)));
		}
	}//termina convertir dolar pesos
	
	
	
	//calcula totales(subtotal, impuesto, total)
	$calcula_totales = function(){
		var $campo_subtotal = $('#forma-faccancelacion-window').find('input[name=subtotal]');
		var $campo_impuesto = $('#forma-faccancelacion-window').find('input[name=impuesto]');
		var $campo_total = $('#forma-faccancelacion-window').find('input[name=total]');
		//var $campo_tc = $('#forma-faccancelacion-window').find('input[name=tc]');
		var $valor_impuesto = $('#forma-faccancelacion-window').find('input[name=valorimpuesto]');
		var $grid_productos = $('#forma-faccancelacion-window').find('#grid_productos');
		
		var sumaSubTotal = 0; //es la suma de todos los importes
		var sumaImpuesto = 0; //valor del iva
		var sumaTotal = 0; //suma del subtotal + totalImpuesto
		/*
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $campo_tc.val()== null || $campo_tc.val()== ''){
			$campo_tc.val(0);
		}
		*/
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#cost').val() != ' ') && ( $(this).find('#cant').val() != ' ' )){
				//alert($(this).find('#cost').val());
				//acumula los importes en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($(this).find('#import').val()));
				//alert($(this).find('#import').val());
				if($(this).find('#totimp').val() != ''){
					//alert($(this).find('#totimp').val());
						sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
		//calcula el total sumando el subtotal y el impuesto
		sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
		
		//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
		$campo_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
		//redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val($(this).agregar_comas(  parseFloat(sumaImpuesto).toFixed(2)  ));
		//redondea a dos digitos la suma  total y se asigna al campo total
		$campo_total.val($(this).agregar_comas(  parseFloat(sumaTotal).toFixed(2)  ));
		
	}//termina calcular totales
	
	
	
	
	
	
	//Dibuja modal de cancelacion
	var modal_cancelar= function(id_to_show, $boton_cancelarfactura, tmov_id, tipo_id, motivo, cancelado, iu){
		$(this).modalPanel_cancelaemision();
		var form_to_show = 'formaCancelaEmision';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		$('#forma-cancelaemision-window').css({"margin-left": -100,"margin-top": -180});
		$forma_selected.prependTo('#forma-cancelaemision-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		var $select_tipo_cancelacion = $('#forma-cancelaemision-window').find('select[name=tipo_cancelacion]');
		var $select_tmov = $('#forma-cancelaemision-window').find('select[name=select_tmov]');
		var $motivo_cancelacion = $('#forma-cancelaemision-window').find('textarea[name=motivo_cancel]');
		
		var $boton_cancelfact = $('#forma-cancelaemision-window').find('#boton_cancelfact');
		var $boton_salir_cancelacion = $('#forma-cancelaemision-window').find('#boton_salir_cancelacion');
		
		if(cancelado){
			$boton_cancelfact.hide();
			$motivo_cancelacion.attr("readonly", true);
		}
		
		$motivo_cancelacion.text(motivo);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTiposCancelacion.json';
		var $arreglo = {'identificador':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		
		$.post(input_json,$arreglo,function(entry){
			$select_tipo_cancelacion.children().remove();
			var tipo_hmtl = '';
			$.each(entry['Tipos'],function(entryIndex,tipo){
				if(parseInt(tipo['id'])==parseInt(tipo_id)){
					tipo_hmtl += '<option value="' + tipo['id'] + '" selected="yes">' + tipo['titulo'] + '</option>';
				}else{
					if(!cancelado){
						tipo_hmtl += '<option value="' + tipo['id'] + '">' + tipo['titulo'] + '</option>';
					}
				}
			});
			$select_tipo_cancelacion.append(tipo_hmtl);
			
			
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
		});
		
		
		$boton_cancelfact.click(function(event){
			event.preventDefault();
			
			//if(parseInt($select_tmov.val())>0){
				if($motivo_cancelacion.val()!=null && $motivo_cancelacion.val()!=""){
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/cancelar_factura.json';
					var $arreglo = {'id_factura':id_to_show,'tipo_cancelacion':$select_tipo_cancelacion.val(),'tmov':$select_tmov.val(),'motivo':$motivo_cancelacion.val(),'iu':iu };
					
					$.post(input_json,$arreglo,function(entry){
						var cad = entry['success'].split(":");
						
						if(entry['success'].trim()=='false'){
							jAlert(entry['msj'], 'Atencion!');
						}else{
							if(entry['valor'].trim()=='false'){
								jAlert(entry['msj'], 'Atencion!');
							}else{
								$boton_cancelarfactura.hide();
								jAlert(entry['msj'], 'Atencion!');
								$get_datos_grid();
								
								var remove = function() {$(this).remove();};
								$('#forma-cancelaemision-overlay').fadeOut(remove);
							}
						}
					});//termina llamada json
				}else{
					jAlert("Es necesario ingresar el motivo de la cancelaci&oacute;n", 'Atencion!');
				}
			/*
			}else{
				jAlert('Es necesario seleccionar el Tipo de Movimiento.', 'Atencion!', function(r) { 
					$select_tmov.focus();
				});
			}
			*/
		});
		
		$boton_salir_cancelacion.click(function(event){
			event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-cancelaemision-overlay').fadeOut(remove);
		});
	}
	
	
	

	//ver detalles de una factura
	var carga_formafaccancelacion00_for_datagrid00Edit = function(id_to_show, accion_mode){
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
							//$get_datos_grid();
						}
						else{
							jAlert("La factura no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-faccancelacion-window').remove();
			$('#forma-faccancelacion-overlay').remove();
            
			var form_to_show = 'formafaccancelacion00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_faccancelacion();
			
			$('#forma-faccancelacion-window').css({"margin-left": -390, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-faccancelacion-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFactura.json';
				$arreglo = {'id_factura':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
				
				var $total_tr = $('#forma-faccancelacion-window').find('input[name=total_tr]');
				var $id_factura = $('#forma-faccancelacion-window').find('input[name=id_factura]');
				var $folio_pedido = $('#forma-faccancelacion-window').find('input[name=folio_pedido]');
				var $fecha = $('#forma-faccancelacion-window').find('input[name=fecha]');
				var $id_cliente = $('#forma-faccancelacion-window').find('input[name=id_cliente]');
				var $rfc_cliente = $('#forma-faccancelacion-window').find('input[name=rfccliente]');
				var $razon_cliente = $('#forma-faccancelacion-window').find('input[name=razoncliente]');
				var $dir_cliente = $('#forma-faccancelacion-window').find('input[name=dircliente]');
				
				var $serie_folio = $('#forma-faccancelacion-window').find('input[name=serie_folio]');
				var $select_moneda = $('#forma-faccancelacion-window').find('select[name=moneda]');
				var $moneda_original = $('#forma-faccancelacion-window').find('input[name=moneda_original]');
				var $tipo_cambio = $('#forma-faccancelacion-window').find('input[name=tipo_cambio]');
				var $orden_compra = $('#forma-faccancelacion-window').find('input[name=orden_compra]');
				
				//var $campo_tc = $('#forma-faccancelacion-window').find('input[name=tc]');
				var $id_impuesto = $('#forma-faccancelacion-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-faccancelacion-window').find('input[name=valorimpuesto]');
				
				var $observaciones = $('#forma-faccancelacion-window').find('textarea[name=observaciones]');
				var $select_condiciones = $('#forma-faccancelacion-window').find('select[name=condiciones]');
				var $select_vendedor = $('#forma-faccancelacion-window').find('select[name=vendedor]');
				var $select_metodo_pago = $('#forma-faccancelacion-window').find('select[name=select_metodo_pago]');
				
				var $fecha_can = $('#forma-faccancelacion-window').find('input[name=fecha_can]');
				
				//var $etiqueta_digit = $('#forma-faccancelacion-window').find('input[name=digit]');
				//var $digitos = $('#forma-faccancelacion-window').find('input[name=digitos]');
				//var $no_cuenta = $('#forma-faccancelacion-window').find('input[name=no_cuenta]');
				//var $sku_producto = $('#forma-faccancelacion-window').find('input[name=sku_producto]');
				//var $nombre_producto = $('#forma-faccancelacion-window').find('input[name=nombre_producto]');
				
				//buscar producto
				//var $busca_sku = $('#forma-faccancelacion-window').find('a[href*=busca_sku]');
				//href para agregar producto al grid
				//var $agregar_producto = $('#forma-faccancelacion-window').find('a[href*=agregar_producto]');
				
				//var $reconstruir_pdf = $('#forma-faccancelacion-window').find('#reconstruir_pdf');
				//var $boton_descargarpdf = $('#forma-faccancelacion-window').find('#descargarpdf');
				var $boton_cancelarfactura = $('#forma-faccancelacion-window').find('#cancelarfactura');
				//var $boton_descargarxml = $('#forma-faccancelacion-window').find('#descargarxml');
				
				//grid de productos
				var $grid_productos = $('#forma-faccancelacion-window').find('#grid_productos');
				//grid de errores
				var $grid_warning = $('#forma-faccancelacion-window').find('#div_warning_grid').find('#grid_warning');
				
				//var $flete = $('#forma-faccancelacion-window').find('input[name=flete]');
				var $subtotal = $('#forma-faccancelacion-window').find('input[name=subtotal]');
				var $campo_ieps = $('#forma-faccancelacion-window').find('input[name=ieps]');
				var $impuesto = $('#forma-faccancelacion-window').find('input[name=impuesto]');
				var $impuesto_retenido = $('#forma-faccancelacion-window').find('input[name=impuesto_retenido]');
				var $total = $('#forma-faccancelacion-window').find('input[name=total]');
				
				var $cerrar_plugin = $('#forma-faccancelacion-window').find('#close');
				var $cancelar_plugin = $('#forma-faccancelacion-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-faccancelacion-window').find('#submit');
				
				//ocultar boton descargar y facturar. Despues de facturar debe mostrarse
				//$reconstruir_pdf.hide();
				//$boton_descargarpdf.hide();
				$boton_cancelarfactura.hide();
				$submit_actualizar.hide();
				
				//$digitos.attr('disabled','-1');
				//$etiqueta_digit.attr('disabled','-1');
				//$no_cuenta.hide();
				$('#forma-faccancelacion-window').find('input').attr("readonly", true)
				$('#forma-faccancelacion-window').find('textarea').attr("readonly", true)
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-faccancelacion-window').find('div.interrogacion').css({'display':'none'});
						jAlert("La prefactura se guard&oacute; con &eacute;xito", 'Atencion!');
						/*
						var remove = function() {$(this).remove();};
						$('#forma-faccancelacion-overlay').fadeOut(remove);
						*/
						//ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
						$submit_actualizar.hide();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-faccancelacion-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-faccancelacion-window').find('.faccancelacion_div_one').css({'height':'575px'});//con errores
						$('#forma-faccancelacion-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-faccancelacion-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-faccancelacion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');

							if( longitud.length > 1 ){
								$('#forma-faccancelacion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

								//alert(tmp.split(':')[0]);

								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
											$('#forma-faccancelacion-window').find('.faccancelacion_div_one').css({'height':'575px'});
											$('#forma-faccancelacion-window').find('#div_warning_grid').css({'display':'block'});
											
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
					$id_factura.val(entry['Datos'][0]['id']);
					$folio_pedido.val(entry['Datos'][0]['folio_pedido']);
					$fecha.val(entry['Datos'][0]['fecha']);
					$id_cliente.val(entry['Datos'][0]['cliente_id']);
					$rfc_cliente.val(entry['Datos'][0]['rfc']);
					$razon_cliente.val(entry['Datos'][0]['razon_social']);
					$dir_cliente.val(entry['Datos'][0]['direccion']);
					$serie_folio.val(entry['Datos'][0]['serie_folio']);
					$observaciones.text(entry['Datos'][0]['observaciones']);
                    $orden_compra.val(entry['Datos'][0]['orden_compra']);
					$fecha_can.val(entry['Datos'][0]['fecha_can']);
					
					if(entry['Datos'][0]['fecha_can'].trim()!=''){
						$('#forma-faccancelacion-window').find('td.td_cancelado').html('CANCELADA');
						$('#forma-faccancelacion-window').find('td.td_detalle').append('<a href="#detalle" style="font-size:13px;">Ver&nbsp;detalle</a>');
						
						//Ver detalle de la cancelacion
						$('#forma-faccancelacion-window').find('a[href=#detalle]').click(function(event){
							event.preventDefault();
							
							//Llamada a funcion
							modal_cancelar($id_factura.val(), $boton_cancelarfactura, entry['Datos'][0]['tmovid_cancel'], entry['Datos'][0]['tipo_cancel'], entry['Datos'][0]['motivo_cancel'], entry['Datos'][0]['cancelado'], $('#lienzo_recalculable').find('input[name=iu]').val());
						});//termina cancelar factura
						
					}
					
					$subtotal.val( $(this).agregar_comas(entry['Datos'][0]['subtotal']));
					$campo_ieps.val( $(this).agregar_comas(entry['Datos'][0]['monto_ieps']));
					$impuesto.val( $(this).agregar_comas( entry['Datos'][0]['impuesto']) );
					$impuesto_retenido.val( $(this).agregar_comas(entry['Datos'][0]['monto_retencion']));
					$total.val($(this).agregar_comas( entry['Datos'][0]['total']));
					
					var sumaIeps = entry['Datos'][0]['monto_ieps'];
					var impuestoRetenido = entry['Datos'][0]['monto_retencion'];
					
					//Ocultar campos si tienen valor menor o igual a cero
					if(parseFloat(sumaIeps)<=0){
						$('#forma-faccancelacion-window').find('#tr_ieps').hide();
					}
					if(parseFloat(impuestoRetenido)<=0){
						$('#forma-faccancelacion-window').find('#tr_retencion').hide();
					}
					
					if(parseFloat(sumaIeps)>0 && parseFloat(impuestoRetenido)<=0){
						$('#forma-faccancelacion-window').find('.faccancelacion_div_one').css({'height':'525px'});
					}
					
					if(parseFloat(sumaIeps)<=0 && parseFloat(impuestoRetenido)>0){
						$('#forma-faccancelacion-window').find('.faccancelacion_div_one').css({'height':'525px'});
					}
					
					if(parseFloat(sumaIeps)<=0 && parseFloat(impuestoRetenido)<=0){
						$('#forma-faccancelacion-window').find('.faccancelacion_div_one').css({'height':'500px'});
					}
					
					if(parseFloat(sumaIeps)>0 && parseFloat(impuestoRetenido)>0){
						$('#forma-faccancelacion-window').find('.faccancelacion_div_one').css({'height':'550px'});
					}
					
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['Datos'][0]['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
							$moneda_original.val(moneda['id']);
						}else{
							moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					$id_impuesto.val(entry['iva'][0]['id_impuesto']);
					$valor_impuesto.val(entry['iva'][0]['valor_impuesto']);
					
					//carga select de vendedores
					$select_vendedor.children().remove();
					var hmtl_vendedor;
					$.each(entry['Vendedores'],function(entryIndex,vendedor){
						if(entry['Datos'][0]['cxc_agen_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_vendedor'] + '</option>';
						}else{
							hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_vendedor'] + '</option>';
						}
					});
					$select_vendedor.append(hmtl_vendedor);
					
					
					//carga select de condiciones
					$select_condiciones.children().remove();
					var hmtl_condiciones;
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['Datos'][0]['terminos_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
						}
					});
					$select_condiciones.append(hmtl_condiciones);
					
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo;
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(entry['Datos'][0]['fac_metodos_pago_id'] == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '"  selected="yes">' + metodo['titulo'] + '</option>';
						}else{
							hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					
					
					if(parseInt(entry['datosGrid'].length) > 0 ){
						$.each(entry['datosGrid'],function(entryIndex,prod){
							
							//obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
									trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
									trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
									//trr += '<span id="elimina">1</span>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['inv_prod_id'] +'">';
									trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ prod['codigo_producto'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
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
								trr += '<INPUT TYPE="text" 	name="cantidad" value="'+  prod['cantidad'] +'" 		id="cant" style="width:76px;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" style="width:86px; text-align:right;">';
								trr += '<INPUT type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 	id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
								trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+parseFloat(prod['importe']) * parseFloat(prod['tasa_iva'])+'">';
							trr += '</td>';
							
							var tasaIeps="";
							var importeIeps="";
							
							if(parseInt(prod['id_ieps'])>0){
								tasaIeps=prod['tasa_ieps'];
								importeIeps=prod['importe_ieps'];
							}
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="50">';
								trr += '<input type="hidden" name="idIeps"     value="'+ prod['id_ieps'] +'" id="idIeps">';
								trr += '<input type="text" name="tasaIeps" value="'+ tasaIeps +'" class="borde_oculto" id="tasaIeps" style="width:46px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="64">';
								trr += '<input type="text" name="importeIeps" value="'+ importeIeps +'" class="borde_oculto" id="importeIeps" style="width:60px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '</tr>';
							$grid_productos.append(trr);
                            
						});
					}
					
					//$calcula_totales();//llamada a la funcion que calcula totales 
					$tipo_cambio.val(entry['Datos'][0]['tipo_cambio']);
					
					$select_moneda.attr('disabled','-1'); //deshabilitar
					$select_vendedor.attr('disabled','-1'); //deshabilitar
					$select_condiciones.attr('disabled','-1'); //deshabilitar
					$select_metodo_pago.attr('disabled','-1'); //deshabilitar
					$grid_productos.find('a').hide();//ocultar
					$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
					$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
					
					$boton_cancelarfactura.show();
					//ocultar boton actualizar porque ya esta facturado, ya no se puede guardar cambios
					$submit_actualizar.hide();
					
					//si el estado del comprobante es 0, esta cancelado
					if(entry['Datos'][0]['estado']=='CANCELADO'){
						$boton_cancelarfactura.hide();
					}
					
					//Cancelar factura
					$boton_cancelarfactura.click(function(event){
						event.preventDefault();
						
						//Llamada a funcion
						modal_cancelar($id_factura.val(), $boton_cancelarfactura, entry['Datos'][0]['tmovid_cancel'], entry['Datos'][0]['tipo_cancel'], entry['Datos'][0]['motivo_cancel'],entry['Datos'][0]['cancelado'], $('#lienzo_recalculable').find('input[name=iu]').val());
					});//termina cancelar factura
					
				});//termina llamada json
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-faccancelacion-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-faccancelacion-overlay').fadeOut(remove);
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
            $.fn.tablaOrdenableEdit(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formafaccancelacion00_for_datagrid00Edit);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



