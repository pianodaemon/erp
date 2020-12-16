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
	var controller = $contextpath.val()+"/controllers/provparamanticipos";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_provparamanticipos = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Par&aacute;metros de Anticipos Proveedor');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $select_sucursal_buscador = $('#barra_buscador').find('.tabla_buscador').find('select[name=select_sucursal_buscador]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "id_suc" + signo_separador + $select_sucursal_buscador.val() + "|";
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
	
	
	var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSucursalesEmpresa.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
		//carga select con las sucursales de la empresa
		$select_sucursal_buscador.children().remove();
		var sucursal_html = '<option value="0">Seleccionar Sucursal</option>';
		$.each(data['Sucursales'],function(entryIndex,suc){
			sucursal_html += '<option value="' + suc['id'] + '"  >' + suc['titulo'] + '</option>';
		});
		$select_sucursal_buscador.append(sucursal_html);
	});
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-provparamanticipos-window').find('#submit').mouseover(function(){
			$('#forma-provparamanticipos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-provparamanticipos-window').find('#submit').mouseout(function(){
			$('#forma-provparamanticipos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-provparamanticipos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-provparamanticipos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-provparamanticipos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-provparamanticipos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-provparamanticipos-window').find('#close').mouseover(function(){
			$('#forma-provparamanticipos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-provparamanticipos-window').find('#close').mouseout(function(){
			$('#forma-provparamanticipos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-provparamanticipos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-provparamanticipos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-provparamanticipos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-provparamanticipos-window').find("ul.pestanas li").click(function() {
			$('#forma-provparamanticipos-window').find(".contenidoPes").hide();
			$('#forma-provparamanticipos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-provparamanticipos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
    //arreglo para select opcion
    var array_incluye_iva = {
				'false':"No incluye", 
				'true':"Si incluye"
			};
	
	//carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero, indice1, indice2){
		var option_hmtl='';
		
		if(texto_elemento_cero ==''){
			option_hmtl='';
		}else{
			option_hmtl='<option value="0">'+texto_elemento_cero+'</option>';			
		}
		
		$campo_select.children().remove();
		$.each(arreglo_elementos,function(entryIndex,indice){
			if( parseInt(indice[indice1]) == parseInt(elemento_seleccionado) ){
				option_hmtl += '<option value="' + indice[indice1] + '" selected="yes">' + indice[indice2] + '</option>';
			}else{
				option_hmtl += '<option value="' + indice[indice1] + '"  >' + indice[indice2] + '</option>';
			}
		});
		$campo_select.append(option_hmtl);
	}
	
	
	
	
	//nuevo centro de costo
	$new_provparamanticipos.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_provparamanticipos();
		
		var form_to_show = 'formaprovparamanticipos';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-provparamanticipos-window').css({ "margin-left": -330, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-provparamanticipos-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-provparamanticipos-window').find('input[name=identificador]');
		
		var $select_sucursal = $('#forma-provparamanticipos-window').find('select[name=select_sucursal]');
		var $select_anticipo = $('#forma-provparamanticipos-window').find('select[name=select_anticipo]');
		var $select_apl_anticipo = $('#forma-provparamanticipos-window').find('select[name=select_apl_anticipo]');
		var $select_apl_factura = $('#forma-provparamanticipos-window').find('select[name=select_apl_factura]');
		var $select_cacelacion = $('#forma-provparamanticipos-window').find('select[name=select_cacelacion]');
		var $select_incluye_iva = $('#forma-provparamanticipos-window').find('select[name=select_incluye_iva]');
		var $check_requiere_oc = $('#forma-provparamanticipos-window').find('input[name=check_requiere_oc]');
		var $select_consecutivo_sucursal = $('#forma-provparamanticipos-window').find('select[name=select_consecutivo_sucursal]');
		
		var $cerrar_plugin = $('#forma-provparamanticipos-window').find('#close');
		var $cancelar_plugin = $('#forma-provparamanticipos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-provparamanticipos-window').find('#submit');
		
		$campo_id.attr({ 'value' : 0 });
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Par&aacute;metro fue dado de alta con &eacute;xito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-provparamanticipos-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-provparamanticipos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-provparamanticipos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
						
						if( tmp.split(':')[0] == 'integridad' ){
							jAlert(tmp.split(':')[1], 'Atencion!');
						}
						
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getParametro.json';
		$arreglo = {	'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
		
					};
		
		$.post(input_json,$arreglo,function(entry){
			//alert("Aqui no hay nada");
			
			//cargar select de Sucursales
			elemento_seleccionado = 0;
			texto_elemento_cero='Seleccionar Sucursal';
			$carga_campos_select($select_sucursal, entry['Sucursales'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");

			//cargar select de anticipo
			elemento_seleccionado = 0;
			texto_elemento_cero='Seleccionar Movimiento';
			$carga_campos_select($select_anticipo, entry['TMovanticipo'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");

			//cargar select de Aplicado Anticipo
			elemento_seleccionado = 0;
			texto_elemento_cero='Seleccionar Movimiento';
			$carga_campos_select($select_apl_anticipo, entry['TMovAplanticipo'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");

			//cargar select de Aplicado Factura
			elemento_seleccionado = 0;
			texto_elemento_cero='Seleccionar Movimiento';
			$carga_campos_select($select_apl_factura, entry['TMovAplfactura'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");						
			
			//cargar select de mov Cancelacion
			elemento_seleccionado = 0;
			texto_elemento_cero='Seleccionar Movimiento';
			$carga_campos_select($select_cacelacion, entry['TMovCancelacion'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");						
			
			var option_hmtl='';
			$select_incluye_iva.children().remove();
				option_hmtl += '<option value="false">No incluye</option>';
				option_hmtl += '<option value="true" >Si incluye</option>';
			$select_incluye_iva.append(option_hmtl);
			
			//$check_requiere_oc
			
			
			//cargar select de consecutivos de sucursales
			elemento_seleccionado = 0;
			texto_elemento_cero='Sucursal Actual';
			$carga_campos_select($select_consecutivo_sucursal, entry['Consecutivo'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");						
			
		},"json");//termina llamada json
		

		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-provparamanticipos-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-provparamanticipos-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
	});
	
	
        
        
        
	
	var carga_formaprovparamanticipos_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'identificador':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar Par&aacute;metro seleccionado.', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Par&aacute;metro fue eliminado exitosamente.", 'Atencion!');
							$get_datos_grid();
						}else{
							jAlert("El Par&aacute;metro no pudo ser eliminado.", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaprovparamanticipos';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_provparamanticipos();
			$('#forma-provparamanticipos-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-provparamanticipos-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-provparamanticipos-window').find('input[name=identificador]');
			
			var $select_sucursal = $('#forma-provparamanticipos-window').find('select[name=select_sucursal]');
			var $select_anticipo = $('#forma-provparamanticipos-window').find('select[name=select_anticipo]');
			var $select_apl_anticipo = $('#forma-provparamanticipos-window').find('select[name=select_apl_anticipo]');
			var $select_apl_factura = $('#forma-provparamanticipos-window').find('select[name=select_apl_factura]');
			var $select_cacelacion = $('#forma-provparamanticipos-window').find('select[name=select_cacelacion]');
			var $select_incluye_iva = $('#forma-provparamanticipos-window').find('select[name=select_incluye_iva]');
			var $check_requiere_oc = $('#forma-provparamanticipos-window').find('input[name=check_requiere_oc]');
			var $select_consecutivo_sucursal = $('#forma-provparamanticipos-window').find('select[name=select_consecutivo_sucursal]');
			
			var $cerrar_plugin = $('#forma-provparamanticipos-window').find('#close');
			var $cancelar_plugin = $('#forma-provparamanticipos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-provparamanticipos-window').find('#submit');
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getParametro.json';
				$arreglo = {	'id':id_to_show,
								'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-provparamanticipos-overlay').fadeOut(remove);
						jAlert("Los datos del Par&aacute;metro se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-provparamanticipos-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-provparamanticipos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
								
								if( tmp.split(':')[0] == 'integridad' ){
									jAlert(tmp.split(':')[1], 'Atencion!');
								}
								
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$campo_id.attr({ 'value' : entry['Parametro'][0]['id'] });
					$check_requiere_oc.attr('checked', (entry['Parametro'][0]['oc_requerida'] == 'true')? true:false );
					
					
					//cargar select de Sucursales
					$select_sucursal.children().remove();
					var suc_hmtl = '';
					$.each(entry['Sucursales'],function(entryIndex,suc){
						if(parseInt(entry['Parametro'][0]['gral_suc_id'])==parseInt(suc['id'])){
							suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">' + suc['titulo'] + '</option>';
						}else{
							//suc_hmtl += '<option value="' + suc['id'] + '">' + suc['titulo'] + '</option>';
						}
					});
					$select_sucursal.append(suc_hmtl);
					
					
					//cargar select de anticipo
					elemento_seleccionado = entry['Parametro'][0]['cxp_mov_tipo_id'];
					texto_elemento_cero='Seleccionar Movimiento';
					$carga_campos_select($select_anticipo, entry['TMovanticipo'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
					
					//cargar select de Aplicado Anticipo
					elemento_seleccionado = entry['Parametro'][0]['cxp_mov_tipo_id_apl_ant'];
					texto_elemento_cero='Seleccionar Movimiento';
					$carga_campos_select($select_apl_anticipo, entry['TMovAplanticipo'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
					
					//cargar select de Aplicado Factura
					elemento_seleccionado = entry['Parametro'][0]['cxp_mov_tipo_id_apl_fac'];
					texto_elemento_cero='Seleccionar Movimiento';
					$carga_campos_select($select_apl_factura, entry['TMovAplfactura'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");						
					
					//cargar select de mov Cancelacion
					elemento_seleccionado = entry['Parametro'][0]['cxp_mov_tipo_id_can'];
					texto_elemento_cero='Seleccionar Movimiento';
					$carga_campos_select($select_cacelacion, entry['TMovCancelacion'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");						
					
					var option_hmtl='';
					$select_incluye_iva.children().remove();
					if(entry['Parametro'][0]['incluye_iva'] == 'true' ){
						option_hmtl += '<option value="false">No incluye</option>';
						option_hmtl += '<option value="true" selected="yes">Si incluye</option>';
					}else{
						option_hmtl += '<option value="false" selected="yes">No incluye</option>';
						option_hmtl += '<option value="true">Si incluye</option>';
					}
					$select_incluye_iva.append(option_hmtl);
					
					
					//cargar select de consecutivos de sucursales
					elemento_seleccionado = entry['Parametro'][0]['gral_suc_id_consecutivo'];
					texto_elemento_cero='Sucursal Actual';
					$carga_campos_select($select_consecutivo_sucursal, entry['Consecutivo'], elemento_seleccionado, texto_elemento_cero, "id", "titulo");
				},"json");//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-provparamanticipos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-provparamanticipos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllParametros.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllParametros.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaprovparamanticipos_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



