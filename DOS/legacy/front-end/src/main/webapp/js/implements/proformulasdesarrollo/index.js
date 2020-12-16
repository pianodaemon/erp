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
	var controller = $contextpath.val()+"/controllers/proformulasdesarrollo";
        
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de formulas en desarrollo');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
        
        
	var $cadena_busqueda = "";
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=descripcion_buscador]');
        var $sku_buscador = $('#barra_buscador').find('.tabla_buscador').find('input[name=sku_buscador]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
        
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "descripcion_buscador" + signo_separador + $busqueda_descripcion.val() + "|";
                valor_retorno += "sku_buscador" + signo_separador + $sku_buscador.val() + "|";
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
	
        
        //buscador de  de versiones de formulas
	$busca_versiones_formulas = function(tipo_busqueda){
            sku_buscar = "";
            
            //limpiar_campos_grids();
            $(this).modalPanel_Buscaversionesformulas();
            var $dialogoc =  $('#forma-versionesformulas-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.buscador_versionesformulas').find('table.formaBusqueda_versionesformulas').clone());
            
            $('#forma-versionesformulas-window').css({"margin-left": -200, 	"margin-top": -200});
            
            var $tabla_resultados = $('#forma-versionesformulas-window').find('#tabla_resultado');

            var $campo_sku = $('#forma-versionesformulas-window').find('input[name=campo_sku]');
            //var $select_tipo_producto = $('#forma-versionesformulas-window').find('select[name=tipo_producto]');
            var $campo_descripcion = $('#forma-versionesformulas-window').find('input[name=campo_descripcion]');

            var $buscar_plugin_producto = $('#forma-versionesformulas-window').find('#busca_producto_modalbox');
            var $cancelar_plugin_busca_producto = $('#forma-versionesformulas-window').find('#cencela');

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
            
            $campo_sku.val(sku_buscar);
            
            //click buscar productos
            $buscar_plugin_producto.click(function(event){
                    event.preventDefault();
                    
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_versione_formulasdesarrollo.json';
                    $arreglo = {'sku':$campo_sku.val(),
                                    //'tipo':$select_tipo_producto.val(),
                                    'descripcion':$campo_descripcion.val(),
                                    'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                }
                    var trr = '';
                    $tabla_resultados.children().remove();
                    $.post(input_json,$arreglo,function(entry){
                        
                            $.each(entry['formulas'],function(entryIndex,formula){
                                trr = '<tr>';
                                    trr += '<td width="120">';
                                            trr += '<span class="sku_prod_buscador">'+formula['sku']+'</span>';
                                            trr += '<input type="hidden" id="version_selected" value="'+formula['version']+'">';
                                            trr += '<input type="hidden" id="id_formula_selected" value="'+formula['id']+'">';
                                            trr += '<input type="hidden" id="inv_prod_id_selected" value="'+formula['inv_prod_id']+'">';
                                    trr += '</td>';
                                    trr += '<td width="480">'+formula['descripcion']+'</td>';
                                    trr += '<td width="90">'+formula['version']+'</td>';
                                trr += '</tr>';
                                $tabla_resultados.append(trr);
                            });
                            
                            $colorea_tr_grid($tabla_resultados);
                            
                            //seleccionar un producto del grid de resultados
                            $tabla_resultados.find('tr').click(function(){
                                var version_selected=$(this).find('#version_selected').val();
                                var id_formula_selected=$(this).find('#id_formula_selected').val();
                                var inv_prod_id_selected=$(this).find('#inv_prod_id_selected').val();
                                
                                //para buscar un producto master
                                if(tipo_busqueda ==1){
                                    //colocando los datos elegidos en el pluguin
                                    $('#forma-proformulasdesarrollo-window').find('input[name=pro_config_prod_pertenece_id]').val(id_formula_selected);
                                    //fin del primer plugin
                                    
                                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_productos_formuladesarrollo.json';
                                    $arreglo = {
                                                    'id_formuladesarrollo':id_formula_selected,
                                                    'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                                }
                                    
                                    $.post(input_json,$arreglo,function(data){
                                        //Formulas_DatosMinigrid.
                                        $.each(data['productos'],function(entryIndex,elemento){
                                            //$('#forma-proformulasdesarrollo-window').find('input[name=version]').val(version_selected);
                                            var $grid_productos_componentes     = $('#forma-proformulasdesarrollo-window').find('div.div_formulaciones');
                                            valida_porciento_tmp = $verifica_porcentaje($grid_productos_componentes, 'valida');
                                            if( valida_porciento_tmp==true){
                                                
                                                //convierte la verdsion de la formula a entero y le suma uno
                                                version_tmp= (parseInt(elemento['version']) + 1);
                                                //alert(elemento['version']);
                                                $('#forma-proformulasdesarrollo-window').find('input[name=version]').val(version_tmp);
                                                
                                                $add_grid_componente_formulaciones(elemento['id'],elemento['inv_prod_id'],elemento['codigo'],elemento['descripcion'],elemento['cantidad'],elemento['elemento'] );
                                                
                                            }else{
                                                if(valida_porciento_tmp == false){
                                                    jAlert("Ya tiene el 100 % de la configuracion", 'Atencion!');
                                                }else{
                                                    jAlert("Ingrese una cantidad diferente de cero", 'Atencion!');
                                                }
                                            }
                                            //sum_porciento += parseFloat(elemento['cantidad']);
                                            ///Esto es para el numero de paso
                                            //$numero_pasos.val(elemento['nivel']);
                                            //$paso_actual.val(elemento['nivel']);
                                        }); 
                                    });
                                }
                                
                                //elimina la ventana de busqueda
                                var remove = function() {$(this).remove();};
                                $('#forma-versionesformulas-overlay').fadeOut(remove);
                                //asignar el enfoque al campo sku del producto
                            });
                        });
                    });
                    
            //}); // comentado, por que en las formulas no se toma en cuenta el tipo de producto

            //si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
            if($campo_sku.val() != ''){
                    $buscar_plugin_producto.trigger('click');
            }

            $cancelar_plugin_busca_producto.click(function(event){
                //event.preventDefault();
                var remove = function() {$(this).remove();};
                $('#forma-versionesformulas-overlay').fadeOut(remove);
            });
                
	}//termina buscador de versiones de formulas
        
	
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
            
            //$tipos_productos_array = new Array();
            
            //buscar todos los tipos de productos
            var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
            $arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
            $.post(input_json_tipos,$arreglo,function(data){
                
                //Llena el select tipos de productos en el buscador
                $select_tipo_producto.children().remove();
                //<option value="0" selected="yes">[--Seleccionar Tipo--]</option>
                var prod_tipos_html = '';
                $tipos_productos_array = data['prodTipos'];
                $.each(data['prodTipos'],function(entryIndex,pt){
                    
                    //buscador principal de el proceso
                    if(tipo_busqueda == 1){
                        if(pt['id'] == 2 || pt['id'] == 1 || pt['id'] == 8){
                            prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                        }
                    }
                    //para buscar un producto master (solo puede b uscar productos de laboratorio)
                    if(tipo_busqueda == 2){
                        if(pt['id'] == 8 ){
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
                    
                    //para buscar el producto de salida (solo puede buscar productos de laboratorio)
                    if(tipo_busqueda == 4){
                        if(pt['id'] == 8 ){
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
                                    trr += '<td width="90">';
                                        trr += '<span class="tipo_id_prod_buscador">'+producto['tipo_id']+'</span>';
                                    trr += '</td>';
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
                                var tipo_id_prod_buscador=$(this).find('span.tipo_id_prod_buscador').html();
                                
                                /*
                                //La opcion tipo_busqueda = 1 , por el momento no se utiliza 
                                //buscador principal de el proceso
                                if(tipo_busqueda == 1){
                                    //asignar a los campos correspondientes el sku y y descripcion
                                    $('#forma-proconfigproduccion-window').find('input[name=id_producto]').val(id_prod);
                                    $('#forma-proconfigproduccion-window').find('input[name=sku]').val(codigo);
                                    $('#forma-proconfigproduccion-window').find('input[name=descripcion]').val(descripcion);
                                    $('#forma-proconfigproduccion-window').find('input[name=sku_producto]').focus();
                                }
                                */
                               
                                //para buscar un producto master
                                if(tipo_busqueda ==2){
                                    //colocando los datos elegidos en el pluguin
                                    $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_master]').val(id_prod);
                                    $('#forma-proformulasdesarrollo-window').find('input[name=codigo_master]').val(codigo);
                                    $('#forma-proformulasdesarrollo-window').find('input[name=descripcion_master]').val(descripcion);
                                    $select_prodtipo = $('#forma-proformulasdesarrollo-window').find('select[name=select_prodtipo]');
                                    $('#forma-proformulasdesarrollo-window').find('input[name=select_unidad]').val(unidad);
                                    
                                    $select_prodtipo.children().remove();
                                    var prod_tipos_html = '';
                                    
                                    $.each($tipos_productos_array,function(entryIndex,tipo){
                                        if(tipo['id'] == tipo_id_prod_buscador){
                                            prod_tipos_html += '<option value="' + tipo['id'] + '" selected="yes" >' + tipo['titulo'] + '</option>';
                                        }else{
                                            prod_tipos_html += '<option value="' + tipo['id'] + '" >' + tipo['titulo'] + '</option>';
                                        }
                                    });
                                    
                                    $select_prodtipo.append(prod_tipos_html);
                                    //fin del primer plugin
                                }
                                
                                //para buscar elementos de el proceso
                                if(tipo_busqueda ==3){
                                   //guardando los datos del pluguin si es que ligio buscar elementos para el grid
                                    $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_entrante]').val(id_prod);
                                    $('#forma-proformulasdesarrollo-window').find('input[name=codigo_producto_minigrid]').val(codigo);
                                    $('#forma-proformulasdesarrollo-window').find('input[name=descr_producto_minigrid]').val(descripcion);

                                }
                                
                                //para buscar el producto de salida
                                if(tipo_busqueda ==4){
                                    //guardando los datos del pluguin si es que ligio buscar elementos para el grid
                                    $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_saliente]').val(id_prod);
                                    $('#forma-proformulasdesarrollo-window').find('input[name=codigo_producto_saliente]').val(codigo);
                                    $('#forma-proformulasdesarrollo-window').find('input[name=descr_producto_saliente]').val(descripcion);
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
        
    
    //agrega producto al grid
    $agrega_producto_ingrediente = function(){
        var $cantidad_calculo              = $('#forma-proformulasdesarrollo-window').find('input[name=cantidad_calculo]');
        //var $select_prod_tipo             = $('#forma-formulas-window').find('select[name=select_prodtipo]');
        var $select_prod_tipo               = $('#forma-proformulasdesarrollo-window').find('input[name=select_prodtipo]');
        var $select_unidad                  = $('#forma-proformulasdesarrollo-window').find('input[name=select_unidad]');

        var $codigo_producto_minigrid       = $('#forma-proformulasdesarrollo-window').find('input[name=codigo_producto_minigrid]');
        var $grid_productos_componentes     = $('#forma-proformulasdesarrollo-window').find('div.div_formulaciones');


        var $total_porcentaje = $('#forma-proformulasdesarrollo-window').find('input[name=total_porcentaje]');

        var $total_tr = $('#forma-proformulasdesarrollo-window').find('input[name=total_tr]');
        var $porcentaje_temporal = $('#forma-proformulasdesarrollo-window').find('input[name=porcentaje_temporal]');
        var $submit_actualizar = $('#forma-proformulasdesarrollo-window').find('#submit');
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
    
    
    
    $valida_cantidades = function(){
        
        cadena_validacion = "";
        
        var $numero_pasos       = $('#forma-proformulasdesarrollo-window').find('input[name=numero_pasos]');                                                            
        var $paso_actual        = $('#forma-proformulasdesarrollo-window').find('input[name=paso_actual]');
        var $cantidad_calculo   = $('#forma-proformulasdesarrollo-window').find('input[name=cantidad_calculo]');
        
        if(isNaN(parseFloat($numero_pasos.val()))){
            cadena_validacion += "Capture en numero de paso\n";
        }
        
        if(isNaN(parseFloat($paso_actual.val()))){
            cadena_validacion += "Capture el paso actual\n";
        }
        
        if(isNaN(parseFloat($cantidad_calculo.val())) || parseFloat($cantidad_calculo.val()) <= 0 ){
            cadena_validacion += "Capture la cantidad total\n";
        }
        
        return cadena_validacion;
    }
        
        
        
        
    //nuevas formulas
    $new_formulas.click(function(event){
        event.preventDefault();
        var id_to_show_form = 0;
        
        $(this).modalPanel_formulasdesarrollo();
        
        var form_to_show = 'formaFormulasDesarrollo';
        $('#' + form_to_show).each (function(){this.reset();});
        var $forma_selected = $('#' + form_to_show).clone();
        $forma_selected.attr({id : form_to_show + id_to_show_form});
        
        $('#forma-proformulasdesarrollo-window').css({"margin-left": -390, 	"margin-top": -280});
        $forma_selected.prependTo('#forma-proformulasdesarrollo-window');
        $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show_form , style:'display:table'});
        
        var $campo_id                       = $('#forma-proformulasdesarrollo-window').find('input[name=identificador]');
        var $id_producto_saliente           = $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_saliente]');
        var $codigo_Productomaster          = $('#forma-proformulasdesarrollo-window').find('input[name=codigo_master]');
        var $descripcion_Productomaster     = $('#forma-proformulasdesarrollo-window').find('input[name=descripcion_master]');
        var $href_buscar_Productomaster     = $('#forma-proformulasdesarrollo-window').find('a[href*=buscar_productomaster]');
        var $pro_config_prod_pertenece_id           = $('#forma-proformulasdesarrollo-window').find('input[name=pro_config_prod_pertenece_id]');
        //pro_config_prod_pertenece_id
        
        //var $buscar_version_formula         = $('#forma-proformulasdesarrollo-window').find('input[name=buscar_version_formula]');
        var $version                        = $('#forma-proformulasdesarrollo-window').find('input[name=version]');
        var $numero_pasos                   = $('#forma-proformulasdesarrollo-window').find('input[name=numero_pasos]');
        var $paso_actual                    = $('#forma-proformulasdesarrollo-window').find('input[name=paso_actual]');
        var $cantidad_calculo               = $('#forma-proformulasdesarrollo-window').find('input[name=cantidad_calculo]');
        var $codigo_producto_minigrid       = $('#forma-proformulasdesarrollo-window').find('input[name=codigo_producto_minigrid]');
        var $descripcion_producto_minigrid  = $('#forma-proformulasdesarrollo-window').find('input[name=descr_producto_minigrid]');
        var $buscar_producto_para_minigrid  = $('#forma-proformulasdesarrollo-window').find('a[href*=busca_producto_ingrediente]');
        var $agregar_producto_minigrid      = $('#forma-proformulasdesarrollo-window').find('a[href*=agregar_producto_minigrid]');
        var $buscar_version_formula      = $('#forma-proformulasdesarrollo-window').find('a[href*=buscar_version_formula]');
        
        var $prod_tipo = $('#forma-proformulasdesarrollo-window').find('select[name=select_prodtipo]');
        var $unidad = $('#forma-proformulasdesarrollo-window').find('input[name=select_unidad]');
        var $id_producto_master             = $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_master]');
        var $id_producto_entrante           = $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_entrante]');
        var $id_producto_saliente           = $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_saliente]');

        var $codigo_Productosaliente        = $('#forma-proformulasdesarrollo-window').find('input[name=codigo_producto_saliente]');
        var $descripcion_Productosaliente   = $('#forma-proformulasdesarrollo-window').find('input[name=descr_producto_saliente]');
        var $href_buscar_Productosaliente   = $('#forma-proformulasdesarrollo-window').find('a[href*=busca_producto_saliente]');

        //grid de productos
        var $grid_productos_componentes     = $('#forma-proformulasdesarrollo-window').find('div.div_formulaciones');
        
        //grid de errores
        var $grid_warning = $('#forma-proformulasdesarrollo-window').find('#div_warning_grid').find('#grid_warning');
        
        //botones
        var $cerrar_plugin = $('#forma-proformulasdesarrollo-window').find('#close');
        var $cancelar_plugin = $('#forma-proformulasdesarrollo-window').find('#boton_cancelar');
        var $submit_actualizar = $('#forma-proformulasdesarrollo-window').find('#submit');
        
        var $genera_pdf_formulas = $('#forma-proformulasdesarrollo-window').find('#genera_pdf_formulas');
        
        
        $codigo_Productomaster.attr({'readOnly':true});
        $descripcion_Productomaster.attr({'readOnly':true});
        $codigo_producto_minigrid.attr({'readOnly':true});
        $descripcion_producto_minigrid.attr({'readOnly':true});
        $codigo_Productosaliente.attr({'readOnly':true});
        $descripcion_Productosaliente.attr({'readOnly':true});
        
        $version.val(0);
        //$buscar_version_formula.val(0);
        $campo_id.val(0);
        $pro_config_prod_pertenece_id.val(0);
        $('#forma-proformulasdesarrollo-window').find('.gen_pdf').hide();
        
        var respuestaProcesadaFormulacion = function(data){
            $('#forma-proformulasdesarrollo-window').find('.proformulasdesarrollo_div_one').css({'height':'470px'});//sin errores
                if ( data['success'] == "true" ){
                    
                    jAlert("La formula fue dada de alta con exito", 'Atencion!');
                    var $grid_productos_componentes     = $('#forma-proformulasdesarrollo-window').find('div.div_formulaciones');
                    var $href_buscar_Productomaster     = $('#forma-proformulasdesarrollo-window').find('a[href*=buscar_productomaster]');
                    var $href_buscar_Productosaliente   = $('#forma-proformulasdesarrollo-window').find('a[href*=busca_producto_saliente]');
                    var $paso_actual                    = $('#forma-proformulasdesarrollo-window').find('input[name=paso_actual]');
                    var $numero_pasos                   = $('#forma-proformulasdesarrollo-window').find('input[name=numero_pasos]');                                                            
                    var $codigo_Productosaliente        = $('#forma-proformulasdesarrollo-window').find('input[name=codigo_producto_saliente]');
                    var $descripcion_Productosaliente   = $('#forma-proformulasdesarrollo-window').find('input[name=descr_producto_saliente]');

                    var $total_porcentaje = $('#forma-proformulasdesarrollo-window').find('input[name=total_porcentaje]');

                    $paso_actual.attr({'readOnly':true});


                    $grid_productos_componentes.find('#minigrid').find('tbody').children().remove();
                    $descripcion_producto=$('#forma-proformulasdesarrollo-window').find('#my-select option:selected').html();
                    $href_buscar_Productomaster.hide();

                    $descripcion_Productosaliente.val('');
                    $codigo_Productosaliente.val('');

                    $id_producto_saliente.val('');
                    $total_porcentaje.val(0);

                    $paso_actual.val(parseInt($paso_actual.val())-1);

                    $get_datos_grid();//actualiza datos del grid

                    if($paso_actual.val()==0){
                        var remove = function() {$(this).remove();};
                        $('#forma-proformulasdesarrollo-overlay').fadeOut(remove);
                    }
                    
                }else{
                    var $grid_productos_componentes     = $('#forma-proformulasdesarrollo-window').find('div.div_formulaciones');
                    // Desaparece todas las interrogaciones si es que existen
                    //$('#forma-pocpedidos-window').find('.div_one').css({'height':'545px'});//sin errores
                    $('#forma-proformulasdesarrollo-window').find('.pocpedidos_div_one').css({'height':'568px'});//con errores
                    $('#forma-proformulasdesarrollo-window').find('div.interrogacion').css({'display':'none'});

                    $grid_productos_componentes.find('#cant').css({'background' : '#ffffff'});
                    $grid_productos_componentes.find('#cost').css({'background' : '#ffffff'});

                    $('#forma-proformulasdesarrollo-window').find('#div_warning_grid').css({'display':'none'});
                    $('#forma-proformulasdesarrollo-window').find('#div_warning_grid').find('#grid_warning').children().remove();

                    var valor = data['success'].split('___');
                    //muestra las interrogaciones
                    for (var element in valor){
                        tmp = data['success'].split('___')[element];
                        longitud = tmp.split(':');

                        if( longitud.length > 1 ){
                            $('#forma-proformulasdesarrollo-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                            .parent()
                            .css({'display':'block'})
                            .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

                            //alert(tmp.split(':')[0]);

                            if(parseInt($("tr", $grid_productos_componentes).size())>0){
                                for (var i=1;i<=parseInt($("tr", $grid_productos_componentes).size());i++){
                                    if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='posicion'+i)){
                                        //alert(tmp.split(':')[0]);
                                        $('#forma-proformulasdesarrollo-window').find('.proformulasdesarrollo_div_one').css({'height':'568px'});
                                        //$('#forma-pocpedidos-window').find('.div_three').css({'height':'910px'});

                                        $('#forma-proformulasdesarrollo-window').find('#div_warning_grid').css({'display':'block'});
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
        
        $agregar_producto_minigrid.click(function(event){
            event.preventDefault();
            $agrega_producto_ingrediente();
            $codigo_producto_minigrid.val("");
            $descripcion_producto_minigrid.val("");
            $cantidad_calculo.attr({'readOnly':true});
        });
        
        $buscar_producto_para_minigrid.click(function(event){
            event.preventDefault();
            valida_paso = $valida_cantidades();
            if(valida_paso == ""){
                buscador_producto=3;
                $busca_productos(buscador_producto);
            }else{
                jAlert(valida_paso, 'Atencion!');
            }
        });
        
        $href_buscar_Productosaliente.click(function(event){
            event.preventDefault();
            buscador_producto=4;
            $busca_productos(buscador_producto);
        });
        
        $buscar_version_formula.click(function(event){
            event.preventDefault();
            
            valida_paso = $valida_cantidades();
            if(valida_paso == ""){
                buscador_versionformula=1;
                $busca_versiones_formulas(buscador_versionformula);
            }else{
                jAlert(valida_paso, 'Atencion!');
            }
            
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
            $('#forma-proformulasdesarrollo-overlay').fadeOut(remove);
        });
        
        $cancelar_plugin.click(function(event){
            var remove = function() {$(this).remove();};
            $('#forma-proformulasdesarrollo-overlay').fadeOut(remove);
            $buscar.trigger('click');
        });
        
        
    });
    
    
    
    
    
    $add_grid_componente_formulaciones = function(id_reg,sku_id,sku,descripcion,cantidad,posicion){
        
        var $grid_productos_componentes     = $('#forma-proformulasdesarrollo-window').find('div.div_formulaciones');
        var $cantidad_calculo              = $('#forma-proformulasdesarrollo-window').find('input[name=cantidad_calculo]');
        var $porcentaje_temporal = $('#forma-proformulasdesarrollo-window').find('input[name=porcentaje_temporal]');
        var $total_porcentaje = $('#forma-proformulasdesarrollo-window').find('input[name=total_porcentaje]');
        
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
                $(this).val(0.0);
            }
        });
    }
    
    
    
    //======bueva version de nuevo y edicion
    var carga_formaFormulacionDesarrollo00_for_datagrid00 = function(id_to_show_form, accion_mode){
            
            $(this).modalPanel_formulasdesarrollo();
            
            var form_to_show = 'formaFormulasDesarrollo';
            $('#' + form_to_show).each (function(){this.reset();});
            var $forma_selected = $('#' + form_to_show).clone();
            $forma_selected.attr({id : form_to_show + id_to_show_form});
            
		$('#forma-proformulasdesarrollo-window').css({"margin-left": -390, 	"margin-top": -280});
		$forma_selected.prependTo('#forma-proformulasdesarrollo-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show_form , style:'display:table'});
                
                /*
                var $dialogoc =  $('#forma-proformulasdesarrollo-window');
                //var $dialogoc.prependTo('#forma-buscaproduct-window');
                $dialogoc.append($('div.plugin_formulaciones').find('table.formaBusqueda_formulaciones').clone());
                
                $('#forma-proformulasdesarrollo-window').css({"margin-left": -390, 	"margin-top": -270});
                */
               
                var $campo_id                       = $('#forma-proformulasdesarrollo-window').find('input[name=identificador]');
                var $id_producto_saliente           = $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_saliente]');
                var $codigo_Productomaster          = $('#forma-proformulasdesarrollo-window').find('input[name=codigo_master]');
                var $descripcion_Productomaster     = $('#forma-proformulasdesarrollo-window').find('input[name=descripcion_master]');
                var $href_buscar_Productomaster     = $('#forma-proformulasdesarrollo-window').find('a[href*=buscar_productomaster]');
                var $pro_config_prod_pertenece_id           = $('#forma-proformulasdesarrollo-window').find('input[name=pro_config_prod_pertenece_id]');
                //pro_config_prod_pertenece_id
                
                
                var $version                        = $('#forma-proformulasdesarrollo-window').find('input[name=version]');
                var $numero_pasos                   = $('#forma-proformulasdesarrollo-window').find('input[name=numero_pasos]');
                var $paso_actual                    = $('#forma-proformulasdesarrollo-window').find('input[name=paso_actual]');
                var $cantidad_calculo               = $('#forma-proformulasdesarrollo-window').find('input[name=cantidad_calculo]');
                var $codigo_producto_minigrid       = $('#forma-proformulasdesarrollo-window').find('input[name=codigo_producto_minigrid]');
                var $descripcion_producto_minigrid  = $('#forma-proformulasdesarrollo-window').find('input[name=descr_producto_minigrid]');
                var $buscar_producto_para_minigrid  = $('#forma-proformulasdesarrollo-window').find('a[href*=busca_producto_ingrediente]');
                var $agregar_producto_minigrid      = $('#forma-proformulasdesarrollo-window').find('a[href*=agregar_producto_minigrid]');
                var $buscar_version_formula      = $('#forma-proformulasdesarrollo-window').find('a[href*=buscar_version_formula]');
                
                var $prod_tipo = $('#forma-proformulasdesarrollo-window').find('select[name=select_prodtipo]');
                var $unidad = $('#forma-proformulasdesarrollo-window').find('input[name=select_unidad]');
                var $id_producto_master             = $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_master]');
                var $id_producto_entrante           = $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_entrante]');
                var $id_producto_saliente           = $('#forma-proformulasdesarrollo-window').find('input[name=id_prod_saliente]');
                
                var $codigo_Productosaliente        = $('#forma-proformulasdesarrollo-window').find('input[name=codigo_producto_saliente]');
                var $descripcion_Productosaliente   = $('#forma-proformulasdesarrollo-window').find('input[name=descr_producto_saliente]');
                var $href_buscar_Productosaliente   = $('#forma-proformulasdesarrollo-window').find('a[href*=busca_producto_saliente]');
                
                //grid de productos
                var $grid_productos_componentes     = $('#forma-proformulasdesarrollo-window').find('div.div_formulaciones');
                
                //grid de errores
                var $grid_warning = $('#forma-proformulasdesarrollo-window').find('#div_warning_grid').find('#grid_warning');
                
                //botones
                var $cerrar_plugin = $('#forma-proformulasdesarrollo-window').find('#close');
                var $cancelar_plugin = $('#forma-proformulasdesarrollo-window').find('#boton_cancelar');
                var $submit_actualizar = $('#forma-proformulasdesarrollo-window').find('#submit');
                
                var $genera_pdf_formulas = $('#forma-proformulasdesarrollo-window').find('#genera_pdf_formulas');
                
                
                $buscar_version_formula.hide();
                $version.val(0);
                $campo_id.val(0);
                $pro_config_prod_pertenece_id.val(0);
                $('#forma-proformulasdesarrollo-window').find('.gen_pdf').hide();
                
                
                var $genera_pdf_formulas = $('#forma-proformulasdesarrollo-window').find('#genera_pdf_formulas');
                
                
                $codigo_Productomaster.attr({'readOnly':true});
                $descripcion_Productomaster.attr({'readOnly':true});
                $codigo_producto_minigrid.attr({'readOnly':true});
                $version.attr({'readOnly':true});
                $descripcion_producto_minigrid.attr({'readOnly':true});
                $codigo_Productosaliente.attr({'readOnly':true});
                $descripcion_Productosaliente.attr({'readOnly':true});
                
                
                $campo_id.attr({'value' : id_to_show_form});
                
                $('#forma-proformulasdesarrollo-window').find('.gen_pdf').show();
                
                //aqui es el post que envia los datos a getForulas.json
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFormulaDesarrollo.json';
                $arreglo = {'id':id_to_show_form,
                            'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                };
                
                var $grid_productos_componentes     = $('#forma-proformulasdesarrollo-window').find('div.div_formulaciones');
                
                var respuestaProcesadaFormulacion = function(data){
                    $('#forma-proformulasdesarrollo-window').find('.proformulasdesarrollo_div_one').css({'height':'470px'});//sin errores
                    if ( data['success'] == 'true' ){
                        var remove = function() {$(this).remove();};
                        $('#forma-proformulasdesarrollo-overlay').fadeOut(remove);
                        jAlert("Los datos se han actualizado.", 'Atencion!');
                        $get_datos_grid();
                    }else{
                        var $grid_productos_componentes     = $('#forma-proformulasdesarrollo-window').find('div.div_formulaciones');
                        // Desaparece todas las interrogaciones si es que existen
                        //$('#forma-pocpedidos-window').find('.div_one').css({'height':'545px'});//sin errores
                        $('#forma-proformulasdesarrollo-window').find('.pocpedidos_div_one').css({'height':'568px'});//con errores
                        $('#forma-proformulasdesarrollo-window').find('div.interrogacion').css({'display':'none'});

                        $grid_productos_componentes.find('#cant').css({'background' : '#ffffff'});
                        $grid_productos_componentes.find('#cost').css({'background' : '#ffffff'});

                        $('#forma-proformulasdesarrollo-window').find('#div_warning_grid').css({'display':'none'});
                        $('#forma-proformulasdesarrollo-window').find('#div_warning_grid').find('#grid_warning').children().remove();

                        var valor = data['success'].split('___');
                        //muestra las interrogaciones
                        for (var element in valor){
                            tmp = data['success'].split('___')[element];
                            longitud = tmp.split(':');

                            if( longitud.length > 1 ){
                                $('#forma-proformulasdesarrollo-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                .parent()
                                .css({'display':'block'})
                                .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

                                //alert(tmp.split(':')[0]);

                                if(parseInt($("tr", $grid_productos_componentes).size())>0){
                                        for (var i=1;i<=parseInt($("tr", $grid_productos_componentes).size());i++){
                                                if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='posicion'+i)){
                                                        //alert(tmp.split(':')[0]);
                                                        $('#forma-proformulasdesarrollo-window').find('.proformulasdesarrollo_div_one').css({'height':'568px'});
                                                        //$('#forma-pocpedidos-window').find('.div_three').css({'height':'910px'});

                                                        $('#forma-proformulasdesarrollo-window').find('#div_warning_grid').css({'display':'block'});
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
                    $pro_config_prod_pertenece_id.val(entry['Formulas']['0']['pro_estruc_id']);
                    $version.val(entry['Formulas']['0']['version']);
                    
                    //$hrer_buscar_Productomaster.hide();
                    /* esto lo moente, por que no tengo los pasos ahorita*/
                    //$numero_pasos.val(entry['Formulas']['0']['numero_pasos']);
                    //$paso_actual.val(entry['Formulas']['0']['nivel_paso_actual']);
                    
                    $cantidad=$cantidad_calculo.val();
                    $codigo_producto_minigrid.val();
                    
                    
                    //$descripcion_producto_minigrid.val();
                    $id_producto_master.val(entry['Formulas']['0']['inv_prod_id']);
                    $id_producto_entrante.val();
                    
                    var sum_porciento=0;
                    
                    //Formulas_DatosMinigrid.
                    $.each(entry['Formulas_DatosMinigrid'],function(entryIndex,elemento){
                        
                        $add_grid_componente_formulaciones(elemento['id'],elemento['inv_prod_id'],elemento['codigo'],elemento['descripcion'],elemento['cantidad'],elemento['elemento']);
                        
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
                    
                    //tipo de producto que es la formula (prodTipos)
                    $prod_tipo.children().remove();
                    var prod_tipos_html = '';
                    $.each(entry['prodTipos'],function(entryIndex,tipo){
                        //alert(entry['Formulas']['0']['tipo_producto_id']+"   "+tipo['id']);
                         if(tipo['id'] == entry['Formulas']['0']['tipo_producto_id']){
                            prod_tipos_html += '<option value="' + tipo['id'] + '" selected="yes" >' + tipo['titulo'] + '</option>';
                            
                        }else{
                            if(entry['Formulas']['0']['tipo_producto_id'] == 8){
                                prod_tipos_html += '<option value="' + tipo['id'] + '" >' + tipo['titulo'] + '</option>';
                            }
                        }
                    });
                    $prod_tipo.append(prod_tipos_html);
                    
                },"json");//termina llamada json
                
                $genera_pdf_formulas.click(function(event){
                    event.preventDefault();
                    iu=$('#lienzo_recalculable').find('input[name=iu]').val();
                    var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
                    var stock = $('#forma-proformulasdesarrollo-window').find('input[name=stock]').is(':checked');
                    
                    var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfFormula/'+id_to_show_form+'/'+stock+'/'+iu+'/out.json';
                    window.location.href=input_json;
                });
                
            //}
            
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
                $('#forma-proformulasdesarrollo-overlay').fadeOut(remove);
            });
            
            $cancelar_plugin.click(function(event){
                var remove = function() {$(this).remove();};
                $('#forma-proformulasdesarrollo-overlay').fadeOut(remove);
                $buscar.trigger('click');
            });
            
       }
    
    
    
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllFormulasEnDesarrollo.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllFormulasEnDesarrollo.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaFormulacionDesarrollo00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();  
});
