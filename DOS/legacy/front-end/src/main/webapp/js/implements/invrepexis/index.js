$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
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
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invrepexis";
    
	$('#barra_acciones').hide();
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Existencias en Inventario');
	
	//barra para el buscador 
	$('#barra_buscador').hide();
    
	
	
	var $tabla_existencias = $('#lienzo_recalculable').find('#table_exis');
	var $select_opciones = $('#lienzo_recalculable').find('select[name=opciones]');
	var $select_almacen = $('#lienzo_recalculable').find('select[name=select_almacen]');
	var $select_tipo_costo = $('#lienzo_recalculable').find('select[name=select_tipo_costo]');
	var $codigo_producto = $('#lienzo_recalculable').find('input[name=codigo]');
	var $descripcion = $('#lienzo_recalculable').find('input[name=descripcion]');
	var $select_tipo_prod = $('#lienzo_recalculable').find('select[name=select_tipo_prod]');
	var $genera_reporte_exis = $('#lienzo_recalculable').find('#genera_reporte_exis');
	var $buscar = $('#lienzo_recalculable').find('#boton_buscador');

	$select_opciones.children().remove();
	var almacen_hmtl = '<option value="1" selected="yes">General</option>';
		almacen_hmtl += '<option value="2">Con Existencia</option>';
		almacen_hmtl += '<option value="3">Sin Existencia</option>'
		almacen_hmtl += '<option value="4">Valor M&iacute;nimo</option>';
		almacen_hmtl += '<option value="5">Valor M&aacute;ximo</option>';
		almacen_hmtl += '<option value="6">Punto de Reorden</option>';
	$select_opciones.append(almacen_hmtl);
	
	$select_tipo_costo.children().remove();
	var tcosto_hmtl = '<option value="1" selected="yes">Costo Ultimo</option>';
		tcosto_hmtl += '<option value="2">Costo Promedio</option>';
	$select_tipo_costo.append(tcosto_hmtl);
	
	
	
	
	//busca el almacen y presentacion en el Grid, esto para evitar que se repitan
	var $actualizarSumasMonedas = function($grid_monedas, idMon, cant){
		var encontrado=0;
		//busca el codigo del producto en el grid
		$grid_monedas.find('tr').each(function (index){
			var cantTr=$(this).find(':eq(5)').text();
			
			if(parseInt($(this).find(':eq(4)').text())==parseInt(idMon)){
				$(this).find(':eq(5)').text(parseFloat(cantTr) + parseFloat(cant));
			}
		});
		return encontrado;
	}
	
	
	
	$genera_reporte_exis.click(function(event){
		event.preventDefault();
		var codigo_producto;
		var descripcion;
		var id_almacen = $select_almacen.val();
		if($codigo_producto.val()==''){
			codigo_producto = 0;
		}else{
			codigo_producto = $codigo_producto.val();
		}
		if($descripcion.val()==''){
			descripcion = 0;
		}else{
			descripcion = $descripcion.val();
		}
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getReporteExistencias/'+$select_opciones.val()+'/'+id_almacen+'/'+codigo_producto+'/'+descripcion+'/'+$select_tipo_costo.val()+'/'+ $select_tipo_prod.val() +'/'+ iu +'/out.json';
		if(parseInt($select_almacen.val()) > 0){
			window.location.href=input_json;
		}else{
			alert("Selecciona un Almacen.");
		}
	});
	
	
	
	
	//obtiene los almacenes para el reporte
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAlmacenes.json';
	$arreglo = {'iu':iu = $('#lienzo_recalculable').find('input[name=iu]').val()};
	$.post(input_json,$arreglo,function(entry){
		$select_almacen.children().remove();
		//var almacen_hmtl = '<option value="0" selected="yes">[--Seleccionar Almacen--]</option>';
		var almacen_hmtl = '';
		$.each(entry['Almacenes'],function(entryIndex,alm){
			almacen_hmtl += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
		});
		$select_almacen.append(almacen_hmtl);
		
		$select_tipo_prod.children().remove();
		var tprod_hmtl = '<option value="0" selected="yes">[--Seleccionar --]</option>';
		$.each(entry['TiposProd'],function(entryIndex,tp){
			tprod_hmtl += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
		});
		$select_tipo_prod.append(tprod_hmtl);
		
	});//termina llamada json
	
	/*
	var height2 = $('#cuerpo').css('height');
	var alto = parseInt(height2)-240;
	var pix_alto=alto+'px';
	
	
	//$('#table_exis').tableScroll({height:parseInt(pix_alto)});
	*/
	
	$buscar.click(function(event){
		var id_almacen = $select_almacen.val();
		$tabla_existencias.children().remove();
		
		var primero=0;
		//$tabla_existencias.find('tbody').children().remove();
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getExistencias.json';
		$arreglo = {'tipo':$select_opciones.val(), 
					'almacen':id_almacen, 
					'codigo':$codigo_producto.val(), 
					'descripcion':$descripcion.val(),
					'tipo_costo':$select_tipo_costo.val(),
					'tipo_prod':$select_tipo_prod.val(),
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		var acumuladoCostoTotalPesos=0;
		var acumuladoCostoTotalDolar=0;
		var acumuladoCostoTotalEuro=0;
		
		
		
		if(parseInt($select_almacen.val()) > 0){
			
			$.post(input_json,$arreglo,function(entry){
				
				var html_reporte='';
				var html_footer='';
				
				html_reporte = '<table id="repExis">';
				html_reporte +='<thead><tr><td width="110">C&oacute;digo</td><td width="400">Descripci&oacute;n</td><td width="100">Unidad</td><td width="90">Existencia</td><td width="120">Costo&nbsp;Unitario</td><td width="120">Costo&nbsp;Total</td><td width="60">Moneda</td></tr></thead>';
					
				if(parseInt(entry['Existencias'].length) > 0){
					
					$.each(entry['Existencias'],function(entryIndex,exi){
						if(primero==0){
							html_reporte += '<tr class="first"><td width="110">'+exi['codigo_producto']+'</td><td width="400">'+exi['descripcion']+'</td><td width="100">'+exi['unidad_medida']+'</td><td width="90" align="right">'+$(this).agregar_comas(parseFloat(exi['existencias']).toFixed(2))+'</td><td width="120" align="right">'+$(this).agregar_comas(parseFloat(exi['costo_unitario']).toFixed(2))+'</td><td width="120" align="right">'+$(this).agregar_comas(parseFloat(exi['costo_total']).toFixed(2))+'</td><td width="60" align="center">'+exi['simbolo_moneda']+'</td></tr>';
							primero=1;
						}else{
							html_reporte += '<tr><td width="110">'+exi['codigo_producto']+'</td><td width="400">'+exi['descripcion']+'</td><td width="100">'+exi['unidad_medida']+'</td><td width="90" align="right">'+$(this).agregar_comas(parseFloat(exi['existencias']).toFixed(2))+'</td><td width="120" align="right">'+$(this).agregar_comas(parseFloat(exi['costo_unitario']).toFixed(2))+'</td><td width="120" align="right">'+$(this).agregar_comas(parseFloat(exi['costo_total']).toFixed(2))+'</td><td width="60" align="center">'+exi['simbolo_moneda']+'</td></tr>';
						}
						
						if(parseInt(exi['moneda_id'])==1){
							acumuladoCostoTotalPesos = parseFloat(acumuladoCostoTotalPesos) + parseFloat(parseFloat(exi['costo_total']).toFixed(2));
						}
						if(parseInt(exi['moneda_id'])==2){
							acumuladoCostoTotalDolar = parseFloat(acumuladoCostoTotalDolar) + parseFloat(parseFloat(exi['costo_total']).toFixed(2));
						}
						if(parseInt(exi['moneda_id'])==3){
							acumuladoCostoTotalEuro = parseFloat(acumuladoCostoTotalEuro) + parseFloat(parseFloat(exi['costo_total']).toFixed(2));
						}
						
					});
					
					
					if(parseFloat(acumuladoCostoTotalPesos)>0){
						html_footer +='<tr><td width="110"></td><td width="400"></td><td width="100"></td><td width="90" align="right"> </td><td width="120" align="right">Suma&nbsp;Costo&nbsp;Total</td><td width="120" align="right">'+$(this).agregar_comas(parseFloat(acumuladoCostoTotalPesos).toFixed(2))+'</td><td width="60" align="center">Pesos</td></tr>';
					}
					
					if(parseFloat(acumuladoCostoTotalDolar)>0){
						html_footer +='<tr><td width="110"></td><td width="400"></td><td width="100"></td><td width="90" align="right"> </td><td width="120" align="right">Suma&nbsp;Costo&nbsp;Total</td><td width="120" align="right">'+$(this).agregar_comas(parseFloat(acumuladoCostoTotalDolar).toFixed(2))+'</td><td width="60" align="center">Dolares</td></tr>';
					}
					
					if(parseFloat(acumuladoCostoTotalEuro)>0){
						html_footer +='<tr><td width="110"></td><td width="400"></td><td width="100"></td><td width="90" align="right"> </td><td width="120" align="right">Suma&nbsp;Costo&nbsp;Total</td><td width="120" align="right">'+$(this).agregar_comas(parseFloat(acumuladoCostoTotalEuro).toFixed(2))+'</td><td width="60" align="center">Euros</td></tr>';
					}
					
					

					
				}
				
				html_reporte +='<tfoot>';
					html_reporte += html_footer;
				html_reporte +='</tfoot>';
				html_reporte += '</table>';
				
				$tabla_existencias.append(html_reporte);
				
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-280;
				var pix_alto=alto+'px';
				
				$('#repExis').tableScroll({height:parseInt(pix_alto)});
					
				
			});//termina llamada json
		}else{
			jAlert("Selecciona un Almacen.",'! Atencion');
		}
	});
	
	$aplicar_evento_keypress($select_opciones, $buscar);
	$aplicar_evento_keypress($select_almacen, $buscar);
	$aplicar_evento_keypress($select_tipo_costo, $buscar);
	$aplicar_evento_keypress($codigo_producto, $buscar);
	$aplicar_evento_keypress($descripcion, $buscar);
	$codigo_producto.focus();
});



