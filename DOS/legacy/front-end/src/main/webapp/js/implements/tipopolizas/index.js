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
	var controller = $contextpath.val()+"/controllers/tipopolizas";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_tipo_poliza = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Tipos de p&oacute;liza');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_tipo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_tipo]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	var $busqueda_select_grupo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_grupo]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "tipo" + signo_separador + $busqueda_tipo.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		valor_retorno += "grupo" + signo_separador + $busqueda_select_grupo.val() + "|";
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
		$busqueda_titulo.val('');
		$busqueda_descripcion.val('');
	});
	
	/*
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
         $('#barra_buscador').toggle( 'blind');
	});	
	*/
	
	
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
	
	
	
	
	//obtiene los grupos para el buscador
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getGruposBuscador.json';
	$arreglo = {};
	$.post(input_json,$arreglo,function(entry){
		//Alimentando los campos select de grupos del buscador
		$busqueda_select_grupo.children().remove();
		var grupo_hmtl = '<option value="0" selected="yes">[--Seleccionar grupo--]</option>';
		$.each(entry['Grupos'],function(entryIndex,grupo){
			grupo_hmtl += '<option value="' + grupo['id'] + '"  >' + grupo['titulo'] + '</option>';
		});
		$busqueda_select_grupo.append(grupo_hmtl);
	});//termina llamada json
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-tipopolizas-window').find('#submit').mouseover(function(){
			$('#forma-tipopolizas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-tipopolizas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-tipopolizas-window').find('#submit').mouseout(function(){
			$('#forma-tipopolizas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-tipopolizas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-tipopolizas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-tipopolizas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-tipopolizas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-tipopolizas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-tipopolizas-window').find('#close').mouseover(function(){
			$('#forma-tipopolizas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-tipopolizas-window').find('#close').mouseout(function(){
			$('#forma-tipopolizas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-tipopolizas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-tipopolizas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-tipopolizas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-tipopolizas-window').find("ul.pestanas li").click(function() {
			$('#forma-tipopolizas-window').find(".contenidoPes").hide();
			$('#forma-tipopolizas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-tipopolizas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	$aplicar_evento_keypress = function( $campo_input ){
		//validar campo cantidad recibida, solo acepte numeros y punto
		$campo_input.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if(e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	
	
	//nuevo centro de costo
	$new_tipo_poliza.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_tipopolizas();
		
		var form_to_show = 'formaTipopolizas00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-tipopolizas-window').css({ "margin-left": -300, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-tipopolizas-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-tipopolizas-window').find('input[name=identificador]');
		var $tipo = $('#forma-tipopolizas-window').find('input[name=tipo]');
		var $descripcion = $('#forma-tipopolizas-window').find('input[name=descripcion]');
		var $select_grupo = $('#forma-tipopolizas-window').find('select[name=select_grupo]');
		
		var $cerrar_plugin = $('#forma-tipopolizas-window').find('#close');
		var $cancelar_plugin = $('#forma-tipopolizas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-tipopolizas-window').find('#submit');
		
		$aplicar_evento_keypress($tipo);
		$campo_id.attr({ 'value' : 0 });
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Tipo de Poliza dado de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-tipopolizas-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-tipopolizas-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-tipopolizas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTipoPoliza.json';
		$arreglo = {'id':id_to_show};
		
		$.post(input_json,$arreglo,function(entry){
			//Alimentando los campos select de grupos
			$select_grupo.children().remove();
			var grupo_hmtl = '<option value="0" selected="yes">[--Seleccionar grupo--]</option>';
			$.each(entry['Grupos'],function(entryIndex,grupo){
				grupo_hmtl += '<option value="' + grupo['id'] + '"  >' + grupo['titulo'] + '</option>';
			});
			$select_grupo.append(grupo_hmtl);
		});//termina llamada json
		

		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-tipopolizas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-tipopolizas-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
                
                
                
		
	});
	
	
        
        
        
	
	var carga_formaTipoPolizas00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Tipo de Poliza seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Tipo de Poliza fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Tipo de Poliza no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaTipopolizas00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_tipopolizas();
			$('#forma-tipopolizas-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-tipopolizas-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-tipopolizas-window').find('input[name=identificador]');
			var $tipo = $('#forma-tipopolizas-window').find('input[name=tipo]');
			var $descripcion = $('#forma-tipopolizas-window').find('input[name=descripcion]');
			var $select_grupo = $('#forma-tipopolizas-window').find('select[name=select_grupo]');

			
			var $cerrar_plugin = $('#forma-tipopolizas-window').find('#close');
			var $cancelar_plugin = $('#forma-tipopolizas-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-tipopolizas-window').find('#submit');
			
			
			$aplicar_evento_keypress($tipo);
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTipoPoliza.json';
				$arreglo = {'id':id_to_show};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-tipopolizas-overlay').fadeOut(remove);
						jAlert("Tipo de Poliza se ha actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-tipopolizas-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-tipopolizas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					
					$campo_id.attr({ 'value' : entry['Tpol']['0']['id'] });
					$tipo.attr({ 'value' : entry['Tpol']['0']['tipo'] });
					$descripcion.attr({ 'value' : entry['Tpol']['0']['titulo'] });
					
					
					$select_grupo.children().remove();
					var grupo_hmtl = '<option value="0">[--Seleccionar grupo--]</option>';
					$.each(entry['Grupos'],function(entryIndex,grupo){
						if(grupo['id'] == entry['Tpol']['0']['grupo_id']){
							grupo_hmtl += '<option value="' + grupo['id'] + '" selected="yes">' + grupo['titulo'] + '</option>';
						}else{
							grupo_hmtl += '<option value="' + grupo['id'] + '"  >' + grupo['titulo'] + '</option>';
						}
					});
					$select_grupo.append(grupo_hmtl);
					
				},"json");//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-tipopolizas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-tipopolizas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllTipoPolizas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllTipoPolizas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaTipoPolizas00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



