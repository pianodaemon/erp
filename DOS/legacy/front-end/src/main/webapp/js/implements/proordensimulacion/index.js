$(function() {
        //jQuery.noConflict();
        
        var config =  {
            empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
            sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
            tituloApp: 'Preorden de Producci&oacute;n' , 
            contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),

            userName : $('#lienzo_recalculable').find('input[name=user]').val(),
            ui : $('#lienzo_recalculable').find('input[name=iu]').val(),

            getUrlForGetAndPost : function(){
                var url = document.location.protocol + '//' + document.location.host + this.getController();
                return url;
            },
            getEmp: function(){
                return this.empresa;
            },
            getSuc: function(){
                return this.sucursal;
            },
            getUserName: function(){
                return this.userName;
            },
            getUi: function(){
                return this.ui;
            },
            getTituloApp: function(){
                return this.tituloApp;
            },
            getController: function(){
                return this.contextpath + "/controllers/proordensimulacion";
                //  return this.controller;
            }
        };
        
        
        String.prototype.toCharCode = function(){
            var str = this.split(''), len = str.length, work = new Array(len);
            for (var i = 0; i < len; ++i){
                work[i] = this.charCodeAt(i);
            }
            return work.join(',');
        };
        
        var $cadena_especificaciones = "";
        var $cadena_procedimientos = "";
        
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
	var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/proordensimulacion";
    
    //Barra para las acciones
    $('#barra_acciones').hide();
	/*
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_orden = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
        */
       
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Simulaci&oacute;n de Producci&oacute;n');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	//Codigo para dejar sil buscador de forma permanente
	TriggerClickVisializaBuscador=1;
	var height2 = $('#cuerpo').css('height');
	//alert('height2: '+height2);
	
	alto = parseInt(height2)-220;
	var pix_alto=alto+'px';
	
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	$('#barra_buscador').animate({height: '60px'}, 500);
	$('#cuerpo').css({'height': pix_alto});
	//Termina codigo para buscador vualizarlo permanente
        
        
	var $cadena_busqueda = "";
	var $buscador_tipoorden_busqueda = $('#barra_buscador').find('.tabla_buscador').find('select[name=buscador_tipoorden]');
	var $busqueda_folio_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_descripcion_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	var $formula_id = $('#barra_buscador').find('.tabla_buscador').find('input[name=formula_id]');
	var $producto_id = $('#barra_buscador').find('.tabla_buscador').find('input[name=producto_id]');
	
	var $buscar_producto = $('#barra_buscador').find('.tabla_buscador').find('a[href*=busca_productos]');
	
	var array_productos_proceso = new Array(); //este arreglo carga la maquinas
	var array_instrumentos = new Array(); //este arreglo carga la maquinas
        
	var $simular = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Simular]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_proordentipos.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
		//Llena el select tipos de productos en el buscador
		$buscador_tipoorden_busqueda.children().remove();
		var prod_tipos_html = '<option value="0" selected="yes">[-- --]</option>';
		$.each(data['ordenTipos'],function(entryIndex,pt){
			prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
		});
		$buscador_tipoorden_busqueda.append(prod_tipos_html);
	});
        
        
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		
		var signo_separador = "=";
		valor_retorno += "tipo_orden" + signo_separador + $buscador_tipoorden_busqueda.val() + "|";
		valor_retorno += "folio_busqueda" + signo_separador + $busqueda_folio_busqueda.val() + "|";
		valor_retorno += "descripcion_busqueda" + signo_separador + $busqueda_descripcion_busqueda.val() + "|";
		
		return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_folio_busqueda.val('');
		$busqueda_descripcion_busqueda.val('');
		$formula_id.val('');
		$producto_id.val('');
	});

	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-proordensimulacion-window').find('select[name=prodtipo]');
		$('#forma-proordensimulacion-window').find('#submit').mouseover(function(){
			$('#forma-proordensimulacion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-proordensimulacion-window').find('#submit').mouseout(function(){
			$('#forma-proordensimulacion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		
		$('#forma-proordensimulacion-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-proordensimulacion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-proordensimulacion-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-proordensimulacion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-proordensimulacion-window').find('#close').mouseover(function(){
			$('#forma-proordensimulacion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-proordensimulacion-window').find('#close').mouseout(function(){
			$('#forma-proordensimulacion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-proordensimulacion-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-proordensimulacion-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-proordensimulacion-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-proordensimulacion-window').find("ul.pestanas li").click(function() {
			$('#forma-proordensimulacion-window').find(".contenidoPes").hide();
			$('#forma-proordensimulacion-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-proordensimulacion-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
        
	$add_suboprocesos = function(id_reg,producto_id, persona,equipo,eq_adicional,cantidad,subprocesos, unidad, unidad_id, densidad){
            contador = 0;
            $.each(subprocesos,function(entryIndex,subproceso){
                if(contador != 0){
                    
                    var $tabla_productos_orden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                    var trCount = $("tr", $tabla_productos_orden).size();
                    trCount++;
                    
                    trr = '<tr>';
                    if(id_reg == "0"){
                        
                            trr += '<td width="65"  align="center" colspan="3" class="grid1">';
                                trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                                trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                                trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+producto_id +'">';
                                trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                            trr += '</td>';
                            
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="subproceso" value="'+subproceso['pro_subprocesos_titulo']+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                                trr += '<input type="hidden" name="subproceso_id" value="'+subproceso['pro_subprocesos_id']+'" >';
                                trr += '<input type="hidden" name="pro_subproceso_prod_id" value="'+subproceso['pro_subproceso_prod_id']+'" >';
                            trr += '</td>';
                            
                            trr += '<td width="100" class="grid1">';
                                //trr += '<a href="#remov_persona" id="remov_persona'+trCount+'">-</a>';
                                trr += '<input type="text" name="persona" id="persona'+trCount+'" value="'+persona+'"  style="width:70px;">';
                                //trr += '<a href="#add_persona" id="add_persona'+trCount+'">+</a>';
                            trr += '</td>';
                            
                            
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="equipo" id="equipo'+trCount+'" value="'+equipo+'"  style="width:70px;">';
                            trr += '</td>';
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="eq_adicional" id="eq_adicional'+trCount+'" value="'+eq_adicional+'"  style="width:70px;">';
                            trr += '</td>';
                            trr += '<td width="100" class="grid1"><input type="text" id="cantidad'+trCount+'" name="cantidad" value="'+cantidad+'"  style="width:70px;"></td>';
                        
                    }else{
                        
                            trr += '<td width="65"  align="center" colspan="3" class="grid1">';
                                trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                                trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                                trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+producto_id +'">';
                                trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                            trr += '</td>';
                            
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="subproceso" value="'+subproceso['pro_subprocesos_titulo']+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                                trr += '<input type="hidden" name="subproceso_id" value="'+subproceso['pro_subprocesos_id']+'" >';
                                trr += '<input type="hidden" name="pro_subproceso_prod_id" value="'+subproceso['pro_subproceso_prod_id']+'" >';
                            trr += '</td>';
                            
                            trr += '<td width="100" class="grid1">';
                                //trr += '<a href="#remov_persona" id="remov_persona'+trCount+'">-</a>';
                                trr += '<input type="text" name="persona" id="persona'+trCount+'" value="'+persona+'"  style="width:70px;">';
                                //trr += '<a href="#add_persona" id="add_persona'+trCount+'">+</a>';
                            trr += '</td>';
                            
                            
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="equipo" id="equipo'+trCount+'" value="'+equipo+'"  style="width:70px;">';
                            trr += '</td>';
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="eq_adicional" id="eq_adicional'+trCount+'" value="'+eq_adicional+'"  style="width:70px;">';
                            trr += '</td>';
                            trr += '<td width="100" class="grid1"><input type="text" id="cantidad'+trCount+'" name="cantidad" value="'+cantidad+'"  style="width:70px;"></td>';
                    }
                        trr += '<td width="80" class="grid1">';
                        trr += '<select id="unidad_default'+trCount+'" name="unidad_default" >';
                        unidad = unidad.toUpperCase();
                        if(/^KILO*|KILOGRAMO$/.test(unidad)){
                            trr += '<option value="'+unidad_id+'" name="unidad_id" selected="yes">'+unidad+'</option>';
                            trr += '<option value="0">LITRO</option>';
                            //, unidad_id, densidad
                        }else{
                            trr += '<option value="'+unidad_id+'" selected="yes">'+unidad+'</option>';
                            trr += '<option value="0">KILO</option>';
                        }
                        trr += '</select>';
                        trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                        trr += '<input type="hidden" name="unidad_id" value="'+unidad_id+'" >';
                        trr += '</td>';
                        
                    trr += '</tr>';
                    
                    $tabla_productos_orden.append(trr);
                    
                    
                    $aplicar_evento_keypress($tabla_productos_orden.find('#cantidad'+ trCount));
                    
                    //se pone todo en kilos, que es lo que debe de ser por defecto
                    $tmp_parent = $tabla_productos_orden.find('#unidad_default'+ trCount).parent().parent();
                    //$tmp_parent = $(this).parent().parent();
                    densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                    text_selected = $tmp_parent.find('select option:selected').text();
                    cantidad_default = $tmp_parent.find('input[name=cantidad]');
                    $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'inicio');
                    $event_changue_input_cantidad(cantidad_default);
                    
                    //para que al cambiar de kilos a litros o de litros a kilos, realize los calculos de acuerdp a la densidad
                    $tabla_productos_orden.find('#unidad_default'+ trCount).change(function() {
                        $tmp_parent = $(this).parent().parent();
                        densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                        text_selected = $tmp_parent.find('select option:selected').text();
                        cantidad_default = $tmp_parent.find('input[name=cantidad]');
                        $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'grid');
                    });
                    
                    //AL CAMBIAR UNA CANTIDAD EN UN SUBPROCESO, LO CAMBIA EN TODOS LOS DEMAS SUBPROCESOS DEL PRODUCTO
                    $tabla_productos_orden.find('#cantidad'+ trCount).blur(function() {
                        cantidad_tr = $(this).val();
                        inv_prod_id_tr = $(this).parent().parent().find('input[name=inv_prod_id]').val();
                        if( ($(this).val() != ' ') && ($(this).val() != '') && ($(this).val() != null ) ){
                            $tabla_productos_orden.find('tr').each(function(){
                                inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                                if(inv_prod_id_tr == inv_prod_id_tmp){
                                    $(this).find('input[name=cantidad]').val(cantidad_tr);
                                }
                            });
                        }
                    });
                    
                    $tabla_productos_orden.find('#remov_persona'+ trCount).bind('click',function(event){
                        //alert($(this).parent().html());
                    });
                    
                    $tabla_productos_orden.find('#add_persona'+ trCount).bind('click',function(event){
                        //alert($(this).parent().html());
                    });
                    
                    //para el autocomplete
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_operarios.json';
                    $autocomplete_input($tabla_productos_orden.find('#persona'+trCount+''), input_json);
                    
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipo.json';
                    $autocomplete_input($tabla_productos_orden.find('#equipo'+trCount+''), input_json);
                    
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipoadicional.json';
                    $autocomplete_input($tabla_productos_orden.find('#eq_adicional'+trCount+''), input_json);
                    
                    /*
                    $tabla_productos_orden.find('a[href^=eliminar'+ trCount+']').bind('click',function(event){
                        event.preventDefault();
                        if(parseInt($(this).parent().find('#delete').val()) != 0){
                            $(this).parent().find('#delete').val(0);
                            $(this).parent().parent().hide();
                        }
                    });
                    */
                    
                }
                contador++;
            });
        }
	
        
	//buscador de productos
	$busca_productos = function(tipo_busqueda, tr_click ){
		sku_buscar = "";
                
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -200});
		
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
		
		//buscar todos los tipos de productos
		var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_tipos,$arreglo,function(data){
                    
			//Llena el select tipos de productos en el buscador
			$select_tipo_producto.children().remove();
			//<option value="0" selected="yes">[--Seleccionar Tipo--]</option>
			var prod_tipos_html = '';
                    
			$.each(data['prodTipos'],function(entryIndex,pt){
				//para productos para tipo de orden stock
				if(tipo_busqueda == 2){
					if(pt['id'] == 2 || pt['id'] == 1 ){
						prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
					}
				}
				
				//para productos para tipo de orden laboratorio
				if(tipo_busqueda == 3){
					if(pt['id'] == 8 ){
						if(pt['id'] == 8){
							prod_tipos_html += '<option value="' + pt['id'] + '" selected="yes">' + pt['titulo'] + '</option>';
						}else{
							prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
						}
					}
				}
				
				//para productos de las formulas en recuperacion
				if(tipo_busqueda == 4){
					if(pt['id'] == 2 || pt['id'] == 1  || pt['id'] == 7 || pt['id'] == 8 ){
						prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
					}
				}
				
			});
			$select_tipo_producto.append(prod_tipos_html);

			$campo_sku.val(sku_buscar);
		
			//click buscar productos
			$buscar_plugin_producto.click(function(event){
				event.preventDefault();
							
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos.json';
				$arreglo = {'sku':$campo_sku.val(),
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
								trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
								trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
							trr += '</td>';
							trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
							trr += '<td width="90"><span class="unidad_prod_buscador">'+producto['unidad']+'</span></td>';
							trr += '<td width="90"><span class="tipo_prod_buscador">'+producto['tipo']+'</span></td>';
						trr += '</tr>';
						$tabla_resultados.append(trr);
					});
									
					$colorea_tr_grid($tabla_resultados);
					
									
					//seleccionar un producto del grid de resultados
					$tabla_resultados.find('tr').click(function(){
						var id_prod=$(this).find('#id_prod_buscador').val();
						var codigo=$(this).find('span.sku_prod_buscador').html();
						var descripcion=$(this).find('span.titulo_prod_buscador').html();
						var producto=$(this).find('span.tipo_prod_buscador').html();
						var unidad=$(this).find('span.unidad_prod_buscador').html();
						
						//buscador para los pedidos de tipo, stock y laboratorio
						if(tipo_busqueda == 2 || tipo_busqueda == 3){
							//asignar a los campos correspondientes el sku y y descripcion
							$('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]').val(codigo);
							$('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]').val(descripcion);
							$('#barra_buscador').find('.tabla_buscador').find('input[name=producto_id]').val(id_prod);
						}
						
						//buscador para poder agregar productos para recuperacion
						if( tipo_busqueda == 4){
							/*
							tr = tr_click.parent().parent().prev();
							var trCount = $("tr", tr.parent().parent()).size();
							
							id_reg_parent = tr.find('#id_reg_parent');//id de el registro padre
							inv_prod_id_elemento = tr.find('#inv_prod_id_elemento');//id de el producto de que se produce en ese subproceso
							//id_prod id de el producto que se agrega
							//$id_tabla---Esto queda pendiente, no se que pèdo, no me acuerdo
							//$grid_parent---checar que tiene grid
							inv_osal_id = 0;//id de la orden de salida
							subproceso_id = tr.find('#subproceso_id');//id de el subproceso
							id_reg_det = 0;//id d eele registro de el subproceso
							
							$grid_parent = tr.parent().parent();
							$posicion = $grid_parent.find('input[name=posicion]');
							$id_tabla = '#detalle_por_prod'+inv_prod_id_elemento.val()+$posicion.val();
							
							//alert($grid_parent.parent().parent().parent().parent().html());
							
							$add_producto_eleemnto_detalle(0,inv_prod_id_elemento.val(), id_prod, codigo, descripcion, 
							0, "", $id_tabla, $grid_parent, 0, trCount, subproceso_id.val(), id_reg_parent.val(),"", 
							id_reg_det, inv_osal_id, "recuperado", 0, 0);
							*/
						}
						
						//elimina la ventana de busqueda
						var remove = function() {$(this).remove();};
						$('#forma-buscaproducto-overlay').fadeOut(remove);
						//asignar el enfoque al campo sku del producto
					});
				});
			});
		});
		
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
        
        
        
        
        
        //agrega productos a el grid de ORDEN DE PRODUCCION en estatus produccion
        //$add_grid_componente_orden(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],""       ,""    ,""          , 0);
        $add_grid_componente_simulacion = function(inv_prod_id,cantidad,densidad,sku,descripcion, titulo, existencia){
			var $tabla_productos_orden = $('#forma-proordensimulacion-window').find('#grid_productos_seleccionados');
			var trCount = $("tr", $tabla_productos_orden).size();
			trCount++;
			
			trr = '<tr >';
				trr += '<td width="100" class="grid1" align="center">';
					trr += '<input type="text" name="sku'+trCount+'" value="'+sku+'"  class="borde_oculto" readOnly="true" style="width:95px;">';
					trr += '<input type="hidden" name="densidad" id="densidad'+trCount+'" value="'+densidad+'"  class="borde_oculto" readOnly="true" style="width:95px;">';
					trr += '<input type="hidden" name="cantidad100" id="cantidad100'+trCount+'" value="'+cantidad+'"  class="borde_oculto" readOnly="true" style="width:95px;">';
				trr += '</td>';
				trr += '<td width="450" class="grid1"><input type="text" name="descripcion'+trCount+'" value="'+descripcion+'"  class="borde_oculto" readOnly="true" style="width:440px;"></td>';
				trr += '<td width="100" class="grid1">';
					trr += '<input type="text" name="unidad" name="id'+trCount+'" value="KILO"  class="borde_oculto" readOnly="true" style="width:70px;">';
				trr += '</td>';
				trr += '<td width="90" class="grid1">';
					trr += '<input type="text" name="cantidad" id="cantidad'+trCount+'" value="'+cantidad+'"  style="width:86px; text-align:right;" readOnly="true">';
				trr += '</td>';
				trr += '<td width="90" class="grid1">';
					trr += '<input type="text" name="existencia" id="existencia'+trCount+'" value="'+existencia+'"  style="width:86px; text-align:right;" readOnly="true">';
				trr += '</td>';
			trr += '</tr>';
			
			$tabla_productos_orden.append(trr);
        }
        
        
        
        
        $simular_orden_produccion = function(version, codigo, id_prod, id_formula, descripcion, cantidad_inicial, densidad){
            var id_to_show = id_prod;
            
            $(this).modalPanel_ProOrdenSumulacion();
            
            var form_to_show = 'formaProOrdenSimulacion00';
            $('#' + form_to_show).each (function(){this.reset();});
            var $forma_selected = $('#' + form_to_show).clone();
            $forma_selected.attr({id : form_to_show + id_to_show});
            
            $('#forma-proordensimulacion-window').css({"margin-left": -375, "margin-top": -230});
            
            $forma_selected.prependTo('#forma-proordensimulacion-window');
            $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
            
            $tabs_li_funxionalidad();
            
            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_productos_formula_prod.json';
            $arreglo = {'id_formula':id_formula,
                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                        };
           
            //Variables
            var $sku_tmp = $('#forma-proordensimulacion-window').find('input[name=sku_tmp]');
            var $descripcion_tmp = $('#forma-proordensimulacion-window').find('input[name=descripcion_tmp]');
            var $cantidad_tmp = $('#forma-proordensimulacion-window').find('input[name=cantidad_tmp]');
            var $densidad_tmp = $('#forma-proordensimulacion-window').find('input[name=densidad_tmp]');
            var $cantidad_litro_tmp = $('#forma-proordensimulacion-window').find('input[name=cantidad_litro_tmp]');
            var $generapdf = $('#forma-proordensimulacion-window').find('input#generapdf');
            
            $sku_tmp.val(codigo);
            $descripcion_tmp.val(descripcion);
            $cantidad_tmp.val(cantidad_inicial);
            $densidad_tmp.val(densidad);
            
            //Convertir los Kilos a Litros de acuerdo a la densidad
			if($densidad_tmp.val().trim()!=''){
				if(parseFloat($densidad_tmp.val())>0){
					$cantidad_litro_tmp.val(parseFloat($cantidad_tmp.val()) / parseFloat($densidad_tmp.val()));
				}else{
					$cantidad_litro_tmp.val(0);
				}
			}else{
				$cantidad_litro_tmp.val(0);
			}
            
            $cantidad_litro_tmp.val(parseFloat($cantidad_litro_tmp.val()).toFixed(4));
            $cantidad_tmp.val(parseFloat($cantidad_tmp.val()).toFixed(4));
            
            
            //grids detalle pedido
            var $tabla_productos_preorden = $('#forma-proordensimulacion-window').find('#grid_productos_seleccionados');
            
            var $cerrar_plugin = $('#forma-proordensimulacion-window').find('#close');
            var $cancelar_plugin = $('#forma-proordensimulacion-window').find('#boton_cancelar');
            
            
            //$.getJSON(json_string,function(entry){
            $.post(input_json,$arreglo,function(entry){
                $.each(entry['productos'],function(entryIndex,producto){
                    $add_grid_componente_simulacion(producto['inv_prod_id'], producto['cantidad'],producto['densidad'], producto['sku'], producto['descripcion'],producto['titulo'], producto['existencia']);
                });
            },"json");//termina llamada json
            
            
			//Generar PDF de la simulacion
			$generapdf.click(function(event){
				event.preventDefault();
				var cantidad_kg=0;
				var cantidad_lt=0;
				var densidad=0;
				
				if($cantidad_tmp.val().trim()!=''){
					if(parseFloat($cantidad_tmp.val())>0){
						cantidad_kg = $cantidad_tmp.val();
					}
				}
				
				if($cantidad_litro_tmp.val().trim()!=''){
					if(parseFloat($cantidad_litro_tmp.val())>0){
						cantidad_lt = $cantidad_litro_tmp.val();
					}
				}
				
				if($densidad_tmp.val().trim()!=''){
					if(parseFloat($densidad_tmp.val())>0){
						densidad = $densidad_tmp.val();
					}
				}
				
				var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
				var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfSimulaProduccion/'+$buscador_tipoorden_busqueda.val()+'/'+id_formula+'/'+cantidad_kg+'/'+cantidad_lt+'/'+densidad+'/'+iu+'/out.json';
				//alert(input_json);
				window.location.href=input_json;
			});
            
            
			//Calcular nuevas cantidades
			$recalcular_cantidades = function(){
				var $tabla_productos_orden = $('#forma-proordensimulacion-window').find('#grid_productos_seleccionados');
				var cantidad = $cantidad_tmp.val();
				var cantidad_header = parseFloat(cantidad);
				
                $tabla_productos_orden.find('tr').each(function(){
                    var cantidad100 = $(this).find('input[name=cantidad100]').val();
                    var cantidadtmp = parseFloat(cantidad100);
                    
                    if(!isNaN(cantidadtmp) && !isNaN(cantidad_header)){
                        var result = parseFloat(cantidadtmp)/100;
                        $(this).find('input[name=cantidad]').val((parseFloat(result) * parseFloat(cantidad_header)));
                    }else{
                        $(this).find('input[name=cantidad]').val(0);
                    }
                    
                    $(this).find('input[name=cantidad]').val(parseFloat($(this).find('input[name=cantidad]').val()).toFixed(4));
                });
			}
            
            
            
            $cantidad_tmp.blur(function() {
				if($(this).val().trim()==''){
					$(this).val(0);
				}
				
				$(this).val(parseFloat($(this).val()).toFixed(4));
				
				var litros=0;
				if($densidad_tmp.val().trim()!=''){
					if(parseFloat($densidad_tmp.val())>0){
						//Convertir la cantidad a Litros
						litros = parseFloat($(this).val()) / parseFloat($densidad_tmp.val());
					}
				}
				$cantidad_litro_tmp.val(parseFloat(litros).toFixed(4));
				//Llamada a la funcion que recalcula cantidades
				$recalcular_cantidades();
            });
			
			
			
            $cantidad_litro_tmp.blur(function() {
				if($(this).val().trim()!=''){
					if(parseFloat($(this).val())>0){
						$(this).val(parseFloat($(this).val()).toFixed(4));
						
						var kilos=0;
						
						if($densidad_tmp.val().trim()!=''){
							if(parseFloat($densidad_tmp.val())>0){
								//Convertir la cantidad en Litros a Kilos
								kilos = parseFloat($(this).val()) * parseFloat($densidad_tmp.val());
							}
						}
						
						$cantidad_tmp.val(parseFloat(kilos).toFixed(4));
						//Llamada a la funcion que recalcula cantidades
						$recalcular_cantidades();
					}
				}else{
					$(this).val(0);
					$(this).val(parseFloat($(this).val()).toFixed(4));
				}
            });
			
			
            $densidad_tmp.blur(function() {
				if($(this).val().trim()==''){
					$(this).val(0);
				}
				
				$(this).val(parseFloat($(this).val()).toFixed(4));
				
				var litros=0;
				if($densidad_tmp.val().trim()!=''){
					if(parseFloat($densidad_tmp.val())>0){
						litros = parseFloat($cantidad_tmp.val()) / parseFloat($densidad_tmp.val());
					}
				}
				$cantidad_litro_tmp.val(parseFloat(litros).toFixed(4));
				//Llamada a la funcion que recalcula cantidades
				$recalcular_cantidades();
            });
        
			
			$cantidad_tmp.focus(function() {
				if(parseFloat($(this).val().trim())==0){
					$(this).val('');
				}
			});
			
			$cantidad_litro_tmp.focus(function() {
				if(parseFloat($(this).val().trim())==0){
					$(this).val('');
				}
			});
			
			$densidad_tmp.focus(function() {
				if(parseFloat($(this).val().trim())==0){
					$(this).val('');
				}
			});
			
			
			$densidad_tmp.keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}		
			});
			
			$cantidad_tmp.keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}		
			});
			
			$cantidad_litro_tmp.keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}		
			});
			
			
			
            //cerrar plugin
            $cerrar_plugin.bind('click',function(){
                var remove = function() {$(this).remove();};
                $('#forma-proordensimulacion-overlay').fadeOut(remove);
            });
            
            //boton cancelar y cerrar plugin
            $cancelar_plugin.click(function(event){
                var remove = function() {$(this).remove();};
                $('#forma-proordensimulacion-overlay').fadeOut(remove);
            });
        }
        
        
        
        
        
        
        $valida_campos_simular = function(){
            $cadena_retorno = "true";
            var folio_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]').val();
            var producto_id = $('#barra_buscador').find('.tabla_buscador').find('input[name=producto_id]').val();
            
            if(folio_busqueda.trim() == ""){
                $cadena_retorno = "Ingrese un c&oacute;digo de un producto valido";
            }
            return $cadena_retorno;
        }
        
        
        
        $opbtiene_datos_producto_por_sku = function($sku, $id_formula, $version){
            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_busca_sku_prod.json';
            $arreglo = {'sku':$sku,
                            'id_formula':$id_formula,
                            'version':$version,
                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                        }
                        
            $.post(input_json,$arreglo,function(prod){
                var res=0;
                if(prod['Sku'][0] != null){
                    unidad = prod['Sku'][0]['unidad'];
                    unidad_id = prod['Sku'][0]['unidad_id'];
                    densidad = prod['Sku'][0]['densidad'];
                    formulacion_id = prod['SubProcesos'][0]['pro_estruc_id'];
                    
                    $('#forma-proordenproduccion-window').find('input[name=id_formula]').val(formulacion_id);
                    //agrega productos a el grid de formulaciones
                    $add_grid_componente_orden(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],""       ,""    ,""          , 0, prod['SubProcesos'], 1, unidad, unidad_id, densidad);
                }else{
                    jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                }
            },"json");
        }
        
        
        
        $seleccionar_version_de_formula = function(sku, tipo_orden){
			if(tipo_orden == 3){
                //limpiar_campos_grids();
				$(this).modalPanel_Formulasendesarrollo();
				var $dialogoc =  $('#forma-formulasendesarrollo-window');
				//var $dialogoc.prependTo('#forma-buscaproduct-window');
				$dialogoc.append($('div.buscador_formulasendesarrollo').find('table.formaBusqueda_formulasendesarrollo').clone());
				
				$('#forma-formulasendesarrollo-window').css({"margin-left": -200, 	"margin-top": -200});
				
				var $tabla_resultados = $('#forma-formulasendesarrollo-window').find('#tabla_resultado');
				var $cancelar_plugin_formulasendesarrollo = $('#forma-formulasendesarrollo-window').find('#cencela');
				
				$cancelar_plugin_formulasendesarrollo.mouseover(function(){
					$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
				});
				$cancelar_plugin_formulasendesarrollo.mouseout(function(){
					$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
				});
			
				//buscador de versiones de formulas
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_versiones_formulas_por_sku.json';
                $arreglo = {'sku':sku,
                                'tipo':tipo_orden,
                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                            }
                var trr = '';
                $tabla_resultados.children().remove();
                $.post(input_json,$arreglo,function(entry){
                    
                    $.each(entry['formulas'],function(entryIndex,formula){
                        trr = '<tr>';
                            trr += '<td width="100">';
                                trr += '<span class="sku_prod_buscador">'+formula['sku']+'</span>';
                                trr += '<input type="hidden" id="id_prod_buscador" value="'+formula['inv_prod_id']+'">';
                                trr += '<input type="hidden" id="id_formula_buscador" value="'+formula['id']+'">';
                                trr += '<input type="hidden" id="densidad_buscador" value="'+formula['densidad']+'">';
                            trr += '</td>';
                            trr += '<td width="330"><span class="titulo_prod_buscador">'+formula['descripcion']+'</span></td>';
                            trr += '<td width="100"><span class="version_form_buscador">'+formula['version']+'</span></td>';
                        trr += '</tr>';
                        $tabla_resultados.append(trr);
                    });
                    
                    $colorea_tr_grid($tabla_resultados);
                    
                    //seleccionar un producto del grid de resultados
                    $tabla_resultados.find('tr').click(function(){
                        var id_prod=$(this).find('#id_prod_buscador').val();
                        var id_formula=$(this).find('#id_formula_buscador').val();
                        var version=$(this).find('span.version_form_buscador').html();
                        var codigo=$(this).find('span.sku_prod_buscador').html();
                        var descripcion=$(this).find('span.titulo_prod_buscador').html();
                        var densidad=$(this).find('#densidad_buscador').val();
                        
                        $('#forma-proordenproduccion-window').find('input[name=version_formula]').val(version);
                        $('#forma-proordenproduccion-window').find('input[name=id_formula]').val(id_formula);
                        $('#forma-proordenproduccion-window').find('input[name=producto_id]').val(id_prod);
                        $('#forma-proordenproduccion-window').find('input[name=densidad_tmp]').val(densidad);
                        
                        $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]').val(codigo);
                        $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]').val(descripcion);
                        
                        $('#barra_buscador').find('.tabla_buscador').find('input[name=formula_id]').val(id_formula);
                        $('#barra_buscador').find('.tabla_buscador').find('input[name=producto_id]').val(id_prod);
                        
                        $simular_orden_produccion(version, codigo, id_prod, id_formula, descripcion, 100, densidad);
                        
                        //elimina la ventana de busqueda
                        var remove = function() {$(this).remove();};
                        $('#forma-formulasendesarrollo-overlay').fadeOut(remove);
                        //asignar el enfoque al campo sku del producto
                        
                    });
                });
                
				$cancelar_plugin_formulasendesarrollo.click(function(event){
					//event.preventDefault();
					var remove = function() {$(this).remove();};
					$('#forma-formulasendesarrollo-overlay').fadeOut(remove);
				});
            }
        }
        
        
        //Simular
		$simular.click(function(event){
			event.preventDefault();
			$cadena = $valida_campos_simular();
			if($cadena == "true"){
				//ejecuta el plugin de formulas
				sku = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]').val();
				tipo_orden = $buscador_tipoorden_busqueda.val();
				
				if(tipo_orden == 2){
					//buscador de versiones de formulas
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_versiones_formulas_por_sku.json';
					$arreglo = {'sku':sku,
									'tipo':tipo_orden,
									'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
								}
								
					var trr = '';
					$.post(input_json,$arreglo,function(entry){
						id_formula = $('#forma-proordenproduccion-window').find('input[name=id_formula]').val(0);
						producto_id = $('#forma-proordenproduccion-window').find('input[name=producto_id]').val(0);
						if(entry['formulas'][0] == null){
							jAlert("El producto "+sku+" No tiene formula, ni configuraci&oacute;n", 'Atencion!');
						}else{
							$.each(entry['formulas'],function(entryIndex,formula){
								//$('#forma-proordenproduccion-window').find('input[name=version_formula]').val(formula['version']);
								$('#forma-proordenproduccion-window').find('input[name=id_formula]').val(formula['id']);
								$('#forma-proordenproduccion-window').find('input[name=producto_id]').val(formula['inv_prod_id']);
								
								$('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]').val(formula['sku']);
								$('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]').val(formula['descripcion']);
								
								id_formula.val(formula['id']);
								producto_id.val(formula['inv_prod_id']);
								
								$simular_orden_produccion(formula['version'], formula['sku'], formula['inv_prod_id'], formula['id'], formula['descripcion'], 100, formula['densidad']);
							});
						}
					});
				}
				
				if(tipo_orden == 3){
					$seleccionar_version_de_formula(sku, tipo_orden);
				}
				
			}else{
				jAlert($cadena, 'Atencion!');
			}
		});
        
        
        
        
        
        
        
        
        

        
        
        
        
        
        //Busca productos
		$buscar_producto.click(function(event){
			event.preventDefault();
			buscador_tipoorden_busqueda = $buscador_tipoorden_busqueda.val();
			
			//para  Stock
			if(buscador_tipoorden_busqueda == 2){
				$busca_productos(2, "");
			}
			
			//para tipo labnoratorio
			if(buscador_tipoorden_busqueda == 3){
				$busca_productos(3, "");
			}
		});
        
        
        //desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
        $busqueda_folio_busqueda.keypress(function(e){
            if(e.which == 13){
                $simular.trigger('click');
                return false;
            }
        });
        
        
        
        
        
        
        
        
        
        
        
        
        /*funcion para colorear la fila en la que pasa el puntero*/
        $colorea_tr_grid = function($tabla){
            $tabla.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
            $tabla.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});
            
            $('tr:odd' , $tabla).hover(function () {
                $(this).find('td').css({background : '#FBD850'});
            }, function() {
                $(this).find('td').css({'background-color':'#e7e8ea'});
            });
            $('tr:even' , $tabla).hover(function () {
                $(this).find('td').css({'background-color':'#FBD850'});
            }, function() {
                $(this).find('td').css({'background-color':'#FFFFFF'});
            });
        };
        
        
        
        
        //para agregar productos, cuando la orden esta en estatus 1 y 2
        //$add_grid_componente_orden(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],""       ,""    ,""          , 0);
        $add_grid_componente_orden = function(id_reg,producto_id,sku,descripcion,persona,maquina,eq_adicional,cantidad, subprocesos, proceso_flujo_id, unidad, unidad_id, densidad){
            
            if(subprocesos == ""){
                jAlert("El producto "+sku+" no tiene subprocesos", 'Atencion!');
            }else{
                
                var $tabla_productos_orden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                var trCount = $("tr", $tabla_productos_orden).size();
                trCount++;
                
                trr = '<tr>';
                    trr += '<td width="61" class="grid1" align="center">';
                        trr += '<a href="#eliminar'+trCount+'">Eliminar</a>';
                        
                        trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                        trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                        trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+producto_id +'">';
                    trr += '</td>';
                    trr += '<td width="80" class="grid1" align="center">';
                        trr += '<input type="text" name="sku'+trCount+'" value="'+sku+'"  class="borde_oculto" readOnly="true" style="width:88px;">';
                    trr += '</td>';
                    trr += '<td width="200" class="grid1"><input type="text" name="descripcion'+trCount+'" value="'+descripcion+'"  class="borde_oculto" readOnly="true" style="width:198px;"></td>';
                    
                    if(id_reg == "0"){
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="subproceso" value="'+subprocesos[0]['pro_subprocesos_titulo']+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                            trr += '<input type="hidden" name="subproceso_id" value="'+subprocesos[0]['pro_subprocesos_id']+'" >';
                            trr += '<input type="hidden" name="pro_subproceso_prod_id" value="'+subprocesos[0]['pro_subproceso_prod_id']+'" >';
                        trr += '</td>';
                    }else{
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="subproceso" value="'+subprocesos['subproceso']+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                            trr += '<input type="hidden" name="subproceso_id" value="'+subprocesos['pro_subprocesos_id']+'" >';
                            trr += '<input type="hidden" name="pro_subproceso_prod_id" value="'+subprocesos['pro_subprocesos_id']+'" >';
                        trr += '</td>';
                    }
                    
                    if(proceso_flujo_id == "1"){
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="persona" id="persona'+trCount+'" value="'+persona+'"  style="width:70px;" readOnly="true">';
                        trr += '</td>';
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="equipo" id="equipo'+trCount+'" value="'+maquina+'"  style="width:70px;" readOnly="true">';
                        trr += '</td>';
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="eq_adicional" id="eq_adicional'+trCount+'" value="'+eq_adicional+'"  style="width:70px;" readOnly="true">';
                        trr += '</td>';
                        trr += '<td width="80" class="grid1"><input type="text" name="cantidad" id="cantidad'+trCount+'" value="'+cantidad+'"  style="width:70px;"></td>';
                    }
                    
                    if(proceso_flujo_id == "2"){
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="persona" id="persona'+trCount+'" value="'+persona+'"  style="width:70px;" title="'+persona+'">';
                        //    trr += '<a href="#add_persona" id="add_persona'+trCount+'">+</a>';
                        trr += '</td>';
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="equipo" id="equipo'+trCount+'" value="'+maquina+'"  style="width:70px;" >';
                        trr += '</td>';
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="eq_adicional" id="eq_adicional'+trCount+'" value="'+eq_adicional+'"  style="width:70px;" title="'+eq_adicional+'">';
                        trr += '</td>';
                        trr += '<td width="80" class="grid1"><input type="text" id="cantidad'+trCount+'" name="cantidad" value="'+cantidad+'"  style="width:70px;" readOnly="true"></td>';
                    }
                    
                    trr += '<td width="80" class="grid1">';
                    trr += '<select id="unidad_default'+trCount+'" name="unidad_default" >';
                    unidad = unidad.toUpperCase();
                    
                    if(/^KILO*|KILOGRAMO$/.test(unidad)){
                        trr += '<option value="'+unidad_id+'" name="unidad_id" selected="yes">'+unidad+'</option>';
                        trr += '<option value="0">LITRO</option>';
                        //, unidad_id, densidad
                    }else{
                        trr += '<option value="'+unidad_id+'" selected="yes">'+unidad+'</option>';
                        trr += '<option value="0">KILO</option>';
                    }
                    
                    trr += '</select>';
                    trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                    trr += '<input type="hidden" name="unidad_id" value="'+unidad_id+'" >';
                    trr += '</td>';
                    
                trr += '</tr>';
                
                $tabla_productos_orden.append(trr);
                
                $aplicar_evento_keypress($tabla_productos_orden.find('#cantidad'+ trCount));
                
                //se pone todo en kilos, que es lo que debe de ser por defecto
                $tmp_parent = $tabla_productos_orden.find('#unidad_default'+ trCount).parent().parent();
                //$tmp_parent = $(this).parent().parent();
                //alert($tmp_parent.html());
                densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                text_selected = $tmp_parent.find('select option:selected').text();
                cantidad_default = $tmp_parent.find('input[name=cantidad]');
                //alert($tmp_parent.html());
                $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'inicio');
                
                //$tmp_parent = $tabla_productos_orden.find('#unidad_default'+ trCount).parent().parent();
                //alert($tmp_parent.html());
                //$event_changue_input_cantidad(cantidad_default);
                
                
                $tabla_productos_orden.find('#cantidad'+ trCount).focus(function(e){
                    if($(this).val() == ' '){
                        $(this).val('0');
                    }
                });
                
                //AL CAMBIAR UNA CANTIDAD EN UN SUBPROCESO, LO CAMBIA EN TODOS LOS DEMAS SUBPROCESOS DEL PRODUCTO
                $tabla_productos_orden.find('#cantidad'+ trCount).blur(function() {
                    cantidad_tr = $(this).val();
                    inv_prod_id_tr = $(this).parent().parent().find('input[name=inv_prod_id]').val();
                    if( ($(this).val() != ' ') && ($(this).val() != '') && ($(this).val() != null ) ){
                        $tabla_productos_orden.find('tr').each(function(){
                            inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                            if(inv_prod_id_tr == inv_prod_id_tmp){
                                $(this).find('input[name=cantidad]').val(cantidad_tr);
                            }
                        });
                    }
                });
                
                //para que al cambiar de kilos a litros o de litros a kilos, realize los calculos de acuerdp a la densidad
                $tabla_productos_orden.find('#unidad_default'+ trCount).change(function() {
                    $tmp_parent = $(this).parent().parent();
                    densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                    text_selected = $tmp_parent.find('select option:selected').text();
                    cantidad_default = $tmp_parent.find('input[name=cantidad]');
                    $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'grid');
                });
                
                $tabla_productos_orden.find('#add_persona'+ trCount).bind('click',function(event){
                    $tmp_parent = $(this).parent();
                    tmp_html = '<span class="person_adicionl">';
                        tmp_html += '<input type="text" name="add_persona" id="add_persona" style="width:70px;">';
                        tmp_html += '<a href="#remov_persona" id="remov_persona'+trCount+'">-</a>';
                    tmp_html += '</span>';
                    $tmp_parent.append(tmp_html);
                    //alert($tmp_parent.html());
                });
                
                
                
                //para el autocomplete
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_operarios.json';
                $autocomplete_input($tabla_productos_orden.find('#persona'+trCount+''), input_json);
                
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipo.json';
                $autocomplete_input($tabla_productos_orden.find('#equipo'+trCount+''), input_json);
                
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipoadicional.json';
                $autocomplete_input($tabla_productos_orden.find('#eq_adicional'+trCount+''), input_json);
                
                
                $tabla_productos_orden.find('a[href^=eliminar'+ trCount+']').bind('click',function(event){
                    if(parseInt($(this).parent().find('#delete').val()) != 0){
                        $(this).parent().find('#delete').val(0);
                        $(this).parent().parent().hide();
                    }
                });
                
                if(id_reg == "0"){
                    $add_suboprocesos(id_reg,producto_id, persona,maquina,eq_adicional, cantidad, subprocesos, unidad, unidad_id, densidad);
                }
            }
        }
        
        
        $recalcula_cantidades = function($tr, $table, text_selected){
            //alert(text_selected);
            inv_prod_id_tr = $tr.find('input[name=inv_prod_id]').val();
            cantidad_tr = $tr.find('input[name=cantidad]').val();
            unidad_id_tr = $tr.find('select[name=unidad_default]').val();
            text_selected = $tr.find('select option:selected').text();
            //alert(inv_prod_id_tr +" "+cantidad_tr+"   "+ unidad_id_tr);
            //alert($tr.html());
            //alert($table.html());
            $table.find('tr').each(function(){
                
                inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                
                if(inv_prod_id_tr == inv_prod_id_tmp){
                    $(this).find('input[name=cantidad]').val(cantidad_tr);
                    option_selected = $(this).find('select option:selected').text();
                    select_value =$(this).find('select[name=unidad_default]').val();
                    select =$(this).find('select[name=unidad_default]');
                    
                    id_unidad = "0";
                    select.find('option').each(function(){
                        if($(this).val() != "0"){
                            id_unidad = $(this).val();
                        }
                    });
                    
                    //alert(unidad_id_tr+"    "+id_unidad+"    "+text_selected);
                    
                    trr = '';
                    select.children().remove();
                    if(/^KILO*|KILOGRAMO*$/.test(text_selected) ){
                        //alert("asd");
                        if( unidad_id_tr != "0" ){
                            trr += '<option value="'+unidad_id_tr+'" selected="yes">'+text_selected+'</option>';
                            trr += '<option value="0">LITRO</option>';
                        }else{
                            trr += '<option value="0" selected="yes">'+text_selected+'</option>';
                            trr += '<option value="'+id_unidad+'">LITRO</option>';
                        }
                        
                    }else{
                        if( unidad_id_tr != "0" ){
                            trr += '<option value="'+unidad_id_tr+'" selected="yes">'+text_selected+'</option>';
                            trr += '<option value="0">KILO</option>';
                        }else{
                            trr += '<option value="0" selected="yes">'+text_selected+'</option>';
                            trr += '<option value="'+id_unidad+'">KILO</option>';
                        }
                    }
                    select.append(trr);
                    
                    
                    option_selected = $(this).find('select option:selected').text();
                    select_value =$(this).find('select[name=unidad_default]').val();
                    select =$(this).find('select[name=unidad_default]');
                    
                    //alert(unidad_id_tr+"    "+id_unidad+"    "+text_selected);
                    
                }
            });
            
        }
        
        $event_changue_input_cantidad = function(input_cantidad){
            $this_tr = input_cantidad.parent().parent();
            $this_table = input_cantidad.parent().parent().parent();
            cantidad_tr = $this_tr.find('input[name=cantidad]').val();
            inv_prod_id_tr = $this_tr.find('input[name=inv_prod_id]').val();
            
            input_cantidad.focus(function() {
                //alert("asd");
                $this_table.find('tr').each(function(){
                    inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                    if(inv_prod_id_tr == inv_prod_id_tmp){
                        $(this).find('input[name=cantidad]').val(cantidad_tr);
                    }
                });
            });
            
        }
        
        $event_changue_umedida = function(input_cantidad, titulo_selected, densidad, desde){
            titulo_selected = titulo_selected.toUpperCase();
            densidad_tmp = parseFloat(densidad);
            cantidad_original = parseFloat(input_cantidad.val());
            //alert(desde);
            if(desde == 'grid'){
                if(!isNaN(cantidad_original) && !isNaN(densidad_tmp)){
                    if(/^KILO*|KILOGRAMO*$/.test(titulo_selected)){
                        calculo = parseFloat(cantidad_original) * parseFloat(densidad_tmp);
                        input_cantidad.val(parseFloat(calculo).toFixed(4));
                    }else{
                        calculo = parseFloat(cantidad_original) / parseFloat(densidad_tmp);
                        input_cantidad.val(parseFloat(calculo).toFixed(4));
                    }
                }else{
                    input_cantidad.val(0);
                }
                
                $this_tr = input_cantidad.parent().parent();
                $this_table = input_cantidad.parent().parent().parent();
            }else{
                //alert(desde+"   "+cantidad_original+"    "+densidad_tmp);
                if(!isNaN(cantidad_original) && !isNaN(densidad_tmp)){
                    if(!/^KILO*|KILOGRAMO*$/.test(titulo_selected)){
                        titulo_selected = "KILO";
                        
                        //aqui falta que se seleccione kilogramo por defecto
                        calculo = parseFloat(cantidad_original) * parseFloat(densidad_tmp);
                        input_cantidad.val(parseFloat(calculo).toFixed(4));
                        
                        $this_tr_tmp = input_cantidad.parent().parent();
                        select_unidad = $this_tr_tmp.find('select[name=unidad_default]');
                        unidad_id_tr = select_unidad.val();
                        text_selected = $this_tr_tmp.find('select option:selected').text();
                        
                        select_unidad.children().remove();
                        
                        trr = '<option value="0" selected="yes" >KILO</option>';
                        trr += '<option value="'+unidad_id_tr+'" >'+text_selected+'</option>';
                        
                        select_unidad.append(trr);
                    }
                }else{
                    input_cantidad.val(0);
                }
                
                $this_tr = input_cantidad.parent().parent();
                $this_table = input_cantidad.parent().parent().parent();
                
            }
            //alert($this_tr.html());
            //alert(titulo_selected);
            $recalcula_cantidades($this_tr, $this_table, titulo_selected);
            
        }
        
        
        
        
        $aplicar_evento_keypress = function( $campo_input ){
            //validar campo cantidad recibida, solo acepte numeros y punto
            $campo_input.keypress(function(e){
                // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                if(e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                        return true;
                }else {
                        return false;
                }
            });
		}
        
        $aplicar_evento_keypress_input_lote = function( $campo_input ){
            //validar campo cantidad recibida, solo acepte numeros y punto
            $campo_input.keypress(function(e){
                // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                if(e.which==13 ) {
                    if ( $(this).val()!=''  &&  $(this).val()!=' ' && $(this).val()!=null ){
                        var $tr_padre = $(this).parent().parent();
                        $obtiene_datos_lote($tr_padre);
                    }else{
                        jAlert("Ingresa un n&uacute;mero de Lote.", 'Atencion!');
                    }
                    return false;
                }
            });
		}
        
        
		$aplicar_evento_focus_input_lote = function( $campo_input ){
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$campo_input.focus(function(e){
				if($(this).val() == ' '){
					$(this).val('');
				}
			});
		}
        
        
		$aplicar_evento_click_input_lote = function( $campo_input ){
			//validar campo cantidad recibida, solo acepte numeros y punto
			$campo_input.dblclick(function(e){
				$(this).select();
			});
		}
	
        $aplicar_evento_blur_input_lote = function( $campo_input ){
            //pone espacio en blanco al perder el enfoque, cuando no se ingresa un valor
            $campo_input.blur(function(e){
                if ( $(this).val() == ''  || $(this).val() == null ){
                    $(this).val(' ');
                }else{
                    //aqui va llamada a funcion que busca datos del lote
                    //var $tr_padre = $(this).parent().parent();
                    //$obtiene_datos_lote($tr_padre);
                }
            });
		}
        
        $aplicar_evento_focus_input_lote = function( $campo_input ){
            //al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
            $campo_input.focus(function(e){
                if($(this).val() == ' '){
                        $(this).val('');
                }
            });
		}
        
        
        $calcula_cantidad_por_porducto = function(cantidad , total){
            cantidad_retorno = 0;
            cantidad_retorno = parseFloat(cantidad).toFixed(4);
            return parseFloat(cantidad_retorno).toFixed(4);
        }
        
        //agrega productos a el grid de ORDEN DE PRODUCCION en estatus produccion
        //$add_grid_componente_orden(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],""       ,""    ,""          , 0);
        $add_grid_componente_orden_en_produccion = function(id_reg,producto_id,sku,descripcion,cantidad, proceso_flujo_id, subproceso,pro_subprocesos_id ,lote,especificaciones, id_reg_esp, unidad, unidad_id, densidad){
                
                var $tabla_productos_orden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                var trCount = $("tr", $tabla_productos_orden).size();
                trCount++;
                
                trr = '<tr >';
                    trr += '<td width="61" class="grid1" align="center">';
                        trr += '<a href="#ver_detalle" id="ver_detalle'+trCount+'">Detalle</a>';
                        trr += '<input type="hidden" id="estatus_detalle" name="estatus_detalle" value="0">';
                        trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                        trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                        trr += '<input type="hidden" id="id_reg_esp" name="id_reg_esp" value="'+id_reg_esp+'">';
                        trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+producto_id +'">';
                        trr += '<input type="hidden" id="posicion" name="posicion" value="'+trCount +'">';
                    trr += '</td>';
                    trr += '<td width="80" class="grid1" align="center">';
                        trr += '<input type="text" name="sku'+trCount+'" value="'+sku+'"  class="borde_oculto" readOnly="true" style="width:78px;">';
                    trr += '</td>';
                    trr += '<td width="200" class="grid1"><input type="text" name="descripcion'+trCount+'" value="'+descripcion+'"  class="borde_oculto" readOnly="true" style="width:208px;"></td>';
                    
                    
                    trr += '<td width="100" class="grid1">';
                        trr += '<span class="subproceso'+trCount+'"></span>';
                        trr += '<input type="text" name="subproceso" value="'+subproceso+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                        trr += '<input type="hidden" name="subproceso_id" value="'+pro_subprocesos_id+'" >';
                    trr += '</td>';
                    
                    trr += '<td width="100" class="grid1">';
                        trr += '<input type="text" name="lote" id="lote'+trCount+'" value="'+lote+'"  style="width:70px;" readOnly="true">';
                    trr += '</td>';
                    trr += '<td width="100" class="grid1">';
                        trr += '<input type="hidden" name="especificaciones" id="especificaciones'+trCount+'" value="'+especificaciones+'"  style="width:70px;" readOnly="true">';
                        trr += '<a href="#ver_especificaciones" id="ver_especificaciones'+trCount+'" title="Ver las especificaici&oacute;n">Res. Analisis</a>&nbsp;&nbsp;&nbsp;';
                        trr += '<a href="#add_especificaciones" id="add_especificaciones'+trCount+'" title="Agregar nueva especificaici&oacute;n">+</a>';
                    trr += '</td>';
                    
                    trr += '<td width="80" class="grid1"><input type="text" id="cantidad'+trCount+'" name="cantidad" value="'+cantidad+'"  style="width:70px;" ></td>';
                    trr += '<td width="80" class="grid1">';
                    trr += '<select id="unidad_default'+trCount+'" name="unidad_default" >';
                    unidad = unidad.toUpperCase();
                    if(/^KILO*|KILOGRAMO*$/.test(unidad)){
                        trr += '<option value="'+unidad_id+'" name="unidad_id" >'+unidad+'</option>';
                        trr += '<option value="0">LITRO</option>';
                        //, unidad_id, densidad
                    }else{
                        trr += '<option value="'+unidad_id+'">'+unidad+'</option>';
                        trr += '<option value="0">KILO</option>';
                    }
                    trr += '</select>';
                    trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                    trr += '<input type="hidden" name="unidad_id" value="'+unidad_id+'" >';
                    trr += '</td>';
                trr += '</tr>';
                
                $tabla_productos_orden.append(trr);
                
                
                $aplicar_evento_keypress($tabla_productos_orden.find('#cantidad'+ trCount));
                //AL CAMBIAR UNA CANTIDAD EN UN SUBPROCESO, LO CAMBIA EN TODOS LOS DEMAS SUBPROCESOS DEL PRODUCTO
                $tabla_productos_orden.find('#cantidad'+ trCount).blur(function() {
                    cantidad_tr = $(this).val();
                    inv_prod_id_tr = $(this).parent().parent().find('input[name=inv_prod_id]').val();
                    if( ($(this).val() != ' ') && ($(this).val() != '') && ($(this).val() != null ) ){
                        $tabla_productos_orden.find('tr').each(function(){
                            inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                            if(inv_prod_id_tr == inv_prod_id_tmp){
                                $(this).find('input[name=cantidad]').val(cantidad_tr);
                            }
                        });
                    }
                });
        }
        
        
        
        
        
        $opbtiene_datos_producto_por_sku = function($sku){
            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_busca_sku_prod.json';
            $arreglo = {'sku':$sku,
                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                        }
            
            $.post(input_json,$arreglo,function(prod){
                var res=0;
                if(prod['Sku'][0] != null){
                    
                    unidad = prod['Sku'][0]['unidad'];
                    unidad_id = prod['Sku'][0]['unidad_id'];
                    densidad = prod['Sku'][0]['densidad'];
                    
                    //agrega productos a el grid de formulaciones
                    $add_grid_componente_orden(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],""       ,""    ,""          , 0, prod['SubProcesos'], 1, unidad, unidad_id, densidad);
                }else{
                    jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                }
            },"json");
        }
        
        
	
});



