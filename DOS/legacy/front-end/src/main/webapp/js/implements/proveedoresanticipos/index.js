$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
		work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select opcion
    var array_select_opcion = {
				0:"Seleccionar Proveedor", 
				1:"Anticipo"
			};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/proveedoresanticipos";
    
    
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
	$('#barra_titulo').find('#td_titulo').append('Anticipos a Proveedores');
    
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'80px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_num_transaccion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_num_transaccion]');
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
		valor_retorno += "proveedor" + signo_separador + $busqueda_proveedor.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
		return valor_retorno;
		alert(valor_retorno);
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
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_num_transaccion, $buscar);
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
		$('#forma-proveedoresanticipos-window').find('#submit').mouseover(function(){
			$('#forma-proveedoresanticipos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-proveedoresanticipos-window').find('#submit').mouseout(function(){
			$('#forma-proveedoresanticipos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		
		$('#forma-proveedoresanticipos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-proveedoresanticipos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-proveedoresanticipos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-proveedoresanticipos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-proveedoresanticipos-window').find('#close').mouseover(function(){
			$('#forma-proveedoresanticipos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		
		$('#forma-proveedoresanticipos-window').find('#close').mouseout(function(){
			$('#forma-proveedoresanticipos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-proveedoresanticipos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-proveedoresanticipos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-proveedoresanticipos-window').find(".contenidoPes:first").show(); //Show first tab content

		//On Click Event
		$('#forma-proveedoresanticipos-window').find("ul.pestanas li").click(function() {
			$('#forma-proveedoresanticipos-window').find(".contenidoPes").hide();
			$('#forma-proveedoresanticipos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-proveedoresanticipos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
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
		$campo.val(0);
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
					$campo.val($valor_tmp);
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
     
	
	
	
	
	//carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, elemento_seleccionado){
		$campo_select.children().remove();
		var select_html = '';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
			}
		}
		$campo_select.append(select_html);
	}
	
	
	
	
	//buscador de proveedores
	$busca_proveedores = function($select_tipo_movimiento,$select_moneda,array_monedas){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -150  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
		var $campo_email = $('#forma-buscaproveedor-window').find('input[name=campo_email]');
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
	
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuacadorProveedores.json';
			$arreglo = {    'rfc':$campo_rfc.val(),
							'email':$campo_email.val(),
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
					if($('#forma-proveedoresanticipos-window').find('input[name=proveedor]').is(':disabled')) {
						$('#forma-proveedoresanticipos-window').find('input[name=proveedor]').removeAttr('disabled');
						$select_tipo_movimiento.removeAttr('disabled');
						$select_moneda.removeAttr('disabled');
					}
					
					//asignar a los campos correspondientes el id, rfc, nombre y id_moneda  del proveedor
					$('#forma-proveedoresanticipos-window').find('input[name=id_proveedor]').val($(this).find('#id_prov').val());
					$('#forma-proveedoresanticipos-window').find('input[name=proveedor]').val($(this).find('#razon_social').html());
					
					var elemento_seleccionado = $(this).find('#id_moneda').val();
					var texto_elemento_cero=0;
					//recargar selec de monedas, con la moneda del proveedor por default
					$carga_campos_select($select_moneda, array_monedas, elemento_seleccionado, texto_elemento_cero, "id", "descripcion");
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
				});
			});
		});
		
		
		//$(this).aplicarEventoKeypressEjecutaTrigger($campo_no_proveedor, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_rfc, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_nombre, $buscar_plugin_proveedor);
		
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
		});
	}//termina buscador de proveedores
	
	
	
	
	//inicializar campos
	$inicializar_campos = function($id_anticipo,$total_tr,$id_proveedor,$no_transaccion,$proveedor,$busca_proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_anticipo,$select_moneda,$fecha_anticipo,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$monto_anticipo,$select_orden_compra,$tr_cheque,$tr_transferencia,$tr_tarjeta,tipo_cambio_actual,array_monedas,array_formas_pago,array_bancos, array_conceptos){
		$id_anticipo.val('0');
		$total_tr.val('0');
		$id_proveedor.val('0');
		$no_transaccion.val('');
		$proveedor.val('');
		$observaciones_anticipo.text('');
		$select_concepto.text('');
		$fecha_anticipo.val(mostrarFecha());
		$tipo_cambio.val(tipo_cambio_actual);
		$num_cheque.val('');
		$referencia.val('');
		$num_tarjeta.val('');
		$monto_anticipo.val('0');
		
		//deshabilitar select tipo movimiento, se habilita hasta que se seleccione un proveedor
		//$select_tipo_movimiento.attr('disabled','-1');
		//$tr_cheque.hide();
		$tr_transferencia.hide();
		$tr_tarjeta.hide();
		
		//cargar select de tipos de movimiento
		elemento_seleccionado = 0;
		$carga_select_con_arreglo_fijo($select_tipo_movimiento, array_select_opcion, elemento_seleccionado);
		
		
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
		
		
		$select_chequera_cheque.children().remove();
		var option_hmtl = '<option value="0">Seleccionar Chequera</option>';
		$select_chequera_cheque.append(option_hmtl);
		
		$select_chequera_transferencia.children().remove();
		$select_chequera_transferencia.append(option_hmtl);
		
		
		$select_orden_compra.children().remove();
		var option_hmtl2 = '<option value="0">Seleccionar Orden Compra</option>';
		$select_orden_compra.append(option_hmtl2);		
	}
	
	
	
	//habilitar y deshabilitar campos
	$deshabilitar_campos = function(accion,$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_anticipo,$select_moneda,$fecha_anticipo,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$monto_anticipo,$select_orden_compra){
		if(accion == 'desahabilitar'){
			if(!$fecha_anticipo.is(':disabled')) {
				$no_transaccion.attr('disabled','-1');
				$proveedor.attr('disabled','-1');
				$select_tipo_movimiento.attr('disabled','-1');
				$observaciones_anticipo.attr('disabled','-1');
				$select_concepto.attr('disabled','-1');
				$select_moneda.attr('disabled','-1');
				$fecha_anticipo.attr('disabled','-1');
				$select_forma_pago.attr('disabled','-1');
				$tipo_cambio.attr('disabled','-1');
				$select_banco_cheque.attr('disabled','-1');
				$select_banco_transferencia.attr('disabled','-1');
				$num_cheque.attr('disabled','-1');
				$referencia.attr('disabled','-1');
				$num_tarjeta.attr('disabled','-1');
				$select_chequera_cheque.attr('disabled','-1');
				$select_chequera_transferencia.attr('disabled','-1');
				$monto_anticipo.attr('disabled','-1');
				$select_orden_compra.attr('disabled','-1');
			}
		}
		
		if(accion == 'habilitar'){
			if($fecha_anticipo.is(':disabled')) {
				$no_transaccion.removeAttr('disabled');
				$proveedor.removeAttr('disabled');
				$select_tipo_movimiento.removeAttr('disabled');
				$observaciones_anticipo.removeAttr('disabled');
				$select_concepto.removeAttr('disabled');
				$select_moneda.removeAttr('disabled');
				$fecha_anticipo.removeAttr('disabled');
				$select_forma_pago.removeAttr('disabled');
				$tipo_cambio.removeAttr('disabled');
				$select_banco_cheque.removeAttr('disabled');
				$select_banco_transferencia.removeAttr('disabled');
				$num_cheque.removeAttr('disabled');
				$referencia.removeAttr('disabled');
				$num_tarjeta.removeAttr('disabled');
				$select_chequera_cheque.removeAttr('disabled');
				$select_chequera_transferencia.removeAttr('disabled');
				$monto_anticipo.removeAttr('disabled');
				$select_orden_compra.removeAttr('disabled');
			}
		}
	}//termina  habilitar y deshabilitar campos
	
	
	//funciones para calculos en pagos--------------------------------------------------------------------------------------
	
	//termina funciones para los calculos en pagos------------------------------------------------------
	
	
	
    
	
	//nuevo pago
	$new_pago.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_proveedoresanticipos();
		
		var form_to_show = 'formaAnticipos00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$forma_selected.prependTo('#forma-proveedoresanticipos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$('#forma-proveedoresanticipos-window').css({"margin-left": -350,"margin-top": -200});
        
        
		$tabs_li_funxionalidad();
		
		
		var $id_anticipo = $('#forma-proveedoresanticipos-window').find('input[name=id_anticipo]');
		var $total_tr = $('#forma-proveedoresanticipos-window').find('input[name=total_tr]');
		var $id_proveedor = $('#forma-proveedoresanticipos-window').find('input[name=id_proveedor]');
		var $no_transaccion = $('#forma-proveedoresanticipos-window').find('input[name=no_transaccion]');
		var $proveedor = $('#forma-proveedoresanticipos-window').find('input[name=proveedor]');
		
		var $busca_proveedor = $('#forma-proveedoresanticipos-window').find('a[href*=busca_proveedor]');
		
		var $select_tipo_movimiento = $('#forma-proveedoresanticipos-window').find('select[name=select_tipo_movimiento]');
		var $observaciones_anticipo = $('#forma-proveedoresanticipos-window').find('textarea[name=observaciones_anticipo]');
		var $select_concepto = $('#forma-proveedoresanticipos-window').find('select[name=select_concepto]');
		var $select_moneda = $('#forma-proveedoresanticipos-window').find('select[name=select_moneda]');
		var $fecha_anticipo = $('#forma-proveedoresanticipos-window').find('input[name=fecha_anticipo]');
		var $select_forma_pago = $('#forma-proveedoresanticipos-window').find('select[name=select_forma_pago]');
		var $tipo_cambio = $('#forma-proveedoresanticipos-window').find('input[name=tipo_cambio]');
		var $select_banco_cheque = $('#forma-proveedoresanticipos-window').find('select[name=select_banco_cheque]');
		var $select_banco_transferencia = $('#forma-proveedoresanticipos-window').find('select[name=select_banco_transferencia]');
		
		var $select_chequera_cheque = $('#forma-proveedoresanticipos-window').find('select[name=select_chequera_cheque]');
		var $select_chequera_transferencia = $('#forma-proveedoresanticipos-window').find('select[name=select_chequera_transferencia]');
		
		
		var $num_cheque = $('#forma-proveedoresanticipos-window').find('input[name=num_cheque]');
		var $referencia = $('#forma-proveedoresanticipos-window').find('input[name=referencia]');
		var $num_tarjeta = $('#forma-proveedoresanticipos-window').find('input[name=num_tarjeta]');
		
		
		var $monto_anticipo = $('#forma-proveedoresanticipos-window').find('input[name=monto_anticipo]');
		var $select_orden_compra = $('#forma-proveedoresanticipos-window').find('select[name=select_orden_compra]');
		var $cancelar_anticipo = $('#forma-proveedoresanticipos-window').find('#cancelar_anticipo');
		var $pdf_anticipo = $('#forma-proveedoresanticipos-window').find('#pdf_anticipo');
		
		var $tr_cheque = $('#forma-proveedoresanticipos-window').find('.cheque');
		var $tr_transferencia = $('#forma-proveedoresanticipos-window').find('.transferencia');
		var $tr_tarjeta = $('#forma-proveedoresanticipos-window').find('.tarjeta');
		
		
		var $cerrar_plugin = $('#forma-proveedoresanticipos-window').find('#close');
		var $cancelar_plugin = $('#forma-proveedoresanticipos-window').find('#boton_cancelar');
		var $registra_anticipo = $('#forma-proveedoresanticipos-window').find('#submit');
		
		
		$add_ceros($monto_anticipo);
		$permitir_solo_numeros($monto_anticipo);
		$accion_focus($monto_anticipo);
		$accion_blur($monto_anticipo);
		
        $permitir_solo_numeros($tipo_cambio);
        $fecha_anticipo.attr("readonly", true);
        //$cancelar_anticipo.hide();
        //$pdf_anticipo.hide();
        $cancelar_anticipo.attr('disabled','-1');
        $pdf_anticipo.attr('disabled','-1');
        $no_transaccion.css({'background' : '#DDDDDD'});
        $pdf_anticipo.hide();
        //$registra_anticipo.hide();
        
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-proveedoresanticipos-window').find('div.interrogacion').css({'display':'none'});
				
				if ( data['error_cheque'] == "false" ){
					jAlert('Anticipo registrado con &eacute;xito.\nN&uacute;mero de transacci&oacute;n: '+data['identificador_anticipo'],'Atencion!')
					$registra_anticipo.hide();
					/*
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfReporteAplicacionPagoProveedor/'+data['identificador_pago']+'/'+$id_proveedor.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
					*/
					$get_datos_grid();
					
					var remove = function() {$(this).remove();};
					$('#forma-proveedoresanticipos-overlay').fadeOut(remove);
				}else{
					jAlert('No se pudo registrar el Anticipo: '+data['error_cheque'],'Atencion!');
					$('#forma-proveedoresanticipos-window').find('img[rel=warning_nocuenta]')
					.parent()
					.css({'display':'block'})
					.easyTooltip({tooltipId: "easyTooltip2",content: data['error_cheque'] });
				}
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-proveedoresanticipos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-proveedoresanticipos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAnticipo.json';
		$arreglo = {'id':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		$.post(input_json,$arreglo,function(entry){
			//inicializar campos
			$inicializar_campos($id_anticipo,$total_tr,$id_proveedor,$no_transaccion,$proveedor,$busca_proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_anticipo,$select_moneda,$fecha_anticipo,$select_forma_pago,$tipo_cambio, $select_banco_cheque, $select_banco_transferencia,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$monto_anticipo,$select_orden_compra,$tr_cheque,$tr_transferencia,$tr_tarjeta,entry['Tipocambio'][0]['valor_tipo_cambio'],entry['Monedas'],entry['Formaspago'],entry['Bancos'],entry['Conceptos']);
			
			//deshabilitar todos los campos al cargar plugin, habilitarlos hasta que se seleecione un proveedor
			$deshabilitar_campos("desahabilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_anticipo,$select_moneda,$fecha_anticipo,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$monto_anticipo,$select_orden_compra);
			
			//buscador de proveedores
			$busca_proveedor.click(function(event){
				event.preventDefault();
				$busca_proveedores($select_tipo_movimiento,$select_moneda,entry['Monedas'] );
			});
			
			
			$select_tipo_movimiento.change(function(){
				var tipo_mov = $(this).val();
				//tipo seleccionar proveedor
				if(parseInt(tipo_mov)==0){
					$('#forma-proveedoresanticipos-window').find('.proveedoresanticipos_div_one').css({'height':'445px'});
					
					//habilitar todos los campos 
					$deshabilitar_campos("desahabilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_anticipo,$select_moneda,$fecha_anticipo,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$monto_anticipo,$select_orden_compra);
					$busca_proveedor.show();
					$inicializar_campos($id_anticipo,$total_tr,$id_proveedor,$no_transaccion,$proveedor,$busca_proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_anticipo,$select_moneda,$fecha_anticipo,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$monto_anticipo,$select_orden_compra,$tr_cheque,$tr_transferencia,$tr_tarjeta,entry['Tipocambio'][0]['valor_tipo_cambio'],entry['Monedas'],entry['Formaspago'],entry['Bancos'],entry['Conceptos']);
					
					//deshabilitar todos los campos al cargar plugin, habilitarlos hasta que se seleecione un proveedor
					$deshabilitar_campos("desahabilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_anticipo,$select_moneda,$fecha_anticipo,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$monto_anticipo,$select_orden_compra);
				}
				
				//tipo movimiento pago
				if(parseInt(tipo_mov)==1){
					//habilitar todos los campos
					$deshabilitar_campos("habilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_anticipo,$select_moneda,$fecha_anticipo,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$monto_anticipo,$select_orden_compra);
					$busca_proveedor.hide();
				}//termina tipo movimiento pago
				
			});//termina change select_tipo_movimiento
			
			$select_moneda.change(function(){
				//carga select con todos los bancos
				elemento_seleccionado = 0;
				texto_elemento_cero='Seleccionar Banco';
				
				$carga_campos_select($select_banco_cheque, entry['Bancos'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");			
				$carga_campos_select($select_banco_transferencia, entry['Bancos'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
				
				
				$select_chequera_cheque.children().remove();
				var option_hmtl = '<option value="0">Seleccionar Chequera</option>';
				$select_chequera_cheque.append(option_hmtl);
				
				$select_chequera_transferencia.children().remove();
				$select_chequera_transferencia.append(option_hmtl);
			});
		});//termina llamada json
		
		
		
		//fecha pago
		$fecha_anticipo.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_anticipo.DatePicker({
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
				$fecha_anticipo.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_anticipo.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_anticipo.val(mostrarFecha());
					}else{
						$fecha_anticipo.DatePickerHide();	
					}
				}
			}
		});
		
		
        
		
		$select_forma_pago.change(function(){
			var forma_pago = $(this).val();
			$num_cheque.val('');
			$referencia.val('');
			$tr_cheque.hide();
			$tr_transferencia.hide();
			$tr_tarjeta.hide();
			
			//efectivo
			if(parseInt(forma_pago)==1){
				$('#forma-proveedoresanticipos-window').find('.proveedoresanticipos_div_one').css({'height':'300px'});
			}
			
			//cheque
			if(parseInt(forma_pago)==2){
				$tr_cheque.show();
				$('#forma-proveedoresanticipos-window').find('.proveedoresanticipos_div_one').css({'height':'300px'});
			}
			//transferencia
			if(parseInt(forma_pago)==3){
				$tr_transferencia.show();
				$('#forma-proveedoresanticipos-window').find('.proveedoresanticipos_div_one').css({'height':'300px'});
			}
			
			
		});
		
		
		
		//este se ejecuta cada vez que se hace un change a los select_banco_cheque, select_banco_transferencia 
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
		
		
		$registra_anticipo.bind('click',function(){
			if(parseFloat($monto_anticipo.val()) <= 0){
				jAlert("El monto del Anticipo debe ser mayor que cero.", 'Atencion!');
				return false;
			}else{
				return true;
			}
		});
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-proveedoresanticipos-overlay').fadeOut(remove);
		});
		
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-proveedoresanticipos-overlay').fadeOut(remove);
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
			
			$(this).modalPanel_proveedoresanticipos();
			
			var form_to_show = 'formaAnticipos00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$forma_selected.prependTo('#forma-proveedoresanticipos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$('#forma-proveedoresanticipos-window').css({"margin-left": -350,"margin-top": -200});
			
			$tabs_li_funxionalidad();
			
			var $id_anticipo = $('#forma-proveedoresanticipos-window').find('input[name=id_anticipo]');
			var $total_tr = $('#forma-proveedoresanticipos-window').find('input[name=total_tr]');
			var $id_proveedor = $('#forma-proveedoresanticipos-window').find('input[name=id_proveedor]');
			var $no_transaccion = $('#forma-proveedoresanticipos-window').find('input[name=no_transaccion]');
			var $proveedor = $('#forma-proveedoresanticipos-window').find('input[name=proveedor]');
			
			var $busca_proveedor = $('#forma-proveedoresanticipos-window').find('a[href*=busca_proveedor]');
			
			var $select_tipo_movimiento = $('#forma-proveedoresanticipos-window').find('select[name=select_tipo_movimiento]');
			var $observaciones_anticipo = $('#forma-proveedoresanticipos-window').find('textarea[name=observaciones_anticipo]');
			var $select_concepto = $('#forma-proveedoresanticipos-window').find('select[name=select_concepto]');
			var $select_moneda = $('#forma-proveedoresanticipos-window').find('select[name=select_moneda]');
			var $fecha_anticipo = $('#forma-proveedoresanticipos-window').find('input[name=fecha_anticipo]');
			var $select_forma_pago = $('#forma-proveedoresanticipos-window').find('select[name=select_forma_pago]');
			var $tipo_cambio = $('#forma-proveedoresanticipos-window').find('input[name=tipo_cambio]');
			var $select_banco_cheque = $('#forma-proveedoresanticipos-window').find('select[name=select_banco_cheque]');
			var $select_banco_transferencia = $('#forma-proveedoresanticipos-window').find('select[name=select_banco_transferencia]');
			var $select_chequera_cheque = $('#forma-proveedoresanticipos-window').find('select[name=select_chequera_cheque]');
			var $select_chequera_transferencia = $('#forma-proveedoresanticipos-window').find('select[name=select_chequera_transferencia]');
			
			var $num_cheque = $('#forma-proveedoresanticipos-window').find('input[name=num_cheque]');
			var $referencia = $('#forma-proveedoresanticipos-window').find('input[name=referencia]');
			var $num_tarjeta = $('#forma-proveedoresanticipos-window').find('input[name=num_tarjeta]');
			var $select_chequera = $('#forma-proveedoresanticipos-window').find('select[name=select_chequera]');
			var $monto_anticipo = $('#forma-proveedoresanticipos-window').find('input[name=monto_anticipo]');
			var $select_orden_compra = $('#forma-proveedoresanticipos-window').find('select[name=select_orden_compra]');
			var $cancelar_anticipo = $('#forma-proveedoresanticipos-window').find('#cancelar_anticipo');
			var $pdf_anticipo = $('#forma-proveedoresanticipos-window').find('#pdf_anticipo');
			
			var $tr_cheque = $('#forma-proveedoresanticipos-window').find('.cheque');
			var $tr_transferencia = $('#forma-proveedoresanticipos-window').find('.transferencia');
			var $tr_tarjeta = $('#forma-proveedoresanticipos-window').find('.tarjeta');
			
			
			var $cerrar_plugin = $('#forma-proveedoresanticipos-window').find('#close');
			var $cancelar_plugin = $('#forma-proveedoresanticipos-window').find('#boton_cancelar');
			var $registra_anticipo = $('#forma-proveedoresanticipos-window').find('#submit');
			
			$tr_cheque.hide();
			$tr_transferencia.hide();
			$tr_tarjeta.hide();
			$busca_proveedor.hide();
			$fecha_anticipo.attr("readonly", true);
			$cancelar_anticipo.hide();
			
			$pdf_anticipo.hide();
			
			$registra_anticipo.hide();
			$no_transaccion.css({'background' : '#DDDDDD'});
			
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAnticipo.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-proveedoresanticipos-overlay').fadeOut(remove);
						jAlert("Los datos Agente se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-proveedoresanticipos-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-proveedoresanticipos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$id_anticipo.attr({ 'value' : entry['Datos']['0']['id'] });
					$id_proveedor.attr({ 'value' : entry['Datos']['0']['cxp_prov_id'] });
					$no_transaccion.attr({ 'value' : entry['Datos']['0']['folio'] });
					$proveedor.attr({ 'value' : entry['Datos']['0']['razon_social'] });
					//$observaciones_anticipo.attr({ 'value' : entry['Datos']['0']['observaciones'] });
					$observaciones_anticipo.text(entry['Datos'][0]['observaciones']);
					$tipo_cambio.attr({ 'value' : entry['Datos']['0']['tipo_cambio'] });
					$fecha_anticipo.attr({ 'value' : entry['Datos']['0']['fecha_anticipo'] });
					$num_cheque.attr({ 'value' : entry['Datos']['0']['numero_cheque'] });
					$referencia.attr({ 'value' : entry['Datos']['0']['referencia'] });
					$monto_anticipo.attr({ 'value' : entry['Datos']['0']['monto_anticipo'] });
					
					if( entry['Datos']['0']['cancelado'] =='false' ){
						$cancelar_anticipo.show();
						$pdf_anticipo.show();
					}
					
					//cargar select de tipos de movimiento
					//elemento_seleccionado = 1;
					//texto_elemento_cero='Seleccionar Proveedor';
					//$carga_campos_select($select_tipo_movimiento, entry['Tiposmov'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
					//cargar select de tipos de movimiento
					elemento_seleccionado = 1;
					$carga_select_con_arreglo_fijo($select_tipo_movimiento, array_select_opcion, elemento_seleccionado);
					
					
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
					
					$select_orden_compra.children().remove();
					var option_hmtl2 = '<option value="0">Seleccionar Orden Compra</option>';
					$select_orden_compra.append(option_hmtl2);	
					
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
						
						$('#forma-proveedoresanticipos-window').find('.proveedoresanticipos_div_one').css({'height':'300px'});
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
						$('#forma-proveedoresanticipos-window').find('.proveedoresanticipos_div_one').css({'height':'300px'});
					}
					
					$deshabilitar_campos("desahabilitar",$no_transaccion,$proveedor,$select_tipo_movimiento,$select_concepto,$observaciones_anticipo,$select_moneda,$fecha_anticipo,$select_forma_pago,$tipo_cambio,$select_banco_cheque, $select_banco_transferencia,$num_cheque,$referencia,$num_tarjeta,$select_chequera_cheque,$select_chequera_transferencia,$monto_anticipo,$select_orden_compra);
				},"json");//termina llamada json
				
				
				
                
				//cancelar pago
				$cancelar_anticipo.click(function(event){
					event.preventDefault();
					var id_to_show = 0;
					$(this).modalPanel_cancelaanticipo();
					var form_to_show = 'formacancelaanticipo';
					$('#' + form_to_show).each (function(){this.reset();});
					var $forma_selected = $('#' + form_to_show).clone();
					$forma_selected.attr({id : form_to_show + id_to_show});
					$('#forma-cancelaanticipo-window').css({"margin-left": -100,"margin-top": -180});
					$forma_selected.prependTo('#forma-cancelaanticipo-window');
					$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
					
					var $motivo_cancelacion = $('#forma-cancelaanticipo-window').find('textarea[name=motivo_cancel]');
					
					var $cancelar_anticipo = $('#forma-cancelaanticipo-window').find('a[href*=cancel_pago]');
					var $salir = $('#forma-cancelaanticipo-window').find('a[href*=salir]');
					
					//cancelar pago
					$cancelar_anticipo.click(function(event){
						event.preventDefault();
						if($motivo_cancelacion.val()!=""){
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/cancelar_anticipo.json';
							$arreglo = {'id_anticipo':$id_anticipo.val(),
										'motivo':$motivo_cancelacion.val(),
										'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
										}
							
							$.post(input_json,$arreglo,function(entry){
								var remove = function() {$(this).remove();};
								$('#forma-cancelaanticipo-overlay').fadeOut(remove);
								
								jAlert("El Anticipo con N&uacute;mero de Transacci&oacute;n "+entry['success']+"  se ha cancelado con &eacute;xito", 'Atencion!');
								
								var remove = function() { $(this).remove(); };
								$('#forma-proveedoresanticipos-overlay').fadeOut(remove);
								$get_datos_grid();
								
							});//termina llamada json
						}else{
							jAlert("Es necesario ingresar el motivo de la cancelaci&oacute;n", 'Atencion!');
						}
					});
					
					$salir.click(function(event){
						event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-cancelaanticipo-overlay').fadeOut(remove);
					});
					
				});//termina cancelar factura
                
				
				
				//descargar pdf de anticipo
				$pdf_anticipo.click(function(event){
					if(parseInt($id_anticipo.val()) !=0){
						var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
						var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfReporteAplicacionAnticipoProveedor/'+$id_anticipo.val()+'/'+$id_proveedor.val()+'/'+iu+'/out.json';
						window.location.href=input_json;
					}
				});
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-proveedoresanticipos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-proveedoresanticipos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    
    
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllAnticipos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllAnticipos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaPagos00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});
