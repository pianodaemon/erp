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
	var controller = $contextpath.val()+"/controllers/crmprospectos";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_cliente = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Prospectos');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
	var $cadena_busqueda = "";
	var $campo_busqueda = $('#barra_buscador').find('.tabla_buscador').find('input[name=cadena_buscar]');
	var $select_filtro_por = $('#barra_buscador').find('.tabla_buscador').find('select[name=filtropor]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var html = '';
	$select_filtro_por.children().remove();
	html='<option value="0">[-- Opcion busqueda --]</option>';
	html+='<option value="1">No. de control</option>';
	html+='<option value="2">RFC</option>';
	html+='<option value="3">Razon social</option>';
	
	$select_filtro_por.append(html);
	
	//alert($select_filtro_por.val());
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "cadena_busqueda" + signo_separador + $campo_busqueda.val() + "|";
		valor_retorno += "filtro_por" + signo_separador + $select_filtro_por.val() + "|";
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
		$campo_busqueda.val('');
		$select_filtro_por.find('option[index=0]').attr('selected','selected');
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
		$('#forma-prospectos-window').find('#submit').mouseover(function(){
			$('#forma-prospectos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-prospectos-window').find('#submit').mouseout(function(){
			$('#forma-prospectos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-prefacturas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-prospectos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-prospectos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-prospectos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-prospectos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-prospectos-window').find('#close').mouseover(function(){
			$('#forma-prospectos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-prospectos-window').find('#close').mouseout(function(){
			$('#forma-prospectos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-prospectos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-prospectos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-prospectos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-prospectos-window').find("ul.pestanas li").click(function() {
			$('#forma-prospectos-window').find(".contenidoPes").hide();
			$('#forma-prospectos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-prospectos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			
			if(activeTab == '#tabx-1'){
				if($('#forma-prospectos-window').find('input[name=consignacion]').is(':checked')){
					$('#forma-prospectos-window').find('#div_consignacion_grid').css({'display':'block'});
					$('#forma-prospectos-window').find('.prospectos_div_one').css({'height':'565px'});
					$('#forma-prospectos-window').find('.prospectos_div_one').css({'width':'810px'});
					$('#forma-prospectos-window').find('.prospectos_div_two').css({'width':'810px'});
					$('#forma-prospectos-window').find('.prospectos_div_three').css({'width':'800px'});
					$('#forma-prospectos-window').find('#cierra').css({'width':'765px'});
					$('#forma-prospectos-window').find('#botones').css({'width':'790px'});
				}else{
					$('#forma-prospectos-window').find('#div_consignacion_grid').css({'display':'none'});
					$('#forma-prospectos-window').find('.prospectos_div_one').css({'height':'470px'});
					$('#forma-prospectos-window').find('.prospectos_div_one').css({'width':'810px'});
					$('#forma-prospectos-window').find('.prospectos_div_two').css({'width':'810px'});
					$('#forma-prospectos-window').find('.prospectos_div_three').css({'width':'800px'});
					$('#forma-prospectos-window').find('#cierra').css({'width':'765px'});
					$('#forma-prospectos-window').find('#botones').css({'width':'790px'});
				}
				
			}
			if(activeTab == '#tabx-2'){
				$('#forma-prospectos-window').find('.prospectos_div_one').css({'height':'290px'});
				$('#forma-prospectos-window').find('.prospectos_div_three').css({'height':'270px'});
				$('#forma-prospectos-window').find('.prospectos_div_one').css({'width':'810px'});
				$('#forma-prospectos-window').find('.prospectos_div_two').css({'width':'810px'});
				$('#forma-prospectos-window').find('.prospectos_div_three').css({'width':'800px'});
				$('#forma-prospectos-window').find('#cierra').css({'width':'765px'});
				$('#forma-prospectos-window').find('#botones').css({'width':'790px'});
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
	
	
	//nuevo prospecto
	$new_cliente.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel();
		
		//alert("si entra");
                
		//aqui entra nuevo
		var form_to_show = 'formaCrmProspectos';
		$('#' + form_to_show).each (function(){   this.reset(); });
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({ id : form_to_show + id_to_show });
		
		//alert("si entra");
		$('#forma-prospectos-window').css({ "margin-left": -400, 	"margin-top": -290 });
		$forma_selected.prependTo('#forma-prospectos-window');
		$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
		//alert("si pasa");
		$tabs_li_funxionalidad();
   
		//var $total_tr = $('#forma-prospectos-window').find('input[name=total_tr]');
		var $campo_prospecto = $('#forma-prospectos-window').find('input[name=prospecto]');
		var $campo_id_prospecto = $('#forma-prospectos-window').find('input[name=identificador_prospecto]');
		var $select_tipo_estado = $('#forma-prospectos-window').find('select[name=status]');
		var $campo_rfc = $('#forma-prospectos-window').find('input[name=rfc]');
		var $select_estatus_prospecto = $('#forma-prospectos-window').find('select[name=estatus]');
		var $select_tipo_prospecto = $('#forma-prospectos-window').find('select[name=tipoprospecto]');
		var $campo_calle = $('#forma-prospectos-window').find('input[name=calle]');
		var $campo_numero = $('#forma-prospectos-window').find('input[name=numero_int]');
		var $campo_numero_ext = $('#forma-prospectos-window').find('input[name=numero_ext]');
		var $campo_entrecalles = $('#forma-prospectos-window').find('input[name=entrecalles]');
		var $campo_colonia = $('#forma-prospectos-window').find('input[name=colonia]');
		var $campo_cp = $('#forma-prospectos-window').find('input[name=cp]');
		var $select_pais = $('#forma-prospectos-window').find('select[name=pais]');
		var $select_entidad = $('#forma-prospectos-window').find('select[name=estado]');
		var $select_localidad = $('#forma-prospectos-window').find('select[name=municipio]');
		var $campo_loc_alternativa = $('#forma-prospectos-window').find('input[name=loc_alternativa]');
		
		
		
		var $campo_email = $('#forma-prospectos-window').find('input[name=email]');
		var $campo_tel1 = $('#forma-prospectos-window').find('input[name=tel1]');
		var $campo_ext1 = $('#forma-prospectos-window').find('input[name=ext1]');
		var $campo_fax = $('#forma-prospectos-window').find('input[name=fax]');
		var $campo_tel2 = $('#forma-prospectos-window').find('input[name=tel2]');
		var $campo_ext2 = $('#forma-prospectos-window').find('input[name=ext2]');
		var $campo_contacto = $('#forma-prospectos-window').find('input[name=contacto]');

		//Extras
		var $select_clasificacion = $('#forma-prospectos-window').find('select[name=clasificacion]');
		var $select_tipo_industria = $('#forma-prospectos-window').find('select[name=tipoindustria]');
		var $txtarea_observaciones =$('#forma-prospectos-window').find('textarea[name=observaciones]');
	   
		//variables para cerrar el plugin
		var $cerrar_plugin = $('#forma-prospectos-window').find('#close');
		var $cancelar_plugin = $('#forma-prospectos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-prospectos-window').find('#submit');
		
		$campo_id_prospecto.attr({ 'value' : 0 });      

		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("Prospecto dado de alta", 'Atencion!');
				var remove = function() { $(this).remove(); };
				$('#forma-prospectos-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}
			else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-prospectos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-prospectos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({	tooltipId: "easyTooltip2",content: tmp.split(':')[1] });
					}
				}
			}
		}
		var options = { dataType :  'json', success : respuestaProcesada };
		$forma_selected.ajaxForm(options);
        
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_Prospectos.json';
		parametros = {'id':id_to_show,
		            'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		$.post(input_json,parametros,function(entry){
                    
		 //Alimentando los campos select de las pais
			$select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
			$.each(entry['Paises'],function(entryIndex,pais){
					pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
			});
			$select_pais.append(pais_hmtl);
			
			$select_tipo_estado.children().remove();
			var estado="";
				//estado+="<option value='0' selected='yes'> Selecciones un Tipo prospecto</option> ";
				estado+="<option value='1' selected='yes'>Activo</option> ";
				estado+="<option value='2'>Inactivo</option> ";
			$select_tipo_estado.append(estado);
			
			//alimentando los tipos de prospectos
			 $select_tipo_prospecto.children().remove();
			var tipo_prospecto = '<option value="0" selected="yes">[-Seleccionar Tipo-]</option>';
			$.each(entry['Tipo_prospectos'],function(entryIndex,tipoprospecto){
					tipo_prospecto += '<option value="' + tipoprospecto['id'] + '"  >' + tipoprospecto['tipo_prospecto'] + '</option>';
			});
			$select_tipo_prospecto.append(tipo_prospecto);
			
			//alimentando $select_estatus_prospecto
			 $select_estatus_prospecto.children().remove();
			var estatusprospecto = '<option value="0" selected="yes">[-Seleccionar etapa-]</option>';
			$.each(entry['Etapa_prospecto'],function(entryIndex,etapa){
				estatusprospecto += '<option value="' + etapa['id'] + '"  >' + etapa['etapa'] + '</option>';
			});
			$select_estatus_prospecto.append(estatusprospecto);
			
			//Clasificacion del prospecto
			$select_clasificacion.children().remove();
			var clasificacion_html = '<option value="0" selected="yes">[--Seleccione Clasificaci&oacute;n--]</option>';
			$.each(entry['Clasificacion'],function(entryIndex,clasificacion){
					clasificacion_html += '<option value="' + clasificacion['id'] + '"  >' + clasificacion['clasificacion_abr'] + '</option>';
			});
			$select_clasificacion.append(clasificacion_html);
		  
			//Cargandoo $select_tipo_industria 
			$select_tipo_industria.children().remove();
			var tipoindustria = '<option value="0" selected="yes">[--Seleccione Tipo Industria--]</option>';
			$.each(entry['Tipo_industria'],function(entryIndex,clasificacion){
					tipoindustria += '<option value="' + clasificacion['id'] + '"  >' + clasificacion['tipo_industria'] + '</option>';
			});
			$select_tipo_industria.append(tipoindustria);
		},"json");//termina llamada json
		
		
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
		
                
		$cerrar_plugin.bind('click',function(){
			var remove = function() { $(this).remove(); };
			$('#forma-prospectos-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() { $(this).remove(); };
			$('#forma-prospectos-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});

	});
	
	
	var carga_formaProspectos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar el Prospecto seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("El Prospecto fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("El Prospecto no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaCrmProspectos';
			
			$('#' + form_to_show).each (function(){   this.reset(); });
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({ id : form_to_show + id_to_show });
			//var accion = "get_cliente";
			
			$(this).modalPanel();
			$('#forma-prospectos-window').css({ "margin-left": -400, 	"margin-top": -290 });
			
			$forma_selected.prependTo('#forma-prospectos-window');
			$forma_selected.find('.panelcito_modal').attr({ id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_Prospectos.json';
				$arreglo = {
					'id':id_to_show,
					'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				//Variables para los datos Basicos del Prospecto
				var $campo_prospecto = $('#forma-prospectos-window').find('input[name=prospecto]');
				var $campo_id_prospecto = $('#forma-prospectos-window').find('input[name=identificador_prospecto]');
				var $select_tipo_estado = $('#forma-prospectos-window').find('select[name=status]');
				var $campo_rfc = $('#forma-prospectos-window').find('input[name=rfc]');
				var $select_estatus_prospecto = $('#forma-prospectos-window').find('select[name=estatus]');
				var $select_tipo_prospecto = $('#forma-prospectos-window').find('select[name=tipoprospecto]');
				var $campo_calle = $('#forma-prospectos-window').find('input[name=calle]');
				var $campo_numero = $('#forma-prospectos-window').find('input[name=numero_int]');
				var $campo_numero_ext = $('#forma-prospectos-window').find('input[name=numero_ext]');
				var $campo_entrecalles = $('#forma-prospectos-window').find('input[name=entrecalles]');
				var $campo_colonia = $('#forma-prospectos-window').find('input[name=colonia]');
				var $campo_cp = $('#forma-prospectos-window').find('input[name=cp]');
				var $select_pais = $('#forma-prospectos-window').find('select[name=pais]');
				var $select_entidad = $('#forma-prospectos-window').find('select[name=estado]');
				var $select_localidad = $('#forma-prospectos-window').find('select[name=municipio]');
				var $campo_loc_alternativa = $('#forma-prospectos-window').find('input[name=loc_alternativa]');
		
		
		
				var $campo_email = $('#forma-prospectos-window').find('input[name=email]');
				var $campo_tel1 = $('#forma-prospectos-window').find('input[name=tel1]');
				var $campo_ext1 = $('#forma-prospectos-window').find('input[name=ext1]');
				var $campo_fax = $('#forma-prospectos-window').find('input[name=fax]');
				var $campo_tel2 = $('#forma-prospectos-window').find('input[name=tel2]');
				var $campo_ext2 = $('#forma-prospectos-window').find('input[name=ext2]');
				var $campo_contacto = $('#forma-prospectos-window').find('input[name=contacto]');

				//Extras
				var $select_clasificacion = $('#forma-prospectos-window').find('select[name=clasificacion]');
				var $select_tipo_industria = $('#forma-prospectos-window').find('select[name=tipoindustria]');
				var $txtarea_observaciones =$('#forma-prospectos-window').find('textarea[name=observaciones]');

				//variables para cerrar el plugin
				var $cerrar_plugin = $('#forma-prospectos-window').find('#close');
				var $cancelar_plugin = $('#forma-prospectos-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-prospectos-window').find('#submit');

				//$campo_id_prospecto.attr({ 'value' : 0 });
				
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						jAlert("Los datos del prospecto se han actualizado.", 'Atencion!');
						var remove = function() { $(this).remove(); };
						$('#forma-prospectos-overlay').fadeOut(remove);
						
						$get_datos_grid();
						
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-prospectos-window').find('div.interrogacion').css({'display':'none'});

						//alert(data['success']);
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-prospectos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({	
									tooltipId: "easyTooltip2",content: tmp.split(':')[1] 
								});
							}
						}
					}
				}
	
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);

				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
						$select_tipo_estado.children().remove();
				    var estatus ='';
					if(parseInt(entry['Prospecto'][0]['estatus']) == 1){
						estatus= '<option value="1" selected="yes">Activo</option>';
						estatus += '<option value="2">Inactivo</option>';
					}else{
						estatus= '<option value="1">Activo</option>';
						estatus += '<option value="2" selected="yes">Inactivo</option>';
					}
					$select_tipo_estado.append(estatus);
                                    
                                   
					
					$campo_id_prospecto.attr({ 'value' : entry['Prospecto']['0']['id_prospecto'] });
					$campo_prospecto.attr({ 'value' : entry['Prospecto']['0']['razon_social'] });
				   // $campo_nocontrol.attr({ 'value' : entry['Prospecto']['0']['numero_control'] });
					$campo_rfc.attr({ 'value' : entry['Prospecto']['0']['rfc'] });
				   // $campo_curp.attr({ 'value' : entry['Prospecto']['0']['curp'] });
					
					
					$campo_calle.attr({ 'value' : entry['Prospecto']['0']['calle'] });
					$campo_numero.attr({ 'value' : entry['Prospecto']['0']['numero'] });
					$campo_entrecalles.attr({ 'value' : entry['Prospecto']['0']['entre_calles'] });
					$campo_numero_ext.attr({ 'value' : entry['Prospecto']['0']['numero_exterior'] });
					$campo_colonia.attr({ 'value' : entry['Prospecto']['0']['colonia'] });
					$campo_cp.attr({ 'value' : entry['Prospecto']['0']['cp'] });
					$campo_loc_alternativa.attr({ 'value' : entry['Prospecto']['0'][''] });
					$campo_email.attr({ 'value' : entry['Prospecto']['0']['email'] });
					$campo_tel1.attr({ 'value' : entry['Prospecto']['0']['telefono1'] });
					$campo_ext1.attr({ 'value' : entry['Prospecto']['0']['extension1'] });
					$campo_fax.attr({ 'value' : entry['Prospecto']['0']['fax'] });
					$campo_tel2.attr({ 'value' : entry['Prospecto']['0']['telefono2'] });
					$campo_ext2.attr({ 'value' : entry['Prospecto']['0']['extension2'] });
					$campo_contacto.attr({ 'value' : entry['Prospecto']['0']['contacto'] });
					$txtarea_observaciones.text(entry['Prospecto']['0']['observaciones']);
		
					//Alimentando los campos select de las pais
					$select_pais.children().remove();
					var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Prospecto']['0']['pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_pais.append(pais_hmtl);
		
					//Alimentando los campos select del estado
					$select_entidad.children().remove();
					var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar entidad--]</option>';
					$.each(entry['Entidades'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Prospecto']['0']['estado_id']){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_entidad.append(entidad_hmtl);
					
					//Alimentando los campos select de los municipios
					$select_localidad.children().remove();
					var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar municipio-]</option>';
					$.each(entry['Localidades'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Prospecto']['0']['municipio_id']){
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_localidad.append(localidad_hmtl);
	
					//carga select de clasificacion
					$select_clasificacion.children().remove();
					var hmtl_class = '<option value="0">[-- Clasificacion --]</option>';
					$.each(entry['Prospecto'],function(entryIndex,classificacion){
						if(entry['Prospecto']['0']['clasificacion_id'] == classificacion['id']){
							hmtl_class += '<option value="' + classificacion['id'] + '" selected="yes" >' + classificacion['nombre_vendedor'] + '</option>';
						}else{
							hmtl_class += '<option value="' + classificacion['id'] + '">' + classificacion['nombre_vendedor'] + '</option>';
						}
					});
					$select_clasificacion.append(hmtl_class);
	
					 
					//carga  $select_tipo_industria
					$select_tipo_industria.children().remove();
					var hmtl_T_i = '<option value="0">[-- Tipo Industria --]</option>';
					$.each(entry['Tipo_industria'],function(entryIndex,tipoindustria){
						if(tipoindustria['id'] == entry['Prospecto']['0']['tipo_industria_id']){
							hmtl_T_i += '<option value="' + tipoindustria['id'] + '"  selected="yes">' + tipoindustria['tipo_industria'] + '</option>';
						}else{
							hmtl_T_i += '<option value="' + tipoindustria['id'] + '"  >' + tipoindustria['tipo_industria'] + '</option>';
						}
					});
					$select_tipo_industria.append(hmtl_T_i);
					
					
				  //carga el select del tipo  prospecto                  
				   $select_tipo_prospecto.children().remove();
					var tipo_prospecto = '<option value="0">[-Tipo Prospecto-]</option>';
					$.each(entry['Tipo_prospectos'],function(entryIndex,tipoprospecto){
						if(tipoprospecto['id'] == entry['Prospecto']['0']['tipo_prospecto_id']){
							tipo_prospecto += '<option value="' + tipoprospecto['id'] + '"  selected="yes">' + tipoprospecto['tipo_prospecto'] + '</option>';
						}else{
							tipo_prospecto += '<option value="' + tipoprospecto['id'] + '"  >' + tipoprospecto['tipo_prospecto'] + '</option>';
						}
					});
					$select_tipo_prospecto.append(tipo_prospecto);  
					
					
					//carga el select_estatus_prospecto                  
				   $select_estatus_prospecto.children().remove();
					var etapa = '<option value="0">[- Selecciiona una etapa-]</option>';
					$.each(entry['Etapa_prospecto'],function(entryIndex,etapaprosp){
						if(etapaprosp['id'] == entry['Prospecto'][0]['etapas_deventa_id']){
							etapa += '<option value="' + etapaprosp['id'] + '"  selected="yes">' + etapaprosp['etapa'] + '</option>';
						}else{
							etapa += '<option value="' + etapaprosp['id'] + '"  >' + etapaprosp['etapa'] + '</option>';
						}
					});
					$select_estatus_prospecto.append(etapa); 
					
					
					//carga el $select_clasificacion                  
				   $select_clasificacion.children().remove();
					var etapa = '<option value="0">[- Seleccion una etapa de venta-]</option>';
					$.each(entry['Clasificacion'],function(entryIndex,clasif){
						if(clasif['id'] == entry['Prospecto']['0']['clasificacion_id']){
							etapa += '<option value="' + clasif['id'] + '"  selected="yes">' + clasif['clasificacion_abr'] + '</option>';
						}else{
							etapa += '<option value="' + clasif['id'] + '"  >' + clasif['clasificacion_abr'] + '</option>';
						}
					});
					$select_clasificacion.append(etapa); 
					
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
						
				},"json");//termina llamada json

				$submit_actualizar.bind('click',function(){
						
				});
	
	
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
						var remove = function() { $(this).remove(); };
						$('#forma-prospectos-overlay').fadeOut(remove);
				});

				$cerrar_plugin.bind('click',function(){
						var remove = function() { $(this).remove(); };
						$('#forma-prospectos-overlay').fadeOut(remove);
						$buscar.trigger('click');
				});	
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProspects.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getProspects.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaProspectos00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
	
    $get_datos_grid();
    
    
});



