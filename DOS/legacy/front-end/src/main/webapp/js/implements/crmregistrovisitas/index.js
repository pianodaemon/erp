$(function() {
	String.prototype.toCharCode = function(){
		var str = this.split(''), len = str.length, work = new Array(len);
		for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
		}
		return work.join(',');
	};
	
	
	var $autocomplete_input = function($tipo, $campo, json_input, iu){		
		$campo.autocomplete({
			source: function(request, response){
				var $arreglo = {'tipo':$tipo.val(),'cadena':$campo.val(),'iu':iu};
				
				$.post(json_input, $arreglo, function(data){
					response($.map(data, function(item) {
						return {
							label: item.titulo,
							value: item.id
						  }
					}))
				}, "json");
			},
			 minLength: 2,
			 dataType: "json",
			 cache: false,
			 focus: function(event, ui) {
				return false;
			 },
			 select: function(event, ui) {
				this.value = ui.item.label;
				return false;
			 }
		 });
	}
	
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
        
	var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/crmregistrovisitas";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_crmregistrovisitas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Registro de Visitas');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	//$('#barra_buscador').find('.tabla_buscador').find('#td_busqueda_cliente').append('<input type="text" name="busqueda_cliente" value="" style="width:200px;">');
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_tipo_visita = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_tipo_visita]');
	var $busqueda_contacto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_contacto]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_agente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
	var html = '';
	$busqueda_tipo_visita.children().remove();
	html='<option value="0">[-- Todos --]</option>';
	html+='<option value="1">Cliente</option>';
	html+='<option value="2">Prospecto</option>';
	$busqueda_tipo_visita.append(html);
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "tipo_visita" + signo_separador + $busqueda_tipo_visita.val() + "|";
		valor_retorno += "contacto" + signo_separador + $busqueda_contacto.val().trim() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val().trim() + "|";
		valor_retorno += "agente" + signo_separador + $busqueda_agente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val() + "|"
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val();
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
		$busqueda_folio.val('');
		$busqueda_contacto.val('');
		$busqueda_cliente.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_agente.find('option[index=0]').attr('selected','selected');
		
		$busqueda_tipo_visita.children().remove();
		var tipos_hmtl = '<option value="0" selected="yes">[-- Todos --]</option>';
		tipos_hmtl += '<option value="1">Cliente</option>';
		tipos_hmtl += '<option value="2">Prospecto</option>';
		$busqueda_tipo_visita.append(tipos_hmtl);
		
		//esto se hace para reinicar los valores del select de agentes
		var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
		$arreglo2 = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json2,$arreglo2,function(data){
			//Alimentando los campos select_agente
			$busqueda_agente.children().remove();
			var agente_hmtl = '';
			if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
				agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
			}
			
			$.each(data['Agentes'],function(entryIndex,agente){
				if(parseInt(agente['id'])==parseInt(data['Extra'][0]['id_agente'])){
					agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
				}else{
					//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
					if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
						agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
					}
				}
			});
			$busqueda_agente.append(agente_hmtl);
		});
		
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
			 
			 $busqueda_folio.focus();
		}else{
			 TriggerClickVisializaBuscador=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
			 
			 $visualiza_buscador.focus();
		};
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_tipo_visita, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_contacto, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_agente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	
	var input_json_agente = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_agente,$arreglo,function(data){
		//Alimentando los campos select_agente
		$busqueda_agente.children().remove();
		var agente_hmtl = '';
		if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
			agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
		}
		
		$.each(data['Agentes'],function(entryIndex,agente){
			if(parseInt(agente['id'])==parseInt(data['Extra'][0]['id_agente'])){
				agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
			}else{
				//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
				if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
					agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
				}
			}
		});
		$busqueda_agente.append(agente_hmtl);
	});
	
	
	//Para el autocomplete
	var input_json1 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAutoClienteProspecto.json';
	$autocomplete_input($busqueda_tipo_visita, $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]'), input_json1,$('#lienzo_recalculable').find('input[name=iu]').val());
	
	//Para el autocomplete
	var input_json1 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAutocompleteContactos.json';
	$autocomplete_input($busqueda_tipo_visita, $busqueda_contacto, input_json1,$('#lienzo_recalculable').find('input[name=iu]').val());
	
	
                    
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
		$('#forma-crmregistrovisitas-window').find('#submit').mouseover(function(){
			$('#forma-crmregistrovisitas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-crmregistrovisitas-window').find('#submit').mouseout(function(){
			$('#forma-crmregistrovisitas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-crmregistrovisitas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-crmregistrovisitas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-crmregistrovisitas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-crmregistrovisitas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-crmregistrovisitas-window').find('#close').mouseover(function(){
			$('#forma-crmregistrovisitas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-crmregistrovisitas-window').find('#close').mouseout(function(){
			$('#forma-crmregistrovisitas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-crmregistrovisitas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-crmregistrovisitas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-crmregistrovisitas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-crmregistrovisitas-window').find("ul.pestanas li").click(function() {
			$('#forma-crmregistrovisitas-window').find(".contenidoPes").hide();
			$('#forma-crmregistrovisitas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-crmregistrovisitas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
		
	/*funcion para colorear la fila en la que pasa el puntero*/
	$colorea_tr_grid = function($tabla){
		$tabla.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
		$tabla.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});
		
		$('tr:odd' , $tabla).hover(function () {
			$(this).find('td').css({background : '#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#e7e8ea'});
		});
		$('tr:even' , $tabla).hover(function () {
			$(this).find('td').css({'background-color':'#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#FFFFFF'});
		});
	};
        
	
	//buscador de Contactos
	$busca_contactos = function(busqueda_inicial ){
		//limpiar_campos_grids();
		$(this).modalPanel_BuscaContacto();
		var $dialogoc =  $('#forma-buscacontactos-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_contactos').find('table.formaBusqueda_contactos').clone());
		
		$('#forma-buscacontactos-window').css({"margin-left": -180, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscacontactos-window').find('#tabla_resultado');
		
		var $campo_buscador_nombre = $('#forma-buscacontactos-window').find('input[name=buscador_nombre]');
		var $campo_buscador_apellidop = $('#forma-buscacontactos-window').find('input[name=buscador_apellidop]');
		var $campo_buscador_apellidom = $('#forma-buscacontactos-window').find('input[name=buscador_apellidom]');
		var $select_buscador_tipo_contacto = $('#forma-buscacontactos-window').find('select[name=buscador_tipo_contacto]');
		
		var $buscar_plugin_contacto = $('#forma-buscacontactos-window').find('#busca_contacto_modalbox');
		var $cancelar_plugin_busca_contacto = $('#forma-buscacontactos-window').find('#cencela');
		
		//funcionalidad botones
		$buscar_plugin_contacto.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_contacto.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_contacto.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_contacto.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		$campo_buscador_nombre.val(busqueda_inicial);
		
		//click buscar productos
		$buscar_plugin_contacto.click(function(event){
                    
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_contactos.json';
			$arreglo = {'buscador_nombre':$campo_buscador_nombre.val(),'buscador_apellidop':$campo_buscador_apellidop.val(),
			'buscador_apellidom':$campo_buscador_apellidom.val(),'buscador_tipo_contacto':$select_buscador_tipo_contacto.val(),'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				
				$.each(entry['contactos'],function(entryIndex,prospecto){
					trr = '<tr>';
						trr += '<td width="280px">';
							trr += '<span class="contacto_buscador">'+prospecto['contacto']+'</span>';
							trr += '<input type="hidden" id="id_contacto_buscador" value="'+prospecto['id']+'">';
						trr += '</td>';
						trr += '<td width="210px"><span class="razon_social_buscador">'+prospecto['razon_social']+'</span></td>';
						trr += '<td width="110px"><span class="rfc_buscador">'+prospecto['rfc']+'</span></td>';
					trr += '</tr>';
					$tabla_resultados.append(trr);
				});
				
				$colorea_tr_grid($tabla_resultados);
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					var id_contacto=$(this).find('#id_contacto_buscador').val();
					var contacto_buscador=$(this).find('span.contacto_buscador').html();
					var razon_social_buscador=$(this).find('span.razon_social_buscador').html();
					var rfc_buscador=$(this).find('span.rfc_buscador').html();
					
					$('#forma-crmregistrovisitas-window').find('input[name=id_contacto]').val(id_contacto);
					$('#forma-crmregistrovisitas-window').find('input[name=contacto]').val(contacto_buscador);
					
					//oculta la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacontactos-overlay').fadeOut(remove);
				});
			});
		});
	
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if(busqueda_inicial != ''){
			$buscar_plugin_contacto.trigger('click');
		}
		
		$cancelar_plugin_busca_contacto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacontactos-overlay').fadeOut(remove);
		});
                
	}//termina buscador de Contactos
	
	
	
	
	
	//nuevo
	$new_crmregistrovisitas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_crmregistrovisitas();
		
		var form_to_show = 'formacrmregistrovisitas';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-crmregistrovisitas-window').css({"margin-left": -400, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-crmregistrovisitas-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-crmregistrovisitas-window').find('input[name=identificador]');
		var $folio = $('#forma-crmregistrovisitas-window').find('input[name=folio]');
		var $select_agente = $('#forma-crmregistrovisitas-window').find('select[name=select_agente]');
		var $id_contacto = $('#forma-crmregistrovisitas-window').find('input[name=id_contacto]');
		var $contacto = $('#forma-crmregistrovisitas-window').find('input[name=contacto]');
		var $busca_contacto = $('#forma-crmregistrovisitas-window').find('#busca_contacto');
		
		var $fecha = $('#forma-crmregistrovisitas-window').find('input[name=fecha]');
		var $hora_visita = $('#forma-crmregistrovisitas-window').find('input[name=hora_visita]');
		var $hora_duracion = $('#forma-crmregistrovisitas-window').find('input[name=hora_duracion]');
		
		var $select_motivo_visita = $('#forma-crmregistrovisitas-window').find('select[name=select_motivo_visita]');
		var $select_calif_visita = $('#forma-crmregistrovisitas-window').find('select[name=select_calif_visita]');
		var $select_tipo_seguimiento = $('#forma-crmregistrovisitas-window').find('select[name=select_tipo_seguimiento]');
		var $select_oportunidad = $('#forma-crmregistrovisitas-window').find('select[name=select_oportunidad]');
		
		var $recusrsos_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=recusrsos_visita]');
		var $resultado_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=resultado_visita]');
		var $observaciones_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=observaciones_visita]');
		
		var $fecha_proxima_visita = $('#forma-crmregistrovisitas-window').find('input[name=fecha_proxima_visita]');
		var $hora_proxima_visita = $('#forma-crmregistrovisitas-window').find('input[name=hora_proxima_visita]');
		var $comentarios_proxima_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=comentarios_proxima_visita]');
		
		var $fecha_proxima_llamada = $('#forma-crmregistrovisitas-window').find('input[name=fecha_proxima_llamada]');
		var $hora_proxima_llamada = $('#forma-crmregistrovisitas-window').find('input[name=hora_proxima_llamada]');
		var $comentarios_proxima_llamada = $('#forma-crmregistrovisitas-window').find('textarea[name=comentarios_proxima_llamada]');
		
		var $cerrar_plugin = $('#forma-crmregistrovisitas-window').find('#close');
		var $cancelar_plugin = $('#forma-crmregistrovisitas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-crmregistrovisitas-window').find('#submit');
		
		$folio.css({'background' : '#DDDDDD'});
		$identificador.attr({'value' : 0});
		$id_contacto.attr({'value' : 0});
		$hora_visita.attr({'value' : '00:00'});
		$hora_duracion.attr({'value' : '00:00'});
		$hora_proxima_visita.attr({'value' : '00:00'});
		$hora_proxima_llamada.attr({'value' : '00:00'});
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La visita se registr&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-crmregistrovisitas-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-crmregistrovisitas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroVisita.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			
			//Alimentando los campos select_agente
			$select_agente.children().remove();
			var agente_hmtl = '';
			if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
				agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
			}
			
			$.each(entry['Agentes'],function(entryIndex,agente){
				if(parseInt(agente['id'])==parseInt(entry['Extra'][0]['id_agente'])){
					agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
				}else{
					//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
					if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
						agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
					}
				}
			});
			$select_agente.append(agente_hmtl);
			
			
			//Alimentando los campos select de Motivos
			$select_motivo_visita.children().remove();
			var motivo_hmtl = '';
			$.each(entry['Motivos'],function(entryIndex,motivo){
				motivo_hmtl += '<option value="' + motivo['id'] + '"  >' + motivo['descripcion'] + '</option>';
			});
			$select_motivo_visita.append(motivo_hmtl);
			
			//Alimentando los campos select de Calificaciones de Visita
			$select_calif_visita.children().remove();
			var calif_hmtl = '';
			$.each(entry['Calificaciones'],function(entryIndex,calif){
				calif_hmtl += '<option value="' + calif['id'] + '"  >' + calif['titulo'] + '</option>';
			});
			$select_calif_visita.append(calif_hmtl);
			
			//Alimentando los campos select de Seguimiento
			$select_tipo_seguimiento.children().remove();
			var seguimiento_hmtl = '';
			$.each(entry['Seguimientos'],function(entryIndex,seg){
				seguimiento_hmtl += '<option value="' + seg['id'] + '"  >' + seg['titulo'] + '</option>';
			});
			$select_tipo_seguimiento.append(seguimiento_hmtl);
			
			//Alimentando los campos select de oportunidad
			$select_oportunidad.children().remove();
			var oportunidad_hmtl = '<option value="1" selected="yes">Si</option>';
			oportunidad_hmtl += '<option value="0">No</option>';
			$select_oportunidad.append(oportunidad_hmtl);
			
		},"json");//termina llamada json
        
        //$('.input1').TimepickerInputMask();
        
        $hora_visita.TimepickerInputMask();
		$hora_duracion.TimepickerInputMask();
        $hora_proxima_visita.TimepickerInputMask();
        $hora_proxima_llamada.TimepickerInputMask();
        
        //fecha de la visita
		$fecha.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
			
		$fecha.DatePicker({
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
				$fecha.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha.val(mostrarFecha());
					}else{
						$fecha.DatePickerHide();	
					}
				}
			}
		});
		
			
        
        //fecha para la proxima visita
		$fecha_proxima_visita.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_proxima_visita.DatePicker({
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
				$fecha_proxima_visita.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_proxima_visita.val(),mostrarFecha());
					
					if (valida_fecha==true){
						$fecha_proxima_visita.DatePickerHide();	
					}else{
						jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
						$fecha_proxima_visita.val(mostrarFecha());
					}
				}
			}
		});
        
		//Fecha para la proxima llamada
		$fecha_proxima_llamada.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_proxima_llamada.DatePicker({
			format:'Y-m-d',
			date: $fecha_proxima_llamada.val(),
			current: $fecha_proxima_llamada.val(),
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
				$fecha_proxima_llamada.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_proxima_llamada.val(),mostrarFecha());
					
					if (valida_fecha==true){
						$fecha_proxima_llamada.DatePickerHide();	
					}else{
						jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
						$fecha_proxima_llamada.val(mostrarFecha());
					}
				}
			}
		});
        
        
        //Buscar contacto
        $busca_contacto.click(function(event){
			event.preventDefault();
			$busca_contactos($contacto.val());
        });
        
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});		
	});
	
	
	
	
	
	var carga_formaDirecciones_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Visita seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Visita fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Visita no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formacrmregistrovisitas';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_crmregistrovisitas();
			$('#forma-crmregistrovisitas-window').css({"margin-left": -400, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-crmregistrovisitas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $identificador = $('#forma-crmregistrovisitas-window').find('input[name=identificador]');
			var $folio = $('#forma-crmregistrovisitas-window').find('input[name=folio]');
			var $select_agente = $('#forma-crmregistrovisitas-window').find('select[name=select_agente]');
			var $id_contacto = $('#forma-crmregistrovisitas-window').find('input[name=id_contacto]');
			var $contacto = $('#forma-crmregistrovisitas-window').find('input[name=contacto]');
			var $busca_contacto = $('#forma-crmregistrovisitas-window').find('#busca_contacto');
			
			var $fecha = $('#forma-crmregistrovisitas-window').find('input[name=fecha]');
			var $hora_visita = $('#forma-crmregistrovisitas-window').find('input[name=hora_visita]');
			var $hora_duracion = $('#forma-crmregistrovisitas-window').find('input[name=hora_duracion]');
			
			var $select_motivo_visita = $('#forma-crmregistrovisitas-window').find('select[name=select_motivo_visita]');
			var $select_calif_visita = $('#forma-crmregistrovisitas-window').find('select[name=select_calif_visita]');
			var $select_tipo_seguimiento = $('#forma-crmregistrovisitas-window').find('select[name=select_tipo_seguimiento]');
			var $select_oportunidad = $('#forma-crmregistrovisitas-window').find('select[name=select_oportunidad]');
			
			var $recusrsos_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=recusrsos_visita]');
			var $resultado_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=resultado_visita]');
			var $observaciones_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=observaciones_visita]');
			
			var $fecha_proxima_visita = $('#forma-crmregistrovisitas-window').find('input[name=fecha_proxima_visita]');
			var $hora_proxima_visita = $('#forma-crmregistrovisitas-window').find('input[name=hora_proxima_visita]');
			var $comentarios_proxima_visita = $('#forma-crmregistrovisitas-window').find('textarea[name=comentarios_proxima_visita]');
			var $productos = $('#forma-crmregistrovisitas-window').find('textarea[name=productos]');
			
			var $fecha_proxima_llamada = $('#forma-crmregistrovisitas-window').find('input[name=fecha_proxima_llamada]');
			var $hora_proxima_llamada = $('#forma-crmregistrovisitas-window').find('input[name=hora_proxima_llamada]');
			var $comentarios_proxima_llamada = $('#forma-crmregistrovisitas-window').find('textarea[name=comentarios_proxima_llamada]');
			
			var $cerrar_plugin = $('#forma-crmregistrovisitas-window').find('#close');
			var $cancelar_plugin = $('#forma-crmregistrovisitas-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-crmregistrovisitas-window').find('#submit');
			
			
			$folio.css({'background' : '#DDDDDD'});
			$hora_visita.attr({'value' : '00:00'});
			$hora_duracion.attr({'value' : '00:00'});
			$hora_proxima_visita.attr({'value' : '00:00'});
			$hora_proxima_llamada.attr({'value' : '00:00'});
			//$busca_contacto.hide();
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroVisita.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("El registro se actualiz&oacute; con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
						//refresh_table();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-crmregistrovisitas-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-crmregistrovisitas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
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
					$identificador.attr({'value' : entry['Datos'][0]['id']});
					$folio.attr({'value' : entry['Datos'][0]['folio']});
					
					$id_contacto.attr({'value' : entry['Datos'][0]['contacto_id']});
					$contacto.attr({'value' : entry['Datos'][0]['nombre_contacto']});

					$fecha.attr({'value' : entry['Datos'][0]['fecha']});
					$hora_visita.attr({'value' : entry['Datos'][0]['hora']});
					$hora_visita.TimepickerInputMask();
					$hora_duracion.attr({'value' : entry['Datos'][0]['duracion']});
					$hora_duracion.TimepickerInputMask();
					
					$recusrsos_visita.text(entry['Datos'][0]['recursos_utilizados']);
					$resultado_visita.text(entry['Datos'][0]['resultado']);
					$observaciones_visita.text(entry['Datos'][0]['observaciones']);
					$productos.text(entry['Datos'][0]['productos']);
					
					$fecha_proxima_visita.attr({'value' : entry['Datos'][0]['fecha_sig_visita']});
					$hora_proxima_visita.attr({'value' : entry['Datos'][0]['hora_sig_visita']});
					$hora_proxima_visita.TimepickerInputMask();
					$comentarios_proxima_visita.text(entry['Datos'][0]['comentarios_sig_visita']);
					
					$fecha_proxima_llamada.attr({'value' : entry['Datos'][0]['fecha_sig_llamada']});
					$hora_proxima_llamada.attr({'value' : entry['Datos'][0]['hora_sig_llamada']});
					$hora_proxima_llamada.TimepickerInputMask();
					$comentarios_proxima_llamada.text(entry['Datos'][0]['comentarios_sig_llamada']);
					
					//Alimentando los campos select_agente
					$select_agente.children().remove();
					var agente_hmtl='';
					$.each(entry['Agentes'],function(entryIndex,agente){
						if(parseInt(agente['id'])==parseInt(entry['Datos'][0]['empleado_id'])){
							agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
						}else{
							agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
						}
					});
					$select_agente.append(agente_hmtl);
					
					//Alimentando los campos select de Motivos
					$select_motivo_visita.children().remove();
					var motivo_hmtl = '';
					$.each(entry['Motivos'],function(entryIndex,motivo){
						if(parseInt(motivo['id'])==parseInt(entry['Datos'][0]['motivo_id'])){
							motivo_hmtl += '<option value="' + motivo['id'] + '" selected="yes">' + motivo['descripcion'] + '</option>';
						}else{
							motivo_hmtl += '<option value="' + motivo['id'] + '"  >' + motivo['descripcion'] + '</option>';
						}
					});
					$select_motivo_visita.append(motivo_hmtl);
					
					//Alimentando los campos select de Calificaciones de Visita
					$select_calif_visita.children().remove();
					var calif_hmtl = '';
					$.each(entry['Calificaciones'],function(entryIndex,calif){
						if(parseInt(calif['id'])==parseInt(entry['Datos'][0]['calificacion_id'])){
							calif_hmtl += '<option value="' + calif['id'] + '" selected="yes">' + calif['titulo'] + '</option>';
						}else{
							calif_hmtl += '<option value="' + calif['id'] + '"  >' + calif['titulo'] + '</option>';
						}
					});
					$select_calif_visita.append(calif_hmtl);
					
					//Alimentando los campos select de Seguimiento
					$select_tipo_seguimiento.children().remove();
					var seguimiento_hmtl = '';
					$.each(entry['Seguimientos'],function(entryIndex,seg){
						if(parseInt(seg['id'])==parseInt(entry['Datos'][0]['seguimiento_id'])){
							seguimiento_hmtl += '<option value="' + seg['id'] + '" selected="yes">' + seg['titulo'] + '</option>';
						}else{
							seguimiento_hmtl += '<option value="' + seg['id'] + '"  >' + seg['titulo'] + '</option>';
						}
					});
					$select_tipo_seguimiento.append(seguimiento_hmtl);
					
					//Alimentando los campos select de oportunidad
					$select_oportunidad.children().remove();
					var oportunidad_hmtl ='';
					if(parseInt(entry['Datos'][0]['deteccion_oportunidad']) == 1){
						oportunidad_hmtl= '<option value="1" selected="yes">Si</option>';
						oportunidad_hmtl += '<option value="0">No</option>';
					}else{
						oportunidad_hmtl= '<option value="1">Si</option>';
						oportunidad_hmtl += '<option value="0" selected="yes">No</option>';
					}
					$select_oportunidad.append(oportunidad_hmtl);
					
				},"json");//termina llamada json
				
				
				//buscar contacto
				$busca_contacto.click(function(event){
					event.preventDefault();
					$busca_contactos($contacto.val());
				});
				
				
				//fecha de la visita
				$fecha.click(function (s){
					var a=$('div.datepicker');
					a.css({'z-index':100});
				});
					
				$fecha.DatePicker({
					format:'Y-m-d',
					date: $fecha.val(),
					current: $fecha.val(),
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
						$fecha.val(formated);
						if (formated.match(patron) ){
							var valida_fecha=mayor($fecha.val(),mostrarFecha());
							
							if (valida_fecha==true){
								jAlert("Fecha no valida",'! Atencion');
								$fecha.val(mostrarFecha());
							}else{
								$fecha.DatePickerHide();	
							}
						}
					}
				});
				
					
				
				//fecha para la proxima visita
				$fecha_proxima_visita.click(function (s){
					var a=$('div.datepicker');
					a.css({'z-index':100});
				});
				
				$fecha_proxima_visita.DatePicker({
					format:'Y-m-d',
					date: $fecha_proxima_visita.val(),
					current: $fecha_proxima_visita.val(),
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
						$fecha_proxima_visita.val(formated);
						if (formated.match(patron) ){
							var valida_fecha=mayor($fecha_proxima_visita.val(),mostrarFecha());
							
							if (valida_fecha==true){
								$fecha_proxima_visita.DatePickerHide();	
							}else{
								jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
								$fecha_proxima_visita.val(mostrarFecha());
							}
						}
					}
				});
				
				//Fecha para la proxima llamada
				$fecha_proxima_llamada.click(function (s){
					var a=$('div.datepicker');
					a.css({'z-index':100});
				});
				
				$fecha_proxima_llamada.DatePicker({
					format:'Y-m-d',
					date: $fecha_proxima_llamada.val(),
					current: $fecha_proxima_llamada.val(),
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
						$fecha_proxima_llamada.val(formated);
						if (formated.match(patron) ){
							var valida_fecha=mayor($fecha_proxima_llamada.val(),mostrarFecha());
							
							if (valida_fecha==true){
								$fecha_proxima_llamada.DatePickerHide();	
							}else{
								jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
								$fecha_proxima_llamada.val(mostrarFecha());
							}
						}
					}
				});
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-crmregistrovisitas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllRegistroVisitas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllRegistroVisitas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
