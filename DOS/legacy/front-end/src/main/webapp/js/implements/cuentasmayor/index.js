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
	var controller = $contextpath.val()+"/controllers/cuentasmayor";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_cuenta_mayor = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Clasificaci&oacute;n de Cuentas');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'60px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	//$('#barra_buscador').hide();
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_clasificacion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_clasificacion]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	var $busqueda_select_ctamayor = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_ctamayor]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "clasificacion" + signo_separador + $busqueda_clasificacion.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		valor_retorno += "ctamayor" + signo_separador + $busqueda_select_ctamayor.val() + "|";
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
			 $('#barra_buscador').animate({height: '70px'}, 500);
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
	
	
	
	
	
	
	
	
	//obtiene los grupos para el buscador
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getClasesCtaMayorBuscador.json';
	$arreglo = {};
	$.post(input_json,$arreglo,function(entry){
		//Alimentando los campos select de grupos del buscador
		$busqueda_select_ctamayor.children().remove();
		var clases_hmtl = '<option value="0" selected="yes">[--Seleccionar Cuenta--]</option>';
		$.each(entry['Clases'],function(entryIndex,grupo){
			clases_hmtl += '<option value="' + grupo['id'] + '"  >' + grupo['titulo'] + '</option>';
		});
		$busqueda_select_ctamayor.append(clases_hmtl);
	});//termina llamada json
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-cuentasmayor-window').find('#submit').mouseover(function(){
			$('#forma-cuentasmayor-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-cuentasmayor-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-cuentasmayor-window').find('#submit').mouseout(function(){
			$('#forma-cuentasmayor-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-cuentasmayor-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-cuentasmayor-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-cuentasmayor-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-cuentasmayor-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-cuentasmayor-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-cuentasmayor-window').find('#close').mouseover(function(){
			$('#forma-cuentasmayor-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-cuentasmayor-window').find('#close').mouseout(function(){
			$('#forma-cuentasmayor-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-cuentasmayor-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-cuentasmayor-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-cuentasmayor-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-cuentasmayor-window').find("ul.pestanas li").click(function() {
			$('#forma-cuentasmayor-window').find(".contenidoPes").hide();
			$('#forma-cuentasmayor-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-cuentasmayor-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	
	
	//nueva cuenta de mayor
	$new_cuenta_mayor.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_cuentasmayor();
		
		var form_to_show = 'formaCuentasmayor00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-cuentasmayor-window').css({ "margin-left": -300, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-cuentasmayor-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-cuentasmayor-window').find('input[name=identificador]');
		var $select_ctamayor = $('#forma-cuentasmayor-window').find('select[name=select_ctamayor]');
		var $clasificacion = $('#forma-cuentasmayor-window').find('input[name=clasificacion]');
		var $des_predeterminada = $('#forma-cuentasmayor-window').find('input[name=des_predeterminada]');
		var $des_espanol = $('#forma-cuentasmayor-window').find('input[name=des_espanol]');
		var $des_ingles = $('#forma-cuentasmayor-window').find('input[name=des_ingles]');
		var $des_otro = $('#forma-cuentasmayor-window').find('input[name=des_otro]');
		
		var $cerrar_plugin = $('#forma-cuentasmayor-window').find('#close');
		var $cancelar_plugin = $('#forma-cuentasmayor-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-cuentasmayor-window').find('#submit');
		
		$campo_id.attr({ 'value' : 0 });
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Cuenta de Mayor dado de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-cuentasmayor-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-cuentasmayor-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-cuentasmayor-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCuentaDeMayor.json';
		$arreglo = {'id':id_to_show};
		
		$.post(input_json,$arreglo,function(entry){
			//Alimentando select cuenta de mayor
			$select_ctamayor.children().remove();
			var clases_hmtl = '<option value="0" selected="yes">[--Seleccionar Cuenta--]</option>';
			$.each(entry['Clases'],function(entryIndex,grupo){
				clases_hmtl += '<option value="' + grupo['id'] + '"  >' + grupo['titulo'] + '</option>';
			});
			$select_ctamayor.append(clases_hmtl);
		},"json");//termina llamada json
		
		//alimentado campo Predeterminada con el contenido de Descripcion en español cada vez que se pulsa una tecla
		$des_espanol.keyup(function(e){
			$des_predeterminada.val($des_espanol.val());
		});
		
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-cuentasmayor-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-cuentasmayor-overlay').fadeOut(remove);
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
			jConfirm('Realmente desea La Cuenta de Mayor seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Cuenta de Mayor fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Cuenta de Mayor no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaCuentasmayor00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_cuentasmayor();
			$('#forma-cuentasmayor-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-cuentasmayor-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-cuentasmayor-window').find('input[name=identificador]');
			var $select_ctamayor = $('#forma-cuentasmayor-window').find('select[name=select_ctamayor]');
			var $clasificacion = $('#forma-cuentasmayor-window').find('input[name=clasificacion]');
			var $des_predeterminada = $('#forma-cuentasmayor-window').find('input[name=des_predeterminada]');
			var $des_espanol = $('#forma-cuentasmayor-window').find('input[name=des_espanol]');
			var $des_ingles = $('#forma-cuentasmayor-window').find('input[name=des_ingles]');
			var $des_otro = $('#forma-cuentasmayor-window').find('input[name=des_otro]');
			
			var $cerrar_plugin = $('#forma-cuentasmayor-window').find('#close');
			var $cancelar_plugin = $('#forma-cuentasmayor-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-cuentasmayor-window').find('#submit');
			
			
		
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCuentaDeMayor.json';
				$arreglo = {'id':id_to_show};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-cuentasmayor-overlay').fadeOut(remove);
						jAlert("Los datos la Cuenta de Mayor se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-cuentasmayor-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-cuentasmayor-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$campo_id.attr({ 'value' : entry['CtaMayor']['0']['id'] });
					$clasificacion.attr({ 'value' : entry['CtaMayor']['0']['clasificacion'] });
					$des_predeterminada.attr({ 'value' : entry['CtaMayor']['0']['predeterminada'] });
					$des_espanol.attr({ 'value' : entry['CtaMayor']['0']['descripcion'] });
					$des_ingles.attr({ 'value' : entry['CtaMayor']['0']['descripcion_ing'] });
					$des_otro.attr({ 'value' : entry['CtaMayor']['0']['descripcion_otr'] });
					
					$select_ctamayor.children().remove();
					var cta_hmtl = '<option value="0">[--Seleccionar Cuenta--]</option>';
					$.each(entry['Clases'],function(entryIndex,cta){
						if(cta['id'] == entry['CtaMayor']['0']['ctb_may_clase_id']){
							cta_hmtl += '<option value="' + cta['id'] + '" selected="yes">' + cta['titulo'] + '</option>';
						}else{
							cta_hmtl += '<option value="' + cta['id'] + '"  >' + cta['titulo'] + '</option>';
						}
					});
					$select_ctamayor.append(cta_hmtl);
					
				});//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-cuentasmayor-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-cuentasmayor-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllCuentasDeMayor.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllCuentasDeMayor.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
});



