$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
		//----------------------------------------------------------------
	//valida la fecha seleccionada
	function fecha_mayor(fecha, fecha2){
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
    
	//Muestra la fecha actual
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
	
	
	$add_calendar = function($campo, $fecha, $condicion){
		$campo.click(function (s){
			//$campo.val(null);
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$campo.DatePicker({
			format:'Y-m-d',
			date: $campo.val(),
			current: $campo.val(),
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
				$campo.val(formated);
				if (formated.match(patron) ){
					switch($condicion){
						case '>':
							var valida_fecha=fecha_mayor($campo.val(),mostrarFecha());
							if (valida_fecha==true){
								$campo.DatePickerHide();
							}else{
								jAlert("Fecha no valida. Debe ser mayor a la actual",'! Atencion');
								$campo.val($fecha);
							}
							break;
						case '>=':
							var valida_fecha=fecha_mayor_igual($campo.val(),mostrarFecha());
							if (valida_fecha==true){
								$campo.DatePickerHide();
							}else{
								jAlert("Fecha no valida. Debe ser mayor o igual a la actual",'! Atencion');
								$campo.val($fecha);
							}
							break;
						case '==':
							//code;
							break;
						case '<':
							//code;
							break;
						case '<=':
							//code;
							break;
						default:
							//para cunado no se le pasan parametros de condicion de fecha
							var valida_fecha=fecha_mayor($campo.val(),mostrarFecha());
							$campo.DatePickerHide();
							break;
					}
				}
			}
		});
	}

	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/gralconfigperiodospago";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_configperiodospago = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo Configuraci&oacute;n de Periodos de Pago');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $select_ano = $('#barra_buscador').find('.tabla_buscador').find('select[name=select_ano]');
	var $busqueda_select_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_agente]');
	var $busqueda_configuracionpago = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_configuracionpago]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limbuscarpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	var $div_reporte = $('#forma-gralconfigperiodospago-window').find('#div_reporte');

	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
                valor_retorno += "anio" + signo_separador + $select_ano.val()+ "|";
                valor_retorno += "titulo" + signo_separador + $busqueda_configuracionpago.val() + "|";
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		return valor_retorno;
		
	};
	
	//esta funcion carga los datos para el buscador del paginado
	$cargar_datos_buscador_principal= function(){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatos.json';
			var parametros={iu: $('#lienzo_recalculable').find('input[name=iu]').val()}
			$.post(input_json,parametros,function(entry){
			
			var $select_ano = $('#barra_buscador').find('.tabla_buscador').find('select[name=select_ano]');

			//carga select de años
			var html_ano = '';
			$select_ano.children().remove();
			var html_ano = '<option value="0"  selected="yes">[-Seleccionar A&ntilde;o-]</option>'
			$.each(entry['Anios'],function(entryIndex,anio){
				if(parseInt(anio['valor'])  ){
					//html_anio += '<option value="' + anio['valor'] + '" selected="yes">' + anio['valor'] + '</option>';
				//}else{
					html_ano += '<option value="' + anio['valor'] + '"  >' + anio['valor'] + '</option>';
				}
			});
			$select_ano.append(html_ano);
		});
	}
	
	//llamada a funcion
	$cargar_datos_buscador_principal();
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	//$cadena_busqueda = cadena;
	
	$buscar.click(function(event){
		event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	
	
	$limbuscarpiar.click(function(event){
		event.preventDefault();
		$select_ano.find('option[index=0]').attr('selected','selected');
		$busqueda_configuracionpago.val('');
		$busqueda_configuracionpago.focus();
		$cargar_datos_buscador_principal();
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
		$busqueda_configuracionpago.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_configuracionpago, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-gralconfigperiodospago-window').find('#submit').mouseover(function(){
			$('#forma-gralconfigperiodospago-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-gralconfigperiodospago-window').find('#submit').mouseout(function(){
			$('#forma-gralconfigperiodospago-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-gralconfigperiodospago-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-gralconfigperiodospago-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-gralconfigperiodospago-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-gralconfigperiodospago-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-gralconfigperiodospago-window').find('#close').mouseover(function(){
			$('#forma-gralconfigperiodospago-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-gralconfigperiodospago-window').find('#close').mouseout(function(){
			$('#forma-gralconfigperiodospago-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-gralconfigperiodospago-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-gralconfigperiodospago-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-gralconfigperiodospago-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-gralconfigperiodospago-window').find("ul.pestanas li").click(function() {
			$('#forma-gralconfigperiodospago-window').find(".contenidoPes").hide();
			$('#forma-gralconfigperiodospago-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-gralconfigperiodospago-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

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
	
	
	
	
	    //nuevas ConfigPeriodosPago
	    $new_configperiodospago.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$select_ano.val(0);
		$(this).modalPanel_GralConfigPeriodosPago();   //llamada al plug in 
		
		var form_to_show = 'formaGralConfigPeriodosPago';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-gralconfigperiodospago-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-gralconfigperiodospago-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
                
				//campos de la vista
				var $campo_id = $('#forma-gralconfigperiodospago-window').find('input[name=identificador]'); 
                var $titulo = $('#forma-gralconfigperiodospago-window').find('input[name=titulo]');
                var $select_periodo = $('#forma-gralconfigperiodospago-window').find('select[name=select_periodo]');
                var $select_anio = $('#forma-gralconfigperiodospago-window').find('select[name=select_anio]');
                var $grid_periodospago = $('#forma-gralconfigperiodospago-window').find('#grid_empleados');
                var $folio = $('#forma-gralconfigperiodospago-window').find('select[name=folio]');
				var $tituloperiodo = $('#forma-gralconfigperiodospago-window').find('select[name=tituloperiodo]');
				var $no_periodos = $('#forma-gralconfigperiodospago-window').find('input[name=no_periodos]');
				var $grid_warning = $('#forma-gralconfigperiodospago-window').find('#div_warning_grid').find('#grid_warning');
		
				//botones		
				var $cerrar_plugin = $('#forma-gralconfigperiodospago-window').find('#close');
				var $cancelar_plugin = $('#forma-gralconfigperiodospago-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-gralconfigperiodospago-window').find('#submit');
		
		$campo_id.attr({'value' : 0});
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Configuraci&oacute;n de Periodos de Pago fue dada de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-gralconfigperiodospago-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-gralconfigperiodospago-window').find('div.interrogacion').css({'display':'none'});
				
				$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
					$('#forma-gralconfigperiodospago-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
					.parent()
					.css({'display':'block'})
					.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					
					 //alert(tmp.split(':')[0]);           
                                            
					var campo = tmp.split(':')[0];
					var $campo_input;
					var cantidad_existencia=0;
					var  width_td=0;
					
					if((tmp.split(':')[0].substring(0, 12) == 'fechainicial') || (tmp.split(':')[0].substring(0, 10) == 'fechafinal') || (tmp.split(':')[0].substring(0, 13) == 'tituloperiodo')){
						//alert(campo);
						$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').css({'display':'block'});
						$campo_input = $grid_periodospago.find('#'+campo+'');
						$campo_input.css({'background' : '#d41000'});
						var folio = $campo_input.parent().parent().find('input[name=folio]').val();
						var titulo = $campo_input.parent().parent().find('input[name=tituloperiodo]').val();
						width_td = 370;
						
						var tr_warning = '<tr>';
								tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
								tr_warning += '<td width="90"><INPUT TYPE="text" value="' + folio + '" class="borde_oculto" readOnly="true" style="width:88px; color:red"></td>';
								tr_warning += '<td width="160"><INPUT TYPE="text" value="' + titulo + '" class="borde_oculto" readOnly="true" style="width:160px; color:red"></td>';
								tr_warning += '<td width="'+width_td+'"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:'+(parseInt(width_td) - 5)+'px; color:red"></td>';
						tr_warning += '</tr>';
						
						$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
					}
                                   
					}
				}
				
				$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
				$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
                
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatos.json';
		var parametros={
                    iu: $('#lienzo_recalculable').find('input[name=iu]').val()
                }
               
		 $.post(input_json,parametros,function(entry){
			//carga select de años
			$select_anio.children().remove();
			var html_anio = '';
			//var html_anio = '<option value="0"  selected="yes">[-Seleccionar A&ntilde;o-]</option>'
			$.each(entry['Anios'],function(entryIndex,anio){
				if(parseInt(anio['valor']) == parseInt(entry['Dato'][0]['anioActual']) ){
					html_anio += '<option value="' + anio['valor'] + '" selected="yes">' + anio['valor'] + '</option>';
				}else{
					html_anio += '<option value="' + anio['valor'] + '"  >' + anio['valor'] + '</option>';
				}
			});
			$select_anio.append(html_anio);
			
	
		});
               
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getConfigPeriodosPago.json';
		var parametros={
                    id:$campo_id.val(),
                    iu: $('#lienzo_recalculable').find('input[name=iu]').val()
                }
 
                $.post(input_json,parametros,function(entry){

					//alimenta el select de Periodicidad de Pago
					$select_periodo.children().remove();
					var periodo_hmtl=""
					var periodo_hmtl = '<option value="0"  selected="yes">[-Seleccionar Periodicidad de Pago-]</option>'
					$.each(entry['TiposPeriodos'],function(entryIndex,tipoperiodo){
						periodo_hmtl += '<option value="' + tipoperiodo['id'] + '"  >' + tipoperiodo['titulo'] + '</option> ';
						//periodo_hmtl += '<input type="hidden" name="no_periodos" id="no_periodos" value="'+tipoperiodo['no_periodos']+'">';
				
					});
					$select_periodo.append(periodo_hmtl);
					//alert($select_periodo.val());
			
			
				  //Cambiar la periodicidad
					$select_periodo.change(function(){
							var valor = $(this).val();
							var numeroperiodo;
					$.each(entry['TiposPeriodos'],function(entryIndex,tipoperiodo){
						//tipoperiodo['id']=valor;
						 if(tipoperiodo['id'] == valor) {
						numeroperiodo=tipoperiodo['no_periodos'];
						//alert(numeroperiodo);
						
						}
					});
					
				
						
						$grid_periodospago.children().remove();
						for (var i = 1; i < (parseInt(numeroperiodo) + 1); i++) {
								
							var tr = $("tr", $grid_periodospago).size();
							tr++;
							var trr = '';
							trr = '<tr class="gral">';	
								trr += '<td class="grid4" width="40">';
									trr += '<input type="hidden" name="id_reg" id="id_reg" class="id_reg'+ tr +'" value="0">';
									trr += '<input type="hidden" name="id_periodo" id="id_periodo" value="0">';
									trr += '<input type="hidden" name="noTr" value="'+ tr +'">';
									trr += '<input type="text" style="width:40px; background:#DDDDDD;" name="folio" value="'+i+'" readOnly="true" id="folio" class="folio'+ tr +'" >';
								trr += '</td>';
								trr += '<td class="grid3" width="420">';
									trr += '<input type="text" style="width:420px;" name="tituloperiodo" value="" id="tituloperiodo'+ tr +'" class="tituloperiodo'+ tr +'" >';
								trr += '</td>';
								trr += '<td class="grid3" width="90">';
									trr += '<input type="text" style="width:90px;" name="fecha_inicial" value="" id="fechainicial'+ tr +'" readOnly="true" class="fecha_inicial'+ tr +'" >';
								trr += '</td>';
								trr += '<td class="grid3" width="90">';
									trr += '<input type="text" style="width:90px;" name="fecha_final" value="" id="fechafinal'+ tr +'" readOnly="true" class="fecha_final'+ tr +'" >';
								trr += '</td>';
							trr += '</tr>';
							$grid_periodospago.append(trr);
							
							var $fecha_inicial=$grid_periodospago.find('#fechainicial'+ tr +'');
							var $fecha_final=$grid_periodospago.find('#fechafinal'+ tr +'');
							$add_calendar($fecha_inicial, " ", "");
							$add_calendar($fecha_final, " ", "");	
						}
				});
               });//termina llamada json
               

                
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-gralconfigperiodospago-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-gralconfigperiodospago-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		//Redefinir click del boton actualizar
			$submit_actualizar.bind('click',function(){
				if(parseInt($("tr", $grid_periodospago).size()) > 0){
					return true;
				}else{
					jAlert('No hay datos para actualizar', 'Atencion!', function(r) {
					$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').css({'display':'none'});
					$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						  });
					return false;
					
					
				}
			});
			
			});
        
        //Eventos del grid edicion,borrar!
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Configuraci&oacute;n de Periodos de Pago', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
                                            
						if ( entry['success'] == '1' ){
							jAlert("La Configuraci&oacute;n de Periodos de Pago fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Configuraci&oacute;n de Periodos de Pago no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
				
                        
		}else{

			//aqui  entra para editar un registro
			var form_to_show = 'formaGralConfigPeriodosPago';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_GralConfigPeriodosPago();
						
						$('#forma-gralconfigperiodospago-window').css({"margin-left": -300, 	"margin-top": -200});
                        $forma_selected.prependTo('#forma-gralconfigperiodospago-window');
                        $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
                        $tabs_li_funxionalidad();

                        //campos de la vista
                        var $campo_id = $('#forma-gralconfigperiodospago-window').find('input[name=identificador]'); 
                        var $titulo = $('#forma-gralconfigperiodospago-window').find('input[name=titulo]');
						var $select_periodo = $('#forma-gralconfigperiodospago-window').find('select[name=select_periodo]');
						var $select_anio = $('#forma-gralconfigperiodospago-window').find('select[name=select_anio]');
						var $folio = $('#forma-gralconfigperiodospago-window').find('select[name=folio]');
						var $tituloperiodo = $('#forma-gralconfigperiodospago-window').find('select[name=tituloperiodo]');
						var $fecha_inicial = $('#forma-gralconfigperiodospago-window').find('select[name=fecha_inicial]');
						var $fecha_final = $('#forma-gralconfigperiodospago-window').find('select[name=fecha_final]');
						var $grid_periodospago = $('#forma-gralconfigperiodospago-window').find('#grid_empleados');
						
						//grid de errores
						var $grid_warning = $('#forma-gralconfigperiodospago-window').find('#div_warning_grid').find('#grid_warning');
						
                        //$clave.attr('disabled','-1'); //deshabilitar
						//$clave.css({'background' : '#DDDDDD'});
				
                        //botones                        
                        var $cerrar_plugin = $('#forma-gralconfigperiodospago-window').find('#close');
                        var $cancelar_plugin = $('#forma-gralconfigperiodospago-window').find('#boton_cancelar');
                        var $submit_actualizar = $('#forma-gralconfigperiodospago-window').find('#submit');
                        

			
			if(accion_mode == 'edit'){
                            
				//aqui es el post que envia los datos a getConfigPeriodosPago.json
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getConfigPeriodosPago.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-gralconfigperiodospago-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
						
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-gralconfigperiodospago-window').find('div.interrogacion').css({'display':'none'});
						$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-gralconfigperiodospago-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								
								
								
							var campo = tmp.split(':')[0];
							var $campo_input;
							var cantidad_existencia=0;
							var  width_td=0;
							
							if((tmp.split(':')[0].substring(0, 12) == 'fechainicial') || (tmp.split(':')[0].substring(0, 10) == 'fechafinal') || (tmp.split(':')[0].substring(0, 13) == 'tituloperiodo')){
							$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').css({'display':'block'});
							
							$campo_input = $grid_periodospago.find('#'+campo+'');
							$campo_input.css({'background' : '#d41000'});
							var folio = $campo_input.parent().parent().find('input[name=folio]').val();
							var titulo = $campo_input.parent().parent().find('input[name=tituloperiodo]').val();
							width_td = 370;
								
								var tr_warning = '<tr>';
										tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
										tr_warning += '<td width="90"><INPUT TYPE="text" value="' + folio + '" class="borde_oculto" readOnly="true" style="width:88px; color:red"></td>';
										tr_warning += '<td width="160"><INPUT TYPE="text" value="' + titulo + '" class="borde_oculto" readOnly="true" style="width:160px; color:red"></td>';
										tr_warning += '<td width="'+width_td+'"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:'+(parseInt(width_td) - 5)+'px; color:red"></td>';
								tr_warning += '</tr>';
								
								$('#forma-gralconfigperiodospago-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
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
				// aqui van los campos de editar
				
						$campo_id.attr({'value' : entry['ConfigPeriodosPago']['0']['id']});
						$titulo.attr({'value' : entry['ConfigPeriodosPago']['0']['prefijo']});
                        
                        //alimenta el select de escolaridad
						$select_periodo.children().remove();


						
						var periodo_hmtl ="";
						$.each(entry['TiposPeriodos'],function(entryIndex,tipoperiodo){
								if(tipoperiodo['id']==  entry['ConfigPeriodosPago']['0']['tipo_periodo']){
									periodo_hmtl += '<option value="' + tipoperiodo['id'] + '"selected="yes" >' + tipoperiodo['titulo'] + '</option>';
								//}else{
									//periodo_hmtl += '<option value="' + tipoperiodo['id'] + '"  >' + tipoperiodo['titulo'] + '</option>';

								}
						});
						$select_periodo.append(periodo_hmtl);

						//Carga select año
						//var $select_anio = $('#forma-gralconfigperiodospago-window').find('select[name=select_anio]');
						$select_anio.children().remove();
						var html_anio ="";

						$.each(entry['ConfigPeriodosPago'],function(entryIndex,anio){
							html_anio += '<option value="' + anio['anio'] + '"  >' + anio['anio'] + '</option>';
						});
						$select_anio.append(html_anio);
						//alert(html_anio);


					$.each(entry['datosGrid'],function(entryIndex,data){
						var tr = $("tr", $grid_periodospago).size();
							tr++;
							var trr = '';
							trr = '<tr class="gral">';	
								trr += '<td class="grid3" width="40">';
									trr += '<input type="hidden" name="id_reg" id="id_reg" class="id_reg'+ tr +'" value="'+data['id_reg']+'">';
									trr += '<input type="hidden" name="id_periodo" id="id_periodo" value="'+data['id_periodo']+'">';
									trr += '<input type="hidden" name="noTr" value="'+ tr +'">';
									trr += '<input type="text" style="width:40px; background:#DDDDDD;" name="folio" readOnly="true" id="folio" class="folio'+ tr +'" value="'+data['folio']+'">';
								trr += '</td>';
								trr += '<td class="grid3" width="420">'
									trr += '<input type="text" name="tituloperiodo" style="width:420px;" id="tituloperiodo'+ tr +'" class="tituloperiodo'+ tr +'" value="'+data['tituloperiodo']+'"> ';
								trr += '</td>';
								trr += '<td class="grid3" width="90">';
									trr += '<input type="text"  name="fecha_inicial" style="width:90px;" id="fechainicial'+ tr +'" class="fecha_inicial'+ tr +'" value="'+data['fecha_inicial']+'"> <div class="interrogacion"><img src="../../img/help-16x16.png" align="top" rel="warning_fecha_inicial"></div>';
								trr += '</td>';
								trr += '<td class="grid3" width="90">';
									trr += '<input type="text"  name="fecha_final" style="width:90px;" id="fechafinal'+ tr +'" class="fecha_final'+ tr +'" value="'+data['fecha_final']+'"> <div class="interrogacion"><img src="../../img/help-16x16.png" align="top" rel="warning_fecha_final"></div>';
								trr += '</td>';
							trr += '</tr>';
							$grid_periodospago.append(trr);
							var $fecha_inicial=$grid_periodospago.find('#fechainicial'+ tr +'');
							var $fecha_final=$grid_periodospago.find('#fechafinal'+ tr +'');
							$add_calendar($fecha_inicial, " ", "");
							$add_calendar($fecha_final, " ", "");
					});
		
						
				 },"json");//termina llamada json
				 

				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralconfigperiodospago-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-gralconfigperiodospago-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    

                        
   $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllConfigPeriodosPago.json';
		
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllConfigPeriodosPago.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        //$.post(input_json,$arreglo,functmodalPanel_pocpedidosion(data){
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    

    $get_datos_grid();
    
    
    
});
