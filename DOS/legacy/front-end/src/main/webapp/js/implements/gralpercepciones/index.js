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
	var controller = $contextpath.val()+"/controllers/gralpercepciones";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_gralpercepciones = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Percepciones');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_percepciones = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_percepciones]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limbuscarpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
                valor_retorno += "percepcion" + signo_separador + $busqueda_percepciones.val() + "|";
                //valor_retorno += "check_activo" + signo_separador + $busqueda_titulo.val()+ "|";
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
	
	
	
	$limbuscarpiar.click(function(event){
		event.preventDefault();
		$busqueda_percepciones.val(' ');
		$busqueda_percepciones.focus();
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
		$busqueda_percepciones.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_percepciones, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-gralpercepciones-window').find('#submit').mouseover(function(){
			$('#forma-gralpercepciones-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-gralpercepciones-window').find('#submit').mouseout(function(){
			$('#forma-gralpercepciones-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-gralpercepciones-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-gralpercepciones-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-gralpercepciones-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-gralpercepciones-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-gralpercepciones-window').find('#close').mouseover(function(){
			$('#forma-gralpercepciones-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-gralpercepciones-window').find('#close').mouseout(function(){
			$('#forma-gralpercepciones-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-gralpercepciones-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-gralpercepciones-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-gralpercepciones-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-gralpercepciones-window').find("ul.pestanas li").click(function() {
			$('#forma-gralpercepciones-window').find(".contenidoPes").hide();
			$('#forma-gralpercepciones-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-gralpercepciones-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
		//funcion para hacer que un campo solo acepte numeros
	$permitir_solo_numeros = function($campo){
		//validar campo costo, solo acepte numeros y punto
		$campo.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	
	    //nuevas percepciones
	    $new_gralpercepciones.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_GralPercepciones();   //llamada al plug in 
		
		var form_to_show = 'formaGralPercepciones';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-gralpercepciones-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-gralpercepciones-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
                
		//campos de la vista
		var $campo_id = $('#forma-gralpercepciones-window').find('input[name=identificador]'); 
                //alert($campo_id);
                
       
				var $clave = $('#forma-gralpercepciones-window').find('input[name=clave]');
                var $percepcion = $('#forma-gralpercepciones-window').find('input[name=titulo]');
                var $check_activo = $('#forma-gralpercepciones-window').find('input[name=check_activo]');
                var $select_percepcion = $('#forma-gralpercepciones-window').find('select[name=select_percepcion]');
              
				$clave.css({'background' : '#DDDDDD'});
				$clave.attr('disabled','-1');
              
                			
		//botones		
		var $cerrar_plugin = $('#forma-gralpercepciones-window').find('#close');
		var $cancelar_plugin = $('#forma-gralpercepciones-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-gralpercepciones-window').find('#submit');
		
		
		//permitir solo numeros al campo tasa
         $permitir_solo_numeros($select_percepcion);
		
		$campo_id.attr({'value' : 0});
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Percepcion fue dada de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-gralpercepciones-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-gralpercepciones-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
                                            $('#forma-gralpercepciones-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                            .parent()
                                            .css({'display':'block'})
                                            .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPercepciones.json';
		var parametros={
                    id:$campo_id.val(),
                    iu: $('#lienzo_recalculable').find('input[name=iu]').val()
                }
                //alert($('#lienzo_recalculable').find('input[name=iu]').val())
                $.post(input_json,parametros,function(entry){
					
					
					
		 //alimenta el select de escolaridad
			$select_percepcion.children().remove();

			var tipopercepcion_hmtl = '<option value="0"  selected="yes">[-Seleccionar Percepcion-]</option>'
			$.each(entry['TiposPercepciones'],function(entryIndex,tipopercepcion){
				tipopercepcion_hmtl += '<option value="' + tipopercepcion['id'] + '"  >' + tipopercepcion['titulo'] + '</option>';
			});
			$select_percepcion.append(tipopercepcion_hmtl);



                        
                });//termina llamada json
		
                
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-gralpercepciones-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-gralpercepciones-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		//campo vacio
		$('#forma-gralpercepciones-window').find('input[name=tasa]').val(0);
	});
        
        //Eventos del grid edicion,borrar!
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Percepcion', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
                                            
						if ( entry['success'] == '1' ){
							jAlert("La Percepcion fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Percepcion no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
				
                        
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaGralPercepciones';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_GralPercepciones();
						
			$('#forma-gralpercepciones-window').css({"margin-left": -300, 	"margin-top": -200});
                        $forma_selected.prependTo('#forma-gralpercepciones-window');
                        $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
                        $tabs_li_funxionalidad();

                        //campos de la vista
                        var $campo_id = $('#forma-gralpercepciones-window').find('input[name=identificador]'); 
                        //alert($campo_id);
                        var $clave = $('#forma-gralpercepciones-window').find('input[name=clave]');
                        var $percepcion = $('#forma-gralpercepciones-window').find('input[name=titulo]');
						var $check_activo = $('#forma-gralpercepciones-window').find('input[name=check_activo]');
						var $select_percepcion = $('#forma-gralpercepciones-window').find('select[name=select_percepcion]');
                        
                        
                        $clave.attr('disabled','-1'); //deshabilitar
						$clave.css({'background' : '#DDDDDD'});
				
                        //botones                        
                        var $cerrar_plugin = $('#forma-gralpercepciones-window').find('#close');
                        var $cancelar_plugin = $('#forma-gralpercepciones-window').find('#boton_cancelar');
                        var $submit_actualizar = $('#forma-gralpercepciones-window').find('#submit');
                        
                        //permitir solo numeros al campo tasa
                        $permitir_solo_numeros($select_percepcion);
			
			if(accion_mode == 'edit'){
                            
                            //aqui es el post que envia los datos a getPercepciones.json
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPercepciones.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-gralpercepciones-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
                                                $get_datos_grid();
						
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-gralpercepciones-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-gralpercepciones-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
				// aqui van los campos de editar
                                $campo_id.attr({'value' : entry['Percepciones']['0']['id']});
                                $clave.attr({'value' : entry['Percepciones']['0']['clave']});
                                $percepcion.attr({'value' : entry['Percepciones']['0']['percepcion']});
                                $check_activo.attr('checked', (entry['Percepciones'][0]['estado'] == 'true')? true:false );
                               
                          //alimenta el select de escolaridad
						$select_percepcion.children().remove();

						var tipopercepcion_hmtl ="";
						// '<option value="0"  selected="yes">[-Seleccionar Escolaridad-]</option>'

						$.each(entry['TiposPercepciones'],function(entryIndex,percepciones){
								if(percepciones['id']==  entry['Percepciones']['0']['tipo_percep']){
									tipopercepcion_hmtl += '<option value="' + percepciones['id'] + '"selected="yes" >' + percepciones['titulo'] + '</option>';
								}else{
									tipopercepcion_hmtl += '<option value="' + percepciones['id'] + '"  >' + percepciones['titulo'] + '</option>';

								}
						});
						$select_percepcion.append(tipopercepcion_hmtl);
						
						
						//alimenta el select de escolaridad
						/*$select_escolaridad.children().remove();

						var escolaridad_hmtl ="";
						// '<option value="0"  selected="yes">[-Seleccionar Escolaridad-]</option>'

						$.each(entry['Escolaridad'],function(entryIndex,escolaridad){
								if(escolaridad['id']== entry['Empleados']['0']['gral_escolaridad_id']){
									escolaridad_hmtl += '<option value="' + escolaridad['id'] + '"selected="yes" >' + escolaridad['titulo'] + '</option>';
								}else{
									escolaridad_hmtl += '<option value="' + escolaridad['id'] + '"  >' + escolaridad['titulo'] + '</option>';

								}
						});
						$select_escolaridad.append(escolaridad_hmtl);*/
                                
                                
                                
				 },"json");//termina llamada json
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralpercepciones-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralpercepciones-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				//campo vacio
			   // $('#forma-gralpercepciones-window').find('input[name=tasa]').val(0);
			}
		}
	}
    
    
    
  
                        
   $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllPercepciones.json';
		
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllPercepciones.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        //$.post(input_json,$arreglo,functmodalPanel_pocpedidosion(data){
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    

    $get_datos_grid();
    
    
    
});
