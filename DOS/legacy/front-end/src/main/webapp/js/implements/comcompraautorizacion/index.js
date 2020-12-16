$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};

	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
        var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());

	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/comcompraautorizacion";

	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');

	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});

	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Autorizaci&oacute;n de Orden de Compra');

	//barra para el buscador
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();


	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
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
		$busqueda_folio.val('');
		$busqueda_proveedor.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_folio.focus();
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
		$busqueda_folio.focus();
	});
	

	//Aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_proveedor, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	

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

   //--------------------------------------------------terminan las acciones del buscador------------------------------------------

	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-comcomprasautoriza-window').find('select[name=prodtipo]');
            $('#forma-comcomprasautoriza-window').find('#submit').mouseover(function(){
                $('#forma-comcomprasautoriza-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
            })
            $('#forma-comcomprasautoriza-window').find('#submit').mouseout(function(){
                $('#forma-comcomprasautoriza-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
            })
            $('#forma-comcomprasautoriza-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-comcomprasautoriza-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-comcomprasautoriza-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-comcomprasautoriza-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })

            $('#forma-comcomprasautoriza-window').find('#close').mouseover(function(){
                $('#forma-comcomprasautoriza-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-comcomprasautoriza-window').find('#close').mouseout(function(){
                $('#forma-comcomprasautoriza-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })

            $('#forma-comcomprasautoriza-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-comcomprasautoriza-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-comcomprasautoriza-window').find(".contenidoPes:first").show(); //Show first tab content

            //On Click Event
            $('#forma-comcomprasautoriza-window').find("ul.pestanas li").click(function() {
                $('#forma-comcomprasautoriza-window').find(".contenidoPes").hide();
                $('#forma-comcomprasautoriza-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-comcomprasautoriza-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}



	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}

	//funcion para hacer que un campo solo acepte numeros
	$permitir_solo_numeros = function($campo){
		//validar campo costo, solo acepte numeros y punto
		$campo.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}

	//carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, elemento_seleccionado){
		$campo_select.children().remove();
		var select_html = '';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				if(parseInt(elemento_seleccionado)==0){
					select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
				}
			}
		}
		$campo_select.append(select_html);
	}




	var carga_formacomcomprasautoriza00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){

			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_pedido':id_to_show,
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
			$('#forma-comcomprasautoriza-window').remove();
			$('#forma-comcomprasautoriza-overlay').remove();

			var form_to_show = 'formacomcomprasautoriza00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});

			$(this).modalPanel_comcomprasautoriza();

			$('#forma-comcomprasautoriza-window').css({"margin-left": -340,"margin-top": -220});

			$forma_selected.prependTo('#forma-comcomprasautoriza-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});

			$tabs_li_funxionalidad();

			var $total_tr = $('#forma-comcomprasautoriza-window').find('input[name=total_tr]');
			var $id_orden_compra = $('#forma-comcomprasautoriza-window').find('input[name=id_orden_compra]');
			var $accion_proceso = $('#forma-comcomprasautoriza-window').find('input[name=accion_proceso]');
			var $folio = $('#forma-comcomprasautoriza-window').find('input[name=folio]');
			var $fecha = $('#forma-comcomprasautoriza-window').find('input[name=fecha]');
			
			var $id_proveedor = $('#forma-comcomprasautoriza-window').find('input[name=id_proveedor]');
			var $no_prov = $('#forma-comcomprasautoriza-window').find('input[name=no_prov]');
			var $rfc_prov = $('#forma-comcomprasautoriza-window').find('input[name=rfc_prov]');
			var $razon_proveedor = $('#forma-comcomprasautoriza-window').find('input[name=razonproveedor]');
			var $empresa_immex = $('#forma-comcomprasautoriza-window').find('input[name=empresa_immex]');
			var $tasa_ret_immex = $('#forma-comcomprasautoriza-window').find('input[name=tasa_ret_immex]');
			var $grupo = $('#forma-comcomprasautoriza-window').find('input[name=grupo]');
			var $select_via_embarque = $('#forma-comcomprasautoriza-window').find('select[name=via_envarque]');
			var consigandoA= $('#forma-comcomprasautoriza-window').find('input[name=consigandoA]');
			var $select_moneda = $('#forma-comcomprasautoriza-window').find('select[name=select_moneda]');
			var $select_moneda_original = $('#forma-comcomprasautoriza-window').find('input[name=select_moneda_original]');
			var $tipo_cambio = $('#forma-comcomprasautoriza-window').find('input[name=tipo_cambio]');
			var $orden_compra = $('#forma-comcomprasautoriza-window').find('input[name=orden_compra]');
			var $id_impuesto = $('#forma-comcomprasautoriza-window').find('input[name=id_impuesto]');
			var $valor_impuesto = $('#forma-comcomprasautoriza-window').find('input[name=valorimpuesto]');
			var $observaciones = $('#forma-comcomprasautoriza-window').find('textarea[name=observaciones]');
			var $select_condiciones = $('#forma-comcomprasautoriza-window').find('select[name=select_condiciones]');
			var $transporte = $('#forma-comcomprasautoriza-window').find('input[name=transporte]');
			
			var $autorizar = $('#forma-comcomprasautoriza-window').find('#autorizar');
			var $descargarpdf = $('#forma-comcomprasautoriza-window').find('#descargarpdf');
			var $cancelar_orden_compra = $('#forma-comcomprasautoriza-window').find('#cancelar_ordencompra');

			var $cancelado = $('#forma-comcomprasautoriza-window').find('input[name=cancelado]');
			var $autorizado =$('#forma-comcomprasautoriza-window').find('input[name=autorizada]');
			//grid de productos
			var $grid_productos = $('#forma-comcomprasautoriza-window').find('#grid_productos');
			//grid de errores
			var $grid_warning = $('#forma-comcomprasautoriza-window').find('#div_warning_grid').find('#grid_warning');

			//var $flete = $('#forma-comcomprasautoriza-window').find('input[name=flete]');
			var $subtotal = $('#forma-comcomprasautoriza-window').find('input[name=subtotal]');
			var $impuesto = $('#forma-comcomprasautoriza-window').find('input[name=impuesto]');
			var $campo_impuesto_retenido = $('#forma-comcomprasautoriza-window').find('input[name=impuesto_retenido]');
			var $total = $('#forma-comcomprasautoriza-window').find('input[name=total]');

			var $cerrar_plugin = $('#forma-comcomprasautoriza-window').find('#close');
			var $cancelar_plugin = $('#forma-comcomprasautoriza-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-comcomprasautoriza-window').find('#submit');

			
			$empresa_immex.val('false');
			$tasa_ret_immex.val(0);
			$cancelado.hide();
			$autorizar.hide();
			$cancelar_orden_compra.hide();

			if(accion_mode == 'edit'){
				$accion_proceso.attr({'value' : "autorizar"});
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getOrden_Compra.json';
				var $arreglo = {'id_orden_compra':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};

				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-comcomprasautoriza-window').find('div.interrogacion').css({'display':'none'});

						if($accion_proceso.val() == 'cancelar'){
							//if ( data['actualizo'] == "1" ){
								jAlert("La Orden de Compra se Cancel&oacute; con &eacute;xito", 'Atencion!');
							//}else{
							//	jAlert(data['actualizo'], 'Atencion!');
							//}
						}
						if($accion_proceso.val() == 'autorizar'){
							//if ( data['actualizo'] == "1" ){
								jAlert("La Orden Compra fue Autorizada con &eacute;xito", 'Atencion!');
							//}else{
							//	jAlert(data['actualizo'], 'Atencion!');
							//}
						}

						var remove = function() {$(this).remove();};
						$('#forma-comcomprasautoriza-overlay').fadeOut(remove);

						//ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
						$submit_actualizar.hide();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-comcomprasautoriza-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-comcomprasautoriza-window').find('.comcomprasautoriza_div_one').css({'height':'568px'});//con errores
						$('#forma-comcomprasautoriza-window').find('div.interrogacion').css({'display':'none'});

						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});

						$('#forma-comcomprasautoriza-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-comcomprasautoriza-window').find('#div_warning_grid').find('#grid_warning').children().remove();

						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
                                                        tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');

							if( longitud.length > 1 ){
								$('#forma-comcomprasautoriza-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

								//alert(tmp.split(':')[0]);

								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
											$('#forma-comcomprasautoriza-window').find('.comcomprasautoriza_div_one').css({'height':'568px'});
											$('#forma-comcomprasautoriza-window').find('#div_warning_grid').css({'display':'block'});

											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
												$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}else{
												if(tmp.split(':')[0].substring(0, 5) == 'costo'){
													$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
												}
											}

											var tr_warning = '<tr>';
												tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
												tr_warning += '<td width="120">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="200">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="235">';
												tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:230px; color:red">';
												tr_warning += '</td>';
											tr_warning += '</tr>';
											$grid_warning.append(tr_warning);
										}
									}
								}
							}
						}

						$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
						$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});

					}
				}

				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);

				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$id_orden_compra.val(entry['Datos'][0]['id']);
					$folio.val(entry['Datos'][0]['folio']);
					$fecha.val(entry['Datos'][0]['fecha']);
					$id_proveedor.val(entry['Datos'][0]['proveedor_id']);
					$no_prov.val(entry['Datos'][0]['no_proveedor']);
					$rfc_prov.val(entry['Datos'][0]['rfc']);
					$razon_proveedor.val(entry['Datos'][0]['razon_social']);
					$grupo.val(entry['Datos'][0]['grupo']);
					consigandoA.val(entry['Datos'][0]['consignado_a']);
					$observaciones.text(entry['Datos'][0]['observaciones']);
					$orden_compra.val(entry['Datos'][0]['orden_compra']);
					$transporte.val(entry['Datos'][0]['transporte']);
					$tipo_cambio.val(entry['Datos'][0]['tipo_cambio']);
					
					$subtotal.val(entry['Datos'][0]['subtotal']);
					$impuesto.val(entry['Datos'][0]['impuesto']);
					$total.val(entry['Datos'][0]['total']);

					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['Datos'][0]['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}/*else{
							//if(parseInt(entry['datosCompra'][0]['proceso_flujo_id'])==4){
							//	moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							//}
						}*/
					});
					$select_moneda.append(moneda_hmtl);


					$id_impuesto.val(entry['iva'][0]['id_impuesto']);
					$valor_impuesto.val(entry['iva'][0]['valor_impuesto']);

					$select_condiciones.children().remove();
					var terminos_html = '';
					$.each(entry['Condiciones'],function(entryIndex,Condiciones){
						if(Condiciones['id'] == entry['Datos'][0]['cxp_prov_credias_id']){
							terminos_html += '<option value="' + Condiciones['id'] + '"  selected="yes">' + Condiciones['descripcion'] + '</option>';
							//$select_moneda_original.val(moneda['id']);
						}
                                                //else{
						//	if(parseInt(entry['Datos'][0]['status'])==0){
						//		terminos_html += '<option value="' + Condiciones['id'] + '"  >' + Condiciones['descripcion'] + '</option>';
						//	}
						//}
					});

					$select_condiciones.append(terminos_html);





					$select_via_embarque.children().remove();
					var via_embarque_html = '';
					$.each(entry['via_embarque'],function(entryIndex,via_embarque){
						if(via_embarque['id'] == entry['Datos'][0]['tipo_embarque_id']){
							via_embarque_html += '<option value="' + via_embarque['id'] + '"  selected="yes">' + via_embarque['tipo_embarque'] + '</option>';
							//$select_moneda_original.val(moneda['id']);
						}/*else{
							if(parseInt(entry['Datos'][0]['status'])==0){
								via_embarque_html += '<option value="' + via_embarque['id'] + '"  >' + via_embarque['tipo_embarque'] + '</option>';
							}
						}*/
					});
					$select_via_embarque.append(via_embarque_html);


					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prod){

							//obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;

							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
								trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
								trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
								trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';//este es el id del registro que ocupa el producto en la tabla comcomprasautoriza_detalles
									//trr += '<span id="elimina">1</span>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
								trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['inv_prod_id'] +'">';
								trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
								trr += '<INPUT TYPE="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
							trr += '</td>';
								trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
								trr += '<INPUT type="hidden" name="id_presentacion"  value="'+  prod['id_presentacion'] +'" id="idpres">';
								trr += '<INPUT TYPE="text" 	name="presentacion'+ tr +'" value="'+  prod['presentacion'] +'" id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<INPUT TYPE="text" 	name="cantidad" value="'+  prod['cantidad'] +'" id="cant" style="width:76px;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" style="width:86px; text-align:right;">';
								trr += '<INPUT type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 	id="import" readOnly="true" style="width:86px; text-align:right;">';
								trr += '<INPUT type="hidden"    name="id_imp_prod"  value="'+  prod['gral_imp_id'] +'" id="idimppord">';
								trr += '<INPUT type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" id="ivalorimp">';
								trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+parseFloat(prod['importe']) * parseFloat( prod['valor_imp'] )+'">';
							trr += '</td>';
							trr += '</tr>';
							$grid_productos.append(trr);

							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							/*$grid_productos.find('#cant').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
                                                        */
							//recalcula importe al perder enfoque el campo cantidad
							/*$grid_productos.find('#cant').blur(function(){
								if ($(this).val() == ''){
									$(this).val(' ');
								}
								if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cost').val() != ' ') ){
									//calcula el importe
									$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cost').val()));
									//redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val( Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100 );
									$(this).parent().parent().find('#import').val( parseFloat($(this).parent().parent().find('#import').val()).toFixed(2) );

									//calcula el impuesto para este producto multiplicando el importe por el valor del iva
									$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat(  $(this).parent().parent().find('#ivalorimp').val()  ));

								}else{
									$(this).parent().parent().find('#import').val('');
									$(this).parent().parent().find('#totimp').val('');
								}
								$calcula_totales();//llamada a la funcion que calcula totales
							});
                                                        */
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							/*$grid_productos.find('#cost').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
                                                        */
							//recalcula importe al perder enfoque el campo costo
							/*$grid_productos.find('#cost').blur(function(){
								if ($(this).val() == ''){
									$(this).val(' ');
								}
								if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cant').val() != ' ') ){
									//calcula el importe
									$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cant').val()));
									//redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
									$(this).parent().parent().find('#import').val( parseFloat($(this).parent().parent().find('#import').val()).toFixed(2));

									//calcula el impuesto para este producto multiplicando el importe por el valor del iva
									$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat( $(this).parent().parent().find('#ivalorimp').val()  ));
								}else{
									$(this).parent().parent().find('#import').val('');
									$(this).parent().parent().find('#totimp').val('');
								}
								$calcula_totales();//llamada a la funcion que calcula totales
							});
                                                                */
							//validar campo costo, solo acepte numeros y punto
							//$permitir_solo_numeros( $grid_productos.find('#cost') );
							//$permitir_solo_numeros( $grid_productos.find('#cant') );

							//elimina un producto del grid
							/*$grid_productos.find('#delete'+ tr).bind('click',function(event){
								event.preventDefault();
								if(parseInt($(this).parent().find('#elim').val()) != 0){
									var iddetalle = $(this).parent().find('#idd').val();

									//asigna espacios en blanco a todos los input de la fila eliminada
									$(this).parent().parent().find('input').val('');

									//asigna un 0 al input eliminado como bandera para saber que esta eliminado
									$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
									$(this).parent().find('#idd').val(iddetalle);
									//oculta la fila eliminada
									$(this).parent().parent().hide();
									$calcula_totales();//llamada a la funcion que calcula totales
								}
							});*/
						});
					}

					//$calcula_totales();//llamada a la funcion que calcula totales

					//oculta los estatus de compra antes de que se verifique su estado
					//$cancelar_pedido.hide();
					$submit_actualizar.hide();
					$cancelado.hide();
					$autorizar.hide();

					var proceso_flujo = entry['Datos'][0]['status'];

					//proceso_flujo_id=0 :Orden Generada
					if(entry['Datos'][0]['status']==0){
						$autorizar.show();
						$cancelar_orden_compra.show();
						$autorizado.hide();
					}

					//proceso_flujo_id=1 :Autorizado
					if(entry['Datos'][0]['status']==1){
						$autorizado.show();
						//$cancelar_pedido.hide();
						$submit_actualizar.hide();
						$autorizar.hide();
						$cancelar_orden_compra.hide();
					}

					//si es refacturacion, no se puede cambiar los datos del grid, solo el header de la factura
					if(entry['Datos'][0]['status']==2){

						$submit_actualizar.hide();
						$autorizado.hide();
						$cancelar_orden_compra.hide();
						$cancelado.show();
						$autorizar.hide();

						$folio.attr('disabled','-1'); //deshabilitar
						$grupo.attr('disabled','-1'); //deshabilitar
						
						$no_prov.attr('disabled','-1'); //deshabilitar
						$razon_proveedor.attr('disabled','-1'); //deshabilitar
						$observaciones.attr('disabled','-1'); //deshabilitar
						$tipo_cambio.attr('disabled','-1'); //deshabilitar
						$orden_compra.attr('disabled','-1'); //deshabilitar
						$descargarpdf.attr('disabled','-1'); //deshabilitar

						consigandoA.attr('disabled','-1'); //deshabilitar
						$select_moneda.attr('disabled','-1'); //deshabilitar
						$select_via_embarque.attr('disabled','-1'); //deshabilitar
						$select_condiciones.attr('disabled','-1');

						//$grid_productos.find('a[href*=elimina_producto]').hide();
						$grid_productos.find('#cant').attr('disabled','-1'); //deshabilitar campos cantidad del grid
						$grid_productos.find('#cost').attr('disabled','-1'); //deshabilitar campos costo del grid
						$grid_productos.find('#import').attr('disabled','-1'); //deshabilitar campos importe del grid

						$subtotal.attr('disabled','-1'); //deshabilitar
						$impuesto.attr('disabled','-1'); //deshabilitar
						$campo_impuesto_retenido.attr('disabled','-1'); //deshabilitar
						$total.attr('disabled','-1'); //deshabilitar
					}


					$submit_actualizar.hide();
					
					$observaciones.attr("readonly", true);
					$tipo_cambio.attr("readonly", true);
					$orden_compra.attr("readonly", true);
					$transporte.attr("readonly", true);

					$grid_productos.find('a[href*=elimina_producto]').hide();
					$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
					$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid

				});//termina llamada json


				$tipo_cambio.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				
				$autorizar.click(function(e){
					$accion_proceso.attr({'value' : "autorizar"});
					jConfirm('Confirmar Autorizacion de la  Orden de Compra?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.parents("FORM").submit();
						}else{
							$accion_proceso.attr({'value' : "edit"});
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});




				$cancelar_orden_compra.click(function(e){
					$accion_proceso.attr({'value' : "cancelar"});
					jConfirm('Desea Cancelar la Orden de Compra?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
						   $submit_actualizar.parents("FORM").submit();
						}else{
							$accion_proceso.attr({'value' : "edit"});
						}
					});
					// Always return     false here since we don't know what jConfirm is going to do
					return false;
				});


				$descargarpdf.click(function(event){
					event.preventDefault();
					if(parseInt($id_orden_compra.val())>0 ){
						var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdf/'+$id_orden_compra.val() +'/'+ $('#forma-comcomprasautoriza-window').find('select[name=select_export]').val() +'/'+ $('#lienzo_recalculable').find('input[name=iu]').val() +'/out.json';
						window.location.href=input_json;
					}else{
						jAlert("Nose esta enviando el identificador  de la Orden de Compra","Atencion!!!")
					}
				 });



				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
					if(parseInt(trCount) > 0){
						$grid_productos.find('tr').each(function (index){
							$(this).find('#cost').val(quitar_comas( $(this).find('#cost').val() ));
						});
						return true;
					}else{
						jAlert("No hay datos para actualizar", 'Atencion!');
						return false;
					}
				});

				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-comcomprasautoriza-overlay').fadeOut(remove);
				});

				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-comcomprasautoriza-overlay').fadeOut(remove);
				});

			}
		}
	}




    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllCompras.json';

        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();

        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllCompras.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}

        $.post(input_json,$arreglo,function(data){

            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formacomcomprasautoriza00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();


});



