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
	var controller = $contextpath.val()+"/controllers/ctbgenerapolizas";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseover(function(){
		$(this).removeClass("onmouseOutNewItem").addClass("onmouseOverNewItem");
	});
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseout(function(){
		$(this).removeClass("onmouseOverNewItem").addClass("onmouseOutNewItem");
	});
	
	$('#barra_titulo').find('#td_titulo').append(document.title);
	
	$tabs_li_funxionalidad = function(){
		$('#forma-ctbgenerapolizas-window').find('#submit').mouseover(function(){
			$('#forma-ctbgenerapolizas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-ctbgenerapolizas-window').find('#submit').mouseout(function(){
			$('#forma-ctbgenerapolizas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-ctbgenerapolizas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-ctbgenerapolizas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-ctbgenerapolizas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-ctbgenerapolizas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-ctbgenerapolizas-window').find('#close').mouseover(function(){
			$('#forma-ctbgenerapolizas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-ctbgenerapolizas-window').find('#close').mouseout(function(){
			$('#forma-ctbgenerapolizas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-ctbgenerapolizas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-ctbgenerapolizas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-ctbgenerapolizas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-ctbgenerapolizas-window').find("ul.pestanas li").click(function() {
			$('#forma-ctbgenerapolizas-window').find(".contenidoPes").hide();
			$('#forma-ctbgenerapolizas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-ctbgenerapolizas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	//Generar tr para agregar al grid
	var $agrega_tr = function($grid_movs, fecha, referencia, descripcion, total, tipo_mov){
		//var noTr = $("tr", $grid_movs).size();
		//noTr++;
		
		var trr = '';
		trr = '<tr>';
			trr += '<td class="grid1" style="" width="70">'+ fecha +'</td>';
			trr += '<td class="grid1" width="70">'+ referencia +'</td>';
			trr += '<td class="grid1" width="300">'+ descripcion +'</td>';
			trr += '<td class="grid2" width="80">'+ $(this).agregar_comas(total) +'</td>';
			trr += '<td class="grid1" width="185">'+ tipo_mov +'</td>';
		trr += '</tr>';
		$grid_movs.append(trr);

	}
	
	
		

	
	//Nuevo 
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_ctbgenerapolizas();
		
		var form_to_show = 'formactbgenerapolizas00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-ctbgenerapolizas-window').css({ "margin-left": -400, 	"margin-top": -265 });
		$forma_selected.prependTo('#forma-ctbgenerapolizas-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $identificador = $('#forma-ctbgenerapolizas-window').find('input[name=identificador]');
		var $fecha_ini = $('#forma-ctbgenerapolizas-window').find('input[name=fecha_ini]');
		var $fecha_fin = $('#forma-ctbgenerapolizas-window').find('input[name=fecha_fin]');
		var $select_tipo_mov = $('#forma-ctbgenerapolizas-window').find('select[name=select_tipo_mov]');
		var $buscar = $('#forma-ctbgenerapolizas-window').find('#buscar');
		
		//Grid de movimientos
		var $grid_movs = $('#forma-ctbgenerapolizas-window').find('#grid_movs');
		var $grid_warning = $('#forma-ctbgenerapolizas-window').find('#grid_warning');
		
		var $cerrar_plugin = $('#forma-ctbgenerapolizas-window').find('#close');
		var $cancelar_plugin = $('#forma-ctbgenerapolizas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-ctbgenerapolizas-window').find('#submit');
		var $boton_actualizar = $('#forma-ctbgenerapolizas-window').find('#boton_actualizar');
		
		$identificador.attr({ 'value' : 0 });
		
		//quitar enter a todos los campos input
		$('#forma-ctbgenerapolizas-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Cuenta fue dada de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-ctbgenerapolizas-overlay').fadeOut(remove);
				//refresh_table();
				//$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-ctbgenerapolizas-window').find('div.interrogacion').css({'display':'none'});
				$('#forma-ctbgenerapolizas-window').find('.ctbgenerapolizas_div_one').css({'height':'620px'});
				
				$grid_cuentas.find('select').css({'background' : '#ffffff'});
				$grid_cuentas.find('input').css({'background' : '#ffffff'});
				
				$('#forma-ctbgenerapolizas-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-ctbgenerapolizas-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-ctbgenerapolizas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
						
						if((tmp.split(':')[0].substring(0, 11) == 'select_tmov') || (tmp.split(':')[0].substring(0, 3) == 'cta') || (tmp.split(':')[0].substring(0, 4) == 'debe') || (tmp.split(':')[0].substring(0, 4) == 'haber')){
							var campo = tmp.split(':')[0];
							
							$('#forma-ctbgenerapolizas-window').find('#div_warning_grid').css({'display':'block'});
							var $campo = $grid_cuentas.find('#'+campo).css({'background' : '#d41000'});
							
							var cta = $campo.parent().parent().find('input[name=cta]').val();
							var descripcion_cta = $campo.parent().parent().find('input[name=descripcion_cta]').val();
							
							var tr_warning = '<tr>';
									tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
									tr_warning += '<td width="150"><INPUT TYPE="text" value="' + cta + '" class="borde_oculto" readOnly="true" style="width:150px; color:red"></td>';
									tr_warning += '<td width="250"><INPUT TYPE="text" value="' + descripcion_cta + '" class="borde_oculto" readOnly="true" style="width:250px; color:red"></td>';
									tr_warning += '<td width="380"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:350px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-ctbgenerapolizas-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
					}
				}
				$('#forma-ctbgenerapolizas-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
				$('#forma-ctbgenerapolizas-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getData.json';
		var $arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			
			//Carga select de Tipos de Movimientos
			$select_tipo_mov.children().remove();
			var tp_hmtl = '<option value="0">[--Todos--]</option>';
			$.each(entry['TM'],function(entryIndex,tp){
				tp_hmtl += '<option value="'+ tp['id'] +'">'+ tp['titulo'] +'</option>';
			});
			$select_tipo_mov.append(tp_hmtl);
			
		},"json");
		//Termina llamada json
		
		
		
		//Buscador de Movimientos
		$buscar.click(function(event){
			event.preventDefault();
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBusqueda.json';
			var $arreglo = {
				'fecha_ini':$fecha_ini.val(),
				'fecha_fin':$fecha_fin.val(),
				'tipo_mov':$select_tipo_mov.val(),
				'iu': $('#lienzo_recalculable').find('input[name=iu]').val() 
			};
			
			var buscar_registro=false;
			
			$.post(input_json,$arreglo,function(entry){
				
				//Eliminar registros
				$grid_movs.children().remove();
				
				$.each(entry['Data'],function(entryIndex, fila){
					$agrega_tr($grid_movs, fila['fecha'], fila['referencia'], fila['descripcion'], fila['total'], fila['tipo_mov']);
				});
			});
		});
		
		//Aplicar evento Keypress para que al pulsar enter ejecute la busqueda
		$(this).aplicarEventoKeypressEjecutaTrigger($fecha_ini, $buscar);
		$(this).aplicarEventoKeypressEjecutaTrigger($fecha_fin, $buscar);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_mov, $buscar);
		
		
		$fecha_ini.val(mostrarFecha());
		
		$fecha_ini.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
			
		$fecha_ini.DatePicker({
			format:'Y-m-d',
			date: $fecha_ini.val(),
			current: $fecha_ini.val(),
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
				$fecha_ini.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_ini.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_ini.val(mostrarFecha());
					}else{
						$fecha_ini.DatePickerHide();	
					}
				}
			}
		});
		
		
		$fecha_fin.val(mostrarFecha());
		
		$fecha_fin.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_fin.DatePicker({
			format:'Y-m-d',
			date: $fecha_fin.val(),
			current: $fecha_fin.val(),
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
				$fecha_fin.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_fin.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_fin.val(mostrarFecha());
					}else{
						$fecha_fin.DatePickerHide();	
					}
				}
			}
		});
		
		
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-ctbgenerapolizas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-ctbgenerapolizas-overlay').fadeOut(remove);
		});
	});
	
});
