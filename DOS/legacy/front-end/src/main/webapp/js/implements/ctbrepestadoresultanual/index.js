$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Estado de Resultados Anual' ,                 
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
			return this.contextpath + "/controllers/ctbrepestadoresultanual";
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
	var $select_ano = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_ano]');
	var $select_mes = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_mes]');
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
		var alto = parseInt(height2)-240;
		var pix_alto=alto+'px';
		$('#table_rep').tableScroll({height:parseInt(pix_alto)});
		
		$vermenos.hide();
		$vermas.show();
		$tr_oculto.hide();
		verMas=false;
	});
	
	
	
	
	
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
	
	$select_tipo_reporte.children().remove();
	var html='<option value="1">Mensual</option>';
	html+='<option value="2">Anual</option>';
	$select_tipo_reporte.append(html);
	
	$select_cuentas.children().remove();
	var html2='<option value="1">Todas</option>';
	html2+='<option value="2">Una cuenta</option>';
	$select_cuentas.append(html2);
	
	var array_meses = {0:"- Seleccionar -",  1:"Enero",  2:"Febrero", 3:"Marzo", 4:"Abirl", 5:"Mayo", 6:"Junio", 7:"Julio", 8:"Agosto", 9:"Septiembre", 10:"Octubre", 11:"Noviembre", 12:"Diciembre"};
	var array_ctas_nivel1;
	var array_ctas;
	var mesActual=0;
	var verMas=false;
	
	var arreglo_parametros = { iu:config.getUi() };
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

		//cargar select de sucursales
		$select_sucursal.children().remove();
		var html_suc = '';
		$.each(entry['Suc'],function(entryIndex,suc){
			html_suc += '<option value="' + suc['id'] + '"  >' + suc['titulo'] + '</option>';
		});
		$select_sucursal.append(html_suc);
		
		
		/*
		$select_cuenta.children().remove();
		var html_cta = '';
		$.each(entry['Cta'],function(entryIndex,cta){
			html_cta += '<option value="' + cta['cta'] + '"  >' + cta['cta'] + '</option>';
		});
		$select_cuenta.append(html_cta);
		*/
		
		//Visualizar subcuentas de acuerdo al nivel definido para la empresa
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=1 ){ $select_cuenta.show(); };
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=2 ){ $select_subcuenta.show(); };
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=3 ){ $select_subsubcuenta.show(); };
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=4 ){ $select_subsubsubcuenta.show(); };
		if(parseInt(entry['Dato'][0]['nivel_cta']) >=5 ){ $select_subsubsubsubcuenta.show(); };
		
		array_ctas_nivel1=entry['Cta'];
		mesActual = entry['Dato'][0]['mesActual'];
	});
	
	
	
	$select_tipo_reporte.change(function(){
		if(parseInt($(this).val())==1){
			$select_mes.removeAttr('disabled');
			
			//Recargar select de Meses
			$select_mes.children().remove();
			var select_html = '';
			for(var i in array_meses){
				if(parseInt(i) == parseInt(mesActual) ){
					select_html += '<option value="' + i + '" selected="yes">' + array_meses[i] + '</option>';	
				}else{
					select_html += '<option value="' + i + '"  >' + array_meses[i] + '</option>';	
				}
			}
			$select_mes.append(select_html);
			$select_mes.focus();
		}else{
			$select_mes.children().remove();
			$select_mes.attr('disabled','-1');
			$select_tipo_reporte.focus();
		}
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
				html_cta += '<option value="' + cta['cta'] + '"  >' + cta['cta'] + '</option>';
			});
			$select_cuenta.append(html_cta);
			
			$select_cuenta.focus();
		}
	});
	

	
	$aplicar_evento_change = function(nivel, $campo_select, $campo_select2){
		$campo_select.change(function(){
			var valor_cta=$(this).val();
			$descripcion.val('');
			
			var input_json_cuentas = config.getUrlForGetAndPost() + '/getCtas.json';
			$arreglo = {
				'cta':$select_cuenta.val(),
				'scta':$select_subcuenta.val(),
				'sscta':$select_subsubcuenta.val(),
				'ssscta':$select_subsubsubcuenta.val(),
				'nivel':nivel,
				'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
			}
			
			$.post(input_json_cuentas,$arreglo,function(data){
				//alert(data['Cta'].length);
				if(parseInt(data['Cta'].length)>0){
					$campo_select2.children().remove();
					var html = '';
					$.each(data['Cta'],function(entryIndex,cta){
						if(parseInt(cta['cta'])==0){
							html += '<option value="' + cta['cta'] + '"  ></option>';
						}else{
							html += '<option value="' + cta['cta'] + '"  >' + cta['cta'] + '</option>';
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
		});
	}
	
	
	//Obtener las subcuentas de acuerdo al nivel que se le indica
	$aplicar_evento_change(2, $select_cuenta, $select_subcuenta);
	$aplicar_evento_change(3, $select_subcuenta, $select_subsubcuenta);
	$aplicar_evento_change(4, $select_subsubcuenta, $select_subsubsubcuenta);
	$aplicar_evento_change(5, $select_subsubsubcuenta, $select_subsubsubsubcuenta);
	
    
	
	//Crear y descargar PDF de Reporte Auxiliar de Movimientos de Cuentas
	$genera_PDF.click(function(event){
		event.preventDefault();
		var mes="0";
		var cta="0";
		var scta="0";
		var sscta="0";
		var ssscta="0";
		var sssscta="0";
		
		if($select_mes.val()!=null && $select_mes.val()!=""){
			mes=$select_mes.val();
		}
		
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
		
		var cadena = $select_tipo_reporte.val()+"___"+$select_ano.val()+"___"+mes+"___"+$select_cuentas.val()+"___"+cta+"___"+scta+"___"+sscta+"___"+ssscta+"___"+sssscta;
		//alert(cadena);
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		var input_json = config.getUrlForGetAndPost() + '/getPdfRepEstadoResultadosAnual/'+cadena+'/'+iu+'/out.json';
		window.location.href=input_json;
	});//termina llamada json
	
	
	
	$busqueda_reporte.click(function(event){
		event.preventDefault();
		$div_rep.children().remove();
		
		var mes="0";
		var cta="0";
		var scta="0";
		var sscta="0";
		var ssscta="0";
		var sssscta="0";
		
		if($select_mes.val()!=null && $select_mes.val()!=""){
			mes=$select_mes.val();
		}
		
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
			tipo_reporte: $select_tipo_reporte.val(),
			ano: $select_ano.val(),
			mes: mes,
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
				descripcion		:'Conceptos',
				enero		    :'Enero',
				febrero	        :'Febrero',
				marzo	        :'Marzo',
				abril	        :'Abril',
				mayo	        :'Mayo',
				junio           :'Junio',
				julio           :'Julio',
				agosto			:'Agosto',
				septiembre	    :'Septiembre',
				octubre 	    :'Octubre',
				noviembre 	    :'Noviembre',
				diciembre 	    :'Diciembre',
				total 	        :'Total',
			};
			
			var html_reporte = '<table id="table_rep">';
			var html_fila_vacia='';
			var html_footer = '';
			
			
			
			
			html_reporte +='<thead> <tr>';
			for(var key in header_tabla){
				var attrValue = header_tabla[key];
				if(attrValue == "Conceptos"){
					html_reporte +='<td width="450px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Enero"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Febrero"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				} 	 	
				
				if(attrValue == "Marzo"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Abril"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Mayo"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Junio"){
					html_reporte +='<td width="120px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Julio"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Agosto"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Septiembre"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Octubre"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Noviembre"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Diciembre"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Total"){
					html_reporte +='<td width="130px" align="left">'+attrValue+'</td>'; 
				}
			}
			html_reporte +='</tr> </thead>';
			
			html_fila_vacia +='<tr class="first">';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="450px" height="10"></td>';
			//html_fila_vacia +='<td align="left"  id="sin_borde" width="50px"></td>';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="right" id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="130px"></td>';
			html_fila_vacia +='<td align="left"  id="sin_borde" width="130px"></td>';
			html_fila_vacia +='</tr>';
			
			
			 //inicializar variables
			 //var simbolo_moneda="";
			    var cliente_actual="";
                var suma_total_debe=0.0;
                var suma_total_haber=0.0;
                var suma_total_saldo=0.0;
                
			/*if(body_tabla[i]["denominacion"]=="M.N."){
							simbolo_moneda="$";
						}else{
							simbolo_moneda=body_tabla[i]["denominacion"];
						}*/
						
			if(parseInt(body_tabla.length)>0){
				
				   
					
				
			
				for(var i=0; i<body_tabla.length; i++){
					/*	
					//if(cliente_actual == body_tabla[i]["cliente"]){
					 html_reporte +='<tr>';
					html_reporte +='<td align="left">'+body_tabla[i]["suc"]+'</td>';
					html_reporte +='<td align="left">'+body_tabla[i]["nombrepol"]+'</td>';
					html_reporte +='<td align="left" id="sin_borde"></td>';
				    html_reporte +='<td align="left" id="sin_borde"></td>';
					html_reporte +='<td align="left" id="sin_borde"></td>';
					html_reporte +='<td align="left">'+body_tabla[i]["nombrech"]+'</td>';
					html_reporte +='<td align="left" id="sin_borde"></td>';
					html_reporte +='<td align="left" id="sin_borde"></td>';
					//html_reporte +='<td align="right" id="sin_borde">Total:</td>';
					html_reporte +='<td align="left" id="sin_borde">Saldo Inicial:</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldoinicial"])+'</td>';
					html_reporte +='<td align="left" id="sin_borde"></td>';
					html_reporte +='</tr>'; 
				*/
				//}else{
					
					
				
					html_reporte +='<tr>';
					html_reporte +='<td align="left">'+body_tabla[i]["descripcion"]+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo_final"])+'</td>';
				
					
					/*html_reporte +='<tr>';
					html_reporte +='<td align="left">'+body_tabla[i]["sucursal"]+'</td>';
					html_reporte +='<td align="left">'+body_tabla[i]["poliza"]+'</td>';
					html_reporte +='<td align="left">'+body_tabla[i]["origen"]+'</td>';
					html_reporte +='<td align="left">'+body_tabla[i]["tipopoliza"]+'</td>';
					html_reporte +='<td align="left">'+body_tabla[i]["fechas"]+'</td>';
					html_reporte +='<td align="left">'+body_tabla[i]["cheque"]+'</td>';
					html_reporte +='<td align="left">'+body_tabla[i]["cc"]+'</td>';
					html_reporte +='<td align="left">'+body_tabla[i]["descripcion"]+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["debe"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["haber"])+'</td>';
					html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["saldo"])+'</td>';
					html_reporte +='</tr>';*/
					
					/*
					suma_total_debe=parseFloat(suma_total_debe) + parseFloat(body_tabla[i]["debe"]);
					suma_total_haber=parseFloat(suma_total_haber) + parseFloat(body_tabla[i]["haber"]);
					suma_total_saldo=parseFloat(suma_total_saldo) + parseFloat(body_tabla[i]["saldo"]);
					
					
					
					
					//imprimir totales
							html_reporte +='<tr id="tr_totales">';
							html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="left" id="sin_borde"></td>';
							//html_reporte +='<td align="left" id="sin_borde"></td>';
							html_reporte +='<td align="right" id="sin_borde">Total:</td>';
							//html_reporte +='<td align="right" id="simbolo_moneda"></td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_total_debe).toFixed(2))+'</td>';
							//html_reporte +='<td align="right" id="simbolo_moneda"></td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_total_haber).toFixed(2))+'</td>';
							//html_reporte +='<td align="left" ></td>';
							//html_reporte +='<td align="right" id="simbolo_moneda"></td>';
							html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_total_saldo).toFixed(2))+'</td>';
							html_reporte +='</tr>';
				*/
			
                     //  }
				
                                       
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
			if(verMas){
				//Entra aqui si esta activado la opcion ver mas parametros de la busqueda
				alto = parseInt(height2)-282;
			}else{
				alto = parseInt(height2)-240;
			}
			var pix_alto=alto+'px';
			$('#table_rep').tableScroll({height:parseInt(pix_alto)});
		});
	});
	
	
	$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_reporte, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_ano, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_mes, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_cuentas, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_cuenta, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_subcuenta, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_subsubcuenta, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_subsubsubcuenta, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_subsubsubsubcuenta, $busqueda_reporte);
	
	$select_tipo_reporte.focus();
});
