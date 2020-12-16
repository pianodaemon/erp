$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Antig&uuml;edad de Saldos de Cuentas por Pagar' ,                 
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
			return this.contextpath + "/controllers/cxprepantiguedadsaldos";
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
	
	var $proveedor = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=proveedor]');
	var $fecha_corte = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_corte]');
	
	var $buscar_proveedor= $('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=buscar_proveedor]');
	
	var $genera_pdf_reporte = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $busqueda_reporte= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_reporte= $('#lienzo_recalculable').find('#divreporte');
	
	
	
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
			
	
	$fecha_corte.val(mostrarFecha());
    
    


	//buscador de proveedores
	$busca_proveedores = function(){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
		var $campo_email = $('#forma-buscaproveedor-window').find('input[name=campo_email]');
		var $campo_nombre = $('#forma-buscaproveedor-window').find('input[name=campo_nombre]');
		
		var $buscar_plugin_proveedor = $('#forma-buscaproveedor-window').find('#busca_proveedor_modalbox');
		var $cancelar_plugin_busca_proveedor = $('#forma-buscaproveedor-window').find('#cencela');
			
		$('#forma-entradamercancias-window').find('input[name=tipo_proveedor]').val('');
			
		//funcionalidad botones
		$buscar_plugin_proveedor.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_proveedor.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_proveedor.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_proveedor.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
					
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			//event.preventDefault();
			var restful_json_service = config.getUrlForGetAndPost() + '/getBuscaProveedores.json'
			$arreglo = {    rfc:$campo_rfc.val(),
							email:$campo_email.val(),
							nombre:$campo_nombre.val(),
							iu:config.getUi()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(restful_json_service,$arreglo,function(entry){
				$.each(entry['Proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							trr += '<span class="rfc">'+proveedor['rfc']+'</span>';
						trr += '</td>';
						trr += '<td width="250"><span id="razon_social">'+proveedor['razon_social']+'</span></td>';
						trr += '<td width="250"><span class="direccion">'+proveedor['direccion']+'</span></td>';
					trr += '</tr>';
					
					$tabla_resultados.append(trr);
				});
				$tabla_resultados.find('tr:odd').find('td').css({ 'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({ 'background-color' : '#FFFFFF'});

				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({ background : '#FBD850'});
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
					//asigna la razon social del proveedor al campo correspondiente
					$proveedor.val($(this).find('#razon_social').html());
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
				});
			});
		});
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
		});
	}//termina buscador de proveedores



	
	
   $buscar_proveedor.click(function(event){
        event.preventDefault();
        $busca_proveedores();//llamada a la funcion que busca proveedores
    });



    
	//click generar pdf
	$genera_pdf_reporte.click(function(event){
		event.preventDefault();
		var cadena = $proveedor.val()+"___"+$fecha_corte.val();
		var input_json = config.getUrlForGetAndPost() + '/getPdfReporteAntiguedadSaldosCxp/'+cadena+'/'+config.getUi()+'/out.json';
		window.location.href=input_json;
	});
		
	
	$busqueda_reporte.click(function(event){
		event.preventDefault();
		$div_reporte.children().remove();
			
		var arreglo_parametros = {	proveedor: $proveedor.val(),
									fecha_corte: $fecha_corte.val(),
									iu:config.getUi()
								};
		
		var restful_json_service = config.getUrlForGetAndPost() + '/getReporteAntiguedadSaldosCxp.json'
		$.post(restful_json_service,arreglo_parametros,function(entry){
			var body_tabla = entry['Facturas'];
			
			//var footer_tabla = entry['Totales'];
			var header_tabla = {
				clave_proveedor			:'Clave',
				proveedor				:'Proveedor',				
				serie_folio				:'Factura',
				fecha_factura			:'Fecha Factura',
				fecha_vencimiento		:'Fecha Vencimiento',
				moneda_por_vencer		:'',
				por_vencer				:'Por Vencer',
				moneda_30_dias			:'',
				de_1_a_30_dias			:'30 Dias',
				moneda_60_dias			:'',
				de_31_a_60_dias			:'60 Dias',
				moneda_90_dias			:'',
				de_61_dias_en_adelante	:'90+ Dias',
			};
			
			
			var html_reporte = '<table id="reporte">';
			var html_fila_vacia='';
			var html_footer = '';
			
			html_reporte +='<thead> <tr>';
			for(var key in header_tabla){
				var attrValue = header_tabla[key];
				
				if(attrValue == 'Clave'){
					html_reporte +='<td width="80px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == 'Proveedor'){
					html_reporte +='<td width="250px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == 'Factura'){
					html_reporte +='<td width="90px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == 'Fecha Factura'){
					html_reporte +='<td width="90px" align="center">'+attrValue+'</td>'; 
				}
				if(attrValue == 'Fecha Vencimiento'){
					html_reporte +='<td width="110px" align="center">'+attrValue+'</td>'; 
				}
				
				//este es para el simbolo de la moneda
				if(attrValue == ''){
					html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+attrValue+'</td>'; 
				}
				
				if(attrValue == 'Por Vencer'){
					html_reporte +='<td width="90px" align="left" id="monto">'+attrValue+'</td>'; 
				}
				
				if(attrValue == '30 Dias'){
					html_reporte +='<td width="90px" align="left" id="monto">'+attrValue+'</td>'; 
				}
				
				if(attrValue == '60 Dias'){
					html_reporte +='<td width="90px" align="left" id="monto">'+attrValue+'</td>'; 
				}
				
				if(attrValue == '90+ Dias'){
					html_reporte +='<td width="90px" align="left" id="monto">'+attrValue+'</td>'; 
				}

			}
			html_reporte +='</tr> </thead>';
			
			
			html_fila_vacia +='<tr>';
			html_fila_vacia +='<td width="80px" align="left"  id="sin_borde" height="13"></td>';
			html_fila_vacia +='<td width="250px" align="left"  id="sin_borde"></td>';
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
			html_fila_vacia +='</tr>';
			
			
			var simbolo_moneda="";
			var simbolo_por_vencer = "";
			var simbolo_de_1_a_30_dias = "";
			var simbolo_de_31_a_60_dias = "";
			var simbolo_de_61_dias_en_adelante = "";
			
			//inicializar variables
			var suma_parcial_por_vencer=0.0;
			var suma_parcial_30_dias=0.0;
			var suma_parcial_60_dias=0.0;
			var suma_parcial_90_dias=0.0;
					
			var suma_total_por_vencer_mn=0.0;
			var suma_total_30_dias_mn=0.0;
			var suma_total_60_dias_mn=0.0;
			var suma_total_90_dias_mn=0.0;
					
			var suma_total_por_vencer_usd=0.0;	
			var suma_total_30_dias_usd=0.0;
			var suma_total_60_dias_usd=0.0;
			var suma_total_90_dias_usd=0.0;
			
			if(parseInt(body_tabla.length)>0){
				
				var campo_proveedor_actual="";
				var campo_moneda_actual="";
				var campo_por_vencer = "";
				var campo_de_1_a_30_dias = "";
				var campo_de_31_a_60_dias = "";
				var campo_de_61_dias_en_adelante = "";
				
				
				campo_proveedor_actual = body_tabla[0]["proveedor"];
				campo_moneda_actual = body_tabla[0]["moneda_factura"];
				simbolo_moneda_actual = body_tabla[0]["moneda_factura"];
				
				
				if(body_tabla[0]["moneda_factura"]=="M.N."){
					simbolo_moneda="$";
				}else{
					simbolo_moneda=body_tabla[0]["moneda_factura"];
				}
				
				for(var i=0; i<body_tabla.length; i++){
					if(body_tabla[i]["moneda_factura"]=="M.N."){
						suma_total_por_vencer_mn = parseFloat(suma_total_por_vencer_mn) + parseFloat(body_tabla[i]["por_vencer"]);
						suma_total_30_dias_mn = parseFloat(suma_total_30_dias_mn) + parseFloat(body_tabla[i]["de_1_a_30_dias"]);
						suma_total_60_dias_mn = parseFloat(suma_total_60_dias_mn) + parseFloat(body_tabla[i]["de_31_a_60_dias"]);
						suma_total_90_dias_mn = parseFloat(suma_total_90_dias_mn) + parseFloat(body_tabla[i]["de_61_dias_en_adelante"]);
					}else{
						suma_total_por_vencer_usd = parseFloat(suma_total_por_vencer_usd) + parseFloat(body_tabla[i]["por_vencer"]);
						suma_total_30_dias_usd = parseFloat(suma_total_30_dias_usd) + parseFloat(body_tabla[i]["de_1_a_30_dias"]);
						suma_total_60_dias_usd = parseFloat(suma_total_60_dias_usd) + parseFloat(body_tabla[i]["de_31_a_60_dias"]);
						suma_total_90_dias_usd = parseFloat(suma_total_90_dias_usd) + parseFloat(body_tabla[i]["de_61_dias_en_adelante"]);
					}
					
					
					
					if( (campo_proveedor_actual == body_tabla[i]["proveedor"]) && (campo_moneda_actual == body_tabla[i]["moneda_factura"]) ){
						
						//si los campos vienen  co cero asignarle espacio en blanco
						if(parseFloat(body_tabla[i]["por_vencer"])>0){
							campo_por_vencer = $(this).agregar_comas(parseFloat(body_tabla[i]["por_vencer"]).toFixed(2));
							simbolo_por_vencer = simbolo_moneda;
						}else{
							campo_por_vencer="";
							simbolo_por_vencer = "";
						}
						
						if(parseFloat(body_tabla[i]["de_1_a_30_dias"])>0){
							campo_de_1_a_30_dias = $(this).agregar_comas(parseFloat(body_tabla[i]["de_1_a_30_dias"]).toFixed(2));
							simbolo_de_1_a_30_dias = simbolo_moneda;
						}else{
							campo_de_1_a_30_dias ="";
							simbolo_de_1_a_30_dias="";
						}
						
						if(parseFloat(body_tabla[i]["de_31_a_60_dias"])>0){
							campo_de_31_a_60_dias = $(this).agregar_comas(parseFloat(body_tabla[i]["de_31_a_60_dias"]).toFixed(2));
							simbolo_de_31_a_60_dias = simbolo_moneda;
						}else{
							campo_de_31_a_60_dias="";
							simbolo_de_31_a_60_dias="";
						}
						
						if(parseFloat(body_tabla[i]["de_61_dias_en_adelante"])>0){
							campo_de_61_dias_en_adelante = $(this).agregar_comas(parseFloat(body_tabla[i]["de_61_dias_en_adelante"]).toFixed(2));
							simbolo_de_61_dias_en_adelante = simbolo_moneda;
						}else{
							campo_de_61_dias_en_adelante="";
							simbolo_de_61_dias_en_adelante="";
						}
						html_reporte +='<tr>';
						html_reporte +='<td width="80px" align="left" >'+body_tabla[i]["clave_proveedor"]+'</td>';
						html_reporte +='<td width="250px" align="left" >'+body_tabla[i]["proveedor"]+'</td>';
						html_reporte +='<td width="90px" align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
						html_reporte +='<td width="90px" align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
						html_reporte +='<td width="110px" align="center" >'+body_tabla[i]["fecha_vencimiento"]+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_por_vencer+'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+campo_por_vencer+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_1_a_30_dias+'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+campo_de_1_a_30_dias+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_31_a_60_dias+'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+campo_de_31_a_60_dias+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_61_dias_en_adelante+'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+campo_de_61_dias_en_adelante+'</td>';
						html_reporte +='</tr>';
					}else{
						//si los campos vienen  co cero asignarle espacio en blanco
						if(parseFloat(suma_parcial_por_vencer)>0){
							suma_parcial_por_vencer = $(this).agregar_comas(parseFloat(suma_parcial_por_vencer).toFixed(2));
							simbolo_por_vencer = simbolo_moneda;
						}else{
							suma_parcial_por_vencer="";
							simbolo_por_vencer = "";
						}
						
						if(parseFloat(suma_parcial_30_dias)>0){
							suma_parcial_30_dias = $(this).agregar_comas(parseFloat(suma_parcial_30_dias).toFixed(2));
							simbolo_de_1_a_30_dias = simbolo_moneda;
						}else{
							suma_parcial_30_dias="";
							simbolo_de_1_a_30_dias="";
						}
						
						if(parseFloat(suma_parcial_60_dias)>0){
							suma_parcial_60_dias = $(this).agregar_comas(parseFloat(suma_parcial_60_dias).toFixed(2));
							simbolo_de_31_a_60_dias = simbolo_moneda;
						}else{
							suma_parcial_60_dias="";
							simbolo_de_31_a_60_dias="";
						}
						
						if(parseFloat(suma_parcial_90_dias)>0){
							suma_parcial_90_dias = $(this).agregar_comas(parseFloat(suma_parcial_90_dias).toFixed(2));
							simbolo_de_61_dias_en_adelante = simbolo_moneda;
						}else{
							suma_parcial_90_dias="";
							simbolo_de_61_dias_en_adelante="";
						}
							
						
						html_reporte +='<tr id="tr_totales">';
						html_reporte +='<td width="80px" align="left"  id="sin_borde_derecho"></td>';
						html_reporte +='<td width="250px" align="left"  id="sin_borde"></td>';
						html_reporte +='<td width="90px" align="left" id="sin_borde"></td>';
						html_reporte +='<td width="90px" align="center" id="sin_borde"></td>';
						html_reporte +='<td width="110px" align="left" id="sin_borde">Total Proveedor</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_por_vencer+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+suma_parcial_por_vencer+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_1_a_30_dias+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+suma_parcial_30_dias+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_31_a_60_dias+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+suma_parcial_60_dias+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_61_dias_en_adelante+'</td>';
						html_reporte +='<td width="90px" align="right"  id="monto">'+suma_parcial_90_dias+'</td>';
						html_reporte +='</tr>';
						
						
						//inicializar variables
						campo_proveedor_actual="";
						campo_moneda_actual="";
						
						suma_parcial_por_vencer = 0.0;
						suma_parcial_30_dias = 0.0;
						suma_parcial_60_dias = 0.0;
						suma_parcial_90_dias = 0.0;
						
						//fila vacia
						html_reporte +=html_fila_vacia;
						
						
						//tomar razon social del proveedor
						campo_proveedor_actual = body_tabla[i]["proveedor"];
						campo_moneda_actual = body_tabla[i]["moneda_factura"];
						
						if(body_tabla[i]["moneda_factura"]=="M.N."){
							simbolo_moneda="$";
						}else{
							simbolo_moneda=body_tabla[i]["moneda_factura"];
						}
						
						//si los campos vienen  co cero asignarle espacio en blanco
						if(parseFloat(body_tabla[i]["por_vencer"])>0){
							campo_por_vencer = $(this).agregar_comas(parseFloat(body_tabla[i]["por_vencer"]).toFixed(2));
							simbolo_por_vencer = simbolo_moneda;
						}else{
							campo_por_vencer="";
							simbolo_por_vencer = "";
						}
						
						if(parseFloat(body_tabla[i]["de_1_a_30_dias"])>0){
							campo_de_1_a_30_dias = $(this).agregar_comas(parseFloat(body_tabla[i]["de_1_a_30_dias"]).toFixed(2));
							simbolo_de_1_a_30_dias = simbolo_moneda;
						}else{
							campo_de_1_a_30_dias ="";
							simbolo_de_1_a_30_dias="";
						}
						
						if(parseFloat(body_tabla[i]["de_31_a_60_dias"])>0){
							campo_de_31_a_60_dias = $(this).agregar_comas(parseFloat(body_tabla[i]["de_31_a_60_dias"]).toFixed(2));
							simbolo_de_31_a_60_dias = simbolo_moneda;
						}else{
							campo_de_31_a_60_dias="";
							simbolo_de_31_a_60_dias="";
						}
						
						if(parseFloat(body_tabla[i]["de_61_dias_en_adelante"])>0){
							campo_de_61_dias_en_adelante = $(this).agregar_comas(parseFloat(body_tabla[i]["de_61_dias_en_adelante"]).toFixed(2));
							simbolo_de_61_dias_en_adelante = simbolo_moneda;
						}else{
							campo_de_61_dias_en_adelante="";
							simbolo_de_61_dias_en_adelante="";
						}
						
						//crear primer registro del nuevo proveedor
						html_reporte +='<tr>';
						html_reporte +='<td width="80px" align="left" >'+body_tabla[i]["clave_proveedor"]+'</td>';
						html_reporte +='<td width="250px" align="left" >'+body_tabla[i]["proveedor"]+'</td>';
						html_reporte +='<td width="90px" align="left" >'+body_tabla[i]["serie_folio"]+'</td>';
						html_reporte +='<td width="90px" align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
						html_reporte +='<td width="110px" align="center" >'+body_tabla[i]["fecha_vencimiento"]+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_por_vencer+'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+campo_por_vencer+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_1_a_30_dias+'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+campo_de_1_a_30_dias+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_31_a_60_dias+'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+campo_de_31_a_60_dias+'</td>';
						html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_61_dias_en_adelante+'</td>';
						html_reporte +='<td width="90px" align="right" id="monto">'+campo_de_61_dias_en_adelante+'</td>';
						html_reporte +='</tr>';
						
					}
					
					//suma parcial de elementos
					suma_parcial_por_vencer = parseFloat(suma_parcial_por_vencer) + parseFloat(body_tabla[i]["por_vencer"]);
					suma_parcial_30_dias = parseFloat(suma_parcial_30_dias) + parseFloat(body_tabla[i]["de_1_a_30_dias"]);
					suma_parcial_60_dias = parseFloat(suma_parcial_60_dias) + parseFloat(body_tabla[i]["de_31_a_60_dias"]);
					suma_parcial_90_dias = parseFloat(suma_parcial_90_dias) + parseFloat(body_tabla[i]["de_61_dias_en_adelante"]);
				}
				
				
				//si los campos vienen  co cero asignarle espacio en blanco
				if(parseFloat(suma_parcial_por_vencer)>0){
					suma_parcial_por_vencer = $(this).agregar_comas(parseFloat(suma_parcial_por_vencer).toFixed(2));
					simbolo_por_vencer = simbolo_moneda;
				}else{
					suma_parcial_por_vencer="";
					simbolo_por_vencer = "";
				}
				
				if(parseFloat(suma_parcial_30_dias)>0){
					suma_parcial_30_dias = $(this).agregar_comas(parseFloat(suma_parcial_30_dias).toFixed(2));
					simbolo_de_1_a_30_dias = simbolo_moneda;
				}else{
					suma_parcial_30_dias="";
					simbolo_de_1_a_30_dias="";
				}
				
				if(parseFloat(suma_parcial_60_dias)>0){
					suma_parcial_60_dias = $(this).agregar_comas(parseFloat(suma_parcial_60_dias).toFixed(2));
					simbolo_de_31_a_60_dias = simbolo_moneda;
				}else{
					suma_parcial_60_dias="";
					simbolo_de_31_a_60_dias="";
				}
				
				if(parseFloat(suma_parcial_90_dias)>0){
					suma_parcial_90_dias = $(this).agregar_comas(parseFloat(suma_parcial_90_dias).toFixed(2));
					simbolo_de_61_dias_en_adelante = simbolo_moneda;
				}else{
					suma_parcial_90_dias="";
					simbolo_de_61_dias_en_adelante="";
				}
				
				//imprimir total del ultimo provedor 
				html_reporte +='<tr id="tr_totales">';
				html_reporte +='<td width="80px" align="left"  id="sin_borde_derecho"></td>';
				html_reporte +='<td width="250px" align="left"  id="sin_borde"></td>';
				html_reporte +='<td width="90px" align="left" id="sin_borde"></td>';
				html_reporte +='<td width="90px" align="center" id="sin_borde"></td>';
				html_reporte +='<td width="110px" align="left" id="sin_borde">Total Proveedor</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_por_vencer+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+suma_parcial_por_vencer+'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_1_a_30_dias+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+suma_parcial_30_dias+'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_31_a_60_dias+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+suma_parcial_60_dias+'</td>';
				html_reporte +='<td width="10px" align="right" id="simbolo_moneda">'+simbolo_de_61_dias_en_adelante+'</td>';
				html_reporte +='<td width="90px" align="right"  id="monto">'+suma_parcial_90_dias+'</td>';
				html_reporte +='</tr>';
				
			}
			
			//fila vacia
			html_reporte +=html_fila_vacia;
			
			//imprimir totales en pesos
			html_footer +='<tr id="tr_totales">';
			html_footer +='<td width="80px" align="left"  id="sin_borde_derecho"></td>';
			html_footer +='<td width="250px" align="left"  id="sin_borde"></td>';
			html_footer +='<td width="90px" align="left" id="sin_borde"></td>';
			html_footer +='<td width="90px" align="center" id="sin_borde"></td>';
			html_footer +='<td width="110px" align="left" id="sin_borde">Total en MN</td>';
			html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
			html_footer +='<td width="90px" align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_total_por_vencer_mn).toFixed(2))+'</td>';
			html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
			html_footer +='<td width="90px" align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_total_30_dias_mn).toFixed(2))+'</td>';
			html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
			html_footer +='<td width="90px" align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_total_60_dias_mn).toFixed(2))+'</td>';
			html_footer +='<td width="10px" align="right" id="simbolo_moneda">$</td>';
			html_footer +='<td width="90px" align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_total_90_dias_mn).toFixed(2))+'</td>';
			html_footer +='</tr>';
			
			//fila vacia
			html_reporte +=html_fila_vacia;
			
			//imprimir totales en dolares
			html_footer +='<tr id="tr_totales">';
			html_footer +='<td width="80px" align="left"  id="sin_borde_derecho"></td>';
			html_footer +='<td width="250px" align="left"  id="sin_borde"></td>';
			html_footer +='<td width="90px" align="left" id="sin_borde"></td>';
			html_footer +='<td width="90px" align="center" id="sin_borde"></td>';
			html_footer +='<td width="110px" align="left" id="sin_borde">Total en USD</td>';
			html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
			html_footer +='<td width="90px" align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_total_por_vencer_usd).toFixed(2))+'</td>';
			html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
			html_footer +='<td width="90px" align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_total_30_dias_usd).toFixed(2))+'</td>';
			html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
			html_footer +='<td width="90px" align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_total_60_dias_usd).toFixed(2))+'</td>';
			html_footer +='<td width="10px" align="right" id="simbolo_moneda">USD</td>';
			html_footer +='<td width="90px" align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_total_90_dias_usd).toFixed(2))+'</td>';
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
	
});
