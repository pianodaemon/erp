$(function() {
	//jQuery.noConflict();
	
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Preorden de Producci&oacute;n' , 
		contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),
		
		userName : $('#lienzo_recalculable').find('input[name=user]').val(),
		ui : $('#lienzo_recalculable').find('input[name=iu]').val(),
		
		getUrlForGetAndPost : function(){
			var url = document.location.protocol + '//' + document.location.host + this.getController();
			return url;
		},
		getEmp: function(){
			return this.empresa;
		},
		getSuc: function(){
			return this.sucursal;
		},
		getUserName: function(){
			return this.userName;
		},
		getUi: function(){
			return this.ui;
		},
		getTituloApp: function(){
			return this.tituloApp;
		},
		getController: function(){
			return this.contextpath + "/controllers/procalidad";
			//  return this.controller;
		}
	};
	
	
	String.prototype.toCharCode = function(){
		var str = this.split(''), len = str.length, work = new Array(len);
		for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
		}
		return work.join(',');
	};
	
	
	var arrayTiposOrden = new Array();
    var arrayStatusOrden = {0:"No definido",1:"Aceptado", 2:"Rechazado"};
    
    
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
        var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/procalidad";
	
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_orden = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Aseguramiento de Calidad');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	
	var $cadena_busqueda = "";
	var $campo_busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $select_buscador_tipoorden = $('#barra_buscador').find('.tabla_buscador').find('select[name=buscador_tipoorden]');
	var $sku_producto_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=sku_producto_busqueda]');
	
	var array_productos_proceso = new Array(); //este arreglo carga la maquinas
	var array_instrumentos = new Array(); //este arreglo carga la maquinas
        
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_proordentipos.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
		//Llena el select tipos de productos en el buscador
		$select_buscador_tipoorden.children().remove();
		var prod_tipos_html = '<option value="0" selected="yes">[-- --]</option>';
		$.each(data['ordenTipos'],function(entryIndex,pt){
				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
		});
		$select_buscador_tipoorden.append(prod_tipos_html);
		
		arrayTiposOrden = data['ordenTipos'];
	});
        
        
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		
		var signo_separador = "=";
		valor_retorno += "folio_orden" + signo_separador + $campo_busqueda_folio.val() + "|";
		valor_retorno += "tipo_orden" + signo_separador + $select_buscador_tipoorden.val() + "|";
		valor_retorno += "sku_producto_busqueda" + signo_separador + $sku_producto_busqueda.val() + "|";
		
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
		$campo_busqueda_folio.val('');
		$sku_producto_busqueda.val('');
	});
        
	//visualizar  la barra del buscador
	TriggerClickVisializaBuscador = 0;
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
	});
	
        
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
        
	//valida la fecha seleccionada
	function fecha_mayor_igual(fecha, fecha2){
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
					if (xDia >= yDia){
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
        
        
        $add_calendar = function($campo, $fecha, $condicion){
            
            $campo.click(function (s){
                $campo.val(null);
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
                                //code;
                                var valida_fecha=fecha_mayor($campo.val(),mostrarFecha());
                                if (valida_fecha==true){
                                    $campo.DatePickerHide();
                                }else{
                                    jAlert("Fecha no valida. Debe ser mayor a la actual",'! Atencion');
                                    $campo.val($fecha);
                                }
                                break;
                            case '>=':
                                //code;
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
                                var valida_fecha=mayor($campo.val(),mostrarFecha());
                                $campo.DatePickerHide();
                                break;
                        }
                    }
                }
            });
        }
        
        

        
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-procalidad-window').find('select[name=prodtipo]');
            $('#forma-procalidad-window').find('#submit').mouseover(function(){
                $('#forma-procalidad-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
            });
            $('#forma-procalidad-window').find('#submit').mouseout(function(){
                $('#forma-procalidad-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
            });
            
            $('#forma-procalidad-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-procalidad-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            });
            $('#forma-procalidad-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-procalidad-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            });
            
            $('#forma-procalidad-window').find('#close').mouseover(function(){
                $('#forma-procalidad-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            });
            $('#forma-procalidad-window').find('#close').mouseout(function(){
                $('#forma-procalidad-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            });
            
            $('#forma-procalidad-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-procalidad-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-procalidad-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-procalidad-window').find("ul.pestanas li").click(function() {
                $('#forma-procalidad-window').find(".contenidoPes").hide();
                $('#forma-procalidad-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-procalidad-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
        
        
        
	
	
	var carga_formaProConfigproduccion0000_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
            if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'no_entrada':id_to_show,
                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el proceso seleccionado?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El proceso fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El proceso no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			//aqui  entra para editar un registro
			$(this).modalPanel_procalidad();
			
			var form_to_show = 'formaprocalidad00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$('#forma-procalidad-window').css({"margin-left": -220, "margin-top": -220});
			
			$forma_selected.prependTo('#forma-procalidad-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
				
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_datos_orden.json';
				$arreglo = {'id_orden':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
							
				var $id_orden = $('#forma-procalidad-window').find('input[name=id_orden]');
				var $proceso_flujo_id = $('#forma-procalidad-window').find('input[name=proceso_flujo_id]');
				var $select_tipoorden = $('#forma-procalidad-window').find('select[name=select_tipoorden]');
				var $folio = $('#forma-procalidad-window').find('input[name=folio]');
				var $fecha = $('#forma-procalidad-window').find('input[name=fecha]');
				var $lote = $('#forma-procalidad-window').find('input[name=lote]');
				var $codigo = $('#forma-procalidad-window').find('input[name=codigo]');
				var $unidad = $('#forma-procalidad-window').find('input[name=unidad]');
				var $cantidad = $('#forma-procalidad-window').find('input[name=cantidad]');
				var $nombre = $('#forma-procalidad-window').find('input[name=nombre]');
				var $observaciones = $('#forma-procalidad-window').find('textarea[name=observaciones]');
				
				var $cancelada = $('#forma-procalidad-window').find('input[name=cancelada]');
				
				var $select_status_calidad = $('#forma-procalidad-window').find('select[name=select_status_calidad]');
				var $comentarios = $('#forma-procalidad-window').find('textarea[name=comentarios]');
				
				var $cerrar_plugin = $('#forma-procalidad-window').find('#close');
				var $cancelar_plugin = $('#forma-procalidad-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-procalidad-window').find('#submit');
				
				
				$id_orden.val(0);
				$cancelada.hide();
				$folio.css({'background' : '#F0F0F0'});
				$fecha.css({'background' : '#F0F0F0'});
				$lote.css({'background' : '#F0F0F0'});
				$codigo.css({'background' : '#F0F0F0'});
				$unidad.css({'background' : '#F0F0F0'});
				$cantidad.css({'background' : '#F0F0F0'});
				$nombre.css({'background' : '#F0F0F0'});
				
				$id_orden.val(id_to_show);
                                
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("Los cambios se guardaron con exito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-procalidad-overlay').fadeOut(remove);
						$get_datos_grid();
					}else{
                                            
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-procalidad-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-procalidad-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
							}
						}
                                                
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					
					$id_orden.attr({'value': entry['Orden']['0']['id']});
					$proceso_flujo_id.attr({'value': entry['Orden']['0']['pro_proceso_flujo_id']});
					$folio.attr({'value': entry['Orden']['0']['folio']});
					$fecha.attr({'value': entry['Orden']['0']['fecha_elavorar']});
					$lote.attr({'value': entry['Orden']['0']['lote']});
					
					$observaciones.val( entry['Orden']['0']['observaciones']);
					$comentarios.val( entry['Orden']['0']['comentarios_calidad']);
					
					$select_tipoorden.children().remove();
					var orden_tipos_html = '';
					$.each(arrayTiposOrden,function(entryIndex,pt){
						if(parseInt(entry['Orden']['0']['pro_orden_tipos_id']) == parseInt(pt['id'])){
							orden_tipos_html += '<option value="' + pt['id'] + '" selected="yes" >' + pt['titulo'] + '</option>';
						}
					});
					$select_tipoorden.append(orden_tipos_html);
					
					
					$select_status_calidad.children().remove();
					var status_html = '';
					for(var i in arrayStatusOrden){
						if(parseInt(entry['Orden']['0']['status_calidad'])==parseInt(i)){
							status_html += '<option value="' + i + '" selected="yes">' + arrayStatusOrden[i] + '</option>';
						}else{
							if(parseInt(entry['Orden']['0']['status_calidad'])==0){
								//si el estatus es igual  a cero hay que mostrar todas las opciones
								status_html += '<option value="' + i + '" >' + arrayStatusOrden[i] + '</option>';
							}else{
								//si es diferente de cero hay que mostrar solo los  que sean mayores a cero
								if(parseInt(i)>0){
									status_html += '<option value="' + i + '" >' + arrayStatusOrden[i] + '</option>';
								}
							}
						}
					}
					$select_status_calidad.append(status_html);
					
					var idProd='';
					var codigo='';
					var nombre='';
					var unidad='';
					var cantidad='';
					var densidad='';
					
					if(entry['OrdenDet'] != null){
						$.each(entry['OrdenDet'],function(entryIndex,prod){
							//prod['cantidad']
							//prod['cantidad_salida']
							idProd = prod['inv_prod_id'];
							codigo = prod['sku'];
							nombre = prod['descripcion'];
							unidad = prod['unidad'];
							cantidad = prod['cantidad'];
							densidad = prod['densidad'];
						});
					};
					
					$codigo.val(codigo);
					$unidad.val(unidad);
					$cantidad.val(cantidad);
					$nombre.val(nombre);
					
					//5;"Cancelada"
					if(parseInt(entry['Orden']['0']['pro_proceso_flujo_id'])==5 ){
						$folio.attr('disabled','-1');
						$fecha.attr('disabled','-1');
						$lote.attr('disabled','-1');
						$observaciones.attr('disabled','-1');
						$comentarios.attr('disabled','-1');
						$select_tipoorden.attr('disabled','-1');
						$select_status_calidad.attr('disabled','-1');
						$codigo.attr('disabled','-1');
						$unidad.attr('disabled','-1');
						$cantidad.attr('disabled','-1');
						$nombre.attr('disabled','-1');
						$cancelada.show();
						$cancelada.val("ORDEN CANCELADA");
						$cancelada.attr('disabled','-1');
						$submit_actualizar.hide();
						$cancelar_plugin.focus();
					}
					
					
				},"json");//termina llamada json
				
				
                                
                                
				//$submit_actualizar
				
				
				//cerrar plugin
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-procalidad-overlay').fadeOut(remove);
				});
				
				//boton cancelar y cerrar plugin
				$cancelar_plugin.click(function(event){
					var remove = function() {$(this).remove();};
					$('#forma-procalidad-overlay').fadeOut(remove);
				});
				
			}
		}
	}
	
	
	
        
	$get_datos_grid = function(){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_all_ordenesproduccion.json';
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
		$arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/get_all_ordenesproduccion.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
		$.post(input_json,$arreglo,function(data){
			//pinta_grid
			//$.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaEntradamercancias00_for_datagrid00);
			
			//aqui se utiliza el mismo datagrid que prefacturas. Solo muesta icono de detalles, el de eliminar No
			$.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaProConfigproduccion0000_for_datagrid00);
			
			//resetea elastic, despues de pintar el grid y el slider
			Elastic.reset(document.getElementById('lienzo_recalculable'));
		},"json");
	}
	
    $get_datos_grid();
});



