$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	var arrayAgentes;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/clientsantcancel";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseover(function(){
		$(this).removeClass("onmouseOutNewItem").addClass("onmouseOverNewItem");
	});
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseout(function(){
		$(this).removeClass("onmouseOverNewItem").addClass("onmouseOutNewItem");
	});
	
	
	$('#barra_acciones').find('.table_acciones').find('#genInforme').mouseover(function(){
		$(this).removeClass("onmouseOutGeneraInforme").addClass("onmouseOverGeneraInforme");
	});
	$('#barra_acciones').find('.table_acciones').find('#genInforme').mouseout(function(){
		$(this).removeClass("onmouseOverGeneraInforme").addClass("onmouseOutGeneraInforme");
	});
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Anticipos de Clientes');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_num_transaccion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_num_transaccion]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	$buscar.mouseover(function(){
		$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
	});
	$buscar.mouseout(function(){
		$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
	});
	   
	$limpiar.mouseover(function(){
		$(this).removeClass("onmouseOutLimpiar").addClass("onmouseOverLimpiar");
	});
	$limpiar.mouseout(function(){
		$(this).removeClass("onmouseOverLimpiar").addClass("onmouseOutLimpiar");
	});
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "num_transaccion" + signo_separador + $busqueda_num_transaccion.val() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
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
		$busqueda_num_transaccion.val('');
		$busqueda_cliente.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_num_transaccion.focus();
		
		$get_datos_grid();
	});    
	
      
    TriggerClickVisializaBuscador = 0;
    
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		$('#barra_genera_informe').find('.tabla_genera_informe').css({'display':'none'});
		
		var alto=0;
		if(TriggerClickVisializaBuscador==0){
			 TriggerClickVisializaBuscador=1;
			 var height2 = $('#cuerpo').css('height');
			 //alert('height2: '+height2);
			 
			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 //alert('pix_alto: '+pix_alto);
			 
			 $('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
			 $('#barra_buscador').animate({height: '80px'}, 500);
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
		
		//asignar el enfoque al visualizar Buscador
		$busqueda_num_transaccion.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_num_transaccion, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	
	
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
		$('#forma-clientsantcancel-window').find('#submit').mouseover(function(){
			$('#forma-clientsantcancel-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-clientsantcancel-window').find('#submit').mouseout(function(){
			$('#forma-clientsantcancel-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-clientsantcancel-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-clientsantcancel-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-clientsantcancel-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-clientsantcancel-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-clientsantcancel-window').find('#close').mouseover(function(){
			$('#forma-clientsantcancel-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-clientsantcancel-window').find('#close').mouseout(function(){
			$('#forma-clientsantcancel-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-clientsantcancel-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-clientsantcancel-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-clientsantcancel-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-clientsantcancel-window').find("ul.pestanas li").click(function() {
			$('#forma-clientsantcancel-window').find(".contenidoPes").hide();
			$('#forma-clientsantcancel-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-clientsantcancel-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	
	var $agregarDatosClienteSeleccionado = function(idCliente, noControl, razonSocial){
		$('#forma-clientsantcancel-window').find('input[name=id_cliente]').val(idCliente);
		$('#forma-clientsantcancel-window').find('input[name=no_cliente]').val(noControl);
		$('#forma-clientsantcancel-window').find('input[name=razoncliente]').val(razonSocial);
		
		$('#forma-clientsantcancel-window').find('input[name=cliente]').focus();
	}
	
	
	
	//buscador de clientes
	$busca_clientes = function(numero_control, razon_social_cliente){
            $(this).modalPanel_Buscacliente();
            var $dialogoc =  $('#forma-buscacliente-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
            $('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -180});
			
            var $tabla_resultados = $('#forma-buscacliente-window').find('#tabla_resultado');
			
			var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('#busca_cliente_modalbox');
			var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('#cencela');
			
            var $cadena_buscar = $('#forma-buscacliente-window').find('input[name=cadena_buscar]');
            var $select_filtro_por = $('#forma-buscacliente-window').find('select[name=filtropor]');
            
            //funcionalidad botones
			$busca_cliente_modalbox.mouseover(function(){
				$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
			});
			$busca_cliente_modalbox.mouseout(function(){
				$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
			});
			   
			$cancelar_plugin_busca_cliente.mouseover(function(){
				$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
			});
			$cancelar_plugin_busca_cliente.mouseout(function(){
				$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
			});
            
            var html = '';
			$select_filtro_por.children().remove();
			html='<option value="0">[-- Opcion busqueda --]</option>';
			
			if(numero_control != ''){
				//asignamos el numero de control al campo de busqueda
				$cadena_buscar.val(numero_control);
				if(razon_social_cliente == ''){
					html+='<option value="1" selected="yes">No. de control</option>';
				}else{
					html+='<option value="1">No. de control</option>';
				}
			}else{
				html+='<option value="1">No. de control</option>';
			}
			html+='<option value="2">RFC</option>';
			if(razon_social_cliente != ''){
				//asignamos la Razon Social del Cliente al campo Nombre
				$cadena_buscar.val(razon_social_cliente);
				html+='<option value="3" selected="yes">Razon social</option>';
			}else{
				if(razon_social_cliente == '' && numero_control == ''){
					html+='<option value="3" selected="yes">Razon social</option>';
				}else{
					html+='<option value="3">Razon social</option>';
				}
			}
			html+='<option value="4">CURP</option>';
			html+='<option value="5">Alias</option>';
			$select_filtro_por.append(html);
            
            $cadena_buscar.select();
            $cadena_buscar.focus();
            
            //click buscar clientes
            $busca_cliente_modalbox.click(function(event){
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_clientes.json';
                $arreglo = {'cadena':$cadena_buscar.val(),
                             'filtro':$select_filtro_por.val(),
                             'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                            }
                            
                var trr = '';
                $tabla_resultados.children().remove();
                $.post(input_json,$arreglo,function(entry){
                    $.each(entry['Clientes'],function(entryIndex,cliente){
                        trr = '<tr>';
                            trr += '<td width="80">';
                                trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
                                trr += '<input type="hidden" id="direccion" value="'+cliente['direccion']+'">';
                                trr += '<input type="hidden" id="id_moneda" value="'+cliente['moneda_id']+'">';
                                trr += '<input type="hidden" id="moneda" value="'+cliente['moneda']+'">';
                                trr += '<span class="no_control">'+cliente['numero_control']+'</span>';
                            trr += '</td>';
                            trr += '<td width="145"><span class="rfc">'+cliente['rfc']+'</span></td>';
                            trr += '<td width="375"><span class="razon">'+cliente['razon_social']+'</span></td>';
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
						var idCliente = $(this).find('#idclient').val();
						var rfc = $(this).find('span.rfc').html();
						var noControl = $(this).find('span.no_control').html();
						var razonSocial = $(this).find('span.razon').html();
						
						//llamada a la funcion para agregar los datos del cliente
						$agregarDatosClienteSeleccionado(idCliente, noControl, razonSocial);
							
                        //elimina la ventana de busqueda
                        var remove = function() {$(this).remove();};
                        $('#forma-buscacliente-overlay').fadeOut(remove);
                        //asignar el enfoque al campo sku del producto
                    });
                });
            });//termina llamada json
			
			
			//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
			if($cadena_buscar.val() != ''){
				$busca_cliente_modalbox.trigger('click');
			}
			
			$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $busca_cliente_modalbox);
			$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $busca_cliente_modalbox);
			
            $cancelar_plugin_busca_cliente.click(function(event){
                var remove = function() {$(this).remove();};
                $('#forma-buscacliente-overlay').fadeOut(remove);
                $('#forma-clientsantcancel-window').find('input[name=cliente]').focus();
            });
	}//termina buscador de clientes


	
	//nuevo registro
	$new.click(function(event){
		
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_clientsantcancel();
		
		var form_to_show = 'formClientsAntCancel';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
					
		$('#forma-clientsantcancel-window').css({"margin-left": -420, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-clientsantcancel-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $identificador = $('#forma-clientsantcancel-window').find('input[name=identificador]');
		var $accion = $('#forma-clientsantcancel-window').find('input[name=accion]');
		var $no_transaccion = $('#forma-clientsantcancel-window').find('input[name=no_transaccion]');
		var $fecha = $('#forma-clientsantcancel-window').find('input[name=fecha]');
		var $id_cliente = $('#forma-clientsantcancel-window').find('input[name=id_cliente]');
		var $no_cliente = $('#forma-clientsantcancel-window').find('input[name=no_cliente]');
		var $razoncliente = $('#forma-clientsantcancel-window').find('input[name=razoncliente]');
		var $monto = $('#forma-clientsantcancel-window').find('input[name=monto]');
		var $select_moneda = $('#forma-clientsantcancel-window').find('select[name=select_moneda]');
		var $observaciones = $('#forma-clientsantcancel-window').find('textarea[name=observaciones]');
		
		var $busca_cliente = $('#forma-clientsantcancel-window').find('#busca_cliente');
		
		var $cancelar = $('#forma-clientsantcancel-window').find('#cancelar');
		var $salir = $('#forma-clientsantcancel-window').find('#salir');
		
		//botones                        
		var $cerrar_plugin = $('#forma-clientsantcancel-window').find('#close');
		
		//quitar enter a todos los campos input
		$('#forma-clientsantcancel-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		
		$no_transaccion.attr("readonly", true);
		$fecha.attr("readonly", true);
		//$no_cliente.attr("readonly", true);
		//$razoncliente.attr("readonly", true);
		//$monto.attr("readonly", true);
		//$observaciones.attr("readonly", true);
		
		$no_transaccion.css({'background':'#F0F0F0'});
		//$fecha.css({'background':'#F0F0F0'});
		//$no_cliente.css({'background':'#F0F0F0'});
		//$razoncliente.css({'background':'#F0F0F0'});
		//$monto.css({'background':'#F0F0F0'});
		
		$identificador.val(0);
		$id_cliente.val(0);
		$monto.val('0.00');
		$accion.val("new");
		$cancelar.val("Guardar");
		var respuestaProcesada = function(data){
			if ( data['valor'] == "true" ){
				jAlert(data['msj'], 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-clientsantcancel-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-clientsantcancel-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-clientsantcancel-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAnticipo.json';
		$arreglo = {'identificador':$identificador.val()}
		
		$.post(input_json,$arreglo,function(entry){
			$select_moneda.children().remove();
			var mon_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,mon){
				mon_hmtl += '<option value="' + mon['id'] + '"  >' + mon['descripcion'] + '</option>';
			});
			$select_moneda.append(mon_hmtl);
		});
		
		
		
		//buscador de clientes
		$busca_cliente.click(function(event){
			event.preventDefault();
			//1 para que retorne datos en buscador para realizar pagos
			$busca_clientes($no_cliente.val(), $razoncliente.val());
		});
		
		//asignar evento keypress al campo Razon Social del cliente
		$(this).aplicarEventoKeypressEjecutaTrigger($razoncliente, $busca_cliente);
	
		$no_cliente.keypress(function(e){
			if(e.which == 13){
				var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoClient.json';
				$arreglo2 = {'no_control':$no_cliente.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				$.post(input_json2,$arreglo2,function(entry2){
					
					if(parseInt(entry2['Cliente'].length) > 0 ){
						var idCliente = entry2['Cliente'][0]['id'];
						var noControl = entry2['Cliente'][0]['numero_control'];
						var razonSocial = entry2['Cliente'][0]['razon_social'];
						
						//llamada a la funcion para agregar los datos del cliente
						$agregarDatosClienteSeleccionado(idCliente, noControl, razonSocial);
						
					}else{
						$('#forma-clientsantcancel-window').find('input[name=id_cliente]').val(0);
						$('#forma-clientsantcancel-window').find('input[name=no_cliente]').val('');
						$('#forma-clientsantcancel-window').find('input[name=razoncliente]').val('');
						
						jAlert('N&uacute;mero de cliente desconocido.', 'Atencion!', function(r) { 
							$('#forma-clientsantcancel-window').find('input[name=no_cliente]').focus(); 
						});
					}
				},"json");//termina llamada json
				
				return false;
			}
		});
		
		
		$fecha.val(mostrarFecha());
		$fecha.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100,});
		});
		
		$fecha.DatePicker({
			format:'Y-m-d',
			onBeforeShow: function(){
				$fecha.DatePickerSetDate($fecha.val(), true);
			},
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
					$fecha.DatePickerHide();
				};
			}
		});
					
		$monto.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});

		//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$monto.focus(function(e){
			if( parseFloat($(this).val().trim()) == 0){
				$(this).val('');
			}
		});

		$monto.blur(function(e){
			if( $(this).val().trim() == ''){
				$(this).val('0.00');
			}
		});

		
		$salir.click(function(event){
			event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-clientsantcancel-overlay').fadeOut(remove);
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-clientsantcancel-overlay').fadeOut(remove);
		});
		   
	});
	
	
    
    
    
    
    
    
	//Eventos del grid edicion,borrar!
	var carga_formaCC00_for_datagrid00Cancel = function(id_to_show, accion_mode){
		//aqui  entra para editar un registro
		var form_to_show = 'formClientsAntCancel';
		
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$(this).modalPanel_clientsantcancel();
					
		$('#forma-clientsantcancel-window').css({"margin-left": -420, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-clientsantcancel-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $identificador = $('#forma-clientsantcancel-window').find('input[name=identificador]');
		var $accion = $('#forma-clientsantcancel-window').find('input[name=accion]');
		var $no_transaccion = $('#forma-clientsantcancel-window').find('input[name=no_transaccion]');
		var $fecha = $('#forma-clientsantcancel-window').find('input[name=fecha]');
		var $no_cliente = $('#forma-clientsantcancel-window').find('input[name=no_cliente]');
		var $razoncliente = $('#forma-clientsantcancel-window').find('input[name=razoncliente]');
		var $monto = $('#forma-clientsantcancel-window').find('input[name=monto]');
		var $select_moneda = $('#forma-clientsantcancel-window').find('select[name=select_moneda]');
		var $observaciones = $('#forma-clientsantcancel-window').find('textarea[name=observaciones]');
		
		var $busca_cliente = $('#forma-clientsantcancel-window').find('#busca_cliente');
		
		var $cancelar = $('#forma-clientsantcancel-window').find('#cancelar');
		var $salir = $('#forma-clientsantcancel-window').find('#salir');
		
		//botones                        
		var $cerrar_plugin = $('#forma-clientsantcancel-window').find('#close');
		
		
		$no_transaccion.attr("readonly", true);
		$fecha.attr("readonly", true);
		$no_cliente.attr("readonly", true);
		$razoncliente.attr("readonly", true);
		$monto.attr("readonly", true);
		$observaciones.attr("readonly", true);
		
		$no_transaccion.css({'background':'#F0F0F0'});
		$fecha.css({'background':'#F0F0F0'});
		$no_cliente.css({'background':'#F0F0F0'});
		$razoncliente.css({'background':'#F0F0F0'});
		$monto.css({'background':'#F0F0F0'});
		
		$identificador.val(id_to_show);
		$accion.val("cancel");
		$busca_cliente.hide();
		
		if(accion_mode == 'edit'){
			var respuestaProcesada = function(data){
				
				jAlert(data['msj'], 'Atencion!');
				
				if ( data['valor'] == 'true' ){
					var remove = function() {$(this).remove();};
					$('#forma-clientsantcancel-overlay').fadeOut(remove);
					$get_datos_grid();
				}
				
				/*
				if ( data['success'] == 'true' ){
					var remove = function() {$(this).remove();};
					$('#forma-clientsantcancel-overlay').fadeOut(remove);
					jAlert("Los datos se han actualizado.", 'Atencion!');
					$get_datos_grid();
				}else{
					// Desaparece todas las interrogaciones si es que existen
					$('#forma-clientsantcancel-window').find('div.interrogacion').css({'display':'none'});
					
					var valor = data['success'].split('___');
					//muestra las interrogaciones
					for (var element in valor){
						tmp = data['success'].split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
							$('#forma-clientsantcancel-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						}
					}
				}
				*/
			}
			
			var options = {dataType :  'json', success : respuestaProcesada};
			$forma_selected.ajaxForm(options);
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAnticipo.json';
			$arreglo = {'identificador':$identificador.val()}
			
			$.post(input_json,$arreglo,function(entry){
				$identificador.val(entry['Datos'][0]['id']);
				$no_transaccion.val(entry['Datos'][0]['numero_transaccion']);
				$fecha.val(entry['Datos'][0]['fecha']);
				$no_cliente.val(entry['Datos'][0]['no_cliente']);
				$razoncliente.val(entry['Datos'][0]['cliente']);
				$monto.val(entry['Datos'][0]['monto']);
				$observaciones.text(entry['Datos'][0]['observaciones']);
				
				if(entry['Datos'][0]['cancelado']=='true'){
					$cancelar.attr('disabled','-1'); //deshabilitar
				}
				
				$select_moneda.children().remove();
				var mon_hmtl = '';
				$.each(entry['Monedas'],function(entryIndex,mon){
					if(parseInt(entry['Datos'][0]['moneda_id'])==parseInt(mon['id'])){
						mon_hmtl += '<option value="' + mon['id'] + '" selected="yes">' + mon['descripcion'] + '</option>';
					}else{
						//mon_hmtl += '<option value="' + mon['id'] + '"  >' + mon['descripcion'] + '</option>';
					}
				});
				$select_moneda.append(mon_hmtl);
			});
			
			
			
			var estado=0;
			$cancelar.click(function(event){
				jConfirm('Confirmar cancelacion?', 'Dialogo de Confirmacion', function(r) {
					// If they confirmed, manually trigger a form submission
					if (r) {
						estado=1;
						$cancelar.trigger('click');
					}else{
						//aqui no hay nada
						estado=0;
					}
				});
				
				if(parseInt(estado)==0){
					return false;
				}else{
					estado=0;
					return true;
				}
			});
			
			
			$salir.click(function(event){
				event.preventDefault();
				var remove = function() {$(this).remove();};
				$('#forma-clientsantcancel-overlay').fadeOut(remove);
			});
			
			//cerrar plugin
			$cerrar_plugin.bind('click',function(){
				var remove = function() {$(this).remove();};
				$('#forma-clientsantcancel-overlay').fadeOut(remove);
			});
		
		
		}
		
	}

    
    
    
    
    
    
    
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllAnticipos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllAnticipos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenableEdit(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00Cancel);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



