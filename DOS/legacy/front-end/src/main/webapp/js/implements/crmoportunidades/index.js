$(function() {
	//var controller = "com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/empleados";
	//var controller = "controllers/crmoportunidades";
	
    //arreglo para select Base Precio
    var array_dias_semana = {
				1:"Domingo", 
				2:"Lunes", 
				3:"Martes",
				4:"Miercoles",
				5:"Jueves",
				6:"Viernes",
				7:"Sabado"
			};
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
	var controller = $contextpath.val()+"/controllers/crmoportunidades";
        
        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_item = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de oportunidades');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
        
	var $cadena_busqueda = "";
	var $campo_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=cadena_buscar]');
	var $select_buscador_etapa_venta = $('#barra_buscador').find('.tabla_buscador').find('select[name=buscador_etapa_venta]');
        var $select_buscador_tipo_oportunidad = $('#barra_buscador').find('.tabla_buscador').find('select[name=buscador_tipo_oportunidad]');
        var $select_buscador_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=buscador_agente]');
	
        
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	$buscar.click(function(event){
            event.preventDefault();
            cadena = to_make_one_search_string();
            $cadena_busqueda = cadena.toCharCode();
            $get_datos_grid();
	});
	
	
	$limpiar.click(function(event){
            event.preventDefault();
            $campo_busqueda.val('');
            $select_buscador_etapa_venta.find('option[index=0]').attr('selected','selected');
            $select_buscador_tipo_oportunidad.find('option[index=0]').attr('selected','selected');
            $select_buscador_agente.find('option[index=0]').attr('selected','selected');
            $get_datos_grid();
	});
	
	var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTiposOportunidad.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
            //Llena el select tipos de productos en el buscador
            $select_buscador_tipo_oportunidad.children().remove();
            var html_tipo_op = '<option value="0" selected="yes">[-- Seleccione un tipo --]</option>';
            $.each(data['TiposOportunidad'], function(entryIndex,item){
                html_tipo_op += '<option value="' + item['id'] + '"  >' + item['descripcion'] + '</option>';
            });
            $select_buscador_tipo_oportunidad.append(html_tipo_op);
	});
        
        var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEtapasVenta.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
            //Llena el select tipos de productos en el buscador
            $select_buscador_etapa_venta.children().remove();
            var html_etapa_venta = '<option value="0" selected="yes">[--Seleccione una etapa --]</option>';
            $.each(data['EtapasVenta'], function(entryIndex,item){
                html_etapa_venta += '<option value="' + item['id'] + '"  >' + item['descripcion'] + '</option>';
             });
            $select_buscador_etapa_venta.append(html_etapa_venta);
	});
        
        var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentes.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
            //Llena el select tipos de productos en el buscador
            $select_buscador_agente.children().remove();
            var html_agente = '<option value="0" selected="yes">[--Seleccione un agente --]</option>';
            $.each(data['Agentes'], function(entryIndex,item){
                html_agente += '<option value="' + item['id'] + '"  >' + item['nombre_agente'] + '</option>';
             });
            $select_buscador_agente.append(html_agente);
	});
        
        
        
	var to_make_one_search_string = function(){
            var valor_retorno = "";
            var signo_separador = "=";
            valor_retorno += "buscador_contacto" + signo_separador + $campo_busqueda.val() + "|";
            valor_retorno += "buscador_etapa_venta" + signo_separador + $select_buscador_etapa_venta.val() + "|";
            valor_retorno += "buscador_tipo_oportunidad" + signo_separador + $select_buscador_tipo_oportunidad.val() + "|";
            valor_retorno += "buscador_agente" + signo_separador + $select_buscador_agente.val() + "|";
            valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
            return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
        
	//$cadena_busqueda = cadena;
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
	
	
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-crmoportunidades-window').find('#submit').mouseover(function(){
			$('#forma-crmoportunidades-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
                
		$('#forma-crmoportunidades-window').find('#submit').mouseout(function(){
			$('#forma-crmoportunidades-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
                
		$('#forma-crmoportunidades-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-crmoportunidades-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
                
		$('#forma-crmoportunidades-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-crmoportunidades-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-crmoportunidades-window').find('#close').mouseover(function(){
			$('#forma-crmoportunidades-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
                
		$('#forma-crmoportunidades-window').find('#close').mouseout(function(){
			$('#forma-crmoportunidades-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-crmoportunidades-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-crmoportunidades-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-crmoportunidades-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-crmoportunidades-window').find("ul.pestanas li").click(function() {
			$('#forma-crmoportunidades-window').find(".contenidoPes").hide();
			$('#forma-crmoportunidades-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-crmoportunidades-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");

			if(activeTab == '#tabx-1'){
                            if($('#forma-crmoportunidades-window').find('input[name=consignacion]').is(':checked')){
                                    $('#forma-crmoportunidades-window').find('#div_consignacion_grid').css({'display':'block'});
                                    $('#forma-crmoportunidades-window').find('.empleados_div_one').css({'height':'165px'});
                                    $('#forma-crmoportunidades-window').find('.empleados_div_one').css({'width':'810px'});
                                    $('#forma-crmoportunidades-window').find('.empleados_div_two').css({'width':'810px'});
                                    $('#forma-crmoportunidades-window').find('.empleados_div_three').css({'width':'800px'});
                                    $('#forma-crmoportunidades-window').find('#cierra').css({'width':'765px'});
                                    $('#forma-crmoportunidades-window').find('#botones').css({'width':'790px'});
                            }else{
                                    $('#forma-crmoportunidades-window').find('#div_consignacion_grid').css({'display':'none'});
                                    $('#forma-crmoportunidades-window').find('.empleados_div_one').css({'height':'370px'});
                                    $('#forma-crmoportunidades-window').find('.empleados_div_one').css({'width':'810px'});
                                    $('#forma-crmoportunidades-window').find('.empleados_div_two').css({'width':'810px'});
                                    $('#forma-crmoportunidades-window').find('.empleados_div_three').css({'width':'800px'});
                                    $('#forma-crmoportunidades-window').find('#cierra').css({'width':'765px'});
                                    $('#forma-crmoportunidades-window').find('#botones').css({'width':'790px'});
                            }
				
			}
			
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
	
	
	//carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero){
		$campo_select.children().remove();
		var select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
			}
		}
		$campo_select.append(select_html);
	}
      
        
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
        
	$add_ceros = function($campo){
		$campo.val("0");
	}
	
	$accion_focus = function($campo){
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo.focus(function(e){
			$valor_tmp = $(this).val().split(",").join("");
			
			if( ($valor_tmp != '') && ($valor_tmp != ' ') && ($valor_tmp != null) ){
				if(parseFloat($valor_tmp)<1){
					$campo.val('');
				}
			}
		});
	}
        
	$accio_blur = function($campo){
		//recalcula importe al perder enfoque el campo costo
		$campo.blur(function(){
			$valor_tmp = $(this).val().split(",").join("");
			
			if ($valor_tmp == ''  || $valor_tmp == null){
					$(this).val('0');
			}
			
			if( ($valor_tmp != '') && ($valor_tmp != ' ') ){
				$campo.val(parseFloat($valor_tmp));
			}else{
				$(this).val('0');
			}
			
		});
	}
	
        
        //buscador de productos
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
                                
                                $('#forma-crmoportunidades-window').find('input[name=prospecto]').val(razon_social_buscador);
                                $('#forma-crmoportunidades-window').find('input=[name=contacto_id]').val(id_contacto);
                                $('#forma-crmoportunidades-window').find('input[name=contacto]').val(contacto_buscador);
                                
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
                
	}//termina buscador de productos
        
        
        
	//nuevo cliente
	$new_item.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanelCrmOportunidades();
		
		
		//aqui entra nuevo
		var form_to_show = 'formaCrmOportunidades00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		//alert("si entra");
		$('#forma-crmoportunidades-window').css({ "margin-left": -400, 	"margin-top": -270 });
		$forma_selected.prependTo('#forma-crmoportunidades-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		//alert("si pasa");
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_oportunidad.json';
		$arreglo = {'id':id_to_show,
                    'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                };
                
                
		
		//tab 1 Datos personales
                var $identificador=$('#forma-crmoportunidades-window').find('input[name=identificador]');
                var $campo_prospecto = $('#forma-crmoportunidades-window').find('input[name=prospecto]');
                var $campo_contacto_id=$('#forma-crmoportunidades-window').find('input=[name=contacto_id]');
		var $campo_contacto = $('#forma-crmoportunidades-window').find('input[name=contacto]');
		var $campo_fecha_oportunidad= $('#forma-crmoportunidades-window').find('input[name=fecha_oportunidad]');
		var $campo_fecha_cotizacion = $('#forma-crmoportunidades-window').find('input[name=fecha_cotizacion]');
		var $campo_fecha_cierre = $('#forma-crmoportunidades-window').find('input[name=fecha_cierre]');
		var $campo_monto = $('#forma-crmoportunidades-window').find('input[name=monto]');
                
		
		var $select_empleado= $('#forma-crmoportunidades-window').find('select[name=empleado]');
		var $select_tipo_oportunidad= $('#forma-crmoportunidades-window').find('select[name=tipo_oportunidad]');
		var $select_etapa_venta = $('#forma-crmoportunidades-window').find('select[name=etapa_venta]');
		var $select_estatus = $('#forma-crmoportunidades-window').find('select[name=estatus]');
                var $select_cierre_oportunidad = $('#forma-crmoportunidades-window').find('select[name=cierre_oportunidad]');
		
                var $buscador_contactos = $('#forma-crmoportunidades-window').find('a[href*=buscador_contactos]');
                
                var $cerrar_plugin = $('#forma-crmoportunidades-window').find('#close');
		var $cancelar_plugin = $('#forma-crmoportunidades-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-crmoportunidades-window').find('#submit');
                
		
                $identificador.val(0);
		
               //quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$permitir_solo_numeros($campo_monto);
                $add_calendar($campo_fecha_oportunidad, " ", ">=");
                $add_calendar($campo_fecha_cotizacion, " ", ">=");
                $add_calendar($campo_fecha_cierre, " ", ">=");
                /*
                 *$campo_prospecto_id
                $campo_prospecto
                 **/
                
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
                            jAlert("Oportunidad dado de alta", 'Atencion!');
                            var remove = function() { $(this).remove(); };
                            $('#forma-crmoportunidades-overlay').fadeOut(remove);
                            //refresh_table();
                            $get_datos_grid();
			}
			else{
                            // Desaparece todas las interrogaciones si es que existen
                            $('#forma-crmoportunidades-window').find('div.interrogacion').css({'display':'none'});

                            var valor = data['success'].split('___');
                            //muestra las interrogaciones
                            for (var element in valor){
                                    tmp = data['success'].split('___')[element];
                                    longitud = tmp.split(':');
                                    if( longitud.length > 1 ){
                                            $('#forma-crmoportunidades-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                            .parent()
                                            .css({'display':'block'})
                                            .easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
                                    }
                            }
			}
		}
                
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
                
                $.post(input_json,$arreglo,function(entry){
                    
                    id_user = 0;
                    $.each(entry['Agentes'], function(entryIndex,item){
                       if(entry['Session'][0]['empleado_id'] == item['id']){
                           id_user = item['id'];
                       }
                    });
                    
                    var html_agente = '';
                    if(id_user == 0){
                        var html_agente = '<option value="0"  selected="yes">[-Seleccione un Agente-]</option>';
                        $.each(entry['Agentes'], function(entryIndex,item){
                            html_agente += '<option value="' + item['id'] + '"  >' + item['nombre_agente'] + '</option>';
                         });
                    }else{
                        $.each(entry['Agentes'], function(entryIndex,item){
                            if(entry['Session'][0]['empleado_id'] == item['id']){
                                html_agente += '<option value="' + item['id'] + '"  >' + item['nombre_agente'] + '</option>';
                            }
                         });
                    }
                    $select_empleado.append(html_agente);
                    
                    //$carga_campos_select($select_empleado, entry['Agentes'], id_user, "[-Seleccione un Agente-]");
                    
                    /*
                    //alimenta el select de $select_estatus
                    var html_estatus = '<option value="true"  selected="yes">Vigente</option>';
                    html_estatus += '<option value="true" >Cancelado</option>';
                    $select_empleado.append(html_estatus);
                    */
                    
                    //alimenta el select de $select_tipo_oportunidad
                    var html_tipo_op = '<option value="0"  selected="yes">[-Seleccionar Tipo Oportunidad-]</option>';
                    $.each(entry['TiposOportunidad'], function(entryIndex,item){
                        html_tipo_op += '<option value="' + item['id'] + '"  >' + item['descripcion'] + '</option>';
                    });
                    $select_tipo_oportunidad.append(html_tipo_op);
                    
                    //alimenta el select de $select_etapa_venta
                    var html_etapa_venta = '<option value="0"  selected="yes">[-Seleccionar Etapa Venta-]</option>';
                    $.each(entry['EtapasVenta'], function(entryIndex,item){
                        html_etapa_venta += '<option value="' + item['id'] + '"  >' + item['descripcion'] + '</option>';
                    });
                    $select_etapa_venta.append(html_etapa_venta);
                    
                    //alimenta el select de $select_estatus
                    var html_estatus = '<option value="true"  selected="yes">Vigente</option>';
                    html_estatus += '<option value="true" >Cancelado</option>';
                    $select_estatus.append(html_estatus);
                    
                    
                    //alimenta el select de $select_cierre_oportunidad
                    var html_cierreop = '<option value="0"  selected="yes">Sin cerrar</option>';
                    html_cierreop += '<option value="1">Ganada</option>';
                    html_cierreop += '<option value="2">Perdida</option>';
                    $select_cierre_oportunidad.append(html_cierreop);
                    
                });
                
                //buscar proveedor
		$buscador_contactos.click(function(event){
                    event.preventDefault();
                    $busca_contactos($campo_prospecto.val());
		});
                /*                
                
                $submit_actualizar.bind('click',function(){
                    var selec=0;
                    
                    if(parseInt($total_tr.val()) > 0){
                        return true;
                    }else{
                        jAlert("No hay roles seleccionadas para actualizar", 'Atencion!');
                        return false;
                    }
                });
                */
                
		$cerrar_plugin.bind('click',function(){
                    var remove = function() { $(this).remove(); };
                    $('#forma-crmoportunidades-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
                    var remove = function() { $(this).remove(); };
                    $('#forma-crmoportunidades-overlay').fadeOut(remove);
                    $buscar.trigger('click');
		});
        });
        
        
	var carga_formaOpotunidades00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
                                    'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                                    };
			jConfirm('Realmente desea eliminar la oportunidad seleccionado', 'Dialogo de confirmacion', function(r) {
                            if (r){
                                $.post(input_json,$arreglo,function(entry){
                                    if ( entry['success'] == '1' ){
                                        jAlert("La oportunidad fue eliminada exitosamente", 'Atencion!');
                                        $get_datos_grid();
                                    }
                                    else{
                                        jAlert("La oportunidad no pudo ser eliminada", 'Atencion!');
                                    }
                                },"json");
                            }
			});  
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaCrmOportunidades00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			//var accion = "get_cliente";
			
			$(this).modalPanelCrmOportunidades();
			$('#forma-crmoportunidades-window').css({ "margin-left": -400, 	"margin-top": -290 });
			
			$forma_selected.prependTo('#forma-crmoportunidades-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_oportunidad.json';
				$arreglo = {
                                        'id':id_to_show,
                                        'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                                };
						
				//tab 1 Datos personales
                                var $identificador=$('#forma-crmoportunidades-window').find('input[name=identificador]');
                                var $campo_prospecto = $('#forma-crmoportunidades-window').find('input[name=prospecto]');
                                var $campo_contacto_id=$('#forma-crmoportunidades-window').find('input=[name=contacto_id]');
                                var $campo_contacto = $('#forma-crmoportunidades-window').find('input[name=contacto]');
                                var $campo_fecha_oportunidad= $('#forma-crmoportunidades-window').find('input[name=fecha_oportunidad]');
                                var $campo_fecha_cotizacion = $('#forma-crmoportunidades-window').find('input[name=fecha_cotizacion]');
                                var $campo_fecha_cierre = $('#forma-crmoportunidades-window').find('input[name=fecha_cierre]');
                                var $campo_monto = $('#forma-crmoportunidades-window').find('input[name=monto]');
                                
                                
                                var $select_empleado= $('#forma-crmoportunidades-window').find('select[name=empleado]');
                                var $select_tipo_oportunidad= $('#forma-crmoportunidades-window').find('select[name=tipo_oportunidad]');
                                var $select_etapa_venta = $('#forma-crmoportunidades-window').find('select[name=etapa_venta]');
                                var $select_estatus = $('#forma-crmoportunidades-window').find('select[name=estatus]');
                                var $select_cierre_oportunidad = $('#forma-crmoportunidades-window').find('select[name=cierre_oportunidad]');
                                
                                var $buscador_contactos = $('#forma-crmoportunidades-window').find('a[href*=buscador_contactos]');
                                
                                var $cerrar_plugin = $('#forma-crmoportunidades-window').find('#close');
                                var $cancelar_plugin = $('#forma-crmoportunidades-window').find('#boton_cancelar');
                                var $submit_actualizar = $('#forma-crmoportunidades-window').find('#submit');
                                
                                
				$buscador_contactos.hide();
                                
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-crmoportunidades-overlay').fadeOut(remove);
						jAlert("Los datos de la oportunidad se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-crmoportunidades-window').find('div.interrogacion').css({'display':'none'});

						//alert(data['success']);

						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-empleados-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
                                    
                                    $identificador.attr({ 'value' : entry['Oportunidad']['0']['id'] });
                                    $campo_prospecto.attr({ 'value' : entry['Oportunidad']['0']['prospecto'] });
                                    $campo_contacto_id.attr({ 'value' : entry['Oportunidad']['0']['crm_contactos_id'] });
                                    $campo_contacto.attr({ 'value' : entry['Oportunidad']['0']['contacto'] });
                                    
                                    $campo_fecha_oportunidad.attr({ 'value' : entry['Oportunidad']['0']['fecha_oportunidad'] });
                                    $campo_fecha_cotizacion.attr({ 'value' : entry['Oportunidad']['0']['fecha_cotizar'] });
                                    $campo_fecha_cierre.attr({ 'value' : entry['Oportunidad']['0']['fecha_cierre'] });
                                    $campo_monto.attr({ 'value' : entry['Oportunidad']['0']['monto'] });
                                    
                                    $permitir_solo_numeros($campo_monto);
                                    $add_calendar($campo_fecha_oportunidad, entry['Oportunidad']['0']['fecha_oportunidad'], ">=");
                                    $add_calendar($campo_fecha_cotizacion,entry['Oportunidad']['0']['fecha_cotizar'], ">=");
                                    $add_calendar($campo_fecha_cierre, entry['Oportunidad']['0']['fecha_cierre'], ">=");
                                    
                                    var html_agente = '';
                                    $.each(entry['Agentes'], function(entryIndex,item){
                                        if(entry['Oportunidad']['0']['gral_empleados_id'] == item['id']){
                                            html_agente += '<option value="' + item['id'] + '" selected="yes">' + item['nombre_agente'] + '</option>';
                                        }
                                     });
                                    $select_empleado.append(html_agente);
                                    
                                    //alimenta el select de $select_tipo_oportunidad
                                    var html_tipo_op = '<option value="0" >[-Seleccionar Tipo Oportunidad-]</option>';
                                    $.each(entry['TiposOportunidad'], function(entryIndex,item){
                                        if(item['id'] == entry['Oportunidad']['0']['crm_tipos_oportunidad_id']){
                                            html_tipo_op += '<option value="' + item['id'] + '"  selected="yes">' + item['descripcion'] + '</option>';
                                        }else{
                                            html_tipo_op += '<option value="' + item['id'] + '"  >' + item['descripcion'] + '</option>';
                                        }
                                    });
                                    $select_tipo_oportunidad.append(html_tipo_op);
                                    
                                    //alimenta el select de $select_etapa_venta
                                    var html_etapa_venta = '<option value="0"  >[-Seleccionar Etapa Venta-]</option>';
                                    $.each(entry['EtapasVenta'], function(entryIndex,item){
                                        if(item['id'] >= entry['Oportunidad']['0']['crm_etapas_venta_id']){
                                            if(item['id'] == entry['Oportunidad']['0']['crm_etapas_venta_id']){
                                                html_etapa_venta += '<option value="' + item['id'] + '"  selected="yes">' + item['descripcion'] + '</option>';
                                            }else{
                                                html_etapa_venta += '<option value="' + item['id'] + '"  >' + item['descripcion'] + '</option>';
                                            }
                                        }
                                    });
                                    $select_etapa_venta.append(html_etapa_venta);
                                    
                                    //alimenta el select de $select_estatus
                                    var html_estatus = '';
                                    if(entry['Oportunidad']['0']['estatus'] == 'true'){
                                        html_estatus = '<option value="true"  selected="yes">Vigente</option>';
                                        html_estatus += '<option value="true" >Cancelado</option>';
                                    }else{
                                        var html_estatus = '<option value="true" >Vigente</option>';
                                        html_estatus += '<option value="true" selected="yes">Cancelado</option>';
                                    }
                                    $select_estatus.append(html_estatus);
                                    
                                    
                                    //alimenta el select de $select_cierre_oportunidad
                                    var html_cierreop = '';
                                    if(entry['Oportunidad']['0']['cierre_oportunidad'] == 0){
                                        html_cierreop = '<option value="0"  selected="yes">Sin cerrar</option>';
                                        html_cierreop += '<option value="1">Ganada</option>';
                                        html_cierreop += '<option value="2">Perdida</option>';
                                    }else{
                                        if(entry['Oportunidad']['0']['cierre_oportunidad'] == 1){
                                            html_cierreop = '<option value="0" >Sin cerrar</option>';
                                            html_cierreop += '<option value="1" selected="yes">Ganada</option>';
                                            html_cierreop += '<option value="2">Perdida</option>';
                                        }else{
                                            html_cierreop = '<option value="0" >Sin cerrar</option>';
                                            html_cierreop += '<option value="1">Ganada</option>';
                                            html_cierreop += '<option value="2" selected="yes">Perdida</option>';
                                        }
                                    }
                                    $select_cierre_oportunidad.append(html_cierreop);
                                    
                                    
                                    
                                    
                                    
                                    $submit_actualizar.bind('click',function(){
                                       
                                        var selec=1;
                                        
                                        if(selec > 0){
                                            return true;
                                        }else{
                                            jAlert("No se puede actualizar", 'Atencion!');
                                            return false;
                                        }
                                    });


                                    //Ligamos el boton cancelar al evento click para eliminar la forma
                                    $cancelar_plugin.bind('click',function(){
                                            var remove = function() { $(this).remove(); };
                                            $('#forma-crmoportunidades-overlay').fadeOut(remove);
                                    });

                                    $cerrar_plugin.bind('click',function(){
                                            var remove = function() { $(this).remove(); };
                                            $('#forma-crmoportunidades-overlay').fadeOut(remove);
                                            $buscar.trigger('click');
                                    });
                                });
                                
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllOpotunidades.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllOpotunidades.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaOpotunidades00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
	
    $get_datos_grid();
    
    
});