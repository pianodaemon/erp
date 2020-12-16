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
     var controller = $contextpath.val()+"/controllers/logasignarutas";
	
	
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
	$('#barra_titulo').find('#td_titulo').append('Asignaci&oacute;n de Rutas');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	//$busqueda_tipo
	var $ruta = $('#barra_buscador').find('.tabla_buscador').find('input[name=ruta]');
	var $marca = $('#barra_buscador').find('.tabla_buscador').find('input[name=marca]');
	var $chofer = $('#barra_buscador').find('.tabla_buscador').find('input[name=chofer]');
	//var $busqueda_select_grupo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_grupo]');

	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');

	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "ruta" + signo_separador + $ruta.val() + "|";
		valor_retorno += "marca" + signo_separador + $marca.val() + "|";
          valor_retorno += "chofer" + signo_separador + $chofer.val() + "|";
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
		$ruta.val('');
		$marca.val('');
          $chofer.val('');
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
	});
     
     //fucniones para las Fechas
               
	
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
	
	
	
	

	
	
	$formar_cadena_tr = function(array_facturas,noTr){ 
		var tr_fac="";
		$.each(array_facturas,function(entryIndex, datos){
			tr_fac += '<tr class="gral">';
			tr_fac += '<td style="font-size: 11px;  border:1px solid #C1DAD7;" align="center" width="30" >';
			tr_fac += '<input type="hidden" name="id_detalle" value="0">';
			tr_fac += '<input type="checkbox" name="micheck" value="check">';
			tr_fac += '<input type="hidden"  name="tipo" value="FAC_DOCS">';
			tr_fac += '<input type="hidden"  name="fac_docs_detalle_id" value="'+datos['id_fac_docs']+'">';
			tr_fac += '<input type="hidden"  name="inv_prod_id" value="'+datos['id_invprod']+'">';
			tr_fac += '<input type="hidden"  name="fac_rev_cobro" value="0">';
			tr_fac += '<input type="hidden"  name="seleccionado" value="0">';
			tr_fac += '<input type="hidden"  name="eliminado" value="1">';
			tr_fac += "</td>";
			
			tr_fac += '<td style="font-size: 11px;  border:1px solid #C1DAD7;" align="center" width="70" ><span id="fac">'+datos['factura'] + '</span></td>';
			tr_fac += '<td style="font-size: 11px;  border:1px solid #C1DAD7;" align="center" width="70" >'+datos['fecha_factura'] +'</td>';
			tr_fac += '<td style="font-size: 11px;  border:1px solid #C1DAD7;" align="left  " width="275">'+datos['cliente']+'</td>';
			tr_fac += '<td style="font-size: 11px;  border:1px solid #C1DAD7;" align="center" width="65" >'+datos['codigo']+'</td>';
			tr_fac += '<td style="font-size: 11px;  border:1px solid #C1DAD7;" align="center" width="57" >'+datos['cantidad']+'</td>';
			
			tr_fac += '<td style="font-size: 11px;  border:1px solid #C1DAD7;" align="left" width="120">' +datos['descripcion']+ '</td>';
			tr_fac += '<td style="font-size: 11px;  border:1px solid #C1DAD7;" align="right" width="80">' +$(this).agregar_comas(parseFloat(datos['importe']).toFixed(2))+ '</td>';
			tr_fac += '<td style="font-size: 11px;  border:1px solid #C1DAD7;" align="left " width="66"><INPUT TYPE="text" name="envase" value=" " class="envase"'+noTr+'" style=width:65px;"></td>';
			tr_fac += '</tr>';
		});
		
		return tr_fac;
	}
	
	
	
	
	//carga facturas para envio de Material cuando es nueva  Ruta
	$buscar_facturas_mercancia = function($tabla_facturas_body, $fecha_inicial, $fecha_final, $factura, $tipo_busqueda ){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFacturas.json';
			
			$arreglo = {
				'fecha_inicial': $fecha_inicial.val(),
				'fecha_final': $fecha_final.val(),
				'factura':$factura.val(),
				'tipo_busqueda': $tipo_busqueda.val(),
				'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
			 };
			 
			$.post(input_json,$arreglo,function(entry){
				
				if ( entry['Facturas_a_entregar'].length > 0 ){
					var noTr = $("tr", $tabla_facturas_body).size();
					
					noTr++;
					
					//formar cadena html para tr
					var contenido_facturas = $formar_cadena_tr(entry['Facturas_a_entregar'], noTr);
					
					//crear el tr en la tabla
					$tabla_facturas_body.append(contenido_facturas);
					
					//aplicar evento al seleccionar un check
					seleccionar_facturas_check($tabla_facturas_body);
				}else{
					if( parseInt($tipo_busqueda.val()) == 1 ){
						jAlert("No hay facturas en este rango de fechas.", 'Atencion!');
					}else{
						jAlert("La factura que intenta agregar no existe.", 'Atencion!');
					}
				}
				
			 });//termina getFacturas
			
     }
     
     
     
     //carga facturas para envio a Revision y Cobro cuando es Nueva Ruta
	grid_fac_rev_cobro = function(){
		var $tabla_facturas_revision = $('#forma-logasignarutas-window').find('.facturas_revision');//.find('.contenido_facturas');
		var $totalTR_RC = $('#forma-logasignarutas-window').find('input[name=total_tr_revision_cobro]');
		var $folio_fac_rev = $('#forma-logasignarutas-window').find('input[name=folio_fac_rev_cobro]');
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFacturas_Rev_cobro.json';
		
		$arreglo = {
			'iu': $('#lienzo_recalculable').find('input[name=iu]').val(),
			'folio': $folio_fac_rev.val()
		};
		
		$.post(input_json,$arreglo,function(entry){
               //$tabla_facturas_revision.children().remove();
               $.each(entry['Facturas_fac_rev_cobro'],function(entryIndex, datos){
                    var fac_RC="";
                    fac_RC += "<tr>";
                    fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' align='center' width='30' >";
						fac_RC += "<input type='hidden' 	name='id_detalle' value='0'>";
                         fac_RC += "<input type='checkbox' 	name='micheck' value='check'>";
                         fac_RC += '<input type="hidden"  	name="tipo" value="RC">';
                         fac_RC += '<input type="hidden" 	name="fac_docs_detalle_id" value="0">';
                         fac_RC += '<input type="hidden" 	name="inv_prod_id" value="0">';
                         fac_RC += '<input type="text"  	name="envase" value="0" style="display:none;">';
                         fac_RC += '<input type="hidden" 	name="fac_rev_cobro" value="'+datos['fac_rev_cob_detalle_id']+'">';
                         fac_RC += '<input type="hidden"  name="seleccionado" value="0">';
                    fac_RC += "</td>";
					fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='60' ><span id='fac'>"+datos['folio_programacion'] + "</span></td>";//folio-programacion
					fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='60' >";
						fac_RC += "<a href='elim' id='eliminar'>Eliminar</a>";
						fac_RC += '<input type="hidden"  name="eliminado" value="1">';
					fac_RC += "</td>";//eliminar
                    fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='70' ><span id='fac'>"+datos['factura'] + "</span></td>";//entry2['numero_factura']
                    fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='70' >"+datos['fecha_factura'] +"</td>";//$(this).agregar_comas(entry['monto_factura'])
                    fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='left  ' width='350'>"+datos['cliente']+"</td>";//$(this).agregar_comas(entry2['monto_pagado'])
                    fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='left  ' width='100'>"+datos['revision_cobro']+"</td>";
                    fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='right' width='123' >"+$(this).agregar_comas(parseFloat(datos['saldo_factura']).toFixed(2))+"</td>";//$(this).agregar_comas(saldo.toFixed(2))
                    fac_RC += "</tr>";
                    $tabla_facturas_revision.append(fac_RC);
               });
               seleccionar_facturas_check($tabla_facturas_revision);
          });
     }
	
	
	var seleccionar_facturas_check = function($tabla){
		$tabla.find('input[name=micheck]').each(function(){
			$(this).click(function(event){
				var factura=$(this).parent().parent().find('#fac').html();
				
				if(this.checked){
					$(this).parent().find('input[name=seleccionado]').val("1");
					
					$tabla.find('input[name=micheck]').each(function(){
						//esto es para seleccionar las partidas de la factura seleccionada
						if( $(this).parent().parent().find('#fac').html() == factura ){
							this.checked = true;
							$(this).parent().find('input[name=seleccionado]').val("1");
						}
					});
					
				}else{
					//$(this).parent().find('input[name=micheck]').removeAttr('checked');
					$(this).parent().find('input[name=seleccionado]').val("0");
				}
			});
		});
		
		
		$tabla.find('input[name=envase]').each(function(){
			$(this).focus(function(e){
				if($(this).val() == ' '){
					$(this).val('');
				}
			});
			
			$(this).blur(function(e){
				if ( $(this).val() == ''  || $(this).val() == null ){
					$(this).val(' ');
				}
			});
		});
		
		
		$tabla.find('#eliminar').each(function(){
			$(this).click(function(event){
				event.preventDefault();
				$(this).parent().find('input[name=eliminado]').val("0");
				$(this).parent().parent().hide();
			});
		});
		
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



	//nuevo centro de costo
	$new_ruta.click(function(event){
          
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_logasignarutas();
		
		var form_to_show = 'formalogasignarutas';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-logasignarutas-window').css({ "margin-left": -450, 	"margin-top": -300 });
		$forma_selected.prependTo('#forma-logasignarutas-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		var $accion_proceso = $('#forma-logasignarutas-window').find('input[name=accion_proceso]');
		
		var $campo_id = $('#forma-logasignarutas-window').find('input[name=identificador]');
		var $total_tr = $('#forma-logasignarutas-window').find('input[name=total_tr]');
		var $select_chofer = $('#forma-logasignarutas-window').find('select[name=select_chofer]');
		var $numero_ruta = $('#forma-logasignarutas-window').find('input[name=numero_ruta]');
		var $select_unidad = $('#forma-logasignarutas-window').find('select[name=select_unidad]');
		var $fecha_inicial = $('#forma-logasignarutas-window').find('input[name=fecha_inicial]');
		var $fecha_final = $('#forma-logasignarutas-window').find('input[name=fecha_final]');
		var $folio_fac_rev = $('#forma-logasignarutas-window').find('input[name=folio_fac_rev_cobro]');
		var $href_buscar_facturas = $('#forma-logasignarutas-window').find('a[href*=busca_facturas]');
		var $tabla_facturas_body = $('#forma-logasignarutas-window').find('.tabla_facturas');
		var $tabla_facturas_revision = $('#forma-logasignarutas-window').find('.facturas_revision');
		var $genera_reporte_rutas= $('#forma-logasignarutas-window').find('#genera_pdf');
		var $confirmar= $('#forma-logasignarutas-window').find('#confirmar');
		
		var $agregar = $('#forma-logasignarutas-window').find('#agregar_factura');
		//href para agregar folios de programacion
		var $agregar_folio = $('#forma-logasignarutas-window').find('#agregar_folio');
		
		var $tipo_busqueda = $('#forma-logasignarutas-window').find('input[name=tipo_busqueda]');
		var $factura = $('#forma-logasignarutas-window').find('input[name=factura]');
        
		var $cerrar_plugin = $('#forma-logasignarutas-window').find('#close');
		var $cancelar_plugin = $('#forma-logasignarutas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-logasignarutas-window').find('#submit');
		
		$accion_proceso.attr({'value' : "new"});
		$campo_id.attr({ 'value' : 0 });
		$genera_reporte_rutas.attr('disabled','-1'); //deshabilitar
		$confirmar.attr('disabled','-1'); //deshabilitar
		
		//var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
		$fecha_inicial.attr("readonly", true);
		$fecha_final.attr("readonly", true);
		$numero_ruta.attr("readonly", true);
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Los datos de la Ruta Exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-logasignarutas-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-logasignarutas-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-logasignarutas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getrutas.json';
		$arreglo = {'id':id_to_show,
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				 };
		
		$.post(input_json,$arreglo,function(entry){
			//Alimentando los campos select de choferes
			$select_chofer.children().remove();
			var chofer_hmtl = '';
			$.each(entry['choferes'],function(entryIndex,choferes){
				chofer_hmtl += '<option value="' + choferes['id'] + '"  >' + choferes['nombre_chofer'] + '</option>';
			});
			$select_chofer.append(chofer_hmtl);

			//Alimentando los campos select de  unidades
			$select_unidad.children().remove();
			var unidad_hmtl = '';
			$.each(entry['vehiculos'],function(entryIndex,vehiculo){
				unidad_hmtl += '<option value="' + vehiculo['id'] + '"  >' + vehiculo['vehiculo'] + '</option>';
			});
			$select_unidad.append(unidad_hmtl);
		});//termina llamada json 


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
        
		mostrarFecha($fecha_inicial.val());
        	
        
		//fecha final
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
        
     	mostrarFecha($fecha_final.val());    
		
		
		$href_buscar_facturas.click(function(event){
			event.preventDefault();
			$tipo_busqueda.val("1");//busqueda por fecha
			if( $fecha_inicial.val()!='' && $fecha_final.val()!='' ){
				$buscar_facturas_mercancia ($tabla_facturas_body,$fecha_inicial, $fecha_final, $factura, $tipo_busqueda );
			}else{
				jAlert("Es necesario la Fecha Inicial y Final.", 'Atencion!');
			}
		});
		
		
		//agregar factura al listado
		$agregar.click(function(event){
			event.preventDefault();
			$tipo_busqueda.val("2");//busqueda por Serie Folio de Factura
			
			if( $factura.val()!=''){
				$buscar_facturas_mercancia ($tabla_facturas_body, $fecha_inicial, $fecha_final, $factura, $tipo_busqueda );
			}else{
				jAlert("Es necesario ingresar la Factura.", 'Atencion!');
			}
		});
		
		
		
		//desencadena clic del href Agregar Factura al pulsar enter en el campo factura
		$factura.keypress(function(e){
			if(e.which == 13){
				$agregar.trigger('click');
				return false;
			}
		});
		
		
		
		
		$agregar_folio.click(function(event){
		   event.preventDefault();
			if( $folio_fac_rev.val()!=''){
				grid_fac_rev_cobro();
			}else{
				jAlert("Es necesario ingresar el Folio de la Programacion de Ruta.", 'Atencion!');
			}
		});
		
		
		//desencadena clic del href Agregar Factura al pulsar enter en el campo factura
		$folio_fac_rev.keypress(function(e){
			if(e.which == 13){
				$agregar_folio.trigger('click');
				return false;
			}
		});
		
		
		
		
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-logasignarutas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-logasignarutas-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$submit_actualizar.bind('click',function(){
			var selec=0;
			var selec_FRC=0;
			
			if($select_chofer.val() !=0){
			   if($select_unidad.val() !=0){
					//checa facturas a revision seleccion
					selec = contar_seleccionados($tabla_facturas_body);

					//checa facturas a Cobro seleccionadas
					selec = parseInt(selec) + parseInt(contar_seleccionados($tabla_facturas_revision));
					
					$total_tr.val(selec);
					if(parseInt($total_tr.val()) > 0){
						return true;
					}else{
						jAlert("No hay facturas seleccionadas para actualizar", 'Atencion!');
						return false;
					}
			   }else{
				 jAlert("Elija una Unidad para la Ruta",'Atencion!!!');   
			   }  
			}else{jAlert("Elija un Chofer para la Ruta",'Atencion!!!');}
		});
	});
	
	
     
	var carga_formalogasignarutas00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar La ruta asignada ', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Ruta  fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Ruta no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formalogasignarutas';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_logasignarutas();
			$('#forma-logasignarutas-window').css({ "margin-left": -450, 	"margin-top": -300 });
			
			$forma_selected.prependTo('#forma-logasignarutas-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			var $accion_proceso = $('#forma-logasignarutas-window').find('input[name=accion_proceso]');
			
			var $campo_id = $('#forma-logasignarutas-window').find('input[name=identificador]');
			var $select_chofer = $('#forma-logasignarutas-window').find('select[name=select_chofer]');
			var $numero_ruta = $('#forma-logasignarutas-window').find('input[name=numero_ruta]');
			var $select_unidad = $('#forma-logasignarutas-window').find('select[name=select_unidad]');
			var $fecha_inicial = $('#forma-logasignarutas-window').find('input[name=fecha_inicial]');
			var $fecha_final = $('#forma-logasignarutas-window').find('input[name=fecha_final]');
			var $folio_fac_rev = $('#forma-logasignarutas-window').find('input[name=folio_fac_rev_cobro]');
			
			var $href_buscar_facturas = $('#forma-logasignarutas-window').find('a[href*=busca_facturas]');
			var $tabla_facturas_body = $('#forma-logasignarutas-window').find('.tabla_facturas');//.find('.contenido_facturas');
			var $tabla_facturas_revision = $('#forma-logasignarutas-window').find('.facturas_revision');//.find('.contenido_facturas');        
			var $tipo_busqueda = $('#forma-logasignarutas-window').find('input[name=tipo_busqueda]');
			var $factura = $('#forma-logasignarutas-window').find('input[name=factura]');
			
			var $agregar = $('#forma-logasignarutas-window').find('#agregar_factura');
			//href para agregar folios de programacion
			var $agregar_folio = $('#forma-logasignarutas-window').find('#agregar_folio');
			
			var $genera_reporte_rutas= $('#forma-logasignarutas-window').find('#genera_pdf');
			var $confirmar= $('#forma-logasignarutas-window').find('#confirmar');
			var $total_tr = $('#forma-logasignarutas-window').find('input[name=total_tr]');
			
			var $cerrar_plugin = $('#forma-logasignarutas-window').find('#close');
			var $cancelar_plugin = $('#forma-logasignarutas-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-logasignarutas-window').find('#submit');
			
			$fecha_inicial.attr('disabled','-1');
			$fecha_final.attr('disabled','-1');
			$numero_ruta.attr("readonly", true);
			
			$href_buscar_facturas.hide();
			
                    
			if(accion_mode == 'edit'){
				$accion_proceso.attr({'value' : "edit"});
				
				var iu = $('#lienzo_recalculable').find('input[name=iu]').val();         
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getrutas_editar.json';
				$arreglo = {'id':id_to_show ,'iu':iu};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-logasignarutas-overlay').fadeOut(remove);
						jAlert("La ruta  se ha actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-logasignarutas-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-logasignarutas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$campo_id.val(entry['header']['0']['id_ruta']);
					$numero_ruta.val(entry['header']['0']['folio']);
					$fecha_final.val(entry['header']['0']['fecha']);
					 
					 //Alimentando los campos select de choferes
					 $select_chofer.children().remove();
					 var chofer_hmtl = '';
					 $.each(entry['choferes'],function(entryIndex,choferes){
						  if(choferes['id'] == entry['header']['0']['id_chofer']){
							   chofer_hmtl += '<option value="' + choferes['id'] + '" selected="yes" >' + choferes['nombre_chofer'] + '</option>'; 
						  }else{
							   //chofer_hmtl += '<option value="' + choferes['id'] + '" >' + choferes['nombre_chofer'] + '</option>'; 
						  }
					 });
					 $select_chofer.append(chofer_hmtl);
					 
					 //Alimentando los campos select de  unidades
					 var unidad_hmtl = '';
					 $select_unidad.children().remove();
					 $.each(entry['vehiculos'],function(entryIndex,vehiculo){
						  if(vehiculo['id'] == entry['header']['0']['id_vehiculo']){
							   unidad_hmtl += '<option value="' + vehiculo['id'] + '"  selected="yes">' + vehiculo['vehiculo'] + '</option>';
						  }else{
							   //unidad_hmtl += '<option value="' + vehiculo['id'] + '"  >' + vehiculo['vehiculo'] + '</option>';
						  }
					 });
					 $select_unidad.append(unidad_hmtl);
					 
					 //$GRID_FAC_DOCS($tabla_facturas_body,entry);
					 //$GRID_FRC($tabla_facturas_revision,entry);
					 
					 
					var valor_seleccionado="0";
					var check_desactivado="";
					var check_seleccionado="";
					var input_desactivado="";
								 
					//carga las facturas que se enviaron a Entrega de Mercancia
					$tabla_facturas_body.children().remove();
					$.each(entry['minigrid_Rutas'],function(entryIndex, datos){
						valor_seleccionado="0";
						check_desactivado="";
						check_seleccionado="";
						input_desactivado="";
						
						if ( datos['enviado'] == 'true' ){
							check_desactivado="";
							valor_seleccionado="1";
							check_seleccionado = "checked";
						}else{
							//si ya esta confirmado, se deshabilitan los campos no seleccionados para ya no permitir seleccionar
							 if(entry['header']['0']['confirmado']=='true' ){
								check_desactivado="disabled='disabled'";
								input_desactivado="readOnly='true'";
							 }
							valor_seleccionado="0";
							check = "";
						}
						
						
						if(datos['envase']==null || datos['envase']==''){
							envase=' ';
						}else{
							envase=datos['envase'];
						}
						
						var noTr = $("tr", $tabla_facturas_body).size();
						
						noTr++;
					
						var contenido_facturas="";
						contenido_facturas += "<tr class='gral'>";
						contenido_facturas += "<td  style='font-size: 11px;  border:1px solid #C1DAD7;' align='center' width='30' >";
						contenido_facturas += "<input type='checkbox' name='micheck' value='check' "+check_seleccionado+" "+check_desactivado+">";
						contenido_facturas += "<input type='hidden' name='id_detalle' value='"+datos['id_detalle']+"'>";
						contenido_facturas += '<input type="hidden" name="tipo" value="FAC_DOCS">';
						contenido_facturas += '<input type="hidden" name="fac_docs_detalle_id" value="'+datos['id_fac_docs']+'">';
						contenido_facturas += '<input type="hidden" name="inv_prod_id" value="'+datos['id_invprod']+'">';
						contenido_facturas += '<input type="hidden" name="fac_rev_cobro" value="0">';
						contenido_facturas += '<input type="hidden" name="seleccionado" value="'+valor_seleccionado+'">';
						contenido_facturas += '<input type="hidden"  name="eliminado" value="1">';
						contenido_facturas += "</td>";
						
						contenido_facturas += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='70' ><span id='fac'>"+datos['factura'] + "</span></td>";
						contenido_facturas += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='70' >"+datos['fecha_factura'] +"</td>";
						contenido_facturas += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='left  ' width='320'>"+datos['cliente']+"</td>";
						contenido_facturas += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='70' >"+datos['codigo']+"</td>";
						contenido_facturas += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='60' >"+datos['cantidad']+"</td>";
						
						contenido_facturas += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' align='left' width='120'>" +datos['descripcion']+ "</td>";
						contenido_facturas += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' align='right' width='80'>" +$(this).agregar_comas(parseFloat(datos['importe']).toFixed(2))+ "</td>";
						contenido_facturas += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' align='left ' width='66'>" +"<INPUT TYPE='text' name='envase'  class='envase'"+noTr+"' style=width:65px;' value='"+envase+"'  "+input_desactivado+" >"+ "</td>";
						contenido_facturas += "</tr>";
						
						$tabla_facturas_body.append(contenido_facturas);
						
						
					});
					seleccionar_facturas_check($tabla_facturas_body);
					
					
					
					$tabla_facturas_revision.children().remove();
					$.each(entry['minigrid_FRC'],function(entryIndex, datos){
						
						valor_seleccionado="0";
						check_desactivado="";
						check_seleccionado="";
						
						if ( datos['enviado'] == 'true' ){
							check_desactivado="";
							valor_seleccionado="1";
							check_seleccionado = "checked";
						}else{
							//si ya esta confirmado, se deshabilitan los campos no seleccionados para ya no permitir seleccionar
							 if(entry['header']['0']['confirmado']=='true' ){
								check_desactivado="disabled='disabled'";
							 }
							valor_seleccionado="0";
							check_seleccionado = "";
						}
						
						var fac_RC="";
						fac_RC += '<tr>';
						fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' align='center' width='30' >";
							fac_RC += "<input type='checkbox' name='micheck' value='check' "+check_seleccionado+" "+check_desactivado+">";
							fac_RC += '<input type="hidden"  name="tipo" value="RC">';
							fac_RC += '<input type="hidden" id="delete" name="fac_docs_detalle_id" value="0">';
							fac_RC += '<input type="hidden" id="delete" name="inv_prod_id" value="0">';
							fac_RC += '<input type="text"  name="envase" value="0" style="display:none;">';
							fac_RC += '<input type="hidden" id="delete" name="fac_rev_cobro" value="'+datos['fac_rev_cob_detalle_id']+'">';
							fac_RC += '<input type="hidden" name="id_detalle" value="'+datos['id_detalle']+'">';
							fac_RC += '<input type="hidden"  name="seleccionado" value="'+valor_seleccionado+'">';
						fac_RC += '</td>';
						fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='60' ><span id='fac'>"+datos['folio_programacion'] + "</span></td>";//folio-programacion
						fac_RC += "<td style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='60' >";
							fac_RC += "<a href='elim' id='eliminar'>Eliminar</a>";
							fac_RC += '<input type="hidden"  name="eliminado" value="1">';
						fac_RC += "</td>";//eliminar
						
						fac_RC += "<td  style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='70' ><span id='fac'>"+datos['factura'] + "</span></td>";//entry2['numero_factura']
						fac_RC += "<td  style='font-size: 11px;  border:1px solid #C1DAD7;'  align='center' width='70' >"+datos['fecha_factura'] +"</td>";//$(this).agregar_comas(entry['monto_factura'])
						fac_RC += "<td  style='font-size: 11px;  border:1px solid #C1DAD7;'  align='left  ' width='350'>"+datos['cliente']+"</td>";//$(this).agregar_comas(entry2['monto_pagado'])
						fac_RC += "<td  style='font-size: 11px;  border:1px solid #C1DAD7;'  align='left  ' width='100'>"+datos['revision_cobro']+"</td>";
						fac_RC += "<td  style='font-size: 11px;  border:1px solid #C1DAD7;'  align='right' width='123' >"+$(this).agregar_comas(parseFloat(datos['saldo_factura']).toFixed(2))+"</td>";//$(this).agregar_comas(saldo.toFixed(2))
						fac_RC += "</tr>";
						$tabla_facturas_revision.append(fac_RC);
						
					});
					seleccionar_facturas_check($tabla_facturas_revision);
					 
					 
					 
					 if(entry['header']['0']['confirmado']=='true' ){
						 $confirmar.attr('disabled','-1');
						 $factura.attr('disabled','-1');
						 $agregar.hide();
						 $folio_fac_rev.attr('disabled','-1');
						 $agregar_folio.hide();
						 $tabla_facturas_revision.find('#eliminar').hide();//oculta eliminar del listado si la asignacion de ruta ya esta confirmado
					 }
					 
					 
					 
					 
				},"json");//termina llamada json
				
				
				
				$fecha_inicial.DatePicker({
					format:'Y-m-d',
					date: $fecha_inicial.val(),
					current:$fecha_inicial.val(),
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
				
				mostrarFecha($fecha_inicial.val());//fecha final
				   
			   $fecha_final.DatePicker({
					format:'Y-m-d',
					date: $fecha_final.val(),
					current: $fecha_final.val(),
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
				
				mostrarFecha($fecha_final.val()); 
				
				
				
				
				//agregar factura al listado
				$agregar.click(function(event){
					event.preventDefault();
					$tipo_busqueda.val("2");//busqueda por Serie Folio de Factura
					
					if( $factura.val()!=''){
						$buscar_facturas_mercancia ($tabla_facturas_body,$fecha_inicial, $fecha_final, $factura, $tipo_busqueda );
					}else{
						jAlert("Es necesario ingresar la Factura.", 'Atencion!');
					}
				});
				
				
				//desencadena clic del href Agregar Factura al pulsar enter en el campo factura
				$factura.keypress(function(e){
					if(e.which == 13){
						$agregar.trigger('click');
						return false;
					}
				});
				
				
				
				
				$agregar_folio.click(function(event){
				   event.preventDefault();
				   grid_fac_rev_cobro();
				});
				
				//desencadena clic del href Agregar Factura al pulsar enter en el campo factura
				$folio_fac_rev.keypress(function(e){
					if(e.which == 13){
						grid_fac_rev_cobro();
						return false;
					}
				});
				
				
				
				
				$genera_reporte_rutas.click(function(event){
					event.preventDefault();
					iu=$('#lienzo_recalculable').find('input[name=iu]').val();
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfRuta/'+$campo_id.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
				
				
				
				$confirmar.click(function(e){
					$accion_proceso.attr({'value' : "confirmar"});
					jConfirm('Confirmar Ruta?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.parents("FORM").submit();
						}else{
							$accion_proceso.attr({'value' : "edit"});
						}
					});
					// Always return false here since we don't know what jConfirm is going to do
					return false;
				});
				
				
				

				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-logasignarutas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-logasignarutas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                    
				$submit_actualizar.bind('click',function(){
					 var selec=0;
					 var selec_FRC=0;
					 if($select_chofer.val() !=0){
						 if($select_unidad.val() !=0){
							//checa facturas a revision seleccion
							selec = contar_seleccionados($tabla_facturas_body);
							//checa facturas a Cobro seleccionadas
							selec = parseInt(selec) + parseInt(contar_seleccionados($tabla_facturas_revision));
							$total_tr.val(selec);

							if(parseInt($total_tr.val()) > 0){
								 return true;
							}else{
								 jAlert("No hay facturas seleccionadas para actualizar", 'Atencion!');
								 return false;
							}
						 }else{
							  jAlert("Elija una Unidad para la Ruta",'Atencion!!!');   
						 }
					 }else{
						jAlert("Elija un Chofer para la Ruta",'Atencion!!!');
					}
			   });
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllRutas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllRutas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenableEdit(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formalogasignarutas00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
	
    $get_datos_grid();
    
    
});
