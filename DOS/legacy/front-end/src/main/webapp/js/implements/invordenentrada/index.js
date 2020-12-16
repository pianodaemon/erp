$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select tipo de Documento
    var arrayTiposDocumento = {
				1:"Factura", 
				2:"Remision",
				3:"Ajuste",
				4:"Produccion"
			};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invordenentrada";
	
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	//var $new_entrada = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Ordenes de Entrada');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	
	var $cadena_busqueda = "";
	var $campo_busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $campo_busqueda_oc = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_oc]');
	var $campo_busqueda_factura = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_factura]');
	var $campo_busqueda_proveedor = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_proveedor]');
	var $campo_select_tipo_doc = $('#barra_buscador').find('.tabla_buscador').find('select[name=select_tipo_doc]');
	var $campo_busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	
	var tiposIva = new Array(); //este arreglo carga los select del grid cada que se agrega un nuevo producto
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $campo_busqueda_folio.val() + "|";
		valor_retorno += "orden_compra" + signo_separador + $campo_busqueda_oc.val() + "|";
		valor_retorno += "factura" + signo_separador + $campo_busqueda_factura.val() + "|";
		valor_retorno += "proveedor" + signo_separador + $campo_busqueda_proveedor.val() + "|";
		valor_retorno += "tipo_doc" + signo_separador + $campo_select_tipo_doc.val() + "|";
		valor_retorno += "codigo" + signo_separador + $campo_busqueda_codigo.val();
		return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	$buscar.click(function(event){
		event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	$limpiar.click(function(event){
		event.preventDefault();
		$campo_busqueda_folio.val('');
		$campo_busqueda_oc.val('');
		$campo_busqueda_factura.val('');
		$campo_busqueda_proveedor.val(''); 
		$campo_busqueda_codigo.val(''); 
		
		$campo_select_tipo_doc.children().remove();
		var select_html = '<option value="0" selected="yes">[-- --]</option>';
		for(var i in arrayTiposDocumento){
			select_html += '<option value="' + i + '" >' + arrayTiposDocumento[i] + '</option>';
		}
		$campo_select_tipo_doc.append(select_html);
		
		$campo_busqueda_folio.focus();
	});


	
	$campo_select_tipo_doc.children().remove();
	var select_html = '<option value="0" selected="yes">[-- --]</option>';
	for(var i in arrayTiposDocumento){
		select_html += '<option value="' + i + '" >' + arrayTiposDocumento[i] + '</option>';
	}
	$campo_select_tipo_doc.append(select_html);
	
	

	//visualizar  la barra del buscador
	TriggerClickVisializaBuscador = 0;
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
			 $('#barra_buscador').animate({height: '60px'}, 500);
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
		
		$campo_busqueda_folio.focus();
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_oc, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_factura, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_proveedor, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_select_tipo_doc, $buscar);
		
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-invordenentrada-window').find('select[name=prodtipo]');
		$('#forma-invordenentrada-window').find('#submit').mouseover(function(){
			$('#forma-invordenentrada-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-invordenentrada-window').find('#submit').mouseout(function(){
			$('#forma-invordenentrada-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		
		$('#forma-invordenentrada-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invordenentrada-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-invordenentrada-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invordenentrada-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invordenentrada-window').find('#close').mouseover(function(){
			$('#forma-invordenentrada-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invordenentrada-window').find('#close').mouseout(function(){
			$('#forma-invordenentrada-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-invordenentrada-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invordenentrada-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invordenentrada-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invordenentrada-window').find("ul.pestanas li").click(function() {
			$('#forma-invordenentrada-window').find(".contenidoPes").hide();
			$('#forma-invordenentrada-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invordenentrada-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
        
        
        
	//----------------------------------------------------------------
	//valida la fecha seleccionada
	function fecha_mayor(fecha, fecha2){
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
	
	//valida la fecha seleccionada
	function fecha_mayor_igual(fecha, fecha2){
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
                        if (xDia >= yDia){
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
	$add_calendar = function($campo, $fecha, $condicion){
		$campo.click(function (s){
			//$campo.val('');
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$campo.DatePicker({
			format:'Y-m-d',
			date: $campo.val(),
			current: $campo.val(),
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
				$campo.val(formated);
				if (formated.match(patron) ){
					
					switch($condicion){
						case '>':
							//code;
							var valida_fecha=fecha_mayor($campo.val(),mostrarFecha());
							if (valida_fecha==true){
								$campo.DatePickerHide();
							}else{
								jAlert("Fecha no valida. Debe ser mayor a la actual",'! Atencion');
								$campo.val($fecha);
							}
							break;
						case '>=':
							//code;
							var valida_fecha=fecha_mayor_igual($campo.val(),mostrarFecha());
							if (valida_fecha==true){
								$campo.DatePickerHide();
							}else{
								jAlert("Fecha no valida. Debe ser mayor o igual a la actual",'! Atencion');
								$campo.val($fecha);
							}
							break;
						case '==':
							//code;
							break;
						case '<':
							//code;
							break;
						case '<=':
							//code;
							break;
						default:
							//para cunado no se le pasan parametros de condicion de fecha
							var valida_fecha=mayor($campo.val(),mostrarFecha());
							$campo.DatePickerHide();
							break;
					}
				}
			}
		});
	}

        
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
        
	
	//calcula totales(subtotal,descuento, impuesto, total)
	$calcula_totales = function(){
		//var $arreglo_grid = $('#forma-invordenentrada-window').find('input[name=arreglo]');
		var $campo_flete = $('#forma-invordenentrada-window').find('input[name=flete]');
		var $monto_iva_flete = $('#forma-invordenentrada-window').find('input[name=iva_flete]');
		
		var $tasa_fletes = $('#forma-invordenentrada-window').find('input[name=tasafletes]');
		var $campo_subtotal = $('#forma-invordenentrada-window').find('input[name=subtotal]');
		//var $campo_descuento = $('#forma-invordenentrada-window').find('input[name=descuento]');
		var $campo_impuesto = $('#forma-invordenentrada-window').find('input[name=totimpuesto]');
		var $retencion = $('#forma-invordenentrada-window').find('input[name=retencion]');
		var $campo_total = $('#forma-invordenentrada-window').find('input[name=total]');
		var sumaImporte = 0;
		//var sumaDescuento = 0;
		var sumaImpuesto = 0;
		var sumaTotal = 0;
		var impuesto = 0.00;
		var partida=0;
		var retencionfletes=0;
		
		var $grid_productos = $('#forma-invordenentrada-window').find('#grid_productos');
		$grid_productos.find('tr').each(function (index){
			if(($(this).find('#cost').val() != ' ') && ($(this).find('#cant').val() != ' ')){
				var id_ivatipo = $(this).find('#imp').val();
				var valorImpuesto = 0;
				//alert(id_ivatipo);
				//alert("Alert1: "+$(this).parent().find('#totimp').val());
				
				/*
				$.each(tiposIva,function(entryIndex,tipos){
					if(parseInt(tipos['id'])==parseInt(id_ivatipo)){
						valorImpuesto = tipos['iva_1'];
					}
				});
				*/
				
				//obtiene el valor del impuesto
				var valorImpuesto = $(this).find('#v_imp').val();
				
				if($(this).find('#import').val()!=''){
					//Calcula total impuesto del producto actual
					$(this).find('#totimp').val(parseFloat($(this).find('#import').val()) * parseFloat(valorImpuesto));
					//$(this).find('#spantotimp').text($(this).find('#totimp').val());
				}
				
				sumaImporte = parseFloat(sumaImporte) + parseFloat($(this).find('#import').val());
				sumaImpuesto = parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
			}
			
		});
		
		
		$.each(tiposIva,function(entryIndex,tipos){
			if(parseInt(tipos['id'])==1){
				//calcula iva del flete
				$monto_iva_flete.val( parseFloat($campo_flete.val()) * parseFloat(tipos['iva_1'])  );
			}
		});
		
		sumaImpuesto = parseFloat(sumaImpuesto) + parseFloat($monto_iva_flete.val());
		
		sumaImporte = parseFloat(sumaImporte) + parseFloat($campo_flete.val());
		
		//calcula la retencion de fletes, para eso convierto la tasa de retencion 4% en su valor 0.04 dividiendola entre 100
		retencionfletes = parseFloat($campo_flete.val()) * parseFloat(parseFloat($tasa_fletes.val())/100);
		
		//se resta la retencion de fletes
		//sumaImpuesto = parseFloat(sumaImpuesto) - parseFloat(retencionfletes);
		
		//calcula el total
		sumaTotal = parseFloat(sumaImporte) + parseFloat(sumaImpuesto) - parseFloat(retencionfletes);
		
		$campo_flete.val(parseFloat($campo_flete.val()).toFixed(2));
		$campo_subtotal.val($(this).agregar_comas( parseFloat(sumaImporte).toFixed(2)));
		$campo_impuesto.val($(this).agregar_comas( parseFloat(sumaImpuesto).toFixed(2)));
		$retencion.val(parseFloat(retencionfletes).toFixed(2));
		$campo_total.val($(this).agregar_comas( parseFloat(sumaTotal).toFixed(2)));
		
	}//termina calculo de impuestos
	
	
	
	
	
	//buscar tipo de cambio de la fecha seleccionada
	function buscarTipoCambio($fecha)
	{	
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/obtener_tipo_cambio.json';
		$arreglo = {'fecha':$fecha}
		$.post(input_json,$arreglo,function(valor){
			$('#forma-invordenentrada-window').find('input[name=tc]').val(valor['tipoCambio'][0]['valor']);
		},"json");
	}
	
	
	
	$aplicar_evento_keypress = function( $campo_input ){
		//validar campo cantidad recibida, solo acepte numeros y punto
		$campo_input.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if(e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	
	
	$aplicar_evento_keypress_campo_caducidad = function( $campo_input ){
		$campo_input.keypress(function(e){
			// Permitir  borrar
			if (e.which == 8) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	
	$aplicar_evento_focus = function( $campo_input ){
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo_input.focus(function(e){
			if(parseFloat($campo_input.val())<1){
				$campo_input.val('');
			}
		});
	}
	
	
	$aplicar_evento_blur = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if(parseFloat($campo_input.val())==0||$campo_input.val()==""){
				$campo_input.val(0);
			}
		});
	}
	
	
	$aplicar_evento_focus_input = function( $campo_input ){
		//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$campo_input.focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
	}
	
	$aplicar_evento_blur_input = function( $campo_input ){
		//pone espacio en blanco al perder el enfoque, cuando no se ingresa un valor
		$campo_input.blur(function(e){
			if ( $(this).val() == ''  || $(this).val() == null ){
				$(this).val(' ');
			}
		});
	}
	

	
	//funcion que genera tr para agregar numero de lote
	$genera_tr_para_numero_de_lote = function( tipo_registro, id_detalle, id_producto, codigo, titulo, oent_det_id,  id_almacen, lote_int, lote_prov, cant_lote,valor_pedimento,fecha_caducidad, trCount){
		var tr_prod='';
			tr_prod += '<tr>';
			tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="hidden" name="no_tr" id="notr" value="'+ trCount +'">';
				tr_prod += '<input type="hidden" name="tipo" id="tip" value="'+ tipo_registro +'">';
				tr_prod += '<input type="hidden" name="id_detalle" id="iddet" value="'+  id_detalle +'">';
				tr_prod += '<input type="hidden" name="id_alm" id="idalm" value="'+  id_almacen +'">';
				tr_prod += '<input type="hidden" name="id_prod_grid" id="idprod" value="'+  id_producto +'">';
				tr_prod += '<input type="text"  name="sku" value="'+codigo+'"  id="codigo'+ trCount +'" class="borde_oculto" style="width:76px; display:none;" readOnly="true">';
				tr_prod += '<input type="hidden" name="oent_detalle_id" id="oentid" value="'+ oent_det_id +'">';
				tr_prod += '<input type="hidden" name="req_lote" id="reqlote" value="true">';
				tr_prod += '<a href="elimina_lote" class="elimina_lote'+ trCount +'">Eliminar</a>';
				tr_prod += '<input type="hidden" name="eliminado" id="elim" value="1">';//1=registro vivo, 0=Registro eliminado
			tr_prod += '</td>';
			tr_prod += '<td width="180" class="grid" align="right" style="font-size:11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="titulo" value="'+titulo+'" id="titulo'+ trCount +'" class="borde_oculto" style="width:176px; display:none;" readOnly="true">';
				tr_prod += '<a href="agrega_lote" class="agrega_lote'+ trCount +'">Agregar Lote</a>';
			tr_prod += '</td>';
			
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="hidden" name="unidad" class="borde_oculto" value="" readOnly="true" style="width:66px;">';
				tr_prod += 'Lote Int.';
			tr_prod += '</td>';
			
			tr_prod += '<td width="225" colspan="3" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="hidden" name="id_pres" id="idpres" value="0">';
				tr_prod += '<input type="text" name="cantidad" id="cant" value="0" style="width:66px; display:none;" readOnly="true">';
				tr_prod += '<input type="hidden" name="costo" id="cost" value="0" style="width:69px; text-align:right;" readOnly="true">';
				tr_prod += '<input type="text" name="lote_int" value="'+ lote_int +'" class="lote_int'+ trCount +'" style="width:220px;" readOnly="true" title="Lote Interno">';
			tr_prod += '</td>';
			/*
			tr_prod += '<td width="70" colspan="2" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="cantidad" id="cant" value="0" style="width:66px; display:none;" readOnly="true">';
			tr_prod += '</td>';
			
			
			tr_prod += '<td width="75" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="hidden" name="costo" id="cost" value="0" style="width:69px; text-align:right;" readOnly="true">';
			tr_prod += '</td>';
			*/
			tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="hidden" name="importe'+ trCount +'" id="import" value="0" style="width:76px; text-align:right;" readOnly="true">';
				tr_prod += 'Lote Prov.';
			tr_prod += '</td>';
			
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="lote_prov" value="'+ lote_prov +'" class="lote_prov'+ trCount +'" title="Ingresar Lote" style="width:68px;">';
				tr_prod += '<select name="impuesto" id="imp" style="display:none;"></select>';
				tr_prod += '<input type="hidden" name="valorimp" id="v_imp" value="0">';
				tr_prod += '<input type="hidden" name="totalimpuesto'+ trCount +'" id="totimp" value="0">';
			tr_prod += '</td>';
			
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="cant_rec" id="cant_r" value="'+cant_lote+'" class="cant_rec'+ trCount +'" title="Cantidad Lote" style="width:66px;" >';
			tr_prod += '</td>';
			
			tr_prod += '<td width="90" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="pedimento" id="ped" value="'+valor_pedimento+'" class="pedimento'+ trCount +'" style="width:86px;" >';
			tr_prod += '</td>';
			
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="caducidad" id="cad" value="' + fecha_caducidad + '" class="caducidad'+ trCount +'" style="width:66px;">';
			tr_prod += '</td>';
			
			
		tr_prod += '</tr>';
		
		return tr_prod;
	}
	
	
	
	//funcion para agregar lote y eliminar
	agregar_lote_y_eliminar = function($grid_productos,tipo_registro, trCount){
		//agregar nuevo lote
		$grid_productos.find('.agrega_lote'+ trCount ).click(function(e){
			e.preventDefault();
			$(this).hide();
			
			$tr_padre=$(this).parent().parent();
			
			var id_detalle = 0;
			var id_producto = $tr_padre.find('input[name=id_prod_grid]').val();
			var codigo = $tr_padre.find('input[name=sku]').val();
			var titulo = $tr_padre.find('input[name=titulo]').val();
			var oent_det_id = $tr_padre.find('input[name=oent_detalle_id]').val();
			var id_almacen = $tr_padre.find('input[name=id_alm]').val();
			var lote_int='';
			var lote_prov = ' ';
			var cant_lote=0;
			var valor_pedimento='';
			var fecha_caducidad='';
			
			var noTr = $("tr", $grid_productos).size();
			noTr++;
			
			//aqui es para crear nuevos registros del lote
			tr_lote = $genera_tr_para_numero_de_lote(tipo_registro, id_detalle, id_producto, codigo, titulo, oent_det_id, id_almacen, lote_int, lote_prov,cant_lote, valor_pedimento, fecha_caducidad, noTr);
			
			//agregar tr_lote despues de tr_padre
			$(tr_lote).insertAfter($tr_padre);
			
			//aplicar click al nuevo registro
			//se hace una llamada recursiva a  la funcion agregar_lote
			agregar_lote_y_eliminar($grid_productos,tipo_registro, noTr);
			$aplicar_evento_keypress( $grid_productos.find('.cant_rec'+ noTr ) );
			$aplicar_evento_blur( $grid_productos.find('.cant_rec'+ noTr ) );
			$aplicar_evento_focus( $grid_productos.find('.cant_rec'+ noTr ) );
			
			$aplicar_evento_focus_input($grid_productos.find('.lote_prov'+ noTr ));
			$aplicar_evento_blur_input($grid_productos.find('.lote_prov'+ noTr ));
			
			$aplicar_evento_focus_input($grid_productos.find('.pedimento'+ noTr ));
			$aplicar_evento_blur_input($grid_productos.find('.pedimento'+ noTr ));
			
			$aplicar_evento_focus_input($grid_productos.find('.caducidad'+ noTr ));
			$aplicar_evento_blur_input($grid_productos.find('.caducidad'+ noTr ));
			
			$aplicar_evento_keypress_campo_caducidad($grid_productos.find('.caducidad'+ noTr ) );
			$add_calendar($grid_productos.find('.caducidad'+ noTr ), mostrarFecha(), ">=");
		});
		
		
		//eliminar un lote
		$grid_productos.find('.elimina_lote'+ trCount ).click(function(e){
			e.preventDefault();
			$tr_padre=$(this).parent().parent();
			var id_detalle = $tr_padre.find('input[name=id_detalle]').val();//tomar el id_detalle
			var tipo_registro = $tr_padre.find('input[name=tipo]').val();//tomar el el tipo de registro
			$tr_padre.find('input').val('');//asignar vacio a todos los input del tr
			$tr_padre.find('input[name=eliminado]').val('0');//asignamos 0 para indicar que se ha eliminado
			$tr_padre.find('input[name=id_detalle]').val(id_detalle);//devolver el id_detalle, este es necesario para actualizar el campo
			$tr_padre.find('input[name=tipo]').val(tipo_registro);
			$tr_padre.hide();//ocultar el tr
		});
	}
	
	
	
	
	
	
	var carga_formainvordenentrada00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'no_entrada':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la entrada seleccionada?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La entrada fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La entrada no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formainvordenentrada00';
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			//var accion = "get_datos_entrada_mercancia";
			
			$(this).modalPanel_invordenentrada();
			
			$('#forma-invordenentrada-window').css({ "margin-left": -425, 	"margin-top": -230 });
			
			$forma_selected.prependTo('#forma-invordenentrada-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getOrdenEntrada.json';
				$arreglo = {'identificador':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var $identificador = $('#forma-invordenentrada-window').find('input[name=identificador]');
				var $folio_entrada = $('#forma-invordenentrada-window').find('input[name=folio_entrada]');
				var $estatus = $('#forma-invordenentrada-window').find('input[name=estatus]');
				var $accion = $('#forma-invordenentrada-window').find('input[name=accion]');
				var $select_tipo_movimiento = $('#forma-invordenentrada-window').find('select[name=select_tipo_movimiento]');
				var $tasa_fletes = $('#forma-invordenentrada-window').find('input[name=tasafletes]');
				var $total_tr = $('#forma-invordenentrada-window').find('input[name=total_tr]');
				var $campo_factura = $('#forma-invordenentrada-window').find('input[name=factura]');
				var $campo_ordencompra = $('#forma-invordenentrada-window').find('input[name=ordencompra]');
				var $campo_numeroguia = $('#forma-invordenentrada-window').find('input[name=numeroguia]');
				var $campo_expedicion = $('#forma-invordenentrada-window').find('input[name=expedicion]');
				var $select_denominacion = $('#forma-invordenentrada-window').find('select[name=denominacion]');
				var $select_fleteras = $('#forma-invordenentrada-window').find('select[name=fletera]');
				var $select_almacen_destino = $('#forma-invordenentrada-window').find('select[name=almacen_destino]');
				var $campo_tc = $('#forma-invordenentrada-window').find('input[name=tc]');
				var $campo_observaciones = $('#forma-invordenentrada-window').find('textarea[name=observaciones]');
				var $select_tipo_doc = $('#forma-invordenentrada-window').find('select[name=tipodoc]');
				
				//campos del proveedor
				var $hidden_id_proveedor = $('#forma-invordenentrada-window').find('input[name=id_proveedor]');
				var $campo_rfc_proveedor = $('#forma-invordenentrada-window').find('input[name=rfcproveedor]');
				var $campo_razon_proveedor = $('#forma-invordenentrada-window').find('input[name=razon_proveedor]');
				//var $campo_dir_proveedor = $('#forma-invordenentrada-window').find('input[name=dir_proveedor]');
				var $campo_tipo_proveedor = $('#forma-invordenentrada-window').find('input[name=tipo_proveedor]');
				var $campo_sku = $('#forma-invordenentrada-window').find('input[name=sku_producto]');
				
				//tabla contenedor del listado de productos
				var $grid_productos = $('#forma-invordenentrada-window').find('#grid_productos');
				//Campos de totales
				var $campo_flete = $('#forma-invordenentrada-window').find('input[name=flete]');
				var $campo_subtotal = $('#forma-invordenentrada-window').find('input[name=subtotal]');
				var $retencion = $('#forma-invordenentrada-window').find('input[name=retencion]');
				var $campo_impuesto = $('#forma-invordenentrada-window').find('input[name=totimpuesto]');
				var $campo_total = $('#forma-invordenentrada-window').find('input[name=total]');
				var $campo_ieps = $('#forma-invordenentrada-window').find('input[name=ieps]');
				
				var $etiqueta_flete = $('#forma-invordenentrada-window').find('#etiqueta_flete');
				var $etiqueta_ieps = $('#forma-invordenentrada-window').find('#etiqueta_ieps');
				var $etiqueta_ret = $('#forma-invordenentrada-window').find('#etiqueta_ret');
				
				
				//href para buscar producto
				var $buscar_producto = $('#forma-invordenentrada-window').find('a[href*=busca_producto]');
				//href para agregar producto al grid
				var $agregar_producto = $('#forma-invordenentrada-window').find('a[href*=agregar_producto]');
				
				var $cancelar = $('#forma-invordenentrada-window').find('#cancelar');
				var $descargar_pdf = $('#forma-invordenentrada-window').find('#descargar_pdf');
				
				var $cerrar_plugin = $('#forma-invordenentrada-window').find('#close');
				var $cancelar_plugin = $('#forma-invordenentrada-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-invordenentrada-window').find('#submit');
				//$submit_actualizar.hide();//ocultar boton para que no permita actualizar
				
				
				$campo_factura.css({'background' : '#F0F0F0'});
				$campo_ordencompra.css({'background' : '#F0F0F0'});
				$campo_numeroguia.css({'background' : '#F0F0F0'});
				$campo_expedicion.css({'background' : '#F0F0F0'});
				$campo_tc.css({'background' : '#F0F0F0'});
				
				
				$campo_flete.val(0);
				$campo_factura.attr("readonly", true);
				$campo_ordencompra.attr("readonly", true);
				$campo_numeroguia.attr("readonly", true);
				$campo_expedicion.attr("readonly", true);
				$campo_tc.attr("readonly", true);
				//$campo_observaciones.attr("readonly", true);
				$campo_flete.attr("readonly", true);
				$accion.val('edit');
				$campo_flete.hide();
				$campo_ieps.hide();
				$retencion.hide();
				$etiqueta_flete.hide();
				$etiqueta_ieps.hide();
				$etiqueta_ret.hide();
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						if ( $accion.val()=='edit'){
							jAlert("La Orden de entrada "+$folio_entrada.val()+" se guard&oacute; con &eacute;xito", 'Atencion!');
						}else{
							jAlert("La Orden de entrada "+$folio_entrada.val()+" se Cancel&oacute; con &eacute;xito", 'Atencion!');
						}
						//habilitar boton actualizar
						$submit_actualizar.removeAttr('disabled');
						var remove = function() { $(this).remove(); };
						$('#forma-invordenentrada-overlay').fadeOut(remove);
						$get_datos_grid();
					}else{
						//habilitar boton actualizar
						$submit_actualizar.removeAttr('disabled');
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-invordenentrada-window').find('div.interrogacion').css({'display':'none'});
						$grid_productos.find('input[name=pedimento]').css({'background' : '#ffffff'});
						$grid_productos.find('input[name=caducidad]').css({'background' : '#ffffff'});
						$grid_productos.find('input[name=cant_rec]').css({'background' : '#ffffff'});
						$grid_productos.find('input[name=lote_prov]').css({'background' : '#ffffff'});
						
						$('#forma-invordenentrada-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-invordenentrada-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-invordenentrada-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								var campo = tmp.split(':')[0];
								
								$('#forma-invordenentrada-window').find('.invordenentrada_div_one').css({'height':'585px'});
								$('#forma-invordenentrada-window').find('#div_warning_grid').css({'display':'block'});
								var $campo = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
								
								var codigo_producto = $campo.parent().parent().find('input[name=sku]').val();
								var titulo_producto = $campo.parent().parent().find('input[name=titulo]').val();
								
								var tr_warning = '<tr>';
										tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
										tr_warning += '<td width="100"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
										tr_warning += '<td width="200"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
										tr_warning += '<td width="420"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:420px; color:red"></td>';
								tr_warning += '</tr>';
								
								$('#forma-invordenentrada-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								
							}
						}
						$('#forma-invordenentrada-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
						$('#forma-invordenentrada-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$estatus.attr({ 'value' : entry['Datos'][0]['estatus'] });
					$tasa_fletes.attr({ 'value' : entry['Datos'][0]['tasa_retencion'] });
					$identificador.attr({ 'value' : entry['Datos'][0]['id'] });
					$folio_entrada.attr({ 'value' : entry['Datos'][0]['folio'] });
					$campo_factura.attr({ 'value' : entry['Datos'][0]['folio_doc'] });
					$campo_ordencompra.attr({ 'value' : entry['Datos'][0]['orden_compra'] });
					$campo_numeroguia.attr({ 'value' : entry['Datos'][0]['numero_guia'] });
					$campo_expedicion.attr({ 'value' : entry['Datos'][0]['fecha_doc'] });
					$campo_tc.attr({ 'value' : entry['Datos'][0]['tipo_cambio'] });
					$campo_observaciones.text(entry['Datos'][0]['observaciones']);
					
					$hidden_id_proveedor.attr({ 'value' : entry['Datos'][0]['proveedor_id'] });
					$campo_rfc_proveedor.attr({ 'value' : entry['Datos'][0]['rfc_proveedor'] });
					$campo_razon_proveedor.attr({ 'value' : entry['Datos'][0]['nombre_proveedor'] });
					$campo_tipo_proveedor.attr({ 'value' : entry['Datos'][0]['id_tipo_prov'] });
					
					$campo_flete.val(parseFloat( entry['Datos'][0]['flete']).toFixed(2));
					tiposIva = entry['Impuestos'];//asigna los tipos de impuestos al arreglo tiposIva
					
					$campo_flete.attr({ 'value' : $(this).agregar_comas(entry['Datos'][0]['flete']) });
					$campo_subtotal.attr({ 'value' : $(this).agregar_comas(entry['Datos'][0]['subtotal']) });
					$campo_ieps.attr({ 'value' : $(this).agregar_comas(entry['Datos'][0]['ieps']) });
					$retencion.attr({ 'value' : $(this).agregar_comas(entry['Datos'][0]['retencion']) });
					$campo_impuesto.attr({ 'value' : $(this).agregar_comas(entry['Datos'][0]['iva']) });
					$campo_total.attr({ 'value' : $(this).agregar_comas(entry['Datos'][0]['total']) });
					
					var countDisplay=0;
					if(parseFloat(entry['Datos'][0]['flete'])>0){
						$etiqueta_flete.show();
						$campo_flete.show();
						countDisplay++;
					}
					
					if(parseFloat(entry['Datos'][0]['ieps'])>0){
						$etiqueta_ieps.show();
						$campo_ieps.show();
						countDisplay++;
					}
					
					if(parseFloat(entry['Datos'][0]['retencion'])>0){
						$etiqueta_ret.show();
						$retencion.show();
						countDisplay++;
					}
					
					if(parseInt(countDisplay)==1){
						$('#forma-invordenentrada-window').find('.invordenentrada_div_one').css({'height':'555px'});
					}
					
					if(parseInt(countDisplay)==2){
						$('#forma-invordenentrada-window').find('.invordenentrada_div_one').css({'height':'570px'});
					}
					
					if(parseInt(countDisplay)==3){
						$('#forma-invordenentrada-window').find('.invordenentrada_div_one').css({'height':'585px'});
					}
					
						
					//carga select con tipo de documento
					$select_tipo_doc.children().remove();
					var select_html = '';
					for(var i in arrayTiposDocumento){
						if(parseInt(entry['Datos'][0]['tipo_doc'])==parseInt(i)){
							select_html += '<option value="' + i + '" selected="yes">' + arrayTiposDocumento[i] + '</option>';
						}else{
							//select_html += '<option value="' + i + '">' + arrayTiposDocumento[i] + '</option>';
						}
					}
					$select_tipo_doc.append(select_html);
					
					
					
					
					//carga select tipo de Movimiento
					$select_tipo_movimiento.children().remove();
					var tipo_mov_hmtl = '';
					$.each(entry['TMovInv'],function(entryIndex,tmov){
						if(parseInt(tmov['id']) == parseInt(entry['Datos'][0]['tipo_movimiento_id'])){
							tipo_mov_hmtl += '<option value="' + tmov['id'] + '" selected="yes">' + tmov['titulo'] + '</option>';
						}else{
							//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_tipo_movimiento.append(tipo_mov_hmtl);
					
					
					
					
					//carga select denominacion con todas las monedas
					$select_denominacion.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(parseInt(moneda['id']) == parseInt(entry['Datos'][0]['id_moneda'])){
							moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_denominacion.append(moneda_hmtl);
					
					
					//carga select almacen destino
					$select_almacen_destino.children().remove();
					//var almacen_hmtl = '<option value="0" selected="yes">[--   --]</option>';
					var almacen_hmtl="";
					$.each(entry['Almacenes'],function(entryIndex,alm){
						if(parseInt(alm['id']) == parseInt(entry['Datos'][0]['id_alm_destino'])){
							almacen_hmtl += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
						}else{
							//almacen_hmtl += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
						}
					});
					$select_almacen_destino.append(almacen_hmtl);
					
					
					
					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prodGrid){
							var trCount = $("tr", $grid_productos).size();
							trCount++;
							
							var valor_pedimento='';
							var tr_prod='';
							var tr_lote='';
							var tipo_registro='PED';
							
							 value=""  
							 value="" 
							
							tr_prod += '<tr>';
								tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="hidden" name="no_tr" id="notr" value="'+ trCount +'">';
									tr_prod += '<input type="hidden" name="tipo" id="tip" value="'+ tipo_registro +'">';
									tr_prod += '<input type="hidden" name="id_detalle" id="iddet" value="'+  prodGrid['id_detalle'] +'">';
									tr_prod += '<input type="hidden" name="oent_detalle_id" id="reqlote" value="'+ prodGrid['id_detalle'] +'">';
									tr_prod += '<input type="hidden" name="id_alm" id="idalm" value="'+  $select_almacen_destino.val() +'">';
									tr_prod += '<input type="hidden" name="id_prod_grid" id="idprod" value="'+  prodGrid['producto_id'] +'">';
									tr_prod += '<input type="hidden" name="req_lote" id="reqlote" value="'+ prodGrid['req_lote'] +'">';
									tr_prod += '<input type="hidden" name="eliminado" id="elim" value="1">';//1=registro vivo, 0=Registro eliminado
									tr_prod += '<input type="text"  name="sku" value="' + prodGrid['codigo'] + '" id="codigo'+ trCount +'" class="borde_oculto" style="width:76px;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="180" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="titulo" value="' + prodGrid['titulo'] + '" id="titulo'+ trCount +'"  class="borde_oculto" style="width:176px;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="unidad" class="borde_oculto" value="' + prodGrid['unidad'] + '" readOnly="true" style="width:66px;">';
								tr_prod += '</td>';
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="hidden" name="id_pres" id="idpres" value="'+ prodGrid['id_presentacion'] +'">';
									tr_prod += '<input type="text" name="presentacion" class="borde_oculto" value="' + prodGrid['presentacion'] + '" readOnly="true" style="width:66px;">';
									tr_prod += '<input type="text" name="lote_int" value="" class="lote_int'+ trCount +'" style="width:220px; display:none;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="cantidad" id="cant" value="' + prodGrid['cantidad'] + '"  style="width:66px;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="75" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="costo" id="cost" value="' + $(this).agregar_comas(prodGrid['costo_unitario']) + '" style="width:69px; text-align:right;" readOnly="true">';
								tr_prod += '</td>';
								
								tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="importe'+ trCount +'" id="import" value="' + $(this).agregar_comas(prodGrid['importe']) + '" style="width:76px; text-align:right;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="lote_prov" value=" " class="lote_prov'+ trCount +'" style="width:66px; display:none;">';
									tr_prod += '<select name="impuesto" id="imp" style="width:68px;">';
									//aqui se carga el select con los tipos de iva
									$.each(tiposIva,function(entryIndex,tipos){
										if(tipos['id'] == prodGrid['id_impuesto']){
											tr_prod += '<option value="' + tipos['id'] + '"  selected="yes">' + tipos['descripcion'] + '</option>';
										}else{
											//tr_prod += '<option value="' + tipos['id'] + '"  >' + tipos['descripcion'] + '</option>';	
										}
									});
									
									if(parseInt(prodGrid['id_impuesto'])==0){
										//si no trae impuesto se agrega vacio
										tr_prod += '<option value="0"  >[--- ---]</option>';
									}
									tr_prod += '</select>';
									tr_prod += '<input type="hidden" name="valorimp" id="v_imp" value="' + prodGrid['valor_imp'] + '">';
									tr_prod += '<input type="hidden" name="totalimpuesto'+ trCount +'" id="totimp" value="0">';
								tr_prod += '</td>';
								
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="cant_rec" id="cant_r" value="' + prodGrid['cant_rec'] + '" class="cant_rec'+ trCount +'" style="width:66px;" >';
								tr_prod += '</td>';
								
								tr_prod += '<td width="90" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="pedimento" id="ped" value="" class="pedimento'+ trCount +'" style="width:86px; display:none;" >';
								tr_prod += '</td>';
								
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="caducidad" id="cad" value="" class="caducidad'+ trCount +'" style="width:66px; display:none;">';
								tr_prod += '</td>';
								
							tr_prod += '</tr>';
							$grid_productos.append(tr_prod);//agrega el tr a la tabla
							
							//aqui empieza a agregar lotes si es que el producto tiene lote
							var id_detalle = 0;
							var id_producto = 0;
							var oent_detalle_id=0;
							var codigo="";
							var titulo="";
							var id_almacen = 0;
							var lote_prov = '';
							var trCount2=0;
							//var valor_pedimento='';
							var fecha_caducidad='';
							//estos  se utiliza pra nuevo lote y editar lote
							codigo = prodGrid['codigo'];
							titulo = prodGrid['titulo'];
							
							
							//if(prodGrid['req_lote']=='true' ){
								if( parseInt(entry['Datos']['0']['estatus'])==0 ){
									trCount2 = $("tr", $grid_productos).size();//obtener de nuevo el numero de trs de la tabla
									trCount2++;
									id_detalle = 0;
									id_producto = prodGrid['producto_id'];
									oent_detalle_id = prodGrid['id_detalle'];
									id_almacen = $select_almacen_destino.val();
									lote_int='';
									lote_prov = ' ';
									cant_lote=0;
									tipo_registro = 'LOT';
									valor_pedimento='';
									fecha_caducidad='';
									//aqui es para crear nuevos registros del lote
									tr_lote = $genera_tr_para_numero_de_lote(tipo_registro, id_detalle, id_producto,codigo,titulo, oent_detalle_id, id_almacen, lote_int, lote_prov,cant_lote, valor_pedimento, fecha_caducidad, trCount2);
									$grid_productos.append(tr_lote);
									
									//Esta funcion es para agregar un nuevo lote
									//En esta funcion se le aplica evento click a los href Agregar Lote y Eliminar
									agregar_lote_y_eliminar($grid_productos,tipo_registro, trCount2);
									$aplicar_evento_keypress( $grid_productos.find('.cant_rec'+ trCount2 ) );
									$aplicar_evento_blur( $grid_productos.find('.cant_rec'+ trCount2 ) );
									$aplicar_evento_focus( $grid_productos.find('.cant_rec'+ trCount2 ) );
									
									$aplicar_evento_focus_input($grid_productos.find('.lote_prov'+ trCount2 ));
									$aplicar_evento_blur_input($grid_productos.find('.lote_prov'+ trCount2 ));
									if(prodGrid['req_lote']=='false'){
										$grid_productos.find('.lote_prov'+ trCount2 ).attr({ 'readOnly':true });
									}
									
									$aplicar_evento_focus_input($grid_productos.find('.pedimento'+ trCount2 ));
									$aplicar_evento_blur_input($grid_productos.find('.pedimento'+ trCount2 ));
									
									$aplicar_evento_focus_input($grid_productos.find('.caducidad'+ trCount2 ));
									$aplicar_evento_blur_input($grid_productos.find('.caducidad'+ trCount2 ));
									
									$aplicar_evento_keypress_campo_caducidad($grid_productos.find('.caducidad'+ trCount2 ) );
									$add_calendar($grid_productos.find('.caducidad'+ trCount2 ), mostrarFecha(), ">=");
									
									$descargar_pdf.attr('disabled','-1'); //deshabilitar
								}else{
									var cont=0;
									$.each(entry['Lotes'],function(entryIndex,lote){
										trCount2 = $("tr", $grid_productos).size();//obtener de nuevo el numero de trs de la tabla
										
										id_detalle = lote['id_lote'];
										id_producto = lote['inv_prod_id_lote'];
										oent_detalle_id = lote['id_detalle_oent'];
										id_almacen = lote['inv_alm_id_lote'];
										lote_int = lote['lote_int'];
										lote_prov = lote['lote_prov_lote'];
										cant_lote = lote['cantidad_lote'];
										tipo_registro='LOT';
										valor_pedimento=lote['ped_lote'];
										fecha_caducidad=lote['cad_lote'];
										
										if( parseInt(prodGrid['id_detalle']) == parseInt(lote['id_detalle_oent']) ){
											trCount2++;
											if( parseInt(cont) > 0 ){
												//ocultar el href anterior de Agregar Lote
												$grid_productos.find('.agrega_lote'+ (parseInt(trCount2)-1) ).hide();
											}
											
											//Aqui ya es para editar el lote
											tr_lote = $genera_tr_para_numero_de_lote(tipo_registro, id_detalle, id_producto,codigo,titulo, oent_detalle_id, id_almacen, lote_int, lote_prov, cant_lote, valor_pedimento, fecha_caducidad, trCount2);
											$grid_productos.append(tr_lote);
											
											//Esta funcion es para agregar un nuevo lote
											//En esta funcion se le aplica evento click a los href Agregar Lote y Eliminar
											agregar_lote_y_eliminar($grid_productos,tipo_registro, trCount2);
											$aplicar_evento_keypress( $grid_productos.find('.cant_rec'+ trCount2 ) );
											$aplicar_evento_blur( $grid_productos.find('.cant_rec'+ trCount2 ) );
											$aplicar_evento_focus( $grid_productos.find('.cant_rec'+ trCount2 ) );
											
											$aplicar_evento_focus_input($grid_productos.find('.lote_prov'+ trCount2 ));
											$aplicar_evento_blur_input($grid_productos.find('.lote_prov'+ trCount2 ));
											if(prodGrid['req_lote']=='false'){
												$grid_productos.find('.lote_prov'+ trCount2 ).attr({ 'readOnly':true });
											}
											
											$aplicar_evento_focus_input($grid_productos.find('.pedimento'+ trCount2 ));
											$aplicar_evento_blur_input($grid_productos.find('.pedimento'+ trCount2 ));
											
											$aplicar_evento_focus_input($grid_productos.find('.caducidad'+ trCount2 ));
											$aplicar_evento_blur_input($grid_productos.find('.caducidad'+ trCount2 ));
											
											$aplicar_evento_keypress_campo_caducidad($grid_productos.find('.caducidad'+ trCount2 ) );
											$add_calendar($grid_productos.find('.caducidad'+ trCount2 ), mostrarFecha(), ">=");
											
											cont++;
										}
										
									});
								}
							//}
							
							
							$aplicar_evento_keypress( $grid_productos.find('.cant_rec'+ trCount ) );
							$aplicar_evento_focus( $grid_productos.find('.cant_rec'+ trCount ) );
							
							if( parseInt(entry['Datos']['0']['estatus']) > 0 ){
								if(parseFloat(prodGrid['cantidad']) != parseFloat(prodGrid['cant_rec'])){
									$grid_productos.find('.cant_rec'+ trCount ).css({'background' : '#d41000'});
								}
							}
							
							
							//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
							$grid_productos.find('.cant_rec'+ trCount ).blur(function(e){
								var tr_actual=$(this).parent().parent();
								if(parseFloat($(this).val())<=0 || $(this).val()==""){
									$(this).val(0);
								}
								
								if ( parseFloat(tr_actual.find('input[name=cantidad]').val()) != parseFloat($(this).val()) ){
									$(this).css({'background' : '#d41000'});
									jAlert("La cantidad de la Factura debe ser igual que la cantidad Recibida.", 'Atencion!');
								}else{
									//aqui si es correcto
									$(this).css({'background' : '#ffffff'});
								}
								$(this).val(parseFloat($(this).val()).toFixed(2));
							});
						});
						
						//$calcula_totales();//llamada a la funcion que calcula totales
					}
					
					
					if(entry['Datos']['0']['cancelado'] == 'CANCELADO'){
						$select_tipo_movimiento.attr('disabled','-1');
						$cancelar.attr('disabled','-1');
						$descargar_pdf.attr('disabled','-1');
						$folio_entrada.attr('disabled','-1');
						$select_denominacion.attr('disabled','-1');
						$campo_razon_proveedor.attr('disabled','-1');
						$select_tipo_doc.attr('disabled','-1');
						$select_almacen_destino.attr('disabled','-1');
						
						$campo_flete.attr('disabled','-1');
						$campo_factura.attr('disabled','-1');
						$campo_ordencompra.attr('disabled','-1');
						$campo_numeroguia.attr('disabled','-1');
						$campo_expedicion.attr('disabled','-1');
						$campo_tc.attr('disabled','-1');
						$campo_observaciones.attr('disabled','-1');
						$campo_flete.attr('disabled','-1');
						$campo_subtotal.attr('disabled','-1');
						$campo_ieps.attr('disabled','-1');
						$retencion.attr('disabled','-1');
						$campo_impuesto.attr('disabled','-1');
						$campo_total.attr('disabled','-1');
						
						$grid_productos.find('a').hide();
						$grid_productos.find('input').attr('disabled','-1');
						$submit_actualizar.hide();
					}
					
				},"json");//termina llamada json
				
				
				
				
				$campo_tc.keypress(function(e){
					// Permitir solo numeros, borrar, suprimir, TAB, punto
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//buscar producto
				$buscar_producto.click(function(event){
					event.preventDefault();
					$busca_productos($campo_sku.val());
				});
				
				
				//agregar producto al grid
				$agregar_producto.click(function(event){
					event.preventDefault();
					//$agrega_producto_al_grid();
					$buscador_presentaciones_producto($campo_rfc_proveedor.val(),$campo_sku.val());
				});
				
				//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
				$campo_sku.keypress(function(e){
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
				});
				
				//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$campo_flete.focus(function(e){
					if(parseFloat($campo_flete.val())<1){
						$campo_flete.val('');
					}
				});
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$campo_flete.blur(function(e){
					if(parseFloat($campo_flete.val())==0||$campo_flete.val()==""){
						$campo_flete.val(0);
					}
				});	
				
				$campo_flete.keypress(function(e){
					//alert(e.which);
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				
				//descargar pdf de Orden de Entrada
				$descargar_pdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_invOrdenEntrada/'+$identificador.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
				
				
				$cancelar.hide();
				/*
				$cancelar.click(function(e){
					$cancelar.attr('disabled','-1'); //deshabilitar
					$submit_actualizar.attr('disabled','-1'); //deshabilitar
					$accion.attr({'value' : "cancelar"});
					
					jConfirm('Desea Cancelar el la Orden de Entrada?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.parents("FORM").submit();
						}else{
							$accion.attr({'value' : "edit"});
							$cancelar.removeAttr('disabled');
							$submit_actualizar.removeAttr('disabled');
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				*/
				
				
				
				
				$submit_actualizar.bind('click',function(){
					$(this).attr('disabled','-1'); //deshabilitar
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
                                        
					$grid_productos.find('tr').each(function (index){
						if($(this).find('#cad').val() == '' ){
							$(this).find('#cad').val(' ');
						}
					});
                                        
					return true;
				});
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-invordenentrada-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-invordenentrada-overlay').fadeOut(remove);
				});
			}
		}
	}
        
        
        
        
	$get_datos_grid = function(){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllOrdenesEntrada.json';
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
		$arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllOrdenesEntrada.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
		$.post(input_json,$arreglo,function(data){
			//pinta_grid
			//$.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvordenentrada00_for_datagrid00);
			
			//aqui se utiliza el mismo datagrid que prefacturas. Solo muesta icono de detalles, el de eliminar No
			$.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvordenentrada00_for_datagrid00);
			
			//resetea elastic, despues de pintar el grid y el slider
			Elastic.reset(document.getElementById('lienzo_recalculable'));
		},"json");
	}
	
    $get_datos_grid();
});



