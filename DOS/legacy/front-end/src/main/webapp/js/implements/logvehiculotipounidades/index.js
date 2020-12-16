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
	var controller = $contextpath.val()+"/controllers/logvehiculotipounidades";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_logvehiculotipounidades = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Tipo de Unidades');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_titulo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_titulo]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limbuscarpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
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
	
	
	
	$limbuscarpiar.click(function(event){
		event.preventDefault();
                $busqueda_titulo.val('');
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
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_titulo, $buscar);
	
	
	
	
	//Funcion para hacer que un campo solo acepte numeros
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
	
	
	//Valida que la cantidad ingresada no tenga mas de un punto decimal
	$validar_numero_puntos = function($campo, campo_nombre){
		//Buscar cuantos puntos tiene  Precio Unitario
		var coincidencias = $campo.val().match(/\./g);
		var numPuntos = coincidencias ? coincidencias.length : 0;
		if(parseInt(numPuntos)>1){
			jAlert('El valor ingresado para el campo '+campo_nombre+' es incorrecto, tiene mas de un punto('+$campo.val()+').', 'Atencion!', function(r) { 
				$campo.focus();
			});
		}
	}
	
	
	$aplica_evento_focus_input_numerico = function($campo){
		//Al iniciar el campo tiene un caracter en blanco o tiene comas, al obtener el foco se elimina el  espacio por espacio en blanco
		$campo.focus(function(e){
			var valor=quitar_comas($(this).val().trim());
			
			if(valor.trim() != ''){
				if(parseFloat(valor)<=0){
					$(this).val('');
				}else{
					$(this).val(valor);
				}
			}
		});
	}
	
	
	$aplica_evento_blur_input_numerico = function($campo, etiqueta_campo){
		$campo.blur(function(e){
			$validar_numero_puntos($(this), etiqueta_campo);
			
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
	}
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-logvehiculotipounidades-window').find('#submit').mouseover(function(){
			$('#forma-logvehiculotipounidades-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-logvehiculotipounidades-window').find('#submit').mouseout(function(){
			$('#forma-logvehiculotipounidades-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-logvehiculotipounidades-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-logvehiculotipounidades-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-logvehiculotipounidades-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-logvehiculotipounidades-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-logvehiculotipounidades-window').find('#close').mouseover(function(){
			$('#forma-logvehiculotipounidades-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-logvehiculotipounidades-window').find('#close').mouseout(function(){
			$('#forma-logvehiculotipounidades-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-logvehiculotipounidades-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-logvehiculotipounidades-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-logvehiculotipounidades-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-logvehiculotipounidades-window').find("ul.pestanas li").click(function() {
			$('#forma-logvehiculotipounidades-window').find(".contenidoPes").hide();
			$('#forma-logvehiculotipounidades-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-logvehiculotipounidades-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	
	
	//nuevo Vehiculo Tipo Unidades
	$new_logvehiculotipounidades.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_logvehiculotipounidades();   //llamada al plug in 
		
		var form_to_show = 'formaLogvehiculotipounidades';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-logvehiculotipounidades-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-logvehiculotipounidades-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
                
		//campos de la vista
		var $campo_id = $('#forma-logvehiculotipounidades-window').find('input[name=identificador]'); 
		var $titulo = $('#forma-logvehiculotipounidades-window').find('input[name=titulo]');
		var $vol_ini = $('#forma-logvehiculotipounidades-window').find('input[name=vol_ini]');
		var $vol_fin = $('#forma-logvehiculotipounidades-window').find('input[name=vol_fin]');
		var $kg_ini = $('#forma-logvehiculotipounidades-window').find('input[name=kg_ini]');
		var $kg_fin = $('#forma-logvehiculotipounidades-window').find('input[name=kg_fin]');
		var $carton_ini = $('#forma-logvehiculotipounidades-window').find('input[name=carton_ini]');
		var $carton_fin = $('#forma-logvehiculotipounidades-window').find('input[name=carton_fin]');
		var $tarima_ini = $('#forma-logvehiculotipounidades-window').find('input[name=tarima_ini]');
		var $tarima_fin = $('#forma-logvehiculotipounidades-window').find('input[name=tarima_fin]');
		
		var $select_um_vol = $('#forma-logtarifariocostos-window').find('select[name=select_um_vol]');
		var $select_um_kg = $('#forma-logtarifariocostos-window').find('select[name=select_um_kg]');
		var $select_um_carton = $('#forma-logtarifariocostos-window').find('select[name=select_um_carton]');
		var $select_um_tarima = $('#forma-logtarifariocostos-window').find('select[name=select_um_tarima]');
		
		//botones		
		var $cerrar_plugin = $('#forma-logvehiculotipounidades-window').find('#close');
		var $cancelar_plugin = $('#forma-logvehiculotipounidades-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-logvehiculotipounidades-window').find('#submit');
		

		$campo_id.attr({'value' : 0});
		$vol_ini.val('0.00');
		$vol_fin.val('0.00');
		$kg_ini.val('0.00');
		$kg_fin.val('0.00');
		
		//Permitir solo numeros y punto
		$permitir_solo_numeros($vol_ini);
		$permitir_solo_numeros($vol_fin);
		$permitir_solo_numeros($kg_ini);
		$permitir_solo_numeros($kg_fin);
		$permitir_solo_numeros($carton_ini);
		$permitir_solo_numeros($carton_fin);
		$permitir_solo_numeros($tarima_ini);
		$permitir_solo_numeros($tarima_fin);
		
		//Aplicar envento focus
		$aplica_evento_focus_input_numerico($vol_ini);
		$aplica_evento_focus_input_numerico($vol_fin);
		$aplica_evento_focus_input_numerico($kg_ini);
		$aplica_evento_focus_input_numerico($kg_fin);
		$aplica_evento_focus_input_numerico($carton_ini);
		$aplica_evento_focus_input_numerico($carton_fin);
		$aplica_evento_focus_input_numerico($tarima_ini);
		$aplica_evento_focus_input_numerico($tarima_fin);
		
		$aplica_evento_blur_input_numerico($vol_ini, "Volumen&nbsp;m&#179;&nbsp;Inicial");
		$aplica_evento_blur_input_numerico($vol_fin, "Volumen&nbsp;m&#179;&nbsp;Final");
		$aplica_evento_blur_input_numerico($kg_ini, "Kg.&nbsp;Inicial");
		$aplica_evento_blur_input_numerico($kg_fin, "Kg.&nbsp;Final");
		$aplica_evento_blur_input_numerico($carton_ini, "Carton&nbsp;Inicial");
		$aplica_evento_blur_input_numerico($carton_fin, "Carton&nbsp;Final");
		$aplica_evento_blur_input_numerico($tarima_ini, "Tarima&nbsp;Inicial");
		$aplica_evento_blur_input_numerico($tarima_fin, "Tarima&nbsp;Inicial");
		/*
		$vol_ini.blur(function(){
			$validar_numero_puntos($(this), "Volumen&nbsp;m&#179;");
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
		
		$vol_fin.blur(function(){
			$validar_numero_puntos($(this), "Volumen&nbsp;m&#179;");
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
		
		$kg_ini.blur(function(){
			$validar_numero_puntos($(this), "Kilogramos.");
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
		
		$kg_fin.blur(function(){
			$validar_numero_puntos($(this), "Kilogramos.");
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
		*/
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El registro fue dado de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-logvehiculotipounidades-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-logvehiculotipounidades-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-logvehiculotipounidades-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getVehiculoTipoUnidades.json';
		var parametros={
                    id:0,
                    iu: $('#lienzo_recalculable').find('input[name=iu]').val()
                }
                //alert($('#lienzo_recalculable').find('input[name=iu]').val())
                $.post(input_json,parametros,function(entry){
                        
                });//termina llamada json
		
                
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-logvehiculotipounidades-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-logvehiculotipounidades-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$titulo.focus();
	});
        
        //Eventos del grid edicion,borrar!
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show, 'iu': $('#lienzo_recalculable').find('input[name=iu]').val() };
			jConfirm('Realmente desea eliminar el Tipo de Unidad seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Tipo de Unidad fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Tipo de Unidad no puede ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaLogvehiculotipounidades';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_logvehiculotipounidades();
						
			$('#forma-logvehiculotipounidades-window').css({"margin-left": -300, 	"margin-top": -200});
			$forma_selected.prependTo('#forma-logvehiculotipounidades-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			$tabs_li_funxionalidad();

			//campos de la vista
			var $campo_id = $('#forma-logvehiculotipounidades-window').find('input[name=identificador]'); 
			var $titulo = $('#forma-logvehiculotipounidades-window').find('input[name=titulo]');
			var $vol_ini = $('#forma-logvehiculotipounidades-window').find('input[name=vol_ini]');
			var $vol_fin = $('#forma-logvehiculotipounidades-window').find('input[name=vol_fin]');
			var $kg_ini = $('#forma-logvehiculotipounidades-window').find('input[name=kg_ini]');
			var $kg_fin = $('#forma-logvehiculotipounidades-window').find('input[name=kg_fin]');
			var $carton_ini = $('#forma-logvehiculotipounidades-window').find('input[name=carton_ini]');
			var $carton_fin = $('#forma-logvehiculotipounidades-window').find('input[name=carton_fin]');
			var $tarima_ini = $('#forma-logvehiculotipounidades-window').find('input[name=tarima_ini]');
			var $tarima_fin = $('#forma-logvehiculotipounidades-window').find('input[name=tarima_fin]');
			
			//botones                        
			var $cerrar_plugin = $('#forma-logvehiculotipounidades-window').find('#close');
			var $cancelar_plugin = $('#forma-logvehiculotipounidades-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-logvehiculotipounidades-window').find('#submit');
			
			
			//Permitir solo numeros y punto
			$permitir_solo_numeros($vol_ini);
			$permitir_solo_numeros($vol_fin);
			$permitir_solo_numeros($kg_ini);
			$permitir_solo_numeros($kg_fin);
			$permitir_solo_numeros($carton_ini);
			$permitir_solo_numeros($carton_fin);
			$permitir_solo_numeros($tarima_ini);
			$permitir_solo_numeros($tarima_fin);
			
			//Aplicar envento focus
			$aplica_evento_focus_input_numerico($vol_ini);
			$aplica_evento_focus_input_numerico($vol_fin);
			$aplica_evento_focus_input_numerico($kg_ini);
			$aplica_evento_focus_input_numerico($kg_fin);
			$aplica_evento_focus_input_numerico($carton_ini);
			$aplica_evento_focus_input_numerico($carton_fin);
			$aplica_evento_focus_input_numerico($tarima_ini);
			$aplica_evento_focus_input_numerico($tarima_fin);
			
			$aplica_evento_blur_input_numerico($vol_ini, "Volumen&nbsp;m&#179;&nbsp;Inicial");
			$aplica_evento_blur_input_numerico($vol_fin, "Volumen&nbsp;m&#179;&nbsp;Final");
			$aplica_evento_blur_input_numerico($kg_ini, "Kg.&nbsp;Inicial");
			$aplica_evento_blur_input_numerico($kg_fin, "Kg.&nbsp;Final");
			$aplica_evento_blur_input_numerico($carton_ini, "Carton&nbsp;Inicial");
			$aplica_evento_blur_input_numerico($carton_fin, "Carton&nbsp;Final");
			$aplica_evento_blur_input_numerico($tarima_ini, "Tarima&nbsp;Inicial");
			$aplica_evento_blur_input_numerico($tarima_fin, "Tarima&nbsp;Inicial");
			
			
			
			if(accion_mode == 'edit'){
                            
				//aqui es el post que envia los datos a getVehiculoTipoUnidades.json
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getVehiculoTipoUnidades.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-logvehiculotipounidades-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						//$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-logvehiculotipounidades-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-logvehiculotipounidades-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$campo_id.attr({'value' : entry['VehiculoTipoUnidades'][0]['id']});
					$titulo.attr({'value' : entry['VehiculoTipoUnidades'][0]['titulo']});
					$vol_ini.attr({'value' : entry['VehiculoTipoUnidades'][0]['volumen_inicio']});
					$vol_fin.attr({'value' : entry['VehiculoTipoUnidades'][0]['volumen_fin']});
					$kg_ini.attr({'value' : entry['VehiculoTipoUnidades'][0]['kg_inicio']});
					$kg_fin.attr({'value' : entry['VehiculoTipoUnidades'][0]['kg_fin']});
					$carton_ini.attr({'value' : entry['VehiculoTipoUnidades'][0]['carton_inicio']});
					$carton_fin.attr({'value' : entry['VehiculoTipoUnidades'][0]['carton_fin']});
					$tarima_ini.attr({'value' : entry['VehiculoTipoUnidades'][0]['tarima_inicio']});
					$tarima_fin.attr({'value' : entry['VehiculoTipoUnidades'][0]['tarima_fin']});
					
					$titulo.focus();
				 },"json");//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-logvehiculotipounidades-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-logvehiculotipounidades-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllVehiculoTipoUnidades.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {
            'orderby':'id',
            'desc':'DESC',
            'items_por_pag':10,
            'pag_start':1,
            'display_pag':10,
            'input_json':'/'+controller+'/getAllVehiculoTipoUnidades.json',
            'cadena_busqueda':$cadena_busqueda,
            'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
    
});
