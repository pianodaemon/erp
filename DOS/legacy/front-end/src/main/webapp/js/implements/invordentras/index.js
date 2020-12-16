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
	var controller = $contextpath.val()+"/controllers/invordentras";
    
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
	$('#barra_titulo').find('#td_titulo').append(document.title);
	
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
		
		$get_datos_grid();
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
		$busqueda_folio.focus();
	});
	
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_descripcion, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_alm_origen, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_alm_destino, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	
	
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
            var $select_prod_tipo = $('#forma-invordentras-window').find('select[name=prodtipo]');
            $('#forma-invordentras-window').find('#submit').mouseover(function(){
                $('#forma-invordentras-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                //$('#forma-invordentras-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
            })
            $('#forma-invordentras-window').find('#submit').mouseout(function(){
                $('#forma-invordentras-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                //$('#forma-invordentras-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
            })
            $('#forma-invordentras-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-invordentras-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-invordentras-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-invordentras-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            
            $('#forma-invordentras-window').find('#close').mouseover(function(){
                $('#forma-invordentras-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-invordentras-window').find('#close').mouseout(function(){
                $('#forma-invordentras-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
            
            $('#forma-invordentras-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-invordentras-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-invordentras-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-invordentras-window').find("ul.pestanas li").click(function() {
                $('#forma-invordentras-window').find(".contenidoPes").hide();
                $('#forma-invordentras-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-invordentras-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	


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
			$(this).val(parseFloat($(this).val()).toFixed(4));
		});
	}
	
	
	$aplicar_evento_focus_input_lote = function( $campo_input ){
		//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$campo_input.focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
	}
	
	$realiza_calculo_kilos = function($this_tr, $campo_input){
            
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
                            $this_tr.find('input[name=cantidad_kilos]').val(parseFloat(kilos).toFixed(4));
                        }else{
                            $this_tr.find('input[name=cantidad_kilos]').val(0);
                        }
                    }
                }
            }
        }
        
        //funcion para aplicar la conversion de litros a kilos, de acuerdo a la densidad
	$aplicar_evento_kilos_densidad = function( $campo_input, tipo){
            //tipo almacene, si se envio, para realizar el evento blur, o para solo hacer el calculo
            $this_tr = $campo_input.parent().parent();
            if(tipo == "blur"){
                $campo_input.blur(function(e){
                    $realiza_calculo_kilos($this_tr, $campo_input);
                });
            }else{
                $realiza_calculo_kilos($this_tr, $campo_input);
            }
	}
        
        
	$aplicar_evento_blur_input_lote = function( $campo_input ){
		//pone espacio en blanco al perder el enfoque, cuando no se ingresa un valor
		$campo_input.blur(function(e){
			if ( $(this).val() == ''  || $(this).val() == null ){
				$(this).val(' ');
			}else{
				//aqui va llamada a funcion que busca datos del lote
				//var $tr_padre = $(this).parent().parent();
				//$obtiene_datos_lote($tr_padre);
			}
		});
	}
	
	
	$aplicar_evento_click_input_lote = function( $campo_input ){
		//validar campo cantidad recibida, solo acepte numeros y punto
		$campo_input.dblclick(function(e){
			$(this).select();
		});
	}
	
	
	$aplicar_evento_keypress_input_lote = function( $campo_input ){
		//validar campo cantidad recibida, solo acepte numeros y punto
		$campo_input.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if(e.which==13 ) {
				if ( $(this).val()!=''  &&  $(this).val()!=' ' && $(this).val()!=null ){
					var $tr_padre = $(this).parent().parent();
					$obtiene_datos_lote($tr_padre);
				}else{
					jAlert("Ingresa un n&uacute;mero de Lote.", 'Atencion!');
				}
				return false;
			}
		});
	}
	
	
	
	
	
	//buscador de de Datos del Lote
	$obtiene_datos_lote = function($tr_padre){
		var numero_lote = $tr_padre.find('input[name=lote_int]').val();
		var id_producto = $tr_padre.find('input[name=idproducto]').val();
		var $select_alm_origen = $('#forma-invordentras-window').find('select[name=select_alm_origen]');
		var encontrado=0;
		$tabla_padre = $tr_padre.parent();
		
		//buscar el numero de lote en la tabla
		$tabla_padre.find('input[name=lote_int]').each(function (index){
			if($(this).val() == numero_lote ){
				encontrado++;
			}
		});
		
		//si el numero de lote solo esta una vez es valido, dos veces ya no es valido
		if(parseInt(encontrado)<=1){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosLote.json';
			$arreglo = {'no_lote':numero_lote,
						'id_producto':id_producto,
						'id_almacen':$select_alm_origen.val(),
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			
			$.post(input_json,$arreglo,function(entry){
				//verifica si el arreglo  retorno datos
				if (entry['Lote'].length > 0){
					
					//crea el tr con los datos del producto seleccionado
					$.each(entry['Lote'],function(entryIndex,lote){
						//$tr_padre.find('input[name=lote_int]').val(lote['']);
						$tr_padre.find('input[name=cant_traspaso]').val(lote['exis_lote']);
					});//termina llamada json
					
				}else{
					jAlert("El n&uacute;mero de Lote no existe para &eacute;ste producto en el Almacen Origen.", 'Atencion!');
					
					$tr_padre.find('input[name=lote_int]').select();
				}
			});
		}else{
			jAlert("El n&uacute;mero de Lote  [ "+numero_lote+" ]  ya se encuentra en la lista.", 'Atencion!');
		}
		
	}//termina buscador de datos del Lote
	
	
	
	
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
	$convertirUnidadesAPresentaciones = function($cantUni, $campoCantPres, $equivPres, noDec){
		$cantUni.blur(function(e){
			if ($(this).val().trim() != ''){
				$campoCantPres.val(parseFloat($cantUni.val()) / parseFloat($equivPres.val()));
			}else{
				$campoCantPres.val('0.0000');
			}
			$campoCantPres.val(parseFloat($campoCantPres.val()).toFixed(noDec));
		});
	};
	
	
	
	//generar tr para lote	  (noTr, tipo_tr, idPartida, id_producto, lote_int, codigo, descripcion, cant_lote,     readOnly,           idPres,cantPresLote,cantEquiv)
	$genera_tr_lote = function(noTr, tipo_tr, idPartida, id_producto, lote_int, codigo, descripcion, cant_traspaso, readOnly, densidad, idPres,cantPres, cantEquiv){
		var trr = '';
		trr = '<tr>';
			trr += '<td class="grid1" style="font-size:11px; border:1px solid #C1DAD7;" width="120">';
				trr += '<input type="hidden" 	name="tipotr" value="'+ tipo_tr +'">';
				trr += '<input type="hidden" 	name="idPartida" value="'+ idPartida +'">';
				trr += '<input type="hidden" 	name="idproducto" id="idprod" value="'+ id_producto +'">';
				trr += '<input type="text" 		name="codigo" value="'+ codigo +'" style="width:116px; display:none;">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="200">';
				trr += '<input type="text" 		name="nombre" 	value="'+ descripcion +'" style="width:296px; display:none;">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
				trr += '<input type="text" 		value="Lote" class="borde_oculto" readOnly="true" style="width:116px; text-align:right;">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
				trr += '<input type="text" 		name="lote_int" class="lote_int'+ noTr +'" value="'+lote_int+'" title="Ingresar Lote" style="width:116px;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
				trr += '<input type="text" 		name="cant_traspaso" value="'+cant_traspaso+'" class="cant_traspaso'+noTr+'" '+readOnly+' style="width:86px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
				trr += '<input type="hidden" 	name="idPres" id="idPres" value="'+ idPres +'">';
				trr += '<input type="hidden" 	name="cantEquiv" id="cantEquiv'+noTr+'" value="'+ cantEquiv +'">';
				trr += '<input type="text" 		name="cantPres" id="cantPres'+noTr+'" value="'+cantPres+'" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size:14px; font-weight:bold; border:1px solid #C1DAD7;" width="15">';
				trr += '<a href="agrega_lote" class="agrega_lote'+ noTr +'" title="Agregar Lote">  +  </a>';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size: 14px; font-weight:bold; border:1px solid #C1DAD7;" width="15">';
				trr += '<input type="hidden" 	name="eliminado" value="1">';//el 1 significa que el registro no ha sido eliminado
				trr += '<input type="hidden" 	name="no_tr" value="'+ noTr +'">';
				trr += '<a href="elimina_lote" class="elimina_lote'+ noTr +'" title="Eliminar Lote"> - </a>';
			trr += '</td>';
			//agregado por paco
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<input name="densidad_litro" value="'+densidad+'" type="hidden">';
					//trr += '<input name="cantidad_kilos" id="cantidad_kilos'+ noTr +'" value="0.00" class="borde_oculto" readonly="true" style="width:56px; text-align:right;" type="text">';
			trr += '</td>';
		trr += '</tr>';
		
		return trr;
	}
	
	
	

	
	
	//generar tr para agregar al grid
	$genera_tr_partida = function(noTr, tipo_tr, idPartida, id_producto, codigo, descripcion, unidad, cant_traspaso, readOnly, densidad, idPres, presentacion, cantPres, noDec, cantEquiv){
		var trr = '';
		trr = '<tr>';
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
				trr += '<input type="hidden" 	name="tipotr" value="'+ tipo_tr +'">';
				trr += '<input type="hidden" 	name="idPartida" value="'+ idPartida +'">';
				trr += '<input type="hidden" 	name="idproducto" id="idprod" value="'+ id_producto +'">';
				trr += '<input type="text" 		name="codigo" value="'+ codigo +'" class="borde_oculto" readOnly="true" style="width:116px;">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200">';
				trr += '<input type="text" 		name="nombre" 	value="'+ descripcion +'" class="borde_oculto" readOnly="true" style="width:196px;">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
				trr += '<input type="text" 		name="unidad" 	value="'+ unidad +'" class="borde_oculto" readOnly="true" style="width:116px;">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="120">';
				trr += '<input type="text" 		name="unidad" 	value="'+ presentacion +'" class="borde_oculto" readOnly="true" style="width:116px;">';
				trr += '<input type="text" 		name="lote_int" class="lote_int'+ noTr +'" value="" style="width:116px; display:none;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
				trr += '<input type="text" 		name="cantidad" class="cantidad'+noTr+'"  value="'+$(this).agregar_comas(cant_traspaso)+'" '+readOnly+' style="width:86px; text-align:right; border-color:transparent; background:transparent;">';
				trr += '<input type="text" 		name="cant_traspaso" value="'+cant_traspaso+'" class="cant_traspaso'+noTr+'"  style="width:86px; display:none;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
				trr += '<input type="hidden" 	name="idPres" id="idPres" value="'+ idPres +'">';
				trr += '<input type="hidden" 	name="cantEquiv" id="cantEquiv" value="'+ cantEquiv +'">';
				trr += '<input type="text" 		name="cantPres" class="cantPres'+noTr+'"  value="'+$(this).agregar_comas(cantPres)+'" '+readOnly+' style="width:86px; text-align:right; border-color:transparent; background:transparent;">';
			trr += '</td>';
			
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="15">';
			trr += '</td>';
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="15">';
				trr += '<input type="hidden" 	name="eliminado" value="1">';//el 1 significa que el registro no ha sido eliminado
				trr += '<input type="hidden" 	name="no_tr" value="'+ noTr +'">';
			trr += '</td>';
			//agregado por paco
			trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<input name="densidad_litro" value="'+densidad+'" type="hidden">';
					trr += '<input name="cantidad_kilos" id="cantidad_kilos'+ noTr +'" value="0.00" class="borde_oculto" readonly="true" style="width:56px; text-align:right;" type="text">';
			trr += '</td>';
		trr += '</tr>';
		
		return trr;
	}
	
	
	
	
	
	//funcion para agregar lote y eliminar
	agregar_lote_y_eliminar = function($grid_productos, tipo_tr, trCount, noDec, cantEquiv, densidad){
		//agregar nuevo lote
		$grid_productos.find('.agrega_lote'+ trCount ).click(function(e){
			e.preventDefault();
			$(this).hide();
			
			$tr_padre=$(this).parent().parent();
			
			var id_producto = $tr_padre.find('input[name=idproducto]').val();
			var codigo = $tr_padre.find('input[name=sku]').val();
			var descripcion = $tr_padre.find('input[name=nombre]').val();
			var idPartida = $tr_padre.find('input[name=idPartida]').val();
			var lote_int = ' ';
			var cant_lote='0.0000';
			var idPres = $tr_padre.find('input[name=idPres]').val();
			var cantPresLote='0.0000';
			
			var readOnly='';
			
			var noTr = $("tr", $grid_productos).size();
			noTr++;
			//alert(cantEquiv);
			//alert(noDec);
			
			//aqui es para crear nuevos registros del lote
			tr_lote = $genera_tr_lote(noTr, tipo_tr, idPartida, id_producto, lote_int, codigo, descripcion, cant_lote, readOnly, densidad, idPres, cantPresLote, cantEquiv);
			//agregar tr_lote despues de tr_padre
			$(tr_lote).insertAfter($tr_padre);
			
			//aplicar click al nuevo registro
			//se hace una llamada recursiva a  la funcion agregar_lote
			agregar_lote_y_eliminar($grid_productos,tipo_tr, noTr, noDec, cantEquiv, densidad);
			$aplicar_evento_keypress( $grid_productos.find('.cant_traspaso'+ noTr ) );
			$aplicar_evento_blur( $grid_productos.find('.cant_traspaso'+ noTr ) );
			$aplicar_evento_focus( $grid_productos.find('.cant_traspaso'+ noTr ) );
			
			$aplicar_evento_focus_input_lote($grid_productos.find('.lote_int'+ noTr ));
			$aplicar_evento_blur_input_lote($grid_productos.find('.lote_int'+ noTr ));
			$aplicar_evento_keypress_input_lote($grid_productos.find('.lote_int'+ noTr ));
			$aplicar_evento_click_input_lote($grid_productos.find('.lote_int'+ noTr ));
			
			$convertirUnidadesAPresentaciones($grid_productos.find('.cant_traspaso'+ noTr), $grid_productos.find('#cantPres'+ noTr), $grid_productos.find('#cantEquiv'+ noTr), noDec);
		});
		
		
		//eliminar un lote
		$grid_productos.find('.elimina_lote'+ trCount ).click(function(e){
			e.preventDefault();
			$tr_padre=$(this).parent().parent();
			//var tipo_registro = $tr_padre.find('input[name=tipotr]').val();//tomar el el tipo de registro
			//$tr_padre.find('input').val('');//asignar vacio a todos los input del tr
			//$tr_padre.find('input[name=eliminado]').val('0');//asignamos 0 para indicar que se ha eliminado
			//$tr_padre.find('input[name=tipotr]').val(tipo_registro);
			$tr_padre.remove();//eliminar el tr
		});
	}
	
	
	
	
	
	var carga_formainvordentras00_for_datagrid00 = function(id_to_show, accion_mode){
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
			$('#forma-invordentras-window').remove();
			$('#forma-invordentras-overlay').remove();
            
			var form_to_show = 'formainvordentras00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_invordentras();
			
			$('#forma-invordentras-window').css({"margin-left": -340, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-invordentras-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getOrdenTraspaso.json';
			$arreglo = {'identificador':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			
			
			var $identificador = $('#forma-invordentras-window').find('input[name=identificador]');
			var $folio = $('#forma-invordentras-window').find('input[name=folio]');
			var $fecha_traspaso = $('#forma-invordentras-window').find('input[name=fecha_traspaso]');
			
			var $select_suc_origen = $('#forma-invordentras-window').find('select[name=select_suc_origen]');
			var $select_alm_origen = $('#forma-invordentras-window').find('select[name=select_alm_origen]');
			
			var $select_suc_destino = $('#forma-invordentras-window').find('select[name=select_suc_destino]');
			var $select_alm_destino = $('#forma-invordentras-window').find('select[name=select_alm_destino]');
			
			var $observaciones = $('#forma-invordentras-window').find('textarea[name=observaciones]');
			
			var $descargarpdf = $('#forma-invordentras-window').find('#descargarpdf');
			
			//grid de productos
			var $grid_productos = $('#forma-invordentras-window').find('#grid_productos');
			//grid de errores
			var $grid_warning = $('#forma-invordentras-window').find('#div_warning_grid').find('#grid_warning');
			
			
			var $cerrar_plugin = $('#forma-invordentras-window').find('#close');
			var $cancelar_plugin = $('#forma-invordentras-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invordentras-window').find('#submit');
			
			//$campo_factura.css({'background' : '#ffffff'});
			
			//ocultar boton de facturar y descargar pdf. Solo debe estar activo en editar
			//$boton_descargarpdf.hide();
			$identificador.val(0);//para nueva pedido el id es 0
			$fecha_traspaso.val(mostrarFecha());
			
			
			$descargarpdf.attr('disabled','-1');
			//$etiqueta_digit.attr('disabled','-1');
			//$submit_actualizar.hide();
			var respuestaProcesada = function(data){
				if ( data['success'] == "true" ){
					jAlert("La Orden de Traspaso se guard&oacute; con &eacute;xito", 'Atencion!');
					var remove = function() {$(this).remove();};
					$('#forma-invordentras-overlay').fadeOut(remove);
					$get_datos_grid();
				}else{
					//habilitar boton actualizar
					$submit_actualizar.removeAttr('disabled');
					// Desaparece todas las interrogaciones si es que existen
					$('#forma-invordentras-window').find('.invordentras_div_one').css({'height':'550px'});//con errores
					$('#forma-invordentras-window').find('div.interrogacion').css({'display':'none'});
					$grid_productos.find('input[name=cant_traspaso]').css({'background' : '#ffffff'});
					$grid_productos.find('input[name=cantidad]').css({'background' : '#ffffff'});
					$grid_productos.find('input[name=lote_int]').css({'background' : '#ffffff'});
					
					$('#forma-invordentras-window').find('#div_warning_grid').css({'display':'none'});
					$('#forma-invordentras-window').find('#div_warning_grid').find('#grid_warning').children().remove();
					
					var valor = data['success'].split('___');
					//muestra las interrogaciones
					for (var element in valor){
						tmp = data['success'].split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
							$('#forma-invordentras-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							
							var campo = tmp.split(':')[0];
							
							$('#forma-invordentras-window').find('#div_warning_grid').css({'display':'block'});
							var $campo = $grid_productos.find('.'+campo);
							$campo.css({'background' : '#d41000'});
							
							var codigo_producto = $campo.parent().parent().find('input[name=codigo]').val();
							var titulo_producto = $campo.parent().parent().find('input[name=nombre]').val();
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="120"><input type="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:120px; color:red"></td>';
									tr_warning += '<td width="200"><input type="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:200px; color:red"></td>';
									tr_warning += '<td width="470"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:470px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-invordentras-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
							
						}
					}
					$('#forma-invordentras-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
					$('#forma-invordentras-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
				}
			
			}		
				var options = {datatype :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.val(entry['Datos']['0']['id']);
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
					
					
					//$grid_productos.find('a').hide();
					$grid_productos.find('input').keypress(function(e){
						if(e.which==13 ) {
							return false;
						}
					});
					
					if (entry['DatosGrid'].length > 0){
						$.each(entry['DatosGrid'],function(entryIndex,prodGrid){
							var noTr = $("tr", $grid_productos).size();
							noTr++;
							var tr_lote='';
							var tipo_tr='PARTIDA';
							var id_partida =  prodGrid['id_partida'];
							var id_producto = prodGrid['id_producto'];
							var codigo = prodGrid['codigo'];
							var descripcion = prodGrid['descripcion'];
							var unidad = prodGrid['unidad'];
							var cant_traspaso=prodGrid['cant_traspaso'];
							var densidad=prodGrid['densidad'];
							
							var idPres = prodGrid['idPres'];
							var presentacion = prodGrid['presentacion'];
							var cantPres=prodGrid['cant_pres'];
							var noDec=prodGrid['no_dec'];
							var cantEquiv=prodGrid['cantEquiv'];
							
							var readOnly='readOnly="true"';
							var lote_int='';
							var cantPresLote=0;
							
							var cadena_tr = $genera_tr_partida(noTr, tipo_tr, id_partida, id_producto, codigo, descripcion, unidad, cant_traspaso, readOnly, densidad,idPres, presentacion, cantPres, noDec, cantEquiv);
							$grid_productos.append(cadena_tr);
							$aplicar_evento_kilos_densidad($grid_productos.find('.cant_traspaso'+noTr), "");
                                                        
							if(parseInt(entry['Datos']['0']['estatus'])==0){
								trCount2 = $("tr", $grid_productos).size();//obtener de nuevo el numero de trs de la tabla
								trCount2++;
								tipo_tr='LOTE';
								cant_traspaso='0.0000';
								cantPresLote='0.0000';
								
								readOnly='';
								tr_lote = $genera_tr_lote(trCount2, tipo_tr, id_partida, id_producto, lote_int, codigo, descripcion, cant_traspaso, readOnly, densidad, idPres, cantPresLote, cantEquiv);
								$grid_productos.append(tr_lote);
								
								
								// esta funcion es para agregar un nuevo lote
								//en esta funcion se le aplica evento click a los href Agregar Lote y Eliminar
								agregar_lote_y_eliminar($grid_productos,tipo_tr, trCount2, noDec, cantEquiv, densidad);
								$aplicar_evento_keypress( $grid_productos.find('.cant_traspaso'+ trCount2 ) );
								$aplicar_evento_blur( $grid_productos.find('.cant_traspaso'+ trCount2 ) );
								$aplicar_evento_focus( $grid_productos.find('.cant_traspaso'+ trCount2 ) );
								
								//$aplicar_evento_kilos_densidad($grid_productos.find('.cant_traspaso'+trCount2), "blur");
								$aplicar_evento_focus_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
								$aplicar_evento_blur_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
								$aplicar_evento_keypress_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
								$aplicar_evento_click_input_lote($grid_productos.find('.lote_int'+ trCount2 ));
								
								$convertirUnidadesAPresentaciones($grid_productos.find('.cant_traspaso'+ trCount2), $grid_productos.find('#cantPres'+ trCount2), $grid_productos.find('#cantEquiv'+ trCount2), noDec);
							}else{
								
								$.each(entry['GridLotes'],function(entryIndex,lote){
									trCount2 = $("tr", $grid_productos).size();//obtener de nuevo el numero de trs de la tabla
									
									if( parseInt(prodGrid['id_partida']) == parseInt(lote['id_partida']) ){
										trCount2++;
										tipo_tr='LOTE';
										lote_int=lote['lote_int']
										cant_traspaso=lote['cant_traspaso'];
										cantPresLote=lote['cant_pres'];
										
										readOnly='readOnly="true"';
										tr_lote = $genera_tr_lote(trCount2, tipo_tr, id_partida, id_producto, lote_int, codigo, descripcion, cant_traspaso, readOnly, densidad, idPres, cantPresLote, cantEquiv);
										$grid_productos.append(tr_lote);
										
										//$convertirUnidadesAPresentaciones($grid_productos.find('.cant_traspaso'+ trCount2).val(), $grid_productos.find('#cantPres'+ trCount2), $grid_productos.find('#cantEquiv'+ trCount2).val());
										
									}
								});
								
								$descargarpdf.removeAttr('disabled');
								$grid_productos.find('a').hide();
								$grid_productos.find('input').keypress(function(e){
									if(e.which==13 ) {
										return false;
									}
								});
								
								$observaciones.attr({ 'readOnly':true });
								$grid_productos.find('input').attr({ 'readOnly':true });
								$submit_actualizar.hide();
							}
						});
					}
					
					if(entry['Datos']['0']['cancelado']=='true'){
						$folio.attr('disabled','-1');
						$fecha_traspaso.attr('disabled','-1');
						$observaciones.attr('disabled','-1');
						$select_suc_origen.attr('disabled','-1');
						$select_alm_origen.attr('disabled','-1');
						$select_suc_destino.attr('disabled','-1');
						$select_alm_destino.attr('disabled','-1');
						$descargarpdf.attr('disabled','-1');
						$grid_productos.find('input').attr('disabled','-1');
						$grid_productos.find('a').hide();
					}
					
				});//termina llamada json
                
                
                
				//descargar pdf de Ajustes
				$descargarpdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfOrdenTraspaso/'+$identificador.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
				
				
				
                
				//deshabilitar tecla enter  en todo el plugin
				$('#forma-invordentras-window').find('input').keypress(function(e){
					if(e.which==13 ) {
						return false;
					}
				});
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invordentras-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invordentras-overlay').fadeOut(remove);
				});
				
			}
		}
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllOrdenTraspasos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllOrdenTraspasos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvordentras00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
});



