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
	var controller = $contextpath.val()+"/controllers/proveedorespagos";
    
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $new_pago = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
    
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseover(function(){
		$(this).removeClass("onmouseOutNewItem").addClass("onmouseOverNewItem");
	});
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseout(function(){
		$(this).removeClass("onmouseOverNewItem").addClass("onmouseOutNewItem");
	});
    
    
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
    
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Pagos a Proveedores');
    
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'80px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_num_transaccion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_num_transaccion]');
	var $busqueda_factura = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_factura]');
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
		valor_retorno += "num_transaccion" + signo_separador + $busqueda_num_transaccion.val() + "|";
		valor_retorno += "factura" + signo_separador + $busqueda_factura.val() + "|";
		valor_retorno += "proveedor" + signo_separador + $busqueda_proveedor.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
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
		$busqueda_num_transaccion.val('');
		$busqueda_factura.val('');
		$busqueda_proveedor.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		
		$busqueda_num_transaccion.focus();
	});
    
	TriggerClickVisializaBuscador = 0;
	TriggerClickVisualizaGeneradorInforme=0;
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		$('#barra_genera_informe').find('.tabla_genera_reportes').css({'display':'none'});
		
		if(parseInt(TriggerClickVisualizaGeneradorInforme)==1){
			$reporte_edo_cta_cliente.trigger('click');
		}
		
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
		$busqueda_num_transaccion.focus();
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_num_transaccion, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_factura, $buscar);
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
	//----------------------------------------------------------------
	
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
		//boton registrar pago
		$('#forma-proveedorespagos-window').find('#submit_pago').mouseover(function(){
			$('#forma-proveedorespagos-window').find('#submit_pago').css({backgroundImage:"url(../../img/modalbox/pago_over.png)"});
		});
		$('#forma-proveedorespagos-window').find('#submit_pago').mouseout(function(){
			$('#forma-proveedorespagos-window').find('#submit_pago').css({backgroundImage:"url(../../img/modalbox/pago.png)"});
		});
		//boton registrar anticipo
		$('#forma-proveedorespagos-window').find('#registra_anticipo').mouseover(function(){
			$('#forma-proveedorespagos-window').find('#registra_anticipo').css({backgroundImage:"url(../../img/modalbox/anticipo_over.png)"});
		});
		$('#forma-proveedorespagos-window').find('#registra_anticipo').mouseout(function(){
			$('#forma-proveedorespagos-window').find('#registra_anticipo').css({backgroundImage:"url(../../img/modalbox/anticipo.png)"});
		});
		//boton registrar cancelacion
		$('#forma-proveedorespagos-window').find('#submit_cancel').mouseover(function(){
			$('#forma-proveedorespagos-window').find('#submit_cancel').css({backgroundImage:"url(../../img/modalbox/cancelacion_over.png)"});
		});
		$('#forma-proveedorespagos-window').find('#submit_cancel').mouseout(function(){
			$('#forma-proveedorespagos-window').find('#submit_cancel').css({backgroundImage:"url(../../img/modalbox/cancelacion.png)"});
		});
		$('#forma-proveedorespagos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-proveedorespagos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-proveedorespagos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-proveedorespagos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		$('#forma-proveedorespagos-window').find('#close').mouseover(function(){
			$('#forma-proveedorespagos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-proveedorespagos-window').find('#close').mouseout(function(){
			$('#forma-proveedorespagos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		$('#forma-proveedorespagos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-proveedorespagos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-proveedorespagos-window').find(".contenidoPes:first").show(); //Show first tab content

		//On Click Event
		$('#forma-proveedorespagos-window').find("ul.pestanas li").click(function() {
			$('#forma-proveedorespagos-window').find(".contenidoPes").hide();
			$('#forma-proveedorespagos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-proveedorespagos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	
	
	$permitir_solo_numeros = function($campo){
		//validar campo solo acepte numeros y punto
		$campo.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
        
	$add_ceros = function($campo){
		$campo.val('0.00');
		$campo.val(parseFloat($campo.val()).toFixed(2));
	}
	
	$accion_focus = function($campo){
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo.focus(function(e){
			$valor_tmp = $(this).val().split(",").join("");
			
			if( ($valor_tmp != '') && ($valor_tmp != ' ') && ($valor_tmp != null) ){
				if(parseFloat($valor_tmp)<1){
					$campo.val('');
				}else{
					$campo.val($(this).agregar_comas(parseFloat($valor_tmp).toFixed(2)));
				}
			}
		});
	}
	
    
	$accion_blur = function($campo){
		//recalcula importe al perder enfoque el campo costo
		$campo.blur(function(){
			$valor_tmp = $(this).val().split(",").join("");
			
			if ($valor_tmp == ''  || $valor_tmp == null){
					$(this).val('0.00');
			}
			
			if( ($valor_tmp != '') && ($valor_tmp != ' ') ){
				$campo.val($(this).agregar_comas(parseFloat($valor_tmp).toFixed(2)));
			}else{
				$(this).val('0.00');
			}
			
		});
	}
	
     
	//carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero, indice1, indice2){
		var option_hmtl='';
		
		if(texto_elemento_cero ==''){
			option_hmtl='';
		}else{
			option_hmtl='<option value="0">'+texto_elemento_cero+'</option>';			
		}
		
		$campo_select.children().remove();
		$.each(arreglo_elementos,function(entryIndex,indice){
			if( parseInt(indice[indice1]) == parseInt(elemento_seleccionado) ){
				option_hmtl += '<option value="' + indice[indice1] + '" selected="yes">' + indice[indice2] + '</option>';
			}else{
				option_hmtl += '<option value="' + indice[indice1] + '"  >' + indice[indice2] + '</option>';
			}
		});
		$campo_select.append(option_hmtl);
	}
     
	
	
	
	
	
	
	
	//buscador de proveedores
	$busca_proveedores = function($select_tipo_movimiento,$select_moneda,array_monedas, $no_proveedor, $proveedor){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -150  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_no_proveedor = $('#forma-buscaproveedor-window').find('input[name=campo_no_proveedor]');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
		var $campo_nombre = $('#forma-buscaproveedor-window').find('input[name=campo_nombre]');
		
		var $buscar_plugin_proveedor = $('#forma-buscaproveedor-window').find('#busca_proveedor_modalbox');
		var $cancelar_plugin_busca_proveedor = $('#forma-buscaproveedor-window').find('#cencela');
			
		$('#forma-provfacturas-window').find('input[name=tipo_proveedor]').val('');
			
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
	
		$campo_no_proveedor.val($no_proveedor.val());
		$campo_nombre.val($proveedor.val());
		
		$campo_nombre.focus();
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuacadorProveedores.json';
			$arreglo = {    'rfc':$campo_rfc.val(),
							'no_prov':$campo_no_proveedor.val(),
							'nombre':$campo_nombre.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							trr += '<input type="hidden" id="no_prov" value="'+proveedor['no_proveedor']+'">';
							trr += '<input type="hidden" id="dias_cred_id" value="'+proveedor['dias_credito_id']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+proveedor['moneda_id']+'">';
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
					
					//habilitar select de monedas y tipo de movimiento
					if($select_tipo_movimiento.is(':disabled')) {
						$select_tipo_movimiento.removeAttr('disabled');
						$select_moneda.removeAttr('disabled');
					}
					/*
					if($('#forma-proveedorespagos-window').find('input[name=proveedor]').is(':disabled')) {
						$('#forma-proveedorespagos-window').find('input[name=proveedor]').removeAttr('disabled');
						$select_tipo_movimiento.removeAttr('disabled');
						$select_moneda.removeAttr('disabled');
					}
					*/
					//asignar a los campos correspondientes el id, rfc, nombre y id_moneda  del proveedor
					$('#forma-proveedorespagos-window').find('input[name=no_proveedor]').val($(this).find('#no_prov').val());
					$('#forma-proveedorespagos-window').find('input[name=id_proveedor]').val($(this).find('#id_prov').val());
					$('#forma-proveedorespagos-window').find('input[name=proveedor]').val($(this).find('#razon_social').html());
					
					var elemento_seleccionado = $(this).find('#id_moneda').val();
					var texto_elemento_cero=0;
					//recargar selec de monedas, con la moneda del proveedor por default
					$carga_campos_select($select_moneda, array_monedas, elemento_seleccionado, texto_elemento_cero, "id", "descripcion");
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
					
					$('#forma-proveedorespagos-window').find('input[name=proveedor]').focus();
				});
			});
		});
		
		if ($campo_no_proveedor.val()!='' || $campo_nombre.val()!=''){
			$buscar_plugin_proveedor.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_no_proveedor, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_rfc, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_nombre, $buscar_plugin_proveedor);
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
			
			$('#forma-proveedorespagos-window').find('input[name=proveedor]').focus();
		});
	}//termina buscador de proveedores
	
	
	
	
	//inicializar campos
	$inicializar_campos = function($id_pago,$total_tr,$id_proveedor,$no_transaccion,$proveedor,$busca_proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_pago,$select_moneda,$fecha_pago,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia, $select_banco_tarjeta,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$select_chequera_tarjeta,$monto_pago,$pagosxguardar,$tabla_facturas_body,$tr_cheque,$tr_transferencia,$tr_tarjeta,tipo_cambio_actual,array_tipo_mov,array_monedas,array_formas_pago,array_bancos, array_conceptos){
		$id_pago.val('0');
		$total_tr.val('0');
		$id_proveedor.val('0');
		$no_transaccion.val('');
		$proveedor.val('');
		$observaciones_pago.text('');
		$select_concepto.text('');
		$fecha_pago.val(mostrarFecha());
		$tipo_cambio.val(tipo_cambio_actual);
		$num_cheque.val('');
		$referencia.val('');
		$num_tarjeta.val('');
		$monto_pago.val('0');
		$pagosxguardar.val('0');
		$tabla_facturas_body.children().remove();
		
		//deshabilitar select tipo movimiento, se habilita hasta que se seleccione un proveedor
		//$select_tipo_movimiento.attr('disabled','-1');
		$tr_cheque.hide();
		$tr_transferencia.hide();
		$tr_tarjeta.hide();
		
		//cargar select de tipos de movimiento
		elemento_seleccionado = 0;
		texto_elemento_cero='Seleccionar Proveedor';
		$carga_campos_select($select_tipo_movimiento, array_tipo_mov, elemento_seleccionado, texto_elemento_cero, "id", "titulo");
		
		//cargar select de conceptos bancarios
		elemento_seleccionado = 0;
		texto_elemento_cero='Seleccionar Concepto';
		$carga_campos_select($select_concepto, array_conceptos, elemento_seleccionado, texto_elemento_cero, "id", "titulo");
		
		//cargar select de monedas
		elemento_seleccionado = 0;
		texto_elemento_cero='';
		$carga_campos_select($select_moneda, array_monedas, elemento_seleccionado, texto_elemento_cero, "id", "descripcion");
		
		
		//carga select con todas las formas de pago
		elemento_seleccionado = 0;
		texto_elemento_cero='';
		$carga_campos_select($select_forma_pago, array_formas_pago, elemento_seleccionado, texto_elemento_cero, "id", "titulo");
		
		//carga select_banco_cheque con todos los bancos
		elemento_seleccionado = 0;
		texto_elemento_cero='Seleccionar Banco';
		$carga_campos_select($select_banco_cheque, array_bancos, elemento_seleccionado, texto_elemento_cero, "id", "titulo");			
		
		//carga select_banco_transferencia con todos los bancos
		elemento_seleccionado = 0;
		texto_elemento_cero='Seleccionar Banco';
		$carga_campos_select($select_banco_transferencia, array_bancos, elemento_seleccionado, texto_elemento_cero, "id", "titulo");	
		
		//carga select_banco_tarjeta con todos los bancos
		elemento_seleccionado = 0;
		texto_elemento_cero='Seleccionar Banco';
		$carga_campos_select($select_banco_tarjeta, array_bancos, elemento_seleccionado, texto_elemento_cero, "id", "titulo");
		
		
		$select_chequera_cheque.children().remove();
		var option_hmtl = '<option value="0">Seleccionar Chequera</option>';
		$select_chequera_cheque.append(option_hmtl);
		
		$select_chequera_transferencia.children().remove();
		$select_chequera_transferencia.append(option_hmtl);
		
		$select_chequera_tarjeta.children().remove();
		$select_chequera_tarjeta.append(option_hmtl);
		
	}
	
	
	
	//habilitar y deshabilitar campos
	$deshabilitar_campos = function(accion,$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_pago,$select_moneda,$fecha_pago,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia, $select_banco_tarjeta,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$select_chequera_tarjeta,$monto_pago,$tabla_facturas_body){
		if(accion == 'desahabilitar'){
			if(!$fecha_pago.is(':disabled')) {
				$no_transaccion.attr('disabled','-1');
				//$proveedor.attr('disabled','-1');
				$select_tipo_movimiento.attr('disabled','-1');
				$observaciones_pago.attr('disabled','-1');
				$select_concepto.attr('disabled','-1');
				$select_moneda.attr('disabled','-1');
				$fecha_pago.attr('disabled','-1');
				$select_forma_pago.attr('disabled','-1');
				$tipo_cambio.attr('disabled','-1');
				$select_banco_cheque.attr('disabled','-1');
				$select_banco_transferencia.attr('disabled','-1');
				$select_banco_tarjeta.attr('disabled','-1');
				$num_cheque.attr('disabled','-1');
				$referencia.attr('disabled','-1');
				$num_tarjeta.attr('disabled','-1');
				$select_chequera_cheque.attr('disabled','-1');
				$select_chequera_transferencia.attr('disabled','-1');
				$select_chequera_tarjeta.attr('disabled','-1');
				$monto_pago.attr('disabled','-1');
				$tabla_facturas_body.attr('disabled','-1');
			}
		}
		
		if(accion == 'habilitar'){
			if($fecha_pago.is(':disabled')) {
				$no_transaccion.removeAttr('disabled');
				//$proveedor.removeAttr('disabled');
				$select_tipo_movimiento.removeAttr('disabled');
				$observaciones_pago.removeAttr('disabled');
				$select_concepto.removeAttr('disabled');
				$select_moneda.removeAttr('disabled');
				$fecha_pago.removeAttr('disabled');
				$select_forma_pago.removeAttr('disabled');
				$tipo_cambio.removeAttr('disabled');
				$select_banco_cheque.removeAttr('disabled');
				$select_banco_transferencia.removeAttr('disabled');
				$select_banco_tarjeta.removeAttr('disabled');
				$num_cheque.removeAttr('disabled');
				$referencia.removeAttr('disabled');
				$num_tarjeta.removeAttr('disabled');
				$select_chequera_cheque.removeAttr('disabled');
				$select_chequera_transferencia.removeAttr('disabled');
				$select_chequera_tarjeta.removeAttr('disabled');
				$monto_pago.removeAttr('disabled');
				$tabla_facturas_body.removeAttr('disabled');
			}
		}
	}//termina  habilitar y deshabilitar campos
	
	
	//funciones para calculos en pagos--------------------------------------------------------------------------------------
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	
	var actualizar_tipo_cambio_grid = function($tabla_facturas,tipocambio_new){
		$tabla_facturas.find('input[name=tipocamb]').each(function(){
			$(this).val(tipocambio_new);
		});
	}
	
	
	var verificar_seleccionados= function($tabla_facturas){
		var seleccionados=false;
		$tabla_facturas.find('input[name=micheck]').each(function(){
			if(this.checked){
				seleccionados=true;
			}
		});
		return seleccionados;
	}
	
	
	var verificar_pago_permitido=function(moneda_pago,denominacion_factura){
		if(parseInt(moneda_pago)==1 && denominacion_factura=='M.N.'){
			return true;
		}
		
		if(parseInt(moneda_pago)==1 && denominacion_factura=='USD'){
			return true;
		}
		
		if(parseInt(moneda_pago)==1 && denominacion_factura=='EUR'){
			return true;
		}
		
		if(parseInt(moneda_pago)==2 && denominacion_factura=='M.N.'){
			return false;
		}
		
		if(parseInt(moneda_pago)==2 && denominacion_factura=='EUR'){
			return false;
		}
		
		if(parseInt(moneda_pago)==2 && denominacion_factura=='USD'){
			return true;
		}
		
		if(parseInt(moneda_pago)==3 && denominacion_factura=='M.N.'){
			return false;
		}
		
		if(parseInt(moneda_pago)==3 && denominacion_factura=='USD'){
			return false;
		}
		
		if(parseInt(moneda_pago)==3 && denominacion_factura=='EUR'){
			return true;
		}
		
	}
	
	
	
	
	//calcula el monto a saldar dependiendo de la moneda del pago y  la moneda de la factura
	var calcular_monto_a_saldar = function(saldar, moneda_pago, denominacion_factura,tipo_cambio){
		var monto_calculado=0;
		saldar = parseFloat(quitar_comas(saldar));
		
		if(parseInt(moneda_pago)==1){
			if( denominacion_factura == 'M.N.'){
				monto_calculado = saldar;
			}else{
				//aqui entra cuando la moneda de la factura es en USD y EUR
				monto_calculado = parseFloat(saldar * tipo_cambio);
			}
		}
		
		if(parseInt(moneda_pago)==2){
			if( denominacion_factura == 'USD'){
				monto_calculado = saldar;
			}
			
			if( denominacion_factura == 'M.N.'){
				monto_calculado = parseFloat(saldar / tipo_cambio);
			}
		}
		
		if(parseInt(moneda_pago)==3){
			if( denominacion_factura == 'EUR'){
				monto_calculado = saldar;
			}
			
			if( denominacion_factura == 'M.N.'){
				monto_calculado = parseFloat(saldar / tipo_cambio);
			}
			
		}
		
		return monto_calculado;
	}
	
	
	
	
	
	var iterar_facturas_input = function($tabla_facturas_body, $monto_pago, $tipo_cambio, $select_moneda, $select_tipo_movimiento){
		$tabla_facturas_body.find('input[name=saldar]').click(function(event){
			if(event.currentTarget === this){
				var moneda_pago = $select_moneda.val();
				var saldar_inicio = parseFloat(quitar_comas($(this).val()));
				var saldar_inicio_calculado = $(this).parent().find('input[name=saldar_calculado]').val();
				var saldo_tope = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(4)').html()));
				var facturado_en = $(this).parent().parent().find('td:eq(6)').html();
				var tipo_cambio_grid = $(this).parent().parent().find('input[name=tipocamb]').val();
				//alert("saldar_inicio:"+saldar_inicio+"\n"+"saldar_inicio_calculado:"+saldar_inicio_calculado+"\n"+"saldo_tope:"+saldo_tope+"\n"+"facturado_en:"+facturado_en+"\n"+"tipo_cambio_grid:"+tipo_cambio_grid);
				$(this).val(saldar_inicio);
				
				$(this).blur(function(event2){
					if(event2.currentTarget === this){
						var patron = /^\d+(\.?\d+)?$/;
						if(patron.test($(this).val())){
							saldar_nuevo = $(this).val();
							
							if(parseFloat(saldar_nuevo) > parseFloat(saldo_tope) ){
								$(this).val( $(this).agregar_comas(parseFloat(saldar_inicio).toFixed(2)) );
								$(this).parent().find('input[name=saldar_calculado]').val( saldar_inicio_calculado );
								//aqui se debe recalcular la suma del monto
								calcular_suma_pagos($tabla_facturas_body, $monto_pago);
								event2.stopImmediatePropagation();
							}else{
								$(this).val( $(this).agregar_comas(parseFloat(saldar_nuevo).toFixed(2)) );
								var monto_calculado=calcular_monto_a_saldar(saldar_nuevo,moneda_pago,facturado_en,tipo_cambio_grid);
								$(this).parent().find('input[name=saldar_calculado]').val(monto_calculado);
								//aqui se debe recalcular la suma del monto
								calcular_suma_pagos($tabla_facturas_body, $monto_pago);
								event2.stopImmediatePropagation();
							}
						}else{
							$(this).val( $(this).agregar_comas(parseFloat(saldar_inicio).toFixed(2)) );
							$(this).parent().find('input[name=saldar_calculado]').val(saldar_inicio_calculado);
							//aqui se debe recalcular la suma del monto
							calcular_suma_pagos($tabla_facturas_body, $monto_pago);
							event2.stopImmediatePropagation();
						}
					}
					
				});
				
			}
			
		});//termina click saldar
	}
	
	
	
	
	var iterar_facturas_check = function($tabla_facturas_body, $monto_pago, $tipo_cambio, $select_moneda, $select_tipo_movimiento){
		$tabla_facturas_body.find('input[name=micheck]').click(function(){
			var moneda_pago = $select_moneda.val();
			var facturado_en = $(this).parent().parent().find('td:eq(6)').html();
			var tipo_cambio_grid = $(this).parent().parent().find('input[name=tipocamb]').val();
			
			if(this.checked){
				$saldo = $(this).parent().parent().find('td:eq(4)').html();
				if($tipo_cambio.val() != ''){
					if(verificar_pago_permitido(moneda_pago,facturado_en)){
						var monto_calculado=calcular_monto_a_saldar($saldo,moneda_pago,facturado_en,tipo_cambio_grid);
						
						$(this).parent().parent().find('input[name=saldar]').val($saldo);
						$(this).parent().parent().find('input[name=saldar_calculado]').val(monto_calculado);
						$(this).parent().parent().find('input[name=saldar]').attr({'disabled':false});
						//aqui se debe recalcular la suma del monto
						calcular_suma_pagos($tabla_facturas_body, $monto_pago);
					}else{
						this.checked = false;
						var nombre_moneda = $select_moneda.find('option:selected').html();
						jAlert('No est&aacute; permitido el pago de una factura en '+facturado_en+' con '+nombre_moneda+'.\n','! Atencion');
						
						//alert($select_moneda.find('option:selected').html());
					}
				}else{
					this.checked = false;
					jAlert('El tipo de cambio no es valido','! Atencion');
				}
			}else{
				$(this).parent().parent().find('input[name=saldar]').val('0.00');
				$(this).parent().parent().find('input[name=saldar_calculado]').val('0.00');
				$(this).parent().parent().find('input[name=saldar]').attr({'disabled':true});
				//aqui se debe recalcular la suma del monto
				calcular_suma_pagos($tabla_facturas_body, $monto_pago);
			}
			
			if(verificar_seleccionados($tabla_facturas_body)){
				$select_tipo_movimiento.attr({'disabled':true});
				$select_moneda.attr({'disabled':true});
				$tipo_cambio.attr("readonly", true);
				$tipo_cambio.css({'background' : '#F0F0F0'});
				//$campo_pagosxguardar.attr({ 'value' : 1});//existen pagos por guardar
			}else{
				//$campo_pagosxguardar.attr({ 'value' : 0});//no hay pagos por guardar
				$select_tipo_movimiento.attr({'disabled':false});
				$select_moneda.attr({'disabled':false});
				$tipo_cambio.attr("readonly", false);
				$tipo_cambio.css({'background' : '#ffffff'});
			}
		});
	}
	
	
	
	
	
	//suma las cantidades a pagar de cada factura para obtener el monto total en la moneda indicada
	var calcular_suma_pagos= function($tabla_facturas, $monto_pago){
		var suma_pagos=0;
		$tabla_facturas.find('input[name=micheck]').each(function(){
			if(this.checked){
				var cantidad_saldar = $(this).parent().parent().find('td:eq(5)').find('input[name=saldar_calculado]').val();
				suma_pagos = parseFloat(suma_pagos) + parseFloat(cantidad_saldar);
			}
		});
		
		if(parseFloat(suma_pagos)<=0 ){
			$('#forma-proveedorespagos-window').find('#submit_pago').hide();
		}else{
			$('#forma-proveedorespagos-window').find('#submit_pago').show();
		}
		
		$monto_pago.val( $(this).agregar_comas(parseFloat(suma_pagos).toFixed(2)) );
	}
	
	//termina funciones para los calculos en pagos------------------------------------------------------
	
	
	
    
	
	//nuevo pago
	$new_pago.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_proveedorespagos();
		
		var form_to_show = 'formaPagos00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$forma_selected.prependTo('#forma-proveedorespagos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$('#forma-proveedorespagos-window').css({"margin-left": -350,"margin-top": -200});
        
        
		$tabs_li_funxionalidad();
		
		
		var $id_pago = $('#forma-proveedorespagos-window').find('input[name=id_pago]');
		var $total_tr = $('#forma-proveedorespagos-window').find('input[name=total_tr]');
		var $id_proveedor = $('#forma-proveedorespagos-window').find('input[name=id_proveedor]');
		var $no_transaccion = $('#forma-proveedorespagos-window').find('input[name=no_transaccion]');
		var $proveedor = $('#forma-proveedorespagos-window').find('input[name=proveedor]');
		var $no_proveedor = $('#forma-proveedorespagos-window').find('input[name=no_proveedor]');
		
		var $busca_proveedor = $('#forma-proveedorespagos-window').find('a[href*=busca_proveedor]');
		
		var $select_tipo_movimiento = $('#forma-proveedorespagos-window').find('select[name=select_tipo_movimiento]');
		var $observaciones_pago = $('#forma-proveedorespagos-window').find('textarea[name=observaciones_pago]');
		var $select_concepto = $('#forma-proveedorespagos-window').find('select[name=select_concepto]');
		var $select_moneda = $('#forma-proveedorespagos-window').find('select[name=select_moneda]');
		var $fecha_pago = $('#forma-proveedorespagos-window').find('input[name=fecha_pago]');
		var $select_forma_pago = $('#forma-proveedorespagos-window').find('select[name=select_forma_pago]');
		var $tipo_cambio = $('#forma-proveedorespagos-window').find('input[name=tipo_cambio]');
		var $select_banco_cheque = $('#forma-proveedorespagos-window').find('select[name=select_banco_cheque]');
		var $select_banco_transferencia = $('#forma-proveedorespagos-window').find('select[name=select_banco_transferencia]');
		var $select_banco_tarjeta = $('#forma-proveedorespagos-window').find('select[name=select_banco_tarjeta]');
		var $select_chequera_cheque = $('#forma-proveedorespagos-window').find('select[name=select_chequera_cheque]');
		var $select_chequera_transferencia = $('#forma-proveedorespagos-window').find('select[name=select_chequera_transferencia]');
		var $select_chequera_tarjeta = $('#forma-proveedorespagos-window').find('select[name=select_chequera_tarjeta]');
		
		var $num_cheque = $('#forma-proveedorespagos-window').find('input[name=num_cheque]');
		var $referencia = $('#forma-proveedorespagos-window').find('input[name=referencia]');
		var $num_tarjeta = $('#forma-proveedorespagos-window').find('input[name=num_tarjeta]');
		
		var $monto_pago = $('#forma-proveedorespagos-window').find('input[name=monto_pago]');
		var $pagosxguardar = $('#forma-proveedorespagos-window').find('input[name=pagosxguardar]');
		var $cancelar_pago = $('#forma-proveedorespagos-window').find('#cancelar_pago');
		var $pdf_pago = $('#forma-proveedorespagos-window').find('#pdf_pago');
		
		var $tabla_facturas_body = $('#forma-proveedorespagos-window').find('.tabla_facturas').find('.contenido_facturas');
		
		var $tr_cheque = $('#forma-proveedorespagos-window').find('.cheque');
		var $tr_transferencia = $('#forma-proveedorespagos-window').find('.transferencia');
		var $tr_tarjeta = $('#forma-proveedorespagos-window').find('.tarjeta');
		
		
		var $cerrar_plugin = $('#forma-proveedorespagos-window').find('#close');
		var $cancelar_plugin = $('#forma-proveedorespagos-window').find('#boton_cancelar');
		var $submit_registrar_pago = $('#forma-proveedorespagos-window').find('#submit_pago');
		var $registra_anticipo = $('#forma-proveedorespagos-window').find('#registra_anticipo');
        var $submit_registrar_cancelacion = $('#forma-proveedorespagos-window').find('#submit_cancel');
		
		//$add_ceros($monto_pago);
		//$permitir_solo_numeros($monto_pago);
		//$accion_focus($monto_pago);
		//$accion_blur($monto_pago);
        
        $permitir_solo_numeros($tipo_cambio);
        $fecha_pago.attr("readonly", true);
        //$cancelar_pago.hide();
        //$pdf_pago.hide();
        $cancelar_pago.attr('disabled','-1');
        $pdf_pago.attr('disabled','-1');
        $no_transaccion.css({'background' : '#F0F0F0'});
        
        $submit_registrar_pago.hide();
        $submit_registrar_cancelacion.hide();
        $registra_anticipo.hide();
        $no_proveedor.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La cotizacion se guardó con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-proveedorespagos-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-proveedorespagos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-proveedorespagos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var elemento_seleccionado = "";
		var texto_elemento_cero="";
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPago.json';
		$arreglo = {'id':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		$.post(input_json,$arreglo,function(entry){
			//inicializar campos
			$inicializar_campos($id_pago,$total_tr,$id_proveedor,$no_transaccion,$proveedor,$busca_proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_pago,$select_moneda,$fecha_pago,$select_forma_pago,$tipo_cambio, $select_banco_cheque, $select_banco_transferencia, $select_banco_tarjeta,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$select_chequera_tarjeta,$monto_pago,$pagosxguardar,$tabla_facturas_body,$tr_cheque,$tr_transferencia,$tr_tarjeta,entry['Tipocambio'][0]['valor_tipo_cambio'],entry['Tiposmov'],entry['Monedas'],entry['Formaspago'],entry['Bancos'],entry['Conceptos']);
			
			//deshabilitar todos los campos al cargar plugin, habilitarlos hasta que se seleecione un proveedor
			$deshabilitar_campos("desahabilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_pago,$select_moneda,$fecha_pago,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia, $select_banco_tarjeta,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$select_chequera_tarjeta,$monto_pago,$tabla_facturas_body);
			
			//buscador de proveedores
			$busca_proveedor.click(function(event){
				event.preventDefault();
				$busca_proveedores($select_tipo_movimiento,$select_moneda,entry['Monedas'], $no_proveedor, $proveedor);
			});
			
			
			//ejecuta Busqueda de Datos del Proveedor al pulsar enter en el campo No. Proveedor
			$no_proveedor.keypress(function(e){
				if(e.which == 13){
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoProv.json';
					$arreglo2 = {'no_prov':$no_proveedor.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					
					$.post(input_json2,$arreglo2,function(entry2){
						$id_proveedor.val('');
						$no_proveedor.val('');
						$proveedor.val('');
						
						if(parseInt(entry2['Proveedor'].length) > 0 ){
							//habilitar select de monedas y tipo de movimiento
							if($select_tipo_movimiento.is(':disabled')) {
								$select_tipo_movimiento.removeAttr('disabled');
								$select_moneda.removeAttr('disabled');
							}
							
							//asignar a los campos correspondientes el id, rfc, nombre y id_moneda  del proveedor
							$no_proveedor.val(entry2['Proveedor'][0]['no_proveedor']);
							$id_proveedor.val(entry2['Proveedor'][0]['id']);
							$proveedor.val(entry2['Proveedor'][0]['razon_social']);
							
							var elemento_seleccionado = entry2['Proveedor'][0]['moneda_id'];
							var texto_elemento_cero=0;
							//recargar selec de monedas, con la moneda del proveedor por default
							$carga_campos_select($select_moneda, entry['Monedas'], elemento_seleccionado, texto_elemento_cero, "id", "descripcion");
					
						}else{
							jAlert('N&uacute;mero de Proveedor desconocido.', 'Atencion!', function(r) { 
								$no_proveedor.focus(); 
							});
						}
					},"json");//termina llamada json
					
					return false;
				}
			});
			
						
			
			
			$select_tipo_movimiento.change(function(){
				var tipo_mov = $(this).val();
				//tipo seleccionar proveedor
				if(parseInt(tipo_mov)==0){
					$('#forma-proveedorespagos-window').find('.proveedorespagos_div_one').css({'height':'445px'});
					
					//habilitar todos los campos 
					$deshabilitar_campos("desahabilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_pago,$select_moneda,$fecha_pago,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia, $select_banco_tarjeta,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$select_chequera_tarjeta,$monto_pago,$tabla_facturas_body);
					$busca_proveedor.show();
					$inicializar_campos($id_pago,$total_tr,$id_proveedor,$no_transaccion,$proveedor,$busca_proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_pago,$select_moneda,$fecha_pago,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia, $select_banco_tarjeta,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$select_chequera_tarjeta,$monto_pago,$pagosxguardar,$tabla_facturas_body,$tr_cheque,$tr_transferencia,$tr_tarjeta,entry['Tipocambio'][0]['valor_tipo_cambio'],entry['Tiposmov'],entry['Monedas'],entry['Formaspago'],entry['Bancos'],entry['Conceptos']);
					
					//deshabilitar todos los campos al cargar plugin, habilitarlos hasta que se seleecione un proveedor
					$deshabilitar_campos("desahabilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_pago,$select_moneda,$fecha_pago,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia, $select_banco_tarjeta,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$select_chequera_tarjeta,$monto_pago,$tabla_facturas_body);
				}
				
				//tipo movimiento pago
				if(parseInt(tipo_mov)==1){
					//habilitar todos los campos
					$deshabilitar_campos("habilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_pago,$select_moneda,$fecha_pago,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia, $select_banco_tarjeta,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$select_chequera_tarjeta,$monto_pago,$tabla_facturas_body);
					$busca_proveedor.hide();
					
					var getfacturas_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getFacturas.json';
					$arreglo2 = {'id_proveedor': $id_proveedor.val()};
					$.post(getfacturas_json,$arreglo2,function(data){
						$tabla_facturas_body.children().remove();
						$.each(data['Facturas'],function(entryIndex, entry2){
							var contenido_c="";
							contenido_c += "<tr>";
							contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='10'><input type='checkbox' name='micheck' value='check'></td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7 ;' width='60'>" + entry2['numero_factura'] + "</td>";
							
							contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(entry2['monto_factura'])+"</td>";
							contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='85' align='right'>"+$(this).agregar_comas(entry2['monto_pagado'])+"</td>";
							//var saldo = parseFloat(entry2['monto_factura'])- parseFloat(entry2['monto_pagado']);
							var saldo = entry2['saldo_factura'];
							
							contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(parseFloat(saldo).toFixed(2))+"</td>";
							
							contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='80'>";
							contenido_c += "<input type='text' name='saldar' value='0.00' style='width: 80px; background-color:#F6F8FB; text-align:right;' disabled='true' align='right'>";
							contenido_c += "<input type='hidden' name='saldar_calculado' value='0.00' style='width: 80px; background-color:#F6F8FB; text-align:right;' disabled='true' align='right'>";
							contenido_c += "</td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='70'>" +entry2['denominacion_factura']+ "</td>";
							//contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='125'><input type='text' name='tipocamb' value='"+$tipo_cambio.val()+"' style='width: 80px; background-color:#F6F8FB;' align='right' disabled=true><span class='button_for_ie'><input align='center' class='borde_oculto' type='botton' value='<<>>' style='font-size: 11px; background-color: rgb(255, 255, 255);  padding: 1px; width: 35px;'></span></td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='125'><input type='text' name='tipocamb' value='"+$tipo_cambio.val()+"' style='width: 80px; background-color:#F6F8FB;' align='right' disabled='true'></td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='92'>" +entry2['fecha_ultimo_pago']+ "</td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='95'>" +entry2['fecha_facturacion']+ "</td>";
							contenido_c += "</tr>";
							$tabla_facturas_body.append(contenido_c);
						});
						iterar_facturas_check($tabla_facturas_body, $monto_pago,$tipo_cambio, $select_moneda, $select_tipo_movimiento);
						iterar_facturas_input($tabla_facturas_body, $monto_pago, $tipo_cambio, $select_moneda, $select_tipo_movimiento);
					});//termina getFacturas
				}//termina tipo movimiento pago
				
			});//termina change select_tipo_movimiento
			
			$select_moneda.change(function(){
				//carga select con todos los bancos
				elemento_seleccionado = 0;
				texto_elemento_cero='Seleccionar Banco';
				
				$carga_campos_select($select_banco_cheque, entry['Bancos'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");			
				$carga_campos_select($select_banco_transferencia, entry['Bancos'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
				$carga_campos_select($select_banco_tarjeta, entry['Bancos'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
				
				$select_chequera_cheque.children().remove();
				var option_hmtl = '<option value="0">Seleccionar Chequera</option>';
				$select_chequera_cheque.append(option_hmtl);
				
				$select_chequera_transferencia.children().remove();
				$select_chequera_transferencia.append(option_hmtl);
				
				$select_chequera_tarjeta.children().remove();
				$select_chequera_tarjeta.append(option_hmtl);
			});
		});//termina llamada json
		
		
		//aplicar evento para que al pulsar Enter sobre el campo proveedor se abra el buscador y ejecute la Busqueda
		$(this).aplicarEventoKeypressEjecutaTrigger($proveedor, $busca_proveedor);
		
		//fecha pago
		$fecha_pago.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_pago.DatePicker({
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
				$fecha_pago.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_pago.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_pago.val(mostrarFecha());
					}else{
						$fecha_pago.DatePickerHide();	
					}
				}
			}
		});
		
		
        
		
		$select_forma_pago.change(function(){
			var forma_pago = $(this).val();
			$num_cheque.val('');
			$referencia.val('');
			$num_tarjeta.val('');
			$tr_cheque.hide();
			$tr_transferencia.hide();
			$tr_tarjeta.hide();
			
			//efectivo
			if(parseInt(forma_pago)==1){
				$('#forma-proveedorespagos-window').find('.proveedorespagos_div_one').css({'height':'470px'});
			}
			
			//cheque
			if(parseInt(forma_pago)==2){
				$tr_cheque.show();
				$('#forma-proveedorespagos-window').find('.proveedorespagos_div_one').css({'height':'492px'});
			}
			//transferencia
			if(parseInt(forma_pago)==3){
				$tr_transferencia.show();
				$('#forma-proveedorespagos-window').find('.proveedorespagos_div_one').css({'height':'492px'});
			}
			
			//tarjeta
			if(parseInt(forma_pago)==4){
				$tr_tarjeta.show();
				$('#forma-proveedorespagos-window').find('.proveedorespagos_div_one').css({'height':'492px'});
			}
			
		});
		
		
		
		
		$accion_change_select = function(id_banco, $select_chequera, $select_moneda){
			var getcuentas_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getChequeras.json';
			$arreglo2 = {	'id_banco': id_banco,
							'id_moneda': $select_moneda.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			
			$.post(getcuentas_json,$arreglo2,function(entry2){
				//carga select con todos los numeros de cuenta de acuerdo a la moneda y banco seleccionados
				elemento_seleccionado = 0;
				texto_elemento_cero='Seleccionar Chequera';
				$carga_campos_select($select_chequera, entry2['Chequeras'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
			});
		}
		
		$select_banco_cheque.change(function(){
			id_banco = $(this).val();
			$accion_change_select(id_banco, $select_chequera_cheque, $select_moneda);
		});
		
		$select_banco_transferencia.change(function(){
			id_banco = $(this).val();
			$accion_change_select(id_banco, $select_chequera_transferencia, $select_moneda);
		});
		
		$select_banco_tarjeta.change(function(){
			id_banco = $(this).val();
			$accion_change_select(id_banco, $select_chequera_tarjeta, $select_moneda);
		});
		
		
		$tipo_cambio.blur(function(){
			var valor=$(this).val();
			$(this).focus();
			var patron = /^\d+(\.?\d+)?$/;
			if(patron.test(valor)){
				if(parseFloat(valor) >= 0){
					$(this).val(valor);
					actualizar_tipo_cambio_grid($tabla_facturas_body,valor);
				}else{
					jAlert('El tipo de cambio no es valido','! Atencion');
				}
			}else{
				jAlert('El tipo de cambio no es valido','! Atencion');
			}
		});
		
		
		$submit_registrar_pago.click(function(event){
			event.preventDefault();//evita que se ejecuta el submit
			if( parseInt($select_forma_pago.val())!=0 ){
				if(verificar_seleccionados($tabla_facturas_body)){
					var saldar=0;
					var saldar_calculado=0;
					var factura_vista="";
					var pagos_aplicados=0;
					var monto_factura=0;
					var tipo_cambio=0;
					var total_pagos=0;
					saldado=false;
					cadena="";
					var contador=0;
					var table_envio="";
					table_envio += "<table><tr><td>";
					table_envio += "<table><thead><tr><th style='background-color:#DCDCDC;border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220);' width=70>Factura</th><th style='border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); background-color:#DCDCDC;' width=15></th><th style='text-align: center ! important; border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-right: 2px solid rgb(220, 220, 220); background-color:#DCDCDC;' width=100>Cantidad a saldar</th></tr></thead></table>";
					table_envio += '<div style="overflow-x: hidden; overflow-y: auto; width: 100%; height: 150px;">'
					table_envio += "<table><tbody>";
					$tabla_facturas_body.find('input[name=micheck]').each(function(){
						if(this.checked){
							saldar = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(5)').find('input[name=saldar]').val()));
							saldar_calculado = parseFloat($(this).parent().parent().find('td:eq(5)').find('input[name=saldar_calculado]').val());
							factura_vista = $(this).parent().parent().find('td:eq(1)').html();
							pagos_aplicados = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(3)').html()));
							monto_factura = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(2)').html()));
							tipo_cambio = $(this).parent().parent().find('td:eq(7)').find('input[name=tipocamb]').val();
							total_pagos = parseFloat(parseFloat(saldar) + parseFloat(pagos_aplicados)).toFixed(2);
							if(parseFloat(total_pagos) == parseFloat(monto_factura)){
								saldado = true;
							}else{
								saldado = false;
							}
							table_envio +=  "<tr><td style='text-align: center ! important; border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220);' width=70>"+factura_vista+"</td><td width=15 style='border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220);'></td><td style='text-align:right !important; border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220);border-right:  2px solid rgb(220, 220, 220);' width=100>"+ $(this).agregar_comas(saldar) +"</td></tr>";
							cadena += factura_vista + "___" + saldado + "___" + saldar + "___" + tipo_cambio + "&&";
							contador++;
						}
					});
					table_envio +="</tbody></table></div></td></tr></table>";
					//alert(cadena);
					//alert("select_banco:"+$select_banco.val());
					var banco_id=0;
					var chequera_id=0;
					
					if(parseInt($select_forma_pago.val())==2){
						banco_id=$select_banco_cheque.val();
						chequera_id=$select_chequera_cheque.val();
					}
					
					if(parseInt($select_forma_pago.val())==3){
						banco_id=$select_banco_transferencia.val();
						chequera_id=$select_chequera_transferencia.val();
					}
					
					if(parseInt($select_forma_pago.val())==4){
						banco_id=$select_banco_tarjeta.val();
						chequera_id=$select_chequera_tarjeta.val();
					}
					
					
					var variables_to_pass = {
						'id_pago': $id_pago.val(),
						'id_proveedor': $id_proveedor.val(),
						'fecha_pago': $fecha_pago.val(),
						'tipo_cambio': $tipo_cambio.val(),
						'id_moneda': $select_moneda.val(),
						'forma_pago': $select_forma_pago.val(),
						'id_banco': banco_id,
						'id_chequera': chequera_id,
						'num_cheque': $num_cheque.val(),
						'referencia': $referencia.val(),
						'num_tarjeta': $num_tarjeta.val(),
						'monto_pago':quitar_comas($monto_pago.val()),
						'observaciones':$observaciones_pago.val(),
						'select_concepto':$select_concepto.val(),
						'cadena':cadena,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
					
					
					jConfirm('Se relaizar&aacute; el pago para el proveedor:\n'+
							$proveedor.val()+
							"\nSe saldaran "+ contador +"facturas."+
							"\nDatos de las facturas a saldar\n"+
					table_envio, 'Dialogo de confirmacion', function(r) {
						if (r){
							var input_registra_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/registra_pagos.json';
							$.post(input_registra_json,variables_to_pass,function(data){
								
								if ( data['success'] == "true" ){
									// Desaparece todas las interrogaciones si es que existen
									$('#forma-proveedorespagos-window').find('div.interrogacion').css({'display':'none'});
									
									if ( data['error_cheque'] == "false" ){
										jAlert('Pago registrado con &eacute;xito.\nN&uacute;mero de transacci&oacute;n: '+data['numero_transaccion'],'Atencion!')
										$submit_registrar_pago.hide();
										
										var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
										var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfReporteAplicacionPagoProveedor/'+data['identificador_pago']+'/'+$id_proveedor.val()+'/'+iu+'/out.json';
										window.location.href=input_json;
										
										$get_datos_grid();
										
										var remove = function() {$(this).remove();};
										$('#forma-proveedorespagos-overlay').fadeOut(remove);
									}else{
										jAlert('No se pudo registrar el pago: '+data['error_cheque'],'Atencion!');
									}
								}else{
									// Desaparece todas las interrogaciones si es que existen
									$('#forma-proveedorespagos-window').find('div.interrogacion').css({'display':'none'});
									
									var valor = data['success'].split('___');
									//muestra las interrogaciones
									for (var element in valor){
										tmp = data['success'].split('___')[element];
										longitud = tmp.split(':');
										
										if( longitud.length > 1 ){
											$('#forma-proveedorespagos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
											.parent()
											.css({'display':'block'})
											.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
										}
									}
								}
								
							});
						}
					});
				}else{
					jAlert("No hay facturas seleccionadas para Pago","Atencion!");
				}
			}
			
		});
		
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-proveedorespagos-overlay').fadeOut(remove);
		});
		
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-proveedorespagos-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	
	
	
	
	var carga_formaPagos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Agente seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Agente fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Agente no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			//event.preventDefault();
			//var id_to_show = 0;
			
			$(this).modalPanel_proveedorespagos();
			
			var form_to_show = 'formaPagos00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$forma_selected.prependTo('#forma-proveedorespagos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$('#forma-proveedorespagos-window').css({"margin-left": -350,"margin-top": -200});
			
			$tabs_li_funxionalidad();
			
			var $id_pago = $('#forma-proveedorespagos-window').find('input[name=id_pago]');
			var $total_tr = $('#forma-proveedorespagos-window').find('input[name=total_tr]');
			var $id_proveedor = $('#forma-proveedorespagos-window').find('input[name=id_proveedor]');
			var $no_transaccion = $('#forma-proveedorespagos-window').find('input[name=no_transaccion]');
			var $proveedor = $('#forma-proveedorespagos-window').find('input[name=proveedor]');
			var $no_proveedor = $('#forma-proveedorespagos-window').find('input[name=no_proveedor]');
			
			var $busca_proveedor = $('#forma-proveedorespagos-window').find('a[href*=busca_proveedor]');
			
			var $select_tipo_movimiento = $('#forma-proveedorespagos-window').find('select[name=select_tipo_movimiento]');
			var $observaciones_pago = $('#forma-proveedorespagos-window').find('textarea[name=observaciones_pago]');
			var $select_concepto = $('#forma-proveedorespagos-window').find('select[name=select_concepto]');
			var $select_moneda = $('#forma-proveedorespagos-window').find('select[name=select_moneda]');
			var $fecha_pago = $('#forma-proveedorespagos-window').find('input[name=fecha_pago]');
			var $select_forma_pago = $('#forma-proveedorespagos-window').find('select[name=select_forma_pago]');
			var $tipo_cambio = $('#forma-proveedorespagos-window').find('input[name=tipo_cambio]');
			var $select_banco_cheque = $('#forma-proveedorespagos-window').find('select[name=select_banco_cheque]');
			var $select_banco_transferencia = $('#forma-proveedorespagos-window').find('select[name=select_banco_transferencia]');
			var $select_banco_tarjeta = $('#forma-proveedorespagos-window').find('select[name=select_banco_tarjeta]');
			var $select_chequera_cheque = $('#forma-proveedorespagos-window').find('select[name=select_chequera_cheque]');
			var $select_chequera_transferencia = $('#forma-proveedorespagos-window').find('select[name=select_chequera_transferencia]');
			var $select_chequera_tarjeta = $('#forma-proveedorespagos-window').find('select[name=select_chequera_tarjeta]');
			
			var $num_cheque = $('#forma-proveedorespagos-window').find('input[name=num_cheque]');
			var $referencia = $('#forma-proveedorespagos-window').find('input[name=referencia]');
			var $num_tarjeta = $('#forma-proveedorespagos-window').find('input[name=num_tarjeta]');
			var $select_chequera = $('#forma-proveedorespagos-window').find('select[name=select_chequera]');
			var $monto_pago = $('#forma-proveedorespagos-window').find('input[name=monto_pago]');
			var $pagosxguardar = $('#forma-proveedorespagos-window').find('input[name=pagosxguardar]');
			var $cancelar_pago = $('#forma-proveedorespagos-window').find('#cancelar_pago');
			var $pdf_pago = $('#forma-proveedorespagos-window').find('#pdf_pago');
			
			var $tabla_facturas_body = $('#forma-proveedorespagos-window').find('.tabla_facturas').find('.contenido_facturas');
			
			var $tr_cheque = $('#forma-proveedorespagos-window').find('.cheque');
			var $tr_transferencia = $('#forma-proveedorespagos-window').find('.transferencia');
			var $tr_tarjeta = $('#forma-proveedorespagos-window').find('.tarjeta');
			
			
			var $cerrar_plugin = $('#forma-proveedorespagos-window').find('#close');
			var $cancelar_plugin = $('#forma-proveedorespagos-window').find('#boton_cancelar');
			var $submit_registrar_pago = $('#forma-proveedorespagos-window').find('#submit_pago');
			var $registra_anticipo = $('#forma-proveedorespagos-window').find('#registra_anticipo');
			var $submit_registrar_cancelacion = $('#forma-proveedorespagos-window').find('#submit_cancel');
			
			$tr_cheque.hide();
			$tr_transferencia.hide();
			$tr_tarjeta.hide();
			$busca_proveedor.hide();
			$fecha_pago.attr("readonly", true);
			$no_proveedor.attr("readonly", true);
			$proveedor.attr("readonly", true);
			$cancelar_pago.hide();
			$pdf_pago.hide();
			
			$submit_registrar_pago.hide();
			$submit_registrar_cancelacion.hide();
			$registra_anticipo.hide();
			$no_transaccion.css({'background' : '#F0F0F0'});
			$no_proveedor.css({'background' : '#F0F0F0'});
			$proveedor.css({'background' : '#F0F0F0'});
			
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPago.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-proveedorespagos-overlay').fadeOut(remove);
						jAlert("Los datos Agente se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-proveedorespagos-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-proveedorespagos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$id_pago.attr({ 'value' : entry['Datos']['0']['id'] });
					$id_proveedor.attr({ 'value' : entry['Datos']['0']['cxp_prov_id'] });
					$no_proveedor.attr({ 'value' : entry['Datos']['0']['no_proveedor'] });
					$no_transaccion.attr({ 'value' : entry['Datos']['0']['numero_transaccion'] });
					$proveedor.attr({ 'value' : entry['Datos']['0']['razon_social'] });
					$observaciones_pago.attr({ 'value' : entry['Datos']['0']['observaciones'] });
					//$select_concepto.attr({ 'value' : entry['Datos']['0']['select_concepto'] });
					$tipo_cambio.attr({ 'value' : entry['Datos']['0']['tipo_cambio'] });
					$fecha_pago.attr({ 'value' : entry['Datos']['0']['fecha_pago'] });
					$num_cheque.attr({ 'value' : entry['Datos']['0']['numero_cheque'] });
					$referencia.attr({ 'value' : entry['Datos']['0']['referencia'] });
					$num_tarjeta.attr({ 'value' : entry['Datos']['0']['numero_tarjeta'] });
					$monto_pago.attr({ 'value' : entry['Datos']['0']['monto_pago'] });
					
					if( entry['Datos']['0']['cancelacion'] =='false' ){
						$cancelar_pago.show();
						$pdf_pago.show();
					}
					
					//cargar select de tipos de movimiento
					elemento_seleccionado = 1;
					texto_elemento_cero='Seleccionar Proveedor';
					$carga_campos_select($select_tipo_movimiento, entry['Tiposmov'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
					
					//cargar select de conceptos bancarios
					elemento_seleccionado = entry['Datos']['0']['tes_con_id'];
					texto_elemento_cero='Seleccionar Concepto';
					$carga_campos_select($select_concepto, entry['Conceptos'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
					
					//cargar select de monedas
					elemento_seleccionado = entry['Datos']['0']['moneda_id'];
					texto_elemento_cero='';
					$carga_campos_select($select_moneda, entry['Monedas'], elemento_seleccionado, texto_elemento_cero, "id", "descripcion");
					
					//carga select con todas las formas de pago
					elemento_seleccionado = entry['Datos']['0']['tes_mov_tipo_id'];
					texto_elemento_cero='';
					$carga_campos_select($select_forma_pago, entry['Formaspago'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
					
					
					elemento_seleccionado = entry['Datos']['0']['tes_ban_id'];
					texto_elemento_cero='Seleccionar Banco';
					
					//cheque
					if(parseInt(entry['Datos']['0']['tes_mov_tipo_id'])==2){
						$tr_cheque.show();
						//carga select con todos los bancos
						$carga_campos_select($select_banco_cheque, entry['Bancos'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");			
						
						$select_chequera_cheque.children().remove();
						var option_hmtl = '';
						if(parseInt(entry['Datos']['0']['tes_che_id']) != 0){
							option_hmtl += '<option value="'+entry['Datos']['0']['tes_che_id']+'">'+entry['Datos']['0']['no_chequera']+'</option>';
						}else{
							option_hmtl += '<option value="0">Seleccionar Chequera</option>';
						}
						$select_chequera_cheque.append(option_hmtl);
						
						$('#forma-proveedorespagos-window').find('.proveedorespagos_div_one').css({'height':'463px'});
					}
					
					//transferencia
					if(parseInt(entry['Datos']['0']['tes_mov_tipo_id'])==3){
						$tr_transferencia.show();
						$carga_campos_select($select_banco_transferencia, entry['Bancos'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");			
						
						$select_chequera_transferencia.children().remove();
						var option_hmtl = '';
						if(parseInt(entry['Datos']['0']['tes_che_id']) != 0){
							option_hmtl += '<option value="'+entry['Datos']['0']['tes_che_id']+'">'+entry['Datos']['0']['no_chequera']+'</option>';
						}else{
							option_hmtl += '<option value="0">Seleccionar Chequera</option>';
						}
						$select_chequera_transferencia.append(option_hmtl);
						$('#forma-proveedorespagos-window').find('.proveedorespagos_div_one').css({'height':'463px'});
					}
					
					//tarjeta
					if(parseInt(entry['Datos']['0']['tes_mov_tipo_id'])==4){
						$tr_tarjeta.show();
						$carga_campos_select($select_banco_tarjeta, entry['Bancos'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");			
						$select_chequera_tarjeta.children().remove();
						var option_hmtl = '';
						if(parseInt(entry['Datos']['0']['tes_che_id']) != 0){
							option_hmtl += '<option value="'+entry['Datos']['0']['tes_che_id']+'">'+entry['Datos']['0']['no_chequera']+'</option>';
						}else{
							option_hmtl += '<option value="0">Seleccionar Chequera</option>';
						}
						$select_chequera_tarjeta.append(option_hmtl);
						$('#forma-proveedorespagos-window').find('.proveedorespagos_div_one').css({'height':'463px'});
					}
					
					
					$tabla_facturas_body.children().remove();
					$.each(entry['Detalles'],function(entryIndex, fac){
						var contenido_c="";
						contenido_c += "<tr>";
						//contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='10'><input type='checkbox' name='micheck' value='check'></td>";
						contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='10'></td>";
						contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7 ;' width='60'>" + fac['numero_factura'] + "</td>";
						
						contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(fac['monto_factura'])+"</td>";
						contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='85' align='right'>"+$(this).agregar_comas(fac['monto_pagado'])+"</td>";
						contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(fac['saldo_factura'])+"</td>";
						contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='80'>";
						contenido_c += "<input type='text' name='saldar' value='"+$(this).agregar_comas(fac['cantidad_pago'])+"' style='width: 80px; background-color:#F6F8FB; text-align:right;' disabled='true' align='right'>";
						contenido_c += "</td>";
						contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='70'>" +fac['denominacion_factura']+ "</td>";
						contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='125'><input type='text' name='tipocamb' value='"+$(this).agregar_comas(fac['tipo_cambio'])+"' style='width: 80px; background-color:#F6F8FB;' align='right' disabled='true'></td>";
						contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='92'>" +fac['fecha_ultimo_pago']+ "</td>";
						contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='95'>" +fac['fecha_facturacion']+ "</td>";
						contenido_c += "</tr>";
						$tabla_facturas_body.append(contenido_c);
					});
					
					$deshabilitar_campos("desahabilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_pago,$select_moneda,$fecha_pago,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia, $select_banco_tarjeta,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$select_chequera_tarjeta,$monto_pago,$tabla_facturas_body);
				},"json");//termina llamada json
				
				
				
                
				//cancelar pago
				$cancelar_pago.click(function(event){
					event.preventDefault();
					var id_to_show = 0;
					$(this).modalPanel_cancelapago();
					var form_to_show = 'formaCancelaPago';
					$('#' + form_to_show).each (function(){this.reset();});
					var $forma_selected = $('#' + form_to_show).clone();
					$forma_selected.attr({id : form_to_show + id_to_show});
					$('#forma-cancelapago-window').css({"margin-left": -100,"margin-top": -180});
					$forma_selected.prependTo('#forma-cancelapago-window');
					$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
					
					var $motivo_cancelacion = $('#forma-cancelapago-window').find('textarea[name=motivo_cancel]');
					
					var $cancelar_pago = $('#forma-cancelapago-window').find('a[href*=cancel_pago]');
					var $salir = $('#forma-cancelapago-window').find('a[href*=salir]');
					
					//cancelar pago
					$cancelar_pago.click(function(event){
						event.preventDefault();
						if($motivo_cancelacion.val()!=""){
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/cancelar_pago.json';
							$arreglo = {'id_pago':$id_pago.val(),
										'motivo':$motivo_cancelacion.val(),
										'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
										}
							
							$.post(input_json,$arreglo,function(entry){
								var remove = function() {$(this).remove();};
								$('#forma-cancelapago-overlay').fadeOut(remove);
								
								jAlert("El pago con N&uacute;mero de Transacci&oacute;n "+entry['success']+"  se ha cancelado con &eacute;xito", 'Atencion!');
								
								var remove = function() { $(this).remove(); };
								$('#forma-proveedorespagos-overlay').fadeOut(remove);
								$get_datos_grid();
								
							});//termina llamada json
						}else{
							jAlert("Es necesario ingresar el motivo de la cancelaci&oacute;n", 'Atencion!');
						}
					});
					
					$salir.click(function(event){
						event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-cancelapago-overlay').fadeOut(remove);
					});
					
				});//termina cancelar factura
                
				
				//descargar pdf de pago
				$pdf_pago.click(function(event){
					if(parseInt($id_pago.val()) !=0){
						var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
						var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfReporteAplicacionPagoProveedor/'+$id_pago.val()+'/'+$id_proveedor.val()+'/'+iu+'/out.json';
						window.location.href=input_json;
					}
				});
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-proveedorespagos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-proveedorespagos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    
    
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPagos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getPagos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaPagos00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});
