$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de BackOrder' ,                 
		contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),
		
		userName : $('#lienzo_recalculable').find('input[name=user]').val(),
		ui : $('#lienzo_recalculable').find('input[name=iu]').val(),
		
		getUrlForGetAndPost : function(){
			var url = document.location.protocol + '//' + document.location.host + this.getController();
			return url;
		},
		
		getEmp: function(){
			return this.empresa;
		},
		
		getSuc: function(){
			return this.sucursal;
		},
		
		getUserName: function(){
			return this.userName;
		},
		
		getUi: function(){
			return this.ui;
		},
		getTituloApp: function(){
			return this.tituloApp;
		},

		getController: function(){
			return this.contextpath + "/controllers/comrepbackorder";
			//  return this.controller;
		}
	};
	
	//desencadena evento del $campo_ejecutar al pulsar Enter en $campo
	$aplicar_evento_keypress = function($campo, $campo_ejecutar){
		$campo.keypress(function(e){
			if(e.which == 13){
				$campo_ejecutar.trigger('click');
				return false;
			}
		});
	}
	
	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
	
	$('#barra_acciones').hide();
	//barra para el buscador 
	$('#barra_buscador').hide();
	
	var $div_reporte= $('#lienzo_recalculable').find('#div_reporte');
	var $select_opciones = $('#lienzo_recalculable').find('select[name=opciones]');
	var $no_oc = $('#lienzo_recalculable').find('input[name=no_oc]');
	var $codigo = $('#lienzo_recalculable').find('input[name=codigo]');
	var $descripcion = $('#lienzo_recalculable').find('input[name=descripcion]');
	var $proveedor = $('#lienzo_recalculable').find('input[name=proveedor]');
	var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
	
	var $genera_pdf = $('#lienzo_recalculable').find('#genera_reporte');
	var $buscar = $('#lienzo_recalculable').find('#boton_buscador');
	
	$select_opciones.children().remove();
	var almacen_hmtl = '<option value="1" selected="yes">Orden de Compra</option>';
		almacen_hmtl += '<option value="2">Producto</option>';
		almacen_hmtl += '<option value="3">Proveedor</option>'
	$select_opciones.append(almacen_hmtl);
	
	
	$genera_pdf.hide();
	
	

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

	var fecha_actual = mostrarFecha();
	$fecha_inicial.val(fecha_actual.split('-')[0]+"-"+fecha_actual.split('-')[1]+"-01");
	$fecha_final.val(fecha_actual);
	
	
	$fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});

	$fecha_inicial.DatePicker({
		format:'Y-m-d',
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
				var valida_fecha=mayor($fecha_inicial.val(),mostrarFecha());

				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_inicial.val(mostrarFecha());
				}else{
					$fecha_inicial.DatePickerHide();
				}
			}
		}
	});

	$fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});

	$fecha_final.DatePicker({
		format:'Y-m-d',
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
				var valida_fecha=mayor($fecha_final.val(),mostrarFecha());

				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_final.val(mostrarFecha());
				}else{
					$fecha_final.DatePickerHide();
				}
			}
		}
	});
	
	
	
	
	
	
	
	/*
	$genera_pdf.click(function(event){
		event.preventDefault();
		
		var codigo='';
		var descripcion='';
		var lote_interno='';
		
		if($codigo_producto.val()==''){
			codigo = '0';
		}else{
			codigo = $codigo_producto.val();
		}
		
		if($descripcion.val()==''){
			descripcion = '0';
		}else{
			descripcion = $descripcion.val();
		}
		
		if($lote_interno.val()==''){
			lote_interno = '0';
		}else{
			lote_interno = $lote_interno.val();
		}
		
		var busqueda = $select_opciones.val() +"___"+ $select_almacen.val() +"___"+ codigo +"___"+ descripcion + "___"+lote_interno;
		
		var input_json = config.getUrlForGetAndPost() + '/getReportedatastencias/'+busqueda+'/'+config.getUi()+'/out.json';
		if(parseInt($select_almacen.val()) > 0){
			window.location.href=input_json;
		}else{
			alert("Selecciona un Almacen.");
		}
	});
	*/
	
	
	
	

	$buscar.click(function(event){
		
		var primero=0;
		
		$div_reporte.children().remove();
		
		var input_json = config.getUrlForGetAndPost()+'/getBackorder.json';
		$arreglo = {
					'tipo':$select_opciones.val(), 
					'oc':$no_oc.val(), 
					'codigo':$codigo.val(), 
					'descripcion':$descripcion.val(),
					'proveedor':$proveedor.val(),
					'finicial':$fecha_inicial.val(),
					'ffinal':$fecha_final.val(),
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
		if($fecha_inicial.val()!='' && $fecha_final.val()!=''){
			
			$.post(input_json,$arreglo,function(entry){
				
				if(parseInt(entry['Datos'].length) > 0 ){
					
					var height2 = $('#cuerpo').css('height');
					var alto = parseInt(height2)-270;
					var pix_alto=alto+'px';
					
					var html_reporte = '<table class="table_main" width="1430">';
					html_reporte +='<thead><tr>';
					html_reporte +='<td class="grid_head" width="90">Orden&nbsp;Compra</td>';
					html_reporte +='<td class="grid_head" width="90">Fecha</td>';
					html_reporte +='<td class="grid_head" width="320">Nombre&nbsp;del&nbsp;Proveedor</td>';
					html_reporte +='<td class="grid_head" width="120">C&oacute;digo&nbsp;Producto</td>';
					html_reporte +='<td class="grid_head" width="300">Descripci&oacute;n&nbsp;del&nbsp;Producto</td>';
					html_reporte +='<td class="grid_head" width="100">Unidad</td>';
					html_reporte +='<td class="grid_head" width="100">Cantidad</td>';
					html_reporte +='<td class="grid_head" width="100">Cant.&nbsp;Recibida</td>';
					html_reporte +='<td class="grid_head" width="105">Cant.&nbsp;Pendiente</td>';
					html_reporte +='<td class="grid_head" width="105">Estado</td>';
					html_reporte +='</tr></thead>';
					html_reporte +='<tbody>';
					html_reporte +='<tr>';
					html_reporte +='<td colspan="10">';
					html_reporte +='<div id="reporte" style="background-color:#ffffff; overflow:scroll; overflow-x:hidden; overflow-y:auto; height:'+pix_alto+'; width=1430px; align=top; border:1px solid #ccc;">';
					html_reporte +='<table class="table_reporte" width="1415">';
					$.each(entry['Datos'],function(entryIndex,data){
						html_reporte +='<tr>';
						html_reporte +='<td class="grid_datos" width="90" align="left">'+data["no_oc"]+'</td>';
						html_reporte +='<td class="grid_datos" width="90" align="left">'+data["fecha_oc"]+'</td>';
						html_reporte +='<td class="grid_datos" width="320" align="left">'+data["proveedor"]+'</td>';  
						html_reporte +='<td class="grid_datos" width="120" align="left">'+data["codigo"]+'</td>';
						html_reporte +='<td class="grid_datos" width="300" align="left">'+data["descripcion"]+'</td>';
						html_reporte +='<td class="grid_datos" width="100" align="left">'+data["unidad"]+'</td>';
						html_reporte +='<td class="grid_datos" width="100" align="right">'+$(this).agregar_comas(parseFloat(data["cantidad"]).toFixed(2))+'</td>';
						html_reporte +='<td class="grid_datos" width="100" align="right">'+$(this).agregar_comas(parseFloat(data["cant_rec"]).toFixed(2))+'</td>';
						html_reporte +='<td class="grid_datos" width="105" align="right">'+$(this).agregar_comas(parseFloat(data["cant_pen"]).toFixed(2))+'</td>';
						html_reporte +='<td class="grid_datos" width="90" align="left">'+data["pstatus"]+'</td>';
						html_reporte +='</tr>';
					});
					html_reporte +='</table>';
					html_reporte +='</div>';
					html_reporte +='</td>';
					html_reporte +='</tr>';
					html_reporte +='</tbody>';
					html_reporte += '</table>';
					
					$div_reporte.append(html_reporte);
					
					$('#div_reporte').css('height:'+pix_alto+'px');
                                
				}else{
					jAlert('No hay datos para mostrar.', 'Atencion!', function(r) { 
						$no_oc.focus(); 
					});
				}
				
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-240;
				var pix_alto=alto+'px';
				
				$('#table_datas').tableScroll({height:parseInt(pix_alto)});
			});//termina llamada json
		}else{
			jAlert("Es necesario definir las dos fechas para el periodo de busqueda.",'! Atencion');
		}
	});
	
	
	

	
	$aplicar_evento_keypress($select_opciones, $buscar);
	$aplicar_evento_keypress($no_oc, $buscar);
	$aplicar_evento_keypress($codigo, $buscar);
	$aplicar_evento_keypress($descripcion, $buscar);
	$aplicar_evento_keypress($proveedor, $buscar);
	$aplicar_evento_keypress($fecha_inicial, $buscar);
	$aplicar_evento_keypress($fecha_final, $buscar);
	$no_oc.focus();
    
});



