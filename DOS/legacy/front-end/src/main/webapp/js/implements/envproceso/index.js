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
	var controller = $contextpath.val()+"/controllers/envproceso";
    
    //arreglo para Tipos de Productos
    var arrayProdTipos;
    
    //arreglo para Presentaciones de Productos
    var arrayEstatus;
    var arrayAlmacenes;
    var arrayPresentacionEnv;
    var decimales = 0;
    var equivalencia = 0;
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_envProceso = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Proceso de Envasado');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
        
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	var $busqueda_select_estatus = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_estatus]');
	
        //$busqueda_select_pres
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
		valor_retorno += "estatus" + signo_separador + $busqueda_select_estatus.val() + "|";
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
	
	
	$cargar_datos_buscador_principal= function(){
		var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosBuscadorPrincipal.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_lineas,$arreglo,function(data){
                    
			/*
			//carga select de tipos de producto
			$busqueda_select_tipo_prod.children().remove();
			var prodtipos = '<option value="0">[--Seleccionar Tipo--]</option>';
			$.each(data['ProdTipos'],function(entryIndex,tp){
				if(parseInt(tp['id'])==1){
					prodtipos += '<option value="' + tp['id'] + '" selected="yes">' + tp['titulo'] + '</option>';
				}else{
					prodtipos += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
				}
			});
			$busqueda_select_tipo_prod.append(prodtipos);
                        */
                        
			//carga select de Estatus
			$busqueda_select_estatus.children().remove();
			var presentacion = '<option value="0">[-Estatus--]</option>';
			$.each(data['Estatus'],function(entryIndex,est){
				presentacion += '<option value="' + est['id'] + '"  >' + est['titulo'] + '</option>';
			});
			$busqueda_select_estatus.append(presentacion);
			
			//asignar valores de presentaciones y tipo de Productos para utilizarlas mas adelante
			arrayProdTipos = data['ProdTipos'];
			arrayEstatus = data['Estatus'];
                        
		});
	}//termina funcion cargar datos buscador principal
	
	
	//ejecutar la funcion cargar datos al cargar la pagina por primera vez
	$cargar_datos_buscador_principal();
	
	
	$limpiar.click(function(event){
		event.preventDefault();
		$busqueda_codigo.val('');
		$busqueda_descripcion.val('');
                $busqueda_folio.val('');
		/*
		//carga select de tipos de producto
		$busqueda_select_tipo_prod.children().remove();
		var prodtipos = '<option value="0">[--Seleccionar Tipo--]</option>';
		var prodtipos = '';
		$.each(arrayProdTipos,function(entryIndex,tp){
			if(parseInt(tp['id'])==1){
				prodtipos += '<option value="' + tp['id'] + '" selected="yes">' + tp['titulo'] + '</option>';
			}else{
				prodtipos += '<option value="' + tp['id'] + '"  >' + tp['titulo'] + '</option>';
			}
		});
		$busqueda_select_tipo_prod.append(prodtipos);
		*/
		//carga select de Presentaciones
		$busqueda_select_estatus.children().remove();
		var presentacion = '<option value="0">[-Presentaci&oacute;n--]</option>';
		$.each(arrayEstatus,function(entryIndex,est){
			presentacion += '<option value="' + est['id'] + '"  >' + est['titulo'] + '</option>';
		});
		$busqueda_select_estatus.append(presentacion);
		
		$busqueda_codigo.focus();
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
		$busqueda_codigo.focus();
	});
	
	
	//aplicar evento keypress a campos para ejecutar la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_descripcion, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_estatus, $buscar);
	//$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_tipo_prod, $buscar);
	
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-envproceso-window').find('#submit').mouseover(function(){
			$('#forma-envproceso-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-envproceso-window').find('#submit').mouseout(function(){
			$('#forma-envproceso-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-envproceso-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-envproceso-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-envproceso-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-envproceso-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-envproceso-window').find('#close').mouseover(function(){
			$('#forma-envproceso-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		
		$('#forma-envproceso-window').find('#close').mouseout(function(){
			$('#forma-envproceso-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-envproceso-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-envproceso-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-envproceso-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-envproceso-window').find("ul.pestanas li").click(function() {
			$('#forma-envproceso-window').find(".contenidoPes").hide();
			$('#forma-envproceso-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-envproceso-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
		
	}
	
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
                                var valida_fecha=fecha_mayor($campo.val(),mostrarFecha());
                                $campo.DatePickerHide();
                                break;
                        }
                    }
                }
            });
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
	
	
	var $agregarTr = function(id_producto, codigo, descripcion, unidad, cantidad){
		//grid de productos
		var $grid_productos = $('#forma-envproceso-window').find('#grid_productos');
		
		//obtiene numero de trs
		var noTr = $("tr", $grid_productos).size();
		var encontrado = 0;
		
		if(parseInt(noTr)>0){
			//busca el codigo del producto en el grid
			$grid_productos.find('tr').each(function (index){
				if(( $(this).find('input[name=cod]').val() == codigo.toUpperCase() ) && (parseInt($(this).find('input[name=eliminado]').val())!=0)){
					encontrado=1;//el producto ya esta en el grid
				}
			});
		}
		
		noTr++;
		
		if(parseInt(encontrado)<=0){//si el producto no esta en el grid entra aqui
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="58">';
					trr += '<a href="elimina_producto" class="delete'+ noTr +'">Eliminar</a>';
					trr += '<input type="hidden" 	name="eliminado" id="elim" class="elim'+ noTr +'" value="1">';
					trr += '<input type="hidden" 	name="iddetalle" id="idd"  class="idd'+ noTr +'" value="0">';//este es el id del registro que ocupa el producto en la tabla detalle
					trr += '<input type="hidden" 	name="noTr" value="'+ noTr +'">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="130">';
					trr += '<input type="hidden" 	name="idprod" id="idprod" value="'+ id_producto +'">';
					trr += '<input type="text" 		name="cod" value="'+ codigo +'" id="cod" class="borde_oculto" readOnly="true" style="width:126px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="290">';
					trr += '<input type="text" 		name="desc" value="'+ descripcion +'" id="desc" class="borde_oculto" readOnly="true" style="width:286px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<input type="text" 		name="uni" value="'+ unidad +'" id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" 		name="cant" value="'+ cantidad +'" id="cant" class="cant'+ noTr +'" style="width:76px;">';
				trr += '</td>';
			trr += '</tr>';
			
			$grid_productos.append(trr);
			
			$permitir_solo_numeros($grid_productos.find('input.cant'+ noTr));
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_productos.find('input.cant'+ noTr).focus(function(e){
				if($(this).val().trim()==''){
					$(this).val('');
				}else{
					if( parseFloat($(this).val())==0 ){
						$(this).val('');
					}
				}
			});
			
			//al perder enfoque el campo cantidad
			$grid_productos.find('input.cant'+ noTr).blur(function(){
				if($(this).val().trim()==''){
					$(this).val(' ');
				}
			});
			
			//elimina un producto del grid
			$grid_productos.find('.delete'+ noTr).bind('click',function(event){
				event.preventDefault();
				if(parseInt($(this).parent().find('#elim').val()) != 0){
					//tomamos el id detalle
					var idDetalle = $(this).parent().find('#idd').val();
					
					//asigna espacios en blanco a todos los input de la fila eliminada
					$(this).parent().parent().find('input').val(' ');
					
					//asigna un 0 al input eliminado como bandera para saber que esta eliminado
					$(this).parent().find('#elim').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
					
					//devolvemos el id detalle para conservar el id eliminado y eliminarlo de la tabla detalle
					$(this).parent().find('#idd').val(idDetalle);
					
					//oculta la fila eliminada
					$(this).parent().parent().hide();
				}
			});
			
			//Limpiar los campos Codigo y Nombre del producto
			$('#forma-envproceso-window').find('input[name=codigo_componente]').val('');
			$('#forma-envproceso-window').find('input[name=nombre_componente]').val('');
			
			//asignar el enfoque
			$grid_productos.find('input.cant'+ noTr).focus();
			
		}else{
			jAlert('El producto: '+codigo+' ya se encuentra en el listado, seleccione otro diferente.', 'Atencion!', function(r) { 
				$('#forma-envproceso-window').find('input[name=codigo_componente]').focus();
			});
		}
	};
	
        $calcula_cantidad = function(campo, control){
            //$calcula_cantidad($(this), 'inpút');
            
            var $grid_presntaciones = $('#forma-envproceso-window').find('#grid_productos');
            var $exis_pres = $('#forma-envproceso-window').find('input[name=exis_pres]').val();
            var $exis_uni = $('#forma-envproceso-window').find('input[name=exis_uni]').val();
            var $unidad = $('#forma-envproceso-window').find('input[name=unidad]').val();
            
            calculo_total = 0;
            var $select_almacen_orig = $('#forma-envproceso-window').find('select[name=select_almacen_orig]');
            var trCount = $('tr', $grid_presntaciones).each (function(){
                //cantpres
                //uni
                //cantuni
                var eliminado = $(this).find('#eliminado').val();
                
                if(parseInt(eliminado) == 1){
                    var cantidad_tr = $(this).find('input[name=cantpres]').val();
                    var presentacion_id_tr = $(this).find('select[name=select_pres_dest]').val();
                    var factor = 0;
                    
                    $.each(arrayPresentacionEnv,function(entryIndex,pres){
                        if(parseInt(pres['id']) == parseInt(presentacion_id_tr)){
                            factor = pres['equivalencia'];
                        }
                    });
					//alert("factor="+factor);
                    calculo_tr = parseFloat(factor) * parseFloat(cantidad_tr);
                    calculo_tr = parseFloat(calculo_tr).toFixed(decimales);
					
                    $(this).find('input[name=cantuni]').val(calculo_tr);
                    $(this).find('input[name=uni]').val($unidad);
                    calculo_total = parseFloat(calculo_total) + parseFloat(calculo_tr);
					
                    if(parseFloat(calculo_total) > parseFloat($exis_pres)){
                        //Aqui mne quede, asdiuasiudiasd asid asudiasi dsd
                        //alert("asdsd");
                        //alert('   campo:'+campo.val() + '    control:' + control);
                        
                        if(control == 'input'){
                            campo.val(0);
                            campo.parent().parent().find('input[name=cantuni]').val(0);
                        }
                    }
                }
            });
            
            //alert("$exis_pres="+$exis_pres+"\ncalculo_total="+calculo_total+"\nequivalencia="+equivalencia);
            
            $('#forma-envproceso-window').find('input[name=disp_pres]').val(parseFloat(parseFloat($exis_pres) - parseFloat(parseFloat(calculo_total) / parseFloat(equivalencia))).toFixed(decimales));
            $('#forma-envproceso-window').find('input[name=disp_uni]').val(parseFloat(parseFloat($exis_uni) - parseFloat(calculo_total)).toFixed(decimales));
        }
        
        
        
        $add_trr_presentacion_enbasar = function(iddetalle, aml_origen, idconf,pres_dest_id, cantpres,uni,cantuni,aml_dest_id , estatus){
            var $producto_id = $('#forma-envproceso-window').find('input[name=producto_id]');
            var $grid_presntaciones = $('#forma-envproceso-window').find('#grid_productos');
            
            var $select_almacen_orig = $('#forma-envproceso-window').find('select[name=select_almacen_orig]');
            var trCount = $('tr', $grid_presntaciones).size();
            //alert(trCount);
            
            html_tr = '<tr>';
                html_tr += '<td class="grid" style="font-size:14px; font-weight:bold; border:1px solid #C1DAD7;" width="30">';
                    html_tr += '<a href="#" class="add'+trCount+'">&nbsp;&nbsp;+&nbsp;&nbsp;</a>';
                html_tr += '</td>';
                html_tr += '<td class="grid" style="font-size:14px; font-weight:bold; border:1px solid #C1DAD7;" width="30">';
                    html_tr += '<input type="hidden" name="eliminado" id="eliminado" value="1">';
                    html_tr += '<input type="hidden" 	name="iddetalle" id="idd"  class="idd'+ trCount +'" value="'+iddetalle+'">';//este es el id del registro que ocupa el producto en la tabla detalle
                    html_tr += '<input type="hidden" 	name="noTr" value="'+ trCount +'">';//numero de posicion de el tr en el grid
                    html_tr += '<a href="#" class="delete'+trCount+'">&nbsp;&nbsp;-&nbsp;&nbsp;</a>';
                html_tr += '</td>';
                html_tr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="180">';
                    html_tr += '<input type="hidden" name="idprod" id="idprod" value="'+$producto_id.val()+'">';
                    html_tr += '<select name="select_aml_origen" id="select_aml_origen'+trCount+'" style="width:156px;">';
                        html_tr += '<option value="0" selected="yes">[--Seleccionar Almacen--]</option>';
                        $.each(arrayAlmacenes,function(entryIndex,alm){
                            //alert(alm['id']);
                            if(estatus == 1){
                                if(aml_origen == alm['id']){
                                    html_tr += '<option value="' + alm['id'] + '" selected="yes" >' + alm['titulo'] + '</option>';
                                }else{
                                    html_tr += '<option value="' + alm['id'] + '" >' + alm['titulo'] + '</option>';
                                }
                            }else{
                                if(aml_origen == alm['id']){
                                    html_tr += '<option value="' + alm['id'] + '" selected="yes" >' + alm['titulo'] + '</option>';
                                }
                            }
                        });
                    html_tr += '</select>';
                html_tr += '</td>';
                html_tr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="130">';
                    html_tr += '<input type="hidden" name="idconf" id="idconf" value="'+idconf+'">';//id de el registro en la tabla de configuracioones
                    html_tr += '<select name="select_pres_dest" id="select_pres_dest'+trCount+'" style="width:126px;">';
                        html_tr += '<option value="0" selected="yes">[--Presentacion--]</option>';
                        
                        $.each(arrayPresentacionEnv,function(entryIndex,pres){
                            //pres_dest_id
                            if(estatus == 1){
                                if(pres_dest_id == pres['id']){
                                    html_tr += '<option value="' + pres['id'] + '" selected="yes" >' + pres['titulo'] + '</option>';
                                }else{
                                    html_tr += '<option value="' + pres['id'] + '" >' + pres['titulo'] + '</option>';
                                }
                            }else{
                                if(pres_dest_id == pres['id']){
                                    html_tr += '<option value="' + pres['id'] + '" selected="yes" >' + pres['titulo'] + '</option>';
                                }
                            }
                            //html_tr += '<option value="' + pres['id'] + '" >' + pres['titulo'] + '</option>';
                        });
                    html_tr += '</select>';
                html_tr += '</td>';
                html_tr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="85">';
                    html_tr += '<input type="text" name="cantpres" id="cantpres'+trCount+'" value="'+cantpres+'" style="width:80px;" maxlength="10">';
                html_tr += '</td>';
                html_tr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
                    html_tr += '<input type="text" name="uni" value="" id="uni" class="borde_oculto" value="'+uni+'" readOnly="true" style="width:86px;">';
                html_tr += '</td>';
                html_tr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="95">';
                    html_tr += '<input type="text" 	name="cantuni" id="cantuni'+trCount+'" value="'+cantuni+'" class="borde_oculto" readOnly="true" style="width:90px; text-align:right;">';
                html_tr += '</td>';
                html_tr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="180">';
                    html_tr += '<select name="select_aml_dest" id="select_aml_dest'+trCount+'" style="width:156px;">';
                        html_tr += '<option value="0" selected="yes">[--Seleccionar Almacen--]</option>';
                        //aml_dest_id
                        $.each(arrayAlmacenes,function(entryIndex,alm){
                            if(estatus == 1){
                                if(aml_dest_id == alm['id']){
                                    html_tr += '<option value="' + alm['id'] + '" selected="yes" >' + alm['titulo'] + '</option>';
                                }else{
                                    html_tr += '<option value="' + alm['id'] + '" >' + alm['titulo'] + '</option>';
                                }
                            }else{
                                if(aml_dest_id == alm['id']){
                                    html_tr += '<option value="' + alm['id'] + '" selected="yes" >' + alm['titulo'] + '</option>';
                                }
                            }
                        });
                    html_tr += '</select>';
                html_tr += '</td>';
            html_tr += '</tr>';
            
            $grid_presntaciones.append(html_tr);
            
            $grid_presntaciones.find('.add'+trCount).bind('click',function(event){
                event.preventDefault();
                $add_trr_presentacion_enbasar(0, 0, 0,0, 0,'',0,0 , 1);
            });
            
            $grid_presntaciones.find('.delete'+trCount).bind('click',function(event){
                event.preventDefault();
                $(this).parent().parent().hide();
                $(this).parent().parent().find('#eliminado').val(0);
                $calcula_cantidad($(this).parent().parent().find('input[name=cantpres]'), 'input');
            });
            
            $grid_presntaciones.find('#select_aml_origen'+trCount).change(function(event){
                event.preventDefault();
                var select_pres_dest = $(this).parent().parent().find('select[name=select_pres_dest]');
                var idprod = $(this).parent().parent().find('input[name=idprod]');
                var alm_id = $(this).val();
            });
            
            
            $grid_presntaciones.find('#select_pres_dest'+trCount).change(function(event){
                event.preventDefault();
                
                //var select_aml_origen = $(this).parent().parent().find('select[name=select_aml_origen]');
                var $selected = parseInt($(this).val());
                var $idconf = $(this).parent().parent().find('input[name=idconf]');
                /*
                $.each(arrayPresentacionEnv,function(entryIndex,pres){
                    html_tr += '<option value="' + pres['id'] + '" >' + pres['titulo'] + '</option>';
                });
                */
                
                $.each(arrayPresentacionEnv,function(entryIndex,pres){
                    if(parseInt(pres['id']) == $selected ){
                        $idconf.val(pres['id_env']);
                    }
                });
            });
            
            /*
            $grid_presntaciones.find('#cantuni'+trCount).blur(function(){
                alert("ssdd");
            });
            */
            
            //al perder enfoque el campo cantidad
            $grid_presntaciones.find('#cantpres'+trCount).blur(function(){
                if($(this).val().trim()==''){
                    $(this).val(' ');
                }else{
					$(this).val( parseFloat($(this).val()).toFixed(decimales) );
					$calcula_cantidad($(this), 'input');
                }
            });
            
            //al perder enfoque el campo cantidad
            $grid_presntaciones.find('#cantpres'+trCount).focus(function(){
                if(!$(this).val().trim()==''){
                    if(parseFloat($(this).val().trim())==0){
						$(this).val('');
					}
                }else{
					$(this).val('');
                }
            });
            
            
			//validar campo cantidad, solo acepte numeros y punto
			$grid_presntaciones.find('#cantpres'+trCount).keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}
			});
            
            
            
        }
        
	
	
	var $agregarDatosProductoSeleccionado = function(id_orden_buscador, id_prod_buscador, id_unidad_buscador, unidad_buscador,folio_orden_buscador, codigo_prod_buscador, descripcion_prod_buscador,id_almacen_buscador,cantidad, id_presentacion_buscador,  arregloPres){
		var $grid_presntaciones = $('#forma-envproceso-window').find('#grid_productos');
		$grid_presntaciones.children().remove();
		
		$('#forma-envproceso-window').find('input[name=producto_id]').val(id_prod_buscador);
		$('#forma-envproceso-window').find('input[name=codigo]').val(codigo_prod_buscador);
		$('#forma-envproceso-window').find('input[name=descripcion]').val(descripcion_prod_buscador);
		$('#forma-envproceso-window').find('input[name=produccion_id]').val(id_orden_buscador);
		$('#forma-envproceso-window').find('input[name=folio_produccion]').val(folio_orden_buscador);
		$('#forma-envproceso-window').find('input[name=unidad]').val(unidad_buscador);
		$('#forma-envproceso-window').find('input[name=exis_uni]').val(cantidad);
		$('#forma-envproceso-window').find('input[name=disp_uni]').val(cantidad);
		$('#forma-envproceso-window').find('input[name=exis_pres]').val(cantidad);
		$('#forma-envproceso-window').find('input[name=disp_pres]').val(cantidad);
                
		var $select_presentacion_orig = $('#forma-envproceso-window').find('select[name=select_pres_orden_orig]');
		var $select_almacen_orig = $('#forma-envproceso-window').find('select[name=select_alm_orden_orig]');
		//var $select_estatus = $('#forma-envproceso-window').find('select[name=select_estatus]');
                
		
		if (parseInt(arregloPres.length) > 0){
                    
			$('#forma-envproceso-window').find('select[name=select_pres_orden_orig]').children().remove();
			var html_pres = '';
			$.each(arregloPres,function(entryIndex,pres){
				if(parseInt(id_presentacion_buscador) == parseInt(pres['id'])){
					html_pres += '<option value="' + pres['id'] + '" selected="yes">' + pres['titulo'] + '</option>';
				}else{
					html_pres += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
				}
			});
			$('#forma-envproceso-window').find('select[name=select_presentacion_orig]').append(html_pres);
			
			$select_almacen_orig.children().remove();
			var almacen = '<option value="0">[-Almacen--]</option>';
			$.each(arrayAlmacenes,function(entryIndex,alm){
				if(id_almacen_buscador == alm['id']){
					almacen += '<option value="' + alm['id'] + '" selected="yes" >' + alm['titulo'] + '</option>';
				}else{
					almacen += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
				}
			});
			$select_almacen_orig.append(almacen);
			
			
			/*Obtiene las presentaciones de el producto*/
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesProducto.json';
			$arreglo = {'id_prod':id_prod_buscador };
			
			$.post(input_json,$arreglo,function(entry){
				//carga select de Presentaciones
				$select_presentacion_orig.children().remove();
				var presentacion = '<option value="0">[-Presentaci&oacute;n--]</option>';
				$.each(entry['Presentaciones'],function(entryIndex,pres){
					alert(pres['titulo'].toUpperCase() + " = " + unidad_buscador.toUpperCase());
					if(pres['titulo'].toUpperCase() == unidad_buscador.toUpperCase()){
						presentacion += '<option value="' + pres['id'] + '" selected="yes" >' + pres['titulo'] + '</option>';
					}
				});
				$select_presentacion_orig.append(presentacion);
			});//termina llamada json
			
			/*Obtiene las presentaciones de el producto*/
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesConfProducto.json';
			$arreglo = {'id_prod':id_prod_buscador };
			
			$.post(input_json,$arreglo,function(entry){
				//carga select de Presentaciones
				if(entry['PresentacionEnvases'] != null ){
					arrayPresentacionEnv = entry['PresentacionEnvases'];
					//Sy hay configuracion de presentacion, agrega un elemento, de lo contrario arroja un mensaje de error.
					$add_trr_presentacion_enbasar(0, 0, 0,0, 0,'',0,0 , 1);
					//$add_trr_presentacion_enbasar();
				}else{
					jAlert("No hay configuraci&oacute;n para "+codigo_prod_buscador+"", 'Atencion!');
				}
			});//termina llamada json
		}else{
			$('#forma-envproceso-window').find('select[name=select_presentacion_orig]').remove();
			var html_pres = '<option value="0">[-Presentaci&oacute;n--]</option>';
			$('#forma-envproceso-window').find('select[name=select_presentacion_orig]').append(html_pres);
		}
		
		//asignar el enfoque
		$('#forma-envproceso-window').find('input[name=codigo_componente]').focus();
	}
	
	
	
	
	
	//buscador de productos
	var $buscar_orden_prod = function(tipoBusqueda, codigo, descripcion, folio){
		//limpiar_campos_grids();
		$(this).modalPanel_BuscaOrdenProduccion();
		var $dialogoc =  $('#forma-buscaordenprod-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_orden').find('table.formaBusqueda_ordenproduccion').clone());
		
		$('#forma-buscaordenprod-window').css({"margin-left": -200, 	"margin-top": -110});
		
		var $tabla_resultados = $('#forma-buscaordenprod-window').find('#tabla_resultado');
		
		var $campo_folio = $('#forma-buscaordenprod-window').find('input[name=campo_folio]');
                var $campo_sku = $('#forma-buscaordenprod-window').find('input[name=campo_sku]');
		var $campo_descripcion = $('#forma-buscaordenprod-window').find('input[name=campo_descripcion]');
		
		var $busca_orden_modalbox = $('#forma-buscaordenprod-window').find('#busca_orden_modalbox');
		var $cancelar_plugin_busca_producto = $('#forma-buscaordenprod-window').find('#cencela');
		
		//funcionalidad botones
		$busca_orden_modalbox.mouseover(function(){
                        $(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$busca_orden_modalbox.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_plugin_busca_producto.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$cancelar_plugin_busca_producto.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		
		$campo_folio.val(folio);
		$campo_sku.val(codigo);
		$campo_descripcion.val(descripcion);
		$campo_folio.focus();
		$campo_folio.val(folio);
                
		//click buscar productos
		$busca_orden_modalbox.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorOrdenProduccion.json';
			$arreglo = {	'folio':$campo_folio.val(),
							'sku':$campo_sku.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Ordenes'],function(entryIndex,orden){
					trr = '<tr>';
						trr += '<td width="90">';
							trr += '<input type="hidden" id="cantidad_buscador" value="'+orden['cantidad']+'">';
							trr += '<input type="hidden" id="id_orden_buscador" value="'+orden['id_op']+'">';
							trr += '<span class="folio_orden_buscador">'+orden['folio']+'</span>';
							trr += '<span class="decimales_orden_buscador" style="display:none;">'+orden['decimales']+'</span>';
						trr += '</td>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prod_buscador" value="'+orden['producto_id']+'">';
							trr += '<span class="codigo_prod_buscador">'+orden['sku']+'</span>';
						trr += '</td>';
						trr += '<td width="200">';
							trr += '<span class="descripcion_prod_buscador">'+orden['descripcion']+'</span>';
						trr += '</td>';
						trr += '<td width="110">';
							trr += '<input type="hidden" id="unidad_buscador" value="'+orden['unidad']+'">';
							trr += '<input type="hidden" id="id_unidad_buscador" value="'+orden['unidad_id']+'">';
							trr += '<input type="hidden" id="id_presentacion_buscador" value="'+orden['inv_prod_presentacion_id']+'">';
							trr += '<span class="unidad_buscador">'+orden['cantidad']+'</span>';
							trr += '<input type="hidden" id="id_almacen_buscador" value="'+orden['inv_alm_id']+'">';
						trr += '</td>';
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
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					//asignar a los campos correspondientes el sku y y descripcion
					var id_orden_buscador = $(this).find('#id_orden_buscador').val();
					var id_prod_buscador = $(this).find('#id_prod_buscador').val();
					var id_unidad_buscador = $(this).find('#id_unidad_buscador').val();
					var id_presentacion_buscador = $(this).find('#id_presentacion_buscador').val();
					var unidad_buscador = $(this).find('#unidad_buscador').val();
					var id_almacen_buscador = $(this).find('#id_almacen_buscador').val();
					var cantidad_buscador = $(this).find('#cantidad_buscador').val();
                                        
					var folio_orden_buscador = $(this).find('span.folio_orden_buscador').html();
					var codigo_prod_buscador = $(this).find('span.codigo_prod_buscador').html();
					var descripcion_prod_buscador = $(this).find('span.descripcion_prod_buscador').html();
					var descripcion_prod_buscador = $(this).find('span.descripcion_prod_buscador').html();
					decimales = $(this).find('span.decimales_orden_buscador').html();
					
					//alert("id_presentacion_buscador: "+id_presentacion_buscador);
					
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesProducto.json';
					$arreglo = {'id_prod':id_prod_buscador,'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					$.post(input_json,$arreglo,function(entry){
						
						$.each( entry['Presentaciones'],function(entryIndex,pres){
							if(parseInt(id_presentacion_buscador) == parseInt(pres['id'])){
								equivalencia = pres['equivalencia'];
								//alert("equivalencia="+equivalencia);
							}
						});
						
						alert("id_prod:"+id_prod_buscador +"    id_uni:"+id_unidad_buscador+"    uni:"+unidad_buscador);
						
						$agregarDatosProductoSeleccionado(id_orden_buscador, id_prod_buscador, id_unidad_buscador, unidad_buscador, folio_orden_buscador, codigo_prod_buscador, descripcion_prod_buscador,id_almacen_buscador,cantidad_buscador,id_presentacion_buscador, entry['Presentaciones']);
						//llamada a la funcion para agregar los datos del Producto seleccionado
					});
                                        
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaordenprod-overlay').fadeOut(remove);
				});
			});
		});
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_sku.val() != ''  ||  $campo_descripcion.val() != ''){
			$busca_orden_modalbox.trigger('click');
		}
                
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_folio, $busca_orden_modalbox);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_sku, $busca_orden_modalbox);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_descripcion, $busca_orden_modalbox);
		
		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaordenprod-overlay').fadeOut(remove);
			
			//asignar el enfoque al campo sku del producto
			if(parseInt(tipoBusqueda)==1){
				$('#forma-envproceso-window').find('input[name=codigo]').focus();
			}else{
				$('#forma-envproceso-window').find('input[name=codigo_componente]').focus();
			}
		});
                
	}//termina buscador de productos
	
        
        $autocomplete_input = function($campo, json_input){
            $campo.autocomplete({
                source: function(request, response){
                    
                    $arreglo = {'cadena':$campo.val(),
                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
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
        
        
        $activa_campos_por_estatus = function(){
            //carga select de Estatus
            var $select_estatus = $('#forma-envproceso-window').find('select[name=select_estatus]');
            var $generarpdf = $('#forma-envproceso-window').find('#generarpdf');
            //alert($select_estatus.val());
            $generarpdf.hide();
            
            if(parseInt($select_estatus.val()) == 0){
                $select_estatus.children().remove();
                var estatus_html = '<option value="0">[-Estatus--]</option>';
                $.each(arrayEstatus,function(entryIndex,est){
                    if(est['id'] == 1){
                        estatus_html += '<option value="' + est['id'] + '" selected="yes" >' + est['titulo'] + '</option>';
                    }
                });
                $select_estatus.append(estatus_html);
            }
            
            if(parseInt($select_estatus.val()) == 1){
                $select_estatus.children().remove();
                var estatus_html = '';
                $.each(arrayEstatus,function(entryIndex,est){
                    if(est['id'] == 1){
                        estatus_html += '<option value="' + est['id'] + '" selected="yes" >' + est['titulo'] + '</option>';
                    }else{
                        if((est['id'] == 2) || (est['id'] == 4)){
                            estatus_html += '<option value="' + est['id'] + '" >' + est['titulo'] + '</option>';
                        }
                    }
                });
                $select_estatus.append(estatus_html);
            }
            
            
            if(parseInt($select_estatus.val()) == 2){
                $select_estatus.children().remove();
                var estatus_html = '';
                $.each(arrayEstatus,function(entryIndex,est){
                    if(est['id'] == 2){
                        estatus_html += '<option value="' + est['id'] + '" selected="yes" >' + est['titulo'] + '</option>';
                    }else{
                        if(est['id'] == 3){
                            estatus_html += '<option value="' + est['id'] + '" >' + est['titulo'] + '</option>';
                        }
                    }
                });
                $select_estatus.append(estatus_html);
            }
            
            if(parseInt($select_estatus.val()) == 3){
                $select_estatus.children().remove();
                var estatus_html = '';
                $.each(arrayEstatus,function(entryIndex,est){
                    if(est['id'] == 3){
                        estatus_html += '<option value="' + est['id'] + '" selected="yes" >' + est['titulo'] + '</option>';
                    }
                });
                $select_estatus.append(estatus_html);
            }
            
            if(parseInt($select_estatus.val()) == 4){
                $generarpdf.show();
                
                $select_estatus.children().remove();
                var estatus_html = '';
                $.each(arrayEstatus,function(entryIndex,est){
                    if(est['id'] == 4){
                        estatus_html += '<option value="' + est['id'] + '" selected="yes" >' + est['titulo'] + '</option>';
                    }
                });
                $select_estatus.append(estatus_html);
            }
            
            
        }
	
        
	//nuevo 
	$new_envProceso.click(function(event){
		event.preventDefault();
                
		var id_to_show = 0;
		$(this).modalPanel_modalboxenvproceso();
		
		var form_to_show = 'formaenvproceso00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-envproceso-window').css({"margin-left": -460, 	"margin-top": -270});
		$forma_selected.prependTo('#forma-envproceso-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $identificador = $('#forma-envproceso-window').find('input[name=identificador]');
                
		var $folio = $('#forma-envproceso-window').find('input[name=folio]');
		var $fecha = $('#forma-envproceso-window').find('input[name=fecha]');
		var $hora = $('#forma-envproceso-window').find('input[name=hora]');
		var $producto_id = $('#forma-envproceso-window').find('input[name=producto_id]');
		var $codigo = $('#forma-envproceso-window').find('input[name=codigo]');
		var $descripcion = $('#forma-envproceso-window').find('input[name=descripcion]');
		var $produccion_id = $('#forma-envproceso-window').find('input[name=produccion_id]');
		var $folio_produccion = $('#forma-envproceso-window').find('input[name=folio_produccion]');
		var $exis_pres = $('#forma-envproceso-window').find('input[name=exis_pres]');
		var $disp_pres = $('#forma-envproceso-window').find('input[name=disp_pres]');
		var $unidad = $('#forma-envproceso-window').find('input[name=unidad]');
		var $exis_uni = $('#forma-envproceso-window').find('input[name=exis_uni]');
		var $disp_uni = $('#forma-envproceso-window').find('input[name=disp_uni]');
		var $equipo = $('#forma-envproceso-window').find('input[name=equipo]');
		var $operador = $('#forma-envproceso-window').find('input[name=operador]');
		var $merma = $('#forma-envproceso-window').find('input[name=merma]');
				
		var $select_presentacion_orig = $('#forma-envproceso-window').find('select[name=select_pres_orden_orig]');
		var $select_almacen_orig = $('#forma-envproceso-window').find('select[name=select_alm_orden_orig]');
		
		var $select_estatus = $('#forma-envproceso-window').find('select[name=select_estatus]');
		
		//href para Agregar y Buscar producto
		var $buscar_orden = $('#forma-envproceso-window').find('#buscar_orden');
                
		//grid de productos
		var $grid_productos = $('#forma-envproceso-window').find('#grid_productos');
		
		//grid de errores
		var $grid_warning = $('#forma-envproceso-window').find('#div_warning_grid').find('#grid_warning');
		
		
		var $cerrar_plugin = $('#forma-envproceso-window').find('#close');
		var $cancelar_plugin = $('#forma-envproceso-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-envproceso-window').find('#submit');
		
		$identificador.attr({'value' : 0});
		$producto_id.attr({'value' : 0});
		$unidad.css({'background' : '#F0F0F0'});
		
		
		//quitar enter a todos los campos input
		$('#forma-envproceso-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		
		//asignar el enfoque al cargar la ventana
		$codigo.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == 'true' ){
				var remove = function() {$(this).remove();};
				$('#forma-envproceso-overlay').fadeOut(remove);
				jAlert("Los datos del envasado se guardaron con &eacute;xito.", 'Atencion!');
				$get_datos_grid();
			}
			else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-envproceso-window').find('div.interrogacion').css({'display':'none'});
				$('#forma-envproceso-window').find('.envproceso_div_one').css({'height':'465px'});//sin errores
                                
				$grid_productos.find('select[name=select_aml_origen]').css({'background' : '#ffffff'});
				$grid_productos.find('select[name=select_pres_dest]').css({'background' : '#ffffff'});
				$grid_productos.find('input[name=cantpres]').css({'background' : '#ffffff'});
                                
				$('#forma-envproceso-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-envproceso-window').find('#div_warning_grid').find('#grid_warning').children().remove();
                                
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					
                                        tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-envproceso-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						var campo = tmp.split(':')[0];
						var $campo_input;
						var cantidad_existencia=0;
						var  width_td=0;
						
						if((tmp.split(':')[0].substring(0,8) == 'cantPres') || (tmp.split(':')[0].substring(0,7) == 'amlDest') || (tmp.split(':')[0].substring(0,8) == 'presDest') || (tmp.split(':')[0].substring(0,6) == 'amlEnv')){
							$('#forma-envproceso-window').find('#div_warning_grid').css({'display':'block'});
							$('#forma-envproceso-window').find('.envproceso_div_one').css({'height':'550px'});//con errores
							$campo_input = $grid_productos.find('.'+campo);
							$campo_input.css({'background' : '#d41000'});
							
							var almacen_destino = $campo_input.parent().parent().find('select[name=select_aml_dest]').find('option:selected').text();
							var presentacion_destino = $campo_input.parent().parent().find('select[name=select_pres_dest]').find('option:selected').text();
							
							var tr_warning = '<tr>';
							tr_warning += '<td width="20"><div><img src="../../img/icono_advertencia.png" align="top" rel="warning_sku"></td>';
							tr_warning += '<td width="150"><input type="text" value="' + almacen_destino + '" class="borde_oculto" readOnly="true" style="width:148px; color:red"></td>';
							tr_warning += '<td width="100"><input type="text" value="' + presentacion_destino + '" class="borde_oculto" readOnly="true" style="width:100px; color:red"></td>';
							tr_warning += '<td width="560"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:560px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-envproceso-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
					}
                                        
				}
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		
		$hora.attr({'value' : '00:00'});
		$hora.TimepickerInputMask();
		
		//fecha de envasado
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
					//var valida_fecha=mayor($fecha.val(),mostrarFecha());
					var valida_fecha=fecha_mayor_igual($fecha.val(),mostrarFecha());
					if (valida_fecha==false){
						jAlert("Fecha no valida",'! Atencion');
						$fecha.val(mostrarFecha());
					}else{
						$fecha.DatePickerHide();	
					}
				}
			}
		});
		
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEnvProceso.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			//carga select de Estatus
			$select_estatus.children().remove();
			var estatus_html = '<option value="0">[--Estatus--]</option>';
			$.each(arrayEstatus,function(entryIndex,est){
				if(parseInt(est['id']) == 1){
					estatus_html += '<option value="' + est['id'] + '" selected="yes" >' + est['titulo'] + '</option>';
				}else{
					estatus_html += '<option value="' + est['id'] + '"  >' + est['titulo'] + '</option>';
				}
			});
			$select_estatus.append(estatus_html);
			
			arrayAlmacenes = entry['Almacenes'];
			$activa_campos_por_estatus();
		});//termina llamada json
		
		/*Autocompletar para los campos Equipo y operador*/
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAutocompleteOperarios.json';
		$autocomplete_input($operador, input_json);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAutocompleteEquipo.json';
		$autocomplete_input($equipo, input_json);
		
		$buscar_orden.click(function(event){
			event.preventDefault();
			//tipo=1 para buscar el producto Principal
			var tipoBusqueda=1;
			$buscar_orden_prod(tipoBusqueda, $codigo.val(), $descripcion.val(), $folio.val() );
		});
		
		$(this).aplicarEventoKeypressEjecutaTrigger($descripcion, $buscar_orden);
		$(this).aplicarEventoKeypressEjecutaTrigger($folio, $buscar_orden);
		$(this).aplicarEventoKeypressEjecutaTrigger($codigo, $buscar_orden);
		$activa_campos_por_estatus();
		
		
		
		//validar campo, solo acepte numeros y punto
		$exis_uni.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		//validar campo, solo acepte numeros y punto
		$disp_uni.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	
		//validar campo, solo acepte numeros y punto
		$exis_pres.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		//validar campo, solo acepte numeros y punto
		$disp_pres.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		//validar campo, solo acepte numeros y punto
		$merma.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		//al perder enfoque
		$exis_uni.blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
				$(this).val( parseFloat($(this).val()).toFixed(decimales) );
			}
		});
		
		//al perder enfoque
		$disp_uni.blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
				$(this).val( parseFloat($(this).val()).toFixed(decimales) );
			}
		});
		
		//al perder enfoque
		$exis_pres.blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
				$(this).val( parseFloat($(this).val()).toFixed(decimales) );
			}
		});
		
		//al perder enfoque
		$disp_pres.blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
				$(this).val( parseFloat($(this).val()).toFixed(decimales) );
			}
		});
		
		//al perder enfoque
		$merma.blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
				$(this).val( parseFloat($(this).val()).toFixed(decimales) );
			}
		});
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert('Es necesario agregar productos en el listado.', 'Atencion!', function(r) { $codigo.focus(); });
				return false;
			}
		});
		
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-envproceso-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-envproceso-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
	});
	
	
	
	var carga_formaenvconf00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el envasado seleccionado?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El envasado fue eliminada exitosamente.", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El envasado no pudo ser eliminada.", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaenvproceso00';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_modalboxenvproceso();
			$('#forma-envproceso-window').css({"margin-left": -460, 	"margin-top": -270});
			
			$forma_selected.prependTo('#forma-envproceso-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			$tabs_li_funxionalidad();
                        
                        
                        
			var $identificador = $('#forma-envproceso-window').find('input[name=identificador]');
                
			var $folio = $('#forma-envproceso-window').find('input[name=folio]');
			var $fecha = $('#forma-envproceso-window').find('input[name=fecha]');
			var $hora = $('#forma-envproceso-window').find('input[name=hora]');
			var $producto_id = $('#forma-envproceso-window').find('input[name=producto_id]');
			var $codigo = $('#forma-envproceso-window').find('input[name=codigo]');
			var $descripcion = $('#forma-envproceso-window').find('input[name=descripcion]');
			var $produccion_id = $('#forma-envproceso-window').find('input[name=produccion_id]');
			var $folio_produccion = $('#forma-envproceso-window').find('input[name=folio_produccion]');
			var $exis_pres = $('#forma-envproceso-window').find('input[name=exis_pres]');
			var $disp_pres = $('#forma-envproceso-window').find('input[name=disp_pres]');
			var $unidad = $('#forma-envproceso-window').find('input[name=unidad]');
			var $exis_uni = $('#forma-envproceso-window').find('input[name=exis_uni]');
			var $disp_uni = $('#forma-envproceso-window').find('input[name=disp_uni]');
			var $equipo = $('#forma-envproceso-window').find('input[name=equipo]');
			var $operador = $('#forma-envproceso-window').find('input[name=operador]');
			var $merma = $('#forma-envproceso-window').find('input[name=merma]');

			var $select_presentacion_orig = $('#forma-envproceso-window').find('select[name=select_pres_orden_orig]');
			var $select_almacen_orig = $('#forma-envproceso-window').find('select[name=select_alm_orden_orig]');

			var $select_estatus = $('#forma-envproceso-window').find('select[name=select_estatus]');
			
			//href para Agregar y Buscar producto
			var $buscar_orden = $('#forma-envproceso-window').find('#buscar_orden');
			var $generarpdf = $('#forma-envproceso-window').find('#generarpdf');
			
			//grid de productos
			var $grid_productos = $('#forma-envproceso-window').find('#grid_productos');

			//grid de errores
			var $grid_warning = $('#forma-envproceso-window').find('#div_warning_grid').find('#grid_warning');
			var $cerrar_plugin = $('#forma-envproceso-window').find('#close');
			var $cancelar_plugin = $('#forma-envproceso-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-envproceso-window').find('#submit');
			
			
			$buscar_orden.hide();
			//$buscar_producto.hide();
			
			$unidad.css({'background' : '#F0F0F0'});
			$codigo.css({'background' : '#F0F0F0'});
			$descripcion.css({'background' : '#F0F0F0'});
			$codigo.attr('readonly',true);
			$descripcion.attr('readonly',true);
			
			//quitar enter a todos los campos input
			$('#forma-envconf-window').find('input').keypress(function(e){
				if(e.which==13 ) {
					return false;
				}
			});
			
			if(accion_mode == 'edit'){
                            
				//var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getConf.json';
				//$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
							var remove = function() {$(this).remove();};
							$('#forma-envproceso-overlay').fadeOut(remove);
							jAlert("Los datos del envasado se guardaron con &eacute;xito.", 'Atencion!');
							$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-envproceso-window').find('div.interrogacion').css({'display':'none'});
						$('#forma-envproceso-window').find('.envproceso_div_one').css({'height':'465px'});//sin errores

						$grid_productos.find('select[name=select_aml_origen]').css({'background' : '#ffffff'});
						$grid_productos.find('select[name=select_pres_dest]').css({'background' : '#ffffff'});
						$grid_productos.find('input[name=cantpres]').css({'background' : '#ffffff'});

						$('#forma-envproceso-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-envproceso-window').find('#div_warning_grid').find('#grid_warning').children().remove();

						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){

							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-envproceso-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

								var campo = tmp.split(':')[0];
								var $campo_input;
								var cantidad_existencia=0;
								var  width_td=0;

								if((tmp.split(':')[0].substring(0,8) == 'cantPres') || (tmp.split(':')[0].substring(0,7) == 'amlDest') || (tmp.split(':')[0].substring(0,8) == 'presDest') || (tmp.split(':')[0].substring(0,6) == 'amlEnv')){

									$('#forma-envproceso-window').find('#div_warning_grid').css({'display':'block'});
									$('#forma-envproceso-window').find('.envproceso_div_one').css({'height':'550px'});//con errores
									$campo_input = $grid_productos.find('.'+campo);
									$campo_input.css({'background' : '#d41000'});

									var almacen_destino = $campo_input.parent().parent().find('select[name=select_aml_dest]').find('option:selected').text();
									var presentacion_destino = $campo_input.parent().parent().find('select[name=select_pres_dest]').find('option:selected').text();

									var tr_warning = '<tr>';
										tr_warning += '<td width="20"><div><img src="../../img/icono_advertencia.png" align="top" rel="warning_sku"></td>';
										tr_warning += '<td width="150"><input type="text" value="' + almacen_destino + '" class="borde_oculto" readOnly="true" style="width:148px; color:red"></td>';
										tr_warning += '<td width="100"><input type="text" value="' + presentacion_destino + '" class="borde_oculto" readOnly="true" style="width:100px; color:red"></td>';
										tr_warning += '<td width="560"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:560px; color:red"></td>';
									tr_warning += '</tr>';

									$('#forma-envproceso-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								}
							}

						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
                                
				//fecha de envasado
				$fecha.click(function (s){
						var a=$('div.datepicker');
						a.css({'z-index':100});
				});
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
										//var valida_fecha=mayor($fecha.val(),mostrarFecha());
										var valida_fecha=fecha_mayor($fecha.val(),mostrarFecha());
										if (valida_fecha==true){
												jAlert("Fecha no valida",'! Atencion');
												$fecha.val(mostrarFecha());
										}else{
												$fecha.DatePickerHide();	
										}
								}
						}
				});
				*/
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEnvProceso.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.attr({'value' : entry['Datos'][0]['id']});
					$folio.attr({'value' : entry['Datos'][0]['folio']});
					$fecha.attr({'value' : entry['Datos'][0]['fecha']});
					$hora.attr({'value' : entry['Datos'][0]['hora']});
					$producto_id.attr({'value' : entry['Datos'][0]['inv_prod_id']});
					$codigo.attr({'value' : entry['Datos'][0]['sku']});
					$descripcion.attr({'value' : entry['Datos'][0]['descripcion']});
					$produccion_id.attr({'value' : entry['Datos'][0]['pro_orden_prod_id']});
					$folio_produccion.attr({'value' : entry['Datos'][0]['folio_op']});
					$exis_pres.attr({'value' : entry['Datos'][0]['cantidad']});
					$disp_pres.attr({'value' : entry['Datos'][0]['cantidad']});
					$unidad.attr({'value' : entry['Datos'][0]['unidad']});
					$exis_uni.attr({'value' : entry['Datos'][0]['cantidad']});
					$disp_uni.attr({'value' : entry['Datos'][0]['cantidad']});
					$equipo.attr({'value' : entry['Datos'][0]['equipo']});
					$operador.attr({'value' : entry['Datos'][0]['operador']});
					$merma.attr({'value' : entry['Datos'][0]['merma']});
					
					if(parseInt(entry['Datos'][0]['env_estatus_id'])==3){
						$submit_actualizar.hide();
					}
					
					//carga select de Estatus
					$select_estatus.children().remove();
					var estatus_html = '<option value="0">[--Estatus--]</option>';
					$.each(arrayEstatus,function(entryIndex,est){
						if(parseInt(est['id']) == parseInt(entry['Datos'][0]['env_estatus_id'])){
							estatus_html += '<option value="' + est['id'] + '" selected="yes" >' + est['titulo'] + '</option>';
						}else{
							estatus_html += '<option value="' + est['id'] + '"  >' + est['titulo'] + '</option>';
						}
					});
					$select_estatus.append(estatus_html);
					
					//carga select de Presentacion
					$select_presentacion_orig.children().remove();
					var presentacion_html = '<option value="0">[--Presentacion--]</option>';
					$.each(entry['Presentaciones'],function(entryIndex,pres){
						if(parseInt(pres['id']) == parseInt(entry['Datos'][0]['inv_prod_presentaciones_id'])){
							presentacion_html += '<option value="' + pres['id'] + '" selected="yes" >' + pres['titulo'] + '</option>';
						}else{
							presentacion_html += '<option value="' + pres['id'] + '"  >' + pres['titulo'] + '</option>';
						}
					});
					$select_presentacion_orig.append(presentacion_html);
					
					
					//carga select de Estatus
					$select_almacen_orig.children().remove();
					var alm_html = '<option value="0">[--Almacen--]</option>';
					$.each(entry['Almacenes'],function(entryIndex,alm){
						if(parseInt(alm['id']) == parseInt(entry['Datos'][0]['inv_alm_id'])){
							alm_html += '<option value="' + alm['id'] + '" selected="yes" >' + alm['titulo'] + '</option>';
						}else{
							alm_html += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
						}
					});
					$select_almacen_orig.append(alm_html);
					
					arrayAlmacenes = entry['Almacenes'];
					
					/*Obtiene las presentaciones de el producto*/
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesConfProducto.json';
					$arreglo = {'id_prod':entry['Datos'][0]['inv_prod_id'] };

					$.post(input_json,$arreglo,function(data){
						//carga select de Presentaciones
						if(data['PresentacionEnvases'] != null ){
							
							arrayPresentacionEnv = data['PresentacionEnvases'];
							
							$.each(entry['DatosDet'],function(entryIndex,element){
								id_presentacion = 0;
								equivalente = 0;
								
								$.each(arrayPresentacionEnv,function(entryIndex,pres){
									if(element['env_conf_id'] == pres['id_env']){
										id_presentacion = pres['id'];
										equivalente = pres['equivalencia'];
										cantuni = (parseFloat(element['cantidad']) * parseFloat(equivalente));
										//$add_trr_presentacion_enbasar();
										
										$add_trr_presentacion_enbasar(element['id'], element['inv_alm_id'], element['env_conf_id'],id_presentacion, element['cantidad'], entry['Datos'][0]['unidad'],cantuni,element['inv_alm_id_env'] , entry['Datos'][0]['env_estatus_id']);
										/*
										if(parseInt(entry['Datos'][0]['env_estatus_id'])==3){
											
										}
										*/
									}
								});
							});
							
							//Si hay configuracion de presentacion, agrega un elemento, de lo contrario arroja un mensaje de error.
							//$add_trr_presentacion_enbasar();
							
						}else{
							jAlert("No hay configurtaci&oacute;n para "+codigo_prod_buscador+"", 'Atencion!');
						}
						
					});//termina llamada json
					
					$hora.TimepickerInputMask();
					
				   /*Autocompletar para los campos Equipo y operador*/
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAutocompleteOperarios.json';
					$autocomplete_input($operador, input_json);
					
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAutocompleteEquipo.json';
					$autocomplete_input($equipo, input_json);
					
					$activa_campos_por_estatus();
					
					
					var iu =$('#lienzo_recalculable').find('input[name=iu]').val();
					$generarpdf.click(function(event){
						var cadena = $identificador.val();
						var input_json =document.location.protocol + '//' + document.location.host + '/'+controller+'/getReportEnvasado/'+cadena+'/'+iu+'/out.json';
						window.location.href=input_json;
					});
                                        
				},"json");//termina llamada json
				
				
				
				//validar campo, solo acepte numeros y punto
				$exis_uni.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//validar campo, solo acepte numeros y punto
				$disp_uni.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});

				//validar campo, solo acepte numeros y punto
				$exis_pres.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//validar campo, solo acepte numeros y punto
				$disp_pres.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//validar campo, solo acepte numeros y punto
				$merma.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				
				//al perder enfoque
				$exis_uni.blur(function(){
					if($(this).val().trim()==''){
						$(this).val(0);
						$(this).val( parseFloat($(this).val()).toFixed(decimales) );
					}
				});
				
				//al perder enfoque
				$disp_uni.blur(function(){
					if($(this).val().trim()==''){
						$(this).val(0);
						$(this).val( parseFloat($(this).val()).toFixed(decimales) );
					}
				});
				
				//al perder enfoque
				$exis_pres.blur(function(){
					if($(this).val().trim()==''){
						$(this).val(0);
						$(this).val( parseFloat($(this).val()).toFixed(decimales) );
					}
				});
				
				//al perder enfoque
				$disp_pres.blur(function(){
					if($(this).val().trim()==''){
						$(this).val(0);
						$(this).val( parseFloat($(this).val()).toFixed(decimales) );
					}
				});
				
				//al perder enfoque
				$merma.blur(function(){
					if($(this).val().trim()==''){
						$(this).val(0);
						$(this).val( parseFloat($(this).val()).toFixed(decimales) );
					}
				});
				
				
				
				//aplicar evento click para que al pulsar Enter sobre el campo Descripcion de la busqueda del producto componente, se ejecute el buscador
				//$(this).aplicarEventoKeypressEjecutaTrigger($nombre_componente, $buscar_producto_componente);
				
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-envproceso-overlay').fadeOut(remove);
				});
				
				$cancelar_plugin.click(function(event){
					var remove = function() {$(this).remove();};
					$('#forma-envproceso-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				$activa_campos_por_estatus();
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllEnvProceso.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllEnvProceso.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaenvconf00_for_datagrid00);
            
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



