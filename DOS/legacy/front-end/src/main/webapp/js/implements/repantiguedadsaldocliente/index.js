$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Antig&uuml;edad de Saldos de Cuentas por Cobrar' ,                 
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
			return this.contextpath + "/controllers/repantiguedadsaldocliente";
			//  return this.controller;
		}
	};
	
	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
	
	$('#barra_acciones').hide();
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
	
	//barra para el buscador 
	$('#barra_buscador').hide();
	
	
	var $select_tipo_reporte = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=tipo_reporte]');
	var $fecha_corte = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_corte]');
	var $id_cliente = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=id_cliente]');
	var $razon_cliente = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=razon_cliente]');
	var $busca_cliente = $('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=busca_cliente]');
	var $genera_pdf_reporte = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $busqueda_reporte= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_reporte= $('#lienzo_recalculable').find('#divreporte');
	
        
        //$div_reporte.css({'background' : 'red'});
	$fecha_corte.attr('readonly',true);
	$razon_cliente.attr('readonly',true);
	$razon_cliente.css({'background' : '#DDDDDD'});
	$busca_cliente.hide();
	
	$select_tipo_reporte.children().remove();
	html='<option value="0">General</option>';
	html+='<option value="1">Por cliente</option>';
	$select_tipo_reporte.append(html);
	
	$razon_cliente.attr('readonly',true);
	
	$select_tipo_reporte.change(function(){
		$div_reporte.children().remove();
		if(parseInt($(this).val())==0){
			$razon_cliente.css({'background' : '#DDDDDD'});
			$razon_cliente.attr('readonly',true);
			$busca_cliente.hide();
			$razon_cliente.val('');
			$id_cliente.val(0);
		}else{
			$razon_cliente.css({'background' : '#ffffff'});
			$razon_cliente.attr('readonly',false);
			$busca_cliente.show();
		}
	});
	
	
	
	//$fecha_corte.attr('readonly',true);
    
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
			
	
	//desencadena evento del $campo_ejecutar al pulsar Enter en $campo
	$aplicar_evento_keypress = function($campo, $campo_ejecutar){
		$campo.keypress(function(e){
			if(e.which == 13){
				$campo_ejecutar.trigger('click');
				return false;
			}
		});
	}
	
	//$aplicar_evento_keypress($busqueda_folio, $buscar);
	
	
	
	$fecha_corte.val(mostrarFecha());
    
    

	//buscador de clientes
	$busca_clientes = function($razon_cliente){
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
		
		$cadena_buscar.val($razon_cliente.val());
		
		
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
					$id_cliente.val($(this).find('#idclient').val());
					$razon_cliente.val($(this).find('span.razon').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					
					$razon_cliente.focus();
				});
			});
		});//termina llamada json
		
		
		if($cadena_buscar.val() != ''){
			$busca_cliente_modalbox.trigger('click');
		}
		
		$aplicar_evento_keypress($cadena_buscar, $busca_cliente_modalbox);
		$aplicar_evento_keypress($select_filtro_por, $busca_cliente_modalbox);
		
		$cancelar_plugin_busca_cliente.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
			$razon_cliente.focus();
		});
	}//termina buscador de clientes
	
	
	
	
	
	//buscador de clientes
	$busca_cliente.click(function(event){
		event.preventDefault();
		$busca_clientes($razon_cliente);
	});



    
	//click generar pdf
	$genera_pdf_reporte.click(function(event){
		event.preventDefault();
		var cadena = $razon_cliente.val()+"___"+$select_tipo_reporte.val()+"___"+$fecha_corte.val();
		var input_json = config.getUrlForGetAndPost() + '/getPdfReporteAntiguedadSaldosCliente/'+cadena+'/'+config.getUi()+'/out.json';
		window.location.href=input_json;
	});
		
	
	$busqueda_reporte.click(function(event){
		event.preventDefault();
		$div_reporte.children().remove();
		
		var arreglo_parametros = {  tipo_reporte: $select_tipo_reporte.val(),
                                            cliente: $razon_cliente.val(),
                                            fecha_corte: $fecha_corte.val(),
                                            iu:config.getUi()
					 };
		
		var restful_json_service = config.getUrlForGetAndPost() + '/getReporteAntiguedadSaldosCliente.json'
		$.post(restful_json_service,arreglo_parametros,function(entry){
			var body_tabla = entry['Facturas'];
			/*
			clave_cliente			:'Clave',
			cliente					:'Cliente',	
			*/
			//var footer_tabla = entry['Totales'];
			var header_tabla = {			
				factura					:'Factura',
				fecha_factura			:'Fecha Factura',
				fecha_vencimiento		:'Fecha Vencimiento',
				moneda_por_vencer		:'',
				por_vencer				:'Por Vencer',
				moneda_menor_igual_15	:'',
				menor_igual_15			:'15 Dias',
				moneda_menor_igual_30	:'',
				menor_igual_30			:'30 Dias',
				moneda_menor_igual_45	:'',
				menor_igual_45			:'45 Dias',
				moneda_menor_igual_60	:'',
				menor_igual_60			:'60 Dias',
				moneda_menor_igual_90	:'',
				menor_igual_90			:'90 Dias',
				moneda_mayor_90	:'',
				mayor_90			:'+90 Dias',
				moneda_saldo_factura	:'',
				Total			        :'Total'
				
			};
	/*		
cliente,
clave_cliente,
factura,
moneda_factura,
simbolo_moneda,
fecha_facturacion,
fecha_vencimiento,
moneda_por_vencer, 
por_vencer,
moneda_menor_igual_15, 
menor_igual_15,
moneda_menor_igual_30, 
menor_igual_30,
moneda_menor_igual_45, 
menor_igual_45,
moneda_menor_igual_60, 
menor_igual_60,
moneda_menor_igual_90, 
menor_igual_90,
moneda_mayor_90, 
mayor_90 
*/	
			
			var html_reporte = '<table id="reporte">';
			var html_fila_vacia='';
			var html_footer = '';
			
			html_reporte +='<thead> <tr>';
			for(var key in header_tabla){
				var attrValue = header_tabla[key];
				
				if(attrValue == 'Factura'){
					html_reporte +='<td width="90px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == 'Fecha Factura'){
					html_reporte +='<td width="90px" align="center">'+attrValue+'</td>'; 
				}
				if(attrValue == 'Fecha Vencimiento'){
					html_reporte +='<td  width="110px" align="center">'+attrValue+'</td>'; 
				}
				
				//este es para el simbolo de la moneda
				if(attrValue == ''){
					html_reporte +='<td  align="right" id="simbolo_moneda">'+attrValue+'</td>'; 
				}
				
				if(attrValue == 'Por Vencer'){
					html_reporte +='<td  align="left" id="monto">'+attrValue+'</td>'; 
				}
				if(attrValue == '15 Dias'){
					html_reporte +='<td  align="left" id="monto">'+attrValue+'</td>'; 
				}
				if(attrValue == '30 Dias'){
					html_reporte +='<td  align="left" id="monto">'+attrValue+'</td>'; 
				}
				if(attrValue == '45 Dias'){
					html_reporte +='<td  align="left" id="monto">'+attrValue+'</td>'; 
				}
				if(attrValue == '60 Dias'){
					html_reporte +='<td  align="left" id="monto">'+attrValue+'</td>'; 
				}
				if(attrValue == '90 Dias'){
					html_reporte +='<td  align="left" id="monto">'+attrValue+'</td>'; 
				}
				if(attrValue == '+90 Dias'){
					html_reporte +='<td  align="left" id="monto">'+attrValue+'</td>'; 
				}
                                if(attrValue == 'Total'){
					html_reporte +='<td  align="left" id="monto">'+attrValue+'</td>'; 
				}

			}
			html_reporte +='</tr> </thead>';
			
			
			html_fila_vacia +='<tr>';
			html_fila_vacia +='<td width="90px" align="left" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="center" id="sin_borde"></td>';
			html_fila_vacia +='<td width="110px" align="left" id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='</tr>';
			/*
			html_fila_vacia +='<tr>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="110px" id="sin_borde"><input type="text" style="width:110px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_fila_vacia +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
			html_fila_vacia +='</tr>';
			*/
			
			//inicializar variables
			var suma_parcial_por_vencer=0.0;
			var suma_parcial_menor_igual_15=0.0;
			var suma_parcial_menor_igual_30=0.0;
			var suma_parcial_menor_igual_45=0.0;
			var suma_parcial_menor_igual_60=0.0;
			var suma_parcial_menor_igual_90=0.0;
			var suma_parcial_mayor_90=0.0;
                        
			var suma_parcial_saldo_factura_total=0.0;
                        

			
			var suma_total_por_vencer_mn=0.0;
			var suma_total_menor_igual_15_mn=0.0;
			var suma_total_menor_igual_30_mn=0.0;
			var suma_total_menor_igual_45_mn=0.0;
			var suma_total_menor_igual_60_mn=0.0;
			var suma_total_menor_igual_90_mn=0.0;
			var suma_total_mayor_90_mn=0.0;
                        
			var suma_total_saldo_factura_total_mn=0.0;
                        
			
			var suma_total_por_vencer_usd=0.0;
			var suma_total_menor_igual_15_usd=0.0;
			var suma_total_menor_igual_30_usd=0.0;
			var suma_total_menor_igual_45_usd=0.0;
			var suma_total_menor_igual_60_usd=0.0;
			var suma_total_menor_igual_90_usd=0.0;
			var suma_total_mayor_90_usd=0.0;
                        
			var suma_total_saldo_factura_total_usd=0.0;
			
			if(parseInt(body_tabla.length)>0){
				var clave_cliente_actual  = body_tabla[0]["clave_cliente"];
				var campo_cliente_actual = body_tabla[0]["cliente"];
				var campo_moneda_actual = body_tabla[0]["moneda_factura"];
				var simbolo_moneda_actual = body_tabla[0]["simbolo_moneda"];
				
				//pintar nombre del primer cliente
				html_reporte +='<tr id="tr_totales">';
				html_reporte +='<td width="90px" align="left" id="sin_borde">'+clave_cliente_actual+'</td>';
				html_reporte +='<td colspan="18">'+campo_cliente_actual+'</td>';
				html_reporte +='</tr>';
				
				
				
				for(var i=0; i<body_tabla.length; i++){
					
					if(body_tabla[i]["moneda_factura"]=="M.N."){
						suma_total_por_vencer_mn 	= parseFloat(suma_total_por_vencer_mn) + parseFloat(body_tabla[i]["por_vencer"]);
						suma_total_menor_igual_15_mn 	= parseFloat(suma_total_menor_igual_15_mn) + parseFloat(body_tabla[i]["menor_igual_15"]);
						suma_total_menor_igual_30_mn 	= parseFloat(suma_total_menor_igual_30_mn) + parseFloat(body_tabla[i]["menor_igual_30"]);
						suma_total_menor_igual_45_mn 	= parseFloat(suma_total_menor_igual_45_mn) + parseFloat(body_tabla[i]["menor_igual_45"]);
						suma_total_menor_igual_60_mn 	= parseFloat(suma_total_menor_igual_60_mn) + parseFloat(body_tabla[i]["menor_igual_60"]);
						suma_total_menor_igual_90_mn 	= parseFloat(suma_total_menor_igual_90_mn) + parseFloat(body_tabla[i]["menor_igual_90"]);
						suma_total_mayor_90_mn 		= parseFloat(suma_total_mayor_90_mn) + parseFloat(body_tabla[i]["mayor_90"]);
					   suma_total_saldo_factura_total_mn= parseFloat(suma_total_saldo_factura_total_mn) + parseFloat(body_tabla[i]["saldo_factura"]);
					}else{
						suma_total_por_vencer_usd 		= parseFloat(suma_total_por_vencer_usd) + parseFloat(body_tabla[i]["por_vencer"]);
						suma_total_menor_igual_15_usd 	= parseFloat(suma_total_menor_igual_15_usd) + parseFloat(body_tabla[i]["menor_igual_15"]);
						suma_total_menor_igual_30_usd 	= parseFloat(suma_total_menor_igual_30_usd) + parseFloat(body_tabla[i]["menor_igual_30"]);
						suma_total_menor_igual_45_usd 	= parseFloat(suma_total_menor_igual_45_usd) + parseFloat(body_tabla[i]["menor_igual_45"]);
						suma_total_menor_igual_60_usd 	= parseFloat(suma_total_menor_igual_60_usd) + parseFloat(body_tabla[i]["menor_igual_60"]);
						suma_total_menor_igual_90_usd 	= parseFloat(suma_total_menor_igual_90_usd) + parseFloat(body_tabla[i]["menor_igual_90"]);
						suma_total_mayor_90_usd 		= parseFloat(suma_total_mayor_90_usd) + parseFloat(body_tabla[i]["mayor_90"]);
						suma_total_saldo_factura_total_usd= parseFloat(suma_total_saldo_factura_total_usd) + parseFloat(body_tabla[i]["saldo_factura"]);
					}
					
					
					if( (clave_cliente_actual == body_tabla[i]["clave_cliente"]) && (simbolo_moneda_actual == body_tabla[i]["simbolo_moneda"]) ){
						html_reporte +='<tr>';
							html_reporte +='<td width="90px" align="left" >'+body_tabla[i]["factura"]+'</td>';
							html_reporte +='<td width="90px" align="center" >'+body_tabla[i]["fecha_facturacion"]+'</td>';
							html_reporte +='<td width="110px" align="center" >'+body_tabla[i]["fecha_vencimiento"]+'</td>';
							html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+  (( parseFloat(body_tabla[i]["por_vencer"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
							html_reporte +='<td width="90px" align="right" id="monto">'+  (( parseFloat(body_tabla[i]["por_vencer"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["por_vencer"]) )  +'</td>';
							html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["menor_igual_15"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )   +'</td>';
							html_reporte +='<td width="90px" align="right" id="monto">'+  (( parseFloat(body_tabla[i]["menor_igual_15"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_15"]) ) +'</td>';
							html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+  (( parseFloat(body_tabla[i]["menor_igual_30"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] ) +'</td>';
							html_reporte +='<td width="90px" align="right" id="monto">'+  (( parseFloat(body_tabla[i]["menor_igual_30"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_30"]) )  +'</td>';
							html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["menor_igual_45"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
							html_reporte +='<td width="90px" align="right" id="monto">'+   (( parseFloat(body_tabla[i]["menor_igual_45"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_45"]) )  +'</td>';
							html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["menor_igual_60"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
							html_reporte +='<td width="90px" align="right" id="monto">'+   (( parseFloat(body_tabla[i]["menor_igual_60"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_60"]))  +'</td>';
							html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["menor_igual_90"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
							html_reporte +='<td width="90px" align="right" id="monto">'+   (( parseFloat(body_tabla[i]["menor_igual_90"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_90"]) )  +'</td>';
							html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["mayor_90"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
							html_reporte +='<td width="90px" align="right" id="monto">'+   (( parseFloat(body_tabla[i]["mayor_90"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["mayor_90"]) )  +'</td>';
							html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["saldo_factura"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
							html_reporte +='<td width="90px" align="right" id="sin_borde">'+   (( parseFloat(body_tabla[i]["saldo_factura"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["saldo_factura"]) )  +'</td>';
						html_reporte +='</tr>';
						
					}else{
						
						html_reporte +='<tr id="tr_totales">';
						html_reporte +='<td width="90px" align="left" id="sin_borde"></td>';
						html_reporte +='<td width="90px" align="center" id="sin_borde"></td>';
						html_reporte +='<td width="110px" align="left" id="sin_borde">Total Cliente</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_por_vencer) <= 0 ) ? ''     :  simbolo_moneda_actual )+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_por_vencer) <= 0 ) ? ''            :  $(this).agregar_comas(suma_parcial_por_vencer.toFixed(2)) ) +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_15) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_15) <= 0 ) ? ''        :  $(this).agregar_comas(suma_parcial_menor_igual_15.toFixed(2)) ) +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_30) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_30) <= 0 ) ? ''        :  $(this).agregar_comas(suma_parcial_menor_igual_30.toFixed(2)) ) +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_45) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_45) <= 0 ) ? ''        :  $(this).agregar_comas(suma_parcial_menor_igual_45.toFixed(2)) ) +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_60) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_60) <= 0 ) ? ''        :  $(this).agregar_comas(suma_parcial_menor_igual_60.toFixed(2)) ) +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_90) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_90) <= 0 ) ? ''        :  $(this).agregar_comas(suma_parcial_menor_igual_90.toFixed(2)) ) +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_mayor_90) <= 0 ) ? ''       :  simbolo_moneda_actual )+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_mayor_90) <= 0 ) ? ''              :  $(this).agregar_comas(suma_parcial_mayor_90.toFixed(2)) ) +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_saldo_factura_total) <= 0 ) ? ''       :  simbolo_moneda_actual )+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_saldo_factura_total) <= 0 ) ? ''   :  $(this).agregar_comas(suma_parcial_saldo_factura_total.toFixed(2)) ) +'</td>';
						html_reporte +='</tr>';
						
						//inicializar variables
						clave_cliente_actual="";
						campo_cliente_actual="";
						simbolo_moneda_actual="";
						
						suma_parcial_por_vencer = 0.0;
						suma_parcial_menor_igual_15 = 0.0;
						suma_parcial_menor_igual_30 = 0.0;
						suma_parcial_menor_igual_45 = 0.0;
						suma_parcial_menor_igual_60 = 0.0;
						suma_parcial_menor_igual_90 = 0.0;
						suma_parcial_mayor_90 = 0.0;
                                                suma_parcial_saldo_factura_total= 0.0;
						
						//fila vacia
						html_reporte +=html_fila_vacia;
						
						//tomar datos del nuevo cliente 
						clave_cliente_actual  = body_tabla[i]["clave_cliente"];
						simbolo_moneda_actual = body_tabla[i]["simbolo_moneda"];
						campo_cliente_actual = body_tabla[i]["cliente"];
						
						//pintar nombre del siguiente cliente
						html_reporte +='<tr id="tr_totales">';
						html_reporte +='<td width="90px" align="left" id="sin_borde">'+clave_cliente_actual+'</td>';
						html_reporte +='<td  colspan="18">'+campo_cliente_actual+'</td>';
						html_reporte +='</tr>';
						
						html_reporte +='<tr>';
						html_reporte +='<td width="90px" align="left" >'+body_tabla[i]["factura"]+'</td>';
						html_reporte +='<td width="90px" align="center" >'+body_tabla[i]["fecha_facturacion"]+'</td>';
						html_reporte +='<td width="110px" align="center" >'+body_tabla[i]["fecha_vencimiento"]+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+  (( parseFloat(body_tabla[i]["por_vencer"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+  (( parseFloat(body_tabla[i]["por_vencer"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["por_vencer"]) )  +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["menor_igual_15"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )   +'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+  (( parseFloat(body_tabla[i]["menor_igual_15"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_15"]) ) +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+  (( parseFloat(body_tabla[i]["menor_igual_30"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] ) +'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+  (( parseFloat(body_tabla[i]["menor_igual_30"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_30"]) )  +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["menor_igual_45"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+   (( parseFloat(body_tabla[i]["menor_igual_45"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_45"]) )  +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["menor_igual_60"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+   (( parseFloat(body_tabla[i]["menor_igual_60"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_60"]))  +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["menor_igual_90"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+   (( parseFloat(body_tabla[i]["menor_igual_90"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["menor_igual_90"]) )  +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["mayor_90"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+   (( parseFloat(body_tabla[i]["mayor_90"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["mayor_90"]) )  +'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+   (( parseFloat(body_tabla[i]["saldo_factura"]) <= 0 ) ? '' :  body_tabla[i]["simbolo_moneda"] )  +'</td>';
						html_reporte +='<td width="90px" align="right" id="sin_borde">'+   (( parseFloat(body_tabla[i]["saldo_factura"]) <= 0 ) ? '' :  $(this).agregar_comas(body_tabla[i]["saldo_factura"]) )  +'</td>';
						html_reporte +='</tr>';
						
					}
					
					//suma parcial de elementos
					suma_parcial_por_vencer 		= parseFloat(suma_parcial_por_vencer) + parseFloat(body_tabla[i]["por_vencer"]);
					suma_parcial_menor_igual_15 	= parseFloat(suma_parcial_menor_igual_15) + parseFloat(body_tabla[i]["menor_igual_15"]);
					suma_parcial_menor_igual_30 	= parseFloat(suma_parcial_menor_igual_30) + parseFloat(body_tabla[i]["menor_igual_30"]);
					suma_parcial_menor_igual_45 	= parseFloat(suma_parcial_menor_igual_45) + parseFloat(body_tabla[i]["menor_igual_45"]);
					suma_parcial_menor_igual_60 	= parseFloat(suma_parcial_menor_igual_60) + parseFloat(body_tabla[i]["menor_igual_60"]);
					suma_parcial_menor_igual_90 	= parseFloat(suma_parcial_menor_igual_90) + parseFloat(body_tabla[i]["menor_igual_90"]);
					suma_parcial_mayor_90 			= parseFloat(suma_parcial_mayor_90) + parseFloat(body_tabla[i]["mayor_90"]);
                                        
					suma_parcial_saldo_factura_total= parseFloat(suma_parcial_saldo_factura_total) + parseFloat(body_tabla[i]["saldo_factura"]);
				}
				

				//imprimir total del ultimo cliente 
				html_reporte +='<tr id="tr_totales">';
				html_reporte +='<td width="150px" align="left" id="sin_borde"></td>';
				html_reporte +='<td width="90px" align="center" id="sin_borde"></td>';
				html_reporte +='<td width="110px" align="left" id="sin_borde">Total Cliente</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_por_vencer) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_por_vencer) <= 0 ) ? '' :  $(this).agregar_comas(suma_parcial_por_vencer.toFixed(2)) ) +'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_15) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_15) <= 0 ) ? '' :  $(this).agregar_comas(suma_parcial_menor_igual_15.toFixed(2)) ) +'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_30) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_30) <= 0 ) ? '' :  $(this).agregar_comas(suma_parcial_menor_igual_30.toFixed(2)) ) +'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_45) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_45) <= 0 ) ? '' :  $(this).agregar_comas(suma_parcial_menor_igual_45.toFixed(2)) ) +'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_60) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_60) <= 0 ) ? '' :  $(this).agregar_comas(suma_parcial_menor_igual_60.toFixed(2)) ) +'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_menor_igual_90) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_menor_igual_90) <= 0 ) ? '' :  $(this).agregar_comas(suma_parcial_menor_igual_90.toFixed(2)) ) +'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_mayor_90) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+ (( parseFloat(suma_parcial_mayor_90) <= 0 ) ? '' :  $(this).agregar_comas(suma_parcial_mayor_90.toFixed(2)) ) +'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+(( parseFloat(suma_parcial_saldo_factura_total) <= 0 ) ? '' :  simbolo_moneda_actual )+'</td>';
				html_reporte +='<td width="90px" align="right"  id="sin_borde">'+ (( parseFloat(suma_parcial_saldo_factura_total) <= 0 ) ? '' :  $(this).agregar_comas(suma_parcial_saldo_factura_total.toFixed(2)) ) +'</td>';
				html_reporte +='</tr>';
				
			}
			
			
			//fila vacia
			html_reporte +=html_fila_vacia;
			
			//imprimir totales en pesos
			html_footer +='<tr id="tr_totales">';
				html_footer +='<td width="150px" align="left" id="sin_borde"></td>';
				html_footer +='<td width="90px" align="center" id="sin_borde"></td>';
				html_footer +='<td width="110px" align="left" id="sin_borde">Total en MN</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_por_vencer_mn.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_15_mn.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_30_mn.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_45_mn.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_60_mn.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_90_mn.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_mayor_90_mn.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_saldo_factura_total_mn.toFixed(2))  +'</td>';
			html_footer +='</tr>';
			
			
			
			//fila vacia
			html_reporte +=html_fila_vacia;
			
			/*
			html_footer +='<tr>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="110px" id="sin_borde"><input type="text" style="width:110px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="10px" id="sin_borde"><input type="text" style="width:10px; height:0px; border-color:transparent; background:transparent;"></td>';
				html_footer +='<td width="90px" id="sin_borde"><input type="text" style="width:90px; height:0px; border-color:transparent; background:transparent;"></td>';
			html_footer +='</tr>';
			*/
			
			//imprimir totales en dolares
			html_footer +='<tr id="tr_totales">';
				html_footer +='<td width="90px" align="left" id="sin_borde"></td>';
				html_footer +='<td width="90px" align="center" id="sin_borde"></td>';
				html_footer +='<td width="110px" align="left" id="sin_borde">Total en USD</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_por_vencer_usd.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_15_usd.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_30_usd.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_45_usd.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_60_usd.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_menor_igual_90_usd.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_mayor_90_usd.toFixed(2))  +'</td>';
				html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
				html_footer +='<td width="90px" align="right"  id="monto">'+ $(this).agregar_comas(suma_total_saldo_factura_total_usd.toFixed(2))  +'</td>';
			html_footer +='</tr>';
			
			html_reporte +='<tfoot>';
				html_reporte += html_footer;
			html_reporte +='</tfoot>';
			
			html_reporte += '</table>';
			
			
			
			$div_reporte.append(html_reporte); 
			
			var height2 = $('#cuerpo').css('height');
			var alto = parseInt(height2)-300;
			var pix_alto=alto+'px';
			$('#reporte').tableScroll({height:parseInt(pix_alto)});
		});          
	});
	
	$aplicar_evento_keypress($select_tipo_reporte, $busqueda_reporte);
	$aplicar_evento_keypress($fecha_corte, $busqueda_reporte);
	$aplicar_evento_keypress($razon_cliente, $busqueda_reporte);
	
	$select_tipo_reporte.focus();
	
});
