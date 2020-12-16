$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select tipo de Nota de credito
    var arrayTipoNotaCredito = {
				1:"Bonificacion", 
				2:"Descuento", 
				3:"Devoluci&oacute;n de Mercanc&iacute;a"
			};
	
	var Impuestos = new Array(); //este arreglo carga los select del de impuestos por partida
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/comfacdevolucion";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    
    var $new_nota_credito = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//$new_nota_credito.hide();
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Devoluciones a Proveedores');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_nota_credito = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_nota_credito]');
	var $busqueda_proveedor = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_proveedor]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
	
	$buscar.mouseover(function(){
		$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
	});
	$buscar.mouseout(function(){
		$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
	});
	   
	$limpiar.mouseover(function(){
		$(this).removeClass("onmouseOutLimpiar").addClass("onmouseOverLimpiar");
	});
	$limpiar.mouseout(function(){
		$(this).removeClass("onmouseOverLimpiar").addClass("onmouseOutLimpiar");
	});
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "nota_credito" + signo_separador + $busqueda_nota_credito.val() + "|";
		valor_retorno += "proveedor" + signo_separador + $busqueda_proveedor.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
		return valor_retorno;
	};
    
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	$buscar.click(function(event){
		//event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	$limpiar.click(function(event){
		$busqueda_factura.val('');
		$busqueda_cliente.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
	});
	
	
	
	TriggerClickVisializaBuscador = 0;
	
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		
		var alto=0;
		if(TriggerClickVisializaBuscador==0){
			 TriggerClickVisializaBuscador=1;
			 var height2 = $('#cuerpo').css('height');
			 //alert('height2: '+height2);
			 
			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 //alert('pix_alto: '+pix_alto);
			 
			 $('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
			 $('#barra_buscador').animate({height: '80px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
			 
			 //alert($('#cuerpo').css('height'));
		}else{
			 TriggerClickVisializaBuscador=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
		};
	});
	
	
	
	
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
	
	
	
	
	
	
	$busqueda_fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
	
	$busqueda_fecha_inicial.DatePicker({
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
			$busqueda_fecha_inicial.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($busqueda_fecha_inicial.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$busqueda_fecha_inicial.val(mostrarFecha());
				}else{
					$busqueda_fecha_inicial.DatePickerHide();	
				}
			}
		}
	});
	
	$busqueda_fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
	
	$busqueda_fecha_final.DatePicker({
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
			$busqueda_fecha_final.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($busqueda_fecha_final.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$busqueda_fecha_final.val(mostrarFecha());
				}else{
					$busqueda_fecha_final.DatePickerHide();	
				}
			}
		}
	});
	
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-comfacdevolucion-window').find('#boton_actualizar').mouseover(function(){
			//$('#forma-comfacdevolucion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			$('#forma-comfacdevolucion-window').find('#boton_actualizar').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-comfacdevolucion-window').find('#boton_actualizar').mouseout(function(){
			//$('#forma-comfacdevolucion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			$('#forma-comfacdevolucion-window').find('#boton_actualizar').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-comfacdevolucion-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-comfacdevolucion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-comfacdevolucion-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-comfacdevolucion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-comfacdevolucion-window').find('#close').mouseover(function(){
			$('#forma-comfacdevolucion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-comfacdevolucion-window').find('#close').mouseout(function(){
			$('#forma-comfacdevolucion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-comfacdevolucion-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-comfacdevolucion-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-comfacdevolucion-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-comfacdevolucion-window').find("ul.pestanas li").click(function() {
			$('#forma-comfacdevolucion-window').find(".contenidoPes").hide();
			$('#forma-comfacdevolucion-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-comfacdevolucion-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	//carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, elemento_seleccionado){
		$campo_select.children().remove();
		var select_html = '';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				//3=Facturacion de Remisiones, solo debe mostrarse cuando se abra la ventana desde el Icono de Nuevo
				if(parseInt(elemento_seleccionado)==0  && parseInt(i)!=3 ){
					select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
				}
			}
		}
		$campo_select.append(select_html);
	}
	
	
	
	
	//buscador de proveedores
	$busca_proveedores = function( $select_moneda, array_monedas, $id_impuesto, $valor_impuesto, razon_social_proveedor){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_no_proveedor = $('#forma-buscaproveedor-window').find('input[name=campo_no_proveedor]');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
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
		
		//asignamos la Razon Social del Proveedor al campo Nombre
		$campo_nombre.val(razon_social_proveedor);
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProveedores.json';
			$arreglo = {    'rfc':$campo_rfc.val(),
							'no_proveedor':$campo_no_proveedor.val(),
							'nombre':$campo_nombre.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="100">';
							trr += '<span>'+proveedor['numero_proveedor']+'</span>';
							trr += '<input type="hidden" id="no_proveedor" value="'+proveedor['numero_proveedor']+'">';
						trr += '</td>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+proveedor['moneda_id']+'">';
							trr += '<span class="rfc">'+proveedor['rfc']+'</span>';
						trr += '</td>';
						trr += '<td width="350"><span id="razon_social">'+proveedor['razon_social']+'</span></td>';
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
					var id_proveedor = $(this).find('#id_prov').val();
					//asignar a los campos correspondientes el sku y y descripcion
					$('#forma-comfacdevolucion-window').find('input[name=id_proveedor]').val(id_proveedor);
					$('#forma-comfacdevolucion-window').find('input[name=numero_proveedor]').val($(this).find('#no_proveedor').val());
					$('#forma-comfacdevolucion-window').find('input[name=tipo_proveedor]').val($(this).find('#tipo_prov').val());
					$('#forma-comfacdevolucion-window').find('input[name=razon_proveedor]').val($(this).find('#razon_social').html());
					
					var id_moneda=$(this).find('#id_moneda').val();
					
					//carga el select de monedas  con la moneda del proveedor seleccionada por default
					$select_moneda.children().remove();
					var moneda_hmtl = '';
					$.each(array_monedas ,function(entryIndex,moneda){
						if( parseInt(moneda['id']) == parseInt(id_moneda) ){
							moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					//buscar el impuesto del proveedor
					//si el proveedor no tiene impuesto asignado
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getValorIva.json';
					$arreglo2 = {    'id_proveedor':id_proveedor,
									'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
								}
								
					$.post(input_json2,$arreglo2,function(entry2){
						$id_impuesto.val(entry2['id_impuesto']);
						$valor_impuesto.val(entry2['valor_impuesto']);
					});
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
				});
			});
		});
		
					
		//si hay algo en el campo Nombre del proveedor al cargar el buscador, ejecuta la busqueda
		if($campo_nombre.val() != ''){
			$buscar_plugin_proveedor.trigger('click');
		}
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
		});
	}//termina buscador de proveedores

	
	
	
	
	
	
	//buscador de facturas sin con saldo del cliente seleccionado
	$busca_facturas = function($select_moneda, id_proveedor,tipo_proveedor, array_monedas, $factura, $fecha_factura, $monto_factura, $pagos_aplicados, $saldo, $nc_aplicados, $select_moneda_fac, $orden_compra, $select_almacen, $grid_productos, $select_almacen, arrayAlmacenes){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFacturasProveedor.json';
		$arreglo = {'id_proveedor':id_proveedor	};
		
		var trr = '';
		
		$.post(input_json,$arreglo,function(entry){
				
				//verifica si el arreglo  retorno datos
				if (entry['Facturas'].length > 0){
					$(this).modalPanel_buscafacturas();
					var $dialogoc =  $('#forma-buscafacturas-window');
					$dialogoc.append($('div.buscador_facturas').find('table.formaBusqueda_facturas').clone());
					$('#forma-buscafacturas-window').css({"margin-left": -110, "margin-top": -150});
					
					var $tabla_resultados = $('#forma-buscafacturas-window').find('#tabla_resultado');
					//var $cancelar_plugin_busca_lotes_producto = $('#forma-buscaremision-window').find('a[href*=cencela]');
					var $cancelar_busca_remisiones = $('#forma-buscafacturas-window').find('#cencela');
					$tabla_resultados.children().remove();
					
					$cancelar_busca_remisiones.mouseover(function(){
						$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
					});
					$cancelar_busca_remisiones.mouseout(function(){
						$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
					});
					
					//crea el tr con los datos del producto seleccionado
					$.each(entry['Facturas'],function(entryIndex,fac){
						trr = '<tr>';
							trr += '<td width="100">';
								trr += '<span class="fech_fac" style="display:none">'+fac['fecha_factura']+'</span>';
								trr += '<span class="oc" style="display:none">'+fac['orden_compra']+'</span>';
								trr += '<span class="id_fac" style="display:none">'+fac['id_fac']+'</span>';
								trr += '<span class="factura">'+fac['factura']+'</span>';
							trr += '</td>';
							trr += '<td width="100" align="right"><span class="monto">'+fac['monto_factura']+'</span></td>';
							trr += '<td width="100" align="right">';
								trr += '<span class="saldo">'+fac['saldo_factura']+'</span>';
								trr += '<span class="p_apl" style="display:none;">'+fac['pago_aplicado']+'</span>';
								trr += '<span class="nc_apl" style="display:none;">'+fac['nc_aplicado']+'</span>';
							trr += '</td>';
							trr += '<td width="90" align="right">';
								trr += '<span class="id_mon" style="display:none;">'+fac['moneda_id']+'</span>';
								trr += fac['moneda'];
							trr += '</td>';
						trr += '</tr>';
						$tabla_resultados.append(trr);
					});//termina llamada json

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
					
					
					//seleccionar un remision del grid de resultados
					$tabla_resultados.find('tr').click(function(){
						//llamada a la funcion que busca los datos de la Factura seleccionada y carga los datos en el grid de productos
						var factura=$(this).find('span.factura').html();
						var id_moneda = $(this).find('span.id_mon').html();
						var monto_factura = $(this).find('span.monto').html();
						var saldo_factura = $(this).find('span.saldo').html();
						var fecha_factura = $(this).find('span.fech_fac').html();
						var pagos_aplicado = $(this).find('span.p_apl').html();
						var nc_aplicado = $(this).find('span.nc_apl').html();
						var orden_compra = $(this).find('span.oc').html();
						var id_fac = $(this).find('span.id_fac').html();
						
						$factura.val(factura);
						$fecha_factura.val(fecha_factura);
						$monto_factura.val( $(this).agregar_comas( parseFloat(monto_factura).toFixed(2)));
						$pagos_aplicados.val( $(this).agregar_comas(parseFloat(pagos_aplicado).toFixed(2)));
						$nc_aplicados.val( $(this).agregar_comas(parseFloat(nc_aplicado).toFixed(2)));
						$saldo.val( $(this).agregar_comas(parseFloat(saldo_factura).toFixed(2)) );
						$orden_compra.val(orden_compra);
						
						//carga el select de monedas  con la moneda del cliente seleccionada por default
						$select_moneda.children().remove();
						var moneda_hmtl = '';
						$.each(array_monedas ,function(entryIndex,moneda){
							if( parseInt(moneda['id']) == parseInt(id_moneda) ){
								moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
							}else{
								//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							}
						});
						$select_moneda.append(moneda_hmtl);
						
						$select_moneda_fac.children().remove();
						$select_moneda_fac.append(moneda_hmtl);
						
						//llamada a la funcion que Obtiene y Carga las partidas de la factura
						obtiene_partidas_factura(id_proveedor, tipo_proveedor, factura,id_moneda, $select_almacen, $grid_productos, $select_almacen, arrayAlmacenes);
						
						
						//elimina la ventana de busqueda
						var remove = function() {$(this).remove();};
						$('#forma-buscafacturas-overlay').fadeOut(remove);
					});
					
					$cancelar_busca_remisiones.click(function(event){
						//event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-buscafacturas-overlay').fadeOut(remove);
					});
				}else{
					jAlert("El Proveedor seleccionado no tiene Facturas pendientes de Pago.\nSeleccione un Proveedor diferente y haga click en Buscar.",'! Atencion');
				}
		});
		
	}//termina buscador de Facturas del cliente
	
    
    
	
	$permitir_solo_numeros = function($campo_input ){
		$campo_input.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	

	$aplicar_evento_focus = function($campo_input ){
		//elimina cero al hacer clic sobre el campo
		$campo_input.focus(function(e){
			if(parseFloat(quitar_comas($campo_input.val()))<1){
				$campo_input.val('');
			}
		});
	}
	
	
	var crear_cadena_concepto= function($grid_productos){
		var cadena='';
		var primero=0;
		cadena = 'Devolucion de ';
		$grid_productos.find('input[name=micheck]').each(function(){
			if(this.checked){
				if(parseInt(primero)>0){
					cadena += ', ';
				}
				cadena += $(this).parent().parent().find('input[name=cant_dev]').val() + ' ';
				cadena += $(this).parent().parent().find('input[name=unidad]').val() + 's de ';
				cadena += $(this).parent().parent().find('input[name=titulo]').val();
				primero++;
			}
		});
		if(cadena == 'Devolucion de '){
			cadena='';
		}
		
		return cadena;
	}

			
	
	
	
	//metodo  que obtiene partidas de la Fcatura de Compra
	obtiene_partidas_factura = function(id_proveedor, tipo_proveedor, factura,id_moneda_fac, $select_almacen, $grid_productos, $select_almacen, arrayAlmacenes){
		var $fecha_expedicion = $('#forma-comfacdevolucion-window').find('input[name=fecha_expedicion]');
		var $tipo_cambio = $('#forma-comfacdevolucion-window').find('input[name=tipo_cambio]');
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPartidasFactura.json';
		$arreglo = {'id_proveedor':id_proveedor, 'factura':factura, 'id_moneda_fac':id_moneda_fac, 'fecha_expedicion':$fecha_expedicion.val()	};
		
		var trr = '';
		
		$.post(input_json,$arreglo,function(entry){
				
				var id_almacen_salida=0;
				$tipo_cambio.val(entry['Tc']['0']['valor']);
				
				//verifica si el arreglo  retorno datos
				if (entry['Partidas'].length > 0){
					
					$grid_productos.children().remove();
					
					//crea el tr con los datos del producto seleccionado
					$.each(entry['Partidas'],function(entryIndex,partida){
						//obtiene numero de trs
						var notr = $("tr", $grid_productos).size();
						notr++;
						
						var id_detalle = partida['id_detalle'];
						var check='';
						var desactivado='';
						var valor_seleccionado = '0';
						var id_producto = partida['producto_id'];
						var codigo_producto = partida['sku'];
						var titulo_producto = partida['titulo'];
						var unidad_medida = partida['unidad'];
						var pres_id = partida['pres_id'];
						var pres = partida['presentacion'];
						
						var cantidad_fac = partida['cantidad_fac'];
						var costo_unitario = partida['costo_unitario'];
						var importe = partida['importe'];
						var cant_devuelto = partida['cantidad_devuelto'];
						var id_impuesto_partida = partida['id_impuesto'];
						var cant_devolucion = '0.00';
						id_almacen_salida = partida['id_almacen'];
						
						var id_ieps = partida['ieps_id'];
						var tasa_ieps = partida['valor_ieps'];
						var importe_ieps = 0;
						
						var nuevo_tr = crear_tr(tipo_proveedor, notr, id_detalle, check,desactivado,valor_seleccionado, id_producto, codigo_producto, titulo_producto, unidad_medida, cantidad_fac, costo_unitario, importe, cant_devuelto, id_impuesto_partida, cant_devolucion, pres_id, pres, id_ieps, tasa_ieps, importe_ieps);
						
						$grid_productos.append(nuevo_tr);
						
						
						//aplicar click a los campso check del grid
						$grid_productos.find('input#micheck'+ notr).click(function(event){
							if( this.checked ){
								$(this).parent().find('input[name=seleccionado]').val("1");
								$(this).parent().parent().find('input[name=cant_dev]').css({'background' : '#ffffff'});
								$(this).parent().parent().find('input[name=cant_dev]').attr("readonly", false);//habilitar campo
								$(this).parent().parent().find('input[name=cant_dev]').focus();
							}else{
								$(this).parent().find('input[name=seleccionado]').val("0");
								$(this).parent().parent().find('input[name=importe_dev]').val(0);
								$(this).parent().parent().find('input[name=importe_imp_dev]').val(0);
								$(this).parent().parent().find('input[name=cant_dev]').val(parseFloat(0).toFixed(2));
								$(this).parent().parent().find('input[name=cant_dev]').css({'background' : '#dddddd'});
								$(this).parent().parent().find('input[name=cant_dev]').attr("readonly", true);//deshabilitar campo
							}
							$calcula_totales_nota_credito();
						});
						
						
						
						$permitir_solo_numeros( $grid_productos.find('input#cant_dev'+ notr) );
						$aplicar_evento_focus( $grid_productos.find('input#cant_dev'+ notr) );
						
						$grid_productos.find('input#cant_dev'+ notr).blur(function(){
							var $tasa_iva = $(this).parent().parent().find('input[name=tasa_imp]');
							var $importe_iva = $(this).parent().parent().find('input[name=importe_imp_dev]');
							var $importe_devolucion = $(this).parent().parent().find('input[name=importe_dev]');
							var cant_devolucion = quitar_comas($(this).val());
							var cant_fac = quitar_comas($(this).parent().parent().find('input[name=cantidad]').val());
							var cant_devuelto = quitar_comas($(this).parent().parent().find('input[name=cant_devuelto]').val());
							var costo_unitario = quitar_comas($(this).parent().parent().find('input[name=costo]').val());
							var $concepto = $('#forma-comfacdevolucion-window').find('textarea[name=concepto]');
							var $id_ieps = $(this).parent().parent().find('input[name=idIeps]');
							var $tasa_ieps = $(this).parent().parent().find('input[name=tasaIeps]');
							var $importe_ieps = $(this).parent().parent().find('input[name=importeIeps]');
							var tasaIeps=0;
							//Disponible para devolucion
							var disponible = parseFloat(cant_fac) - parseFloat(cant_devuelto);
							
							if(cant_devolucion=="" || parseFloat(cant_devolucion)==0){
								$(this).val(parseFloat(0.00).toFixed(2));//si el campo esta en blanco, pone cero
								$importe_devolucion.val(0);
								$importe_iva.val(0);
								if(parseInt($id_ieps.val())<=0){
									$tasa_ieps.val(' ');
									$importe_ieps.val('');
								}else{
									$tasa_ieps.val(0);
									$importe_ieps.val(0);
								}
							}else{
								//Valida  que la cantidad a devolver no sea mayor a la cantidad vendida en la partida
								if( parseFloat(cant_devolucion) > parseFloat(disponible)  ){
									jAlert("La Cantidad a devolver No debe ser mayor a la cantidad Disponible.\n\nCantidad disponible: "+disponible+"\nCantidad devoluci&oacute;n: "+cant_devolucion+"\n\nSe ha asignado por default la cantidad disponible.", 'Atencion!');
									$(this).val(parseFloat( disponible ).toFixed(2));
								}else{
									$(this).val(parseFloat( quitar_comas($(this).val()) ).toFixed(2));
								}
								
								if($tasa_ieps.val().trim()!=''){
									if(parseFloat($tasa_ieps.val().trim())>0){
										tasaIeps = parseFloat($tasa_ieps.val().trim()) / 100;
									}
								}
								
								//Calcula el importe de la devolucion
								$importe_devolucion.val( parseFloat(parseFloat(quitar_comas($(this).val())) * parseFloat(costo_unitario)).toFixed(4));
								
								$importe_ieps.val(parseFloat(parseFloat($importe_devolucion.val()) * parseFloat(tasaIeps)).toFixed(4));
								
								//Calcula el iva de la partida
								$importe_iva.val( parseFloat(parseFloat(parseFloat($importe_devolucion.val()) + parseFloat($importe_ieps.val())) * parseFloat($tasa_iva.val())).toFixed(4));
							}
							$calcula_totales_nota_credito();
							$concepto.text(crear_cadena_concepto($(this).parent().parent().parent()));
						});
						
					});
					
					
					
					//carga select almacen salida
					$select_almacen.children().remove();
					var almacen_hmtl = '';
					$.each(arrayAlmacenes,function(entryIndex,alm){
						if(parseInt(alm['id']) == parseInt(id_almacen_salida) ){
							almacen_hmtl += '<option value="' + alm['id'] + '" selected="yes">' + alm['titulo'] + '</option>';
						}
					});
					$select_almacen.append(almacen_hmtl);
					
				}else{
					jAlert("Esta Factura no incluye Partidas. Revisar Factura",'! Atencion');
				}
		});
		
	}//termina de partidas de la Factura de Compra
	
    
	
	
	
	
	//metodo  que carga el grid con las partidas de la Factura de compra
	crear_tr = function(tipo_proveedor, notr, id_detalle, check,desactivado,valor_seleccionado, id_producto, codigo_producto, titulo_producto, unidad_medida, cantidad_fac, costo_unitario, importe, cant_devuelto, id_impuesto_partida, cant_devolucion, pres_id, pres, id_ieps, tasa_ieps, importe_ieps){
		var tr='';
		tr += '<tr>';
			tr += '<td width="30" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<input type="checkbox" name="micheck" id="micheck'+ notr +'" value="true" '+check+' '+desactivado+'>';
				tr += '<input type="hidden" name="seleccionado" value="'+valor_seleccionado+'">';//el 1 significa que el checkbox está seleccionado
				tr += '<input type="hidden" name="notr" value="'+notr+'">';
				tr += '<input type="hidden" name="iddetalle" value="'+id_detalle+'">';
			tr += '</td>';
			tr += '<td width="90" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<input type="hidden" name="idproducto" 	value="'+ id_producto +'">';
				tr += '<input type="text" 	name="sku" 			value="'+ codigo_producto +'" class="borde_oculto" style="width:86px;" readOnly="true">';
			tr += '</td>';
			tr += '<td width="200" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<input type="text" 	name="titulo" 	 	value="'+ titulo_producto +'" class="borde_oculto" style="width:196px;" readOnly="true">';
			tr += '</td>';
			tr += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<input type="text" 	name="unidad" value="'+ unidad_medida +'" class="borde_oculto" readOnly="true" style="width:66px;">';
			tr += '</td>';
			tr += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<input type="hidden" name="pres_id" 	value="'+ pres_id +'">';
				tr += '<input type="text" 	name="presentacion" value="'+ pres +'" class="borde_oculto" readOnly="true" style="width:76px;">';
			tr += '</td>';
			
			tr += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<input type="text" 	name="cantidad" 	value="'+ $(this).agregar_comas(cantidad_fac) +'" class="borde_oculto" style="width:66px; text-align:right;">';
			tr += '</td>';
			
			tr += '<td width="75" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<input type="text" name="costo" value="'+ (costo_unitario) +'"  class="borde_oculto" style="width:69px; text-align:right;">';
			tr += '</td>';
			
			tr += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<input type="text" name="importe" value="'+ $(this).agregar_comas(importe) +'"  class="borde_oculto" style="width:76px; text-align:right;" readOnly="true">';
			tr += '</td>';
			
			
			tr += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<select name="select_imp_partida" id="select_imp'+ notr +'" style="width:76px;">';
					var id_impuesto_seleccionado=0;
					var tasa_impuesto=0;
					//carga select con tipos de impuesto
					if(parseInt(tipo_proveedor) == 2){
						id_impuesto_seleccionado=2;
					}else{
						id_impuesto_seleccionado=id_impuesto_partida;
					}
					var con_impuesto=0;
					//aqui se carga el select con los tipos de iva
					$.each(Impuestos,function(entryIndex,imp){
						if(parseInt(imp['id']) == parseInt(id_impuesto_seleccionado)){
							tr += '<option value="' + imp['id'] + '"  selected="yes">' + imp['descripcion'] + '</option>';
							tasa_impuesto = imp['iva_1'];
							con_impuesto=1;
						}else{
							//tr += '<option value="' + imp['id'] + '"  >' + imp['descripcion'] + '</option>';
						}
					});
					if(parseInt(con_impuesto) == 0){
						tr += '<option value="0">[--  --]</option>';
						tasa_impuesto = '0';
					}
					tr += '</select>';
			tr += '</td>';
			
			
			var tasaIeps=" ";
			var importeIeps="";
			
			if(parseInt(id_ieps)>0){
				tasaIeps=tasa_ieps;
				importeIeps=importe_ieps;
			}
			
			tr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="50">';
				tr += '<input type="hidden" name="idIeps"     value="'+ id_ieps +'" id="idIeps">';
				tr += '<input type="text" name="tasaIeps" value="'+ tasaIeps +'" class="borde_oculto" id="tasaIeps" style="width:46px; text-align:right;" readOnly="true">';
			tr += '</td>';
			
			
			tr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70">';
				tr += '<input type="text" name="importeIeps" value="'+ importeIeps +'" class="borde_oculto" id="importeIeps" style="width:66px; text-align:right;" readOnly="true">';
			tr += '</td>';
			
			
			tr += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr += '<input type="text" 		name="cant_devuelto" 	value="'+ $(this).agregar_comas(cant_devuelto) +'" 	class="borde_oculto" readOnly="true" style="width:76px; text-align:right;">';
			tr += '</td>';
			tr += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				var color_fondo='';
				if(parseFloat(cant_devolucion)==0){	color_fondo='background:#dddddd;';	}
				tr += '<input type="text" 		name="cant_dev" value="'+$(this).agregar_comas(cant_devolucion)+'" id="cant_dev'+ notr +'"	readOnly="true" style="width:76px; '+color_fondo+' text-align:right;">';
				tr += '<input type="hidden"    	name="tasa_imp"     	value="'+  tasa_impuesto +'">';
				tr += '<input type="hidden" 	name="importe_dev" 		value="0">';
				tr += '<input type="hidden" 	name="importe_imp_dev" 	value="0">';
			tr += '</td>';
		tr += '</tr>';
		
		
		return tr;
	}//termina de partidas de la Factura de Compra
	
    
	
	
	
	
	//Calcula totales de la Nota de Credito(subtotal, IEPS, IVA, retencion, total)
	$calcula_totales_nota_credito = function(){
		var $subtotal_nota = $('#forma-comfacdevolucion-window').find('input[name=subtotal]');
		var $ieps_nota = $('#forma-comfacdevolucion-window').find('input[name=ieps_nota]');
		var $impuesto_nota = $('#forma-comfacdevolucion-window').find('input[name=impuesto]');
		var $total_nota = $('#forma-comfacdevolucion-window').find('input[name=total]');
		var $grid_productos = $('#forma-comfacdevolucion-window').find('#grid_productos');
		
		 //Suma de todos los importes antes de impuestos
		var sumaSubTotal = 0;
		//Suma de todos los importes del IEPS
		var sumaImporteIeps=0;
		//Suma de todos los importes del IVA
		var sumaImpuesto = 0;
		//sumaSubTotal + sumaImporteIeps + sumaImpuesto
		var sumaTotal = 0;
		
		$grid_productos.find('tr').each(function (index){
			sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat( $(this).find('input[name=importe_dev]').val() );
			sumaImpuesto = parseFloat(sumaImpuesto) + parseFloat( $(this).find('input[name=importe_imp_dev]').val() );
			if($(this).find('input[name=importeIeps]').val().trim()!=''){
				sumaImporteIeps = parseFloat(sumaImporteIeps) + parseFloat( $(this).find('input[name=importeIeps]').val() );
			}
		});
		
		//Calcula el total sumando el subtotal y el impuesto
		sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImporteIeps) + parseFloat(sumaImpuesto);
		
		//Redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
		$subtotal_nota.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
		
		//Redondear la suma del IEPS
		$ieps_nota.val($(this).agregar_comas( parseFloat(sumaImporteIeps).toFixed(2) ));
		
		//Redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$impuesto_nota.val($(this).agregar_comas(  parseFloat(sumaImpuesto).toFixed(2)  ));
		//Redondea a dos digitos la suma  total y se asigna al campo total
		$total_nota.val($(this).agregar_comas(  parseFloat(sumaTotal).toFixed(2) ));
	}
	//Termina calcular totales
	
	
	
	$limpiar_campos = function($subtotal,$impuesto,$total,$factura, $fecha_factura, $monto_factura, $monto_factura, $pagos_aplicados, $saldo, $fac_saldado, $nc_aplicados, $fecha_expedicion, $folio_nota_credito, $select_moneda_fac, $orden_compra){
		$subtotal.val('');
		$impuesto.val('');
		$total.val('');
		$factura.val('');
		$fecha_factura.val('');
		$monto_factura.val('');
		$monto_factura.val('');
		$pagos_aplicados.val('');
		$nc_aplicados.val('');
		$saldo.val('');
		$fac_saldado.val('false');
		$folio_nota_credito.val('');
		//$fecha_expedicion.val('');
		//$select_moneda_fac
		$orden_compra.val('');
	}
	

	
	//nueva Devolucion
	$new_nota_credito.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_comfacdevolucion();
		
		var form_to_show = 'formacomfacdevolucion00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-comfacdevolucion-window').css({"margin-left": -435, 	"margin-top": -220});
		
		$forma_selected.prependTo('#forma-comfacdevolucion-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDevolucion.json';
		$arreglo = {'id_nota_credito':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
		var $identificador = $('#forma-comfacdevolucion-window').find('input[name=identificador]');
		var $folio = $('#forma-comfacdevolucion-window').find('input[name=folio]');
		var $folio_nota_credito = $('#forma-comfacdevolucion-window').find('input[name=folio_nota_credito]');
		var $fecha_expedicion = $('#forma-comfacdevolucion-window').find('input[name=fecha_expedicion]');
		
		var $generar = $('#forma-comfacdevolucion-window').find('input[name=generar]');
		var $fac_saldado = $('#forma-comfacdevolucion-window').find('input[name=fac_saldado]');
		
		var $busca_proveedor = $('#forma-comfacdevolucion-window').find('a[href*=busca_proveedor]');
		var $id_proveedor = $('#forma-comfacdevolucion-window').find('input[name=id_proveedor]');
		var $numero_proveedor = $('#forma-comfacdevolucion-window').find('input[name=numero_proveedor]');
		var $razon_proveedor = $('#forma-comfacdevolucion-window').find('input[name=razon_proveedor]');
		var $tipo_proveedor = $('#forma-comfacdevolucion-window').find('input[name=tipo_proveedor]');
		
		var $select_moneda = $('#forma-comfacdevolucion-window').find('select[name=select_moneda]');
		var $id_impuesto = $('#forma-comfacdevolucion-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-comfacdevolucion-window').find('input[name=valorimpuesto]');
		var $tipo_cambio = $('#forma-comfacdevolucion-window').find('input[name=tipo_cambio]');
		
		var $concepto = $('#forma-comfacdevolucion-window').find('textarea[name=concepto]');
		
		var $subtotal = $('#forma-comfacdevolucion-window').find('input[name=subtotal]');
		var $impuesto = $('#forma-comfacdevolucion-window').find('input[name=impuesto]');
		var $total = $('#forma-comfacdevolucion-window').find('input[name=total]');
		var $descargar_pdf = $('#forma-comfacdevolucion-window').find('#descargar_pdf');
		var $cancelado = $('#forma-comfacdevolucion-window').find('input[name=cancelado]');
		
		var $busca_factura = $('#forma-comfacdevolucion-window').find('a[href*=busca_factura]');
		var $factura = $('#forma-comfacdevolucion-window').find('input[name=factura]');
		var $select_moneda_fac = $('#forma-comfacdevolucion-window').find('select[name=select_moneda_fac]');
		var $orden_compra = $('#forma-comfacdevolucion-window').find('input[name=orden_compra]');
		var $id_moneda_factura = $('#forma-comfacdevolucion-window').find('input[name=id_moneda_factura]');
		var $fecha_factura = $('#forma-comfacdevolucion-window').find('input[name=fecha_factura]');
		var $monto_factura = $('#forma-comfacdevolucion-window').find('input[name=monto_factura]');
		var $pagos_aplicados = $('#forma-comfacdevolucion-window').find('input[name=pagos_aplicados]');
		var $nc_aplicados = $('#forma-comfacdevolucion-window').find('input[name=nc_aplicados]');
		var $saldo = $('#forma-comfacdevolucion-window').find('input[name=saldo]');
		
		var $select_tipo_movimiento = $('#forma-comfacdevolucion-window').find('select[name=select_tipo_movimiento]');
		var $select_almacen = $('#forma-comfacdevolucion-window').find('select[name=select_almacen]');
		var $select_tipo_nota = $('#forma-comfacdevolucion-window').find('select[name=select_tipo_nota]');
		
		//tabla contenedor del listado de productos
		var $grid_productos = $('#forma-comfacdevolucion-window').find('#grid_productos');
		
		var $cerrar_plugin = $('#forma-comfacdevolucion-window').find('#close');
		var $cancelar_plugin = $('#forma-comfacdevolucion-window').find('#boton_cancelar');
		var $boton_actualizar = $('#forma-comfacdevolucion-window').find('#boton_actualizar');
		var $submit_actualizar = $('#forma-comfacdevolucion-window').find('#submit');
		
		$identificador.val(0);//para nueva cotizacion el folio es 0
		
		//quitar enter a todos los campos input
		$('#forma-comfacdevolucion-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$descargar_pdf.attr('disabled','-1');
		$fac_saldado.val('false');
		$cancelado.hide();
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los Nota de C&eacute;dito se guardad&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-comfacdevolucion-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				//habilitar boton actualizar
				$submit_actualizar.removeAttr('disabled');
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-comfacdevolucion-window').find('div.interrogacion').css({'display':'none'});
				$grid_productos.find('input[name=cant_dev]').css({'background' : '#ffffff'});
				
				$('#forma-comfacdevolucion-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-comfacdevolucion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				$('#forma-comfacdevolucion-window').find('.comfacdevolucion_div_one').css({'height':'585px'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-comfacdevolucion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						var campo = tmp.split(':')[0];
						
						if(campo.substring(0, 8) == 'cant_dev'){
							$('#forma-comfacdevolucion-window').find('#div_warning_grid').css({'display':'block'});
							var $campo = $grid_productos.find('#'+campo).css({'background' : '#d41000'});
							
							var codigo_producto = $campo.parent().parent().find('input[name=sku]').val();
							var titulo_producto = $campo.parent().parent().find('input[name=titulo]').val();
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="80"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:80px; color:red"></td>';
									tr_warning += '<td width="150"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:150px; color:red"></td>';
									tr_warning += '<td width="400"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:400px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-comfacdevolucion-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
					}
				}
				$('#forma-comfacdevolucion-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
				$('#forma-comfacdevolucion-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
				
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			Impuestos = entry['Impuestos'];
			//$id_impuesto.val(entry['iva']['0']['id_impuesto']);
			//$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
			//$tipo_cambio.val(entry['Tc']['0']['valor_tipo_cambio']);
			
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			
			//carga select de moneda de la factura denominacion con todas las monedas
			$select_moneda_fac.children().remove();
			$select_moneda_fac.append(moneda_hmtl);
			

			
			//carga select con tipo de movimiento
			$select_tipo_movimiento.children().remove();
			var tmov_hmtl = '';
			$.each(entry['TMov'],function(entryIndex,tmov){
				if(parseInt(tmov['id'])==6){
					tmov_hmtl += '<option value="' + tmov['id'] + '"  >' + tmov['titulo'] + '</option>';
				}
			});
			$select_tipo_movimiento.append(tmov_hmtl);
			
			
			//carga select con tipo de movimiento
			$select_almacen.children().remove();
			var almacen_hmtl = '';
			$.each(entry['Almacenes'],function(entryIndex,alm){
				almacen_hmtl += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
			});
			$select_almacen.append(almacen_hmtl);
			
			
			
			//carga select con tipos de nota de credito
			$select_tipo_nota.children().remove();
			var tipo_html = '';
			for(var i in arrayTipoNotaCredito){
				if(parseInt(i)==3){
					tipo_html += '<option value="' + i + '" selected="yes">' + arrayTipoNotaCredito[i] + '</option>';
				}else{
					//select_html += '<option value="' + i + '">' + arrayTipoNotaCredito[i] + '</option>';
				}
			}
			$select_tipo_nota.append(tipo_html);
					
			
			//buscador de facturas del cliente
			$busca_factura.click(function(event){
				event.preventDefault();
				if ($razon_proveedor.val()!=''){
					$busca_facturas( $select_moneda,$id_proveedor.val(),$tipo_proveedor.val(), entry['Monedas'],$factura, $fecha_factura, $monto_factura, $pagos_aplicados, $saldo, $nc_aplicados, $select_moneda_fac, $orden_compra, $select_almacen, $grid_productos, $select_almacen, entry['Almacenes']);
				}else{
					jAlert("Es necesario seleccionar un Proveedor", 'Atencion!');
				}
			});
			
			$select_moneda.change(function(){
				var id_mon = $(this).val();
				if ( parseInt($id_moneda_factura.val()) == 1 ){
					if(  parseInt(id_mon) == 2 ){
						jAlert("No se debe cambiar la moneda cuando la factura es en pesos.", 'Atencion!');
						
						$select_moneda.children().remove();
						var moneda_hmtl = '';
						$.each(entry['Monedas'],function(entryIndex,moneda){
							if( $id_moneda_factura.val() == moneda['id'] ) {
								moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							}
						});
						$select_moneda.append(moneda_hmtl);
					}
				}
			});
			
			
			
			
			//buscador de clientes
			$busca_proveedor.click(function(event){
				event.preventDefault();
				$busca_proveedores( $select_moneda,entry['Monedas'], $id_impuesto, $valor_impuesto, $razon_proveedor.val() );
				$limpiar_campos($subtotal,$impuesto,$total,$factura, $fecha_factura, $monto_factura, $monto_factura, $pagos_aplicados, $saldo, $fac_saldado, $nc_aplicados, $fecha_expedicion, $folio_nota_credito, $select_moneda_fac, $orden_compra);
			});
			
			
			//asignar evento keypress al campo Razon Social del proveedor
			$razon_proveedor.keypress(function(e){
				if(e.which==13 ) {
					$busca_proveedor.trigger('click');
				}
			});
			
			
		},"json");//termina llamada json
		
		
		
		$fecha_expedicion.val(mostrarFecha());


		$fecha_expedicion.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_expedicion.DatePicker({
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
				$fecha_expedicion.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_expedicion.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_expedicion.val(mostrarFecha());
					}else{
						$fecha_expedicion.DatePickerHide();	
					}
				}
			}
		});
		
		
		
		
                
		$boton_actualizar.bind('click',function(){
			jConfirm('Confirmar devoluci&oacute;n?', 'Dialogo de Confirmacion', function(r) {
				// If they confirmed, manually trigger a form submission
				if (r) {
					$submit_actualizar.trigger('click');
				}else{
					//aqui no hay nada
				}
			});
			return false;
		});

	
	
		var contar_seleccionados= function($grid_productos){
			var seleccionados=0;
			
			$grid_productos.find('input[name=micheck]').each(function(){
				if(this.checked){
					seleccionados = parseInt(seleccionados) + 1;
				}
			});
			return seleccionados;
		}

			
		$submit_actualizar.bind('click',function(){
			var selec=0;
			//checa facturas a revision seleccionadas
			selec = contar_seleccionados($grid_productos);
			
			if(parseInt(selec) > 0){
				return true;
			}else{
				jAlert("No hay partidas seleccionadas para devoluci&oacute;n", 'Atencion!');
				return false;
			}
		});
	
		
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-comfacdevolucion-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-comfacdevolucion-overlay').fadeOut(remove);
		});
		
	});//termina nueva prefactura
	
	
	
	
	
	
	
	
	
	
	
	
	
	var carga_formacomfacdevolucion00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_prefactura':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  la factura?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La factura fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La factura no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-comfacdevolucion-window').remove();
			$('#forma-comfacdevolucion-overlay').remove();
            
			var form_to_show = 'formacomfacdevolucion00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_comfacdevolucion();
			
			$('#forma-comfacdevolucion-window').css({"margin-left": -435, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-comfacdevolucion-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDevolucion.json';
				$arreglo = {'id_nota_credito':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
							
				var $identificador = $('#forma-comfacdevolucion-window').find('input[name=identificador]');
				var $folio = $('#forma-comfacdevolucion-window').find('input[name=folio]');
				var $folio_nota_credito = $('#forma-comfacdevolucion-window').find('input[name=folio_nota_credito]');
				var $fecha_expedicion = $('#forma-comfacdevolucion-window').find('input[name=fecha_expedicion]');
				
				var $generar = $('#forma-comfacdevolucion-window').find('input[name=generar]');
				var $fac_saldado = $('#forma-comfacdevolucion-window').find('input[name=fac_saldado]');
				
				var $busca_proveedor = $('#forma-comfacdevolucion-window').find('a[href*=busca_proveedor]');
				var $id_proveedor = $('#forma-comfacdevolucion-window').find('input[name=id_proveedor]');
				var $numero_proveedor = $('#forma-comfacdevolucion-window').find('input[name=numero_proveedor]');
				var $razon_proveedor = $('#forma-comfacdevolucion-window').find('input[name=razon_proveedor]');
				var $tipo_proveedor = $('#forma-comfacdevolucion-window').find('input[name=tipo_proveedor]');
				
				var $select_moneda = $('#forma-comfacdevolucion-window').find('select[name=select_moneda]');
				var $id_impuesto = $('#forma-comfacdevolucion-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-comfacdevolucion-window').find('input[name=valorimpuesto]');
				var $tipo_cambio = $('#forma-comfacdevolucion-window').find('input[name=tipo_cambio]');
				
				var $concepto = $('#forma-comfacdevolucion-window').find('textarea[name=concepto]');
				
				var $subtotal = $('#forma-comfacdevolucion-window').find('input[name=subtotal]');
				var $ieps_nota = $('#forma-comfacdevolucion-window').find('input[name=ieps_nota]');
				var $impuesto = $('#forma-comfacdevolucion-window').find('input[name=impuesto]');
				var $total = $('#forma-comfacdevolucion-window').find('input[name=total]');
				var $descargar_pdf = $('#forma-comfacdevolucion-window').find('#descargar_pdf');
				var $cancelado = $('#forma-comfacdevolucion-window').find('input[name=cancelado]');
				
				var $busca_factura = $('#forma-comfacdevolucion-window').find('a[href*=busca_factura]');
				var $factura = $('#forma-comfacdevolucion-window').find('input[name=factura]');
				var $select_moneda_fac = $('#forma-comfacdevolucion-window').find('select[name=select_moneda_fac]');
				var $orden_compra = $('#forma-comfacdevolucion-window').find('input[name=orden_compra]');
				var $id_moneda_factura = $('#forma-comfacdevolucion-window').find('input[name=id_moneda_factura]');
				var $fecha_factura = $('#forma-comfacdevolucion-window').find('input[name=fecha_factura]');
				var $monto_factura = $('#forma-comfacdevolucion-window').find('input[name=monto_factura]');
				var $pagos_aplicados = $('#forma-comfacdevolucion-window').find('input[name=pagos_aplicados]');
				var $nc_aplicados = $('#forma-comfacdevolucion-window').find('input[name=nc_aplicados]');
				var $saldo = $('#forma-comfacdevolucion-window').find('input[name=saldo]');
				
				var $select_tipo_movimiento = $('#forma-comfacdevolucion-window').find('select[name=select_tipo_movimiento]');
				var $select_almacen = $('#forma-comfacdevolucion-window').find('select[name=select_almacen]');
				var $select_tipo_nota = $('#forma-comfacdevolucion-window').find('select[name=select_tipo_nota]');
				
				//tabla contenedor del listado de productos
				var $grid_productos = $('#forma-comfacdevolucion-window').find('#grid_productos');
				
				var $cerrar_plugin = $('#forma-comfacdevolucion-window').find('#close');
				var $cancelar_plugin = $('#forma-comfacdevolucion-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-comfacdevolucion-window').find('#submit');
				var $boton_actualizar = $('#forma-comfacdevolucion-window').find('#boton_actualizar');
				
				$generar.val('false');
				$fac_saldado.val('false');
				$busca_proveedor.hide();
				$busca_factura.hide();
				$boton_actualizar.hide();
				$razon_proveedor.attr("readonly", true);
				//$permitir_solo_numeros($importe);
				$cancelado.hide();
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-comfacdevolucion-window').find('div.interrogacion').css({'display':'none'});
						
						if( $generar.val() == 'true' ){
							jAlert("Se gener&oacute; la Nota de Cr&eacute;dito: "+data['folio'], 'Atencion!');
						}else{
							jAlert("Los datos de la Nota de Cr&eacute;dito se han guardado con &eacute;xito", 'Atencion!');
						}
						
						$get_datos_grid();
						var remove = function() {$(this).remove();};
						$('#forma-comfacdevolucion-overlay').fadeOut(remove);
						
					}else{
						//habilitar boton actualizar
						$submit_actualizar.removeAttr('disabled');
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-comfacdevolucion-window').find('div.interrogacion').css({'display':'none'});
						$grid_productos.find('input[name=cant_dev]').css({'background' : '#ffffff'});
						
						$('#forma-comfacdevolucion-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-comfacdevolucion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						$('#forma-comfacdevolucion-window').find('.comfacdevolucion_div_one').css({'height':'585px'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-comfacdevolucion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								var campo = tmp.split(':')[0];
								
								$('#forma-comfacdevolucion-window').find('#div_warning_grid').css({'display':'block'});
								var $campo = $grid_productos.find('#'+campo).css({'background' : '#d41000'});
								
								var codigo_producto = $campo.parent().parent().find('input[name=sku]').val();
								var titulo_producto = $campo.parent().parent().find('input[name=titulo]').val();
								
								var tr_warning = '<tr>';
										tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
										tr_warning += '<td width="80"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:80px; color:red"></td>';
										tr_warning += '<td width="150"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:150px; color:red"></td>';
										tr_warning += '<td width="400"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:400px; color:red"></td>';
								tr_warning += '</tr>';
								
								$('#forma-comfacdevolucion-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								
							}
						}
						$('#forma-comfacdevolucion-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
						$('#forma-comfacdevolucion-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
						
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					Impuestos = entry['Impuestos'];
					$identificador.val(entry['datosNota']['0']['id']);
					$folio.val(entry['datosNota']['0']['folio']);
					$folio_nota_credito.val(entry['datosNota']['0']['nota_credito']);
					$fecha_expedicion.val(entry['datosNota']['0']['fecha_expedicion']);
					$id_proveedor.val(entry['datosNota']['0']['id_proveedor']);
					$numero_proveedor.val(entry['datosNota']['0']['numero_proveedor']);
					$razon_proveedor.val(entry['datosNota']['0']['proveedor']);
					//$id_impuesto.val(entry['datosNota']['0']['']);
					$id_impuesto.val(entry['iva']['0']['id_impuesto']);
					$valor_impuesto.val(entry['datosNota']['0']['valor_impuesto']);
					//$observaciones.text(entry['datosNota']['0']['observaciones']);
					$tipo_cambio.val(entry['datosNota']['0']['tipo_cambio']);
					$concepto.text(entry['datosNota']['0']['concepto']);
					
					
					$subtotal.val($(this).agregar_comas(parseFloat(entry['datosNota']['0']['subtotal']).toFixed(2)));
					$ieps_nota.val($(this).agregar_comas(parseFloat(entry['datosNota']['0']['ieps_nota']).toFixed(2)));
					$impuesto.val($(this).agregar_comas(parseFloat(entry['datosNota']['0']['impuesto']).toFixed(2)));
					$total.val($(this).agregar_comas(parseFloat(entry['datosNota']['0']['total']).toFixed(2)));
					
					$factura.val(entry['datosNota']['0']['factura']);
					$orden_compra.val(entry['datosNota']['0']['orden_compra']);
					$id_moneda_factura.val(entry['datosNota']['0']['id_moneda_factura']);
					$fecha_factura.val(entry['datosNota']['0']['fecha_factura']);
					$monto_factura.val($(this).agregar_comas(parseFloat(entry['datosNota']['0']['cantidad_factura']).toFixed(2)));
					$pagos_aplicados.val($(this).agregar_comas(parseFloat(entry['datosNota']['0']['total_pagos']).toFixed(2)));
					$nc_aplicados.val($(this).agregar_comas(parseFloat(entry['datosNota']['0']['total_notas_creditos']).toFixed(2)));
					$saldo.val($(this).agregar_comas(entry['datosNota']['0']['saldo_factura']));
					
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosNota']['0']['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//if(parseInt(flujo_proceso)==2){
							//	moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							//}
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					//carga select moneda factura
					$select_moneda_fac.children().remove();
					moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosNota']['0']['id_moneda_fac']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda_fac.append(moneda_hmtl);
					
                    
			
					//carga select con tipo de movimiento
					$select_tipo_movimiento.children().remove();
					var tmov_hmtl = '';
					$.each(entry['TMov'],function(entryIndex,tmov){
						if(parseInt(tmov['id'])==parseInt(entry['datosNota']['0']['id_tipo_mov'])){
							tmov_hmtl += '<option value="' + tmov['id'] + '"  >' + tmov['titulo'] + '</option>';
						}
					});
					$select_tipo_movimiento.append(tmov_hmtl);
			
			
					//carga select con tipo de movimiento
					$select_almacen.children().remove();
					var almacen_hmtl = '';
					$.each(entry['Almacenes'],function(entryIndex,alm){
						if(parseInt(alm['id'])==parseInt(entry['datosNota']['0']['id_almacen'])){
							almacen_hmtl += '<option value="' + alm['id'] + '"  selectted="yes">' + alm['titulo'] + '</option>';
						}
					});
					$select_almacen.append(almacen_hmtl);
                    
                    
					//carga select con tipos de nota de credito
					$select_tipo_nota.children().remove();
					var tipo_html = '';
					for(var i in arrayTipoNotaCredito){
						if(parseInt(i)==parseInt(entry['datosNota']['0']['tipo'])){
							tipo_html += '<option value="' + i + '" selected="yes">' + arrayTipoNotaCredito[i] + '</option>';
						}else{
							//select_html += '<option value="' + i + '">' + arrayTipoNotaCredito[i] + '</option>';
						}
					}
					$select_tipo_nota.append(tipo_html);
                    
                    //aqui se le asigna 0 al tipo de proveedor porque solo interesa mostrar los datos, ya esta filtrado desde que se creo el registro
                    var tipo_proveedor = 0;
                    
					$grid_productos.children().remove();
					
					//crea el tr con los datos del producto seleccionado
					$.each(entry['datosGrid'],function(entryIndex,partida){
						//obtiene numero de trs
						var notr = $("tr", $grid_productos).size();
						notr++;
						
						var id_detalle = partida['id_detalle'];
						var check='checked';
						var desactivado='disabled="disabled"';
						var valor_seleccionado = '1';
						var id_producto = partida['producto_id'];
						var codigo_producto = partida['sku'];
						var titulo_producto = partida['titulo'];
						var unidad_medida = partida['unidad'];
						var pres_id = partida['pres_id'];
						var pres = partida['presentacion'];
						
						var cantidad_fac = partida['cantidad_fac'];
						var costo_unitario = partida['costo_unitario'];
						var importe = partida['importe'];
						var cant_devuelto = partida['cantidad_devuelto'];
						var id_impuesto_partida = partida['id_impuesto'];
						var cant_devolucion = partida['cantidad_devolucion'];
						
						var id_ieps = partida['id_ieps'];
						var tasa_ieps = partida['valor_ieps'];
						var importe_ieps = partida['importe_ieps'];
						
						var nuevo_tr = crear_tr(tipo_proveedor, notr, id_detalle, check,desactivado,valor_seleccionado, id_producto, codigo_producto, titulo_producto, unidad_medida, cantidad_fac, costo_unitario, importe, cant_devuelto, id_impuesto_partida, cant_devolucion, pres_id, pres, id_ieps, tasa_ieps, importe_ieps);
						
						$grid_productos.append(nuevo_tr);
					});
					
                    
                    
					var nota_nota_cancelada = entry['datosNota']['0']['cancelado'];
					
					if ( nota_nota_cancelada == 'true'){
						//aqui hay que desahabilitar campos
						$cancelado.show();
						$folio.attr('disabled','-1');
						$folio_nota_credito.attr('disabled','-1');
						$fecha_expedicion.attr('disabled','-1');
						$numero_proveedor.attr('disabled','-1');
						$razon_proveedor.attr('disabled','-1');
						$select_moneda.attr('disabled','-1');
						$tipo_cambio.attr('disabled','-1');
						$concepto.attr('disabled','-1');
						$subtotal.attr('disabled','-1');
						$impuesto.attr('disabled','-1');
						$total.attr('disabled','-1');
						$factura.attr('disabled','-1');
						$fecha_factura.attr('disabled','-1');
						$monto_factura.attr('disabled','-1');
						$pagos_aplicados.attr('disabled','-1');
						$nc_aplicados.attr('disabled','-1');
						$saldo.attr('disabled','-1');
						$descargar_pdf.attr('disabled','-1');
						$select_tipo_movimiento.attr('disabled','-1');
						$select_moneda_fac.attr('disabled','-1');
						$select_tipo_nota.attr('disabled','-1');
						$select_almacen.attr('disabled','-1');
						$orden_compra.attr('disabled','-1');
						$grid_productos.find('input').attr('disabled','-1');
						$grid_productos.find('select').attr('disabled','-1');
						
					}
					
					
					if ( entry['datosNota']['0']['serie_folio'] != ''){
							$folio.attr("readonly", true);
							$folio_nota_credito.attr("readonly", true);
							$tipo_cambio.attr("readonly", true);
							$concepto.attr("readonly", true);
							//$observaciones.attr("readonly", true);
							$submit_actualizar.hide();
					}else{
						if(!$descargar_pdf.is(':disabled')) {
							$descargar_pdf.attr('disabled','-1');
							$descargar_xml.attr('disabled','-1');
							$cancelar_nota_credito.attr('disabled','-1');
							//$submit_actualizar.hide();
						}
					}
					
				});//termina llamada json
                
					
				
				//descargar pdf de la Nota de Credito
				$descargar_pdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfNotaCreditoProveedor/'+$identificador.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
                
                
				
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-comfacdevolucion-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-comfacdevolucion-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllComFacDevolucion.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllComFacDevolucion.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formacomfacdevolucion00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



