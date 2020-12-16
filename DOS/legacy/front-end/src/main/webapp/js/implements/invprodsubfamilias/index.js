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
	var controller = $contextpath.val()+"/controllers/invprodsubfamilias";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_subfamilia = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Subfamilias');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_subfamilia = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_subfamilia]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	var $busqueda_select_familia = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_familia]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "busqueda_subfamilia" + signo_separador + $busqueda_subfamilia.val() + "|";
		valor_retorno += "busqueda_descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		valor_retorno += "busqueda_select_familia" + signo_separador + $busqueda_select_familia.val() + "|";
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
		$busqueda_familia.val('');
		$busqueda_descripcion.val('');
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
	
	
	
	
	//obtiene todas las familias para el buscador
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFamilias.json';
	$arreglo = {'iu': $('#lienzo_recalculable').find('input[name=iu]').val() };
	$.post(input_json,$arreglo,function(entry){
		//Alimentando los campos select de familias del buscador
		$busqueda_select_familia.children().remove();
		var familia_hmtl = '<option value="0" selected="yes">[--Familia--]</option>';
		$.each(entry['Familias'],function(entryIndex,fam){
			familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
		});
		$busqueda_select_familia.append(familia_hmtl);
                
	});//termina llamada json
	
	
	
	
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-invprodsubfamilias-window').find('#submit').mouseover(function(){
			$('#forma-invprodsubfamilias-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invprodsubfamilias-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-invprodsubfamilias-window').find('#submit').mouseout(function(){
			$('#forma-invprodsubfamilias-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-invprodsubfamilias-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-invprodsubfamilias-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invprodsubfamilias-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invprodsubfamilias-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invprodsubfamilias-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invprodsubfamilias-window').find('#close').mouseover(function(){
			$('#forma-invprodsubfamilias-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invprodsubfamilias-window').find('#close').mouseout(function(){
			$('#forma-invprodsubfamilias-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-invprodsubfamilias-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invprodsubfamilias-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invprodsubfamilias-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invprodsubfamilias-window').find("ul.pestanas li").click(function() {
			$('#forma-invprodsubfamilias-window').find(".contenidoPes").hide();
			$('#forma-invprodsubfamilias-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invprodsubfamilias-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
		
	}
	
	
	
	
	
	//nueva subfamilia
	$new_subfamilia.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_invprodsubfamilias();
		
		var form_to_show = 'formainvprodsubfamilias00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-invprodsubfamilias-window').css({ "margin-left": -300, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-invprodsubfamilias-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-invprodsubfamilias-window').find('input[name=identificador]');
		var $subfamilia = $('#forma-invprodsubfamilias-window').find('input[name=subfamilia]');
		var $campo_descripcion = $('#forma-invprodsubfamilias-window').find('input[name=descripcion]');
		var $descripcion_familia = $('#forma-invprodsubfamilias-window').find('input[name=descripcion_familia]');
		var $select_familia = $('#forma-invprodsubfamilias-window').find('select[name=select_familia]');
		var $select_tipo_producto = $('#forma-invprodsubfamilias-window').find('select[name=tipo_producto]');
		
		var $cerrar_plugin = $('#forma-invprodsubfamilias-window').find('#close');
		var $cancelar_plugin = $('#forma-invprodsubfamilias-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invprodsubfamilias-window').find('#submit');
		
		$campo_id.attr({ 'value' : 0 });
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Subfamilia fue dado de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-invprodsubfamilias-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invprodsubfamilias-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-invprodsubfamilias-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSubFamilia.json';
		$arreglo = {	'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		$.post(input_json,$arreglo,function(entry){
			
			
			//Alimentando los campos select de familias
			$select_familia.children().remove();
			var familia_hmtl = '<option value="0" selected="yes">[--Seleccionar Familia--]</option>';
			$.each(entry['Familias'],function(entryIndex,fam){
				familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
			});
			$select_familia.append(familia_hmtl);
			
			
			$select_familia.change(function(){
				$descripcion_familia.val('');
				var id_familia_seleccionado = $(this).val();
				$.each(entry['Familias'],function(entryIndex,fam){
					if(parseInt(id_familia_seleccionado)==parseInt(fam['id'])){
						$descripcion_familia.val(fam['descripcion']);
					}
				});
			});
			
                        
                        $select_tipo_producto.children().remove();
                        var tipo_prod_hmtl = '<option value="0" selected="yes">[--Familia--]</option>';
                        $.each(entry['TiposProd'],function(entryIndex,tipo){
                                tipo_prod_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
                        });
                        $select_tipo_producto.append(tipo_prod_hmtl);
                        
		},"json");//termina llamada json
		
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-invprodsubfamilias-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-invprodsubfamilias-overlay').fadeOut(remove);
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
			jConfirm('Realmente desea eliminar la Subfamilia seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Subfamilia fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Subfamilia no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formainvprodsubfamilias00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_invprodsubfamilias();
			$('#forma-invprodsubfamilias-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-invprodsubfamilias-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-invprodsubfamilias-window').find('input[name=identificador]');
			var $subfamilia = $('#forma-invprodsubfamilias-window').find('input[name=subfamilia]');
			var $campo_descripcion = $('#forma-invprodsubfamilias-window').find('input[name=descripcion]');
			var $descripcion_familia = $('#forma-invprodsubfamilias-window').find('input[name=descripcion_familia]');
			var $select_familia = $('#forma-invprodsubfamilias-window').find('select[name=select_familia]');
			var $select_tipo_producto = $('#forma-invprodsubfamilias-window').find('select[name=tipo_producto]');
                        
			var $cerrar_plugin = $('#forma-invprodsubfamilias-window').find('#close');
			var $cancelar_plugin = $('#forma-invprodsubfamilias-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invprodsubfamilias-window').find('#submit');
			
			
			
			if(accion_mode == 'edit'){
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSubFamilia.json';
				$arreglo = {	'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
                                        
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-invprodsubfamilias-overlay').fadeOut(remove);
						jAlert("Los datos de la Subfamilia se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-invprodsubfamilias-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-invprodsubfamilias-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$campo_id.attr({ 'value' : entry['Subfamilia']['0']['id'] });
					$subfamilia.attr({ 'value' : entry['Subfamilia']['0']['titulo'] });
					$campo_descripcion.attr({ 'value' : entry['Subfamilia']['0']['descripcion'] });
					
					
                                        
					//Alimentando los campos select de familias
					$select_familia.children().remove();
					var familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
					$.each(entry['Familias'],function(entryIndex,fam){
						if( parseInt(fam['id']) == parseInt(entry['Subfamilia']['0']['identificador_familia_padre']) ){
							familia_hmtl += '<option value="' + fam['id'] + '"  selected="yes" >' + fam['titulo'] + '</option>';
						}else{
							familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
						}
					});
					$select_familia.append(familia_hmtl);
					
					
								
					$select_familia.change(function(){
						$descripcion_familia.val('');
						var id_familia_seleccionado = $(this).val();
						$.each(entry['Familias'],function(entryIndex,fam){
							if(parseInt(id_familia_seleccionado)==parseInt(fam['id'])){
								$descripcion_familia.val(fam['descripcion']);
							}
						});
					});
					
                                        $select_tipo_producto.children().remove();
                                        tipo_prod_hmtl = "";
                                        $.each(entry['TiposProd'],function(entryIndex,tipo){
                                            if(entry['Subfamilia']['0']['inv_prod_tipo_id'] == tipo['id']){
                                                tipo_prod_hmtl += '<option value="' + tipo['id'] + '" selected="yes" >' + tipo['titulo'] + '</option>';
                                            }else{
                                                tipo_prod_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
                                            }
                                        });
                                        $select_tipo_producto.append(tipo_prod_hmtl);
					
				},"json");//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-invprodsubfamilias-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-invprodsubfamilias-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllSubFamilias.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllSubFamilias.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



