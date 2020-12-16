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
	var controller = $contextpath.val()+"/controllers/teschequera";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_chequera = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Chequera');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	var $cadena_busqueda = "";
	var $busqueda_nombre = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_nombre]');
	var $busqueda_select_banco = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_banco]');
	
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTesChera.json';
		$arreglo = {'id':0,
			'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
		};
        $.post(input_json,$arreglo,function(entry){
        $busqueda_select_banco.children().remove();
            var banco_hmtl = '';
            var banco_hmtl = '<option value="0" selected="yes">[-Seleccionar un Banco-]</option>';
            $.each(entry['Bancos'],function(entryIndex,banco){
                banco_hmtl += '<option value="' + banco['id_banco'] + '"  >' + banco['banco'] + '</option>';
            });
        $busqueda_select_banco.append(banco_hmtl);
        });
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "id_banco" + signo_separador + $busqueda_select_banco.val() + "|";
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
		var  banco_hmtl = '';
		banco_hmtl = '<option value="0" selected="yes">[-Seleccionar un Banco-]</option>';
		$busqueda_select_banco.append(banco_hmtl);
                
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
		
		$('#forma-chequeras-window').find('#submit').mouseover(function(){
			$('#forma-chequeras-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-chequeras-window').find('#submit').mouseout(function(){
			$('#forma-chequeras-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-chequeras-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-chequeras-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-chequeras-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-chequeras-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-chequeras-window').find('#close').mouseover(function(){
			$('#forma-chequeras-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-chequeras-window').find('#close').mouseout(function(){
			$('#forma-chequeras-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-chequeras-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-chequeras-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-chequeras-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-chequeras-window').find("ul.pestanas li").click(function() {
			$('#forma-chequeras-window').find(".contenidoPes").hide();
			$('#forma-chequeras-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-chequeras-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			
			if(activeTab == '#tabx-1'){
				$('#forma-chequeras-window').find('.chequeras_div_one').css({'height':'300px'});
				$('#forma-chequeras-window').find('.chequeras_div_one').css({'width':'610px'});
				$('#forma-chequeras-window').find('.chequeras_div_two').css({'width':'610px'});
				$('#forma-chequeras-window').find('#cierra').css({'width':'560px'});
				$('#forma-chequeras-window').find('#botones').css({'width':'579px'});
			}
			if(activeTab == '#tabx-2'){
				$('#forma-chequeras-window').find('.chequeras_div_one').css({'height':'340px'});
				$('#forma-chequeras-window').find('.chequeras_div_one').css({'width':'610px'});
				$('#forma-chequeras-window').find('.chequeras_div_two').css({'width':'610px'});
				$('#forma-chequeras-window').find('#cierra').css({'width':'560px'});
				$('#forma-chequeras-window').find('#botones').css({'width':'579px'});
			}
			if(activeTab == '#tabx-3'){
				$('#forma-chequeras-window').find('.chequeras_div_one').css({'height':'308px'});
				$('#forma-chequeras-window').find('.chequeras_div_one').css({'width':'610px'});
				$('#forma-chequeras-window').find('.chequeras_div_two').css({'width':'610px'});
				$('#forma-chequeras-window').find('#cierra').css({'width':'560px'});
				$('#forma-chequeras-window').find('#botones').css({'width':'579px'});
			}
			
			if(activeTab == '#tabx-4'){
				$('#forma-chequeras-window').find('.chequeras_div_one').css({'height':'200px'});
				$('#forma-chequeras-window').find('.chequeras_div_one').css({'width':'810px'});
				$('#forma-chequeras-window').find('.chequeras_div_two').css({'width':'810px'});
				$('#forma-chequeras-window').find('#cierra').css({'width':'765px'});
				$('#forma-chequeras-window').find('#botones').css({'width':'790px'});
			}
			return false;
		});

	}
	
	
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
						$('#forma-chequeras-window').find('input[name=id_cta_activo]').val(id_cta);
						$('#forma-chequeras-window').find('input[name=ac_cuenta]').val(cta);
						$('#forma-chequeras-window').find('input[name=ac_scuenta]').val(scta);
						$('#forma-chequeras-window').find('input[name=ac_sscuenta]').val(sscta);
						$('#forma-chequeras-window').find('input[name=ac_ssscuenta]').val(ssscta);
						$('#forma-chequeras-window').find('input[name=ac_sssscuenta]').val(sssscta);
						$('#forma-chequeras-window').find('input[name=descripcion_ac]').val(desc);
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

	
	
	
	//nuevo centro de costo
	$new_chequera.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_chequera();
		
		var form_to_show = 'formachequera';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		$('#forma-chequeras-window').css({ "margin-left": -300, 	"margin-top": -200 });
		$forma_selected.prependTo('#forma-chequeras-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		//variables Pestaña (Chequera)
		var $campo_id = $('#forma-chequeras-window').find('input[name=identificador]');
		
		var $chequera = $('#forma-chequeras-window').find('input[name=chequera]');
		var $select_moneda = $('#forma-chequeras-window').find('select[name=moneda]');
		var $check_modificar_consecutivo = $('#forma-chequeras-window').find('input[name=check_modificar_consecutivo]');
		var $check_modificar_fecha = $('#forma-chequeras-window').find('input[name=check_modificar_fecha]');
		var $check_modificar_cheque = $('#forma-chequeras-window').find('input[name=check_modificar_cheque]');
		var $check_imprimir_chequeningles = $('#forma-chequeras-window').find('input[name=check_imprimir_chequeningles]');
		//fin de variables Pestaña (Chequera)
                
                
		//variables Pestaña Dos (Datos)
		var $select_banco = $('#forma-chequeras-window').find('select[name=select_banco]');
		var $campo_numero_sucursal = $('#forma-chequeras-window').find('input[name=numero_sucursal]');
		var $campo_nombre_sucursal = $('#forma-chequeras-window').find('input[name=nombre_sucursal]');
		var $campo_calle = $('#forma-chequeras-window').find('input[name=calle]');
		var $campo_numero = $('#forma-chequeras-window').find('input[name=numero]');
		var $campo_colonia = $('#forma-chequeras-window').find('input[name=colonia]');
		var $campo_cp = $('#forma-chequeras-window').find('input[name=cp]');
		var $select_pais = $('#forma-chequeras-window').find('select[name=pais]');
		var $select_entidad = $('#forma-chequeras-window').find('select[name=estado]');
		var $select_municipio = $('#forma-chequeras-window').find('select[name=municipio]');
		//fin de variable de pestana  (Datos)
                
		//// variable de pestana tres (Otros)
		var $campo_tel1 = $('#forma-chequeras-window').find('input[name=tel1]');
		var $campo_ext1 = $('#forma-chequeras-window').find('input[name=ext1]');
		var $campo_tel2 = $('#forma-chequeras-window').find('input[name=tel2]');
		var $campo_ext2 = $('#forma-chequeras-window').find('input[name=ext2]');
		var $campo_fax = $('#forma-chequeras-window').find('input[name=fax]');
		var $campo_gerente = $('#forma-chequeras-window').find('input[name=gerente]');
		var $campo_ejecutivo= $('#forma-chequeras-window').find('input[name=ejecutivo]');
		var $campo_email = $('#forma-chequeras-window').find('input[name=email]');
		//fin de variables de la pestaña (Otros)
        
        
        var $pestana_contabilidad = $('#forma-chequeras-window').find('ul.pestanas').find('a[href*=#tabx-4]');
        
		var $id_cta_activo = $('#forma-chequeras-window').find('input[name=id_cta_activo]');
		var $ac_cuenta = $('#forma-chequeras-window').find('input[name=ac_cuenta]');
		var $ac_scuenta = $('#forma-chequeras-window').find('input[name=ac_scuenta]');
		var $ac_sscuenta = $('#forma-chequeras-window').find('input[name=ac_sscuenta]');
		var $ac_ssscuenta = $('#forma-chequeras-window').find('input[name=ac_ssscuenta]');
		var $ac_sssscuenta = $('#forma-chequeras-window').find('input[name=ac_sssscuenta]');
		var $descripcion_ac = $('#forma-chequeras-window').find('input[name=descripcion_ac]');
		
		var $busca_activo = $('#forma-chequeras-window').find('a[href=busca_activo]');
		var $limpiar_activo = $('#forma-chequeras-window').find('a[href=limpiar_activo]');
		
		var $cerrar_plugin = $('#forma-chequeras-window').find('#close');
		var $cancelar_plugin = $('#forma-chequeras-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-chequeras-window').find('#submit');
		
		$campo_id.attr({ 'value' : 0 });
		$ac_cuenta.hide();
		$ac_scuenta.hide();
		$ac_sscuenta.hide();
		$ac_ssscuenta.hide();
		$ac_sssscuenta.hide();
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Chequera fue dada de alta con exito", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-chequeras-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-chequeras-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-chequeras-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTesChera.json';
		$arreglo = {'id':0,
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		$.post(input_json,$arreglo,function(entry){
			
			if( entry['Extras'][0]['inc_ctb']=='false' ){
				$pestana_contabilidad.parent().hide();
			}else{
				//visualizar subcuentas de acuerdo al nivel definido para la empresa
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $ac_cuenta.show(); };
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $ac_scuenta.show(); };
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $ac_sscuenta.show(); };
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ac_ssscuenta.show(); };
				if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $ac_sssscuenta.show(); };
				
				//busca Cuenta Activo
				$busca_activo.click(function(event){
					event.preventDefault();
					$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
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
				
			}
			
			
			$select_banco.children().remove();
			var banco_hmtl = '';
			var banco_hmtl = '<option value="0" selected="yes">[-Seleccionar un Banco-]</option>';
			$.each(entry['Bancos'],function(entryIndex,banco){
				banco_hmtl += '<option value="' + banco['id_banco'] + '"  >' + banco['banco'] + '</option>';
			});
			$select_banco.append(banco_hmtl);
			
			
			
			$select_moneda.children().remove();
				var moneda_hmtl = '';
				$.each(entry['Monedas'],function(entryIndex,moneda){
					moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
				});
			$select_moneda.append(moneda_hmtl);
			
			
			
			var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar entidad--]</option>';
			$select_entidad.children().remove();
			$select_entidad.append(entidad_hmtl);

			var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar municipio-]</option>';
			$select_municipio.children().remove();
			$select_municipio.append(localidad_hmtl);
                        
			//Alimentando los campos select de las pais
			$select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
			$.each(entry['Paises'],function(entryIndex,pais){
				pais_hmtl += '<option value="' + pais['id_pais'] + '"  >' + pais['pais'] + '</option>';
			});
			$select_pais.append(pais_hmtl);
                        
                        
                        
                                
                        
			//carga select estados al cambiar el pais
			$select_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$select_entidad.children().remove();
					var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['Entidades'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['id_Estado'] + '"  >' + entidad['Estado'] + '</option>';
					});
					$select_entidad.append(entidad_hmtl);
					//var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
					//$select_municipio.children().remove();
					//$select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			//carga select municipios al cambiar el estado
			$select_entidad.change(function(){
					var valor_entidad = $(this).val();
					var valor_pais = $select_pais.val();
					
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getmunicipios.json';
					$arreglo = {'id_pais':valor_pais,'id_entidad': valor_entidad};
					$.post(input_json,$arreglo,function(entry){
						$select_municipio.children().remove();
						var trama_hmtl_municipios = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
						$.each(entry['Municipios'],function(entryIndex,mun){
							trama_hmtl_municipios += '<option value="' + mun['id_municipio'] + '"  >' + mun['municipio'] + '</option>';
						});
						$select_municipio.append(trama_hmtl_municipios);
					},"json");//termina llamada json
			});
			
                        
		
		},"json");//termina llamada json
		

		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-chequeras-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-chequeras-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
                
                
                
		
	});
	
	
        
        
        
	
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la chequera seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Chequera fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La Chequera no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formachequera';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			
			$(this).modalPanel_chequera();
			$('#forma-chequeras-window').css({ "margin-left": -350, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-chequeras-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//variables Pestaña (Chequera)
			var $campo_id = $('#forma-chequeras-window').find('input[name=identificador]');
			var $chequera = $('#forma-chequeras-window').find('input[name=chequera]');
			var $select_moneda = $('#forma-chequeras-window').find('select[name=moneda]');
			var $check_modificar_consecutivo = $('#forma-chequeras-window').find('input[name=check_modificar_consecutivo]');
			var $check_modificar_fecha = $('#forma-chequeras-window').find('input[name=check_modificar_fecha]');
			var $check_modificar_cheque = $('#forma-chequeras-window').find('input[name=check_modificar_cheque]');
			var $check_imprimir_chequeningles = $('#forma-chequeras-window').find('input[name=check_imprimir_chequeningles]');
			//fin de variables Pestaña (Chequera)
			
			//variables Pestaña Dos (Datos)
			var $select_banco = $('#forma-chequeras-window').find('select[name=select_banco]');
			var $campo_numero_sucursal = $('#forma-chequeras-window').find('input[name=numero_sucursal]');
			var $campo_nombre_sucursal = $('#forma-chequeras-window').find('input[name=nombre_sucursal]');
			var $campo_calle = $('#forma-chequeras-window').find('input[name=calle]');
			var $campo_numero = $('#forma-chequeras-window').find('input[name=numero]');
			var $campo_colonia = $('#forma-chequeras-window').find('input[name=colonia]');
			var $campo_cp = $('#forma-chequeras-window').find('input[name=cp]');
			var $select_pais = $('#forma-chequeras-window').find('select[name=pais]');
			var $select_entidad = $('#forma-chequeras-window').find('select[name=estado]');
			var $select_municipio = $('#forma-chequeras-window').find('select[name=municipio]');
			//fin de variable de pestana  (Datos)

			//// variable de pestana tres (Otros)
			var $campo_tel1 = $('#forma-chequeras-window').find('input[name=tel1]');
			var $campo_ext1 = $('#forma-chequeras-window').find('input[name=ext1]');
			var $campo_tel2 = $('#forma-chequeras-window').find('input[name=tel2]');
			var $campo_ext2 = $('#forma-chequeras-window').find('input[name=ext2]');
			var $campo_fax = $('#forma-chequeras-window').find('input[name=fax]');
			var $campo_gerente = $('#forma-chequeras-window').find('input[name=gerente]');
			var $campo_ejecutivo= $('#forma-chequeras-window').find('input[name=ejecutivo]');
			var $campo_email = $('#forma-chequeras-window').find('input[name=email]');
			//fin de variables de la pestaña (Otros)
			
			var $pestana_contabilidad = $('#forma-chequeras-window').find('ul.pestanas').find('a[href*=#tabx-4]');
			
			var $id_cta_activo = $('#forma-chequeras-window').find('input[name=id_cta_activo]');
			var $ac_cuenta = $('#forma-chequeras-window').find('input[name=ac_cuenta]');
			var $ac_scuenta = $('#forma-chequeras-window').find('input[name=ac_scuenta]');
			var $ac_sscuenta = $('#forma-chequeras-window').find('input[name=ac_sscuenta]');
			var $ac_ssscuenta = $('#forma-chequeras-window').find('input[name=ac_ssscuenta]');
			var $ac_sssscuenta = $('#forma-chequeras-window').find('input[name=ac_sssscuenta]');
			var $descripcion_ac = $('#forma-chequeras-window').find('input[name=descripcion_ac]');
			
			var $busca_activo = $('#forma-chequeras-window').find('a[href=busca_activo]');
			var $limpiar_activo = $('#forma-chequeras-window').find('a[href=limpiar_activo]');
		
			var $cerrar_plugin = $('#forma-chequeras-window').find('#close');
			var $cancelar_plugin = $('#forma-chequeras-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-chequeras-window').find('#submit');
			
			$ac_cuenta.hide();
			$ac_scuenta.hide();
			$ac_sscuenta.hide();
			$ac_ssscuenta.hide();
			$ac_sssscuenta.hide();
		
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getTesChera.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-chequeras-overlay').fadeOut(remove);
						jAlert("Los datos de la Chequera se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-chequeras-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-chequeras-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					if( entry['Extras'][0]['inc_ctb']=='false' ){
						$pestana_contabilidad.parent().hide();
					}else{
						//visualizar subcuentas de acuerdo al nivel definido para la empresa
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $ac_cuenta.show(); };
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $ac_scuenta.show(); };
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $ac_sscuenta.show(); };
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $ac_ssscuenta.show(); };
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $ac_sssscuenta.show(); };
						
						$id_cta_activo.attr({ 'value' : entry['Contab'][0]['ac_id_cta'] });
						$ac_cuenta.attr({ 'value' : entry['Contab'][0]['ac_cta'] });
						$ac_scuenta.attr({ 'value' : entry['Contab'][0]['ac_subcta'] });
						$ac_sscuenta.attr({ 'value' : entry['Contab'][0]['ac_ssubcta'] });
						$ac_ssscuenta.attr({ 'value' : entry['Contab'][0]['ac_sssubcta'] });
						$ac_sssscuenta.attr({ 'value' : entry['Contab'][0]['ac_ssssubcta'] });
						$descripcion_ac.attr({ 'value' : entry['Contab'][0]['ac_descripcion'] });
						
						//Busca Cuenta Activo
						$busca_activo.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
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
					}
					
					
					$campo_id.attr({ 'value' : entry['Chequera']['0']['id'] });
					$chequera.attr({ 'value' : entry['Chequera']['0']['chequera'] });
					//$select_moneda
					$check_modificar_consecutivo.attr('checked',  (entry['Chequera']['0']['chk_modificar_consecutivo'] == 'true')? true:false );
					$check_modificar_fecha.attr('checked',  (entry['Chequera']['0']['chk_modificar_fecha'] == 'true')? true:false );
					$check_modificar_cheque.attr('checked',  (entry['Chequera']['0']['chk_modificar_cheque'] == 'true')? true:false );
					$check_imprimir_chequeningles.attr('checked',  (entry['Chequera']['0']['chk_imp_cheque_ingles'] == 'true')? true:false );
					
					//$select_banco
					$campo_numero_sucursal.attr({ 'value' : entry['Chequera']['0']['numero_sucursal'] });
					$campo_nombre_sucursal.attr({ 'value' : entry['Chequera']['0']['nombre_sucursal'] });
					$campo_calle.attr({ 'value' : entry['Chequera']['0']['calle'] });
					$campo_numero.attr({ 'value' : entry['Chequera']['0']['numero'] });
					$campo_colonia.attr({ 'value' : entry['Chequera']['0']['colonia'] });
					$campo_cp.attr({ 'value' : entry['Chequera']['0']['codigo_postal'] });
					//$select_pais
					//$select_entidad
					//$select_municipio
					
					$campo_tel1.attr({ 'value' : entry['Chequera']['0']['telefono1'] });
					$campo_ext1.attr({ 'value' : entry['Chequera']['0']['extencion1'] });
					$campo_tel2.attr({ 'value' : entry['Chequera']['0']['telefono2'] });
					$campo_ext2.attr({ 'value' : entry['Chequera']['0']['extencion2'] });
					$campo_fax.attr({ 'value' : entry['Chequera']['0']['fax'] });
					
					$campo_gerente.attr({ 'value' : entry['Chequera']['0']['gerente'] });
					$campo_ejecutivo.attr({ 'value' : entry['Chequera']['0']['ejecutivo'] });
					$campo_email.attr({ 'value' : entry['Chequera']['0']['email'] });
					
					
					//Alimentando los campos select de las pais
					$select_pais.children().remove();
					var pais_hmtl = "";
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['id_pais'] == entry['Chequera']['0']['pais_id']){
							pais_hmtl += '<option value="' + pais['id_pais'] + '"  selected="yes">' + pais['pais'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['id_pais'] + '"  >' + pais['pais'] + '</option>';
						}
					});
					$select_pais.append(pais_hmtl);
					$select_pais.val();
					//Alimentando los campos select del estado
					$select_entidad.children().remove();
					var entidad_hmtl = "";
					$.each(entry['Entidades'],function(entryIndex,entidad){
						if(entidad['id_Estado'] == entry['Chequera']['0']['estado_id']){
							entidad_hmtl += '<option value="' + entidad['id_Estado'] + '"  selected="yes">' + entidad['Estado'] + '</option>';
						}else{
							entidad_hmtl += '<option value="' + entidad['id_Estado'] + '"  >' + entidad['Estado'] + '</option>';
						}
					});
					$select_entidad.append(entidad_hmtl);
					$select_entidad.val()
					
					//Alimentando los campos select de los municipios
					$select_municipio.children().remove();
					var localidad_hmtl = "";
					$.each(entry['Municipios'],function(entryIndex,mun){
						if(mun['id_municipio'] == entry['Chequera']['0']['municipio_id']){
							localidad_hmtl += '<option value="' + mun['id_municipio'] + '"  selected="yes">' + mun['municipio'] + '</option>';
						}else{
							localidad_hmtl += '<option value="' + mun['id_municipio'] + '"  >' + mun['municipio'] + '</option>';
						}
					});
					$select_municipio.append(localidad_hmtl);
					$select_municipio.val();
                                        
                                        
						$select_moneda.children().remove();
						var moneda_hmtl = '';
						$.each(entry['Monedas'],function(entryIndex,mon){
							if(mon['id'] == entry['Chequera']['0']['moneda_id']){
								moneda_hmtl += '<option value="' + mon['id'] + '"  selected="yes">' + mon['descripcion'] + '</option>';
							}else{
								moneda_hmtl += '<option value="' + mon['id'] + '"  >' + mon['descripcion'] + '</option>';
							}
						});
						$select_moneda.append(moneda_hmtl);
					
                                        
                                        
						$select_banco.children().remove();
						var banco_hmtl = '';
						//var banco_hmtl = '<option value="0" selected="yes">[-Seleccionar un Banco-]</option>';
						$.each(entry['Bancos'],function(entryIndex,banco){
							//banco_hmtl += '<option value="' + banco['id_banco'] + '"  >' + banco['banco'] + '</option>';
							if(banco['id_banco'] == entry['Chequera']['0']['banco_id']){
								banco_hmtl += '<option value="' + banco['id_banco'] + '"  selected="yes">' + banco['banco'] + '</option>';
							}else{
								banco_hmtl += '<option value="' + banco['id_banco'] + '"  >' + banco['banco'] + '</option>';
							}
					   });
						$select_banco.append(banco_hmtl);
                                        
				},"json");//termina llamada json
				
				
				
				
				$submit_actualizar.bind('click',function(){
					
				});
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-chequeras-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-chequeras-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllTesChequera.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllTesChequera.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



