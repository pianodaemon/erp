$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	var arrayTProd;
	var arrayLineas;
	var arrayMarcas;
	var rolVendedor=0;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
	var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/productos";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_producto = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Productos');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $sku_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=sku_busqueda]');
	var $campo_descripcion_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=descripcion]');
	var $select_tipo_productos_busqueda = $('#barra_buscador').find('.tabla_buscador').find('select[name=tipo_productos]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		
		var signo_separador = "=";
		valor_retorno += "sku_busqueda" + signo_separador + $sku_busqueda.val() + "|";
		valor_retorno += "descripcion_busqueda" + signo_separador + $campo_descripcion_busqueda.val() + "|";
		valor_retorno += "por_tipo" + signo_separador + $select_tipo_productos_busqueda.val() + "|";
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
	
	var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInit.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
		//Llena el select tipos de productos en el buscador
		$select_tipo_productos_busqueda.children().remove();
		var prod_tipos_html = '<option value="0" selected="yes">[-- --]</option>';
		$.each(data['prodTipos'],function(entryIndex,pt){
			prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
		});
		$select_tipo_productos_busqueda.append(prod_tipos_html);
		
		arrayTProd = data['prodTipos'];
		arrayLineas = data['Lineas'];
		arrayMarcas = data['Marcas'];
		arrayMonedas = data['Monedas'];
		
		rolVendedor=data['Extra'][0]['rol_agente_venta'];
		
		if(parseInt(rolVendedor)>=1){
			//SI no es Administrador ocultar boton Actualizar
			$new_producto.hide();
		}
	});
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		$sku_busqueda.val('');
		$campo_descripcion_busqueda.val('');
		
		//Llena el select tipos de productos en el buscador
		$select_tipo_productos_busqueda.children().remove();
		var tipos_html = '<option value="0" selected="yes">[-- --]</option>';
		$.each(arrayTProd,function(entryIndex,pt){
			tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
		});
		$select_tipo_productos_busqueda.append(tipos_html);
		
		$sku_busqueda.focus();
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
		
		$sku_busqueda.focus();
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($sku_busqueda, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_descripcion_busqueda, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_productos_busqueda, $buscar);
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-product-window').find('select[name=select_prodtipo]');
		var $incluye_produccion = $('#forma-product-window').find('input[name=incluye_pro]');
		
		$('#forma-product-window').find('#submit').mouseover(function(){
			$('#forma-product-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		})
		$('#forma-product-window').find('#submit').mouseout(function(){
			$('#forma-product-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		})
		$('#forma-product-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-product-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-product-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-product-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-product-window').find('#close').mouseover(function(){
			$('#forma-product-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-product-window').find('#close').mouseout(function(){
			$('#forma-product-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-product-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-product-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-product-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-product-window').find("ul.pestanas li").click(function() {
			$('#forma-product-window').find(".contenidoPes").hide();
			$('#forma-product-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-product-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			
			if(activeTab == '#tabx-1'){
				if( parseInt($select_prod_tipo.val())==1 || parseInt($select_prod_tipo.val())==2 || parseInt($select_prod_tipo.val())==3 || parseInt($select_prod_tipo.val())==8){
					if($incluye_produccion.val()=='false'){
						if( parseInt($select_prod_tipo.val())==1 || parseInt($select_prod_tipo.val())==2 || parseInt($select_prod_tipo.val())==8){
							$('#forma-product-window').find('.product_div_one').css({'height':'505px'});
						}
					}else{
						$('#forma-product-window').find('.product_div_one').css({'height':'350px'});
					}
					
					if( parseInt($select_prod_tipo.val())==3){
						$('#forma-product-window').find('.product_div_one').css({'height':'505px'});
					}
				}else{
					$('#forma-product-window').find('.product_div_one').css({'height':'350px'});
				}
			}
			
			if(activeTab == '#tabx-2'){
				$('#forma-product-window').find('.product_div_one').css({'height':'430px'});
			}
			
			if(activeTab == '#tabx-3'){
				$('#forma-product-window').find('.product_div_one').css({'height':'220px'});
			}
			
			if(activeTab == '#tabx-4'){
				$('#forma-product-window').find('.product_div_one').css({'height':'470px'});
			}
			//$finction_redimensiona_divs($('#forma-product-window'));
			return false;
		});
	}
	
	
	solo_numeros = function($campo_input){
		//validar campo cantidad, solo acepte numeros y punto
		$campo_input.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB
			if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});
	}
	
	
	
	//buscador de Cuentas Contables
	$busca_cuentas_contables = function(tipo, nivel_cta, arrayCtasMayor){
		//limpiar_campos_grids();
		$(this).modalPanel_buscactacontable();
		var $dialogoc =  $('#forma-buscactacontable-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_cuentas').find('table.formaBusqueda_cuentas').clone());
		
		$('#forma-buscactacontable-window').css({"margin-left": -200, 	"margin-top": -160});
		
		var $tabla_resultados = $('#forma-buscactacontable-window').find('#tabla_resultado');
		
		var $select_cta_mayor = $('#forma-buscactacontable-window').find('select[name=select_cta_mayor]');
		var $campo_clasif = $('#forma-buscactacontable-window').find('input[name=clasif]');
		var $campo_cuenta = $('#forma-buscactacontable-window').find('input[name=cuenta]');
		var $campo_scuenta = $('#forma-buscactacontable-window').find('input[name=scuenta]');
		var $campo_sscuenta = $('#forma-buscactacontable-window').find('input[name=sscuenta]');
		var $campo_ssscuenta = $('#forma-buscactacontable-window').find('input[name=ssscuenta]');
		var $campo_sssscuenta = $('#forma-buscactacontable-window').find('input[name=sssscuenta]');
		var $campo_descripcion = $('#forma-buscactacontable-window').find('input[name=campo_descripcion]');
		
		var $boton_busca = $('#forma-buscactacontable-window').find('#boton_busca');
		var $boton_cencela = $('#forma-buscactacontable-window').find('#boton_cencela');
		var mayor_seleccionado=0;
		var detalle=0;
		var clasifica=0;
		
		$campo_cuenta.hide();
		$campo_scuenta.hide();
		$campo_sscuenta.hide();
		$campo_ssscuenta.hide();
		$campo_sssscuenta.hide();
		
		solo_numeros($campo_clasif);
		solo_numeros($campo_cuenta);
		solo_numeros($campo_scuenta);
		solo_numeros($campo_sscuenta);
		solo_numeros($campo_ssscuenta);
		solo_numeros($campo_sssscuenta);
		
		//funcionalidad botones
		$boton_busca.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		
		$boton_busca.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$boton_cencela.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$boton_cencela.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		if(parseInt(nivel_cta) >=1 ){ $campo_cuenta.show(); };
		if(parseInt(nivel_cta) >=2 ){ $campo_scuenta.show(); };
		if(parseInt(nivel_cta) >=3 ){ $campo_sscuenta.show(); };
		if(parseInt(nivel_cta) >=4 ){ $campo_ssscuenta.show(); };
		if(parseInt(nivel_cta) >=5 ){ $campo_sssscuenta.show(); };
		
		//mayor_seleccionado 1=Activo	clasifica=1(Activo Circulante)
		//mayor_seleccionado 5=Egresos	clasifica=1(Costo de Ventas)
		//mayor_seleccionado 4=Activo	clasifica=1(Ventas)
		if(parseInt(tipo)==1 ){mayor_seleccionado=1; detalle=1; clasifica=1; };
		if(parseInt(tipo)==2 ){mayor_seleccionado=5; detalle=1; clasifica=1; };
		if(parseInt(tipo)==3 ){mayor_seleccionado=4; detalle=1; clasifica=1; };
		
		$campo_clasif.val(clasifica);
		
		//carga select de cuentas de Mayor
		$select_cta_mayor.children().remove();
		var ctamay_hmtl = '';
		$.each(arrayCtasMayor,function(entryIndex,ctamay){
			if (parseInt(mayor_seleccionado) == parseInt( ctamay['id']) ){
				ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['titulo'] + '</option>';
			}
		});
		$select_cta_mayor.append(ctamay_hmtl);
		
		//click buscar Cuentas Contables
		$boton_busca.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorCuentasContables.json';
			$arreglo = {	'cta_mayor':$select_cta_mayor.val(),
							'detalle':detalle,
							'clasifica':$campo_clasif.val(),
							'cta':$campo_cuenta.val(),
							'scta':$campo_scuenta.val(),
							'sscta':$campo_sscuenta.val(),
							'ssscta':$campo_ssscuenta.val(),
							'sssscta':$campo_sssscuenta.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				var notr=0;
				$.each(entry['CtaContables'],function(entryIndex,cuenta){
					//obtiene numero de trs
					notr = $("tr", $tabla_resultados).size();
					notr++;
					
					trr = '<tr class="tr'+notr+'">';
						trr += '<td width="30">'+cuenta['m']+'</td>';
						trr += '<td width="30">'+cuenta['c']+'</td>';
						trr += '<td width="170">';
							trr += '<input type="hidden" name="id_cta" value="'+cuenta['id']+'" >';
							trr += '<input type="text" name="cta" value="'+cuenta['cuenta']+'" class="borde_oculto" style="width:166px; readOnly="true">';
							trr += '<input type="hidden" name="campo_cta" value="'+cuenta['cta']+'" >';
							trr += '<input type="hidden" name="campo_scta" value="'+cuenta['subcta']+'" >';
							trr += '<input type="hidden" name="campo_sscta" value="'+cuenta['ssubcta']+'" >';
							trr += '<input type="hidden" name="campo_ssscta" value="'+cuenta['sssubcta']+'" >';
							trr += '<input type="hidden" name="campo_ssscta" value="'+cuenta['ssssubcta']+'" >';
						trr += '</td>';
						trr += '<td width="230"><input type="text" name="des" value="'+cuenta['descripcion']+'" class="borde_oculto" style="width:226px; readOnly="true"></td>';
						trr += '<td width="70">'+cuenta['detalle']+'</td>';
						trr += '<td width="50">'+cuenta['nivel_cta']+'</td>';
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
					var id_cta = $(this).find('input[name=id_cta]').val();
					var cta = $(this).find('input[name=campo_cta]').val();
					var scta = $(this).find('input[name=campo_scta]').val();
					var sscta = $(this).find('input[name=campo_sscta]').val();
					var ssscta = $(this).find('input[name=campo_ssscta]').val();
					var sssscta = $(this).find('input[name=campo_ssscta]').val();
					var desc = $(this).find('input[name=des]').val();
					
					if(parseInt(tipo)==1 ){ 
						$('#forma-product-window').find('input[name=id_cta_gasto]').val(id_cta);
						$('#forma-product-window').find('input[name=gas_cuenta]').val(cta);
						$('#forma-product-window').find('input[name=gas_scuenta]').val(scta);
						$('#forma-product-window').find('input[name=gas_sscuenta]').val(sscta);
						$('#forma-product-window').find('input[name=gas_ssscuenta]').val(ssscta);
						$('#forma-product-window').find('input[name=gas_sssscuenta]').val(sssscta);
						$('#forma-product-window').find('input[name=gas_descripcion]').val(desc);
					};
					
					if(parseInt(tipo)==2 ){ 
						$('#forma-product-window').find('input[name=id_cta_costvent]').val(id_cta);
						$('#forma-product-window').find('input[name=costvent_cuenta]').val(cta);
						$('#forma-product-window').find('input[name=costvent_scuenta]').val(scta);
						$('#forma-product-window').find('input[name=costvent_sscuenta]').val(sscta);
						$('#forma-product-window').find('input[name=costvent_ssscuenta]').val(ssscta);
						$('#forma-product-window').find('input[name=costvent_sssscuenta]').val(sssscta);
						$('#forma-product-window').find('input[name=costvent_descripcion]').val(desc);
					};
					
					if(parseInt(tipo)==3 ){ 
						$('#forma-product-window').find('input[name=id_cta_vent]').val(id_cta);
						$('#forma-product-window').find('input[name=vent_cuenta]').val(cta);
						$('#forma-product-window').find('input[name=vent_scuenta]').val(scta);
						$('#forma-product-window').find('input[name=vent_sscuenta]').val(sscta);
						$('#forma-product-window').find('input[name=vent_ssscuenta]').val(ssscta);
						$('#forma-product-window').find('input[name=vent_sssscuenta]').val(sssscta);
						$('#forma-product-window').find('input[name=vent_descripcion]').val(desc);
					};
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscactacontable-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
				});
			});//termina llamada json
		});
		
		$campo_clasif.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_cuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_scuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_sscuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_ssscuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_sssscuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_descripcion.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$boton_cencela.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscactacontable-overlay').fadeOut(remove);
		});
	}//termina buscador de Cuentas Contables

	
	
	//Buscador de producto ingrediente
	$buscador_producto_ingrediente = function(sku_prod, descripcion_prod){
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -180});
		
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
			var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			$.each(data['prodTipos'],function(entryIndex,pt){
				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		
		
		//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
		$campo_sku.val(sku_prod);
		
		//Asignamos la descripcion del producto, si el usuario capturo la descripcion antes de abrir el buscador
		$campo_descripcion.val(descripcion_prod);
		
		$campo_sku.focus();
		
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos_ingredientes.json';
			$arreglo = {
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
						trr += '<td width="120"><span class="sku_prod_buscador">'+producto['sku']+'</span></td>';
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
					$('#forma-product-window').find('input[name=sku_minigrid]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-product-window').find('input[name=descr_prod_minigrid]').val($(this).find('span.titulo_prod_buscador').html());
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
		$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_producto, $buscar_plugin_producto);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_descripcion, $buscar_plugin_producto);
		
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaproducto-overlay').fadeOut(remove);
		});
	}
	
	
	
	//Agrega producto al grid
	$agrega_producto_ingrediente = function(){
			var $select_prodtipo = $('#forma-product-window').find('select[name=select_prodtipo]');
			var $campo_sku_minigrid = $('#forma-product-window').find('input[name=sku_minigrid]');
			var $campo_buscar_prod_minigrid = $('#forma-product-window').find('input[name=descr_prod_minigrid]');
			
			var $grid_productos_componentes = $('#forma-product-window').find('#grid_productos');
			
			var $total_porcentaje = $('#forma-product-window').find('input[name=total_porcentaje]');
			//var $total_tr = $('#forma-product-window').find('input[name=total_tr]');
			
			if($campo_sku_minigrid.val() != null && $campo_sku_minigrid.val() != ''){
				var encontrado=0;
				
				if( parseInt($select_prodtipo.val())==3 ){
					$total_porcentaje.val(0);
				}
				
				if(parseInt($total_porcentaje.val())<1){
					$grid_productos_componentes.find('tbody > tr').each(function (index){
						if(($(this).find('#sku').text()==$campo_sku_minigrid.val().toUpperCase()) && (parseInt($(this).find('#delete').val())!=0)){
							encontrado=1;
						}
					});
					
					if(encontrado==0){
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_busca_sku_prod.json';
						$arreglo = {'sku':$campo_sku_minigrid.val(),
										'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
									}
						
						$.post(input_json,$arreglo,function(prod){
							var res=0;
							if(prod['Sku'][0] != null){
								var trCount = $("tbody > tr", $grid_productos_componentes).size();
								trCount++;
								var valor_componente=0;
								
								if( parseInt($select_prodtipo.val())==3 ){
									valor_componente=1;
								}
								
								var trr = '';
								trr = '<tr>';
									trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70">';
										trr += '<a href=#>Eliminar</a>';
										trr += '<input type="hidden" id="delete" name="eliminar" value="'+prod['Sku'][0]['id']+'">';
										trr += '<input type="hidden" 	name="no_tr" value="'+ trCount +'">';
									trr += '</td>';
									trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="180">';
										trr += '<input type="text" 	value="'+ prod['Sku'][0]['sku'] +'" id="sku" class="borde_oculto" readOnly="true" style="width:176px;">';
									trr += '</td>';
									trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="330">';
										trr += '<input type="text" 		name="nombre" 	value="'+ prod['Sku'][0]['descripcion'] +'" class="borde_oculto" readOnly="true" style="width:326px;">';
									trr += '</td>';
									trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
										trr += '<input type="hidden" id="dec" name="decimales" value="'+ prod['Sku'][0]['decimales']+'">';
										trr += '<input type="text" id="porcentaje" class="porcentaje'+trCount+'" name="porcentaje_grid" value="'+valor_componente+'" style="width:116px;">';
									trr += '</td>';
									
								trr += '</tr>';
								
								var tabla = $grid_productos_componentes.find('tbody');
								tabla.append(trr);
								
								tabla.find('a').bind('click',function(event){
									var total_porcentaje=0;
									if(parseInt($(this).parent().find('#delete').val()) != 0){
										//alert("Alert1: "+total_porcentaje+"  Campo total:"+$total_porcentaje.val());
										total_porcentaje = parseFloat($total_porcentaje.val())-parseFloat(parseFloat($(this).parent().parent().find('#porcentaje').val()).toFixed(4));
										$total_porcentaje.val(total_porcentaje);
										//alert("Alert2: "+total_porcentaje+"  Campo total:"+$total_porcentaje.val());
										$(this).parent().find('#delete').val(0);
										$(this).parent().parent().hide();
									}
								});
								
								/*
								var numero_decimales = $(this).parent().parent().find('#numdec').val();
								var patron = /^-?[0-9]+([,\.][0-9]{0,0})?$/;
								if(parseInt(numero_decimales)==1){
									patron = /^-?[0-9]+([,\.][0-9]{0,1})?$/;
								}
								if(parseInt(numero_decimales)==2){
									patron = /^-?[0-9]+([,\.][0-9]{0,2})?$/;
								}
								if(parseInt(numero_decimales)==3){
									patron = /^-?[0-9]+([,\.][0-9]{0,3})?$/;
								}
								if(parseInt(numero_decimales)==4){
									patron = /^-?[0-9]+([,\.][0-9]{0,4})?$/;
								}
								
								
								if(patron.test($(this).val())){
									alert("Si valido"+$(this).val());
								}else{
									alert("El numero de decimales es incorrecto: "+$(this).val());
									$(this).val('')
								}
								*/
								
								
								if( parseInt($select_prodtipo.val())==1 || parseInt($select_prodtipo.val())==2 || parseInt($select_prodtipo.val())==8){
									var total_inicial=0
									$grid_productos_componentes.find('tbody > tr').each(function (index){
										if(parseInt($(this).find('#delete').val())!=0){
											total_inicial = parseFloat(total_inicial) + parseFloat(parseFloat($(this).find('#porcentaje').val()).toFixed(4));
										}
										$total_porcentaje.val(total_inicial);
									});
									
									
									
									//calcula porcentaje al perder enfoque 
									tabla.find('.porcentaje'+trCount).blur(function(){
										if($(this).val().trim()=='') {
											$(this).val(0);
										}
										$(this).val(parseFloat($(this).val()).toFixed(4));
										
										var total=0
										var patron=/^([0-9]){1,12}[.]?[0-9]*$/
										if(patron.test($(this).val())){
											$grid_productos_componentes.find('tbody > tr').each(function (index){
												if(parseInt($(this).find('#delete').val())!=0){
													total = parseFloat(total) + parseFloat(parseFloat($(this).find('#porcentaje').val()).toFixed(4));
												}
												$total_porcentaje.val(total);
											});
											if(parseFloat(parseFloat($total_porcentaje.val()).toFixed(4))>1){
												jAlert("Has excedido la Unidad del producto",'! Atencion');
											}
										}else{
											jAlert("La cantidad debe tener un 0, ejemplo: 0.5, 0.1, 0.9",'! Atencion');
											$(this).val(0.0)
										}
									});
								}
								
								
								
								if( parseInt($select_prodtipo.val())==3 ){
									var total_inicial=0
									$grid_productos_componentes.find('tbody > tr').each(function (index){
										if(parseInt($(this).find('#delete').val())!=0){
											total_inicial = parseFloat(total_inicial) + parseFloat($(this).find('#porcentaje').val());
										}
										$total_porcentaje.val(total_inicial);
									});
									
									//Calcula porcentaje al perder enfoque 
									tabla.find('.porcentaje'+trCount).blur(function(){
										if(parseFloat($(this).val()) < 1 || $(this).val()=='') {
											$(this).val(1);
										}
										
										var total=0
										$grid_productos_componentes.find('tbody > tr').each(function (index){
											if(parseInt($(this).find('#delete').val())!=0){
												total = parseFloat(parseFloat(total).toFixed(4)) + parseFloat(parseFloat($(this).find('#porcentaje').val()));
											}
											$total_porcentaje.val(total);
										});
									});
								}
								
								
							}else{
								jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
							}
						},"json");
					}else{
						jAlert("El producto ya se encuentra en la lista.",'! Atencion');
					}
					
				}else{
					jAlert("La Cantidad de ingredientes ya acomplet&oacute; la Unidad del producto.",'! Atencion');
				}
			}else{
				jAlert("Ingrese un C&oacute;digo de producto valido", 'Atencion!');
			}
	}
	
	
	
	//habilitar y deshabilitar campos
	$deshabilitar_campos = function(estado,$proveedor,$tiempos_de_entrega,$select_prod_tipo,$select_estatus,$select_seccion,$select_grupo,$select_linea,$select_marca,$select_clase,$select_familia,$select_subfamilia,$select_unidad,$select_clasifstock,$select_iva,$select_ieps,$check_noserie,$check_nom,$check_nolote,$check_pedimento,$check_stock,$check_ventaext,$check_compraext,$select_disponibles,$select_seleccionados,$agregar_pres,$remover_pres,$densidad, $valor_maximo, $valor_minimo, $punto_reorden){
		if(estado == 'desahabilitar'){
			$agregar_pres.hide();
			$remover_pres.hide();
			if(!$select_seccion.is(':disabled')) {
				$proveedor.attr('disabled','-1');
				$tiempos_de_entrega.attr('disabled','-1');
				//$select_prod_tipo.attr('disabled','-1');
				//$select_estatus.attr('disabled','-1');
				$select_seccion.attr('disabled','-1');
				$select_grupo.attr('disabled','-1');
				$select_linea.attr('disabled','-1');
				$select_marca.attr('disabled','-1');
				$select_clase.attr('disabled','-1');
				$select_familia.attr('disabled','-1');
				$select_subfamilia.attr('disabled','-1');
				//$select_unidad.attr('disabled','-1');
				$select_clasifstock.attr('disabled','-1');
				$select_iva.attr('disabled','-1');
				$select_ieps.attr('disabled','-1');
				$check_noserie.attr('disabled','-1');
				$check_nom.attr('disabled','-1');
				$check_nolote.attr('disabled','-1');
				$check_pedimento.attr('disabled','-1');
				$check_stock.attr('disabled','-1');
				$check_ventaext.attr('disabled','-1');
				$check_compraext.attr('disabled','-1');
				$select_disponibles.attr('disabled','-1');
				$select_seleccionados.attr('disabled','-1');
				$densidad.attr('disabled','-1');
				$valor_maximo.attr('disabled','-1');
				$valor_minimo.attr('disabled','-1');
				$punto_reorden.attr('disabled','-1');
                $clave_cfdi_claveprodserv.attr('disabled','-1');
			}
		}
		
		if(estado == 'habilitar'){
			$agregar_pres.show();
			$remover_pres.show();
			if($select_seccion.is(':disabled')) {
				$proveedor.removeAttr('disabled');
				$tiempos_de_entrega.removeAttr('disabled');
				//$select_prod_tipo.removeAttr('disabled');
				//$select_estatus.removeAttr('disabled');
				$select_seccion.removeAttr('disabled');
				$select_grupo.removeAttr('disabled');
				$select_linea.removeAttr('disabled');
				$select_marca.removeAttr('disabled');
				$select_clase.removeAttr('disabled');
				$select_familia.removeAttr('disabled');
				$select_subfamilia.removeAttr('disabled');
				//$select_unidad.removeAttr('disabled');
				$select_clasifstock.removeAttr('disabled');
				$select_iva.removeAttr('disabled');
				$select_ieps.removeAttr('disabled');
				$check_noserie.removeAttr('disabled');
				$check_nom.removeAttr('disabled');
				$check_nolote.removeAttr('disabled');
				$check_pedimento.removeAttr('disabled');
				$check_stock.removeAttr('disabled');
				$check_ventaext.removeAttr('disabled');
				$check_compraext.removeAttr('disabled');
				$select_disponibles.removeAttr('disabled');
				$select_seleccionados.removeAttr('disabled');
				$densidad.removeAttr('disabled');
				$valor_maximo.removeAttr('disabled');
				$valor_minimo.removeAttr('disabled');
				$punto_reorden.removeAttr('disabled');
                $clave_cfdi_claveprodserv.removeAttr('disabled');
			}
		}
	}//termina  habilitar y deshabilitar campos
	
	
	
	
	
	
	//buscador de clientes
	$busca_clientes = function($nocliente, id_user ){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscacliente();
		var $dialogoc =  $('#forma-buscacliente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
		$('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -190});
		
		var $tabla_resultados = $('#forma-buscacliente-window').find('#tabla_resultado');
		var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('#busca_cliente_modalbox');
		var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscacliente-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscacliente-window').find('select[name=filtropor]');
		
		//Funcionalidad botones
		$busca_cliente_modalbox.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		
		$busca_cliente_modalbox.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_plugin_busca_cliente.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_cliente.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		var html = '';
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		
		if($nocliente.val().trim() != ''){
			//asignamos el numero de control al campo de busqueda
			$cadena_buscar.val($nocliente.val());
			html+='<option value="1" selected="yes">No. de control</option>';
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		html+='<option value="3">Razon social</option>';
		//html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		
		$cadena_buscar.focus();
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorClientes.json';
			$arreglo = {	'cadena':$cadena_buscar.val(),
							'filtro':$select_filtro_por.val(),
							'iu':id_user 
                        }
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Clientes'],function(entryIndex,cliente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
							trr += '<span class="no_control">'+cliente['numero_control']+'</span>';
						trr += '</td>';
						trr += '<td width="145"><span class="rfc">'+cliente['rfc']+'</span></td>';
						trr += '<td width="375"><span class="razon">'+cliente['razon_social']+'</span></td>';
					trr += '</tr>';
					
					$tabla_resultados.append(trr);
				});
				
				//$tabla_resultados.find('tr').focus();
				
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
					
					$nocliente.val($(this).find('span.no_control').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					
					//Asignar el enfoque al campo Numero de Control
					$nocliente.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$busca_cliente_modalbox.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $busca_cliente_modalbox);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $busca_cliente_modalbox);
		
		$cancelar_plugin_busca_cliente.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
			$nocliente.focus();
		});
	}//termina buscador de clientes

	
	
	
	
	
	$new_producto.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_Products();
		
		var form_to_show = 'formaProducto00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-product-window').css({"margin-left": -260, 	"margin-top": -220});
		
		$forma_selected.prependTo('#forma-product-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProducto.json';
		$arreglo = {'id_producto':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		//variables del catalogo
		var $incluye_produccion = $('#forma-product-window').find('input[name=incluye_pro]');
		var $campo_id_producto = $('#forma-product-window').find('input[name=id_producto]');
		var $codigo = $('#forma-product-window').find('input[name=codigo]');
		var $descripcion = $('#forma-product-window').find('input[name=descripcion]');
		var $codigo_barras = $('#forma-product-window').find('input[name=codigo_barras]');
		var $tiempos_de_entrega = $('#forma-product-window').find('input[name=tentrega]');
		var $densidad = $('#forma-product-window').find('input[name=densidad]');
		var $valor_maximo = $('#forma-product-window').find('input[name=valor_maximo]');
		var $valor_minimo = $('#forma-product-window').find('input[name=valor_minimo]');
		var $punto_reorden = $('#forma-product-window').find('input[name=punto_reorden]');
                
		//var $busca_cfdi_claveprodserv = $('#forma-product-window').find('a[href=busca_cfdi_claveprodserv]');
		var $clave_cfdi_claveprodserv = $('#forma-product-window').find('input[name=clave_cfdi_claveprodserv]');
		
		var $td_etiqueta_prov_clie = $('#forma-product-window').find('#td_etiqueta_prov_clie');
		var $busca_clie_prov = $('#forma-product-window').find('#busca_clie_prov');
		var $proveedor = $('#forma-product-window').find('input[name=proveedor]');
		var $id_proveedor = $('#forma-product-window').find('input[name=id_proveedor]');
		var $no_clie = $('#forma-product-window').find('input[name=no_clie]');
		
		
		var $select_estatus = $('#forma-product-window').find('select[name=select_estatus]');
		var $select_seccion = $('#forma-product-window').find('select[name=select_seccion]');
		var $select_grupo = $('#forma-product-window').find('select[name=select_grupo]');
		var $select_linea = $('#forma-product-window').find('select[name=select_linea]');
		var $select_marca = $('#forma-product-window').find('select[name=select_marca]');
		var $select_clase = $('#forma-product-window').find('select[name=select_clase]');
		var $select_familia = $('#forma-product-window').find('select[name=select_familia]');
		var $select_subfamilia = $('#forma-product-window').find('select[name=select_subfamilia]');
		var $select_prod_tipo = $('#forma-product-window').find('select[name=select_prodtipo]');
		var $tipo_producto_anterior = $('#forma-product-window').find('input[name=tipo_producto_anterior]');
		
		var $select_unidad = $('#forma-product-window').find('select[name=select_unidad]');
		var $select_clasifstock = $('#forma-product-window').find('select[name=select_clasifstock]');
		var $select_iva = $('#forma-product-window').find('select[name=select_iva]');
		var $select_ieps = $('#forma-product-window').find('select[name=select_ieps]');
		var $select_moneda = $('#forma-product-window').find('select[name=select_moneda]');
		var $select_retencion = $('#forma-product-window').find('select[name=select_retencion]');
		
		var $check_noserie = $('#forma-product-window').find('input[name=check_noserie]');
		var $check_nom = $('#forma-product-window').find('input[name=check_nom]');
		var $check_nolote = $('#forma-product-window').find('input[name=check_nolote]');
		var $check_pedimento = $('#forma-product-window').find('input[name=check_pedimento]');
		var $check_stock = $('#forma-product-window').find('input[name=check_stock]');
		var $check_ventaext = $('#forma-product-window').find('input[name=check_ventaext]');
		var $check_compraext = $('#forma-product-window').find('input[name=check_compraext]');
		
		//presentaciones seleccionados y disponibles
		var $select_disponibles= $('#forma-product-window').find('select[name=disponibles]');
		var $select_seleccionados = $('#forma-product-window').find('select[name=seleccionados]');
		var $campo_pres_on = $('#forma-product-window').find('input[name=pres_on]');
		var $select_pres_default = $('#forma-product-window').find('select[name=select_pres_default]');
		
		//agregar y remover presentaciones
		var $agregar_pres = $('#forma-product-window').find('a[href*=agregar_pres]');
		var $remover_pres = $('#forma-product-window').find('a[href*=remover_pres]');
		
		var $grid_productos_componentes = $('#forma-product-window').find('#grid_productos');
		var $total_porcentaje = $('#forma-product-window').find('input[name=total_porcentaje]');
		var $total_tr = $('#forma-product-window').find('input[name=total_tr]');
		
		var $sku_minigrid = $('#forma-product-window').find('input[name=sku_minigrid]');
		var $descr_prod_minigrid = $('#forma-product-window').find('input[name=descr_prod_minigrid]');
			
		//variables de los href
		var $agregar_prod = $('#forma-product-window').find('a[href*=agregar_produ_minigrid]');
		var $buscar_prod_ingrediente = $('#forma-product-window').find('a[href*=busca_producto_ingrediente]');
		var $busca_proveedor = $('#forma-product-window').find('a[href*=busca_proveedor]');
		
		var $pestana_contabilidad = $('#forma-product-window').find('ul.pestanas').find('a[href*=#tabx-3]');
		
		var $id_cta_gas = $('#forma-product-window').find('input[name=id_cta_gas]');
		var $gas_cuenta = $('#forma-product-window').find('input[name=gas_cuenta]');
		var $gas_scuenta = $('#forma-product-window').find('input[name=gas_scuenta]');
		var $gas_sscuenta = $('#forma-product-window').find('input[name=gas_sscuenta]');
		var $gas_ssscuenta = $('#forma-product-window').find('input[name=gas_ssscuenta]');
		var $gas_sssscuenta = $('#forma-product-window').find('input[name=gas_sssscuenta]');
		var $gas_descripcion = $('#forma-product-window').find('input[name=gas_descripcion]');
		
		var $id_cta_costvent = $('#forma-product-window').find('input[name=id_cta_costvent]');
		var $costvent_cuenta = $('#forma-product-window').find('input[name=costvent_cuenta]');
		var $costvent_scuenta = $('#forma-product-window').find('input[name=costvent_scuenta]');
		var $costvent_sscuenta = $('#forma-product-window').find('input[name=costvent_sscuenta]');
		var $costvent_ssscuenta = $('#forma-product-window').find('input[name=costvent_ssscuenta]');
		var $costvent_sssscuenta = $('#forma-product-window').find('input[name=costvent_sssscuenta]');
		var $costvent_descripcion = $('#forma-product-window').find('input[name=costvent_descripcion]');
		
		var $id_cta_vent = $('#forma-product-window').find('input[name=id_cta_vent]');
		var $vent_cuenta = $('#forma-product-window').find('input[name=vent_cuenta]');
		var $vent_scuenta = $('#forma-product-window').find('input[name=vent_scuenta]');
		var $vent_sscuenta = $('#forma-product-window').find('input[name=vent_sscuenta]');
		var $vent_ssscuenta = $('#forma-product-window').find('input[name=vent_ssscuenta]');
		var $vent_sssscuenta = $('#forma-product-window').find('input[name=vent_sssscuenta]');
		var $vent_descripcion = $('#forma-product-window').find('input[name=vent_descripcion]');
		
		var $busca_gasto = $('#forma-product-window').find('a[href=busca_gasto]');
		var $busca_costvent = $('#forma-product-window').find('a[href=busca_costvent]');
		var $busca_vent = $('#forma-product-window').find('a[href=busca_vent]');
		
		var $limpiar_gasto = $('#forma-product-window').find('a[href=limpiar_gasto]');
		var $limpiar_costvent = $('#forma-product-window').find('a[href=limpiar_costvent]');
		var $limpiar_vent = $('#forma-product-window').find('a[href=limpiar_vent]');
                
		var $nameimg = $('#forma-product-window').find('input[name=nameimg]');
		var $namepdf = $('#forma-product-window').find('input[name=namepdf]');
		var $descripcion_corta = $('#forma-product-window').find('textarea[name=descripcion_corta]');
		var $descripcion_larga = $('#forma-product-window').find('textarea[name=descripcion_larga]');
		var $edito_pdf = $('#forma-product-window').find('input[name=edito_pdf]');
		var $edito_imagen = $('#forma-product-window').find('input[name=edito_imagen]');
                
		var $cerrar_plugin = $('#forma-product-window').find('#close');
		var $cancelar_plugin = $('#forma-product-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-product-window').find('#submit');
		
		//var $cancel_button = $('#forma-product-window').find('input[value$=Cancelar]');
		$campo_id_producto.val(0);
		$total_porcentaje.val(0);
		
		//$codigo.attr({ 'readOnly':true });
		//$codigo.css({'background' : '#DDDDDD'});
		$proveedor.attr({'readOnly':true});
		$busca_proveedor.hide();
		
		if(parseInt(rolVendedor)>=1){
			//Ocultar boton Actualizar
			$submit_actualizar.hide();
			
			//Quitar enter a todos los campos input
			$('#forma-product-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
		}
		
		//Quitar enter al input sku_minigrid
		$sku_minigrid.keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		//Quitar enter al input descr_prod_minigrid
		$descr_prod_minigrid.keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$gas_cuenta.hide();
		$gas_scuenta.hide();
		$gas_sscuenta.hide();
		$gas_ssscuenta.hide();
		$gas_sssscuenta.hide();
		
		$costvent_cuenta.hide();
		$costvent_scuenta.hide();
		$costvent_sscuenta.hide();
		$costvent_ssscuenta.hide();
		$costvent_sssscuenta.hide();
		
		$vent_cuenta.hide();
		$vent_scuenta.hide();
		$vent_sscuenta.hide();
		$vent_ssscuenta.hide();
		$vent_sssscuenta.hide();
		
		$busca_clie_prov.hide();
		$proveedor.hide();
		$no_clie.hide();
		
		/*Codigo para subir la imagen*/
		var input_json_upload = document.location.protocol + '//' + document.location.host + '/'+controller+'/fileUpload.json';
		var button_img = $('#forma-product-window').find('#upload_button_img'), interval;
		new AjaxUpload(button_img,{
			action: input_json_upload, 
			name: 'file',
			onSubmit : function(file , ext){
				if (! (ext && /^(png*|jpg*)$/.test(ext))){
					jAlert("El formato de la imagen debe de ser .png", 'Atencion!');
					return false;
				} else {
					button_img.text('Cargando..');
					this.disable();
				}
			},
			onComplete: function(file, response){
				button_img.text('Cambiar Imagen');
				window.clearInterval(interval);
				this.enable();
				$nameimg.val(file);
				$edito_imagen.val(1);
				var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
				var input_json_img = document.location.protocol + '//' + document.location.host + '/' + controller + '/imgDownloadImg/'+file+'/0/'+iu+'/out.json';
				$('#forma-product-window').find('#contenidofile_img').removeAttr("src").attr("src",input_json_img);
			}
		});
		
		/*Codigo para subir la pdf*/
		var input_json_upload = document.location.protocol + '//' + document.location.host + '/'+controller+'/fileUpload.json';
		var button_pdf = $('#forma-product-window').find('#upload_button_pdf'), interval;
		new AjaxUpload(button_pdf,{
			action: input_json_upload, 
			name: 'file',
			onSubmit : function(file , ext){
				if (! (ext && /^(pdf)$/.test(ext))){
					jAlert("El formato del archivo, puede ser .pdf", 'Atencion!');
					return false;
				} else {
					button_pdf.text('Cargando..');
					this.disable();
				}
			},
			onComplete: function(file, response){
				button_pdf.text('Cambiar PDF');
				window.clearInterval(interval);
				this.enable();
				$namepdf.val(file);
				$edito_pdf.val(1);
				$('#forma-product-window').find('#contenidofile_pdf').text(file);
			}
		});
                
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Producto dado de alta", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-product-overlay').fadeOut(remove);
				$get_datos_grid();
			}
			else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-product-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-product-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			};
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			$incluye_produccion.val(entry['Extras'][0]['mod_produccion']);
			
			if(entry['Extras'][0]['ilog']=='true'){
				$td_etiqueta_prov_clie.html('No. Cliente');
				$no_clie.show();
				$busca_clie_prov.show();
			}else{
				$td_etiqueta_prov_clie.html('Proveedor');
				$proveedor.show();
			}
				
			
			if( entry['Extras'][0]['incluye_contab']=='false' ){
				$pestana_contabilidad.parent().hide();
			}else{
				//visualizar subcuentas de acuerdo al nivel definido para la empresa
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $gas_cuenta.show(); $costvent_cuenta.show(); $vent_cuenta.show();  };
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $gas_scuenta.show(); $costvent_scuenta.show(); $vent_scuenta.show(); };
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $gas_sscuenta.show(); $costvent_sscuenta.show(); $vent_sscuenta.show(); };
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $gas_ssscuenta.show(); $costvent_ssscuenta.show(); $vent_ssscuenta.show(); };
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $gas_sssscuenta.show(); $costvent_sssscuenta.show(); $vent_sssscuenta.show(); };
				
				//busca Cuenta Gastos
				$busca_gasto.click(function(event){
					event.preventDefault();
					$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
				});
				
				//busca Cuenta Costo de Venta
				$busca_costvent.click(function(event){
					event.preventDefault();
					$busca_cuentas_contables(2, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
				});
				
				//busca Cuenta Venta
				$busca_vent.click(function(event){
					event.preventDefault();
					$busca_cuentas_contables(3, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
				});
				
				
				//limpiar campos Cuenta Gastos
				$limpiar_gasto.click(function(event){
					event.preventDefault();
					$id_cta_gas.val(0);
					$gas_cuenta.val('');
					$gas_scuenta.val('');
					$gas_sscuenta.val('');
					$gas_ssscuenta.val('');
					$gas_sssscuenta.val('');
					$gas_descripcion.val('');
				});
				
				//limpiar campos Cuenta Costo de Venta
				$limpiar_costvent.click(function(event){
					event.preventDefault();
					$id_cta_costvent.val(0);
					$costvent_cuenta.val('');
					$costvent_scuenta.val('');
					$costvent_sscuenta.val('');
					$costvent_ssscuenta.val('');
					$costvent_sssscuenta.val('');
					$costvent_descripcion.val('');
				});
				
				//limpiar campos Cuenta Venta
				$limpiar_vent.click(function(event){
					event.preventDefault();
					$id_cta_vent.val(0);
					$vent_cuenta.val('');
					$vent_scuenta.val('');
					$vent_sscuenta.val('');
					$vent_ssscuenta.val('');
					$vent_sssscuenta.val('');
					$vent_descripcion.val('');
				});
			}
			
			//estatus
			$select_estatus.children().remove();
			var status_html = '<option value="true" selected="yes">Activo</option>';
			status_html += '<option value="false">Inactivo</option>';
			$select_estatus.append(status_html);
			
			
			//Alimentando select de secciones
			$select_seccion.children().remove();
			var secciones_hmtl = '<option value="0">[--Seleccionar Seccion--]</option>';
			$.each(entry['Secciones'],function(entryIndex,lin){
				secciones_hmtl += '<option value="' + lin['id'] + '"  >' + lin['titulo'] + '</option>';
			});
			$select_seccion.append(secciones_hmtl);
			
			//Alimentando select de lineas
			$select_linea.children().remove();
			var lineas_hmtl = '<option value="0">[--Seleccionar Linea--]</option>';
			$.each(arrayLineas,function(entryIndex,lin){
				lineas_hmtl += '<option value="' + lin['id'] + '"  >' + lin['titulo'] + '</option>';
			});
			$select_linea.append(lineas_hmtl);
			
			//Alimentando select de marcas
			$select_marca.children().remove();
			var marcas_hmtl = '<option value="0">[--Seleccionar Marca--]</option>';
			$.each(arrayMarcas,function(entryIndex,mar){
				marcas_hmtl += '<option value="' + mar['id'] + '"  >' + mar['titulo'] + '</option>';
			});
			$select_marca.append(marcas_hmtl);
			
			
			//Alimentando select de clases
			$select_clase.children().remove();
			var clase_hmtl = '<option value="0">[--Seleccionar Clase--]</option>';
			$.each(entry['Clases'],function(entryIndex,clase){
				clase_hmtl += '<option value="' + clase['id'] + '"  >' + clase['titulo'] + '</option>';
			});
			$select_clase.append(clase_hmtl);
			
			//Alimentando select de grupos
			$select_grupo.children().remove();
			var grupo_hmtl = '<option value="0">[--Seleccionar Grupo--]</option>';
			$.each(entry['Grupos'],function(entryIndex,lin){
				grupo_hmtl += '<option value="' + lin['id'] + '"  >' + lin['titulo'] + '</option>';
			});
			$select_grupo.append(grupo_hmtl);
			
			//ProdTipos
			//carga select de tipos de producto
			$select_prod_tipo.children().remove();
			var prodtipos_hmtl = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			$.each(arrayTProd,function(entryIndex,lin){
				prodtipos_hmtl += '<option value="' + lin['id'] + '"  >' + lin['titulo'] + '</option>';
			});
			$select_prod_tipo.append(prodtipos_hmtl);
                        
			//Alimentando select de familias
			$select_familia.children().remove();
			var familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
			$select_familia.append(familia_hmtl);
			
                        
			$select_familia.change(function(){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSubFamiliasByFamProd.json';
				$arreglo = {'fam':$select_familia.val(),
								'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							}
				$.post(input_json,$arreglo,function(data){
					//Alimentando select de Subfamilias
					$select_subfamilia.children().remove();
					var subfamilia_hmtl = '<option value="0">[--Seleccionar Subfmilia--]</option>';
					$.each(data['SubFamilias'],function(dataIndex,subfam){
						subfamilia_hmtl += '<option value="' + subfam['id'] + '"  >' + subfam['titulo'] + '</option>';
					});
					$select_subfamilia.append(subfamilia_hmtl);
				});
			});

			//Alimentando select de unidades
			$select_unidad.children().remove();
			var unidads_hmtl = '<option value="0">[--Seleccionar Unidad--]</option>';
			$.each(entry['Unidades'],function(entryIndex,uni){
				unidads_hmtl += '<option value="' + uni['id'] + '"  >' + uni['titulo'] + '</option>';
			});
			$select_unidad.append(unidads_hmtl);
			
			
			//Alimentando select de clasificacion Stock
			$select_clasifstock.children().remove();
			var stock_hmtl = '<option value="0">[--Seleccionar Clasificacion--]</option>';
			$.each(entry['ClasifStock'],function(entryIndex,stock){
				stock_hmtl += '<option value="' + stock['id'] + '"  >' + stock['titulo'] + '</option>';
			});
			$select_clasifstock.append(stock_hmtl);
			
			
			//Alimentando select de ivas
			$select_iva.children().remove();
			var iva_hmtl = '';
			$.each(entry['Impuestos'],function(entryIndex,iva){
				if(parseInt(iva['id'])==1){
					iva_hmtl += '<option value="' + iva['id'] + '" selected="yes">' + iva['descripcion'] + '</option>';
				}else{
					iva_hmtl += '<option value="' + iva['id'] + '"  >' + iva['descripcion'] + '</option>';
				}
			});
			$select_iva.append(iva_hmtl);
			
			
			//Alimentando select de ieps
			$select_ieps.children().remove();
			var ieps_hmtl = '<option value="0">[--IEPS--]</option>';
			$.each(entry['Ieps'],function(entryIndex,ieps){
				ieps_hmtl += '<option value="' + ieps['id'] + '"  >' + ieps['titulo'] + '</option>';
			});
			$select_ieps.append(ieps_hmtl);
			
			
			//Alimentando select de retencion de iva
			$select_retencion.children().remove();
			var ret_iva_hmtl = '<option value="0">[------]</option>';
			var primer_selec=0;
			$.each(entry['ImptosRet'],function(entryIndex,ieps){
				ret_iva_hmtl += '<option value="' + ieps['id'] + '"  >' + ieps['titulo'] + '</option>';
			});
			$select_retencion.append(ret_iva_hmtl);
			
			
			//Alimentando select de monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(arrayMonedas,function(entryIndex,mon){
				moneda_hmtl += '<option value="' + mon['id'] + '"  >' + mon['descripcion_abr'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			
			
			//carga select de presentaciones disponibles
			$select_disponibles.children().remove();
			var presentaciones_hmtl = '';
			$.each(entry['Presentaciones'],function(entryIndex,pres){
				presentaciones_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
			});
			$select_disponibles.append(presentaciones_hmtl);
			
			$select_pres_default.children().remove();
			presentaciones_hmtl = '<option value="0" selected="yes">[--Presentaci&oacute;n--]</option>';
			$.each(entry['Presentaciones'],function(entryIndex,pres){
				presentaciones_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
			});
			$select_pres_default.append(presentaciones_hmtl);
			
			
			
			$('.contenedor_grid_prod').css({'display':'none'});
			//$('#forma-product-window').find('.product_div_one').css({'height':'250px'});
			$select_prod_tipo.change(function(){
				var valor_tipo = $(this).val();
				
				if(parseInt($("tbody > tr", $grid_productos_componentes).size()) > 0){
					jAlert("Actualmente hay productos en el  listado de Materias primas, no puedes cambiar de tipo de producto",'! Atencion');
					var tipo_anterior=$tipo_producto_anterior.val();
					
					if($incluye_produccion.val()=='false'){
						//aqui solo debe entrar cuando la empresa no incluya modulo de produccion
						//tipo 1=Terminado, 2=Intermedio, 8=Desarrollo, 3=Kit
						if( parseInt(tipo_anterior)!=1 || parseInt(tipo_anterior)!=2 || parseInt(tipo_anterior)!=8 || parseInt(tipo_anterior)!=3){
							tipo_anterior=0;
						}
					}else{
						//tipo=3 es Kit
						if( parseInt(tipo_anterior)!=3 ){
							tipo_anterior=0;
						}
					}
					
					//Recargar select tipo de producto
					$select_prod_tipo.children().remove();
					var prodtipo_hmtl = '<option value="0" >[--Seleccionar Tipo--]</option>';
					$.each(arrayTProd,function(entryIndex,tip){
						if(parseInt(tip['id'])==parseInt(tipo_anterior)){
							prodtipo_hmtl += '<option value="' + tip['id'] + '" selected="yes">' + tip['titulo'] + '</option>';
						}else{
							prodtipo_hmtl += '<option value="' + tip['id'] + '"  >' + tip['titulo'] + '</option>';
						}
					});
					$select_prod_tipo.append(prodtipo_hmtl);
					$tipo_producto_anterior.val(tipo_anterior);
				}else{
					$('div.contenedor_grid_prod').css({'display':'none'});
					$('#forma-product-window').find('.product_div_one').css({'height':'350px'});
					
					//Recargar select de unidades al cambiar de tipo de producto que no es subensamble
					$select_unidad.children().remove();
					var unidads_hmtl = '<option value="0">[--Seleccionar Unidad--]</option>';
					$.each(entry['Unidades'],function(entryIndex,uni){
						unidads_hmtl += '<option value="' + uni['id'] + '"  >' + uni['titulo'] + '</option>';
					});
					$select_unidad.append(unidads_hmtl);
					
					
					//Habilitar campos cuando sea diferente de 3=KIT, 4=SERVICIOS
					if( parseInt(valor_tipo)!=3 || parseInt(valor_tipo)!=4){
						$deshabilitar_campos("habilitar",$proveedor,$tiempos_de_entrega,$select_prod_tipo,$select_estatus,$select_seccion,$select_grupo,$select_linea,$select_marca,$select_clase,$select_familia,$select_subfamilia,$select_unidad,$select_clasifstock,$select_iva,$select_ieps,$check_noserie,$check_nom,$check_nolote,$check_pedimento,$check_stock,$check_ventaext,$check_compraext,$select_disponibles,$select_seleccionados,$agregar_pres,$remover_pres,$densidad, $valor_maximo, $valor_minimo, $punto_reorden);
						$tipo_producto_anterior.val(valor_tipo);
					}
					
					if($incluye_produccion.val()=='false'){
						//Aqui solo debe entrar cuando la empresa no incluya modulo de produccion
						//tipo=1  PROD TERMINADO, 2=INTERMEDIO, DESARROLLO
						if( parseInt(valor_tipo)==1 || parseInt(valor_tipo)==2 || parseInt(valor_tipo)==8){
							$deshabilitar_campos("habilitar",$proveedor,$tiempos_de_entrega,$select_prod_tipo,$select_estatus,$select_seccion,$select_grupo,$select_linea,$select_marca,$select_clase,$select_familia,$select_subfamilia,$select_unidad,$select_clasifstock,$select_iva,$select_ieps,$check_noserie,$check_nom,$check_nolote,$check_pedimento,$check_stock,$check_ventaext,$check_compraext,$select_disponibles,$select_seleccionados,$agregar_pres,$remover_pres,$densidad, $valor_maximo, $valor_minimo, $punto_reorden);
							$tipo_producto_anterior.val(valor_tipo);
							//visualizar grid para agregar productos componentes
							
							$('div.contenedor_grid_prod').css({'display':'block'});
							$('#forma-product-window').find('.product_div_one').css({'height':'505px'});
						}
					}
					
					//tipo=3 es KIT
					if( parseInt(valor_tipo)==3 ){
						$deshabilitar_campos("desahabilitar",$proveedor,$tiempos_de_entrega,$select_prod_tipo,$select_estatus,$select_seccion,$select_grupo,$select_linea,$select_marca,$select_clase,$select_familia,$select_subfamilia,$select_unidad,$select_clasifstock,$select_iva,$select_ieps,$check_noserie,$check_nom,$check_nolote,$check_pedimento,$check_stock,$check_ventaext,$check_compraext,$select_disponibles,$select_seleccionados,$agregar_pres,$remover_pres,$densidad, $valor_maximo, $valor_minimo, $punto_reorden);
						$tipo_producto_anterior.val(valor_tipo);
						
						//visualizar grid para agregar productos componentes
						$('div.contenedor_grid_prod').css({'display':'block'});
						$('#forma-product-window').find('.product_div_one').css({'height':'505px'});
					}
					
					//tipo=4 es SERVICIOS
					if( parseInt(valor_tipo)==4 ){
						$deshabilitar_campos("desahabilitar",$proveedor,$tiempos_de_entrega,$select_prod_tipo,$select_estatus,$select_seccion,$select_grupo,$select_linea,$select_marca,$select_clase,$select_familia,$select_subfamilia,$select_unidad,$select_clasifstock,$select_iva,$select_ieps,$check_noserie,$check_nom,$check_nolote,$check_pedimento,$check_stock,$check_ventaext,$check_compraext,$select_disponibles,$select_seleccionados,$agregar_pres,$remover_pres,$densidad, $valor_maximo, $valor_minimo, $punto_reorden);
						$tipo_producto_anterior.val(valor_tipo);
						
						//Recargar select de unidades. Carga solo la unidad SERVICIO
						$select_unidad.children().remove();
						var unidads_hmtl = '<option value="0">[--Seleccionar Unidad--]</option>';
						$.each(entry['Unidades'],function(entryIndex,uni){
							if(/^SERVICIO*|SERVICIOS$/.test(uni['titulo'].toUpperCase())){
								unidads_hmtl += '<option value="' + uni['id'] + '"  >' + uni['titulo'] + '</option>';
							}
						});
						$select_unidad.append(unidads_hmtl);
						
						//Recargar presentaciones default. Carga solo la Presentacion SERVICIO
						$select_pres_default.children().remove();
						presentaciones_hmtl = '<option value="0" selected="yes">[--Presentaci&oacute;n--]</option>';
						$.each(entry['Presentaciones'],function(entryIndex,pres){
							if(/^SERVICIO*|SERVICIOS$/.test(pres['titulo'].toUpperCase())){
								presentaciones_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
							}
						});
						$select_pres_default.append(presentaciones_hmtl);
					}else{
						//recarga select de unidades para permitir seleccionar una nueva unidad
						$select_unidad.children().remove();
						var unidads_hmtl = '<option value="0">[--Seleccionar Unidad--]</option>';
						$.each(entry['Unidades'],function(entryIndex,uni){
							unidads_hmtl += '<option value="' + uni['id'] + '"  >' + uni['titulo'] + '</option>';
						});
						$select_unidad.append(unidads_hmtl);
						
						//Recarga select de presentacion default
						$select_pres_default.children().remove();
						presentaciones_hmtl = '<option value="0" selected="yes">[--Presentaci&oacute;n--]</option>';
						$.each(entry['Presentaciones'],function(entryIndex,pres){
							presentaciones_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
						});
						$select_pres_default.append(presentaciones_hmtl);
					}
				}//termina id que valida existencia de productos componentes en el grid
				
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFamiliasByTipoProd.json';
				$arreglo = {	'tipo_prod':$select_prod_tipo.val(),
								'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							};
	
				$.post(input_json,$arreglo,function(data){
					$select_familia.children().remove();
					familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
					$.each(data['Familias'],function(entryIndex,fam){
						familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
					});
					$select_familia.append(familia_hmtl);
				});
			});
			
			
			$select_unidad.change(function(){
				var id_unidad = $(this).val();
				if(parseInt($select_prod_tipo.val())==1 || parseInt($select_prod_tipo.val())==2 || parseInt($select_prod_tipo.val())==8){
					//si el tipo de producto es TERMINADO entra aqui
					if(parseInt(id_unidad)==1 || parseInt(id_unidad)==2 ){
						//si la unidad es Kilogramo y litro dejar seleccionado solo uno
						$select_unidad.children().remove();
						var unidads_hmtl = '';
						$.each(entry['Unidades'],function(entryIndex,uni){
							if(parseInt(id_unidad)==parseInt(uni['id'])){
								unidads_hmtl += '<option value="' + uni['id'] + '"  >' + uni['titulo'] + '</option>';
							}
						});
						$select_unidad.append(unidads_hmtl);
					}else{
						jAlert("La unidad "+$select_unidad.find('option:selected').html()+" no es para para productos formulados, seleccione otra diferente",'! Atencion');
						
						//recarga select de unidades para permitir seleccionar una nueva unidad
						$select_unidad.children().remove();
						var unidads_hmtl = '<option value="0">[--Seleccionar Unidad--]</option>';
						$.each(entry['Unidades'],function(entryIndex,uni){
							unidads_hmtl += '<option value="' + uni['id'] + '"  >' + uni['titulo'] + '</option>';
						});
						$select_unidad.append(unidads_hmtl);
					}
				}
			});
			
		});//termina llamada json
		
		
		
		//Buscador de clientes
		$busca_clie_prov.click(function(event){
			event.preventDefault();
			$busca_clientes($no_clie, $('#lienzo_recalculable').find('input[name=iu]').val() );
		});
		
		/*.click(function(event){
			event.preventDefault();
			//$busca_clientes($no_clie, $('#lienzo_recalculable').find('input[name=iu]').val() );
                    alert("Ok");
		});*/
                
		//agregar presentacion
		$agregar_pres.click(function(event){
			event.preventDefault();
			var logica = false;
			var primero=0;
			logica = !$select_disponibles.find('option:selected').remove().appendTo( $select_seleccionados);
			var valor_campo = "";
			var ahora_seleccionados = $select_seleccionados.find('option').get();
			$.each( ahora_seleccionados , function(indice , seleccionado){
				if(primero==0){
					valor_campo += seleccionado.value;
					primero=1;
				}else{
					valor_campo += "," + seleccionado.value;
				}
			});
			//alert(valor_campo);
			$campo_pres_on.attr({'value' : valor_campo});
			return logica; 
		});
		
		
		//remover presentacion
		$remover_pres.click(function(event){
			event.preventDefault();
			var logica = false;
			var primero=0;
			logica = !$select_seleccionados.find('option:selected').remove().appendTo($select_disponibles);
			var valor_campo = "";
			var ahora_seleccionados = $select_seleccionados.find('option').get();
			$.each( ahora_seleccionados , function(indice , seleccionado){
				if(primero==0){
					valor_campo += seleccionado.value;
					primero=1;
				}else{
					valor_campo += "," + seleccionado.value;
				}
			});
			//alert(valor_campo);
			$campo_pres_on.attr({'value' : valor_campo}); 
			
			
			$select_disponibles.find('option').attr("selected",false);
			return logica;
		});
		
		
		
		
		$select_pres_default.change(function(){
			var idPresDef = $(this).val();
			
			//seleccionar la presentación que sea igual a la seleccionada en el campo Default
			$select_disponibles.find('option[value='+ idPresDef +']').attr("selected",true);
			
			//ejecutar la Click del href Agregar Presentacion
			$agregar_pres.trigger('click');
		});
		
		
		
		$agregar_prod.click(function(event){
			event.preventDefault();
			if( parseInt($select_prod_tipo.val())!=3 ){
				if(parseInt($select_unidad.val())==0){
					jAlert("Es necesario seleccionar la Unidad de Medida.",'! Atencion');
				}else{
					$agrega_producto_ingrediente();
				}
			}else{
				//cuando el producto es 3=kit, no se necesita validar unaidad
				$agrega_producto_ingrediente();
			}
		});
		
		//Ejecutar click del href Agregar al pulsar Enter sobre el campo $sku_minigrid
		$(this).aplicarEventoKeypressEjecutaTrigger($sku_minigrid, $agregar_prod);
		
		
		$buscar_prod_ingrediente.click(function(event){
			event.preventDefault();
			if( parseInt($select_prod_tipo.val())!=3 ){
				if(parseInt($select_unidad.val())==0){
					jAlert("Es necesario seleccionar la Unidad de Medida.",'! Atencion');
				}else{
					$buscador_producto_ingrediente($sku_minigrid.val(), $descr_prod_minigrid.val());
				}
			}else{
				//cuando el producto es 3=kit, no se necesita validar unaidad
				$buscador_producto_ingrediente($sku_minigrid.val(), $descr_prod_minigrid.val());
			}
		});
		
		
		
		//Validar campo tiempos de entrega, solo acepte numeros y punto
		$tiempos_de_entrega.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		//Quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$tiempos_de_entrega.focus(function(e){
			if(parseFloat($tiempos_de_entrega.val())<1){
				$tiempos_de_entrega.val('');
			}
		});
		
		//Pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$tiempos_de_entrega.blur(function(e){
			if(parseFloat($tiempos_de_entrega.val())==0||$tiempos_de_entrega.val()==""){
				$tiempos_de_entrega.val(0.0);
			}
		});	
		
		//Validar campo tiempo de entrega, solo acepte numeros y punto
		$densidad.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		//Quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$densidad.focus(function(e){
			if(parseFloat($densidad.val())<0.00001){
				$densidad.val('');
			}
		});
		
		//Pone Uno al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$densidad.blur(function(e){
			if($densidad.val().trim()==""){
				$densidad.val(1);
			}else{
				if(parseFloat($densidad.val())<=0){
					$densidad.val(1);
				}
			}
			$densidad.val(parseFloat($densidad.val()).toFixed(4));
		});


		// clave producto servicio (request al catalogo de sat)
		let prodserv_datalist = document.getElementById('prodserv_datalist');
		let timeoutId = 0;
		let route = document.location.protocol + '//' + document.location.host + controller + '/prodserv_suggestions/?search_term=';
		let ultimaBusq = '';

		$clave_cfdi_claveprodserv.keyup(function(event) {

			window.clearTimeout(timeoutId);
			let value = event.target.value.trim();

			if (value.length >= 3 && value != ultimaBusq) {
				timeoutId = window.setTimeout(makeRequestCatalogData, 750, prodserv_datalist, value, route);
			}
		});

		function makeRequestCatalogData(datalist, str, route) {
			let url = encodeURI(route + str);
			// console.log('(new product) entered makeRequestCatalogData');

			httpRequest = new XMLHttpRequest();

			if (!httpRequest) {
				console.log('No puede crearse instancia de XMLHttpRequest ' + url);
				return false;
			}

			httpRequest.onreadystatechange = () => {responseHandler(datalist, str, route)};
			httpRequest.open('GET', url);
			httpRequest.send();
		}

		function responseHandler(datalist, str, route) {

			if (httpRequest.readyState === XMLHttpRequest.DONE) {

				if (httpRequest.status === 200) {

					let list = JSON.parse(httpRequest.responseText);

					clearOptions(datalist);
					addOptionsFromArray(datalist, list);
					ultimaBusq = str;

					// if (list.length) {
					// 	console.log('(new product) "' + str + '" agregadas [...' + list.length + '].');
					// } else {
					// 	console.log('(new product) "' + str + '" agregadas [].');
					// }

				} else {
					console.log('Hubo un problema con el request ' + route + str);
				}
			}
		}

		function addOptionsFromArray(datalist, arr) {
			for (let item of arr) {
				let opt = document.createElement('option');

				opt.value = item[0];
				opt.text = item[1] + ' (' + item[0] + ')';
				datalist.appendChild(opt);
			}
		}

		function clearOptions(datalist) {
			while (datalist.firstChild) {
				datalist.removeChild(datalist.firstChild);
			}
		}


		$valor_maximo.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        alert("Ok");
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});


		//Quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$valor_maximo.focus(function(e){
			if(parseFloat($valor_maximo.val())<1){
				$valor_maximo.val('');
			}
		});
		
		//Pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$valor_maximo.blur(function(e){
			if(parseFloat($valor_maximo.val())==0||$valor_maximo.val()==""){
				$valor_maximo.val(0.0);
			}
		});
                
		//Validar campo tiempos de entrega, solo acepte numeros y punto
		$valor_minimo.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		//Quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$valor_minimo.focus(function(e){
			if(parseFloat($valor_minimo.val())<1){
				$valor_minimo.val('');
			}
		});
		
		//Pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$valor_minimo.blur(function(e){
			if(parseFloat($valor_minimo.val())==0||$valor_minimo.val()==""){
				$valor_minimo.val(0.0);
			}
		});
                
		//Validar campo tiempos de entrega, solo acepte numeros y punto
		$punto_reorden.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		//Quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$punto_reorden.focus(function(e){
			if(parseFloat($punto_reorden.val())<1){
				$punto_reorden.val('');
			}
		});
		
		//Pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$punto_reorden.blur(function(e){
			if(parseFloat($punto_reorden.val())==0||$punto_reorden.val()==""){
				$punto_reorden.val(0.0);
			}
		});
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tbody > tr", $grid_productos_componentes).size();
			$total_tr.val(trCount);
			
			if($incluye_produccion.val()=='false'){
				//Aqui solo debe entrar cuando la empresa no incluya modulo de produccion
				//PRODUCTO TERMINADO, INTERMEDIO Y EN DESARROLLO
				if(parseInt($select_prod_tipo.val())==1 || parseInt($select_prod_tipo.val())==2 || parseInt($select_prod_tipo.val())==8){
					if(trCount > 0){
						//alert($total_porcentaje.val());
						if(parseFloat(parseFloat($total_porcentaje.val()).toFixed(4))<1){
							jAlert("La suma total de las cantidades debe ser igual a la Unidad(1).", 'Atencion!');
							//alert($total_porcentaje.val());
							return false;
						}else{
							if(parseFloat(parseFloat($total_porcentaje.val()).toFixed(4))>1){
								jAlert("Has excedido la Unidad(1). Verifique los datos ingresados en la lista de productos.", 'Atencion!');
								return false;
							}else{
								return true;
							}
						}
					}else{
						jAlert("Es necesario Agregar a la lista los productos Materia Prima.", 'Atencion!');
						return false;
					}
				}
			}
			
			//KIT
			if(parseInt($select_prod_tipo.val())==3){
				if(trCount > 0){
					if(parseFloat($total_porcentaje.val())<1){
						jAlert("La suma total de los componentes del KIT debe ser mayor que 0.", 'Atencion!');
						return false;
					}
				}else{
					jAlert("Es necesario Agregar los productos componentes del KIT.", 'Atencion!');
					return false;
				}
			}
		});
		
		//Ligamos el boton cancelar al evento click para eliminar la forma
		$cancelar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-product-overlay').fadeOut(remove);
		});
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-product-overlay').fadeOut(remove);
		});
	});
	
	var carga_formaProducts00_for_datagrid00 = function(id_to_show, accion_mode){
		
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_producto':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el producto seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El producto fue eliminado exitosamente.", 'Atencion!');
						}
						
						if ( entry['success'] == '01' ){
							jAlert("El producto no pudo ser eliminado porque tiene Formula.\nElimine la Formula y despues proceda a eliminar el producto.", 'Atencion!');
						}
						
						if ( entry['success'] == '02' ){
							jAlert("El producto no pudo ser eliminado porque forma parte de una Formula.\nElimine la Formula y despues proceda a eliminar el producto.", 'Atencion!');
						}
						
						if ( entry['success'] == '03' ){
							jAlert("El producto no pudo ser eliminado porque forma parte de un KIT.\nElimine el KIT y despues proceda a eliminar el producto.", 'Atencion!');
						}
						
						if ( entry['success'] == '04' ){
							jAlert("El producto no pudo ser eliminado porque forma parte de una Formula o un KIT.\nElimine la Formula o KIT y despues proceda a eliminar el producto.", 'Atencion!');
						}
						
						if ( entry['success'] == '05' ){
							jAlert("El producto no pudo ser eliminado porque forma parte de la Configuraci&oacute;n de un Envase.\nElimine la configuraci&oacute;n del Envase en el M&oacute;dulo de Envasado y despu&oacute;s proceda a eliminar el producto.", 'Atencion!');
						}
						
						if ( entry['success'] == '06' ){
							jAlert("El producto no pudo ser eliminado porque tiene existencia en almacen.", 'Atencion!');
						}
						
						$get_datos_grid();
					});
				};
			});
		}else{
			var form_to_show = 'formaProducto00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			var accion = "get_producto";
			
			$(this).modalPanel_Products();
			
			$('#forma-product-window').css({"margin-left": -260, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-product-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProducto.json';
				var $arreglo = {'id_producto':id_to_show, 'iu': $('#lienzo_recalculable').find('input[name=iu]').val() };
				
				//variables del catalogo
				var $incluye_produccion = $('#forma-product-window').find('input[name=incluye_pro]');
				var $campo_id_producto = $('#forma-product-window').find('input[name=id_producto]');
				var $codigo = $('#forma-product-window').find('input[name=codigo]');
				var $descripcion = $('#forma-product-window').find('input[name=descripcion]');
				var $codigo_barras = $('#forma-product-window').find('input[name=codigo_barras]');
				var $tiempos_de_entrega = $('#forma-product-window').find('input[name=tentrega]');
				var $densidad = $('#forma-product-window').find('input[name=densidad]');
				var $valor_maximo = $('#forma-product-window').find('input[name=valor_maximo]');
				var $valor_minimo = $('#forma-product-window').find('input[name=valor_minimo]');
				var $punto_reorden = $('#forma-product-window').find('input[name=punto_reorden]');
                var $clave_cfdi_claveprodserv = $('#forma-product-window').find('input[name=clave_cfdi_claveprodserv]');
			
				var $td_etiqueta_prov_clie = $('#forma-product-window').find('#td_etiqueta_prov_clie');
				var $busca_clie_prov = $('#forma-product-window').find('#busca_clie_prov');
				var $proveedor = $('#forma-product-window').find('input[name=proveedor]');
				var $id_proveedor = $('#forma-product-window').find('input[name=id_proveedor]');
				var $no_clie = $('#forma-product-window').find('input[name=no_clie]');
		
				var $select_estatus = $('#forma-product-window').find('select[name=select_estatus]');
				var $select_seccion = $('#forma-product-window').find('select[name=select_seccion]');
				var $select_grupo = $('#forma-product-window').find('select[name=select_grupo]');
				var $select_linea = $('#forma-product-window').find('select[name=select_linea]');
				var $select_marca = $('#forma-product-window').find('select[name=select_marca]');
				var $select_clase = $('#forma-product-window').find('select[name=select_clase]');
				var $select_familia = $('#forma-product-window').find('select[name=select_familia]');
				var $select_subfamilia = $('#forma-product-window').find('select[name=select_subfamilia]');
				var $select_prod_tipo = $('#forma-product-window').find('select[name=select_prodtipo]');
				var $select_unidad = $('#forma-product-window').find('select[name=select_unidad]');
				var $select_clasifstock = $('#forma-product-window').find('select[name=select_clasifstock]');
				var $select_iva = $('#forma-product-window').find('select[name=select_iva]');
				var $select_ieps = $('#forma-product-window').find('select[name=select_ieps]');
				var $select_moneda = $('#forma-product-window').find('select[name=select_moneda]');
				var $select_retencion = $('#forma-product-window').find('select[name=select_retencion]');
				
				var $check_noserie = $('#forma-product-window').find('input[name=check_noserie]');
				var $check_nom = $('#forma-product-window').find('input[name=check_nom]');
				var $check_nolote = $('#forma-product-window').find('input[name=check_nolote]');
				var $check_pedimento = $('#forma-product-window').find('input[name=check_pedimento]');
				var $check_stock = $('#forma-product-window').find('input[name=check_stock]');
				var $check_ventaext = $('#forma-product-window').find('input[name=check_ventaext]');
				var $check_compraext = $('#forma-product-window').find('input[name=check_compraext]');
				var $check_flete = $('#forma-product-window').find('input[name=check_flete]');
				
				//variables para controlar las imagenes y el pdf
				var $nameimg = $('#forma-product-window').find('input[name=nameimg]');
				var $namepdf = $('#forma-product-window').find('input[name=namepdf]');
				var $descripcion_corta = $('#forma-product-window').find('textarea[name=descripcion_corta]');
				var $descripcion_larga = $('#forma-product-window').find('textarea[name=descripcion_larga]');
				var $edito_pdf = $('#forma-product-window').find('input[name=edito_pdf]');
				var $edito_imagen = $('#forma-product-window').find('input[name=edito_imagen]');
				
                                
				//presentaciones seleccionados y disponibles
				var $select_disponibles= $('#forma-product-window').find('select[name=disponibles]');
				var $select_seleccionados = $('#forma-product-window').find('select[name=seleccionados]');
				var $campo_pres_on = $('#forma-product-window').find('input[name=pres_on]');
				var $select_pres_default = $('#forma-product-window').find('select[name=select_pres_default]');
				
				//agregar y remover presentaciones
				var $agregar_pres = $('#forma-product-window').find('a[href*=agregar_pres]');
				var $remover_pres = $('#forma-product-window').find('a[href*=remover_pres]');
				
				var $grid_productos_componentes = $('#forma-product-window').find('#grid_productos');
				var $total_porcentaje = $('#forma-product-window').find('input[name=total_porcentaje]');
				var $total_tr = $('#forma-product-window').find('input[name=total_tr]');
				
				var $sku_minigrid = $('#forma-product-window').find('input[name=sku_minigrid]');
				var $descr_prod_minigrid = $('#forma-product-window').find('input[name=descr_prod_minigrid]');
				
				//variables de los href
				var $agregar_prod = $('#forma-product-window').find('a[href*=agregar_produ_minigrid]');
				var $buscar_prod_ingrediente = $('#forma-product-window').find('a[href*=busca_producto_ingrediente]');
				var $busca_proveedor = $('#forma-product-window').find('a[href*=busca_proveedor]');
				
				var $pestana_contabilidad = $('#forma-product-window').find('ul.pestanas').find('a[href*=#tabx-3]');
				
				var $id_cta_gas = $('#forma-product-window').find('input[name=id_cta_gasto]');
				var $gas_cuenta = $('#forma-product-window').find('input[name=gas_cuenta]');
				var $gas_scuenta = $('#forma-product-window').find('input[name=gas_scuenta]');
				var $gas_sscuenta = $('#forma-product-window').find('input[name=gas_sscuenta]');
				var $gas_ssscuenta = $('#forma-product-window').find('input[name=gas_ssscuenta]');
				var $gas_sssscuenta = $('#forma-product-window').find('input[name=gas_sssscuenta]');
				var $gas_descripcion = $('#forma-product-window').find('input[name=gas_descripcion]');
				
				var $id_cta_costvent = $('#forma-product-window').find('input[name=id_cta_costvent]');
				var $costvent_cuenta = $('#forma-product-window').find('input[name=costvent_cuenta]');
				var $costvent_scuenta = $('#forma-product-window').find('input[name=costvent_scuenta]');
				var $costvent_sscuenta = $('#forma-product-window').find('input[name=costvent_sscuenta]');
				var $costvent_ssscuenta = $('#forma-product-window').find('input[name=costvent_ssscuenta]');
				var $costvent_sssscuenta = $('#forma-product-window').find('input[name=costvent_sssscuenta]');
				var $costvent_descripcion = $('#forma-product-window').find('input[name=costvent_descripcion]');
				
				var $id_cta_vent = $('#forma-product-window').find('input[name=id_cta_vent]');
				var $vent_cuenta = $('#forma-product-window').find('input[name=vent_cuenta]');
				var $vent_scuenta = $('#forma-product-window').find('input[name=vent_scuenta]');
				var $vent_sscuenta = $('#forma-product-window').find('input[name=vent_sscuenta]');
				var $vent_ssscuenta = $('#forma-product-window').find('input[name=vent_ssscuenta]');
				var $vent_sssscuenta = $('#forma-product-window').find('input[name=vent_sssscuenta]');
				var $vent_descripcion = $('#forma-product-window').find('input[name=vent_descripcion]');
				
				var $busca_gasto = $('#forma-product-window').find('a[href=busca_gasto]');
				var $busca_costvent = $('#forma-product-window').find('a[href=busca_costvent]');
				var $busca_vent = $('#forma-product-window').find('a[href=busca_vent]');
				
				var $limpiar_gasto = $('#forma-product-window').find('a[href=limpiar_gasto]');
				var $limpiar_costvent = $('#forma-product-window').find('a[href=limpiar_costvent]');
				var $limpiar_vent = $('#forma-product-window').find('a[href=limpiar_vent]');
				
				var $cerrar_plugin = $('#forma-product-window').find('#close');
				var $cancelar_plugin = $('#forma-product-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-product-window').find('#submit');
				$total_porcentaje.val(0);
				
				//$codigo.attr({'readOnly':true});
				//$codigo.css({'background' : '#DDDDDD'});
				$proveedor.attr({'readOnly':true});
				$busca_proveedor.hide();
				
				if(parseInt(rolVendedor)>=1){
					//Ocultar boton Actualizar
					$submit_actualizar.hide();
					
					//Quitar enter a todos los campos input
					$('#forma-product-window').find('input').keypress(function(e){
						if(e.which==13 ) {
							return false;
						}
					});
				}
				
				//Quitar enter al input sku_minigrid
				$sku_minigrid.keypress(function(e){
					if(e.which==13 ) {
						return false;
					}
				});
				
				//Quitar enter al input descr_prod_minigrid
				$descr_prod_minigrid.keypress(function(e){
					if(e.which==13 ) {
						return false;
					}
				});
				
				
				$gas_cuenta.hide();
				$gas_scuenta.hide();
				$gas_sscuenta.hide();
				$gas_ssscuenta.hide();
				$gas_sssscuenta.hide();
				
				$costvent_cuenta.hide();
				$costvent_scuenta.hide();
				$costvent_sscuenta.hide();
				$costvent_ssscuenta.hide();
				$costvent_sssscuenta.hide();
				
				$vent_cuenta.hide();
				$vent_scuenta.hide();
				$vent_sscuenta.hide();
				$vent_ssscuenta.hide();
				$vent_sssscuenta.hide();
				
				$busca_clie_prov.hide();
				$proveedor.hide();
				$no_clie.hide();
				
				/*Codigo para subir la imagen*/
				var input_json_upload = document.location.protocol + '//' + document.location.host + '/'+controller+'/fileUpload.json';
				var button_img = $('#forma-product-window').find('#upload_button_img'), interval;
				new AjaxUpload(button_img,{
					action: input_json_upload, 
					name: 'file',
					onSubmit : function(file , ext){
						if (! (ext && /^(png*|jpg*)$/.test(ext))){
							jAlert("El formato de la imagen debe de ser .png", 'Atencion!');
							return false;
						} else {
							button_img.text('Cargando..');
							this.disable();
						}
					},
					onComplete: function(file, response){
						button_img.text('Cambiar Imagen');
						window.clearInterval(interval);
						this.enable();
						$nameimg.val(file);
						$edito_imagen.val(1);
						var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
						var input_json_img = document.location.protocol + '//' + document.location.host + '/' + controller + '/imgDownloadImg/'+file+'/0/'+iu+'/out.json';
						
						$('#forma-product-window').find('#contenidofile_img').removeAttr("src").attr("src",input_json_img);
					}
				});
				
				/*Codigo para subir la pdf*/
				var input_json_upload = document.location.protocol + '//' + document.location.host + '/'+controller+'/fileUpload.json';
				var button_pdf = $('#forma-product-window').find('#upload_button_pdf'), interval;
				new AjaxUpload(button_pdf,{
					action: input_json_upload, 
					name: 'file',
					onSubmit : function(file , ext){
						if (! (ext && /^(pdf)$/.test(ext))){
							jAlert("El formato del archivo, puede ser .pdf", 'Atencion!');
							return false;
						} else {
							button_pdf.text('Cargando..');
							this.disable();
						}
					},
					onComplete: function(file, response){
						button_pdf.text('Cambiar PDF');
						window.clearInterval(interval);
						this.enable();
						$namepdf.val(file);
						$edito_pdf.val(1);
						$('#forma-product-window').find('#contenidofile_pdf').text(file);
					}
				});
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-product-overlay').fadeOut(remove);
						jAlert("Producto actualizado", 'Atencion!');
						//$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-product-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-product-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							}
						}
					};
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$incluye_produccion.val(entry['Extras']['0']['mod_produccion']);
					
					if(entry['Extras'][0]['ilog']=='true'){
						$td_etiqueta_prov_clie.html('No. Cliente');
						$no_clie.show();
						$busca_clie_prov.show();
					}else{
						$td_etiqueta_prov_clie.html('Proveedor');
						$proveedor.show();
					}
					
					if( entry['Extras'][0]['incluye_contab']=='false' ){
						$pestana_contabilidad.parent().hide();
					}else{
						//visualizar subcuentas de acuerdo al nivel definido para la empresa
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $gas_cuenta.show(); $costvent_cuenta.show(); $vent_cuenta.show();  };
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $gas_scuenta.show(); $costvent_scuenta.show(); $vent_scuenta.show(); };
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $gas_sscuenta.show(); $costvent_sscuenta.show(); $vent_sscuenta.show(); };
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $gas_ssscuenta.show(); $costvent_ssscuenta.show(); $vent_ssscuenta.show(); };
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $gas_sssscuenta.show(); $costvent_sssscuenta.show(); $vent_sssscuenta.show(); };
						
						$id_cta_gas.attr({ 'value' : entry['Contab'][0]['gas_id_cta'] });
						$gas_cuenta.attr({ 'value' : entry['Contab'][0]['gas_cta'] });
						$gas_scuenta.attr({ 'value' : entry['Contab'][0]['gas_subcta'] });
						$gas_sscuenta.attr({ 'value' : entry['Contab'][0]['gas_ssubcta'] });
						$gas_ssscuenta.attr({ 'value' : entry['Contab'][0]['gas_sssubcta'] });
						$gas_sssscuenta.attr({ 'value' : entry['Contab'][0]['gas_ssssubcta'] });
						$gas_descripcion.attr({ 'value' : entry['Contab'][0]['gas_descripcion'] });
						
						$id_cta_costvent.attr({ 'value' : entry['Contab'][0]['costvent_id_cta'] });
						$costvent_cuenta.attr({ 'value' : entry['Contab'][0]['costvent_cta'] });
						$costvent_scuenta.attr({ 'value' : entry['Contab'][0]['costvent_subcta'] });
						$costvent_sscuenta.attr({ 'value' : entry['Contab'][0]['costvent_ssubcta'] });
						$costvent_ssscuenta.attr({ 'value' : entry['Contab'][0]['costvent_sssubcta'] });
						$costvent_sssscuenta.attr({ 'value' : entry['Contab'][0]['costvent_ssssubcta'] });
						$costvent_descripcion.attr({ 'value' : entry['Contab'][0]['costvent_descripcion'] });
						
						$id_cta_vent.attr({ 'value' : entry['Contab'][0]['vent_id_cta'] });
						$vent_cuenta.attr({ 'value' : entry['Contab'][0]['vent_cta'] });
						$vent_scuenta.attr({ 'value' : entry['Contab'][0]['vent_subcta'] });
						$vent_sscuenta.attr({ 'value' : entry['Contab'][0]['vent_ssubcta'] });
						$vent_ssscuenta.attr({ 'value' : entry['Contab'][0]['vent_sssubcta'] });
						$vent_sssscuenta.attr({ 'value' : entry['Contab'][0]['vent_ssssubcta'] });
						$vent_descripcion.attr({ 'value' : entry['Contab'][0]['vent_descripcion'] });
						
						//Busca Cuenta Gastos
						$busca_gasto.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//Busca Cuenta Costo de Venta
						$busca_costvent.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(2, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//Busca Cuenta Venta
						$busca_vent.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(3, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//Limpiar campos Cuenta Gastos
						$limpiar_gasto.click(function(event){
							event.preventDefault();
							$id_cta_gas.val(0);
							$gas_cuenta.val('');
							$gas_scuenta.val('');
							$gas_sscuenta.val('');
							$gas_ssscuenta.val('');
							$gas_sssscuenta.val('');
							$gas_descripcion.val('');
						});
						
						//limpiar campos Cuenta Costo de Venta
						$limpiar_costvent.click(function(event){
							event.preventDefault();
							$id_cta_costvent.val(0);
							$costvent_cuenta.val('');
							$costvent_scuenta.val('');
							$costvent_sscuenta.val('');
							$costvent_ssscuenta.val('');
							$costvent_sssscuenta.val('');
							$costvent_descripcion.val('');
						});
						
						//limpiar campos Cuenta Venta
						$limpiar_vent.click(function(event){
							event.preventDefault();
							$id_cta_vent.val(0);
							$vent_cuenta.val('');
							$vent_scuenta.val('');
							$vent_sscuenta.val('');
							$vent_ssscuenta.val('');
							$vent_sssscuenta.val('');
							$vent_descripcion.val('');
						});
					}
					
					$campo_id_producto.attr({'value' : entry['Producto'][0]['id']});
					$codigo.attr({'value' : entry['Producto'][0]['sku']});
					$descripcion.attr({'value' : entry['Producto'][0]['descripcion']});
					$codigo_barras.attr({'value' : entry['Producto'][0]['codigo_barras']});
					$proveedor.text(entry['Producto'][0]['proveedor']);
					$id_proveedor.text(entry['Producto'][0]['cxp_prov_id']);
					$tiempos_de_entrega.attr({'value' : entry['Producto'][0]['tentrega']});
					$densidad.attr({'value' : entry['Producto'][0]['densidad']});
					$valor_maximo.attr({'value' : entry['Producto'][0]['valor_maximo']});
					$valor_minimo.attr({'value' : entry['Producto'][0]['valor_minimo']});
					$punto_reorden.attr({'value' : entry['Producto'][0]['punto_reorden']});
					$no_clie.attr({'value' : entry['Producto'][0]['no_clie']});
                    $clave_cfdi_claveprodserv.attr({'value' : entry['Producto'][0]['clave_cfdi_claveprodserv']});
					
					$check_noserie.attr('checked', (entry['Producto'][0]['requiere_numero_serie'] == 'true')? true:false );
					$check_nom.attr('checked', (entry['Producto'][0]['requiere_nom'] == 'true')? true:false );
					$check_nolote.attr('checked', (entry['Producto'][0]['requiere_numero_lote'] == 'true')? true:false );
					$check_pedimento.attr('checked', (entry['Producto'][0]['requiere_pedimento'] == 'true')? true:false );
					$check_stock.attr('checked', (entry['Producto'][0]['permitir_stock'] == 'true')? true:false );
					$check_ventaext.attr('checked', (entry['Producto'][0]['venta_moneda_extranjera'] == 'true')? true:false );
					$check_compraext.attr('checked', (entry['Producto'][0]['compra_moneda_extranjera'] == 'true')? true:false );
					$check_flete.attr('checked', (entry['Producto'][0]['flete'] == 'true')? true:false );
					
					$nameimg.attr('value', entry['Producto'][0]['archivo_img']);
					$namepdf.attr('value', entry['Producto'][0]['archivo_pdf']);
					$descripcion_corta.text(entry['Producto'][0]['descripcion_corta']);
					$descripcion_larga.text(entry['Producto'][0]['descripcion_larga']);
					
					
					if(entry['Producto'][0]['archivo_img'] != ""){
						button_img.text('Cambiar Imagen');
						$nameimg.val(entry['Producto'][0]['archivo_img']);
						$edito_imagen.val(0);
						var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
						var input_json_img = document.location.protocol + '//' + document.location.host + '/' + controller + '/imgDownloadImg/'+entry['Producto'][0]['archivo_img']+'/'+entry['Producto'][0]['id']+'/'+iu+'/out.json';
						$('#forma-product-window').find('#contenidofile_img').removeAttr("src").attr("src",input_json_img);
					}
					
					if(entry['Producto'][0]['archivo_pdf'] != ""){
						button_pdf.text('Cambiar PDF');
						$namepdf.val(entry['Producto'][0]['archivo_pdf']);
						$edito_pdf.val(0);
						$('#forma-product-window').find('#contenidofile_pdf').text(entry['Producto'][0]['archivo_pdf']);
					}
                                        
					//estatus
					$select_estatus.children().remove();
					var status_html = '';		
					if( entry['Producto'][0]['cxp_prov_id']=='true'){
						status_html = '<option value="true" selected="yes">Activo</option>';
						status_html += '<option value="false">Inactivo</option>';
					}else{
						status_html = '<option value="true">Activo</option>';
						status_html += '<option value="false" selected="yes">Inactivo</option>';	
					}
					$select_estatus.append(status_html);
					
					
					//Alimentando select de secciones
					$select_seccion.children().remove();
					var secciones_hmtl = '<option value="0">[--Seleccionar Seccion--]</option>';
					$.each(entry['Secciones'],function(entryIndex,sec){
						if(parseInt(entry['Producto'][0]['inv_seccion_id'])==parseInt(sec['id'])){
							secciones_hmtl += '<option value="' + sec['id'] + '"  selected="yes">' + sec['titulo'] + '</option>';
						}else{
							secciones_hmtl += '<option value="' + sec['id'] + '"  >' + sec['titulo'] + '</option>';
						}
					});
					$select_seccion.append(secciones_hmtl);
					
					
					
					//Alimentando select de grupos
					$select_grupo.children().remove();
					var grupo_hmtl = '<option value="0">[--Seleccionar Grupo--]</option>';
					$.each(entry['Grupos'],function(entryIndex,gpo){
						if(parseInt(entry['Producto'][0]['inv_prod_grupo_id'])==parseInt(gpo['id'])){
							grupo_hmtl += '<option value="' + gpo['id'] + '"  selected="yes">' + gpo['titulo'] + '</option>';
						}else{
							grupo_hmtl += '<option value="' + gpo['id'] + '"  >' + gpo['titulo'] + '</option>';
						}
					});
					$select_grupo.append(grupo_hmtl);
					
					//Alimentando select de lineas
					$select_linea.children().remove();
					var lineas_hmtl = '<option value="0">[--Seleccionar Linea--]</option>';
					$.each(arrayLineas,function(entryIndex,lin){
						if(parseInt(entry['Producto'][0]['inv_prod_linea_id'])==parseInt(lin['id'])){
							lineas_hmtl += '<option value="' + lin['id'] + '" selected="yes">' + lin['titulo'] + '</option>';
						}else{
							lineas_hmtl += '<option value="' + lin['id'] + '">' + lin['titulo'] + '</option>';
						}
					});
					$select_linea.append(lineas_hmtl);
					
					
					//Alimentando select de marcas
					$select_marca.children().remove();
					var marcas_hmtl = '<option value="0">[--Seleccionar Marca--]</option>';
					$.each(arrayMarcas,function(entryIndex,mar){
						if(parseInt(entry['Producto'][0]['inv_mar_id'])==parseInt(mar['id'])){
							marcas_hmtl += '<option value="' + mar['id'] + '" selected="yes">' + mar['titulo'] + '</option>';
						}else{
							marcas_hmtl += '<option value="' + mar['id'] + '"  >' + mar['titulo'] + '</option>';
						}
					});
					$select_marca.append(marcas_hmtl);
					
					
					
					//Alimentando select de clases
					$select_clase.children().remove();
					var clase_hmtl = '<option value="0">[--Seleccionar Clase--]</option>';
					$.each(entry['Clases'],function(entryIndex,clase){
						if(parseInt(entry['Producto'][0]['inv_clas_id'])==parseInt(clase['id'])){
							clase_hmtl += '<option value="' + clase['id'] + '"  selected="yes">' + clase['titulo'] + '</option>';
						}else{
							clase_hmtl += '<option value="' + clase['id'] + '"  >' + clase['titulo'] + '</option>';
						}
					});
					$select_clase.append(clase_hmtl);
					
					/*
					//Alimentando select de familias
					$select_familia.children().remove();
					var familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
					$.each(entry['Familias'],function(entryIndex,fam){
						if(parseInt(entry['Producto'][0]['inv_prod_familia_id'])==parseInt(fam['id'])){
							familia_hmtl += '<option value="' + fam['id'] + '" selected="yes">' + fam['titulo'] + '</option>';
						}else{
							familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
						}
					});
					$select_familia.append(familia_hmtl);
					
					
					
					//Alimentando select de Subfamilias
					$select_subfamilia.children().remove();
					var subfamilia_hmtl = '<option value="0">[--Seleccionar Subfmilia--]</option>';
					$.each(entry['Subfamilias'],function(entryIndex,subfam){
						if(parseInt(entry['Producto'][0]['subfamilia_id'])==parseInt(subfam['id'])){
							subfamilia_hmtl += '<option value="' + subfam['id'] + '" selected="yes">' + subfam['titulo'] + '</option>';
						}else{
							subfamilia_hmtl += '<option value="' + subfam['id'] + '"  >' + subfam['titulo'] + '</option>';
						}
					});
					$select_subfamilia.append(subfamilia_hmtl);
					*/
					
					//alimentando select con tipos de producto
					$select_prod_tipo.children().remove();
					//var prodtipos_hmtl = '<option value="0">[--Seleccionar Tipo--]</option>';
					var prodtipos_hmtl = '';
					$.each(arrayTProd,function(entryIndex,pt){
						if(parseInt(entry['Producto'][0]['tipo_de_producto_id'])==parseInt(pt['id'])){
							prodtipos_hmtl += '<option value="' + pt['id'] + '" selected="yes">' + pt['titulo'] + '</option>';
							
							//tipo=1 TERMINADO, 2=INTERMEDIO, 8=DESARROLLO
							if(parseInt(entry['Producto'][0]['tipo_de_producto_id'])==1 || parseInt(entry['Producto'][0]['tipo_de_producto_id'])==2 || parseInt(entry['Producto'][0]['tipo_de_producto_id'])==8){
								if($incluye_produccion.val()=='false'){
									//aqui solo debe entrar cuando la empresa no incluya modulo de produccion
									$('div.contenedor_grid_prod').css({'display':'block'});
									$('#forma-product-window').find('.product_div_one').css({'height':'505px'});
								}
							}
							
							//compentado por paco, por el cambio de productos
							if(parseInt(entry['Producto'][0]['tipo_de_producto_id'])==3 ){
								$('div.contenedor_grid_prod').css({'display':'block'});
								$('#forma-product-window').find('.product_div_one').css({'height':'505px'});
							}
							
							//tipo=3 es KIT, tipo=4 es SERVICIOS
							if(parseInt(entry['Producto'][0]['tipo_de_producto_id'])==3 || parseInt(entry['Producto'][0]['tipo_de_producto_id'])==4 ){
								$deshabilitar_campos(
									"desahabilitar",
									$proveedor,
									$tiempos_de_entrega,
									$select_prod_tipo,
									$select_estatus,
									$select_seccion,
									$select_grupo,
									$select_linea,
									$select_marca,
									$select_clase,
									$select_familia,
									$select_subfamilia,
									$select_unidad,
									$select_clasifstock,
									$select_iva,
									$select_ieps,
									$check_noserie,
									$check_nom,
									$check_nolote,
									$check_pedimento,
									$check_stock,
									$check_ventaext,
									$check_compraext,
									$select_disponibles,
									$select_seleccionados,
									$agregar_pres,
									$remover_pres,
									$densidad,
									$valor_maximo,
									$valor_minimo,
									$punto_reorden,
									$clave_cfdi_claveprodserv);
							}else{
									//$deshabilitar_campos("habilitar",$proveedor,$tiempos_de_entrega,$select_prod_tipo,$select_estatus,$select_seccion,$select_grupo,$select_linea,$select_marca,$select_clase,$select_familia,$select_subfamilia,$select_unidad,$select_clasifstock,$select_iva,$select_ieps,$check_noserie,$check_nom,$check_nolote,$check_pedimento,$check_stock,$check_ventaext,$check_compraext,$select_disponibles,$select_seleccionados,$agregar_pres,$remover_pres);
							}
						}else{
							//prodtipos_hmtl += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
						}
					});
					$select_prod_tipo.append(prodtipos_hmtl);
					
                                        
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFamiliasByTipoProd.json';
					$arreglo = {'tipo_prod':entry['Producto'][0]['tipo_de_producto_id'], 'iu': $('#lienzo_recalculable').find('input[name=iu]').val() };
					$.post(input_json,$arreglo,function(data){
						$select_familia.children().remove();
						familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
						$.each(data['Familias'],function(entryIndex,fam){
							if(parseInt(entry['Producto'][0]['inv_prod_familia_id'])==parseInt(fam['id'])){
								familia_hmtl += '<option value="' + fam['id'] + '" selected="yes">' + fam['titulo'] + '</option>';
							}else{
								familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
							}
						});
						$select_familia.append(familia_hmtl);
					});
                                        
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSubFamiliasByFamProd.json';
					$arreglo = {'fam':entry['Producto'][0]['inv_prod_familia_id'],
									'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
								}
					$.post(input_json,$arreglo,function(data){
						//Alimentando select de Subfamilias
						$select_subfamilia.children().remove();
						var subfamilia_hmtl = '<option value="0">[--Seleccionar Subfmilia--]</option>';
						$.each(data['SubFamilias'],function(dataIndex,subfam){
							if(parseInt(entry['Producto'][0]['subfamilia_id'])==parseInt(subfam['id'])){
								subfamilia_hmtl += '<option value="' + subfam['id'] + '" selected="yes">' + subfam['titulo'] + '</option>';
							}else{
								subfamilia_hmtl += '<option value="' + subfam['id'] + '"  >' + subfam['titulo'] + '</option>';
							}
						});
						$select_subfamilia.append(subfamilia_hmtl);
					});
					
					
					//Alimentando select de unidades
					$select_unidad.children().remove();
					//var unidads_hmtl = '<option value="0">[--Seleccionar Unidad--]</option>';
					var unidads_hmtl = '';
					$.each(entry['Unidades'],function(entryIndex,uni){
						if(parseInt(entry['Producto'][0]['unidad_id'])==parseInt(uni['id'])){
							unidads_hmtl += '<option value="' + uni['id'] + '" selected="yes">' + uni['titulo'] + '</option>';
						}else{
							unidads_hmtl += '<option value="' + uni['id'] + '"  >' + uni['titulo'] + '</option>';
						}
					});
					$select_unidad.append(unidads_hmtl);
					
					
					//Alimentando select de clasificacion Stock
					$select_clasifstock.children().remove();
					var stock_hmtl = '<option value="0">[--Seleccionar Clasificacion--]</option>';
					$.each(entry['ClasifStock'],function(entryIndex,stock){
						if(parseInt(entry['Producto'][0]['inv_stock_clasif_id'])==parseInt(stock['id'])){
							stock_hmtl += '<option value="' + stock['id'] + '" selected="yes">' + stock['titulo'] + '</option>';
						}else{
							stock_hmtl += '<option value="' + stock['id'] + '"  >' + stock['titulo'] + '</option>';
						}
					});
					$select_clasifstock.append(stock_hmtl);
					
					
					//Alimentando select de ivas
					$select_iva.children().remove();
					var iva_hmtl = '';
					$.each(entry['Impuestos'],function(entryIndex,iva){
						if(parseInt(entry['Producto'][0]['id_impuesto'])==parseInt(iva['id'])){
							iva_hmtl += '<option value="' + iva['id'] + '" selected="yes">' + iva['descripcion'] + '</option>';
						}else{
							iva_hmtl += '<option value="' + iva['id'] + '"  >' + iva['descripcion'] + '</option>';
						}
					});
					$select_iva.append(iva_hmtl);
					
					
					
					//Alimentando select de ieps
					$select_ieps.children().remove();
					var ieps_hmtl = '<option value="0">[--IEPS--]</option>';
					$.each(entry['Ieps'],function(entryIndex,ieps){
						if(parseInt(entry['Producto'][0]['ieps'])==parseInt(ieps['id'])){
							ieps_hmtl += '<option value="' + ieps['id'] + '" selected="yes">' + ieps['titulo'] + '</option>';
						}else{
							ieps_hmtl += '<option value="' + ieps['id'] + '"  >' + ieps['titulo'] + '</option>';
						}
					});
					$select_ieps.append(ieps_hmtl);
					
					//Alimentando select de retencion de iva
					$select_retencion.children().remove();
					var ret_iva_hmtl = '<option value="0">[------]</option>';
					$.each(entry['ImptosRet'],function(entryIndex,ivaret){
						if(parseInt(entry['Producto'][0]['impto_ret_id'])==parseInt(ivaret['id'])){
							ret_iva_hmtl += '<option value="' + ivaret['id'] + '" selected="yes">' + ivaret['titulo'] + '</option>';
						}else{
							ret_iva_hmtl += '<option value="' + ivaret['id'] + '"  >' + ivaret['titulo'] + '</option>';
						}
					});
					$select_retencion.append(ret_iva_hmtl);
					
					
					$select_moneda.children().remove();
					var moneda_hmtl = '';
					if(parseInt(entry['Producto'][0]['mon_id'])==0){
						moneda_hmtl = '<option value="0" selected="yes">[ - - - ]</option>';
					}
					$.each(arrayMonedas,function(entryIndex,mon){
						if(parseInt(entry['Producto'][0]['mon_id'])==parseInt(mon['id'])){
							moneda_hmtl += '<option value="' + mon['id'] + '" selected="yes">' + mon['descripcion_abr'] + '</option>';
						}else{
							moneda_hmtl += '<option value="' + mon['id'] + '">' + mon['descripcion_abr'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					
					//carga select de presentaciones disponibles
					$select_disponibles.children().remove();
					var presentaciones_hmtl = '';
					$.each(entry['Presentaciones'],function(entryIndex,pres){
						presentaciones_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
					});
					$select_disponibles.append(presentaciones_hmtl);
					
					
					//carga select de presentaciones seleccionados
					$select_seleccionados.children().remove();
					var pres_hmtl = '';
					$.each(entry['PresOn'],function(entryIndex,preson){
						pres_hmtl += '<option value="' + preson['id'] + '"  >' + preson['titulo'] + '</option>';
					});
					$select_seleccionados.append(pres_hmtl);
					
					
					if(parseInt(entry['PresOn'].length)<=0){
						if(parseInt(entry['Producto'][0]['tipo_de_producto_id'])==4){
							//Carga select de Presentacion Default
							$select_pres_default.children().remove();
							var pres_def_hmtl = '<option value="0" selected="yes">[--Presentaci&oacute;n--]</option>';
							$.each(entry['Presentaciones'],function(entryIndex,pres){
								if(/^SERVICIO*|SERVICIOS$/.test(pres['titulo'].toUpperCase())){
									pres_def_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
								}
							});
							$select_pres_default.append(pres_def_hmtl);
						}else{
							//Carga select de Presentacion Default
							$select_pres_default.children().remove();
							var pres_def_hmtl = '<option value="0" selected="yes">[--Presentaci&oacute;n--]</option>';
							$.each(entry['Presentaciones'],function(entryIndex,pres){
								pres_def_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
							});
							$select_pres_default.append(pres_def_hmtl);
						}
					}else{
						//Carga select de Presentacion Default
						$select_pres_default.children().remove();
						var pres_def_hmtl = '<option value="0" selected="yes">[--Presentaci&oacute;n--]</option>';
						$.each(entry['PresOn'],function(entryIndex,presdef){
							if(parseInt(presdef['id']) == parseInt(entry['Producto'][0]['presentacion_id'])){
								pres_def_hmtl += '<option value="' + presdef['id'] + '" selected="yes">' + presdef['titulo'] + '</option>';
							}else{
								pres_def_hmtl += '<option value="' + presdef['id'] + '"  >' + presdef['titulo'] + '</option>';
							}
						});
						$select_pres_default.append(pres_def_hmtl);
					}
					
					
					
					$.each(entry['Ingredientes'],function(entryIndex,ingrediente){
						//$('div.contenedor_grid_prod').css({'display':'block'});
						//$('#forma-product-window').find('.product_div_one').css({'height':'400px'});
						
						//$ingrediente_id.":".$sku.":".$descripcion_es.":".$porcentaje
						//var cadena_to_cut = ingrediente;
						//var valor = cadena_to_cut.split(':')[0];
						//var nom_valor = cadena_to_cut.split(':')[1];
						var trCount = $("tbody > tr", $grid_productos_componentes).size();
						trCount++;
						
						var trr = '';
						trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70">';
								trr += '<a href=#>Eliminar</a>';
								trr += '<input type="hidden" id="delete" name="eliminar" value="'+ingrediente['producto_ingrediente_id']+'">';
								trr += '<input type="hidden" 	name="no_tr" value="'+ trCount +'">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="180">';
								trr += '<input type="text"  value="'+ ingrediente['sku'] +'" id="sku" class="borde_oculto" readOnly="true" style="width:176px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="330">';
								trr += '<input type="text" 		name="nombre" 	value="'+ ingrediente['titulo'] +'" class="borde_oculto" readOnly="true" style="width:326px;">';
							trr += '</td>';
							
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
								trr += '<input type="hidden" id="dec" name="decimales" value="'+ ingrediente['decimales']+'">';
								trr += '<input type="text" id="porcentaje" class="porcentaje'+trCount+'" name="porcentaje_grid" value="'+ ingrediente['cantidad'] +'" style="width:116px;">';
							trr += '</td>';
							
						trr += '</tr>';
						
						var tabla = $grid_productos_componentes.find('tbody');
						tabla.append(trr);
						
						$total_porcentaje.val(parseFloat($total_porcentaje.val())+parseFloat(ingrediente['cantidad']));
						//alert($total_porcentaje.val());
						
						tabla.find('a').bind('click',function(event){
							var total_porcentaje=0;
							if(parseInt($(this).parent().find('#delete').val()) != 0){
								//alert("total_porcentaje1: "+total_porcentaje+"    $total_porcentaje::"+$total_porcentaje.val());
								total_porcentaje = parseFloat($total_porcentaje.val())-parseFloat(parseFloat($(this).parent().parent().find('#porcentaje').val()).toFixed(4));
								$total_porcentaje.val(total_porcentaje);
								//alert("total_porcentaje2: "+total_porcentaje+"      $total_porcentaje:"+$total_porcentaje.val());
								$(this).parent().find('#delete').val(0);
								$(this).parent().parent().hide();
							}
						});
						
						
						if( parseInt($select_prod_tipo.val())==1 || parseInt($select_prod_tipo.val())==2 || parseInt($select_prod_tipo.val())==8){
							//Calcula porcentaje al perder enfoque 
							tabla.find('.porcentaje'+trCount).blur(function(){
								if($(this).val().trim()=='') {
									$(this).val(0);
								}
								
								$(this).val(parseFloat($(this).val()).toFixed(4));
								
								var total=0
								var patron=/^([0-9]){1,12}[.]?[0-9]*$/
								if(patron.test($(this).val())){
									$grid_productos_componentes.find('tbody > tr').each(function (index){
										if(parseInt($(this).find('#delete').val())!=0){
											total = parseFloat(total) + parseFloat(parseFloat($(this).find('#porcentaje').val()).toFixed(4));
										}
										$total_porcentaje.val(total);
									});
									if(parseFloat($total_porcentaje.val())>1){
										jAlert("Has excedido la Unidad del producto",'! Atencion');
									}
								}else{
									jAlert("La cantidad debe tener un 0, ejemplo: 0.5, 0.1, 0.9",'! Atencion');
									$(this).val(0.0)
								}
							});
						}
						
						
						if( parseInt($select_prod_tipo.val())==3 ){
							//calcula porcentaje al perder enfoque 
							tabla.find('.porcentaje'+trCount).blur(function(){
								if(parseFloat($(this).val()) < 1 || $(this).val()=='') {
									$(this).val(1);
								}
								
								var total=0
								$grid_productos_componentes.find('tbody > tr').each(function (index){
									if(parseInt($(this).find('#delete').val())!=0){
										total = parseFloat(total) + parseFloat($(this).find('#porcentaje').val());
									}
									$total_porcentaje.val(total);
								});
								
								if(parseFloat($total_porcentaje.val())<1){
									jAlert("El total de articulos del kit debe ser mayor que 1.",'! Atencion');
								}
							});
						}
					});
					
					
					
					
					//Cambiar tipo de producto
					$select_prod_tipo.change(function(){
						var valor_tipo = $(this).val();
						if(parseInt(valor_tipo)==1 || parseInt(valor_tipo)==2 || parseInt(valor_tipo)==8 || parseInt(valor_tipo)==3){
							if(parseInt(valor_tipo)==1 || parseInt(valor_tipo)==2 || parseInt(valor_tipo)==8){
								if($incluye_produccion.val()=='false'){
									//aqui solo debe entrar cuando la empresa no incluya modulo de produccion
									$('div.contenedor_grid_prod').css({'display':'block'});
									$('#forma-product-window').find('.product_div_one').css({'height':'505px'});
								}
							}else{
								//aqui cuando el tipo de producto es kit
								$('div.contenedor_grid_prod').css({'display':'block'});
								$('#forma-product-window').find('.product_div_one').css({'height':'505px'});
							}
						}else{
							$('div.contenedor_grid_prod').css({'display':'none'});
							$('#forma-product-window').find('.product_div_one').css({'height':'350px'});
						}
						
						
						if(parseInt(valor_tipo)==4){
							//Recargar select de unidades. Carga solo la unidad SERVICIO
							$select_unidad.children().remove();
							var unidads_hmtl = '<option value="0">[--Seleccionar Unidad--]</option>';
							$.each(entry['Unidades'],function(entryIndex,uni){
								if(/^SERVICIO*|SERVICIOS$/.test(uni['titulo'].toUpperCase())){
									unidads_hmtl += '<option value="' + uni['id'] + '"  >' + uni['titulo'] + '</option>';
								}
							});
							$select_unidad.append(unidads_hmtl);
							
							//Recargar presentaciones default. Carga solo la Presentacion SERVICIO
							$select_pres_default.children().remove();
							presentaciones_hmtl = '<option value="0" selected="yes">[--Presentaci&oacute;n--]</option>';
							$.each(entry['Presentaciones'],function(entryIndex,pres){
								if(/^SERVICIO*|SERVICIOS$/.test(pres['titulo'].toUpperCase())){
									presentaciones_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
								}
							});
							$select_pres_default.append(presentaciones_hmtl);
						}else{
							//recarga select de unidades para permitir seleccionar una nueva unidad
							$select_unidad.children().remove();
							var unidads_hmtl = '<option value="0">[--Seleccionar Unidad--]</option>';
							$.each(entry['Unidades'],function(entryIndex,uni){
								unidads_hmtl += '<option value="' + uni['id'] + '"  >' + uni['titulo'] + '</option>';
							});
							$select_unidad.append(unidads_hmtl);
							
							//Recarga select de presentacion default
							$select_pres_default.children().remove();
							presentaciones_hmtl = '<option value="0" selected="yes">[--Presentaci&oacute;n--]</option>';
							$.each(entry['Presentaciones'],function(entryIndex,pres){
								presentaciones_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
							});
							$select_pres_default.append(presentaciones_hmtl);
						}
						
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFamiliasByTipoProd.json';
						$arreglo = {'tipo_prod':$select_prod_tipo.val(),
										'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
									};
						
						$.post(input_json,$arreglo,function(data){
							$select_familia.children().remove();
							familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
							$.each(data['Familias'],function(entryIndex,fam){
									familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
							});
							$select_familia.append(familia_hmtl);
						});
					});
						
					
					
					
				});
				
				
				
				//Buscador de clientes
				$busca_clie_prov.click(function(event){
					event.preventDefault();
					$busca_clientes($no_clie, $('#lienzo_recalculable').find('input[name=iu]').val() );
				});

                                
                                
				$select_familia.change(function(){
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSubFamiliasByFamProd.json';
					$arreglo = {'fam':$select_familia.val(),
								'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
								}
								
					$.post(input_json,$arreglo,function(data){
						//Alimentando select de Subfamilias
						$select_subfamilia.children().remove();
						var subfamilia_hmtl = '<option value="0">[--Seleccionar Subfmilia--]</option>';
						$.each(data['SubFamilias'],function(dataIndex,subfam){
								subfamilia_hmtl += '<option value="' + subfam['id'] + '"  >' + subfam['titulo'] + '</option>';
						});
						$select_subfamilia.append(subfamilia_hmtl);
					});
					
				});
				
				//agregar presentacion
				$agregar_pres.click(function(event){
					event.preventDefault();
					var logica = false;
					var primero=0;
					logica = !$select_disponibles.find('option:selected').remove().appendTo( $select_seleccionados);
					var valor_campo = "";
					var ahora_seleccionados = $select_seleccionados.find('option').get();
					$.each( ahora_seleccionados , function(indice , seleccionado){
						if(primero==0){
							valor_campo += seleccionado.value;
							primero=1;
						}else{
							valor_campo += "," + seleccionado.value;
						}
					});
					//alert(valor_campo);
					$campo_pres_on.attr({'value' : valor_campo});
					return logica; 
				});
				
				
				//remover presentacion
				$remover_pres.click(function(event){
					event.preventDefault();
					var logica = false;
					var primero=0;
					logica = !$select_seleccionados.find('option:selected').remove().appendTo($select_disponibles);
					var valor_campo = "";
					var ahora_seleccionados = $select_seleccionados.find('option').get();
					$.each( ahora_seleccionados , function(indice , seleccionado){
						if(primero==0){
							valor_campo += seleccionado.value;
							primero=1;
						}else{
							valor_campo += "," + seleccionado.value;
						}
					});
					$campo_pres_on.attr({'value' : valor_campo}); 
					
					$select_disponibles.find('option').attr("selected",false);
					
					return logica;
				});
				
				
				
				
				$select_pres_default.change(function(){
					var idPresDef = $(this).val();
					
					//seleccionar la presentación que sea igual a la seleccionada en el campo Default
					$select_disponibles.find('option[value='+ idPresDef +']').attr("selected",true);
					
					//ejecutar la Click del href Agregar Presentacion
					$agregar_pres.trigger('click');
				});
				
				
				
				//validar campo tiempo de entrega, solo acepte numeros y punto
				$tiempos_de_entrega.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
                                
				//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$tiempos_de_entrega.focus(function(e){
					if(parseFloat($tiempos_de_entrega.val())<1){
						$tiempos_de_entrega.val('');
					}
				});
				
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$tiempos_de_entrega.blur(function(e){
					if(parseFloat($tiempos_de_entrega.val())==0||$tiempos_de_entrega.val()==""){
						$tiempos_de_entrega.val(0.0);
					}
				});	
                                
                                
				//validar campo tiempo de entrega, solo acepte numeros y punto
				$densidad.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//Quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$densidad.focus(function(e){
					if(parseFloat($densidad.val())<0.00001){
						$densidad.val('');
					}
				});
				
				//Pone Uno al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$densidad.blur(function(e){
					if($densidad.val().trim()==""){
						$densidad.val(1);
					}else{
						if(parseFloat($densidad.val())<=0){
							$densidad.val(1);
						}
					}
					$densidad.val(parseFloat($densidad.val()).toFixed(4));
				});


				// clave producto servicio (request al catalogo de sat)
				let prodserv_datalist = document.getElementById('prodserv_datalist');
				let timeoutId = 0;
				let route = document.location.protocol + '//' + document.location.host + controller + '/prodserv_suggestions/?search_term=';
				let ultimaBusq = '';

				$clave_cfdi_claveprodserv.keyup(function(event) {

					window.clearTimeout(timeoutId);
					let value = event.target.value.trim();

					if (value.length >= 3 && value != ultimaBusq) {
						timeoutId = window.setTimeout(makeRequestCatalogData, 750, prodserv_datalist, value, route);
					}
				});

				function makeRequestCatalogData(datalist, str, route) {
					let url = encodeURI(route + str);
					// console.log('(edit product) entered makeRequestCatalogData');

					httpRequest = new XMLHttpRequest();

					if (!httpRequest) {
						console.log('No puede crearse instancia de XMLHttpRequest ' + url);
						return false;
					}

					httpRequest.onreadystatechange = () => {responseHandler(datalist, str, route)};
					httpRequest.open('GET', url);
					httpRequest.send();
				}

				function responseHandler(datalist, str, route) {

					if (httpRequest.readyState === XMLHttpRequest.DONE) {

						if (httpRequest.status === 200) {

							let list = JSON.parse(httpRequest.responseText);

							clearOptions(datalist);
							addOptionsFromArray(datalist, list);
							ultimaBusq = str;

							// if (list.length) {
							// 	console.log('(edit product) "' + str + '" agregadas [...' + list.length + '].');
							// } else {
							// 	console.log('(edit product) "' + str + '" agregadas [].');
							// }

						} else {
							console.log('Hubo un problema con el request ' + route + str);
						}
					}
				}

				function addOptionsFromArray(datalist, arr) {
					for (let item of arr) {
						let opt = document.createElement('option');

						opt.value = item[0];
						opt.text = item[1] + ' (' + item[0] + ')';
						datalist.appendChild(opt);
					}
				}

				function clearOptions(datalist) {
					while (datalist.firstChild) {
						datalist.removeChild(datalist.firstChild);
					}
				}


                $valor_maximo.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$valor_maximo.focus(function(e){
					if(parseFloat($valor_maximo.val())<1){
						$valor_maximo.val('');
					}
				});
				
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$valor_maximo.blur(function(e){
					if(parseFloat($valor_maximo.val())==0||$valor_maximo.val()==""){
						$valor_maximo.val(0.0);
					}
				});
						
						//validar campo tiempos de entrega, solo acepte numeros y punto
				$valor_minimo.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$valor_minimo.focus(function(e){
					if(parseFloat($valor_minimo.val())<1){
						$valor_minimo.val('');
					}
				});
				
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$valor_minimo.blur(function(e){
					if(parseFloat($valor_minimo.val())==0||$valor_minimo.val()==""){
						$valor_minimo.val(0.0);
					}
				});
						
				//validar campo tiempos de entrega, solo acepte numeros y punto
				$punto_reorden.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$punto_reorden.focus(function(e){
					if(parseFloat($punto_reorden.val())<1){
						$punto_reorden.val('');
					}
				});
				
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$punto_reorden.blur(function(e){
					if(parseFloat($punto_reorden.val())==0||$punto_reorden.val()==""){
						$punto_reorden.val(0.0);
					}
				});
				
				
				$agregar_prod.click(function(event){
					event.preventDefault();
					$agrega_producto_ingrediente();
				});
				
				//Ejecutar click del href Agregar al pulsar Enter sobre el campo $sku_minigrid
				$(this).aplicarEventoKeypressEjecutaTrigger($sku_minigrid, $agregar_prod);
				
				$buscar_prod_ingrediente.click(function(event){
					event.preventDefault();
					$buscador_producto_ingrediente($sku_minigrid.val(), $descr_prod_minigrid.val());
				});
				
				
				$submit_actualizar.bind('click',function(){
					var trCount = $("tbody > tr", $grid_productos_componentes).size();
					$total_tr.val(trCount);
					
					//aqui se crea cadena con id de presentaciones seleccionados
					var primero=0;
					var valor_campo = "";
					var ahora_seleccionados = $select_seleccionados.find('option').get();
					$.each( ahora_seleccionados , function(indice , seleccionado){
						if(primero==0){
							valor_campo += seleccionado.value;
							primero=1;
						}else{
							valor_campo += "," + seleccionado.value;
						}
					});
					$campo_pres_on.attr({'value' : valor_campo});
					
					
					if(parseInt($select_prod_tipo.val())==1 || parseInt($select_prod_tipo.val())==2 || parseInt($select_prod_tipo.val())==8){
						if($incluye_produccion.val()=='false'){
							//aqui solo debe entrar cuando la empresa no incluya modulo de produccion
							if(trCount > 0){
								//alert($total_porcentaje.val());
								if(parseFloat(parseFloat($total_porcentaje.val()).toFixed(4))<1){
									jAlert("La suma total de las cantidades debe ser igual a la Unidad(1).", 'Atencion!');
									//alert($total_porcentaje.val());
									return false;
								}else{
									if(parseFloat(parseFloat($total_porcentaje.val()).toFixed(4))>1){
											jAlert("Has excedido la Unidad(1). Verifique los datos ingresados en la lista de productos.", 'Atencion!');
											return false;
									}else{
											return true;
									}
								}
							}else{
								jAlert("Es necesario Agregar a la lista los productos Materia Prima.", 'Atencion!');
								return false;
							}
						}
					}
				});
                                
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-product-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-product-overlay').fadeOut(remove);
				});
				
			}
		}
	}


	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getProductos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaProducts00_for_datagrid00);
			
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
