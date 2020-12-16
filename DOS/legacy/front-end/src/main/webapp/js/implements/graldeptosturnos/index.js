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
	var controller = $contextpath.val()+"/controllers/graldeptosturnos";
    
        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_depto= $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Turno');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
	var $cadena_busqueda = "";
        var id_to_show = 0;
	var $busqueda_turno = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_turno]');
	var $busqueda_hora_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_hora_inicial]');
        var $busqueda_hora_final= $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_hora_final]');
	var $busqueda_select_depto = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_depto]');	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
        var $select_depto = $('#forma-graldeptosturnos-window').find('select[name=select_depto]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "depto" + signo_separador + $busqueda_select_depto.val() + "|";
		valor_retorno += "turno" + signo_separador + $busqueda_turno.val() + "|";
		valor_retorno += "hora_ini" + signo_separador + $busqueda_hora_inicial.val() + "|";
		valor_retorno += "hora_fin" + signo_separador + $busqueda_hora_final.val() + "|";        
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
		$busqueda_turno.val('');
		$busqueda_hora_inicial.val('');
		$busqueda_hora_final.val('');
                //Esto es para llenar el select de departamentos  en la vista por default al dar en limpiar
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDeptos.json';
                $arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
                $.post(input_json,$arreglo,function(data){
                        //Llena el select tipos de deptos en el buscador
                        $busqueda_select_depto.children().remove();
                        var depto_hmtl = '<option value="0" selected="yes">[--Seleccione un Depto --]</option>';
                        $.each(data['Deptos'],function(entryIndex,pt){
                                depto_hmtl += '<option value="' + pt['id'] + '"  >' + pt['depto'] + '</option>';
                        });
                        $busqueda_select_depto.append(depto_hmtl);
                });
	});
	

        $busqueda_turno.keypress(function(e){
                                //alert(e.which);
                                // Permitir  numeros,    borrar,       uprimir,         TAB,              puntos,      comas
                                if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                        return true;
                                }else {
                                        return false;
                                }
        });
        
        $busqueda_hora_inicial.keypress(function(e){
                                //alert(e.which);
                                // Permitir  numeros,    borrar,       uprimir,         TAB,              puntos,      comas
                                if (e.which == 58 || e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                        return true;
                                }else {
                                        return false;
                                }
        });
        
        $busqueda_hora_final.keypress(function(e){
                                //alert(e.which);
                                // Permitir  numeros,    borrar,       uprimir,         TAB,              puntos,      comas
                                if (e.which == 58 || e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                        return true;
                                }else {
                                        return false;
                                }
        });
        
        $busqueda_hora_inicial.blur(function(e){
            patron = /^(0[1-9]|1\d|2[0-3]):([0-5]\d):([0-5]\d)$/;
            if(!patron.test($(this).val())){
                jAlert("Hora no valida. El formato es HH:MM:SS", 'Atencion!');
            }               
               
        });
        
        
        
        $busqueda_hora_final.blur(function(e){
            patron = /^(0[1-9]|1\d|2[0-3]):([0-5]\d):([0-5]\d)$/;
            if(!patron.test($(this).val())){
                jAlert("Hora no valida. El formato es HH:MM:SS", 'Atencion!');
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
	
//Esto es para llenar el select de departamentos  en la vista
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDeptos.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json,$arreglo,function(data){
		//Llena el select tipos de deptos en el buscador
		$busqueda_select_depto.children().remove();
		var depto_hmtl = '<option value="0" selected="yes">[--Seleccione un Depto --]</option>';
		$.each(data['Deptos'],function(entryIndex,pt){
			depto_hmtl += '<option value="' + pt['id'] + '"  >' + pt['depto'] + '</option>';
		});
		$busqueda_select_depto.append(depto_hmtl);
	});
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-graldeptosturnos-window').find('#submit').mouseover(function(){
			$('#forma-graldeptosturnos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-graldeptosturnos-window').find('#submit').mouseout(function(){
			$('#forma-graldeptosturnos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-graldeptosturnos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-graldeptosturnos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-graldeptosturnos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-graldeptosturnos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-graldeptosturnos-window').find('#close').mouseover(function(){
			$('#forma-graldeptosturnos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-graldeptosturnos-window').find('#close').mouseout(function(){
			$('#forma-graldeptosturnos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-graldeptosturnos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-graldeptosturnos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-graldeptosturnos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-graldeptosturnos-window').find("ul.pestanas li").click(function() {
			$('#forma-graldeptosturnos-window').find(".contenidoPes").hide();
			$('#forma-graldeptosturnos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-graldeptosturnos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	//me quede aqui antes de pasarme a sublime
	//nuevo 
	$new_depto.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_GralDeptosTurnos();
		
		var form_to_show = 'formaDeptosTurnos';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-graldeptosturnos-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-graldeptosturnos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-graldeptosturnos-window').find('input[name=identificador]');
		var $turno = $('#forma-graldeptosturnos-window').find('input[name=turno]');
		var $hora_inicial = $('#forma-graldeptosturnos-window').find('input[name=hora_inicial]');
                var $hora_final = $('#forma-graldeptosturnos-window').find('input[name=hora_final]');
		var $select_depto = $('#forma-graldeptosturnos-window').find('select[name=select_depto]');
		var $cerrar_plugin = $('#forma-graldeptosturnos-window').find('#close');
		var $cancelar_plugin = $('#forma-graldeptosturnos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-graldeptosturnos-window').find('#submit');
		


               $turno.keypress(function(e){
                                //alert(e.which);
                                // Permitir  numeros,    borrar,       uprimir,         TAB,              puntos,      comas
                                if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                        return true;
                                }else {
                                        return false;
                                }
                });
                
                $hora_inicial.keypress(function(e){
                        // Permitir  numeros,    borrar,       uprimir,         TAB,              puntos,      comas
                        if (e.which == 58 || e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }
                });

                $hora_final.keypress(function(e){
                                        // Permitir  numeros,    borrar,       uprimir,         TAB,              puntos,      comas
                                        if (e.which == 58 || e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                                return true;
                                        }else {
                                                return false;
                                        }
                });

                $hora_inicial.blur(function(e){
                            patron = /^(0[1-9]|1\d|2[0-3]):([0-5]\d):([0-5]\d)$/;
                            if(!patron.test($(this).val())){
                                jAlert("Hora no valida. El formato es HH:MM:SS", 'Atencion!');
                            }               

                });



                $hora_final.blur(function(e){
                    patron = /^(0[1-9]|1\d|2[0-3]):([0-5]\d):([0-5]\d)$/;
                    if(!patron.test($(this).val())){
                        jAlert("Hora no valida. El formato es HH:MM:SS", 'Atencion!');
                    }               

                });
                
                
                
                
                
		$campo_id.attr({'value' : 0});
                $turno.attr({'value' : 0});
                
                $aplicar_evento_focus_input($turno);
                $aplicar_evento_blur_input ($turno);
                
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Turno dado de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-graldeptosturnos-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-graldeptosturnos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-graldeptosturnos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTurnos.json'; 
		
                $arreglo = {'id':id_to_show};
		
		$.post(input_json,$arreglo,function(entry){
			//Alimentando los campos select de deptos
			$select_depto.children().remove();
			var depto_hmtl = '<option value="0" selected="yes">[--Seleccionar Depto--]</option>';
			$.each(entry['Deptos'],function(entryIndex,depto){
				depto_hmtl += '<option value="' + depto['id'] + '"  >' + depto['depto'] + '</option>';
			});
			$select_depto.append(depto_hmtl);
		});//termina llamada json


		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-graldeptosturnos-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-graldeptosturnos-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
	});
	
	
	//var carga_formaCateg_for_datagrid = function(id_to_show, accion_mode){
		var formaDeptos_grid = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Turno seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Turno fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("el Turno no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaDeptosTurnos';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_GralDeptosTurnos();
			$('#forma-graldeptosturnos-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-graldeptosturnos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-graldeptosturnos-window').find('input[name=identificador]');
                        var $turno = $('#forma-graldeptosturnos-window').find('input[name=turno]');
			var $hora_inicial = $('#forma-graldeptosturnos-window').find('input[name=hora_inicial]');
                        var $hora_final = $('#forma-graldeptosturnos-window').find('input[name=hora_final]');
			var $select_depto = $('#forma-graldeptosturnos-window').find('select[name=select_depto]');
			var $cerrar_plugin = $('#forma-graldeptosturnos-window').find('#close');
			var $cancelar_plugin = $('#forma-graldeptosturnos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-graldeptosturnos-window').find('#submit');
                         
                         $turno.keypress(function(e){
                                //alert(e.which);
                                // Permitir  numeros,    borrar,       uprimir,         TAB,              puntos,      comas
                                if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                        return true;
                                }else {
                                        return false;
                                }
                         });
                         
                         $aplicar_evento_focus_input($turno);
                         $aplicar_evento_blur_input ($turno);
		
			if(accion_mode == 'edit'){ //para editar un row
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTurnos.json';
				$arreglo = {'id':id_to_show};
				//$arreglo = {'iu':_to_show};
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-graldeptosturnos-overlay').fadeOut(remove);
						jAlert("El Turno se ha actualizado", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-graldeptosturnos-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-graldeptosturnos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$turno.attr({'value' : entry['Turno']['0']['turno']});
					$campo_id.attr({ 'value' : entry['Turno']['0']['id'] });
					$hora_inicial.attr({ 'value' : entry['Turno']['0']['hora_inicial'] });
                                        $hora_final.attr({'value' : entry['Turno']['0']['hora_final']});
//                                      
					
					
					$select_depto.children().remove();
					var depto_hmtl = '<option value="0">[--Seleccionar--]</option>';
					$.each(entry['Deptos'],function(entryIndex,depto){
						if(depto['id'] == entry['Turno']['0']['depto']){
							depto_hmtl += '<option value="' + depto['id'] + '" selected="yes">' + depto['depto'] + '</option>';
						}else{
							depto_hmtl += '<option value="' + depto['id'] + '"  >' + depto['depto'] + '</option>';
						}
					});
					$select_depto.append(depto_hmtl);					
				},"json");//termina llamada json
                                
                                
                                
                                                 
                                
                                
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-graldeptosturnos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-graldeptosturnos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}//termina edicion de row
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllTurnos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllTurnos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),formaDeptos_grid);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
});



