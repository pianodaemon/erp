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
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/provnotascredito";
    
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
	$('#barra_titulo').find('#td_titulo').append('Notas de Cr&eacute;dito - Proveedores');
	
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
		var $select_prod_tipo = $('#forma-provnotascredito-window').find('select[name=prodtipo]');
		$('#forma-provnotascredito-window').find('#submit').mouseover(function(){
			$('#forma-provnotascredito-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-provnotascredito-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-provnotascredito-window').find('#submit').mouseout(function(){
			$('#forma-provnotascredito-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-provnotascredito-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-provnotascredito-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-provnotascredito-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-provnotascredito-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-provnotascredito-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-provnotascredito-window').find('#close').mouseover(function(){
			$('#forma-provnotascredito-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-provnotascredito-window').find('#close').mouseout(function(){
			$('#forma-provnotascredito-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-provnotascredito-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-provnotascredito-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-provnotascredito-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-provnotascredito-window').find("ul.pestanas li").click(function() {
			$('#forma-provnotascredito-window').find(".contenidoPes").hide();
			$('#forma-provnotascredito-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-provnotascredito-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
		
		//asignamos la Razon Social del Proveedor al campo Nombre
		$campo_nombre.val(razon_social_proveedor);
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProveedores.json';
			$arreglo = {    'rfc':$campo_rfc.val(),
							'email':$campo_email.val(),
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
					$('#forma-provnotascredito-window').find('input[name=id_proveedor]').val(id_proveedor);
					$('#forma-provnotascredito-window').find('input[name=numero_proveedor]').val($(this).find('#no_proveedor').val());
					$('#forma-provnotascredito-window').find('input[name=tipo_proveedor]').val($(this).find('#tipo_prov').val());
					$('#forma-provnotascredito-window').find('input[name=razon_proveedor]').val($(this).find('#razon_social').html());
					
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
	$busca_facturas = function($select_moneda, id_proveedor, array_monedas, $factura, $fecha_factura, $monto_factura, $pagos_aplicados, $saldo, $nc_aplicados){
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
						
						$('#forma-provnotascredito-window').find('input[name=id_moneda_factura]').val(id_moneda);
						//var monto_pagos_aplicados=parseFloat(monto_factura) - parseFloat(saldo_factura);
						
						$factura.val(factura);
						$fecha_factura.val(fecha_factura);
						$monto_factura.val( $(this).agregar_comas( parseFloat(monto_factura).toFixed(2)));
						$pagos_aplicados.val( $(this).agregar_comas(parseFloat(pagos_aplicado).toFixed(2)));
						$nc_aplicados.val( $(this).agregar_comas(parseFloat(nc_aplicado).toFixed(2)));
						$saldo.val( $(this).agregar_comas(parseFloat(saldo_factura).toFixed(2)) );
						
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
			if(parseFloat($campo_input.val())<1){
				$campo_input.val('');
			}
		});
	}
	
	$aplicar_evento_blur = function($campo_input ){
		$campo_input.blur(function(){
			if($campo_input.val()=="" || parseFloat($campo_input.val())==0){
				$campo_input.val(0.00);//si el campo esta en blanco, pone cero
			}
		});
	}
	
	
	$limpiar_campos = function($importe,$ieps,$impuesto,$total,$factura, $fecha_factura, $monto_factura, $monto_factura, $pagos_aplicados, $saldo, $fac_saldado, $nc_aplicados, $fecha_expedicion, $folio_nota_credito){
		$importe.val('');
		$ieps.val('');
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
	}
	
	
	$calcula_total_nota_credito = function($importe, $ieps, $impuesto, $total, $valor_impuesto, $saldo_factura,$chkbox_aplicar_saldo, $fac_saldado,evaluar ){
		var SubTotal = 0; //aqui va el valor del importe
		var Impuesto = 0; //monto del iva calculado a partir del importe
		var Total = 0; //suma del subtotal + totalImpuesto - impuestoRetenido
		var saldo_fac=quitar_comas($saldo_factura.val());
		
		SubTotal = $importe.val();
		Impuesto = parseFloat(SubTotal) * parseFloat($valor_impuesto.val());
		Total = parseFloat(SubTotal) + parseFloat(Impuesto);
		
		$importe.val(parseFloat(SubTotal).toFixed(2));
		$impuesto.val(parseFloat(Impuesto).toFixed(2));
		$total.val(parseFloat(Total).toFixed(2));
		$ieps.val(parseFloat(0).toFixed(2));
		//si evaluar es igual a verdadero, se checa que el monto de la Nota de Credito no sea mayor que el saldo
		//cuando es nueva Nota de Credito se evalua
		//cuando solo es ver detalle de una Nota de Credito generada con anterioridad, ya no es necesario hacer esta evaluacion
		if(evaluar=="true"){
			//aqui entra cuando el total de la nota de credito sobrepasa al saldo de la factura
			if(parseFloat($total.val()) > parseFloat(saldo_fac) ){
				jAlert("El Total de la Nota de Cr&eacute;dito no debe ser mayor que el saldo de la factura.\n	Total Nota Cr&eacute;dito: "+parseFloat(Total).toFixed(2)+"\n	Saldo Factura: "+$saldo_factura.val()+"\nSe asignara por default el saldo de la factura al total de la Nota.", 'Atencion!');
				
				SubTotal = saldo_fac / ( parseFloat($valor_impuesto.val()) + 1 );
				Impuesto = parseFloat(SubTotal) * parseFloat($valor_impuesto.val());
				
				//alert(SubTotal);
				$importe.val(parseFloat(SubTotal).toFixed(2));
				$impuesto.val(parseFloat(Impuesto).toFixed(2));
				$total.val( quitar_comas($saldo_factura.val()) );
			}
		}
		
		
		if(parseFloat($total.val()) == parseFloat(saldo_fac) ){
			$fac_saldado.val('true');
		}
		
		
	}
	
	
	
	
	
	
	
	//nueva prefactura
	$new_nota_credito.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_provnotascredito();
		
		var form_to_show = 'formaprovnotascredito00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-provnotascredito-window').css({"margin-left": -340, 	"margin-top": -200});
		
		$forma_selected.prependTo('#forma-provnotascredito-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getNotaCredito.json';
		$arreglo = {'id_nota_credito':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
		var $identificador = $('#forma-provnotascredito-window').find('input[name=identificador]');
		var $folio = $('#forma-provnotascredito-window').find('input[name=folio]');
		var $folio_nota_credito = $('#forma-provnotascredito-window').find('input[name=folio_nota_credito]');
		var $fecha_expedicion = $('#forma-provnotascredito-window').find('input[name=fecha_expedicion]');
		
		var $generar = $('#forma-provnotascredito-window').find('input[name=generar]');
		var $fac_saldado = $('#forma-provnotascredito-window').find('input[name=fac_saldado]');
		
		var $busca_proveedor = $('#forma-provnotascredito-window').find('a[href*=busca_proveedor]');
		var $id_proveedor = $('#forma-provnotascredito-window').find('input[name=id_proveedor]');
		var $numero_proveedor = $('#forma-provnotascredito-window').find('input[name=numero_proveedor]');
		var $razon_proveedor = $('#forma-provnotascredito-window').find('input[name=razon_proveedor]');
		
		var $select_moneda = $('#forma-provnotascredito-window').find('select[name=moneda]');
		var $id_impuesto = $('#forma-provnotascredito-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-provnotascredito-window').find('input[name=valorimpuesto]');
		var $observaciones = $('#forma-provnotascredito-window').find('textarea[name=observaciones]');
		var $select_moneda = $('#forma-provnotascredito-window').find('select[name=select_moneda]');
		var $tipo_cambio = $('#forma-provnotascredito-window').find('input[name=tipo_cambio]');
		
		var $concepto = $('#forma-provnotascredito-window').find('textarea[name=concepto]');
		
		var $importe = $('#forma-provnotascredito-window').find('input[name=importe]');
		var $ieps = $('#forma-provnotascredito-window').find('input[name=ieps]');
		var $impuesto = $('#forma-provnotascredito-window').find('input[name=impuesto]');
		var $total = $('#forma-provnotascredito-window').find('input[name=total]');
		var $generar_nota_credito = $('#forma-provnotascredito-window').find('#generar_nota_credito');
		var $cancelar_nota_credito = $('#forma-provnotascredito-window').find('#cancelar_nota_credito');
		var $descargar_pdf = $('#forma-provnotascredito-window').find('#descargar_pdf');
		var $descargar_xml = $('#forma-provnotascredito-window').find('#descargar_xml');
		var $cancelado = $('#forma-provnotascredito-window').find('input[name=cancelado]');
		
		var $busca_factura = $('#forma-provnotascredito-window').find('a[href*=busca_factura]');
		var $factura = $('#forma-provnotascredito-window').find('input[name=factura]');
		var $id_moneda_factura = $('#forma-provnotascredito-window').find('input[name=id_moneda_factura]');
		var $fecha_factura = $('#forma-provnotascredito-window').find('input[name=fecha_factura]');
		var $monto_factura = $('#forma-provnotascredito-window').find('input[name=monto_factura]');
		var $pagos_aplicados = $('#forma-provnotascredito-window').find('input[name=pagos_aplicados]');
		var $nc_aplicados = $('#forma-provnotascredito-window').find('input[name=nc_aplicados]');
		var $saldo = $('#forma-provnotascredito-window').find('input[name=saldo]');
		var $chkbox_aplicar_saldo = $('#forma-provnotascredito-window').find('input[name=chkbox_aplicar_saldo]');
		var $select_tipo_nota = $('#forma-provnotascredito-window').find('select[name=select_tipo_nota]');
		
		var $cerrar_plugin = $('#forma-provnotascredito-window').find('#close');
		var $cancelar_plugin = $('#forma-provnotascredito-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-provnotascredito-window').find('#submit');
		
		$identificador.val(0);//para nueva cotizacion el folio es 0
		//$submit_actualizar.hide();
		//$generar_nota_credito.hide();
		
		$permitir_solo_numeros($tipo_cambio);
		$permitir_solo_numeros($importe);
		$permitir_solo_numeros($total);
		
		//$aplicar_evento_blur($importe);
		//$aplicar_evento_focus($importe);
		
		$generar_nota_credito.attr('disabled','-1');
		$cancelar_nota_credito.attr('disabled','-1');
		$descargar_pdf.attr('disabled','-1');
		$descargar_xml.attr('disabled','-1');
		$fac_saldado.val('false');
		$cancelado.hide();
		
		//quitar enter a todos los campos input
		$('#forma-provnotascredito-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los datos se han guardado con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-provnotascredito-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-provnotascredito-window').find('div.interrogacion').css({'display':'none'});

				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-provnotascredito-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
					}
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			//$id_impuesto.val(entry['iva']['0']['id_impuesto']);
			//$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
			$tipo_cambio.val(entry['Tc']['0']['valor_tipo_cambio']);
			
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			
			
			//carga select con tipos de nota de credito
			$select_tipo_nota.children().remove();
			var tipo_html = '';
			for(var i in arrayTipoNotaCredito){
				if(parseInt(i)!=3){
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
					$busca_facturas( $select_moneda,$id_proveedor.val(), entry['Monedas'],$factura, $fecha_factura, $monto_factura, $pagos_aplicados, $saldo, $nc_aplicados );
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
				$limpiar_campos($importe,$ieps,$impuesto,$total,$factura, $fecha_factura, $monto_factura, $monto_factura, $pagos_aplicados, $saldo, $fac_saldado, $nc_aplicados, $fecha_expedicion, $folio_nota_credito);
			});
			
			//asignar evento keypress al campo Razon Social del proveedor
			$razon_proveedor.keypress(function(e){
				if(e.which==13 ) {
					$busca_proveedor.trigger('click');
				}
			});
			
			
		},"json");//termina llamada json
		
		
		
		
		$chkbox_aplicar_saldo.bind('click',function(event){
			var saldo_fac=parseFloat(quitar_comas($saldo.val()));
			var SubTotal = 0;
			if( parseFloat(saldo_fac) >0 ){
				if($(this).is(':checked')){
					SubTotal = saldo_fac / ( parseFloat($valor_impuesto.val()) + 1 );
					$importe.val(SubTotal);
				}else{
					$importe.val(SubTotal);
				}
				var evaluar="true";//esta variable es para decidir  si va a evaluar que el monto de la nota de credito no sea mayor que en Saldo de la factura
				$calcula_total_nota_credito($importe, $ieps, $impuesto, $total, $valor_impuesto, $saldo, $chkbox_aplicar_saldo,$fac_saldado,evaluar );
			}else{
				this.checked = false;
			}
		});
		
		
		
		
		
		//elimina cero al hacer clic sobre el campo
		$total.focus(function(e){
			if ($razon_proveedor.val()!=''){
				if(parseFloat($total.val())<1){
					$total.val('');
				}
			}else{
				jAlert("Es necesario seleccionar un Proveedor antes de ingresar el Total", 'Atencion!');
			}
		});
		
		
		
		$total.blur(function(){
			if($total.val()=="" || parseFloat($total.val())<=0){
				$total.val(parseFloat(0.00).toFixed(2));//si el campo esta en blanco, pone cero
			}
			//aqui el calculo se origina del total porque el total fue  ingresado
			var SubTotal = parseFloat(quitar_comas($total.val())) / ( parseFloat($valor_impuesto.val()) + 1 );
			$importe.val(SubTotal);
			
			var evaluar="true";
			$calcula_total_nota_credito($importe, $ieps, $impuesto, $total, $valor_impuesto, $saldo,$chkbox_aplicar_saldo,$fac_saldado, evaluar );
		});
		
		
		
		
		
		
		//elimina cero al hacer clic sobre el campo
		$importe.focus(function(e){
			if ($razon_proveedor.val()!=''){
				if(parseFloat($importe.val())<1){
					$importe.val('');
				}
			}else{
				jAlert("Es necesario seleccionar un Proveedor antes de ingresar el Importe", 'Atencion!');
			}
		});
		
		
		
		$importe.blur(function(){
			if($importe.val()=="" || parseFloat($importe.val())<=0){
				$importe.val(parseFloat(0.00).toFixed(2));//si el campo esta en blanco, pone cero
			}
			var evaluar="true";
			$calcula_total_nota_credito($importe, $ieps, $impuesto, $total, $valor_impuesto, $saldo,$chkbox_aplicar_saldo,$fac_saldado,evaluar );
		});
		
		
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
		
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-provnotascredito-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-provnotascredito-overlay').fadeOut(remove);
		});
		
	});//termina nueva prefactura
	
	
	
	
	
	
	
	
	
	
	
	
	
	var carga_formaprovnotascredito00_for_datagrid00 = function(id_to_show, accion_mode){
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
			$('#forma-provnotascredito-window').remove();
			$('#forma-provnotascredito-overlay').remove();
            
			var form_to_show = 'formaprovnotascredito00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_provnotascredito();
			
			$('#forma-provnotascredito-window').css({"margin-left": -340, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-provnotascredito-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getNotaCredito.json';
				$arreglo = {'id_nota_credito':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
						
				var $identificador = $('#forma-provnotascredito-window').find('input[name=identificador]');
				var $folio = $('#forma-provnotascredito-window').find('input[name=folio]');
				var $folio_nota_credito = $('#forma-provnotascredito-window').find('input[name=folio_nota_credito]');
				var $fecha_expedicion = $('#forma-provnotascredito-window').find('input[name=fecha_expedicion]');
				var $generar = $('#forma-provnotascredito-window').find('input[name=generar]');
				var $fac_saldado = $('#forma-provnotascredito-window').find('input[name=fac_saldado]');
				
				var $busca_proveedor = $('#forma-provnotascredito-window').find('a[href*=busca_proveedor]');
				var $id_proveedor = $('#forma-provnotascredito-window').find('input[name=id_proveedor]');
				var $numero_proveedor = $('#forma-provnotascredito-window').find('input[name=numero_proveedor]');
				var $razon_proveedor = $('#forma-provnotascredito-window').find('input[name=razon_proveedor]');
				
				var $id_impuesto = $('#forma-provnotascredito-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-provnotascredito-window').find('input[name=valorimpuesto]');
				var $observaciones = $('#forma-provnotascredito-window').find('textarea[name=observaciones]');
				var $select_moneda = $('#forma-provnotascredito-window').find('select[name=select_moneda]');
				var $tipo_cambio = $('#forma-provnotascredito-window').find('input[name=tipo_cambio]');
				var $concepto = $('#forma-provnotascredito-window').find('textarea[name=concepto]');
				
				var $importe = $('#forma-provnotascredito-window').find('input[name=importe]');
				var $ieps = $('#forma-provnotascredito-window').find('input[name=ieps]');
				var $impuesto = $('#forma-provnotascredito-window').find('input[name=impuesto]');
				var $total = $('#forma-provnotascredito-window').find('input[name=total]');
				
				var $busca_factura = $('#forma-provnotascredito-window').find('a[href*=busca_factura]');
				var $factura = $('#forma-provnotascredito-window').find('input[name=factura]');
				var $id_moneda_factura = $('#forma-provnotascredito-window').find('input[name=id_moneda_factura]');
				var $fecha_factura = $('#forma-provnotascredito-window').find('input[name=fecha_factura]');
				var $monto_factura = $('#forma-provnotascredito-window').find('input[name=monto_factura]');
				var $pagos_aplicados = $('#forma-provnotascredito-window').find('input[name=pagos_aplicados]');
				var $nc_aplicados = $('#forma-provnotascredito-window').find('input[name=nc_aplicados]');
				var $saldo = $('#forma-provnotascredito-window').find('input[name=saldo]');
				var $chkbox_aplicar_saldo = $('#forma-provnotascredito-window').find('input[name=chkbox_aplicar_saldo]');
				var $generar_nota_credito = $('#forma-provnotascredito-window').find('#generar_nota_credito');
				var $cancelar_nota_credito = $('#forma-provnotascredito-window').find('#cancelar_nota_credito');
				var $descargar_pdf = $('#forma-provnotascredito-window').find('#descargar_pdf');
				var $descargar_xml = $('#forma-provnotascredito-window').find('#descargar_xml');
				var $cancelado = $('#forma-provnotascredito-window').find('input[name=cancelado]');
				var $select_tipo_nota = $('#forma-provnotascredito-window').find('select[name=select_tipo_nota]');
				
				var $cerrar_plugin = $('#forma-provnotascredito-window').find('#close');
				var $cancelar_plugin = $('#forma-provnotascredito-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-provnotascredito-window').find('#submit');
				
				$chkbox_aplicar_saldo.attr('disabled','-1');
				$generar.val('false');
				$fac_saldado.val('false');
				$busca_proveedor.hide();
				$busca_factura.hide();
				$permitir_solo_numeros($tipo_cambio);
				//$permitir_solo_numeros($importe);
				$cancelado.hide();
				$razon_proveedor.attr("readonly", true);
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-provnotascredito-window').find('div.interrogacion').css({'display':'none'});
						
						if( $generar.val() == 'true' ){
							jAlert("Se gener&oacute; la Nota de Cr&eacute;dito: "+data['folio'], 'Atencion!');
						}else{
							jAlert("Los datos de la Nota de Cr&eacute;dito se han guardado con &eacute;xito", 'Atencion!');
						}
						
						$get_datos_grid();
						var remove = function() {$(this).remove();};
						$('#forma-provnotascredito-overlay').fadeOut(remove);
						
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-provnotascredito-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-provnotascredito-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
							}
						}
						
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					
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
					$observaciones.text(entry['datosNota']['0']['observaciones']);
					$tipo_cambio.val(entry['datosNota']['0']['tipo_cambio']);
					$concepto.text(entry['datosNota']['0']['concepto']);
					
					
					$importe.val(entry['datosNota']['0']['subtotal']);
					$ieps.val(entry['datosNota']['0']['ieps_nota']);
					$impuesto.val(entry['datosNota']['0']['impuesto']);
					$total.val(entry['datosNota']['0']['total']);
					
					$factura.val(entry['datosNota']['0']['factura']);
					$id_moneda_factura.val(entry['datosNota']['0']['id_moneda_factura']);
					$fecha_factura.val(entry['datosNota']['0']['fecha_factura']);
					$monto_factura.val(entry['datosNota']['0']['cantidad_factura']);
					$pagos_aplicados.val(parseFloat(entry['datosNota']['0']['total_pagos']).toFixed(2));
					$nc_aplicados.val(parseFloat(entry['datosNota']['0']['total_notas_creditos']).toFixed(2));
					$saldo.val(entry['datosNota']['0']['saldo_factura']);
					
					
					
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
					
					if(entry['datosNota']['0']['tipo']=='3'){
						$cancelar_nota_credito.attr('disabled','-1');
					}
					
					var nota_nota_cancelada = entry['datosNota']['0']['cancelado'];
					
					if ( nota_nota_cancelada == 'true'){
						//aqui hay que desahabilitar campos
						$cancelado.show();
						$folio.attr('disabled','-1');
						$folio_nota_credito.attr('disabled','-1');
						$fecha_expedicion.attr('disabled','-1');
						$numero_proveedor.attr('disabled','-1');
						$razon_proveedor.attr('disabled','-1');
						$observaciones.attr('disabled','-1');
						$select_moneda.attr('disabled','-1');
						$tipo_cambio.attr('disabled','-1');
						$concepto.attr('disabled','-1');
						$importe.attr('disabled','-1');
						$ieps.attr('disabled','-1');
						$impuesto.attr('disabled','-1');
						$total.attr('disabled','-1');
						$factura.attr('disabled','-1');
						$fecha_factura.attr('disabled','-1');
						$monto_factura.attr('disabled','-1');
						$pagos_aplicados.attr('disabled','-1');
						$nc_aplicados.attr('disabled','-1');
						$saldo.attr('disabled','-1');
						$chkbox_aplicar_saldo.attr('disabled','-1');
						$descargar_pdf.attr('disabled','-1');
						$descargar_xml.attr('disabled','-1');
						$generar_nota_credito.attr('disabled','-1');
						$cancelar_nota_credito.attr('disabled','-1');
						$descargar_pdf.attr('disabled','-1');
						$descargar_xml.attr('disabled','-1');
						$importe.attr("readonly", true);
						$tipo_cambio.attr("readonly", true);
						$concepto.attr("readonly", true);
						$observaciones.attr("readonly", true);
						$submit_actualizar.hide();
					}
					
					
					if ( entry['datosNota']['0']['serie_folio'] != ''){
						//aqui hay que desahabilitar campos
						if(!$generar_nota_credito.is(':disabled')) {
							$generar_nota_credito.attr('disabled','-1');
							$importe.attr("readonly", true);
							$folio.attr("readonly", true);
							$folio_nota_credito.attr("readonly", true);
							$tipo_cambio.attr("readonly", true);
							$concepto.attr("readonly", true);
							$observaciones.attr("readonly", true);
							$submit_actualizar.hide();
						}
					}else{
						if(!$descargar_pdf.is(':disabled')) {
							$descargar_pdf.attr('disabled','-1');
							$descargar_xml.attr('disabled','-1');
							$cancelar_nota_credito.attr('disabled','-1');
							//$submit_actualizar.hide();
						}
					}
					
					/*
					var evaluar="false";//esta variable es para decidir  si va a evaluar que el monto de la nota de credito no sea mayor que en Saldo de la factura
					if ( entry['datosNota']['0']['serie_folio'] == '' && nota_nota_cancelada != 'true'){
						evaluar="true";
					}else{
						
						//---------------------------------------------------------------------------------------------------------------------------------
						//si la Nota de Credito no ha sido generada y no esta cancelada, aplicamos el evento blur
						$total.blur(function(){
							if($total.val()=="" || parseFloat($total.val())<=0){
								$total.val(parseFloat(0.00).toFixed(2));//si el campo esta en blanco, pone cero
							}
							//aqui el calculo se origina del total porque el total fue  ingresado
							var SubTotal = parseFloat(quitar_comas($total.val())) / ( parseFloat($valor_impuesto.val()) + 1 );
							$importe.val(SubTotal);
							$calcula_total_nota_credito($importe,$ieps, $impuesto, $total, $valor_impuesto, $saldo,$chkbox_aplicar_saldo, $fac_saldado,evaluar );
						});
						//---------------------------------------------------------------------------------------------------------------------------------
						
					}
					//llamada a la funcion que calcula el total
					$calcula_total_nota_credito($importe,$ieps, $impuesto, $total, $valor_impuesto, $saldo,$chkbox_aplicar_saldo, $fac_saldado,evaluar );
					*/
				});//termina llamada json
                
				
				
				
				
				$generar_nota_credito.click(function(event){
					$generar.val('true');
					if( parseFloat($importe.val()) <= parseFloat(quitar_comas($saldo.val()))){
						jConfirm('Confirmar creaci&oacute;n de Nota de Cr&eacute;dito?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) {
								$submit_actualizar.trigger('click');
							}else{
								//aqui no hay nada
								$generar.val('false');
							}
						});
					}else{
						jAlert("El importe es mayor que el saldo de la factura. No es posible crear la Nota de Cr&eacute;dito", 'Atencion!');
					}
				});
				
					
				
				//descargar pdf de la Nota de Credito
				$descargar_pdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfNotaCreditoProveedor/'+$identificador.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
                
                
				
				
				//cancelar Nota de Credito
				$cancelar_nota_credito.click(function(event){
					event.preventDefault();
					var id_to_show = 0;
					$(this).modalPanel_cancelaemision();
					var form_to_show = 'formaCancelaEmision';
					$('#' + form_to_show).each (function(){this.reset();});
					var $forma_selected = $('#' + form_to_show).clone();
					$forma_selected.attr({id : form_to_show + id_to_show});
					$('#forma-cancelaemision-window').css({"margin-left": -100,"margin-top": -180});
					$forma_selected.prependTo('#forma-cancelaemision-window');
					$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
					
					var $motivo_cancelacion = $('#forma-cancelaemision-window').find('textarea[name=motivo_cancel]');
					
					var $cancel = $('#forma-cancelaemision-window').find('a[href*=cancel]');
					var $salir = $('#forma-cancelaemision-window').find('a[href*=salir]');
					
					
					//generar informe mensual
					$cancel.click(function(event){
						event.preventDefault();
						
						if($motivo_cancelacion.val()!=null && $motivo_cancelacion.val()!=""){
							
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/cancelarNotaCredito.json';
							$arreglo = {'id_nota':$identificador.val(),
										'motivo':$motivo_cancelacion.val(),
										'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
										}
							
							$.post(input_json,$arreglo,function(entry){
								
								var cad = entry['success'].split(":");
								if(cad[1]=='false'){
									
									jAlert("No se ha podido cancelar la Nota de Cr&eacute;dito: "+cad[0]+"", 'Atencion!');
									
								}else{
									$descargar_pdf.attr('disabled','-1');
									$cancelar_nota_credito.attr('disabled','-1');
									jAlert("La Nota de Cr&eacute;dito "+cad[0]+"  se ha cancelado con &eacute;xito", 'Atencion!');
									$get_datos_grid();
								}
								
								var remove = function() {$(this).remove();};
								$('#forma-cancelaemision-overlay').fadeOut(remove);
							});//termina llamada json
						}else{
							jAlert("Es necesario ingresar el motivo de la cancelaci&oacute;n", 'Atencion!');
						}
					});
					
					
					$salir.click(function(event){
						event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-cancelaemision-overlay').fadeOut(remove);
					});
					
				});//termina cancelar factura

				
				
				
				
				$importe.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						$importe.attr("readonly", true);//cuando se ingese el Importe, el Total debe quedar como solo lectura
						return true;
					}else {
						return false;
					}
				});
				
				$total.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						$importe.attr("readonly", true);//cuando se ingese el Total, el Importe debe quedar como solo lectura
						return true;
					}else {
						return false;
					}
				});
				

                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-provnotascredito-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-provnotascredito-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllProvNotasCredito.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllProvNotasCredito.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaprovnotascredito00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



