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
                return this.contextpath + "/controllers/proordenproduccion";
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
        
        var $cadena_especificaciones = "";
        var $cadena_procedimientos = "";
        var array_almacenes = new Array(); //este arreglo para la lista de almacenes
        var array_sucursales = new Array(); //este arreglo para la lista de asucursales de la empresa
        var array_extradata = new Array(); //este arreglo para la lista de asucursales de la empresa
        
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
        var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/proordenproduccion";
	
        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_orden = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Orden de Producci&oacute;n');
	
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
	});
        
        
	var to_make_one_search_string = function(){
            var valor_retorno = "";
            
            var signo_separador = "=";
            valor_retorno += "folio_orden" + signo_separador + $campo_busqueda_folio.val() + "|";
            valor_retorno += "tipo_orden" + signo_separador + $select_buscador_tipoorden.val() + "|";
            valor_retorno += "sku_producto_busqueda" + signo_separador + $sku_producto_busqueda.val() + "|";
            
            return valor_retorno;
	};
        
        /*
        var $chek1 = $('#barra_buscador').find('.tabla_buscador').find('#chek1');
        var $chek2 = $('#barra_buscador').find('.tabla_buscador').find('#chek2');
	if($chek1.is(':checked')){
            $chek2.removeAttr('checked');
        }else{
            $chek1.removeAttr('checked');
            $chek2.attr('checked',true);
        }
        
        $('#barra_buscador').find('.tabla_buscador').find('#chek1').change(function(event){
            event.preventDefault();
            if($(this).is(':checked')){
                $('#barra_buscador').find('.tabla_buscador').find('#chek1').attr('checked',true);
                $('#barra_buscador').find('.tabla_buscador').find('#chek2').attr('checked',false);
            }else{
                $('#barra_buscador').find('.tabla_buscador').find('#chek1').attr('checked',false);
                $('#barra_buscador').find('.tabla_buscador').find('#chek2').attr('checked',true);
            }
        });
        
        $('#barra_buscador').find('.tabla_buscador').find('#chek2').change(function(event){
            event.preventDefault();
            if($(this).is(':checked')){
                $('#barra_buscador').find('.tabla_buscador').find('#chek1').attr('checked',false);
                $('#barra_buscador').find('.tabla_buscador').find('#chek2').attr('checked',true);
            }else{
                $('#barra_buscador').find('.tabla_buscador').find('#chek1').attr('checked',true);
                $('#barra_buscador').find('.tabla_buscador').find('#chek2').attr('checked',false);
            }
        });
        */
        /*
        $chek2.click(function(event) {
            var $chek1 = $('#barra_buscador').find('.tabla_buscador').find('#chek1');
            var $chek2 = $('#barra_buscador').find('.tabla_buscador').find('#chek2');
            if($chek2.is(':checked')){
                $chek1.attr('checked',false);
            }else{
                $chek1.attr('checked',false);
                $chek2.attr('checked',true);
            }
        });
        */
        
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
		
		$campo_busqueda_folio.focus();
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
		$campo_busqueda_folio.focus();
	});
	
	//Aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($select_buscador_tipoorden, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($sku_producto_busqueda, $buscar);
	
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
        
        
        //verifica si lo que viene es un -1 y lo convierte a letras N.A.
       $convierte_numero_caracter = function(cadena){
           cadena_tmp = parseInt(cadena);
           if(!isNaN(cadena_tmp)){
               if(cadena_tmp == -1 ){
                   return "N.A.";
               }else{
                   return cadena;
               }
           }else{
               return "N.A.";
           }
       }
        
        //verifica si lo que viene es un caracter y lo convierte a numero
        $convierte_caracter_numero = function(cadena){
           cadena = cadena.toUpperCase();
           if(/NA/.test(cadena)  || /N.A./.test(cadena) ){
               return -1;
           }else{
               return cadena;
           }
        }
       
       
       //verifica que lo que se esta mandando en una cadena se vaerdadera
       $verificar_cadena_verdadera = function($option, $cadena){
           $cadena_retorno = "";
           if($cadena == "true" || $cadena == "TRUE"){
               $cadena_retorno = "";
           }else{
               $cadena_retorno = $cadena;
           }
           
           if($option == "true" || $option == "TRUE"){
               if($cadena_retorno == "true" || $cadena_retorno == "TRUE"  || $cadena_retorno == ""){
                   $cadena_retorno = $cadena_retorno;
               }
           }else{
               $cadena_retorno = $cadena+$option+"\n";
           }
           
           return $cadena_retorno;
       }
       
       //valida que no pueda meter valores diferentes de enteros y diferentes de N.A. en las especificaciones
       $compara_cantidades_especificaciones = function(valor1, campo){
           
           retorno = "true";
           
           valor1 = valor1.toUpperCase();
           
           if((/^NA$/.test(valor1)  || /^N.A.$/.test(valor1)) ){
              retorno = "true";
           }else{
               valor1_1 = parseFloat(valor1);
               if(isNaN(valor1_1)){
                   retorno = "Verifique el correcto llenado para "+campo;
               }else{
                   retorno = "true";
               }
           }
           
           return retorno;
       }
        
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-proordenproduccion-window').find('select[name=prodtipo]');
            $('#forma-proordenproduccion-window').find('#submit').mouseover(function(){
                $('#forma-proordenproduccion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
            });
            $('#forma-proordenproduccion-window').find('#submit').mouseout(function(){
                $('#forma-proordenproduccion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
            });
            
            $('#forma-proordenproduccion-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-proordenproduccion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            });
            $('#forma-proordenproduccion-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-proordenproduccion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            });
            
            $('#forma-proordenproduccion-window').find('#close').mouseover(function(){
                $('#forma-proordenproduccion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            });
            $('#forma-proordenproduccion-window').find('#close').mouseout(function(){
                $('#forma-proordenproduccion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            });
            
            $('#forma-proordenproduccion-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-proordenproduccion-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-proordenproduccion-window').find(".contenidoPes:first").show(); //Show first tab content
            
            //On Click Event
            $('#forma-proordenproduccion-window').find("ul.pestanas li").click(function() {
                $('#forma-proordenproduccion-window').find(".contenidoPes").hide();
                $('#forma-proordenproduccion-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-proordenproduccion-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
        
        
        
        $autocomplete_input = function($campo, json_input){
            
            $campo.autocomplete({
                source: function(request, response){
                    
                    $arreglo = {'cadena':$campo.val(),
                            'iu':config.getUi()
                            };
                           
                    $.post(json_input, $arreglo, function(data){
                        response($.map(data, function(item) {
                            return {
                                label: item.titulo,
                                value: item.id
                              }
                        }))
                    }, "json");
                },
                 minLength: 2,
                 dataType: "json",
                 cache: false,
                 focus: function(event, ui) {
                    return false;
                 },
                 select: function(event, ui) {
                    this.value = ui.item.label;
                    return false;
                 }
             });
        }
        
        $add_suboprocesos = function(id_reg,producto_id, persona,equipo,eq_adicional,cantidad,subprocesos, unidad, unidad_id, densidad){
            
            contador = 0;
            $.each(subprocesos,function(entryIndex,subproceso){
                if(contador != 0){
                    
                    var $tabla_productos_orden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                    var trCount = $("tr", $tabla_productos_orden).size();
                    trCount++;
                    
                    trr = '<tr>';
                    if(id_reg == "0"){
                            
                            trr += '<td width="65"  align="center" colspan="3" class="grid1">';
                                trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                                trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                                trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+producto_id +'">';
                                trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                            trr += '</td>';
                            
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="subproceso" value="'+subproceso['pro_subprocesos_titulo']+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                                trr += '<input type="hidden" name="subproceso_id" value="'+subproceso['pro_subprocesos_id']+'" >';
                                trr += '<input type="hidden" name="pro_subproceso_prod_id" value="'+subproceso['pro_subproceso_prod_id']+'" >';
                            trr += '</td>';
                            
                            trr += '<td width="100" class="grid1">';
                                //trr += '<a href="#remov_persona" id="remov_persona'+trCount+'">-</a>';
                                trr += '<input type="text" name="persona" id="persona'+trCount+'" value="'+persona+'"  style="width:70px;">';
                                //trr += '<a href="#add_persona" id="add_persona'+trCount+'">+</a>';
                            trr += '</td>';
                            
                            
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="equipo" id="equipo'+trCount+'" value="'+equipo+'"  style="width:70px;">';
                            trr += '</td>';
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="eq_adicional" id="eq_adicional'+trCount+'" value="'+eq_adicional+'"  style="width:70px;">';
                            trr += '</td>';
                            trr += '<td width="100" class="grid1"><input type="text" id="cantidad'+trCount+'" name="cantidad" value="'+cantidad+'"  style="width:70px;"></td>';
                        
                    }else{
                        
                            trr += '<td width="65"  align="center" colspan="3" class="grid1">';
                                trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                                trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                                trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+producto_id +'">';
                                trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                            trr += '</td>';
                            
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="subproceso" value="'+subproceso['pro_subprocesos_titulo']+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                                trr += '<input type="hidden" name="subproceso_id" value="'+subproceso['pro_subprocesos_id']+'" >';
                                trr += '<input type="hidden" name="pro_subproceso_prod_id" value="'+subproceso['pro_subproceso_prod_id']+'" >';
                            trr += '</td>';
                            
                            trr += '<td width="100" class="grid1">';
                                //trr += '<a href="#remov_persona" id="remov_persona'+trCount+'">-</a>';
                                trr += '<input type="text" name="persona" id="persona'+trCount+'" value="'+persona+'"  style="width:70px;">';
                                //trr += '<a href="#add_persona" id="add_persona'+trCount+'">+</a>';
                            trr += '</td>';
                            
                            
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="equipo" id="equipo'+trCount+'" value="'+equipo+'"  style="width:70px;">';
                            trr += '</td>';
                            trr += '<td width="100" class="grid1">';
                                trr += '<input type="text" name="eq_adicional" id="eq_adicional'+trCount+'" value="'+eq_adicional+'"  style="width:70px;">';
                            trr += '</td>';
                            trr += '<td width="100" class="grid1"><input type="text" id="cantidad'+trCount+'" name="cantidad" value="'+cantidad+'"  style="width:70px;"></td>';
                    }
                        trr += '<td width="80" class="grid1">';
                        trr += '<select id="unidad_default'+trCount+'" name="unidad_default" >';
                        unidad = unidad.toUpperCase();
                        
                        if(/^KILO*|KILOGRAMO$/.test(unidad.toUpperCase())){
                            trr += '<option value="'+unidad_id+'" name="unidad_id" selected="yes">'+unidad+'</option>';
                            trr += '<option value="0">LITRO</option>';
                            //, unidad_id, densidad
                        }else{
                            trr += '<option value="'+unidad_id+'" selected="yes">'+unidad+'</option>';
                            trr += '<option value="0">KILO</option>';
                        }
                        
                        trr += '</select>';
                        trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                        trr += '<input type="hidden" name="unidad_id" value="'+unidad_id+'" >';
                        trr += '</td>';
                        
                    trr += '</tr>';
                    
                    $tabla_productos_orden.append(trr);
                    
                    
                    $aplicar_evento_keypress($tabla_productos_orden.find('#cantidad'+ trCount));
                    
                    //se pone todo en kilos, que es lo que debe de ser por defecto
                    $tmp_parent = $tabla_productos_orden.find('#unidad_default'+ trCount).parent().parent();
                    //$tmp_parent = $(this).parent().parent();
                    densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                    text_selected = $tmp_parent.find('select option:selected').text();
                    cantidad_default = $tmp_parent.find('input[name=cantidad]');
                    $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'inicio');
                    $event_changue_input_cantidad(cantidad_default);
                    
                    //para que al cambiar de kilos a litros o de litros a kilos, realize los calculos de acuerdp a la densidad
                    $tabla_productos_orden.find('#unidad_default'+ trCount).change(function() {
                        $tmp_parent = $(this).parent().parent();
                        densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                        text_selected = $tmp_parent.find('select option:selected').text();
                        cantidad_default = $tmp_parent.find('input[name=cantidad]');
                        $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'grid');
                    });
                    
                    //AL CAMBIAR UNA CANTIDAD EN UN SUBPROCESO, LO CAMBIA EN TODOS LOS DEMAS SUBPROCESOS DEL PRODUCTO
                    $tabla_productos_orden.find('#cantidad'+ trCount).blur(function() {
                        cantidad_tr = $(this).val();
                        inv_prod_id_tr = $(this).parent().parent().find('input[name=inv_prod_id]').val();
                        if( ($(this).val() != ' ') && ($(this).val() != '') && ($(this).val() != null ) ){
                            $tabla_productos_orden.find('tr').each(function(){
                                inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                                if(inv_prod_id_tr == inv_prod_id_tmp){
                                    $(this).find('input[name=cantidad]').val(cantidad_tr);
                                }
                            });
                        }
                    });
                    
                    $tabla_productos_orden.find('#remov_persona'+ trCount).bind('click',function(event){
                        //alert($(this).parent().html());
                    });
                    
                    $tabla_productos_orden.find('#add_persona'+ trCount).bind('click',function(event){
                        //alert($(this).parent().html());
                    });
                    
                    //para el autocomplete
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_operarios.json';
                    $autocomplete_input($tabla_productos_orden.find('#persona'+trCount+''), input_json);
                    
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipo.json';
                    $autocomplete_input($tabla_productos_orden.find('#equipo'+trCount+''), input_json);
                    
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipoadicional.json';
                    $autocomplete_input($tabla_productos_orden.find('#eq_adicional'+trCount+''), input_json);
                    
                    /*
                    $tabla_productos_orden.find('a[href^=eliminar'+ trCount+']').bind('click',function(event){
                        event.preventDefault();
                        if(parseInt($(this).parent().find('#delete').val()) != 0){
                            $(this).parent().find('#delete').val(0);
                            $(this).parent().parent().hide();
                        }
                    });
                    */
                    
                }
                contador++;
            });
        }
	
        
        
        
        //para agregar productos, cuando la orden esta en estatus 1 y 2
        //$add_grid_componente_orden(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],""       ,""    ,""          , 0);
        $add_grid_componente_orden = function(id_reg,producto_id,sku,descripcion,persona,maquina,eq_adicional,cantidad, subprocesos, proceso_flujo_id, unidad, unidad_id, densidad){
            
            if(subprocesos == ""){
                jAlert("El producto "+sku+" no tiene subprocesos", 'Atencion!');
            }else{
                
                var $tabla_productos_orden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                var trCount = $("tr", $tabla_productos_orden).size();
                trCount++;
                
                trr = '<tr>';
                    trr += '<td width="61" class="grid1" align="center">';
                        trr += '<a href="#eliminar'+trCount+'">Eliminar</a>';
                        
                        trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                        trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                        trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+producto_id +'">';
                    trr += '</td>';
                    trr += '<td width="80" class="grid1" align="center">';
                        trr += '<input type="text" name="sku'+trCount+'" value="'+sku+'"  class="borde_oculto" readOnly="true" style="width:88px;">';
                    trr += '</td>';
                    trr += '<td width="446" class="grid1"><input type="text" name="descripcion'+trCount+'" value="'+descripcion+'"  class="borde_oculto" readOnly="true" style="width:198px;"></td>';
                    
                    if(id_reg == "0"){
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="subproceso" value="'+subprocesos[0]['pro_subprocesos_titulo']+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                            trr += '<input type="hidden" name="subproceso_id" value="'+subprocesos[0]['pro_subprocesos_id']+'" >';
                            trr += '<input type="hidden" name="pro_subproceso_prod_id" value="'+subprocesos[0]['pro_subproceso_prod_id']+'" >';
                        trr += '</td>';
                    }else{
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="subproceso" value="'+subprocesos['subproceso']+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                            trr += '<input type="hidden" name="subproceso_id" value="'+subprocesos['pro_subprocesos_id']+'" >';
                            trr += '<input type="hidden" name="pro_subproceso_prod_id" value="'+subprocesos['pro_subprocesos_id']+'" >';
                        trr += '</td>';
                    }
                    
                    if(proceso_flujo_id == "1"){
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="persona" id="persona'+trCount+'" value="'+persona+'"  style="width:70px;" readOnly="true">';
                        trr += '</td>';
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="equipo" id="equipo'+trCount+'" value="'+maquina+'"  style="width:70px;" readOnly="true">';
                        trr += '</td>';
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="eq_adicional" id="eq_adicional'+trCount+'" value="'+eq_adicional+'"  style="width:70px;" readOnly="true">';
                        trr += '</td>';
                        trr += '<td width="80" class="grid1"><input type="text" name="cantidad" id="cantidad'+trCount+'" value="'+cantidad+'"  style="width:70px;"></td>';
                    }
                    
                    if(proceso_flujo_id == "2"){
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="persona" id="persona'+trCount+'" value="'+persona+'"  style="width:70px;" title="'+persona+'">';
                        //    trr += '<a href="#add_persona" id="add_persona'+trCount+'">+</a>';
                        trr += '</td>';
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="equipo" id="equipo'+trCount+'" value="'+maquina+'"  style="width:70px;" >';
                        trr += '</td>';
                        trr += '<td width="100" class="grid1">';
                            trr += '<input type="text" name="eq_adicional" id="eq_adicional'+trCount+'" value="'+eq_adicional+'"  style="width:70px;" title="'+eq_adicional+'">';
                        trr += '</td>';
                        trr += '<td width="80" class="grid1"><input type="text" id="cantidad'+trCount+'" name="cantidad" value="'+cantidad+'"  style="width:70px;" readOnly="true"></td>';
                    }
                    
                    trr += '<td width="80" class="grid1">';
                    trr += '<select id="unidad_default'+trCount+'" name="unidad_default" >';
                    unidad = unidad.toUpperCase();
                    
                    if(/^KILO*|KILOGRAMO$/.test(unidad)){
                        trr += '<option value="'+unidad_id+'" name="unidad_id" selected="yes">'+unidad+'</option>';
                        trr += '<option value="0">LITRO</option>';
                        //, unidad_id, densidad
                    }else{
                        trr += '<option value="'+unidad_id+'" selected="yes">'+unidad+'</option>';
                        trr += '<option value="0">KILO</option>';
                    }
                    
                    trr += '</select>';
                    trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                    trr += '<input type="hidden" name="unidad_id" value="'+unidad_id+'" >';
                    trr += '</td>';
                    
                trr += '</tr>';
                
                $tabla_productos_orden.append(trr);
                
                $aplicar_evento_keypress($tabla_productos_orden.find('#cantidad'+ trCount));
                
                //se pone todo en kilos, que es lo que debe de ser por defecto
                $tmp_parent = $tabla_productos_orden.find('#unidad_default'+ trCount).parent().parent();
                //$tmp_parent = $(this).parent().parent();
                //alert($tmp_parent.html());
                densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                text_selected = $tmp_parent.find('select option:selected').text();
                cantidad_default = $tmp_parent.find('input[name=cantidad]');
                //alert($tmp_parent.html());
                $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'inicio');
                
                //$tmp_parent = $tabla_productos_orden.find('#unidad_default'+ trCount).parent().parent();
                //alert($tmp_parent.html());
                //$event_changue_input_cantidad(cantidad_default);
                
                
                $tabla_productos_orden.find('#cantidad'+ trCount).focus(function(e){
                    if($(this).val() == ' '){
                        $(this).val('0');
                    }
                });
                
                //AL CAMBIAR UNA CANTIDAD EN UN SUBPROCESO, LO CAMBIA EN TODOS LOS DEMAS SUBPROCESOS DEL PRODUCTO
                $tabla_productos_orden.find('#cantidad'+ trCount).blur(function() {
                    cantidad_tr = $(this).val();
                    inv_prod_id_tr = $(this).parent().parent().find('input[name=inv_prod_id]').val();
                    if( ($(this).val() != ' ') && ($(this).val() != '') && ($(this).val() != null ) ){
                        $tabla_productos_orden.find('tr').each(function(){
                            inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                            if(inv_prod_id_tr == inv_prod_id_tmp){
                                $(this).find('input[name=cantidad]').val(cantidad_tr);
                            }
                        });
                    }
                });
                
                //para que al cambiar de kilos a litros o de litros a kilos, realize los calculos de acuerdp a la densidad
                $tabla_productos_orden.find('#unidad_default'+ trCount).change(function() {
                    $tmp_parent = $(this).parent().parent();
                    densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                    text_selected = $tmp_parent.find('select option:selected').text();
                    cantidad_default = $tmp_parent.find('input[name=cantidad]');
                    $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'grid');
                });
                
                $tabla_productos_orden.find('#add_persona'+ trCount).bind('click',function(event){
                    $tmp_parent = $(this).parent();
                    tmp_html = '<span class="person_adicionl">';
                        tmp_html += '<input type="text" name="add_persona" id="add_persona" style="width:70px;">';
                        tmp_html += '<a href="#remov_persona" id="remov_persona'+trCount+'">-</a>';
                    tmp_html += '</span>';
                    $tmp_parent.append(tmp_html);
                    //alert($tmp_parent.html());
                });
                
                
                
                //para el autocomplete
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_operarios.json';
                $autocomplete_input($tabla_productos_orden.find('#persona'+trCount+''), input_json);
                
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipo.json';
                $autocomplete_input($tabla_productos_orden.find('#equipo'+trCount+''), input_json);
                
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipoadicional.json';
                $autocomplete_input($tabla_productos_orden.find('#eq_adicional'+trCount+''), input_json);
                
                
                $tabla_productos_orden.find('a[href^=eliminar'+ trCount+']').bind('click',function(event){
                    if(parseInt($(this).parent().find('#delete').val()) != 0){
                        $(this).parent().find('#delete').val(0);
                        $(this).parent().parent().hide();
                    }
                });
                
                if(id_reg == "0"){
                    $add_suboprocesos(id_reg,producto_id, persona,maquina,eq_adicional, cantidad, subprocesos, unidad, unidad_id, densidad);
                }
            }
        }
        
        
        $recalcula_cantidades = function($tr, $table, text_selected){
            //alert(text_selected);
            inv_prod_id_tr = $tr.find('input[name=inv_prod_id]').val();
            cantidad_tr = $tr.find('input[name=cantidad]').val();
            unidad_id_tr = $tr.find('select[name=unidad_default]').val();
            text_selected = $tr.find('select option:selected').text();
            //alert(inv_prod_id_tr +" "+cantidad_tr+"   "+ unidad_id_tr);
            //alert($tr.html());
            //alert($table.html());
            $table.find('tr').each(function(){
                
                inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                
                if(inv_prod_id_tr == inv_prod_id_tmp){
                    $(this).find('input[name=cantidad]').val(cantidad_tr);
                    option_selected = $(this).find('select option:selected').text();
                    select_value =$(this).find('select[name=unidad_default]').val();
                    select =$(this).find('select[name=unidad_default]');
                    
                    id_unidad = "0";
                    select.find('option').each(function(){
                        if($(this).val() != "0"){
                            id_unidad = $(this).val();
                        }
                    });
                    
                    //alert(unidad_id_tr+"    "+id_unidad+"    "+text_selected);
                    
                    trr = '';
                    select.children().remove();
                    if(/^KILO*|KILOGRAMO*$/.test(text_selected) ){
                        //alert("asd");
                        if( unidad_id_tr != "0" ){
                            trr += '<option value="'+unidad_id_tr+'" selected="yes">'+text_selected+'</option>';
                            trr += '<option value="0">LITRO</option>';
                        }else{
                            trr += '<option value="0" selected="yes">'+text_selected+'</option>';
                            trr += '<option value="'+id_unidad+'">LITRO</option>';
                        }
                        
                    }else{
                        if( unidad_id_tr != "0" ){
                            trr += '<option value="'+unidad_id_tr+'" selected="yes">'+text_selected+'</option>';
                            trr += '<option value="0">KILO</option>';
                        }else{
                            trr += '<option value="0" selected="yes">'+text_selected+'</option>';
                            trr += '<option value="'+id_unidad+'">KILO</option>';
                        }
                    }
                    select.append(trr);
                    
                    
                    option_selected = $(this).find('select option:selected').text();
                    select_value =$(this).find('select[name=unidad_default]').val();
                    select =$(this).find('select[name=unidad_default]');
                    
                    //alert(unidad_id_tr+"    "+id_unidad+"    "+text_selected);
                    
                }
            });
            
        }
        
        $event_changue_input_cantidad = function(input_cantidad){
            $this_tr = input_cantidad.parent().parent();
            $this_table = input_cantidad.parent().parent().parent();
            cantidad_tr = $this_tr.find('input[name=cantidad]').val();
            inv_prod_id_tr = $this_tr.find('input[name=inv_prod_id]').val();
            
            input_cantidad.focus(function() {
                //alert("asd");
                $this_table.find('tr').each(function(){
                    inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                    if(inv_prod_id_tr == inv_prod_id_tmp){
                        $(this).find('input[name=cantidad]').val(cantidad_tr);
                    }
                });
            });
            
        }
        
        $event_changue_umedida = function(input_cantidad, titulo_selected, densidad, desde){
            titulo_selected = titulo_selected.toUpperCase();
            densidad_tmp = parseFloat(densidad);
            cantidad_original = parseFloat(input_cantidad.val());
            //alert(desde);
            if(desde == 'grid'){
                if(!isNaN(cantidad_original) && !isNaN(densidad_tmp)){
                    if(/^KILO*|KILOGRAMO*$/.test(titulo_selected)){
                        calculo = parseFloat(cantidad_original) * parseFloat(densidad_tmp);
                        input_cantidad.val(parseFloat(calculo).toFixed(4));
                    }else{
                        calculo = parseFloat(cantidad_original) / parseFloat(densidad_tmp);
                        input_cantidad.val(parseFloat(calculo).toFixed(4));
                    }
                }else{
                    input_cantidad.val(0);
                }
                
                $this_tr = input_cantidad.parent().parent();
                $this_table = input_cantidad.parent().parent().parent();
            }else{
                //alert(desde+"   "+cantidad_original+"    "+densidad_tmp);
                if(!isNaN(cantidad_original) && !isNaN(densidad_tmp)){
                    if(!/^KILO*|KILOGRAMO*$/.test(titulo_selected)){
                        titulo_selected = "KILO";
                        
                        //aqui falta que se seleccione kilogramo por defecto
                        calculo = parseFloat(cantidad_original) * parseFloat(densidad_tmp);
                        input_cantidad.val(parseFloat(calculo).toFixed(4));
                        
                        $this_tr_tmp = input_cantidad.parent().parent();
                        select_unidad = $this_tr_tmp.find('select[name=unidad_default]');
                        unidad_id_tr = select_unidad.val();
                        text_selected = $this_tr_tmp.find('select option:selected').text();
                        
                        select_unidad.children().remove();
                        
                        trr = '<option value="0" selected="yes" >KILO</option>';
                        trr += '<option value="'+unidad_id_tr+'" >'+text_selected+'</option>';
                        
                        select_unidad.append(trr);
                    }
                }else{
                    input_cantidad.val(0);
                }
                
                $this_tr = input_cantidad.parent().parent();
                $this_table = input_cantidad.parent().parent().parent();
                
            }
            //alert($this_tr.html());
            //alert(titulo_selected);
            $recalcula_cantidades($this_tr, $this_table, titulo_selected);
            
        }
        
        
        $add_grid_componente_orden_finalizada_o_cancelada = function(id_reg,producto_id,sku,descripcion,persona,maquina,eq_adicional,cantidad, subprocesos, proceso_flujo_id, unidad, unidad_id, densidad){
            if(subprocesos == ""){
                jAlert("El producto "+sku+" no tiene subprocesos", 'Atencion!');
            }else{
                
                var $tabla_productos_orden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                var trCount = $("tr", $tabla_productos_orden).size();
                trCount++;
                
                trr = '<tr>';
                    trr += '<td width="61" class="grid1" align="center">';
                        trr += 'Eliminar';
                        trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                        trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                        trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+producto_id +'">';
                    trr += '</td>';
                    trr += '<td width="100" class="grid1" align="center">';
                        trr += '<input type="text" name="sku'+trCount+'" value="'+sku+'"  class="borde_oculto" readOnly="true" style="width:98px;">';
                    trr += '</td>';
                    trr += '<td width="446" class="grid1"><input type="text" name="descripcion'+trCount+'" value="'+descripcion+'"  class="borde_oculto" readOnly="true" style="width:208px;"></td>';
                    
                    trr += '<td width="100" class="grid1">';
                        trr += '<input type="text" name="subproceso" value="'+subprocesos['subproceso']+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                        trr += '<input type="hidden" name="subproceso_id" value="'+subprocesos['pro_subprocesos_id']+'" >';
                        trr += '<input type="hidden" name="pro_subproceso_prod_id" value="'+subprocesos['pro_subprocesos_id']+'" >';
                    trr += '</td>';
                    
                    
                    trr += '<td width="100" class="grid1">';
                        trr += '<input type="text" name="persona" id="persona'+trCount+'" value="'+persona+'"  style="width:70px;" title="'+persona+'" readOnly="true">';
                    trr += '</td>';
                    trr += '<td width="100" class="grid1">';
                        trr += '<input type="text" name="equipo" id="equipo'+trCount+'" value="'+maquina+'"  style="width:70px;" readOnly="true">';
                    trr += '</td>';
                    trr += '<td width="100" class="grid1">';
                        trr += '<input type="text" name="eq_adicional" id="eq_adicional'+trCount+'" value="'+eq_adicional+'"  style="width:70px;" title="'+eq_adicional+'" readOnly="true">';
                    trr += '</td>';
                    trr += '<td width="100" class="grid1"><input type="text" name="cantidad" value="'+cantidad+'"  style="width:70px;" readOnly="true"></td>';
                    
                    trr += '<td width="80" class="grid1">';
                    trr += '<select id="unidad_default'+trCount+'" name="unidad_default" >';
                    unidad = unidad.toUpperCase();
                    if(/^[KILO*]$/.test(unidad)){
                        trr += '<option value="'+unidad_id+'">'+unidad+'</option>';
                        trr += '<option value="0">LITRO</option>';
                        //, unidad_id, densidad
                    }else{
                        trr += '<option value="'+unidad_id+'">'+unidad+'</option>';
                        trr += '<option value="0">KILO</option>';
                    }
                    trr += '</select>';
                    trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                    trr += '<input type="hidden" name="unidad_id" value="'+unidad_id+'" >';
                    trr += '</td>';
                    
                trr += '</tr>';
                
                $tabla_productos_orden.append(trr);
                
                
                //se pone todo en kilos, que es lo que debe de ser por defecto
                $tmp_parent = $tabla_productos_orden.find('#unidad_default'+ trCount).parent().parent();
                //$tmp_parent = $(this).parent().parent();
                densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                text_selected = $tmp_parent.find('select option:selected').text();
                cantidad_default = $tmp_parent.find('input[name=cantidad]');
                $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'inicio');
                $event_changue_input_cantidad(cantidad_default);
                
                $tabla_productos_orden.find('#unidad_default'+ trCount).change(function() {
                    $tmp_parent = $(this).parent().parent();
                    densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                    text_selected = $tmp_parent.find('select option:selected').text();
                    cantidad_default = $tmp_parent.find('input[name=cantidad]');
                    $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'grid');
                });
                
                $tabla_productos_orden.find('#add_persona'+ trCount).bind('click',function(event){
                    $tmp_parent = $(this).parent();
                    tmp_html = '<span class="person_adicionl">';
                        tmp_html += '<input type="text" name="add_persona" id="add_persona" style="width:70px;">';
                        tmp_html += '<a href="#remov_persona" id="remov_persona'+trCount+'">-</a>';
                    tmp_html += '</span>';
                    $tmp_parent.append(tmp_html);
                    //alert($tmp_parent.html());
                });
                
                
                
                //para el autocomplete
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_operarios.json';
                $autocomplete_input($tabla_productos_orden.find('#persona'+trCount+''), input_json);
                
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipo.json';
                $autocomplete_input($tabla_productos_orden.find('#equipo'+trCount+''), input_json);
                
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_autocomplete_equipoadicional.json';
                $autocomplete_input($tabla_productos_orden.find('#eq_adicional'+trCount+''), input_json);
                
                
                $tabla_productos_orden.find('a[href^=eliminar'+ trCount+']').bind('click',function(event){
                    if(parseInt($(this).parent().find('#delete').val()) != 0){
                        $(this).parent().find('#delete').val(0);
                        $(this).parent().parent().hide();
                    }
                });
                
                if(id_reg == "0"){
                    $add_suboprocesos(id_reg,producto_id, persona,maquina,eq_adicional, cantidad, subprocesos, unidad, unidad_id, densidad);
                }
            }
        }
        
        $aplicar_evento_keypress = function( $campo_input ){
            //validar campo cantidad recibida, solo acepte numeros y punto
            $campo_input.keypress(function(e){
                // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                if(e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                        return true;
                }else {
                        return false;
                }
            });
		}
        
        $aplicar_evento_keypress_input_lote = function( $campo_input ){
            //validar campo cantidad recibida, solo acepte numeros y punto
            $campo_input.keypress(function(e){
                // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                if(e.which==13 ) {
                    if ( $(this).val()!=''  &&  $(this).val()!=' ' && $(this).val()!=null ){
                        var $tr_padre = $(this).parent().parent();
                        $obtiene_datos_lote($tr_padre);
                    }else{
                        jAlert("Ingresa un n&uacute;mero de Lote.", 'Atencion!');
                    }
                    return false;
                }
            });
		}
        
        
		$aplicar_evento_focus_input_lote = function( $campo_input ){
            //al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
            $campo_input.focus(function(e){
                if($(this).val() == ' '){
                    $(this).val('');
                }
            });
		}
        
        
		$aplicar_evento_click_input_lote = function( $campo_input ){
            //validar campo cantidad recibida, solo acepte numeros y punto
            $campo_input.dblclick(function(e){
                $(this).select();
            });
		}
	
        $aplicar_evento_blur_input_lote = function( $campo_input ){
            //pone espacio en blanco al perder el enfoque, cuando no se ingresa un valor
            $campo_input.blur(function(e){
                if ( $(this).val() == ''  || $(this).val() == null ){
                    $(this).val(' ');
                }else{
                    //aqui va llamada a funcion que busca datos del lote
                    //var $tr_padre = $(this).parent().parent();
                    //$obtiene_datos_lote($tr_padre);
                }
            });
		}
        
        $aplicar_evento_focus_input_lote = function( $campo_input ){
            //al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
            $campo_input.focus(function(e){
                if($(this).val() == ' '){
                        $(this).val('');
                }
            });
		}
        
        /* Plugin para agregar las espedificaciones */
        //$tr_parent//el ter al que se le da click
        //$table_parent //la tabla completa
        $plugin_especificaciones = function(id_reg, id_subp,id_prod, sku, data_string, accion, $tr_parent, $subproceso, id_reg_esp){
            
            $cadeana_retorno = data_string;
            $(this).modalPanel_Especificaciones();
            var $dialogoc =  $('#forma-especificaciones-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.panel_especificacoines').find('table.forma_especificacoines').clone());
            
            $('#forma-especificaciones-window').css({"margin-left": -200, 	"margin-top": -140});
            
            
            /*campos para las especificaciones*/
            var $campo_id_reg = $('#forma-especificaciones-window').find('input[name=id_reg]');
            var $campo_id_subp = $('#forma-especificaciones-window').find('input[name=id_subp]');
            var $campo_id_prod = $('#forma-especificaciones-window').find('input[name=id_prod]');
            
            
            
            var $campo_fineza = $('#forma-especificaciones-window').find('input[name=fineza]');
            var $campo_viscosidad1 = $('#forma-especificaciones-window').find('input[name=viscosidad1]');
            var $campo_viscosidad2 = $('#forma-especificaciones-window').find('input[name=viscosidad2]');
            var $campo_viscosidad3 = $('#forma-especificaciones-window').find('input[name=viscosidad3]');
            var $campo_densidad = $('#forma-especificaciones-window').find('input[name=densidad]');
            var $campo_volatil = $('#forma-especificaciones-window').find('input[name=volatil]');
            var $campo_cubriente = $('#forma-especificaciones-window').find('input[name=cubriente]');
            var $campo_tono = $('#forma-especificaciones-window').find('input[name=tono]');
            var $campo_brillo = $('#forma-especificaciones-window').find('input[name=brillo]');
            var $campo_dureza = $('#forma-especificaciones-window').find('input[name=dureza]');
            var $campo_adherencia = $('#forma-especificaciones-window').find('input[name=adherencia]');
            var $campo_hidrogeno = $('#forma-especificaciones-window').find('input[name=hidrogeno]');
            
            //para desplegar los instrumentos en el select
            var $select_inst_fineza = $('#forma-especificaciones-window').find('select[name=inst_fineza]');
            var $select_inst_viscosidad1 = $('#forma-especificaciones-window').find('select[name=inst_viscosidad1]');
            var $select_inst_viscosidad2 = $('#forma-especificaciones-window').find('select[name=inst_viscosidad2]');
            var $select_inst_viscosidad3 = $('#forma-especificaciones-window').find('select[name=inst_viscosidad3]');
            var $select_inst_densidad = $('#forma-especificaciones-window').find('select[name=inst_densidad]');
            var $select_inst_volatil = $('#forma-especificaciones-window').find('select[name=inst_volatil]');
            var $select_inst_cubriente = $('#forma-especificaciones-window').find('select[name=inst_cubriente]');
            var $select_inst_tono = $('#forma-especificaciones-window').find('select[name=inst_tono]');
            var $select_inst_brillo = $('#forma-especificaciones-window').find('select[name=inst_brillo]');
            var $select_inst_dureza = $('#forma-especificaciones-window').find('select[name=inst_dureza]');
            var $select_inst_adherencia = $('#forma-especificaciones-window').find('select[name=inst_adherencia]');
            var $select_inst_hidrogeno = $('#forma-especificaciones-window').find('select[name=inst_hidrogeno]');
            
            
            var $aceptar_acepta_especificacaiones = $('#forma-especificaciones-window').find('#acepta_especificacaiones');
            var $cancelar_cencela_especificacaiones = $('#forma-especificaciones-window').find('#cencela_especificacaiones');
            
            $campo_id_reg.val(id_reg);
            $campo_id_subp.val(id_subp);
            $campo_id_prod.val(id_prod);
            //id_reg, id_subp,id_prod
            
            //alert(accion+"  edit   "+data_string+"       "+data_string);
            
            if(accion == "edit" && data_string != "" && data_string != " "){
                
                $campos_espliteados = data_string.split("&&&");
                
                $campo_fineza.val($convierte_numero_caracter($campos_espliteados[0]));
                $campo_viscosidad1.val($convierte_numero_caracter($campos_espliteados[1]));
                $campo_viscosidad2.val($convierte_numero_caracter($campos_espliteados[2]));
                $campo_viscosidad3.val($convierte_numero_caracter($campos_espliteados[3]));
                $campo_densidad.val($convierte_numero_caracter($campos_espliteados[4]));
                $campo_volatil.val($convierte_numero_caracter($campos_espliteados[5]));
                $campo_cubriente.val($convierte_numero_caracter($campos_espliteados[6]));
                $campo_tono.val($convierte_numero_caracter($campos_espliteados[7]));
                $campo_brillo.val($convierte_numero_caracter($campos_espliteados[8]));
                $campo_dureza.val($campos_espliteados[9]);
                $campo_adherencia.val($convierte_numero_caracter($campos_espliteados[10]));
                $campo_hidrogeno.val($convierte_numero_caracter($campos_espliteados[11]));
                
                /*
                $campo_fineza1.val($convierte_numero_caracter($campos_espliteados[12]));
                $campo_viscosidad11.val($convierte_numero_caracter($campos_espliteados[13]));
                $campo_viscosidad21.val($convierte_numero_caracter($campos_espliteados[14]));
                $campo_viscosidad31.val($convierte_numero_caracter($campos_espliteados[15]));
                $campo_densidad1.val($convierte_numero_caracter($campos_espliteados[16]));
                $campo_volatil1.val($convierte_numero_caracter($campos_espliteados[17]));
                $campo_cubriente1.val($convierte_numero_caracter($campos_espliteados[18]));
                $campo_tono1.val($convierte_numero_caracter($campos_espliteados[19]));
                $campo_brillo1.val($convierte_numero_caracter($campos_espliteados[20]));
                $campo_dureza1.val($campos_espliteados[21]);
                $campo_adherencia1.val($convierte_numero_caracter($campos_espliteados[22]));
                $campo_hidrogeno1.val($convierte_numero_caracter($campos_espliteados[23]));
                */
                
                //llena los selects para los instrumentos
                $html_subprocesos = "";
                encontrado = 0; 
                $select_inst_fineza.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == parseInt($campos_espliteados[12])){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'"  selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_fineza.append($html_subprocesos);
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_viscosidad1.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[13]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_viscosidad1.append($html_subprocesos);
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_viscosidad2.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[14]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_viscosidad2.append($html_subprocesos);
                
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_viscosidad3.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[15]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_viscosidad3.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_densidad.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[16]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_densidad.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_volatil.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[17]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_volatil.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_cubriente.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[18]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_cubriente.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_tono.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[19]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_tono.append($html_subprocesos);
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_brillo.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[20]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_brillo.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_dureza.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[21]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_dureza.append($html_subprocesos);
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_adherencia.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[22]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_adherencia.append($html_subprocesos);
                
                
                $html_subprocesos = "";
                encontrado = 0;
                $select_inst_hidrogeno.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    if(instrumento['id'] == $campos_espliteados[23]){
                        encontrado = 1;
                        $html_subprocesos += '<option value="'+instrumento['id']+'" selected="yes">'+instrumento['titulo']+'</option>';
                    }else{
                        $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                    }
                });
                if(encontrado == 0){
                    $html_subprocesos += '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                }
                $select_inst_hidrogeno.append($html_subprocesos);
                
                
            }else{
                $campo_fineza.val('N.A.');
                $campo_viscosidad1.val('N.A.');
                $campo_viscosidad2.val('N.A.');
                $campo_viscosidad3.val('N.A.');
                $campo_densidad.val('N.A.');
                $campo_volatil.val('N.A.');
                $campo_cubriente.val('N.A.');
                $campo_tono.val('N.A.');
                $campo_brillo.val('N.A.');
                $campo_dureza.val('N.A.');
                $campo_adherencia.val('N.A.');
                $campo_hidrogeno.val('N.A.');
                
                /*
                $campo_fineza1.val('N.A.');
                $campo_viscosidad11.val('N.A.');
                $campo_viscosidad21.val('N.A.');
                $campo_viscosidad31.val('N.A.');
                $campo_densidad1.val('N.A.');
                $campo_volatil1.val('N.A.');
                $campo_cubriente1.val('N.A.');
                $campo_tono1.val('N.A.');
                $campo_brillo1.val('N.A.');
                $campo_dureza1.val('N.A.');
                $campo_adherencia1.val('N.A.');
                $campo_hidrogeno1.val('N.A.');
                */
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_fineza.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_fineza.append($html_subprocesos);
                
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_viscosidad1.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_viscosidad1.append($html_subprocesos);
                
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_viscosidad2.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_viscosidad2.append($html_subprocesos);
                
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_viscosidad3.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_viscosidad3.append($html_subprocesos);
                
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_densidad.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_densidad.append($html_subprocesos);
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_volatil.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_volatil.append($html_subprocesos);
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_cubriente.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_cubriente.append($html_subprocesos);
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_tono.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_tono.append($html_subprocesos);
                
                $html_subprocesos = '<option value="0"  selected="yes">[--Instrumento--]</option>';
                $select_inst_brillo.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_brillo.append($html_subprocesos);
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_dureza.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_dureza.append($html_subprocesos);
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_adherencia.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_adherencia.append($html_subprocesos);
                
                $html_subprocesos = '<option value="-1"  selected="yes">[--Instrumento--]</option>';
                $select_inst_hidrogeno.children().remove();
                $.each(array_instrumentos,function(entryIndex,instrumento){
                    $html_subprocesos += '<option value="'+instrumento['id']+'">'+instrumento['titulo']+'</option>';
                });
                $select_inst_hidrogeno.append($html_subprocesos);
            }
            
            $aceptar_acepta_especificacaiones.click(function(event){
                
                event.preventDefault();
                
                $valida_result = "";
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_fineza.val(),  "Fineza"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_viscosidad1.val(),  "Viscosidad"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_viscosidad2.val(),  "Viscosidad"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_viscosidad3.val(),  "Viscosidad"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_densidad.val(),  "Densidad"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_volatil.val(), "No Vol&aacute;tiles"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_cubriente.val(), "Cubriente"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_tono.val(),  "Tono"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_brillo.val(), "Brillo"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_adherencia.val(),  "Adherencia"), $valida_result);
                $valida_result = $verificar_cadena_verdadera($compara_cantidades_especificaciones($campo_hidrogeno.val(),  "pH"), $valida_result);
                
                //$campo_tono.val($convierte_numero_caracter($campos_espliteados[7]));
                
                if($valida_result == "true" || $valida_result == "TRUE" || $valida_result == ""){
                    
                    $tr_posicion = $tr_parent.find('input[name=posicion]').val();
                    td_seleccionado = $tr_parent.find('.subproceso'+$tr_posicion);
                    
                    $cadeana_retorno = $convierte_caracter_numero($campo_fineza.val())+"&&&"+$convierte_caracter_numero($campo_viscosidad1.val())+"&&&"+$convierte_caracter_numero($campo_viscosidad2.val())+"&&&";
                    $cadeana_retorno += $convierte_caracter_numero($campo_viscosidad3.val())+"&&&"+$convierte_caracter_numero($campo_densidad.val())+"&&&"+$convierte_caracter_numero($campo_volatil.val())+"&&&"+$convierte_caracter_numero($campo_cubriente.val())+"&&&"+$convierte_caracter_numero($campo_tono.val())+"&&&";
                    $cadeana_retorno += $convierte_caracter_numero($campo_brillo.val())+"&&&"+$campo_dureza.val()+"&&&"+$convierte_caracter_numero($campo_adherencia.val())+"&&&"+$convierte_caracter_numero($campo_hidrogeno.val())+"&&&";
                    /*
                    $cadeana_retorno += $convierte_caracter_numero($campo_fineza1.val())+"&&&"+$convierte_caracter_numero($campo_viscosidad11.val())+"&&&"+$convierte_caracter_numero($campo_viscosidad21.val())+"&&&";
                    $cadeana_retorno += $convierte_caracter_numero($campo_viscosidad31.val())+"&&&"+$convierte_caracter_numero($campo_densidad1.val())+"&&&"+$convierte_caracter_numero($campo_volatil1.val())+"&&&"+$convierte_caracter_numero($campo_cubriente1.val())+"&&&"+$convierte_caracter_numero($campo_tono1.val())+"&&&";
                    $cadeana_retorno += $convierte_caracter_numero($campo_brillo1.val())+"&&&"+$campo_dureza1.val()+"&&&"+$convierte_caracter_numero($campo_adherencia1.val())+"&&&"+$convierte_caracter_numero($campo_hidrogeno1.val());
                    */
                    $cadeana_retorno += $select_inst_fineza.val()+"&&&"+$select_inst_viscosidad1.val()+"&&&"+$select_inst_viscosidad2.val()+"&&&";
                    $cadeana_retorno += $select_inst_viscosidad3.val()+"&&&"+$select_inst_densidad.val()+"&&&"+$select_inst_volatil.val()+"&&&";
                    $cadeana_retorno += $select_inst_cubriente.val()+"&&&"+$select_inst_tono.val()+"&&&"+$select_inst_brillo.val()+"&&&";
                    $cadeana_retorno += $select_inst_dureza.val()+"&&&"+$select_inst_adherencia.val()+"&&&"+$select_inst_hidrogeno.val();
                    
                    if(accion == "edit"){
                        $tr_parent.find('input[name=especificaciones]').val($cadeana_retorno);
                    }
                    
                    if(accion == "new"){
                        //alert($grid_parent.html());
                        //$tr_parent.after(html_tabla);
                        var $tabla_productos_orden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                        var trCount = $("tr", $tabla_productos_orden).size();
                        trCount++;
                        
                        //alert($tr_posicion);
                        posicion_mas_1 = (parseInt($tr_posicion) + 1);
                        //tr_actual = td_seleccionado.parent().parent();
                        
                        html_tabla = '<tr>';
                            html_tabla += '<td width="681" colspan="5" class="grid1" align="center">&nbsp;';
                                html_tabla += '<input type="hidden" id="delete" name="eliminar" value="1">';
                                html_tabla += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="1">';
                                html_tabla += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                                html_tabla += '<input type="hidden" id="id_reg_esp" name="id_reg_esp" value="'+id_reg_esp+'">';
                                html_tabla += '<input type="hidden" name="subproceso_id" value="'+id_subp+'" >';
                            html_tabla += '</td>';
                            html_tabla += '<td width="100" class="grid1" align="center">&nbsp;';
                                html_tabla += '<input type="hidden" name="especificaciones" id="especificaciones'+trCount+'" value="'+$cadeana_retorno+'"  style="width:70px;" readOnly="true">';
                                html_tabla += '<a href="#ver_especificaciones" id="ver_especificaciones'+trCount+'" title="Ver las especificaici&oacute;n">Res. Analisis</a>&nbsp;&nbsp;&nbsp;';
                                html_tabla += '<a href="#remov_especificacion" id="remov_especificacion'+trCount+'" title="ELiminar especificaci&oacute;n">-</a>';
                            html_tabla += '</td>';
                            html_tabla += '<td width="100" class="grid1" align="center">&nbsp;';
                            html_tabla += '</td>';
                        html_tabla += '</tr>';
                        
                        $tr_parent.after(html_tabla);
                        //$table_parent
                        $tabla_productos_orden.find('#remov_especificacion'+trCount).click(function(event){
                            event.preventDefault();
                            $(this).parent().parent().find('input[name=eliminar]').val("0");
                            $(this).parent().parent().find('input[name=especificaciones1]').val("");
                            
                            $(this).parent().parent().hide();
                        });
                        
                        
                        //para pder ver el detalle de la especificacion
                        $tabla_productos_orden.find('#ver_especificaciones'+ trCount).bind('click',function(event){
                            event.preventDefault();
                            
                            $tr_parent = $(this).parent().parent();
                            $id_producto = $tr_parent.find('input[name=inv_prod_id]');
                            $subproceso_id = $tr_parent.find('input[name=subproceso_id]');
                            $subproceso = $tr_parent.find('input[name=subproceso]');
                            $especificaciones = $tr_parent.find('input[name=especificaciones]');
                            
                            $id_reg_tmp = $tr_parent.find('input[name=id_reg]');
                            $id_reg_esp = $tr_parent.find('input[name=id_reg_esp]');
                            
                            accion = "edit";
                            
                            $plugin_especificaciones($id_reg_tmp.val(), $subproceso_id.val(),$id_producto.val(), "s", $especificaciones.val(), accion, $tr_parent, $subproceso, $id_reg_esp.val());
                        });
                    }
                    
                    var remove = function() {$(this).remove();};
                    $('#forma-especificaciones-overlay').fadeOut(remove);
                    $cadena_especificaciones = $cadeana_retorno;
                    
                    
                    /*variables para accesar a los campos de el prugin principal*/
                    var $submit_actualizar = $('#forma-proordenproduccion-window').find('#submit');
                    var $command_selected = $('#forma-proordenproduccion-window').find('input[name=command_selected]');
                    var $confirmar_enviar_produccion = $('#forma-proordenproduccion-window').find('#confirmar_enviar_produccion');
                    var $tabla_productos_preorden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                    var $especificaicones_lista = $('#forma-proordenproduccion-window').find('input[name=especificaicones_lista]');
                    var $submit_actualizar = $('#forma-proordenproduccion-window').find('#submit');
                    
                    /*guarda resultados de analisis*/
                    //$confirmar_enviar_produccion.bind('click',function(){
                        
                        $command_selected.val("3");
                        $agrega_esp_en_blanco_grid();
                        var trCount = $("tr", $tabla_productos_preorden).size();
                        if(trCount > 0){
                            
                            jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
                                
                                cadena_pos = "";
                                $tabla_productos_preorden.find('tr').each(function(){
                                    
                                    eliminar_tmp = $(this).find('input[name=eliminar]').val();
                                    id_reg_tmp = $(this).find('input[name=id_reg]').val();
                                    inv_prod_id = $(this).find('input[name=inv_prod_id]').val();
                                    subproceso_id = $(this).find('input[name=subproceso_id]').val();
                                    especificaciones = $(this).find('input[name=especificaciones]').val();
                                    id_reg_esp = $(this).find('input[name=id_reg_esp]').val();
                                    
                                    if(eliminar_tmp != null && id_reg_tmp != null && subproceso_id != null && especificaciones != null){
                                        //                  1               2                   3           
                                        cadena_pos += eliminar_tmp+"___"+id_reg_tmp+"___"+inv_prod_id+"___"+
                                            //      4                   5 
                                            subproceso_id+"___"+especificaciones+"___"+id_reg_esp+"$$$$";
                                    }
                                });
                                
                                $especificaicones_lista.val(cadena_pos);
                                
                                if (r) $submit_actualizar.parents("FORM").submit();
                            });
                            
                        }else{
                            jAlert("Es necesario agregar productos.", 'Atencion!');
                        }
                        // Always return false here since we don't know what jConfirm is going to do
                        return false;
                    //});
                    
               }else{
                  jAlert($valida_result, 'Atencion!');
               }
            });
            
            $cancelar_cencela_especificacaiones.click(function(event){
                event.preventDefault();
                
                $cadena_especificaciones = $cadeana_retorno;
                
                var remove = function() {$(this).remove();};
                $('#forma-especificaciones-overlay').fadeOut(remove);
            });
        }
        
        
        $calcula_cantidad_por_porducto = function(cantidad , total){
            cantidad_retorno = 0;
            
            cantidad_retorno = parseFloat(cantidad).toFixed(4);
            return parseFloat(cantidad_retorno).toFixed(4);
        }
        
        //agrega productos a el grid de ORDEN DE PRODUCCION en estatus produccion
        //$add_grid_componente_orden(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],""       ,""    ,""          , 0);
        $add_grid_componente_orden_en_produccion = function(id_reg,producto_id,sku,descripcion,cantidad, proceso_flujo_id, subproceso,pro_subprocesos_id ,lote,especificaciones, id_reg_esp, unidad, unidad_id, densidad){
                
                var $tabla_productos_orden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                var trCount = $("tr", $tabla_productos_orden).size();
                trCount++;
                
                trr = '<tr >';
                    trr += '<td width="61" class="grid1" align="center">';
                        trr += '<a href="#ver_detalle" id="ver_detalle'+trCount+'">Detalle</a>';
                        trr += '<input type="hidden" id="estatus_detalle" name="estatus_detalle" value="0">';
                        trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                        trr += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                        trr += '<input type="hidden" id="id_reg_esp" name="id_reg_esp" value="'+id_reg_esp+'">';
                        trr += '<input type="hidden" id="inv_prod_id" name="inv_prod_id" value="'+producto_id +'">';
                        trr += '<input type="hidden" id="posicion" name="posicion" value="'+trCount +'">';
                    trr += '</td>';
                    trr += '<td width="80" class="grid1" align="center">';
                        trr += '<input type="text" name="sku'+trCount+'" value="'+sku+'"  class="borde_oculto" readOnly="true" style="width:78px;">';
                    trr += '</td>';
                    trr += '<td width="446" class="grid1"><input type="text" name="descripcion'+trCount+'" value="'+descripcion+'"  class="borde_oculto" readOnly="true" style="width:208px;"></td>';
                    
                    
                    trr += '<td width="100" class="grid1">';
                        trr += '<span class="subproceso'+trCount+'"></span>';
                        trr += '<input type="text" name="subproceso" value="'+subproceso+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                        trr += '<input type="hidden" name="subproceso_id" value="'+pro_subprocesos_id+'" >';
                    trr += '</td>';
                    
                    trr += '<td width="100" class="grid1">';
                        trr += '<input type="text" name="lote" id="lote'+trCount+'" value="'+lote+'"  style="width:70px;" readOnly="true">';
                    trr += '</td>';
                    trr += '<td width="100" class="grid1">';
                        trr += '<input type="hidden" name="especificaciones" id="especificaciones'+trCount+'" value="'+especificaciones+'"  style="width:70px;" readOnly="true">';
                        trr += '<a href="#ver_especificaciones" id="ver_especificaciones'+trCount+'" title="Ver las especificaici&oacute;n">Res. Analisis</a>&nbsp;&nbsp;&nbsp;';
                        trr += '<a href="#add_especificaciones" id="add_especificaciones'+trCount+'" title="Agregar nueva especificaici&oacute;n">+</a>';
                    trr += '</td>';
                    
                    trr += '<td width="80" class="grid1"><input type="text" id="cantidad'+trCount+'" name="cantidad" value="'+cantidad+'"  style="width:70px;" ></td>';
                    trr += '<td width="80" class="grid1">';
                    trr += '<select id="unidad_default'+trCount+'" name="unidad_default" >';
                    unidad = unidad.toUpperCase();
                    if(/^KILO*|KILOGRAMO*$/.test(unidad)){
                        trr += '<option value="'+unidad_id+'" name="unidad_id" >'+unidad+'</option>';
                        trr += '<option value="0">LITRO</option>';
                        //, unidad_id, densidad
                    }else{
                        trr += '<option value="'+unidad_id+'">'+unidad+'</option>';
                        trr += '<option value="0">KILO</option>';
                    }
                    trr += '</select>';
                    trr += '<input type="hidden" name="densidad" value="'+densidad+'" >';
                    trr += '<input type="hidden" name="unidad_id" value="'+unidad_id+'" >';
                    trr += '</td>';
                trr += '</tr>';
                
                $tabla_productos_orden.append(trr);
                
                
                $aplicar_evento_keypress($tabla_productos_orden.find('#cantidad'+ trCount));
                //AL CAMBIAR UNA CANTIDAD EN UN SUBPROCESO, LO CAMBIA EN TODOS LOS DEMAS SUBPROCESOS DEL PRODUCTO
                $tabla_productos_orden.find('#cantidad'+ trCount).blur(function() {
                    cantidad_tr = $(this).val();
                    inv_prod_id_tr = $(this).parent().parent().find('input[name=inv_prod_id]').val();
                    if( ($(this).val() != ' ') && ($(this).val() != '') && ($(this).val() != null ) ){
                        $tabla_productos_orden.find('tr').each(function(){
                            inv_prod_id_tmp = $(this).find('input[name=inv_prod_id]').val();
                            if(inv_prod_id_tr == inv_prod_id_tmp){
                                $(this).find('input[name=cantidad]').val(cantidad_tr);
                            }
                        });
                    }
                });
                
                
                //se pone todo en kilos, que es lo que debe de ser por defecto
                $tmp_parent = $tabla_productos_orden.find('#unidad_default'+ trCount).parent().parent();
                //$tmp_parent = $(this).parent().parent();
                densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                text_selected = $tmp_parent.find('select option:selected').text();
                cantidad_default = $tmp_parent.find('input[name=cantidad]');
                $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'inicio');
                $event_changue_input_cantidad(cantidad_default);
                
                //al cambiar de unidad de medida, realiza los calculos de acuerdo a la densidad
                $tabla_productos_orden.find('#unidad_default'+ trCount).change(function() {
                    $tmp_parent = $(this).parent().parent();
                    densidad_tmp = $tmp_parent.find('input[name=densidad]').val();
                    text_selected = $tmp_parent.find('select option:selected').text();
                    cantidad_default = $tmp_parent.find('input[name=cantidad]');
                    $event_changue_umedida(cantidad_default, text_selected, densidad_tmp, 'grid');
                });
                
                
                
                $tabla_productos_orden.find('#ver_especificaciones'+ trCount).bind('click',function(event){
                    event.preventDefault();
                    
                    $tr_parent = $(this).parent().parent();
                    $id_producto = $tr_parent.find('input[name=inv_prod_id]');
                    $subproceso_id = $tr_parent.find('input[name=subproceso_id]');
                    $subproceso = $tr_parent.find('input[name=subproceso]');
                    $especificaciones = $tr_parent.find('input[name=especificaciones]');
                    $id_reg_tmp = $tr_parent.find('input[name=id_reg]');
                    $id_reg_esp = $tr_parent.find('input[name=id_reg_esp]');
                    accion = "edit";
                    //id_reg_esp
                    //id_reg
                    
                    $plugin_especificaciones($id_reg_tmp.val(), $subproceso_id.val(),$id_producto.val(), "s", $especificaciones.val(), accion, $tr_parent, $subproceso, $id_reg_esp.val());
                });
                
                
                $tabla_productos_orden.find('#add_especificaciones'+ trCount).bind('click',function(event){
                    event.preventDefault();
                    
                    $tr_parent = $(this).parent().parent();
                    $id_producto = $tr_parent.find('input[name=inv_prod_id]');
                    $subproceso_id = $tr_parent.find('input[name=subproceso_id]');
                    $subproceso = $tr_parent.find('input[name=subproceso]');
                    $especificaciones = $tr_parent.find('input[name=especificaciones]');
                    $id_reg_tmp = $tr_parent.find('input[name=id_reg]');
                    $id_reg_esp = $tr_parent.find('input[name=id_reg_esp]');
                    
                    accion = "new";
                    
                    $plugin_especificaciones($id_reg_tmp.val(), $subproceso_id.val(),$id_producto.val(), "s", $especificaciones.val(), accion, $tr_parent, $subproceso, "0");
                });
                
                
                /*Para que muestre el subgrid de el detalle de la formula, para este producto*/
                $tabla_productos_orden.find('#ver_detalle'+ trCount).bind('click',function(event){
                    
                    $grid_parent = $(this).parent().parent().parent();
                    $tr_parent = $(this).parent().parent();
                    $id_producto = $tr_parent.find('input[name=inv_prod_id]');
                    $estatus_detalle = $tr_parent.find('input[name=estatus_detalle]');
                    $posicion = $tr_parent.find('input[name=posicion]');
                    $cantidad = $tr_parent.find('input[name=cantidad]');
                    $subproceso_id  = $tr_parent.find('input[name=subproceso_id]');
                    
                    $id_reg_parent  = $tr_parent.find('input[name=id_reg]');
                    $id_reg_esp = $tr_parent.find('input[name=id_reg_esp]');
                    
                    
                    if($estatus_detalle.val() == "0"){
                        $estatus_detalle.val("1");
                        var $id_orden = $('#forma-proordenproduccion-window').find('input[name=id_orden]');
                        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/detalle_elementos_prod_formula.json';
                        $arreglo = {
                                        'id_orden':$id_orden.val(),
                                        'id_producto':$id_producto.val(),
                                        'id_subproceso':$subproceso_id.val(),
                                        'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                    }
                        
                        
                        $.post(input_json,$arreglo,function(producto){
                            html_tabla = '<tr>';
                                html_tabla += '<td colspan="8" width="1000">';
                                    //html_tabla += '<form id="guarda_lotes" action="guarda_lotes.json" method="POST" >';
                                        html_tabla += '<table class="detalle_por_prod" border="1">';
                                            html_tabla += '<header>';
                                                html_tabla += '<td class="grid" width="70px">&nbsp;#</td>';
                                                html_tabla += '<td class="grid" width="80px">&nbsp;Codigo</td>';
                                                html_tabla += '<td class="grid" width="200px">&nbsp;Descripci&oacute;n</td>';
                                                html_tabla += '<td class="grid" width="80px">&nbsp;Cantidad</td>';
                                                html_tabla += '<td class="grid" width="90px">&nbsp;Adicional</td>';
                                                html_tabla += '<td class="grid" width="90px">&nbsp;Uso Real</td>';
                                                html_tabla += '<td class="grid" width="130px" >&nbsp;Lote</td>';
                                                html_tabla += '<td class="grid" width="50px" >&nbsp;Exist</td>';
                                                html_tabla += '<td class="grid" width="100px" >&nbsp;Sucursal</td>';
                                                html_tabla += '<td class="grid" width="110px" >&nbsp;Almacen</td>';
                                            html_tabla += '</header>';
                                        html_tabla += '</table>';
                                        html_tabla += '<div style="overflow:scroll; overflow-x: hidden; overflow-y: auto;  width:1000; height: 200px; border:1px solid #C1DAD7 !important;">';
                                            html_tabla += '<table class="detalle_por_prod" >';
                                                html_tabla += '<tbody style="background-color: #FFFFFF;" valign="top" id="detalle_por_prod'+$id_producto.val()+$posicion.val()+'">';
                                                html_tabla += '</tbody>';
                                            html_tabla += '</table>';
                                        html_tabla += '</div>';
                                    //html_tabla += '</form>';
                                html_tabla += '</td>';
                            html_tabla += '</tr>';
                            
                            $tr_parent.after(html_tabla);
                            
                            if(producto['productos'] != null){
                                $id_tabla = '#detalle_por_prod'+$id_producto.val()+$posicion.val();
                                $.each(producto['productos'],function(entryIndex,prod){
                                    //alert(prod['cantidad']+'    '+ prod['sku']);
                                    
                                    if(prod['cantidad_adicional'] == "" || prod['cantidad_adicional'] == "0.0" || prod['cantidad_adicional'] == null){
                                        prod['cantidad_adicional'] = "0";
                                    }
                                    
                                    if(prod['id_reg_det'] == "" || prod['id_reg_det'] == null){
                                        prod['id_reg_det'] = "0";
                                    }
                                    if(prod['num_lote'] == "" || prod['num_lote'] == null){
                                        prod['num_lote'] = " ";
                                    }
                                    //alert(prod['cantidad_adicional']);
                                    prod['cantidad'] = $calcula_cantidad_por_porducto(prod['cantidad'] , $cantidad.val());
                                    //                        function(id_reg, $id_prod, $id_prod_detalle,            $sku,         $descripcion,       $cantidad,          $con_lote,                  clase_tmp,  grid,       cantidad_adicional,             posicion,       subproceso_id, id_reg_parent)
                                    $add_producto_eleemnto_detalle(prod['id'],$id_producto.val(), prod['inv_prod_id'], prod['sku'], prod['descripcion'], prod['cantidad'], prod['requiere_numero_lote'], $id_tabla, $grid_parent, prod['cantidad_adicional'], $posicion.val(), $subproceso_id.val(), $id_reg_parent.val(),prod['num_lote'], prod['id_reg_det'], prod['inv_osal_id'] , "", prod['inv_alm_id'], prod['gral_suc_id'], 0, prod['cantidad_usada']);
                                });
                                
                                
                                $tmp_tr = $grid_parent.find($id_tabla);
                                var trCount = $("tr", $tmp_tr).size();
                                
                                tmp_html = '<tr>';
                                    tmp_html += '<td class="grid1" align="center" colspan="4">&nbsp;';
                                        tmp_html += '<INPUT TYPE="button" id="agregar_producto_por_recuperacion'+trCount+'" name="guardar_producto_por_recuperacion" value="Agregar Producto" style="height:20px;" class="confirmar">&nbsp;&nbsp;&nbsp;&nbsp;';
                                    tmp_html += '</td>';
                                    tmp_html += '<td width="100" class="grid1" >';
                                        tmp_html += '<INPUT TYPE="button" id="guardar_detalle_prod'+trCount+'" name="guardar_detalle_prod" value="Guardar" style="height:20px;" class="confirmar">&nbsp;&nbsp;&nbsp;&nbsp;';
                                    tmp_html += '</td>';
                                    tmp_html += '<td width="100" class="grid1" >';
                                        tmp_html += '<INPUT TYPE="button" id="guardar_uso_real_op'+trCount+'" name="guardar_uso_real_op" value="Cant. Usada" style="height:20px;" class="confirmar">&nbsp;&nbsp;&nbsp;&nbsp;';
                                    tmp_html += '</td>';
                                    
                                tmp_html += '</tr>';
                                
                                $tmp_tr.append(tmp_html);
                                
                                
                                
                                //agregar un producto al grid, para recuperacion de la formula
                                $tmp_tr.find('#agregar_producto_por_recuperacion'+trCount).click(function(event){
                                    $busca_productos(4, $(this));
                                });
                                
                                
                                
                                
                                
                                
                                //codigo, para guardar las cantidades reales utilizadas por cada lote
                                $tmp_tr.find('#guardar_uso_real_op'+trCount).click(function(event){
                                    event.preventDefault();
                                    
                                    $guardar_detalle_prod_tmp = $(this).parent().parent().parent().find('input[name=guardar_uso_real_op]');
                                    
                                    if( $guardar_detalle_prod_tmp != null ){
                                        
                                        var $id_orden = $('#forma-proordenproduccion-window').find('input[name=id_orden]');
                                        var $tipoorden = $('#forma-proordenproduccion-window').find('input[name=tipoorden]');
                                        var $fecha_elavorar = $('#forma-proordenproduccion-window').find('input[name=fecha_elavorar]');
                                        
                                        var $command_selected = $('#forma-proordenproduccion-window').find('input[name=command_selected]');
                                        var $proceso_flujo_id = $('#forma-proordenproduccion-window').find('input[name=proceso_flujo_id]');
                                        var $observaciones = $('#forma-proordenproduccion-window').find('textarea[name=observaciones]');
                                        
                                        $command_selected.val(9);
                                        
                                        //alert($(this).parent().parent().parent().parent().parent().html());
                                        
                                        table_producto = $(this).parent().parent().parent().parent().parent();
                                        
                                        $id_prod = table_producto.find('input[name=inv_prod_id_elemento]');
                                        $id_prod_detalle = table_producto.find('input[name=id_prod_detalle]');
                                        $posicion_detalle = table_producto.find('input[name=posicion]');
                                        $subproceso_id = table_producto.find('input[name=subproceso_id]');
                                        
                                        //subproceso_id
                                        $id_tabla = '#detalle_por_prod'+$id_prod.val()+$posicion_detalle.val();
                                        
                                        table_producto_detalle = table_producto.find($id_tabla);
                                        //detalle_por_prod3602
                                        
                                        
                                        lotes_completos = 1;
                                        cadena_pos = "";
                                        table_producto_detalle.find('tr').each(function(){
                                            
                                            eliminar_tmp = $(this).find('input[name=eliminar]').val();
                                            id_reg_tmp = $(this).find('input[name=id_reg]').val();//id de el registro ern la tabla
                                            id_reg_parent = $(this).find('input[name=id_reg_parent]').val();
                                            inv_prod_id_elemento_tmp = $(this).find('input[name=inv_prod_id_elemento]').val();
                                            id_prod_detalle_tmp = $(this).find('input[name=id_prod_detalle]').val();//id de la materia prima
                                            cantidad_elemento_tmp = $(this).find('input[name=cantidad_elemento]').val();//cantidad solicitar
                                            cantidad_adicional_tmp = $(this).find('input[name=cantidad_adicional]').val();//cantidad adicional
                                            lote_tmp = $(this).find('input[name=lote]').val();
                                            id_reg_det = $(this).find('input[name=id_reg_det]').val();
                                            inv_osal_id = $(this).find('input[name=inv_osal_id]').val();
                                            almacen_id = $(this).find('select[name=almacen]').val();
                                            sucursal_id = $(this).find('select[name=sucursal]').val();
                                            agregado = $(this).find('input[name=agregado]').val();
                                            cantidad_real_tmp = $(this).find('input[name=cantidad_real]').val();
                                            //1___0___1483___12___d3da21c7-c4ba-49be-a241-9529336c5e75&&&1___0___158___0___2471c2a0-f253-4504-9bca-b7f843a5c72d&&&1___0___148___0___f84b5f6c-6cd4-45cb-a404-b532527f60e2&&&1___0___147___0___ &&&1___0___191___0___ &&&1___0___151___0___ &&&1___0___1493___0___ &&&1___0___1397___0___ &&&1___0___1390___0___ &&&1___0___374___0___ &&&1___0___378___0___ &&&1___0___1180___0___ &&&1___0___149___0___ &&&1___0___150___0___ &&&1___0___91___0___ &&&1___0___160___0___ &&&1___0___127___0___ &&&1___0___1483___0___ 
                                            /*
                                            alert(
												"cantidad_elemento_tmp: "+cantidad_elemento_tmp+"\n"+
												"cantidad_adicional_tmp: "+cantidad_adicional_tmp+"\n"+
												"lote_tmp: "+lote_tmp+"\n"+
												"cantidad_real_tmp: "+cantidad_real_tmp+"\n"
                                            
                                            );
                                            */
                                            if(eliminar_tmp != null && lote_tmp != null){
                                                if(lote_tmp == "" || lote_tmp == " " ){
                                                    lotes_completos = 0;
                                                }
                                                
                                                cadena_pos += eliminar_tmp+"___"+id_reg_tmp+"___"+id_prod_detalle_tmp+"___"+ 
                                                    cantidad_elemento_tmp+"___"+cantidad_adicional_tmp+"___"+lote_tmp+"___"+//inv_osal_id
                                                    inv_prod_id_elemento_tmp+"___"+id_reg_parent+"___"+$subproceso_id.val()+
                                                        "___"+id_reg_det+"___"+inv_osal_id+"___"+almacen_id+"___"+sucursal_id+"___"+
                                                        agregado+"___"+cantidad_real_tmp+"$$$$";
                                            }
                                        });
                                        
                                        cadena_pos = cadena_pos.substring(0, (cadena_pos.length - 4 ));
                                        jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
                                            // If they confirmed, manually trigger a form submission
                                            if (r){
                                                var $id_formula = $('#forma-proordenproduccion-window').find('input[name=id_formula]');
                                                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/guarda_lotes.json';
                                                $arreglo = {
                                                        'id':$id_orden.val(),
                                                        'id_prod':$id_prod.val(),
                                                        'tipoorden':8,
                                                        'id_subproceso':$subproceso_id.val(),
                                                        'cadena':cadena_pos,
                                                        'command_selected':$command_selected.val() ,
                                                        'observaciones': $observaciones.val(),
                                                        'fecha_elavorar':$fecha_elavorar.val(),
                                                        'id_formula':$id_formula.val(),
                                                        'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                                    }
                                                    
                                                    $.post(input_json,$arreglo,function(data){
                                                        if(data['success'] == "true"){
                                                            var remove = function() {$(this).remove();};
                                                            $('#forma-proordenproduccion-overlay').fadeOut(remove);
                                                            jAlert("Lotes registrados", 'Atencion!');
                                                        }else{
                                                            
                                                            var $tabla_resultados = $('#forma-proordenproduccion-window').find('#tabla_resultado');
                                                            
                                                            //grids detalle pedido
                                                            var $tabla_productos_preorden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados').find('.detalle_por_prod');
                                                            
                                                            // Desaparece todas las interrogaciones si es que existen
                                                            $('#forma-proordenproduccion-window').find('div.interrogacion').css({'display':'none'});
                                                            
                                                            /*
                                                            $tabla_productos_preorden.find('input[name=persona]').css({'background' : '#ffffff'});
                                                            $tabla_productos_preorden.find('input[name=equipo]').css({'background' : '#ffffff'});
                                                            $tabla_productos_preorden.find('input[name=eq_adicional]').css({'background' : '#ffffff'});
                                                            $tabla_productos_preorden.find('input[name=cantidad]').css({'background' : '#ffffff'});
                                                            */
                                                            
                                                            $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'578px'});//con errores
                                                            $('#forma-proordenproduccion-window').find('#div_warning_grid').css({'display':'none'});
                                                            $('#forma-proordenproduccion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
                                                            
                                                            //alert($tabla_productos_preorden.html());
                                                            
                                                            var valor = data['success'].split('___');
                                                            //muestra las interrogaciones
                                                            for (var element in valor){
                                                                    tmp = data['success'].split('___')[element];
                                                                    longitud = tmp.split(':');
                                                                    
                                                                    if( longitud.length > 1 ){
                                                                            $('#forma-proordenproduccion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                                                            .parent()
                                                                            .css({'display':'block'})
                                                                            .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
                                                                            
                                                                            //alert(tmp.split(':')[0]);
                                                                            
                                                                            if(parseInt($("tr", $tabla_productos_preorden).size())>0){
                                                                                    for (var i=1;i<=parseInt($("tr", $tabla_productos_preorden).size());i++){
                                                                                        //alert(tmp.split(':')[0]);
                                                                                        $(this).find('input[name=id_reg]').val();//id de el registro ern la tabla
                                                                                            
                                                                                            if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='lote'+i) ){
                                                                                                
                                                                                                $('#forma-proordenproduccion-window').find('#div_warning_grid').css({'display':'block'});
                                                                                                //$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
                                                                                                
                                                                                                if(tmp.split(':')[0].substring(0, 9) == 'cantidad'+i){
                                                                                                    $tabla_productos_preorden.find('#cantidad_real'+i).css({'background' : '#d41000'});
                                                                                                }
                                                                                                
                                                                                                //alert(tmp.split(':')[0].substring(0, 5)+ "    " + i+ "    "+$tabla_productos_preorden.find('#lote'+i).val());
                                                                                                if(tmp.split(':')[0].substring(0, 5) == 'lote'+i){
                                                                                                    $tabla_productos_preorden.find('#lote'+i).css({'background' : '#d41000'});
                                                                                                }
                                                                                                
                                                                                                var tr_warning = '<tr>';
                                                                                                        tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
                                                                                                        tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=sku]').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
                                                                                                        tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=descripcoin]').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
                                                                                                        tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:485px; color:red"></td>';
                                                                                                    tr_warning += '</tr>';
                                                                                                    
                                                                                                $('#forma-proordenproduccion-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
                                                                                                
                                                                                            }
                                                                                    }
                                                                            }
                                                                    }
                                                            }
                                                            
                                                            $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
                                                            $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
                                                            
                                                        }
                                                    });
                                                    
                                                    
                                            }else{
                                                return false;
                                            }
                                        });
                                    }
                                });
                                
                                
                                //codigo, para guardar los cambios en los productos del detalle de la formula, asi como los lotes
                                $tmp_tr.find('#guardar_detalle_prod'+trCount).click(function(event){
                                    event.preventDefault();
                                    
                                    $guardar_detalle_prod = $(this).parent().parent().parent().find('input[name=guardar_detalle_prod]');
                                    
                                    if( $guardar_detalle_prod != null ){
                                        var $id_orden = $('#forma-proordenproduccion-window').find('input[name=id_orden]');
                                        var $tipoorden = $('#forma-proordenproduccion-window').find('input[name=tipoorden]');
                                        var $fecha_elavorar = $('#forma-proordenproduccion-window').find('input[name=fecha_elavorar]');
                                        
                                        var $command_selected = $('#forma-proordenproduccion-window').find('input[name=command_selected]');
                                        var $proceso_flujo_id = $('#forma-proordenproduccion-window').find('input[name=proceso_flujo_id]');
                                        var $observaciones = $('#forma-proordenproduccion-window').find('textarea[name=observaciones]');
                                        
                                        $command_selected.val(3);
                                        
                                        //alert($(this).parent().parent().parent().parent().parent().html());
                                        
                                        table_producto = $(this).parent().parent().parent().parent().parent();
                                        
                                        $id_prod = table_producto.find('input[name=inv_prod_id_elemento]');
                                        $id_prod_detalle = table_producto.find('input[name=id_prod_detalle]');
                                        $posicion_detalle = table_producto.find('input[name=posicion]');
                                        $subproceso_id = table_producto.find('input[name=subproceso_id]');
                                        
                                        //subproceso_id
                                        $id_tabla = '#detalle_por_prod'+$id_prod.val()+$posicion_detalle.val();
                                        
                                        table_producto_detalle = table_producto.find($id_tabla);
                                        //detalle_por_prod3602
                                        
                                        
                                        lotes_completos = 1;
                                        cadena_pos = "";
                                        table_producto_detalle.find('tr').each(function(){
                                            
                                            eliminar_tmp = $(this).find('input[name=eliminar]').val();
                                            id_reg_tmp = $(this).find('input[name=id_reg]').val();//id de el registro ern la tabla
                                            id_reg_parent = $(this).find('input[name=id_reg_parent]').val();
                                            inv_prod_id_elemento_tmp = $(this).find('input[name=inv_prod_id_elemento]').val();
                                            id_prod_detalle_tmp = $(this).find('input[name=id_prod_detalle]').val();//id de la materia prima
                                            cantidad_elemento_tmp = $(this).find('input[name=cantidad_elemento]').val();//cantidad solicitar
                                            cantidad_adicional_tmp = $(this).find('input[name=cantidad_adicional]').val();//cantidad adicional
                                            lote_tmp = $(this).find('input[name=lote]').val();
                                            id_reg_det = $(this).find('input[name=id_reg_det]').val();
                                            inv_osal_id = $(this).find('input[name=inv_osal_id]').val();
                                            almacen_id = $(this).find('select[name=almacen]').val();
                                            sucursal_id = $(this).find('select[name=sucursal]').val();
                                            agregado = $(this).find('input[name=agregado]').val();
                                            cantidad_real_tmp = $(this).find('input[name=cantidad_real]').val();
                                            //1___0___1483___12___d3da21c7-c4ba-49be-a241-9529336c5e75&&&1___0___158___0___2471c2a0-f253-4504-9bca-b7f843a5c72d&&&1___0___148___0___f84b5f6c-6cd4-45cb-a404-b532527f60e2&&&1___0___147___0___ &&&1___0___191___0___ &&&1___0___151___0___ &&&1___0___1493___0___ &&&1___0___1397___0___ &&&1___0___1390___0___ &&&1___0___374___0___ &&&1___0___378___0___ &&&1___0___1180___0___ &&&1___0___149___0___ &&&1___0___150___0___ &&&1___0___91___0___ &&&1___0___160___0___ &&&1___0___127___0___ &&&1___0___1483___0___ 
                                            
                                            if(eliminar_tmp != null && lote_tmp != null){
                                                if(lote_tmp == "" || lote_tmp == " " ){
                                                    lotes_completos = 0;
                                                }
                                                
                                                cadena_pos += eliminar_tmp+"___"+id_reg_tmp+"___"+id_prod_detalle_tmp+"___"+cantidad_elemento_tmp+"___"+cantidad_adicional_tmp+"___"+lote_tmp+"___"+inv_prod_id_elemento_tmp+"___"+id_reg_parent+"___"+$subproceso_id.val()+"___"+id_reg_det+"___"+inv_osal_id+"___"+almacen_id+"___"+sucursal_id+"___"+agregado+"___"+cantidad_real_tmp+"$$$$";
                                            }
                                            
                                        });
                                        
                                        cadena_pos = cadena_pos.substring(0, (cadena_pos.length - 4 ));
                                        
                                        //if(lotes_completos == 1){
                                            
                                            jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
                                                // If they confirmed, manually trigger a form submission
                                                if (r){
                                                    var $id_formula = $('#forma-proordenproduccion-window').find('input[name=id_formula]');
                                                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/guarda_lotes.json';
                                                    $arreglo = {
                                                                'id':$id_orden.val(),
                                                                'id_prod':$id_prod.val(),
                                                                'tipoorden':3,
                                                                'id_subproceso':$subproceso_id.val(),
                                                                'cadena':cadena_pos,
                                                                'command_selected':$command_selected.val() ,
                                                                'observaciones': $observaciones.val(),
                                                                'fecha_elavorar':$fecha_elavorar.val(),
                                                                'id_formula':$id_formula.val(),
                                                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                                                }
                                                       
                                                    $.post(input_json,$arreglo,function(data){
                                                        if(data['success'] == "true"){
                                                            var remove = function() {$(this).remove();};
                                                            $('#forma-proordenproduccion-overlay').fadeOut(remove);
                                                            jAlert("Lotes registrados", 'Atencion!');
                                                        }else{
                                                            var $tabla_resultados = $('#forma-proordenproduccion-window').find('#tabla_resultado');
                                                                    
                                                                    //grids detalle pedido
                                                                    var $tabla_productos_preorden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
                                                                    // Desaparece todas las interrogaciones si es que existen
                                                                    $('#forma-proordenproduccion-window').find('div.interrogacion').css({'display':'none'});
                                                                    /*
                                                                    $tabla_productos_preorden.find('input[name=persona]').css({'background' : '#ffffff'});
                                                                    $tabla_productos_preorden.find('input[name=equipo]').css({'background' : '#ffffff'});
                                                                    $tabla_productos_preorden.find('input[name=eq_adicional]').css({'background' : '#ffffff'});
                                                                    $tabla_productos_preorden.find('input[name=cantidad]').css({'background' : '#ffffff'});
                                                                    */
                                                                    
                                                                    $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'578px'});//con errores
                                                                    $('#forma-proordenproduccion-window').find('#div_warning_grid').css({'display':'none'});
                                                                    $('#forma-proordenproduccion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
                                                                    
                                                                    
                                                                    var valor = data['success'].split('___');
                                                                    //muestra las interrogaciones
                                                                    for (var element in valor){
                                                                            tmp = data['success'].split('___')[element];
                                                                            longitud = tmp.split(':');
                                                                            
                                                                            if( longitud.length > 1 ){
                                                                                    $('#forma-proordenproduccion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                                                                    .parent()
                                                                                    .css({'display':'block'})
                                                                                    .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

                                                                                    //alert(tmp.split(':')[0]);
                                                                                    
                                                                                    if(parseInt($("tr", $tabla_productos_preorden).size())>0){
                                                                                            for (var i=1;i<=parseInt($("tr", $tabla_productos_preorden).size());i++){
                                                                                                    if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='apoerario'+i) || (tmp.split(':')[0]=='equipo'+i) || 
                                                                                                        (tmp.split(':')[0]=='equipo_adicional'+i) || (tmp.split(':')[0]=='almacen'+i)){
                                                                                                            
                                                                                                            $('#forma-proordenproduccion-window').find('#div_warning_grid').css({'display':'block'});
                                                                                                            //$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
                                                                                                            
                                                                                                            if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
                                                                                                                $tabla_productos_preorden.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                                                            }
                                                                                                            
                                                                                                            if(tmp.split(':')[0].substring(0, 5) == 'apoerario'){
                                                                                                                $tabla_productos_preorden.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                                                            }
                                                                                                            
                                                                                                            if(tmp.split(':')[0].substring(0, 9) == 'equipo'){
                                                                                                                $tabla_productos_preorden.find('input[name=equipo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                                                            }
                                                                                                            
                                                                                                            if(tmp.split(':')[0].substring(0, 9) == 'equipo_adicional'){
                                                                                                                $tabla_productos_preorden.find('input[name=equipo_adicional]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                                                            }
                                                                                                            if(tmp.split(':')[0].substring(0, 9) == 'almacen'){
                                                                                                                $tabla_productos_preorden.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                                                                                            }
                                                                                                            
                                                                                                            
                                                                                                            var tr_warning = '<tr>';
                                                                                                                            tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
                                                                                                                            tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=sku' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
                                                                                                                            tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=descripcion' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
                                                                                                                            tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:485px; color:red"></td>';
                                                                                                            tr_warning += '</tr>';
                                                                                                            
                                                                                                            $('#forma-proordenproduccion-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
                                                                                                    }

                                                                                            }
                                                                                    }
                                                                            }
                                                                    }
                                                                    $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
                                                                    $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({'background-color' : '#e7e8ea'});

                                                        }
                                                    });
                                                    
                                                    return true;
                                                }else{
                                                    return false;
                                                }
                                            });
                                            /*
                                         }else{
                                             jAlert("Ingrese todos los lotes", 'Atencion!');
                                         }
                                        */
                                        
                                    }
                                    //alert(alert($(this).parent().parent().parent().parent().parent().parent().parent().html()));
                                });
                                
                            }
                        },"json");
                        
                    }else{
                        
                        $id_tabla = '#detalle_por_prod'+$id_producto.val()+$posicion.val();
                        $table_pos_pros = $grid_parent.find($id_tabla).parent().parent().parent().parent();
                        if($estatus_detalle.val() == "1"){
                            $estatus_detalle.val("2");
                            $table_pos_pros.hide();
                        }else{
                            $estatus_detalle.val("1");
                            $table_pos_pros.show();
                        }
                    }
                    
                });
        }
        
        
                                //prod['id'],$id_producto.val(), prod['inv_prod_id'], prod['sku'], prod['cantidad'], p $id_tabla, $grid_parent, prod['cantidad_adicional'], $posicion.val(), $subproceso_id.val(), $id_reg_parent.val(),prod['num_lote'], prod['id_reg_det'] );
        //para ver la lista de productos de que esta formulado el producto
                                                 //prod['id'],
                                                 //$id_producto.val(), 
                                                 //prod['inv_prod_id'], prod['sku'], prod['descripcion'], prod['cantidad'], prod $id_tabla, $grid_parent, prod['cant $posicion$subproceso_id.$id_reg_parent.val(),prod['num_lote'], prod['id_reg_det'] 
        $add_producto_eleemnto_detalle = function(id_reg, $id_prod, $id_prod_detalle, $sku, $descripcion, $cantidad, $con_lote, clase_tmp, grid, cantidad_adicional, posicion, subproceso_id, id_reg_parent, num_lote, id_reg_det, inv_osal_id, tipo_agregado, id_almacen, id_sucursal, agregado, cantidad_usada){
            
            if(parseFloat(cantidad_usada) <= 0){
                cantidad_usada = parseFloat(parseFloat($cantidad) + parseFloat(cantidad_adicional)).toFixed(4);
            }
            
            $tmp_tr = grid.find(clase_tmp);
            var trCount = $("tr", $tmp_tr).size();
            trCount++;
            
            tmp_html = '<tr>';
                tmp_html += '<td width="70px" class="grid1" align="center" >';
                    
                    if(inv_osal_id == 0){
                        tmp_html += '<a href="#elimina_producto_componente" id="elimina_producto_componente'+trCount+'">';
                            tmp_html += "Eliminar";
                        tmp_html += '</a>';
                    }else{
                            tmp_html += "Eliminar";
                    }
                    
            
                tmp_html += '</td>';
                tmp_html += '<td width="80px" class="grid1" align="center" >';
                    tmp_html += '<input type="hidden" id="id_reg_parent" name="id_reg_parent" value="'+id_reg_parent+'">';
                    tmp_html += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                    tmp_html += '<input type="hidden" id="inv_osal_id" name="inv_osal_id" value="'+inv_osal_id+'">';
                    
                    tmp_html += '<input type="hidden" id="agregado" name="agregado" value="'+agregado+'">';//campo para indicar que el producto, venia de la formula original o equivalente u no un agregado.(1->original, 0->agregado)
                    tmp_html += '<input type="hidden" id="id_reg_det" name="id_reg_det" value="'+id_reg_det+'">';
                    tmp_html += '<input type="hidden" id="delete" name="eliminar" value="1">';
                    tmp_html += '<input type="hidden" id="inv_prod_id_elemento" name="inv_prod_id_elemento" value="'+$id_prod +'">';
                    tmp_html += '<input type="hidden" id="subproceso_id" name="subproceso_id" value="'+subproceso_id +'">';
                    tmp_html += '<input type="hidden" id="id_prod_detalle" name="id_prod_detalle" value="'+$id_prod_detalle +'">';
                    tmp_html += '<input type="hidden" id="posicion" name="posicion" value="'+posicion +'">';
                    tmp_html += '<input type="hidden" name="sku" value="'+$sku+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                    tmp_html += '<a href="#buscar_contratipo" id="buscar_contratipo'+trCount+'">'+$sku+'</a>';
                    
                tmp_html += '</td>';
                
                tmp_html += '<td width="200px" class="grid1">';
                    tmp_html += '<input type="text" name="descripcoin" value="'+$descripcion+'"  class="borde_oculto" style="width:150px;" readOnly="true" >';
                tmp_html += '</td>';
                tmp_html += '<td width="80" class="grid1">';
                    if(inv_osal_id == 0){
                        tmp_html += '<input type="text" name="cantidad_elemento" id="cantidad_elemento'+trCount+'" value="'+$cantidad+'" style="width:70px;" >';
                    }else{
                        tmp_html += '<input type="text" name="cantidad_elemento" id="cantidad_elemento'+trCount+'" value="'+$cantidad+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                    }
                tmp_html += '</td>';
                tmp_html += '<td width="90px" class="grid1">';
                    tmp_html += '<input type="text" id="cantidad_adicional'+trCount+'" name="cantidad_adicional" value="'+cantidad_adicional+'"  style="width:70px;" readOnly="true">';
                    //tmp_html += '<input type="text" id="cantidad_adicional'+trCount+'" name="cantidad_adicional" value="'+cantidad_adicional+'"  style="width:70px;">';
                tmp_html += '</td>';
                tmp_html += '<td width="90px" class="grid1">';
                    tmp_html += '<input type="text" id="cantidad_real'+trCount+'" name="cantidad_real" value="'+cantidad_usada+'"  style="width:70px;" >';
                tmp_html += '</td>';
                
                tmp_html += '<td width="130px" class="grid1" >';
                
                if(num_lote == " " || num_lote == ""){
                    if(inv_osal_id != 0){
                        tmp_html += '<input type="text" name="lote" id="lote'+trCount+'" value="'+num_lote+'"  style="width:100px;">';
                        tmp_html += '<a href="#add_lote" id="add_lote'+trCount+'">+</a>';
                    }else{
                        tmp_html += '<input type="text" name="lote" id="lote'+trCount+'" value="'+num_lote+'"  style="width:100px;">';
                    }
                }else{
                    tmp_html += '<input type="text" name="lote" id="lote'+trCount+'" value="'+num_lote+'"  style="width:100px;" readOnly="true">';
                    tmp_html += '<a href="#add_lote" id="add_lote'+trCount+'">+</a>';
                }
                
                tmp_html += '</td>';
                tmp_html += '<td width="50px" class="grid1"><input type="text" name="existencia" id="existencia'+trCount+'" value="0"  style="width:50px;" readOnly="true"></td>';
                tmp_html += '<td width="100px" class="grid1">';
                tmp_html += '<select name="sucursal" id="sucursal'+trCount+'" style="width:85px;">';
                //para mostrar las sucursales en los componentes de la orden de produccion
                selececcionado = 0;
                $.each(array_sucursales, function(entryIndex,item){
                    if(id_sucursal == item['id']){
                        tmp_html += '<option value="' + item['id'] + '" selected="yes" >' + item['sucursal'] + '</option>';
                        selececcionado = 1;
                    }else{
                        tmp_html += '<option value="' + item['id'] + '"  >' + item['sucursal'] + '</option>';
                    }
                });
                if(selececcionado==0){
                    tmp_html += '<option value="0" selected="yes" >[- Selecciona una sucursal -]</option>';
                }
                tmp_html += '</select>';
                
                tmp_html += '</td>';
                //para mostrar los almacenes en los componentes de la orden de produccion
                tmp_html += '<td width="110px" class="grid1">';
                tmp_html += '<select name="almacen" id="almacen'+trCount+'" style="width:85px;">';
                tmp_html += '<option value="0"  >[- Selecciona un almacen -]</option>';
                selececcionado = 0;
                $.each(array_almacenes, function(entryIndex,item){
                    if(id_almacen == item['id']){
                        selececcionado = 1;
                        tmp_html += '<option value="' + item['id'] + '" selected="yes" >' + item['titulo'] + '</option>';
                    }else{
                        tmp_html += '<option value="' + item['id'] + '"  >' + item['titulo'] + '</option>';
                    }
                });
                if(selececcionado==0){
                    tmp_html += '<option value="0" selected="yes" >[- Selecciona una sucursal -]</option>';
                }
                tmp_html += '</select>';
                
                tmp_html += '</td>';
                
            tmp_html += '</tr>';
            //alert(tmp_html);
            if(tipo_agregado == "recuperado"){
                tr = $tmp_tr.find('tr').eq(trCount - 2);
                tr.before(tmp_html);
            }else{
                $tmp_tr.append(tmp_html);
            }
            
            //alert(id_reg_det);
            
            $aplicar_evento_focus_input_lote($tmp_tr.find('#lote'+ trCount ));
            $aplicar_evento_blur_input_lote($tmp_tr.find('#lote'+ trCount ));
            $aplicar_evento_keypress_input_lote($tmp_tr.find('#lote'+ trCount ));
            $aplicar_evento_click_input_lote($tmp_tr.find('#lote'+ trCount ));
            $aplicar_evento_keypress($tmp_tr.find('#cantidad_adicional'+ trCount));
            $aplicar_evento_keypress($tmp_tr.find('#cantidad_real'+ trCount));
            
            
			//Validar al perder el enfoque
			$tmp_tr.find('#cantidad_real'+ trCount).blur(function(){
				$inputCantReal = $(this);
				if ($inputCantReal.val().trim() == ''){
						jAlert('El campo Cantidad Real no debe quedar vac&iacute;o.', 'Atencion!', function(r) { 
							$inputCantReal.focus();
						});
				}else{
					
					if (parseFloat($inputCantReal.val()) >= 0.0001){
						//Buscar cuantos puntos tiene inputCantReal
						var coincidencias = $inputCantReal.val().match(/\./g);
						var numPuntos = coincidencias ? coincidencias.length : 0;
						if(parseInt(numPuntos)>1){
							jAlert('El valor ingresado para Cantidad Real es incorrecto, tiene mas de un punto('+$inputCantReal.val()+').', 'Atencion!', function(r) { 
								$inputCantReal.focus();
							});
						}
					}else{
						jAlert('La Cantidad Real debe ser mayor o igual a 0.0001', 'Atencion!', function(r) { 
							$inputCantReal.focus();
						});
					}
				}
			});
            
            
            
            //$tmp_tr.find('#lote'+trCount).click(function(event){
            //  
            //});
            
            $tmp_tr.find('#almacen'+trCount).change(function(event) {
                event.preventDefault();
                $almacen_seleccionado = $(this).val();
                $this_tr = $(this).parent().parent();
                $sku_tmp = $this_tr.find('input[name=sku]').val();
                $sucursal_tmp = $this_tr.find('select[name=sucursal]').val();
                $id_prod_detalle_tmp = $this_tr.find('input[name=id_prod_detalle]').val();
                var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
                
                if($sucursal_tmp != 0){
                    //obtiene los tipos de almacen para el buscador
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_existenciapor_producto.json';
                    $arreglo = {'almacen_id':$almacen_seleccionado,'codigo':$sku_tmp,
                        'id_producto':$id_prod_detalle_tmp, 'iu': iu};
                    $.post(input_json,$arreglo,function(entry){
                        
                        $this_tr.find('input[name=existencia]').val(entry['Existencia']);
                        
                        cantidad_existencia = parseFloat(entry['Existencia']);
                        
                        //alert(entry['Existencia']);
                        $tmp_tr.find('tr').each(function(){
                            
                            $sku_this_tr = $(this).find('input[name=sku]').val();
                            $inv_prod_det_tr = $(this).find('input[name=id_prod_detalle]').val();
                            $existencia_tr = $(this).find('input[name=existencia]').val();
                            $almacen_tr = $(this).find('select[name=almacen]').val();
                            $cantidad_elemnto_tr = isNaN(parseFloat($(this).find('input[name=cantidad_elemento]').val())) ? 0 : parseFloat($(this).find('input[name=cantidad_elemento]').val());
                            $cantidad_adicional_tr = isNaN(parseFloat($(this).find('input[name=cantidad_adicional]').val())) ? 0 : parseFloat($(this).find('input[name=cantidad_adicional]').val());
                            if($id_prod_detalle_tmp == $inv_prod_det_tr && $almacen_seleccionado == $almacen_tr ){
                                //alert("cantidad_elemnto_tr:"+$(this).find('input[name=cantidad_elemnto]').val() +"   cantidad_adicional_tr:"+ $cantidad_adicional_tr);
                                cantidad_existencia -= ($cantidad_elemnto_tr + $cantidad_adicional_tr );
                            }
                            
                        });
                        
                        $cantidad_this_tr = isNaN(parseFloat($this_tr.find('input[name=cantidad_elemento]').val())) ? 0 : parseFloat($this_tr.find('input[name=cantidad_elemento]').val());
                        $adicional_this_tr = isNaN(parseFloat($this_tr.find('input[name=cantidad_adicional]').val())) ? 0 : parseFloat($this_tr.find('input[name=cantidad_adicional]').val());
                        cantidad_existencia = (cantidad_existencia + ($cantidad_this_tr + $adicional_this_tr ) );
                        $this_tr.find('input[name=existencia]').val(cantidad_existencia);

                        if(cantidad_existencia < ($cantidad_this_tr + $adicional_this_tr )){
                            $this_tr.css({'background-color' : '#F64848'});
                        }else{
                            $this_tr.parent().css({'background-color' : '#FFFFFF'});
                        }
                    });//termina llamada json
                }else{
                    jAlert("Seleccione una sucursal", 'Atencion!');
                }
                
            });
            
            $tmp_tr.find('#cantidad_elemento'+trCount).change(function() {
				//alert("hola");
                $valor = isNaN(parseFloat($(this).val())) ? 0 : parseFloat($(this).val());
                $this_tr_tpm = $(this).parent().parent();
                $cantidad_this_tr = isNaN(parseFloat($this_tr_tpm.find('input[name=existencia]').val())) ? 0 : parseFloat($this_tr_tpm.find('input[name=existencia]').val());
                if($valor > $cantidad_this_tr){
                    jAlert("La cantidad debe de ser menor o igual a la existencia", 'Atencion!');
                }
            });
            
            $tmp_tr.find('#cantidad_adicional'+trCount).change(function() {
                
                $valor = isNaN(parseFloat($(this).val())) ? 0 : parseFloat($(this).val());
                $this_tr_tpm = $(this).parent().parent();
                $adicional_this_tr = isNaN(parseFloat($this_tr_tpm.find('input[name=existencia]').val())) ? 0 : parseFloat($this_tr_tpm.find('input[name=existencia]').val());
                if($valor > $cantidad_this_tr){
                    jAlert("La cantidad debe de ser menor o igual a la existencia", 'Atencion!');
                }
            });
            
            $tmp_tr.find('#elimina_producto_componente'+trCount).click(function(event){
                event.preventDefault();
                $(this).parent().parent().find('input[name=eliminar]').val("0");
                $(this).parent().parent().hide();
            });
            
            $tmp_tr.find('#add_lote'+trCount).click(function(event){
                event.preventDefault();
                
                $tmp_this_tr = $(this).parent().parent();
                
                //$id_producto.val()+$posicion.val()
                
                id_reg_parent = $tmp_this_tr.find('input[name=id_reg_parent]').val();
                id_reg = $tmp_this_tr.find('input[name=id_reg]').val();
                
                inv_prod_id_elemento = $tmp_this_tr.find('input[name=inv_prod_id_elemento]').val();
                posicion = $tmp_this_tr.find('input[name=posicion]').val();
                id_prod_detalle = $tmp_this_tr.find('input[name=id_prod_detalle]').val();
                cantidad_elemento = $tmp_this_tr.find('input[name=cantidad_elemento]').val();
                cantidad_elemento = 0;
                cantidad_adicional = $tmp_this_tr.find('input[name=cantidad_adicional]').val();
                id_subproceso = $tmp_this_tr.find('input[name=subproceso_id]').val();
                agregado = $tmp_this_tr.find('input[name=agregado]').val();
                
                $tmp_this_tbody = $(this).parent().parent().parent().parent().find('#detalle_por_prod'+inv_prod_id_elemento+posicion);
                
                
                var trCount = $("tr", $tmp_this_tbody).size();
                trCount++;
                
                
                tmp_html = '<tr>';
                tmp_html += '<td width="70px" class="grid1" align="center" >';
                    tmp_html += '<a href="#elimina_producto_componente" id="elimina_producto_componente'+trCount+'">';
                        tmp_html += "Eliminar";
                    tmp_html += '</a>';
                tmp_html += '</td>';
                
                tmp_html += '<td class="grid1" align="center" colspan="2" width="280px">';
                    tmp_html += '<input type="hidden" id="id_reg_parent" name="id_reg_parent" value="'+id_reg_parent+'">';
                    tmp_html += '<input type="hidden" id="id_reg" name="id_reg" value="'+id_reg+'">';
                    tmp_html += '<input type="hidden" id="inv_osal_id" name="inv_osal_id" value="0">';
                    
                    tmp_html += '<input type="hidden" id="agregado" name="agregado" value="'+agregado+'">';//campo para indicar que el producto, venia de la formula original o equivalente u no un agregado.(0->original, 1->agregado)
                    tmp_html += '<input type="hidden" id="id_reg_det" name="id_reg_det" value="0">';
                    tmp_html += '<input type="hidden" id="delete" name="eliminar" value="1">';
                    tmp_html += '<input type="hidden" id="subproceso_id" name="subproceso_id" value="'+id_subproceso +'">';
                    tmp_html += '<input type="hidden" id="inv_prod_id_elemento" name="inv_prod_id_elemento" value="'+$id_prod +'">';
                    tmp_html += '<input type="hidden" id="id_prod_detalle" name="id_prod_detalle" value="'+$id_prod_detalle +'">';
                tmp_html += '</td>';
                
                tmp_html += '<td width="80px" class="grid1">';
                    tmp_html += '<input type="text" name="cantidad_elemento" value="'+cantidad_elemento+'"  class="borde_oculto" style="width:70px;" readOnly="true" >';
                tmp_html += '</td>';
                tmp_html += '<td width="90px" class="grid1">';
                    tmp_html += '<input type="text" name="cantidad_adicional" value="'+cantidad_adicional+'"  style="width:70px;" >';
                tmp_html += '</td>';
                tmp_html += '<td width="90px" class="grid1">';
                    tmp_html += '<input type="text" id="cantidad_real'+trCount+'" name="cantidad_real" value="'+$cantidad+'"  style="width:70px;" readOnly="true">';
                tmp_html += '</td>';
                
                tmp_html += '<td width="130px" class="grid1">';
                    tmp_html += '<input type="text" name="lote" id="lote'+trCount+'" value=" "  style="width:100px;">';
                    tmp_html += '<a href="#remove_lote'+trCount+'" id="remove_lote'+trCount+'">-</a>';
                tmp_html += '</td>';
                
                
                
                tmp_html += '<td width="50px" class="grid1"><input type="text" name="existencia" id="existencia'+trCount+'" value="0"  style="width:50px;" readOnly="true"></td>';
                tmp_html += '<td width="100px" class="grid1">';
                
                tmp_html += '<select name="sucursal" id="sucursal'+trCount+'" style="width:85px;">';
                tmp_html += '<option value="0"  >[- Selecciona una sucursal -]</option>';
                
                $.each(array_sucursales, function(entryIndex,item){
                    tmp_html += '<option value="' + item['id'] + '"  >' + item['sucursal'] + '</option>';
                });
                
                tmp_html += '</select>';
                tmp_html += '</td>';
                
                //para mostrar los almacenes en los componentes de la orden de produccion
                tmp_html += '<td width="110px" class="grid1">';
                selececcionado = 0;
                tmp_html += '<select name="almacen" id="almacen'+trCount+'" style="width:85px;">';
                tmp_html += '<option value="0"  >[- Selecciona un almacen -]</option>';
                $.each(array_almacenes, function(entryIndex,item){
                    tmp_html += '<option value="' + item['id'] + '"  >' + item['titulo'] + '</option>';
                });
                tmp_html += '</select>';
                tmp_html += '</td>';
                
                tmp_html += '</tr>';
                
                $tmp_this_tr.after(tmp_html);
                
                $aplicar_evento_focus_input_lote($tmp_this_tr.parent().find('#lote'+ trCount ));
                $aplicar_evento_blur_input_lote($tmp_this_tr.parent().find('#lote'+ trCount ));
                $aplicar_evento_keypress_input_lote($tmp_this_tr.parent().find('#lote'+ trCount ));
                $aplicar_evento_click_input_lote($tmp_this_tr.parent().find('#lote'+ trCount ));
                
                $tmp_this_tr.find('#almacen'+trCount).change(function(event) {
                    event.preventDefault();
                    
                    /*
                    //obtiene los tipos de almacen para el buscador
                    var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_existenciapor_producto.json';
                    $arreglo = {'almacen_id':valor_pais,'codigo':valor_pais,'id_producto':valor_pais, 'iu': valor_entidad};
                    $.post(input_json,$arreglo,function(entry){
                        //Alimentando los campos select tipos de almacen del buscador
                        $busqueda_select_tipo.children().remove();
                        var tipo_hmtl = '<option value="0" selected="yes">[--Seleccionar Tipo --]</option>';
                        $.each(entry['Tipos'],function(entryIndex,reg){
                            tipo_hmtl += '<option value="' + reg['id'] + '"  >' + reg['titulo'] + '</option>';
                        });
                        $busqueda_select_tipo.append(tipo_hmtl);
                    });//termina llamada json
                    alert($(this).val());
                    */
                   
                });
                
                $tmp_this_tr.parent().find('#remove_lote'+trCount).click(function(event){
                    event.preventDefault();
                    $(this).parent().parent().find('input[name=eliminar]').val("0");
                    $(this).parent().parent().hide();
                });
                
                $tmp_this_tr.parent().find('#elimina_producto_componente'+trCount).click(function(event){
                    event.preventDefault();
                    $(this).parent().parent().find('input[name=eliminar]').val("0");
                    $(this).parent().parent().hide();
                });
                
            });
            
            
            /*
             $tr_parent.after(html_tabla);
            //$table_parent
            $tabla_productos_orden.find('#remov_especificacion'+trCount).click(function(event){
                event.preventDefault();
                
                $(this).parent().parent().find('input[name=eliminar]').val("0");
                $(this).parent().parent().find('input[name=especificaciones1]').val("");
                
                $(this).parent().parent().hide();
            });
            
             **/
            //alert($tmp_tr.html());
            
        }
        
        //buscador de de Datos del Lote
	$obtiene_datos_lote = function($tr_padre){
            var numero_lote = $tr_padre.find('input[name=lote]').val();
            var id_producto = $tr_padre.find('input[name=id_prod_detalle]').val();
            var encontrado=0;
            $tabla_padre = $tr_padre.parent();
            
            //buscar el numero de lote en la tabla
            $tabla_padre.find('input[name=lote_int]').each(function (index){
                if($(this).val() == numero_lote ){
                    encontrado++;
                }
            });
            
            //si el numero de lote solo esta una vez es valido, dos veces ya no es valido
            if(parseInt(encontrado)<=1){
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosLote.json';
                $arreglo = {'no_lote':numero_lote,
                            'id_producto':id_producto,
                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                            };
                            
                $.post(input_json,$arreglo,function(entry){
                    //verifica si el arreglo  retorno datos
                    if (entry['Lote'].length > 0){
                        
                        //crea el tr con los datos del producto seleccionado
                        $.each(entry['Lote'],function(entryIndex,lote){
                            //$tr_padre.find('input[name=lote_int]').val(lote['']);
                            /*
                            $tr_padre.find('input[name=exis_lote]').val(lote['exis_lote']);
                            $tr_padre.find('input[name=pedimento]').val(lote['pedimento']);
                            $tr_padre.find('input[name=caducidad]').val(lote['caducidad']);
                            $tr_padre.find('input[name=cant_sur]').val(lote['exis_lote']);
                            */
                        });//termina llamada json
                        
                    }else{
                        jAlert("El n&uacute;mero de Lote no existe para &eacute;ste producto.", 'Atencion!');
                        $tr_padre.find('input[name=lote]').val(" ");
                        $tr_padre.find('input[name=lote]').select();
                    }
                });
            }else{
                jAlert("El n&uacute;mero de Lote  [ "+numero_lote+" ]  ya se encuentra en la lista.", 'Atencion!');
            }
            
	}//termina buscador de datos del Lote
        
        $opbtiene_datos_producto_por_sku = function($sku, $id_formula, $version){
            
            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_busca_sku_prod.json';
            $arreglo = {'sku':$sku,
                            'id_formula':$id_formula,
                            'version':$version,
                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                        }
                        
            $.post(input_json,$arreglo,function(prod){
                var res=0;
                if(prod['Sku'][0] != null && prod['SubProcesos'][0] != null){
                    
                    unidad = prod['Sku'][0]['unidad'];
                    unidad_id = prod['Sku'][0]['unidad_id'];
                    densidad = prod['Sku'][0]['densidad'];
                    
                    formulacion_id = prod['SubProcesos'][0]['pro_estruc_id'];
                    
                    $('#forma-proordenproduccion-window').find('input[name=id_formula]').val(formulacion_id);
                    //                                id, prod_id,              aku,                descripcion,                , persona, maquina,eq_adicional, cantidad , proceso_flujo_id
                    //agrega productos a el grid de formulaciones
                    $add_grid_componente_orden(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],""       ,""    ,""          , 0, prod['SubProcesos'], 1, unidad, unidad_id, densidad);
                }else{
                    jAlert("El producto que intenta agregar no existe o no tiene formula, pruebe ingresando otro.\nHaga clic en Buscar.",'! Atencion');
                }
            },"json");
        }
        
        
        
        //buscador de productos
	$busca_productos = function(tipo_busqueda, tr_click ){
            
            sku_buscar = "";
                
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -200});
		
		var $tabla_resultados = $('#forma-buscaproducto-window').find('#tabla_resultado');
		
		var $campo_sku = $('#forma-buscaproducto-window').find('input[name=campo_sku]');
		var $select_tipo_producto = $('#forma-buscaproducto-window').find('select[name=tipo_producto]');
		var $campo_descripcion = $('#forma-buscaproducto-window').find('input[name=campo_descripcion]');
		
		var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('#busca_producto_modalbox');
		var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('#cencela');
		
		//funcionalidad botones
		$buscar_plugin_producto.mouseover(function(){
                    $(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_producto.mouseout(function(){
                    $(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_plugin_busca_producto.mouseover(function(){
                    $(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_producto.mouseout(function(){
                    $(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		//buscar todos los tipos de productos
		var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProductoTipos.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_tipos,$arreglo,function(data){
                    
                    //Llena el select tipos de productos en el buscador
                    $select_tipo_producto.children().remove();
                    //<option value="0" selected="yes">[--Seleccionar Tipo--]</option>
                    var prod_tipos_html = '';
                    
                    $.each(data['prodTipos'],function(entryIndex,pt){
                        
                        
                        //para productos para tipo de orden stock
                        if(tipo_busqueda == 2){
                            if(pt['id'] == 2 || pt['id'] == 1 ){
                                prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                            }
                        }
                        
                        //para productos para tipo de orden laboratorio
                        if(tipo_busqueda == 3){
                            if(pt['id'] == 8 ){
                                if(pt['id'] == 8){
                                    prod_tipos_html += '<option value="' + pt['id'] + '" selected="yes">' + pt['titulo'] + '</option>';
                                }else{
                                    prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                                }
                            }
                        }
                        
                        //para productos de las formulas en recuperacion
                        if(tipo_busqueda == 4){
                            if(pt['id'] == 2 || pt['id'] == 1  || pt['id'] == 7 || pt['id'] == 8 ){
                                prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                            }
                        }
                        
                    });
                    $select_tipo_producto.append(prod_tipos_html);
		
		$campo_sku.val(sku_buscar);
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			event.preventDefault();
                        
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos.json';
			$arreglo = {'sku':$campo_sku.val(),
                                        'tipo':$select_tipo_producto.val(),
                                        'descripcion':$campo_descripcion.val(),
                                        'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                    }
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
                            
				$.each(entry['productos'],function(entryIndex,producto){
                                    trr = '<tr>';
                                        trr += '<td width="120">';
                                            trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
                                            trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
                                        trr += '</td>';
                                        trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
                                        trr += '<td width="90"><span class="unidad_prod_buscador">'+producto['unidad']+'</span></td>';
                                        trr += '<td width="90"><span class="tipo_prod_buscador">'+producto['tipo']+'</span></td>';
                                    trr += '</tr>';
                                    $tabla_resultados.append(trr);
				});
                                
				$colorea_tr_grid($tabla_resultados);
				
                                
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
                                    var id_prod=$(this).find('#id_prod_buscador').val();
                                    var codigo=$(this).find('span.sku_prod_buscador').html();
                                    var descripcion=$(this).find('span.titulo_prod_buscador').html();
                                    var producto=$(this).find('span.tipo_prod_buscador').html();
                                    var unidad=$(this).find('span.unidad_prod_buscador').html();
                                    
                                    //buscador para los pedidos de tipo, stock y laboratorio
                                    if(tipo_busqueda == 2 || tipo_busqueda == 3){
                                        //asignar a los campos correspondientes el sku y y descripcion
                                        $('#forma-proordenproduccion-window').find('input[name=id_producto_tmp]').val(id_prod);
                                        $('#forma-proordenproduccion-window').find('input[name=sku_tmp]').val(codigo);
                                        $('#forma-proordenproduccion-window').find('input[name=descripcion_tmp]').val(descripcion);
                                    }
                                    
                                    //buscador para poder agregar productos para recuperacion
                                    if( tipo_busqueda == 4){
                                        
                                        tr = tr_click.parent().parent().prev();
                                        var trCount = $("tr", tr.parent().parent()).size();
                                        
                                        id_reg_parent = tr.find('#id_reg_parent');//id de el registro padre
                                        inv_prod_id_elemento = tr.find('#inv_prod_id_elemento');//id de el producto de que se produce en ese subproceso
                                        //id_prod id de el producto que se agrega
                                        //$id_tabla---Esto queda pendiente, no se que pèdo, no me acuerdo
                                        //$grid_parent---checar que tiene grid
                                        inv_osal_id = 0;//id de la orden de salida
                                        subproceso_id = tr.find('#subproceso_id');//id de el subproceso
                                        id_reg_det = 0;//id d eele registro de el subproceso
                                        
                                        $grid_parent = tr.parent().parent();
                                        $posicion = $grid_parent.find('input[name=posicion]');
                                        $id_tabla = '#detalle_por_prod'+inv_prod_id_elemento.val()+$posicion.val();
                                        
                                        //alert($grid_parent.parent().parent().parent().parent().html());
                                        
                                        $add_producto_eleemnto_detalle(0,inv_prod_id_elemento.val(), id_prod, codigo, descripcion, 
                                        0, "", $id_tabla, $grid_parent, 0, trCount, subproceso_id.val(), id_reg_parent.val(),"", 
                                        id_reg_det, inv_osal_id, "recuperado", 0, 0, 1, 0);
                                        
                                    }
                                    
                                    //elimina la ventana de busqueda
                                    var remove = function() {$(this).remove();};
                                    $('#forma-buscaproducto-overlay').fadeOut(remove);
                                    //asignar el enfoque al campo sku del producto
                                });
                            });
			});
                });
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_sku.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}
                
		$cancelar_plugin_busca_producto.click(function(event){
                    //event.preventDefault();
                    var remove = function() {$(this).remove();};
                    $('#forma-buscaproducto-overlay').fadeOut(remove);
		});
                
	}//termina buscador de productos
	
        
        $agrega_esp_en_blanco_grid = function(){
            
            var $tabla_productos_preorden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
            $tabla_productos_preorden.find('tr').each(function(){
                if($(this).find('#id_reg').val() == ""){
                    $(this).find('#id_reg').val(" ");
                }
                if($(this).find('#inv_prod_id').val() == ""){
                    $(this).find('#id_reg').val(" ");
                }
                
                
                
                if($(this).find('input[name=subproceso_id]').val() == ""){
                    $(this).find('input[name=subproceso_id]').val(" ");
                }
                
                if($(this).find('input[name=pro_subproceso_prod_id]').val() == ""){
                    $(this).find('input[name=pro_subproceso_prod_id]').val(" ");
                }
                
                if($(this).find('input[name=persona]').val() == ""){
                    $(this).find('input[name=persona]').val(" ");
                }
                
                if($(this).find('input[name=equipo]').val() == ""){
                    $(this).find('input[name=equipo]').val(" ");
                }
                
                if($(this).find('input[name=eq_adicional]').val() == ""){
                    $(this).find('input[name=eq_adicional]').val(" ");
                }
                
                if($(this).find('input[name=cantidad]').val() == ""){
                    $(this).find('input[name=cantidad]').val(" ");
                }
                
                
            });
        }
        
        $repaint_header_minigrid = function($estatus){
            var $tabla_productos_header = $('#forma-proordenproduccion-window').find('.subprocesos_seleccionados');
            
            if($estatus == "3"){
                $tabla_productos_header.children().remove();
                
                html_header = '';
                html_header += '<td class="grid" id="td_eliminar" width="61"><div class="delete">&nbsp;#</div></td>';
                html_header += '<td class="grid" width="80">&nbsp;Codigo<td>';
                html_header += '<td class="grid" width="446">&nbsp;Descripci&oacute;n</td>';
                html_header += '<td class="grid" width="100">&nbsp;Subproceso</td>';
                html_header += '<td class="grid" width="100">&nbsp;Lote</td>';
                html_header += '<td class="grid" width="100">&nbsp;Especificaciones</td>';
                html_header += '<td class="grid" width="80">&nbsp;Cantidad</td>';
                html_header += '<td class="grid" width="80">&nbsp;U. Medida</td>';
                
                $tabla_productos_header.append(html_header);
                
            }
        }
        
        
        $ocullta_de_acuerdo_a_el_tipo_y_estatus = function($tipo, estatus, cantidad_salida){
            //alert($tipo +"    "+ estatus);
            //tipos de preorden
            var $preorden_tipo_pedido = $('#forma-proordenproduccion-window').find('.tipo_pedido');
            var tipo_stock_laboratorio = $('#forma-proordenproduccion-window').find('.tipo_stock_laboratorio'); 

            //href para buscar producto
            var $buscar_producto = $('#forma-proordenproduccion-window').find('a[href*=busca_producto]');
            var $agregar_producto = $('#forma-proordenproduccion-window').find('a[href*=agregar_producto]');
            //href para agregar producto al grid

            var $cancelar_proceso = $('#forma-proordenproduccion-window').find('#cancela_entrada');

            var $cerrar_plugin = $('#forma-proordenproduccion-window').find('#close');
            var $cancelar_plugin = $('#forma-proordenproduccion-window').find('#boton_cancelar');
            var $submit_actualizar = $('#forma-proordenproduccion-window').find('#submit');

            var $botones_confirmacion = $('#forma-proordenproduccion-window').find('.botones_confirmacion');
            var $confirmar_programacion = $('#forma-proordenproduccion-window').find('#confirmar_programacion');
            var $confirmar_enviar_produccion = $('#forma-proordenproduccion-window').find('#confirmar_enviar_produccion');
            var $confirmar_terminada = $('#forma-proordenproduccion-window').find('#confirmar_terminada');
            var $cancelar_orden = $('#forma-proordenproduccion-window').find('#cancelar_orden');
            var $pdf_orden = $('#forma-proordenproduccion-window').find('#pdf_orden');
            var $pdf_requisicion = $('#forma-proordenproduccion-window').find('#pdf_requisicion');
            var $costo_ultimo = $('#forma-proordenproduccion-window').find('input[name=costo_ultimo]');
            var $costo_ultimo_text = $('#forma-proordenproduccion-window').find('.costo_ultimo');
            
            
            $botones_confirmacion.show();
            $confirmar_programacion.hide();
            $confirmar_enviar_produccion.hide();
            $confirmar_terminada.hide();
            $cancelar_orden.hide();
            $pdf_orden.hide();
            $pdf_requisicion.hide();
            
            //Si la orden es de tipo laboratorio, muestra los campos vendedor y cliente, que son para una orden de tipo laboratorio, de lo contrario los oculta.
            if($tipo == 3){
                $('#forma-proordenproduccion-window').find('.tipo_laboratorio').show();
            }else{
                $('#forma-proordenproduccion-window').find('.tipo_laboratorio').hide();
            }
            
            $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'475px'});
            if(estatus == "1"){
                $submit_actualizar.show();
                $confirmar_programacion.show();
                $confirmar_enviar_produccion.hide();
                $confirmar_terminada.hide();
                $cancelar_orden.show();
                $pdf_requisicion.hide();
                $costo_ultimo.hide();
                $costo_ultimo_text.hide();
                
                if($tipo == 1){
                    $preorden_tipo_pedido.hide();
                    tipo_stock_laboratorio.hide();
                    $buscar_producto.hide();
                    $cancelar_proceso.hide();
                    
                    $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'475px'});
                }
                
                if($tipo == 2){
                    $preorden_tipo_pedido.hide();
                    tipo_stock_laboratorio.show();
                    $buscar_producto.show();
                    $agregar_producto.show();
                    $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'505px'});
                }
                
                if($tipo == 3){
                    $preorden_tipo_pedido.hide();
                    tipo_stock_laboratorio.show();
                    $buscar_producto.show();
                    $agregar_producto.show();
                    $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'490px'});
                }
            }
            
            if(estatus == "2"){
                
                $costo_ultimo.hide();
                $costo_ultimo_text.hide();
                $submit_actualizar.hide();
                $confirmar_programacion.show();
                $confirmar_enviar_produccion.show();
                $confirmar_terminada.hide();
                $cancelar_orden.show();
                $pdf_orden.hide();
                
                $preorden_tipo_pedido.hide();
                tipo_stock_laboratorio.hide();
                $buscar_producto.hide();
                $cancelar_proceso.hide();
                $pdf_requisicion.hide();
            }
            
            if(estatus == "3"){
                
                $costo_ultimo.hide();
                $costo_ultimo_text.hide();
                $submit_actualizar.hide();
                $confirmar_programacion.hide();
                $confirmar_enviar_produccion.hide();
                $confirmar_terminada.show();
                $pdf_orden.show();
                $pdf_requisicion.show();
                $cancelar_proceso.hide();
                
                $preorden_tipo_pedido.hide();
                tipo_stock_laboratorio.hide();
                $buscar_producto.hide();
                
                if(cantidad_salida == '0'){
                    $cancelar_orden.show();
                }else{
                    $cancelar_orden.hide();
                }
                
                $repaint_header_minigrid(estatus);
            }
            
            if(estatus == "4"){
                $costo_ultimo.show();
                $costo_ultimo_text.show();
                $submit_actualizar.hide();
                $confirmar_programacion.hide();
                $confirmar_enviar_produccion.hide();
                $confirmar_terminada.hide();
                $cancelar_orden.hide();
                $pdf_orden.show();
                $pdf_requisicion.hide();
                
                $preorden_tipo_pedido.hide();
                tipo_stock_laboratorio.hide();
                $buscar_producto.hide();
                $cancelar_proceso.hide();
            }
            
            if(estatus == "5"){
                $pdf_orden.hide();
                $pdf_requisicion.hide();
                $submit_actualizar.hide();
                $confirmar_programacion.hide();
                $confirmar_enviar_produccion.hide();
                $confirmar_terminada.hide();
                $cancelar_orden.hide();
                $pdf_orden.hide();
                $pdf_requisicion.hide();
                
                $preorden_tipo_pedido.hide();
                tipo_stock_laboratorio.hide();
                $buscar_producto.hide();
                $cancelar_proceso.hide();
            }
            
        }
        
        $seleccionar_version_de_formula = function(sku, tipo_orden){
            if(tipo_orden == 3){
                //limpiar_campos_grids();
		$(this).modalPanel_Formulasendesarrollo();
		var $dialogoc =  $('#forma-formulasendesarrollo-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_formulasendesarrollo').find('table.formaBusqueda_formulasendesarrollo').clone());
		
		$('#forma-formulasendesarrollo-window').css({"margin-left": -200, 	"margin-top": -200});
		
		var $tabla_resultados = $('#forma-formulasendesarrollo-window').find('#tabla_resultado');
		
		var $cancelar_plugin_formulasendesarrollo = $('#forma-formulasendesarrollo-window').find('#cencela');
		
		$cancelar_plugin_formulasendesarrollo.mouseover(function(){
                    $(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_formulasendesarrollo.mouseout(function(){
                    $(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		//buscador de versiones de formulas
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_versiones_formulas_por_sku.json';
                $arreglo = {'sku':sku,
                                'tipo':tipo_orden,
                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                            }
                var trr = '';
                $tabla_resultados.children().remove();
                $.post(input_json,$arreglo,function(entry){
                    
                    $.each(entry['formulas'],function(entryIndex,formula){
                        trr = '<tr>';
                            trr += '<td width="100">';
                                trr += '<span class="sku_prod_buscador">'+formula['sku']+'</span>';
                                trr += '<input type="hidden" id="id_prod_buscador" value="'+formula['inv_prod_id']+'">';
                                trr += '<input type="hidden" id="id_formula_buscador" value="'+formula['id']+'">';
                            trr += '</td>';
                            trr += '<td width="330"><span class="titulo_prod_buscador">'+formula['descripcion']+'</span></td>';
                            trr += '<td width="100"><span class="version_form_buscador">'+formula['version']+'</span></td>';
                        trr += '</tr>';
                        $tabla_resultados.append(trr);
                    });
                    
                    $colorea_tr_grid($tabla_resultados);
                    
                    //seleccionar un producto del grid de resultados
                    $tabla_resultados.find('tr').click(function(){
                        var id_prod=$(this).find('#id_prod_buscador').val();
                        var id_formula=$(this).find('#id_formula_buscador').val();
                        var version=$(this).find('span.version_form_buscador').html();
                        var codigo=$(this).find('span.sku_prod_buscador').html();
                        var descripcion=$(this).find('span.titulo_prod_buscador').html();
                        
                        $('#forma-proordenproduccion-window').find('input[name=version_formula]').val(version);
                        $('#forma-proordenproduccion-window').find('input[name=id_formula]').val(id_formula);
                        
                        $opbtiene_datos_producto_por_sku(codigo,id_formula, version);
                        
                        //elimina la ventana de busqueda
                        var remove = function() {$(this).remove();};
                        $('#forma-formulasendesarrollo-overlay').fadeOut(remove);
                        //asignar el enfoque al campo sku del producto
                        
                    });
                });
                
		$cancelar_plugin_formulasendesarrollo.click(function(event){
                    //event.preventDefault();
                    var remove = function() {$(this).remove();};
                    $('#forma-formulasendesarrollo-overlay').fadeOut(remove);
		});
            }
        }
        
	//nueva entrada
	$new_orden.click(function(event){
            
            event.preventDefault();
            var id_to_show = 0;
            
            $(this).modalPanel_ProOrdenProduccion();
            
            var form_to_show = 'formaProOrdenProduccion00';
            $('#' + form_to_show).each (function(){this.reset();});
            var $forma_selected = $('#' + form_to_show).clone();
            $forma_selected.attr({id : form_to_show + id_to_show});
            
            $('#forma-proordenproduccion-window').css({"margin-left": -415, "margin-top": -230});
            
            $forma_selected.prependTo('#forma-proordenproduccion-window');
            $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
            
            $tabs_li_funxionalidad();
            
            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_datos_orden.json';
            $arreglo = {'id_orden':id_to_show,
                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                        };
                        
            var $command_selected = $('#forma-proordenproduccion-window').find('input[name=command_selected]');
            var $id_orden = $('#forma-proordenproduccion-window').find('input[name=id_orden]');
            var $proceso_flujo_id = $('#forma-proordenproduccion-window').find('input[name=proceso_flujo_id]');
            var $select_tipoorden = $('#forma-proordenproduccion-window').find('select[name=tipoorden]');
            var $fecha_elavorar = $('#forma-proordenproduccion-window').find('input[name=fecha_elavorar]');
            var $observaciones = $('#forma-proordenproduccion-window').find('textarea[name=observaciones]');
            
            
            //
            var $titprod_tmp = $('#forma-proordenproduccion-window').find('input[name=titprod_tmp]');
            var $sku_tmp = $('#forma-proordenproduccion-window').find('input[name=sku_tmp]');
            var $version_formula = $('#forma-proordenproduccion-window').find('input[name=version_formula]');
            var $id_formula = $('#forma-proordenproduccion-window').find('input[name=id_formula]');
            var $id_producto_tmp = $('#forma-proordenproduccion-window').find('input[name=id_producto_tmp]');
            var $descripcion_tmp = $('#forma-proordenproduccion-window').find('input[name=descripcion_tmp]');
            
            //grids detalle pedido
            var $tabla_productos_preorden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
            
            //tipos de preorden
            var $preorden_tipo_pedido = $('#forma-proordenproduccion-window').find('.tipo_pedido');
            var tipo_stock_laboratorio = $('#forma-proordenproduccion-window').find('.tipo_stock_laboratorio'); 
            
            //href para buscar producto
            var $buscar_producto = $('#forma-proordenproduccion-window').find('a[href*=busca_producto]');
            var $agregar_producto = $('#forma-proordenproduccion-window').find('a[href*=agregar_producto]');
            //href para agregar producto al grid
            
            var $cancelar_proceso = $('#forma-proordenproduccion-window').find('#cancela_entrada');
            
            var $cerrar_plugin = $('#forma-proordenproduccion-window').find('#close');
            var $cancelar_plugin = $('#forma-proordenproduccion-window').find('#boton_cancelar');
            var $submit_actualizar = $('#forma-proordenproduccion-window').find('#submit');
            
            var $botones_confirmacion = $('#forma-proordenproduccion-window').find('.botones_confirmacion');
            var $confirmar_programacion = $('#forma-proordenproduccion-window').find('#confirmar_programacion');
            var $confirmar_enviar_produccion = $('#forma-proordenproduccion-window').find('#confirmar_enviar_produccion');
            var $confirmar_terminada = $('#forma-proordenproduccion-window').find('#confirmar_terminada');
            var $cancelar_orden = $('#forma-proordenproduccion-window').find('#cancelar_orden');
            
            var $pdf_orden = $('#forma-proordenproduccion-window').find('#pdf_orden');
            var $pdf_requisicion = $('#forma-proordenproduccion-window').find('#pdf_requisicion');
            
            //oculta los campos y textos para una orden de produccion que no sea de tipo laboratorio
            $('#forma-proordenproduccion-window').find('.tipo_laboratorio').hide();
            
            $command_selected.val("new");
            $id_orden.val(0);
                
                $botones_confirmacion.hide();
                $confirmar_programacion.hide();
                $confirmar_enviar_produccion.hide();
                $confirmar_terminada.hide();
                $cancelar_orden.hide();
                $pdf_orden.hide();
                $pdf_requisicion.hide();
                $proceso_flujo_id.val("1");
                //mostrarFecha() // por si se quiere agregar la fecha actual
                $add_calendar($fecha_elavorar, " ", ">=");
		
		var respuestaProcesada = function(data){
                    if ( data['success'] == "true" ){
                        jAlert("Los cambios se guardaron con exito", 'Atencion!');
                        var remove = function() {$(this).remove();};
                        $('#forma-proordenproduccion-overlay').fadeOut(remove);
                        $get_datos_grid();
                    }else{
                        
                        // Desaparece todas las interrogaciones si es que existen
                        $('#forma-proordenproduccion-window').find('div.interrogacion').css({'display':'none'});
                        $tabla_productos_preorden.find('input[name=persona]').css({'background' : '#ffffff'});
                        $tabla_productos_preorden.find('input[name=equipo]').css({'background' : '#ffffff'});
                        $tabla_productos_preorden.find('input[name=eq_adicional]').css({'background' : '#ffffff'});
                        $tabla_productos_preorden.find('input[name=cantidad]').css({'background' : '#ffffff'});
                        
                        $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'548px'});//con errores
                        $('#forma-proordenproduccion-window').find('#div_warning_grid').css({'display':'none'});
                        $('#forma-proordenproduccion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
                        
                        
                        var valor = data['success'].split('___');
                        //muestra las interrogaciones
                        for (var element in valor){
                            tmp = data['success'].split('___')[element];
                            longitud = tmp.split(':');
                            
                            if( longitud.length > 1 ){
                                
                                $('#forma-proordenproduccion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                .parent()
                                .css({'display':'block'})
                                .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
                                
                                //alert(tmp.split(':')[0]);
                                
                                if(parseInt($("tr", $tabla_productos_preorden).size())>0){
                                    for (var i=1;i<=parseInt($("tr", $tabla_productos_preorden).size());i++){
                                        if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='apoerario'+i) || (tmp.split(':')[0]=='equipo'+i) || (tmp.split(':')[0]=='equipo_adicional'+i)){
                                            
                                            $('#forma-proordenproduccion-window').find('#div_warning_grid').css({'display':'block'});
                                            //$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
                                            
                                            if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
                                                    $tabla_productos_preorden.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                            }
                                            
                                            if(tmp.split(':')[0].substring(0, 5) == 'apoerario'){
                                                    $tabla_productos_preorden.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                            }
                                            
                                            if(tmp.split(':')[0].substring(0, 9) == 'equipo'){
                                                    $tabla_productos_preorden.find('input[name=caducidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                            }
                                            
                                            if(tmp.split(':')[0].substring(0, 9) == 'equipo_adicional'){
                                                    $tabla_productos_preorden.find('input[name=caducidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
                                            }
                                            
                                            
                                            var tr_warning = '<tr>';
                                                tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
                                                tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=sku' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
                                                tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=descripcion' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
                                                tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:285px; color:red"></td>';
                                                tr_warning += '</tr>';
                                            
                                            $('#forma-proordenproduccion-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
                                        }
                                        
                                    }
                                }
                            }
                        }
                        $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
                        $('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({'background-color' : '#e7e8ea'});

                    }
                }
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
                
		//$.getJSON(json_string,function(entry){
                $.post(input_json,$arreglo,function(entry){
                    
                    //$id_formula.val(entry['Orden']['0']['pro_proceso_id']);
                    
                    array_instrumentos = entry['Instrumentos'];
                    
                    $select_tipoorden.children().remove();
                    var orden_tipos_html = '<option value="0" selected="yes">[-- --]</option>';
                    $.each(entry['ordenTipos'],function(entryIndex,pt){
                        pt['titulo']=pt['titulo'].toUpperCase();
                        if(!/^[PEDIDO]*$/.test(pt['titulo'])){
                            orden_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                        }
                    });
                    $select_tipoorden.append(orden_tipos_html);
                    
                    array_almacenes = entry['Almacenes'];
                    
                    $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'450px'});
                    $select_tipoorden.change(function(){
                        tipo_preorden = $select_tipoorden.val();
                        
                        if(tipo_preorden == 0){
                            $preorden_tipo_pedido.hide();
                            tipo_stock_laboratorio.hide();
                            $('#forma-proordenproduccion-window').find('.tipo_laboratorio').hide();
                            $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'450px'});
                        }
                        
                        if(tipo_preorden == 1){
                            $preorden_tipo_pedido.hide();
                            tipo_stock_laboratorio.hide();
                            $('#forma-proordenproduccion-window').find('.tipo_laboratorio').hide();
                            $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'565px'});
                        }
                        
                        if(tipo_preorden == 2){
                            $preorden_tipo_pedido.hide();
                            tipo_stock_laboratorio.show();
                            $('#forma-proordenproduccion-window').find('.tipo_laboratorio').hide();
                            $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'480px'});
                        }
                        
                        if(tipo_preorden == 3){
                            $preorden_tipo_pedido.hide();
                            tipo_stock_laboratorio.show();
                            $('#forma-proordenproduccion-window').find('.tipo_laboratorio').show();
                            $('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'480px'});
                        }
                        
                        
                    });
                    
                },"json");//termina llamada json
		
                $buscar_producto.click(function(event){
                    event.preventDefault();
                    tipo_preorden = $select_tipoorden.val();
                    
                    //para  Stock
                    if(tipo_preorden == 2){
                        $busca_productos(2, "");
                    }
                    
                    //para tipo labnoratorio
                    if(tipo_preorden == 3){
                        $busca_productos(3, "");
                    }
                    
                });
                
                $agregar_producto.click(function(event){
                    event.preventDefault();
                    //if(/^[A-Za-z0-9]*$/.test($sku_tmp.val())){
					if($sku_tmp.val().trim()!=''){
                        $tipo = parseInt($select_tipoorden.val());
                        if($tipo == 3){
                            //Esta parte es para las ordende de produccioin de productos en desarrollo, 
                            $seleccionar_version_de_formula($sku_tmp.val(), $tipo);
                            
                            //$opbtiene_datos_producto_por_sku($sku_tmp.val());
                        }else{
                            //llama el meto $opbtiene_datos_producto_por_sku para agregar un producto diferente a desarrollo
                            $opbtiene_datos_producto_por_sku($sku_tmp.val(), 0, 0);
                        }
                    }else{
                        jAlert("Agregue un c&oacute;digo", 'Atencion!');
                    }
                });
                
                //desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$sku_tmp.keypress(function(e){
                    if(e.which == 13){
                        $agregar_producto.trigger('click');
                        return false;
                    }
		});
		
                
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
                    var remove = function() {$(this).remove();};
                    $('#forma-proordenproduccion-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
                    var remove = function() {$(this).remove();};
                    $('#forma-proordenproduccion-overlay').fadeOut(remove);
		});
                
                $submit_actualizar.bind('click',function(){
                    $command_selected.val("1");
                    $agrega_esp_en_blanco_grid();
                    var trCount = $("tr", $tabla_productos_preorden).size();
                    if(trCount > 0){
                        jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
                            // If they confirmed, manually trigger a form submission
                            if (r) $submit_actualizar.parents("FORM").submit();
                        });
                    }else{
                        jAlert("Es necesario agregar productos.", 'Atencion!');
                    }
                    // Always return false here since we don't know what jConfirm is going to do
                    return false;
                });
                
	});
	
	
	
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
                        $(this).modalPanel_ProOrdenProduccion();
                        
                        var form_to_show = 'formaProOrdenProduccion00';
                        $('#' + form_to_show).each (function(){this.reset();});
                        var $forma_selected = $('#' + form_to_show).clone();
                        $forma_selected.attr({id : form_to_show + id_to_show});
                        
                        $('#forma-proordenproduccion-window').css({"margin-left": -415, "margin-top": -230});
                        
                        $forma_selected.prependTo('#forma-proordenproduccion-window');
                        $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
                        
                        $tabs_li_funxionalidad();
                        
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_datos_orden.json';
				$arreglo = {'id_orden':id_to_show,
								'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
							
				var $command_selected = $('#forma-proordenproduccion-window').find('input[name=command_selected]');
				var $id_orden = $('#forma-proordenproduccion-window').find('input[name=id_orden]');
				var $proceso_flujo_id = $('#forma-proordenproduccion-window').find('input[name=proceso_flujo_id]');
				var $select_tipoorden = $('#forma-proordenproduccion-window').find('select[name=tipoorden]');
				var $fecha_elavorar = $('#forma-proordenproduccion-window').find('input[name=fecha_elavorar]');
				var $observaciones = $('#forma-proordenproduccion-window').find('textarea[name=observaciones]');
				var $folio_op = $('#forma-proordenproduccion-window').find('input[name=folio_op]');
				var $lote_pop = $('#forma-proordenproduccion-window').find('input[name=lote_pop]');
				var $costo_ultimo = $('#forma-proordenproduccion-window').find('input[name=costo_ultimo]');
				var $costo_ultimo_text = $('#forma-proordenproduccion-window').find('.costo_ultimo');
				
				//
				var $titprod_tmp = $('#forma-proordenproduccion-window').find('input[name=titprod_tmp]');
				var $sku_tmp = $('#forma-proordenproduccion-window').find('input[name=sku_tmp]');
				var $id_producto_tmp = $('#forma-proordenproduccion-window').find('input[name=id_producto_tmp]');
				var $descripcion_tmp = $('#forma-proordenproduccion-window').find('input[name=descripcion_tmp]');
				var $especificaicones_lista = $('#forma-proordenproduccion-window').find('input[name=especificaicones_lista]');
				var $version_formula = $('#forma-proordenproduccion-window').find('input[name=version_formula]');
				var $id_formula = $('#forma-proordenproduccion-window').find('input[name=id_formula]');
				var $solicitante = $('#forma-proordenproduccion-window').find('input[name=solicitante]');
				var $vendedor = $('#forma-proordenproduccion-window').find('input[name=vendedor]');
				
				//grids detalle pedido
				var $tabla_productos_header = $('#forma-proordenproduccion-window').find('#subprocesos_seleccionados');
				var $tabla_productos_preorden = $('#forma-proordenproduccion-window').find('#grid_productos_seleccionados');
				
				//tipos de preorden
				var $preorden_tipo_pedido = $('#forma-proordenproduccion-window').find('.tipo_pedido');
				var tipo_stock_laboratorio = $('#forma-proordenproduccion-window').find('.tipo_stock_laboratorio'); 
				
				//href para buscar producto
				var $buscar_producto = $('#forma-proordenproduccion-window').find('a[href*=busca_producto]');
				var $agregar_producto = $('#forma-proordenproduccion-window').find('a[href*=agregar_producto]');
				//href para agregar producto al grid
				
				var $cancelar_proceso = $('#forma-proordenproduccion-window').find('#cancela_entrada');
				
				var $cerrar_plugin = $('#forma-proordenproduccion-window').find('#close');
				var $cancelar_plugin = $('#forma-proordenproduccion-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-proordenproduccion-window').find('#submit');
				
				
				var $botones_confirmacion = $('#forma-proordenproduccion-window').find('.botones_confirmacion');
				var $confirmar_programacion = $('#forma-proordenproduccion-window').find('#confirmar_programacion');
				var $confirmar_enviar_produccion = $('#forma-proordenproduccion-window').find('#confirmar_enviar_produccion');
				var $confirmar_terminada = $('#forma-proordenproduccion-window').find('#confirmar_terminada');
				var $cancelar_orden = $('#forma-proordenproduccion-window').find('#cancelar_orden');
				
				var $pdf_orden = $('#forma-proordenproduccion-window').find('#pdf_orden');
				var $pdf_requisicion = $('#forma-proordenproduccion-window').find('#pdf_requisicion');
				var $pdf_version_formula = $('#forma-proordenproduccion-window').find('#pdf_version_formula');
				
				$command_selected.val("new");
				$id_orden.val(0);
				
				$submit_actualizar.show();
				$botones_confirmacion.show();
				$confirmar_programacion.show();
				$confirmar_enviar_produccion.show();
				$confirmar_terminada.show();
				$cancelar_orden.show();
				$pdf_orden.show();
				
				//$costo_ultimo.hide();
				//$costo_ultimo_text.hide();
				
				//$sku.attr("readonly", true);
				//$titulo.attr("readonly", true);
				//$descripcion.attr("readonly", true);
								
				$buscar_producto.hide();
				$id_orden.val(id_to_show);
				
				$add_calendar($fecha_elavorar, " ", ">=");
                                
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("Los cambios se guardaron con exito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-proordenproduccion-overlay').fadeOut(remove);
						//$get_datos_grid();
					}else{
						
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-proordenproduccion-window').find('div.interrogacion').css({'display':'none'});
						$tabla_productos_preorden.find('input[name=persona]').css({'background' : '#ffffff'});
						$tabla_productos_preorden.find('input[name=equipo]').css({'background' : '#ffffff'});
						$tabla_productos_preorden.find('input[name=eq_adicional]').css({'background' : '#ffffff'});
						$tabla_productos_preorden.find('input[name=cantidad]').css({'background' : '#ffffff'});
						
						//$('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'578px'});//con errores
						$('#forma-proordenproduccion-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-proordenproduccion-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-proordenproduccion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								if(tmp.split(':')[0]=='statuscalidad'){
									jAlert(tmp.split(':')[1], 'Atencion!');
								}
								
								//alert(tmp.split(':')[0]);
								
								if(parseInt($("tr", $tabla_productos_preorden).size())>0){
									for (var i=1;i<=parseInt($("tr", $tabla_productos_preorden).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='apoerario'+i) || (tmp.split(':')[0]=='equipo'+i) || (tmp.split(':')[0]=='equipo_adicional'+i)){
											$('#forma-proordenproduccion-window').find('.proordenproduccion_div_one').css({'height':'578px'});//con errores
											
											$('#forma-proordenproduccion-window').find('#div_warning_grid').css({'display':'block'});
											//$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
											
											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
												$tabla_productos_preorden.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											if(tmp.split(':')[0].substring(0, 5) == 'apoerario'){
												$tabla_productos_preorden.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											if(tmp.split(':')[0].substring(0, 9) == 'equipo'){
												$tabla_productos_preorden.find('input[name=caducidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
                                                                                        
											if(tmp.split(':')[0].substring(0, 9) == 'equipo_adicional'){
												$tabla_productos_preorden.find('input[name=caducidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											
											var tr_warning = '<tr>';
													tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
													tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=sku' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
													tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=descripcion' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
													tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:485px; color:red"></td>';
											tr_warning += '</tr>';
											
											$('#forma-proordenproduccion-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
										}
										
									}
								}
							}
						}
						$('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
						$('#forma-proconfigproduccion-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
                                                
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
                                    
					cantidad_salida = 0;
					
					$id_orden.attr({'value': entry['Orden']['0']['id']});
					$observaciones.val( entry['Orden']['0']['observaciones']);
					$fecha_elavorar.attr({'value': entry['Orden']['0']['fecha_elavorar']});
					$proceso_flujo_id.attr({'value': entry['Orden']['0']['pro_proceso_flujo_id']});
					$folio_op.attr({'value': entry['Orden']['0']['folio']});
					$lote_pop.attr({'value': entry['Orden']['0']['lote']});
					$id_formula.attr({'value': entry['Orden']['0']['pro_estruc_id']});
					$solicitante.attr({'value': entry['Orden']['0']['solicitante']});
					$vendedor.attr({'value': entry['Orden']['0']['vendedor']});
					
					$costo_ultimo.attr({'value': entry['Orden']['0']['costo_ultimo']});
					
					array_almacenes = entry['Almacenes'];
					array_sucursales = entry['Sucursales'];
					array_extradata = entry['Extras'];
					
					array_instrumentos = entry['Instrumentos'];
					
					$select_tipoorden.children().remove();
					var orden_tipos_html = '';
					$.each(entry['ordenTipos'],function(entryIndex,pt){
						pt['titulo']=pt['titulo'].toUpperCase();
						
						if(entry['Orden']['0']['pro_orden_tipos_id'] == pt['id']){
							orden_tipos_html += '<option value="' + pt['id'] + '" selected="yes" >' + pt['titulo'] + '</option>';
						}
					});
					$select_tipoorden.append(orden_tipos_html);
					
					if(entry['OrdenDet'] != null){
						$.each(entry['OrdenDet'],function(entryIndex,prod){
							//$add_grid_componente_orden(0,prod['Sku'][0]['id'],prod['Sku'][0]['sku'],prod['Sku'][0]['descripcion'],""       ,""    ,""          , 0);
							if(prod['eq_adicional'] == ""){
								prod['eq_adicional'] = " ";
							}
							if(prod['empleado'] == ""){
								prod['empleado'] = " ";
							}
							
							if(prod['equipo'] == ""){
								prod['equipo'] = " ";
							}
							
							
							if(entry['Orden']['0']['pro_proceso_flujo_id'] == "3"){
								
								$cadena_especificaciones = prod['fineza_inicial']+"&&&"+prod['viscosidads_inicial']+"&&&"+prod['viscosidadku_inicial']+"&&&";
								$cadena_especificaciones += prod['viscosidadcps_inicial']+"&&&"+prod['densidad_inicial']+"&&&"+prod['volatiles_inicial']+"&&&"+prod['cubriente_inicial']+"&&&"+prod['tono_inicial']+"&&&";
								$cadena_especificaciones += prod['brillo_inicial']+"&&&"+prod['dureza_inicial']+"&&&"+prod['adherencia_inicial']+"&&&"+prod['hidrogeno_inicial']+"&&&";
								
								/*
								$cadena_especificaciones += prod['fineza_final']+"&&&"+prod['viscosidads_final']+"&&&"+prod['viscosidadku_final']+"&&&";
								$cadena_especificaciones += prod['viscosidadcps_final']+"&&&"+prod['densidad_final']+"&&&"+prod['volatiles_final']+"&&&"+prod['cubriente_final']+"&&&"+prod['tono_final']+"&&&";
								$cadena_especificaciones += prod['brillo_final']+"&&&"+prod['dureza_final']+"&&&"+prod['adherencia_final']+"&&&"+prod['hidrogeno_final']+"&&&";
								*/
							   
							   $cadena_especificaciones += prod['pro_instrumentos_fineza']+"&&&"+prod['pro_instrumentos_viscosidad1']+"&&&"+prod['pro_instrumentos_viscosidad2']+"&&&";
							   $cadena_especificaciones += prod['pro_instrumentos_viscosidad3']+"&&&"+prod['pro_instrumentos_densidad']+"&&&"+prod['pro_instrumentos_volatil']+"&&&";
							   $cadena_especificaciones += prod['pro_instrumentos_cubriente']+"&&&"+prod['pro_instrumentos_tono']+"&&&"+prod['pro_instrumentos_brillo']+"&&&";
							   $cadena_especificaciones += prod['pro_instrumentos_dureza']+"&&&"+prod['pro_instrumentos_adherencia']+"&&&"+prod['pro_instrumentos_hidrogeno']+"&&&";
							   
								if(prod['id_esp'] == "" || prod['id_esp'] == null){
									prod['id_esp'] = "0";
								}
								
								if(prod['num_lote'] == "" || prod['num_lote'] == null){
									prod['num_lote'] = " ";
								}
								
								cantidad_salida = prod['cantidad_salida'];
								//cantidad_salida
								//                                                                                                                                                                                                                      lote, especificaciones
								$add_grid_componente_orden_en_produccion(prod['id'],prod['inv_prod_id'],prod['sku'],prod['descripcion'],prod['cantidad'], entry['Orden']['0']['pro_proceso_flujo_id'] , prod['subproceso'],prod['pro_subprocesos_id'], prod['num_lote'], $cadena_especificaciones,prod['id_esp'],prod['unidad'], prod['unidad_id'], prod['densidad']);
								
							}else{
								if(entry['Orden']['0']['pro_proceso_flujo_id'] == "1" || entry['Orden']['0']['pro_proceso_flujo_id'] == "2"){
									
									pro_proceso_flujo_id = entry['Orden']['0']['pro_proceso_flujo_id'];
									if(entry['Orden']['0']['pro_proceso_flujo_id'] == "1"){
										pro_proceso_flujo_id = "2";
									}
									$add_grid_componente_orden(prod['id'],prod['inv_prod_id'],prod['sku'],prod['descripcion'],prod['empleado'],prod['equipo'],prod['eq_adicional'],prod['cantidad'], prod, pro_proceso_flujo_id,prod['unidad'], prod['unidad_id'], prod['densidad']);
									
								}else{
									if(entry['Orden']['0']['pro_proceso_flujo_id'] == "4" || entry['Orden']['0']['pro_proceso_flujo_id'] == "5"){
										$add_grid_componente_orden_finalizada_o_cancelada(prod['id'],prod['inv_prod_id'],prod['sku'],prod['descripcion'],prod['empleado'],prod['equipo'],prod['eq_adicional'],prod['cantidad'], prod, entry['Orden']['0']['pro_proceso_flujo_id'],prod['unidad'], prod['unidad_id'], prod['densidad']);
									}
								}
							}
						});
					};
					
					//cantidad_salida
					$ocullta_de_acuerdo_a_el_tipo_y_estatus($select_tipoorden.val(), entry['Orden']['0']['pro_proceso_flujo_id'], cantidad_salida);
					
                                    
				},"json");//termina llamada json
				
                                
				$buscar_producto.click(function(event){
					event.preventDefault();
					tipo_preorden = $select_tipoorden.val();

					//para  Stock
					if(tipo_preorden == 2){
						$busca_productos(2, "");
					}

					//para tipo labnoratorio
					if(tipo_preorden == 3){
						$busca_productos(3, "");
					}
					
				});
                                
				$agregar_producto.click(function(event){
					event.preventDefault();
					if(/^[A-Za-z0-9]*$/.test($sku_tmp.val())){
						$opbtiene_datos_producto_por_sku($sku_tmp.val(), 0, 0);
					}else{
						jAlert("Agregue un c&oacute;digo", 'Atencion!');
					}
				});
				
				//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
				$sku_tmp.keypress(function(e){
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
				});
                                
                                
				$submit_actualizar.bind('click',function(){
					$command_selected.val("1");
					$agrega_esp_en_blanco_grid();
					var trCount = $("tr", $tabla_productos_preorden).size();
					if(trCount > 0){
						jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) $submit_actualizar.parents("FORM").submit();
						});
					}else{
						jAlert("Es necesario agregar productos.", 'Atencion!');
					}
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
                                
				
				$confirmar_programacion.bind('click',function(){
					$command_selected.val("2");
					$agrega_esp_en_blanco_grid();
					var trCount = $("tr", $tabla_productos_preorden).size();
					if(trCount > 0){
						jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) $submit_actualizar.parents("FORM").submit();
						});
					}else{
						jAlert("Es necesario agregar productos.", 'Atencion!');
					}
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
                                
                                
				$confirmar_enviar_produccion.bind('click',function(){
					$muestra_requisicionop("");
					/*
					$command_selected.val("3");
					$agrega_esp_en_blanco_grid();
					var trCount = $("tr", $tabla_productos_preorden).size();
					if(trCount > 0){
						
						jConfirm('Desea guardar los cambios ?', 'Dialogo de Confirmacion', function(r) {
							
							cadena_pos = "";
							$tabla_productos_preorden.find('tr').each(function(){
								
								eliminar_tmp = $(this).find('input[name=eliminar]').val();
								id_reg_tmp = $(this).find('input[name=id_reg]').val();
								inv_prod_id = $(this).find('input[name=inv_prod_id]').val();
								subproceso_id = $(this).find('input[name=subproceso_id]').val();
								especificaciones = $(this).find('input[name=especificaciones]').val();
								id_reg_esp = $(this).find('input[name=id_reg_esp]').val();
								
								if(eliminar_tmp != null && id_reg_tmp != null && subproceso_id != null && especificaciones != null){
									//                  1               2                   3           
									cadena_pos += eliminar_tmp+"___"+id_reg_tmp+"___"+inv_prod_id+"___"+
										//      4                   5 
										subproceso_id+"___"+especificaciones+"___"+id_reg_esp+"$$$$";
								}
							});
							
							$especificaicones_lista.val(cadena_pos);
							
							if (r) $submit_actualizar.parents("FORM").submit();
						});
					}else{
						jAlert("Es necesario agregar productos.", 'Atencion!');
					}
					*/
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				$cancelar_orden.bind('click',function(){
					$command_selected.val("5");
					$agrega_esp_en_blanco_grid();
					var trCount = $("tr", $tabla_productos_preorden).size();
					if(trCount > 0){
						jConfirm('Desea cancelar la orden de prudcci&oacute;n?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) $submit_actualizar.parents("FORM").submit();
						});
					}else{
						jAlert("No se puede cancelar la orden.", 'Atencion!');
					}
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				$confirmar_terminada.bind('click',function(){
					$command_selected.val("4");
					$agrega_esp_en_blanco_grid();
					var trCount = $("tr", $tabla_productos_preorden).size();
					jConfirm('Desea Terminar la orden?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if(trCount > 0){
							if (r) $submit_actualizar.parents("FORM").submit();
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				
				$pdf_orden.bind('click',function(event){
					event.preventDefault();
					jConfirm('Descargar PDF?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							
							var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
							
							var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfProduccion/'+id_to_show+'/'+iu+'/out.json';
							window.location.href=input_json;
							
							
						}
					});
					
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				$pdf_requisicion.bind('click',function(event){
					event.preventDefault();
					jConfirm('Descargar PDF?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							
							var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
							
							var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfRequisicion/'+id_to_show+'/'+iu+'/out.json';
							window.location.href=input_json;
						}
					});
					
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				
				/*Codigo para descargar el pdf de productos formulados*/
				$pdf_version_formula.bind('click',function(event){
					event.preventDefault();
					
					$id_prod = $tabla_productos_preorden.find('input[name=inv_prod_id]').val();
					
					jConfirm('Descargar PDF?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
							
							var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfProdLaboratorio/'+id_to_show+'/'+iu+'/out.json';
							window.location.href=input_json;
						}
					});
					
					// Always return false here since we don't know what jConfirm is going to do
					return false;
					
				});
				
				
				//cerrar plugin
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-proordenproduccion-overlay').fadeOut(remove);
				});
				
				//boton cancelar y cerrar plugin
				$cancelar_plugin.click(function(event){
					var remove = function() {$(this).remove();};
					$('#forma-proordenproduccion-overlay').fadeOut(remove);
				});
				
			}
		}
	}
        
        
        //grid metarias primas de productos a formular
	$muestra_requisicionop = function(tipo_busqueda){
            sku_buscar = "";
                
		//limpiar_campos_grids();
		$(this).modalPanel_RequisicionOP();
		var $dialogoc =  $('#forma-requisicionop-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_requisicionop').find('table.formaBusqueda_requisicionop').clone());
		
		$('#forma-requisicionop-window').css({"margin-left": -300, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-requisicionop-window').find('#tabla_resultado');
		
                var $id_orden = $('#forma-proordenproduccion-window').find('input[name=id_orden]');
                
		var $campo_sku = $('#forma-requisicionop-window').find('input[name=campo_sku]');
		var $select_tipo_producto = $('#forma-requisicionop-window').find('select[name=tipo_producto]');
		var $campo_descripcion = $('#forma-requisicionop-window').find('input[name=campo_descripcion]');
                
		//var $cancelar_plugin_requisicionop = $('#forma-requisicionop-window').find('#cencela');
                
                var $enviar_requisicion = $('#forma-requisicionop-window').find('#enviar_requisicion');
		var $cancelar_plugin_requisicionop = $('#forma-requisicionop-window').find('#close');
                /*
		$cancelar_plugin_requisicionop.mouseover(function(){
                    $(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_requisicionop.mouseout(function(){
                    $(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		*/
                var $id_orden = $('#forma-proordenproduccion-window').find('input[name=id_orden]');
                
                //buscar todos los productos de la formula
		var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_requisicion_orden_prod.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val(), 'id_orden':$id_orden.val()}
                
                $.post(input_json_tipos,$arreglo,function(data){
                    
                    $.each(data['requisicion'],function(entryIndex,producto){
                        
                        var trCount = $("tr", $tabla_resultados).size();
                        trCount++;
                        
                        trr = '<tr>';
                            trr += '<td width="100">';
                                trr += '<a href="equivalente'+trCount+'">Equivalentes</a>';
                                trr += '<input type="hidden" id="id_prod_requisicion" value="'+producto['inv_prod_id']+'">';
                                trr += '<input type="hidden" id="id_reg_requisicion" value="'+producto['id']+'">';
                                trr += '<input type="hidden" id="elemento" value="'+producto['elemento']+'">';
                                trr += '<input type="hidden" id="pro_orden_prod_det_id" value="'+producto['pro_orden_prod_det_id']+'">';
                                trr += '<input type="hidden" id="pro_subprocesos_id" value="'+producto['pro_subprocesos_id']+'">';
                                trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                            trr += '</td>';
                            trr += '<td width="100">';
                                trr += '<input type="text" id="sku_prod_buscador" value="'+producto['sku']+'" class="borde_oculto" readOnly="true" style="width:100px;">';
                            trr += '</td>';
                            trr += '<td width="300">';
                                trr += '<input type="text" id="titulo_prod_buscador" value="'+producto['descripcion']+'" class="borde_oculto" readOnly="true" style="width:300px;">';
                            trr += '</td>';
                            trr += '<td width="100" >';
                                trr += '<input type="text" id="cantidad_requisicion" value="'+producto['cantidad']+'" class="borde_oculto" readOnly="true" style="width:100px;">';
                            trr += '</td>';
                            trr += '<td width="100" >';
                                trr += '<input type="text" id="existencia_requisicion" value="'+producto['existencia']+'" class="borde_oculto" readOnly="true" style="width:100px;">';
                            trr += '</td>';
                        trr += '</tr>';
                        
                        $tabla_resultados.append(trr);
                        
                        $tabla_resultados.find('a[href^=equivalente'+ trCount+']').bind('click',function(event){
                            event.preventDefault();
                            
                            //panel para los equivalentes
                            $(this).modalPanel_Equivalentes();
                            var $dialogoc_equiv =  $('#forma-equivalentes-window');
                            
                            $dialogoc_equiv.append($('div.buscador_equivalentes').find('table.formaBusqueda_equivalentes').clone());
                            
                            
                            $('#forma-equivalentes-window').css({"margin-left": -200, 	"margin-top": -150});
                            
                            var $tabla_resultados_equivalente = $('#forma-equivalentes-window').find('#tabla_resultado');
                            var $cancelar_plugin_equivalente = $('#forma-equivalentes-window').find('#cerrar');
                            /*
                            $cancelar_plugin_equivalente.mouseover(function(){
                                $(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
                            });
                            $cancelar_plugin_equivalente.mouseout(function(){
                                $(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
                            });
                            */
                            
                            
                            $tmp_parent = $(this).parent().parent();
                            var $id_producto = $tmp_parent.find('#id_prod_requisicion').val();
                            //buscar todos los productos equivalentes de el producto
                            var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_equivalentes.json';
                            $arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val(), 'id_producto':$id_producto}
                            
                            $.post(input_json_tipos,$arreglo,function(entry){
                                
                                if(entry['equivalentes'] != ""){
                                    
                                    $.each(entry['equivalentes'],function(entryIndex,equivalente){
                                        
                                        trr_tmp = '<tr>';
                                            trr_tmp += '<td width="100">';
                                                trr_tmp += '<input type="hidden" id="id_producto" value="'+equivalente['id']+'">';
                                                trr_tmp += '<input type="text" id="sku_prod" value="'+equivalente['sku']+'" class="borde_oculto" readOnly="true" style="width:100px;">';
                                            trr_tmp += '</td>';
                                            trr_tmp += '<td width="300">';
                                                trr_tmp += '<input type="text" id="titulo_prod" value="'+equivalente['descripcion']+'" class="borde_oculto" readOnly="true" style="width:300px;">';
                                            trr_tmp += '</td>';
                                            trr_tmp += '<td width="100" >';
                                                trr_tmp += '<input type="text" id="existencia" value="'+equivalente['existencia']+'" class="borde_oculto" readOnly="true" style="width:100px;">';
                                            trr_tmp += '</td>';
                                        trr_tmp += '</tr>';
                                        
                                        $tabla_resultados_equivalente.append(trr_tmp);
                                        
                                    });
                                    
                                    
                                    //seleccionar el equivalente por el cual se sustituira el producto
                                    $tabla_resultados_equivalente.find('tr').click(function(){
                                        
                                        var id_prod = $(this).find('#id_producto').val();
                                        var sku_prod = $(this).find('#sku_prod').val();
                                        var titulo_prod = $(this).find('#titulo_prod').val();
                                        var existencia = $(this).find('#existencia').val();
                                        
                                        var cantidad = $tmp_parent.find('#cantidad_requisicion').val();
                                        var elemento_tmp = $tmp_parent.find('#elemento').val();
                                        var pro_orden_prod_det_id = $tmp_parent.find('#pro_orden_prod_det_id').val();
                                         var pro_subprocesos_id = $tmp_parent.find('#pro_subprocesos_id').val();
                                        
                                        $tmp_parent.find('#delete').val("0");
                                        $tmp_parent.hide();
                                        //$tmp_parent
                                        
                                        
                                        //agregar el equivalente
                                        trr = '<tr>';
                                            trr += '<td width="100">';
                                                trr += 'Equivalente';
                                                trr += '<input type="hidden" id="id_prod_requisicion" value="'+id_prod+'">';
                                                trr += '<input type="hidden" id="id_reg_requisicion" value="0">';
                                                trr += '<input type="hidden" id="elemento" value="'+elemento_tmp+'">';
                                                trr += '<input type="hidden" id="pro_orden_prod_det_id" value="'+pro_orden_prod_det_id+'">';
                                                trr += '<input type="hidden" id="pro_subprocesos_id" value="'+pro_subprocesos_id+'">';
                                                trr += '<input type="hidden" id="delete" name="eliminar" value="1">';
                                            trr += '</td>';
                                            trr += '<td width="100">';
                                                trr += '<input type="text" id="sku_prod_buscador" value="'+sku_prod+'" class="borde_oculto" readOnly="true" style="width:100px;">';
                                            trr += '</td>';
                                            trr += '<td width="300">';
                                                trr += '<input type="text" id="titulo_prod_buscador" value="'+titulo_prod+'" class="borde_oculto" readOnly="true" style="width:300px;">';
                                            trr += '</td>';
                                            trr += '<td width="100" >';
                                                trr += '<input type="text" id="cantidad_requisicion" value="'+cantidad+'" style="width:100px;">';
                                            trr += '</td>';
                                            trr += '<td width="100" >';
                                                trr += '<input type="text" id="existencia_requisicion" value="'+existencia+'" class="borde_oculto" readOnly="true" style="width:100px;">';
                                            trr += '</td>';
                                        trr += '</tr>';
                                        
                                        $tmp_parent.after(trr);
                                        
                                        //oculta el plugin de equivalentes
                                        var remove = function() {$(this).remove();};
                                        $('#forma-equivalentes-overlay').fadeOut(remove);
                                    });
                                    
                                    
                                    
                                    $colorea_tr_grid($tabla_resultados_equivalente);
                                }else{
                                    //oculta el plugin de equivalentes
                                    jAlert("El producto, no tiene equivalente", 'Atencion!');
                                    var remove = function() {$(this).remove();};
                                    $('#forma-equivalentes-overlay').fadeOut(remove);
                                }
                            });
                            
                            $cancelar_plugin_equivalente.click(function(event){
                                //event.preventDefault();
                                var remove = function() {$(this).remove();};
                                $('#forma-equivalentes-overlay').fadeOut(remove);
                            });
                            
                        });
                        
                        
                    });
                    
                    $colorea_tr_grid($tabla_resultados);
                });
                
                $cancelar_plugin_requisicionop.bind('click',function(){
                    //event.preventDefault();
                    var remove = function() {$(this).remove();};
                    $('#forma-requisicionop-overlay').fadeOut(remove);
                });
		
                $enviar_requisicion.bind('click',function(event){
                    //event.preventDefault();
                    event.preventDefault();
                    jConfirm('Desea enviar la requisicion?', 'Dialogo de Confirmacion', function(r) {
                        // If they confirmed, manually trigger a form submission
                        if (r) {
                            
                            var $data_string = "";
                            lotes_completos = 1;
                            cadena_pos = "";
                            $tabla_resultados.find('tr').each(function(){
                                
                                eliminar_tmp = $(this).find('#delete').val();
                                id_reg_requisicion = $(this).find('#id_reg_requisicion').val();
                                id_prod_requisicion = $(this).find('#id_prod_requisicion').val();
                                cantidad_requisicion = $(this).find('#cantidad_requisicion').val();
                                existencia_requisicion = $(this).find('#existencia_requisicion').val();
                                elemento_requisicion = $(this).find('#elemento').val();
                                pro_orden_prod_det_id = $(this).find('#pro_orden_prod_det_id').val();
                                pro_subprocesos_id = $(this).find('#pro_subprocesos_id').val();
                                
                                //1___0___1483___12___d3da21c7-c4ba-49be-a241-9529336c5e75&&&1___0___158___0___2471c2a0-f253-4504-9bca-b7f843a5c72d&&&1___0___148___0___f84b5f6c-6cd4-45cb-a404-b532527f60e2&&&1___0___147___0___ &&&1___0___191___0___ &&&1___0___151___0___ &&&1___0___1493___0___ &&&1___0___1397___0___ &&&1___0___1390___0___ &&&1___0___374___0___ &&&1___0___378___0___ &&&1___0___1180___0___ &&&1___0___149___0___ &&&1___0___150___0___ &&&1___0___91___0___ &&&1___0___160___0___ &&&1___0___127___0___ &&&1___0___1483___0___ 
                                
                                cadena_pos += eliminar_tmp+"___"+id_reg_requisicion+"___"+id_prod_requisicion+"___"+
                                    cantidad_requisicion+"___"+existencia_requisicion+"___"+elemento_requisicion+"___"+
                                    pro_orden_prod_det_id+"___"+
                                    pro_subprocesos_id+"$$$$";
                                
                            });
                            
                            //alert(cadena_pos);
                            
                            cadena_pos = cadena_pos.substring(0, (cadena_pos.length - 4 ));
                            
                            
                            
                            //url para hacer la peticion pos para que genere la requisicion
                            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/send_requisicion_op.json';
                            
                            //para que ejecute la parte de generar la orden de entrada en la requisicion
                            //command_selected=6
                            
                            $id_formula = $('#forma-proordenproduccion-window').find('input[name=id_formula]').val();
                            var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
                            $arreglo = {'id':$id_orden.val(),'iu':iu,'data_string':cadena_pos,'command_selected':6, 
                            'id_formula':$id_formula}
                            
                            $.post(input_json,$arreglo,function(entry){
                                    
                                    ///Codigo js para las alertas de una requisicion
                                    //var respuestaProcesada = function(data){
                                    
                                            if ( entry['success'] == "true" ){
                                                jAlert("Los cambios se guardaron con exito", 'Atencion!');
                                                
                                                //ocultar plugin de la requisicion
                                                var remove = function() {$(this).remove();};
                                                $('#forma-requisicionop-overlay').fadeOut(remove);
                                                
                                                //ocultar plugin de la orden de produccion
                                                var remove = function() {$(this).remove();};
                                                $('#forma-proordenproduccion-overlay').fadeOut(remove);
                                                
                                                $get_datos_grid();
                                            }else{
                                                
                                                $('#forma-requisicionop-window').find('.proordenproduccion_div_one').css({'height':'578px'});//con errores
                                                $('#forma-requisicionop-window').find('#div_warning_grid').css({'display':'none'});
                                                $('#forma-requisicionop-window').find('#div_warning_grid').find('#grid_warning').children().remove();
                                                
                                                
                                                var valor = entry['success'].split('___');
                                                //muestra las interrogaciones
                                                    for (var element in valor){
                                                        tmp = entry['success'].split('___')[element];
                                                        longitud = tmp.split(':');
                                                            
                                                            if( longitud.length > 1 ){
                                                                
                                                                $('#forma-requisicionop-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                                                .parent()
                                                                .css({'display':'block'})
                                                                .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

                                                                //alert(tmp.split(':')[0]);

                                                                if(parseInt($("tr", $tabla_resultados).size())>0){
                                                                    for (var i=1;i<=parseInt($("tr", $tabla_resultados).size());i++){
                                                                        //if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='apoerario'+i) || (tmp.split(':')[0]=='equipo'+i) || (tmp.split(':')[0]=='equipo_adicional'+i)){
                                                                        if((tmp.split(':')[0]=='cantidad'+i) ){
                                                                            
                                                                            $('#forma-requisicionop-window').find('#div_warning_grid').css({'display':'block'});
                                                                            //$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
                                                                            
                                                                            var tr_warning = '<tr>';
                                                                                tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
                                                                                //tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=sku' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
                                                                                //tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $tabla_productos_preorden.find('input[name=descripcion' + i + ']').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
                                                                                tr_warning += '<td width="635"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:385px; color:red"></td>';
                                                                            tr_warning += '</tr>';
                                                                            
                                                                            $('#forma-requisicionop-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
                                                                        }

                                                                    }
                                                                }
                                                            }
                                                    }
                                                    $('#forma-requisicionop-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
                                                    $('#forma-requisicionop-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({'background-color' : '#e7e8ea'});

                                            }
                                    //}
                                    
                                    
                                    
                            },"json");
                            
                        }
                    });
                    
                    // Always return false here since we don't know what jConfirm is going to do
                    return false;
                    
                });
	}//termina buscador de productos
        
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



