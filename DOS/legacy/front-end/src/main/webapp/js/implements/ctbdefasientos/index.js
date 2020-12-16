$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
		work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	var ArraySuc;
	var ArrayTP;
	var arrayCampos;
	var CtaMay;
	var array_fecha = {1:"Documento"};
	var array_polnum = {1:"Cosecutivo del sistema"};
	var parametros;
	
	//------------------------------------------------------------------
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
	//------------------------------------------------------------------
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	//Carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, elemento_seleccionado, mostrar_opciones){
		$campo_select.children().remove();
		var select_html = '';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				if (mostrar_opciones=='true'){
					select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
				}
			}
		}
		$campo_select.append(select_html);
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
	
	
	//Valida que la cantidad ingresada no tenga mas de un punto decimal
	$validar_numero_puntos = function($campo, campo_nombre){
		//Buscar cuantos puntos tiene  Precio Unitario
		var coincidencias = $campo.val().match(/\./g);
		var numPuntos = coincidencias ? coincidencias.length : 0;
		if(parseInt(numPuntos)>1){
			jAlert('El valor ingresado para el campo '+campo_nombre+' es incorrecto, tiene mas de un punto('+$campo.val()+').', 'Atencion!', function(r) { 
				$campo.focus();
			});
		}
	}
	
	
	$aplica_evento_focus_input_numerico = function($campo){
		//Al iniciar el campo tiene un caracter en blanco o tiene comas, al obtener el foco se elimina el  espacio por espacio en blanco
		$campo.focus(function(e){
			var valor=quitar_comas($(this).val().trim());
			
			if(valor != ''){
				if(parseFloat(valor)<=0){
					$(this).val('');
				}else{
					$(this).val(valor);
				}
			}
		});
	}
	
	
	
	/*
	Esta funcion es para manejar el comportamiento de los input de cuentas.
	Al eliminar los datos de un campo, se regresa el cursor al campo anterior
	Al teclear 4 digitos en un campo, se pasa el cursor al siguiente campo
	*/
	$aplica_evento_keypress_input_cta = function($campo_input, $campo_input_anterior, $campo_input_siguiente, saltar_anterior, saltar_siguiente){
		$campo_input.keypress(function(e){
			if (e.which == 8) {
				if(saltar_anterior){
					if((parseInt($campo_input.val().length)-1)<=0){
						$campo_input_anterior.focus();
					}
				}
			}else{
				if(saltar_siguiente){
					if((parseInt($campo_input.val().length)+1)>=4){
						$campo_input_siguiente.focus();
					}
				}
			}
		});
	}


	
	
	$aplica_read_only_input_text = function($campo){
		$campo.attr("readonly", true);
		$campo.css({'background' : '#f0f0f0'});
	}
	
	
	
	
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/ctbdefasientos";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	
	$('#barra_titulo').find('#td_titulo').append(document.title);
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
	var $cadena_busqueda = "";
	//var $busqueda_select_sucursal = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_sucursal]');
	var $busqueda_select_tipo_poliza = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_tipo_poliza]');
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_nombre = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_nombre]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		//valor_retorno += "sucursal" + signo_separador + $busqueda_select_sucursal.val() + "|";
		valor_retorno += "tipo_pol" + signo_separador + $busqueda_select_tipo_poliza.val() + "|";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "nombre" + signo_separador + $busqueda_nombre.val() + "|";
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
	
	
	$iniciar_campos_busqueda = function(){
		$busqueda_folio.val('');
		$busqueda_nombre.val('');
		
		/*
		//Cargar select con estatus
		var elemento_seleccionado = 0;
		var mostrar_opciones = 'true';
		$carga_select_con_arreglo_fijo($busqueda_select_estatus, array_status, elemento_seleccionado, mostrar_opciones);
		*/
		
		var input_json_cuentas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInicializar.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_cuentas,$arreglo,function(data){
			/*
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
			*/
			
			//Carga select de Tipos de Poliza
			var elemento_seleccionado = 0;
			var texto_elemento_cero = '[-- --]';
			var index_elem = 'id';
			var index_text_elem = 'titulo';
			var option_fijo = false;
			$carga_campos_select($busqueda_select_tipo_poliza, data['TP'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
			
			/*
			ArrayCon = data['Con'];
			arrayTmov = data['Tmov'];
			*/
			
			CtaMay = data['CtaMay'];
			ArraySuc = data['Suc'];
			ArrayTP = data['TP'];
			parametros = data['Data'];
			arrayCampos = data['Campos'];
			$busqueda_select_tipo_poliza.focus();
		});
	}
	
	
	//LLamarda a la funcion que inicializa los campos de busqueda
	$iniciar_campos_busqueda();
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		//LLamarda a la funcion que inicializa los campos de busqueda
		$iniciar_campos_busqueda();
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
		
		$busqueda_select_tipo_poliza.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	//$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_sucursal, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_tipo_poliza, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_nombre, $buscar);
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-ctbdefasientos-window').find('#submit').mouseover(function(){
			$('#forma-ctbdefasientos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-ctbdefasientos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-ctbdefasientos-window').find('#submit').mouseout(function(){
			$('#forma-ctbdefasientos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-ctbdefasientos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-ctbdefasientos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-ctbdefasientos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-ctbdefasientos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-ctbdefasientos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-ctbdefasientos-window').find('#close').mouseover(function(){
			$('#forma-ctbdefasientos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-ctbdefasientos-window').find('#close').mouseout(function(){
			$('#forma-ctbdefasientos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-ctbdefasientos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-ctbdefasientos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-ctbdefasientos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-ctbdefasientos-window').find("ul.pestanas li").click(function() {
			$('#forma-ctbdefasientos-window').find(".contenidoPes").hide();
			$('#forma-ctbdefasientos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-ctbdefasientos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	

	

	
	
	//buscador de Cuentas Contables
	$busca_cuentas_contables = function(tipo, nivel_cta, arrayCtasMayor, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta){
		//limpiar_campos_grids();
		$(this).modalPanel_buscactacontable();
		var $dialogoc =  $('#forma-buscactacontable-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_cuentas').find('table.formaBusqueda_cuentas').clone());
		
		$('#forma-buscactacontable-window').css({"margin-left": -200, 	"margin-top": -160});
		
		var $tabla_resultados = $('#forma-buscactacontable-window').find('#tabla_resultado');
		
		var $select_cta_mayor = $('#forma-buscactacontable-window').find('select[name=select_cta_mayor]');
		var $campo_clasif = $('#forma-buscactacontable-window').find('input[name=clasif]');
		var $campo_cuenta = $('#forma-buscactacontable-window').find('input[name=cuenta]');
		var $campo_scuenta = $('#forma-buscactacontable-window').find('input[name=scuenta]');
		var $campo_sscuenta = $('#forma-buscactacontable-window').find('input[name=sscuenta]');
		var $campo_ssscuenta = $('#forma-buscactacontable-window').find('input[name=ssscuenta]');
		var $campo_sssscuenta = $('#forma-buscactacontable-window').find('input[name=sssscuenta]');
		var $campo_descripcion = $('#forma-buscactacontable-window').find('input[name=campo_descripcion]');
		
		var $boton_busca = $('#forma-buscactacontable-window').find('#boton_busca');
		var $boton_cencela = $('#forma-buscactacontable-window').find('#boton_cencela');
		var mayor_seleccionado=0;
		var detalle=0;
		var clasifica='';
		
		$campo_cuenta.hide();
		$campo_scuenta.hide();
		$campo_sscuenta.hide();
		$campo_ssscuenta.hide();
		$campo_sssscuenta.hide();
		
		$permitir_solo_numeros($campo_clasif);
		$permitir_solo_numeros($campo_cuenta);
		$permitir_solo_numeros($campo_scuenta);
		$permitir_solo_numeros($campo_sscuenta);
		$permitir_solo_numeros($campo_ssscuenta);
		$permitir_solo_numeros($campo_sssscuenta);
		
		//funcionalidad botones
		$boton_busca.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		
		$boton_busca.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$boton_cencela.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$boton_cencela.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		if(parseInt(nivel_cta) >=1 ){ $campo_cuenta.show(); $campo_cuenta.val($cuenta.val())};
		if(parseInt(nivel_cta) >=2 ){ $campo_scuenta.show(); $campo_scuenta.val($scuenta.val())};
		if(parseInt(nivel_cta) >=3 ){ $campo_sscuenta.show(); $campo_sscuenta.val($sscuenta.val())};
		if(parseInt(nivel_cta) >=4 ){ $campo_ssscuenta.show(); $campo_ssscuenta.val($ssscuenta.val())};
		if(parseInt(nivel_cta) >=5 ){ $campo_sssscuenta.show(); $campo_sssscuenta.val($sssscuenta.val())};
		
		
		//mayor_seleccionado 1=Activo	clasifica=1(Activo Circulante)
		//mayor_seleccionado 5=Egresos	clasifica=1(Costo de Ventas)
		//mayor_seleccionado 4=Activo	clasifica=1(Ventas)
		//if(parseInt(tipo)==1 ){mayor_seleccionado=1; detalle=1; clasifica=1; };
		//if(parseInt(tipo)==2 ){mayor_seleccionado=5; detalle=1; clasifica=1; };
		//if(parseInt(tipo)==3 ){mayor_seleccionado=4; detalle=1; clasifica=1; };
		
		//detalle=1;
		
		$campo_clasif.val(clasifica);
		
		//carga select de cuentas de Mayor
		$select_cta_mayor.children().remove();
		var ctamay_hmtl = '<option value="0_0">[---- ----]</option>';;
		$.each(arrayCtasMayor,function(entryIndex,ctamay){
			/*
			if (parseInt(mayor_seleccionado) == parseInt( ctamay['id']) ){
				ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['titulo'] + '</option>';
			}
			*/
			ctamay_hmtl += '<option value="'+ ctamay['cta_mayor'] +'_'+ ctamay['clasificacion'] +'">'+ ctamay['descripcion'] + '</option>';
		});
		$select_cta_mayor.append(ctamay_hmtl);
		
		//click buscar Cuentas Contables
		$boton_busca.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorCuentasContables.json';
			$arreglo = {	'cta_mayor':$select_cta_mayor.val(),
							'detalle':detalle,
							'clasifica':$campo_clasif.val(),
							'cta':$campo_cuenta.val(),
							'scta':$campo_scuenta.val(),
							'sscta':$campo_sscuenta.val(),
							'ssscta':$campo_ssscuenta.val(),
							'sssscta':$campo_sssscuenta.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				var notr=0;
				$.each(entry['CtaContables'],function(entryIndex,cuenta){
					//obtiene numero de trs
					notr = $("tr", $tabla_resultados).size();
					notr++;
					
					trr = '<tr class="tr'+notr+'">';
						trr += '<td width="30">'+cuenta['m']+'</td>';
						trr += '<td width="30">'+cuenta['c']+'</td>';
						trr += '<td width="170">';
							trr += '<input type="hidden" name="id_cta" value="'+cuenta['id']+'" >';
							trr += '<input type="text" name="cta" value="'+cuenta['cuenta']+'" class="borde_oculto" style="width:166px; readOnly="true">';
							trr += '<input type="hidden" name="campo_cta" value="'+cuenta['cta']+'" >';
							trr += '<input type="hidden" name="campo_scta" value="'+cuenta['subcta']+'" >';
							trr += '<input type="hidden" name="campo_sscta" value="'+cuenta['ssubcta']+'" >';
							trr += '<input type="hidden" name="campo_ssscta" value="'+cuenta['sssubcta']+'" >';
							trr += '<input type="hidden" name="campo_ssscta" value="'+cuenta['ssssubcta']+'" >';
						trr += '</td>';
						trr += '<td width="230"><input type="text" name="des" value="'+cuenta['descripcion']+'" class="borde_oculto" style="width:226px; readOnly="true"></td>';
						trr += '<td width="70">'+cuenta['detalle']+'</td>';
						trr += '<td width="50">'+cuenta['nivel_cta']+'</td>';
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
				
				//Seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					var id_cta = $(this).find('input[name=id_cta]').val();
					var cta = $(this).find('input[name=campo_cta]').val();
					var scta = $(this).find('input[name=campo_scta]').val();
					var sscta = $(this).find('input[name=campo_sscta]').val();
					var ssscta = $(this).find('input[name=campo_ssscta]').val();
					var sssscta = $(this).find('input[name=campo_ssscta]').val();
					var desc = $(this).find('input[name=des]').val();
					
					if(parseInt(tipo)==1 ){
						//$('#forma-ctbdefasientos-window').find('input[name=id_cta]').val(id_cta);
						$('#forma-ctbdefasientos-window').find('input[name=cuenta]').val(cta);
						$('#forma-ctbdefasientos-window').find('input[name=scuenta]').val(scta);
						$('#forma-ctbdefasientos-window').find('input[name=sscuenta]').val(sscta);
						$('#forma-ctbdefasientos-window').find('input[name=ssscuenta]').val(ssscta);
						$('#forma-ctbdefasientos-window').find('input[name=sssscuenta]').val(sssscta);
						$('#forma-ctbdefasientos-window').find('input[name=descripcion_cuenta]').val(desc);
						
						if(parseInt(nivel_cta) ==1 ){ $campo_scuenta.val(''); $campo_sscuenta.val(''); $campo_ssscuenta.val(''); $campo_sssscuenta.val('');};
						if(parseInt(nivel_cta) ==2 ){ $campo_sscuenta.val(''); $campo_ssscuenta.val(''); $campo_sssscuenta.val(''); };
						if(parseInt(nivel_cta) ==3 ){ $campo_ssscuenta.val(''); $campo_sssscuenta.val(''); };
						if(parseInt(nivel_cta) ==4 ){ $campo_sssscuenta.val(''); };
						if(parseInt(nivel_cta) ==5 ){ /*Aqui no hay nada*/ };
					};
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscactacontable-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-ctbdefasientos-window').find('input[name=cuenta]').focus();
				});
			});//termina llamada json
		});
		
		$campo_clasif.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		//Aplica funcionalidad para saltar al del campo actual al siguiente y al campo anterior
		$aplica_evento_keypress_input_cta($campo_cuenta, $campo_cuenta, $campo_scuenta, false, true);
		$aplica_evento_keypress_input_cta($campo_scuenta, $campo_cuenta, $campo_sscuenta, true, true);
		$aplica_evento_keypress_input_cta($campo_sscuenta, $campo_scuenta, $campo_ssscuenta, true, true);
		$aplica_evento_keypress_input_cta($campo_ssscuenta, $campo_sscuenta, $campo_sssscuenta, true, true);
		$aplica_evento_keypress_input_cta($campo_sssscuenta, $campo_ssscuenta, $campo_sssscuenta, true, false);
		
		//Aplicar evento keypress para que al momento de pulsar enter se ejecute la busqueda
		$(this).aplicarEventoKeypressEjecutaTrigger($select_cta_mayor, $boton_busca);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_clasif, $boton_busca);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_cuenta, $boton_busca);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_scuenta, $boton_busca);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_sscuenta, $boton_busca);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_ssscuenta, $boton_busca);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_sssscuenta, $boton_busca);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_descripcion, $boton_busca);
		
		$boton_cencela.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscactacontable-overlay').fadeOut(remove);
		});
		
		$select_cta_mayor.focus();
	}//termina buscador de Cuentas Contables


	
	
	
	
	

	
	//generar tr para agregar al grid
	var $agrega_tr = function($grid_cuentas, id_det, id_cta, cta, descripcion, tipo, seleccionado, campo_importe){
		var noTr = $("tr", $grid_cuentas).size();
		noTr++;
		
		var encontrado = 0;
		var valor_seleccionado="false";
		var check_seleccionado="";
		
		//Busca si la cuenta ya se encuentra en el grid
		$grid_cuentas.find('tr').each(function (index){
			if(parseInt($(this).find('input[name=id_cta]').val()) == parseInt(id_cta)){
				//La cuenta ya se encuentra en el grid
				encontrado=1;
			}
		});
		
		if(seleccionado){
			valor_seleccionado="true";
			check_seleccionado="checked";
		}
		
		if(parseInt(encontrado)<=0){
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size:11px; border:1px solid #C1DAD7;" width="28">';
					trr += '<a href="#del'+ noTr +'"><div id="eliminar'+ noTr +'" class="onmouseOutEliminar" style="width:25px; background-position:center;"/></a>';
					trr += '<input type="hidden" 	name="id_det" value="'+ id_det +'">';
					trr += '<input type="hidden" 	name="delete" value="1">';//El 1 significa que el registro no ha sido eliminado
					trr += '<input type="hidden" 	name="no_tr" value="'+ noTr +'">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="150">';
					trr += '<input type="hidden" 	name="id_cta" value="'+ id_cta +'">';
					trr += '<input type="text" 		name="cta" 	  id="cta'+ noTr +'"value="'+ cta +'" class="borde_oculto" readOnly="true" style="width:146px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="250">';
					trr += '<input type="text" 		name="descripcion_cta" 	value="'+ descripcion +'" class="borde_oculto" readOnly="true" style="width:246px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<select name="select_importe" id="select_importe'+ noTr +'" style="width:78px;"></select>';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="60">';
					trr += '<select name="select_mov" id="select_mov'+ noTr +'" style="width:58px;"></select>';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="30">';
					trr += '<input type="checkbox" name="micheck" id="micheck'+ noTr +'" value="check" '+ check_seleccionado +' >';
					trr += '<input type="hidden" name="detalle" id="detalle'+ noTr +'" value="'+ valor_seleccionado +'">';
				trr += '</td>';
			trr += '</tr>';
			$grid_cuentas.append(trr);
			
			
			
			//Carga select 
			$grid_cuentas.find('#select_importe'+noTr).children().remove();
			var campo_hmtl = '';
			
			if(parseInt(campo_importe)<=0){
				campo_hmtl += '<option value="0">[--- ---]</option>';
			}
			
			$.each(arrayCampos,function(entryIndex,campo){
				if(parseInt(campo_importe)==parseInt(campo['id'])){
					campo_hmtl += '<option value="' + campo['id'] + '" selected="yes">'+ campo['titulo'] + '</option>';
				}else{
					campo_hmtl += '<option value="' + campo['id'] + '">'+ campo['titulo'] + '</option>';
				}
			});
			$grid_cuentas.find('#select_importe'+noTr).append(campo_hmtl);
			
			
			
			//Carga select
			$grid_cuentas.find('#select_mov'+noTr).children().remove();
			var suc_hmtl = '';
			if(parseInt(tipo)==1){
				suc_hmtl += '<option value="1" selected="yes">Debe</option>';
				suc_hmtl += '<option value="2">Haber</option>';
			}else{
				suc_hmtl += '<option value="1">Debe</option>';
				suc_hmtl += '<option value="2" selected="yes">Haber</option>';
			}
			$grid_cuentas.find('#select_mov'+noTr).append(suc_hmtl);
			
			$grid_cuentas.find('#micheck'+noTr).click(function(){
				if(this.checked){
					$grid_cuentas.find('#detalle'+noTr).val('true');
				}else{
					$grid_cuentas.find('#detalle'+noTr).val('false');
				}
			});
			
			
			//elimina un producto del grid
			$grid_cuentas.find('a[href=#del'+ noTr +']').bind('click',function(event){
				//event.preventDefault();
				if(parseInt($(this).parent().find('input[name=delete]').val()) != 0){
					//Obtener el id_det
					var id_det = $(this).parent().find('input[name=id_det]').val();
					
					//Asigna espacios en blanco a todos los input de la fila eliminada
					$(this).parent().parent().find('input').val(' ');
					
					//Asigna un 0 al input eliminado como bandera para saber que esta eliminado
					$(this).parent().find('input[name=delete]').val(0);
					
					//Regresar el valor al campo id_det
					$(this).parent().find('input[name=id_det]').val(id_det);
					
					//oculta la fila eliminada
					$(this).parent().parent().hide();
				}
			});
			
			
			$grid_cuentas.find('#eliminar'+ noTr).mouseover(function(){
				$(this).removeClass("onmouseOutEliminar").addClass("onmouseOverEliminar");
			});
			$grid_cuentas.find('#eliminar'+ noTr).mouseout(function(){
				$(this).removeClass("onmouseOverEliminar").addClass("onmouseOutEliminar");
			});
			
			//Limpiar campos de busqueda
			$('#forma-ctbdefasientos-window').find('input[name=cuenta]').val('');
			$('#forma-ctbdefasientos-window').find('input[name=scuenta]').val('');
			$('#forma-ctbdefasientos-window').find('input[name=sscuenta]').val('');
			$('#forma-ctbdefasientos-window').find('input[name=ssscuenta]').val('');
			$('#forma-ctbdefasientos-window').find('input[name=sssscuenta]').val('');
		}else{
			jAlert('La cuenta: '+cta+' ya se encuentra en el listado, ingrese otro diferente.', 'Atencion!', function(r) { 
				if($('#forma-ctbdefasientos-window').find('input[name=sssscuenta]').val().trim()==''){
					$('#forma-ctbdefasientos-window').find('input[name=sssscuenta]').focus();
				}
				if($('#forma-ctbdefasientos-window').find('input[name=ssscuenta]').val().trim()==''){
					$('#forma-ctbdefasientos-window').find('input[name=ssscuenta]').focus();
				}
				if($('#forma-ctbdefasientos-window').find('input[name=sscuenta]').val().trim()==''){
					$('#forma-ctbdefasientos-window').find('input[name=sscuenta]').focus();
				}
				if($('#forma-ctbdefasientos-window').find('input[name=scuenta]').val().trim()==''){
					$('#forma-ctbdefasientos-window').find('input[name=scuenta]').focus();
				}
				if($('#forma-ctbdefasientos-window').find('input[name=cuenta]').val().trim()==''){
					$('#forma-ctbdefasientos-window').find('input[name=cuenta]').focus();
				}
			});
		}
	}
	
	
			


	//Obtiene datos de una cuenta contable en especifico
	$getDataCta = function($grid_cuentas, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta){
		var detalle=0;
		
		//detalle=1;
		
		if($cuenta.val().trim()!='' || $scuenta.val().trim()!='' || $sscuenta.val().trim()!='' || $ssscuenta.val().trim()!='' || $sssscuenta.val().trim()!=''){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataCta.json';
			var $arreglo = {'detalle':detalle, 'cta':$cuenta.val(), 'scta':$scuenta.val(), 'sscta':$sscuenta.val(), 'ssscta':$ssscuenta.val(), 'sssscta':$sssscuenta.val(),'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
			
			$.post(input_json,$arreglo,function(entry){
				if(parseInt(entry['Cta'].length)>0){
					var id_det=0;
					var id_tmov=0;
					var id_cta=entry['Cta'][0]['id'];
					var cta=entry['Cta'][0]['cuenta'].trim();
					var descripcion=entry['Cta'][0]['descripcion'];
					var tipo = 1;
					
					$agrega_tr($grid_cuentas, id_det, id_cta, cta, descripcion, tipo, false, 0);
				}else{
					jAlert('La cuenta ingresada no es valida.', 'Atencion!', function(r) {
						if($sssscuenta.val().trim()==''){
							$sssscuenta.focus();
						}
						if($ssscuenta.val().trim()==''){
							$ssscuenta.focus();
						}
						if($sscuenta.val().trim()==''){
							$sscuenta.focus();
						}
						if($scuenta.val().trim()==''){
							$scuenta.focus();
						}
						if($cuenta.val().trim()==''){
							$cuenta.focus();
						}
					});
				}
			});//termina llamada json
		}else{
			jAlert('Es necesario ingresar una cuenta', 'Atencion!', function(r) {
				if($sssscuenta.val().trim()==''){
					$sssscuenta.focus();
				}
				if($ssscuenta.val().trim()==''){
					$ssscuenta.focus();
				}
				if($sscuenta.val().trim()==''){
					$sscuenta.focus();
				}
				if($scuenta.val().trim()==''){
					$scuenta.focus();
				}
				if($cuenta.val().trim()==''){
					$cuenta.focus();
				}
			});
		}
		
	}//termina buscador de Cuentas Contables








	
	//nuevo 
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_ctbdefasientos();
		
		var form_to_show = 'formactbdefasientos00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-ctbdefasientos-window').css({ "margin-left": -310, 	"margin-top": -265 });
		$forma_selected.prependTo('#forma-ctbdefasientos-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $identificador = $('#forma-ctbdefasientos-window').find('input[name=identificador]');
		//var $select_sucursal = $('#forma-ctbdefasientos-window').find('select[name=select_sucursal]');
		var $folio = $('#forma-ctbdefasientos-window').find('input[name=folio]');
		var $nombre = $('#forma-ctbdefasientos-window').find('input[name=nombre]');
		var $select_fecha = $('#forma-ctbdefasientos-window').find('select[name=select_fecha]');
		var $select_pol_num = $('#forma-ctbdefasientos-window').find('select[name=select_pol_num]');
		var $select_tipo = $('#forma-ctbdefasientos-window').find('select[name=select_tipo]');
		var $select_app = $('#forma-ctbdefasientos-window').find('select[name=select_app]');
		
		var $cuenta = $('#forma-ctbdefasientos-window').find('input[name=cuenta]');
		var $scuenta = $('#forma-ctbdefasientos-window').find('input[name=scuenta]');
		var $sscuenta = $('#forma-ctbdefasientos-window').find('input[name=sscuenta]');
		var $ssscuenta = $('#forma-ctbdefasientos-window').find('input[name=ssscuenta]');
		var $sssscuenta = $('#forma-ctbdefasientos-window').find('input[name=sssscuenta]');
		
		var $agregar_cta = $('#forma-ctbdefasientos-window').find('#agregar_cta');
		var $buscar_cta = $('#forma-ctbdefasientos-window').find('#buscar_cta');
		
		//Grid de Cuentas contables
		var $grid_cuentas = $('#forma-ctbdefasientos-window').find('#grid_cuentas');
		var $grid_warning = $('#forma-ctbdefasientos-window').find('#grid_warning');
		
		var $cerrar_plugin = $('#forma-ctbdefasientos-window').find('#close');
		var $cancelar_plugin = $('#forma-ctbdefasientos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-ctbdefasientos-window').find('#submit');
		var $boton_actualizar = $('#forma-ctbdefasientos-window').find('#boton_actualizar');
		
		$permitir_solo_numeros($cuenta);
		$permitir_solo_numeros($scuenta);
		$permitir_solo_numeros($sscuenta);
		$permitir_solo_numeros($ssscuenta);
		$permitir_solo_numeros($sssscuenta);
		
		$cuenta.hide();
		$scuenta.hide();
		$sscuenta.hide();
		$ssscuenta.hide();
		$sssscuenta.hide();
		
		$aplica_read_only_input_text($folio);
		
		$identificador.attr({ 'value' : 0 });
		
		//quitar enter a todos los campos input
		$('#forma-ctbdefasientos-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Cuenta fue dada de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-ctbdefasientos-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-ctbdefasientos-window').find('div.interrogacion').css({'display':'none'});
				//$('#forma-ctbdefasientos-window').find('.ctbdefasientos_div_one').css({'height':'620px'});
				
				//$grid_cuentas.find('select').css({'background' : '#ffffff'});
				//$grid_cuentas.find('input').css({'background' : '#ffffff'});
				
				$('#forma-ctbdefasientos-window').find('#div_warning_grid').css({'display':'none'});
				//$('#forma-ctbdefasientos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-ctbdefasientos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
						
						/*
						if((tmp.split(':')[0].substring(0, 11) == 'select_tmov') || (tmp.split(':')[0].substring(0, 3) == 'cta') || (tmp.split(':')[0].substring(0, 4) == 'debe') || (tmp.split(':')[0].substring(0, 4) == 'haber')){
							var campo = tmp.split(':')[0];
							
							$('#forma-ctbdefasientos-window').find('#div_warning_grid').css({'display':'block'});
							var $campo = $grid_cuentas.find('#'+campo).css({'background' : '#d41000'});
							
							var cta = $campo.parent().parent().find('input[name=cta]').val();
							var descripcion_cta = $campo.parent().parent().find('input[name=descripcion_cta]').val();
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="150"><INPUT TYPE="text" value="' + cta + '" class="borde_oculto" readOnly="true" style="width:150px; color:red"></td>';
									tr_warning += '<td width="250"><INPUT TYPE="text" value="' + descripcion_cta + '" class="borde_oculto" readOnly="true" style="width:250px; color:red"></td>';
									tr_warning += '<td width="380"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:350px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-ctbdefasientos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
						*/
					}
				}
				/*
				$('#forma-ctbdefasientos-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
				$('#forma-ctbdefasientos-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
				*/
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAsiento.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			//Visualizar subcuentas de acuerdo al nivel definido para la empresa
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $cuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $scuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $sscuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ssscuenta.show(); };
			if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $sssscuenta.show(); };
			
			/*
			//Carga select de cuentas de Mayor
			$select_sucursal.children().remove();
			var suc_hmtl = '';
			$.each(ArraySuc,function(entryIndex,suc){
				if(parseInt(suc['id'])==parseInt(parametros['suc'])){
					suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
				}else{
					if(parametros['versuc']==true){
						suc_hmtl += '<option value="' + suc['id'] + '">'+ suc['titulo'] + '</option>';
					}
				}
			});
			$select_sucursal.append(suc_hmtl);
			*/
			
			//Carga select de Tipos de Poliza
			$select_tipo.children().remove();
			var tp_hmtl = '';
			$.each(ArrayTP,function(entryIndex,tp){
				tp_hmtl += '<option value="'+ tp['id'] +'">'+ tp['titulo'] +'</option>';
			});
			$select_tipo.append(tp_hmtl);
			
			//Carga select 
			$select_app.children().remove();
			var app_hmtl = '';
			$.each(entry['App'],function(entryIndex,tp){
				app_hmtl += '<option value="' + tp['id'] + '">'+ tp['titulo'] + '</option>';
			});
			$select_app.append(app_hmtl);
			
			$agregar_cta.click(function(event){
				event.preventDefault();
				$getDataCta($grid_cuentas, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta);
			});
			
			//Busca Cuentas Contables
			$buscar_cta.click(function(event){
				event.preventDefault();
				$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], CtaMay, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta);
			});
			
			$nombre.focus();
		},"json");
		//termina llamada json
		
		
		//Cargar select
		var elemento_seleccionado = 0;
		var mostrar_opciones = 'true';
		$carga_select_con_arreglo_fijo($select_pol_num, array_polnum, elemento_seleccionado, mostrar_opciones);
		
		elemento_seleccionado = 0;
		mostrar_opciones = 'true';
		$carga_select_con_arreglo_fijo($select_fecha, array_fecha, elemento_seleccionado, mostrar_opciones);
		
		//Aplica evento para agregar al grid al pulsar enter en cuelquira de los campos de la cuenta
		$(this).aplicarEventoKeypressEjecutaTrigger($cuenta, $agregar_cta);
		$(this).aplicarEventoKeypressEjecutaTrigger($scuenta, $agregar_cta);
		$(this).aplicarEventoKeypressEjecutaTrigger($sscuenta, $agregar_cta);
		$(this).aplicarEventoKeypressEjecutaTrigger($ssscuenta, $agregar_cta);
		$(this).aplicarEventoKeypressEjecutaTrigger($sssscuenta, $agregar_cta);
		
		//Aplica funcionalidad para saltar al del campo actual al siguiente y al campo anterior
		$aplica_evento_keypress_input_cta($cuenta, $cuenta, $scuenta, false, true);
		$aplica_evento_keypress_input_cta($scuenta, $cuenta, $sscuenta, true, true);
		$aplica_evento_keypress_input_cta($sscuenta, $scuenta, $ssscuenta, true, true);
		$aplica_evento_keypress_input_cta($ssscuenta, $sscuenta, $sssscuenta, true, true);
		$aplica_evento_keypress_input_cta($sssscuenta, $ssscuenta, $sssscuenta, true, false);
		
		/*
		//Este boton no es submit, se ejecuta $submit_actualizar.trigger('click') para enviar la peticion
		$boton_actualizar.bind('click',function(){
			var encontrado=0;
			//Busca si hay algun registro en el grid
			$grid_cuentas.find('tr').each(function (index){
				if(parseInt($(this).find('input[name=delete]').val())>0){
					encontrado++;
				}
			});
			
			if(parseInt(encontrado) > 0){
				if(parseFloat(quitar_comas($('#forma-ctbdefasientos-window').find('#suma_debe').html()))!=parseFloat(quitar_comas($('#forma-ctbdefasientos-window').find('#suma_haber').html()))){					
					jConfirm('La suma de los asientos contables no cuadran. Es necesario cuadrar antes de contabilizar.<br>Desea guardar la poliza?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.trigger('click');
						}
					});
				}else{
					$submit_actualizar.trigger('click');
				}
			}else{
				jAlert('No hay datos para actualizar', 'Atencion!', function(r) { $cuenta.focus(); });
				return false;
			}
		});
		*/
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-ctbdefasientos-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-ctbdefasientos-overlay').fadeOut(remove);
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
			var form_to_show = 'formactbdefasientos00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_ctbdefasientos();
			$('#forma-ctbdefasientos-window').css({ "margin-left": -310, 	"margin-top": -265 });
			
			$forma_selected.prependTo('#forma-ctbdefasientos-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $identificador = $('#forma-ctbdefasientos-window').find('input[name=identificador]');
			//var $select_sucursal = $('#forma-ctbdefasientos-window').find('select[name=select_sucursal]');
			var $folio = $('#forma-ctbdefasientos-window').find('input[name=folio]');
			var $nombre = $('#forma-ctbdefasientos-window').find('input[name=nombre]');
			var $select_fecha = $('#forma-ctbdefasientos-window').find('select[name=select_fecha]');
			var $select_pol_num = $('#forma-ctbdefasientos-window').find('select[name=select_pol_num]');
			var $select_tipo = $('#forma-ctbdefasientos-window').find('select[name=select_tipo]');
			var $select_app = $('#forma-ctbdefasientos-window').find('select[name=select_app]');
			
			var $cuenta = $('#forma-ctbdefasientos-window').find('input[name=cuenta]');
			var $scuenta = $('#forma-ctbdefasientos-window').find('input[name=scuenta]');
			var $sscuenta = $('#forma-ctbdefasientos-window').find('input[name=sscuenta]');
			var $ssscuenta = $('#forma-ctbdefasientos-window').find('input[name=ssscuenta]');
			var $sssscuenta = $('#forma-ctbdefasientos-window').find('input[name=sssscuenta]');
			
			var $agregar_cta = $('#forma-ctbdefasientos-window').find('#agregar_cta');
			var $buscar_cta = $('#forma-ctbdefasientos-window').find('#buscar_cta');
			
			//Grid de Cuentas contables
			var $grid_cuentas = $('#forma-ctbdefasientos-window').find('#grid_cuentas');
			var $grid_warning = $('#forma-ctbdefasientos-window').find('#grid_warning');
			
			var $cerrar_plugin = $('#forma-ctbdefasientos-window').find('#close');
			var $cancelar_plugin = $('#forma-ctbdefasientos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-ctbdefasientos-window').find('#submit');
			var $boton_actualizar = $('#forma-ctbdefasientos-window').find('#boton_actualizar');
			
			$permitir_solo_numeros($cuenta);
			$permitir_solo_numeros($scuenta);
			$permitir_solo_numeros($sscuenta);
			$permitir_solo_numeros($ssscuenta);
			$permitir_solo_numeros($sssscuenta);
			
			$cuenta.hide();
			$scuenta.hide();
			$sscuenta.hide();
			$ssscuenta.hide();
			$sssscuenta.hide();
			
			$aplica_read_only_input_text($folio);
			
									
			//quitar enter a todos los campos input
			$('#forma-ctbdefasientos-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAsiento.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-ctbdefasientos-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						//$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-ctbdefasientos-window').find('div.interrogacion').css({'display':'none'});
						//$('#forma-ctbdefasientos-window').find('.ctbdefasientos_div_one').css({'height':'620px'});
						
						$('#forma-ctbdefasientos-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-ctbdefasientos-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-ctbdefasientos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
								/*
								if((tmp.split(':')[0].substring(0, 11) == 'select_tmov') || (tmp.split(':')[0].substring(0, 3) == 'cta') || (tmp.split(':')[0].substring(0, 4) == 'debe') || (tmp.split(':')[0].substring(0, 4) == 'haber')){
									var campo = tmp.split(':')[0];
									
									$('#forma-ctbdefasientos-window').find('#div_warning_grid').css({'display':'block'});
									var $campo = $grid_cuentas.find('#'+campo).css({'background' : '#d41000'});
									
									var cta = $campo.parent().parent().find('input[name=cta]').val();
									var descripcion_cta = $campo.parent().parent().find('input[name=descripcion_cta]').val();
									
									var tr_warning = '<tr>';
											tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
											tr_warning += '<td width="150"><INPUT TYPE="text" value="' + cta + '" class="borde_oculto" readOnly="true" style="width:150px; color:red"></td>';
											tr_warning += '<td width="250"><INPUT TYPE="text" value="' + descripcion_cta + '" class="borde_oculto" readOnly="true" style="width:250px; color:red"></td>';
											tr_warning += '<td width="380"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:350px; color:red"></td>';
									tr_warning += '</tr>';
									
									$('#forma-ctbdefasientos-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								}
								*/
							}
						}
						/*
						$('#forma-ctbdefasientos-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
						$('#forma-ctbdefasientos-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
						*/
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//Aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					//Visualizar subcuentas de acuerdo al nivel definido para la empresa
					if(parseInt(entry['Extras'][0]['nivel_cta'])>=1){ $cuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta'])>=2){ $scuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta'])>=3){ $sscuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta'])>=4){ $ssscuenta.show(); };
					if(parseInt(entry['Extras'][0]['nivel_cta'])>=5){ $sssscuenta.show(); };
					
					$identificador.attr({ 'value' : entry['Data'][0]['id'] });
					$folio.attr({ 'value' : entry['Data'][0]['folio'] });
					$nombre.val(entry['Data'][0]['titulo']);
					
					//Cargar select
					var elemento_seleccionado = entry['Data'][0]['pol_num'];
					var mostrar_opciones = 'true';
					$carga_select_con_arreglo_fijo($select_pol_num, array_polnum, elemento_seleccionado, mostrar_opciones);
					
					elemento_seleccionado = entry['Data'][0]['fecha'];
					mostrar_opciones = 'true';
					$carga_select_con_arreglo_fijo($select_fecha, array_fecha, elemento_seleccionado, mostrar_opciones);
					
					
					//Carga select de Tipos de Poliza
					$select_tipo.children().remove();
					var tp_hmtl = '';
					$.each(ArrayTP,function(entryIndex,tp){
						if(parseInt(entry['Data'][0]['tpol_id'])==parseInt(tp['id'])){
							tp_hmtl += '<option value="'+ tp['id'] +'" selected="yes">'+ tp['titulo'] + '</option>';
						}else{
							tp_hmtl += '<option value="'+ tp['id'] +'">'+ tp['titulo'] + '</option>';
						}
					});
					$select_tipo.append(tp_hmtl);
					
					//Carga select 
					$select_app.children().remove();
					var app_hmtl = '';
					if(parseInt(entry['Data'][0]['app_id'])<=0){
						app_hmtl = '<option value="0" selected="yes">[---- ----]</option>';
					}
					
					$.each(entry['App'],function(entryIndex,ap){
						if(parseInt(entry['Data'][0]['app_id'])==parseInt(ap['id'])){
							app_hmtl += '<option value="'+ ap['id'] +'" selected="yes">'+ ap['titulo'] +'</option>';
						}else{
							app_hmtl += '<option value="'+ ap['id'] +'">'+ ap['titulo'] +'</option>';
						}
					});
					$select_app.append(app_hmtl);
					
					if(parseInt(entry['Grid'].length)>0){
						$.each(entry['Grid'],function(entryIndex,grid){
							var id_det=grid['id_det'];
							var id_cta=grid['cta_id'];
							var cta=grid['cuenta'];
							var descripcion=grid['descripcion'];
							var tipo = grid['mov_tipo'];
							
							$agrega_tr($grid_cuentas, id_det, id_cta, cta, descripcion, tipo, grid['detalle'], grid['campo']);
						});
					}
					
					
					//Busca Cuentas Contables
					$buscar_cta.click(function(event){
						event.preventDefault();
						$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], CtaMay, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta);
					});
					
					$nombre.focus();
				},"json");//termina llamada json
				
				
				$agregar_cta.click(function(event){
					event.preventDefault();
					$getDataCta($grid_cuentas, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta);
				});
				
				//Aplica evento para agregar al grid al pulsar enter en cuelquira de los campos de la cuenta
				$(this).aplicarEventoKeypressEjecutaTrigger($cuenta, $agregar_cta);
				$(this).aplicarEventoKeypressEjecutaTrigger($scuenta, $agregar_cta);
				$(this).aplicarEventoKeypressEjecutaTrigger($sscuenta, $agregar_cta);
				$(this).aplicarEventoKeypressEjecutaTrigger($ssscuenta, $agregar_cta);
				$(this).aplicarEventoKeypressEjecutaTrigger($sssscuenta, $agregar_cta);
				
				//Aplica funcionalidad para saltar al del campo actual al siguiente y al campo anterior
				$aplica_evento_keypress_input_cta($cuenta, $cuenta, $scuenta, false, true);
				$aplica_evento_keypress_input_cta($scuenta, $cuenta, $sscuenta, true, true);
				$aplica_evento_keypress_input_cta($sscuenta, $scuenta, $ssscuenta, true, true);
				$aplica_evento_keypress_input_cta($ssscuenta, $sscuenta, $sssscuenta, true, true);
				$aplica_evento_keypress_input_cta($sssscuenta, $ssscuenta, $sssscuenta, true, false);
				
				
				/*
				$submit_actualizar.bind('click',function(){
					var encontrado=0;
					//Busca si hay algun registro en el grid
					$grid_cuentas.find('tr').each(function (index){
						if(parseInt($(this).find('input[name=delete]').val())>0){
							encontrado++;
						}
					});
					
					if(parseInt(encontrado) > 0){
						return true;
					}else{
						jAlert('No hay datos para actualizar', 'Atencion!', function(r) { $cuenta.focus(); });
						return false;
					}
				});
				*/
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-ctbdefasientos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-ctbdefasientos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                
			}
		}
	}
    
    
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllAsientos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllAsientos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
});



