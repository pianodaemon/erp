$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Reporte de D&iacute;as de entrega de O.C.' ,                 
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
			return this.contextpath + "/controllers/comdiasentrega";
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
	
	var $select_tipo_reporte = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=tipo_reporte]');
	
    var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
    var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
    
	var $id_proveedor_edo_cta = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=id_proveedor_edo_cta]');
	var $razon_proveedor = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=razon_proveedor]');
	var $buscar_proveedor= $('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=buscar_proveedor]');
	var $genera_PDF = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
	var $genera_EXCEL = $('#lienzo_recalculable').find('table#busqueda tr td').find('input#excel');
	var $busqueda_reporte= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
	var $div_reporte_estados_de_cuenta= $('#lienzo_recalculable').find('#divreporteedocta');
	
	$razon_proveedor.attr('readonly',true);
	$razon_proveedor.css({'background' : '#DDDDDD'});
	$buscar_proveedor.hide();
	
	$select_tipo_reporte.children().remove();
	html='<option value="0">General</option>';
	html+='<option value="1">Por proveedor</option>';
	$select_tipo_reporte.append(html);
	
	
	$select_tipo_reporte.change(function(){
		if(parseInt($(this).val())==0){
			$razon_proveedor.css({'background' : '#DDDDDD'});
			$razon_proveedor.attr('readonly',true);
			$buscar_proveedor.hide();
			$razon_proveedor.val('');
			$id_proveedor_edo_cta.val(0);
		}else{
			$razon_proveedor.css({'background' : '#ffffff'});
			$buscar_proveedor.show();
			$razon_proveedor.attr('readonly',false);
		}
	});
	

/*
	 var arreglo_parametros = { 
		iu:config.getUi()
	 };
		
	var restful_json_service = config.getUrlForGetAndPost() + '/getDatos.json';
	$.post(restful_json_service,arreglo_parametros,function(entry){
		//carga select de años
		$select_ano.children().remove();
		var html_anio = '';
		$.each(entry['Anios'],function(entryIndex,anio){
			if(parseInt(anio['valor']) == parseInt(entry['Dato'][0]['anioActual']) ){
				html_anio += '<option value="' + anio['valor'] + '" selected="yes">' + anio['valor'] + '</option>';
			}else{
				html_anio += '<option value="' + anio['valor'] + '"  >' + anio['valor'] + '</option>';
			}
		});
		$select_ano.append(html_anio);
		
		//cargar select del Mes inicial
		$select_mes.children().remove();
		var select_html = '';
		for(var i in array_meses){
			if(parseInt(i) == parseInt(entry['Dato'][0]['mesActual']) ){
				select_html += '<option value="' + i + '" selected="yes">' + array_meses[i] + '</option>';	
			}else{
				select_html += '<option value="' + i + '"  >' + array_meses[i] + '</option>';	
			}
		}
		$select_mes.append(select_html);
	});

*/



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
        a.css({
            'z-index':100
        });
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
        a.css({
            'z-index':100
        });
    });
    $fecha_final.val(mostrarFecha());

    //aplicar mascara a campos para entrada manual de fecha
    //9 indica que va a permitir captura de numeros solamante
    $fecha_inicial.mask('9999-99-99');
    $fecha_final.mask('9999-99-99');



	$busca_proveedores = function(){
		$(this).modalPanel_Buscaprov();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
		var $campo_email = $('#forma-buscaproveedor-window').find('input[name=campo_email]');
		var $campo_nombre = $('#forma-buscaproveedor-window').find('input[name=campo_nombre]');
		
		var $buscar_plugin_proveedor = $('#forma-buscaproveedor-window').find('#busca_proveedor_modalbox');
		var $cancelar_plugin_busca_proveedor = $('#forma-buscaproveedor-window').find('#cencela');
		
		$('#forma-entradamercancias-window').find('input[name=tipo_proveedor]').val('');
			
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
			var restful_json_service = config.getUrlForGetAndPost() + '/getBuscaProveedores.json'
			$arreglo = {    rfc:$campo_rfc.val(),
							email:$campo_email.val(),
							nombre:$campo_nombre.val(),
							iu:config.getUi()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(restful_json_service,$arreglo,function(entry){
				$.each(entry['Proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
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
					$razon_proveedor.val($(this).find('#razon_social').html());
					$id_proveedor_edo_cta.val($(this).find('#id_prov').val());
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
				});
			});
		});
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_rfc, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_email, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_nombre, $buscar_plugin_proveedor);
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
		});
	}//termina buscador de proveedores
	
	
	
    $buscar_proveedor.click(function(event){
        //alert("aqui ando");
        event.preventDefault();
        $busca_proveedores();//llamada a la funcion que busca proveedores
    });
    
    
	
//	//genera pdf del reporte de estados de cuenta de proveedores
	$genera_EXCEL.click(function(event){
		event.preventDefault();
		var proveedor = '0';
		var f_inicial = '0';
		var f_final = '0';
		
		if($razon_proveedor.val().trim()!=''){
			proveedor=$razon_proveedor.val();
		}
		if($fecha_inicial.val().trim()!=''){
			f_inicial=$fecha_inicial.val();
		}
		if($fecha_final.val().trim()!=''){
			f_final=$fecha_final.val();
		}
				
		var cadena = $select_tipo_reporte.val()+"___"+f_inicial+"___"+f_final+"___"+proveedor+"___xls";
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		var input_json = config.getUrlForGetAndPost() + '/getReporte/'+cadena+'/'+iu+'/out.json'
		window.location.href=input_json;
		
	});//termina llamada json
	
	
	
