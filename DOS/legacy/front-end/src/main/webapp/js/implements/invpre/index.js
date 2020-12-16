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
	var controller = $contextpath.val()+"/controllers/invpre";
    
    //arreglo para select Base Precio
    var array_base_precio = {
				1:"Costo Ultima Entrada", 
				2:"Costo Pormedio", 
				3:"Lista de Precio 1",
				4:"Lista de Precio 2",
				5:"Lista de Precio 3",
				6:"Lista de Precio 4",
				7:"Lista de Precio 5",
				8:"Lista de Precio 6",
				9:"Lista de Precio 7",
				10:"Lista de Precio 8",
				11:"Lista de Precio 9",
				12:"Lista de Precio 10"
			};
    
    //arreglo para select  de operacion para el calculo
    var array_operacion_calculo = {
				1:"Porcentaje (%)"
				//2:"Suma (+/-)", 
				//3:"Multiplicaci&oacute;n (*)",
				//4:"Divisi&oacute;n (/)"
			};
    
    //Arreglo para select  de Forma de calculo
    var array_forma_calculo = {
				1:"Autom&aacute;tico (En l&iacute;nea)", 
				//2:"Semiautom&aacute;tico (Proceso)", 
				3:"Manual (Captura Directa)"
			};

    //arreglo para select Tipo de Redondeo
    var array_tipo_redondeo = {
				1:"Sin Redondeo", 
				2:"A 5 Centavos", 
				3:"A 50 Centavos",
				4:"Sin Centavos"
			};
			
	var rolVendedor=0;
	
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Precios');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
	var $cadena_busqueda = "";
	var $busqueda_select_tipo_prod = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_tipo_prod]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	var $busqueda_select_pres = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_pres]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "tipo_prod" + signo_separador + $busqueda_select_tipo_prod.val() + "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		valor_retorno += "presentacion" + signo_separador + $busqueda_select_pres.val() + "|";
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
	
	
	$cargar_datos_buscador_principal= function(){
		var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosBuscadorPrincipal.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_lineas,$arreglo,function(data){
			
			//carga select de tipos de producto
			$busqueda_select_tipo_prod.children().remove();
			//var prodtipos = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			var prodtipos = '';
			$.each(data['ProdTipos'],function(entryIndex,tp){
				if(parseInt(tp['id'])==1){
					prodtipos += '<option value="' + tp['id'] + '" selected="yes">' + tp['titulo'] + '</option>';
				}else{
					//if(parseInt(tp['id'])!=3 && parseInt(tp['id'])!=4){
						prodtipos += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
					//}
				}
			});
			$busqueda_select_tipo_prod.append(prodtipos);
			
			//carga select de Presentaciones
			$busqueda_select_pres.children().remove();
			var presentacion = '<option value="0">[-Presentaci&oacute;n--]</option>';
			$.each(data['Presentaciones'],function(entryIndex,pres){
				presentacion += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
			});
			$busqueda_select_pres.append(presentacion);
			
			rolVendedor=data['Extra'][0]['rol_agente_venta'];
			
			if(parseInt(rolVendedor)>=1){
				//SI no es Administrador ocultar boton Actualizar
				$new_invprodlineas.hide();
			}
		});
	}//termina funcion cargar datos buscador principal
	
	
	//ejecutar la funcion cargar datos al cargar la pagina por primera vez
	$cargar_datos_buscador_principal();
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_codigo.val('');
		$busqueda_descripcion.val('');
		$cargar_datos_buscador_principal();
		$busqueda_codigo.focus();
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
		$busqueda_codigo.focus();
	});
	
	
	//aplicar evento keypress a campos para ejecutar la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_descripcion, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_pres, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_tipo_prod, $buscar);
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-invpre-window').find('#submit').mouseover(function(){
			$('#forma-invpre-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-invpre-window').find('#submit').mouseout(function(){
			$('#forma-invpre-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-invpre-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invpre-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invpre-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invpre-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invpre-window').find('#close').mouseover(function(){
			$('#forma-invpre-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		
		$('#forma-invpre-window').find('#close').mouseout(function(){
			$('#forma-invpre-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-invpre-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invpre-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invpre-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invpre-window').find("ul.pestanas li").click(function() {
			$('#forma-invpre-window').find(".contenidoPes").hide();
			$('#forma-invpre-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invpre-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
		
		
		/*codigo para las pestañas internas*/
		$('#forma-invpre-window').find(".contenidoPes_internas").hide(); //Hide all content
		$('#forma-invpre-window').find("ul.pestanas_internas li:first").addClass("active_internas").show(); //Activate first tab
		$('#forma-invpre-window').find(".contenidoPes_internas:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invpre-window').find("ul.pestanas_internas li").click(function() {
			$('#forma-invpre-window').find(".contenidoPes_internas").hide();
			$('#forma-invpre-window').find("ul.pestanas_internas li").removeClass("active_internas");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invpre-window').find( activeTab , "ul.pestanas_internas li" ).fadeIn().show();
			$(this).addClass("active_internas");
			return false;
		});
	}
	
	
	//buscador de productos
	$busca_productos = function($select_presentacion, sku, descripcion){
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
				if(parseInt(pt['id'])==1 ){
					prod_tipos_html += '<option value="' + pt['id'] + '" selected="yes">' + pt['titulo'] + '</option>';
				}else{
					prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
				}
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		
		$campo_sku.val(sku);
		$campo_descripcion.val(descripcion);
		$campo_sku.focus();
		
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
					var id_producto = $(this).find('#id_prod_buscador').val();
					$('#forma-invpre-window').find('input[name=producto_id]').val(id_producto);
					$('#forma-invpre-window').find('input[name=productosku]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-invpre-window').find('input[name=producto_descripcion]').val($(this).find('span.titulo_prod_buscador').html());
					$('#forma-invpre-window').find('input[name=producto_unidad]').val($(this).find('span.utitulo').html());
					
					//aqui nos vamos a buscar las presentaciones del Producto seleccionado
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesProducto.json';
					$arreglo = {'id_prod':id_producto,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
					
					$.post(input_json,$arreglo,function(entry){
						//verifica si el arreglo  retorno datos
						if (entry['Presentaciones'].length > 0){
							$select_presentacion.children().remove();
							var html_pres = '';
							$.each(entry['Presentaciones'],function(entryIndex,pres){
								html_pres += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
							});
							$select_presentacion.append(html_pres);
						}else{
							$select_presentacion.children().remove();
							var html_pres = '<option value="0">[-Presentaci&oacute;n--]</option>';
							$select_presentacion.append(html_pres);
						}
					});
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					
					$('#forma-invpre-window').find('input[name=lista1]').focus();
				});
			});
		});
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_sku.val() != ''  ||  $campo_descripcion.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}
		
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_sku, $buscar_plugin_producto);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_descripcion, $buscar_plugin_producto);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_producto, $buscar_plugin_producto);
		
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaproducto-overlay').fadeOut(remove);
			
			//asignar el enfoque al campo sku del producto
			$('#forma-invpre-window').find('input[name=productosku]').focus();
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
	
	$accion_focus = function($campo){
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo.focus(function(e){
			$valor_tmp = $(this).val().split(",").join("");
			
			if( ($valor_tmp != '') && ($valor_tmp != ' ') && ($valor_tmp != null) ){
				if(parseFloat($valor_tmp)<1){
					$campo.val('');
				}else{
					$campo.val($(this).agregar_comas(parseFloat($valor_tmp).toFixed(2)));
				}
			}
		});
	}
	
	$accio_blur = function($campo){
		//recalcula importe al perder enfoque el campo costo
		$campo.blur(function(){
			$valor_tmp = $(this).val().split(",").join("");
			
			if ($valor_tmp == ''  || $valor_tmp == null){
					$(this).val('0.00');
			}
			
			if( ($valor_tmp != '') && ($valor_tmp != ' ') ){
				$campo.val($(this).agregar_comas(parseFloat($valor_tmp).toFixed(2)));
			}else{
				$(this).val('0.00');
			}
		});
	}
	
     
	//carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero){
		$campo_select.children().remove();
		var select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
			}
		}
		$campo_select.append(select_html);
	}
        
        
	//carga los campos select con los datos que recibe como parametro
	$carga_campos_select_moneda = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero){
		$campo_select.children().remove();
		var select_html = '';
		
		if(texto_elemento_cero != ""){
			select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		}
		
		$.each(arreglo_elementos,function(entryIndex,elemento){
			if( parseInt(elemento['id']) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + elemento['id'] + '" selected="yes">' + elemento['descripcion_abr'] + '</option>';
			}else{
				select_html += '<option value="' + elemento['id'] + '" >' + elemento['descripcion_abr'] + '</option>';
			}
		});
		$campo_select.append(select_html);
	}
        
        
	$aplicar_accion_focus_blur_y_solo_numeros = function($lista1,$lista2,$lista3,$lista4,$lista5,$lista6,$lista7,$lista8,$lista9,$lista10,$descto1,$descto2,$descto3,$descto4,$descto5,$descto6,$descto7,$descto8,$descto9,$descto10,$valor_default_l1,$valor_default_l2,$valor_default_l3,$valor_default_l4,$valor_default_l5,$valor_default_l6,$valor_default_l7,$valor_default_l8,$valor_default_l9,$valor_default_l10	){
		/*para que permita solo numeros*/
		$permitir_solo_numeros($lista1);
		$permitir_solo_numeros($lista2);
		$permitir_solo_numeros($lista3);
		$permitir_solo_numeros($lista4);
		$permitir_solo_numeros($lista5);
		$permitir_solo_numeros($lista6);
		$permitir_solo_numeros($lista7);
		$permitir_solo_numeros($lista8);
		$permitir_solo_numeros($lista9);
		$permitir_solo_numeros($lista10);
		$permitir_solo_numeros($descto1);
		$permitir_solo_numeros($descto2);
		$permitir_solo_numeros($descto3);
		$permitir_solo_numeros($descto4);
		$permitir_solo_numeros($descto5);
		$permitir_solo_numeros($descto6);
		$permitir_solo_numeros($descto7);
		$permitir_solo_numeros($descto8);
		$permitir_solo_numeros($descto9);
		$permitir_solo_numeros($descto10);
		$permitir_solo_numeros($valor_default_l1);
		$permitir_solo_numeros($valor_default_l2);
		$permitir_solo_numeros($valor_default_l3);
		$permitir_solo_numeros($valor_default_l4);
		$permitir_solo_numeros($valor_default_l5);
		$permitir_solo_numeros($valor_default_l6);
		$permitir_solo_numeros($valor_default_l7);
		$permitir_solo_numeros($valor_default_l8);
		$permitir_solo_numeros($valor_default_l9);
		$permitir_solo_numeros($valor_default_l10);
		
		/*Aplica accion al obtener el enfoque*/
		$accion_focus($lista1);
		$accion_focus($lista2);
		$accion_focus($lista3);
		$accion_focus($lista4);
		$accion_focus($lista5);
		$accion_focus($lista6);
		$accion_focus($lista7);
		$accion_focus($lista8);
		$accion_focus($lista9);
		$accion_focus($lista10);
		$accion_focus($descto1);
		$accion_focus($descto2);
		$accion_focus($descto3);
		$accion_focus($descto4);
		$accion_focus($descto5);
		$accion_focus($descto6);
		$accion_focus($descto7);
		$accion_focus($descto8);
		$accion_focus($descto9);
		$accion_focus($descto10);
		$accion_focus($valor_default_l1);
		$accion_focus($valor_default_l2);
		$accion_focus($valor_default_l3);
		$accion_focus($valor_default_l4);
		$accion_focus($valor_default_l5);
		$accion_focus($valor_default_l6);
		$accion_focus($valor_default_l7);
		$accion_focus($valor_default_l8);
		$accion_focus($valor_default_l9);
		$accion_focus($valor_default_l10);
		
		
		/*Aplica accion al perder el enfoque*/
		$accio_blur($lista1);
		$accio_blur($lista2);
		$accio_blur($lista3);
		$accio_blur($lista4);
		$accio_blur($lista5);
		$accio_blur($lista6);
		$accio_blur($lista7);
		$accio_blur($lista8);
		$accio_blur($lista9);
		$accio_blur($lista10);
		$accio_blur($descto1);
		$accio_blur($descto2);
		$accio_blur($descto3);
		$accio_blur($descto4);
		$accio_blur($descto5);
		$accio_blur($descto6);
		$accio_blur($descto7);
		$accio_blur($descto8);
		$accio_blur($descto9);
		$accio_blur($descto10);
		$accio_blur($valor_default_l1);
		$accio_blur($valor_default_l2);
		$accio_blur($valor_default_l3);
		$accio_blur($valor_default_l4);
		$accio_blur($valor_default_l5);
		$accio_blur($valor_default_l6);
		$accio_blur($valor_default_l7);
		$accio_blur($valor_default_l8);
		$accio_blur($valor_default_l9);
		$accio_blur($valor_default_l10);
	}
	
	
	$inicializa_valores = function($lista1,$lista2,$lista3,$lista4,$lista5,$lista6,$lista7,$lista8,$lista9,$lista10,$descto1,$descto2,$descto3,$descto4,$descto5,$descto6,$descto7,$descto8,$descto9,$descto10,$valor_default_l1,$valor_default_l2,$valor_default_l3,$valor_default_l4,$valor_default_l5,$valor_default_l6,$valor_default_l7,$valor_default_l8,$valor_default_l9,$valor_default_l10	){
		/*para que inicialize en cero*/
		$add_ceros($lista1);
		$add_ceros($lista2);
		$add_ceros($lista3);
		$add_ceros($lista4);
		$add_ceros($lista5);
		$add_ceros($lista6);
		$add_ceros($lista7);
		$add_ceros($lista8);
		$add_ceros($lista9);
		$add_ceros($lista10);
		$add_ceros($descto1);
		$add_ceros($descto2);
		$add_ceros($descto3);
		$add_ceros($descto4);
		$add_ceros($descto5);
		$add_ceros($descto6);
		$add_ceros($descto7);
		$add_ceros($descto8);
		$add_ceros($descto9);
		$add_ceros($descto10);
		$add_ceros($valor_default_l1);
		$add_ceros($valor_default_l2);
		$add_ceros($valor_default_l3);
		$add_ceros($valor_default_l4);
		$add_ceros($valor_default_l5);
		$add_ceros($valor_default_l6);
		$add_ceros($valor_default_l7);
		$add_ceros($valor_default_l8);
		$add_ceros($valor_default_l9);
		$add_ceros($valor_default_l10);
		
	}

        
        
	//nuevo centro de costo
	$new_invprodlineas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_modalboxInvPre();
		
		var form_to_show = 'formaInvPre00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-invpre-window').css({"margin-left": -390, 	"margin-top": -210});
		$forma_selected.prependTo('#forma-invpre-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-invpre-window').find('input[name=identificador]');
		var $producto_id = $('#forma-invpre-window').find('input[name=producto_id]');
		var $productosku = $('#forma-invpre-window').find('input[name=productosku]');
		var $producto_descripcion = $('#forma-invpre-window').find('input[name=producto_descripcion]');
		var $producto_unidad = $('#forma-invpre-window').find('input[name=producto_unidad]');
		var $select_presentacion = $('#forma-invpre-window').find('select[name=select_presentacion]');
		
		var $lista1 = $('#forma-invpre-window').find('input[name=lista1]');
		var $lista2 = $('#forma-invpre-window').find('input[name=lista2]');
		var $lista3 = $('#forma-invpre-window').find('input[name=lista3]');
		var $lista4 = $('#forma-invpre-window').find('input[name=lista4]');
		var $lista5 = $('#forma-invpre-window').find('input[name=lista5]');
		var $lista6 = $('#forma-invpre-window').find('input[name=lista6]');
		var $lista7 = $('#forma-invpre-window').find('input[name=lista7]');
		var $lista8 = $('#forma-invpre-window').find('input[name=lista8]');
		var $lista9 = $('#forma-invpre-window').find('input[name=lista9]');
		var $lista10 = $('#forma-invpre-window').find('input[name=lista10]');
		var $select_moneda1 = $('#forma-invpre-window').find('select[name=select_moneda1]');
		var $select_moneda2 = $('#forma-invpre-window').find('select[name=select_moneda2]');
		var $select_moneda3 = $('#forma-invpre-window').find('select[name=select_moneda3]');
		var $select_moneda4 = $('#forma-invpre-window').find('select[name=select_moneda4]');
		var $select_moneda5 = $('#forma-invpre-window').find('select[name=select_moneda5]');
		var $select_moneda6 = $('#forma-invpre-window').find('select[name=select_moneda6]');
		var $select_moneda7 = $('#forma-invpre-window').find('select[name=select_moneda7]');
		var $select_moneda8 = $('#forma-invpre-window').find('select[name=select_moneda8]');
		var $select_moneda9 = $('#forma-invpre-window').find('select[name=select_moneda9]');
		var $select_moneda10 = $('#forma-invpre-window').find('select[name=select_moneda10]');
		var $descto1 = $('#forma-invpre-window').find('input[name=descto1]');
		var $descto2 = $('#forma-invpre-window').find('input[name=descto2]');
		var $descto3 = $('#forma-invpre-window').find('input[name=descto3]');
		var $descto4 = $('#forma-invpre-window').find('input[name=descto4]');
		var $descto5 = $('#forma-invpre-window').find('input[name=descto5]');
		var $descto6 = $('#forma-invpre-window').find('input[name=descto6]');
		var $descto7 = $('#forma-invpre-window').find('input[name=descto7]');
		var $descto8 = $('#forma-invpre-window').find('input[name=descto8]');
		var $descto9 = $('#forma-invpre-window').find('input[name=descto9]');
		var $descto10 = $('#forma-invpre-window').find('input[name=descto10]');
		
		var $valor_default_l1 = $('#forma-invpre-window').find('input[name=valor_default_l1]');
		var $select_base_precio1 = $('#forma-invpre-window').find('select[name=select_base_precio1]');
		var $select_operacion1 = $('#forma-invpre-window').find('select[name=select_operacion1]');
		var $select_forma_calculo1 = $('#forma-invpre-window').find('select[name=select_forma_calculo1]');
		var $select_tipo_redondeo1 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo1]');
		
		var $valor_default_l2 = $('#forma-invpre-window').find('input[name=valor_default_l2]');
		var $select_base_precio2 = $('#forma-invpre-window').find('select[name=select_base_precio2]');
		var $select_operacion2 = $('#forma-invpre-window').find('select[name=select_operacion2]');
		var $select_forma_calculo2 = $('#forma-invpre-window').find('select[name=select_forma_calculo2]');
		var $select_tipo_redondeo2 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo2]');
		
		var $valor_default_l3 = $('#forma-invpre-window').find('input[name=valor_default_l3]');
		var $select_base_precio3 = $('#forma-invpre-window').find('select[name=select_base_precio3]');
		var $select_operacion3 = $('#forma-invpre-window').find('select[name=select_operacion3]');
		var $select_forma_calculo3 = $('#forma-invpre-window').find('select[name=select_forma_calculo3]');
		var $select_tipo_redondeo3 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo3]');
		
		var $valor_default_l4 = $('#forma-invpre-window').find('input[name=valor_default_l4]');
		var $select_base_precio4 = $('#forma-invpre-window').find('select[name=select_base_precio4]');
		var $select_operacion4 = $('#forma-invpre-window').find('select[name=select_operacion4]');
		var $select_forma_calculo4 = $('#forma-invpre-window').find('select[name=select_forma_calculo4]');
		var $select_tipo_redondeo4 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo4]');
		
		var $valor_default_l5 = $('#forma-invpre-window').find('input[name=valor_default_l5]');
		var $select_base_precio5 = $('#forma-invpre-window').find('select[name=select_base_precio5]');
		var $select_operacion5 = $('#forma-invpre-window').find('select[name=select_operacion5]');
		var $select_forma_calculo5 = $('#forma-invpre-window').find('select[name=select_forma_calculo5]');
		var $select_tipo_redondeo5 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo5]');
		
		var $valor_default_l6 = $('#forma-invpre-window').find('input[name=valor_default_l6]');
		var $select_base_precio6 = $('#forma-invpre-window').find('select[name=select_base_precio6]');
		var $select_operacion6 = $('#forma-invpre-window').find('select[name=select_operacion6]');
		var $select_forma_calculo6 = $('#forma-invpre-window').find('select[name=select_forma_calculo6]');
		var $select_tipo_redondeo6 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo6]');
		
		var $valor_default_l7 = $('#forma-invpre-window').find('input[name=valor_default_l7]');
		var $select_base_precio7 = $('#forma-invpre-window').find('select[name=select_base_precio7]');
		var $select_operacion7 = $('#forma-invpre-window').find('select[name=select_operacion7]');
		var $select_forma_calculo7 = $('#forma-invpre-window').find('select[name=select_forma_calculo7]');
		var $select_tipo_redondeo7 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo7]');
		
		var $valor_default_l8 = $('#forma-invpre-window').find('input[name=valor_default_l8]');
		var $select_base_precio8 = $('#forma-invpre-window').find('select[name=select_base_precio8]');
		var $select_operacion8 = $('#forma-invpre-window').find('select[name=select_operacion8]');
		var $select_forma_calculo8 = $('#forma-invpre-window').find('select[name=select_forma_calculo8]');
		var $select_tipo_redondeo8 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo8]');
		
		var $valor_default_l9 = $('#forma-invpre-window').find('input[name=valor_default_l9]');
		var $select_base_precio9 = $('#forma-invpre-window').find('select[name=select_base_precio9]');
		var $select_operacion9 = $('#forma-invpre-window').find('select[name=select_operacion9]');
		var $select_forma_calculo9 = $('#forma-invpre-window').find('select[name=select_forma_calculo9]');
		var $select_tipo_redondeo9 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo9]');
		
		var $valor_default_l10 = $('#forma-invpre-window').find('input[name=valor_default_l10]');
		var $select_base_precio10 = $('#forma-invpre-window').find('select[name=select_base_precio10]');
		var $select_operacion10 = $('#forma-invpre-window').find('select[name=select_operacion10]');
		var $select_forma_calculo10 = $('#forma-invpre-window').find('select[name=select_forma_calculo10]');
		var $select_tipo_redondeo10 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo10]');
		
		//href para buscar producto
		var $buscar_producto = $('#forma-invpre-window').find('a[href*=busca_producto]');
		var $agrega_producto = $('#forma-invpre-window').find('a[href*=agrega_producto]');
		
		var $cerrar_plugin = $('#forma-invpre-window').find('#close');
		var $cancelar_plugin = $('#forma-invpre-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invpre-window').find('#submit');
		
		$campo_id.attr({'value' : 0});
		$producto_id.attr({'value' : 0});
		$producto_unidad.css({'background' : '#F0F0F0'});
		
		var elemento_seleccionado = 0;
		var cadena_elemento_cero ="";
		
		
		//quitar enter a todos los campos input
		$('#forma-invpre-window').find('input[name=producto_descripcion]').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		//quitar enter a todos los campos input
		$('#forma-invpre-window').find('input[name=productosku]').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		//quitar enter a todos los campos input
		$('#forma-invpre-window').find('input[name=producto_unidad]').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		if(parseInt(rolVendedor)>=1){
			//Ocultar boton Actualizar
			$submit_actualizar.hide();
			
			//Quitar enter a todos los campos input
			$('#forma-invpre-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
		}
		
		$productosku.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == 'true' ){
				var remove = function() {$(this).remove();};
				$('#forma-invpre-overlay').fadeOut(remove);
				jAlert("Los precios se han actualizado.", 'Atencion!');
				$get_datos_grid();
			}
			else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invpre-window').find('div.interrogacion').css({'display':'none'});

				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
							$('#forma-invpre-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvPre.json';
		$arreglo = {
					'id':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		$.post(input_json,$arreglo,function(entry){
			//cargar selects de Monedas
			cadena_elemento_cero ="[----]";
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista1'];
			$carga_campos_select_moneda($select_moneda1, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista2'];
			$carga_campos_select_moneda($select_moneda2, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista3'];
			$carga_campos_select_moneda($select_moneda3, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista4'];
			$carga_campos_select_moneda($select_moneda4, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista5'];
			$carga_campos_select_moneda($select_moneda5, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista6'];
			$carga_campos_select_moneda($select_moneda6, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista7'];
			$carga_campos_select_moneda($select_moneda7, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista8'];
			$carga_campos_select_moneda($select_moneda8, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista9'];
			$carga_campos_select_moneda($select_moneda9, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			elemento_seleccionado = entry['InvPre'][0]['moneda_lista10'];
			$carga_campos_select_moneda($select_moneda10, entry['Monedas'],elemento_seleccionado, cadena_elemento_cero);
			
		});//termina llamada json
		
		
		
		$buscar_producto.click(function(event){
			event.preventDefault();
			$busca_productos($select_presentacion, $productosku.val(), $producto_descripcion.val());
		});
		
		
		$agrega_producto.click(function(event){
			event.preventDefault();
			var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/gatDatosProducto.json';
			var $arreglo2 = {	'codigo':$productosku.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			
			$.post(input_json2,$arreglo2,function(entry2){
				if(parseInt(entry2['Producto'].length) > 0 ){
					$('#forma-invpre-window').find('input[name=producto_id]').val(entry2['Producto'][0]['id']);
					$('#forma-invpre-window').find('input[name=productosku]').val(entry2['Producto'][0]['sku']);
					$('#forma-invpre-window').find('input[name=producto_descripcion]').val(entry2['Producto'][0]['descripcion']);
					$('#forma-invpre-window').find('input[name=producto_unidad]').val( entry2['Producto'][0]['unidad'] );
					
					//verifica si el arreglo  retorno datos
					if (entry2['Presentaciones'].length > 0){
						$select_presentacion.children().remove();
						var html_pres = '';
						$.each(entry2['Presentaciones'],function(entryIndex,pres){
							html_pres += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
						});
						$select_presentacion.append(html_pres);
					}else{
						$select_presentacion.children().remove();
						var html_pres = '<option value="0">[-Presentaci&oacute;n--]</option>';
						$select_presentacion.append(html_pres);
					}
				}
			});
		});
		
		
		
		
		//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$productosku.keypress(function(e){
			if(e.which == 13){
				$agrega_producto.trigger('click');
				return false;
			}
		});
		
		//desencadena clic del href Buscar producto al pulsar enter en el campo sku del producto
		$producto_descripcion.keypress(function(e){
			if(e.which == 13){
				$buscar_producto.trigger('click');
				return false;
			}
		});		
		
		
		//inicializa campos en cero
		$inicializa_valores($lista1,$lista2,$lista3,$lista4,$lista5,$lista6,$lista7,$lista8,$lista9,$lista10,$descto1,$descto2,$descto3,$descto4,$descto5,$descto6,$descto7,$descto8,$descto9,$descto10,$valor_default_l1,$valor_default_l2,$valor_default_l3,$valor_default_l4,$valor_default_l5,$valor_default_l6,$valor_default_l7,$valor_default_l8,$valor_default_l9,$valor_default_l10);
		
		//aplicar evento focus, blur y permitir solo numeros en los campos
		$aplicar_accion_focus_blur_y_solo_numeros($lista1,$lista2,$lista3,$lista4,$lista5,$lista6,$lista7,$lista8,$lista9,$lista10,$descto1,$descto2,$descto3,$descto4,$descto5,$descto6,$descto7,$descto8,$descto9,$descto10,$valor_default_l1,$valor_default_l2,$valor_default_l3,$valor_default_l4,$valor_default_l5,$valor_default_l6,$valor_default_l7,$valor_default_l8,$valor_default_l9,$valor_default_l10);
		
		//cargar selects de precio base
		elemento_seleccionado = 0;
		cadena_elemento_cero ="[--Seleccionar Lista--]";
		$carga_campos_select($select_base_precio1, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_base_precio2, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_base_precio3, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_base_precio4, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_base_precio5, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_base_precio6, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_base_precio7, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_base_precio8, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_base_precio9, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_base_precio10, array_base_precio,elemento_seleccionado, cadena_elemento_cero);
		
		
		//cargar selects de tipos de operacion para el calculo
		elemento_seleccionado = 0;
		cadena_elemento_cero ="[--Seleccionar Operaci&oacute;n--]";
		$carga_campos_select($select_operacion1, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_operacion2, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_operacion3, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_operacion4, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_operacion5, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_operacion6, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_operacion7, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_operacion8, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_operacion9, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_operacion10, array_operacion_calculo,elemento_seleccionado, cadena_elemento_cero);
		
		
		//cargar selects de Forma de calculo
		elemento_seleccionado = 0;
		cadena_elemento_cero ="[--Seleccionar Forma--]";
		$carga_campos_select($select_forma_calculo1, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_forma_calculo2, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_forma_calculo3, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_forma_calculo4, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_forma_calculo5, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_forma_calculo6, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_forma_calculo7, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_forma_calculo8, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_forma_calculo9, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_forma_calculo10, array_forma_calculo,elemento_seleccionado, cadena_elemento_cero);
		
		
		//cargar selects de Tipo de Redondeo
		elemento_seleccionado = 0;
		cadena_elemento_cero ="[--Seleccionar Redondeo--]";
		$carga_campos_select($select_tipo_redondeo1, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_tipo_redondeo2, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_tipo_redondeo3, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_tipo_redondeo4, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_tipo_redondeo5, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_tipo_redondeo6, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_tipo_redondeo7, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_tipo_redondeo8, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_tipo_redondeo9, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);
		$carga_campos_select($select_tipo_redondeo10, array_tipo_redondeo,elemento_seleccionado, cadena_elemento_cero);	
		
		
		
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invpre-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invpre-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
	});
	
        
	
	var carga_formaInvPre00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar los precios para el producto', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("Los precios fueron eliminados exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("Los precios no puden ser eliminados", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaInvPre00';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_modalboxInvPre();
			$('#forma-invpre-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-invpre-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-invpre-window').find('input[name=identificador]');
			var $producto_id = $('#forma-invpre-window').find('input[name=producto_id]');
			var $productosku = $('#forma-invpre-window').find('input[name=productosku]');
			var $producto_descripcion = $('#forma-invpre-window').find('input[name=producto_descripcion]');
			var $producto_unidad = $('#forma-invpre-window').find('input[name=producto_unidad]');
			var $select_presentacion = $('#forma-invpre-window').find('select[name=select_presentacion]');
			
			var $lista1 = $('#forma-invpre-window').find('input[name=lista1]');
			var $lista2 = $('#forma-invpre-window').find('input[name=lista2]');
			var $lista3 = $('#forma-invpre-window').find('input[name=lista3]');
			var $lista4 = $('#forma-invpre-window').find('input[name=lista4]');
			var $lista5 = $('#forma-invpre-window').find('input[name=lista5]');
			var $lista6 = $('#forma-invpre-window').find('input[name=lista6]');
			var $lista7 = $('#forma-invpre-window').find('input[name=lista7]');
			var $lista8 = $('#forma-invpre-window').find('input[name=lista8]');
			var $lista9 = $('#forma-invpre-window').find('input[name=lista9]');
			var $select_moneda1 = $('#forma-invpre-window').find('select[name=select_moneda1]');
			var $select_moneda2 = $('#forma-invpre-window').find('select[name=select_moneda2]');
			var $select_moneda3 = $('#forma-invpre-window').find('select[name=select_moneda3]');
			var $select_moneda4 = $('#forma-invpre-window').find('select[name=select_moneda4]');
			var $select_moneda5 = $('#forma-invpre-window').find('select[name=select_moneda5]');
			var $select_moneda6 = $('#forma-invpre-window').find('select[name=select_moneda6]');
			var $select_moneda7 = $('#forma-invpre-window').find('select[name=select_moneda7]');
			var $select_moneda8 = $('#forma-invpre-window').find('select[name=select_moneda8]');
			var $select_moneda9 = $('#forma-invpre-window').find('select[name=select_moneda9]');
			var $select_moneda10 = $('#forma-invpre-window').find('select[name=select_moneda10]');
			var $lista10 = $('#forma-invpre-window').find('input[name=lista10]');
			var $descto1 = $('#forma-invpre-window').find('input[name=descto1]');
			var $descto2 = $('#forma-invpre-window').find('input[name=descto2]');
			var $descto3 = $('#forma-invpre-window').find('input[name=descto3]');
			var $descto4 = $('#forma-invpre-window').find('input[name=descto4]');
			var $descto5 = $('#forma-invpre-window').find('input[name=descto5]');
			var $descto6 = $('#forma-invpre-window').find('input[name=descto6]');
			var $descto7 = $('#forma-invpre-window').find('input[name=descto7]');
			var $descto8 = $('#forma-invpre-window').find('input[name=descto8]');
			var $descto9 = $('#forma-invpre-window').find('input[name=descto9]');
			var $descto10 = $('#forma-invpre-window').find('input[name=descto10]');
			
			var $valor_default_l1 = $('#forma-invpre-window').find('input[name=valor_default_l1]');
			var $select_base_precio1 = $('#forma-invpre-window').find('select[name=select_base_precio1]');
			var $select_operacion1 = $('#forma-invpre-window').find('select[name=select_operacion1]');
			var $select_forma_calculo1 = $('#forma-invpre-window').find('select[name=select_forma_calculo1]');
			var $select_tipo_redondeo1 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo1]');
			
			var $valor_default_l2 = $('#forma-invpre-window').find('input[name=valor_default_l2]');
			var $select_base_precio2 = $('#forma-invpre-window').find('select[name=select_base_precio2]');
			var $select_operacion2 = $('#forma-invpre-window').find('select[name=select_operacion2]');
			var $select_forma_calculo2 = $('#forma-invpre-window').find('select[name=select_forma_calculo2]');
			var $select_tipo_redondeo2 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo2]');
			
			var $valor_default_l3 = $('#forma-invpre-window').find('input[name=valor_default_l3]');
			var $select_base_precio3 = $('#forma-invpre-window').find('select[name=select_base_precio3]');
			var $select_operacion3 = $('#forma-invpre-window').find('select[name=select_operacion3]');
			var $select_forma_calculo3 = $('#forma-invpre-window').find('select[name=select_forma_calculo3]');
			var $select_tipo_redondeo3 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo3]');
			
			var $valor_default_l4 = $('#forma-invpre-window').find('input[name=valor_default_l4]');
			var $select_base_precio4 = $('#forma-invpre-window').find('select[name=select_base_precio4]');
			var $select_operacion4 = $('#forma-invpre-window').find('select[name=select_operacion4]');
			var $select_forma_calculo4 = $('#forma-invpre-window').find('select[name=select_forma_calculo4]');
			var $select_tipo_redondeo4 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo4]');
			
			var $valor_default_l5 = $('#forma-invpre-window').find('input[name=valor_default_l5]');
			var $select_base_precio5 = $('#forma-invpre-window').find('select[name=select_base_precio5]');
			var $select_operacion5 = $('#forma-invpre-window').find('select[name=select_operacion5]');
			var $select_forma_calculo5 = $('#forma-invpre-window').find('select[name=select_forma_calculo5]');
			var $select_tipo_redondeo5 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo5]');
			
			var $valor_default_l6 = $('#forma-invpre-window').find('input[name=valor_default_l6]');
			var $select_base_precio6 = $('#forma-invpre-window').find('select[name=select_base_precio6]');
			var $select_operacion6 = $('#forma-invpre-window').find('select[name=select_operacion6]');
			var $select_forma_calculo6 = $('#forma-invpre-window').find('select[name=select_forma_calculo6]');
			var $select_tipo_redondeo6 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo6]');
			
			var $valor_default_l7 = $('#forma-invpre-window').find('input[name=valor_default_l7]');
			var $select_base_precio7 = $('#forma-invpre-window').find('select[name=select_base_precio7]');
			var $select_operacion7 = $('#forma-invpre-window').find('select[name=select_operacion7]');
			var $select_forma_calculo7 = $('#forma-invpre-window').find('select[name=select_forma_calculo7]');
			var $select_tipo_redondeo7 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo7]');
			
			var $valor_default_l8 = $('#forma-invpre-window').find('input[name=valor_default_l8]');
			var $select_base_precio8 = $('#forma-invpre-window').find('select[name=select_base_precio8]');
			var $select_operacion8 = $('#forma-invpre-window').find('select[name=select_operacion8]');
			var $select_forma_calculo8 = $('#forma-invpre-window').find('select[name=select_forma_calculo8]');
			var $select_tipo_redondeo8 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo8]');
			
			var $valor_default_l9 = $('#forma-invpre-window').find('input[name=valor_default_l9]');
			var $select_base_precio9 = $('#forma-invpre-window').find('select[name=select_base_precio9]');
			var $select_operacion9 = $('#forma-invpre-window').find('select[name=select_operacion9]');
			var $select_forma_calculo9 = $('#forma-invpre-window').find('select[name=select_forma_calculo9]');
			var $select_tipo_redondeo9 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo9]');
			
			var $valor_default_l10 = $('#forma-invpre-window').find('input[name=valor_default_l10]');
			var $select_base_precio10 = $('#forma-invpre-window').find('select[name=select_base_precio10]');
			var $select_operacion10 = $('#forma-invpre-window').find('select[name=select_operacion10]');
			var $select_forma_calculo10 = $('#forma-invpre-window').find('select[name=select_forma_calculo10]');
			var $select_tipo_redondeo10 = $('#forma-invpre-window').find('select[name=select_tipo_redondeo10]');
			
			//href para buscar producto
			var $buscar_producto = $('#forma-invpre-window').find('a[href*=busca_producto]');
			var $agrega_producto = $('#forma-invpre-window').find('a[href*=agrega_producto]');
			
			var $cerrar_plugin = $('#forma-invpre-window').find('#close');
			var $cancelar_plugin = $('#forma-invpre-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invpre-window').find('#submit');
			
			$buscar_producto.hide();
			$agrega_producto.hide();
			$producto_unidad.css({'background' : '#F0F0F0'});
			$productosku.css({'background' : '#F0F0F0'});
			$producto_descripcion.css({'background' : '#F0F0F0'});
			$productosku.attr('readonly',true);
			$producto_descripcion.attr('readonly',true);
			
			
			//quitar enter a todos los campos input
			$('#forma-invpre-window').find('input[name=producto_descripcion]').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			//quitar enter a todos los campos input
			$('#forma-invpre-window').find('input[name=productosku]').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			//quitar enter a todos los campos input
			$('#forma-invpre-window').find('input[name=producto_unidad]').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			if(parseInt(rolVendedor)>=1){
				//Ocultar boton Actualizar
				$submit_actualizar.hide();
				
				//Quitar enter a todos los campos input
				$('#forma-invpre-window').find('input').keypress(function(e){
					if(e.which==13 ) {
						return false;
					}
				});
			}
			
			$lista1.focus();//asignar el cursor al campo Lista 1 al cargar la ventana
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvPre.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-invpre-overlay').fadeOut(remove);
						jAlert("Los precios para este producto se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-invpre-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-invpre-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					
					$campo_id.attr({'value' : entry['InvPre']['0']['id']});
					$producto_id.attr({'value' : entry['InvPre']['0']['inv_prod_id']});
					$productosku.attr({'value' : entry['InvPre']['0']['sku']});
					$producto_descripcion.attr({'value' : entry['InvPre']['0']['titulo']});
					$producto_unidad.attr({'value' : entry['InvPre']['0']['utitulo']});
					
					$select_presentacion.children().remove();
					var html_pres = '';
					if(parseInt(entry['InvPre']['0']['presentacion_id'])==0 ){
						html_pres = '<option value="0" selected="yes">[--Presentaci&oacute;n--]</option>';
					}else{
						html_pres = '';
					}
					$.each(entry['Presentaciones'],function(entryIndex,pres){
						if(parseInt(entry['InvPre']['0']['presentacion_id']) == parseInt(pres['id'] )){
							html_pres += '<option value="' + pres['id'] + '" selected="yes">' + pres['titulo'] + '</option>';
						}else{
							//html_pres += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
						}
					});
					$select_presentacion.append(html_pres);
					
					$lista1.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_1']).toFixed(2))});
					$lista2.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_2']).toFixed(2))});
					$lista3.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_3']).toFixed(2))});
					$lista4.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_4']).toFixed(2))});
					$lista5.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_5']).toFixed(2))});
					$lista6.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_6']).toFixed(2))});
					$lista7.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_7']).toFixed(2))});
					$lista8.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_8']).toFixed(2))});
					$lista9.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_9']).toFixed(2))});
					$lista10.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['precio_10']).toFixed(2))});
					
					//cargar selects de Monedas
					var elemento_seleccionado = 0;
					var cadena_elemento_cero ="";
					$carga_campos_select_moneda($select_moneda1, entry['Monedas'],entry['InvPre']['0']['id_mon1'], cadena_elemento_cero);
					$carga_campos_select_moneda($select_moneda2, entry['Monedas'],entry['InvPre']['0']['id_mon2'], cadena_elemento_cero);
					$carga_campos_select_moneda($select_moneda3, entry['Monedas'],entry['InvPre']['0']['id_mon3'], cadena_elemento_cero);
					$carga_campos_select_moneda($select_moneda4, entry['Monedas'],entry['InvPre']['0']['id_mon4'], cadena_elemento_cero);
					$carga_campos_select_moneda($select_moneda5, entry['Monedas'],entry['InvPre']['0']['id_mon5'], cadena_elemento_cero);
					$carga_campos_select_moneda($select_moneda6, entry['Monedas'],entry['InvPre']['0']['id_mon6'], cadena_elemento_cero);
					$carga_campos_select_moneda($select_moneda7, entry['Monedas'],entry['InvPre']['0']['id_mon7'], cadena_elemento_cero);
					$carga_campos_select_moneda($select_moneda8, entry['Monedas'],entry['InvPre']['0']['id_mon8'], cadena_elemento_cero);
					$carga_campos_select_moneda($select_moneda9, entry['Monedas'],entry['InvPre']['0']['id_mon9'], cadena_elemento_cero);
					$carga_campos_select_moneda($select_moneda10, entry['Monedas'],entry['InvPre']['0']['id_mon10'], cadena_elemento_cero);
					
					$descto1.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_1']).toFixed(2))});
					$descto2.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_2']).toFixed(2))});
					$descto3.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_3']).toFixed(2))});
					$descto4.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_4']).toFixed(2))});
					$descto5.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_5']).toFixed(2))});
					$descto6.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_6']).toFixed(2))});
					$descto7.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_7']).toFixed(2))});
					$descto8.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_8']).toFixed(2))});
					$descto9.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_9']).toFixed(2))});
					$descto10.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['descuento_10']).toFixed(2))});
					
					
					$valor_default_l1.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_1']).toFixed(2))});
					$valor_default_l2.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_2']).toFixed(2))});
					$valor_default_l3.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_3']).toFixed(2))});
					$valor_default_l4.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_4']).toFixed(2))});
					$valor_default_l5.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_5']).toFixed(2))});
					$valor_default_l6.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_6']).toFixed(2))});
					$valor_default_l7.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_7']).toFixed(2))});
					$valor_default_l8.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_8']).toFixed(2))});
					$valor_default_l9.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_9']).toFixed(2))});
					$valor_default_l10.attr({'value' : $(this).agregar_comas(parseFloat(entry['InvPre']['0']['default_precio_10']).toFixed(2))});
					
					
					//cargar selects de precio base
					cadena_elemento_cero ="[--Seleccionar Lista--]";
					$carga_campos_select($select_base_precio1, array_base_precio,entry['InvPre']['0']['base_precio_1'], cadena_elemento_cero);
					$carga_campos_select($select_base_precio2, array_base_precio,entry['InvPre']['0']['base_precio_2'], cadena_elemento_cero);
					$carga_campos_select($select_base_precio3, array_base_precio,entry['InvPre']['0']['base_precio_3'], cadena_elemento_cero);
					$carga_campos_select($select_base_precio4, array_base_precio,entry['InvPre']['0']['base_precio_4'], cadena_elemento_cero);
					$carga_campos_select($select_base_precio5, array_base_precio,entry['InvPre']['0']['base_precio_5'], cadena_elemento_cero);
					$carga_campos_select($select_base_precio6, array_base_precio,entry['InvPre']['0']['base_precio_6'], cadena_elemento_cero);
					$carga_campos_select($select_base_precio7, array_base_precio,entry['InvPre']['0']['base_precio_7'], cadena_elemento_cero);
					$carga_campos_select($select_base_precio8, array_base_precio,entry['InvPre']['0']['base_precio_8'], cadena_elemento_cero);
					$carga_campos_select($select_base_precio9, array_base_precio,entry['InvPre']['0']['base_precio_9'], cadena_elemento_cero);
					$carga_campos_select($select_base_precio10, array_base_precio,entry['InvPre']['0']['base_precio_10'], cadena_elemento_cero);
					
					
					//cargar selects de tipos de operacion para el calculo
					cadena_elemento_cero ="[--Seleccionar Operaci&oacute;n--]";
					$carga_campos_select($select_operacion1, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_1'], cadena_elemento_cero);
					$carga_campos_select($select_operacion2, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_2'], cadena_elemento_cero);
					$carga_campos_select($select_operacion3, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_3'], cadena_elemento_cero);
					$carga_campos_select($select_operacion4, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_4'], cadena_elemento_cero);
					$carga_campos_select($select_operacion5, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_5'], cadena_elemento_cero);
					$carga_campos_select($select_operacion6, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_6'], cadena_elemento_cero);
					$carga_campos_select($select_operacion7, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_7'], cadena_elemento_cero);
					$carga_campos_select($select_operacion8, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_8'], cadena_elemento_cero);
					$carga_campos_select($select_operacion9, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_9'], cadena_elemento_cero);
					$carga_campos_select($select_operacion10, array_operacion_calculo,entry['InvPre']['0']['operacion_precio_10'], cadena_elemento_cero);
					
					
					//cargar selects de Forma de calculo
					cadena_elemento_cero ="[--Seleccionar Forma--]";
					$carga_campos_select($select_forma_calculo1, array_forma_calculo,entry['InvPre']['0']['calculo_precio_1'], cadena_elemento_cero);
					$carga_campos_select($select_forma_calculo2, array_forma_calculo,entry['InvPre']['0']['calculo_precio_2'], cadena_elemento_cero);
					$carga_campos_select($select_forma_calculo3, array_forma_calculo,entry['InvPre']['0']['calculo_precio_3'], cadena_elemento_cero);
					$carga_campos_select($select_forma_calculo4, array_forma_calculo,entry['InvPre']['0']['calculo_precio_4'], cadena_elemento_cero);
					$carga_campos_select($select_forma_calculo5, array_forma_calculo,entry['InvPre']['0']['calculo_precio_5'], cadena_elemento_cero);
					$carga_campos_select($select_forma_calculo6, array_forma_calculo,entry['InvPre']['0']['calculo_precio_6'], cadena_elemento_cero);
					$carga_campos_select($select_forma_calculo7, array_forma_calculo,entry['InvPre']['0']['calculo_precio_7'], cadena_elemento_cero);
					$carga_campos_select($select_forma_calculo8, array_forma_calculo,entry['InvPre']['0']['calculo_precio_8'], cadena_elemento_cero);
					$carga_campos_select($select_forma_calculo9, array_forma_calculo,entry['InvPre']['0']['calculo_precio_9'], cadena_elemento_cero);
					$carga_campos_select($select_forma_calculo10, array_forma_calculo,entry['InvPre']['0']['calculo_precio_10'], cadena_elemento_cero);
		
  
					//cargar selects de Tipo de Redondeo
					cadena_elemento_cero ="[--Seleccionar Redondeo--]";
					$carga_campos_select($select_tipo_redondeo1, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_1'], cadena_elemento_cero);
					$carga_campos_select($select_tipo_redondeo2, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_2'], cadena_elemento_cero);
					$carga_campos_select($select_tipo_redondeo3, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_3'], cadena_elemento_cero);
					$carga_campos_select($select_tipo_redondeo4, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_4'], cadena_elemento_cero);
					$carga_campos_select($select_tipo_redondeo5, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_5'], cadena_elemento_cero);
					$carga_campos_select($select_tipo_redondeo6, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_6'], cadena_elemento_cero);
					$carga_campos_select($select_tipo_redondeo7, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_7'], cadena_elemento_cero);
					$carga_campos_select($select_tipo_redondeo8, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_8'], cadena_elemento_cero);
					$carga_campos_select($select_tipo_redondeo9, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_9'], cadena_elemento_cero);
					$carga_campos_select($select_tipo_redondeo10, array_tipo_redondeo,entry['InvPre']['0']['redondeo_precio_10'], cadena_elemento_cero);
																		
		
				},"json");//termina llamada json
				
				
				
				//aplicar evento focus, blur y permitir solo numeros en los campos
				$aplicar_accion_focus_blur_y_solo_numeros($lista1,$lista2,$lista3,$lista4,$lista5,$lista6,$lista7,$lista8,$lista9,$lista10,$descto1,$descto2,$descto3,$descto4,$descto5,$descto6,$descto7,$descto8,$descto9,$descto10,$valor_default_l1,$valor_default_l2,$valor_default_l3,$valor_default_l4,$valor_default_l5,$valor_default_l6,$valor_default_l7,$valor_default_l8,$valor_default_l9,$valor_default_l10);
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invpre-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invpre-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllInvPre.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllInvPre.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaInvPre00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
            
			if(parseInt(rolVendedor)>=1){
				//SI no es Administrador ocultar iconos de eliminar en el grid
				$('#lienzo_recalculable').find('a.cancelar_item').hide();
			}
			
        },"json");
    }

    $get_datos_grid();
    
    
});



