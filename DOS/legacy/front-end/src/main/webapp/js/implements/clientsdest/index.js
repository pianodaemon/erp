$(function() {
	String.prototype.toCharCode = function(){
		var str = this.split(''), len = str.length, work = new Array(len);
		for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
		}
		return work.join(',');
	};
	
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
	
	
	$aplicar_evento_focus_input = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val() == ' ' || parseFloat($(this).val()) <= 0){
				$(this).val('');
			}
		});
	}
	
	$aplicar_evento_blur_input = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if(parseFloat($campo_input.val())==0 || $campo_input.val()==""){
				$campo_input.val(0);
			}
			$campo_input.val(parseFloat($campo_input.val()).toFixed(2))
		});
	}
	
	
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
        
	var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/clientsdest";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_clientsdest = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Destinatarios');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_razon_social = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_razon_social]');
	var $busqueda_rfc = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_rfc]');
	var $busqueda_select_tipo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_tipo]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "destinatario" + signo_separador + $busqueda_razon_social.val() + "|";
		valor_retorno += "rfc" + signo_separador + $busqueda_rfc.val() + "|";
		valor_retorno += "tipo" + signo_separador + $busqueda_select_tipo.val() + "|";
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
	
	//Alimentando select de Tipo de destinatario
	$busqueda_select_tipo.children().remove();
	var tipo_hmtl = '<option value="0">[--Seleccionar Tipo--]</option>';
	tipo_hmtl += '<option value="1">Nacional</option>';
	tipo_hmtl += '<option value="2">Extranjero</option>';
	$busqueda_select_tipo.append(tipo_hmtl);
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_folio.val('');
		$busqueda_razon_social.val('');
		$busqueda_rfc.val('');
		
		//Alimentando select de Tipo de destinatario
		$busqueda_select_tipo.children().remove();
		tipo_hmtl = '<option value="0">[--Seleccionar Tipo--]</option>';
		tipo_hmtl += '<option value="1">Nacional</option>';
		tipo_hmtl += '<option value="2">Extranjero</option>';
		$busqueda_select_tipo.append(tipo_hmtl);
		
		$busqueda_folio.focus();
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
		$busqueda_folio.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_razon_social, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_rfc, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_tipo, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-clientsdest-window').find('#submit').mouseover(function(){
			$('#forma-clientsdest-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-clientsdest-window').find('#submit').mouseout(function(){
			$('#forma-clientsdest-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-clientsdest-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-clientsdest-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-clientsdest-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-clientsdest-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-clientsdest-window').find('#close').mouseover(function(){
			$('#forma-clientsdest-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-clientsdest-window').find('#close').mouseout(function(){
			$('#forma-clientsdest-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-clientsdest-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-clientsdest-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-clientsdest-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-clientsdest-window').find("ul.pestanas li").click(function() {
			$('#forma-clientsdest-window').find(".contenidoPes").hide();
			$('#forma-clientsdest-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-clientsdest-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	//Aplicar solo lectura
	$aplica_read_only_input_text = function($campo){
		$campo.attr("readonly", true);
		$campo.css({'background' : '#f0f0f0'});
	}
	
	//Quitar solo lectura a campo
	$quitar_readonly_input = function($input){
		$input.css({'background' : '#ffffff'});
		$input.attr('readonly',false);
	}
	
	
	
	//Agregar los datos del cliente seleccionado
	$agregarDatosClienteSeleccionado = function($id_cliente, $no_cliente, $razon_cliente, id_cliente, no_control, razon_social, $busca_cliente){
		$id_cliente.val(id_cliente);
		$no_cliente.val(no_control);
		$razon_cliente.val(razon_social);
		
		//Aplicar solo lectura al campo
		$aplica_read_only_input_text($razon_cliente);
		
		//Ocultar link de busqueda
		$busca_cliente.hide();
		
		//Asignar enfoque
		$no_cliente.focus();
	}
	
	
	//Agregar los datos del cliente seleccionado
	$vaciarCamposCliente = function($id_cliente, $no_cliente, $razon_cliente, $busca_cliente){
		$id_cliente.val(0);
		$no_cliente.val('');
		$razon_cliente.val('');
		
		//Quitar solo lectura a campo
		$quitar_readonly_input($razon_cliente);
			
		//Mostrar link de busqueda
		$busca_cliente.show();
		
		//Asignar enfoque
		$no_cliente.focus();
	}



	//Buscador de clientes
	$busca_clientes = function($id_cliente, $nocliente, $razoncliente, id_user, $busca_cliente){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscacliente();
		var $dialogoc =  $('#forma-buscacliente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
		$('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -190});
		
		var $tabla_resultados = $('#forma-buscacliente-window').find('#tabla_resultado');
		var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('#busca_cliente_modalbox');
		var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscacliente-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscacliente-window').find('select[name=filtropor]');
		
		//Funcionalidad botones
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
		
		if($nocliente.val().trim() != ''){
			//asignamos el numero de control al campo de busqueda
			$cadena_buscar.val($nocliente.val());
			if($razoncliente.val().trim() == ''){
				html+='<option value="1" selected="yes">No. de control</option>';
			}else{
				html+='<option value="1">No. de control</option>';
			}
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if($razoncliente.val().trim() != ''){
			//asignamos la Razon Social del Cliente al campo Nombre
			$cadena_buscar.val($razoncliente.val());
			html+='<option value="3" selected="yes">Razon social</option>';
		}else{
			if($razoncliente.val().trim()=='' && $nocliente.val().trim()==''){
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
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorClientes.json';
			$arreglo = {	'cadena':$cadena_buscar.val(),
							'filtro':$select_filtro_por.val(),
							'iu':id_user 
                        }
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Clientes'],function(entryIndex,cliente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
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
					var no_control = $(this).find('span.no_control').html();
					var razon_social = $(this).find('span.razon').html();
					
					$agregarDatosClienteSeleccionado($id_cliente, $nocliente, $razoncliente, id_cliente, no_control, razon_social, $busca_cliente);
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					
					//Asignar el enfoque al campo Numero de Control
					$nocliente.focus();
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
			$razoncliente.focus();
		});
	}
	//Termina buscador de clientes



	//Buscar datos de un cliente en especifico
	var $buscar_datos_de_cliente_especifico = function($id_cliente, $no_cliente, $razon_cliente, $busca_cliente){
		
		var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoClient.json';
		$arreglo2 = {'no_control':$no_cliente.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json2,$arreglo2,function(entry2){
			
			if(parseInt(entry2['Cliente'].length) > 0 ){
				var id_cliente = entry2['Cliente'][0]['id'];
				var no_control = entry2['Cliente'][0]['numero_control'];
				var razon_social = entry2['Cliente'][0]['razon_social'];
				
				$agregarDatosClienteSeleccionado($id_cliente, $no_cliente, $razon_cliente, id_cliente, no_control, razon_social, $busca_cliente);
			}else{
				$id_cliente.val(0);
				$no_cliente.val('');
				$razon_cliente.val('');
				
				jAlert('N&uacute;mero de cliente desconocido.\nEs necesario seleccionar un cliente.', 'Atencion!', function(r) { 
					$no_cliente.focus(); 
				});
			}
		},"json");//termina llamada json
	}
	
	
	
	
	
	//nuevo 
	$new_clientsdest.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_clientsdest();
		
		var form_to_show = 'formaDestinatarios';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-clientsdest-window').css({"margin-left": -410, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-clientsdest-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-clientsdest-window').find('input[name=identificador]');
		var $select_tipo = $('#forma-clientsdest-window').find('select[name=select_tipo]');
		var $destinatario = $('#forma-clientsdest-window').find('input[name=destinatario]');
		var $folio = $('#forma-clientsdest-window').find('input[name=folio]');
		var $folio_ext = $('#forma-clientsdest-window').find('input[name=folio_ext]');
		var $rfc = $('#forma-clientsdest-window').find('input[name=rfc]');
		var $calle = $('#forma-clientsdest-window').find('input[name=calle]');
		var $numero_int = $('#forma-clientsdest-window').find('input[name=numero_int]');
		var $numero_ext = $('#forma-clientsdest-window').find('input[name=numero_ext]');
		var $colonia = $('#forma-clientsdest-window').find('input[name=colonia]');
		var $select_pais = $('#forma-clientsdest-window').find('select[name=select_pais]');
		var $select_estado = $('#forma-clientsdest-window').find('select[name=select_estado]');
		var $select_municipio = $('#forma-clientsdest-window').find('select[name=select_municipio]');
		var $email = $('#forma-clientsdest-window').find('input[name=email]');
		var $tel1 = $('#forma-clientsdest-window').find('input[name=tel1]');
		var $ext1 = $('#forma-clientsdest-window').find('input[name=ext1]');
		var $tel2 = $('#forma-clientsdest-window').find('input[name=tel2]');
		var $check_firma = $('#forma-clientsdest-window').find('input[name=check_firma]');
		var $check_sello = $('#forma-clientsdest-window').find('input[name=check_sello]');
		var $check_efectivo = $('#forma-clientsdest-window').find('input[name=check_efectivo]');
		var $check_cheque = $('#forma-clientsdest-window').find('input[name=check_cheque]');
		var $select_serv = $('#forma-clientsdest-window').find('select[name=select_serv]');
		var $costo_serv = $('#forma-clientsdest-window').find('input[name=costo_serv]');
		
		var $razon_cliente = $('#forma-clientsdest-window').find('input[name=razon_cliente]');
		var $id_cliente = $('#forma-clientsdest-window').find('input[name=id_cliente]');
		var $no_cliente = $('#forma-clientsdest-window').find('input[name=no_cliente]');
		
		var $busca_cliente = $('#forma-clientsdest-window').find('a[href=#busca_cliente]');
		
		var $cerrar_plugin = $('#forma-clientsdest-window').find('#close');
		var $cancelar_plugin = $('#forma-clientsdest-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-clientsdest-window').find('#submit');
		
		//quitar enter a todos los campos input
		$('#forma-clientsdest-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$folio.css({'background' : '#DDDDDD'});
		//$rfc.css({'background' : '#DDDDDD'});
		$identificador.attr({'value' : 0});
		$permitir_solo_numeros($costo_serv);
		$aplicar_evento_focus_input($costo_serv);
		$aplicar_evento_blur_input($costo_serv);
		$costo_serv.val(parseFloat(0).toFixed(2));
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los datos se guardaron  con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-clientsdest-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-clientsdest-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-clientsdest-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDestinatario.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			//Alimentando select de Tipo de destinatario
			$select_tipo.children().remove();
			var tipo_hmtl = '<option value="1" selected="yes">Nacional</option>';
			tipo_hmtl += '<option value="2">Extranjero</option>';
			$select_tipo.append(tipo_hmtl);
			
			
			//Alimentando los campos select de las pais
			$select_serv.children().remove();
			var serv_hmtl = '<option value="0" selected="yes">[-Seleccionar Servicio-]</option>';
			$.each(entry['Servicios'],function(entryIndex,serv){
				serv_hmtl += '<option value="' + serv['id'] + '"  >' + serv['sku']+'-'+ serv['descripcion'] + '</option>';
			});
			$select_serv.append(serv_hmtl);
			
			
			//Alimentando los campos select de las pais
			$select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar Pais-]</option>';
			$.each(entry['Paises'],function(entryIndex,pais){
				pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
			});
			$select_pais.append(pais_hmtl);
			
			var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>';
			$select_estado.children().remove();
			$select_estado.append(entidad_hmtl);
			
			var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
			$select_municipio.children().remove();
			$select_municipio.append(localidad_hmtl);
			
			
			//carga select estados al cambiar el pais
			$select_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$select_estado.children().remove();
					var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
					$.each(entry['Estados'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$select_estado.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
					$select_municipio.children().remove();
					$select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			//carga select municipios al cambiar el estado
			$select_estado.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $select_pais.val();
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$select_municipio.children().remove();
					var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
					$.each(entry['Municipios'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
		},"json");//termina llamada json
        
        
        
        
        
		//Buscador de clientes
		$busca_cliente.click(function(event){
			event.preventDefault();
			$busca_clientes($id_cliente, $no_cliente, $razon_cliente, $('#lienzo_recalculable').find('input[name=iu]').val(), $busca_cliente);
		});
		
		//Asignar evento keypress al campo Razon Social del cliente para abrir el buscador al pulsar enter sobre este campo
		$(this).aplicarEventoKeypressEjecutaTrigger($razon_cliente, $busca_cliente);
		
		$no_cliente.keypress(function(e){
			var valor=$(this).val();
			
			if(e.which == 13){
				//Llamada a funcion para la busqueda de datos de un cliente en especifico
				$buscar_datos_de_cliente_especifico($id_cliente, $no_cliente, $razon_cliente, $busca_cliente);
				
				return false;
			}else{
				if (parseInt(e.which) == 8) {
					//Si se oprime la tecla borrar se vacía el campo no_economico 
					if(parseInt(valor.length)>0 && parseInt($id_cliente.val())>0){
						jConfirm('Seguro que desea cambiar el Cliente seleccionado?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) {
								//LLamada a la funcion para vaciar campos
								$vaciarCamposCliente($id_cliente, $no_cliente, $razon_cliente, $busca_cliente);
							}else{
								$no_cliente.val(valor);
								$no_cliente.focus();
							}
						});
					}else{
						$no_cliente.focus();
					}
				}
			}
		});
		
		
		
		//Buscar datos del cliente al perder el enfoque
		$no_cliente.blur(function(e){
			var valor=$(this).val().trim();
			
			if(valor!='' && parseInt($id_cliente.val())==0){
				//Llamada a funcion para la busqueda de datos de un cliente en especifico
				$buscar_datos_de_cliente_especifico($id_cliente, $no_cliente, $razon_cliente, $busca_cliente);
				
				return false;
			}
		});
		
		
		
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-clientsdest-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-clientsdest-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$folio_ext.focus();
	});
	
	
	
	
	
	var carga_formaDestinatarios_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Destinatario?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Destinatario fue eliminado exitosamente.", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Destinatario no pudo ser eliminado.", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaDestinatarios';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_clientsdest();
			$('#forma-clientsdest-window').css({"margin-left": -410, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-clientsdest-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $identificador = $('#forma-clientsdest-window').find('input[name=identificador]');
			var $select_tipo = $('#forma-clientsdest-window').find('select[name=select_tipo]');
			var $destinatario = $('#forma-clientsdest-window').find('input[name=destinatario]');
			var $folio = $('#forma-clientsdest-window').find('input[name=folio]');
			var $folio_ext = $('#forma-clientsdest-window').find('input[name=folio_ext]');
			var $rfc = $('#forma-clientsdest-window').find('input[name=rfc]');
			var $calle = $('#forma-clientsdest-window').find('input[name=calle]');
			var $numero_int = $('#forma-clientsdest-window').find('input[name=numero_int]');
			var $numero_ext = $('#forma-clientsdest-window').find('input[name=numero_ext]');
			var $colonia = $('#forma-clientsdest-window').find('input[name=colonia]');
			var $cp = $('#forma-clientsdest-window').find('input[name=cp]');
			var $select_pais = $('#forma-clientsdest-window').find('select[name=select_pais]');
			var $select_estado = $('#forma-clientsdest-window').find('select[name=select_estado]');
			var $select_municipio = $('#forma-clientsdest-window').find('select[name=select_municipio]');
			var $email = $('#forma-clientsdest-window').find('input[name=email]');
			var $tel1 = $('#forma-clientsdest-window').find('input[name=tel1]');
			var $ext1 = $('#forma-clientsdest-window').find('input[name=ext1]');
			var $tel2 = $('#forma-clientsdest-window').find('input[name=tel2]');
			var $check_firma = $('#forma-clientsdest-window').find('input[name=check_firma]');
			var $check_sello = $('#forma-clientsdest-window').find('input[name=check_sello]');
			var $check_efectivo = $('#forma-clientsdest-window').find('input[name=check_efectivo]');
			var $check_cheque = $('#forma-clientsdest-window').find('input[name=check_cheque]');
			
			var $select_serv = $('#forma-clientsdest-window').find('select[name=select_serv]');
			var $costo_serv = $('#forma-clientsdest-window').find('input[name=costo_serv]');
			
			var $razon_cliente = $('#forma-clientsdest-window').find('input[name=razon_cliente]');
			var $id_cliente = $('#forma-clientsdest-window').find('input[name=id_cliente]');
			var $no_cliente = $('#forma-clientsdest-window').find('input[name=no_cliente]');
			
			var $busca_cliente = $('#forma-clientsdest-window').find('a[href=#busca_cliente]');
			
			var $cerrar_plugin = $('#forma-clientsdest-window').find('#close');
			var $cancelar_plugin = $('#forma-clientsdest-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-clientsdest-window').find('#submit');
			
			$folio.css({'background' : '#DDDDDD'});
			//$destinatario.css({'background' : '#DDDDDD'});
			//$rfc.css({'background' : '#DDDDDD'});
			
			$folio.attr({ 'readOnly':true });
			//$destinatario.attr({ 'readOnly':true });
			$permitir_solo_numeros($costo_serv);
			$aplicar_evento_focus_input($costo_serv);
			$aplicar_evento_blur_input($costo_serv);
			
			
			if(accion_mode == 'edit'){
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDestinatario.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("Los datos se guardaron con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-clientsdest-overlay').fadeOut(remove);
						//refresh_table();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-clientsdest-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-clientsdest-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
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
					$identificador.attr({'value' : entry['Datos'][0]['identificador']});
					$destinatario.attr({'value' : entry['Datos'][0]['destinatario']});
					$folio.attr({'value' : entry['Datos'][0]['folio']});
					$folio_ext.attr({'value' : entry['Datos'][0]['f_ext']});
					$rfc.attr({'value' : entry['Datos'][0]['rfc']});
					$calle.attr({'value' : entry['Datos'][0]['calle']});
					$numero_int.attr({'value' : entry['Datos'][0]['no_int']});
					$numero_ext.attr({'value' : entry['Datos'][0]['no_ext']});
					$colonia.attr({'value' : entry['Datos'][0]['colonia']});
					$cp.attr({'value' : entry['Datos'][0]['cp']});
					$email.attr({'value' : entry['Datos'][0]['email']});
					$tel1.attr({'value' : entry['Datos'][0]['telefono1']});
					$ext1.attr({'value' : entry['Datos'][0]['extension']});
					$tel2.attr({'value' : entry['Datos'][0]['telefono2']});
					$check_firma.attr('checked', (entry['Datos'][0]['sfirma']==true)? true:false );
					$check_sello.attr('checked', (entry['Datos'][0]['ssello']==true)? true:false );
					$check_efectivo.attr('checked', (entry['Datos'][0]['sefectivo']==true)? true:false );
					$check_cheque.attr('checked', (entry['Datos'][0]['scheque']==true)? true:false );
					
					$razon_cliente.attr({'value' : entry['Datos'][0]['cliente']});
					$id_cliente.attr({'value' : entry['Datos'][0]['cliente_id']});
					$no_cliente.attr({'value' : entry['Datos'][0]['no_control']});
					
					$costo_serv.attr({'value' : entry['Datos'][0]['serv_costo']});
					
					//Alimentando los campos select de Servicios para maniobras
					$select_serv.children().remove();
					var serv_hmtl = '<option value="0">[-Seleccionar Servicio-]</option>';
					$.each(entry['Servicios'],function(entryIndex,serv){
						if(parseInt(entry['Datos'][0]['serv_id'])==parseInt(serv['id'])){
							serv_hmtl += '<option value="' + serv['id'] + '" selected="yes">' + serv['sku']+'-'+ serv['descripcion'] + '</option>';
						}else{
							serv_hmtl += '<option value="' + serv['id'] + '">' + serv['sku']+'-'+ serv['descripcion'] + '</option>';	
						}
					});
					$select_serv.append(serv_hmtl);
					
					
					if(parseInt(entry['Datos'][0]['cliente'])>0){
						$busca_cliente.hide();
					}
					
					var tipo_hmtl='';
					if(parseInt(entry['Datos'][0]['tipo'])==0){
						tipo_hmtl = '<option value="0">Seleccionar Tipo</option>';
					}else{
						if(parseInt(entry['Datos'][0]['tipo'])==1){
							tipo_hmtl = '<option value="1" selected="yes">Nacional</option>';
							tipo_hmtl += '<option value="2">Extranjero</option>';
						}else{
							tipo_hmtl = '<option value="1">Nacional</option>';
							tipo_hmtl += '<option value="2" selected="yes">Extranjero</option>';
						}
					}
					//Alimentando select de Tipo de destinatario
					$select_tipo.children().remove();
					$select_tipo.append(tipo_hmtl);
					
					//Alimentando los campos select de las pais
					$select_pais.children().remove();
					var pais_hmtl = '<option value="0" >[-Seleccionar Pais-]</option>';
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Datos']['0']['pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_pais.append(pais_hmtl);
					
					//Alimentando los campos select del estado
					$select_estado.children().remove();
					var entidad_hmtl = '<option value="00"  >[-Seleccionar Estado--]</option>';
					$.each(entry['Estados'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Datos']['0']['estado_id']){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_estado.append(entidad_hmtl);
					
					
					//Alimentando los campos select de los municipios
					$select_municipio.children().remove();
					var localidad_hmtl = '<option value="00" >[-Seleccionar Municipio-]</option>';
					$.each(entry['Municipios'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Datos']['0']['municipio_id']){
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_municipio.append(localidad_hmtl);
					
					
					//carga select estados al cambiar el pais
					$select_pais.change(function(){
						var valor_pais = $(this).val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
						$arreglo = {'id_pais':valor_pais};
						$.post(input_json,$arreglo,function(entry){
							$select_estado.children().remove();
							var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
							$.each(entry['Estados'],function(entryIndex,entidad){
								entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
							});
							$select_estado.append(entidad_hmtl);
							
							var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
							$select_municipio.children().remove();
							$select_municipio.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					//carga select municipios al cambiar el estado
					$select_estado.change(function(){
						var valor_entidad = $(this).val();
						var valor_pais = $select_pais.val();
						
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
						$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
						$.post(input_json,$arreglo,function(entry){
							$select_municipio.children().remove();
							var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
							$.each(entry['Municipios'],function(entryIndex,mun){
								trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
							});
							$select_municipio.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
				},"json");//termina llamada json
				
				
				
				//Buscador de clientes
				$busca_cliente.click(function(event){
					event.preventDefault();
					$busca_clientes($id_cliente, $no_cliente, $razon_cliente, $('#lienzo_recalculable').find('input[name=iu]').val(), $busca_cliente);
				});
				
				//Asignar evento keypress al campo Razon Social del cliente para abrir el buscador al pulsar enter sobre este campo
				$(this).aplicarEventoKeypressEjecutaTrigger($razon_cliente, $busca_cliente);
				
				$no_cliente.keypress(function(e){
					var valor=$(this).val();
					
					if(e.which == 13){
						//Llamada a funcion para la busqueda de datos de un cliente en especifico
						$buscar_datos_de_cliente_especifico($id_cliente, $no_cliente, $razon_cliente, $busca_cliente);
						
						return false;
					}else{
						if (parseInt(e.which) == 8) {
							//Si se oprime la tecla borrar se vacía el campo no_economico 
							if(parseInt(valor.length)>0 && parseInt($id_cliente.val())>0){
								jConfirm('Seguro que desea cambiar el Cliente seleccionado?', 'Dialogo de Confirmacion', function(r) {
									// If they confirmed, manually trigger a form submission
									if (r) {
										//LLamada a la funcion para vaciar campos
										$vaciarCamposCliente($id_cliente, $no_cliente, $razon_cliente, $busca_cliente);
									}else{
										$no_cliente.val(valor);
										$no_cliente.focus();
									}
								});
							}else{
								$no_cliente.focus();
							}
						}
					}
				});
				
				
				
				//Buscar datos del cliente al perder el enfoque
				$no_cliente.blur(function(e){
					var valor=$(this).val().trim();
					
					if(valor!='' && parseInt($id_cliente.val())==0){
						//Llamada a funcion para la busqueda de datos de un cliente en especifico
						$buscar_datos_de_cliente_especifico($id_cliente, $no_cliente, $razon_cliente, $busca_cliente);
						
						return false;
					}
				});
				
				
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsdest-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsdest-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
				$folio_ext.focus();
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllClientsDest.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllClientsDest.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDestinatarios_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
