$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	var DataObject;
	var array_clasificacion2 = {1:"Rentada", 2:"Propio"};
	
	//Carga los campos select con los datos que recibe como parametro
	$carga_campos_select = function($campo_select, $arreglo_elementos, elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, fijo){
		var select_html = '';
		
		if(texto_elemento_cero.trim() != ''){
			select_html = '<option value="0">'+texto_elemento_cero+'</option>';
		}
		/*
		if(parseInt(elemento_seleccionado)<=0 && texto_elemento_cero.trm()==''){
			select_html = '<option value="0">[--- ---]</option>';
		}
		*/
		$.each($arreglo_elementos,function(entryIndex,elemento){
			if( parseInt(elemento[index_elem]) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + elemento[index_elem] + '" selected="yes">' + elemento[index_text_elem] + '</option>';
			}else{
				if(!fijo){
					select_html += '<option value="' + elemento[index_elem] + '" >' + elemento[index_text_elem] + '</option>';
				}
			}
		});
		
		$campo_select.children().remove();
		$campo_select.append(select_html);
	}
	
	
	//Carga los campos select con los datos que recibe como parametro
	$carga_select_con_arreglo_fijo = function($campo_select, arreglo_elementos, elemento_seleccionado, mostrar_opciones){
		$campo_select.children().remove();
		var select_html = '';
		for(var i in arreglo_elementos){
			if( parseInt(i) == parseInt(elemento_seleccionado) ){
				select_html += '<option value="' + i + '" selected="yes">' + arreglo_elementos[i] + '</option>';
			}else{
				if (mostrar_opciones=='true'){
					select_html += '<option value="' + i + '"  >' + arreglo_elementos[i] + '</option>';
				}
			}
		}
		$campo_select.append(select_html);
	}
	
	
	//Aplicar solo lectura
	$aplica_read_only_input_text = function($campo){
		$campo.attr("readonly", true);
		$campo.css({'background' : '#f0f0f0'});
	}
	
	
	$quitar_readonly_input = function($input){
		$input.css({'background' : '#ffffff'});
		$input.attr('readonly',false);
	}
	
	
	//Funcion para hacer que un campo solo acepte numeros
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
	
	
	//Valida que la cantidad ingresada no tenga mas de un punto decimal
	$validar_numero_puntos = function($campo, campo_nombre){
		//Buscar cuantos puntos tiene  Precio Unitario
		var coincidencias = $campo.val().match(/\./g);
		var numPuntos = coincidencias ? coincidencias.length : 0;
		if(parseInt(numPuntos)>1){
			jAlert('El valor ingresado para el campo '+campo_nombre+' es incorrecto, tiene mas de un punto('+$campo.val()+').', 'Atencion!', function(r) { 
				$campo.focus();
			});
		}
	}
	
	
	$aplica_evento_focus_input_numerico = function($campo){
		//Al iniciar el campo tiene un caracter en blanco o tiene comas, al obtener el foco se elimina el  espacio por espacio en blanco
		$campo.focus(function(e){
			var valor=quitar_comas($(this).val().trim());
			
			if(valor.trim() != ''){
				if(parseFloat(valor)<=0){
					$(this).val('');
				}else{
					$(this).val(valor);
				}
			}
		});
	}
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/logvehiculos";
    
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Unidades');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	
	var $cadena_busqueda = "";
	var $busqueda_marca = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_marca]');
	var $busqueda_num_economico = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_numeconomico]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limbuscarpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "marca" + signo_separador + $busqueda_marca.val() + "|";
		valor_retorno += "no_economico" + signo_separador + $busqueda_num_economico.val() + "|";
		valor_retorno += "descripcion" + signo_separador + $busqueda_marca.val() + $busqueda_num_economico.val()+ "|";
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
                $busqueda_marca.val(' ');
                $busca_numeconomico.val(' ');
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
	
	
	
	
	
	$iniciar_campos_generales = function(){
		var input_json_cuentas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getInicializar.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_cuentas,$arreglo,function(data){
			/*
			$busqueda_select_sucursal.children().remove();
			var suc_hmtl = '';
			if(data['Data']['versuc']==true){
				//Aqui carga todas las sucursales porque el usuario es un administrador
				suc_hmtl = '<option value="0" selected="yes">[--- Todos ---]</option>';
				$.each(data['Data']['Suc'],function(entryIndex,suc){
					suc_hmtl += '<option value="' + suc['id'] + '">'+ suc['titulo'] + '</option>';
				});
			}else{
				//Aqui solo debe cargar la sucursal del usuario logueado
				$.each(data['Data']['Suc'],function(entryIndex,suc){
					if(parseInt(suc['id'])==parseInt(data['Data']['suc_id'])){
						suc_hmtl += '<option value="' + suc['id'] + '" selected="yes">'+ suc['titulo'] + '</option>';
					}
				});
			}
			$busqueda_select_sucursal.append(suc_hmtl);
			*/
			
			/*
			//Carga select de Sucursales
			var elemento_seleccionado = 0;
			var texto_elemento_cero = '[-- --]';
			var index_elem = 'id';
			var index_text_elem = 'titulo';
			var option_fijo = false;
			$carga_campos_select($busqueda_select_transportista, data['Data']['Trans'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
			*/
			
			
			
			DataObject = data['Data'];
			
			$busqueda_marca.focus();
		});
	}
	
	
	//Llamada a la funcion que inicializa datos
	$iniciar_campos_generales();
	
	
	
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-logvehiculos-window').find('#submit').mouseover(function(){
			$('#forma-logvehiculos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-logvehiculos-window').find('#submit').mouseout(function(){
			$('#forma-logvehiculos-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-logvehiculos-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-logvehiculos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-logvehiculos-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-logvehiculos-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-logvehiculos-window').find('#close').mouseover(function(){
			$('#forma-logvehiculos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-logvehiculos-window').find('#close').mouseout(function(){
			$('#forma-logvehiculos-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-logvehiculos-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-logvehiculos-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-logvehiculos-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-logvehiculos-window').find("ul.pestanas li").click(function() {
			$('#forma-logvehiculos-window').find(".contenidoPes").hide();
			$('#forma-logvehiculos-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-logvehiculos-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	//Agregar datos del proveedor seleccionado
	$agregarDatosProveedorSeleccionado = function($id_prov, $no_prov, $proveedor, id_proveedor, no_proveedor, razon_soc_proveedor, $busca_proveedor){
		var $id_operador = $('#forma-logvehiculos-window').find('input[name=id_operador]');
		var $no_operador = $('#forma-logvehiculos-window').find('input[name=no_operador]');
		var $operador = $('#forma-logvehiculos-window').find('input[name=operador]');
		var $busca_operador = $('#forma-logvehiculos-window').find('#busca_operador');
		
		$id_prov.val(id_proveedor);
		$no_prov.val(no_proveedor);
		$proveedor.val(razon_soc_proveedor);
		
		//Ocultar el buscador de proveedores
		$busca_proveedor.hide();
		
		$aplica_read_only_input_text($proveedor);
		
		if(parseInt($id_prov.val())>0){
			//Si el id del proveedor(transportista) es mayor a cero, entonces buscamos todos los peradores(choferes) que tiene registrados
			$busca_operadores($id_operador, $no_operador, $operador, $id_prov.val(), $busca_operador);
		}
	}
	
	
	
	//Buscador de Proveedores(Transportistas)
	$busca_proveedores = function($id_prov, $no_prov, $proveedor, $busca_proveedor){
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
		
		//Funcionalidad botones
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
		
		$campo_no_proveedor.val($no_prov.val());
		$campo_nombre.val($proveedor.val());
		
		$campo_no_proveedor.focus();
		
		//click buscar proveedor
		$buscar_plugin_proveedor.click(function(event){
			var restful_json_service = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscaProveedores.json'
			$arreglo = {    rfc:$campo_rfc.val(),
							no_proveedor:$campo_no_proveedor.val(),
							nombre:$campo_nombre.val(),
							transportista:'true',
							iu:$('#lienzo_recalculable').find('input[name=iu]').val()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(restful_json_service,$arreglo,function(entry){
				$.each(entry['Proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							//trr += '<input type="hidden" id="no_prov" value="'+proveedor['numero_proveedor']+'">';
							trr += '<input type="hidden" id="id_moneda" value="'+proveedor['moneda_id']+'">';
							trr += '<input type="hidden" id="descuento" value="'+proveedor['descuento']+'">';
							trr += '<input type="hidden" id="limite_de_credito" value="'+proveedor['limite_de_credito']+'">';
							trr += '<input type="hidden" id="id_dias_credito" value="'+proveedor['id_dias_credito']+'">';
							trr += '<input type="hidden" id="id_tipo_embarque" value="'+proveedor['id_tipo_embarque']+'">';
							trr += '<input type="hidden" id="comienzo_de_credito" value="'+proveedor['comienzo_de_credito']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							trr += '<input type="hidden" id="impto_id" value="'+proveedor['impuesto_id']+'">';
							trr += '<input type="hidden" id="valor_impto" value="'+proveedor['valor_impuesto']+'">';
							//trr += '<span class="rfc">'+proveedor['rfc']+'</span>';
							trr += '<span class="no_prov">'+proveedor['numero_proveedor']+'</span>';
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
					//var rfc_proveedor = $(this).find('.rfc').html();
					var razon_soc_proveedor = $(this).find('#razon_social').html();
					var dir_proveedor = $(this).find('.direccion').html();
					var id_proveedor = $(this).find('#id_prov').val();
					var tipo_proveedor = $(this).find('#tipo_prov').val();
					var no_proveedor = $(this).find('span.no_prov').html();
					var id_moneda=$(this).find('#id_moneda').val();
					var id_dias_credito=$(this).find('#id_dias_credito').val();
					var id_tipo_embarque=$(this).find('#id_tipo_embarque').val();
					
					var idImptoProv=$(this).find('#impto_id').val();
					var valorImptoProv=$(this).find('#valor_impto').val();
					
					//Llamada a la función que agrega datos del proveedor seleccionado
					$agregarDatosProveedorSeleccionado($id_prov, $no_prov, $proveedor, id_proveedor, no_proveedor, razon_soc_proveedor, $busca_proveedor);
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
					$('#forma-comordencompra-window').find('input[name=razonproveedor]').focus();
				});
			});
		});
		
		
		if ($campo_no_proveedor.val().trim()!='' || $campo_nombre.val().trim()!=''){
			$buscar_plugin_proveedor.trigger('click');
		}
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_rfc, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_no_proveedor, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_nombre, $buscar_plugin_proveedor);
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
			$('#forma-logvehiculos-window').find('input[name=no_prov]').focus();
		});
	}
	//Termina buscador de proveedores
	
	
	
	//Agregar datos del Operador seleccionado
	$agregarDatosOperadorSeleccionado = function($id_operador, $no_operador, $operador, id_operador, no_operador, operador, $busca_operador){
		$id_operador.val(id_operador);
		$no_operador.val(no_operador);
		$operador.val(operador);
		
		$busca_operador.hide();
		
		//Aplicar solo lectura
		$aplica_read_only_input_text($operador);
	}
	
	
	
	//Buscador de Operadores(Choferes)
	$busca_operadores= function($id_operador, $no_operador, $nombre_operador, id_proveedor, $busca_operador){
		$(this).modalPanel_busquedaoperador();
		var $dialogoc =  $('#forma-busquedaoperador-window');
		$dialogoc.append($('div.buscador_busquedaoperador').find('table.formaBusqueda_busquedaoperador').clone());
		$('#forma-busquedaoperador-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-busquedaoperador-window').find('#tabla_resultado');
		
		var $boton_busquedaoperador = $('#forma-busquedaoperador-window').find('#boton_busquedaoperador');
		var $cancelar_busqueda = $('#forma-busquedaoperador-window').find('#cencela');
		
		var $cadena_nooperador = $('#forma-busquedaoperador-window').find('input[name=cadena_nooperador]');
		var $cadena_nombre = $('#forma-busquedaoperador-window').find('input[name=cadena_nombre]');
		
		//funcionalidad botones
		$boton_busquedaoperador.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_busquedaoperador.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		
		$cancelar_busqueda.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		
		$cancelar_busqueda.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
		
		$cadena_nooperador.val($cadena_nooperador.val());
		$cadena_nombre.val($nombre_operador.val());
		
		//click buscar clientes
		$boton_busquedaoperador.click(function(event){
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorOperadores.json';
			$arreglo = { 'no_operador':$cadena_nooperador.val(),
						 'nombre':$cadena_nombre.val(),
						 'id_prov':id_proveedor,
						 'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						}
						
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
				if(parseInt(entry['Operadores'].length) > 0 ){
					$.each(entry['Operadores'],function(entryIndex,operador){
						trr = '<tr>';
							trr += '<td width="180">';
								trr += '<input type="hidden" id="id" value="'+operador['id']+'">';
								trr += '<span class="no_ope">'+operador['clave']+'</span>';
							trr += '</td>';
							trr += '<td width="420"><span class="nombre">'+operador['nombre']+'</span></td>';
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
					
					//Seleccionar un elemento del resultado
					$tabla_resultados.find('tr').click(function(){
						
						var id_operador = $(this).find('input#id').val();
						var no_operador = $(this).find('span.no_ope').html();
						var operador = $(this).find('span.nombre').html();
						
						//Llamada a la función que agrega datos del proveedor seleccionado
						$agregarDatosOperadorSeleccionado($id_operador, $no_operador, $nombre_operador, id_operador, no_operador, operador, $busca_operador);
						
						//Elimina la ventana de busqueda
						var remove = function() {$(this).remove();};
						$('#forma-busquedaoperador-overlay').fadeOut(remove);
						
						$no_operador.focus();
					});

				}else{
					if(parseInt(id_proveedor)>0){
						var remove = function() {$(this).remove();};
						$('#forma-busquedaoperador-overlay').fadeOut(remove);
					}
				}
			});
				
			
		});//termina llamada json
		
		
		//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
		if($cadena_nooperador.val().trim()!='' || $cadena_nombre.val().trim()!=''){
			$boton_busquedaoperador.trigger('click');
		}else{
			if(parseInt(id_proveedor)>0){
				$boton_busquedaoperador.trigger('click');
			}
		}
		
		
		
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_nooperador, $boton_busquedaoperador);
		$(this).aplicarEventoKeypressEjecutaTrigger($cadena_nombre, $boton_busquedaoperador);
		
		$cancelar_busqueda.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-busquedaoperador-overlay').fadeOut(remove);
		});
		
		$cadena_nooperador.focus();
	}//Termina buscador de Operadores(Choferes)
	
	
	
	
	
	
	
	
	//Nuevo
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_LogVehiculos();
		
		var form_to_show = 'formaLogVehiculos';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-logvehiculos-window').css({"margin-left": -400, 	"margin-top": -240});
		$forma_selected.prependTo('#forma-logvehiculos-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
                
		//Campos de la vista
		var $identificador = $('#forma-logvehiculos-window').find('input[name=identificador]');
		var $folio = $('#forma-logvehiculos-window').find('input[name=folio]');
		var $select_tipo_unidad = $('#forma-logvehiculos-window').find('select[name=select_tipo_unidad]');
		var $select_clase = $('#forma-logvehiculos-window').find('select[name=select_clase]');
		var $select_marca = $('#forma-logvehiculos-window').find('select[name=select_marca]');
		var $select_anio = $('#forma-logvehiculos-window').find('select[name=select_anio]');
		var $color = $('#forma-logvehiculos-window').find('input[name=color]');
		var $no_economico = $('#forma-logvehiculos-window').find('input[name=no_economico]');
		var $select_tipo_placa = $('#forma-logvehiculos-window').find('select[name=select_tipo_placa]');
		var $placas = $('#forma-logvehiculos-window').find('input[name=placas]');
		var $no_serie = $('#forma-logvehiculos-window').find('input[name=no_serie]');
		var $select_tipo_rodada = $('#forma-logvehiculos-window').find('select[name=select_tipo_rodada]');
		var $select_tipo_caja = $('#forma-logvehiculos-window').find('select[name=select_tipo_caja]');
		var $cap_volumen = $('#forma-logvehiculos-window').find('input[name=cap_volumen]');
		var $cap_peso = $('#forma-logvehiculos-window').find('input[name=cap_peso]');
		var $select_clasif2 = $('#forma-logvehiculos-window').find('select[name=select_clasif2]');
		
		var $id_prov = $('#forma-logvehiculos-window').find('input[name=id_prov]');
		var $no_prov = $('#forma-logvehiculos-window').find('input[name=no_prov]');
		var $proveedor = $('#forma-logvehiculos-window').find('input[name=proveedor]');
		var $id_operador = $('#forma-logvehiculos-window').find('input[name=id_operador]');
		var $no_operador = $('#forma-logvehiculos-window').find('input[name=no_operador]');
		var $operador = $('#forma-logvehiculos-window').find('input[name=operador]');
		
		var $comentarios = $('#forma-logvehiculos-window').find('textarea[name=comentarios]');
		
		var $busca_proveedor = $('#forma-logvehiculos-window').find('#busca_proveedor');
		var $busca_operador = $('#forma-logvehiculos-window').find('#busca_operador');
		
		var $cerrar_plugin = $('#forma-logvehiculos-window').find('#close');
		var $cancelar_plugin = $('#forma-logvehiculos-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-logvehiculos-window').find('#submit');
		
		$identificador.attr({'value' : 0});
		$id_prov.val(0);
		$cap_volumen.val('0.000');
		$cap_peso.val('0.000');
		$aplica_read_only_input_text($folio);
		
		//Permitir solo numeros y punto
		$permitir_solo_numeros($cap_volumen);
		$permitir_solo_numeros($cap_peso);
		
		//Aplicar envento focus
		$aplica_evento_focus_input_numerico($cap_volumen);
		$aplica_evento_focus_input_numerico($cap_peso);
		

		$cap_volumen.blur(function(){
			$validar_numero_puntos($(this), "Capacidad&nbsp;m&#179;");
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(3));
		});
		
		$cap_peso.blur(function(){
			$validar_numero_puntos($(this), "Capacidad Ton.");
			if($(this).val().trim()==''){
				$(this).val(0);
			}
			$(this).val(parseFloat($(this).val()).toFixed(3));
		});
		


		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La Unidad fue dado de alta con exito.", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-logvehiculos-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-logvehiculos-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-logvehiculos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
                
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getVehiculo.json';
		var parametros={
			id:$identificador.val(),
			iu: $('#lienzo_recalculable').find('input[name=iu]').val()
		}
		
		/*
		$.post(input_json,parametros,function(entry){
				
		});//termina llamada json
		*/
		
		
		//Carga select de Tipos de Unidades
		var elemento_seleccionado = 0;
		var texto_elemento_cero = '[-- --]';
		if(parseInt(DataObject['TUnidades'].length) > 0 ){
			texto_elemento_cero = '';
		}
		var index_elem = 'id';
		var index_text_elem = 'titulo';
		var option_fijo = false;
		$carga_campos_select($select_tipo_unidad, DataObject['TUnidades'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
		
		//Carga select de Clase de unidad
		elemento_seleccionado = 0;
		texto_elemento_cero = '[-- --]';
		if(parseInt(DataObject['Clases'].length) > 0 ){
			texto_elemento_cero = '';
		}
		index_elem = 'id';
		index_text_elem = 'titulo';
		option_fijo = false;
		$carga_campos_select($select_clase, DataObject['Clases'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
		
		
		//Carga select de Marcas e Camiones(Unidades)
		elemento_seleccionado = 0;
		texto_elemento_cero = '[-- --]';
		if(parseInt(DataObject['Marcas'].length) > 0 ){
			texto_elemento_cero = '';
		}
		index_elem = 'id';
		index_text_elem = 'titulo';
		option_fijo = false;
		$carga_campos_select($select_marca, DataObject['Marcas'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
		
		
		//Carga select de Tipos de Placas
		elemento_seleccionado = 0;
		texto_elemento_cero = '[-- --]';
		if(parseInt(DataObject['TPlacas'].length) > 0 ){
			texto_elemento_cero = '';
		}
		index_elem = 'id';
		index_text_elem = 'titulo';
		option_fijo = false;
		$carga_campos_select($select_tipo_placa, DataObject['TPlacas'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
		
		
		//Carga select de Tipos de Rodadas
		elemento_seleccionado = 0;
		texto_elemento_cero = '[-- --]';
		if(parseInt(DataObject['TRodadas'].length) > 0 ){
			texto_elemento_cero = '';
		}
		index_elem = 'id';
		index_text_elem = 'titulo';
		option_fijo = false;
		$carga_campos_select($select_tipo_rodada, DataObject['TRodadas'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
		
		
		//Carga select de Tipos de Caja
		elemento_seleccionado = 0;
		texto_elemento_cero = '[-- --]';
		if(parseInt(DataObject['TCajas'].length) > 0 ){
			texto_elemento_cero = '';
		}
		index_elem = 'id';
		index_text_elem = 'titulo';
		option_fijo = false;
		$carga_campos_select($select_tipo_caja, DataObject['TCajas'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
		
		
		//Carga select de Años
		$select_anio.children().remove();
		var anio_html = '';
		$.each(DataObject['Anios'],function(entryIndex,anio){
			anio_html += '<option value="' + anio['valor'] + '"  >'+ anio['valor'] + '</option>';
		});
		$select_anio.append(anio_html);
		
		
		//Cargar select con estatus
		var elemento_seleccionado = 0;
		var mostrar_opciones = 'true';
		$carga_select_con_arreglo_fijo($select_clasif2, array_clasificacion2, elemento_seleccionado, mostrar_opciones);
		
		
		
		
		//Buscador de provedores(Transportista)
		$busca_proveedor.click(function(event){
			event.preventDefault();
			$busca_proveedores($id_prov, $no_prov, $proveedor, $busca_proveedor );
		});
		
		
		$no_prov.keypress(function(e){
			var valor=$(this).val();
			
			if(e.which == 13){
				var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoProv.json';
				$arreglo2 = {'no_proveedor':$no_prov.val(),  'transportista':'true', 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				$.post(input_json2,$arreglo2,function(entry2){
					if(parseInt(entry2['Proveedor'].length) > 0 ){
						var id_proveedor = entry2['Proveedor'][0]['id'];
						var no_proveedor = entry2['Proveedor'][0]['numero_proveedor'];
						var razon_soc_proveedor = entry2['Proveedor'][0]['razon_social'];
						
						//Llamada a la función que agrega datos del proveedor seleccionado
						$agregarDatosProveedorSeleccionado($id_prov, $no_prov, $proveedor, id_proveedor, no_proveedor, razon_soc_proveedor, $busca_proveedor);
					}else{
						$id_prov.val(0);
						$no_prov.val('');
						$proveedor.val('');
						
						jAlert('N&uacute;mero de Transportista(Proveedor) desconocido.', 'Atencion!', function(r) { 
							$no_prov.focus(); 
						});
					}
				},"json");//termina llamada json
				
				return false;
			}else{
				if (parseInt(e.which) == 8) {
					//Si se oprime la tecla borrar se vacía el campo no_prov 
					if(parseInt(valor.length)>0 && parseInt($id_prov.val())>0){
						jConfirm('Seguro que desea cambiar el Proveedor seleccionado?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) {
								$id_prov.val(0);
								$no_prov.val('');
								$proveedor.val('');
								
								//Quitar solo lectura una vez que se ha eliminado datos del Proveedor
								$quitar_readonly_input($proveedor);
								
								//Mostrar link busca Proveedores
								$busca_proveedor.show();
								
								//--Datos del OPERADOR------------------
								$id_operador.val(0);
								$no_operador.val('');
								$operador.val('');
								
								//Quitar solo lectura una vez que se ha eliminado datos del Proveedor
								$quitar_readonly_input($operador);
								
								//Mostrar link busca Operadores
								$busca_operador.show();
								//--Datos del OPERADOR------------------
								
								$no_prov.focus();
							}else{
								$no_prov.val(valor);
								$no_prov.focus();
							}
						});
					}else{
						$no_prov.focus();
					}
				}
			}
			
		});
		
		
		
		
		//Buscador de Operadores
		$busca_operador.click(function(event){
			event.preventDefault();
			/*
			Le asignamos cero al id del proveedor para permitir la busqueda de todos los choferes 
			sin importar el contratista con el que esté registrado
			*/
			var id_proveedor=0;
			$busca_operadores($id_operador, $no_operador, $operador, id_proveedor, $busca_operador);
		});
		
		$(this).aplicarEventoKeypressEjecutaTrigger($operador, $busca_operador);
		
		$no_operador.keypress(function(e){
			var valor=$(this).val();
			if(e.which == 13){
				if($no_operador.val().trim()!=''){
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataOperadorByNo.json';
					$arreglo2 = {'no_operador':$no_operador.val(), 'id_prov':0, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					$.post(input_json2,$arreglo2,function(entry2){
						if(parseInt(entry2['Operador'].length) > 0 ){
							var id_operador = entry2['Operador'][0]['id'];
							var no_operador = entry2['Operador'][0]['id'];
							var operador = entry2['Operador'][0]['id'];
							
							/*
							Le asignamos cero al id del proveedor para permitir la busqueda de todos los choferes 
							sin importar el contratista con el que esté registrado
							*/
							var id_proveedor=0;
							
							//Llamada a la función que agrega datos del proveedor seleccionado
							$agregarDatosOperadorSeleccionado($id_operador, $no_operador, $operador, id_operador, no_operador, operador, $busca_operador);
							
						}else{
							jAlert('N&uacute;mero de Operador desconocido.', 'Atencion!', function(r) {
								$id_operador.val(0);
								$no_operador.val('');
								$no_operador.focus(); 
							});
						}
					},"json");//termina llamada json
				}
				return false;
			}else{
				if (parseInt(e.which) == 8) {
					//Si se oprime la tecla borrar se vacía el campo no_prov 
					if(parseInt(valor.length)>0 && parseInt($id_prov.val())>0){
						jConfirm('Seguro que desea cambiar el Operador seleccionado?', 'Dialogo de Confirmacion', function(r) {
							// If they confirmed, manually trigger a form submission
							if (r) {
								$id_operador.val(0);
								$no_operador.val('');
								$operador.val('');
								
								//Quitar solo lectura una vez que se ha eliminado datos del Operador
								$quitar_readonly_input($operador);
								
								//Mostrar link busca Operadores
								$busca_operador.show();
								
								$no_operador.focus();
							}else{
								$no_prov.val(valor);
								$no_prov.focus();
							}
						});
					}else{
						$no_prov.focus();
					}
				}
			}
		});
		
		
		
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-logvehiculos-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-logvehiculos-overlay').fadeOut(remove);
			$buscar.trigger('click');
		});
		
		//Asignar enfoque al primer campo editable
		$select_tipo_unidad.focus();
	});
	
	
	
	
	//Eventos del grid edicion,borrar!
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para eliminar una entrada
		if(accion_mode == 'cancel'){
                     
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			jConfirm('Realmente desea eliminar la Unidad seleccionada.', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La Unidad fue eliminada exitosamente.", 'Atencion!');
							$get_datos_grid();
						}else{
							jAlert("La Unidad no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaLogVehiculos';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_LogVehiculos();
			
			$('#forma-logvehiculos-window').css({"margin-left": -400, 	"margin-top": -200});
			$forma_selected.prependTo('#forma-logvehiculos-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			$tabs_li_funxionalidad();
			
			//Campos de la vista
			var $identificador = $('#forma-logvehiculos-window').find('input[name=identificador]');
			var $folio = $('#forma-logvehiculos-window').find('input[name=folio]');
			var $select_tipo_unidad = $('#forma-logvehiculos-window').find('select[name=select_tipo_unidad]');
			var $select_clase = $('#forma-logvehiculos-window').find('select[name=select_clase]');
			var $select_marca = $('#forma-logvehiculos-window').find('select[name=select_marca]');
			var $select_anio = $('#forma-logvehiculos-window').find('select[name=select_anio]');
			var $color = $('#forma-logvehiculos-window').find('input[name=color]');
			var $no_economico = $('#forma-logvehiculos-window').find('input[name=no_economico]');
			var $select_tipo_placa = $('#forma-logvehiculos-window').find('select[name=select_tipo_placa]');
			var $placas = $('#forma-logvehiculos-window').find('input[name=placas]');
			var $no_serie = $('#forma-logvehiculos-window').find('input[name=no_serie]');
			var $select_tipo_rodada = $('#forma-logvehiculos-window').find('select[name=select_tipo_rodada]');
			var $select_tipo_caja = $('#forma-logvehiculos-window').find('select[name=select_tipo_caja]');
			var $cap_volumen = $('#forma-logvehiculos-window').find('input[name=cap_volumen]');
			var $cap_peso = $('#forma-logvehiculos-window').find('input[name=cap_peso]');
			var $select_clasif2 = $('#forma-logvehiculos-window').find('select[name=select_clasif2]');
			
			var $id_prov = $('#forma-logvehiculos-window').find('input[name=id_prov]');
			var $no_prov = $('#forma-logvehiculos-window').find('input[name=no_prov]');
			var $proveedor = $('#forma-logvehiculos-window').find('input[name=proveedor]');
			var $id_operador = $('#forma-logvehiculos-window').find('input[name=id_operador]');
			var $no_operador = $('#forma-logvehiculos-window').find('input[name=no_operador]');
			var $operador = $('#forma-logvehiculos-window').find('input[name=operador]');
			
			var $comentarios = $('#forma-logvehiculos-window').find('textarea[name=comentarios]');
			
			var $busca_proveedor = $('#forma-logvehiculos-window').find('#busca_proveedor');
			var $busca_operador = $('#forma-logvehiculos-window').find('#busca_operador');
			
			//Botones                        
			var $cerrar_plugin = $('#forma-logvehiculos-window').find('#close');
			var $cancelar_plugin = $('#forma-logvehiculos-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-logvehiculos-window').find('#submit');
			
			//$id_prov.val(0);
			//$cap_volumen.val('0.000');
			//$cap_peso.val('0.000');
			$aplica_read_only_input_text($folio);
			
			if(accion_mode == 'edit'){
				//Aqui es el post que envia los datos a getMarcas.json
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getVehiculo.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-logvehiculos-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-logvehiculos-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-logvehiculos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
							}
						}
					}
				}
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);

				//Aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.attr({'value' : entry['Vehiculo'][0]['id']});
					$folio.attr({'value' : entry['Vehiculo'][0]['folio']});
					$color.attr({'value' : entry['Vehiculo'][0]['color']});
					$no_economico.attr({'value' : entry['Vehiculo'][0]['numero_economico']});
					$placas.attr({'value' : entry['Vehiculo'][0]['placa']});
					$no_serie.attr({'value' : entry['Vehiculo'][0]['numero_serie']});
					$cap_volumen.attr({'value' : entry['Vehiculo'][0]['cap_volumen']});
					$cap_peso.attr({'value' : entry['Vehiculo'][0]['cap_peso']});
					$id_prov.attr({'value' : entry['Vehiculo'][0]['prov_id']});
					$no_prov.attr({'value' : entry['Vehiculo'][0]['no_prov']});
					$proveedor.attr({'value' : entry['Vehiculo'][0]['proveedor']});
					$id_operador.attr({'value' : entry['Vehiculo'][0]['operador_id']});
					$no_operador.attr({'value' : entry['Vehiculo'][0]['no_operador']});
					$operador.attr({'value' : entry['Vehiculo'][0]['operador']});
					$comentarios.text(entry['Vehiculo'][0]['comentarios']);
					
					
					//Carga select de Tipos de Unidades
					var elemento_seleccionado = entry['Vehiculo'][0]['tipo_id'];
					var texto_elemento_cero = '[-- --]';
					if(parseInt(DataObject['TUnidades'].length) > 0 ){
						texto_elemento_cero = '';
					}
					var index_elem = 'id';
					var index_text_elem = 'titulo';
					var option_fijo = false;
					$carga_campos_select($select_tipo_unidad, DataObject['TUnidades'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
					
					//Carga select de Clase de unidad
					elemento_seleccionado = entry['Vehiculo'][0]['clase_id'];
					texto_elemento_cero = '[-- --]';
					if(parseInt(DataObject['Clases'].length) > 0 ){
						if(parseInt(elemento_seleccionado)>0){
							texto_elemento_cero = '';
						}
					}
					index_elem = 'id';
					index_text_elem = 'titulo';
					option_fijo = false;
					$carga_campos_select($select_clase, DataObject['Clases'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
					
					
					//Carga select de Marcas e Camiones(Unidades)
					elemento_seleccionado = entry['Vehiculo'][0]['marca_id'];
					texto_elemento_cero = '[-- --]';
					if(parseInt(DataObject['Marcas'].length) > 0 ){
						if(parseInt(elemento_seleccionado)>0){
							texto_elemento_cero = '';
						}
					}
					index_elem = 'id';
					index_text_elem = 'titulo';
					option_fijo = false;
					$carga_campos_select($select_marca, DataObject['Marcas'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
					
					
					//Carga select de Tipos de Placas
					elemento_seleccionado = entry['Vehiculo'][0]['tplaca_id'];
					texto_elemento_cero = '[-- --]';
					if(parseInt(DataObject['TPlacas'].length) > 0 ){
						if(parseInt(elemento_seleccionado)>0){
							texto_elemento_cero = '';
						}
					}
					index_elem = 'id';
					index_text_elem = 'titulo';
					option_fijo = false;
					$carga_campos_select($select_tipo_placa, DataObject['TPlacas'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
					
					
					//Carga select de Tipos de Rodadas
					elemento_seleccionado = entry['Vehiculo'][0]['trodada_id'];
					texto_elemento_cero = '[-- --]';
					if(parseInt(DataObject['TRodadas'].length) > 0 ){
						if(parseInt(elemento_seleccionado)>0){
							texto_elemento_cero = '';
						}
					}
					index_elem = 'id';
					index_text_elem = 'titulo';
					option_fijo = false;
					$carga_campos_select($select_tipo_rodada, DataObject['TRodadas'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
					
					
					//Carga select de Tipos de Caja
					elemento_seleccionado = entry['Vehiculo'][0]['tcaja_id'];
					texto_elemento_cero = '[-- --]';
					if(parseInt(DataObject['TCajas'].length) > 0 ){
						if(parseInt(elemento_seleccionado)>0){
							texto_elemento_cero = '';
						}
					}
					index_elem = 'id';
					index_text_elem = 'titulo';
					option_fijo = false;
					$carga_campos_select($select_tipo_caja, DataObject['TCajas'], elemento_seleccionado, texto_elemento_cero, index_elem, index_text_elem, option_fijo);
					
					
					//Carga select de Años
					$select_anio.children().remove();
					var anio_html = '';
					$.each(DataObject['Anios'],function(entryIndex,anio){
						if(parseInt(anio['valor'])==parseInt(entry['Vehiculo'][0]['anio'])){
							anio_html += '<option value="' + anio['valor'] + '" selected="yes">'+ anio['valor'] + '</option>';
						}else{
							anio_html += '<option value="' + anio['valor'] + '">'+ anio['valor'] + '</option>';
						}
					});
					$select_anio.append(anio_html);
					
					
					//Cargar select de clasificacion2
					var elemento_seleccionado = entry['Vehiculo'][0]['clasificacion2'];
					var mostrar_opciones = 'true';
					$carga_select_con_arreglo_fijo($select_clasif2, array_clasificacion2, elemento_seleccionado, mostrar_opciones);
					
				},"json");//termina llamada json
				
				
				//Permitir solo numeros y punto
				$permitir_solo_numeros($cap_volumen);
				$permitir_solo_numeros($cap_peso);
				
				//Aplicar envento focus
				$aplica_evento_focus_input_numerico($cap_volumen);
				$aplica_evento_focus_input_numerico($cap_peso);
				

				$cap_volumen.blur(function(){
					$validar_numero_puntos($(this), "Capacidad&nbsp;m&#179;");
					if($(this).val().trim()==''){
						$(this).val(0);
					}
					$(this).val(parseFloat($(this).val()).toFixed(3));
				});
				
				$cap_peso.blur(function(){
					$validar_numero_puntos($(this), "Capacidad Ton.");
					if($(this).val().trim()==''){
						$(this).val(0);
					}
					$(this).val(parseFloat($(this).val()).toFixed(3));
				});

				
				
				//Buscador de provedores(Transportista)
				$busca_proveedor.click(function(event){
					event.preventDefault();
					$busca_proveedores($id_prov, $no_prov, $proveedor, $busca_proveedor );
				});
				
				
				$no_prov.keypress(function(e){
					var valor=$(this).val();
					
					if(e.which == 13){
						var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoProv.json';
						$arreglo2 = {'no_proveedor':$no_prov.val(),  'transportista':'true', 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
						
						$.post(input_json2,$arreglo2,function(entry2){
							if(parseInt(entry2['Proveedor'].length) > 0 ){
								var id_proveedor = entry2['Proveedor'][0]['id'];
								var no_proveedor = entry2['Proveedor'][0]['numero_proveedor'];
								var razon_soc_proveedor = entry2['Proveedor'][0]['razon_social'];
								
								//Llamada a la función que agrega datos del proveedor seleccionado
								$agregarDatosProveedorSeleccionado($id_prov, $no_prov, $proveedor, id_proveedor, no_proveedor, razon_soc_proveedor, $busca_proveedor);
							}else{
								$id_prov.val(0);
								$no_prov.val('');
								$proveedor.val('');
								
								jAlert('N&uacute;mero de Transportista(Proveedor) desconocido.', 'Atencion!', function(r) { 
									$no_prov.focus(); 
								});
							}
						},"json");//termina llamada json
						
						return false;
					}else{
						if (parseInt(e.which) == 8) {
							//Si se oprime la tecla borrar se vacía el campo no_prov 
							if(parseInt(valor.length)>0 && parseInt($id_prov.val())>0){
								jConfirm('Seguro que desea cambiar el Proveedor seleccionado?', 'Dialogo de Confirmacion', function(r) {
									// If they confirmed, manually trigger a form submission
									if (r) {
										$id_prov.val(0);
										$no_prov.val('');
										$proveedor.val('');
										
										//Quitar solo lectura una vez que se ha eliminado datos del Proveedor
										$quitar_readonly_input($proveedor);
										
										//Mostrar link busca Proveedores
										$busca_proveedor.show();
										
										//--Datos del OPERADOR------------------
										$id_operador.val(0);
										$no_operador.val('');
										$operador.val('');
										
										//Quitar solo lectura una vez que se ha eliminado datos del Proveedor
										$quitar_readonly_input($operador);
										
										//Mostrar link busca Operadores
										$busca_operador.show();
										//--Datos del OPERADOR------------------
										
										$no_prov.focus();
									}else{
										$no_prov.val(valor);
										$no_prov.focus();
									}
								});
							}else{
								$no_prov.focus();
							}
						}
					}
					
				});
				
				
				
				
				//Buscador de Operadores
				$busca_operador.click(function(event){
					event.preventDefault();
					/*
					Le asignamos cero al id del proveedor para permitir la busqueda de todos los choferes 
					sin importar el contratista con el que esté registrado
					*/
					var id_proveedor=0;
					$busca_operadores($id_operador, $no_operador, $operador, id_proveedor, $busca_operador);
				});
				
				$(this).aplicarEventoKeypressEjecutaTrigger($operador, $busca_operador);
				
				$no_operador.keypress(function(e){
					var valor=$(this).val();
					if(e.which == 13){
						if($no_operador.val().trim()!=''){
							var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataOperadorByNo.json';
							$arreglo2 = {'no_operador':$no_operador.val(), 'id_prov':0, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
							$.post(input_json2,$arreglo2,function(entry2){
								if(parseInt(entry2['Operador'].length) > 0 ){
									var id_operador = entry2['Operador'][0]['id'];
									var no_operador = entry2['Operador'][0]['id'];
									var operador = entry2['Operador'][0]['id'];
									
									/*
									Le asignamos cero al id del proveedor para permitir la busqueda de todos los choferes 
									sin importar el contratista con el que esté registrado
									*/
									var id_proveedor=0;
									
									//Llamada a la función que agrega datos del proveedor seleccionado
									$agregarDatosOperadorSeleccionado($id_operador, $no_operador, $operador, id_operador, no_operador, operador, $busca_operador);
									
								}else{
									jAlert('N&uacute;mero de Operador desconocido.', 'Atencion!', function(r) {
										$id_operador.val(0);
										$no_operador.val('');
										$no_operador.focus(); 
									});
								}
							},"json");//termina llamada json
						}
						return false;
					}else{
						if (parseInt(e.which) == 8) {
							//Si se oprime la tecla borrar se vacía el campo no_prov 
							if(parseInt(valor.length)>0 && parseInt($id_prov.val())>0){
								jConfirm('Seguro que desea cambiar el Operador seleccionado?', 'Dialogo de Confirmacion', function(r) {
									// If they confirmed, manually trigger a form submission
									if (r) {
										$id_operador.val(0);
										$no_operador.val('');
										$operador.val('');
										
										//Quitar solo lectura una vez que se ha eliminado datos del Operador
										$quitar_readonly_input($operador);
										
										//Mostrar link busca Operadores
										$busca_operador.show();
										
										$no_operador.focus();
									}else{
										$no_prov.val(valor);
										$no_prov.focus();
									}
								});
							}else{
								$no_prov.focus();
							}
						}
					}
				});



				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-logvehiculos-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-logvehiculos-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllVehiculos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {
            'orderby':'id',
            'desc':'DESC',
            'items_por_pag':10,
            'pag_start':1,
            'display_pag':10,
            'input_json':'/'+controller+'/getAllVehiculos.json',
            'cadena_busqueda':$cadena_busqueda,
            'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
    
});
