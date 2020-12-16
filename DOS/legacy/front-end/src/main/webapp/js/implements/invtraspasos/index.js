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
				2:"Negativo"//grupo salidas en la tabla tipos de movimiento de Invetario
			};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invtraspasos";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $new_traspaso = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Traspasos');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	var $busqueda_alm_origen = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_alm_origen]');
	var $busqueda_alm_destino = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_alm_destino]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');

	
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
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		valor_retorno += "alm_origen" + signo_separador + $busqueda_alm_origen.val() + "|";
		valor_retorno += "alm_destino" + signo_separador + $busqueda_alm_destino.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();

		return valor_retorno;
	};
    
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	$buscar.click(function(event){
		//event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	$limpiar.click(function(event){
		$busqueda_folio.val('');
		$busqueda_codigo.val('');
		$busqueda_descripcion.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		
		//cargar select de Almacen Origen y Almacen Destino para el Buscador
		var input_json_mov_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAlmacenes.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_mov_tipos,$arreglo,function(data){
			$busqueda_alm_origen.children().remove();
			var alm_html = '<option value="0" selected="yes">[--- ---]</option>';
			$.each(data['AlmEmpresa'],function(entryIndex,tm){
				alm_html += '<option value="' + tm['id'] + '"  >' + tm['titulo'] + '</option>';
			});
			$busqueda_alm_origen.append(alm_html);
			
			$busqueda_alm_destino.append(alm_html);
			
		});
	});
	
	
	//cargar select de Almacen Origen y Almacen Destino para el Buscador
	var input_json_mov_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAlmacenes.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_mov_tipos,$arreglo,function(data){
		$busqueda_alm_origen.children().remove();
		var alm_html = '<option value="0" selected="yes">[--- ---]</option>';
		$.each(data['AlmEmpresa'],function(entryIndex,tm){
			alm_html += '<option value="' + tm['id'] + '"  >' + tm['titulo'] + '</option>';
		});
		$busqueda_alm_origen.append(alm_html);
		
		$busqueda_alm_destino.append(alm_html);
		
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
	});
	
	
	

	
	
	
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
            var $select_prod_tipo = $('#forma-invtraspasos-window').find('select[name=prodtipo]');
            $('#forma-invtraspasos-window').find('#submit').mouseover(function(){
                $('#forma-invtraspasos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                //$('#forma-invtraspasos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
            })
            $('#forma-invtraspasos-window').find('#submit').mouseout(function(){
                $('#forma-invtraspasos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                //$('#forma-invtraspasos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
            })
            $('#forma-invtraspasos-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-invtraspasos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-invtraspasos-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-invtraspasos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-invtraspasos-window').find('#close').mouseover(function(){
                $('#forma-invtraspasos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-invtraspasos-window').find('#close').mouseout(function(){
                $('#forma-invtraspasos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-invtraspasos-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-invtraspasos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-invtraspasos-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-invtraspasos-window').find("ul.pestanas li").click(function() {
                $('#forma-invtraspasos-window').find(".contenidoPes").hide();
                $('#forma-invtraspasos-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-invtraspasos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
					$('#forma-invtraspasos-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-invtraspasos-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-invtraspasos-window').find('input[name=sku_producto]').focus();
                                        
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
	$aplicar_evento_eliminar = function( $campo_href, arraySucursales, arrayAlmacenes ){
		//eliminar un lote
		$campo_href.click(function(e){
			e.preventDefault();
			$tr_padre=$(this).parent().parent();
			//$tr_padre.find('input').val('');//asignar vacio a todos los input del tr
			//$tr_padre.find('input[name=eliminado]').val('0');//asignamos 0 para indicar que se ha eliminado
			$tr_padre.remove();//eliminar el tr
			
			$grid_productos = $tr_padre.parent();
			
			$fijar_opciones_select($grid_productos, arraySucursales, arrayAlmacenes);
		});
	}
	
	
	
	$realiza_calculo_kilos = function($this_tr, $campo_input, noDec){
		if($campo_input.val()=='0' || $campo_input.val()==""){
				$this_tr.find('input[name=cantidad_kilos]').val(0);
		}else{
			$densidad_tmp = parseFloat($this_tr.find('input[name=densidad_litro]').val());
			cant_traspaso = $this_tr.find('input[name=cant_traspaso]').val();
			
			if(isNaN($densidad_tmp)){
				$this_tr.find('input[name=cantidad_kilos]').val(0);
			}else{
				unidad = $this_tr.find('input[name=unidad]').val();
				unidad = unidad.toUpperCase();
				if(/^KILO*|KILOGRAMO$/.test(unidad)){
					$this_tr.find('input[name=cantidad_kilos]').val(cant_traspaso);
				}else{
					if(/^LITRO*|LITROS$/.test(unidad)){
						kilos = parseFloat(cant_traspaso) * ($densidad_tmp);
						$this_tr.find('input[name=cantidad_kilos]').val(parseFloat(kilos).toFixed(noDec));
					}else{
						$this_tr.find('input[name=cantidad_kilos]').val(0);
					}
				}
			}
		}
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
	
	
        
	//funcion para aplicar la conversion de litros a kilos, de acuerdo a la densidad
	$aplicar_evento_campo_cantidad = function( $campo_input, tipo, noDec, arrayPres, controlExisPres){
		//tipo almacene, si se envio, para realizar el evento blur, o para solo hacer el calculo
		var $this_tr = $campo_input.parent().parent();
		
		if(tipo == "blur"){
			$campo_input.blur(function(e){
				$realiza_calculo_kilos($this_tr, $campo_input, noDec);
				
				//si la configuracion indica que debe controlar existencias por presentacion hay que realizar los calculos
				if(controlExisPres=='true'){
					var idPres = $this_tr.find('select[name=select_pres]').val();
					var $cantUniTras = $this_tr.find('input[name=cant_traspaso]');
					var $cantPresTras = $this_tr.find('input[name=cantTrasPres]');
					var cantUniExis = quitar_comas($this_tr.find('input[name=cantidad]').val());
					
					//convertir las Unidades en Presentaciones
					var exisPres = $convertirUniAPres(idPres, $cantUniTras.val(), arrayPres);
					
					if(parseFloat($cantUniTras.val()) > parseFloat(cantUniExis)){
						jAlert('No es posible realizar un traspaso mayor a la Existencia de la Presentaci&oacute;n.', 'Atencion!', function(r) { 
							$cantUniTras.val(parseFloat(0).toFixed(noDec));
							$cantPresTras.val(parseFloat(0).toFixed(noDec));
						});
					}else{
						$cantPresTras.val(parseFloat(exisPres).toFixed(noDec));
					}
				}
			});
		}else{
			$realiza_calculo_kilos($this_tr, $campo_input);
		}
	}
        
	
	//fijar opciones de select para que se pueda modificar mientras el grid tenga datos
	$fijar_opciones_select = function($grid_productos, arraySucursales, arrayAlmacenes){
		var $select_suc_origen = $('#forma-invtraspasos-window').find('select[name=select_suc_origen]');
		var $select_alm_origen = $('#forma-invtraspasos-window').find('select[name=select_alm_origen]');
		var $select_suc_destino = $('#forma-invtraspasos-window').find('select[name=select_suc_destino]');
		var $select_alm_destino = $('#forma-invtraspasos-window').find('select[name=select_alm_destino]');
		
		var id_suc_origen = $select_suc_origen.val();
		var id_alm_origen = $select_alm_origen.val();
		var id_suc_destino = $select_suc_destino.val();
		var id_alm_destino = $select_alm_destino.val();
		
		if( parseInt($("tr", $grid_productos).size()) > 0 ){
			//fijar la sucursal actual seleccionada para que no se pueda cambiar
			$select_suc_origen.children().remove();
			var suc_hmtl = '';
			$.each(arraySucursales,function(entryIndex,suc){
				if(suc['id'] == id_suc_origen){
					suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">' + suc['sucursal'] + '</option>';
				}
			});
			$select_suc_origen.append(suc_hmtl);
			
			//fijar valor seleccionado para el select de almacenes, esto para que no sea modificado por el usuario
			$select_alm_origen.children().remove();
			var almacen_hmtl = '';
			$.each(arrayAlmacenes,function(entryIndex,almacen){
				if(parseInt(id_alm_origen)==parseInt(almacen['id'])){
					almacen_hmtl += '<option value="' + almacen['id'] + '" selected="yes">' + almacen['titulo'] + '</option>';
				}
			});
			$select_alm_origen.append(almacen_hmtl);
			
		}else{
			
			//fijar la sucursal actual seleccionada pero permite cambio de sucursal
			$select_suc_origen.children().remove();
			var suc_hmtl = '';
			$.each(arraySucursales,function(entryIndex,suc){
				if(suc['id'] == id_suc_origen){
					suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">' + suc['sucursal'] + '</option>';
				}else{
					suc_hmtl += '<option value="' + suc['id'] + '" >' + suc['sucursal'] + '</option>';
				}
			});
			$select_suc_origen.append(suc_hmtl);
			
			//fijar valor seleccionado para el select de almacenes, pero permite cambiar almacen
			$select_alm_origen.children().remove();
			var almacen_hmtl = '';
			$.each(arrayAlmacenes,function(entryIndex,almacen){
				if(id_suc_origen == almacen['suc_id']){
					if(parseInt(id_alm_origen)==parseInt(almacen['id'])){
						almacen_hmtl += '<option value="' + almacen['id'] + '" selected="yes">' + almacen['titulo'] + '</option>';
					}else{
						almacen_hmtl += '<option value="' + almacen['id'] + '">' + almacen['titulo'] + '</option>';
					}
				}
			});
			$select_alm_origen.append(almacen_hmtl);
		}
	}
	
	
	
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
			
			$exisPresTr = $this_tr.find('input[name=exis_pres]');
			$exisUniTr = $this_tr.find('input[name=cantidad]');
			$presIdSelecTr = $this_tr.find('input[name=presIdSeleccionado]');
			$cantTrasPresTr = $this_tr.find('input[name=cantTrasPres]');
			$cantTrasUniTr = $this_tr.find('input[name=cant_traspaso]');
			
			//asignar valores en cero
			$exisPresTr.val(parseFloat(0).toFixed(2));
			$exisUniTr.val(parseFloat(0).toFixed(2));
			$cantTrasPresTr.val(parseFloat(0).toFixed(2));
			$cantTrasUniTr.val(parseFloat(0).toFixed(2));
			
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
							
							$presIdSelecTr.val(idPres);
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
	
	
	
	//generar tr para agregar al grid
	$genera_tr = function(noTr, id_producto, codigo, descripcion, unidad, existencia, cant_traspaso, densidad, exisPres, cantTraspasoPres){
		var readOnly=''//esta variable indica si el campo costo ajuste va a ser editable o no de pendiendo del tipo de costo que se utiliza para el tipo de movimiento
		
		var trr = '';
		trr = '<tr>';
			trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
				trr += '<a href="elimina_producto" class="delete'+ noTr +'">Eliminar</a>';
				trr += '<input type="hidden" 	name="eliminado" value="1">';//el 1 significa que el registro no ha sido eliminado
				trr += '<input type="hidden" 	name="no_tr" value="'+ noTr +'">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
				trr += '<input type="hidden" 	name="idproducto" id="idprod" value="'+ id_producto +'">';
				trr += '<input type="text" 		name="codigo" value="'+ codigo +'" class="borde_oculto" readOnly="true" style="width:96px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="180">';
				trr += '<input type="text" 		name="nombre" 	value="'+ descripcion +'" class="borde_oculto" readOnly="true" style="width:176px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
				trr += '<input type="text" 		name="unidad" 	value="'+ unidad +'" class="borde_oculto" readOnly="true" style="width:86px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
				trr += '<input type="hidden" 	name="presIdSeleccionado" id="presId" value="0">';
				trr += '<select name="select_pres" class="pres'+ noTr +'" style="width:116px;"></select>';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
				trr += '<input type="hidden" name="densidad_litro" value="'+densidad+'">';
				trr += '<input type="text" name="cantidad_kilos" id="cantidad_kilos'+ noTr +'" value="'+ $(this).agregar_comas(cant_traspaso) +'" class="borde_oculto" readOnly="true" style="width:56px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
				trr += '<input type="text" 		name="cantidad" value="'+$(this).agregar_comas(existencia)+'" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
				trr += '<input type="text" 		name="exis_pres" value="'+ $(this).agregar_comas(exisPres) +'" class="borde_oculto" readOnly="true" style="width:76px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
				trr += '<input type="text" 		name="cant_traspaso" value="'+ $(this).agregar_comas(cant_traspaso) +'" class="cant_traspaso'+noTr+'" style="width:86px; text-align:right;">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
				trr += '<input type="text" 		name="cantTrasPres" value="'+ $(this).agregar_comas(cantTraspasoPres) +'" id="cantTrasPres'+noTr+'" class="borde_oculto" style="width:76px; text-align:right;">';
			trr += '</td>';
		trr += '</tr>';
		
		return trr;
	}
	
	
	
	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_datos_producto = function($grid_productos, codigo_producto, id_alm, fecha, arraySucursales, arrayAlmacenes ){
		//verifica si el campo sku no esta vacio para realizar busqueda
		if(codigo_producto != ''){
			
			var encontrado = 0;
			/*
			//busca el sku y la presentacion en el grid
			$grid_productos.find('tr').each(function (index){
				if(($(this).find('input[name=codigo]').val()==codigo_producto.toUpperCase()) && (parseInt($(this).find('input[name=eliminado]').val())!=0) ){
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
					//verifica si el arreglo retorno datos del Producto
					if (entry['Producto'].length > 0){
						
						if (entry['Presentaciones'].length > 0){
							var noTr = $("tr", $grid_productos).size();
							noTr++;
							
							var id_producto = entry['Producto'][0]['id_producto'];
							var codigo = entry['Producto'][0]['codigo'];
							var descripcion = entry['Producto'][0]['descripcion'];
							var unidad = entry['Producto'][0]['unidad'];
							var noDec = entry['Producto'][0]['no_dec'];
							var controlExisPres = entry['Par'][0]['exis_pres'];
							
							var existencia = parseFloat(entry['Producto'][0]['existencia']).toFixed(noDec);
							var densidad = entry['Producto'][0]['densidad'];
							var cant_traspaso=parseFloat(0).toFixed(noDec);
							var exisPres = parseFloat(0).toFixed(noDec);
							var cantTraspasoPres = parseFloat(0).toFixed(noDec);
							
							var cadena_tr = $genera_tr(noTr, id_producto, codigo, descripcion, unidad, existencia, cant_traspaso, densidad, exisPres, cantTraspasoPres);
							
							$grid_productos.append(cadena_tr);
							
							$aplicar_evento_keypress($grid_productos.find('.cant_traspaso'+noTr));
							$aplicar_evento_focus_input($grid_productos.find('.cant_traspaso'+noTr));
							$aplicar_evento_blur_input($grid_productos.find('.cant_traspaso'+noTr));
							
							$aplicar_evento_eliminar($grid_productos.find('.delete'+noTr), arraySucursales, arrayAlmacenes);
							
							//carga select con las Presentaciones del Producto
							$grid_productos.find('select.pres'+noTr).children().remove();
							var pres_hmtl = '<option value="0">[--Seleccionar--]</option>';
							$.each(entry['Presentaciones'],function(entryIndex,pres){
								pres_hmtl += '<option value="' + pres['id'] + '">' + pres['titulo'] + '</option>';
							});
							$grid_productos.find('select.pres'+noTr).append(pres_hmtl);
							
							$aplicar_evento_campo_cantidad($grid_productos.find('.cant_traspaso'+noTr), "blur", noDec, entry['Presentaciones'], controlExisPres);
							
							//fijar las opciones actuales de los select
							$fijar_opciones_select($grid_productos, arraySucursales, arrayAlmacenes);
							
							if(controlExisPres=='true'){
								//llamada a la funcion que aplica Evento Change al select de Presentaciones
								$aplicarEventoChangeSelectPres($grid_productos.find('select.pres'+noTr), id_alm, entry['Presentaciones']);
							}
						}else{
							jAlert('El producto que intenta agregar no tiene Presentaciones Asignadas, pruebe ingresando otro.\nHaga clic en Buscar.', 'Atencion!', function(r) {
								$('#forma-invtraspasos-window').find('input[name=sku_producto]').val('');
								$('#forma-invtraspasos-window').find('input[name=titulo_producto]').val('');
								$('#forma-invtraspasos-window').find('input[name=titulo_producto]').focus();
							});
						}
					}else{
						jAlert('El producto que intenta agregar no existe en el almacen origen, pruebe ingresando otro.\nHaga clic en Buscar.', 'Atencion!', function(r) {
							$('#forma-invtraspasos-window').find('input[name=sku_producto]').val('');
							$('#forma-invtraspasos-window').find('input[name=titulo_producto]').val('');
							$('#forma-invtraspasos-window').find('input[name=titulo_producto]').focus();
						});
					}
				});
			}else{
				jAlert('El producto que intenta Agregar ya se encuentra en el listado.', 'Atencion!', function(r) {
					$('#forma-invtraspasos-window').find('input[name=sku_producto]').val('');
					$('#forma-invtraspasos-window').find('input[name=titulo_producto]').val('');
					$('#forma-invtraspasos-window').find('input[name=sku_producto]').focus();
				});
			}
		}else{
			jAlert('Es necesario ingresar un C&oacute;digo de producto valido.', 'Atencion!', function(r) {
				$('#forma-invtraspasos-window').find('input[name=sku_producto]').val('');
				$('#forma-invtraspasos-window').find('input[name=titulo_producto]').val('');
				$('#forma-invtraspasos-window').find('input[name=sku_producto]').focus();
			});
		}
	}//termina buscador de datos del producto
	
    
	
	
	
	
	
	
	
	
	
	//nuevo Traspaso
	$new_traspaso.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_invtraspasos();
		
		var form_to_show = 'formainvtraspasos00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-invtraspasos-window').css({"margin-left": -340, 	"margin-top": -220});
		
		$forma_selected.prependTo('#forma-invtraspasos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTraspaso.json';
		$arreglo = {'identificador':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
		var $identificador = $('#forma-invtraspasos-window').find('input[name=identificador]');
		var $folio = $('#forma-invtraspasos-window').find('input[name=folio]');
		var $fecha_traspaso = $('#forma-invtraspasos-window').find('input[name=fecha_traspaso]');
		
		var $select_suc_origen = $('#forma-invtraspasos-window').find('select[name=select_suc_origen]');
		var $select_alm_origen = $('#forma-invtraspasos-window').find('select[name=select_alm_origen]');
		
		var $select_suc_destino = $('#forma-invtraspasos-window').find('select[name=select_suc_destino]');
		var $select_alm_destino = $('#forma-invtraspasos-window').find('select[name=select_alm_destino]');
		
		var $observaciones = $('#forma-invtraspasos-window').find('textarea[name=observaciones]');
		
		var $sku_producto = $('#forma-invtraspasos-window').find('input[name=sku_producto]');
		var $nombre_producto = $('#forma-invtraspasos-window').find('input[name=nombre_producto]');
		
		//buscar producto
		var $busca_sku = $('#forma-invtraspasos-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-invtraspasos-window').find('a[href*=agregar_producto]');
		var $descargarpdf = $('#forma-invtraspasos-window').find('#descargarpdf');
		
		//grid de productos
		var $grid_productos = $('#forma-invtraspasos-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning');
		
		var $cerrar_plugin = $('#forma-invtraspasos-window').find('#close');
		var $cancelar_plugin = $('#forma-invtraspasos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invtraspasos-window').find('#submit');
		
		//$campo_factura.css({'background' : '#ffffff'});
		
		//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
		$descargarpdf.attr('disabled','-1');
		$identificador.val(0);//para nueva pedido el id es 0
		
		
		$folio.css({'background' : '#F0F0F0'});
		$fecha_traspaso.css({'background' : '#F0F0F0'});
		
		$select_suc_origen.focus();
		
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El traspaso se guard&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-invtraspasos-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				//habilitar boton actualizar
				$submit_actualizar.removeAttr('disabled');
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-invtraspasos-window').find('div.interrogacion').css({'display':'none'});
				$grid_productos.find('input[name=cant_traspaso]').css({'background' : '#ffffff'});
				$grid_productos.find('select[name=select_pres]').css({'background' : '#ffffff'});
				
				$('#forma-invtraspasos-window').find('.invtraspasos_div_one').css({'height':'470px'});//sin errores
				$('#forma-invtraspasos-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-invtraspasos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						var campo = tmp.split(':')[0];
						
						
						if((campo.substring(0,13) == 'cant_traspaso') || (campo.substring(0,4) == 'pres')){
							$('#forma-invtraspasos-window').find('.invtraspasos_div_one').css({'height':'575px'});//con errores
							$('#forma-invtraspasos-window').find('#div_warning_grid').css({'display':'block'});
							var $campo = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
							
							var codigo_producto = $campo.parent().parent().find('input[name=codigo]').val();
							var titulo_producto = $campo.parent().parent().find('input[name=nombre]').val();
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="120"><input type="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:120px; color:red"></td>';
									tr_warning += '<td width="200"><input type="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:200px; color:red"></td>';
									tr_warning += '<td width="560"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:530px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
					}
				}
				$('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
				$('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
			}
		}
		
		var options = {datatype :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			$fecha_traspaso.val(entry['Extras'][0]['fecha_actual']);
			
			//-------------------------------------------------------------------------------------------------------------------
			//sucursal y almacen Origen
			//-------------------------------------------------------------------------------------------------------------------
			//carga select con todos los Sucursales de la Empresa
			$select_suc_origen.children().remove();
			var suc_hmtl = '';
			$.each(entry['Sucursales'],function(entryIndex,suc){
				if(suc['id'] == entry['Extras'][0]['suc_id_actual']){
					suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">' + suc['sucursal'] + '</option>';
				}else{
					suc_hmtl += '<option value="' + suc['id'] + '" >' + suc['sucursal'] + '</option>';
				}
			});
			$select_suc_origen.append(suc_hmtl);
			
			
			//carga select con todos los Almacenes de la Sucursal seleccionada
			$select_alm_origen.children().remove();
			var almacen_hmtl = '';
			$.each(entry['Almacenes'],function(entryIndex,almacen){
				if(parseInt(entry['Extras'][0]['suc_id_actual'])==parseInt(almacen['suc_id'])){
					almacen_hmtl += '<option value="' + almacen['id'] + '">' + almacen['titulo'] + '</option>';
				}
			});
			$select_alm_origen.append(almacen_hmtl);
			
			
			//cambiar la sucursal Origen
			$select_suc_origen.change(function(){
				var suc_origen_id = $(this).val();
				$select_alm_origen.children().remove();
				var alm_hmtl = '';
				$.each(entry['Almacenes'],function(entryIndex,almacen){
					if(parseInt(suc_origen_id)==parseInt(almacen['suc_id'])){
						alm_hmtl += '<option value="' + almacen['id'] + '">' + almacen['titulo'] + '</option>';
					}
				});
				$select_alm_origen.append(alm_hmtl);
				
				//cargamos el select de sucursal destino al cambiar la sucursal origen, le asginamos la misma sucursal destino
				$select_suc_destino.children().remove();
				suc_hmtl = '';
				$.each(entry['Sucursales'],function(entryIndex,suc){
					if(suc['id'] == suc_origen_id){
						suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">' + suc['sucursal'] + '</option>';
					}else{
						suc_hmtl += '<option value="' + suc['id'] + '" >' + suc['sucursal'] + '</option>';
					}
				});
				$select_suc_destino.append(suc_hmtl);
				
				
				
				//asignamos el almacen destino, este almacen debe ser diferente al almacen origen de si la sucursal es igual al origen				
				$select_alm_destino.children().remove();
				var alm_hmtl = '';
				var exis_alm=0;
				$.each(entry['Almacenes'],function(entryIndex,almacen){
					if( parseInt(suc_origen_id)==parseInt(almacen['suc_id']) ){
						if(parseInt(suc_origen_id)==parseInt($select_suc_origen.val()) ){
							if( (parseInt(almacen['id']) != parseInt($select_alm_origen.val())) ){
								alm_hmtl += '<option value="' + almacen['id'] + '" >' + almacen['titulo'] + '</option>';
								exis_alm++;
							}
						}else{
							alm_hmtl += '<option value="' + almacen['id'] + '" >' + almacen['titulo'] + '</option>';
							exis_alm++;
						}
					}
				});
				
				if(parseInt(exis_alm)==0){
					alm_hmtl += '<option value="0">[ No hay almacen para traspaso ]</option>';
				}
				$select_alm_destino.append(alm_hmtl);
			});
			
			
			//cambiar el Almacen Origen
			$select_alm_origen.change(function(){
				var alm_origen_id = $(this).val();
				$select_alm_destino.children().remove();
				almacen_hmtl = '';
				var exis=0;
				$.each(entry['Almacenes'],function(entryIndex,almacen){
					if(parseInt($select_suc_destino.val())==parseInt(almacen['suc_id'])){
						if(parseInt($select_suc_destino.val())==parseInt($select_suc_origen.val()) ){
							if( (parseInt(almacen['id']) != parseInt(alm_origen_id)) ){
								almacen_hmtl += '<option value="' + almacen['id'] + '" >' + almacen['titulo'] + '</option>';
								exis++;
							}
						}else{
							almacen_hmtl += '<option value="' + almacen['id'] + '" >' + almacen['titulo'] + '</option>';
							exis++;
						}
					}
				});
				if(parseInt(exis)==0){
					almacen_hmtl += '<option value="0">[ No hay almacen para traspaso ]</option>';
				}
				$select_alm_destino.append(almacen_hmtl);
			});
			//Termina sucursa y almacen origen ----------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------------------------
			//sucursal y almacen destino
			//-------------------------------------------------------------------------------------------------------------------
			//carga select con todos los Sucursales de la Empresa
			$select_suc_destino.children().remove();
			suc_hmtl = '';
			$.each(entry['Sucursales'],function(entryIndex,suc){
				if(suc['id'] == entry['Extras'][0]['suc_id_actual']){
					suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">' + suc['sucursal'] + '</option>';
				}else{
					suc_hmtl += '<option value="' + suc['id'] + '" >' + suc['sucursal'] + '</option>';
				}
			});
			$select_suc_destino.append(suc_hmtl);
			
			
			
			//carga select con todos los Almacenes de la Sucursal Destino seleccionada
			//Si la sucursal es la misma que la origen el almacen debe ser diferente que el almacen Origen
			$select_alm_destino.children().remove();
			almacen_hmtl = '';
			var exis=0;
			$.each(entry['Almacenes'],function(entryIndex,almacen){
				if(parseInt(entry['Extras'][0]['suc_id_actual'])==parseInt(almacen['suc_id'])){
					if(parseInt(entry['Extras'][0]['suc_id_actual'])==parseInt($select_suc_origen.val()) ){
						if( (parseInt(almacen['id']) != parseInt($select_alm_origen.val())) ){
							almacen_hmtl += '<option value="' + almacen['id'] + '" >' + almacen['titulo'] + '</option>';
							exis++;
						}
					}else{
						almacen_hmtl += '<option value="' + almacen['id'] + '" >' + almacen['titulo'] + '</option>';
						exis++;
					}
				}
			});
			if(parseInt(exis)==0){
				almacen_hmtl += '<option value="0">[ No hay almacen para traspaso ]</option>';
			}
			$select_alm_destino.append(almacen_hmtl);
			
			
			//cambiar la sucursal Destino
			$select_suc_destino.change(function(){
				var suc_destino_id = $(this).val();
				$select_alm_destino.children().remove();
				var alm_hmtl = '';
				var exis_alm=0;
				$.each(entry['Almacenes'],function(entryIndex,almacen){
					if( parseInt(suc_destino_id)==parseInt(almacen['suc_id']) ){
						if(parseInt(suc_destino_id)==parseInt($select_suc_origen.val()) ){
							if( (parseInt(almacen['id']) != parseInt($select_alm_origen.val())) ){
								alm_hmtl += '<option value="' + almacen['id'] + '" >' + almacen['titulo'] + '</option>';
								exis_alm++;
							}
						}else{
							alm_hmtl += '<option value="' + almacen['id'] + '" >' + almacen['titulo'] + '</option>';
							exis_alm++;
						}
					}
				});
				if(parseInt(exis_alm)==0){
					alm_hmtl += '<option value="0">[ No hay almacen para traspaso ]</option>';
				}
				$select_alm_destino.append(alm_hmtl);
			});
			
			
			
			//-------------------------------------------------------------------------------------------------------------------
			
			
			
			//agregar producto al grid
			$agregar_producto.click(function(event){
				event.preventDefault();
				var codigo_producto = $sku_producto.val();
				var id_alm = $select_alm_origen.val();
				var fecha = $fecha_traspaso.val();
				$buscador_datos_producto($grid_productos, codigo_producto, id_alm, fecha, entry['Sucursales'], entry['Almacenes'] );
			});
			
			
		},"json");//termina llamada json
		
		
		
		
		//buscador de productos
		$busca_sku.click(function(event){
			event.preventDefault();
			$busca_productos($select_alm_origen.val(), $fecha_traspaso.val());
		});
		
		
		//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$sku_producto.keypress(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
		
		
		
		//deshabilitar tecla enter  en todo el plugin
		$('#forma-invtraspasos-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		
				
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert("No hay productos para realizar el traspaso.", 'Atencion!');
				return false;
			}
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invtraspasos-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invtraspasos-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	var carga_formainvtraspasos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'identificador':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  la el registro?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El registro se elimino con exito.", 'Atencion!');
							$get_datos_grid();
						}else{
							jAlert("El registro no pudo ser eliminado.", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-invtraspasos-window').remove();
			$('#forma-invtraspasos-overlay').remove();
            
			var form_to_show = 'formainvtraspasos00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_invtraspasos();
			
			$('#forma-invtraspasos-window').css({"margin-left": -340, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-invtraspasos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTraspaso.json';
			$arreglo = {'identificador':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			
			var $identificador = $('#forma-invtraspasos-window').find('input[name=identificador]');
			var $folio = $('#forma-invtraspasos-window').find('input[name=folio]');
			var $fecha_traspaso = $('#forma-invtraspasos-window').find('input[name=fecha_traspaso]');
			
			var $select_suc_origen = $('#forma-invtraspasos-window').find('select[name=select_suc_origen]');
			var $select_alm_origen = $('#forma-invtraspasos-window').find('select[name=select_alm_origen]');
			
			var $select_suc_destino = $('#forma-invtraspasos-window').find('select[name=select_suc_destino]');
			var $select_alm_destino = $('#forma-invtraspasos-window').find('select[name=select_alm_destino]');
			
			var $observaciones = $('#forma-invtraspasos-window').find('textarea[name=observaciones]');
			
			var $sku_producto = $('#forma-invtraspasos-window').find('input[name=sku_producto]');
			var $nombre_producto = $('#forma-invtraspasos-window').find('input[name=nombre_producto]');
			
			//buscar producto
			var $busca_sku = $('#forma-invtraspasos-window').find('a[href*=busca_sku]');
			//href para agregar producto al grid
			var $agregar_producto = $('#forma-invtraspasos-window').find('a[href*=agregar_producto]');
			var $descargarpdf = $('#forma-invtraspasos-window').find('#descargarpdf');
			
			//grid de productos
			var $grid_productos = $('#forma-invtraspasos-window').find('#grid_productos');
			//grid de errores
			var $grid_warning = $('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning');
			
			
			var $cerrar_plugin = $('#forma-invtraspasos-window').find('#close');
			var $cancelar_plugin = $('#forma-invtraspasos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invtraspasos-window').find('#submit');
			
			$identificador.val(0);//para nueva pedido el id es 0
			$fecha_traspaso.val(mostrarFecha());
			
			$busca_sku.hide();
			$agregar_producto.hide();
			$submit_actualizar.hide();
			
			$folio.css({'background' : '#F0F0F0'});
			$fecha_traspaso.css({'background' : '#F0F0F0'});
		
			var respuestaProcesada = function(data){
				if ( data['success'] == "true" ){
					jAlert("El Traspaso se guard&oacute; con &eacute;xito", 'Atencion!');
					var remove = function() {$(this).remove();};
					$('#forma-invtraspasos-overlay').fadeOut(remove);
					$get_datos_grid();
				}else{
					//habilitar boton actualizar
					$submit_actualizar.removeAttr('disabled');
					// Desaparece todas las interrogaciones si es que existen
					$('#forma-invtraspasos-window').find('.invtraspasos_div_one').css({'height':'550px'});//con errores
					$('#forma-invtraspasos-window').find('div.interrogacion').css({'display':'none'});
					$grid_productos.find('input[name=cant_traspaso]').css({'background' : '#ffffff'});
					$grid_productos.find('input[name=costo_ajuste]').css({'background' : '#ffffff'});
					
					$('#forma-invtraspasos-window').find('#div_warning_grid').css({'display':'none'});
					$('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
					
					var valor = data['success'].split('___');
					//muestra las interrogaciones
					for (var element in valor){
						tmp = data['success'].split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
							$('#forma-invtraspasos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							
							var campo = tmp.split(':')[0];
							
							$('#forma-invtraspasos-window').find('#div_warning_grid').css({'display':'block'});
							var $campo = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
							
							var codigo_producto = $campo.parent().parent().find('input[name=codigo]').val();
							var titulo_producto = $campo.parent().parent().find('input[name=nombre]').val();
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="150"><input type="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:150px; color:red"></td>';
									tr_warning += '<td width="250"><input type="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:250px; color:red"></td>';
									tr_warning += '<td width="380"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:350px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
							
						}
					}
					$('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
					$('#forma-invtraspasos-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
				}
			
			}		
			var options = {datatype :  'json', success : respuestaProcesada};
			$forma_selected.ajaxForm(options);
			
			//aqui se cargan los campos al editar
			$.post(input_json,$arreglo,function(entry){
				$identificador.val(entry['Datos']['0']['id_traspaso']);
				$folio.val(entry['Datos']['0']['folio']);
				$fecha_traspaso.val(entry['Datos']['0']['fecha']);
				$observaciones.text(entry['Datos']['0']['observaciones']);
				
				//carga select con todos los Sucursales de la Empresa
				$select_suc_origen.children().remove();
				var suc_hmtl = '';
				$.each(entry['Sucursales'],function(entryIndex,suc){
					if(suc['id'] == entry['Datos'][0]['suc_origen']){
						suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">' + suc['sucursal'] + '</option>';
					}
				});
				$select_suc_origen.append(suc_hmtl);
				
				//carga select con todos los Almacenes de la Sucursal seleccionada
				$select_alm_origen.children().remove();
				var almacen_hmtl = '';
				$.each(entry['Almacenes'],function(entryIndex,almacen){
					if(parseInt(entry['Datos'][0]['alm_origen'])==parseInt(almacen['id'])){
						almacen_hmtl += '<option value="' + almacen['id'] + '">' + almacen['titulo'] + '</option>';
					}
				});
				$select_alm_origen.append(almacen_hmtl);
				
				
				//carga select con todos los Sucursales de la Empresa
				$select_suc_destino.children().remove();
				suc_hmtl = '';
				$.each(entry['Sucursales'],function(entryIndex,suc){
					if(suc['id'] == entry['Datos'][0]['suc_destino']){
						suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">' + suc['sucursal'] + '</option>';
					}
				});
				$select_suc_destino.append(suc_hmtl);
				
				//carga select con todos los Almacenes de la Sucursal seleccionada
				$select_alm_destino.children().remove();
				almacen_hmtl = '';
				$.each(entry['Almacenes'],function(entryIndex,almacen){
					if(parseInt(entry['Datos'][0]['alm_destino'])==parseInt(almacen['id'])){
						almacen_hmtl += '<option value="' + almacen['id'] + '">' + almacen['titulo'] + '</option>';
					}
				});
				$select_alm_destino.append(almacen_hmtl);
				
				
				if (entry['DatosGrid'].length > 0){						
					$.each(entry['DatosGrid'],function(entryIndex,prodGrid){
						var noTr = $("tr", $grid_productos).size();
						noTr++;
						var id_producto = prodGrid['producto_id'];
						var codigo = prodGrid['codigo'];
						var descripcion = prodGrid['descripcion'];
						var unidad = prodGrid['unidad'];
						var noDec = prodGrid['no_dec'];
						var existencia = parseFloat(prodGrid['existencia']).toFixed(noDec);
						var cant_traspaso = parseFloat(prodGrid['cant_traspaso']).toFixed(noDec);
						var densidad=prodGrid['densidad'];
						var idPres = prodGrid['presentacion_id'];
						var exisPres = parseFloat(0).toFixed(noDec);
						var cantTraspasoPres = parseFloat(0).toFixed(noDec);
						var controlExisPres = entry['Par'][0]['exis_pres'];
						
						if(controlExisPres=='true'){
							//convertir las Unidades en Presentaciones
							var cantTraspasoPres = $convertirUniAPres(idPres, cant_traspaso, entry['Presentaciones']);
						}
						
						var cadena_tr = $genera_tr(noTr, id_producto, codigo, descripcion, unidad, existencia, cant_traspaso, densidad, exisPres, cantTraspasoPres);
						
						//agregar tr al grid
						$grid_productos.append(cadena_tr);
						
						//carga select con las Presentaciones del Producto
						$grid_productos.find('select.pres'+noTr).children().remove();
						var pres_hmtl = '';
						if (parseInt(idPres) <=0){
							pres_hmtl = '<option value="0">[--Seleccionar--]</option>';
						}
						$.each(entry['Presentaciones'],function(entryIndex,pres){
							if(parseInt(pres['id'])==parseInt(idPres)){
								pres_hmtl += '<option value="' + pres['id'] + '" selected="yes">' + pres['titulo'] + '</option>';
							}else{
								//pres_hmtl += '<option value="' + pres['id'] + '">' + pres['titulo'] + '</option>';
							}
						});
						$grid_productos.find('select.pres'+noTr).append(pres_hmtl);
						
						//$aplicar_evento_campo_cantidad($grid_productos.find('.cant_traspaso'+noTr), "");
						
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
				var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfTraspaso/'+$identificador.val()+'/'+iu+'/out.json';
				window.location.href=input_json;
			});
			
			
			
			
			//deshabilitar tecla enter  en todo el plugin
			$('#forma-invtraspasos-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			
			//Ligamos el boton cancelar al evento click para eliminar la forma
			$cancelar_plugin.bind('click',function(){
				var remove = function() {$(this).remove();};
				$('#forma-invtraspasos-overlay').fadeOut(remove);
			});
			
			$cerrar_plugin.bind('click',function(){
				var remove = function() {$(this).remove();};
				$('#forma-invtraspasos-overlay').fadeOut(remove);
			});
			
		}
	}

	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllTraspasos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllTraspasos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvtraspasos00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



