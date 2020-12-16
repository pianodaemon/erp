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
	var controller = $contextpath.val()+"/controllers/clientsdf";
    
        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_clientsdf = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Direcciones Fiscales de Clientes');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_nocontrol = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_nocontrol]');
	var $busqueda_razon_social = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_razon_social]');
	var $busqueda_rfc = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_rfc]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	//alert($select_filtro_por.val());
	
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
	
	
	$tabs_li_funxionalidad = function(){
		$('#forma-clientsdf-window').find('#submit').mouseover(function(){
			$('#forma-clientsdf-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-clientsdf-window').find('#submit').mouseout(function(){
			$('#forma-clientsdf-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-clientsdf-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-clientsdf-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-clientsdf-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-clientsdf-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-clientsdf-window').find('#close').mouseover(function(){
			$('#forma-clientsdf-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-clientsdf-window').find('#close').mouseout(function(){
			$('#forma-clientsdf-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-clientsdf-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-clientsdf-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-clientsdf-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-clientsdf-window').find("ul.pestanas li").click(function() {
			$('#forma-clientsdf-window').find(".contenidoPes").hide();
			$('#forma-clientsdf-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-clientsdf-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	


	//buscador de clientes
	$busca_clientes = function(razon_social_cliente, numero_control){
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
		
		if(numero_control !='' && razon_social_cliente==''){
			html+='<option value="1" selected="yes">No. de control</option>';
			$cadena_buscar.val(numero_control);
		}else{
			html+='<option value="1">No. de control</option>';
		}
		html+='<option value="2">RFC</option>';
		if(razon_social_cliente!=''){
			$cadena_buscar.val(razon_social_cliente);
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		if(numero_control =='' && razon_social_cliente==''){
			html+='<option value="3" selected="yes">Razon social</option>';
		}
		html+='<option value="4">CURP</option>';
		html+='<option value="5">Alias</option>';
		$select_filtro_por.append(html);
		
		//click buscar clientes
		$busca_cliente_modalbox.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorClientes.json';
			$arreglo = {'cadena':$cadena_buscar.val(),
						 'filtro':$select_filtro_por.val(),
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
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
					$('#forma-clientsdf-window').find('input[name=id_cliente]').val($(this).find('#idclient').val());
					$('#forma-clientsdf-window').find('input[name=rfc]').val($(this).find('span.rfc').html());
					$('#forma-clientsdf-window').find('input[name=cliente]').val($(this).find('span.razon').html());
					$('#forma-clientsdf-window').find('input[name=nocontrol]').val($(this).find('span.no_control').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente-overlay').fadeOut(remove);
					
					
					$('#forma-clientsdf-window').find('input[name=cliente]').focus();
				});

			});
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_buscar.val() != ''){
			$busca_cliente_modalbox.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $busca_cliente_modalbox);
		$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $busca_cliente_modalbox);
		
		$cancelar_plugin_busca_cliente.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-buscacliente-overlay').fadeOut(remove);
			
			$('#forma-clientsdf-window').find('input[name=cliente]').focus();
		});
		
		$cadena_buscar.focus();
	}//termina buscador de clientes

	
	
	//nuevo direcciones de proveedores
	$new_clientsdf.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_clientsdf();
		
		var form_to_show = 'formaDirecciones';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-clientsdf-window').css({"margin-left": -400, 	"margin-top": -265});
		$forma_selected.prependTo('#forma-clientsdf-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
		var $identificador = $('#forma-clientsdf-window').find('input[name=identificador]');
		var $busca_cliente = $('#forma-clientsdf-window').find('a[href*=busca_cliente]');
		var $id_cliente = $('#forma-clientsdf-window').find('input[name=id_cliente]');
		var $cliente = $('#forma-clientsdf-window').find('input[name=cliente]');
		var $nocontrol = $('#forma-clientsdf-window').find('input[name=nocontrol]');
		var $rfc = $('#forma-clientsdf-window').find('input[name=rfc]');
		var $calle = $('#forma-clientsdf-window').find('input[name=calle]');
		var $numero_int = $('#forma-clientsdf-window').find('input[name=numero_int]');
		var $numero_ext = $('#forma-clientsdf-window').find('input[name=numero_ext]');
		var $entrecalles = $('#forma-clientsdf-window').find('input[name=entrecalles]');
		var $colonia = $('#forma-clientsdf-window').find('input[name=colonia]');
		var $select_pais = $('#forma-clientsdf-window').find('select[name=select_pais]');
		var $select_estado = $('#forma-clientsdf-window').find('select[name=select_estado]');
		var $select_municipio = $('#forma-clientsdf-window').find('select[name=select_municipio]');
		var $contacto = $('#forma-clientsdf-window').find('input[name=contacto]');
		var $email = $('#forma-clientsdf-window').find('input[name=email]');
		var $tel1 = $('#forma-clientsdf-window').find('input[name=tel1]');
		var $ext1 = $('#forma-clientsdf-window').find('input[name=ext1]');
		var $fax = $('#forma-clientsdf-window').find('input[name=fax]');
		var $tel2 = $('#forma-clientsdf-window').find('input[name=tel2]');
		var $ext2 = $('#forma-clientsdf-window').find('input[name=ext2]');
		
		var $cerrar_plugin = $('#forma-clientsdf-window').find('#close');
		var $cancelar_plugin = $('#forma-clientsdf-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-clientsdf-window').find('#submit');
		
		//quitar enter a todos los campos input
		$('#forma-clientsdf-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
		//$nocontrol.css({'background' : '#DDDDDD'});
		$rfc.css({'background' : '#DDDDDD'});
		$identificador.attr({'value' : 0});
		$id_cliente.attr({'value' : 0});
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La direcci&oacute;n fue dada de alta con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-clientsdf-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-clientsdf-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					//telUno: Numero Telefonico no Valido___
					if( longitud.length > 1 ){
						$('#forma-clientsdf-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getclientsdf.json';
		$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
		
		$.post(input_json,$arreglo,function(entry){
			//Alimentando los campos select de las pais
			$select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar Pais-]</option>';
			$.each(entry['Paises'],function(entryIndex,pais){
				pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
			});
			$select_pais.append(pais_hmtl);
			
			var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>';
			$select_estado.children().remove();
			$select_estado.append(entidad_hmtl);
			
			var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
			$select_municipio.children().remove();
			$select_municipio.append(localidad_hmtl);
			
			
			//carga select estados al cambiar el pais
			$select_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$select_estado.children().remove();
					var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
					$.each(entry['Estados'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$select_estado.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
					$select_municipio.children().remove();
					$select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			//carga select municipios al cambiar el estado
			$select_estado.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $select_pais.val();
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$select_municipio.children().remove();
					var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
					$.each(entry['Municipios'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
		},"json");//termina llamada json
        
        //buscar cliente
        $busca_cliente.click(function(event){
			event.preventDefault();
			$busca_clientes($cliente.val(), $nocontrol.val());
        });
        
        
		//ejecutar busqueda de cliente al pulsar enter sobre el campo No de control
		$nocontrol.keypress(function(e){
			if(e.which == 13){
				var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoClient.json';
				$arreglo2 = {'no_control':$nocontrol.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				$.post(input_json2,$arreglo2,function(entry2){
					if(parseInt(entry2['Cliente'].length) > 0 ){
						$('#forma-clientsdf-window').find('input[name=id_cliente]').val(entry2['Cliente'][0]['id']);
						$('#forma-clientsdf-window').find('input[name=rfc]').val(entry2['Cliente'][0]['rfc']);
						$('#forma-clientsdf-window').find('input[name=cliente]').val(entry2['Cliente'][0]['razon_social']);
						$('#forma-clientsdf-window').find('input[name=nocontrol]').val(entry2['Cliente'][0]['numero_control']);
					}else{
						//limpiar campos
						$('#forma-clientsdf-window').find('input[name=id_cliente]').val('');
						$('#forma-clientsdf-window').find('input[name=rfc]').val('');
						$('#forma-clientsdf-window').find('input[name=cliente]').val('');
						$('#forma-clientsdf-window').find('input[name=nocontrol]').val('');
						
						jAlert('N&uacute;mero de cliente desconocido.', 'Atencion!', function(r) { 
							$('#forma-clientsdf-window').find('input[name=nocontrol]').focus(); 
						});
					}
				},"json");//termina llamada json
				
				return false;
			}
		});
		
        
        $(this).aplicarEventoKeypressEjecutaTrigger($cliente, $busca_cliente);
        
        
        
        
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-clientsdf-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-clientsdf-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		$nocontrol.focus();
	});
	
	
	
	
	
	var carga_formaDirecciones_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la direci&oacute;n seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La direcci&oacute;n  fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La direci&oacute;n no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaDirecciones';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_clientsdf();
			$('#forma-clientsdf-window').css({"margin-left": -400, 	"margin-top": -265});
			
			$forma_selected.prependTo('#forma-clientsdf-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $identificador = $('#forma-clientsdf-window').find('input[name=identificador]');
			var $busca_cliente = $('#forma-clientsdf-window').find('a[href*=busca_cliente]');
			var $id_cliente = $('#forma-clientsdf-window').find('input[name=id_cliente]');
			var $cliente = $('#forma-clientsdf-window').find('input[name=cliente]');
			var $nocontrol = $('#forma-clientsdf-window').find('input[name=nocontrol]');
			var $rfc = $('#forma-clientsdf-window').find('input[name=rfc]');
			var $calle = $('#forma-clientsdf-window').find('input[name=calle]');
			var $numero_int = $('#forma-clientsdf-window').find('input[name=numero_int]');
			var $numero_ext = $('#forma-clientsdf-window').find('input[name=numero_ext]');
			var $entrecalles = $('#forma-clientsdf-window').find('input[name=entrecalles]');
			var $colonia = $('#forma-clientsdf-window').find('input[name=colonia]');
			var $cp = $('#forma-clientsdf-window').find('input[name=cp]');
			var $select_pais = $('#forma-clientsdf-window').find('select[name=select_pais]');
			var $select_estado = $('#forma-clientsdf-window').find('select[name=select_estado]');
			var $select_municipio = $('#forma-clientsdf-window').find('select[name=select_municipio]');
			var $contacto = $('#forma-clientsdf-window').find('input[name=contacto]');
			var $email = $('#forma-clientsdf-window').find('input[name=email]');
			var $tel1 = $('#forma-clientsdf-window').find('input[name=tel1]');
			var $ext1 = $('#forma-clientsdf-window').find('input[name=ext1]');
			var $fax = $('#forma-clientsdf-window').find('input[name=fax]');
			var $tel2 = $('#forma-clientsdf-window').find('input[name=tel2]');
			var $ext2 = $('#forma-clientsdf-window').find('input[name=ext2]');
                        
			var $cerrar_plugin = $('#forma-clientsdf-window').find('#close');
			var $cancelar_plugin = $('#forma-clientsdf-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-clientsdf-window').find('#submit');
			
			$nocontrol.css({'background' : '#DDDDDD'});
			$cliente.css({'background' : '#DDDDDD'});
			$rfc.css({'background' : '#DDDDDD'});
			$busca_cliente.hide();
			
			$nocontrol.attr({ 'readOnly':true });
			$cliente.attr({ 'readOnly':true });
			
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getclientsdf.json';
				$arreglo = {'id':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("La direcci&oacute;n fue dada de alta con &eacute;xito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-clientsdf-overlay').fadeOut(remove);
						//refresh_table();
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-clientsdf-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
											 
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							//telUno: Numero Telefonico no Valido___
							if( longitud.length > 1 ){
								$('#forma-clientsdf-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
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
					$identificador.attr({'value' : entry['Datos']['0']['identificador']});
					$id_cliente.attr({'value' : entry['Datos']['0']['id_cliente']});
					$cliente.attr({'value' : entry['Datos']['0']['cliente']});
					$nocontrol.attr({'value' : entry['Datos']['0']['numero_control']});
					$rfc.attr({'value' : entry['Datos']['0']['rfc']});
					$calle.attr({'value' : entry['Datos']['0']['calle']});
					$numero_int.attr({'value' : entry['Datos']['0']['numero_interior']});
					$numero_ext.attr({'value' : entry['Datos']['0']['numero_exterior']});
					$entrecalles.attr({'value' : entry['Datos']['0']['entre_calles']});
					$colonia.attr({'value' : entry['Datos']['0']['colonia']});
					$cp.attr({'value' : entry['Datos']['0']['cp']});
					$contacto.attr({'value' : entry['Datos']['0']['contacto']});
					$email.attr({'value' : entry['Datos']['0']['email']});
					$tel1.attr({'value' : entry['Datos']['0']['telefono1']});
					$ext1.attr({'value' : entry['Datos']['0']['extension1']});
					$fax.attr({'value' : entry['Datos']['0']['fax']});
					$tel2.attr({'value' : entry['Datos']['0']['telefono2']});
					$ext2.attr({'value' : entry['Datos']['0']['extension2']});
					
					//Alimentando los campos select de las pais
					$select_pais.children().remove();
					var pais_hmtl = '<option value="0" >[-Seleccionar Pais-]</option>';
					$.each(entry['Paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Datos']['0']['pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
					$select_pais.append(pais_hmtl);
					
					//Alimentando los campos select del estado
					$select_estado.children().remove();
					var entidad_hmtl = '<option value="00"  >[-Seleccionar Estado--]</option>';
					$.each(entry['Estados'],function(entryIndex,entidad){
						if(entidad['cve_ent'] == entry['Datos']['0']['estado_id']){
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  selected="yes">' + entidad['nom_ent'] + '</option>';
						}else{
							entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
						}
					});
					$select_estado.append(entidad_hmtl);
					
					
					//Alimentando los campos select de los municipios
					$select_municipio.children().remove();
					var localidad_hmtl = '<option value="00" >[-Seleccionar Municipio-]</option>';
					$.each(entry['Municipios'],function(entryIndex,mun){
						if(mun['cve_mun'] == entry['Datos']['0']['municipio_id']){
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  selected="yes">' + mun['nom_mun'] + '</option>';
						}else{
							localidad_hmtl += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
						}
					});
					$select_municipio.append(localidad_hmtl);
					
					
					//carga select estados al cambiar el pais
					$select_pais.change(function(){
						var valor_pais = $(this).val();
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEstados.json';
						$arreglo = {'id_pais':valor_pais};
						$.post(input_json,$arreglo,function(entry){
							$select_estado.children().remove();
							var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar Estado--]</option>'
							$.each(entry['Estados'],function(entryIndex,entidad){
								entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
							});
							$select_estado.append(entidad_hmtl);
							var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>';
							$select_municipio.children().remove();
							$select_municipio.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
					//carga select municipios al cambiar el estado
					$select_estado.change(function(){
						var valor_entidad = $(this).val();
						var valor_pais = $select_pais.val();
						
						var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getMunicipios.json';
						$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
						$.post(input_json,$arreglo,function(entry){
							$select_municipio.children().remove();
							var trama_hmtl_localidades = '<option value="00" selected="yes" >[-Seleccionar Municipio-]</option>'
							$.each(entry['Municipios'],function(entryIndex,mun){
								trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
							});
							$select_municipio.append(trama_hmtl_localidades);
						},"json");//termina llamada json
					});
					
				},"json");//termina llamada json
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsdf-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-clientsdf-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
				$calle.focus();
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllClientsDf.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllClientsDf.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
