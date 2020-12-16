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
	var controller = $contextpath.val()+"/controllers/gralimpret";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	$new.hide();
	
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
	var $busqueda_titulo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_titulo]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "titulo" + signo_separador + $busqueda_titulo.val() + "|";
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
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_titulo.val('');
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
		$busqueda_titulo.focus();
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_titulo, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-gralimpret-window').find('#submit').mouseover(function(){
			$('#forma-gralimpret-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-gralimpret-window').find('#submit').mouseout(function(){
			$('#forma-gralimpret-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-gralimpret-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-gralimpret-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-gralimpret-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-gralimpret-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-gralimpret-window').find('#close').mouseover(function(){
			$('#forma-gralimpret-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-gralimpret-window').find('#close').mouseout(function(){
			$('#forma-gralimpret-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-gralimpret-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-gralimpret-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-gralimpret-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-gralimpret-window').find("ul.pestanas li").click(function() {
			$('#forma-gralimpret-window').find(".contenidoPes").hide();
			$('#forma-gralimpret-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-gralimpret-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			
			if(activeTab=='#tabx-1'){
				$('#forma-gralimpret-window').find('input[name=titulo]').focus();
			}
			
			if(activeTab=='#tabx-2'){
				$('#forma-gralimpret-window').find('input[name=cuenta]').focus();
			}
			
			return false;
		});

	}
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	//funcion para hacer que un campo solo acepte numeros
	var $permitir_solo_numeros = function($campo){
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
	var $validar_numero_puntos = function($campo, campo_nombre){
		//Buscar cuantos puntos tiene  Precio Unitario
		var coincidencias = $campo.val().match(/\./g);
		var numPuntos = coincidencias ? coincidencias.length : 0;
		if(parseInt(numPuntos)>1){
			jAlert('El valor ingresado para el campo '+campo_nombre+' es incorrecto, tiene mas de un punto('+$campo.val()+').', 'Atencion!', function(r) { 
				$campo.focus();
			});
		}
	}
	
	
	var $aplica_evento_focus_input_numerico = function($campo){
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
					/*
					if((parseInt($campo_input.val().length)-1)<=0){
						$campo_input_anterior.focus();
					}
					*/
					if($campo_input.val().trim()==""){
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


	
	
	//buscador de Cuentas Contables
	$busca_cuentas_contables = function(tipo, nivel_cta, arrayCtasMayor, $cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion){
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
		
		detalle=1;
		
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
			var $arreglo = {'cta_mayor':$select_cta_mayor.val(),'detalle':detalle,'clasifica':$campo_clasif.val(),'cta':$campo_cuenta.val(),'scta':$campo_scuenta.val(),'sscta':$campo_sscuenta.val(),'ssscta':$campo_ssscuenta.val(),'sssscta':$campo_sssscuenta.val(),'descripcion':$campo_descripcion.val(),'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
			
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
					if(parseInt(tipo)==1 ){
						$cta_id.val($(this).find('input[name=id_cta]').val());
						$cuenta.val($(this).find('input[name=campo_cta]').val());
						$scuenta.val($(this).find('input[name=campo_scta]').val());
						$sscuenta.val($(this).find('input[name=campo_sscta]').val());
						$ssscuenta.val($(this).find('input[name=campo_ssscta]').val());
						$sssscuenta.val($(this).find('input[name=campo_ssscta]').val());
						$descripcion.val($(this).find('input[name=des]').val());
					};
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscactacontable-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-gralimpret-window').find('input[name=cuenta]').focus();
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
	
	
	
	
	//Obtiene datos de una cuenta contable en especifico
	var $getDataCta = function($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion){
		var detalle=1;
		
		if($cuenta.val().trim()!='' || $scuenta.val().trim()!='' || $sscuenta.val().trim()!='' || $ssscuenta.val().trim()!='' || $sssscuenta.val().trim()!=''){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataCta.json';
			var $arreglo = {'detalle':detalle, 'cta':$cuenta.val(), 'scta':$scuenta.val(), 'sscta':$sscuenta.val(), 'ssscta':$ssscuenta.val(), 'sssscta':$sssscuenta.val(),'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
			
			$.post(input_json,$arreglo,function(entry){
				if(parseInt(entry['Cta'].length)>0){
					$cta_id.val(entry['Cta'][0]['id']);
					$descripcion.val(entry['Cta'][0]['descripcion']);
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
	}
	//Termina buscador de Cuentas Contables
	
	
	
	
	//Nuevos
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_gralimpret();
		
		var form_to_show = 'formagralimpret';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-gralimpret-window').css({"margin-left": -260, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-gralimpret-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		$('#forma-gralimpret-window').find('ul.pestanas').find('a[href=#tabx-2]').parent().hide();
		
		var $identificador = $('#forma-gralimpret-window').find('input[name=identificador]'); 
		var $titulo = $('#forma-gralimpret-window').find('input[name=titulo]');
		var $tasa = $('#forma-gralimpret-window').find('input[name=tasa]');
		
		var $cta_id = $('#forma-gralimpret-window').find('input[name=cta_id]');
		var $cuenta = $('#forma-gralimpret-window').find('input[name=cuenta]');
		var $scuenta = $('#forma-gralimpret-window').find('input[name=scuenta]');
		var $sscuenta = $('#forma-gralimpret-window').find('input[name=sscuenta]');
		var $ssscuenta = $('#forma-gralimpret-window').find('input[name=ssscuenta]');
		var $sssscuenta = $('#forma-gralimpret-window').find('input[name=sssscuenta]');
		var $descripcion = $('#forma-gralimpret-window').find('input[name=descripcion]');
		
		var $buscar_cta = $('#forma-gralimpret-window').find('#buscar_cta');
		
		//Botones
		var $cerrar_plugin = $('#forma-gralimpret-window').find('#close');
		var $cancelar_plugin = $('#forma-gralimpret-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-gralimpret-window').find('#submit');
		
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
		
		//permitir solo numeros al campo tasa
		$permitir_solo_numeros($tasa);
		
		$identificador.attr({'value' : 0});
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Registro fue dado de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-gralimpret-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-gralimpret-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-gralimpret-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
                
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getImpto.json';
		var parametros={id:$identificador.val(), iu: $('#lienzo_recalculable').find('input[name=iu]').val() };
		$.post(input_json,parametros,function(entry){
			if(entry['Param']['contab']=='true'){
				$('#forma-gralimpret-window').find('ul.pestanas').find('a[href=#tabx-2]').parent().show();
			};
			
			//Visualizar subcuentas de acuerdo al nivel definido para la empresa
			if(parseInt(entry['Param']['nivel_cta']) >=1 ){ $cuenta.show(); };
			if(parseInt(entry['Param']['nivel_cta']) >=2 ){ $scuenta.show(); };
			if(parseInt(entry['Param']['nivel_cta']) >=3 ){ $sscuenta.show(); };
			if(parseInt(entry['Param']['nivel_cta']) >=4 ){ $ssscuenta.show(); };
			if(parseInt(entry['Param']['nivel_cta']) >=5 ){ $sssscuenta.show(); };
			
			//Busca Cuentas Contables
			$buscar_cta.click(function(event){
				event.preventDefault();
				$busca_cuentas_contables(1, entry['Param']['nivel_cta'], entry['CtaMay'], $cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
			});
		});//termina llamada json
		
		
		$aplica_evento_focus_input_numerico($tasa);
		
		$tasa.blur(function(){
			$validar_numero_puntos($tasa, "Tasa");
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
		
		
		$cuenta.keypress(function(e){
			if(e.which == 13){
				$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
				return false;
			}
		});
		
		$scuenta.keypress(function(e){
			if(e.which == 13){
				$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
				return false;
			}
		});
		
		$sscuenta.keypress(function(e){
			if(e.which == 13){
				$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
				return false;
			}
		});
		
		$ssscuenta.keypress(function(e){
			if(e.which == 13){
				$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
				return false;
			}
		});
		
		$sssscuenta.keypress(function(e){
			if(e.which == 13){
				$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
				return false;
			}
		});
		
		//Aplica funcionalidad para saltar al del campo actual al siguiente y al campo anterior
		$aplica_evento_keypress_input_cta($cuenta, $cuenta, $scuenta, false, true);
		$aplica_evento_keypress_input_cta($scuenta, $cuenta, $sscuenta, true, true);
		$aplica_evento_keypress_input_cta($sscuenta, $scuenta, $ssscuenta, true, true);
		$aplica_evento_keypress_input_cta($ssscuenta, $sscuenta, $sssscuenta, true, true);
		$aplica_evento_keypress_input_cta($sssscuenta, $ssscuenta, $sssscuenta, true, false);
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-gralimpret-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-gralimpret-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$titulo.focus();
	});
	
	
	
	//Eventos del grid edicion,borrar!
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			var $arreglo = {'id':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar el IEPS', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Registro fue eliminado exitosamente.", 'Atencion!');
							//$get_datos_grid();
						}else{
							jAlert("El Registro no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formagralimpret';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_gralimpret();
						
			$('#forma-gralimpret-window').css({"margin-left": -260, 	"margin-top": -200});
			$forma_selected.prependTo('#forma-gralimpret-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			$tabs_li_funxionalidad();
			
			$('#forma-gralimpret-window').find('ul.pestanas').find('a[href=#tabx-2]').parent().hide();
			
			//campos de la vista
			var $identificador = $('#forma-gralimpret-window').find('input[name=identificador]'); 
			//alert($identificador);
			var $ieps = $('#forma-gralimpret-window').find('input[name=nombreieps]');
			var $titulo = $('#forma-gralimpret-window').find('input[name=titulo]');
			var $tasa = $('#forma-gralimpret-window').find('input[name=tasa]');
			
			var $cta_id = $('#forma-gralimpret-window').find('input[name=cta_id]');
			var $cuenta = $('#forma-gralimpret-window').find('input[name=cuenta]');
			var $scuenta = $('#forma-gralimpret-window').find('input[name=scuenta]');
			var $sscuenta = $('#forma-gralimpret-window').find('input[name=sscuenta]');
			var $ssscuenta = $('#forma-gralimpret-window').find('input[name=ssscuenta]');
			var $sssscuenta = $('#forma-gralimpret-window').find('input[name=sssscuenta]');
			var $descripcion = $('#forma-gralimpret-window').find('input[name=descripcion]');
			
			var $buscar_cta = $('#forma-gralimpret-window').find('#buscar_cta');
			
			//botones                        
			var $cerrar_plugin = $('#forma-gralimpret-window').find('#close');
			var $cancelar_plugin = $('#forma-gralimpret-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-gralimpret-window').find('#submit');
			
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
			
			//permitir solo numeros al campo tasa
			$permitir_solo_numeros($tasa);
			
			if(accion_mode == 'edit'){
				//aqui es el post que envia los datos a getIeps.json
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getImpto.json';
				var $arreglo = {'id':id_to_show,'iu': $('#lienzo_recalculable').find('input[name=iu]').val()};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-gralimpret-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						//$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-gralimpret-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-gralimpret-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//Aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					if(entry['Param']['contab']=='true'){
						$('#forma-gralimpret-window').find('ul.pestanas').find('a[href=#tabx-2]').parent().show();
						
						//Visualizar subcuentas de acuerdo al nivel definido para la empresa
						if(parseInt(entry['Param']['nivel_cta']) >=1 ){ $cuenta.show(); };
						if(parseInt(entry['Param']['nivel_cta']) >=2 ){ $scuenta.show(); };
						if(parseInt(entry['Param']['nivel_cta']) >=3 ){ $sscuenta.show(); };
						if(parseInt(entry['Param']['nivel_cta']) >=4 ){ $ssscuenta.show(); };
						if(parseInt(entry['Param']['nivel_cta']) >=5 ){ $sssscuenta.show(); };
						
						if(parseInt(entry['Cta'].length) > 0 ){
							$cta_id.val(entry['Cta'][0]['id_cta']);
							$cuenta.val(entry['Cta'][0]['cta']);
							$scuenta.val(entry['Cta'][0]['subcta']);
							$sscuenta.val(entry['Cta'][0]['ssubcta']);
							$ssscuenta.val(entry['Cta'][0]['sssubcta']);
							$sssscuenta.val(entry['Cta'][0]['ssssubcta']);
							$descripcion.val(entry['Cta'][0]['descripcion']);
						}
						
						//Busca Cuentas Contables
						$buscar_cta.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(1, entry['Param']['nivel_cta'], entry['CtaMay'], $cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
						});
					};
					
					
					//Aqui van los campos de editar
					$identificador.attr({'value' : entry['Data'][0]['id']});
					$titulo.attr({'value' : entry['Data'][0]['titulo']});
					$tasa.attr({'value' : entry['Data'][0]['tasa']});
										
				 },"json");
				 //Termina llamada json
				 
				$aplica_evento_focus_input_numerico($tasa);
				
				$tasa.blur(function(){
					$validar_numero_puntos($tasa, "Tasa");
					if($(this).val().trim()==''){
						$(this).val(0);
					}
					$(this).val(parseFloat($(this).val()).toFixed(2));
				});
				
				$cuenta.keypress(function(e){
					if(e.which == 13){
						$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
						return false;
					}
				});
				
				$scuenta.keypress(function(e){
					if(e.which == 13){
						$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
						return false;
					}
				});
				
				$sscuenta.keypress(function(e){
					if(e.which == 13){
						$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
						return false;
					}
				});
				
				$ssscuenta.keypress(function(e){
					if(e.which == 13){
						$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
						return false;
					}
				});
				
				$sssscuenta.keypress(function(e){
					if(e.which == 13){
						$getDataCta($cta_id, $cuenta, $scuenta, $sscuenta, $ssscuenta, $sssscuenta, $descripcion);
						return false;
					}
				});
				
				//Aplica funcionalidad para saltar al del campo actual al siguiente y al campo anterior
				$aplica_evento_keypress_input_cta($cuenta, $cuenta, $scuenta, false, true);
				$aplica_evento_keypress_input_cta($scuenta, $cuenta, $sscuenta, true, true);
				$aplica_evento_keypress_input_cta($sscuenta, $scuenta, $ssscuenta, true, true);
				$aplica_evento_keypress_input_cta($ssscuenta, $sscuenta, $sssscuenta, true, true);
				$aplica_evento_keypress_input_cta($sssscuenta, $ssscuenta, $sssscuenta, true, false);
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralimpret-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralimpret-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
				$titulo.focus();
			}
		}
	}
    
    
    
  
                        
   $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllImptos.json';
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        var $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllImptos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        //$.post(input_json,$arreglo,functmodalPanel_pocpedidosion(data){
        $.post(input_json,$arreglo,function(data){
            //pinta_grid
            $.fn.tablaOrdenableEdit(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
});
