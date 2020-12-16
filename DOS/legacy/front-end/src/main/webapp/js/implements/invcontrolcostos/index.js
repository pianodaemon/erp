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
	var controller = $contextpath.val()+"/controllers/invcontrolcostos";
	
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Control de Costos');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_select_tipo_prod = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_tipo_prod]');
	var $busqueda_select_familia = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_familia]');
	var $busqueda_select_subfamilia = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_subfamilia]');
	var $busqueda_select_marca = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_marca]');
	var $busqueda_select_presentacion = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_presentacion]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_select_ano = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_ano]');
	var $busqueda_select_mes = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_mes]');
	
	var $campo_busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $campo_busqueda_oc = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_oc]');
	
	//este arreglo carga el select de meses
	var array_meses = { 0:"- Mes -",  1:"Enero",  2:"Febrero", 3:"Marzo", 4:"Abril", 5:"Mayo", 6:"Junio", 7:"Julio", 8:"Agosto", 9:"Septiembre", 10:"Octubre", 11:"Noviembre", 12:"Diciembre"};
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "tipo_producto" + signo_separador + $busqueda_select_tipo_prod.val() + "|";
		valor_retorno += "familia" + signo_separador + $busqueda_select_familia.val() + "|";
		valor_retorno += "subfamilia" + signo_separador + $busqueda_select_subfamilia.val() + "|";
		valor_retorno += "marca" + signo_separador + $busqueda_select_marca.val()+ "|";
		valor_retorno += "presentacion" + signo_separador + $busqueda_select_presentacion.val() + "|";
		valor_retorno += "producto" + signo_separador + $busqueda_producto.val() + "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "ano" + signo_separador + $busqueda_select_ano.val() + "|";
		valor_retorno += "mes" + signo_separador + $busqueda_select_mes.val();
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
	
	
	
	//desencadena evento del $campo_ejecutar al pulsar Enter en $campo
	$aplicar_evento_keypress = function($campo, $campo_ejecutar){
		$campo.keypress(function(e){
			if(e.which == 13){
				$campo_ejecutar.trigger('click');
				return false;
			}
		});
	}
	
	$aplicar_evento_keypress($busqueda_select_tipo_prod, $buscar);
	$aplicar_evento_keypress($busqueda_select_familia, $buscar);
	$aplicar_evento_keypress($busqueda_select_subfamilia, $buscar);
	$aplicar_evento_keypress($busqueda_select_marca, $buscar);
	$aplicar_evento_keypress($busqueda_select_presentacion, $buscar);
	$aplicar_evento_keypress($busqueda_producto, $buscar);
	$aplicar_evento_keypress($busqueda_codigo, $buscar);
	$aplicar_evento_keypress($busqueda_select_ano, $buscar);
	$aplicar_evento_keypress($busqueda_select_mes, $buscar);
	$aplicar_evento_keypress($campo_busqueda_folio, $buscar);
	$aplicar_evento_keypress($campo_busqueda_oc, $buscar);
	
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
		
		$busqueda_select_tipo_prod.focus();
	});
	
	
	
	$cargar_datos_buscador_principal= function(){
		var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosBuscadorPrincipal.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_lineas,$arreglo,function(data){
			$busqueda_producto.val('');
			$busqueda_codigo.val('');
		
			//carga select de tipos de producto
			$busqueda_select_tipo_prod.children().remove();
			//var prodtipos = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			var prodtipos = '';
			$.each(data['ProdTipos'],function(entryIndex,tp){
				if(parseInt(tp['id'])==1){
					prodtipos += '<option value="' + tp['id'] + '" selected="yes">' + tp['titulo'] + '</option>';
				}else{
					if(parseInt(tp['id'])!=3 && parseInt(tp['id'])!=4){
						prodtipos += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
					}
				}
			});
			$busqueda_select_tipo_prod.append(prodtipos);
			
			//carga select de Marcas
			$busqueda_select_marca.children().remove();
			var marca = '<option value="0" selected="yes"> -Marca- </option>';
			$.each(data['Marcas'],function(entryIndex,mar){
				marca += '<option value="' + mar['id'] + '"  >' + mar['titulo'] + '</option>';
			});
			$busqueda_select_marca.append(marca);
			
			//carga select de familas
			$busqueda_select_familia.children().remove();
			var familia = '<option value="0">--Familia--</option>';
			$.each(data['Familias'],function(entryIndex,fam){
				familia += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
			});
			$busqueda_select_familia.append(familia);
			
			//Alimentando select de SubFamilias
			$busqueda_select_subfamilia.children().remove();
			var subfamilia = '<option value="0">--SubFamilia--</option>';
			$busqueda_select_subfamilia.append(subfamilia);
			
			//carga select de Presentaciones
			$busqueda_select_presentacion.children().remove();
			var presentacion = '<option value="0">-Presentaci&oacute;n-</option>';
			$.each(data['Presentaciones'],function(entryIndex,pres){
				if(parseInt(pres['id'])==1){
					presentacion += '<option value="' + pres['id'] + '" selected="yes">' + pres['titulo'] + '</option>';
				}else{
					presentacion += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
				}
			});
			$busqueda_select_presentacion.append(presentacion);
			
			
			//carga select de años
			$busqueda_select_ano.children().remove();
			var html_anio = '';
			$.each(data['Anios'],function(entryIndex,anio){
				html_anio += '<option value="' + anio['valor'] + '"  >' + anio['valor'] + '</option>';
			});
			$busqueda_select_ano.append(html_anio);
			
			//cargar select del Mes
			$busqueda_select_mes.children().remove();
			var select_html = '';
			for(var i in array_meses){
				select_html += '<option value="' + i + '"  >' + array_meses[i] + '</option>';	
			}
			$busqueda_select_mes.append(select_html);
		
			$busqueda_select_tipo_prod.change(function(){
				var input_json1 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFamiliasByTipoProd.json';
				$arreglo1 = { 'tipo_prod':$busqueda_select_tipo_prod.val(), 'iu': $('#lienzo_recalculable').find('input[name=iu]').val() };
				$.post(input_json1,$arreglo1,function(data1){
					$busqueda_select_familia.children().remove();
					familia_hmtl = '<option value="0">--Familia--</option>';
					$.each(data1['Familias'],function(entryIndex,fam){
						familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
					});
					$busqueda_select_familia.append(familia_hmtl);
				});
			});
			
			$busqueda_select_familia.change(function(){
				var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSubFamiliasByFamProd.json';
				$arreglo2 = {'fam':$busqueda_select_familia.val(),'iu': $('#lienzo_recalculable').find('input[name=iu]').val() };
				$.post(input_json2,$arreglo2,function(data2){
					//Alimentando select de Subfamilias
					$busqueda_select_subfamilia.children().remove();
					var subfamilia_hmtl = '<option value="0">--Subfmilia--</option>';
					$.each(data2['SubFamilias'],function(dataIndex,subfam){
						subfamilia_hmtl += '<option value="' + subfam['id'] + '"  >' + subfam['titulo'] + '</option>';
					});
					$busqueda_select_subfamilia.append(subfamilia_hmtl);
				});
			});
				
		});
	}//termina funcion cargar datos buscador principal
	
	
	//ejecutar la funcion cargar datos al cargar la pagina por primera vez
	$cargar_datos_buscador_principal();
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		//ejecutar la funcion cargar datos al hacer click en Limpiar
		$cargar_datos_buscador_principal();
		$busqueda_select_tipo_prod.focus();
	});
	
	$busqueda_select_tipo_prod.focus();
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-invcontrolcostos-window').find('select[name=prodtipo]');
		$('#forma-invcontrolcostos-window').find('#submit').mouseover(function(){
			$('#forma-invcontrolcostos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-invcontrolcostos-window').find('#submit').mouseout(function(){
			$('#forma-invcontrolcostos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		
		$('#forma-invcontrolcostos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invcontrolcostos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-invcontrolcostos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invcontrolcostos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invcontrolcostos-window').find('#close').mouseover(function(){
			$('#forma-invcontrolcostos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invcontrolcostos-window').find('#close').mouseout(function(){
			$('#forma-invcontrolcostos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-invcontrolcostos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invcontrolcostos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invcontrolcostos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invcontrolcostos-window').find("ul.pestanas li").click(function() {
			$('#forma-invcontrolcostos-window').find(".contenidoPes").hide();
			$('#forma-invcontrolcostos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invcontrolcostos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
        
        
        
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
	//----------------------------------------------------------------
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
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
	
	
	
	$aplicar_evento_focus = function( $campo_input ){
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo_input.focus(function(e){
			if(parseFloat($campo_input.val())<1){
				$campo_input.val('');
			}
		});
	}
	
	
	$aplicar_evento_blur = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if(parseFloat($campo_input.val())==0||$campo_input.val()==""){
				$campo_input.val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
	}
	
	

	
	
	$aplicar_evento_click_input_lote = function( $campo_input ){
		//validar campo cantidad recibida, solo acepte numeros y punto
		$campo_input.dblclick(function(e){
			$(this).select();
		});
	}
	
	
	
	
	
	
	
	//buscador de productos
	$busca_productos = function(producto, codigo, tipo_prod, marca, familia, subfamilia ){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
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
		
		$select_tipo_producto.hide();
		
		/*
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
		* */
		
		$campo_sku.val(codigo);
		$campo_descripcion.val(producto);
		$campo_sku.focus();
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorProductos.json';
			$arreglo = {    'tipo':tipo_prod,
							'marca':marca,
							'familia':familia,
							'subfamilia':subfamilia,
							'sku':$campo_sku.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['productos'],function(entryIndex,producto){
					trr = '<tr>';
						trr += '<td width="110">';
							trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
							trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
						trr += '</td>';
						trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
						trr += '<td width="90"><span class="unidad_prod_buscador">'+producto['unidad']+'</span></td>';
						trr += '<td width="100"><span class="tipo_prod_buscador">'+producto['tipo']+'</span></td>';
					trr += '</tr>';
					$tabla_resultados.append(trr);
				});
				$tabla_resultados.find('tr:odd').find('td').css({ 'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({ 'background-color' : '#FFFFFF'});
				
				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({ background : '#FBD850'});
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
					$('#forma-invcontrolcostos-window').find('input[name=id_producto]').val($(this).find('#id_prod_buscador').val());
					$('#forma-invcontrolcostos-window').find('input[name=producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-invcontrolcostos-window').find('input[name=producto]').focus();
				});
			});
		});
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_descripcion.val()!='' || $campo_sku.val()!=''){
			$buscar_plugin_producto.trigger('click');
		}
		
		$aplicar_evento_keypress($campo_descripcion, $buscar_plugin_producto);
		$aplicar_evento_keypress($campo_sku, $buscar_plugin_producto);
		$aplicar_evento_keypress($select_tipo_producto, $buscar_plugin_producto);
		
		
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproducto-overlay').fadeOut(remove);
			$('#forma-invcontrolcostos-window').find('input[name=codigo]').focus();
		});
	}//termina buscador de productos

	
	
	
	
    
	
	//funcion que genera tr para agregar 
	$genera_tr = function(noTr, producto_id, codigo, descripcion, unidad, presentacion, orden_compra, factura_prov, moneda, costo, tipo_cambio, costo_importacion, costo_directo, costo_referencia, precio_minimo, costo_adic, moneda_pm ){
		var tr_prod='';
			tr_prod += '<tr>';
			tr_prod += '<td width="80" class="grid" style="font-size: 11px; border:1px solid #C1DAD7; text-align:left;">';
				tr_prod += '<input type="hidden" name="no_tr" id="notr" value="'+ noTr +'">';
				tr_prod += '<input type="hidden" name="id_prod" id="idprod" value="'+  producto_id +'">';
				tr_prod += codigo;
			tr_prod += '</td>';
			tr_prod += '<td width="147" class="grid" align="right" style="font-size:11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="desc" value="'+descripcion+'" id="desc'+ noTr +'" class="borde_oculto" style="width:145px;" readOnly="true">';
			tr_prod += '</td>';
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="unidad" class="borde_oculto" value="'+unidad+'" readOnly="true" style="width:66px;">';
			tr_prod += '</td>';
			tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:left;">';
				tr_prod += '<input type="hidden" name="id_pres" id="idpres" value="0">'+ presentacion +'</td>';
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:left;">'+ orden_compra +'</td>';
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:left;">'+ factura_prov +'</td>';
			tr_prod += '<td width="45" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">'+ moneda +'</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">'+ tipo_cambio +'</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">'+ costo +'</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">'+ costo_importacion +'</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">'+ costo_directo +'</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">'+ costo_adic +'</td>';
			tr_prod += '<td width="60" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">'+ costo_referencia +'</td>';
			tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7; text-align:right;">'+ precio_minimo +'</td>';
			tr_prod += '<td width="45" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">'+ moneda_pm +'</td>';
		tr_prod += '</tr>';
		
		return tr_prod;
	}
	
	
	
	
	
	
	//Aquí entra nuevo
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_invcontrolcostos();
		
		var form_to_show = 'formainvcontrolcostos00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-invcontrolcostos-window').css({ "margin-left": -470, 	"margin-top": -230 });
		
		$forma_selected.prependTo('#forma-invcontrolcostos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosCalculoCosto.json';
		$arreglo = {'identificador':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		var $identificador = $('#forma-invcontrolcostos-window').find('input[name=identificador]');
		var $folio = $('#forma-invcontrolcostos-window').find('input[name=folio]');
		var $id_producto = $('#forma-invcontrolcostos-window').find('input[name=id_producto]');
		var $codigo = $('#forma-invcontrolcostos-window').find('input[name=codigo]');
		var $producto = $('#forma-invcontrolcostos-window').find('input[name=producto]');
		var $buscar_producto = $('#forma-invcontrolcostos-window').find('#buscar_producto');
		
		var $select_tipo_prod = $('#forma-invcontrolcostos-window').find('select[name=select_tipo_prod]');
		var $select_marca = $('#forma-invcontrolcostos-window').find('select[name=select_marca]');
		var $select_familia = $('#forma-invcontrolcostos-window').find('select[name=select_familia]');
		var $select_subfamilia = $('#forma-invcontrolcostos-window').find('select[name=select_subfamilia]');
		var $select_presentacion = $('#forma-invcontrolcostos-window').find('select[name=select_presentacion]');
		var $tipo_cambio = $('#forma-invcontrolcostos-window').find('input[name=tipo_cambio]');
		
		var $radio_costo_ultimo = $('#forma-invcontrolcostos-window').find('.radio_costo_ultimo');
		var $radio_costo_promedio = $('#forma-invcontrolcostos-window').find('.radio_costo_promedio');
		
		var $costo_importacion = $('#forma-invcontrolcostos-window').find('input[name=costo_importacion]');
		var $costo_directo = $('#forma-invcontrolcostos-window').find('input[name=costo_directo]');
		var $precio_minimo = $('#forma-invcontrolcostos-window').find('input[name=precio_minimo]');
		var $costo_adic = $('#forma-invcontrolcostos-window').find('input[name=costo_adic]');
		
		var $check_simulacion = $('#forma-invcontrolcostos-window').find('input[name=check_simulacion]');
		var $calculo_simulacion = $('#forma-invcontrolcostos-window').find('input[name=calculo_simulacion]');
		
		
		var $busqueda = $('#forma-invcontrolcostos-window').find('#busqueda');
		var $pdf = $('#forma-invcontrolcostos-window').find('#pdf');
		var $excel = $('#forma-invcontrolcostos-window').find('#excel');
		var $aplicar = $('#forma-invcontrolcostos-window').find('#aplicar');
		
		//tabla contenedor del listado de productos
		var $grid_productos = $('#forma-invcontrolcostos-window').find('#grid_productos');
		var $etiqueta_encabezado_tipo_costo = $('#forma-invcontrolcostos-window').find('#tipo_costo');
		
		
		
		var $cerrar_plugin = $('#forma-invcontrolcostos-window').find('#close');
		var $cancelar_plugin = $('#forma-invcontrolcostos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invcontrolcostos-window').find('#submit');
		
		
		//quitar enter a todos los campos input
		$('#forma-invcontrolcostos-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$radio_costo_ultimo.attr('checked',  true );
		$costo_importacion.val(parseFloat(0).toFixed(2));
		$costo_directo.val(parseFloat(0).toFixed(2));
		$precio_minimo.val(parseFloat(0).toFixed(2));
		$costo_adic.val(parseFloat(0).toFixed(2));
		$tipo_cambio.val(parseFloat(0).toFixed(4));
		
		$permitir_solo_numeros($costo_importacion);
		$permitir_solo_numeros($costo_directo);
		$permitir_solo_numeros($precio_minimo);
		$permitir_solo_numeros($costo_adic);
		$permitir_solo_numeros($tipo_cambio);
		
		$aplicar_evento_focus( $costo_importacion );
		$aplicar_evento_focus( $costo_directo );
		$aplicar_evento_focus( $precio_minimo );
		$aplicar_evento_focus( $costo_adic );
		$aplicar_evento_focus( $tipo_cambio );
		
		$aplicar_evento_blur( $costo_importacion );
		$aplicar_evento_blur( $costo_directo );
		$aplicar_evento_blur( $precio_minimo );
		$aplicar_evento_blur( $costo_adic );
		//$aplicar_evento_blur( $tipo_cambio );
		
		$tipo_cambio.css({'background' : '#F0F0F0'});
		$tipo_cambio.attr('readonly',true);
		//$pdf
		$excel.hide();//oculto por lo pronto mientras no se utiliza
		$calculo_simulacion.val(0);
		$select_tipo_prod.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los datos de guardaron con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-invcontrolcostos-overlay').fadeOut(remove);
				$pdf.trigger('click');
				//$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-invcontrolcostos-window').find('.div_one').css({'height':'545px'});//sin errores
				$('#forma-invcontrolcostos-window').find('.invcontrolcostos_div_one').css({'height':'578px'});//con errores
				$('#forma-invcontrolcostos-window').find('div.interrogacion').css({'display':'none'});
				
				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});
				
				$('#forma-invcontrolcostos-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-invcontrolcostos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-invcontrolcostos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
						
						
						
					}
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			
			//carga select de tipos de producto
			$select_tipo_prod.children().remove();
			//var prodtipos_hmtl = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			var prodtipos_hmtl = '';
			$.each(entry['ProdTipos'],function(entryIndex,tp){
				if(parseInt(tp['id'])==1){
					prodtipos_hmtl += '<option value="' + tp['id'] + '" selected="yes">' + tp['titulo'] + '</option>';
				}else{
					if(parseInt(tp['id'])!=3 && parseInt(tp['id'])!=4){
						prodtipos_hmtl += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
					}
				}
			});
			$select_tipo_prod.append(prodtipos_hmtl);
			
			
			//carga select de Marcas
			$select_marca.children().remove();
			var marca_hmtl = '<option value="0" selected="yes">[--Seleccionar Marca--]</option>';
			$.each(entry['Marcas'],function(entryIndex,mar){
				marca_hmtl += '<option value="' + mar['id'] + '"  >' + mar['titulo'] + '</option>';
			});
			$select_marca.append(marca_hmtl);
			
			
			//carga select de familas
			$select_familia.children().remove();
			var familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
			$.each(entry['Familias'],function(entryIndex,fam){
				familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
			});
			$select_familia.append(familia_hmtl);
			
			
			//Alimentando select de SubFamilias
			$select_subfamilia.children().remove();
			var subfamilia_hmtl = '<option value="0">[--Seleccionar SubFamilia--]</option>';
			$select_subfamilia.append(subfamilia_hmtl);
			
			
			//carga select de Presentaciones
			$select_presentacion.children().remove();
			var presentacion_hmtl = '<option value="0">[--Seleccionar Presentaci&oacute;n--]</option>';
			//var presentacion_hmtl = '';
			var primer_elemento=0;
			$.each(entry['Presentaciones'],function(entryIndex,pres){
				if(parseInt(primer_elemento)<=0){
					//Esto es para que quede seleccionado el primer elemento
					presentacion_hmtl += '<option value="' + pres['id'] + '" selected="yes" >' + pres['titulo'] + '</option>';
				}else{
					presentacion_hmtl += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
				}
			});
			$select_presentacion.append(presentacion_hmtl);
			
			
			$select_tipo_prod.change(function(){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFamiliasByTipoProd.json';
				$arreglo = {	'tipo_prod':$select_tipo_prod.val(),
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
			
			
			
		},"json");//termina llamada json
		
		
		
		
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$tipo_cambio.blur(function(e){
			if(parseFloat($tipo_cambio.val())==0||$tipo_cambio.val()==""){
				$tipo_cambio.val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(4));
		});
		
		
	
		
		//click al radio buton de Costo Promedio
		$radio_costo_promedio.click(function(event){
			if($check_simulacion.is(':checked')){
				$tipo_cambio.val("1.0000");
				$tipo_cambio.css({'background' : '#F0F0F0'});
				$tipo_cambio.attr('readonly',true);
			}
			$etiqueta_encabezado_tipo_costo.html("C.&nbsp;P.");
		});
		
		//click al radio buton de Costo Ultimo
		$radio_costo_ultimo.click(function(event){
			if($check_simulacion.is(':checked')){
				//quitar propiedad de solo lectura
				$tipo_cambio.val("1.0000");
				$tipo_cambio.css({'background' : '#ffffff'});
				$tipo_cambio.removeAttr('readonly');
			}
			$etiqueta_encabezado_tipo_costo.html("C.&nbsp;U.");
		});
		
		//click al check de Simulacion
		$check_simulacion.click(function(event){
			if($(this).is(':checked')){
				if($radio_costo_ultimo.is(':checked')){
					//quitar propiedad de solo lectura
					$tipo_cambio.val("1.0000");
					$tipo_cambio.css({'background' : '#ffffff'});
					$tipo_cambio.removeAttr('readonly');
					//$submit_actualizar.attr('disabled','-1');//deshabilitar
					$submit_actualizar.hide();
				}
			}else{
				$tipo_cambio.val("1.0000");
				$tipo_cambio.css({'background' : '#F0F0F0'});
				$tipo_cambio.attr('readonly',true);
				//$submit_actualizar.removeAttr('disabled');//habilitar
				$submit_actualizar.show();
			}
		});
		
		
		
		//buscar producto
		$buscar_producto.click(function(event){
			event.preventDefault();
			$busca_productos($producto.val(), $codigo.val(), $select_tipo_prod.val(), $select_marca.val(), $select_familia.val(), $select_subfamilia.val() );
		});
		
		$aplicar_evento_keypress($producto, $buscar_producto);
		
		
		$busqueda.click(function(event){
			var tipo_costo=0;
			var simulacion="false";
			if($radio_costo_ultimo.is(':checked')){
				tipo_costo=1;
			}
			
			if($radio_costo_promedio.is(':checked')){
				tipo_costo=2;
			}
			
			if($check_simulacion.is(':checked')){
				//si calculo_simulacion=1, indica que el boton BUSCAR fue activado desde el boton APLICAR,
				//por lo tanto hay que hacer calculos de simulacion con los parametros
				if(parseInt($calculo_simulacion.val())==1){
					simulacion="true";
				}
			}
			
			if($costo_adic.val().trim()==''){
				$costo_adic.val(0);
			}
			
			//eliminar contenidos
			$grid_productos.children().remove();
			
			var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBusquedaProductos.json';
			$arreglo2 = {
							'tipo_prod':$select_tipo_prod.val(),
							'mar':$select_marca.val(),
							'fam':$select_familia.val(),
							'subfam':$select_subfamilia.val(),
							'tipo_costo':tipo_costo,
							'codigo':$codigo.val(),
							'producto':$producto.val(),
							'pres':$select_presentacion.val(),
							'simulacion':simulacion,
							'importacion':$costo_importacion.val(),
							'directo':$costo_directo.val(),
							'pminimo':$precio_minimo.val(),
							'tc':$tipo_cambio.val(),
							'costo_adic':$costo_adic.val(),
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
						
			$.post(input_json2,$arreglo2,function(data){
				
				$.each(data['Grid'],function(dataIndex,prod){
					var producto_id=prod['producto_id'];
					var codigo=prod['codigo'];
					var descripcion=prod['descripcion'];
					var unidad=prod['unidad'];
					var presentacion=prod['presentacion'];
					var orden_compra=prod['orden_compra'];
					var factura_prov=prod['factura_prov'];
					var moneda=prod['moneda'];
					var costo=prod['costo'];
					var tipo_cambio=prod['tipo_cambio'];
					var costo_importacion=prod['costo_importacion'];
					var costo_directo=prod['costo_directo'];
					var costo_referencia=prod['costo_referencia'];
					var precio_minimo=prod['precio_minimo'];
					var costo_adic=prod['costo_adic'];
					var moneda_pm=prod['moneda_pm'];
					
					var noTr = $("tr", $grid_productos).size();
					noTr++;
					
					var nuevo_tr = $genera_tr(noTr, producto_id, codigo, descripcion, unidad, presentacion, orden_compra, factura_prov, moneda, costo, tipo_cambio, costo_importacion, costo_directo, costo_referencia, precio_minimo, costo_adic, moneda_pm);
					//alert(nuevo_tr);
					$grid_productos.append(nuevo_tr);//agrega el tr a la tabla
					
				});
			});
		});
		
		
		//al darle aplicar en Simulacion, ejecutamos el click del boton Busqueda
		$aplicar.click(function(event){
			$('#forma-invcontrolcostos-window').find('div.interrogacion').css({'display':'none'});//desaparecer los warning
			var ejecutar=false;
			//indica que la busqueda se ejecuta desde el Boton APLICAR, 
			//por lo tanto debe hacer calculos de simulacion
			$calculo_simulacion.val(1);
			
			if(parseInt($("tr", $grid_productos).size())>0){
				if($check_simulacion.is(':checked')){
					if(parseFloat($tipo_cambio.val()) > 0 ){
						ejecutar=true;
					}else{
						ejecutar=false;
						//visualizar el warning
						$('#forma-invcontrolcostos-window').find('img[rel=warning_tipocambio]')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: "El Tipo de Cambio debe ser mayor que cero para la Simulaci&oacute;n."});
					}
					
					if(ejecutar){
						if(parseFloat($precio_minimo.val()) > 0 ){
							ejecutar=true;
						}else{
							ejecutar=false;
							jAlert("El porcentaje para el c&aacute;lculo del Precio M&iacute;nimo debe ser mayor que cero.", 'Atencion!');
							
							//visualizar el warning
							$('#forma-invcontrolcostos-window').find('img[rel=warning_preciominimo]')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: "El porcentaje para el c&aacute;lculo del Precio M&iacute;nimo debe ser mayor que cero."});
						}
					}
					
					if(ejecutar){
						$busqueda.trigger('click');
					}
					
				}else{
					jAlert("El boton Aplicar es para realizar una Simulaci&oacute;n de c&aacute;lculo de costos.\nSeleccione la casilla de Simulaci&oacute;n y haga click en el boton Aplicar.", 'Atencion!');
				}
			}else{
				jAlert("No hay productos en el listado.", 'Atencion!');
			}
			
			//devolvemos el valor cero para que la proxima que se ejecute el 
			//boton BUSCAR verifique si fue ejecutado desde el Boton APLICAR
			$calculo_simulacion.val(0);
		});
		
		
		
		//descargar pdf 
		$pdf.click(function(event){
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var tipo_costo=0;
			var simulacion="false";
			if($radio_costo_ultimo.is(':checked')){
				tipo_costo=1;
			}
			
			if($radio_costo_promedio.is(':checked')){
				tipo_costo=2;
			}
			
			if($check_simulacion.is(':checked')){
				simulacion="true";
			}
			
			if($costo_importacion.val().trim()=='') $costo_importacion.val(0);
			if($costo_directo.val().trim()=='') $costo_directo.val(0);
			if($precio_minimo.val().trim()=='') $precio_minimo.val(0);
			if($costo_adic.val().trim()=='') $costo_adic.val(0);
			//if($producto.val()=='') $producto.val("%%");
			
			//aqui se construye la cadena con los parametros de la busqueda
			var cadena = $select_tipo_prod.val()+"___"+$select_marca.val()+"___"+$select_familia.val()+"___"+$select_subfamilia.val()+"___"+$producto.val()+"___"+$select_presentacion.val()+"___"+tipo_costo+"___"+simulacion+"___"+$costo_importacion.val()+"___"+$costo_directo.val()+"___"+$precio_minimo.val()+"___"+$tipo_cambio.val()+"___"+$codigo.val();
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfReporteCostos/'+cadena+'/'+$costo_adic.val()+'/'+iu+'/out.json';
			
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				window.location.href=input_json;
			}else{
				jAlert("No hay datos para generar PDF.", 'Atencion!');
			}
			
			
			//5468
			
			
		});
		
		
		
		
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				if(parseFloat($precio_minimo.val()) > 0 ){
					return true;
				}else{
					jAlert("El porcentaje para el c&aacute;lculo del Precio M&iacute;nimo debe ser mayor que cero.", 'Atencion!');
					
					//visualizar el warning
					$('#forma-invcontrolcostos-window').find('img[rel=warning_preciominimo]')
					.parent()
					.css({'display':'block'})
					.easyTooltip({tooltipId: "easyTooltip2",content: "El porcentaje para el c&aacute;lculo del Precio M&iacute;nimo debe ser mayor que cero."});
					return false;
				}
			}else{
				jAlert("No hay datos para actualizar", 'Atencion!');
				return false;
			}
		});
		
		
		$aplicar_evento_keypress($select_tipo_prod, $busqueda);
		$aplicar_evento_keypress($select_marca, $busqueda);
		$aplicar_evento_keypress($select_familia, $busqueda);
		$aplicar_evento_keypress($select_subfamilia, $busqueda);
		$aplicar_evento_keypress($codigo, $busqueda);
		$aplicar_evento_keypress($select_presentacion, $busqueda);
		$aplicar_evento_keypress($radio_costo_ultimo, $busqueda);
		$aplicar_evento_keypress($radio_costo_promedio, $busqueda);
		$aplicar_evento_keypress($busqueda, $busqueda);
		$aplicar_evento_keypress($pdf, $pdf);
		
		$aplicar_evento_keypress($costo_importacion, $aplicar);
		$aplicar_evento_keypress($costo_directo, $aplicar);
		$aplicar_evento_keypress($precio_minimo, $aplicar);
		$aplicar_evento_keypress($aplicar, $aplicar);
		$aplicar_evento_keypress($submit_actualizar, $submit_actualizar);
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invcontrolcostos-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invcontrolcostos-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	
	
	
	var carga_formainvcontrolcostos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui no hay nada
	}
	
	
	$get_datos_grid = function(){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllCostos.json';
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
		$arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllCostos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
		$.post(input_json,$arreglo,function(data){
			//pinta_grid			
			//aqui se utiliza el mismo datagrid que prefacturas. Solo muesta icono de detalles, el de eliminar No
			$.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvcontrolcostos00_for_datagrid00);
			
			//resetea elastic, despues de pintar el grid y el slider
			Elastic.reset(document.getElementById('lienzo_recalculable'));
		},"json");
	}
	
    $get_datos_grid();
});



