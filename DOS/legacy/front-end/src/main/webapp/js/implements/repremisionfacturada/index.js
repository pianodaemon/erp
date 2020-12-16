$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Remisiones Facturadas' ,                 
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
			return this.contextpath + "/controllers/repremisionfacturada";
			//  return this.controller;
		}
	};
	
	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
	
	$('#barra_acciones').hide();
	//barra para el buscador 
	$('#barra_buscador').hide();
	
	var $select_opciones = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=opciones]');
	var $remision_facturada = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=remision]');
	var $cliente = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=cliente]');
	var $busca_cliente =$('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=busca_cliente]');
	var $fecha_inicial = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_final]');
	var $genera_reporte_remision_facturada = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $busqueda_reporte_remision_facturada= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_reporte_remision_facturada= $('#lienzo_recalculable').find('#divreporteremision');
	
	
	$select_opciones.children().remove();
	var hmtl_opciones;
	hmtl_opciones += '<option value="1"  >Remisiones Facturadas</option>';
	hmtl_opciones += '<option value="2"  >Remisiones NO Facturadas</option>';
	
	$select_opciones.append(hmtl_opciones);
	
	$fecha_inicial.attr('readonly',true);
	$fecha_final.attr('readonly',true);
     //$cliente.attr('readonly',true);
     
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
			
			
		
	$fecha_inicial.DatePicker({
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
    
	//click generar reporte de remision
	$genera_reporte_remision_facturada.click(function(event){
		event.preventDefault();
		var busqueda = $select_opciones.val()+"___"+$remision_facturada.val()+"___"+$cliente.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val();
		var input_json = config.getUrlForGetAndPost() + '/get_genera_reporte_remision_facturada/'+busqueda+'/'+config.getUi()+'/out.json';
		window.location.href=input_json;
	});
		
	
	$busqueda_reporte_remision_facturada.click(function(event){
		event.preventDefault();
		$div_reporte_remision_facturada.children().remove();
		var fecha_inicial = $fecha_inicial.val();
		var fecha_final = $fecha_final.val();
		if(fecha_inicial != "" && fecha_final != ""){
			
			var arreglo_parametros = {	opcion: $select_opciones.val(),
                                             remision_facturada: $remision_facturada.val(),
                                             cliente: $cliente.val(),
                                             fecha_inicial : $fecha_inicial.val(), 
                                             fecha_final : $fecha_final.val(), 
                                             iu:config.getUi()
                                        };
			
			var restful_json_service = config.getUrlForGetAndPost() + '/getRemision_Facturada.json'
			var cliente="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				var body_tabla = entry['Remisiones'];
				var footer_tabla = entry['Totales'];
				var header_tabla = {
					factura				:'Factura',
					Remision_facturada	:'Remisi&oacute;n',
					fecha_factura		:'Fecha&nbsp;Remisi&oacute;n',
					cliente				:'Cliente',
					moneda_subtotal		:'',
					monto  				:'Subtotal',
					moneda_iva    		:'',
					iepscampo  		    :'IEPS',
					moneda_ieps    	    :'',
					impuesto  			:'IVA',
					moneda_total    	:'',
					total    			:'Total'
				};

									
				var TPventa_neta=0.0;
				var Sumatoriaventa_neta = 0.0;
				var sumatotoriaporciento = 0.0;
				var html_reporte = '<table id="remisiones_facturadas" >';
				var porcentaje = 0.0;
				
				var numero_control=0.0; 
				var cliente=0.0; 
				var moneda="$"; 
				var venta_neta=0.0; 
				var porciento=0.0;
				var tmp= 0;
			
				html_reporte +='<thead> <tr>';
				for(var key in header_tabla){
					var attrValue = header_tabla[key];
					if(attrValue == "Factura"){
						html_reporte +='<td width="75px" align="left">'+attrValue+'</td>'; 
					}
					if(attrValue == "Remisi&oacute;n"){
						html_reporte +='<td width="100px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Fecha&nbsp;Remisi&oacute;n"){
						html_reporte +='<td width="120px" align="left">'+attrValue+'</td>'; 
					}
					if(attrValue == "Cliente"){
						html_reporte +='<td width="390px" align="left">'+attrValue+'</td>'; 
					}
					if(attrValue == ''){
						html_reporte +='<td width="5px" align="right" id="simbolo_moneda">'+attrValue+'</td>'; 
					}
					if(attrValue == "Subtotal"){
						html_reporte +='<td width="80px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					if(attrValue == "IEPS"){
						html_reporte +='<td width="80px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					if(attrValue == "IVA"){
						html_reporte +='<td width="75px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					if(attrValue == "Total"){
						html_reporte +='<td width="100px" align="left" id="monto">'+attrValue+'</td>'; 
					}
				}
				
				html_reporte +='</tr> </thead>';
				var remision="";
				var simbolo_moneda="";
				for(var i=0; i<body_tabla.length; i++){
					if(body_tabla[i]["remision"]==null){
						remision="";
					}else{
						remision=body_tabla[i]["remision"];
					}
					
					simbolo_moneda = body_tabla[i]["moneda_simbolo"];
					/*
					if(body_tabla[i]["moneda_remision_facturada"]=="M.N."){
						simbolo_moneda="$";
					}else{
						simbolo_moneda=body_tabla[i]["moneda_remision_facturada"];
					}
					*/
					html_reporte +='<tr>';
					html_reporte +='<td align="left" >'+body_tabla[i]["factura"]+'</td>'; 
					html_reporte +='<td align="left" >'+remision+'</td>'; 
					html_reporte +='<td align="left" >'+body_tabla[i]["fecha_remision_facturada"]+'</td>'; 
					html_reporte +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_ieps"]).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["impuesto"]).toFixed(2))+'</td>'; 					
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["total"]).toFixed(2))+'</td>';
					html_reporte +='</tr>';
				}
				
				html_reporte +='<tfoot>';
					html_reporte +='<tr>';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="right" id="sin_borde">Total M.N.</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_subtotal"]).toFixed(2))+'</td>'; 
										html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_monto_ieps"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_impuesto"]).toFixed(2))+'</td>'; 					
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_total"]).toFixed(2))+'</td>';
					html_reporte +='</tr>';
					
					html_reporte +='<tr>';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="left"  id="sin_borde"></td>'; 
					html_reporte +='<td align="right" id="sin_borde">Total USD</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">USD</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_subtotal"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">USD</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_monto_ieps"]).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">USD</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_impuesto"]).toFixed(2))+'</td>'; 					
					html_reporte +='<td align="right" id="simbolo_moneda">USD</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_total"]).toFixed(2))+'</td>';
					html_reporte +='</tr>';

					html_reporte +='<tr>';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="right" id="sin_borde">Total General en M.N.</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_subtotal_mn"]).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_monto_ieps_mn"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_impuesto_mn"]).toFixed(2))+'</td>'; 					
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_total_mn"]).toFixed(2))+'</td>';
					html_reporte +='</tr>';
					
				html_reporte +='</tfoot>';
				
				html_reporte += '</table>';
				
				
				$div_reporte_remision_facturada.append(html_reporte); 
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-300;
				var pix_alto=alto+'px';
				$('#remisiones_facturadas').tableScroll({height:parseInt(pix_alto)});
			});
			
			$remision_facturada.focus();
		}else{
			jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
		}
             
	});
        
    //buscador de clientes
	$busca_clientes = function($cliente){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscacliente();
		var $dialogoc =  $('#forma-buscacliente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
		$('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscacliente-window').find('#tabla_resultado');
		var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('#busca_cliente_modalbox');
		var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscacliente-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscacliente-window').find('select[name=filtropor]');
		
		//funcionalidad botones
		$busca_cliente_modalbox.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$busca_cliente_modalbox.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_cliente.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_cliente.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		var html = '';
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		html+='<option value="1">No. de control</option>';
		html+='<option value="2">RFC</option>';
		html+='<option value="3" selected="yes">Razon social</option>';
		html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		$cadena_buscar.val($cliente.val());
		
		$cadena_buscar.focus();
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			$tabla_resultados.children().remove();
			
			var arreglo_parametros = {	cadena: $cadena_buscar.val(),
										filtro: $select_filtro_por.val(),
										iu:config.getUi()
									};
			var restful_json_service = config.getUrlForGetAndPost() + '/get_buscador_clientes.json'
			//alert(restful_json_service);
			var cliente="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				$.each(entry['Clientes'],function(entryIndex,cliente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
							trr += '<input type="hidden" id="direccion" value="'+cliente['direccion']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+cliente['moneda_id']+'">';
							trr += '<input type="hidden" id="moneda" value="'+cliente['moneda']+'">';
							trr += '<span class="no_control">'+cliente['numero_control']+'</span>';
						trr += '</td>';
						trr += '<td width="145"><span class="rfc">'+cliente['rfc']+'</span></td>';
						trr += '<td width="375"><span class="razon">'+cliente['razon_social']+'</span></td>';
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
				
				//seleccionar un cliente del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					//$id_cliente_edo_cta.val($(this).find('#idclient').val());
					$cliente.val($(this).find('span.razon').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					
					$cliente.focus();
					
				});
			});
		});//termina llamada json
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$busca_cliente_modalbox.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $busca_cliente_modalbox);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $busca_cliente_modalbox);
		
		
		$cancelar_plugin_busca_cliente.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
			
			$cliente.focus();
		});
	}//termina buscador de clientes
	
	
	
	
	
	//buscador de clientes
	$busca_cliente.click(function(event){
		event.preventDefault();
		$busca_clientes($cliente);
	});
	
	
	$(this).aplicarEventoKeypressEjecutaTrigger($select_opciones, $busqueda_reporte_remision_facturada);
	$(this).aplicarEventoKeypressEjecutaTrigger($remision_facturada, $busqueda_reporte_remision_facturada);
	$(this).aplicarEventoKeypressEjecutaTrigger($cliente, $busqueda_reporte_remision_facturada);
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_inicial, $busqueda_reporte_remision_facturada);
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_final, $busqueda_reporte_remision_facturada);
	
	$remision_facturada.focus();
});   
        
        
        
        
    
