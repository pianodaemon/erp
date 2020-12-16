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
	var usuario_codificado = $('#lienzo_recalculable').find('input[name=iu]').val();
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invprodequiv";
    
        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_productos = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Productos');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
        var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');

	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
                valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	$buscar.click(function(event){
		event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	
	
	$limpiar.click(function(event){
		event.preventDefault();
                $busqueda_descripcion.val('');
                $busqueda_codigo.val('');
	});
        
        
        $aplicar_evento_focus_input = function( $campo_input ){
		$campo_input.focus(function(e){
			if($campo_input.val() =='Escriba su observaci\u00f3n'){
				$(this).val('');
			}
		});
	}
	
	$aplicar_evento_blur_input = function( $campo_input ){
		$campo_input.blur(function(e){
			if($campo_input.val()==""){
				$campo_input.val('Escriba su observaci\u00f3n');
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
			 
			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
			 $('#barra_buscador').animate({height: '60px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
                         
		}else{
			 TriggerClickVisializaBuscador=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
		};
	});
	
	
	
	
	//buscador de productos                                  sku_buscar
	busca_productos     = function(buscador_producto,sku_buscar){
			$(this).modalPanel_Buscaproducto();
			var $dialogoc =  $('#forma-buscaproducto-window');
			$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());

			$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -200});
                        
			var $unidad                  = $('#forma-invprodequiv-window').find('input[name=select_unidad]');
			
			var $tabla_resultados = $('#forma-buscaproducto-window').find('#tabla_resultado');
			var $codigo_Producto          = $('#forma-invprodequiv-window').find('input[name=codigo]');
			var $descripcion_Producto     = $('#forma-invprodequiv-window').find('input[name=descripcion]');
			var $codigo_producto_minigrid       = $('#forma-invprodequiv-window').find('input[name=codigo_producto_minigrid]');
			var $descripcion_producto_minigrid  = $('#forma-invprodequiv-window').find('input[name=descr_producto_minigrid]');
			var $id_producto             = $('#forma-invprodequiv-window').find('input[name=id_prod]');
			var $id_producto_entrante           = $('#forma-invprodequiv-window').find('input[name=id_prod_entrante]');
			var $campo_sku = $('#forma-buscaproducto-window').find('input[name=campo_sku]');
			var $select_tipo_producto = $('#forma-buscaproducto-window').find('select[name=tipo_producto]');
			var $campo_descripcion = $('#forma-buscaproducto-window').find('input[name=campo_descripcion]');
			var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('#busca_producto_modalbox');
			var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('#cencela');
                        var $id_tipo_producto = $('#forma-invprodequiv-window').find('input[name=tipo_id]');
                        var titulo = $id_tipo_producto.val();
                        
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

			//buscar todos los tipos de productos
			
			var input_json_tipos =document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
                        $arreglo = {
                            'iu':usuario_codificado,
                            'identificador':usuario_codificado,
                            //'titulo_producto':id_tipo_producto,
                            'buscador_producto':titulo
                        };
			
                        
                        
                        
			$.post(input_json_tipos,$arreglo,function(data){
				//Llena el select tipos de productos en el buscador
				$select_tipo_producto.children().remove();
				var prod_tipos_html = '';
				$.each(data['prodTipos'],function(entryIndex,pt){
						prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
				});
				$select_tipo_producto.append(prod_tipos_html);
                                //alert($select_tipo_producto.val());
			});

			//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
			
			//click buscar productos
			$buscar_plugin_producto.click(function(event){
					//event.preventDefault();
					$tabla_resultados.children().remove();
					var restful_json_service = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos_equivalentes.json';
					arreglo_parametros = {
							'sku':$campo_sku.val(),
							'tipo':$select_tipo_producto.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':usuario_codificado,
                                                        'id_producto':$id_producto.val()
					}
							  
					
					var trr = '';
					$.post(restful_json_service,arreglo_parametros,function(entry){
						
						$.each(entry['Productos'],function(entryIndex,producto){
							trr = '<tr>';
								trr += '<td width="120">';
										trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
										trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
										trr += '<span class="id_sku_prod_buscador">'+producto['id']+'</span>';
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
								$(this).find('td').css({'background-color':'#e7e8ea'});
						});
						$('tr:even' , $tabla_resultados).hover(function () {
								$(this).find('td').css({'background-color':'#FBD850'});
						}, function() {
								$(this).find('td').css({'background-color':'#FFFFFF'});
						});

						//seleccionar un producto del grid de resultados
						$tabla_resultados.find('tr').click(function(){
							
							var id_prod=$(this).find('#id_prod_buscador').val();
							var codigo=$(this).find('span.sku_prod_buscador').html();
							var descripcion=$(this).find('span.titulo_prod_buscador').html();
							var producto=$(this).find('span.tipo_prod_buscador').html();
							var unidad=$(this).find('span.utitulo').html();
							
                                                        //elimina la ventana de busqueda
							var remove = function() {$(this).remove();};
							$('#forma-buscaproducto-overlay').fadeOut(remove);
							//asignar el enfoque al campo sku del producto
							$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();

							
							if(buscador_producto ==1){
								//colocando los datos elegidos en el pluguin 1
								$id_producto.val(id_prod);
								$codigo_Producto.val(codigo);
								$descripcion_Producto.val(descripcion);
                                                                $id_tipo_producto.val(producto);
								//fin del primer plugin
							}
							if(buscador_producto ==2){
                                                            //colocando los datos elegidos en el pluguin 2
								$id_producto_entrante.val(id_prod);
								$codigo_producto_minigrid.val(codigo);
								$descripcion_producto_minigrid.val(descripcion);
                                                                //$id_tipo_producto.val(producto);
                                                                //fin del plugin 2
								
							}
								
						});
						
					});//termina llamada json
			});

			$cancelar_plugin_busca_producto.click(function(event){
				var remove = function() {$(this).remove();};
				$('#forma-buscaproducto-overlay').fadeOut(remove);
			});
	}//termina buscador de productos
	
	
	//nuevos productos
	$new_productos.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_InvProdEquiv();   //llamada al plug in 
		
		var form_to_show = 'formaInvProdEquiv';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-invprodequiv-window').css({"margin-left": -390, 	"margin-top": -270});
		$forma_selected.prependTo('#forma-invprodequiv-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		//tabs_li_funxionalidad();
		
		var $campo_id                       = $('#forma-invprodequiv-window').find('input[name=identificador]');
		var $codigo_Producto                = $('#forma-invprodequiv-window').find('input[name=codigo]');
		var $descripcion_Producto           = $('#forma-invprodequiv-window').find('input[name=descripcion]');
		var $href_buscar_Producto           = $('#forma-invprodequiv-window').find('a[href*=buscar_producto]');
		var $codigo_producto_minigrid       = $('#forma-invprodequiv-window').find('input[name=codigo_producto_minigrid]');
		var $descripcion_producto_minigrid  = $('#forma-invprodequiv-window').find('input[name=descr_producto_minigrid]');
		var $buscar_producto_para_minigrid  = $('#forma-invprodequiv-window').find('a[href*=busca_producto_equivalente]');
		var $agregar_producto_minigrid      = $('#forma-invprodequiv-window').find('a[href*=agregar_producto_minigrid]');
                var $id_producto                    = $('#forma-invprodequiv-window').find('input[name=id_prod]');
                var $id_producto_entrante           = $('#forma-invprodequiv-window').find('input[name=id_prod_entrante]');
                var $grid_productos_componentes     = $('#forma-invprodequiv-window').find('div.div_formulaciones');
                var $id_tipo_producto = $('#forma-invprodequiv-window').find('input[name=tipo_id]');
                var tabla = $grid_productos_componentes.find('tbody');
                $buscar_producto_para_minigrid.hide();
                
                var $total_tr = $('#forma-invprodequiv-window').find('input[name=total_tr]');
                                
		//botones		
		var $cerrar_plugin = $('#forma-invprodequiv-window').find('#close');
		var $cancelar_plugin = $('#forma-invprodequiv-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invprodequiv-window').find('#submit');
		var buscador_producto= 0;
		
		$codigo_Producto.attr({'readOnly':true});
		$descripcion_Producto.attr({'readOnly':true});
		$codigo_producto_minigrid.attr({'readOnly':true});
		$descripcion_producto_minigrid.attr({'readOnly':true});
                $id_tipo_producto.attr({'readOnly':true}); 
	   
		$href_buscar_Producto.click(function(event){
                        $buscar_producto_para_minigrid.show(); 
			event.preventDefault();
			buscador_producto=1;
			var sku_buscar=$codigo_Producto.val();
			busca_productos(buscador_producto,sku_buscar);
                        
		});

		$agregar_producto_minigrid.click(function(event){
                        $href_buscar_Producto.hide();
			event.preventDefault();
			$agrega_producto_ingrediente();
			$codigo_producto_minigrid.val("");
			$descripcion_producto_minigrid.val("");
		});
		
		$buscar_producto_para_minigrid.click(function(event){
			event.preventDefault();
			buscador_producto=2;
			var sku_buscar=$codigo_producto_minigrid.val();
			busca_productos(buscador_producto,sku_buscar);
                        
		});
		
		$campo_id.attr({'value' : 0});
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El producto equivalente fue dado de alta con exito", 'Atencion!');
				var $grid_productos_componentes     = $('#forma-invprodequiv-window').find('div.div_formulaciones');
				var $href_buscar_Producto    = $('#forma-invprodequiv-window').find('a[href*=buscar_producto]');
				var $href_buscar_Productoequivalente   = $('#forma-invprodequiv-window').find('a[href*=busca_producto_equivalente]');
				$grid_productos_componentes.find('#minigrid').find('tbody').children().remove();
				$descripcion_Producto=$('#forma-invprodequiv-window').find('#my-select option:selected').html();
				var remove = function() {$(this).remove();};
				$('#forma-invprodequiv-overlay').fadeOut(remove);
                                
				$get_datos_grid();
                                
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invprodequiv-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-invprodequiv-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductosEquivalentes.json';
		$arreglo = {'id':id_to_show,
				'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
			   };
		
		$.post(input_json,$arreglo,function(entry){
			//aqui no va nada por ahora porque no hay camposse esten cargandi desde un inicio
                },"json");//termina llamada json


		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invprodequiv-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invprodequiv-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
                
                $submit_actualizar.bind('click',function(){
                                        var trCount = $("tr", tabla).size();
                                        if(parseInt(trCount) > 0){
                                                return true;
                                        }else{
                                                jAlert("Debe de agregar por lo menos un producto equivalente", 'Atencion!');
                                                return false;
                                        }
                });
                
                
	});
        //agrega producto al grid
		$agrega_producto_ingrediente = function(){
                    
                                //var $select_unidad                  = $('#forma-invprodequiv-window').find('input[name=select_unidad]');
				var $codigo_producto_minigrid       = $('#forma-invprodequiv-window').find('input[name=codigo_producto_minigrid]');
				var $grid_productos_componentes     = $('#forma-invprodequiv-window').find('div.div_formulaciones');
                                var tabla = $grid_productos_componentes.find('tbody');
				var $total_tr = $('#forma-invprodequiv-window').find('input[name=total_tr]');
                                var $submit_actualizar = $('#forma-invprodequiv-window').find('#submit');
                                if($codigo_producto_minigrid.val() != null && $codigo_producto_minigrid.val() != ''){
					
                                        //1 agregado
                                        var encontrado = 0;
                                        var codigo = $codigo_producto_minigrid.val();
                                        
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_busca_sku_prod.json';
                                        $arreglo = {'sku':$codigo_producto_minigrid.val(), 'iu': usuario_codificado}

                                        $.post(input_json,$arreglo,function(prod){
                                            var tabla = $grid_productos_componentes.find('tbody');
                                            tabla.find('tr').each(function (index){
                                            
                                            if(( $(this).find('#sku').html() == codigo)){
                                                encontrado=1;//el producto ya esta en el grid
                                                }
                                            });
                                            
                                            if(parseInt(encontrado)==0){
                                                var trCount = $("tr", tabla).size();
                                                trCount++;
                                                var trr='';
                                                        //var res=0;
                                                        if(prod['Sku'][0] != null){
                                                                var trCount = $("tbody > tr", $grid_productos_componentes).size();
                                                                trCount++;
                                                                trr = '<tr>';
                                                                    trr += '<td>';
                                                                    trr += '<a href=#>Eliminar</a>';
                                                                    trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                                                                    trr += '<input type="hidden" id="id_producto_equivalente" name="id_producto_equivalente" value="'+prod['Sku'][0]['id']+'">';
                                                                    trr += '</td>';
                                                                    trr += '<td id="sku">'+prod['Sku'][0]['sku']+'</td>';
                                                                    trr += '<td>'+prod['Sku'][0]['descripcion']+'</td>';
                                                                    trr += '<td>';
                                                                    trr += '<INPUT TYPE="text" id ="ob" name="observacion" value="Escriba su observaci&oacute;n" style="width:200px;height:20px">';
                                                                    trr += '</td>';
                                                                trr += '</tr>';
                                                                var tabla = $grid_productos_componentes.find('tbody');
                                                                tabla.append(trr);
                                                                var observ = tabla.find('#ob');
                                                                $aplicar_evento_focus_input(observ);
                                                                $aplicar_evento_blur_input (observ);
                                                                

                                                                tabla.find('a').bind('click',function(event){
                                                                        //if(parseInt($(this).parent().find('#delete').val()) != 0){
                                                                                $(this).parent().parent().remove();
                                                                        //}
                                                                });

                                                        }else{
                                                                jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                                                        }
                                                        }else{
                                        if(parseInt(encontrado)==1){
                                        jAlert("El producto ya se encuentra en el listado, seleccione otro diferente.", 'Atencion!');
                                        }
                                                        }
                                        },"json");
	
				}else{
					jAlert("Ingrese un Producto v&aacute;lido", 'Atencion!');
				}
                                
                                
                                $submit_actualizar.bind('click',function(){
                                        var trCount = $("tr", tabla).size();
                                        if(parseInt(trCount) > 0){
                                                return true;
                                        }else{
                                                jAlert("Debe de agregar por lo menos un producto equivalente", 'Atencion!');
                                                return false;
                                        }
                                });
						  
		}
	
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el producto equivalente seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El producto  fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El producto no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaInvProdEquiv';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_InvProdEquiv();
			$('#forma-invprodequiv-window').css({"margin-left": -390, 	"margin-top": -270});
			
			$forma_selected.prependTo('#forma-invprodequiv-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
                        var $campo_id                       = $('#forma-invprodequiv-window').find('input[name=identificador]');
                        var $codigo_Producto                = $('#forma-invprodequiv-window').find('input[name=codigo]');
                        var $descripcion_Producto           = $('#forma-invprodequiv-window').find('input[name=descripcion]');
                        var $href_buscar_Producto           = $('#forma-invprodequiv-window').find('a[href*=buscar_producto]');
                        var $codigo_producto_minigrid       = $('#forma-invprodequiv-window').find('input[name=codigo_producto_minigrid]');
                        var $descripcion_producto_minigrid  = $('#forma-invprodequiv-window').find('input[name=descr_producto_minigrid]');
                        var $buscar_producto_para_minigrid  = $('#forma-invprodequiv-window').find('a[href*=busca_producto_equivalente]');
                        var $agregar_producto_minigrid      = $('#forma-invprodequiv-window').find('a[href*=agregar_producto_minigrid]');
                        var $id_producto                    = $('#forma-invprodequiv-window').find('input[name=id_prod]');
                        var $id_producto_entrante           = $('#forma-invprodequiv-window').find('input[name=id_prod_entrante]');
                        var $grid_productos_componentes     = $('#forma-invprodequiv-window').find('div.div_formulaciones');
                        var $id_tipo_producto = $('#forma-invprodequiv-window').find('input[name=tipo_id]');
                        var tabla = $grid_productos_componentes.find('tbody');
                        
                        $href_buscar_Producto.hide();
                        var $total_tr = $('#forma-invprodequiv-window').find('input[name=total_tr]');
                        
                        //botones		
                        var $cerrar_plugin      = $('#forma-invprodequiv-window').find('#close');
                        var $cancelar_plugin    = $('#forma-invprodequiv-window').find('#boton_cancelar');
                        var $submit_actualizar  = $('#forma-invprodequiv-window').find('#submit');
                        //var buscador_producto= 0;
			
			var $submit_actualizar  = $('#forma-invprodequiv-window').find('#submit');
			if(accion_mode == 'edit'){
                            
				//aqui es el post que envia los datos
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductosEquivalentes.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-invprodequiv-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-invprodequiv-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-invprodequiv-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
                                            
                                                        $codigo_Producto.attr({'readOnly':true});
							$descripcion_Producto.attr({'readOnly':true});
							$codigo_producto_minigrid.attr({'readOnly':true});
							$descripcion_producto_minigrid.attr({'readOnly':true});
                                                        $id_tipo_producto.attr({'readOnly':true}); 
							
							
							$.each(entry['ProductosEquivalentes'],function(entryIndex,pod_equiv){
                                                           $campo_id.val( pod_equiv['id']); 
                                                           $id_producto.val(pod_equiv['id_producto']);
                                                           $codigo_Producto.val(pod_equiv['codigo']);
                                                           $descripcion_Producto.val(pod_equiv['descripcion']);                                                            
                                                           $id_tipo_producto.val(pod_equiv['tipo_producto']);
                                                        });
							
							$codigo_producto_minigrid.val();
							$buscar_producto_para_minigrid.click(function(event){
                                                            event.preventDefault();
                                                            buscador_producto=2;
                                                            var sku_buscar=$codigo_producto_minigrid.val();
                                                            busca_productos(buscador_producto,sku_buscar);
							});
                                                        
							//$id_producto_entrante.val();

							var contador=0;

							//Productos equivalnte_DatosMinigrid.
							$.each(entry['ProductosEquivalentes_DatosMinigrid'],function(entryIndex,prod){
                                                            var trCount = $("tbody > tr", tabla).size();
                                                            contador=contador+1;
                                                            trr = '<tr>';
                                                                trr += '<td>';
                                                                trr += '<a href=#>Eliminar</a>';
                                                                trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                                                                trr += '<input type="hidden" id="id_producto_equivalente" name="id_producto_equivalente" value="'+prod['inv_prod_id_equiv']+'">';
                                                                trr += '</td>';
                                                                trr += '<td id="sku">'+prod['codigo']+'</td>';
                                                                trr += '<td         >'+prod['descripcion']+'</td>'
                                                                trr += '<td>';
                                                                trr += '<INPUT TYPE="text" id ="ob" name="observacion" style="width:200px;height:20px" value="'+prod['observaciones'] +'">';                                                                            
                                                                trr += '</td>';
                                                            trr += '</tr>';
							var tabla = $grid_productos_componentes.find('tbody');
							tabla.append(trr);
                                                        var observ = tabla.find('#ob');
                                                        $aplicar_evento_focus_input(observ);
                                                        $aplicar_evento_blur_input (observ);
                                                        
                                                        
                                                        tabla.find('a').bind('click',function(event){
                                                            $(this).parent().parent().remove();
                                                        });
						   
						  trCount++;
						});

						
						$agregar_producto_minigrid.click(function(event){
                                                    event.preventDefault();
                                                    $agrega_producto_ingrediente();
                                                    $codigo_producto_minigrid.val("");
                                                    $descripcion_producto_minigrid.val("");
							
						});
						
				 },"json");//termina llamada json
				
				
//				$genera_pdf_productosequivalentes.click(function(event){
//					event.preventDefault();
//					iu=$('#lienzo_recalculable').find('input[name=iu]').val();
//					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
//                                        var stock = $('#forma-invprodequiv-window').find('input[name=stock]').is(':checked');
//                                        
//                                        var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfproductosequivalentes/'+$id_producto_master.val()+'/'+stock+'/'+iu+'/out.json';
//					window.location.href=input_json;
//				});
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invprodequiv-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invprodequiv-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
                                
                                $submit_actualizar.bind('click',function(){
                                        var trCount = $("tr", tabla).size();
                                        if(parseInt(trCount) > 0){
                                                return true;
                                        }else{
                                            jAlert("Debe de agregar por lo menos un producto equivalente", 'Atencion!');
                                            return false;
                                        }
                                });
		 }
            }
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllProductosEquivalentes.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllProductosEquivalentes.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();  
});
