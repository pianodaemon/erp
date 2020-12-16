$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //Arreglo para select tipo de producto
    var arrayTProd;
    
    //Arreglo para Monedas
	var arrayMon;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invcapturacostos";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $nuevo = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Captura de Costo de Reposici&oacute;n');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $sku_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=sku_busqueda]');
	var $campo_descripcion_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=descripcion]');
	var $select_tipo_productos_busqueda = $('#barra_buscador').find('.tabla_buscador').find('select[name=tipo_productos]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		
		var signo_separador = "=";
		valor_retorno += "codigo" + signo_separador + $sku_busqueda.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $campo_descripcion_busqueda.val() + "|";
		valor_retorno += "tipo" + signo_separador + $select_tipo_productos_busqueda.val() + "|";
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
	
	var input_json_busqueda = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_busqueda,$arreglo,function(data){
		//Llena el select tipos de productos en el buscador
		$select_tipo_productos_busqueda.children().remove();
		var prod_tipos_html = '<option value="0" selected="yes">[-- --]</option>';
		$.each(data['prodTipos'],function(entryIndex,pt){
			prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
		});
		$select_tipo_productos_busqueda.append(prod_tipos_html);
		
		arrayTProd=data['prodTipos'];
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
	
	
	//----------------------------------------------------------------
	//valida la fecha seleccionada
	function mayor(fecha, fecha2){
		var xMes=fecha.substring(5, 7);
		var xDia=fecha.substring(8, 10);
		var xAnio=fecha.substring(0,4);
		var yMes=fecha2.substring(5, 7);
		var yDia=fecha2.substring(8, 10);
		var yAnio=fecha2.substring(0,4);
		
		if (xAnio > yAnio){
			return(true);
		}else{
			if (xAnio == yAnio){
				if (xMes > yMes){
					return(true);
				}
				if (xMes == yMes){
					if (xDia > yDia){
						return(true);
					}else{
						return(false);
					}
				}else{
					return(false);
				}
			}else{
				return(false);
			}
		}
	}
	//muestra la fecha actual
	var mostrarFecha = function mostrarFecha(){
		var ahora = new Date();
		var anoActual = ahora.getFullYear();
		var mesActual = ahora.getMonth();
		mesActual = mesActual+1;
		mesActual = (mesActual <= 9)?"0" + mesActual : mesActual;
		var diaActual = ahora.getDate();
		diaActual = (diaActual <= 9)?"0" + diaActual : diaActual;
		var Fecha = anoActual + "-" + mesActual + "-" + diaActual;		
		return Fecha;
	}
	
	
	
    
	
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-invcapturacostos-window').find('select[name=prodtipo]');
            $('#forma-invcapturacostos-window').find('#submit').mouseover(function(){
                $('#forma-invcapturacostos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                //$('#forma-invcapturacostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
            })
            $('#forma-invcapturacostos-window').find('#submit').mouseout(function(){
                $('#forma-invcapturacostos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                //$('#forma-invcapturacostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
            })
            $('#forma-invcapturacostos-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-invcapturacostos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-invcapturacostos-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-invcapturacostos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-invcapturacostos-window').find('#close').mouseover(function(){
                $('#forma-invcapturacostos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-invcapturacostos-window').find('#close').mouseout(function(){
                $('#forma-invcapturacostos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-invcapturacostos-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-invcapturacostos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-invcapturacostos-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-invcapturacostos-window').find("ul.pestanas li").click(function() {
                $('#forma-invcapturacostos-window').find(".contenidoPes").hide();
                $('#forma-invcapturacostos-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-invcapturacostos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	//funcion para hacer que un campo solo acepte numeros
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
	
	
	
	
	//buscador de productos
	$busca_productos = function(sku_buscar, descripcion){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto($('#forma-invcapturacostos-window').find('input[name=nombre_producto]'));
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscaproducto-window').find('#tabla_resultado');
		
		var $campo_sku = $('#forma-buscaproducto-window').find('input[name=campo_sku]');
		var $select_tipo_producto = $('#forma-buscaproducto-window').find('select[name=tipo_producto]');
		var $campo_descripcion = $('#forma-buscaproducto-window').find('input[name=campo_descripcion]');
		
		//var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('a[href*=busca_producto_modalbox]');
		//var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('a[href*=cencela]');
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

		
		//Llena el select tipos de productos en el buscador
		$select_tipo_producto.children().remove();
		var prod_tipos_html = '<option value="0">[--Seleccionar Tipo--]</option>';
		$.each(arrayTProd,function(entryIndex,pt){
			prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
		});
		$select_tipo_producto.append(prod_tipos_html);
		
		//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
		$select_tipo_producto.val(sku_buscar);
		
		//asignamos la descripcion del producto, si el usuario capturo la descripcion antes de abrir el buscador
		$campo_descripcion.val(descripcion);
		
		$campo_sku.focus();
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorProductos.json';
			$arreglo = {	'sku':$campo_sku.val(),
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
					$('#forma-invcapturacostos-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-invcapturacostos-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-invcapturacostos-window').find('input[name=sku_producto]').focus();
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
			$('#forma-invcapturacostos-window').find('input[name=nombre_producto]').focus();
		});
	}//termina buscador de productos
	
    
	
	
	
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
	
	
	
	
	$aplicar_evento_blur_tc = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if($campo_input.val().trim()==''){
				$campo_input.val(0);
			}
			$campo_input.val(parseFloat($campo_input.val()).toFixed(4));
			
			$campo_input.val($(this).agregar_comas($campo_input.val()));
		});
	}
	
	
	
	$aplicar_evento_focus = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val().trim()=='' || parseInt($(this).val())==0){
				$(this).val('');
			}
			$(this).val(quitar_comas($(this).val()));
		});
	}
	
	
	$aplicar_evento_blur = function( $campo_input, captura_costo_ref ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if($campo_input.val().trim()==''){
				$campo_input.val(0);
			}
			$campo_input.val(parseFloat($campo_input.val()).toFixed(2));
			//$campo_input.val($(this).agregar_comas($campo_input.val()));
			
			if(captura_costo_ref=="true"){
				//Calcular CIT y PMIN
				$tr = $(this).parent().parent();
				//$tr.find('input[name=cit]').val( parseFloat($tr.find('input[name=costo_ultimo]').val()) + parseFloat($tr.find('input[name=ca]').val()) + (parseFloat($tr.find('input[name=costo_ultimo]').val())*(parseFloat($tr.find('input[name=igi]').val())/100)) + (parseFloat($tr.find('input[name=costo_ultimo]').val())*(parseFloat($tr.find('input[name=gi]').val())/100)) );
				$tr.find('input[name=cit]').val(parseFloat($tr.find('input[name=costo_ultimo]').val()) + parseFloat($tr.find('input[name=ca]').val()) + (parseFloat($tr.find('input[name=costo_ultimo]').val())*(parseFloat($tr.find('input[name=igi]').val())/100)) + ((parseFloat($tr.find('input[name=costo_ultimo]').val()) + (parseFloat($tr.find('input[name=costo_ultimo]').val())*(parseFloat($tr.find('input[name=igi]').val())/100)))*(parseFloat($tr.find('input[name=gi]').val())/100)));
				$tr.find('input[name=pmin]').val(parseFloat($tr.find('input[name=cit]').val())/(1 - (parseFloat($tr.find('input[name=margen_pmin]').val())/100)));
				$tr.find('input[name=cit]').val(parseFloat($tr.find('input[name=cit]').val()).toFixed(2));
				$tr.find('input[name=pmin]').val(parseFloat($tr.find('input[name=pmin]').val()).toFixed(2));
			}
		});
	}
	
	//Funcion para aplicar metodo click para eliminar tr
	$aplicar_evento_eliminar = function( $campo_href ){
		//Eliminar un lote
		$campo_href.click(function(e){
			e.preventDefault();
			$tr_padre=$(this).parent().parent();
			//Eliminar el tr
			$tr_padre.remove();
		});
	}
	
	
	
	
	//Generar tr para agregar al grid
	$genera_tr = function(noTr, id_reg, idProd, codigo, descripcion, unidad, costo_ultimo, tc, id_pres, pres, igi, gi, ca, cit, margen_pmin, pmin){
		
		var trr = '';
		trr = '<tr>';
			trr += '<td class="grid" style="border:1px solid #C1DAD7;" width="60">';
				trr += '<a href="#" class="delete'+ noTr +'">Eliminar</a>';
				trr += '<input type="hidden" 	name="idreg" id="idd"  class="idreg'+ noTr +'" value="'+id_reg+'">';//este es el id del registro que ocupa el producto en la tabla detalle
				trr += '<input type="hidden" 	name="notr" class="notr'+ noTr +'" value="'+ noTr +'">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px; border:1px solid #C1DAD7;" width="120">';
				trr += '<input type="hidden" 	name="idprod" id="idprod" value="'+ idProd +'">';
				trr += '<input type="text" 		name="codigo" value="'+ codigo +'" class="borde_oculto" readOnly="true" style="width:116px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px; border:1px solid #C1DAD7;" width="200">';
				trr += '<input type="text" 		name="nombre" 	value="'+ descripcion +'" class="borde_oculto" readOnly="true" style="width:196px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px; border:1px solid #C1DAD7;" width="100">';
				trr += '<input type="text" 		name="unidad" 	value="'+ unidad +'" class="borde_oculto" readOnly="true" style="width:96px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" id="ocultar" style="font-size:11px; border:1px solid #C1DAD7;" width="100">';
				trr += '<input type="hidden" 	name="id_pres" class="id_pres'+ noTr +'" value="'+ id_pres +'">';
				trr += '<input type="text" 		name="presentacion" 	value="'+ pres +'" class="borde_oculto" readOnly="true" style="width:96px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px; border:1px solid #C1DAD7;" width="60">';
				trr += '<select name="selectMon" class="selectMon'+ noTr +'" style="width:56px;"></select>';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px; border:1px solid #C1DAD7;" width="60">';
				trr += '<input type="text" 		name="tc" value="'+ tc +'" class="tc'+noTr+'" style="width:56px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px; border:1px solid #C1DAD7;" width="80">';
				trr += '<input type="text" 		name="costo_ultimo" value="'+ costo_ultimo +'" class="costo_ultimo'+ noTr +'" style="width:76px; text-align:right;">';
			trr += '</td>';
			
			
			trr += '<td class="grid1" id="ocultar" style="font-size:11px; border:1px solid #C1DAD7;" width="60">';
				trr += '<input type="text" 		name="igi" value="'+ igi +'" class="igi'+noTr+'" style="width:56px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" id="ocultar" style="font-size:11px; border:1px solid #C1DAD7;" width="60">';
				trr += '<input type="text" 		name="gi" value="'+ gi +'" class="gi'+noTr+'" style="width:56px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" id="ocultar" style="font-size:11px; border:1px solid #C1DAD7;" width="60">';
				trr += '<input type="text" 		name="ca" value="'+ ca +'" class="ca'+noTr+'" style="width:56px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" id="ocultar" style="font-size:11px; border:1px solid #C1DAD7;" width="70">';
				trr += '<input type="text" 		name="cit" value="'+ cit +'" class="borde_oculto" readOnly="true" style="width:66px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" id="ocultar" style="font-size:11px; border:1px solid #C1DAD7;" width="60">';
				trr += '<input type="text" 		name="margen_pmin" value="'+ margen_pmin +'" class="margen_pmin'+noTr+'" style="width:56px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" id="ocultar" style="font-size:11px; border:1px solid #C1DAD7;" width="70">';
				trr += '<input type="text" 		name="pmin" value="'+ pmin +'" class="borde_oculto" readOnly="true" style="width:66px; text-align:right;">';
			trr += '</td>';
			
		trr += '</tr>';
		
		return trr;
	}
	



	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_datos_producto = function($grid_productos, codigo_producto, captura_costo_ref){
		//verifica si el campo sku no esta vacio para realizar busqueda
		if(codigo_producto != ''){
			
			var encontrado = 0;
			
			//busca el sku y la presentacion en el grid
			$grid_productos.find('tr').each(function (index){
				if( $(this).find('input[name=codigo]').val() == codigo_producto.toUpperCase() ){
					encontrado=1;//el producto ya esta en el grid
				}
			});
			
			
			
			if( parseInt(encontrado)==0 ){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosProducto.json';
				$arreglo = {'sku':codigo_producto,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var trr = '';
				
				$.post(input_json,$arreglo,function(entry){
					//verifica si el arreglo retorno datos
					if (entry['Producto'].length > 0){
						$.each(entry['Producto'],function(entryIndex,data){
							var noTr = $("tr", $grid_productos).size();
							noTr++;
							
							var id_reg = data['id_reg'];
							var idProd = data['id_prod'];
							var codigo = data['codigo'];
							var descripcion = data['descripcion'];
							var unidad = data['unidad'];
							var costo_ultimo = data['costo_ultimo'];
							var tc = data['tc'];
							var idMon = data['idMon'];
							var id_pres = data['id_pres'];
							var pres = data['presentacion'];
							var igi = data['igi'];
							var gi = data['gi'];
							var ca = data['ca'];
							var cit = data['cit'];
							var margen_pmin = data['margen_pmin'];
							var pmin = data['pmin'];
							
							//var cadena_tr = $genera_tr(noTr, id_reg, idProd, codigo, descripcion, unidad, costo_ultimo, tc);
							var cadena_tr = $genera_tr(noTr, id_reg, idProd, codigo, descripcion, unidad, costo_ultimo, tc, id_pres, pres, igi, gi, ca, cit, margen_pmin, pmin);
							
							$grid_productos.append(cadena_tr);
							if(captura_costo_ref=="false"){
								//Ocultar cuando no se incluye costo de referencia
								$grid_productos.find('#ocultar').hide();
							}
							
							$permitir_solo_numeros($grid_productos.find('.costo_ultimo'+noTr));
							$aplicar_evento_keypress($grid_productos.find('.costo_ultimo'+noTr));
							$aplicar_evento_focus($grid_productos.find('.costo_ultimo'+noTr));
							$aplicar_evento_blur($grid_productos.find('.costo_ultimo'+noTr), captura_costo_ref);
							
							$permitir_solo_numeros($grid_productos.find('.tc'+noTr));
							$aplicar_evento_keypress($grid_productos.find('.tc'+noTr));
							$aplicar_evento_focus($grid_productos.find('.tc'+noTr));
							$aplicar_evento_blur_tc($grid_productos.find('.tc'+noTr), 'false');
							$aplicar_evento_eliminar($grid_productos.find('.delete'+noTr));
							
							$permitir_solo_numeros($grid_productos.find('.igi'+noTr));
							$aplicar_evento_keypress($grid_productos.find('.igi'+noTr));
							$aplicar_evento_focus($grid_productos.find('.igi'+noTr));
							$aplicar_evento_blur($grid_productos.find('.igi'+noTr), captura_costo_ref);
							
							$permitir_solo_numeros($grid_productos.find('.gi'+noTr));
							$aplicar_evento_keypress($grid_productos.find('.gi'+noTr));
							$aplicar_evento_focus($grid_productos.find('.gi'+noTr));
							$aplicar_evento_blur($grid_productos.find('.gi'+noTr), captura_costo_ref);
							
							$permitir_solo_numeros($grid_productos.find('.ca'+noTr));
							$aplicar_evento_keypress($grid_productos.find('.ca'+noTr));
							$aplicar_evento_focus($grid_productos.find('.ca'+noTr));
							$aplicar_evento_blur($grid_productos.find('.ca'+noTr), captura_costo_ref);
							
							$permitir_solo_numeros($grid_productos.find('.margen_pmin'+noTr));
							$aplicar_evento_keypress($grid_productos.find('.margen_pmin'+noTr));
							$aplicar_evento_focus($grid_productos.find('.margen_pmin'+noTr));
							$aplicar_evento_blur($grid_productos.find('.margen_pmin'+noTr), captura_costo_ref);
							
							
							
							$grid_productos.find('.selectMon'+noTr).children().remove();
							var mon_hmtl='';
							//mon_hmtl = '<option value="0">[-  -]</option>';
							$.each(arrayMon,function(entryIndex,mon){
								if(parseInt(mon['id'])==parseInt(idMon)){
									mon_hmtl += '<option value="' + mon['id'] + '" selected="yes">' + mon['descripcion_abr'] + '</option>';
								}else{
									mon_hmtl += '<option value="' + mon['id'] + '">' + mon['descripcion_abr'] + '</option>';
								}
							});
							$grid_productos.find('.selectMon'+noTr).append(mon_hmtl);
							
							//Quitar valor de los campos de la busqueda
							$('#forma-invcapturacostos-window').find('input[name=sku_producto]').val('');
							$('#forma-invcapturacostos-window').find('input[name=nombre_producto]').val('');
								
							//Asignar el enfoque al campo costo ultimo
							$grid_productos.find('.costo_ultimo'+noTr).focus();
						});
					}else{
						jAlert('El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.', 'Atencion!', function(r) { 
							$('#forma-invcapturacostos-window').find('input[name=sku_producto]').val('');
							$('#forma-invcapturacostos-window').find('input[name=nombre_producto]').val('');
							$('#forma-invcapturacostos-window').find('input[name=sku_producto]').focus();
						});
					}
				});
			}else{
				jAlert('El producto que intenta Agregar ya se encuentra en el listado.', 'Atencion!', function(r) { 
					$('#forma-invcapturacostos-window').find('input[name=sku_producto]').val('');
					$('#forma-invcapturacostos-window').find('input[name=nombre_producto]').val('');
					$('#forma-invcapturacostos-window').find('input[name=sku_producto]').focus();
				});
			}
		}else{
			jAlert('Es necesario ingresar un C&oacute;digo de producto valido.', 'Atencion!', function(r) { 
				$('#forma-invcapturacostos-window').find('input[name=sku_producto]').val('');
				$('#forma-invcapturacostos-window').find('input[name=nombre_producto]').val('');
				$('#forma-invcapturacostos-window').find('input[name=sku_producto]').focus();
			});
		}
	}//termina buscador de datos del producto
	
    
	
	
	
	
	
	
	
	
	
	//nuevo ajuste
	$nuevo.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_invcapturacostos();
		
		var form_to_show = 'formainvcapturacostos00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-invcapturacostos-window').css({"margin-left": -260, 	"margin-top": -220});
		
		$forma_selected.prependTo('#forma-invcapturacostos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCosto.json';
		$arreglo = {'identificador':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
		var $identificador = $('#forma-invcapturacostos-window').find('input[name=identificador]');
		
		var $sku_producto = $('#forma-invcapturacostos-window').find('input[name=sku_producto]');
		var $nombre_producto = $('#forma-invcapturacostos-window').find('input[name=nombre_producto]');
		
		//buscar producto
		var $busca_sku = $('#forma-invcapturacostos-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-invcapturacostos-window').find('a[href*=agregar_producto]');
		
		//grid de productos
		var $grid_productos = $('#forma-invcapturacostos-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning');
		
		
		var $cerrar_plugin = $('#forma-invcapturacostos-window').find('#close');
		var $cancelar_plugin = $('#forma-invcapturacostos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invcapturacostos-window').find('#submit');
		
		//$campo_factura.css({'background' : '#ffffff'});
		
		//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
		//$descargarpdf.attr('disabled','-1');
		$identificador.val(0);
		
		//$folio.css({'background' : '#F0F0F0'});
		
		
		//Ocultar columnas del Grid
		$('#forma-invcapturacostos-window').find('#ocultar').hide();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los datos se actualizaron con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-invcapturacostos-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				//habilitar boton actualizar
				$submit_actualizar.removeAttr('disabled');
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invcapturacostos-window').find('.invcapturacostos_div_one').css({'height':'480px'});//con errores
				$('#forma-invcapturacostos-window').find('div.interrogacion').css({'display':'none'});
				$grid_productos.find('input[name=costo_ultimo]').css({'background' : '#ffffff'});
				$grid_productos.find('select[name=selectMon]').css({'background' : '#ffffff'});
				$grid_productos.find('input[name=tc]').css({'background' : '#ffffff'});
				
				$('#forma-invcapturacostos-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-invcapturacostos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						var campo = tmp.split(':')[0];
						
						$('#forma-invcapturacostos-window').find('#div_warning_grid').css({'display':'block'});
						var $campo = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
						
						var codigo_producto = $campo.parent().parent().find('input[name=codigo]').val();
						var titulo_producto = $campo.parent().parent().find('input[name=nombre]').val();
						
						var tr_warning = '<tr>';
								tr_warning += '<td width="20"><div><img src="../../img/icono_advertencia.png" align="top" rel="warning_sku"></td>';
								tr_warning += '<td width="120"><input type="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:120px; color:red"></td>';
								tr_warning += '<td width="240"><input type="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:240px; color:red"></td>';
								tr_warning += '<td width="380"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:380px; color:red"></td>';
						tr_warning += '</tr>';
						
						$('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						
					}
				}
				$('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
				$('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
			}
		}
		
		var options = {datatype :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		
		$.post(input_json,$arreglo,function(entry){
			arrayMon = entry['Monedas'];
			
			if(entry['Extra'][0]['captura_costo_ref']=="true"){
				$('#forma-invcapturacostos-window').css({"margin-left": -510, 	"margin-top": -220});
				$('#forma-invcapturacostos-window').find('.invcapturacostos_div_one').css({'width':'1280px'});
				$('#forma-invcapturacostos-window').find('.invcapturacostos_div_two').css({'width':'1280px'});
				$('#forma-invcapturacostos-window').find('#width_content_grid').css({'width':'1240px'});
				$('#forma-invcapturacostos-window').find('#div_botones_footer').css({'width':'1230px'});
				$('#forma-invcapturacostos-window').find('#div_titulo').css({'width':'1240px'});
				$('#forma-invcapturacostos-window').find('#ocultar').show();
			}
			
			
			//Buscador de productos
			$busca_sku.click(function(event){
				event.preventDefault();
				$busca_productos($sku_producto.val(), $nombre_producto.val());
			});
			
			
			//Agregar producto al grid
			$agregar_producto.click(function(event){
				event.preventDefault();
				$buscador_datos_producto($grid_productos, $sku_producto.val(), entry['Extra'][0]['captura_costo_ref']);
			});
			
			
			//Desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
			$(this).aplicarEventoKeypressEjecutaTrigger($sku_producto, $agregar_producto);
			
			
		},"json");//termina llamada json
		
		
		
		

		/*
		//Desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$sku_producto.keypress(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
		*/
		
		
		//deshabilitar tecla enter  en todo el plugin
		$('#forma-invcapturacostos-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert('No hay datos para actualizar.', 'Atencion!', function(r) { 
					$sku_producto.focus();
				});
				return false;
			}
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invcapturacostos-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invcapturacostos-overlay').fadeOut(remove);
		});
		
		//Asignar el enfoque al cargar la ventana
		$sku_producto.focus();
		
	});
	
	
	
	var carga_formainvcapturacostos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'identificador':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  el Costo?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("Costo fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}else{
							jAlert("El costo no pudo ser eliminado.", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-invcapturacostos-window').remove();
			$('#forma-invcapturacostos-overlay').remove();
            
			var form_to_show = 'formainvcapturacostos00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_invcapturacostos();
			
			$('#forma-invcapturacostos-window').css({"margin-left": -260, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-invcapturacostos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCosto.json';
			$arreglo = {'identificador':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
        
			
			var $identificador = $('#forma-invcapturacostos-window').find('input[name=identificador]');
			
			var $sku_producto = $('#forma-invcapturacostos-window').find('input[name=sku_producto]');
			var $nombre_producto = $('#forma-invcapturacostos-window').find('input[name=nombre_producto]');
			
			//buscar producto
			var $busca_sku = $('#forma-invcapturacostos-window').find('a[href*=busca_sku]');
			//href para agregar producto al grid
			var $agregar_producto = $('#forma-invcapturacostos-window').find('a[href*=agregar_producto]');
			
			//grid de productos
			var $grid_productos = $('#forma-invcapturacostos-window').find('#grid_productos');
			//grid de errores
			var $grid_warning = $('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning');
			
			
			var $cerrar_plugin = $('#forma-invcapturacostos-window').find('#close');
			var $cancelar_plugin = $('#forma-invcapturacostos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invcapturacostos-window').find('#submit');
			
			
			//$campo_factura.css({'background' : '#ffffff'});
			
			//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
			//$boton_descargarpdf.hide();
			$identificador.val(0);//para nueva pedido el id es 0
			
			//Ocultar columnas del Grid
			$('#forma-invcapturacostos-window').find('#ocultar').hide();
				
			var respuestaProcesada = function(data){
				if ( data['success'] == "true" ){
					jAlert("Los datos se guardaron con &eacute;xito", 'Atencion!');
					var remove = function() {$(this).remove();};
					$('#forma-invcapturacostos-overlay').fadeOut(remove);
					$get_datos_grid();
				}else{
					//habilitar boton actualizar
					$submit_actualizar.removeAttr('disabled');
					// Desaparece todas las interrogaciones si es que existen
					$('#forma-invcapturacostos-window').find('.invcapturacostos_div_one').css({'height':'480px'});//con errores
					$('#forma-invcapturacostos-window').find('div.interrogacion').css({'display':'none'});
					$grid_productos.find('input[name=costo_ultimo]').css({'background' : '#ffffff'});
					$grid_productos.find('select[name=selectMon]').css({'background' : '#ffffff'});
					$grid_productos.find('input[name=tc]').css({'background' : '#ffffff'});
					
					$('#forma-invcapturacostos-window').find('#div_warning_grid').css({'display':'none'});
					$('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
					
					var valor = data['success'].split('___');
					//muestra las interrogaciones
					for (var element in valor){
						tmp = data['success'].split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
							$('#forma-invcapturacostos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							
							var campo = tmp.split(':')[0];
							
							$('#forma-invcapturacostos-window').find('#div_warning_grid').css({'display':'block'});
							var $campo = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
							
							var codigo_producto = $campo.parent().parent().find('input[name=codigo]').val();
							var titulo_producto = $campo.parent().parent().find('input[name=nombre]').val();
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><img src="../../img/icono_advertencia.png" align="top" rel="warning_sku"></td>';
									tr_warning += '<td width="120"><input type="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:120px; color:red"></td>';
									tr_warning += '<td width="240"><input type="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:240px; color:red"></td>';
									tr_warning += '<td width="380"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:380px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
							
						}
					}
					$('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
					$('#forma-invcapturacostos-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
				}
			
			}		
				var options = {datatype :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					if(entry['Extra'][0]['captura_costo_ref']=="true"){
						$('#forma-invcapturacostos-window').css({"margin-left": -510, 	"margin-top": -220});
						$('#forma-invcapturacostos-window').find('.invcapturacostos_div_one').css({'width':'1280px'});
						$('#forma-invcapturacostos-window').find('.invcapturacostos_div_two').css({'width':'1280px'});
						$('#forma-invcapturacostos-window').find('#width_content_grid').css({'width':'1240px'});
						$('#forma-invcapturacostos-window').find('#div_botones_footer').css({'width':'1230px'});
						$('#forma-invcapturacostos-window').find('#div_titulo').css({'width':'1240px'});
						$('#forma-invcapturacostos-window').find('#ocultar').show();
					}
					
					$identificador.val(entry['Costo']['0']['id']);
					arrayMon = entry['Monedas'];
					
					
					$.each(entry['Costo'],function(entryIndex,data){
						var noTr = $("tr", $grid_productos).size();
						noTr++;
						
						var id_reg = data['id_reg'];
						var idProd = data['id_prod'];
						var codigo = data['codigo'];
						var descripcion = data['descripcion'];
						var unidad = data['unidad'];
						var costo_ultimo = data['costo_ultimo'];
						var tc = data['tc'];
						var idMon = data['idMon'];
						var id_pres = data['id_pres'];
						var pres = data['presentacion'];
						var igi = data['igi'];
						var gi = data['gi'];
						var ca = data['ca'];
						var cit = data['cit'];
						var margen_pmin = data['margen_pmin'];
						var pmin = data['pmin'];
						
						//var cadena_tr = $genera_tr(noTr, id_reg, idProd, codigo, descripcion, unidad, costo_ultimo, tc);
						var cadena_tr = $genera_tr(noTr, id_reg, idProd, codigo, descripcion, unidad, costo_ultimo, tc, id_pres, pres, igi, gi, ca, cit, margen_pmin, pmin);
						
						//Agregar fila a la tabla
						$grid_productos.append(cadena_tr);
						
						if(entry['Extra'][0]['captura_costo_ref']=="false"){
							//Ocultar cuando no se incluye costo de referencia
							$grid_productos.find('#ocultar').hide();
						}
						
						$permitir_solo_numeros($grid_productos.find('.costo_ultimo'+noTr));
						$aplicar_evento_keypress($grid_productos.find('.costo_ultimo'+noTr));
						$aplicar_evento_focus($grid_productos.find('.costo_ultimo'+noTr));
						$aplicar_evento_blur($grid_productos.find('.costo_ultimo'+noTr), entry['Extra'][0]['captura_costo_ref']);
						
						$permitir_solo_numeros($grid_productos.find('.tc'+noTr));
						$aplicar_evento_keypress($grid_productos.find('.tc'+noTr));
						$aplicar_evento_focus($grid_productos.find('.tc'+noTr));
						$aplicar_evento_blur_tc($grid_productos.find('.tc'+noTr), 'false');
						$aplicar_evento_eliminar($grid_productos.find('.delete'+noTr));
						
						$permitir_solo_numeros($grid_productos.find('.igi'+noTr));
						$aplicar_evento_keypress($grid_productos.find('.igi'+noTr));
						$aplicar_evento_focus($grid_productos.find('.igi'+noTr));
						$aplicar_evento_blur($grid_productos.find('.igi'+noTr), entry['Extra'][0]['captura_costo_ref']);
						
						$permitir_solo_numeros($grid_productos.find('.gi'+noTr));
						$aplicar_evento_keypress($grid_productos.find('.gi'+noTr));
						$aplicar_evento_focus($grid_productos.find('.gi'+noTr));
						$aplicar_evento_blur($grid_productos.find('.gi'+noTr), entry['Extra'][0]['captura_costo_ref']);
						
						$permitir_solo_numeros($grid_productos.find('.ca'+noTr));
						$aplicar_evento_keypress($grid_productos.find('.ca'+noTr));
						$aplicar_evento_focus($grid_productos.find('.ca'+noTr));
						$aplicar_evento_blur($grid_productos.find('.ca'+noTr), entry['Extra'][0]['captura_costo_ref']);
						
						$permitir_solo_numeros($grid_productos.find('.margen_pmin'+noTr));
						$aplicar_evento_keypress($grid_productos.find('.margen_pmin'+noTr));
						$aplicar_evento_focus($grid_productos.find('.margen_pmin'+noTr));
						$aplicar_evento_blur($grid_productos.find('.margen_pmin'+noTr), entry['Extra'][0]['captura_costo_ref']);
						
						$grid_productos.find('.selectMon'+noTr).children().remove();
						var mon_hmtl='';
						if(parseInt(idMon)<=0){
							mon_hmtl = '<option value="0">[-  -]</option>';
						}
						
						$.each(entry['Monedas'],function(entryIndex,mon){
							if(parseInt(mon['id'])==parseInt(idMon)){
								mon_hmtl += '<option value="' + mon['id'] + '" selected="yes">' + mon['descripcion_abr'] + '</option>';
							}else{
								mon_hmtl += '<option value="' + mon['id'] + '">' + mon['descripcion_abr'] + '</option>';
							}
						});
						$grid_productos.find('.selectMon'+noTr).append(mon_hmtl);
						
						//Quitar valor de los campos de la busqueda
						$('#forma-invcapturacostos-window').find('input[name=sku_producto]').val('');
						$('#forma-invcapturacostos-window').find('input[name=nombre_producto]').val('');
							
						//Asignar el enfoque al campo costo ultimo
						$grid_productos.find('.costo_ultimo'+noTr).focus();
					});
				});//termina llamada json
                
                
                
                /*
				//Descargar pdf de Ajustes
				$descargarpdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfAjuste/'+$identificador.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
				*/
				
				//buscador de productos
				$busca_sku.click(function(event){
					event.preventDefault();
					$busca_productos($sku_producto.val(), $nombre_producto.val());
				});
				
				
				//agregar producto al grid
				$agregar_producto.click(function(event){
					event.preventDefault();
					$buscador_datos_producto($grid_productos, $sku_producto.val());
				});
				
				
				//Desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
				$(this).aplicarEventoKeypressEjecutaTrigger($sku_producto, $agregar_producto);
				
                
				//deshabilitar tecla enter  en todo el plugin
				$('#forma-invcapturacostos-window').find('input').keypress(function(e){
					if(e.which==13 ) {
						return false;
					}
				});
				
				
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_productos).size();
					if(parseInt(trCount) > 0){
						return true;
					}else{
						jAlert('No hay datos para actualizar.', 'Atencion!', function(r) { 
							$sku_producto.focus();
						});
						return false;
					}
				});
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invcapturacostos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invcapturacostos-overlay').fadeOut(remove);
				});
				
			}
		}
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllCostos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllCostos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvcapturacostos00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



