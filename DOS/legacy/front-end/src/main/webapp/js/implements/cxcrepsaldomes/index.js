$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Saldos por Mes - Cuentas por Cobrar' ,                 
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
			return this.contextpath + "/controllers/cxcrepsaldomes";
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
	
	//var $cliente = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=cliente]');
	var $select_tipo_reporte = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=tipo_reporte]');
	var $select_ano = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_ano]');
	var $select_mes = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_mes]');
	var $id_cliente = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=id_cliente]');
	var $razon_cliente = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=razon_cliente]');
	var $buscar_cliente= $('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=buscar_cliente]');
	var $genera_PDF = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $busqueda_reporte= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_reporte_estados_de_cuenta= $('#lienzo_recalculable').find('#divreporteedocta');
	
	$razon_cliente.attr('readonly',true);
	$razon_cliente.css({'background' : '#DDDDDD'});
	$buscar_cliente.hide();
	
	$select_tipo_reporte.children().remove();
	html='<option value="0">General</option>';
	html+='<option value="1">Por Cliente</option>';
	$select_tipo_reporte.append(html);
	
	var array_meses = {0:"- Seleccionar -",  1:"Enero",  2:"Febrero", 3:"Marzo", 4:"Abirl", 5:"Mayo", 6:"Junio", 7:"Julio", 8:"Agosto", 9:"Septiembre", 10:"Octubre", 11:"Noviembre", 12:"Diciembre"};
	
	
	$select_tipo_reporte.change(function(){
		if(parseInt($(this).val())==0){
			$razon_cliente.css({'background' : '#DDDDDD'});
			$razon_cliente.attr('readonly',true);
			$buscar_cliente.hide();
			$razon_cliente.val('');
			$id_cliente.val(0);
		}else{
			$razon_cliente.css({'background' : '#ffffff'});
			$buscar_cliente.show();
			$razon_cliente.attr('readonly',false);
		}
	});
	


	 var arreglo_parametros = { 
		iu:config.getUi()
	 };
		
	var restful_json_service = config.getUrlForGetAndPost() + '/getDatos.json';
	$.post(restful_json_service,arreglo_parametros,function(entry){
		//carga select de años
		$select_ano.children().remove();
		var html_anio = '';
		$.each(entry['Anios'],function(entryIndex,anio){
			if(parseInt(anio['valor']) == parseInt(entry['Dato'][0]['anioActual']) ){
				html_anio += '<option value="' + anio['valor'] + '" selected="yes">' + anio['valor'] + '</option>';
			}else{
				html_anio += '<option value="' + anio['valor'] + '"  >' + anio['valor'] + '</option>';
			}
		});
		$select_ano.append(html_anio);
		
		//cargar select del Mes inicial
		$select_mes.children().remove();
		var select_html = '';
		for(var i in array_meses){
			if(parseInt(i) == parseInt(entry['Dato'][0]['mesActual']) ){
				select_html += '<option value="' + i + '" selected="yes">' + array_meses[i] + '</option>';	
			}else{
				select_html += '<option value="' + i + '"  >' + array_meses[i] + '</option>';	
			}
		}
		$select_mes.append(select_html);
	});




	//Buscador de clientes
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
		
		//Click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			$tabla_resultados.children().remove();
			
			var arreglo_parametros = {	cadena: $cadena_buscar.val(),
										filtro: $select_filtro_por.val(),
										iu:config.getUi()
									};
			var restful_json_service = config.getUrlForGetAndPost() + '/getBuscadorClientes.json'
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
				});
			});//termina llamada json
		});
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $busca_cliente_modalbox);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $busca_cliente_modalbox);
		
		$cancelar_plugin_busca_cliente.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
		});
	}//termina buscador de clientes
	
	
	
	
    $buscar_cliente.click(function(event){
        //alert("aqui ando");
        event.preventDefault();
        $busca_clientes();//llamada a la funcion que busca clientees
    });
    
    
	
	//genera pdf del reporte de estados de cuenta de clientees
	$genera_PDF.click(function(event){
		event.preventDefault();
		var cliente = '0';
		if($razon_cliente.val().trim()!=''){
			cliente=$razon_cliente.val();
		}
		
		var cadena = $select_tipo_reporte.val()+"___"+$select_ano.val()+"___"+$select_mes.val()+"___"+cliente;
		
		//var id_cliente=$id_cliente.val();
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		var input_json = config.getUrlForGetAndPost() + '/getPdfSaldoMensual/'+cadena+'/'+iu+'/out.json'
		window.location.href=input_json;
		
	});//termina llamada json
	
	
	
	$busqueda_reporte.click(function(event){
		event.preventDefault();
		$div_reporte_estados_de_cuenta.children().remove();
			
			var arreglo_parametros = {	
										tipo_reporte: $select_tipo_reporte.val(),
										cliente: $razon_cliente.val(),
										anio_corte: $select_ano.val(),
										mes_corte: $select_mes.val(),
										iu:config.getUi()
									};
			
			var restful_json_service = config.getUrlForGetAndPost() + '/getReporteSaldos.json'
			var proveedoor="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				var body_tabla = entry['Facturas'];
				var header_tabla = {
					serie_folio			:'Factura',
					fecha_factura		:'Fecha',
					orden_compra		:'O. Compra',
					moneda_total		:'',
					monto_factura		:'Monto Facturado',
					moneda_pagado		:'',
					importe_pagado  	:'Monto Pagado',
					//ultimo_pago    		:'Ultimo Pago',
					moneda_saldo    	:'',
					saldo_factura    	:'Saldo Pendiente'
				};
                    
				var html_reporte = '<table id="edocta">';
				var html_fila_vacia='';
				var html_footer = '';
				
				html_reporte +='<thead> <tr>';
				for(var key in header_tabla){
					var attrValue = header_tabla[key];
					if(attrValue == "Factura"){
						html_reporte +='<td width="120px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Fecha"){
						html_reporte +='<td width="100px" align="center">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "O. Compra"){
						html_reporte +='<td width="140px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == ''){
						html_reporte +='<td width="10px" align="right" >'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Monto Facturado"){
						html_reporte +='<td width="130px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Monto Pagado"){
						html_reporte +='<td width="130px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					/*
					if(attrValue == "Ultimo Pago"){
						html_reporte +='<td width="90px" align="center">'+attrValue+'</td>'; 
					}
					*/
					if(attrValue == "Saldo Pendiente"){
						html_reporte +='<td width="130px" align="left" id="monto">'+attrValue+'</td>'; 
					}

				}
				html_reporte +='</tr> </thead>';
				
				html_fila_vacia +='<tr>';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="120px" height="10"></td>';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="100px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="140px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="10px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="10px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="10px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
				html_fila_vacia +='</tr>';
				
				var orden_compra="";
				var simbolo_moneda="";
				var cliente_actual="";
				
                //inicializar variables
                var suma_monto_factura_cliente=0.0;
                var suma_importe_pagado_cliente=0.0;
                var suma_saldo_pendiente_cliente=0.0;
                
                var simbolo_moneda_pesos="";
                var suma_monto_factura_moneda_pesos=0.0;
                var suma_importe_pagado_moneda_pesos=0.0;
                var suma_saldo_pendiente_moneda_pesos=0.0;
				
				var simbolo_moneda_dolar="";
                var suma_monto_factura_moneda_dolar=0.0;
                var suma_importe_pagado_moneda_dolar=0.0;
                var suma_saldo_pendiente_moneda_dolar=0.0;
                
                var simbolo_moneda_euro="";
                var suma_monto_factura_moneda_euro=0.0;
                var suma_importe_pagado_moneda_euro=0.0;
                var suma_saldo_pendiente_moneda_euro=0.0;
				
				if(parseInt(body_tabla.length)>0){
					cliente_actual=body_tabla[0]["cliente"];
					simbolo_moneda=body_tabla[0]["moneda_simbolo"];
					
					html_reporte +='<tr id="tr_totales" class="first"><td align="left" colspan="11">'+cliente_actual+'</td></tr>';
					
					for(var i=0; i<body_tabla.length; i++){
						if(body_tabla[i]["orden_compra"]==null){
							orden_compra="";
						}else{
							orden_compra=body_tabla[i]["orden_compra"];
						}
						
						if(cliente_actual==body_tabla[i]["cliente"] && simbolo_moneda==body_tabla[i]["moneda_simbolo"]){
							html_reporte +='<tr>';
							html_reporte +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
							html_reporte +='<td align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
							html_reporte +='<td align="left" >'+orden_compra+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_factura"]).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe_pagado"]).toFixed(2))+'</td>';
							//html_reporte +='<td align="center" >'+body_tabla[i]["ultimo_pago"]+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["saldo_factura"]).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							suma_monto_factura_cliente=parseFloat(suma_monto_factura_cliente) + parseFloat(body_tabla[i]["monto_factura"]);
							suma_importe_pagado_cliente=parseFloat(suma_importe_pagado_cliente) + parseFloat(body_tabla[i]["importe_pagado"]);
							suma_saldo_pendiente_cliente=parseFloat(suma_saldo_pendiente_cliente) + parseFloat(body_tabla[i]["saldo_factura"]);
							
							//pesos
							if(parseInt(body_tabla[i]["moneda_id"])==1){
								suma_monto_factura_moneda_pesos=parseFloat(suma_monto_factura_moneda_pesos) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_pesos=parseFloat(suma_importe_pagado_moneda_pesos) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_pesos=parseFloat(suma_saldo_pendiente_moneda_pesos) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_pesos=body_tabla[i]["moneda_simbolo"];
							}
							
							//dolares
							if(parseInt(body_tabla[i]["moneda_id"])==2){
								suma_monto_factura_moneda_dolar=parseFloat(suma_monto_factura_moneda_dolar) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_dolar=parseFloat(suma_importe_pagado_moneda_dolar) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_dolar=parseFloat(suma_saldo_pendiente_moneda_dolar) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_dolar=body_tabla[i]["moneda_simbolo"];
							}
							
							//euros
							if(parseInt(body_tabla[i]["moneda_id"])==3){
								suma_monto_factura_moneda_euro=parseFloat(suma_monto_factura_moneda_euro) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_euro=parseFloat(suma_importe_pagado_moneda_euro) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_euro=parseFloat(suma_saldo_pendiente_moneda_euro) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_euro=body_tabla[i]["moneda_simbolo"];
							}
						}else{
							//imprimir totales
							html_reporte +='<tr id="tr_totales">';
							html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="right" id="sin_borde">Total cliente</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_cliente).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_cliente).toFixed(2))+'</td>';
							//html_reporte +='<td align="left" ></td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_cliente).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							//fila vacia
							html_reporte +=html_fila_vacia;
							
							//reinicializar varibles
							suma_monto_factura_cliente=0.0;
							suma_importe_pagado_cliente=0.0;
							suma_saldo_pendiente_cliente=0.0;
							
							//tomar razon social de nuevo prov
							cliente_actual=body_tabla[i]["cliente"];
							simbolo_moneda=body_tabla[i]["moneda_simbolo"]
							
							html_reporte +='<tr id="tr_totales"><td align="left" colspan="12">'+cliente_actual+'</td></tr>';
							//crear primer registro del nuevo prov
							html_reporte +='<tr>';
							html_reporte +='<td align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
							html_reporte +='<td align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
							html_reporte +='<td align="left" >'+orden_compra+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_factura"]).toFixed(2))+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe_pagado"]).toFixed(2))+'</td>';
							//html_reporte +='<td align="center" >'+body_tabla[i]["ultimo_pago"]+'</td>';
							html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["saldo_factura"]).toFixed(2))+'</td>';
							html_reporte +='</tr>';
							
							//sumar montos del nuevo prov
							suma_monto_factura_cliente=parseFloat(suma_monto_factura_cliente) + parseFloat(body_tabla[i]["monto_factura"]);
							suma_importe_pagado_cliente=parseFloat(suma_importe_pagado_cliente) + parseFloat(body_tabla[i]["importe_pagado"]);
							suma_saldo_pendiente_cliente=parseFloat(suma_saldo_pendiente_cliente) + parseFloat(body_tabla[i]["saldo_factura"]);
							
							//pesos
							if(parseInt(body_tabla[i]["moneda_id"])==1){
								suma_monto_factura_moneda_pesos=parseFloat(suma_monto_factura_moneda_pesos) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_pesos=parseFloat(suma_importe_pagado_moneda_pesos) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_pesos=parseFloat(suma_saldo_pendiente_moneda_pesos) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_pesos=body_tabla[i]["moneda_simbolo"];
							}
							
							//dolares
							if(parseInt(body_tabla[i]["moneda_id"])==2){
								suma_monto_factura_moneda_dolar=parseFloat(suma_monto_factura_moneda_dolar) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_dolar=parseFloat(suma_importe_pagado_moneda_dolar) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_dolar=parseFloat(suma_saldo_pendiente_moneda_dolar) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_dolar=body_tabla[i]["moneda_simbolo"];
							}
							
							//euros
							if(parseInt(body_tabla[i]["moneda_id"])==3){
								suma_monto_factura_moneda_euro=parseFloat(suma_monto_factura_moneda_euro) + parseFloat(body_tabla[i]["monto_factura"]);
								suma_importe_pagado_moneda_euro=parseFloat(suma_importe_pagado_moneda_euro) + parseFloat(body_tabla[i]["importe_pagado"]);
								suma_saldo_pendiente_moneda_euro=parseFloat(suma_saldo_pendiente_moneda_euro) + parseFloat(body_tabla[i]["saldo_factura"]);
								simbolo_moneda_euro=body_tabla[i]["moneda_simbolo"];
							}

						}
					}
					//imprimir total del ultimo prov
					html_reporte +='<tr id="tr_totales">';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
					html_reporte +='<td align="left" id="sin_borde"></td>';
					html_reporte +='<td align="right" id="sin_borde">Total cliente</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_cliente).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_cliente).toFixed(2))+'</td>';
					//html_reporte +='<td align="left" ></td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>';
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_cliente).toFixed(2))+'</td>';
					html_reporte +='</tr>';
					
					
					//imprimir totales de la moneda PESOS
					html_footer +='<tr id="tr_totales">';
					html_footer +='<td align="left" id="sin_borde_derecho"></td>';
					html_footer +='<td align="left" id="sin_borde"></td>';
					html_footer +='<td align="right" id="sin_borde">Total MN</td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_pesos+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_moneda_pesos).toFixed(2))+'</td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_pesos+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_moneda_pesos).toFixed(2))+'</td>';
					//html_footer +='<td align="left" ></td>';
					html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_pesos+'</td>';
					html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_moneda_pesos).toFixed(2))+'</td>';
					html_footer +='</tr>';
					
					if(parseFloat(suma_saldo_pendiente_moneda_dolar) > 0){
						//imprimir totales de la moneda DOLARES
						html_footer +='<tr id="tr_totales">';
						html_footer +='<td align="left" id="sin_borde_derecho"></td>';
						html_footer +='<td align="left" id="sin_borde"></td>';
						html_footer +='<td align="right" id="sin_borde">Total USD</td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_dolar+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_moneda_dolar).toFixed(2))+'</td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_dolar+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_moneda_dolar).toFixed(2))+'</td>';
						//html_footer +='<td align="left" ></td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_dolar+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_moneda_dolar).toFixed(2))+'</td>';
						html_footer +='</tr>';
					}
					
					if(parseFloat(suma_saldo_pendiente_moneda_euro) > 0){
						//Imprimir totales de la moneda EUROS
						html_footer +='<tr id="tr_totales">';
						html_footer +='<td align="left" id="sin_borde_derecho"></td>';
						html_footer +='<td align="left" id="sin_borde"></td>';
						html_footer +='<td align="right" id="sin_borde">Total EUR</td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_euro+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_monto_factura_moneda_euro).toFixed(2))+'</td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_euro+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_importe_pagado_moneda_euro).toFixed(2))+'</td>';
						//html_footer +='<td align="left" ></td>';
						html_footer +='<td align="right" id="simbolo_moneda">'+simbolo_moneda_euro+'</td>';
						html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_saldo_pendiente_moneda_euro).toFixed(2))+'</td>';
						html_footer +='</tr>';
					}
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
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_reporte, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_ano, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_mes, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($razon_cliente, $busqueda_reporte);
	
});   
        
        
        
        
    
