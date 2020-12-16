$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select tipo de Ajuste
    var arrayTiposAjuste = {
				0:"Positivo", //grupo Entradas en la tabla tipos de movimiento de Invetario
				2:"Negativo",//grupo salidas en la tabla tipos de movimiento de Invetario
			};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invajustes";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $new_ajuste = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Ajustes de Inventario');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_tipo_mov = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_tipo_mov]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
	
	$buscar.mouseover(function(){
		$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
	});
	$buscar.mouseout(function(){
		$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
	});
	
	

	$limpiar.mouseover(function(){
		$(this).removeClass("onmouseOutLimpiar").addClass("onmouseOverLimpiar");
	});
	$limpiar.mouseout(function(){
		$(this).removeClass("onmouseOverLimpiar").addClass("onmouseOutLimpiar");
	});
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "tipo_mov" + signo_separador + $busqueda_tipo_mov.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val() + "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val();
		return valor_retorno;
	};
    
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	
	
	//cargar select tipos de Movimiento del Buscador
	var input_json_mov_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMovTipos.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_mov_tipos,$arreglo,function(data){
		//Llena el select tipos de movimientos en el buscador
		$busqueda_tipo_mov.children().remove();
		var mov_tipos_html = '<option value="0" selected="yes">[-- --]</option>';
		$.each(data['MovTipos'],function(entryIndex,tm){
			mov_tipos_html += '<option value="' + tm['id'] + '"  >' + tm['titulo'] + '</option>';
		});
		$busqueda_tipo_mov.append(mov_tipos_html);
	});
	
	
	
	$buscar.click(function(event){
		//event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	$limpiar.click(function(event){
		$busqueda_folio.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_codigo.val('');
		$busqueda_descripcion.val('');
		
		//cargar select tipos de Movimiento del Buscador
		var input_json_mov_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMovTipos.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_mov_tipos,$arreglo,function(data){
			//Llena el select tipos de movimientos en el buscador
			$busqueda_tipo_mov.children().remove();
			var mov_tipos_html = '<option value="0" selected="yes">[-- --]</option>';
			$.each(data['MovTipos'],function(entryIndex,tm){
				mov_tipos_html += '<option value="' + tm['id'] + '"  >' + tm['titulo'] + '</option>';
			});
			$busqueda_tipo_mov.append(mov_tipos_html);
		});
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
			 $('#barra_buscador').animate({height: '80px'}, 500);
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
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_descripcion, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_tipo_mov, $buscar);
	
	
	
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
	
	
	$busqueda_fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
	$busqueda_fecha_inicial.DatePicker({
		format:'Y-m-d',
		date: $(this).val(),
		current: $(this).val(),
		starts: 1,
		position: 'bottom',
		locale: {
			days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
			daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
			daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
			months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
			monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
			weekMin: 'se'
		},
		onChange: function(formated, dates){
			var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
			$busqueda_fecha_inicial.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($busqueda_fecha_inicial.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$busqueda_fecha_inicial.val(mostrarFecha());
				}else{
					$busqueda_fecha_inicial.DatePickerHide();	
				}
			}
		}
	});
        
	$busqueda_fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
	$busqueda_fecha_final.DatePicker({
		format:'Y-m-d',
		date: $(this).val(),
		current: $(this).val(),
		starts: 1,
		position: 'bottom',
		locale: {
			days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
			daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
			daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
			months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
			monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
			weekMin: 'se'
		},
		onChange: function(formated, dates){
			var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
			$busqueda_fecha_final.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($busqueda_fecha_final.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$busqueda_fecha_final.val(mostrarFecha());
				}else{
					$busqueda_fecha_final.DatePickerHide();	
				}
			}
		}
	});
	
    
	
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-invajustes-window').find('select[name=prodtipo]');
            $('#forma-invajustes-window').find('#submit').mouseover(function(){
                $('#forma-invajustes-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                //$('#forma-invajustes-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
            })
            $('#forma-invajustes-window').find('#submit').mouseout(function(){
                $('#forma-invajustes-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                //$('#forma-invajustes-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
            })
            $('#forma-invajustes-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-invajustes-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-invajustes-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-invajustes-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-invajustes-window').find('#close').mouseover(function(){
                $('#forma-invajustes-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-invajustes-window').find('#close').mouseout(function(){
                $('#forma-invajustes-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-invajustes-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-invajustes-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-invajustes-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-invajustes-window').find("ul.pestanas li").click(function() {
                $('#forma-invajustes-window').find(".contenidoPes").hide();
                $('#forma-invajustes-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-invajustes-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
	$busca_productos = function(id_almacen, fecha){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
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
		
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorProductos.json';
			$arreglo = {	'sku':$campo_sku.val(),
							'tipo':$select_tipo_producto.val(),
							'descripcion':$campo_descripcion.val(),
							'id_almacen':id_almacen,
							'fecha':fecha,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Productos'],function(entryIndex,producto){
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
							trr += '<span class="sku_prod_buscador">'+producto['codigo']+'</span>';
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
					$('#forma-invajustes-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-invajustes-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-invajustes-window').find('input[name=sku_producto]').focus();
				});
				
			});//termina llamada json
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
	
	$aplicar_evento_focus_input = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val() == ' ' || parseFloat($(this).val()) <= 0){
				$(this).val('');
			}
		});
	}
	
	$aplicar_evento_blur_input = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if(parseFloat($campo_input.val())==0 || $campo_input.val()==""){
				$campo_input.val(0);
				$campo_input.val(parseFloat($campo_input.val()).toFixed(2))
			}
			
			//Esto es para controlar la suma del costo cuando cambie el costo del ajuste
			var $this_tr = $campo_input.parent().parent();
			var $cantUniAjuste = $this_tr.find('input[name=cant_ajuste]');
			var $costo_ajuste = $this_tr.find('input[name=costo_ajuste]');
			var $tdSumaTotalCostoAjuste = $('#forma-invajustes-window').find('#suma_total_costo_ajuste');
			var $tdCostoAjustePartida = $this_tr.find('td:eq(11)');
			var valorCostoAjustePartidaAnterior = quitar_comas($tdCostoAjustePartida.html());
			
			//Calcular el nuevo costo del Ajuste
			var valorCostoAjustePartidaNuevo = parseFloat(parseFloat($cantUniAjuste.val())*parseFloat($costo_ajuste.val())).toFixed(2);
			
			//Asignar el nuevo costo de la partida
			$tdCostoAjustePartida.html($(this).agregar_comas(valorCostoAjustePartidaNuevo));
			
			//Restar el costo anterior de la partida y sumar el nuevo costo
			$tdSumaTotalCostoAjuste.html($(this).agregar_comas(parseFloat(parseFloat(quitar_comas($tdSumaTotalCostoAjuste.html())) - parseFloat(valorCostoAjustePartidaAnterior) + parseFloat(valorCostoAjustePartidaNuevo)).toFixed(2)));
			
		});
	}
	
	
	
	
	$aplicar_evento_focus = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
	}
	
	
	$aplicar_evento_blur = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if($campo_input.val()=='0' || $campo_input.val()==""){
				$campo_input.val(' ');
			}
		});
	}
	
	//funcion para aplicar metodo click para eliminar tr
	$aplicar_evento_eliminar = function( $campo_href ){
		//eliminar un lote
		$campo_href.click(function(e){
			e.preventDefault();
			$tr_padre=$(this).parent().parent();
			var $tdSumaTotalCostoAjuste = $('#forma-invajustes-window').find('#suma_total_costo_ajuste');
			var valorCostoAjustePartidaAnterior = quitar_comas($tr_padre.find('td:eq(11)').html());
			//$tr_padre.find('input').val('');//asignar vacio a todos los input del tr
			//$tr_padre.find('input[name=eliminado]').val('0');//asignamos 0 para indicar que se ha eliminado
			
			//Restar de la suma total de costos el importe del costo de la partida
			$tdSumaTotalCostoAjuste.html($(this).agregar_comas(parseFloat(parseFloat(quitar_comas($tdSumaTotalCostoAjuste.html())) - parseFloat(valorCostoAjustePartidaAnterior)).toFixed(2)));
			
			$tr_padre.remove();//eliminar el tr
			
			//$grid_productos = $tr_padre.parent();
		});
	}
	
	
	
	//generar tr para agregar al grid
	$genera_tr = function(noTr, id_producto, codigo, descripcion, unidad, id_almacen, existencia,costo_prom, cant_ajuste, costo_ajuste, tipo_costo, idPres, equivPres, exisPres, ajustePres){
		var readOnly=''//esta variable indica si el campo costo ajuste va a ser editable o no de pendiendo del tipo de costo que se utiliza para el tipo de movimiento
		var fondo_input="";
		var $tdSumaTotalCostoAjuste = $('#forma-invajustes-window').find('#suma_total_costo_ajuste');
		var valorCostoAjustePartida = parseFloat(parseFloat(cant_ajuste)*parseFloat(costo_ajuste)).toFixed(2);
		
		//Sumar el costo de la partida
		$tdSumaTotalCostoAjuste.html($(this).agregar_comas(parseFloat(parseFloat(quitar_comas($tdSumaTotalCostoAjuste.html())) + parseFloat(valorCostoAjustePartida)).toFixed(2)));
		
		//0=Alimentado
		if(parseInt(tipo_costo)==0){
			readOnly='';
			fondo_input='';
		}
		
		//1=Promedio
		if(parseInt(tipo_costo)==1){
			readOnly='readOnly="true"';
			fondo_input='background:#dddddd';
		}
		
		var trr = '';
		trr = '<tr>';
			trr += '<td class="grid" style="font-size:11px;  border:1px solid #C1DAD7;" width="25">';
				//trr += '<a href="elimina_producto" class="delete'+ noTr +'">Eliminar</a>';
				trr += '<a href="elimina_producto" class="delete'+ noTr +'"><div id="eliminar'+ noTr +'" class="onmouseOutEliminar" style="width:24px; background-position:center;"/></a>';
				trr += '<input type="hidden" 	name="eliminado" value="1">';//el 1 significa que el registro no ha sido eliminado
				trr += '<input type="hidden" 	name="id_almacen" value="'+ id_almacen +'">';
				trr += '<input type="hidden" 	name="no_tr" value="'+ noTr +'">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="110">';
				trr += '<input type="hidden" 	name="idproducto" id="idprod" value="'+ id_producto +'">';
				trr += '<INPUT TYPE="text" 		name="codigo" value="'+ codigo +'" class="borde_oculto" readOnly="true" style="width:106px;">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="180">';
				trr += '<INPUT TYPE="text" 		name="nombre" 	value="'+ descripcion +'" class="borde_oculto" readOnly="true" style="width:176px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="90">';
				trr += '<INPUT TYPE="text" 		name="unidad" 	value="'+ unidad +'" class="borde_oculto" readOnly="true" style="width:86px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="100">';
				trr += '<input type="hidden" 	name="equivPres" value="'+ equivPres +'">';
				trr += '<input type="hidden" 	name="idPresSelec" id="idPresSelec" value="'+idPres+'">';
				trr += '<select name="select_pres" class="select_pres'+ noTr +'" style="width:96px;"></select>';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="80">';
				trr += '<INPUT TYPE="text" 		name="cantidad" value="'+$(this).agregar_comas(existencia)+'" class="borde_oculto" readOnly="true" style="width:76px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="75">';
				trr += '<INPUT TYPE="text" 		name="exisPres" value="'+$(this).agregar_comas(exisPres)+'" class="borde_oculto" readOnly="true" style="width:71px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="75">';
				trr += '<INPUT TYPE="text" 		name="costo_prom" value="'+$(this).agregar_comas(costo_prom)+'" class="borde_oculto" readOnly="true" style="width:71px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="70">';
				trr += '<INPUT TYPE="text" 		name="cant_ajuste" value="'+cant_ajuste+'" class="cant_ajuste'+noTr+'" style="width:67px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="75">';
				trr += '<INPUT TYPE="text" 		name="cantAjustePres" value="'+ajustePres+'" id="cantAjustePres'+noTr+'" class="borde_oculto" style="width:71px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="65">';
				trr += '<INPUT TYPE="text" 		name="costo_ajuste" value="'+costo_ajuste+'" '+readOnly+' class="costo_ajuste'+noTr+'" style="width:62px; text-align:right; '+fondo_input+'">';
			trr += '</td>';
			trr += '<td class="grid2" style="font-size:11px;  border:1px solid #C1DAD7;" width="65">'+$(this).agregar_comas(valorCostoAjustePartida)+'</td>';
		trr += '</tr>';
		
		return trr;
	}
	

	//convertir la Presentacion en Unidad de acuerdo a la Equivalencia
	$convertirPresAUni = function(idPres, cantPres, arrayPres){
		var valor=0;
		$.each(arrayPres,function(entryIndex,pres){
			if(parseInt(pres['id'])==parseInt(idPres)){
				valor = parseFloat(cantPres) * parseFloat(pres['equiv']);
			}
		});
		return valor;
	};
	
	
	
	
	//convertir la Cantidad de Unidades en cantidad de Presentaciones
	$convertirUniAPres = function(idPres, cantUni, arrayPres){
		var valor=0;
		//alert("idPres:"+idPres+"\ncantUni:"+cantUni);
		$.each(arrayPres,function(entryIndex,pres){
			if(parseInt(pres['id'])==parseInt(idPres)){
				valor = parseFloat(cantUni) / parseFloat(pres['equiv']);
			}
		});
		return valor;
	};
	
	
	
	//busca el almacen y presentacion en el Grid, esto para evitar que se repitan
	var $buscarRegistroProductoPresentacion = function($grid_productos, idProd, idPres, notr){
		var encontrado=0;
		//busca el codigo del producto y Prresentacion en el grid
		$grid_productos.find('tr').each(function (index){
			var $regEliminado = $(this).find('input[name=eliminado]');
			var $idProducto = $(this).find('input[name=idproducto]');
			var $selectPres = $(this).find('select[name=select_pres]');
			var $noTr = $(this).find('input[name=no_tr]');
			
			if(parseInt($regEliminado.val())!=0){
				if( (parseInt($idProducto.val())==parseInt(idProd))  &&  (parseInt($selectPres.val())==parseInt(idPres))){
					if(parseInt($noTr.val())!=parseInt(notr)){
						encontrado++;
					}
				}
			}
		});
		return encontrado;
	}
	
	
	//aplicar evento change al select de Presentaciones que esta en el Grid
	$aplicarEventoChangeSelectPres = function($select_pres, idAlmOrigen, arregloPres){
		//cambiar presentacion
		$select_pres.change(function(){
			var idPres = $(this).val();
			var $this_tr = $(this).parent().parent();
			$grid_productos = $(this).parent().parent().parent();
			var idProd = $this_tr.find('input[name=idproducto]').val();
			var notr = $this_tr.find('input[name=no_tr]').val();
			
			$exisPresTr = $this_tr.find('input[name=exisPres]');
			$exisUniTr = $this_tr.find('input[name=cantidad]');
			$presIdSelecTr = $this_tr.find('input[name=idPresSelec]');
			$cantAjustePresTr = $this_tr.find('input[name=cantAjustePres]');
			$cantAjusteUniTr = $this_tr.find('input[name=cant_ajuste]');
			
			//asignar valores en cero
			$exisPresTr.val(parseFloat(0).toFixed(2));
			$exisUniTr.val(parseFloat(0).toFixed(2));
			$cantAjustePresTr.val(parseFloat(0).toFixed(2));
			$cantAjusteUniTr.val(parseFloat(0).toFixed(2));
			
			if(parseInt(idPres) > 0 ){
				//buscar existencia del producto y la presentacion en el Grid, esto para evitar que se repita
				var exisProdPres = $buscarRegistroProductoPresentacion($grid_productos, idProd, idPres, notr);
				
				if(parseInt(exisProdPres)<=0){
					//buscar existencias al seleccionar una presentacion
					var input_json3 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getExisPres.json';
					var $arreglo3 = {'id_prod':idProd, 'id_pres':idPres, 'id_alm':idAlmOrigen, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					
					$.post(input_json3,$arreglo3,function(entry3){
						if(parseInt(entry3['Existencia'].length) > 0 ){
							var exisPres = entry3['Existencia'][0]['exis'];
							var noDec = entry3['Existencia'][0]['no_dec'];
							
							//convertir la Cantidad de la Presentacion en cantidad de Unidades
							var exisUnidad = $convertirPresAUni(idPres, exisPres, arregloPres);
							
							//asignar la existencia en Presentaciones
							$exisPresTr.val(parseFloat(exisPres).toFixed(noDec));
							
							//asignar la existencia en Unidades
							$exisUniTr.val(parseFloat(exisUnidad).toFixed(noDec));
							
							//agregar comas
							$exisPresTr.val($(this).agregar_comas($exisPresTr.val()));
							$exisUniTr.val($(this).agregar_comas($exisUniTr.val()));
							
						}else{
							//asignar la existencias en Cero
							$exisPresTr.val(parseFloat(0.0).toFixed(noDec));
							$exisUniTr.val(parseFloat(0.0).toFixed(noDec));
							$presIdSelecTr.val(0);
							
							jAlert('No hay existencias en &eacute;sta Presentaci&oacute;n.', 'Atencion!', function(r) { 
								$select_pres.focus();
							});
						}
					});
					
					$presIdSelecTr.val(idPres);
					
				}else{
					//aqui entra porque el producto y la presentacion ya estan en el GRID
					jAlert('No se puede seleccionar &eacute;sta Presentaci&oacute;n para &eacute;ste Producto, ya existe un registro igual.', 'Atencion!', function(r) { 
						var html_select='';
						$select_pres.find('option').each(function(){
							if(parseInt($(this).val())==parseInt($presIdSelecTr.val())){
								html_select += '<option value="' + $(this).val() + '" selected="yes">' + $(this).text() + '</option>';
							}else{
								html_select += '<option value="' + $(this).val() + '"  >' + $(this).text() + '</option>';
							}
						});
						
						$select_pres.children().remove();
						$select_pres.append(html_select);
						$select_pres.focus();
					});
				}
			}else{
				jAlert('Es necesario seleccionar una Presentaci&oacute;n para realizar el traspaso.', 'Atencion!', function(r) { 
					$presIdSelecTr.val(idPres);
					$select_pres.focus();
				});
			}
		});
	}
	
	
	
	//funcion para aplicar evento Blur 
	$aplicar_evento_blur_campo_cantidad = function( $campo_input, noDec, arrayPres, controlExisPres, $select_tipo_ajuste){
		var $this_tr = $campo_input.parent().parent();
		
		$campo_input.blur(function(e){
			var idPres = $this_tr.find('select[name=select_pres]').val();
			var $cantUniAjuste = $this_tr.find('input[name=cant_ajuste]');
			var $cantPresAjuste = $this_tr.find('input[name=cantAjustePres]');
			var cantUniExis = quitar_comas($this_tr.find('input[name=cantidad]').val());
			var $costo_ajuste = $this_tr.find('input[name=costo_ajuste]');
			var $tdSumaTotalCostoAjuste = $('#forma-invajustes-window').find('#suma_total_costo_ajuste');
			var $tdCostoAjustePartida = $this_tr.find('td:eq(11)');
			var valorCostoAjustePartidaAnterior = quitar_comas($tdCostoAjustePartida.html());
			
			if($cantUniAjuste.val().trim()=='' || parseFloat($campo_input.val())==0 ){
				$campo_input.val(0);
				$campo_input.val(parseFloat($campo_input.val()).toFixed(2))
			}else{
				//si la configuracion indica que debe controlar existencias por presentacion hay que realizar los calculos
				if(controlExisPres=='true'){
					
					//convertir las Unidades en Presentaciones
					var exisPres = $convertirUniAPres(idPres, $cantUniAjuste.val(), arrayPres);
					
					if(parseInt($select_tipo_ajuste.val())==2){
						//aqui entra cuando es ajuste Negativo
						if(parseFloat($cantUniAjuste.val()) > parseFloat(cantUniExis)){
							jAlert('No es posible realizar un Ajuste mayor a la Existencia de la Presentaci&oacute;n.', 'Atencion!', function(r) { 
								$cantUniAjuste.val(parseFloat(0).toFixed(noDec));
								$cantPresAjuste.val(parseFloat(0).toFixed(noDec));
							});
						}else{
							$cantPresAjuste.val(parseFloat(exisPres).toFixed(noDec));
						}
					}else{
						//aqui entra cuando es ajuste Positivo
						$cantPresAjuste.val(parseFloat(exisPres).toFixed(noDec));
					}
				}else{
					$cantPresAjuste.val(parseFloat(0).toFixed(noDec));
				}
			}
			
			if($costo_ajuste.val().trim()==""){
				$costo_ajuste.val("0.00");
			}
			
			
			//Calcular el nuevo costo del Ajuste
			var valorCostoAjustePartidaNuevo = parseFloat(parseFloat($cantUniAjuste.val())*parseFloat($costo_ajuste.val())).toFixed(2);
			
			//Asignar el nuevo costo de la partida
			$tdCostoAjustePartida.html($(this).agregar_comas(valorCostoAjustePartidaNuevo));
			
			//Restar el costo anterior de la partida y sumar el nuevo costo
			$tdSumaTotalCostoAjuste.html($(this).agregar_comas(parseFloat(parseFloat(quitar_comas($tdSumaTotalCostoAjuste.html())) - parseFloat(valorCostoAjustePartidaAnterior) + parseFloat(valorCostoAjustePartidaNuevo)).toFixed(2)));
		});
	}
        
	
	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_datos_producto = function($grid_productos, codigo_producto, id_alm,fecha, tipo_costo, controlExisPres, $select_tipo_ajuste){
		//verifica si el campo sku no esta vacio para realizar busqueda
		if(codigo_producto != ''){
			
			var encontrado = 0;
			/*
			//busca el sku y la presentacion en el grid
			$grid_productos.find('tr').each(function (index){
				if(($(this).find('input[name=codigo]').val() == codigo_producto.toUpperCase()) && (parseInt($(this).find('input[name=eliminado]').val())!=0) ){
					encontrado=1;//el producto ya esta en el grid
				}
			});
			*/
			
			
			if( parseInt(encontrado)==0 ){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosProducto.json';
				$arreglo = {'sku':codigo_producto,
							'id_almacen':id_alm,
							'fecha':fecha,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var trr = '';
				
				$.post(input_json,$arreglo,function(entry){
					//verifica si el arreglo retorno datos
					if (entry['Producto'].length > 0){
						var noTr = $("tr", $grid_productos).size();
						noTr++;
						
						var id_producto = entry['Producto'][0]['id_producto'];
						var codigo = entry['Producto'][0]['codigo'];
						var descripcion = entry['Producto'][0]['descripcion'];
						var unidad = entry['Producto'][0]['unidad'];
						var id_almacen = entry['Producto'][0]['id_almacen'];
						var existencia = entry['Producto'][0]['existencia'];
						var costo_prom = entry['Costo'][0]['costo_promedio'];
						var noDec=entry['Producto'][0]['no_dec'];
						var cant_ajuste='0.00';
						var costo_ajuste=entry['Costo'][0]['costo_promedio'];
						
						var idPres=0;
						var equivPres=0;
						var exisPres=0;
						var ajustePres=0;
						
						//idPres, equivPres, exisPres, ajustePres
						
						var cadena_tr = $genera_tr(noTr, id_producto, codigo, descripcion, unidad, id_almacen, existencia, costo_prom, cant_ajuste, costo_ajuste, tipo_costo, idPres, equivPres, exisPres, ajustePres);
						
						$grid_productos.append(cadena_tr);
						
						$aplicar_evento_keypress($grid_productos.find('.cant_ajuste'+noTr));
						$aplicar_evento_focus_input($grid_productos.find('.cant_ajuste'+noTr));
						//$aplicar_evento_blur_input($grid_productos.find('.cant_ajuste'+noTr));
						
						$aplicar_evento_keypress($grid_productos.find('.costo_ajuste'+noTr));
						$aplicar_evento_focus_input($grid_productos.find('.costo_ajuste'+noTr));
						$aplicar_evento_blur_input($grid_productos.find('.costo_ajuste'+noTr));
						$aplicar_evento_eliminar($grid_productos.find('.delete'+noTr));
						
						$grid_productos.find('#eliminar'+ noTr).mouseover(function(){
							$(this).removeClass("onmouseOutEliminar").addClass("onmouseOverEliminar");
						});
						$grid_productos.find('#eliminar'+ noTr).mouseout(function(){
							$(this).removeClass("onmouseOverEliminar").addClass("onmouseOutEliminar");
						});
						
						
						$grid_productos.find('.select_pres'+noTr).children().remove();
						var pres_hmtl = '<option value="0">[-Seleccionar-]</option>';
						$.each(entry['Presentaciones'],function(entryIndex,pres){
							pres_hmtl += '<option value="' + pres['id'] + '">' + pres['titulo'] + '</option>';
						});
						$grid_productos.find('.select_pres'+noTr).append(pres_hmtl);
						
						if (controlExisPres=='true'){
							//aplicar evento al cambiar presentacion
							$aplicarEventoChangeSelectPres($grid_productos.find('.select_pres'+noTr), id_alm, entry['Presentaciones'], $select_tipo_ajuste);
						}
						
						$aplicar_evento_blur_campo_cantidad( $grid_productos.find('.cant_ajuste'+noTr), noDec, entry['Presentaciones'], controlExisPres, $select_tipo_ajuste);
						
						
						
					}else{
						jAlert("El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
						$('#forma-invajustes-window').find('input[name=titulo_producto]').val('');
					}
				});
			}else{
				jAlert("El producto que intenta Agregar ya se encuentra en el listado.", 'Atencion!');
			}
		}else{
			jAlert("Es necesario ingresar un C&oacute;digo de producto valido", 'Atencion!');
		}
	
	}//termina buscador de datos del producto
	
    
	
	
	
	
	
	
	
	
	
	//nuevo ajuste
	$new_ajuste.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_invajustes();
		
		var form_to_show = 'formainvajustes00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-invajustes-window').css({"margin-left": -410, 	"margin-top": -220});
		
		$forma_selected.prependTo('#forma-invajustes-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAjuste.json';
		$arreglo = {'identificador':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
		var $identificador = $('#forma-invajustes-window').find('input[name=identificador]');
		var $folio = $('#forma-invajustes-window').find('input[name=folio]');
		var $exis_pres = $('#forma-invajustes-window').find('input[name=exis_pres]');
		
		var $select_tipo_ajuste = $('#forma-invajustes-window').find('select[name=select_tipo_ajuste]');
		var $tipo_ajuste = $('#forma-invajustes-window').find('input[name=tipo_ajuste]');
		
		var $fecha_ajuste = $('#forma-invajustes-window').find('input[name=fecha_ajuste]');
		var $select_tipo_mov = $('#forma-invajustes-window').find('select[name=select_tipo_mov]');
		var $id_tipo_mov = $('#forma-invajustes-window').find('input[name=id_tipo_mov]');
		
		var $tipo_costo = $('#forma-invajustes-window').find('input[name=tipo_costo]');
		var $select_almacen = $('#forma-invajustes-window').find('select[name=select_almacen]');
		var $id_almacen = $('#forma-invajustes-window').find('input[name=id_almacen]');
		
		var $observaciones = $('#forma-invajustes-window').find('textarea[name=observaciones]');
		
		var $sku_producto = $('#forma-invajustes-window').find('input[name=sku_producto]');
		var $nombre_producto = $('#forma-invajustes-window').find('input[name=nombre_producto]');
		
		//buscar producto
		var $busca_sku = $('#forma-invajustes-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-invajustes-window').find('a[href*=agregar_producto]');
		var $descargarpdf = $('#forma-invajustes-window').find('#descargarpdf');
		
		//grid de productos
		var $grid_productos = $('#forma-invajustes-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning');
		
		
		var $cerrar_plugin = $('#forma-invajustes-window').find('#close');
		var $cancelar_plugin = $('#forma-invajustes-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invajustes-window').find('#submit');
		
		//$campo_factura.css({'background' : '#ffffff'});
		
		//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
		$descargarpdf.attr('disabled','-1');
		$identificador.val(0);//para nueva pedido el id es 0
		
		$folio.css({'background' : '#F0F0F0'});
		$select_tipo_ajuste.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Ajuste se guard&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-invajustes-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				//habilitar boton actualizar
				$submit_actualizar.removeAttr('disabled');
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invajustes-window').find('.invajustes_div_one').css({'height':'550px'});//con errores
				$('#forma-invajustes-window').find('div.interrogacion').css({'display':'none'});
				$grid_productos.find('input[name=cant_ajuste]').css({'background' : '#ffffff'});
				$grid_productos.find('input[name=costo_ajuste]').css({'background' : '#ffffff'});
				
				$('#forma-invajustes-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-invajustes-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						var campo = tmp.split(':')[0];
						
						$('#forma-invajustes-window').find('#div_warning_grid').css({'display':'block'});
						var $campo = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
						
						var codigo_producto = $campo.parent().parent().find('input[name=codigo]').val();
						var titulo_producto = $campo.parent().parent().find('input[name=nombre]').val();
						
						var tr_warning = '<tr>';
								tr_warning += '<td width="10"></td>';
								tr_warning += '<td width="25"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
								tr_warning += '<td width="120"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:116px; color:red"></td>';
								tr_warning += '<td width="220"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:216px; color:red"></td>';
								tr_warning += '<td width="500"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:496px; color:red"></td>';
								tr_warning += '<td width="10"></td>';
						tr_warning += '</tr>';
						
						$('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						
					}
				}
				$('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
				$('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			
			$fecha_ajuste.val(entry['AnoActual'][0]['fecha_actual']);
			$exis_pres.val(entry['Par'][0]['exis_pres']);
			
			$select_tipo_ajuste.children().remove();
			var select_html = '';
			for(var i in arrayTiposAjuste){
				select_html += '<option value="' + i + '" >' + arrayTiposAjuste[i] + '</option>';
			}
			$select_tipo_ajuste.append(select_html);
			
			//guardamos el tipo de ajuste
			$tipo_ajuste.val($select_tipo_ajuste.val());
			
			
			//carga select con los tipos de Movimientos
			$select_tipo_mov.children().remove();
			var tmov_hmtl = '';
			$.each(entry['TMov'],function(entryIndex,mov){
				if(parseInt($select_tipo_ajuste.val())==parseInt(mov['grupo'])){
					tmov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
				}
			});
			$select_tipo_mov.append(tmov_hmtl);
			$id_tipo_mov.val($select_tipo_mov.val());
			
			
			//tomar el tipo de Costo que se utilizara de acuerdo al tipo de movimiento selecionado
			//el tipo de costo en la vista define si el campo costo ajuste debe estar habilitado para edicion o no.
			$.each(entry['TMov'],function(entryIndex,mov){
				if(parseInt($select_tipo_mov.val())==parseInt(mov['id'])){
					$tipo_costo.val(mov['tipo_costo']);
				}
			});
			
			
			//carga select con todos los Almacenes de la Empresa
			$select_almacen.children().remove();
			var almacen_hmtl = '';
			$.each(entry['Almacenes'],function(entryIndex,almacen){
				almacen_hmtl += '<option value="' + almacen['id'] + '"  >' + almacen['titulo'] + '</option>';
			});
			$select_almacen.append(almacen_hmtl);
			$id_almacen.val($select_almacen.val());
			
			
			//cambiar tipo de ajuste
			$select_tipo_ajuste.change(function(){
				var valor_tipo = $(this).val();
				var valor_tipo_anterior = $tipo_ajuste.val();//tomamos el valor anterior
				
				//0:"Positivo", //grupo Entradas en la tabla tipos de movimiento de Invetario
				//2:"Negativo",//grupo salidas en la tabla tipos de movimiento de Invetario
				
				if (parseInt($("tr", $grid_productos).size()) > 0 ){
					//aqui regresamos el tipo seleccionado anteriormente porque hay productos en el grid
					$select_tipo_ajuste.children().remove();
					var select_html = '';
					for(var i in arrayTiposAjuste){
						if(valor_tipo_anterior == i){
							select_html += '<option value="' + i + '" selected="yes">' + arrayTiposAjuste[i] + '</option>';
						}else{
							select_html += '<option value="' + i + '">' + arrayTiposAjuste[i] + '</option>';
						}
					}
					$select_tipo_ajuste.append(select_html);
					jAlert("No es posible cambiar el Tipo de Ajuste mientras existan productos en el listado.", 'Atencion!');
				}else{
					$select_tipo_mov.children().remove();
					var tmov_hmtl = '';
					$.each(entry['TMov'],function(entryIndex,mov){
						if(parseInt(valor_tipo)==parseInt(mov['grupo'])){
							tmov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
							$tipo_costo.val(mov['tipo_costo']);
						}
					});
					$select_tipo_mov.append(tmov_hmtl);
					$tipo_ajuste.val(valor_tipo);//tomamos el valor del nuevo tipo seleccionado
					$id_tipo_mov.val($select_tipo_mov.val());
				}
			});
			
			
			//cambiar el tipo de Movimiento
			$select_tipo_mov.change(function(){
				var valor_tipo = $(this).val();
				var valor_tipo_mov_anterior = $id_tipo_mov.val();//tomamos el valor anterior
				
				if (parseInt($("tr", $grid_productos).size()) > 0 ){
					$select_tipo_mov.children().remove();
					var tmov_hmtl = '';
					$.each(entry['TMov'],function(entryIndex,mov){
						if(parseInt($select_tipo_ajuste.val())==parseInt(mov['grupo'])){
							if(parseInt(valor_tipo_mov_anterior)==parseInt(mov['id'])){
								tmov_hmtl += '<option value="' + mov['id'] + '" selected="yes">' + mov['titulo'] + '</option>';
							}else{
								tmov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
							}
						}
					});
					$select_tipo_mov.append(tmov_hmtl);
					
					jAlert("No es posible cambiar el Tipo de Movimiento mientras existan productos en el listado.", 'Atencion!');
				}else{
					$id_tipo_mov.val(valor_tipo);//guardamos el nuevo tipo seleccionado
					
					//tomar el tipo de Costo que se utilizara de acuerdo al tipo de movimiento selecionado
					//el tipo de costo en la vista define si el campo costo ajuste debe estar habilitado para edicion o no.
					$.each(entry['TMov'],function(entryIndex,mov){
						if(parseInt($select_tipo_mov.val())==parseInt(mov['id'])){
							$tipo_costo.val(mov['tipo_costo']);
						}
					});
					
					
					
				}
			});
			
			
			
			
			
			//cambiar el almacen
			$select_tipo_mov.change(function(){
				var valor_tipo = $(this).val();
				var valor_tipo_mov_anterior = $id_tipo_mov.val();//tomamos el valor anterior
				
				if (parseInt($("tr", $grid_productos).size()) > 0 ){
					$select_tipo_mov.children().remove();
					var tmov_hmtl = '';
					$.each(entry['TMov'],function(entryIndex,mov){
						if(parseInt($select_tipo_ajuste.val())==parseInt(mov['grupo'])){
							if(parseInt(valor_tipo_mov_anterior)==parseInt(mov['id'])){
								tmov_hmtl += '<option value="' + mov['id'] + '" selected="yes">' + mov['titulo'] + '</option>';
							}else{
								tmov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
							}
						}
					});
					$select_tipo_mov.append(tmov_hmtl);
					
					jAlert("No es posible cambiar el Tipo de Movimiento mientras existan productos en el listado.", 'Atencion!');
					
				}else{
					$id_tipo_mov.val(valor_tipo);//guardamos el nuevo tipo seleccionado
				}
			});
			
			
			
			//cambiar el almacen
			$select_almacen.change(function(){
				var valor_alm = $(this).val();
				var valor_alm_anterior = $id_almacen.val();//tomamos el valor anterior
				
				if (parseInt($("tr", $grid_productos).size()) > 0 ){
					$select_almacen.children().remove();
					var almacen_hmtl = '';
					$.each(entry['Almacenes'],function(entryIndex,almacen){
						if(parseInt(valor_alm_anterior)==parseInt(almacen['id'])){
							almacen_hmtl += '<option value="' + almacen['id'] + '" selected="yes">' + almacen['titulo'] + '</option>';
						}else{
							almacen_hmtl += '<option value="' + almacen['id'] + '"  >' + almacen['titulo'] + '</option>';
						}
					});
					$select_almacen.append(almacen_hmtl);
					jAlert("No es posible cambiar el Tipo de Movimiento mientras existan productos en el listado.", 'Atencion!');
				}else{
					$id_almacen.val(valor_alm);//guardamos el nuevo id de almacen seleccionado
				}
			});
			
			
			
			//agregar producto al grid
			$agregar_producto.click(function(event){
				event.preventDefault();
				var codigo_producto = $sku_producto.val();
				var id_alm = $select_almacen.val();
				var fecha = $fecha_ajuste.val();
				var tipo_costo = $tipo_costo.val();
				var exisPres = $exis_pres.val();
				
				//alert("tipo_costo:"+$tipo_costo.val());
				$buscador_datos_producto($grid_productos, codigo_producto, id_alm, fecha, tipo_costo, exisPres, $select_tipo_ajuste);
				
			});
			
			
		},"json");//termina llamada json
		
		
		
		
		//buscador de productos
		$busca_sku.click(function(event){
			event.preventDefault();
			$busca_productos($select_almacen.val(), $fecha_ajuste.val());
		});
		
		
		//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$sku_producto.keypress(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
		
		
		
		//deshabilitar tecla enter  en todo el plugin
		$('#forma-invajustes-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		
				
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert("No hay productos para ajuste.", 'Atencion!');
				return false;
			}
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invajustes-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invajustes-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	var carga_formainvajustes00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'identificador':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  la factura?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La factura fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}else{
							jAlert("La factura no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-invajustes-window').remove();
			$('#forma-invajustes-overlay').remove();
            
			var form_to_show = 'formainvajustes00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_invajustes();
			
			$('#forma-invajustes-window').css({"margin-left": -410, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-invajustes-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAjuste.json';
			$arreglo = {'identificador':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
        
			
			var $identificador = $('#forma-invajustes-window').find('input[name=identificador]');
			var $folio = $('#forma-invajustes-window').find('input[name=folio]');
			var $exis_pres = $('#forma-invajustes-window').find('input[name=exis_pres]');
			
			var $select_tipo_ajuste = $('#forma-invajustes-window').find('select[name=select_tipo_ajuste]');
			var $tipo_ajuste = $('#forma-invajustes-window').find('input[name=tipo_ajuste]');
			
			var $fecha_ajuste = $('#forma-invajustes-window').find('input[name=fecha_ajuste]');
			var $select_tipo_mov = $('#forma-invajustes-window').find('select[name=select_tipo_mov]');
			var $id_tipo_mov = $('#forma-invajustes-window').find('input[name=id_tipo_mov]');
			
			var $tipo_costo = $('#forma-invajustes-window').find('input[name=tipo_costo]');
			var $select_almacen = $('#forma-invajustes-window').find('select[name=select_almacen]');
			var $id_almacen = $('#forma-invajustes-window').find('input[name=id_almacen]');
			
			var $observaciones = $('#forma-invajustes-window').find('textarea[name=observaciones]');
			
			var $sku_producto = $('#forma-invajustes-window').find('input[name=sku_producto]');
			var $nombre_producto = $('#forma-invajustes-window').find('input[name=nombre_producto]');
			
			//buscar producto
			var $busca_sku = $('#forma-invajustes-window').find('a[href*=busca_sku]');
			//href para agregar producto al grid
			var $agregar_producto = $('#forma-invajustes-window').find('a[href*=agregar_producto]');
			var $descargarpdf = $('#forma-invajustes-window').find('#descargarpdf');
			
			//grid de productos
			var $grid_productos = $('#forma-invajustes-window').find('#grid_productos');
			//grid de errores
			var $grid_warning = $('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning');
			
			
			var $cerrar_plugin = $('#forma-invajustes-window').find('#close');
			var $cancelar_plugin = $('#forma-invajustes-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invajustes-window').find('#submit');
			
			//$campo_factura.css({'background' : '#ffffff'});
			
			//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
			//$boton_descargarpdf.hide();
			$identificador.val(0);//para nueva pedido el id es 0
			$fecha_ajuste.val(mostrarFecha());
			
			
			//$permitir_solo_numeros($no_cuenta);
			//$no_cuenta.attr('disabled','-1');
			//$etiqueta_digit.attr('disabled','-1');
			
			$busca_sku.hide();
			$agregar_producto.hide();
			$submit_actualizar.hide();
			
			$folio.css({'background' : '#F0F0F0'});
			
			var respuestaProcesada = function(data){
				if ( data['success'] == "true" ){
					jAlert("El Ajuste se guard&oacute; con &eacute;xito", 'Atencion!');
					var remove = function() {$(this).remove();};
					$('#forma-invajustes-overlay').fadeOut(remove);
					$get_datos_grid();
				}else{
					//habilitar boton actualizar
					$submit_actualizar.removeAttr('disabled');
					// Desaparece todas las interrogaciones si es que existen
					$('#forma-invajustes-window').find('.invajustes_div_one').css({'height':'550px'});//con errores
					$('#forma-invajustes-window').find('div.interrogacion').css({'display':'none'});
					$grid_productos.find('input[name=cant_ajuste]').css({'background' : '#ffffff'});
					$grid_productos.find('input[name=costo_ajuste]').css({'background' : '#ffffff'});
					
					$('#forma-invajustes-window').find('#div_warning_grid').css({'display':'none'});
					$('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning').children().remove();
					
					var valor = data['success'].split('___');
					//muestra las interrogaciones
					for (var element in valor){
						tmp = data['success'].split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
							$('#forma-invajustes-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							
							var campo = tmp.split(':')[0];
							
							$('#forma-invajustes-window').find('#div_warning_grid').css({'display':'block'});
							var $campo = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
							
							var codigo_producto = $campo.parent().parent().find('input[name=codigo]').val();
							var titulo_producto = $campo.parent().parent().find('input[name=nombre]').val();
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="10"></td>';
									tr_warning += '<td width="25"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="120"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:116px; color:red"></td>';
									tr_warning += '<td width="220"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:216px; color:red"></td>';
									tr_warning += '<td width="500"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:496px; color:red"></td>';
									tr_warning += '<td width="10"></td>';
							tr_warning += '</tr>';
							
							$('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
					}
					$('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
					$('#forma-invajustes-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
				}
			
			}		
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.val(entry['Datos']['0']['id']);
					$folio.val(entry['Datos']['0']['folio_ajuste']);
					$fecha_ajuste.val(entry['Datos']['0']['fecha_ajuste']);
					$observaciones.text(entry['Datos']['0']['observacion']);
					
					
					$select_tipo_ajuste.children().remove();
					var select_html = '';
					for(var i in arrayTiposAjuste){
						if(parseInt(entry['Datos']['0']['tipo_ajuste'])==i){
							select_html += '<option value="' + i + '" selected="yes">' + arrayTiposAjuste[i] + '</option>';
						}else{
							//select_html += '<option value="' + i + '" >' + arrayTiposAjuste[i] + '</option>';
						}
					}
					$select_tipo_ajuste.append(select_html);
					
					
					
					//carga select con los tipos de Movimientos
					$select_tipo_mov.children().remove();
					var tmov_hmtl = '';
					$.each(entry['TMov'],function(entryIndex,mov){
						if(parseInt(entry['Datos']['0']['inv_mov_tipo_id'])==parseInt(mov['id'])){
							tmov_hmtl += '<option value="' + mov['id'] + '" selected="yes">' + mov['titulo'] + '</option>';
						}else{
							//tmov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
						}
					});
					$select_tipo_mov.append(tmov_hmtl);
			
			
			
					//carga select con todos los Almacenes de la Empresa
					$select_almacen.children().remove();
					var almacen_hmtl = '';
					$.each(entry['Almacenes'],function(entryIndex,almacen){
						if(parseInt(entry['Datos']['0']['id_almacen'])==parseInt(almacen['id'])){
							almacen_hmtl += '<option value="' + almacen['id'] + '" selected="yes">' + almacen['titulo'] + '</option>';
						}else{
							//almacen_hmtl += '<option value="' + almacen['id'] + '"  >' + almacen['titulo'] + '</option>';
						}
					});
					$select_almacen.append(almacen_hmtl);
					
					
					if (entry['DatosGrid'].length > 0){
						
						$.each(entry['DatosGrid'],function(entryIndex,prodGrid){
							
							var noTr = $("tr", $grid_productos).size();
							noTr++;
							var tipo_costo=1;//en esta parte de editar no importa el valor de esta variable
							var id_producto = prodGrid['producto_id'];
							var codigo = prodGrid['codigo'];
							var descripcion = prodGrid['descripcion'];
							var unidad = prodGrid['unidad'];
							var id_almacen = prodGrid['id_almacen'];
							var existencia = prodGrid['existencia'];
							var costo_prom = prodGrid['costo_promedio'];
							var cant_ajuste=prodGrid['cant_ajuste'];
							var costo_ajuste=prodGrid['costo_ajuste'];
							
							var idPres = prodGrid['idPres'];
							var equivPres = prodGrid['cantEqiv'];
							var exisPres = 0;
							var ajustePres = 0;
							
							var controlExisPres = entry['Par'][0]['exis_pres'];
							
							if(controlExisPres=='true'){
								ajustePres = prodGrid['cantPres'];
								//alert(ajustePres);
							}
							
							var cadena_tr = $genera_tr(noTr, id_producto, codigo, descripcion, unidad, id_almacen, existencia, costo_prom, cant_ajuste, costo_ajuste, tipo_costo, idPres, equivPres, exisPres, ajustePres);
						
							$grid_productos.append(cadena_tr);
							
							$grid_productos.find('.select_pres'+noTr).children().remove();
							var pres_hmtl = '';
							
							if(parseInt(idPres)==0){
								pres_hmtl = '<option value="0">[-Seleccionar-]</option>';
							}
							$.each(entry['Presentaciones'],function(entryIndex,pres){
								if(parseInt(idPres) == parseInt(pres['id'])){
									pres_hmtl += '<option value="' + pres['id'] + '">' + pres['titulo'] + '</option>';
								}
							});
							$grid_productos.find('.select_pres'+noTr).append(pres_hmtl);
							
						});
						
					}
					
					
					
					$observaciones.attr({ 'readOnly':true });
					$sku_producto.attr('disabled','-1');
					$nombre_producto.attr('disabled','-1');
					$grid_productos.find('input').attr({ 'readOnly':true });
					$grid_productos.find('a').hide();
					$grid_productos.find('input').keypress(function(e){
						if(e.which==13 ) {
							return false;
						}
					});
					
				});//termina llamada json
                
                
                
				//descargar pdf de Ajustes
				$descargarpdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfAjuste/'+$identificador.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
				
				
				
                
				//deshabilitar tecla enter  en todo el plugin
				$('#forma-invajustes-window').find('input').keypress(function(e){
					if(e.which==13 ) {
						return false;
					}
				});
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invajustes-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invajustes-overlay').fadeOut(remove);
				});
				
			}
		}
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllAjustes.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllAjustes.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvajustes00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



