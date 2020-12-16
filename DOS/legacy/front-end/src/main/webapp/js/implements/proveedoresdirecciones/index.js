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
	var controller = $contextpath.val()+"/controllers/proveedoresdirecciones";
    
        //Barra para las acciones
        $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
        $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_proveedoresdirecciones = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Direcciones de Proveedores');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var busqueda_proveedor = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_proveedor]');
        busqueda_proveedor .attr({'readOnly':true});
        var $id_proveedorBuscador =$('#barra_buscador').find('.tabla_buscador').find('input[name=id_proveedorBuscador]');
	var $busca_proveedorBuscador =$('#barra_buscador').find('.tabla_buscador').find('a[href*=busca_clienteBuscador]');
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
        
       
        
        //buscador de proveedores
	$busca_proveedores = function(){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({"margin-left": -200, 	"margin-top": -200});
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
		var $campo_email = $('#forma-buscaproveedor-window').find('input[name=campo_email]');
		var $campo_nombre = $('#forma-buscaproveedor-window').find('input[name=campo_nombre]');
		
		var $buscar_plugin_proveedor = $('#forma-buscaproveedor-window').find('#busca_proveedor_modalbox');
		var $cancelar_plugin_busca_proveedor = $('#forma-buscaproveedor-window').find('#cencela');
			
		$('#forma-provfacturas-window').find('input[name=tipo_proveedor]').val('');
			
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
	
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuacadorProveedores.json';
			$arreglo = {'rfc':$campo_rfc.val(),
							'email':$campo_email.val(),
							'nombre':$campo_nombre.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
                                    
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							trr += '<input type="hidden" id="dias_cred_id" value="'+proveedor['dias_credito_id']+'">';
							trr += '<span class="rfc">'+proveedor['rfc']+'</span>';
						trr += '</td>';
						trr += '<td width="250"><span id="razon_social">'+proveedor['razon_social']+'</span></td>';
						trr += '<td width="250"><span class="direccion">'+proveedor['direccion']+'</span></td>';
					trr += '</tr>';
					
					$tabla_resultados.append(trr);
				});
				$tabla_resultados.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});

				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({background : '#FBD850'});
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
					//asignar a los campos correspondientes el sku y y descripcion
					$('#forma-provfacturas-window').find('input[name=id_proveedor]').val($(this).find('#id_prov').val());
					$('#forma-provfacturas-window').find('input[name=tipo_proveedor]').val($(this).find('#tipo_prov').val());
					$('#forma-provfacturas-window').find('input[name=rfcproveedor]').val($(this).find('span.rfc').html());
					$('#forma-provfacturas-window').find('input[name=razon_proveedor]').val($(this).find('#razon_social').html());
					//$('#forma-provfacturas-window').find('input[name=dir_proveedor]').val($(this).find('span.direccion').html());
					$('#forma-provfacturas-window').find('select[name=tipo_factura]').find('option[value="'+$(this).find('#dias_cred_id').val()+'"]').attr('selected','selected');
					
                                        
                                        $id_proveedorBuscador.val($(this).find('#id_prov').val());
                                        busqueda_proveedor.val($(this).find('#razon_social').html());
                                        //elimina la ventana de busqueda
					var remove = function() {$(this).remove(); 
                                        };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
				});
			});
		});
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
		});
	}//termina buscador de proveedores

        
        //buscar proveedor
        $busca_proveedorBuscador.click(function(event){
                event.preventDefault();
                $busca_proveedores();
        });
        
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
                if($id_proveedorBuscador.val()==''){
                    $id_proveedorBuscador.val(0);
                }
		valor_retorno += "id_proveedor" + signo_separador + $id_proveedorBuscador.val() + "|";
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		
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
		busqueda_proveedor.val('');
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
	});
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-proveedoresdirecciones-window').find('#submit').mouseover(function(){
			$('#forma-proveedoresdirecciones-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-proveedoresdirecciones-window').find('#submit').mouseout(function(){
			$('#forma-proveedoresdirecciones-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-proveedoresdirecciones-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-proveedoresdirecciones-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-proveedoresdirecciones-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-proveedoresdirecciones-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-proveedoresdirecciones-window').find('#close').mouseover(function(){
			$('#forma-proveedoresdirecciones-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-proveedoresdirecciones-window').find('#close').mouseout(function(){
			$('#forma-proveedoresdirecciones-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-proveedoresdirecciones-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-proveedoresdirecciones-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-proveedoresdirecciones-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-proveedoresdirecciones-window').find("ul.pestanas li").click(function() {
			$('#forma-proveedoresdirecciones-window').find(".contenidoPes").hide();
			$('#forma-proveedoresdirecciones-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-proveedoresdirecciones-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	//nuevo direcciones de proveedores
	$new_proveedoresdirecciones.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_proveedoresdirecciones();
		
		var form_to_show = 'formaDirecciones';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-proveedoresdirecciones-window').css({"margin-left": -250, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-proveedoresdirecciones-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();		
		
                var $busca_proveedor = $('#forma-proveedoresdirecciones-window').find('a[href*=busca_cliente]');
                var $id_proveedor = $('#forma-proveedoresdirecciones-window').find('input[name=id_proveedor]');
                var $campo_id = $('#forma-proveedoresdirecciones-window').find('input[name=identificador]');
		var $campo_proveedor = $('#forma-proveedoresdirecciones-window').find('input[name=proveedor]');
                $campo_proveedor .attr({'readOnly':true});                
                var $campo_calle = $('#forma-proveedoresdirecciones-window').find('input[name=calle]');
                var $campo_entreCalles = $('#forma-proveedoresdirecciones-window').find('input[name=entreCalles]');
                var $campo_numeroInterior = $('#forma-proveedoresdirecciones-window').find('input[name=numInterior]');
                var $campo_numeroExterior = $('#forma-proveedoresdirecciones-window').find('input[name=numExterior]');
                var $campo_colonia = $('#forma-proveedoresdirecciones-window').find('input[name=colonia]');
                var $campo_select_pais = $('#forma-proveedoresdirecciones-window').find('select[name=select_pais]');
                var $campo_select_estado = $('#forma-proveedoresdirecciones-window').find('select[name=select_estado]');
                var $campo_select_municipio = $('#forma-proveedoresdirecciones-window').find('select[name=select_municipio]');
                var $campo_codigoPostal = $('#forma-proveedoresdirecciones-window').find('input[name=codigoPostal]');
                var $campo_telUno = $('#forma-proveedoresdirecciones-window').find('input[name=telUno]');
                var $campo_extUno = $('#forma-proveedoresdirecciones-window').find('input[name=extUno]');
                var $campo_telDos = $('#forma-proveedoresdirecciones-window').find('input[name=telDos]');
                var $campo_extDos = $('#forma-proveedoresdirecciones-window').find('input[name=extDos]');                
		
		var $cerrar_plugin = $('#forma-proveedoresdirecciones-window').find('#close');
		var $cancelar_plugin = $('#forma-proveedoresdirecciones-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-proveedoresdirecciones-window').find('#submit');
		
                        
		$campo_id.attr({'value' : 0});
                $id_proveedor.attr({'value' : 0});
		var respuestaProcesada = function(data){
                    
                        if ( data['success'] == "true" ){
				jAlert("La direccion fue dada de alta con &eacute;xito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-proveedoresdirecciones-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-proveedoresdirecciones-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
                                     
                                //muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
                                        longitud = tmp.split(':');
                                        //telUno: Numero Telefonico no Valido___
                                        if( longitud.length > 1 ){
                                                $('#forma-proveedoresdirecciones-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')						
                                                .parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
                                        }
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProveedoresDirecciones.json';
		$arreglo = {'id':id_to_show};
		
		$.post(input_json,$arreglo,function(entry){
		
                       $campo_select_pais.children().remove();
			var pais_hmtl = '<option value="0" selected="yes">[-Seleccionar pais-]</option>';
			$.each(entry['paises'],function(entryIndex,pais){
				pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
			});
			$campo_select_pais.append(pais_hmtl);
                        
                        var entidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar entidad--]</option>';
			$campo_select_estado.children().remove();
			$campo_select_estado.append(entidad_hmtl);
                        
                        var localidad_hmtl = '<option value="00" selected="yes" >[-Seleccionar municipio-]</option>';
			$campo_select_municipio.children().remove();
			$campo_select_municipio.append(localidad_hmtl);
                        
                        //carga select estados al cambiar el pais
			$campo_select_pais.change(function(){
				var valor_pais = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
				$arreglo = {'id_pais':valor_pais};
				$.post(input_json,$arreglo,function(entry){
					$campo_select_estado.children().remove();
					var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
					$.each(entry['Entidades'],function(entryIndex,entidad){
						entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
					});
					$campo_select_estado.append(entidad_hmtl);
					var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';
					$campo_select_municipio.children().remove();
					$campo_select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
			
			//carga select municipios al cambiar el estado
			$campo_select_estado.change(function(){
				var valor_entidad = $(this).val();
				var valor_pais = $campo_select_pais.val();
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
				$arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
				$.post(input_json,$arreglo,function(entry){
					$campo_select_municipio.children().remove();
					var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
					$.each(entry['Localidades'],function(entryIndex,mun){
						trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
					});
					$campo_select_municipio.append(trama_hmtl_localidades);
				},"json");//termina llamada json
			});
		},"json");//termina llamada json
                
                //buscador de proveedores
	$busca_proveedores = function(){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({"margin-left": -200, 	"margin-top": -200});
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
		var $campo_email = $('#forma-buscaproveedor-window').find('input[name=campo_email]');
		var $campo_nombre = $('#forma-buscaproveedor-window').find('input[name=campo_nombre]');
		
		var $buscar_plugin_proveedor = $('#forma-buscaproveedor-window').find('#busca_proveedor_modalbox');
		var $cancelar_plugin_busca_proveedor = $('#forma-buscaproveedor-window').find('#cencela');
			
		$('#forma-provfacturas-window').find('input[name=tipo_proveedor]').val('');
			
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
	
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			//event.preventDefault();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuacadorProveedores.json';
			$arreglo = {'rfc':$campo_rfc.val(),
							'email':$campo_email.val(),
							'nombre':$campo_nombre.val(),
							'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
						}
                                    
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							trr += '<input type="hidden" id="dias_cred_id" value="'+proveedor['dias_credito_id']+'">';
							trr += '<span class="rfc">'+proveedor['rfc']+'</span>';
						trr += '</td>';
						trr += '<td width="250"><span id="razon_social">'+proveedor['razon_social']+'</span></td>';
						trr += '<td width="250"><span class="direccion">'+proveedor['direccion']+'</span></td>';
					trr += '</tr>';
					
					$tabla_resultados.append(trr);
				});
				$tabla_resultados.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
				$tabla_resultados.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});

				$('tr:odd' , $tabla_resultados).hover(function () {
					$(this).find('td').css({background : '#FBD850'});
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
					//asignar a los campos correspondientes el sku y y descripcion
					$('#forma-provfacturas-window').find('input[name=id_proveedor]').val($(this).find('#id_prov').val());
					$('#forma-provfacturas-window').find('input[name=tipo_proveedor]').val($(this).find('#tipo_prov').val());
					$('#forma-provfacturas-window').find('input[name=rfcproveedor]').val($(this).find('span.rfc').html());
					$('#forma-provfacturas-window').find('input[name=razon_proveedor]').val($(this).find('#razon_social').html());
					//$('#forma-provfacturas-window').find('input[name=dir_proveedor]').val($(this).find('span.direccion').html());
					$('#forma-provfacturas-window').find('select[name=tipo_factura]').find('option[value="'+$(this).find('#dias_cred_id').val()+'"]').attr('selected','selected');
					
                                        
                                        $id_proveedor.val($(this).find('#id_prov').val());
                                        $campo_proveedor.val($(this).find('#razon_social').html());
                                        
                                        
                                        //elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
				});
			});
		});
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
		});
	}//termina buscador de proveedores

        
        //buscar proveedor
        $busca_proveedor.click(function(event){
                event.preventDefault();
                $busca_proveedores();
        });
        
        $cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-proveedoresdirecciones-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-proveedoresdirecciones-overlay').fadeOut(remove);
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
			jConfirm('Realmente desea eliminar la direci&oacute;n seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La direccion  fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La direccion  no pudo ser eliminada", 'Atencion!');
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
			
			$(this).modalPanel_proveedoresdirecciones();
			$('#forma-proveedoresdirecciones-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-proveedoresdirecciones-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
                        
			var $id_proveedor = $('#forma-proveedoresdirecciones-window').find('input[name=id_proveedor]');
                        var $campo_id = $('#forma-proveedoresdirecciones-window').find('input[name=identificador]');
                        var $campo_proveedor = $('#forma-proveedoresdirecciones-window').find('input[name=proveedor]');
                        //var $campo_id_proveedor = $('#forma-proveedoresdirecciones-window').find('input[name=id_proveedor]');
                        var $campo_calle = $('#forma-proveedoresdirecciones-window').find('input[name=calle]');
                        var $campo_entreCalles = $('#forma-proveedoresdirecciones-window').find('input[name=entreCalles]');
                        var $campo_numeroInterior = $('#forma-proveedoresdirecciones-window').find('input[name=numInterior]');
                        var $campo_numeroExterior = $('#forma-proveedoresdirecciones-window').find('input[name=numExterior]');
                        var $campo_colonia = $('#forma-proveedoresdirecciones-window').find('input[name=colonia]');
                        var $campo_select_pais = $('#forma-proveedoresdirecciones-window').find('select[name=select_pais]');
                        var $campo_select_estado = $('#forma-proveedoresdirecciones-window').find('select[name=select_estado]');
                        var $campo_select_municipio = $('#forma-proveedoresdirecciones-window').find('select[name=select_municipio]');
                        var $campo_codigoPostal = $('#forma-proveedoresdirecciones-window').find('input[name=codigoPostal]');
                        var $campo_telUno = $('#forma-proveedoresdirecciones-window').find('input[name=telUno]');
                        var $campo_extUno = $('#forma-proveedoresdirecciones-window').find('input[name=extUno]');
                        var $campo_telDos = $('#forma-proveedoresdirecciones-window').find('input[name=telDos]');
                        var $campo_extDos = $('#forma-proveedoresdirecciones-window').find('input[name=extDos]');   
                        
			var $cerrar_plugin = $('#forma-proveedoresdirecciones-window').find('#close');
			var $cancelar_plugin = $('#forma-proveedoresdirecciones-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-proveedoresdirecciones-window').find('#submit');
		
			if(accion_mode == 'edit'){
                                
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getProveedoresDirecciones.json';
				$arreglo = {'id':id_to_show};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-proveedoresdirecciones-overlay').fadeOut(remove);
						jAlert("La direccion del proveedor se Actiualizo correctamente.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-proveedoresdirecciones-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
                                                for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 2 ){
								$('#forma-proveedoresdirecciones-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
                                var hmtl_paises = ''; 
				
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$campo_proveedor.attr({'value' : entry['Direcciones']['0']['razon_social']});
                                        $id_proveedor.attr({'value' : entry['Direcciones']['0']['proveedor_id']});
                                        $campo_id.attr({'value' : entry['Direcciones']['0']['id']});
                                        $campo_calle.attr({'value' : entry['Direcciones']['0']['calle']});
                                        $campo_entreCalles.attr({'value' : entry['Direcciones']['0']['entre_calles']});
                                        $campo_numeroInterior.attr({'value' : entry['Direcciones']['0']['numero_interior']});
                                        $campo_numeroExterior.attr({'value' : entry['Direcciones']['0']['numero_exterior']});
                                        $campo_colonia.attr({'value' : entry['Direcciones']['0']['colonia']});
                                        $campo_codigoPostal.attr({'value' : entry['Direcciones']['0']['cp']});
                                        $campo_telUno.attr({'value' : entry['Direcciones']['0']['telefono1']});
                                        $campo_extUno.attr({'value' : entry['Direcciones']['0']['extension1']});
                                        $campo_telDos.attr({'value' : entry['Direcciones']['0']['telefono2']});
                                        $campo_extDos.attr({'value' : entry['Direcciones']['0']['extension2']});                                      
                        
                                        //carga select estados al cambiar el pais
                                        $campo_select_pais.change(function(){
                                                var valor_pais = $(this).val();
                                                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEntidades.json';
                                                $arreglo = {'id_pais':valor_pais};
                                                $.post(input_json,$arreglo,function(entry){
                                                        $campo_select_estado.children().remove();
                                                        var entidad_hmtl = '<option value="0"  selected="yes">[-Seleccionar entidad-]</option>'
                                                        $.each(entry['Entidades'],function(entryIndex,entidad){
                                                            entidad_hmtl += '<option value="' + entidad['cve_ent'] + '"  >' + entidad['nom_ent'] + '</option>';
                                                        });
                                                        
                                                        $campo_select_estado.append(entidad_hmtl);
                                                        
                                                        var trama_hmtl_localidades = '<option value="' + '000' + '" >' + 'Localidad alternativa' + '</option>';$campo_select_municipio.children().remove();
                                                        $campo_select_municipio.append(trama_hmtl_localidades);
                                                },"json");//termina llamada json
                                        });                                        
                                        
                                        $campo_select_pais.children().remove();
					var pais_hmtl = "";
					$.each(entry['paises'],function(entryIndex,pais){
						if(pais['cve_pais'] == entry['Direcciones']['0']['pais_id']){
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  selected="yes">' + pais['pais_ent'] + '</option>';
						}else{
							pais_hmtl += '<option value="' + pais['cve_pais'] + '"  >' + pais['pais_ent'] + '</option>';
						}
					});
                                        
                                          $campo_select_pais.append(pais_hmtl);
                                          
                                        //carga select municipios al cambiar el estado
                                        $campo_select_estado.change(function(){
                                                var valor_entidad = $(this).val();
                                                var valor_pais = $campo_select_pais.val();
                                                //alert("Pais: "+valor_pais+"    Entidad:"+valor_entidad);
                                                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getLocalidades.json';
                                                $arreglo = {'id_pais':valor_pais, 'id_entidad': valor_entidad};
                                                $.post(input_json,$arreglo,function(entry){
                                                        $campo_select_municipio.children().remove();
                                                        var trama_hmtl_localidades = '<option value="0"  selected="yes">[-Seleccionar municipio-]</option>'
                                                        $.each(entry['Localidades'],function(entryIndex,mun){
                                                                trama_hmtl_localidades += '<option value="' + mun['cve_mun'] + '"  >' + mun['nom_mun'] + '</option>';
                                                        });
                                                        $campo_select_municipio.append(trama_hmtl_localidades);
                                                },"json");//termina llamada json
                                        })
                                        
                                         //cargar estado
                                      $campo_select_estado.children().remove();
					var estado_hmtl = "";
					$.each(entry['estados'],function(entryIndex,estado){
						if(estado['cve_ent'] == entry['Direcciones']['0']['estado_id']){
							estado_hmtl += '<option value="' + estado['cve_ent'] + '"  selected="yes">' + estado['nom_ent'] + '</option>';
						}else{
							estado_hmtl += '<option value="' + estado['cve_ent'] + '"  >' + estado['nom_ent'] + '</option>';
						}
					});
                                        $campo_select_estado.append(estado_hmtl);                                        
                                        
                                         //cargar municipio
                                      $campo_select_municipio.children().remove();
					var municipio_hmtl = "";
					$.each(entry['municipios'],function(entryIndex,municipio){
						if(municipio['cve_mun'] == entry['Direcciones']['0']['municipio_id']){
							municipio_hmtl += '<option value="' + municipio['cve_mun'] + '"  selected="yes">' + municipio['nom_mun'] + '</option>';
						}else{
							municipio_hmtl += '<option value="' + municipio['cve_mun'] + '"  >' + municipio['nom_mun'] + '</option>';
						}
					});
                                        $campo_select_municipio.append(municipio_hmtl);
					
				},"json");//termina llamada json
                                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-proveedoresdirecciones-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-proveedoresdirecciones-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllgetProveedoresDirecciones.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllgetProveedoresDirecciones.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaDirecciones_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    $get_datos_grid();
    
});
