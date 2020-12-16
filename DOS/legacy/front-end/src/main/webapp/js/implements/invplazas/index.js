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
	var controller = $contextpath.val()+"/controllers/invplazas";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_plazas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Inventario Plazas ');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_plaza = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_plaza]');
        var $select_estatusBuscador = $('#barra_buscador').find('.tabla_buscador').find('select[name=select_estatusBuscador]');
        
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');	
	
	
	var to_make_one_search_string = function(){
            
        
         var variable = $select_estatusBuscador.val();
              if(variable == 1){
                  variable='sinestatus';
              }
         
           
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "plaza" + signo_separador + $busqueda_plaza.val() + "|";
                valor_retorno += "estatus" + signo_separador + variable + "|";
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
                $busqueda_plaza.val('');
                
              
                $select_estatusBuscador.children().remove();
                var option="";
                option+="<option value='0' selected='yes'> </option> ";
                option+="<option value='true'>Activo</option> ";
                option+="<option value='false' >Inactivo</option> ";
                $select_estatusBuscador.append(option);
              
                
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
		
		$('#forma-plazas-window').find('#submit').mouseover(function(){
			$('#forma-plazas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-plazas-window').find('#submit').mouseout(function(){
			$('#forma-plazas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-plazas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-plazas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-plazas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-plazas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-plazas-window').find('#close').mouseover(function(){
			$('#forma-plazas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-plazas-window').find('#close').mouseout(function(){
			$('#forma-plazas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-plazas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-plazas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-plazas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-plazas-window').find("ul.pestanas li").click(function() {
			$('#forma-plazas-window').find(".contenidoPes").hide();
			$('#forma-plazas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-plazas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	
	
	//nuevas plazas
	$new_plazas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_plazas();   //llamada al plug in 
		
		var form_to_show = 'formaPlazas';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-plazas-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-plazas-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-plazas-window').find('input[name=identificador]');                
		var $nombre = $('#forma-plazas-window').find('input[name=descripcion]');
                var $plaza = $('#forma-plazas-window').find('input[name=zona]');
		var $select_zonas = $('#forma-plazas-window').find('select[name=id_zona]');
                var $select_estatus = $('#forma-plazas-window').find('select[name=id_estatus]');
		
                //botones		
		var $cerrar_plugin = $('#forma-plazas-window').find('#close');
		var $cancelar_plugin = $('#forma-plazas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-plazas-window').find('#submit');
		
		$campo_id.attr({'value' : 0});
       
       
      
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La plaza fue dada de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-plazas-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-plazas-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-plazas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPlazas.json';
		$arreglo = {'id':id_to_show,
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		$.post(input_json,$arreglo,function(entry){
                   //aqui solo se pone algo cuando  realizas otros json para cargar datos que no estan en una sola consulta como monedas, estatus  etc
			$select_zonas.children().remove();
                            var zona_hmtl = '';
                            $.each(entry['Zonas'],function(entryIndex,zona){
                                zona_hmtl += '<option value="' + zona['id'] + '"  >' + zona['descripcion'] + '</option>';
                            });
                        
                        $select_zonas.append(zona_hmtl);
			$select_zonas.val();
                       
                       
		},"json");//termina llamada json
		

		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-plazas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-plazas-overlay').fadeOut(remove);
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
			jConfirm('Realmente desea eliminar la Plaza seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Plaza fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Plaza no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaPlazas';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_plazas();
               
			$('#forma-plazas-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-plazas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-plazas-window').find('input[name=identificador]');
                        var $nombre = $('#forma-plazas-window').find('input[name=nombre]');
                        var $plaza = $('#forma-plazas-window').find('input[name=plaza]');
                        var $select_estatus = $('#forma-plazas-window').find('select[name=id_estatus]'); 
                        var $select_zonas = $('#forma-plazas-window').find('select[name=id_zona]'); 
                
			var $cerrar_plugin = $('#forma-plazas-window').find('#close');
			var $cancelar_plugin = $('#forma-plazas-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-plazas-window').find('#submit');
			
			if(accion_mode == 'edit'){
                            
                            //aqui es el post que envia los datos a getPlazas.json  cuando se va a editar
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPlazas.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-plazas-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-plazas-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-plazas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
                                    
 //{"Plazas":[{"nombre":"PLAZA DE HERMOSILLO","id":"1","zona":"PLAZA1","estatus":"ACTIVO","plaza":"PLAZA1"
//					$campo_id.attr({ 'value' : entry['Marca']['0']['id'] });
//					$nombre.attr({ 'value' : entry['Marca']['0']['nombre'] });
                                        $campo_id.attr({'value' : entry['Plazas']['0']['id']});
                                        $nombre.attr({'value' : entry['Plazas']['0']['nombre']});
                                        
                                        $plaza.attr({'value' : entry['Plazas']['0']['plaza']});
					
                                        //estatus
					var sel_activo='';
					var sel_inactivo='';
					if(entry['Plazas']['0']['estatus']=='true'){
						sel_activo='selected="yes"';
					}else{
						sel_inactivo='selected="yes"';
					}
                                        
                                        $select_estatus.children().remove();
                                        var status_html = '<option value="false" '+sel_activo+'>Inactivo</option>';
                                        status_html += '<option value="true" '+sel_inactivo+'>Activo</option>';
                                        $select_estatus.append(status_html);
                                        
                                        //aqui falta poner  un select como el de monedasssss para llenar el select de zonas
                                        //carga select denominacion con todas las monedas
                                            $select_zonas.children().remove();
                                           var zona_hmtl = '';
                                            
                                            $.each(entry['Zonas'],function(entryIndex,zona){
                                                    if(zona['id'] == entry['Plazas']['0']['id']){
                                                            zona_hmtl += '<option value="' + zona['id'] + '"  selected="yes">' + zona['descripcion'] + '</option>';
                                                    }else{
                                                            zona_hmtl += '<option value="' + zona['id'] + '"  >' + zona['descripcion'] + '</option>';
                                                    }
                                             });
                                            $select_zonas.append(zona_hmtl);
                                      
                                      //carga select con todas las zonas
                                      
					
				},"json");//termina llamada json
				
				
				//validar campo comision, solo acepte numeros y punto
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-plazas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-plazas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllPlazas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllPlazas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



