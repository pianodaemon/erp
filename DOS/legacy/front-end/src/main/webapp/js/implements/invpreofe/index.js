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
    var controller = $contextpath.val()+"/controllers/invpreofe";
    
    var criteriooferta = {1:"Indistinto", 2:"Precio Venta Mayor a Oferta"};
    
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_invprodlineas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Promociones');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	//var $busqueda_descripcion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_descripcion]');
	//var $busqueda_select_grupo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_grupo]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "producto" + signo_separador + $busqueda_producto.val() + "|";
		//valor_retorno += "descripcion" + signo_separador + $busqueda_descripcion.val() + "|";
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
		$busqueda_titulo.val('');
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
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-invpreofe-window').find('#submit').mouseover(function(){
			$('#forma-invpreofe-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-invpreofe-window').find('#submit').mouseout(function(){
			$('#forma-invpreofe-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-invpreofe-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invpreofe-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invpreofe-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invpreofe-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invpreofe-window').find('#close').mouseover(function(){
			$('#forma-invpreofe-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invpreofe-window').find('#close').mouseout(function(){
			$('#forma-invpreofe-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-invpreofe-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invpreofe-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invpreofe-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invpreofe-window').find("ul.pestanas li").click(function() {
			$('#forma-invpreofe-window').find(".contenidoPes").hide();
			$('#forma-invpreofe-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invpreofe-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
                
                
                
                /*codigo para las pestañas internas*/
               /*
               $('#forma-invpre-window').find(".contenidoPes_internas").hide(); //Hide all content
		$('#forma-invpre-window').find("ul.pestanas_internas li:first").addClass("active_internas").show(); //Activate first tab
		$('#forma-invpre-window').find(".contenidoPes_internas:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invpre-window').find("ul.pestanas_internas li").click(function() {
			$('#forma-invpre-window').find(".contenidoPes_internas").hide();
			$('#forma-invpre-window').find("ul.pestanas_internas li").removeClass("active_internas");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invpre-window').find( activeTab , "ul.pestanas_internas li" ).fadeIn().show();
			$(this).addClass("active_internas");
			return false;
		});
                */
	}
	
	    //buscador de producto ingrediente
        $busca_productos = function(){
            $(this).modalPanel_Buscaproducto();
            var $dialogoc =  $('#forma-buscaproducto-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
           
            $('#forma-buscaproducto-window').css({"margin-left": -190,     "margin-top": -160});
           
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
                var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
                $.each(data['prodTipos'],function(entryIndex,pt){
                    prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                });
                $select_tipo_producto.append(prod_tipos_html);
            });
           
           
            //Aqui asigno al campo sku del buscador si el usuario ingres√≥ un sku antes de hacer clic en buscar en la ventana principal
            //$campo_sku.val(sku_buscar);
           
            //click buscar productos
            $buscar_plugin_producto.click(function(event){
                //event.preventDefault();
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos.json';
                $arreglo = {//'almacen':$select_almacen.val(),
                        'sku':$campo_sku.val(),
                        'tipo':$select_tipo_producto.val(),
                        'descripcion':$campo_descripcion.val(),
                        'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                    }
                var trr = '';
                $tabla_resultados.children().remove();
                $.post(input_json,$arreglo,function(entry){
                    $.each(entry['Productos'],function(entryIndex,producto){
                        trr = '<tr>';
                            trr += '<td width="120">';
                                trr += '<span class="id_prod_buscador">'+producto['id']+'</span>';
                                trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
                            trr +=  '</td>';
                            trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
                            trr += '<td width="90"><span class="unidad_prod_buscador">'+producto['unidad']+'</span></td>';
                            trr += '<td width="90"><span class="tipo_prod_buscador">'+producto['tipo']+'</span></td>';
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
                        $('#forma-invpreofe-window').find('input[name=producto_id]').val($(this).find('span.id_prod_buscador').html());
                        $('#forma-invpreofe-window').find('input[name=productosku]').val($(this).find('span.sku_prod_buscador').html());
                        $('#forma-invpreofe-window').find('input[name=producto_descripcion]').val($(this).find('span.titulo_prod_buscador').html());
                        //elimina la ventana de busqueda
                        var remove = function() {$(this).remove();};
                        $('#forma-buscaproducto-overlay').fadeOut(remove);
                           
                       
                    });
                   
                });//termina llamada json
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
           
    }
        
        
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
	//----------------------------------------------------------------
        
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
            $campo.val("0.00");
        }
        
        $accio_blur = function($campo){
            //recalcula importe al perder enfoque el campo costo
            $campo.blur(function(){
                
                $valor_tmp = $(this).val().split(",").join("");
                
                if ($valor_tmp == ''  || $valor_tmp == null){
                        $(this).val('0.00');
                }
                
                if( ($valor_tmp != '') && ($valor_tmp != ' ') )
                {
                    $campo.val($(this).agregar_comas(parseFloat($valor_tmp).toFixed(2)));
                }else{
                        $(this).val('0.00');
                }
                
            });
        }
        
        
	//nuevo centro de costo
	$new_invprodlineas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_modalboxInvPreOfe();
		
		var form_to_show = 'formaInvPreOfe00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-invpreofe-window').css({"margin-left": -330, 	"margin-top": -210});
		$forma_selected.prependTo('#forma-invpreofe-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
                var $campo_id = $('#forma-invpreofe-window').find('input[name=identificador]');
                var $producto_id = $('#forma-invpreofe-window').find('input[name=producto_id]');
                var $productosku = $('#forma-invpreofe-window').find('input[name=productosku]');
                var $producto_descripcion = $('#forma-invpreofe-window').find('input[name=producto_descripcion]');
                
                
                var $consecutivo = $('#forma-invpreofe-window').find('select[name=consecutivo]');
                var $fecha_inicial = $('#forma-invpreofe-window').find('input[name=fecha_inicial]');
                var $fecha_final = $('#forma-invpreofe-window').find('input[name=fecha_final]');
                var $pordescuento = $('#forma-invpreofe-window').find('input[name=pordescuento]');
                var $porprecio = $('#forma-invpreofe-window').find('input[name=porprecio]');
                var $criterio = $('#forma-invpreofe-window').find('select[name=criterio]');
                
                var $popreciocheck = $('#forma-invpreofe-window').find('input[name=popreciocheck]');
                var $pordescuentocheck = $('#forma-invpreofe-window').find('input[name=pordescuentocheck]');
                
                var $lista_1 = $('#forma-invpreofe-window').find('input[name=lista_1]');
                var $lista_2 = $('#forma-invpreofe-window').find('input[name=lista_2]');
                var $lista_3 = $('#forma-invpreofe-window').find('input[name=lista_3]');
                var $lista_4 = $('#forma-invpreofe-window').find('input[name=lista_4]');
                var $lista_5 = $('#forma-invpreofe-window').find('input[name=lista_5]');
                var $lista_6 = $('#forma-invpreofe-window').find('input[name=lista_6]');
                var $lista_7 = $('#forma-invpreofe-window').find('input[name=lista_7]');
                var $lista_8 = $('#forma-invpreofe-window').find('input[name=lista_8]');
                var $lista_9 = $('#forma-invpreofe-window').find('input[name=lista_9]');
                var $lista_10 = $('#forma-invpreofe-window').find('input[name=lista_10]');
                
                
                
                //href para buscar producto
		var $buscar_producto = $('#forma-invpreofe-window').find('a[href*=busca_producto]');
                
                
		var $cerrar_plugin = $('#forma-invpreofe-window').find('#close');
		var $cancelar_plugin = $('#forma-invpreofe-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invpreofe-window').find('#submit');
		
		$campo_id.attr({'value' : 0});
                $producto_id.attr({'value' : 0});
                
		var respuestaProcesada = function(data){
                    if ( data['success'] == 'true' ){
                        var remove = function() {$(this).remove();};
                        $('#forma-invpreofe-overlay').fadeOut(remove);
                        jAlert("La promocion se ha actualizado.", 'Atencion!');
                        $get_datos_grid();
                    }
                    else{
                        // Desaparece todas las interrogaciones si es que existen
                        $('#forma-invpreofe-window').find('div.interrogacion').css({'display':'none'});

                        var valor = data['success'].split('___');
                        //muestra las interrogaciones
                        for (var element in valor){
                            tmp = data['success'].split('___')[element];
                            longitud = tmp.split(':');
                            if( longitud.length > 1 ){
                                    $('#forma-invpreofe-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                    .parent()
                                    .css({'display':'block'})
                                    .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
                            }
                        }
                    }
                }
                
                var options = {dataType :  'json', success : respuestaProcesada};
                $forma_selected.ajaxForm(options);
                
                $popreciocheck.click(function(event){
                    //alert($(this).is(':checked'));
                    if($popreciocheck.is(":checked") == true){
                        $popreciocheck.removeAttr('checked');
                        $pordescuentocheck.removeAttr('checked');
                        $porprecio.attr("readonly", true);
                        $pordescuento.attr("readonly", false);
                        $porprecio.val(0);
                        $pordescuento.val(0);
                        $popreciocheck.attr('checked', true);
                        $pordescuentocheck.attr('checked', false);
                    }else{
                        $popreciocheck.removeAttr('checked');
                        $pordescuentocheck.removeAttr('checked');
                        $porprecio.attr("readonly", true);
                        $pordescuento.attr("readonly", false);
                        $porprecio.val(0);
                        $pordescuento.val(0);
                        $popreciocheck.attr('checked', false);
                        $pordescuentocheck.attr('checked', true);
                    }
                });
                
                $pordescuentocheck.click(function(event){
                    //alert($(this).is(':checked'));
                    if($pordescuentocheck.is(":checked")  == true ){
                        $popreciocheck.removeAttr('checked');
                        $pordescuentocheck.removeAttr('checked');
                        $porprecio.attr("readonly", true);
                        $pordescuento.attr("readonly", false);
                        $porprecio.val(0);
                        $pordescuento.val(0);
                        $popreciocheck.attr('checked', false);
                        $pordescuentocheck.attr('checked', true);
                    }else{
                        $popreciocheck.removeAttr('checked');
                        $pordescuentocheck.removeAttr('checked');
                        $porprecio.attr("readonly", true);
                        $pordescuento.attr("readonly", false);
                        $porprecio.val(0);
                        $pordescuento.val(0);
                        $popreciocheck.attr('checked', true);
                        $pordescuentocheck.attr('checked', false);
                    }
                });
                
                $buscar_producto.click(function(event){
                    event.preventDefault();
                    $busca_productos();
                });
                
                $criterio.children().remove();
                var nivel_hmtl = '';
                
                nivel_hmtl += '<option value="true"  selected = "yes">Indistinto</option>';
                nivel_hmtl += '<option value="false" >Precio Venta Mayor a Oferta</option>';
                    
                $criterio.append(nivel_hmtl);
                
                //muestra fecha en input
		$fecha_inicial.val(mostrarFecha());
		$fecha_inicial.click(function (s){
                    var a=$('div.datepicker');
                    a.css({'z-index':100});
		});
		
		//seleccionar fecha
		$fecha_inicial.DatePicker({
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
				$fecha_inicial.val(formated);
				
				if (formated.match(patron) ){
					//var valida_fecha=mayor($fecha_inicial.val(),mostrarFecha());
					var valida_fecha = false;
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_inicial.val(mostrarFecha());
					}else{
						$fecha_inicial.DatePickerHide();	
					}
				}
			}
		});
                
                
                //muestra fecha en input
		$fecha_final.val(mostrarFecha());
		$fecha_final.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		//seleccionar fecha
		$fecha_final.DatePicker({
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
				$fecha_final.val(formated);
				
				if (formated.match(patron) ){
					//var valida_fecha=mayor($fecha_final.val(),mostrarFecha());
					var valida_fecha = false;
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_final.val(mostrarFecha());
					}else{
						$fecha_final.DatePickerHide();	
					}
				}
			}
		});
                
                $permitir_solo_numeros($porprecio);
                $permitir_solo_numeros($pordescuento);
                $accio_blur($porprecio);
                $accio_blur($pordescuento);
                
		/*
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvPre.json';
		$arreglo = {'id':id_to_show};
		
		$.post(input_json,$arreglo,function(entry){
			
                        //Alimentando los campos select de secciones
			$seccion.children().remove();
			var seccion_hmtl = '<option value="0" selected="yes">[--Seleccionar una seccion--]</option>';
			$.each(entry['Secciones'],function(entryIndex,seccion){
				seccion_hmtl += '<option value="' + seccion['id'] + '"  >' + seccion['titulo'] + '</option>';
			});
			$seccion.append(seccion_hmtl);
                        
                        //Alimentando los campos select de Marcas
			$marcasdisponibles.children().remove();
			var marcasdisponibles = '';
			$.each(entry['Marcas'],function(entryIndex,marca){
				marcasdisponibles += '<option value="' + marca['id'] + '"  >' + marca['titulo'] + '</option>';
			});
			$marcasdisponibles.append(marcasdisponibles);
                        
		});//termina llamada json
		
                $agregar_remover_marcas();
                */
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-invpreofe-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-invpreofe-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
        });
	
        
	
	var carga_formaInvPreOfe00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la promocion', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La promocion fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La promocion no puden ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
                        
			//aqui  entra para editar un registro
			var form_to_show = 'formaInvPreOfe00';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
                        $(this).modalPanel_modalboxInvPreOfe();
                        
                        
			$('#forma-invpreofe-window').css({"margin-left": -330, 	"margin-top": -210});
                        $forma_selected.prependTo('#forma-invpreofe-window');
                        $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
                        $tabs_li_funxionalidad();
			
                        var $campo_id = $('#forma-invpreofe-window').find('input[name=identificador]');
                        var $producto_id = $('#forma-invpreofe-window').find('input[name=producto_id]');
                        var $productosku = $('#forma-invpreofe-window').find('input[name=productosku]');
                        var $producto_descripcion = $('#forma-invpreofe-window').find('input[name=producto_descripcion]');


                        var $consecutivo = $('#forma-invpreofe-window').find('select[name=consecutivo]');
                        var $fecha_inicial = $('#forma-invpreofe-window').find('input[name=fecha_inicial]');
                        var $fecha_final = $('#forma-invpreofe-window').find('input[name=fecha_final]');
                        var $pordescuento = $('#forma-invpreofe-window').find('input[name=pordescuento]');
                        var $porprecio = $('#forma-invpreofe-window').find('input[name=porprecio]');
                        var $criterio = $('#forma-invpreofe-window').find('select[name=criterio]');

                        var $popreciocheck = $('#forma-invpreofe-window').find('input[name=popreciocheck]');
                        var $pordescuentocheck = $('#forma-invpreofe-window').find('input[name=pordescuentocheck]');

                        var $lista_1 = $('#forma-invpreofe-window').find('input[name=lista_1]');
                        var $lista_2 = $('#forma-invpreofe-window').find('input[name=lista_2]');
                        var $lista_3 = $('#forma-invpreofe-window').find('input[name=lista_3]');
                        var $lista_4 = $('#forma-invpreofe-window').find('input[name=lista_4]');
                        var $lista_5 = $('#forma-invpreofe-window').find('input[name=lista_5]');
                        var $lista_6 = $('#forma-invpreofe-window').find('input[name=lista_6]');
                        var $lista_7 = $('#forma-invpreofe-window').find('input[name=lista_7]');
                        var $lista_8 = $('#forma-invpreofe-window').find('input[name=lista_8]');
                        var $lista_9 = $('#forma-invpreofe-window').find('input[name=lista_9]');
                        var $lista_10 = $('#forma-invpreofe-window').find('input[name=lista_10]');
                        
                        //href para buscar producto
                        var $buscar_producto = $('#forma-invpreofe-window').find('a[href*=busca_producto]');
                        
			var $cerrar_plugin = $('#forma-invpreofe-window').find('#close');
			var $cancelar_plugin = $('#forma-invpreofe-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invpreofe-window').find('#submit');
			
                        $buscar_producto.hide();
                        
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvPreOfe.json';
				$arreglo = {'id':id_to_show};
				
				var respuestaProcesada = function(data){
                                    if ( data['success'] == 'true' ){
                                        var remove = function() {$(this).remove();};
                                        $('#forma-invpreofe-overlay').fadeOut(remove);
                                        jAlert("La promocion para este producto se han actualizado.", 'Atencion!');
                                        $get_datos_grid();
                                    }else{
                                        // Desaparece todas las interrogaciones si es que existen
                                        $('#forma-invpreofe-window').find('div.interrogacion').css({'display':'none'});
                                        
                                        var valor = data['success'].split('___');
                                        //muestra las interrogaciones
                                        for (var element in valor){
                                            tmp = data['success'].split('___')[element];
                                            longitud = tmp.split(':');
                                            if( longitud.length > 1 ){
                                                    $('#forma-invpreofe-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
                                    $campo_id.attr({'value' : entry['InvPreOfe']['0']['id']});
                                    $producto_id.attr({'value' : entry['InvPreOfe']['0']['inv_prod_id']});
                                    $productosku.attr({'value' : entry['InvPreOfe']['0']['sku']});
                                    $producto_descripcion.attr({'value' : entry['InvPreOfe']['0']['titulo']});
                                    
                                    $fecha_inicial.attr({'value' : entry['InvPreOfe']['0']['fecha_inicial']});
                                    $fecha_final.attr({'value' : entry['InvPreOfe']['0']['fecha_final']});
                                    
                                    if(entry['InvPreOfe']['0']['tipo_descto_precio'] == "true"){
                                        $popreciocheck.attr('checked', false);
                                        $pordescuentocheck.attr('checked', true);
                                        $pordescuento.attr({'value' : entry['InvPreOfe']['0']['precio_oferta']});
                                        $porprecio.attr({'value' : "0"});
                                    }else{
                                        $popreciocheck.attr('checked', true);
                                        $pordescuentocheck.attr('checked', false);
                                        $pordescuento.attr({'value' : "0"});
                                        $porprecio.attr({'value' : entry['InvPreOfe']['0']['precio_oferta']});
                                    }
                                    
                                    
                                    
                                    
                                    if(entry['InvPreOfe']['0']['precio_lista_1'] == "true"){
                                        $lista_1.attr('checked', true);
                                    }else{
                                        $lista_1.attr('checked', false); 
                                    }
                                    if(entry['InvPreOfe']['0']['precio_lista_2'] == "true"){
                                        $lista_2.attr('checked', true);
                                    }else{
                                        $lista_2.attr('checked', false); 
                                    }
                                    if(entry['InvPreOfe']['0']['precio_lista_3'] == "true"){
                                        $lista_3.attr('checked', true);
                                    }else{
                                        $lista_3.attr('checked', false); 
                                    }
                                    if(entry['InvPreOfe']['0']['precio_lista_4'] == "true"){
                                        $lista_4.attr('checked', true);
                                    }else{
                                        $lista_4.attr('checked', false); 
                                    }
                                    if(entry['InvPreOfe']['0']['precio_lista_5'] == "true"){
                                        $lista_5.attr('checked', true);
                                    }else{
                                        $lista_5.attr('checked', false); 
                                    }
                                    if(entry['InvPreOfe']['0']['precio_lista_6'] == "true"){
                                        $lista_6.attr('checked', true);
                                    }else{
                                        $lista_6.attr('checked', false); 
                                    }
                                    if(entry['InvPreOfe']['0']['precio_lista_7'] == "true"){
                                        $lista_7.attr('checked', true);
                                    }else{
                                        $lista_7.attr('checked', false); 
                                    }
                                    if(entry['InvPreOfe']['0']['precio_lista_8'] == "true"){
                                        $lista_8.attr('checked', true);
                                    }else{
                                        $lista_8.attr('checked', false); 
                                    }
                                    if(entry['InvPreOfe']['0']['precio_lista_9'] == "true"){
                                        $lista_9.attr('checked', true);
                                    }else{
                                        $lista_9.attr('checked', false); 
                                    }
                                    if(entry['InvPreOfe']['0']['precio_lista_10'] == "true"){
                                        $lista_10.attr('checked', true);
                                    }else{
                                        $lista_10.attr('checked', false); 
                                    }
                                    
                                    $criterio.children().remove();
                                    var nivel_hmtl = '';
                                    if(entry['InvPreOfe']['0']['criterio_oferta'] == "true"){
                                        nivel_hmtl += '<option value="true"  selected = "yes">Indistinto</option>';
                                        nivel_hmtl += '<option value="false" >Precio Venta Mayor a Oferta</option>';
                                    }else{
                                        nivel_hmtl += '<option value="true"  >Indistinto</option>';
                                        nivel_hmtl += '<option value="false" selected = "yes">Precio Venta Mayor a Oferta</option>';
                                    }
                                    
                                    $criterio.append(nivel_hmtl);
                                    
                                    $permitir_solo_numeros($porprecio);
                                    $permitir_solo_numeros($pordescuento);
                                    $accio_blur($porprecio);
                                    $accio_blur($pordescuento);
                                    
				},"json");//termina llamada json
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invpreofe-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invpreofe-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllInvPreOfe.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllInvPreOfe.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaInvPreOfe00_for_datagrid00);
                
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



