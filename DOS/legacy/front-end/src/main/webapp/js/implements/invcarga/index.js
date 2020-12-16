$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select tipo de Ajuste
    var arrayTiposAjuste = {
				0:"Positivo", //grupo Entradas en la tabla tipos de movimiento de Invetario
				2:"Negativo",//grupo salidas en la tabla tipos de movimiento de Invetario
			};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invcarga";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $new = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseover(function(){
		$(this).removeClass("onmouseOutNewItem").addClass("onmouseOverNewItem");
	});
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseout(function(){
		$(this).removeClass("onmouseOverNewItem").addClass("onmouseOutNewItem");
	});
	
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Carga de inventario f&iacute;sico');
	
	$('#barra_buscador').hide();
	
	
	
	
    
	
	$tabs_li_funxionalidad = function(){
		var $select_prod_tipo = $('#forma-invcarga-window').find('select[name=prodtipo]');
		$('#forma-invcarga-window').find('#submit').mouseover(function(){
			$('#forma-invcarga-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invcarga-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-invcarga-window').find('#submit').mouseout(function(){
			$('#forma-invcarga-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-invcarga-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-invcarga-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invcarga-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invcarga-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invcarga-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-invcarga-window').find('#close').mouseover(function(){
			$('#forma-invcarga-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-invcarga-window').find('#close').mouseout(function(){
			$('#forma-invcarga-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-invcarga-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invcarga-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invcarga-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invcarga-window').find("ul.pestanas li").click(function() {
			$('#forma-invcarga-window').find(".contenidoPes").hide();
			$('#forma-invcarga-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invcarga-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	//funcion para hacer que un campo solo acepte numeros
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
	
	
	
	
	$aplicar_evento_keypress = function( $campo_input ){
		//validar campo cantidad recibida, solo acepte numeros y punto
		$campo_input.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if(e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	$aplicar_evento_focus_input = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val() == ' ' || parseFloat($(this).val()) <= 0){
				$(this).val('');
			}
		});
	}
	
	$aplicar_evento_blur_input = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if(parseFloat($campo_input.val())==0 || $campo_input.val()==""){
				$campo_input.val(0);
				$campo_input.val(parseFloat($campo_input.val()).toFixed(2))
			}
		});
	}
	
	
	
	
	$aplicar_evento_focus = function( $campo_input ){
		$campo_input.focus(function(e){
			if($(this).val() == ' '){
				$(this).val('');
			}
		});
	}
	
	
	$aplicar_evento_blur = function( $campo_input ){
		//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
		$campo_input.blur(function(e){
			if($campo_input.val()=='0' || $campo_input.val()==""){
				$campo_input.val(' ');
			}
		});
	}
	
	//funcion para aplicar metodo click para eliminar tr
	$aplicar_evento_eliminar = function( $campo_href ){
		//eliminar un lote
		$campo_href.click(function(e){
			e.preventDefault();
			$tr_padre=$(this).parent().parent();
			//$tr_padre.find('input').val('');//asignar vacio a todos los input del tr
			//$tr_padre.find('input[name=eliminado]').val('0');//asignamos 0 para indicar que se ha eliminado
			$tr_padre.remove();//eliminar el tr
			
			$grid_productos = $tr_padre.parent();
			
			
		});
	}
	
	
	
	
	//Funcion para eliminar el archivo cargado si es que no se decide actualizar los datos del inventario
	deleteFile = function( $nombre_archivo, id_user, $buttonSeleccionarArchivo, $eliminar_archivo, $nombre_archivo ){
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/deleteFile.json';
		var arreglo_parametros = {'file': $nombre_archivo.val(), 'iu':id_user };
		$.post(input_json,arreglo_parametros,function(entry){
			if(entry['success']==true){
				$buttonSeleccionarArchivo.show();
				$eliminar_archivo.hide();
				$nombre_archivo.val('');
			}
			
			jAlert(entry['msj'], 'Atencion!', function(r) { 
				$eliminar_archivo.focus();
			});
			
		});
	}
	
	
	
	//nuevo 
	$new.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_invcarga();
		
		var form_to_show = 'formainvcarga00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$('#forma-invcarga-window').css({"margin-left": -200, 	"margin-top": -210});
		
		$forma_selected.prependTo('#forma-invcarga-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$tabs_li_funxionalidad();
		
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCargar.json';
		$arreglo = {'identificador':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
        
		var $identificador = $('#forma-invcarga-window').find('input[name=identificador]');
		
		var $select_almacen = $('#forma-invcarga-window').find('select[name=select_almacen]');
		var $descargar_formato = $('#forma-invcarga-window').find('#descargar_formato');
		var $seleccionar_archivo = $('#forma-invcarga-window').find('#seleccionar_archivo');
		var $eliminar_archivo = $('#forma-invcarga-window').find('#eliminar_archivo');
		var $nombre_archivo = $('#forma-invcarga-window').find('#nombre_archivo');
		
		var $select_prod_tipo = $('#forma-invcarga-window').find('select[name=select_prod_tipo]');
		var $select_linea = $('#forma-invcarga-window').find('select[name=select_linea]');
		var $select_marca = $('#forma-invcarga-window').find('select[name=select_marca]');
		var $select_familia = $('#forma-invcarga-window').find('select[name=select_familia]');
		var $select_subfamilia = $('#forma-invcarga-window').find('select[name=select_subfamilia]');
		
		var $select_tipo_reporte = $('#forma-invcarga-window').find('select[name=select_tipo_reporte]');
		
		
		//grid de productos
		var $grid_productos = $('#forma-invcarga-window').find('#grid_productos');
		//grid de errores
		var $grid_warning = $('#forma-invcarga-window').find('#div_warning_grid').find('#grid_warning');
		
		
		var $cerrar_plugin = $('#forma-invcarga-window').find('#close');
		var $cancelar_plugin = $('#forma-invcarga-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-invcarga-window').find('#submit');
		


		$eliminar_archivo.hide();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				var remove = function() {$(this).remove();};
				$('#forma-invcarga-overlay').fadeOut(remove);
			}
			
			jAlert(data['msj'], 'Atencion!');
		}
		
		
		var options = {dataType:'json', success:respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		$.post(input_json,$arreglo,function(entry){
			
			//Alimentar select de tipo de reporte
			$select_tipo_reporte.children().remove();
			var tipo_reporte_html = '<option value="1">Inventario</option>';
			tipo_reporte_html += '<option value="2">Lotes</option>';
			//tipo_reporte_html += '<option value="3">[--Presentaciones--]</option>';
			$select_tipo_reporte.append(tipo_reporte_html);
			
			//carga select Con los almacenes
			$select_almacen.children().remove();
			var alm_hmtl = '<option value="0">Todos</option>';
			$.each(entry['Alms'],function(entryIndex,alm){
				alm_hmtl += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
			});
			$select_almacen.append(alm_hmtl);
			
			//Alimentando select de lineas
			$select_linea.children().remove();
			var lineas_hmtl = '<option value="0">[--Seleccionar Linea--]</option>';
			$.each(entry['Lineas'],function(entryIndex,lin){
				lineas_hmtl += '<option value="' + lin['id'] + '"  >' + lin['titulo'] + '</option>';
			});
			$select_linea.append(lineas_hmtl);
			
			//Alimentando select de marcas
			$select_marca.children().remove();
			var marcas_hmtl = '<option value="0">[--Seleccionar Marca--]</option>';
			$.each(entry['Marcas'],function(entryIndex,mar){
				marcas_hmtl += '<option value="' + mar['id'] + '"  >' + mar['titulo'] + '</option>';
			});
			$select_marca.append(marcas_hmtl);
			
			//carga select de tipos de producto
			$select_prod_tipo.children().remove();
			var prodtipos_hmtl = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
			$.each(entry['Tipos'],function(entryIndex,tipo){
				prodtipos_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
			});
			$select_prod_tipo.append(prodtipos_hmtl);
			
			//Alimentando select de familias
			$select_familia.children().remove();
			var familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
			$select_familia.append(familia_hmtl);
			
			//Alimentando select de familias
			$select_subfamilia.children().remove();
			var subfamilia_hmtl = '<option value="0">[--Seleccionar Sub-Familia--]</option>';
			$select_subfamilia.append(subfamilia_hmtl);
			
			
			$select_prod_tipo.change(function(){
				var valor_tipo = $(this).val();
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFamiliasByTipoProd.json';
				$arreglo = {	'tipo_prod':$select_prod_tipo.val(),
								'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				$.post(input_json,$arreglo,function(data){
					$select_familia.children().remove();
					var familia_hmtl = '<option value="0">[--Seleccionar Familia--]</option>';
					$.each(data['Familias'],function(entryIndex,fam){
						familia_hmtl += '<option value="' + fam['id'] + '"  >' + fam['titulo'] + '</option>';
					});
					$select_familia.append(familia_hmtl);
				});
				
			});
			
			
			$select_familia.change(function(){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSubFamiliasByFamProd.json';
				$arreglo = {'fam':$select_familia.val(),
								'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							}
				//Alimentando select de Subfamilias
				$.post(input_json,$arreglo,function(data){
					$select_subfamilia.children().remove();
					var subfamilia_hmtl = '<option value="0">[--Seleccionar Subfmilia--]</option>';
					$.each(data['SubFamilias'],function(dataIndex,subfam){
						subfamilia_hmtl += '<option value="' + subfam['id'] + '"  >' + subfam['titulo'] + '</option>';
					});
					$select_subfamilia.append(subfamilia_hmtl);
				});
				
				//Reiniciar select de subfamilias
				$select_subfamilia.children().remove();
				var subfamilia_hmtl = '<option value="0">[--Seleccionar Sub-Familia--]</option>';
				$select_subfamilia.append(subfamilia_hmtl);
			});
			
			
			
		},"json");//termina llamada json
		
		
		$descargar_formato.click(function(event){
			event.preventDefault();
			
			var parametros = $select_tipo_reporte.val()+"/"+$select_almacen.val()+"/"+$select_linea.val()+"/"+$select_marca.val()+"/"+$select_prod_tipo.val()+"/"+$select_familia.val()+"/"+$select_subfamilia.val();
			
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller + '/getFormato/'+parametros+'/'+iu+'/out.json'
			window.location.href=input_json;
			
		});//termina llamada json



		/*Codigo para subir la xls*/
		var input_json_upload = document.location.protocol + '//' + document.location.host + '/'+controller+'/fileUpload.json';
		var $buttonSeleccionarArchivo = $('#forma-invcarga-window').find('#seleccionar_archivo'), interval;
		new AjaxUpload($buttonSeleccionarArchivo,{
			action: input_json_upload, 
			name: 'file',
			onSubmit : function(file , ext){
				//if ((/^(xls)$/.test(ext)) || (/^(xlsx)$/.test(ext))){
				if(/^(xls)$/.test(ext)){
					$buttonSeleccionarArchivo.text('Cargando..');
					this.disable();
				} else {
					jAlert("Fichero no valido.", 'Atencion!');
					return false;
				}
			},
			onComplete: function(file, response){
				//button_pdf.text('Cambiar PDF');
				window.clearInterval(interval);
				this.enable();
				$nombre_archivo.val(file);
				if(response=="true"){
					$buttonSeleccionarArchivo.hide();
					$eliminar_archivo.show();
				}
			},
		});
		
		
		
		
		$eliminar_archivo.click(function(event){
			event.preventDefault();
			var id_user = $('#lienzo_recalculable').find('input[name=iu]').val();
			deleteFile( $nombre_archivo, id_user, $buttonSeleccionarArchivo, $eliminar_archivo, $nombre_archivo );
		});//termina llamada json

		
		
		//deshabilitar tecla enter  en todo el plugin
		$('#forma-invcarga-window').find('input').keypress(function(e){
			if(e.which==13 ) {
				return false;
			}
		});
		
				
		
		$submit_actualizar.bind('click',function(){
			
			if($nombre_archivo.val().trim()==''){
				jAlert("No se ha seleccionado ningun archivo para actualizar datos del inventario.", 'Atencion!', function(r) { 
					$buttonSeleccionarArchivo.focus();
				});
				return false;
			}else{
				return true;
			}
			
			var trCount = $("tr", $grid_productos).size();
			if(parseInt(trCount) > 0){
				return true;
			}else{
				jAlert("No hay productos para ajuste.", 'Atencion!');
				return false;
			}
		});
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			if($nombre_archivo.val().trim()!=''){
				var id_user = $('#lienzo_recalculable').find('input[name=iu]').val();
				deleteFile( $nombre_archivo, id_user, $buttonSeleccionarArchivo, $eliminar_archivo, $nombre_archivo );
			}
			
			var remove = function() {$(this).remove();};
			$('#forma-invcarga-overlay').fadeOut(remove);
		});
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			if($nombre_archivo.val().trim()!=''){
				var id_user = $('#lienzo_recalculable').find('input[name=iu]').val();
				deleteFile( $nombre_archivo, id_user, $buttonSeleccionarArchivo, $eliminar_archivo, $nombre_archivo );
			}
			
			var remove = function() {$(this).remove();};
			$('#forma-invcarga-overlay').fadeOut(remove);
		});
		
	});
	
	

    
    
});



