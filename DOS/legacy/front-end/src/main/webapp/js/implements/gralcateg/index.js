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
	var controller = $contextpath.val()+"/controllers/gralcateg";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_categ= $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Categor&iacute;a');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
	var $cadena_busqueda = "";
        var id_to_show = 0;
	var $busqueda_categ = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_categ]');
	var $busqueda_sueldo_hora = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_sueldo_hora]');
        var $busqueda_sueldo_hora_extra= $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_sueldo_hora_extra]');
	var $busqueda_select_puesto = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_puesto]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
        var $select_puesto = $('#forma-gralcateg-window').find('select[name=select_puesto]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "categ" + signo_separador + $busqueda_categ.val() + "|";
		valor_retorno += "sueldo_hora" + signo_separador + $busqueda_sueldo_hora.val() + "|";
		valor_retorno += "sueldo_hora_extra" + signo_separador + $busqueda_sueldo_hora_extra.val() + "|";
                valor_retorno += "puesto" + signo_separador + $busqueda_select_puesto.val() + "|";
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
		$busqueda_categ.val('');
		$busqueda_sueldo_hora.val('');
                $busqueda_sueldo_hora_extra.val('');
	});
	

        $busqueda_sueldo_hora.keypress(function(e){
                                //alert(e.which);
                                // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                                if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                        return true;
                                }else {
                                        return false;
                                }
        });
        
        $busqueda_sueldo_hora_extra.keypress(function(e){
                                //alert(e.which);
                                // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                                if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                        return true;
                                }else {
                                        return false;
                                }
        });
        
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

        
        
        
        
        
        $aplicar_evento_focus_input = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val() == ' ' || parseFloat($(this).val()) == 0){
				$(this).val('');
			}
		});
	}
	
	$aplicar_evento_blur_input = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if(parseFloat($campo_input.val())==0 || $campo_input.val()==""){
				$campo_input.val('0');
			}
		});
	}
	
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
	
