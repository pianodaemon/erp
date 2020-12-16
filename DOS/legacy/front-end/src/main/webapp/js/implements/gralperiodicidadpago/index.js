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
	var controller = $contextpath.val()+"/controllers/gralperiodicidadpago";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_gralperiodicidadpago = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo Periodicidad de Pago');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_periodicidadpago = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda__periodicidadpago]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limbuscarpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
                valor_retorno += "periodo_pago" + signo_separador + $busqueda_periodicidadpago.val() + "|";
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
		$busqueda_periodicidadpago.val(' ');
		$busqueda_periodicidadpago.focus();
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
		$busqueda_periodicidadpago.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_periodicidadpago, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-gralperiodicidadpago-window').find('#submit').mouseover(function(){
			$('#forma-gralperiodicidadpago-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-gralperiodicidadpago-window').find('#submit').mouseout(function(){
			$('#forma-gralperiodicidadpago-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-gralperiodicidadpago-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-gralperiodicidadpago-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-gralperiodicidadpago-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-gralperiodicidadpago-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-gralperiodicidadpago-window').find('#close').mouseover(function(){
			$('#forma-gralperiodicidadpago-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-gralperiodicidadpago-window').find('#close').mouseout(function(){
			$('#forma-gralperiodicidadpago-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-gralperiodicidadpago-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-gralperiodicidadpago-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-gralperiodicidadpago-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-gralperiodicidadpago-window').find("ul.pestanas li").click(function() {
			$('#forma-gralperiodicidadpago-window').find(".contenidoPes").hide();
			$('#forma-gralperiodicidadpago-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-gralperiodicidadpago-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
	    $new_gralperiodicidadpago.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_GralPeriodicidadPago();   //llamada al plug in 
		
		var form_to_show = 'formagralPeriodicidadPago';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-gralperiodicidadpago-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-gralperiodicidadpago-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
                
		//campos de la vista
		var $campo_id = $('#forma-gralperiodicidadpago-window').find('input[name=identificador]'); 
                //alert($campo_id);

                var $periodo_titulo = $('#forma-gralperiodicidadpago-window').find('input[name=titulo]');
                var $num_periodos = $('#forma-gralperiodicidadpago-window').find('input[name=num_periodos]');
                var $check_activo = $('#forma-gralperiodicidadpago-window').find('input[name=check_activo]');
                
                //permitir solo numeros al campo tasa
				$permitir_solo_numeros($num_periodos);
              
				//$num_periodos.css({'background' : '#DDDDDD'});
				//$num_periodos.attr('disabled','-1');
              
                			
		//botones		
		var $cerrar_plugin = $('#forma-gralperiodicidadpago-window').find('#close');
		var $cancelar_plugin = $('#forma-gralperiodicidadpago-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-gralperiodicidadpago-window').find('#submit');

		$campo_id.attr({'value' : 0});
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Periodicidad de Pago fue dada de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-gralperiodicidadpago-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-gralperiodicidadpago-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
                                            $('#forma-gralperiodicidadpago-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                            .parent()
                                            .css({'display':'block'})
                                            .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPeriodicidadpago.json';
		var parametros={
                    id:$campo_id.val(),
                    iu: $('#lienzo_recalculable').find('input[name=iu]').val()
                }
                //alert($('#lienzo_recalculable').find('input[name=iu]').val())
                $.post(input_json,parametros,function(entry){
					   //select
                });//termina llamada json
		
                
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-gralperiodicidadpago-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-gralperiodicidadpago-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		//campo num_periodos
		$('#forma-gralperiodicidadpago-window').find('input[name=num_periodos]').val(0);
	});
        
        //Eventos del grid edicion,borrar!
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Periodicidad de Pago', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
                                            
						if ( entry['success'] == '1' ){
							jAlert("La Periodicidad de Pago fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Periodicidad de Pago no puede ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
				
                        
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formagralPeriodicidadPago';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_GralPeriodicidadPago();
						
			$('#forma-gralperiodicidadpago-window').css({"margin-left": -300, 	"margin-top": -200});
                        $forma_selected.prependTo('#forma-gralperiodicidadpago-window');
                        $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
                        $tabs_li_funxionalidad();

                        //campos de la vista
                        var $campo_id = $('#forma-gralperiodicidadpago-window').find('input[name=identificador]'); 
                        //alert($campo_id);
                        var $periodo_titulo = $('#forma-gralperiodicidadpago-window').find('input[name=titulo]');
                        var $num_periodos = $('#forma-gralperiodicidadpago-window').find('input[name=num_periodos]');
						var $check_activo = $('#forma-gralperiodicidadpago-window').find('input[name=check_activo]');
					
                        
                        
                        //$num_periodos.attr('disabled','-1'); //deshabilitar
						//$num_periodos.css({'background' : '#DDDDDD'});
				
                        //botones                        
                        var $cerrar_plugin = $('#forma-gralperiodicidadpago-window').find('#close');
                        var $cancelar_plugin = $('#forma-gralperiodicidadpago-window').find('#boton_cancelar');
                        var $submit_actualizar = $('#forma-gralperiodicidadpago-window').find('#submit');
                        
                        //permitir solo numeros al campo tasa
                        $permitir_solo_numeros($num_periodos);
			
			if(accion_mode == 'edit'){
                            
                            //aqui es el post que envia los datos a getPeriodicidadpago.json
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPeriodicidadpago.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-gralperiodicidadpago-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
                                                $get_datos_grid();
						
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-gralperiodicidadpago-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-gralperiodicidadpago-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
                                $campo_id.attr({'value' : entry['Periodicidadpago']['0']['id']});
                                $periodo_titulo.attr({'value' : entry['Periodicidadpago']['0']['periodo_pago']});
                                $num_periodos.attr({'value' : entry['Periodicidadpago']['0']['numero_periodo']});
                                $check_activo.attr('checked', (entry['Periodicidadpago'][0]['estado'] == 'true')? true:false );
				 },"json");//termina llamada json
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralperiodicidadpago-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralperiodicidadpago-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				//campo vacio
			   // $('#forma-gralperiodicidadpago-window').find('input[name=tasa]').val(0);
			}
		}
	}
    
    
    
  
                        
   $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllPeriodicidadpago.json';
		
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllPeriodicidadpago.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
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
