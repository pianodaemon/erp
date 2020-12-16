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
	var controller = $contextpath.val()+"/controllers/crmregistrollamadas";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_crmregistrollamadas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Registro de Llamadas');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_tipo_visita = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_tipo_visita]');
	var $busqueda_contacto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_contacto]');
	var $busqueda_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_agente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
        
	var html = '';
	$busqueda_tipo_visita.children().remove();
	html='<option value="0">[-- Todos --]</option>';
	html+='<option value="1">Cliente</option>';
	html+='<option value="2">Prospecto</option>';
	$busqueda_tipo_visita.append(html);
        
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "tipo_visita" + signo_separador + $busqueda_tipo_visita.val() + "|";
		valor_retorno += "contacto" + signo_separador + $busqueda_contacto.val() + "|";
		valor_retorno += "agente" + signo_separador + $busqueda_agente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
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
	
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_folio.val('');
		$busqueda_contacto.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_agente.find('option[index=0]').attr('selected','selected');
		
		$busqueda_tipo_visita.children().remove();
		var tipos_hmtl = '<option value="0" selected="yes">[-- Todos --]</option>';
		tipos_hmtl += '<option value="1">Cliente</option>';
		tipos_hmtl += '<option value="2">Prospecto</option>';
		$busqueda_tipo_visita.append(tipos_hmtl);
		
		//esto se hace para reinicar los valores del select de agentes
		var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
		$arreglo2 = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json2,$arreglo2,function(data){
			//Alimentando los campos select_agente
			$busqueda_agente.children().remove();
			var agente_hmtl = '';
			if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
				agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
			}
			
			$.each(data['Agentes'],function(entryIndex,agente){
				if(parseInt(agente['id'])==parseInt(data['Extra'][0]['id_agente'])){
					agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
				}else{
					//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
					if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
						agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
					}
				}
			});
			$busqueda_agente.append(agente_hmtl);
		});
                
		
	});
	
	
	TriggerClickVisializaBuscador = 0;
	
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		var alto=0;
		if(TriggerClickVisializaBuscador==0){
			 TriggerClickVisializaBuscador=1;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
			 $('#barra_buscador').animate({height: '60px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
			
		}else{
			 TriggerClickVisializaBuscador=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
		};
	});
	
        var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
		//Alimentando los campos select_agente
		$busqueda_agente.children().remove();
		var agente_hmtl = '';
		if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
			agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
		}
		
		$.each(data['Agentes'],function(entryIndex,agente){
			if(parseInt(agente['id'])==parseInt(data['Extra'][0]['id_agente'])){
				agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
			}else{
				//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
				if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
					agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
				}
			}
		});
		$busqueda_agente.append(agente_hmtl);
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
		$('#forma-registro-window').find('#submit').mouseover(function(){
			$('#forma-registro-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-registro-window').find('#submit').mouseout(function(){
			$('#forma-registro-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-registro-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-registro-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-registro-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-registro-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-registro-window').find('#close').mouseover(function(){
			$('#forma-registro-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-registro-window').find('#close').mouseout(function(){
			$('#forma-registro-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-registro-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-registro-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-registro-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-registro-window').find("ul.pestanas li").click(function() {
			$('#forma-registro-window').find(".contenidoPes").hide();
			$('#forma-registro-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-registro-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	/*funcion para colorear la fila en la que pasa el puntero*/
	$colorea_tr_grid = function($tabla){
		$tabla.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
		$tabla.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});
		
		$('tr:odd' , $tabla).hover(function () {
			$(this).find('td').css({background : '#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#e7e8ea'});
		});
		$('tr:even' , $tabla).hover(function () {
			$(this).find('td').css({'background-color':'#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#FFFFFF'});
		});
	};
        
	
	//buscador de Contactos
	$busca_contactos = function(busqueda_inicial ){
		//limpiar_campos_grids();
		$(this).modalPanel_BuscaContacto();
		var $dialogoc =  $('#forma-buscacontactos-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_contactos').find('table.formaBusqueda_contactos').clone());
		
		$('#forma-buscacontactos-window').css({"margin-left": -180, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscacontactos-window').find('#tabla_resultado');
		
		var $campo_buscador_nombre = $('#forma-buscacontactos-window').find('input[name=buscador_nombre]');
		var $campo_buscador_apellidop = $('#forma-buscacontactos-window').find('input[name=buscador_apellidop]');
		var $campo_buscador_apellidom = $('#forma-buscacontactos-window').find('input[name=buscador_apellidom]');
		var $select_buscador_tipo_contacto = $('#forma-buscacontactos-window').find('select[name=buscador_tipo_contacto]');
		
		var $buscar_plugin_contacto = $('#forma-buscacontactos-window').find('#busca_contacto_modalbox');
		var $cancelar_plugin_busca_contacto = $('#forma-buscacontactos-window').find('#cencela');
		
		//funcionalidad botones
		$buscar_plugin_contacto.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_contacto.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_contacto.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_contacto.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		$campo_buscador_nombre.val(busqueda_inicial);
		
		//click buscar productos
		$buscar_plugin_contacto.click(function(event){
                    
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_contactos.json';
			$arreglo = {'buscador_nombre':$campo_buscador_nombre.val(),'buscador_apellidop':$campo_buscador_apellidop.val(),
			'buscador_apellidom':$campo_buscador_apellidom.val(),'buscador_tipo_contacto':$select_buscador_tipo_contacto.val(),'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				
				$.each(entry['contactos'],function(entryIndex,prospecto){
					trr = '<tr>';
						trr += '<td width="280px">';
							trr += '<span class="contacto_buscador">'+prospecto['contacto']+'</span>';
							trr += '<input type="hidden" id="id_contacto_buscador" value="'+prospecto['id']+'">';
						trr += '</td>';
						trr += '<td width="210px"><span class="razon_social_buscador">'+prospecto['razon_social']+'</span></td>';
						trr += '<td width="110px"><span class="rfc_buscador">'+prospecto['rfc']+'</span></td>';
					trr += '</tr>';
					$tabla_resultados.append(trr);
				});
				
				$colorea_tr_grid($tabla_resultados);
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					var id_contacto=$(this).find('#id_contacto_buscador').val();
					var contacto_buscador=$(this).find('span.contacto_buscador').html();
					var razon_social_buscador=$(this).find('span.razon_social_buscador').html();
					var rfc_buscador=$(this).find('span.rfc_buscador').html();
					
					$('#forma-registro-window').find('input[name=id_contacto]').val(id_contacto);
					$('#forma-registro-window').find('input[name=contacto]').val(contacto_buscador);
					
					//oculta la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacontactos-overlay').fadeOut(remove);
				});
			});
		});
	
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if(busqueda_inicial != ''){
			$buscar_plugin_contacto.trigger('click');
		}
		
		$cancelar_plugin_busca_contacto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacontactos-overlay').fadeOut(remove);
		});
                
	}//termina buscador de Contactos
	

	//nuevo
	$new_crmregistrollamadas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel();
		
		var form_to_show = 'formaCrmRegistroLlamadas';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-registro-window').css({"margin-left": -400, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-registro-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-registro-window').find('input[name=identificador_prospecto]');
		var $folio = $('#forma-registro-window').find('input[name=ncontrol]');
                var $id_contacto = $('#forma-registro-window').find('input[name=id_contacto]');
		var $select_agente = $('#forma-registro-window').find('select[name=select_agente]');
		var $contacto = $('#forma-registro-window').find('input[name=contacto]');
		var $busca_contacto = $('#forma-registro-window').find('#busca_contacto');
		var $fecha =$('#forma-registro-window').find('input[name=fecha_registro]');
		var $hora_registro =$('#forma-registro-window').find('input[name=hora_registro]');
		var $hora_duracion =$('#forma-registro-window').find('input[name=duracion_llamada]');
		var $select_motivo_llamada =$('#forma-registro-window').find('select[name=select_motivo_llamada]');
                var $select_llamada_completa =$('#forma-registro-window').find('select[name=select_llamada_completa]');
		var $select_calif_llamada  =$('#forma-registro-window').find('select[name=select_calificacion]');
		var $select_tipo_seguimiento =$('#forma-registro-window').find('select[name=select_tipo_seguimiento]');
		var $select_oportunidad =$('#forma-registro-window').find('select[name=select_consiguio_cita]');
                var $select_tipo_llamada =$('#forma-registro-window').find('select[name=select_tipo_llamada]');
		var $select_llamda_planeada =$('#forma-registro-window').find('select[name=select_llamada_planeada]');
		//variables de observaciones y comentarios
		var $resultado_llamada   =$('#forma-registro-window').find('textarea[name=resultado_llamada]');
		var $observaciones_visita  =$('#forma-registro-window').find('textarea[name=comentarios]');
                
                var $comentarios_proxima_llamada =$('#forma-registro-window').find('textarea[name=observaciones');
		
		var $fecha_proxima_llamada =$('#forma-registro-window').find('input[name=fecha_cita_proxima]');
		var $hora_proxima_llamada =$('#forma-registro-window').find('input[name=hora_prox_cita]');
		
		
		var $cerrar_plugin = $('#forma-registro-window').find('#close');
		var $cancelar_plugin = $('#forma-registro-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-registro-window').find('#submit');
		
		$folio.css({'background' : '#DDDDDD'});
		$identificador.attr({'value' : 0});
		$id_contacto.attr({'value' : 0});
		$hora_registro.attr({'value' : '00:00'});
		$hora_duracion.attr({'value' : '00:00'});
		$hora_proxima_llamada.attr({'value' : '00:00'});
                
               
		var respuestaProcesada = function(data){
                     
			if ( data['success'] == "true" ){
				jAlert("El Registro de Llamada se registr&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-registro-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-registro-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-registro-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroLlamada.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			
			$select_agente.children().remove();
			var agente_hmtl = '';
			if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
				agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
			}
			
			$.each(entry['Agentes'],function(entryIndex,agente){
				if(parseInt(agente['id'])==parseInt(entry['Extra'][0]['id_agente'])){
					agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
				}else{
					//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
					if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
						agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
					}
				}
			});
			$select_agente.append(agente_hmtl);
                        
                        //Carga select motivo llamada
              
			$select_motivo_llamada.children().remove();
                       
			var motivos_llamada_html = '<option value="0" selected="yes">--Seleccione Motivo de Llamada--</option>';
			$.each(entry['MotivosLlamadas'],function(entryIndex,motivo){
				motivos_llamada_html += '<option value="' + motivo['id'] + '"  >' + motivo['descripcion'] + '</option>';
			});
			$select_motivo_llamada.append(motivos_llamada_html);
		
                        //carga las calificaciones
			$select_calif_llamada.children().remove();
                        
			var calificacion_llamada_html = '<option value="0" selected="yes">--Seleccione Calificaci&oacute;n--</option>';
			$.each(entry['CalificacionLlamadas'],function(entryIndex,cal){
				calificacion_llamada_html += '<option value="' + cal['id'] + '"  >' + cal['titulo'] + '</option>';
			});
			$select_calif_llamada.append(calificacion_llamada_html);
		
		
                        //carga select de tipo de seguimiento
           
			$select_tipo_seguimiento.children().remove();
                        
			var tipo_seguimiento_html = '<option value="0" selected="yes">--Seleccione Tipo de Seguimiento--</option>';
			$.each(entry['TipoSeguimiento'],function(entryIndex,seguimieto){
				tipo_seguimiento_html += '<option value="' + seguimieto['id'] + '"  >' + seguimieto['titulo'] + '</option>';
			});
			$select_tipo_seguimiento.append(tipo_seguimiento_html);
                        
			
			//Alimentando los campos select de oportunidad
			$select_oportunidad.children().remove();
			var oportunidad_hmtl = '<option value="1" selected="yes">SI</option>';
			oportunidad_hmtl += '<option value="0">NO</option>';
			$select_oportunidad.append(oportunidad_hmtl);
                        
                        //Alimentando los campos select de llamda completada
			$select_llamada_completa.children().remove();
			var llamcompleta_hmtl = '<option value="1" selected="yes">SI</option>';
			llamcompleta_hmtl += '<option value="0">NO</option>';
			$select_llamada_completa.append(llamcompleta_hmtl);
                        
                        $select_tipo_llamada.children().remove();
                        var tipo_llamada_html ='<option value="0" selected="yes">Entrante</option>';
                        tipo_llamada_html +='<option value="1">Saliente</option>';
                        $select_tipo_llamada.append(tipo_llamada_html);
                        
                        $select_llamda_planeada.children().remove();
                        var llamada_planeada ='<option value="1" selected="yes">SI</option>';
                        llamada_planeada +='<option value="0">NO</option>';
                        $select_llamda_planeada.append(llamada_planeada);
                        
			
		},"json");//termina llamada json
        
        //$('.input1').TimepickerInputMask();
        
        $hora_registro.TimepickerInputMask();
        $hora_duracion.TimepickerInputMask();
        $hora_proxima_llamada.TimepickerInputMask();
        
        //fecha de la visita
		$fecha.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
			
		$fecha.DatePicker({
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
				$fecha.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha.val(mostrarFecha());
					}else{
						$fecha.DatePickerHide();	
					}
				}
			}
		});
		
			
        
        //fecha para la proxima visita
		$fecha_proxima_llamada.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_proxima_llamada.DatePicker({
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
				$fecha_proxima_llamada.val(formated);
				/*if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_proxima_llamada.val(),mostrarFecha());
					
					if (valida_fecha==true){
						$fecha_proxima_llamada.DatePickerHide();	
					}else{
						jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
						$fecha_proxima_llamada.val(mostrarFecha());
					}
				}*/
			}
		});
        
        
        
        //buscar contacto
        $busca_contacto.click(function(event){
			event.preventDefault();
			$busca_contactos($contacto.val());
        });
        
        
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-registro-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-registro-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});		
	});
	
	
	
	
	
	var carga_formaDirecciones_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Registro de Llamada seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("EL Registro de Llamada fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Registro de Llamada no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaCrmRegistroLlamadas';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel();
			$('#forma-registro-window').css({"margin-left": -400, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-registro-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $identificador = $('#forma-registro-window').find('input[name=identificador_prospecto]');
                        var $folio = $('#forma-registro-window').find('input[name=ncontrol]');
                        var $id_contacto = $('#forma-registro-window').find('input[name=id_contacto]');
                        var $select_agente = $('#forma-registro-window').find('select[name=select_agente]');
                        var $contacto = $('#forma-registro-window').find('input[name=contacto]');
                        var $busca_contacto = $('#forma-registro-window').find('#busca_contacto');
                        var $fecha =$('#forma-registro-window').find('input[name=fecha_registro]');
                        var $hora_registro =$('#forma-registro-window').find('input[name=hora_registro]');
                        var $hora_duracion =$('#forma-registro-window').find('input[name=duracion_llamada]');
                        var $select_motivo_llamada =$('#forma-registro-window').find('select[name=select_motivo_llamada]');
                        var $select_llamada_completa =$('#forma-registro-window').find('select[name=select_llamada_completa]');
                       
                        var $select_calif_llamada  =$('#forma-registro-window').find('select[name=select_calificacion]');
                        var $select_tipo_seguimiento =$('#forma-registro-window').find('select[name=select_tipo_seguimiento]');
                        var $select_oportunidad =$('#forma-registro-window').find('select[name=select_consiguio_cita]');
                        var $select_tipo_llamada =$('#forma-registro-window').find('select[name=select_tipo_llamada]');
                        var $select_llamda_planeada =$('#forma-registro-window').find('select[name=select_llamada_planeada]');
                        
                        //variables de observaciones y comentarios
                        var $resultado_llamada   =$('#forma-registro-window').find('textarea[name=resultado_llamada]');
                        var $observaciones_visita  =$('#forma-registro-window').find('textarea[name=comentarios]');
                        var $comentarios_proxima_llamada =$('#forma-registro-window').find('textarea[name=observaciones]');

                        var $fecha_proxima_llamada =$('#forma-registro-window').find('input[name=fecha_cita_proxima]');
                        var $hora_proxima_llamada =$('#forma-registro-window').find('input[name=hora_prox_cita]');
                        
			var $cerrar_plugin = $('#forma-registro-window').find('#close');
			var $cancelar_plugin = $('#forma-registro-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-registro-window').find('#submit');
			
			
			$folio.css({'background' : '#DDDDDD'});
			$hora_registro.attr({'value' : '00:00'});
			$hora_duracion.attr({'value' : '00:00'});
			$hora_proxima_llamada.attr({'value' : '00:00'});
			//$busca_contacto.hide();
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroLlamada.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("El Registro de Llamada se Actualiz&oacute; con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-registro-overlay').fadeOut(remove);
						//refresh_table();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-registro-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-registro-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
                                    
                                        
					$identificador.attr({'value' : entry['Datos']['0']['id']});
					$folio.attr({'value' : entry['Datos']['0']['folio']});
					
					$id_contacto.attr({'value' : entry['Datos']['0']['contacto_id']});
					$contacto.attr({'value' : entry['Datos']['0']['nombre_contacto']});

					$fecha.attr({'value' : entry['Datos']['0']['fecha']});
					
                                        $hora_registro.attr({'value' : entry['Datos']['0']['hora']});
					$hora_registro.TimepickerInputMask();
					
                                        $hora_duracion.attr({'value' : entry['Datos']['0']['duracion']});
					$hora_duracion.TimepickerInputMask();
				
					$comentarios_proxima_llamada.text(entry['Datos']['0']['comentarios_sig_llamada']);
					$resultado_llamada.text(entry['Datos']['0']['resultado']);
					$observaciones_visita.text(entry['Datos']['0']['observaciones']);
					
					$fecha_proxima_llamada.attr({'value' : entry['Datos']['0']['fecha_sig_llamada']});
					$hora_proxima_llamada.attr({'value' : entry['Datos']['0']['hora_sig_llamada']});
					$hora_proxima_llamada.TimepickerInputMask();
					
					
					//Alimentando los campos select_agente
					$select_agente.children().remove();
					var agente_hmtl='';
					$.each(entry['Agentes'],function(entryIndex,agente){
						if(parseInt(agente['id'])==parseInt(entry['Datos'][0]['empleado_id'])){
							agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
						}else{
							agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
						}
					});
					$select_agente.append(agente_hmtl);
					
					//Alimentando los campos select de Motivos
					$select_motivo_llamada.children().remove();
					var motivo_hmtl = '';
					$.each(entry['MotivosLlamadas'],function(entryIndex,motivo){
						if(parseInt(motivo['id'])==parseInt(entry['Datos'][0]['crm_motivos_llamda_id'])){
							motivo_hmtl += '<option value="' + motivo['id'] + '" selected="yes">' + motivo['descripcion'] + '</option>';
						}else{
							motivo_hmtl += '<option value="' + motivo['id'] + '"  >' + motivo['descripcion'] + '</option>';
						}
					});
					$select_motivo_llamada.append(motivo_hmtl);
					
					//Alimentando los campos select de Calificaciones de Visita
					$select_calif_llamada.children().remove();
					var calif_hmtl = '';
					$.each(entry['CalificacionLlamadas'],function(entryIndex,calif){
						if(parseInt(calif['id'])==parseInt(entry['Datos'][0]['calificacion_id'])){
							calif_hmtl += '<option value="' + calif['id'] + '" selected="yes">' + calif['titulo'] + '</option>';
						}else{
							calif_hmtl += '<option value="' + calif['id'] + '"  >' + calif['titulo'] + '</option>';
						}
					});
					$select_calif_llamada.append(calif_hmtl);
					
					//Alimentando los campos select de Seguimiento
					$select_tipo_seguimiento.children().remove();
					var seguimiento_hmtl = '';
					$.each(entry['TipoSeguimiento'],function(entryIndex,seg){
						if(parseInt(seg['id'])==parseInt(entry['Datos'][0]['seguimiento_id'])){
							seguimiento_hmtl += '<option value="' + seg['id'] + '" selected="yes">' + seg['titulo'] + '</option>';
						}else{
							seguimiento_hmtl += '<option value="' + seg['id'] + '"  >' + seg['titulo'] + '</option>';
						}
					});
					$select_tipo_seguimiento.append(seguimiento_hmtl);
					
					
                                        //Alimentando los campos select de oportunidad
					$select_oportunidad.children().remove();
					var oportunidad_hmtl ='';
					if(parseInt(entry['Datos'][0]['deteccion_oportunidad']) == 1){
						oportunidad_hmtl= '<option value="1" selected="yes">SI</option>';
						oportunidad_hmtl += '<option value="0">NO</option>';
					}else{
						oportunidad_hmtl= '<option value="1">SI</option>';
						oportunidad_hmtl += '<option value="0" selected="yes">NO</option>';
					}
					$select_oportunidad.append(oportunidad_hmtl);
                                        
                                        //alimenta el select de llamada planeada
                                        $select_llamda_planeada.children().remove();
                                        var llamda_comp_html ="";
                                        if(parseInt(entry['Datos'][0]['llamada_planeada'])==1){
                                            llamda_comp_html +='<option value="1" selected="yes">SI</option>';
                                            llamda_comp_html +='<option value="0">NO</option>';
                                        }else{
                                            llamda_comp_html +='<option value="1">SI</option>';
                                            llamda_comp_html +='<option value="0" selected="yes">NO</option>'; 
                                        }
					$select_llamda_planeada.append(llamda_comp_html);
                                        
                                        //aliemtenta select de tipo de llamada
                                        $select_tipo_llamada.children().remove();
                                        var tipo_llamada_html ="";
                                        //alert(entry['Datos'][0]['tipo_llamada']);
                                        if(parseInt(entry['Datos'][0]['tipo_llamada']) == 1){
                                            tipo_llamada_html += '<option value="0">Entrante</option>';
                                            tipo_llamada_html += '<option value="1" selected="yes">Saliente</option>';
                                        }else{
                                            tipo_llamada_html += '<option value="0" selected="yes">Entrante</option>';
                                            tipo_llamada_html += '<option value="1" >Saliente</option>';
                                        }
                                        $select_tipo_llamada.append(tipo_llamada_html);
                                        
                                        //alimentando el select de llamada completa
                                        $select_llamada_completa.children().remove();
                                        var llamadacom_html="";
                                        if (parseInt(entry['Datos'][0]['llamada_completada'])==1){
                                            llamadacom_html+='<option value="1" selected="yes">SI</option>';
                                            llamadacom_html+='<option value="0">NO</option>';
                                        }else{
                                            llamadacom_html+='<option value="1">SI</option>';
                                            llamadacom_html+='<option value="0" selected="yes">NO</option>';
                                        }
                                        
                                       $select_llamada_completa.append(llamadacom_html);
                                        
				},"json");//termina llamada json
				
				
				//buscar contacto
				$busca_contacto.click(function(event){
					event.preventDefault();
					$busca_contactos($contacto.val());
				});
				
				
				//fecha de la visita
				$fecha.click(function (s){
					var a=$('div.datepicker');
					a.css({'z-index':100});
				});
					
				$fecha.DatePicker({
					format:'Y-m-d',
					date: $fecha.val(),
					current: $fecha.val(),
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
						$fecha.val(formated);
						/*if (formated.match(patron) ){
							var valida_fecha=mayor($fecha.val(),mostrarFecha());
							
							if (valida_fecha==true){
								jAlert("Fecha no valida",'! Atencion');
								$fecha.val(mostrarFecha());
							}else{
								$fecha.DatePickerHide();	
							}
						}*/
					}
				});
				
					
				
				//fecha para la proxima visita
				$fecha_proxima_llamada.click(function (s){
					var a=$('div.datepicker');
					a.css({'z-index':100});
				});
				
				$fecha_proxima_llamada.DatePicker({
					format:'Y-m-d',
					date: $fecha_proxima_llamada.val(),
					current: $fecha_proxima_llamada.val(),
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
						$fecha_proxima_llamada.val(formated);
						/*if (formated.match(patron) ){
							var valida_fecha=mayor($fecha_proxima_llamada.val(),mostrarFecha());
							
							if (valida_fecha==true){
								$fecha_proxima_llamada.DatePickerHide();	
							}else{
								jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
								$fecha_proxima_llamada.val(mostrarFecha());
							}
						}*/
					}
				});
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-registro-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-registro-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllRegistroLlamadas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllRegistroLlamadas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
