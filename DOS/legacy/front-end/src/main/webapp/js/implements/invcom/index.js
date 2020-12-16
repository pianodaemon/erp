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
    var controller = $contextpath.val()+"/controllers/invcom";
    
    var niveles = {1:"Nivel 1", 2:"Nivel 2", 3:"Nivel 3", 4:"Nivel 4", 5:"Nivel 5"};
    
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_invprodlineas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Comisiones de Art&iacute;culos');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	//var $busqueda_select_grupo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_grupo]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "seccion" + signo_separador + $busqueda_producto.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		//valor_retorno += "grupo" + signo_separador + $busqueda_select_grupo.val() + "|";
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
	
	/*
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
         $('#barra_buscador').toggle( 'blind');
	});	
	*/
	
	
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
		
		$('#forma-invcom-window').find('#submit').mouseover(function(){
			$('#forma-invcom-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-invcom-window').find('#submit').mouseout(function(){
			$('#forma-invcom-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-invcom-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invcom-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invcom-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invcom-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invcom-window').find('#close').mouseover(function(){
			$('#forma-invcom-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invcom-window').find('#close').mouseout(function(){
			$('#forma-invcom-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-invcom-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invcom-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invcom-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invcom-window').find("ul.pestanas li").click(function() {
			$('#forma-invcom-window').find(".contenidoPes").hide();
			$('#forma-invcom-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invcom-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	//buscador de productos
	$busca_productos = function(){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -140});
		
		var $tabla_resultados = $('#forma-buscaproducto-window').find('#tabla_resultado');
		
		var $campo_sku = $('#forma-buscaproducto-window').find('input[name=campo_sku]');
		var $select_tipo_producto = $('#forma-buscaproducto-window').find('select[name=tipo_producto]');
		var $campo_descripcion = $('#forma-buscaproducto-window').find('input[name=campo_descripcion]');
		
		var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('#busca_producto_modalbox');
		var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('#cencela');
			
		//funcionalidad botones
		$buscar_plugin_producto.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_producto.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_producto.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_producto.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});		
		$campo_sku.val("");
		
		
		//buscar todos los tipos de productos
		var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_tipos,$arreglo,function(data){
			//Llena el select tipos de productos en el buscador
			$select_tipo_producto.children().remove();
			var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			$.each(data['prodTipos'],function(entryIndex,pt){
				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos.json';
			$arreglo = {	'sku':$campo_sku.val(),
							'tipo':$select_tipo_producto.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['productos'],function(entryIndex,producto){
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
							trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
						trr += '</td>';
						trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
						trr += '<td width="90">';
							trr += '<span class="unidad_id" style="display:none;">'+producto['unidad_id']+'</span>';
							trr += '<span class="utitulo">'+producto['unidad']+'</span>';
						trr += '</td>';
						trr += '<td width="90"><span class="tipo_prod_buscador">'+producto['tipo']+'</span></td>';
					trr += '</tr>';
					$tabla_resultados.append(trr);
				});
				$tabla_resultados.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});

				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({background : '#FBD850'});
				}, function() {
					//$(this).find('td').css({'background-color':'#DDECFF'});
					$(this).find('td').css({'background-color':'#e7e8ea'});
				});
				$('tr:even' , $tabla_resultados).hover(function () {
					$(this).find('td').css({'background-color':'#FBD850'});
				}, function() {
					$(this).find('td').css({'background-color':'#FFFFFF'});
				});
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					//asignar a los campos correspondientes el sku y y descripcion
					$('#forma-entradamercancias-window').find('input[name=id_producto]').val($(this).find('#id_prod_buscador').val());
					$('#forma-entradamercancias-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-entradamercancias-window').find('input[name=titulo_producto]').val($(this).find('span.titulo_prod_buscador').html());
					
					$('#forma-invcom-window').find('input[name=producto_id]').val($(this).find('#id_prod_buscador').val());
					$('#forma-invcom-window').find('input[name=productosku]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-invcom-window').find('input[name=producto_descripcion]').val($(this).find('span.titulo_prod_buscador').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-entradamercancias-window').find('input[name=sku_producto]').focus();
				});
                                
			});
		})
		
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_sku.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}

		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaproducto-overlay').fadeOut(remove);
		});
	}//termina buscador de productos
	
        
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
        
        $add_ceros = function($campo){
            $campo.val("0.00");
        }
        
        $accio_blur = function($campo){
            //recalcula importe al perder enfoque el campo costo
            $campo.blur(function(){
                
                $valor_tmp = $(this).val().split(",").join("");
                
                if ($valor_tmp == ''  || $valor_tmp == null){
                        $(this).val('0.00');
                }
                
                if( ($valor_tmp != '') && ($valor_tmp != ' ') )
                {
                    $campo.val($(this).agregar_comas(parseFloat($valor_tmp).toFixed(2)));
                }else{
                        $(this).val('0.00');
                }
                
            });
        }
        
        
	//nuevo centro de costo
	$new_invprodlineas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_modalboxInvCom();
		
		var form_to_show = 'formaInvCom00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-invcom-window').css({"margin-left": -390, 	"margin-top": -210});
		$forma_selected.prependTo('#forma-invcom-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-invcom-window').find('input[name=identificador]');
		var $producto_id = $('#forma-invcom-window').find('input[name=producto_id]');
		var $productosku = $('#forma-invcom-window').find('input[name=productosku]');
		var $producto_descripcion = $('#forma-invcom-window').find('input[name=producto_descripcion]');
		var $producto_unidad = $('#forma-invcom-window').find('input[name=producto_unidad]');
		
		var $nivel = $('#forma-invcom-window').find('select[name=nivel]');
		
		var $escala = $('#forma-invcom-window').find('input[name=escala]');
		var $limiteinf = $('#forma-invcom-window').find('input[name=limiteinf]');
		var $limitesup = $('#forma-invcom-window').find('input[name=limitesup]');
		var $comicionporciento = $('#forma-invcom-window').find('input[name=comicionporciento]');
		var $comicionvalor = $('#forma-invcom-window').find('input[name=comicionvalor]');
		
		
		//href para buscar producto
		var $buscar_producto = $('#forma-invcom-window').find('a[href*=busca_producto]');
		
		var $cerrar_plugin = $('#forma-invcom-window').find('#close');
		var $cancelar_plugin = $('#forma-invcom-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invcom-window').find('#submit');
		
		$campo_id.attr({'value' : 0});
		$producto_id.attr({'value' : 0});
                
		var respuestaProcesada = function(data){
			if ( data['success'] == 'true' ){
				var remove = function() {$(this).remove();};
				$('#forma-invcom-overlay').fadeOut(remove);
				jAlert("La comision se ha actualizado.", 'Atencion!');
				$get_datos_grid();
			}
			else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invcom-window').find('div.interrogacion').css({'display':'none'});

				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
							$('#forma-invcom-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		$buscar_producto.click(function(event){
			event.preventDefault();
			$busca_productos();
		})
		
		$nivel.children().remove();
		var nivel_hmtl = '';
		for(var i in niveles){   
			nivel_hmtl += '<option value="' + i + '"  >' + niveles[i] + '</option>';
		}
		$nivel.append(nivel_hmtl);
		
		
		$add_ceros($limiteinf);
		$add_ceros($limitesup);
		$add_ceros($comicionporciento);
		$add_ceros($comicionvalor);
		
		/*para que permita solo numeros*/
		$permitir_solo_numeros($escala);
		$permitir_solo_numeros($limitesup);
		$permitir_solo_numeros($comicionporciento);
		$permitir_solo_numeros($comicionvalor);
		
		$accio_blur($limiteinf);
		$accio_blur($limitesup);
		$accio_blur($comicionporciento);
		$accio_blur($comicionvalor);
		/*
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvPre.json';
		$arreglo = {'id':id_to_show};
		
		$.post(input_json,$arreglo,function(entry){
			
                        //Alimentando los campos select de secciones
			$seccion.children().remove();
			var seccion_hmtl = '<option value="0" selected="yes">[--Seleccionar una seccion--]</option>';
			$.each(entry['Secciones'],function(entryIndex,seccion){
				seccion_hmtl += '<option value="' + seccion['id'] + '"  >' + seccion['titulo'] + '</option>';
			});
			$seccion.append(seccion_hmtl);
                        
                        //Alimentando los campos select de Marcas
			$marcasdisponibles.children().remove();
			var marcasdisponibles = '';
			$.each(entry['Marcas'],function(entryIndex,marca){
				marcasdisponibles += '<option value="' + marca['id'] + '"  >' + marca['titulo'] + '</option>';
			});
			$marcasdisponibles.append(marcasdisponibles);
                        
		});//termina llamada json
		
                $agregar_remover_marcas();
                */
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invcom-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invcom-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
        });
	
        
	
	var carga_formaInvCom00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la comision para el producto', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La comision fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La comision no puden ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaInvCom00';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_modalboxInvCom();
			
			var form_to_show = 'formaInvCom00';

			$('#forma-invcom-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-invcom-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-invcom-window').find('input[name=identificador]');
			var $producto_id = $('#forma-invcom-window').find('input[name=producto_id]');
			var $productosku = $('#forma-invcom-window').find('input[name=productosku]');
			var $producto_descripcion = $('#forma-invcom-window').find('input[name=producto_descripcion]');
			var $producto_unidad = $('#forma-invcom-window').find('input[name=producto_unidad]');

			var $nivel = $('#forma-invcom-window').find('select[name=nivel]');

			var $escala = $('#forma-invcom-window').find('input[name=escala]');
			var $limiteinf = $('#forma-invcom-window').find('input[name=limiteinf]');
			var $limitesup = $('#forma-invcom-window').find('input[name=limitesup]');
			var $comicionporciento = $('#forma-invcom-window').find('input[name=comicionporciento]');
			var $comicionvalor = $('#forma-invcom-window').find('input[name=comicionvalor]');
			
			//href para buscar producto
			var $buscar_producto = $('#forma-invcom-window').find('a[href*=busca_producto]');
                        
                        
			var $cerrar_plugin = $('#forma-invcom-window').find('#close');
			var $cancelar_plugin = $('#forma-invcom-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invcom-window').find('#submit');
			
			$buscar_producto.hide();
                        
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvCom.json';
				$arreglo = {'id':id_to_show};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-invcom-overlay').fadeOut(remove);
						jAlert("La comision para este producto se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-invcom-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
									$('#forma-invcom-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					
					$campo_id.attr({'value' : entry['InvCom']['0']['id']});
					$producto_id.attr({'value' : entry['InvCom']['0']['inv_prod_id']});
					$productosku.attr({'value' : entry['InvCom']['0']['sku']});
					$producto_descripcion.attr({'value' : entry['InvCom']['0']['titulo']});
					
					$limiteinf.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvCom']['0']['limite_inferior']).toFixed(2))});
					$limitesup.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvCom']['0']['limite_superior']).toFixed(2))});
					$comicionporciento.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvCom']['0']['comision']).toFixed(2))});
					$comicionvalor.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvCom']['0']['comision_valor']).toFixed(2))});
					$escala.attr({'value' : entry['InvCom']['0']['escala']});
					//nivelnivel
					$nivel.children().remove();
					var nivel_hmtl = '';
					for(var i in niveles){
						if(entry['InvCom']['0']['nivel'] == i){
							nivel_hmtl += '<option value="' + i + '" selected="yes >' + niveles[i] + '</option>';
						}else{
							nivel_hmtl += '<option value="' + i + '"  >' + niveles[i] + '</option>';
						}
					}
					$nivel.append(nivel_hmtl);

					
					/*para que permita solo numeros*/
					$permitir_solo_numeros($limiteinf);
					$permitir_solo_numeros($limitesup);
					$permitir_solo_numeros($comicionporciento);
					$permitir_solo_numeros($comicionvalor);
					
					$accio_blur($limiteinf);
					$accio_blur($limitesup);
					$accio_blur($comicionporciento);
					$accio_blur($comicionvalor);
                                        
				},"json");//termina llamada json
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invcom-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invcom-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllInvCom.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllInvCom.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaInvCom00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



