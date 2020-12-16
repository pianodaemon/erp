$(function() {
	var config =  {
				tituloApp: 'Programaci&oacute;n de Rutas' ,                 
				empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
				sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
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

				getUi: function(){
					return this.ui;
				},
				
				getEmpresa: function(){
					return this.empresa;
				},
				getSucursal: function(){
					return this.sucursal;
				},
				
				getTituloApp: function(){
					return this.tituloApp;
				},


				getController: function(){
					return this.contextpath + "/controllers/programacionrutas";
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
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
	var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	//var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	//var controller = $contextpath.val()+"/controllers/programacionrutas";
    

	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_ruta = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	//$('#barra_titulo').find('#td_titulo').append('Programaci&oacute;n de Rutas');
	$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
    
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'80px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	//$('#barra_buscador').hide();
	

	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_factura = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_factura]');
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
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "factura" + signo_separador + $busqueda_factura.val() + "|";
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
    
    
    
	TriggerClickVisializaBuscador = 0;
	TriggerClickVisualizaGeneradorInforme=0;
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		$('#barra_genera_informe').find('.tabla_genera_reportes').css({'display':'none'});
		
		if(parseInt(TriggerClickVisualizaGeneradorInforme)==1){
			$reporte_edo_cta_cliente.trigger('click');
		}
		
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
	});
	
	
	
	
    
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
		//boton registrar pago
		$('#forma-programacionrutas-window').find('#submit_pago').mouseover(function(){
			$('#forma-programacionrutas-window').find('#submit_pago').css({backgroundImage:"url(../../img/modalbox/pago_over.png)"});
		});
		$('#forma-programacionrutas-window').find('#submit_pago').mouseout(function(){
			$('#forma-programacionrutas-window').find('#submit_pago').css({backgroundImage:"url(../../img/modalbox/pago.png)"});
		});
		//boton registrar anticipo
		$('#forma-programacionrutas-window').find('#registra_anticipo').mouseover(function(){
			$('#forma-programacionrutas-window').find('#registra_anticipo').css({backgroundImage:"url(../../img/modalbox/anticipo_over.png)"});
		});
		$('#forma-programacionrutas-window').find('#registra_anticipo').mouseout(function(){
			$('#forma-programacionrutas-window').find('#registra_anticipo').css({backgroundImage:"url(../../img/modalbox/anticipo.png)"});
		});
		//boton registrar cancelacion
		$('#forma-programacionrutas-window').find('#submit_cancel').mouseover(function(){
			$('#forma-programacionrutas-window').find('#submit_cancel').css({backgroundImage:"url(../../img/modalbox/cancelacion_over.png)"});
		});
		$('#forma-programacionrutas-window').find('#submit_cancel').mouseout(function(){
			$('#forma-programacionrutas-window').find('#submit_cancel').css({backgroundImage:"url(../../img/modalbox/cancelacion.png)"});
		});
		$('#forma-programacionrutas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-programacionrutas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-programacionrutas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-programacionrutas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		$('#forma-programacionrutas-window').find('#close').mouseover(function(){
			$('#forma-programacionrutas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-programacionrutas-window').find('#close').mouseout(function(){
			$('#forma-programacionrutas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		$('#forma-programacionrutas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-programacionrutas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-programacionrutas-window').find(".contenidoPes:first").show(); //Show first tab content

		//On Click Event
		$('#forma-programacionrutas-window').find("ul.pestanas li").click(function() {
			$('#forma-programacionrutas-window').find(".contenidoPes").hide();
			$('#forma-programacionrutas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-programacionrutas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	
	
	//buscador de clientes
	$busca_clientes = function(){
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
		html+='<option value="1">No. de control</option>';
		html+='<option value="2">RFC</option>';
		html+='<option value="3">Razon social</option>';
		html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			
			
			var arreglo_parametros = {
				cadena: $cadena_buscar.val(),
				filtro: $select_filtro_por.val(),
				iu: config.getUi()
			}
			
			var restful_json_service = config.getUrlForGetAndPost() + '/get_buscador_clientes.json';
			
			var trr = '';
			$tabla_resultados.children().remove();
			//$.post(input_json,$arreglo,function(entry){
			$.post(restful_json_service,arreglo_parametros,function(entry){
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
					
					$('#forma-programacionrutas-window').find('input[name=id_cliente]').val($(this).find('#idclient').val());
					$('#forma-programacionrutas-window').find('input[name=nombre_cliente]').val($(this).find('span.razon').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
				});
				
			});
		});//termina llamada json

		$cancelar_plugin_busca_cliente.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
		});
	}//termina buscador de clientes
	
	
	
	
	
	
	
	
	var verificar_seleccionados= function($tabla_facturas){
		var seleccionados=false;
		$tabla_facturas.find('input[name=micheck]').each(function(){
			if(this.checked){
				seleccionados=true;
			}
		});
		return seleccionados;
	}
	
	
	
	
	var contar_seleccionados= function($tabla_facturas){
		var seleccionados=0;
		
		$tabla_facturas.find('input[name=micheck]').each(function(){
			if(this.checked){
				seleccionados = parseInt(seleccionados) + 1;
			}
		});
		
		return seleccionados;
	}
	
	
	
	var seleccionar_facturas_check = function($tabla_facturas_body){
		$tabla_facturas_body.find('input[name=micheck]').click(function(event){
			if(event.currentTarget == this){
				
				if(this.checked){
					$(this).parent().find('input[name=seleccionado]').val("1");
				}else{
					$(this).parent().find('input[name=seleccionado]').val("0");
				}
				
			}
		});
	}
	
	
	
	
	
	var agrega_trs_grid= function(check, id_detalle, id_h_fac, factura, fecha_fac, cliente, saldo_fac, rev_cob, actualizado ){
		var tr = "";
		var valor_seleccionado="0";
		var desactivado="";
		if ( check == 'checked' ){
			valor_seleccionado="1";
		}
		
		if ( actualizado == 'true' ){
			desactivado="disabled='disabled'";
		}
		
		tr += "<tr>";
		tr += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='70'>";
			//tr += "<input type='checkbox' name='micheck' value='true' "+check+" "+desactivado+">";
			tr += "<input type='checkbox' name='micheck' value='true' "+check+" "+desactivado+">";
			tr += "<input type='hidden' name='seleccionado' value='"+valor_seleccionado+"'>";
			tr += "<input type='hidden' name='id_detalle' value='"+id_detalle+"'>";
			tr += "<input type='hidden' name='id_h_fac' value='"+id_h_fac+"'>";
			tr += "<input type='hidden' name='rev_cob' value='"+rev_cob+"'>";
		tr += "</td>";
		tr += "<td class='grid' style='font-size:11px; border:1px solid #C1DAD7 ;' width='120'>" + factura + "</td>";
		tr += "<td class='grid' style='font-size:11px; border:1px solid #C1DAD7 ;' width='120'>" + fecha_fac + "</td>";
		tr += "<td class='grid1' style='font-size:11px; border:1px solid #C1DAD7 ;' width='420'>" + cliente + "</td>";
		tr += "<td class='grid2' style='font-size:11px; border:1px solid #C1DAD7 ;' width='130'>" + $(this).agregar_comas(saldo_fac) + "</td>";
		tr += "</tr>";
		
		return tr;
	}
	
	
	
	$new_ruta.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_programacionrutas();
		
		var form_to_show = 'formaProgramacionRutas00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$forma_selected.prependTo('#forma-programacionrutas-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$('#forma-programacionrutas-window').css({"margin-left": -350,"margin-top": -220});
        
        
		$tabs_li_funxionalidad();
                
		/// variables de la forma captura de rutas y sus especificaciones 
		var $total_tr = $('#forma-programacionrutas-window').find('input[name=total_tr]');
		var $identificador = $('#forma-programacionrutas-window').find('input[name=identificador]');
		var $folio = $('#forma-programacionrutas-window').find('input[name=folio]');
		
		var $id_cliente= $('#forma-programacionrutas-window').find('input[name=id_cliente]');
		var $nombre_cliente= $('#forma-programacionrutas-window').find('input[name=nombre_cliente]');
		var $busca_cliente= $('#forma-programacionrutas-window').find('a[href*=busca_cliente]');
		var $fecha=$('#forma-programacionrutas-window').find('input[name=fecha]');
		
		var $ver_facturas=$('#forma-programacionrutas-window').find('input[name=ver_facturas]');
		var $tabla_revision=$('#forma-programacionrutas-window').find('.facturas_revision');
		var $tabla_cobro=$('#forma-programacionrutas-window').find('.facturas_cobrar');
		
		
		
		var $cerrar_plugin = $('#forma-programacionrutas-window').find('#close');
		var $cancelar_plugin = $('#forma-programacionrutas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-programacionrutas-window').find('#submit');
		
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los datos se guardaron con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-programacionrutas-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-programacionrutas-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-programacionrutas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		
                
                
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		
		/*
		var arreglo_parametros = {
			id:$identificador.val(),
		}
		
		var restful_json_service = config.getUrlForGetAndPost() + '/getFacturas.json';
		
		
		//alert(restful_json_service);
		$.post(restful_json_service,arreglo_parametros,function(entry){
			$tabla_facturas_body.children().remove();
			$.each(data['Facturas'],function(entryIndex, entry2){
				var contenido_c="";
				contenido_c += "<tr>";
				contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='10'><input type='checkbox' name='micheck' value='check'></td>";
				contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7 ;' width='60'>" + entry2['numero_factura'] + "</td>";
				
				contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(entry2['monto_factura'])+"</td>";
				contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='85' align='right'>"+$(this).agregar_comas(entry2['monto_pagado'])+"</td>";
				var saldo = parseFloat(entry2['monto_factura'])- parseFloat(entry2['monto_pagado']);
				contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(saldo.toFixed(2))+"</td>";
				
				contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='80'>";
				contenido_c += "<input type='text' name='saldar' value='0.00' style='width: 80px; background-color:#F6F8FB; text-align:right;' disabled='true' align='right'>";
				contenido_c += "<input type='hidden' name='saldar_calculado' value='0.00' style='width: 80px; background-color:#F6F8FB; text-align:right;' disabled='true' align='right'>";
				contenido_c += "</td>";
				contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='70'>" +entry2['denominacion_factura']+ "</td>";
				//contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='125'><input type='text' name='tipocamb' value='"+$tipo_cambio.val()+"' style='width: 80px; background-color:#F6F8FB;' align='right' disabled=true><span class='button_for_ie'><input align='center' class='borde_oculto' type='botton' value='<<>>' style='font-size: 11px; background-color: rgb(255, 255, 255);  padding: 1px; width: 35px;'></span></td>";
				contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='125'><input type='text' name='tipocamb' value='"+$tipo_cambio.val()+"' style='width: 80px; background-color:#F6F8FB;' align='right' disabled='true'></td>";
				contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='92'>" +entry2['fecha_ultimo_pago']+ "</td>";
				contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='95'>" +entry2['fecha_facturacion']+ "</td>";
				contenido_c += "</tr>";
				$tabla_facturas_body.append(contenido_c);
			});
			iterar_facturas_check($tabla_facturas_body);
			iterar_facturas_input($tabla_facturas_body);
		});
		*/
		
		//muestra la fecha actual en el campo
        $fecha.val(mostrarFecha());
        
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
					var valida_fecha=mayor($fecha.val(),mostrarFecha());
					/*
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha.val(mostrarFecha());
					}else{
					*/
						$fecha.DatePickerHide();
					//}
				}
			}
		});
		
		
		
		
		$ver_facturas.click(function(event){
			event.preventDefault();
			var arreglo_parametros = {
				id_cliente: $id_cliente.val(),
				fecha: $fecha.val(),
				iu: config.getUi() 
			};
			
			var restful_json_service = config.getUrlForGetAndPost() + '/getFacturas.json'
			
			$.post(restful_json_service,arreglo_parametros,function(entry){
				$tabla_revision.children().remove();
				$tabla_cobro.children().remove();
				var check;
				var id_detalle;
				var id_h_fac;
				var factura;
				var fecha_fac;
				var cliente;
				var saldo_fac;
				var rev_cob;
				var nuevo_tr;
				var actualizado;
				
				$.each(entry['facRevision'],function(entryIndex,facrev){
					//check = "checked";
					check = "";
					id_detalle = "0";
					id_h_fac = facrev['id_erp_h_fac'];
					factura = facrev['factura'];
					fecha_fac = facrev['fecha_factura'];
					cliente = facrev['cliente'];
					saldo_fac = facrev['saldo_factura'];
					rev_cob = facrev['revision_cobro'];
					actualizado = "false";
					//llamada a funcion que crea cadena para el tr
					nuevo_tr = agrega_trs_grid(check, id_detalle, id_h_fac, factura, fecha_fac, cliente, saldo_fac, rev_cob, actualizado );
					//agrega el tr a la tabla facturas a revision
					$tabla_revision.append(nuevo_tr);
					seleccionar_facturas_check ($tabla_revision);
				});
				
				
				$.each(entry['facCobro'],function(entryIndex,faccob){
					//check = "checked";
					check = "";
					id_detalle = "0";
					id_h_fac = faccob['id_erp_h_fac'];
					factura = faccob['factura'];
					fecha_fac = faccob['fecha_factura'];
					cliente = faccob['cliente'];
					saldo_fac = faccob['saldo_factura'];
					rev_cob = faccob['revision_cobro'];
					actualizado = "false";
					//llamada a funcion que crea cadena para el tr
					nuevo_tr = agrega_trs_grid(check, id_detalle, id_h_fac, factura, fecha_fac, cliente, saldo_fac, rev_cob, actualizado );
					//agrega el tr a la tabla facturas cobro
					$tabla_cobro.append(nuevo_tr);
					seleccionar_facturas_check ($tabla_cobro);
					
				});
				
			});//termina llamada json
			
		});
			
		
		
		
		
		//buscador de clientes
		$busca_cliente.click(function(event){
			event.preventDefault();
			$busca_clientes();
		});
		
		
		
		$submit_actualizar.bind('click',function(){
			var selec=0;
			//checa facturas a revision seleccionadas
			selec = contar_seleccionados($tabla_revision);
			//checa facturas a Cobro seleccionadas
			selec = parseInt(selec) + parseInt(contar_seleccionados($tabla_cobro));
			
			$total_tr.val(selec);
			
			if(parseInt($total_tr.val()) > 0){
				return true;
			}else{
				jAlert("No hay facturas seleccionadas para actualizar", 'Atencion!');
				return false;
			}
		});
		
		
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-programacionrutas-overlay').fadeOut(remove);
		});
		
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-programacionrutas-overlay').fadeOut(remove);
		});
	});
	
	
	
	
	
	var carga_formaPagos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
			
			jConfirm('Realmente desea eliminar la Ruta seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Agente fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Agente no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			//event.preventDefault();
			//var id_to_show = 0;
			
			$(this).modalPanel_programacionrutas();
			
			var form_to_show = 'formaProgramacionRutas00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$forma_selected.prependTo('#forma-programacionrutas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$('#forma-programacionrutas-window').css({"margin-left": -350,"margin-top": -220});
			
			$tabs_li_funxionalidad();
			
			
			/// variables de la forma captura de rutas y sus especificaciones 
			var $total_tr = $('#forma-programacionrutas-window').find('input[name=total_tr]');
			var $identificador = $('#forma-programacionrutas-window').find('input[name=identificador]');
			var $folio = $('#forma-programacionrutas-window').find('input[name=folio]');
			
			var $id_cliente= $('#forma-programacionrutas-window').find('input[name=id_cliente]');
			var $nombre_cliente= $('#forma-programacionrutas-window').find('input[name=nombre_cliente]');
			var $busca_cliente= $('#forma-programacionrutas-window').find('a[href*=busca_cliente]');
			var $fecha=$('#forma-programacionrutas-window').find('input[name=fecha]');
			
			var $ver_facturas=$('#forma-programacionrutas-window').find('input[name=ver_facturas]');
			var $tabla_revision=$('#forma-programacionrutas-window').find('.facturas_revision');
			var $tabla_cobro=$('#forma-programacionrutas-window').find('.facturas_cobrar');
			
			var $cerrar_plugin = $('#forma-programacionrutas-window').find('#close');
			var $cancelar_plugin = $('#forma-programacionrutas-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-programacionrutas-window').find('#submit');
			
			$busca_cliente.hide();
			$ver_facturas.attr('disabled','-1');
			$nombre_cliente.attr('disabled','-1');
			$tabla_revision.children().remove();
			$tabla_cobro.children().remove();
			
			if(accion_mode == 'edit'){
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-programacionrutas-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-programacionrutas-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-programacionrutas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				var arreglo_parametros = {
					id:id_to_show,
					iu: config.getUi() 
					
				}
				
				var restful_json_service = config.getUrlForGetAndPost() + '/getDatosProgramacion.json';
				
				$.post(restful_json_service,arreglo_parametros,function(entry){
					
					$identificador.attr({ 'value' : entry['Datos']['0']['id'] });
					$folio.attr({ 'value' : entry['Datos']['0']['folio'] });
					$fecha.attr({ 'value' : entry['Datos']['0']['fecha_proceso'] });
					
					var check;
					var id_detalle;
					var id_h_fac;
					var factura;
					var fecha_fac;
					var cliente;
					var saldo_fac;
					var rev_cob;
					var actualizado;
					var nuevo_tr;
					
					$.each(entry['Facturas'],function(entryIndex,fac){
						
						//check = "checked";
						
						check = fac['seleccionado'];
						id_detalle = fac['id_detalle'];
						id_h_fac = fac['id_erp_h_fac'];
						factura = fac['factura'];
						fecha_fac = fac['fecha_factura'];
						cliente = fac['cliente'];
						saldo_fac = fac['saldo_factura'];
						rev_cob = fac['revision_cobro'];
						actualizado = fac['actualizado'];
						//llamada a funcion que crea cadena para el tr
						nuevo_tr = agrega_trs_grid(check, id_detalle, id_h_fac, factura, fecha_fac, cliente, saldo_fac, rev_cob, actualizado );
						
						if ( rev_cob == 'R' ){
							//agrega el tr a la tabla facturas a revision
							$tabla_revision.append(nuevo_tr);
							seleccionar_facturas_check ($tabla_revision);
						}else{
							//agrega el tr a la tabla facturas cobro
							$tabla_cobro.append(nuevo_tr);
							seleccionar_facturas_check ($tabla_cobro);
						}
					});
					
					//si  el registro ya  fue actualizado se deshabilita el boton actualizar
					if ( actualizado == 'true' ) {
						//$submit_actualizar.attr('disabled','-1');
						$submit_actualizar.hide();
					}
					
					
				});//termina llamada json
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-programacionrutas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-programacionrutas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    
    
    
    $get_datos_grid = function(){
        //var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/gatAllRutas.json';
        var input_json = config.getUrlForGetAndPost() + '/gatAllRutas.json';
        
        //var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        var iu = config.getUi();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+config.getController()+'/gatAllRutas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenablePrefacturas(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaPagos00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});
