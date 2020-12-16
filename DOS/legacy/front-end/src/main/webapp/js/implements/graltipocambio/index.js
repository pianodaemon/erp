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
	var controller = $contextpath.val()+"/controllers/graltipocambio";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_tipocambio = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Actualizador de Tipo de Cambio');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
        
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_fecha = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha]');
        var $select_monedaBuscador = $('#barra_buscador').find('.tabla_buscador').find('select[name=select_monedaBuscador]');
        
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');	
        
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTipocambio.json';
		$arreglo = {'id':0,
		             'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
			   };
		
		$.post(input_json,$arreglo,function(entry){
                    $select_monedaBuscador.children().remove();
			var moneda_hmtl = '<option value="0" selected="yes">[--Seleccionar Moneda--]</option>';
                        
			$.each(entry['monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_monedaBuscador.append(moneda_hmtl);
			
			
		},"json");//termina llamada json
             
             
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
        
          $busqueda_fecha.DatePicker({
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
                         $busqueda_fecha.val(formated);
                         if (formated.match(patron) ){
                                   var valida_fecha=mayor($busqueda_fecha.val(),mostrarFecha());

                                   if (valida_fecha==true){
                                        jAlert("Fecha no valida",'! Atencion');
                                        $busqueda_fecha.val(mostrarFecha());
                                   }else{
                                        $busqueda_fecha.DatePickerHide();	
                                   }
                         }
                    }
          });
        
    
          $busqueda_fecha.click(function (s){
				var a=$('div.datepicker');
				a.css({'z-index':100});
          });

          //$busqueda_fecha.val(mostrarFecha());
	$busqueda_fecha.val();
	
	var to_make_one_search_string = function(){
            
        
        
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "fecha" + signo_separador + $busqueda_fecha.val() + "|";
                valor_retorno += "moneda" + signo_separador + $select_monedaBuscador.val() + "|";
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
                $busqueda_fecha.val('');
                
              
              
               
              
                
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
	});
	
	
	/*
	//obtiene los regiones para el buscador
	var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegionesBuscador.json';
	$arreglo = {};
	$.post(input_json,$arreglo,function(entry){
		//Alimentando los campos select de regiones del buscador
		$busqueda_select_region.children().remove();
		var region_hmtl = '<option value="0" selected="yes">[--Seleccionar Region --]</option>';
		$.each(entry['Regiones'],function(entryIndex,reg){
			region_hmtl += '<option value="' + reg['id'] + '"  >' + reg['titulo'] + '</option>';
		});
		$busqueda_select_region.append(region_hmtl);
	});//termina llamada json
	*/
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-tipocambio-window').find('#submit').mouseover(function(){
			$('#forma-tipocambio-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-tipocambio-window').find('#submit').mouseout(function(){
			$('#forma-tipocambio-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-tipocambio-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-tipocambio-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-tipocambio-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-tipocambio-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-tipocambio-window').find('#close').mouseover(function(){
			$('#forma-tipocambio-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-tipocambio-window').find('#close').mouseout(function(){
			$('#forma-tipocambio-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-tipocambio-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-tipocambio-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-tipocambio-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-tipocambio-window').find("ul.pestanas li").click(function() {
			$('#forma-tipocambio-window').find(".contenidoPes").hide();
			$('#forma-tipocambio-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-tipocambio-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
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
	
	
	//nuevas tipocambio
	$new_tipocambio.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_tipocambio();   //llamada al plug in 
		
		var form_to_show = 'formaTipocambio';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-tipocambio-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-tipocambio-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-tipocambio-window').find('input[name=identificador]');                
                var $select_monedas = $('#forma-tipocambio-window').find('select[name=select_monedas]');
		var $fecha= $('#forma-tipocambio-window').find('input[name=fecha]');
                var $tipocambio = $('#forma-tipocambio-window').find('input[name=tipocambio]');
                var $fecha_de_hoy = $('#forma-tipocambio-window').find('input[name=fecha_de_hoy]');                
                
			
		//botones		
		var $cerrar_plugin = $('#forma-tipocambio-window').find('#close');
		var $cancelar_plugin = $('#forma-tipocambio-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-tipocambio-window').find('#submit');
		
		$campo_id.attr({'value' : 0});
                $fecha_de_hoy.attr({'value' : ""});
                $fecha.attr({'readOnly':true});
          
          
             /*      
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
          }*/
	
          //muestra la fecha actual
         /* var mostrarFecha = function mostrarFecha(){
                    var ahora = new Date();
                    var anoActual = ahora.getFullYear();
                    var mesActual = ahora.getMonth();
                    mesActual = mesActual+1;
                    mesActual = (mesActual <= 9)?"0" + mesActual : mesActual;
                    var diaActual = ahora.getDate();
                    diaActual = (diaActual <= 9)?"0" + diaActual : diaActual;
                    var Fecha = anoActual + "-" + mesActual + "-" + diaActual;		
                    return Fecha;
          }*/
          //----------------------------------------------------------------
        /*
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
        
    
          $fecha.click(function (s){
				var a=$('div.datepicker');
				a.css({'z-index':100});
          });*/

          $fecha.val(mostrarFecha());
       
       
      
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Tipo de Cambio fue dado de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-tipocambio-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-tipocambio-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-tipocambio-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTipocambio.json';
		$arreglo = {'id':id_to_show,
		             'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
			   };
		
		$.post(input_json,$arreglo,function(entry){
                    
                    
                    
                    
                   
			
			$select_monedas.children().remove();
			//var moneda_hmtl = '<option value="0" selected="yes">[--Seleccionar Moneda--]</option>';
                        var moneda_hmtl = '';
			$.each(entry['monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_monedas.append(moneda_hmtl);
			
			
		},"json");//termina llamada json
		

		//validar campo comision, solo acepte numeros y punto
//		$comision.keypress(function(e){
//			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
//			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
//				return true;
//			}else {
//				return false;
//			}		
//		});


		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-tipocambio-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-tipocambio-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
                
                
                
		
	});
	
	
        
        
        
	
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Marca seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Marca fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La marca no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaTipocambio';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_tipocambio();
               
			$('#forma-tipocambio-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-tipocambio-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-tipocambio-window').find('input[name=identificador]');                
                        var $select_monedas = $('#forma-tipocambio-window').find('select[name=select_monedas]');
                        var $fecha= $('#forma-tipocambio-window').find('input[name=fecha]');
                        var $tipocambio = $('#forma-tipocambio-window').find('input[name=tipocambio]');
                        var $fecha_de_hoy = $('#forma-tipocambio-window').find('input[name=fecha_de_hoy]');                
               
			var $cerrar_plugin = $('#forma-tipocambio-window').find('#close');
			var $cancelar_plugin = $('#forma-tipocambio-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-tipocambio-window').find('#submit');
                        
                        $fecha.attr({'readOnly':true});
          
          
                   
          
       
			
			if(accion_mode == 'edit'){
                            
                   
                            
                            
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTipocambio.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-tipocambio-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-tipocambio-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-tipocambio-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
                                    var fecha_hoy=mostrarFecha();
                                      //  alert("HOY ES::"+fecha_hoy);
                                        
                                    if(fecha_hoy==entry['DatosTC']['0']['fecha']){
                                       //alert("La fecha que trae es igual a la fecha de Hoy");
                                       /* $fecha.click(function (s){
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
                                                              
                                                               // if (valida_fecha==true){
                                                                //	jAlert("Fecha no valida",'! Atencion');
                                                                //	$fecha.val(mostrarFecha());
                                                               // }else{
                                                                    //    $fecha.DatePickerHide();	
                                                               // }
                                                               // alert("Entra aqui");
                                                        }
                                                }
                                        });*/
                                        
                                        
                                        //$select_monedas.children().remove();
                                        /*var moneda = '<option value="0">[- Seleccion una Moneda-]</option>';
                                        $.each(entry['monedas'],function(entryIndex,clasif){
                                                if(clasif['id'] == entry['DatosTC']['0']['moneda_id']){
                                                        moneda += '<option value="' + clasif['id'] + '"  selected="yes">' + clasif['descripcion'] + '</option>';
                                                }else{
                                                        moneda += '<option value="' + clasif['id'] + '"  >' + clasif['descripcion'] + '</option>';
                                                }
                                        });
                                        $select_monedas.append(moneda); */
                                       
                                       var moneda = '';
                                       moneda += '<option value="' + entry['DatosTC']['0']['moneda_id'] + '"  selected="yes">' + entry['DatosTC']['0']['descripcion_abr'] + '</option>';
                                       $select_monedas.append(moneda); 
                                       $fecha.attr({ 'value' : entry['DatosTC']['0']['fecha'] });              
                                       $tipocambio.attr({ 'value' : entry['DatosTC']['0']['valor'] });
                                       
                                       $fecha.attr({'readOnly':true});
                                    }else{
                                        //alert("La fecha es diferente a a la de hoy,,,NO SE PERMITE  MODIFICAR");
                                       $submit_actualizar.hide();
                                       moneda += '<option value="' + entry['DatosTC']['0']['moneda_id'] + '"  selected="yes">' + entry['DatosTC']['0']['descripcion_abr'] + '</option>';
                                       $select_monedas.append(moneda); 
                                       $fecha.attr({ 'value' : entry['DatosTC']['0']['fecha'] });              
                                       $tipocambio.attr({ 'value' : entry['DatosTC']['0']['valor'] });
                                       
                                       $fecha.attr({'readOnly':true});
                                       $tipocambio.attr({'readOnly':true});
                                    }
                                    //$submit_actualizar.hide();
                                    $campo_id.attr({ 'value' : entry['DatosTC']['0']['id'] });               
                                    $fecha_de_hoy.attr({ 'value' :fecha_hoy  });               
                                    
                                    
                                     
					
				},"json");//termina llamada json
                               
                                //
				
	
				
				//validar campo comision, solo acepte numeros y punto
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-tipocambio-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-tipocambio-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllTipocambio.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllTipocambio.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



