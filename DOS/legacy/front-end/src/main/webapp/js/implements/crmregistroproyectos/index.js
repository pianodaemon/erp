$(function() {
	String.prototype.toCharCode = function(){
		var str = this.split(''), len = str.length, work = new Array(len);
		for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
		}
		return work.join(',');
	};
	
    //Arreglo para select  de Forma de calculo
    var arrayPrioridad = {1:"Baja", 2:"Media", 3:"Alta"};
    var arrayMuestra = {1:"N/A", 2:"Pendiente", 3:"OK"};
    var arrayPeriodicidad = {1:"Mensual", 2:"Anual"};
			
		
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
        
	var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/crmregistroproyectos";
    
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_crmregistroproyectos = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append(document.title);
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_proyecto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_proyecto]');
	var $busqueda_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_agente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_segmento = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_segmento]');
	var $busqueda_mercado = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_mercado]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "proyecto" + signo_separador + $busqueda_proyecto.val() + "|";
		valor_retorno += "agente" + signo_separador + $busqueda_agente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val() + "|"
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|"
		valor_retorno += "segmento" + signo_separador + $busqueda_segmento.val() + "|"
		valor_retorno += "mercado" + signo_separador + $busqueda_mercado.val() + "|"
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val();
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
		$busqueda_folio.val('');
		$busqueda_proyecto.val('');
		$busqueda_cliente.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		iniciarDatosBuscador();
		
	});
	
	
	TriggerClickVisializaBuscador = 0;
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		
		var alto=0;
		if(TriggerClickVisializaBuscador==0){
			 TriggerClickVisializaBuscador=1;
			 var height2 = $('#cuerpo').css('height');
			 
			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
			 $('#barra_buscador').animate({height: '60px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
			 $busqueda_folio.focus();
		}else{
			 TriggerClickVisializaBuscador=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
			 
			 $visualiza_buscador.focus();
		};
		
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_proyecto, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_agente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_segmento, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_mercado, $buscar);
	
	
	var iniciarDatosBuscador = function(){
		var input_json_agente = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDatosParaBuscador.json';
		var $arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_agente,$arreglo,function(data){
			//Alimentando los campos select_agente
			$busqueda_agente.children().remove();
			var agente_hmtl = '';
			
			if(data['Extra'][0]['mostrarAgentes']){
				agente_hmtl += '<option value="0" >[-- Selecionar Empleado --]</option>';
			}
			
			$.each(data['Agentes'],function(entryIndex,agente){
				if(parseInt(agente['id'])==parseInt(data['Extra'][0]['no_agen'])){
					agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
				}else{
					if(data['Extra'][0]['mostrarAgentes']){
						agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
					}
				}
			});
			$busqueda_agente.append(agente_hmtl);
			
			$busqueda_segmento.children().remove();
			var clas1_hmtl = '<option value="0">[-- Selecionar --]</option>';
			$.each(data['Segmentos'],function(entryIndex,clas1){
				clas1_hmtl += '<option value="' + clas1['id'] + '"  >' + clas1['clasificacion1'] + '</option>';
			});
			$busqueda_segmento.append(clas1_hmtl);
			
			$busqueda_mercado.children().remove();
			var clasif2_html = '<option value="0">[-- Selecionar --]</option>';
			$.each(data['Mercados'],function(entryIndex,clas2){
				clasif2_html += '<option value="' + clas2['id'] + '"  >' + clas2['clasificacion2'] + '</option>';
			});
			$busqueda_mercado.append(clasif2_html);
			
		});
	}
	
	iniciarDatosBuscador();
	
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


	//Funcion para aplicar evento focus
	var $aplicar_evento_focus = function($campo){
		$campo.focus(function(e){
			if($(this).val().trim()==''){
				$(this).val('');
			}else{
				if(parseFloat($(this).val())<=0){
					$(this).val('');
				}
			}
		});
	}
	
        
        
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
	
	
	
	//Carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero){
		$campo_select.children().remove();
		var select_html = '';
		if(texto_elemento_cero.trim()!=''){
			select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		}
		
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
			}
		}
		$campo_select.append(select_html);
	}
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-crmregistroproyectos-window').find('#submit').mouseover(function(){
			$('#forma-crmregistroproyectos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-crmregistroproyectos-window').find('#submit').mouseout(function(){
			$('#forma-crmregistroproyectos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-crmregistroproyectos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-crmregistroproyectos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-crmregistroproyectos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-crmregistroproyectos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-crmregistroproyectos-window').find('#close').mouseover(function(){
			$('#forma-crmregistroproyectos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-crmregistroproyectos-window').find('#close').mouseout(function(){
			$('#forma-crmregistroproyectos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-crmregistroproyectos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-crmregistroproyectos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-crmregistroproyectos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-crmregistroproyectos-window').find("ul.pestanas li").click(function() {
			$('#forma-crmregistroproyectos-window').find(".contenidoPes").hide();
			$('#forma-crmregistroproyectos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-crmregistroproyectos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	/*funcion para colorear la fila en la que pasa el puntero*/
	$colorea_tr_grid = function($tabla){
		$tabla.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
		$tabla.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});
		
		$('tr:odd' , $tabla).hover(function () {
			$(this).find('td').css({background : '#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#e7e8ea'});
		});
		$('tr:even' , $tabla).hover(function () {
			$(this).find('td').css({'background-color':'#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#FFFFFF'});
		});
	};
        
	
	//buscador de Contactos
	$busca_contactos = function(busqueda_inicial, $select_segmento,$select_mercado,arregloSegmentos,arregloMercados){
		//limpiar_campos_grids();
		$(this).modalPanel_BuscaContacto();
		var $dialogoc =  $('#forma-buscacontactos-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_contactos').find('table.formaBusqueda_contactos').clone());
		
		$('#forma-buscacontactos-window').css({"margin-left": -180, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscacontactos-window').find('#tabla_resultado');
		
		var $campo_buscador_nombre = $('#forma-buscacontactos-window').find('input[name=buscador_nombre]');
		var $campo_buscador_apellidop = $('#forma-buscacontactos-window').find('input[name=buscador_apellidop]');
		var $campo_buscador_apellidom = $('#forma-buscacontactos-window').find('input[name=buscador_apellidom]');
		var $select_buscador_tipo_contacto = $('#forma-buscacontactos-window').find('select[name=buscador_tipo_contacto]');
		
		var $buscar_plugin_contacto = $('#forma-buscacontactos-window').find('#busca_contacto_modalbox');
		var $cancelar_plugin_busca_contacto = $('#forma-buscacontactos-window').find('#cencela');
		
		//funcionalidad botones
		$buscar_plugin_contacto.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$buscar_plugin_contacto.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_contacto.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_contacto.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		$campo_buscador_nombre.val(busqueda_inicial);
		
		//click buscar productos
		$buscar_plugin_contacto.click(function(event){
                    
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_contactos.json';
			$arreglo = {'buscador_nombre':$campo_buscador_nombre.val().trim(),'buscador_apellidop':$campo_buscador_apellidop.val().trim(),
			'buscador_apellidom':$campo_buscador_apellidom.val().trim(),'buscador_tipo_contacto':$select_buscador_tipo_contacto.val(),'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				
				$.each(entry['contactos'],function(entryIndex,prospecto){
					trr = '<tr>';
						trr += '<td width="280px">';
							trr += '<span class="contacto_buscador">'+prospecto['contacto']+'</span>';
							trr += '<input type="hidden" id="id_contacto_buscador" value="'+prospecto['id']+'">';
							trr += '<input type="hidden" id="segmento_id_buscador" value="'+prospecto['segmento_id']+'">';
							trr += '<input type="hidden" id="mercado_id_buscador" value="'+prospecto['mercado_id']+'">';
						trr += '</td>';
						trr += '<td width="210px"><span class="razon_social_buscador">'+prospecto['razon_social']+'</span></td>';
						trr += '<td width="110px"><span class="rfc_buscador">'+prospecto['rfc']+'</span></td>';
					trr += '</tr>';
					$tabla_resultados.append(trr);
				});
				
				$colorea_tr_grid($tabla_resultados);
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					var id_segmento = $(this).find('#segmento_id_buscador').val()
					var id_mercado = $(this).find('#mercado_id_buscador').val()
					
					$('#forma-crmregistroproyectos-window').find('input[name=id_contacto]').val($(this).find('#id_contacto_buscador').val());
					$('#forma-crmregistroproyectos-window').find('input[name=contacto]').val($(this).find('span.contacto_buscador').html());
					$('#forma-crmregistroproyectos-window').find('input[name=cliente]').val($(this).find('span.razon_social_buscador').html());
					
					$select_segmento.children().remove();
					var segmento_hmtl = '<option value="0">[-- Selecionar --]</option>';
					$.each(arregloSegmentos,function(entryIndex,clas1){
						if(parseInt(clas1['id'])==parseInt(id_segmento)){
							segmento_hmtl += '<option value="' + clas1['id'] + '" selected="yes">' + clas1['clasificacion1'] + '</option>';
						}else{
							segmento_hmtl += '<option value="' + clas1['id'] + '" >' + clas1['clasificacion1'] + '</option>';
						}
					});
					$select_segmento.append(segmento_hmtl);
					
					$select_mercado.children().remove();
					var mercado_html = '<option value="0">[-- Selecionar --]</option>';
					$.each(arregloMercados,function(entryIndex,clas2){
						if(parseInt(clas2['id'])==parseInt(id_mercado)){
							mercado_html += '<option value="' + clas2['id'] + '" selected="yes">' + clas2['clasificacion2'] + '</option>';
						}else{
							mercado_html += '<option value="' + clas2['id'] + '">' + clas2['clasificacion2'] + '</option>';
						}
					});
					$select_mercado.append(mercado_html);
					
					//oculta la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacontactos-overlay').fadeOut(remove);
				});
			});
		});
	
		//si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
		if(busqueda_inicial != ''){
			$buscar_plugin_contacto.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_buscador_nombre, $buscar_plugin_contacto);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_buscador_apellidop, $buscar_plugin_contacto);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_buscador_apellidom, $buscar_plugin_contacto);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_buscador_tipo_contacto, $buscar_plugin_contacto);
		
		$cancelar_plugin_busca_contacto.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscacontactos-overlay').fadeOut(remove);
		});
        
        $campo_buscador_nombre.focus();
                
	}//termina buscador de Contactos
	
	
	
	//buscador de proveedores
	var $busca_proveedores = function(no_proveedor, nombre_proveedor){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_no_proveedor = $('#forma-buscaproveedor-window').find('input[name=campo_no_proveedor]');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
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
		
		$campo_no_proveedor.val(no_proveedor);
		$campo_nombre.val(nombre_proveedor);
		
		$campo_nombre.focus();
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProveedores.json';
			var $arreglo = {'rfc':$campo_rfc.val(), 'no_prov':$campo_no_proveedor.val(), 'nombre':$campo_nombre.val(), 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['proveedores'],function(entryIndex,proveedor){
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							//trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							//trr += '<input type="hidden" id="no_prov" value="'+proveedor['no_proveedor']+'">';
							//trr += '<input type="hidden" id="impto_id" value="'+proveedor['impuesto_id']+'">';
							//trr += '<input type="hidden" id="valor_impto" value="'+proveedor['valor_impuesto']+'">';
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
					$('#forma-crmregistroproyectos-window').find('input[name=id_prov]').val($(this).find('#id_prov').val());
					$('#forma-crmregistroproyectos-window').find('input[name=proveedor]').val($(this).find('#razon_social').html());
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
					
					$('#forma-crmregistroproyectos-window').find('input[name=proveedor]').focus();
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
			$('#forma-crmregistroproyectos-window').find('input[name=proveedor]').focus();
		});
	}//termina buscador de proveedores
	
	
	
	var $agrega_fila_grid = function($grid_registros, id, competidor, precio, proveedor){
		var notr = $("tr", $grid_registros).size();
		notr++;
		
		var trr = '';
		
		trr = '<tr>';
			trr += '<td class="grid1">'+ notr +'</td>';
			trr += '<td class="grid1" width="200">';
				trr += '<input type="hidden" name="iddet" value="'+ id +'"">';
				trr += '<input type="text" name="competidor" id="competidor'+ notr +'" value="'+ competidor +'" style="width:197px;">';
			trr += '</td>';
			trr += '<td class="grid1" width="100"><input type="text" name="precio" id="precio'+ notr +'" value="'+ precio +'" style="width:97px;"></td>';
			trr += '<td class="grid1" width="200"><input type="text" name="prov" id="prov'+ notr +'" value="'+ proveedor +'" style="width:197px;"></td>';
		trr += '</tr>';
		
		$grid_registros.append(trr);
		
		$grid_registros.find('#precio'+ notr).keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
        
		$aplicar_evento_focus($grid_registros.find('#precio'+ notr));
		
		$grid_registros.find('#precio'+ notr).blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
		
	}
	
	
	//nuevo
	$new_crmregistroproyectos.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_crmregistroproyectos();
		
		var form_to_show = 'formacrmregistroproyectos';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-crmregistroproyectos-window').css({"margin-left": -400, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-crmregistroproyectos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-crmregistroproyectos-window').find('input[name=identificador]');
		var $folio = $('#forma-crmregistroproyectos-window').find('input[name=folio]');
		var $fecha_alta = $('#forma-crmregistroproyectos-window').find('input[name=fecha_alta]');
		var $nombre = $('#forma-crmregistroproyectos-window').find('input[name=nombre]');
		var $descripcion = $('#forma-crmregistroproyectos-window').find('textarea[name=descripcion]');
		var $select_agente = $('#forma-crmregistroproyectos-window').find('select[name=select_agente]');
		
		var $id_contacto = $('#forma-crmregistroproyectos-window').find('input[name=id_contacto]');
		var $contacto = $('#forma-crmregistroproyectos-window').find('input[name=contacto]');
		var $cliente = $('#forma-crmregistroproyectos-window').find('input[name=cliente]');
		var $busca_contacto = $('#forma-crmregistroproyectos-window').find('#busca_contacto');
		
		var $id_prov = $('#forma-crmregistroproyectos-window').find('input[name=id_prov]');
		var $proveedor = $('#forma-crmregistroproyectos-window').find('input[name=proveedor]');
		var $busca_proveedor = $('#forma-crmregistroproyectos-window').find('#busca_proveedor');
		
		var $fecha_inicio = $('#forma-crmregistroproyectos-window').find('input[name=fecha_inicio]');
		var $fecha_fin = $('#forma-crmregistroproyectos-window').find('input[name=fecha_fin]');
		var $monto = $('#forma-crmregistroproyectos-window').find('input[name=monto]');
		var $kilogramos = $('#forma-crmregistroproyectos-window').find('input[name=kilogramos]');
		
		var $select_estatus = $('#forma-crmregistroproyectos-window').find('select[name=select_estatus]');
		var $select_prioridad = $('#forma-crmregistroproyectos-window').find('select[name=select_prioridad]');
		var $select_muestra = $('#forma-crmregistroproyectos-window').find('select[name=select_muestra]');
		var $select_periodicidad = $('#forma-crmregistroproyectos-window').find('select[name=select_periodicidad]');
		var $select_moneda = $('#forma-crmregistroproyectos-window').find('select[name=select_moneda]');
		var $observaciones = $('#forma-crmregistroproyectos-window').find('textarea[name=observaciones]');
		var $select_segmento = $('#forma-crmregistroproyectos-window').find('select[name=select_segmento]');
		var $select_mercado = $('#forma-crmregistroproyectos-window').find('select[name=select_mercado]');
		
		var $grid_registros = $('#forma-crmregistroproyectos-window').find('#grid_registros');
		
		var $cerrar_plugin = $('#forma-crmregistroproyectos-window').find('#close');
		var $cancelar_plugin = $('#forma-crmregistroproyectos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-crmregistroproyectos-window').find('#submit');
		
		$folio.css({'background' : '#DDDDDD'});
		$fecha_alta.css({'background' : '#DDDDDD'});
		$cliente.css({'background' : '#DDDDDD'});
		$identificador.attr({'value' : 0});
		$id_contacto.attr({'value' : 0});
		$id_prov.attr({'value' : 0});
		$fecha_alta.val(mostrarFecha());
		$monto.attr({'value' :parseFloat(0).toFixed(2)});
		$kilogramos.attr({'value' :parseFloat(0).toFixed(2)});
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("El Proyecto se registr&oacute; con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-crmregistroproyectos-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-crmregistroproyectos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-crmregistroproyectos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroProyecto.json';
		var $arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			
			//Alimentando los campos select_agente
			$select_agente.children().remove();
			var agente_hmtl = '';
			if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
				agente_hmtl += '<option value="0" >[-- Selecionar Empleado --]</option>';
			}
			
			$.each(entry['Agentes'],function(entryIndex,agente){
				if(parseInt(agente['id'])==parseInt(entry['Extra'][0]['id_agente'])){
					agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
				}else{
					//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
					if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
						agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
					}
				}
			});
			$select_agente.append(agente_hmtl);
			
			$select_estatus.children().remove();
			var estatus_hmtl='';
			$.each(entry['Estatus'],function(entryIndex,estat){
				estatus_hmtl += '<option value="' + estat['id'] + '" >' + estat['titulo'] + '</option>';
			});
			$select_estatus.append(estatus_hmtl);
			
			//Alimentando los campos select_moneda
			$select_moneda.children().remove();
			var moneda_hmtl='';
			$.each(entry['Monedas'],function(entryIndex,mon){
				moneda_hmtl += '<option value="' + mon['id'] + '">' + mon['descripcion_abr'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
			
			$select_segmento.children().remove();
			var segmento_hmtl = '<option value="0">[-- Selecionar --]</option>';
			$.each(entry['Segmentos'],function(entryIndex,clas1){
				segmento_hmtl += '<option value="' + clas1['id'] + '"  >' + clas1['clasificacion1'] + '</option>';
			});
			$select_segmento.append(segmento_hmtl);
			
			$select_mercado.children().remove();
			var mercado_html = '<option value="0">[-- Selecionar --]</option>';
			$.each(entry['Mercados'],function(entryIndex,clas2){
				mercado_html += '<option value="' + clas2['id'] + '"  >' + clas2['clasificacion2'] + '</option>';
			});
			$select_mercado.append(mercado_html);
			
			$busca_contacto.click(function(event){
				event.preventDefault();
				$busca_contactos($contacto.val(),$select_segmento,$select_mercado,entry['Segmentos'],entry['Mercados']);
			});
			
			//Asignar evento keypress al campo Nombre del Contacto
			$(this).aplicarEventoKeypressEjecutaTrigger($contacto, $busca_contacto);
			
		},"json");//termina llamada json
        
        
        
		//cargar select
		var elemento_seleccionado = 0;
		var cadena_elemento_cero ="";
		$carga_campos_select($select_prioridad, arrayPrioridad, elemento_seleccionado, cadena_elemento_cero);
		
		//cargar select
		elemento_seleccionado = 0;
		cadena_elemento_cero ="";
		$carga_campos_select($select_muestra, arrayMuestra, elemento_seleccionado, cadena_elemento_cero);
		
		//cargar select
		elemento_seleccionado = 0;
		cadena_elemento_cero ="";
		$carga_campos_select($select_periodicidad, arrayPeriodicidad, elemento_seleccionado, cadena_elemento_cero);
				
		var competidor="";
		var precio="0.00";
		var proveedor="";
		
		for(var i=1; i<=3; i++){
			$agrega_fila_grid($grid_registros, 0, competidor, precio, proveedor);
		}
		
		$fecha_inicio.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_inicio.DatePicker({
			format:'Y-m-d',
			date: $fecha_inicio.val(),
			current: $fecha_inicio.val(),
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
				$fecha_inicio.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_inicio.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_inicio.val(mostrarFecha());
					}else{
						$fecha_inicio.DatePickerHide();	
					}
				}
			}
		});
		
		
		
		$fecha_fin.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100});
		});
		
		$fecha_fin.DatePicker({
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
				$fecha_fin.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_fin.val(),mostrarFecha());
					
					if (valida_fecha==true){
						$fecha_fin.DatePickerHide();	
					}else{
						jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
						$fecha_fin.val(mostrarFecha());
					}
				}
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
        
		$aplicar_evento_focus($monto);
		
		$monto.blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
		
		$kilogramos.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		$aplicar_evento_focus($kilogramos);
		
		$kilogramos.blur(function(){
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			
			$(this).val(parseFloat($(this).val()).toFixed(2));
		});
		
        
        $busca_proveedor.click(function(event){
			event.preventDefault();
			$busca_proveedores('', $proveedor.val().trim())
        });
        
		//Asignar evento keypress al campo Proveedor
		$(this).aplicarEventoKeypressEjecutaTrigger($proveedor, $busca_proveedor);
        
		$submit_actualizar.bind('click',function(){
			if($contacto.val().trim()==''){
				$id_contacto.val(0);
			}
			
			if($proveedor.val().trim()==''){
				$id_prov.val(0);
			}
		});
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-crmregistroproyectos-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-crmregistroproyectos-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});		
	});
	
	
	
	
	
	var carga_formaDirecciones_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el registro seleccionado?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El registro fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El registro no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formacrmregistroproyectos';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_crmregistroproyectos();
			$('#forma-crmregistroproyectos-window').css({"margin-left": -400, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-crmregistroproyectos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $identificador = $('#forma-crmregistroproyectos-window').find('input[name=identificador]');
			var $folio = $('#forma-crmregistroproyectos-window').find('input[name=folio]');
			var $fecha_alta = $('#forma-crmregistroproyectos-window').find('input[name=fecha_alta]');
			var $nombre = $('#forma-crmregistroproyectos-window').find('input[name=nombre]');
			var $descripcion = $('#forma-crmregistroproyectos-window').find('textarea[name=descripcion]');
			var $select_agente = $('#forma-crmregistroproyectos-window').find('select[name=select_agente]');
			
			var $id_contacto = $('#forma-crmregistroproyectos-window').find('input[name=id_contacto]');
			var $contacto = $('#forma-crmregistroproyectos-window').find('input[name=contacto]');
			var $cliente = $('#forma-crmregistroproyectos-window').find('input[name=cliente]');
			var $busca_contacto = $('#forma-crmregistroproyectos-window').find('#busca_contacto');
			
			var $id_prov = $('#forma-crmregistroproyectos-window').find('input[name=id_prov]');
			var $proveedor = $('#forma-crmregistroproyectos-window').find('input[name=proveedor]');
			var $busca_proveedor = $('#forma-crmregistroproyectos-window').find('#busca_proveedor');
			
			var $fecha_inicio = $('#forma-crmregistroproyectos-window').find('input[name=fecha_inicio]');
			var $fecha_fin = $('#forma-crmregistroproyectos-window').find('input[name=fecha_fin]');
			var $monto = $('#forma-crmregistroproyectos-window').find('input[name=monto]');
			var $kilogramos = $('#forma-crmregistroproyectos-window').find('input[name=kilogramos]');
			
			var $select_estatus = $('#forma-crmregistroproyectos-window').find('select[name=select_estatus]');
			var $select_prioridad = $('#forma-crmregistroproyectos-window').find('select[name=select_prioridad]');
			var $select_muestra = $('#forma-crmregistroproyectos-window').find('select[name=select_muestra]');
			var $select_periodicidad = $('#forma-crmregistroproyectos-window').find('select[name=select_periodicidad]');
			var $select_moneda = $('#forma-crmregistroproyectos-window').find('select[name=select_moneda]');
			var $observaciones = $('#forma-crmregistroproyectos-window').find('textarea[name=observaciones]');
			var $select_segmento = $('#forma-crmregistroproyectos-window').find('select[name=select_segmento]');
			var $select_mercado = $('#forma-crmregistroproyectos-window').find('select[name=select_mercado]');
			
			var $grid_registros = $('#forma-crmregistroproyectos-window').find('#grid_registros');
			
			var $cerrar_plugin = $('#forma-crmregistroproyectos-window').find('#close');
			var $cancelar_plugin = $('#forma-crmregistroproyectos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-crmregistroproyectos-window').find('#submit');
			
			$folio.css({'background' : '#DDDDDD'});
			$fecha_alta.css({'background' : '#DDDDDD'});
			$cliente.css({'background' : '#DDDDDD'});
			$identificador.attr({'value' : 0});
			$id_contacto.attr({'value' : 0});
			$id_prov.attr({'value' : 0});
			$monto.attr({'value' :parseFloat(0).toFixed(2)});
			$kilogramos.attr({'value' :parseFloat(0).toFixed(2)});
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getRegistroProyecto.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("El registro se actualiz&oacute; con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-crmregistroproyectos-overlay').fadeOut(remove);
						//refresh_table();
						//$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-crmregistroproyectos-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-crmregistroproyectos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
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
					
					$identificador.attr({'value' : entry['Datos'][0]['id']});
					$folio.attr({'value' : entry['Datos'][0]['folio']});
					$fecha_alta.attr({'value' : entry['Datos'][0]['fecha']});
					$nombre.attr({'value' : entry['Datos'][0]['titulo']});
					$descripcion.text(entry['Datos'][0]['descripcion']);
					
					$id_contacto.attr({'value' : entry['Datos'][0]['contacto_id']});
					$contacto.attr({'value' : entry['Datos'][0]['contacto']});
					$cliente.attr({'value' : entry['Datos'][0]['cliente']});
					
					$id_prov.attr({'value' : entry['Datos'][0]['prov_id']});
					$proveedor.attr({'value' : entry['Datos'][0]['proveedor']});
					
					$fecha_inicio.attr({'value' : entry['Datos'][0]['fecha_inicio']});
					$fecha_fin.attr({'value' : entry['Datos'][0]['fecha_fin']});
					$monto.attr({'value' : entry['Datos'][0]['monto']});
					$kilogramos.attr({'value' : entry['Datos'][0]['kg']});
					
					$observaciones.text(entry['Datos'][0]['observaciones']);
					
					//Alimentando los campos select_agente
					$select_agente.children().remove();
					var agente_hmtl='';
					$.each(entry['Agentes'],function(entryIndex,agente){
						if(parseInt(agente['id'])==parseInt(entry['Datos'][0]['agen_id'])){
							agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
						}else{
							agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
						}
					});
					$select_agente.append(agente_hmtl);
					
					$select_estatus.children().remove();
					var estatus_hmtl='';
					$.each(entry['Estatus'],function(entryIndex,estat){
						if(parseInt(estat['id'])==parseInt(entry['Datos'][0]['estatus_id'])){
							estatus_hmtl += '<option value="' + estat['id'] + '" selected="yes">' + estat['titulo'] + '</option>';
						}else{
							estatus_hmtl += '<option value="' + estat['id'] + '" >' + estat['titulo'] + '</option>';
						}
					});
					$select_estatus.append(estatus_hmtl);
					
					//Alimentando los campos select_moneda
					$select_moneda.children().remove();
					var moneda_hmtl='';
					
					if(parseInt(entry['Datos'][0]['mon_id'])<=0){
						moneda_hmtl='<option value="0">[--- ---]</option>'
					}
					$.each(entry['Monedas'],function(entryIndex,mon){
						if(parseInt(mon['id'])==parseInt(entry['Datos'][0]['mon_id'])){
							moneda_hmtl += '<option value="' + mon['id'] + '" selected="yes">' + mon['descripcion_abr'] + '</option>';
						}else{
							moneda_hmtl += '<option value="' + mon['id'] + '" >' + mon['descripcion_abr'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					//cargar select
					var elemento_seleccionado = entry['Datos'][0]['prioridad'];
					var cadena_elemento_cero ="";
					$carga_campos_select($select_prioridad, arrayPrioridad, elemento_seleccionado, cadena_elemento_cero);
					
					//cargar select
					elemento_seleccionado = entry['Datos'][0]['muestra'];
					cadena_elemento_cero ="[--- ---]";
					$carga_campos_select($select_muestra, arrayMuestra, elemento_seleccionado, cadena_elemento_cero);
					
					//cargar select
					elemento_seleccionado = entry['Datos'][0]['periodicidad'];
					cadena_elemento_cero ="[--- ---]";
					$carga_campos_select($select_periodicidad, arrayPeriodicidad, elemento_seleccionado, cadena_elemento_cero);
					
					
					$select_segmento.children().remove();
					var segmento_hmtl = '<option value="0">[-- Selecionar --]</option>';
					$.each(entry['Segmentos'],function(entryIndex,clas1){
						if(parseInt(clas1['id'])==parseInt(entry['Datos'][0]['segmento_id'])){
							segmento_hmtl += '<option value="' + clas1['id'] + '" selected="yes">' + clas1['clasificacion1'] + '</option>';
						}else{
							segmento_hmtl += '<option value="' + clas1['id'] + '" >' + clas1['clasificacion1'] + '</option>';
						}
					});
					$select_segmento.append(segmento_hmtl);
					
					$select_mercado.children().remove();
					var mercado_html = '<option value="0">[-- Selecionar --]</option>';
					$.each(entry['Mercados'],function(entryIndex,clas2){
						if(parseInt(clas2['id'])==parseInt(entry['Datos'][0]['mercado_id'])){
							mercado_html += '<option value="' + clas2['id'] + '" selected="yes">' + clas2['clasificacion2'] + '</option>';
						}else{
							mercado_html += '<option value="' + clas2['id'] + '">' + clas2['clasificacion2'] + '</option>';
						}
					});
					$select_mercado.append(mercado_html);
					
					
					var cont=1;
					if(entry['Competidores']){
						if(parseInt(entry['Competidores'].length) > 0 ){
							$.each(entry['Competidores'],function(entryIndex,competidor){
								
								$agrega_fila_grid($grid_registros, competidor['id'], competidor['nombre'], competidor['precio'], competidor['proveedor']);
								
								cont++;
							});
						}
					}
					
					for(var i=cont; i<=3; i++){
						$agrega_fila_grid($grid_registros, 0, "", "0.00", "");
					}
					
					$busca_contacto.click(function(event){
						event.preventDefault();
						$busca_contactos($contacto.val(),$select_segmento,$select_mercado,entry['Segmentos'],entry['Mercados']);
					});
					
					//Asignar evento keypress al campo Nombre del Contacto
					$(this).aplicarEventoKeypressEjecutaTrigger($contacto, $busca_contacto);
				},"json");//termina llamada json
				
				
				$monto.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				$aplicar_evento_focus($monto);
				
				$monto.blur(function(){
					if($(this).val().trim()==''){
						$(this).val(0);
					}
					
					$(this).val(parseFloat($(this).val()).toFixed(2));
				});
				
				$kilogramos.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				$aplicar_evento_focus($kilogramos);
				
				$kilogramos.blur(function(){
					if($(this).val().trim()==''){
						$(this).val(0);
					}
					
					$(this).val(parseFloat($(this).val()).toFixed(2));
				});
				
				$busca_proveedor.click(function(event){
					event.preventDefault();
					$busca_proveedores('', $proveedor.val().trim())
				});
				
				//Asignar evento keypress al campo Proveedor
				$(this).aplicarEventoKeypressEjecutaTrigger($proveedor, $busca_proveedor);
				
				$fecha_inicio.click(function (s){
					var a=$('div.datepicker');
					a.css({'z-index':100});
				});
					
				$fecha_inicio.DatePicker({
					format:'Y-m-d',
					date: $fecha_inicio.val(),
					current: $fecha_inicio.val(),
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
						$fecha_inicio.val(formated);
						if (formated.match(patron) ){
							var valida_fecha=mayor($fecha_inicio.val(),mostrarFecha());
							
							if (valida_fecha==true){
								jAlert("Fecha no valida",'! Atencion');
								$fecha_inicio.val(mostrarFecha());
							}else{
								$fecha_inicio.DatePickerHide();	
							}
						}
					}
				});
				
				
				
				$fecha_fin.click(function (s){
					var a=$('div.datepicker');
					a.css({'z-index':100});
				});
				
				$fecha_fin.DatePicker({
					format:'Y-m-d',
					date: $fecha_fin.val(),
					current: $fecha_fin.val(),
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
						$fecha_fin.val(formated);
						if (formated.match(patron) ){
							var valida_fecha=mayor($fecha_fin.val(),mostrarFecha());
							
							if (valida_fecha==true){
								$fecha_fin.DatePickerHide();	
							}else{
								jAlert("Fecha no valida, debe ser mayor a la actual.",'! Atencion');
								$fecha_fin.val(mostrarFecha());
							}
						}
					}
				});

				
				$submit_actualizar.bind('click',function(){
					if($contacto.val().trim()==''){
						$id_contacto.val(0);
					}
					
					if($proveedor.val().trim()==''){
						$id_prov.val(0);
					}
				});
						
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-crmregistroproyectos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-crmregistroproyectos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllRegistros.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        var $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllRegistros.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
