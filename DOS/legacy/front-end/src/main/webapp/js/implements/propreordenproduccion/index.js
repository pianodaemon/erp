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
            return this.contextpath + "/controllers/propreordenproduccion";
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
	var controller = $contextpath.val()+"/controllers/propreordenproduccion";
	
        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_preorden = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Preorden de Producci&oacute;n');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	
	var $cadena_busqueda = "";
	var $campo_busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $select_buscador_tipoorden = $('#barra_buscador').find('.tabla_buscador').find('select[name=buscador_tipoorden]');
        
        var array_productos_proceso = new Array(); //este arreglo carga la maquinas
        
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_proordentipos.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
            //Llena el select tipos de productos en el buscador
            $select_buscador_tipoorden.children().remove();
            var prod_tipos_html = '<option value="0" selected="yes">[-- --]</option>';
            $.each(data['ordenTipos'],function(entryIndex,pt){
                    prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
            });
            $select_buscador_tipoorden.append(prod_tipos_html);
	});
        
        
	var to_make_one_search_string = function(){
            var valor_retorno = "";
            
            var signo_separador = "=";
            valor_retorno += "folio_preorden" + signo_separador + $campo_busqueda_folio.val() + "|";
            valor_retorno += "tipo_orden" + signo_separador + $select_buscador_tipoorden.val() + "|";
            
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
        
        
        
        
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-propreordenproduccion-window').find('select[name=prodtipo]');
            $('#forma-propreordenproduccion-window').find('#submit').mouseover(function(){
                $('#forma-propreordenproduccion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
            });
            $('#forma-propreordenproduccion-window').find('#submit').mouseout(function(){
                $('#forma-propreordenproduccion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
            });
            
            $('#forma-propreordenproduccion-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-propreordenproduccion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            });
            $('#forma-propreordenproduccion-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-propreordenproduccion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            });
            
            $('#forma-propreordenproduccion-window').find('#close').mouseover(function(){
                $('#forma-propreordenproduccion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            });
            $('#forma-propreordenproduccion-window').find('#close').mouseout(function(){
                $('#forma-propreordenproduccion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            });
            
            $('#forma-propreordenproduccion-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-propreordenproduccion-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-propreordenproduccion-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-propreordenproduccion-window').find("ul.pestanas li").click(function() {
                $('#forma-propreordenproduccion-window').find(".contenidoPes").hide();
                $('#forma-propreordenproduccion-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-propreordenproduccion-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
        
        //buscador de pedidos
        $busca_pedidos = function(tipo_busqueda){
            
            //limpiar_campos_grids();
            $(this).modalPanel_BuscaPedido();
            var $dialogoc =  $('#forma-buscapedido-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.buscador_pedidos').find('table.formaBusqueda_pedidos').clone());
            
            $('#forma-buscapedido-window').css({"margin-left": -200, 	"margin-top": -200});
            
            var $tabla_resultados = $('#forma-buscapedido-window').find('#tabla_resultado');
            
            var $buscapedido_folio = $('#forma-buscapedido-window').find('input[name=buscapedido_folio]');
            var $buscapedido_proveedor = $('#forma-buscapedido-window').find('input[name=buscapedido_proveedor]');
            
            var $buscar_plugin_producto = $('#forma-buscapedido-window').find('#busca_producto_modalbox');
            var $cancelar_plugin_busca_producto = $('#forma-buscapedido-window').find('#cencela');
            
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
            
            
            
            //click buscar productos
            $buscar_plugin_producto.click(function(event){
                event.preventDefault();
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_pedidos.json';
                $arreglo = {'folio':$buscapedido_folio.val(),
                                'proveedor':$buscapedido_proveedor.val(),
                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                            }
                var trr = '';
                $tabla_resultados.children().remove();
                $.post(input_json,$arreglo,function(entry){
                    
                    $.each(entry['pedidos'],function(entryIndex,pedido){
                            trr = '<tr>';
                                    trr += '<td width="100" align="center">';
                                            trr += '<span class="id_pedido" style="display:none;">'+pedido['id']+'</span>';
                                            trr += '<span class="cxc_clie_id_proveedor" style="display:none;">'+pedido['cxc_clie_id']+'</span>';
                                            trr += '<span class="numero_control_proveedor" style="display:none;">'+pedido['numero_control']+'</span>';
                                            trr += '<span class="rfc_proveedor" style="display:none;">'+pedido['rfc']+'</span>';
                                            trr += '<span class="folio_pedido">'+pedido['folio']+'</span>';
                                    trr += '</td>';
                                    trr += '<td width="350" colspan="2"><span class="razon_social">'+pedido['razon_social']+'</span></td>';
                                    trr += '<td width="150"><span class="momento_creacion">'+pedido['momento_creacion']+'</span></td>';
                            trr += '</tr>';
                            $tabla_resultados.append(trr);
                    });
                    
                    $colorea_tr_grid($tabla_resultados);
                    
                    //seleccionar un producto del grid de resultados
                    $tabla_resultados.find('tr').click(function(){
                        var id_pedido=$(this).find('span.id_pedido').html();
                        var cxc_clie_id_proveedor=$(this).find('span.cxc_clie_id_proveedor').html();
                        var numero_control_proveedor=$(this).find('span.numero_control_proveedor').html();
                        var rfc_proveedor=$(this).find('span.rfc_proveedor').html();
                        var folio_pedido=$(this).find('span.folio_pedido').html();
                        var razon_social=$(this).find('span.razon_social').html();
                        
                        //buscador principal de el proceso
                        if(tipo_busqueda == 1){
                            //asignar a los campos correspondientes el sku y y descripcion
                            $('#forma-propreordenproduccion-window').find('input[name=folio_pedido]').val(folio_pedido);
                            $('#forma-propreordenproduccion-window').find('input[name=id_pedido]').val(id_pedido);
                            $('#forma-propreordenproduccion-window').find('input[name=num_cliente]').val(numero_control_proveedor);
                            $('#forma-propreordenproduccion-window').find('input[name=rsocialcliente]').val(razon_social);
                            
                            $tabla_productos_porpedido = $('#forma-propreordenproduccion-window').find('#productos_porpedido');
                            
                            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_productos_pedido.json';
                            $arreglo = {'id_pedido':id_pedido,
                                        'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                    }
                            
                            $.post(input_json,$arreglo,function(entry){
                                $tabla_productos_porpedido.children().remove();
                                $.each(entry['productos'],function(entryIndex,producto){
                                    if(producto['inv_prod_id'] != null){
                                        var trCount = $("tr", $tabla_productos_porpedido).size();
                                        trCount++;
                                        
                                        trr = '<tr class="selected'+trCount+'">';
                                            trr += '<td width="105px"  align="center">';
                                                trr += '<span class="presentacion_id" style="display:none;">'+producto['presentacion_id']+'</span>';
                                                trr += '<span class="inv_prod_id" style="display:none;">'+producto['inv_prod_id']+'</span>';
                                                trr += '<span class="gral_imp_id" style="display:none;">'+producto['gral_imp_id']+'</span>';
                                                trr += '<span class="codigo" >'+producto['codigo']+'</span>';
                                            trr += '</td>';
                                            trr += '<td width="110px" ><span class="presentacion">'+producto['presentacion']+'</span></td>';
                                            trr += '<td width="520px"><span class="descripcion">'+producto['descripcion']+'</span></td>';
                                            producto['unidad'] = producto['unidad'].toUpperCase();
                                            factor = producto['unidad'];
                                            trr += '<td width="110px" >';
                                            trr += '<span class="unidad">'+producto['unidad']+'</span>';
                                            trr += '<span class="unidad_id" style="display:none;">'+producto['unidad']+'</span>';
                                            trr += '</td>';
                                            trr += '<td width="100px"><span class="cantidad">'+producto['cantidad']+'</span></td>';
                                        trr += '</tr>';
                                        
                                        $tabla_productos_porpedido.append(trr);
                                        
                                        var remove = function() {$(this).remove();};
                                        $('#forma-buscapedido-overlay').fadeOut(remove);
                                        
                                        $colorea_tr_grid($tabla_productos_porpedido);
                                        
                                        //seleccionar un producto del grid de resultados
                                        $tabla_productos_porpedido.find('.selected'+trCount).click(function(){
                                            var presentacion_id_element =    $(this).find('span.presentacion_id').html();
                                            var inv_prod_id_element     =    $(this).find('span.inv_prod_id').html();
                                            var gral_imp_id_element     =    $(this).find('span.gral_imp_id').html();
                                            var codigo_element          =    $(this).find('span.codigo').html();
                                            var presentacion_element    =   $(this).find('span.presentacion').html();
                                            var descripcion_element     =   $(this).find('span.descripcion').html();
                                            var cantidad_element        =   $(this).find('span.cantidad').html();
                                            var umedida = $(this).find('span.unidad').html();
                                            var umedida_id = $(this).find('span.unidad_id').html();
                                            
                                            //id_pedido//este tambien debe de ir en la parte de abajo, para saber de que pedido es el producto
                                            id_reg = 0;
                                            $tabla_productos_preorden = $('#forma-propreordenproduccion-window').find('#grid_productos_seleccionados');
                                            
                                            existe = 0;
                                            $tabla_productos_preorden.find('tr').each(function(){
                                                id_pedido_tmp = $(this).find('input[name=id_pedido]').val();
                                                inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                                                eliminado_tmp = $(this).find('input[name=eliminar]').val();
                                                eliminado_tmp = $(this).find('input[name=eliminar]').val();
                                                presentacion_id_tmp = $(this).find('input[name=presentacion_id]').val();
                                                
                                                if(parseInt(id_pedido_tmp) == parseInt(id_pedido) && parseInt(inv_prod_id_tmp) == parseInt(inv_prod_id_element) && parseInt(eliminado_tmp) == 1 && presentacion_id_tmp == presentacion_id_element){
                                                    existe=1;
                                                }
                                            });
                                            
                                            
                                            if(existe == 0){
                                                $agrega_producto_preorden(id_reg,presentacion_id_element,inv_prod_id_element,gral_imp_id_element,codigo_element,presentacion_element,descripcion_element,cantidad_element, id_pedido, "false", umedida, umedida_id);
                                            }else{
                                                jAlert("El codigo "+codigo_element+" ya esta seleccionado", 'Atencion!');
                                            }
                                            
                                        });
                                    }
                                });
                            });
                        }
                        
                        //elimina la ventana de busqueda
                        var remove = function() {$(this).remove();};
                        $('#forma-buscapedido-overlay').fadeOut(remove);
                        //asignar el enfoque al campo sku del producto
                    });
                });
            });
            
            
            //si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
            if($buscapedido_proveedor.val() != ''){
                $buscar_plugin_producto.trigger('click');
            }
            
            //si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
            if($buscapedido_folio.val() != ''){
                $buscar_plugin_producto.trigger('click');
            }
            
            
            //cerrar plugin
            $cancelar_plugin_busca_producto.bind('click',function(){
                var remove = function() {$(this).remove();};
                $('#forma-buscapedido-overlay').fadeOut(remove);
            });
            
        }
        //termina buscador pedidos
		
		//prod['id'],prod['inv_prod_presentaciones_id'],prod['inv_prod_id'],"0",prod['sku'],prod['presentacion'],prod['descripcion'],prod['cantidad'], prod['poc_pedidos_id'], "false"
        $agrega_producto_preorden = function(id_reg,presentacion_id,inv_prod_id,gral_imp_id,codigo,presentacion,descripcion,cantidad, id_pedido, confirmado, umedida, umedida_id){
                $tabla_productos_preorden = $('#forma-propreordenproduccion-window').find('#grid_productos_seleccionados');
                
                trr = '<tr>';
                    trr += '<td width="65px"  align="center">';
                        if(confirmado == "true" ){
                            trr += 'Eliminar';
                        }else{
                            trr += '<a href="#eliminar">Eliminar</a>';
                        }
                        
                        trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                        trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                        trr += '<input type="hidden" id="presentacion_id" name="presentacion_id" value="'+presentacion_id+'">';
                        trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+inv_prod_id +'">';
                        trr += '<input type="hidden" id="id_pedido" name="id_pedido" value="'+id_pedido +'">';
                        trr += '<input type="hidden" id="gral_imp_id" name="gral_imp_id" value="'+gral_imp_id+'">';
                    trr += '</td>';
                    trr += '<td width="108px"  align="center">';
                        trr += '<input type="text" name="codigo" value="'+codigo+'"  class="borde_oculto" readOnly="true" style="width:98px;">';
                    trr += '</td>';
                    trr += '<td width="108px" ><input type="text" name="presentacion" value="'+presentacion+'"  class="borde_oculto" readOnly="true" style="width:98px;"></td>';
                    trr += '<td width="420px"><input type="text" name="descripcion" value="'+descripcion+'"  class="borde_oculto" readOnly="true" style="width:415px;"></td>';
                    trr += '<td width="108px" >';
                        trr += '<input type="text" name="umedida" value="'+umedida+'"  class="borde_oculto" readOnly="true" style="width:98px;">';
                        trr += '<input type="hidden" name="umedida_id" value="'+umedida_id+'" > ';
                    trr += '</td>';
                    if(confirmado == "true" ){
                        trr += '<td width="100px"><input type="text" name="cantidad" value="'+cantidad+'"  style="width:97px;" readOnly="true"></td>';
                    }else{
                        trr += '<td width="100px"><input type="text" name="cantidad" value="'+cantidad+'"  style="width:97px;"></td>';
                    }
                trr += '</tr>';
                
                $tabla_productos_preorden.append(trr);
                
                
                $tabla_productos_preorden.find('a').bind('click',function(event){
                    
                    if(parseInt($(this).parent().find('#delete').val()) != 0){
                        
                        $(this).parent().find('#delete').val(0);
                        $(this).parent().parent().hide();
                        
                    }
                });
                
            }
        
	
	//nueva entrada
	$new_preorden.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_ProPreordenProduccion();
		
		var form_to_show = 'formaProPreordenProduccion00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-propreordenproduccion-window').css({"margin-left": -375, "margin-top": -230});
		
		$forma_selected.prependTo('#forma-propreordenproduccion-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_datos_preorden.json';
		$arreglo = {'id_preorden':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
                
                
                var $command_selected = $('#forma-propreordenproduccion-window').find('input[name=command_selected]');
		var $id_preorden = $('#forma-propreordenproduccion-window').find('input[name=id_preorden]');
                var $select_tipoorden = $('#forma-propreordenproduccion-window').find('select[name=tipoorden]');
		var $observaciones = $('#forma-propreordenproduccion-window').find('text[name=observaciones]');
                
                //campos para productos
		var $folio_pedido = $('#forma-propreordenproduccion-window').find('input[name=folio_pedido]');
		var $id_pedido = $('#forma-propreordenproduccion-window').find('input[name=id_pedido_parent]');
		var $num_cliente = $('#forma-proconfigproduccion-window').find('input[name=num_cliente]');
		var $rsocialcliente = $('#forma-propreordenproduccion-window').find('input[name=rsocialcliente]');
                
		
		//grids detalle pedido
		var $productos_porpedido = $('#forma-propreordenproduccion-window').find('#productos_porpedido');
                var $tabla_productos_preorden = $('#forma-propreordenproduccion-window').find('#grid_productos_seleccionados');
                
                //href
                var $busca_pedido = $('#forma-propreordenproduccion-window').find('a[href*=busca_pedido]');
                
                //tipos de preorden
                var $preorden_tipo_pedido = $('#forma-propreordenproduccion-window').find('.tipo_pedido');
                var $preorden_tipo_laboratorio = $('#forma-propreordenproduccion-window').find('.tipo_laboratorio');
                
		//href para buscar producto
		var $buscar_producto = $('#forma-propreordenproduccion-window').find('a[href*=busca_producto]');
		//href para agregar producto al grid
		
                var $cancelar_proceso = $('#forma-propreordenproduccion-window').find('#cancela_entrada');
		
		var $cerrar_plugin = $('#forma-propreordenproduccion-window').find('#close');
		var $cancelar_plugin = $('#forma-propreordenproduccion-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-propreordenproduccion-window').find('#submit');
		var $confirmar_orden = $('#forma-propreordenproduccion-window').find('#confirmar_orden');
                var $cancelar_orden = $('#forma-propreordenproduccion-window').find('#cancelar_orden');
                var $descargar_pdf = $('#forma-propreordenproduccion-window').find('#descargar_pdf');
                
                $command_selected.val("new");
                $id_preorden.val(0);
                $confirmar_orden.hide();
		$cancelar_orden.hide();
                $descargar_pdf.hide();
                
		var respuestaProcesada = function(data){
			if ( data['success'] == "1"  || data['success'] == "2" ){
				
				if ( data['success'] == "2" ){
					jAlert("Salio sin guardar cambios", 'Atencion!');
				}else{
					jAlert("La preorden se ha dado de alta", 'Atencion!');
				}
				
				var remove = function() {$(this).remove();};
				$('#forma-propreordenproduccion-overlay').fadeOut(remove);
				$get_datos_grid();
				
			}else{
				
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-propreordenproduccion-window').find('div.interrogacion').css({'display':'none'});
				//$grid_productos.find('#cost').css({'background' : '#ffffff'});
				//$grid_productos.find('#cant').css({'background' : '#ffffff'});
				//$grid_productos.find('#cad').css({'background' : '#ffffff'});
				
				$('#forma-propreordenproduccion-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-propreordenproduccion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-propreordenproduccion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						
						if( tmp.split(':')[0]=='error_grid'){
							jAlert("Es necesario agregar por lo menos un registro en la segunda tabla para cerrar correctamente el proceso.\nHaga clic en cualquier registro de la tabla de arriba y despues haga clic en Cerrar.", 'Atencion!');
						}
						
					}
				}
				
				$('#forma-propreordenproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
				$('#forma-propreordenproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({'background-color' : '#e7e8ea'});			
					
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
                
		//$.getJSON(json_string,function(entry){
                $.post(input_json,$arreglo,function(entry){
                    
                    
                    $select_tipoorden.children().remove();
                    var orden_tipos_html = '<option value="0" selected="yes">[-- --]</option>';
                    $.each(entry['ordenTipos'],function(entryIndex,pt){
                        pt['titulo']=pt['titulo'].toUpperCase();
                        if( pt['titulo'] != "STOCK"){
                            orden_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                        }
                    });
                    $select_tipoorden.append(orden_tipos_html);
                    
                    
                    $select_tipoorden.change(function(){
                        tipo_preorden = $select_tipoorden.val()
                        if(tipo_preorden == 1){
                            $preorden_tipo_pedido.show();
                            $preorden_tipo_laboratorio.hide();
                            $('#forma-propreordenproduccion-window').find('.propreordenproduccion_div_one').css({'height':'535px'});
                        }
                        
                        if(tipo_preorden == 3){
                            $preorden_tipo_pedido.hide();
                            $preorden_tipo_laboratorio.show();
                            $('#forma-propreordenproduccion-window').find('.propreordenproduccion_div_one').css({'height':'370px'});
                        }
                    });
                    
                },"json");//termina llamada json
		
                
		//buscar pedido
		$busca_pedido.click(function(event){
                    event.preventDefault();
                    $busca_pedidos(1);
		});
                
                //cerrar plugin
                $cerrar_plugin.bind('click',function(){
                    $command_selected.val("cerrar");

                    jConfirm('Desea cancelar la preorden?', 'Dialogo de Confirmacion', function(r) {
                        // If they confirmed, manually trigger a form submission
                        if (r) $submit_actualizar.parents("FORM").submit();
                    });

                    // Always return false here since we don't know what jConfirm is going to do
                    return false;
                });

                //boton cancelar y cerrar plugin
                $cancelar_plugin.click(function(event){
                    $command_selected.val("cerrar");

                    jConfirm('Desea cancelar la preorden?', 'Dialogo de Confirmacion', function(r) {
                        // If they confirmed, manually trigger a form submission
                        if (r) $submit_actualizar.parents("FORM").submit();
                    });

                    // Always return false here since we don't know what jConfirm is going to do
                    return false;
                });
		
                
                
                $submit_actualizar.bind('click',function(){
                    var trCount = $("tr", $tabla_productos_preorden).size();
                    
                    if(trCount > 0){
                        
                        jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
                            // If they confirmed, manually trigger a form submission
                            if (r) $submit_actualizar.parents("FORM").submit();
                        });
                        
                    }else{
                        jAlert("Es necesario agregar productos.", 'Atencion!');
                    }
                    // Always return false here since we don't know what jConfirm is going to do
                    return false;
                });
                /*
                $confirmar_orden.bind('click',function(){
                    $command_selected.val("edit");

                    var trCount = $("tr", $tabla_productos_preorden).size();
                    if(trCount > 0){
                        jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
                            // If they confirmed, manually trigger a form submission
                            if (r) $submit_actualizar.parents("FORM").submit();
                        });
                    }else{
                        jAlert("Es necesario agregar productos.", 'Atencion!');
                    }
                    // Always return false here since we don't know what jConfirm is going to do
                    return false;
                });*/
		
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
                                $(this).modalPanel_ProPreordenProduccion();
                                
                        var form_to_show = 'formaProPreordenProduccion00';
                        $('#' + form_to_show).each (function(){this.reset();});
                        var $forma_selected = $('#' + form_to_show).clone();
                        $forma_selected.attr({id : form_to_show + id_to_show});

                        $('#forma-propreordenproduccion-window').css({"margin-left": -375, "margin-top": -230});

                        $forma_selected.prependTo('#forma-propreordenproduccion-window');
                        $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
                        
                        $tabs_li_funxionalidad();
                        
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_datos_preorden.json';
					$arreglo = {'id_preorden':id_to_show,
									'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
									};
									
					var $command_selected = $('#forma-propreordenproduccion-window').find('input[name=command_selected]');       
					var $id_preorden = $('#forma-propreordenproduccion-window').find('input[name=id_preorden]');
					var $select_tipoorden = $('#forma-propreordenproduccion-window').find('select[name=tipoorden]');
					var $observaciones = $('#forma-propreordenproduccion-window').find('textarea[name=observaciones]');

					//campos para productos
					var $folio_pedido = $('#forma-propreordenproduccion-window').find('input[name=folio_pedido]');
					var $id_pedido = $('#forma-propreordenproduccion-window').find('input[name=id_pedido_parent]');
					var $num_cliente = $('#forma-proconfigproduccion-window').find('input[name=num_cliente]');
					var $rsocialcliente = $('#forma-propreordenproduccion-window').find('input[name=rsocialcliente]');
					

					//grids detalle pedido
					var $productos_porpedido = $('#forma-propreordenproduccion-window').find('#productos_porpedido');
					var $tabla_productos_preorden = $('#forma-propreordenproduccion-window').find('#grid_productos_seleccionados');

					//href
					var $busca_pedido = $('#forma-propreordenproduccion-window').find('a[href*=busca_pedido]');

					//tipos de preorden
					var $preorden_tipo_pedido = $('#forma-propreordenproduccion-window').find('.tipo_pedido');
					var $preorden_tipo_laboratorio = $('#forma-propreordenproduccion-window').find('.tipo_laboratorio');
					
					
					//href para buscar producto
					var $buscar_producto = $('#forma-propreordenproduccion-window').find('a[href*=busca_producto]');
					//href para agregar producto al grid
					
					var $cancelar_proceso = $('#forma-propreordenproduccion-window').find('#cancela_entrada');
					
					var $cerrar_plugin = $('#forma-propreordenproduccion-window').find('#close');
					var $cancelar_plugin = $('#forma-propreordenproduccion-window').find('#boton_cancelar');
					var $submit_actualizar = $('#forma-propreordenproduccion-window').find('#submit');
					var $confirmar_orden = $('#forma-propreordenproduccion-window').find('#confirmar_orden');
					var $cancelar_orden = $('#forma-propreordenproduccion-window').find('#cancelar_orden');
					var $descargar_pdf = $('#forma-propreordenproduccion-window').find('#descargar_pdf');
					//$sku.attr("readonly", true);
					//$titulo.attr("readonly", true);
					//$descripcion.attr("readonly", true);
									
					$confirmar_orden.hide();
							$buscar_producto.hide();
							$cancelar_orden.hide();
							$descargar_pdf.hide();
							$id_preorden.val(id_to_show);
							
							$command_selected.val("edit");
					var respuestaProcesada = function(data){
                                    //1 para cunado se guandaron los cambios o fuardo uno nuevo y 2 par acuendo sel e dio click en cerrar o la X
					if ( data['success'] == "1" || data['success']=="2"){
							if ( data['success'] == "2" ){
								if($command_selected.val()!='cerrar'){
									jAlert("Salio sin guardar cambios", 'Atencion!');
								}
							}else{
								jAlert("Cambios guardados exitosamente", 'Atencion!');
							}
							var remove = function() {$(this).remove();};
							$('#forma-propreordenproduccion-overlay').fadeOut(remove);
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
                                    
						$id_preorden.attr({'value': entry['Preorden']['0']['id']});
						$observaciones.text( entry['Preorden']['0']['observaciones']);
						
						$select_tipoorden.attr({'value': entry['Preorden']['0']['descripcion']});
						
						$select_tipoorden.children().remove();
						var orden_tipos_html = '';
						$.each(entry['ordenTipos'],function(entryIndex,pt){
							pt['titulo']=pt['titulo'].toUpperCase();
							if( pt['titulo'] != "STOCK"){
								if(entry['Preorden']['0']['pro_orden_tipos_id'] == pt['id']){
									orden_tipos_html += '<option value="' + pt['id'] + '" selected="yes" >' + pt['titulo'] + '</option>';
								}
							}
						});
						$select_tipoorden.append(orden_tipos_html);
						
						
						tipo_preorden = $select_tipoorden.val();
						if(entry['Preorden']['0']['confirmado'] == "false"){
						   if(entry['PreordenDet'] != null){
								$.each(entry['PreordenDet'],function(entryIndex,prod){
															//gral_imp_id --impuersto
															//id_reg,presentacion_id,inv_prod_id,gral_imp_id,codigo,presentacion,descripcion,cantidad, id_pedido
															
									//$agrega_producto_preorden(prod['id'],prod['inv_prod_presentaciones_id'],prod['inv_prod_id'],"0",prod['sku'],prod['presentacion'],prod['descripcion'],prod['cantidad'], prod['poc_pedidos_id'], 'false');
									$agrega_producto_preorden(prod['id'],prod['inv_prod_presentaciones_id'],prod['inv_prod_id'],"0",prod['sku'],prod['presentacion'],prod['descripcion'],prod['cantidad'], prod['poc_pedidos_id'], "false", prod['unidad'], prod['unidad_id']);
								});
							}
							
							//buscar pedido
							$busca_pedido.click(function(event){
								event.preventDefault();
								$busca_pedidos(1);
							});
							
							$confirmar_orden.show();
							$cancelar_orden.show();
							$preorden_tipo_pedido.show();
							$preorden_tipo_laboratorio.hide();
							$descargar_pdf.hide();
							$('#forma-propreordenproduccion-window').find('.propreordenproduccion_div_one').css({'height':'535px'});
							
						}else{
							if(entry['PreordenDet'] != null){
								$.each(entry['PreordenDet'],function(entryIndex,prod){
															//gral_imp_id --impuersto
															//id_reg,presentacion_id,inv_prod_id,gral_imp_id,codigo,presentacion,descripcion,cantidad, id_pedido
									$agrega_producto_preorden(prod['id'],prod['inv_prod_presentaciones_id'],prod['inv_prod_id'],"0",prod['sku'],prod['presentacion'],prod['descripcion'],prod['cantidad'], prod['poc_pedidos_id'], "true",prod['unidad'], prod['unidad_id'] );
								});
							}
							$confirmar_orden.hide();
							$preorden_tipo_pedido.hide();
							$cancelar_orden.hide();
							$preorden_tipo_laboratorio.hide();
							$descargar_pdf.show();
							$('#forma-propreordenproduccion-window').find('.propreordenproduccion_div_one').css({'height':'370px'});
						}
                                    
				},"json");//termina llamada json
				
				
				
				$submit_actualizar.bind('click',function(){
					$command_selected.val("edit");
					
					var trCount = $("tr", $tabla_productos_preorden).size();
					if(trCount > 0){
						jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) $submit_actualizar.parents("FORM").submit();
						});
					}else{
						jAlert("Es necesario agregar productos.", 'Atencion!');
					}
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
                                
                                $confirmar_orden.bind('click',function(){
                                    $command_selected.val("confirm");
                                    
                                    var trCount = $("tr", $tabla_productos_preorden).size();
                                    if(trCount > 0){
                                        jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
                                            // If they confirmed, manually trigger a form submission
                                            if (r) $submit_actualizar.parents("FORM").submit();
                                        });
                                    }else{
                                        jAlert("Es necesario agregar productos.", 'Atencion!');
                                    }
                                    // Always return false here since we don't know what jConfirm is going to do
                                    return false;
                                });
                                
                                $cancelar_orden.bind('click',function(){
                                    $command_selected.val("cancelar");
                                    
                                    jConfirm('Desea cancelar la preorden?', 'Dialogo de Confirmacion', function(r) {
                                        // If they confirmed, manually trigger a form submission
                                        if (r) $submit_actualizar.parents("FORM").submit();
                                    });
                                    
                                    // Always return false here since we don't know what jConfirm is going to do
                                    return false;
                                });
                                
								//cerrar plugin
                                $cerrar_plugin.bind('click',function(){
                                    $command_selected.val("cerrar");
                                    
                                    jConfirm('Desea salir sin guardar cambios?', 'Dialogo de Confirmacion', function(r) {
                                        // If they confirmed, manually trigger a form submission
                                        if (r) $submit_actualizar.parents("FORM").submit();
                                    });
                                    
                                    // Always return false here since we don't know what jConfirm is going to do
                                    return false;
                                });

                                //boton cancelar y cerrar plugin
                                $cancelar_plugin.click(function(event){
                                    $command_selected.val("cerrar");
                                    
                                    jConfirm('Desea salir sin guardar cambios?', 'Dialogo de Confirmacion', function(r) {
                                        // If they confirmed, manually trigger a form submission
                                        if (r) $submit_actualizar.parents("FORM").submit();
                                    });
                                    
                                    // Always return false here since we don't know what jConfirm is going to do
                                    return false;
                                });
                                
                                
                                $descargar_pdf.bind('click',function(event){
                                    event.preventDefault();
                                    jConfirm('Descargar PDF?', 'Dialogo de Confirmacion', function(r) {
                                        // If they confirmed, manually trigger a form submission
                                        if (r) {
                                            
                                            var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
                                            
                                            var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfPreorden/'+id_to_show+'/'+iu+'/out.json';
                                            window.location.href=input_json;
                                        }
                                    });
                                    
                                    // Always return false here since we don't know what jConfirm is going to do
                                    return false;
                                });
				
			}
		}
	}
        
        
        
        
	$get_datos_grid = function(){
            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_all_preordenesproduccion.json';
            
            var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
            
            $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/get_all_preordenesproduccion.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
            
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



