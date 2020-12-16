$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de IEPS Cobrado por Cliente' ,                 
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
			return this.contextpath + "/controllers/cxcrepiepsxcliente";
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
	var $ciente = $('#lienzo_recalculable').find('input[name=ciente]');
	var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
	
	var $pdf = $('#lienzo_recalculable').find('#pdf');
	var $excel = $('#lienzo_recalculable').find('#excel');
	var $buscar = $('#lienzo_recalculable').find('#buscar');
	
	
	
	//$pdf.hide();
	
	

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
	
	
	
	
	
	
	
	
	$pdf.click(function(event){
		event.preventDefault();
		
		var busqueda = $ciente.val() +"___"+ $fecha_inicial.val() +"___"+ $fecha_final.val();
		
		var input_json = config.getUrlForGetAndPost() + '/getPdf/'+busqueda+'/'+config.getUi()+'/out.json';
		if($fecha_inicial.val()!='' && $fecha_final.val()!=''){
			window.location.href=input_json;
		}else{
			jAlert("Es necesario definir las dos fechas para el periodo de busqueda.",'! Atencion');
		}
	});
	
	
	$excel.click(function(event){
		event.preventDefault();
		
		var busqueda = $ciente.val() +"___"+ $fecha_inicial.val() +"___"+ $fecha_final.val();
		
		var input_json = config.getUrlForGetAndPost() + '/getXls/'+busqueda+'/'+config.getUi()+'/out.json';
		if($fecha_inicial.val()!='' && $fecha_final.val()!=''){
			window.location.href=input_json;
		}else{
			jAlert("Es necesario definir las dos fechas para el periodo de busqueda.",'! Atencion');
		}
	});
	
	
	
	

	$buscar.click(function(event){
		var primero=0;
		$div_reporte.children().remove();
		
		//Modificar el ancho del div donde se muestra el reporte de acuerdo al ancho de la pantalla
		$div_reporte.css("width", (parseInt($('#cuerpo').css('width'))-20)+'px');
		
		var input_json = config.getUrlForGetAndPost()+'/getDatos.json';
		$arreglo = {'ciente':$ciente.val(), 'finicial':$fecha_inicial.val(), 'ffinal':$fecha_final.val(), 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()};
		
		if($fecha_inicial.val()!='' && $fecha_final.val()!=''){
			
			$.post(input_json,$arreglo,function(entry){
				
				if(parseInt(entry['Datos'].length) > 0 ){
					
					var height2 = $('#cuerpo').css('height');
					var alto = parseInt(height2)-280;
					var pix_alto=alto+'px';
					
					var html_reporte = '<table class="table_main" width="'+entry['Conf']['widthMainTable']+'">';
					html_reporte +='<thead><tr>';
					
					$.each(entry['Conf']['ColumnsHead'],function(entryIndexCol,col){
						html_reporte +='<td class="grid_head" width="'+col.split(':')[1]+'" align="'+col.split(':')[2]+'">'+col.split(':')[0]+'</td>';
						//alert(entryIndex);
					});
					html_reporte +='</tr></thead>';
					html_reporte +='<tbody>';
					html_reporte +='<tr>';
					html_reporte +='<td colspan="'+entry['Conf']['colspanMainTable']+'">';
					html_reporte +='<div id="reporte" style="background-color:#ffffff; overflow:scroll; overflow-x:hidden; overflow-y:auto; height:'+pix_alto+'; width='+entry['Conf']['widthMainTable']+'px; align=top; border:1px solid #ccc;">';
						html_reporte +='<table class="table_reporte" width="'+entry['Conf']['widthReportTable']+'">';
						
						$.each(entry['Datos'],function(entryIndex,data){
							html_reporte +='<tr>';
							$.each(entry['Conf']['ColumnsBody'],function(entryIndexCol,col){
								html_reporte +='<td class="grid_datos" width="'+col.split(':')[0]+'" align="'+col.split(':')[1]+'">'+data[entryIndexCol]+'</td>';
							});
							html_reporte +='</tr>';
						});
						
						html_reporte +='</table>';
					html_reporte +='</div>';
					html_reporte +='</td>';
					html_reporte +='</tr>';
					html_reporte +='</tbody>';
					html_reporte +='<tfoot><tr>';
					
					$.each(entry['Conf']['ColumnsBody'],function(entryIndexCol,col){
						html_reporte +='<td class="grid_foot" width="'+col.split(':')[0]+'" align="'+col.split(':')[1]+'">'+ ((entry['Totales'][entryIndexCol]==undefined)?'':entry['Totales'][entryIndexCol]) +'</td>';
						//alert(entryIndex);
					});
					
					html_reporte +='</tr>';
					
					html_reporte +='<tr>';
					html_reporte +='<td class="grid_foot" colspan="'+(parseInt(entry['Conf']['colspanMainTable'])-1)+'" align="right">TOTAL IEPS</td>';
					html_reporte +='<td class="grid_foot" align="right">'+entry['Totales']['sumaIeps']+'</td>';
					html_reporte +='</tr></tfoot>';
					html_reporte += '</table>';
					
					$div_reporte.append(html_reporte);
					
					$('#div_reporte').css('height:'+pix_alto+'px');
					$('#div_reporte').css('width:'+entry['Conf']['widthMainTable']+'px');
                    
				}else{
					jAlert('No hay datos para mostrar.', 'Atencion!', function(r) { 
						$ciente.focus(); 
					});
				}
				
				/*
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-240;
				var pix_alto=alto+'px';
				
				$('#table_datas').tableScroll({height:parseInt(pix_alto)});
				*/
			});//termina llamada json
		}else{
			jAlert("Es necesario definir las dos fechas para el periodo de busqueda.",'! Atencion');
		}
	});
	
	
	$aplicar_evento_keypress($ciente, $buscar);
	$aplicar_evento_keypress($fecha_inicial, $buscar);
	$aplicar_evento_keypress($fecha_final, $buscar);
	$ciente.focus();
    
});



