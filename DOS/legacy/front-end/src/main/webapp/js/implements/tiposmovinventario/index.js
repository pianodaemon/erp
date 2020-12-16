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
    var controller = $contextpath.val()+"/controllers/tiposmovinventario";
    
    var grupos = {0:"Entradas", 1:"Existencia Inicial", 2:"Salidas", 3:"Traspasos"};
    var tipo_costo = {0:"Alimentado", 1:"Promedio", 2:"Ultima Entrada"};
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_tipo_mov_inv = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Tipos de Moviemintos de Inventario');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_tipo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_tipo]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	//var $busqueda_select_grupo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_grupo]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "tipo" + signo_separador + $busqueda_tipo.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		//valor_retorno += "grupo" + signo_separador + $busqueda_select_grupo.val() + "|";
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
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-tiposmovinventario-window').find('#submit').mouseover(function(){
			$('#forma-tiposmovinventario-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-tiposmovinventario-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-tiposmovinventario-window').find('#submit').mouseout(function(){
			$('#forma-tiposmovinventario-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-tiposmovinventario-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-tiposmovinventario-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-tiposmovinventario-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-tiposmovinventario-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-tiposmovinventario-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-tiposmovinventario-window').find('#close').mouseover(function(){
			$('#forma-tiposmovinventario-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-tiposmovinventario-window').find('#close').mouseout(function(){
			$('#forma-tiposmovinventario-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-tiposmovinventario-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-tiposmovinventario-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-tiposmovinventario-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-tiposmovinventario-window').find("ul.pestanas li").click(function() {
			$('#forma-tiposmovinventario-window').find(".contenidoPes").hide();
			$('#forma-tiposmovinventario-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-tiposmovinventario-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	
	
	//nuevo centro de costo
	$new_tipo_mov_inv.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_modalboxTipoInMovIventarios();
		
		var form_to_show = 'formaTipoMovInv00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-tiposmovinventario-window').css({ "margin-left": -300, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-tiposmovinventario-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-tiposmovinventario-window').find('input[name=identificador]');
		var $tipo = $('#forma-tiposmovinventario-window').find('input[name=tipo]');
		var $descripcion = $('#forma-tiposmovinventario-window').find('input[name=descripcion]');
		var $mov_de_ajuste = $('#forma-tiposmovinventario-window').find('input[name=mov_de_ajuste]');
		
		var $grupo = $('#forma-tiposmovinventario-window').find('select[name=grupo]');
		var $afecta_compras = $('#forma-tiposmovinventario-window').find('input[name=afecta_compras]');
		var $tipocosto = $('#forma-tiposmovinventario-window').find('select[name=tipo_costo]');
		var $afecta_ventas = $('#forma-tiposmovinventario-window').find('input[name=afecta_ventas]');
		var $considera_consumo = $('#forma-tiposmovinventario-window').find('input[name=considera_consumo]');
		
                
                
		var $cerrar_plugin = $('#forma-tiposmovinventario-window').find('#close');
		var $cancelar_plugin = $('#forma-tiposmovinventario-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-tiposmovinventario-window').find('#submit');
		
		$campo_id.attr({ 'value' : 0 });
                $mov_de_ajuste.attr('checked', false);
                
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Tipo de Movivmieto dado de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-tiposmovinventario-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-tiposmovinventario-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-tiposmovinventario-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		
		$grupo.children().remove();
		var grupo_hmtl = '<option value="0" selected="yes">[--Seleccionar grupo--]</option>';
		for(var i in grupos){   
			grupo_hmtl += '<option value="' + i + '"  >' + grupos[i] + '</option>';
		}
		$grupo.append(grupo_hmtl);
		
		
		$tipocosto.children().remove();
		var tipo_hmtl = '<option value="0" selected="yes">[--Seleccionar grupo--]</option>';
		for(var i in tipo_costo){
			tipo_hmtl += '<option value="' + i + '"  >' + tipo_costo[i] + '</option>';
		}
		$tipocosto.append(tipo_hmtl);
		
		
		//aplicar click a checkbox de ajuste
		$mov_de_ajuste.click(function(event){
			if( this.checked ){
				
				//si es tipo ajuste, el tipo de costo solo debe ser 0:"Entradas", 2:"Salidas"
				$grupo.children().remove();
				var grupo_hmtl = '';
				for(var i in grupos){
					if(parseInt(i)==0 || parseInt(i)==2){
						grupo_hmtl += '<option value="' + i + '"  >' + grupos[i] + '</option>';
					}
				}
				$grupo.append(grupo_hmtl);
				
				//si es tipo ajuste, el tipo de costo solo debe ser 0:"Alimentado", 1:"Promedio"
				$tipocosto.children().remove();
				var tipo_hmtl = '';
				for(var i in tipo_costo){
					if(parseInt(i)==0 || parseInt(i)==1){
						tipo_hmtl += '<option value="' + i + '"  >' + tipo_costo[i] + '</option>';
					}
				}
				$tipocosto.append(tipo_hmtl);
				
			}else{
				$grupo.children().remove();
				var grupo_hmtl = '<option value="0" selected="yes">[--Seleccionar grupo--]</option>';
				for(var i in grupos){   
					grupo_hmtl += '<option value="' + i + '"  >' + grupos[i] + '</option>';
				}
				$grupo.append(grupo_hmtl);
				
				
				$tipocosto.children().remove();
				var tipo_hmtl = '<option value="0" selected="yes">[--Seleccionar grupo--]</option>';
				for(var i in tipo_costo){
					tipo_hmtl += '<option value="' + i + '"  >' + tipo_costo[i] + '</option>';
				}
				$tipocosto.append(tipo_hmtl);
			}
			
		});


		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-tiposmovinventario-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-tiposmovinventario-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
	});
	
	
        
        
        
	
	var carga_formaTiposmovinventario00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Tipo de movimiento seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El tipo de movimiento fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El tipo de movimiento no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaTipoMovInv00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_modalboxTipoInMovIventarios();
			$('#forma-tiposmovinventario-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-tiposmovinventario-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();

			var $campo_id = $('#forma-tiposmovinventario-window').find('input[name=identificador]');
			var $tipo = $('#forma-tiposmovinventario-window').find('input[name=tipo]');
			var $descripcion = $('#forma-tiposmovinventario-window').find('input[name=descripcion]');
			var $mov_de_ajuste = $('#forma-tiposmovinventario-window').find('input[name=mov_de_ajuste]');
			
			var $grupo = $('#forma-tiposmovinventario-window').find('select[name=grupo]');
			var $afecta_compras = $('#forma-tiposmovinventario-window').find('input[name=afecta_compras]');
			var $tipocosto = $('#forma-tiposmovinventario-window').find('select[name=tipo_costo]');
			var $afecta_ventas = $('#forma-tiposmovinventario-window').find('input[name=afecta_ventas]');
			var $considera_consumo = $('#forma-tiposmovinventario-window').find('input[name=considera_consumo]');
			
                        
			var $cerrar_plugin = $('#forma-tiposmovinventario-window').find('#close');
			var $cancelar_plugin = $('#forma-tiposmovinventario-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-tiposmovinventario-window').find('#submit');
			
			$mov_de_ajuste.attr('checked', false);
                        
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTipoMovInventario.json';
				$arreglo = {'id':id_to_show};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-tiposmovinventario-overlay').fadeOut(remove);
						jAlert("El Tipo de Movimiento se ha actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-tiposmovinventario-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-tiposmovinventario-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					
					$campo_id.attr({ 'value' : entry['TipoMov']['0']['id'] });
					$tipo.attr({ 'value' : entry['TipoMov']['0']['titulo'] });
					$descripcion.attr({ 'value' : entry['TipoMov']['0']['descripcion'] });
					
					if(entry['TipoMov'][0]['ajuste'] == 'true'){
						$mov_de_ajuste.attr('checked', true);
					}else{
						$mov_de_ajuste.attr('checked', false);
					}
					
					if(entry['TipoMov'][0]['afecta_compreas'] == 'true'){
						$afecta_compras.attr('checked', true);
					}else{
						$afecta_compras.attr('checked', false);
					}
					if(entry['TipoMov'][0]['afecta_ventas'] == 'true'){
						$afecta_ventas.attr('checked', true);
					}else{
						$afecta_ventas.attr('checked', false);
					}
					if(entry['TipoMov'][0]['considera_consumo'] == 'true'){
						$considera_consumo.attr('checked', true);
					}else{
						$considera_consumo.attr('checked', false);
					}
					
					$grupo.children().remove();
					var grupo_hmtl = '';
					for(var i in grupos){
						if(entry['TipoMov']['0']['grupo'] == i){
							grupo_hmtl += '<option value="' + i + '"  selected="yes">' + grupos[i] + '</option>';
						}else{
							if(entry['TipoMov'][0]['ajuste'] == 'true'){
								if(parseInt(i)==0 || parseInt(i)==2){
									grupo_hmtl += '<option value="' + i + '"  >' + grupos[i] + '</option>';
								}
							}else{
								grupo_hmtl += '<option value="' + i + '"  >' + grupos[i] + '</option>';
							}
						}
					}
					$grupo.append(grupo_hmtl);
					
					
					$tipocosto.children().remove();
					var tipo_hmtl = '';
					for(var i in tipo_costo){
						if(entry['TipoMov']['0']['tipo_costo'] == i){
							tipo_hmtl += '<option value="' + i + '" selected="yes" >' + tipo_costo[i] + '</option>';
						}else{
							if(entry['TipoMov'][0]['ajuste'] == 'true'){
								if(parseInt(i)==0 || parseInt(i)==1){
									tipo_hmtl += '<option value="' + i + '"  >' + tipo_costo[i] + '</option>';
								}
							}else{
								tipo_hmtl += '<option value="' + i + '" >' + tipo_costo[i] + '</option>';
							}
						}
					}
					$tipocosto.append(tipo_hmtl);
					
					
					
					
					//aplicar click a checkbox de ajuste
					$mov_de_ajuste.click(function(event){
						if( this.checked ){
							
							//si es tipo ajuste, el tipo de costo solo debe ser 0:"Entradas", 2:"Salidas"
							$grupo.children().remove();
							var grupo_hmtl = '';
							for(var i in grupos){
								if(parseInt(i)==0 || parseInt(i)==2){
									if(entry['TipoMov']['0']['grupo'] == i){
										grupo_hmtl += '<option value="' + i + '"  selected="yes">' + grupos[i] + '</option>';
									}else{
										grupo_hmtl += '<option value="' + i + '"  >' + grupos[i] + '</option>';
									}
								}
							}
							$grupo.append(grupo_hmtl);
							
							//si es tipo ajuste, el tipo de costo solo debe ser 0:"Alimentado", 1:"Promedio"
							$tipocosto.children().remove();
							var tipo_hmtl = '';
							for(var i in tipo_costo){
								if(parseInt(i)==0 || parseInt(i)==1){
									if(entry['TipoMov']['0']['tipo_costo'] == i){
										tipo_hmtl += '<option value="' + i + '" selected="yes" >' + tipo_costo[i] + '</option>';
									}else{
										tipo_hmtl += '<option value="' + i + '"  >' + tipo_costo[i] + '</option>';
									}
								}
							}
							$tipocosto.append(tipo_hmtl);
							
						}else{
							$grupo.children().remove();
							var grupo_hmtl = '<option value="0" selected="yes">[--Seleccionar grupo--]</option>';
							for(var i in grupos){   
								if(entry['TipoMov']['0']['grupo'] == i){
									grupo_hmtl += '<option value="' + i + '"  selected="yes">' + grupos[i] + '</option>';
								}else{
									grupo_hmtl += '<option value="' + i + '"  >' + grupos[i] + '</option>';
								}
							}
							$grupo.append(grupo_hmtl);
							
							
							$tipocosto.children().remove();
							var tipo_hmtl = '<option value="0" selected="yes">[--Seleccionar grupo--]</option>';
							for(var i in tipo_costo){
								if(entry['TipoMov']['0']['tipo_costo'] == i){
									tipo_hmtl += '<option value="' + i + '" selected="yes" >' + tipo_costo[i] + '</option>';
								}else{
									tipo_hmtl += '<option value="' + i + '"  >' + tipo_costo[i] + '</option>';
								}
							}
							$tipocosto.append(tipo_hmtl);
							
						}
					});

								
					
					
					
                                        
				},"json");//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-tiposmovinventario-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-tiposmovinventario-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllTipoMovInventario.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllTipoMovInventario.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaTiposmovinventario00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});
