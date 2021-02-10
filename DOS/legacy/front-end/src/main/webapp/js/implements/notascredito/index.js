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
	var controller = $contextpath.val()+"/controllers/notascredito";
    
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
	$('#barra_titulo').find('#td_titulo').append('Notas de Cr&eacute;dito');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_factura = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_factura]');
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
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "factura" + signo_separador + $busqueda_factura.val() + "|";
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
		$busqueda_cliente.val('');
		$busqueda_factura.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		
		$busqueda_factura.focus();
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
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_factura, $buscar);
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
	
	
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-notascredito-window').find('select[name=prodtipo]');
		$('#forma-notascredito-window').find('#submit').mouseover(function(){
			$('#forma-notascredito-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-notascredito-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-notascredito-window').find('#submit').mouseout(function(){
			$('#forma-notascredito-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-notascredito-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-notascredito-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-notascredito-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-notascredito-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-notascredito-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-notascredito-window').find('#close').mouseover(function(){
			$('#forma-notascredito-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-notascredito-window').find('#close').mouseout(function(){
			$('#forma-notascredito-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-notascredito-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-notascredito-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-notascredito-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-notascredito-window').find("ul.pestanas li").click(function() {
			$('#forma-notascredito-window').find(".contenidoPes").hide();
			$('#forma-notascredito-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-notascredito-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
	
	
	
	var $agregarDatosClienteSeleccionado = function($select_moneda, $select_vendedor, array_monedas, array_vendedores, id_cliente, nocliente, razoncliente, empresa_immex, tasa_ret_immex, id_moneda, id_vendedor){
		//asignar a los campos correspondientes los datos del cliente
		$('#forma-notascredito-window').find('input[name=id_cliente]').val( id_cliente );
		$('#forma-notascredito-window').find('input[name=nocliente]').val( nocliente );
		$('#forma-notascredito-window').find('input[name=razoncliente]').val( razoncliente );
		$('#forma-notascredito-window').find('input[name=empresa_immex]').val( empresa_immex );
		$('#forma-notascredito-window').find('input[name=tasa_ret_immex]').val( tasa_ret_immex );
		
		//carga el select de monedas  con la moneda del cliente seleccionada por default
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
		
		
		//carga select de vendedores
		$select_vendedor.children().remove();
		var hmtl_vendedor;
		$.each(array_vendedores,function(entryIndex,vendedor){
			if( parseInt(vendedor['id']) == parseInt(id_vendedor) ){
				hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes">' + vendedor['nombre_vendedor'] + '</option>';
			}else{
				//hmtl_vendedor += '<option value="' + vendedor['id'] + '" >' + vendedor['nombre_agente'] + '</option>';
			}
		});
		$select_vendedor.append(hmtl_vendedor);
	};
	
	
	//buscador de clientes
	$busca_clientes = function($select_moneda, $select_vendedor, array_monedas, array_vendedores, razon_social_cliente, numero_control ){
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
		
		if(numero_control != ''){
			//asignamos el numero de control al campo de busqueda
			$cadena_buscar.val(numero_control);
			if(razon_social_cliente == ''){
				html+='<option value="1" selected="yes">No. de control</option>';
			}else{
				html+='<option value="1">No. de control</option>';
			}
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if(razon_social_cliente != ''){
			//asignamos la Razon Social del Cliente al campo Nombre
			$cadena_buscar.val(razon_social_cliente);
			html+='<option value="3" selected="yes">Razon social</option>';
		}else{
			if(razon_social_cliente == '' && numero_control == ''){
				html+='<option value="3" selected="yes">Razon social</option>';
			}else{
				html+='<option value="3">Razon social</option>';
			}
		}
		html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		
		$cadena_buscar.focus();
		
		
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_clientes.json';
			$arreglo = {	'cadena':$cadena_buscar.val(),
							'filtro':$select_filtro_por.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['clientes'],function(entryIndex,cliente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
							trr += '<input type="hidden" id="direccion" value="'+cliente['direccion']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+cliente['moneda_id']+'">';
							trr += '<input type="hidden" id="moneda" value="'+cliente['moneda']+'">';
							trr += '<input type="hidden" id="vendedor_id" value="'+cliente['cxc_agen_id']+'">';
							trr += '<input type="hidden" id="terminos_id" value="'+cliente['terminos_id']+'">';
							trr += '<input type="hidden" id="emp_immex" value="'+cliente['empresa_immex']+'">';
							trr += '<input type="hidden" id="tasa_immex" value="'+cliente['tasa_ret_immex']+'">';
							trr += '<input type="hidden" id="cta_mn" value="'+cliente['cta_pago_mn']+'">';
							trr += '<input type="hidden" id="cta_usd" value="'+cliente['cta_pago_usd']+'">';
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
					$(this).find('td').css({'background-color':'#e7e8ea'});
				});
				$('tr:even' , $tabla_resultados).hover(function () {
					$(this).find('td').css({'background-color':'#FBD850'});
				}, function() {
					$(this).find('td').css({'background-color':'#FFFFFF'});
				});
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					var id_cliente = $(this).find('#idclient').val();
					var nocliente = $(this).find('span.no_control').html();
					var razoncliente = $(this).find('span.razon').html();
					var empresa_immex = $(this).find('#emp_immex').val();
					var tasa_ret_immex = $(this).find('#tasa_immex').val();
					var id_moneda=$(this).find('#id_moneda').val();
					var id_vendedor=$(this).find('#vendedor_id').val();
					
					//llamada a funcion que aguega datos del Cliente
					$agregarDatosClienteSeleccionado($select_moneda, $select_vendedor, array_monedas, array_vendedores, id_cliente, nocliente, razoncliente, empresa_immex, tasa_ret_immex, id_moneda, id_vendedor);
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					//asignar el enfoque al campo Razon Social del Cliente
					$('#forma-notascredito-window').find('input[name=razoncliente]').focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$busca_cliente_modalbox.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $busca_cliente_modalbox);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $busca_cliente_modalbox);
		
		
		$cancelar_plugin_busca_cliente.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
			
			//asignar el enfoque al campo Razon Social del Cliente
			$('#forma-notascredito-window').find('input[name=razoncliente]').focus();
		});
	}//termina buscador de clientes
	
	
	
	var $agregarDatosFacturaSeleccionada = function($select_moneda,$select_vendedor, array_monedas, array_vendedores, $factura, $fecha_factura, $monto_factura, $aplicado, $saldo, factura, id_moneda, id_agente, monto_factura, saldo_factura, fecha_factura){
		$('#forma-notascredito-window').find('input[name=id_moneda_factura]').val(id_moneda);
		var monto_aplicado=parseFloat(monto_factura) - parseFloat(saldo_factura);
		
		$factura.val(factura);
		$fecha_factura.val(fecha_factura);
		$monto_factura.val( $(this).agregar_comas( parseFloat(monto_factura).toFixed(2)));
		$aplicado.val( $(this).agregar_comas(monto_aplicado.toFixed(2)));
		$saldo.val( $(this).agregar_comas(parseFloat(saldo_factura).toFixed(2)) );
		
		//carga el select de monedas  con la moneda del cliente seleccionada por default
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
		
		//carga select de vendedores
		$select_vendedor.children().remove();
		var hmtl_vendedor;
		$.each(array_vendedores,function(entryIndex,vendedor){
			if( parseInt(vendedor['id']) == parseInt(id_agente) ){
				hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes">' + vendedor['nombre_vendedor'] + '</option>';
			}else{
				//hmtl_vendedor += '<option value="' + vendedor['id'] + '" >' + vendedor['nombre_agente'] + '</option>';
			}
		});
		$select_vendedor.append(hmtl_vendedor);
	};
	
	
	//buscador de facturas con saldo del cliente seleccionado
	$busca_facturas = function($select_moneda,$select_vendedor, id_cliente, array_monedas, array_vendedores, $factura, $fecha_factura, $monto_factura, $aplicado, $saldo){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFacturasCliente.json';
		$arreglo = {'id_cliente':id_cliente, 'serie_folio':$factura.val()};
		
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
								trr += '<span class="id_agen" style="display:none">'+fac['cxc_agen_id']+'</span>';
								trr += '<span class="fech_fac" style="display:none">'+fac['fecha_factura']+'</span>';
								trr += '<span class="factura">'+fac['factura']+'</span>';
							trr += '</td>';
							trr += '<td width="100" align="right"><span class="monto">'+fac['monto_factura']+'</span></td>';
							trr += '<td width="100" align="right"><span class="saldo">'+fac['saldo_factura']+'</span></td>';
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
						var id_agente = $(this).find('span.id_agen').html();
						var monto_factura = $(this).find('span.monto').html();
						var saldo_factura = $(this).find('span.saldo').html();
						var fecha_factura = $(this).find('span.fech_fac').html();
						
						//llamada a la funcion para agregar datos de la factura
						$agregarDatosFacturaSeleccionada($select_moneda,$select_vendedor, array_monedas, array_vendedores, $factura, $fecha_factura, $monto_factura, $aplicado, $saldo, factura, id_moneda, id_agente, monto_factura, saldo_factura, fecha_factura);
						
						//elimina la ventana de busqueda
						var remove = function() {$(this).remove();};
						$('#forma-buscafacturas-overlay').fadeOut(remove);
						
						$factura.focus();
					});
					
					$cancelar_busca_remisiones.click(function(event){
						//event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-buscafacturas-overlay').fadeOut(remove);
						
						$factura.focus();
					});
				}else{
					jAlert('El cliente seleccionado no tiene Facturas pendientes de Pago.\nSeleccione un cliente diferente y haga click en Buscar.', 'Atencion!', function(r) { 
						$('#forma-notascredito-window').find('input[name=nocliente]').focus(); 
					});
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
	
	
	$limpiar_campos = function($importe,$ieps,$impuesto,$retencion,$total,$factura, $fecha_factura, $monto_factura, $monto_factura, $aplicado, $saldo, $fac_saldado){
		$importe.val('');
		$ieps.val('');
		$impuesto.val('');
		$retencion.val('');
		$total.val('');
		$factura.val('');
		$fecha_factura.val('');
		$monto_factura.val('');
		$monto_factura.val('');
		$aplicado.val('');
		$saldo.val('');
		$fac_saldado.val('false');
	}
	
	
	$calcula_total_nota_credito = function($importe, $ieps, $impuesto, $retencion, $total, $valor_impuesto, $tasa_retencion, $saldo_factura,$empresa_immex,$chkbox_aplicar_saldo, $fac_saldado,evaluar ){
		var SubTotal = 0; //aqui va el valor del importe
		var importeIeps=0;
		var Impuesto = 0; //monto del iva calculado a partir del importe
		var impuestoRetenido = 0; //monto del iva retenido de acuerdo a la tasa de retencion immex
		var Total = 0; //suma del subtotal + totalImpuesto - impuestoRetenido
		var saldo_fac=quitar_comas($saldo_factura.val());
		
		SubTotal = $importe.val();
		Impuesto = parseFloat(SubTotal) * parseFloat($valor_impuesto.val());
		impuestoRetenido = parseFloat(SubTotal) * parseFloat($tasa_retencion.val());
		Total = parseFloat(SubTotal) + parseFloat(Impuesto) - parseFloat(impuestoRetenido);
		
		$importe.val(parseFloat(SubTotal).toFixed(2));
		$impuesto.val(parseFloat(Impuesto).toFixed(2));
		$retencion.val(parseFloat(impuestoRetenido).toFixed(2));
		$total.val(parseFloat(Total).toFixed(2));
		
		//si evaluar es igual a verdadero, se checa que el monto de la Nota de Credito no sea mayor que el saldo
		//cuando es nueva Nota de Credito se evalua
		//cuando solo es ver detalle de una Nota de Credito generada con anterioridad, ya no es necesario hacer esta evaluacion
		if(evaluar=="true"){
			//aqui entra cuando el total de la nota de credito sobrepasa al saldo de la factura
			if(parseFloat($total.val()) > parseFloat(saldo_fac) ){
				jAlert("El Total de la Nota de Cr&eacute;dito no debe ser mayor que el saldo de la factura.\n	Total Nota Cr&eacute;dito: "+parseFloat(Total).toFixed(2)+"\n	Saldo Factura: "+$saldo_factura.val()+"\nSe asignara por default el saldo de la factura al total de la Nota.", 'Atencion!');
				
				if( $empresa_immex.val() == 'true' ){
					SubTotal = saldo_fac;
					Impuesto = parseFloat(SubTotal) * parseFloat($valor_impuesto.val());
					impuestoRetenido = parseFloat(SubTotal) * parseFloat($tasa_retencion.val());
					
					$importe.val(parseFloat(SubTotal).toFixed(2));
					$impuesto.val(parseFloat(Impuesto).toFixed(2));
					$retencion.val(parseFloat(impuestoRetenido).toFixed(2));
					$total.val( $saldo_factura.val() );
					
				}else{
					SubTotal = saldo_fac / ( parseFloat($valor_impuesto.val()) + 1 );
					Impuesto = parseFloat(SubTotal) * parseFloat($valor_impuesto.val());
					impuestoRetenido = parseFloat(SubTotal) * parseFloat($tasa_retencion.val());
					
					//alert(SubTotal);
					$importe.val(parseFloat(SubTotal).toFixed(2));
					$impuesto.val(parseFloat(Impuesto).toFixed(2));
					$retencion.val(parseFloat(impuestoRetenido).toFixed(2));
					$total.val( $saldo_factura.val() );
				}
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
		
		$(this).modalPanel_notascredito();
		
		var form_to_show = 'formanotascredito00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-notascredito-window').css({"margin-left": -340, 	"margin-top": -200});
		
		$forma_selected.prependTo('#forma-notascredito-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getNotaCredito.json';
		$arreglo = {'id_nota_credito':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
        
		var $identificador = $('#forma-notascredito-window').find('input[name=identificador]');
		var $folio_nota_credito = $('#forma-notascredito-window').find('input[name=folio_nota_credito]');
		var $fecha_exp = $('#forma-notascredito-window').find('input[name=fecha_exp]');
		var $generar = $('#forma-notascredito-window').find('input[name=generar]');
		var $fac_saldado = $('#forma-notascredito-window').find('input[name=fac_saldado]');
		
		var $busca_cliente = $('#forma-notascredito-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-notascredito-window').find('input[name=id_cliente]');
		var $no_cliente = $('#forma-notascredito-window').find('input[name=nocliente]');
		var $razon_cliente = $('#forma-notascredito-window').find('input[name=razoncliente]');
		var $empresa_immex = $('#forma-notascredito-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-notascredito-window').find('input[name=tasa_ret_immex]');
		
		var $select_moneda = $('#forma-notascredito-window').find('select[name=moneda]');
		var $id_impuesto = $('#forma-notascredito-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-notascredito-window').find('input[name=valorimpuesto]');
		var $observaciones = $('#forma-notascredito-window').find('textarea[name=observaciones]');
		var $select_vendedor = $('#forma-notascredito-window').find('select[name=select_vendedor]');
		var $select_moneda = $('#forma-notascredito-window').find('select[name=select_moneda]');
		var $tipo_cambio = $('#forma-notascredito-window').find('input[name=tipo_cambio]');
		var $concepto = $('#forma-notascredito-window').find('textarea[name=concepto]');
		var $select_tmov = $('#forma-notascredito-window').find('select[name=select_tmov]');
		
		var $importe = $('#forma-notascredito-window').find('input[name=importe]');
		var $ieps = $('#forma-notascredito-window').find('input[name=ieps]');
		var $impuesto = $('#forma-notascredito-window').find('input[name=impuesto]');
		var $retencion = $('#forma-notascredito-window').find('input[name=retencion]');
		var $total = $('#forma-notascredito-window').find('input[name=total]');
		var $generar_nota_credito = $('#forma-notascredito-window').find('#generar_nota_credito');
		var $cancelar_nota_credito = $('#forma-notascredito-window').find('#cancelar_nota_credito');
		var $descargar_pdf = $('#forma-notascredito-window').find('#descargar_pdf');
		var $descargar_xml = $('#forma-notascredito-window').find('#descargar_xml');
		var $cancelado = $('#forma-notascredito-window').find('input[name=cancelado]');
		
		var $busca_factura = $('#forma-notascredito-window').find('a[href*=busca_factura]');
		var $factura = $('#forma-notascredito-window').find('input[name=factura]');
		var $id_moneda_factura = $('#forma-notascredito-window').find('input[name=id_moneda_factura]');
		var $fecha_factura = $('#forma-notascredito-window').find('input[name=fecha_factura]');
		var $monto_factura = $('#forma-notascredito-window').find('input[name=monto_factura]');
		var $aplicado = $('#forma-notascredito-window').find('input[name=aplicado]');
		var $saldo = $('#forma-notascredito-window').find('input[name=saldo]');
		var $chkbox_aplicar_saldo = $('#forma-notascredito-window').find('input[name=chkbox_aplicar_saldo]');
		
		var $cerrar_plugin = $('#forma-notascredito-window').find('#close');
		var $cancelar_plugin = $('#forma-notascredito-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-notascredito-window').find('#submit');
		
		$identificador.val(0);//para nueva cotizacion el folio es 0
		//$submit_actualizar.hide();
		//$generar_nota_credito.hide();
		
		$permitir_solo_numeros($tipo_cambio);
		$permitir_solo_numeros($importe);
		//$aplicar_evento_blur($importe);
		//$aplicar_evento_focus($importe);
		
		$folio_nota_credito.css({'background' : '#F0F0F0'});
		$fecha_factura.css({'background' : '#F0F0F0'});
		$monto_factura.css({'background' : '#F0F0F0'});
		$aplicado.css({'background' : '#F0F0F0'});
		$saldo.css({'background' : '#F0F0F0'});
		
		$generar_nota_credito.attr('disabled','-1');
		$cancelar_nota_credito.attr('disabled','-1');
		$descargar_pdf.attr('disabled','-1');
		$descargar_xml.attr('disabled','-1');
		$fac_saldado.val('false');
		$cancelado.hide();
		
		//quitar enter a todos los campos input
		$('#forma-notascredito-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				
				jAlert(data['msj'], 'Atencion!');
				
				if ( data['valor'] == "true" ){
					$get_datos_grid();
					var remove = function() {$(this).remove();};
					$('#forma-notascredito-overlay').fadeOut(remove);
				}
				/*
				jAlert("Los datos se han guardado con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-notascredito-overlay').fadeOut(remove);
				$get_datos_grid();
				*/
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-notascredito-window').find('div.interrogacion').css({'display':'none'});

				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-notascredito-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
			$id_impuesto.val(entry['iva']['0']['id_impuesto']);
			$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
			$tipo_cambio.val(entry['Tc']['0']['tipo_cambio']);
			
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '">' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			
			//carga select de vendedores
			$select_vendedor.children().remove();
			var hmtl_vendedor;
			$.each(entry['Vendedores'],function(entryIndex,vendedor){
				hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_vendedor'] + '</option>';
			});
			$select_vendedor.append(hmtl_vendedor);
			
			$select_tmov.children().remove();
			var tmov_hmtl = '<option value="0">[--- ---]</option>';
			if(entry['TMov']){
				$.each(entry['TMov'],function(entryIndex,mov){
					tmov_hmtl += '<option value="'+ mov['id'] +'">'+ mov['titulo'] + '</option>';
				});
			}
			$select_tmov.append(tmov_hmtl);
			
			//buscador de clientes
			$busca_cliente.click(function(event){
				event.preventDefault();
				$busca_clientes( $select_moneda,$select_vendedor, entry['Monedas'],entry['Vendedores'], $razon_cliente.val(), $no_cliente.val() );
				$limpiar_campos($importe, $ieps, $impuesto,$retencion,$total,$factura, $fecha_factura, $monto_factura, $monto_factura, $aplicado, $saldo, $fac_saldado);
			});
			
			//buscador de facturas del cliente
			$busca_factura.click(function(event){
				event.preventDefault();
				if ($razon_cliente.val().trim()!=''){
					$busca_facturas( $select_moneda,$select_vendedor,$id_cliente.val(), entry['Monedas'],entry['Vendedores'],$factura, $fecha_factura, $monto_factura, $aplicado, $saldo );
				}else{
					jAlert('Es necesario seleccionar un Cliente.', 'Atencion!', function(r) { 
						$no_cliente.focus(); 
					});
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
			
			//buscar y agregar datos del cliente al pulsar Enter
			$no_cliente.keypress(function(e){
				if(e.which == 13){
					
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoClient.json';
					$arreglo2 = {'no_control':$no_cliente.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					
					$.post(input_json2,$arreglo2,function(entry2){
						
						if(parseInt(entry2['Cliente'].length) > 0 ){
							var id_cliente = entry2['Cliente'][0]['id'];
							var nocliente = entry2['Cliente'][0]['numero_control'];
							var razoncliente = entry2['Cliente'][0]['razon_social'];
							var empresa_immex = entry2['Cliente'][0]['empresa_immex'];
							var tasa_ret_immex = entry2['Cliente'][0]['tasa_ret_immex'];
							var id_moneda = entry2['Cliente'][0]['moneda_id'];
							var id_vendedor = entry2['Cliente'][0]['cxc_agen_id'];
							
							//llamada a funcion que aguega datos del Cliente
							$agregarDatosClienteSeleccionado($select_moneda, $select_vendedor, entry['Monedas'], entry['Vendedores'], id_cliente, nocliente, razoncliente, empresa_immex, tasa_ret_immex, id_moneda, id_vendedor);
							
						}else{
							//limpiar campos del cliente
							$('#forma-notascredito-window').find('input[name=id_cliente]').val(0);
							$('#forma-notascredito-window').find('input[name=nocliente]').val('');
							$('#forma-notascredito-window').find('input[name=razoncliente]').val('');
							$('#forma-notascredito-window').find('input[name=empresa_immex]').val('');
							$('#forma-notascredito-window').find('input[name=tasa_ret_immex]').val(0);
							
							jAlert('N&uacute;mero de cliente desconocido.', 'Atencion!', function(r) { 
								$('#forma-notascredito-window').find('input[name=nocliente]').focus(); 
							});
						}
					},"json");//termina llamada json
					
					return false;
				}
			});
			
			
			
			//buscar y agregar datos de la Factura al pulsar Enter
			$factura.keypress(function(e){
				if(e.which == 13){
					if ($no_cliente.val()!=''){
						var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosFactura.json';
						$arreglo2 = {
									'id_cliente':$id_cliente.val(),
									'serie_folio':$factura.val(),  
									'iu':$('#lienzo_recalculable').find('input[name=iu]').val() 
								};
						
						$.post(input_json2,$arreglo2,function(entry2){
							
							if(parseInt(entry2['Factura'].length) > 0 ){
								var factura = entry2['Factura'][0]['factura'];
								var id_moneda = entry2['Factura'][0]['moneda_id'];
								var id_agente = entry2['Factura'][0]['cxc_agen_id'];
								var monto_factura = entry2['Factura'][0]['monto_factura'];
								var saldo_factura = entry2['Factura'][0]['saldo_factura'];
								var fecha_factura = entry2['Factura'][0]['fecha_factura'];
								
								//llamada a la funcion para agregar datos de la factura
								$agregarDatosFacturaSeleccionada($select_moneda,$select_vendedor, entry['Monedas'], entry['Vendedores'], $factura, $fecha_factura, $monto_factura, $aplicado, $saldo, factura, id_moneda, id_agente, monto_factura, saldo_factura, fecha_factura);
								
							}else{
								//limpiar campos de datos de la Factura
								$factura.val('');
								$fecha_factura.val('');
								$monto_factura.val("0.00");
								$aplicado.val("0.00");
								$saldo.val("0.00");
								$id_moneda_factura.val(0);
								
								jAlert('N&uacute;mero de Factura desconocido.', 'Atencion!', function(r) { 
									$factura.focus(); 
								});
							}
						},"json");//termina llamada json
					}else{
						jAlert('Es necesario seleccionar un Cliente.', 'Atencion!', function(r) { 
							$no_cliente.focus(); 
						});
					}
					return false;
				}
			});
			
		},"json");//termina llamada json
		
		
		
		//elimina cero al hacer clic sobre el campo
		$importe.focus(function(e){
			if ($razon_cliente.val()!=''){
				if(parseFloat($importe.val())<1){
					$importe.val('');
				}
			}else{
				jAlert("Es necesario seleccionar un Cliente antes de ingresar el Importe", 'Atencion!');
			}
		});
		
		
		
		$importe.blur(function(){
			if($importe.val()=="" || parseFloat($importe.val())==0){
				$importe.val(parseFloat(0.00).toFixed(2));//si el campo esta en blanco, pone cero
			}
			var evaluar="true";
			$calcula_total_nota_credito($importe, $ieps, $impuesto, $retencion, $total, $valor_impuesto, $tasa_ret_immex, $saldo,$empresa_immex,$chkbox_aplicar_saldo,$fac_saldado,evaluar );
		});
		
		
		
		
		
		$chkbox_aplicar_saldo.bind('click',function(event){
			var saldo_fac=parseFloat(quitar_comas($saldo.val()));
			var SubTotal = 0;
			if( parseFloat(saldo_fac) >0 ){
				if($(this).is(':checked')){
					if( $empresa_immex.val() == 'true' ){
						SubTotal = saldo_fac;
					}else{
						SubTotal = saldo_fac / ( parseFloat($valor_impuesto.val()) + 1 );
					}
					$importe.val(SubTotal);
				}else{
					$importe.val(SubTotal);
				}
				var evaluar="true";//esta variable es para decidir  si va a evaluar que el monto de la nota de credito no sea mayor que en Saldo de la factura
				$calcula_total_nota_credito($importe, $ieps, $impuesto, $retencion, $total, $valor_impuesto, $tasa_ret_immex, $saldo,$empresa_immex, $chkbox_aplicar_saldo,$fac_saldado,evaluar );
			}else{
				this.checked = false;
			}
			
		});
		
		
		
		
		
		/*
		$generar_nota_credito.click(function(event){
			$generar.val('true');
			if( parseFloat($importe.val()) <= parseFloat(quitar_comas($saldo.val()))){
				jConfirm('Confirmar creacion de Nota de Credito?', 'Dialogo de Confirmacion', function(r) {
					// If they confirmed, manually trigger a form submission
					if (r) {
						$submit_actualizar.trigger('click');
					}else{
						//aqui no hay nada
						$generar.val('false');
					}
				});
			}else{
				jAlert("El importe es mayor que el saldo de la factura. No es posible crear la Nota de Credito", 'Atencion!');
			}
		});
		*/
		
		
		
		//asignar el enfoque al cargar la ventana
		$no_cliente.focus();
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-notascredito-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-notascredito-overlay').fadeOut(remove);
		});
		
	});//termina nueva prefactura
	
	
	
	
	
	
	//Dibuja modal de cancelacion
	var modal_cancelar= function(id_to_show, $cancelar_nota_credito, $descargar_pdf, $descargar_xml, tmov_id, motivo, cancelado, iu){
		$(this).modalPanel_cancelaemision();
		var form_to_show = 'formaCancelaEmision';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		$('#forma-cancelaemision-window').css({"margin-left": -100,"margin-top": -180});
		$forma_selected.prependTo('#forma-cancelaemision-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		var $select_tmov = $('#forma-cancelaemision-window').find('select[name=select_tmov]');
		var $motivo_cancelacion = $('#forma-cancelaemision-window').find('textarea[name=motivo_cancel]');
		var $boton_cancelacion = $('#forma-cancelaemision-window').find('#boton_cancelacion');
		var $boton_salir_cancelacion = $('#forma-cancelaemision-window').find('#boton_salir_cancelacion');
		
		$motivo_cancelacion.val(motivo);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataCancel.json';
		var $arreglo = {'identificador':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		
		$.post(input_json,$arreglo,function(entry){
			$select_tmov.children().remove();
			var tmov_hmtl = '<option value="0">[--- ---]</option>';
			if(entry['TMov']){
				if(parseInt(tmov_id)>0){
					tmov_hmtl='';
				}else{
					/*
					if(!cancelado){
						tmov_hmtl='';
					}
					*/
				}
				$.each(entry['TMov'],function(entryIndex,mov){
					if(parseInt(mov['id'])==parseInt(tmov_id)){
						tmov_hmtl += '<option value="'+ mov['id'] +'" selected="yes">'+ mov['titulo'] + '</option>';
					}else{
						if(!cancelado){
							tmov_hmtl += '<option value="'+ mov['id'] +'">'+ mov['titulo'] + '</option>';
						}
					}
				});
			}
			$select_tmov.append(tmov_hmtl);
		});
		
		
		//Cancelar
		$boton_cancelacion.click(function(event){
			event.preventDefault();
			
			//if(parseInt($select_tmov.val())>0){
				if($motivo_cancelacion.val()!=null && $motivo_cancelacion.val()!=""){
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/cancelarNotaCredito.json';
					var $arreglo = {
                                            'id_nota': id_to_show,
                                            'tmov': $select_tmov.val(),
                                            'motivo': $motivo_cancelacion.val()
                                            /*,iu: ???*/
                                        };
					
					$.post(input_json,$arreglo,function(entry){
						var cad = entry['success'].split(":");
						
						jAlert(entry['msj'], 'Atencion!');
						
						if(entry['valor']=='true'){
							$descargar_pdf.attr('disabled','-1');
							$descargar_xml.attr('disabled','-1');
							$cancelar_nota_credito.attr('disabled','-1');
							
							//$get_datos_grid();
							var remove = function() {$(this).remove();};
							$('#forma-cancelaemision-overlay').fadeOut(remove);
						}
					});//termina llamada json
				}else{
					jAlert("Es necesario ingresar el Motivo de la cancelaci&oacute;n", 'Atencion!');
				}
			/*
			}else{
				jAlert("Es necesario seleccionar el Tipo de Movimiento.", 'Atencion!');
			}
			*/
		});
		
		
		$boton_salir_cancelacion.click(function(event){
			event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-cancelaemision-overlay').fadeOut(remove);
		});
	}
	
	
	
	
	
	
	var carga_formanotascredito00_for_datagrid00 = function(id_to_show, accion_mode){
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
			$('#forma-notascredito-window').remove();
			$('#forma-notascredito-overlay').remove();
            
			var form_to_show = 'formanotascredito00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_notascredito();
			
			$('#forma-notascredito-window').css({"margin-left": -340, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-notascredito-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getNotaCredito.json';
				$arreglo = {'id_nota_credito':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
							
				var $identificador = $('#forma-notascredito-window').find('input[name=identificador]');
				var $folio_nota_credito = $('#forma-notascredito-window').find('input[name=folio_nota_credito]');
				var $fecha_exp = $('#forma-notascredito-window').find('input[name=fecha_exp]');
				var $generar = $('#forma-notascredito-window').find('input[name=generar]');
				var $fac_saldado = $('#forma-notascredito-window').find('input[name=fac_saldado]');
				
				var $busca_cliente = $('#forma-notascredito-window').find('a[href*=busca_cliente]');
				var $id_cliente = $('#forma-notascredito-window').find('input[name=id_cliente]');
				var $no_cliente = $('#forma-notascredito-window').find('input[name=nocliente]');
				var $razon_cliente = $('#forma-notascredito-window').find('input[name=razoncliente]');
				var $empresa_immex = $('#forma-notascredito-window').find('input[name=empresa_immex]');
				var $tasa_ret_immex = $('#forma-notascredito-window').find('input[name=tasa_ret_immex]');
				
				var $id_impuesto = $('#forma-notascredito-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-notascredito-window').find('input[name=valorimpuesto]');
				var $observaciones = $('#forma-notascredito-window').find('textarea[name=observaciones]');
				var $select_vendedor = $('#forma-notascredito-window').find('select[name=select_vendedor]');
				var $select_moneda = $('#forma-notascredito-window').find('select[name=select_moneda]');
				var $tipo_cambio = $('#forma-notascredito-window').find('input[name=tipo_cambio]');
				var $concepto = $('#forma-notascredito-window').find('textarea[name=concepto]');
				var $select_tmov = $('#forma-notascredito-window').find('select[name=select_tmov]');
				
				var $importe = $('#forma-notascredito-window').find('input[name=importe]');
				var $ieps = $('#forma-notascredito-window').find('input[name=ieps]');
				var $impuesto = $('#forma-notascredito-window').find('input[name=impuesto]');
				var $retencion = $('#forma-notascredito-window').find('input[name=retencion]');
				var $total = $('#forma-notascredito-window').find('input[name=total]');
				var $busca_factura = $('#forma-notascredito-window').find('a[href*=busca_factura]');
				var $factura = $('#forma-notascredito-window').find('input[name=factura]');
				var $id_moneda_factura = $('#forma-notascredito-window').find('input[name=id_moneda_factura]');
				var $fecha_factura = $('#forma-notascredito-window').find('input[name=fecha_factura]');
				var $monto_factura = $('#forma-notascredito-window').find('input[name=monto_factura]');
				var $aplicado = $('#forma-notascredito-window').find('input[name=aplicado]');
				var $saldo = $('#forma-notascredito-window').find('input[name=saldo]');
				var $chkbox_aplicar_saldo = $('#forma-notascredito-window').find('input[name=chkbox_aplicar_saldo]');
				var $generar_nota_credito = $('#forma-notascredito-window').find('#generar_nota_credito');
				var $cancelar_nota_credito = $('#forma-notascredito-window').find('#cancelar_nota_credito');
				var $descargar_pdf = $('#forma-notascredito-window').find('#descargar_pdf');
				var $descargar_xml = $('#forma-notascredito-window').find('#descargar_xml');
				var $cancelado = $('#forma-notascredito-window').find('input[name=cancelado]');
				
				var $cerrar_plugin = $('#forma-notascredito-window').find('#close');
				var $cancelar_plugin = $('#forma-notascredito-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-notascredito-window').find('#submit');
				
				$folio_nota_credito.css({'background' : '#F0F0F0'});
				$no_cliente.css({'background' : '#F0F0F0'});
				$razon_cliente.css({'background' : '#F0F0F0'});
				$factura.css({'background' : '#F0F0F0'});
				$fecha_factura.css({'background' : '#F0F0F0'});
				$monto_factura.css({'background' : '#F0F0F0'});
				$aplicado.css({'background' : '#F0F0F0'});
				$saldo.css({'background' : '#F0F0F0'});
				
				$chkbox_aplicar_saldo.attr('disabled','-1');
				$generar.val('false');
				$fac_saldado.val('false');
				$busca_cliente.hide();
				$busca_factura.hide();
				$permitir_solo_numeros($tipo_cambio);
				$permitir_solo_numeros($importe);
				$cancelado.hide();
				
				//quitar enter a todos los campos input
				$('#forma-notascredito-window').find('input').keypress(function(e){
					if(e.which==13 ) {
						return false;
					}
				});
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-notascredito-window').find('div.interrogacion').css({'display':'none'});
						
						jAlert(data['msj'], 'Atencion!');
						
						if ( data['valor'] == "true" ){
							$get_datos_grid();
							var remove = function() {$(this).remove();};
							$('#forma-notascredito-overlay').fadeOut(remove);
						}
						
						
						/*
						if( $generar.val() == 'true' ){
							jAlert("Se gener&oacute; la Nota de Cr&eacute;dito: "+data['folio'], 'Atencion!');
						}else{
							jAlert("Los datos de la Nota de Cr&eacute;dito se han guardado con &eacute;xito", 'Atencion!');
						}
						*/
						
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-notascredito-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-notascredito-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
				
				//Aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.val(entry['Datos'][0]['id']);
					$folio_nota_credito.val(entry['Datos'][0]['serie_folio']);
					$fecha_exp.val(entry['Datos'][0]['fecha_exp']);
					$id_cliente.val(entry['Datos'][0]['cxc_clie_id']);
					$no_cliente.val(entry['Datos'][0]['no_cliente']);
					$razon_cliente.val(entry['Datos'][0]['razon_social']);
					$empresa_immex.val(entry['Datos'][0]['empresa_immex']);
					$tasa_ret_immex.val(entry['Datos'][0]['tasa_retencion_immex']);
					//$id_impuesto.val(entry['Datos'][0]['']);
					$id_impuesto.val(entry['iva'][0]['id_impuesto']);
					$valor_impuesto.val(entry['Datos'][0]['valor_impuesto']);
					$observaciones.text(entry['Datos'][0]['observaciones']);
					$tipo_cambio.val(entry['Datos'][0]['tipo_cambio']);
					$concepto.text(entry['Datos'][0]['concepto']);
					
					$importe.val(entry['Datos'][0]['importe']);
					
					if($folio_nota_credito.val().trim()!=''){
						$ieps.val(entry['Datos'][0]['monto_ieps']);
						$impuesto.val(entry['Datos'][0]['importe_iva']);
						$retencion.val(entry['Datos'][0]['importe_retencion']);
						$total.val(entry['Datos'][0]['monto_total']);
					}
					
					var monto_aplicado = parseFloat(entry['Datos'][0]['monto_factura']) - parseFloat(entry['Datos'][0]['saldo_factura']);
					
					$factura.val(entry['Datos'][0]['factura']);
					$id_moneda_factura.val(entry['Datos'][0]['id_moneda_factura']);
					$fecha_factura.val(entry['Datos'][0]['fecha_factura']);
					$monto_factura.val(entry['Datos'][0]['monto_factura']);
					$aplicado.val(parseFloat(monto_aplicado).toFixed(2));
					$saldo.val(entry['Datos'][0]['saldo_factura']);
					
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['Datos'][0]['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//if(parseInt(flujo_proceso)==2){
							//	moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							//}
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					//Carga select de vendedores
					$select_vendedor.children().remove();
					var hmtl_vendedor;
					$.each(entry['Vendedores'],function(entryIndex,vendedor){
						if(entry['Datos'][0]['cxc_agen_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_vendedor'] + '</option>';
						}else{
							/*
							if(parseInt(flujo_proceso)==2){
								hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_vendedor'] + '</option>';
							}
							*/
						}
					});
					$select_vendedor.append(hmtl_vendedor);
					
					$select_tmov.children().remove();
					var tmov_hmtl = '<option value="0">[--- ---]</option>';
					if(entry['TMov']){
						if(parseInt(entry['Datos'][0]['tmov_id'])>0){
							tmov_hmtl='';
						}else{
							/*
							if(!entry['Datos'][0]['cancelado']){
								tmov_hmtl='';
							}
							*/
						}
						$.each(entry['TMov'],function(entryIndex,mov){
							if(parseInt(mov['id'])==parseInt(entry['Datos'][0]['tmov_id'])){
								tmov_hmtl += '<option value="'+ mov['id'] +'" selected="yes">'+ mov['titulo'] + '</option>';
							}else{
								if(!entry['Datos'][0]['cancelado']){
									tmov_hmtl += '<option value="'+ mov['id'] +'">'+ mov['titulo'] + '</option>';
								}
							}
						});
					}
					$select_tmov.append(tmov_hmtl);
					
					var nota_nota_cancelada = entry['Datos'][0]['cancelado'];
					
					if (nota_nota_cancelada){
						//Aqui hay que desahabilitar campos
						$cancelado.show();
						$folio_nota_credito.attr('disabled','-1');
						$no_cliente.attr('disabled','-1');
						$razon_cliente.attr('disabled','-1');
						$observaciones.attr('disabled','-1');
						$select_vendedor.attr('disabled','-1');
						$select_moneda.attr('disabled','-1');
						$tipo_cambio.attr('disabled','-1');
						$concepto.attr('disabled','-1');
						$importe.attr('disabled','-1');
						$impuesto.attr('disabled','-1');
						$ieps.attr('disabled','-1');
						$retencion.attr('disabled','-1');
						$total.attr('disabled','-1');
						$factura.attr('disabled','-1');
						$fecha_factura.attr('disabled','-1');
						$monto_factura.attr('disabled','-1');
						$aplicado.attr('disabled','-1');
						$saldo.attr('disabled','-1');
						
						$importe.attr("readonly", true);
						$tipo_cambio.attr("readonly", true);
						$concepto.attr("readonly", true);
						$observaciones.attr("readonly", true);
						
						$chkbox_aplicar_saldo.attr('disabled','-1');
						
						$generar_nota_credito.attr('disabled','-1');
						$cancelar_nota_credito.attr('disabled','-1');
						$descargar_pdf.attr('disabled','-1');
						$descargar_xml.attr('disabled','-1');
						$submit_actualizar.hide();
					}
					
					if ( entry['Datos'][0]['serie_folio'] != ''){
						//aqui hay que desahabilitar campos
						if(!$generar_nota_credito.is(':disabled')) {
							$generar_nota_credito.attr('disabled','-1');
							$importe.attr("readonly", true);
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
					
					//Esta variable es para decidir  si va a evaluar que el monto de la nota de credito no sea mayor que en Saldo de la factura
					var evaluar="false";
					if(entry['Datos'][0]['serie_folio'].trim()=='' && !nota_nota_cancelada){
						evaluar="true";
					}
					
					if($folio_nota_credito.val().trim()==''){
						//Llamada a la funcion que calcula el total
						$calcula_total_nota_credito($importe, $ieps, $impuesto, $retencion, $total, $valor_impuesto, $tasa_ret_immex, $saldo,$empresa_immex,$chkbox_aplicar_saldo, $fac_saldado,evaluar );
					}
					
					//Cancelar Nota de Credito
					$cancelar_nota_credito.click(function(event){
						event.preventDefault();
						
						modal_cancelar($identificador.val(), $cancelar_nota_credito, $descargar_pdf, $descargar_xml, entry['Datos'][0]['tmov_id_cancel'], entry['Datos'][0]['motivo_cancel'], entry['Datos'][0]['cancelado'], $('#lienzo_recalculable').find('input[name=iu]').val());
						
					});//termina cancelar factura
					
				});//termina llamada json
                
                
				//elimina cero al hacer clic sobre el campo
				$importe.focus(function(e){
					if ($razon_cliente.val()!=''){
						if(parseFloat($importe.val())<1){
							$importe.val('');
						}
					}else{
						jAlert("Es necesario seleccionar un Cliente antes de ingresar el Importe", 'Atencion!');
					}
				});
				
				
				$importe.blur(function(){
					if($importe.val()=="" || parseFloat($importe.val())==0){
						$importe.val(parseFloat(0.00).toFixed(2));//si el campo esta en blanco, pone cero
					}
					var evaluar="true";
					$calcula_total_nota_credito($importe, $ieps, $impuesto, $retencion, $total, $valor_impuesto, $tasa_ret_immex, $saldo,$empresa_immex,$chkbox_aplicar_saldo, $fac_saldado,evaluar );
				});
				
				
				$chkbox_aplicar_saldo.bind('click',function(event){
					var saldo_fac=parseFloat(quitar_comas($saldo.val()));
					var SubTotal = 0;
					if( parseFloat(saldo_fac) >0 ){
						if($(this).is(':checked')){
							if( $empresa_immex.val() == 'true' ){
								SubTotal = saldo_fac;
							}else{
								SubTotal = parseFloat(saldo_fac) / ( parseFloat($valor_impuesto.val()) + 1 );
							}
							$importe.val(SubTotal);
						}else{
							$importe.val(SubTotal);
						}
						var evaluar="true";
						$calcula_total_nota_credito($importe, $ieps, $impuesto, $retencion, $total, $valor_impuesto, $tasa_ret_immex, $saldo,$empresa_immex, $chkbox_aplicar_saldo, $fac_saldado,evaluar );
					}else{
						this.checked = false;
					}
				});
				
				
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
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getVerificaArchivoGenerado.json';
					$arreglo = {'serie_folio':$folio_nota_credito.val(), 'ext':'pdf', 'id':$identificador.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
					
					$.post(input_json,$arreglo,function(entry){
						var descargar  = entry['descargar'];
						if(descargar == 'true'){
							var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
							var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getDescargarPdfNotaCredito/'+$identificador.val()+'/'+iu+'/out.json';
							window.location.href=input_json;
						}else{
							//jAlert("La factura "+$serie_folio.val()+" aun no esta disponible para descarga, intente nuevamente en 10 segundos.", 'Atencion!');
							jAlert("El archivo "+$folio_nota_credito.val()+".pdf  no esta disponible para descarga.", 'Atencion!');
						}
					});//termina llamada json
				});
                
                
                
				//descargar xml de la Nota de Credito
				$descargar_xml.click(function(event){
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getVerificaArchivoGenerado.json';
					$arreglo = {'serie_folio':$folio_nota_credito.val(), 'ext':'xml', 'id':$identificador.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
					$.post(input_json,$arreglo,function(entry){
						var descargar  = entry['descargar'];
						if(descargar == 'true'){
							var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
							var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getDescargarXmlNotaCredito/'+$identificador.val()+'/'+iu+'/out.json';
							window.location.href=input_json;
						}else{
							//jAlert("La factura "+$serie_folio.val()+" aun no esta disponible para descarga, intente nuevamente en 10 segundos.", 'Atencion!');
							jAlert("El archivo "+$folio_nota_credito.val()+".xml  no esta disponible para descarga.", 'Atencion!');
						}
					});//termina llamada json
				});
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-notascredito-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-notascredito-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllNotasCredito.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        var $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllNotasCredito.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formanotascredito00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



