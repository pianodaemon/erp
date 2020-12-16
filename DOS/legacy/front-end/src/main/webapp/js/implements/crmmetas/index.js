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
	var controller = $contextpath.val()+"/controllers/crmmetas";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	
        var $new_crmmetas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Registro de Metas');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_agente]');
        var $busqueda_mes = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_mes]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limbuscarpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
        
	var html = '';
	$busqueda_mes.children().remove();
            html='<option value="0">[-- Todos --]</option>';
            html+='<option value="1">Enero</option>';
            html+='<option value="2">Febrero</option>';
            html+='<option value="3">Marzo</option>';
            html+='<option value="4">Abril</option>';
            html+='<option value="5">Mayo</option>';
            html+='<option value="6">Junio</option>';
            html+='<option value="7">Julio</option>';
            html+='<option value="8">Agosto</option>';
            html+='<option value="9">Septiembre</option>';
            html+='<option value="10">Octubre</option>';
            html+='<option value="11">Noviembre</option>';
            html+='<option value="12">Diciembre</option>';
        
	$busqueda_mes.append(html);
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
                valor_retorno += "agente" + signo_separador + $busqueda_agente.val() + "|";
                //valor_retorno +="mes"+signo_separador +$busqueda_mes.val() + "|";
                //valor_retorno += "descripcion" + signo_separador + $busqueda_titulo.val()+ "|";
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
	
	
	
	$limbuscarpiar.click(function(event){
		event.preventDefault();
                $busqueda_mes.val(' ');
                
                //esto se hace para reinicar los valores del select de agentes
		var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
		$arreglo2 = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json2,$arreglo2,function(data){
			//Alimentando los campos select_agente
			$busqueda_agente.children().remove();
			var agente_hmtl = '';
			if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
				agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
			}
			
			$.each(data['Agentes'],function(entryIndex,agente){
				if(parseInt(agente['id'])==parseInt(data['Extra'][0]['id_agente'])){
					agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
				}else{
					//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
					if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
						agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
					}
				}
			});
			$busqueda_agente.append(agente_hmtl);
		});
	});
        
        //Alimentando el select del mes
                                        
        var array_meses = {
            0:"- Seleccionar -",  
            1:"Enero",  
            2:"Febrero", 
            3:"Marzo", 
            4:"Abirl", 
            5:"Mayo", 
            6:"Junio", 
            7:"Julio", 
            8:"Agosto", 
            9:"Septiembre", 
            10:"Octubre", 
            11:"Noviembre", 
            12:"Diciembre"
        };

       
	var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
		//Alimentando los campos select_agente
		$busqueda_agente.children().remove();
		var agente_hmtl = '';
		if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
			agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
		}
		
		$.each(data['Agentes'],function(entryIndex,agente){
			if(parseInt(agente['id'])==parseInt(data['Extra'][0]['id_agente'])){
				agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
			}else{
				//si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
				if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
					agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
				}
			}
		});
		$busqueda_agente.append(agente_hmtl);
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
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-metas-window').find('#submit').mouseover(function(){
			$('#forma-metas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-metas-window').find('#submit').mouseout(function(){
			$('#forma-metas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-metas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-metas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-metas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-metas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-metas-window').find('#close').mouseover(function(){
			$('#forma-metas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-metas-window').find('#close').mouseout(function(){
			$('#forma-metas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-metas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-metas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-metas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-metas-window').find("ul.pestanas li").click(function() {
			$('#forma-metas-window').find(".contenidoPes").hide();
			$('#forma-metas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-metas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
        
        //muestra la fecha actual
        var mostrarano = function Mostrarano(){
                var ahora = new Date();
                var anoActual = ahora.getFullYear();
                var mesActual = ahora.getMonth();
                mesActual = mesActual+1;
                mesActual = (mesActual <= 9)?"0" + mesActual : mesActual;
                var diaActual = ahora.getDate();
                diaActual = (diaActual <= 9)?"0" + diaActual : diaActual;
                var Fecha = anoActual;		
                return Fecha;
        }
	//nuevos puestos
	$new_crmmetas.click(function(event){
            
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_CrmMetas();   //llamada al plug in 
		
		var form_to_show = 'formaCrmMetas';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-metas-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-metas-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
                
		//campos de la vista
		var $campo_id = $('#forma-metas-window').find('input[name=identificador]'); 
                var $folio =$('#forma-metas-window').find('input[name=folio]');
                var $ano =$('#forma-metas-window').find('input[name=ano]');
                var $select_mes =$('#forma-metas-window').find('select[name=mes]');
                var $select_agente =$('#forma-metas-window').find('select[name=agente]');
                var $select_opciones =$('#forma-metas-window').find('select[name=opciones]');
                var $cantidad_visitas =$('#forma-metas-window').find('input[name=cant_visitas]');
                var $cantidad_llamadas =$('#forma-metas-window').find('input[name=cant_llamadas]');
                var $cantidad_prospectos =$('#forma-metas-window').find('input[name=cant_prospectos]');
                
                //por prospecto
                var $cant_cotizaciones =$('#forma-metas-window').find('input[name=cant_cotizaciones]');
                var $cant_oportunidades =$('#forma-metas-window').find('input[name=cant_oportunidades]');
                var $monto_cotizaciones=$('#forma-metas-window').find('input[name=monto_cotizaciones]');
                var $monto_oportunidades=$('#forma-metas-window').find('input[name=monto_oportunidades]');
                var $ventas_prospecto=$('#forma-metas-window').find('input[name=ventas_prospecto]');
                
                //por cliente
                var $cantidad_cotizaciones=$('#forma-metas-window').find('input[name=cant_cotizaciones2]');
                var $cantidad_oportunidades=$('#forma-metas-window').find('input[name=cant_oportunidades2]');
                var $montos_cotizaciones=$('#forma-metas-window').find('input[name=monto_cotizaciones2]');
                var $montos_oportunidades=$('#forma-metas-window').find('input[name=monto_oportunidades2]');
                var $ventas_clientes=$('#forma-metas-window').find('input[name=ventas_clientes]');
                var $ventas_oportunidades=$('#forma-metas-window').find('input[name=ventas_opor_clientes]');
                
                //fieldset ocultos
                var $div_prospectos =$('#forma-metas-window').find('#prospectos');
		var $div_clientes =$('#forma-metas-window').find('#clientes');
                
                			
		//botones		
		var $cerrar_plugin = $('#forma-metas-window').find('#close');
		var $cancelar_plugin = $('#forma-metas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-metas-window').find('#submit');
                
                
		
                $folio.css({'background' : '#DDDDDD'});   
		$campo_id.attr({'value' : 0});
                $ano.attr({'value':2013});
                //dandole valores por defecto a los campos
                $cantidad_llamadas.val(0);
                $cantidad_visitas.val(0);
                $cantidad_prospectos.val(0);
                
                $cant_cotizaciones.val(0);
                $cant_oportunidades.val(0);
                $monto_cotizaciones.val(0);
                $monto_oportunidades.val(0);
                $ventas_prospecto.val(0);
       
                $cantidad_cotizaciones.val(0)
                $cantidad_oportunidades.val(0)
                $montos_cotizaciones.val(0);
                $montos_oportunidades.val(0);
                $ventas_clientes.val(0);
                $ventas_oportunidades.val(0);
                //validaciones de los campos 
                
                
                $ano.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                
                //elimina cero al hacer clic sobre el campo
                $ano.focus(function(e){
                        if(parseFloat($ano.val())>0){
                                $ano.val('');
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $ano.blur(function(){
                        if($ano.val()==""){
                                $ano.val(mostrarano());//si el campo esta en blanco, pone cero
                        }
                });
                
                //+++++++++++++++++++++++++ validando los campos+++++++++++++++++++++++++++++++++++
                $cantidad_llamadas.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                
                $cantidad_visitas.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                
                $cantidad_prospectos.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                
                $cant_cotizaciones.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                $cant_oportunidades.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                $monto_cotizaciones.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                
                $monto_oportunidades.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                $ventas_prospecto.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                $cantidad_cotizaciones.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                $cantidad_oportunidades.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                $montos_cotizaciones.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                $montos_oportunidades.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                $ventas_clientes.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                $ventas_oportunidades.keypress(function(e){
                        // Permitir  numeros, borrar, suprimir, TAB, puntos, comas
                        if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
                                return true;
                        }else {
                                return false;
                        }		
                });
                //******************************fin de las validaciones de los campos************************************
               
               //****************************** Evento focus ****************************************
                //elimina cero al hacer clic sobre el campo
                $cantidad_llamadas.focus(function(e){
                        if(parseFloat($cantidad_llamadas.val())<1){
                                $cantidad_llamadas.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $cantidad_visitas.focus(function(e){
                        if(parseFloat($cantidad_visitas.val())<1){
                                $cantidad_visitas.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $cantidad_prospectos.focus(function(e){
                        if(parseFloat($cantidad_prospectos.val())<1){
                                $cantidad_prospectos.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $cant_cotizaciones.focus(function(e){
                        if(parseFloat($cant_cotizaciones.val())<1){
                                $cant_cotizaciones.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $cant_oportunidades.focus(function(e){
                        if(parseFloat($cant_oportunidades.val())<1){
                                $cant_oportunidades.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $monto_cotizaciones.focus(function(e){
                        if(parseFloat($monto_cotizaciones.val())<1){
                                $monto_cotizaciones.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $monto_oportunidades.focus(function(e){
                        if(parseFloat($monto_oportunidades.val())<1){
                                $monto_oportunidades.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $ventas_prospecto.focus(function(e){
                        if(parseFloat($ventas_prospecto.val())<1){
                                $ventas_prospecto.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $cantidad_cotizaciones.focus(function(e){
                        if(parseFloat($cantidad_cotizaciones.val())<1){
                                $cantidad_cotizaciones.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $cantidad_oportunidades.focus(function(e){
                        if(parseFloat($cantidad_oportunidades.val())<1){
                                $cantidad_oportunidades.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $montos_cotizaciones.focus(function(e){
                        if(parseFloat($montos_cotizaciones.val())<1){
                                $montos_cotizaciones.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $montos_oportunidades.focus(function(e){
                        if(parseFloat($montos_oportunidades.val())<1){
                                $montos_oportunidades.val('');
                        }
                });
                
                
                //elimina cero al hacer clic sobre el campo
                $ventas_clientes.focus(function(e){
                        if(parseFloat($ventas_clientes.val())<1){
                                $ventas_clientes.val('');
                        }
                });
                
                //elimina cero al hacer clic sobre el campo
                $ventas_oportunidades.focus(function(e){
                        if(parseFloat($ventas_oportunidades.val())<1){
                                $ventas_oportunidades.val('');
                        }
                });
                
               //**************************************fin evento focus***************************************
               
               //-------------------------------evento blur-----------------------------------------
               
                //coloca 0 al perder el foco del campo
                $cantidad_llamadas.blur(function(){
                        if($cantidad_llamadas.val()=="" || parseFloat($cantidad_llamadas.val())==0){
                                $cantidad_llamadas.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $cantidad_visitas.blur(function(){
                        if($cantidad_visitas.val()=="" || parseFloat($cantidad_visitas.val())==0){
                                $cantidad_visitas.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $cantidad_prospectos.blur(function(){
                        if($cantidad_prospectos.val()=="" || parseFloat($cantidad_prospectos.val())==0){
                                $cantidad_prospectos.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $cant_cotizaciones.blur(function(){
                        if($cant_cotizaciones.val()=="" || parseFloat($cant_cotizaciones.val())==0){
                                $cant_cotizaciones.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $cant_oportunidades.blur(function(){
                        if($cant_oportunidades.val()=="" || parseFloat($cant_oportunidades.val())==0){
                                $cant_oportunidades.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $monto_oportunidades.blur(function(){
                        if($monto_oportunidades.val()=="" || parseFloat($monto_oportunidades.val())==0){
                                $monto_oportunidades.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $monto_cotizaciones.blur(function(){
                        if($monto_cotizaciones.val()=="" || parseFloat($monto_cotizaciones.val())==0){
                                $monto_cotizaciones.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $ventas_prospecto.blur(function(){
                        if($ventas_prospecto.val()=="" || parseFloat($ventas_prospecto.val())==0){
                                $ventas_prospecto.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $cantidad_cotizaciones.blur(function(){
                        if($cantidad_cotizaciones.val()=="" || parseFloat($cantidad_cotizaciones.val())==0){
                                $cantidad_cotizaciones.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $cantidad_oportunidades.blur(function(){
                        if($cantidad_oportunidades.val()=="" || parseFloat($cantidad_oportunidades.val())==0){
                                $cantidad_oportunidades.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $montos_cotizaciones.blur(function(){
                        if($montos_cotizaciones.val()=="" || parseFloat($montos_cotizaciones.val())==0){
                                $montos_cotizaciones.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $montos_oportunidades.blur(function(){
                        if($montos_oportunidades.val()=="" || parseFloat($montos_oportunidades.val())==0){
                                $montos_oportunidades.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $ventas_clientes.blur(function(){
                        if($ventas_clientes.val()=="" || parseFloat($ventas_clientes.val())==0){
                                $ventas_clientes.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //coloca 0 al perder el foco del campo
                $ventas_oportunidades.blur(function(){
                        if($ventas_oportunidades.val()=="" || parseFloat($ventas_oportunidades.val())==0){
                                $ventas_oportunidades.val("0");//si el campo esta en blanco, pone cero
                        }
                });
                
                //alert($cantidad_oportunidades.val());
               //----------------------------------fin evento blur-------------------------------------------
                
                
                
                
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Meta fue dada de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-metas-overlay').fadeOut(remove);
			
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-metas-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
                                            $('#forma-metas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                            .parent()
                                            .css({'display':'block'})
                                            .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
                
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMetas.json';
		var parametros={
                    id:$campo_id.val(),
                    iu: $('#lienzo_recalculable').find('input[name=iu]').val()
                }
                //alert($('#lienzo_recalculable').find('input[name=iu]').val())
                $.post(input_json,parametros,function(entry){
                    
                  
                                
                    //cargar select del Mes inicial
                    $select_mes.children().remove();
                    var select_html = '';
                    for(var i in array_meses){
                            if(parseInt(i) == 1 ){
                                    select_html += '<option value="' + i + '" selected="yes">' + array_meses[i] + '</option>';	
                            }else{
                                    select_html += '<option value="' + i + '"  >' + array_meses[i] + '</option>';	
                            }
                    }
                    $select_mes.append(select_html);
                        
                    //Alimentando los campos select_agente
                    $select_agente.children().remove();
			var agente_hmtl = '';
			if(parseInt(entry['Extra'][0]['exis_rol_admin']) > 0){
				agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
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
                    
                    //select de las opciones de visualizacion "cliente" y "prospecto"
                    $select_opciones.children().remove();
                    var opciones_html='<option value="0" selected="yes">--Seleccione Visualizaci&oacute;n--</option>';
                        opciones_html +='<option value="1">Prospectos</option>';
                        opciones_html +='<option value="2">Clientes</option>';
                    $select_opciones.append(opciones_html);
                    
                    
                    $select_opciones.change(function(){
                        if($select_opciones.val()==1){
                            
                            //prospectos
                            $('#forma-metas-window').find('#prospectos').css({'display':'block'});
                            $('#forma-metas-window').find('.metas_div_one').css({'height':'460px'});
                            $('#forma-metas-window').find('.metas_div_one').css({'width':'870px'});
                            $('#forma-metas-window').find('.metas_div_two').css({'width':'28px'});
                            $('#forma-metas-window').find('.metas_div_three').css({'width':'280px'});
                            $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                            $('#forma-metas-window').find('#botones').css({'width':'890px'});
                            
                            //clientes
                            $('#forma-metas-window').find('#clientes').css({'display':'none'});
                            $('#forma-metas-window').find('.metas_div_one').css({'height':'460px'});
                            $('#forma-metas-window').find('.metas_div_one').css({'width':'910px'});
                            $('#forma-metas-window').find('.metas_div_two').css({'width':'910px'});
                            $('#forma-metas-window').find('.metas_div_three').css({'width':'900px'});
                            $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                            $('#forma-metas-window').find('#botones').css({'width':'890px'});
                            
                            
                            
                        }else if($select_opciones.val()==2){
                            $('#forma-metas-window').find('#clientes').css({'display':'block'});
                            $('#forma-metas-window').find('.metas_div_one').css({'height':'480px'});
                            $('#forma-metas-window').find('.metas_div_one').css({'width':'910px'});
                            $('#forma-metas-window').find('.metas_div_two').css({'width':'910px'});
                            $('#forma-metas-window').find('.metas_div_three').css({'width':'910px'});
                            $('#forma-metas-window').find('#cierra').css({'width':'745px'});
                            $('#forma-metas-window').find('#botones').css({'width':'790px'});
                            
                            $('#forma-metas-window').find('#prospectos').css({'display':'none'});
                            $('#forma-metas-window').find('.metas_div_one').css({'height':'480px'});
                            $('#forma-metas-window').find('.metas_div_one').css({'width':'910px'});
                            $('#forma-metas-window').find('.metas_div_two').css({'width':'910px'});
                            $('#forma-metas-window').find('.metas_div_three').css({'width':'900px'});
                            $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                            $('#forma-metas-window').find('#botones').css({'width':'890px'});
                        }else{
                            //prospectos
                            $('#forma-metas-window').find('#prospectos').css({'display':'none'});
                            $('#forma-metas-window').find('.metas_div_one').css({'height':'290px'});
                            $('#forma-metas-window').find('.metas_div_one').css({'width':'870px'});
                            $('#forma-metas-window').find('.metas_div_two').css({'width':'28px'});
                            $('#forma-metas-window').find('.metas_div_three').css({'width':'280px'});
                            $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                            $('#forma-metas-window').find('#botones').css({'width':'890px'});
                            
                            //clientes
                            $('#forma-metas-window').find('#clientes').css({'display':'none'});
                            $('#forma-metas-window').find('.metas_div_one').css({'height':'290px'});
                            $('#forma-metas-window').find('.metas_div_one').css({'width':'910px'});
                            $('#forma-metas-window').find('.metas_div_two').css({'width':'910px'});
                            $('#forma-metas-window').find('.metas_div_three').css({'width':'900px'});
                            $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                            $('#forma-metas-window').find('#botones').css({'width':'890px'});
                        }
                    });
                    
        
        
                  
                });//termina llamada json
                                 

                
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-metas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-metas-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
	});
        
        var carga_formaClientsgrupos_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
                                    'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                                    };
			jConfirm('Realmente desea eliminar el registro de metas seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El registro de metas fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El registro de metas no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaCrmMetas';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_CrmMetas();
			$('#forma-metas-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-metas-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//campos de la vista
                        var $campo_id = $('#forma-metas-window').find('input[name=identificador]'); 
                        var $folio =$('#forma-metas-window').find('input[name=folio]');
                        var $ano =$('#forma-metas-window').find('input[name=ano]');
                        var $select_mes =$('#forma-metas-window').find('select[name=mes]');
                        var $select_agente =$('#forma-metas-window').find('select[name=agente]');
                        var $select_opciones =$('#forma-metas-window').find('select[name=opciones]');
                        var $cantidad_visitas =$('#forma-metas-window').find('input[name=cant_visitas]');
                        var $cantidad_llamadas =$('#forma-metas-window').find('input[name=cant_llamadas]');
                        var $cantidad_prospectos =$('#forma-metas-window').find('input[name=cant_prospectos]');

                        //por prospecto
                        var $cant_cotizaciones =$('#forma-metas-window').find('input[name=cant_cotizaciones]');
                        var $cant_oportunidades =$('#forma-metas-window').find('input[name=cant_oportunidades]');
                        var $monto_cotizaciones=$('#forma-metas-window').find('input[name=monto_cotizaciones]');
                        var $monto_oportunidades=$('#forma-metas-window').find('input[name=monto_oportunidades]');
                        var $ventas_prospecto=$('#forma-metas-window').find('input[name=ventas_prospecto]');

                        //por cliente
                        var $cantidad_cotizaciones=$('#forma-metas-window').find('input[name=cant_cotizaciones2]');
                        var $cantidad_oportunidades=$('#forma-metas-window').find('input[name=cant_oportunidades2]');
                        
                        var $montos_cotizaciones=$('#forma-metas-window').find('input[name=monto_cotizaciones2]');
                        var $montos_oportunidades=$('#forma-metas-window').find('input[name=monto_oportunidades2]');
                        var $ventas_clientes=$('#forma-metas-window').find('input[name=ventas_clientes]');
                        var $ventas_oportunidades=$('#forma-metas-window').find('input[name=ventas_opor_clientes]');

                        //fieldset ocultos
                        var $div_prospectos =$('#forma-metas-window').find('#prospectos');
                        var $div_clientes =$('#forma-metas-window').find('#clientes');


                        //botones		
                        var $cerrar_plugin = $('#forma-metas-window').find('#close');
                        var $cancelar_plugin = $('#forma-metas-window').find('#boton_cancelar');
                        var $submit_actualizar = $('#forma-metas-window').find('#submit');



                        $folio.css({'background' : '#DDDDDD'});   
                       
			
			
		
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMetas.json';
				
                                $arreglo = {
                                    
                                    id:id_to_show,
                                    iu: $('#lienzo_recalculable').find('input[name=iu]').val()
                                };
                                
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-metas-overlay').fadeOut(remove);
						jAlert("El Registro de Metas se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-metas-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 2 ){
								$('#forma-metas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
                                    
                                    
                                        $campo_id.attr({'value' : entry['Datos']['0']['id']});
					$folio.attr({'value' : entry['Datos']['0']['folio']});
					$ano.attr({'value' : entry['Datos']['0']['ano']});
					$cantidad_visitas.attr({'value' : entry['Datos']['0']['cantidad_visitas']});
                                        $cantidad_llamadas.attr({'value' : entry['Datos']['0']['cantidad_llamadas']});
                                        $cantidad_prospectos.attr({'value' : entry['Datos']['0']['cantidad_prospectos']});
                                        //prospectos
                                        $cant_cotizaciones.attr({'value' : entry['Datos']['0']['cantidad_cotizaciones']});
                                        $cant_oportunidades.attr({'value' : entry['Datos']['0']['cantidad_oportunidades']});
                                        $monto_cotizaciones.attr({'value' : entry['Datos']['0']['monto_cotizaciones']});
                                        $monto_oportunidades.attr({'value' : entry['Datos']['0']['monto_oportunidades']});
                                        $ventas_prospecto.attr({'value' : entry['Datos']['0']['ventas_prospectos']});
                                        //clientes
                                        $cantidad_cotizaciones.attr({'value' : entry['Datos']['0']['cant_cotizaciones']});
                                        $cantidad_oportunidades.attr({'value' : entry['Datos']['0']['cant_oportunidades']});
                                        $montos_cotizaciones.attr({'value' : entry['Datos']['0']['montos_cotizaciones']});
                                        $montos_oportunidades.attr({'value' : entry['Datos']['0']['montos_oportunidades']});
                                        $ventas_clientes.attr({'value' : entry['Datos']['0']['ventas_clientes']});
                                        $ventas_oportunidades.attr({'value' : entry['Datos']['0']['ventas_oportunidades_clientes']});
				
					//Alimentando los campos select_agente
					$select_agente.children().remove();
					var agente_hmtl='';
					$.each(entry['Agentes'],function(entryIndex,agente){
						if(parseInt(agente['id'])==parseInt(entry['Datos'][0]['empleado_id'])){
							agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
						}else{
							agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
						}
					});
					$select_agente.append(agente_hmtl);
					
                                        //select de las opciones de visualizacion "cliente" y "prospecto"
                                        $select_opciones.children().remove();
                                        var opciones_html='<option value="0" selected="yes">--Seleccione Visualizaci&oacute;n--</option>';
                                            opciones_html +='<option value="1">Prospectos</option>';
                                            opciones_html +='<option value="2">Clientes</option>';
                                        $select_opciones.append(opciones_html);


                                        $select_opciones.change(function(){
                                            if($select_opciones.val()==1){

                                                //prospectos
                                                $('#forma-metas-window').find('#prospectos').css({'display':'block'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'height':'460px'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'width':'870px'});
                                                $('#forma-metas-window').find('.metas_div_two').css({'width':'28px'});
                                                $('#forma-metas-window').find('.metas_div_three').css({'width':'280px'});
                                                $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                                                $('#forma-metas-window').find('#botones').css({'width':'890px'});

                                                //clientes
                                                $('#forma-metas-window').find('#clientes').css({'display':'none'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'height':'460px'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'width':'910px'});
                                                $('#forma-metas-window').find('.metas_div_two').css({'width':'910px'});
                                                $('#forma-metas-window').find('.metas_div_three').css({'width':'900px'});
                                                $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                                                $('#forma-metas-window').find('#botones').css({'width':'890px'});



                                            }else if($select_opciones.val()==2){
                                                $('#forma-metas-window').find('#clientes').css({'display':'block'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'height':'480px'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'width':'910px'});
                                                $('#forma-metas-window').find('.metas_div_two').css({'width':'910px'});
                                                $('#forma-metas-window').find('.metas_div_three').css({'width':'910px'});
                                                $('#forma-metas-window').find('#cierra').css({'width':'745px'});
                                                $('#forma-metas-window').find('#botones').css({'width':'790px'});

                                                $('#forma-metas-window').find('#prospectos').css({'display':'none'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'height':'480px'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'width':'910px'});
                                                $('#forma-metas-window').find('.metas_div_two').css({'width':'910px'});
                                                $('#forma-metas-window').find('.metas_div_three').css({'width':'900px'});
                                                $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                                                $('#forma-metas-window').find('#botones').css({'width':'890px'});
                                            }else{
                                                //prospectos
                                                $('#forma-metas-window').find('#prospectos').css({'display':'none'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'height':'290px'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'width':'870px'});
                                                $('#forma-metas-window').find('.metas_div_two').css({'width':'28px'});
                                                $('#forma-metas-window').find('.metas_div_three').css({'width':'280px'});
                                                $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                                                $('#forma-metas-window').find('#botones').css({'width':'890px'});

                                                //clientes
                                                $('#forma-metas-window').find('#clientes').css({'display':'none'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'height':'290px'});
                                                $('#forma-metas-window').find('.metas_div_one').css({'width':'910px'});
                                                $('#forma-metas-window').find('.metas_div_two').css({'width':'910px'});
                                                $('#forma-metas-window').find('.metas_div_three').css({'width':'900px'});
                                                $('#forma-metas-window').find('#cierra').css({'width':'865px'});
                                                $('#forma-metas-window').find('#botones').css({'width':'890px'});
                                            }
                                        });

					var array = {
                                             
                                            1:"Enero",  
                                            2:"Febrero", 
                                            3:"Marzo", 
                                            4:"Abirl", 
                                            5:"Mayo", 
                                            6:"Junio", 
                                            7:"Julio", 
                                            8:"Agosto", 
                                            9:"Septiembre", 
                                            10:"Octubre", 
                                            11:"Noviembre", 
                                            12:"Diciembre"
                                        };
                                        
					
                                        
                                        var i=$select_mes.val();
                                        var select_html='';
                                        $select_mes.children().remove();
                                        while (i<=12){
                                                if(parseInt(entry['Datos'][0]['mes']) == parseInt(i) ){
                                                        select_html += '<option value="' + i + '" selected="yes">' + array[i] + '</option>';
                                                }else{
                                                        
                                                        select_html += '<option value="' + i + '" >' + array[i] + '</option>';
                                                }
                                                i=i+1;
                                        }
                                        $select_mes.append(select_html);
                                        
                                       
					
				},"json");//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-metas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-metas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                        }
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllCodigos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllCodigos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaClientsgrupos_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});
