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
	var controller = $contextpath.val()+"/controllers/almacenes";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_almacen = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Almacenes');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_nombre = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_nombre]');
	var $busqueda_select_tipo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_tipo]');
	
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "nombre" + signo_separador + $busqueda_nombre.val() + "|";
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
	
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_nombre.val('');
		//$busqueda_select_tipo.removeAttr("selected");
		$busqueda_select_tipo.find("option[value=0]").attr("selected",true);
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
	});
	
	
	
	//obtiene los tipos de almacen para el buscador
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTiposAlmacen.json';
	$arreglo = {};
	$.post(input_json,$arreglo,function(entry){
		//Alimentando los campos select tipos de almacen del buscador
		$busqueda_select_tipo.children().remove();
		var tipo_hmtl = '<option value="0" selected="yes">[--Seleccionar Tipo --]</option>';
		$.each(entry['Tipos'],function(entryIndex,reg){
			tipo_hmtl += '<option value="' + reg['id'] + '"  >' + reg['titulo'] + '</option>';
		});
		$busqueda_select_tipo.append(tipo_hmtl);
	});//termina llamada json
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-almacenes-window').find('#submit').mouseover(function(){
			$('#forma-almacenes-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-almacenes-window').find('#submit').mouseout(function(){
			$('#forma-almacenes-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-almacenes-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-almacenes-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-almacenes-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-almacenes-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-almacenes-window').find('#close').mouseover(function(){
			$('#forma-almacenes-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-almacenes-window').find('#close').mouseout(function(){
			$('#forma-almacenes-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-almacenes-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-almacenes-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-almacenes-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-almacenes-window').find("ul.pestanas li").click(function() {
			$('#forma-almacenes-window').find(".contenidoPes").hide();
			$('#forma-almacenes-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-almacenes-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			
			if(activeTab == '#tabx-1'){
				$('#forma-almacenes-window').find('.almacenes_div_one').css({'height':'355px'});
			}
			if(activeTab == '#tabx-2'){
				$('#forma-almacenes-window').find('.almacenes_div_one').css({'height':'240px'});
			}
			if(activeTab == '#tabx-3'){
				$('#forma-almacenes-window').find('.almacenes_div_one').css({'height':'355px'});
			}
			if(activeTab == '#tabx-4'){
				$('#forma-almacenes-window').find('.almacenes_div_one').css({'height':'355px'});
			}
			return false;
		});

	}
	
	
	
	
	
	//nuevo centro de costo
	$new_almacen.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_almacenes();
		
		var form_to_show = 'formaalmacenes00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-almacenes-window').css({ "margin-left": -300, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-almacenes-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-almacenes-window').find('input[name=identificador]');
		var $nombre = $('#forma-almacenes-window').find('input[name=nombre]');
		var $campo_calle = $('#forma-almacenes-window').find('input[name=calle]');
		var $campo_numero = $('#forma-almacenes-window').find('input[name=numero]');
		var $campo_colonia = $('#forma-almacenes-window').find('input[name=colonia]');
		var $campo_cp = $('#forma-almacenes-window').find('input[name=cp]');
		var $select_pais = $('#forma-almacenes-window').find('select[name=pais]');
		var $select_entidad = $('#forma-almacenes-window').find('select[name=estado]');
		var $select_localidad = $('#forma-almacenes-window').find('select[name=municipio]');
		
		var $campo_tel1 = $('#forma-almacenes-window').find('input[name=tel1]');
		var $campo_ext1 = $('#forma-almacenes-window').find('input[name=ext1]');
		var $campo_tel2 = $('#forma-almacenes-window').find('input[name=tel2]');
		var $campo_ext2 = $('#forma-almacenes-window').find('input[name=ext2]');
		
		var $campo_responsable = $('#forma-almacenes-window').find('input[name=responsable]');
		var $campo_puesto = $('#forma-almacenes-window').find('input[name=puesto]');
		var $campo_email = $('#forma-almacenes-window').find('input[name=email]');
		
		var $check_reporteo = $('#forma-almacenes-window').find('input[name=check_reporteo]');
		var $check_ventas = $('#forma-almacenes-window').find('input[name=check_ventas]');
		var $check_compras = $('#forma-almacenes-window').find('input[name=check_compras]');
		var $check_traspaso = $('#forma-almacenes-window').find('input[name=check_traspaso]');
		var $check_reabastecimiento = $('#forma-almacenes-window').find('input[name=check_reabastecimiento]');
		var $check_garantias = $('#forma-almacenes-window').find('input[name=check_garantias]');
		var $check_consignacion = $('#forma-almacenes-window').find('input[name=check_consignacion]');
		var $check_recepcion_mat = $('#forma-almacenes-window').find('input[name=check_recepcion_mat]');
		var $check_explosion_mat = $('#forma-almacenes-window').find('input[name=check_explosion_mat]');
		
		var $select_tipo_almacen = $('#forma-almacenes-window').find('select[name=tipo_almacen]');
		
		//Sucursales disponibles
		var $select_disponibles= $('#forma-almacenes-window').find('select[name=disponibles]');
		//Sucursales Asignados
		var $select_seleccionados = $('#forma-almacenes-window').find('select[name=seleccionados]');
		
		//id  de las sucursales seleccionadas
		var $campo_suc_on = $('#forma-almacenes-window').find('input[name=suc_on]');
		
		//agregar y remover sucursales
		var $agregar_pres = $('#forma-almacenes-window').find('a[href*=agregar_pres]');
		var $remover_pres = $('#forma-almacenes-window').find('a[href*=remover_pres]');
		
		var $cerrar_plugin = $('#forma-almacenes-window').find('#close');
		var $cancelar_plugin = $('#forma-almacenes-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-almacenes-window').find('#submit');
		
		$campo_id.attr({ 'value' : 0 });
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Almacen fue dado de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-almacenes-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-almacenes-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-almacenes-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAlmacen.json';
		$arreglo = {'id':id_to_show,
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		$.post(input_json,$arreglo,function(entry){
			
			//Alimentando los campos select tipos de almacen del buscador
			$select_tipo_almacen.children().remove();
			var tipo_hmtl = '<option value="0" selected="yes">[--Seleccionar Tipo --]</option>';
			$.each(entry['Tipos'],function(entryIndex,reg){
				tipo_hmtl += '<option value="' + reg['id'] + '"  >' + reg['titulo'] + '</option>';
			});
			$select_tipo_almacen.append(tipo_hmtl);
			
			
			//carga select de sucursales disponibles
			$select_disponibles.children().remove();
			var sucursales_hmtl = '';
			$.each(entry['Sucursales'],function(entryIndex,suc){
				sucursales_hmtl += '<option value="' + suc['id'] + '"  >' + suc['titulo'] + '</option>';
			});
			$select_disponibles.append(sucursales_hmtl);
			
			
			//Alimentando los campos select de las pais
			$select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
			$.each(entry['Paises'],function(entryIndex,pais){
				pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
			});
			$select_pais.append(pais_hmtl);


			var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar entidad--]</option>';
			$select_entidad.children().remove();
			$select_entidad.append(entidad_hmtl);

			var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar municipio-]</option>';
			$select_localidad.children().remove();
			$select_localidad.append(localidad_hmtl);
			
			
			//carga select estados al cambiar el pais
			$select_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$select_entidad.children().remove();
					var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['Entidades'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$select_entidad.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
					$select_localidad.children().remove();
					$select_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			//carga select municipios al cambiar el estado
			$select_entidad.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $select_pais.val();
				//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$select_localidad.children().remove();
					var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
					$.each(entry['Localidades'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$select_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
		},"json");//termina llamada json
		



		//agregar sucursal
		$agregar_pres.click(function(event){
			event.preventDefault();
			var logica = false;
			var primero=0;
			logica = !$select_disponibles.find('option:selected').remove().appendTo( $select_seleccionados);
			var valor_campo = "";
			var ahora_seleccionados = $select_seleccionados.find('option').get();
			$.each( ahora_seleccionados , function(indice , seleccionado){
				if(primero==0){
					valor_campo += seleccionado.value;
					primero=1;
				}else{
					valor_campo += "," + seleccionado.value;
				}
			});
			//alert(valor_campo);
			$campo_suc_on.attr({'value' : valor_campo });
			return logica; 
		});
		
		
		//remover sucursal
		$remover_pres.click(function(event){
			event.preventDefault();
			var logica = false;
			var primero=0;
			logica = !$select_seleccionados.find('option:selected').remove().appendTo($select_disponibles);
			var valor_campo = "";
			var ahora_seleccionados = $select_seleccionados.find('option').get();
			$.each( ahora_seleccionados , function(indice , seleccionado){
				if(primero==0){
					valor_campo += seleccionado.value;
					primero=1;
				}else{
					valor_campo += "," + seleccionado.value;
				}
			});
			//alert(valor_campo);
			$campo_suc_on.attr({'value' : valor_campo }); 
			return logica;
		});

		

		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-almacenes-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-almacenes-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
                
                
                
		
	});
	
	
        
        
        
	
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Almacen seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Almacen fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Almacen no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaalmacenes00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_almacenes();
			$('#forma-almacenes-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-almacenes-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-almacenes-window').find('input[name=identificador]');
			var $nombre = $('#forma-almacenes-window').find('input[name=nombre]');
			var $campo_calle = $('#forma-almacenes-window').find('input[name=calle]');
			var $campo_numero = $('#forma-almacenes-window').find('input[name=numero]');
			var $campo_colonia = $('#forma-almacenes-window').find('input[name=colonia]');
			var $campo_cp = $('#forma-almacenes-window').find('input[name=cp]');
			var $select_pais = $('#forma-almacenes-window').find('select[name=pais]');
			var $select_entidad = $('#forma-almacenes-window').find('select[name=estado]');
			var $select_localidad = $('#forma-almacenes-window').find('select[name=municipio]');
			
			var $campo_tel1 = $('#forma-almacenes-window').find('input[name=tel1]');
			var $campo_ext1 = $('#forma-almacenes-window').find('input[name=ext1]');
			var $campo_tel2 = $('#forma-almacenes-window').find('input[name=tel2]');
			var $campo_ext2 = $('#forma-almacenes-window').find('input[name=ext2]');
			
			var $campo_responsable = $('#forma-almacenes-window').find('input[name=responsable]');
			var $campo_puesto = $('#forma-almacenes-window').find('input[name=puesto]');
			var $campo_email = $('#forma-almacenes-window').find('input[name=email]');
			
			var $check_reporteo = $('#forma-almacenes-window').find('input[name=check_reporteo]');
			var $check_ventas = $('#forma-almacenes-window').find('input[name=check_ventas]');
			var $check_compras = $('#forma-almacenes-window').find('input[name=check_compras]');
			var $check_traspaso = $('#forma-almacenes-window').find('input[name=check_traspaso]');
			var $check_reabastecimiento = $('#forma-almacenes-window').find('input[name=check_reabastecimiento]');
			var $check_garantias = $('#forma-almacenes-window').find('input[name=check_garantias]');
			var $check_consignacion = $('#forma-almacenes-window').find('input[name=check_consignacion]');
			var $check_recepcion_mat = $('#forma-almacenes-window').find('input[name=check_recepcion_mat]');
			var $check_explosion_mat = $('#forma-almacenes-window').find('input[name=check_explosion_mat]');
			
			var $select_tipo_almacen = $('#forma-almacenes-window').find('select[name=tipo_almacen]');
			
			//Sucursales disponibles
			var $select_disponibles= $('#forma-almacenes-window').find('select[name=disponibles]');
			//Sucursales Asignados
			var $select_seleccionados = $('#forma-almacenes-window').find('select[name=seleccionados]');
			
			//id  de las sucursales seleccionadas
			var $campo_suc_on = $('#forma-almacenes-window').find('input[name=suc_on]');
			
			//agregar y remover sucursales
			var $agregar_pres = $('#forma-almacenes-window').find('a[href*=agregar_pres]');
			var $remover_pres = $('#forma-almacenes-window').find('a[href*=remover_pres]');
			
			var $cerrar_plugin = $('#forma-almacenes-window').find('#close');
			var $cancelar_plugin = $('#forma-almacenes-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-almacenes-window').find('#submit');
			
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAlmacen.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-almacenes-overlay').fadeOut(remove);
						jAlert("Los datos Almacen se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-almacenes-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-almacenes-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$campo_id.attr({ 'value' : entry['Almacen']['0']['id'] });
					$nombre.attr({ 'value' : entry['Almacen']['0']['titulo'] });
					$campo_calle.attr({ 'value' : entry['Almacen']['0']['calle'] });
					$campo_numero.attr({ 'value' : entry['Almacen']['0']['numero'] });
					$campo_colonia.attr({ 'value' : entry['Almacen']['0']['colonia'] });
					$campo_cp.attr({ 'value' : entry['Almacen']['0']['codigo_postal'] });
					$campo_tel1.attr({ 'value' : entry['Almacen']['0']['tel_1'] });
					$campo_ext1.attr({ 'value' : entry['Almacen']['0']['tel_1_ext'] });
					$campo_tel2.attr({ 'value' : entry['Almacen']['0']['tel_2'] });
					$campo_ext2.attr({ 'value' : entry['Almacen']['0']['tel_2_ext'] });
					$campo_responsable.attr({ 'value' : entry['Almacen']['0']['responsable'] });
					$campo_puesto.attr({ 'value' : entry['Almacen']['0']['responsable_puesto'] });
					$campo_email.attr({ 'value' : entry['Almacen']['0']['responsable_email'] });
					
					$check_reporteo.attr('checked',  (entry['Almacen']['0']['reporteo'] == 'true')? true:false );
					$check_ventas.attr('checked', (entry['Almacen']['0']['ventas'] == 'true')? true:false );
					$check_compras.attr('checked', (entry['Almacen']['0']['compras'] == 'true')? true:false );
					$check_traspaso.attr('checked', (entry['Almacen']['0']['traspaso'] == 'true')? true:false );
					$check_reabastecimiento.attr('checked', (entry['Almacen']['0']['reabastecimiento'] == 'true')? true:false );
					$check_garantias.attr('checked', (entry['Almacen']['0']['garantias'] == 'true')? true:false );
					$check_consignacion.attr('checked', (entry['Almacen']['0']['consignacion'] == 'true')? true:false );
					$check_recepcion_mat.attr('checked', (entry['Almacen']['0']['recepcion_mat'] == 'true')? true:false );
					$check_explosion_mat.attr('checked', (entry['Almacen']['0']['explosion_mat'] == 'true')? true:false );
					
					//Alimentando los campos select tipos de almacen del buscador
					$select_tipo_almacen.children().remove();
					var tipo_hmtl = '<option value="0" selected="yes">[--Seleccionar Tipo --]</option>';
					$.each(entry['Tipos'],function(entryIndex,tipo){
						if(tipo['id']==entry['Almacen']['0']['almacen_tipo_id']){
							tipo_hmtl += '<option value="' + tipo['id'] + '"  selected="yes">' + tipo['titulo'] + '</option>';
						}else{
							tipo_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
						}
					});
					$select_tipo_almacen.append(tipo_hmtl);
					
					
					
					//carga select de sucursales disponibles
					$select_disponibles.children().remove();
					var sucursales_hmtl = '';
					$.each(entry['Sucursales'],function(entryIndex,suc){
						sucursales_hmtl += '<option value="' + suc['id'] + '"  >' + suc['titulo'] + '</option>';
					});
					$select_disponibles.append(sucursales_hmtl);
					
					
					//carga select de sucursales seleccionados
					$select_seleccionados.children().remove();
					var suc_on_hmtl = '';
					$.each(entry['SucursalesOn'],function(entryIndex,sucon){
						suc_on_hmtl += '<option value="' + sucon['id'] + '"  >' + sucon['titulo'] + '</option>';
					});
					$select_seleccionados.append(suc_on_hmtl);
					
					
					
					//Alimentando los campos select de las pais
					$select_pais.children().remove();
					var pais_hmtl = "";
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Almacen']['0']['gral_pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_pais.append(pais_hmtl);

					
					//Alimentando los campos select del estado
					$select_entidad.children().remove();
					var entidad_hmtl = "";
					$.each(entry['Entidades'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Almacen']['0']['gral_edo_id']){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_entidad.append(entidad_hmtl);
					
					
					//Alimentando los campos select de los municipios
					$select_localidad.children().remove();
					var localidad_hmtl = "";
					$.each(entry['Municipios'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Almacen']['0']['gral_mun_id']){
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_localidad.append(localidad_hmtl);
					
					
					
					
					//carga select estados al cambiar el pais
					$select_pais.change(function(){
						var valor_pais = $(this).val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
						$arreglo = {'id_pais':valor_pais};
						$.post(input_json,$arreglo,function(entry){
							$select_entidad.children().remove();
							var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
							$.each(entry['Entidades'],function(entryIndex,entidad){
								entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
							});
							$select_entidad.append(entidad_hmtl);
							var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
							$select_localidad.children().remove();
							$select_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					//carga select municipios al cambiar el estado
					$select_entidad.change(function(){
						var valor_entidad = $(this).val();
						var valor_pais = $select_pais.val();
						//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
						$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
						$.post(input_json,$arreglo,function(entry){
							$select_localidad.children().remove();
							var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
							$.each(entry['Localidades'],function(entryIndex,mun){
								trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
							});
							$select_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
				},"json");//termina llamada json
				
				
				
				//agregar sucursal
				$agregar_pres.click(function(event){
					event.preventDefault();
					var logica = false;
					var primero=0;
					logica = !$select_disponibles.find('option:selected').remove().appendTo( $select_seleccionados);
					var valor_campo = "";
					var ahora_seleccionados = $select_seleccionados.find('option').get();
					$.each( ahora_seleccionados , function(indice , seleccionado){
						if(primero==0){
							valor_campo += seleccionado.value;
							primero=1;
						}else{
							valor_campo += "," + seleccionado.value;
						}
					});
					//alert(valor_campo);
					$campo_suc_on.attr({'value' : valor_campo });
					//alert($campo_suc_on.val());
					return logica; 
				});
				
				
				//remover sucursal
				$remover_pres.click(function(event){
					event.preventDefault();
					var logica = false;
					var primero=0;
					logica = !$select_seleccionados.find('option:selected').remove().appendTo($select_disponibles);
					var valor_campo = "";
					var ahora_seleccionados = $select_seleccionados.find('option').get();
					$.each( ahora_seleccionados , function(indice , seleccionado){
						if(primero==0){
							valor_campo += seleccionado.value;
							primero=1;
						}else{
							valor_campo += "," + seleccionado.value;
						}
					});
					//alert(valor_campo);
					$campo_suc_on.attr({'value' : valor_campo }); 
					return logica;
				});

				
				
				
				$submit_actualizar.bind('click',function(){
					//aqui se crea cadena con id de sucursales seleccionados
					var primero=0;
					var valor_campo = "";
					var ahora_seleccionados = $select_seleccionados.find('option').get();
					$.each( ahora_seleccionados , function(indice , seleccionado){
						if(primero==0){
							valor_campo += seleccionado.value;
							primero=1;
						}else{
							valor_campo += "," + seleccionado.value;
						}
					});
					$campo_suc_on.attr({'value' : valor_campo });
					return true;
				});
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-almacenes-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-almacenes-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllAlmacenes.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllAlmacenes.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



