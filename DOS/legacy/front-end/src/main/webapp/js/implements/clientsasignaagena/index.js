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
	var controller = $contextpath.val()+"/controllers/clientsasignaagena";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_clientsasignaagena = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Asignaci&oacute;n de Agentes Aduanales');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_nocontrol = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_nocontrol]');
	var $busqueda_razon_social = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_razon_social]');
	var $busqueda_rfc = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_rfc]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "nocontrol" + signo_separador + $busqueda_nocontrol.val() + "|";
		valor_retorno += "razonsoc" + signo_separador + $busqueda_razon_social.val() + "|";
		valor_retorno += "rfc" + signo_separador + $busqueda_rfc.val() + "|";
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
		$busqueda_rfc.val('');
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
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_rfc, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-clientsasignaagena-window').find('#submit').mouseover(function(){
			$('#forma-clientsasignaagena-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-clientsasignaagena-window').find('#submit').mouseout(function(){
			$('#forma-clientsasignaagena-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-clientsasignaagena-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-clientsasignaagena-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-clientsasignaagena-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-clientsasignaagena-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-clientsasignaagena-window').find('#close').mouseover(function(){
			$('#forma-clientsasignaagena-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-clientsasignaagena-window').find('#close').mouseout(function(){
			$('#forma-clientsasignaagena-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-clientsasignaagena-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-clientsasignaagena-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-clientsasignaagena-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-clientsasignaagena-window').find("ul.pestanas li").click(function() {
			$('#forma-clientsasignaagena-window').find(".contenidoPes").hide();
			$('#forma-clientsasignaagena-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-clientsasignaagena-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	
	//Buscador de clientes
	$busca_clientes = function($razoncliente, $nocliente, $id_cliente, $busca_cliente, $agregar_cliente,  $grid_agenaduanales, $identificador){
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
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorClientes.json';
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
					
					//Llamada a la funcion que busca remitentes asigados al cliente seleccionado
					$destinatarios_asignados($(this).find('#idclient').val(), $grid_agenaduanales, $identificador);
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					
					$busca_cliente.hide();
					$agregar_cliente.hide();
					$razoncliente.focus();
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
	
	
	
	
	//Buscador de Agentes Aduanales
	$busca_agentes_aduanales= function($nombre_agena, $noagena, $grid_agenaduanales, idCliente){
		$(this).modalPanel_buscaagen();
		var $dialogoc =  $('#forma-buscaagen-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_buscaagen').find('table.formaBusqueda_buscaagen').clone());
		$('#forma-buscaagen-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscaagen-window').find('#tabla_resultado');
		
		var $boton_buscaagen = $('#forma-buscaagen-window').find('#boton_buscaagen');
		var $cancelar_busqueda = $('#forma-buscaagen-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscaagen-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscaagen-window').find('select[name=filtropor]');
		
		//funcionalidad botones
		$boton_buscaagen.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_buscaagen.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_busqueda.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$cancelar_busqueda.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		var html = '';		
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		
		if($noagena.val() !='' && $nombre_agena.val()==''){
			html+='<option value="1" selected="yes">No. de control</option>';
			$cadena_buscar.val($noagena.val());
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if($nombre_agena.val()!=''){
			$cadena_buscar.val($nombre_agena.val());
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		if($noagena.val() =='' && $nombre_agena.val()==''){
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		$select_filtro_por.append(html);
		
		//click buscar clientes
		$boton_buscaagen.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorAgenA.json';
			$arreglo = {'cadena':$cadena_buscar.val(),
						 'filtro':$select_filtro_por.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Agentes'],function(entryIndex,agen){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="id" value="'+agen['id']+'">';
							trr += '<span class="no_control">'+agen['folio']+'</span>';
						trr += '</td>';
						//trr += '<td width="145"><span class="rfc">'+agen['rfc']+'</span></td>';
						trr += '<td width="520"><span class="razon">'+agen['razon_social']+'</span></td>';
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
				
				//Seleccionar un elemento del resultado
				$tabla_resultados.find('tr').click(function(){
					var idDetalle=0;
					var idAgena = $(this).find('#id').val();
					var noControl = $(this).find('span.no_control').html();
					var razonSocial = $(this).find('span.razon').html();
					
					//LLamada a la funcion que agrega tr al grid
					$agrega_agente_aduanal_grid($grid_agenaduanales, idDetalle, idCliente, idAgena, noControl, razonSocial);
					
					//Elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaagen-overlay').fadeOut(remove);
					
					//Limpiar campos
					$nombre_agena.val('');
					$noagena.val('');

					$nombre_agena.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$boton_buscaagen.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $boton_buscaagen);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $boton_buscaagen);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-buscaagen-overlay').fadeOut(remove);
			
			//$('#forma-clientsdf-window').find('input[name=cliente]').focus();
		});
		
		$cadena_buscar.focus();
	}//Termina buscador de Agentes Aduanales
	
	
	
	
	//agregar producto al grid
	$agrega_agente_aduanal_grid = function($grid_agenaduanales, idDetalle, idCliente, idAgena, noControl, razonSocial){
		var encontrado = 0;
		//Busca el remitente en el grid para asegurar que no haya sido asignado al cliente
		$grid_agenaduanales.find('tr').each(function (index){
			if( parseInt($(this).find('#idagen').val()) == parseInt(idAgena) ){
				//El remitente ya esta en el grid
				encontrado=1;
			}
		});
		
		if(parseInt(encontrado)!=1){
			//Si el remitente no esta en el grid entra aqui
			
			//Obtiene numero de trs
			var tr = $("tr", $grid_agenaduanales).size();
			tr++;
			
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
					trr += '<input type="hidden" 	name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
					trr += '<input type="hidden" 	name="iddet" id="iddet" value="'+idDetalle+'">';//este es el id del registro que ocupa el producto en la tabla clientsasignaagena_detalles
					trr += '<input type="hidden" 	name="noTr" value="'+ tr +'">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
					trr += '<input type="hidden" 	name="idcli" id="idcli" value="'+ idCliente +'">';
					trr += '<input type="hidden" 	name="idagen" id="idagen" value="'+ idAgena +'">';
					trr += noControl;
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="520">';
					trr += razonSocial;
				trr += '</td>';
			trr += '</tr>';
			$grid_agenaduanales.append(trr);
			
			
			//elimina un producto del grid
			$grid_agenaduanales.find('#delete'+ tr).bind('click',function(event){
				event.preventDefault();
				$tr = $(this).parent().parent();
				
				//Tomar el id del detalle del elemento eliminado
				var idDet = $tr.find('#iddet').val();
				
				//Asigna espacios en blanco a todos los input de la fila eliminada
				$tr.find('input').val(' ');
				
				//Asigna un 0 al input eliminado como bandera para saber que esta eliminado
				$tr.find('#elim').val(0);
				//Asigna un 0 al input del id de Remitente
				$tr.find('#idagen').val(0);
				//Asigna un 0 al input del id del cliente
				$tr.find('#idcli').val(0);
				
				//Devolver valor al campo iddet
				$tr.find('#iddet').val(idDet);
				
				//Oculta la fila eliminada
				$(this).parent().parent().hide();
			});
			
			//Limpiar campos
			$('#forma-clientsasignaagena-window').find('input[name=noagena]').val('');
			$('#forma-clientsasignaagena-window').find('input[name=nombre_agena]').val('');
			
			//Asignar enfoque
			$('#forma-clientsasignaagena-window').find('input[name=noagena]').focus();
		}else{
			jAlert('El Agente Aduanal ya se encuentra en el listado, seleccione otro diferente.', 'Atencion!', function(r) { 
				//Limpiar campos
				$('#forma-clientsasignaagena-window').find('input[name=noagena]').val('');
				$('#forma-clientsasignaagena-window').find('input[name=nombre_agena]').val('');
			});
		}
	}//Termina agregar remitente al grid
	
	
	//Vaciar campos
	$vaciar_campos = function($id_cliente,$nocliente,$razoncliente, $grid_agenaduanales, $busca_cliente, $agregar_cliente){
		$id_cliente.val(0);
		$nocliente.val('');
		$razoncliente.val('');
		
		$grid_agenaduanales.children().remove();
		$busca_cliente.show();
		$agregar_cliente.show();
	}
	
	
	//Buscar remitentes asignados al cliente seleccionado
	$destinatarios_asignados = function(id_cliente, $grid_agenaduanales, $identificador){
		var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesAsignados.json';
		$arreglo2 = {'id':id_cliente, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json2,$arreglo2,function(entry2){
			//verificar que el arreglo traiga datos
			if(parseInt(entry2['Asignados'].length) > 0){
				//Aqui le asignamos el id del cliente para idicar que ya tiene remitentes asignados,
				//por lo tanto solo se debe actualizar el registro
				$identificador.val(id_cliente);
				$.each(entry2['Asignados'],function(entryIndex,grid){
					var idDetalle= grid['iddet'];
					var idCliente = grid['clie_id'];
					var idAgena = grid['agena_id'];
					var noControl = grid['no_control'];
					var razonSocial = grid['nombre'];
					
					//Llamada a la funcion para agregar remitente al grid
					$agrega_agente_aduanal_grid($grid_agenaduanales, idDetalle, idCliente, idAgena, noControl, razonSocial);
				});
			}
		},"json");//termina llamada json
	}
	
	
	
	
	
	
	//nuevo 
	$new_clientsasignaagena.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_clientsasignaagena();
		
		var form_to_show = 'formaAsignaAgentes';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-clientsasignaagena-window').css({"margin-left": -400, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-clientsasignaagena-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-clientsasignaagena-window').find('input[name=identificador]');
		var $razoncliente = $('#forma-clientsasignaagena-window').find('input[name=razoncliente]');
		var $id_cliente = $('#forma-clientsasignaagena-window').find('input[name=id_cliente]');
		var $nocliente = $('#forma-clientsasignaagena-window').find('input[name=nocliente]');
		
		var $busca_cliente = $('#forma-clientsasignaagena-window').find('a[href*=busca_cliente]');
		var $agregar_cliente = $('#forma-clientsasignaagena-window').find('a[href*=agregar_cliente]');
		
		var $nombre_agena = $('#forma-clientsasignaagena-window').find('input[name=nombre_agena]');
		var $noagena = $('#forma-clientsasignaagena-window').find('input[name=noagena]');
		
		var $busca_agena = $('#forma-clientsasignaagena-window').find('a[href*=busca_agena]');
		var $agregar_agena = $('#forma-clientsasignaagena-window').find('a[href*=agregar_agena]');
		
		//Grid de Remitentes
		var $grid_agenaduanales = $('#forma-clientsasignaagena-window').find('#grid_agenaduanales');
		//Grid de errores
		var $grid_warning = $('#forma-clientsasignaagena-window').find('#div_warning_grid').find('#grid_warning');
		
		var $cerrar_plugin = $('#forma-clientsasignaagena-window').find('#close');
		var $cancelar_plugin = $('#forma-clientsasignaagena-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-clientsasignaagena-window').find('#submit');
		
		//Quitar enter a todos los campos input
		$('#forma-clientsasignaagena-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		//$folio.css({'background' : '#DDDDDD'});
		$id_cliente.attr({'value' : 0});
		$identificador.attr({'value' : 0});
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los datos se guardaron  con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-clientsasignaagena-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-clientsasignaagena-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-clientsasignaagena-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAsignacion.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		/*
		$.post(input_json,$arreglo,function(entry){
			//Aqui va los datos de la respuesta de la peticion json
			
		},"json");//termina llamada json
        */
        
		//Llamada al buscador de clientes
		$busca_cliente.click(function(event){
			event.preventDefault();
			$busca_clientes($razoncliente, $nocliente, $id_cliente, $busca_cliente, $agregar_cliente, $grid_agenaduanales, $identificador);
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
					$agregar_cliente.hide();
					
					//Llamada a la cuncion que busca remitentes asigados al cliente seleccionado
					$destinatarios_asignados(id_cliente, $grid_agenaduanales, $identificador);
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
		
		//Agregar cliente
		$agregar_cliente.click(function(event){
			event.preventDefault();
			//Llamada a funcion que agrega los datos del cliente
			agregarDatosClient();
		});
		
		
		$nocliente.keypress(function(e){
			var valor=$(this).val();
			if(e.which == 13){
				//Agrega datos del cliente
				agregarDatosClient();
				return false;
			}else{
				if (parseInt(e.which) == 8) {
					//Si se oprime la tecla borrar se vacía el campo Numero de Control del Cliente
					//Tambien vaciamos el grid
					if(parseInt($("tr", $grid_agenaduanales).size())>0){
						//si hay elementos en el grid, preguntar si se desea cambiar el producto seleccionado
						jConfirm('Hay remitentes en el Listado, \n&eacute;sta seguro que desea cambiar el Cliente seleccionado?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) {
								//Llamada a la funcion para vaciar los campos
								$vaciar_campos($id_cliente,$nocliente,$razoncliente, $grid_agenaduanales, $busca_cliente, $agregar_cliente);
								$nocliente.focus();
								return true;
							}else{
								$nocliente.val(valor);
								$nocliente.focus();
							}
						});
					}else{
						//Si no hay elementos en el grid, vaciar sin preguntar
						$vaciar_campos($id_cliente,$nocliente,$razoncliente, $grid_agenaduanales, $busca_cliente, $agregar_cliente);
						$nocliente.focus();
					}
				}
			}
		});
        
        
		//Llamada al buscador de Remitentes
		$busca_agena.click(function(event){
			event.preventDefault();
			if($nocliente.val().trim()!='' ){
				$busca_agentes_aduanales($nombre_agena, $noagena, $grid_agenaduanales, $id_cliente.val());
			}else{
				jAlert('Es necesario seleccionar un cliente.', 'Atencion!', function(r) { 
					$nocliente.focus(); 
				});
			}
		});
		
		$(this).aplicarEventoKeypressEjecutaTrigger($nombre_agena, $busca_agena);
		
		
		//Agregar datos del Agente Aduanal
		agregarDatosAgen = function(){
			if($nocliente.val().trim()!='' ){
				var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoAgen.json';
				$arreglo2 = {'no_control':$noagena.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				$.post(input_json2,$arreglo2,function(entry2){
					if(parseInt(entry2['Agen'].length) > 0 ){
						var idDetalle=0;
						var idCliente = $id_cliente.val();
						var idAgena = entry2['Agen'][0]['id'];
						var noControl = entry2['Agen'][0]['folio'];
						var razonSocial = entry2['Agen'][0]['razon_social'];
						
						//Llamada a la funcion para agregar destinatario al grid
						$agrega_agente_aduanal_grid($grid_agenaduanales, idDetalle, idCliente, idAgena, noControl, razonSocial);
					}else{
						$noagena.val('');
						$nombre_agena.val('');
						jAlert('N&uacute;mero de Destinatario desconocido.', 'Atencion!', function(r) { 
							$noagena.focus(); 
						});
					}
				},"json");//termina llamada json
			}else{
				jAlert('Es necesario seleccionar un cliente.', 'Atencion!', function(r) { 
					$nocliente.focus(); 
				});
			}
		}
		
		
		
		$noagena.keypress(function(e){
			if(e.which == 13){
				//Llamada a funcion que agrega los datos del destinatario
				agregarDatosAgen();
				return false;
			}
		});
		
		//Agregar agente
		$agregar_agena.click(function(event){
			event.preventDefault();
			//Llamada a funcion que agrega los datos del destinatario
			agregarDatosAgen();
		});
		
        
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_agenaduanales).size();
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert('No hay datos para actualizar', 'Atencion!', function(r) { 
					$noagena.focus();
				});
				return false;
			}
		});
		
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-clientsasignaagena-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-clientsasignaagena-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$nocliente.focus();
	});
	
	
	
	
	
	var carga_formaDirecciones_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Agente Aduanal?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Agente Aduanal fue eliminado exitosamente.", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Agente Aduanal no pudo ser eliminado.", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaAsignaAgentes';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_clientsasignaagena();
			$('#forma-clientsasignaagena-window').css({"margin-left": -400, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-clientsasignaagena-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $identificador = $('#forma-clientsasignaagena-window').find('input[name=identificador]');
			var $razoncliente = $('#forma-clientsasignaagena-window').find('input[name=razoncliente]');
			var $id_cliente = $('#forma-clientsasignaagena-window').find('input[name=id_cliente]');
			var $nocliente = $('#forma-clientsasignaagena-window').find('input[name=nocliente]');
			
			var $busca_cliente = $('#forma-clientsasignaagena-window').find('a[href*=busca_cliente]');
			var $agregar_cliente = $('#forma-clientsasignaagena-window').find('a[href*=agregar_cliente]');
			
			var $nombre_agena = $('#forma-clientsasignaagena-window').find('input[name=nombre_agena]');
			var $noagena = $('#forma-clientsasignaagena-window').find('input[name=noagena]');
			
			var $busca_agena = $('#forma-clientsasignaagena-window').find('a[href*=busca_agena]');
			var $agregar_agena = $('#forma-clientsasignaagena-window').find('a[href*=agregar_agena]');
			
			//Grid de Remitentes
			var $grid_agenaduanales = $('#forma-clientsasignaagena-window').find('#grid_agenaduanales');
			//Grid de errores
			var $grid_warning = $('#forma-clientsasignaagena-window').find('#div_warning_grid').find('#grid_warning');
			
			var $cerrar_plugin = $('#forma-clientsasignaagena-window').find('#close');
			var $cancelar_plugin = $('#forma-clientsasignaagena-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-clientsasignaagena-window').find('#submit');
			
			$nocliente.css({'background' : '#DDDDDD'});
			$razoncliente.css({'background' : '#DDDDDD'});
			
			$nocliente.attr({ 'readOnly':true });
			$razoncliente.attr({ 'readOnly':true });
			
			$busca_cliente.hide();
			$agregar_cliente.hide();
			
			//Quitar enter a todos los campos input
			$('#forma-clientsasignaagena-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAsignacion.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("Los datos se guardaron con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-clientsasignaagena-overlay').fadeOut(remove);
						//refresh_table();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-clientsasignaagena-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-clientsasignaagena-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
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
					$identificador.attr({'value' : entry['Datos']['0']['identificador']});
					$id_cliente.attr({'value' : entry['Datos']['0']['identificador']});
					$razoncliente.attr({'value' : entry['Datos']['0']['razon_social']});
					$nocliente.attr({'value' : entry['Datos']['0']['numero_control']});
					
					//verificar que el arreglo traiga datos
					if(parseInt(entry['Grid'].length) > 0){
						$.each(entry['Grid'],function(entryIndex,grid){
							var idDetalle= grid['iddet'];
							var idCliente = grid['clie_id'];
							var idAgena = grid['agena_id'];
							var noControl = grid['no_control'];
							var razonSocial = grid['nombre'];
							//alert(idAgena);
							//Llamada a la funcion para agregar remitente al grid
							$agrega_agente_aduanal_grid($grid_agenaduanales, idDetalle, idCliente, idAgena, noControl, razonSocial);
						});
					}
				},"json");//termina llamada json
				
				
        
				//Llamada al buscador de Remitentes
				$busca_agena.click(function(event){
					event.preventDefault();
					if($nocliente.val().trim()!='' ){
						$busca_agentes_aduanales($nombre_agena, $noagena, $grid_agenaduanales, $id_cliente.val());
					}else{
						jAlert('Es necesario seleccionar un cliente.', 'Atencion!', function(r) { 
							$nocliente.focus(); 
						});
					}
				});
				
				$(this).aplicarEventoKeypressEjecutaTrigger($nombre_agena, $busca_agena);
				
				//Agregar datos del Agente Aduanal
				agregarDatosAgen = function(){
					if($nocliente.val().trim()!='' ){
						var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoAgen.json';
						$arreglo2 = {'no_control':$noagena.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
						
						$.post(input_json2,$arreglo2,function(entry2){
							if(parseInt(entry2['Agen'].length) > 0 ){
								var idDetalle=0;
								var idCliente = $id_cliente.val();
								var idAgena = entry2['Agen'][0]['id'];
								var noControl = entry2['Agen'][0]['folio'];
								var razonSocial = entry2['Agen'][0]['razon_social'];
								
								//Llamada a la funcion para agregar destinatario al grid
								$agrega_agente_aduanal_grid($grid_agenaduanales, idDetalle, idCliente, idAgena, noControl, razonSocial);
							}else{
								$noagena.val('');
								$nombre_agena.val('');
								jAlert('N&uacute;mero de Destinatario desconocido.', 'Atencion!', function(r) { 
									$noagena.focus(); 
								});
							}
						},"json");//termina llamada json
					}else{
						jAlert('Es necesario seleccionar un cliente.', 'Atencion!', function(r) { 
							$nocliente.focus(); 
						});
					}
				}
				
				
				
				$noagena.keypress(function(e){
					if(e.which == 13){
						//Llamada a funcion que agrega los datos del Agente Aduanal
						agregarDatosAgen();
						return false;
					}
				});
				
				//Agregar agente
				$agregar_agena.click(function(event){
					event.preventDefault();
					//Llamada a funcion que agrega los datos del Agente Aduanal
					agregarDatosAgen();
				});
				
				
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_agenaduanales).size();
					if(parseInt(trCount) > 0){
						return true;
					}else{
						jAlert('No hay datos para actualizar', 'Atencion!', function(r) { 
							$noagena.focus();
						});
						return false;
					}
				});
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsasignaagena-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsasignaagena-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
				$noagena.focus();
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllAsignacion.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllAsignacion.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
