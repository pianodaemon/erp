$(function() {
	
	var DataObject;
	
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	
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
	
	
	//Carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, $arreglo_elementos, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, fijo){
		var select_html = '';
		
		if(texto_elemento_cero != ''){
			select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		}
		
		if(parseInt(elemento_seleccionado)<=0 && texto_elemento_cero==''){
			select_html = '<option value="0">[--- ---]</option>';
		}
		
		$.each($arreglo_elementos,function(entryIndex,elemento){
			if( parseInt(elemento[index_elem]) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + elemento[index_elem] + '" selected="yes">' + elemento[index_text_elem] + '</option>';
			}else{
				if(!fijo){
					select_html += '<option value="' + elemento[index_elem] + '" >' + elemento[index_text_elem] + '</option>';
				}
			}
		});
		
		$campo_select.children().remove();
		$campo_select.append(select_html);
	}
	
	
	
	//Aplicar solo lectura
	$aplica_read_only_input_text = function($campo){
		$campo.attr("readonly", true);
		$campo.css({'background' : '#f0f0f0'});
	}
	
	
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
        var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/com_requisicion";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_requisicion = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Requisici&oacute;n');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	
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
		
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
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
	
	
	
	$iniciar_campos_generales = function(){
		
		var input_json_cuentas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInicializar.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_cuentas,$arreglo,function(data){
			
			/*
			$busqueda_select_sucursal.children().remove();
			var suc_hmtl = '';
			if(data['Data']['versuc']==true){
				//Aqui carga todas las sucursales porque el usuario es un administrador
				suc_hmtl = '<option value="0" selected="yes">[--- Todos ---]</option>';
				$.each(data['Data']['Suc'],function(entryIndex,suc){
					suc_hmtl += '<option value="' + suc['id'] + '">'+ suc['titulo'] + '</option>';
				});
			}else{
				//Aqui solo debe cargar la sucursal del usuario logueado
				$.each(data['Data']['Suc'],function(entryIndex,suc){
					if(parseInt(suc['id'])==parseInt(data['Data']['suc_id'])){
						suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
					}
				});
			}
			$busqueda_select_sucursal.append(suc_hmtl);
			*/
			
			DataObject = data['Data'];
			
			//$busqueda_select_sucursal.focus();
		});
	}
	
	
	$iniciar_campos_generales();
	
	
	
	
	
	
	
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
            var $select_prod_tipo = $('#forma-com_requisicion-window').find('select[name=prodtipo]');
            $('#forma-com_requisicion-window').find('#submit').mouseover(function(){
                $('#forma-com_requisicion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                //$('#forma-com_requisicion-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
            })
            $('#forma-com_requisicion-window').find('#submit').mouseout(function(){
                $('#forma-com_requisicion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                //$('#forma-com_requisicion-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
            })
            $('#forma-com_requisicion-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-com_requisicion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-com_requisicion-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-com_requisicion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-com_requisicion-window').find('#close').mouseover(function(){
                $('#forma-com_requisicion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-com_requisicion-window').find('#close').mouseout(function(){
                $('#forma-com_requisicion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-com_requisicion-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-com_requisicion-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-com_requisicion-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-com_requisicion-window').find("ul.pestanas li").click(function() {
                $('#forma-com_requisicion-window').find(".contenidoPes").hide();
                $('#forma-com_requisicion-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-com_requisicion-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
	$busca_productos = function(sku_buscar){
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
		$campo_sku.val(sku_buscar);
		
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
					$('#forma-com_requisicion-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-com_requisicion-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-com_requisicion-window').find('input[name=sku_producto]').focus();
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
	
	
	
	
	
	//buscador de presentaciones disponibles para un producto
                   
	$buscador_presentaciones_producto = function($fecha_compromiso, sku_producto,$nombre_producto,$grid_productos, $sku_producto){
			//verifica si el campo sku no esta vacio para realizar busqueda
			if(sku_producto != ''){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesProducto.json';
				$arreglo = {'sku':sku_producto,
				            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					   };

				var trr = '';
				
				$.post(input_json,$arreglo,function(entry){

					//verifica si el arreglo  retorno datos
					if (entry['Presentaciones'].length > 0){
                                            $(this).modalPanel_Buscapresentacion();
                                            
						var $dialogoc =  $('#forma-buscapresentacion-window');
						$dialogoc.append($('div.buscador_presentaciones').find('table.formaBusqueda_presentaciones').clone());
						$('#forma-buscapresentacion-window').css({"margin-left": -200, "margin-top": -180});
						
						var $tabla_resultados = $('#forma-buscapresentacion-window').find('#tabla_resultado');
						//var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('a[href*=cencela]');
						var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('#cencela');
						$tabla_resultados.children().remove();
						
						
						$cancelar_plugin_busca_lotes_producto.mouseover(function(){
							$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
						});
						$cancelar_plugin_busca_lotes_producto.mouseout(function(){
							$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
						});
						
						//crea el tr con los datos del producto seleccionado
						$.each(entry['Presentaciones'],function(entryIndex,pres){
							trr = '<tr>';
								trr += '<td width="100">';
									trr += '<span class="id_prod" style="display:none">'+pres['id']+'</span>';
									trr += '<span class="sku">'+pres['sku']+'</span>';
								trr += '</td>';
								trr += '<td width="250"><span class="titulo">'+pres['titulo']+'</span></td>';
								trr += '<td width="80">';
									trr += '<span class="unidad" style="display:none">'+pres['unidad']+'</span>';
									trr += '<span class="id_pres" style="display:none">'+pres['id_presentacion']+'</span>';
									trr += '<span class="pres">'+pres['presentacion']+'</span>';
									trr += '<span class="dec" style="display:none">'+pres['decimales']+'</span>';
								trr += '</td>';
							trr += '</tr>';
							$tabla_resultados.append(trr);
						});//termina llamada json
						
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
							//llamada a la funcion que busca y agrega producto al grid, se le pasa como parametro el lote y el almacen
							var id_prod = $(this).find('span.id_prod').html();
							var sku = $(this).find('span.sku').html();
							var titulo = $(this).find('span.titulo').html();
							var unidad = $(this).find('span.unidad').html();
							var id_pres = $(this).find('span.id_pres').html();
							var pres = $(this).find('span.pres').html();
							var num_dec = $(this).find('span.dec').html();
							var prec_unitario=" ";
							var id_moneda=0;
							
							//llamada a la funcion que agrega el producto al grid
							$agrega_producto_grid($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres,prec_unitario,num_dec);
							
							//$nombre_producto.val(titulo);//muestra el titulo del producto 
							
							$sku_producto.val('');
							$nombre_producto.val('');
							
							//elimina la ventana de busqueda
							var remove = function() {$(this).remove();};
							$('#forma-buscapresentacion-overlay').fadeOut(remove);
						});
						
						$cancelar_plugin_busca_lotes_producto.click(function(event){
							//event.preventDefault();
							var remove = function() {$(this).remove();};
							$('#forma-buscapresentacion-overlay').fadeOut(remove);
							$nombre_producto.val('');
							$sku_producto.focus();
						});
					}else{
						jAlert('El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.', 'Atencion!', function(r) {
							$sku_producto.val('');
							$sku_producto.focus();
						});
					}
				});
			}else{
				jAlert('Es necesario ingresar un Sku de producto valido.', 'Atencion!', function(r) {
					$sku_producto.val('');
					$sku_producto.focus();
				});
			}
	}//termina buscador dpresentaciones disponibles de un producto
	
    
    
    var $grid_productos = $('#forma-com_requisicion-window').find('#grid_productos');
    $grid_productos.find('tr').each(function (index){
			if(( $(this).find('#cant').val() != ' ' || $(this).find('#cant').val() != 0 ) ){
			     $(this).find('#cant').val()
			}else{
				 jAlert("Ingrese una cantidad",'Atencion');
				 return false;
			}
     });

	
	
	//agregar producto al grid
	$agrega_producto_grid = function($grid_productos,id_prod,sku,titulo,unidad,id_pres,pres,prec_unitario,$select_moneda, id_moneda, $tipo_cambio,num_dec){
		
		var encontrado = 0;
		//busca el sku y la presentacion en el grid
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#skuprod').val() == sku.toUpperCase() )  && (parseInt($(this).find('#idpres').val())== parseInt(id_pres) ) && (parseInt($(this).find('#elim').val())!=0)){
				encontrado=1;//el producto ya esta en el grid
			}
		});
		
		
		if(parseInt(encontrado)!=1){//si el producto no esta en el grid entra aqui
			//ocultamos el boton facturar para permitir Guardar los cambios  antes de facturar
			$('#forma-com_requisicion-window').find('#facturar').hide();
			//obtiene numero de trs
			var tr = $("tr", $grid_productos).size();
			tr++;
			
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
					trr += '<input type="hidden" 	name="eliminado" id="elim" value="1">';    //el 1 significa que el registro no ha sido eliminado
					trr += '<input type="hidden" 	name="iddetalle" id="idd"  value="0">';    //este es el id del registro que ocupa el producto en la tabla com_oc_req_detalles
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
					trr += '<input type="hidden" 	name="id_producto" id="idprod" value="'+ id_prod +'">';
					trr += '<INPUT TYPE="text" 		name="sku'+ tr +'" value="'+ sku +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
					trr += '<INPUT TYPE="text" 		name="nombre'+ tr +'" 	value="'+ titulo +'" id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ unidad +'" id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
					trr += '<INPUT type="hidden"    name="id_presentacion"      value="'+  id_pres +'" id="idpres">';
					//trr += '<INPUT type="hidden"    name="numero_decimales"     value="'+  num_dec +'" id="numdec">';
					trr += '<INPUT TYPE="text" 	name="presentacion'+ tr +'" value="'+  pres +'" id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<INPUT TYPE="text" 	name="cantidad" value=" " id="cant" class="cant'+ tr +'" style="width:76px;">';
				trr += '</td>';
			
			trr += '</tr>';
            
			$grid_productos.append(trr);
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se cambia el  espacio por comillas
			$grid_productos.find('input.cant'+ tr).focus(function(e){
				if($(this).val() == ' '){
					$(this).val('');
				}
			});
			
			
			$grid_productos.find('input.cant'+ tr).keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					jAlert("Solo se permiten numeros",'Atencion!!!');
					return false;
				}
			});
			
			
			//recalcula importe al perder enfoque el campo cantidad
			$grid_productos.find('input.cant'+ tr).blur(function(){
				if ($(this).val().trim() == ''){
					jAlert('Ingrese una cantidad.', 'Atencion!', function(r) {
						//$grid_productos.find('input.cant'+ tr).focus();
					});
				}
			});
			
			//elimina un producto del grid
			$grid_productos.find('#delete'+ tr).bind('click',function(event){
				event.preventDefault();
				if(parseInt($(this).parent().find('#elim').val()) != 0){
					//asigna espacios en blanco a todos los input de la fila eliminada
					$(this).parent().parent().find('input').val(' ');
					
					//asigna un 0 al input eliminado como bandera para saber que esta eliminado
					$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
					
					//oculta la fila eliminada
					$(this).parent().parent().hide();
				}
			});
			
		}else{
			jAlert("El producto: "+sku+" con presentacion: "+pres+" ya se encuentra en el listado, seleccione otro diferente.", 'Atencion!');
		}
		
		//Asignar enfoque al registro nuevo
		$grid_productos.find('input.cant'+ tr).focus();
		
	}//termina agregar producto al grid
	
	
	
	
	//nueva Requisicion
	$new_requisicion.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_com_requisicion();
                        
		var form_to_show = 'formacom_requisicion';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		
		$('#forma-com_requisicion-window').css({"margin-left": -260, 	"margin-top": -235});
		
		$forma_selected.prependTo('#forma-com_requisicion-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getcom_requisicion.json';
		$arreglo = {'id_requisicion':id_to_show,
			    'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
			   };
        
		var $id_requisicion = $('#forma-com_requisicion-window').find('input[name=id_requisicion]');
		var $total_tr = $('#forma-com_requisicion-window').find('input[name=total_tr]');
		var $folio_requisicion = $('#forma-com_requisicion-window').find('input[name=folio_requisicion]');
                
		var $fecha_compromiso = $('#forma-com_requisicion-window').find('input[name=fecha_compromiso]');
		var $observaciones = $('#forma-com_requisicion-window').find('textarea[name=observaciones]');
         
        var $select_empleado = $('#forma-com_requisicion-window').find('select[name=select_empleado]');
        var $select_departamento = $('#forma-com_requisicion-window').find('select[name=select_departamento]');
        var $folio_pedido = $('#forma-com_requisicion-window').find('input[name=folio_pedido]');
        var $etiqueta_folio_pedido =  $('#forma-com_requisicion-window').find('#etiqueta_folio_pedido');
        
        
		var $cancelar_requisicion = $('#forma-com_requisicion-window').find('#cancelar_requisicion').hide();
		
		var $sku_producto = $('#forma-com_requisicion-window').find('input[name=sku_producto]');
		var $nombre_producto = $('#forma-com_requisicion-window').find('input[name=nombre_producto]');
		//buscar producto
		var $busca_sku = $('#forma-com_requisicion-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-com_requisicion-window').find('a[href*=agregar_producto]');
		
        var $descargarpdf = $('#forma-com_requisicion-window').find('#descargarpdf');
      
		
		//grid de productos
		var $grid_productos = $('#forma-com_requisicion-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-com_requisicion-window').find('#div_warning_grid').find('#grid_warning');
		
	
		var $cerrar_plugin = $('#forma-com_requisicion-window').find('#close');
		var $cancelar_plugin = $('#forma-com_requisicion-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-com_requisicion-window').find('#submit');
		
		
		
		$id_requisicion.val(0);  //para nueva requisicion   el  id es 0
		$folio_pedido.val('');
		
		$aplica_read_only_input_text($folio_requisicion);
		$aplica_read_only_input_text($folio_pedido);
		$aplica_read_only_input_text($fecha_compromiso);
		
		
		$descargarpdf.hide();
		$folio_pedido.hide();
		$etiqueta_folio_pedido.hide();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Requisicion se guard&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-com_requisicion-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-com_requisicion-window').find('.div_one').css({'height':'545px'});//sin errores
				$('#forma-com_requisicion-window').find('.pocpedidos_div_one').css({'height':'568px'});//con errores
				$('#forma-com_requisicion-window').find('div.interrogacion').css({'display':'none'});

				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});

				$('#forma-com_requisicion-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-com_requisicion-window').find('#div_warning_grid').find('#grid_warning').children().remove();

				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-com_requisicion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
						
						if(parseInt($("tr", $grid_productos).size())>0){
							for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
								if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
									//alert(tmp.split(':')[0]);
									$('#forma-com_requisicion-window').find('.com_requisicion_div_one').css({'height':'568px'});
									//$('#forma-com_requisicion-window').find('.div_three').css({'height':'910px'});
									
									$('#forma-com_requisicion-window').find('#div_warning_grid').css({'display':'block'});
									if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
										$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
										//alert();
									}else{
										if(tmp.split(':')[0].substring(0, 5) == 'costo'){
											$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
										}
									}
									
									//$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
									//$grid_productos.find('select[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
									
									var tr_warning = '<tr>';
										tr_warning += '<td width="20"><div><IMG SRC="../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
										tr_warning += '<td width="120">';
										tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
										tr_warning += '</td>';
										tr_warning += '<td width="200">';
										tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
										tr_warning += '</td>';
										tr_warning += '<td width="235">';
										tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:230px; color:red">';
										tr_warning += '</td>';
									tr_warning += '</tr>';
									$grid_warning.append(tr_warning);
								}
							}
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
		$.post(input_json,$arreglo,function(entry){
			
			//aqui van los  campos cargados (desde que se le da clic en nuevo ----en este caso no se esta cargando ningun campo
                       
		},"json");//termina llamada json
		*/
		
		
		//Carga select de Empleados
		var elemento_seleccionado = DataObject['emplActualId'];
		var texto_elemento_cero = '';
		var index_elem = 'id';
		var index_text_elem = 'nombre';
		var option_fijo = false;
		$carga_campos_select($select_empleado, DataObject['Empdos'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
		
		
		//Carga select de Departamentos
		elemento_seleccionado = 0;
		$.each(DataObject['Empdos'],function(entryIndex,emp){
			//Depende del empleado, hay que seleccionar el departamento por default si es que tiene asignado
			if(parseInt(emp['id'])==parseInt($select_empleado.val())){
				elemento_seleccionado = emp['depto_id'];
			}
		});
		texto_elemento_cero = '[--- Seleccionar ---]';
		index_elem = 'id';
		index_text_elem = 'titulo';
		option_fijo = false;
		$carga_campos_select($select_departamento, DataObject['Deptos'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
		
		
		$fecha_compromiso.val(DataObject['fecha_actual']);
		
		
                
		$fecha_compromiso.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_compromiso.DatePicker({
			format:'Y-m-d',
			date: $fecha_compromiso.val(),
			current: $fecha_compromiso.val(),
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
				$fecha_compromiso.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_compromiso.val(),mostrarFecha());
					
					if (valida_fecha==true){
						$fecha_compromiso.DatePickerHide();	
					}else{
						jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
						$fecha_compromiso.val(mostrarFecha());
					}
				}
			}
		});



		
		
		
		
		//buscador de productos
		$busca_sku.click(function(event){
			event.preventDefault();
			$busca_productos($sku_producto.val());
		});
		
		//agregar producto al grid
		$agregar_producto.click(function(event){
			event.preventDefault();
			$buscador_presentaciones_producto($fecha_compromiso, $sku_producto.val(),$nombre_producto,$grid_productos, $sku_producto);
		});
		
		//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$sku_producto.keypress(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			$total_tr.val(trCount);
			if(parseInt(trCount) > 0){
				$grid_productos.find('tr').each(function (index){
					$(this).find('#cant').val(quitar_comas( $(this).find('#cant').val() ));
					var campo_cantidad=$(this).find('#cant').val(quitar_comas( $(this).find('#cant').val() ))
				});
				
				$grid_productos.find('tr').find('#cant').each(function (index){
					// alert($(this).val());
					if($(this).val()==' '){
						jAlert("No se permiten campos vacios, ingrese una cantidad.",'Atencion!!!');
						return false;
					};
				});
			}else{
				jAlert("No hay datos para actualizar", 'Atencion!');
				return false;
			}
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-com_requisicion-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-com_requisicion-overlay').fadeOut(remove);
		});
		
	});
	
	
        
	var carga_formaordencompra00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_requisicion':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  la Orden de Compra?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Orden fue fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Orden de Compra no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-com_requisicion-window').remove();
			$('#forma-com_requisicion-overlay').remove();
            
			var form_to_show = 'formacom_requisicion';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_com_requisicion();
			
			$('#forma-com_requisicion-window').css({"margin-left": -260, 	"margin-top": -235});
			
			$forma_selected.prependTo('#forma-com_requisicion-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $total_tr = $('#forma-com_requisicion-window').find('input[name=total_tr]');
			var $id_requisicion = $('#forma-com_requisicion-window').find('input[name=id_requisicion]');
			var $accion_proceso = $('#forma-com_requisicion-window').find('input[name=accion_proceso]');
			var $folio_requisicion = $('#forma-com_requisicion-window').find('input[name=folio_requisicion]');
			
			var $fecha_compromiso = $('#forma-com_requisicion-window').find('input[name=fecha_compromiso]');
			var $observaciones = $('#forma-com_requisicion-window').find('textarea[name=observaciones]');
			
			var $select_empleado = $('#forma-com_requisicion-window').find('select[name=select_empleado]');
			var $select_departamento = $('#forma-com_requisicion-window').find('select[name=select_departamento]');
			var $folio_pedido = $('#forma-com_requisicion-window').find('input[name=folio_pedido]');
			var $etiqueta_folio_pedido =  $('#forma-com_requisicion-window').find('#etiqueta_folio_pedido');
			
			var $sku_producto = $('#forma-com_requisicion-window').find('input[name=sku_producto]');
			var $nombre_producto = $('#forma-com_requisicion-window').find('input[name=nombre_producto]');
			//buscar producto
			var $busca_sku = $('#forma-com_requisicion-window').find('a[href*=busca_sku]');
			//href para agregar producto al grid
			var $agregar_producto = $('#forma-com_requisicion-window').find('a[href*=agregar_producto]');
			
			var $descargarpdf = $('#forma-com_requisicion-window').find('#descargarpdf');
			//var $identificador = $('#forma-com_requisicion-window').find('input[name=identificador]');
			

			
			var $cancelar_requisicion = $('#forma-com_requisicion-window').find('#cancelar_requisicion');
			var $cancelado = $('#forma-com_requisicion-window').find('input[name=cancelado]');
			
			//grid de productos
			var $grid_productos = $('#forma-com_requisicion-window').find('#grid_productos');
			//grid de errores
			var $grid_warning = $('#forma-com_requisicion-window').find('#div_warning_grid').find('#grid_warning');
			
		        
			
			var $cerrar_plugin = $('#forma-com_requisicion-window').find('#close');
			var $cancelar_plugin = $('#forma-com_requisicion-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-com_requisicion-window').find('#submit');
			
			
			
			$aplica_read_only_input_text($folio_requisicion);
			$aplica_read_only_input_text($folio_pedido);
			$aplica_read_only_input_text($fecha_compromiso);
			
			
			$folio_pedido.hide();
			$etiqueta_folio_pedido.hide();
			
			$cancelado.hide();
			
			                      
			
			if(accion_mode == 'edit'){
				$accion_proceso.attr({'value' : "edit"});
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getcom_requisicion.json';
				$arreglo = {'id_requisicion':id_to_show,
                                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                           };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-com_requisicion-window').find('div.interrogacion').css({'display':'none'});
						
						if($accion_proceso.val() == 'cancelar'){
							if ( data['actualizo'] == "1" ){
								jAlert("La Requisicion se Cancel&oacute; con &eacute;xito", 'Atencion!');
							}else{
								jAlert(data['actualizo'], 'Atencion!');
							}
						}else{
							jAlert("La Requisicion  se guard&oacute; con &eacute;xito", 'Atencion!');
						}
						
						var remove = function() {$(this).remove();};
						$('#forma-com_requisicion-overlay').fadeOut(remove);
						
						//ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
						$submit_actualizar.hide();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-com_requisicion-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-com_requisicion-window').find('.pocpedidos_div_one').css({'height':'568px'});//con errores
						$('#forma-com_requisicion-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-com_requisicion-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-com_requisicion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');

							if( longitud.length > 1 ){
								$('#forma-com_requisicion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
											$('#forma-com_requisicion-window').find('.pocpedidos_div_one').css({'height':'568px'});
											$('#forma-com_requisicion-window').find('#div_warning_grid').css({'display':'block'});
											
											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
												$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}else{
												if(tmp.split(':')[0].substring(0, 5) == 'costo'){
														$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
												}
											}
											
											var tr_warning = '<tr>';
												tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
												tr_warning += '<td width="120">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="200">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="235">';
												tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:230px; color:red">';
												tr_warning += '</td>';
											tr_warning += '</tr>';
											$grid_warning.append(tr_warning);
										}
									}
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
					$id_requisicion.val(entry['datosRequisicion'][0]['id']);
					$folio_requisicion.val(entry['datosRequisicion'][0]['folio']);
					$fecha_compromiso.val(entry['datosRequisicion'][0]['fecha_compromiso']);
					$observaciones.text(entry['datosRequisicion'][0]['observaciones']);
					$folio_pedido.val(entry['datosRequisicion'][0]['pedido']);
					
					if(parseInt(entry['datosRequisicion'][0]['tipo'])==2){
						$folio_pedido.show();
						$etiqueta_folio_pedido.show();
					}
					
					//Carga select de Empleados
					var elemento_seleccionado = entry['datosRequisicion'][0]['empleado_id'];
					var texto_elemento_cero = '';
					var index_elem = 'id';
					var index_text_elem = 'nombre';
					var option_fijo = false;
					$carga_campos_select($select_empleado, DataObject['Empdos'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
					
					//Carga select de Departamentos
					elemento_seleccionado = entry['datosRequisicion'][0]['depto_id'];
					texto_elemento_cero = '[--- Seleccionar ---]';
					index_elem = 'id';
					index_text_elem = 'titulo';
					option_fijo = false;
					$carga_campos_select($select_departamento, DataObject['Deptos'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
					
		
					
					//----------------------------------------------------------------
					
					
					
					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prod){
							//obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
									trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
									trr += '<input type="hidden" name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
									trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">'; //este es el id del registro que ocupa el producto en la tabla com_oc_req_detalles
									//trr += '<span id="elimina">1</span>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="114">';
									trr += '<input type="hidden" name="id_producto" id="idprod" value="'+ prod['inv_prod_id'] +'">';
									trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
								trr += '<INPUT TYPE="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<INPUT type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
									trr += '<INPUT TYPE="text"		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<INPUT TYPE="text" 	name="cantidad" value="'+  prod['cantidad'] +'" 		id="cant" style="width:76px;">';
							trr += '</td>';
							
							
							trr += '</tr>';
							$grid_productos.append(trr);
							
							
							if(parseInt(entry['datosRequisicion'][0]['status'])==1){
								//Tipo de OC generada a partir de una requisicion
								$grid_productos.find('#delete'+ tr).hide();
							}
							
                            
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('#cant').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});

							//recalcula importe al perder enfoque el campo cantidad
							$grid_productos.find('#cant').blur(function(){
								if ($(this).val() == ''){
									$(this).val(' ');
								}
								if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cost').val() != ' ') )
								{   //calcula el importe
									$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cost').val()));
									//redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val( Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100 );
									$(this).parent().parent().find('#import').val( parseFloat($(this).parent().parent().find('#import').val()).toFixed(2) );

									//calcula el impuesto para este producto multiplicando el importe por el valor del iva
									$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat(  $(this).parent().parent().find('#ivalorimp').val()  ));

								}else{
									$(this).parent().parent().find('#import').val('');
									$(this).parent().parent().find('#totimp').val('');
								}
								$calcula_totales();//llamada a la funcion que calcula totales
							});
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('#cost').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							//recalcula importe al perder enfoque el campo costo
							$grid_productos.find('#cost').blur(function(){
								if ($(this).val() == ''){
									$(this).val(' ');
								}
								if( ($(this).val() != ' ') && ($(this).parent().parent().find('#cant').val() != ' ') )
								{	//calcula el importe
									$(this).parent().parent().find('#import').val(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cant').val()));
									//redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
									$(this).parent().parent().find('#import').val( parseFloat($(this).parent().parent().find('#import').val()).toFixed(2));
									
									//calcula el impuesto para este producto multiplicando el importe por el valor del iva
									$(this).parent().parent().find('#totimp').val(parseFloat($(this).parent().parent().find('#import').val()) * parseFloat( $(this).parent().parent().find('#ivalorimp').val()  ));
								}else{
									$(this).parent().parent().find('#import').val('');
									$(this).parent().parent().find('#totimp').val('');
								}
								$calcula_totales();//llamada a la funcion que calcula totales
							});
							
							//validar campo costo, solo acepte numeros y punto
							$permitir_solo_numeros( $grid_productos.find('#cost') );
							$permitir_solo_numeros( $grid_productos.find('#cant') );
							
							//elimina un producto del grid
							$grid_productos.find('#delete'+ tr).bind('click',function(event){
								event.preventDefault();
                                                                if(parseInt($(this).parent().find('#elim').val()) != 0){
									var iddetalle = $(this).parent().find('#idd').val();
									
									//asigna espacios en blanco a todos los input de la fila eliminada
									$(this).parent().parent().find('input').val(' ');
									
									//asigna un 0 al input eliminado como bandera para saber que esta eliminado
									$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
									$(this).parent().find('#idd').val(iddetalle);
									//oculta la fila eliminada
									$(this).parent().parent().hide();
									
								}
							});
						});
					}
					
                                        
                                        
                                        
                                        
					if(entry['datosGrid'][0]['status']=='t' ){
						$cancelar_requisicion.hide();
						$('#forma-com_requisicion-window').find('a[href*=busca_sku]').hide();
						$('#forma-com_requisicion-window').find('a[href*=agregar_producto]').hide();
						$('#forma-com_requisicion-window').find('#submit').hide();
						$('#forma-com_requisicion-window').find('#cancelar_orden_compra').hide();
						$folio_requisicion.attr('disabled','-1'); //deshabilitar
						
						$sku_producto.attr('disabled','-1'); //deshabilitar
						$fecha_compromiso.attr('disabled','-1'); //deshabilitar
						$nombre_producto.attr('disabled','-1'); //deshabilitar
						$observaciones.attr('disabled','-1'); //deshabilitar
                                                
						$grid_productos.find('#cant').attr('disabled','-1'); //deshabilitar campos cantidad del grid
						$grid_productos.find('#cost').attr('disabled','-1'); //deshabilitar campos costo del grid
						$grid_productos.find('#import').attr('disabled','-1'); //deshabilitar campos importe del grid
					}else{ 
						$('#forma-com_requisicion-window').find('a[href*=busca_sku]').show();
						$('#forma-com_requisicion-window').find('a[href*=agregar_producto]').show();
						$('#forma-com_requisicion-window').find('#submit').show();
					}
				
				
				
				
					$fecha_compromiso.click(function (s){
						var a=$('div.datepicker');
						a.css({'z-index':100});
					});
					
					$fecha_compromiso.DatePicker({
						format:'Y-m-d',
						date: $fecha_compromiso.val(),
						current: $fecha_compromiso.val(),
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
							$fecha_compromiso.val(formated);
							if (formated.match(patron) ){
								var valida_fecha=mayor($fecha_compromiso.val(),mostrarFecha());
								
								if (valida_fecha==true){
									$fecha_compromiso.DatePickerHide();	
								}else{
									jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
									$fecha_compromiso.val(mostrarFecha());
								}
							}
						}
					});
				
				});//termina llamada json
               
               
            //aqui                 
                $descargarpdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfRequisicion/'+$id_requisicion.val()+'/'+iu+'/out.json';
					//alert(input_json);
					window.location.href=input_json;
				});
			//end
				
				

                
				
                                
				//buscador de productos
				$busca_sku.click(function(event){
					event.preventDefault();
					$busca_productos($sku_producto.val());
				});
				
				//agregar producto al grid
				$agregar_producto.click(function(event){
					event.preventDefault();
					$buscador_presentaciones_producto($fecha_compromiso.val(),$sku_producto.val(),$nombre_producto,$grid_productos, $sku_producto);
				});
				
				
				//ejecutar clic del href Agregar producto al pulsar enter en el campo sku del producto
				$sku_producto.keypress(function(e){
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
				});
				
				
				
				
				$cancelar_requisicion.click(function(e){
					$accion_proceso.attr({'value' : "cancelar"});
					jConfirm('Desea Cancelar la Requisicion?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
                                                       $submit_actualizar.parents("FORM").submit();
						}else{
							$accion_proceso.attr({'value' : "edit"});
						}
					});
					// Always return     false here since we don't know what jConfirm is going to do
					return false;
				});
				
                                
                                
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
					if(parseInt(trCount) > 0){
						$grid_productos.find('tr').each(function (index){
							$(this).find('#cost').val(quitar_comas( $(this).find('#cost').val() ));
						});
                                                
                                                return true;
					}else{
						jAlert("No hay datos para actualizar", 'Atencion!');
						return false;
					}
				});
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-com_requisicion-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-com_requisicion-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllcom_requisicion.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllcom_requisicion.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaordencompra00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }  
      
    $get_datos_grid();   
   
});
