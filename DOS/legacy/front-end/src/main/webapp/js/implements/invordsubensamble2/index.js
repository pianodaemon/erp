$(function() {
    String.prototype.toCharCode = function(){
        var str = this.split(''), len = str.length, work = new Array(len);
        for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
        }
        return work.join(',');
    };
    
    var arrayestatus = {0:"Sin estatus",1:"Enterado", 2:"En Proceso", 3:"Listo", 4:"Cancelado"};
    
    $('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
    $('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
    $username.text($('#lienzo_recalculable').find('input[name=user]').val());
    
    var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
    var controller = $contextpath.val()+"/controllers/invordsubensamble2";
    
    var criteriooferta = {1:"Indistinto", 2:"Orden de Subensamble"};
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
	var $new_item = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$new_item.hide();
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Producci&oacute;n de Subemsamble');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	//var $busqueda_select_grupo = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_grupo]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_producto.val() + "|";
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
		$busqueda_folio.val('');
		$busqueda_codigo.val('');
		$busqueda_producto.val('');
		$busqueda_folio.focus();
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
		$busqueda_folio.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_producto, $buscar);
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-invordsubensamble-window').find('#submit').mouseover(function(){
			$('#forma-invordsubensamble-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-invordsubensamble-window').find('#submit').mouseout(function(){
			$('#forma-invordsubensamble-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-invprodlineas-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-invordsubensamble-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invordsubensamble-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invordsubensamble-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invordsubensamble-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-invordsubensamble-window').find('#close').mouseover(function(){
			$('#forma-invordsubensamble-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-invordsubensamble-window').find('#close').mouseout(function(){
			$('#forma-invordsubensamble-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-invordsubensamble-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invordsubensamble-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invordsubensamble-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invordsubensamble-window').find("ul.pestanas li").click(function() {
			$('#forma-invordsubensamble-window').find(".contenidoPes").hide();
			$('#forma-invordsubensamble-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invordsubensamble-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
                
                
	}
	
        
	
	var carga_formaInvOrdSuben00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar 
		if(accion_mode == 'cancel'){
                     
                    var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
                    $arreglo = {'id':id_to_show,
                                'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                                };
                    jConfirm('Realmente desea eliminar la orden', 'Dialogo de confirmacion', function(r) {
                        if (r){
                            $.post(input_json,$arreglo,function(entry){
                                if ( entry['success'] == '1' ){
                                    jAlert("La orden fue eliminada exitosamente", 'Atencion!');
                                    $get_datos_grid();
                                }
                                else{
                                    jAlert("La orden no puden ser eliminada", 'Atencion!');
                                }
                            },"json");
                        }
                    });
                    
		}else{
                        
			//aqui  entra para editar un registro
			var form_to_show = 'formaInvOrdSubensamble00';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_modalboxInvOrdSubensamble();
			
			$('#forma-invordsubensamble-window').css({"margin-left": -430, 	"margin-top": -295});
			$forma_selected.prependTo('#forma-invordsubensamble-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			$tabs_li_funxionalidad();

			var $campo_id = $('#forma-invordsubensamble-window').find('input[name=identificador]');
			var $accion_proceso = $('#forma-invordsubensamble-window').find('input[name=accion_proceso]');
			
			var $folio = $('#forma-invordsubensamble-window').find('input[name=folio]');
			var $observaciones = $('#forma-invordsubensamble-window').find('textarea[name=observaciones]');
			var $estatus = $('#forma-invordsubensamble-window').find('select[name=estatus]');
			var $estatus_actual = $('#forma-invordsubensamble-window').find('input[name=estatus_actual]');
			
			var $pdf_orden = $('#forma-invordsubensamble-window').find('#pdf_orden');
                        
			var $cerrar_plugin = $('#forma-invordsubensamble-window').find('#close');
			var $cancelar_plugin = $('#forma-invordsubensamble-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-invordsubensamble-window').find('#submit');
			
			var $grid_productos = $('#forma-invordsubensamble-window').find('#grid_productos');
			var $grid_componentes = $('#forma-invordsubensamble-window').find('#grid_productos_complementos');
			
			
			if(accion_mode == 'edit'){
				
				$accion_proceso.attr({'value' : "edit"});
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInvOrdSub.json';
				$arreglo = {'id':id_to_show};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-invordsubensamble-overlay').fadeOut(remove);
						jAlert("La orden se ha actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-invordsubensamble-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_componentes.find('input[name=prescomp]').css({'background' : '#ffffff'});
						$grid_componentes.find('input[name=cantidadcomp]').css({'background' : '#ffffff'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-invordsubensamble-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								if(tmp.split(':')[0].split('_')[0]=='cantidadcomp' || tmp.split(':')[0].split('_')[0]=='prescomp'){
									$('#forma-invordsubensamble-window').find('.invordsubensamble_div_one').css({'height':'570px'});
									$('#forma-invordsubensamble-window').find('#div_warning_grid').css({'display':'block'});
									
									if(parseInt($("tr", $grid_componentes).size())>0){
										
										var $campo = $grid_componentes.find('#'+tmp.split(':')[0]);
										
										$campo.css({'background' : '#d41000'});
										
										$tr = $campo.parent().parent();
										
										var tr_warning = '<tr>';
											tr_warning += '<td width="20"><div><img src="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
											tr_warning += '<td width="120"><input type="text" value="' + $tr.find('#skucomp').val() + '" class="borde_oculto" readOnly="true" style="width:95px; color:red"></td>';
											tr_warning += '<td width="200"><input type="text" value="' +$tr.find('#titulocomp').val() + '" class="borde_oculto" readOnly="true" style="width:205px; color:red"></td>';
											tr_warning += '<td width="445"><input type="text" value="'+  tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:440px; color:red"></td>';
										tr_warning += '</tr>';
										$('#forma-invordsubensamble-window').find('#div_warning_grid').find('#grid_warning').append(tr_warning);
									}
								}
								
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
                var estatus_actual=0;
				//aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					//si el estatus es cero, se actualiza a 1= Enterado
					if(parseInt(entry['InvOrdSub']['0']['estatus'])==0){
						var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getActualizarEstatus.json';
						$arreglo1 = {	'id_subensamble':id_to_show,
										'estatus': 1,// estatus enterado
										'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
									};
						
						$.post(input_json2,$arreglo1,function(entry1){
							if(entry1['success']=='1'){
								estatus_actual = 1;
							}
						},"json");//termina llamada json
					}else{
						estatus_actual = entry['InvOrdSub']['0']['estatus'];
					}
					
					if(parseInt(entry['InvOrdSub']['0']['estatus'])==0){
						estatus_actual = 1;
					}
					
					//alert(entry['InvOrdSub']['0']['id']);
					$campo_id.attr({'value' : entry['InvOrdSub']['0']['id']});
					$folio.attr({'value' : entry['InvOrdSub']['0']['folio']});
					$observaciones.val(entry['InvOrdSub']['0']['comentarios']);
					$estatus_actual.val(entry['InvOrdSub']['0']['estatus']);
					
					$estatus.children().remove();
					var estatus_hmtl = '';
					for(var i in arrayestatus){
						if(parseInt(estatus_actual) == parseInt(i)){
							estatus_hmtl += '<option value="' + i + '"  selected="yes">' + arrayestatus[i] + '</option>';
						}else{
							if(parseInt(i) > parseInt(estatus_actual)){
								estatus_hmtl += '<option value="' + i + '"  >' + arrayestatus[i] + '</option>';
							}
						}
					}
					$estatus.append(estatus_hmtl);
					
					
					for(var i in entry['Detalle']){
						var trCount = $("tr", $grid_productos).size();
						trCount++;
						var tr_prod='';
						
						var cantidad_litro=0;
						
						if(parseFloat(entry['Detalle'][i]['densidad'])>0){
							cantidad_litro = entry['Detalle'][i]['cantidad'] / entry['Detalle'][i]['densidad'];
						}
						
						tr_prod += '<tr>';
							tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="99">';
								tr_prod += '<input type="hidden" name="id_prod_grid" id="id_prod_grid" value="'+entry['Detalle'][i]['id'] +'">';
								tr_prod += '<input type="text" id="skup" name="sku'+ trCount +'" value="'+ entry['Detalle'][i]['sku'] +'" class="borde_oculto" style="width:97px;" readOnly="true">';
							tr_prod += '</td>';
							tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="254">';
								tr_prod += '<input type="text"  name="titulo'+ trCount +'" value="'+ entry['Detalle'][i]['descripcion'] +'" class="borde_oculto" style="width:251px;" readOnly="true">';
							tr_prod += '</td>';
							tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="100">';
								tr_prod += '<input type="text" name="unidad" class="borde_oculto" value="'+ entry['Detalle'][i]['unidad'] +'" readOnly="true" style="width:98px;">';
							tr_prod += '</td>';
							tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="110">';
								tr_prod += '<input type="text" name="unidad" class="borde_oculto" value="'+ entry['Detalle'][i]['presentacion'] +'" readOnly="true" style="width:108px;">';
							tr_prod += '</td>';
							tr_prod += '<td class="grid1" id="td_densidad'+ trCount +'" style="font-size:11px; border:1px solid #C1DAD7;" width="50">'+ parseFloat(entry['Detalle'][i]['densidad']).toFixed(4) +'</td>';
							tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="75">';
								tr_prod += '<input type="text" name="cantidad" id="cantidad'+trCount+'" value="'+entry['Detalle'][i]['cantidad']+'" readOnly="true" style="width:73px; text-align:right;">';
							tr_prod += '</td>';
							
							tr_prod += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="75">';
								tr_prod += '<input type="text" name="cant_litro" id="cant_litro'+trCount+'" value="'+ parseFloat(cantidad_litro).toFixed(entry['Detalle'][i]['no_dec']) +'" readOnly="true" style="width:71px; text-align:right;">';
							tr_prod += '</td>';
							
						tr_prod += '</tr>';
						
						$grid_productos.append(tr_prod);
					}
					
					for(var i in entry['componentes']){
						var tr_complemento='';
						var idProdComp = entry['componentes'][i]['id'];
						
						tr_complemento += '<tr>';
							tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="99">';
								tr_complemento += '<input type="hidden" name="eliminadocomp" id="eliminadocomp" value="1">';
								tr_complemento += '<input type="text" id="skucomp" name="skucomp'+ trCount +'" value="'+ entry['componentes'][i]['sku'] +'" class="borde_oculto" style="width:97px;" readOnly="true">';
							tr_complemento += '</td>';
							tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="215">';
								tr_complemento += '<input type="text" id="titulocomp" name="titulocomp'+ trCount +'" value="'+ entry['componentes'][i]['descripcion'] +'" class="borde_oculto" style="width:213px;" readOnly="true">';
							tr_complemento += '</td>';
							tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								tr_complemento += '<input type="text" name="unidadcomp" class="borde_oculto" value="'+ entry['componentes'][i]['utitulo'] +'" readOnly="true" style="width:88px;">';
							tr_complemento += '</td>';
							tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								tr_complemento += '<input type="text" name="prescomp" id="prescomp_'+idProdComp+'"class="borde_oculto" value="'+ entry['componentes'][i]['pres_comp'] +'" readOnly="true" style="width:88px;">';
							tr_complemento += '</td>';
							tr_complemento += '<td class="grid1" id="td_densidad" style="font-size:11px; border:1px solid #C1DAD7;" width="50">'+ parseFloat(entry['componentes'][i]['densidad']).toFixed(4) +'</td>';
							tr_complemento += '<td class="grid1" id="td_densidad" style="font-size:11px; border:1px solid #C1DAD7;" width="65">'+ parseFloat(entry['componentes'][i]['densidad_promedio']).toFixed(4) +'</td>';
							tr_complemento += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="75">';
								tr_complemento += '<input type="text" name="cantidadcomp" id="cantidadcomp_'+idProdComp+'" value="'+ parseFloat(entry['componentes'][i]['cantidad']).toFixed(4) +'" readOnly="true" style="width:73px; text-align:right;">';
							tr_complemento += '</td>';
							
							tr_complemento += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="75">';
								tr_complemento += '<input type="text" name="cant_comp_litro" id="cant_comp_litro" value="'+ parseFloat(entry['componentes'][i]['cant_mp_lt']).toFixed(4) +'" readOnly="true" style="width:71px; text-align:right;">';
							tr_complemento += '</td>';
							
						tr_complemento += '</tr>';

						$grid_componentes.append(tr_complemento);
					}
				},"json");//termina llamada json
				
                                
				//descargar pdf de factura
				$pdf_orden.click(function(event){
					event.preventDefault();
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_ordensubensamble/'+$campo_id.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
				
				
				
				$submit_actualizar.bind('click',function(){
					var estado = $estatus.val();
					if(parseInt(estado)==4){
						jAlert("No puedes registrar el estatus Cancelado", 'Atencion!');
						return false;
					}else{
						if(parseInt($estatus_actual.val())==3){
							jAlert("La Produccion ya se encuentra en Estado Listo.", 'Atencion!');
							return false;
						}else{
							return true;
						}
					}
				});
				
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invordsubensamble-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invordsubensamble-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
				
				$observaciones.focus();
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllInvOrdSubensamble.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllInvOrdSubensamble.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaInvOrdSuben00_for_datagrid00);
                
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
});
