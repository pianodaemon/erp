$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Balanza de Comprobaci&oacute;n' ,                 
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
			return this.contextpath + "/controllers/ctbrepbalanzacomp";
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
	
	/*
	var $select_tipo_reporte = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=tipo_reporte]');
	var $select_ano = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_ano]');
	var $select_mes = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_mes]');
	*/
	var $fecha_inicial = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_final]');
	var $select_cuentas = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_cuentas]');
	var $select_cuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_cuenta]');
	var $select_subcuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_subcuenta]');
	var $select_subsubcuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_subsubcuenta]');
	var $select_subsubsubcuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_subsubsubcuenta]');
	var $select_subsubsubsubcuenta = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_subsubsubsubcuenta]');
	
	var $descripcion = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=descripcion]');
	var $select_sucursal = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_sucursal]');
	
	var $genera_PDF = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=PDF]');
	var $busqueda_reporte= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_rep= $('#lienzo_recalculable').find('#div_rep');
	
	var $div_busqueda= $('#lienzo_recalculable').find('#div_busqueda');
	var $tr_oculto= $('#lienzo_recalculable').find('#tr_oculto');
	var $vermas= $('#lienzo_recalculable').find('#vermas');
	var $vermenos= $('#lienzo_recalculable').find('#vermenos');
	
	//Ocultar tr
	$tr_oculto.hide();
	
	//Muestra trs al hacer clic en esta imagen
	$vermas.click(function(event){
		event.preventDefault();
		$div_busqueda.animate({height: '100px'}, 500);
		
		//Redimensionar el espacio para el resultado del reporte
		var height2 = $('#cuerpo').css('height');
		var alto = parseInt(height2)-282;
		var pix_alto=alto+'px';
		$('#table_rep').tableScroll({height:parseInt(pix_alto)});
		
		$vermas.hide();
		$vermenos.show();
		$tr_oculto.show();
		verMas=true;
	});
	
	
	
	//Oculta trs al hacer clic en esta imagen
	$vermenos.click(function(event){
		event.preventDefault();
		$div_busqueda.animate({height: '58px'}, 500);
		
		//Redimensionar el espacio para el resultado del reporte
		var height2 = $('#cuerpo').css('height');
		var alto = parseInt(height2)-260;
		var pix_alto=alto+'px';
		$('#table_rep').tableScroll({height:parseInt(pix_alto)});
		
		$vermenos.hide();
		$vermas.show();
		$tr_oculto.hide();
		verMas=false;
	});
	
	
	$select_cuentas.children().remove();
	var html2='<option value="1">Todas</option>';
	html2+='<option value="2">Una cuenta</option>';
	$select_cuentas.append(html2);
	
	$descripcion.css({'background' : '#DDDDDD'});
	$descripcion.attr('readonly',true);
	$descripcion.css({'background' : '#DDDDDD'});
	$select_cuenta.attr('disabled','-1');
	$select_subcuenta.attr('disabled','-1');
	$select_subsubcuenta.attr('disabled','-1');
	$select_subsubsubcuenta.attr('disabled','-1');
	$select_subsubsubsubcuenta.attr('disabled','-1');
	$descripcion.attr('disabled','-1');
	
	//Ocultar las cuentas por default, solo se mostraran mas adelante de acuerdo al nivel definido para la empresa
	$select_cuenta.hide();
	$select_subcuenta.hide();
	$select_subsubcuenta.hide();
	$select_subsubsubcuenta.hide();
	$select_subsubsubsubcuenta.hide();
	
	var array_ctas_nivel1;
	var array_ctas;
	var verMas=false;
	
	var arreglo_parametros = { iu:config.getUi() };
	var restful_json_service = config.getUrlForGetAndPost() + '/getDatos.json';
	$.post(restful_json_service,arreglo_parametros,function(entry){
		
		//cargar select de sucursales
		$select_sucursal.children().remove();
		var html_suc = '';
		$.each(entry['Suc'],function(entryIndex,suc){
			html_suc += '<option value="' + suc['id'] + '"  >' + suc['titulo'] + '</option>';
		});
		$select_sucursal.append(html_suc);
		
		//Visualizar subcuentas de acuerdo al nivel definido para la empresa
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=1 ){ $select_cuenta.show(); };
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=2 ){ $select_subcuenta.show(); };
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=3 ){ $select_subsubcuenta.show(); };
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=4 ){ $select_subsubsubcuenta.show(); };
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=5 ){ $select_subsubsubsubcuenta.show(); };
		
		array_ctas_nivel1=entry['Cta'];
		//mesActual = entry['Dato'][0]['mesActual'];
	});
	
	
	$select_cuentas.change(function(){
		if(parseInt($(this).val())==1){
			$descripcion.val('');
			$select_cuenta.children().remove();
			$select_subcuenta.children().remove();
			$select_subsubcuenta.children().remove();
			$select_subsubsubcuenta.children().remove();
			$select_subsubsubsubcuenta.children().remove();
			
			$descripcion.css({'background' : '#DDDDDD'});
			$select_cuenta.attr('disabled','-1');
			$select_subcuenta.attr('disabled','-1');
			$select_subsubcuenta.attr('disabled','-1');
			$select_subsubsubcuenta.attr('disabled','-1');
			$select_subsubsubsubcuenta.attr('disabled','-1');
			$descripcion.attr('disabled','-1');
			$select_cuentas.focus();
		}else{
			$descripcion.css({'background' : '#ffffff'});
			$select_cuenta.removeAttr('disabled');
			$select_subcuenta.removeAttr('disabled');
			$select_subsubcuenta.removeAttr('disabled');
			$select_subsubsubcuenta.removeAttr('disabled');
			$select_subsubsubsubcuenta.removeAttr('disabled');
			$descripcion.removeAttr('disabled');
			
			$select_cuenta.children().remove();
			var html_cta = '';
			$.each(array_ctas_nivel1,function(entryIndex,cta){
				html_cta +='<option value="'+ cta['cta'] +'">' + cta['cta'] +'</option>';
			});
			$select_cuenta.append(html_cta);
			
			//Llamada a la funcion que busca las cuentas
			buscar_subcuentas($select_cuenta.val(), 2, $select_cuenta, $select_subcuenta)
			
			$select_cuenta.focus();
		}
	});
	
	
	
	
	var buscar_subcuentas= function(valor_cta, nivel, $campo_select, $campo_select2){
		var input_json_cuentas = config.getUrlForGetAndPost() + '/getCtas.json';
		$arreglo = {'cta':$select_cuenta.val(), 'scta':$select_subcuenta.val(), 'sscta':$select_subsubcuenta.val(), 'ssscta':$select_subsubsubcuenta.val(), 'nivel':nivel, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
		
		$.post(input_json_cuentas,$arreglo,function(data){
			//alert(data['Cta'].length);
			if(parseInt(data['Cta'].length)>0){
				$campo_select2.children().remove();
				var html = '';
				$.each(data['Cta'],function(entryIndex,cta){
					if(parseInt(cta['cta'])==0){
						html += '<option value="'+ cta['cta'] +'"></option>';
					}else{
						html += '<option value="'+ cta['cta'] +'">' + cta['cta'] + '</option>';
					}
					if($descripcion.val()==''){
						$descripcion.val(cta['descripcion']);
					}
				});
				$campo_select2.append(html);
				
				//Almacena el arreglo del ultimo nivel que trae datos
				array_ctas=data['Cta'];
			}else{
				$.each(array_ctas,function(entryIndex,cta){
					//alert("valor_cta:"+valor_cta+" | cta:"+cta['cta']);
					if(parseInt(valor_cta)==parseInt(cta['cta'])){
						$descripcion.val(cta['descripcion']);
					}
				});
			}
		});
	}
	
	$aplicar_evento_change = function(nivel, $campo_select, $campo_select2){
		$campo_select.change(function(){
			var valor_cta=$(this).val();
			$descripcion.val('');
			
			//Llamada a la funcion que busca las cuentas
			buscar_subcuentas(valor_cta, nivel, $campo_select, $campo_select2)
		});
	}
	
	//Obtener las subcuentas de acuerdo al nivel que se le indica
	$aplicar_evento_change(2, $select_cuenta, $select_subcuenta);
	$aplicar_evento_change(3, $select_subcuenta, $select_subsubcuenta);
	$aplicar_evento_change(4, $select_subsubcuenta, $select_subsubsubcuenta);
	$aplicar_evento_change(5, $select_subsubsubcuenta, $select_subsubsubsubcuenta);
	
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
		
	$fecha_inicial.click(function (s){
	var a=$('div.datepicker');
		a.css({'z-index':100});
	});
	$fecha_inicial.val(mostrarFecha());
			
			
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
			$fecha_inicial.val(formated);
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
			
        
        
	$fecha_final.DatePicker({
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
	
	
	$fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
    
	$fecha_final.val(mostrarFecha());






	
	//Crear y descargar PDF de Reporte de Balanza de Comprobación
	$genera_PDF.click(function(event){
		event.preventDefault();
		var mes="0";
		var cta="0";
		var scta="0";
		var sscta="0";
		var ssscta="0";
		var sssscta="0";
		
		if($fecha_inicial.val().trim()!='' && $fecha_final.val().trim()!=''){
			
			if($select_cuenta.val()!=null && $select_cuenta.val()!=""){
				cta=$select_cuenta.val();
			}
			if($select_subcuenta.val()!=null && $select_subcuenta.val()!=""){
				scta=$select_subcuenta.val();
			}
			if($select_subsubcuenta.val()!=null && $select_subsubcuenta.val()!=""){
				sscta=$select_subsubcuenta.val();
			}
			if($select_subsubsubcuenta.val()!=null && $select_subsubsubcuenta.val()!=""){
				ssscta=$select_subsubsubcuenta.val();
			}
			if($select_subsubsubsubcuenta.val()!=null && $select_subsubsubsubcuenta.val()!=""){
				sssscta=$select_subsubsubsubcuenta.val();
			}
				
			var cadena = $select_sucursal.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val()+"___"+$select_cuentas.val()+"___"+cta+"___"+scta+"___"+sscta+"___"+ssscta+"___"+sssscta;
			//alert(cadena);
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var input_json = config.getUrlForGetAndPost() + '/getPdfRepBalanzaComprobacion/'+cadena+'/'+iu+'/out.json';
			window.location.href=input_json;
		}else{
			jAlert('Es necesario seleccionar las fechas del Periodo.', 'Atencion!', function(r) {
				if($fecha_inicial.val().trim()==''){
					$fecha_inicial.focus();
				}else{
					$fecha_final.focus();
				}
			});
		}
	});//termina llamada json
	
	
	
	$busqueda_reporte.click(function(event){
		event.preventDefault();
		$div_rep.children().remove();
		
		var cta="0";
		var scta="0";
		var sscta="0";
		var ssscta="0";
		var sssscta="0";
		
		
		if($fecha_inicial.val().trim()!='' && $fecha_final.val().trim()!=''){
			
			if($select_cuenta.val()!=null && $select_cuenta.val()!=""){
				cta=$select_cuenta.val();
			}
			if($select_subcuenta.val()!=null && $select_subcuenta.val()!=""){
				scta=$select_subcuenta.val();
			}
			if($select_subsubcuenta.val()!=null && $select_subsubcuenta.val()!=""){
				sscta=$select_subsubcuenta.val();
			}
			if($select_subsubsubcuenta.val()!=null && $select_subsubsubcuenta.val()!=""){
				ssscta=$select_subsubsubcuenta.val();
			}
			if($select_subsubsubsubcuenta.val()!=null && $select_subsubsubsubcuenta.val()!=""){
				sssscta=$select_subsubsubsubcuenta.val();
			}
			
			var arreglo_parametros = {
				suc: $select_sucursal.val(),
				fecha_ini: $fecha_inicial.val(),
				fecha_fin: $fecha_final.val(),
				cuentas: $select_cuentas.val(),
				cta: cta,
				scta: scta,
				sscta: sscta,
				ssscta: ssscta,
				sssscta: sssscta,
				iu:config.getUi()
			};
			
		
			var restful_json_service = config.getUrlForGetAndPost() + '/getDatosReporte.json'
			var proveedoor="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				var body_tabla = entry['Data'];
				
				var header_tabla = {
					cuenta			:'Cuenta',
					descripcion		:'Descripci&oacute;n',
					saldo_inicial	:'Saldo&nbsp;Inicial',
					debe			:'Debe',
					haber			:'Haber',
					saldo_final  	:'Saldo&nbsp;Final',
				};
				
				var html_reporte = '<table id="table_rep">';
				var html_fila_vacia='';
				var html_footer = '';
				
				html_reporte +='<thead> <tr>';
				for(var key in header_tabla){
					var attrValue = header_tabla[key];
					if(attrValue == "Cuenta"){
						html_reporte +='<td width="150px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Descripci&oacute;n"){
						html_reporte +='<td width="450px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Saldo&nbsp;Inicial"){
						html_reporte +='<td width="135px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == 'Debe'){
						html_reporte +='<td width="130px" align="left" >'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Haber"){
						html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Saldo&nbsp;Final"){
						html_reporte +='<td width="135px" align="left">'+attrValue+'</td>'; 
					}
				}
				html_reporte +='</tr> </thead>';
				
				html_fila_vacia +='<tr class="first">';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="150px" height="10"></td>';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="450px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="135px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="135px"></td>';
				html_fila_vacia +='</tr>';
				
				
				if(parseInt(body_tabla.length)>0){
					
					for(var i=0; i<body_tabla.length; i++){
						html_reporte +='<tr>';
						html_reporte +='<td align="left">'+body_tabla[i]["cuenta"]+'</td>';
						html_reporte +='<td align="left">'+body_tabla[i]["descripcion"]+'</td>';
						html_reporte +='<td align="right">'+ ((body_tabla[i]["saldo_inicial"].trim()=='')? '':$(this).agregar_comas(body_tabla[i]["saldo_inicial"])) +'</td>';
						html_reporte +='<td align="right">'+ ((body_tabla[i]["debe"].trim()=='')? '':$(this).agregar_comas(body_tabla[i]["debe"])) +'</td>';
						html_reporte +='<td align="right">'+ ((body_tabla[i]["haber"].trim()=='')? '':$(this).agregar_comas(body_tabla[i]["haber"])) +'</td>';
						html_reporte +='<td align="right">'+ ((body_tabla[i]["saldo_final"].trim()=='')? '':$(this).agregar_comas(body_tabla[i]["saldo_final"])) +'</td>';
						html_reporte +='</tr>';
					}
					
				}
				
				
				html_reporte +='<tfoot>';
					html_reporte +='<tr>';
					html_reporte +='<td align="left"></td>';
					html_reporte +='<td align="left"></td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(entry['Total']['suma_si'])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(entry['Total']['suma_d'])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(entry['Total']['suma_h'])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(entry['Total']['suma_sf'])+'</td>';
					html_reporte +='</tr>';
				html_reporte +='</tfoot>';
				
				
				html_reporte += '</table>';
				
				
				$div_rep.append(html_reporte); 
				var height2 = $('#cuerpo').css('height');
				var alto = 0;
				if(verMas){
					//Entra aqui si esta activado la opcion ver mas parametros de la busqueda
					alto = parseInt(height2)-302;
				}else{
					alto = parseInt(height2)-260;
				}
				var pix_alto=alto+'px';
				$('#table_rep').tableScroll({height:parseInt(pix_alto)});
			});
			
		}else{
			jAlert('Es necesario seleccionar las fechas del Periodo.', 'Atencion!', function(r) {
				if($fecha_inicial.val().trim()==''){
					$fecha_inicial.focus();
				}else{
					$fecha_final.focus();
				}
			});
		}
		
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_inicial, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_final, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_cuentas, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_cuenta, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_subcuenta, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_subsubcuenta, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_subsubsubcuenta, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_subsubsubsubcuenta, $busqueda_reporte);
	
	//$select_tipo_reporte.focus();
});
