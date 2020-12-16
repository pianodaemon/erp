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
	var controller = $contextpath.val()+"/controllers/invunidades";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_unidades = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
        
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Unidades');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_unidad = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_unidad]');
        var $select_decimalesBuscador = $('#barra_buscador').find('.tabla_buscador').find('select[name=select_decimalesBuscador]');
        
        
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');	
	
        
	var to_make_one_search_string = function(){
            
        
         var variable = $select_decimalesBuscador.val();
         
         if (variable == 0){
             variable = 0;
         }else{
             variable = $select_decimalesBuscador.val();
         }
          
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "unidad" + signo_separador + $busqueda_unidad.val() + "|";
                valor_retorno += "decimales" + signo_separador + variable + "|";
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		
               //alert(valor_retorno)
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
                $busqueda_unidad.val('');
                
              
                $select_decimalesBuscador.children().remove();
                var option="";
                option+="<option value='0' selected='yes'> </option> ";
                option+="<option value='0'>0</option> ";
                option+="<option value='1'>1</option> ";
                option+="<option value='2'>2</option> ";
                option+="<option value='3'>3</option> ";
                option+="<option value='4'>4</option> ";
                $select_decimalesBuscador.append(option);
                
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
	
	
	/*
	//obtiene los regiones para el buscador
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegionesBuscador.json';
	$arreglo = {};
	$.post(input_json,$arreglo,function(entry){
		//Alimentando los campos select de regiones del buscador
		$busqueda_select_region.children().remove();
		var region_hmtl = '<option value="0" selected="yes">[--Seleccionar Region --]</option>';
		$.each(entry['Regiones'],function(entryIndex,reg){
			region_hmtl += '<option value="' + reg['id'] + '"  >' + reg['titulo'] + '</option>';
		});
		$busqueda_select_region.append(region_hmtl);
	});//termina llamada json
	*/
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-unidades-window').find('#submit').mouseover(function(){
			$('#forma-unidades-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-unidades-window').find('#submit').mouseout(function(){
			$('#forma-unidades-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-unidades-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-unidades-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-unidades-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-unidades-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-unidades-window').find('#close').mouseover(function(){
			$('#forma-unidades-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-unidades-window').find('#close').mouseout(function(){
			$('#forma-unidades-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-unidades-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-unidades-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-unidades-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-unidades-window').find("ul.pestanas li").click(function() {
			$('#forma-unidades-window').find(".contenidoPes").hide();
			$('#forma-unidades-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-unidades-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	
	
	//nuevas unidades
	$new_unidades.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_unidades();   //llamada al plug in 
		
		var form_to_show = 'formaUnidades';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-unidades-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-unidades-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-unidades-window').find('input[name=identificador]');                
		var $descripcion = $('#forma-unidades-window').find('input[name=descripcion]');
                var $unidad = $('#forma-unidades-window').find('input[name=unidad]');
		var $select_decimales = $('#forma-unidades-window').find('select[name=select_decimales]');
			
		//botones		
		var $cerrar_plugin = $('#forma-unidades-window').find('#close');
		var $cancelar_plugin = $('#forma-unidades-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-unidades-window').find('#submit');
		
		$campo_id.attr({'value' : 0});
       
       
      
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Unidad fue dada de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-unidades-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-unidades-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-unidades-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getUnidades.json';
		$arreglo = {'id':id_to_show,
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		$.post(input_json,$arreglo,function(entry){
                   
			/*
			$select_estatus.children().remove();
			var usuario_hmtl = '<option value="0" selected="yes">[--Seleccionar Usuario--]</option>';
			$.each(entry['Usuarios'],function(entryIndex,user){
				usuario_hmtl += '<option value="' + user['id'] + '"  >' + user['username'] + '</option>';
			});
			$select_estatus.append(usuario_hmtl);
			
			*/
			
		},"json");//termina llamada json
		

		//validar campo comision, solo acepte numeros y punto
//		$comision.keypress(function(e){
//			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
//			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
//				return true;
//			}else {
//				return false;
//			}		
//		});


		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-unidades-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-unidades-overlay').fadeOut(remove);
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
			jConfirm('Realmente desea eliminar la Unidad seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						
						jAlert(entry['success'], 'Atencion!');
						$get_datos_grid();
						/*
						if ( entry['success'] == '1' ){
							jAlert("La Unidad fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}else{
							jAlert("La Unidad no pudo ser eliminada", 'Atencion!');
						}
						*/
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaUnidades';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_unidades();
               
			$('#forma-unidades-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-unidades-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-unidades-window').find('input[name=identificador]');
                        var $descripcion = $('#forma-unidades-window').find('input[name=descripcion]');
                        var $unidad = $('#forma-unidades-window').find('input[name=unidad]');
                        var $select_decimales = $('#forma-unidades-window').find('select[name=select_decimales]'); 
                
			var $cerrar_plugin = $('#forma-unidades-window').find('#close');
			var $cancelar_plugin = $('#forma-unidades-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-unidades-window').find('#submit');
			
			if(accion_mode == 'edit'){
                            
                            //aqui es el post que envia los datos a getZonas.json  cuando se va a editar
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getUnidades.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-unidades-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-unidades-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-unidades-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
//					$campo_id.attr({ 'value' : entry['Marca']['0']['id'] });
//					$nombre.attr({ 'value' : entry['Marca']['0']['nombre'] });
                                        $unidad.attr({'value' : entry['Unidades']['0']['unidad']});
					$descripcion.attr({'value' : entry['Unidades']['0']['descripcion']});			
                                        $campo_id.attr({'value' : entry['Unidades']['0']['id']});
                                         
                                       
                                       
                                       //decimales
					
                                        
                                        
                                        /*$.each(entry['sdfsfs'],function(entryIndex,moneda){
                                                    if(moneda['id'] == entry['Unidades']['0']['id_moneda']){
                                                            moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
                                                    }else{
                                                            moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
                                                    }
                                             });
                                            $select_moneda.append(moneda_hmtl);
                                      */
                                     
                                       //carga select decimales 
                                       
                                       var arreglo  = [0,1,2,3,4];
                                       
                                       /*
                                       $select_decimales.children().remove();
                                       
                                            var decimal_hmtl = '';
                                            $.each(entry['Unidades'],function(entryIndex,decimal){
                                              if(arreglo == entry['decimal']['id']){
                                                    decimal_hmtl += '<option value="' + decimal['id'] + '"  >' + decimal['decimales'] + '</option>';
                                              }else{
                                                            decimal_hmtl += '<option value="' + decimal['id'] + '"  >' + decimal['decimales'] + '</option>';
                                                    }
                                            });
                                            $select_decimales.append(decimal_hmtl);
                                      
                                      
                                     */ 
                                    
                                    $select_decimales.children().remove();
                                      var decimal_hmtl = '';
                                      for(i=0; i<=4; i++){
                                          if(parseInt(entry['Unidades']['0']['decimales'])==parseInt(i) ){
                                              decimal_hmtl += '<option value="' + i + '" selected="yes" >' + i + '</option>';
                                          }else{
                                              decimal_hmtl += '<option value="' + i + '"  >' + i + '</option>';
                                          }                                          
                                      }
                                    
                                    $select_decimales.append(decimal_hmtl);
                                    
                                              /*                                
                                           var option="";
                                            option+="<option value='0'>0 </option> ";
                                            option+="<option value='1'>1</option> ";
                                            option+="<option value='2'>2</option> ";
                                            option+="<option value='3'>3</option> ";
                                            option+="<option value='4'>4</option> ";
                                            $select_decimales.append(option);

                                       */
                                        
                                        
                                        
                                        
					
				},"json");//termina llamada json
				
				
				//validar campo comision, solo acepte numeros y punto
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-unidades-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-unidades-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllUnidades.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllUnidades.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