//	//genera pdf del reporte de estados de cuenta de proveedores
	$genera_PDF.click(function(event){
		event.preventDefault();
		var proveedor = '0';
		var f_inicial = '0';
		var f_final = '0';
		
		if($razon_proveedor.val().trim()!=''){
			proveedor=$razon_proveedor.val();
		}
		if($fecha_inicial.val().trim()!=''){
			f_inicial=$fecha_inicial.val();
		}
		if($fecha_final.val().trim()!=''){
			f_final=$fecha_final.val();
		}
		
		var cadena = $select_tipo_reporte.val()+"___"+f_inicial+"___"+f_final+"___"+proveedor+"___pdf";
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		var input_json = config.getUrlForGetAndPost() + '/getReporte/'+cadena+'/'+iu+'/out.json'
		window.location.href=input_json;
		
	});//termina llamada json
	
	
	$busqueda_reporte.click(function(event){
		event.preventDefault();
		$div_reporte_estados_de_cuenta.children().remove();
			
			var arreglo_parametros = {	
				tipo_reporte: $select_tipo_reporte.val(),
				proveedor: $razon_proveedor.val(),
				f_inicial: $fecha_inicial.val(),
				f_final: $fecha_final.val(),
				iu:config.getUi()
			};
			
			var restful_json_service = config.getUrlForGetAndPost() + '/getDatosReporte.json'
			var proveedoor="";
			$.post(restful_json_service,arreglo_parametros,function(entry){
				var body_tabla = entry['Data'];
				var header_tabla = {
					oc				:'Orden Compra',
					codigo			:'C&oacute;digo',
					descripcion		:'Descripci&oacute;n',
					cantidad		:'Cantidad',
					proveedor		:'Proveedor',
					fecha_oc		:'Fecha O.C.',
					fecha_recepcion  :'Fecha Recepci&oacute;n',
					dias_promedio    :'D&iacute;as Promedio'
				};
				
				var html_reporte = '<table id="edocta">';
				var html_fila_vacia='';
				var html_footer = '';
				
				html_reporte +='<thead> <tr>';
				for(var key in header_tabla){
					var attrValue = header_tabla[key];
					if(attrValue == "Orden Compra"){
						html_reporte +='<td width="90px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "C&oacute;digo"){
						html_reporte +='<td width="120px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Descripci&oacute;n"){
						html_reporte +='<td width="200px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == 'Cantidad'){
						html_reporte +='<td width="90px" align="left" >'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Proveedor"){
						html_reporte +='<td width="280px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Fecha O.C."){
						html_reporte +='<td width="80px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "Fecha Recepci&oacute;n"){
						html_reporte +='<td width="100px" align="left">'+attrValue+'</td>'; 
					}
					
					if(attrValue == "D&iacute;as Promedio"){
						html_reporte +='<td width="90px" align="left">'+attrValue+'</td>'; 
					}
				}
				html_reporte +='</tr> </thead>';
				
				html_fila_vacia +='<tr class="first">';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="90px" height="10"></td>';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="120px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="200px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="90px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="280px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="80px"></td>';
				html_fila_vacia +='<td align="right" id="sin_borde" width="90px"></td>';
				html_fila_vacia +='<td align="left"  id="sin_borde" width="90px"></td>';
				html_fila_vacia +='</tr>';
				
				var orden_compra="";
				var simbolo_moneda="";
				var proveedor_actual="";
				
                //inicializar variables
                var suma_monto_total_proveedor=0.0;
                var suma_importe_pagado_proveedor=0.0;
                var suma_saldo_pendiente_proveedor=0.0;
                
                var simbolo_moneda_pesos="";
                var suma_monto_total_moneda_pesos=0.0;
                var suma_importe_pagado_moneda_pesos=0.0;
                var suma_saldo_pendiente_moneda_pesos=0.0;
				
				var simbolo_moneda_dolar="";
                var suma_monto_total_moneda_dolar=0.0;
                var suma_importe_pagado_moneda_dolar=0.0;
                var suma_saldo_pendiente_moneda_dolar=0.0;
                
                var simbolo_moneda_euro="";
                var suma_monto_total_moneda_euro=0.0;
                var suma_importe_pagado_moneda_euro=0.0;
                var suma_saldo_pendiente_moneda_euro=0.0;
				
				if(parseInt(body_tabla.length)>0){
					
					for(var i=0; i<body_tabla.length; i++){
						html_reporte +='<tr>';
						html_reporte +='<td align="left">'+body_tabla[i]["oc"]+'</td>';
						html_reporte +='<td align="left">'+body_tabla[i]["codigo"]+'</td>';
						html_reporte +='<td align="left">'+body_tabla[i]["descripcion"]+'</td>';
						html_reporte +='<td align="right">'+$(this).agregar_comas(body_tabla[i]["cantidad"])+'</td>';
						html_reporte +='<td align="left">'+body_tabla[i]["proveedor"]+'</td>';
						html_reporte +='<td align="left">'+body_tabla[i]["fecha_oc"]+'</td>';
						html_reporte +='<td align="left">'+body_tabla[i]["fecha_recepcion"]+'</td>';
						html_reporte +='<td align="left">'+body_tabla[i]["dias_promedio"]+'</td>';
						html_reporte +='</tr>';
					}
					
				}
				
				/*
				html_reporte +='<tfoot>';
					html_reporte += html_footer;
				html_reporte +='</tfoot>';
				*/
				
				
				html_reporte += '</table>';
				
				
				$div_reporte_estados_de_cuenta.append(html_reporte); 
				var height2 = $('#cuerpo').css('height');
				var alto = parseInt(height2)-240;
				var pix_alto=alto+'px';
				$('#edocta').tableScroll({height:parseInt(pix_alto)});
			});
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_reporte, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_inicial, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($fecha_final, $busqueda_reporte);
	$(this).aplicarEventoKeypressEjecutaTrigger($razon_proveedor, $busqueda_reporte);
	
});   
        
        
        
        
    
