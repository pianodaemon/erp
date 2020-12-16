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
    var controller = $contextpath.val()+"/controllers/invordpresuben2";
	
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_item = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Pre-Orden de Producci&oacute;n de Subensamble');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
	
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	//var $busqueda_select_grupo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_grupo]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
        
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_producto.val() + "|";
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
		$busqueda_folio.val('');
		$busqueda_codigo.val('');
		$busqueda_producto.val('');
		$busqueda_folio.focus();
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
		$busqueda_folio.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_producto, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-invordpresuben-window').find('#submit').mouseover(function(){
			$('#forma-invordpresuben-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-invordpresuben-window').find('#submit').mouseout(function(){
			$('#forma-invordpresuben-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-invordpresuben-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invordpresuben-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invordpresuben-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invordpresuben-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invordpresuben-window').find('#close').mouseover(function(){
			$('#forma-invordpresuben-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invordpresuben-window').find('#close').mouseout(function(){
			$('#forma-invordpresuben-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-invordpresuben-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invordpresuben-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invordpresuben-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invordpresuben-window').find("ul.pestanas li").click(function() {
			$('#forma-invordpresuben-window').find(".contenidoPes").hide();
			$('#forma-invordpresuben-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invordpresuben-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	//buscador de producto ingrediente
	$busca_productos = function(sku_buscar, descripcion, arrayProdTipos){
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
	   
		$('#forma-buscaproducto-window').css({"margin-left": -190,     "margin-top": -160});
	   
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
	   
	   
		//Llena el select tipos de productos en el buscador  de productos formulados
		$select_tipo_producto.children().remove();
		var prod_tipos_html = '<option value="0">[--Seleccionar Tipo--]</option>';
		$.each(arrayProdTipos,function(entryIndex,pt){
			//Aqui solo hay que mostrar los 1=PROD TERMINADO, 2=PROD INTERMEDIO, 8=PROD DESARROLLO
			if(parseInt(pt['id'])==1 || parseInt(pt['id'])==2 || parseInt(pt['id'])==1 || parseInt(pt['id'])==8 ){
				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
			}
		});
		$select_tipo_producto.append(prod_tipos_html);
		
		
		//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
		$campo_sku.val(sku_buscar);
		
		//asignamos la descripcion del producto, si el usuario capturo la descripcion antes de abrir el buscador
		$campo_descripcion.val(descripcion);
		
		$campo_sku.focus();
	   
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos.json';
			$arreglo = {//'almacen':$select_almacen.val(),
					'sku':$campo_sku.val(),
					'tipo':$select_tipo_producto.val(),
					'descripcion':$campo_descripcion.val(),
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
				}
			var trr = '';
			
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Productos'],function(entryIndex,producto){
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<span class="id_prod_buscador" style="display:none;">'+producto['id']+'</span>';
							trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
						trr +=  '</td>';
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
					//asignar a los campos correspondientes el sku y y descripcion
					$('#forma-invordpresuben-window').find('input[name=producto_id]').val($(this).find('span.id_prod_buscador').html());
					$('#forma-invordpresuben-window').find('input[name=productosku]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-invordpresuben-window').find('input[name=producto_descripcion]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
				});
			   
			});//termina llamada json
		});
	   
	   
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_sku.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_sku, $buscar_plugin_producto);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_descripcion, $buscar_plugin_producto);
		
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_sku.val()!='' || $campo_descripcion.val()!=''){
			$buscar_plugin_producto.trigger('click');
		}
	   
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaproducto-overlay').fadeOut(remove);
		});
	}
	
	
	
	
        
        //Agregar nuevo producto formulado al grid
		$agrega_producto_al_grid = function(id_det, id_prod,sku,titulo,unidad, noDec, componentes, cantidad, idPresDef, arrayPres, procesoFlujo, densidad, estatus){
            var $id_prod = $('#forma-invordpresuben-window').find('input[name=id_producto]');
            var $sku_prod = $('#forma-invordpresuben-window').find('input[name=productosku]');
            var $nombre_prod = $('#forma-invordpresuben-window').find('input[name=titulo_producto]');
            var $grid_productos = $('#forma-invordpresuben-window').find('#grid_productos');
            var $grid_componentes = $('#forma-invordpresuben-window').find('#grid_productos_complementos');
            var $grid_ids = $('#forma-invordpresuben-window').find('#grid_prodid_compid');
            
            var encontrado = 0;
            $grid_productos.find('tr').each(function (index){
                if(( $(this).find('#skup').val() == sku.toUpperCase() )  && (parseInt($(this).find('#eliminado').val())!=0)){
                    encontrado=1;//el producto ya esta en el grid
					
                    $grid_componentes.find('tr').each(function (index){
                        if(( $(this).find('#skup').val() == sku.toUpperCase() )  && (parseInt($(this).find('#eliminado').val())!=0)){
                            encontrado=1;//el producto ya esta en el grid
                        }
                    });
                }
            });
            //alert(encontrado);
			
            if(parseInt(encontrado) == 0 ){
                $grid_productos.find('tr').each(function (index){
                    if(( $(this).find('#skup').val() == sku.toUpperCase() )  && (parseInt($(this).find('#eliminado').val()) == 0)){
						
                        encontrado=2;//el producto ya esta en el grid
                        $id_prod_tmp = $(this).find('#id_prod_grid').val();
                        $(this).find('input[name=cantidad]').val(1);
                        $(this).find('#eliminado').val(1);
                        $(this).show();
						
                        $grid_ids.find('tr').each(function (index){
                            if( $(this).find('#id_prod_gridid').val() == $id_prod_tmp ){
                                $componente_id_tmp = $(this).find('#id_comp_grid').val();
                                $(this).find('#eliminadogridid').val(1);
                            }
                        });
                    }
                });
            }
			
			
			
            if(parseInt(encontrado)==0){
					//si el producto no esta en el grid entra aqui
                    var trCount = $("tr", $grid_productos).size();
                    trCount++;
                    var tr_prod='';
					$sku_prod.val('');
					$nombre_prod.val('');
					var cantidad_litro=0;
					
					//alert("sku="+sku+" | densidad="+densidad);
					
					if(parseFloat(densidad)>0){
						cantidad_litro = cantidad / densidad;
					}
					
                    tr_prod += '<tr>';
						tr_prod += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="24">';
							tr_prod += '<a href="elimina_producto" id="eliminaprod'+ id_prod +'"><div id="eliminar'+ trCount +'" class="onmouseOutEliminar" style="width:24px; background-position:center;"/></a>';
							tr_prod += '<input type="hidden" name="eliminado" id="eliminado" value="1">';//el 1 significa que el registro no ha sido eliminado
							tr_prod += '<input type="hidden" name="id_det" id="id_det" value="'+ id_det +'">';
							tr_prod += '<input type="hidden" name="noDec'+ id_prod +'" id="noDec" value="'+ noDec +'">';
						tr_prod += '</td>';
						tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="100">';
							tr_prod += '<input type="hidden" name="id_prod_grid" id="id_prod_grid" value="'+ id_prod +'">';
							tr_prod += '<input type="text" id="skup" name="sku'+ id_prod +'" value="'+ sku +'" class="borde_oculto" style="width:96px;" readOnly="true">';
						tr_prod += '</td>';
						tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="230">';
							tr_prod += '<input type="text" name="titulo'+ id_prod +'" value="'+ titulo +'" class="borde_oculto" style="width:227px;" readOnly="true">';
						tr_prod += '</td>';
						
						tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="90">';
							tr_prod += '<input type="text" name="unidad" class="borde_oculto" value="'+ unidad +'" readOnly="true" style="width:88px;">';
						tr_prod += '</td>';
						
						tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="100">';
							tr_prod += '<select name="select_pres" class="select_pres'+ trCount +'" style="width:98px;"></select>';
						tr_prod += '</td>';
						
						tr_prod += '<td class="grid1" style="font-size:11px; border:1px solid #C1DAD7;" width="50">';
							tr_prod += '<input type="text" name="densidad" id="densidad'+id_prod+'" value="'+ parseFloat(densidad).toFixed(4)+'" style="width:48px;">';
						tr_prod += '</td>';
						
						tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="75">';
							tr_prod += '<input type="text" name="cantidad" id="cantidad'+id_prod+'" value="'+ parseFloat(cantidad).toFixed(noDec)+'" style="width:72px;">';
						tr_prod += '</td>';
						
						tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="75">';
							tr_prod += '<input type="text" name="cant_litro" id="cant_litro'+id_prod+'" value="'+ parseFloat(cantidad_litro).toFixed(noDec) +'" style="width:71px;">';
						tr_prod += '</td>';
					tr_prod += '</tr>';
					
                    $grid_productos.append(tr_prod);
                    
                    if(parseInt(estatus)>=1){
						$grid_productos.find('#densidad'+id_prod).attr("readonly", true);
						$grid_productos.find('#cantidad'+id_prod).attr("readonly", true);
						$grid_productos.find('#cant_litro'+id_prod).attr("readonly", true);
					}
                    
					$grid_productos.find('select.select_pres'+ trCount).children().remove();
					var pres_hmtl='';
					$.each(arrayPres ,function(entryIndex,pres){
						if( parseInt(pres['id']) == parseInt(idPresDef) ){
							pres_hmtl += '<option value="' + pres['id'] + '" selected="yes">' + pres['titulo'] + '</option>';
						}else{
							if(parseInt(procesoFlujo) < 2){
								pres_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
							}
						}
					});
					$grid_productos.find('select.select_pres'+ trCount).append(pres_hmtl);
                    
                    
                    
                    //Al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
                    $grid_productos.find('#densidad'+id_prod).focus(function(e){
                        if($(this).val().trim() == ''){
                            $(this).val('');
                        }else{
							if(parseFloat($(this).val().trim()) <= 0){
								$(this).val('');
							}
						}
                    });
                    
                    //Validar campo cantidad, solo acepte numeros y punto
                    $grid_productos.find('#densidad'+id_prod).keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                            return true;
                        }else {
                            return false;
                        }
                    });
                    
                    
                    $grid_productos.find('#densidad'+id_prod).blur(function(e){
						var no_dec = $grid_productos.find('input[name=noDec'+id_prod+']').val();
						
                        if($grid_productos.find('#densidad'+id_prod).val().trim()==""){
                            $grid_productos.find('#densidad'+id_prod).val(0);
                        }
                        $grid_productos.find('#densidad'+id_prod).val(parseFloat($grid_productos.find('#densidad'+id_prod).val()).toFixed(4));
                        
                        //alert("td_densidad: "+$grid_productos.find('#densidad'+id_prod).val());
                        
                        if(parseFloat($grid_productos.find('#densidad'+id_prod).val())>0){
							//Convertir los Kilos a Litros
							$grid_productos.find('#cant_litro'+id_prod).val(parseFloat(parseFloat($grid_productos.find('#cantidad'+id_prod).val()) / parseFloat($grid_productos.find('#densidad'+id_prod).val())).toFixed(no_dec));
						}
                        
                        //$suma_cantidades();
                    });
                    
                    
                    
                    //Al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
                    $grid_productos.find('#cantidad'+id_prod).focus(function(e){
                        if(($(this).val().trim() == '') || (parseInt($(this).val().trim())==0)){
                            $(this).val('');
                        }
                    });
                    
                    //validar campo cantidad, solo acepte numeros y punto
                    $grid_productos.find('#cantidad'+id_prod).keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                            return true;
                        }else {
                            return false;
                        }
                    });
                    
                    $grid_productos.find('#cantidad'+id_prod).blur(function(e){
                        if($grid_productos.find('#cantidad'+id_prod).val().trim() == "" ){
                            $grid_productos.find('#cantidad'+id_prod).val(0);
                        }
                        //Redondear de acuerdo al numero de decimales de la unidad de medida.
                        $grid_productos.find('#cantidad'+id_prod).val(parseFloat($grid_productos.find('#cantidad'+id_prod).val()).toFixed(noDec));
                        
                        //alert("td_densidad: "+$grid_productos.find('#densidad'+id_prod).val());
                        
                        if(parseFloat($grid_productos.find('#densidad'+id_prod).val())>0){
							//Convertir los Kilos a Litros
							$grid_productos.find('#cant_litro'+id_prod).val(parseFloat(parseFloat($grid_productos.find('#cantidad'+id_prod).val()) / parseFloat($grid_productos.find('#densidad'+id_prod).val())).toFixed(noDec));
						}
                        
                        $suma_cantidades();
                    });
                    
                    
                    $grid_productos.find('#cantidad'+id_prod).change(function(e){
                        if($grid_productos.find('#cantidad'+id_prod).val().trim() == "" ){
                            $grid_productos.find('#cantidad'+id_prod).val(0);
                        }
                        //Redondear de acuerdo al numero de decimales de la unidad de medida.
                        $grid_productos.find('#cantidad'+id_prod).val(parseFloat($grid_productos.find('#cantidad'+id_prod).val()).toFixed(noDec));
                        
                        if(parseFloat($grid_productos.find('#densidad'+id_prod).val())>0){
							//Convertir los Kilos a Litros
							$grid_productos.find('#cant_litro'+id_prod).val(parseFloat(parseFloat($grid_productos.find('#cantidad'+id_prod).val()) / parseFloat($grid_productos.find('#densidad'+id_prod).val())).toFixed(noDec));
						}
                        
                        $suma_cantidades();
                    });
                    
                    
                    
                    
                    //validar campo Cantidad en Litros, solo acepte numeros y punto
                    $grid_productos.find('#cant_litro'+id_prod).keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                            return true;
                        }else {
                            return false;
                        }
                    });
                    
                    
                    $grid_productos.find('#cant_litro'+id_prod).blur(function(e){
                        if($grid_productos.find('#cant_litro'+id_prod).val().trim() == "" ){
                            $grid_productos.find('#cant_litro'+id_prod).val(0);
                        }
                        //Redondear de acuerdo al numero de decimales de la unidad de medida.
                        $grid_productos.find('#cant_litro'+id_prod).val(parseFloat($grid_productos.find('#cant_litro'+id_prod).val()).toFixed(noDec));
                        
                        if(parseFloat($grid_productos.find('#densidad'+id_prod).val())>0){
							//Convertir los Kilos a Litros
							$grid_productos.find('#cantidad'+id_prod).val(parseFloat(parseFloat($grid_productos.find('#cant_litro'+id_prod).val()) * parseFloat($grid_productos.find('#densidad'+id_prod).val())).toFixed(noDec));
						}
                        
                        $suma_cantidades();
                    });
                    
                    
                    $grid_productos.find('#cant_litro'+id_prod).change(function(e){
                        if($grid_productos.find('#cant_litro'+id_prod).val().trim() == "" ){
                            $grid_productos.find('#cant_litro'+id_prod).val(0);
                        }
                        //Redondear de acuerdo al numero de decimales de la unidad de medida.
                        $grid_productos.find('#cant_litro'+id_prod).val(parseFloat($grid_productos.find('#cant_litro'+id_prod).val()).toFixed(noDec));
                        
                        if(parseFloat($grid_productos.find('#densidad'+id_prod).val())>0){
							//Convertir los Kilos a Litros
							$grid_productos.find('#cantidad'+id_prod).val(parseFloat(parseFloat($grid_productos.find('#cant_litro'+id_prod).val()) * parseFloat($grid_productos.find('#densidad'+id_prod).val())).toFixed(noDec));
						}
                        
                        $suma_cantidades();
                    });
                    
                    
                    
                    //Elimina un producto del grid
                    $grid_productos.find('#eliminaprod'+ id_prod).bind('click',function(event){
                        event.preventDefault();
                        if(parseInt($(this).parent().find('#eliminado').val()) != 0){
                            $id_prod_tmp = $(this).parent().parent().find('#id_prod_grid').val();
                            $cantidad_tmp = $(this).parent().parent().find('input[name=cantidad]').val();

                            //Asigna un 0 al input eliminado como bandera para saber que esta eliminado
                            $(this).parent().find('#eliminado').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
                            //Oculta la fila eliminada
                            $(this).parent().parent().hide();

                            $grid_ids.find('tr').each(function (index){
                                if( $(this).find('#id_prod_gridid').val() == $id_prod_tmp ){
                                    $componente_id_tmp = $(this).find('#id_comp_grid').val();
                                    $cantidad_comp_tmp = $(this).find('#cantidad_prd_comp').val();

                                    $grid_componentes.find('tr').each(function (index){
                                        if($(this).find('#id_comp_grid').val() == $componente_id_tmp ){
                                            $cantidadactual = $(this).find('#cantidadcomp').val();
                                            $cantidad_total = parseFloat($cantidadactual) - (parseFloat($cantidad_comp_tmp) * parseFloat($cantidad_tmp));
                                            if($cantidad_total < 0){
                                                $cantidad_total = 0;
                                            }
                                            $(this).find('#cantidadcomp').val(parseFloat($cantidad_total).toFixed(2));
                                        }
                                    });
                                    $(this).find('#eliminadogridid').val(0);
                                }
                            });
                        }
                    });
                    
					$grid_productos.find('#eliminar'+ trCount).mouseover(function(){
						$(this).removeClass("onmouseOutEliminar").addClass("onmouseOverEliminar");
					});
					$grid_productos.find('#eliminar'+ trCount).mouseout(function(){
						$(this).removeClass("onmouseOverEliminar").addClass("onmouseOutEliminar");
					});
                    
                    /***********************************************************************************
                     * Aqui se crea el TR para el grid de productos componentes de la formula.
                     * Si un producto ya se encuentra en éste grid, se hace una sumatoria para no mostrar varias
                     * veces el mismo producto.
                     ***********************************************************************************/
                    
                    for(var i in componentes){
                        var trCount = $("tr", $grid_componentes).size();
                        trCount++;
                        $skuexiste = 0;
                        $grid_componentes.find('tr').each(function (index){
                            if($(this).find('#skucomp').val()==componentes[i]['sku']){
                                $skuexiste = 1;
                            }
                        });
						
                        if($skuexiste == 0){
                            var tr_complemento='';
							tr_complemento += '<tr>';
								tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
									tr_complemento += '<input type="hidden" name="id_prod_comp" id="id_prod_comp" value="'+id_prod+'">';//el 1 significa que el registro no ha sido eliminado
									tr_complemento += '<input type="hidden" name="eliminadocomp" id="eliminadocomp" value="1">';
									tr_complemento += '<input type="hidden" name="id_comp_grid" id="id_comp_grid" value="'+ componentes[i]['id'] +'">';
									tr_complemento += '<INPUT TYPE="text" id="skucomp" name="skucomp'+ id_prod +'" value="'+ componentes[i]['sku'] +'" class="borde_oculto" style="width:96px;" readOnly="true">';
								tr_complemento += '</td>';
								tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="210">';
									tr_complemento += '<INPUT TYPE="text" id="titulocomp" name="titulocomp'+ id_prod +'" value="'+ componentes[i]['descripcion'] +'" class="borde_oculto" style="width:208px;" readOnly="true">';
								tr_complemento += '</td>';
								
								tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
									tr_complemento += '<INPUT TYPE="text" name="unidadcomp" id="unidadcomp" class="borde_oculto" value="'+ componentes[i]['utitulo'] +'" readOnly="true" style="width:88px;">';
								tr_complemento += '</td>';
								
								tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
									tr_complemento += '<INPUT TYPE="text" name="prescomp" class="borde_oculto" value="'+ componentes[i]['presentacion'] +'" readOnly="true" style="width:88px;">';
								tr_complemento += '</td>';
								
								tr_complemento += '<td class="grid1" id="td_densidad" style="font-size:11px; border:1px solid #C1DAD7;" width="50">'+ parseFloat(componentes[i]['densidad']).toFixed(4) +'</td>';
								tr_complemento += '<td class="grid1" id="td_densidad_promedio" style="font-size:11px; border:1px solid #C1DAD7;" width="65">'+ parseFloat(componentes[i]['densidad_promedio']).toFixed(4) +'</td>';
								
								tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70">';
									var cantidad_mp_kg = parseFloat(componentes[i]['cantidad'])*parseFloat(cantidad);
                                    tr_complemento += '<INPUT TYPE="text" name="cantidadcomp" id="cantidadcomp" value="'+ parseFloat(cantidad_mp_kg).toFixed(parseInt(componentes[i]['no_dec'])) +'" style="width:68px; text-align:right;">';
                                    tr_complemento += '<INPUT TYPE="hidden" name="cantidunitaria" id="cantidunitaria" value="'+parseFloat(componentes[i]['cantidad']).toFixed(componentes[i]['no_dec'])+'" >';
								tr_complemento += '</td>';
								
								var cantidad_mp_litro=0;
								
								if(parseFloat(componentes[i]['densidad'])>0){
									cantidad_mp_litro = parseFloat(cantidad_mp_kg)/parseFloat(componentes[i]['densidad']);
									/*
									if(/^KILO*|KILOGRAMO$/.test(componentes[i]['utitulo'].trim().toUpperCase())){
										cantidad_mp_litro = cantidad_mp_kg;
									}else{
										cantidad_mp_litro = parseFloat(cantidad_mp_kg)/parseFloat(componentes[i]['densidad']);
									}
									*/
								}
								
								tr_complemento += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="70">';
									tr_complemento += '<input type="text" name="cant_comp_litro" id="cant_comp_litro" value="'+ parseFloat(cantidad_mp_litro).toFixed(4) +'" style="width:68px; text-align:right;">';
								tr_complemento += '</td>';
								
							tr_complemento += '</tr>';
                            $grid_componentes.append(tr_complemento);
                        }else{
                            $grid_componentes.find('tr').each(function (index){
                                if($(this).find('#skucomp').val() == componentes[i]['sku']){
                                    $tmp_cantidad = $(this).find('#cantidadcomp').val();
                                    var cantidad_mp_kg = parseFloat(componentes[i]['cantidad'])*parseFloat(cantidad);
                                    
                                    $(this).find('#cantidadcomp').val(parseFloat(parseFloat($tmp_cantidad) + parseFloat(cantidad_mp_kg)).toFixed(4));
                                }
                            });
                        }
                        
                    /***********************************************************************************
                     * Aqui cargamos el grid oculto  con los componentes de la formula
                     ***********************************************************************************/
                        var tr_ids='';
                        tr_ids = '<tr>';
                        tr_ids += '<td >';
                                tr_ids += '<input type="hidden" name="id_prod_gridid" id="id_prod_gridid" value="'+id_prod+'">';//el 1 significa que el registro no ha sido eliminado
                                tr_ids += '<input type="hidden" name="eliminadogridid" id="eliminadogridid" value="1">';
                                tr_ids += '<input type="hidden" name="cantidad_prd_comp" id="cantidad_prd_comp" value="'+parseFloat(componentes[i]['cantidad']).toFixed(4)+'">';
                                tr_ids += '<input type="hidden" name="id_comp_grid" id="id_comp_grid" value="'+ componentes[i]['id'] +'">';
                                tr_ids += '<input type="hidden" name="comp_grid_densidad_prom" id="comp_grid_densidad_prom" value="'+ parseFloat(componentes[i]['densidad_promedio']).toFixed(4) +'">';
                        tr_ids += '</td>';
                        tr_ids += '</tr>';
                        
                        $grid_ids.append(tr_ids);
                    }
                    $grid_componentes.find('tr').find('input').attr({ 'readOnly':true });
                    //$suma_cantidades();
                    
                    //asignar el enfoque
					$grid_productos.find('#cantidad'+id_prod).focus();
					
					if(parseInt(procesoFlujo)<=1){
						//LLamada a la funcion que calcula la densidad promedio del producto sumbensamble(Producto Formulado)
						$calcula_densidad_promedio_producto_subensamble();
					}
            }else{
                if(parseInt(encontrado)==1){
					jAlert('El producto ya se encuentra en el listado, seleccione otro diferente.', 'Atencion!', function(r) { 
						$('#forma-invordpresuben-window').find('input[name=productosku]').val('');
						$('#forma-invordpresuben-window').find('input[name=productosku]').focus(); 
					});
                }
            }
		}//termina agregar producto nuevo al grid
	
	
	
	//Funcion para calcular la densidad promedio del producto formulado
	$calcula_densidad_promedio_producto_subensamble = function(){
		var $grid_productos = $('#forma-invordpresuben-window').find('#grid_productos');
		var $grid_componentes = $('#forma-invordpresuben-window').find('#grid_productos_complementos');
		var $grid_ids = $('#forma-invordpresuben-window').find('#grid_prodid_compid');
		
		$grid_productos.find('tr').each(function (index3){
			var id_subensamble = $(this).find('#id_prod_grid').val();//Id del Producto formulado
			var $densidad_subensamble = $(this).find('input[name=densidad]');//Densidad promedio del Producto formulado
			var $cantidad_kg_subensamble = $(this).find('input[name=cantidad]');//Cantidad en Kg del Producto formulado
			var $cant_litro_subensamble = $(this).find('input[name=cant_litro]');//Cantidad en Litros del Producto formulado
			var noDec = $(this).find('#noDec').val();//Id del Producto formulado
			
			var suma_densidad_subensamble = 0;
			
			if(parseInt($(this).find('#eliminado').val()) != 0){
				$grid_ids.find('tr').each(function (index2){
					var ids_id_subensamble = $(this).find('#id_prod_gridid').val();//Id del Producto formulado
					var ids_comp_grid_densidad_prom = $(this).find('#comp_grid_densidad_prom').val();
					
					if( parseInt(ids_id_subensamble) == parseInt(id_subensamble) ){
						suma_densidad_subensamble = parseFloat(suma_densidad_subensamble) + parseFloat(ids_comp_grid_densidad_prom);
					}
				});
			}
			
			//Se le asigna la densidad al producto subensamble
			$densidad_subensamble.val(parseFloat(suma_densidad_subensamble).toFixed(4));
			
			if(parseFloat($densidad_subensamble.val())>0){
				//Convertir los Kilos a Litros
				$cant_litro_subensamble.val(parseFloat(parseFloat($cantidad_kg_subensamble.val()) / parseFloat($densidad_subensamble.val())).toFixed(noDec));
			}
		});
	}
	
	
	$suma_cantidades = function(){
		var $grid_productos = $('#forma-invordpresuben-window').find('#grid_productos');
		var $grid_componentes = $('#forma-invordpresuben-window').find('#grid_productos_complementos');
		var $grid_ids = $('#forma-invordpresuben-window').find('#grid_prodid_compid');
		
		$grid_componentes.find('tr').each(function (index1){
			if(parseInt($(this).find('#eliminadocomp').val()) != 0){
				var $id_prod_comp = 0;
				var $id_prod_comp = $(this).find('#id_comp_grid').val();//Id de producto materia prima
				var $suma_cantidad_comp_kg = $(this).find('#cantidadcomp');
				var $suma_cantidad_comp_litro = $(this).find('#cant_comp_litro');
				var $td_densidad = $(this).find('#td_densidad');
				var $td_densidad_promedio_mp = $(this).find('#td_densidad_promedio');
				var $unidad_comp = $(this).find('#unidadcomp');
				var suma_componente=0;
				var cantidad_mp_litro=0;
				var suma_densidad_promedio_mp=0;
				
				$grid_ids.find('tr').each(function (index2){
					var ids_id_subensamble = $(this).find('#id_prod_gridid').val();//Id del Producto formulado
					var ids_id_componente = $(this).find('#id_comp_grid').val();//Id de producto materia prima
					var ids_cantidad_comp = $(this).find('#cantidad_prd_comp').val();
					var ids_comp_grid_densidad_prom = $(this).find('#comp_grid_densidad_prom').val();
					
					if(parseInt($(this).find('#eliminadogridid').val()) != 0){
						if( parseInt($id_prod_comp) == parseInt(ids_id_componente) ){
							$grid_productos.find('tr').each(function (index3){
								var id_subensamble = $(this).find('#id_prod_grid').val();//Id del Producto formulado
								var $densidad_subensamble = $(this).find('input[name=densidad]');//Densidad promedio del Producto formulado
								var cantidad_subensamble = $(this).find('input[name=cantidad]').val();
								
								if(parseInt($(this).find('#eliminado').val()) != 0){
									if( parseInt(ids_id_subensamble) == parseInt(id_subensamble) ){
										suma_componente += parseFloat(ids_cantidad_comp) * parseFloat(cantidad_subensamble);
										$suma_cantidad_comp_kg.val(parseFloat(suma_componente).toFixed(4));
										
										suma_densidad_promedio_mp += parseFloat(ids_comp_grid_densidad_prom);
										
										$td_densidad_promedio_mp.html(parseFloat(suma_densidad_promedio_mp).toFixed(4));
									}
								}
							});
							
						}
					}
				});
				
				
				if(parseInt($(this).find('#eliminadocomp').val()) != 0){
					cantidad_mp_litro = parseFloat($suma_cantidad_comp_kg.val())/parseFloat($td_densidad.html());
					/*
					if(parseFloat($td_densidad.html())>0){
						if(/^KILO*|KILOGRAMO$/.test($unidad_comp.val().trim().toUpperCase())){
							cantidad_mp_litro = $suma_cantidad_comp_kg.val();
						}else{
							cantidad_mp_litro = parseFloat($suma_cantidad_comp_kg.val())/parseFloat($td_densidad.html());
						}
					}
					*/
					$suma_cantidad_comp_litro.val(parseFloat(cantidad_mp_litro).toFixed(4));
				}
				
			}
			
			if(parseFloat($(this).find('#cantidadcomp').val()) <= 0 ){
				$(this).hide();
			}else{
				$(this).show();
			}
		});
	}

	
	
	
	
	//Obtener datos del producto formulado para agregar al grid
	$obtener_datos_de_producto = function(id_detalle, sku, cantidad, idPres, procesoFlujo){
		var $sku_prod = $('#forma-invordpresuben-window').find('input[name=productosku]');
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_datos_producto.json';
		$arreglo = {'sku':sku, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
			
		if(sku.trim()!=""){
			$.post(input_json,$arreglo,function(entry){
				if(parseInt(entry['CompProducto'].length) > 0 ){
					if(parseInt(idPres)==0){
						idPres = entry['Producto']['0']['id_pres_def'];
					}
					
					$agrega_producto_al_grid(id_detalle, entry['Producto']['0']['id'],entry['Producto']['0']['sku'],entry['Producto']['0']['descripcion'],entry['Producto']['0']['titulo'], entry['Producto']['0']['no_dec'], entry['CompProducto'], cantidad,  idPres, entry['Presentaciones'], procesoFlujo, entry['Producto']['0']['densidad'], 0);
				}else{
					jAlert('El c&oacute;digo del producto ingresado no es Formulado &oacute; no existe, pruebe ingrese otro diferente.', 'Atencion!', function(r) { 
						$('#forma-invordpresuben-window').find('input[name=productosku]').val('');
						$('#forma-invordpresuben-window').find('input[name=productosku]').focus(); 
					});
				}
			});
		}else{
			jAlert('Es necesario ingresar el c&oacute;digo de un Producto Formulado.', 'Atencion!', function(r) { 
				$('#forma-invordpresuben-window').find('input[name=productosku]').val('');
				$('#forma-invordpresuben-window').find('input[name=productosku]').focus(); 
			});
		}
	}
	
	
	
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
	
	//Nuevo
	$new_item.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_modalboxInvOrdPreSuben();
		
		var form_to_show = 'formaInvOrdPreSuben00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-invordpresuben-window').css({"margin-left": -430, 	"margin-top": -295});
		$forma_selected.prependTo('#forma-invordpresuben-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-invordpresuben-window').find('input[name=identificador]');
		var $accion_proceso = $('#forma-invordpresuben-window').find('input[name=accion_proceso]');
		
		var $folio = $('#forma-invordpresuben-window').find('input[name=folio]');
		var $observaciones = $('#forma-invordpresuben-window').find('textarea[name=observaciones]');
		var $productosku = $('#forma-invordpresuben-window').find('input[name=productosku]');
		var $producto_descripcion = $('#forma-invordpresuben-window').find('input[name=producto_descripcion]');
		var $confirmar_orden = $('#forma-invordpresuben-window').find('#confirmar_orden');
		var $cancelar_orden = $('#forma-invordpresuben-window').find('#cancelar_orden');
		var $select_almacen = $('#forma-invordpresuben-window').find('select[name=select_almacen]');
		
		//href para buscar producto
		var $buscar_producto = $('#forma-invordpresuben-window').find('a[href*=busca_producto]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-invordpresuben-window').find('a[href*=agregar_producto]');
		
		var $grid_productos = $('#forma-invordpresuben-window').find('#grid_productos');
		var $grid_componentes = $('#forma-invordpresuben-window').find('#grid_productos_complementos');
		
		var $cerrar_plugin = $('#forma-invordpresuben-window').find('#close');
		var $cancelar_plugin = $('#forma-invordpresuben-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invordpresuben-window').find('#submit');
		
		var $pdf_orden = $('#forma-invordpresuben-window').find('#pdf_orden');
		
		//quitar enter a todos los campos input
		$('#forma-invordpresuben-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$campo_id.attr({'value' : 0});
		$accion_proceso.attr({'value' : "new"});
		$confirmar_orden.hide();
        $cancelar_orden.hide();
        
        $folio.css({'background' : '#DDDDDD'});
        //$grid_productos_componentes.find('input').attr({ 'readOnly':true });
		var respuestaProcesada = function(data){
			if ( data['success'] == 'true' ){
				var remove = function() {$(this).remove();};
				$('#forma-invordpresuben-overlay').fadeOut(remove);
				jAlert("La Orden de Producci&oacute;n se ha Guardado.", 'Atencion!');
				$get_datos_grid();
			}
			else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invordpresuben-window').find('div.interrogacion').css({'display':'none'});
				$('#forma-invordpresuben-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-invordpresuben-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				$grid_componentes.find('#cantidadcomp').css({'background' : '#ffffff'});
				$grid_componentes.find('input[name=prescomp]').css({'background' : '#ffffff'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-invordpresuben-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						/*
						name="sku'+ id_prod +'"
						name="titulo'+ id_prod +'"
						*/
						if( tmp.split(':')[0].substring(0, 8) == 'cantidad' ){
							$('#forma-invordpresuben-window').find('.invordpresuben_div_one').css({'height':'570px'});
							$('#forma-invordpresuben-window').find('#div_warning_grid').css({'display':'block'});
							//alert("cantidad:"+ tmp.split(':')[0]);
							//$grid_productos.find('#cantidad'+tmp.split(':')[0].substring(8, 9) ).css({'background' : '#d41000'});
							$grid_productos.find('#'+tmp.split(':')[0] ).css({'background' : '#d41000'});
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><img src="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="120"><input type="text" value="' + $grid_productos.find('input[name=sku'+ tmp.split(':')[0].substring(8, 13) +']').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
									tr_warning += '<td width="200"><input type="text" value="' + $grid_productos.find('input[name=titulo'+ tmp.split(':')[0].substring(8, 13) +']').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
									tr_warning += '<td width="420"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:410px; color:red"></td>';
							tr_warning += '</tr>';
							$('#forma-invordpresuben-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						} 
						
						if(tmp.split(':')[0].split('_')[0]=='idprod' || tmp.split(':')[0].split('_')[0]=='presentacion'){
							$('#forma-invordpresuben-window').find('.invordpresuben_div_one').css({'height':'570px'});
							$('#forma-invordpresuben-window').find('#div_warning_grid').css({'display':'block'});
							
							if(parseInt($("tr", $grid_componentes).size())>0){
								for ( var i=1; i<=parseInt($("tr", $grid_componentes).size()); i++){
									if( parseInt($grid_componentes.find('#id_comp_grid').eq(parseInt(i) - 1).val()) == parseInt(tmp.split(':')[0].split('_')[1]) ){
										//alert("id-prod_grid:"+$grid_componentes.find('#id_comp_grid').eq(parseInt(i) - 1).val() + "     id: "+parseInt(tmp.split(':')[0].split('_')[1]));
										
										if(tmp.split(':')[0].split('_')[0]=='idprod'){
											$grid_componentes.find('#cantidadcomp').eq(parseInt(i) - 1).css({'background' : '#d41000'});
										}
										
										if(tmp.split(':')[0].split('_')[0]=='presentacion'){
											$grid_componentes.find('input[name=prescomp]').eq(parseInt(i) - 1).css({'background' : '#d41000'});
										}
										
										var tr_warning = '<tr>';
												tr_warning += '<td width="20"><div><img src="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
												tr_warning += '<td width="120"><input type="text" value="' + $grid_componentes.find('#skucomp').eq(parseInt(i) - 1).val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
												tr_warning += '<td width="200"><input type="text" value="' +$grid_componentes.find('#titulocomp').eq(parseInt(i) - 1).val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
												tr_warning += '<td width="420"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:410px; color:red"></td>';
										tr_warning += '</tr>';
										$('#forma-invordpresuben-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
									}
									
								}
							}
						}
						
						
					}
				}
				$('#forma-invordpresuben-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
				$('#forma-invordpresuben-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});			
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvOrdSub.json';
		$arreglo = {'id':id_to_show, 'iu': $('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			//carga select almacen
			$select_almacen.children().remove();
			var almacen_hmtl="";
			$.each(entry['Almacenes'],function(entryIndex,alm){
				if(parseInt(alm['id']) == parseInt(entry['Extras'][0]['id_alm_prod'])){
					almacen_hmtl += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
				}
			});
			$select_almacen.append(almacen_hmtl);
			
			//Link de Buscar
			$buscar_producto.click(function(event){
				event.preventDefault();
				$busca_productos($productosku.val(), $producto_descripcion.val(), entry['ProdTipos']);
			});
		});//termina llamada json
		
         

		
		//agregar producto al grid
		$agregar_producto.click(function(event){
			event.preventDefault();
			$obtener_datos_de_producto(0, $productosku.val(), "1", 0,0);
		});
		
		//Desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$(this).aplicarEventoKeypressEjecutaTrigger($productosku, $agregar_producto);
		
		//Desencadena clic del href Buscar Producto al pulsar enter en el campo Nombre del producto
		$(this).aplicarEventoKeypressEjecutaTrigger($producto_descripcion, $buscar_producto);
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_componentes).size();
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert("No hay datos para actualizar", 'Atencion!');
				return false;
			}
		});
         
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invordpresuben-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invordpresuben-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$productosku.focus();
		
	});//termina nuevo
	
        
        
        
        
	
	//Obtener datos de los productos de la orden de produccion
	var $get_datos_edit_produccion = function(id_detalle, id_producto, sku, descripcion, unidad, no_dec, pres_id, cantidad, densidad, proceso_flujo_id, estatus){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosEditProductoFormulado.json';
		$arreglo = {'id_det':id_detalle, 'id_prod':id_producto, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
		
		$.post(input_json,$arreglo,function(entry2){
			if(parseInt(entry2['CompProducto'].length) > 0 ){
				
				$agrega_producto_al_grid(id_detalle, id_producto, sku, descripcion, unidad, no_dec, entry2['CompProducto'], cantidad, pres_id, entry2['Presentaciones'], proceso_flujo_id, densidad, estatus);
				
			}else{
				jAlert('El c&oacute;digo del producto ingresado no es Formulado &oacute; no existe, pruebe ingrese otro diferente.', 'Atencion!', function(r) { 
					$('#forma-invordpresuben-window').find('input[name=productosku]').val('');
					$('#forma-invordpresuben-window').find('input[name=productosku]').focus(); 
				});
			}
		});
	}
					

        
        
        
        
	
	var carga_formaInvOrdSuben00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			
			jConfirm('Realmente desea eliminar la Orden', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La orden fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La orden no puden ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			
			//aqui  entra para editar un registro
			var form_to_show = 'formaInvOrdPreSuben00';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_modalboxInvOrdPreSuben();
			
			$('#forma-invordpresuben-window').css({"margin-left": -430, 	"margin-top": -295});
			$forma_selected.prependTo('#forma-invordpresuben-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-invordpresuben-window').find('input[name=identificador]');
			var $accion_proceso = $('#forma-invordpresuben-window').find('input[name=accion_proceso]');
			
			var $folio = $('#forma-invordpresuben-window').find('input[name=folio]');
			var $observaciones = $('#forma-invordpresuben-window').find('textarea[name=observaciones]');
			var $productosku = $('#forma-invordpresuben-window').find('input[name=productosku]');
			var $producto_descripcion = $('#forma-invordpresuben-window').find('input[name=producto_descripcion]');
			var $select_almacen = $('#forma-invordpresuben-window').find('select[name=select_almacen]');
			
			//var $pdf_orden = $('#forma-invordpresuben-window').find('#pdf_orden');
			var $confirmar_orden = $('#forma-invordpresuben-window').find('#confirmar_orden');
			var $cancelar_orden = $('#forma-invordpresuben-window').find('#cancelar_orden');
			
			//href para buscar producto
			var $buscar_producto = $('#forma-invordpresuben-window').find('a[href*=busca_producto]');
			//href para agregar producto al grid
			var $agregar_producto = $('#forma-invordpresuben-window').find('a[href*=agregar_producto]');
			
			var $grid_productos = $('#forma-invordpresuben-window').find('#grid_productos');
			var $grid_productos_componentes=$('#forma-invordpresuben-window').find('#grid_productos_complementos');
			
			var $cerrar_plugin = $('#forma-invordpresuben-window').find('#close');
			var $cancelar_plugin = $('#forma-invordpresuben-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invordpresuben-window').find('#submit');
			
			
			//quitar enter a todos los campos input
			$('#forma-invordpresuben-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			$buscar_producto.hide();
			$agregar_producto.hide();
			$confirmar_orden.hide();
			$cancelar_orden.hide();
			
			$folio.css({'background' : '#DDDDDD'});
			
			if(accion_mode == 'edit'){
				$accion_proceso.attr({'value' : "edit"});
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvOrdSub.json';
				$arreglo = {'id':id_to_show, 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-invordpresuben-overlay').fadeOut(remove);
						jAlert("La orden se ha actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-invordpresuben-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-invordpresuben-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								if( tmp.split(':')[0].substring(0, 8) == 'cantidad' ){
									$('#forma-invordpresuben-window').find('.invordpresuben_div_one').css({'height':'570px'});
									$('#forma-invordpresuben-window').find('#div_warning_grid').css({'display':'block'});
									alert("cantidad:"+ tmp.split(':')[0]);
									//$grid_productos.find('#cantidad'+tmp.split(':')[0].substring(8, 9) ).css({'background' : '#d41000'});
									$grid_productos.find('#'+tmp.split(':')[0] ).css({'background' : '#d41000'});
									var tr_warning = '<tr>';
											tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
											tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $grid_productos.find('input[name=sku'+ tmp.split(':')[0].substring(8, 13) +']').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
											tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $grid_productos.find('input[name=titulo'+ tmp.split(':')[0].substring(8, 13) +']').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
											tr_warning += '<td width="420"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:410px; color:red"></td>';
									tr_warning += '</tr>';
									$('#forma-invordpresuben-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								} 
								
								if(tmp.split(':')[0].split('_')[0] == 'idprod' || tmp.split(':')[0].split('_')[0]=='presentacion'){
									$('#forma-invordpresuben-window').find('.invordpresuben_div_one').css({'height':'570px'});
									$('#forma-invordpresuben-window').find('#div_warning_grid').css({'display':'block'});
									
									if(parseInt($("tr", $grid_productos_componentes).size())>0){
										for ( var i=1; i<=parseInt($("tr", $grid_productos_componentes).size()); i++){
											if( parseInt($grid_productos_componentes.find('#id_comp_grid').eq(parseInt(i) - 1).val()) == parseInt(tmp.split(':')[0].split('_')[1]) ){
												//alert("id-prod_grid:"+$grid_componentes.find('#id_comp_grid').eq(parseInt(i) - 1).val() + "     id: "+parseInt(tmp.split(':')[0].split('_')[1]));
												//$grid_productos_componentes.find('#cantidadcomp').eq(parseInt(i) - 1).css({'background' : '#d41000'});
												
												if(tmp.split(':')[0].split('_')[0]=='idprod'){
													$grid_productos_componentes.find('#cantidadcomp').eq(parseInt(i) - 1).css({'background' : '#d41000'});
												}
												
												if(tmp.split(':')[0].split('_')[0]=='presentacion'){
													$grid_productos_componentes.find('input[name=prescomp]').eq(parseInt(i) - 1).css({'background' : '#d41000'});
												}
										
												var tr_warning = '<tr>';
														tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
														tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $grid_productos_componentes.find('#skucomp').eq(parseInt(i) - 1).val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
														tr_warning += '<td width="200"><INPUT TYPE="text" value="' +$grid_productos_componentes.find('#titulocomp').eq(parseInt(i) - 1).val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
														tr_warning += '<td width="420"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:410px; color:red"></td>';
												tr_warning += '</tr>';
												$('#forma-invordpresuben-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
											}
											
										}
									}
								}
							}
						}
						
						$('#forma-invordpresuben-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
						$('#forma-invordpresuben-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});			
						
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					//alert(entry['InvOrdSub']['0']['id']);
					$campo_id.attr({'value' : entry['InvOrdSub']['0']['id']});
					$folio.attr({'value' : entry['InvOrdSub']['0']['folio']});
					$observaciones.val(entry['InvOrdSub']['0']['comentarios']);
					
					//carga select almacen
					$select_almacen.children().remove();
					var almacen_hmtl="";
					$.each(entry['Almacenes'],function(entryIndex,alm){
						if(parseInt(alm['id']) == parseInt(entry['Extras'][0]['id_alm_prod'])){
							almacen_hmtl += '<option value="' + alm['id'] + '"  selected="yes">' + alm['titulo'] + '</option>';
						}
					});
					$select_almacen.append(almacen_hmtl);
					
					for(var i in entry['Detalle']){
						$get_datos_edit_produccion(entry['Detalle'][i]['id'], entry['Detalle'][i]['prod_id'], entry['Detalle'][i]['sku'], entry['Detalle'][i]['descripcion'], entry['Detalle'][i]['unidad'], entry['Detalle'][i]['no_dec'], entry['Detalle'][i]['presentacion_id'], entry['Detalle'][i]['cantidad'], entry['Detalle'][i]['densidad'], entry['InvOrdSub']['0']['proceso_flujo_id'], entry['InvOrdSub']['0']['estatus']);
					}
					

					
					
					if(parseInt(entry['InvOrdSub']['0']['proceso_flujo_id'])==1){
						$confirmar_orden.show();
						$agregar_producto.show();
						$buscar_producto.show();
						$cancelar_orden.show();
						
						$buscar_producto.click(function(event){
							event.preventDefault();
							$busca_productos($productosku.val(), $producto_descripcion.val(), entry['ProdTipos']);
						});
						
						//agregar producto al grid
						$agregar_producto.click(function(event){
							event.preventDefault();
							$obtener_datos_de_producto($productosku.val(), "1", 0, 0);
						});
						
						//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
						$(this).aplicarEventoKeypressEjecutaTrigger($productosku, $agregar_producto);
						
						//desencadena clic del href Buscar Producto al pulsar enter en el campo Nombre del producto
						$(this).aplicarEventoKeypressEjecutaTrigger($producto_descripcion, $buscar_producto);
					}
					
					
					if(parseInt(entry['InvOrdSub']['0']['proceso_flujo_id'])==2){
						$confirmar_orden.hide();
						$buscar_producto.hide();
						$observaciones.attr('disabled','-1');
						$productosku.attr('disabled','-1');
						$producto_descripcion.attr('disabled','-1');
						$grid_productos.find('tr').find('input').attr({ 'readOnly':true });
					}
					
					//confirmado y enterado
					if(parseInt(entry['InvOrdSub']['0']['estatus'])==0  ||  parseInt(entry['InvOrdSub']['0']['estatus'])==1){
						$cancelar_orden.show();
						//quitar enter a todos los campos input
						$grid_productos.find('input').keypress(function(e){
							if(e.which==13 ) {
								return false;
							}
						});
					}
					
					//en proceso, listo y cancelado
					if(parseInt(entry['InvOrdSub']['0']['estatus'])==4 || parseInt(entry['InvOrdSub']['0']['estatus'])==2 || parseInt(entry['InvOrdSub']['0']['estatus'])==3){
						$cancelar_orden.hide();
						$confirmar_orden.hide();
						$buscar_producto.hide();
						$observaciones.attr('disabled','-1');
						$productosku.attr('disabled','-1');
						$producto_descripcion.attr('disabled','-1');
						
						$submit_actualizar.hide();
						
						//quitar enter a todos los campos input
						$grid_productos.find('input').keypress(function(e){
							if(e.which==13 ) {
								return false;
							}
						});
					}
					
				},"json");//termina llamada json
				
				
				
				$confirmar_orden.click(function(e){
					$accion_proceso.attr({'value' : "confirm"});
					jConfirm('Desea confirmar la orden ?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.parents("FORM").submit();
						}else{
							$accion_proceso.attr({'value' : "edit"});
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				$cancelar_orden.click(function(e){
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCancelarOrden.json';
					$arreglo1 = {	'id_subensamble':$campo_id.val(),
									'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
								};
					$.post(input_json2,$arreglo1,function(entry1){
						jAlert(entry1['success'], 'Atencion!');
					},"json");//termina llamada json
				});
				
				
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invordpresuben-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invordpresuben-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
				$productosku.focus();
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllInvOrdPreSuben.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllInvOrdPreSuben.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaInvOrdSuben00_for_datagrid00);
                
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



