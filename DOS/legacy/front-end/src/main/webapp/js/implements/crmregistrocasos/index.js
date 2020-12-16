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
	var controller = $contextpath.val()+"/controllers/crmregistrocasos";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_crmregistrocasos = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Registro de Casos');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_tipo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_tipo]');
        var $busqueda_id_cliente_prospecto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_id_cliente_prospecto]');
        var $busqueda_cliente_prospecto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente_prospecto]');
        var $busqueda_cliente_prospecto_href =$('#barra_buscador').find('.tabla_buscador').find('#busca_cliente_prospecto');
        
        var $busqueda_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_agente]');
        
	var $busqueda_select_prioridad = $('#barra_buscador').find('.tabla_buscador').find('select[name=buscador_select_prioridad]');
	var $busqueda_fecha_cierre = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_cierre]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
        var html = '';
	$busqueda_tipo.children().remove();
	html='<option value="0">[-- Todos --]</option>';
	html+='<option value="1">Cliente</option>';
	html+='<option value="2">Prospecto</option>';
	$busqueda_tipo.append(html);
        
        var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "tipo" + signo_separador + $busqueda_tipo.val() + "|";
		valor_retorno += "id_cliente_prospecto" + signo_separador + $busqueda_id_cliente_prospecto.val() + "|";
                valor_retorno += "cliente_prospecto" + signo_separador + $busqueda_cliente_prospecto.val() + "|";
		valor_retorno += "prioridad" + signo_separador + $busqueda_select_prioridad.val() + "|";
		valor_retorno += "fecha_cierre" + signo_separador + $busqueda_fecha_cierre.val() + "|";
                valor_retorno += "agente" + signo_separador + $busqueda_agente.val() + "|";
		
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		return valor_retorno;
	};
	
        
        
        //buscar cliente_prospecto
        $busqueda_cliente_prospecto_href.click(function(event){
            event.preventDefault();
            var  valor="";
            if($busqueda_tipo.val() != 0 ){
                 if($busqueda_tipo.val()== 1){
                     valor= 'Cliente';
                 }else{
                     valor= 'Prospecto';
                 }
                     
                if($busqueda_cliente_prospecto.val() != '' ){
                    var nivel_ejecucion=1;
                    $Pluguin_cliente_prospecto($busqueda_cliente_prospecto.val(),$busqueda_tipo.val(),nivel_ejecucion);
                }else{
                    
                    jAlert("Ingrese un"+valor,'Atencion!!!');
                }
            }else{
                
                jAlert("elige un Caso",'! Atencion');
            }
        });
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
                var html_tipo = '';
                var html_prioridad = '';
                $busqueda_folio.val('');
		$busqueda_cliente_prospecto.val('');
                $busqueda_id_cliente_prospecto.val(0);
		$busqueda_fecha_cierre.val('');
                $busqueda_agente.find('option[index=0]').attr('selected','selected');
                
                $busqueda_tipo.children().remove();
		$busqueda_select_prioridad.children().remove();
                
                    html_tipo='<option value="0" selected="yes">[-- Todos --]</option>';
                    html_tipo+='<option value="1">Cliente</option>';
                    html_tipo+='<option value="2">Prospecto</option>';
                    
		$busqueda_tipo.append(html_tipo);
                
                    html_prioridad='<option value="0" selected="yes">[-- ninguna --]</option>';
                    html_prioridad+='<option value="1">Muy urgente</option>';
                    html_prioridad+='<option value="2">Urgente</option>';
                    html_prioridad+='<option value="3">Importante</option>';
                    html_prioridad+='<option value="4">Normal</option>';
                    html_prioridad+='<option value="5">Baja</option>';
               
               $busqueda_select_prioridad.append(html_prioridad);
               
                
		
               
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


	
	
        
        
	$busqueda_fecha_cierre.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
	$busqueda_fecha_cierre.DatePicker({
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
			$busqueda_fecha_cierre.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($busqueda_fecha_cierre.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$busqueda_fecha_cierre.val(mostrarFecha());
				}else{
					$busqueda_fecha_cierre.DatePickerHide();	
				}
			}
		}
	});
        
	$busqueda_fecha_cierre.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
	
	
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-crmregistrocasos-window').find('#submit').mouseover(function(){
			$('#forma-crmregistrocasos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-crmregistrocasos-window').find('#submit').mouseout(function(){
			$('#forma-crmregistrocasos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-crmregistrocasos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-crmregistrocasos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-crmregistrocasos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-crmregistrocasos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-crmregistrocasos-window').find('#close').mouseover(function(){
			$('#forma-crmregistrocasos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-crmregistrocasos-window').find('#close').mouseout(function(){
			$('#forma-crmregistrocasos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-crmregistrocasos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-crmregistrocasos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-crmregistrocasos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-crmregistrocasos-window').find("ul.pestanas li").click(function() {
			$('#forma-crmregistrocasos-window').find(".contenidoPes").hide();
			$('#forma-crmregistrocasos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-crmregistrocasos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
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
	$Pluguin_cliente_prospecto = function(busqueda_inicial ,buscado_por,nivel_ejecucion){
		//limpiar_campos_grids();
                $(this).modalPanel_BuscaCliente_prospecto();
		var $dialogoc =  $('#forma-buscacliente_prospecto-window');
                
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes_prospectos').find('table.formaBusqueda_cliente_prospecto').clone());
		                                                                   
              // alert($dialogoc.append($('div.buscador_clientes_prospectos').find('table.formaBusqueda_cliente_prospecto')).html());
               
		$('#forma-buscacliente_prospecto-window').css({"margin-left": -180, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscacliente_prospecto-window').find('#tabla_resultado');
		 
                var $cliente_prospecto = $('#forma-crmregistrocasos-window').find('input[name=cliente_prospecto]');
                var $id_cliente_prospecto = $('#forma-crmregistrocasos-window').find('input[name=id_cliente_prospecto]');
		
                if(nivel_ejecucion == 1){
                    var $busqueda_id_cliente_prospecto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_id_cliente_prospecto]');
                    var $busqueda_cliente_prospecto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente_prospecto]');
                }
		
                
                var $campo_buscador_razon_social = $('#forma-buscacliente_prospecto-window').find('input[name=buscador_razon_social]');
		var $campo_buscador_rfc = $('#forma-buscacliente_prospecto-window').find('input[name=buscador_rfc]');
		
               
		
		var $buscar_plugin_contacto = $('#forma-buscacliente_prospecto-window').find('#busca_contacto_modalbox');
		var $cancelar_plugin_busca_contacto = $('#forma-buscacliente_prospecto-window').find('#cencela');
		
                
                
                $campo_buscador_razon_social.val(busqueda_inicial);
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
		
		$cliente_prospecto.val(busqueda_inicial);
		
		//click buscar productos
		$buscar_plugin_contacto.click(function(event){
                    
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_cliente_prospecto.json';
			$arreglo = {'buscador_razon_social':$campo_buscador_razon_social.val(),'buscador_rfc':$campo_buscador_rfc.val(),
			'identificador_cliente_prospecto':buscado_por,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				
				$.each(entry['array_cliente_prospecto'],function(entryIndex,prospecto){
					trr = '<tr>';
						trr += '<td width="280px">';
							trr += '<span class="cliente_prospecto">'+prospecto['numero_control']+'</span>';
							trr += '<input type="hidden" id="id_cliente" value="'+prospecto['id']+'">';
						trr += '</td>';
						trr += '<td width="210px"><span class="razon_social_buscador">'+prospecto['razon_social']+'</span></td>';
						trr += '<td width="110px"><span class="rfc_buscador">'+prospecto['rfc']+'</span></td>';
					trr += '</tr>';
					$tabla_resultados.append(trr);
				});
				
				$colorea_tr_grid($tabla_resultados);
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					var id_cliente_prospecto=$(this).find('#id_cliente').val();
					
					var razon_social_buscador=$(this).find('span.razon_social_buscador').html();
					var rfc_buscador=$(this).find('span.rfc_buscador').html();
					
                                        if(nivel_ejecucion == 1){
                                            $busqueda_id_cliente_prospecto.val(id_cliente_prospecto);
                                            $busqueda_cliente_prospecto.val(razon_social_buscador);
                                        }
                                        
                                        $cliente_prospecto.val(razon_social_buscador);
                                        $id_cliente_prospecto.val(id_cliente_prospecto);
					//oculta la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente_prospecto-overlay').fadeOut(remove);
				});
			});
		});
	
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_buscador_razon_social.val() != ''){
                     $buscar_plugin_contacto.trigger('click');
		}
		
		$cancelar_plugin_busca_contacto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente_prospecto-overlay').fadeOut(remove);
		});
                
	}//termina buscador de Cliente_Prospecto
	
	
	
	
	
	//nuevo
	$new_crmregistrocasos.click(function(event){
		event.preventDefault();
                
		var id_to_show = 0;
		$(this).modalPanel_crmregistrocasos();
                        
		
		var form_to_show = 'formacrmregistrocasos';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-crmregistrocasos-window').css({"margin-left": -400, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-crmregistrocasos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-crmregistrocasos-window').find('input[name=identificador]');
		var $folio = $('#forma-crmregistrocasos-window').find('input[name=folio]');
		//var $id_agente = $('#forma-crmregistrocasos-window').find('input[name=id_agente]');
		var $agente = $('#forma-crmregistrocasos-window').find('select[name=select_agente]');
                
                var $cliente_prospecto = $('#forma-crmregistrocasos-window').find('input[name=cliente_prospecto]');
                var $id_cliente_prospecto = $('#forma-crmregistrocasos-window').find('input[name=id_cliente_prospecto]');
		var $busca_cliente_prospecto = $('#forma-crmregistrocasos-window').find('#busca_cliente_prospecto');
		var $buscando_por = $('#forma-crmregistrocasos-window').find('select[name=buscando_por]');
                
		var $fecha = $('#forma-crmregistrocasos-window').find('input[name=fecha_cierre]');
		
		
		var $select_estatus = $('#forma-crmregistrocasos-window').find('select[name=select_estatus]');
		var $select_prioridad = $('#forma-crmregistrocasos-window').find('select[name=select_prioridad]');
		var $select_tipo_caso = $('#forma-crmregistrocasos-window').find('select[name=select_tipo_caso]');
                
                var $observacion_agente = $('#forma-crmregistrocasos-window').find('textarea[name=observacion_agente]');
                var $descripcion = $('#forma-crmregistrocasos-window').find('textarea[name=descripcion]');
                var $resolucion = $('#forma-crmregistrocasos-window').find('textarea[name=resolucion]');
		
		var $cerrar_plugin = $('#forma-crmregistrocasos-window').find('#close');
		var $cancelar_plugin = $('#forma-crmregistrocasos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-crmregistrocasos-window').find('#submit');
		
		$folio.css({'background' : '#DDDDDD'});
		$identificador.attr({'value' : 0});
		//$id_contacto.attr({'value' : 0});
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Caso se registr&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-crmregistrocasos-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-crmregistrocasos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-crmregistrocasos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroCaso.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(data){
			//Alimentando los campos select agente
			
			$agente.children().remove();
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
			$agente.append(agente_hmtl);
		},"json");//termina llamada json
        
        
        //fecha de cierre
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
		
			
        
       
		
		
        
        
        
        //buscar contacto
        $busca_cliente_prospecto.click(function(event){
			event.preventDefault();
                        if($buscando_por.val() != 0 ){
                            var nivel_ejecucion=0;
                            $Pluguin_cliente_prospecto($cliente_prospecto.val(),$buscando_por.val(),nivel_ejecucion);
                        }else{
                            jAlert("Elije una Opcion",'Atencion!!!');
                        }
			
        
        });
        
        
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-crmregistrocasos-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-crmregistrocasos-overlay').fadeOut(remove);
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
			jConfirm('Realmente desea eliminar el Registro seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Registro fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Registro no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formacrmregistrocasos';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_crmregistrocasos();
			$('#forma-crmregistrocasos-window').css({"margin-left": -400, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-crmregistrocasos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $identificador = $('#forma-crmregistrocasos-window').find('input[name=identificador]');
                        var $folio = $('#forma-crmregistrocasos-window').find('input[name=folio]');
                        //var $id_agente = $('#forma-crmregistrocasos-window').find('input[name=id_agente]');
                        var $agente = $('#forma-crmregistrocasos-window').find('select[name=select_agente]');

                        var $cliente_prospecto = $('#forma-crmregistrocasos-window').find('input[name=cliente_prospecto]');
                        var $id_cliente_prospecto = $('#forma-crmregistrocasos-window').find('input[name=id_cliente_prospecto]');
                        var $busca_cliente_prospecto = $('#forma-crmregistrocasos-window').find('#busca_cliente_prospecto');
                        var $buscando_por = $('#forma-crmregistrocasos-window').find('select[name=buscando_por]');

                        var $fecha = $('#forma-crmregistrocasos-window').find('input[name=fecha_cierre]');


                        var $select_estatus = $('#forma-crmregistrocasos-window').find('select[name=select_estatus]');
                        var $select_prioridad = $('#forma-crmregistrocasos-window').find('select[name=select_prioridad]');
                        var $select_tipo_caso = $('#forma-crmregistrocasos-window').find('select[name=select_tipo_caso]');

                        var $observacion_agente = $('#forma-crmregistrocasos-window').find('textarea[name=observacion_agente]');
                        var $descripcion = $('#forma-crmregistrocasos-window').find('textarea[name=descripcion]');
                        var $resolucion = $('#forma-crmregistrocasos-window').find('textarea[name=resolucion]');
		
			var $cerrar_plugin = $('#forma-crmregistrocasos-window').find('#close');
			var $cancelar_plugin = $('#forma-crmregistrocasos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-crmregistrocasos-window').find('#submit');
			
			
			$folio.css({'background' : '#DDDDDD'});
			//$buscando_por.hide();
			$busca_cliente_prospecto.hide();
			$cliente_prospecto.attr('readonly',true);
                            
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroCaso.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("El Registro se actualiz&oacute; con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-crmregistrocasos-overlay').fadeOut(remove);
						//refresh_table();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-crmregistrocasos-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-crmregistrocasos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
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
					
                                        $cliente_prospecto.attr({'value' : entry['Datos']['0']['razon_social']});
                                        $id_cliente_prospecto.attr({'value' : entry['Datos']['0']['id_cliente_prospecto']});
					//$id_agente.attr({'value' : entry['Datos']['0']['agente_id']});
					//$agente.attr({'value' : entry['Datos']['0']['agente_id']});

					$fecha.attr({'value' : entry['Datos']['0']['fecha']});
					
				
					$descripcion.text(entry['Datos']['0']['descripcion']);
                                        $resolucion.text(entry['Datos']['0']['resolucion']);
					$observacion_agente.text(entry['Datos']['0']['observacion_agente']);
                                        
                                        
					var Estatus_html='';
                                        $select_estatus.children().remove();
                                        var status = ["ninguno", "Registro", "Asignado", "Segimiento", "Resolucion"];
                                                for(var i in status){
                                                       
                                                        if(parseInt(i) == parseInt(entry['Datos'][0]['estatus'])){
                                                            Estatus_html += '<option value="' + i + '" selected="yes">' + status[i] + '</option>';
                                                        }else{
                                                            Estatus_html += '<option value="' + i + '" >' + status[i] + '</option>';
                                                        }
                                                }

                                                $select_estatus.append(Estatus_html);
                                            
                                        $select_estatus.append(Estatus_html);
                                        
                                        
                                        var Prioridad_html='';
                                        $select_prioridad.children().remove();
                                        var Prioridd = ["ninguna", "Muy urgente", "Urgente", "Importante", "Normal","Baja"];
                                                /*
                                                for(var i in status){
                                                    select_html += '<option value="' + i + '" >' + status[i] + '</option>';
                                                }*/
                                            	
                                                for(var i in Prioridd){
                                                        
                                                        if(parseInt(i) == parseInt(entry['Datos'][0]['prioridad'])){
                                                            Prioridad_html += '<option value="' + i + '" selected="yes">' + Prioridd[i] + '</option>';
                                                        }else{
                                                            Prioridad_html += '<option value="' + i + '" >' + Prioridd[i] + '</option>';
                                                        }
                                                }
                                            $select_prioridad.append(Prioridad_html);
                                        
                                        var Tipocaso_html='';
                                        $select_tipo_caso.children().remove();
                                        var TipoCaso = ["ninguno", "Facturacion","Productos","Garantia", "Distribucion", "Danos", "Devoluciones","Cobranza","Varios"];
                                                for(var i in TipoCaso){
                                                        
                                                        if(parseInt(i) == parseInt(entry['Datos'][0]['tipo_caso'])){
                                                            Tipocaso_html += '<option value="' + i + '" selected="yes">' + TipoCaso[i] + '</option>';
                                                        }else{
                                                            Tipocaso_html += '<option value="' + i + '" >' + TipoCaso[i] + '</option>';
                                                        }
                                                }
                                        $select_tipo_caso.append(Tipocaso_html);
                                        
                                        //Alimentando los campos $agente
					/*$agente.children().remove();
					var agente_hmtl='';
					$.each(entry['Agentes'],function(entryIndex,agente){
						if(parseInt(agente['id'])==parseInt(entry['Datos'][0]['agente_id'])){
							agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
						}else{
							agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
						}
					});
					$agente.append(agente_hmtl);*/
                                       
                                        //$agente.children().remove();
                                        var agente_hmtl = '';
                                        if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
                                                agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
                                        }
			
                                        $.each(entry['Agentes'],function(entryIndex,agente){
                                                if(parseInt(agente['id'])==parseInt(entry['Datos'][0]['agente_id'])){
                                                        agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
                                                }else{
                                                        //si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
                                                        if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
                                                                agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
                                                        }
                                                }
                                        });
                                        $agente.append(agente_hmtl);
                                        
                                          //Alimentando el buscandopor
					$buscando_por.children().remove();
					var buscadopor_hmtl='';
                                        var text='';
                                        if(parseInt(entry['Datos'][0]['buscado_por'])== 1){
                                            text='Cliente';
                                        }else{
                                            text='Prospecto'
                                        }
                                                /*
						if(parseInt(entry['Datos'][0]['buscado_por'])== 1){
                                                        buscadopor_hmtl += '<option value="' +parseInt(entry['Datos'][0]['buscado_por']) + '" selected="yes" >Cliente</option>';
                                                        buscadopor_hmtl += '<option value="2" >Prospecto</option>';
                                                  }else{
							buscadopor_hmtl += '<option value="1" >Cliente</option>';
                                                        buscadopor_hmtl += '<option value="2" selected="yes">Prospecto</option>';
						}*/
					 buscadopor_hmtl += '<option value="' +parseInt(entry['Datos'][0]['buscado_por']) + '" selected="yes">' + text + '</option>';
					$buscando_por.append(buscadopor_hmtl);
					
				},"json");//termina llamada json
				//buscar cliente_prospecto
                                $busca_cliente_prospecto.click(function(event){
                                    event.preventDefault();
                                    if($buscando_por.val() != 0 ){
                                        var nivel_ejecucion=0;
                                        $Pluguin_cliente_prospecto($cliente_prospecto.val(),$buscando_por.val(),nivel_ejecucion);
                                    }else{
                                        jAlert("Elije una Opcion",'Atencion!!!');
                                    }
                                });
				
				/*/buscar contacto
				$busca_cliente_prospecto.click(function(event){
					event.preventDefault();
					$busca_cliente_prospecto($id_cliente_prospecto.val());
				});*/
				
				
				//fecha del caso
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
				
					
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-crmregistrocasos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-crmregistrocasos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllRegistroCasos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllRegistroCasos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
