$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
		work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	var arrayCentroCostos;
	var ArraySuc;
	var Param;
	
    //Arreglo para select de Nivel de cuenta
    var array_nive_cta = {1:"Auxiliar", 2:"Mayor" };
    //Arreglo para select de Naturaleza de la cuenta
    var array_naturaleza_cta = {1:"Deudora", 2:"Acreedora" };
    //Arreglo para select de Tipo de cuenta
    var array_tipo_cta = {1:"Balance", 2:"Resultados", 3:"De orden" };
    
	//carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, txt_elemento_cero, elemento_seleccionado, mostrar_opciones){
		$campo_select.children().remove();
		var select_html = '';
		if(txt_elemento_cero.trim()!=''){
			select_html = '<option value="0" selected="yes">'+ txt_elemento_cero +'</option>';
		}
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				if (mostrar_opciones){
					select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
				}
			}
		}
		$campo_select.append(select_html);
	}
	
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/cuentascontables";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_centro_costo = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cuentas de Contables');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
	var $cadena_busqueda = "";
	var $busqueda_select_cuenta_mayor = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_cuenta_mayor]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	var $busqueda_select_sucursal = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_sucursal]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "cta_mayor" + signo_separador + $busqueda_select_cuenta_mayor.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		valor_retorno += "sucursal" + signo_separador + $busqueda_select_sucursal.val() + "|";
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
	
	
	
	$ubtener_cuentas_mayor = function(){
		var input_json_cuentas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInicializar.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_cuentas,$arreglo,function(data){
			$busqueda_select_cuenta_mayor.children().remove();
			var cta_html = '<option value="0" selected="yes">[-- --]</option>';
			$.each(data['CtaMay'],function(entryIndex,ctamay){
				cta_html += '<option value="' + ctamay['id'] + '"  >( ' + ctamay['cta_mayor']+', '+ ctamay['clasificacion'] +' ) '+ ctamay['descripcion'] + '</option>';
			});
			$busqueda_select_cuenta_mayor.append(cta_html);
			
			
			$busqueda_select_sucursal.children().remove();
			var suc_hmtl = '';
			if(data['Data']['versuc']==true){
				//Aqui carga todas las sucursales porque el usuario es un administrador
				suc_hmtl = '<option value="0" selected="yes">[--- Todos ---]</option>';
				$.each(data['Suc'],function(entryIndex,suc){
					suc_hmtl += '<option value="' + suc['id'] + '">'+ suc['titulo'] + '</option>';
				});
			}else{
				//Aqui solo debe cargar la sucursal del usuario logueado
				$.each(data['Suc'],function(entryIndex,suc){
					if(parseInt(suc['id'])==parseInt(data['Data']['suc'])){
						suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
					}
				});
			}
			$busqueda_select_sucursal.append(suc_hmtl);
			
			
			//Cargar arreglo con la lista de centros de costo.
			arrayCentroCostos=data['CC'];
			ArraySuc = data['Suc'];
			Param = data['Data'];
		});
	}
	
	//Cargar datos al cargar la ventana
	$ubtener_cuentas_mayor();
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_descripcion.val('');
		$ubtener_cuentas_mayor();
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
		
		$busqueda_select_cuenta_mayor.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_cuenta_mayor, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_descripcion, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-cuentascontables-window').find('#submit').mouseover(function(){
			$('#forma-cuentascontables-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-cuentascontables-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-cuentascontables-window').find('#submit').mouseout(function(){
			$('#forma-cuentascontables-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-cuentascontables-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-cuentascontables-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-cuentascontables-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-cuentascontables-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-cuentascontables-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-cuentascontables-window').find('#close').mouseover(function(){
			$('#forma-cuentascontables-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-cuentascontables-window').find('#close').mouseout(function(){
			$('#forma-cuentascontables-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-cuentascontables-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-cuentascontables-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-cuentascontables-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-cuentascontables-window').find("ul.pestanas li").click(function() {
			$('#forma-cuentascontables-window').find(".contenidoPes").hide();
			$('#forma-cuentascontables-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-cuentascontables-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
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
	
	
	//Carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, $arreglo_elementos, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem){
		var select_html = '';
		
		if(texto_elemento_cero != ''){
			select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		}
		
		$.each($arreglo_elementos,function(entryIndex,elemento){
			if( parseInt(elemento[index_elem]) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + elemento[index_elem] + '" selected="yes">' + elemento[index_text_elem] + '</option>';
			}else{
				select_html += '<option value="' + elemento[index_elem] + '" >' + elemento[index_text_elem] + '</option>';
			}
		});
		$campo_select.children().remove();
		$campo_select.append(select_html);
	}
	
	
	//Carga el campo centro costo dependiendo de la clase de la cuenta de mayor
	$carga_select_centro_costos = function($campo_select, arrayCentroCostos, clase, class_selected){
		//Cargar select de Centro de Costos
		var elemento_seleccionado = 0;
		var texto_elemento_cero = '[--- ---]';
		var index_elem = 'id';
		var index_text_elem = 'titulo';
		
		//4=Ingresos
		//5=Egresos
		if(parseInt(clase)==4 || parseInt(clase)==5){
			//Cargar select de Centro de Costos
			elemento_seleccionado = class_selected;
			texto_elemento_cero = '[--- ---]';
			index_elem = 'id';
			index_text_elem = 'titulo';
			$carga_campos_select($campo_select, arrayCentroCostos, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
		}else{
			//Vaciar select
			$campo_select.children().remove();
			var cc_hmtl = '';
			cc_hmtl += '<option value="0" selected="yes">[--- ---]</option>';
			$campo_select.append(cc_hmtl);
		}
	}
			
	
	
	//buscador de productos
	var $busca_cta_sat = function(codigo, descripcion){
		//limpiar_campos_grids();
		$(this).modalPanel_cuentasat();
		var $dialogoc =  $('#forma-cuentasat-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_cuentasat').find('table.formaBusqueda_cuentasat').clone());
		
		$('#forma-cuentasat-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-cuentasat-window').find('#tabla_resultado');
		
		var $campo_codigo_sat = $('#forma-cuentasat-window').find('input[name=campo_codigo_sat]');
		var $campo_descripcion_sat = $('#forma-cuentasat-window').find('input[name=campo_descripcion_sat]');
		
		var $buscar_plugin_producto = $('#forma-cuentasat-window').find('#busca_producto_modalbox');
		var $cancelar_plugin_busca_producto = $('#forma-cuentasat-window').find('#cencela');
		
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
		
		//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
		$campo_codigo_sat.val(codigo);
		
		//asignamos la descripcion del producto, si el usuario capturo la descripcion antes de abrir el buscador
		$campo_descripcion_sat.val(descripcion);
		
		
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCtaAgrupadorSat.json';
			var $arreglo = {'codigo':$campo_codigo_sat.val(), 'descripcion':$campo_descripcion_sat.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Ctas'],function(entryIndex,cta){
					trr = '<tr>';
						trr += '<td width="70"><span class="nivel">'+cta['nivel']+'</span></td>';
						trr += '<td width="140">';
							trr += '<input type="hidden" name="id_sat" value="'+cta['id']+'">';
							trr += '<span class="codigo_sat">'+cta['codigo']+'</span>';
						trr += '</td>';
						trr += '<td width="360"><span class="titulo_sat">'+cta['nombre']+'</span></td>';
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
					$('#forma-cuentascontables-window').find('input[name=ctasat_id]').val($(this).find('input[name=id_sat]').val());
					$('#forma-cuentascontables-window').find('input[name=cta_sat]').val($(this).find('span.codigo_sat').html());
					$('#forma-cuentascontables-window').find('input[name=desc_cta_sat]').val($(this).find('span.titulo_sat').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-cuentasat-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-cuentascontables-window').find('input[name=cta_sat]').focus();
				});
				
			});//termina llamada json
		});
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_codigo_sat.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_codigo_sat, $buscar_plugin_producto);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_descripcion_sat, $buscar_plugin_producto);
		
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-cuentasat-overlay').fadeOut(remove);
			$('#forma-cuentascontables-window').find('input[name=cta_sat]').focus();
		});
		
		$campo_codigo_sat.focus();
		
	}//termina buscador de productos
	
	
	
	var $busca_datos_cta_sat = function($codigo){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCtaSat.json';
		var $arreglo = {'codigo':$codigo.val().trim()};
		
		$.post(input_json,$arreglo,function(entry){
			if(parseInt(entry['Cta'].length) > 0 ){
				$('#forma-cuentascontables-window').find('input[name=ctasat_id]').val(entry['Cta'][0]['id']);
				$('#forma-cuentascontables-window').find('input[name=cta_sat]').val(entry['Cta'][0]['codigo']);
				$('#forma-cuentascontables-window').find('input[name=desc_cta_sat]').val(entry['Cta'][0]['nombre']);
			}else{
				$('#forma-cuentascontables-window').find('input[name=ctasat_id]').val(0);
				//$('#forma-cuentascontables-window').find('input[name=cta_sat]').val();
				$('#forma-cuentascontables-window').find('input[name=desc_cta_sat]').val('');
				
				jAlert('La cuenta agrupadora del SAT no existe.', 'Atencion!', function(r) { 
					$('#forma-cuentascontables-window').find('input[name=cta_sat]').focus(); 
				});
			}
		});//termina llamada json
	}
	
	//nuevo 
	$new_centro_costo.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_cuentascontables();
		
		var form_to_show = 'formaCuentasContables00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-cuentascontables-window').css({ "margin-left": -375, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-cuentascontables-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $identificador = $('#forma-cuentascontables-window').find('input[name=identificador]');
		var $cuenta = $('#forma-cuentascontables-window').find('input[name=cuenta]');
		var $scuenta = $('#forma-cuentascontables-window').find('input[name=scuenta]');
		var $sscuenta = $('#forma-cuentascontables-window').find('input[name=sscuenta]');
		var $ssscuenta = $('#forma-cuentascontables-window').find('input[name=ssscuenta]');
		var $sssscuenta = $('#forma-cuentascontables-window').find('input[name=sssscuenta]');
		var $select_cuenta_mayor = $('#forma-cuentascontables-window').find('select[name=select_cuenta_mayor]');
		
		var $select_centro_costo = $('#forma-cuentascontables-window').find('select[name=select_centro_costo]');
		var $select_sucursal = $('#forma-cuentascontables-window').find('select[name=select_sucursal]');
		
		var $select_nivel = $('#forma-cuentascontables-window').find('select[name=select_nivel]');
		var $select_naturaleza = $('#forma-cuentascontables-window').find('select[name=select_naturaleza]');
		var $select_tipo_cta = $('#forma-cuentascontables-window').find('select[name=select_tipo_cta]');
		
		var $descripcion = $('#forma-cuentascontables-window').find('input[name=descripcion]');
		var $chk_cta_detalle = $('#forma-cuentascontables-window').find('input[name=chk_cta_detalle]');
		var $select_estatus = $('#forma-cuentascontables-window').find('select[name=select_estatus]');
		var $select_agrupador = $('#forma-cuentascontables-window').find('select[name=select_agrupador]');
		
		var $descripcion_es = $('#forma-cuentascontables-window').find('input[name=descripcion_es]');
		var $descripcion_in = $('#forma-cuentascontables-window').find('input[name=descripcion_in]');
		var $descripcion_otro = $('#forma-cuentascontables-window').find('input[name=descripcion_otro]');
		
		var $ctasat_id = $('#forma-cuentascontables-window').find('input[name=ctasat_id]');
		var $cta_sat = $('#forma-cuentascontables-window').find('input[name=cta_sat]');
		var $desc_cta_sat = $('#forma-cuentascontables-window').find('input[name=desc_cta_sat]');
		
		var $cerrar_plugin = $('#forma-cuentascontables-window').find('#close');
		var $cancelar_plugin = $('#forma-cuentascontables-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-cuentascontables-window').find('#submit');
		
		solo_numeros($cuenta);
		solo_numeros($scuenta);
		solo_numeros($sscuenta);
		solo_numeros($ssscuenta);
		solo_numeros($sssscuenta);
		
		$cuenta.hide();
		$scuenta.hide();
		$sscuenta.hide();
		$ssscuenta.hide();
		$sssscuenta.hide();
		
		$identificador.attr({ 'value' : 0 });
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Cuenta fue dada de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-cuentascontables-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-cuentascontables-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-cuentascontables-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCuentaContable.json';
		$arreglo = {
			'id':id_to_show,
			'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
		};
		
		$.post(input_json,$arreglo,function(entry){
			//visualizar subcuentas de acuerdo al nivel definido para la empresa
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $cuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $scuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $sscuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ssscuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $sssscuenta.show(); };
			
			$select_sucursal.children().remove();
			var suc_hmtl = '';
			$.each(ArraySuc,function(entryIndex,suc){
				if(parseInt(suc['id'])==parseInt(Param['suc'])){
					suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
				}else{
					if(Param['versuc']==true){
						suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
					}
				}
			});
			$select_sucursal.append(suc_hmtl);
			
			//Carga select de cuentas de Mayor
			$select_cuenta_mayor.children().remove();
			var ctamay_hmtl = '';
			$.each(entry['CtaMay'],function(entryIndex,ctamay){
				ctamay_hmtl += '<option value="' + ctamay['id'] + '"  >( ' + ctamay['cta_mayor']+', '+ ctamay['clasificacion'] +' ) '+ ctamay['descripcion'] + '</option>';
			});
			$select_cuenta_mayor.append(ctamay_hmtl);
			
			//carga select de cuentas de Estatus
			$select_estatus.children().remove();
			var estatus_hmtl = '';
			estatus_hmtl += '<option value="1" selected="yes">Activada</option>';
			estatus_hmtl += '<option value="2">Desactivada</option>';
			$select_estatus.append(estatus_hmtl);
			
			//Carga default al cargar la ventana
			$carga_select_centro_costos($select_centro_costo, arrayCentroCostos, entry['CtaMay'][0]['cta_mayor'], 0);
			
			$select_cuenta_mayor.change(function(){
				var valor = $(this).val();
				$.each(entry['CtaMay'],function(entryIndex,ctamay){
					if(parseInt(ctamay['id'])==parseInt(valor)){
						$carga_select_centro_costos($select_centro_costo, arrayCentroCostos, ctamay['cta_mayor'], 0);
					}
				});
			});
			
			//Carga select de niveles
			var mostrar_opciones=true;
			var elemento_seleccionado=0;
			var txt_elemento_cero='';
			$carga_select_con_arreglo_fijo($select_nivel, array_nive_cta, txt_elemento_cero, elemento_seleccionado, mostrar_opciones)
			
			//Carga select de naturaleza de la cuenta
			mostrar_opciones=true;
			elemento_seleccionado=0;
			txt_elemento_cero='';
			$carga_select_con_arreglo_fijo($select_naturaleza, array_naturaleza_cta, txt_elemento_cero, elemento_seleccionado, mostrar_opciones)
			
			mostrar_opciones=true;
			elemento_seleccionado=0;
			txt_elemento_cero='';
			$carga_select_con_arreglo_fijo($select_tipo_cta, array_tipo_cta, txt_elemento_cero, elemento_seleccionado, mostrar_opciones)
			
			//Carga select de agrupador
			$select_agrupador.children().remove();
			var agrupa_hmtl = '<option value="0">[-----------]</option>';
			$.each(entry['App'],function(entryIndex,agrupa){
				agrupa_hmtl += '<option value="' + agrupa['id'] + '"  >'+ agrupa['titulo'] + '</option>';
			});
			$select_agrupador.append(agrupa_hmtl);
			
			/*
			//Carga select de niveles
			$select_nivel.children().remove();
			var nivel_hmtl = '';
			$.each(Param['NivCta'],function(entryIndex,nivel){
				nivel_hmtl += '<option value="' + nivel['index'] + '"  >'+ nivel['text'] + '</option>';
			});
			$select_nivel.append(nivel_hmtl);
			
			//Carga select de naturaleza de la cuenta
			$select_naturaleza.children().remove();
			var naturaleza_hmtl = '';
			$.each(Param['NatCta'],function(entryIndex,nat){
				naturaleza_hmtl += '<option value="' + nat['index'] + '"  >'+ nat['text'] + '</option>';
			});
			$select_naturaleza.append(naturaleza_hmtl);
			
			//Carga select de tipo de cuenta
			$select_tipo_cta.children().remove();
			var tipo_hmtl = '';
			$.each(Param['TipoCta'],function(entryIndex,tipo){
				tipo_hmtl += '<option value="' + tipo['index'] + '"  >'+ tipo['text'] + '</option>';
			});
			$select_tipo_cta.append(tipo_hmtl);
			*/
			
			$cuenta.focus();
		},"json");//termina llamada json
		
		
		$descripcion.change(function(){
			$descripcion_es.val($(this).val());
		});
		
		$descripcion_es.change(function(){
			$descripcion.val($(this).val());
		});
		
		
		$('#forma-cuentascontables-window').find('a[href=busca_codigo_sat]').click(function(event){
			event.preventDefault();
			$busca_cta_sat($cta_sat.val(), $desc_cta_sat.val());
		});
		
		$cta_sat.blur(function(){
			if($(this).val().trim()!=''){
				if(parseInt($ctasat_id.val())<=0){
					$busca_datos_cta_sat($cta_sat);
				}
			}
		});
							
		$cta_sat.keypress(function(e){
			if(e.which == 8) {
				$ctasat_id.val(0);
			}
		});
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-cuentascontables-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-cuentascontables-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
	});
	
	
        
        
        
	
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Cuenta Contable seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Cuenta Contable fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Cuenta Contable no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaCuentasContables00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_cuentascontables();
			$('#forma-cuentascontables-window').css({ "margin-left": -375, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-cuentascontables-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $identificador = $('#forma-cuentascontables-window').find('input[name=identificador]');
			var $cuenta = $('#forma-cuentascontables-window').find('input[name=cuenta]');
			var $scuenta = $('#forma-cuentascontables-window').find('input[name=scuenta]');
			var $sscuenta = $('#forma-cuentascontables-window').find('input[name=sscuenta]');
			var $ssscuenta = $('#forma-cuentascontables-window').find('input[name=ssscuenta]');
			var $sssscuenta = $('#forma-cuentascontables-window').find('input[name=sssscuenta]');
			var $select_cuenta_mayor = $('#forma-cuentascontables-window').find('select[name=select_cuenta_mayor]');
			
			var $select_centro_costo = $('#forma-cuentascontables-window').find('select[name=select_centro_costo]');
			var $select_sucursal = $('#forma-cuentascontables-window').find('select[name=select_sucursal]');
			
			var $select_nivel = $('#forma-cuentascontables-window').find('select[name=select_nivel]');
			var $select_naturaleza = $('#forma-cuentascontables-window').find('select[name=select_naturaleza]');
			var $select_tipo_cta = $('#forma-cuentascontables-window').find('select[name=select_tipo_cta]');
			
			var $descripcion = $('#forma-cuentascontables-window').find('input[name=descripcion]');
			var $chk_cta_detalle = $('#forma-cuentascontables-window').find('input[name=chk_cta_detalle]');
			var $select_estatus = $('#forma-cuentascontables-window').find('select[name=select_estatus]');
			var $select_agrupador = $('#forma-cuentascontables-window').find('select[name=select_agrupador]');
			
			var $descripcion_es = $('#forma-cuentascontables-window').find('input[name=descripcion_es]');
			var $descripcion_in = $('#forma-cuentascontables-window').find('input[name=descripcion_in]');
			var $descripcion_otro = $('#forma-cuentascontables-window').find('input[name=descripcion_otro]');
			
			var $ctasat_id = $('#forma-cuentascontables-window').find('input[name=ctasat_id]');
			var $cta_sat = $('#forma-cuentascontables-window').find('input[name=cta_sat]');
			var $desc_cta_sat = $('#forma-cuentascontables-window').find('input[name=desc_cta_sat]');
			
			var $cerrar_plugin = $('#forma-cuentascontables-window').find('#close');
			var $cancelar_plugin = $('#forma-cuentascontables-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-cuentascontables-window').find('#submit');
			
			solo_numeros($cuenta);
			solo_numeros($scuenta);
			solo_numeros($sscuenta);
			solo_numeros($ssscuenta);
			solo_numeros($sssscuenta);
			
			$cuenta.hide();
			$scuenta.hide();
			$sscuenta.hide();
			$ssscuenta.hide();
			$sssscuenta.hide();
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCuentaContable.json';
				$arreglo = {	'id':id_to_show,
								'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-cuentascontables-overlay').fadeOut(remove);
						jAlert("Los datos de la Cuenta se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-cuentascontables-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-cuentascontables-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					//visualizar subcuentas de acuerdo al nivel definido para la empresa
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $cuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $scuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $sscuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ssscuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $sssscuenta.show(); };
					
					$identificador.attr({ 'value' : entry['Cc'][0]['id'] });
					$cuenta.attr({ 'value' : entry['Cc'][0]['cta'] });
					$scuenta.attr({ 'value' : entry['Cc'][0]['subcta'] });
					$sscuenta.attr({ 'value' : entry['Cc'][0]['ssubcta'] });
					$ssscuenta.attr({ 'value' : entry['Cc'][0]['sssubcta'] });
					$sssscuenta.attr({ 'value' : entry['Cc'][0]['ssssubcta'] });
					
					$descripcion.attr({ 'value' : entry['Cc'][0]['descripcion'] });
					$descripcion_es.attr({ 'value' : entry['Cc'][0]['descripcion'] });
					$descripcion_in.attr({ 'value' : entry['Cc'][0]['descripcion_ing'] });
					$descripcion_otro.attr({ 'value' : entry['Cc'][0]['descripcion_otr'] });
					
					$ctasat_id.attr({ 'value' : entry['Cc'][0]['ctasat_id'] });
					$cta_sat.attr({ 'value' : entry['Cc'][0]['codigo_sat'] });
					$desc_cta_sat.attr({ 'value' : entry['Cc'][0]['nombre_cta_sat'] });
			
					$select_sucursal.children().remove();
					var suc_hmtl = '';
					$.each(ArraySuc,function(entryIndex,suc){
						if(parseInt(suc['id'])==parseInt(entry['Cc'][0]['suc_id'])){
							suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
						}else{
							if(Param['versuc']==true){
								suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
							}
						}
					});
					$select_sucursal.append(suc_hmtl);
					
					
					//carga select de cuentas de Mayor
					$select_cuenta_mayor.children().remove();
					var ctamay_hmtl = '';
					$.each(entry['CtaMay'],function(entryIndex,ctamay){
						if(entry['Cc']['0']['cta_mayor']==ctamay['cta_mayor'] && entry['Cc']['0']['clasifica']==ctamay['clasificacion']){
							ctamay_hmtl += '<option value="' + ctamay['id'] + '" selected="yes">( ' + ctamay['cta_mayor']+', '+ ctamay['clasificacion'] +' ) '+ ctamay['descripcion'] + '</option>';
							
							var class_selected =entry['Cc'][0]['cc_id'];
							
							//Carga default al cargar la ventana
							$carga_select_centro_costos($select_centro_costo, arrayCentroCostos, ctamay['cta_mayor'], class_selected);
							
						}else{
							ctamay_hmtl += '<option value="' + ctamay['id'] + '">( ' + ctamay['cta_mayor']+', '+ ctamay['clasificacion'] +' ) '+ ctamay['descripcion'] + '</option>';
						}
					});
					$select_cuenta_mayor.append(ctamay_hmtl);
					
					//var array_nive_cta = {1:"Auxiliar", 2:"Mayor" };
					//var array_naturaleza_cta = {1:"Deudora", 2:"Acreedora" };
					//var array_tipo_cta = {1:"Balance", 2:"Resultados", 3:"De orden" };
					
					//Carga select de niveles
					var mostrar_opciones=true;
					var elemento_seleccionado=entry['Cc'][0]['nivel'];
					var txt_elemento_cero='';
					if(parseInt(entry['Cc'][0]['nivel'])==0){
						txt_elemento_cero = '[---------]';
					}
					$carga_select_con_arreglo_fijo($select_nivel, array_nive_cta, txt_elemento_cero, elemento_seleccionado, mostrar_opciones)
					
					//Carga select de naturaleza de la cuenta
					mostrar_opciones=true;
					elemento_seleccionado=entry['Cc'][0]['naturaleza'];
					txt_elemento_cero='';
					if(parseInt(entry['Cc'][0]['naturaleza'])==0){
						txt_elemento_cero = '[---------]';
					}
					$carga_select_con_arreglo_fijo($select_naturaleza, array_naturaleza_cta, txt_elemento_cero, elemento_seleccionado, mostrar_opciones)
					
					
					mostrar_opciones=true;
					elemento_seleccionado=entry['Cc'][0]['tipo'];
					txt_elemento_cero='';
					if(parseInt(entry['Cc'][0]['tipo'])==0){
						txt_elemento_cero = '[---------]';
					}
					$carga_select_con_arreglo_fijo($select_tipo_cta, array_tipo_cta, txt_elemento_cero, elemento_seleccionado, mostrar_opciones)
					
					//Carga select de agrupador
					$select_agrupador.children().remove();
					var agrupa_hmtl = '<option value="0">[-----------]</option>';
					$.each(entry['App'],function(entryIndex,agrupa){
						if(parseInt(agrupa['id'])==parseInt(entry['Cc'][0]['agrupa_id'])){
							agrupa_hmtl += '<option value="'+ agrupa['id'] +'" selected="yes">'+ agrupa['titulo'] + '</option>';
						}else{
							agrupa_hmtl += '<option value="'+ agrupa['id'] +'">'+ agrupa['titulo'] + '</option>';
						}
					});
					$select_agrupador.append(agrupa_hmtl);
					
					/*
					$select_nivel.children().remove();
					var nivel_hmtl = '';
					if(parseInt(entry['Cc'][0]['nivel'])==0){
						nivel_hmtl = '<option value="0" selected="yes">[---------]</option>';
					}
					$.each(Param['NivCta'],function(entryIndex,nivel){
						if(parseInt(entry['Cc'][0]['nivel'])==parseInt(nivel['index'])){
							nivel_hmtl += '<option value="' + nivel['index'] + '" selected="yes">'+ nivel['text'] + '</option>';
						}else{
							nivel_hmtl += '<option value="' + nivel['index'] + '"  >'+ nivel['text'] + '</option>';
						}
					});
					$select_nivel.append(nivel_hmtl);
					
					//Carga select de naturaleza de la cuenta
					$select_naturaleza.children().remove();
					var naturaleza_hmtl = '';
					if(parseInt(entry['Cc'][0]['naturaleza'])==0){
						naturaleza_hmtl = '<option value="0" selected="yes">[---------]</option>';
					}
					$.each(Param['NatCta'],function(entryIndex,nat){
						if(parseInt(entry['Cc'][0]['naturaleza'])==parseInt(nat['index'])){
							naturaleza_hmtl += '<option value="' + nat['index'] + '" selected="yes">'+ nat['text'] + '</option>';
						}else{
							naturaleza_hmtl += '<option value="' + nat['index'] + '" >'+ nat['text'] + '</option>';
						}
					});
					$select_naturaleza.append(naturaleza_hmtl);
					
					
					//Carga select de tipo de cuenta
					$select_tipo_cta.children().remove();
					var tipo_hmtl = '';
					if(parseInt(entry['Cc'][0]['tipo'])==0){
						tipo_hmtl = '<option value="0" selected="yes">[---------]</option>';
					}
					$.each(Param['TipoCta'],function(entryIndex,tipo){
						if(parseInt(entry['Cc'][0]['tipo'])==parseInt(tipo['index'])){
							tipo_hmtl += '<option value="' + tipo['index'] + '" selected="yes">'+ tipo['text'] + '</option>';
						}else{
							tipo_hmtl += '<option value="' + tipo['index'] + '"  >'+ tipo['text'] + '</option>';
						}
					});
					$select_tipo_cta.append(tipo_hmtl);
					*/
					
					$chk_cta_detalle.attr('checked',  (entry['Cc'][0]['detalle'] == '1')? true:false );
					
					var estatus_hmtl = '';
					if(entry['Cc'][0]['estatus']=='1'){
						estatus_hmtl += '<option value="1" selected="yes">Activada</option>';
						estatus_hmtl += '<option value="2">Desactivada</option>';
					}
					if(entry['Cc'][0]['estatus']=='2'){
						estatus_hmtl += '<option value="1">Activada</option>';
						estatus_hmtl += '<option value="2" selected="yes">Desactivada</option>';
					}
					//carga select de cuentas de Estatus
					$select_estatus.children().remove();
					$select_estatus.append(estatus_hmtl);
					
					$select_cuenta_mayor.change(function(){
						var valor = $(this).val();
						if(parseInt($select_centro_costo.val())<=0){
							$.each(entry['CtaMay'],function(entryIndex,ctamay){
								if(parseInt(ctamay['id'])==parseInt(valor)){
									$carga_select_centro_costos($select_centro_costo, arrayCentroCostos, ctamay['cta_mayor'], 0);
								}
							});
						}
					});
					
					$cuenta.focus();
				},"json");//termina llamada json
				
				$descripcion.change(function(){
					$descripcion_es.val($(this).val());
				});
				
				$descripcion_es.change(function(){
					$descripcion.val($(this).val());
				});
				
				$('#forma-cuentascontables-window').find('a[href=busca_codigo_sat]').click(function(event){
					event.preventDefault();
					$busca_cta_sat($cta_sat.val(), $desc_cta_sat.val());
				});
				
				$cta_sat.blur(function(){
					if($(this).val().trim()!=''){
						if(parseInt($ctasat_id.val())<=0){
							$busca_datos_cta_sat($cta_sat);
						}
					}
				});
				
				$cta_sat.keypress(function(e){
					if(e.which == 8) {
						$ctasat_id.val(0);
					}
				});
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-cuentascontables-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-cuentascontables-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllCuentasContables.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'ASC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllCuentasContables.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
});