//Esto es para llenar el select de puestos  en la vista
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPuestos.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json,$arreglo,function(data){
		//Llena el select tipos de puestos en el buscador
		$busqueda_select_puesto.children().remove();
		var puesto_hmtl = '<option value="0" selected="yes">[--Seleccione un Puesto --]</option>';
		$.each(data['Puestos'],function(entryIndex,pt){
			puesto_hmtl += '<option value="' + pt['id'] + '"  >' + pt['puesto'] + '</option>';
		});
		$busqueda_select_puesto.append(puesto_hmtl);
	});
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-gralcateg-window').find('#submit').mouseover(function(){
			$('#forma-gralcateg-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-gralcateg-window').find('#submit').mouseout(function(){
			$('#forma-gralcateg-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-gralcateg-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-gralcateg-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-gralcateg-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-gralcateg-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-gralcateg-window').find('#close').mouseover(function(){
			$('#forma-gralcateg-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-gralcateg-window').find('#close').mouseout(function(){
			$('#forma-gralcateg-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-gralcateg-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-gralcateg-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-gralcateg-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-gralcateg-window').find("ul.pestanas li").click(function() {
			$('#forma-gralcateg-window').find(".contenidoPes").hide();
			$('#forma-gralcateg-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-gralcateg-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	//nuevo 
	$new_categ.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_GralCateg();
		
		var form_to_show = 'formaCateg';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-gralcateg-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-gralcateg-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-gralcateg-window').find('input[name=identificador]');
		var $categ = $('#forma-gralcateg-window').find('input[name=categoria]');
		var $sueldo_hora = $('#forma-gralcateg-window').find('input[name=sueldo_hora]');
                var $sueldo_hora_extra = $('#forma-gralcateg-window').find('input[name=sueldo_hora_extra]');
		var $select_puesto = $('#forma-gralcateg-window').find('select[name=select_puesto]');
		$sueldo_hora.val(0);
                $sueldo_hora_extra.val(0);
		var $cerrar_plugin = $('#forma-gralcateg-window').find('#close');
		var $cancelar_plugin = $('#forma-gralcateg-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-gralcateg-window').find('#submit');
		
                               
                $sueldo_hora.blur(function(e){
                //patron = /^(0[1-9]|1\d|2[0-3]):([0-5]\d):([0-5]\d)$/;
                //patron = /^(\d{1}\.)?(\d+\.?)+(,\d{2})?$/; 
                    patron = /^\d+(\.\d{1,2})?$/;
                    if(!patron.test($(this).val())){
                        jAlert("Formato de n&uacute;mero no v&aacute;lido Ej. 2500.50", 'Atencion!');
                    }               

                });

                $sueldo_hora_extra.blur(function(e){ 
                    patron = /^\d+(\.\d{1,2})?$/;
                    if(!patron.test($(this).val())){
                        jAlert("Formato de n&uacute;mero no v&aacute;lido Ej. 2500.50", 'Atencion!');
                    }
                });               

            
                
		$campo_id.attr({'value' : 0});
                
                                
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("categor&iacute;a dada de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-gralcateg-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-gralcateg-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-gralcateg-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCateg_Datos.json'; //getCateg_Datos
		
		$arreglo = {'id':id_to_show, 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()};
		
		$.post(input_json,$arreglo,function(entry){
			//Alimentando los campos select de puestos
			$select_puesto.children().remove();
			var puesto_hmtl = '<option value="0" selected="yes">[--Seleccionar puesto--]</option>';
			$.each(entry['Puestos'],function(entryIndex,puesto){
				puesto_hmtl += '<option value="' + puesto['id'] + '"  >' + puesto['puesto'] + '</option>';
			});
			$select_puesto.append(puesto_hmtl);
		});//termina llamada json
		
                $aplicar_evento_focus_input($sueldo_hora);
                $aplicar_evento_blur_input($sueldo_hora);
                $aplicar_evento_focus_input($sueldo_hora_extra);
                $aplicar_evento_blur_input($sueldo_hora_extra);
                $aplicar_evento_keypress($sueldo_hora); 
                $aplicar_evento_keypress($sueldo_hora_extra);
                
                
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-gralcateg-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-gralcateg-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
	});
	
	
	var carga_formaCateg_for_datagrid = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Categoía seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La categor&iacute;a fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La categor&iacute;a no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaCateg';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_GralCateg();
			$('#forma-gralcateg-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-gralcateg-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-gralcateg-window').find('input[name=identificador]');
                        var $categ = $('#forma-gralcateg-window').find('input[name=categoria]');
			var $sueldo_hora = $('#forma-gralcateg-window').find('input[name=sueldo_hora]');
                        var $sueldo_hora_extra = $('#forma-gralcateg-window').find('input[name=sueldo_hora_extra]');
			var $select_puesto = $('#forma-gralcateg-window').find('select[name=select_puesto]');
			var $cerrar_plugin = $('#forma-gralcateg-window').find('#close');
			var $cancelar_plugin = $('#forma-gralcateg-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-gralcateg-window').find('#submit');
		
			if(accion_mode == 'edit'){ //para editar un row
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCateg_Datos.json';
				$arreglo = {'id':id_to_show};
				//$arreglo = {'iu':_to_show};
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-gralcateg-overlay').fadeOut(remove);
						jAlert("La categor&iacute;a se ha actualizado", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-gralcateg-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-gralcateg-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					
					$campo_id.attr({ 'value' : entry['Categ']['0']['id'] });
					$categ.attr({ 'value' : entry['Categ']['0']['categoria'] });
					$sueldo_hora.attr({ 'value' : entry['Categ']['0']['sueldo_por_hora'] });
                                        $sueldo_hora_extra.attr({'value' : entry['Categ']['0']['sueldo_por_horas_ext']});
					
					
					$select_puesto.children().remove();
					var puesto_hmtl = '<option value="0">[--Seleccionar--]</option>';
					$.each(entry['Puestos'],function(entryIndex,puesto){
						if(puesto['id'] == entry['Categ']['0']['puesto']){
							puesto_hmtl += '<option value="' + puesto['id'] + '" selected="yes">' + puesto['puesto'] + '</option>';
						}else{
							puesto_hmtl += '<option value="' + puesto['id'] + '"  >' + puesto['puesto'] + '</option>';
						}
					});
					$select_puesto.append(puesto_hmtl);
					
				},"json");//termina llamada json
                                
                                $aplicar_evento_keypress($sueldo_hora); 
                                $aplicar_evento_keypress($sueldo_hora_extra); 
                                $aplicar_evento_focus_input($sueldo_hora);
                                $aplicar_evento_blur_input($sueldo_hora_extra);
                                
                                $sueldo_hora.blur(function(e){ 
                                    patron = /^\d+(\.\d{1,2})?$/;
                                    if(!patron.test($(this).val())){
                                        jAlert("Formato de n&uacute;mero no v&aacute;lido Ej. 2500.50", 'Atencion!');
                                    }               

                                });

                                $sueldo_hora_extra.blur(function(e){ 
                                    patron = /^\d+(\.\d{1,2})?$/;
                                    if(!patron.test($(this).val())){
                                        jAlert("Formato de n&uacute;mero no v&aacute;lido Ej. 2500.50", 'Atencion!');
                                    }
                                }); 
                                
                                
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralcateg-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralcateg-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}//termina edicion de row
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllCategs.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllCategs.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCateg_for_datagrid);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
});



