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
	var controller = $contextpath.val()+"/controllers/clientsdescuentos";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_clientsdescuentos = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Descuentos de Clientes');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_nocontrol = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_nocontrol]');
	var $busqueda_razon_social = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_razon_social]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "nocontrol" + signo_separador + $busqueda_nocontrol.val() + "|";
		valor_retorno += "razonsoc" + signo_separador + $busqueda_razon_social.val() + "|";
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	//$cadena_busqueda = cadena;
	
	$buscar.click(function(event){
		event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_nocontrol.val('');
		$busqueda_razon_social.val('');
		$busqueda_nocontrol.focus();
	});
	
	
	TriggerClickVisializaBuscador = 0;

	$visualiza_buscador.click(function(event){
		event.preventDefault();
		
		var alto=0;
		if(TriggerClickVisializaBuscador==0){
			 TriggerClickVisializaBuscador=1;
			 var height2 = $('#cuerpo').css('height');

			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
			 $('#barra_buscador').animate({height: '60px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});

		}else{
			 TriggerClickVisializaBuscador=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
		};
		$busqueda_nocontrol.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_nocontrol, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_razon_social, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-clientsdescuentos-window').find('#submit').mouseover(function(){
			$('#forma-clientsdescuentos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-clientsdescuentos-window').find('#submit').mouseout(function(){
			$('#forma-clientsdescuentos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-clientsdescuentos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-clientsdescuentos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-clientsdescuentos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-clientsdescuentos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-clientsdescuentos-window').find('#close').mouseover(function(){
			$('#forma-clientsdescuentos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-clientsdescuentos-window').find('#close').mouseout(function(){
			$('#forma-clientsdescuentos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-clientsdescuentos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-clientsdescuentos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-clientsdescuentos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-clientsdescuentos-window').find("ul.pestanas li").click(function() {
			$('#forma-clientsdescuentos-window').find(".contenidoPes").hide();
			$('#forma-clientsdescuentos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-clientsdescuentos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
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
	
	
	
	//Buscador de clientes
	$busca_clientes = function($razoncliente, $nocliente, $id_cliente, $busca_cliente, $identificador){
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
		
		if($nocliente.val() !='' && $razoncliente.val()==''){
			html+='<option value="1" selected="yes">No. de control</option>';
			$cadena_buscar.val($nocliente.val());
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if($razoncliente.val()!=''){
			$cadena_buscar.val($razoncliente.val());
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		if($nocliente.val() =='' && $razoncliente.val()==''){
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_clientes.json';
			$arreglo = {'cadena':$cadena_buscar.val(),
						 'filtro':$select_filtro_por.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Clientes'],function(entryIndex,cliente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
							trr += '<input type="hidden" id="direccion" value="'+cliente['direccion']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+cliente['moneda_id']+'">';
							trr += '<input type="hidden" id="moneda" value="'+cliente['moneda']+'">';
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
				$razoncliente.val($(this).find('span.razon').html());
				$nocliente.val($(this).find('span.no_control').html());
				$id_cliente.val($(this).find('#idclient').val());
				
				//elimina la ventana de busqueda
				var remove = function() {$(this).remove();};
				$('#forma-buscacliente-overlay').fadeOut(remove);
				
				$busca_cliente.hide();
				$razoncliente.focus();
				$campo_valor.focus();
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
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
			
			$('#forma-clientsdf-window').find('input[name=cliente]').focus();
		});
		
		$cadena_buscar.focus();
	}//termina buscador de clientes

	
	
	//Vaciar campos
	$vaciar_campos = function($id_cliente,$nocliente,$razoncliente, $busca_cliente){
		$id_cliente.val(0);
		$nocliente.val('');
		$razoncliente.val('');
		$busca_cliente.show();
	}
	
	//nuevo clientsdescuentos
	$new_clientsdescuentos.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_clientsdescuentos();
		
		var form_to_show = 'formaClientsDescuentos';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-clientsdescuentos-window').css({"margin-left": -400, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-clientsdescuentos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-clientsdescuentos-window').find('input[name=identificador]');
		var $razoncliente = $('#forma-clientsdescuentos-window').find('input[name=razoncliente]');
		var $id_cliente = $('#forma-clientsdescuentos-window').find('input[name=id_cliente]');
		var $nocliente = $('#forma-clientsdescuentos-window').find('input[name=nocliente]');
		var $campo_valor = $('#forma-clientsdescuentos-window').find('input[name=valor]');
		var $busca_cliente = $('#forma-clientsdescuentos-window').find('a[href*=busca_cliente]');
		
		var $cerrar_plugin = $('#forma-clientsdescuentos-window').find('#close');
		var $cancelar_plugin = $('#forma-clientsdescuentos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-clientsdescuentos-window').find('#submit');
		
		//Quitar enter a todos los campos input
		$('#forma-clientsdescuentos-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		
		$nocliente.focus();
		//$permitir_solo_numeros($nocliente);
        $permitir_solo_numeros($campo_valor);
		
		//$folio.css({'background' : '#DDDDDD'});
		$id_cliente.attr({'value' : 0});
		$identificador.attr({'value' : 0});
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los datos se guardaron  con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-clientsdescuentos-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-clientsdescuentos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-clientsdescuentos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getClientsDescuentos.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		/*
		$.post(input_json,$arreglo,function(entry){
			//Aqui va los datos de la respuesta de la peticion json
			
		},"json");//termina llamada json
        */
        
		//Llamada al buscador de clientes
		$busca_cliente.click(function(event){
			event.preventDefault();
			$busca_clientes($razoncliente, $nocliente, $id_cliente, $busca_cliente, $identificador);
		});
		
		$(this).aplicarEventoKeypressEjecutaTrigger($razoncliente, $busca_cliente);
		
		
		
		//Agregar datos del Cliente
		agregarDatosClient = function(){
			var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoClient.json';
			$arreglo2 = {'no_control':$nocliente.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
			
			$.post(input_json2,$arreglo2,function(entry2){
				if(parseInt(entry2['Cliente'].length) > 0 ){
					var id_cliente = entry2['Cliente'][0]['id'];
					var no_control = entry2['Cliente'][0]['numero_control'];
					var razon_social = entry2['Cliente'][0]['razon_social'];
					
					$razoncliente.val(razon_social);
					$nocliente.val(no_control);
					$id_cliente.val(id_cliente);
					$busca_cliente.hide();

					
				}else{
					$id_cliente.val(0);
					$nocliente.val('');
					$razoncliente.val('');
					jAlert('N&uacute;mero de cliente desconocido.', 'Atencion!', function(r) { 
					$nocliente.focus(); 
					});
				}
			},"json");//termina llamada json
		}
		

		$nocliente.keypress(function(e){
			var valor=$(this).val();
			if(e.which == 13){
				//Agrega datos del cliente
				agregarDatosClient();
				$campo_valor.focus();
				return false;
			
			}
		});
        
        
		$submit_actualizar.bind('click',function(){
		});
        
        
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-clientsdescuentos-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-clientsdescuentos-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$nocliente.focus();
		//$campo_valor.focus();
	});
	
	
	
	
	
	var carga_formaDirecciones_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Descuento?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Descuento fue eliminado exitosamente.", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Descuento no pudo ser eliminado.", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaClientsDescuentos';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_clientsdescuentos();
			$('#forma-clientsdescuentos-window').css({"margin-left": -400, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-clientsdescuentos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $identificador = $('#forma-clientsdescuentos-window').find('input[name=identificador]');
			var $razoncliente = $('#forma-clientsdescuentos-window').find('input[name=razoncliente]');
			var $id_cliente = $('#forma-clientsdescuentos-window').find('input[name=id_cliente]');
			var $nocliente = $('#forma-clientsdescuentos-window').find('input[name=nocliente]');
			var $campo_valor = $('#forma-clientsdescuentos-window').find('input[name=valor]');
			var $busca_cliente = $('#forma-clientsdescuentos-window').find('a[href*=busca_cliente]');
			
			var $cerrar_plugin = $('#forma-clientsdescuentos-window').find('#close');
			var $cancelar_plugin = $('#forma-clientsdescuentos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-clientsdescuentos-window').find('#submit');
			
			$nocliente.css({'background' : '#DDDDDD'});
			$razoncliente.css({'background' : '#DDDDDD'});
			
			$nocliente.attr({ 'readOnly':true });
			$razoncliente.attr({ 'readOnly':true });
			$busca_cliente.hide();
			//$permitir_solo_numeros($nocliente);
			$permitir_solo_numeros($campo_valor);
			
			//Quitar enter a todos los campos input
			$('#forma-clientsdescuentos-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getClientsDescuentos.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("Los datos se actualizaron con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-clientsdescuentos-overlay').fadeOut(remove);
						//refresh_table();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-clientsdescuentos-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-clientsdescuentos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.attr({'value' : entry['Descuentos']['0']['id']});
					$id_cliente.attr({'value' : entry['Descuentos']['0']['cxc_clie_id']});
					$razoncliente.attr({'value' : entry['Descuentos']['0']['razon_social']});
					$nocliente.attr({'value' : entry['Descuentos']['0']['numero_control']});
					$campo_valor.attr({'value' : entry['Descuentos']['0']['valor']});
					
				},"json");//termina llamada json
				
	
				
				$submit_actualizar.bind('click',function(){
				});
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsdescuentos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsdescuentos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllgetClientsDescuentos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllgetClientsDescuentos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
