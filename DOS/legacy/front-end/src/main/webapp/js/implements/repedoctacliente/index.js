$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Estados de Clientes' ,                 
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
			return this.contextpath + "/controllers/repedoctacliente";
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
	
	
	var $select_tipo_reporte = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=tipo_reporte]');
        var $select_agentes = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=agentes]');
	var $fecha_corte = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_corte]');
	var $id_cliente_edo_cta = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=id_cliente_edo_cta]');
	var $razon_cli = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=razon_cli]');
	var $busca_cliente = $('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=busca_cliente]');
	var $genera_edocta = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $busqueda_reporte_edocta= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_reporte_estados_de_cuenta= $('#lienzo_recalculable').find('#divreporteedocta');
	
	$fecha_corte.attr('readonly',true);
	$razon_cli.attr('readonly',true);
	$razon_cli.css({'background' : '#DDDDDD'});
	$busca_cliente.hide();
	
	$select_tipo_reporte.children().remove();
	html='<option value="0">General</option>';
	html+='<option value="1">Por cliente</option>';
        html+='<option value="2">Por agente</option>';
	$select_tipo_reporte.append(html);
        
        $select_agentes.children().remove();
        option='<option value="0">[----------------------------------]</option>';
	$select_agentes.append(option);	
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

	$fecha_corte.val(mostrarFecha());
	$fecha_corte.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
	$fecha_corte.DatePicker({
		format:'Y-m-d',
		onBeforeShow: function(){
			$fecha_corte.DatePickerSetDate($fecha_corte.val(), true);
		},
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
				$fecha_corte.DatePickerHide();
			};
		}
	});
	
	
	
	$select_tipo_reporte.change(function(){
            
		if(parseInt($(this).val())==0){
			$div_reporte_estados_de_cuenta.children().remove();
			$razon_cli.css({'background' : '#DDDDDD'});
			$razon_cli.attr('readonly',true);
			$busca_cliente.hide();
			$razon_cli.val('');
			$id_cliente_edo_cta.val(0);
                        
			//$select_agentes.hide(); 
			$select_agentes.children().remove();
			option='<option value="0">[----------------------------------]</option>';
			$select_agentes.append(option);	
		}
		if(parseInt($(this).val())==1){
			$div_reporte_estados_de_cuenta.children().remove();
			//$select_agentes.hide(); 
			$select_agentes.children().remove();
			option='<option value="0">[----------------------------------]</option>';
			$select_agentes.append(option);
			$razon_cli.css({'background' : '#ffffff'});
			$busca_cliente.show();
		}
                
		if(parseInt($(this).val())==2){
			$div_reporte_estados_de_cuenta.children().remove();
			$razon_cli.css({'background' : '#DDDDDD'});
			$razon_cli.attr('readonly',true);
			$busca_cliente.hide();
			$razon_cli.val('');
			$id_cliente_edo_cta.val(0);
			
			var arreglo_parametros = {	iu:config.getUi()};
			var restful_json_service = config.getUrlForGetAndPost() + '/get_cargando_agentes.json'
			//alert(restful_json_service);
			
			$.post(restful_json_service,arreglo_parametros,function(entry){
				$select_agentes.children().remove();
				var agente_html = '<option value="0" selected="yes">[--Seleccionar Agente--]</option>';
				$.each(entry['Agentes'],function(entryIndex,agente){
					agente_html += '<option value="' + agente['id'] + '"  >' + agente['nombre_agente'] + '</option>';
				});
				$select_agentes.append(agente_html);
			});
		}
                
                
	});
	
	
	
	
	//buscador de clientes
	$busca_clientes = function(){
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
		html+='<option value="3">Razon social</option>';
		html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		
		
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
					$id_cliente_edo_cta.val($(this).find('#idclient').val());
					$razon_cli.val($(this).find('span.razon').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
				});
			});//termina llamada json
		});
		
		$cancelar_plugin_busca_cliente.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
		});
	}//termina buscador de clientes
	
	
	
	
	
	//buscador de clientes
	$busca_cliente.click(function(event){
		event.preventDefault();
		$busca_clientes();
	});
	
	
	
	
	
	
	//genera pdf del reporte de estados de cuenta de clientes
	$genera_edocta.click(function(event){
		event.preventDefault();
		var fecha = $fecha_corte.val();
		var id_cliente=$id_cliente_edo_cta.val();
		var id_agente=$select_agentes.val()
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
		var input_json = config.getUrlForGetAndPost() + '/get_genera_pdf_estado_cuenta_cliente/'+$select_tipo_reporte.val()+'/'+id_agente+'/'+id_cliente+'/'+fecha+'/'+iu+'/out.json'
		
		//var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_estado_cuenta_cliente/'+$select_tipo_reporte.val()+'/'+id_cliente+'/'+fecha+'/'+iu+'/out.json';
		window.location.href=input_json;
		
	});//termina llamada json
	
	
        
        
        
        
        reporte_edocta=function(){
            $div_reporte_estados_de_cuenta.children().remove();
			var arreglo_parametros = {	tipo_reporte: $select_tipo_reporte.val(),
                                                        id_cliente: $id_cliente_edo_cta.val(),
                                                        fecha_corte: $fecha_corte.val(),
                                                        iu:config.getUi(),
                                                        agente:$select_agentes.val()
                                                 };
			
			var restful_json_service = config.getUrlForGetAndPost() + '/getReporteEdoCtaClientes.json'
			var cliente="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				var body_tabla = entry['Facturasmn'];
				var body_tablausd = entry['Facturasusd'];
				var footer_tabla = entry['Totales'];
				var header_tabla = {
					serie_folio		:'Factura',
					fecha_facturacion	:'Fecha',
					orden_compra		:'O. Compra',
					moneda_total		:'',
					monto_total		:'Monto Facturado',
					moneda_pagado		:'',
					importe_pagado  	:'Monto Pagado',
					ultimo_pago    		:'Ultimo Pago',
					moneda_saldo    	:'',
					saldo_factura    	:'Saldo Pendiente'
				};
				
				var html_reporte = '<table id="edocta" width="100%">';
				var html_fila_vacia='';
				var html_footer = '';
				
				html_reporte +='<thead> <tr>';
				for(var key in header_tabla){
					var attrValue = header_tabla[key];
					if(attrValue == "Factura"){
						html_reporte +='<td width="90px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Fecha"){
						html_reporte +='<td width="90px" align="center">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "O. Compra"){
						html_reporte +='<td width="120px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == ''){
						html_reporte +='<td width="10px" align="right" >'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Monto Facturado"){
						html_reporte +='<td width="130px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Monto Pagado"){
						html_reporte +='<td width="100px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Ultimo Pago"){
						html_reporte +='<td width="90px" align="center">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Saldo Pendiente"){
						html_reporte +='<td width="90px" align="left" id="monto">'+attrValue+'</td>'; 
					}

				}
				html_reporte +='</tr> </thead>';
				
				
				html_fila_vacia +='<tr>';
				html_fila_vacia +='<td align="left"  id="sin_borde" height="10"></td>';
				html_fila_vacia +='<td align="left"  id="sin_borde"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde"></td>';
				html_fila_vacia +='<td align="left"  id="sin_borde"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde"></td>';
				html_fila_vacia +='</tr>';
				
				
				
				
				
				var orden_compra="";
				var simbolo_moneda="";
				var cliente_actual="";
				
                //inicializar variables
                var suma_monto_total_cliente=0.0;
                var suma_importe_pagado_cliente=0.0;
                var suma_saldo_pendiente_cliente=0.0;
                
                var suma_monto_total_moneda=0.0;
                var suma_importe_pagado_moneda=0.0;
                var suma_saldo_pendiente_moneda=0.0;
				
				
				if(parseInt(body_tabla.length)>0){
					cliente_actual=body_tabla[0]["cliente"];
					html_reporte +='<tr id="tr_totales" class="first"><td align="left" colspan="12">'+cliente_actual+'</td></tr>';
					for(var i=0; i<body_tabla.length; i++){
						if(body_tabla[i]["orden_compra"]==null){
							orden_compra="";
						}else{
							orden_compra=body_tabla[i]["orden_compra"];
						}
						
						if(body_tabla[i]["denominacion"]=="M.N."){
							simbolo_moneda="$";
						}else{
							simbolo_moneda=body_tabla[i]["denominacion"];
						}
						
						
						if(cliente_actual == body_tabla[i]["cliente"]){
							html_reporte +='<tr>';
							html_reporte +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
							html_reporte +='<td align="center" >'+body_tabla[i]["fecha_facturacion"]+'</td>';
							html_reporte +='<td align="left" >'+orden_compra+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_total"]).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe_pagado"]).toFixed(2))+'</td>';
							html_reporte +='<td align="center" >'+body_tabla[i]["ultimo_pago"]+'</td>';
							html_reporte +='<td  align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td width="100px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["saldo_factura"]).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							suma_monto_total_cliente=parseFloat(suma_monto_total_cliente) + parseFloat(body_tabla[i]["monto_total"]);
							suma_importe_pagado_cliente=parseFloat(suma_importe_pagado_cliente) + parseFloat(body_tabla[i]["importe_pagado"]);
							suma_saldo_pendiente_cliente=parseFloat(suma_saldo_pendiente_cliente) + parseFloat(body_tabla[i]["saldo_factura"]);
							
							suma_monto_total_moneda=parseFloat(suma_monto_total_moneda) + parseFloat(body_tabla[i]["monto_total"]);
							suma_importe_pagado_moneda=parseFloat(suma_importe_pagado_moneda) + parseFloat(body_tabla[i]["importe_pagado"]);
							suma_saldo_pendiente_moneda=parseFloat(suma_saldo_pendiente_moneda) + parseFloat(body_tabla[i]["saldo_factura"]);
						}else{
							//imprimir totales
							html_reporte +='<tr id="tr_totales">';
							html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="right" id="sin_borde">Total Cliente</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_total_cliente).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_cliente).toFixed(2))+'</td>';
							html_reporte +='<td align="left" ></td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_cliente).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							//fila vacia
							html_reporte +=html_fila_vacia;
							
							//reinicializar varibles
							suma_monto_total_cliente=0.0;
							suma_importe_pagado_cliente=0.0;
							suma_saldo_pendiente_cliente=0.0;
							
							//tomar razon social de nuevo cliente
							cliente_actual=body_tabla[i]["cliente"];
							
							html_reporte +='<tr id="tr_totales"><td align="left" colspan="12">'+cliente_actual+'</td></tr>';
							//crear primer registro del nuevo cliente
							html_reporte +='<tr>';
							html_reporte +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
							html_reporte +='<td align="center" >'+body_tabla[i]["fecha_facturacion"]+'</td>';
							html_reporte +='<td align="left" >'+orden_compra+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_total"]).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe_pagado"]).toFixed(2))+'</td>';
							html_reporte +='<td align="center" >'+body_tabla[i]["ultimo_pago"]+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["saldo_factura"]).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							//sumar montos del nuevo cliente
							suma_monto_total_cliente=parseFloat(suma_monto_total_cliente) + parseFloat(body_tabla[i]["monto_total"]);
							suma_importe_pagado_cliente=parseFloat(suma_importe_pagado_cliente) + parseFloat(body_tabla[i]["importe_pagado"]);
							suma_saldo_pendiente_cliente=parseFloat(suma_saldo_pendiente_cliente) + parseFloat(body_tabla[i]["saldo_factura"]);
							
							suma_monto_total_moneda=parseFloat(suma_monto_total_moneda) + parseFloat(body_tabla[i]["monto_total"]);
							suma_importe_pagado_moneda=parseFloat(suma_importe_pagado_moneda) + parseFloat(body_tabla[i]["importe_pagado"]);
							suma_saldo_pendiente_moneda=parseFloat(suma_saldo_pendiente_moneda) + parseFloat(body_tabla[i]["saldo_factura"]);
						}
					}
					//imprimir total del ultimo cliente
					html_reporte +='<tr id="tr_totales">';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
					html_reporte +='<td align="left" id="sin_borde"></td>';
					html_reporte +='<td align="right" id="sin_borde">Total Cliente</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_total_cliente).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_cliente).toFixed(2))+'</td>';
					html_reporte +='<td align="left" ></td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_cliente).toFixed(2))+'</td>';
					html_reporte +='</tr>';
					
					//$div_reporte_estados_de_cuenta.append(html_reporte);
					
					//imprimir totales de la moneda
					html_footer +='<tr id="tr_totales">';
					html_footer +='<td align="left" id="sin_borde_derecho"></td>';
					html_footer +='<td align="left" id="sin_borde"></td>';
					html_footer +='<td align="right" id="sin_borde">Total MN</td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_total_moneda).toFixed(2))+'</td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_moneda).toFixed(2))+'</td>';
					html_footer +='<td align="left" ></td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_moneda).toFixed(2))+'</td>';
					html_footer +='</tr>';
				}
                //inicializar variables
                suma_monto_total_cliente=0.0;
                suma_importe_pagado_cliente=0.0;
                suma_saldo_pendiente_cliente=0.0;
                
                suma_monto_total_moneda=0.0;
                suma_importe_pagado_moneda=0.0;
                suma_saldo_pendiente_moneda=0.0;
				
				//html_reporte='';
				
				if(parseInt(body_tablausd.length)>0){
					cliente_actual=body_tablausd[0]["cliente"];
					//fila vacia
					html_reporte +=html_fila_vacia;
					html_reporte +=html_fila_vacia;
					html_reporte +='<tr id="tr_totales"><td align="left" colspan="12">'+cliente_actual+'</td></tr>';
					for(var i=0; i<body_tablausd.length; i++){
						if(body_tablausd[i]["orden_compra"]==null){
							orden_compra="";
						}else{
							orden_compra=body_tablausd[i]["orden_compra"];
						}
						
						if(body_tablausd[i]["denominacion"]=="M.N."){
							simbolo_moneda="$";
						}else{
							simbolo_moneda=body_tablausd[i]["denominacion"];
						}
						
						
						
						if(cliente_actual == body_tablausd[i]["cliente"]){
							html_reporte +='<tr>';
							html_reporte +='<td align="left" >'+body_tablausd[i]["serie_folio"]+'</td>';
							html_reporte +='<td align="center" >'+body_tablausd[i]["fecha_facturacion"]+'</td>';
							html_reporte +='<td align="left" >'+orden_compra+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tablausd[i]["monto_total"]).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tablausd[i]["importe_pagado"]).toFixed(2))+'</td>';
							html_reporte +='<td align="center" >'+body_tablausd[i]["ultimo_pago"]+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tablausd[i]["saldo_factura"]).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							suma_monto_total_cliente=parseFloat(suma_monto_total_cliente) + parseFloat(body_tablausd[i]["monto_total"]);
							suma_importe_pagado_cliente=parseFloat(suma_importe_pagado_cliente) + parseFloat(body_tablausd[i]["importe_pagado"]);
							suma_saldo_pendiente_cliente=parseFloat(suma_saldo_pendiente_cliente) + parseFloat(body_tablausd[i]["saldo_factura"]);
							
							suma_monto_total_moneda=parseFloat(suma_monto_total_moneda) + parseFloat(body_tablausd[i]["monto_total"]);
							suma_importe_pagado_moneda=parseFloat(suma_importe_pagado_moneda) + parseFloat(body_tablausd[i]["importe_pagado"]);
							suma_saldo_pendiente_moneda=parseFloat(suma_saldo_pendiente_moneda) + parseFloat(body_tablausd[i]["saldo_factura"]);
						}else{
							//imprimir totales
							html_reporte +='<tr id="tr_totales">';
							html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="right" id="sin_borde">Total Cliente</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_total_cliente).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_cliente).toFixed(2))+'</td>';
							html_reporte +='<td align="left" ></td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_cliente).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							//fila vacia
							html_reporte +=html_fila_vacia;
							
							//reinicializar varibles
							suma_monto_total_cliente=0.0;
							suma_importe_pagado_cliente=0.0;
							suma_saldo_pendiente_cliente=0.0;
							
							//tomar razon social de nuevo cliente
							cliente_actual=body_tablausd[i]["cliente"];
							
							html_reporte +='<tr id="tr_totales"><td align="left" colspan="12">'+cliente_actual+'</td></tr>';
							//crear primer registro del nuevo cliente
							html_reporte +='<tr>';
							html_reporte +='<td align="left" >'+body_tablausd[i]["serie_folio"]+'</td>';
							html_reporte +='<td align="center" >'+body_tablausd[i]["fecha_facturacion"]+'</td>';
							html_reporte +='<td align="left" >'+orden_compra+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tablausd[i]["monto_total"]).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tablausd[i]["importe_pagado"]).toFixed(2))+'</td>';
							html_reporte +='<td align="center" >'+body_tablausd[i]["ultimo_pago"]+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tablausd[i]["saldo_factura"]).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							//sumar montos del nuevo cliente
							suma_monto_total_cliente=parseFloat(suma_monto_total_cliente) + parseFloat(body_tablausd[i]["monto_total"]);
							suma_importe_pagado_cliente=parseFloat(suma_importe_pagado_cliente) + parseFloat(body_tablausd[i]["importe_pagado"]);
							suma_saldo_pendiente_cliente=parseFloat(suma_saldo_pendiente_cliente) + parseFloat(body_tablausd[i]["saldo_factura"]);
							
							suma_monto_total_moneda=parseFloat(suma_monto_total_moneda) + parseFloat(body_tablausd[i]["monto_total"]);
							suma_importe_pagado_moneda=parseFloat(suma_importe_pagado_moneda) + parseFloat(body_tablausd[i]["importe_pagado"]);
							suma_saldo_pendiente_moneda=parseFloat(suma_saldo_pendiente_moneda) + parseFloat(body_tablausd[i]["saldo_factura"]);
						}
					}
					
					//imprimir total del ultimo cliente
					html_reporte +='<tr id="tr_totales">';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
					html_reporte +='<td align="left" id="sin_borde"></td>';
					html_reporte +='<td align="right" id="sin_borde">Total Cliente</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_total_cliente).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_cliente).toFixed(2))+'</td>';
					html_reporte +='<td align="left" ></td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_cliente).toFixed(2))+'</td>';
					html_reporte +='</tr>';
					
					//$div_reporte_estados_de_cuenta.append(html_reporte); 
					
					
					//imprimir totales de la moneda
					html_footer +='<tr id="tr_totales">';
					html_footer +='<td align="left" id="sin_borde_derecho"></td>';
					html_footer +='<td align="left" id="sin_borde"></td>';
					html_footer +='<td align="right" id="sin_borde">Total USD</td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_total_moneda).toFixed(2))+'</td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_moneda).toFixed(2))+'</td>';
					html_footer +='<td align="left" ></td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_moneda).toFixed(2))+'</td>';
					html_footer +='</tr>';
					
				}
				
				
				html_reporte +='<tfoot>';
					html_reporte += html_footer;
				html_reporte +='</tfoot>';
				
				
				
				html_reporte += '</table>';
				
				
				$div_reporte_estados_de_cuenta.append(html_reporte); 
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-300;
				var pix_alto=alto+'px';
				$('#edocta').tableScroll({height:parseInt(pix_alto)});
			});
            
        }
        
        
	
	$busqueda_reporte_edocta.click(function(event){
		event.preventDefault();
                if($select_tipo_reporte.val() == 0){
                    reporte_edocta();
                }
                if($select_tipo_reporte.val() == 1){
                    if($razon_cli.val()!= ''){
                        reporte_edocta();
                    }else{
                        jAlert("Debe de Asignar un cliente");
                    }
                    
                }
                if($select_tipo_reporte.val() == 2){
                    if($select_agentes.val()!= 0){
                        reporte_edocta();
                    }else{
                        jAlert("Debe de Elegir un Agente");
                    }
                    
                }
         });
	
});   
        
        
        
        
    
