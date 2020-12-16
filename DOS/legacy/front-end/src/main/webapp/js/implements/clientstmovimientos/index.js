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
	var controller = $contextpath.val()+"/controllers/clientstmovimientos";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_clientstmovimientos = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Tipos de movimientos');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_titulo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_titulo]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "titulo" + signo_separador + $busqueda_titulo.val() + "|";
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
		//$busqueda_descripcion.val('');
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
	
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-clientstmovimientos-window').find('#submit').mouseover(function(){
			$('#forma-clientstmovimientos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-clientstmovimientos-window').find('#submit').mouseout(function(){
			$('#forma-clientstmovimientos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-clientstmovimientos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-clientstmovimientos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-clientstmovimientos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-clientstmovimientos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-clientstmovimientos-window').find('#close').mouseover(function(){
			$('#forma-clientstmovimientos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-clientstmovimientos-window').find('#close').mouseout(function(){
			$('#forma-clientstmovimientos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-clientstmovimientos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-clientstmovimientos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-clientstmovimientos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-clientstmovimientos-window').find("ul.pestanas li").click(function() {
			$('#forma-clientstmovimientos-window').find(".contenidoPes").hide();
			$('#forma-clientstmovimientos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-clientstmovimientos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	
	
	//Registrar nuevooooo
	$new_clientstmovimientos.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_clientstmovimientos();
		
		var form_to_show = 'formaClientstmovimientos';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-clientstmovimientos-window').css({ "margin-left": -250, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-clientstmovimientos-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-clientstmovimientos-window').find('input[name=identificador]');
		var $campo_titulo = $('#forma-clientstmovimientos-window').find('input[name=titulo]');
                var $campo_descripcion = $('#forma-clientstmovimientos-window').find('input[name=descripcion]');
                var $select_moneda =     $('#forma-clientstmovimientos-window').find('select[name=id_moneda]');
                
                //var $titulo_moneda = $('#forma-clientstmovimientos-window').find('input[name=titulo_moneda]');
		
                
                
		var $cerrar_plugin = $('#forma-clientstmovimientos-window').find('#close');
		var $cancelar_plugin = $('#forma-clientstmovimientos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-clientstmovimientos-window').find('#submit');
		
		$campo_id.attr({ 'value' : 0 });
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Tipo de movimiento fue dado de alta con &eacute;xito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-clientstmovimientos-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-clientstmovimientos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-clientstmovimientos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getClientstMovimientos.json';
		$arreglo = {'id':id_to_show};
		
		$.post(input_json,$arreglo,function(entry){
			//alert("Aqui no hay nada");
                        $select_moneda.children().remove();
                            var moneda_hmtl = '';
                            $.each(entry['monedas'],function(entryIndex,moneda){
                                moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
                            });
                        
                        $select_moneda.append(moneda_hmtl);
                          

		},"json");//termina llamada json
		

		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-clientstmovimientos-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-clientstmovimientos-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
        });
	
	
        
        
        
	
	var carga_formaClientstmovimientos_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el tipo de movimiento', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Tipo de movimiento fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Tipo de movimiento  no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaClientstmovimientos';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_clientstmovimientos();
			$('#forma-clientstmovimientos-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-clientstmovimientos-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
                        var $campo_id = $('#forma-clientstmovimientos-window').find('input[name=identificador]');
                        var $campo_titulo = $('#forma-clientstmovimientos-window').find('input[name=titulo]');
                        var $campo_descripcion = $('#forma-clientstmovimientos-window').find('input[name=descripcion]');
                        var $select_moneda = $('#forma-clientstmovimientos-window').find('select[name=id_moneda]');
                        
                        var moneda_hmtl = '';
                        
                        
                        //var $titulo_meneda = $('#forma-clientstmovimientos-window').find('input[name=titulo_moneda]');
                        
			
			var $cerrar_plugin = $('#forma-clientstmovimientos-window').find('#close');
			var $cancelar_plugin = $('#forma-clientstmovimientos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-clientstmovimientos-window').find('#submit');
			
			
		
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getClientstMovimientos.json';
				$arreglo = {'id':id_to_show};
                                
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-clientstmovimientos-overlay').fadeOut(remove);
						jAlert("Los Tipos de movimientos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-clientstmovimientos-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 2 ){
								$('#forma-clientstmovimientos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
                                    
                                     //carga select denominacion con todas las monedas
                                            $select_moneda.children().remove();
                                            //var moneda_hmtl = '<option value="0">[--   --]</option>';
                                            var moneda_hmtl = '';
                                            
                                            $.each(entry['monedas'],function(entryIndex,moneda){
                                                    if(moneda['id'] == entry['tMovimientos']['0']['id_moneda']){
                                                            moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
                                                    }else{
                                                            moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
                                                    }
                                             });
                                        
                                        $select_moneda.append(moneda_hmtl);
                                         
                                        $campo_id.attr({ 'value' : entry['tMovimientos']['0']['id'] });
					$campo_titulo.attr({ 'value' : entry['tMovimientos']['0']['titulo'] });
                                        $campo_descripcion.attr({ 'value' : entry['tMovimientos']['0']['descripcion'] });
                                        
                                       
				},"json");//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-clientstmovimientos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-clientstmovimientos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllgetClientstMovimientos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllgetClientstMovimientos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaClientstmovimientos_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



