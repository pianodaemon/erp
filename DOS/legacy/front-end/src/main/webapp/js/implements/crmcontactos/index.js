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
	var controller = $contextpath.val()+"/controllers/crmcontactos";
        
	//Barra para las acciones
	$('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
	$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	
	var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Contactos');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_nombre = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_nombre]');
	var $busquedatipo_contacto = $('#barra_buscador').find('.tabla_buscador').find('select[name=busquedatipo_contacto]');
	var $busqueda_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_agente]');
        
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limbuscarpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
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
        
        
        
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "nombre" + signo_separador + $busqueda_nombre.val() + "|";
		valor_retorno += "busquedatipo_contacto" + signo_separador + $busquedatipo_contacto.val() + "|";
		valor_retorno += "busqueda_agente" + signo_separador + $busqueda_agente.val() + "|";
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
		$busqueda_nombre.val(' ');
		
		$busquedatipo_contacto.children().remove();
		$busquedatipo_contacto.append('<option value="0">[-Seleccionar-]</option>');
		$busquedatipo_contacto.append('<option value="1">Cliente</option>');
		$busquedatipo_contacto.append('<option value="2">Prospecto</option>');
		
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
			
			$get_datos_grid();
		});
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
		
		$busqueda_nombre.focus();
		
	});
	
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_nombre, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busquedatipo_contacto, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_agente, $buscar);
	
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
        
        
	$tabs_li_funxionalidad = function(){
		
		$('#forma-crmcontactos-window').find('#submit').mouseover(function(){
			$('#forma-crmcontactos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-crmcontactos-window').find('#submit').mouseout(function(){
			$('#forma-crmcontactos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-crmcontactos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-crmcontactos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-crmcontactos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-crmcontactos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-crmcontactos-window').find('#close').mouseover(function(){
			$('#forma-crmcontactos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-crmcontactos-window').find('#close').mouseout(function(){
			$('#forma-crmcontactos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		$('#forma-crmcontactos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-crmcontactos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-crmcontactos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-crmcontactos-window').find("ul.pestanas li").click(function() {
			$('#forma-crmcontactos-window').find(".contenidoPes").hide();
			$('#forma-crmcontactos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-crmcontactos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
                
	}
        
        
	//buscador de Contactos
	$Pluguin_cliente_prospecto = function(busqueda_inicial_rfc, busqueda_inicial_razon_soc ,buscado_por,nivel_ejecucion){
		//limpiar_campos_grids();
		$(this).modalPanel_BuscaCliente_prospecto();
		var $dialogoc =  $('#forma-buscacliente_prospecto-window');
                
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_clientes_prospectos').find('table.formaBusqueda_cliente_prospecto').clone());
		                                                                   
              // alert($dialogoc.append($('div.buscador_clientes_prospectos').find('table.formaBusqueda_cliente_prospecto')).html());
               
		$('#forma-buscacliente_prospecto-window').css({"margin-left": -180, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscacliente_prospecto-window').find('#tabla_resultado');
		 
		var $cliente_prospecto = $('#forma-crmregistrocasos-window').find('input[name=cliente_prospecto]');
		var $id_cliente_prospecto = $('#forma-crmregistrocasos-window').find('input[name=id_cliente_prospecto]');

		//nivel de ejecucion, 1=cliente, 2 prospecto
		if(nivel_ejecucion == 1){
			var $busqueda_id_cliente_prospecto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_id_cliente_prospecto]');
			var $busqueda_cliente_prospecto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente_prospecto]');
		}
		
		var $campo_buscador_razon_social = $('#forma-buscacliente_prospecto-window').find('input[name=buscador_razon_social]');
		var $campo_buscador_rfc = $('#forma-buscacliente_prospecto-window').find('input[name=buscador_rfc]');
		
		var $buscar_plugin_contacto = $('#forma-buscacliente_prospecto-window').find('#busca_contacto_modalbox');
		var $cancelar_plugin_busca_contacto = $('#forma-buscacliente_prospecto-window').find('#cencela');
		
		$campo_buscador_razon_social.val(busqueda_inicial_razon_soc);
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
		
		$cliente_prospecto.val(busqueda_inicial_rfc);
		
		//click buscar productos
		$buscar_plugin_contacto.click(function(event){
                    
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_cliente_prospecto.json';
			$arreglo = {'buscador_razon_social':$campo_buscador_razon_social.val(),'buscador_rfc':$campo_buscador_rfc.val(),
			'identificador_cliente_prospecto':buscado_por,'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				
				$.each(entry['array_cliente_prospecto'],function(entryIndex,prospecto){
					trr = '<tr>';
						trr += '<td width="140px">';
							trr += '<span class="cliente_prospecto">'+prospecto['numero_control']+'</span>';
							trr += '<input type="hidden" id="id_cliente" value="'+prospecto['id']+'">';
						trr += '</td>';
						trr += '<td width="350px"><span class="razon_social_buscador">'+prospecto['razon_social']+'</span></td>';
						trr += '<td width="110px"><span class="rfc_buscador">'+prospecto['rfc']+'</span></td>';
					trr += '</tr>';
					$tabla_resultados.append(trr);
				});
				
				$colorea_tr_grid($tabla_resultados);
				
				//seleccionar un producto del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					var id_cliente_prospecto=$(this).find('#id_cliente').val();
					
					var razon_social_buscador=$(this).find('span.razon_social_buscador').html();
					var rfc_buscador=$(this).find('span.rfc_buscador').html();
					
					$('#forma-crmcontactos-window').find('input[name=rfc]').val(rfc_buscador);
					$('#forma-crmcontactos-window').find('input[name=id_cliente]').val(id_cliente_prospecto);
					$('#forma-crmcontactos-window').find('input[name=razon_social]').val(razon_social_buscador);
					
					//oculta la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscacliente_prospecto-overlay').fadeOut(remove);
				});
			});
		});
                
            //si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
            if($campo_buscador_razon_social.val() != ''){
                 $buscar_plugin_contacto.trigger('click');
            }
            
            $cancelar_plugin_busca_contacto.click(function(event){
                //event.preventDefault();
                var remove = function() {$(this).remove();};
                $('#forma-buscacliente_prospecto-overlay').fadeOut(remove);
            });
	}//termina buscador de Cliente_Prospecto
        
        
	//nuevos puestos
	$new.click(function(event){
            event.preventDefault();
            var id_to_show = 0;
            $(this).modalPanel_CrmContactos();   //contacto al plug in 
            
            var form_to_show = 'formaCrmContactos';
            $('#' + form_to_show).each (function(){this.reset();});
            var $forma_selected = $('#' + form_to_show).clone();
            $forma_selected.attr({id : form_to_show + id_to_show});
            
            $('#forma-crmcontactos-window').css({"margin-left": -200, 	"margin-top": -220});
            $forma_selected.prependTo('#forma-crmcontactos-window');
            $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
            $tabs_li_funxionalidad();
            
            //campos de la vista
            var $campo_id = $('#forma-crmcontactos-window').find('input[name=identificador]'); 
            
            var $tipo_contacto=$('#forma-crmcontactos-window').find('select[name=tipo_contacto]');
            var $folio = $('#forma-crmcontactos-window').find('input[name=folio]');
            var $rfc = $('#forma-crmcontactos-window').find('input[name=rfc]');
            var $id_cliente = $('#forma-crmcontactos-window').find('input[name=id_cliente]');
            var $razon_social = $('#forma-crmcontactos-window').find('input[name=razon_social]');
            var $select_agente = $('#forma-crmcontactos-window').find('select[name=select_agente]');
            
            var $nombre = $('#forma-crmcontactos-window').find('input[name=nombre]');
            var $apellido_paterno = $('#forma-crmcontactos-window').find('input[name=apellido_paterno]');
            var $apellido_materno = $('#forma-crmcontactos-window').find('input[name=apellido_materno]');
            var $telefono_2 = $('#forma-crmcontactos-window').find('input[name=telefono_2]');
            var $telefono_1 = $('#forma-crmcontactos-window').find('input[name=telefono_1]');
            var $fax = $('#forma-crmcontactos-window').find('input[name=fax]');
            var $telefono_directo = $('#forma-crmcontactos-window').find('input[name=telefono_directo]');
            var $correo_1 = $('#forma-crmcontactos-window').find('input[name=correo_1]');
            var $correo_2 = $('#forma-crmcontactos-window').find('input[name=correo_2]');
            var $check_decisor = $('#forma-crmcontactos-window').find('input[name=check_decisor]');
            var $departamento = $('#forma-crmcontactos-window').find('input[name=departamento]');
            var $puesto = $('#forma-crmcontactos-window').find('input[name=puesto]');
            
            var $busca_cliente = $('#forma-crmcontactos-window').find('a[href*=busca_cliente]');
            
            //botones
            var $cerrar_plugin = $('#forma-crmcontactos-window').find('#close');
            var $cancelar_plugin = $('#forma-crmcontactos-window').find('#boton_cancelar');
            var $submit_actualizar = $('#forma-crmcontactos-window').find('#submit');
            
            $campo_id.attr({'value' : 0});
            
            var respuestaProcesada = function(data){
                    if ( data['success'] == "true" ){
                        jAlert("El contacto fue dado de alta con exito", 'Atencion!');
                        var remove = function() {$(this).remove();};
                        $('#forma-crmcontactos-overlay').fadeOut(remove);
                        
                        $get_datos_grid();
                    }else{
                        // Desaparece todas las interrogaciones si es que existen
                        $('#forma-crmcontactos-window').find('div.interrogacion').css({'display':'none'});
                        
                        var valor = data['success'].split('___');
                        //muestra las interrogaciones
                        for (var element in valor){
                            tmp = data['success'].split('___')[element];
                            longitud = tmp.split(':');
                            if( longitud.length > 1 ){
                                $('#forma-crmcontactos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                .parent()
                                .css({'display':'block'})
                                .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
                            }
                        }
                    }
            }
            var options = {dataType :  'json', success : respuestaProcesada};
            $forma_selected.ajaxForm(options);
            
            var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getContacto.json';
            var parametros={
                id:$campo_id.val(),
                iu: $('#lienzo_recalculable').find('input[name=iu]').val()
            }
            
            //alert($('#lienzo_recalculable').find('input[name=iu]').val())
            $.post(input_json,parametros,function(entry){
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
                
                
            });//termina llamada json
            
			$tipo_contacto.change(function(){
				$('#forma-crmcontactos-window').find('input[name=rfc]').val('');
				$('#forma-crmcontactos-window').find('input[name=id_cliente]').val(0);
				$('#forma-crmcontactos-window').find('input[name=razon_social]').val('');
				
				$busca_cliente.show();
				
				$rfc.focus();
			});
			
			
            //Buscar contacto
            $busca_cliente.click(function(event){
                event.preventDefault();
                if($tipo_contacto.val() != 0 ){
                    $Pluguin_cliente_prospecto($rfc.val(),$razon_social.val(),$tipo_contacto.val(), $tipo_contacto.val());
                }else{
                    jAlert("Elije una Opcion",'Atencion!!!');
                }
            });
            
            $cerrar_plugin.bind('click',function(){
                var remove = function() {$(this).remove();};
                $('#forma-crmcontactos-overlay').fadeOut(remove);
            });
            
            $cancelar_plugin.click(function(event){
                var remove = function() {$(this).remove();};
                $('#forma-crmcontactos-overlay').fadeOut(remove);
                $buscar.trigger('click');
            });
	});
    
    
        
	var carga_formaClientsgrupos_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			var $arreglo = {'id':id_to_show, 'iu': $('#lienzo_recalculable').find('input[name=iu]').val() };
			
			jConfirm('Realmente desea eliminar el contacto seleccionado', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if(entry['success']=='1'){
							jAlert("El contacto fue eliminado exitosamente", 'Atencion!');
							$get_datos_grid();
						}else{
							jAlert("El contacto  no pudo ser eliminado", 'Atencion!');
						}
					},"json");
				}
			});
            
		}else{
			//aqui  entra para editar un registro
			$(this).modalPanel_CrmContactos();   //contacto al plug in 
			
			var form_to_show = 'formaCrmContactos';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$('#forma-crmcontactos-window').css({"margin-left": -200, 	"margin-top": -220});
			$forma_selected.prependTo('#forma-crmcontactos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			$tabs_li_funxionalidad();
			
			//campos de la vista
			var $campo_id = $('#forma-crmcontactos-window').find('input[name=identificador]'); 

			var $tipo_contacto=$('#forma-crmcontactos-window').find('select[name=tipo_contacto]');
			var $folio = $('#forma-crmcontactos-window').find('input[name=folio]');
			var $rfc = $('#forma-crmcontactos-window').find('input[name=rfc]');
			var $id_cliente = $('#forma-crmcontactos-window').find('input[name=id_cliente]');
			var $razon_social = $('#forma-crmcontactos-window').find('input[name=razon_social]');
			var $select_agente = $('#forma-crmcontactos-window').find('select[name=select_agente]');
			
			var $nombre = $('#forma-crmcontactos-window').find('input[name=nombre]');
			var $apellido_paterno = $('#forma-crmcontactos-window').find('input[name=apellido_paterno]');
			var $apellido_materno = $('#forma-crmcontactos-window').find('input[name=apellido_materno]');
			var $telefono_2 = $('#forma-crmcontactos-window').find('input[name=telefono_2]');
			var $telefono_1 = $('#forma-crmcontactos-window').find('input[name=telefono_1]');
			var $fax = $('#forma-crmcontactos-window').find('input[name=fax]');
			var $telefono_directo = $('#forma-crmcontactos-window').find('input[name=telefono_directo]');
			var $correo_1 = $('#forma-crmcontactos-window').find('input[name=correo_1]');
			var $correo_2 = $('#forma-crmcontactos-window').find('input[name=correo_2]');
			var $observaciones = $('#forma-crmcontactos-window').find('textarea[name=observaciones]');
			var $check_decisor = $('#forma-crmcontactos-window').find('input[name=check_decisor]');
            var $departamento = $('#forma-crmcontactos-window').find('input[name=departamento]');
            var $puesto = $('#forma-crmcontactos-window').find('input[name=puesto]');
            
			var $busca_cliente = $('#forma-crmcontactos-window').find('a[href*=busca_cliente]');
			$busca_cliente.hide();
			
			//botones
			var $cerrar_plugin = $('#forma-crmcontactos-window').find('#close');
			var $cancelar_plugin = $('#forma-crmcontactos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-crmcontactos-window').find('#submit');
			
			$campo_id.attr({'value' : id_to_show});
			
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getContacto.json';
				$arreglo = {
					id:id_to_show,
					iu: $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						jAlert("El contacto fue actualizado con exito", 'Atencion!');
						var remove = function() {$(this).remove();};
						$('#forma-crmcontactos-overlay').fadeOut(remove);

						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-crmcontactos-window').find('div.interrogacion').css({'display':'none'});

						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-crmcontactos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$campo_id.val(entry['Contacto'][0]['id']);
					$folio.val(entry['Contacto'][0]['folio']);
					$nombre.val(entry['Contacto'][0]['nombre']);
					$apellido_paterno.val(entry['Contacto'][0]['apellido_paterno']);
					$apellido_materno.val(entry['Contacto'][0]['apellido_materno']);
					$telefono_2.val(entry['Contacto'][0]['telefono2']);
					$telefono_1.val(entry['Contacto'][0]['telefono1']);
					$fax.val(entry['Contacto'][0]['fax']);
					$telefono_directo.val(entry['Contacto'][0]['telefono_directo']);
					$correo_1.val(entry['Contacto'][0]['email']);
					$correo_2.val(entry['Contacto'][0]['email2']);
					$observaciones.text(entry['Contacto'][0]['observaciones']);
					$check_decisor.attr('checked',  (entry['Contacto'][0]['decisor'] == 'true')? true:false );
					$departamento.val(entry['Contacto'][0]['departamento']);
					$puesto.val(entry['Contacto'][0]['puesto']);
            
					$tipo_contacto.children().remove();
					$html_tipo = "";
					
					
					if(parseInt(entry['Contacto'][0]['tipo_contacto']) == 1){
						$tipo_contacto.append('<option value="1" selected="yes">Cliente</option>');
						$tipo_contacto.append('<option value="2">Prospecto</option>');
					}else{
						$tipo_contacto.append('<option value="1">Cliente</option>');
						$tipo_contacto.append('<option value="2" selected="yes">Prospecto</option>');
					}
					
					if(entry['Contacto']['0']['cliente'].trim() != "" && entry['Contacto']['0']['cliente'] != null ){
						$id_cliente.val(entry['Contacto']['0']['cliente'].split("___")[0]);
						$rfc.val(entry['Contacto']['0']['cliente'].split("___")[1]);
						$razon_social.val(entry['Contacto']['0']['cliente'].split("___")[2]);
					}
					
					//Alimentando los campos select_agente
					$select_agente.children().remove();
					var agente_hmtl='';
					$.each(entry['Agentes'],function(entryIndex,agente){
						if(parseInt(agente['id'])==parseInt(entry['Contacto'][0]['gral_empleado_id'])){
							agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
						}else{
							agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
						}
					});
					$select_agente.append(agente_hmtl);
				},"json");//termina llamada json
				
				
				$tipo_contacto.change(function(){
					$('#forma-crmcontactos-window').find('input[name=rfc]').val('');
					$('#forma-crmcontactos-window').find('input[name=id_cliente]').val(0);
					$('#forma-crmcontactos-window').find('input[name=razon_social]').val('');
					
					$busca_cliente.show();
					
					$rfc.focus();
				});
				
				//Buscar Cliente o Prospecto
				$busca_cliente.click(function(event){
					event.preventDefault();
					if($tipo_contacto.val() != 0 ){
						$Pluguin_cliente_prospecto($rfc.val(),$razon_social.val(),$tipo_contacto.val(), $tipo_contacto.val());
					}else{
						jAlert("Elije una Opcion",'Atencion!!!');
					}
				});
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-crmcontactos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() { $(this).remove(); };
					$('#forma-crmcontactos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getContactos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getContactos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaClientsgrupos_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});
