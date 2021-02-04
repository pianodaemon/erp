//AGNUX
//NLE Controller de Contra Recibos IMSS

$(function() {
        //var controller = "com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/remisionesIMSS";
        
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	//Arreglo que almacena las unidades de medida
	var arrayUM;
	//Variable que indica si se debe permitir cambiar la unidad de medida al agregar el producto
	var cambiarUM;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/remisionesIMSS";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $new_cotizacion = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Contra Recibos IMSS');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));	
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
        var $busqueda_folioIMSS = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folioIMSS]');
        var $busqueda_numContrato = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_numContrato]');
   	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $busqueda_select_tipo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_tipo]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');	
	var $busqueda_select_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_agente]');
        var $busqueda_select_status = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_status]');
        
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	//var almacenes = new Array(); //este arreglo carga el select de almacen, los select almacen destino del grid
	
	//con esta variable se verifica si el erp incluye modulo de CRM
	if($('#lienzo_recalculable').find('input[name=crm]').val()=='false'){
		$('#barra_buscador').find('.tabla_buscador').find('#td_tipo').hide();
		$busqueda_select_tipo.children().remove();
			var html='<option value="1" selected="yes">Cliente</option>';
		$busqueda_select_tipo.append(html);
		$('#barra_buscador').find('.tabla_buscador').find('#etiqueta_tipo').text("Cliente");
	}else{
		$('#barra_buscador').find('.tabla_buscador').find('#etiqueta_tipo').text("Cliente/Prospecto");	
	}
	
	
	//cambiar el tipo
	$busqueda_select_tipo.change(function(){
		if(parseInt($(this).val())==0){
			$('#barra_buscador').find('.tabla_buscador').find('#etiqueta_tipo').text("Cliente/Prospecto");
		}
		
		if(parseInt($(this).val())==1){
			$('#barra_buscador').find('.tabla_buscador').find('#etiqueta_tipo').text("Cliente");
		}
		
		if(parseInt($(this).val())==2){
			$('#barra_buscador').find('.tabla_buscador').find('#etiqueta_tipo').text("Prospecto");
		}
	});
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val()+ "|";
		valor_retorno += "tipo" + signo_separador + $busqueda_select_tipo.val()+ "|";
		valor_retorno += "incluye_crm" + signo_separador + $('#lienzo_recalculable').find('input[name=crm]').val() + "|";
		valor_retorno += "folioIMSS" + signo_separador + $busqueda_folioIMSS.val() + "|";
		valor_retorno += "numContrato" + signo_separador + $busqueda_numContrato.val() + "|";
		valor_retorno += "status" + signo_separador + $busqueda_select_status.val();
		//alert("valor_retorno="+valor_retorno);
		return valor_retorno;
	};
        
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	$buscar.click(function(event){
		event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
                //alert("Buscar");
		$get_datos_grid();
	});
	
	//esta funcion carga los datos para el buscador del paginado
	$cargar_datos_buscador_principal= function(){
            //var $select_statusRemisionIMSS = $('#forma-cotizacions-window').find('select[name=statusRemisionIMSS]');
            //alert("Entrando a carga de select Status remisiones IMSS");
            try {
                var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getStatusRemisionIMSS.json';                
                $.post(input_json_tipos,function(data){
                        //Carga el select con tipos de estatus
                        //alert("$busqueda_select_status="+$busqueda_select_status);
                        $busqueda_select_status.children().remove();
                        var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
                        $.each(data,function(entryIndex,pt){
                            prod_tipos_html += '<option value="' + pt['id'] + '">' + pt['descripcion'] + '</option>';
                                /*if(parseInt(pt['id'])==1){
                                        prod_tipos_html += '<option value="' + pt['id'] + '" selected="yes">' + pt['titulo'] + '</option>';
                                }else{
                                        prod_tipos_html += '<option value="' + pt['id'] + '" >' + pt['titulo'] + '</option>';
                                }*/
                        });
                        $busqueda_select_status.append(prod_tipos_html);
                });
            }catch(err){
                alert("Error:"+err);
            }

            
		/*var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_lineas,$arreglo,function(data){
			//Alimentando los campos select_agente
			$busqueda_select_agente.children().remove();
			var agente_hmtl = '';
			if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
				agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
			}
			
			$.each(data['Agentes'],function(entryIndex,agente){
				if(parseInt(agente['id'])==parseInt(data['Extra'][0]['id_agente'])){
					agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
				}else{
					//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador,
					//por lo tanto hay que cargar todos los agentes en el select del buscador
					if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
						agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
					}
				}
			});
			$busqueda_select_agente.append(agente_hmtl);
		});
            */
	}
	
	
	$limpiar.click(function(event){
		$cargar_datos_buscador_principal();
		
		$busqueda_folio.val('');
		$busqueda_cliente.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_codigo.val('');
		$busqueda_producto.val('');
		$busqueda_select_tipo.children().remove();
		var html2="";
		//con esta variable se verifica si el erp incluye modulo de CRM
		if($('#lienzo_recalculable').find('input[name=crm]').val()=='false'){
			html2='<option value="1" selected="yes">Cliente</option>';
		}else{
			html2 ='<option value="0" selected="yes">[-Todos-]</option>';
			html2+='<option value="1">Cliente</option>';
			html2 +='<option value="2">Prospecto</option>';
		}
		$busqueda_select_tipo.append(html2);
		$get_datos_grid();
		
		$busqueda_folio.focus();
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
		$busqueda_folio.focus();
	});
	
	//llamamos a la fucnion que carga datos para el buscador principal
	$cargar_datos_buscador_principal();
	
	
	//aplicar evento keypress a campos para ejecutar la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_producto, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_tipo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_agente, $buscar);
	
	
	
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
	
	
	
	
    //datos para el buscador
    //$arreglo = {}
    //var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/data_buscador/out.json';
    //alimenta campos buscador
    
	
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-cotizacions-window').find('select[name=prodtipo]');
		$('#forma-cotizacions-window').find('#submit').mouseover(function(){
			$('#forma-cotizacions-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-cotizacions-window').find('#submit').mouseout(function(){
			$('#forma-cotizacions-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		
		$('#forma-cotizacions-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-cotizacions-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-cotizacions-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-cotizacions-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-cotizacions-window').find('#close').mouseover(function(){
			$('#forma-cotizacions-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-cotizacions-window').find('#close').mouseout(function(){
			$('#forma-cotizacions-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});

		$('#forma-cotizacions-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-cotizacions-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-cotizacions-window').find(".contenidoPes:first").show(); //Show first tab content

		//On Click Event
		$('#forma-cotizacions-window').find("ul.pestanas li").click(function() {
			$('#forma-cotizacions-window').find(".contenidoPes").hide();
			$('#forma-cotizacions-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-cotizacions-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	//carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, $arreglo_elementos, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem){
		var select_html = '';
		
		if(texto_elemento_cero != ''){
			select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		}
		
		$.each($arreglo_elementos,function(entryIndex,elemento){
			if( parseInt(elemento[index_elem]) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + elemento[index_elem] + '" selected="yes">' + elemento[index_text_elem] + '</option>';
			}else{
				select_html += '<option value="' + elemento[index_elem] + '" >' + elemento[index_text_elem] + '</option>';
			}
		});
		$campo_select.children().remove();
		$campo_select.append(select_html);
	}
	
	
	
	$agregarDatosClienteSeleccionado = function(array_monedas, array_agentes, num_lista_precio, id_cliente, numero_control, rfc_cliente, razon_social_cliente, dir_cliente, contacto_cliente, id_moneda_cliente, id_agente){
		//asignar a los campos correspondientes el sku y y descripcion
		$('#forma-cotizacions-window').find('input[name=id_cliente]').val(id_cliente);
		$('#forma-cotizacions-window').find('input[name=nocontrolcliente]').val(numero_control);
		$('#forma-cotizacions-window').find('input[name=rfccliente]').val(rfc_cliente);
		$('#forma-cotizacions-window').find('input[name=razoncliente]').val(razon_social_cliente);
		$('#forma-cotizacions-window').find('input[name=dircliente]').val(dir_cliente);
		$('#forma-cotizacions-window').find('input[name=contactocliente]').val(contacto_cliente);
		$('#forma-cotizacions-window').find('input[name=num_lista_precio]').val(num_lista_precio);
		
		var id_moneda = id_moneda_cliente;
		
		var moneda_hmtl = '';
		
		/*
		if(parseInt(num_lista_precio)>0){
			//aquí se arma la cadena json para traer la moneda de la lista de precio
			var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMonedaLista.json';
			$arreglo2 = { 'lista_precio':num_lista_precio }
			$.post(input_json2,$arreglo2,function(moneda_lista){
				$.each(moneda_lista['listaprecio'],function(entryIndex ,monedalista){
					id_moneda = monedalista['moneda_id'];
					
					//carga el select de monedas  con la moneda del cliente seleccionada por default
					$('#forma-cotizacions-window').find('select[name=moneda]').children().remove();
					$('#forma-cotizacions-window').find('select[name=moneda2]').children().remove();
					$.each(array_monedas ,function(entryIndex,moneda){
						if( parseInt(moneda['id']) == parseInt(id_moneda) ){
							moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//permitir que se muestren todas las monedas
							//if(parseInt(id_moneda)==0){
								moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
							//}
						}
					});
					$('#forma-cotizacions-window').find('select[name=moneda]').append(moneda_hmtl);
					//esta es la moneda original, no cambia al cambiar la moneda de la vista
					$('#forma-cotizacions-window').find('select[name=moneda2]').append(moneda_hmtl);
					
					
					var cambiarTcOriginal='true';
					//ejecutar funcion que obtiene el tipo de cambio de acuerdo a la moneda seleccionada
					getTcIdMoneda(id_moneda, cambiarTcOriginal);
				});
			});
		}else{
			//carga el select de monedas  con la moneda del cliente seleccionada por default
			$('#forma-cotizacions-window').find('select[name=moneda]').children().remove();
			$('#forma-cotizacions-window').find('select[name=moneda2]').children().remove();
			$.each(array_monedas ,function(entryIndex,moneda){
				if( parseInt(moneda['id']) == parseInt(id_moneda) ){
					moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
				}else{
					//permitir que se muestren todas las monedas
					//if(parseInt(id_moneda)==0){
						moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
					//}
				}
			});
			$('#forma-cotizacions-window').find('select[name=moneda]').append(moneda_hmtl);
			
			//esta es la moneda original, no cambia al cambiar la moneda de la vista
			$('#forma-cotizacions-window').find('select[name=moneda2]').append(moneda_hmtl);
			
			var cambiarTcOriginal='true';
			//ejecutar funcion que obtiene el tipo de cambio de acuerdo a la moneda seleccionada
			getTcIdMoneda(id_moneda, cambiarTcOriginal);
		}
		*/
		
		
		//carga el select de monedas  con la moneda del cliente seleccionada por default
		$('#forma-cotizacions-window').find('select[name=moneda]').children().remove();
		$('#forma-cotizacions-window').find('select[name=moneda2]').children().remove();
		$.each(array_monedas ,function(entryIndex,moneda){
			if( parseInt(moneda['id']) == parseInt(id_moneda) ){
				moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
			}else{
				//permitir que se muestren todas las monedas
				//if(parseInt(id_moneda)==0){
				//	moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
				//}
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			}
		});
		$('#forma-cotizacions-window').find('select[name=moneda]').append(moneda_hmtl);
		//esta es la moneda original, no cambia al cambiar la moneda de la vista
		$('#forma-cotizacions-window').find('select[name=moneda2]').append(moneda_hmtl);
		
		var cambiarTcOriginal='true';
		//ejecutar funcion que obtiene el tipo de cambio de acuerdo a la moneda seleccionada
		getTcIdMoneda(id_moneda, cambiarTcOriginal);
		
		
		
		
		//carga select de agentes dejando seleccionado por default el agente asignado al cliente
		$('#forma-cotizacions-window').find('select[name=select_agente]').children().remove();
		var agen_hmtl = '';
		$.each(array_agentes,function(entryIndex,agen){
			if(parseInt(agen['id']) == parseInt(id_agente)){
				agen_hmtl += '<option value="' + agen['id'] + '" selected="yes">' + agen['nombre_agente'] + '</option>';
			}else{
				agen_hmtl += '<option value="' + agen['id'] + '"  >' + agen['nombre_agente'] + '</option>';
			}
		});
		$('#forma-cotizacions-window').find('select[name=select_agente]').append(agen_hmtl);
		
	}
	
	
	
	
	
	//buscador de clientes
	$busca_clientes = function(tipo, no_control, cliente, array_monedas, array_agentes){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscacliente();
		var $dialogoc =  $('#forma-buscacliente-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
		$('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -200});
		
		var $tabla_resultados = $('#forma-buscacliente-window').find('#tabla_resultado');
		
		//var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('a[href*=busca_cliente_modalbox]');
		//var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('a[href*=cencela]');
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
		
		if(parseInt(tipo)==1){
			$('#forma-buscacliente-window').find('div.titulo_clientes').find('strong').text("Buscador de Clientes");
		}else{
			$('#forma-buscacliente-window').find('div.titulo_clientes').find('strong').text("Buscador de Prospectos");
		}
		
		var html = '';
		$select_filtro_por.children().remove();
		html='<option value="0">[-- Opcion busqueda --]</option>';
		
		if(no_control!=''){
			html+='<option value="1" selected="yes">No. de control</option>';
			$cadena_buscar.val(no_control);
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if(cliente!=''){
			html+='<option value="3" selected="yes">Razon social</option>';
			$cadena_buscar.val(cliente);
		}else{
			if(no_control=='' && cliente==''){
				html+='<option value="3" selected="yes">Razon social</option>';
			}else{
				html+='<option value="3">Razon social</option>';
			}
		}
		
		if(parseInt(tipo)==1){
			//estos dos filtros solo son para clientes, no para prospectos
			html+='<option value="4">CURP</option>';
			html+='<option value="5">Alias</option>';
		}
		$select_filtro_por.append(html);
		
		
		$cadena_buscar.focus();
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorClienteProspecto.json';
			$arreglo = {
						'tipo':tipo,
						'cadena':$cadena_buscar.val(),
						'filtro':$select_filtro_por.val(),
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Resultado'],function(entryIndex,cliente){
					
					trr = '<tr>';
						trr += '<td width="80">';
							trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
							trr += '<input type="hidden" id="direccion" value="'+cliente['direccion']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+cliente['moneda_id']+'">';
							trr += '<input type="hidden" id="moneda" value="'+cliente['moneda']+'">';
							trr += '<input type="hidden" id="contacto" value="'+cliente['contacto']+'">';
							trr += '<span class="no_control">'+cliente['numero_control']+'</span>';
							trr += '<input type="hidden" id="id_agente" value="'+cliente['cxc_agen_id']+'">';
							trr += '<input type="hidden" id="lista_precio" value="'+cliente['lista_precio']+'">';
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
					var num_lista_precio=$(this).find('#lista_precio').val();
					var id_cliente = $(this).find('#idclient').val();
					var numero_control = $(this).find('span.no_control').html();
					var rfc_cliente = $(this).find('span.rfc').html();
					var razon_social_cliente = $(this).find('span.razon').html();
					var dir_cliente = $(this).find('#direccion').val();
					var contacto_cliente = $(this).find('#contacto').val();
					var id_moneda_cliente = $(this).find('#id_moneda').val();
					var id_agente = $(this).find('#id_agente').val();
					
					$agregarDatosClienteSeleccionado(array_monedas, array_agentes, num_lista_precio, id_cliente, numero_control, rfc_cliente, razon_social_cliente, dir_cliente, contacto_cliente, id_moneda_cliente, id_agente);
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();
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
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
			$('#forma-cotizacions-window').find('input[name=nocontrolcliente]').focus();
		});
	}//termina buscador de clientes
	
	
	
	
	
	//buscador de productos
	$busca_productos = function(sku_buscar, descripcion){
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
		
		//var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('a[href*=busca_producto_modalbox]');
		//var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('a[href*=cencela]');
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
			var prod_tipos_html = '<option value="0">[--Seleccionar Tipo--]</option>';
			$.each(data['prodTipos'],function(entryIndex,pt){
				if(parseInt(pt['id'])==1){
					prod_tipos_html += '<option value="' + pt['id'] + '" selected="yes">' + pt['titulo'] + '</option>';
				}else{
					prod_tipos_html += '<option value="' + pt['id'] + '" >' + pt['titulo'] + '</option>';
				}
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		
		
		//Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
		$campo_sku.val(sku_buscar);
		
		$campo_descripcion.val(descripcion);
		
		$campo_sku.focus();//asignar enfoque al cargar plugin
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_productos.json';
			$arreglo = {	'sku':$campo_sku.val(),
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
							trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
							trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
						trr += '</td>';
						trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
						trr += '<td width="90">';
							trr += '<span class="unidad_id" style="display:none;">'+producto['unidad_id']+'</span>';
							trr += '<span class="utitulo">'+producto['unidad']+'</span>';
						trr += '</td>';
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
					$('#forma-cotizacions-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
					$('#forma-cotizacions-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();
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
			$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();
		});
	}//termina buscador de productos
	
	//buscador de presentaciones disponibles para un producto
	$buscador_presentaciones_producto = function(id_cliente, rfc_cliente, sku_producto,$nombre_producto,$grid_productos, arrayMonedas, $select_moneda, $tc_usd){
		var $cliente_listaprecio=  $('#forma-cotizacions-window').find('input[name=num_lista_precio]');
		var $select_tipo_cotizacion=  $('#forma-cotizacions-window').find('select[name=select_tipo_cotizacion]');
		
		//Verifica si el campo rfc proveedor no esta vacio
		if(id_cliente.trim()!='' && id_cliente.trim()!='0'){
			//verifica si el campo sku no esta vacio para realizar busqueda
			if(sku_producto != ''){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPresentacionesProducto.json';
				$arreglo = {
					'sku':sku_producto,
					'lista_precio':$cliente_listaprecio.val(),
					'tipo':$select_tipo_cotizacion.val(),
					'id_client':id_cliente,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var trr = '';
				
				$.post(input_json,$arreglo,function(entry){
					
					//verifica si el arreglo  retorno datos
					if (entry['Presentaciones'].length > 0){
						
						if(parseInt(entry['Presentaciones'].length)==1){
							
							var id_detalle=0;
							var id_prod = entry['Presentaciones'][0]['id'];
							var sku = entry['Presentaciones'][0]['sku'];
							var titulo = entry['Presentaciones'][0]['titulo'];
							var imagen = entry['Presentaciones'][0]['archivo_img'];
							var descripcion = entry['Presentaciones'][0]['descripcion_larga'];
							var unidadId = entry['Presentaciones'][0]['unidad_id'];
							var unidad = entry['Presentaciones'][0]['unidad'];
							var id_pres = entry['Presentaciones'][0]['id_presentacion'];
							var pres = entry['Presentaciones'][0]['presentacion'];
							var precio = entry['Presentaciones'][0]['precio'];
							//id de la moneda en la que viene el precio
							var idmonpre = entry['Presentaciones'][0]['id_moneda'];
							//tipo de cambio de la moneda del precio del producto
							var tcMonProd = entry['Presentaciones'][0]['tc'];
							var exislp = entry['Presentaciones'][0]['exis_prod_lp'];
							
							var cantidad = 0;
							var importe = 0;
							var idmonpartida=0;
							var idImpto = entry['Presentaciones'][0]['id_impto'];
							var valorImpto = entry['Presentaciones'][0]['valor_impto'];
							var reg_aut = '0&&&0&&&0';
							
							if($tc_usd.val().trim()!=''){
								if(exislp=='1'){
									//aqui se pasan datos a la funcion que agrega el tr en el grid
									$agrega_producto_grid($grid_productos, id_detalle, id_prod, sku, titulo, imagen, descripcion, unidadId, unidad, id_pres, pres, precio, cantidad, importe, idmonpre, arrayMonedas, idImpto, valorImpto, tcMonProd, idmonpartida, reg_aut);
								}else{
									jAlert(exislp, 'Atencion!', function(r) { 
										$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();
									});
								}
							}else{
								jAlert('Es necesario ingresar el Tipo de Cambio.', 'Atencion!', function(r) { 
									$tc_usd.focus();
								});
							}
						}else{
							
							$(this).modalPanel_Buscapresentacion();
							var $dialogoc =  $('#forma-buscapresentacion-window');
							$dialogoc.append($('div.buscador_presentaciones').find('table.formaBusqueda_presentaciones').clone());
							$('#forma-buscapresentacion-window').css({"margin-left": -200, "margin-top": -200});
							
							var $tabla_resultados = $('#forma-buscapresentacion-window').find('#tabla_resultado');
							var $cancelar_plugin_busca_lotes_producto = $('#forma-buscapresentacion-window').find('a[href*=cencela]');
							$tabla_resultados.children().remove();
							
							//crea el tr con los datos del producto seleccionado
							$.each(entry['Presentaciones'],function(entryIndex,pres){
								trr = '<tr>';
									trr += '<td width="100">';
										trr += '<span class="id_prod" style="display:none">'+pres['id']+'</span>';
										trr += '<span class="sku">'+pres['sku']+'</span>';
									trr += '</td>';
									trr += '<td width="250"><span class="titulo">'+pres['titulo']+'</span></td>';
									trr += '<td width="80">';
										trr += '<span class="unidadId" style="display:none">'+pres['unidad_id']+'</span>';
										trr += '<span class="unidad" style="display:none">'+pres['unidad']+'</span>';
										trr += '<span class="id_pres" style="display:none">'+pres['id_presentacion']+'</span>';
										trr += '<span class="pres">'+pres['presentacion']+'</span>';
										trr += '<span class="precio" style="display:none">'+pres['precio']+'</span>';
										trr += '<span class="idmonpre" style="display:none">'+pres['id_moneda']+'</span>';
										trr += '<span class="tc" style="display:none">'+pres['tc']+'</span>';
										trr += '<span class="img" style="display:none">'+pres['archivo_img']+'</span>';
										trr += '<span class="desclarga" style="display:none">'+pres['descripcion_larga']+'</span>';
										trr += '<span class="exislp" style="display:none">'+pres['exis_prod_lp']+'</span>';
										trr += '<span class="idImpto" style="display:none">'+pres['id_impto']+'</span>';
										trr += '<span class="valorImpto" style="display:none">'+pres['valor_impto']+'</span>';
									trr += '</td>';
								trr += '</tr>';
								$tabla_resultados.append(trr);
							});//termina llamada json
							
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
								var id_detalle=0;
								var id_prod = $(this).find('span.id_prod').html();
								var sku = $(this).find('span.sku').html();
								var titulo = $(this).find('span.titulo').html();
								var imagen =$(this).find('span.img').html();
								var descripcion =$(this).find('span.desclarga').html();
								var unidadId = $(this).find('span.unidadId').html();
								var unidad = $(this).find('span.unidad').html();
								var id_pres = $(this).find('span.id_pres').html();
								var pres = $(this).find('span.pres').html();
								var precio = $(this).find('span.precio').html();
								//id de la moneda en la que viene el precio
								var idmonpre = $(this).find('span.idmonpre').html();
								//tipo de cambio de la moneda del precio del producto
								var tcMonProd = $(this).find('span.tc').html();
								var exislp = $(this).find('span.exislp').html();
								
								var cantidad = 0;
								var importe = 0;
								var idmonpartida=0;
								var idImpto = $(this).find('span.idImpto').html();
								var valorImpto = $(this).find('span.valorImpto').html();
								var reg_aut = '0&&&0&&&0';
								
								if($tc_usd.val().trim()!=''){
									if(exislp=='1'){
										//aqui se pasan datos a la funcion que agrega el tr en el grid
										//$agrega_producto_grid($grid_productos, id_detalle, id_prod, sku, titulo, imagen, descripcion, unidad, id_pres, pres, precio, cantidad, importe, mon_id, arrayMonedas, idImp, valorImp);
										$agrega_producto_grid($grid_productos, id_detalle, id_prod, sku, titulo, imagen, descripcion, unidadId, unidad, id_pres, pres, precio, cantidad, importe, idmonpre, arrayMonedas, idImpto, valorImpto, tcMonProd, idmonpartida, reg_aut);
									}else{
										jAlert(exislp, 'Atencion!', function(r) { 
											$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();
										});
									}
								}else{
									jAlert('Es necesario ingresar el Tipo de Cambio.', 'Atencion!', function(r) { 
										$tc_usd.focus();
									});
								}
								
								//elimina la ventana de busqueda
								var remove = function() {$(this).remove();};
								$('#forma-buscapresentacion-overlay').fadeOut(remove);
							});
							
							$cancelar_plugin_busca_lotes_producto.click(function(event){
								event.preventDefault();
								var remove = function() {$(this).remove();};
								$('#forma-buscapresentacion-overlay').fadeOut(remove);
								
								//regresa el enfoque al campo sku para permitir ingresar uno nuevo
								$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();
							});
						}
						
					}else{
						jAlert('El producto que intenta agregar no existe, pruebe ingresando otro.\nHaga clic en Buscar.', 'Atencion!', function(r) { 
							$('#forma-cotizacions-window').find('input[name=titulo_producto]').val('');
							$('#forma-cotizacions-window').find('input[name=titulo_producto]').focus();
						});
					}
				},"json");
				
			}else{
				jAlert('Es necesario ingresar un Sku de producto valido.', 'Atencion!', function(r) { 
					$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();
				});
			}
		}else{
			jAlert('Es necesario seleccionar un Cliente.', 'Atencion!', function(r) { 
				$('#forma-cotizacions-window').find('input[name=nocontrolcliente]').focus();
			});
		}
		
	}//termina buscador dpresentaciones disponibles de un producto
	
	
	
	
	
	
	
	//agregar producto al grid
	$agrega_producto_grid = function($grid_productos, id_detalle, id_prod, sku, titulo, imagen, descripcion, unidadId, unidad, id_pres, pres, precio, cantidad, importe, idmonpre, arrayMonedas, idImp, valorImp, tcMonProd, idmonpartida, reg_aut){
		var $id_impuesto = $('#forma-cotizacions-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-cotizacions-window').find('input[name=valorimpuesto]');
		var $check_descripcion_larga =$('#forma-cotizacions-window').find('input[name=check_descripcion_larga]');
		
		var $select_moneda = $('#forma-cotizacions-window').find('select[name=moneda]');
		//var $moneda_original = $('#forma-cotizacions-window').find('select[name=moneda2]');
		var $num_lista_precio = $('#forma-cotizacions-window').find('input[name=num_lista_precio]');
		var $tc = $('#forma-cotizacions-window').find('input[name=tc]');
		//var $tc_original = $('#forma-cotizacions-window').find('input[name=tc_original]');
		var $tc_usd = $('#forma-cotizacions-window').find('input[name=tc_usd]');
		
		var idMonedaCotizacion = $select_moneda.val();
		var precioOriginal = precio;
		var precioCambiado = 0.00;
		var importeImpuesto=0.00;
		var agregarTr=false;
		
		
		//Verificamos si la Lista de Precio trae moneda
		if(parseInt($num_lista_precio.val())>0){
			//Aqui solo debe entrar cuando el tr es Nuevo, en editar ya no
			if(parseInt(idmonpartida)==0){
				/*
				Si la moneda de la Cotizacion es diferente a la moneda del Precio del Producto
				entonces convertimos el precio a la moneda del Cotizacion de acuerdo al tipo de cambio actual
				*/
				if( parseInt(idMonedaCotizacion) != parseInt(idmonpre) ){
					if(parseInt(idMonedaCotizacion)==1 && parseInt(idmonpre)!=1){
						//si la moneda de la Cotizacion es pesos y la moneda del precio es diferente de Pesos,
						//entonces calculamos su equivalente a pesos
						precioCambiado = parseFloat(parseFloat(precioOriginal) * parseFloat($tc_usd.val())).toFixed(4);
					}
					
					if(parseInt(idMonedaCotizacion)!=1 && parseInt(idmonpre)==1){
						//si la moneda de la Cotizacion es dolar y la moneda del precio es Pesos, calculamos su equivalente a dolar
						precioCambiado = parseFloat( parseFloat(precioOriginal) / parseFloat($tc_usd.val()) ).toFixed(4);
					}
				}else{
					precioCambiado = precio;
				}
			}else{
				precioCambiado = precio;
			}
		}else{
			precioCambiado = precio;
			idmonpre = idMonedaCotizacion;
		}
		
		//agregar comas
		precioCambiado = $(this).agregar_comas(parseFloat(precioCambiado).toFixed(4));
		
		//alert("precioCambiado: "+precioCambiado);
		
		
		/*
		Si la partida que se está agregando tiene idmonpartida=0, entonces  significa que es nuevo, 
		por lo tanto se le asigna la moneda global definida para la cotizacion
		*/
		if(parseInt(idmonpartida)==0){
			idmonpartida=idMonedaCotizacion;
		}
		
		
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		importe = parseFloat(importe).toFixed(4);
		
		var importeMonCotizacion=0;
		
		/*
		Calcula el Importe de la partida en la Moneda Global de la Cotizacion, esto porque el total de la cotizacion
		se saca tomando la moneda global y no la moneda de cada partida.
		*/
		if(parseInt($select_moneda.val()) == parseInt(idmonpartida)){
			importeMonCotizacion = importe;
		}else{
			if(parseInt($select_moneda.val())==1 && parseInt(idmonpartida)!=1){
				importeMonCotizacion = parseFloat(importe) * parseFloat($tc_usd.val());
			}else{
				if(parseInt($select_moneda.val())!=1 && parseInt(idmonpartida)==1){
					importeMonCotizacion = parseFloat(importe) / parseFloat($tc_usd.val());
				}
			}
		}
		
		importeMonCotizacion = parseFloat(importeMonCotizacion).toFixed(4);
		
		if (parseFloat(idImp)!=0 && parseFloat(valorImp)!=0){
			$id_impuesto.val(idImp);
			$valor_impuesto.val(valorImp);
			importeImpuesto = parseFloat(parseFloat(importeMonCotizacion) * parseFloat(valorImp)).toFixed(4);
		}
		
		var encontrado = 0;
		//busca el sku y la presentacion en el grid
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#skuprod').val() == sku.toUpperCase() )  && (parseInt($(this).find('#idpres').val())== parseInt(id_pres) ) && (parseInt($(this).find('#elim').val())!=0)){
				encontrado=1;//el producto ya esta en el grid
			}
		});
		
		if(parseInt(encontrado)!=1){//si el producto no esta en el grid entra aqui
			//obtiene numero de trs
			var tr = $("tr", $grid_productos).size();
			tr++;
			
			var trr = '';
			trr = '<tr>';
				trr += '<td class="grid" style="font-size:11px;  border:1px solid #C1DAD7;" width="25">';
					trr += '<a href="#delete'+ tr +'" id="delete'+ tr +'"><div id="eliminar'+ tr +'" class="onmouseOutEliminar" style="width:24px; background-position:center;"/></a>';
					trr += '<input type="hidden" name="eliminado" class="elim'+ tr +'" id="elim" value="1">';//el 1 significa que el registro no ha sido eliminado
					trr += '<input type="hidden" name="iddetalle" class="iddetalle'+ tr +'" id="idd" value="'+id_detalle+'">';//este es el id del registro que ocupa el producto en la tabla cotizacions_detalles
					trr += '<input type="hidden" name="notr" class="notr'+ tr +'" value="'+ tr +'">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="100">';
					trr += '<input type="hidden" name="idproducto" class="borde_oculto" id="idprod" value="'+ id_prod +'">';
					trr += '<input type="text" name="sku" value="'+ sku +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:96px;">';
				trr += '</td>';
				trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="180">';
					trr += '<input type="text" 	name="nombre" 	value="'+ titulo +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:176px;">';
				trr += '</td>';
				
				var altura_td='';
				if(imagen != ""){
					altura_td='height="90"';
				}
				
				trr += '<td class="grid1" id="td_imagen" style="font-size:11px;  border:1px solid #C1DAD7;" width="100" '+altura_td+' >';
					trr += '<div class="div_img'+ tr +'" style="width:96px; height:90px;" border="1">';
						trr += '<img id="contenidofileimg'+tr+'" src="#" align="top" width="96" height="90">';
					trr += '</div>';
				trr += '</td>';
				trr += '<td class="grid1" id="td_descripcion" style="font-size:11px;  border:1px solid #C1DAD7;" width="220">';
					trr += '<textarea name="descripcion" id="desc'+ tr +'" readOnly="true" class="borde_oculto" cols="10" rows="5" readOnly="true" style="width:216px; height:90px; resize:none; background-color:#FFFFF;">'+descripcion+'</textarea>';
				trr += '</td>';
				
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
					trr += '<select name="select_umedida" class="select_umedida'+ tr +'" style="width:86px;"></select>';
					trr += '<input type="text" 	name="unidad'+ tr +'" value="'+ unidad +'" id="uni" class="borde_oculto" readOnly="true" style="width:86px;">';
				trr += '</td>';
				
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
					trr += '<INPUT type="hidden" name="id_presentacion" class="id_pres'+ tr +'" value="'+  id_pres +'" 	id="idpres">';
					trr += '<INPUT TYPE="text" name="presentacion'+ tr +'" class="borde_oculto" value="'+  pres +'" id="pres"  readOnly="true" style="width:96px;">';
				trr += '</td>';
				
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70">';
					trr += '<input type="text" name="cantidad" class="cant'+ tr +'" value="'+cantidad+'" id="cant" style="width:66px;">';
				trr += '</td>';
				
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
					trr += '<input type="text" name="precio" class="precio'+ tr +'" value="'+precioCambiado+'" id="cost" style="width:76px;">';
					trr += '<input type="hidden" value="'+precioOriginal+'" class="precor'+ tr +'" id="precor" style="width:76px;">';
				trr += '</td>';
				
				trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="50">';
					trr += '<select name="monedagrid" class="monedagrid'+ tr +'" style="width:46px;"></select>';
					trr += '<input type="hidden" name="idMonLp"  class="idMonLp'+ tr +'" value="'+ idmonpre +'" id="idMonLp">';
				trr += '</td>';
				
				trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70">';
					trr += '<input type="text" 	name="importe" 	class="import'+ tr +'" value="'+importe+'" id="import" readOnly="true" style="width:66px; text-align:right;">';
					trr += '<input type="hidden" name="importeMonCot" class="impMonCot'+ tr +'" value="'+  importeMonCotizacion +'" id="impMonCot">';
					trr += '<input type="hidden" name="id_imp_prod"   value="'+ idImp +'" id="idimppord">';
					trr += '<input type="hidden" name="valor_imp"     class="ivalorimp'+ tr +'" value="'+  valorImp +'" id="ivalorimp">';
					trr += '<input type="hidden" name="totimpuesto'+ tr +'" class="totimp'+ tr +'" id="totimp" value="'+importeImpuesto+'">';
				trr += '</td>';
				
				trr += '<td class="grid2" style="font-size:11px;  border:1px solid #C1DAD7;" width="25" id="td_check_auth'+ tr +'">';
					trr += '<input type="hidden" name="statusreg"   class="statusreg'+ tr +'" value="'+ reg_aut +'">';
					trr += '<input type="hidden" name="reqauth"   class="reqauth'+ tr +'" value="false">';
					trr += '<input type="hidden" name="success"   class="success'+ tr +'" value="false">';
					trr += '<input type="checkbox" name="checkauth" class="checkauth'+ tr +'">';
				trr += '</td>';
			trr += '</tr>';
			$grid_productos.append(trr);
			
			$grid_productos.find('.checkauth'+tr).hide();
			
			$grid_productos.find('.checkauth'+ tr).click(function(event){
				if(this.checked){
					$('#forma-cotizacions-window').find('#btn_autorizar').show();
				}else{
					var cont_check=0;
					$grid_productos.find('input[name=checkauth]').each(function(index){
						if(this.checked){
							if(parseInt($(this).parent().parent().find('input[name=eliminado]').val())==1){
								cont_check++;
							}
						}
					});
					
					if(parseInt(cont_check)<=0){
						$('#forma-cotizacions-window').find('#btn_autorizar').hide();
					}
				}
			});
			
			
			var unidadLitroKilo=false;
			
			if(parseInt(unidad.toUpperCase().search(/KILO/))>-1){
				unidadLitroKilo=true;
			}else{
				if(parseInt(unidad.toUpperCase().search(/LITRO/))>-1){
					unidadLitroKilo=true;
				}
			}
			
			var hmtl_um="";
			if(cambiarUM.trim()=='true'){
				if(unidadLitroKilo){
					$grid_productos.find('select.select_umedida'+tr).children().remove();
					$.each(arrayUM,function(entryIndex,um){
						if(parseInt(unidadId) == parseInt(um['id'])){
							hmtl_um += '<option value="' + um['id'] + '" selected="yes" >' + um['titulo'] + '</option>';
						}else{
							if(parseInt(um['titulo'].toUpperCase().search(/KILO/))>-1 || parseInt(um['titulo'].toUpperCase().search(/LITRO/))>-1){
								hmtl_um += '<option value="' + um['id'] + '">' + um['titulo'] + '</option>';
							}
						}
					});
					$grid_productos.find('select.select_umedida'+tr).append(hmtl_um);
				}else{
					$grid_productos.find('select.select_umedida'+tr).children().remove();
					$.each(arrayUM,function(entryIndex,um){
						if(parseInt(unidadId) == parseInt(um['id'])){
							hmtl_um += '<option value="' + um['id'] + '" selected="yes" >' + um['titulo'] + '</option>';
						}
					});
					$grid_productos.find('select.select_umedida'+tr).append(hmtl_um);
				}
				//Ocultar campo input porque se debe mostrar select para permitir cambio de unidad de medida
				$grid_productos.find('input[name=unidad'+ tr +']').hide();
			}else{
				//Carga select de pais Origen
				var elemento_seleccionado = unidadId;
				var texto_elemento_cero = '';
				var index_elem = 'id';
				var index_text_elem = 'titulo';
				$carga_campos_select($grid_productos.find('select.select_umedida'+tr), arrayUM, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem);
				
				//Ocultar porque no se permitirá cambiar de unidad de medida
				$grid_productos.find('select.select_umedida'+tr).hide();
			}
			
			
			
			$aplicar_evento_click_checkbox($check_descripcion_larga);
			
			if(imagen != ""){
				var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
				var input_json_img = document.location.protocol + '//' + document.location.host + '/' + controller + '/imgDownloadImg/'+imagen+'/'+id_prod+'/'+iu+'/out.json';
				$('#forma-cotizacions-window').find('#contenidofileimg'+tr).removeAttr("src").attr("src",input_json_img);
			}
			
			//ocultar tds  si no esta seleccionado el checkbox de descripcion larga
			if( !$check_descripcion_larga.is(':checked') ){
				$grid_productos.find('#td_imagen').hide();
				$grid_productos.find('#td_descripcion').hide();
			}
			
			//si la descripcion larga viene vacia entonces no mostramos el txtarea
			if(descripcion=="" && imagen == ""){
				$grid_productos.find('#desc'+tr).hide();
				$grid_productos.find('.div_img'+tr).hide();
			}
			/*
			if(parseInt($num_lista_precio.val())<=0){
				idmonpre=$select_moneda.val();
			}
			*/
			
			//carga el select de monedas  con la moneda del cliente seleccionada por default
			$grid_productos.find('.monedagrid'+ tr).children().remove();
			var moneda_hmtl = '';
			$.each(arrayMonedas ,function(entryIndex,moneda){
				if( parseInt(moneda['id']) == parseInt(idmonpartida) ){
					moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion_abr'] + '</option>';
				}else{
					//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion_abr'] + '</option>';
				}
			});
			$grid_productos.find('.monedagrid'+ tr).append(moneda_hmtl);
			
			
			/*
			$grid_productos.find('.idMonGridAnt'+ tr).val(idmonpartida);
			
			
			$grid_productos.find('.moneda'+ tr).click(function(event){
				$grid_productos.find('.idMonGridAnt'+ tr).val($(this).val());
			});
			
			
			$grid_productos.find('.moneda'+ tr).change(function(){
				var idMonSeleccionado = $(this).val();
				var idMonAnterior = $grid_productos.find('.idMonGridAnt'+ tr).val();
				var $precioPartida = $grid_productos.find('.precio'+ tr);
				var $catidadPartida = $grid_productos.find('.cant'+ tr);
				
				var $importePartida = $grid_productos.find('.import'+ tr);
				var $importePartidaMonCot = $grid_productos.find('.impMonCot'+ tr);
				var $tasaIva = $grid_productos.find('.ivalorimp'+ tr);
				var $totalImpuestoPartida = $grid_productos.find('.totimp'+ tr);
				
				if($catidadPartida.val().trim()!='' && $precioPartida.val().trim()!=''){
					if(parseInt(idMonAnterior)==1 && parseInt(idMonSeleccionado)!=1){
						$precioPartida.val(parseFloat(parseFloat($precioPartida.val()) / parseFloat($tc.val())).toFixed(4));
					}else{
						if(parseInt(idMonAnterior)!=1 && parseInt(idMonSeleccionado)==1){
							$precioPartida.val(parseFloat(parseFloat($precioPartida.val()) * parseFloat($tc.val())).toFixed(4));
						}
					}
					
					$importePartida.val(parseFloat(parseFloat($precioPartida.val()) * parseFloat($catidadPartida.val())).toFixed(4));
					
					
					//Calcula el Importe de la partida en la Moneda Global de la Cotizacion, esto porque el total de la cotizacion
					//se saca tomando la moneda global y no la moneda de cada partida.
					
					if(parseInt($select_moneda.val()) == parseInt(idMonSeleccionado)){
						$importePartidaMonCot.val(parseFloat($importePartida.val()).toFixed(4));
					}else{
						if(parseInt($select_moneda.val())==1 && parseInt(idMonSeleccionado)!=1){
							$importePartidaMonCot.val( parseFloat($importePartida.val()) * parseFloat($tc.val()) );
						}else{
							if(parseInt($select_moneda.val())!=1 && parseInt(idMonSeleccionado)==1){
								$importePartidaMonCot.val( parseFloat($importePartida.val()) / parseFloat($tc.val()) );
							}
						}
					}
					
					$importePartidaMonCot.val(parseFloat($importePartidaMonCot.val()).toFixed(4));
					
					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					$totalImpuestoPartida.val( parseFloat(parseFloat( $importePartidaMonCot.val() ) * parseFloat(  $tasaIva.val()  ) ).toFixed(4));
					
					//Recalcular los totales
					$recalcula_totales();
				}
			});
			*/
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_productos.find('.cant'+ tr).focus(function(e){
				if($(this).val().trim() == ''){
					$(this).val('');
				}else{
					if(parseFloat($(this).val()) <=0 ){
						$(this).val('');
					}
				}
			});
			
			//Recalcula importe al perder enfoque el campo cantidad
			$grid_productos.find('.cant'+ tr).blur(function(){
				var $precioPartida = $(this).parent().parent().find('.precio'+ tr);
				var $importePartida = $(this).parent().parent().find('.import'+ tr);
				var $tasaIva = $(this).parent().parent().find('#ivalorimp');
				var $totalImpuestoPartida = $(this).parent().parent().find('#totimp');
				var $idMonedaPartida = $(this).parent().parent().find('.monedagrid'+ tr);
				
				//importe de la partida en la Moneda de la cotizacion
				//var $importePartidaMonCot = $(this).parent().parent().find('.impMonCot'+ tr);
				
				if ($(this).val().trim()==''){
					$(this).val(0);
				}
				
				if($(this).val().trim()!='' && $precioPartida.val().trim()!=''){
					//calcula el importe
					$importePartida.val(parseFloat($(this).val()) * parseFloat(quitar_comas($precioPartida.val())));
					
					$importePartida.val(parseFloat(quitar_comas($importePartida.val())).toFixed(4));
					
					/*
					Calcula el Importe de la partida en la Moneda Global de la Cotizacion, esto porque el total de la cotizacion
					se saca tomando la moneda global y no la moneda de cada partida.
					*/
					/*
					if(parseInt($select_moneda.val()) == parseInt($idMonedaPartida.val())){
						$importePartidaMonCot.val(parseFloat($importePartida.val()).toFixed(4));
					}else{
						if(parseInt($select_moneda.val())==1 && parseInt($idMonedaPartida.val())!=1){
							$importePartidaMonCot.val( parseFloat($importePartida.val()) * parseFloat($tc_usd.val()) );
						}else{
							if(parseInt($select_moneda.val())!=1 && parseInt($idMonedaPartida.val())==1){
								$importePartidaMonCot.val( parseFloat($importePartida.val()) / parseFloat($tc_usd.val()) );
							}
						}
					}
					
					$importePartidaMonCot.val(parseFloat($importePartidaMonCot.val()).toFixed(4));
					*/
					
					//calcula el impuesto para este producto multiplicando el importe por el valor del iva
					$totalImpuestoPartida.val( parseFloat(parseFloat( $importePartida.val() ) * parseFloat(  $tasaIva.val()  ) ).toFixed(4));
					//$totalImpuestoPartida.val( parseFloat(parseFloat( $importePartidaMonCot.val() ) * parseFloat(  $tasaIva.val()  ) ).toFixed(4));
				}else{
					$importePartida.val(parseFloat(0).toFixed(4));
					//$importePartidaMonCot.val('');
				}
				
				$recalcula_totales();
			});
			
			
			
			//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
			$grid_productos.find('.precio'+ tr).focus(function(e){
				$(this).val(quitar_comas($(this).val()));
				
				if($(this).val().trim()==''){
					$(this).val('');
				}else{
					if(parseFloat($(this).val()) <= 0 ){
						$(this).val('');
					}
				}
			});
			
			//recalcula importe al perder enfoque el campo costo
			$grid_productos.find('.precio'+ tr).blur(function(){
				var $catidadPartida = $(this).parent().parent().find('.cant'+ tr);
				var $importePartida = $(this).parent().parent().find('.import'+ tr);
				var $tasaIva = $(this).parent().parent().find('#ivalorimp');
				var $totalImpuestoPartida = $(this).parent().parent().find('#totimp');
				var $idMonedaListaPrecioPartida = $(this).parent().parent().find('.idMonLp'+ tr);
				var $precioOriginalPartida = $(this).parent().parent().find('.precor'+ tr);
				var precioCambiado=0.0;
				
				//importe de la partida en la Moneda de la cotizacion
				//var $importePartidaMonCot = $(this).parent().parent().find('.impMonCot'+ tr);
				
				//Quitar marca que indica que requiere autorizacion
				$(this).parent().parent().find('input[name=reqauth]').val('false');
				
				$(this).val(quitar_comas($(this).val()));
				
				if ($(this).val().trim()==''){
					$(this).val(0);
				}
				
				$(this).val(parseFloat($(this).val()).toFixed(4));
				
				if(parseFloat($(this).val()) > 0){
					//alert("moneda_original:"+$moneda_original.val()+ "		idMonedaPartida:"+$idMonedaPartida.val());
					//si la moneda inicial de la cotizacion es diferente a la moneda actual seleccionada
					//entonces recalculamos los precios de acuerdo al tipo de cambio
					if( parseInt($select_moneda.val()) != parseInt($idMonedaListaPrecioPartida.val()) ){
						
						if(parseInt($select_moneda.val())==1 && parseInt($idMonedaListaPrecioPartida.val())!=1){
							//si la moneda original es pesos, calculamos su equivalente a dolares
							precioCambiado = parseFloat( parseFloat($(this).val()) / parseFloat($tc_usd.val())).toFixed(4);
						}
						
						if(parseInt($select_moneda.val())!=1 && parseInt($idMonedaListaPrecioPartida.val())==1){
							//alert("precioOriginal:"+precioOriginal +"		tc_original:"+$tc_original.val());
							//si la moneda original es dolar y la moneda del precio es Pesos, calculamos su equivalente a dolar
							precioCambiado = parseFloat( parseFloat($(this).val()) * parseFloat($tc_usd.val()) ).toFixed(4);
						}
					}else{
						precioCambiado = $(this).val();
					}
				}else{
					precioCambiado = $(this).val();
				}
				
				$precioOriginalPartida.val(precioCambiado);
				
				
				
				//$(this).val(parseFloat($(this).val()).toFixed(2));
				
				if( $(this).val().trim()!='' && $catidadPartida.val().trim()!=''){
					//Calcular el importe y redondear el importe
					$importePartida.val( parseFloat(parseFloat($(this).val()) * parseFloat(quitar_comas($catidadPartida.val()))).toFixed(4));
					
					/*
					Calcula el Importe de la partida en la Moneda Global de la Cotizacion, esto porque el total de la cotizacion
					se saca tomando la moneda global y no la moneda de cada partida.
					*/
					/*
					if(parseInt($select_moneda.val()) == parseInt($idMonedaPartida.val())){
						$importePartidaMonCot.val(parseFloat($importePartida.val()).toFixed(4));
					}else{
						if(parseInt($select_moneda.val())==1 && parseInt($idMonedaPartida.val())!=1){
							$importePartidaMonCot.val( parseFloat($importePartida.val()) * parseFloat($tc_usd.val()) );
						}else{
							if(parseInt($select_moneda.val())!=1 && parseInt($idMonedaPartida.val())==1){
								$importePartidaMonCot.val( parseFloat($importePartida.val()) / parseFloat($tc_usd.val()) );
							}
						}
					}
					
					$importePartidaMonCot.val(parseFloat($importePartidaMonCot.val()).toFixed(4));
					*/
					
					//calcula el impuesto para este producto multiplicando el importe por la tasa del iva
					$totalImpuestoPartida.val( parseFloat( parseFloat($importePartida.val()) * parseFloat($tasaIva.val())).toFixed(4));
					//$totalImpuestoPartida.val( parseFloat(parseFloat( $importePartidaMonCot.val() ) * parseFloat(  $tasaIva.val()  ) ).toFixed(4));
				}else{
					$importePartida.val('0');
					//$importePartidaMonCot.val('');
				}
				
				$(this).val($(this).agregar_comas($(this).val()));
				$importePartida.val($(this).agregar_comas($importePartida.val()));
				
				$recalcula_totales();
			});
			
			//validar campo costo, solo acepte numeros y punto
			$grid_productos.find('.precio'+ tr).keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}
			});
			
			//validar campo cantidad, solo acepte numeros y punto
			$grid_productos.find('.cant'+ tr).keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}
			});
			
			
			//elimina un producto del grid
			//$grid_productos.find('#delete'+ tr).bind('click',function(event){
			$grid_productos.find('a[href=#delete'+ tr +']').click(function(e){
				e.preventDefault();
				if(parseInt($(this).parent().find('.elim'+ tr).val()) != 0){
					//tomamos el valor de la partida eliminada
					var iddetalle= $(this).parent().find('.iddetalle'+ tr).val();
					var numTr= $(this).parent().find('.notr'+ tr).val();
					
					//asigna espacios en blanco a todos los input de la fila eliminada
					$(this).parent().parent().find('input').val(' ');
					$(this).parent().parent().find('input[name=statusreg]').val('0&&&0&&&0');
					$(this).parent().parent().find('input[name=reqauth]').val('false');
					$(this).parent().parent().find('input[name=success]').val('false');
					
					//asigna un 0 al input eliminado como bandera para saber que esta eliminado
					$(this).parent().find('.elim'+ tr).val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
					$(this).parent().find('.iddetalle'+ tr).val(iddetalle);//le devolvemos el valor a la partida eliminada
					$(this).parent().find('.notr'+ tr).val(numTr);//le devolvemos el valor a la partida eliminada
					
					//oculta la fila eliminada
					$(this).parent().parent().hide();
					
					//recalcular los totales al eliminar un registro
					$recalcula_totales();
				}
			});
			
			$('#forma-cotizacions-window').find('input[name=sku_producto]').val('');
			$('#forma-cotizacions-window').find('input[name=nombre_producto]').val('');
			
			//asignar enfoque al campo cantidad que se acaba de agregar
			$grid_productos.find('.cant'+ tr).focus();
			
		}else{
			jAlert('El producto: '+sku+' con presentacion: '+pres+' ya se encuentra en el listado, seleccione otro diferente.', 'Atencion!', function(r) { 
				$('#forma-cotizacions-window').find('input[name=nombre_producto]').val('');
				$('#forma-cotizacions-window').find('input[name=sku_producto]').focus();
			});
		}
		
		
		$grid_productos.find('#eliminar'+ tr).mouseover(function(){
			$(this).removeClass("onmouseOutEliminar").addClass("onmouseOverEliminar");
		});
		$grid_productos.find('#eliminar'+ tr).mouseout(function(){
			$(this).removeClass("onmouseOverEliminar").addClass("onmouseOutEliminar");
		});
		
	}//termina agregar producto al grid
	
	
	
	
	
	$aplicar_evento_click_checkbox = function($campo_check){
		//click al checkbox descripcion larga
		$campo_check.click(function(event){
			if(this.checked){
				$('#forma-cotizacions-window').find('input[name=razoncliente]').css({'width':'555px'});
				$('#forma-cotizacions-window').find('input[name=dircliente]').css({'width':'550px'});
				$('#forma-cotizacions-window').find('input[name=contactocliente]').css({'width':'550px'});
				$('#forma-cotizacions-window').find('#td_imagen').show();
				$('#forma-cotizacions-window').find('#td_descripcion').show();
				$('#forma-cotizacions-window').find('#td1').css({'width':'740px'});
				$('#forma-cotizacions-window').find('#td2').css({'width':'460px'});
				$('#forma-cotizacions-window').find('.contenedor_grid').css({'width':'1180px'});
				$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'width':'1218px'});
				$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'width':'1218px'});
				$('#forma-cotizacions-window').find('.cotizacions_div_two').css({'width':'1218px'});
				$('#forma-cotizacions-window').find('.cotizacions_div_three').css({'width':'1208px'});
				$('#forma-cotizacions-window').css({"margin-left": -480, 	"margin-top": -230});
				$('#forma-cotizacions-window').find('#titulo_plugin').css({'width':'1178px'});
				$('#forma-cotizacions-window').find('#div_botones').css({'width':'1195px'});
				$('#forma-cotizacions-window').find('#div_botones').find('.tabla_botones').find('.td_left').css({'width':'1095px'});
			}else{
				$('#forma-cotizacions-window').find('input[name=razoncliente]').css({'width':'430px'});
				$('#forma-cotizacions-window').find('input[name=dircliente]').css({'width':'430px'});
				$('#forma-cotizacions-window').find('input[name=contactocliente]').css({'width':'430px'});
				$('#forma-cotizacions-window').find('#td_imagen').hide();
				$('#forma-cotizacions-window').find('#td_descripcion').hide();
				$('#forma-cotizacions-window').find('#td1').css({'width':'520px'});
				$('#forma-cotizacions-window').find('#td2').css({'width':'350px'});
				$('#forma-cotizacions-window').find('.contenedor_grid').css({'width':'860px'});
				$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'width':'893px'});
				$('#forma-cotizacions-window').find('.cotizacions_div_two').css({'width':'893px'});
				$('#forma-cotizacions-window').find('.cotizacions_div_three').css({'width':'880px'});
				$('#forma-cotizacions-window').css({"margin-left": -320, 	"margin-top": -230});
				$('#forma-cotizacions-window').find('#titulo_plugin').css({'width':'853px'});
				$('#forma-cotizacions-window').find('#div_botones').css({'width':'870px'});
				$('#forma-cotizacions-window').find('#div_botones').find('.tabla_botones').find('.td_left').css({'width':'770px'});
			}
		});
	}
	
	
	
	$aplicar_evento_click_checkbox_incluye_iva = function($campo_check, incluyeCrm){
		//click al checkbox descripcion larga
		$campo_check.click(function(event){
			if(this.checked){
				$('#forma-cotizacions-window').find('.cotizacions_div_one').find('#tabla_totales').show();
				if(incluyeCrm=='true'){
					$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'565px'});
				}else{
					$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'595px'});
				}
			}else{
				$('#forma-cotizacions-window').find('.cotizacions_div_one').find('#tabla_totales').hide();
				if(incluyeCrm=='true'){
					$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'495px'});
				}else{
					$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'495px'});
				}
			}
		});
	}
	
	
	
	
	//calcula totales(subtotal, impuesto, total)
	$recalcula_totales = function(){
		var $campo_subtotal = $('#forma-cotizacions-window').find('input[name=subtotal]');
		var $campo_impuesto = $('#forma-cotizacions-window').find('input[name=impuesto]');
		var $campo_total = $('#forma-cotizacions-window').find('input[name=total]');
		var $valor_impuesto = $('#forma-cotizacions-window').find('input[name=valorimpuesto]');
		var $grid_productos = $('#forma-cotizacions-window').find('#grid_productos');
		
		var sumaSubTotal = 0; //es la suma de todos los importes
		var sumaImpuesto = 0; //valor del iva
		var sumaTotal = 0; //suma del subtotal + totalImpuesto
		
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		$grid_productos.find('tr').each(function (index){
			if(( $(this).find('#cost').val().trim() != '') && ( $(this).find('#cant').val().trim() != '' )){
				//acumula los importes en la variable subtotal
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat( quitar_comas( $(this).find('#import').val() ) );
				
				//aqui se suma el importe en la moneda global de la cotizacion
				//sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat( quitar_comas( $(this).find('#impMonCot').val() ) );
				
				if($(this).find('#totimp').val() != ''){
					sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
		});
		
		//calcula el total sumando el subtotal y el impuesto
		sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
		
		//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
		$campo_subtotal.val( $(this).agregar_comas( parseFloat(sumaSubTotal).toFixed(2) ) );
		//redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val( $(this).agregar_comas( parseFloat(sumaImpuesto).toFixed(2) ) );
		//redondea a dos digitos la suma  total y se asigna al campo total
		//$campo_total.val( $(this).agregar_comas( Math.round(parseFloat(sumaTotal)*100)/100) );
		$campo_total.val( $(this).agregar_comas( parseFloat(sumaTotal).toFixed(2) ) );
	}//termina calcular totales
    
	
	
	
    //convertir costos en dolar y pesos
	$convertir_costos = function(moneda_id, arrayMonedas){
		var $campo_subtotal = $('#forma-cotizacions-window').find('input[name=subtotal]');
		var $campo_impuesto = $('#forma-cotizacions-window').find('input[name=impuesto]');
		var $campo_total = $('#forma-cotizacions-window').find('input[name=total]');
		var $tipo_cambio = $('#forma-cotizacions-window').find('input[name=tc]');
		var $tc_usd = $('#forma-cotizacions-window').find('input[name=tc_usd]');
		var $valor_impuesto = $('#forma-cotizacions-window').find('input[name=valorimpuesto]');
		var $grid_productos = $('#forma-cotizacions-window').find('#grid_productos');
		var $select_moneda = $('#forma-cotizacions-window').find('select[name=moneda]');
		
		var sumaSubTotal = 0; //es la suma de todos los importes
		var sumaImpuesto = 0; //valor del iva
		var sumaTotal = 0; //suma del subtotal + totalImpuesto
		
		//si  el campo tipo de cambio es null o vacio, se le asigna un 0
		if( $valor_impuesto.val()== null || $valor_impuesto.val()== ''){
			$valor_impuesto.val(0);
		}
		
		$grid_productos.find('tr').each(function (index){
			var precio_cambiado=0;
			var importe_cambiado=0;
			var $tasaIva = $(this).find('#ivalorimp');
			var $totalImpuestoPartida = $(this).find('#totimp');
			var $idMonedaPartida = $(this).find('select[name=monedagrid]');
			var $precioPartida = $(this).find('input[name=precio]');
			var $importePartida = $(this).find('input[name=importe]');
			
			if(( $(this).find('#cost').val().trim() != '') && ( $(this).find('#cant').val().trim() != '' )){
				//alert("idMonedaPartida: "+$idMonedaPartida.val()+"    moneda_id: "+moneda_id);
				//importe_cambiado = quitar_comas($importePartida.val());
				
				if( parseInt($idMonedaPartida.val()) != parseInt(moneda_id) ){
					if(parseInt($idMonedaPartida.val())==1 && parseInt(moneda_id)!=1){
						//si la moneda original es pesos, calculamos su equivalente a dolares
						precio_cambiado = parseFloat(quitar_comas($precioPartida.val())) / parseFloat($tc_usd.val());
					}
					
					if(parseInt($idMonedaPartida.val())!=1 && parseInt(moneda_id)==1){
						//si la moneda original es dolar, calculamos su equivalente a pesos
						precio_cambiado = parseFloat(quitar_comas($precioPartida.val())) * parseFloat($tc_usd.val());
					}
					
					$precioPartida.val($(this).agregar_comas(parseFloat(precio_cambiado).toFixed(4)));
					
					//calcula el nuevo importe
					importe_cambiado = parseFloat($(this).find('#cant').val()) * parseFloat(precio_cambiado).toFixed(4);
					
					//asignamos el nuevo laor del importe
					$importePartida.val(parseFloat(importe_cambiado).toFixed(4));
				}
				
				$importePartida.val(parseFloat($importePartida.val()).toFixed(4));
				
				//calcula el impuesto para este producto multiplicando el importe por el valor del iva
				//$totalImpuestoPartida.val( parseFloat(parseFloat( $importePartida.val() ) * parseFloat(  $tasaIva.val()  ) ).toFixed(4));
				$totalImpuestoPartida.val( parseFloat(parseFloat( $importePartida.val() ) * parseFloat(  $tasaIva.val()  ) ).toFixed(4));
				
				//acumula los importes en la variable subtotal
				//sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat(quitar_comas($(this).find('#import').val()));
				sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat($importePartida.val());
				
				if($(this).find('#totimp').val() != ''){
					//$(this).find('#totimp').val(parseFloat( quitar_comas($(this).find('#import').val()) ) * parseFloat($tasaIva.val()));
					$(this).find('#totimp').val(parseFloat( quitar_comas($importePartida.val()) ) * parseFloat($tasaIva.val()));
					sumaImpuesto =  parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
				}
			}
			
			$importePartida.val($(this).agregar_comas(parseFloat($importePartida.val()).toFixed(4) ) );
			
			//cambiar la moneda de las partidas del grid al cambiar la moneda
			$grid_productos.find('select[name=monedagrid]').children().remove();
			var moneda_grid_hmtl = '';
			$.each(arrayMonedas,function(entryIndex,moneda){
				if(parseInt(moneda_id) == parseInt(moneda['id'])){
					moneda_grid_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion_abr'] + '</option>';
				}
			});
			$grid_productos.find('select[name=monedagrid]').append(moneda_grid_hmtl);
			
		});
		
		//calcula el total sumando el subtotal y el impuesto
		sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImpuesto);
		//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
		$campo_subtotal.val($(this).agregar_comas(parseFloat(sumaSubTotal).toFixed(2)));
		//redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$campo_impuesto.val($(this).agregar_comas(parseFloat(sumaImpuesto).toFixed(2)));
		//redondea a dos digitos la suma  total y se asigna al campo total
		$campo_total.val($(this).agregar_comas(parseFloat(sumaTotal).toFixed(2)));

	}//termina convertir dolar pesos


    
    
	//Carga combo de Tipos de Estatus de Remisiones IMSS
        $loadSelectStatusRemisionesIMSS = function(index) {
            var idx = index!=null?index:0;
            var $select_statusRemisionIMSS = $('#forma-cotizacions-window').find('select[name=statusRemisionIMSS]');
            //alert("Entrando a carga de select remisiones IMSS");
            try {
                var input_json_tipos = document.location.protocol + '//' + document.location.host + '/'+controller+'/getStatusRemisionIMSS.json';                
                $.post(input_json_tipos,function(data){
                        //Carga el select con tipos de estatus
                        //alert("idx="+idx);
                        $select_statusRemisionIMSS.children().remove();
                        var prod_tipos_html = "";
                        var selectedStr = "selected='yes'";
                        if (idx==0) {
                            //alert("Cero");
                            prod_tipos_html = '<option value="0" '+selectedStr+'>[--Seleccionar Tipo--]</option>';
                        }else{
                            prod_tipos_html = '<option value="0">[--Seleccionar Tipo--]</option>';
                        }
                        
                        $.each(data,function(entryIndex,pt){
                            prod_tipos_html += '<option value="' + pt['id'] + '" '+(idx==pt['id']?selectedStr:"")+'>' + pt['descripcion'] + '</option>';
                                /*if(parseInt(pt['id'])==1){
                                        prod_tipos_html += '<option value="' + pt['id'] + '" selected="yes">' + pt['titulo'] + '</option>';
                                }else{
                                        prod_tipos_html += '<option value="' + pt['id'] + '" >' + pt['titulo'] + '</option>';
                                }*/
                        });
                        $select_statusRemisionIMSS.append(prod_tipos_html);
                });
            }catch(err){
                alert("Error:"+err);
            }
        }
	
	
	
	//obtener el tipo de cambio de acuerdo al id de la moneda
	getTcIdMoneda = function(idMoneda, arrayMonedas){
		var $tipo_cambio = $('#forma-cotizacions-window').find('input[name=tc]');
		//var $tc_original = $('#forma-cotizacions-window').find('input[name=tc_original]');
		
		var inputJsonTc = document.location.protocol + '//' + document.location.host + '/'+controller+'/getValorTc.json';
		var $arreglo2 = {'idmon':idMoneda };
		
		$.post(inputJsonTc,$arreglo2,function(entry){
			$tipo_cambio.val(entry['valor']);
			
			$convertir_costos(idMoneda, arrayMonedas);
		},"json");//termina llamada json
	}
	
	
	
	//vencido
	
	//habilitar y deshabilitar campos
	$habilitarDeshabilitarCampos = function(accion, $select_tipo_cotizacion, $folio, $nocontrolcliente, $razon_cliente, $observaciones, $select_moneda, $tc, $fecha, $vigencia, $select_agente, $select_incoterms, $sku_producto, $nombre_producto, $busca_sku, $agregar_producto, $grid_productos, $subtotal, $impuesto, $total, $submit_actualizar, $check_descripcion_larga, $check_incluye_iva){
		if(accion=='deshabilitar'){
			$select_tipo_cotizacion.attr('disabled','-1');
			$folio.attr('disabled','-1');
			$nocontrolcliente.attr('disabled','-1');
			$razon_cliente.attr('disabled','-1');
			$observaciones.attr('disabled','-1');
			$select_moneda.attr('disabled','-1');
			$tc.attr('disabled','-1');
			$fecha.attr('disabled','-1');
			$vigencia.attr('disabled','-1');
			$select_agente.attr('disabled','-1');
			$check_descripcion_larga.attr('disabled','-1');
			$check_incluye_iva.attr('disabled','-1');
			$select_incoterms.attr('disabled','-1');
			$sku_producto.attr('disabled','-1');
			$nombre_producto.attr('disabled','-1');
			$busca_sku.hide();
			$agregar_producto.hide();
			$grid_productos.find('input').attr('disabled','-1');
			$grid_productos.find('select').attr('disabled','-1');
			$grid_productos.find('a').hide();
			$subtotal.attr('disabled','-1');
			$impuesto.attr('disabled','-1');
			$total.attr('disabled','-1');
			$submit_actualizar.hide();
		}else{
			$select_tipo_cotizacion.removeAttr('disabled');
			$folio.removeAttr('disabled');
			$nocontrolcliente.removeAttr('disabled');
			$razon_cliente.removeAttr('disabled');
			$observaciones.removeAttr('disabled');
			$select_moneda.removeAttr('disabled');
			$tc.removeAttr('disabled');
			$fecha.removeAttr('disabled');
			$vigencia.removeAttr('disabled');
			$select_agente.removeAttr('disabled');
			$check_descripcion_larga.removeAttr('disabled');
			$check_incluye_iva.removeAttr('disabled');
			$select_incoterms.removeAttr('disabled');
			$sku_producto.removeAttr('disabled');
			$nombre_producto.removeAttr('disabled');
			$busca_sku.show();
			$agregar_producto.show();
			$grid_productos.find('input').removeAttr('disabled');
			$grid_productos.find('select').removeAttr('disabled');
			$grid_productos.find('a').show();
			$subtotal.removeAttr('disabled');
			$impuesto.removeAttr('disabled');
			$total.removeAttr('disabled');
			$submit_actualizar.show();
		}
	}//termina habilitar y deshabilitar campos
	
	
	
	
	$forma_autorizacion= function($grid_productos, $btn_autorizar,id_to_show){
		$(this).modalPanel_authorize();
		var form_to_show = 'formaauthorize';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		$('#forma-authorize-window').css({"margin-left": -40,"margin-top": -180});
		$forma_selected.prependTo('#forma-authorize-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		var $idauth = $('#forma-authorize-window').find('input[name=idauth]');
		var $passauth = $('#forma-authorize-window').find('input[name=passauth]');
		
		var $boton_aceptar = $('#forma-authorize-window').find('#boton_aceptar_validacion');
		var $cancelar_plugin = $('#forma-authorize-window').find('#boton_cancelar_validacion');
		
		
		$boton_aceptar.click(function(event){
			if($idauth.val().trim()!="" && $passauth.val().trim()!=""){
				var cadena = 'idauth='+$idauth.val()+'|passauth='+$passauth.val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAuth.json';
				$arreglo = {'cad':cadena.toCharCode(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() }
				
				$.post(input_json,$arreglo,function(entry){
					if(entry['Data']['success'].trim()=='true'){
						//Eliminar formulario de autorizacion
						var remove = function() {$(this).remove();};
						$('#forma-authorize-overlay').fadeOut(remove);
						
						//Ocultar boton de autorizar
						$('#forma-cotizacions-window').find('#btn_autorizar').hide();
						
						var cont_check=0;
						$grid_productos.find('input[name=checkauth]').each(function(index){
							$tr = $(this).parent().parent();
							
							if(this.checked){
								if(parseInt($tr.find('input[name=eliminado]').val())==1){
									$tr.find('input[name=statusreg]').val('1&&&'+quitar_comas($tr.find('input[name=precio]').val())+'&&&'+entry['Data']['ident']);
									$tr.find('input[name=reqauth]').val('true');
									$tr.find('input[name=precio]').css({'background':'#ffffff'});
									//cont_check++;
								}
							}
						});
						
						/*
						if(parseInt(cont_check)<=0){
							$('#forma-cotizacions-window').find('#btn_autorizar').hide();
						}
						*/
						
						jAlert('Precios autorizados!', 'Atencion!', function(r) {
							
						});
					}else{
						jAlert('Error al intentar autorizar. EL usuario debe tener permiso para autorizar precios.', 'Atencion!', function(r) {
							//var remove = function() {$(this).remove();};
							//$('#forma-authorize-overlay').fadeOut(remove);
							$idauth.focus();
						});
					}
					
				});//termina llamada json
			}else{
				if($idauth.val().trim()=="" && $passauth.val().trim()==""){
					jAlert('Ingresar nombre de Usuario y Password.', 'Atencion!', function(r) {
						$idauth.focus();
					});
				}else{
					if($idauth.val().trim()==""){
						jAlert('Ingresar nombre de Usuario.', 'Atencion!', function(r) {
							$idauth.focus(); 
						});
					}else{
						if($passauth.val().trim()==""){
							jAlert('Ingresar Password.', 'Atencion!', function(r) {
								$passauth.focus(); 
							});
						}
					}
				}
			}
		});
		
		
		
		$(this).aplicarEventoKeypressEjecutaTrigger($idauth, $boton_aceptar);
		$(this).aplicarEventoKeypressEjecutaTrigger($passauth, $boton_aceptar);
		
		//Ligamos el boton cancelar al evento click para eliminar la forma
		$cancelar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-authorize-overlay').fadeOut(remove);
		});
		
		$idauth.focus();
	}
	
	
	
	
	//nueva cotizacion
	$new_cotizacion.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_cotizacions();
		
		var form_to_show = 'formaCotizacions00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-cotizacions-window').css({"margin-left": -480, 	"margin-top": -230});
		
		$forma_selected.prependTo('#forma-cotizacions-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCotizacion.json';
		var $arreglo = {'id_cotizacion':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
                
		var $tr_tipo = $('#forma-cotizacions-window').find('#tr_tipo');
		var $td_imagen = $('#forma-cotizacions-window').find('#td_imagen');
		var $td_descripcion = $('#forma-cotizacions-window').find('#td_descripcion');
		var $td1 = $('#forma-cotizacions-window').find('#td1');
		var $td2 = $('#forma-cotizacions-window').find('#td2');
		var $check_descripcion_larga =$('#forma-cotizacions-window').find('input[name=check_descripcion_larga]');
		var $check_incluye_iva =$('#forma-cotizacions-window').find('input[name=check_incluye_iva]');
		var $contenedor_grid = $('#forma-cotizacions-window').find('.contenedor_grid');
		
		var $id_cotizacion = $('#forma-cotizacions-window').find('input[name=id_cotizacion]');
		var $total_tr = $('#forma-cotizacions-window').find('input[name=total_tr]');
		var $select_tipo_cotizacion = $('#forma-cotizacions-window').find('select[name=select_tipo_cotizacion]');
		var $folio = $('#forma-cotizacions-window').find('input[name=folio]');
		
		var $etiqueta_accion = $('#forma-cotizacions-window').find('#etiqueta_accion');
		var $select_accion = $('#forma-cotizacions-window').find('select[name=select_accion]');
		
		var $busca_cliente = $('#forma-cotizacions-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-cotizacions-window').find('input[name=id_cliente]');
		var $rfc_cliente = $('#forma-cotizacions-window').find('input[name=rfccliente]');
		var $nocontrolcliente = $('#forma-cotizacions-window').find('input[name=nocontrolcliente]');
		var $razon_cliente = $('#forma-cotizacions-window').find('input[name=razoncliente]');
		var $dir_cliente = $('#forma-cotizacions-window').find('input[name=dircliente]');
		var $contactocliente = $('#forma-cotizacions-window').find('input[name=contactocliente]');
		var $select_moneda = $('#forma-cotizacions-window').find('select[name=moneda]');
		var $select_moneda_original = $('#forma-cotizacions-window').find('select[name=moneda2]');
		var $tc = $('#forma-cotizacions-window').find('input[name=tc]');
		//var $tc_original = $('#forma-cotizacions-window').find('input[name=tc_original]');
		var $tc_usd = $('#forma-cotizacions-window').find('input[name=tc_usd]');
		var $tc_usd_sat = $('#forma-cotizacions-window').find('input[name=tc_usd_sat]');
		
		var $fecha = $('#forma-cotizacions-window').find('input[name=fecha]');
                var $fecha2 = $('#forma-cotizacions-window').find('input[name=fecha2]');
		var $vigencia = $('#forma-cotizacions-window').find('input[name=vigencia]');
		var $select_agente = $('#forma-cotizacions-window').find('select[name=select_agente]');
		var $select_incoterms = $('#forma-cotizacions-window').find('select[name=select_incoterms]');
                var $select_statusRemisionIMSS = $('#forma-cotizacions-window').find('select[name=statusRemisionIMSS]');
		
		//var $campo_tc = $('#forma-cotizacions-window').find('input[name=tc]');
		var $id_impuesto = $('#forma-cotizacions-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-cotizacions-window').find('input[name=valorimpuesto]');
		var $observaciones = $('#forma-cotizacions-window').find('textarea[name=observaciones]');
		
		//var $select_almacen = $('#forma-cotizacions-window').find('select[name=almacen]');
		var $sku_producto = $('#forma-cotizacions-window').find('input[name=sku_producto]');
		var $nombre_producto = $('#forma-cotizacions-window').find('input[name=nombre_producto]');
		
		//buscar producto
		var $busca_sku = $('#forma-cotizacions-window').find('a[href*=busca_sku]');
		//href para agregar producto al grid
		var $agregar_producto = $('#forma-cotizacions-window').find('a[href*=agregar_producto]');
		
		var $btn_autorizar = $('#forma-cotizacions-window').find('#btn_autorizar');
		var $boton_genera_pdf = $('#forma-cotizacions-window').find('#genera_pdf');
		
		//grid de productos
		var $grid_productos = $('#forma-cotizacions-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning');
		
		//var $flete = $('#forma-cotizacions-window').find('input[name=flete]');
		var $subtotal = $('#forma-cotizacions-window').find('input[name=subtotal]');
		var $impuesto = $('#forma-cotizacions-window').find('input[name=impuesto]');
		var $total = $('#forma-cotizacions-window').find('input[name=total]');
		
		
		var $cerrar_plugin = $('#forma-cotizacions-window').find('#close');
		var $cancelar_plugin = $('#forma-cotizacions-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-cotizacions-window').find('#submit');
		
		
		$id_cotizacion.val(0);//para nueva cotizacion el folio es 0
		$vigencia.val(1);
		$select_moneda_original.hide();
		
		//quitar enter a todos los campos input
		$('#forma-cotizacions-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		//$campo_factura.css({'background' : '#ffffff'});
		
		//ocultar boton de generar pdf. Solo debe estar activo en editar
		$boton_genera_pdf.hide();
		$etiqueta_accion.hide();
		$select_accion.hide();
		$btn_autorizar.hide();
		
		//$descripcion_larga.hide();
		$tr_tipo.hide();
		$tc.attr('readonly',true);
		$dir_cliente.attr('readonly',true);
		$folio.css({'background' : '#F0F0F0'});
		$dir_cliente.css({'background' : '#F0F0F0'});
		$contactocliente.css({'background' : '#F0F0F0'});
		//$fecha.css({'background' : '#F0F0F0'});
                
                //Carga select Estatus de Remisiones IMSS
                $loadSelectStatusRemisionesIMSS(0);
		
		$fecha.val(mostrarFecha());//mostrar la fecha actual
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
				/*if (formated.match(patron) ){
					var valida_fecha=mayor($fecha.val(),mostrarFecha());
					
					if (valida_fecha==true){
						$fecha.DatePickerHide();
					}else{
						jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
						$fecha.val(mostrarFecha());
					}
				}*/
                            $fecha.DatePickerHide();
			}
		});
		
                //NLE
                try {
                    $fecha2.val(mostrarFecha());
                    $fecha2.click(function (s){
                            var a=$('div.datepicker');
                            a.css({'z-index':100});
                    });
                    $fecha2.DatePicker({
                            format:'Y-m-d',
                            date: $fecha2.val(),
                            current: $fecha2.val(),
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
                                    $fecha2.val(formated);
                                    /*if (formated.match(patron) ){
                                            var valida_fecha=mayor($fecha2.val(),mostrarFecha());

                                            if (valida_fecha==true){
                                                    $fecha2.DatePickerHide();
                                            }else{
                                                    jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
                                                    $fecha2.val(mostrarFecha());
                                            }
                                    }*/
                                $fecha2.DatePickerHide();
                            }
                    });
                }catch(err){
                    alert("Error en DatePicker:"+err);
                }

                
                
		//seleccionar por default
		$check_incluye_iva.attr('checked', true );
		
		$('#forma-cotizacions-window').find('input[name=razoncliente]').css({'width':'430px'});
		$('#forma-cotizacions-window').find('input[name=dircliente]').css({'width':'430px'});
		$('#forma-cotizacions-window').find('input[name=contactocliente]').css({'width':'430px'});
		$('#forma-cotizacions-window').find('#td_imagen').hide();
		$('#forma-cotizacions-window').find('#td_descripcion').hide();
		$('#forma-cotizacions-window').find('#td1').css({'width':'520px'});
		$('#forma-cotizacions-window').find('#td2').css({'width':'350px'});
		$('#forma-cotizacions-window').find('.contenedor_grid').css({'width':'860px'});
		$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'width':'893px'});
		$('#forma-cotizacions-window').find('.cotizacions_div_two').css({'width':'893px'});
		$('#forma-cotizacions-window').find('.cotizacions_div_three').css({'width':'880px'});
		$('#forma-cotizacions-window').css({"margin-left": -320, 	"margin-top": -230});
		$('#forma-cotizacions-window').find('#titulo_plugin').css({'width':'853px'});
		$('#forma-cotizacions-window').find('#div_botones').css({'width':'870px'});
		$('#forma-cotizacions-window').find('#div_botones').find('.tabla_botones').find('.td_left').css({'width':'770px'});
		
		$nocontrolcliente.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La cotizaci&oacute;n se guard&oacute; con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-cotizacions-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				//$('#forma-cotizacions-window').find('.div_one').css({'height':'545px'});//sin errores
				$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'600px'});//con errores
				$('#forma-cotizacions-window').find('div.interrogacion').css({'display':'none'});
				$grid_productos.find('input[name=reqauth]').val('false');
				var contador_alert=0;
				
				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});
				
				$('#forma-cotizacions-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-cotizacions-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
						
						//alert(tmp.split(':')[0]);
						var campo = tmp.split(':')[0];
						var $campo_input;
						
						if((tmp.split(':')[0].substring(0, 4) == 'cant') || (tmp.split(':')[0].substring(0, 6) == 'precio')){
							$('#forma-cotizacions-window').find('#div_warning_grid').css({'display':'block'});
							$campo_input = $grid_productos.find('.'+campo);
							$campo_input.css({'background' : '#d41000'});
							$campo_input.focus();
							
							var codigo_producto = $campo_input.parent().parent().find('input[name=sku]').val();
							var titulo_producto = $campo_input.parent().parent().find('input[name=nombre]').val();
							
							var tr_warning = '<tr>';
								tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
								tr_warning += '<td width="120"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:116px; color:red"></td>';
								tr_warning += '<td width="200"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:196px; color:red"></td>';
								tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:225px; color:red"></td>';
							tr_warning += '</tr>';
							
							$('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
						}
						
						
						if(tmp.split(':')[0].substring(0,9)=='checkauth'){
							//alert(tmp);
							$grid_productos.find('.'+tmp.split(':')[0]).show();
							$grid_productos.find('.'+tmp.split(':')[0]).parent().find('input[name=reqauth]').val('true');
							
							if(parseInt(contador_alert)<=0){
								//Confirm para guardar sin autorizacion
								jConfirm('Hay precios que necesitan autorizaci&oacute;n.<br>&iquest;Desea guardar para autorizar despu&eacute;s&#63;', 'Dialogo de Confirmacion', function(r) {
									// If they confirmed, manually trigger a form submission
									if (r) {
										//Asignar los estatus correspondientes para permitir guardar sin autorizacion
										$grid_productos.find('input[name=checkauth]').each(function(index){
											$tr = $(this).parent().parent();
											if(parseInt($tr.find('input[name=eliminado]').val())==1){
												$tr.find('input[name=success]').val('true');
											}
										});
										
										//Ejecutar el submit de actualizar
                                                                                //alert("Ejecutar el submit de actualizar");
										$submit_actualizar.trigger('click');
									}
								});
							}
							
							contador_alert++;
						}
					}
				}
				
				$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
				$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			//Almacenar el arreglo de unidades de medida en la variable
			arrayUM = entry['UM'];
			
			//Almacenar valor para variable que indica si se debe permitir el cambio de la unidad de medida
			cambiarUM = entry['Extras'][0]['cambioUM'];
			
			//carga select de incoterms
			$select_incoterms.children().remove();
			var incoterms_hmtl = '';
			$.each(entry['Incoterms'],function(entryIndex,incoterm){
				//incoterms_hmtl += '<option value="' + incoterm['id'] + '"  >' + incoterm['titulo'] + '</option>';
				incoterms_hmtl += incoterm['opcion_select'];
			});
			$select_incoterms.append(incoterms_hmtl);
			
			if(entry['Extras'][0]['mod_crm']=='true'){
				$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'565px'});
				$tr_tipo.show();//mostrar tr para escoger el tipo destino de la cotizacion
			}
			$aplicar_evento_click_checkbox_incluye_iva($check_incluye_iva, entry['Extras'][0]['mod_crm']);
			
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			$select_moneda_original.append(moneda_hmtl);//este  esta oculto
			
			$id_impuesto.val(entry['iva']['0']['id_impuesto']);
			$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
			
			//carga select de agentes
			$select_agente.children().remove();
			var agen_hmtl = '';
			$.each(entry['Agentes'],function(entryIndex,agen){
				agen_hmtl += '<option value="' + agen['id'] + '"  >' + agen['nombre_agente'] + '</option>';
			});
			$select_agente.append(agen_hmtl);
			
			//buscador de clientes
			$busca_cliente.click(function(event){
				event.preventDefault();
				$busca_clientes($select_tipo_cotizacion.val(), $nocontrolcliente.val(), $razon_cliente.val(), entry['Monedas'], entry['Agentes']);
			});
			
			//agregar producto al grid
			$agregar_producto.click(function(event){
				event.preventDefault();
				$buscador_presentaciones_producto($id_cliente.val(), $rfc_cliente.val(), $sku_producto.val(),$nombre_producto, $grid_productos, entry['Monedas'], $select_moneda, $tc_usd);
			});
			
			
			//busca datos del cliente al pulsar enter sobre en campo numero de control
			$nocontrolcliente.keypress(function(e){
				if(e.which == 13){
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoControl.json';
					$arreglo2 = {'tipo':$select_tipo_cotizacion.val(), 'no_control':$nocontrolcliente.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					
					$.post(input_json2,$arreglo2,function(entry2){
						if(parseInt(entry2['Resultado'].length) > 0 ){
							var num_lista_precio = entry2['Resultado'][0]['lista_precio'];
							var id_cliente = entry2['Resultado'][0]['id'];
							var numero_control = entry2['Resultado'][0]['numero_control'];
							var razon_social_cliente = entry2['Resultado'][0]['razon_social'];
							var dir_cliente = entry2['Resultado'][0]['direccion'];
							var id_moneda_cliente = entry2['Resultado'][0]['moneda_id'];
							var id_agente = entry2['Resultado'][0]['cxc_agen_id'];
							var rfc_cliente = entry2['Resultado'][0]['rfc'];
							var contacto_cliente = entry2['Resultado'][0]['contacto'];
							
							$agregarDatosClienteSeleccionado(entry['Monedas'], entry['Agentes'], num_lista_precio, id_cliente, numero_control, rfc_cliente, razon_social_cliente, dir_cliente, contacto_cliente, id_moneda_cliente, id_agente);
							
						}else{
							//limpiar campos
							$('#forma-cotizacions-window').find('input[name=id_cliente]').val('');
							$('#forma-cotizacions-window').find('input[name=nocontrolcliente]').val('');
							$('#forma-cotizacions-window').find('input[name=rfccliente]').val('');
							$('#forma-cotizacions-window').find('input[name=razoncliente]').val('');
							$('#forma-cotizacions-window').find('input[name=dircliente]').val('');
							$('#forma-cotizacions-window').find('input[name=contactocliente]').val('');
							$('#forma-cotizacions-window').find('input[name=num_lista_precio]').val('');
							
							jAlert('N&uacute;mero de cliente desconocido.', 'Atencion!', function(r) { 
								$nocontrolcliente.focus(); 
							});
						}
					},"json");//termina llamada json
					
					return false;
				}
			});
			
			//Aqui se pone el tipo de cambio de acuerdo a la Moneda seleccionada
			if(parseInt($select_moneda.val())==1){
				$tc.val(parseFloat(1).toFixed(4));
			}else{
				$tc.val(parseFloat(entry['Tc'][0]['tipo_cambio']).toFixed(4));
			}
			
			$tc_usd.val(parseFloat(entry['Tc'][0]['tipo_cambio']).toFixed(4));
			$tc_usd_sat.val(parseFloat(entry['Tc'][0]['tipo_cambio']).toFixed(4));
			
			//Cambiar moneda
			$select_moneda.change(function(){
				var idMonSeleccionado = $(this).val();
				
				//ejecutar funcion al cambiar la moneda
				getTcIdMoneda(idMonSeleccionado, entry['Monedas']);
			});
			
			
			//aplicar multiselect
			$select_incoterms.multiselect();
			
		},"json");//termina llamada json
		

		
		//carga select denominacion con todas las monedas
		$select_accion.children().remove();
		var accion_hmtl = '<option value="new">Nuevo</option>';
		//accion_hmtl = '<option value="edit">Actualizar</option>';
		$select_accion.append(accion_hmtl);
		
		
		//buscador de productos
		$busca_sku.click(function(event){
			event.preventDefault();
			$busca_productos($sku_producto.val(), $nombre_producto.val());
		});
		
		
		//Aplicar tipo de cambio a todos los precios al cambiar valor de tipo de cambio
		$tc_usd.blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			
			if(parseFloat($(this).val())>=parseFloat($tc_usd_sat.val())){
				$grid_productos.find('tr').each(function (index){
					var precio_cambiado=0;
					var importe_cambiado=0;
					var $idMonLpPartida = $(this).find('input[name=idMonLp]');
					var $precioOriginalListaPrecio = $(this).find('#precor');
					var $precioPartida = $(this).find('input[name=precio]');
					var $importePartida = $(this).find('input[name=importe]');
					
					if($precioPartida.val().trim()!=''){
						if(parseFloat($precioPartida.val().trim())>0){
							//si la moneda inicial de la cotizacion es diferente a la moneda actual seleccionada
							//entonces recalculamos los precios de acuerdo al tipo de cambio
							if( parseInt($idMonLpPartida.val()) != parseInt($select_moneda.val()) ){
								if(parseInt($idMonLpPartida.val())==1 && parseInt($select_moneda.val())!=1){
									//Si la moneda original es pesos, calculamos su equivalente a dolares
									precio_cambiado = parseFloat($precioOriginalListaPrecio.val()) / parseFloat($tc_usd.val());
								}
								
								if(parseInt($idMonLpPartida.val())!=1 && parseInt($select_moneda.val())==1){
									//Si la moneda original es dolar, calculamos su equivalente a pesos
									precio_cambiado = parseFloat($precioOriginalListaPrecio.val()) * parseFloat($tc_usd.val());
								}
								
								precio_cambiado = parseFloat(precio_cambiado).toFixed(4);
								
								$(this).find('#cost').val($(this).agregar_comas(precio_cambiado));
								
								importe_cambiado = parseFloat(parseFloat($(this).find('#cant').val()) * parseFloat(precio_cambiado)).toFixed(4);
								
								$(this).find('#import').val($(this).agregar_comas(importe_cambiado));
							}
						}
					}
				});
				//Llamada a la funcion que calcula totales
				$recalcula_totales();
			}else{
				jAlert('El TC USD para la conversion de precios no debe ser menor a '+$tc_usd_sat.val()+'.', 'Atencion!', function(r) { 
					$tc_usd.focus(); 
				});
			}
		});
		
		
		//pone cero al perder el enfoque
		$vigencia.blur(function(e){
			if(parseFloat($vigencia.val())==0||$vigencia.val()==""){
				$vigencia.val(0);
			}
		});
		
		
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$vigencia.focus(function(e){
			if(parseFloat($vigencia.val())<1){
				$vigencia.val('');
			}
		});
		
		
		//validar dias de vigencia
		$vigencia.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});
		
		//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
		$sku_producto.keypress(function(e){
			if(e.which == 13){
				$agregar_producto.trigger('click');
				return false;
			}
		});
		
		
		//desencadena clic del href Buscar producto al pulsar enter en el campo Nombre del producto
		$nombre_producto.keypress(function(e){
			if(e.which == 13){
				$busca_sku.trigger('click');
				return false;
			}
		});
		
		
		//desencadena clic del href Buscar cliente al pulsar enter en el campo razon social del cliente
		$razon_cliente.keypress(function(e){
			if(e.which == 13){
				$busca_cliente.trigger('click');
				return false;
			}
		});
		
		
		$aplicar_evento_click_checkbox($check_descripcion_larga);
		
		
		
		//validar campo Tipo de Cambio, solo acepte numeros y punto
		$tc.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		$tc_usd.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		
		$btn_autorizar.click(function(event){
			//LLamada a la funcion de la ventana de autorizacion
			$forma_autorizacion($grid_productos, $btn_autorizar, id_to_show);
		});
		
		
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			$total_tr.val(trCount);
                        var remove = function() {$(this).remove();};
			$('#forma-cotizacions-overlay').fadeOut(remove);
                        jAlert("Contra Recibo IMMS registrado con &eacute;xito", 'Atenci&oacute;n!');
                        //location.reload();
			/*if(parseInt(trCount) > 0){
				$grid_productos.find('tr').each(function (index){
					$(this).find('input[name=precio]').val(quitar_comas($(this).find('input[name=precio]').val()));
				});
				return true;
			}else{
				jAlert('No hay datos para actualizar', 'Atencion!', function(r) { $sku_producto.focus(); });
				return false;
			}*/
                    return true;
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-cotizacions-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-cotizacions-overlay').fadeOut(remove);
		});
	});
	
	
	
	var carga_formaCotizacions00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
                //alert("accion_mode="+accion_mode);
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_cotizacion':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			
			jConfirm('Realmente desea eliminar la cotizacion?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La cotizacion fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La cotizacion no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-cotizacions-window').remove();
			$('#forma-cotizacions-overlay').remove();
			
			var form_to_show = 'formaCotizacions00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_cotizacions();
			
			$('#forma-cotizacions-window').css({"margin-left": -370, 	"margin-top": -230});
			
			$forma_selected.prependTo('#forma-cotizacions-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert("id_to_show:"+id_to_show);
                        //Edición
			if(accion_mode == 'edit'){
                            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFormRemisionIMSS.json';
				
                                //alert("input_json EDIT:"+input_json);
                                
                                //$arreglo = {'id_cotizacion':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
                                $arreglo = {'id':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
                            //try {id_cotizacion
				var $tr_tipo = $('#forma-cotizacions-window').find('#tr_tipo');
				var $check_descripcion_larga =$('#forma-cotizacions-window').find('input[name=check_descripcion_larga]');
				var $check_incluye_iva =$('#forma-cotizacions-window').find('input[name=check_incluye_iva]');
				
				var $id_cotizacion = $('#forma-cotizacions-window').find('input[name=id_cotizacion]');
				var $total_tr = $('#forma-cotizacions-window').find('input[name=total_tr]');
				var $select_tipo_cotizacion = $('#forma-cotizacions-window').find('select[name=select_tipo_cotizacion]');
				
                                var $folio = $('#forma-cotizacions-window').find('input[name=folio]');
                                var $numeroContrato = $('#forma-cotizacions-window').find('input[name=numeroContrato]');
                                var $folioIMSS = $('#forma-cotizacions-window').find('input[name=folioIMSS]');
                                var $conducto_pago = $('#forma-cotizacions-window').find('input[name=conducto_pago]');
                                var $total = $('#forma-cotizacions-window').find('input[name=total]');
                                var $doc1 = $('#forma-cotizacions-window').find('input[name=doc1]');
                                var $doc2 = $('#forma-cotizacions-window').find('input[name=doc2]');
                                var $doc3 = $('#forma-cotizacions-window').find('input[name=doc3]');
                                var $doc4 = $('#forma-cotizacions-window').find('input[name=doc4]');
                                var $doc5 = $('#forma-cotizacions-window').find('input[name=doc5]');
                                var $doc6 = $('#forma-cotizacions-window').find('input[name=doc6]');
                                var $doc7 = $('#forma-cotizacions-window').find('input[name=doc7]');
                                var $doc8 = $('#forma-cotizacions-window').find('input[name=doc8]');
                                var $doc9 = $('#forma-cotizacions-window').find('input[name=doc9]');
                                var $doc10 = $('#forma-cotizacions-window').find('input[name=doc10]');
                                var $statusRemisionIMSS = $('#forma-cotizacions-window').find('input[name=statusRemisionIMSS]');
                                var $fecha = $('#forma-cotizacions-window').find('input[name=fecha]');
                                var $fecha2 = $('#forma-cotizacions-window').find('input[name=fecha2]');
                                
				
                                var $select_accion = $('#forma-cotizacions-window').find('select[name=select_accion]');
				//var $num_lista_precio=  $('#forma-cotizacions-window').find('input[name=num_lista_precio]');
				
				var $busca_cliente = $('#forma-cotizacions-window').find('a[href*=busca_cliente]');
				var $nocontrolcliente = $('#forma-cotizacions-window').find('input[name=nocontrolcliente]');
				var $razon_cliente = $('#forma-cotizacions-window').find('input[name=razoncliente]');
				var $dir_cliente = $('#forma-cotizacions-window').find('input[name=dircliente]');
				var $contactocliente = $('#forma-cotizacions-window').find('input[name=contactocliente]');
				var $select_moneda_original = $('#forma-cotizacions-window').find('select[name=moneda2]');
				var $tc = $('#forma-cotizacions-window').find('input[name=tc]');
				
                                var $vigencia = $('#forma-cotizacions-window').find('input[name=vigencia]');
				
				var $sku_producto = $('#forma-cotizacions-window').find('input[name=sku_producto]');
				var $nombre_producto = $('#forma-cotizacions-window').find('input[name=nombre_producto]');
				
				//buscar producto
				var $busca_sku = $('#forma-cotizacions-window').find('a[href*=busca_sku]');
				//href para agregar producto al grid
				var $agregar_producto = $('#forma-cotizacions-window').find('a[href*=agregar_producto]');
				
				var $btn_autorizar = $('#forma-cotizacions-window').find('#btn_autorizar');
				var $boton_genera_pdf = $('#forma-cotizacions-window').find('#genera_pdf');
				
				//grid de productos
				var $grid_productos = $('#forma-cotizacions-window').find('#grid_productos');
				//grid de errores
				var $grid_warning = $('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning');
				
				var $cerrar_plugin = $('#forma-cotizacions-window').find('#close');
				var $cancelar_plugin = $('#forma-cotizacions-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-cotizacions-window').find('#submit');
				
				
				$btn_autorizar.hide();
				$select_moneda_original.hide();
				//ocultar boton de generar pdf. Solo debe estar activo en editar
				//$boton_genera_pdf.hide();
				//$descripcion_larga.hide();
				$tr_tipo.hide();
				$tc.attr('readonly',true);
				$nocontrolcliente.attr('readonly',true);
				$razon_cliente.attr('readonly',true);
				$dir_cliente.attr('readonly',true);
				$nocontrolcliente.css({'background' : '#F0F0F0'});
				$razon_cliente.css({'background' : '#F0F0F0'});
				$folio.css({'background' : '#F0F0F0'});
				$dir_cliente.css({'background' : '#F0F0F0'});
				$contactocliente.css({'background' : '#F0F0F0'});
				//$fecha.css({'background' : '#F0F0F0'});
				
				$nocontrolcliente.focus();
				
				var respuestaProcesada = function(data){
                                    //alert("Respuesta procesada");
                                    jAlert("Contra Recibo IMMS actualizado con &eacute;xito", 'Atenci&oacute;n!');
                                    location.reload();
					if ( data['success'] == "true" ){
						jAlert("La cotizaci&oacute;n se guard&oacute; con exito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-cotizacions-overlay').fadeOut(remove);
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-cotizacions-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-cotizacions-window').find('.cotizacions_div_one').css({'height':'600px'});//con errores
						$('#forma-cotizacions-window').find('div.interrogacion').css({'display':'none'});
						$grid_productos.find('input[name=reqauth]').val('false');
						var contador_alert=0;
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-cotizacions-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-cotizacions-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								var campo = tmp.split(':')[0];
								var $campo_input;
								
								if((tmp.split(':')[0].substring(0, 4) == 'cant') || (tmp.split(':')[0].substring(0, 6) == 'precio')){
									$('#forma-cotizacions-window').find('#div_warning_grid').css({'display':'block'});
									$campo_input = $grid_productos.find('.'+campo).css({'background' : '#d41000'});
									
									var codigo_producto = $campo_input.parent().parent().find('input[name=sku]').val();
									var titulo_producto = $campo_input.parent().parent().find('input[name=nombre]').val();
									
									var tr_warning = '<tr>';
											tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
											tr_warning += '<td width="120"><INPUT TYPE="text" value="' + codigo_producto + '" class="borde_oculto" readOnly="true" style="width:116px; color:red"></td>';
											tr_warning += '<td width="200"><INPUT TYPE="text" value="' + titulo_producto + '" class="borde_oculto" readOnly="true" style="width:196px; color:red"></td>';
											tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:225px; color:red"></td>';
									tr_warning += '</tr>';
									
									$('#forma-cotizacions-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								}
								
								if(tmp.split(':')[0].substring(0,9)=='checkauth'){
									//alert(tmp);
									$grid_productos.find('.'+tmp.split(':')[0]).show();
									$grid_productos.find('.'+tmp.split(':')[0]).parent().find('input[name=reqauth]').val('true');
									
									var $campo_status_reg = $grid_productos.find('.'+tmp.split(':')[0]).parent().find('input[name=statusreg]');
									
									//Asignar nuevo valor
									$campo_status_reg.val('0&&&'+$campo_status_reg.val().split('&&&')[1]+'&&&'+$campo_status_reg.val().split('&&&')[2]);
									
									if(parseInt(contador_alert)<=0){
										//Confirm para guardar sin autorizacion
										jConfirm('Hay precios que necesitan autorizaci&oacute;n.<br>&iquest;Desea guardar para autorizar despu&eacute;s&#63;', 'Dialogo de Confirmacion', function(r) {
											// If they confirmed, manually trigger a form submission
											if (r) {
												//Asignar los estatus correspondientes para permitir guardar sin autorizacion
												$grid_productos.find('input[name=checkauth]').each(function(index){
													$tr = $(this).parent().parent();
													if(parseInt($tr.find('input[name=eliminado]').val())==1){
														$tr.find('input[name=success]').val('true');
													}
												});
												
												//Ejecutar el submit de actualizar
                                                                                                //alert("Ejecutar el submit de actualizar 2");
												$submit_actualizar.trigger('click');
											}
										});
									}
									
									contador_alert++;
								}
							}
						}
						
						$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
						$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});

						
					}
				   }
                                   
                                /*}catch(err){
                                    alert("Error:"+err);
                                }*/
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
                                
				//AQUI SE CARGAN LOS DATOS A EDITAR
				$.post(input_json,$arreglo,function(entry){
                                    //alert("Aquí va 1");
                                    try {
                                        //alert("Aquí va!");
                                        
                                        $folio.val(entry['id']);
                                        $numeroContrato.val(entry['numero_contrato']);
                                        $folioIMSS.val(entry['folio_imss']);
                                        $conducto_pago.val(entry['cliente']);
                                        $total.val(entry['importe']);
                                        $total.val(parseFloat($total.val()).toFixed(2));
                                        $doc1.val(entry['doc1']);
                                        $doc2.val(entry['doc2']);
                                        $doc3.val(entry['doc3']);
                                        $doc4.val(entry['doc4']);
                                        $doc5.val(entry['doc5']);
                                        $doc6.val(entry['doc6']);
                                        $doc7.val(entry['doc7']);
                                        $doc8.val(entry['doc8']);
                                        $doc9.val(entry['doc9']);
                                        $doc10.val(entry['doc10']);
                                        $fecha.val(entry['fecha_expedicion']);
                                        $fecha2.val(entry['fecha_pago']);
                                        $statusRemisionIMSS.val(entry['id_status']);
                                        
                                        //alert("entry['id_status']="+entry['id_status']);
					
					$busca_cliente.hide();
					
					$loadSelectStatusRemisionesIMSS(entry['id_status']);

					//$fecha.val(mostrarFecha());
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
                                                            $fecha.DatePickerHide();
								/*var valida_fecha=mayor($fecha.val(),mostrarFecha());
								
								if (valida_fecha==true){
									$fecha.DatePickerHide();
								}else{
									jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
									$fecha.val(mostrarFecha());
								}*/
							}
						}
					});
                                        
                                        $fecha2.click(function (s){
						var a=$('div.datepicker');
						a.css({'z-index':100});
					});
                                        
					$fecha2.DatePicker({
						format:'Y-m-d',
						date: $fecha2.val(),
						current: $fecha2.val(),
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
							$fecha2.val(formated);
							if (formated.match(patron) ){
                                                            $fecha2.DatePickerHide();
								/*var valida_fecha=mayor($fecha2.val(),mostrarFecha());
								
								if (valida_fecha==true){
									$fecha.DatePickerHide();
								}else{
									jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
									$fecha2.val(mostrarFecha());
								}*/
							}
						}
					});                                        
					
                                    }catch(err){
                                        alert("Error="+err);
                                    }
					
				});//termina llamada json
				
				//Carga select de acciones
				$select_accion.children().remove();
				var accion_hmtl = '<option value="edit">Actualizar</option>';
				accion_hmtl += '<option value="new">Nuevo</option>';
				$select_accion.append(accion_hmtl);
				
				
				//buscador de productos
				$busca_sku.click(function(event){
					event.preventDefault();
					$busca_productos($sku_producto.val(), $nombre_producto.val());
				});
				
				//desencadena clic del href Agregar producto al pulsar enter en el campo sku del producto
				$sku_producto.keypress(function(e){
					if(e.which == 13){
						$agregar_producto.trigger('click');
						return false;
					}
				});
		
				//desencadena clic del href Buscar producto al pulsar enter en el campo Nombre del producto
				$nombre_producto.keypress(function(e){
					if(e.which == 13){
						$busca_sku.trigger('click');
						return false;
					}
				});
		
				
				//alert($descripcion_larga.val());
				$boton_genera_pdf.click(function(event){
					var seleccionado="false";
					var incluyeIva="false";
					
					if($check_descripcion_larga.is(':checked')){
						seleccionado="true";
					}
					
					if($check_incluye_iva.is(':checked')){
						incluyeIva="true";
					}
					
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getGeneraPdfCotizacion/'+$id_cotizacion.val()+'/'+seleccionado+'/'+incluyeIva+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
				
				
				$aplicar_evento_click_checkbox($check_descripcion_larga);
				
				//validar campo Tipo de Cambio, solo acepte numeros y punto
				$tc.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//validar dias de vigencia
				$vigencia.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}		
				});
				
				
				
				$btn_autorizar.click(function(event){
					//LLamada a la funcion de la ventana de autorizacion
					$forma_autorizacion($grid_productos, $btn_autorizar, id_to_show);
				});
				
				
				
				$submit_actualizar.bind('click',function(){
                                    
                                    /*
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
					if(parseInt(trCount) > 0){
						
						$grid_productos.find('tr').each(function (index){
							$(this).find('input[name=precio]').val(quitar_comas($(this).find('input[name=precio]').val()));
						});
						
						return true;                
					}else{
						jAlert("No hay datos para actualizar", 'Atencion!');
						return false;
					}
                                        */
                                        var remove = function() {$(this).remove();};
					$('#forma-cotizacions-overlay').fadeOut(remove);

                                       return true;
				});
				
				
				//recalcular los totales al eliminar un registro
				//$recalcula_totales();
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-cotizacions-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-cotizacions-overlay').fadeOut(remove);
				});
				
			}
		}
	}
        
        
        
        
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRemisionesIMSS.json';
        //alert("input_json="+input_json);
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
        $arreglo = {'orderby':'erp_status_remisiones_imss.id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getRemisionesIMSS.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCotizacions00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



