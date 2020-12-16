$(function() {
    //jQuery.noConflict();
    
    var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Configuraci&oacute;n de Producci&oacute;n' ,                 
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
			return this.contextpath + "/controllers/proconfigproduccion";
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
	var controller = $contextpath.val()+"/controllers/proconfigproduccion";
	
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_entrada = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Configuraci&oacute;n de Producci&oacute;n');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	
	var $cadena_busqueda = "";
	var $campo_busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $campo_busqueda_descripcionproceso = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcionproceso]');
	var $campo_busqueda_sku = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_sku]');
	var $campo_busqueda_descripcionproducto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcionproducto]');
	
	var array_subprocesos_disponibles = new Array(); //este arreglo carga los procesos disponibles
	var array_subprocesos_seleccionados = new Array(); //este arreglo carga los procesos seleccionados
	var array_maquinas = new Array(); //este arreglo carga la maquinas
	var array_instrumentos = new Array(); //este arreglo carga los procesos disponibles
        
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio_proceso" + signo_separador + $campo_busqueda_folio.val() + "|";
		valor_retorno += "descripcion_proceso" + signo_separador + $campo_busqueda_descripcionproceso.val() + "|";
		valor_retorno += "sku_producto" + signo_separador + $campo_busqueda_sku.val() + "|";
		valor_retorno += "descripcion_producto" + signo_separador + $campo_busqueda_descripcionproducto.val() + "|";
		
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
		$campo_busqueda_folio.val('');
		$campo_busqueda_descripcionproceso.val('');
		$campo_busqueda_sku.val('');
		$campo_busqueda_descripcionproducto.val('');
		
		$campo_busqueda_folio.focus();
	});
        
	//visualizar  la barra del buscador
	TriggerClickVisializaBuscador = 0;
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
		$campo_busqueda_folio.focus();
	});
	
	//Aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_descripcionproceso, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_sku, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_descripcionproducto, $buscar);
	
	
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
        
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-proconfigproduccion-window').find('select[name=prodtipo]');
            $('#forma-proconfigproduccion-window').find('#submit').mouseover(function(){
                $('#forma-proconfigproduccion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
            });
            $('#forma-proconfigproduccion-window').find('#submit').mouseout(function(){
                $('#forma-entradaproconfigproduccionmercancias-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
            });

            $('#forma-proconfigproduccion-window').find('#boton_cancelar').mouseover(function(){
                    $('#forma-proconfigproduccion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            });
            $('#forma-proconfigproduccion-window').find('#boton_cancelar').mouseout(function(){
                    $('#forma-proconfigproduccion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            });

            $('#forma-proconfigproduccion-window').find('#close').mouseover(function(){
                    $('#forma-proconfigproduccion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            });
            $('#forma-proconfigproduccion-window').find('#close').mouseout(function(){
                    $('#forma-proconfigproduccion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            });

            $('#forma-proconfigproduccion-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-proconfigproduccion-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-proconfigproduccion-window').find(".contenidoPes:first").show(); //Show first tab content

            //On Click Event
            $('#forma-proconfigproduccion-window').find("ul.pestanas li").click(function() {
                $('#forma-proconfigproduccion-window').find(".contenidoPes").hide();
                $('#forma-proconfigproduccion-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-proconfigproduccion-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
        
        
        
        
        
	//buscador de productos
	$busca_productos = function(tipo_busqueda){
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
                        
                        //buscador principal de el proceso
                        if(tipo_busqueda == 1){
                            if(pt['id'] == 2 || pt['id'] == 1 || pt['id'] == 8){
                                prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                            }
                        }
                        //para buscar un producto master
                        if(tipo_busqueda == 2){
                            if(pt['id'] == 2 || pt['id'] == 1 ){
                                prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                            }
                        }
                        
                        //para buscar elementos de el proceso
                        if(tipo_busqueda == 3){
                            if(pt['id'] == 2 || pt['id'] == 1  || pt['id'] == 7 ){
                                if(pt['id'] == 7){
                                    prod_tipos_html += '<option value="' + pt['id'] + '" selected="yes">' + pt['titulo'] + '</option>';
                                }else{
                                    prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                                }
                            }
                        }
                        
                        //para buscar el producto de salida
                        if(tipo_busqueda == 4){
                            if(pt['id'] == 2 || pt['id'] == 1 ){
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
					var unidad=$(this).find('span.unidad_prod_buscador').html();
					
					//buscador principal de el proceso
					if(tipo_busqueda == 1){
						//asignar a los campos correspondientes el sku y y descripcion
						$('#forma-proconfigproduccion-window').find('input[name=id_producto]').val(id_prod);
						$('#forma-proconfigproduccion-window').find('input[name=sku]').val(codigo);
						$('#forma-proconfigproduccion-window').find('input[name=descripcion]').val(descripcion);
						$('#forma-proconfigproduccion-window').find('input[name=sku_producto]').focus();
						
					}
                           
					//para buscar un producto master
					if(tipo_busqueda ==2){
						//colocando los datos elegidos en el pluguin
						$('#forma-formulas-window').find('input[name=id_prod_master]').val(id_prod);
						$('#forma-formulas-window').find('input[name=codigo_master]').val(codigo);
						$('#forma-formulas-window').find('input[name=descripcion_master]').val(descripcion);
						$('#forma-formulas-window').find('input[name=select_prodtipo]').val(producto);
						$('#forma-formulas-window').find('input[name=select_unidad]').val(unidad);
						//fin del primer plugin
					}
					
					//para buscar elementos de el proceso
					if(tipo_busqueda ==3){
					   //guardando los datos del pluguin si es que ligio buscar elementos para el grid
						$('#forma-formulas-window').find('input[name=id_prod_entrante]').val(id_prod);
						$('#forma-formulas-window').find('input[name=codigo_producto_minigrid]').val(codigo);
						$('#forma-formulas-window').find('input[name=descr_producto_minigrid]').val(descripcion);

					}
					
					//para buscar el producto de salida
					if(tipo_busqueda ==4){
						//guardando los datos del pluguin si es que ligio buscar elementos para el grid
						$('#forma-formulas-window').find('input[name=id_prod_saliente]').val(id_prod);
						$('#forma-formulas-window').find('input[name=codigo_producto_saliente]').val(codigo);
						$('#forma-formulas-window').find('input[name=descr_producto_saliente]').val(descripcion);
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
	
        $verifica_porcentaje = function($tabla_tmp, accion){
            /*
            $sum_porciento = 0;
            $tabla_tmp.find('tbody > tr').each(function (index){
                
                if(parseInt($(this).find('#delete').val())!=0){
                    $porciento = parseFloat($(this).find('input[name=cantidad]').val()).toFixed(4);
                    if(! isNaN($porciento)){
                        $sum_porciento = parseFloat($sum_porciento) + parseFloat($porciento);
                    }
                }
            });
            */
            $sum_porciento = 0;
            $tabla_tmp.find('tbody > tr').each(function (index){
                if(parseInt($(this).find('#delete').val())!=0){
                    $porciento = parseFloat($(this).find('input[name=cantidad]').val()).toFixed(4);
                    if(! isNaN($porciento)){
                        $sum_porciento = parseFloat($sum_porciento) + parseFloat($porciento);
                    }
                }
            });
            $sum_porciento =  parseFloat(parseFloat($sum_porciento).toFixed(4));
            
            if(accion == 'confirm'){
                $sum_porciento = parseFloat($sum_porciento).toFixed(4);
                if(parseFloat($sum_porciento) == 100 || parseFloat($sum_porciento) == 100.00){
                    return true;
                }else{
                    return false;
                }
            }else{
                if($sum_porciento <= 100){
                    return true;
                }else{
                    return false;
                }
            }
        }
	
        
        $add_grid_componente_formulaciones = function(id_reg,sku_id,sku,descripcion,cantidad,posicion ){
            
            var $grid_productos_componentes     = $('#forma-formulas-window').find('div.div_formulaciones');
            var $cantidad_calculo              = $('#forma-formulas-window').find('input[name=cantidad_calculo]');
            var $porcentaje_temporal = $('#forma-formulas-window').find('input[name=porcentaje_temporal]');
            var $total_porcentaje = $('#forma-formulas-window').find('input[name=total_porcentaje]');
            
            var trCount = $("tbody > tr", $grid_productos_componentes).size();
            trCount++;
            if(posicion == 0){
                posicion = trCount;
            }
            
            trr = '<tr>';
                    trr += '<td width="92px">';
                            trr += '<a href=#>Eliminar</a>';
                            trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                            trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                            trr += '<input type="hidden" id="id_prod_componente" name="id_prod_componente" value="'+sku_id+'">';
                    trr += '</td>';
                    trr += '<td width="92px">';
                        trr += '<INPUT TYPE="text" id="posicion'+trCount+'" value="'+posicion+'" name="posicion" style="width:70px;height:20px">';
                    trr += '</td>';
                    trr += '<td width="90px">';
                        trr += '<INPUT TYPE="text" name="sku'+ trCount +'" value="'+sku +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:70px;">';
                    trr += '</td>';
                    trr += '<td width="370px">';
                        trr += '<INPUT TYPE="text" name="nombre'+ trCount +'" 	value="'+ descripcion +'" id="nom" class="borde_oculto" readOnly="true" style="width:350px;">';
                    trr += '</td>';
                    trr += '<td width="90px">';
                            trr += '<INPUT TYPE="text" id="porcentaje'+trCount+'" value="'+cantidad+'" name="cantidad" style="width:88px;height:20px">';
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
                    $(this).parent().find('#delete').val(0);
                    $(this).parent().parent().hide();
                    //$(this).parent().parent().remove();
                }
                
            });
            
            
            tabla.find('#porcentaje'+trCount).focus(function(){
                if($(this).val() !=''){
                    $porcentaje_temporal.val($(this).val());
                    valida_porciento_tmp = $verifica_porcentaje($grid_productos_componentes, 'valida');
                    
                    if(valida_porciento_tmp == false){
                        $porcentaje_temporal.val(0);
                        jAlert("Esta excediendo el 100 % de la configuración", 'Atencion!');
                    }
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
                                //calculo_porcentaje=(parseFloat($(this).val()) / parseFloat($cantidad_calculo.val())) *100;
                                
                                calculo_porcentaje=(parseFloat(parseFloat($(this).val()).toFixed(4)) / parseFloat(parseFloat($cantidad_calculo.val()).toFixed(4))) *100;
                                $(this).val(parseFloat(calculo_porcentaje).toFixed(4));
                                
                                valida_porciento_tmp = $verifica_porcentaje($grid_productos_componentes, 'valida');
                                if(valida_porciento_tmp == false){
                                    $(this).val(0);
                                    jAlert("Esta excediendo el 100 % de la configuración", 'Atencion!');
                                }
                        }
                        
                        $grid_productos_componentes.find('tbody > tr').each(function (index){
                            if(parseInt($(this).find('#delete').val())!=0){
                                    total = parseFloat(total) + parseFloat(calculo_porcentaje);
                            }
                        });
                        $total_porcentaje.val(total);
                        //alert("total_porcentaje: "+$total_porcentaje.val()+"       total: "+total);
                        
                }else{
                        jAlert("La cantidad debe tener un 0, ejemplo: 0.3, 456.5, 654.9",'! Atencion');
                        $(this).val(0.0)
                }
            });
        }
        
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
            valida_porciento_tmp = true;
            
            valida_porciento_tmp = $verifica_porcentaje($grid_productos_componentes, 'valida');
            if($cantidad_calculo.val() !=0 && valida_porciento_tmp==true){
                if($codigo_producto_minigrid.val() != null && $codigo_producto_minigrid.val() != ''){
                    var encontrado=0;
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_busca_sku_prod.json';
                    $arreglo = {'sku':$codigo_producto_minigrid.val(),
                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                            }
                            
                    $.post(input_json,$arreglo,function(prod){
                        var res=0;
                        if(prod['Sku'][0] != null){
                            
                            //agrega productos a el grid de formulaciones
                            $add_grid_componente_formulaciones(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],0,0);
                            
                        }else{
                                jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                        }
                    },"json");		
                }else{
                    jAlert("Ingrese un C&oacute;digo de producto valido", 'Atencion!');
                }
            }else{
                if(valida_porciento_tmp == false){
                    jAlert("Ya tiene el 100 % de la configuracion", 'Atencion!');
                }else{
                    jAlert("Ingrese una cantidad diferente de cero", 'Atencion!');
                }
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
	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_presentaciones_producto = function(rfc_proveedor, sku_producto){
		//verifica si el campo rfc proveedor no esta vacio
		if(rfc_proveedor != ''){
			//verifica si el campo sku no esta vacio para realizar busqueda
			if(sku_producto != ''){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_presentaciones_producto.json';
				$arreglo = {'sku':sku_producto};
				
				var trr = '';
				
				$.post(input_json,$arreglo,function(entry){
					
					//verifica si el arreglo  retorno datos
					if (entry['Presentaciones'].length > 0){
						$(this).modalPanel_Buscapresentacion();
						var $dialogoc =  $('#forma-buscapresentacion-window');
						$dialogoc.append($('div.buscador_presentaciones').find('table.formaBusqueda_presentaciones').clone());
						$('#forma-buscapresentacion-window').css({"margin-left": -200, "margin-top": -200});
						
						var $tabla_resultados = $('#forma-buscapresentacion-window').find('#tabla_resultado');
						var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('#cencela');
                                                
						$cancelar_plugin_busca_lotes_producto.mouseover(function(){
                                                    $(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
						});
						$cancelar_plugin_busca_lotes_producto.mouseout(function(){
                                                    $(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
						});	
						
						
						$tabla_resultados.children().remove();
						
						//crea el tr con los datos del producto seleccionado
						$.each(entry['Presentaciones'],function(entryIndex,pres){
                                                    trr = '<tr>';
                                                        trr += '<td width="100">';
                                                            trr += '<span class="id_prod" style="display:none">'+pres['id']+'</span>';
                                                            trr += '<span class="sku">'+pres['sku']+'</span>';
                                                        trr += '</td>';
                                                        trr += '<td width="250"><span class="titulo">'+pres['titulo']+'</span></td>';
                                                        trr += '<td width="80">';
                                                            trr += '<span class="unidad" style="display:none">'+pres['unidad']+'</span>';
                                                            trr += '<span class="id_pres" style="display:none">'+pres['id_presentacion']+'</span>';
                                                            trr += '<span class="pres">'+pres['presentacion']+'</span>';
                                                        trr += '</td>';
                                                    trr += '</tr>';
                                                    $tabla_resultados.append(trr);
                                                    
                                                    $colorea_tr_grid($tabla_resultados);
						});
                                                
						/*
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
						*/
						//seleccionar un producto del grid de resultados
						$tabla_resultados.find('tr').click(function(){
                                                    //llamada a la funcion que busca y agrega producto al grid, se le pasa como parametro el lote y el almacen
                                                    //$agrega_producto_grid($(this).find('span.lote').html(),$(this).find('input.idalmacen').val());
                                                    var id_prod = $(this).find('span.id_prod').html();
                                                    var sku = $(this).find('span.sku').html();
                                                    var titulo = $(this).find('span.titulo').html();
                                                    var unidad = $(this).find('span.unidad').html();
                                                    var id_pres = $(this).find('span.id_pres').html();
                                                    var pres = $(this).find('span.pres').html();

                                                    //aqui se pasan datos a la funcion que agrega el tr en el grid
                                                    $agrega_producto_al_grid(id_prod,sku,titulo,unidad,id_pres,pres);

                                                    //elimina la ventana de busqueda
                                                    var remove = function() {$(this).remove();};
                                                    $('#forma-buscapresentacion-overlay').fadeOut(remove);
						});
                                                
                                                $cancelar_plugin_busca_lotes_producto.click(function(event){
                                                    //event.preventDefault();
                                                    var remove = function() {$(this).remove();};
                                                    $('#forma-buscapresentacion-overlay').fadeOut(remove);
                                                });
                                                
					}else{
                                            jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                                            $('#forma-proconfigproduccion-window').find('input[name=titulo_producto]').val('');
					}
				},"json");
				
			}else{
				jAlert("Es necesario ingresar un Sku de producto valido", 'Atencion!');
			}
		}else{
			jAlert("Es necesario seleccionar un proveedor", 'Atencion!');
		}
		
	}//termina buscador de lotes disponibles de un producto
	
        
        $ordenar_por_nivel = function(){
            var $grid_sub_procesos = $('#forma-proconfigproduccion-window').find('#tabla_subprocesos_seleccionados');
        }
        
	
        $agregar_subprocesos_al_proceso = function(id_subp, titulo, nivel, maquina, doc_calidad, empleados, metadata, especificaciones, procedimiento,id_master, id_reg, id_formula){
            
            var $grid_sub_procesos = $('#forma-proconfigproduccion-window').find('#tabla_subprocesos_seleccionados');
            
            var trCount = $("tr", $grid_sub_procesos).size();
			trCount++;
                        
            trr = '<tr >';
                trr += '<td class="grid1 eliminar'+trCount+'" id="td_eliminar" style="border-botton: 0px none;" width="60px">';
                    trr += '<div class="delete"><a href="elimina_producto" id="eliminaprod'+ trCount +'">Eliminar</a></div>';
                    trr += '<input type="hidden" name="eliminado" id="eliminado" value="1">';
                    trr += '<input type="hidden" name="id_master" id="id_master" value="'+id_master+'">';
                    trr += '<input type="hidden" name="id_formula" id="id_formula" value="'+id_formula+'">';
                    trr += '<input type="hidden" name="id_reg" id="id_reg" value="'+id_reg+'">';
                    trr += '<input type="hidden" name="especificaciones" id="especificaciones" value="'+especificaciones+'">';
                    trr += '<input type="hidden" name="procediemientos" id="procediemientos" value="'+procedimiento+'">';
                trr += '</td>';
                trr += '<td class="grid1" id="td_nivel" style="border-botton: 0px none;" width="98px"><input type="input" id="nivel_grid" name="nivel_grid" value="'+nivel+'" style="width:60px;" readOnly="true" ></td>';
                trr += '<td class="grid1 ver_detalles'+trCount+'" id="td_subproceso" width="150">';
                    trr += '<input type="hidden" name="id_subproceso_grid" id="id_subproceso_grid" value="'+id_subp+'">';
                    trr += '<input type="input" name="titulo_subproceso_grid" id="titulo_subproceso_grid" value="'+titulo+'" readOnly="true" style="width:140px;">';
                trr += '</td>';
                trr += '<td class="grid1" width="102">';
                
                    trr += '<select name="tipo_maquina_grid" id="maquina_grid" style="width:99px;">';
                    var verificador = 0;//esta variable me sirve para verificar si se selecciono una maquina, si no se ha seleccionado, se le agrega la opcione por default
                    $.each(array_maquinas,function(entryIndex,maq){
                        if(maq['id'] == maquina){
                            trr += '<option value="'+maq['id']+'" selected="yes">'+maq['titulo']+'</option>';
                            verificador = 1;
                        }else{
                            trr += '<option value="'+maq['id']+'">'+maq['titulo']+'</option>';
                        }
                    });
                    if(verificador == 0){
                        trr += '<option value="0" selected="yes">[-- Seleccione una opcion --]</option>';
                    }
                    trr += '</select>';
                    
                trr += '</td>';
                trr += '<td class="grid1" width="126"><input type="input" name="doc_calidad_grid" id="doc_calidad_grid'+ trCount +'" value="'+doc_calidad+'"></td>';
                trr += '<td class="grid1" width="150"><input type="input" name="empleados_grid" id="empleados_grid'+ trCount +'" value="'+empleados+'" readOnly="true"></td>';
                trr += '<td class="grid1" width="150"><input type="input" name="metadata_grid" id="metadata_grid'+ trCount +'" value="'+metadata+'" readOnly="true"></td>';
                
            trr += '</tr>';
            $grid_sub_procesos.append(trr);
            
            //$grid_sub_procesos.find('#eliminaprod'+ trCount).parent().parent().find('#doc_calidad_grid').focus();
            
            $colorea_tr_grid($grid_sub_procesos);
            
            $grid_sub_procesos.find('.ver_detalles'+trCount).bind('click',function(event){
                especificaciones_tmp = $(this).parent().find('input[name=especificaciones]').val();
                procediemientos_tmp = $(this).parent().find('input[name=procediemientos]').val();
                id_subp = $(this).parent().find('input[name=id_subproceso_grid]').val();
                titulo = $(this).parent().find('input[name=titulo_subproceso_grid]').val();
                id_master = $(this).parent().find('input[name=id_master]').val();
                nivel_grid = $(this).parent().find('input[name=nivel_grid]').val();
                id_reg_ = $(this).parent().find('input[name=id_reg]').val();
                id_formula_ = $(this).parent().find('input[name=id_formula]').val();
                
                $llenar_especificaciones_procedidmientos_config(id_subp, titulo, especificaciones_tmp, procediemientos_tmp, 'edit', '.ver_detalles'+trCount, id_master,nivel_grid, id_reg_, id_formula_);
            });
            
            //elimina un producto del grid
            $grid_sub_procesos.find('#eliminaprod'+ trCount).bind('click',function(event){
                event.preventDefault();
                if(parseInt($(this).parent().find('#eliminado').val()) != 0){
                    //$(this).parent().parent().find('input').val(' ');
                    //asigna un 0 al input eliminado como bandera para saber que esta eliminado
                    $(this).parent().parent().find('#eliminado').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
                    //oculta la fila eliminada
                    $(this).parent().parent().parent().hide();
                }
            });
        }
        
        
        $valida_campos_grid = function(valor, mascara){
           
           retorno = true;
           if(mascara == 'cadena'){
               if(!/^.{1,250}$/.test(valor)){
                   retorno = false;
               }
           }
           
           if(mascara == 'numeroentero'){
               if(!/^[0-9]{0,14}$/.test(valor)){
                   retorno = false;
               }
           }
           
           if(mascara == 'numeroflotante'){
               if(!/^([0-9]){1,12}[.]?[0-9]*$/.test(valor)){
                   retorno = false;
               }
           }
           
           if(mascara == 'fecha'){
               if(!/^[0-9]{4}[-]{1}[0-9]{2}[-]{1}[0-9]{2}$/.test(valor)){
                   retorno = false;
               }
           }
           
           return retorno;
       }
       
       $compara_cantidades_especificaciones = function(valor1, valor2, campo){
           
           retorno = "true";
           
           valor1 = valor1.toUpperCase();
           valor2 = valor2.toUpperCase();
           if((/^NA$/.test(valor1)  || /^N.A.$/.test(valor1)) && (/^NA$/.test(valor2)  || /^N.A.$/.test(valor2)) ){
              retorno = "true";
           }else{
               valor1_1 = parseFloat(valor1);
               valor2_2 = parseFloat(valor2);
               
               if(!isNaN(valor1_1) && !isNaN(valor2_2) ){
                   if((valor1_1 >  valor2_2) && retorno == "true"){
                       retorno = "El valor uno para "+campo+" debe de ser mayor que el valor 2";
                   }
               }else{
                   retorno = "Verifique el correcto llenado para "+campo;
               }
           }
           
           return retorno;
       }
       
       
       
       $plugin_cargar_formulacion = function(id_to_show_form){
            $(this).modalPanel_formulas();
            
            var form_to_show = 'formaFormulas';
            $('#' + form_to_show).each (function(){this.reset();});
            var $forma_selected = $('#' + form_to_show).clone();
            $forma_selected.attr({id : form_to_show + id_to_show_form});
            
			$('#forma-formulas-window').css({"margin-left": -390, 	"margin-top": -280});
			$forma_selected.prependTo('#forma-formulas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show_form , style:'display:table'});
                
            /*
            var $dialogoc =  $('#forma-formulas-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.plugin_formulaciones').find('table.formaBusqueda_formulaciones').clone());
            
            $('#forma-formulas-window').css({"margin-left": -390, 	"margin-top": -270});
            */
            
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
            var $tc_costear              		= $('#forma-formulas-window').find('input[name=tc_costear]');
            
            var $prod_tipo = $('#forma-formulas-window').find('input[name=select_prodtipo]');
            var $unidad = $('#forma-formulas-window').find('input[name=select_unidad]');
            var $id_producto_master             = $('#forma-formulas-window').find('input[name=id_prod_master]');
            var $id_producto_entrante           = $('#forma-formulas-window').find('input[name=id_prod_entrante]');
            var $id_producto_saliente           = $('#forma-formulas-window').find('input[name=id_prod_saliente]');
            
            var $codigo_Productosaliente        = $('#forma-formulas-window').find('input[name=codigo_producto_saliente]');
            var $descripcion_Productosaliente   = $('#forma-formulas-window').find('input[name=descr_producto_saliente]');
            var $href_buscar_Productosaliente   = $('#forma-formulas-window').find('a[href*=busca_producto_saliente]');
            
            //grid de productos
            var $grid_productos_componentes     = $('#forma-formulas-window').find('div.div_formulaciones');
            
            //grid de errores
            var $grid_warning = $('#forma-formulas-window').find('#div_warning_grid').find('#grid_warning');

            //botones		
            var $cerrar_plugin = $('#forma-formulas-window').find('#close');
            var $cancelar_plugin = $('#forma-formulas-window').find('#boton_cancelar');
            var $submit_actualizar = $('#forma-formulas-window').find('#submit');
            
            var $genera_pdf_formulas = $('#forma-formulas-window').find('#genera_pdf_formulas');
            
            
            $codigo_Productomaster.attr({'readOnly':true});
            $descripcion_Productomaster.attr({'readOnly':true});
            $codigo_producto_minigrid.attr({'readOnly':true});
            $descripcion_producto_minigrid.attr({'readOnly':true});
            $codigo_Productosaliente.attr({'readOnly':true});
            $descripcion_Productosaliente.attr({'readOnly':true});
            
			$tc_costear.keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}		
			});
			
			// perder enfoque
			$tc_costear.blur(function(){
				if ($(this).val().trim() == ''){
					$(this).val("1");
				}else{
					if (parseFloat($(this).val().trim()) == 0){
						$(this).val("1");
					}
				}
				$(this).val(parseFloat($(this).val()).toFixed(4));
			});
			
			// Al obtener el enfoque
			$tc_costear.focus(function(){
				if (parseFloat($(this).val().trim()) <= 0){
					$(this).val("");
				}
			});
			
			
            if(parseInt(id_to_show_form) == 0){
                id_to_show_form = 0;
                $campo_id.attr({'value' : id_to_show_form});
                
                
                $('#forma-formulas-window').find('.gen_pdf').hide();
                
                var respuestaProcesadaFormulacion = function(data){
                    $('#forma-formulas-window').find('.formulas_div_one').css({'height':'470px'});//sin errores
					if ( data['success'] == "true" ){
						jAlert("La formula fue dada de alta con exito", 'Atencion!');
						var $grid_productos_componentes     = $('#forma-formulas-window').find('div.div_formulaciones');
						var $href_buscar_Productomaster     = $('#forma-formulas-window').find('a[href*=buscar_productomaster]');
						var $href_buscar_Productosaliente   = $('#forma-formulas-window').find('a[href*=busca_producto_saliente]');
						var $paso_actual                    = $('#forma-formulas-window').find('input[name=paso_actual]');
						var $numero_pasos                   = $('#forma-formulas-window').find('input[name=numero_pasos]');                                                            
						var $codigo_Productosaliente        = $('#forma-formulas-window').find('input[name=codigo_producto_saliente]');
						var $descripcion_Productosaliente   = $('#forma-formulas-window').find('input[name=descr_producto_saliente]');
						
						var $total_porcentaje = $('#forma-formulas-window').find('input[name=total_porcentaje]');
						
						$paso_actual.attr({'readOnly':true});
						
						
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
						var $grid_productos_componentes     = $('#forma-formulas-window').find('div.div_formulaciones');
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-pocpedidos-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-formulas-window').find('.pocpedidos_div_one').css({'height':'568px'});//con errores
						$('#forma-formulas-window').find('div.interrogacion').css({'display':'none'});

						$grid_productos_componentes.find('#cant').css({'background' : '#ffffff'});
						$grid_productos_componentes.find('#cost').css({'background' : '#ffffff'});

						$('#forma-formulas-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-formulas-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
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
								
								//alert(tmp.split(':')[0]);
								
								if(parseInt($("tr", $grid_productos_componentes).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos_componentes).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='posicion'+i)){
											//alert(tmp.split(':')[0]);
											$('#forma-formulas-window').find('.formulas_div_one').css({'height':'568px'});
											//$('#forma-pocpedidos-window').find('.div_three').css({'height':'910px'});

											$('#forma-formulas-window').find('#div_warning_grid').css({'display':'block'});
											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
													$grid_productos_componentes.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
													//alert();
											}else{
													if(tmp.split(':')[0].substring(0, 8) == 'posicion'){
															$grid_productos_componentes.find('input[name=posicion]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
													}
											}

											//$grid_productos_componentes.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
											//$grid_productos_componentes.find('select[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});

											var tr_warning = '<tr>';
													tr_warning += '<td style="width:25px;"><div><IMG SRC="../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
													tr_warning += '<td style="width:122px;" >';
													tr_warning += '<INPUT TYPE="text" value="'+$grid_productos_componentes.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
													tr_warning += '</td>';
													tr_warning += '<td style="width:202px;">';
													tr_warning += '<INPUT TYPE="text" value="'+$grid_productos_componentes.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
													tr_warning += '</td>';
													tr_warning += '<td style="width:375px;">';
													tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:370px; color:red">';
													tr_warning += '</td>';
											tr_warning += '</tr>';
											$grid_warning.append(tr_warning);
										}
									}
								}
							}
						}
						
						$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
						$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
					}
				}
				var options = {dataType :  'json', success : respuestaProcesadaFormulacion};
				$forma_selected.ajaxForm(options);
                
                
                var buscador_producto= 0;
                
                var $id_tipo_producto=0;
				var $descripcion_producto="";
				var $id_unidad=0;
				var $descripcion_unidad="";
						
				$numero_pasos.change(function() {
					$numero_pasos.attr({'readOnly':true});
					$paso_actual.val($numero_pasos.val());

					$paso_actual.attr({'readOnly':true});
					//$paso_actual.attr("disabled", "disabled");
					//para habilitarlo   .removeAttr("disabled");
				});
						
						
						
				$href_buscar_Productomaster.click(function(event){
					event.preventDefault();
					buscador_producto=2;
					$busca_productos(buscador_producto);
				});
                
                
            }else{
                
                
                $campo_id.attr({'value' : id_to_show_form});
                
                $('#forma-formulas-window').find('.gen_pdf').show();
                
                //aqui es el post que envia los datos a getForulas.json
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFormula.json';
                $arreglo = {'id':id_to_show_form,
                            'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                };
                
                var $grid_productos_componentes     = $('#forma-formulas-window').find('div.div_formulaciones');
                
                var respuestaProcesadaFormulacion = function(data){
                    $('#forma-formulas-window').find('.formulas_div_one').css({'height':'470px'});//sin errores
                    if ( data['success'] == 'true' ){
                        var remove = function() {$(this).remove();};
                        $('#forma-formulas-overlay').fadeOut(remove);
                        jAlert("Los datos se han actualizado.", 'Atencion!');
                        $get_datos_grid();
                    }else{
                        var $grid_productos_componentes     = $('#forma-formulas-window').find('div.div_formulaciones');
                        // Desaparece todas las interrogaciones si es que existen
                        //$('#forma-pocpedidos-window').find('.div_one').css({'height':'545px'});//sin errores
                        $('#forma-formulas-window').find('.pocpedidos_div_one').css({'height':'568px'});//con errores
                        $('#forma-formulas-window').find('div.interrogacion').css({'display':'none'});

                        $grid_productos_componentes.find('#cant').css({'background' : '#ffffff'});
                        $grid_productos_componentes.find('#cost').css({'background' : '#ffffff'});

                        $('#forma-formulas-window').find('#div_warning_grid').css({'display':'none'});
                        $('#forma-formulas-window').find('#div_warning_grid').find('#grid_warning').children().remove();

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

                                //alert(tmp.split(':')[0]);

                                if(parseInt($("tr", $grid_productos_componentes).size())>0){
                                        for (var i=1;i<=parseInt($("tr", $grid_productos_componentes).size());i++){
                                                if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='posicion'+i)){
                                                        //alert(tmp.split(':')[0]);
                                                        $('#forma-formulas-window').find('.formulas_div_one').css({'height':'568px'});
                                                        //$('#forma-pocpedidos-window').find('.div_three').css({'height':'910px'});

                                                        $('#forma-formulas-window').find('#div_warning_grid').css({'display':'block'});
                                                        if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
                                                                $grid_productos_componentes.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                //alert();
                                                        }else{
                                                                if(tmp.split(':')[0].substring(0, 8) == 'posicion'){
                                                                        $grid_productos_componentes.find('input[name=posicion]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                }
                                                        }

                                                        //$grid_productos_componentes.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
                                                        //$grid_productos_componentes.find('select[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});

                                                        var tr_warning = '<tr>';
                                                                tr_warning += '<td style="width:25px;"><div><IMG SRC="../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
                                                                tr_warning += '<td style="width:122px;" >';
                                                                tr_warning += '<INPUT TYPE="text" value="'+$grid_productos_componentes.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
                                                                tr_warning += '</td>';
                                                                tr_warning += '<td style="width:202px;">';
                                                                tr_warning += '<INPUT TYPE="text" value="'+$grid_productos_componentes.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
                                                                tr_warning += '</td>';
                                                                tr_warning += '<td style="width:375px;">';
                                                                tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:370px; color:red">';
                                                                tr_warning += '</td>';
                                                        tr_warning += '</tr>';
                                                        $grid_warning.append(tr_warning);
                                                }
                                        }
                                }
                            }
                        }

                        $grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
                        $grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
                    }
                }
                
                var options = {dataType :  'json', success : respuestaProcesadaFormulacion};
                $forma_selected.ajaxForm(options);
                
                
                //aqui se cargan los campos al editar
                $.post(input_json,$arreglo,function(entry){
                    $codigo_Productomaster.attr({'readOnly':true});
                    $descripcion_Productomaster.attr({'readOnly':true});
                    $codigo_producto_minigrid.attr({'readOnly':true});
                    $descripcion_producto_minigrid.attr({'readOnly':true});
                    $codigo_Productosaliente.attr({'readOnly':true});
                    $descripcion_Productosaliente.attr({'readOnly':true});
                    $paso_actual.attr({'readOnly':true});
                    $numero_pasos.attr({'readOnly':true});
                    
                    $campo_id.val (entry['Formulas']['0']['id']);
                    $prod_tipo.val(entry['Formulas']['0']['tipo_producto']);
                    $unidad.val(entry['Formulas']['0']['unidad']);
                    $codigo_Productomaster.val(entry['Formulas']['0']['codigo']);
                    $descripcion_Productomaster.val(entry['Formulas']['0']['descripcion']);
                    //$hrer_buscar_Productomaster.hide();
                    /* esto lo moente, por que no tengo los pasos ahorita*/
                    //$numero_pasos.val(entry['Formulas']['0']['numero_pasos']);
                    //$paso_actual.val(entry['Formulas']['0']['nivel_paso_actual']);
                    
                    $tc_costear.val(parseFloat(entry['Extra'][0]['valor_tc']).toFixed(4));
                    
                    $cantidad=$cantidad_calculo.val();
                    $codigo_producto_minigrid.val();
                    
                    //$descripcion_producto_minigrid.val();
                    $id_producto_master.val(entry['Formulas']['0']['inv_prod_id']);
                    $id_producto_entrante.val();
                    
                    var sum_porciento=0;
                    
                    //Formulas_DatosMinigrid.
                    $.each(entry['Formulas_DatosMinigrid'],function(entryIndex,elemento){
                        $add_grid_componente_formulaciones(elemento['id'],elemento['inv_prod_id'],elemento['codigo'],elemento['descripcion'],elemento['cantidad'],elemento['elemento'] );
                        sum_porciento = parseFloat(parseFloat(sum_porciento).toFixed(4)) + parseFloat(parseFloat(elemento['cantidad']).toFixed(4));
                        //Esto es para el numero de paso
                        $numero_pasos.val(elemento['nivel']);
                        $paso_actual.val(elemento['nivel']);
                    });
                    
                    $cantidad_calculo.val(sum_porciento);
                    
                    $id_producto_saliente.val(entry['Formulas_DatosProductoSaliente']['0']['inv_prod_id_salida'])//entry['Formulas_DatosProductoSaliente']['0']['inv_prod_id']
                    $codigo_Productosaliente.val(entry['Formulas_DatosProductoSaliente']['0']['codigo']);
                    $descripcion_Productosaliente.val(entry['Formulas_DatosProductoSaliente']['0']['descripcion']);
                    //$hrer_buscar_Productosaliente.val();
                    
                },"json");//termina llamada json
                
                $genera_pdf_formulas.click(function(event){
                    event.preventDefault();
                    //iu=$('#lienzo_recalculable').find('input[name=iu]').val();
                    var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
                    var stock = $('#forma-formulas-window').find('input[name=stock]').is(':checked');
                    var costear = $('#forma-formulas-window').find('input[name=chkcostear]').is(':checked');
                    var tc = $('#forma-formulas-window').find('input[name=tc_costear]').val();
                    var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfFormula/'+id_to_show_form+'/'+stock+'/'+costear+'/'+tc+'/'+iu+'/out.json';
                    window.location.href=input_json;
                });
                
            }
            
            $agregar_producto_minigrid.click(function(event){
                event.preventDefault();
                $agrega_producto_ingrediente();
                $codigo_producto_minigrid.val("");
                $descripcion_producto_minigrid.val("");
                $cantidad_calculo.attr({'readOnly':true});
            });
            
            $buscar_producto_para_minigrid.click(function(event){
                event.preventDefault();
                buscador_producto=3;
                $busca_productos(buscador_producto);
            });
            
            $href_buscar_Productosaliente.click(function(event){
                event.preventDefault();
                buscador_producto=4;
                $busca_productos(buscador_producto);
            });
            
            $submit_actualizar.bind('click',function(){
                var trCount = $("tbody > tr", $grid_productos_componentes).size();
                //$total_tr.val(trCount);
                if(trCount > 0){
                    valida_porciento_tmp = $verifica_porcentaje($grid_productos_componentes, 'confirm');
                    if(valida_porciento_tmp == true){
                        jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
                            // If they confirmed, manually trigger a form submission
                            if (r) $submit_actualizar.parents("FORM").submit();
                        });
                    }else{
                        jAlert("La configiuracion, no suma el 100 %.", 'Atencion!');
                    }
                }else{
                    jAlert("Es necesario Agregar a la lista los productos para la formula.", 'Atencion!');
                }
                // Always return false here since we don't know what jConfirm is going to do
                return false;
            });
            
            
            $cerrar_plugin.bind('click',function(){
                var remove = function() {$(this).remove();};
                $('#forma-formulas-overlay').fadeOut(remove);
            });
            
            $cancelar_plugin.click(function(event){
                var remove = function() {$(this).remove();};
                $('#forma-formulas-overlay').fadeOut(remove);
                $buscar.trigger('click');
            });
            
       }
        /* Plugin para agregar las espedificaciones */
       $plugin_procedimiento = function(id_subp, sku, data_string, accion){
           
           $cadeana_retorno = data_string;
           
           $(this).modalPanel_Procedidmiento();
            var $dialogoc =  $('#forma-procedidmiento-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.panel_procedimiento').find('table.formapanel_procedimiento').clone());
            
            $('#forma-procedidmiento-window').css({"margin-left": -250, 	"margin-top": -100});
            
            var $tabla_resultados_proced = $('#forma-procedidmiento-window').find('#tabla_resultado');
            
            var $campo_paso_titulo = $('#forma-procedidmiento-window').find('input[name=paso_titulo]');
            $campo_paso_titulo.val("");
            /*
            var $campo_descripcion = $('#forma-adicionalemergentes-window').find('input[name=campo_descripcion]');
            */
            
            var $aceptar_acepta_procedidmiento = $('#forma-procedidmiento-window').find('#acepta_procedidmiento');
            var $cancelar_cencela_procedidmiento = $('#forma-procedidmiento-window').find('#cencela_procedidmiento');
            var $agregar_paso = $('#forma-procedidmiento-window').find('a[href*=agregar_paso]');
            var $buscar_procedidmiento = $('#forma-procedidmiento-window').find('a[href*=buscar_procedidmiento]');
            
            
            $buscar_procedidmiento.click(function(event){
                event.preventDefault();
                //limpiar_campos_grids();
		$(this).modalboxBuscaProcedimiento();
		var $dialogocx =  $('#forma-buscaprocedimiento-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogocx.append($('div.buscador_procedidmiento').find('table.formaBusqueda_procedidmiento').clone());
		
		$('#forma-buscaprocedimiento-window').css({"margin-left": -200, 	"margin-top": -90});
		
		var $tabla_resultados_otrosprocesos = $('#forma-buscaprocedimiento-window').find('#tabla_resultado');
		
		var $campo_sku = $('#forma-buscaprocedimiento-window').find('input[name=campo_sku]');
		var $campo_descripcion = $('#forma-buscaprocedimiento-window').find('input[name=campo_descripcion]');
                var $select_buscador_subproceso = $('#forma-buscaprocedimiento-window').find('select[name=buscador_subproceso]');
		
                
		var $buscar_plugin_procedimiento = $('#forma-buscaprocedimiento-window').find('#busca_producto_modalbox');
		var $cancelar_plugin_busca = $('#forma-buscaprocedimiento-window').find('#cencela');
		
		//funcionalidad botones
		$buscar_plugin_procedimiento.mouseover(function(){
                    $(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_procedimiento.mouseout(function(){
                    $(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_plugin_busca.mouseover(function(){
                    $(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
                
		$cancelar_plugin_busca.mouseout(function(){
                    $(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
                $select_buscador_subproceso.children().remove();
                $html_subprocesos = "";
                $.each(array_subprocesos_disponibles,function(entryIndex,pres){
                    $html_subprocesos += '<option value="'+pres['id']+'">'+pres['titulo']+'</option>';
                });
		$select_buscador_subproceso.append($html_subprocesos);
                
                $cancelar_plugin_busca.click(function(event){
                    event.preventDefault();
                    var remove = function() {$(this).remove();};
                    $('#forma-buscaprocedimiento-overlay').fadeOut(remove);;
                });
                
		//click buscar productos
		$buscar_plugin_procedimiento.click(function(event){
                    //event.preventDefault();
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_formulaciones_para_obtener_proced.json';
                    $arreglo = {'sku':$campo_sku.val(),
                                'descripcion':$campo_descripcion.val(),
                                'subproceso':$select_buscador_subproceso.val(),
                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                }
                    var trr = '';
                    $tabla_resultados_otrosprocesos.children().remove();
                    $.post(input_json,$arreglo,function(entrytmp){
                        $.each(entrytmp['Formulas'],function(entryIndex,formula){
                                trr = '<tr>';
                                        trr += '<td width="120">';
                                                trr += '<span class="sku_prod_buscador">'+formula['sku']+'</span>';
                                                trr += '<input type="hidden" id="id_subp_buscador" value="'+formula['id']+'">';
                                        trr += '</td>';
                                        trr += '<td width="390"><span class="titulo_prod_buscador">'+formula['descripcion']+'</span></td>';
                                        trr += '<td width="90"><span class="unidad_prod_buscador">'+formula['nivel']+'</span></td>';
                                trr += '</tr>';
                                $tabla_resultados_otrosprocesos.append(trr);
                        });
                        
                        $colorea_tr_grid($tabla_resultados_otrosprocesos);
                        
                        //seleccionar un producto del grid de resultados
                        $tabla_resultados_otrosprocesos.find('tr').click(function(){
                            //asignar a los campos correspondientes el sku y y descripcion
                            $id_prod_padre = $(this).find('#id_subp_buscador').val();
                            
                            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_procedimientos_por_formulacion.json';
                            $arreglo = {'id':$id_prod_padre,
                                    'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                            }
                            
                            var trr = '';
                            $tabla_resultados_otrosprocesos.children().remove();
                            $.post(input_json,$arreglo,function(entrytmp){
                                $.each(entrytmp['Formulas'],function(entryIndex,pasos){
                                    trCount = $('tr', $tabla_resultados_proced).size();
                                    trCount++;
                                    pasos['descripcion'] = pasos['descripcion'].split('macrocoma').join(',');
                                    trr = '<tr>';
                                            trr += '<td width="100px">';
                                                trr += '<a href="eliminar_paso'+pasos['posicion']+'">Eliminar</a>';
                                                trr += '<input type="hidden" id="num_pos" value="'+pasos['posicion']+'">';
                                            trr += '</td>';
                                            trr += '<td width="79px"><input type="text" id="posicion" name="posicion"  value="'+pasos['posicion']+'"   style="width:74px;height:17px;"></td>';
                                            trr += '<td style="width:590px;"><input type="text" id="descripcion" name="descripcion" value="'+pasos['descripcion']+'" style="width:535px;height:17px;"></td>';
                                    trr += '</tr>';
                                    
                                    $tabla_resultados_proced.append(trr);
                                    
                                    //elimina un producto del grid
                                    $tabla_resultados_proced.find('a[href*=eliminar_paso'+ pasos['posicion']+']').bind('click',function(event){
                                        event.preventDefault();
                                        $(this).parent().parent().remove();
                                        
                                        contador = 1;
                                        $tabla_resultados_proced.find('tbody').find('tr').each(function (index){
                                            $(this).find('input[name=posicion]').val(contador);
                                            contador++
                                        });
                                    });
                                });
                            });
                            
                            //elimina la ventana de busqueda
                            var remove = function() {$(this).remove();};
                            $('#forma-buscaprocedimiento-overlay').fadeOut(remove);;
                        });
                    });
		});
	
            });
            
            if(accion == "edit"){
                //data_string = data_string.replace(new RegExp("macrocoma", 'g'), ",");
                data_string = data_string.split('macrocoma').join(',');
                
                $rows_tmp = data_string.split("$$$");
                for(i=0;i < (($rows_tmp.length) - 1) ; i++){
                    array_data = $rows_tmp[i].split("&&&");
                     trCount = $('tr', $tabla_resultados_proced).size();
                    trCount++;
                    
                    trr = '<tr>';
                            trr += '<td  width="100">';
                                trr += '<a href="eliminar_paso'+array_data[0]+'">Eliminar</a>';
                                trr += '<input type="hidden" id="num_pos" value="'+array_data[0]+'">';
                            trr += '</td>';
                            trr += '<td width="80"><input type="text" id="posicion" name="posicion"  value="'+array_data[0]+'"   style="width:74px;height:17px;"></td>';
                            trr += '<td width="590"><input type="text" id="descripcion" name="descripcion" value="'+array_data[1]+'" style="width:535px;height:17px;"></td>';
                    trr += '</tr>';
                    
                    $tabla_resultados_proced.append(trr);
                    
                    //elimina un producto del grid
                    $tabla_resultados_proced.find('a[href*=eliminar_paso'+ array_data[0]+']').bind('click',function(event){
                        event.preventDefault();
                        $(this).parent().parent().remove()
                        
                        contador = 1;
                        $tabla_resultados_proced.find('tbody').find('tr').each(function (index){
                            $(this).find('input[name=posicion]').val(contador);
                            contador++
                        });
                    });
                }
            }
            
            $agregar_paso.click(function(event){
                event.preventDefault();
                if($valida_campos_grid($campo_paso_titulo.val(), 'cadena')){
                    trCount = $('tr', $tabla_resultados_proced).size();
                    trCount++;
                    
                    trr = '<tr>';
                            trr += '<td width="100px">';
                                trr += '<a href="eliminar_paso'+trCount+'">Eliminar</a>';
                            trr += '</td>';
                            trr += '<td width="79px"><input type="text" id="posicion" name="posicion" value="'+trCount+'" style="width:74px;height:17px;"></td>';
                            trr += '<td width="540"><input type="text" id="descripcion" name="descripcion" value="'+$campo_paso_titulo.val()+'" style="width:535px;height:17px;"></td>';
                    trr += '</tr>';
                    
                    $tabla_resultados_proced.append(trr);
                    
                    //elimina un producto del grid
                    $tabla_resultados_proced.find('a[href*=eliminar_paso'+ trCount+']').bind('click',function(event){
                        event.preventDefault();
                        $(this).parent().parent().remove();
                        contador = 1;
                        $tabla_resultados_proced.find('tbody').find('tr').each(function (index){
                            $(this).find('input[name=posicion]').val(contador);
                            contador++
                        });
                    });
                    
                    $campo_paso_titulo.val("");
                    
                }else{
                    jAlert("Ingrese una descripcion", 'Atencion!');
                }
            });
            
            $aceptar_acepta_procedidmiento.click(function(event){
               event.preventDefault();
               trCount = $('tr', $tabla_resultados_proced).size();
               if(trCount > 0 ){
                   $cadeana_retorno = "";
                   $tabla_resultados_proced.find('tbody').find('tr').each(function (index){
                       posicion = $(this).find('input[name=posicion]').val();
                       descripcion = $(this).find('input[name=descripcion]').val();
                       
                       $cadeana_retorno += posicion+"&&&"+descripcion+"$$$";
                   });
                   
                   var remove = function() {$(this).remove();};
                   $('#forma-procedidmiento-overlay').fadeOut(remove);
                   
                   //tmp_cadena = tmp_cadena.replace(new RegExp(",", 'g'), "macrocoma");
                   
                   //"macrocoma", ",", tmp_cadena
                   $cadeana_retorno = $cadeana_retorno.split(',').join('macrocoma');
                   
                   $cadena_procedimientos = $cadeana_retorno;
               }else{
                   jAlert("Ingrese todos los pasos necesarios", 'Atencion!');
               }
            });
            
            $cancelar_cencela_procedidmiento.click(function(event){
                event.preventDefault();
                var remove = function() {$(this).remove();};
                $('#forma-procedidmiento-overlay').fadeOut(remove);
                $cadeana_retorno = $cadeana_retorno.split(',').join('macrocoma');
                
                $cadena_procedimientos = $cadeana_retorno;
            });
       }
       
       $convierte_numero_caracter = function(cadena){
           cadena_tmp = parseInt(cadena);
           if(!isNaN(cadena_tmp)){
               if(cadena_tmp == -1 ){
                   return "N.A.";
               }else{
                   return cadena;
               }
           }else{
               return "N.A.";
           }
       }
       
       $convierte_caracter_numero = function(cadena){
           cadena = cadena.toUpperCase();
           if(/NA/.test(cadena)  || /N.A./.test(cadena) ){
               return -1;
           }else{
               return cadena;
           }
       }
       
       $verificar_cadena_verdadera = function($option, $cadena){
           $cadena_retorno = "";
           if($cadena == "true" || $cadena == "TRUE"){
               $cadena_retorno = "";
           }else{
               $cadena_retorno = $cadena;
           }
           
           if($option == "true" || $option == "TRUE"){
               if($cadena_retorno == "true" || $cadena_retorno == "TRUE"  || $cadena_retorno == ""){
                   $cadena_retorno = $cadena_retorno;
               }
           }else{
               $cadena_retorno = $cadena+$option+"\n";
           }
           
           return $cadena_retorno;
       }
       
       /* Plugin para agregar las espedificaciones */
       $plugin_especificaciones = function(id_subp, sku, data_string, accion){
           
           $cadeana_retorno = data_string;
           $(this).modalPanel_Especificaciones();
            var $dialogoc =  $('#forma-especificaciones-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.panel_especificacoines').find('table.forma_especificacoines').clone());
            
            $('#forma-especificaciones-window').css({"margin-left": -250, 	"margin-top": -100});
            
            /*campos para las especificaciones*/
            var $campo_fineza = $('#forma-especificaciones-window').find('input[name=fineza]');
            var $campo_viscosidad1 = $('#forma-especificaciones-window').find('input[name=viscosidad1]');
            var $campo_viscosidad2 = $('#forma-especificaciones-window').find('input[name=viscosidad2]');
            var $campo_viscosidad3 = $('#forma-especificaciones-window').find('input[name=viscosidad3]');
            var $campo_densidad = $('#forma-especificaciones-window').find('input[name=densidad]');
            var $campo_volatil = $('#forma-especificaciones-window').find('input[name=volatil]');
            var $campo_cubriente = $('#forma-especificaciones-window').find('input[name=cubriente]');
            var $campo_tono = $('#forma-especificaciones-window').find('input[name=tono]');
            var $campo_brillo = $('#forma-especificaciones-window').find('input[name=brillo]');
            var $campo_dureza = $('#forma-especificaciones-window').find('input[name=dureza]');
            var $campo_adherencia = $('#forma-especificaciones-window').find('input[name=adherencia]');
            var $campo_hidrogeno = $('#forma-especificaciones-window').find('input[name=hidrogeno]');
            
            var $campo_fineza1 = $('#forma-especificaciones-window').find('input[name=fineza1]');
            var $campo_viscosidad11 = $('#forma-especificaciones-window').find('input[name=viscosidad11]');
            var $campo_viscosidad21 = $('#forma-especificaciones-window').find('input[name=viscosidad21]');
            var $campo_viscosidad31 = $('#forma-especificaciones-window').find('input[name=viscosidad31]');
            var $campo_densidad1 = $('#forma-especificaciones-window').find('input[name=densidad1]');
            var $campo_volatil1 = $('#forma-especificaciones-window').find('input[name=volatil1]');
            var $campo_cubriente1 = $('#forma-especificaciones-window').find('input[name=cubriente1]');
            var $campo_tono1 = $('#forma-especificaciones-window').find('input[name=tono1]');
            var $campo_brillo1 = $('#forma-especificaciones-window').find('input[name=brillo1]');
            var $campo_dureza1 = $('#forma-especificaciones-window').find('input[name=dureza1]');
            var $campo_adherencia1 = $('#forma-especificaciones-window').find('input[name=adherencia1]');
            var $campo_hidrogeno1 = $('#forma-especificaciones-window').find('input[name=hidrogeno1]');
            
            
            var $select_inst_fineza = $('#forma-especificaciones-window').find('select[name=inst_fineza]');
            var $select_inst_viscosidad1 = $('#forma-especificaciones-window').find('select[name=inst_viscosidad1]');
            var $select_inst_viscosidad2 = $('#forma-especificaciones-window').find('select[name=inst_viscosidad2]');
            var $select_inst_viscosidad3 = $('#forma-especificaciones-window').find('select[name=inst_viscosidad3]');
            var $select_inst_densidad = $('#forma-especificaciones-window').find('select[name=inst_densidad]');
            var $select_inst_volatil = $('#forma-especificaciones-window').find('select[name=inst_volatil]');
            var $select_inst_cubriente = $('#forma-especificaciones-window').find('select[name=inst_cubriente]');
            var $select_inst_tono = $('#forma-especificaciones-window').find('select[name=inst_tono]');
            var $select_inst_brillo = $('#forma-especificaciones-window').find('select[name=inst_brillo]');
            var $select_inst_dureza = $('#forma-especificaciones-window').find('select[name=inst_dureza]');
            var $select_inst_adherencia = $('#forma-especificaciones-window').find('select[name=inst_adherencia]');
            var $select_inst_hidrogeno = $('#forma-especificaciones-window').find('select[name=inst_hidrogeno]');
            
            
            var $aceptar_acepta_especificacaiones = $('#forma-especificaciones-window').find('#acepta_especificacaiones');
            var $cancelar_cencela_especificacaiones = $('#forma-especificaciones-window').find('#cencela_especificacaiones');
            
            
            if(accion == "edit"){
                
                $campos_espliteados = data_string.split("&&&");
                
                $campo_fineza.val($convierte_numero_caracter($campos_espliteados[0]));
                $campo_viscosidad1.val($convierte_numero_caracter($campos_espliteados[1]));
                $campo_viscosidad2.val($convierte_numero_caracter($campos_espliteados[2]));
                $campo_viscosidad3.val($convierte_numero_caracter($campos_espliteados[3]));
                $campo_densidad.val($convierte_numero_caracter($campos_espliteados[4]));
                $campo_volatil.val($convierte_numero_caracter($campos_espliteados[5]));
                $campo_cubriente.val($convierte_numero_caracter($campos_espliteados[6]));
                $campo_tono.val($convierte_numero_caracter($campos_espliteados[7]));
                $campo_brillo.val($convierte_numero_caracter($campos_espliteados[8]));
                $campo_dureza.val($campos_espliteados[9]);
                $campo_adherencia.val($convierte_numero_caracter($campos_espliteados[10]));
                $campo_hidrogeno.val($convierte_numero_caracter($campos_espliteados[11]));
                
                $campo_fineza1.val($convierte_numero_caracter($campos_espliteados[12]));
                $campo_viscosidad11.val($convierte_numero_caracter($campos_espliteados[13]));
                $campo_viscosidad21.val($convierte_numero_caracter($campos_espliteados[14]));
                $campo_viscosidad31.val($convierte_numero_caracter($campos_espliteados[15]));
                $campo_densidad1.val($convierte_numero_caracter($campos_espliteados[16]));
                $campo_volatil1.val($convierte_numero_caracter($campos_espliteados[17]));
                $campo_cubriente1.val($convierte_numero_caracter($campos_espliteados[18]));
                $campo_tono1.val($convierte_numero_caracter($campos_espliteados[19]));
                $campo_brillo1.val($convierte_numero_caracter($campos_espliteados[20]));
                $campo_dureza1.val($campos_espliteados[21]);
                $campo_adherencia1.val($convierte_numero_caracter($campos_espliteados[22]));
                $campo_hidrogeno1.val($convierte_numero_caracter($campos_espliteados[23]));
                
                //alert($campos_espliteados[24]);
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_fineza.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == parseInt($campos_espliteados[24])){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'"  selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[-- --]</option>';
                }
                $select_inst_fineza.append($html_subprocesos);
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_viscosidad1.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[25]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_viscosidad1.append($html_subprocesos);
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_viscosidad2.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[26]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_viscosidad2.append($html_subprocesos);
                
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_viscosidad3.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[27]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_viscosidad3.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_densidad.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[28]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_densidad.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_volatil.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[29]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_volatil.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_cubriente.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[30]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_cubriente.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_tono.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[31]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_tono.append($html_subprocesos);
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_brillo.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[32]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_brillo.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_dureza.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[33]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_dureza.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_adherencia.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[34]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_adherencia.append($html_subprocesos);
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_hidrogeno.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[35]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="0"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_hidrogeno.append($html_subprocesos);
                
                
            }else{
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_fineza.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_fineza.append($html_subprocesos);
                
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_viscosidad1.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_viscosidad1.append($html_subprocesos);
                
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_viscosidad2.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_viscosidad2.append($html_subprocesos);
                
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_viscosidad3.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_viscosidad3.append($html_subprocesos);
                
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_densidad.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_densidad.append($html_subprocesos);
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_volatil.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_volatil.append($html_subprocesos);
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_cubriente.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_cubriente.append($html_subprocesos);
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_tono.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_tono.append($html_subprocesos);
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_brillo.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_brillo.append($html_subprocesos);
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_dureza.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_dureza.append($html_subprocesos);
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_adherencia.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_adherencia.append($html_subprocesos);
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_hidrogeno.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_hidrogeno.append($html_subprocesos);
                
            }
            
            $aceptar_acepta_especificacaiones.click(function(event){
               event.preventDefault();
               $valida_result = "";
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_fineza.val(), $campo_fineza1.val(), "Fineza"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_viscosidad1.val(), $campo_viscosidad11.val(), "Viscosidad"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_viscosidad2.val(), $campo_viscosidad21.val(), "Viscosidad"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_viscosidad3.val(), $campo_viscosidad31.val(), "Viscosidad"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_densidad.val(), $campo_densidad1.val(), "Densidad"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_volatil.val(), $campo_volatil1.val(), "No Vol&aacute;tiles"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_cubriente.val(), $campo_cubriente1.val(), "Cubriente"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_tono.val(), $campo_tono1.val(), "Tono"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_brillo.val(), $campo_brillo1.val(), "Brillo"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_adherencia.val(), $campo_adherencia1.val(), "Adherencia"), $valida_result);
               $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_hidrogeno.val(), $campo_hidrogeno1.val(), "pH"), $valida_result);
               
               
               if($valida_result == "true" || $valida_result == "TRUE" || $valida_result == ""){
                    
                    $cadeana_retorno = $convierte_caracter_numero($campo_fineza.val())+"&&&"+$convierte_caracter_numero($campo_viscosidad1.val())+"&&&"+$convierte_caracter_numero($campo_viscosidad2.val())+"&&&";
                    $cadeana_retorno += $convierte_caracter_numero($campo_viscosidad3.val())+"&&&"+$convierte_caracter_numero($campo_densidad.val())+"&&&"+$convierte_caracter_numero($campo_volatil.val())+"&&&"+$convierte_caracter_numero($campo_cubriente.val())+"&&&"+$convierte_caracter_numero($campo_tono.val())+"&&&";
                    $cadeana_retorno += $convierte_caracter_numero($campo_brillo.val())+"&&&"+$campo_dureza.val()+"&&&"+$convierte_caracter_numero($campo_adherencia.val())+"&&&"+$convierte_caracter_numero($campo_hidrogeno.val())+"&&&";
                    
                    $cadeana_retorno += $convierte_caracter_numero($campo_fineza1.val())+"&&&"+$convierte_caracter_numero($campo_viscosidad11.val())+"&&&"+$convierte_caracter_numero($campo_viscosidad21.val())+"&&&";
                    $cadeana_retorno += $convierte_caracter_numero($campo_viscosidad31.val())+"&&&"+$convierte_caracter_numero($campo_densidad1.val())+"&&&"+$convierte_caracter_numero($campo_volatil1.val())+"&&&"+$convierte_caracter_numero($campo_cubriente1.val())+"&&&"+$convierte_caracter_numero($campo_tono1.val())+"&&&";
                    $cadeana_retorno += $convierte_caracter_numero($campo_brillo1.val())+"&&&"+$campo_dureza1.val()+"&&&"+$convierte_caracter_numero($campo_adherencia1.val())+"&&&"+$convierte_caracter_numero($campo_hidrogeno1.val())+"&&&";
                    
                    //for instruments (add instruments at the string)
                    $cadeana_retorno += $select_inst_fineza.val()+"&&&"+$select_inst_viscosidad1.val()+"&&&"+$select_inst_viscosidad2.val()+"&&&";
                    $cadeana_retorno += $select_inst_viscosidad3.val()+"&&&"+$select_inst_densidad.val()+"&&&"+$select_inst_volatil.val()+"&&&";
                    $cadeana_retorno += $select_inst_cubriente.val()+"&&&"+$select_inst_tono.val()+"&&&"+$select_inst_brillo.val()+"&&&";
                    $cadeana_retorno += $select_inst_dureza.val()+"&&&"+$select_inst_adherencia.val()+"&&&"+$select_inst_hidrogeno.val();
                    
                    var remove = function() {$(this).remove();};
                    $('#forma-especificaciones-overlay').fadeOut(remove);
                    $cadena_especificaciones = $cadeana_retorno;
               }else{
                  jAlert($valida_result, 'Atencion!');
               }
               
            });
            
            $cancelar_cencela_especificacaiones.click(function(event){
                event.preventDefault();
                
                $cadena_especificaciones = $cadeana_retorno;
                
                var remove = function() {$(this).remove();};
                $('#forma-especificaciones-overlay').fadeOut(remove);
            });
       }
       
       
       
       /*
        pantalla para agregar datos adicionales, especificaciones y procedimientos
        */
       $llenar_especificaciones_procedidmientos_config = function(id_subp, titulo, especificaciones, procedimientos, accion, pos_edit, id_prod_master,nivel, id_reg, id_formula){
           
           $cadena_especificaciones = especificaciones;
           $cadena_procedimientos = procedimientos;
           
           $(this).modalPanel_adicionalEmergentes();
           var $dialogoc =  $('#forma-adicionalemergentes-window');
           //var $dialogoc.prependTo('#forma-buscaproduct-window');
           $dialogoc.append($('div.panel_opciones_adicionales').find('table.formapanel_opciones_adicionales').clone());
           
           $('#forma-adicionalemergentes-window').css({"margin-left": -150, 	"margin-top": -100});
           
           var $aceptar_add_especificaciones = $('#forma-adicionalemergentes-window').find('#add_especificaciones');
           var $aceptar_add_procedimiento = $('#forma-adicionalemergentes-window').find('#add_procedimiento');
           var $aceptar_add_formulacion = $('#forma-adicionalemergentes-window').find('#add_formulacion');
           
           
           var $aceptar_plugin_opciones_adicionales = $('#forma-adicionalemergentes-window').find('#acepta_opciones_adicionales');
           var $cancelar_plugin_opciones_adicionales = $('#forma-adicionalemergentes-window').find('#cencela_opciones_adicionales');
           
           $cancelar_plugin_opciones_adicionales.mouseover(function(){
               $(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
           });
           
           $cancelar_plugin_opciones_adicionales.mouseout(function(){
               $(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
           });
           
           $cont_click_esp = 0;
           $aceptar_add_especificaciones.click(function(event){
              event.preventDefault();
              if($cont_click_esp == 0 && accion != 'edit'){
                  $plugin_especificaciones(id_subp, "s", $cadena_especificaciones, accion);
              }else{
                  $plugin_especificaciones(id_subp, "s", $cadena_especificaciones, 'edit');
              }
           });
           
           $cont_click_proc = 0;
           $aceptar_add_procedimiento.click(function(event){
              event.preventDefault();
              if($cont_click_proc == 0 && accion != 'edit'){
                  $plugin_procedimiento(id_subp, "s", $cadena_procedimientos,accion);
              }else{
                  $plugin_procedimiento(id_subp, "s", $cadena_procedimientos,'edit');
              }
           });
           
           $aceptar_add_formulacion.click(function(event){
               event.preventDefault();
               $plugin_cargar_formulacion(id_formula);
           });
           
           $aceptar_plugin_opciones_adicionales.click(function(event){
               event.preventDefault();
               if($cadena_especificaciones == "" || $cadena_procedimientos == ""){
                   jAlert("No se puede agregar un subproceso sin especificaciones ni procedimiento", 'Atencion!');
               }else{
                   if(pos_edit == "0"){
                       var remove = function() {$(this).remove();};
                       $('#forma-adicionalemergentes-overlay').fadeOut(remove);
                       //agrega datos a el grid de los subprocesos para el producto
                       $agregar_subprocesos_al_proceso(id_subp, titulo, nivel,"","PDF", "", "", $cadena_especificaciones, $cadena_procedimientos, id_prod_master, 0, id_formula);
                       // id_prod_master,nivel, sku
                   }else{
                       var remove = function() {$(this).remove();};
                       $('#forma-adicionalemergentes-overlay').fadeOut(remove);
                       var $grid_sub_procesos = $('#forma-proconfigproduccion-window').find('#tabla_subprocesos_seleccionados');
                       $grid_sub_procesos.find('tr').find(pos_edit).parent().find('input[name=especificaciones]').val($cadena_especificaciones);
                       $grid_sub_procesos.find('tr').find(pos_edit).parent().find('input[name=procediemientos]').val($cadena_procedimientos);
                   }
               }
           });
           
            $cancelar_plugin_opciones_adicionales.click(function(event){
                event.preventDefault();
                var remove = function() {$(this).remove();};
                $('#forma-adicionalemergentes-overlay').fadeOut(remove);
            });
       }
        
        
        $alimenta_grid_formulaciones = function($tr_count, $id_formula, $sku, $descripcion, $version, proceso_id){
            var $formulas_proceso = $('#forma-proconfigproduccion-window').find('#formulas_porproceso');
            
            //$formulas_proceso.children().remove();
            
            //crea el tr con los datos de las formulas
            trr = '<tr>';
                trr += '<td width="100">';
                    trr += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="editar_' + $id_formula + '" class="editar_item" title="Editar" >';
                    trr += '<span id="img_editar" class="onmouseOutEdit" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> </a>&nbsp;&nbsp;&nbsp;&nbsp;';
                    trr += '<a href="cancelar_' + $id_formula + '" class="cancelar_item" title="Eliminar"> ';
                    trr += '<span id="img_eliminar" class="onmouseOutDelete" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></a> ';
                trr += '</td>';
                trr += '<td width="100">'+$sku+'</td>';
                trr += '<td width="276">'+$descripcion+'&nbsp;&nbsp;&nbsp;&nbsp;Version: '+$version+'</td>';
            trr += '</tr>';
            
            $formulas_proceso.append(trr);
            
            $colorea_tr_grid($formulas_proceso);
            
            $tr_clickeado = $formulas_proceso.find('a[href="editar_'+$id_formula+'"],a[href="cancelar_'+$id_formula+'"]');
            
            $tr_clickeado.click(function(event){
                event.preventDefault();
                $apuntador_tr = $(this).parent().parent();
                if ( $(this).is('.editar_item') ){
                    
                    var llave = $(this).attr('href').split('_')[1];
                    
                    $plugin_cargar_formulacion(llave);
                };
                if ( $(this).is('.cancelar_item') ){
                    
                    var llave = $(this).attr('href').split('_')[1];
                    
                    var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDeleteFormula.json';
                    $arreglo = {'id':llave,
								'proceso_id':proceso_id,
                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                };
                    jConfirm('Realmente desea eliminar la formula seleccionada', 'Dialogo de confirmacion', function(r) {
                        if (r){
                            $.post(input_json,$arreglo,function(entry){
                                if ( entry['success'] == '1' ){
									
									//Cerrar la ventana actual de configuracion
									var remove = function() {$(this).remove();};
									$('#forma-proconfigproduccion-overlay').fadeOut(remove);
									
									//Actualizar el grid para que desaparezca la configuracion eliminada
									$get_datos_grid();
									
									//Mostrar mensaje de confirmacion de configuracion eliminada
                                    jAlert("La formula fue eliminada exitosamente", 'Atencion!');
                                }
                                else{
                                    jAlert(entry['success'], 'Atencion!');
                                }
                            },"json");
                        }
                    });
                    
                };
                Elastic.reset(document.getElementById('lienzo_recalculable'));
            });
            
        }
        
        
        $alimenta_grid_subprocesos = function(){
            
            var $sub_procesos = $('#forma-proconfigproduccion-window').find('#sub_procesos');
            
            $sub_procesos.children().remove();
            
            //crea el tr con los datos del producto seleccionado
            $.each(array_subprocesos_disponibles,function(entryIndex,pres){
                trr = '<tr>';
                    trr += '<td width="370" class="grid1" >';
                       trr += '<span class="id_subp" style="display:none">'+pres['id']+'</span>';
                    trr += '<span class="titulo">'+pres['titulo']+'</span></td>';
                trr += '</tr>';
                $sub_procesos.append(trr);
                
            });
            $colorea_tr_grid($sub_procesos);
            
            //seleccionar un subproceso del grid de subprocesos para el producto
            $sub_procesos.find('tr').click(function(){
                
                //llamada a la funcion que busca y agrega producto al grid, se le pasa como parametro el lote y el almacen
                //$agrega_producto_grid($(this).find('span.lote').html(),$(this).find('input.idalmacen').val());
                var id_subp = $(this).find('span.id_subp').html();
                var titulo = $(this).find('span.titulo').html();
                var $id_producto = $('#forma-proconfigproduccion-window').find('input[name=id_producto]');
                
                
                
                procs_seleccionado = false;
                var $grid_sub_procesos = $('#forma-proconfigproduccion-window').find('#tabla_subprocesos_seleccionados');
                $grid_sub_procesos.find('tr').each(function(){
                    if(parseInt($(this).find('input[name=id_subproceso_grid]').val()) == parseInt(id_subp)){
                        procs_seleccionado = true;
                    }
                });
                
                if(procs_seleccionado){
                    jConfirm('Ya Existe '+titulo+'  desea agregarlo?', 'Dialogo de confirmacion', function(r) {
                        if (r){
                            $(this).modalPanel_Listaformulas();
                            var $dialogoc1 =  $('#forma-listaformulas-window');
                            //var $dialogoc.prependTo('#forma-buscaproduct-window');
                            $dialogoc1.append($('div.panel_listaformulas').find('table.formapanel_listaformulas').clone());

                            $('#forma-listaformulas-window').css({"margin-left": -150, 	"margin-top": -100});

                            $('#forma-listaformulas-window').find('.titulo_subproceso_seleccionado').text("Subproceso seleccionado:  "+titulo);

                            var $tabla_resultados_formulas = $('#forma-listaformulas-window').find('#tabla_resultado_formulas');    

                            var $cancela_lista_formulas = $('#forma-listaformulas-window').find('#cencela_listaformulas');
                            var $crear_nueva_formula_en_listaformulas = $('#forma-listaformulas-window').find('#crear_nueva_formula_en_listaformulas')

                            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_formulas_for_this_producto.json';
                            $arreglo = {'id_prod':$id_producto.val(),
                                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                            };
                            //$.getJSON(json_string,function(entry){
                            $.post(input_json,$arreglo,function(entry){
                                $.each(entry,function(entryIndex,form){
                                    
                                    trr = '<tr>';
                                        trr += '<td width="100">';
                                        trr += '<span class="id_formula" style="display:none">'+form['id']+'</span>';
                                        trr += '<span class="inv_prod_id_master" style="display:none">'+form['inv_prod_id']+'</span>';
                                        trr += '</td>';
                                        trr += '<td width="80"> <span class="sku" >'+form['sku']+'</span></td>';
                                        trr += '<td width="390"><span class="descripcion" >'+form['descripcion']+'</span>&nbsp;&nbsp;&nbsp;';
                                            trr += '<span class="version" >'+form['version']+'</span>';
                                        trr += '</td>';
                                    trr += '</tr>';
                                    
                                    $tabla_resultados_formulas.append(trr);
                                    
                                    $colorea_tr_grid($tabla_resultados_formulas);
                                    
                                    
                                    $tabla_resultados_formulas.find('tr').click(function(){

                                        var $grid_sub_procesos = $('#forma-proconfigproduccion-window').find('#tabla_subprocesos_seleccionados');
                                        $grid_sub_procesos.find('tbody').find('tr').each(function(){
                                            //alert($(this).html());
                                        });

                                        var id_prod_master_ = $(this).find('span.inv_prod_id_master').html();
                                        //var nivel_ = $(this).find('span.nivel').html();
                                        var sku_ = $(this).find('span.sku').html();
                                        var id_formula_ = $(this).find('span.id_formula').html();
                                        nivel_ = 1;

                                        $llenar_especificaciones_procedidmientos_config(id_subp, titulo, "","", 'new', '0', id_prod_master_, nivel_, 0, id_formula_);

                                         var remove = function() {$(this).remove();};
                                        $('#forma-listaformulas-overlay').fadeOut(remove);
                                    });
                                });
                            },"json");//termina llamada json

                            $crear_nueva_formula_en_listaformulas.click(function(event){
                                event.preventDefault();
                                $plugin_cargar_formulacion(0);
                            });

                            $cancela_lista_formulas.click(function(event){
                                event.preventDefault();
                                var remove = function() {$(this).remove();};
                                $('#forma-listaformulas-overlay').fadeOut(remove);
                            });
                        }
                    });
                    
                }else{
                    $(this).modalPanel_Listaformulas();
                    var $dialogoc1 =  $('#forma-listaformulas-window');
                    //var $dialogoc.prependTo('#forma-buscaproduct-window');
                    $dialogoc1.append($('div.panel_listaformulas').find('table.formapanel_listaformulas').clone());

                    $('#forma-listaformulas-window').css({"margin-left": -150, 	"margin-top": -100});

                    $('#forma-listaformulas-window').find('.titulo_subproceso_seleccionado').text("Subproceso seleccionado:  "+titulo);

                    var $tabla_resultados_formulas = $('#forma-listaformulas-window').find('#tabla_resultado_formulas');    

                    var $cancela_lista_formulas = $('#forma-listaformulas-window').find('#cencela_listaformulas');
                    var $crear_nueva_formula_en_listaformulas = $('#forma-listaformulas-window').find('#crear_nueva_formula_en_listaformulas')

                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_formulas_for_this_producto.json';
                    $arreglo = {'id_prod':$id_producto.val(),
                                    'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                    };
                    //$.getJSON(json_string,function(entry){
                    contador = 0;
                    $.post(input_json,$arreglo,function(entry){
                        $.each(entry,function(entryIndex,form){
                            
                            trr = '<tr class="clickmetr'+contador+'">';
                                trr += '<td width="100">';
                                trr += '<span class="id_formula" style="display:none">'+form['id']+'</span>';
                                trr += '<span class="inv_prod_id_master" style="display:none">'+form['inv_prod_id']+'</span>';
                                trr += '</td>';
                                trr += '<td width="80"> <span class="sku" >'+form['sku']+'</span></td>';
                                trr += '<td width="80"> <span class="sku" >'+form['sku']+'</span></td>';
                                trr += '<td width="390"><span class="descripcion" >'+form['descripcion']+'</span>&nbsp;&nbsp;&nbsp;';
                                    trr += '<span class="version" >'+form['version']+'</span>';
                                trr += '</td>';
                            trr += '</tr>';
                            
                            $tabla_resultados_formulas.append(trr);
                            
                            $colorea_tr_grid($tabla_resultados_formulas);
                            
                            //$tabla_resultados_formulas.find('tbody > tr').each(function (index){
                            value_plugin = 0;
                            $tabla_resultados_formulas.find('.clickmetr'+contador).bind('click', function(event){
                                event.preventDefault();
                                if(value_plugin == 0){
                                
                                    value_plugin = 1;
                                    var $grid_sub_procesos = $('#forma-proconfigproduccion-window').find('#tabla_subprocesos_seleccionados');
                                    /*
                                    $grid_sub_procesos.find('tbody').find('tr').each(function(){
                                        //alert($(this).html());
                                    });
                                    */

                                    var id_prod_master_ = $(this).find('span.inv_prod_id_master').html();
                                    //var nivel_ = $(this).find('span.nivel').html();
                                    var sku_ = $(this).find('span.sku').html();
                                    var id_formula_ = $(this).find('span.id_formula').html();
                                    nivel_ = 1;

                                    $llenar_especificaciones_procedidmientos_config(id_subp, titulo, "","", 'new', '0', id_prod_master_, nivel_, 0, id_formula_);

                                    var remove = function() {$(this).remove();};
                                    $('#forma-listaformulas-overlay').fadeOut(remove);
                                }
                            });
                        });
                        contador++;
                    },"json");//termina llamada json
                    
                    $crear_nueva_formula_en_listaformulas.click(function(event){
                        event.preventDefault();
                        $plugin_cargar_formulacion(0);
                    });
                    
                    $cancela_lista_formulas.click(function(event){
                        event.preventDefault();
                        var remove = function() {$(this).remove();};
                        $('#forma-listaformulas-overlay').fadeOut(remove);
                    });
                }
                
            });
        }
	
	
	
	
	//nueva entrada
	$new_entrada.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_ProConfigProduccion();
		
		var form_to_show = 'formaProConfigproduccion00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-proconfigproduccion-window').css({"margin-left": -375, "margin-top": -230});
		
		$forma_selected.prependTo('#forma-proconfigproduccion-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_datos_configuracion_produccion.json';
		$arreglo = {'id_proceso':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
                
		var $id_proceso = $('#forma-proconfigproduccion-window').find('input[name=id_proceso]');
		var $titulo = $('#forma-proconfigproduccion-window').find('input[name=titulo]');
		var $id_producto = $('#forma-proconfigproduccion-window').find('input[name=id_producto]');
		var $sku = $('#forma-proconfigproduccion-window').find('input[name=sku]');
		var $descripcion = $('#forma-proconfigproduccion-window').find('input[name=descripcion]');
		var $dias_caducidad = $('#forma-proconfigproduccion-window').find('input[name=dias_caducidad]');
		
		//grids
		var $sub_procesos = $('#forma-proconfigproduccion-window').find('#sub_procesos');
		var $tabla_subprocesos_seleccionados = $('#forma-proconfigproduccion-window').find('#tabla_subprocesos_seleccionados');
                
		//href para buscar producto
		var $buscar_producto = $('#forma-proconfigproduccion-window').find('a[href*=busca_producto]');
		//href para agregar producto al grid
		
		var $cancelar_proceso = $('#forma-proconfigproduccion-window').find('#cancela_entrada');
		
		var $cerrar_plugin = $('#forma-proconfigproduccion-window').find('#close');
		var $cancelar_plugin = $('#forma-proconfigproduccion-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-proconfigproduccion-window').find('#submit');
		
		$id_proceso.val(0);
		//$campo_factura.css({'background' : '#ffffff'});
		//$cancelar_entrada.hide();
		//$pdf_entrada.hide();
		
		var respuestaProcesada = function(data){
                    if ( data['success'] == "true" ){
                        jAlert("El proceso se ha dado de alta", 'Atencion!');
                        var remove = function() {$(this).remove();};
                        $('#forma-proconfigproduccion-overlay').fadeOut(remove);
                        $get_datos_grid();
                    }else{
                        // Desaparece todas las interrogaciones si es que existen
                        $('#forma-proconfigproduccion-window').find('div.interrogacion').css({'display':'none'});
                        //$grid_productos.find('#cost').css({'background' : '#ffffff'});
                        //$grid_productos.find('#cant').css({'background' : '#ffffff'});
                        //$grid_productos.find('#cad').css({'background' : '#ffffff'});

                        $('#forma-proconfigproduccion-window').find('#div_warning_grid').css({'display':'none'});
                        $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').children().remove();


                        var valor = data['success'].split('___');
                        //muestra las interrogaciones
                        for (var element in valor){
                                tmp = data['success'].split('___')[element];
                                longitud = tmp.split(':');

                                if( longitud.length > 1 ){
                                        $('#forma-proconfigproduccion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                        .parent()
                                        .css({'display':'block'})
                                        .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

                                        //alert(tmp.split(':')[0]);
                                        /*
                                        if(parseInt($("tr", $grid_productos).size())>0){
                                                for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
                                                        if((tmp.split(':')[0]=='costo'+i) || (tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='caducidad'+i)){
                                                                //alert(tmp.split(':')[0]);
                                                                $('#forma-proconfigproduccion-window').find('#div_warning_grid').css({'display':'block'});

                                                                if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
                                                                        $grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                }
                                                                if(tmp.split(':')[0].substring(0, 5) == 'costo'){
                                                                        $grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                }
                                                                if(tmp.split(':')[0].substring(0, 9) == 'caducidad'){
                                                                        $grid_productos.find('input[name=caducidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                }

                                                                //$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
                                                                //$grid_productos.find('select[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});

                                                                var tr_warning = '<tr>';
                                                                                tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
                                                                                tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $grid_productos.find('input[name=sku' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
                                                                                tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $grid_productos.find('input[name=titulo' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
                                                                                tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:285px; color:red"></td>';
                                                                tr_warning += '</tr>';
                                                                $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
                                                        }
                                                }
                                        }*/
                                }
                        }

                        $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
                        $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({'background-color' : '#e7e8ea'});			
                        
                    }
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
                
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			array_subprocesos_disponibles = entry['SubProcesos'];
			array_maquinas = entry['Maquinas'];
			array_instrumentos = entry['Instrumentos'];
			array_subprocesos_seleccionados = null;
			
			$alimenta_grid_subprocesos();
		},"json");//termina llamada json
		
		
		//validar campo dias de caducidad, solo acepte numeros y punto
		$dias_caducidad.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB
			if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		$dias_caducidad.focus(function(){
			if ($(this).val().trim()=='' || parseFloat($(this).val())==0){
				$(this).val('');
			}
		});
		
		$dias_caducidad.blur(function(){
			if ($(this).val().trim() == ''){
				$(this).val(1);
			}
		});
		
		//buscar producto
		$buscar_producto.click(function(event){
			event.preventDefault();
			$busca_productos(1);
		});
                
		/*
		$submit_actualizar.bind('click',function(){
                    var trCount = $("tr", $grid_productos).size();
                    $total_tr.val(trCount);
                    if(parseInt(trCount) > 0){
                        $grid_productos.find('tr').each(function (index){
                            if($(this).find('#cad').val() == '' ){
                                    $(this).find('#cad').val(' ');
                            }
                        });
                        return true;
                    }else{
                        jAlert("No hay datos para actualizar", 'Atencion!');
                        return false;
                    }
		});
		*/
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-proconfigproduccion-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-proconfigproduccion-overlay').fadeOut(remove);
		});
                
		
	});
	
	
	
	var carga_formaProConfigproduccion0000_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
            if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'no_entrada':id_to_show,
                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                            };
                    jConfirm('Realmente desea eliminar el proceso seleccionado?', 'Dialogo de confirmacion', function(r) {
                        if (r){
                            $.post(input_json,$arreglo,function(entry){
                                if ( entry['success'] == '1' ){
                                    jAlert("El proceso fue eliminado exitosamente", 'Atencion!');
                                    $get_datos_grid();
                                }
                                else{
                                    jAlert("El proceso no pudo ser eliminado", 'Atencion!');
                                }
                            },"json");
                        }
                    });
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaProConfigproduccion00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			//var accion = "get_datos_entrada_mercancia";
			
			$(this).modalPanel_ProConfigProduccion();
			
			$('#forma-proconfigproduccion-window').css({"margin-left": -375, 	"margin-top": -230});
			
			$forma_selected.prependTo('#forma-proconfigproduccion-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_datos_configuracion_produccion.json';
				$arreglo = {'id_proceso':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var $id_proceso = $('#forma-proconfigproduccion-window').find('input[name=id_proceso]');
				var $titulo = $('#forma-proconfigproduccion-window').find('input[name=titulo]');
				var $id_producto = $('#forma-proconfigproduccion-window').find('input[name=id_producto]');
				var $sku = $('#forma-proconfigproduccion-window').find('input[name=sku]');
				var $descripcion = $('#forma-proconfigproduccion-window').find('input[name=descripcion]');
				var $dias_caducidad = $('#forma-proconfigproduccion-window').find('input[name=dias_caducidad]');
				
				//grids
				var $sub_procesos = $('#forma-proconfigproduccion-window').find('#sub_procesos');
				var $tabla_subprocesos_seleccionados = $('#forma-proconfigproduccion-window').find('#tabla_subprocesos_seleccionados');
				
				//href para buscar producto
				var $buscar_producto = $('#forma-proconfigproduccion-window').find('a[href*=busca_producto]');
				//href para agregar producto al grid
				
				var $cancelar_proceso = $('#forma-proconfigproduccion-window').find('#cancela_entrada');
				
				var $cerrar_plugin = $('#forma-proconfigproduccion-window').find('#close');
				var $cancelar_plugin = $('#forma-proconfigproduccion-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-proconfigproduccion-window').find('#submit');
                                
				
				$sku.attr("readonly", true);
				//$titulo.attr("readonly", true);
				$descripcion.attr("readonly", true);
				
				$buscar_producto.hide();
				$id_proceso.val(id_to_show);
                                
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("El proceso se guardo con exito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-proconfigproduccion-overlay').fadeOut(remove);
						$get_datos_grid();
					}else{
                                            /*
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-proconfigproduccion-window').find('div.interrogacion').css({'display':'none'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cad').css({'background' : '#ffffff'});

						$('#forma-proconfigproduccion-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
								
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-proconfigproduccion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='costo'+i) || (tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='caducidad'+i)){

											$('#forma-proconfigproduccion-window').find('#div_warning_grid').css({'display':'block'});
											//$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
											
											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
												$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											if(tmp.split(':')[0].substring(0, 5) == 'costo'){
												$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											if(tmp.split(':')[0].substring(0, 9) == 'caducidad'){
												$grid_productos.find('input[name=caducidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											/*
											var tr_warning = '<tr>';
												tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
												tr_warning += '<td width="120">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="200">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=titulo' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="235">';
												tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:230px; color:red">';
												tr_warning += '</td>';
											tr_warning += '</tr>';
											*/
											/*
											var tr_warning = '<tr>';
													tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
													tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $grid_productos.find('input[name=sku' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
													tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $grid_productos.find('input[name=titulo' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
													tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:285px; color:red"></td>';
											tr_warning += '</tr>';
											
											$('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
										}
										
									}
								}
							}
						}
						$('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
						$('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
                                                */
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$id_proceso.attr({'value' : entry['Proceso']['0']['id']});
					$titulo.attr({'value' : entry['Proceso']['0']['titulo']});
					$id_producto.attr({'value' : entry['Proceso']['0']['inv_prod_id']});
					$sku.attr({'value' : entry['Proceso']['0']['sku']});
					$descripcion.attr({'value' : entry['Proceso']['0']['descripcion']});
					$dias_caducidad.attr({'value' : entry['Proceso']['0']['dias_caducidad']});
					
					array_subprocesos_disponibles = entry['SubProcesos'];
					array_maquinas = entry['Maquinas'];
					array_instrumentos = entry['Instrumentos'];
					array_subprocesos_seleccionados = null;
					
					$alimenta_grid_subprocesos();
					
					//llena el grid de las formulas
					if(entry['getAllFormulas'] != null){
						$.each(entry['getAllFormulas'],function(entryIndex,formula){
							$alimenta_grid_formulaciones(0, formula['id'], formula['codigo'], formula['descripcion'], formula['version'], $id_proceso.val());
						});
					}else{
						//aqui ira el buscador de formulas
					}
					
					if(entry['datosSubProProd'] != null){
						
						$.each(entry['datosSubProProd'],function(entryIndex,prodSubProceso){
							
							//$agregar_subprocesos_al_proceso = function(id_subp, titulo, nivel, 
							//maquina, doc_calidad, empleados, metadata, especificaciones, procedimiento,id_master, id_reg);
							//$cadena_procedimientos
							//$cadena_especificaciones
							
							$cadena_especificaciones = "";
							$.each(entry['datosSubProEspecificaciones'],function(entryIndex,proEsp){
							   //prodSubProceso['id']
							   
							   if(prodSubProceso['id'] == proEsp['pro_subproceso_prod_id']){
								   $cadena_especificaciones = proEsp['fineza_inicial']+"&&&"+proEsp['viscosidads_inicial']+"&&&"+proEsp['viscosidadku_inicial']+"&&&";
								   $cadena_especificaciones += proEsp['viscosidadcps_inicial']+"&&&"+proEsp['densidad_inicial']+"&&&"+proEsp['volatiles_inicial']+"&&&"+proEsp['cubriente_inicial']+"&&&"+proEsp['tono_inicial']+"&&&";
								   $cadena_especificaciones += proEsp['brillo_inicial']+"&&&"+proEsp['dureza_inicial']+"&&&"+proEsp['adherencia_inicial']+"&&&"+proEsp['hidrogeno_inicial']+"&&&";
								   
								   $cadena_especificaciones += proEsp['fineza_final']+"&&&"+proEsp['viscosidads_final']+"&&&"+proEsp['viscosidadku_final']+"&&&";
								   $cadena_especificaciones += proEsp['viscosidadcps_final']+"&&&"+proEsp['densidad_final']+"&&&"+proEsp['volatiles_final']+"&&&"+proEsp['cubriente_final']+"&&&"+proEsp['tono_final']+"&&&";
								   $cadena_especificaciones += proEsp['brillo_final']+"&&&"+proEsp['dureza_final']+"&&&"+proEsp['adherencia_final']+"&&&"+proEsp['hidrogeno_final']+"&&&";
								   
								   
								   //$cadena_especificaciones += proEsp['fineza_final']+"&&&"+proEsp['viscosidads_final']+"&&&"+proEsp['viscosidadku_final']+"&&&";
								   //$cadena_especificaciones += proEsp['viscosidadcps_final']+"&&&"+proEsp['densidad_final']+"&&&"+proEsp['volatiles_final']+"&&&"+proEsp['cubriente_final']+"&&&"+proEsp['tono_final']+"&&&";
								   //$cadena_especificaciones += proEsp['brillo_final']+"&&&"+proEsp['dureza_final']+"&&&"+proEsp['adherencia_final']+"&&&"+proEsp['hidrogeno_final']+"&&&";
								   
								   //for instruments (add instruments at the string)
								   $cadena_especificaciones += proEsp['pro_instrumentos_fineza']+"&&&"+proEsp['pro_instrumentos_viscosidad1']+"&&&"+proEsp['pro_instrumentos_viscosidad2']+"&&&";
								   $cadena_especificaciones += proEsp['pro_instrumentos_viscosidad3']+"&&&"+proEsp['pro_instrumentos_densidad']+"&&&"+proEsp['pro_instrumentos_volatil']+"&&&";
								   $cadena_especificaciones += proEsp['pro_instrumentos_cubriente']+"&&&"+proEsp['pro_instrumentos_tono']+"&&&"+proEsp['pro_instrumentos_brillo']+"&&&";
								   $cadena_especificaciones += proEsp['pro_instrumentos_dureza']+"&&&"+proEsp['pro_instrumentos_adherencia']+"&&&"+proEsp['pro_instrumentos_hidrogeno']+"&&&";
								   
							   }
							   //pro_subproceso_prod_id
							});
							
							$cadena_procedimientos = "";
							$.each(entry['datosSubProProcedimientos'],function(entryIndex,proProc){
							   /*prodSubProceso['id'] 
							   posicion
							   descripcion
							   inv_prod_id
							   pro_subproceso_prod_id
							   */
							  if(prodSubProceso['id'] == proProc['pro_subproceso_prod_id']){
								  $cadena_procedimientos += proProc['posicion']+"&&&"+proProc['descripcion']+"$$$";
							  }
							});
							
							titulo_subp = "";
							$.each(entry['SubProcesos'], function(entryIndex, subproceso){
								if(prodSubProceso['pro_subprocesos_id'] == subproceso['id']){
								  titulo_subp = subproceso['titulo'];
								}
							});
							
							$agregar_subprocesos_al_proceso(prodSubProceso['pro_subprocesos_id'], titulo_subp, prodSubProceso['nivel'],prodSubProceso['pro_tipo_equipo_id'],prodSubProceso['documento_calidad'],"", "", $cadena_especificaciones, $cadena_procedimientos, prodSubProceso['inv_prod_id'],prodSubProceso['id'], prodSubProceso['pro_estruc_id']);
						});
						
					};
                                    	
				},"json");//termina llamada json
				
				
				//validar campo costo, solo acepte numeros y punto
				$dias_caducidad.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB
					if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				
				$dias_caducidad.focus(function(){
					if ($(this).val().trim()=='' || parseFloat($(this).val())==0){
						$(this).val('');
					}
				});
				
				
				$dias_caducidad.blur(function(){
					if ($(this).val().trim() == ''){
						$(this).val(1);
					}
				});
				
				
				/*
				
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
                                        
					$grid_productos.find('tr').each(function (index){
						if($(this).find('#cad').val() == '' ){
							$(this).find('#cad').val(' ');
						}
					});
                                        
					return true;
				});
                                */
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-proconfigproduccion-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-proconfigproduccion-overlay').fadeOut(remove);
				});
                                
				
			}
		}
	}
        
        
        
        
	$get_datos_grid = function(){
            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_all_proformulaciones.json';
            
            var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
            
            $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/get_all_proformulaciones.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
            
            $.post(input_json,$arreglo,function(data){
                //pinta_grid
                //$.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaEntradamercancias00_for_datagrid00);

                //aqui se utiliza el mismo datagrid que prefacturas. Solo muesta icono de detalles, el de eliminar No
                $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaProConfigproduccion0000_for_datagrid00);

                //resetea elastic, despues de pintar el grid y el slider
                Elastic.reset(document.getElementById('lienzo_recalculable'));
            },"json");
	}
	
    $get_datos_grid();
});



