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
	var controller = $contextpath.val()+"/controllers/clientsremiten";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_clientsremiten = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Remitentes');
	
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
		valor_retorno += "remitente" + signo_separador + $busqueda_razon_social.val() + "|";
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
	
	//Alimentando select de Tipo de Remitente
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
		
		//Alimentando select de Tipo de Remitente
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
		$('#forma-clientsremiten-window').find('#submit').mouseover(function(){
			$('#forma-clientsremiten-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-clientsremiten-window').find('#submit').mouseout(function(){
			$('#forma-clientsremiten-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-clientsremiten-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-clientsremiten-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-clientsremiten-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-clientsremiten-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-clientsremiten-window').find('#close').mouseover(function(){
			$('#forma-clientsremiten-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-clientsremiten-window').find('#close').mouseout(function(){
			$('#forma-clientsremiten-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-clientsremiten-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-clientsremiten-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-clientsremiten-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-clientsremiten-window').find("ul.pestanas li").click(function() {
			$('#forma-clientsremiten-window').find(".contenidoPes").hide();
			$('#forma-clientsremiten-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-clientsremiten-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	//nuevo 
	$new_clientsremiten.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_clientsremiten();
		
		var form_to_show = 'formaDirecciones';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-clientsremiten-window').css({"margin-left": -400, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-clientsremiten-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-clientsremiten-window').find('input[name=identificador]');
		var $select_tipo = $('#forma-clientsremiten-window').find('select[name=select_tipo]');
		var $remitente = $('#forma-clientsremiten-window').find('input[name=remitente]');
		var $folio = $('#forma-clientsremiten-window').find('input[name=folio]');
		var $rfc = $('#forma-clientsremiten-window').find('input[name=rfc]');
		var $calle = $('#forma-clientsremiten-window').find('input[name=calle]');
		var $numero_int = $('#forma-clientsremiten-window').find('input[name=numero_int]');
		var $numero_ext = $('#forma-clientsremiten-window').find('input[name=numero_ext]');
		var $colonia = $('#forma-clientsremiten-window').find('input[name=colonia]');
		var $select_pais = $('#forma-clientsremiten-window').find('select[name=select_pais]');
		var $select_estado = $('#forma-clientsremiten-window').find('select[name=select_estado]');
		var $select_municipio = $('#forma-clientsremiten-window').find('select[name=select_municipio]');
		var $email = $('#forma-clientsremiten-window').find('input[name=email]');
		var $tel1 = $('#forma-clientsremiten-window').find('input[name=tel1]');
		var $ext1 = $('#forma-clientsremiten-window').find('input[name=ext1]');
		var $tel2 = $('#forma-clientsremiten-window').find('input[name=tel2]');
		
		var $cerrar_plugin = $('#forma-clientsremiten-window').find('#close');
		var $cancelar_plugin = $('#forma-clientsremiten-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-clientsremiten-window').find('#submit');
		
		//quitar enter a todos los campos input
		$('#forma-clientsremiten-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$folio.css({'background' : '#DDDDDD'});
		//$rfc.css({'background' : '#DDDDDD'});
		$identificador.attr({'value' : 0});
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los datos se guardaron  con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-clientsremiten-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-clientsremiten-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-clientsremiten-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRemitente.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			//Alimentando select de Tipo de Remitente
			$select_tipo.children().remove();
			var tipo_hmtl = '<option value="1" selected="yes">Nacional</option>';
			tipo_hmtl += '<option value="2">Extranjero</option>';
			$select_tipo.append(tipo_hmtl);
			
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
        
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-clientsremiten-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-clientsremiten-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$remitente.focus();
	});
	
	
	
	
	
	var carga_formaDirecciones_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Remitente?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El remitente fue eliminado exitosamente.", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Remitente no pudo ser eliminado.", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaDirecciones';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_clientsremiten();
			$('#forma-clientsremiten-window').css({"margin-left": -400, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-clientsremiten-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $identificador = $('#forma-clientsremiten-window').find('input[name=identificador]');
			var $select_tipo = $('#forma-clientsremiten-window').find('select[name=select_tipo]');
			var $remitente = $('#forma-clientsremiten-window').find('input[name=remitente]');
			var $folio = $('#forma-clientsremiten-window').find('input[name=folio]');
			var $rfc = $('#forma-clientsremiten-window').find('input[name=rfc]');
			var $calle = $('#forma-clientsremiten-window').find('input[name=calle]');
			var $numero_int = $('#forma-clientsremiten-window').find('input[name=numero_int]');
			var $numero_ext = $('#forma-clientsremiten-window').find('input[name=numero_ext]');
			var $colonia = $('#forma-clientsremiten-window').find('input[name=colonia]');
			var $cp = $('#forma-clientsremiten-window').find('input[name=cp]');
			var $select_pais = $('#forma-clientsremiten-window').find('select[name=select_pais]');
			var $select_estado = $('#forma-clientsremiten-window').find('select[name=select_estado]');
			var $select_municipio = $('#forma-clientsremiten-window').find('select[name=select_municipio]');
			var $email = $('#forma-clientsremiten-window').find('input[name=email]');
			var $tel1 = $('#forma-clientsremiten-window').find('input[name=tel1]');
			var $ext1 = $('#forma-clientsremiten-window').find('input[name=ext1]');
			var $tel2 = $('#forma-clientsremiten-window').find('input[name=tel2]');
                        
			var $cerrar_plugin = $('#forma-clientsremiten-window').find('#close');
			var $cancelar_plugin = $('#forma-clientsremiten-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-clientsremiten-window').find('#submit');
			
			$folio.css({'background' : '#DDDDDD'});
			//$remitente.css({'background' : '#DDDDDD'});
			//$rfc.css({'background' : '#DDDDDD'});
			
			$folio.attr({ 'readOnly':true });
			//$remitente.attr({ 'readOnly':true });
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRemitente.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("Los datos se guardaron con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-clientsremiten-overlay').fadeOut(remove);
						//refresh_table();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-clientsremiten-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-clientsremiten-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
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
					$remitente.attr({'value' : entry['Datos']['0']['remitente']});
					$folio.attr({'value' : entry['Datos']['0']['folio']});
					$rfc.attr({'value' : entry['Datos']['0']['rfc']});
					$calle.attr({'value' : entry['Datos']['0']['calle']});
					$numero_int.attr({'value' : entry['Datos']['0']['no_int']});
					$numero_ext.attr({'value' : entry['Datos']['0']['no_ext']});
					$colonia.attr({'value' : entry['Datos']['0']['colonia']});
					$cp.attr({'value' : entry['Datos']['0']['cp']});
					$email.attr({'value' : entry['Datos']['0']['email']});
					$tel1.attr({'value' : entry['Datos']['0']['telefono1']});
					$ext1.attr({'value' : entry['Datos']['0']['extension']});
					$tel2.attr({'value' : entry['Datos']['0']['telefono2']});
					
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
					//Alimentando select de Tipo de Remitente
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
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsremiten-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsremiten-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
				$remitente.focus();
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllClientsRemiten.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllClientsRemiten.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
