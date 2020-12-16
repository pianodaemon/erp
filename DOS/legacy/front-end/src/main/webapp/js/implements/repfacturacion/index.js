$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Facturaci&oacute;n' ,                 
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
			return this.contextpath + "/controllers/repfacturacion";
			//  return this.controller;
		}
	};
	
	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
	
	$('#barra_acciones').hide();
	//barra para el buscador 
	$('#barra_buscador').hide();
	
	var $select_opciones = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=opciones]');
	var $factura = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=factura]');
	var $ciente = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=ciente]');
	var $fecha_inicial = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_final]');
	var $genera_reporte_facturacion = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $busqueda_reporte_facturacion= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_reporte_facturacion= $('#lienzo_recalculable').find('#divreportefacturacion');
	
	
	$select_opciones.children().remove();
	var hmtl_opciones;
	hmtl_opciones += '<option value="1"  >Ventas Totales</option>';
	hmtl_opciones += '<option value="2"  >Ventas a Filiales</option>';
	hmtl_opciones += '<option value="3"  >Ventas Netas</option>';
	$select_opciones.append(hmtl_opciones);
	
	$fecha_inicial.attr('readonly',true);
	$fecha_final.attr('readonly',true);
        
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
				var valida_fecha=mayor($fecha_inicial.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_inicial.val(mostrarFecha());
				}else{
					$fecha_inicial.DatePickerHide();	
				}
			}
		}
	});
		
	$fecha_inicial.click(function (s){
	var a=$('div.datepicker');
		a.css({'z-index':100});
	});
	$fecha_inicial.val(mostrarFecha());
			
			
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
			$fecha_inicial.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($fecha_final.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_final.val(mostrarFecha());
				}else{
					$fecha_final.DatePickerHide();	
				}
			}
		}
	});
			
        
        
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
				var valida_fecha=mayor($fecha_final.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_final.val(mostrarFecha());
                                        
                                         
                                       
				}else{
					$fecha_final.DatePickerHide();	
				}
			}
		}
	});
	
	
	$fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
    
	$fecha_final.val(mostrarFecha());
    
    
	//click generar reporte de facturacion
	$genera_reporte_facturacion.click(function(event){
		event.preventDefault();
		var busqueda = $select_opciones.val()+"___"+$factura.val()+"___"+$ciente.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val();
		var input_json = config.getUrlForGetAndPost() + '/get_genera_reporte_facturacion/'+busqueda+'/'+config.getUi()+'/out.json';
		window.location.href=input_json;
	});
	
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-repfacturacion-window').find('select[name=prodtipo]');
		$('#forma-repfacturacion-window').find('#submit').mouseover(function(){
			$('#forma-repfacturacion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		})
		$('#forma-repfacturacion-window').find('#submit').mouseout(function(){
			$('#forma-repfacturacion-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		})
		$('#forma-repfacturacion-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-repfacturacion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-repfacturacion-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-repfacturacion-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-repfacturacion-window').find('#close').mouseover(function(){
			$('#forma-repfacturacion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-repfacturacion-window').find('#close').mouseout(function(){
			$('#forma-repfacturacion-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-repfacturacion-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-repfacturacion-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-repfacturacion-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-repfacturacion-window').find("ul.pestanas li").click(function() {
			$('#forma-repfacturacion-window').find(".contenidoPes").hide();
			$('#forma-repfacturacion-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-repfacturacion-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	
	
	//ver detalles de una factura
	var ver_detalle_factura = function(id_to_show){
			
		//aqui  entra para editar un registro
		$('#forma-repfacturacion-window').remove();
		
		var form_to_show = 'formarepfacturacion00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$(this).modalPanel_repfacturacion();
		
		$('#forma-repfacturacion-window').css({"margin-left": -390, 	"margin-top": -290});
		
		$forma_selected.prependTo('#forma-repfacturacion-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
			
		var $total_tr = $('#forma-repfacturacion-window').find('input[name=total_tr]');
		var $id_factura = $('#forma-repfacturacion-window').find('input[name=id_factura]');
		var $folio_pedido = $('#forma-repfacturacion-window').find('input[name=folio_pedido]');
		var $busca_cliente = $('#forma-repfacturacion-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-repfacturacion-window').find('input[name=id_cliente]');
		var $rfc_cliente = $('#forma-repfacturacion-window').find('input[name=rfccliente]');
		var $razon_cliente = $('#forma-repfacturacion-window').find('input[name=razoncliente]');
		var $dir_cliente = $('#forma-repfacturacion-window').find('input[name=dircliente]');
		
		var $serie_folio = $('#forma-repfacturacion-window').find('input[name=serie_folio]');
		var $select_moneda = $('#forma-repfacturacion-window').find('select[name=moneda]');
		var $tipo_cambio = $('#forma-repfacturacion-window').find('input[name=tipo_cambio]');
		var $orden_compra = $('#forma-repfacturacion-window').find('input[name=orden_compra]');
		
		var $id_impuesto = $('#forma-repfacturacion-window').find('input[name=id_impuesto]');
		var $valor_impuesto = $('#forma-repfacturacion-window').find('input[name=valorimpuesto]');
		var $tasa_retencion = $('#forma-repfacturacion-window').find('input[name=tasa_retencion]');
		
		var $select_condiciones = $('#forma-repfacturacion-window').find('select[name=condiciones]');
		var $select_vendedor = $('#forma-repfacturacion-window').find('select[name=vendedor]');
		
		var $select_metodo_pago = $('#forma-repfacturacion-window').find('select[name=select_metodo_pago]');
		var $etiqueta_digit = $('#forma-repfacturacion-window').find('input[name=digit]');
		var $no_cuenta = $('#forma-repfacturacion-window').find('input[name=no_cuenta]');
		
		//grid de productos
		var $grid_productos = $('#forma-repfacturacion-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-repfacturacion-window').find('#div_warning_grid').find('#grid_warning');
		
		//Variables para totales de la Factura
		var $subtotal = $('#forma-repfacturacion-window').find('input[name=subtotal]');
		//ieps
		var $campo_ieps = $('#forma-repfacturacion-window').find('input[name=ieps]');
		var $impuesto = $('#forma-repfacturacion-window').find('input[name=impuesto]');
		var $impuesto_retenido = $('#forma-repfacturacion-window').find('input[name=impuesto_retenido]');
		var $total = $('#forma-repfacturacion-window').find('input[name=total]');
		var $saldo_fac = $('#forma-repfacturacion-window').find('input[name=saldo_fac]');
		
		var $observaciones = $('#forma-repfacturacion-window').find('textarea[name=observaciones]');
		var $nota_credito = $('#forma-repfacturacion-window').find('input[name=nota_credito]');
		
		//variables para totales de la Nota de Credito
		var $subtotal_nota = $('#forma-repfacturacion-window').find('input[name=subtotal_nota]');
		var $impuesto_nota = $('#forma-repfacturacion-window').find('input[name=impuesto_nota]');
		var $impuesto_retenido_nota = $('#forma-repfacturacion-window').find('input[name=impuesto_retenido_nota]');
		var $total_nota = $('#forma-repfacturacion-window').find('input[name=total_nota]');
		
		var $registrar_devolucion = $('#forma-repfacturacion-window').find('#registrar_devolucion');
		
		var $cerrar_plugin = $('#forma-repfacturacion-window').find('#close');
		var $cancelar_plugin = $('#forma-repfacturacion-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-repfacturacion-window').find('#submit');
		
		$submit_actualizar.hide();
		
			
		var arreglo_parametros = {	id_factura: id_to_show,
									iu:config.getUi()
								};
		var restful_json_service = config.getUrlForGetAndPost() + '/getFacturaDetalle.json'
		//aqui se cargan los campos al editar
		$.post(restful_json_service,arreglo_parametros,function(entry){
			$id_factura.val(entry['datosFactura']['0']['id']);
			$folio_pedido.val(entry['datosFactura']['0']['folio_pedido']);
			$id_cliente.val(entry['datosFactura']['0']['cliente_id']);
			$rfc_cliente.val(entry['datosFactura']['0']['rfc']);
			$razon_cliente.val(entry['datosFactura']['0']['razon_social']);
			$dir_cliente.val(entry['datosFactura']['0']['direccion']);
			$serie_folio.val(entry['datosFactura']['0']['serie_folio']);
			$observaciones.text(entry['datosFactura']['0']['observaciones']);
			$orden_compra.val(entry['datosFactura']['0']['orden_compra']);
			$tasa_retencion.val(entry['datosFactura']['0']['tasa_ret_immex']);
			
			$id_impuesto.val(entry['iva']['0']['id_impuesto']);
			$valor_impuesto.val(entry['iva']['0']['valor_impuesto']);
			$subtotal.val( $(this).agregar_comas(entry['datosFactura']['0']['subtotal']));
			//ieps monto
			$campo_ieps.val( $(this).agregar_comas(entry['datosFactura']['0']['monto_ieps']));
			$impuesto.val( $(this).agregar_comas( entry['datosFactura']['0']['impuesto']) );
			$impuesto_retenido.val( $(this).agregar_comas(entry['datosFactura']['0']['monto_retencion']));
			$total.val($(this).agregar_comas( entry['datosFactura']['0']['total']));
			$no_cuenta.val(entry['datosFactura']['0']['no_cuenta']);
			$tipo_cambio.val( entry['datosFactura']['0']['tipo_cambio'] );
			//ieps suma
			var sumaIeps = entry['datosFactura']['0']['monto_ieps'];
			var impuestoRetenido = entry['datosFactura']['0']['monto_retencion'];
			
			//Ocultar campos si tienen valor menor o igual a cero
			if(parseFloat(sumaIeps)<=0){
				$('#forma-facconsultas-window').find('#tr_ieps').hide();
			}
			if(parseFloat(impuestoRetenido)<=0){
				$('#forma-facconsultas-window').find('#tr_retencion').hide();
			}
			
			if(parseFloat(sumaIeps)>0 && parseFloat(impuestoRetenido)<=0){
				$('#forma-facconsultas-window').find('.facconsultas_div_one').css({'height':'525px'});
			}
			
			if(parseFloat(sumaIeps)<=0 && parseFloat(impuestoRetenido)>0){
				$('#forma-facconsultas-window').find('.facconsultas_div_one').css({'height':'525px'});
			}
			
			if(parseFloat(sumaIeps)<=0 && parseFloat(impuestoRetenido)<=0){
				$('#forma-facconsultas-window').find('.facconsultas_div_one').css({'height':'500px'});
			}
			
			if(parseFloat(sumaIeps)>0 && parseFloat(impuestoRetenido)>0){
				$('#forma-facconsultas-window').find('.facconsultas_div_one').css({'height':'550px'});
			}
			
			//form pago 2=Tarjeta Credito, 3=Tarjeta Debito
			if(parseInt(entry['datosFactura']['0']['fac_metodos_pago_id'])==2 || parseInt(entry['datosFactura']['0']['fac_metodos_pago_id']==3)){
				$etiqueta_digit.val('Ingrese los ultimos 4 Digitos de la Tarjeta');
			}
			
			//form pago 4=Cheque Nominativo, 5=Transferencia Electronica de Fondos
			if(parseInt(entry['datosFactura']['0']['fac_metodos_pago_id'])==4 || parseInt(entry['datosFactura']['0']['fac_metodos_pago_id']==5)){
				if(parseInt(entry['datosFactura']['0']['moneda_id'])==1){
					$etiqueta_digit.val('Numero de Cuenta para pagos en Pesos');
				}else{
					$etiqueta_digit.val('Numero de Cuenta para pagos en Dolares');
				}
			}
			
			
			//carga select denominacion con todas las monedas
			$select_moneda.children().remove();
			//var moneda_hmtl = '<option value="0">[--   --]</option>';
			var moneda_hmtl = '';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				if(moneda['id'] == entry['datosFactura']['0']['moneda_id']){
					moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
				}else{
					//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
				}
			});
			$select_moneda.append(moneda_hmtl);
			
			
			//carga select de vendedores
			$select_vendedor.children().remove();
			var hmtl_vendedor;
			$.each(entry['Vendedores'],function(entryIndex,vendedor){
				if(entry['datosFactura']['0']['cxc_agen_id'] == vendedor['id']){
					hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_vendedor'] + '</option>';
				}else{
					//hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_vendedor'] + '</option>';
				}
			});
			$select_vendedor.append(hmtl_vendedor);
			
			
			//carga select de condiciones
			$select_condiciones.children().remove();
			var hmtl_condiciones;
			$.each(entry['Condiciones'],function(entryIndex,condicion){
				if(entry['datosFactura']['0']['terminos_id'] == condicion['id']){
					hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
				}else{
					//hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
				}
			});
			$select_condiciones.append(hmtl_condiciones);
			
			
			//carga select de metodos de pago
			$select_metodo_pago.children().remove();
			var hmtl_metodo;
			$.each(entry['MetodosPago'],function(entryIndex,metodo){
				if(entry['datosFactura']['0']['fac_metodos_pago_id'] == metodo['id']){
					hmtl_metodo += '<option value="' + metodo['id'] + '"  selected="yes">' + metodo['titulo'] + '</option>';
				}else{
					//hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
				}
			});
			$select_metodo_pago.append(hmtl_metodo);
			
			
			$busca_cliente.hide();
			
			var desactivado="";
			var check="";
			var valor_seleccionado="";
			if(entry['datosGrid'] != null){
				$.each(entry['datosGrid'],function(entryIndex,prod){
					
					//obtiene numero de trs
					var tr = $("tr", $grid_productos).size();
					tr++;
					
					var trr = '';
					trr = '<tr>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="116">';
							trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['inv_prod_id'] +'">';
							trr += '<INPUT TYPE="text" name="sku'+ tr +'" value="'+ prod['codigo_producto'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:112px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="250">';
						trr += '<INPUT TYPE="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:246px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
						trr += '<INPUT TYPE="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:96px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
							trr += '<INPUT type="hidden" 	name="id_presentacion"  value="'+  prod['id_presentacion'] +'" 	id="idpres">';
							trr += '<INPUT TYPE="text" 		name="presentacion'+ tr +'" 	value="'+  prod['presentacion'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:96px;">';
					trr += '</td>';
					trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
						trr += '<INPUT TYPE="text" 	name="cantidad" value="'+  prod['cantidad'] +'" 		id="cant" style="width:76px; text-align:right;">';
					trr += '</td>';
					trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<INPUT TYPE="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" style="width:76px; text-align:right;">';
					trr += '</td>';
					trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
						trr += '<INPUT TYPE="text" 	name="importe'+ tr +'" 	value="'+  $(this).agregar_comas( prod['importe'] )  +'" 	id="import" readOnly="true" style="width:86px; text-align:right;">';
						trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+  parseFloat(prod['importe']) * parseFloat($valor_impuesto.val()) +'">';
						trr += '<INPUT type="hidden"    name="valor_imp"     	value="'+  $valor_impuesto.val() +'" id="ivalorimp">';
					trr += '</td>';
					
					var tasaIeps="";
							var importeIeps="";
							
							if(parseInt(prod['id_ieps'])>0){
								tasaIeps=prod['tasa_ieps'];
								importeIeps=prod['importe_ieps'];
							}
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="50">';
								trr += '<input type="hidden" name="idIeps"     value="'+ prod['id_ieps'] +'" id="idIeps">';
								trr += '<input type="text" name="tasaIeps" value="'+ tasaIeps +'" class="borde_oculto" id="tasaIeps" style="width:46px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="64">';
								trr += '<input type="text" name="importeIeps" value="'+ importeIeps +'" class="borde_oculto" id="importeIeps" style="width:60px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
					
					trr += '</tr>';
					$grid_productos.append(trr);
					
				});
			}
			
			$observaciones.attr("readonly", true);
			$tipo_cambio.attr("readonly", true);
			$folio_pedido.attr("readonly", true);
			$orden_compra.attr("readonly", true);
			$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
			$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid					
			
			
			//si el estado del comprobante es 0, esta cancelado
			if(entry['datosFactura']['0']['estado']=='CANCELADO'){
				$tipo_cambio.val(entry['datosFactura']['0']['tipo_cambio']);
				$rfc_cliente.attr('disabled','-1'); //deshabilitar
				$folio_pedido.attr('disabled','-1'); //deshabilitar
				$razon_cliente.attr('disabled','-1'); //deshabilitar
				$dir_cliente.attr('disabled','-1'); //deshabilitar
				$serie_folio.attr('disabled','-1'); //deshabilitar
				$observaciones.attr('disabled','-1'); //deshabilitar
				$select_moneda.attr('disabled','-1'); //deshabilitar
				$tipo_cambio.attr('disabled','-1'); //deshabilitar
				$select_vendedor.attr('disabled','-1'); //deshabilitar
				$select_condiciones.attr('disabled','-1'); //deshabilitar
				$orden_compra.attr('disabled','-1'); //deshabilitar
				$select_metodo_pago.attr('disabled','-1'); //deshabilitar
				$no_cuenta.attr('disabled','-1'); //deshabilitar
				$grid_productos.find('#cant').attr('disabled','-1'); //deshabilitar
				$grid_productos.find('#cost').attr('disabled','-1'); //deshabilitar
				$grid_productos.find('#import').attr('disabled','-1'); //deshabilitar
				
				$subtotal.attr('disabled','-1'); //deshabilitar
				$campo_ieps.attr('disabled','-1'); //deshabilitar
				$impuesto.attr('disabled','-1'); //deshabilitar
				$impuesto_retenido.attr('disabled','-1'); //deshabilitar
				$total.attr('disabled','-1'); //deshabilitar
			}
		});//termina llamada json
		
		//Ligamos el boton cancelar al evento click para eliminar la forma
		$cancelar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-repfacturacion-overlay').fadeOut(remove);
		});
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-repfacturacion-overlay').fadeOut(remove);
		});
		
	}

	
	//aplica evento a  campos del grid
	var aplicar_click_a_href = function($tabla){
		$tabla.find('tr').each(function(){
			//aplicar click a los campso check del grid
			$(this).find('a[href=ver_detalle]').click(function(event){
				event.preventDefault();
				var id_factura = $(this).parent().find('input[name=fac]').val();
				
				ver_detalle_factura(id_factura);
			});
		});
     }
	
	
	
	
	$busqueda_reporte_facturacion.click(function(event){
		event.preventDefault();
		$div_reporte_facturacion.children().remove();
		var fecha_inicial = $fecha_inicial.val();
		var fecha_final = $fecha_final.val();
		
		if(fecha_inicial != "" && fecha_final != ""){
			
			var arreglo_parametros = {	opcion: $select_opciones.val(),
										factura: $factura.val(),
										cliente: $ciente.val(),
										fecha_inicial : $fecha_inicial.val(), 
										fecha_final : $fecha_final.val(), 
										iu:config.getUi()
									};
			
			var restful_json_service = config.getUrlForGetAndPost() + '/getFacturacion.json'
			var cliente="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				var body_tabla = entry['Facturas'];
				var footer_tabla = entry['Totales'];
				var header_tabla = {
					serie_folio		:'Factura',
					orden_compra	:'O. Compra',
					fecha_factura	:'Fecha',
					cliente			:'Cliente',
					moneda_subtotal	:'',
					subtotal  		:'Sub-Total',
					moneda_ieps    	:'',
					iepscampo  		:'IEPS',
					moneda_iva    	:'',
					impuesto  		:'IVA',
					moneda_total    :'',
					total    		:'Total'
				};

									
				var TPventa_neta=0.0;
				var Sumatoriaventa_neta = 0.0;
				var sumatotoriaporciento = 0.0;
				var html_reporte = '<table id="ventas" >';
				var porcentaje = 0.0;
				
				var numero_control=0.0; 
				var cliente=0.0; 
				var moneda="$"; 
				var venta_neta=0.0; 
				var porciento=0.0;
				var tmp= 0;
				
				html_reporte +='<thead> <tr>';
				for(var key in header_tabla){
					var attrValue = header_tabla[key];
					if(attrValue == "Factura"){
						html_reporte +='<td width="75px" align="left">'+attrValue+'</td>'; 
					}
					if(attrValue == "O. Compra"){
						html_reporte +='<td width="100px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Fecha"){
						html_reporte +='<td width="75px" align="left">'+attrValue+'</td>'; 
					}
					if(attrValue == "Cliente"){
						html_reporte +='<td width="390px" align="left">'+attrValue+'</td>'; 
					}
					if(attrValue == ''){
						html_reporte +='<td width="5px" align="right" id="simbolo_moneda">'+attrValue+'</td>'; 
					}
					if(attrValue == "Sub-Total"){
						html_reporte +='<td width="80px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					if(attrValue == "IEPS"){
						html_reporte +='<td width="80px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					if(attrValue == "IVA"){
						html_reporte +='<td width="75px" align="left" id="monto">'+attrValue+'</td>'; 
					}
					if(attrValue == "Total"){
						html_reporte +='<td width="100px" align="left" id="monto">'+attrValue+'</td>'; 
					}
				}
				html_reporte +='</tr> </thead>';
				var orden_compra="";
				var simbolo_moneda="";
				for(var i=0; i<body_tabla.length; i++){
					if(body_tabla[i]["orden_compra"]==null){
						orden_compra="";
					}else{
						orden_compra=body_tabla[i]["orden_compra"];
					}
					
					simbolo_moneda=body_tabla[i]["simbolo_moneda"];
					
					html_reporte +='<tr>';
					html_reporte +='<td align="left" >';
						html_reporte +='<a href="ver_detalle" class="detalle_item" title="Ver detalle">'+body_tabla[i]["serie_folio"]+'</a>';
						html_reporte += '<input type="hidden" name="fac" id="selec" value="'+body_tabla[i]["id"]+'">';
					html_reporte +='</td>'; 
					html_reporte +='<td align="left" >'+orden_compra+'</td>'; 
					html_reporte +='<td align="left" >'+body_tabla[i]["fecha_factura"]+'</td>'; 
					html_reporte +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["subtotal"]).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_ieps"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["impuesto"]).toFixed(2))+'</td>'; 					
					html_reporte +='<td align="right" id="simbolo_moneda">'+simbolo_moneda+'</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["total"]).toFixed(2))+'</td>';
					html_reporte +='</tr>';
				}
				
				html_reporte +='<tfoot>';
					html_reporte +='<tr>';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="right" id="sin_borde">Total M.N.</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_subtotal"]).toFixed(2))+'</td>'; 
					
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_monto_ieps"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_impuesto"]).toFixed(2))+'</td>'; 					
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_total"]).toFixed(2))+'</td>';
					html_reporte +='</tr>';
					
					html_reporte +='<tr>';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="left"  id="sin_borde"></td>'; 
					html_reporte +='<td align="right" id="sin_borde">Total USD</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">USD</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_subtotal"]).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">USD</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_monto_ieps"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">USD</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_impuesto"]).toFixed(2))+'</td>'; 					
					html_reporte +='<td align="right" id="simbolo_moneda">USD</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_total"]).toFixed(2))+'</td>';
					html_reporte +='</tr>';
					
					html_reporte +='<tr>';
					html_reporte +='<td align="left" id="sin_borde_derecho"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="left" id="sin_borde"></td>'; 
					html_reporte +='<td align="right" id="sin_borde">Total General en M.N.</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_subtotal_mn"]).toFixed(2))+'</td>';
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_monto_ieps_mn"]).toFixed(2))+'</td>'; 
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_impuesto_mn"]).toFixed(2))+'</td>'; 					
					html_reporte +='<td align="right" id="simbolo_moneda">$</td>'; 
					html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_total_mn"]).toFixed(2))+'</td>';
					html_reporte +='</tr>';
					
				html_reporte +='</tfoot>';
				
				html_reporte += '</table>';
				
				
				
				$div_reporte_facturacion.append(html_reporte); 
				
				var $tabla = $div_reporte_facturacion.find('#ventas');
				
				
				aplicar_click_a_href($tabla);
				
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-300;
				var pix_alto=alto+'px';
				$('#ventas').tableScroll({height:parseInt(pix_alto)});
			});
			
			$factura.focus();
		}else{
			jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
		}
	});
	
	
	$(this).aplicarEventoKeypressEjecutaTrigger($select_opciones, $busqueda_reporte_facturacion);
	$(this).aplicarEventoKeypressEjecutaTrigger($factura, $busqueda_reporte_facturacion);
	$(this).aplicarEventoKeypressEjecutaTrigger($ciente, $busqueda_reporte_facturacion);
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_inicial, $busqueda_reporte_facturacion);
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_final, $busqueda_reporte_facturacion);
	
	$factura.focus();
	
});   
        
        
        
        
    
