$(function() {
    //var controller = "com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/proveedores";
    
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
    var controller = $contextpath.val()+"/controllers/proveedores";
	
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_proveedor = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Proveedores');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
    var $cadena_busqueda = "";
    var $campo_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=cadena_busqueda]');
    var $campo_busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=folio]');
    var $campo_rfc_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=rfc]');
	
    var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
    var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
    var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "cadena_busqueda" + signo_separador + $campo_busqueda.val() + "|";
		valor_retorno += "folio" + signo_separador + $campo_busqueda_folio.val() + "|";
		valor_retorno += "por_rfc" + signo_separador + $campo_rfc_busqueda.val() + "|";
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
        $campo_busqueda.val("");
        $campo_busqueda_folio.val("");
        $campo_rfc_busqueda.val("");
        $campo_busqueda_folio.focus();
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
		$campo_busqueda_folio.focus();
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_busqueda, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($campo_rfc_busqueda, $buscar);
	
	
    //datos para el buscador
    //$arreglo = {}
    //var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/data_buscador/out.json';
    //alimenta campos buscador
    
	
    $tabs_li_funxionalidad = function(){
		$('#forma-proveedors-window').find('#submit').mouseover(function(){
			$('#forma-proveedors-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-proveedors-window').find('#submit').mouseout(function(){
			$('#forma-proveedors-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		
		$('#forma-proveedors-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-proveedors-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		});
		$('#forma-proveedors-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-proveedors-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-proveedors-window').find('#close').mouseover(function(){
			$('#forma-proveedors-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-proveedors-window').find('#close').mouseout(function(){
			$('#forma-proveedors-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})

		$('#forma-proveedors-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-proveedors-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-proveedors-window').find(".contenidoPes:first").show(); //Show first tab content

		//On Click Event
		$('#forma-proveedors-window').find("ul.pestanas li").click(function() {
			$('#forma-proveedors-window').find(".contenidoPes").hide();
			$('#forma-proveedors-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-proveedors-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			/*
			if(activeTab == '#tabx-1'){
				$('#forma-proveedors-window').find('.proveedors_div_one').css({'height':'465px'});
			}
			
			if(activeTab == '#tabx-2'){
				$('#forma-proveedors-window').find('.proveedors_div_one').css({'height':'330px'});
			}
			
			if(activeTab == '#tabx-3'){
				$('#forma-proveedors-window').find('.proveedors_div_one').css({'height':'270px'});
			}
			
			if(activeTab == '#tabx-4'){
				$('#forma-proveedors-window').find('.proveedors_div_one').css({'height':'360px'});
			}
			
			if(activeTab == '#tabx-5'){
				$('#forma-proveedors-window').find('.proveedors_div_one').css({'height':'360px'});
			}
			
			if(activeTab == '#tabx-6'){
				$('#forma-proveedors-window').find('.proveedors_div_one').css({'height':'285px'});
			}
			*/
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
		var clasifica=0;
		
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
		
		var ctamay_hmtl = '';
		
		if(parseInt(tipo)==1 ){
			//CTA_MAYOR=2 (Pasivo) 	CLASIFICA=1 (Pasivo Circulante)
			mayor_seleccionado=2; detalle=1; clasifica=1;
			$.each(arrayCtasMayor,function(entryIndex,ctamay){
				if (parseInt(mayor_seleccionado)== parseInt( ctamay['id']) ){
					ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['titulo'] + '</option>';
				}
			});
		};
		
		if(parseInt(tipo)==2 ){
			//CTA_MAYOR=1(Activo) o 5(Egresos)		CLASIFICA=1 (Pasivo Circulante o Costo de Ventas).  
			detalle=1; clasifica=1; detalle=1;
			$.each(arrayCtasMayor,function(entryIndex,ctamay){
				if (parseInt( ctamay['id'])==1 || parseInt( ctamay['id'])==5 ){
					ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['titulo'] + '</option>';
				}
			});
		};
		
		if(parseInt(tipo)==3 ){
			//CTA_MAYOR=7 (IETU) 	CLASIFICA=1 (Impuesto IETU). 
			mayor_seleccionado=7; clasifica=2; detalle=1;
			$.each(arrayCtasMayor,function(entryIndex,ctamay){
				if (parseInt(mayor_seleccionado)== parseInt( ctamay['id']) ){
					ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['titulo'] + '</option>';
				}
			});
		};
		
		if(parseInt(tipo)==4 ){
			//CTA_MAYOR=2 (Pasivo)		CLASIFICA=1 (Pasivo Circulante)
			mayor_seleccionado=2; detalle=1; clasifica=1;
			$.each(arrayCtasMayor,function(entryIndex,ctamay){
				if (parseInt(mayor_seleccionado)== parseInt( ctamay['id']) ){
					ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['titulo'] + '</option>';
				}
			});
		};
		
		if(parseInt(tipo)==5 ){
			//CTA_MAYOR=2 (Pasivo)		CLASIFICA=1 (Pasivo Circulante)
			mayor_seleccionado=2; detalle=1; clasifica=1;
			$.each(arrayCtasMayor,function(entryIndex,ctamay){
				if (parseInt(mayor_seleccionado)== parseInt( ctamay['id']) ){
					ctamay_hmtl += '<option value="' + ctamay['id'] + '">'+ ctamay['titulo'] + '</option>';
				}
			});
		};
		
		//carga select de cuentas de Mayor
		$select_cta_mayor.children().remove();
		$select_cta_mayor.append(ctamay_hmtl);
		
		$campo_clasif.val(clasifica);
		
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
						$('#forma-proveedors-window').find('input[name=id_cta_pasivo]').val(id_cta);
						$('#forma-proveedors-window').find('input[name=pasivo_cuenta]').val(cta);
						$('#forma-proveedors-window').find('input[name=pasivo_scuenta]').val(scta);
						$('#forma-proveedors-window').find('input[name=pasivo_sscuenta]').val(sscta);
						$('#forma-proveedors-window').find('input[name=pasivo_ssscuenta]').val(ssscta);
						$('#forma-proveedors-window').find('input[name=pasivo_sssscuenta]').val(sssscta);
						$('#forma-proveedors-window').find('input[name=pasivo_descripcion]').val(desc);
					};
					
					if(parseInt(tipo)==2 ){ 
						$('#forma-proveedors-window').find('input[name=id_cta_egreso]').val(id_cta);
						$('#forma-proveedors-window').find('input[name=egreso_cuenta]').val(cta);
						$('#forma-proveedors-window').find('input[name=egreso_scuenta]').val(scta);
						$('#forma-proveedors-window').find('input[name=egreso_sscuenta]').val(sscta);
						$('#forma-proveedors-window').find('input[name=egreso_ssscuenta]').val(ssscta);
						$('#forma-proveedors-window').find('input[name=egreso_sssscuenta]').val(sssscta);
						$('#forma-proveedors-window').find('input[name=egreso_descripcion]').val(desc);
					};
					
					if(parseInt(tipo)==3 ){ 
						$('#forma-proveedors-window').find('input[name=id_cta_ietu]').val(id_cta);
						$('#forma-proveedors-window').find('input[name=ietu_cuenta]').val(cta);
						$('#forma-proveedors-window').find('input[name=ietu_scuenta]').val(scta);
						$('#forma-proveedors-window').find('input[name=ietu_sscuenta]').val(sscta);
						$('#forma-proveedors-window').find('input[name=ietu_ssscuenta]').val(ssscta);
						$('#forma-proveedors-window').find('input[name=ietu_sssscuenta]').val(sssscta);
						$('#forma-proveedors-window').find('input[name=ietu_descripcion]').val(desc);
					};
					
					if(parseInt(tipo)==4 ){ 
						$('#forma-proveedors-window').find('input[name=id_cta_complement]').val(id_cta);
						$('#forma-proveedors-window').find('input[name=complement_cuenta]').val(cta);
						$('#forma-proveedors-window').find('input[name=complement_scuenta]').val(scta);
						$('#forma-proveedors-window').find('input[name=complement_sscuenta]').val(sscta);
						$('#forma-proveedors-window').find('input[name=complement_ssscuenta]').val(ssscta);
						$('#forma-proveedors-window').find('input[name=complement_sssscuenta]').val(sssscta);
						$('#forma-proveedors-window').find('input[name=complement_descripcion]').val(desc);
					};
					
					if(parseInt(tipo)==5 ){ 
						$('#forma-proveedors-window').find('input[name=id_cta_pasivo_complement]').val(id_cta);
						$('#forma-proveedors-window').find('input[name=pasivo_complement_cuenta]').val(cta);
						$('#forma-proveedors-window').find('input[name=pasivo_complement_scuenta]').val(scta);
						$('#forma-proveedors-window').find('input[name=pasivo_complement_sscuenta]').val(sscta);
						$('#forma-proveedors-window').find('input[name=pasivo_complement_ssscuenta]').val(ssscta);
						$('#forma-proveedors-window').find('input[name=pasivo_complement_sssscuenta]').val(sssscta);
						$('#forma-proveedors-window').find('input[name=pasivo_complement_descripcion]').val(desc);
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
	}//termina buscador de Cuentas Contables
    
    
    
    

    $new_proveedor.click(function(event){
            event.preventDefault();
            var id_to_show = 0;

            $(this).modalPanel_proveedors();

            var form_to_show = 'formaProveedor00';
            $('#' + form_to_show).each (function(){   this.reset(); });
            var $forma_selected = $('#' + form_to_show).clone();
            $forma_selected.attr({ id : form_to_show + id_to_show });
            //var accion = "get_proveedor";

            $('#forma-proveedors-window').css({ "margin-left": -300, 	"margin-top": -200});

            $forma_selected.prependTo('#forma-proveedors-window');
            $forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});

            $tabs_li_funxionalidad();

            //var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + accion + '/' + id_to_show + '/out.json';
            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProveedor.json';
            $arreglo = {'id':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
            
			//tab1 Proveedor
			var $campo_id_proveedor = $('#forma-proveedors-window').find('input[name=id_proveedor]');
			var $campo_folio = $('#forma-proveedors-window').find('input[name=folio]');
			var $campo_rfc = $('#forma-proveedors-window').find('input[name=rfc]');
			var $campo_curp = $('#forma-proveedors-window').find('input[name=curp]');
			var $campo_raz_social = $('#forma-proveedors-window').find('input[name=raz_social]');
			var $campo_nombre_comercial = $('#forma-proveedors-window').find('input[name=nombre_comercial]');
			var $campo_calle = $('#forma-proveedors-window').find('input[name=calle]');
			var $campo_num_calle = $('#forma-proveedors-window').find('input[name=num_calle]');
			var $campo_colonia = $('#forma-proveedors-window').find('input[name=colonia]');
			var $campo_cp = $('#forma-proveedors-window').find('input[name=cp]');
			var $campo_entrecalles = $('#forma-proveedors-window').find('input[name=entrecalles]');
			var $select_pais = $('#forma-proveedors-window').find('select[name=pais]');
			var $select_estado = $('#forma-proveedors-window').find('select[name=estado]');
			var $select_localidad = $('#forma-proveedors-window').find('select[name=localidad]');
			var $campo_loc_alternativa = $('#forma-proveedors-window').find('input[name=loc_alternativa]');
			var $campo_tel1 = $('#forma-proveedors-window').find('input[name=tel1]');
			var $campo_ext1 = $('#forma-proveedors-window').find('input[name=ext1]');
			var $campo_fax = $('#forma-proveedors-window').find('input[name=fax]');
			var $campo_tel2 = $('#forma-proveedors-window').find('input[name=tel2]');
			var $campo_ext2 = $('#forma-proveedors-window').find('input[name=ext2]');
			var $campo_email = $('#forma-proveedors-window').find('input[name=email]');
			var $campo_pag_web = $('#forma-proveedors-window').find('input[name=pag_web]');
			var $select_impuesto = $('#forma-proveedors-window').find('select[name=impuesto]');
			
			
			//tab2 Datos
			var $select_zona = $('#forma-proveedors-window').find('select[name=zona]');
			var $select_grupo = $('#forma-proveedors-window').find('select[name=grupo]');
			var $select_prov_tipo = $('#forma-proveedors-window').find('select[name=prov_tipo]');
			var $select_clasif1 = $('#forma-proveedors-window').find('select[name=clasif1]');
			var $select_clasif2 = $('#forma-proveedors-window').find('select[name=clasif2]');
			var $select_clasif3 = $('#forma-proveedors-window').find('select[name=clasif3]');
			var $select_moneda = $('#forma-proveedors-window').find('select[name=moneda]');
			var $select_tiempo_entrega = $('#forma-proveedors-window').find('select[name=tentrega]');
			var $select_estatus = $('#forma-proveedors-window').find('select[name=estatus]');
			var $campo_comentarios = $('#forma-proveedors-window').find('textarea[name=comentarios]');
			var $check_transportista = $('#forma-proveedors-window').find('input[name=check_transportista]');
			
			//tab3 Credito
			var $campo_limite_credito = $('#forma-proveedors-window').find('input[name=limite_credito]');
			var $campo_descuento = $('#forma-proveedors-window').find('input[name=descuento]');
			var $select_inicio_credito = $('#forma-proveedors-window').find('select[name=inicio_credito]');
			var $select_credito = $('#forma-proveedors-window').find('select[name=credito]');
			var $select_tipo_embarque = $('#forma-proveedors-window').find('select[name=tipo_embarque]');
			var $select_flete = $('#forma-proveedors-window').find('select[name=flete]');
			var $txtarea_condiciones = $('#forma-proveedors-window').find('textarea[name=condiciones]');
			var $txtarea_observaciones = $('#forma-proveedors-window').find('textarea[name=observaciones]');
			
			
			//tab4 Contacto Ventas
			var $campo_vent_contacto = $('#forma-proveedors-window').find('input[name=vent_contacto]');
			var $campo_vent_puesto = $('#forma-proveedors-window').find('input[name=vent_puesto]');
			var $campo_vent_calle = $('#forma-proveedors-window').find('input[name=vent_calle]');
			var $campo_vent_numcalle = $('#forma-proveedors-window').find('input[name=vent_numcalle]');
			var $campo_vent_colonia = $('#forma-proveedors-window').find('input[name=vent_colonia]');
			var $campo_vent_cp = $('#forma-proveedors-window').find('input[name=vent_cp]');
			var $campo_vent_entrecalles = $('#forma-proveedors-window').find('input[name=vent_entrecalles]');
			var $select_vent_pais = $('#forma-proveedors-window').find('select[name=vent_pais]');
			var $select_vent_estado = $('#forma-proveedors-window').find('select[name=vent_estado]');
			var $select_vent_localidad = $('#forma-proveedors-window').find('select[name=vent_localidad]');
			var $campo_vent_tel1 = $('#forma-proveedors-window').find('input[name=vent_tel1]');
			var $campo_vent_ext1 = $('#forma-proveedors-window').find('input[name=vent_ext1]');
			var $campo_vent_fax = $('#forma-proveedors-window').find('input[name=vent_fax]');
			var $campo_vent_tel2 = $('#forma-proveedors-window').find('input[name=vent_tel2]');
			var $campo_vent_ext2 = $('#forma-proveedors-window').find('input[name=vent_ext2]');
			var $campo_vent_email = $('#forma-proveedors-window').find('input[name=vent_email]');
			
			
			//tab5 Contacto Cobranza
			var $campo_cob_contacto = $('#forma-proveedors-window').find('input[name=cob_contacto]');
			var $campo_cob_puesto = $('#forma-proveedors-window').find('input[name=cob_puesto]');
			var $campo_cob_calle = $('#forma-proveedors-window').find('input[name=cob_calle]');
			var $campo_cob_numcalle = $('#forma-proveedors-window').find('input[name=cob_numcalle]');
			var $campo_cob_colonia = $('#forma-proveedors-window').find('input[name=cob_colonia]');
			var $campo_cob_cp = $('#forma-proveedors-window').find('input[name=cob_cp]');
			var $campo_cob_entrecalles = $('#forma-proveedors-window').find('input[name=cob_entrecalles]');
			var $select_cob_pais = $('#forma-proveedors-window').find('select[name=cob_pais]');
			var $select_cob_estado = $('#forma-proveedors-window').find('select[name=cob_estado]');
			var $select_cob_localidad = $('#forma-proveedors-window').find('select[name=cob_localidad]');
			var $campo_cob_tel1 = $('#forma-proveedors-window').find('input[name=cob_tel1]');
			var $campo_cob_ext1 = $('#forma-proveedors-window').find('input[name=cob_ext1]');
			var $campo_cob_fax = $('#forma-proveedors-window').find('input[name=cob_fax]');
			var $campo_cob_tel2 = $('#forma-proveedors-window').find('input[name=cob_tel2]');
			var $campo_cob_ext2 = $('#forma-proveedors-window').find('input[name=cob_ext2]');
			var $campo_cob_email = $('#forma-proveedors-window').find('input[name=cob_email]');
			
			//tab6 Contabilidad
			var $pestana_contabilidad = $('#forma-proveedors-window').find('ul.pestanas').find('a[href*=#tabx-6]');
			
			var $id_cta_pasivo = $('#forma-proveedors-window').find('input[name=id_cta_pasivo]');
			var $pasivo_cuenta = $('#forma-proveedors-window').find('input[name=pasivo_cuenta]');
			var $pasivo_scuenta = $('#forma-proveedors-window').find('input[name=pasivo_scuenta]');
			var $pasivo_sscuenta = $('#forma-proveedors-window').find('input[name=pasivo_sscuenta]');
			var $pasivo_ssscuenta = $('#forma-proveedors-window').find('input[name=pasivo_ssscuenta]');
			var $pasivo_sssscuenta = $('#forma-proveedors-window').find('input[name=pasivo_sssscuenta]');
			var $pasivo_descripcion = $('#forma-proveedors-window').find('input[name=pasivo_descripcion]');
			
			var $id_cta_egreso = $('#forma-proveedors-window').find('input[name=id_cta_egreso]');
			var $egreso_cuenta = $('#forma-proveedors-window').find('input[name=egreso_cuenta]');
			var $egreso_scuenta = $('#forma-proveedors-window').find('input[name=egreso_scuenta]');
			var $egreso_sscuenta = $('#forma-proveedors-window').find('input[name=egreso_sscuenta]');
			var $egreso_ssscuenta = $('#forma-proveedors-window').find('input[name=egreso_ssscuenta]');
			var $egreso_sssscuenta = $('#forma-proveedors-window').find('input[name=egreso_sssscuenta]');
			var $egreso_descripcion = $('#forma-proveedors-window').find('input[name=egreso_descripcion]');
			
			var $id_cta_ietu = $('#forma-proveedors-window').find('input[name=id_cta_ietu]');
			var $ietu_cuenta = $('#forma-proveedors-window').find('input[name=ietu_cuenta]');
			var $ietu_scuenta = $('#forma-proveedors-window').find('input[name=ietu_scuenta]');
			var $ietu_sscuenta = $('#forma-proveedors-window').find('input[name=ietu_sscuenta]');
			var $ietu_ssscuenta = $('#forma-proveedors-window').find('input[name=ietu_ssscuenta]');
			var $ietu_sssscuenta = $('#forma-proveedors-window').find('input[name=ietu_sssscuenta]');
			var $ietu_descripcion = $('#forma-proveedors-window').find('input[name=ietu_descripcion]');
			
			var $id_cta_complement = $('#forma-proveedors-window').find('input[name=id_cta_complement]');
			var $complement_cuenta = $('#forma-proveedors-window').find('input[name=complement_cuenta]');
			var $complement_scuenta = $('#forma-proveedors-window').find('input[name=complement_scuenta]');
			var $complement_sscuenta = $('#forma-proveedors-window').find('input[name=complement_sscuenta]');
			var $complement_ssscuenta = $('#forma-proveedors-window').find('input[name=complement_ssscuenta]');
			var $complement_sssscuenta = $('#forma-proveedors-window').find('input[name=complement_sssscuenta]');
			var $complement_descripcion = $('#forma-proveedors-window').find('input[name=complement_descripcion]');
			
			var $id_cta_pasivo_complement = $('#forma-proveedors-window').find('input[name=id_cta_pasivo_complement]');
			var $pasivo_complement_cuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_cuenta]');
			var $pasivo_complement_scuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_scuenta]');
			var $pasivo_complement_sscuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_sscuenta]');
			var $pasivo_complement_ssscuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_ssscuenta]');
			var $pasivo_complement_sssscuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_sssscuenta]');
			var $pasivo_complement_descripcion = $('#forma-proveedors-window').find('input[name=pasivo_complement_descripcion]');
			
			var $pasivo_busca = $('#forma-proveedors-window').find('a[href=pasivo_busca]');
			var $egreso_busca = $('#forma-proveedors-window').find('a[href=egreso_busca]');
			var $ietu_busca = $('#forma-proveedors-window').find('a[href=ietu_busca]');
			var $complement_busca = $('#forma-proveedors-window').find('a[href=complement_busca]');
			var $pasivo_complement_busca = $('#forma-proveedors-window').find('a[href=pasivo_complement_busca]');
			
			var $pasivo_limpiar = $('#forma-proveedors-window').find('a[href=pasivo_limpiar]');
			var $egreso_limpiar = $('#forma-proveedors-window').find('a[href=egreso_limpiar]');
			var $ietu_limpiar = $('#forma-proveedors-window').find('a[href=ietu_limpiar]');
			var $complement_limpiar = $('#forma-proveedors-window').find('a[href=complement_limpiar]');
			var $pasivo_complement_limpiar = $('#forma-proveedors-window').find('a[href=pasivo_complement_limpiar]');
			
            var $cerrar_plugin = $('#forma-proveedors-window').find('#close');
            var $cancelar_plugin = $('#forma-proveedors-window').find('#boton_cancelar');
            
            //var $cancel_button = $('#forma-proveedors-window').find('input[value$=Cancelar]');
            $campo_folio.attr({ 'readOnly':true });
            $campo_id_proveedor.attr({ 'value' : 0 });
            $campo_folio.css({'background' : '#DDDDDD'});
            
			$pasivo_cuenta.hide();
			$pasivo_scuenta.hide();
			$pasivo_sscuenta.hide();
			$pasivo_ssscuenta.hide();
			$pasivo_sssscuenta.hide();
			
			$egreso_cuenta.hide();
			$egreso_scuenta.hide();
			$egreso_sscuenta.hide();
			$egreso_ssscuenta.hide();
			$egreso_sssscuenta.hide();
			
			$ietu_cuenta.hide();
			$ietu_scuenta.hide();
			$ietu_sscuenta.hide();
			$ietu_ssscuenta.hide();
			$ietu_sssscuenta.hide();
			
			$complement_cuenta.hide();
			$complement_scuenta.hide();
			$complement_sscuenta.hide();
			$complement_ssscuenta.hide();
			$complement_sssscuenta.hide();
			
			$pasivo_complement_cuenta.hide();
			$pasivo_complement_scuenta.hide();
			$pasivo_complement_sscuenta.hide();
			$pasivo_complement_ssscuenta.hide();
			$pasivo_complement_sssscuenta.hide();
		
            var respuestaProcesada = function(data){
                if ( data['success'] == "true" ){
                    jAlert("Proveedor dado de alta", 'Atencion!');
                    var remove = function() { $(this).remove(); };
                    $('#forma-proveedors-overlay').fadeOut(remove);
                    $get_datos_grid();
                }
                else{
                    // Desaparece todas las interrogaciones si es que existen
                    $('#forma-proveedors-window').find('div.interrogacion').css({'display':'none'});
                    var valor = data['success'].split('___');
                    //muestra las interrogaciones
                    for (var element in valor){
                        tmp = data['success'].split('___')[element];
                        longitud = tmp.split(':');
                        if( longitud.length > 1 ){
                            $('#forma-proveedors-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                            .parent()
                            .css({'display':'block'})
                            .easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
                        }
                    }				
                }
            }
            
            var options = { dataType :  'json', success : respuestaProcesada };
            $forma_selected.ajaxForm(options);
            
            
            //$.getJSON(json_string,function(entry){
            $.post(input_json,$arreglo,function(entry){
				if( entry['Extras'][0]['incluye_contab']=='false' ){
					$pestana_contabilidad.parent().hide();
				}else{
					//visualizar subcuentas de acuerdo al nivel definido para la empresa
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $pasivo_cuenta.show(); $egreso_cuenta.show(); $ietu_cuenta.show();  $complement_cuenta.show(); $pasivo_complement_cuenta.show();};
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $pasivo_scuenta.show(); $egreso_scuenta.show(); $ietu_scuenta.show(); $complement_scuenta.show(); $pasivo_complement_scuenta.show();};
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $pasivo_sscuenta.show(); $egreso_sscuenta.show(); $ietu_sscuenta.show(); $complement_sscuenta.show(); $pasivo_complement_sscuenta.show();};
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $pasivo_ssscuenta.show(); $egreso_ssscuenta.show(); $ietu_ssscuenta.show(); $complement_ssscuenta.show(); $pasivo_complement_ssscuenta.show();};
					if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $pasivo_sssscuenta.show(); $egreso_sssscuenta.show(); $ietu_sssscuenta.show(); $complement_sssscuenta.show(); $pasivo_complement_sssscuenta.show();};
					
					//busca Cuenta Pasivo
					$pasivo_busca.click(function(event){
						event.preventDefault();
						$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
					});
					
					//busca Cuenta Costo de Egreso
					$egreso_busca.click(function(event){
						event.preventDefault();
						$busca_cuentas_contables(2, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
					});
					
					//busca Cuenta IETU
					$ietu_busca.click(function(event){
						event.preventDefault();
						$busca_cuentas_contables(3, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
					});
					
					//busca Cuenta Complementaria
					$complement_busca.click(function(event){
						event.preventDefault();
						$busca_cuentas_contables(4, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
					});
					
					//busca Cuenta Pasivo Complementaria
					$pasivo_complement_busca.click(function(event){
						event.preventDefault();
						$busca_cuentas_contables(5, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
					});
					
					//limpiar campos Cuenta Pasivo
					$pasivo_limpiar.click(function(event){
						event.preventDefault();
						$id_cta_pasivo.val(0);
						$pasivo_cuenta.val('');
						$pasivo_scuenta.val('');
						$pasivo_sscuenta.val('');
						$pasivo_ssscuenta.val('');
						$pasivo_sssscuenta.val('');
						$pasivo_descripcion.val('');
					});
					
					//limpiar campos Cuenta de Egreso
					$egreso_limpiar.click(function(event){
						event.preventDefault();
						$id_cta_egreso.val(0);
						$egreso_cuenta.val('');
						$egreso_scuenta.val('');
						$egreso_sscuenta.val('');
						$egreso_ssscuenta.val('');
						$egreso_sssscuenta.val('');
						$egreso_descripcion.val('');
					});
					
					//limpiar campos IETU
					$ietu_limpiar.click(function(event){
						event.preventDefault();
						$id_cta_ietu.val(0);
						$ietu_cuenta.val('');
						$ietu_scuenta.val('');
						$ietu_sscuenta.val('');
						$ietu_ssscuenta.val('');
						$ietu_sssscuenta.val('');
						$ietu_descripcion.val('');
					});
					
					//limpiar campos Cuenta Complementaria
					$complement_limpiar.click(function(event){
						event.preventDefault();
						$id_cta_complement.val(0);
						$complement_cuenta.val('');
						$complement_scuenta.val('');
						$complement_sscuenta.val('');
						$complement_ssscuenta.val('');
						$complement_sssscuenta.val('');
						$complement_descripcion.val('');
					});
					
					//limpiar campos Cuenta Pasivo Complementaria
					$pasivo_complement_limpiar.click(function(event){
						event.preventDefault();
						$id_cta_pasivo_complement.val(0);
						$pasivo_complement_cuenta.val('');
						$pasivo_complement_scuenta.val('');
						$pasivo_complement_sscuenta.val('');
						$pasivo_complement_ssscuenta.val('');
						$pasivo_complement_sssscuenta.val('');
						$pasivo_complement_descripcion.val('');
					});
				}
				
				
			
				//Alimentando los campos select de las pais
				$select_pais.children().remove();
				var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
				$.each(entry['pais'],function(entryIndex,pais){
					pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
				});
				$select_pais.append(pais_hmtl);

				var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar entidad--]</option>';
				$select_estado.children().remove();
				$select_estado.append(entidad_hmtl);
				
				var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar municipio-]</option>';
				$select_localidad.children().remove();
				$select_localidad.append(localidad_hmtl);
				
				//carga select estados al cambiar el pais
				$select_pais.change(function(){
					var valor_pais = $(this).val();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
					$arreglo = {'id_pais':valor_pais};
					$.post(input_json,$arreglo,function(entry){
						$select_estado.children().remove();
						var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
						$.each(entry['entidades'],function(entryIndex,entidad){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						});
						$select_estado.append(entidad_hmtl);
						var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Seleccionar localidad' + '</option>';
						$select_localidad.children().remove();
						$select_localidad.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});


				//carga select municipios al cambiar el estado
				$select_estado.change(function(){
					var valor_entidad = $(this).val();
					var valor_pais = $select_pais.val();
					//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
					$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
					$.post(input_json,$arreglo,function(entry){
						$select_localidad.children().remove();
						var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
						$.each(entry['localidades'],function(entryIndex,mun){
							trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						});
						$select_localidad.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
				
				//impuesto
				$select_impuesto.children().remove();
				var impuesto_html = '<option value="0" selected="yes">[- Impuesto -]</option>';
				$.each(entry['Impuestos'],function(entryIndex,imp){
					impuesto_html += '<option value="' + imp['id'] + '"  >' + imp['descripcion'] + '</option>';
				});
				$select_impuesto.append(impuesto_html);
				
				
				//zona
				$select_zona.children().remove();
				var zona_html = '<option value="0" selected="yes">[-Seleccionar Zona-]</option>';
				$.each(entry['Zonas'],function(entryIndex,zona){
					zona_html += '<option value="' + zona['id'] + '"  >' + zona['nombre_zona'] + '</option>';
				});
				$select_zona.append(zona_html);
				
				//grupo
				$select_grupo.children().remove();
				var grupo_html = '<option value="0" selected="yes">[-Seleccionar Grupo-]</option>';
				$.each(entry['Grupos'],function(entryIndex,grupo){
					grupo_html += '<option value="' + grupo['id'] + '"  >' + grupo['nombre_grupo'] + '</option>';
				});
				$select_grupo.append(grupo_html);
				
				//tipo de proveedor
				$select_prov_tipo.children().remove();
				var tiposproov_html = '<option value="0" selected="yes">[-Tipo de proveedor-]</option>';
				$.each(entry['proveedor_tipo'],function(entryIndex,tipo){
					tiposproov_html += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
				});
				$select_prov_tipo.append(tiposproov_html);
				
				//clasificacion 1
				$select_clasif1.children().remove();
				var clasif1_html = '<option value="0" selected="yes">[-Clasificacion 1-]</option>';
				$.each(entry['Clas1'],function(entryIndex,clas1){
					clasif1_html += '<option value="' + clas1['id'] + '"  >' + clas1['clasificacion1'] + '</option>';
				});
				$select_clasif1.append(clasif1_html);
				
				//clasificacion 2
				$select_clasif2.children().remove();
				var clasif2_html = '<option value="0" selected="yes">[-Clasificacion 2-]</option>';
				$.each(entry['Clas2'],function(entryIndex,clas2){
					clasif2_html += '<option value="' + clas2['id'] + '"  >' + clas2['clasificacion2'] + '</option>';
				});
				$select_clasif2.append(clasif2_html);
				
				//clasificacion 3
				$select_clasif3.children().remove();
				var clasif3_html = '<option value="0" selected="yes">[-Clasificacion 3-]</option>';
				$.each(entry['Clas3'],function(entryIndex,clas3){
					clasif3_html += '<option value="' + clas3['id'] + '" >' + clas3['clasificacion3'] + '</option>';
				});
				$select_clasif3.append(clasif3_html);
				
				//carga select denominacion con todas las monedas
				$select_moneda.children().remove();
				var moneda_hmtl = '<option value="0" selected="yes">[-Seleccionar moneda-]</option>';
				$.each(entry['monedas'],function(entryIndex,moneda){
					moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
				});
				$select_moneda.append(moneda_hmtl);
				
				//alimentando select de tiempos de entrega
				$select_tiempo_entrega.children().remove();
				var tiempos_html = '<option value="0" selected="yes">[-Seleccionar tiempo-]</option>';
				$.each(entry['te'],function(entryIndex,tiempo){
					tiempos_html += '<option value="' + tiempo['id'] + '"  >' + tiempo['descripcion'] + '</option>';
				});
				$select_tiempo_entrega.append(tiempos_html);
				
				
				//estatus
				$select_estatus.children().remove();
				var status_html = '<option value="true" selected="yes">Activo</option>';
				status_html += '<option value="false">Inactivo</option>';
				$select_estatus.append(status_html);
				
				
				//flete
				$select_flete.children().remove();
				var flete_html = '<option value="true" selected="yes">Pagado</option>';
				flete_html += '<option value="false">Por pagar</option>';
				$select_flete.append(flete_html);
				
				
				//carga el select de dias de credito
				$select_credito.children().remove();
				var credito_hmtl = '<option value="0" selected="yes">[--Seleccionar Dias --]</option>';
				$.each(entry['DiasCredito'],function(entryIndex,credito){
					credito_hmtl += '<option value="' + credito['id'] + '"  >' + credito['descripcion'] + '</option>';
				});
				$select_credito.append(credito_hmtl);
				
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
				
				//pais contacto ventas
				$select_vent_pais.children().remove();
				$select_vent_pais.append(pais_hmtl);
				
				//estado contacto ventas
				$select_vent_estado.children().remove();
				$select_vent_estado.append(entidad_hmtl);
				
				//municipio contacto ventas
				$select_vent_localidad.children().remove();
				$select_vent_localidad.append(localidad_hmtl);
				
				
				//carga select estados al cambiar el pais del contacto ventas
				$select_vent_pais.change(function(){
					var valor_pais = $(this).val();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
					$arreglo = {'id_pais':valor_pais};
					$.post(input_json,$arreglo,function(entry){
						$select_vent_estado.children().remove();
						var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
						$.each(entry['entidades'],function(entryIndex,entidad){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						});
						$select_vent_estado.append(entidad_hmtl);
						var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Seleccionar municipio' + '</option>';
						$select_vent_localidad.children().remove();
						$select_vent_localidad.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
				
				
				//carga select municipios al cambiar el estado del contacto ventas
				$select_vent_estado.change(function(){
					var valor_entidad = $(this).val();
					var valor_pais = $select_vent_pais.val();
					//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
					$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
					$.post(input_json,$arreglo,function(entry){
						$select_vent_localidad.children().remove();
						var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
						$.each(entry['localidades'],function(entryIndex,mun){
							trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						});
						$select_vent_localidad.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
				
				//pais contacto cobranza
				$select_cob_pais.children().remove();
				$select_cob_pais.append(pais_hmtl);
				
				//estado contacto cobranza
				$select_cob_estado.children().remove();
				$select_cob_estado.append(entidad_hmtl);
				
				//municipio contacto cobranza
				$select_cob_localidad.children().remove();
				$select_cob_localidad.append(localidad_hmtl);
				
				//carga select estados al cambiar el pais del contacto cobranza
				$select_cob_pais.change(function(){
					var valor_pais = $(this).val();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
					$arreglo = {'id_pais':valor_pais};
					$.post(input_json,$arreglo,function(entry){
						$select_cob_estado.children().remove();
						var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
						$.each(entry['entidades'],function(entryIndex,entidad){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						});
						$select_cob_estado.append(entidad_hmtl);
						var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Seleccionar municipio' + '</option>';
						$select_cob_localidad.children().remove();
						$select_cob_localidad.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
				
				
				//carga select municipios al cambiar el estado del contacto cobranza
				$select_cob_estado.change(function(){
					var valor_entidad = $(this).val();
					var valor_pais = $select_cob_pais.val();
					//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
					$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
					$.post(input_json,$arreglo,function(entry){
						$select_cob_localidad.children().remove();
						var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
						$.each(entry['localidades'],function(entryIndex,mun){
							trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						});
						$select_cob_localidad.append(trama_hmtl_localidades);
					},"json");//termina llamada json
				});
				
            },"json");//termina llamada json
            
            
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
			$campo_descuento.focus(function(e){
				if(parseFloat($campo_descuento.val())<1){
					$campo_descuento.val('');
				}
			});
			
			//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
			$campo_descuento.blur(function(e){
				if(parseFloat($campo_descuento.val())==0||$campo_descuento.val()==""){
					$campo_descuento.val(0.00);
				}
			});	
			
			$campo_descuento.keypress(function(e){
				// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
				if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
					return true;
				}else {
					return false;
				}
			});
            
            
            $cerrar_plugin.bind('click',function(){
                    var remove = function() { $(this).remove(); };
                    $('#forma-proveedors-overlay').fadeOut(remove);
            });
			
            $cancelar_plugin.click(function(event){
                    var remove = function() { $(this).remove(); };
                    $('#forma-proveedors-overlay').fadeOut(remove);
            });
    });
	
	
	
	//comienza editar
    var carga_formaProveedors00_for_datagrid00 = function(id_to_show, accion_mode){
		
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
			jConfirm('Realmente desea eliminar el proveedor seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El proveedor fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}else{
							jAlert("El proveedor no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			var form_to_show = 'formaProveedor00';
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			var accion = "get_proveedor";

			$(this).modalPanel_proveedors();

			$('#forma-proveedors-window').css({ "margin-left": -300, 	"margin-top": -200 });
			
			$forma_selected.prependTo('#forma-proveedors-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			if(accion_mode == 'edit'){
				//var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + accion + '/' + id_to_show + '/out.json';
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProveedor.json';
				$arreglo = {'id':id_to_show,
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				//tab1 Proveedor
				var $campo_id_proveedor = $('#forma-proveedors-window').find('input[name=id_proveedor]');
				var $campo_folio = $('#forma-proveedors-window').find('input[name=folio]');
				var $campo_rfc = $('#forma-proveedors-window').find('input[name=rfc]');
				var $campo_curp = $('#forma-proveedors-window').find('input[name=curp]');
				var $campo_raz_social = $('#forma-proveedors-window').find('input[name=raz_social]');
				var $campo_nombre_comercial = $('#forma-proveedors-window').find('input[name=nombre_comercial]');
				var $campo_calle = $('#forma-proveedors-window').find('input[name=calle]');
				var $campo_num_calle = $('#forma-proveedors-window').find('input[name=num_calle]');
				var $campo_colonia = $('#forma-proveedors-window').find('input[name=colonia]');
				var $campo_cp = $('#forma-proveedors-window').find('input[name=cp]');
				var $campo_entrecalles = $('#forma-proveedors-window').find('input[name=entrecalles]');
				var $select_pais = $('#forma-proveedors-window').find('select[name=pais]');
				var $select_estado = $('#forma-proveedors-window').find('select[name=estado]');
				var $select_localidad = $('#forma-proveedors-window').find('select[name=localidad]');
				var $campo_loc_alternativa = $('#forma-proveedors-window').find('input[name=loc_alternativa]');
				var $campo_tel1 = $('#forma-proveedors-window').find('input[name=tel1]');
				var $campo_ext1 = $('#forma-proveedors-window').find('input[name=ext1]');
				var $campo_fax = $('#forma-proveedors-window').find('input[name=fax]');
				var $campo_tel2 = $('#forma-proveedors-window').find('input[name=tel2]');
				var $campo_ext2 = $('#forma-proveedors-window').find('input[name=ext2]');
				var $campo_email = $('#forma-proveedors-window').find('input[name=email]');
				var $campo_pag_web = $('#forma-proveedors-window').find('input[name=pag_web]');
				var $select_impuesto = $('#forma-proveedors-window').find('select[name=impuesto]');
				
				//tab2 Datos
				var $select_zona = $('#forma-proveedors-window').find('select[name=zona]');
				var $select_grupo = $('#forma-proveedors-window').find('select[name=grupo]');
				var $select_prov_tipo = $('#forma-proveedors-window').find('select[name=prov_tipo]');
				var $select_clasif1 = $('#forma-proveedors-window').find('select[name=clasif1]');
				var $select_clasif2 = $('#forma-proveedors-window').find('select[name=clasif2]');
				var $select_clasif3 = $('#forma-proveedors-window').find('select[name=clasif3]');
				var $select_moneda = $('#forma-proveedors-window').find('select[name=moneda]');
				var $select_tiempo_entrega = $('#forma-proveedors-window').find('select[name=tentrega]');
				var $select_estatus = $('#forma-proveedors-window').find('select[name=estatus]');
				var $campo_comentarios = $('#forma-proveedors-window').find('textarea[name=comentarios]');
				var $check_transportista = $('#forma-proveedors-window').find('input[name=check_transportista]');
				
				//tab3 Credito
				var $campo_limite_credito = $('#forma-proveedors-window').find('input[name=limite_credito]');
				var $select_credito = $('#forma-proveedors-window').find('select[name=credito]');
				var $campo_descuento = $('#forma-proveedors-window').find('input[name=descuento]');
				var $select_inicio_credito = $('#forma-proveedors-window').find('select[name=inicio_credito]');
				var $select_tipo_embarque = $('#forma-proveedors-window').find('select[name=tipo_embarque]');
				var $select_flete = $('#forma-proveedors-window').find('select[name=flete]');
				var $txtarea_condiciones = $('#forma-proveedors-window').find('textarea[name=condiciones]');
				var $txtarea_observaciones = $('#forma-proveedors-window').find('textarea[name=observaciones]');
				
				//tab4 Contacto Ventas
				var $campo_vent_contacto = $('#forma-proveedors-window').find('input[name=vent_contacto]');
				var $campo_vent_puesto = $('#forma-proveedors-window').find('input[name=vent_puesto]');
				var $campo_vent_calle = $('#forma-proveedors-window').find('input[name=vent_calle]');
				var $campo_vent_numcalle = $('#forma-proveedors-window').find('input[name=vent_numcalle]');
				var $campo_vent_colonia = $('#forma-proveedors-window').find('input[name=vent_colonia]');
				var $campo_vent_cp = $('#forma-proveedors-window').find('input[name=vent_cp]');
				var $campo_vent_entrecalles = $('#forma-proveedors-window').find('input[name=vent_entrecalles]');
				var $select_vent_pais = $('#forma-proveedors-window').find('select[name=vent_pais]');
				var $select_vent_estado = $('#forma-proveedors-window').find('select[name=vent_estado]');
				var $select_vent_localidad = $('#forma-proveedors-window').find('select[name=vent_localidad]');
				var $campo_vent_tel1 = $('#forma-proveedors-window').find('input[name=vent_tel1]');
				var $campo_vent_ext1 = $('#forma-proveedors-window').find('input[name=vent_ext1]');
				var $campo_vent_fax = $('#forma-proveedors-window').find('input[name=vent_fax]');
				var $campo_vent_tel2 = $('#forma-proveedors-window').find('input[name=vent_tel2]');
				var $campo_vent_ext2 = $('#forma-proveedors-window').find('input[name=vent_ext2]');
				var $campo_vent_email = $('#forma-proveedors-window').find('input[name=vent_email]');
				
				//tab5 Contacto Cobranza
				var $campo_cob_contacto = $('#forma-proveedors-window').find('input[name=cob_contacto]');
				var $campo_cob_puesto = $('#forma-proveedors-window').find('input[name=cob_puesto]');
				var $campo_cob_calle = $('#forma-proveedors-window').find('input[name=cob_calle]');
				var $campo_cob_numcalle = $('#forma-proveedors-window').find('input[name=cob_numcalle]');
				var $campo_cob_colonia = $('#forma-proveedors-window').find('input[name=cob_colonia]');
				var $campo_cob_cp = $('#forma-proveedors-window').find('input[name=cob_cp]');
				var $campo_cob_entrecalles = $('#forma-proveedors-window').find('input[name=cob_entrecalles]');
				var $select_cob_pais = $('#forma-proveedors-window').find('select[name=cob_pais]');
				var $select_cob_estado = $('#forma-proveedors-window').find('select[name=cob_estado]');
				var $select_cob_localidad = $('#forma-proveedors-window').find('select[name=cob_localidad]');
				var $campo_cob_tel1 = $('#forma-proveedors-window').find('input[name=cob_tel1]');
				var $campo_cob_ext1 = $('#forma-proveedors-window').find('input[name=cob_ext1]');
				var $campo_cob_fax = $('#forma-proveedors-window').find('input[name=cob_fax]');
				var $campo_cob_tel2 = $('#forma-proveedors-window').find('input[name=cob_tel2]');
				var $campo_cob_ext2 = $('#forma-proveedors-window').find('input[name=cob_ext2]');
				var $campo_cob_email = $('#forma-proveedors-window').find('input[name=cob_email]');
				
				//tab6 comentarios
				var $pestana_contabilidad = $('#forma-proveedors-window').find('ul.pestanas').find('a[href*=#tabx-6]');
				
				var $id_cta_pasivo = $('#forma-proveedors-window').find('input[name=id_cta_pasivo]');
				var $pasivo_cuenta = $('#forma-proveedors-window').find('input[name=pasivo_cuenta]');
				var $pasivo_scuenta = $('#forma-proveedors-window').find('input[name=pasivo_scuenta]');
				var $pasivo_sscuenta = $('#forma-proveedors-window').find('input[name=pasivo_sscuenta]');
				var $pasivo_ssscuenta = $('#forma-proveedors-window').find('input[name=pasivo_ssscuenta]');
				var $pasivo_sssscuenta = $('#forma-proveedors-window').find('input[name=pasivo_sssscuenta]');
				var $pasivo_descripcion = $('#forma-proveedors-window').find('input[name=pasivo_descripcion]');
				
				var $id_cta_egreso = $('#forma-proveedors-window').find('input[name=id_cta_egreso]');
				var $egreso_cuenta = $('#forma-proveedors-window').find('input[name=egreso_cuenta]');
				var $egreso_scuenta = $('#forma-proveedors-window').find('input[name=egreso_scuenta]');
				var $egreso_sscuenta = $('#forma-proveedors-window').find('input[name=egreso_sscuenta]');
				var $egreso_ssscuenta = $('#forma-proveedors-window').find('input[name=egreso_ssscuenta]');
				var $egreso_sssscuenta = $('#forma-proveedors-window').find('input[name=egreso_sssscuenta]');
				var $egreso_descripcion = $('#forma-proveedors-window').find('input[name=egreso_descripcion]');
				
				var $id_cta_ietu = $('#forma-proveedors-window').find('input[name=id_cta_ietu]');
				var $ietu_cuenta = $('#forma-proveedors-window').find('input[name=ietu_cuenta]');
				var $ietu_scuenta = $('#forma-proveedors-window').find('input[name=ietu_scuenta]');
				var $ietu_sscuenta = $('#forma-proveedors-window').find('input[name=ietu_sscuenta]');
				var $ietu_ssscuenta = $('#forma-proveedors-window').find('input[name=ietu_ssscuenta]');
				var $ietu_sssscuenta = $('#forma-proveedors-window').find('input[name=ietu_sssscuenta]');
				var $ietu_descripcion = $('#forma-proveedors-window').find('input[name=ietu_descripcion]');
				
				var $id_cta_complement = $('#forma-proveedors-window').find('input[name=id_cta_complement]');
				var $complement_cuenta = $('#forma-proveedors-window').find('input[name=complement_cuenta]');
				var $complement_scuenta = $('#forma-proveedors-window').find('input[name=complement_scuenta]');
				var $complement_sscuenta = $('#forma-proveedors-window').find('input[name=complement_sscuenta]');
				var $complement_ssscuenta = $('#forma-proveedors-window').find('input[name=complement_ssscuenta]');
				var $complement_sssscuenta = $('#forma-proveedors-window').find('input[name=complement_sssscuenta]');
				var $complement_descripcion = $('#forma-proveedors-window').find('input[name=complement_descripcion]');
				
				var $id_cta_pasivo_complement = $('#forma-proveedors-window').find('input[name=id_cta_pasivo_complement]');
				var $pasivo_complement_cuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_cuenta]');
				var $pasivo_complement_scuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_scuenta]');
				var $pasivo_complement_sscuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_sscuenta]');
				var $pasivo_complement_ssscuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_ssscuenta]');
				var $pasivo_complement_sssscuenta = $('#forma-proveedors-window').find('input[name=pasivo_complement_sssscuenta]');
				var $pasivo_complement_descripcion = $('#forma-proveedors-window').find('input[name=pasivo_complement_descripcion]');
				
				var $pasivo_busca = $('#forma-proveedors-window').find('a[href=pasivo_busca]');
				var $egreso_busca = $('#forma-proveedors-window').find('a[href=egreso_busca]');
				var $ietu_busca = $('#forma-proveedors-window').find('a[href=ietu_busca]');
				var $complement_busca = $('#forma-proveedors-window').find('a[href=complement_busca]');
				var $pasivo_complement_busca = $('#forma-proveedors-window').find('a[href=pasivo_complement_busca]');
				
				var $pasivo_limpiar = $('#forma-proveedors-window').find('a[href=pasivo_limpiar]');
				var $egreso_limpiar = $('#forma-proveedors-window').find('a[href=egreso_limpiar]');
				var $ietu_limpiar = $('#forma-proveedors-window').find('a[href=ietu_limpiar]');
				var $complement_limpiar = $('#forma-proveedors-window').find('a[href=complement_limpiar]');
				var $pasivo_complement_limpiar = $('#forma-proveedors-window').find('a[href=pasivo_complement_limpiar]');
				
				var $cerrar_plugin = $('#forma-proveedors-window').find('#close');
				var $cancelar_plugin = $('#forma-proveedors-window').find('#boton_cancelar');
				
				//$campo_nombre_comercial.attr({ 'readOnly':true });
				$campo_folio.attr({ 'readOnly':true });
				//$campo_raz_social.attr({ 'readOnly':true });
				//$campo_rfc.attr({ 'readOnly':true });
				$campo_folio.css({'background' : '#DDDDDD'});
				
				$pasivo_cuenta.hide();
				$pasivo_scuenta.hide();
				$pasivo_sscuenta.hide();
				$pasivo_ssscuenta.hide();
				$pasivo_sssscuenta.hide();
				
				$egreso_cuenta.hide();
				$egreso_scuenta.hide();
				$egreso_sscuenta.hide();
				$egreso_ssscuenta.hide();
				$egreso_sssscuenta.hide();
				
				$ietu_cuenta.hide();
				$ietu_scuenta.hide();
				$ietu_sscuenta.hide();
				$ietu_ssscuenta.hide();
				$ietu_sssscuenta.hide();
				
				$complement_cuenta.hide();
				$complement_scuenta.hide();
				$complement_sscuenta.hide();
				$complement_ssscuenta.hide();
				$complement_sssscuenta.hide();
				
				$pasivo_complement_cuenta.hide();
				$pasivo_complement_scuenta.hide();
				$pasivo_complement_sscuenta.hide();
				$pasivo_complement_ssscuenta.hide();
				$pasivo_complement_sssscuenta.hide();
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() { $(this).remove(); };
						$('#forma-proveedors-overlay').fadeOut(remove);
						jAlert("Proveedor actualizado", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-proveedors-window').find('div.interrogacion').css({'display':'none'});

						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-proveedors-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
							}
						}
					}
				}

				var options = { dataType :  'json', success : respuestaProcesada };
				$forma_selected.ajaxForm(options);
				
				//$.getJSON(json_string,function(entry){
				$.post(input_json,$arreglo,function(entry){
					if( entry['Extras'][0]['incluye_contab']=='false' ){
						$pestana_contabilidad.parent().hide();
					}else{
						//visualizar subcuentas de acuerdo al nivel definido para la empresa
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=1 ){ $pasivo_cuenta.show(); $egreso_cuenta.show(); $ietu_cuenta.show();  $complement_cuenta.show(); $pasivo_complement_cuenta.show();};
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=2 ){ $pasivo_scuenta.show(); $egreso_scuenta.show(); $ietu_scuenta.show(); $complement_scuenta.show(); $pasivo_complement_scuenta.show();};
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=3 ){ $pasivo_sscuenta.show(); $egreso_sscuenta.show(); $ietu_sscuenta.show(); $complement_sscuenta.show(); $pasivo_complement_sscuenta.show();};
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=4 ){ $pasivo_ssscuenta.show(); $egreso_ssscuenta.show(); $ietu_ssscuenta.show(); $complement_ssscuenta.show(); $pasivo_complement_ssscuenta.show();};
						if(parseInt(entry['Extras'][0]['nivel_cta']) >=5 ){ $pasivo_sssscuenta.show(); $egreso_sssscuenta.show(); $ietu_sssscuenta.show(); $complement_sssscuenta.show(); $pasivo_complement_sssscuenta.show();};
						
						$id_cta_pasivo.attr({ 'value' : entry['Contab'][0]['pasivo_id_cta'] });
						$pasivo_cuenta.attr({ 'value' : entry['Contab'][0]['pasivo_cta'] });
						$pasivo_scuenta.attr({ 'value' : entry['Contab'][0]['pasivo_subcta'] });
						$pasivo_sscuenta.attr({ 'value' : entry['Contab'][0]['pasivo_ssubcta'] });
						$pasivo_ssscuenta.attr({ 'value' : entry['Contab'][0]['pasivo_sssubcta'] });
						$pasivo_sssscuenta.attr({ 'value' : entry['Contab'][0]['pasivo_ssssubcta'] });
						$pasivo_descripcion.attr({ 'value' : entry['Contab'][0]['pasivo_descripcion'] });
						
						$id_cta_egreso.attr({ 'value' : entry['Contab'][0]['egreso_id_cta'] });
						$egreso_cuenta.attr({ 'value' : entry['Contab'][0]['egreso_cta'] });
						$egreso_scuenta.attr({ 'value' : entry['Contab'][0]['egreso_subcta'] });
						$egreso_sscuenta.attr({ 'value' : entry['Contab'][0]['egreso_ssubcta'] });
						$egreso_ssscuenta.attr({ 'value' : entry['Contab'][0]['egreso_sssubcta'] });
						$egreso_sssscuenta.attr({ 'value' : entry['Contab'][0]['egreso_ssssubcta'] });
						$egreso_descripcion.attr({ 'value' : entry['Contab'][0]['egreso_descripcion'] });
						
						$id_cta_ietu.attr({ 'value' : entry['Contab'][0]['ietu_id_cta'] });
						$ietu_cuenta.attr({ 'value' : entry['Contab'][0]['ietu_cta'] });
						$ietu_scuenta.attr({ 'value' : entry['Contab'][0]['ietu_subcta'] });
						$ietu_sscuenta.attr({ 'value' : entry['Contab'][0]['ietu_ssubcta'] });
						$ietu_ssscuenta.attr({ 'value' : entry['Contab'][0]['ietu_sssubcta'] });
						$ietu_sssscuenta.attr({ 'value' : entry['Contab'][0]['ietu_ssssubcta'] });
						$ietu_descripcion.attr({ 'value' : entry['Contab'][0]['ietu_descripcion'] });
						
						$id_cta_complement.attr({ 'value' : entry['Contab'][0]['complement_id_cta'] });
						$complement_cuenta.attr({ 'value' : entry['Contab'][0]['complement_cta'] });
						$complement_scuenta.attr({ 'value' : entry['Contab'][0]['complement_subcta'] });
						$complement_sscuenta.attr({ 'value' : entry['Contab'][0]['complement_ssubcta'] });
						$complement_ssscuenta.attr({ 'value' : entry['Contab'][0]['complement_sssubcta'] });
						$complement_sssscuenta.attr({ 'value' : entry['Contab'][0]['complement_ssssubcta'] });
						$complement_descripcion.attr({ 'value' : entry['Contab'][0]['complement_descripcion'] });
						
						$id_cta_pasivo_complement.attr({ 'value' : entry['Contab'][0]['pc_id_cta'] });
						$pasivo_complement_cuenta.attr({ 'value' : entry['Contab'][0]['pc_cta'] });
						$pasivo_complement_scuenta.attr({ 'value' : entry['Contab'][0]['pc_subcta'] });
						$pasivo_complement_sscuenta.attr({ 'value' : entry['Contab'][0]['pc_ssubcta'] });
						$pasivo_complement_ssscuenta.attr({ 'value' : entry['Contab'][0]['pc_sssubcta'] });
						$pasivo_complement_sssscuenta.attr({ 'value' : entry['Contab'][0]['pc_ssssubcta'] });
						$pasivo_complement_descripcion.attr({ 'value' : entry['Contab'][0]['pc_descripcion'] });
						
						//busca Cuenta Pasivo
						$pasivo_busca.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(1, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//busca Cuenta Costo de Egreso
						$egreso_busca.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(2, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//busca Cuenta IETU
						$ietu_busca.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(3, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//busca Cuenta Complementaria
						$complement_busca.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(4, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//busca Cuenta Pasivo Complementaria
						$pasivo_complement_busca.click(function(event){
							event.preventDefault();
							$busca_cuentas_contables(5, entry['Extras'][0]['nivel_cta'], entry['CtaMay']);
						});
						
						//limpiar campos Cuenta Pasivo
						$pasivo_limpiar.click(function(event){
							event.preventDefault();
							$id_cta_pasivo.val(0);
							$pasivo_cuenta.val('');
							$pasivo_scuenta.val('');
							$pasivo_sscuenta.val('');
							$pasivo_ssscuenta.val('');
							$pasivo_sssscuenta.val('');
							$pasivo_descripcion.val('');
						});
						
						//limpiar campos Cuenta de Egreso
						$egreso_limpiar.click(function(event){
							event.preventDefault();
							$id_cta_egreso.val(0);
							$egreso_cuenta.val('');
							$egreso_scuenta.val('');
							$egreso_sscuenta.val('');
							$egreso_ssscuenta.val('');
							$egreso_sssscuenta.val('');
							$egreso_descripcion.val('');
						});
						
						//limpiar campos IETU
						$ietu_limpiar.click(function(event){
							event.preventDefault();
							$id_cta_ietu.val(0);
							$ietu_cuenta.val('');
							$ietu_scuenta.val('');
							$ietu_sscuenta.val('');
							$ietu_ssscuenta.val('');
							$ietu_sssscuenta.val('');
							$ietu_descripcion.val('');
						});
						
						//limpiar campos Cuenta Complementaria
						$complement_limpiar.click(function(event){
							event.preventDefault();
							$id_cta_complement.val(0);
							$complement_cuenta.val('');
							$complement_scuenta.val('');
							$complement_sscuenta.val('');
							$complement_ssscuenta.val('');
							$complement_sssscuenta.val('');
							$complement_descripcion.val('');
						});
						
						//limpiar campos Cuenta Pasivo Complementaria
						$pasivo_complement_limpiar.click(function(event){
							event.preventDefault();
							$id_cta_pasivo_complement.val(0);
							$pasivo_complement_cuenta.val('');
							$pasivo_complement_scuenta.val('');
							$pasivo_complement_sscuenta.val('');
							$pasivo_complement_ssscuenta.val('');
							$pasivo_complement_sssscuenta.val('');
							$pasivo_complement_descripcion.val('');
						});
					}
					
					
					$campo_id_proveedor.attr({ 'value' : entry['Proveedor'][0]['id'] });
					$campo_folio.attr({ 'value' : entry['Proveedor'][0]['folio'] });
					$campo_rfc.attr({ 'value' : entry['Proveedor'][0]['rfc'] });
					$campo_curp.attr({ 'value' : entry['Proveedor'][0]['curp'] });
					$campo_raz_social.attr({ 'value' : entry['Proveedor'][0]['razon_social'] });
					$campo_nombre_comercial.attr({ 'value' : entry['Proveedor'][0]['clave_comercial'] });
					$campo_calle.attr({ 'value' : entry['Proveedor'][0]['calle'] });
					$campo_num_calle.attr({ 'value' : entry['Proveedor'][0]['numero'] });
					$campo_colonia.attr({ 'value' : entry['Proveedor'][0]['colonia'] });
					$campo_cp.attr({ 'value' : entry['Proveedor'][0]['cp'] });
					$campo_entrecalles.attr({ 'value' : entry['Proveedor'][0]['entre_calles'] });
					$campo_loc_alternativa.attr({ 'value' : entry['Proveedor'][0]['localidad_alternativa'] });
					$campo_tel1.attr({ 'value' : entry['Proveedor'][0]['telefono1'] });
					$campo_ext1.attr({ 'value' : entry['Proveedor'][0]['extension1'] });
					$campo_fax.attr({ 'value' : entry['Proveedor'][0]['fax'] });
					$campo_tel2.attr({ 'value' : entry['Proveedor'][0]['telefono2'] });
					$campo_ext2.attr({ 'value' : entry['Proveedor'][0]['extension2'] });
					$campo_email.attr({ 'value' : entry['Proveedor'][0]['correo_electronico'] });
					$campo_pag_web.attr({ 'value' : entry['Proveedor'][0]['web_site'] });
					$check_transportista.attr('checked',  (entry['Proveedor'][0]['transportista']=='true')? true:false );
					
					$campo_limite_credito.attr({ 'value' : entry['Proveedor'][0]['limite_credito'] });
					$campo_descuento.attr({ 'value' : entry['Proveedor'][0]['descuento'] });
					$txtarea_condiciones.text(entry['Proveedor'][0]['condiciones']);
					$txtarea_observaciones.text(entry['Proveedor'][0]['observaciones']);
					
					$campo_vent_contacto.attr({ 'value' : entry['Proveedor'][0]['vent_contacto'] });
					$campo_vent_puesto.attr({ 'value' : entry['Proveedor'][0]['vent_puesto'] });
					$campo_vent_calle.attr({ 'value' : entry['Proveedor'][0]['vent_calle'] });
					$campo_vent_numcalle.attr({ 'value' : entry['Proveedor'][0]['vent_numero'] });
					$campo_vent_colonia.attr({ 'value' : entry['Proveedor'][0]['vent_colonia'] });
					$campo_vent_cp.attr({ 'value' : entry['Proveedor'][0]['vent_cp'] });
					$campo_vent_entrecalles.attr({ 'value' : entry['Proveedor'][0]['vent_entre_calles'] });
					$campo_vent_tel1.attr({ 'value' : entry['Proveedor'][0]['vent_telefono1'] });
					$campo_vent_ext1.attr({ 'value' : entry['Proveedor'][0]['vent_extension1'] });
					$campo_vent_fax.attr({ 'value' : entry['Proveedor'][0]['vent_fax'] });
					$campo_vent_tel2.attr({ 'value' : entry['Proveedor'][0]['vent_telefono2'] });
					$campo_vent_ext2.attr({ 'value' : entry['Proveedor'][0]['vent_extension2'] });
					$campo_vent_email.attr({ 'value' : entry['Proveedor'][0]['vent_email'] });
					
					$campo_cob_contacto.attr({ 'value' : entry['Proveedor'][0]['cob_contacto'] });
					$campo_cob_puesto.attr({ 'value' : entry['Proveedor'][0]['cob_puesto'] });
					$campo_cob_calle.attr({ 'value' : entry['Proveedor'][0]['cob_calle'] });
					$campo_cob_numcalle.attr({ 'value' : entry['Proveedor'][0]['cob_numero'] });
					$campo_cob_colonia.attr({ 'value' : entry['Proveedor'][0]['cob_colonia'] });
					$campo_cob_cp.attr({ 'value' : entry['Proveedor'][0]['cob_cp'] });
					$campo_cob_entrecalles.attr({ 'value' : entry['Proveedor'][0]['cob_entre_calles'] });
					$campo_cob_tel1.attr({ 'value' : entry['Proveedor'][0]['cob_telefono1'] });
					$campo_cob_ext1.attr({ 'value' : entry['Proveedor'][0]['cob_extension1'] });
					$campo_cob_fax.attr({ 'value' : entry['Proveedor'][0]['cob_fax'] });
					$campo_cob_tel2.attr({ 'value' : entry['Proveedor'][0]['cob_telefono2'] });
					$campo_cob_ext2.attr({ 'value' : entry['Proveedor'][0]['cob_extension2'] });
					$campo_cob_email.attr({ 'value' : entry['Proveedor'][0]['cob_email'] });
					$txtarea_observaciones.text(entry['Proveedor'][0]['comentarios']);
					
					
					//Alimentando los campos select de las pais
					$select_pais.children().remove();
					var pais_hmtl = "";
					$.each(entry['pais'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Proveedor']['0']['pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_pais.append(pais_hmtl);
					
					//Alimentando los campos select del estado
					$select_estado.children().remove();
					var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['entidades'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Proveedor'][0]['estado_id']){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_estado.append(entidad_hmtl);
					
					//Alimentando los campos select de los municipios
					$select_localidad.children().remove();
					var localidad_hmtl = '<option value="0" selected="yes" >[-Seleccionar municipio-]</option>';
					$.each(entry['municipios'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Proveedor'][0]['municipio_id']){
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_localidad.append(localidad_hmtl);
					
					//el impuesto aun no esta ligada a nada
                    $select_impuesto.children().remove();
                    var impuesto_html = '<option value="0">[- Impuesto -]</option>';
                    $.each(entry['Impuestos'],function(entryIndex,imp){
						if(parseInt(imp['id']) == parseInt(entry['Proveedor'][0]['impuesto'])){
							impuesto_html += '<option value="' + imp['id'] + '"  selected="yes">' + imp['descripcion'] + '</option>';
						}else{
							impuesto_html += '<option value="' + imp['id'] + '"  >' + imp['descripcion'] + '</option>';
						}
                    });
                    $select_impuesto.append(impuesto_html);
					
					
					
                    $select_zona.children().remove();
                    var zona_html = '<option value="0">[-Zona-]</option>';
                    $.each(entry['Zonas'],function(entryIndex,zona){
						if(parseInt(zona['id']) == parseInt(entry['Proveedor'][0]['cxp_prov_zona_id'])){
							zona_html += '<option value="' + zona['id'] + '" selected="yes">' + zona['nombre_zona'] + '</option>';
						}else{
							zona_html += '<option value="' + zona['id'] + '"  >' + zona['nombre_zona'] + '</option>';
						}
                    });
                    $select_zona.append(zona_html);
                    
                    $select_grupo.children().remove();
                    var grupo_html = '<option value="0">[-Grupo-]</option>';
                    $.each(entry['Grupos'],function(entryIndex,grupo){
						if(parseInt(grupo['id']) == parseInt(entry['Proveedor'][0]['grupo_id'])){
							grupo_html += '<option value="' + grupo['id'] + '"  selected="yes">' + grupo['nombre_grupo'] + '</option>';
						}else{
							grupo_html += '<option value="' + grupo['id'] + '"  >' + grupo['nombre_grupo'] + '</option>';
						}
                    });
                    $select_grupo.append(grupo_html);
					
					
					$select_prov_tipo.children().remove();
					var tiposproov_html = '<option value="0">[--Seleccionar tipo--]</option>';
					$.each(entry['proveedor_tipo'],function(entryIndex,tipo){
						if(parseInt(tipo['id']) == parseInt(entry['Proveedor'][0]['proveedortipo_id'])){
							tiposproov_html += '<option value="' + tipo['id'] + '"  selected="yes">' + tipo['titulo'] + '</option>';
						}else{
							tiposproov_html += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
						}
					});
					$select_prov_tipo.append(tiposproov_html);
					
                    $select_clasif1.children().remove();
                    var clasif1_html = '<option value="0">[-Clasificacion 1-]</option>';
                    $.each(entry['Clas1'],function(entryIndex,clas1){
						if(parseInt(clas1['id']) == parseInt(entry['Proveedor'][0]['clasif_1'])){
							clasif1_html += '<option value="' + clas1['id'] + '" selected="yes">' + clas1['clasificacion1'] + '</option>';
						}else{
							clasif1_html += '<option value="' + clas1['id'] + '"  >' + clas1['clasificacion1'] + '</option>';
						}
                    });
                    $select_clasif1.append(clasif1_html);
                    
					
                    $select_clasif2.children().remove();
                    var clasif2_html = '<option value="0">[-Clasificacion 2-]</option>';
                    $.each(entry['Clas2'],function(entryIndex,clas2){
						if(parseInt(clas2['id']) == parseInt(entry['Proveedor'][0]['clasif_2'])){
							clasif2_html += '<option value="' + clas2['id'] + '"  selected="yes">' + clas2['clasificacion2'] + '</option>';
						}else{
							clasif2_html += '<option value="' + clas2['id'] + '"  >' + clas2['clasificacion2'] + '</option>';
						}
                    });
                    $select_clasif2.append(clasif2_html);
                    
					
                    $select_clasif3.children().remove();
                    var clasif3_html = '<option value="0">[-Clasificacion 3-]</option>';
                    $.each(entry['Clas3'],function(entryIndex,clas3){
						if(parseInt(clas3['id']) == parseInt(entry['Proveedor'][0]['clasif_3'])){
							clasif3_html += '<option value="' + clas3['id'] + '"  selected="yes">' + clas3['clasificacion3'] + '</option>';
						}else{
							clasif3_html += '<option value="' + clas3['id'] + '" >' + clas3['clasificacion3'] + '</option>';
						}
                    });
                    $select_clasif3.append(clasif3_html);
					
					//carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					var moneda_hmtl = '<option value="0">[-Seleccionar moneda-]</option>';
					$.each(entry['monedas'],function(entryIndex,moneda){
						if(parseInt(moneda['id']) == parseInt(entry['Proveedor'][0]['moneda_id'])){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					
					//carga select de tiempos de entrega
					$select_tiempo_entrega.children().remove();
					var tiempos_html = '<option value="0">[-Seleccionar tiempo-]</option>';
					$.each(entry['te'],function(entryIndex,tiempo){
						if(parseInt(tiempo['id']) == parseInt(entry['Proveedor'][0]['tiempo_entrega_id'])){
							tiempos_html += '<option value="' + tiempo['id'] + '"  selected="yes">' + tiempo['descripcion'] + '</option>';
						}else{
							tiempos_html += '<option value="' + tiempo['id'] + '"  >' + tiempo['descripcion'] + '</option>';
						}
					});
					$select_tiempo_entrega.append(tiempos_html);
					
					//estatus
					var sel_activo='';
					var sel_inactivo='';
					if(entry['Proveedor']['0']['estatus']=='true'){
						sel_activo='selected="yes"';
					}else{
						sel_inactivo='selected="yes"';
					}
                    $select_estatus.children().remove();
                    var status_html = '<option value="true" '+sel_activo+'>Activo</option>';
                    status_html += '<option value="false" '+sel_inactivo+'>Inactivo</option>';
                    $select_estatus.append(status_html);
                    
					
					//carga el select de dias de credito
					$select_credito.children().remove();
					var credito_hmtl = '';
					$.each(entry['DiasCredito'],function(entryIndex,credito){
						if(parseInt(credito['id']) == parseInt(entry['Proveedor'][0]['dias_credito_id'])){
							credito_hmtl += '<option value="' + credito['id'] + '" selected="yes">' + credito['descripcion'] + '</option>';
						}else{
							credito_hmtl += '<option value="' + credito['id'] + '"  >' + credito['descripcion'] + '</option>';
						}
					});
					$select_credito.append(credito_hmtl);
					
					
					//carga el select de Inicios de Credito
					$select_inicio_credito.children().remove();
					var inicio_hmtl = '<option value="0" selected="yes">[--Seleccionar inicio --]</option>';
					$.each(entry['InicioCredito'],function(entryIndex,inicio){
						if(parseInt(inicio['id']) == parseInt(entry['Proveedor'][0]['credito_a_partir'])){
							inicio_hmtl += '<option value="' + inicio['id'] + '" selected="yes">' + inicio['titulo'] + '</option>';
						}else{
							inicio_hmtl += '<option value="' + inicio['id'] + '"  >' + inicio['titulo'] + '</option>';
						}
					});
					$select_inicio_credito.append(inicio_hmtl);
                    
					//carga el select de tipos de Embarque
					$select_tipo_embarque.children().remove();
					var embarque_hmtl = '<option value="0" selected="yes">[--Seleccionar tipo --]</option>';
					$.each(entry['TiposEmbarque'],function(entryIndex,tipo){
						if(parseInt(tipo['id']) == parseInt(entry['Proveedor'][0]['cxp_prov_tipo_embarque_id'])){
							embarque_hmtl += '<option value="' + tipo['id'] + '" selected="yes">' + tipo['titulo'] + '</option>';
						}else{
							embarque_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
						}
					});
					$select_tipo_embarque.append(embarque_hmtl);
					
                    //flete
					var sel_pagado='';
					var sel_por_pagar='';
					if(entry['Proveedor']['0']['flete_pagado']=='true'){
						sel_pagado='selected="yes"';
					}else{
						sel_por_pagar='selected="yes"';
					}
                    $select_flete.children().remove();
                    var flete_html = '<option value="true" '+sel_pagado+'>Pagado</option>';
                    flete_html += '<option value="false" '+sel_por_pagar+'>Por pagar</option>';
                    $select_flete.append(flete_html);
                    
					
					
					
					
					//Alimentando los campos select de las pais del contacto ventas
					$select_vent_pais.children().remove();
					var vent_pais_hmtl = "";
					$.each(entry['pais'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Proveedor'][0]['vent_pais_id']){
							vent_pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							vent_pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_vent_pais.append(vent_pais_hmtl);
					
					//Alimentando los campos select del estado del contacto ventas
					$select_vent_estado.children().remove();
					var vent_entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['entidades'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Proveedor'][0]['vent_estado_id']){
							vent_entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							vent_entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_vent_estado.append(vent_entidad_hmtl);
					
					//Alimentando los campos select de los municipios del contacto ventas
					$select_vent_localidad.children().remove();
					var vent_localidad_hmtl = '<option value="0" selected="yes" >[-Seleccionar municipio-]</option>';
					$.each(entry['ventmunicipios'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Proveedor'][0]['vent_municipio_id']){
							vent_localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							vent_localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_vent_localidad.append(vent_localidad_hmtl);
					
					
					
					//Alimentando los campos select de las pais del contacto cobranza
					$select_cob_pais.children().remove();
					var cob_pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
					$.each(entry['pais'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Proveedor'][0]['cob_pais_id']){
							cob_pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							cob_pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_cob_pais.append(cob_pais_hmtl);
					
					//Alimentando los campos select del estado del contacto cobranza
					$select_cob_estado.children().remove();
					var cob_entidad_hmtl = '<option value="0" selected="yes" >[-Seleccionar entidad--]</option>';
					$.each(entry['entidades'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Proveedor'][0]['cob_estado_id']){
							cob_entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							cob_entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_cob_estado.append(cob_entidad_hmtl);
					
					//Alimentando los campos select de los municipios del contacto cobranza
					$select_cob_localidad.children().remove();
					var cob_localidad_hmtl = '<option value="0" selected="yes" >[-Seleccionar municipio-]</option>';
					$.each(entry['cobmunicipios'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Proveedor'][0]['cob_municipio_id']){
							cob_localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							cob_localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_cob_localidad.append(cob_localidad_hmtl);
					
					
					//carga select estados al cambiar el pais
					$select_pais.change(function(){
						var valor_pais = $(this).val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
						$arreglo = {'id_pais':valor_pais};
						$.post(input_json,$arreglo,function(entry){
							$select_estado.children().remove();
							var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
							$.each(entry['entidades'],function(entryIndex,entidad){
								entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
							});
							$select_estado.append(entidad_hmtl);
							
							var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
							$select_localidad.children().remove();
							$select_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					
					//carga select municipios al cambiar el estado
					$select_estado.change(function(){
						var valor_entidad = $(this).val();
						var valor_pais = $select_pais.val();
						
						//alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
						$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
						$.post(input_json,$arreglo,function(entry){
							$select_localidad.children().remove();
							var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
							$.each(entry['localidades'],function(entryIndex,mun){
								trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
							});
							$select_localidad.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
				   

					//CONTACTO VENTAS
                    //carga select estados al cambiar el pais del contacto ventas
                    $select_vent_pais.change(function(){
                        var valor_pais = $(this).val();
                        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
                        $arreglo = {'id_pais':valor_pais};
                        $.post(input_json,$arreglo,function(entry){
                            $select_vent_estado.children().remove();
                            var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
                            $.each(entry['entidades'],function(entryIndex,entidad){
                                entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
                            });
                            $select_vent_estado.append(entidad_hmtl);
                            var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Seleccionar municipio' + '</option>';
                            $select_vent_localidad.children().remove();
                            $select_vent_localidad.append(trama_hmtl_localidades);
                        },"json");//termina llamada json
                    });


                    //carga select municipios al cambiar el estado del contacto ventas
                    $select_vent_estado.change(function(){
                        var valor_entidad = $(this).val();
                        var valor_pais = $select_vent_pais.val();
                        //alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
                        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
                        $arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
                        $.post(input_json,$arreglo,function(entry){
                            $select_vent_localidad.children().remove();
                            var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
                            $.each(entry['localidades'],function(entryIndex,mun){
                                trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
                            });
                            $select_vent_localidad.append(trama_hmtl_localidades);
                        },"json");//termina llamada json
                    });
                    
                    
                    
                    //CONTACTO COBRANZA
                    //carga select estados al cambiar el pais del contacto cobranza
                    $select_cob_pais.change(function(){
                        var valor_pais = $(this).val();
                        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
                        $arreglo = {'id_pais':valor_pais};
                        $.post(input_json,$arreglo,function(entry){
                            $select_cob_estado.children().remove();
                            var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
                            $.each(entry['entidades'],function(entryIndex,entidad){
                                entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
                            });
                            $select_cob_estado.append(entidad_hmtl);
                            var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Seleccionar municipio' + '</option>';
                            $select_cob_localidad.children().remove();
                            $select_cob_localidad.append(trama_hmtl_localidades);
                        },"json");//termina llamada json
                    });


                    //carga select municipios al cambiar el estado del contacto cobranza
                    $select_cob_estado.change(function(){
                        var valor_entidad = $(this).val();
                        var valor_pais = $select_cob_pais.val();
                        //alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
                        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
                        $arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
                        $.post(input_json,$arreglo,function(entry){
                            $select_cob_localidad.children().remove();
                            var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
                            $.each(entry['localidades'],function(entryIndex,mun){
                                trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
                            });
                            $select_cob_localidad.append(trama_hmtl_localidades);
                        },"json");//termina llamada json
                    });

				},"json");//termina llamada json
				
				
				
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
				$campo_descuento.focus(function(e){
					if(parseFloat($campo_descuento.val())<1){
						$campo_descuento.val('');
					}
				});
				
				//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
				$campo_descuento.blur(function(e){
					if(parseFloat($campo_descuento.val())==0||$campo_descuento.val()==""){
						$campo_descuento.val(0.00);
					}
				});	
				
				$campo_descuento.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						return true;
					}else {
						return false;
					}
				});
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-proveedors-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-proveedors-overlay').fadeOut(remove);
				});
			}
		}
    }
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProveedores.json';
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getProveedores.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaProveedors00_for_datagrid00);
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
	
    $get_datos_grid();
});
