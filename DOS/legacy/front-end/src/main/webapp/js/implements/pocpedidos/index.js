$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};

    var fullfill_select = function( $_s, _l, _c, _cap ) {
        var _t;

        $.each( _l, function( entryIndex, i ){
            if( _c == i[ 'id' ] ) {
                _t += '<option value="' + i[ 'id' ] + '" selected="yes" >' + i[ _cap ] + '</option>';
            }
            else {
                _t += '<option value="' + i[ 'id' ] + '"  >' + i[ _cap ] + '</option>';
            }
        });

        $_s.append( _t );
    };
	
	//Arreglo que almacena las unidades de medida
	var arrayUM;
	//Variable que indica si se debe permitir cambiar la unidad de medida al agregar el producto
	var cambiarUM;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/pocpedidos";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $new_pedido = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Pedidos de Clientes');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $busqueda_select_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_agente]');
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
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val()+ "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "producto" + signo_separador + $busqueda_producto.val() + "|";
		valor_retorno += "agente" + signo_separador + $busqueda_select_agente.val();
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
	
	
	//esta funcion carga los datos para el buscador del paginado
	$cargar_datos_buscador_principal= function(){
		var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_lineas,$arreglo,function(data){
			//Alimentando los campos select_agente
			$busqueda_select_agente.children().remove();
			var agente_hmtl = '<option value="0">[-Seleccionar Agente-]</option>';
			$.each(data['Agentes'],function(entryIndex,agente){
				agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
			});
			$busqueda_select_agente.append(agente_hmtl);
		});
	}
	
	//llamada a funcion
	$cargar_datos_buscador_principal();
	
	$limpiar.click(function(event){
		$busqueda_folio.val('');
		$busqueda_cliente.val('');
		$busqueda_codigo.val('');
		$busqueda_producto.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		//llamada a funcion al limpiar campos
		$cargar_datos_buscador_principal();
		
		$busqueda_folio.focus();
		
		$get_datos_grid();
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
	/*
	//desencadena evento del $campo_ejecutar al pulsar Enter en $campo
	$(this).aplicarEventoKeypressEjecutaTrigger = function($campo, $campo_ejecutar){
		$campo.keypress(function(e){
			if(e.which == 13){
				$campo_ejecutar.trigger('click');
				return false;
			}
		});
	}
	*/
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_producto, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_agente, $buscar);
	
	
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
	
    
	$busqueda_folio.focus();
	
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-pocpedidos-window').find('select[name=prodtipo]');
            $('#forma-pocpedidos-window').find('#submit').mouseover(function(){
                $('#forma-pocpedidos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                //$('#forma-pocpedidos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
            })
            $('#forma-pocpedidos-window').find('#submit').mouseout(function(){
                $('#forma-pocpedidos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                //$('#forma-pocpedidos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
            })
            $('#forma-pocpedidos-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-pocpedidos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-pocpedidos-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-pocpedidos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-pocpedidos-window').find('#close').mouseover(function(){
                $('#forma-pocpedidos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-pocpedidos-window').find('#close').mouseout(function(){
                $('#forma-pocpedidos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-pocpedidos-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-pocpedidos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-pocpedidos-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-pocpedidos-window').find("ul.pestanas li").click(function() {
                $('#forma-pocpedidos-window').find(".contenidoPes").hide();
                $('#forma-pocpedidos-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-pocpedidos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
	

												
	
	//Buscador de Unidades(Vehiculo)
	$busca_unidades= function($id_vehiculo, $no_economico, $marca_vehiculo, $busca_vehiculo){
		$(this).modalPanel_busquedaunidad();
		var $dialogoc =  $('#forma-busquedaunidad-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_busquedaunidad').find('table.formaBusqueda_busquedaunidad').clone());
		$('#forma-busquedaunidad-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-busquedaunidad-window').find('#tabla_resultado');
		
		var $boton_busquedaunidad = $('#forma-busquedaunidad-window').find('#boton_busquedaunidad');
		var $cancelar_busqueda = $('#forma-busquedaunidad-window').find('#cencela');
		
		var $cadena_noeconomico = $('#forma-busquedaunidad-window').find('input[name=cadena_noeconomico]');
		var $cadena_marca = $('#forma-busquedaunidad-window').find('input[name=cadena_marca]');
		
		
		//funcionalidad botones
		$boton_busquedaunidad.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_busquedaunidad.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_busqueda.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$cancelar_busqueda.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		$cadena_noeconomico.val($no_economico.val());
		$cadena_marca.val($marca_vehiculo.val());
		
		
		//click buscar clientes
		$boton_busquedaunidad.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorUnidades.json';
			$arreglo = { 'no_economico':$cadena_noeconomico.val(),
						 'marca':$cadena_marca.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Vehiculos'],function(entryIndex,vehiculo){
					trr = '<tr>';
						trr += '<td width="180">';
							trr += '<input type="hidden" id="id" value="'+vehiculo['id']+'">';
							trr += '<span class="no_eco">'+vehiculo['numero_economico']+'</span>';
						trr += '</td>';
						trr += '<td width="420"><span class="marca">'+vehiculo['marca']+'</span></td>';
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
				
				//Seleccionar un elemento del resultado
				$tabla_resultados.find('tr').click(function(){
					
					$id_vehiculo.val($(this).find('#id').val());
					$no_economico.val($(this).find('span.no_eco').html());
					$marca_vehiculo.val($(this).find('span.marca').html());
					$busca_vehiculo.hide();
					
					//Aplicar solo lectura una vez que se ha escogido la unidad
					$aplicar_readonly_input($marca_vehiculo);
					
					//Elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-busquedaunidad-overlay').fadeOut(remove);
					
					$no_economico.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_noeconomico.val().trim()!='' || $cadena_marca.val().trim()!=''){
			$boton_busquedaunidad.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_noeconomico, $boton_busquedaunidad);
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_marca, $boton_busquedaunidad);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-busquedaunidad-overlay').fadeOut(remove);
		});
		
		$no_economico.focus();
	}//Termina buscador de Unidades(Vehiculos)

	
	//Buscador de Operadores(Chofer)
	$busca_operadores= function($no_operador, $nombre_operador){
		$(this).modalPanel_busquedaoperador();
		var $dialogoc =  $('#forma-busquedaoperador-window');
		$dialogoc.append($('div.buscador_busquedaoperador').find('table.formaBusqueda_busquedaoperador').clone());
		$('#forma-busquedaoperador-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-busquedaoperador-window').find('#tabla_resultado');
		
		var $boton_busquedaoperador = $('#forma-busquedaoperador-window').find('#boton_busquedaoperador');
		var $cancelar_busqueda = $('#forma-busquedaoperador-window').find('#cencela');
		
		var $cadena_nooperador = $('#forma-busquedaoperador-window').find('input[name=cadena_nooperador]');
		var $cadena_nombre = $('#forma-busquedaoperador-window').find('input[name=cadena_nombre]');
		
		//funcionalidad botones
		$boton_busquedaoperador.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_busquedaoperador.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_busqueda.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$cancelar_busqueda.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		$cadena_nooperador.val($cadena_nooperador.val());
		$cadena_nombre.val($nombre_operador.val());
		
		//click buscar clientes
		$boton_busquedaoperador.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorOperadores.json';
			$arreglo = { 'no_operador':$cadena_nooperador.val(),
						 'nombre':$cadena_nombre.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Operadores'],function(entryIndex,operador){
					trr = '<tr>';
						trr += '<td width="180">';
							trr += '<input type="hidden" id="id" value="'+operador['id']+'">';
							trr += '<span class="no_ope">'+operador['clave']+'</span>';
						trr += '</td>';
						trr += '<td width="420"><span class="nombre">'+operador['nombre']+'</span></td>';
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
				
				//Seleccionar un elemento del resultado
				$tabla_resultados.find('tr').click(function(){
					$no_operador.val($(this).find('span.no_ope').html());
					$nombre_operador.val($(this).find('span.nombre').html());
					
					//Elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-busquedaoperador-overlay').fadeOut(remove);
					
					$no_operador.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_nooperador.val().trim()!='' || $cadena_nombre.val().trim()!=''){
			$boton_busquedaoperador.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_nooperador, $boton_busquedaoperador);
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_nombre, $boton_busquedaoperador);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-busquedaoperador-overlay').fadeOut(remove);
		});
		
		$no_operador.focus();
	}//Termina buscador de Operadores(Choferes)
	
	
	
	//Buscador de Agentes Aduanales
	$busca_agentes_aduanales= function($agena_id, $noagena, $noagenambre, $busca_agena){
		$(this).modalPanel_buscaagen();
		var $dialogoc =  $('#forma-buscaagen-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_buscaagen').find('table.formaBusqueda_buscaagen').clone());
		$('#forma-buscaagen-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscaagen-window').find('#tabla_resultado');
		
		var $boton_buscaagen = $('#forma-buscaagen-window').find('#boton_buscaagen');
		var $cancelar_busqueda = $('#forma-buscaagen-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscaagen-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscaagen-window').find('select[name=filtropor]');
		
		//funcionalidad botones
		$boton_buscaagen.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_buscaagen.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_busqueda.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$cancelar_busqueda.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		var html = '';		
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		
		if($noagena.val() !='' && $noagenambre.val()==''){
			html+='<option value="1" selected="yes">No. de control</option>';
			$cadena_buscar.val($noagena.val());
		}else{
			html+='<option value="1">No. de control</option>';
		}
		if($noagenambre.val()!=''){
			$cadena_buscar.val($noagenambre.val());
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		if($noagena.val() =='' && $noagenambre.val()==''){
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		$select_filtro_por.append(html);
		
		
		//click buscar Agentes Aduanales
		$boton_buscaagen.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorAgenA.json';
			$arreglo = {'cadena':$cadena_buscar.val(),
						 'filtro':$select_filtro_por.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['AgentesAduanales'],function(entryIndex,agen){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="id" value="'+agen['id']+'">';
							trr += '<span class="no_control">'+agen['folio']+'</span>';
						trr += '</td>';
						trr += '<td width="520"><span class="razon">'+agen['razon_social']+'</span></td>';
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
				
				//Seleccionar un elemento del resultado
				$tabla_resultados.find('tr').click(function(){
					$agena_id.val($(this).find('#id').val());
					$noagena.val($(this).find('span.no_control').html());
					$noagenambre.val($(this).find('span.razon').html());
					$busca_agena.hide();
					
					//Aplicar solo lectura una vez que se ha escogido un agente aduanal
					$aplicar_readonly_input($noagenambre);
					
					//Elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaagen-overlay').fadeOut(remove);
					$noagena.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$boton_buscaagen.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $boton_buscaagen);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $boton_buscaagen);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-buscaagen-overlay').fadeOut(remove);
			$noagena.focus();
		});
		
		$cadena_buscar.focus();
	}//Termina buscador de Agentes Aduanales
	
	
	
	//Agregar datos del remitente
	$agregar_datos_remitente = function($rem_id, $nombre_remitente, $noremitente, $dir_remitente, $busca_remitente, rem_id, rem_nombre, rem_numero, rem_dir){
		$rem_id.val(rem_id);
		$nombre_remitente.val(rem_nombre);
		$noremitente.val(rem_numero);
		$dir_remitente.val(rem_dir);
		
		//Aplicar solo lectura una vez que se ha escogido un remitente
		$aplicar_readonly_input($nombre_remitente);
		
		//Oculta link buscar remitente
		$busca_remitente.hide();
		
		$noremitente.focus();
	}
	
	//Buscador de Remitentes
	$busca_remitentes= function($rem_id, $nombre_remitente, $noremitente, $dir_remitente, $id_cliente, $busca_remitente){
		$(this).modalPanel_buscaremitente();
		var $dialogoc =  $('#forma-buscaremitente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_remitentes').find('table.formaBusqueda_remitentes').clone());
		$('#forma-buscaremitente-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscaremitente-window').find('#tabla_resultado');
		
		var $boton_buscaremitente = $('#forma-buscaremitente-window').find('#boton_buscaremitente');
		var $cancelar_busqueda = $('#forma-buscaremitente-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscaremitente-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscaremitente-window').find('select[name=filtropor]');
		
		//funcionalidad botones
		$boton_buscaremitente.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_buscaremitente.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$boton_buscaremitente.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$boton_buscaremitente.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		var html = '';		
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		
		if($noremitente.val() !='' && $nombre_remitente.val()==''){
			html+='<option value="1" selected="yes">No. de control</option>';
			$cadena_buscar.val($noremitente.val());
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if($nombre_remitente.val()!=''){
			$cadena_buscar.val($nombre_remitente.val());
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		if($noremitente.val() =='' && $nombre_remitente.val()==''){
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		$select_filtro_por.append(html);
		
		//click buscar clientes
		$boton_buscaremitente.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorRemitentes.json';
			$arreglo = {'cadena':$cadena_buscar.val(),
						 'filtro':$select_filtro_por.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Remitentes'],function(entryIndex,remitente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="id" value="'+remitente['id']+'">';
							trr += '<input type="hidden" id="dir" value="'+remitente['dir']+'">';
							trr += '<span class="no_control">'+remitente['folio']+'</span>';
						trr += '</td>';
						trr += '<td width="145"><span class="rfc">'+remitente['rfc']+'</span></td>';
						trr += '<td width="375"><span class="razon">'+remitente['razon_social']+'</span></td>';
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
				
				//Seleccionar un elemento del resultado
				$tabla_resultados.find('tr').click(function(){
					var rem_id = $(this).find('#id').val();
					var rem_nombre = $(this).find('span.razon').html();
					var rem_numero = $(this).find('span.no_control').html();
					var rem_dir = $(this).find('#dir').val();
					
					$agregar_datos_remitente($rem_id, $nombre_remitente, $noremitente, $dir_remitente, $busca_remitente, rem_id, rem_nombre, rem_numero, rem_dir);
					
					//Elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaremitente-overlay').fadeOut(remove);

					$nombre_remitente.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$boton_buscaremitente.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $boton_buscaremitente);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $boton_buscaremitente);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-buscaremitente-overlay').fadeOut(remove);
			
			//$('#forma-clientsdf-window').find('input[name=cliente]').focus();
		});		
		$cadena_buscar.focus();
	}//Termina buscador de Remitentes
	
	
	
	//Agregar datos del destinatario
	$agregar_datos_destinatario = function($dest_id, $dest_nombre, $dest_no, $dest_dir, $busca_dest, dest_id, dest_nombre, dest_numero, dest_dir){
		$dest_id.val(dest_id);
		$dest_nombre.val(dest_nombre);
		$dest_no.val(dest_numero);
		$dest_dir.val(dest_dir);
		
		//Aplicar solo lectura una vez que se ha escogido un destinatario
		$aplicar_readonly_input($dest_nombre);
		
		//Oculta link buscar destinatario
		$busca_dest.hide();
		
		$dest_no.focus();
	}
	
	
	//Buscador de Destinatarios
	$busca_destinatarios= function($dest_id, $dest_nombre, $dest_no, $dest_dir, $id_cliente, $busca_dest){
		$(this).modalPanel_buscadestinatario();
		var $dialogoc =  $('#forma-buscadestinatario-window');
		$dialogoc.append($('div.buscador_buscadestinatario').find('table.formaBusqueda_buscadestinatario').clone());
		$('#forma-buscadestinatario-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscadestinatario-window').find('#tabla_resultado');
		
		var $boton_buscadestinatario = $('#forma-buscadestinatario-window').find('#boton_buscadestinatario');
		var $cancelar_busqueda = $('#forma-buscadestinatario-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscadestinatario-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscadestinatario-window').find('select[name=filtropor]');
		
		//funcionalidad botones
		$boton_buscadestinatario.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_buscadestinatario.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_busqueda.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$cancelar_busqueda.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		var html = '';		
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		
		if($dest_no.val() !='' && $dest_nombre.val()==''){
			html+='<option value="1" selected="yes">No. de control</option>';
			$cadena_buscar.val($dest_no.val());
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if($dest_nombre.val()!=''){
			$cadena_buscar.val($dest_nombre.val());
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		if($dest_no.val() =='' && $dest_nombre.val()==''){
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		$select_filtro_por.append(html);
		
		//click buscar clientes
		$boton_buscadestinatario.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorDestinatarios.json';
			$arreglo = {'cadena':$cadena_buscar.val(),
						 'filtro':$select_filtro_por.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Destinatarios'],function(entryIndex,dest){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="id" value="'+dest['id']+'">';
							trr += '<input type="hidden" id="dir" value="'+dest['dir']+'">';
							trr += '<span class="no_control">'+dest['folio']+'</span>';
						trr += '</td>';
						trr += '<td width="145"><span class="rfc">'+dest['rfc']+'</span></td>';
						trr += '<td width="375"><span class="razon">'+dest['razon_social']+'</span></td>';
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
				
				//Seleccionar un elemento del resultado
				$tabla_resultados.find('tr').click(function(){
					var dest_id = $(this).find('#id').val();
					var dest_numero = $(this).find('span.no_control').html();
					var dest_nombre = $(this).find('span.razon').html();
					var dest_dir = $(this).find('#dir').val();
											
					$agregar_datos_remitente($dest_id, $dest_nombre, $dest_no, $dest_dir, $busca_dest, dest_id, dest_nombre, dest_numero, dest_dir);
					
					//Elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscadestinatario-overlay').fadeOut(remove);
					
					$dest_no.focus();
				});
			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$boton_buscadestinatario.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $boton_buscadestinatario);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $boton_buscadestinatario);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-buscadestinatario-overlay').fadeOut(remove);
			$dest_nombre.focus();
		});
		
		$cadena_buscar.focus();
	}//Termina buscador de destinatarios
	
	
	
	
	
	
	
	
	
	
	//buscador de presentaciones disponibles para un producto
	$buscador_direcciones_fiscales = function(id_cliente){
		$(this).modalPanel_df();
		var $dialogoc =  $('#forma-df-window');
		$dialogoc.append($('div.buscador_direcciones_fiscales').find('table.formaBusqueda_df').clone());
		$('#forma-df-window').css({"margin-left": -150, "margin-top": -180});
		
		var $tabla_resultados = $('#forma-df-window').find('#tabla_resultado');
		//var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('a[href*=cencela]');
		var $cancelar_plugin_busca_lotes_producto = $('#forma-df-window').find('#cencela');
		$tabla_resultados.children().remove();
		
		$cancelar_plugin_busca_lotes_producto.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_lotes_producto.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		//aquí se arma la cadena json para traer las Direcciones Fiscales del cliente
		var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDireccionesFiscalesCliente.json';
		$arreglo2 = { 'id_cliente':id_cliente }
		$.post(input_json2,$arreglo2,function(entrydf){
			$('#forma-pocpedidos-window').find('input[name=no_cotizacion]').focus();
			
			//crea el tr con los datos del producto seleccionado
			$.each(entrydf['DirFiscal'],function(entryIndex ,df){
				trr = '<tr>';
					trr += '<td width="430">';
						trr += df['direccion_fiscal'];
						trr += '<input type="hidden" id="iddf" value="'+df['id_df']+'">';
						trr += '<input type="hidden" id="direccion" value="'+df['direccion_fiscal']+'">';
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
				var prec_unitario= $(this).find('span.costo').html();
				$('#forma-pocpedidos-window').find('input[name=id_df]').val($(this).find('#iddf').val());
				$('#forma-pocpedidos-window').find('input[name=dircliente]').val($(this).find('#direccion').val());
				//elimina la ventana de busqueda
				var remove = function() {$(this).remove();};
				$('#forma-df-overlay').fadeOut(remove);
			});
			
			$cancelar_plugin_busca_lotes_producto.click(function(event){
				//event.preventDefault();
				var remove = function() {$(this).remove();};
				$('#forma-df-overlay').fadeOut(remove);
			});
		});
		
	}//termina buscador dpresentaciones disponibles de un producto
	
    
    
    //funcion para aplicar evento a trs de una tabla para permitir seleccionar elemento desde el teclado
    $aplicarEventoSeleccionarTrkeypress = function($grid){
		var tr = $("tr", $grid).size();
		tr;
		
		//$('tr:first', $grid).css({background : '#FBD850'});
		$('tr:eq(0)', $grid).find('td').css({background : '#FBD850'});
		
		$('tr:eq(0)', $grid).focus();
		
		
		//$('tr:first' , $grid).find('td').css({background : '#FBD850'});
		
		//alert($('tr:first' , $grid).find('td:eq(0)').find('#direccion').val());
		
		
		//.css({background : '#FBD850'});
		/*
		$('tr:odd' , $grid).keypress(function () {
			$(this).find('td').css({'background-color': '#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#e7e8ea'});
		});
		
		$('tr:even' , $grid).keypress(function () {
			$(this).find('td').css({'background-color':'#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#FFFFFF'});
		});
		*/
		
		
		/*
		$grid.find('tr').each(function (index){
			$(this).find('td').css({'background':'#FBD850'});
		});
		*/
		
		
		$campo_sku.onkeyup(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
			/*
		var oTable = $('#example').dataTable( {
			"sScrollY": 200,
			"sScrollX": "100%",
			"sScrollXInner": "110%"
		} );
		
		var keys = new KeyTable( {
			"table": document.getElementById('example'),
			"datatable": oTable
		} );
		*/
	}
	
	
	
	
	$agregarDatosClienteSeleccionado = function($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, array_monedas, array_condiciones, array_vendedores, array_metodos_pago, $no_cuenta, $etiqueta_digit, id_cliente, no_control, razon_social, dir_cliente, empresa_immex, tasa_ret_immex, cuenta_mn, cuenta_usd, id_moneda, id_termino, id_vendedor, num_lista_precio, id_metodo_de_pago, tiene_dir_fiscal, cred_susp, pdescto, vdescto){
		
		if(cred_susp=='false'){
			//asignar a los campos correspondientes el sku y y descripcion
			$('#forma-pocpedidos-window').find('input[name=id_cliente]').val( id_cliente );
			$('#forma-pocpedidos-window').find('input[name=nocliente]').val( no_control );
			$('#forma-pocpedidos-window').find('input[name=razoncliente]').val( razon_social );
			$('#forma-pocpedidos-window').find('input[name=empresa_immex]').val( empresa_immex );
			$('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]').val( tasa_ret_immex );
			$('#forma-pocpedidos-window').find('input[name=cta_mn]').val( cuenta_mn );
			$('#forma-pocpedidos-window').find('input[name=cta_usd]').val( cuenta_usd );
			$('#forma-pocpedidos-window').find('input[name=num_lista_precio]').val( num_lista_precio );
			//por default asignamos cero para el campo id de Direccion Fiscal, esto significa que la direccion se tomara de la tabla de clientes
			$('#forma-pocpedidos-window').find('input[name=id_df]').val(0);
			if(parseFloat(vdescto)>0){
				$('#forma-pocpedidos-window').find('input[name=check_descto]').attr('checked',  (pdescto == 'true')? true:false );
				$('#forma-pocpedidos-window').find('input[name=valor_descto]').attr("readonly", false);
				$('#forma-pocpedidos-window').find('input[name=valor_descto]').css({'background' : '#ffffff'});
			}
			$('#forma-pocpedidos-window').find('input[name=pdescto]').val(pdescto);
			$('#forma-pocpedidos-window').find('input[name=valor_descto]').val(vdescto);
			
			if(tiene_dir_fiscal=='true'){
				//llamada a la funcion que busca las direcciones fiscales del cliente.
				//se le pasa como parametro el id del cliente
				$buscador_direcciones_fiscales($('#forma-pocpedidos-window').find('input[name=id_cliente]').val());
			}else{
				//si no tiene varias direcciones fiscales, se asigna la direccion default
				$('#forma-pocpedidos-window').find('input[name=dircliente]').val(dir_cliente);
				$('#forma-pocpedidos-window').find('input[name=id_df]').val(0);
			}
			var moneda_hmtl = '';
			
			
			$select_moneda.children().remove();
			$.each(array_monedas ,function(entryIndex,moneda){
				if( parseInt(moneda['id']) == parseInt(id_moneda) ){
					moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
				}else{
					moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
				}
			});
			$select_moneda.append(moneda_hmtl);
			
			
			
			//carga select de condiciones con los dias de Credito default del Cliente
			$select_condiciones.children().remove();
			var hmtl_condiciones;
			$.each(array_condiciones, function(entryIndex,condicion){
				if( parseInt(condicion['id']) == parseInt(id_termino) ){
					hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes">' + condicion['descripcion'] + '</option>';
				}else{
					hmtl_condiciones += '<option value="' + condicion['id'] + '" >' + condicion['descripcion'] + '</option>';
				}
			});
			$select_condiciones.append(hmtl_condiciones);
			
			//carga select de vendedores
			$select_vendedor.children().remove();
			var hmtl_vendedor;
			$.each(array_vendedores,function(entryIndex,vendedor){
				if( parseInt(vendedor['id']) == parseInt(id_vendedor) ){
					hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes">' + vendedor['nombre_agente'] + '</option>';
				}else{
					hmtl_vendedor += '<option value="' + vendedor['id'] + '" >' + vendedor['nombre_agente'] + '</option>';
				}
			});
			$select_vendedor.append(hmtl_vendedor);
			
			//alert("id_metodo_de_pago: "+id_metodo_de_pago);
			if(parseInt(id_metodo_de_pago)==0){
				id_metodo_de_pago=6;//si el cliente no tiene asignado un metodo de pago, se le asigna por default 6=No Identificado
			}
			
			//carga select de metodos de pago
			$select_metodo_pago.children().remove();
			var hmtl_metodo;
			$.each(array_metodos_pago,function(entryIndex,metodo){
				if ( parseInt(metodo['id']) == parseInt(id_metodo_de_pago) ){
					hmtl_metodo += '<option value="' + metodo['id'] + '" selected="yes">' + metodo['titulo'] + '</option>';
				}else{
					hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
				}
			});
			$select_metodo_pago.append(hmtl_metodo);
			
			
			if(parseInt(id_metodo_de_pago)>0){
				$no_cuenta.val('');
				
				//valor_metodo 2=Tarjeta Credito, 3=Tarjeta Debito
				if(parseInt(id_metodo_de_pago)==2 || parseInt(id_metodo_de_pago)==3){
					//si esta desahabilitado, hay que habilitarlo para permitir la captura de los digitos de la tarjeta.
					if($no_cuenta.is(':disabled')) {
						$no_cuenta.removeAttr('disabled');
					}
					
					//quitar propiedad de solo lectura
					$no_cuenta.removeAttr('readonly');
					
					if($etiqueta_digit.is(':disabled')) {
						$etiqueta_digit.removeAttr('disabled');
					}
					
					$etiqueta_digit.val('Ingrese los ultimos 4 Digitos de la Tarjeta');
				}
				
				//id_metodo_de_pago 4=Cheque Nominativo, 5=Transferencia Electronica de Fondos
				if(parseInt(id_metodo_de_pago)==4 || parseInt(id_metodo_de_pago)==5){
					//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
					if($no_cuenta.is(':disabled')) {
						$no_cuenta.removeAttr('disabled');
					}
					
					//fijar propiedad de solo lectura en verdadero
					$no_cuenta.attr('readonly',true);
					
					if($etiqueta_digit.is(':disabled')) {
						$etiqueta_digit.removeAttr('disabled');
					}
					
					if(parseInt($select_moneda.val())==1){
						$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						$no_cuenta.val($('#forma-pocpedidos-window').find('input[name=cta_mn]').val());
					}else{
						$etiqueta_digit.val('Numero de Cuenta en Dolares');
						$no_cuenta.val($('#forma-pocpedidos-window').find('input[name=cta_usd]').val());
					}
				}
				
				//id_metodo_de_pago 1=Efectivo, 6=No Identificado
				if(parseInt(id_metodo_de_pago)==1 || parseInt(id_metodo_de_pago)==6){
					if(!$no_cuenta.is(':disabled')) {
						$no_cuenta.attr('disabled','-1');
					}
					if(!$etiqueta_digit.is(':disabled')) {
						$etiqueta_digit.attr('disabled','-1');
					}
				}
				
				//id_metodo_de_pago 7=NA(No Aplica)
				if(parseInt(id_metodo_de_pago)==7){
					$no_cuenta.show();
					$no_cuenta.val('NA');
					//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
					if($no_cuenta.is(':disabled')) {
						$no_cuenta.removeAttr('disabled');
					}
					if($etiqueta_digit.is(':disabled')) {
						$etiqueta_digit.removeAttr('disabled');
					}
					if(parseInt($select_moneda.val())==1){
						$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
					}else{
						$etiqueta_digit.val('Numero de Cuenta en Dolares');
					}
				}
			}
		}else{
			jAlert('El cliente '+razon_social+', tiene Cr&eacute;dito Suspendido.', 'Atencion!', function(r) { 
				$('#forma-pocpedidos-window').find('input[name=nocliente]').val('');
				$('#forma-pocpedidos-window').find('input[name=razoncliente]').val('');
				$('#forma-pocpedidos-window').find('input[name=nocliente]').focus();
			});
		}
	}
	
	
	
	//buscador de clientes
	$busca_clientes = function($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, array_monedas, array_condiciones, array_vendedores, array_metodos_pago, $no_cuenta, $etiqueta_digit, razon_social_cliente, numero_control ){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscacliente();
		var $dialogoc =  $('#forma-buscacliente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
		$('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscacliente-window').find('#tabla_resultado');
		
		//var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('a[href*=busca_cliente_modalbox]');
		//var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('a[href*=cencela]');
		var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('#busca_cliente_modalbox');
		var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('#cencela');
		
		var $cadena_buscar = $('#forma-buscacliente-window').find('input[name=cadena_buscar]');
		var $select_filtro_por = $('#forma-buscacliente-window').find('select[name=filtropor]');
		
		//funcionalidad botones
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
		
		if(numero_control != ''){
			//asignamos el numero de control al campo de busqueda
			$cadena_buscar.val(numero_control);
			if(razon_social_cliente == ''){
				html+='<option value="1" selected="yes">No. de control</option>';
			}else{
				html+='<option value="1">No. de control</option>';
			}
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if(razon_social_cliente != ''){
			//asignamos la Razon Social del Cliente al campo Nombre
			$cadena_buscar.val(razon_social_cliente);
			html+='<option value="3" selected="yes">Razon social</option>';
		}else{
			if(razon_social_cliente == '' && numero_control == ''){
				html+='<option value="3" selected="yes">Razon social</option>';
			}else{
				html+='<option value="3">Razon social</option>';
			}
		}
		html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		
		$cadena_buscar.focus();
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorClientes.json';
			$arreglo = {	'cadena':$cadena_buscar.val(),
							'filtro':$select_filtro_por.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                        }
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				
				$.each(entry['clientes'],function(entryIndex,cliente){
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
							trr += '<input type="hidden" id="direccion" value="'+cliente['direccion']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+cliente['moneda_id']+'">';
							trr += '<input type="hidden" id="moneda" value="'+cliente['moneda']+'">';
							trr += '<input type="hidden" id="vendedor_id" value="'+cliente['cxc_agen_id']+'">';
							trr += '<input type="hidden" id="terminos_id" value="'+cliente['terminos_id']+'">';
							trr += '<input type="hidden" id="emp_immex" value="'+cliente['empresa_immex']+'">';
							trr += '<input type="hidden" id="tasa_immex" value="'+cliente['tasa_ret_immex']+'">';
							trr += '<span class="no_control">'+cliente['numero_control']+'</span>';
							trr += '<input type="hidden" id="cta_mn" value="'+cliente['cta_pago_mn']+'">';
							trr += '<input type="hidden" id="cta_usd" value="'+cliente['cta_pago_usd']+'">';
							trr += '<input type="hidden" id="lista_precios" value="'+cliente['lista_precio']+'">';
							trr += '<input type="hidden" id="metodo_id" value="'+cliente['metodo_pago_id']+'">';
							trr += '<input type="hidden" id="tiene_df" value="'+cliente['tiene_dir_fiscal']+'">';//variable para indicar si tiene direccion fiscal
							trr += '<input type="hidden" id="cred_susp" value="'+cliente['credito_suspendido']+'">';//variable para indicar si el credito esta suspendido
							trr += '<input type="hidden" id="pdescto" value="'+cliente['pdescto']+'">';
							trr += '<input type="hidden" id="vdescto" value="'+cliente['vdescto']+'">';
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
					var id_cliente = $(this).find('#idclient').val();
					var no_control = $(this).find('span.no_control').html();
					var razon_social = $(this).find('span.razon').html();
					var dir_cliente = $(this).find('#direccion').val();
					var empresa_immex = $(this).find('#emp_immex').val();
					var tasa_ret_immex = $(this).find('#tasa_immex').val();
					var cuenta_mn = $(this).find('#cta_mn').val();
					var cuenta_usd = $(this).find('#cta_usd').val();
					
					var id_moneda=$(this).find('#id_moneda').val();
					var id_termino=$(this).find('#terminos_id').val();
					var id_vendedor=$(this).find('#vendedor_id').val();
					//almacena el valor de la lista
					var num_lista_precio =$(this).find('#lista_precios').val();
					var id_metodo_de_pago=$(this).find('#metodo_id').val();
					var tiene_dir_fiscal=$(this).find('#tiene_df').val();
					var cred_susp = $(this).find('#cred_susp').val();
					
					var pdescto = $(this).find('#pdescto').val();
					var vdescto = $(this).find('#vdescto').val();
					
					//llamada a la funcion para agregar los datos del cliente seleccionado
					$agregarDatosClienteSeleccionado($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, array_monedas, array_condiciones, array_vendedores, array_metodos_pago, $no_cuenta, $etiqueta_digit, id_cliente, no_control, razon_social, dir_cliente, empresa_immex, tasa_ret_immex, cuenta_mn, cuenta_usd, id_moneda, id_termino, id_vendedor, num_lista_precio, id_metodo_de_pago, tiene_dir_fiscal, cred_susp, pdescto, vdescto);
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					//asignar el enfoque al campo Razon social del cliente
					$('#forma-pocpedidos-window').find('input[name=razoncliente]').focus();
				});
				
				
				//$aplicarEventoSeleccionarTrkeypress($tabla_resultados);
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
			$('#forma-pocpedidos-window').find('input[name=razoncliente]').focus();
		});
	}//termina buscador de clientes
	
	
	
	
	
	//buscador de productos
	$busca_productos = function(sku_buscar, descripcion){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto($('#forma-pocpedidos-window').find('input[name=nombre_producto]'));
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
			var prod_tipos_html = '<option value="0">[--Seleccionar Tipo--]</option>';
			$.each(data['prodTipos'],function(entryIndex,pt){

				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		
		//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
		$campo_sku.val(sku_buscar);
		
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
					$('#forma-pocpedidos-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-pocpedidos-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
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
			$('#forma-pocpedidos-window').find('input[name=nombre_producto]').focus();
		});
		
	}//termina buscador de productos
	
	
	
	
	
	//Buscador de presentaciones disponibles para un producto
	$buscador_presentaciones_producto = function($id_cliente,nocliente, sku_producto,$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio, arrayMonedas){
		//Verifica si el campo rfc proveedor no esta vacio
		var cliente_listaprecio=  $('#forma-pocpedidos-window').find('input[name=num_lista_precio]').val();
		var vdescto = $('#forma-pocpedidos-window').find('input[name=valor_descto]').val();
		
		if(nocliente.trim() != ''){
			
			//verifica si el campo sku no esta vacio para realizar busqueda
			if(sku_producto.trim() != ''){
				
				//Asignar el enfoque para corregir el problema donde muestra varias ventanas si le dan mas de un enter al querer agregar el producto al grid
				$('#forma-pocpedidos-window').find('input[name=folio]').focus();
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesProducto.json';
				$arreglo = {'sku':sku_producto,'lista_precios':cliente_listaprecio, 'id_client':$id_cliente.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var trr = '';
				
				$.post(input_json,$arreglo,function(entry){
					
					//Verifica si el arreglo  retorno datos
					if (entry['Presentaciones'].length > 0){
						
						if(parseInt(entry['Presentaciones'].length)==1){
							//Agregar TR de manera automatica porque solo hay una presentacion
							var id_prod = entry['Presentaciones'][0]['id'];
							var sku = entry['Presentaciones'][0]['sku'];
							var titulo = entry['Presentaciones'][0]['titulo'];
							var unidadId = entry['Presentaciones'][0]['unidad_id'];
							var unidad = entry['Presentaciones'][0]['unidad'];
							var id_pres = entry['Presentaciones'][0]['id_presentacion'];
							var pres = entry['Presentaciones'][0]['presentacion'];
							var num_dec = entry['Presentaciones'][0]['decimales'];
							var prec_unitario = entry['Presentaciones'][0]['precio'];
							var id_moneda = entry['Presentaciones'][0]['id_moneda'];
							var tcMonProd = entry['Presentaciones'][0]['tc'];
							var exislp = entry['Presentaciones'][0]['exis_prod_lp'];
							var idImpto = entry['Presentaciones'][0]['id_impto'];
							var valorImpto = entry['Presentaciones'][0]['valor_impto'];
							var iepsId = entry['Presentaciones'][0]['ieps_id'];
							var iepsTasa = entry['Presentaciones'][0]['ieps_tasa'];
							var retencion_id = entry['Presentaciones'][0]['ret_id'];
							var retencion_tasa = entry['Presentaciones'][0]['ret_tasa'];
							var id_cot="0";
							var no_cot="0";
							var id_det_cot="0";
							var cantPartida=" ";
							var reg_aut = '0&&&0&&&0';
							
							if($tipo_cambio.val().trim()!=''){
								if(exislp=='1'){
									//llamada a la funcion que agrega el producto al grid
									$agrega_producto_grid($grid_productos, id_prod, sku, titulo, unidadId, unidad, id_pres,pres,prec_unitario,$select_moneda,id_moneda,$tipo_cambio,num_dec, arrayMonedas, tcMonProd, idImpto, valorImpto, iepsId, iepsTasa, vdescto, id_cot, no_cot, id_det_cot, cantPartida, reg_aut, retencion_id, retencion_tasa);
								}else{
									jAlert(exislp, 'Atencion!', function(r) { 
										$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
									});
								}
							}else{
								jAlert('Es necesario ingresar el Tipo de Cambio.', 'Atencion!');
							}
						}else{
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
											trr += '<span class="unidadId" style="display:none">'+pres['unidad_id']+'</span>';
											trr += '<span class="unidad" style="display:none">'+pres['unidad']+'</span>';
											trr += '<span class="id_pres" style="display:none">'+pres['id_presentacion']+'</span>';
											trr += '<span class="pres">'+pres['presentacion']+'</span>';
											trr += '<span class="costo" style="display:none">'+pres['precio']+'</span>';
											trr += '<span class="idmon" style="display:none">'+pres['id_moneda']+'</span>';
											trr += '<span class="tc" style="display:none">'+pres['tc']+'</span>';
											trr += '<span class="dec" style="display:none">'+pres['decimales']+'</span>';
											trr += '<span class="exislp" style="display:none">'+pres['exis_prod_lp']+'</span>';
											trr += '<span class="idImpto" style="display:none">'+pres['id_impto']+'</span>';
											trr += '<span class="valorImpto" style="display:none">'+pres['valor_impto']+'</span>';
											trr += '<span class="iepsId" style="display:none">'+pres['ieps_id']+'</span>';
											trr += '<span class="iepsTasa" style="display:none">'+pres['ieps_tasa']+'</span>';
											trr += '<span class="retId" style="display:none">'+pres['ret_id']+'</span>';
											trr += '<span class="retTasa" style="display:none">'+pres['ret_tasa']+'</span>';
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
								
								//Seleccionar un producto del grid de resultados
								$tabla_resultados.find('tr').click(function(){
									//llamada a la funcion que busca y agrega producto al grid, se le pasa como parametro el lote y el almacen
									var id_prod = $(this).find('span.id_prod').html();
									var sku = $(this).find('span.sku').html();
									var titulo = $(this).find('span.titulo').html();
									var unidadId = $(this).find('span.unidadId').html();
									var unidad = $(this).find('span.unidad').html();
									var id_pres = $(this).find('span.id_pres').html();
									var pres = $(this).find('span.pres').html();
									var num_dec = $(this).find('span.dec').html();
									var prec_unitario = $(this).find('span.costo').html();
									var id_moneda = $(this).find('span.idmon').html();
									//tipo de cambio de la moneda del producto
									var tcMonProd = $(this).find('span.tc').html();
									var exislp = $(this).find('span.exislp').html();
									
									var idImpto = $(this).find('span.idImpto').html();
									var valorImpto = $(this).find('span.valorImpto').html();
									
									var iepsId = $(this).find('span.iepsId').html();
									var iepsTasa = $(this).find('span.iepsTasa').html();
									
									var id_cot="0";
									var no_cot="0";
									var id_det_cot="0";
									var cantPartida=" ";
									var reg_aut = '0&&&0&&&0';
									
									var retencion_id = $(this).find('span.retId').html();
									var retencion_tasa = $(this).find('span.retTasa').html();
									
									if($tipo_cambio.val().trim()!=''){
										if(exislp=='1'){
											//Llamada a la funcion que agrega el producto al grid
											$agrega_producto_grid($grid_productos, id_prod, sku, titulo, unidadId, unidad, id_pres,pres,prec_unitario,$select_moneda,id_moneda,$tipo_cambio,num_dec, arrayMonedas, tcMonProd, idImpto, valorImpto, iepsId, iepsTasa, vdescto, id_cot, no_cot, id_det_cot, cantPartida, reg_aut, retencion_id, retencion_tasa);
										}else{
											jAlert(exislp, 'Atencion!', function(r) { 
												$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
											});
										}
									}else{
										jAlert('Es necesario ingresar el Tipo de Cambio.', 'Atencion!');
									}
									
									//elimina la ventana de busqueda
									var remove = function() {$(this).remove();};
									$('#forma-buscapresentacion-overlay').fadeOut(remove);
								});
								
								//$tabla_resultados.find('tr').focus();
								//$(this).aplicarEventoKeypressEjecutaTrigger($('#forma-buscaproducto-window'), $tabla_resultados.find('tr'));
								
								
								$cancelar_plugin_busca_lotes_producto.click(function(event){
									//event.preventDefault();
									var remove = function() {$(this).remove();};
									$('#forma-buscapresentacion-overlay').fadeOut(remove);
									$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
								});
						}

					}else{
						jAlert('El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.', 'Atencion!', function(r) { 
							$('#forma-pocpedidos-window').find('input[name=titulo_producto]').val('');
						});
					}
				});
				
			}else{
				jAlert('Es necesario ingresar un Sku de producto valido.', 'Atencion!', function(r) { 
					$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
				});
			}
		}else{
			jAlert('Es necesario seleccionar un Cliente.', 'Atencion!', function(r) { 
				$('#forma-pocpedidos-window').find('input[name=nocliente]').focus();
			});
		}
		
	}//termina buscador dpresentaciones disponibles de un producto
	
    
    
    
	
	//calcula totales(subtotal, impuesto, total)
	$calcula_totales = function(){
		var $campo_subtotal = $('#forma-pocpedidos-window').find('input[name=subtotal]');
		var $campo_ieps = $('#forma-pocpedidos-window').find('input[name=ieps]');
		var $campo_impuesto = $('#forma-pocpedidos-window').find('input[name=impuesto]');
		var $campo_impuesto_retenido = $('#forma-pocpedidos-window').find('input[name=impuesto_retenido]');
		var $campo_total = $('#forma-pocpedidos-window').find('input[name=total]');
		//var $campo_tc = $('#forma-pocpedidos-window').find('input[name=tc]');
		//var $valor_impuesto = $('#forma-pocpedidos-window').find('input[name=valorimpuesto]');
		var $grid_productos = $('#forma-pocpedidos-window').find('#grid_productos');
		var $grid_warning = $('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning');
		
		var $empresa_immex = $('#forma-pocpedidos-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]');
		
		var $importe_subtotal = $('#forma-pocpedidos-window').find('input[name=importe_subtotal]');
		var $monto_descuento = $('#forma-pocpedidos-window').find('input[name=monto_descuento]');
		
		var pdescto = $('#forma-pocpedidos-window').find('input[name=pdescto]').val();
		var vdescto = $('#forma-pocpedidos-window').find('input[name=valor_descto]').val();
		
		var sumaDescuento=0;
		var sumaSubtotalConDescuento=0;
		var sumaRetencionesDePartidas=0;
		
		var sumaSubTotal = 0; //es la suma de todos los importes
		//Suma de todos los importes del IEPS
		var sumaIeps = 0;
		//Suma de los importes del IVA
		var sumaImpuesto = 0;
		//Monto del iva retenido de acuerdo a la tasa de retencion immex
		var impuestoRetenido = 0;
		//Suma del subtotal + totalImpuesto + sumaIeps - impuestoRetenido
		var sumaTotal = 0;
		
		
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#cost').val().trim() != '') && ( $(this).find('#cant').val().trim() != '' )){
				//Acumula los importes sin IVA, sin IEPS en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($(this).find('#import').val()));
				
				//Acumula valores con descuento
				sumaDescuento = parseFloat(sumaDescuento) + parseFloat(quitar_comas($(this).find('#importe_del_descto').val()));
				sumaSubtotalConDescuento = parseFloat(sumaSubtotalConDescuento) + parseFloat(quitar_comas($(this).find('#importe_con_descto').val()));
				
				//Acumula los importes del IEPS
				sumaIeps =  parseFloat(sumaIeps) + parseFloat($(this).find('#importeIeps').val());
				
				//Acumula las retenciones de iva por partida
				sumaRetencionesDePartidas = parseFloat(sumaRetencionesDePartidas) + parseFloat($(this).find('#ret_importe').val());
				
				if($(this).find('#totimp').val() != ''){
					//Acumula los importes del IVA
					sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
		
		if(pdescto.trim()=='true' && parseFloat(sumaDescuento)>0){
			//Agregar importe sin descuento, sin impuesto
			$importe_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
			
			//Agregar monto del descuento
			$monto_descuento.val($(this).agregar_comas(  parseFloat(sumaDescuento).toFixed(2)  ));
			
			//Calcular  la tasa de retencion IMMEX
			impuestoRetenido = parseFloat(sumaSubtotalConDescuento) * parseFloat(parseFloat($tasa_ret_immex.val()));
			
			if(parseFloat(sumaRetencionesDePartidas)>0){
				//Sumar el  monto de las retenciones de las partidas si es que existe
				impuestoRetenido = parseFloat(impuestoRetenido) + parseFloat(sumaRetencionesDePartidas);
			}
			
			//Calcula el total sumando el sumaSubTotal + sumaIeps + sumaImpuesto - impuestoRetenido
			sumaTotal = parseFloat(sumaSubtotalConDescuento) + parseFloat(sumaIeps) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
			
			//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(parseFloat(sumaSubtotalConDescuento).toFixed(2)));
		}else{
			//calcular  la tasa de retencion IMMEX
			impuestoRetenido = parseFloat(sumaSubTotal) * parseFloat(parseFloat($tasa_ret_immex.val()));
			
			if(parseFloat(sumaRetencionesDePartidas)>0){
				//Sumar el  monto de las retenciones de las partidas si es que existe
				impuestoRetenido = parseFloat(impuestoRetenido) + parseFloat(sumaRetencionesDePartidas);
			}
			
			//Calcula el total sumando el sumaSubTotal + sumaIeps + sumaImpuesto - impuestoRetenido
			sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaIeps) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
			
			//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
			$campo_subtotal.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
		}
		
		
		//Redondea a dos digitos el IEPS y lo asigna  al campo ieps
		$campo_ieps.val($(this).agregar_comas(  parseFloat(sumaIeps).toFixed(2)  ));
		
		//redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val($(this).agregar_comas(  parseFloat(sumaImpuesto).toFixed(2)  ));
		
		//Redondea a dos digitos el impuesto y lo asigna al campo retencion
		$campo_impuesto_retenido.val($(this).agregar_comas(  parseFloat(impuestoRetenido).toFixed(2)  ));
		
		//Redondea a dos digitos la suma  total y se asigna al campo total
		$campo_total.val($(this).agregar_comas(parseFloat(sumaTotal).toFixed(2)));
		
		var valorHeight=550;
		
		if(parseFloat(sumaDescuento)>0){
			$('#forma-pocpedidos-window').find('#tr_importe_subtotal').show();
			$('#forma-pocpedidos-window').find('#tr_descto').show();
			$('#forma-pocpedidos-window').find('input[name=etiqueta_motivo_descto]').show();
			$('#forma-pocpedidos-window').find('input[name=motivo_descuento]').show();
			if(parseInt($("tr",$grid_warning).size()) > 0){
				valorHeight = parseFloat(valorHeight) + 40;
			}else{
				valorHeight = parseFloat(valorHeight) + 30;
			}
		}else{
			$('#forma-pocpedidos-window').find('#tr_importe_subtotal').hide();
			$('#forma-pocpedidos-window').find('#tr_descto').hide();
			$('#forma-pocpedidos-window').find('input[name=etiqueta_motivo_descto]').hide();
			$('#forma-pocpedidos-window').find('input[name=motivo_descuento]').hide();
		}
		
		//Ocultar campos si tienen valor menor o igual a cero
		if(parseFloat(sumaIeps)<=0){
			$('#forma-pocpedidos-window').find('#tr_ieps').hide();
		}else{
			$('#forma-pocpedidos-window').find('#tr_ieps').show();
			valorHeight = parseFloat(valorHeight) + 20;
		}
		if(parseFloat(impuestoRetenido)<=0){
			$('#forma-pocpedidos-window').find('#tr_retencion').hide();
		}else{
			$('#forma-pocpedidos-window').find('#tr_retencion').show();
			valorHeight = parseFloat(valorHeight) + 20;
		}
		
		$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'height':valorHeight+'px'});
	}//termina calcular totales
	
	
	
	
	
	$aplicar_evento_click_a_input_check = function($input_check){
		//aplicar click a los campso check del grid
		$input_check.click(function(event){
			if( this.checked ){
				$(this).parent().find('input[name=seleccionado]').val("1");
				$(this).parent().parent().find('input[name=cantidad]').attr("readonly", true);
				
			}else{
				$(this).parent().find('input[name=seleccionado]').val("0");
				$(this).parent().parent().find('input[name=cantidad]').attr("readonly", false);
			}
		});
	}
	
	
	
	
	//agregar producto al grid
	$agrega_producto_grid = function($grid_productos, id_prod, sku, titulo, unidadId, unidad,id_pres,pres,prec_unitario,$select_moneda, id_moneda, $tipo_cambio,num_dec, arrayMonedas, tcMonProd, idImpto, valorImpto, id_ieps, tasa_ieps, vdescto, id_cot, no_cot, id_det_cot, cantPartida, reg_aut, retencion_id, retencion_tasa){
		var $id_impuesto = $('#forma-pocpedidos-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-pocpedidos-window').find('input[name=valorimpuesto]');
		var $incluye_produccion = $('#forma-pocpedidos-window').find('input[name=incluye_pro]');
		var $permite_req = $('#forma-pocpedidos-window').find('input[name=permite_req]');
		var $num_lista_precio = $('#forma-pocpedidos-window').find('input[name=num_lista_precio]');
		
		var pdescto = $('#forma-pocpedidos-window').find('input[name=pdescto]').val();
		//var vdescto = $('#forma-pocpedidos-window').find('input[name=vdescto]').val();
		
		//esta es la moneda definida para el pedido
		var idMonedaPedido = $select_moneda.val();
		var precioOriginal = prec_unitario;
		var precioCambiado = 0.00;
		var importeImpuesto=0.00;
		
		//verificamos si la Lista de Precio trae moneda
		if(parseInt($num_lista_precio.val())>0){
			//verificamos si el grid no tiene registros
			if(parseInt($("tr", $grid_productos).size())<=0){
				//aqui fijamos la moneda seleccionada para el pedido, esto evita que cambien la moneda
				var moneda_prod='';
				$select_moneda.children().remove();
				$.each(arrayMonedas ,function(entryIndex,moneda){
					if( parseInt(moneda['id']) == parseInt(idMonedaPedido) ){
						moneda_prod += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
					}else{
						//moneda_prod += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
					}
				});
				$select_moneda.append(moneda_prod);
			}
			
			//Si la moneda del Pedido es diferente a la moneda del Precio del Producto
			//Entonces convertimos el precio a la moneda del pedido de acuerdo al tipo de cambio actual
			if( parseInt(idMonedaPedido) != parseInt(id_moneda) ){
				if(parseInt(idMonedaPedido)==1 && parseInt(id_moneda)!=1){
					//si la moneda del pedido es pesos y la moneda del precio es diferente de Pesos,
					//entonces calculamos su equivalente a pesos
					precioCambiado = parseFloat( parseFloat(precioOriginal) * parseFloat($tipo_cambio.val())).toFixed(4);
				}
				if(parseInt(idMonedaPedido)!=1 && parseInt(id_moneda)==1){
					//alert("precioOriginal:"+precioOriginal +"		tc_original:"+$tc_original.val());
					//si la moneda original es dolar y la moneda del precio es Pesos, calculamos su equivalente a dolar
					precioCambiado = parseFloat( parseFloat(precioOriginal) / parseFloat($tipo_cambio.val()) ).toFixed(4);
				}
			}else{
				precioCambiado = prec_unitario;
			}
		}else{
			precioCambiado = prec_unitario;
		}
		
		
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		if(vdescto==''){
			vdescto='0';
		}
		
		var encontrado = 0;
		//busca el sku y la presentacion en el grid
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#skuprod').val() == sku.toUpperCase() )  && (parseInt($(this).find('#idpres').val())== parseInt(id_pres) ) && (parseInt($(this).find('#elim').val())!=0)){
				encontrado=1;//el producto ya esta en el grid
			}
		});
		
		if(parseInt(encontrado)!=1){//si el producto no esta en el grid entra aqui
			//ocultamos el boton facturar para permitir Guardar los cambios  antes de facturar
			$('#forma-pocpedidos-window').find('#facturar').hide();
			//obtiene numero de trs
			var tr = $("tr", $grid_productos).size();
			tr++;
			
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size:11px;  border:1px solid #C1DAD7;" width="25">';
					//trr += '<a href="elimina_producto" id="delete'+ tr +'">Eliminar</a>';
					trr += '<a href="elimina_producto" id="delete'+ tr +'"><div id="eliminar'+ tr +'" class="onmouseOutEliminar" style="width:24px; background-position:center;"/></a>';
					
					trr += '<input type="hidden" 	name="eliminado" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
					trr += '<input type="hidden" 	name="iddetalle" id="idd" value="0">';//este es el id del registro que ocupa el producto en la tabla pocpedidos_detalles
					trr += '<input type="hidden" 	name="noTr" value="'+ tr +'">';
					
					trr += '<input type="hidden" name="idcot" id="idcot" value="'+ id_cot +'">';
					trr += '<input type="hidden" name="nocot" id="nocot" value="'+ no_cot +'">';
					trr += '<input type="hidden" name="iddetcot" id="iddetcot" value="'+ id_det_cot +'">';
					
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="116">';
					trr += '<input type="hidden" 	name="idproducto" id="idprod" value="'+ id_prod +'">';
					trr += '<input type="text" 		name="sku" value="'+ sku +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200">';
					//trr += '<input type="text" 		name="nombre" 	value="'+ titulo +'" id="nom" class="borde_oculto" readOnly="true" style="width:196px;">';
                                    let arrTitulo = titulo.split('||');
                                    let option = '';
                                    let arrOption;

                                    if (arrTitulo.length == 1) {

                                        arrOption = arrTitulo[0].split('|');

                                        if (arrOption.length == 1) {
                                            option += '<option value="0">' + arrOption[0] + '</option>';
                                        } else {
                                            option += '<option value="' + arrOption[0] + '">' + arrOption[1] + '</option>';
                                        }

                                    } else {

                                        for (i of arrTitulo) {
                                            arrOption = i.split('|');
                                            option += '<option value="' + arrOption[0] + '">' + arrOption[1] + '</option>';
                                        }
                                    }
                                    trr += '<select name="nombre" id="nom" class="nombre' + tr + '" style="width:196px;">' + option + '</select>';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<select name="select_umedida" class="select_umedida'+ tr +'" style="width:86px;"></select>';
					trr += '<input type="text" 		name="unidad'+ tr +'" 	value="'+ unidad +'" id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
					trr += '<input type="hidden"    name="id_presentacion"  	value="'+  id_pres +'" id="idpres">';
					trr += '<input type="hidden"    name="numero_decimales"     value="'+  num_dec +'" id="numdec">';
					trr += '<input type="text" 	name="presentacion'+ tr +'" value="'+  pres +'" id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" name="cantidad" value="'+cantPartida+'" class="cantidad'+ tr +'" id="cant" style="width:76px;">';
				trr += '</td>';
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<input type="text" name="costo" value="'+ precioCambiado +'" class="costo'+ tr +'" id="cost" style="width:86px; text-align:right;">';
					trr += '<input type="hidden" 	name="vdescto" id="vdescto" value="'+vdescto+'">';
					trr += '<input type="hidden" 	name="pu_descto" id="pu_descto" value="0">';
					trr += '<input type="hidden" 	name="id_mon_pre" id="id_moneda" value="'+id_moneda+'">';
					trr += '<input type="hidden" 	name="pre_original" id="prec_original" value="'+precioOriginal+'">';
				trr += '</td>';
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<input type="text" name="importe'+ tr +'" value="" id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
					trr += '<input type="hidden" name="importe_del_descto" id="importe_del_descto" value="0">';
					trr += '<input type="hidden" name="importe_con_descto" id="importe_con_descto" value="0">';
					trr += '<input type="hidden" name="id_imp_prod"   value="'+  idImpto +'" id="idimppord">';
					trr += '<input type="hidden" name="valor_imp"     value="'+  valorImpto +'" id="ivalorimp">';
					trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="0">';
					
					trr += '<input type="hidden" name="ret_id" 		class="ret_id'+ tr +'" id="ret_id" value="'+  retencion_id +'">';
					trr += '<input type="hidden" name="ret_tasa" 	class="ret_tasa'+ tr +'" id="ret_tasa" value="'+  retencion_tasa +'">';
					trr += '<input type="hidden" name="ret_importe" class="ret_importe'+ tr +'" id="ret_importe" value="0">';
				trr += '</td>';
				
				trr += '<td class="grid2" style="font-size:11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<input type="hidden" name="idIeps"     value="'+ id_ieps +'" id="idIeps">';
					trr += '<input type="text" name="tasaIeps" value="'+ tasa_ieps +'" class="borde_oculto" id="tasaIeps" style="width:56px; text-align:right;" readOnly="true">';
				trr += '</td>';
				
				trr += '<td class="grid2" style="font-size:11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" name="importeIeps" value="'+parseFloat(0).toFixed(4)+'" class="borde_oculto" id="importeIeps" style="width:76px; text-align:right;" readOnly="true">';
				trr += '</td>';
				
				trr += '<td class="grid2" id="td_oculto'+ tr +'" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" 		name="produccion" 	value="" 	 class="borde_oculto" readOnly="true" style="width:76px; text-align:right;">';
					trr += '<input type="hidden"    name="existencia" 	value="0">';
				trr += '</td>';
				
				var desactivado="";
				var check="";
				var valor_seleccionado="0";
				trr += '<td class="grid2" id="td_oculto'+ tr +'" style="font-size: 11px;  border:1px solid #C1DAD7;" width="20">';
					trr += '<input type="checkbox" 	name="checkProd" class="checkProd'+ tr +'" '+check+' '+desactivado+'>';
					trr += '<input type="hidden" 	name="seleccionado" value="'+valor_seleccionado+'">';//el 1 significa que el registro no ha sido eliminado
				trr += '</td>';
				
				trr += '<td class="grid2" style="font-size:11px;  border:1px solid #C1DAD7;" width="20" id="td_check_auth'+ tr +'">';
					trr += '<input type="hidden" 	name="statusreg"   	class="statusreg'+ tr +'" value="'+ reg_aut +'">';
					trr += '<input type="hidden" 	name="reqauth"   	class="reqauth'+ tr +'" value="false">';
					trr += '<input type="hidden" 	name="success"   	class="success'+ tr +'" value="false">';
					trr += '<input type="checkbox" 	name="checkauth" 	class="checkauth'+ tr +'">';
				trr += '</td>';
			trr += '</tr>';
			
			$grid_productos.append(trr);
			
			
			
			$grid_productos.find('.checkauth'+tr).hide();
			
			$grid_productos.find('.checkauth'+ tr).click(function(event){
				if(this.checked){
					$('#forma-pocpedidos-window').find('#btn_autorizar').show();
				}else{
					var cont_check=0;
					$grid_productos.find('input[name=checkauth]').each(function(index){
						if(this.checked){
							if(parseInt($(this).parent().parent().find('input[name=eliminado]').val())==1){
								cont_check++;
							}
						}
					});
					
					if(parseInt(cont_check)<=0){
						$('#forma-pocpedidos-window').find('#btn_autorizar').hide();
					}
				}
			});
			
			
			
			var unidadLitroKilo=false;
			
			if(parseInt(unidad.toUpperCase().search(/KILO/))>-1){
				unidadLitroKilo=true;
			}else{
				if(parseInt(unidad.toUpperCase().search(/LITRO/))>-1){
					unidadLitroKilo=true;
				}
			}
			
			var hmtl_um="";
			if(cambiarUM.trim()=='true'){
				if(unidadLitroKilo){
					$grid_productos.find('select.select_umedida'+tr).children().remove();
					$.each(arrayUM,function(entryIndex,um){
						if(parseInt(unidadId) == parseInt(um['id'])){
							hmtl_um += '<option value="' + um['id'] + '" selected="yes" >' + um['titulo'] + '</option>';
						}else{
							if(parseInt(um['titulo'].toUpperCase().search(/KILO/))>-1 || parseInt(um['titulo'].toUpperCase().search(/LITRO/))>-1){
								hmtl_um += '<option value="' + um['id'] + '">' + um['titulo'] + '</option>';
							}
						}
					});
					$grid_productos.find('select.select_umedida'+tr).append(hmtl_um);
				}else{
					$grid_productos.find('select.select_umedida'+tr).children().remove();
					$.each(arrayUM,function(entryIndex,um){
						if(parseInt(unidadId) == parseInt(um['id'])){
							hmtl_um += '<option value="' + um['id'] + '" selected="yes" >' + um['titulo'] + '</option>';
						}
					});
					$grid_productos.find('select.select_umedida'+tr).append(hmtl_um);
				}
				//Ocultar campo input porque se debe mostrar select para permitir cambio de unidad de medida
				$grid_productos.find('input[name=unidad'+ tr +']').hide();
			}else{
				//Carga select de pais Origen
				var elemento_seleccionado = unidadId;
				var texto_elemento_cero = '';
				var index_elem = 'id';
				var index_text_elem = 'titulo';
				$carga_campos_select($grid_productos.find('select.select_umedida'+tr), arrayUM, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
				
				//Ocultar porque no se permitirá cambiar de unidad de medida
				$grid_productos.find('select.select_umedida'+tr).hide();
			}
			
			
			if($incluye_produccion.val()=='true' || $permite_req.val()=='true'){
				//Aplicar evento click al check, cuando la empresa incluya modulo de produccion
				$aplicar_evento_click_a_input_check($grid_productos.find('.checkProd'+ tr));
				//Ocultar check porque es un registro nuevo, se debe mostrar  hasta que se genere un warning
				$grid_productos.find('.checkProd'+ tr).hide();
			}else{
				//ocualtar campos,  cuando la empresa no incluya modulo de produccion
				$grid_productos.find('#td_oculto'+tr).hide();
			}
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_productos.find('#cant').focus(function(e){
				if($(this).val() == ' '){
						$(this).val('');
				}
			});
			
			//recalcula importe al perder enfoque el campo cantidad
			$grid_productos.find('#cant').blur(function(){
				var $campoCantidad = $(this);
				var $campoPrecioU = $(this).parent().parent().find('#cost');
				var $campoImporte = $(this).parent().parent().find('#import');
				
				var $campoTasaIeps = $(this).parent().parent().find('#tasaIeps');
				var $importeIeps = $(this).parent().parent().find('#importeIeps');
				
				var $campoTasaIva = $(this).parent().parent().find('#ivalorimp');
				var $importeIva = $(this).parent().parent().find('#totimp');
				
				var $ret_tasa = $(this).parent().parent().find('#ret_tasa');
				var $ret_importe = $(this).parent().parent().find('#ret_importe');
				
				var $vdescto = $(this).parent().parent().find('#vdescto');
				var $pu_con_descto = $(this).parent().parent().find('#pu_descto');
				var $importe_del_descto = $(this).parent().parent().find('#importe_del_descto');
				var $importe_con_descto = $(this).parent().parent().find('#importe_con_descto');
				
				if ($campoCantidad.val() == ''){
					$campoCantidad.val(' ');
				}
				
				if( ($campoCantidad.val().trim() != '') && ($campoPrecioU.val().trim() != '') ){
					//Calcular y redondear el importe
					$campoImporte.val( parseFloat( parseFloat($campoCantidad.val()) * parseFloat($campoPrecioU.val()) ).toFixed(4));
					
					//Calcular y redondear el importe del IEPS
					$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
					
					//Calcular el impuesto para este producto multiplicando el importe + ieps por la tasa del iva
					$importeIva.val( (parseFloat($campoImporte.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
					
					if(parseFloat($ret_tasa.val())>0){
						//Calcular la retencion de la partida
						$ret_importe.val(parseFloat(parseFloat($campoImporte.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
					}
					
					if(pdescto.trim()=='true'){
						if(parseFloat($vdescto.val())>0){
							$pu_con_descto.val(parseFloat(parseFloat($campoPrecioU.val()) - (parseFloat($campoPrecioU.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
							$importe_del_descto.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
							$importe_con_descto.val(parseFloat(parseFloat($campoImporte.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
							
							//Calcular y redondear el importe del IEPS, tomando el importe con descuento
							$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
							
							//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
							$importeIva.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
							
							if(parseFloat($ret_tasa.val())>0){
								//Calcular la retencion del importe con descuento
								$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
							}
						}
					}
				}else{
					$campoImporte.val('');
					$importeIva.val('');
				}
				
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
				
				/*
				if(!patron.test($(this).val())){
					//alert("Si valido"+$(this).val());
				}else{
					
				}
				*/
				
				//Buscar cuantos puntos tiene  cantidad
				var coincidencias = $(this).val().match(/\./g);
				var numPuntos = coincidencias ? coincidencias.length : 0;
				if(parseInt(numPuntos)>1){
					jAlert('El valor ingresado para Cantidad es incorrecto, tiene mas de un punto('+$(this).val()+').', 'Atencion!', function(r) { 
						$campoCantidad.focus();
					});
				}else{
					//Llamada a la funcion que calcula totales
					$calcula_totales();
				}
			});
			
			
			//Al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_productos.find('#cost').focus(function(e){
				if($(this).val() == ' '){
					$(this).val('');
				}
			});
			
			
			//Recalcula importe al perder enfoque el campo costo
			$grid_productos.find('#cost').blur(function(){
				var $campoCantidad = $(this).parent().parent().find('#cant');
				var $campoPrecioU = $(this);
				var $campoImporte = $(this).parent().parent().find('#import');
				
				var $campoTasaIeps = $(this).parent().parent().find('#tasaIeps');
				var $importeIeps = $(this).parent().parent().find('#importeIeps');
				
				var $campoTasaIva = $(this).parent().parent().find('#ivalorimp');
				var $importeIva = $(this).parent().parent().find('#totimp');
				
				var $ret_tasa = $(this).parent().parent().find('#ret_tasa');
				var $ret_importe = $(this).parent().parent().find('#ret_importe');
				
				var $vdescto = $(this).parent().parent().find('#vdescto');
				var $pu_con_descto = $(this).parent().parent().find('#pu_descto');
				var $importe_del_descto = $(this).parent().parent().find('#importe_del_descto');
				var $importe_con_descto = $(this).parent().parent().find('#importe_con_descto');
				
				if ($campoPrecioU.val().trim() == ''){
					$campoPrecioU.val(' ');
				}
				
				//Quitar marca que indica que requiere autorizacion
				$(this).parent().parent().find('input[name=reqauth]').val('false');
				
				if( ($campoPrecioU.val().trim()!= '') && ($campoCantidad.val().trim() != '')){	
					//Calcular y redondear el importe
					$campoImporte.val( parseFloat(parseFloat($campoPrecioU.val()) * parseFloat( $campoCantidad.val())).toFixed(4) );
					
					//Calcular y redondear el importe del IEPS
					$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
					
					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					$importeIva.val( (parseFloat($campoImporte.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
					
					if(parseFloat($ret_tasa.val())>0){
						//Calcular la retencion de la partida
						$ret_importe.val(parseFloat(parseFloat($campoImporte.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
					}
					
					if(pdescto.trim()=='true'){
						if(parseFloat($vdescto.val())>0){
							$pu_con_descto.val(parseFloat(parseFloat($campoPrecioU.val()) - (parseFloat($campoPrecioU.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
							$importe_del_descto.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
							$importe_con_descto.val(parseFloat(parseFloat($campoImporte.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
							
							//Calcular y redondear el importe del IEPS, tomando el importe con descuento
							$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
							
							//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
							$importeIva.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
							
							if(parseFloat($ret_tasa.val())>0){
								//Calcular la retencion del importe con descuento
								$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
							}
						}
					}
				}else{
					$campoImporte.val('');
					$importeIva.val('');
				}
				
				//Buscar cuantos puntos tiene  Precio Unitario
				var coincidencias = $(this).val().match(/\./g);
				var numPuntos = coincidencias ? coincidencias.length : 0;
				if(parseInt(numPuntos)>1){
					jAlert('El valor ingresado para Precio Unitario es incorrecto, tiene mas de un punto('+$(this).val()+').', 'Atencion!', function(r) { 
						$campoPrecioU.focus();
					});
				}else{
					//Llamada a la funcion que calcula totales
					$calcula_totales();
				}
			});
			
			//Validar campo costo, solo acepte numeros y punto
			$permitir_solo_numeros( $grid_productos.find('#cost') );
			$permitir_solo_numeros( $grid_productos.find('#cant') );
			
			//elimina un producto del grid
			//$grid_productos.find().bind('click',function(event){
			$grid_productos.find('#delete'+ tr).click(function(e){
				e.preventDefault();
				if(parseInt($(this).parent().find('#elim').val()) != 0){
					//Asigna espacios en blanco a todos los input de la fila eliminada
					$(this).parent().parent().find('input').val(' ');
					
					//Asigna un 0 al input eliminado como bandera para saber que esta eliminado
					$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
					$(this).parent().parent().find('input[name=statusreg]').val('0&&&0&&&0');
					$(this).parent().parent().find('input[name=reqauth]').val('false');
					$(this).parent().parent().find('input[name=success]').val('false');
					
					//oculta la fila eliminada
					$(this).parent().parent().hide();
					
					$calcula_totales();//llamada a la funcion que calcula totales
				}
			});
			
			$grid_productos.find('#eliminar'+ tr).mouseover(function(){
				$(this).removeClass("onmouseOutEliminar").addClass("onmouseOverEliminar");
			});
			$grid_productos.find('#eliminar'+ tr).mouseout(function(){
				$(this).removeClass("onmouseOverEliminar").addClass("onmouseOutEliminar");
			});
			
			//limpiar campos
			$('#forma-pocpedidos-window').find('input[name=sku_producto]').val('');
			$('#forma-pocpedidos-window').find('input[name=nombre_producto]').val('');
			
			//asignar el enfoque al campo catidad
			$grid_productos.find('.cantidad'+ tr).focus();
		}else{
			jAlert('El producto: '+sku+' con presentacion: '+pres+' ya se encuentra en el listado, seleccione otro diferente.', 'Atencion!', function(r) { 
				$('#forma-pocpedidos-window').find('input[name=nombre_producto]').val('');
				$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
			});
		}
	}//termina agregar producto al grid
	
	
	$aplicar_readonly_input = function($input){
		$input.css({'background' : '#f0f0f0'});
		$input.attr('readonly',true);
	}
	
	$quitar_readonly_input = function($input){
		$input.css({'background' : '#ffffff'});
		$input.attr('readonly',false);
	}

	//Aplicar evento change al select tipo de viaje
	$aplicar_evento_change_select_tviaje = function($select_tviaje, $remolque1, $remolque2){
		$select_tviaje.change(function(){
			var valor = $(this).val();
			if(parseInt(valor)==1){
				$remolque1.css({'background' : '#ffffff'});
				$remolque2.css({'background' : '#f0f0f0'});
				$remolque2.val('');
				$remolque2.attr('readonly',true);
			}else{
				$remolque1.css({'background' : '#ffffff'});
				$remolque2.css({'background' : '#ffffff'});
				$remolque2.attr('readonly',false);
			}
			$remolque1.focus();
		});
	}
	
	
	
	//carga los campos select con los datos que recibe como parametro
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
	
	
	//Recalcula importes por partida
	$recalcular_importes_partidas = function($grid_productos, $pdescto, $porcentaje_descuento){
		$grid_productos.find('tr').each(function (index){
			var $campoCantidad = $(this).find('#cant');
			var $campoPrecioU = $(this).find('#cost');
			var $campoImporte = $(this).find('#import');
			var $campoTasaIeps = $(this).find('#tasaIeps');
			var $importeIeps = $(this).find('#importeIeps');
			var $campoTasaIva = $(this).find('#ivalorimp');
			var $importeIva = $(this).find('#totimp');
			var $ret_tasa = $(this).find('#ret_tasa');
			var $ret_importe = $(this).find('#ret_importe');
			var $vdescto = $(this).find('#vdescto');
			var $pu_con_descto = $(this).find('#pu_descto');
			var $importe_del_descto = $(this).find('#importe_del_descto');
			var $importe_con_descto = $(this).find('#importe_con_descto');
			
			if($pdescto.val().trim()=='true' && parseFloat($porcentaje_descuento.val())>0){
				$vdescto.val($porcentaje_descuento.val());
				$pu_con_descto.val(0);
				$importe_del_descto.val(0);
				$importe_con_descto.val(0);
			}else{
				$vdescto.val(0);
				$pu_con_descto.val(0);
				$importe_del_descto.val(0);
				$importe_con_descto.val(0);
			}
			
			if( ($campoPrecioU.val().trim() != '') && ($campoCantidad.val().trim() != '') ){
				//Calcula el importe
				$campoImporte.val(parseFloat($campoPrecioU.val()) * parseFloat($campoCantidad.val()));
				//Redondea el importe en dos decimales
				//$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
				$campoImporte.val( parseFloat($campoImporte.val()).toFixed(4));
				
				//Calcular el importe del IEPS
				$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
				
				//Calcula el impuesto para este producto multiplicando el importe por el valor del iva
				$importeIva.val((parseFloat($campoImporte.val()) + parseFloat($importeIeps.val())) * parseFloat($campoTasaIva.val()));
				
				if(parseFloat($ret_tasa.val())>0){
					//Calcular la retencion de la partida si l tasa de retencion es mayor a cero
					$ret_importe.val(parseFloat(parseFloat($campoImporte.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
				}
				
				if($pdescto.val().trim()=='true'){
					if(parseFloat($vdescto.val())>0){
						$pu_con_descto.val(parseFloat(parseFloat($campoPrecioU.val()) - (parseFloat($campoPrecioU.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
						$importe_del_descto.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
						$importe_con_descto.val(parseFloat(parseFloat($campoImporte.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
						
						//Calcular y redondear el importe del IEPS, tomando el importe con descuento
						$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
						
						//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
						$importeIva.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
						
						if(parseFloat($ret_tasa.val())>0){
							//Calcular la retencion de la partida si l tasa de retencion es mayor a cero
							$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
						}
					}
				}
			}else{
				$campoImporte.val(0);
				$importeIva.val(0);
			}
		});
		
		//Llamada a la funcion que calcula totales 
		$calcula_totales();
	}
	
	
	
	
	$forma_autorizacion= function($grid_productos, $btn_autorizar,id_to_show){
		$(this).modalPanel_authorize();
		var form_to_show = 'formaauthorize';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		$('#forma-authorize-window').css({"margin-left": -40,"margin-top": -180});
		$forma_selected.prependTo('#forma-authorize-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		var $idauth = $('#forma-authorize-window').find('input[name=idauth]');
		var $passauth = $('#forma-authorize-window').find('input[name=passauth]');
		
		var $boton_aceptar = $('#forma-authorize-window').find('#boton_aceptar_validacion');
		var $cancelar_plugin = $('#forma-authorize-window').find('#boton_cancelar_validacion');
		
		
		$boton_aceptar.click(function(event){
			if($idauth.val().trim()!="" && $passauth.val().trim()!=""){
				var cadena = 'idauth='+$idauth.val()+'|passauth='+$passauth.val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAuth.json';
				$arreglo = {'cad':cadena.toCharCode(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
				
				$.post(input_json,$arreglo,function(entry){
					if(entry['Data']['success'].trim()=='true'){
						//Eliminar formulario de autorizacion
						var remove = function() {$(this).remove();};
						$('#forma-authorize-overlay').fadeOut(remove);
						
						//Ocultar boton de autorizar
						$('#forma-pocpedidos-window').find('#btn_autorizar').hide();
						
						var cont_check=0;
						$grid_productos.find('input[name=checkauth]').each(function(index){
							$tr = $(this).parent().parent();
							
							if(this.checked){
								if(parseInt($tr.find('input[name=eliminado]').val())==1){
									$tr.find('input[name=statusreg]').val('1&&&'+quitar_comas($tr.find('input[name=costo]').val())+'&&&'+entry['Data']['ident']);
									$tr.find('input[name=reqauth]').val('true');
									$tr.find('input[name=precio]').css({'background':'#ffffff'});
									//cont_check++;
								}
							}
						});
						
						/*
						if(parseInt(cont_check)<=0){
							$('#forma-cotizacions-window').find('#btn_autorizar').hide();
						}
						*/
						
						jAlert('Precios autorizados!', 'Atencion!', function(r) {
							
						});
					}else{
						jAlert('Error al intentar autorizar. EL usuario debe tener permiso para autorizar precios.', 'Atencion!', function(r) {
							//var remove = function() {$(this).remove();};
							//$('#forma-authorize-overlay').fadeOut(remove);
							$idauth.focus();
						});
					}
					
				});//termina llamada json
			}else{
				if($idauth.val().trim()=="" && $passauth.val().trim()==""){
					jAlert('Ingresar nombre de Usuario y Password.', 'Atencion!', function(r) {
						$idauth.focus();
					});
				}else{
					if($idauth.val().trim()==""){
						jAlert('Ingresar nombre de Usuario.', 'Atencion!', function(r) {
							$idauth.focus(); 
						});
					}else{
						if($passauth.val().trim()==""){
							jAlert('Ingresar Password.', 'Atencion!', function(r) {
								$passauth.focus(); 
							});
						}
					}
				}
			}
		});
		
		
		
		$(this).aplicarEventoKeypressEjecutaTrigger($idauth, $boton_aceptar);
		$(this).aplicarEventoKeypressEjecutaTrigger($passauth, $boton_aceptar);
		
		//Ligamos el boton cancelar al evento click para eliminar la forma
		$cancelar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-authorize-overlay').fadeOut(remove);
		});
		
		$idauth.focus();
	}
	
	
	
	
	
	//nuevo pedido
	$new_pedido.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_pocpedidos();
		
		var form_to_show = 'formapocpedidos00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-pocpedidos-window').css({"margin-left": -400, 	"margin-top": -235});
		
		$forma_selected.prependTo('#forma-pocpedidos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPedido.json';
		$arreglo = {'id_pedido':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
        
		var $id_pedido = $('#forma-pocpedidos-window').find('input[name=id_pedido]');
		var $folio = $('#forma-pocpedidos-window').find('input[name=folio]');
		var $total_tr = $('#forma-pocpedidos-window').find('input[name=total_tr]');
		var $busca_cliente = $('#forma-pocpedidos-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-pocpedidos-window').find('input[name=id_cliente]');
		var $nocliente = $('#forma-pocpedidos-window').find('input[name=nocliente]');
		var $razon_cliente = $('#forma-pocpedidos-window').find('input[name=razoncliente]');
		var $id_df = $('#forma-pocpedidos-window').find('input[name=id_df]');
		var $dir_cliente = $('#forma-pocpedidos-window').find('input[name=dircliente]');
		var $empresa_immex = $('#forma-pocpedidos-window').find('input[name=empresa_immex]');
		var $tasa_ret_immex = $('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]');
		var $incluye_produccion = $('#forma-pocpedidos-window').find('input[name=incluye_pro]');
		var $permite_req = $('#forma-pocpedidos-window').find('input[name=permite_req]');
		
		var $select_moneda = $('#forma-pocpedidos-window').find('select[name=select_moneda]');
		var $tipo_cambio = $('#forma-pocpedidos-window').find('input[name=tipo_cambio]');
		var $tc_usd_sat = $('#forma-pocpedidos-window').find('input[name=tc_usd_sat]');
		var $id_impuesto = $('#forma-pocpedidos-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-pocpedidos-window').find('input[name=valorimpuesto]');
		var $check_enviar_obser = $('#forma-pocpedidos-window').find('input[name=check_enviar_obser]');
		var $observaciones = $('#forma-pocpedidos-window').find('textarea[name=observaciones]');
		
		var $select_condiciones = $('#forma-pocpedidos-window').find('select[name=select_condiciones]');
		var $select_metodo_pago = $('#forma-pocpedidos-window').find('select[name=select_metodo_pago]');
		var $no_cuenta = $('#forma-pocpedidos-window').find('input[name=no_cuenta]');
		var $etiqueta_digit = $('#forma-pocpedidos-window').find('input[name=etiqueta_digit]');
		var $cta_mn = $('#forma-pocpedidos-window').find('input[name=cta_mn]');
		var $cta_usd = $('#forma-pocpedidos-window').find('input[name=cta_usd]');
		var $check_ruta = $('#forma-pocpedidos-window').find('input[name=check_ruta]');
		
		var $select_vendedor = $('#forma-pocpedidos-window').find('select[name=vendedor]');
		var $orden_compra = $('#forma-pocpedidos-window').find('input[name=orden_compra]');
		var $transporte = $('#forma-pocpedidos-window').find('input[name=transporte]');
		var $lugar_entrega = $('#forma-pocpedidos-window').find('input[name=lugar_entrega]');
		var $fecha_compromiso = $('#forma-pocpedidos-window').find('input[name=fecha_compromiso]');
		var $select_almacen = $('#forma-pocpedidos-window').find('select[name=select_almacen]');

        var $select_sat_usos = $('#forma-pocpedidos-window').find('select[name=select_uso]');
        var $select_sat_metodos = $('#forma-pocpedidos-window').find('select[name=select_metodo]');

		var $no_cotizacion = $('#forma-pocpedidos-window').find('input[name=no_cotizacion]');
		
		var $sku_producto = $('#forma-pocpedidos-window').find('input[name=sku_producto]');
		var $nombre_producto = $('#forma-pocpedidos-window').find('input[name=nombre_producto]');
		
		//buscar producto
		var $busca_sku = $('#forma-pocpedidos-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-pocpedidos-window').find('a[href*=agregar_producto]');
		
		var $cancelar_pedido = $('#forma-pocpedidos-window').find('#cancelar_pedido');
		var $descargarpdf = $('#forma-pocpedidos-window').find('#descargarpdf');
		var $cancelado = $('#forma-pocpedidos-window').find('input[name=cancelado]');
		var $btn_autorizar = $('#forma-pocpedidos-window').find('#btn_autorizar');
		
		//grid de productos
		var $grid_productos = $('#forma-pocpedidos-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning');
		
		var $check_descto = $('#forma-pocpedidos-window').find('input[name=check_descto]');
		var $pdescto = $('#forma-pocpedidos-window').find('input[name=pdescto]');
		var $valor_descto = $('#forma-pocpedidos-window').find('input[name=valor_descto]');
		var $etiqueta_motivo_descto = $('#forma-pocpedidos-window').find('input[name=etiqueta_motivo_descto]');
		var $motivo_descuento = $('#forma-pocpedidos-window').find('input[name=motivo_descuento]');
		
		var $subtotal = $('#forma-pocpedidos-window').find('input[name=subtotal]');
		var $ieps = $('#forma-pocpedidos-window').find('input[name=ieps]');
		var $impuesto = $('#forma-pocpedidos-window').find('input[name=impuesto]');
		var $impuesto_retenido = $('#forma-pocpedidos-window').find('input[name=impuesto_retenido]');
		var $total = $('#forma-pocpedidos-window').find('input[name=total]');
		
		var $pestana_transportista = $('#forma-pocpedidos-window').find('ul.pestanas').find('a[href=#tabx-2]');
		
		var $transportista = $('#forma-pocpedidos-window').find('input[name=transportista]');
		var $check_flete = $('#forma-pocpedidos-window').find('input[name=check_flete]');
		var $nombre_documentador = $('#forma-pocpedidos-window').find('input[name=nombre_documentador]');
		var $valor_declarado = $('#forma-pocpedidos-window').find('input[name=valor_declarado]');
		var $select_tviaje = $('#forma-pocpedidos-window').find('select[name=select_tviaje]');
		var $remolque1 = $('#forma-pocpedidos-window').find('input[name=remolque1]');
		var $remolque2 = $('#forma-pocpedidos-window').find('input[name=remolque2]');
		
		var $id_vehiculo = $('#forma-pocpedidos-window').find('input[name=id_vehiculo]');
		var $no_economico = $('#forma-pocpedidos-window').find('input[name=no_economico]');
		var $marca_vehiculo = $('#forma-pocpedidos-window').find('input[name=marca_vehiculo]');
		
		var $no_operador = $('#forma-pocpedidos-window').find('input[name=no_operador]');
		var $nombre_operador = $('#forma-pocpedidos-window').find('input[name=nombre_operador]');
		
		var $agena_id = $('#forma-pocpedidos-window').find('input[name=agena_id]');
		var $agena_no = $('#forma-pocpedidos-window').find('input[name=agena_no]');
		var $agena_nombre = $('#forma-pocpedidos-window').find('input[name=agena_nombre]');
		
		var $select_pais_origen = $('#forma-pocpedidos-window').find('select[name=select_pais_origen]');
		var $select_estado_origen = $('#forma-pocpedidos-window').find('select[name=select_estado_origen]');
		var $select_municipio_origen = $('#forma-pocpedidos-window').find('select[name=select_municipio_origen]');
		
		var $select_pais_dest = $('#forma-pocpedidos-window').find('select[name=select_pais_dest]');
		var $select_estado_dest = $('#forma-pocpedidos-window').find('select[name=select_estado_dest]');
		var $select_municipio_dest = $('#forma-pocpedidos-window').find('select[name=select_municipio_dest]');
		
		var $rem_id = $('#forma-pocpedidos-window').find('input[name=rem_id]');
		var $rem_no = $('#forma-pocpedidos-window').find('input[name=rem_no]');
		var $rem_nombre = $('#forma-pocpedidos-window').find('input[name=rem_nombre]');
		var $rem_dir = $('#forma-pocpedidos-window').find('input[name=rem_dir]');
		var $rem_dir_alterna = $('#forma-pocpedidos-window').find('input[name=rem_dir_alterna]');
		
		var $dest_id = $('#forma-pocpedidos-window').find('input[name=dest_id]');
		var $dest_no = $('#forma-pocpedidos-window').find('input[name=dest_no]');
		var $dest_nombre = $('#forma-pocpedidos-window').find('input[name=dest_nombre]');
		var $dest_dir = $('#forma-pocpedidos-window').find('input[name=dest_dir]');
		var $dest_dir_alterna = $('#forma-pocpedidos-window').find('input[name=dest_dir_alterna]');
		
		var $observaciones_transportista = $('#forma-pocpedidos-window').find('textarea[name=observaciones_transportista]');
		
		var $busca_vehiculo = $('#forma-pocpedidos-window').find('a[href=busca_vehiculo]');
		var $busca_operador = $('#forma-pocpedidos-window').find('a[href=busca_operador]');
		var $busca_agena = $('#forma-pocpedidos-window').find('a[href=busca_agena]');
		var $busca_remitente = $('#forma-pocpedidos-window').find('a[href=busca_remitente]');
		var $busca_dest = $('#forma-pocpedidos-window').find('a[href=busca_dest]');
		
		
		var $cerrar_plugin = $('#forma-pocpedidos-window').find('#close');
		var $cancelar_plugin = $('#forma-pocpedidos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-pocpedidos-window').find('#submit');
		
		$pestana_transportista.parent().hide();
		
		//$campo_factura.css({'background' : '#ffffff'});
		
		//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
		//$boton_descargarpdf.hide();
		$id_pedido.val(0);//para nueva pedido el id es 0
		$empresa_immex.val('false');
		$tasa_ret_immex.val('0');
		$cancelar_pedido.hide();
		$descargarpdf.hide();
		$cancelado .hide();
		$btn_autorizar.hide();
		
		
		$etiqueta_motivo_descto.hide();
		$motivo_descuento.hide();
		$valor_descto.attr("readonly", true);
		$valor_descto.css({'background' : '#F0F0F0'});
		$valor_descto.val(parseFloat(0).toFixed(4));
		$permitir_solo_numeros($valor_descto);
		$('#forma-pocpedidos-window').find('#permite_descto').hide();
		
		$permitir_solo_numeros($no_cuenta);
		$no_cuenta.attr('disabled','-1');
		$etiqueta_digit.attr('disabled','-1');
		$folio.css({'background' : '#F0F0F0'});
		//$nocliente.css({'background' : '#F0F0F0'});
		$dir_cliente.css({'background' : '#F0F0F0'});
		//$remolque2.css({'background' : '#F0F0F0'});
		//$remolque2.attr('readonly',true);
		
		$aplicar_readonly_input($remolque2);
		$aplicar_readonly_input($rem_dir);
		$aplicar_readonly_input($dest_dir);
		
		//Ocultar etiquetas
		$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_produccion').hide();
		$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_requisicion').hide();
		$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_req_prod').hide();
		
		//quitar enter a todos los campos input
		$('#forma-pocpedidos-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		$nocliente.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Pedido se guard&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-pocpedidos-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				var valorHeight=550;
				
				if(parseFloat(quitar_comas($('#forma-pocpedidos-window').find('input[name=monto_descuento]').val()))>0){
					valorHeight = parseFloat(valorHeight) + 30;
				}
				
				if(parseFloat(quitar_comas($ieps.val()))>0){
					valorHeight = parseFloat(valorHeight) + 25;
				}
				if(parseFloat(quitar_comas($impuesto_retenido.val()))>0){
					valorHeight = parseFloat(valorHeight) + 25;
				}
				$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'height':valorHeight+'px'});
						
				$('#forma-pocpedidos-window').find('div.interrogacion').css({'display':'none'});
				
				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});
				$grid_productos.find('#pres').css({'background' : '#ffffff'});
				
				$('#forma-pocpedidos-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				$grid_productos.find('input[name=reqauth]').val('false');
				var contador_alert=0;
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-pocpedidos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
						
						var campo = tmp.split(':')[0];
						var $campo_input;
						var cantidad_existencia=0;
						var  width_td=0;
						
						if((tmp.split(':')[0].substring(0, 8) == 'cantidad') || (tmp.split(':')[0].substring(0, 5) == 'costo') || (tmp.split(':')[0].substring(0, 12) == 'presentacion')){
							
							$('#forma-pocpedidos-window').find('#div_warning_grid').css({'display':'block'});
							
							if(tmp.split(':')[0].substring(0, 12) == 'presentacion'){
								$campo_input = $grid_productos.find('input[name='+campo+']');
							}else{
								$campo_input = $grid_productos.find('.'+campo);
							}
							$campo_input.css({'background' : '#d41000'});
							
							var codigo_producto = $campo_input.parent().parent().find('input[name=sku]').val();
							var titulo_producto = $campo_input.parent().parent().find('input[name=nombre]').val();
							
							if($incluye_produccion.val()=='true' || $permite_req.val()=='true'){
								width_td = 370;
							}else{
								width_td = 255;
							}
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="90"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:88px; color:red"></td>';
									tr_warning += '<td width="160"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:160px; color:red"></td>';
									tr_warning += '<td width="'+width_td+'"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:'+(parseInt(width_td) - 5)+'px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
						
						if(campo == 'backorder'){
							$campo_input = $grid_productos.find('.'+tmp.split(':')[1]);
							cantidad_existencia = tmp.split(':')[2];
							var cant_prod = parseFloat( $campo_input.val() ) - parseFloat(cantidad_existencia);
							
							$campo_input.parent().parent().find('input[name=produccion]').val(parseFloat(cant_prod).toFixed(2));
							$campo_input.parent().parent().find('input[name=existencia]').val(parseFloat(cantidad_existencia).toFixed(2));
							
							if(parseFloat(cant_prod) > 0 ){
								$campo_input.parent().parent().find('input[name=checkProd]').show();
							}
						}
						
						if(tmp.split(':')[0].substring(0,9)=='checkauth'){
							//alert(tmp);
							$grid_productos.find('.'+tmp.split(':')[0]).show();
							$grid_productos.find('.'+tmp.split(':')[0]).parent().find('input[name=reqauth]').val('true');
							
							if(parseInt(contador_alert)<=0){
								//Confirm para guardar sin autorizacion
								jConfirm('Hay precios que necesitan autorizaci&oacute;n.<br>&iquest;Desea guardar para autorizar despu&eacute;s&#63;', 'Dialogo de Confirmacion', function(r) {
									// If they confirmed, manually trigger a form submission
									if (r) {
										//Asignar los estatus correspondientes para permitir guardar sin autorizacion
										$grid_productos.find('input[name=checkauth]').each(function(index){
											$tr = $(this).parent().parent();
											if(parseInt($tr.find('input[name=eliminado]').val())==1){
												$tr.find('input[name=success]').val('true');
											}
										});
										
										//Ejecutar el submit de actualizar
										$submit_actualizar.trigger('click');
									}
								});
							}
							
							contador_alert++;
						}
						
					}
				}
				
				$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
				$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			
			//Almacenar el arreglo de unidades de medida en la variable
			arrayUM = entry['UM'];
			
			//Almacenar valor para variable que indica si se debe permitir el cambio de la unidad de medida
			cambiarUM = entry['Extras'][0]['cambioUM'];
			
			if(entry['Extras'][0]['per_descto']=='true'){
				$('#forma-pocpedidos-window').find('#permite_descto').show();
			}
			
			$incluye_produccion.val(entry['Extras'][0]['mod_produccion']);
			$permite_req.val(entry['Extras'][0]['per_req']);
			$transportista.val(entry['Extras'][0]['transportista']);
			
			if(entry['Extras'][0]['mod_produccion']=='true' || entry['Extras'][0]['per_req']=='true'){
				
				if(entry['Extras'][0]['mod_produccion']=='true' && entry['Extras'][0]['per_req']=='false'){
					//Mostrar etiqueta para cuando incluye solo produccion
					$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_produccion').show();
				}
				
				if(entry['Extras'][0]['mod_produccion']=='false' && entry['Extras'][0]['per_req']=='true'){
					//Mostrar etiqueta para cuando incluye solo requisicion
					$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_requisicion').show();
				}
				
				if(entry['Extras'][0]['mod_produccion']=='true' && entry['Extras'][0]['per_req']=='true'){
					//Mostrar etiqueta para cuando incluye requisicion y produccion
					$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_req_prod').show();
				}
				
				$('#forma-pocpedidos-window').css({"margin-left": -450, 	"margin-top": -235});
				$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'width':'1180px'});
				$('#forma-pocpedidos-window').find('.pocpedidos_div_two').css({'width':'1180px'});
				$('#forma-pocpedidos-window').find('#titulo_plugin').css({'width':'1140px'});
				$('#forma-pocpedidos-window').find('.header_grid').css({'width':'1155px'});
				$('#forma-pocpedidos-window').find('.contenedor_grid').css({'width':'1145px'});
				$('#forma-pocpedidos-window').find('#div_botones').css({'width':'1153px'});
				$('#forma-pocpedidos-window').find('#div_botones').find('.tabla_botones').find('.td_left').css({'width':'1053px'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').css({'width':'810px'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').find('.td_head').css({'width':'470px'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').find('.div_cont_grid_warning').css({'width':'800px'});
				$('#forma-pocpedidos-window').find('#div_warning_grid').find('.div_cont_grid_warning').find('#grid_warning').css({'width':'780px'});
			}else{
				//ocultar td porque la empresa no incluye Produccion
				$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').hide();
			}
			
			//$campo_tc.val(entry['tc']['tipo_cambio']);
			$id_impuesto.val(entry['iva'][0]['id_impuesto']);
			$valor_impuesto.val(entry['iva'][0]['valor_impuesto']);
			$tipo_cambio.val(entry['Tc'][0]['tipo_cambio']);
			$tc_usd_sat.val(entry['Tc'][0]['tipo_cambio']);
			
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);

			//carga select de vendedores
			$select_vendedor.children().remove();
			var hmtl_vendedor;
			$.each(entry['Vendedores'],function(entryIndex,vendedor){
				hmtl_vendedor += '<option value="' + vendedor['id'] + '"  >' + vendedor['nombre_agente'] + '</option>';
			});
			$select_vendedor.append(hmtl_vendedor);

            $select_sat_usos.children().remove();
            fullfill_select( $select_sat_usos, entry['Usos'], 0, "numero_control" );

            $select_sat_metodos.children().remove();
            fullfill_select( $select_sat_metodos, entry['Metodos'], 0, "clave" );

			//carga select de terminos
			$select_condiciones.children().remove();
			var hmtl_condiciones;
			$.each(entry['Condiciones'],function(entryIndex,condicion){
				hmtl_condiciones += '<option value="' + condicion['id'] + '"  >' + condicion['descripcion'] + '</option>';
			});
			$select_condiciones.append(hmtl_condiciones);
			
			
			//carga select de metodos de pago
			$select_metodo_pago.children().remove();
			var hmtl_metodo;
			$.each(entry['MetodosPago'],function(entryIndex,metodo){
				hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
			});
			$select_metodo_pago.append(hmtl_metodo);
			
			//carga select de almacenes
			$select_almacen.children().remove();
			var hmtl_alm;
			$.each(entry['Almacenes'],function(entryIndex,alm){
				hmtl_alm += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
			});
			$select_almacen.append(hmtl_alm);
			
			
			
			if(entry['Extras'][0]['transportista']=='true'){
				$check_flete.attr('checked',  (entry['Extras'][0]['transportista']=='true')? true:false );
				$pestana_transportista.parent().show();
				$nombre_documentador.val(entry['Extras'][0]['nombre_empleado'].trim());
				
				//LLamada a la funcion para aplicar el evento change al select tipo de viaje
				$aplicar_evento_change_select_tviaje($select_tviaje, $remolque1, $remolque2);
				
				//Alimentar select pais origen
				$select_pais_origen.children().remove();
				var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar Pais-]</option>';
				$.each(entry['Paises'],function(entryIndex,pais){
					pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
				});
				$select_pais_origen.append(pais_hmtl);
				
				//Alimentar select estado origen
				var entidad_hmtl = '<option value="0" selected="yes" >[-Seleccionar Estado--]</option>';
				$select_estado_origen.children().remove();
				$select_estado_origen.append(entidad_hmtl);
				
				//Alimentar select municipio origen
				var localidad_hmtl = '<option value="0" selected="yes" >[-Seleccionar Municipio-]</option>';
				$select_municipio_origen.children().remove();
				$select_municipio_origen.append(localidad_hmtl);
				
				//Alimentar select pais destino
				$select_pais_dest.children().remove();
				$select_pais_dest.append(pais_hmtl);
				
				//Alimentar select estado destino
				$select_estado_dest.children().remove();
				$select_estado_dest.append(entidad_hmtl);
				
				//Alimentar select municipio destino
				$select_municipio_dest.children().remove();
				$select_municipio_dest.append(localidad_hmtl);
				
				//Carga select estados al cambiar el pais Origen
				$select_pais_origen.change(function(){
					var valor_pais = $(this).val();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
					$arreglo = {'id_pais':valor_pais};
					$.post(input_json,$arreglo,function(entry){
						$select_estado_origen.children().remove();
						var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
						$.each(entry['Estados'],function(entryIndex,entidad){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						});
						$select_estado_origen.append(entidad_hmtl);
						
						var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
						$select_municipio_origen.children().remove();
						$select_municipio_origen.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
				
				//Carga select municipios al cambiar el estado origen
				$select_estado_origen.change(function(){
					var valor_entidad = $(this).val();
					var valor_pais = $select_pais_origen.val();
					
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
					$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
					$.post(input_json,$arreglo,function(entry){
						$select_municipio_origen.children().remove();
						var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
						$.each(entry['Municipios'],function(entryIndex,mun){
							trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						});
						$select_municipio_origen.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
				
				
				
				//Carga select estados al cambiar el pais destino
				$select_pais_dest.change(function(){
					var valor_pais = $(this).val();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
					$arreglo = {'id_pais':valor_pais};
					$.post(input_json,$arreglo,function(entry){
						$select_estado_dest.children().remove();
						var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
						$.each(entry['Estados'],function(entryIndex,entidad){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						});
						$select_estado_dest.append(entidad_hmtl);
						
						var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
						$select_municipio_dest.children().remove();
						$select_municipio_dest.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
				
				//Carga select municipios al cambiar el estado destino
				$select_estado_dest.change(function(){
					var valor_entidad = $(this).val();
					var valor_pais = $select_pais_dest.val();
					
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
					$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
					$.post(input_json,$arreglo,function(entry){
						$select_municipio_dest.children().remove();
						var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
						$.each(entry['Municipios'],function(entryIndex,mun){
							trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						});
						$select_municipio_dest.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
				
				
				
				
				//Buscador de Unidades(Vehiculo)
				$busca_vehiculo.click(function(event){
					event.preventDefault();
					$busca_unidades($id_vehiculo, $no_economico, $marca_vehiculo, $busca_vehiculo);
				});
				
				$(this).aplicarEventoKeypressEjecutaTrigger($marca_vehiculo, $busca_vehiculo);
				
				$no_economico.keypress(function(e){
					var valor=$(this).val();
					if(e.which == 13){
						if($no_economico.val().trim()!=''){
							var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataUnidadByNoEco.json';
							$arreglo2 = {'no_economico':$no_economico.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
							$.post(input_json2,$arreglo2,function(entry2){
								if(parseInt(entry2['Vehiculo'].length) > 0 ){
									$id_vehiculo.val(entry2['Vehiculo'][0]['id']);
									$no_economico.val(entry2['Vehiculo'][0]['numero_economico']);
									$marca_vehiculo.val(entry2['Vehiculo'][0]['marca']);
									$busca_vehiculo.hide();
									//Aplicar solo lectura una vez que se ha escogido la unidad
									$aplicar_readonly_input($marca_vehiculo);
									$no_economico.focus(); 
								}else{
									jAlert('N&uacute;mero econ&oacute;mico desconocido.', 'Atencion!', function(r) {
										$no_economico.val('');
										$no_economico.focus(); 
									});
								}
							},"json");//termina llamada json
						}
						return false;
					}else{
						if (parseInt(e.which) == 8) {
							//Si se oprime la tecla borrar se vacía el campo no_economico 
							if(parseInt(valor.length)>0 && parseInt($id_vehiculo.val())>0){
								jConfirm('Seguro que desea cambiar la Unidad seleccionada?', 'Dialogo de Confirmacion', function(r) {
									// If they confirmed, manually trigger a form submission
									if (r) {
										$id_vehiculo.val(0);
										$no_economico.val('');
										$marca_vehiculo.val('');
										$busca_vehiculo.show();
										//Quitar solo lectura una vez que se ha borrado la unidad
										$quitar_readonly_input($marca_vehiculo);
										$no_economico.focus();
									}else{
										$no_economico.val(valor);
										$no_economico.focus();
									}
								});
							}else{
								$no_economico.focus();
							}
						}
					}
				});
				
				
				
				//Buscador de Operadores
				$busca_operador.click(function(event){
					event.preventDefault();
					$busca_operadores($no_operador, $nombre_operador);
				});
				
				$(this).aplicarEventoKeypressEjecutaTrigger($nombre_operador, $busca_operador);
				
				$no_operador.keypress(function(e){
					var valor=$(this).val();
					if(e.which == 13){
						if($no_operador.val().trim()!=''){
							var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataOperadorByNo.json';
							$arreglo2 = {'no_operador':$no_operador.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
							$.post(input_json2,$arreglo2,function(entry2){
								if(parseInt(entry2['Operador'].length) > 0 ){
									$no_operador.val(entry2['Operador'][0]['clave']);
									$nombre_operador.val(entry2['Operador'][0]['nombre']);
								}else{
									jAlert('N&uacute;mero de Operador desconocido.', 'Atencion!', function(r) {
										$no_operador.val('');
										$no_operador.focus(); 
									});
								}
							},"json");//termina llamada json
						}
						return false;
					}
				});
				
				
				//Buscador de Agentes Aduanales
				$busca_agena.click(function(event){
					event.preventDefault();
					$busca_agentes_aduanales($agena_id, $agena_no, $agena_nombre, $busca_agena);
				});
				
				$(this).aplicarEventoKeypressEjecutaTrigger($agena_nombre, $busca_agena);
				
				$agena_no.keypress(function(e){
					var valor=$(this).val();
					if(e.which == 13){
						if($agena_no.val().trim()!=''){
							var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoAgen.json';
							$arreglo2 = {'no_control':$agena_no.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
							
							$.post(input_json2,$arreglo2,function(entry2){
								if(parseInt(entry2['AgenA'].length) > 0 ){
									$agena_id.val(entry2['AgenA'][0]['id']);
									$agena_no.val(entry2['AgenA'][0]['folio']);
									$agena_nombre.val(entry2['AgenA'][0]['razon_social']);
									$busca_agena.hide();
									
									//Aplicar solo lectura una vez que se ha escogido un agente aduanal
									$aplicar_readonly_input($agena_nombre);
								}else{
									jAlert('N&uacute;mero de Agente Aduanal desconocido.', 'Atencion!', function(r) { 
										$agena_no.focus(); 
									});
								}
							},"json");//termina llamada json
						}
						return false;
					}else{
						if (parseInt(e.which) == 8) {
							//Si se oprime la tecla borrar se vacía el campo agena_no 
							if(parseInt(valor.length)>0 && parseInt($agena_id.val())>0){
								jConfirm('Seguro que desea cambiar el Agente Aduanal seleccionado?', 'Dialogo de Confirmacion', function(r) {
									// If they confirmed, manually trigger a form submission
									if (r) {
										$agena_id.val(0);
										$agena_no.val('');
										$agena_nombre.val('');
										$busca_agena.show();
										
										//Quitar solo lectura una vez que se ha eliminado datos del Agente Aduanal
										$quitar_readonly_input($agena_nombre);
										
										$agena_no.focus();
									}else{
										$agena_no.val(valor);
										$agena_no.focus();
									}
								});
							}else{
								$agena_no.focus();
							}
						}
					}
				});
				
				
				
				//Buscador de Remitentes
				$busca_remitente.click(function(event){
					event.preventDefault();
					$busca_remitentes($rem_id, $rem_nombre, $rem_no, $rem_dir, $id_cliente, $busca_remitente);
				});
				
				$(this).aplicarEventoKeypressEjecutaTrigger($rem_nombre, $busca_remitente);
					
				$rem_no.keypress(function(e){
					var valor=$(this).val();
					if(e.which == 13){
						if($rem_no.val().trim()!=''){
							var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoRemitente.json';
							$arreglo2 = {'no_control':$rem_no.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
							$.post(input_json2,$arreglo2,function(entry2){
								if(parseInt(entry2['Remitente'].length) > 0 ){
									var rem_id = entry2['Remitente'][0]['id'];
									var rem_numero = entry2['Remitente'][0]['folio'];
									var rem_nombre = entry2['Remitente'][0]['razon_social'];
									var rem_dir = entry2['Remitente'][0]['dir'];
									$agregar_datos_remitente($rem_id, $rem_nombre, $rem_no, $rem_dir, $busca_remitente, rem_id, rem_nombre, rem_numero, rem_dir);
								}else{
									jAlert('N&uacute;mero de Remitente desconocido.', 'Atencion!', function(r) { 
										$rem_no.focus(); 
									});
								}
							},"json");//termina llamada json
						}
						return false;
					}else{
						if (parseInt(e.which) == 8) {
							//Si se oprime la tecla borrar se vacía el campo agena_no 
							if(parseInt(valor.length)>0 && parseInt($rem_id.val())>0){
								jConfirm('Seguro que desea cambiar el Remitente seleccionado?', 'Dialogo de Confirmacion', function(r) {
									// If they confirmed, manually trigger a form submission
									if (r) {
										$rem_id.val(0);
										$rem_no.val('');
										$rem_nombre.val('');
										$rem_dir.val('');
										
										//Quitar solo lectura una vez que se ha eliminado datos del Remitente
										$quitar_readonly_input($rem_nombre);
										
										//Mostrar link busca remitente
										$busca_remitente.show();
										
										$rem_no.focus();
									}else{
										$rem_no.val(valor);
										$rem_no.focus();
									}
								});
							}else{
								$rem_no.focus();
							}
						}
					}
				});
				
				
				
				
				//Buscador de Destinatarios
				$busca_dest.click(function(event){
					event.preventDefault();
					$busca_destinatarios($dest_id, $dest_nombre, $dest_no, $dest_dir, $id_cliente, $busca_dest);
				});
				
				$(this).aplicarEventoKeypressEjecutaTrigger($dest_nombre, $busca_dest);
				
				$dest_no.keypress(function(e){
					var valor=$(this).val();
					if(e.which == 13){
						if($dest_no.val().trim()!=''){
							var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoDestinatario.json';
							$arreglo2 = {'no_control':$dest_no.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
							$.post(input_json2,$arreglo2,function(entry2){
								if(parseInt(entry2['Dest'].length) > 0 ){
									var dest_id = entry2['Dest'][0]['id'];
									var dest_numero = entry2['Dest'][0]['folio'];
									var dest_nombre = entry2['Dest'][0]['razon_social'];
									var dest_dir = entry2['Dest'][0]['dir'];
															
									$agregar_datos_remitente($dest_id, $dest_nombre, $dest_no, $dest_dir, $busca_dest, dest_id, dest_nombre, dest_numero, dest_dir);
								}else{
									jAlert('N&uacute;mero de Destinatario desconocido.', 'Atencion!', function(r) { 
										$dest_no.focus(); 
									});
								}
							},"json");//termina llamada json
						}
						return false;
					}else{
						if (parseInt(e.which) == 8) {
							//Si se oprime la tecla borrar se vacía el campo agena_no 
							if(parseInt(valor.length)>0 && parseInt($dest_id.val())>0){
								jConfirm('Seguro que desea cambiar el Destinatario seleccionado?', 'Dialogo de Confirmacion', function(r) {
									// If they confirmed, manually trigger a form submission
									if (r) {
										$dest_id.val(0);
										$dest_no.val('');
										$dest_nombre.val('');
										$dest_dir.val('');
										
										//Quitar solo lectura una vez que se ha eliminado datos del Remitente
										$quitar_readonly_input($dest_nombre);
										
										//Mostrar link busca remitente
										$busca_dest.show();
										
										$dest_no.focus();
									}else{
										$dest_no.val(valor);
										$dest_no.focus();
									}
								});
							}else{
								$dest_no.focus();
							}
						}
					}
				});
			}//Termina datos para transportista
			
			
			
			//Buscador de clientes
			$busca_cliente.click(function(event){
				event.preventDefault();
				$busca_clientes($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, entry['Monedas'], entry['Condiciones'],entry['Vendedores'], entry['MetodosPago'], $no_cuenta, $etiqueta_digit, $razon_cliente.val(), $nocliente.val());
			});
			
			
			$nocliente.keypress(function(e){
				if(e.which == 13){
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoClient.json';
					$arreglo2 = {'no_control':$nocliente.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					
					$.post(input_json2,$arreglo2,function(entry2){
						
						if(parseInt(entry2['Cliente'].length) > 0 ){
							var id_cliente = entry2['Cliente'][0]['id'];
							var no_control = entry2['Cliente'][0]['numero_control'];
							var razon_social = entry2['Cliente'][0]['razon_social'];
							var dir_cliente = entry2['Cliente'][0]['direccion'];
							var empresa_immex = entry2['Cliente'][0]['empresa_immex'];
							var tasa_ret_immex = entry2['Cliente'][0]['tasa_ret_immex'];
							var cuenta_mn = entry2['Cliente'][0]['cta_pago_mn'];
							var cuenta_usd = entry2['Cliente'][0]['cta_pago_usd'];
							
							var id_moneda = entry2['Cliente'][0]['moneda_id'];
							var id_termino = entry2['Cliente'][0]['terminos_id'];
							var id_vendedor = entry2['Cliente'][0]['cxc_agen_id'];
							//almacena el valor de la lista
							var num_lista_precio = entry2['Cliente'][0]['lista_precio'];
							var id_metodo_de_pago = entry2['Cliente'][0]['metodo_pago_id'];
							var tiene_dir_fiscal = entry2['Cliente'][0]['tiene_dir_fiscal'];
							var cred_susp = entry2['Cliente'][0]['credito_suspendido'];
							var pdescto = entry2['Cliente'][0]['pdescto'];
							var vdescto = entry2['Cliente'][0]['vdescto'];
							
							$agregarDatosClienteSeleccionado($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, entry['Monedas'], entry['Condiciones'],entry['Vendedores'], entry['MetodosPago'], $no_cuenta, $etiqueta_digit, id_cliente, no_control, razon_social, dir_cliente, empresa_immex, tasa_ret_immex, cuenta_mn, cuenta_usd, id_moneda, id_termino, id_vendedor, num_lista_precio, id_metodo_de_pago, tiene_dir_fiscal, cred_susp, pdescto, vdescto);
							
						}else{
							$('#forma-pocpedidos-window').find('input[name=id_cliente]').val('');
							$('#forma-pocpedidos-window').find('input[name=nocliente]').val('');
							$('#forma-pocpedidos-window').find('input[name=razoncliente]').val('');
							$('#forma-pocpedidos-window').find('input[name=dircliente]').val('');
							$('#forma-pocpedidos-window').find('input[name=empresa_immex]').val('');
							$('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]').val('');
							$('#forma-pocpedidos-window').find('input[name=cta_mn]').val('');
							$('#forma-pocpedidos-window').find('input[name=cta_usd]').val('');
							$('#forma-pocpedidos-window').find('input[name=num_lista_precio]').val(0);
							//por default asignamos cero para el campo id de Direccion Fiscal, esto significa que la direccion se tomara de la tabla de clientes
							$('#forma-pocpedidos-window').find('input[name=id_df]').val(0);
							
							$('#forma-pocpedidos-window').find('input[name=pdescto]').val('false');
							$('#forma-pocpedidos-window').find('input[name=valor_descto]').val(0);
							
							jAlert('N&uacute;mero de cliente desconocido.', 'Atencion!', function(r) { 
								$('#forma-pocpedidos-window').find('input[name=nocliente]').focus(); 
							});
						}
					},"json");//termina llamada json
					
					return false;
				}
			});
			
			
			
			//Obtiene datos de la cotizacion
			$no_cotizacion.keypress(function(e){
				if(e.which == 13){
					
					var encontrado = 0;
					//busca el sku y la presentacion en el grid
					$grid_productos.find('tr').each(function (index){
						if($(this).find('#nocot').val() == $no_cotizacion.val()){
							encontrado=1;//el producto ya esta en el grid
						}
					});
					
					if(parseInt(encontrado)<=0){
						var input_json3 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosCotizacion.json';
						$arreglo3 = {'no_cot':$no_cotizacion.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
						
						$.post(input_json3,$arreglo3,function(entry3){
							
							if(entry3['VERIF']['msj'].trim()!=""){
								jAlert(entry3['VERIF']['msj'], 'Atencion!', function(r) { 
									$no_cotizacion.focus();
								});
							}
							
							if(parseInt(entry3['COTDATOS'].length) > 0 ){
								$tipo_cambio.val(entry3['COTDATOS'][0]['tc_usd']);
								var id_cliente = entry3['COTDATOS'][0]['cliente_id'];
								var no_control = entry3['COTDATOS'][0]['numero_control'];
								var razon_social = entry3['COTDATOS'][0]['razon_social'];
								var dir_cliente = entry3['COTDATOS'][0]['direccion'];
								var empresa_immex = entry3['COTDATOS'][0]['empresa_immex'];
								var tasa_ret_immex = entry3['COTDATOS'][0]['tasa_retencion_immex'];
								var cuenta_mn = entry3['COTDATOS'][0]['cta_pago_mn'];
								var cuenta_usd = entry3['COTDATOS'][0]['cta_pago_usd'];
								var id_moneda = entry3['COTDATOS'][0]['moneda_id'];
								var id_termino = entry3['COTDATOS'][0]['dias_credito_id'];
								var id_vendedor = entry3['COTDATOS'][0]['cxc_agen_id'];
								var num_lista_precio = entry3['COTDATOS'][0]['lista_precio'];
								var id_metodo_de_pago = entry3['COTDATOS'][0]['metodo_pago_id'];
								var tiene_dir_fiscal = entry3['COTDATOS'][0]['tiene_df'];
								var cred_susp = entry3['COTDATOS'][0]['credito_suspendido'];
								var pdescto = entry3['COTDATOS'][0]['pdescto'];
								var vdescto = entry3['COTDATOS'][0]['porcentaje_descto'];
								
								$agregarDatosClienteSeleccionado($select_moneda,$select_condiciones,$select_vendedor, $select_metodo_pago, entry['Monedas'], entry['Condiciones'],entry['Vendedores'], entry['MetodosPago'], $no_cuenta, $etiqueta_digit, id_cliente, no_control, razon_social, dir_cliente, empresa_immex, tasa_ret_immex, cuenta_mn, cuenta_usd, id_moneda, id_termino, id_vendedor, num_lista_precio, id_metodo_de_pago, tiene_dir_fiscal, cred_susp, pdescto, vdescto);
								
								//Cargar lista de productos de la cotizacion para el nuevo pedido
								if(parseInt(entry3['COTGRID'].length) > 0 ){
									$.each(entry3['COTGRID'],function(entryIndex,prod){
										var id_prod = prod['id'];
										var sku = prod['sku'];
										var titulo = prod['titulo'];
										var unidadId = prod['unidad_id'];
										var unidad = prod['unidad'];
										var id_pres = prod['id_presentacion'];
										var pres = prod['presentacion'];
										var num_dec = prod['decimales'];
										var prec_unitario = prod['precio'];
										var id_moneda = entry3['COTDATOS'][0]['moneda_id'];
										var tcMonProd = entry3['COTDATOS'][0]['tipo_cambio'];
										var idImpto = prod['iva_id'];
										var valorImpto = prod['valor_impto_prod'];
										var iepsId = prod['ieps_id'];
										var iepsTasa = prod['ieps_tasa'];
										var id_cot = entry3['COTDATOS'][0]['id_cot']
										var no_cot = $no_cotizacion.val();
										var id_det_cot = prod['id_det'];
										var cantPartida = prod['cantidad'];
										var reg_aut = prod['status_aut'];
										
										var retencion_id=0; 
										var retencion_tasa=0;
										
										//llamada a la funcion que agrega el producto al grid
										$agrega_producto_grid($grid_productos, id_prod, sku, titulo, unidadId, unidad, id_pres,pres,prec_unitario,$select_moneda,id_moneda,$tipo_cambio,num_dec, entry['Monedas'], tcMonProd, idImpto, valorImpto, iepsId, iepsTasa, vdescto, id_cot, no_cot, id_det_cot, cantPartida, reg_aut, retencion_id, retencion_tasa);
										
									});
								}
							}
						},"json");//termina llamada json
						
					}else{
						jAlert('No se puede agregar mas de una cotizaci&oacute;n en el pedido.', 'Atencion!', function(r) { 
							$no_cotizacion.focus();
						});
					}
				}
			});
				
			
			
			
			//agregar producto al grid
			$agregar_producto.click(function(event){
				event.preventDefault();
				$buscador_presentaciones_producto($id_cliente,$nocliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio, entry['Monedas']);
			});
			
		},"json");//termina llamada json
		
		
		//asignar evento keypress al campo Razon Social del cliente
		$(this).aplicarEventoKeypressEjecutaTrigger($razon_cliente, $busca_cliente);
		
		
		//$fecha_compromiso.val(mostrarFecha());
		$fecha_compromiso.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_compromiso.DatePicker({
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
		
		
                
		$check_descto.click(function(event){
			if(this.checked){
				$pdescto.val('true');
				$valor_descto.attr("readonly", false);
				$valor_descto.css({'background' : '#ffffff'});
				$valor_descto.val(parseFloat(0).toFixed(4));
				$recalcular_importes_partidas($grid_productos, $pdescto, $valor_descto);
			}else{
				$pdescto.val('false');
				$valor_descto.attr("readonly", true);
				$valor_descto.val(parseFloat(0).toFixed(4));
				$valor_descto.css({'background' : '#F0F0F0'});
				$recalcular_importes_partidas($grid_productos, $pdescto, $valor_descto);
			}
		});
		
		
		
		$valor_descto.focus(function(e){
			if($(this).val().trim()==''){
				$(this).val('');
			}else{
				if(parseFloat($(this).val())<=0){
					$(this).val('');
				}
			}
		});
		
		$valor_descto.blur(function(){
			var $campo_descto = $(this);
			
			if($campo_descto.val().trim()==''){
				$campo_descto.val(0);
			}
			
			$campo_descto.val(parseFloat($campo_descto.val()).toFixed(4));
			
			if(parseFloat($campo_descto.val())<=0){
				if($check_descto.prop("checked")){
					jAlert('Es necesario ingresar el Porcentaje del Descuento', 'Atencion!', function(r) { 
						$recalcular_importes_partidas($grid_productos, $pdescto, $campo_descto);
						$campo_descto.focus();
					});
				}
			}else{
				$recalcular_importes_partidas($grid_productos, $pdescto, $campo_descto);
			}
		});
		
		
		
		
		//cambiar metodo de pago
		$select_metodo_pago.change(function(){
			var valor_metodo = $(this).val();
			$no_cuenta.val('');
			
			//valor_metodo 2=Tarjeta Credito, 3=Tarjeta Debito
			if(parseInt(valor_metodo)==2 || parseInt(valor_metodo)==3){
				$no_cuenta.val('');
				//si esta desahabilitado, hay que habilitarlo para permitir la captura de los digitos de la tarjeta.
				if($no_cuenta.is(':disabled')) {
					$no_cuenta.removeAttr('disabled');
				}
				
				//quitar propiedad de solo lectura
				$no_cuenta.removeAttr('readonly');
				
				//$no_cuenta.attr('readonly',true);
				
				if($etiqueta_digit.is(':disabled')) {
					$etiqueta_digit.removeAttr('disabled');
				}
				
				$etiqueta_digit.val('Ingrese los ultimos 4 Digitos de la Tarjeta');
			}
			
			//valor_metodo 4=Cheque Nominativo, 5=Transferencia Electronica de Fondos
			if(parseInt(valor_metodo)==4 || parseInt(valor_metodo)==5){
				$no_cuenta.val('');
				$no_cuenta.show();
				//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
				if($no_cuenta.is(':disabled')) {
					$no_cuenta.removeAttr('disabled');
				}
				
				//fijar propiedad de solo lectura en verdadero
				$no_cuenta.attr('readonly',true);
				
				if($etiqueta_digit.is(':disabled')) {
					$etiqueta_digit.removeAttr('disabled');
				}
				
				if(parseInt($select_moneda.val())==1){
					$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
					$no_cuenta.val($cta_mn.val());
				}else{
					$etiqueta_digit.val('Numero de Cuenta en Dolares');
					$no_cuenta.val($cta_usd.val());
				}
			}
			
			//valor_metodo 1=Efectivo, 6=No Identificado
			if(parseInt(valor_metodo)==1 || parseInt(valor_metodo)==6){
				$no_cuenta.val('');
				if(!$no_cuenta.is(':disabled')) {
					$no_cuenta.attr('disabled','-1');
				}
				if(!$etiqueta_digit.is(':disabled')) {
					$etiqueta_digit.attr('disabled','-1');
				}
			}
			
			if(parseInt(valor_metodo)==7){
				$no_cuenta.show();
				$no_cuenta.val('NA');
				//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
				if($no_cuenta.is(':disabled')) {
					$no_cuenta.removeAttr('disabled');
				}
				if($etiqueta_digit.is(':disabled')) {
					$etiqueta_digit.removeAttr('disabled');
				}
				if(parseInt($select_moneda.val())==1){
					$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
				}else{
					$etiqueta_digit.val('Numero de Cuenta en Dolares');
				}
			}
			
		});
		
		
		//buscador de productos
		$busca_sku.click(function(event){
			event.preventDefault();
			$busca_productos($sku_producto.val(), $nombre_producto.val());
		});
		
		
		//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$(this).aplicarEventoKeypressEjecutaTrigger($sku_producto, $agregar_producto);
		
		//desencadena clic del href Buscar Producto al pulsar enter en el campo Nombre del producto
		$(this).aplicarEventoKeypressEjecutaTrigger($nombre_producto, $busca_sku);
		
		
		
		$tipo_cambio.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		
		//Aplicar tipo de cambio a todos los precios al cambiar valor de tipo de cambio
		$tipo_cambio.blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			
			$(this).val(parseFloat($(this).val()).toFixed(4));
			
			if(parseFloat($(this).val())>=parseFloat($tc_usd_sat.val())){
				$grid_productos.find('tr').each(function (index){
					
					var $campoCantidad = $(this).find('#cant');
					var $campoPrecioU = $(this).find('input[name=costo]');
					var $campoImporte = $(this).find('#import');
					
					var $campoTasaIeps = $(this).find('#tasaIeps');
					var $importeIeps = $(this).find('#importeIeps');
					
					var $campoTasaIva = $(this).find('#ivalorimp');
					var $importeIva = $(this).find('#totimp');
					
					var $ret_tasa = $(this).find('#ret_tasa');
					var $ret_importe = $(this).find('#ret_importe');
					
					var $vdescto = $(this).find('#vdescto');
					var $pu_con_descto = $(this).find('#pu_descto');
					var $importe_del_descto = $(this).find('#importe_del_descto');
					var $importe_con_descto = $(this).find('#importe_con_descto');
					
					var $campoMonedaPrecioOriginalPartida = $(this).find('input[name=id_mon_pre]');
					var $campoPrecioOriginalPartida = $(this).find('input[name=pre_original]');
					
					if ($campoPrecioU.val().trim() == ''){
						$campoPrecioU.val(' ');
					}else{
						$campoPrecioU.val(parseFloat($campoPrecioU.val()).toFixed(4));
					}
					
					if( ($campoPrecioU.val().trim()!='') && ($campoCantidad.val().trim()!='') ){
						if( parseInt($campoMonedaPrecioOriginalPartida.val()) != parseInt($select_moneda.val()) ){
							if(parseInt($campoMonedaPrecioOriginalPartida.val())==1 && parseInt($select_moneda.val())!=1){
								//Si la moneda original es pesos, calculamos su equivalente a dolares
								precio_cambiado = parseFloat($campoPrecioOriginalPartida.val()) / parseFloat($tipo_cambio.val());
							}
							
							if(parseInt($campoMonedaPrecioOriginalPartida.val())!=1 && parseInt($select_moneda.val())==1){
								//Si la moneda original es dolar, calculamos su equivalente a pesos
								precio_cambiado = parseFloat($campoPrecioOriginalPartida.val()) * parseFloat($tipo_cambio.val());
							}
							
							$campoPrecioU.val(parseFloat(precio_cambiado).toFixed(4));
						}
						
						//Calcula el importe
						$campoImporte.val(parseFloat($campoPrecioU.val()) * parseFloat($campoCantidad.val()));
						//Redondea el importe en dos decimales
						//$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
						$campoImporte.val( parseFloat($campoImporte.val()).toFixed(4));
						
						//Calcular el importe del IEPS
						$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
						
						//Calcula el impuesto para este producto multiplicando el importe por el valor del iva
						$importeIva.val((parseFloat($campoImporte.val()) + parseFloat($importeIeps.val())) * parseFloat($campoTasaIva.val()));
						
						if(parseFloat($ret_tasa.val())>0){
							//Calcular la retencion de la partida
							$ret_importe.val(parseFloat(parseFloat($campoImporte.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
						}
						
						if($pdescto.val().trim()=='true'){
							if(parseFloat($vdescto.val())>0){
								$pu_con_descto.val(parseFloat(parseFloat($campoPrecioU.val()) - (parseFloat($campoPrecioU.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
								$importe_del_descto.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
								$importe_con_descto.val(parseFloat(parseFloat($campoImporte.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
								
								//Calcular y redondear el importe del IEPS, tomando el importe con descuento
								$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
								
								//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
								$importeIva.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
								
								if(parseFloat($ret_tasa.val())>0){
									//Calcular la retencion de la partida
									$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
								}
							}
						}
					}else{
						$campoImporte.val(0);
						$importeIva.val(0);
					}
					
					//Llamada a la funcion que calcula totales
					$calcula_totales();
				});
			}else{
				jAlert('El TC USD para la conversion de precios no debe ser menor a '+$tc_usd_sat.val()+'.', 'Atencion!', function(r) { 
					$tipo_cambio.focus(); 
				});
			}
		});
		
		
		
		
		
		$btn_autorizar.click(function(event){
			//LLamada a la funcion de la ventana de autorizacion
			$forma_autorizacion($grid_productos, $btn_autorizar, id_to_show);
		});
		
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			$total_tr.val(trCount);
			if(parseInt(trCount) > 0){
				$subtotal.val(quitar_comas($subtotal.val()));
				$impuesto.val(quitar_comas($impuesto.val()));
				$total.val(quitar_comas($total.val()));
				return true;
			}else{
				//jAlert("No hay datos para actualizar", 'Atencion!');
				jAlert('No hay datos para actualizar', 'Atencion!', function(r) { $sku_producto.focus(); });
				return false;
			}
		});
		
		
		$calcula_totales();
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-pocpedidos-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-pocpedidos-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	var carga_formapocpedidos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_pedido':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  el pedido?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El pedido fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El pedido no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-pocpedidos-window').remove();
			$('#forma-pocpedidos-overlay').remove();
            
			var form_to_show = 'formapocpedidos00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_pocpedidos();
			
			$('#forma-pocpedidos-window').css({"margin-left": -400, 	"margin-top": -235});
			
			$forma_selected.prependTo('#forma-pocpedidos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $total_tr = $('#forma-pocpedidos-window').find('input[name=total_tr]');
			var $id_pedido = $('#forma-pocpedidos-window').find('input[name=id_pedido]');
			var $accion_proceso = $('#forma-pocpedidos-window').find('input[name=accion_proceso]');
			var $folio = $('#forma-pocpedidos-window').find('input[name=folio]');
			var $incluye_produccion = $('#forma-pocpedidos-window').find('input[name=incluye_pro]');
			var $permite_req = $('#forma-pocpedidos-window').find('input[name=permite_req]');
			
			var $busca_cliente = $('#forma-pocpedidos-window').find('a[href*=busca_cliente]');
			var $id_cliente = $('#forma-pocpedidos-window').find('input[name=id_cliente]');
			var $nocliente = $('#forma-pocpedidos-window').find('input[name=nocliente]');
			var $razon_cliente = $('#forma-pocpedidos-window').find('input[name=razoncliente]');
			var $id_df = $('#forma-pocpedidos-window').find('input[name=id_df]');
			var $dir_cliente = $('#forma-pocpedidos-window').find('input[name=dircliente]');
			var $empresa_immex = $('#forma-pocpedidos-window').find('input[name=empresa_immex]');
			var $tasa_ret_immex = $('#forma-pocpedidos-window').find('input[name=tasa_ret_immex]');
			var $cliente_listaprecio=  $('#forma-pocpedidos-window').find('input[name=num_lista_precio]');
			var $pdescto = $('#forma-pocpedidos-window').find('input[name=pdescto]');

        var $select_sat_usos = $('#forma-pocpedidos-window').find('select[name=select_uso]');
        var $select_sat_metodos = $('#forma-pocpedidos-window').find('select[name=select_metodo]');

			var $select_moneda = $('#forma-pocpedidos-window').find('select[name=select_moneda]');
			var $select_moneda_original = $('#forma-pocpedidos-window').find('input[name=select_moneda_original]');
			var $tipo_cambio = $('#forma-pocpedidos-window').find('input[name=tipo_cambio]');
			var $tipo_cambio_original = $('#forma-pocpedidos-window').find('input[name=tipo_cambio_original]');
			var $tc_usd_sat = $('#forma-pocpedidos-window').find('input[name=tc_usd_sat]');
			var $orden_compra = $('#forma-pocpedidos-window').find('input[name=orden_compra]');
			var	$orden_compra_original = $('#forma-pocpedidos-window').find('input[name=orden_compra_original]');
			
			var $id_impuesto = $('#forma-pocpedidos-window').find('input[name=id_impuesto]');
			var $valor_impuesto = $('#forma-pocpedidos-window').find('input[name=valorimpuesto]');
			
			var $check_enviar_obser = $('#forma-pocpedidos-window').find('input[name=check_enviar_obser]');
			var $observaciones = $('#forma-pocpedidos-window').find('textarea[name=observaciones]');
			var $observaciones_original = $('#forma-pocpedidos-window').find('textarea[name=observaciones_original]');
			
			var $select_condiciones = $('#forma-pocpedidos-window').find('select[name=select_condiciones]');
			var $select_condiciones_original = $('#forma-pocpedidos-window').find('select[name=select_condiciones_original]');
			
			var $select_vendedor = $('#forma-pocpedidos-window').find('select[name=vendedor]');
			var $select_vendedor_original = $('#forma-pocpedidos-window').find('select[name=vendedor_original]');
			var $select_almacen = $('#forma-pocpedidos-window').find('select[name=select_almacen]');
			
			var $select_metodo_pago = $('#forma-pocpedidos-window').find('select[name=select_metodo_pago]');
			var $no_cuenta = $('#forma-pocpedidos-window').find('input[name=no_cuenta]');
			var $etiqueta_digit = $('#forma-pocpedidos-window').find('input[name=etiqueta_digit]');
			var $cta_mn = $('#forma-pocpedidos-window').find('input[name=cta_mn]');
			var $cta_usd = $('#forma-pocpedidos-window').find('input[name=cta_usd]');
			var $check_ruta = $('#forma-pocpedidos-window').find('input[name=check_ruta]');
			
			var $transporte = $('#forma-pocpedidos-window').find('input[name=transporte]');
			var $transporte_original = $('#forma-pocpedidos-window').find('input[name=transporte_original]');
			
			var $lugar_entrega = $('#forma-pocpedidos-window').find('input[name=lugar_entrega]');
			var $lugar_entrega_original = $('#forma-pocpedidos-window').find('input[name=lugar_entrega_original]');
			
			var $fecha_compromiso = $('#forma-pocpedidos-window').find('input[name=fecha_compromiso]');
			var $fecha_compromiso_original = $('#forma-pocpedidos-window').find('input[name=fecha_compromiso_original]');
			
			var $no_cotizacion = $('#forma-pocpedidos-window').find('input[name=no_cotizacion]');
			
			//var $select_almacen = $('#forma-pocpedidos-window').find('select[name=almacen]');
			var $sku_producto = $('#forma-pocpedidos-window').find('input[name=sku_producto]');
			var $nombre_producto = $('#forma-pocpedidos-window').find('input[name=nombre_producto]');
			
			//buscar producto
			var $busca_sku = $('#forma-pocpedidos-window').find('a[href*=busca_sku]');
			//href para agregar producto al grid
			var $agregar_producto = $('#forma-pocpedidos-window').find('a[href*=agregar_producto]');
			
			
			var $descargarpdf = $('#forma-pocpedidos-window').find('#descargarpdf');
			var $cancelar_pedido = $('#forma-pocpedidos-window').find('#cancelar_pedido');
			var $cancelado = $('#forma-pocpedidos-window').find('input[name=cancelado]');
			var $btn_autorizar = $('#forma-pocpedidos-window').find('#btn_autorizar');
			
			//grid de productos
			var $grid_productos = $('#forma-pocpedidos-window').find('#grid_productos');
			//grid de errores
			var $grid_warning = $('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning');
			
			var $check_descto = $('#forma-pocpedidos-window').find('input[name=check_descto]');
			var $pdescto = $('#forma-pocpedidos-window').find('input[name=pdescto]');
			var $valor_descto = $('#forma-pocpedidos-window').find('input[name=valor_descto]');
			var $etiqueta_motivo_descto = $('#forma-pocpedidos-window').find('input[name=etiqueta_motivo_descto]');
			var $motivo_descuento = $('#forma-pocpedidos-window').find('input[name=motivo_descuento]');
			
			
			//var $flete = $('#forma-pocpedidos-window').find('input[name=flete]');
			var $subtotal = $('#forma-pocpedidos-window').find('input[name=subtotal]');
			var $ieps = $('#forma-pocpedidos-window').find('input[name=ieps]');
			var $impuesto = $('#forma-pocpedidos-window').find('input[name=impuesto]');
			var $campo_impuesto_retenido = $('#forma-pocpedidos-window').find('input[name=impuesto_retenido]');
			var $total = $('#forma-pocpedidos-window').find('input[name=total]');
		
		
			//Variables para transportista
			var $pestana_transportista = $('#forma-pocpedidos-window').find('ul.pestanas').find('a[href=#tabx-2]');
			var $transportista = $('#forma-pocpedidos-window').find('input[name=transportista]');
			var $check_flete = $('#forma-pocpedidos-window').find('input[name=check_flete]');
			var $nombre_documentador = $('#forma-pocpedidos-window').find('input[name=nombre_documentador]');
			var $valor_declarado = $('#forma-pocpedidos-window').find('input[name=valor_declarado]');
			var $select_tviaje = $('#forma-pocpedidos-window').find('select[name=select_tviaje]');
			var $remolque1 = $('#forma-pocpedidos-window').find('input[name=remolque1]');
			var $remolque2 = $('#forma-pocpedidos-window').find('input[name=remolque2]');
			
			var $id_vehiculo = $('#forma-pocpedidos-window').find('input[name=id_vehiculo]');
			var $no_economico = $('#forma-pocpedidos-window').find('input[name=no_economico]');
			var $marca_vehiculo = $('#forma-pocpedidos-window').find('input[name=marca_vehiculo]');
			
			var $no_operador = $('#forma-pocpedidos-window').find('input[name=no_operador]');
			var $nombre_operador = $('#forma-pocpedidos-window').find('input[name=nombre_operador]');
			
			var $agena_id = $('#forma-pocpedidos-window').find('input[name=agena_id]');
			var $agena_no = $('#forma-pocpedidos-window').find('input[name=agena_no]');
			var $agena_nombre = $('#forma-pocpedidos-window').find('input[name=agena_nombre]');
			
			var $select_pais_origen = $('#forma-pocpedidos-window').find('select[name=select_pais_origen]');
			var $select_estado_origen = $('#forma-pocpedidos-window').find('select[name=select_estado_origen]');
			var $select_municipio_origen = $('#forma-pocpedidos-window').find('select[name=select_municipio_origen]');
			
			var $select_pais_dest = $('#forma-pocpedidos-window').find('select[name=select_pais_dest]');
			var $select_estado_dest = $('#forma-pocpedidos-window').find('select[name=select_estado_dest]');
			var $select_municipio_dest = $('#forma-pocpedidos-window').find('select[name=select_municipio_dest]');
			
			var $rem_id = $('#forma-pocpedidos-window').find('input[name=rem_id]');
			var $rem_no = $('#forma-pocpedidos-window').find('input[name=rem_no]');
			var $rem_nombre = $('#forma-pocpedidos-window').find('input[name=rem_nombre]');
			var $rem_dir = $('#forma-pocpedidos-window').find('input[name=rem_dir]');
			var $rem_dir_alterna = $('#forma-pocpedidos-window').find('input[name=rem_dir_alterna]');
			
			var $dest_id = $('#forma-pocpedidos-window').find('input[name=dest_id]');
			var $dest_no = $('#forma-pocpedidos-window').find('input[name=dest_no]');
			var $dest_nombre = $('#forma-pocpedidos-window').find('input[name=dest_nombre]');
			var $dest_dir = $('#forma-pocpedidos-window').find('input[name=dest_dir]');
			var $dest_dir_alterna = $('#forma-pocpedidos-window').find('input[name=dest_dir_alterna]');
			
			var $observaciones_transportista = $('#forma-pocpedidos-window').find('textarea[name=observaciones_transportista]');
			
			var $busca_vehiculo = $('#forma-pocpedidos-window').find('a[href=busca_vehiculo]');
			var $busca_operador = $('#forma-pocpedidos-window').find('a[href=busca_operador]');
			var $busca_agena = $('#forma-pocpedidos-window').find('a[href=busca_agena]');
			var $busca_remitente = $('#forma-pocpedidos-window').find('a[href=busca_remitente]');
			var $busca_dest = $('#forma-pocpedidos-window').find('a[href=busca_dest]');
			//Termina variables para transportista
			
			var $cerrar_plugin = $('#forma-pocpedidos-window').find('#close');
			var $cancelar_plugin = $('#forma-pocpedidos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-pocpedidos-window').find('#submit');
			
			$pestana_transportista.parent().hide();
			
			$busca_cliente.hide();
			$razon_cliente.attr("readonly", true);
			$empresa_immex.val('false');
			$tasa_ret_immex.val('0');
			$busca_cliente.hide();
			$cancelado.hide();
			$btn_autorizar.hide();
			
			$etiqueta_motivo_descto.hide();
			$motivo_descuento.hide();
			$valor_descto.attr("readonly", true);
			$valor_descto.css({'background' : '#F0F0F0'});
			$valor_descto.val(parseFloat(0).toFixed(4));
			$permitir_solo_numeros($valor_descto);
			$no_cotizacion.attr("readonly", true);
			
			$('#forma-pocpedidos-window').find('#permite_descto').hide();
			
			$permitir_solo_numeros($no_cuenta);
			$no_cuenta.attr('disabled','-1');
			$etiqueta_digit.attr('disabled','-1');
			$folio.css({'background' : '#F0F0F0'});
			$nocliente.css({'background' : '#F0F0F0'});
			$dir_cliente.css({'background' : '#F0F0F0'});
			
			//Ocultar etiquetas
			$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_produccion').hide();
			$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_requisicion').hide();
			$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_req_prod').hide();
			
			//quitar enter a todos los campos input
			$('#forma-pocpedidos-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			if(accion_mode == 'edit'){
				$accion_proceso.attr({'value' : "edit"});
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPedido.json';
				$arreglo = {'id_pedido':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-pocpedidos-window').find('div.interrogacion').css({'display':'none'});
						
						if($accion_proceso.val() == 'cancelar'){
							if ( data['actualizo'] == "1" ){
								jAlert("El Pedido se Cancel&oacute; con &eacute;xito", 'Atencion!');
							}else{
								jAlert(data['actualizo'], 'Atencion!');
							}
						}else{
							jAlert("El Pedido se guard&oacute; con &eacute;xito", 'Atencion!');
						}
						
						var remove = function() {$(this).remove();};
						$('#forma-pocpedidos-overlay').fadeOut(remove);
						
						//ocultar boton actualizar porque ya se actualizo, ya no se puede guardar cambios, hay que cerrar y volver a abrir
						$submit_actualizar.hide();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-pocpedidos-window').find('.div_one').css({'height':'545px'});//sin errores
						//$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'height':'588px'});//con errores
						
						var valorHeight=550;
						
						if(parseFloat(quitar_comas($('#forma-pocpedidos-window').find('input[name=monto_descuento]').val()))>0){
							valorHeight = parseFloat(valorHeight) + 30;
						}
						
						if(parseFloat(quitar_comas($ieps.val()))>0){
							valorHeight = parseFloat(valorHeight) + 25;
						}
						if(parseFloat(quitar_comas($campo_impuesto_retenido.val()))>0){
							valorHeight = parseFloat(valorHeight) + 25;
						}
						$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'height':valorHeight+'px'});
								
						
						$('#forma-pocpedidos-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						$grid_productos.find('#pres').css({'background' : '#ffffff'});
						
						$('#forma-pocpedidos-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						$grid_productos.find('input[name=reqauth]').val('false');
						var contador_alert=0;
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-pocpedidos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								var campo = tmp.split(':')[0];
								var $campo_input;
								var cantidad_existencia=0;
								var  width_td=0;
								
								if((tmp.split(':')[0].substring(0, 8) == 'cantidad') || (tmp.split(':')[0].substring(0, 5) == 'costo') || (tmp.split(':')[0].substring(0, 12) == 'presentacion')){
									
									$('#forma-pocpedidos-window').find('#div_warning_grid').css({'display':'block'});
									
									if(tmp.split(':')[0].substring(0, 12) == 'presentacion'){
										$campo_input = $grid_productos.find('input[name='+campo+']');
									}else{
										$campo_input = $grid_productos.find('.'+campo);
									}
									
									$campo_input.css({'background' : '#d41000'});
									
									var codigo_producto = $campo_input.parent().parent().find('input[name=sku]').val();
									var titulo_producto = $campo_input.parent().parent().find('input[name=nombre]').val();
									
									if($incluye_produccion.val() == 'true' ){
										width_td = 370;
									}else{
										width_td = 255;
									}
									
									var tr_warning = '<tr>';
											tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
											tr_warning += '<td width="90"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:88px; color:red"></td>';
											tr_warning += '<td width="160"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:160px; color:red"></td>';
											tr_warning += '<td width="'+width_td+'"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:'+(parseInt(width_td) - 5)+'px; color:red"></td>';
									tr_warning += '</tr>';
									
									$('#forma-pocpedidos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								}
								
								if(campo == 'backorder'){
									$campo_input = $grid_productos.find('.'+tmp.split(':')[1]);
									cantidad_existencia = tmp.split(':')[2];
									var cant_prod = parseFloat( $campo_input.val() ) - parseFloat(cantidad_existencia);
									
									$campo_input.parent().parent().find('input[name=produccion]').val(parseFloat(cant_prod).toFixed(2));
									$campo_input.parent().parent().find('input[name=existencia]').val(parseFloat(cantidad_existencia).toFixed(2));
									
									if(parseFloat(cant_prod) > 0 ){
										$campo_input.parent().parent().find('input[name=checkProd]').show();
									}
								}
								
								if(tmp.split(':')[0].substring(0,9)=='checkauth'){
									//alert(tmp);
									$grid_productos.find('.'+tmp.split(':')[0]).show();
									$grid_productos.find('.'+tmp.split(':')[0]).parent().find('input[name=reqauth]').val('true');
									
									if(parseInt(contador_alert)<=0){
										//Confirm para guardar sin autorizacion
										jConfirm('Hay precios que necesitan autorizaci&oacute;n.<br>&iquest;Desea guardar para autorizar despu&eacute;s&#63;', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												//Asignar los estatus correspondientes para permitir guardar sin autorizacion
												$grid_productos.find('input[name=checkauth]').each(function(index){
													$tr = $(this).parent().parent();
													if(parseInt($tr.find('input[name=eliminado]').val())==1){
														$tr.find('input[name=success]').val('true');
													}
												});
												
												//Ejecutar el submit de actualizar
												$submit_actualizar.trigger('click');
											}
										});
									}
									
									contador_alert++;
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
					//Almacenar el arreglo de unidades de medida en la variable
					arrayUM = entry['UM'];
					
					//Almacenar valor para variable que indica si se debe permitir el cambio de la unidad de medida
					cambiarUM = entry['Extras'][0]['cambioUM']
					
					if(entry['Extras'][0]['per_descto']=='true'){
						$('#forma-pocpedidos-window').find('#permite_descto').show();
					}
					
					$incluye_produccion.val(entry['Extras']['0']['mod_produccion']);
					$permite_req.val(entry['Extras']['0']['per_req']);
					
					if(entry['Extras'][0]['mod_produccion']=='true' || entry['Extras'][0]['per_req']=='true'){
						
						if(entry['Extras'][0]['mod_produccion']=='true' && entry['Extras'][0]['per_req']=='false'){
							//Mostrar etiqueta para cuando incluye solo produccion
							$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_produccion').show();
						}
						
						if(entry['Extras'][0]['mod_produccion']=='false' && entry['Extras'][0]['per_req']=='true'){
							//Mostrar etiqueta para cuando incluye solo requisicion
							$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_requisicion').show();
						}
						
						if(entry['Extras'][0]['mod_produccion']=='true' && entry['Extras'][0]['per_req']=='true'){
							//Mostrar etiqueta para cuando incluye requisicion y produccion
							$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').find('#etiqueta_req_prod').show();
						}
						
						$('#forma-pocpedidos-window').css({"margin-left": -450, 	"margin-top": -235});
						$('#forma-pocpedidos-window').find('.pocpedidos_div_one').css({'width':'1180px'});
						$('#forma-pocpedidos-window').find('.pocpedidos_div_two').css({'width':'1180px'});
						$('#forma-pocpedidos-window').find('#titulo_plugin').css({'width':'1140px'});
						$('#forma-pocpedidos-window').find('.header_grid').css({'width':'1155px'});
						$('#forma-pocpedidos-window').find('.contenedor_grid').css({'width':'1145px'});
						$('#forma-pocpedidos-window').find('#div_botones').css({'width':'1153px'});
						$('#forma-pocpedidos-window').find('#div_botones').find('.tabla_botones').find('.td_left').css({'width':'1053px'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').css({'width':'810px'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').find('.td_head').css({'width':'470px'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').find('.div_cont_grid_warning').css({'width':'800px'});
						$('#forma-pocpedidos-window').find('#div_warning_grid').find('.div_cont_grid_warning').find('#grid_warning').css({'width':'780px'});
					}else{
						//Ocultar td porque la empresa no incluye Produccion
						$('#forma-pocpedidos-window').find('.tabla_header_grid').find('#td_oculto').hide();
					}
					
					$tasa_ret_immex.val(entry['datosPedido'][0]['tasa_retencion_immex']);
					$id_pedido.val(entry['datosPedido'][0]['id']);
					$folio.val(entry['datosPedido'][0]['folio']);
					$id_cliente.val(entry['datosPedido'][0]['cliente_id']);
					$nocliente.val(entry['datosPedido'][0]['numero_control']);
					$razon_cliente.val(entry['datosPedido'][0]['razon_social']);
					$id_df.val(entry['datosPedido'][0]['df_id']);
					$dir_cliente.val(entry['datosPedido'][0]['direccion']);
					$cliente_listaprecio.val(entry['datosPedido'][0]['lista_precio']);
					$pdescto.val(entry['datosPedido'][0]['pdescto']);
					$motivo_descuento.val(entry['datosPedido'][0]['mdescto']);
					$no_cotizacion.val(entry['datosPedido'][0]['folio_cot']);
					
					if($pdescto.val()=='true'){
						$('#forma-pocpedidos-window').find('input[name=check_descto]').attr('checked',  ($pdescto.val() == 'true')? true:false );
						$('#forma-pocpedidos-window').find('#permite_descto').show();
						$valor_descto.val(entry['datosPedido'][0]['porcentaje_descto']);
					}
					
					$check_enviar_obser.attr('checked',  (entry['datosPedido'][0]['enviar_obser'] == 'true')? true:false );
					$observaciones.text(entry['datosPedido'][0]['observaciones']);
					$observaciones_original.val(entry['datosPedido'][0]['observaciones']);
					
					$orden_compra.val(entry['datosPedido'][0]['orden_compra']);
					$orden_compra_original.val(entry['datosPedido'][0]['orden_compra']);
                    
					$transporte.val(entry['datosPedido'][0]['transporte']);
					$transporte_original.val(entry['datosPedido'][0]['transporte']);
                    
					$lugar_entrega.val(entry['datosPedido'][0]['lugar_entrega']);
					$lugar_entrega_original.val(entry['datosPedido'][0]['lugar_entrega']);
					
					$fecha_compromiso.val(entry['datosPedido'][0]['fecha_compromiso']);
					$fecha_compromiso_original.val(entry['datosPedido'][0]['fecha_compromiso']);
					
					$tipo_cambio.val(entry['datosPedido'][0]['tipo_cambio']);
					$tipo_cambio_original.val(entry['datosPedido'][0]['tipo_cambio']);
					$tc_usd_sat.val(entry['Tc'][0]['tipo_cambio']);
					
					$no_cuenta.val(entry['datosPedido'][0]['no_cuenta']);
					
					$cta_mn.val(entry['datosPedido'][0]['cta_pago_mn']);
					$cta_usd.val(entry['datosPedido'][0]['cta_pago_usd']);
					
					$check_ruta.attr('checked',  (entry['datosPedido'][0]['enviar_ruta'] == 'true')? true:false );
					
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosPedido'][0]['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
							$select_moneda_original.val(moneda['id']);
						}else{
							if(parseInt(entry['datosPedido'][0]['proceso_flujo_id'])==4){
								//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							}
						}
					});
					$select_moneda.append(moneda_hmtl);
					$select_moneda.find('option').clone().appendTo($select_moneda_original);
					
					
					$id_impuesto.val(entry['iva'][0]['id_impuesto']);
					$valor_impuesto.val(entry['iva'][0]['valor_impuesto']);
					
					//carga select de vendedores
					$select_vendedor.children().remove();
					var hmtl_vendedor;
					$.each(entry['Vendedores'],function(entryIndex,vendedor){
						if(entry['datosPedido'][0]['cxc_agen_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_agente'] + '</option>';
						}else{
							if(parseInt(entry['datosPedido'][0]['proceso_flujo_id'])==4){
								hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_agente'] + '</option>';
							}
						}
					});
					$select_vendedor.append(hmtl_vendedor);
					$select_vendedor.find('option').clone().appendTo($select_vendedor_original);
					
					//carga select de condiciones
					$select_condiciones.children().remove();
					var hmtl_condiciones;
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['datosPedido'][0]['cxp_prov_credias_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							if(parseInt(entry['datosPedido'][0]['proceso_flujo_id'])==4){
								hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
							}
						}
					});
					$select_condiciones.append(hmtl_condiciones);
					$select_condiciones.find('option').clone().appendTo($select_condiciones_original);
					
					
					//carga select de almacenes
					$select_almacen.children().remove();
					var hmtl_alm;
					$.each(entry['Almacenes'],function(entryIndex,alm){
						hmtl_alm += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
					});
					$select_almacen.append(hmtl_alm);
					
					
					
					var valor_metodo = entry['datosPedido'][0]['metodo_pago_id'];
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo="";
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(valor_metodo == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '" selected="yes" >' + metodo['titulo'] + '</option>';
						}else{
							if(parseInt(entry['datosPedido'][0]['proceso_flujo_id'])==4){
								hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
							}
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					
					$select_sat_usos.children().remove();
                                        fullfill_select( $select_sat_usos, entry['Usos'], entry['datosPedido'][0]['cfdi_usos_id'], "numero_control" );

                                        $select_sat_metodos.children().remove();
                                        fullfill_select( $select_sat_metodos, entry['Metodos'], entry['datosPedido'][0]['cfdi_metodo_id'], "clave" );
					
					if(parseInt(valor_metodo)==2 || parseInt(valor_metodo)==3){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura de los digitos de la tarjeta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						//quitar propiedad de solo lectura
						$no_cuenta.removeAttr('readonly');
						
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						$etiqueta_digit.val('Ingrese los ultimos 4 Digitos de la Tarjeta');
					}
					
					
					if(parseInt(valor_metodo)==4 || parseInt(valor_metodo)==5){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
					//valor_metodo 1=Efectivo, 6=No Identificado
					if(parseInt(valor_metodo)==1 || parseInt(valor_metodo)==6){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
					//valor_metodo 7=NA
					if(parseInt(valor_metodo)==7){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
					
					
					if(parseInt(entry['datosGrid'].length) > 0 ){
						$.each(entry['datosGrid'],function(entryIndex,prod){
							
							//Obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="25">';
									trr += '<a href="elimina_producto" id="delete'+ tr +'"><div id="eliminar'+ tr +'" class="onmouseOutEliminar" style="width:24px; background-position:center;"/></a>';
									//El 1 significa que el registro no ha sido eliminado
									trr += '<input type="hidden" name="eliminado" id="elim" value="1">';
									//Este es el id del registro que ocupa el producto en la tabla pocpedidos_detalles
									trr += '<input type="hidden" name="iddetalle" id="idd" value="'+ prod['id_detalle'] +'">';
									trr += '<input type="hidden" name="noTr" value="'+ tr +'">';
									//trr += '<span id="elimina">1</span>';
									
									trr += '<input type="hidden" name="idcot" id="idcot" value="'+ prod['id_cot'] +'">';
									if($no_cotizacion.val().trim()==''){
										trr += '<input type="hidden" name="nocot" id="nocot" value=" ">';
									}else{
										trr += '<input type="hidden" name="nocot" id="nocot" value="'+ $no_cotizacion.val() +'">';
									}
									trr += '<input type="hidden" name="iddetcot" id="iddetcot" value="'+ prod['id_cot_det'] +'">';
									
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="116">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['inv_prod_id'] +'">';
									trr += '<input type="text" name="sku" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:110px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="200">';
								// trr += '<input type="text" 	name="nombre" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:196px;">';
                                                                let arrTitulo = prod['titulo'].split('||');
                                                                let option = '';
                                                                let arrOption;

                                                                if (arrTitulo.length == 1) {

                                                                    arrOption = arrTitulo[0].split('|');

                                                                    if (arrOption.length == 1) {
                                                                        option += '<option value="0">' + arrOption[0] + '</option>';
                                                                    } else {
                                                                        option += '<option value="' + arrOption[0] + '">' + arrOption[1] + '</option>';
                                                                    }

                                                                } else {

                                                                    for (i of arrTitulo) {
                                                                        arrOption = i.split('|');

                                                                        if (prod['inv_prod_alias_id'] == arrOption[0]) {
                                                                            option += '<option value="' + arrOption[0] + '" selected>' + arrOption[1] + '</option>';
                                                                        } else {
                                                                            option += '<option value="' + arrOption[0] + '">' + arrOption[1] + '</option>';
                                                                        }
                                                                    }
                                                                }
                                                                trr += '<select name="nombre" id="nom" class="nombre' + tr + '" style="width:196px;">' + option + '</select>';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<select name="select_umedida" class="select_umedida'+ tr +'" style="width:86px;"></select>';
								trr += '<input type="text" 		name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<input type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
									trr += '<input type="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" 	name="cantidad" value="'+  prod['cantidad'] +'" class="cantidad'+ tr +'" id="cant" style="width:76px;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<input type="text" 		name="costo" 	value="'+  prod['precio_unitario'] +'" 	class="costo'+ tr +'" id="cost" style="width:86px; text-align:right;">';
								trr += '<input type="hidden" value="'+  prod['precio_unitario'] +'" id="costor">';
								
								var precio_u_con_descto = parseFloat( parseFloat(prod['precio_unitario']) - (parseFloat(prod['precio_unitario'])*(parseFloat(prod['descto'])/100)) ).toFixed(4);
								
								trr += '<input type="hidden" 	name="vdescto" id="vdescto" value="'+ prod['descto'] +'">';
								trr += '<input type="hidden" 	name="pu_descto" id="pu_descto" value="'+ precio_u_con_descto +'">';
								
								//Se toma la moneda del precio como moneda del precio
								trr += '<input type="hidden" 	name="id_mon_pre" id="id_moneda" value="'+ $select_moneda.val() +'">';
								//Se toma el precio como precio original porque ya no disponemos del precio de la lista de precios
								trr += '<input type="hidden" 	name="pre_original" id="prec_original" value="'+ prod['precio_unitario'] +'">';
								
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size:11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<input type="text" 		name="importe'+ tr +'" 	value="'+  prod['importe'] +'" 	id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
								trr += '<input type="hidden"    name="id_imp_prod"  value="'+  prod['gral_imp_id'] +'" 		id="idimppord">';
								trr += '<input type="hidden"    name="valor_imp" 	value="'+  prod['valor_imp'] +'" 	id="ivalorimp">';
								
								var importeIeps = 0;
								var importeIva = 0;
								var importeRetencionIva = 0;
								var importe_del_descuento = parseFloat(parseFloat(prod['importe'])*(parseFloat(prod['descto'])/100)).toFixed(4);
								var importe_con_descto = parseFloat(parseFloat(prod['importe'])-parseFloat(importe_del_descuento)).toFixed(4);
								
								if($pdescto.val().trim()=='true' && parseFloat(prod['descto'])>0){
									importeIeps = parseFloat(parseFloat(importe_con_descto) * (parseFloat(prod['valor_ieps'])/100)).toFixed(4);
									importeIva = (parseFloat(importe_con_descto) + parseFloat(importeIeps)) * parseFloat(prod['valor_imp']);
									importeRetencionIva = parseFloat(parseFloat(importe_con_descto) * (parseFloat(prod['ret_tasa'])/100)).toFixed(4);
								}else{
									importeIeps = parseFloat(parseFloat(prod['importe']) * (parseFloat(prod['valor_ieps'])/100)).toFixed(4);
									importeIva = (parseFloat(prod['importe']) + parseFloat(importeIeps)) * parseFloat( prod['valor_imp'] );
									importeRetencionIva = parseFloat(parseFloat(prod['importe']) * (parseFloat(prod['ret_tasa'])/100)).toFixed(4);
								}
								
								trr += '<input type="hidden" 	name="totimpuesto'+ tr +'" id="totimp" value="'+ importeIva +'">';
								trr += '<input type="hidden" name="importe_del_descto" id="importe_del_descto" value="'+ importe_del_descuento +'">';
								trr += '<input type="hidden" name="importe_con_descto" id="importe_con_descto" value="'+ importe_con_descto +'">';
								
								trr += '<input type="hidden" name="ret_id" 		id="ret_id" value="'+  prod['ret_id'] +'">';
								trr += '<input type="hidden" name="ret_tasa" 	id="ret_tasa" value="'+  prod['ret_tasa'] +'">';
								trr += '<input type="hidden" name="ret_importe" id="ret_importe" value="'+ importeRetencionIva +'">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
								trr += '<input type="hidden" name="idIeps"     value="'+ prod['ieps_id'] +'" id="idIeps">';
								trr += '<input type="text" name="tasaIeps" value="'+ prod['valor_ieps'] +'" class="borde_oculto" id="tasaIeps" style="width:56px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" name="importeIeps" value="'+ importeIeps +'" class="borde_oculto" id="importeIeps" style="width:76px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							var cant_prod = prod['cant_produccion'];
							
							trr += '<td class="grid2" id="td_oculto'+ tr +'" style="font-size:11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" 		name="produccion" 	value="'+cant_prod+'" 	 class="borde_oculto" readOnly="true" style="width:76px; text-align:right;">';
								trr += '<input type="hidden"    name="existencia" 	value="0">';
							trr += '</td>';
							
							var desactivado="";
							var check=prod['valor_check'];
							var valor_seleccionado = prod['valor_selecionado'];
							
							trr += '<td class="grid2" id="td_oculto'+ tr +'" style="font-size: 11px;  border:1px solid #C1DAD7;" width="20">';
								trr += '<input type="checkbox" 	name="checkProd" class="checkProd'+ tr +'" '+check+' '+desactivado+'>';
								trr += '<input type="hidden" 	name="seleccionado" value="'+valor_seleccionado+'">';//el 1 significa que el registro no ha sido eliminado
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size:11px;  border:1px solid #C1DAD7;" width="20" id="td_check_auth'+ tr +'">';
								trr += '<input type="hidden" 	name="statusreg"   	class="statusreg'+ tr +'" value="'+ prod['status_aut'] +'">';
								trr += '<input type="hidden" 	name="reqauth"   	class="reqauth'+ tr +'" value="false">';
								trr += '<input type="hidden" 	name="success"   	class="success'+ tr +'" value="false">';
								trr += '<input type="checkbox" 	name="checkauth" 	class="checkauth'+ tr +'">';
							trr += '</td>';
							
							trr += '</tr>';
							$grid_productos.append(trr);
                            
                            
							$grid_productos.find('.checkauth'+tr).hide();
							
							$grid_productos.find('.checkauth'+ tr).click(function(event){
								if(this.checked){
									$btn_autorizar.show();
								}else{
									var cont_check=0;
									$grid_productos.find('input[name=checkauth]').each(function(index){
										if(this.checked){
											if(parseInt($(this).parent().parent().find('input[name=eliminado]').val())==1){
												cont_check++;
											}
										}
									});
									
									if(parseInt(cont_check)<=0){
										$btn_autorizar.hide();
									}
								}
							});
							
							
							//carga select de metodos de pago
							$grid_productos.find('select.select_umedida'+tr).children().remove();
							var hmtl_um="";
							$.each(arrayUM,function(entryIndex,um){
								if(parseInt(prod['unidad_id']) == parseInt(um['id'])){
									hmtl_um += '<option value="' + um['id'] + '" selected="yes" >' + um['titulo'] + '</option>';
								}
							});
							$grid_productos.find('select.select_umedida'+tr).append(hmtl_um);
							
							if(cambiarUM.trim()=='true'){
								//Ocultar campo input porque se debe mostrar select para permitir cambio de unidad de medida
								$grid_productos.find('input[name=unidad'+ tr +']').hide();
							}else{
								//Ocultar porque no se permitirá cambiar de unidad de medida
								$grid_productos.find('select.select_umedida'+tr).hide();
							}
                            
                            
                            if(entry['Extras'][0]['mod_produccion']=='true' || entry['Extras'][0]['per_req']=='true'){
								//Aplicar evento click al check, cuando la empresa incluya modulo de produccion
								$aplicar_evento_click_a_input_check($grid_productos.find('.checkProd'+ tr));
								
								if(parseFloat(cant_prod) <=0 ){
									//Ocualtar check, solo se debe mostrar cuando el producto no tenga existencia suficiente
									$grid_productos.find('.checkProd'+tr).hide();
								}
                            }else{
								//ocualtar campos,  cuando la empresa no incluya modulo de produccion
								$grid_productos.find('#td_oculto'+tr).hide();
							}
                            
                            
                            
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('.cantidad'+ tr ).focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							//Recalcula importe al perder enfoque el campo cantidad
							$grid_productos.find('.cantidad'+ tr ).blur(function(){
								var $campoCantidad = $(this);
								var $campoPrecioU = $(this).parent().parent().find('#cost');
								var $campoImporte = $(this).parent().parent().find('#import');
								
								var $campoTasaIeps = $(this).parent().parent().find('#tasaIeps');
								var $importeIeps = $(this).parent().parent().find('#importeIeps');
								
								var $campoTasaIva = $(this).parent().parent().find('#ivalorimp');
								var $importeIva = $(this).parent().parent().find('#totimp');
								
								var $ret_tasa = $(this).parent().parent().find('#ret_tasa');
								var $ret_importe = $(this).parent().parent().find('#ret_importe');
								
								var $vdescto = $(this).parent().parent().find('#vdescto');
								var $pu_con_descto = $(this).parent().parent().find('#pu_descto');
								var $importe_del_descto = $(this).parent().parent().find('#importe_del_descto');
								var $importe_con_descto = $(this).parent().parent().find('#importe_con_descto');
								
								if ($campoCantidad.val().trim() == ''){
									$campoCantidad.val(' ');
								}else{
									$campoCantidad.val(parseFloat($campoCantidad.val()).toFixed(parseInt(prod['no_dec'])));
								}
								
								if( ($campoCantidad.val().trim() != '') && ($campoPrecioU.val().trim() != '') ){
									//Calcula el importe
									$campoImporte.val(parseFloat($campoCantidad.val()) * parseFloat($campoPrecioU.val()));
									
									//Redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val( Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100 );
									$campoImporte.val( parseFloat($campoImporte.val()).toFixed(4) );
									
									//Calcular el importe del IEPS
									$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
									
									//Calcula el IVA para este producto multiplicando el importe + ieps por la tasa del iva
									$importeIva.val((parseFloat($campoImporte.val()) + parseFloat($importeIeps.val()) ) * parseFloat( $campoTasaIva.val() ));
									
									if(parseFloat($ret_tasa.val())>0){
										//Calcular la retencion de la partida
										$ret_importe.val(parseFloat(parseFloat($campoImporte.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
									}
									
									if($pdescto.val().trim()=='true'){
										if(parseFloat($vdescto.val())>0){
											$pu_con_descto.val(parseFloat(parseFloat($campoPrecioU.val()) - (parseFloat($campoPrecioU.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
											$importe_del_descto.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
											$importe_con_descto.val(parseFloat(parseFloat($campoImporte.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
											
											//Calcular y redondear el importe del IEPS, tomando el importe con descuento
											$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
											
											//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
											$importeIva.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
											
											if(parseFloat($ret_tasa.val())>0){
												//Calcular la retencion del importe con descuento
												$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
											}
										}
									}
									
								}else{
									$campoImporte.val(0);
									$importeIva.val(0);
								}
								//Llamada a la funcion que calcula totales
								$calcula_totales();
							});
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('.costo'+ tr).focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							//Recalcula importe al perder enfoque el campo costo
							$grid_productos.find('.costo'+ tr).blur(function(){
								var $campoCantidad = $(this).parent().parent().find('#cant');
								var $campoPrecioU = $(this);
								var $campoImporte = $(this).parent().parent().find('#import');
								
								var $campoTasaIeps = $(this).parent().parent().find('#tasaIeps');
								var $importeIeps = $(this).parent().parent().find('#importeIeps');
								
								var $campoTasaIva = $(this).parent().parent().find('#ivalorimp');
								var $importeIva = $(this).parent().parent().find('#totimp');
								
								var $ret_tasa = $(this).parent().parent().find('#ret_tasa');
								var $ret_importe = $(this).parent().parent().find('#ret_importe');
								
								var $vdescto = $(this).parent().parent().find('#vdescto');
								var $pu_con_descto = $(this).parent().parent().find('#pu_descto');
								var $importe_del_descto = $(this).parent().parent().find('#importe_del_descto');
								var $importe_con_descto = $(this).parent().parent().find('#importe_con_descto');
								
								if ($campoPrecioU.val().trim() == ''){
									$campoPrecioU.val(' ');
								}else{
									$campoPrecioU.val(parseFloat($campoPrecioU.val()).toFixed(4));
								}
								
								//Quitar marca que indica que requiere autorizacion
								$(this).parent().parent().find('input[name=reqauth]').val('false');
								
								if( ($campoPrecioU.val().trim() != '') && ($campoCantidad.val().trim() != '') ){
									//Calcula el importe
									$campoImporte.val(parseFloat($campoPrecioU.val()) * parseFloat($campoCantidad.val()));
									//Redondea el importe en dos decimales
									//$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
									$campoImporte.val( parseFloat($campoImporte.val()).toFixed(4));
									
									//Calcular el importe del IEPS
									$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
									
									//Calcula el impuesto para este producto multiplicando el importe por el valor del iva
									$importeIva.val((parseFloat($campoImporte.val()) + parseFloat($importeIeps.val())) * parseFloat($campoTasaIva.val()));
									
									if(parseFloat($ret_tasa.val())>0){
										//Calcular la retencion de la partida
										$ret_importe.val(parseFloat(parseFloat($campoImporte.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
									}
									
									if($pdescto.val().trim()=='true'){
										if(parseFloat($vdescto.val())>0){
											$pu_con_descto.val(parseFloat(parseFloat($campoPrecioU.val()) - (parseFloat($campoPrecioU.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
											
											$importe_del_descto.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
											$importe_con_descto.val(parseFloat(parseFloat($campoImporte.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
											
											//Calcular y redondear el importe del IEPS, tomando el importe con descuento
											$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
											
											//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
											$importeIva.val( (parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
											
											if(parseFloat($ret_tasa.val())>0){
												//Calcular la retencion del importe con descuento
												$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
											}
										}
									}
									
								}else{
									$campoImporte.val(0);
									$importeIva.val(0);
								}
								
								//Llamada a la funcion que calcula totales
								$calcula_totales();
							});
							
							//validar campo costo, solo acepte numeros y punto
							$permitir_solo_numeros( $grid_productos.find('#cost') );
							$permitir_solo_numeros( $grid_productos.find('#cant') );
							
							//Elimina un producto del grid
							$grid_productos.find('#delete'+ tr).bind('click',function(event){
								event.preventDefault();
								if(parseInt($(this).parent().find('#elim').val()) != 0){
									var iddetalle = $(this).parent().find('#idd').val();
									
									//asigna espacios en blanco a todos los input de la fila eliminada
									$(this).parent().parent().find('input').val(' ');
									
									//asigna un 0 al input eliminado como bandera para saber que esta eliminado
									$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
									$(this).parent().find('#idd').val(iddetalle);
									
									$(this).parent().parent().find('input[name=statusreg]').val('0&&&0&&&0');
									$(this).parent().parent().find('input[name=reqauth]').val('false');
									$(this).parent().parent().find('input[name=success]').val('false');
									
									
									//oculta la fila eliminada
									$(this).parent().parent().hide();
									$calcula_totales();//llamada a la funcion que calcula totales
								}
							});
							
							$grid_productos.find('#eliminar'+ tr).mouseover(function(){
								$(this).removeClass("onmouseOutEliminar").addClass("onmouseOverEliminar");
							});
							$grid_productos.find('#eliminar'+ tr).mouseout(function(){
								$(this).removeClass("onmouseOverEliminar").addClass("onmouseOutEliminar");
							});
							
						});
						
					}
					
					$calcula_totales();//llamada a la funcion que calcula totales 
					
					
					
					
					
					
					
					//Inicia carga de datos para pestaña de transportista
					if(entry['Extras'][0]['transportista']=='true'){
						var elemento_seleccionado = 0;
						var texto_elemento_cero = '';
						var index_elem = '';
						var index_text_elem = '';
						
						//LLamada a la funcion para aplicar el evento change al select tipo de viaje
						$aplicar_evento_change_select_tviaje($select_tviaje, $remolque1, $remolque2);
						
						$check_flete.attr('checked',  (entry['datosPedido'][0]['flete']=='true')? true:false );
						$pestana_transportista.parent().show();
						
						if(entry['datosPedido'][0]['flete']=='true'){
							$nombre_documentador.val(entry['datosTrans'][0]['documentador'].trim());
							$valor_declarado.val(entry['datosTrans'][0]['valor_declarado'].trim());
							
							$remolque1.val(entry['datosTrans'][0]['remolque1'].trim());
							$remolque2.val(entry['datosTrans'][0]['remolque2'].trim());
							
							$id_vehiculo.val(entry['datosTrans'][0]['vehiculo_id']);
							$no_economico.val(entry['datosTrans'][0]['vehiculo_no']);
							$marca_vehiculo.val(entry['datosTrans'][0]['vehiculo_marca']);
							
							$no_operador.val(entry['datosTrans'][0]['no_operador']);
							$nombre_operador.val(entry['datosTrans'][0]['nombre_operador']);
							
							$agena_id.val(entry['datosTrans'][0]['agena_id']);
							$agena_no.val(entry['datosTrans'][0]['agena_no']);
							$agena_nombre.val(entry['datosTrans'][0]['agena_nombre']);
							
							$rem_id.val(entry['datosTrans'][0]['rem_id']);
							$rem_no.val(entry['datosTrans'][0]['rem_no']);
							$rem_nombre.val(entry['datosTrans'][0]['rem_nombre']);
							$rem_dir.val(entry['datosTrans'][0]['rem_dir']);
							$rem_dir_alterna.val(entry['datosTrans'][0]['rem_dir_alterna']);
							
							$dest_id.val(entry['datosTrans'][0]['dest_id']);
							$dest_no.val(entry['datosTrans'][0]['dest_no']);
							$dest_nombre.val(entry['datosTrans'][0]['dest_nombre']);
							$dest_dir.val(entry['datosTrans'][0]['dest_dir']);
							$dest_dir_alterna.val(entry['datosTrans'][0]['dest_dir_alterna']);
							
							$observaciones_transportista.text(entry['datosTrans'][0]['trans_observaciones']);
							
							if(parseInt(entry['datosTrans'][0]['vehiculo_id'])!=0){
								$busca_vehiculo.hide();
								$aplicar_readonly_input($marca_vehiculo);
							}
							if(entry['datosTrans'][0]['nombre_operador']!=''){
								$busca_operador.hide();
							}
							if(parseInt(entry['datosTrans'][0]['agena_id'])!=0){
								$busca_agena.hide();
								$aplicar_readonly_input($agena_nombre);
							}
							if(parseInt(entry['datosTrans'][0]['rem_id'])!=0){
								$busca_remitente.hide();
								$aplicar_readonly_input($rem_nombre);
								$aplicar_readonly_input($rem_dir);
							}
							if(parseInt(entry['datosTrans'][0]['dest_id'])!=0){
								$busca_dest.hide();
								$aplicar_readonly_input($dest_nombre);
								$aplicar_readonly_input($dest_dir);
							}
							
							var tviaje_hmtl = '';
							if(parseInt(entry['datosTrans'][0]['tipo_viaje'])==1){
								$aplicar_readonly_input($remolque2);
								tviaje_hmtl = '<option value="1" selected="yes">Sencilla</option>';
								tviaje_hmtl += '<option value="2" >Full</option>';
							}else{
								tviaje_hmtl = '<option value="1">Sencilla</option>';
								tviaje_hmtl += '<option value="2" selected="yes">Full</option>';
							}
							//Alimentar select de tipo de viaje
							$select_tviaje.children().remove();
							$select_tviaje.append(tviaje_hmtl);
							
							//carga select de pais Origen
							elemento_seleccionado = entry['datosTrans'][0]['pais_id_orig'];
							texto_elemento_cero = '[-Seleccionar Pais-]';
							index_elem = 'cve_pais';
							index_text_elem = 'pais_ent';
							$carga_campos_select($select_pais_origen, entry['Paises'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//Carga select de estado Origen
							elemento_seleccionado = entry['datosTrans'][0]['edo_id_orig'];
							texto_elemento_cero = '[-Seleccionar Estado--]';
							index_elem = 'cve_ent';
							index_text_elem = 'nom_ent';
							$carga_campos_select($select_estado_origen, entry['EdoOrig'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//Carga select de municipio Origen
							elemento_seleccionado = entry['datosTrans'][0]['mun_id_orig'];
							texto_elemento_cero = '[-Seleccionar Municipio-]';
							index_elem = 'cve_mun';
							index_text_elem = 'nom_mun';
							$carga_campos_select($select_municipio_origen, entry['MunOrig'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							
							//carga select de pais Destino
							elemento_seleccionado = entry['datosTrans'][0]['pais_id_dest'];
							texto_elemento_cero = '[-Seleccionar Pais-]';
							index_elem = 'cve_pais';
							index_text_elem = 'pais_ent';
							$carga_campos_select($select_pais_dest, entry['Paises'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//Carga select de estado Destino
							elemento_seleccionado = entry['datosTrans'][0]['edo_id_dest'];
							texto_elemento_cero = '[-Seleccionar Estado--]';
							index_elem = 'cve_ent';
							index_text_elem = 'nom_ent';
							$carga_campos_select($select_estado_dest, entry['EdoDest'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//Carga select de municipio Destino
							elemento_seleccionado = entry['datosTrans'][0]['mun_id_dest'];
							texto_elemento_cero = '[-Seleccionar Municipio-]';
							index_elem = 'cve_mun';
							index_text_elem = 'nom_mun';
							$carga_campos_select($select_municipio_dest, entry['MunDest'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
						}else{
							//Aqui entra cuando no es pedido de flete
							//Esta informacion que se agrega en esta parte es para permitir al usuario la posibilidad de convertirlo en pedido de Flete
							//carga select de pais Origen
							elemento_seleccionado = 0;
							texto_elemento_cero = '[-Seleccionar Pais-]';
							index_elem = 'cve_pais';
							index_text_elem = 'pais_ent';
							$carga_campos_select($select_pais_origen, entry['Paises'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							//carga select de pais Destino
							elemento_seleccionado = 0;
							texto_elemento_cero = '[-Seleccionar Pais-]';
							index_elem = 'cve_pais';
							index_text_elem = 'pais_ent';
							$carga_campos_select($select_pais_dest, entry['Paises'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
							
							$aplicar_readonly_input($remolque2);
						}
						
						
						//Carga select estados al cambiar el pais Origen
						$select_pais_origen.change(function(){
							var valor_pais = $(this).val();
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
							$arreglo = {'id_pais':valor_pais};
							$.post(input_json,$arreglo,function(entry){
								$select_estado_origen.children().remove();
								var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
								$.each(entry['Estados'],function(entryIndex,entidad){
									entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
								});
								$select_estado_origen.append(entidad_hmtl);
								
								var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
								$select_municipio_origen.children().remove();
								$select_municipio_origen.append(trama_hmtl_localidades);
							},"json");//termina llamada json
						});
						
						//Carga select municipios al cambiar el estado origen
						$select_estado_origen.change(function(){
							var valor_entidad = $(this).val();
							var valor_pais = $select_pais_origen.val();
							
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
							$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
							$.post(input_json,$arreglo,function(entry){
								$select_municipio_origen.children().remove();
								var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
								$.each(entry['Municipios'],function(entryIndex,mun){
									trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
								});
								$select_municipio_origen.append(trama_hmtl_localidades);
							},"json");//termina llamada json
						});
						
						
						
						
						//Carga select estados al cambiar el pais destino
						$select_pais_dest.change(function(){
							var valor_pais = $(this).val();
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
							$arreglo = {'id_pais':valor_pais};
							$.post(input_json,$arreglo,function(entry){
								$select_estado_dest.children().remove();
								var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
								$.each(entry['Estados'],function(entryIndex,entidad){
									entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
								});
								$select_estado_dest.append(entidad_hmtl);
								
								var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
								$select_municipio_dest.children().remove();
								$select_municipio_dest.append(trama_hmtl_localidades);
							},"json");//termina llamada json
						});
						
						//Carga select municipios al cambiar el estado destino
						$select_estado_dest.change(function(){
							var valor_entidad = $(this).val();
							var valor_pais = $select_pais_dest.val();
							
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
							$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
							$.post(input_json,$arreglo,function(entry){
								$select_municipio_dest.children().remove();
								var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
								$.each(entry['Municipios'],function(entryIndex,mun){
									trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
								});
								$select_municipio_dest.append(trama_hmtl_localidades);
							},"json");//termina llamada json
						});
						
						
						

											
						//Buscador de Unidades(Vehiculo)
						$busca_vehiculo.click(function(event){
							event.preventDefault();
							$busca_unidades($id_vehiculo, $no_economico, $marca_vehiculo, $busca_vehiculo);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($marca_vehiculo, $busca_vehiculo);
						
						$no_economico.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($no_economico.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataUnidadByNoEco.json';
									$arreglo2 = {'no_economico':$no_economico.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['Vehiculo'].length) > 0 ){
											$id_vehiculo.val(entry2['Vehiculo'][0]['id']);
											$no_economico.val(entry2['Vehiculo'][0]['numero_economico']);
											$marca_vehiculo.val(entry2['Vehiculo'][0]['marca']);
											$busca_vehiculo.hide();
											//Aplicar solo lectura una vez que se ha escogido la unidad
											$aplicar_readonly_input($marca_vehiculo);
											$no_economico.focus(); 
										}else{
											jAlert('N&uacute;mero econ&oacute;mico desconocido.', 'Atencion!', function(r) {
												$no_economico.val('');
												$no_economico.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}else{
								if (parseInt(e.which) == 8) {
									//Si se oprime la tecla borrar se vacía el campo no_economico 
									if(parseInt(valor.length)>0 && parseInt($id_vehiculo.val())>0){
										jConfirm('Seguro que desea cambiar la Unidad seleccionada?', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												$id_vehiculo.val(0);
												$no_economico.val('');
												$marca_vehiculo.val('');
												$busca_vehiculo.show();
												//Quitar solo lectura una vez que se ha borrado la unidad
												$quitar_readonly_input($marca_vehiculo);
												$no_economico.focus();
											}else{
												$no_economico.val(valor);
												$no_economico.focus();
											}
										});
									}else{
										$no_economico.focus();
									}
								}
							}
						});
						
						
						
						//Buscador de Operadores
						$busca_operador.click(function(event){
							event.preventDefault();
							$busca_operadores($no_operador, $nombre_operador);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($nombre_operador, $busca_operador);
						
						$no_operador.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($no_operador.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataOperadorByNo.json';
									$arreglo2 = {'no_operador':$no_operador.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['Operador'].length) > 0 ){
											$no_operador.val(entry2['Operador'][0]['clave']);
											$nombre_operador.val(entry2['Operador'][0]['nombre']);
										}else{
											jAlert('N&uacute;mero de Operador desconocido.', 'Atencion!', function(r) {
												$no_operador.val('');
												$no_operador.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}
						});
						
						
						
						//Buscador de Agentes Aduanales
						$busca_agena.click(function(event){
							event.preventDefault();
							$busca_agentes_aduanales($agena_id, $agena_no, $agena_nombre, $busca_agena);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($agena_nombre, $busca_agena);
						
						$agena_no.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($agena_no.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoAgen.json';
									$arreglo2 = {'no_control':$agena_no.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['AgenA'].length) > 0 ){
											$agena_id.val(entry2['AgenA'][0]['id']);
											$agena_no.val(entry2['AgenA'][0]['folio']);
											$agena_nombre.val(entry2['AgenA'][0]['razon_social']);
											$busca_agena.hide();
											
											//Aplicar solo lectura una vez que se ha escogido un agente aduanal
											$aplicar_readonly_input($agena_nombre);
										}else{
											jAlert('N&uacute;mero de Agente Aduanal desconocido.', 'Atencion!', function(r) { 
												$agena_no.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}else{
								if (parseInt(e.which) == 8) {
									//Si se oprime la tecla borrar se vacía el campo agena_no 
									if(parseInt(valor.length)>0 && parseInt($agena_id.val())>0){
										jConfirm('Seguro que desea cambiar el Agente Aduanal seleccionado?', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												$agena_id.val(0);
												$agena_no.val('');
												$agena_nombre.val('');
												$busca_agena.show();
												
												//Quitar solo lectura una vez que se ha eliminado datos del Agente Aduanal
												$quitar_readonly_input($agena_nombre);
												
												$agena_no.focus();
											}else{
												$agena_no.val(valor);
												$agena_no.focus();
											}
										});
									}else{
										$agena_no.focus();
									}
								}
							}
						});

						
						
						
						//Buscador de Remitentes
						$busca_remitente.click(function(event){
							event.preventDefault();
							$busca_remitentes($rem_id, $rem_nombre, $rem_no, $rem_dir, $id_cliente, $busca_remitente);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($rem_nombre, $busca_remitente);
							
						$rem_no.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($rem_no.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoRemitente.json';
									$arreglo2 = {'no_control':$rem_no.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['Remitente'].length) > 0 ){
											var rem_id = entry2['Remitente'][0]['id'];
											var rem_numero = entry2['Remitente'][0]['folio'];
											var rem_nombre = entry2['Remitente'][0]['razon_social'];
											var rem_dir = entry2['Remitente'][0]['dir'];
											$agregar_datos_remitente($rem_id, $rem_nombre, $rem_no, $rem_dir, $busca_remitente, rem_id, rem_nombre, rem_numero, rem_dir);
										}else{
											jAlert('N&uacute;mero de Remitente desconocido.', 'Atencion!', function(r) { 
												$rem_no.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}else{
								if (parseInt(e.which) == 8) {
									//Si se oprime la tecla borrar se vacía el campo agena_no 
									if(parseInt(valor.length)>0 && parseInt($rem_id.val())>0){
										jConfirm('Seguro que desea cambiar el Remitente seleccionado?', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												$rem_id.val(0);
												$rem_no.val('');
												$rem_nombre.val('');
												$rem_dir.val('');
												
												//Quitar solo lectura una vez que se ha eliminado datos del Remitente
												$quitar_readonly_input($rem_nombre);
												
												//Mostrar link busca remitente
												$busca_remitente.show();
												
												$rem_no.focus();
											}else{
												$rem_no.val(valor);
												$rem_no.focus();
											}
										});
									}else{
										$rem_no.focus();
									}
								}
							}
						});
						
						
						
						
						//Buscador de Destinatarios
						$busca_dest.click(function(event){
							event.preventDefault();
							$busca_destinatarios($dest_id, $dest_nombre, $dest_no, $dest_dir, $id_cliente, $busca_dest);
						});
						
						$(this).aplicarEventoKeypressEjecutaTrigger($dest_nombre, $busca_dest);
						
						$dest_no.keypress(function(e){
							var valor=$(this).val();
							if(e.which == 13){
								if($dest_no.val().trim()!=''){
									var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoDestinatario.json';
									$arreglo2 = {'no_control':$dest_no.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
									$.post(input_json2,$arreglo2,function(entry2){
										if(parseInt(entry2['Dest'].length) > 0 ){
											var dest_id = entry2['Dest'][0]['id'];
											var dest_numero = entry2['Dest'][0]['folio'];
											var dest_nombre = entry2['Dest'][0]['razon_social'];
											var dest_dir = entry2['Dest'][0]['dir'];
																	
											$agregar_datos_remitente($dest_id, $dest_nombre, $dest_no, $dest_dir, $busca_dest, dest_id, dest_nombre, dest_numero, dest_dir);
										}else{
											jAlert('N&uacute;mero de Destinatario desconocido.', 'Atencion!', function(r) { 
												$dest_no.focus(); 
											});
										}
									},"json");//termina llamada json
								}
								return false;
							}else{
								if (parseInt(e.which) == 8) {
									//Si se oprime la tecla borrar se vacía el campo agena_no 
									if(parseInt(valor.length)>0 && parseInt($dest_id.val())>0){
										jConfirm('Seguro que desea cambiar el Destinatario seleccionado?', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												$dest_id.val(0);
												$dest_no.val('');
												$dest_nombre.val('');
												$dest_dir.val('');
												
												//Quitar solo lectura una vez que se ha eliminado datos del Remitente
												$quitar_readonly_input($dest_nombre);
												
												//Mostrar link busca remitente
												$busca_dest.show();
												
												$dest_no.focus();
											}else{
												$dest_no.val(valor);
												$dest_no.focus();
											}
										});
									}else{
										$dest_no.focus();
									}
								}
							}
						});
					}//Termina datos para transportista
					
					
					
					
					//si es refacturacion, no se puede cambiar los datos del grid, solo el header de la factura
					if(entry['datosPedido']['0']['cancelado']=="true"){
						$cancelar_pedido.hide();
						$submit_actualizar.hide();
						$busca_sku.hide();
						$agregar_producto.hide();
						$cancelado.show();
						$folio.attr('disabled','-1'); //deshabilitar
						$check_ruta.attr('disabled','-1'); //deshabilitar
						$check_enviar_obser.attr('disabled','-1'); //deshabilitar
						$check_descto.attr('disabled','-1'); //deshabilitar
						$sku_producto.attr('disabled','-1'); //deshabilitar
						$nombre_producto.attr('disabled','-1'); //deshabilitar
						$nocliente.attr('disabled','-1'); //deshabilitar
						$razon_cliente.attr('disabled','-1'); //deshabilitar
						$dir_cliente.attr('disabled','-1'); //deshabilitar
						$observaciones.attr('disabled','-1'); //deshabilitar
						$tipo_cambio.attr('disabled','-1'); //deshabilitar
						$orden_compra.attr('disabled','-1'); //deshabilitar
						$transporte.attr('disabled','-1'); //deshabilitar
						$lugar_entrega.attr('disabled','-1'); //deshabilitar
						$fecha_compromiso.attr('disabled','-1'); //deshabilitar
						$select_moneda.attr('disabled','-1'); //deshabilitar
						$select_condiciones.attr('disabled','-1'); //deshabilitar
						$select_vendedor.attr('disabled','-1'); //deshabilitar
						
						$grid_productos.find('a[href*=elimina_producto]').hide();
						$grid_productos.find('input').attr('disabled','-1'); //deshabilitar todos los campos input del grid
						$subtotal.attr('disabled','-1'); //deshabilitar
						$impuesto.attr('disabled','-1'); //deshabilitar
						$campo_impuesto_retenido.attr('disabled','-1'); //deshabilitar
						$total.attr('disabled','-1'); //deshabilitar
						
						$('#forma-pocpedidos-window').find('#tabx-2').find('input').attr('disabled','-1'); //deshabilitar
						$('#forma-pocpedidos-window').find('#tabx-2').find('select').attr('disabled','-1'); //deshabilitar
						$('#forma-pocpedidos-window').find('#tabx-2').find('textarea').attr('disabled','-1'); //deshabilitar
						$('#forma-pocpedidos-window').find('#tabx-2').find('a').hide();
					}
					
					
					
					//proceso_flujo_id=4 :Pedido, diferente de 4 ya esta en otro estado del proceso
					if(parseInt(entry['datosPedido']['0']['proceso_flujo_id'])!=4){
						$cancelar_pedido.hide();
						$submit_actualizar.hide();
						$busca_sku.hide();
						$agregar_producto.hide();
						$check_enviar_obser.attr('disabled','-1'); //deshabilitar
						$check_ruta.attr('disabled','-1'); //deshabilitar
						$check_descto.attr('disabled','-1'); //deshabilitar
						$sku_producto.attr('disabled','-1'); //deshabilitar
						$nombre_producto.attr('disabled','-1'); //deshabilitar
						//$nocliente.attr('disabled','-1'); //deshabilitar
						//$razon_cliente.attr('disabled','-1'); //deshabilitar
						//$dir_cliente.attr('disabled','-1'); //deshabilitar
						$observaciones.attr("readonly", true);
						$tipo_cambio.attr("readonly", true);
						$orden_compra.attr("readonly", true);
						$transporte.attr("readonly", true);
						$lugar_entrega.attr("readonly", true);
						//$fecha_compromiso.attr('disabled','-1'); //deshabilitar
						//$select_moneda.attr('disabled','-1'); //deshabilitar
						//$select_condiciones.attr('disabled','-1'); //deshabilitar
						//$select_vendedor.attr('disabled','-1'); //deshabilitar
						$grid_productos.find('a[href*=elimina_producto]').hide();
						$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
						$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid
						$grid_productos.find('input[name=checkProd]').attr('disabled','-1'); //deshabilitar
						
						$('#forma-pocpedidos-window').find('#tabx-2').find('input').attr("readonly", true);
						$('#forma-pocpedidos-window').find('#tabx-2').find('input').css({'background' : '#F0F0F0'});
						$('#forma-pocpedidos-window').find('#tabx-2').find('textarea').attr("readonly", true);
						$('#forma-pocpedidos-window').find('#tabx-2').find('select').attr('disabled','-1'); //deshabilitar
						$('#forma-pocpedidos-window').find('#tabx-2').find('input[name=check_flete]').attr('disabled','-1'); //deshabilitar
						$('#forma-pocpedidos-window').find('#tabx-2').find('a').hide();
					}else{
						//$fecha_compromiso.val(mostrarFecha());
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
						
						
						//Aplicar tipo de cambio a todos los precios al cambiar valor de tipo de cambio
						$tipo_cambio.blur(function(){
							if($(this).val().trim()==''){
								$(this).val(0);
							}
							
							$(this).val(parseFloat($(this).val()).toFixed(4));
							
							if(parseFloat($(this).val())>=parseFloat($tc_usd_sat.val())){
								$grid_productos.find('tr').each(function (index){
									var $campoCantidad = $(this).find('#cant');
									var $campoPrecioU = $(this).find('input[name=costo]');
									var $campoImporte = $(this).find('#import');
									
									var $campoTasaIeps = $(this).find('#tasaIeps');
									var $importeIeps = $(this).find('#importeIeps');
									
									var $campoTasaIva = $(this).find('#ivalorimp');
									var $importeIva = $(this).find('#totimp');
									
									var $ret_tasa = $(this).find('#ret_tasa');
									var $ret_importe = $(this).find('#ret_importe');
									
									var $vdescto = $(this).find('#vdescto');
									var $pu_con_descto = $(this).find('#pu_descto');
									var $importe_del_descto = $(this).find('#importe_del_descto');
									var $importe_con_descto = $(this).find('#importe_con_descto');
									
									var $campoMonedaPrecioOriginalPartida = $(this).find('input[name=id_mon_pre]');
									var $campoPrecioOriginalPartida = $(this).find('input[name=pre_original]');
									
									if ($campoPrecioU.val().trim() == ''){
										$campoPrecioU.val(' ');
									}else{
										$campoPrecioU.val(parseFloat($campoPrecioU.val()).toFixed(4));
									}
									
									if( ($campoPrecioU.val().trim()!='') && ($campoCantidad.val().trim()!='') ){
										if( parseInt($campoMonedaPrecioOriginalPartida.val()) != parseInt($select_moneda.val()) ){
											if(parseInt($campoMonedaPrecioOriginalPartida.val())==1 && parseInt($select_moneda.val())!=1){
												//Si la moneda original es pesos, calculamos su equivalente a dolares
												precio_cambiado = parseFloat($campoPrecioOriginalPartida.val()) / parseFloat($tipo_cambio.val());
											}
											
											if(parseInt($campoMonedaPrecioOriginalPartida.val())!=1 && parseInt($select_moneda.val())==1){
												//Si la moneda original es dolar, calculamos su equivalente a pesos
												precio_cambiado = parseFloat($campoPrecioOriginalPartida.val()) * parseFloat($tipo_cambio.val());
											}
											
											$campoPrecioU.val(parseFloat(precio_cambiado).toFixed(4));
										}
										
										//Calcula el importe
										$campoImporte.val(parseFloat($campoPrecioU.val()) * parseFloat($campoCantidad.val()));
										//Redondea el importe en dos decimales
										//$(this).parent().parent().find('#import').val(Math.round(parseFloat($(this).parent().parent().find('#import').val())*100)/100);
										$campoImporte.val( parseFloat($campoImporte.val()).toFixed(4));
										
										//Calcular el importe del IEPS
										$importeIeps.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
										
										//Calcula el impuesto para este producto multiplicando el importe por el valor del iva
										$importeIva.val((parseFloat($campoImporte.val()) + parseFloat($importeIeps.val())) * parseFloat($campoTasaIva.val()));
										
										if(parseFloat($ret_tasa.val())>0){
											//Calcular la retencion de la partida
											$ret_importe.val(parseFloat(parseFloat($campoImporte.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
										}
										
										if($pdescto.val().trim()=='true'){
											if(parseFloat($vdescto.val())>0){
												$pu_con_descto.val(parseFloat(parseFloat($campoPrecioU.val()) - (parseFloat($campoPrecioU.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
												$importe_del_descto.val(parseFloat(parseFloat($campoImporte.val()) * (parseFloat($vdescto.val())/100)).toFixed(4));
												$importe_con_descto.val(parseFloat(parseFloat($campoImporte.val()) - parseFloat($importe_del_descto.val())).toFixed(4));
												
												//Calcular y redondear el importe del IEPS, tomando el importe con descuento
												$importeIeps.val(parseFloat(parseFloat($importe_con_descto.val()) * (parseFloat($campoTasaIeps.val())/100)).toFixed(4));
												
												//Calcular el impuesto para este producto multiplicando el importe_con_descto + ieps por la tasa del iva
												$importeIva.val((parseFloat($importe_con_descto.val()) + parseFloat($importeIeps.val())) * parseFloat( $campoTasaIva.val() ));
												
												if(parseFloat($ret_tasa.val())>0){
													//Calcular la retencion de la partida
													$ret_importe.val(parseFloat(parseFloat($importe_con_descto.val()) * parseFloat(parseFloat($ret_tasa.val())/100)).toFixed(4));
												}
											}
										}
									}else{
										$campoImporte.val(0);
										$importeIva.val(0);
									}
									
									//Llamada a la funcion que calcula totales
									$calcula_totales();
								});
							}else{
								jAlert('El TC USD para la conversion de precios no debe ser menor a '+$tc_usd_sat.val()+'.', 'Atencion!', function(r) { 
									$tipo_cambio.focus(); 
								});
							}
						});
					}
					
					
					
					//agregar producto al grid
					$agregar_producto.click(function(event){
						event.preventDefault();
						$buscador_presentaciones_producto($id_cliente, $nocliente.val(), $sku_producto.val(),$nombre_producto,$grid_productos,$select_moneda,$tipo_cambio, entry['Monedas']);
					});
					
					
					
					$check_descto.click(function(event){
						if(this.checked){
							$pdescto.val('true');
							$valor_descto.attr("readonly", false);
							$valor_descto.css({'background' : '#ffffff'});
							$valor_descto.val(parseFloat(0).toFixed(4));
							$recalcular_importes_partidas($grid_productos, $pdescto, $valor_descto);
						}else{
							$pdescto.val('false');
							$valor_descto.attr("readonly", true);
							$valor_descto.val(parseFloat(0).toFixed(4));
							$valor_descto.css({'background' : '#F0F0F0'});
							$recalcular_importes_partidas($grid_productos, $pdescto, $valor_descto);
						}
					});
					
					$valor_descto.focus(function(e){
						if($(this).val().trim()==''){
							$(this).val('');
						}else{
							if(parseFloat($(this).val())<=0){
								$(this).val('');
							}
						}
					});
					
					$valor_descto.blur(function(){
						var $campo_descto = $(this);
						
						if($campo_descto.val().trim()==''){
							$campo_descto.val(0);
						}
						
						$campo_descto.val(parseFloat($campo_descto.val()).toFixed(4));
						
						if(parseFloat($campo_descto.val())<=0){
							if($check_descto.prop("checked")){
								jAlert('Es necesario ingresar el Porcentaje del Descuento', 'Atencion!', function(r) { 
									$recalcular_importes_partidas($grid_productos, $pdescto, $campo_descto);
									$campo_descto.focus();
								});
							}
						}else{
							$recalcular_importes_partidas($grid_productos, $pdescto, $campo_descto);
						}
					});
					
				});//termina llamada json
                
                
                
				//cambiar metodo de pago
				$select_metodo_pago.change(function(){
					var valor_metodo = $(this).val();
					$no_cuenta.val('');
					
					//valor_metodo 2=Tarjeta Credito, 3=Tarjeta Debito
					if(parseInt(valor_metodo)==2 || parseInt(valor_metodo)==3){
						$no_cuenta.val('');
						//si esta desahabilitado, hay que habilitarlo para permitir la captura de los digitos de la tarjeta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						//quitar propiedad de solo lectura
						$no_cuenta.removeAttr('readonly');
						
						//$no_cuenta.attr('readonly',true);
						
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						
						$etiqueta_digit.val('Ingrese los ultimos 4 Digitos de la Tarjeta');
					}
					
					//valor_metodo 4=Cheque Nominativo, 5=Transferencia Electronica de Fondos
					if(parseInt(valor_metodo)==4 || parseInt(valor_metodo)==5){
						$no_cuenta.val('');
						$no_cuenta.show();
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						
						//fijar propiedad de solo lectura en verdadero
						$no_cuenta.attr('readonly',true);
						
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
							$no_cuenta.val($cta_mn.val());
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
							$no_cuenta.val($cta_usd.val());
						}
					}
					
					//valor_metodo 1=Efectivo, 6=No Identificado
					if(parseInt(valor_metodo)==1 || parseInt(valor_metodo)==6){
						$no_cuenta.val('');
						if(!$no_cuenta.is(':disabled')) {
							$no_cuenta.attr('disabled','-1');
						}
						if(!$etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.attr('disabled','-1');
						}
					}
					
					if(parseInt(valor_metodo)==7){
						$no_cuenta.show();
						$no_cuenta.val('NA');
						//si esta desahabilitado, hay que habilitarlo para permitir la captura del Numero de cuenta.
						if($no_cuenta.is(':disabled')) {
							$no_cuenta.removeAttr('disabled');
						}
						if($etiqueta_digit.is(':disabled')) {
							$etiqueta_digit.removeAttr('disabled');
						}
						if(parseInt($select_moneda.val())==1){
							$etiqueta_digit.val('Numero de Cuenta para pago en Pesos');
						}else{
							$etiqueta_digit.val('Numero de Cuenta en Dolares');
						}
					}
					
				});
				
                
                
				$tipo_cambio.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}		
				});

				
				//buscador de clientes
				$busca_cliente.click(function(event){
					event.preventDefault();
					$busca_clientes();
				});
				
				
				//buscador de productos
				$busca_sku.click(function(event){
					event.preventDefault();
					$busca_productos($sku_producto.val(), $nombre_producto.val());
				});
				
				
				
				//ejecutar clic del href Agregar producto al pulsar enter en el campo sku del producto
				$sku_producto.keypress(function(e){
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
				});
				
				//desencadena clic del href Buscar Producto al pulsar enter en el campo Nombre del producto
				$nombre_producto.keypress(function(e){
					if(e.which == 13){
						$busca_sku.trigger('click');
						return false;
					}
				});
						
				
				$cancelar_pedido.click(function(e){
					$accion_proceso.attr({'value' : "cancelar"});
					jConfirm('Desea Cancelar el Pedido?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.parents("FORM").submit();
						}else{
							$accion_proceso.attr({'value' : "edit"});
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				//click generar reporte de pedidos 
				$descargarpdf.click(function(event){
					event.preventDefault();
					var id_pedido = $id_pedido.val();
					if($id_pedido.val() != 0 ){
						var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
						var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_pedido/'+id_pedido+'/'+iu+'/out.json';
						window.location.href=input_json;

					}else{
						jAlert("No se esta enviando el identificador  del pedido","Atencion!!!")
					}
				 });
                
                
				$btn_autorizar.click(function(event){
					//LLamada a la funcion de la ventana de autorizacion
					$forma_autorizacion($grid_productos, $btn_autorizar, id_to_show);
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
						jAlert('No hay datos para actualizar', 'Atencion!', function(r) { 
							$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
						});
						return false;
					}
				});
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-pocpedidos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-pocpedidos-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllPedidos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllPedidos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formapocpedidos00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



