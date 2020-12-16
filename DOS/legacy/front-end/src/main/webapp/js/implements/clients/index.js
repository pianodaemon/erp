$(function() {
	
    //arreglo para select dias revision y dias pago
    var array_dias_semana = {
				1:"Domingo", 
				2:"Lunes", 
				3:"Martes",
				4:"Miercoles",
				5:"Jueves",
				6:"Viernes",
				7:"Sabado"
			};
			
    //arreglo para select listas de precio
    var array_listas_precio = {
				1:"Lista 1", 
				2:"Lista 2", 
				3:"Lista 3",
				4:"Lista 4",
				5:"Lista 5",
				6:"Lista 6",
				7:"Lista 7",
				8:"Lista 8",
				9:"Lista 9",
				10:"Lista 10"
			};
	var rolVendedor=0;
	
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
	var controller = $contextpath.val()+"/controllers/clients";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_cliente = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Clientes');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
	var $cadena_busqueda = "";
	var $busqueda_nocontrol = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_nocontrol]');
	var $busqueda_razon_social = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_razon_social]');
	var $busqueda_rfc = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_rfc]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "nocontrol" + signo_separador + $busqueda_nocontrol.val() + "|";
		valor_retorno += "razonsoc" + signo_separador + $busqueda_razon_social.val() + "|";
		valor_retorno += "rfc" + signo_separador + $busqueda_rfc.val() + "|";
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
		$busqueda_nocontrol.val('');
		$busqueda_razon_social.val('');
		$busqueda_rfc.val('');
		$busqueda_nocontrol.focus();
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
		$busqueda_nocontrol.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_nocontrol, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_razon_social, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_rfc, $buscar);
	
	
	var input_json_inicio = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInicio.json';
	$arregloI = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_inicio,$arregloI,function(data){
		rolVendedor=data['Extra'][0]['rol_agente_venta'];
		/*
		if(parseInt(rolVendedor)<=0){
			//SI no es Administrador ocultar boton Actualizar
			$new_cliente.hide();
		}
		*/
	});
	
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-clients-window').find('#submit').mouseover(function(){
			$('#forma-clients-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-clients-window').find('#submit').mouseout(function(){
			$('#forma-clients-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-clients-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-clients-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-clients-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-clients-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-clients-window').find('#close').mouseover(function(){
			$('#forma-clients-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-clients-window').find('#close').mouseout(function(){
			$('#forma-clients-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-clients-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-clients-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-clients-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-clients-window').find("ul.pestanas li").click(function() {
			$('#forma-clients-window').find(".contenidoPes").hide();
			$('#forma-clients-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-clients-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			
			if(activeTab == '#tabx-1'){
				if($('#forma-clients-window').find('input[name=consignacion]').is(':checked')){
					$('#forma-clients-window').find('#div_consignacion_grid').css({'display':'block'});
					$('#forma-clients-window').find('.clients_div_one').css({'height':'565px'});
					$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
					$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
					$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
					$('#forma-clients-window').find('#cierra').css({'width':'765px'});
					$('#forma-clients-window').find('#botones').css({'width':'790px'});
				}else{
					$('#forma-clients-window').find('#div_consignacion_grid').css({'display':'none'});
					$('#forma-clients-window').find('.clients_div_one').css({'height':'470px'});
					$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
					$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
					$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
					$('#forma-clients-window').find('#cierra').css({'width':'765px'});
					$('#forma-clients-window').find('#botones').css({'width':'790px'});
				}
				
			}
			if(activeTab == '#tabx-2'){
				$('#forma-clients-window').find('.clients_div_one').css({'height':'290px'});
				$('#forma-clients-window').find('.clients_div_three').css({'height':'270px'});
				$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
				$('#forma-clients-window').find('#cierra').css({'width':'765px'});
				$('#forma-clients-window').find('#botones').css({'width':'790px'});
			}
			if(activeTab == '#tabx-3'){
				$('#forma-clients-window').find('.clients_div_one').css({'height':'240px'});
				$('#forma-clients-window').find('.clients_div_three').css({'height':'220px'});
				$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
				$('#forma-clients-window').find('#cierra').css({'width':'765px'});
				$('#forma-clients-window').find('#botones').css({'width':'790px'});
			}
			if(activeTab == '#tabx-4'){
				$('#forma-clients-window').find('.clients_div_one').css({'height':'330px'});
				$('#forma-clients-window').find('.clients_div_three').css({'height':'310px'});
				$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
				$('#forma-clients-window').find('#cierra').css({'width':'765px'});
				$('#forma-clients-window').find('#botones').css({'width':'790px'});
			}
			if(activeTab == '#tabx-5'){
				$('#forma-clients-window').find('.clients_div_one').css({'height':'330px'});
				$('#forma-clients-window').find('.clients_div_three').css({'height':'310px'});
				$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
				$('#forma-clients-window').find('#cierra').css({'width':'765px'});
				$('#forma-clients-window').find('#botones').css({'width':'790px'});
			}
			if(activeTab == '#tabx-6'){
				$('#forma-clients-window').find('.clients_div_one').css({'height':'250px'});
				$('#forma-clients-window').find('.clients_div_three').css({'height':'230px'});
				$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
				$('#forma-clients-window').find('#cierra').css({'width':'765px'});
				$('#forma-clients-window').find('#botones').css({'width':'790px'});
			}
			return false;
		});

	}
	
	
	$tabs_li_funxionalidad_dirconsignacion = function(){
		$('#forma-dirconsignacion-window').find('#boton_actualizar_forma_consignacion').mouseover(function(){
			$('#forma-dirconsignacion-window').find('#boton_actualizar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-dirconsignacion-window').find('#boton_actualizar_forma_consignacion').mouseout(function(){
			$('#forma-dirconsignacion-window').find('#boton_actualizar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		
		
		$('#forma-dirconsignacion-window').find('#boton_cancelar_forma_consignacion').mouseover(function(){
			$('#forma-dirconsignacion-window').find('#boton_cancelar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-dirconsignacion-window').find('#boton_cancelar_forma_consignacion').mouseout(function(){
			$('#forma-dirconsignacion-window').find('#boton_cancelar_forma_consignacion').css({ backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-dirconsignacion-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-dirconsignacion-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-dirconsignacion-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-dirconsignacion-window').find("ul.pestanas li").click(function() {
			$('#forma-dirconsignacion-window').find(".contenidoPes").hide();
			$('#forma-dirconsignacion-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-dirconsignacion-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	//carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, arreglo_elementos, elemento_seleccionado, texto_elemento_cero){
		$campo_select.children().remove();
		var select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
			}
		}
		$campo_select.append(select_html);
	}
	
	
      
        
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
		$campo.val("0");
	}
	
	$accion_focus = function($campo){
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo.focus(function(e){
			$valor_tmp = $(this).val().split(",").join("");
			
			if( ($valor_tmp != '') && ($valor_tmp != ' ') && ($valor_tmp != null) ){
				if(parseFloat($valor_tmp)<1){
					$campo.val('');
				}
			}
		});
	}
        
	$accio_blur = function($campo){
		//recalcula importe al perder enfoque el campo costo
		$campo.blur(function(){
			$valor_tmp = $(this).val().split(",").join("");
			
			if ($valor_tmp == ''  || $valor_tmp == null){
					$(this).val('0');
			}
			
			if( ($valor_tmp != '') && ($valor_tmp != ' ') ){
				$campo.val(parseFloat($valor_tmp));
			}else{
				$(this).val('0');
			}
			
		});
	}
	
	//agrega tr a la tabla
	$agrega_tr_grid_dirconsignacion = function(numFila,$select_pais,$select_entidad,$select_localidad,$campo_calle,$campo_numero,$campo_colonia,$campo_cp,$campo_telefono,$textarea_localternativa,$campo_fax){
		var $tabla_datos_consignacion = $('#forma-clients-window').find('#tabla_datos_consignacion');
		if(parseInt(numFila) != 0){
			//etra aqui para editar una direccion
			var cont=1;
			$tabla_datos_consignacion.find('tr').each(function (index){
				//alert('Fila: '+$(this).find('#fil').val()+ '  Contador: '+cont +'  Numfila: '+numFila);
				if( parseInt(cont) == parseInt(numFila) ){
					//actualiza los datos de la direccion
					$tabla_datos_consignacion.find('input[name=idpais]').eq(parseInt(cont) - 1).val($select_pais.val());
					$tabla_datos_consignacion.find('input[name=pais]').eq(parseInt(cont) - 1).val($('select[name=pais_consignacion] option:selected').html());
					$tabla_datos_consignacion.find('input[name=identidad]').eq(parseInt(cont) - 1).val($select_entidad.val());
					$tabla_datos_consignacion.find('input[name=entidad]').eq(parseInt(cont) - 1).val($('select[name=entidad_consignacion] option:selected').html());
					$tabla_datos_consignacion.find('input[name=idlocalidad]').eq(parseInt(cont) - 1).val($select_localidad.val());
					$tabla_datos_consignacion.find('input[name=localidad]').eq(parseInt(cont) - 1).val($('select[name=localidad_consignacion] option:selected').html());
					$tabla_datos_consignacion.find('input[name=calle]').eq(parseInt(cont) - 1).val($campo_calle.val());
					$tabla_datos_consignacion.find('input[name=numero]').eq(parseInt(cont) - 1).val($campo_numero.val());
					$tabla_datos_consignacion.find('input[name=colonia]').eq(parseInt(cont) - 1).val($campo_colonia.val());
					$tabla_datos_consignacion.find('input[name=codigop]').eq(parseInt(cont) - 1).val($campo_cp.val());
					$tabla_datos_consignacion.find('input[name=telefono]').eq(parseInt(cont) - 1).val($campo_telefono.val());
					$tabla_datos_consignacion.find('input[name=localternativa]').eq(parseInt(cont) - 1).val($textarea_localternativa.val());
					$tabla_datos_consignacion.find('input[name=numfax]').eq(parseInt(cont) - 1).val($campo_fax.val());
					$tabla_datos_consignacion.find('#direccion'+ cont).text($campo_calle.val()+' No.'+$campo_numero.val()+', Col. '+$campo_colonia.val()+', '+$('select[name=localidad_consignacion] option:selected').html()+', '+$('select[name=entidad_consignacion] option:selected').html()+', '+$('select[name=pais_consignacion] option:selected').html()+' C.P. '+$campo_cp.val());
				}
				cont++;
			});
			
		}else{
			//si es nueva direccion entra aqui y crea  una nueva fila en el grid
			var trCount = $("tr", $('#forma-clients-window').find('#tabla_datos_consignacion')).size();
			trCount++;
                        
			var tr='';
			tr = '<tr>';
				tr += '<td width="55">';
					tr += '<a href="elimina">Eliminar</a>';
					tr += '<input type="hidden" name="dc_eliminado" id="elim" value="1">';
				tr += '</td>';
				tr += '<td width="645" class="dir" id="dir'+ trCount +'">';
					tr += '<input type="hidden" name="fila'+ trCount +'"             id="fil" 	value="'+trCount+'">';
					tr += '<input type="hidden" name="dc_calle"         id="cal" 	value="'+ $campo_calle.val() +'">';
					tr += '<input type="hidden" name="dc_numero"        id="num" 	value="'+ $campo_numero.val() +'">';
					tr += '<input type="hidden" name="dc_colonia"       id="col" 	value="'+ $campo_colonia.val() +'">';
					tr += '<input type="hidden" name="dc_idlocalidad"   id="idloc" 	value="'+ $select_localidad.val() +'">';
					tr += '<input type="hidden" name="localidad'+ trCount +'"        id="loc" 	value="'+ $('select[name=localidad_consignacion] option:selected').html() +'">';
					tr += '<input type="hidden" name="dc_identidad"     id="ident" 	value="'+ $select_entidad.val() +'">';
					tr += '<input type="hidden" name="entidad'+ trCount +'"          id="ent" 	value="'+ $('select[name=entidad_consignacion] option:selected').html() +'">';
					tr += '<input type="hidden" name="dc_idpais"        id="idpa" 	value="'+ $select_pais.val() +'">';
					tr += '<input type="hidden" name="pais'+ trCount +'"             id="pa" 	value="'+ $('select[name=pais_consignacion] option:selected').html() +'">';
					tr += '<input type="hidden" name="dc_codigop"       id="cp" 	value="'+ $campo_cp.val() +'">';
					tr += '<input type="hidden" name="dc_telefono"      id="tel" 	value="'+ $campo_telefono.val() +'">';
					tr += '<input type="hidden" name="dc_localternativa" id="localt"   value="'+ $textarea_localternativa.val() +'">';
					tr += '<input type="hidden" name="dc_numfax"        id="fax" 	value="'+ $campo_fax.val() +'">';
					tr += '<span id="direccion'+ trCount +'">'+$campo_calle.val()+' No.'+$campo_numero.val()+', Col. '+$campo_colonia.val()+', '+$('select[name=localidad_consignacion] option:selected').html()+', '+$('select[name=entidad_consignacion] option:selected').html()+', '+$('select[name=pais_consignacion] option:selected').html()+' C.P. '+$campo_cp.val()+'</span>';
				tr += '</td>';
			tr += '</tr>';
                        
			//crea la nueva fila
			$tabla_datos_consignacion.append(tr);
		}
		
		$tabla_datos_consignacion.find('tr:odd').css({ 'background-color' : '#e7e8ea'});
		$tabla_datos_consignacion.find('tr:even').css({ 'background-color' : '#FFFFFF'});
		
		$('tr:odd' , $tabla_datos_consignacion).hover(function () {
			$(this).find('td').css({ background : '#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#e7e8ea'});
		});
		$('tr:even' , $tabla_datos_consignacion).hover(function () {
			$(this).find('td').css({'background-color':'#FBD850'});
		}, function() {
			$(this).find('td').css({'background-color':'#FFFFFF'});
		});
		
		$tabla_datos_consignacion.find('a').bind('click',function(event){
			event.preventDefault();
			$(this).parent().parent().find('#elim').val(0);
			$(this).parent().parent().hide();
		});
		
		//seleccionar una direccion para editar
		$tabla_datos_consignacion.find('tr').each(function (index){
			$(this).find(".dir").click(function(event){
				var numFila = $(this).find('#fil').val();
				var pais = $(this).find('#idpa').val();
				var entidad = $(this).find('#ident').val();
				var localidad = $(this).find('#idloc').val();
				var calle = $(this).find('#cal').val();
				var numero = $(this).find('#num').val();
				var colonia = $(this).find('#col').val();
				var cp = $(this).find('#cp').val();
				var telefono = $(this).find('#tel').val();
				var localternativa = $(this).find('#localt').val();
				var fax = $(this).find('#fax').val();
				$forma_direccion_consignacion(numFila,pais,entidad,localidad,calle,numero,colonia,cp,telefono,localternativa,fax);
			});
		});
	}//termina funcion que agrega tr a la tabla
	
	
	
	//ventana para capturar direccion de consignacion
	$forma_direccion_consignacion = function(numFila,pais,entidad,localidad,calle,numero,colonia,cp,telefono,localternativa,fax){
		$('#forma-dirconsignacion-window').remove();
		$('#forma-dirconsignacion-overlay').remove();
		$(this).modalPanel_Dirconsignacion();
		var $dialogoc =  $('#forma-dirconsignacion-window');
		$dialogoc.append($('div.direccion_consignacion').find('table.formaDirconsignacion').clone());
		
		$('#forma-dirconsignacion-window').css({ "margin-left": -190, 	"margin-top": -200  });
		$tabs_li_funxionalidad_dirconsignacion();
		
		var $select_pais = $('#forma-dirconsignacion-window').find('select[name=pais_consignacion]');
		var $select_entidad = $('#forma-dirconsignacion-window').find('select[name=entidad_consignacion]');
		var $select_localidad = $('#forma-dirconsignacion-window').find('select[name=localidad_consignacion]');
		var $campo_calle = $('#forma-dirconsignacion-window').find('input[name=calle_consignacion]');
		var $campo_numero = $('#forma-dirconsignacion-window').find('input[name=numero_consignacion]');
		var $campo_colonia = $('#forma-dirconsignacion-window').find('input[name=colonia_consignacion]');
		var $campo_cp = $('#forma-dirconsignacion-window').find('input[name=cp_consignacion]');
		var $campo_telefono = $('#forma-dirconsignacion-window').find('input[name=telefono_consignacion]');
		var $textarea_localternativa = $('#forma-dirconsignacion-window').find('textarea[name=localternativa_consignacion]');
		var $campo_fax = $('#forma-dirconsignacion-window').find('input[name=fax_consignacion]');
		
		var $cierra_forma_consignacion = $('#forma-dirconsignacion-window').find('#cierra_forma_consignacion');
		var $boton_cancelar_forma_consignacion = $('#forma-dirconsignacion-window').find('#boton_cancelar_forma_consignacion');
		var $boton_actualizar_forma_consignacion = $('#forma-dirconsignacion-window').find('#boton_actualizar_forma_consignacion');
		
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_datos_forma_consignacion.json';
		$arreglo = {"numFila":numFila,'id_pais':pais, 'id_entidad': entidad  };

		$.post(input_json,$arreglo,function(entry){
			
		
			if(parseInt(numFila)==0){
				//Alimentando los campos select de las pais
				$select_pais.children().remove();
				var pais_hmtl = '<option value="0" selected="yes">[--Seleccione un pais--]</option>';
				$.each(entry['Paises'],function(entryIndex,pa){
					pais_hmtl += '<option value="' + pa['cve_pais'] + '"  >' + pa['pais_ent'] + '</option>';
				});
				$select_pais.append(pais_hmtl);
					
				var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar entidad--]</option>';
				$select_entidad.children().remove();
				$select_entidad.append(entidad_hmtl);

				var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar municipio-]</option>';
				$select_localidad.children().remove();
				$select_localidad.append(localidad_hmtl);
					
				//carga select estados al cambiar el pais
				$select_pais.change(function(){
					var valor_pais = $(this).val();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
					$arreglo = {'id_pais':valor_pais};
					$.post(input_json,$arreglo,function(entry){
						$select_entidad.children().remove();
						var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
						$.each(entry['Entidades'],function(entryIndex,ent){
							entidad_hmtl += '<option value="' + ent['cve_ent'] + '"  >' + ent['nom_ent'] + '</option>';
						});
						$select_entidad.append(entidad_hmtl);

						var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
						$select_localidad.children().remove();
						$select_localidad.append(trama_hmtl_localidades);

					},"json");//termina llamada json
				});
				
				//carga select municipios al cambiar el estado
				$select_entidad.change(function(){
					var valor_entidad = $(this).val();
					var valor_pais = $select_pais.val();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
					$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
					$.post(input_json,$arreglo,function(entry){
						$select_localidad.children().remove();
						var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
						$.each(entry['Localidades'],function(entryIndex,mun){
							trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						});
						$select_localidad.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
                                
				
			}else{
				//aqui entra para editar una direccion
				//carga los campos con datos actuales
				//Alimentando los campos select de las pais
				$select_pais.children().remove();
				var pais_hmtl = '';
				$.each(entry['Paises'],function(entryIndex,pa){
					if(pa['cve_pais'] == pais){
						pais_hmtl += '<option value="' + pa['cve_pais'] + '"  selected="yes">' + pa['pais_ent'] + '</option>';
					}else{
						pais_hmtl += '<option value="' + pa['cve_pais'] + '"  >' + pa['pais_ent'] + '</option>';
					}
				});
				$select_pais.append(pais_hmtl);
				

				//Alimentando los campos select del estado
				$select_entidad.children().remove();
				var entidad_hmtl = ''
				$.each(entry['Entidades'],function(entryIndex,ent){
					if(ent['cve_ent'] == entidad){
						entidad_hmtl += '<option value="' + ent['cve_ent'] + '"  selected="yes">' + ent['nom_ent'] + '</option>';
					}else{
						entidad_hmtl += '<option value="' + ent['cve_ent'] + '"  >' + ent['nom_ent'] + '</option>';
					}
				});
				$select_entidad.append(entidad_hmtl);
							
					
					
				//Alimentando los campos select de los municipios
				$select_localidad.children().remove();
				var localidad_hmtl = "";
				$.each(entry['Localidades'],function(entryIndex,mun){
					if(mun['cve_mun'] == localidad){
						localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
					}else{
						localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					}
				});
				$select_localidad.append(localidad_hmtl);
					
					
	
				$campo_calle.val(calle);
				$campo_numero.val(numero);
				$campo_colonia.val(colonia);
				$campo_cp.val(cp);
				$campo_telefono.val(telefono);
				
				if(localternativa == " "){
					$textarea_localternativa.val("");
				}else{
					$textarea_localternativa.val(localternativa);
				}
				
				//si trae espacio en blanco se le elimina
				if(fax == " "){
					$campo_fax.val("");
				}else{
					$campo_fax.val(fax);
				}
				
				//carga select estados al cambiar el pais
				$select_pais.change(function(){
					var valor_pais = $(this).val();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
					$arreglo = {'id_pais':valor_pais};
					$.post(input_json,$arreglo,function(entry){
						$select_entidad.children().remove();
						var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
						$.each(entry['Entidades'],function(entryIndex,ent){
							entidad_hmtl += '<option value="' + ent['cve_ent'] + '"  >' + ent['nom_ent'] + '</option>';
						});
						$select_entidad.append(entidad_hmtl);

						var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
						$select_localidad.children().remove();
						$select_localidad.append(trama_hmtl_localidades);

					},"json");//termina llamada json
				});
				
				//carga select municipios al cambiar el estado
				$select_entidad.change(function(){
					var valor_entidad = $(this).val();
					var valor_pais = $select_pais.val();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
					$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
					$.post(input_json,$arreglo,function(entry){
						$select_localidad.children().remove();
						var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
						$.each(entry['Localidades'],function(entryIndex,mun){
							trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						});
						$select_localidad.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});





			}
		},"json");
		/*
		$campo_numero.keypress(function(e){
			//alert(e.which);
			if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});
		*/
		
		$campo_cp.keypress(function(e){
			//alert(e.which);
			if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});
		
		$campo_telefono.keypress(function(e){
			//alert(e.which);
			if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});
		
		$campo_fax.keypress(function(e){
			//alert(e.which);
			if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});
		
		
		$boton_actualizar_forma_consignacion.bind('click',function(){
			//llamada a la funcion que valida los campos
			var dir_loc_alt;
			var dir_fax;
			
			if($textarea_localternativa.val() == ""){
				dir_loc_alt =" ";
			}else{
				dir_loc_alt = $textarea_localternativa.val();
			}
			
			if($campo_fax.val() == ""){
				dir_fax=" ";
			}else{
				dir_fax = $campo_fax.val();
			}
			
			var confirma = '';
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/valida_direcciones_consignacion.json';
			$arreglo = {
				'pais':$select_pais.val(),
				'entidad':$select_entidad.val(),
				'localidad':$select_localidad.val(),
				'calle':$campo_calle.val(),
				'numero':$campo_numero.val(),
				'colonia':$campo_colonia.val(),
				'cp':$campo_cp.val(),
				'telefono':$campo_telefono.val(),
				'localternativa':dir_loc_alt,
				'fax':dir_fax
			};
			
			$.post(input_json,$arreglo,function(entry){
				//confirma = entry['success']['fn_validaciones_dir_consignacion_cliente'];
				confirma = entry['success'];
				if ( confirma == "true" ){
					//llamada a la funcion que agrega tr al grid de direcciones de consignacion
					$agrega_tr_grid_dirconsignacion(numFila,$select_pais,$select_entidad,$select_localidad,$campo_calle,$campo_numero,$campo_colonia,$campo_cp,$campo_telefono,$textarea_localternativa,$campo_fax);

					var remove = function() { $(this).remove(); };
					$('#forma-dirconsignacion-overlay').fadeOut(remove);
				}else{
					// Desaparece todas las interrogaciones si es que existen
					$('#forma-dirconsignacion-window').find('div.interrogacion').css({'display':'none'});
					var valor = confirma.split('___');
					//muestra las interrogaciones
					for (var element in valor){
						tmp = confirma.split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
							$('#forma-dirconsignacion-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
							.parent()
							.css({'display':'block'})
							.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
						}
					}
				}
			},"json");
		});
		
		$boton_cancelar_forma_consignacion.click(function(event){
			event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-dirconsignacion-overlay').fadeOut(remove);
		});
		
		$cierra_forma_consignacion.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-dirconsignacion-overlay').fadeOut(remove);
		});
		
	}//termina forma dirconsignacion
	
	
	
	
	
	solo_numeros = function($campo_input){
		//validar campo cantidad, solo acepte numeros y punto
		$campo_input.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB
			if (e.which == 8 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}		
		});
	}
	
	
	
	//buscador de Cuentas Contables
	$busca_cuentas_contables = function(tipo, nivel_cta, arrayCtasMayor){
		//limpiar_campos_grids();
		$(this).modalPanel_buscactacontable();
		var $dialogoc =  $('#forma-buscactacontable-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_cuentas').find('table.formaBusqueda_cuentas').clone());
		
		$('#forma-buscactacontable-window').css({"margin-left": -200, 	"margin-top": -160});
		
		var $tabla_resultados = $('#forma-buscactacontable-window').find('#tabla_resultado');
		
		var $select_cta_mayor = $('#forma-buscactacontable-window').find('select[name=select_cta_mayor]');
		var $campo_clasif = $('#forma-buscactacontable-window').find('input[name=clasif]');
		var $campo_cuenta = $('#forma-buscactacontable-window').find('input[name=cuenta]');
		var $campo_scuenta = $('#forma-buscactacontable-window').find('input[name=scuenta]');
		var $campo_sscuenta = $('#forma-buscactacontable-window').find('input[name=sscuenta]');
		var $campo_ssscuenta = $('#forma-buscactacontable-window').find('input[name=ssscuenta]');
		var $campo_sssscuenta = $('#forma-buscactacontable-window').find('input[name=sssscuenta]');
		var $campo_descripcion = $('#forma-buscactacontable-window').find('input[name=campo_descripcion]');
		
		var $boton_busca = $('#forma-buscactacontable-window').find('#boton_busca');
		var $boton_cencela = $('#forma-buscactacontable-window').find('#boton_cencela');
		var mayor_seleccionado=0;
		var detalle=0;
		
		$campo_cuenta.hide();
		$campo_scuenta.hide();
		$campo_sscuenta.hide();
		$campo_ssscuenta.hide();
		$campo_sssscuenta.hide();
		
		solo_numeros($campo_clasif);
		solo_numeros($campo_cuenta);
		solo_numeros($campo_scuenta);
		solo_numeros($campo_sscuenta);
		solo_numeros($campo_ssscuenta);
		solo_numeros($campo_sssscuenta);
		
		//funcionalidad botones
		$boton_busca.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		
		$boton_busca.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$boton_cencela.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$boton_cencela.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		if(parseInt(nivel_cta) >=1 ){ $campo_cuenta.show(); };
		if(parseInt(nivel_cta) >=2 ){ $campo_scuenta.show(); };
		if(parseInt(nivel_cta) >=3 ){ $campo_sscuenta.show(); };
		if(parseInt(nivel_cta) >=4 ){ $campo_ssscuenta.show(); };
		if(parseInt(nivel_cta) >=5 ){ $campo_sssscuenta.show(); };
		
		//Activo
		if(parseInt(tipo)==1 ){mayor_seleccionado=1; detalle=1; };
		//Ingreso
		if(parseInt(tipo)==2 ){mayor_seleccionado=4; detalle=1; };
		//IETU
		if(parseInt(tipo)==3 ){mayor_seleccionado=7; detalle=1; };
		//Cuenta Complementaria
		if(parseInt(tipo)==4 ){mayor_seleccionado=1; detalle=1; };
		//Cuenta Activo Complementaria
		if(parseInt(tipo)==5 ){mayor_seleccionado=1; detalle=1; };
		
		//carga select de cuentas de Mayor
		$select_cta_mayor.children().remove();
		var ctamay_hmtl = '';
		$.each(arrayCtasMayor,function(entryIndex,ctamay){
			if (parseInt(mayor_seleccionado) == parseInt( ctamay['id']) ){
				ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['titulo'] + '</option>';
			}
		});
		$select_cta_mayor.append(ctamay_hmtl);
		
		
		//click buscar Cuentas Contables
		$boton_busca.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorCuentasContables.json';
			$arreglo = {	'cta_mayor':$select_cta_mayor.val(),
							'detalle':detalle,
							'clasifica':$campo_clasif.val(),
							'cta':$campo_cuenta.val(),
							'scta':$campo_scuenta.val(),
							'sscta':$campo_sscuenta.val(),
							'ssscta':$campo_ssscuenta.val(),
							'sssscta':$campo_sssscuenta.val(),
							'descripcion':$campo_descripcion.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				var notr=0;
				$.each(entry['CtaContables'],function(entryIndex,cuenta){
					//obtiene numero de trs
					notr = $("tr", $tabla_resultados).size();
					notr++;
					
					trr = '<tr class="tr'+notr+'">';
						trr += '<td width="30">'+cuenta['m']+'</td>';
						trr += '<td width="30">'+cuenta['c']+'</td>';
						trr += '<td width="170">';
							trr += '<input type="hidden" name="id_cta" value="'+cuenta['id']+'" >';
							trr += '<input type="text" name="cta" value="'+cuenta['cuenta']+'" class="borde_oculto" style="width:166px; readOnly="true">';
							trr += '<input type="hidden" name="campo_cta" value="'+cuenta['cta']+'" >';
							trr += '<input type="hidden" name="campo_scta" value="'+cuenta['subcta']+'" >';
							trr += '<input type="hidden" name="campo_sscta" value="'+cuenta['ssubcta']+'" >';
							trr += '<input type="hidden" name="campo_ssscta" value="'+cuenta['sssubcta']+'" >';
							trr += '<input type="hidden" name="campo_ssscta" value="'+cuenta['ssssubcta']+'" >';
						trr += '</td>';
						trr += '<td width="230"><input type="text" name="des" value="'+cuenta['descripcion']+'" class="borde_oculto" style="width:226px; readOnly="true"></td>';
						trr += '<td width="70">'+cuenta['detalle']+'</td>';
						trr += '<td width="50">'+cuenta['nivel_cta']+'</td>';
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
					var id_cta = $(this).find('input[name=id_cta]').val();
					var cta = $(this).find('input[name=campo_cta]').val();
					var scta = $(this).find('input[name=campo_scta]').val();
					var sscta = $(this).find('input[name=campo_sscta]').val();
					var ssscta = $(this).find('input[name=campo_ssscta]').val();
					var sssscta = $(this).find('input[name=campo_ssscta]').val();
					var desc = $(this).find('input[name=des]').val();
					
					if(parseInt(tipo)==1 ){ 
						$('#forma-clients-window').find('input[name=id_cta_activo]').val(id_cta);
						$('#forma-clients-window').find('input[name=ac_cuenta]').val(cta);
						$('#forma-clients-window').find('input[name=ac_scuenta]').val(scta);
						$('#forma-clients-window').find('input[name=ac_sscuenta]').val(sscta);
						$('#forma-clients-window').find('input[name=ac_ssscuenta]').val(ssscta);
						$('#forma-clients-window').find('input[name=ac_sssscuenta]').val(sssscta);
						$('#forma-clients-window').find('input[name=descripcion_ac]').val(desc);
					};
					
					if(parseInt(tipo)==2 ){ 
						$('#forma-clients-window').find('input[name=id_cta_ingreso]').val(id_cta);
						$('#forma-clients-window').find('input[name=ing_cuenta]').val(cta);
						$('#forma-clients-window').find('input[name=ing_scuenta]').val(scta);
						$('#forma-clients-window').find('input[name=ing_sscuenta]').val(sscta);
						$('#forma-clients-window').find('input[name=ing_ssscuenta]').val(ssscta);
						$('#forma-clients-window').find('input[name=ing_sssscuenta]').val(sssscta);
						$('#forma-clients-window').find('input[name=descripcion_ing]').val(desc);
					};
					
					if(parseInt(tipo)==3 ){ 
						$('#forma-clients-window').find('input[name=id_cta_ietu]').val(id_cta);
						$('#forma-clients-window').find('input[name=ietu_cuenta]').val(cta);
						$('#forma-clients-window').find('input[name=ietu_scuenta]').val(scta);
						$('#forma-clients-window').find('input[name=ietu_sscuenta]').val(sscta);
						$('#forma-clients-window').find('input[name=ietu_ssscuenta]').val(ssscta);
						$('#forma-clients-window').find('input[name=ietu_sssscuenta]').val(sssscta);
						$('#forma-clients-window').find('input[name=descripcion_ietu]').val(desc);
					};
					
					if(parseInt(tipo)==4 ){ 
						$('#forma-clients-window').find('input[name=id_cta_complementaria]').val(id_cta);
						$('#forma-clients-window').find('input[name=com_cuenta]').val(cta);
						$('#forma-clients-window').find('input[name=com_scuenta]').val(scta);
						$('#forma-clients-window').find('input[name=com_sscuenta]').val(sscta);
						$('#forma-clients-window').find('input[name=com_ssscuenta]').val(ssscta);
						$('#forma-clients-window').find('input[name=com_sssscuenta]').val(sssscta);
						$('#forma-clients-window').find('input[name=descripcion_com]').val(desc);
					};
					
					if(parseInt(tipo)==5 ){ 
						$('#forma-clients-window').find('input[name=id_cta_activo_complementaria]').val(id_cta);
						$('#forma-clients-window').find('input[name=ac_com_cuenta]').val(cta);
						$('#forma-clients-window').find('input[name=ac_com_scuenta]').val(scta);
						$('#forma-clients-window').find('input[name=ac_com_sscuenta]').val(sscta);
						$('#forma-clients-window').find('input[name=ac_com_ssscuenta]').val(ssscta);
						$('#forma-clients-window').find('input[name=ac_com_sssscuenta]').val(sssscta);
						$('#forma-clients-window').find('input[name=descripcion_ac_com]').val(desc);
					};
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscactacontable-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-pocpedidos-window').find('input[name=sku_producto]').focus();
				});
				
			});//termina llamada json
		});
		
		
		$campo_clasif.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_cuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_scuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_sscuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_ssscuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_sssscuenta.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$campo_descripcion.keypress(function(e){
			if(e.which == 13){
				$boton_busca.trigger('click');
				return false;
			}
		});
		
		$boton_cencela.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscactacontable-overlay').fadeOut(remove);
		});
		
		$campo_clasif.focus();
		
	}//termina buscador de Cuentas Contables
	
	
	
	
	//nuevo cliente
	$new_cliente.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel();
		
		//alert("si entra");
		//aqui entra nuevo
		var form_to_show = 'formaClients00';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		//alert("si entra");
		$('#forma-clients-window').css({ "margin-left": -400, 	"margin-top": -290 });
		$forma_selected.prependTo('#forma-clients-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		//alert("si pasa");
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_cliente.json';
		$arreglo = {'id':id_to_show,
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
                
		var $total_tr = $('#forma-clients-window').find('input[name=total_tr]');
		var $campo_consignacion = $('#forma-clients-window').find('input[name=campo_consignacion]');
		var $campo_id_cliente = $('#forma-clients-window').find('input[name=identificador_cliente]');
		var $campo_nocontrol = $('#forma-clients-window').find('input[name=nocontrol]');
		var $campo_rfc = $('#forma-clients-window').find('input[name=rfc]');
		var $campo_curp = $('#forma-clients-window').find('input[name=curp]');
		var $campo_razonsocial = $('#forma-clients-window').find('input[name=razonsocial]');
		var $clave_comercial = $('#forma-clients-window').find('input[name=clave_comercial]');
		var $campo_calle = $('#forma-clients-window').find('input[name=calle]');
		var $campo_numero = $('#forma-clients-window').find('input[name=numero_int]');
		var $campo_numero_ext = $('#forma-clients-window').find('input[name=numero_ext]');
		var $campo_entrecalles = $('#forma-clients-window').find('input[name=entrecalles]');
		var $campo_colonia = $('#forma-clients-window').find('input[name=colonia]');
		var $campo_cp = $('#forma-clients-window').find('input[name=cp]');
		var $select_pais = $('#forma-clients-window').find('select[name=pais]');
		var $select_entidad = $('#forma-clients-window').find('select[name=estado]');
		var $select_localidad = $('#forma-clients-window').find('select[name=municipio]');
		var $select_agente = $('#forma-clients-window').find('select[name=agente]');
		var $campo_loc_alternativa = $('#forma-clients-window').find('input[name=loc_alternativa]');
		var $campo_email = $('#forma-clients-window').find('input[name=email]');
		var $campo_tel1 = $('#forma-clients-window').find('input[name=tel1]');
		var $campo_ext1 = $('#forma-clients-window').find('input[name=ext1]');
		var $campo_fax = $('#forma-clients-window').find('input[name=fax]');
		var $campo_tel2 = $('#forma-clients-window').find('input[name=tel2]');
		var $campo_ext2 = $('#forma-clients-window').find('input[name=ext2]');
		var $campo_contacto = $('#forma-clients-window').find('input[name=contacto]');
		
		//datos
		var $select_zona = $('#forma-clients-window').find('select[name=zona]');
		var $select_grupo = $('#forma-clients-window').find('select[name=grupo]');
		var $select_tipocliente = $('#forma-clients-window').find('select[name=tipocliente]');
		var $select_clasif1 = $('#forma-clients-window').find('select[name=clasif1]');
		var $select_clasif2 = $('#forma-clients-window').find('select[name=clasif2]');
		var $select_clasif3 = $('#forma-clients-window').find('select[name=clasif3]');
		var $select_moneda = $('#forma-clients-window').find('select[name=moneda]');
		var $select_empresa_filial = $('#forma-clients-window').find('select[name=filial]');
		var $select_estatus = $('#forma-clients-window').find('select[name=estatus]');
		var $select_impuesto = $('#forma-clients-window').find('select[name=impuesto]');
		
		var $select_immex = $('#forma-clients-window').find('select[name=select_immex]');
		var $retencion_immex = $('#forma-clients-window').find('input[name=retencion_immex]');
		var $select_dia_revision = $('#forma-clients-window').find('select[name=select_dia_revision]');
		var $select_dia_pago = $('#forma-clients-window').find('select[name=select_dia_pago]');
		var $cuenta_mn = $('#forma-clients-window').find('input[name=cuenta_mn]');
		var $cuenta_usd = $('#forma-clients-window').find('input[name=cuenta_usd]');
		var $select_lista_precio = $('#forma-clients-window').find('select[name=select_lista_precio]');
		var $select_metodo_pago = $('#forma-clients-window').find('select[name=select_metodo_pago]');
		
		//credito
		var $campo_limite_credito = $('#forma-clients-window').find('input[name=limite_credito]');
		var $select_dias_credito = $('#forma-clients-window').find('select[name=dias_credito]');
		var $select_credito_suspendido = $('#forma-clients-window').find('select[name=credito_suspendido]');
		var $select_inicio_credito = $('#forma-clients-window').find('select[name=inicio_credito]');
		var $select_tipo_embarque = $('#forma-clients-window').find('select[name=tipo_embarque]');
		var $campo_caducidad_cotizacion = $('#forma-clients-window').find('input[name=cad_cotizacion]');
		var $txtarea_condiciones = $('#forma-clients-window').find('textarea[name=condiciones]');
		var $txtarea_observaciones = $('#forma-clients-window').find('textarea[name=observaciones]');
		
		//tab4 Contacto Compras
		var $campo_comp_contacto = $('#forma-clients-window').find('input[name=comp_contacto]');
		var $campo_comp_puesto = $('#forma-clients-window').find('input[name=comp_puesto]');
		var $campo_comp_calle = $('#forma-clients-window').find('input[name=comp_calle]');
		var $campo_comp_numcalle = $('#forma-clients-window').find('input[name=comp_numcalle]');
		var $campo_comp_colonia = $('#forma-clients-window').find('input[name=comp_colonia]');
		var $campo_comp_cp = $('#forma-clients-window').find('input[name=comp_cp]');
		var $campo_comp_entrecalles = $('#forma-clients-window').find('input[name=comp_entrecalles]');
		var $select_comp_pais = $('#forma-clients-window').find('select[name=comp_pais]');
		var $select_comp_estado = $('#forma-clients-window').find('select[name=comp_estado]');
		var $select_comp_localidad = $('#forma-clients-window').find('select[name=comp_municipio]');
		var $campo_comp_tel1 = $('#forma-clients-window').find('input[name=comp_tel1]');
		var $campo_comp_ext1 = $('#forma-clients-window').find('input[name=comp_ext1]');
		var $campo_comp_fax = $('#forma-clients-window').find('input[name=comp_fax]');
		var $campo_comp_tel2 = $('#forma-clients-window').find('input[name=comp_tel2]');
		var $campo_comp_ext2 = $('#forma-clients-window').find('input[name=comp_ext2]');
		var $campo_comp_email = $('#forma-clients-window').find('input[name=comp_email]');
		
		//tab5 Contacto Pagos
		var $campo_pag_contacto = $('#forma-clients-window').find('input[name=pag_contacto]');
		var $campo_pag_puesto = $('#forma-clients-window').find('input[name=pag_puesto]');
		var $campo_pag_calle = $('#forma-clients-window').find('input[name=pag_calle]');
		var $campo_pag_numcalle = $('#forma-clients-window').find('input[name=pag_numcalle]');
		var $campo_pag_colonia = $('#forma-clients-window').find('input[name=pag_colonia]');
		var $campo_pag_cp = $('#forma-clients-window').find('input[name=pag_cp]');
		var $campo_pag_entrecalles = $('#forma-clients-window').find('input[name=pag_entrecalles]');
		var $select_pag_pais = $('#forma-clients-window').find('select[name=pag_pais]');
		var $select_pag_estado = $('#forma-clients-window').find('select[name=pag_estado]');
		var $select_pag_localidad = $('#forma-clients-window').find('select[name=pag_municipio]');
		var $campo_pag_tel1 = $('#forma-clients-window').find('input[name=pag_tel1]');
		var $campo_pag_ext1 = $('#forma-clients-window').find('input[name=pag_ext1]');
		var $campo_pag_fax = $('#forma-clients-window').find('input[name=pag_fax]');
		var $campo_pag_tel2 = $('#forma-clients-window').find('input[name=pag_tel2]');
		var $campo_pag_ext2 = $('#forma-clients-window').find('input[name=pag_ext2]');
		var $campo_pag_email = $('#forma-clients-window').find('input[name=pag_email]');
		
		var $chkbox_consignacion = $('#forma-clients-window').find('input[name=consignacion]');
		var $agrega_direccion = $('#forma-clients-window').find('a[href*=agrega_direccion]');
		var $div_consignacion_grid = $('#forma-clients-window').find('#div_consignacion_grid');
		var $tabla_datos_consignacion = $('#forma-clients-window').find('#tabla_datos_consignacion');
		
		var $pestana_contabilidad = $('#forma-clients-window').find('ul.pestanas').find('a[href*=#tabx-6]');
		
		var $id_cta_activo = $('#forma-clients-window').find('input[name=id_cta_activo]');
		var $ac_cuenta = $('#forma-clients-window').find('input[name=ac_cuenta]');
		var $ac_scuenta = $('#forma-clients-window').find('input[name=ac_scuenta]');
		var $ac_sscuenta = $('#forma-clients-window').find('input[name=ac_sscuenta]');
		var $ac_ssscuenta = $('#forma-clients-window').find('input[name=ac_ssscuenta]');
		var $ac_sssscuenta = $('#forma-clients-window').find('input[name=ac_sssscuenta]');
		var $descripcion_ac = $('#forma-clients-window').find('input[name=descripcion_ac]');
		
		var $id_cta_ingreso = $('#forma-clients-window').find('input[name=id_cta_ingreso]');
		var $ing_cuenta = $('#forma-clients-window').find('input[name=ing_cuenta]');
		var $ing_scuenta = $('#forma-clients-window').find('input[name=ing_scuenta]');
		var $ing_sscuenta = $('#forma-clients-window').find('input[name=ing_sscuenta]');
		var $ing_ssscuenta = $('#forma-clients-window').find('input[name=ing_ssscuenta]');
		var $ing_sssscuenta = $('#forma-clients-window').find('input[name=ing_sssscuenta]');
		var $descripcion_ing = $('#forma-clients-window').find('input[name=descripcion_ing]');
		
		var $id_cta_ietu = $('#forma-clients-window').find('input[name=id_cta_ietu]');
		var $ietu_cuenta = $('#forma-clients-window').find('input[name=ietu_cuenta]');
		var $ietu_scuenta = $('#forma-clients-window').find('input[name=ietu_scuenta]');
		var $ietu_sscuenta = $('#forma-clients-window').find('input[name=ietu_sscuenta]');
		var $ietu_ssscuenta = $('#forma-clients-window').find('input[name=ietu_ssscuenta]');
		var $ietu_sssscuenta = $('#forma-clients-window').find('input[name=ietu_sssscuenta]');
		var $descripcion_ietu = $('#forma-clients-window').find('input[name=descripcion_ietu]');
		
		var $id_cta_complementaria = $('#forma-clients-window').find('input[name=id_cta_complementaria]');
		var $com_cuenta = $('#forma-clients-window').find('input[name=com_cuenta]');
		var $com_scuenta = $('#forma-clients-window').find('input[name=com_scuenta]');
		var $com_sscuenta = $('#forma-clients-window').find('input[name=com_sscuenta]');
		var $com_ssscuenta = $('#forma-clients-window').find('input[name=com_ssscuenta]');
		var $com_sssscuenta = $('#forma-clients-window').find('input[name=com_sssscuenta]');
		var $descripcion_com = $('#forma-clients-window').find('input[name=descripcion_com]');
		
		var $id_cta_activo_complementaria = $('#forma-clients-window').find('input[name=id_cta_activo_complementaria]');
		var $ac_com_cuenta = $('#forma-clients-window').find('input[name=ac_com_cuenta]');
		var $ac_com_scuenta = $('#forma-clients-window').find('input[name=ac_com_scuenta]');
		var $ac_com_sscuenta = $('#forma-clients-window').find('input[name=ac_com_sscuenta]');
		var $ac_com_ssscuenta = $('#forma-clients-window').find('input[name=ac_com_ssscuenta]');
		var $ac_com_sssscuenta = $('#forma-clients-window').find('input[name=ac_com_sssscuenta]');
		var $descripcion_ac_com = $('#forma-clients-window').find('input[name=descripcion_ac_com]');
		
		var $busca_activo = $('#forma-clients-window').find('a[href=busca_activo]');
		var $busca_ingreso = $('#forma-clients-window').find('a[href=busca_ingreso]');
		var $busca_ietu = $('#forma-clients-window').find('a[href=busca_ietu]');
		var $busca_com = $('#forma-clients-window').find('a[href=busca_com]');
		var $busca_ac_com = $('#forma-clients-window').find('a[href=busca_ac_com]');
		
		var $limpiar_activo = $('#forma-clients-window').find('a[href=limpiar_activo]');
		var $limpiar_ingreso = $('#forma-clients-window').find('a[href=limpiar_ingreso]');
		var $limpiar_ietu = $('#forma-clients-window').find('a[href=limpiar_ietu]');
		var $limpiar_com = $('#forma-clients-window').find('a[href=limpiar_com]');
		var $limpiar_ac_com = $('#forma-clients-window').find('a[href=limpiar_ac_com]');
		
		var $cerrar_plugin = $('#forma-clients-window').find('#close');
		var $cancelar_plugin = $('#forma-clients-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-clients-window').find('#submit');
		
		//estos son campos que se pasan como parametro a la funcion que crea  el formulario de direcciones de consignacion
		var numFila = 0;
		var pais = '';
		var entidad = '';
		var localidad = '';
		var calle = '';
		var numero = '';
		var colonia = '';
		var cp = '';
		var telefono = '';
		var localternativa = '';
		var fax = '';
		$campo_id_cliente.attr({ 'value' : 0 });
		//$campo_nocontrol.attr('disabled','-1'); //deshabilitar
		$campo_nocontrol.css({'background' : '#DDDDDD'});
		$retencion_immex.attr('disabled','-1');
		
		$ac_cuenta.hide();
		$ac_scuenta.hide();
		$ac_sscuenta.hide();
		$ac_ssscuenta.hide();
		$ac_sssscuenta.hide();
		
		$ing_cuenta.hide();
		$ing_scuenta.hide();
		$ing_sscuenta.hide();
		$ing_ssscuenta.hide();
		$ing_sssscuenta.hide();
		
		$ietu_cuenta.hide();
		$ietu_scuenta.hide();
		$ietu_sscuenta.hide();
		$ietu_ssscuenta.hide();
		$ietu_sssscuenta.hide();
		
		$com_cuenta.hide();
		$com_scuenta.hide();
		$com_sscuenta.hide();
		$com_ssscuenta.hide();
		$com_sssscuenta.hide();
		
		$ac_com_cuenta.hide();
		$ac_com_scuenta.hide();
		$ac_com_sscuenta.hide();
		$ac_com_ssscuenta.hide();
		$ac_com_sssscuenta.hide();
		
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Cliente dado de alta", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-clients-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}
			else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-clients-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-clients-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			if( entry['Extras'][0]['incluye_contab']=='false' ){
				$pestana_contabilidad.parent().hide();
			}else{
				//visualizar subcuentas de acuerdo al nivel definido para la empresa
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $ac_cuenta.show(); $ing_cuenta.show(); $ietu_cuenta.show(); $com_cuenta.show(); $ac_com_cuenta.show(); };
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $ac_scuenta.show(); $ing_scuenta.show(); $ietu_scuenta.show(); $com_scuenta.show(); $ac_com_scuenta.show();};
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $ac_sscuenta.show(); $ing_sscuenta.show(); $ietu_sscuenta.show(); $com_sscuenta.show(); $ac_com_sscuenta.show();};
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ac_ssscuenta.show(); $ing_ssscuenta.show(); $ietu_ssscuenta.show(); $com_ssscuenta.show(); $ac_com_ssscuenta.show();};
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $ac_sssscuenta.show(); $ing_sssscuenta.show(); $ietu_sssscuenta.show(); $com_sssscuenta.show(); $ac_com_sssscuenta.show();};
				
				//busca Cuenta Activo
				$busca_activo.click(function(event){
					event.preventDefault();
					$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
				});
				
				//busca Cuenta Ingreso
				$busca_ingreso.click(function(event){
					event.preventDefault();
					$busca_cuentas_contables(2, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
				});
				
				//busca Cuenta IETU
				$busca_ietu.click(function(event){
					event.preventDefault();
					$busca_cuentas_contables(3, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
				});
				
				//busca Cuenta Complementaria
				$busca_com.click(function(event){
					event.preventDefault();
					$busca_cuentas_contables(4, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
				});
				
				//busca Cuenta Activo Complementaria
				$busca_ac_com.click(function(event){
					event.preventDefault();
					$busca_cuentas_contables(5, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
				});
				
				//Limpiar campos Cuenta Activo
				$limpiar_activo.click(function(event){
					event.preventDefault();
					$id_cta_activo.val(0);
					$ac_cuenta.val('');
					$ac_scuenta.val('');
					$ac_sscuenta.val('');
					$ac_ssscuenta.val('');
					$ac_sssscuenta.val('');
					$descripcion_ac.val('');
				});
				
				//Limpiar campos Cuenta Ingreso
				$limpiar_ingreso.click(function(event){
					event.preventDefault();
					$id_cta_ingreso.val(0);
					$ing_cuenta.val('');
					$ing_scuenta.val('');
					$ing_sscuenta.val('');
					$ing_ssscuenta.val('');
					$ing_sssscuenta.val('');
					$descripcion_ing.val('');
				});
				
				//Limpiar campos Cuenta IETU
				$limpiar_ietu.click(function(event){
					event.preventDefault();
					$id_cta_ietu.val(0);
					$ietu_cuenta.val('');
					$ietu_scuenta.val('');
					$ietu_sscuenta.val('');
					$ietu_ssscuenta.val('');
					$ietu_sssscuenta.val('');
					$descripcion_ietu.val('');
				});
				
				//Limpiar campos Cuenta Complementaria
				$limpiar_com.click(function(event){
					event.preventDefault();
					$id_cta_complementaria.val(0);
					$com_cuenta.val('');
					$com_scuenta.val('');
					$com_sscuenta.val('');
					$com_ssscuenta.val('');
					$com_sssscuenta.val('');
					$descripcion_com.val('');
				});
				
				//Limpiar campos Cuenta Activo Complementaria
				$limpiar_ac_com.click(function(event){
					event.preventDefault();
					$id_cta_activo_complementaria.val(0);
					$ac_com_cuenta.val('');
					$ac_com_scuenta.val('');
					$ac_com_sscuenta.val('');
					$ac_com_ssscuenta.val('');
					$ac_com_sssscuenta.val('');
					$descripcion_ac_com.val('');
				});
				
			}
			
			//Alimentando los campos select de las pais
			$select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
			$.each(entry['Paises'],function(entryIndex,pais){
				pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
			});
			$select_pais.append(pais_hmtl);
			
			
			var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar entidad--]</option>';
			$select_entidad.children().remove();
			$select_entidad.append(entidad_hmtl);
			
			var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar municipio-]</option>';
			$select_localidad.children().remove();
			$select_localidad.append(localidad_hmtl);
			
			//carga select estados al cambiar el pais
			$select_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$select_entidad.children().remove();
					var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['Entidades'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$select_entidad.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
					$select_localidad.children().remove();
					$select_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			//carga select municipios al cambiar el estado
			$select_entidad.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $select_pais.val();
				//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$select_localidad.children().remove();
					var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
					$.each(entry['Localidades'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$select_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			
			//carga select de vendedores
			$select_agente.children().remove();
			var hmtl_vendedor = '<option value="0" selected="yes">[-- Seleccionar agente --]</option>';
			$.each(entry['Vendedores'],function(entryIndex,vendedor){
				hmtl_vendedor += '<option value="' + vendedor['id'] + '"  >' + vendedor['nombre_vendedor'] + '</option>';
			});
			$select_agente.append(hmtl_vendedor);
			
			//carga select de zonas
			$select_zona.children().remove();
			var zona_html = '<option value="0" selected="yes">[-Zona-]</option>';
			$.each(entry['Zonas'],function(entryIndex,zona){
				zona_html += '<option value="' + zona['id'] + '"  >' + zona['nombre_zona'] + '</option>';
			});
			$select_zona.append(zona_html);
			
			//alimentando select de grupos
			$select_grupo.children().remove();
			var grupo_html = '<option value="0" selected="yes">[-Grupo-]</option>';
			$.each(entry['Grupos'],function(entryIndex,grupo){
				grupo_html += '<option value="' + grupo['id'] + '"  >' + grupo['nombre_grupo'] + '</option>';
			});
			$select_grupo.append(grupo_html);
			
			//Alimentando los campos select de tipos de cliente
			$select_tipocliente.children().remove();
			var tipo_cliente_hmtl = '<option value="0" selected="yes">[--Tipo de cliente--]</option>';
			$.each(entry['Tiposclient'],function(entryIndex,tipo){
				tipo_cliente_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
			});
			$select_tipocliente.append(tipo_cliente_hmtl);
			
			//carga select de clasificacion 1 de cliente
			$select_clasif1.children().remove();
			var clas1_hmtl = '<option value="0" selected="yes">[-- Clasificacion 1 --]</option>';
			$.each(entry['Clas1'],function(entryIndex,clas1){
				clas1_hmtl += '<option value="' + clas1['id'] + '"  >' + clas1['clasificacion1'] + '</option>';
			});
			$select_clasif1.append(clas1_hmtl);
			
			//carga select de clasificacion 2 de cliente
			$select_clasif2.children().remove();
			var clasif2_html = '<option value="0" selected="yes">[-Clasificacion 2-]</option>';
			$.each(entry['Clas2'],function(entryIndex,clas2){
				clasif2_html += '<option value="' + clas2['id'] + '"  >' + clas2['clasificacion2'] + '</option>';
			});
			$select_clasif2.append(clasif2_html);
			
			//carga select de clasificacion 3 de cliente
			$select_clasif3.children().remove();
			var clasif3_html = '<option value="0" selected="yes">[-Clasificacion 3-]</option>';
			$.each(entry['Clas3'],function(entryIndex,clas3){
				clasif3_html += '<option value="' + clas3['id'] + '" >' + clas3['clasificacion3'] + '</option>';
			});
			$select_clasif3.append(clasif3_html);
			
			
			//Alimentando los campos select de monedas
			$select_moneda.children().remove();
			moneda_hmtl='';
			moneda_hmtl = '<option value="0" selected="yes">[-- Moneda --]</option>';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(moneda_hmtl);
            
            
			var filial_html = '';
			$select_empresa_filial.children().remove();
			filial_html += '<option value="false">No</option>';
			filial_html += '<option value="true">Si</option>';
			$select_empresa_filial.append(filial_html);
            
			
			//estatus
			$select_estatus.children().remove();
			var status_html = '<option value="true" selected="yes">Activo</option>';
			status_html += '<option value="false">Inactivo</option>';
			$select_estatus.append(status_html);

            
			//impuesto
			$select_impuesto.children().remove();
			var impuesto_html = '<option value="0" selected="yes">[- Impuesto -]</option>';
			$.each(entry['Impuestos'],function(entryIndex,tipo){
				impuesto_html += '<option value="' + tipo['id'] + '"  >' + tipo['descripcion'] + '</option>';
			});
			$select_impuesto.append(impuesto_html);
			
			
			var immex_html = '';
			$select_immex.children().remove();
			immex_html += '<option value="false">No</option>';
			immex_html += '<option value="true">Si</option>';
			$select_immex.append(immex_html);
			
		
			//cargar select_dia_revision de la semana 
			var elemento_seleccionado = 0;
			var cadena_elemento_cero ="[--Seleccionar D&iacute;a--]";
			$carga_campos_select($select_dia_revision, array_dias_semana,elemento_seleccionado, cadena_elemento_cero);
			
			//cargar select_dia_pago con dias de la semana 
			elemento_seleccionado = 0;
			cadena_elemento_cero ="[--Seleccionar D&iacute;a--]";
			$carga_campos_select($select_dia_pago, array_dias_semana,elemento_seleccionado, cadena_elemento_cero);
			
			//carga select con listas de precio
			elemento_seleccionado = 1;
			cadena_elemento_cero ="[--Seleccionar lista--]";
			$carga_campos_select($select_lista_precio, array_listas_precio,elemento_seleccionado, cadena_elemento_cero);
			
			
			//carga select de metodos de pago
			$select_metodo_pago.children().remove();
			var hmtl_metodo='<option value="0" selected="yes">[-- Seleccionar M&eacute;todo  --]</option>';
			$.each(entry['MetodosPago'],function(entryIndex,metodo){
				if(metodo['id']=='6'){
					hmtl_metodo += '<option value="' + metodo['id'] + '" selected="yes">' + metodo['titulo'] + '</option>';
				}else{
					hmtl_metodo += '<option value="' + metodo['id'] + '" >' + metodo['titulo'] + '</option>';
				}
			});
			$select_metodo_pago.append(hmtl_metodo);
			
			
			//carga select dias de credito
			$select_dias_credito.children().remove();
			var hmtl_condiciones = '<option value="0" selected="yes">[-- Terminos  --]</option>';
			$.each(entry['Condiciones'],function(entryIndex,condicion){
				hmtl_condiciones += '<option value="' + condicion['id'] + '"  >' + condicion['descripcion'] + '</option>';
			});
			$select_dias_credito.append(hmtl_condiciones);
			
				
			//credito suspendido
			$select_credito_suspendido.children().remove();
			var suspendido_html = '<option value="false" selected="yes">No</option>';
			suspendido_html += '<option value="true">Si</option>';
			$select_credito_suspendido.append(suspendido_html);		
			
			
			//carga el select de Inicios de Credito
			$select_inicio_credito.children().remove();
			var inicio_hmtl = '<option value="0" selected="yes">[--Seleccionar inicio --]</option>';
			$.each(entry['InicioCredito'],function(entryIndex,inicio){
				inicio_hmtl += '<option value="' + inicio['id'] + '"  >' + inicio['titulo'] + '</option>';
			});
			$select_inicio_credito.append(inicio_hmtl);
			
			//carga el select de tipos de Embarque
			$select_tipo_embarque.children().remove();
			var embarque_hmtl = '<option value="0" selected="yes">[--Seleccionar tipo --]</option>';
			$.each(entry['TiposEmbarque'],function(entryIndex,tipo){
				embarque_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
			});
			$select_tipo_embarque.append(embarque_hmtl);


			//pais contacto compras
			$select_comp_pais.children().remove();
			$select_comp_pais.append(pais_hmtl);
			
			//estado contacto compras
			$select_comp_estado.children().remove();
			$select_comp_estado.append(entidad_hmtl);
			
			//municipio contacto compras
			$select_comp_localidad.children().remove();
			$select_comp_localidad.append(localidad_hmtl);
			
			//carga select estados al cambiar el pais del contacto compras
			$select_comp_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$select_comp_estado.children().remove();
					var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['Entidades'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$select_comp_estado.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Seleccionar municipio' + '</option>';
					$select_comp_localidad.children().remove();
					$select_comp_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			
			//carga select municipios al cambiar el estado del contacto compras
			$select_comp_estado.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $select_comp_pais.val();
				//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$select_comp_localidad.children().remove();
					var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
					$.each(entry['Localidades'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$select_comp_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
                    
			//pais contacto pagos
			$select_pag_pais.children().remove();
			$select_pag_pais.append(pais_hmtl);
			
			//estado contacto pagos
			$select_pag_estado.children().remove();
			$select_pag_estado.append(entidad_hmtl);
			
			//municipio contacto pagos
			$select_pag_localidad.children().remove();
			$select_pag_localidad.append(localidad_hmtl);

			//carga select estados al cambiar el pais del contacto pagos
			$select_pag_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$select_pag_estado.children().remove();
					var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['Entidades'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$select_pag_estado.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Seleccionar municipio' + '</option>';
					$select_pag_localidad.children().remove();
					$select_pag_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});


			//carga select municipios al cambiar el estado del contacto pagos
			$select_pag_estado.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $select_pag_pais.val();
				//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$select_pag_localidad.children().remove();
					var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
					$.each(entry['Localidades'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$select_pag_localidad.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
		},"json");//termina llamada json
		
		
		
		//cambiar select empresa immex
		$select_immex.change(function(){
			var valor = $(this).val();
			if(valor=='true'){
				//si esta desahabilitado, hay que habilitarlo para permitir la captura de la tasa
				if($retencion_immex.is(':disabled')) {
					$retencion_immex.removeAttr('disabled');
				}
			}else{
				//si no esta deshabilitado, hay que deshabilitarlo
				if(!$retencion_immex.is(':disabled')) {
					$retencion_immex.val('0.00');
					$retencion_immex.attr('disabled','-1');
				}
			}
		});

        
        
		$chkbox_consignacion.bind('click',function(event){
			if($(this).is(':checked')){
				$campo_consignacion.val(1);
				$div_consignacion_grid.css({'display':'block'});
				$agrega_direccion.css({'display':'block'});
				$('#forma-clients-window').find('.clients_div_one').css({'height':'565px'});
				//$('#forma-clients-window').find('.clients_div_three').css({'height':'355px'});
				$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
				$('#forma-clients-window').find('#cierra').css({'width':'765px'});
				$('#forma-clients-window').find('#botones').css({'width':'790px'});
			}else{
				$campo_consignacion.val(0);
				$div_consignacion_grid.css({'display':'none'});
				$agrega_direccion.css({'display':'none'});
				$('#forma-clients-window').find('.clients_div_one').css({'height':'470px'});
				$('#forma-clients-window').find('.clients_div_three').css({'height':'260px'});
				//$('#forma-clients-window').find('.clients_div_three').css({'height':'355px'});
				$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
				$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
				$('#forma-clients-window').find('#cierra').css({'width':'765px'});
				$('#forma-clients-window').find('#botones').css({'width':'790px'});
			}
		});
		
		
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo_limite_credito.focus(function(e){
			if(parseFloat($campo_limite_credito.val())<1){
				$campo_limite_credito.val('');
			}
		});
		
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_limite_credito.blur(function(e){
			if(parseFloat($campo_limite_credito.val())==0||$campo_limite_credito.val()==""){
				$campo_limite_credito.val(0.00);
			}
		});	
		
		$campo_limite_credito.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$campo_caducidad_cotizacion.focus(function(e){
			if(parseFloat($campo_caducidad_cotizacion.val())<1){
				$campo_caducidad_cotizacion.val('');
			}
		});
		
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_caducidad_cotizacion.blur(function(e){
			if(parseFloat($campo_caducidad_cotizacion.val())==0||$campo_caducidad_cotizacion.val()==""){
				$campo_caducidad_cotizacion.val(0);
			}
		});	
		
		$campo_caducidad_cotizacion.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
		$retencion_immex.focus(function(e){
			if(parseFloat($retencion_immex.val())<1){
				$retencion_immex.val('');
			}
		});
		
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$retencion_immex.blur(function(e){
			if(parseFloat($retencion_immex.val())==0||$retencion_immex.val()==""){
				$retencion_immex.val(0);
			}
		});
		
		$retencion_immex.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		$cuenta_mn.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		$cuenta_usd.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
		
		
		//agrega nueva direccion de consignacion
		$agrega_direccion.click(function(event){
			event.preventDefault();
			$forma_direccion_consignacion(numFila,pais,entidad,localidad,calle,numero,colonia,cp,telefono,localternativa,fax);
		});
		
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $tabla_datos_consignacion).size();
			$total_tr.val(trCount);
			if($chkbox_consignacion.is(':checked')){
				if(parseInt(trCount)<1){
					jAlert("Debes ingresar por lo menos una direccion de consignacion para el cliente.", 'Atencion!');
					return false;
				}else{
					return true;
				}
			}else{
				return true;
			}
		});

		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-clients-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-clients-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$campo_rfc.focus();
	});
	
	
	var carga_formaClients00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Cliente seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Cliente fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Cliente no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaClients00';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			//var accion = "get_cliente";
			
			$(this).modalPanel();
			$('#forma-clients-window').css({ "margin-left": -400, 	"margin-top": -290 });
			
			$forma_selected.prependTo('#forma-clients-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_cliente.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var $total_tr = $('#forma-clients-window').find('input[name=total_tr]');
				var $campo_consignacion = $('#forma-clients-window').find('input[name=campo_consignacion]');
				var $campo_id_cliente = $('#forma-clients-window').find('input[name=identificador_cliente]');
				var $campo_nocontrol = $('#forma-clients-window').find('input[name=nocontrol]');
				var $campo_rfc = $('#forma-clients-window').find('input[name=rfc]');
				var $campo_curp = $('#forma-clients-window').find('input[name=curp]');
				var $campo_razonsocial = $('#forma-clients-window').find('input[name=razonsocial]');
				var $clave_comercial = $('#forma-clients-window').find('input[name=clave_comercial]');
				var $campo_calle = $('#forma-clients-window').find('input[name=calle]');
				var $campo_numero = $('#forma-clients-window').find('input[name=numero_int]');
				var $campo_numero_ext = $('#forma-clients-window').find('input[name=numero_ext]');
				var $campo_entrecalles = $('#forma-clients-window').find('input[name=entrecalles]');
				var $campo_colonia = $('#forma-clients-window').find('input[name=colonia]');
				var $campo_cp = $('#forma-clients-window').find('input[name=cp]');
				var $select_pais = $('#forma-clients-window').find('select[name=pais]');
				var $select_entidad = $('#forma-clients-window').find('select[name=estado]');
				var $select_localidad = $('#forma-clients-window').find('select[name=municipio]');
				var $select_agente = $('#forma-clients-window').find('select[name=agente]');
				var $campo_loc_alternativa = $('#forma-clients-window').find('input[name=loc_alternativa]');
				var $campo_email = $('#forma-clients-window').find('input[name=email]');
				var $campo_tel1 = $('#forma-clients-window').find('input[name=tel1]');
				var $campo_ext1 = $('#forma-clients-window').find('input[name=ext1]');
				var $campo_fax = $('#forma-clients-window').find('input[name=fax]');
				var $campo_tel2 = $('#forma-clients-window').find('input[name=tel2]');
				var $campo_ext2 = $('#forma-clients-window').find('input[name=ext2]');
				var $campo_contacto = $('#forma-clients-window').find('input[name=contacto]');
				
				//datos
				var $select_zona = $('#forma-clients-window').find('select[name=zona]');
				var $select_grupo = $('#forma-clients-window').find('select[name=grupo]');
				var $select_tipocliente = $('#forma-clients-window').find('select[name=tipocliente]');
				var $select_clasif1 = $('#forma-clients-window').find('select[name=clasif1]');
				var $select_clasif2 = $('#forma-clients-window').find('select[name=clasif2]');
				var $select_clasif3 = $('#forma-clients-window').find('select[name=clasif3]');
				var $select_moneda = $('#forma-clients-window').find('select[name=moneda]');
				var $select_empresa_filial = $('#forma-clients-window').find('select[name=filial]');
				var $select_estatus = $('#forma-clients-window').find('select[name=estatus]');
				var $select_impuesto = $('#forma-clients-window').find('select[name=impuesto]');
				var $select_immex = $('#forma-clients-window').find('select[name=select_immex]');
				var $retencion_immex = $('#forma-clients-window').find('input[name=retencion_immex]');
				var $select_dia_revision = $('#forma-clients-window').find('select[name=select_dia_revision]');
				var $select_dia_pago = $('#forma-clients-window').find('select[name=select_dia_pago]');
				var $cuenta_mn = $('#forma-clients-window').find('input[name=cuenta_mn]');
				var $cuenta_usd = $('#forma-clients-window').find('input[name=cuenta_usd]');
				var $select_lista_precio = $('#forma-clients-window').find('select[name=select_lista_precio]');
				var $select_metodo_pago = $('#forma-clients-window').find('select[name=select_metodo_pago]');
				
				//credito
				var $campo_limite_credito = $('#forma-clients-window').find('input[name=limite_credito]');
				var $select_dias_credito = $('#forma-clients-window').find('select[name=dias_credito]');
				var $select_credito_suspendido = $('#forma-clients-window').find('select[name=credito_suspendido]');
				var $select_inicio_credito = $('#forma-clients-window').find('select[name=inicio_credito]');
				var $select_tipo_embarque = $('#forma-clients-window').find('select[name=tipo_embarque]');
				var $campo_caducidad_cotizacion = $('#forma-clients-window').find('input[name=cad_cotizacion]');
				var $txtarea_condiciones = $('#forma-clients-window').find('textarea[name=condiciones]');
				var $txtarea_observaciones = $('#forma-clients-window').find('textarea[name=observaciones]');
				
				//tab4 Contacto Compras
				var $campo_comp_contacto = $('#forma-clients-window').find('input[name=comp_contacto]');
				var $campo_comp_puesto = $('#forma-clients-window').find('input[name=comp_puesto]');
				var $campo_comp_calle = $('#forma-clients-window').find('input[name=comp_calle]');
				var $campo_comp_numcalle = $('#forma-clients-window').find('input[name=comp_numcalle]');
				var $campo_comp_colonia = $('#forma-clients-window').find('input[name=comp_colonia]');
				var $campo_comp_cp = $('#forma-clients-window').find('input[name=comp_cp]');
				var $campo_comp_entrecalles = $('#forma-clients-window').find('input[name=comp_entrecalles]');
				var $select_comp_pais = $('#forma-clients-window').find('select[name=comp_pais]');
				var $select_comp_estado = $('#forma-clients-window').find('select[name=comp_estado]');
				var $select_comp_localidad = $('#forma-clients-window').find('select[name=comp_municipio]');
				var $campo_comp_tel1 = $('#forma-clients-window').find('input[name=comp_tel1]');
				var $campo_comp_ext1 = $('#forma-clients-window').find('input[name=comp_ext1]');
				var $campo_comp_fax = $('#forma-clients-window').find('input[name=comp_fax]');
				var $campo_comp_tel2 = $('#forma-clients-window').find('input[name=comp_tel2]');
				var $campo_comp_ext2 = $('#forma-clients-window').find('input[name=comp_ext2]');
				var $campo_comp_email = $('#forma-clients-window').find('input[name=comp_email]');
				
				//tab5 Contacto Pagos
				var $campo_pag_contacto = $('#forma-clients-window').find('input[name=pag_contacto]');
				var $campo_pag_puesto = $('#forma-clients-window').find('input[name=pag_puesto]');
				var $campo_pag_calle = $('#forma-clients-window').find('input[name=pag_calle]');
				var $campo_pag_numcalle = $('#forma-clients-window').find('input[name=pag_numcalle]');
				var $campo_pag_colonia = $('#forma-clients-window').find('input[name=pag_colonia]');
				var $campo_pag_cp = $('#forma-clients-window').find('input[name=pag_cp]');
				var $campo_pag_entrecalles = $('#forma-clients-window').find('input[name=pag_entrecalles]');
				var $select_pag_pais = $('#forma-clients-window').find('select[name=pag_pais]');
				var $select_pag_estado = $('#forma-clients-window').find('select[name=pag_estado]');
				var $select_pag_localidad = $('#forma-clients-window').find('select[name=pag_municipio]');
				var $campo_pag_tel1 = $('#forma-clients-window').find('input[name=pag_tel1]');
				var $campo_pag_ext1 = $('#forma-clients-window').find('input[name=pag_ext1]');
				var $campo_pag_fax = $('#forma-clients-window').find('input[name=pag_fax]');
				var $campo_pag_tel2 = $('#forma-clients-window').find('input[name=pag_tel2]');
				var $campo_pag_ext2 = $('#forma-clients-window').find('input[name=pag_ext2]');
				var $campo_pag_email = $('#forma-clients-window').find('input[name=pag_email]');
				
				var $chkbox_consignacion = $('#forma-clients-window').find('input[name=consignacion]');
				var $agrega_direccion = $('#forma-clients-window').find('a[href*=agrega_direccion]');
				var $div_consignacion_grid = $('#forma-clients-window').find('#div_consignacion_grid');
				var $tabla_datos_consignacion = $('#forma-clients-window').find('#tabla_datos_consignacion');
				
				var $pestana_contabilidad = $('#forma-clients-window').find('ul.pestanas').find('a[href*=#tabx-6]');
				
				var $id_cta_activo = $('#forma-clients-window').find('input[name=id_cta_activo]');
				var $ac_cuenta = $('#forma-clients-window').find('input[name=ac_cuenta]');
				var $ac_scuenta = $('#forma-clients-window').find('input[name=ac_scuenta]');
				var $ac_sscuenta = $('#forma-clients-window').find('input[name=ac_sscuenta]');
				var $ac_ssscuenta = $('#forma-clients-window').find('input[name=ac_ssscuenta]');
				var $ac_sssscuenta = $('#forma-clients-window').find('input[name=ac_sssscuenta]');
				var $descripcion_ac = $('#forma-clients-window').find('input[name=descripcion_ac]');
				
				var $id_cta_ingreso = $('#forma-clients-window').find('input[name=id_cta_ingreso]');
				var $ing_cuenta = $('#forma-clients-window').find('input[name=ing_cuenta]');
				var $ing_scuenta = $('#forma-clients-window').find('input[name=ing_scuenta]');
				var $ing_sscuenta = $('#forma-clients-window').find('input[name=ing_sscuenta]');
				var $ing_ssscuenta = $('#forma-clients-window').find('input[name=ing_ssscuenta]');
				var $ing_sssscuenta = $('#forma-clients-window').find('input[name=ing_sssscuenta]');
				var $descripcion_ing = $('#forma-clients-window').find('input[name=descripcion_ing]');
				
				var $id_cta_ietu = $('#forma-clients-window').find('input[name=id_cta_ietu]');
				var $ietu_cuenta = $('#forma-clients-window').find('input[name=ietu_cuenta]');
				var $ietu_scuenta = $('#forma-clients-window').find('input[name=ietu_scuenta]');
				var $ietu_sscuenta = $('#forma-clients-window').find('input[name=ietu_sscuenta]');
				var $ietu_ssscuenta = $('#forma-clients-window').find('input[name=ietu_ssscuenta]');
				var $ietu_sssscuenta = $('#forma-clients-window').find('input[name=ietu_sssscuenta]');
				var $descripcion_ietu = $('#forma-clients-window').find('input[name=descripcion_ietu]');
				
				var $id_cta_complementaria = $('#forma-clients-window').find('input[name=id_cta_complementaria]');
				var $com_cuenta = $('#forma-clients-window').find('input[name=com_cuenta]');
				var $com_scuenta = $('#forma-clients-window').find('input[name=com_scuenta]');
				var $com_sscuenta = $('#forma-clients-window').find('input[name=com_sscuenta]');
				var $com_ssscuenta = $('#forma-clients-window').find('input[name=com_ssscuenta]');
				var $com_sssscuenta = $('#forma-clients-window').find('input[name=com_sssscuenta]');
				var $descripcion_com = $('#forma-clients-window').find('input[name=descripcion_com]');
				
				var $id_cta_activo_complementaria = $('#forma-clients-window').find('input[name=id_cta_activo_complementaria]');
				var $ac_com_cuenta = $('#forma-clients-window').find('input[name=ac_com_cuenta]');
				var $ac_com_scuenta = $('#forma-clients-window').find('input[name=ac_com_scuenta]');
				var $ac_com_sscuenta = $('#forma-clients-window').find('input[name=ac_com_sscuenta]');
				var $ac_com_ssscuenta = $('#forma-clients-window').find('input[name=ac_com_ssscuenta]');
				var $ac_com_sssscuenta = $('#forma-clients-window').find('input[name=ac_com_sssscuenta]');
				var $descripcion_ac_com = $('#forma-clients-window').find('input[name=descripcion_ac_com]');
				
				var $busca_activo = $('#forma-clients-window').find('a[href=busca_activo]');
				var $busca_ingreso = $('#forma-clients-window').find('a[href=busca_ingreso]');
				var $busca_ietu = $('#forma-clients-window').find('a[href=busca_ietu]');
				var $busca_com = $('#forma-clients-window').find('a[href=busca_com]');
				var $busca_ac_com = $('#forma-clients-window').find('a[href=busca_ac_com]');
				
				var $limpiar_activo = $('#forma-clients-window').find('a[href=limpiar_activo]');
				var $limpiar_ingreso = $('#forma-clients-window').find('a[href=limpiar_ingreso]');
				var $limpiar_ietu = $('#forma-clients-window').find('a[href=limpiar_ietu]');
				var $limpiar_com = $('#forma-clients-window').find('a[href=limpiar_com]');
				var $limpiar_ac_com = $('#forma-clients-window').find('a[href=limpiar_ac_com]');
				
				var $cerrar_plugin = $('#forma-clients-window').find('#close');
				var $cancelar_plugin = $('#forma-clients-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-clients-window').find('#submit');
				
				//estos son campos que se pasan como parametro a la funcion que crea  el formulario de direcciones de consignacion
				var numFila = 0;
				var pais = '';
				var entidad = '';
				var localidad = '';
				var calle = '';
				var numero = '';
				var colonia = '';
				var cp = '';
				var telefono = '';
				var localternativa = '';
				var fax = '';
				
				//$campo_titulo.attr({ 'readOnly':true });
				//$campo_nocontrol.attr('disabled','-1'); //deshabilitar
				$campo_nocontrol.css({'background' : '#DDDDDD'});
				
				if(parseInt(rolVendedor)>=1){
					//Ocultar boton Actualizar
					$submit_actualizar.hide();
					
					//Quitar enter a todos los campos input
					$('#forma-clients-window').find('input').keypress(function(e){
						if(e.which==13 ) {
							return false;
						}
					});
				}
				
				$ac_cuenta.hide();
				$ac_scuenta.hide();
				$ac_sscuenta.hide();
				$ac_ssscuenta.hide();
				$ac_sssscuenta.hide();
				
				$ing_cuenta.hide();
				$ing_scuenta.hide();
				$ing_sscuenta.hide();
				$ing_ssscuenta.hide();
				$ing_sssscuenta.hide();
				
				$ietu_cuenta.hide();
				$ietu_scuenta.hide();
				$ietu_sscuenta.hide();
				$ietu_ssscuenta.hide();
				$ietu_sssscuenta.hide();
				
				$com_cuenta.hide();
				$com_scuenta.hide();
				$com_sscuenta.hide();
				$com_ssscuenta.hide();
				$com_sssscuenta.hide();
				
				$ac_com_cuenta.hide();
				$ac_com_scuenta.hide();
				$ac_com_sscuenta.hide();
				$ac_com_ssscuenta.hide();
				$ac_com_sssscuenta.hide();
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-clients-overlay').fadeOut(remove);
						jAlert("Los datos del cliente se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-clients-window').find('div.interrogacion').css({'display':'none'});
						
						//alert(data['success']);
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-clients-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					if( entry['Extras'][0]['incluye_contab']=='false' ){
						$pestana_contabilidad.parent().hide();
					}else{
						//visualizar subcuentas de acuerdo al nivel definido para la empresa
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $ac_cuenta.show(); $ing_cuenta.show(); $ietu_cuenta.show(); $com_cuenta.show(); $ac_com_cuenta.show(); };
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $ac_scuenta.show(); $ing_scuenta.show(); $ietu_scuenta.show(); $com_scuenta.show(); $ac_com_scuenta.show();};
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $ac_sscuenta.show(); $ing_sscuenta.show(); $ietu_sscuenta.show(); $com_sscuenta.show(); $ac_com_sscuenta.show();};
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ac_ssscuenta.show(); $ing_ssscuenta.show(); $ietu_ssscuenta.show(); $com_ssscuenta.show(); $ac_com_ssscuenta.show();};
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $ac_sssscuenta.show(); $ing_sssscuenta.show(); $ietu_sssscuenta.show(); $com_sssscuenta.show(); $ac_com_sssscuenta.show();};
						
						$id_cta_activo.attr({ 'value' : entry['Contab'][0]['ac_id_cta'] });
						$ac_cuenta.attr({ 'value' : entry['Contab'][0]['ac_cta'] });
						$ac_scuenta.attr({ 'value' : entry['Contab'][0]['ac_subcta'] });
						$ac_sscuenta.attr({ 'value' : entry['Contab'][0]['ac_ssubcta'] });
						$ac_ssscuenta.attr({ 'value' : entry['Contab'][0]['ac_sssubcta'] });
						$ac_sssscuenta.attr({ 'value' : entry['Contab'][0]['ac_ssssubcta'] });
						$descripcion_ac.attr({ 'value' : entry['Contab'][0]['ac_descripcion'] });
						
						$id_cta_ingreso.attr({ 'value' : entry['Contab'][0]['ing_id_cta'] });
						$ing_cuenta.attr({ 'value' : entry['Contab'][0]['ing_cta'] });
						$ing_scuenta.attr({ 'value' : entry['Contab'][0]['ing_subcta'] });
						$ing_sscuenta.attr({ 'value' : entry['Contab'][0]['ing_ssubcta'] });
						$ing_ssscuenta.attr({ 'value' : entry['Contab'][0]['ing_sssubcta'] });
						$ing_sssscuenta.attr({ 'value' : entry['Contab'][0]['ing_ssssubcta'] });
						$descripcion_ing.attr({ 'value' : entry['Contab'][0]['ing_descripcion'] });
						
						$id_cta_ietu.attr({ 'value' : entry['Contab'][0]['ietu_id_cta'] });
						$ietu_cuenta.attr({ 'value' : entry['Contab'][0]['ietu_cta'] });
						$ietu_scuenta.attr({ 'value' : entry['Contab'][0]['ietu_subcta'] });
						$ietu_sscuenta.attr({ 'value' : entry['Contab'][0]['ietu_ssubcta'] });
						$ietu_ssscuenta.attr({ 'value' : entry['Contab'][0]['ietu_sssubcta'] });
						$ietu_sssscuenta.attr({ 'value' : entry['Contab'][0]['ietu_ssssubcta'] });
						$descripcion_ietu.attr({ 'value' : entry['Contab'][0]['ietu_descripcion'] });
						
						$id_cta_complementaria.attr({ 'value' : entry['Contab'][0]['comp_id_cta'] });
						$com_cuenta.attr({ 'value' : entry['Contab'][0]['comp_cta'] });
						$com_scuenta.attr({ 'value' : entry['Contab'][0]['comp_subcta'] });
						$com_sscuenta.attr({ 'value' : entry['Contab'][0]['comp_ssubcta'] });
						$com_ssscuenta.attr({ 'value' : entry['Contab'][0]['comp_sssubcta'] });
						$com_sssscuenta.attr({ 'value' : entry['Contab'][0]['comp_ssssubcta'] });
						$descripcion_com.attr({ 'value' : entry['Contab'][0]['comp_descripcion'] });
						
						$id_cta_activo_complementaria.attr({ 'value' : entry['Contab'][0]['ac_comp_id_cta'] });
						$ac_com_cuenta.attr({ 'value' : entry['Contab'][0]['ac_comp_cta'] });
						$ac_com_scuenta.attr({ 'value' : entry['Contab'][0]['ac_comp_subcta'] });
						$ac_com_sscuenta.attr({ 'value' : entry['Contab'][0]['ac_comp_ssubcta'] });
						$ac_com_ssscuenta.attr({ 'value' : entry['Contab'][0]['ac_comp_sssubcta'] });
						$ac_com_sssscuenta.attr({ 'value' : entry['Contab'][0]['ac_comp_ssssubcta'] });
						$descripcion_ac_com.attr({ 'value' : entry['Contab'][0]['ac_comp_descripcion'] });
						
						//busca Cuenta Activo
						$busca_activo.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//busca Cuenta Ingreso
						$busca_ingreso.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(2, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//busca Cuenta IETU
						$busca_ietu.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(3, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//busca Cuenta Complementaria
						$busca_com.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(4, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//busca Cuenta Activo Complementaria
						$busca_ac_com.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(5, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//Limpiar campos Cuenta Activo
						$limpiar_activo.click(function(event){
							event.preventDefault();
							$id_cta_activo.val(0);
							$ac_cuenta.val('');
							$ac_scuenta.val('');
							$ac_sscuenta.val('');
							$ac_ssscuenta.val('');
							$ac_sssscuenta.val('');
							$descripcion_ac.val('');
						});
						
						//Limpiar campos Cuenta Ingreso
						$limpiar_ingreso.click(function(event){
							event.preventDefault();
							$id_cta_ingreso.val(0);
							$ing_cuenta.val('');
							$ing_scuenta.val('');
							$ing_sscuenta.val('');
							$ing_ssscuenta.val('');
							$ing_sssscuenta.val('');
							$descripcion_ing.val('');
						});
						
						//Limpiar campos Cuenta IETU
						$limpiar_ietu.click(function(event){
							event.preventDefault();
							$id_cta_ietu.val(0);
							$ietu_cuenta.val('');
							$ietu_scuenta.val('');
							$ietu_sscuenta.val('');
							$ietu_ssscuenta.val('');
							$ietu_sssscuenta.val('');
							$descripcion_ietu.val('');
						});
						
						//Limpiar campos Cuenta Complementaria
						$limpiar_com.click(function(event){
							event.preventDefault();
							$id_cta_complementaria.val(0);
							$com_cuenta.val('');
							$com_scuenta.val('');
							$com_sscuenta.val('');
							$com_ssscuenta.val('');
							$com_sssscuenta.val('');
							$descripcion_com.val('');
						});
						
						//Limpiar campos Cuenta Activo Complementaria
						$limpiar_ac_com.click(function(event){
							event.preventDefault();
							$id_cta_activo_complementaria.val(0);
							$ac_com_cuenta.val('');
							$ac_com_scuenta.val('');
							$ac_com_sscuenta.val('');
							$ac_com_ssscuenta.val('');
							$ac_com_sssscuenta.val('');
							$descripcion_ac_com.val('');
						});
					}
					
					$campo_id_cliente.attr({ 'value' : entry['Cliente']['0']['id_cliente'] });
					$campo_nocontrol.attr({ 'value' : entry['Cliente']['0']['numero_control'] });
					$campo_rfc.attr({ 'value' : entry['Cliente']['0']['rfc'] });
					$campo_curp.attr({ 'value' : entry['Cliente']['0']['curp'] });
					$campo_razonsocial.attr({ 'value' : entry['Cliente']['0']['razon_social'] });
					$clave_comercial.attr({ 'value' : entry['Cliente']['0']['clave_comercial'] });
					$campo_calle.attr({ 'value' : entry['Cliente']['0']['calle'] });
					$campo_numero.attr({ 'value' : entry['Cliente']['0']['numero'] });
					$campo_entrecalles.attr({ 'value' : entry['Cliente']['0']['entre_calles'] });
					$campo_numero_ext.attr({ 'value' : entry['Cliente']['0']['numero_exterior'] });
					$campo_colonia.attr({ 'value' : entry['Cliente']['0']['colonia'] });
					$campo_cp.attr({ 'value' : entry['Cliente']['0']['cp'] });
					$campo_loc_alternativa.attr({ 'value' : entry['Cliente']['0'][''] });
					$campo_email.attr({ 'value' : entry['Cliente']['0']['email'] });
					$campo_tel1.attr({ 'value' : entry['Cliente']['0']['telefono1'] });
					$campo_ext1.attr({ 'value' : entry['Cliente']['0']['extension1'] });
					$campo_fax.attr({ 'value' : entry['Cliente']['0']['fax'] });
					$campo_tel2.attr({ 'value' : entry['Cliente']['0']['telefono2'] });
					$campo_ext2.attr({ 'value' : entry['Cliente']['0']['extension2'] });
					$campo_contacto.attr({ 'value' : entry['Cliente']['0']['contacto'] });
					$campo_limite_credito.attr({ 'value' : entry['Cliente']['0']['limite_credito'] });
					$campo_caducidad_cotizacion.attr({ 'value' : entry['Cliente']['0']['dias_caducidad_cotizacion'] });
					$txtarea_condiciones.text(entry['Cliente']['0']['condiciones']);
					$txtarea_observaciones.text(entry['Cliente']['0']['observaciones']);
					$campo_comp_contacto.attr({ 'value' : entry['Cliente']['0']['contacto_compras_nombre'] });
					$campo_comp_puesto.attr({ 'value' : entry['Cliente']['0']['contacto_compras_puesto'] });
					$campo_comp_calle.attr({ 'value' : entry['Cliente']['0']['contacto_compras_calle'] });
					$campo_comp_numcalle.attr({ 'value' : entry['Cliente']['0']['contacto_compras_numero'] });
					$campo_comp_colonia.attr({ 'value' : entry['Cliente']['0']['contacto_compras_colonia'] });
					$campo_comp_cp.attr({ 'value' : entry['Cliente']['0']['contacto_compras_cp'] });
					$campo_comp_entrecalles.attr({ 'value' : entry['Cliente']['0']['contacto_compras_entre_calles'] });
					$campo_comp_tel1.attr({ 'value' : entry['Cliente']['0']['contacto_compras_telefono1'] });
					$campo_comp_ext1.attr({ 'value' : entry['Cliente']['0']['contacto_compras_extension1'] });
					$campo_comp_fax.attr({ 'value' : entry['Cliente']['0']['contacto_compras_fax'] });
					$campo_comp_tel2.attr({ 'value' : entry['Cliente']['0']['contacto_compras_telefono2'] });
					$campo_comp_ext2.attr({ 'value' : entry['Cliente']['0']['contacto_compras_extension2'] });
					$campo_comp_email.attr({ 'value' : entry['Cliente']['0']['contacto_compras_email'] });
					$campo_pag_contacto.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_nombre'] });
					$campo_pag_puesto.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_puesto'] });
					$campo_pag_calle.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_calle'] });
					$campo_pag_numcalle.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_numero'] });
					$campo_pag_colonia.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_colonia'] });
					$campo_pag_cp.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_cp'] });
					$campo_pag_entrecalles.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_entre_calles'] });
					$campo_pag_tel1.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_telefono1'] });
					$campo_pag_ext1.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_extension1'] });
					$campo_pag_fax.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_fax'] });
					$campo_pag_tel2.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_telefono2'] });
					$campo_pag_ext2.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_extension2'] });
					$campo_pag_email.attr({ 'value' : entry['Cliente']['0']['contacto_pagos_email'] });
					
					$retencion_immex.attr({ 'value' : entry['Cliente']['0']['tasa_ret_immex'] });
					$cuenta_mn.attr({ 'value' : entry['Cliente']['0']['cta_pago_mn'] });
					$cuenta_usd.attr({ 'value' : entry['Cliente']['0']['cta_pago_usd'] });
					
					//Alimentando los campos select de las pais
					$select_pais.children().remove();
					var pais_hmtl = "";
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Cliente']['0']['pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_pais.append(pais_hmtl);

					
					//Alimentando los campos select del estado
					$select_entidad.children().remove();
					var entidad_hmtl = "";
					$.each(entry['Entidades'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Cliente']['0']['estado_id']){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_entidad.append(entidad_hmtl);
					
					
					//Alimentando los campos select de los municipios
					$select_localidad.children().remove();
					var localidad_hmtl = "";
					$.each(entry['Localidades'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Cliente']['0']['municipio_id']){
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_localidad.append(localidad_hmtl);
					
					

					
					//carga select de vendedores
					$select_agente.children().remove();
					var hmtl_vendedor = '<option value="0">[-- Vendedor --]</option>';
					$.each(entry['Vendedores'],function(entryIndex,vendedor){
						if(entry['Cliente']['0']['cxc_agen_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_vendedor'] + '</option>';
						}else{
							hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_vendedor'] + '</option>';
						}
					});
					$select_agente.append(hmtl_vendedor);
					
                    $select_zona.children().remove();
                    var zona_html = '<option value="0">[-Zona-]</option>';
                    $.each(entry['Zonas'],function(entryIndex,zona){
						if(parseInt(zona['id']) == parseInt(entry['Cliente']['0']['zona_id'])){
							zona_html += '<option value="' + zona['id'] + '" selected="yes">' + zona['nombre_zona'] + '</option>';
						}else{
							zona_html += '<option value="' + zona['id'] + '"  >' + zona['nombre_zona'] + '</option>';
						}
                    });
                    $select_zona.append(zona_html);
                    
                    $select_grupo.children().remove();
                    var grupo_html = '<option value="0">[-Grupo-]</option>';
                    $.each(entry['Grupos'],function(entryIndex,grupo){
						if(parseInt(grupo['id']) == parseInt(entry['Cliente']['0']['cxc_clie_grupo_id'])){
							grupo_html += '<option value="' + grupo['id'] + '"  selected="yes">' + grupo['nombre_grupo'] + '</option>';
						}else{
							grupo_html += '<option value="' + grupo['id'] + '"  >' + grupo['nombre_grupo'] + '</option>';
						}
                    });
                    $select_grupo.append(grupo_html);
                    
					//Alimentando los campos select de tipo de cliente
					$select_tipocliente.children().remove();
					var tipocliente_hmtl = '<option value="0">[--Tipo de cliente--]</option>';
					$.each(entry['Tiposclient'],function(entryIndex, tipo){
						if(entry['Cliente']['0']['clienttipo_id'] == tipo['id']){
							tipocliente_hmtl += '<option value="' + tipo['id'] + '"  selected="yes">' + tipo['titulo'] + '</option>';
						}else{
							tipocliente_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
						}
					});
					$select_tipocliente.append(tipocliente_hmtl);
					
					
                    $select_clasif1.children().remove();
                    var clasif1_html = '<option value="0">[-Clasificacion 1-]</option>';
                    $.each(entry['Clas1'],function(entryIndex,clas1){
						if(parseInt(clas1['id']) == parseInt(entry['Cliente']['0']['clasif_1'])){
							clasif1_html += '<option value="' + clas1['id'] + '" selected="yes">' + clas1['clasificacion1'] + '</option>';
						}else{
							clasif1_html += '<option value="' + clas1['id'] + '"  >' + clas1['clasificacion1'] + '</option>';
						}
                    });
                    $select_clasif1.append(clasif1_html);
                    
					
                    $select_clasif2.children().remove();
                    var clasif2_html = '<option value="0">[-Clasificacion 2-]</option>';
                    $.each(entry['Clas2'],function(entryIndex,clas2){
						if(parseInt(clas2['id']) == parseInt(entry['Cliente']['0']['clasif_2'])){
							clasif2_html += '<option value="' + clas2['id'] + '"  selected="yes">' + clas2['clasificacion2'] + '</option>';
						}else{
							clasif2_html += '<option value="' + clas2['id'] + '"  >' + clas2['clasificacion2'] + '</option>';
						}
                    });
                    $select_clasif2.append(clasif2_html);
                    
					
                    $select_clasif3.children().remove();
                    var clasif3_html = '<option value="0">[-Clasificacion 3-]</option>';
                    $.each(entry['Clas3'],function(entryIndex,clas3){
						if(parseInt(clas3['id']) == parseInt(entry['Cliente']['0']['clasif_3'])){
							clasif3_html += '<option value="' + clas3['id'] + '"  selected="yes">' + clas3['clasificacion3'] + '</option>';
						}else{
							clasif3_html += '<option value="' + clas3['id'] + '" >' + clas3['clasificacion3'] + '</option>';
						}
                    });
                    $select_clasif3.append(clasif3_html);
                    
					//Alimentando los campos select de monedas
					$select_moneda.children().remove();
					var moneda_hmtl = '<option value="0">[--Moneda--]</option>';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(entry['Cliente']['0']['moneda'] == moneda['id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
                    
                    
					var si_filial = '';
					var no_filial = '';
					if ( entry['Cliente']['0']['filial'] == 'true'  ){
						si_filial = 'selected="yes"';
					}else{
						no_filial = 'selected="yes"';			
					}
					var filial_html = '';
					$select_empresa_filial.children().remove();
					filial_html += '<option value="false" '+no_filial+'>No</option>';
					filial_html += '<option value="true" '+si_filial+'>Si</option>';
					$select_empresa_filial.append(filial_html);
					
					//estatus
					var sel_activo='';
					var sel_inactivo='';
					if(entry['Cliente']['0']['estatus']=='true'){
						sel_activo='selected="yes"';
					}else{
						sel_inactivo='selected="yes"';
					}
                    $select_estatus.children().remove();
                    var status_html = '<option value="true" '+sel_activo+'>Activo</option>';
                    status_html += '<option value="false" '+sel_inactivo+'>Inactivo</option>';
                    $select_estatus.append(status_html);
                    
                    
					//impuesto
					$select_impuesto.children().remove();
					var impuesto_html = '<option value="0">[- Impuesto -]</option>';
					$.each(entry['Impuestos'],function(entryIndex,tipo){
						if(entry['Cliente']['0']['gral_imp_id'] == tipo['id']){
							impuesto_html += '<option value="' + tipo['id'] + '" selected="yes">' + tipo['descripcion'] + '</option>';
						}else{
							impuesto_html += '<option value="' + tipo['id'] + '" >' + tipo['descripcion'] + '</option>';
						}
					});
					$select_impuesto.append(impuesto_html);
					
					
					var immex_html = '';
					$select_immex.children().remove();
					if( entry['Cliente']['0']['empresa_immex'] == 'true' ){
						immex_html += '<option value="false">No</option>';
						immex_html += '<option value="true" selected="yes">Si</option>';
					}else{
						immex_html += '<option value="false" selected="yes">No</option>';
						immex_html += '<option value="true">Si</option>';
						$retencion_immex.attr('disabled','-1');
					}
					$select_immex.append(immex_html);
					
					//cargar select_dia_revision de la semana 
					var elemento_seleccionado = entry['Cliente']['0']['dia_revision'];
					var cadena_elemento_cero ="[--Seleccionar D&iacute;a--]";
					$carga_campos_select($select_dia_revision, array_dias_semana,elemento_seleccionado, cadena_elemento_cero);
				
					//cargar select_dia_pago con dias de la semana 
					elemento_seleccionado = entry['Cliente']['0']['dia_pago'];
					cadena_elemento_cero ="[--Seleccionar D&iacute;a--]";
					$carga_campos_select($select_dia_pago, array_dias_semana,elemento_seleccionado, cadena_elemento_cero);
					
					//cargar select_lista_precio con todas las listas
					elemento_seleccionado = entry['Cliente']['0']['lista_precio'];
					cadena_elemento_cero ="[--Seleccionar lista--]";
					$carga_campos_select($select_lista_precio, array_listas_precio,elemento_seleccionado, cadena_elemento_cero);
					
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo='<option value="0">[-- Seleccionar M&eacute;todo  --]</option>';
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(entry['Cliente']['0']['metodo_pago_id'] == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '"  selected="yes">' + metodo['titulo'] + '</option>';
						}else{
							hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
							
					
					//carga select dias de credito
					$select_dias_credito.children().remove();
					var hmtl_condiciones = '<option value="0">[-- D&iacute;as de Cr&eacute;dito --]</option>';
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['Cliente']['0']['dias_credito_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
						}
					});
					$select_dias_credito.append(hmtl_condiciones);
					
					
					//credito suspendido
					var html = '';
					var sel_suspendido='';
					var sel_no_suspendido='';
					
					$select_credito_suspendido.children().remove();
					if(entry['Cliente']['0']['credito_suspendido']=='true'){
						sel_suspendido='selected="yes"';
					}else{
						sel_no_suspendido='selected="yes"';
					}
					html += '<option value="false" ' + sel_no_suspendido+ '>No</option>';
					html += '<option value="true" ' + sel_suspendido + '>Si</option>';
					$select_credito_suspendido.append(html);
					
					//carga el select de Inicios de Credito
					$select_inicio_credito.children().remove();
					var inicio_hmtl = '<option value="0">[--Seleccionar inicio --]</option>';
					$.each(entry['InicioCredito'],function(entryIndex,inicio){
						if(entry['Cliente']['0']['credito_a_partir'] == inicio['id']){
							inicio_hmtl += '<option value="' + inicio['id'] + '"  selected="yes">' + inicio['titulo'] + '</option>';
						}else{
							inicio_hmtl += '<option value="' + inicio['id'] + '"  >' + inicio['titulo'] + '</option>';
						}
					});
					$select_inicio_credito.append(inicio_hmtl);
					
					//carga el select de tipos de Embarque
					$select_tipo_embarque.children().remove();
					var embarque_hmtl = '<option value="0" >[--Seleccionar tipo --]</option>';
					$.each(entry['TiposEmbarque'],function(entryIndex,tipo){
						if(entry['Cliente']['0']['cxp_prov_tipo_embarque_id'] == tipo['id']){
							embarque_hmtl += '<option value="' + tipo['id'] + '" selected="yes" >' + tipo['titulo'] + '</option>';
						}else{
							embarque_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
						}
					});
					$select_tipo_embarque.append(embarque_hmtl);
					
                   
					//Alimentando los campos select de las pais  del contacto compras
					$select_comp_pais.children().remove();
					var compras_pais_hmtl = '<option value="00">[-Seleccionar Pais-]</option>';
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Cliente']['0']['contacto_compras_pais_id']){
							compras_pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							compras_pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_comp_pais.append(compras_pais_hmtl);

					
					//Alimentando los campos select de estados del contacto compras
					$select_comp_estado.children().remove();
					var compras_entidad_hmtl = '<option value="00" >[-Seleccionar Estado-]</option>';
					$.each(entry['Entidades'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Cliente']['0']['contacto_compras_estado_id']){
							compras_entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							compras_entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_comp_estado.append(compras_entidad_hmtl);
					
					
					//Alimentando los campos select de los municipios
					$select_comp_localidad.children().remove();
					var compras_localidad_hmtl = '<option value="00">[-Seleccionar Municipio-]</option>';
					$.each(entry['Localidades'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Cliente']['0']['contacto_compras_municipio_id']){
							compras_localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							compras_localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_comp_localidad.append(compras_localidad_hmtl);

					
					

					//Alimentando los campos select de las pais  del contacto pagos
					$select_pag_pais.children().remove();
					var pagos_pais_hmtl = '<option value="00"  >[-Seleccionar Pais-]</option>';
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Cliente']['0']['contacto_pagos_pais_id']){
							pagos_pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pagos_pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_pag_pais.append(pagos_pais_hmtl);

					
					//Alimentando los campos select de estados del contacto pagos
					$select_pag_estado.children().remove();
					var pagos_entidad_hmtl = '<option value="00">[-Seleccionar Estado-]</option>';
					$.each(entry['Entidades'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Cliente']['0']['contacto_pagos_estado_id']){
							pagos_entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							pagos_entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_pag_estado.append(pagos_entidad_hmtl);
					
					
					//Alimentando los campos select de los municipios del contacto pagos
					$select_pag_localidad.children().remove();
					var pagos_localidad_hmtl = '<option value="00">[-Seleccionar Municipio-]</option>';
					$.each(entry['Localidades'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Cliente']['0']['contacto_pagos_municipio_id']){
							pagos_localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							pagos_localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_pag_localidad.append(pagos_localidad_hmtl);

                            


					//carga select estados al cambiar el pais
					$select_pais.change(function(){
						var valor_pais = $(this).val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
						$arreglo = {'id_pais':valor_pais};
						$.post(input_json,$arreglo,function(entry){
							$select_entidad.children().remove();
							var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar Estado-]</option>'
							$.each(entry['Entidades'],function(entryIndex,entidad){
								entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
							});
							$select_entidad.append(entidad_hmtl);
							var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
							$select_localidad.children().remove();
							$select_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					
					//carga select municipios al cambiar el estado
					$select_entidad.change(function(){
						var valor_entidad = $(this).val();
						var valor_pais = $select_pais.val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
						$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
						$.post(input_json,$arreglo,function(entry){
							$select_localidad.children().remove();
							var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
							$.each(entry['Localidades'],function(entryIndex,mun){
								trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
							});
							$select_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});

					
					//carga select estados al cambiar el pais
					$select_comp_pais.change(function(){
						var valor_pais = $(this).val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
						$arreglo = {'id_pais':valor_pais};
						$.post(input_json,$arreglo,function(entry){
							$select_comp_estado.children().remove();
							var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar Estado-]</option>'
							$.each(entry['Entidades'],function(entryIndex,entidad){
								entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
							});
							$select_comp_estado.append(entidad_hmtl);
							var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Municipio' + '</option>';
							$select_comp_localidad.children().remove();
							$select_comp_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					
					//carga select municipios al cambiar el estado
					$select_comp_estado.change(function(){
						var valor_entidad = $(this).val();
						var valor_pais = $select_comp_pais.val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
						$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
						$.post(input_json,$arreglo,function(entry){
							$select_comp_localidad.children().remove();
							var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar Municipio-]</option>'
							$.each(entry['Localidades'],function(entryIndex,mun){
								trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
							});
							$select_comp_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
                    
					//carga select estados al cambiar el pais
					$select_pag_pais.change(function(){
						var valor_pais = $(this).val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
						$arreglo = {'id_pais':valor_pais};
						$.post(input_json,$arreglo,function(entry){
							$select_pag_estado.children().remove();
							var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar Estado-]</option>'
							$.each(entry['Entidades'],function(entryIndex,entidad){
								entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
							});
							$select_pag_estado.append(entidad_hmtl);
							var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Municipio' + '</option>';
							$select_pag_localidad.children().remove();
							$select_pag_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					
					//carga select municipios al cambiar el estado
					$select_pag_estado.change(function(){
						var valor_entidad = $(this).val();
						var valor_pais = $select_pag_pais.val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
						$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
						$.post(input_json,$arreglo,function(entry){
							$select_pag_localidad.children().remove();
							var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar Municipio-]</option>'
							$.each(entry['Localidades'],function(entryIndex,mun){
								trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
							});
							$select_pag_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					
					
					//si hay direcciones entra aqui y carga el grid
					if(entry['Direcciones']['0'] != null){
						$chkbox_consignacion.attr('checked',true);
						$campo_consignacion.val(1);
						$div_consignacion_grid.css({'display':'block'});
						$agrega_direccion.css({'display':'block'});
						$('#forma-clients-window').find('.clients_div_one').css({'height':'345px'});
						$('#forma-clients-window').find('.clients_div_three').css({'height':'355px'});
						$('#forma-clients-window').find('.clients_div_one').css({'width':'780px'});
						$('#forma-clients-window').find('.clients_div_two').css({'width':'770px'});
						$('#forma-clients-window').find('.clients_div_three').css({'width':'754px'});
						$('#forma-clients-window').find('#cierra').css({'width':'730px'});
						$('#forma-clients-window').find('#botones').css({'width':'770px'});
						
						$.each(entry['Direcciones'],function(entryIndex,Dir){
							var trCount = $("tr", $tabla_datos_consignacion).size();
							trCount++;
							var dir_loc_alt = " ";
							var dir_fax = " ";
							var tr='';
							tr = '<tr>';
								tr += '<td width="55">';
									tr += '<a href="elimina">Eliminar</a>';
									tr += '<input type="hidden" name="dc_eliminado" id="elim" value="1">';
								tr += '</td>';
								tr += '<td width="645" class="dir" id="dir'+ trCount +'">';
									tr += '<input type="hidden" name="fila'+ trCount +'"        id="fil" 	value="'+trCount+'">';
									tr += '<input type="hidden" name="dc_calle"                 id="cal" 	value="'+ Dir['calle'] +'">';
									tr += '<input type="hidden" name="dc_numero"                id="num" 	value="'+ Dir['numero'] +'">';
									tr += '<input type="hidden" name="dc_colonia"               id="col" 	value="'+ Dir['colonia'] +'">';
									tr += '<input type="hidden" name="dc_idlocalidad"           id="idloc" 	value="'+ Dir['id_localidad'] +'">';
									tr += '<input type="hidden" name="localidad'+ trCount +'"   id="loc" 	value="'+ Dir['localidad'] +'">';
									tr += '<input type="hidden" name="dc_identidad"             id="ident" 	value="'+ Dir['id_entidad'] +'">';
									tr += '<input type="hidden" name="entidad'+ trCount +'"     id="ent" 	value="'+ Dir['entidad'] +'">';
									tr += '<input type="hidden" name="dc_idpais"                id="idpa" 	value="'+ Dir['id_pais'] +'">';
									tr += '<input type="hidden" name="pais'+ trCount +'"        id="pa" 	value="'+ Dir['pais'] +'">';
									tr += '<input type="hidden" name="dc_codigop"               id="cp" 	value="'+ Dir['cp'] +'">';
									tr += '<input type="hidden" name="dc_telefono"              id="tel" 	value="'+ Dir['telefono'] +'">';
									
									if (Dir['localidad_alternativa'] != null && Dir['localidad_alternativa'] !=""){
										dir_loc_alt = Dir['localidad_alternativa'];
										//alert("Entra en loc alternativa");
									}
									
									if (Dir['fax'] !=null && Dir['fax']!=""){
										//alert("entra en fax");
										dir_fax = Dir['fax'];
									}
									
									tr += '<input type="hidden" name="dc_localternativa"        id="localt" value="'+ dir_loc_alt +'">';
									tr += '<input type="hidden" name="dc_numfax"                id="fax" 	value="'+ dir_fax +'">';
									tr += '<span id="direccion'+ trCount +'">'+ Dir['calle'] +' No.'+ Dir['numero'] +', Col. '+ Dir['colonia'] +', '+ Dir['localidad'] +', '+ Dir['entidad'] +', '+ Dir['pais'] +' C.P. '+ Dir['cp'] +'</span>';
								tr += '</td>';
							tr += '</tr>';
							
							//crea la nueva fila
							$tabla_datos_consignacion.append(tr);
						});
						
						$tabla_datos_consignacion.find('tr:odd').css({ 'background-color' : '#e7e8ea'});
						$tabla_datos_consignacion.find('tr:even').css({ 'background-color' : '#FFFFFF'});
						
						$('tr:odd' , $tabla_datos_consignacion).hover(function () {
							$(this).find('td').css({ background : '#FBD850'});
						}, function() {
							$(this).find('td').css({'background-color':'#e7e8ea'});
						});
						$('tr:even' , $tabla_datos_consignacion).hover(function () {
							$(this).find('td').css({'background-color':'#FBD850'});
						}, function() {
							$(this).find('td').css({'background-color':'#FFFFFF'});
						});
						
						$tabla_datos_consignacion.find('a').bind('click',function(event){
							event.preventDefault();
							$(this).parent().parent().find('#elim').val(0);
							$(this).parent().parent().hide();
						});
						
						//seleccionar una direccion para editar
						$tabla_datos_consignacion.find('tr').each(function (index){
							$(this).find(".dir").click(function(event){
								var numFila = $(this).find('#fil').val();
								var pais = $(this).find('#idpa').val();
								var entidad = $(this).find('#ident').val();
								var localidad = $(this).find('#idloc').val();
								var calle = $(this).find('#cal').val();
								var numero = $(this).find('#num').val();
								var colonia = $(this).find('#col').val();
								var cp = $(this).find('#cp').val();
								var telefono = $(this).find('#tel').val();
								var localternativa = $(this).find('#localt').val();
								var fax = $(this).find('#fax').val();
								$forma_direccion_consignacion(numFila,pais,entidad,localidad,calle,numero,colonia,cp,telefono,localternativa,fax);
							});
						});
					}
				},"json");//termina llamada json
				
				//cambiar select empresa immex
				$select_immex.change(function(){
					var valor = $(this).val();
					if(valor=='true'){
						//si esta desahabilitado, hay que habilitarlo para permitir la captura de la tasa
						if($retencion_immex.is(':disabled')) {
							$retencion_immex.removeAttr('disabled');
						}
					}else{
						//si no esta deshabilitado, hay que deshabilitarlo
						if(!$retencion_immex.is(':disabled')) {
							$retencion_immex.val('0.00');
							$retencion_immex.attr('disabled','-1');
						}
					}
				});
						
				
				$chkbox_consignacion.bind('click',function(event){
					if($(this).is(':checked')){
						$campo_consignacion.val(1);
						$div_consignacion_grid.css({'display':'block'});
						$agrega_direccion.css({'display':'block'});
						$('#forma-clients-window').find('.clients_div_one').css({'height':'565px'});
						//$('#forma-clients-window').find('.clients_div_three').css({'height':'355px'});
						$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
						$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
						$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
						$('#forma-clients-window').find('#cierra').css({'width':'765px'});
						$('#forma-clients-window').find('#botones').css({'width':'790px'});
					}else{
						$campo_consignacion.val(0);
						$div_consignacion_grid.css({'display':'none'});
						$agrega_direccion.css({'display':'none'});
						$('#forma-clients-window').find('.clients_div_one').css({'height':'470px'});
						$('#forma-clients-window').find('.clients_div_three').css({'height':'260px'});
						//$('#forma-clients-window').find('.clients_div_three').css({'height':'355px'});
						$('#forma-clients-window').find('.clients_div_one').css({'width':'810px'});
						$('#forma-clients-window').find('.clients_div_two').css({'width':'810px'});
						$('#forma-clients-window').find('.clients_div_three').css({'width':'800px'});
						$('#forma-clients-window').find('#cierra').css({'width':'765px'});
						$('#forma-clients-window').find('#botones').css({'width':'790px'});
					}
				});
				
				
				//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$campo_limite_credito.focus(function(e){
					if(parseFloat($campo_limite_credito.val())<1){
						$campo_limite_credito.val('');
					}
				});
				
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$campo_limite_credito.blur(function(e){
					if(parseFloat($campo_limite_credito.val())==0||$campo_limite_credito.val()==""){
						$campo_limite_credito.val(0.00);
					}
				});	
				
				$campo_limite_credito.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$campo_caducidad_cotizacion.focus(function(e){
					if(parseFloat($campo_caducidad_cotizacion.val())<1){
						$campo_caducidad_cotizacion.val('');
					}
				});
				
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$campo_caducidad_cotizacion.blur(function(e){
					if(parseFloat($campo_caducidad_cotizacion.val())==0||$campo_caducidad_cotizacion.val()==""){
						$campo_caducidad_cotizacion.val(0);
					}
				});	
				
				$campo_caducidad_cotizacion.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				
				//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
				$retencion_immex.focus(function(e){
					if(parseFloat($retencion_immex.val())<1){
						$retencion_immex.val('');
					}
				});
				
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$retencion_immex.blur(function(e){
					if(parseFloat($retencion_immex.val())==0||$retencion_immex.val()==""){
						$retencion_immex.val(0);
					}
				});
				
				$retencion_immex.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				$cuenta_mn.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				$cuenta_usd.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				//agrega nueva direccion de consignacion
				$agrega_direccion.click(function(event){
					event.preventDefault();
					$forma_direccion_consignacion(numFila,pais,entidad,localidad,calle,numero,colonia,cp,telefono,localternativa,fax);
				});
				
				$submit_actualizar.bind('click',function(){
					var trCount = $("tr", $tabla_datos_consignacion).size();
					$total_tr.val(trCount);
					
					//alert("Controller: "+controller +"\n"+input_json+"\n"+document.location.protocol + '//' + document.location.host);
					if($chkbox_consignacion.is(':checked')){
						if(parseInt(trCount)<1){
							jAlert("Debes ingresar por lo menos una direccion de consignacion para el cliente.", 'Atencion!');
							return false;
						}else{
							return true;
						}
					}else{
						return true;
					}
				});
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-clients-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-clients-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				$campo_rfc.focus();
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getClients.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getClients.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaClients00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
            
			if(parseInt(rolVendedor)>=1){
				//SI no es Administrador ocultar iconos de eliminar en el grid
				$('#lienzo_recalculable').find('a.cancelar_item').hide();
			}
        },"json");
    }
	
    $get_datos_grid();
    
    
});



