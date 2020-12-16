$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select tipo de documento
    var array_select_documento = {
				1:"Factura", 
				2:"Remisi&oacute;n",
				3:"Factura&nbsp;de&nbsp;Remisi&oacute;n"
			};
	var arrayAgentes;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/prefacturas";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    
    var $new_prefactura = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//$new_prefactura.hide();
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Facturaci&oacute;n');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	var $cadena_busqueda = "";
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	var $busqueda_select_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_agente]');
	var $busqueda_folio_pedido = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio_pedido]');
	
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
		valor_retorno += "folio_pedido" + signo_separador + $busqueda_folio_pedido.val() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val() + "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "producto" + signo_separador + $busqueda_producto.val() + "|";
		valor_retorno += "agente" + signo_separador + $busqueda_select_agente.val();
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
	
	
	var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
		//Alimentando los campos select_agente
		$busqueda_select_agente.children().remove();
		var agente_hmtl = '<option value="0">[-Seleccionar Agente-]</option>';
		$.each(data['Agentes'],function(entryIndex,agente){
			agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_vendedor'] + '</option>';
		});
		$busqueda_select_agente.append(agente_hmtl);
		
		//asignamos el arreglo a una variable para utilizarla mas adelante
		arrayAgentes = data['Agentes'];
	});
	
	
	
	$limpiar.click(function(event){
		$busqueda_folio_pedido.val('');
		$busqueda_cliente.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_codigo.val('');
		$busqueda_producto.val('');
		
		//Recargar select de agentes
		$busqueda_select_agente.children().remove();
		var agente_hmtl = '<option value="0">[-Seleccionar Agente-]</option>';
		$.each(arrayAgentes,function(entryIndex,agente){
			agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_vendedor'] + '</option>';
		});
		$busqueda_select_agente.append(agente_hmtl);
		
		//asignar el enfoque al limpiar campos
		$busqueda_folio_pedido.focus();
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
		//asignar el enfoque al visualizar el buscador
		$busqueda_folio_pedido.focus();
	});
	
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio_pedido, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_producto, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_agente, $buscar);
	
	
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
		var $select_prod_tipo = $('#forma-prefacturas-window').find('select[name=prodtipo]');
		$('#forma-prefacturas-window').find('#submit').mouseover(function(){
			$('#forma-prefacturas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-prefacturas-window').find('#submit').mouseout(function(){
			$('#forma-prefacturas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-prefacturas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-prefacturas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-prefacturas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-prefacturas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-prefacturas-window').find('#close').mouseover(function(){
			$('#forma-prefacturas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-prefacturas-window').find('#close').mouseout(function(){
			$('#forma-prefacturas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-prefacturas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-prefacturas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-prefacturas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-prefacturas-window').find("ul.pestanas li").click(function() {
			$('#forma-prefacturas-window').find(".contenidoPes").hide();
			$('#forma-prefacturas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-prefacturas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	$tabs_li_funxionalidad_datos_adenda = function(){
		$('#forma-datosadenda-window').find('#submit').mouseover(function(){
			$('#forma-datosadenda-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-datosadenda-window').find('#submit').mouseout(function(){
			$('#forma-datosadenda-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-datosadenda-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-datosadenda-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-datosadenda-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-datosadenda-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-datosadenda-window').find('#close').mouseover(function(){
			$('#forma-datosadenda-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-datosadenda-window').find('#close').mouseout(function(){
			$('#forma-datosadenda-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-datosadenda-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-datosadenda-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-datosadenda-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-datosadenda-window').find("ul.pestanas li").click(function() {
			$('#forma-datosadenda-window').find(".contenidoPes").hide();
			$('#forma-datosadenda-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-datosadenda-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	//carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, elemento_seleccionado, mostrar_opciones){
		$campo_select.children().remove();
		var select_html = '';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				if (mostrar_opciones=='true'){
					//3=Facturacion de Remisiones, solo debe mostrarse cuando se abra la ventana desde el Icono de Nuevo
					if(parseInt(i)!=3 ){
						select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
					}
				}
			}
		}
		$campo_select.append(select_html);
	}
	
	
	//funcion para agregar datos del cliente en la ventana de la prefactura
	$agregarDatosClienteSeleccionado = function($select_moneda, $select_condiciones, $select_vendedor, array_monedas, array_condiciones, array_vendedores, id_moneda, id_termino, id_vendedor, id_cliente, no_control_cliente, razon_social_cliente, empresa_immex, tasa_immex, cuenta_mn, cuenta_usd, dir_cliente, rfc_cliente){
		
		//asignar a los campos correspondientes el sku y y descripcion
		$('#forma-prefacturas-window').find('input[name=id_cliente]').val(id_cliente);
		$('#forma-prefacturas-window').find('input[name=nocliente]').val(no_control_cliente);
		$('#forma-prefacturas-window').find('input[name=razoncliente]').val(razon_social_cliente);
		$('#forma-prefacturas-window').find('input[name=empresa_immex]').val(empresa_immex);
		$('#forma-prefacturas-window').find('input[name=tasa_ret_immex]').val(tasa_immex);
		$('#forma-prefacturas-window').find('input[name=cta_mn]').val(cuenta_mn);
		$('#forma-prefacturas-window').find('input[name=cta_usd]').val(cuenta_usd);
		$('#forma-prefacturas-window').find('input[name=dircliente]').val(dir_cliente);
		$('#forma-prefacturas-window').find('input[name=rfc]').val(rfc_cliente);
		
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
		
		//carga select de condiciones con los dias de Credito default del Cliente
		$select_condiciones.children().remove();
		var hmtl_condiciones= '';
		$.each(array_condiciones, function(entryIndex,condicion){
			if( parseInt(condicion['id']) == parseInt(id_termino) ){
				hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes">' + condicion['descripcion'] + '</option>';
			}else{
				//hmtl_condiciones += '<option value="' + condicion['id'] + '" >' + condicion['descripcion'] + '</option>';
			}
		});
		if(hmtl_condiciones == ''){
			hmtl_condiciones = '<option value="0">[-Seleccionar Termino-]</option>';
		}
		$select_condiciones.append(hmtl_condiciones);
		
		//carga select de vendedores
		$select_vendedor.children().remove();
		var hmtl_vendedor= '';
		$.each(array_vendedores,function(entryIndex,vendedor){
			if( parseInt(vendedor['id']) == parseInt(id_vendedor) ){
				hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes">' + vendedor['nombre_vendedor'] + '</option>';
			}else{
				//hmtl_vendedor += '<option value="' + vendedor['id'] + '" >' + vendedor['nombre_agente'] + '</option>';
			}
		});
		if(hmtl_vendedor == ''){
			hmtl_vendedor = '<option value="0">[-Seleccionar Agente-]</option>';
		}
		$select_vendedor.append(hmtl_vendedor);
	}
	
	
	
	
	//buscador de clientes
	$busca_clientes = function($select_moneda,$select_condiciones,$select_vendedor, array_monedas, array_condiciones, array_vendedores, $razon_cliente, $no_cliente ){
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
            
            if($no_cliente.val() !='' && $razon_cliente.val()==''){
				html+='<option value="1" selected="yes">No. de control</option>';
				$cadena_buscar.val($no_cliente.val());
			}else{
				html+='<option value="1">No. de control</option>';
			}
            html+='<option value="2">RFC</option>';
            if($razon_cliente.val()!=''){
				$cadena_buscar.val($razon_cliente.val());
				html+='<option value="3" selected="yes">Razon social</option>';
			}
            if($no_cliente.val() =='' && $razon_cliente.val()==''){
				html+='<option value="3" selected="yes">Razon social</option>';
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
						var id_moneda=$(this).find('#id_moneda').val();
						var id_termino=$(this).find('#terminos_id').val();
						var id_vendedor=$(this).find('#vendedor_id').val();
                        var id_cliente = $(this).find('#idclient').val();
                        var no_control_cliente = $(this).find('span.no_control').html();
                        var razon_social_cliente = $(this).find('span.razon').html();
						var empresa_immex = $(this).find('#emp_immex').val();
						var tasa_immex = $(this).find('#tasa_immex').val();
						var cuenta_mn = $(this).find('#cta_mn').val();
						var cuenta_usd = $(this).find('#cta_usd').val();
						var dir_cliente = $(this).find('#direccion').val();
						
						var rfc_cliente = $(this).find('span.rfc').html();
						
						//llamada a la funcion que agrega datos del cliente a la ventana de la prefactura
                        $agregarDatosClienteSeleccionado($select_moneda, $select_condiciones, $select_vendedor, array_monedas, array_condiciones, array_vendedores, id_moneda, id_termino, id_vendedor, id_cliente, no_control_cliente, razon_social_cliente, empresa_immex, tasa_immex, cuenta_mn, cuenta_usd, dir_cliente, rfc_cliente);
                        
                        //elimina la ventana de busqueda
                        var remove = function() {$(this).remove();};
                        $('#forma-buscacliente-overlay').fadeOut(remove);
                        
                        //asignar el enfoque al campo Razon Social del Cliente
                        $razon_cliente.focus();
                    });
                });
            });//termina llamada json
			
			
			//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda al cargar el plugin
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
                $razon_cliente.focus();
            });
	}//termina buscador de clientes
	
	
	
	
	
	//buscador de productos
	$busca_productos = function(sku_buscar){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscaproducto-window').find('#tabla_resultado');
		
		var $campo_sku = $('#forma-buscaproducto-window').find('input[name=campo_sku]');
		var $select_tipo_producto = $('#forma-buscaproducto-window').find('select[name=tipo_producto]');
		var $campo_descripcion = $('#forma-buscaproducto-window').find('input[name=campo_descripcion]');
		
		//var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('a[href*=busca_producto_modalbox]');
		//var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('a[href*=cencela]');
		var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('#busca_producto_modalbox');
		var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('#cencela');
		
		//funcionalidad botones
		$buscar_plugin_producto.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_producto.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_producto.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_producto.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});

		//buscar todos los tipos de productos
		var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_tipos,$arreglo,function(data){
			//Llena el select tipos de productos en el buscador
			$select_tipo_producto.children().remove();
			var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			$.each(data['prodTipos'],function(entryIndex,pt){
				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		
		//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
		$campo_sku.val(sku_buscar);
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos.json';
			$arreglo = {	'sku':$campo_sku.val(),
							'tipo':$select_tipo_producto.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['productos'],function(entryIndex,producto){
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
							trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
						trr += '</td>';
						trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
						trr += '<td width="90">';
							trr += '<span class="unidad_id" style="display:none;">'+producto['unidad_id']+'</span>';
							trr += '<span class="utitulo">'+producto['unidad']+'</span>';
						trr += '</td>';
						trr += '<td width="90"><span class="tipo_prod_buscador">'+producto['tipo']+'</span></td>';
					trr += '</tr>';
					$tabla_resultados.append(trr);
				});
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
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					//asignar a los campos correspondientes el sku y y descripcion
					$('#forma-prefacturas-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-prefacturas-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-prefacturas-window').find('input[name=sku_producto]').focus();
				});
				
			});//termina llamada json
		});
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_sku.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}
		
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaproducto-overlay').fadeOut(remove);
		});
	}//termina buscador de productos
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Carga datos de la remision seleccionada al Grid de Productos de la factura
	//Las remisiones a facturar deben tener la misma direccion fiscal, de otra manera no se permite agregar junto con otra remision
	$agrega_productos_remision_al_grid = function($grid_productos, $select_moneda,$select_metodo_pago, $folio_pedido, $orden_compra, $no_cuenta, id_remision, id_moneda_remision,  array_monedas, array_metodos_pago, id_alm, $check_incluye_adenda, $adenda, $agregarDatosAdenda, $campo1, $campo2, $campo3, $campo4, $campo5, $campo6, $campo7, $campo8 ){
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosRemision.json';
		$arreglo = {'id_remision':id_remision, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			var trCount = $("tr", $grid_productos).size();
			var valor_orden_compra = $orden_compra.val();
			var valor_folio_pedido = $folio_pedido.val();
			var valor_folio_remision = entry['Datos'][0]['folio_remision'];
			var id_dir_fiscal = entry['Datos'][0]['df_id'];
			var dir_fiscal = entry['Datos'][0]['direccion'];
			var cargar_datos=false;
			
			//verificar si el id de la direccion fiscal es igual a cero o  es igual a 1. Cualquiera de estos dos valosres significa que es la primera remision
			if( parseInt($('#forma-prefacturas-window').find('input[name=id_df]').val())==0 || parseInt($('#forma-prefacturas-window').find('input[name=id_df]').val())==1){
				cargar_datos=true;
				//solo se debe tomar la direccion fiscal de la primera remision
				//los siguientes ya no porque todos deben tener la misma direccion
				$('#forma-prefacturas-window').find('input[name=id_df]').val(id_dir_fiscal);
				$('#forma-prefacturas-window').find('input[name=dircliente]').val(dir_fiscal);
			}
			
			if( parseInt($('#forma-prefacturas-window').find('input[name=id_df]').val()) == parseInt(id_dir_fiscal) ){
				cargar_datos=true;
			}else{
				cargar_datos=false;
			}
			
			
			if(cargar_datos){
				
				if(parseInt(trCount) <= 0){
					//carga select denominacion con con la moneda de la primera remision seleccionada
					$select_moneda.children().remove();
					var moneda_hmtl = '';
					$.each(array_monedas,function(entryIndex,moneda){
						if(parseInt(moneda['id']) == parseInt(id_moneda_remision)){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
					$('#forma-prefacturas-window').find('input[name=moneda_original]').val(id_moneda_remision);
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo="";
					$.each(array_metodos_pago,function(entryIndex,metodo){
						if(parseInt(metodo['id']) == parseInt(entry['Datos']['0']['fac_metodos_pago_id'])){
							hmtl_metodo += '<option value="' + metodo['id'] + '" selected="yes">' + metodo['titulo'] + '</option>';
						}else{
							//hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					$no_cuenta.val(entry['Datos']['0']['no_cuenta']);
					
				}
				
				if( valor_orden_compra != null && valor_orden_compra != '' ){
					if( entry['Datos']['0']['orden_compra'] != null && entry['Datos']['0']['orden_compra'] !='' ){
						$orden_compra.val( valor_orden_compra + "," + entry['Datos']['0']['orden_compra']);
					}
				}else{
					$orden_compra.val(entry['Datos']['0']['orden_compra']);
				}
				
				if( valor_folio_pedido != null && valor_folio_pedido != '' ){
					if( entry['Datos']['0']['folio_pedido'] != null && entry['Datos']['0']['folio_pedido'] !='' ){
						$folio_pedido.val( valor_folio_pedido + "," + entry['Datos']['0']['folio_pedido']);
					}
				}else{
					$folio_pedido.val(entry['Datos']['0']['folio_pedido']);
				}
				
				$('#forma-prefacturas-window').find('input[name=pdescto]').val(entry['Datos'][0]['pdescto']);
				$('#forma-prefacturas-window').find('input[name=motivo_descuento]').val(entry['Datos'][0]['mdescto']);
				
				var importeIeps=0;
				var tasaIeps=0;
				var importePartida = 0;
				var importeImpuesto = 0;
				var precio_u_con_descto = 0;
				var valor_descto = 0;
				var importe_del_descuento = 0;
				var importe_con_descto = 0;
				var tasaRetencionIva=0;
				var importeRetencionIva = 0;
				
				if(entry['Conceptos'] != null){
					$.each(entry['Conceptos'],function(entryIndex,prod){
						//obtiene numero de trs
						var tr = $("tr", $grid_productos).size();
						tr++;
						importeIeps=0;
						tasaIeps=0;
						
						importePartida = 0;
						importeImpuesto = 0;
						precio_u_con_descto = 0;
						valor_descto = 0;
						importe_del_descuento = 0;
						importe_con_descto = 0;
						tasaRetencionIva = 0;
						importeRetencionIva = 0;
						
						//Redondear a 4 digitos el importe de la partida
						importePartida = parseFloat(prod['importe']).toFixed(4);
						
						if(parseFloat(prod['valor_ieps'])>0){
							tasaIeps = parseFloat(prod['valor_ieps'])/100;
						}
						
						if(parseFloat(prod['ret_tasa'])>0){
							tasaRetencionIva = parseFloat(prod['ret_tasa'])/100;
						}
						
						if($('#forma-prefacturas-window').find('input[name=pdescto]').val().trim()=='true' && parseFloat(prod['descto'])>0){
							valor_descto = prod['descto'];
							precio_u_con_descto = parseFloat( parseFloat(prod['precio_unitario']) - (parseFloat(prod['precio_unitario'])*(parseFloat(valor_descto)/100)) ).toFixed(4);
							
							importe_del_descuento = parseFloat(parseFloat(importePartida)*(parseFloat(valor_descto)/100)).toFixed(4);
							importe_con_descto = parseFloat(parseFloat(importePartida)-parseFloat(importe_del_descuento)).toFixed(4);
							
							importeIeps = parseFloat(parseFloat(importe_con_descto) * parseFloat(tasaIeps)).toFixed(4);
							importeImpuesto = (parseFloat(importe_con_descto) + parseFloat(importeIeps)) * parseFloat( prod['valor_imp'] );
							
							importeRetencionIva = parseFloat(parseFloat(importe_con_descto) * parseFloat(tasaRetencionIva)).toFixed(4);
						}else{
							importeIeps = parseFloat(parseFloat(importePartida) * parseFloat(tasaIeps)).toFixed(4);
							importeImpuesto = parseFloat(parseFloat(parseFloat(importePartida) + parseFloat(importeIeps)) * parseFloat(prod['valor_imp'])).toFixed(4);
							importeRetencionIva = parseFloat(parseFloat(importePartida) * parseFloat(tasaRetencionIva)).toFixed(4);
						}
						
						var trr = '';
						trr = '<tr>';
						trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
								trr += '<a href="elimina_producto" id="delete'+ tr +'"></a>';
								trr += '<input type="hidden" name="id_almacen" id="id_alm" value="'+id_alm+'">';//id del almacen de donde se saco el producto remisionado
								trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
								trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';//este es el id del registro que ocupa el producto en la tabla prefacturas_detalles
								trr += '<input type="hidden" name="id_remision" id="id_rem" value="'+ prod['id_remision'] +'">';//id de la  remision seleccionada
								trr += '<input type="text" 	name="remision" value="'+ valor_folio_remision +'" 	id="id_rem" class="borde_oculto" readOnly="true" style="width:60px;">';
								trr += '<input type="hidden" name="id_mon_rem" id="id_mon" value="'+ id_moneda_remision +'">';//id de la moneda de la remision
								trr += '<input type="hidden" name="costo_promedio" id="costprom" value="'+ prod['costo_prom'] +'">';
						trr += '</td>';
						trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
								trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['producto_id'] +'">';
								trr += '<input type="text" name="sku'+ tr +'" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
						trr += '</td>';
						trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
							trr += '<input type="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
						trr += '</td>';
						trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
							trr += '<input type="hidden" name="idUnidad" id="idUnidad" value="'+prod['unidad_id']+'">';
							trr += '<input type="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
						trr += '</td>';
						trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
								trr += '<input type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
								trr += '<input type="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
								//trr += '<select name="select_pres" class="selectPres'+ tr +'" style="width:96px;"></select>';
						trr += '</td>';
						
						trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
							trr += '<input type="text" 	name="cantPedido" value="'+  prod['cantidad'] +'" 	id="cantped" class="borde_oculto" style="width:76px;" readOnly="true">';
						trr += '</td>';
						
						trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
							trr += '<input type="text" 	name="cantFacturado" value="0" 	id="cantfac" class="borde_oculto" style="width:76px;" readOnly="true">';
						trr += '</td>';
						
						trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
							trr += '<input type="text" 	name="cantidad" value="'+  prod['cantidad'] +'" 	id="cant" class="cant'+ tr +'" style="width:76px;" readOnly="true">';
						trr += '</td>';
						
						trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
							trr += '<input type="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" class="borde_oculto" style="width:76px; text-align:right;" readOnly="true">';
							trr += '<input type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
							
							trr += '<input type="hidden" 	name="vdescto" id="vdescto" value="'+ valor_descto +'">';
							trr += '<input type="hidden" 	name="pu_descto" id="pu_descto" value="'+ precio_u_con_descto +'">';
						trr += '</td>';
						
						trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
							trr += '<input type="text" 	name="importe'+ tr +'" 	value="'+  importePartida +'" 	id="import" class="borde_oculto" style="width:86px; text-align:right;" readOnly="true">';
							trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+ importeImpuesto +'">';
							trr += '<input type="hidden"    name="id_imp_prod"  value="'+  prod['gral_imp_id'] +'" id="idimppord">';
							trr += '<input type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" 		id="ivalorimp">';
							
							trr += '<input type="hidden" name="importe_del_descto" id="importe_del_descto" value="'+ importe_del_descuento +'">';
							trr += '<input type="hidden" name="importe_con_descto" id="importe_con_descto" value="'+ importe_con_descto +'">';
							
							trr += '<input type="hidden" name="ret_id" 		id="ret_id" value="'+  prod['ret_id'] +'">';
							trr += '<input type="hidden" name="ret_tasa" 	id="ret_tasa" value="'+  prod['ret_tasa'] +'">';
							trr += '<input type="hidden" name="ret_importe" id="ret_importe" value="'+ importeRetencionIva +'">';
						trr += '</td>';
						
						trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="50">';
							trr += '<input type="hidden" name="idIeps"     value="'+ prod['ieps_id'] +'" id="idIeps">';
							trr += '<input type="text" name="tasaIeps" value="'+ prod['valor_ieps'] +'" class="borde_oculto" id="tasaIeps" style="width:46px; text-align:right;" readOnly="true">';
						trr += '</td>';
						
						trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="62">';
							trr += '<input type="text" name="importeIeps" value="'+ importeIeps +'" class="borde_oculto" id="importeIeps" style="width:58px; text-align:right;" readOnly="true">';
						trr += '</td>';
						
						trr += '</tr>';
						$grid_productos.append(trr);
						$grid_productos.find('a').hide();//ocultar
						
						
						
						
						/*
						//cargar select de presentaciones de cada producto
						$grid_productos.find('select.selectPres'+ tr).children().remove();
						var moneda_hmtl = '';
						$.each(entry['Pres'],function(entryIndex,pres){
							if(parseInt(prod['producto_id'])==parseInt(pres['producto_id'])){
								if(parseInt(prod['id_presentacion'])==parseInt(pres['presentacion_id'])){
									moneda_hmtl += '<option value="' + pres['presentacion_id'] + '" selected="yes">' + pres['presentacion'] + '</option>';
								}
							}
						});
						$grid_productos.find('select.selectPres'+ tr).append(moneda_hmtl);
						*/
					});
				}
				
				
				
				//***************************************************************************************************************
				//Aqui va el codigo que muestra los campos para adenda
				
				$check_incluye_adenda.click(function(event){
					//Esto es para evitar que le quiten la seleccion cuando incluye adenda.
					if(entry['RemExtra']['0']['adenda'] == 'true'){
						this.checked=true;
					}
				});
				
				
				
				
				
				
				//Ocultar check y boton de la adenda, cuando el cliente no incluya adenda.
				if(entry['RemExtra']['0']['adenda'] == 'true'){
					if(parseInt(entry['Datos']['0']['adenda_id'])==1){
						$adenda.show();
						$check_incluye_adenda.attr('checked',  (entry['RemExtra']['0']['adenda'] == 'true')? true:false );
						
						
						//Cargar datos
						//$campo1.val();//No. Entrada
						$campo2.val(valor_folio_remision);//No. Remision
						$campo3.val();//Consignacion
						//$campo4.val();//Centro Costos
						//$campo5.val();//Fecha Inicio
						//$campo6.val();//Fecha Fin
						$campo7.val($orden_compra.val());//Orden de Compra
						$campo8.val(entry['Datos'][0]['moneda2']);//Moneda
						
						
						//Asignar el evento click
						$agregarDatosAdenda.click(function(event){
							$cargaFormaDatosAdenda(entry['Datos']['0']['adenda_id'], $campo1, $campo2, $campo3, $campo4, $campo5, $campo6, $campo7, $campo8);
						});
					}
				}
				//***************************************************************************************************************
				
				
				
				$calcula_totales();//llamada a la funcion que calcula totales 
				
			}else{
				jAlert("Las remisiones deben tener la misma direcci&oacute;n fiscal.\nLa remisi&oacute;n "+valor_folio_remision+" tiene direcci&oacute;n fiscal diferente a la primera remisi&oacute;n seleccionada.", 'Atencion!');
			}
			
		});
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	//buscador de Remisiones  sin facturar del cliente seleccionado
	$busca_remisiones = function($grid_productos, $select_moneda,$select_metodo_pago, $folio_pedido, $orden_compra, $no_cuenta, id_cliente, array_monedas, array_metodos_pago, $select_almacen, arrayAlmacenes, $check_incluye_adenda, $adenda, $agregarDatosAdenda, $campo1, $campo2, $campo3, $campo4, $campo5, $campo6, $campo7, $campo8 ){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRemisionesCliente.json';
		$arreglo = {'id_cliente':id_cliente	};
		
		var trr = '';
		
		$.post(input_json,$arreglo,function(entry){
				
				//verifica si el arreglo  retorno datos
				if (entry['Remisiones'].length > 0){
					$(this).modalPanel_buscaremision();
					var $dialogoc =  $('#forma-buscaremision-window');
					$dialogoc.append($('div.buscador_remisiones').find('table.formaBusqueda_remisiones').clone());
					$('#forma-buscaremision-window').css({"margin-left": -110, "margin-top": -150});
					
					var $tabla_resultados = $('#forma-buscaremision-window').find('#tabla_resultado');
					//var $cancelar_plugin_busca_lotes_producto = $('#forma-buscaremision-window').find('a[href*=cencela]');
					var $cancelar_busca_remisiones = $('#forma-buscaremision-window').find('#cencela');
					$tabla_resultados.children().remove();
					
					$cancelar_busca_remisiones.mouseover(function(){
						$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
					});
					$cancelar_busca_remisiones.mouseout(function(){
						$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
					});
					
					
					//crea el tr con los datos del producto seleccionado
					$.each(entry['Remisiones'],function(entryIndex,rem){
						trr = '<tr>';
							trr += '<td width="100">';
								trr += '<span class="id_rem" style="display:none">'+rem['id']+'</span>';
								trr += '<span class="id_alm" style="display:none">'+rem['id_almacen']+'</span>';
								trr += '<span class="folio">'+rem['folio']+'</span>';
							trr += '</td>';
							trr += '<td width="100"><span>'+rem['monto_remision']+'</span></td>';
							trr += '<td width="90">';
								trr += '<span class="id_mon" style="display:none">'+rem['moneda_id']+'</span>';
								trr += rem['moneda'];
							trr += '</td>';
							trr += '<td width="90">'+rem['fecha_remision']+'</td>';
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
						//llamada a la funcion que busca los datos de la remision seleccionada y carga los datos en el grid de productos
						var id_rem = $(this).find('span.id_rem').html();
						var id_moneda = $(this).find('span.id_mon').html();
						var folio = $(this).find('span.folio').html();
						var id_alm = $(this).find('span.id_alm').html();
						
						var encontrado = 0;
						var moneda_diferente = 0;
						var almacen_diferente = 0;
						
						//busca el sku y la presentacion en el grid
						$grid_productos.find('tr').each(function (index){
							if(( $(this).find('#id_rem').val() == id_rem )){
								encontrado=1;//la remision ya se encuentra en el grid
							}
							
							if(( $(this).find('#id_mon').val() != id_moneda )){
								moneda_diferente=1;//la moneda es diferente a la que se encuentra en el grid
							}
							
							if(( $(this).find('#id_alm').val() != id_alm )){
								almacen_diferente=1;//el almacen es diferente a la que ya se encuentra en el grid
							}
						});
						
						if( parseInt(encontrado) != 1 ) {
							if( parseInt(moneda_diferente) != 1 ) {
								if( parseInt(almacen_diferente) != 1 ) {
									$agrega_productos_remision_al_grid($grid_productos, $select_moneda, $select_metodo_pago, $folio_pedido, $orden_compra, $no_cuenta, id_rem, id_moneda, array_monedas, array_metodos_pago, id_alm, $check_incluye_adenda, $adenda, $agregarDatosAdenda, $campo1, $campo2, $campo3, $campo4, $campo5, $campo6, $campo7, $campo8 );
									
									//carga select de almacen con el almacen de donde se saco los productos de la remision
									$select_almacen.children().remove();
									var hmtl_alm='';
									$.each(arrayAlmacenes,function(entryIndex,alm){
										if(id_alm == alm['id']){
											hmtl_alm += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
										}
									});
									$select_almacen.append(hmtl_alm);
									
								}else{
									jAlert("No se puede mezclar productos remisionados de diferentes Almacenes.",'! Atencion');
								}
							}else{
								jAlert("No se puede mezclar remisiones de diferentes monedas.",'! Atencion');
							}
						}else{
							jAlert("La remisi&oacute;n ya fue seleccionado. Intente agregar una diferente.",'! Atencion');
						}
						
						//$nombre_producto.val(titulo);//muestra el titulo del producto en el campo nombre del producto de la ventana de cotizaciones
						
						//elimina la ventana de busqueda
						var remove = function() {$(this).remove();};
						$('#forma-buscaremision-overlay').fadeOut(remove);
					});
					
					$cancelar_busca_remisiones.click(function(event){
						//event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-buscaremision-overlay').fadeOut(remove);
					});
				}else{
					jAlert("El cliente seleccionado no tiene Remisiones pendientes de Facturar.\nSeleccione un cliente diferente y haga click en Agregar Remisiones.",'! Atencion');
					$('#forma-prefacturas-window').find('input[name=titulo_producto]').val('');
				}
		});
	}//termina buscador de remisiones del cliente
	
    
	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_presentaciones_producto = function($id_cliente,no_cliente, sku_producto,$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio){
		//verifica si el campo rfc proveedor no esta vacio
		if(no_cliente != ''){
                    //verifica si el campo sku no esta vacio para realizar busqueda
                    if(sku_producto != ''){
                        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_presentaciones_producto.json';
                        $arreglo = {'sku':sku_producto,
									'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
									};

                        var trr = '';
						
                        $.post(input_json,$arreglo,function(entry){
                                //verifica si el arreglo  retorno datos
                                if (entry['Presentaciones'].length > 0){
                                    $(this).modalPanel_Buscapresentacion();
                                    var $dialogoc =  $('#forma-buscapresentacion-window');
                                    $dialogoc.append($('div.buscador_presentaciones').find('table.formaBusqueda_presentaciones').clone());
                                    $('#forma-buscapresentacion-window').css({"margin-left": -200, "margin-top": -180});

                                    var $tabla_resultados = $('#forma-buscapresentacion-window').find('#tabla_resultado');
                                    //var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('a[href*=cencela]');
                                    var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('#cencela');
                                    $tabla_resultados.children().remove();
									
									
									$cancelar_plugin_busca_lotes_producto.mouseover(function(){
										$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
									});
									$cancelar_plugin_busca_lotes_producto.mouseout(function(){
										$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
									});
									
                                    //crea el tr con los datos del producto seleccionado
                                    $.each(entry['Presentaciones'],function(entryIndex,pres){
                                        trr = '<tr>';
                                            trr += '<td width="100">';
                                                trr += '<span class="id_prod" style="display:none">'+pres['id']+'</span>';
                                                trr += '<span class="sku">'+pres['sku']+'</span>';
                                            trr += '</td>';
                                            trr += '<td width="250"><span class="titulo">'+pres['titulo']+'</span></td>';
                                            trr += '<td width="80">';
                                                trr += '<span class="unidad" style="display:none">'+pres['unidad']+'</span>';
                                                trr += '<span class="id_pres" style="display:none">'+pres['id_presentacion']+'</span>';
                                                trr += '<span class="pres">'+pres['presentacion']+'</span>';
                                                trr += '<span class="dec" style="display:none">'+pres['decimales']+'</span>';
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
									
                                    //seleccionar un producto del grid de resultados
                                    $tabla_resultados.find('tr').click(function(){
										//llamada a la funcion que busca y agrega producto al grid, se le pasa como parametro el lote y el almacen
										//$agrega_producto_grid($(this).find('span.lote').html(),$(this).find('input.idalmacen').val());
										var id_prod = $(this).find('span.id_prod').html();
										var sku = $(this).find('span.sku').html();
										var titulo = $(this).find('span.titulo').html();
										var unidad = $(this).find('span.unidad').html();
										var id_pres = $(this).find('span.id_pres').html();
										var pres = $(this).find('span.pres').html();
										var num_dec = $(this).find('span.dec').html();
										
										var prec_unitario;
										var id_moneda=0;
										
										//cadena json para buscar si el producto ha sido cotizado para el cliente actual
										//si ha sido cotizado anteriormente, traer el precio_unitario
										var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_precio_unitario.json';
										$arreglo2 = {'id_cliente':$id_cliente.val(),
													'id_producto':id_prod,
													'id_pres':id_pres,
													'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
												};
										
										//aqui se pasan datos a la funcion que agrega el tr en el grid
										$.post(input_json2,$arreglo2,function(prod){
											if(prod['Pu']['precio_unitario']==""){
												//alert("No hay precio unitario:"+prec_unitario);
												$agrega_producto_grid($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres," ",$select_moneda,id_moneda,$tipo_cambio,num_dec);
											}else{
												//alert("Si hay precio unitario:"+prec_unitario);
												prec_unitario = prod['Pu']['precio_unitario'];
												id_moneda = prod['Pu']['moneda_id'];
												$agrega_producto_grid($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres,prec_unitario,$select_moneda,id_moneda,$tipo_cambio,num_dec);
											}
										});
										
										$nombre_producto.val(titulo);//muestra el titulo del producto en el campo nombre del producto de la ventana de cotizaciones
										
                                        //elimina la ventana de busqueda
                                        var remove = function() {$(this).remove();};
                                        $('#forma-buscapresentacion-overlay').fadeOut(remove);
                                    });
									
                                    $cancelar_plugin_busca_lotes_producto.click(function(event){
                                        //event.preventDefault();
                                        var remove = function() {$(this).remove();};
                                        $('#forma-buscapresentacion-overlay').fadeOut(remove);
                                    });
									
                                }else{
                                    jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                                    $('#forma-prefacturas-window').find('input[name=titulo_producto]').val('');
                                }
                        });
						
                    }else{
                            jAlert("Es necesario ingresar un Sku de producto valido", 'Atencion!');
                    }
		}else{
			jAlert("Es necesario seleccionar un Cliente", 'Atencion!');
		}
		
	}//termina buscador dpresentaciones disponibles de un producto
	
    
    
    //Convertir costos en dolar y pesos
	$convertir_costos = function($tipo_cambio,moneda_id,$campo_subtotal,$campo_impuesto,$campo_total,$valor_impuesto,$grid_productos){
		var $moneda_original = $('#forma-prefacturas-window').find('input[name=moneda_original]');
		var $campo_ieps = $('#forma-prefacturas-window').find('input[name=ieps]');
		var $campo_impuesto_retenido = $('#forma-prefacturas-window').find('input[name=impuesto_retenido]');
		var $empresa_immex = $('#forma-prefacturas-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-prefacturas-window').find('input[name=tasa_ret_immex]');
		var $pdescto = $('#forma-prefacturas-window').find('input[name=pdescto]');
		
		var sumaDescuento=0;
		var sumaSubtotalConDescuento=0;
		var sumaRetencionesDePartidas=0;
		
		//Suma de todos los importes sin IVA, sin IEPS
		var sumaSubTotal = 0;
		//Suma de todos los importes del IEPS
		var sumaIeps = 0;
		//Suma de todos los importes del IVA
		var sumaImpuesto = 0;
		//Monto del iva retenido de acuerdo a la tasa de retencion immex
		var impuestoRetenido = 0;
		//suma del subtotal + totalImpuesto + sumaIeps - impuestoRetenido
		var sumaTotal = 0;
		
		$grid_productos.find('tr').each(function (index){
			var precio_cambiado=0;
			var importe_cambiado=0;
			
			var $cantidad = $(this).find('#cant');
			var $costo_unitario = $(this).find('#cost');
			var $importe = $(this).find('#import');
			var $tasa_iva = $(this).find('input[name=valor_imp]');
			var $importe_iva = $(this).find('#totimp');
			
			var $campoTasaIeps = $(this).find('#tasaIeps');
			var $importeIeps = $(this).find('#importeIeps');
			
			var $ret_tasa = $(this).find('#ret_tasa');
			var $ret_importe = $(this).find('#ret_importe');
			
			var $vdescto = $(this).find('#vdescto');
			var $pu_con_descto = $(this).find('#pu_descto');
			var $importe_del_descto = $(this).find('#importe_del_descto');
			var $importe_con_descto = $(this).find('#importe_con_descto');
									
			
			if(($costo_unitario.val().trim()!='') && ($cantidad.val().trim()!='' )){
				if( parseInt($moneda_original.val()) != parseInt(moneda_id) ){
					if(parseInt($moneda_original.val())==1){
						//si la moneda original es pesos, calculamos su equivalente a dolares
						precio_cambiado = parseFloat(quitar_comas($(this).find('#costor').val())) / parseFloat($tipo_cambio.val());
					}else{
						//si la moneda original es dolar, calculamos su equivalente a pesos
						precio_cambiado = parseFloat(quitar_comas($(this).find('#costor').val())) * parseFloat($tipo_cambio.val());
					}
					
					$costo_unitario.val($(this).agregar_comas(parseFloat(precio_cambiado).toFixed(4)));
					
					//Calcula el nuevo importe
					importe_cambiado = parseFloat($cantidad.val()) * parseFloat(precio_cambiado).toFixed(4);
					
					//Asignamos el nuevo laor del importe
					$importe.val($(this).agregar_comas(parseFloat(importe_cambiado).toFixed(4) ) );
				}else{
					//aqui entra si la moneda seleccionada es la moneda original. Le devolvemos al campo costo su valor original
					$costo_unitario.val( $(this).find('#costor').val());
					//calcula el nuevo importe
					importe_cambiado = parseFloat($cantidad.val()) * parseFloat($costo_unitario.val()).toFixed(2);
					//asignamos el nuevo laor del importe
					$importe.val($(this).agregar_comas(parseFloat(importe_cambiado).toFixed(2) ) );
				}
				
				
				//Calcular el importe del ieps
				$importeIeps.val(parseFloat(parseFloat($importe.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
				
				$importe_iva.val(parseFloat((parseFloat(quitar_comas($importe.val())) + parseFloat($importeIeps.val())) * parseFloat($tasa_iva.val())).toFixed(4));
				
				if(parseFloat($ret_tasa.val())>0){
					//Calcular la retencion de la partida
					$ret_importe.val(parseFloat(parseFloat($importe.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
				}
				
				if($pdescto.val().trim()=='true'){
					if(parseFloat($vdescto.val())>0){
						$pu_con_descto.val(parseFloat(parseFloat($costo_unitario.val()) - (parseFloat($costo_unitario.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
						$importe_del_descto.val(parseFloat(parseFloat($importe.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
						$importe_con_descto.val(parseFloat(parseFloat($importe.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
						
						//Calcular y redondear el importe del IEPS, tomando el importe con descuento
						$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
						
						//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
						$importe_impuesto.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat($tasa_iva.val()));
						
						if(parseFloat($ret_tasa.val())>0){
							//Calcular la retencion del importe con descuento
							$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
						}
					}
				}
				
				//Acumula los importes en la variable subtotal antes de descuento si existe.
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($importe.val()));
				
				//Acumula valores con descuento
				sumaDescuento = parseFloat(sumaDescuento) + parseFloat(quitar_comas($importe_del_descto.val()));
				sumaSubtotalConDescuento = parseFloat(sumaSubtotalConDescuento) + parseFloat(quitar_comas($importe_con_descto.val()));
				
				//Acumula los importes del IEPS
				sumaIeps =  parseFloat(sumaIeps) + parseFloat($importeIeps.val());
				
				//Acumula importe de IVA
				sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($importe_iva.val());
				
				//Acumula las retenciones de iva por partida
				sumaRetencionesDePartidas = parseFloat(sumaRetencionesDePartidas) + parseFloat($ret_importe.val());
			}
		});
		
		
		if($pdescto.val().trim()=='true' && parseFloat(sumaDescuento)>0){
			//Agregar importe sin descuento, sin impuesto
			$importe_subtotal.val($(this).agregar_comas(parseFloat(sumaSubTotal).toFixed(2)));
			
			//Agregar monto del descuento
			$monto_descuento.val($(this).agregar_comas(parseFloat(sumaDescuento).toFixed(2)  ));
			
			//Calcular  la tasa de retencion IMMEX
			impuestoRetenido = parseFloat(sumaSubtotalConDescuento) * parseFloat(parseFloat($tasa_ret_immex.val()));
			
			if(parseFloat(sumaRetencionesDePartidas)>0){
				//Sumar el  monto de las retenciones de las partidas si es que existe
				impuestoRetenido = parseFloat(impuestoRetenido) + parseFloat(sumaRetencionesDePartidas);
			}
			
			//Calcula el total sumando el sumaSubTotal + sumaIeps + sumaImpuesto - impuestoRetenido
			sumaTotal = parseFloat(sumaSubtotalConDescuento) + parseFloat(sumaIeps) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
			
			//Redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubtotalConDescuento).toFixed(2)  ));
		}else{
			//Calcular  la tasa de retencion IMMEX
			impuestoRetenido = parseFloat(sumaSubTotal) * parseFloat(parseFloat($tasa_ret_immex.val()));
			
			if(parseFloat(sumaRetencionesDePartidas)>0){
				//Sumar el  monto de las retenciones de las partidas si es que existe
				impuestoRetenido = parseFloat(impuestoRetenido) + parseFloat(sumaRetencionesDePartidas);
			}
			
			//Calcula el total sumando el sumaSubTotal + sumaIeps + sumaImpuesto - impuestoRetenido
			sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaIeps) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
			
			//Redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
		}
		
		//Redondea a dos digitos el IEPS y lo asigna  al campo ieps
		$campo_ieps.val($(this).agregar_comas(  parseFloat(sumaIeps).toFixed(2)  ));
		
		//Redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val($(this).agregar_comas(  parseFloat(sumaImpuesto).toFixed(2)  ));
		
		//Redondea a dos digitos el impuesto y lo asigna al campo retencion
		$campo_impuesto_retenido.val($(this).agregar_comas(  parseFloat(impuestoRetenido).toFixed(2)  ));
		
		//Redondea a dos digitos la suma  total y se asigna al campo total
		$campo_total.val($(this).agregar_comas(  parseFloat(sumaTotal).toFixed(2)  ));
		
		
		var valorHeight=540;
		
		if(parseFloat(sumaDescuento)>0){
			$('#forma-prefacturas-window').find('#tr_importe_subtotal').show();
			$('#forma-prefacturas-window').find('#tr_descto').show();
			$('#forma-prefacturas-window').find('input[name=etiqueta_motivo_descto]').show();
			$('#forma-prefacturas-window').find('input[name=motivo_descuento]').show();
			valorHeight = parseFloat(valorHeight) + 30;
		}else{
			$('#forma-prefacturas-window').find('#tr_importe_subtotal').hide();
			$('#forma-prefacturas-window').find('#tr_descto').hide();
			$('#forma-prefacturas-window').find('input[name=etiqueta_motivo_descto]').hide();
			$('#forma-prefacturas-window').find('input[name=motivo_descuento]').hide();
		}
		
		//Ocultar campos si tienen valor menor o igual a cero
		if(parseFloat(sumaIeps)<=0){
			$('#forma-prefacturas-window').find('#tr_ieps').hide();
		}else{
			$('#forma-prefacturas-window').find('#tr_ieps').show();
			valorHeight = parseFloat(valorHeight) + 15;
		}
		
		if(parseFloat(impuestoRetenido)<=0){
			$('#forma-prefacturas-window').find('#tr_retencion').hide();
		}else{
			$('#forma-prefacturas-window').find('#tr_retencion').show();
			valorHeight = parseFloat(valorHeight) + 15;
		}
		
		$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':valorHeight+'px'});
		
		/*
		if( parseInt($moneda_original.val()) != parseInt(moneda_id) ){
			//calcula el total sumando el subtotal y el impuesto
			sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
			//redondea a 4 digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(parseFloat(sumaSubTotal).toFixed(4)));
			//redondea a 4 digitos el impuesto y lo asigna al campo impuesto
			$campo_impuesto.val($(this).agregar_comas(parseFloat(sumaImpuesto).toFixed(4)));
			//redondea a 4 digitos la suma  total y se asigna al campo total
			$campo_total.val($(this).agregar_comas(parseFloat(sumaTotal).toFixed(4)));
		}else{
			//calcula el total sumando el subtotal y el impuesto
			sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
			//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(parseFloat(sumaSubTotal).toFixed(2)));
			//redondea a dos digitos el impuesto y lo asigna al campo impuesto
			$campo_impuesto.val($(this).agregar_comas(parseFloat(sumaImpuesto).toFixed(2)));
			//redondea a dos digitos la suma  total y se asigna al campo total
			$campo_total.val($(this).agregar_comas(parseFloat(sumaTotal).toFixed(2)));
		}*/
	}//termina convertir dolar pesos
	
	
	
	//Calcula totales(subtotal, impuesto, Retencion, total)
	$calcula_totales = function(){
		var $campo_subtotal = $('#forma-prefacturas-window').find('input[name=subtotal]');
		var $campo_ieps = $('#forma-prefacturas-window').find('input[name=ieps]');
		var $campo_impuesto = $('#forma-prefacturas-window').find('input[name=impuesto]');
		var $campo_impuesto_retenido = $('#forma-prefacturas-window').find('input[name=impuesto_retenido]');
		var $campo_total = $('#forma-prefacturas-window').find('input[name=total]');
		var $empresa_immex = $('#forma-prefacturas-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-prefacturas-window').find('input[name=tasa_ret_immex]');
		
		var $importe_subtotal = $('#forma-prefacturas-window').find('input[name=importe_subtotal]');
		var $monto_descuento = $('#forma-prefacturas-window').find('input[name=monto_descuento]');
		var pdescto = $('#forma-prefacturas-window').find('input[name=pdescto]').val();
		
		var $grid_productos = $('#forma-prefacturas-window').find('#grid_productos');
		
		if($tasa_ret_immex.val().trim()==''){
			$tasa_ret_immex.val(0);
		}
		
		var sumaDescuento=0;
		var sumaSubtotalConDescuento=0;
		var sumaRetencionesDePartidas=0;
		
		//Suma de todos los importes sin IVA, sin IEPS
		var sumaSubTotal = 0;
		//Suma de todos los importes del IEPS
		var sumaIeps = 0;
		//Suma de todos los importes del IVA
		var sumaImpuesto = 0;
		//Monto del iva retenido de acuerdo a la tasa de retencion immex
		var impuestoRetenido = 0;
		//suma del subtotal + totalImpuesto + sumaIeps - impuestoRetenido
		var sumaTotal = 0;
		
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#cost').val().trim() != '') && ( $(this).find('#cant').val().trim() != '' )){
				//Acumula los importes en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($(this).find('#import').val()));
				
				//Acumula valores con descuento
				sumaDescuento = parseFloat(sumaDescuento) + parseFloat(quitar_comas($(this).find('#importe_del_descto').val()));
				sumaSubtotalConDescuento = parseFloat(sumaSubtotalConDescuento) + parseFloat(quitar_comas($(this).find('#importe_con_descto').val()));
				
				//Acumula los importes del IEPS
				sumaIeps =  parseFloat(sumaIeps) + parseFloat($(this).find('#importeIeps').val());
				
				//Acumula las retenciones de iva por partida
				sumaRetencionesDePartidas = parseFloat(sumaRetencionesDePartidas) + parseFloat($(this).find('#ret_importe').val());
				
				if($(this).find('#totimp').val().trim()!=''){
					sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
		
		if(pdescto.trim()=='true' && parseFloat(sumaDescuento)>0){
			//Agregar importe sin descuento, sin impuesto
			$importe_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
			
			//Agregar monto del descuento
			$monto_descuento.val($(this).agregar_comas(  parseFloat(sumaDescuento).toFixed(2)  ));
			
			//Calcular  la tasa de retencion IMMEX
			impuestoRetenido = parseFloat(sumaSubtotalConDescuento) * parseFloat(parseFloat($tasa_ret_immex.val()));
			
			if(parseFloat(sumaRetencionesDePartidas)>0){
				//Sumar el  monto de las retenciones de las partidas si es que existe
				impuestoRetenido = parseFloat(impuestoRetenido) + parseFloat(sumaRetencionesDePartidas);
			}
			
			//Calcula el total sumando el sumaSubTotal + sumaIeps + sumaImpuesto - impuestoRetenido
			sumaTotal = parseFloat(sumaSubtotalConDescuento) + parseFloat(sumaIeps) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
			
			//Redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubtotalConDescuento).toFixed(2)  ));
		}else{
			//Calcular  la tasa de retencion IMMEX
			impuestoRetenido = parseFloat(sumaSubTotal) * parseFloat(parseFloat($tasa_ret_immex.val()));
			
			if(parseFloat(sumaRetencionesDePartidas)>0){
				//Sumar el  monto de las retenciones de las partidas si es que existe
				impuestoRetenido = parseFloat(impuestoRetenido) + parseFloat(sumaRetencionesDePartidas);
			}
			
			//Calcula el total sumando el sumaSubTotal + sumaIeps + sumaImpuesto - impuestoRetenido
			sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaIeps) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
			
			//Redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
		}
		
		
		//Redondea a dos digitos el IEPS y lo asigna  al campo ieps
		$campo_ieps.val($(this).agregar_comas(  parseFloat(sumaIeps).toFixed(2)  ));
		
		//Redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val($(this).agregar_comas(  parseFloat(sumaImpuesto).toFixed(2)  ));
		
		//Redondea a dos digitos el impuesto y lo asigna al campo retencion
		$campo_impuesto_retenido.val($(this).agregar_comas(  parseFloat(impuestoRetenido).toFixed(2)  ));
		
		//Redondea a dos digitos la suma  total y se asigna al campo total
		$campo_total.val($(this).agregar_comas(  parseFloat(sumaTotal).toFixed(2)  ));
		
		var valorHeight=540;
		
		if(parseFloat(sumaDescuento)>0){
			$('#forma-prefacturas-window').find('#tr_importe_subtotal').show();
			$('#forma-prefacturas-window').find('#tr_descto').show();
			$('#forma-prefacturas-window').find('input[name=etiqueta_motivo_descto]').show();
			$('#forma-prefacturas-window').find('input[name=motivo_descuento]').show();
			valorHeight = parseFloat(valorHeight) + 30;
		}else{
			$('#forma-prefacturas-window').find('#tr_importe_subtotal').hide();
			$('#forma-prefacturas-window').find('#tr_descto').hide();
			$('#forma-prefacturas-window').find('input[name=etiqueta_motivo_descto]').hide();
			$('#forma-prefacturas-window').find('input[name=motivo_descuento]').hide();
		}
		
		//Ocultar campos si tienen valor menor o igual a cero
		if(parseFloat(sumaIeps)<=0){
			$('#forma-prefacturas-window').find('#tr_ieps').hide();
		}else{
			$('#forma-prefacturas-window').find('#tr_ieps').show();
			valorHeight = parseFloat(valorHeight) + 15;
		}
		
		if(parseFloat(impuestoRetenido)<=0){
			$('#forma-prefacturas-window').find('#tr_retencion').hide();
		}else{
			$('#forma-prefacturas-window').find('#tr_retencion').show();
			valorHeight = parseFloat(valorHeight) + 15;
		}
		
		$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':valorHeight+'px'});
	}//termina calcular totales
	
	
	
	
	
	
	//agregar producto al grid
	$agrega_producto_grid = function($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres,prec_unitario,$select_moneda, id_moneda, $tipo_cambio,num_dec, arrayPres){
		var $valor_impuesto = $('#forma-prefacturas-window').find('input[name=valorimpuesto]');
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		var encontrado = 0;
		//busca el sku y la presentacion en el grid
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#skuprod').val() == sku.toUpperCase() )  && (parseInt($(this).find('#idpres').val())== parseInt(id_pres) ) && (parseInt($(this).find('#elim').val())!=0)){
				encontrado=1;//el producto ya esta en el grid
			}
		});
		
		var pu;
		//prec_unitario trae el precio unitario del producto
		//id_moneda trae la moneda del precio unitario
		//$select_moneda.val() trae la moneda de la prefactura actual
		if(prec_unitario != " "){
			if(parseInt($select_moneda.val())==1){
				if(parseInt(id_moneda)==1){
					pu = prec_unitario;
				}else{
					if(parseInt(id_moneda)==2){
						pu = parseFloat(parseFloat(prec_unitario) * parseFloat($tipo_cambio.val())).toFixed(2);
					}
				}
			}else{
				if(parseInt($select_moneda.val())==2){
					if(parseInt(id_moneda)==1){
						pu = parseFloat(parseFloat(prec_unitario) / parseFloat($tipo_cambio.val())).toFixed(2);
					}else{
						if(parseInt(id_moneda)==2){
							pu = prec_unitario;
						}
					}
				}
			}
		}else{
			pu = prec_unitario;
		}
		
		
		
		if(parseInt(encontrado)!=1){//si el producto no esta en el grid entra aqui
			//ocultamos el boton facturar para permitir Guardar los cambios  antes de facturar
			$('#forma-prefacturas-window').find('#facturar').hide();
			//obtiene numero de trs
			var tr = $("tr", $grid_productos).size();
			tr++;
			
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
					trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
					trr += '<input type="hidden" name="iddetalle" id="idd" value="0">';//este es el id del registro que ocupa el producto en la tabla prefacturas_detalles
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
					trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ id_prod +'">';
					trr += '<input type="text" name="sku'+ tr +'" value="'+ sku +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
					trr += '<input type="text" 	name="nombre'+ tr +'" 	value="'+ titulo +'" id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<input type="text" 	name="unidad'+ tr +'" 	value="'+ unidad +'" id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
					trr += '<input type="hidden"    name="id_presentacion"        	value="'+  id_pres +'" id="idpres">';
					trr += '<input type="hidden"    name="numero_decimales"        	value="'+  num_dec +'" id="numdec">';
					trr += '<input type="hidden" 		name="presentacion'+ tr +'"         value="'+  pres +'" id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
					trr += '<select name="select_pres" class="selectPres'+ tr +'" style="width:96px;">';
						trr += '<option value="0" selected="yes">[-Seleccionar-]</option>';
					trr += '</select>';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" 	name="cantidad" value=" " id="cant" style="width:76px;">';
				trr += '</td>';
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<input type="text" 	name="costo" 	value="'+ pu +'" id="cost" style="width:86px; text-align:right;">';
				trr += '</td>';
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<input type="text" 	name="importe'+ tr +'" 	value="" id="import" readOnly="true" style="width:86px; text-align:right;">';
					trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="0">';
				trr += '</td>';
			trr += '</tr>';
            
			$grid_productos.append(trr);
			
			
			//cargar select de presentaciones de cada producto
			$grid_productos.find('select.selectPres'+ tr).children().remove();
			var moneda_hmtl = '';
			$.each(arrayPres,function(entryIndex,pres){
				moneda_hmtl += '<option value="' + pres['id'] + '"  >' + pres['descripcion'] + '</option>';
			});
			$grid_productos.find('select.selectPres'+ tr).append(moneda_hmtl);
			
			
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_productos.find('#cant').focus(function(e){
				if($(this).val() == ' '){
						$(this).val('');
				}
			});
			
			//recalcula importe al perder enfoque el campo cantidad
			//$grid_productos.find('input[name=cantidad['+ tr +']]').blur(function(){
			$grid_productos.find('#cant').blur(function(){
				if ($(this).val() == ''){
					$(this).val(' ');
				}
				if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cost').val() != ' ') )
				{	//calcula el importe
					$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cost').val()));
					//redondea el importe en dos decimales
					$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
					
					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($valor_impuesto.val()));
				}else{
					$(this).parent().parent().find('#import').val('');
					$(this).parent().parent().find('#totimp').val('');
				}
				
				
				
				var numero_decimales = $(this).parent().parent().find('#numdec').val();
				var patron = /^-?[0-9]+([,\.][0-9]{0,0})?$/;
				if(parseInt(numero_decimales)==1){
					patron = /^-?[0-9]+([,\.][0-9]{0,1})?$/;
				}
				if(parseInt(numero_decimales)==2){
					patron = /^-?[0-9]+([,\.][0-9]{0,2})?$/;
				}
				if(parseInt(numero_decimales)==3){
					patron = /^-?[0-9]+([,\.][0-9]{0,3})?$/;
				}
				if(parseInt(numero_decimales)==4){
					patron = /^-?[0-9]+([,\.][0-9]{0,4})?$/;
				}
				
				/*
				if(patron.test($(this).val())){
					alert("Si valido"+$(this).val());
				}else{
					alert("El numero de decimales es incorrecto: "+$(this).val());
					$(this).val('')
				}
				*/
				
				if(!patron.test($(this).val())){
					//alert("Si valido"+$(this).val());
				}else{
					
				}
				
				
				
				$calcula_totales();//llamada a la funcion que calcula totales
			});
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_productos.find('#cost').focus(function(e){
					if($(this).val() == ' '){
							$(this).val('');
					}
			});
                        
			//recalcula importe al perder enfoque el campo costo
			$grid_productos.find('#cost').blur(function(){
				if ($(this).val() == ''){
					$(this).val(' ');
				}
				//$grid_productos.find('input[name=costo['+ tr +']]').blur(function(){
				if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cant').val() != ' ') )
				{	//calcula el importe
					$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cant').val()));
					//redondea el importe en dos decimales
					$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
					
					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($valor_impuesto.val()));
				}else{
					$(this).parent().parent().find('#import').val('');
					$(this).parent().parent().find('#totimp').val('');
				}
				$calcula_totales();//llamada a la funcion que calcula totales
			});
			
			//validar campo costo, solo acepte numeros y punto
			//$grid_productos.find('input[name=costo['+ tr +']]').keypress(function(e){
			$grid_productos.find('#cost').keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}
			});
			
			//validar campo cantidad, solo acepte numeros y punto
			//$grid_productos.find('input[name=cantidad['+ tr +']]').keypress(function(e){
			$grid_productos.find('#cant').keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}		
			});
			
			//elimina un producto del grid
			$grid_productos.find('#delete'+ tr).bind('click',function(event){
				event.preventDefault();
				if(parseInt($(this).parent().find('#elim').val()) != 0){
					//asigna espacios en blanco a todos los input de la fila eliminada
					$(this).parent().parent().find('input').val(' ');
					
					//asigna un 0 al input eliminado como bandera para saber que esta eliminado
					$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
					
					//oculta la fila eliminada
					$(this).parent().parent().hide();
					$calcula_totales();//llamada a la funcion que calcula totales
				}
			});
			
		}else{
			jAlert("El producto: "+sku+" con presentacion: "+pres+" ya se encuentra en el listado, seleccione otro diferente.", 'Atencion!');
		}
		
	}//termina agregar producto al grid
	
	
	
	
	
	
	
	
	
	
	//Ventana para agregar datos de la Adenda
	var $cargaFormaDatosAdenda = function(id_adenda, $campo1, $campo2, $campo3, $campo4, $campo5, $campo6, $campo7, $campo8){
		//aqui  entra para editar un registro
		var form_to_show = 'formaDatosAdenda';
		
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_adenda});
		
		$(this).modalPanel_datosadenda();
					
		$('#forma-datosadenda-window').css({"margin-left": -420, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-datosadenda-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_adenda , style:'display:table'});
		$tabs_li_funxionalidad_datos_adenda();
		
		
		var $adenda1_campo1 = $('#forma-datosadenda-window').find('input[name=adenda1_campo1]');
		var $adenda1_campo2 = $('#forma-datosadenda-window').find('input[name=adenda1_campo2]');
		var $check_adenda1_campo3 = $('#forma-datosadenda-window').find('input[name=check_adenda1_campo3]');
		var $adenda1_campo4 = $('#forma-datosadenda-window').find('input[name=adenda1_campo4]');
		var $adenda1_campo5 = $('#forma-datosadenda-window').find('input[name=adenda1_campo5]');
		var $adenda1_campo6 = $('#forma-datosadenda-window').find('input[name=adenda1_campo6]');
		var $adenda1_campo7 = $('#forma-datosadenda-window').find('input[name=adenda1_campo7]');
		var $adenda1_campo8 = $('#forma-datosadenda-window').find('input[name=adenda1_campo8]');
		
		//Este campo almacena la cadena del warning de la ventana de Adenda
		var $warning_adenda = $('#forma-prefacturas-window').find('input[name=warning_adenda]');
		
		var $aceptar_adenda = $('#forma-datosadenda-window').find('#aceptar_adenda');
		var $salir = $('#forma-datosadenda-window').find('#salir');
		
		//Botones                        
		var $cerrar_plugin = $('#forma-datosadenda-window').find('#close');
		
		
		if(parseInt(id_adenda)==1){
			$adenda1_campo1.val($campo1.val());
			$adenda1_campo2.val($campo2.val());
			$check_adenda1_campo3.attr('checked',  ($campo3.val().trim() == 'true')? true:false );
			$adenda1_campo4.val($campo4.val());
			$adenda1_campo5.val($campo5.val());
			$adenda1_campo6.val($campo6.val());
			$adenda1_campo7.val($campo7.val());
			$adenda1_campo8.val($campo8.val());
			
			//Aqui se muestran los warning si es que hay
			var valor = $warning_adenda.val().split('&&&&&');
			for (var element in valor){
				tmp = $warning_adenda.val().split('&&&&&')[element];
				longitud = tmp.split('$');
				if( longitud.length > 1 ){
					$('#forma-datosadenda-window').find('img[rel=warning_' + tmp.split('$')[0] + ']')
					.parent()
					.css({'display':'block'})
					.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split('$')[1]});
				}
			}
			
			
			
			
			$adenda1_campo5.attr("readonly", true);
			$adenda1_campo5.click(function (s){
				var a=$('div.datepicker');
				a.css({'z-index':100});
			});
			
			$adenda1_campo5.DatePicker({
				format:'Y-m-d',
				date: $adenda1_campo5.val(),
				current: $adenda1_campo5.val(),
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
					$adenda1_campo5.val(formated);
					if (formated.match(patron) ){
						var valida_fecha=mayor($adenda1_campo5.val(),mostrarFecha());
						if (valida_fecha==true){
							jAlert("Fecha no valida",'! Atencion');
							$adenda1_campo5.val(mostrarFecha());
						}else{
							$adenda1_campo5.DatePickerHide();	
						}
					}
				}
			});
			
			$adenda1_campo6.attr("readonly", true);
			$adenda1_campo6.click(function (s){
				var a=$('div.datepicker');
				a.css({'z-index':100});
			});
			
			$adenda1_campo6.DatePicker({
				format:'Y-m-d',
				date: $adenda1_campo6.val(),
				current: $adenda1_campo6.val(),
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
					$adenda1_campo6.val(formated);
					if (formated.match(patron) ){
						var valida_fecha=mayor($adenda1_campo6.val(),mostrarFecha());
						if (valida_fecha==true){
							jAlert("Fecha no valida",'! Atencion');
							$adenda1_campo6.val(mostrarFecha());
						}else{
							$adenda1_campo6.DatePickerHide();	
						}
					}
				}
			});
			
			$adenda1_campo1.focus();
		}
		
		
		//Aceptar datos de la adenda
		$aceptar_adenda.click(function(event){
			if(parseInt(id_adenda)==1){
				//Obtener el valor del campo checkbox
				var checkbox_marcado = $check_adenda1_campo3.prop("checked");
				
				$campo1.val($adenda1_campo1.val());
				$campo2.val($adenda1_campo2.val());
				$campo3.val(checkbox_marcado);
				$campo4.val($adenda1_campo4.val());
				$campo5.val($adenda1_campo5.val());
				$campo6.val($adenda1_campo6.val());
				$campo7.val($adenda1_campo7.val());
				$campo8.val($adenda1_campo8.val());
				
				//eliminar el warning 
				$warning_adenda.val('');
				
				//Cerrar ventana
				var remove = function() {$(this).remove();};
				$('#forma-datosadenda-overlay').fadeOut(remove);
			}
		});
		
		
		$salir.click(function(event){
			event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-datosadenda-overlay').fadeOut(remove);
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-datosadenda-overlay').fadeOut(remove);
		});
		
	}
	
	
	
	
	
	
	
	
	
	//nueva prefactura
	$new_prefactura.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_prefacturas();
		
		var form_to_show = 'formaPrefacturas00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-prefacturas-window').css({"margin-left": -465, 	"margin-top": -235});
		
		$forma_selected.prependTo('#forma-prefacturas-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		//var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + accion + '/' + id_to_show + '/out.json';
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPrefactura.json';
		$arreglo = {'id_prefactura':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
        var $folio_pedido = $('#forma-prefacturas-window').find('input[name=folio_pedido]');
        var $select_tipo_documento = $('#forma-prefacturas-window').find('select[name=select_tipo_documento]');
		var $id_prefactura = $('#forma-prefacturas-window').find('input[name=id_prefactura]');
		var $refacturar = $('#forma-prefacturas-window').find('input[name=refacturar]');
		var $accion = $('#forma-prefacturas-window').find('input[name=accion]');		
		var $total_tr = $('#forma-prefacturas-window').find('input[name=total_tr]');
		
		var $busca_cliente = $('#forma-prefacturas-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-prefacturas-window').find('input[name=id_cliente]');
		var $no_cliente = $('#forma-prefacturas-window').find('input[name=nocliente]');
		var $razon_cliente = $('#forma-prefacturas-window').find('input[name=razoncliente]');
		var $id_df = $('#forma-prefacturas-window').find('input[name=id_df]');
		var $dir_cliente = $('#forma-prefacturas-window').find('input[name=dircliente]');
		var $select_moneda = $('#forma-prefacturas-window').find('select[name=moneda]');
		var $moneda_original = $('#forma-prefacturas-window').find('input[name=moneda_original]');
		var $tipo_cambio = $('#forma-prefacturas-window').find('input[name=tipo_cambio]');
		var $tasa_ret_immex = $('#forma-prefacturas-window').find('input[name=tasa_ret_immex]');
		var $empresa_immex = $('#forma-prefacturas-window').find('input[name=empresa_immex]');
		
		var $id_impuesto = $('#forma-prefacturas-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-prefacturas-window').find('input[name=valorimpuesto]');
		var $observaciones = $('#forma-prefacturas-window').find('textarea[name=observaciones]');
		
		var $select_condiciones = $('#forma-prefacturas-window').find('select[name=condiciones]');
		var $select_vendedor = $('#forma-prefacturas-window').find('select[name=vendedor]');
		var $orden_compra = $('#forma-prefacturas-window').find('input[name=orden_compra]');
		
		var $select_metodo_pago = $('#forma-prefacturas-window').find('select[name=select_metodo_pago]');
		var $etiqueta_digit = $('#forma-prefacturas-window').find('input[name=digit]');
		var $digitos_original = $('#forma-prefacturas-window').find('input[name=digitos_original]');
		var $no_cuenta = $('#forma-prefacturas-window').find('input[name=no_cuenta]');
		var $cta_mn = $('#forma-prefacturas-window').find('input[name=cta_mn]');
		var $cta_usd = $('#forma-prefacturas-window').find('input[name=cta_usd]');
		var $select_almacen = $('#forma-prefacturas-window').find('select[name=select_almacen]');
		var $select_tmov = $('#forma-prefacturas-window').find('select[name=select_tmov]');
		
		//Boton para agregar datos de la Adenda
		var $agregarDatosAdenda = $('#forma-prefacturas-window').find('#agregarDatosAdenda');
		var $check_incluye_adenda = $('#forma-prefacturas-window').find('input[name=check_incluye_adenda]');
		
		//Variable para ocultar campos de la adenda cuando no se debe incluir
		var $adenda = $('#forma-prefacturas-window').find('.adenda');
		
		var $campo1 = $('#forma-prefacturas-window').find('input[name=campo1]');
		var $campo2 = $('#forma-prefacturas-window').find('input[name=campo2]');
		var $campo3 = $('#forma-prefacturas-window').find('input[name=campo3]');
		var $campo4 = $('#forma-prefacturas-window').find('input[name=campo4]');
		var $campo5 = $('#forma-prefacturas-window').find('input[name=campo5]');
		var $campo6 = $('#forma-prefacturas-window').find('input[name=campo6]');
		var $campo7 = $('#forma-prefacturas-window').find('input[name=campo7]');
		var $campo8 = $('#forma-prefacturas-window').find('input[name=campo8]');
		
		//Este campo almacena la cadena del warning de la ventana de Adenda
		var $warning_adenda = $('#forma-prefacturas-window').find('input[name=warning_adenda]');
		
		
		//Boton para Facturar
		var $boton_facturar = $('#forma-prefacturas-window').find('#facturar');
		
		//var $boton_descargarpdf = $('#forma-prefacturas-window').find('#descargarpdf');
		//var $boton_cancelarfactura = $('#forma-prefacturas-window').find('#cancelarfactura');
		//var $boton_descargarxml = $('#forma-prefacturas-window').find('#descargarxml');
		
		//busca remisiones para agregar y facturar
		var $agregar_remision = $('#forma-prefacturas-window').find('a[href*=agregar_remision]');
		
		//grid de productos
		var $grid_productos = $('#forma-prefacturas-window').find('#grid_productos');
		var $titulo_delete = $('#forma-prefacturas-window').find('.titulo_delete');
		var $titulo_remision = $('#forma-prefacturas-window').find('#titulo_remision');
		
		//grid de errores
		var $grid_warning = $('#forma-prefacturas-window').find('#div_warning_grid').find('#grid_warning');
		
		var $etiqueta_motivo_descto = $('#forma-prefacturas-window').find('input[name=etiqueta_motivo_descto]');
		var $motivo_descuento = $('#forma-prefacturas-window').find('input[name=motivo_descuento]');
		var $subtotal = $('#forma-prefacturas-window').find('input[name=subtotal]');
		var $impuesto = $('#forma-prefacturas-window').find('input[name=impuesto]');
		var $total = $('#forma-prefacturas-window').find('input[name=total]');
		
		var $cerrar_plugin = $('#forma-prefacturas-window').find('#close');
		var $cancelar_plugin = $('#forma-prefacturas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-prefacturas-window').find('#submit');
		
		//quitar enter a todos los campos input
		$('#forma-prefacturas-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$id_prefactura.val(0);//para nueva cotizacion el folio es 0
		$id_df.val(1);
		//$campo_factura.css({'background' : '#ffffff'});
		$etiqueta_motivo_descto.hide();
		$motivo_descuento.hide();
		
		//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
		$boton_facturar.hide();
		$titulo_delete.hide();
		//$boton_descargarpdf.hide();
		//$boton_cancelarfactura.hide();
		//$boton_descargarxml.hide();
		$refacturar.val('false');
		$accion.val('new');
		$etiqueta_digit.attr('disabled','-1');
		$folio_pedido.css({'background' : '#F0F0F0'});
		//$no_cliente.css({'background' : '#F0F0F0'});
		//$razon_cliente.css({'background' : '#F0F0F0'});
		$dir_cliente.css({'background' : '#F0F0F0'});
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				
				jAlert(data['msj'], 'Atencion!');
				
				var remove = function() {$(this).remove();};
				$('#forma-prefacturas-overlay').fadeOut(remove);
				$get_datos_grid();
				
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-prefacturas-window').find('.div_one').css({'height':'545px'});//sin errores
				$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':'578px'});//con errores
				$('#forma-prefacturas-window').find('div.interrogacion').css({'display':'none'});
				
				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});
				
				$('#forma-prefacturas-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-prefacturas-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-prefacturas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
						
						if(tmp.split(':')[0].substring(0, 7) == 'adenda2'){
							//alert(tmp.split(':')[1]);
							$warning_adenda.val(tmp.split(':')[1]);
						}
						
						if(parseInt($("tr", $grid_productos).size())>0){
							for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
								if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
									//alert(tmp.split(':')[0]);
									$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':'578px'});
									//$('#forma-prefacturas-window').find('.div_three').css({'height':'910px'});
									
									$('#forma-prefacturas-window').find('#div_warning_grid').css({'display':'block'});
									if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
										$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
										//alert();
									}else{
										if(tmp.split(':')[0].substring(0, 5) == 'costo'){
											$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
										}
									}
									
									//$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
									//$grid_productos.find('select[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
									
									var tr_warning = '<tr>';
										tr_warning += '<td width="20"><div><IMG SRC="../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
										tr_warning += '<td width="120">';
										tr_warning += '<input type="text" value="'+$grid_productos.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
										tr_warning += '</td>';
										tr_warning += '<td width="200">';
										tr_warning += '<input type="text" value="'+$grid_productos.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
										tr_warning += '</td>';
										tr_warning += '<td width="235">';
										tr_warning += '<input type="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:230px; color:red">';
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
		
		var options = {datatype :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			
			//cargar select de tipos de movimiento
			var elemento_seleccionado = 3;//Facturacion de Remisiones
			var mostrar_opciones = 'false';
			$carga_select_con_arreglo_fijo($select_tipo_documento, array_select_documento, elemento_seleccionado, mostrar_opciones);
			
			//$campo_tc.val(entry['tc']['tipo_cambio']);
			$id_impuesto.val(entry['iva']['0']['id_impuesto']);
			$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
			$tipo_cambio.val(entry['Extras']['0']['tipo_cambio']);
			
			$check_incluye_adenda, $adenda, $agregarDatosAdenda
			
			$check_incluye_adenda.attr('checked',  (entry['Extras']['0']['adenda'] == 'true')? true:false );
			
			$check_incluye_adenda.click(function(event){
				//Esto es para evitar que le quiten la seleccion cuando incluye adenda.
				if(entry['Extras']['0']['adenda'] == 'true'){
					this.checked=true;
				}
			});
			
			//Ocultar check y boton de la adenda, cuando el cliente no incluya adenda.
			if(entry['Extras']['0']['adenda'] == 'false'){
				$adenda.hide();
			}
			
			/*
			$agregarDatosAdenda.click(function(event){
				cargaFormaDatosAdenda(1);
			});
			*/
			
			$select_tmov.children().remove();
			var tmov_hmtl = '';
			if(entry['TMov']){
				if(parseInt(entry['TMov'].length) <= 0 ){
					tmov_hmtl += '<option value="0">[--- ---]</option>';
				}
				$.each(entry['TMov'],function(entryIndex,mov){
					tmov_hmtl += '<option value="'+ mov['id'] +'">'+ mov['titulo'] + '</option>';
				});
			}else{
				tmov_hmtl += '<option value="0">[--- ---]</option>';
			}
			$select_tmov.append(tmov_hmtl);
			
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="'+ moneda['id'] +'">'+ moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			
			//carga select de vendedores
			$select_vendedor.children().remove();
			var hmtl_vendedor='';
			$.each(entry['Vendedores'],function(entryIndex,vendedor){
				hmtl_vendedor += '<option value="' + vendedor['id'] + '"  >' + vendedor['nombre_vendedor'] + '</option>';
			});
			$select_vendedor.append(hmtl_vendedor);
			
			//carga select de terminos
			$select_condiciones.children().remove();
			var hmtl_condiciones='';
			$.each(entry['Condiciones'],function(entryIndex,condicion){
				hmtl_condiciones += '<option value="' + condicion['id'] + '"  >' + condicion['descripcion'] + '</option>';
			});
			$select_condiciones.append(hmtl_condiciones);
			
			//carga select de metodos de pago
			$select_metodo_pago.children().remove();
			var hmtl_metodo='';
			$.each(entry['MetodosPago'],function(entryIndex,metodo){
				hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
			});
			$select_metodo_pago.append(hmtl_metodo);
			
			//carga select de almacen de la venta
			$select_almacen.children().remove();
			var hmtl_alm='';
			$.each(entry['Almacenes'],function(entryIndex,alm){
				hmtl_alm += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
			});
			$select_almacen.append(hmtl_alm);
			
			//buscador de clientes
			$busca_cliente.click(function(event){
				event.preventDefault();
				$busca_clientes( $select_moneda,$select_condiciones,$select_vendedor, entry['Monedas'], entry['Condiciones'],entry['Vendedores'], $razon_cliente, $no_cliente );
			});
			
			//buscador de remisiones para agregar al grid
			$agregar_remision.click(function(event){
				event.preventDefault();
				if(parseInt($id_cliente.val()) != 0){
					$busca_remisiones($grid_productos, $select_moneda,$select_metodo_pago, $folio_pedido, $orden_compra, $no_cuenta, $id_cliente.val(), entry['Monedas'], entry['MetodosPago'], $select_almacen, entry['Almacenes'], $check_incluye_adenda, $adenda, $agregarDatosAdenda, $campo1, $campo2, $campo3, $campo4, $campo5, $campo6, $campo7, $campo8 );
				}else{
					jAlert('Es necesario seleccionar un Cliente', 'Atencion!', function(r) { $agregar_remision.focus(); });
				}
			});
			
			
			//ejecutar busqueda de cliente al pulsar enter sobre el campo No de control
			$no_cliente.keypress(function(e){
				if(e.which == 13){
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoClient.json';
					$arreglo2 = {'no_control':$no_cliente.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					
					$.post(input_json2,$arreglo2,function(entry2){
						
						if(parseInt(entry2['Cliente'].length) > 0 ){
							var id_moneda = entry2['Cliente'][0]['moneda_id'];
							var id_termino = entry2['Cliente'][0]['terminos_id'];
							var id_vendedor = entry2['Cliente'][0]['cxc_agen_id'];
							var id_cliente = entry2['Cliente'][0]['id'];
							var no_control_cliente = entry2['Cliente'][0]['numero_control'];
							var razon_social_cliente = entry2['Cliente'][0]['razon_social'];
							var empresa_immex = entry2['Cliente'][0]['empresa_immex'];
							var tasa_immex = entry2['Cliente'][0]['tasa_ret_immex'];
							var cuenta_mn = entry2['Cliente'][0]['cta_pago_mn'];
							var cuenta_usd = entry2['Cliente'][0]['cta_pago_usd'];
							var dir_cliente = entry2['Cliente'][0]['direccion'];
							var rfc_cliente = entry2['Cliente'][0]['rfc'];
							
							//llamada a la funcion que agrega datos del cliente a la ventana de la prefactura
							$agregarDatosClienteSeleccionado($select_moneda, $select_condiciones, $select_vendedor, entry['Monedas'], entry['Condiciones'], entry['Vendedores'], id_moneda, id_termino, id_vendedor, id_cliente, no_control_cliente, razon_social_cliente, empresa_immex, tasa_immex, cuenta_mn, cuenta_usd, dir_cliente, rfc_cliente);
							
						}else{
							//limpiar campos
							$('#forma-prefacturas-window').find('input[name=id_cliente]').val('');
							$('#forma-prefacturas-window').find('input[name=nocliente]').val('');
							$('#forma-prefacturas-window').find('input[name=razoncliente]').val('');
							$('#forma-prefacturas-window').find('input[name=empresa_immex]').val('');
							$('#forma-prefacturas-window').find('input[name=tasa_ret_immex]').val('');
							$('#forma-prefacturas-window').find('input[name=cta_mn]').val('');
							$('#forma-prefacturas-window').find('input[name=cta_usd]').val('');
							$('#forma-prefacturas-window').find('input[name=dircliente]').val('');
							$('#forma-prefacturas-window').find('input[name=rfc]').val('');
							
							jAlert('N&uacute;mero de cliente desconocido.', 'Atencion!', function(r) { 
								$('#forma-prefacturas-window').find('input[name=nocliente]').focus(); 
							});
						}
					},"json");//termina llamada json
					
					return false;
				}
			});
			
			
		},"json");//termina llamada json
		
		//aplicar evento keypress para el campo Razon Social para que al pulsar enter sobre ella ejecute la busqueda de Cliente
		$(this).aplicarEventoKeypressEjecutaTrigger($razon_cliente, $busca_cliente);
		
		
		$tipo_cambio.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		


		//Se invoca solo para redimensionar altura de la ventana
		$calcula_totales();

		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			$total_tr.val(trCount);
			if(parseInt(trCount) > 0){
				$subtotal.val(quitar_comas($subtotal.val()));
				$impuesto.val(quitar_comas($impuesto.val()));
				$total.val(quitar_comas($total.val()));
				return true;
			}else{
				jAlert('No hay datos para actualizar.', 'Atencion!', function(r) { $agregar_remision.focus(); });
				return false;
			}
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-prefacturas-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-prefacturas-overlay').fadeOut(remove);
		});
		
		//asignar el enfoque al campo Numero de Control del Cliente al cargar la ventana
		$no_cliente.focus();
		
	});//termina nueva prefactura
	
	
	
	
	
	var carga_formaPrefacturas00_for_datagrid00 = function(id_to_show, accion_mode){
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
			$('#forma-prefacturas-window').remove();
			$('#forma-prefacturas-overlay').remove();
            
			var form_to_show = 'formaPrefacturas00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_prefacturas();
			
			$('#forma-prefacturas-window').css({"margin-left": -465, 	"margin-top": -235});
			
			$forma_selected.prependTo('#forma-prefacturas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPrefactura.json';
				$arreglo = {'id_prefactura':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var $select_tipo_documento = $('#forma-prefacturas-window').find('select[name=select_tipo_documento]');
				var $folio_pedido = $('#forma-prefacturas-window').find('input[name=folio_pedido]');
				var $total_tr = $('#forma-prefacturas-window').find('input[name=total_tr]');
				var $id_prefactura = $('#forma-prefacturas-window').find('input[name=id_prefactura]');
				var $refacturar = $('#forma-prefacturas-window').find('input[name=refacturar]');
				var $accion = $('#forma-prefacturas-window').find('input[name=accion]');
				
				var $busca_cliente = $('#forma-prefacturas-window').find('a[href*=busca_cliente]');
				var $id_cliente = $('#forma-prefacturas-window').find('input[name=id_cliente]');
				var $no_cliente = $('#forma-prefacturas-window').find('input[name=nocliente]');
				var $razon_cliente = $('#forma-prefacturas-window').find('input[name=razoncliente]');
				var $rfc = $('#forma-prefacturas-window').find('input[name=rfc]');
				var $id_df = $('#forma-prefacturas-window').find('input[name=id_df]');
				var $dir_cliente = $('#forma-prefacturas-window').find('input[name=dircliente]');
				var $tasa_ret_immex = $('#forma-prefacturas-window').find('input[name=tasa_ret_immex]');
				var $empresa_immex = $('#forma-prefacturas-window').find('input[name=empresa_immex]');
				
				var $select_moneda = $('#forma-prefacturas-window').find('select[name=moneda]');
				var $moneda_original = $('#forma-prefacturas-window').find('input[name=moneda_original]');
				var $tipo_cambio = $('#forma-prefacturas-window').find('input[name=tipo_cambio]');
				var $tipo_tipo_cambio_original = $('#forma-prefacturas-window').find('input[name=tipo_cambio_original]');
				var $orden_compra = $('#forma-prefacturas-window').find('input[name=orden_compra]');
				var	$orden_compra_original = $('#forma-prefacturas-window').find('input[name=orden_compra_original]');
				
				var $select_metodo_pago = $('#forma-prefacturas-window').find('select[name=select_metodo_pago]');
				var $select_metodo_pago_original = $('#forma-prefacturas-window').find('select[name=select_metodo_pago_original]');
				var $etiqueta_digit = $('#forma-prefacturas-window').find('input[name=digit]');
				var $digitos_original = $('#forma-prefacturas-window').find('input[name=digitos_original]');
				var $no_cuenta = $('#forma-prefacturas-window').find('input[name=no_cuenta]');
				var $cta_mn = $('#forma-prefacturas-window').find('input[name=cta_mn]');
				var $cta_usd = $('#forma-prefacturas-window').find('input[name=cta_usd]');
				
				var $id_impuesto = $('#forma-prefacturas-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-prefacturas-window').find('input[name=valorimpuesto]');
				
				var $observaciones = $('#forma-prefacturas-window').find('textarea[name=observaciones]');
				var $select_condiciones = $('#forma-prefacturas-window').find('select[name=condiciones]');
				var $select_vendedor = $('#forma-prefacturas-window').find('select[name=vendedor]');
				var $observaciones_original = $('#forma-prefacturas-window').find('textarea[name=observaciones_original]');
				var $select_condiciones_original = $('#forma-prefacturas-window').find('select[name=condiciones_original]');
				var $select_vendedor_original = $('#forma-prefacturas-window').find('select[name=vendedor_original]');
				var $select_almacen = $('#forma-prefacturas-window').find('select[name=select_almacen]');
				var $select_tmov = $('#forma-prefacturas-window').find('select[name=select_tmov]');
				
				//var $select_almacen = $('#forma-prefacturas-window').find('select[name=almacen]');
				//var $sku_producto = $('#forma-prefacturas-window').find('input[name=sku_producto]');
				//var $nombre_producto = $('#forma-prefacturas-window').find('input[name=nombre_producto]');
				
				//buscar producto
				//var $busca_sku = $('#forma-prefacturas-window').find('a[href*=busca_sku]');
				//href para agregar producto al grid
				//var $agregar_producto = $('#forma-prefacturas-window').find('a[href*=agregar_producto]');
				
				//Boton para agregar datos de la Adenda
				var $agregarDatosAdenda = $('#forma-prefacturas-window').find('#agregarDatosAdenda');
				var $check_incluye_adenda = $('#forma-prefacturas-window').find('input[name=check_incluye_adenda]');
				//Variable para ocultar campos de la adenda cuando no se debe incluir
				var $adenda = $('#forma-prefacturas-window').find('.adenda');
				
				var $campo1 = $('#forma-prefacturas-window').find('input[name=campo1]');
				var $campo2 = $('#forma-prefacturas-window').find('input[name=campo2]');
				var $campo3 = $('#forma-prefacturas-window').find('input[name=campo3]');
				var $campo4 = $('#forma-prefacturas-window').find('input[name=campo4]');
				var $campo5 = $('#forma-prefacturas-window').find('input[name=campo5]');
				var $campo6 = $('#forma-prefacturas-window').find('input[name=campo6]');
				var $campo7 = $('#forma-prefacturas-window').find('input[name=campo7]');
				var $campo8 = $('#forma-prefacturas-window').find('input[name=campo8]');
				
				
				//Este campo almacena la cadena del warning de la ventana de Adenda
				var $warning_adenda = $('#forma-prefacturas-window').find('input[name=warning_adenda]');
				
				
				var $boton_facturar = $('#forma-prefacturas-window').find('#facturar');
				
				var $agregar_remision = $('#forma-prefacturas-window').find('a[href*=agregar_remision]');
				
				//grid de productos
				var $grid_productos = $('#forma-prefacturas-window').find('#grid_productos');
				var $titulo_delete = $('#forma-prefacturas-window').find('.titulo_delete');
				var $titulo_remision = $('#forma-prefacturas-window').find('#titulo_remision');
				
				//grid de errores
				var $grid_warning = $('#forma-prefacturas-window').find('#div_warning_grid').find('#grid_warning');
				
				var $subtotal = $('#forma-prefacturas-window').find('input[name=subtotal]');
				var $impuesto = $('#forma-prefacturas-window').find('input[name=impuesto]');
				var $total = $('#forma-prefacturas-window').find('input[name=total]');
				
				var $importe_subtotal = $('#forma-prefacturas-window').find('input[name=importe_subtotal]');
				var $monto_descuento = $('#forma-prefacturas-window').find('input[name=monto_descuento]');
				var $pdescto = $('#forma-prefacturas-window').find('input[name=pdescto]');
				var $etiqueta_motivo_descto = $('#forma-prefacturas-window').find('input[name=etiqueta_motivo_descto]');
				var $motivo_descuento = $('#forma-prefacturas-window').find('input[name=motivo_descuento]');
				
				
				var $cerrar_plugin = $('#forma-prefacturas-window').find('#close');
				var $cancelar_plugin = $('#forma-prefacturas-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-prefacturas-window').find('#submit');
				
				//Ocultar boton actualizar porque ya esta facturado, ya no se puede guardar cambios
				$submit_actualizar.hide();
				$titulo_remision.hide();
				$agregar_remision.hide();
				$etiqueta_motivo_descto.hide();
				$motivo_descuento.hide();
				
				$etiqueta_digit.attr('disabled','-1');
				
				$refacturar.val('');
				$boton_facturar.hide();
				$accion.val('actualizar');
				
				$folio_pedido.css({'background' : '#F0F0F0'});
				$no_cliente.css({'background' : '#F0F0F0'});
				$razon_cliente.css({'background' : '#F0F0F0'});
				$dir_cliente.css({'background' : '#F0F0F0'});
				$orden_compra.attr("readonly", true);
				
				//Quitar enter a todos los campos input
				$('#forma-prefacturas-window').find('input').keypress(function(e){
					if(e.which==13 ) {
						return false;
					}
				});
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
							
						$('#forma-prefacturas-window').find('div.interrogacion').css({'display':'none'});
						
						jAlert(data['msj'], 'Atencion!');
						
						if ( data['valor'] == "true" ){
							$no_cliente.attr('disabled','-1'); //deshabilitar
							$razon_cliente.attr('disabled','-1'); //deshabilitar
							$observaciones.attr('disabled','-1'); //deshabilitar
							$select_moneda.attr('disabled','-1'); //deshabilitar
							$tipo_cambio.attr('disabled','-1'); //deshabilitar
							$select_vendedor.attr('disabled','-1'); //deshabilitar
							$select_condiciones.attr('disabled','-1'); //deshabilitar
							//$sku_producto.attr('disabled','-1'); //deshabilitar
							//$nombre_producto.attr('disabled','-1'); //deshabilitar
							$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
							$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
							$grid_productos.find('#cant').attr('disabled','-1'); //deshabilitar
							$grid_productos.find('#cost').attr('disabled','-1'); //deshabilitar
							$grid_productos.find('a').hide();//ocultar
							$orden_compra.attr('disabled','-1'); //deshabilitar
							$select_metodo_pago.attr('disabled','-1'); //deshabilitar
							$select_metodo_pago_original.attr('disabled','-1'); //deshabilitar
							$select_almacen.attr('disabled','-1'); //deshabilitar
							//$busca_sku.hide();
							//$agregar_producto.hide();
							$boton_facturar.hide();
							
							//Guardar nueva prefactura generada con datos de remisiones
							if( parseInt($select_tipo_documento.val()) == 3 ){
								//Ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
								$submit_actualizar.hide();
							}
							
							$get_datos_grid();
							
							var remove = function() {$(this).remove();};
							$('#forma-prefacturas-overlay').fadeOut(remove);
							
						}
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-prefacturas-window').find('.div_one').css({'height':'550px'});//sin errores
						$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':'578px'});//con errores
						$('#forma-prefacturas-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						$grid_productos.find('#pres').css({'background' : '#ffffff'});
						
						$('#forma-prefacturas-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-prefacturas-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-prefacturas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								
								if(tmp.split(':')[0].substring(0, 7) == 'adenda2'){
									//alert(tmp.split(':')[1]);
									$warning_adenda.val(tmp.split(':')[1]);
								}
								
								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i) || (tmp.split(':')[0]=='presentacion'+i)){
											$('#forma-prefacturas-window').find('.prefacturas_div_one').css({'height':'578px'});
											$('#forma-prefacturas-window').find('#div_warning_grid').css({'display':'block'});
											
											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
												$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											if(tmp.split(':')[0].substring(0, 5) == 'costo'){
												$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											if(tmp.split(':')[0].substring(0, 12) == 'presentacion'){
												$grid_productos.find('input[name=presentacion]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											var tr_warning = '<tr>';
												tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
												tr_warning += '<td width="120">';
												tr_warning += '<input type="text" value="'+$grid_productos.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="200">';
												tr_warning += '<input type="text" value="'+$grid_productos.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="235">';
												tr_warning += '<input type="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:230px; color:red">';
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
				
				var options = {datatype :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					var flujo_proceso = entry['datosPrefactura'][0]['proceso_flujo_id'];
					$folio_pedido.val(entry['datosPrefactura'][0]['folio_pedido']);
					
					$id_prefactura.val(entry['datosPrefactura'][0]['id']);
					$refacturar.val(entry['datosPrefactura'][0]['refacturar']);
					$id_cliente.val(entry['datosPrefactura'][0]['cliente_id']);
					$no_cliente.val(entry['datosPrefactura'][0]['numero_control']);
					$razon_cliente.val(entry['datosPrefactura'][0]['razon_social']);
					$rfc.val(entry['datosPrefactura'][0]['rfc']);
					$id_df.val(entry['datosPrefactura'][0]['df_id']);
					$dir_cliente.val(entry['datosPrefactura'][0]['direccion']);
					$observaciones.text(entry['datosPrefactura'][0]['observaciones']);
					$observaciones_original.val(entry['datosPrefactura'][0]['observaciones']);
                    $orden_compra.val(entry['datosPrefactura'][0]['orden_compra']);
                    $orden_compra_original.val(entry['datosPrefactura'][0]['orden_compra']);
                    $tasa_ret_immex.val(entry['datosPrefactura'][0]['tasa_retencion_immex']);
                    $empresa_immex.val(entry['datosPrefactura'][0]['empresa_immex']);
                    
					$cta_mn.val(entry['datosPrefactura'][0]['cta_pago_mn']);
					$cta_usd.val(entry['datosPrefactura'][0]['cta_pago_usd']);
					$no_cuenta.val(entry['datosPrefactura'][0]['no_cuenta']);
					
                    $pdescto.val(entry['datosPrefactura'][0]['pdescto']);
                    $motivo_descuento.val(entry['datosPrefactura'][0]['mdescto']);
                    
                    $check_incluye_adenda.attr('checked',  (entry['Extras'][0]['adenda'] == 'true')? true:false );
                    
					$check_incluye_adenda.click(function(event){
						//Esto es para evitar que le quiten la seleccion cuando incluye adenda.
						if(entry['Extras']['0']['adenda'] == 'true'){
							this.checked=true;
						}
					});
                    
                    
                    //Ocultar check y boton de la adenda, cuando el cliente no incluya adenda.
					if(entry['Extras']['0']['adenda'] == 'false'){
						$adenda.hide();
					}else{
						//Numero de adenda
						if(parseInt(entry['datosPrefactura']['0']['adenda_id'])==1){
							//3=Factura de Remisión
							if(parseInt(entry['datosPrefactura']['0']['tipo_documento'])!=3){
								//Si el tipo de documento es diferente de 3, hay que ocultar. 
								//Esto porque para la adenda 1, es obligatorio que exista una Remision para Facturar con Adenda.
								$adenda.hide();
							}
							
							if(parseInt(entry['datosAdenda'].length)>0){
								$campo1.val(entry['datosAdenda'][0]['valor1']);//NoEntrada
								$campo2.val(entry['datosAdenda'][0]['valor2']);//NoRemision
								$campo3.val(entry['datosAdenda'][0]['valor3']);//Consignacion
								$campo4.val(entry['datosAdenda'][0]['valor4']);//CentroCostos
								$campo5.val(entry['datosAdenda'][0]['valor5']);//FechaInicio
								$campo6.val(entry['datosAdenda'][0]['valor6']);//FechaFin
								$campo7.val(entry['datosAdenda'][0]['valor7']);//Orden Compra
								$campo8.val(entry['datosAdenda'][0]['valor8']);//Moneda
							}else{
								$campo7.val(entry['datosPrefactura'][0]['orden_compra']);
								$campo8.val(entry['datosPrefactura'][0]['moneda2']);
							}
						}
						
						//Numero de adenda
						if(parseInt(entry['datosPrefactura']['0']['adenda_id'])==2){
							//Ocultar campos
							$adenda.hide();
							
							//Habilitar campo Orden de Compra
							$orden_compra.attr("readonly", false);
							
						}
						
					}
                    
                    
					$agregarDatosAdenda.click(function(event){
						$cargaFormaDatosAdenda(entry['datosPrefactura']['0']['adenda_id'], $campo1, $campo2, $campo3, $campo4, $campo5, $campo6, $campo7, $campo8);
					});
                    
                    
					$select_tmov.children().remove();
					var tmov_hmtl = '<option value="0">[--- ---]</option>';
					if(entry['TMov']){
						if(parseInt(entry['datosPrefactura'][0]['tmov_id'])>0){
							tmov_hmtl='';
						}
						$.each(entry['TMov'],function(entryIndex,mov){
							if(parseInt(mov['id'])==parseInt(entry['datosPrefactura'][0]['tmov_id'])){
								tmov_hmtl += '<option value="'+ mov['id'] +'" selected="yes">'+ mov['titulo'] + '</option>';
							}else{
								tmov_hmtl += '<option value="'+ mov['id'] +'">'+ mov['titulo'] + '</option>';
							}
						});
					}
					$select_tmov.append(tmov_hmtl);
                    
                    
                    
					//cargar select de tipos de movimiento
					var elemento_seleccionado = entry['datosPrefactura']['0']['tipo_documento'];
					var mostrar_opciones = 'false';
					if(parseInt(flujo_proceso)==2 || parseInt(flujo_proceso)==7 || parseInt(flujo_proceso)==8){
						if(parseInt(elemento_seleccionado)!=3){
							mostrar_opciones = 'true';
						}
					}
					
					$carga_select_con_arreglo_fijo($select_tipo_documento, array_select_documento, elemento_seleccionado, mostrar_opciones);
					
                    
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosPrefactura']['0']['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
							$moneda_original.val(moneda['id']);
						}else{
							if(parseInt(flujo_proceso)==2 || parseInt(flujo_proceso)==7 || parseInt(flujo_proceso)==8){
								moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							}
						}
					});
					$select_moneda.append(moneda_hmtl);
					
                    
					//$campo_tc.val();
					//$id_impuesto.val();
					//$valor_impuesto.val();
					//$campo_tc.val(entry['tc']['tipo_cambio']);
					$id_impuesto.val(entry['iva']['0']['id_impuesto']);
					$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
					
					//carga select de vendedores
					$select_vendedor.children().remove();
					var hmtl_vendedor;
					$.each(entry['Vendedores'],function(entryIndex,vendedor){
						if(entry['datosPrefactura']['0']['empleado_id'] == vendedor['id']){
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
					$select_vendedor.find('option').clone().appendTo($select_vendedor_original);
					
					
					//carga select de condiciones
					$select_condiciones.children().remove();
					var hmtl_condiciones;
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['datosPrefactura']['0']['terminos_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							/*
							if(parseInt(flujo_proceso)==2){
								hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
							}
							*/
						}
					});
					$select_condiciones.append(hmtl_condiciones);
					$select_condiciones.find('option').clone().appendTo($select_condiciones_original);
					
					var valor_metodo = entry['datosPrefactura']['0']['fac_metodos_pago_id'];
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo;
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(valor_metodo == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '"  selected="yes">' + metodo['titulo'] + '</option>';
						}else{
							/*
							if(parseInt(flujo_proceso)==2){
								hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
							}
							*/
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					$select_metodo_pago.find('option').clone().appendTo($select_metodo_pago_original);
					
					var valor_id_almacen = entry['datosPrefactura']['0']['id_almacen'];
					
					//carga select de almacen de la venta
					$select_almacen.children().remove();
					var hmtl_alm;
					$.each(entry['Almacenes'],function(entryIndex,alm){
						if(valor_id_almacen == alm['id']){
							hmtl_alm += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
						}
					});
					$select_almacen.append(hmtl_alm);
					
					//metodo pago 2=Tarjeta de credito, 3=tarjeta de debito
					if(parseInt(valor_metodo)==2 || parseInt(valor_metodo)==3){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura de los digitos de la tarjeta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						//quitar propiedad de solo lectura
						//$no_cuenta.removeAttr('readonly');
						
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						$etiqueta_digit.val('Ingrese los ultimos 4 Digitos de la Tarjeta');
					}
					
					
					if(parseInt(valor_metodo)==4 || parseInt(valor_metodo)==5){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
					if(parseInt(valor_metodo)==1 || parseInt(valor_metodo)==6){
						$no_cuenta.val('');
						if(!$no_cuenta.is(':disabled')) {
							$no_cuenta.attr('disabled','-1');
						}
						if(!$etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.attr('disabled','-1');
						}
					}
					
					
					if(parseInt(entry['datosGrid'].length) > 0){
						$.each(entry['datosGrid'],function(entryIndex,prod){
							var importePartida = 0;
							var importeImpuesto = 0;
							var importeIeps = 0;
							var importeRetencionIva = 0;
							var precio_u_con_descto = 0;
							var valor_descto = 0;
							var importe_del_descuento = 0;
							var importe_con_descto = 0;
							
							//Verificar si esta en proceso de 2=Facturacion
							if(parseInt(flujo_proceso)==2 || parseInt(flujo_proceso)==7 || parseInt(flujo_proceso)==8){
								importePartida = parseFloat(prod['cant_pendiente']) * parseFloat(prod['precio_unitario']);
							}else{
								importePartida = prod['importe'];
							}
							
							//Redondear a 4 digitos el importe de la partida
							importePartida = parseFloat(importePartida).toFixed(4);
							
							if($pdescto.val().trim()=='true' && parseFloat(prod['descto'])>0){
								valor_descto = prod['descto'];
								precio_u_con_descto = parseFloat( parseFloat(prod['precio_unitario']) - (parseFloat(prod['precio_unitario'])*(parseFloat(valor_descto)/100)) ).toFixed(4);
								
								importe_del_descuento = parseFloat(parseFloat(importePartida)*(parseFloat(prod['descto'])/100)).toFixed(4);
								importe_con_descto = parseFloat(parseFloat(importePartida)-parseFloat(importe_del_descuento)).toFixed(4);
								
								importeIeps = parseFloat(parseFloat(importe_con_descto) * (parseFloat(prod['valor_ieps'])/100)).toFixed(4);
								importeImpuesto = (parseFloat(importe_con_descto) + parseFloat(importeIeps)) * parseFloat(prod['valor_imp']);
								
								if(parseFloat(prod['ret_tasa'])>0){
									importeRetencionIva = parseFloat(parseFloat(importe_con_descto) * (parseFloat(prod['ret_tasa'])/100)).toFixed(4);
								}
							}else{
								importeIeps = parseFloat(parseFloat(importePartida) * (parseFloat(prod['valor_ieps'])/100)).toFixed(4);
								importeImpuesto = parseFloat(parseFloat(parseFloat(importePartida) + parseFloat(importeIeps)) * parseFloat(prod['valor_imp'])).toFixed(4);
								if(parseFloat(prod['ret_tasa'])>0){
									importeRetencionIva = parseFloat(parseFloat(importePartida) * (parseFloat(prod['ret_tasa'])/100)).toFixed(4);
								}
							}
							
							//obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
									trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
									trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
									trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';//este es el id del registro que ocupa el producto en la tabla prefacturas_detalles
									trr += '<input type="hidden" name="id_remision" value="0" 	id="id_rem">';
									trr += '<input type="hidden" name="costo_promedio" id="costprom" value="'+ prod['costo_prom'] +'">';
									//trr += '<span id="elimina">1</span>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['producto_id'] +'">';
									trr += '<input type="text" name="sku'+ tr +'" value="'+ prod['sku'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
								trr += '<input type="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<input type="hidden" name="idUnidad" id="idUnidad" value="'+prod['unidad_id']+'">';
								trr += '<input type="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<input type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
									trr += '<input type="text" 	name="presentacion" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
									//trr += '<select name="select_pres" class="selectPres'+ tr +'" style="width:96px;"></select>';
							trr += '</td>';
							
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" 	name="cantPedido" value="'+  prod['cant_pedido'] +'" 	id="cantped" class="borde_oculto" style="width:76px;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" 	name="cantFacturado" value="'+  prod['cant_facturado'] +'" 	id="cantfac" class="borde_oculto" style="width:76px;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" 	name="cantidad" value="'+  prod['cant_pendiente'] +'" 	id="cant" class="cant'+ tr +'" style="width:76px;">';
								trr += '<input type="hidden" name="cantPendiente" value="'+  prod['cant_pendiente'] +'" class="cantPendiente'+ tr +'" >';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" class="borde_oculto" style="width:76px; text-align:right;">';
								trr += '<input type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
								
								trr += '<input type="hidden" 	name="vdescto" id="vdescto" value="'+ valor_descto +'">';
								trr += '<input type="hidden" 	name="pu_descto" id="pu_descto" value="'+ precio_u_con_descto +'">';
								
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<input type="text" 	name="importe'+ tr +'" 	value="'+ importePartida +'" 	id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
								trr += '<input type="hidden"    name="id_imp_prod"  value="'+  prod['tipo_impuesto_id'] +'" id="idimppord">';
								trr += '<input type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" 		id="ivalorimp">';
								
								trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+ importeImpuesto +'">';
								
								trr += '<input type="hidden" name="importe_del_descto" id="importe_del_descto" value="'+ importe_del_descuento +'">';
								trr += '<input type="hidden" name="importe_con_descto" id="importe_con_descto" value="'+ importe_con_descto +'">';
								
								trr += '<input type="hidden" name="ret_id" 		id="ret_id" value="'+  prod['ret_id'] +'">';
								trr += '<input type="hidden" name="ret_tasa" 	id="ret_tasa" value="'+  prod['ret_tasa'] +'">';
								trr += '<input type="hidden" name="ret_importe" id="ret_importe" value="'+ importeRetencionIva +'">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="50">';
								trr += '<input type="hidden" name="idIeps"     value="'+ prod['ieps_id'] +'" id="idIeps">';
								trr += '<input type="text" name="tasaIeps" value="'+ prod['valor_ieps'] +'" class="borde_oculto" id="tasaIeps" style="width:46px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="62">';
								trr += '<input type="text" name="importeIeps" value="'+ importeIeps +'" class="borde_oculto" id="importeIeps" style="width:58px; text-align:right;" readOnly="true">';
							trr += '</td>';
							trr += '</tr>';
							$grid_productos.append(trr);
                            
                            
							//quitar enter a todos los campos input del grid
							$grid_productos.find('input').keypress(function(e){
								if(e.which==13 ) {
									return false;
								}
							});
                            
                            
							//validar campo cantidad, solo acepte numeros y punto
							$grid_productos.find('input.cant'+tr).keypress(function(e){
								// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
								if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
									return true;
								}else {
									return false;
								}
							});
                            
                            
                            //if((parseInt(flujo_proceso)==2 || parseInt(flujo_proceso)==7 || parseInt(flujo_proceso)==8) && parseInt(entry['datosPrefactura'][0]['tipo_documento'])!=3 && prod['facturado']!='true'){
                            if((parseInt(flujo_proceso)==2 || parseInt(flujo_proceso)==7 || parseInt(flujo_proceso)==8) && prod['facturado']!='true'){
								//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
								$grid_productos.find('input.cant'+tr).focus(function(e){
									if($(this).val().trim() == ''){
										$(this).val('');
									}else{
										if(parseFloat($(this).val()) <=0 ){
											$(this).val('');
										}
									}
								});
								
								
								//Al perder el enfoque, si el campo quedo vacio se le asigna un cero
								$grid_productos.find('input.cant'+tr).blur(function(e){
									var $facturar = $(this);
									var $disponible = $grid_productos.find('input.cantPendiente'+tr);
									var $costo_unitario = $(this).parent().parent().find('input[name=costo]');
									var $importe = $grid_productos.find('input[name=importe'+ tr +']');
									var $importe_impuesto = $grid_productos.find('input[name=totimpuesto'+ tr +']');
									var $tasa_impuesto = $(this).parent().parent().find('input[name=valor_imp]');
									
									var $campoTasaIeps = $(this).parent().parent().find('#tasaIeps');
									var $importeIeps = $(this).parent().parent().find('#importeIeps');
									
									var $ret_tasa = $(this).parent().parent().find('#ret_tasa');
									var $ret_importe = $(this).parent().parent().find('#ret_importe');
									
									var $vdescto = $(this).parent().parent().find('#vdescto');
									var $pu_con_descto = $(this).parent().parent().find('#pu_descto');
									var $importe_del_descto = $(this).parent().parent().find('#importe_del_descto');
									var $importe_con_descto = $(this).parent().parent().find('#importe_con_descto');
									
									if($facturar.val().trim() == ''){
										$facturar.val(0);
									}
									
									//Redondear de acuerdo al numero de decimales de la Unidad de Medida del Producto
									$facturar.val(parseFloat($facturar.val()).toFixed(parseInt(prod['no_dec'])));
									
									if(parseFloat($facturar.val()) > parseFloat($disponible.val())){
										jAlert('La cantidad a '+$boton_facturar.val()+' para &eacute;sta partida no debe ser mayor a la cantidad pendiente.\nPendiente='+$disponible.val()+'\n'+$boton_facturar.val()+'='+$facturar.val(), 'Atencion!', function(r) { 
											$facturar.val($disponible.val());
											$importe.val(parseFloat(parseFloat($facturar.val()) * parseFloat($costo_unitario.val())).toFixed(4));
											
											$importeIeps.val(parseFloat(parseFloat($importe.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
											
											$importe_impuesto.val(parseFloat((parseFloat($importe.val()) + parseFloat($importeIeps.val())) * parseFloat($tasa_impuesto.val())).toFixed(4));
											
											if(parseFloat($ret_tasa.val())>0){
												//Calcular la retencion de la partida
												$ret_importe.val(parseFloat(parseFloat($importe.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
											}
											
											if($pdescto.val().trim()=='true'){
												if(parseFloat($vdescto.val())>0){
													$pu_con_descto.val(parseFloat(parseFloat($costo_unitario.val()) - (parseFloat($costo_unitario.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
													$importe_del_descto.val(parseFloat(parseFloat($importe.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
													$importe_con_descto.val(parseFloat(parseFloat($importe.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
													
													//Calcular y redondear el importe del IEPS, tomando el importe con descuento
													$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
													
													//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
													$importe_impuesto.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $tasa_impuesto.val() ));
													
													if(parseFloat($ret_tasa.val())>0){
														//Calcular la retencion del importe con descuento
														$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
													}
												}
											}
											
											//Llamada a la funcion que calcula totales
											$calcula_totales();
											$facturar.focus();
										});
									}else{
										
										if(parseFloat($facturar.val()) < 0){
											jAlert('La cantidad a '+$boton_facturar.val()+' para &eacute;sta partida no debe ser igual a cero.\nPendiente='+$disponible.val()+'\n'+$boton_facturar.val()+'='+$facturar.val(), 'Atencion!', function(r) { 
												$facturar.val($disponible.val());
												$importe.val(parseFloat(parseFloat($facturar.val()) * parseFloat($costo_unitario.val())).toFixed(4));
												$importeIeps.val(parseFloat(parseFloat($importe.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
												$importe_impuesto.val(parseFloat((parseFloat($importe.val()) + parseFloat($importeIeps.val())) * parseFloat($tasa_impuesto.val())).toFixed(4));
												
												if(parseFloat($ret_tasa.val())>0){
													//Calcular la retencion de la partida
													$ret_importe.val(parseFloat(parseFloat($importe.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
												}
												
												if($pdescto.val().trim()=='true'){
													if(parseFloat($vdescto.val())>0){
														$pu_con_descto.val(parseFloat(parseFloat($costo_unitario.val()) - (parseFloat($costo_unitario.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
														$importe_del_descto.val(parseFloat(parseFloat($importe.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
														$importe_con_descto.val(parseFloat(parseFloat($importe.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
														
														//Calcular y redondear el importe del IEPS, tomando el importe con descuento
														$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
														
														//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
														$importe_impuesto.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $tasa_impuesto.val() ));
														
														if(parseFloat($ret_tasa.val())>0){
															//Calcular la retencion del importe con descuento
															$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
														}
													}
												}
												
												//Llamada a la funcion que calcula totales
												$calcula_totales();
												
												$facturar.focus();
											});
										}else{
											$importe.val(parseFloat(parseFloat($facturar.val()) * parseFloat($costo_unitario.val())).toFixed(4));
											$importeIeps.val(parseFloat(parseFloat($importe.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
											$importe_impuesto.val(parseFloat((parseFloat($importe.val()) + parseFloat($importeIeps.val())) * parseFloat($tasa_impuesto.val())).toFixed(4));
											
											if(parseFloat($ret_tasa.val())>0){
												//Calcular la retencion de la partida
												$ret_importe.val(parseFloat(parseFloat($importe.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
											}
											
											if($pdescto.val().trim()=='true'){
												if(parseFloat($vdescto.val())>0){
													$pu_con_descto.val(parseFloat(parseFloat($costo_unitario.val()) - (parseFloat($costo_unitario.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
													$importe_del_descto.val(parseFloat(parseFloat($importe.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
													$importe_con_descto.val(parseFloat(parseFloat($importe.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
													
													//Calcular y redondear el importe del IEPS, tomando el importe con descuento
													$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
													
													//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
													$importe_impuesto.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $tasa_impuesto.val() ));
													
													if(parseFloat($ret_tasa.val())>0){
														//Calcular la retencion del importe con descuento
														$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
													}
												}
											}
											
											//Llamada a la funcion que calcula totales
											$calcula_totales();
										}
									}
								});
                            }
                            
                            
							/*
							2=FACTURACION
							7=FAC PARCIAL
							8=REM PARCIAL
							*/
							//Verificar si esta en proceso de 2=Facturacion
							if(parseInt(flujo_proceso)==2 || parseInt(flujo_proceso)==7 || parseInt(flujo_proceso)==8){
								/*
								1=Factura
								2=Remision
								3=Factura de Remision
								*/
								/*
								if(parseInt(entry['datosPrefactura'][0]['tipo_documento'])==3){
									//Si es facturacion de Remision, establecemos el campo en solo lectura
									$grid_productos.find('input.cant'+tr).attr("readonly", true);
									$grid_productos.find('input.cant'+tr).css({'background' : '#F0F0F0'});
								}else{
								*/
									if(prod['facturado']=='true'){
										//Si la partida ya esta facturada en su totalidad, establecemos el campo en solo lectura
										$grid_productos.find('input.cant'+tr).attr("readonly", true);
										$grid_productos.find('input.cant'+tr).css({'background' : '#F0F0F0'});
									}
								//}
							}else{
								//Si el flujo es diferente de Facturacion, establecemos el campo en solo lectura
								$grid_productos.find('input.cant'+tr).attr("readonly", true);
								$grid_productos.find('input.cant'+tr).css({'background' : '#F0F0F0'});
							}
                            
                            
                            /*
							//cargar select de presentaciones de cada producto
							$grid_productos.find('select.selectPres'+ tr).children().remove();
							var moneda_hmtl = '';
							$.each(entry['Pres'],function(entryIndex,pres){
								if(parseInt(prod['producto_id'])==parseInt(pres['producto_id'])){
									
									if(entry['Extras'][0]['controlExiPres']=='true'){
										if(entry['Extras'][0]['validaPresPedido']=='true'){
											if(parseInt(prod['id_presentacion'])==parseInt(pres['presentacion_id'])){
												moneda_hmtl += '<option value="' + pres['presentacion_id'] + '" selected="yes">' + pres['presentacion'] + '</option>';
											}
										}else{
											if(parseInt(prod['id_presentacion'])==parseInt(pres['presentacion_id'])){
												moneda_hmtl += '<option value="' + pres['presentacion_id'] + '" selected="yes">' + pres['presentacion'] + '</option>';
											}else{
												if(parseInt(flujo_proceso)==2){
													if(parseInt(entry['datosPrefactura']['0']['tipo_documento'])!=3){
														moneda_hmtl += '<option value="' + pres['presentacion_id'] + '"  >' + pres['presentacion'] + '</option>';
													}
												}
											}
										}
									}else{
										//solo se debe cargar la presentacion asignada desde el pedido
										if(parseInt(prod['id_presentacion'])==parseInt(pres['presentacion_id'])){
											moneda_hmtl += '<option value="' + pres['presentacion_id'] + '" selected="yes">' + pres['presentacion'] + '</option>';
										}
									}
									
								}
							});
							$grid_productos.find('select.selectPres'+ tr).append(moneda_hmtl);
							*/
						});
					}
					
					$calcula_totales();//llamada a la funcion que calcula totales 
					
					//si es refacturacion, no se puede cambiar los datos del grid, solo el header de la factura
					if(entry['datosPrefactura']['0']['refacturar']=='true'){
						$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
						$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
					}
					
					/*
					2=FACTURACION
					7=FAC PARCIAL
					8=REM PARCIAL
					*/
					//Verificar flujo del proceso
					if(parseInt(flujo_proceso)==2 || parseInt(flujo_proceso)==7 || parseInt(flujo_proceso)==8){
						$boton_facturar.show();
					}
					
					//proceso_flujo_id 3=Facturado,5=Remision
					if(parseInt(flujo_proceso)==3 || parseInt(flujo_proceso)==5){
						$tipo_cambio.val(entry['datosPrefactura']['0']['tipo_cambio']);
						$tipo_tipo_cambio_original.val(entry['datosPrefactura']['0']['tipo_cambio']);
					}else{
						$tipo_cambio.val(entry['datosPrefactura']['0']['tipo_cambio']);
						$tipo_tipo_cambio_original.val(entry['datosPrefactura']['0']['tipo_cambio']);
					}
					
					//$observaciones.attr("readonly", true);
					//$tipo_cambio.attr("readonly", true);
					//$orden_compra.attr("readonly", true);
					//$digitos.attr("readonly", true);
					
					$busca_cliente.hide();
					//$busca_sku.hide();
					//$agregar_producto.hide();
					
					$grid_productos.find('a[href*=elimina_producto]').hide();
					$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
					
					
					
					//Cambiar el tipo de Documento
					$select_tipo_documento.change(function(){
						var tipo = $(this).val();
						if(parseInt(tipo)==1){
							$boton_facturar.val('Facturar');
							//Mostrar check y boton de la adenda, cuando el cliente si incluye adenda.
							if(entry['Extras']['0']['adenda'] == 'true'){
								//Numero de adenda
								if(parseInt(entry['datosPrefactura']['0']['adenda_id'])==1){
									//3=Factura de Remisión
									if(parseInt(entry['datosPrefactura']['0']['tipo_documento'])==3){
										//Si el tipo de documento es diferente de 3, hay que ocultar. 
										//Esto porque para la adenda 1, es obligatorio que exista una Remision para Facturar con Adenda.
										$adenda.show();
									}
								}
							}
						}else{
							$boton_facturar.val('Remisionar');
							//Ocultar check y boton de la adenda, cuando el cliente si incluye adenda.
							if(entry['Extras']['0']['adenda'] == 'true'){
								$adenda.hide();
							}
						}
					});
					
					
					
					
					
					$boton_facturar.click(function(event){
						var tipo = '';
						var ejecutar=true;
						
						if( parseInt($select_tipo_documento.val())==1  ||  parseInt($select_tipo_documento.val())==3 ) {
							tipo = 'Facturacion';
						}
						
						if( parseInt($select_tipo_documento.val())==2 ) {
							tipo = 'Remision';
						}
						
						
						if( parseInt($select_tipo_documento.val())==1  ||  parseInt($select_tipo_documento.val())==3 ) {
							//Ocultar check y boton de la adenda, cuando el cliente no incluya adenda.
							if(entry['Extras'][0]['adenda'] == 'true'){
								//Numero de adenda
								if(parseInt(entry['datosPrefactura'][0]['adenda_id'])==1){
									//1=Factura
									if(parseInt(entry['datosPrefactura'][0]['tipo_documento'])==1 || parseInt(entry['datosPrefactura'][0]['tipo_documento'])==0){
										//Confirmacion para facturar sin Remision para adenda 1
										ejecutar=false;
									}
								}
							}
						}
						
						
						jConfirm('Confirmar '+tipo+'?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) {
								if(ejecutar){
									$submit_actualizar.trigger('click');
								}else{
									
									jAlert('Es necesario crear primero una Remisi&oacute;n para poder agregar la adenda a la Factura.', 'Atencion!', function(r) { 
										$boton_facturar.focus();
									});
									
									/*
									//Confirm para Facturar si Remsion
									jConfirm('Es necesario crear primero una Remisi&oacute;n para poder agregar la adenda a la Factura.\nDesea Facturar si Remisi&oacute;n?', 'Dialogo de Confirmacion', function(r) {
										// If they confirmed, manually trigger a form submission
										if (r) {
											$submit_actualizar.trigger('click');
										}
									});
									*/
								}
							}else{
								//aqui no hay nada
							}
						});
						
					});
					
					
				});//termina llamada json
                
				
				
				
				
				$select_moneda.change(function(){
					var moneda_id = $(this).val();
					//alert(moneda_id);
					$convertir_costos($tipo_cambio,moneda_id,$subtotal,$impuesto,$total,$valor_impuesto,$grid_productos);
				});
				
				$tipo_cambio.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}		
				});
				
				
				//Aplicar tipo de cambio a todos los costos al cambiar valor de tipo de cambio
				$tipo_cambio.blur(function(){
					$grid_productos.find('tr').each(function (index){
						var precio_cambiado=0;
						var importe_cambiado=0;
						if($(this).find('#cost').val() != ' '){
							//si la moneda inicial de la prefactura es diferente a la moneda actual seleccionada
							//entonces recalculamos los costos de acuerdo al tipo de cambio
							if( parseInt($moneda_original.val()) != parseInt($select_moneda.val()) ){
								if(parseInt($moneda_original.val())==1){
									//si la moneda original es pesos, calculamos su equivalente a dolares
									precio_cambiado = parseFloat($(this).find('#costor').val()) / parseFloat($tipo_cambio.val());
								}else{
									//si la moneda original es dolar, calculamos su equivalente a pesos
									precio_cambiado = parseFloat($(this).find('#costor').val()) * parseFloat($tipo_cambio.val());
								}
								$(this).find('#cost').val(parseFloat(precio_cambiado).toFixed(4));
								importe_cambiado = parseFloat($(this).find('#cant').val()) * parseFloat($(this).find('#cost').val());
								$(this).find('#import').val(parseFloat(importe_cambiado).toFixed(4));
							}else{
								//aqui no se cambia porque es la misma moneda en la que se hizo la prefactura, asi que no se aplica tipo de cambio
							}
						}
					});
					$calcula_totales();//llamada a la funcion que calcula totales
				});
				
				
				
				//buscador de clientes
				$busca_cliente.click(function(event){
					event.preventDefault();
					$busca_clientes();
				});
				
				/*
				//buscador de productos
				$busca_sku.click(function(event){
					event.preventDefault();
					//if(parseInt($select_almacen.val()) != 0){
						$busca_productos($sku_producto.val());
					//}else{
					//	jAlert("Es necesario seleccionar un almacen", 'Atencion!');
					//}
				});
				
				
				//agregar producto al grid
				$agregar_producto.click(function(event){
					event.preventDefault();
					//if($sku_producto.val() != ''){
						//$agrega_producto_grid($sku_producto,$grid_productos);
						$buscador_presentaciones_producto($id_cliente, $no_cliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio);
					//}else{
					//	jAlert("Es necesario ingresar un sku valido", '¡ Atencion !');
					//}
					
				});
				
				
				//ejecutar clic del href Agregar producto al pulsar enter en el campo sku del producto
				$sku_producto.keypress(function(e){
					
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
					
				});
				*/
				
				
                
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
					if(parseInt(trCount) > 0){
						return true;
					}else{
						jAlert("No hay datos para actualizar", 'Atencion!');
						return false;
					}
				});
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-prefacturas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-prefacturas-overlay').fadeOut(remove);
				});
				
			}
			
			$observaciones.focus();
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPrefacturas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getPrefacturas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaPrefacturas00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



