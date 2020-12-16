$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Pedidos' ,                 
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
			return this.contextpath + "/controllers/reppedidos";
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
	var $agente = $('#lienzo_recalculable').find('select[name=agente]');
	var $hidde_cliente;
	var $fecha_inicial = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_final]');
	var $genera_reporte_facturacion = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $busqueda_reporte_pedidos= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_reporte_facturacion= $('#lienzo_recalculable').find('#divreportefacturacion');
	var $Nombre_Cliente= $('#lienzo_recalculable').find('input[name=nombrecliente]');
	var $Buscar_clientes= $('#lienzo_recalculable').find('a[href*=busca_cliente]');
	
	//$Nombre_Cliente.attr({'readOnly':true});
	var arreglo_parametros = { 
		iu:config.getUi()
	 };
	var restful_json_service = config.getUrlForGetAndPost() + '/getBuscaDatos.json';
	$.post(restful_json_service,arreglo_parametros,function(entry){
		//cargar select de agentes
		$agente.children().remove();
		var agente_hmtl = '<option value= "0" >Seleccione Agente</option>';
		$.each(entry['agentes'],function(entryIndex,data){
			agente_hmtl +='<option value= "' + data['id'] + '" >' + data['nombre'] + '</option>';
		});
		$agente.append(agente_hmtl);
		
		//cargar select de Opciones
		$select_opciones.children().remove();
		var pedido_hmtl = '<option value= "0" >General</option>';
		$.each(entry['proceso'],function(entryIndex,data){
			pedido_hmtl +='<option value= "' + data['id'] + '" >' + data['titulo'] + '</option>';
		});
		$select_opciones.append(pedido_hmtl);
		
	});
	
	
	$fecha_inicial.attr('readonly',true);
	$fecha_final.attr('readonly',true);
        
        
       
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
        
	$Buscar_clientes.click(function(event){
		event.preventDefault();
		busca_clientes($Nombre_Cliente);
	});
        
	//buscador de clientes
	
	busca_clientes=function($Nombre_Cliente){
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
		
		$cadena_buscar.val($Nombre_Cliente.val());
		
		$cadena_buscar.focus();
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			var restful_json_service = config.getUrlForGetAndPost()+'/get_buscador_clientes.json';
			var  arreglo_parametros = {'cadena':$cadena_buscar.val(),'filtro':$select_filtro_por.val(),  'iu': $('#lienzo_recalculable').find('input[name=iu]').val()}

			var trr = '';
			$tabla_resultados.children().remove();
			//$.post(input_json,$arreglo,function(entry){
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
					$(this).find('td').css({'background-color':'#e7e8ea'});
				});
				$('tr:even' , $tabla_resultados).hover(function () {
					$(this).find('td').css({'background-color':'#FBD850'});
				}, function() {
					$(this).find('td').css({'background-color':'#FFFFFF'});
				});
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					$Nombre_Cliente.val($(this).find('span.razon').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					
					//asignar el enfoque al campo nombre del cliente
					$Nombre_Cliente.focus();
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
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
			
			//asignar el enfoque al campo nombre del cliente
			$Nombre_Cliente.focus();
		});
	}
	
	
	//genera pdf del reporte 
	$genera_reporte_facturacion.click(function(event){
		event.preventDefault();
		var cadena = $select_opciones.val()+"___"+$agente.val()+"___"+$Nombre_Cliente.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val();
		var input_json = config.getUrlForGetAndPost() + '/Maker_PDF_Pedidos/'+cadena+'/'+config.getUi()+'/out.json';
		window.location.href=input_json;
	});//termina llamada json
		
	
	$busqueda_reporte_pedidos.click(function(event){
		event.preventDefault();
		$div_reporte_facturacion.children().remove();
		var fecha_inicial = $fecha_inicial.val();
		var fecha_final = $fecha_final.val();
		
		var arreglo_parametros = {	
			opcion: $select_opciones.val(),
			agente: $agente.val(),
			cliente: $Nombre_Cliente.val(),
			fecha_inicial : $fecha_inicial.val(), 
			fecha_final : $fecha_final.val(), 
			iu:config.getUi()
                        
		};//alert($cliente.val());
		
			var restful_json_service = config.getUrlForGetAndPost() + '/getPedidos.json'
			var cliente="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				var body_tabla = entry['Pedidos'];
				var footer_tabla = entry['Totales'];
				var header_tabla = {
					serie_folio		:'Pedido',
					orden_compra    :'O. Compra',
					fecha_factura   :'Fecha',
					cliente			:'Cliente',
					moneda_subtotal :'',
					subtotal  		:'Sub-Total',
					moneda_ieps    	:'',
					iepscampo  		:'IEPS',
					moneda_iva      :'',
					impuesto  		:'IVA',
					moneda_total    :'',
					total    		:'Total'
				};

									
				var TPventa_neta=0.0;
				var Sumatoriaventa_neta = 0.0;
				var sumatotoriaporciento = 0.0;
				var html_reporte = '<table id="ventas" >';
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
					if(attrValue == "Pedido"){
						html_reporte +='<td width="75px" align="left">'+attrValue+'</td>'; 
					}
					if(attrValue == "O. Compra"){
						html_reporte +='<td width="100px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Fecha"){
						html_reporte +='<td width="75px" align="left">'+attrValue+'</td>'; 
					}
					if(attrValue == "Cliente"){
						html_reporte +='<td width="390px" align="left">'+attrValue+'</td>'; 
					}
					if(attrValue == ''){
						html_reporte +='<td width="5px" align="right" id="simbolo_moneda">'+attrValue+'</td>'; 
					}
					if(attrValue == "Sub-Total"){
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
				var orden_compra="";
				var simbolo_moneda="";
				for(var i=0; i<body_tabla.length; i++){
					
					if(body_tabla[i]["orden_compra"]==null){
						orden_compra="";
					}else{
						orden_compra=body_tabla[i]["orden_compra"];
					}
					
					html_reporte +='<tr>';
					html_reporte +='<td align="left" >'+body_tabla[i]["folio"]+'</td>'; 
					html_reporte +='<td align="left" >'+orden_compra+'</td>'; 
					html_reporte +='<td align="left" >'+body_tabla[i]["fecha_factura"]+'</td>'; 
					html_reporte +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda"]+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["subtotal"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda"]+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_ieps"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda"]+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["impuesto"]).toFixed(2))+'</td>'; 					
					html_reporte +='<td align="right" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda"]+'</td>'; 
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
				
				
				$div_reporte_facturacion.append(html_reporte); 
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-300;
				var pix_alto=alto+'px';
				$('#ventas').tableScroll({height:parseInt(pix_alto)});
			});
             
	});
	
	
	$(this).aplicarEventoKeypressEjecutaTrigger($select_opciones, $busqueda_reporte_pedidos);
	$(this).aplicarEventoKeypressEjecutaTrigger($agente, $busqueda_reporte_pedidos);
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_inicial, $busqueda_reporte_pedidos);
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_final, $busqueda_reporte_pedidos);
	$(this).aplicarEventoKeypressEjecutaTrigger($Nombre_Cliente, $busqueda_reporte_pedidos);
	
	$Nombre_Cliente.focus();
	
});   
        
        
        
        
    
