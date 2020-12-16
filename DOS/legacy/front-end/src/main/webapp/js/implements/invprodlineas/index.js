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
	var controller = $contextpath.val()+"/controllers/invprodlineas";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_invprodlineas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de L&iacute;neas');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_seccion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_seccion]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	//var $busqueda_select_grupo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_grupo]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "seccion" + signo_separador + $busqueda_seccion.val() + "|";
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
		
		$('#forma-invprodlineas-window').find('#submit').mouseover(function(){
			$('#forma-invprodlineas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-invprodlineas-window').find('#submit').mouseout(function(){
			$('#forma-invprodlineas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-invprodlineas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invprodlineas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invprodlineas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invprodlineas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invprodlineas-window').find('#close').mouseover(function(){
			$('#forma-invprodlineas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invprodlineas-window').find('#close').mouseout(function(){
			$('#forma-invprodlineas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-invprodlineas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invprodlineas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invprodlineas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invprodlineas-window').find("ul.pestanas li").click(function() {
			$('#forma-invprodlineas-window').find(".contenidoPes").hide();
			$('#forma-invprodlineas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invprodlineas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	$agregar_remover_marcas = function(){
		var $marcasdisponibles = $('#forma-invprodlineas-window').find('select[name=marcasdisponibles]');
		var $marcasseleccionadas = $('#forma-invprodlineas-window').find('select[name=marcasseleccionadas]');
		var $agregar_marca = $('#forma-invprodlineas-window').find('a[href*=agregar_marca]');
		var $remover_marca = $('#forma-invprodlineas-window').find('a[href*=remover_marca]');
		var $pres_on = $('#forma-invprodlineas-window').find('input[name=pres_on]');
            
		$agregar_marca.click(function(event){
			event.preventDefault();
			var logica = false;
			var primero=0;
			logica = !$marcasdisponibles.find('option:selected').remove().appendTo( $marcasseleccionadas);
			var valor_campo = "";
			var ahora_seleccionados = $marcasseleccionadas.find('option').get();
			$.each( ahora_seleccionados , function(indice , seleccionado){
				if(primero==0){
					valor_campo += seleccionado.value;
					primero=1;
				}else{
					valor_campo += "," + seleccionado.value;
				}
			});
			$pres_on.attr({'value' : valor_campo });
			return logica; 
		});
		
		
		//remover presentacion
		$remover_marca.click(function(event){
			event.preventDefault();
			var logica = false;
			var primero=0;
			logica = !$marcasseleccionadas.find('option:selected').remove().appendTo($marcasdisponibles);
			var valor_campo = "";
			var ahora_seleccionados = $marcasseleccionadas.find('option').get();
			$.each( ahora_seleccionados , function(indice , seleccionado){
				if(primero==0){
					valor_campo += seleccionado.value;
					primero=1;
				}else{
					valor_campo += "," + seleccionado.value;
				}
			});
			//alert(valor_campo);
			$pres_on.attr({'value' : valor_campo }); 
			return logica;
		});
        }
	
	
	//nuevo centro de costo
	$new_invprodlineas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_modalboxInvProdLineas();
		
		var form_to_show = 'formaInvProdLineas00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-invprodlineas-window').css({ "margin-left": -300, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-invprodlineas-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-invprodlineas-window').find('input[name=identificador]');
		var $titulo = $('#forma-invprodlineas-window').find('input[name=titulo]');
		var $descripcion = $('#forma-invprodlineas-window').find('input[name=descripcion]');
		var $seccion = $('#forma-invprodlineas-window').find('select[name=seccion]');
		var $marcasdisponibles = $('#forma-invprodlineas-window').find('select[name=marcasdisponibles]');
                var $marcasseleccionadas = $('#forma-invprodlineas-window').find('select[name=marcasseleccionadas]');
                var $agregar_marca = $('#forma-invprodlineas-window').find('a[href*=agregar_marca]');
		var $remover_marca = $('#forma-invprodlineas-window').find('a[href*=remover_marca]');
                var $pres_on = $('#forma-invprodlineas-window').find('input[name=pres_on]');
                
		var $cerrar_plugin = $('#forma-invprodlineas-window').find('#close');
		var $cancelar_plugin = $('#forma-invprodlineas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invprodlineas-window').find('#submit');
		
		$campo_id.attr({ 'value' : 0 });
                
		var respuestaProcesada = function(data){
			if ( data['success'] == 'true' ){
				var remove = function() { $(this).remove(); };
				$('#forma-invprodlineas-overlay').fadeOut(remove);
				jAlert("La linea se ha actualizado.", 'Atencion!');
				$get_datos_grid();
			}
			else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invprodlineas-window').find('div.interrogacion').css({'display':'none'});

				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
							$('#forma-invprodlineas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvProdLinea.json';
		$arreglo = {'id':id_to_show,
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
		
					};
		
		$.post(input_json,$arreglo,function(entry){
			
                        //Alimentando los campos select de secciones
			$seccion.children().remove();
			var seccion_hmtl = '<option value="0" selected="yes">[--Seleccionar una seccion--]</option>';
			$.each(entry['Secciones'],function(entryIndex,seccion){
				seccion_hmtl += '<option value="' + seccion['id'] + '"  >' + seccion['titulo'] + '</option>';
			});
			$seccion.append(seccion_hmtl);
                        
                        //Alimentando los campos select de Marcas
			$marcasdisponibles.children().remove();
			var marcasdisponibles = '';
			$.each(entry['Marcas'],function(entryIndex,marca){
				marcasdisponibles += '<option value="' + marca['id'] + '"  >' + marca['titulo'] + '</option>';
			});
			$marcasdisponibles.append(marcasdisponibles);
                        
		});//termina llamada json
		
                $agregar_remover_marcas();
                
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-invprodlineas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-invprodlineas-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
        });
	
        
	
	var carga_formaInvProdLineas00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la linea seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La linea fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La linea no puda ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaInvProdLineas00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_modalboxInvProdLineas();
			$('#forma-invprodlineas-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-invprodlineas-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();

			var $campo_id = $('#forma-invprodlineas-window').find('input[name=identificador]');
			var $titulo = $('#forma-invprodlineas-window').find('input[name=titulo]');
			var $descripcion = $('#forma-invprodlineas-window').find('input[name=descripcion]');
			var $seccion = $('#forma-invprodlineas-window').find('select[name=seccion]');
			var $marcasdisponibles = $('#forma-invprodlineas-window').find('select[name=marcasdisponibles]');
			var $marcasseleccionadas = $('#forma-invprodlineas-window').find('select[name=marcasseleccionadas]');
			var $agregar_marca = $('#forma-invprodlineas-window').find('a[href*=agregar_marca]');
			var $remover_marca = $('#forma-invprodlineas-window').find('a[href*=remover_marca]');
			var $pres_on = $('#forma-invprodlineas-window').find('input[name=pres_on]');
			
                        
			var $cerrar_plugin = $('#forma-invprodlineas-window').find('#close');
			var $cancelar_plugin = $('#forma-invprodlineas-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invprodlineas-window').find('#submit');
			
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvProdLinea.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-invprodlineas-overlay').fadeOut(remove);
						jAlert("La linea se ha actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-invprodlineas-window').find('div.interrogacion').css({'display':'none'});

						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-invprodlineas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$campo_id.attr({ 'value' : entry['InvLineas']['0']['id'] });
					$titulo.attr({ 'value' : entry['InvLineas']['0']['titulo'] });
					$descripcion.attr({ 'value' : entry['InvLineas']['0']['descripcion'] });
					
					//Alimentando los campos select de secciones
					$seccion.children().remove();
					var seccion_hmtl = '';
					$.each(entry['Secciones'],function(entryIndex,seccion){
						if(seccion['id'] == entry['InvLineas']['0']['descripcion']){
							seccion_hmtl += '<option value="' + seccion['id'] + '"  selected="yes">' + seccion['titulo'] + '</option>';
						}else{
							seccion_hmtl += '<option value="' + seccion['id'] + '"  >' + seccion['titulo'] + '</option>';
						}
					});
					$seccion.append(seccion_hmtl);
					
					//Alimentando los campos select de Marcas
					$marcasdisponibles.children().remove();
					$marcasseleccionadas.children().remove();
					var marcasdisponibles = '';
					var marcasseleccionadas = '';
					var valor_campo = "";
					var primero = 0;
					$.each(entry['Marcas'],function(entryIndex,marca){
						var $estatus = 0;
						$.each(entry['MarcasSeleccionadas'],function(entryIndex1,marcaseleccionada){
							if(marca['id'] == marcaseleccionada['inv_mar_id']){
								marcasseleccionadas += '<option value="' + marca['id'] + '"  >' + marca['titulo'] + '</option>';
								$estatus = 1;
								if(primero==0){
									valor_campo += marca['id'];
									primero=1;
								}else{
									valor_campo += "," + marca['id'];
								}
							}
						});
						if($estatus == 0){
							marcasdisponibles += '<option value="' + marca['id'] + '"  >' + marca['titulo'] + '</option>';
						}
					});
					$marcasdisponibles.append(marcasdisponibles);
					$marcasseleccionadas.append(marcasseleccionadas);
					$pres_on.attr({'value' : valor_campo });
						
				},"json");//termina llamada json
				
				$agregar_remover_marcas();
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-invprodlineas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-invprodlineas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllInvProdLineas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllInvProdLineas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaInvProdLineas00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



