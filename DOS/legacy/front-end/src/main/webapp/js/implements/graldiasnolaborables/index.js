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
	var controller = $contextpath.val()+"/controllers/graldiasnolaborables";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_item = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de D&iacute;as no Laborables');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_dia = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_dia]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	//var $busqueda_select_grupo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_grupo]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "fecha_no_laborable" + signo_separador + $busqueda_dia.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		//valor_retorno += "grupo" + signo_separador + $busqueda_select_grupo.val() + "|";
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
		$busqueda_dia.val('');
		$busqueda_descripcion.val('');
	});
	
	/*
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
         $('#barra_buscador').toggle( 'blind');
	});	
	*/
	
	
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


            $busqueda_dia.click(function (s){
                    var a=$('div.datepicker');
                    a.css({'z-index':100});
            });

            $busqueda_dia.DatePicker({
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
                            $busqueda_dia.val(formated);
                            $busqueda_dia.DatePickerHide();
                    }
            });
        
        
        
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-graldiasnolaborables-window').find('#submit').mouseover(function(){
			$('#forma-graldiasnolaborables-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-tesmovtipos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-graldiasnolaborables-window').find('#submit').mouseout(function(){
			$('#forma-graldiasnolaborables-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-tesmovtipos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-graldiasnolaborables-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-graldiasnolaborables-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-graldiasnolaborables-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-graldiasnolaborables-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-graldiasnolaborables-window').find('#close').mouseover(function(){
			$('#forma-tegraldiasnolaborablesan-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-graldiasnolaborables-window').find('#close').mouseout(function(){
			$('#forma-graldiasnolaborables-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-graldiasnolaborables-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-graldiasnolaborables-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-graldiasnolaborables-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-graldiasnolaborables-window').find("ul.pestanas li").click(function() {
			$('#forma-graldiasnolaborables-window').find(".contenidoPes").hide();
			$('#forma-graldiasnolaborables-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-graldiasnolaborables-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	
	
	//nuevo dia no laborable
	$new_item.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_modalboxDiasNoLaborables();
		
		var form_to_show = 'formaDiaNoLaborable';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-graldiasnolaborables-window').css({ "margin-left": -300, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-graldiasnolaborables-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-graldiasnolaborables-window').find('input[name=identificador]');
                var $dia = $('#forma-graldiasnolaborables-window').find('input[name=DNL]');
                var $descripcion = $('#forma-graldiasnolaborables-window').find('input[name=descripcion]');

		var $cerrar_plugin = $('#forma-graldiasnolaborables-window').find('#close');
		var $cancelar_plugin = $('#forma-graldiasnolaborables-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-graldiasnolaborables-window').find('#submit');
		
		$campo_id.attr({ 'value' : 0 });
             

                $dia.click(function (s){
                        var a=$('div.datepicker');
                        a.css({'z-index':100});
                });

                $dia.DatePicker({
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
                                $dia.val(formated);
                                $dia.DatePickerHide();
                        }
                });
            //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                
                
		var respuestaProcesada = function(data){
                    if ( data['success'] == 'true' ){
                        var remove = function() { $(this).remove(); };
                        $('#forma-graldiasnolaborables-overlay').fadeOut(remove);
                        jAlert("El Dia se ha actualizado.", 'Atencion!');
                        $get_datos_grid();
                    }
                    else{
                        // Desaparece todas las interrogaciones si es que existen
                        $('#forma-graldiasnolaborables-window').find('div.interrogacion').css({'display':'none'});

                        var valor = data['success'].split('___');
                        //muestra las interrogaciones
                        for (var element in valor){
                            tmp = data['success'].split('___')[element];
                            longitud = tmp.split(':');
                            if( longitud.length > 1 ){
                                    $('#forma-graldiasnolaborables-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                    .parent()
                                    .css({'display':'block'})
                                    .easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
                            }
                        }
                    }
                }             


                var options = {dataType :  'json', success : respuestaProcesada};
                $forma_selected.ajaxForm(options);
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-graldiasnolaborables-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-graldiasnolaborables-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
                
	});
	
	
        
        
        
	
	var Load_formaDNL = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Dia seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Dia fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Dia no puede ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaDiaNoLaborable';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_modalboxDiasNoLaborables();
			$('#forma-graldiasnolaborables-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-graldiasnolaborables-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
                        var $campo_id = $('#forma-graldiasnolaborables-window').find('input[name=identificador]');//estos son los nombres del campo del VM
                        var $dia = $('#forma-graldiasnolaborables-window').find('input[name=DNL]');
                        var $descripcion = $('#forma-graldiasnolaborables-window').find('input[name=descripcion]');
			var $cerrar_plugin = $('#forma-graldiasnolaborables-window').find('#close');
			var $cancelar_plugin = $('#forma-graldiasnolaborables-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-graldiasnolaborables-window').find('#submit');
                        
                        
			
                        
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDiaNoLaborable.json';
				$arreglo = {'id':id_to_show};
                                
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-graldiasnolaborables-overlay').fadeOut(remove);
						jAlert("El dia se ha actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-graldiasnolaborables-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-graldiasnolaborables-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$campo_id.attr({ 'value' : entry['DiaNoLaborable']['0']['id'] });//se cargan los datos para editar
                                        $dia.attr({ 'value' : entry['DiaNoLaborable']['0']['fecha_no_laborable'] });//esto viene del indice del controller
                                        $descripcion.attr({ 'value' : entry['DiaNoLaborable']['0']['descripcion'] });//que es en DiasNoLaborables del spring
                                },"json");//termina llamada json
				
				
                                
                                $dia.click(function (s){
                                var a=$('div.datepicker');
                                a.css({'z-index':100});
                                });

                                $dia.DatePicker({
                                        format:'Y-m-d',
                                        date: $dia.val(),
                                        current: $dia.val(),
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
                                                $dia.val(formated);
                                                $dia.DatePickerHide();
                                        }
                                });
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-graldiasnolaborables-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-graldiasnolaborables-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllDiasNoLaborables.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllDiasNoLaborables.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),Load_formaDNL);
            
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



