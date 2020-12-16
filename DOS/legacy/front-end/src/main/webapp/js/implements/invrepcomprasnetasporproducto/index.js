$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de Compras Netas por Producto' ,                 
		contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),
		
		userName : $('#lienzo_recalculable').find('input[name=user]').val(),
		ui : $('#lienzo_recalculable').find('input[name=iu]').val(),
		
		getUrlForGetAndPost : function(){
			var url = document.location.protocol + '//' + document.location.host + this.getController();
			return url;
		},
		
		getUserName: function(){
			return this.userName;
		},
		
		getEmp: function(){
			return this.empresa;
		},
		
		getSuc: function(){
			return this.sucursal;
		},

		getUi: function(){
			return this.ui;
		},
		getTituloApp: function(){
			return this.tituloApp;
		},

		getController: function(){
			return this.contextpath + "/controllers/invrepcomprasnetasporproducto";
			//  return this.controller;
		}
	};
	
	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
        $('#header').find('#header1').find('span.username').text(config.getUserName());
	$('#barra_acciones').hide();
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
	
	//barra para el buscador 
	$('#barra_buscador').hide();
	
	var $select_opciones = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=opciones]');
	var $no_prov = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=no_prov]');
	var $proveedor = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=proveedor]');
	var $producto = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=producto]');
	var $fecha_inicial = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=fecha_final]');
	
	var $buscar_producto= $('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=buscar_producto]');
	var $buscar_proveedor= $('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=buscar_proveedor]');
	
	var $genera_pdf_reporte = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $busqueda_reporte= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_reporte= $('#lienzo_recalculable').find('#divreportecomprasnetas');
	
	
	$select_opciones.children().remove();
	var hmtl_opciones;
	hmtl_opciones += '<option value="1"  >Por Producto</option>';
	hmtl_opciones += '<option value="2"  >Por Proveedor</option>';
        hmtl_opciones += '<option value="3"  >++ Producto </option>';
        hmtl_opciones += '<option value="4"  >++ Proveedor</option>';
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
    
    





	//buscador de proveedores
	$busca_proveedores = function($no_proveedor, $proveedor){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -150  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_no_proveedor = $('#forma-buscaproveedor-window').find('input[name=campo_no_proveedor]');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
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
	
		$campo_no_proveedor.val($no_proveedor.val());
		$campo_nombre.val($proveedor.val());
		
		$campo_nombre.focus();
		
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			
			var input_json = config.getUrlForGetAndPost() + '/getBuacadorProveedores.json'
			
			$arreglo = {    rfc:$campo_rfc.val(),
							no_prov:$campo_no_proveedor.val(),
							nombre:$campo_nombre.val(),
							iu:config.getUi()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['Proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							trr += '<input type="hidden" id="no_prov" value="'+proveedor['no_proveedor']+'">';
							trr += '<input type="hidden" id="dias_cred_id" value="'+proveedor['dias_credito_id']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+proveedor['moneda_id']+'">';
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
					
					//asigna la razon social del proveedor al campo correspondiente
					$proveedor.val($(this).find('#razon_social').html());
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
					
					$proveedor.focus();
				});
			});
		});
		
		if ($campo_no_proveedor.val()!='' || $campo_nombre.val()!=''){
			$buscar_plugin_proveedor.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_no_proveedor, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_rfc, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_nombre, $buscar_plugin_proveedor);
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
			
			$proveedor.focus();
		});
	}//termina buscador de proveedores
	








	//Buscador de productos
	$busca_productos = function(nombre_producto){
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());
		
		$('#forma-buscaproducto-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
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
		var input_json_tipos = config.getUrlForGetAndPost() + '/getProductoTipos.json'
		$arreglo = { iu:config.getUi() 		};
		$.post(input_json_tipos,$arreglo,function(data){
			//Llena el select tipos de productos en el buscador
			$select_tipo_producto.children().remove();
			var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			$.each(data['prodTipos'],function(entryIndex,pt){
				prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
			});
			$select_tipo_producto.append(prod_tipos_html);
		});
		
		$campo_descripcion.val(nombre_producto);
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			//event.preventDefault();
			$arreglo = {    sku:$campo_sku.val(),
							tipo:$select_tipo_producto.val(),
							descripcion:$campo_descripcion.val(),
							iu:config.getUi()
						};
						
			var restful_json_service = config.getUrlForGetAndPost() + '/getBuscadorProductos.json'
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(restful_json_service,$arreglo,function(entry){
				$.each(entry['Productos'],function(entryIndex,producto){
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
					//asignar  descripcion
					$producto.val($(this).find('span.titulo_prod_buscador').html());
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproducto-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-entradamercancias-window').find('input[name=sku_producto]').focus();
				});

			});
		})
		
		
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if($campo_descripcion.val() != ''){
			$buscar_plugin_producto.trigger('click');
		}

		$cancelar_plugin_busca_producto.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproducto-overlay').fadeOut(remove);
		});
	}//termina buscador de productos
	

	
	
   $buscar_proveedor.click(function(event){
        event.preventDefault();
        $busca_proveedores($no_prov, $proveedor);//llamada a la funcion que busca proveedores
    });
    
    
    $buscar_producto.click(function(event){
        event.preventDefault();
        $busca_productos($producto.val());//llamada a la funcion que busca productos
    });





    
	//click generar pdf
	$genera_pdf_reporte.click(function(event){
		event.preventDefault();
		var cadena = $select_opciones.val()+"___"+$proveedor.val()+"___"+$producto.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val();
		var input_json = config.getUrlForGetAndPost() + '/getPdfReporteComprasNetasPorProducto/'+cadena+'/'+config.getUi()+'/out.json';
		window.location.href=input_json;
	});
		
	
	$busqueda_reporte.click(function(event){
		event.preventDefault();
                $div_reporte.children().remove();
                if ($select_opciones.val() == 1 || $select_opciones.val() == 2){
			
                        var arreglo_parametros = {	tipo_reporte: $select_opciones.val(),
                                                        proveedor: $proveedor.val(),
                                                        producto: $producto.val(),
                                                        fecha_inicial: $fecha_inicial.val(),
                                                        fecha_final: $fecha_final.val(),
                                                        iu:config.getUi()
                                                 };

                        var restful_json_service = config.getUrlForGetAndPost() + '/getReporteComprasNetasPorProducto.json'
                        var cliente="";
                        $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry['Compras'];
			
                            
                            
                        
			//var footer_tabla = entry['Totales'];
			var header_tabla = {
                                        clave_proveedor     :'Clave',
                                        proveedor           :'Proveedor',				
                                        codigo_producto     :'Codigo',
                                        producto            :'Producto',
                                        factura             :'Factura',
                                        fecha               :'Fecha',
                                        unidad              :'Unidad',
                                        cantidad            :'Cantidad',
                                        moneda_costo        :'',
                                        costo_unitario      :'Costo Unitario',
                                        moneda              :'Moneda',
                                        moneda_tipo_cambio  :'',
                                        tipo_cambio         :'Tipo Cambio',
                                        moneda_compra_neta  :'',
                                        compra_neta_mn      :'Compra Neta en M.N.'
			};
			
			var header_clave_codigo="";
			var header_producto_proveedor="";
                        
			if(parseInt($select_opciones.val())==1){
				header_clave_codigo="Clave";
				header_producto_proveedor="Proveedor";
			}
			
                        if(parseInt($select_opciones.val())==2){
				header_clave_codigo="Codigo";
				header_producto_proveedor="Producto";
			}			
			
			var html_reporte = '<table id="reporte">';
			var html_fila_vacia='';
			var html_footer = '';
			
			html_reporte +='<thead> <tr>';
			for(var key in header_tabla){
				var attrValue = header_tabla[key];
				if(attrValue == header_clave_codigo){
					html_reporte +='<td width="80px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == header_producto_proveedor){
					html_reporte +='<td width="200px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Factura"){
					html_reporte +='<td width="90px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Fecha"){
					html_reporte +='<td width="80px" align="left">'+attrValue+'</td>'; 
				}
				if(attrValue == "Unidad"){
					html_reporte +='<td width="70px" align="left">'+attrValue+'</td>'; 
				}				
				if(attrValue == "Cantidad"){
					html_reporte +='<td width="80px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == ''){
					html_reporte +='<td width="10px" align="right" >'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Costo Unitario"){
					html_reporte +='<td width="90px" align="left" id="monto">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Moneda"){
					html_reporte +='<td width="60px" align="left">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Tipo Cambio"){
					html_reporte +='<td width="80px" align="left"  id="monto">'+attrValue+'</td>'; 
				}
				
				if(attrValue == "Compra Neta en M.N."){
					html_reporte +='<td width="130px" align="left" id="monto">'+attrValue+'</td>'; 
				}

			}
			html_reporte +='</tr> </thead>';
				
			html_fila_vacia +='<tr>';
			html_fila_vacia +='<td width="80px" align="left"  id="sin_borde" height="10"></td>';
			html_fila_vacia +='<td width="200px" align="left"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="left" id="sin_borde"></td>';
			html_fila_vacia +='<td width="80px" align="center" id="sin_borde"></td>';
			html_fila_vacia +='<td width="70px" align="left" id="sin_borde"></td>';
			html_fila_vacia +='<td width="80px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="60px" align="left" id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="80px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="130px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='</tr>';
			
			var simbolo_moneda="";
			//esta variable toma el campo de ordenamiento dependiendo del tipo de reporte
			var campo_ordenamiento_actual="";
			
			//inicializar variables
			var suma_parcial=0.0;//esta variable es para las sumas parciales, puede ser por producto o proveedor dependiento del tipo de reporte
			var suma_general=0.0;
			var suma_cantidad_unidad=0.0;
                        
                                if(parseInt(body_tabla.length)>0){

                                        var campo_proveedor_producto_actual="";
                                        var unidad="";
                                        //estas dos variables indican el indice que debe tomar del arreglo dependiendo del timpo de reporte
                                        var campo_clave_codigo="";
                                        var campo_proveedor_producto="";
                                        var campo_comparador="";

                                        if(parseInt($select_opciones.val())==1){
                                                campo_proveedor_producto_actual=body_tabla[0]["producto"];
                                                unidad=body_tabla[0]["unidad"];
                                        }
                                        if(parseInt($select_opciones.val())==2){
                                                campo_proveedor_producto_actual=body_tabla[0]["proveedor"];
                                                unidad=body_tabla[0]["unidad"];
                                        }
                                        /*
                                        html_reporte +='<tr class="first">';
                                        html_reporte +='<td width="80px" align="left"  id="sin_borde"></td>';
                                        html_reporte +='<td width="200px" align="left"  id="sin_borde"></td>';
                                        html_reporte +='<td width="90px" align="left" id="sin_borde"></td>';
                                        html_reporte +='<td width="80px" align="center" id="sin_borde"></td>';
                                        html_reporte +='<td width="70px" align="left" id="sin_borde"></td>';
                                        html_reporte +='<td width="80px" align="right" id="sin_borde"></td>';
                                        html_reporte +='<td width="10px" align="right" id="sin_borde"></td>';
                                        html_reporte +='<td width="90px" align="right"  id="sin_borde"></td>';
                                        html_reporte +='<td width="60px" align="left" id="sin_borde"></td>';
                                        html_reporte +='<td width="10px" align="right" id="sin_borde"></td>';
                                        html_reporte +='<td width="80px" align="right" id="sin_borde"></td>';
                                        html_reporte +='<td width="10px" align="right" id="sin_borde"></td>';
                                        html_reporte +='<td width="130px" align="right" id="sin_borde"></td>';
                                        html_reporte +='</tr>';
                                        */
                                        html_reporte +='<tr id="tr_totales"><td align="left" colspan="13">'+campo_proveedor_producto_actual+'</td></tr>';
                                        for(var i=0; i<body_tabla.length; i++){

                                                if(parseInt($select_opciones.val())==1){
                                                        campo_clave_codigo="clave_proveedor";
                                                        campo_proveedor_producto="proveedor";
                                                        campo_comparador="producto";

                                                }
                                                if(parseInt($select_opciones.val())==2){
                                                        campo_clave_codigo="codigo_producto";
                                                        campo_proveedor_producto="producto";
                                                        campo_comparador="proveedor";

                                                }

                                                if(body_tabla[i]["moneda"]=="M.N."){
                                                        simbolo_moneda="$";
                                                }else{
                                                        simbolo_moneda=body_tabla[i]["moneda"];
                                                }


                                                if(campo_proveedor_producto_actual == body_tabla[i][campo_comparador] && unidad == body_tabla[i]["unidad"] ){
                                                        html_reporte +='<tr>';
                                                        html_reporte +='<td width="80 px" align="left" >'+body_tabla[i][campo_clave_codigo]+'</td>';
                                                        html_reporte +='<td width="200px" align="left" >'+body_tabla[i][campo_proveedor_producto]+'</td>';
                                                        html_reporte +='<td width="90 px" align="left" >'+body_tabla[i]["factura"]+'</td>';
                                                        html_reporte +='<td width="80 px" align="center" >'+body_tabla[i]["fecha"]+'</td>';
                                                        html_reporte +='<td width="70 px" align="left" >'+body_tabla[i]["unidad"]+'</td>';
                                                        html_reporte +='<td width="80 px" align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                        html_reporte +='<td width="10 px" align="left" id="simbolo_moneda">'+simbolo_moneda+'</td>';
                                                        html_reporte +='<td width="90 px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["costo_unitario"]).toFixed(2))+'</td>';
                                                        html_reporte +='<td width="60 px" align="left" >'+body_tabla[i]["moneda"]+'</td>';
                                                        html_reporte +='<td width="10 px" align="left" id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td width="80 px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(4))+'</td>';
                                                        html_reporte +='<td width="10 px" align="left" id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td width="130px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["compra_neta_mn"]).toFixed(2))+'</td>';
                                                        html_reporte +='</tr>';

                                                        suma_parcial=parseFloat(suma_parcial) + parseFloat(body_tabla[i]["compra_neta_mn"]);
                                                        suma_general=parseFloat(suma_general) + parseFloat(body_tabla[i]["compra_neta_mn"]);

                                                        suma_cantidad_unidad=parseFloat(suma_cantidad_unidad) + parseFloat(body_tabla[i]["cantidad"]);
                                                        unidad=body_tabla[i]["unidad"];


                                                }else{
                                                        //imprimir totales
                                                        html_reporte +='<tr id="tr_totales ">';
                                                        html_reporte +='<td align="left"   id="sin_borde_derecho" ></td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="right" id="sin_borde" >Total:</td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" >'+unidad+'</td>';
                                                        html_reporte +='<td align="right"   id="sin_borde" >'+$(this).agregar_comas(parseFloat(suma_cantidad_unidad).toFixed(2))+'</td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="right"  id="sin_borde" ></td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="right"  id="sin_borde" >Total</td>';
                                                        html_reporte +='<td align="left"   id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_parcial).toFixed(2))+'</td>';
                                                        html_reporte +='</tr>';

                                                        //fila vacia
                                                        html_reporte +=html_fila_vacia;

                                                        //reinicializar varibles
                                                        unidad="";
                                                        suma_parcial=0.0;
                                                        suma_cantidad_unidad=0.0;


                                                        //tomar razon social del proveedor o descripcion del producto, dependiendo del timpo de reporte
                                                        campo_proveedor_producto_actual=body_tabla[i][campo_comparador];

                                                        html_reporte +='<tr id="tr_totales"><td align="left" colspan="13">'+campo_proveedor_producto_actual+'</td></tr>';
                                                        //crear primer registro del nuevo cliente
                                                        html_reporte +='<tr>';
                                                        html_reporte +='<td width="80px" align="left" >'+body_tabla[i][campo_clave_codigo]+'</td>';
                                                        html_reporte +='<td width="200px" align="left" >'+body_tabla[i][campo_proveedor_producto]+'</td>';
                                                        html_reporte +='<td width="90px" align="left" >'+body_tabla[i]["factura"]+'</td>';
                                                        html_reporte +='<td width="80px" align="center" >'+body_tabla[i]["fecha"]+'</td>';
                                                        html_reporte +='<td width="70px" align="left" >'+body_tabla[i]["unidad"]+'</td>';
                                                        html_reporte +='<td width="80px" align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                        html_reporte +='<td width="10px" align="left" id="simbolo_moneda">'+simbolo_moneda+'</td>';
                                                        html_reporte +='<td width="90px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["costo_unitario"]).toFixed(2))+'</td>';
                                                        html_reporte +='<td width="60px" align="left" >'+body_tabla[i]["moneda"]+'</td>';
                                                        html_reporte +='<td width="10px" align="left" id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td width="80px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(4))+'</td>';
                                                        html_reporte +='<td width="10px" align="left" id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td width="130px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["compra_neta_mn"]).toFixed(2))+'</td>';
                                                        html_reporte +='</tr>';

                                                        suma_parcial=parseFloat(suma_parcial) + parseFloat(body_tabla[i]["compra_neta_mn"]);
                                                        suma_general=parseFloat(suma_general) + parseFloat(body_tabla[i]["compra_neta_mn"]);
                                                        unidad=body_tabla[i]["unidad"]
                                                        suma_cantidad_unidad=parseFloat(suma_cantidad_unidad) + parseFloat(body_tabla[i]["cantidad"]);
                                                }
                                        }
                                        //imprimir total del ultimo provedor o producto
                                        html_reporte +='<tr id="tr_totales">';
                                        html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="right" id="sin_borde">Total:</td>';
                                        html_reporte +='<td align="left" id="sin_borde">'+unidad+'</td>';
                                        html_reporte +='<td align="right" id="sin_borde">'+$(this).agregar_comas(parseFloat(suma_cantidad_unidad).toFixed(2))+'</td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="right" id="sin_borde"></td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="right" id="sin_borde">Total</td>';
                                        html_reporte +='<td align="left" id="simbolo_moneda">$</td>';
                                        html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_parcial).toFixed(2))+'</td>';
                                        html_reporte +='</tr>';
                               }

                                //fila vacia
                                html_reporte +=html_fila_vacia;

                                html_footer +='<tr id="tr_totales">';
                                html_footer +='<td align="left" id="sin_borde_derecho"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="center" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="right" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="right" id="sin_borde">Total General:</td>';
                                html_footer +='<td align="left" id="simbolo_moneda">$</td>';
                                html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_general).toFixed(2))+'</td>';
                                html_footer +='</tr>';
							
                                html_reporte +='<tfoot>';
                                        html_reporte += html_footer;
                                html_reporte +='</tfoot>';

                                html_reporte += '</table>';
						
                                $div_reporte.append(html_reporte); 

                                var height2 = $('#cuerpo').css('height');
                                var alto = parseInt(height2)-300;
                                var pix_alto=alto+'px';
                                $('#reporte').tableScroll({height:parseInt(pix_alto)});
                        
                        });//fin del jsonnnnn  
                }
                
                
                //MUESTRA LOS PRODUCTOS SUMARIZADO
                       if ($select_opciones.val() == 3 || $select_opciones.val() == 4 ){
                        $div_reporte.children().remove();
                        var tipo_reporte= $select_opciones.val();
                        var proveedor = $proveedor.val();
                        var producto = $producto.val();
                        var fecha_inicial = $fecha_inicial.val();
                        var fecha_final = $fecha_final.val();
                        var usuario = config.getUi();
			
			if(fecha_inicial != "" && fecha_final != ""){ 
				var arreglo_parametros = {	tipo_reporte: $select_opciones.val(),
                                                                proveedor: $proveedor.val(),
                                                                producto: $producto.val(),
                                                                fecha_inicial: $fecha_inicial.val(),
                                                                fecha_final: $fecha_final.val(),
                                                                iu:config.getUi()
                                                         };
		
                                var restful_json_service = config.getUrlForGetAndPost() + '/getReporteComprasNetasPorProducto.json'
				
                                var unidad="";
                                var cantidad=0.0;
                                
                                var compra_neta=0.0;
                                var contador_costo_unitario=0;
                                var costo_unitario=0.0;
                                var costo_promedio=0.0;
                                var indice_extraido = "";
				
				$.post(restful_json_service,arreglo_parametros,function(entry){
					var body_tabla = entry['Compras']; 
                                        
					var header_tabla = {
                                                            Producto    :'Producto',
                                                            proveedor    :'Proveedor',
                                                            Unidad : 'Unidad',
                                                            Cantidad : 'Cantidad',
                                                            Monedapu    :"",
                                                            Precio_unitario : 'P.Promedio',
                                                            Monedavn    :"",
                                                            Venta_Neta : 'V.Total'
					};

					var totalpesos = 0.0;
					var totalxproducto= 0.0;
					var tmp = 0;
                                         
                                        var suma_general=0.0;
                                        var producto_proveedor = "";
                                        var html_footer ="";
					var html_comprasnetas = '<table id="ventas" width="100%" >';

                                                html_comprasnetas +='<thead> <tr>';
                                                
                                                
						for(var key in header_tabla){
							var attrValue = header_tabla[key];
                                                        //html_ventasnetas +='<td  align="left">'+attrValue+'</td>'; 
                                                        if($select_opciones.val() == 3){
                                                            if(attrValue == "Producto"){
                                                                    html_comprasnetas +='<td  width="50px" align="left" >'+attrValue+'</td>'; 
                                                            }
                                                        }
                                                        if($select_opciones.val() == 4){
                                                            if(attrValue == "Proveedor"){
                                                                    html_comprasnetas +='<td  width="50px" align="left" >'+attrValue+'</td>'; 
                                                            }
                                                        }
                                                        
							if(attrValue == "Unidad"){
								html_comprasnetas +='<td  align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == "Cantidad"){
								html_comprasnetas +='<td  align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == ""){
								html_comprasnetas +='<td width="8px" align="right" >'+attrValue+'</td>'; 
							}
							if(attrValue == "P.Promedio"){
								html_comprasnetas +='<td   align="right" >'+attrValue+'</td>'; 
							}
							
							if(attrValue == "V.Total"){
								html_comprasnetas +='<td  align="right" >'+attrValue+'</td>'; 
							}
						}
                                                html_comprasnetas +='</tr> </thead>';
                                                                
                                                
                                                if(parseInt(body_tabla.length) > 0){
                                                    if($select_opciones.val() == 3){
                                                            producto_proveedor = body_tabla[0]["producto"];
                                                            unidad = body_tabla[0]["unidad"];
                                                            indice_extraido="producto";
                                                    }
                                                    
                                                    if($select_opciones.val() == 4){
                                                            producto_proveedor = body_tabla[0]["proveedor"];        
                                                            indice_extraido="proveedor";
                                                            unidad = body_tabla[0]["unidad"];
                                                    }
                                                }
                                                               
                                                               
                                                 
                                                for(var i=0; i<body_tabla.length; i++){
                                                    if(producto_proveedor ==  body_tabla[i][indice_extraido] ){
                                                        if(unidad == body_tabla[i]["unidad"]){
                                                            
                                                            /*html_comprasnetas +='<tr>';
                                                                             html_comprasnetas +='<td width="300px" align="left" >'+body_tabla[i][indice_extraido]+'</td>'; 
                                                                             html_comprasnetas +='<td  align="right" >'+body_tabla[i]["unidad"]+'</td>';
                                                                             html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                                             html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                             html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["costo_unitario"]).toFixed(2))+'</td>';
                                                                             html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                             html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["compra_neta_mn"]).toFixed(2))+'</td>';
                                                             html_comprasnetas +='</tr>';*/
//                                                            producto_proveedor      =body_tabla[i][indice_extraido]; 
//                                                            unidad                  =body_tabla[i]["unidad"];
                                                            cantidad                =cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                            costo_unitario          =costo_unitario +parseFloat(body_tabla[i]["costo_unitario"]);
                                                            compra_neta             = compra_neta+parseFloat(body_tabla[i]["compra_neta_mn"]);
                                                            contador_costo_unitario =contador_costo_unitario+1;
                                                            costo_promedio=costo_unitario/contador_costo_unitario;
                                                            suma_general =suma_general +parseFloat(body_tabla[i]["compra_neta_mn"]);
                                                            
                                                           
                                                        }else{
                                                             
                                                            html_comprasnetas +='<tr>';
                                                                        html_comprasnetas +='<td width="300px" align="left" >'+producto_proveedor+'</td>'; 
                                                                        html_comprasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                        html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(costo_promedio).toFixed(2))+'</td>';
                                                                        html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(compra_neta).toFixed(2))+'</td>';
                                                             html_comprasnetas +='</tr>';
                                                        
                                                        
                                                            //reinicio varibles
                                                            cantidad                =0.0;
                                                            costo_unitario          =0.0;
                                                            compra_neta             = 0.0;
                                                            contador_costo_unitario =0.0;
                                                            costo_promedio=0.0;
                                                            
                                                            
                                                            /*
                                                            html_comprasnetas +='<tr>';
                                                                             html_comprasnetas +='<td width="300px" align="left" >'+body_tabla[i][indice_extraido]+'</td>'; 
                                                                             html_comprasnetas +='<td  align="right" >'+body_tabla[i]["unidad"]+'</td>';
                                                                             html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                                             html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                             html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["costo_unitario"]).toFixed(2))+'</td>';
                                                                             html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                             html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["compra_neta_mn"]).toFixed(2))+'</td>';
                                                             html_comprasnetas +='</tr>';
                                                             */
                                                            
                                                            
                                                            
                                                            
                                                            //producto_proveedor      =body_tabla[i][indice_extraido]; 
                                                            //unidad                  =body_tabla[i]["unidad"];
                                                            cantidad                =cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                            costo_unitario          =costo_unitario +parseFloat(body_tabla[i]["costo_unitario"]);
                                                            compra_neta             = compra_neta+parseFloat(body_tabla[i]["compra_neta_mn"]);
                                                            contador_costo_unitario =contador_costo_unitario+1;
                                                            costo_promedio=costo_unitario/contador_costo_unitario;
                                                            suma_general =suma_general +parseFloat(body_tabla[i]["compra_neta_mn"]);
                                                            
                                                        }
                                                        producto_proveedor      =body_tabla[i][indice_extraido]; 
                                                        unidad                  =body_tabla[i]["unidad"];
                                                        
                                                        
                                                        
                                                       
                                                    }else{
                                                        
                                                        //entra aqu cuando el proveedor es diferente   pintala sumatoria delproveedorr anterior
                                                       html_comprasnetas +='<tr>';
                                                                        html_comprasnetas +='<td width="300px" align="left" >'+producto_proveedor+'</td>'; 
                                                                        html_comprasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                        html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(costo_promedio).toFixed(2))+'</td>';
                                                                        html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(compra_neta).toFixed(2))+'</td>';
                                                        html_comprasnetas +='</tr>';
                                                        //fin del primer proveedor
                                                        //reinicio variables
                                                        cantidad                =0.0;
                                                        costo_unitario          =0.0;
                                                        compra_neta             = 0.0;
                                                        contador_costo_unitario =0.0;
                                                        costo_promedio=0.0;
                                                        //pint al nuevo proveedor
                                                       /* html_comprasnetas +='<tr>';
                                                                             html_comprasnetas +='<td width="300px" align="left" >'+body_tabla[i][indice_extraido]+'</td>'; 
                                                                             html_comprasnetas +='<td  align="right" >'+body_tabla[i]["unidad"]+'</td>';
                                                                             html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                                             html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                             html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["costo_unitario"]).toFixed(2))+'</td>';
                                                                             html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                             html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["compra_neta_mn"]).toFixed(2))+'</td>';
                                                           html_comprasnetas +='</tr>';
                                                       */
                                                             
                                                    
                                                    
                                                        
                                                        
                                                        

                                                        //producto_proveedor      =body_tabla[i][indice_extraido]; 
                                                        //unidad                  =body_tabla[i]["unidad"];
                                                        cantidad                =cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                        costo_unitario          =costo_unitario +parseFloat(body_tabla[i]["costo_unitario"]);
                                                        compra_neta             = compra_neta+parseFloat(body_tabla[i]["compra_neta_mn"]);
                                                        contador_costo_unitario =contador_costo_unitario+1;
                                                        costo_promedio=costo_unitario/contador_costo_unitario;
                                                        suma_general =suma_general +parseFloat(body_tabla[i]["compra_neta_mn"]);
                                                         
                                                         

                                                        
                                                    
                                                    }
                                                    producto_proveedor      =body_tabla[i][indice_extraido]; 
                                                    unidad                  =body_tabla[i]["unidad"];
                                                    
                                                }
                                                        html_comprasnetas +='<tr>';
                                                                 html_comprasnetas +='<td width="300px" align="left" >'+producto_proveedor+'</td>'; 
                                                                 html_comprasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                 html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                 html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                 html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(costo_promedio).toFixed(2))+'</td>';
                                                                 html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>'; 
                                                                 html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(compra_neta).toFixed(2))+'</td>';
                                                        html_comprasnetas +='</tr>'; 
                                                        
                                                        html_footer +='<tr id="tr_totales">';
                                                        html_footer +='<td width="500px" id="sin_borde_derecho">&nbsp;</td>';
                                                        
                                                        html_footer +='<td lign="right" id="sin_borde">&nbsp;</td>';
                                                        html_footer +='<td lign="right" id="sin_borde">&nbsp;</td>';
                                                        html_footer +='<td widht="5px"  align="right" id="sin_borde"></td>';
                                                        html_footer +='<td align="left" id="sin_borde">&nbsp;</td>';
                                                        html_footer +='<td align="right" id="sin_borde">Total&nbsp;General:</td>';
                                                        html_footer +='<td widht="5px" align="right" id="simbolo_moneda">$</td>';
                                                        html_footer +='<td align="right" id="monto" id="sin_borde">'+$(this).agregar_comas(parseFloat(suma_general).toFixed(2))+'</td>';
                                                        html_footer +='</tr>';

                                                        html_comprasnetas +='<tfoot>';
                                                                html_comprasnetas += html_footer;
                                                        html_comprasnetas +='</tfoot>';
                                                       
                                                        html_comprasnetas += '</table>';

                                                $div_reporte.append(html_comprasnetas); 
                                                var height2 = $('#cuerpo').css('height');
                                                var alto = parseInt(height2)-250;
                                                var pix_alto=alto+'px';
                                                $('#ventas').tableScroll({height:parseInt(pix_alto)});
				});
			}else{
				jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
			}
                        
                }//FIN DE LA VISTA DE PRODUCTO SUMARIZADOs
                
	});//fin del .click de buscar
});
