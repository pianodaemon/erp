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
	var controller = $contextpath.val()+"/controllers/invactualizaprecios";
	
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Actualizador de Precios a partir de Precio M&iacute;nimo');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_select_tipo_prod = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_tipo_prod]');
	var $busqueda_select_familia = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_familia]');
	var $busqueda_select_subfamilia = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_subfamilia]');
	var $busqueda_select_marca = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_marca]');
	var $busqueda_select_presentacion = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_presentacion]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	
	var $campo_busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $campo_busqueda_oc = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_oc]');
	
	//este arreglo carga el select de meses
	var array_meses = { 0:"- Mes -",  1:"Enero",  2:"Febrero", 3:"Marzo", 4:"Abirl", 5:"Mayo", 6:"Junio", 7:"Julio", 8:"Agosto", 9:"Septiembre", 10:"Octubre", 11:"Noviembre", 12:"Diciembre"};
	
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
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "producto" + signo_separador + $busqueda_producto.val();
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
	$aplicar_evento_keypress($busqueda_codigo, $buscar);
	$aplicar_evento_keypress($busqueda_producto, $buscar);
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
	
	$visualiza_buscador.trigger('click');
	
	$cargar_datos_buscador_principal= function(){
		var seleccionar=0;
		var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosBuscadorPrincipal.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_lineas,$arreglo,function(data){
			$busqueda_codigo.val('');
			$busqueda_producto.val('');
			
			//carga select de tipos de producto
			$busqueda_select_tipo_prod.children().remove();
			var prodtipos = '<option value="0">[--Seleccionar Tipo--]</option>';
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
				familia += '<option value="' + fam['id'] + '" >' + fam['titulo'] + '</option>';
			});
			$busqueda_select_familia.append(familia);
			
			//Alimentando select de SubFamilias
			$busqueda_select_subfamilia.children().remove();
			var subfamilia = '<option value="0">--SubFamilia--</option>';
			$busqueda_select_subfamilia.append(subfamilia);
			
			/*
			//carga select de familas
			$busqueda_select_familia.children().remove();
			
			if (entry['Familias'].length > 0){
				seleccionar=1;
			}
			
			var familia = '<option value="0">--Familia--</option>';
			$.each(data['Familias'],function(entryIndex,fam){
				if(parseInt(seleccionar)==1){
					familia += '<option value="' + fam['id'] + '" selected="yes">' + fam['titulo'] + '</option>';
				}else{
					familia += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
					seleccionar=0;//esto hara que ya no vuelva a pasar en el lado verdadero del if
				}
			});
			$busqueda_select_familia.append(familia);
			
			
			
			
			//Alimentando select de SubFamilias
			$busqueda_select_subfamilia.children().remove();
			if (entry['SubFamilias'].length > 0){
				seleccionar=1;
			}
			var subfamilia = '<option value="0">--SubFamilia--</option>';
			$.each(data['SubFamilias'],function(entryIndex,subfam){
				if(parseInt(seleccionar)==1){
					subfamilia += '<option value="' + subfam['id'] + '" selected="yes">' + subfam['titulo'] + '</option>';
				}else{
					subfamilia += '<option value="' + subfam['id'] + '"  >' + subfam['titulo'] + '</option>';
					seleccionar=0;//esto hara que ya no vuelva a pasar en el lado verdadero del if
				}
			});
			$busqueda_select_subfamilia.append(subfamilia);
			*/
			
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
	
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-invactualizaprecios-window').find('select[name=prodtipo]');
		$('#forma-invactualizaprecios-window').find('#submit').mouseover(function(){
			$('#forma-invactualizaprecios-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-invactualizaprecios-window').find('#submit').mouseout(function(){
			$('#forma-invactualizaprecios-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		
		$('#forma-invactualizaprecios-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invactualizaprecios-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-invactualizaprecios-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invactualizaprecios-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invactualizaprecios-window').find('#close').mouseover(function(){
			$('#forma-invactualizaprecios-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invactualizaprecios-window').find('#close').mouseout(function(){
			$('#forma-invactualizaprecios-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-invactualizaprecios-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invactualizaprecios-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invactualizaprecios-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invactualizaprecios-window').find("ul.pestanas li").click(function() {
			$('#forma-invactualizaprecios-window').find(".contenidoPes").hide();
			$('#forma-invactualizaprecios-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invactualizaprecios-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
	
	$add_ceros = function($campo){
		$campo.val("0.00");
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
	
	
	
	$accion_focus = function( $campo_input ){
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo_input.focus(function(e){
			if(parseFloat($campo_input.val())<1){
				$campo_input.val('');
			}
		});
	}
	
	
	$accio_blur = function( $campo_input ){
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
	
	
	
	
	
	
	
	//Aquí entra nuevo
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_invactualizaprecios();
		
		var form_to_show = 'formainvactualizaprecios00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-invactualizaprecios-window').css({ "margin-left": -190, 	"margin-top": -200 });
		
		$forma_selected.prependTo('#forma-invactualizaprecios-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var $identificador = $('#forma-invactualizaprecios-window').find('input[name=identificador]');
		var $tipo_producto = $('#forma-invactualizaprecios-window').find('input[name=tipo_producto]');
		var $familia = $('#forma-invactualizaprecios-window').find('input[name=familia]');
		var $subfamilia = $('#forma-invactualizaprecios-window').find('input[name=subfamilia]');
		var $marca = $('#forma-invactualizaprecios-window').find('input[name=marca]');
		var $presentacion = $('#forma-invactualizaprecios-window').find('input[name=presentacion]');
		var $codigo = $('#forma-invactualizaprecios-window').find('input[name=codigo]');
		var $producto = $('#forma-invactualizaprecios-window').find('input[name=producto]');
		
		var $lista1 = $('#forma-invactualizaprecios-window').find('input[name=lista1]');
		var $lista2 = $('#forma-invactualizaprecios-window').find('input[name=lista2]');
		var $lista3 = $('#forma-invactualizaprecios-window').find('input[name=lista3]');
		var $lista4 = $('#forma-invactualizaprecios-window').find('input[name=lista4]');
		var $lista5 = $('#forma-invactualizaprecios-window').find('input[name=lista5]');
		var $lista6 = $('#forma-invactualizaprecios-window').find('input[name=lista6]');
		var $lista7 = $('#forma-invactualizaprecios-window').find('input[name=lista7]');
		var $lista8 = $('#forma-invactualizaprecios-window').find('input[name=lista8]');
		var $lista9 = $('#forma-invactualizaprecios-window').find('input[name=lista9]');
		var $lista10 = $('#forma-invactualizaprecios-window').find('input[name=lista10]');
		var $descto1 = $('#forma-invactualizaprecios-window').find('input[name=descto1]');
		var $descto2 = $('#forma-invactualizaprecios-window').find('input[name=descto2]');
		var $descto3 = $('#forma-invactualizaprecios-window').find('input[name=descto3]');
		var $descto4 = $('#forma-invactualizaprecios-window').find('input[name=descto4]');
		var $descto5 = $('#forma-invactualizaprecios-window').find('input[name=descto5]');
		var $descto6 = $('#forma-invactualizaprecios-window').find('input[name=descto6]');
		var $descto7 = $('#forma-invactualizaprecios-window').find('input[name=descto7]');
		var $descto8 = $('#forma-invactualizaprecios-window').find('input[name=descto8]');
		var $descto9 = $('#forma-invactualizaprecios-window').find('input[name=descto9]');
		var $descto10 = $('#forma-invactualizaprecios-window').find('input[name=descto10]');
		
		var $check_aplicar_descto = $('#forma-invactualizaprecios-window').find('input[name=check_aplicar_descto]');
		
		var $genera_pdf = $('#forma-invactualizaprecios-window').find('#genera_pdf');
		
		var $cerrar_plugin = $('#forma-invactualizaprecios-window').find('#close');
		var $cancelar_plugin = $('#forma-invactualizaprecios-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invactualizaprecios-window').find('#submit');
		
		/*
		//quitar enter a todos los campos input
		$('#forma-invactualizaprecios-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		*/
		
		/*
		$radio_costo_ultimo.attr('checked',  true );
		$costo_importacion.val(parseFloat(0).toFixed(2));
		$costo_directo.val(parseFloat(0).toFixed(2));
		$precio_minimo.val(parseFloat(0).toFixed(2));
		$tipo_cambio.val(parseFloat(0).toFixed(4));
		*/
		
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
		
		$identificador.val('0');
		
		/*
		Aquí se asignan valores que vienen del buscador principal, 
		estos seran utilizados para actualizar precios de productos que cumplan con estos filtros
		*/
		$tipo_producto.val($busqueda_select_tipo_prod.val());
		$familia.val($busqueda_select_familia.val());
		$subfamilia.val($busqueda_select_subfamilia.val());
		$marca.val($busqueda_select_marca.val());
		$presentacion.val($busqueda_select_presentacion.val());
		$codigo.val($busqueda_codigo.val());
		$producto.val($busqueda_producto.val());
		
		$genera_pdf.hide();
		$lista1.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los precios se han actualizado con &eacute;xito", 'Atencion!');
				//var remove = function() {$(this).remove();};
				//$('#forma-invactualizaprecios-overlay').fadeOut(remove);
				
				$genera_pdf.trigger('click');
				$genera_pdf.show();
				//$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-invactualizaprecios-window').find('.div_one').css({'height':'545px'});//sin errores
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						/*
						$('#forma-invactualizaprecios-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						*/
						//alert(tmp.split(':')[0]);
						jAlert(tmp.split(':')[1], 'Atencion!');
					}
					
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		/*
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			
		},"json");//termina llamada json
		*/
		
		
		//Generar Pdf 
		$genera_pdf.click(function(event){
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var actualizar_descto="false";
			
			if($check_aplicar_descto.is(':checked')){
				actualizar_descto="true";
			}
			
			//aqui se construye la cadena con los parametros de la busqueda
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getGenerarPdf.json';
			$arreglo = {
						'tipo_producto': $tipo_producto.val(),
						'marca': $marca.val(),
						'familia': $familia.val(),
						'subfamilia': $subfamilia.val(),
						'codigo': $codigo.val(),
						'producto': $producto.val(),
						'presentacion': $presentacion.val(),
						'lista1': $lista1.val(),
						'lista2': $lista2.val(),
						'lista3': $lista3.val(),
						'lista4': $lista4.val(),
						'lista5': $lista5.val(),
						'lista6': $lista6.val(),
						'lista7': $lista7.val(),
						'lista8': $lista8.val(),
						'lista9': $lista9.val(),
						'lista10': $lista10.val(),
						'descto1': $descto1.val(),
						'descto2': $descto2.val(),
						'descto3': $descto3.val(),
						'descto4': $descto4.val(),
						'descto5': $descto5.val(),
						'descto6': $descto6.val(),
						'descto7': $descto7.val(),
						'descto8': $descto8.val(),
						'descto9': $descto9.val(),
						'descto10': $descto10.val(),
						'actualizar_descto': actualizar_descto,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			
			$.post(input_json,$arreglo,function(entry){
				
				if(entry['generado']=='true'){
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getDescargaPdf/'+entry['file_name']+'/out.json';
					window.location.href=input_json;
				}else{
					jAlert("Error al generar el PDF.", 'Atencion!');
				}
				
			},"json");//termina llamada json
			
		});
		
		
		
		
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invactualizaprecios-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invactualizaprecios-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	
	
	
	var carga_formainvactualizaprecios00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui no hay nada
	}
	
	
	$get_datos_grid = function(){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllPminProductos.json';
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
		$arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllPminProductos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
		$.post(input_json,$arreglo,function(data){
			//pinta_grid			
			//aqui se utiliza el mismo datagrid que prefacturas. Solo muesta icono de detalles, el de eliminar No
			$.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvactualizaprecios00_for_datagrid00);
			
			//resetea elastic, despues de pintar el grid y el slider
			Elastic.reset(document.getElementById('lienzo_recalculable'));
		},"json");
	}
	
    $get_datos_grid();
});



