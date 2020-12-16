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
	var controller = $contextpath.val()+"/controllers/facconsultas";
    
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
	$('#barra_titulo').find('#td_titulo').append('Consulta de Facturas');
	
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
	
	
	
	
	/*
	//descarga de xml
	$('#barra_descarga_xml').append($('#lienzo_recalculable').find('.tabla_decarga_xml'));
	var $boton_descarga_xml = $('#barra_descarga_xml').find('.tabla_decarga_xml').find('#descarga_xml');
	var $factura_xml = $('#barra_descarga_xml').find('.tabla_decarga_xml').find('input[name=factura_xml]');
	*/
	
	
	//informe mensual
	$('#barra_genera_informe').append($('#lienzo_recalculable').find('.tabla_genera_informe'));
	var $select_tipo_reporte = $('#barra_genera_informe').find('.tabla_genera_informe').find('select[name=select_tipo_reporte]');
	var $tabla_informe_mensual = $('#barra_genera_informe').find('.tabla_genera_informe').find('.tabla_informe_mensual');
	var $select_anio = $('#barra_genera_informe').find('.tabla_genera_informe').find('select[name=select_anio]');
	var $select_mes = $('#barra_genera_informe').find('.tabla_genera_informe').find('select[name=select_mes]');
	var $boton_generar_informe_txt = $('#barra_genera_informe').find('.tabla_genera_informe').find('#generar_informe_mensual');
	
	/*
	var $tabla_reporte_facturacion = $('#barra_genera_informe').find('.tabla_genera_informe').find('.tabla_reporte_facturacion');
	var $reporte_fecha_inicial = $('#barra_genera_informe').find('.tabla_genera_informe').find('input[name=fecha_inicial]');
	var $reporte_fecha_final = $('#barra_genera_informe').find('.tabla_genera_informe').find('input[name=fecha_final]');
	var $boton_generar_reporte_facconsultas = $('#barra_genera_informe').find('.tabla_genera_informe').find('#generar_reporte_facconsultas');
	*/
	//$tabla_reporte_facturacion.hide();
	
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/datos_generador_informe.json';
	$arreglo = { }
	$.post(input_json,$arreglo,function(entry){
		//carga select de años
		$select_anio.children().remove();
		var html_anio = '';
		$.each(entry['anioinforme'],function(entryIndex,anio){
			html_anio += '<option value="' + anio['valor'] + '"  >' + anio['valor'] + '</option>';
		});
		$select_anio.append(html_anio);
	});//termina llamada json
	
	
	
	
	
	//TriggerClickVisualizaDescargaXml = 0;
	TriggerClickVisualizaGeneradorInforme = 0;
	TriggerClickVisializaBuscador = 0;

	
	
	//visualizar generador de informe mensual
	$generar_informe.click(function(event){
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
			 
			 $('#barra_genera_informe').find('.tabla_genera_informe').css({'display':'block'});
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
	
	
	
	
	
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		$('#barra_genera_informe').find('.tabla_genera_informe').css({'display':'none'});
		//$('#barra_descarga_xml').find('.tabla_decarga_xml').css({'display':'none'});
		
		if(parseInt(TriggerClickVisualizaGeneradorInforme)==1){
			$generar_informe.trigger('click');
		}
		/*
		if(parseInt(TriggerClickVisualizaDescargaXml)==1){
			$descargar_xml.trigger('click');
		}
		*/
		
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
	
	/*
	//fecha inicial para reporte de facconsultas
	$reporte_fecha_inicial.val(mostrarFecha());
	$reporte_fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100,});
	});
	$reporte_fecha_inicial.DatePicker({
		format:'Y-m-d',
		onBeforeShow: function(){
			$reporte_fecha_inicial.DatePickerSetDate($reporte_fecha_inicial.val(), true);
		},
		date: $reporte_fecha_inicial.val(),
		current: $reporte_fecha_inicial.val(),
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
			$reporte_fecha_inicial.val(formated);
			if (formated.match(patron) ){
				$reporte_fecha_inicial.DatePickerHide();
			};
		}
	});
	
	//fecha final para reporte de facconsultas
	$reporte_fecha_final.val(mostrarFecha());
	$reporte_fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100,});
	});
	$reporte_fecha_final.DatePicker({
		format:'Y-m-d',
		onBeforeShow: function(){
			$reporte_fecha_final.DatePickerSetDate($reporte_fecha_final.val(), true);
		},
		date: $reporte_fecha_final.val(),
		current: $reporte_fecha_final.val(),
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
			$reporte_fecha_final.val(formated);
			if (formated.match(patron) ){
				$reporte_fecha_final.DatePickerHide();
			};
		}
	});

	$reporte_fecha_inicial.attr('readonly',true);
	$reporte_fecha_final.attr('readonly',true);
	*/
	
	//seleccionar tipo de reporte
	$select_tipo_reporte.change(function(){
		var tipo_reporte = $(this).val();
		if(parseInt(tipo_reporte)==1){
			//$tabla_reporte_facturacion.hide();
			$tabla_informe_mensual.toggle("slow");
		}else{
			$tabla_informe_mensual.hide();
			//$tabla_reporte_facturacion.toggle("slow");
		}
	});
	
	
	
	
	//generar informe mensual
	$boton_generar_informe_txt.click(function(event){
		//event.preventDefault();
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_txt_reporte_mensual_sat/'+$select_mes.val()+'/'+$select_anio.val()+'/'+ iu +'/out.json';
		window.location.href=input_json;
	});
	
	
	/*
	$boton_generar_reporte_facconsultas.click(function(event){
		//$reporte_fecha_inicial
		//$reporte_fecha_final
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_reporte_facturacion/'+$reporte_fecha_inicial.val()+'/'+$reporte_fecha_final.val()+'/'+ iu +'/out.json';
		window.location.href=input_json;
	});
	*/


        
        
        

        
        
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
            var $select_prod_tipo = $('#forma-facconsultas-window').find('select[name=prodtipo]');
            $('#forma-facconsultas-window').find('#submit').mouseover(function(){
                $('#forma-facconsultas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                //$('#forma-facconsultas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
            })
            $('#forma-facconsultas-window').find('#submit').mouseout(function(){
                $('#forma-facconsultas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                //$('#forma-facconsultas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
            })
            $('#forma-facconsultas-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-facconsultas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-facconsultas-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-facconsultas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-facconsultas-window').find('#close').mouseover(function(){
                $('#forma-facconsultas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-facconsultas-window').find('#close').mouseout(function(){
                $('#forma-facconsultas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-facconsultas-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-facconsultas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-facconsultas-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-facconsultas-window').find("ul.pestanas li").click(function() {
                $('#forma-facconsultas-window').find(".contenidoPes").hide();
                $('#forma-facconsultas-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-facconsultas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
    
    //convertir costos en dolar y pesos
	$convertir_costos = function($tipo_cambio,moneda_id,$campo_subtotal,$campo_impuesto,$campo_total,$valor_impuesto,$grid_productos){
		var sumaSubTotal = 0; //es la suma de todos los importes
		var sumaImpuesto = 0; //valor del iva
		var sumaTotal = 0; //suma del subtotal + totalImpuesto
		var $moneda_original = $('#forma-facconsultas-window').find('input[name=moneda_original]');
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
		var $campo_subtotal = $('#forma-facconsultas-window').find('input[name=subtotal]');
		var $campo_impuesto = $('#forma-facconsultas-window').find('input[name=impuesto]');
		var $campo_total = $('#forma-facconsultas-window').find('input[name=total]');
		//var $campo_tc = $('#forma-facconsultas-window').find('input[name=tc]');
		var $valor_impuesto = $('#forma-facconsultas-window').find('input[name=valorimpuesto]');
		var $grid_productos = $('#forma-facconsultas-window').find('#grid_productos');
		
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
	
	
	
	

	//ver detalles de una factura
	var carga_formafacconsultas00_for_datagrid00Edit = function(id_to_show, accion_mode){
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
			$('#forma-facconsultas-window').remove();
			$('#forma-facconsultas-overlay').remove();
            
			var form_to_show = 'formafacconsultas00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_facconsultas();
			
			$('#forma-facconsultas-window').css({"margin-left": -390, 	"margin-top": -290});
			
			$forma_selected.prependTo('#forma-facconsultas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFactura.json';
				$arreglo = {'id_factura':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
                                
				var $total_tr = $('#forma-facconsultas-window').find('input[name=total_tr]');
				var $id_factura = $('#forma-facconsultas-window').find('input[name=id_factura]');
				var $folio_pedido = $('#forma-facconsultas-window').find('input[name=folio_pedido]');
				var $busca_cliente = $('#forma-facconsultas-window').find('a[href*=busca_cliente]');
				var $id_cliente = $('#forma-facconsultas-window').find('input[name=id_cliente]');
				var $rfc_cliente = $('#forma-facconsultas-window').find('input[name=rfccliente]');
				var $razon_cliente = $('#forma-facconsultas-window').find('input[name=razoncliente]');
				var $dir_cliente = $('#forma-facconsultas-window').find('input[name=dircliente]');
				
				
				var $serie_folio = $('#forma-facconsultas-window').find('input[name=serie_folio]');
				var $select_moneda = $('#forma-facconsultas-window').find('select[name=moneda]');
				var $moneda_original = $('#forma-facconsultas-window').find('input[name=moneda_original]');
				var $tipo_cambio = $('#forma-facconsultas-window').find('input[name=tipo_cambio]');
				var $tipo_tipo_cambio_original = $('#forma-facconsultas-window').find('input[name=tipo_cambio_original]');
				var $orden_compra = $('#forma-facconsultas-window').find('input[name=orden_compra]');
				var	$orden_compra_original = $('#forma-facconsultas-window').find('input[name=orden_compra_original]');
				
				//var $campo_tc = $('#forma-facconsultas-window').find('input[name=tc]');
				var $id_impuesto = $('#forma-facconsultas-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-facconsultas-window').find('input[name=valorimpuesto]');
				
				var $observaciones = $('#forma-facconsultas-window').find('textarea[name=observaciones]');
				var $select_condiciones = $('#forma-facconsultas-window').find('select[name=condiciones]');
				var $select_vendedor = $('#forma-facconsultas-window').find('select[name=vendedor]');
				var $observaciones_original = $('#forma-facconsultas-window').find('textarea[name=observaciones_original]');
				var $select_condiciones_original = $('#forma-facconsultas-window').find('select[name=condiciones_original]');
				var $select_vendedor_original = $('#forma-facconsultas-window').find('select[name=vendedor_original]');
				
				var $select_metodo_pago = $('#forma-facconsultas-window').find('select[name=select_metodo_pago]');
				var $etiqueta_digit = $('#forma-facconsultas-window').find('input[name=digit]');
				var $digitos = $('#forma-facconsultas-window').find('input[name=digitos]');
				var $no_cuenta = $('#forma-facconsultas-window').find('input[name=no_cuenta]');
				
				
				//Variables para el envio del email
				var $email_envio = $('#forma-facconsultas-window').find('input[name=email_envio]');
				var $asunto_envio = $('#forma-facconsultas-window').find('input[name=asunto_envio]');
				var $msj_envio = $('#forma-facconsultas-window').find('textarea[name=msj_envio]');
				var $check_xml = $('#forma-facconsultas-window').find('input[name=check_xml]');
				var $check_pdf = $('#forma-facconsultas-window').find('input[name=check_pdf]');
				var $enviar_email = $('#forma-facconsultas-window').find('#enviar_email');
				
				
				var $reconstruir_pdf = $('#forma-facconsultas-window').find('#reconstruir_pdf');
				var $boton_descargarpdf = $('#forma-facconsultas-window').find('#descargarpdf');
				//var $boton_cancelarfactura = $('#forma-facconsultas-window').find('#cancelarfactura');
				var $boton_descargarxml = $('#forma-facconsultas-window').find('#descargarxml');
				
				//grid de productos
				var $grid_productos = $('#forma-facconsultas-window').find('#grid_productos');
				//grid de errores
				var $grid_warning = $('#forma-facconsultas-window').find('#div_warning_grid').find('#grid_warning');
				
				//var $flete = $('#forma-facconsultas-window').find('input[name=flete]');
				var $subtotal = $('#forma-facconsultas-window').find('input[name=subtotal]');
				var $campo_ieps = $('#forma-facconsultas-window').find('input[name=ieps]');
				var $impuesto = $('#forma-facconsultas-window').find('input[name=impuesto]');
				var $impuesto_retenido = $('#forma-facconsultas-window').find('input[name=impuesto_retenido]');
				var $total = $('#forma-facconsultas-window').find('input[name=total]');
				
				var $importe_subtotal = $('#forma-facconsultas-window').find('input[name=importe_subtotal]');
				var $monto_descuento = $('#forma-facconsultas-window').find('input[name=monto_descuento]');
				var $etiqueta_motivo_descto = $('#forma-facconsultas-window').find('input[name=etiqueta_motivo_descto]');
				var $motivo_descuento = $('#forma-facconsultas-window').find('input[name=motivo_descuento]');
				
				var $pestana_addenda = $('#forma-facconsultas-window').find('ul.pestanas').find('a[href=#tabx-2]');
				
				var $cerrar_plugin = $('#forma-facconsultas-window').find('#close');
				var $cancelar_plugin = $('#forma-facconsultas-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-facconsultas-window').find('#submit');
				
				
				
				
				//Campos para addendas
				$pestana_addenda.parent().hide();
				$('#forma-facconsultas-window').find('#adenda1').hide();
				$('#forma-facconsultas-window').find('#adenda2').hide();
				$('#forma-facconsultas-window').find('#adenda3').hide();
				var $boton_agregar_addenda = $('#forma-facconsultas-window').find('#agregar_addenda');
				$boton_agregar_addenda.hide();
				//Termina campos para pestaña addendas
				
				
				$etiqueta_motivo_descto.hide();
				$motivo_descuento.hide();
				//ocultar boton descargar y facturar. Despues de facturar debe mostrarse
				//$reconstruir_pdf.hide();
				$boton_descargarpdf.hide();
				//$boton_cancelarfactura.hide();
				$submit_actualizar.hide();
				
				
				$digitos.attr('disabled','-1');
				$etiqueta_digit.attr('disabled','-1');
				$no_cuenta.hide();
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-facconsultas-window').find('div.interrogacion').css({'display':'none'});
						jAlert("La prefactura se guard&oacute; con &eacute;xito", 'Atencion!');
						/*
						var remove = function() {$(this).remove();};
						$('#forma-facconsultas-overlay').fadeOut(remove);
						*/
						//ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
						$submit_actualizar.hide();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-facconsultas-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-facconsultas-window').find('.facconsultas_div_one').css({'height':'575px'});//con errores
						$('#forma-facconsultas-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-facconsultas-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-facconsultas-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');

							if( longitud.length > 1 ){
								$('#forma-facconsultas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

								//alert(tmp.split(':')[0]);

								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
											$('#forma-facconsultas-window').find('.facconsultas_div_one').css({'height':'575px'});
											$('#forma-facconsultas-window').find('#div_warning_grid').css({'display':'block'});
											
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
					
					//alert(entry['Addenda']);
					
					if(entry['Addenda']){
						$pestana_addenda.parent().show();
						
						//alert(entry['datosFactura'][0]['adenda_id']);
						
						if(parseInt(entry['datosFactura'][0]['t_adenda_id'])==1){
							$('#forma-facconsultas-window').find('#adenda1').show();
						}
						
						if(parseInt(entry['datosFactura'][0]['t_adenda_id'])==2){
							$('#forma-facconsultas-window').find('#adenda2').show();
						}
						
						if(parseInt(entry['datosFactura'][0]['t_adenda_id'])==3){
							$('#forma-facconsultas-window').find('#adenda3').show();
							
							$('#forma-facconsultas-window').find('input[name=adenda3_campo1]').val(entry['datosAdenda'][0]['valor1']);
							$('#forma-facconsultas-window').find('input[name=adenda3_campo2]').val(entry['datosAdenda'][0]['valor2']);
							$('#forma-facconsultas-window').find('input[name=adenda3_campo3]').val(entry['datosAdenda'][0]['valor3']);
							$('#forma-facconsultas-window').find('input[name=adenda3_campo4]').val(entry['datosAdenda'][0]['valor4']);
							$('#forma-facconsultas-window').find('input[name=adenda3_campo5]').val(entry['datosAdenda'][0]['valor5']);
							$('#forma-facconsultas-window').find('input[name=adenda3_campo6]').val(entry['datosAdenda'][0]['valor6']);							
						}
						
						if(entry['datosAdenda'][0]['generado']=='false'){
							$boton_agregar_addenda.show();
						}
						
						
						//Este boton ejecuta el submit para agregar addenda al xml de la factura.
						$boton_agregar_addenda.click(function(event){
							var cadena_datos = '';
							
							if(parseInt(entry['datosFactura'][0]['t_adenda_id'])==3){
								var email_emisor = $('#forma-facconsultas-window').find('input[name=adenda3_campo2]').val();
								var oc = $('#forma-facconsultas-window').find('input[name=adenda3_campo1]').val();
								var moneda = $('#forma-facconsultas-window').find('input[name=adenda3_campo3]').val();
								var tc = $('#forma-facconsultas-window').find('input[name=adenda3_campo4]').val();
								var subtotal = $('#forma-facconsultas-window').find('input[name=adenda3_campo5]').val();
								var total = $('#forma-facconsultas-window').find('input[name=adenda3_campo6]').val();
								
								email_emisor = (email_emisor.trim()!='')? email_emisor.trim():'___'; 
								oc = (oc.trim()!='')? oc.trim():'___'; 
								moneda = (moneda.trim()!='')? moneda.trim():'___'; 
								tc = (tc.trim()!='')? tc.trim():'___'; 
								subtotal = (subtotal.trim()!='')? subtotal.trim():'___'; 
								total = (total.trim()!='')? total.trim():'___'; 
								
								cadena_datos = oc +"|"+ email_emisor +"|"+ moneda +"|"+ tc +"|"+ subtotal +"|"+ total;
							}
							
							//alert(cadena_datos);
							
							var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAddAddenda.json';
							$arreglo2 = {'id_fac':$id_factura.val(), 't_addenda_id':entry['datosFactura'][0]['t_adenda_id'], 'cadena_datos':cadena_datos,  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
							
							$.post(input_json2,$arreglo2,function(entry2){
								
									jAlert(entry2['msj'], 'Atencion!', function(r) { 
										//$dest_no.focus(); 
									});
									
									if( entry2['generado']=='true' ){
										//var dest_id = entry2['Dest'][0]['id'];
										$boton_agregar_addenda.hide();
										
										$('#forma-facconsultas-window').find('input[name=adenda3_campo1]').attr("readonly", true);
										$('#forma-facconsultas-window').find('input[name=adenda3_campo2]').attr("readonly", true);
										$('#forma-facconsultas-window').find('input[name=adenda3_campo3]').attr("readonly", true);
										$('#forma-facconsultas-window').find('input[name=adenda3_campo4]').attr("readonly", true);
										$('#forma-facconsultas-window').find('input[name=adenda3_campo5]').attr("readonly", true);
										$('#forma-facconsultas-window').find('input[name=adenda3_campo6]').attr("readonly", true);
									}
							},"json");//termina llamada json

						});						
						
						
					}
					
					
					
					$id_factura.val(entry['datosFactura'][0]['id']);
					$folio_pedido.val(entry['datosFactura'][0]['folio_pedido']);
					$id_cliente.val(entry['datosFactura'][0]['cliente_id']);
					$rfc_cliente.val(entry['datosFactura'][0]['rfc']);
					$razon_cliente.val(entry['datosFactura'][0]['razon_social']);
					$dir_cliente.val(entry['datosFactura'][0]['direccion']);
					$serie_folio.val(entry['datosFactura'][0]['serie_folio']);
					$observaciones.text(entry['datosFactura'][0]['observaciones']);
					$observaciones_original.val(entry['datosFactura'][0]['observaciones']);
                    $orden_compra.val(entry['datosFactura'][0]['orden_compra']);
                    $orden_compra_original.val(entry['datosFactura'][0]['orden_compra']);
					$digitos.val(entry['datosFactura'][0]['no_tarjeta']);
					
					$subtotal.val( $(this).agregar_comas(entry['datosFactura'][0]['subtotal']));
					$campo_ieps.val( $(this).agregar_comas(entry['datosFactura'][0]['monto_ieps']));
					$impuesto.val( $(this).agregar_comas( entry['datosFactura'][0]['impuesto']) );
					$impuesto_retenido.val( $(this).agregar_comas(entry['datosFactura'][0]['monto_retencion']));
					$total.val($(this).agregar_comas( entry['datosFactura'][0]['total']));
					
					$importe_subtotal.val($(this).agregar_comas(entry['datosFactura'][0]['subtotal_sin_descto']));
					$monto_descuento.val($(this).agregar_comas(entry['datosFactura'][0]['monto_descto']));
					$motivo_descuento.val(entry['datosFactura'][0]['motivo_descto']);
					
					var sumaIeps = entry['datosFactura'][0]['monto_ieps'];
					var impuestoRetenido = entry['datosFactura'][0]['monto_retencion'];
					
					
					var valorHeight=620;
					
					if(parseFloat(entry['datosFactura'][0]['monto_descto'])>0){
						$('#forma-facconsultas-window').find('#tr_importe_subtotal').show();
						$('#forma-facconsultas-window').find('#tr_descto').show();
						$('#forma-facconsultas-window').find('input[name=etiqueta_motivo_descto]').show();
						$('#forma-facconsultas-window').find('input[name=motivo_descuento]').show();
						valorHeight = parseFloat(valorHeight) + 30;
					}else{
						$('#forma-facconsultas-window').find('#tr_importe_subtotal').hide();
						$('#forma-facconsultas-window').find('#tr_descto').hide();
						$('#forma-facconsultas-window').find('input[name=etiqueta_motivo_descto]').hide();
						$('#forma-facconsultas-window').find('input[name=motivo_descuento]').hide();
					}
					
					//Ocultar campos si tienen valor menor o igual a cero
					if(parseFloat(sumaIeps)<=0){
						$('#forma-facconsultas-window').find('#tr_ieps').hide();
					}else{
						$('#forma-facconsultas-window').find('#tr_ieps').show();
						valorHeight = parseFloat(valorHeight) + 15;
					}
					
					if(parseFloat(impuestoRetenido)<=0){
						$('#forma-facconsultas-window').find('#tr_retencion').hide();
					}else{
						$('#forma-facconsultas-window').find('#tr_retencion').show();
						valorHeight = parseFloat(valorHeight) + 15;
					}
					
					$('#forma-facconsultas-window').find('.facconsultas_div_one').css({'height':valorHeight+'px'});
					
					
					
                    //form pago 2=Tarjeta Credito, 3=Tarjeta Debito
                    if(parseInt(entry['datosFactura'][0]['fac_metodos_pago_id'])==2 || parseInt(entry['datosFactura'][0]['fac_metodos_pago_id']==3)){
						$no_cuenta.hide();
						$digitos.show();
						$digitos.val(entry['datosFactura'][0]['no_cuenta']);
					}
                    
                    //form pago 4=Cheque Nominativo, 5=Transferencia Electronica de Fondos
                    if(parseInt(entry['datosFactura'][0]['fac_metodos_pago_id'])==4 || parseInt(entry['datosFactura'][0]['fac_metodos_pago_id']==5)){
						$no_cuenta.show();
						$digitos.val('');
						$digitos.hide();
						
						if(parseInt(entry['datosFactura'][0]['moneda_id'])==1){
							$etiqueta_digit.val('N&uacute;mero de Cuenta para pagos en Pesos');
						}else{
							$etiqueta_digit.val('N&uacute;mero de Cuenta para pagos en Dolares');
						}
						$no_cuenta.val(entry['datosFactura'][0]['no_cuenta']);
					}
					
					
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosFactura'][0]['moneda_id']){
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
						if(entry['datosFactura'][0]['cxc_agen_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_vendedor'] + '</option>';
						}else{
							hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_vendedor'] + '</option>';
						}
					});
					$select_vendedor.append(hmtl_vendedor);
					$select_vendedor.find('option').clone().appendTo($select_vendedor_original);
					
					
					//carga select de condiciones
					$select_condiciones.children().remove();
					var hmtl_condiciones;
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['datosFactura'][0]['terminos_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
						}
					});
					$select_condiciones.append(hmtl_condiciones);
					$select_condiciones.find('option').clone().appendTo($select_condiciones_original);
					
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo;
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(entry['datosFactura'][0]['fac_metodos_pago_id'] == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '"  selected="yes">' + metodo['titulo'] + '</option>';
						}else{
							hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					
					
					$busca_cliente.hide();
					
					
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
					
					
					$tipo_cambio.val(entry['datosFactura'][0]['tipo_cambio']);
					$tipo_tipo_cambio_original.val(entry['datosFactura'][0]['tipo_cambio']);
					
					$rfc_cliente.attr('disabled','-1'); //deshabilitar
					$folio_pedido.attr('disabled','-1'); //deshabilitar
					$razon_cliente.attr('disabled','-1'); //deshabilitar
					$dir_cliente.attr('disabled','-1'); //deshabilitar
					$serie_folio.attr('disabled','-1'); //deshabilitar
					$observaciones.attr('disabled','-1'); //deshabilitar
					$select_moneda.attr('disabled','-1'); //deshabilitar
					$tipo_cambio.attr('disabled','-1'); //deshabilitar
					$select_vendedor.attr('disabled','-1'); //deshabilitar
					$select_condiciones.attr('disabled','-1'); //deshabilitar
					//$sku_producto.attr('disabled','-1'); //deshabilitar
					//$nombre_producto.attr('disabled','-1'); //deshabilitar
					$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
					$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
					$grid_productos.find('#cant').attr('disabled','-1'); //deshabilitar
					$grid_productos.find('#cost').attr('disabled','-1'); //deshabilitar
					$grid_productos.find('a').hide();//ocultar
					$orden_compra.attr('disabled','-1'); //deshabilitar
					//$factura_sai.attr('disabled','-1'); //deshabilitar
					
					$select_metodo_pago.attr('disabled','-1'); //deshabilitar
					$digitos.attr('disabled','-1'); //deshabilitar
					
					//$busca_sku.hide();
					//$agregar_producto.hide();
					$boton_descargarpdf.show();
					//$boton_cancelarfactura.show();
					$boton_descargarxml.show();
					//ocultar boton actualizar porque ya esta facturado, ya no se puede guardar cambios
					$submit_actualizar.hide();
					
					
					
					

					
					//si el estado del comprobante es 0, esta cancelado
					if(entry['datosFactura']['0']['estado']=='CANCELADO'){
						$reconstruir_pdf.hide();
						//$boton_descargarpdf.hide();
						$boton_descargarxml.hide();
						
						$email_envio.attr('disabled','-1');
						$asunto_envio.attr('disabled','-1');
						$msj_envio.attr('disabled','-1');
						$check_xml.attr('disabled','-1');
						$check_pdf.attr('disabled','-1');
					}else{
						var asunto = 'FACTURA '+ entry['datosFactura']['0']['serie_folio'];
						var msj = 'Envio de la factura '+ entry['datosFactura']['0']['serie_folio'] +' de '+ $('#lienzo_recalculable').find('input[name=emp]').val()
						$email_envio.val(entry['datosFactura'][0]['email']);
						$asunto_envio.val(asunto);
						$msj_envio.val(msj);
						$check_xml.attr('checked', true);
						$check_pdf.attr('checked', true);
					}
					
					
					
					
					//Descargar pdf de factura
					$boton_descargarpdf.click(function(event){
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getVerificaArchivoGenerado.json';
						$arreglo = {'serie_folio':$serie_folio.val(),'ext':'pdf', 'id':$id_factura.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
						$.post(input_json,$arreglo,function(entry2){
							var descargar  = entry2['descargar'];
							if(descargar == 'true'){
								
								var estado = entry['datosFactura']['0']['estado'];								
								var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
								var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_descargar_pdf_factura/'+$id_factura.val()+'/'+iu+'/out.json';
								window.location.href=input_json;
							}else{
								//jAlert("La factura "+$serie_folio.val()+" aun no esta disponible para descarga, intente nuevamente en 10 segundos.", 'Atencion!');
								jAlert("La factura "+$serie_folio.val()+" no esta disponible para descarga.", 'Atencion!');
							}
						});//termina llamada json
					});
					
					
				});//termina llamada json
                
                
                
                
				//Volver a generar el pdf para CFD y CFDI con Timbrado Fiscal
				$reconstruir_pdf.click(function(event){
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getReconstruirPdfFactura.json';
					$arreglo = {'id_factura':$id_factura.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
					
					$.post(input_json,$arreglo,function(entry){
						var generado  = entry['generado'];
						if(generado=='true'){
							jAlert("El PDF de la Factura "+$serie_folio.val()+" se gener&oacute; con &eacute;xito.", 'Atencion!');
						}else{
							jAlert("Error al generar el pdf.", 'Atencion!');
						}
					});//termina llamada json
				});
                
                

                
                
                
				//Descargar xml de factura
				$boton_descargarxml.click(function(event){
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getVerificaArchivoGenerado.json';
					$arreglo = {'serie_folio':$serie_folio.val(), 'ext':'xml', 'id':$id_factura.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
					
					$.post(input_json,$arreglo,function(entry){
						var descargar  = entry['descargar'];
						if(descargar == 'true'){
							var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
							var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_descargar_xml_factura/'+$id_factura.val()+'/'+iu+'/out.json';
							window.location.href=input_json;
						}else{
							//jAlert("La factura "+$serie_folio.val()+" aun no esta disponible para descarga, intente nuevamente en 10 segundos.", 'Atencion!');
							jAlert("La factura "+$serie_folio.val()+" no esta disponible para descarga.", 'Atencion!');
						}
					});//termina llamada json
				});
                
                
                
				//Enviar email
				$enviar_email.click(function(event){
					var descargar_xml="false";
					var descargar_pdf="false";
					
					if($check_xml.is(':checked')){ descargar_xml="true"; }
					if($check_pdf.is(':checked')){ descargar_pdf="true"; }
					
					if(descargar_xml=="false" && descargar_pdf=="false"){
						jAlert("Es necesario indicar los archivos a adjuntar, XML &oacute; PDF &oacute; los dos.", 'Atencion!');
					}else{
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSendMail.json';
						$arreglo = {
							'id':$id_factura.val(), 
							'correo':$email_envio.val(), 
							'asunto':$asunto_envio.val(), 
							'msj':$msj_envio.val(), 
							'xml':descargar_xml, 
							'pdf':descargar_pdf, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() 
						}
						
						$.post(input_json,$arreglo,function(entry){
							
							jAlert(entry['msj'], 'Atencion!');
							
							if(entry['valor'] == 'true'){
								$email_envio.val('');
								$asunto_envio.val('');
								$msj_envio.val('');
								$check_xml.attr('checked', false);
								$check_pdf.attr('checked', false);
							}
						});
					}
				});
                
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-facconsultas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-facconsultas-overlay').fadeOut(remove);
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
            $.fn.tablaOrdenableEdit(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formafacconsultas00_for_datagrid00Edit);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



