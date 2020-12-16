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
	var controller = $contextpath.val()+"/controllers/provfacturas";
	
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_factura = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Facturas de Proveedores');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_factura = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_factura]');
	var $busqueda_proveedor = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_proveedor]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
	
	var tiposIva = new Array(); //este arreglo carga los select del grid cada que se agrega un nuevo producto
	//Este arreglo almacena los diferentes valores para el IEPS
	var arrayIeps = new Array(); 
	
	
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
		valor_retorno += "factura" + signo_separador + $busqueda_factura.val() + "|";
		valor_retorno += "proveedor" + signo_separador + $busqueda_proveedor.val() + "|";
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
		event.preventDefault();
		$busqueda_factura.val('');
		$busqueda_proveedor.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val(''); 
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
		$busqueda_factura.focus();
	});
	
	//Aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_factura, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_proveedor, $buscar);
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
		var $select_prod_tipo = $('#forma-provfacturas-window').find('select[name=prodtipo]');
		$('#forma-provfacturas-window').find('#submit').mouseover(function(){
			$('#forma-provfacturas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-provfacturas-window').find('#submit').mouseout(function(){
			$('#forma-provfacturas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		
		$('#forma-provfacturas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-provfacturas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-provfacturas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-provfacturas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-provfacturas-window').find('#close').mouseover(function(){
			$('#forma-provfacturas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-provfacturas-window').find('#close').mouseout(function(){
			$('#forma-provfacturas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-provfacturas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-provfacturas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-provfacturas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-provfacturas-window').find("ul.pestanas li").click(function() {
			$('#forma-provfacturas-window').find(".contenidoPes").hide();
			$('#forma-provfacturas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-provfacturas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
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



	//buscador de proveedores
	$busca_proveedores = function(){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
		var $campo_email = $('#forma-buscaproveedor-window').find('input[name=campo_email]');
		var $campo_nombre = $('#forma-buscaproveedor-window').find('input[name=campo_nombre]');
		
		var $buscar_plugin_proveedor = $('#forma-buscaproveedor-window').find('#busca_proveedor_modalbox');
		var $cancelar_plugin_busca_proveedor = $('#forma-buscaproveedor-window').find('#cencela');
			
		$('#forma-provfacturas-window').find('input[name=tipo_proveedor]').val('');
			
		//funcionalidad botones
		$buscar_plugin_proveedor.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_proveedor.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_proveedor.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_proveedor.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
	
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuacadorProveedores.json';
			$arreglo = {    'rfc':$campo_rfc.val(),
							'email':$campo_email.val(),
							'nombre':$campo_nombre.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
                                    
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							trr += '<input type="hidden" id="dias_cred_id" value="'+proveedor['dias_credito_id']+'">';
							trr += '<span class="rfc">'+proveedor['rfc']+'</span>';
						trr += '</td>';
						trr += '<td width="250"><span id="razon_social">'+proveedor['razon_social']+'</span></td>';
						trr += '<td width="250"><span class="direccion">'+proveedor['direccion']+'</span></td>';
					trr += '</tr>';
					
					$tabla_resultados.append(trr);
				});
				$tabla_resultados.find('tr:odd').find('td').css({ 'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({ 'background-color' : '#FFFFFF'});

				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({ background : '#FBD850'});
				}, function() {
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
					$('#forma-provfacturas-window').find('input[name=id_proveedor]').val($(this).find('#id_prov').val());
					$('#forma-provfacturas-window').find('input[name=tipo_proveedor]').val($(this).find('#tipo_prov').val());
					$('#forma-provfacturas-window').find('input[name=rfcproveedor]').val($(this).find('span.rfc').html());
					$('#forma-provfacturas-window').find('input[name=razon_proveedor]').val($(this).find('#razon_social').html());
					//$('#forma-provfacturas-window').find('input[name=dir_proveedor]').val($(this).find('span.direccion').html());
					$('#forma-provfacturas-window').find('select[name=tipo_factura]').find('option[value="'+$(this).find('#dias_cred_id').val()+'"]').attr('selected','selected');
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
				});
			});
		});
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
		});
	}//termina buscador de proveedores

	

	//buscador de remisiones de entradas de mercancias
	$buscador_remisiones = function(){
		$(this).modalPanel_buscaremision();
		var $dialogoc =  $('#forma-buscaremision-window');
		$dialogoc.append($('div.buscador_remisiones').find('table.formaBusqueda_remisiones').clone());
		
		$('#forma-buscaremision-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
		var $tabla_resultados = $('#forma-buscaremision-window').find('#tabla_resultado');
		
		var $folio_remision = $('#forma-buscaremision-window').find('input[name=folio_remision]');
		var $folio_entrada = $('#forma-buscaremision-window').find('input[name=folio_entrada]');
		var $proveedor = $('#forma-buscaremision-window').find('input[name=proveedor]');
		
		var $buscar_remision = $('#forma-buscaremision-window').find('#busca_remision');
		var $cancelar = $('#forma-buscaremision-window').find('#cencela');
			
		//funcionalidad botones
		$buscar_remision.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_remision.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});	
		
		//click buscar remision
		$buscar_remision.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorRemisiones.json';
			$arreglo = {    'folio_remision':$folio_remision.val(),
							'folio_entrada':$folio_entrada.val(),
							'proveedor':$proveedor.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Remisiones'],function(entryIndex,remision){
					trr = '<tr>';
						trr += '<td width="100">';
							trr += '<input type="hidden" id="id_entrada" value="'+remision['id']+'">';
							trr += '<span class="folio_remision">'+remision['folio_remision']+'</span>';
						trr += '</td>';
						trr += '<td width="105">';
							trr += '<span class="folio_entrada">'+remision['fecha_remision']+'</span>';
						trr += '</td>';
						trr += '<td width="390"><span class="proveedor">'+remision['proveedor']+'</span></td>';
					trr += '</tr>';
					
					$tabla_resultados.append(trr);
				});
				$tabla_resultados.find('tr:odd').find('td').css({ 'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({ 'background-color' : '#FFFFFF'});

				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({ background : '#FBD850'});
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
					
					var id_entrada=$(this).find('#id_entrada').val();
					$carga_datos_remision(id_entrada);
					//alert("id_entrada: "+id_entrada);
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaremision-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-provfacturas-window').find('input[name=sku_producto]').focus();
				});

			});
		})
		
		$cancelar.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaremision-overlay').fadeOut(remove);
		});
	}//termina buscador de productos
	
	
	
	//carga datos de la remision seleccionada en el buscador de remisiones
	$carga_datos_remision = function(id_entrada){
		var $select_tipo_factura = $('#forma-provfacturas-window').find('select[name=tipo_factura]');
		var $tasa_fletes = $('#forma-provfacturas-window').find('input[name=tasafletes]');
		var $campo_factura = $('#forma-provfacturas-window').find('input[name=factura]');
		var $campo_ordencompra = $('#forma-provfacturas-window').find('input[name=ordencompra]');
		var $campo_numeroguia = $('#forma-provfacturas-window').find('input[name=numeroguia]');
		var $campo_expedicion = $('#forma-provfacturas-window').find('input[name=expedicion]');
		var $select_denominacion = $('#forma-provfacturas-window').find('select[name=denominacion]');
		var $select_fleteras = $('#forma-provfacturas-window').find('select[name=fletera]');
		var $campo_tc = $('#forma-provfacturas-window').find('input[name=tc]');
		var $campo_observaciones = $('#forma-provfacturas-window').find('textarea[name=observaciones]');
		var $select_credito = $('#forma-provfacturas-window').find('select[name=credito]');
		
		//campos del proveedor
		//var $buscar_proveedor = $('#forma-provfacturas-window').find('a[href*=busca_proveedor]');
		var $hidden_id_proveedor = $('#forma-provfacturas-window').find('input[name=id_proveedor]');
		var $campo_rfc_proveedor = $('#forma-provfacturas-window').find('input[name=rfcproveedor]');
		var $campo_razon_proveedor = $('#forma-provfacturas-window').find('input[name=razon_proveedor]');
		var $campo_tipo_proveedor = $('#forma-provfacturas-window').find('input[name=tipo_proveedor]');
		
		//tabla contenedor del listado de productos
		var $grid_productos = $('#forma-provfacturas-window').find('#grid_productos');
		
		//campos de totales
		var $campo_flete = $('#forma-provfacturas-window').find('input[name=flete]');
		var $monto_iva_flete = $('#forma-provfacturas-window').find('input[name=iva_flete]');
		var $campo_subtotal = $('#forma-provfacturas-window').find('input[name=subtotal]');
		var $campo_impuesto = $('#forma-provfacturas-window').find('input[name=totimpuesto]');
		var $campo_total = $('#forma-provfacturas-window').find('input[name=total]');
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosEntradaRemision.json';
		$arreglo = {	'id_entrada':id_entrada,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		//aqui se cargan los datos de la remision seleccionada
		$.post(input_json,$arreglo,function(entry){
			$campo_factura.attr({ 'value' : entry['datosRemision']['0']['factura'] });
			$campo_ordencompra.attr({ 'value' : entry['datosRemision']['0']['orden_compra'] });
			$campo_numeroguia.attr({ 'value' : entry['datosRemision']['0']['numero_guia'] });
			$campo_expedicion.attr({ 'value' : entry['datosRemision']['0']['fecha_factura'] });
			$campo_tc.attr({ 'value' : entry['datosRemision']['0']['tipo_cambio'] });
			$campo_observaciones.text(entry['datosRemision']['0']['observaciones']);
			
			
			//carga el select de dias de credito
			$select_credito.children().remove();
			var credito_hmtl = '';
			$.each(entry['DiasCredito'],function(entryIndex,credito){
				if(parseInt(credito['id']) == parseInt(entry['datosRemision']['0']['dias_credito_id'])){
					credito_hmtl += '<option value="' + credito['id'] + '" selected="yes">' + credito['descripcion'] + '</option>';
				}else{
					credito_hmtl += '<option value="' + credito['id'] + '"  >' + credito['descripcion'] + '</option>';
				}
			});
			$select_credito.append(credito_hmtl);
			
			
			//carga select denominacion con todas las monedas
			$select_denominacion.children().remove();
			//var moneda_hmtl = '<option value="0">[--   --]</option>';
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				if(parseInt(moneda['id']) == parseInt(entry['datosRemision']['0']['moneda_id'])){
					moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
				}else{
					moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
				}
			});
			$select_denominacion.append(moneda_hmtl);
			
			$.each(entry['datosProveedorRemision'],function(entryIndex,proveedor){
				$hidden_id_proveedor.attr({ 'value' : proveedor['id'] });
				$campo_rfc_proveedor.attr({ 'value' : proveedor['rfc'] });
				$campo_razon_proveedor.attr({ 'value' : proveedor['razon_social'] });
				$campo_tipo_proveedor.attr({ 'value' : proveedor['proveedortipo_id'] });
			});
			
			//carga select de companias fleteras
			$select_fleteras.children().remove();
			var fletera_hmtl = '<option value="0">[--   --]</option>';
			
			$.each(entry['Fleteras'],function(entryIndex,fletera){
				if(parseInt(fletera['id']) == parseInt(entry['datosRemision'][0]['fletera_id'])){
					fletera_hmtl += '<option value="' + fletera['id'] + '" selected="yes">' + fletera['razon_social'] + '</option>';
				}else{
					fletera_hmtl += '<option value="' + fletera['id'] + '">' + fletera['razon_social'] + '</option>';
				}
			});
			$select_fleteras.append(fletera_hmtl);
			
			if(entry['datosGridRemision'] != null){
				$grid_productos.children().remove();
				$.each(entry['datosGridRemision'],function(entryIndex,prodGrid){
					
					var trCount = $("tr", $grid_productos).size();
					trCount++;
					var valor_pedimento=" ";
					var tr_prod='';
					
					tr_prod += '<tr>';
						tr_prod += '<td width="65" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<a href="elimina_producto" id="eliminaprod'+ trCount +'">Eliminar</a>';
							tr_prod += '<input type="hidden" name="eliminado" id="eliminado" value="1">';//el 1 significa que el registro no ha sido eliminado
						tr_prod += '</td>';
						tr_prod += '<td width="100" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<INPUT TYPE="text" name="codigo" id="codigo'+ trCount +'" value="' + prodGrid['codigo_producto'] + '" class="borde_oculto" style="width:96px;" readOnly="true">';
						tr_prod += '</td>';
						tr_prod += '<td width="200" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<INPUT TYPE="text" name="titulo" id="titulo'+ trCount +'" value="' + prodGrid['descripcion'] + '" class="borde_oculto" style="width:196px;" readOnly="true">';
						tr_prod += '</td>';
						tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<INPUT TYPE="text" name="unidad" class="borde_oculto" value="' + prodGrid['unidad_medida'] + '" readOnly="true" style="width:66px;">';
						tr_prod += '</td>';
						tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<INPUT TYPE="text" name="presentacion" class="borde_oculto" value="' + prodGrid['presentacion'] + '" readOnly="true" style="width:66px;">';
						tr_prod += '</td>';
						tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<INPUT TYPE="text" name="cantidad" id="cant" value="' + prodGrid['cantidad'] + '" style="width:66px;">';
						tr_prod += '</td>';
						tr_prod += '<td width="75" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<INPUT TYPE="text" name="costo" id="cost" value="' + prodGrid['costo_unitario'] + '" style="width:69px;">';
						tr_prod += '</td>';
						tr_prod += '<td width="90" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<INPUT TYPE="text" name="importe'+ trCount +'" id="import" value="' + prodGrid['importe'] + '" style="width:86px;" readOnly="true">';
						tr_prod += '</td>';
						tr_prod += '<td width="82" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							
							tr_prod += '<SELECT name="impuesto" id="imp" style="width:80px;">';
							if(parseInt(prodGrid['tipo_impuesto'])<=0){
								tr_prod += '<option value="0" selected="yes">[-- --]</option>';
							}
							
							//aqui se carga el select con los tipos de iva
							$.each(tiposIva,function(entryIndex,tipos){
								if(tipos['id'] == prodGrid['tipo_impuesto']){
									tr_prod += '<option value="' + tipos['id'] + '"  selected="yes">' + tipos['descripcion'] + '</option>';
								}else{
									tr_prod += '<option value="' + tipos['id'] + '"  >' + tipos['descripcion'] + '</option>';
								}
							});
							
							tr_prod += '</SELECT>';
							tr_prod += '<input type="hidden" name="valorimp" id="v_imp" value="0">';
							tr_prod += '<input type="hidden" name="totalimpuesto'+ trCount +'" id="totimp" value="0">';
						tr_prod += '</td>';
						
						
						tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<select name="select_ieps" id="selectIeps" style="width:76px;">';
							
							if(parseInt(prodGrid['ieps_id'])<=0){
								tr_prod += '<option value="0" selected="yes">[-- --]</option>';
							}
							$.each(arrayIeps,function(entryIndex,ieps){
								if(parseInt(prodGrid['ieps_id'])==parseInt(ieps['id'])){
									tr_prod += '<option value="' + ieps['id'] +'" selected="yes">' + ieps['titulo'] + '</option>';
								}
							});
							tr_prod += '</select>';
							tr_prod += '<input type="hidden" name="valorieps" id="tIeps" value="'+parseFloat(prodGrid['valor_ieps']).toFixed(4)+'">';
						tr_prod += '</td>';
						
						tr_prod += '<td width="84" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
							tr_prod += '<input type="text" name="importe_ieps'+ trCount +'" id="import_ieps" value="'+parseFloat(prodGrid['importe_ieps']).toFixed(4)+'" style="width:80px; text-align:right;" readOnly="true">';
						tr_prod += '</td>';
						
					tr_prod += '</tr>';
					$grid_productos.append(tr_prod);
					
					//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
					$grid_productos.find('input[name=codigo]').focus(function(e){
						if($(this).val() == ' '){
							$(this).val('');
						}
					});
					
					$grid_productos.find('input[name=codigo]').blur(function(){
						if ($(this).val() == ''  || $(this).val() == null){
						$(this).val(' ');
						}
					});
					
					//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
					$grid_productos.find('input[name=unidad]').focus(function(e){
						if($(this).val() == ' '){
							$(this).val('');
						}
					});
					
					$grid_productos.find('input[name=unidad]').blur(function(){
						if ($(this).val() == ''  || $(this).val() == null){
						$(this).val(' ');
						}
					});
					
					//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
					$grid_productos.find('input[name=presentacion]').focus(function(e){
						if($(this).val() == ' '){
							$(this).val('');
						}
					});
					
					$grid_productos.find('input[name=presentacion]').blur(function(){
						if ($(this).val() == ''  || $(this).val() == null){
						$(this).val(' ');
						}
					});
					
					//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
					$grid_productos.find('input[name=titulo]').focus(function(e){
						if($(this).val() == ' '){
							$(this).val('');
						}
					});
					
					$grid_productos.find('input[name=titulo]').blur(function(){
						if ($(this).val() == ''  || $(this).val() == null){
						$(this).val(' ');
						}
					});
					
					
					//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
					$grid_productos.find('input[name=costo]').focus(function(e){
						if($(this).val() == ' '){
							$(this).val('');
						}
					});
					
					//recalcula importe al perder enfoque el campo costo
					$grid_productos.find('input[name=costo]').blur(function(){
						if ($(this).val().trim()==''  || $(this).val() == null){
							$(this).val(' ');
						}
						
						if( $(this).val().trim()!='' && $(this).parent().parent().find('#cant').val().trim()!='' ){
							//Calcular y redondear el Importe
							$(this).parent().parent().find('#import').val( parseFloat(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cant').val())).toFixed(4) );
							
							//Calcular y redondear el importe del IEPS
							$(this).parent().parent().find('#import_ieps').val(parseFloat( parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($(this).parent().parent().find('#tIeps').val()) ).toFixed(4));
						}else{
							$(this).parent().parent().find('#import').val('');
						}
						$calcula_totales();//llamada a la funcion que calcula totales
					});
					
					//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
					$grid_productos.find('input[name=cantidad]').focus(function(e){
						if($(this).val() == ' '){
							$(this).val('');
						}
					});

					//recalcula importe al perder enfoque el campo cantidad
					$grid_productos.find('input[name=cantidad]').blur(function(){
						if ($(this).val() == ''  || $(this).val() == null){
							$(this).val(' ');
						}
						if( ($(this).val().trim()!='') && ($(this).parent().parent().find('#cost').val().trim()!='') ){	
							//Calcular y redondear el Importe
							$(this).parent().parent().find('#import').val(parseFloat(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cost').val())).toFixed(4));
							
							//Calcular y redondear el importe del IEPS
							$imprteIepsPartida.val(parseFloat( parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($(this).parent().parent().find('#tIeps').val()) ).toFixed(4));
						}else{
							$(this).parent().parent().find('#import').val('');
						}
						$calcula_totales();//llamada a la funcion que calcula totales
					});
					
					
					//validar campo costo, solo acepte numeros y punto
					$grid_productos.find('input[name=costo]').keypress(function(e){
						// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
						if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
							return true;
						}else {
							return false;
						}		
					});
					
					//validar campo cantidad, solo acepte numeros y punto
					$grid_productos.find('input[name=cantidad]').keypress(function(e){
						// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
						if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
							return true;
						}else {
							return false;
						}
					});
					
					//elimina un producto del grid
					$grid_productos.find('#eliminaprod'+ trCount).bind('click',function(event){
						event.preventDefault();
						//asigna espacios en blanco a todos los input de la fila eliminada
						$(this).parent().parent().find('input').val(' ');
						//asigna un 0 al input eliminado como bandera para saber que esta eliminado
						$(this).parent().find('#eliminado').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
						//oculta la fila eliminada
						$(this).parent().parent().hide();
						$calcula_totales();//llamada a la funcion que calcula totales
					});
					
					//seleccionar tipo de  impuesto
					$grid_productos.find('select[name=impuesto]').change(function(){
						var valorImpuesto=0;
						var id_ivatipo = $(this).val();
						$.each(tiposIva,function(entryIndex,tipos){
							if(parseInt(tipos['id'])==parseInt(id_ivatipo)){
								valorImpuesto = tipos['iva_1'];
							}
						});
						$(this).parent().find('input[name=valorimp]').val(valorImpuesto);
								
						$calcula_totales();//llamada a la funcion que calcula totales
					});
					
					
					//Seleccionar tipo de  impuesto IEPS
					$grid_productos.find('select[name=select_ieps]').change(function(){
						var valorIeps=0;
						var id_ieps = $(this).val();
						$.each(arrayIeps,function(entryIndex,ieps){
							valorIeps=0;
							if(parseInt(ieps['id'])==parseInt(id_ieps)){
								if(parseFloat(ieps['tasa'])>0){
									valorIeps = parseFloat(ieps['tasa'])/100;
								}
							}
						});
						$(this).parent().find('input[name=valorieps]').val(valorIeps);
						
						//Calcular y redondear el importe del IEPS
						$(this).parent().parent().find('#import_ieps').val(parseFloat( parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($(this).parent().parent().find('#tIeps').val()) ).toFixed(4));
						
						$calcula_totales();//llamada a la funcion que calcula totales
					});
		
					
				});
				$campo_flete.val(parseFloat( entry['datosRemision']['0']['flete']).toFixed(2));
				
				$calcula_totales();//llamada a la funcion que calcula totales
			}
			
		},"json");//termina llamada json
	
	}//termina carga de datos de la remision
	
	
	
	
	//funcion  para agregar nueva fila al grid
	$agrega_fila_al_grid = function($grid_productos){
		var trCount = $("tr", $grid_productos).size();
		trCount++;
		var valor_pedimento=" ";
		var tr_prod='';
		
		tr_prod += '<tr>';
			tr_prod += '<td width="65" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<a href="elimina_producto" id="eliminaprod'+ trCount +'">Eliminar</a>';
				tr_prod += '<input type="hidden" name="eliminado" id="eliminado" value="1">';//el 1 significa que el registro no ha sido eliminado
			tr_prod += '</td>';
			tr_prod += '<td width="100" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<INPUT TYPE="text" name="codigo" id="codigo'+ trCount +'" value=" " style="width:96px;">';
			tr_prod += '</td>';
			tr_prod += '<td width="200" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<INPUT TYPE="text" name="titulo" id="titulo'+ trCount +'" value=" " style="width:196px;">';
			tr_prod += '</td>';
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<INPUT TYPE="text" name="unidad" value=" " style="width:66px;">';
			tr_prod += '</td>';
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<INPUT TYPE="text" name="presentacion" value=" " style="width:66px;">';
			tr_prod += '</td>';
			tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<INPUT TYPE="text" name="cantidad" id="cant" value=" " style="width:66px;">';
			tr_prod += '</td>';
			tr_prod += '<td width="75" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<INPUT TYPE="text" name="costo" id="cost" value=" " style="width:69px;">';
			tr_prod += '</td>';
			tr_prod += '<td width="90" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<INPUT TYPE="text" name="importe'+ trCount +'" id="import" value="" style="width:86px;" readOnly="true">';
			tr_prod += '</td>';
			tr_prod += '<td width="82" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<SELECT name="impuesto" id="imp" style="width:80px;">';
				//aqui se carga el select con los tipos de iva
				$.each(tiposIva,function(entryIndex,tipos){
					tr_prod += '<option value="' + tipos['id'] + '"  >' + tipos['descripcion'] + '</option>';
				});
				tr_prod += '</SELECT>';
				tr_prod += '<input type="hidden" name="valorimp" id="v_imp" value="0">';
				tr_prod += '<input type="hidden" name="totalimpuesto'+ trCount +'" id="totimp" value="0">';
			tr_prod += '</td>';
			
			tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<select name="select_ieps" id="selectIeps" style="width:76px;">';
				tr_prod += '<option value="0" selected="yes">[-- --]</option>';
				$.each(arrayIeps,function(entryIndex,ieps){
					tr_prod += '<option value="' + ieps['id'] + '">' + ieps['titulo'] + '</option>';
				});
				tr_prod += '</select>';
				tr_prod += '<input type="hidden" name="valorieps" id="tIeps" value="'+parseFloat(0).toFixed(2)+'">';
			tr_prod += '</td>';
			
			tr_prod += '<td width="84" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
				tr_prod += '<input type="text" name="importe_ieps'+ trCount +'" id="import_ieps" value="'+parseFloat(0).toFixed(4)+'" style="width:80px; text-align:right;" readOnly="true">';
			tr_prod += '</td>';
			
		tr_prod += '</tr>';
		$grid_productos.append(tr_prod);
		
		//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$grid_productos.find('input[name=codigo]').focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
		
		$grid_productos.find('input[name=codigo]').blur(function(){
			if ($(this).val() == ''  || $(this).val() == null){
			$(this).val(' ');
			}
		});
		
		//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$grid_productos.find('input[name=unidad]').focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
		
		$grid_productos.find('input[name=unidad]').blur(function(){
			if ($(this).val() == ''  || $(this).val() == null){
			$(this).val(' ');
			}
		});
		
		//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$grid_productos.find('input[name=presentacion]').focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
		
		$grid_productos.find('input[name=presentacion]').blur(function(){
			if ($(this).val() == ''  || $(this).val() == null){
			$(this).val(' ');
			}
		});
		
		//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$grid_productos.find('input[name=titulo]').focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
		
		$grid_productos.find('input[name=titulo]').blur(function(){
			if ($(this).val() == ''  || $(this).val() == null){
			$(this).val(' ');
			}
		});
		
		
		//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$grid_productos.find('input[name=costo]').focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
		
		//recalcula importe al perder enfoque el campo costo
		$grid_productos.find('input[name=costo]').blur(function(){
			if ($(this).val() == ''  || $(this).val() == null){
				$(this).val(' ');
			}
			
			if( $(this).val().trim()!='' && $(this).parent().parent().find('#cant').val().trim()!='' ){
				//Calcular y redondear el importe
				$(this).parent().parent().find('#import').val( parseFloat(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cant').val())).toFixed(4) );
				
				//Calcular y redondear el importe del IEPS
				$(this).parent().parent().find('#import_ieps').val(parseFloat( parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($(this).parent().parent().find('#tIeps').val()) ).toFixed(4));
			}else{
				$(this).parent().parent().find('#import').val('');
			}
			$calcula_totales();//llamada a la funcion que calcula totales
		});
		
		//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
		$grid_productos.find('input[name=cantidad]').focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
		
		//recalcula importe al perder enfoque el campo cantidad
		$grid_productos.find('input[name=cantidad]').blur(function(){
			var $costoUniarioPartida = $(this).parent().parent().find('#cost');
			var $importePartida = $(this).parent().parent().find('#import');
			var $imprteIepsPartida = $(this).parent().parent().find('#import_ieps');
			var $tasaIepsPartida = $(this).parent().parent().find('#tIeps');
			
			if ($(this).val().trim() == ''  || $(this).val() == null){
				$(this).val(' ');
			}
			if( ($(this).val().trim()!='') && ($(this).parent().parent().find('#cost').val().trim()!='') ){	
				//Calcular y redondear el importe
				$importePartida.val( parseFloat(parseFloat($(this).val()) * parseFloat($costoUniarioPartida.val())).toFixed(4) );
				
				//Calcular y redondear el importe del IEPS
				$imprteIepsPartida.val(parseFloat( parseFloat($importePartida.val()) * parseFloat($tasaIepsPartida.val()) ).toFixed(4));
				
			}else{
				$importePartida.val('');
			}
			$calcula_totales();//llamada a la funcion que calcula totales
		});
		
		
		//validar campo costo, solo acepte numeros y punto
		$grid_productos.find('input[name=costo]').keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});
		
		//validar campo cantidad, solo acepte numeros y punto
		$grid_productos.find('input[name=cantidad]').keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});
		
		//elimina un producto del grid
		$grid_productos.find('#eliminaprod'+ trCount).bind('click',function(event){
			event.preventDefault();
			//asigna espacios en blanco a todos los input de la fila eliminada
			$(this).parent().parent().find('input').val(' ');
			//asigna un 0 al input eliminado como bandera para saber que esta eliminado
			$(this).parent().find('#eliminado').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
			//oculta la fila eliminada
			$(this).parent().parent().hide();
			$calcula_totales();//llamada a la funcion que calcula totales
		});
		
		//Seleccionar tipo de  impuesto IVA
		$grid_productos.find('select[name=impuesto]').change(function(){
			var valorImpuesto=0;
			var id_ivatipo = $(this).val();
			$.each(tiposIva,function(entryIndex,tipos){
				if(parseInt(tipos['id'])==parseInt(id_ivatipo)){
					valorImpuesto = tipos['iva_1'];
				}
			});
			$(this).parent().find('input[name=valorimp]').val(valorImpuesto);
			
			$calcula_totales();//llamada a la funcion que calcula totales
		});
		
		
		//Seleccionar tipo de  impuesto IEPS
		$grid_productos.find('select[name=select_ieps]').change(function(){
			var valorIeps=0;
			var id_ieps = $(this).val();
			$.each(arrayIeps,function(entryIndex,ieps){
				valorIeps=0;
				if(parseInt(ieps['id'])==parseInt(id_ieps)){
					if(parseFloat(ieps['tasa'])>0){
						valorIeps = parseFloat(ieps['tasa'])/100;
					}
				}
			});
			$(this).parent().find('input[name=valorieps]').val(valorIeps);
			
			//Calcular y redondear el importe del IEPS
			$(this).parent().parent().find('#import_ieps').val(parseFloat( parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($(this).parent().parent().find('#tIeps').val()) ).toFixed(4));
			
			$calcula_totales();//llamada a la funcion que calcula totales
		});
		
	}
	
	
	
	
	//calcula totales(subtotal,descuento, impuesto, total)
	$calcula_totales = function(){
		var $select_tipo_factura = $('#forma-provfacturas-window').find('select[name=tipo_factura]');
		var $campo_flete = $('#forma-provfacturas-window').find('input[name=flete]');
		var $monto_iva_flete = $('#forma-provfacturas-window').find('input[name=iva_flete]');
		var $tasa_fletes = $('#forma-provfacturas-window').find('input[name=tasafletes]');
		var $campo_subtotal = $('#forma-provfacturas-window').find('input[name=subtotal]');
		var $campo_ieps = $('#forma-provfacturas-window').find('input[name=total_ieps]');
		var $campo_ret_isr = $('#forma-provfacturas-window').find('input[name=total_ret_isr]');
		//var $campo_descuento = $('#forma-provfacturas-window').find('input[name=descuento]');
		var $campo_impuesto = $('#forma-provfacturas-window').find('input[name=totimpuesto]');
		var $retencion = $('#forma-provfacturas-window').find('input[name=retencion]');
		var $campo_total = $('#forma-provfacturas-window').find('input[name=total]');
		
		var $tr_ieps = $('#forma-provfacturas-window').find('#tr_ieps');
		var $tr_isr = $('#forma-provfacturas-window').find('#tr_isr');
		var $tr_ret_iva = $('#forma-provfacturas-window').find('#tr_ret_iva');
		
		var sumaImporte = 0;
		//var sumaDescuento = 0;
		var sumaImpuesto = 0;
		var sumaTotal = 0;
		var impuesto = 0.00;
		var partida=0;
		var retencionIva=0;
		var sumaImporteIeps=0;
		var importeIsr=0;
		var tasaIva=0;
		
		var $grid_productos = $('#forma-provfacturas-window').find('#grid_productos');
		$grid_productos.find('tr').each(function (index){
			if(($(this).find('#cost').val().trim()!='') && ($(this).find('#cant').val().trim()!='')){
				var id_ivatipo = $(this).find('#imp').val();
				var valorImpuesto = 0;
				//alert(id_ivatipo);
				//alert("Alert1: "+$(this).parent().find('#totimp').val());
				
				$.each(tiposIva,function(entryIndex,tipos){
					if(parseInt(tipos['id'])==parseInt(id_ivatipo)){
						valorImpuesto = tipos['iva_1'];
					}
				});
				
				if($(this).find('#import').val().trim()!=''){
					//Calcula total impuesto del producto actual
					$(this).find('#totimp').val(parseFloat(parseFloat($(this).find('#import').val()) + parseFloat($(this).find('#import_ieps').val())) * parseFloat(valorImpuesto));
				}
				
				sumaImporte = parseFloat(sumaImporte) + parseFloat($(this).find('#import').val());
				sumaImporteIeps = parseFloat(sumaImporteIeps) + parseFloat($(this).find('#import_ieps').val());
				sumaImpuesto = parseFloat(sumaImpuesto) + parseFloat($(this).find('#totimp').val());
			}
		});
		
		//2=Factura de Proveedor de Servicios u Honorarios
		//4=Facturas de Fletes
		
		if(parseInt($select_tipo_factura.val()) != 4 ){
			$.each(tiposIva,function(entryIndex,tipos){
				if(parseInt(tipos['id'])==1){
					//calcula iva del flete
					$monto_iva_flete.val( parseFloat($campo_flete.val()) * parseFloat(tipos['iva_1'])  );
					tasaIva=tipos['iva_1'];
				}
			});
			//Calcula la retencion de fletes, para eso convierto la tasa de retencion 4% en su valor 0.04 dividiendola entre 100
			retencionIva = parseFloat($campo_flete.val()) * parseFloat(parseFloat($tasa_fletes.val())/100);
		}else{
			//Calcula la retencion de fletes, para eso convierto la tasa de retencion 4% en su valor 0.04 dividiendola entre 100
			retencionIva = parseFloat(sumaImporte) * parseFloat(parseFloat($tasa_fletes.val())/100);
			$monto_iva_flete.val(0);
		}
		
		
		sumaImpuesto = parseFloat(sumaImpuesto) + parseFloat($monto_iva_flete.val());
		
		sumaImporte = parseFloat(sumaImporte) + parseFloat($campo_flete.val());
		
		
		//2=Factura de Proveedor de Servicios u Honorarios
		if(parseInt($select_tipo_factura.val())==2){
			//Calcular la Retencion del IVA
			retencionIva = ((parseFloat(sumaImpuesto) / 3) * 2);
			$monto_iva_flete.val(0);
			
			//Calcular el ISR
			importeIsr = parseFloat(sumaImporte) * 0.10;
		}
		
		
		//se resta la retencion de fletes
		//calcula el total
		sumaTotal = parseFloat(sumaImporte) + parseFloat(sumaImporteIeps) + parseFloat(sumaImpuesto) - parseFloat(retencionIva) - parseFloat(importeIsr);
		
		if(parseFloat(sumaImporteIeps)>0){
			$tr_ieps.show();
		}else{
			$tr_ieps.hide();
		}
		
		if(parseFloat(importeIsr)>0){
			$tr_isr.show();
		}else{
			$tr_isr.hide();
		}
		
		if(parseFloat(retencionIva)>0){
			$tr_ret_iva.show();
		}else{
			$tr_ret_iva.hide();
		}
		
		$campo_flete.val(parseFloat($campo_flete.val()).toFixed(2));
		$campo_subtotal.val($(this).agregar_comas( parseFloat(sumaImporte).toFixed(2)));
		$campo_ieps.val($(this).agregar_comas( parseFloat(sumaImporteIeps).toFixed(2)));
		$campo_ret_isr.val($(this).agregar_comas( parseFloat(importeIsr).toFixed(2)));
		$campo_impuesto.val($(this).agregar_comas( parseFloat(sumaImpuesto).toFixed(2)));
		$retencion.val(parseFloat(retencionIva).toFixed(2));
		$campo_total.val($(this).agregar_comas( parseFloat(sumaTotal).toFixed(2)));
	}//termina calculo de impuestos
	
	
	//buscar tipo de cambio de la fecha seleccionada
	function buscarTipoCambio($fecha){	
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTipoCambio.json';
		$arreglo = {'fecha':$fecha}
		$.post(input_json,$arreglo,function(valor){
			$('#forma-provfacturas-window').find('input[name=tc]').val(valor['tipoCambio'][0]['valor']);
		},"json");
	}
	
	
	
	//nueva factura de proveedor
	$new_factura.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_provfacturas();
		
		var form_to_show = 'formaprovfacturas00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-provfacturas-window').css({ "margin-left": -450, "margin-top": -230 });
		
		$forma_selected.prependTo('#forma-provfacturas-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosFacturaProveedor.json';
		$arreglo = {'id_factura':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
                
                
		var $id_factura = $('#forma-provfacturas-window').find('input[name=id_factura]');
		var $tasa_fletes = $('#forma-provfacturas-window').find('input[name=tasafletes]');
		var $total_tr = $('#forma-provfacturas-window').find('input[name=total_tr]');
		var $campo_factura = $('#forma-provfacturas-window').find('input[name=factura]');
		var $campo_ordencompra = $('#forma-provfacturas-window').find('input[name=ordencompra]');
		var $campo_numeroguia = $('#forma-provfacturas-window').find('input[name=numeroguia]');
		var $campo_expedicion = $('#forma-provfacturas-window').find('input[name=expedicion]');
		var $select_denominacion = $('#forma-provfacturas-window').find('select[name=denominacion]');
		var $select_fleteras = $('#forma-provfacturas-window').find('select[name=fletera]');
		var $campo_tc = $('#forma-provfacturas-window').find('input[name=tc]');
		var $campo_observaciones = $('#forma-provfacturas-window').find('textarea[name=observaciones]');
		var $select_tipo_factura = $('#forma-provfacturas-window').find('select[name=tipo_factura]');
		var $select_credito = $('#forma-provfacturas-window').find('select[name=credito]');
		
		
		//campos del proveedor
		var $buscar_proveedor = $('#forma-provfacturas-window').find('a[href*=busca_proveedor]');
		var $hidden_id_proveedor = $('#forma-provfacturas-window').find('input[name=id_proveedor]');
		var $campo_rfc_proveedor = $('#forma-provfacturas-window').find('input[name=rfcproveedor]');
		var $campo_razon_proveedor = $('#forma-provfacturas-window').find('input[name=razon_proveedor]');
		//var $campo_dir_proveedor = $('#forma-provfacturas-window').find('input[name=dir_proveedor]');
		
		
		//tabla contenedor del listado de productos
		var $grid_productos = $('#forma-provfacturas-window').find('#grid_productos');
		//campos de totales
		var $campo_flete = $('#forma-provfacturas-window').find('input[name=flete]');
		var $monto_iva_flete = $('#forma-provfacturas-window').find('input[name=iva_flete]');
		var $campo_subtotal = $('#forma-provfacturas-window').find('input[name=subtotal]');
		var $campo_ieps = $('#forma-provfacturas-window').find('input[name=total_ieps]');
		var $campo_ret_isr = $('#forma-provfacturas-window').find('input[name=total_ret_isr]');
		var $campo_descuento = $('#forma-provfacturas-window').find('input[name=descuento]');
		var $campo_impuesto = $('#forma-provfacturas-window').find('input[name=totimpuesto]');
		var $campo_total = $('#forma-provfacturas-window').find('input[name=total]');
		
		var $tr_ieps = $('#forma-provfacturas-window').find('#tr_ieps');
		var $tr_isr = $('#forma-provfacturas-window').find('#tr_isr');
		
		
		
		var $busca_remision = $('#forma-provfacturas-window').find('a[href*=busca_remision]');
		var $agregar_concepto = $('#forma-provfacturas-window').find('a[href*=agregar_concepto]');
		var $cancelar_factura = $('#forma-provfacturas-window').find('#cancela_factura');
		var $div_contenedor_grid = $('#forma-provfacturas-window').find('#contenedor_grid');
		
		var $cerrar_plugin = $('#forma-provfacturas-window').find('#close');
		var $cancelar_plugin = $('#forma-provfacturas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-provfacturas-window').find('#submit');
		
		$campo_flete.val(0);
		$id_factura.val(0);
		$cancelar_factura.hide();
		
		$busca_remision.show();
		$agregar_concepto.hide();
		$buscar_proveedor.hide();
		//$campo_factura.css({'background' : '#ffffff'});
		$tr_ieps.hide();
		$tr_isr.hide();
		
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Factura se ha dado de alta", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-provfacturas-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-provfacturas-window').find('div.interrogacion').css({'display':'none'});
				$grid_productos.find('#cost').css({'background' : '#ffffff'});
				$grid_productos.find('#cant').css({'background' : '#ffffff'});
				$grid_productos.find('#cad').css({'background' : '#ffffff'});
				
				$('#forma-provfacturas-window').find('#div_warning_grid').css({'display':'none'});
				$('#forma-provfacturas-window').find('#div_warning_grid').find('#grid_warning').children().remove();
				
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-provfacturas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });

						//alert(tmp.split(':')[0]);

						if(parseInt($("tr", $grid_productos).size())>0){
							for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
								if((tmp.split(':')[0]=='costo'+i) || (tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='titulo'+i) || (tmp.split(':')[0]=='codigo'+i) || (tmp.split(':')[0]=='unidad'+i) || (tmp.split(':')[0]=='caducidad'+i)){
									
									$('#forma-provfacturas-window').find('#div_warning_grid').css({'display':'block'});
									//$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
									if(tmp.split(':')[0].substring(0, 6) == 'codigo'){
										$grid_productos.find('input[name=codigo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
									}
									
									if(tmp.split(':')[0].substring(0, 6) == 'titulo'){
										$grid_productos.find('input[name=titulo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
									}
									
									if(tmp.split(':')[0].substring(0, 6) == 'unidad'){
										$grid_productos.find('input[name=unidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
									}
									
									if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
										$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
									}
									
									if(tmp.split(':')[0].substring(0, 5) == 'costo'){
										$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
									}
									
									
									var tr_warning = '<tr>';
											tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
											tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $grid_productos.find('#codigo' + i ).val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
											tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $grid_productos.find('#titulo' + i ).val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
											tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:285px; color:red"></td>';
									tr_warning += '</tr>';
									
									$('#forma-provfacturas-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
								}
							}
						}
					}
				}

				$('#forma-provfacturas-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
				$('#forma-provfacturas-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});			
			}
		}
		
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		
		//muestra fecha en input
		$campo_expedicion.val(mostrarFecha());
		$campo_expedicion.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		//seleccionar fecha
		$campo_expedicion.DatePicker({
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
				$campo_expedicion.val(formated);
				
				buscarTipoCambio($campo_expedicion.val());//busca tipo de cambio de la fecha seleccionada
				
				if (formated.match(patron) ){
					var valida_fecha=mayor($campo_expedicion.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$campo_expedicion.val(mostrarFecha());
					}else{
						$campo_expedicion.DatePickerHide();	
					}
				}
			}
		});
		
		
		
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			//Asignar los valores del IEPS al arreglo
			arrayIeps=entry['Ieps'];
			
			$tasa_fletes.attr({ 'value' : entry['tasaFletes']['0']['valor'] });
			//alert(entry['tasaFletes']['0']['valor']);
			
			//carga el select de dias de credito
			$select_credito.children().remove();
			var credito_hmtl = '<option value="0" >[--   --]</option>';
			$.each(entry['DiasCredito'],function(entryIndex,credito){
				credito_hmtl += '<option value="' + credito['id'] + '"  >' + credito['descripcion'] + '</option>';
			});
			$select_credito.append(credito_hmtl);
			
			//carga select denominacion con todas las monedas
			$select_denominacion.children().remove();
			var moneda_hmtl = '<option value="0" selected="yes">[--   --]</option>';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_denominacion.append(moneda_hmtl);
			
			//carga select de companias fleteras
			$select_fleteras.children().remove();
			var fletera_hmtl = '<option value="0" selected="yes">[--   --]</option>';
			$.each(entry['Fleteras'],function(entryIndex,fletera){
				fletera_hmtl += '<option value="' + fletera['id'] + '"  >' + fletera['razon_social'] + '</option>';
			});
			$select_fleteras.append(fletera_hmtl);
			
			tiposIva = entry['Impuestos'];//asigna los tipos de impuestos al arrglo tiposIva
		},"json");//termina llamada json
		
		
		/*
		var $grid_productos = $('#forma-provfacturas-window').find('#grid_productos');
		//campos de totales
		var $campo_flete = $('#forma-provfacturas-window').find('input[name=flete]');
		var $monto_iva_flete = $('#forma-provfacturas-window').find('input[name=iva_flete]');
		var $campo_subtotal = $('#forma-provfacturas-window').find('input[name=subtotal]');
		var $campo_ieps = $('#forma-provfacturas-window').find('input[name=total_ieps]');
		var $campo_ret_isr = $('#forma-provfacturas-window').find('input[name=total_ret_isr]');
		var $campo_descuento = $('#forma-provfacturas-window').find('input[name=descuento]');
		var $campo_impuesto = $('#forma-provfacturas-window').find('input[name=totimpuesto]');
		var $campo_total = $('#forma-provfacturas-window').find('input[name=total]');
		
		var $tr_ieps = $('#forma-provfacturas-window').find('#tr_ieps');
		var $tr_isr = $('#forma-provfacturas-window').find('#tr_isr');
		*/
		
		$select_tipo_factura.change(function(){
			var opcion = $(this).val();
			
			//Factura de Compra(con Remisi&oacute;n previamente capturada)
			if(parseInt(opcion)==1){
				$busca_remision.show();
				$buscar_proveedor.hide();
				$agregar_concepto.hide();
				$campo_flete.attr("readonly", false);
			}
			
			//Factura de Proveedor de Servicios u Honorarios
			if(parseInt(opcion)==2){
				$buscar_proveedor.show();
				$agregar_concepto.show();
				$busca_remision.hide();
				$campo_flete.attr("readonly", false);
				$tr_isr.show();
			}else{
				$tr_isr.hide();
				$campo_ret_isr.val(0);
			}
			
			//Facturas Varias(de Otros insumos)
			if(parseInt(opcion)==3){
				$buscar_proveedor.show();
				$agregar_concepto.show();
				$busca_remision.hide();
				$campo_flete.attr("readonly", false);
			}
			
			//Facturas de Fletes
			if(parseInt(opcion)==4){
				$buscar_proveedor.show();
				$agregar_concepto.show();
				$busca_remision.hide();
				$campo_flete.attr("readonly", true);
			}
			
			$calcula_totales();
		});
		
		
		//buscar remision
		$busca_remision.click(function(event){
			event.preventDefault();
			$buscador_remisiones();
		});
		
		
		$campo_tc.keypress(function(e){
			// Permitir solo numeros, borrar, suprimir, TAB, punto
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		//buscar proveedor
		$buscar_proveedor.click(function(event){
			event.preventDefault();
			$busca_proveedores();
		});
		
		
		
		//agregar fila al grid
		$agregar_concepto.click(function(event){
			event.preventDefault();
			$agrega_fila_al_grid($grid_productos);
		});
		
		
		
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo_flete.focus(function(e){
			if(parseFloat($campo_flete.val())<1){
				$campo_flete.val('');
			}
		});
		
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_flete.blur(function(e){
			if(parseFloat($campo_flete.val())==0||$campo_flete.val()==""){
				$campo_flete.val(0.0);
			}
			$calcula_totales();//llamada a la funcion que calcula totales
		});	

		$campo_flete.keypress(function(e){
			//alert(e.which);
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			$total_tr.val(trCount);
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert("No hay datos para actualizar", 'Atencion!');
				return false;
			}
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-provfacturas-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-provfacturas-overlay').fadeOut(remove);
		});
                
		
	});
	
	
	
	var carga_formaprovfacturas00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'no_entrada':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Factura seleccionada?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Factura fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Factura no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                        
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaprovfacturas00';
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			//var accion = "get_datos_entrada_mercancia";
			
			$(this).modalPanel_provfacturas();
			
			$('#forma-provfacturas-window').css({ "margin-left": -450, 	"margin-top": -230 });
			
			$forma_selected.prependTo('#forma-provfacturas-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosFacturaProveedor.json';
				$arreglo = {'id_factura':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
                                
				var $id_factura = $('#forma-provfacturas-window').find('input[name=id_factura]');
				var $tasa_fletes = $('#forma-provfacturas-window').find('input[name=tasafletes]');
				var $select_tipo_factura = $('#forma-provfacturas-window').find('select[name=tipo_factura]');
				
				var $total_tr = $('#forma-provfacturas-window').find('input[name=total_tr]');
				var $campo_factura = $('#forma-provfacturas-window').find('input[name=factura]');
				var $campo_ordencompra = $('#forma-provfacturas-window').find('input[name=ordencompra]');
				var $campo_numeroguia = $('#forma-provfacturas-window').find('input[name=numeroguia]');
				var $campo_expedicion = $('#forma-provfacturas-window').find('input[name=expedicion]');
				var $select_denominacion = $('#forma-provfacturas-window').find('select[name=denominacion]');
				
				var $select_credito = $('#forma-provfacturas-window').find('select[name=credito]');
				var $select_fleteras = $('#forma-provfacturas-window').find('select[name=fletera]');
				//var $select_almacen_destino = $('#forma-provfacturas-window').find('select[name=almacen_destino]');
				var $campo_tc = $('#forma-provfacturas-window').find('input[name=tc]');
				var $campo_observaciones = $('#forma-provfacturas-window').find('textarea[name=observaciones]');
				
				//campos del proveedor
				var $buscar_proveedor = $('#forma-provfacturas-window').find('a[href*=busca_proveedor]');
				var $hidden_id_proveedor = $('#forma-provfacturas-window').find('input[name=id_proveedor]');
				var $campo_rfc_proveedor = $('#forma-provfacturas-window').find('input[name=rfcproveedor]');
				var $campo_razon_proveedor = $('#forma-provfacturas-window').find('input[name=razon_proveedor]');
				//var $campo_dir_proveedor = $('#forma-provfacturas-window').find('input[name=dir_proveedor]');
				var $campo_tipo_proveedor = $('#forma-provfacturas-window').find('input[name=tipo_proveedor]');
				
				//var $campo_sku = $('#forma-provfacturas-window').find('input[name=sku_producto]');
				
				//tabla contenedor del listado de productos
				var $grid_productos = $('#forma-provfacturas-window').find('#grid_productos');
				//campos de totales
				var $campo_flete = $('#forma-provfacturas-window').find('input[name=flete]');
				var $campo_subtotal = $('#forma-provfacturas-window').find('input[name=subtotal]');
				var $campo_descuento = $('#forma-provfacturas-window').find('input[name=descuento]');
				var $campo_impuesto = $('#forma-provfacturas-window').find('input[name=totimpuesto]');
				var $campo_total = $('#forma-provfacturas-window').find('input[name=total]');
				
				
				var $busca_remision = $('#forma-provfacturas-window').find('a[href*=busca_remision]');
				var $agregar_concepto = $('#forma-provfacturas-window').find('a[href*=agregar_concepto]');
				var $div_contenedor_grid = $('#forma-provfacturas-window').find('#contenedor_grid');
				var $cancelar_factura = $('#forma-provfacturas-window').find('#cancela_factura');
				
				
				var $cerrar_plugin = $('#forma-provfacturas-window').find('#close');
				var $cancelar_plugin = $('#forma-provfacturas-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-provfacturas-window').find('#submit');
				//$submit_actualizar.hide();//ocultar boton para que no permita actualizar
				
				$campo_flete.val(0.0);
				$busca_remision.hide();
				$buscar_proveedor.hide();
				//$campo_factura.attr("readonly", true);
				//$campo_ordencompra.attr("readonly", true);
				//$campo_numeroguia.attr("readonly", true);
				//$campo_expedicion.attr("readonly", true);
				//$campo_tc.attr("readonly", true);
				//$campo_observaciones.attr("readonly", true);
				//$campo_flete.attr("readonly", true);
				
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("La Factura se guardo con exito", 'Atencion!');
						var remove = function() { $(this).remove(); };
						$('#forma-provfacturas-overlay').fadeOut(remove);
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-provfacturas-window').find('div.interrogacion').css({'display':'none'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cad').css({'background' : '#ffffff'});

						$('#forma-provfacturas-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-provfacturas-window').find('#div_warning_grid').find('#grid_warning').children().remove();
								
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							
							if( longitud.length > 1 ){
								$('#forma-provfacturas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								
								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='costo'+i) || (tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='titulo'+i) || (tmp.split(':')[0]=='codigo'+i) || (tmp.split(':')[0]=='unidad'+i) || (tmp.split(':')[0]=='caducidad'+i)){
											
											$('#forma-provfacturas-window').find('#div_warning_grid').css({'display':'block'});
											//$grid_productos.find('input[name=' + tmp.split(':')[0] + ']').css({'background' : '#d41000'});
											if(tmp.split(':')[0].substring(0, 6) == 'codigo'){
												$grid_productos.find('input[name=codigo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											if(tmp.split(':')[0].substring(0, 6) == 'titulo'){
												$grid_productos.find('input[name=titulo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											if(tmp.split(':')[0].substring(0, 6) == 'unidad'){
												$grid_productos.find('input[name=unidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
												$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											if(tmp.split(':')[0].substring(0, 5) == 'costo'){
												$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											var tr_warning = '<tr>';
													tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top"></td>';
													tr_warning += '<td width="120"><INPUT TYPE="text" value="' + $grid_productos.find('#codigo' + i ).val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
													tr_warning += '<td width="200"><INPUT TYPE="text" value="' + $grid_productos.find('#titulo' + i ).val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
													tr_warning += '<td width="235"><INPUT TYPE="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:285px; color:red"></td>';
											tr_warning += '</tr>';
											
											$('#forma-provfacturas-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
										}
										
									}
								}
							}
						}
						$('#forma-provfacturas-window').find('#div_warning_grid').find('#grid_warning').find('tr:odd').find('td').css({ 'background-color' : '#FFFFFF'});
						$('#forma-provfacturas-window').find('#div_warning_grid').find('#grid_warning').find('tr:even').find('td').css({ 'background-color' : '#e7e8ea'});
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					//Asignar los valores del IEPS al arreglo
					arrayIeps=entry['Ieps'];
					
					$tasa_fletes.attr({ 'value' : entry['tasaFletes']['0']['valor'] });
					$id_factura.attr({ 'value' : entry['datosFactura']['0']['id'] });
					$campo_factura.attr({ 'value' : entry['datosFactura']['0']['factura'] });
					$campo_ordencompra.attr({ 'value' : entry['datosFactura']['0']['orden_compra'] });
					$campo_numeroguia.attr({ 'value' : entry['datosFactura']['0']['numero_guia'] });
					$campo_expedicion.attr({ 'value' : entry['datosFactura']['0']['fecha_factura'] });
					$campo_tc.attr({ 'value' : entry['datosFactura']['0']['tipo_cambio'] });
					$campo_observaciones.text(entry['datosFactura']['0']['observaciones']);
										
					//carga el select de dias de credito
					$select_tipo_factura.children().remove();
					var tipo_hmtl = '';
					if(parseInt(entry['datosFactura']['0']['tipo_factura_proveedor'])==1){
						tipo_hmtl += '<option value="1" selected="yes">Factura de Compra(con Remisi&oacute;n previamente capturada)</option>';
					}
					if(parseInt(entry['datosFactura']['0']['tipo_factura_proveedor'])==2){
						tipo_hmtl += '<option value="2" selected="yes">Factura de Proveedor de Servicios u Honorarios</option>';
					}
					if(parseInt(entry['datosFactura']['0']['tipo_factura_proveedor'])==3){
						tipo_hmtl += '<option value="3" selected="yes">Facturas Varias(de Otros insumos)</option>';
					}
					if(parseInt(entry['datosFactura']['0']['tipo_factura_proveedor'])==4){
						tipo_hmtl += '<option value="4" selected="yes">Facturas de Fletes</option>';
						$campo_flete.attr("readonly", true);
					}
					$select_tipo_factura.append(tipo_hmtl);
					
					
					//carga el select de dias de credito
					$select_credito.children().remove();
					var credito_hmtl = '';
					$.each(entry['DiasCredito'],function(entryIndex,credito){
						if(parseInt(credito['id']) == parseInt(entry['datosFactura']['0']['dias_credito_id'])){
							credito_hmtl += '<option value="' + credito['id'] + '" selected="yes">' + credito['descripcion'] + '</option>';
						}else{
							credito_hmtl += '<option value="' + credito['id'] + '"  >' + credito['descripcion'] + '</option>';
						}
					});
					$select_credito.append(credito_hmtl);
					
					
					//carga select denominacion con todas las monedas
					$select_denominacion.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(parseInt(moneda['id']) == parseInt(entry['datosFactura']['0']['moneda_id'])){
							moneda_hmtl += '<option value="' + moneda['id'] + '" selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_denominacion.append(moneda_hmtl);
					
					$.each(entry['datosProveedor'],function(entryIndex,proveedor){
						$hidden_id_proveedor.attr({ 'value' : proveedor['id'] });
						$campo_rfc_proveedor.attr({ 'value' : proveedor['rfc'] });
						$campo_razon_proveedor.attr({ 'value' : proveedor['razon_social'] });
						//$campo_dir_proveedor.attr({ 'value' : proveedor['direccion'] });
						$campo_tipo_proveedor.attr({ 'value' : proveedor['proveedortipo_id'] });
					});
					
					//carga select de companias fleteras
					$select_fleteras.children().remove();
					var fletera_hmtl = '<option value="0">[--   --]</option>';
					//var fletera_hmtl = '';
					$.each(entry['Fleteras'],function(entryIndex,fletera){
						if(parseInt(fletera['id']) == parseInt(entry['datosFactura'][0]['fletera_id'])){
							fletera_hmtl += '<option value="' + fletera['id'] + '" selected="yes">' + fletera['razon_social'] + '</option>';
						}else{
							//fletera_hmtl += '<option value="' + fletera['id'] + '">' + fletera['razon_social'] + '</option>';
						}
					});
					$select_fleteras.append(fletera_hmtl);
					
					
					tiposIva = entry['Impuestos'];//asigna los tipos de impuestos al arreglo tiposIva
					
					
					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prodGrid){
							var trCount = $("tr", $grid_productos).size();
							trCount++;
							var valor_pedimento=" ";
							var tr_prod='';
							
							tr_prod += '<tr>';
								tr_prod += '<td width="65" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<a href="elimina_producto" id="eliminaprod'+ trCount +'">Eliminar</a>';
									tr_prod += '<input type="hidden" name="eliminado" id="eliminado" value="1">';//el 1 significa que el registro no ha sido eliminado
								tr_prod += '</td>';
								tr_prod += '<td width="100" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<INPUT TYPE="text" name="codigo" id="codigo'+ trCount +'" value="' + prodGrid['codigo_producto'] + '" class="borde_oculto" style="width:96px;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="200" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<INPUT TYPE="text" name="titulo" id="titulo'+ trCount +'" value="' + prodGrid['descripcion'] + '" class="borde_oculto" style="width:196px;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<INPUT TYPE="text" name="unidad" class="borde_oculto" value="' + prodGrid['unidad_medida'] + '" readOnly="true" style="width:66px;">';
								tr_prod += '</td>';
								
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<INPUT TYPE="text" name="presentacion" class="borde_oculto" value="' + prodGrid['presentacion'] + '" readOnly="true" style="width:66px;">';
								tr_prod += '</td>';
								
								tr_prod += '<td width="70" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<INPUT TYPE="text" name="cantidad" id="cant" value="' + prodGrid['cantidad'] + '" style="width:66px;">';
								tr_prod += '</td>';
								tr_prod += '<td width="75" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<INPUT TYPE="text" name="costo" id="cost" value="' + prodGrid['costo_unitario'] + '" style="width:69px;">';
								tr_prod += '</td>';
								
								tr_prod += '<td width="90" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<INPUT TYPE="text" name="importe'+ trCount +'" id="import" value="' + prodGrid['importe'] + '" style="width:86px;" readOnly="true">';
								tr_prod += '</td>';
								tr_prod += '<td width="82" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<SELECT name="impuesto" id="imp" style="width:80px;">';
									if(parseInt(prodGrid['gral_imp_id'])<=0){
										tr_prod += '<option value="0">[-- --]</option>';
									}
									//aqui se carga el select con los tipos de iva
									$.each(tiposIva,function(entryIndex,tipos){
										if(tipos['id'] == prodGrid['gral_imp_id']){
											tr_prod += '<option value="' + tipos['id'] + '"  selected="yes">' + tipos['descripcion'] + '</option>';
										}else{
											//tr_prod += '<option value="' + tipos['id'] + '"  >' + tipos['descripcion'] + '</option>';
										}
									});
									
									tr_prod += '</SELECT>';
									tr_prod += '<input type="hidden" name="valorimp" id="v_imp" value="0">';
									tr_prod += '<input type="hidden" name="totalimpuesto'+ trCount +'" id="totimp" value="0">';
								tr_prod += '</td>';
								
								
								
								tr_prod += '<td width="80" class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;">';
									
									tr_prod += '<select name="select_ieps" id="selectIeps" style="width:76px;">';
									if(parseInt(prodGrid['ieps_id'])<=0){
										tr_prod += '<option value="0">[-- --]</option>';
									}
									
									$.each(arrayIeps,function(entryIndex,ieps){
										if(parseInt(prodGrid['ieps_id'])==parseInt(ieps['id'])){
											tr_prod += '<option value="' + ieps['id'] + '"  selected="yes">' + ieps['titulo'] + '</option>';
										}
									});
									tr_prod += '</select>';
									tr_prod += '<input type="hidden" name="valorieps" id="tIeps" value="'+prodGrid['valor_ieps']+'">';
								tr_prod += '</td>';
								
								tr_prod += '<td width="84" class="grid" style="font-size:11px;  border:1px solid #C1DAD7;">';
									tr_prod += '<input type="text" name="importe_ieps'+ trCount +'" id="import_ieps" value="'+parseFloat(prodGrid['importe_ieps']).toFixed(4)+'" style="width:80px; text-align:right;" readOnly="true">';
								tr_prod += '</td>';
								
							tr_prod += '</tr>';
							$grid_productos.append(tr_prod);
							
							
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('input[name=codigo]').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							$grid_productos.find('input[name=codigo]').blur(function(){
								if ($(this).val() == ''  || $(this).val() == null){
								$(this).val(' ');
								}
							});
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('input[name=unidad]').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							$grid_productos.find('input[name=unidad]').blur(function(){
								if ($(this).val() == ''  || $(this).val() == null){
								$(this).val(' ');
								}
							});
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('input[name=presentacion]').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							$grid_productos.find('input[name=presentacion]').blur(function(){
								if ($(this).val() == ''  || $(this).val() == null){
								$(this).val(' ');
								}
							});
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('input[name=titulo]').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							$grid_productos.find('input[name=titulo]').blur(function(){
								if ($(this).val() == ''  || $(this).val() == null){
								$(this).val(' ');
								}
							});
							
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('input[name=costo]').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							//recalcula importe al perder enfoque el campo costo
							$grid_productos.find('input[name=costo]').blur(function(){
								if ($(this).val().trim()=='' || $(this).val()==null){
									$(this).val(' ');
								}
								
								if( ($(this).val().trim()!='') && ($(this).parent().parent().find('#cant').val().trim()!='') ){	
									//Calcular y redondear el importe
									$(this).parent().parent().find('#import').val( parseFloat(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cant').val())).toFixed(4) );
									
									//Calcular y redondear el importe del IEPS
									$(this).parent().parent().find('#import_ieps').val(parseFloat( parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($(this).parent().parent().find('#tIeps').val()) ).toFixed(4));
								}else{
									$(this).parent().parent().find('#import').val('');
								}
								$calcula_totales();//llamada a la funcion que calcula totales
							});
							
							//al iniciar el campo tiene un  caracter en blanco, al obtener el foco se elimina el  espacio por comillas
							$grid_productos.find('input[name=cantidad]').focus(function(e){
								if($(this).val() == ' '){
									$(this).val('');
								}
							});
							
							//recalcula importe al perder enfoque el campo cantidad
							$grid_productos.find('input[name=cantidad]').blur(function(){
								var $importePartida = $(this).parent().parent().find('#import');
								var $imprteIepsPartida = $(this).parent().parent().find('#import_ieps');
								var $tasaIepsPartida = $(this).parent().parent().find('#tIeps');
								
								if ($(this).val() == ''  || $(this).val() == null){
									$(this).val(' ');
								}
								if( $(this).val().trim()!='' && $(this).parent().parent().find('#cost').val().trim()!='' ){
									//Calcular y redondear el importe
									$importePartida.val( parseFloat(parseFloat($(this).val()) * parseFloat($(this).parent().parent().find('#cost').val())).toFixed(4) );
									
									//Calcular y redondear el importe del IEPS
									$imprteIepsPartida.val(parseFloat( parseFloat($importePartida.val()) * parseFloat($tasaIepsPartida.val()) ).toFixed(4));
								}else{
									$(this).parent().parent().find('#import').val('');
								}
								$calcula_totales();//llamada a la funcion que calcula totales
							});
							
							
							//validar campo costo, solo acepte numeros y punto
							$grid_productos.find('input[name=costo]').keypress(function(e){
								// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
								if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
									return true;
								}else {
									return false;
								}		
							});

							//validar campo cantidad, solo acepte numeros y punto
							$grid_productos.find('input[name=cantidad]').keypress(function(e){
								// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
								if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
									return true;
								}else {
									return false;
								}		
							});
							
							//elimina un producto del grid
							$grid_productos.find('#eliminaprod'+ trCount).bind('click',function(event){
								event.preventDefault();
								//asigna espacios en blanco a todos los input de la fila eliminada
								$(this).parent().parent().find('input').val(' ');
								//asigna un 0 al input eliminado como bandera para saber que esta eliminado
								$(this).parent().find('#eliminado').val(0);//cambiar valor del campo a 0 para indicar que se ha elimnado
								//oculta la fila eliminada
								$(this).parent().parent().hide();
								$calcula_totales();//llamada a la funcion que calcula totales
							});
							
							
							
							//Seleccionar tipo de  impuesto IVA
							$grid_productos.find('select[name=impuesto]').change(function(){
								var valorImpuesto=0;
								var id_ivatipo = $(this).val();
								$.each(tiposIva,function(entryIndex,tipos){
									if(parseInt(tipos['id'])==parseInt(id_ivatipo)){
										valorImpuesto = tipos['iva_1'];
									}
								});
								$(this).parent().find('input[name=valorimp]').val(valorImpuesto);
								
								$calcula_totales();//llamada a la funcion que calcula totales
							});
							
							
							//Seleccionar tipo de  impuesto IEPS
							$grid_productos.find('select[name=select_ieps]').change(function(){
								var valorIeps=0;
								var id_ieps = $(this).val();
								$.each(arrayIeps,function(entryIndex,ieps){
									valorIeps=0;
									if(parseInt(ieps['id'])==parseInt(id_ieps)){
										if(parseFloat(ieps['tasa'])>0){
											valorIeps = parseFloat(ieps['tasa'])/100;
										}
									}
								});
								$(this).parent().find('input[name=valorieps]').val(valorIeps);
								
								//Calcular y redondear el importe del IEPS
								$(this).parent().parent().find('#import_ieps').val(parseFloat( parseFloat($(this).parent().parent().find('#import').val()) * parseFloat($(this).parent().parent().find('#tIeps').val()) ).toFixed(4));
								
								$calcula_totales();//llamada a la funcion que calcula totales
							});
							
							
							
						});
						
						$campo_flete.val(parseFloat( entry['datosFactura']['0']['flete']).toFixed(2));
						
						$calcula_totales();//llamada a la funcion que calcula totales
					}
					
					
					
					if(entry['datosFactura'][0]['estado'] == 'CANCELADO'){
						$buscar_proveedor.hide();
						$busca_remision.hide();
						$agregar_concepto.hide();
						$select_denominacion.attr('disabled','-1'); //deshabilitar
						$select_fleteras.attr('disabled','-1'); //deshabilitar
						$select_tipo_factura.attr('disabled','-1'); //deshabilitar
						
						$campo_factura.attr('disabled','disabled'); 
						$campo_ordencompra.attr('disabled','disabled'); 
						$campo_numeroguia.attr('disabled','disabled'); 
						$campo_expedicion.attr('disabled','disabled'); 
						$campo_tc.attr('disabled','disabled'); 
						$campo_observaciones.attr('disabled','disabled'); 
						
						$grid_productos.find('a').hide();
						$grid_productos.find('#cost').attr("readonly", true);
						$grid_productos.find('#cant').attr("readonly", true);
						$grid_productos.find('#imp').attr('disabled','-1'); //deshabilitar
						//$grid_productos.find('#cad').attr("readonly", true);
						$select_credito.attr('disabled','-1'); //deshabilitar
						
						$cancelar_factura.hide();
						$submit_actualizar.hide();
					}
					
					
				},"json");//termina llamada json
				
				
				
				
				$campo_tc.keypress(function(e){
					// Permitir solo numeros, borrar, suprimir, TAB, punto
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//buscar proveedor
				$buscar_proveedor.click(function(event){
					event.preventDefault();
					//$busca_proveedores();
					jAlert("El proveedor no puede ser cambiado", 'Atencion!');
				});
				
				//agregar fila al grid
				$agregar_concepto.click(function(event){
					event.preventDefault();
					$agrega_fila_al_grid($grid_productos);
				});
		
				//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$campo_flete.focus(function(e){
					if(parseFloat($campo_flete.val())<1){
						$campo_flete.val('');
					}
				});
				
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$campo_flete.blur(function(e){
					if(parseFloat($campo_flete.val())==0||$campo_flete.val()==""){
						$campo_flete.val(0.0);
					}
					$calcula_totales();//llamada a la funcion que calcula totales
				});
				
				$campo_flete.keypress(function(e){
					//alert(e.which);
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				
				
				
				
				
				//cancelar 
				$cancelar_factura.click(function(event){
					event.preventDefault();
					var id_to_show = 0;
					$(this).modalPanel_cancelafactura();
					var form_to_show = 'formacancelafactura';
					$('#' + form_to_show).each (function(){this.reset();});
					var $forma_selected = $('#' + form_to_show).clone();
					$forma_selected.attr({id : form_to_show + id_to_show});
					$('#forma-cancelafactura-window').css({"margin-left": -100, 	"margin-top": -185});
					$forma_selected.prependTo('#forma-cancelafactura-window');
					$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
					
					var $select_tipo_cancelacion = $('#forma-cancelafactura-window').find('select[name=tipo_cancelacion]');
					var $motivo_cancelacion = $('#forma-cancelafactura-window').find('textarea[name=motivo_cancel]');
					
					var $cancel = $('#forma-cancelafactura-window').find('a[href*=cancela_factura]');
					var $exit = $('#forma-cancelafactura-window').find('a[href*=salir]');
					
					
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTiposCancelacion.json';
					$arreglo = {}
					
					$.post(input_json,$arreglo,function(entry){
						$select_tipo_cancelacion.children().remove();
						var tipo_hmtl = '';
						$.each(entry['Tipos'],function(entryIndex,tipo){
								tipo_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
						});
						$select_tipo_cancelacion.append(tipo_hmtl);
					});
					
					//cancela la entrada
					$cancel.click(function(event){
						event.preventDefault();
						if($motivo_cancelacion.val()!=null && $motivo_cancelacion.val()!=""){
							var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/cancelar_factura.json';
							$arreglo = {'id_factura':$id_factura.val(),
										'tipo_cancelacion':$select_tipo_cancelacion.val(),
										'motivo':$motivo_cancelacion.val(),
										'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
										}
							
							$.post(input_json,$arreglo,function(entry){
								//var cad = entry['success'].split(":");
								/*if(cad[1]=='false'){
									jAlert("La Entrada "+cad[0]+" tiene pagos aplicados. Es necesario cancelar primeramente los pagos y despues cancelar la factura.", 'Atencion!');
								}else{
									jAlert("La Entrada "+cad[0]+"  se ha cancelado con &eacute;xito", 'Atencion!');
									$cancelar_entrada.hide();
									$get_datos_grid();
								}*/
								
								if(entry['success']=='true'){
									jAlert("La Factura  se ha cancelado con &eacute;xito", 'Atencion!');
									$buscar_proveedor.hide();
									$busca_remision.hide();
									$agregar_concepto.hide();
									$select_denominacion.attr('disabled','-1'); //deshabilitar
									$select_fleteras.attr('disabled','-1'); //deshabilitar
									$select_tipo_factura.attr('disabled','-1'); //deshabilitar
									
									$campo_factura.attr('disabled','disabled'); 
									$campo_ordencompra.attr('disabled','disabled'); 
									$campo_numeroguia.attr('disabled','disabled'); 
									$campo_expedicion.attr('disabled','disabled'); 
									$campo_tc.attr('disabled','disabled'); 
									$campo_observaciones.attr('disabled','disabled'); 
									
									$grid_productos.find('a').hide();
									$grid_productos.find('#cost').attr("readonly", true);
									$grid_productos.find('#cant').attr("readonly", true);
									$grid_productos.find('#imp').attr('disabled','-1'); //deshabilitar
									$select_credito.attr('disabled','-1'); //deshabilitar
									
									$cancelar_factura.hide();
									$submit_actualizar.hide();
									$get_datos_grid();
								}
								
								var remove = function() {$(this).remove();};
								$('#forma-cancelafactura-overlay').fadeOut(remove);
							});//termina llamada json
						}else{
							jAlert("Es necesario ingresar el motivo de la cancelaci&oacute;n", 'Atencion!');
						}
					});
					
					$exit.click(function(event){
						event.preventDefault();
						var remove = function() {$(this).remove();};
						$('#forma-cancelafactura-overlay').fadeOut(remove);
					});
					
				});//termina cancelar Entrada
				
				
				
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $grid_productos).size();
					$total_tr.val(trCount);
                                        
					$grid_productos.find('tr').each(function (index){
						if($(this).find('#cad').val() == '' ){
							$(this).find('#cad').val(' ');
						}
						if($(this).find('input[name=codigo]').val() == '' ){
							$(this).find('input[name=codigo]').val(' ');
						}
						if($(this).find('input[name=titulo]').val() == '' ){
							$(this).find('input[name=titulo]').val(' ');
						}
						if($(this).find('input[name=unidad]').val() == '' ){
							$(this).find('input[name=unidad]').val(' ');
						}
						if($(this).find('input[name=presentacion]').val() == '' ){
							$(this).find('input[name=presentacion]').val(' ');
						}
					});
					
					return true;
				});
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-provfacturas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-provfacturas-overlay').fadeOut(remove);
				});
				
			}
		}
	}
        
        
        
        
	$get_datos_grid = function(){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllFacturas.json';
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
		$arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllFacturas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
		$.post(input_json,$arreglo,function(data){
			//pinta_grid
			//$.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaprovfacturas00_for_datagrid00);
			
			//aqui se utiliza el mismo datagrid que prefacturas. Solo muesta icono de detalles, el de eliminar No
			$.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaprovfacturas00_for_datagrid00);
			
			//resetea elastic, despues de pintar el grid y el slider
			Elastic.reset(document.getElementById('lienzo_recalculable'));
		},"json");
	}
	
    $get_datos_grid();
});



