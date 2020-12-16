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
	var controller = $contextpath.val()+"/controllers/envconf";
    
    //arreglo para Tipos de Productos
    var arrayProdTipos;
    
    //arreglo para Presentaciones de Productos
    var arrayPresentaciones;
	
	
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
	$('#barra_titulo').find('#td_titulo').append('Configuraci&oacute;n de Envasado');
	
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
			var prodtipos = '<option value="0">[--Seleccionar Tipo--]</option>';
			$.each(data['ProdTipos'],function(entryIndex,tp){
				prodtipos += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
				/*
				if(parseInt(tp['id'])==1){
					prodtipos += '<option value="' + tp['id'] + '" selected="yes">' + tp['titulo'] + '</option>';
				}else{
					prodtipos += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
				}
				*/
			});
			$busqueda_select_tipo_prod.append(prodtipos);
			
			//carga select de Presentaciones
			$busqueda_select_pres.children().remove();
			var presentacion = '<option value="0">[-Presentaci&oacute;n--]</option>';
			$.each(data['Presentaciones'],function(entryIndex,pres){
				presentacion += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
			});
			$busqueda_select_pres.append(presentacion);
			
			//asignar valores de presentaciones y tipo de Productos para utilizarlas mas adelante
			arrayProdTipos = data['ProdTipos'];
			arrayPresentaciones = data['Presentaciones'];
		});
	}//termina funcion cargar datos buscador principal
	
	
	//ejecutar la funcion cargar datos al cargar la pagina por primera vez
	$cargar_datos_buscador_principal();
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_codigo.val('');
		$busqueda_descripcion.val('');
		
		//carga select de tipos de producto
		$busqueda_select_tipo_prod.children().remove();
		var prodtipos = '<option value="0">[--Seleccionar Tipo--]</option>';
		$.each(arrayProdTipos,function(entryIndex,tp){
			/*
			if(parseInt(tp['id'])==1){
				prodtipos += '<option value="' + tp['id'] + '" selected="yes">' + tp['titulo'] + '</option>';
			}else{
				prodtipos += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
			}*/
			prodtipos += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
		});
		$busqueda_select_tipo_prod.append(prodtipos);
		
		//carga select de Presentaciones
		$busqueda_select_pres.children().remove();
		var presentacion = '<option value="0">[-Presentaci&oacute;n--]</option>';
		$.each(arrayPresentaciones,function(entryIndex,pres){
			presentacion += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
		});
		$busqueda_select_pres.append(presentacion);
		
		$busqueda_codigo.focus();
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
		$busqueda_codigo.focus();
	});
	
	
	//aplicar evento keypress a campos para ejecutar la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_descripcion, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_pres, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_tipo_prod, $buscar);
	
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-envconf-window').find('#submit').mouseover(function(){
			$('#forma-envconf-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-envconf-window').find('#submit').mouseout(function(){
			$('#forma-envconf-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-envconf-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-envconf-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-envconf-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-envconf-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-envconf-window').find('#close').mouseover(function(){
			$('#forma-envconf-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		
		$('#forma-envconf-window').find('#close').mouseout(function(){
			$('#forma-envconf-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-envconf-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-envconf-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-envconf-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-envconf-window').find("ul.pestanas li").click(function() {
			$('#forma-envconf-window').find(".contenidoPes").hide();
			$('#forma-envconf-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-envconf-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
		
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
	
	
	var $agregarTr = function(id_producto, codigo, descripcion, unidad, cantidad, iddetalle, noDec){
		//grid de productos
		var $grid_productos = $('#forma-envconf-window').find('#grid_productos');
		
		//obtiene numero de trs
		var noTr = $("tr", $grid_productos).size();
		var encontrado = 0;
		
		if(parseInt(noTr)>0){
			//busca el codigo del producto en el grid
			$grid_productos.find('tr').each(function (index){
				if(( $(this).find('input[name=cod]').val() == codigo.toUpperCase() ) && (parseInt($(this).find('input[name=eliminado]').val())!=0)){
					encontrado=1;//el producto ya esta en el grid
				}
			});
		}
		
		noTr++;
		
		if(parseInt(encontrado)<=0){//si el producto no esta en el grid entra aqui
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="58">';
					trr += '<a href="elimina_producto" class="delete'+ noTr +'">Eliminar</a>';
					trr += '<input type="hidden" 	name="eliminado" id="elim" class="elim'+ noTr +'" value="1">';
					trr += '<input type="hidden" 	name="iddetalle" id="idd"  class="idd'+ noTr +'" value="'+iddetalle+'">';//este es el id del registro que ocupa el producto en la tabla detalle
					trr += '<input type="hidden" 	name="notr" value="'+ noTr +'">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="130">';
					trr += '<input type="hidden" 	name="idprod" id="idprod" value="'+ id_producto +'">';
					trr += '<input type="text" 		name="cod" value="'+ codigo +'" id="cod" class="borde_oculto" readOnly="true" style="width:126px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="290">';
					trr += '<input type="text" 		name="desc" value="'+ descripcion +'" id="desc" class="borde_oculto" readOnly="true" style="width:286px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<input type="text" 		name="uni" value="'+ unidad +'" id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" 		name="cant" value="'+ cantidad +'" id="cant" class="cant'+ noTr +'" style="width:76px;" maxlength="10">';
				trr += '</td>';
			trr += '</tr>';
			
			$grid_productos.append(trr);
			
			$permitir_solo_numeros($grid_productos.find('input.cant'+ noTr));
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_productos.find('input.cant'+ noTr).focus(function(e){
				if($(this).val().trim()==''){
					$(this).val('');
				}else{
					if( parseFloat($(this).val())==0 ){
						$(this).val('');
					}
				}
			});
			
			//al perder enfoque el campo cantidad
			$grid_productos.find('input.cant'+ noTr).blur(function(){
				if($(this).val().trim()==''){
					$(this).val(' ');
				}else{
					//validar numero de decimales
					var patron = /^-?[0-9]+([,\.][0-9]{0,0})?$/;
					
					if(parseInt(noDec)==1){
						patron = /^-?[0-9]+([,\.][0-9]{0,1})?$/;
					}
					if(parseInt(noDec)==2){
						patron = /^-?[0-9]+([,\.][0-9]{0,2})?$/;
					}
					if(parseInt(noDec)==3){
						patron = /^-?[0-9]+([,\.][0-9]{0,3})?$/;
					}
					if(parseInt(noDec)==4){
						patron = /^-?[0-9]+([,\.][0-9]{0,4})?$/;
					}
					if(parseInt(noDec)==5){
						patron = /^-?[0-9]+([,\.][0-9]{0,5})?$/;
					}
					if(parseInt(noDec)==6){
						patron = /^-?[0-9]+([,\.][0-9]{0,6})?$/;
					}
					
					if(!patron.test($(this).val())){
						jAlert('El n&uacute;mero de decimales es incorrecto, solo debe ser '+noDec+'.', 'Atencion!', function(r) {
							$grid_productos.find('input.cant'+ noTr).val('');
							$grid_productos.find('input.cant'+ noTr).focus();
						});
					}else{
						$(this).val( parseFloat($(this).val()).toFixed(noDec) );
					}
				}
			});
			
			//elimina un producto del grid
			$grid_productos.find('.delete'+ noTr).bind('click',function(event){
				event.preventDefault();
				if(parseInt($(this).parent().find('#elim').val()) != 0){
					//tomamos el id detalle
					var idDetalle = $(this).parent().find('#idd').val();
					
					//asigna espacios en blanco a todos los input de la fila eliminada
					$(this).parent().parent().find('input').val(' ');
					
					//asigna un 0 al input eliminado como bandera para saber que esta eliminado
					$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
					
					//devolvemos el id detalle para conservar el id eliminado y eliminarlo de la tabla detalle
					$(this).parent().find('#idd').val(idDetalle);
					
					//oculta la fila eliminada
					$(this).parent().parent().hide();
				}
			});
			
			//Limpiar los campos Codigo y Nombre del producto
			$('#forma-envconf-window').find('input[name=codigo_componente]').val('');
			$('#forma-envconf-window').find('input[name=nombre_componente]').val('');
			
			//asignar el enfoque
			$grid_productos.find('input.cant'+ noTr).focus();
			
		}else{
			jAlert('El producto: '+codigo+' ya se encuentra en el listado, seleccione otro diferente.', 'Atencion!', function(r) { 
				$('#forma-envconf-window').find('input[name=codigo_componente]').focus();
			});
		}
	};
	

	
	
	var $agregarDatosProductoSeleccionado = function(id_producto, codigo, descripcion, unidad, arregloPres){
		$('#forma-envconf-window').find('input[name=producto_id]').val(id_producto);
		$('#forma-envconf-window').find('input[name=codigo]').val(codigo);
		$('#forma-envconf-window').find('input[name=descripcion]').val(descripcion);
		$('#forma-envconf-window').find('input[name=unidad]').val(unidad);
		
		if (parseInt(arregloPres.length) > 0){
			$('#forma-envconf-window').find('select[name=select_presentacion]').children().remove();
			var html_pres = '';
			$.each(arregloPres,function(entryIndex,pres){
				html_pres += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
			});
			$('#forma-envconf-window').find('select[name=select_presentacion]').append(html_pres);
		}else{
			$('#forma-envconf-window').find('select[name=select_presentacion]').remove();
			var html_pres = '<option value="0">[-Presentaci&oacute;n--]</option>';
			$('#forma-envconf-window').find('select[name=select_presentacion]').append(html_pres);
		}
		
		//asignar el enfoque
		$('#forma-envconf-window').find('input[name=codigo_componente]').focus();
	}
	
	
	
	
	
	//buscador de productos
	var $buscador_productos = function(tipoBusqueda, codigo, descripcion){
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
		
		
		//Llena el select tipos de productos en el buscador
		$select_tipo_producto.children().remove();
		var prod_tipos_html = '';
		if(parseInt(tipoBusqueda)==1){
			prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
		}
		$.each(arrayProdTipos,function(entryIndex,pt){
			if(parseInt(tipoBusqueda)==1){
				if(parseInt(pt['id'])!=4){
					prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
				}
			}else{
				//filtro para productos componentes
				if(parseInt(pt['id'])==7){
					prod_tipos_html += '<option value="' + pt['id'] + '"  selected="yes">' + pt['titulo'] + '</option>';
				}
			}
		});
		$select_tipo_producto.append(prod_tipos_html);
		
		
		$campo_sku.val(codigo);
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
							trr += '<span class="codigo_prod_buscador">'+producto['sku']+'</span>';
						trr += '</td>';
						trr += '<td width="280"><span class="descripcion_prod_buscador">'+producto['descripcion']+'</span></td>';
						trr += '<td width="90">';
							trr += '<span class="unidad_id" style="display:none;">'+producto['unidad_id']+'</span>';
							trr += '<span class="dec" style="display:none;">'+producto['decimales']+'</span>';
							trr += '<span class="unidad_prod_buscador">'+producto['unidad']+'</span>';
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
					var codigo = $(this).find('span.codigo_prod_buscador').html();
					var descripcion = $(this).find('span.descripcion_prod_buscador').html();
					var unidad = $(this).find('span.unidad_prod_buscador').html();
					var cantidad=0;
					var iddetalle=0;
					var noDec = $(this).find('span.dec').html();
					
					if(parseInt(tipoBusqueda)==1){
						//aqui nos vamos a buscar las presentaciones del Producto seleccionado
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesProducto.json';
						$arreglo = {'id_prod':id_producto,'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
						$.post(input_json,$arreglo,function(entry){
							//llamada a la funcion para agregar los datos del Producto seleccionado
							$agregarDatosProductoSeleccionado(id_producto, codigo, descripcion, unidad, entry['Presentaciones']);
						});
					}else{
						//llamada a la funcion para agregar tr al Grid
						$agregarTr(id_producto, codigo, descripcion, unidad, cantidad, iddetalle, noDec);
					}
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
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
			if(parseInt(tipoBusqueda)==1){
				$('#forma-envconf-window').find('input[name=codigo]').focus();
			}else{
				$('#forma-envconf-window').find('input[name=codigo_componente]').focus();
			}
		});
	}//termina buscador de productos
	
	        
	//nuevo 
	$new_invprodlineas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_modalboxenvconf();
		
		var form_to_show = 'formaenvconf00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-envconf-window').css({"margin-left": -390, 	"margin-top": -210});
		$forma_selected.prependTo('#forma-envconf-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $identificador = $('#forma-envconf-window').find('input[name=identificador]');
		var $producto_id = $('#forma-envconf-window').find('input[name=producto_id]');
		var $codigo = $('#forma-envconf-window').find('input[name=codigo]');
		var $descripcion = $('#forma-envconf-window').find('input[name=descripcion]');
		var $unidad = $('#forma-envconf-window').find('input[name=unidad]');
		var $select_presentacion = $('#forma-envconf-window').find('select[name=select_presentacion]');
		
		//href para Agregar y Buscar producto
		var $agregar_producto = $('#forma-envconf-window').find('#agregar_producto');
		var $buscar_producto = $('#forma-envconf-window').find('#buscar_producto');
		
		//Codigo y Nombre del producto componente del Envase
		var $codigo_componente = $('#forma-envconf-window').find('input[name=codigo_componente]');
		var $nombre_componente = $('#forma-envconf-window').find('input[name=nombre_componente]');
		
		//href para Agregar y Buscar producto Elemento del Envase
		var $agregar_producto_componente = $('#forma-envconf-window').find('a[href=agregar_producto_componente]');
		var $buscar_producto_componente = $('#forma-envconf-window').find('a[href=buscar_producto_componente]');
		
		//grid de productos
		var $grid_productos = $('#forma-envconf-window').find('#grid_productos');
		
		//grid de errores
		var $grid_warning = $('#forma-envconf-window').find('#div_warning_grid').find('#grid_warning');
		
		
		var $cerrar_plugin = $('#forma-envconf-window').find('#close');
		var $cancelar_plugin = $('#forma-envconf-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-envconf-window').find('#submit');
		
		$identificador.attr({'value' : 0});
		$producto_id.attr({'value' : 0});
		$unidad.css({'background' : '#F0F0F0'});
		
		
		
		//quitar enter a todos los campos input
		$('#forma-envconf-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		
		//asignar el enfoque al cargar la ventana
		$codigo.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == 'true' ){
				var remove = function() {$(this).remove();};
				$('#forma-envconf-overlay').fadeOut(remove);
				jAlert("Los datos de la configuraci&oacute;n se guardaron con &eacute;xito.", 'Atencion!');
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-envconf-window').find('div.interrogacion').css({'display':'none'});
				$('#forma-envconf-window').find('.envconf_div_one').css({'height':'390px'});//sin errores
				
				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});
				
				$('#forma-envconf-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-envconf-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-envconf-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						var campo = tmp.split(':')[0];
						var $campo_input;
						var cantidad_existencia=0;
						var  width_td=0;
						
						if(tmp.split(':')[0].substring(0,4) == 'cant'){
							
							$('#forma-envconf-window').find('#div_warning_grid').css({'display':'block'});
							$('#forma-envconf-window').find('.envconf_div_one').css({'height':'490px'});//con errores
							$campo_input = $grid_productos.find('.'+campo);
							$campo_input.css({'background' : '#d41000'});
							
							var codigo_producto = $campo_input.parent().parent().find('input[name=cod]').val();
							var titulo_producto = $campo_input.parent().parent().find('input[name=desc]').val();
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><img src="../../img/icono_advertencia.png" align="top" rel="warning_sku"></td>';
									tr_warning += '<td width="90"><input type="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:88px; color:red"></td>';
									tr_warning += '<td width="160"><input type="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:160px; color:red"></td>';
									tr_warning += '<td width="380"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:380px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-envconf-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
					}
				}
				
				$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
				$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		
		/*
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getConf.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			
		});//termina llamada json
		*/
		
		$buscar_producto.click(function(event){
			event.preventDefault();
			//tipo=1 para buscar el producto Principal
			var tipoBusqueda=1;
			$buscador_productos(tipoBusqueda, $codigo.val(), $descripcion.val() );
		});
		
		
		$agregar_producto.click(function(event){
			event.preventDefault();
			var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/gatDatosProducto.json';
			var $arreglo2 = {'codigo':$codigo.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
			
			$.post(input_json2,$arreglo2,function(entry2){
				if(parseInt(entry2['Producto'].length) > 0 ){
					var id_producto = entry2['Producto'][0]['id'];
					var codigo = entry2['Producto'][0]['sku'];
					var descripcion = entry2['Producto'][0]['descripcion'];
					var unidad = entry2['Producto'][0]['unidad'];
					
					//llamada a la funcion para agregar datos del producto
					$agregarDatosProductoSeleccionado(id_producto, codigo, descripcion, unidad, entry2['Presentaciones']);
				}else{
					jAlert('C&oacute;digo de Producto desconocido.', 'Atencion!', function(r) { 
						$codigo.focus(); 
					});
				}
			});
		});
		
		
		$codigo.keypress(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
		
		
		//aplicar evento click para que al pulsar Enter sobre el campo Descripcion de la busqueda del producto Principal, se ejecute el buscador
		$(this).aplicarEventoKeypressEjecutaTrigger($descripcion, $buscar_producto);
		
		
		//Buscar Productos Componentes
		$buscar_producto_componente.click(function(event){
			event.preventDefault();
			//tipo=2 para buscar los productos Componentes
			var tipoBusqueda=2;
			$buscador_productos(tipoBusqueda, $codigo_componente.val(), $nombre_componente.val() );
		});
		
		$agregar_producto_componente.click(function(event){
			event.preventDefault();
			var input_json3 = document.location.protocol + '//' + document.location.host + '/'+controller+'/gatDatosProducto.json';
			var $arreglo3 = {'codigo':$codigo_componente.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
			
			$.post(input_json3,$arreglo3,function(entry3){
				if(parseInt(entry3['Producto'].length) > 0 ){
					var id_producto = entry3['Producto'][0]['id'];
					var codigo = entry3['Producto'][0]['sku'];
					var descripcion = entry3['Producto'][0]['descripcion'];
					var unidad = entry3['Producto'][0]['unidad'];
					var cantidad=0;
					var iddetalle=0;
					var noDec = entry3['Producto'][0]['decimales'];
					
					//llamada a la funcion para agregar tr al grid
					$agregarTr(id_producto, codigo, descripcion, unidad, cantidad, iddetalle, noDec);
					
				}else{
					jAlert('C&oacute;digo de Producto desconocido.', 'Atencion!', function(r) { 
						$codigo_componente.focus(); 
					});
				}
			});
		});
		
		
		//agrega productos componentes al grid
		$codigo_componente.keypress(function(e){
			if(e.which == 13){
				$agregar_producto_componente.trigger('click');
				return false;
			}
		});
		
		//aplicar evento click para que al pulsar Enter sobre el campo Descripcion de la busqueda del producto componente, se ejecute el buscador
		$(this).aplicarEventoKeypressEjecutaTrigger($nombre_componente, $buscar_producto_componente);
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert('Es necesario agregar productos en el listado.', 'Atencion!', function(r) { $codigo_componente.focus(); });
				return false;
			}
		});
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-envconf-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-envconf-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
	});
	
	
	
	var carga_formaenvconf00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar configuraci&oacute;n seleccionada?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La configuraci&oacute;n  fue eliminada exitosamente.", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La configuraci&oacute;n no pudo ser eliminada.", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaenvconf00';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_modalboxenvconf();
			$('#forma-envconf-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-envconf-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
		
			var $identificador = $('#forma-envconf-window').find('input[name=identificador]');
			var $producto_id = $('#forma-envconf-window').find('input[name=producto_id]');
			var $codigo = $('#forma-envconf-window').find('input[name=codigo]');
			var $descripcion = $('#forma-envconf-window').find('input[name=descripcion]');
			var $unidad = $('#forma-envconf-window').find('input[name=unidad]');
			var $select_presentacion = $('#forma-envconf-window').find('select[name=select_presentacion]');
			
			//href para Agregar y Buscar producto
			var $agregar_producto = $('#forma-envconf-window').find('#agregar_producto');
			var $buscar_producto = $('#forma-envconf-window').find('#buscar_producto');
			
			//Codigo y Nombre del producto componente del Envase
			var $codigo_componente = $('#forma-envconf-window').find('input[name=codigo_componente]');
			var $nombre_componente = $('#forma-envconf-window').find('input[name=nombre_componente]');
			
			//href para Agregar y Buscar producto Elemento del Envase
			var $agregar_producto_componente = $('#forma-envconf-window').find('a[href=agregar_producto_componente]');
			var $buscar_producto_componente = $('#forma-envconf-window').find('a[href=buscar_producto_componente]');
			
			//grid de productos
			var $grid_productos = $('#forma-envconf-window').find('#grid_productos');
			
			//grid de errores
			var $grid_warning = $('#forma-envconf-window').find('#div_warning_grid').find('#grid_warning');
			
			
			var $cerrar_plugin = $('#forma-envconf-window').find('#close');
			var $cancelar_plugin = $('#forma-envconf-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-envconf-window').find('#submit');
			
			$agregar_producto.hide();
			$buscar_producto.hide();
			
			$unidad.css({'background' : '#F0F0F0'});
			$codigo.css({'background' : '#F0F0F0'});
			$descripcion.css({'background' : '#F0F0F0'});
			$codigo.attr('readonly',true);
			$descripcion.attr('readonly',true);
			
			//quitar enter a todos los campos input
			$('#forma-envconf-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getConf.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-envconf-overlay').fadeOut(remove);
						jAlert("Los datos de la configuraci&oacute;n se han actualizado con &eacute;xito.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-envconf-window').find('div.interrogacion').css({'display':'none'});
						$('#forma-envconf-window').find('.envconf_div_one').css({'height':'390px'});//sin errores
								
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-envconf-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-envconf-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-envconf-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								
								var campo = tmp.split(':')[0];
								var $campo_input;
								var cantidad_existencia=0;
								var  width_td=0;
								
								if(tmp.split(':')[0].substring(0,4) == 'cant'){
									
									$('#forma-envconf-window').find('#div_warning_grid').css({'display':'block'});
									$('#forma-envconf-window').find('.envconf_div_one').css({'height':'490px'});//con errores
									
									$campo_input = $grid_productos.find('.'+campo);
									$campo_input.css({'background' : '#d41000'});
									
									var codigo_producto = $campo_input.parent().parent().find('input[name=cod]').val();
									var titulo_producto = $campo_input.parent().parent().find('input[name=desc]').val();
									
									var tr_warning = '<tr>';
											tr_warning += '<td width="20"><div><img src="../../img/icono_advertencia.png" align="top" rel="warning_sku"></td>';
											tr_warning += '<td width="90"><input type="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:88px; color:red"></td>';
											tr_warning += '<td width="160"><input type="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:160px; color:red"></td>';
											tr_warning += '<td width="380"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:380px; color:red"></td>';
									tr_warning += '</tr>';
									
									$('#forma-envconf-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								}
							}
						}
						
						$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
						$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
						
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.attr({'value' : entry['Datos']['0']['id']});
					$producto_id.attr({'value' : entry['Datos']['0']['producto_id']});
					$codigo.attr({'value' : entry['Datos']['0']['codigo']});
					$descripcion.attr({'value' : entry['Datos']['0']['descripcion']});
					$unidad.attr({'value' : entry['Datos']['0']['unidad']});
					
					$select_presentacion.children().remove();
					var html_pres = '';
					$.each(entry['Presentaciones'],function(entryIndex,pres){
						if(parseInt(entry['Datos']['0']['presentacion_id']) == parseInt(pres['id'] )){
							html_pres += '<option value="' + pres['id'] + '" selected="yes">' + pres['titulo'] + '</option>';
						}else{
							html_pres += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
						}
					});
					$select_presentacion.append(html_pres);							
					
					
					//verificar que el arreglo traiga datos
					if(parseInt(entry['DatosGrid'].length) > 0){
						$.each(entry['DatosGrid'],function(entryIndex,prod){
							var id_producto = prod['id_prod'];
							var codigo = prod['codigo'];
							var descripcion = prod['descripcion'];
							var unidad = prod['unidad'];
							var cantidad = prod['cant'];
							var iddetalle = prod['iddet'];
							var noDec = prod['precision'];
							
							//llamada a la funcion para agregar tr al grid
							$agregarTr(id_producto, codigo, descripcion, unidad, cantidad, iddetalle, noDec);
						});
					}
				},"json");//termina llamada json
				
				
				
				//Buscar Productos Componentes
				$buscar_producto_componente.click(function(event){
					event.preventDefault();
					//tipo=2 para buscar los productos Componentes
					var tipoBusqueda=2;
					$buscador_productos(tipoBusqueda, $codigo_componente.val(), $nombre_componente.val() );
				});
				
				$agregar_producto_componente.click(function(event){
					event.preventDefault();
					var input_json3 = document.location.protocol + '//' + document.location.host + '/'+controller+'/gatDatosProducto.json';
					var $arreglo3 = {'codigo':$codigo_componente.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					
					$.post(input_json3,$arreglo3,function(entry3){
						if(parseInt(entry3['Producto'].length) > 0 ){
							var id_producto = entry3['Producto'][0]['id'];
							var codigo = entry3['Producto'][0]['sku'];
							var descripcion = entry3['Producto'][0]['descripcion'];
							var unidad = entry3['Producto'][0]['unidad'];
							var cantidad=0;
							var iddetalle=0;
							var noDec = entry3['Producto'][0]['decimales'];
							
							//llamada a la funcion para agregar tr al grid
							$agregarTr(id_producto, codigo, descripcion, unidad, cantidad, iddetalle, noDec);
							
						}else{
							jAlert('C&oacute;digo de Producto desconocido.', 'Atencion!', function(r) { 
								$codigo_componente.focus(); 
							});
						}
					});
				});
				
				
				//agrega productos componentes al grid
				$codigo_componente.keypress(function(e){
					if(e.which == 13){
						$agregar_producto_componente.trigger('click');
						return false;
					}
				});
				
				//aplicar evento click para que al pulsar Enter sobre el campo Descripcion de la busqueda del producto componente, se ejecute el buscador
				$(this).aplicarEventoKeypressEjecutaTrigger($nombre_componente, $buscar_producto_componente);
				
				$codigo_componente.focus();
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-envconf-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-envconf-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllConf.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllConf.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaenvconf00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



