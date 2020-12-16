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
	var controller = $contextpath.val()+"/controllers/invformulas";
    
        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_formulas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de formulas');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
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
                $busqueda_descripcion.val(' ');
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
	
	
	
	
	//buscador de productos                                  sku_buscar
	busca_productos     = function(buscador_producto,sku_buscar){
			$(this).modalPanel_Buscaproducto();
			var $dialogoc =  $('#forma-buscaproducto-window');
			$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());

			$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -200});
						
			var $prod_tipo               = $('#forma-formulas-window').find('input[name=select_prodtipo]');
			var $unidad                  = $('#forma-formulas-window').find('input[name=select_unidad]');
			
			var $tabla_resultados = $('#forma-buscaproducto-window').find('#tabla_resultado');
			var $codigo_Productomaster          = $('#forma-formulas-window').find('input[name=codigo_master]');
			var $descripcion_Productomaster     = $('#forma-formulas-window').find('input[name=descripcion_master]');
			var $codigo_producto_minigrid       = $('#forma-formulas-window').find('input[name=codigo_producto_minigrid]');
			var $descripcion_producto_minigrid  = $('#forma-formulas-window').find('input[name=descr_producto_minigrid]');
			var $codigo_Productosaliente        = $('#forma-formulas-window').find('input[name=codigo_producto_saliente]');
			var $descripcion_Productosaliente   = $('#forma-formulas-window').find('input[name=descr_producto_saliente]');
			
			var $id_producto_master             = $('#forma-formulas-window').find('input[name=id_prod_master]');
			var $id_producto_entrante           = $('#forma-formulas-window').find('input[name=id_prod_entrante]');
			var $id_producto_saliente           = $('#forma-formulas-window').find('input[name=id_prod_saliente]');

			
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

			//buscar todos los tipos de productos
			
			var input_json_tipos =document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
			$arreglo = { iu:usuario_codificado, identificador:usuario_codificado,buscador_producto:buscador_producto};
			
			$.post(input_json_tipos,$arreglo,function(data){
				//Llena el select tipos de productos en el buscador
				$select_tipo_producto.children().remove();
				//var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
				var prod_tipos_html = '';
				$.each(data['prodTipos'],function(entryIndex,pt){
						prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
				});
				$select_tipo_producto.append(prod_tipos_html);
			});

			//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
			//$campo_sku.val(sku_buscar);

			//click buscar productos
			$buscar_plugin_producto.click(function(event){
					//event.preventDefault();
					$tabla_resultados.children().remove();
					var restful_json_service = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos_ingredientes.json';
					arreglo_parametros = {
							'sku':$campo_sku.val(),
							'tipo':$select_tipo_producto.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':usuario_codificado
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
							
							var id_prod=$(this).find('#id_prod_buscador').val();
							var codigo=$(this).find('span.sku_prod_buscador').html();
							var descripcion=$(this).find('span.titulo_prod_buscador').html();
							 var producto=$(this).find('span.tipo_prod_buscador').html();
							 var unidad=$(this).find('span.utitulo').html();
							
							
							
							//asignar a los campos correspondientes el sku y y descripcion
							//$('#forma-formulas-window').find('input[name=sku_producto]').val($(this).find('#id_prod_buscador').val());
							//$('#forma-formulas-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
							//$('#forma-formulas-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
							//elimina la ventana de busqueda
							var remove = function() {$(this).remove();};
							$('#forma-buscaproducto-overlay').fadeOut(remove);
							//asignar el enfoque al campo sku del producto
							$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();

							
							if(buscador_producto ==1){
								//colocando los datos elegidos en el pluguin
								$id_producto_master.val(id_prod);
								$codigo_Productomaster.val(codigo);
								$descripcion_Productomaster.val(descripcion);
								$prod_tipo.val(producto);
								$unidad.val(unidad);
								//fin del primer plugin
							}
							if(buscador_producto ==2){
							   //guardando los datos del pluguin si es que ligio buscar elementos para el grid
								// $codigo_producto_minigrid.val($(this).find('span.sku_prod_buscador').html());
							   // $descripcion_producto_minigrid.val($(this).find('span.titulo_prod_buscador').html());
							   //
								$id_producto_entrante.val(id_prod);
								$codigo_producto_minigrid.val(codigo);
								$descripcion_producto_minigrid.val(descripcion);
								
							}
							
							if(buscador_producto ==3){
							   //guardando los datos del pluguin si es que ligio buscar elementos para el grid
								// $codigo_Productosaliente.val($(this).find('span.sku_prod_buscador').html());
								//$descripcion_Productosaliente.val($(this).find('span.titulo_prod_buscador').html());
							   // 
								$id_producto_saliente.val(id_prod);
								$codigo_Productosaliente.val(codigo);
								$descripcion_Productosaliente.val(descripcion);
							}
								
						});
						
					});//termina llamada json
			});

			$cancelar_plugin_busca_producto.click(function(event){
				//event.preventDefault();
				var remove = function() {$(this).remove();};
				$('#forma-buscaproducto-overlay').fadeOut(remove);
			});
	}//termina buscador de productos
	
	
	//nuevas formulas
	$new_formulas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_formulas();   //llamada al plug in 
		
		var form_to_show = 'formaFormulas';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-formulas-window').css({"margin-left": -390, 	"margin-top": -270});
		$forma_selected.prependTo('#forma-formulas-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		//tabs_li_funxionalidad();
		
		var $campo_id                       = $('#forma-formulas-window').find('input[name=identificador]');
		//var $id_producto_master             = $('#forma-formulas-window').find('input[name=id_prod_master]');
		//var $id_producto_entrante           = $('#forma-formulas-window').find('input[name=id_prod_entrante]');
		var $id_producto_saliente           = $('#forma-formulas-window').find('input[name=id_prod_saliente]');
		//var $prod_tipo               = $('#forma-formulas-window').find('input[name=select_prodtipo]');
		//var $unidad                  = $('#forma-formulas-window').find('input[name=select_unidad]');
		var $codigo_Productomaster          = $('#forma-formulas-window').find('input[name=codigo_master]');
		var $descripcion_Productomaster     = $('#forma-formulas-window').find('input[name=descripcion_master]');
		var $href_buscar_Productomaster     = $('#forma-formulas-window').find('a[href*=buscar_productomaster]');
		var $numero_pasos                   = $('#forma-formulas-window').find('input[name=numero_pasos]');                                                            
		var $paso_actual                    = $('#forma-formulas-window').find('input[name=paso_actual]');
                var $cantidad_calculo              = $('#forma-formulas-window').find('input[name=cantidad_calculo]');
		var $codigo_producto_minigrid       = $('#forma-formulas-window').find('input[name=codigo_producto_minigrid]');
		var $descripcion_producto_minigrid  = $('#forma-formulas-window').find('input[name=descr_producto_minigrid]');
		var $buscar_producto_para_minigrid  = $('#forma-formulas-window').find('a[href*=busca_producto_ingrediente]');
		var $agregar_producto_minigrid      = $('#forma-formulas-window').find('a[href*=agregar_producto_minigrid]');
		
		var $codigo_Productosaliente        = $('#forma-formulas-window').find('input[name=codigo_producto_saliente]');
		var $descripcion_Productosaliente   = $('#forma-formulas-window').find('input[name=descr_producto_saliente]');
		var $href_buscar_Productosaliente   = $('#forma-formulas-window').find('a[href*=busca_producto_saliente]');
		
		var $id_tipo_producto=0;
		var $descripcion_producto="";
		var $id_unidad=0;
		var $descripcion_unidad="";
		//botones		
		var $cerrar_plugin = $('#forma-formulas-window').find('#close');
		var $cancelar_plugin = $('#forma-formulas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-formulas-window').find('#submit');
                
                var $genera_pdf_formulas = $('#forma-formulas-window').find('#genera_pdf_formulas');
                
		$numero_pasos.change(function() {
			$numero_pasos.attr({ 'readOnly':true });
			$paso_actual.val($numero_pasos.val());
			
			$paso_actual.attr({ 'readOnly':true });
			//$paso_actual.attr("disabled", "disabled");
			//para habilitarlo   .removeAttr("disabled");
		});
                $genera_pdf_formulas.hide();
		var buscador_producto= 0;
		
		$codigo_Productomaster.attr({ 'readOnly':true });
		$descripcion_Productomaster.attr({ 'readOnly':true });
		$codigo_producto_minigrid.attr({ 'readOnly':true });
		$descripcion_producto_minigrid.attr({ 'readOnly':true });
		$codigo_Productosaliente.attr({ 'readOnly':true });
		$descripcion_Productosaliente.attr({ 'readOnly':true });
	   
		$href_buscar_Productomaster.click(function(event){
			event.preventDefault();
			buscador_producto=1;
			var sku_buscar=$codigo_Productomaster.val();
			busca_productos(buscador_producto,sku_buscar);
		});

		$agregar_producto_minigrid.click(function(event){
			event.preventDefault();
			$agrega_producto_ingrediente();
			$codigo_producto_minigrid.val("");
			$descripcion_producto_minigrid.val("");
			$cantidad_calculo.attr({ 'readOnly':true });
		});
		
		$buscar_producto_para_minigrid.click(function(event){
			event.preventDefault();
			buscador_producto=2;
			var sku_buscar=$codigo_producto_minigrid.val();
			busca_productos(buscador_producto,sku_buscar);
		});
		
		$href_buscar_Productosaliente.click(function(event){
			event.preventDefault();
			buscador_producto=3;
			var sku_buscar=$codigo_producto_minigrid.val();
			busca_productos(buscador_producto,sku_buscar);
		});

		
		$campo_id.attr({'value' : 0});
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La formula fue dada de alta con exito", 'Atencion!');
				var $grid_productos_componentes     = $('#forma-formulas-window').find('div.div_formulaciones');
				var $href_buscar_Productomaster     = $('#forma-formulas-window').find('a[href*=buscar_productomaster]');
				var $href_buscar_Productosaliente   = $('#forma-formulas-window').find('a[href*=busca_producto_saliente]');
				var $paso_actual                    = $('#forma-formulas-window').find('input[name=paso_actual]');
				var $numero_pasos                   = $('#forma-formulas-window').find('input[name=numero_pasos]');                                                            
				var $codigo_Productosaliente        = $('#forma-formulas-window').find('input[name=codigo_producto_saliente]');
				var $descripcion_Productosaliente   = $('#forma-formulas-window').find('input[name=descr_producto_saliente]');
				var $id_producto_saliente           = $('#forma-formulas-window').find('input[name=id_prod_saliente]');
                                
				var $total_porcentaje = $('#forma-formulas-window').find('input[name=total_porcentaje]');
				
				$paso_actual.attr({ 'readOnly':true });
				
				
				$grid_productos_componentes.find('#minigrid').find('tbody').children().remove();
				$descripcion_producto=$('#forma-formulas-window').find('#my-select option:selected').html();
				$href_buscar_Productomaster.hide();
				
				$descripcion_Productosaliente.val('');
				$codigo_Productosaliente.val('');
				
				$id_producto_saliente.val('');
				$total_porcentaje.val(0);
				
				$paso_actual.val(parseInt($paso_actual.val())-1);
				
				$get_datos_grid();//actualiza datos del grid
				
				if($paso_actual.val()==0){
					var remove = function() {$(this).remove();};
					$('#forma-formulas-overlay').fadeOut(remove);
				}
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-formulas-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-formulas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFormulas.json';
		$arreglo = {'id':id_to_show,
				'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
			   };
		
		$.post(input_json,$arreglo,function(entry){
			//aqui no va nada por ahora porque no hay camposse esten cargandi desde un inicio
                },"json");//termina llamada json


		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-formulas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-formulas-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
	});
	
	
        
        
        
        //agrega producto al grid
		$agrega_producto_ingrediente = function(){
				var $cantidad_calculo              = $('#forma-formulas-window').find('input[name=cantidad_calculo]');
				//var $select_prod_tipo             = $('#forma-formulas-window').find('select[name=select_prodtipo]');
				var $select_prod_tipo               = $('#forma-formulas-window').find('input[name=select_prodtipo]');
				var $select_unidad                  = $('#forma-formulas-window').find('input[name=select_unidad]');
				
				var $codigo_producto_minigrid       = $('#forma-formulas-window').find('input[name=codigo_producto_minigrid]');
				var $grid_productos_componentes     = $('#forma-formulas-window').find('div.div_formulaciones');
				
				var $total_porcentaje = $('#forma-formulas-window').find('input[name=total_porcentaje]');
				
				var $total_tr = $('#forma-formulas-window').find('input[name=total_tr]');
                                var $porcentaje_temporal = $('#forma-formulas-window').find('input[name=porcentaje_temporal]');
                                var $submit_actualizar = $('#forma-formulas-window').find('#submit');
                                
                                if($cantidad_calculo.val() !=0){
				if($codigo_producto_minigrid.val() != null && $codigo_producto_minigrid.val() != ''){
					var encontrado=0;
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_busca_sku_prod.json';
                                        $arreglo = {	'sku':$codigo_producto_minigrid.val(),
                                                        //'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                                                        'iu': usuario_codificado
                                                    }

                                        $.post(input_json,$arreglo,function(prod){
                                                        var res=0;
                                                        if(prod['Sku'][0] != null){
                                                                var trCount = $("tbody > tr", $grid_productos_componentes).size();
                                                                trCount++;
                                                                trr = '<tr>';
                                                                                trr += '<td>';
                                                                                        trr += '<a href=#>Eliminar</a>';
                                                                                        trr += '<input type="hidden" id="delete" name="eliminar" value="'+prod['Sku'][0]['id']+'">';
                                                                                trr += '</td>';
                                                                                trr += '<td id="sku">'+prod['Sku'][0]['sku']+'</td>';
                                                                                trr += '<td>'+prod['Sku'][0]['descripcion']+'</td>';
                                                                                trr += '<td>';
                                                                                        trr += '<INPUT TYPE="text" id="porcentaje'+trCount+'" name="cantidad" style="width:100px;height:20px">';
                                                                                                //trr += '<input type="hidden" id="dec" name="decimales" value="'+ prod['Sku'][0]['decimales']+'">';
                                                                                trr += '</td>';
                                                                trr += '</tr>';
                                                                var tabla = $grid_productos_componentes.find('tbody');
                                                                tabla.prepend(trr);

                                                                tabla.find('a').bind('click',function(event){
                                                                        var total_porcentaje=0;
                                                                        if(parseInt($(this).parent().find('#delete').val()) != 0){
                                                                                //alert("Alert1: "+total_porcentaje+"  Campo total:"+$total_porcentaje.val());
                                                                                total_porcentaje = parseFloat($total_porcentaje.val())-parseFloat($(this).parent().parent().find('#porcentaje').val());
                                                                                $total_porcentaje.val(total_porcentaje);
                                                                                //alert("Alert2: "+total_porcentaje+"  Campo total:"+$total_porcentaje.val());
                                                                                //$(this).parent().find('#delete').val(0);
                                                                                //$(this).parent().parent().hide();
                                                                                $(this).parent().parent().remove();
                                                                        }
                                                                });


                                                                        tabla.find('#porcentaje'+trCount).focus(function(){
                                                                            if($(this).val() !=''){
                                                                                $porcentaje_temporal.val($(this).val()); 
                                                                            }
                                                                            //alert('Este es e porcentaje temporal'+$porcentaje_temporal.val());
                                                                        }); 


                                                                        //calcula porcentaje al perder enfoque 
                                                                        tabla.find('#porcentaje'+trCount).blur(function(){
																			var total=0
																			var patron=/^([0-9]){1,12}[.]?[0-9]*$/
																			
																			if(patron.test($(this).val())){
																				var calculo_porcentaje=0;

																				if(parseFloat($porcentaje_temporal.val()) != parseFloat($(this).val())){
																					calculo_porcentaje=(parseFloat($(this).val()) / parseFloat($cantidad_calculo.val())) *100;
																					$(this).val(parseFloat(calculo_porcentaje).toFixed(4));
																				}
																				
																				$grid_productos_componentes.find('tbody > tr').each(function (index){
																					if(parseInt($(this).find('#delete').val())!=0){
																						total = parseFloat(total) + parseFloat(calculo_porcentaje);
																					}
																				});
																				$total_porcentaje.val(total);

																			}else{
																				jAlert("La cantidad debe tener un 0, ejemplo: 0.3, 456.5, 654.9",'! Atencion');
																				$(this).val(0.0)
																			}
                                                                        });
                                                        }else{
                                                                jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                                                        }
                                        },"json");
//					
				}else{
					jAlert("Ingrese un C&oacute;digo de producto valido", 'Atencion!');
				}
				}else{
					jAlert("Ingrese una cantidad diferente de cero", 'Atencion!');
				}
				
				
				
		   
			$submit_actualizar.bind('click',function(){
				//event.preventDefault();
				var trCount = $("tbody > tr", $grid_productos_componentes).size();
				$total_tr.val(trCount);
				if(parseInt($select_prod_tipo.val())==1  || parseInt($select_prod_tipo.val())==2){
					if(trCount > 0){

					}else{
						jAlert("Es necesario Agregar a la lista los productos Entrantes.", 'Atencion!');
						return false;
					}
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
			jConfirm('Realmente desea eliminar la formula seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La formula fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Formula no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaFormulas';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_formulas();
			$('#forma-formulas-window').css({"margin-left": -390, 	"margin-top": -270});
			
			$forma_selected.prependTo('#forma-formulas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			//$tabs_li_funxionalidad();
			
			var $campo_id                       = $('#forma-formulas-window').find('input[name=identificador]');
			var $prod_tipo                      = $('#forma-formulas-window').find('input[name=select_prodtipo]');
                        var $unidad                         = $('#forma-formulas-window').find('input[name=select_unidad]');
			
			var $codigo_Productomaster          = $('#forma-formulas-window').find('input[name=codigo_master]');
			var $descripcion_Productomaster     = $('#forma-formulas-window').find('input[name=descripcion_master]');
			var $hrer_buscar_Productomaster     = $('#forma-formulas-window').find('a[href*=buscar_productomaster]').hide();
			var $numero_pasos                   = $('#forma-formulas-window').find('input[name=numero_pasos]');                                                            
			var $paso_actual                    = $('#forma-formulas-window').find('input[name=paso_actual]');
			var $cantidad_calculo               = $('#forma-formulas-window').find('input[name=cantidad_calculo]');
                        
			var $codigo_producto_minigrid        = $('#forma-formulas-window').find('input[name=codigo_producto_minigrid]');
			var $buscar_producto_para_minigrid    = $('#forma-formulas-window').find('a[href*=busca_producto_ingrediente]');
			var $descripcion_producto_minigrid   = $('#forma-formulas-window').find('input[name=descr_producto_minigrid]');
			var $agregar_producto_minigrid      = $('#forma-formulas-window').find('a[href*=agregar_producto_minigrid]');
			
			var $id_producto_master             = $('#forma-formulas-window').find('input[name=id_prod_master]');
			var $id_producto_entrante           = $('#forma-formulas-window').find('input[name=id_prod_entrante]');
			var $id_producto_saliente           = $('#forma-formulas-window').find('input[name=id_prod_saliente]');
			
			var $codigo_Productosaliente        = $('#forma-formulas-window').find('input[name=codigo_producto_saliente]');
			var $descripcion_Productosaliente   = $('#forma-formulas-window').find('input[name=descr_producto_saliente]');
			var $href_buscar_Productosaliente   = $('#forma-formulas-window').find('a[href*=busca_producto_saliente]');
                            
			var $grid_productos_componentes     = $('#forma-formulas-window').find('div.div_formulaciones');
                        
                        var $porcentaje_temporal = $('#forma-formulas-window').find('input[name=porcentaje_temporal]');
			
                        var $total_tr = $('#forma-formulas-window').find('input[name=total_tr]');
                        var $total_porcentaje = $('#forma-formulas-window').find('input[name=total_porcentaje]');
                        var $cantidad =0;
                        
                        var $genera_pdf_formulas = $('#forma-formulas-window').find('#genera_pdf_formulas');
                        
			var $cerrar_plugin = $('#forma-formulas-window').find('#close');
			var $cancelar_plugin = $('#forma-formulas-window').find('#boton_cancelar');
			
			var $submit_actualizar = $('#forma-formulas-window').find('#submit');
                        $cantidad_calculo.change(function() {
                            $cantidad_calculo.attr({ 'readOnly':true });
                        });
			if(accion_mode == 'edit'){
                            
				//aqui es el post que envia los datos a getForulas.json
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFormulas.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-formulas-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-formulas-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-formulas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				
				
				var asigna_focus_input = function($campo_input,tabla,trCount,$cantidad){
					tabla.find('a').bind('click',function(event){
						var total_porcentaje=0;
						if(parseInt($(this).parent().find('#delete').val()) != 0){
							total_porcentaje = parseFloat($total_porcentaje.val())-parseFloat($(this).parent().parent().find('#porcentaje').val());
							$total_porcentaje.val(total_porcentaje);
							$(this).parent().find('#delete').val(0);
							//$(this).parent().parent().hide();
							$(this).parent().parent().remove();
						}
					});					
					$campo_input.focus(function(){
						if($(this).val() !=''){
							$porcentaje_temporal.val($(this).val());
						}
						var tmp=$porcentaje_temporal.val($(this).val());
				   }); 
					
					
					//calcula porcentaje al perder enfoque 
					$campo_input.blur(function(){
					   $cantidad= $cantidad_calculo.val();
							var total=0
							var patron=/^([0-9]){1,12}[.]?[0-9]*$/
							
							if(patron.test($(this).val())){
								//alert("on blur"+$porcentaje_temporal.val());
								var calculo_porcentaje=0;
								if( $cantidad ==0){
									jAlert("Ingrese una cantidad para el calculo",'Atencion');
									$(this).val($porcentaje_temporal.val());
									//$(this).val(parseFloat(calculo_porcentaje).toFixed(2));
								}else{
									if(parseFloat($porcentaje_temporal.val()) != parseFloat($(this).val()) ){
										calculo_porcentaje=(parseFloat($(this).val()) / parseFloat($cantidad_calculo.val())) *100;
										$(this).val(parseFloat(calculo_porcentaje).toFixed(4));
									}
								}
							}else{
								jAlert("La cantidad debe tener un 0, ejemplo: 0.3, 456.5, 654.9",'! Atencion');
								$(this).val(0.0)
							}
						});
					}
					
					//aqui se cargan los campos al editar
					$.post(input_json,$arreglo,function(entry){
							$codigo_Productomaster.attr({ 'readOnly':true });
							$descripcion_Productomaster.attr({ 'readOnly':true });
							$codigo_producto_minigrid.attr({ 'readOnly':true });
							$descripcion_producto_minigrid.attr({ 'readOnly':true });
							$codigo_Productosaliente.attr({ 'readOnly':true });
							$descripcion_Productosaliente.attr({ 'readOnly':true });
							$paso_actual.attr({ 'readOnly':true });
							$numero_pasos.attr({ 'readOnly':true });
							
							$campo_id.val (entry['Formulas']['0']['id_formula']);
							$prod_tipo.val(entry['Formulas']['0']['tipo_producto']);
							$unidad.val(entry['Formulas']['0']['unidad']);
							$codigo_Productomaster.val(entry['Formulas']['0']['codigo']);
							$descripcion_Productomaster.val(entry['Formulas']['0']['descripcion']);
							//$hrer_buscar_Productomaster.hide();
							$numero_pasos.val(entry['Formulas']['0']['numero_pasos']);
							$paso_actual.val(entry['Formulas']['0']['nivel_paso_actual']);
							
							$cantidad=$cantidad_calculo.val();
							$codigo_producto_minigrid.val();
							//$buscar_producto.val();
							$buscar_producto_para_minigrid.click(function(event){
									event.preventDefault();
									buscador_producto=2;
									var sku_buscar=$codigo_producto_minigrid.val();
									busca_productos(buscador_producto,sku_buscar);
							});
							$descripcion_producto_minigrid.val;

							$id_producto_master.val(entry['Formulas']['0']['id_producto_master']);
							$id_producto_entrante.val();
							$id_producto_saliente.val();

							var contador=0;

							//Formulas_DatosMinigrid.
							$.each(entry['Formulas_DatosMinigrid'],function(entryIndex,elemento){
								var trCount = $("tbody > tr", $grid_productos_componentes).size();

									contador=contador+1;
								   
									trr = '<tr>';
										trr += '<td>';
										trr += '<a href=#>Eliminar</a>';
										trr += '<input type="hidden" id="delete" name="eliminar" value="'+elemento['producto_elemento_id']+'">';
										trr += '</td>';
										trr += '<td id="sku">'+elemento['codigo']+'</td>';
										trr += '<td         >'+elemento['descripcion']+'</td>';
										trr += '<td>';                                                                                                                            
										trr += '<INPUT TYPE="text" id="porcentaje'+trCount+'" name="cantidad" style="width:100px;height:20px" value="'+parseFloat(elemento['cantidad']).toFixed(4) +'">';
										trr += '</td>';
									trr += '</tr>';
							var tabla = $grid_productos_componentes.find('tbody');
							tabla.prepend(trr);
							
							var $input_cantidad=$grid_productos_componentes.find('tbody').find('#porcentaje'+trCount);
							
							asigna_focus_input ($input_cantidad,tabla,trCount,$cantidad);
						   
						  trCount++;
						});
						
						
						$id_producto_saliente.val(entry['Formulas_DatosProductoSaliente']['0']['inv_prod_id'])//entry['Formulas_DatosProductoSaliente']['0']['inv_prod_id']
						$codigo_Productosaliente.val(entry['Formulas_DatosProductoSaliente']['0']['codigo']);
						$descripcion_Productosaliente.val(entry['Formulas_DatosProductoSaliente']['0']['descripcion']);
						//$hrer_buscar_Productosaliente.val();
						$href_buscar_Productosaliente.click(function(event){
							event.preventDefault();
							buscador_producto=3;
							var sku_buscar=$codigo_producto_minigrid.val();
							busca_productos(buscador_producto,sku_buscar);
						});
						
						$agregar_producto_minigrid.click(function(event){
							event.preventDefault();
							$agrega_producto_ingrediente();
							$codigo_producto_minigrid.val("");
							$descripcion_producto_minigrid.val("");
							
						});
						
				 },"json");//termina llamada json
				
				
				$genera_pdf_formulas.click(function(event){
					event.preventDefault();
					iu=$('#lienzo_recalculable').find('input[name=iu]').val();
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
                                        var stock = $('#forma-formulas-window').find('input[name=stock]').is(':checked');
                                        
                                        var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfFormula/'+$id_producto_master.val()+'/'+stock+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-formulas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-formulas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
			$submit_actualizar.bind('click',function(){
			   var trCount = $("tbody > tr", $grid_productos_componentes).size();
				$total_tr.val(trCount);
				
				if(trCount > 0){

				}else{
					jAlert("Es necesario Agregar a la lista los productos Entrantes.", 'Atencion!');
					return false;
				}
			});
		 }
	}
	}
    
    
    
    
    
    
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllFormulas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllFormulas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();  
});
