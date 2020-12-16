$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Balance General' ,                 
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
			return this.contextpath + "/controllers/ctbrepbalancegnl";
			//  return this.controller;
		}
	};
	
	//------------------------------------------------------------------
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
	//------------------------------------------------------------------
	
	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
	
	$('#barra_acciones').hide();
	
	//barra para el buscador 
	$('#barra_buscador').hide();
	
	var $fecha_corte = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_corte]');
	var $select_sucursal = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_sucursal]');
	
	var $genera_PDF = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=PDF]');
	var $busqueda_reporte= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_rep= $('#lienzo_recalculable').find('#div_rep');
	
	var $div_busqueda= $('#lienzo_recalculable').find('#div_busqueda');
	
	var arreglo_parametros = { iu:config.getUi() };
	var restful_json_service = config.getUrlForGetAndPost() + '/getDatos.json';
	$.post(restful_json_service,arreglo_parametros,function(entry){

		//cargar select de sucursales
		$select_sucursal.children().remove();
		var html_suc = '<option value="0">Todos</option>';
		$.each(entry['Suc'],function(entryIndex,suc){
			html_suc += '<option value="' + suc['id'] + '"  >' + suc['titulo'] + '</option>';
		});
		$select_sucursal.append(html_suc);
		
		$fecha_corte.val(entry['fecha']);
		
	});
	
	$fecha_corte.click(function (s){
	var a=$('div.datepicker');
		a.css({'z-index':100});
	});
	
	
	
	$fecha_corte.DatePicker({
		format:'Y-m-d',
		date: $fecha_corte.val(),
		current: $fecha_corte.val(),
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
			$fecha_corte.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($fecha_corte.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_corte.val(mostrarFecha());
				}else{
					$fecha_corte.DatePickerHide();	
				}
			}
		}
	});
		


	
	//Crear y descargar PDF de Reporte de Balance General
	$genera_PDF.click(function(event){
		event.preventDefault();
		
		if($fecha_corte.val().trim()!=''){
			var cadena = $select_sucursal.val()+"___"+$fecha_corte.val();
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var input_json = config.getUrlForGetAndPost() + '/getPdfRepBalanceGeneral/'+cadena+'/'+iu+'/out.json';
			window.location.href=input_json;
		}else{
			jAlert('Es necesario seleccionar la Fecha.', 'Atencion!', function(r) {
				$fecha_corte.focus();
			});
		}
	});//termina llamada json
	
	
	
	$busqueda_reporte.click(function(event){
		event.preventDefault();
		$div_rep.children().remove();
		
		if($fecha_corte.val().trim()!=''){
			var arreglo_parametros = {	suc: $select_sucursal.val(), fecha: $fecha_corte.val(), iu:config.getUi() };
			var restful_json_service = config.getUrlForGetAndPost() + '/getDatosReporte.json'
			var proveedoor="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				var body_tabla = entry['Data'];
				var header_tabla = {
					descripcion	:'Cuenta',
					saldo_final	:'Saldo'
				};
				
				var html_reporte = '<table id="table_rep">';
				var html_fila_vacia='';
				var html_footer = '';
				
				html_reporte +='<thead> <tr>';
				for(var key in header_tabla){
					var attrValue = header_tabla[key];
					if(attrValue == "Cuenta"){
						html_reporte +='<td width="485px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == 'Saldo'){
						html_reporte +='<td width="150px" align="left" >'+attrValue+'</td>'; 
					}
				}
				html_reporte +='</tr> </thead>';
				
				html_fila_vacia +='<tr class="first">';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="485px" height="10"></td>';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="150px"></td>';
				html_fila_vacia +='</tr>';
				
				var id_html = '';
				if(parseInt(body_tabla.length)>0){
					for(var i=0; i<body_tabla.length; i++){
						id_html = '';
						if(parseInt(body_tabla[i]["tipo_reg"])!=3){
							id_html='id="tr_totales"';
						}
						
						html_reporte +='<tr '+id_html+'>';
						html_reporte +='<td align="left">'+body_tabla[i]["descripcion"]+'</td>';
						html_reporte +='<td align="right">'+ ((body_tabla[i]["saldo_fin"].trim()=='')? '':$(this).agregar_comas(body_tabla[i]["saldo_fin"])) +'</td>';
						html_reporte +='</tr>';
					}
				}
				
				
				/*
				html_reporte +='<tfoot>';
					html_reporte += html_footer;
				html_reporte +='</tfoot>';
				*/
				
				
				html_reporte += '</table>';
				
				
				$div_rep.append(html_reporte); 
				var height2 = $('#cuerpo').css('height');
				var alto = 0;
				//alto = parseInt(height2)-282;
				alto = parseInt(height2)-240;
				
				var pix_alto=alto+'px';
				$('#table_rep').tableScroll({height:parseInt(pix_alto)});
			});
		}else{
			jAlert('Es necesario seleccionar la Fecha.', 'Atencion!', function(r) {
				$fecha_corte.focus();
			});
		}
	});
	
	
	
	
	
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_corte, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_sucursal, $busqueda_reporte);
	
	$fecha_corte.focus();
});
