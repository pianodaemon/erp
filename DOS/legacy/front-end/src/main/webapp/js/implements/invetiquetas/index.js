$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    var arrayTiposOrigen = {
				1:"Entrada", 
				2:"Producci&oacute;n",
				3:"Requisici&oacute;n"
			  };
			
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
    var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
    var controller = $contextpath.val()+"/controllers/invetiquetas";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
        var $new_etiquetas = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
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
	$('#barra_titulo').find('#td_titulo').append('Cat&aacute;logo de Impresion de Etiquetas');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
    
    
	var bus_tipo_origen="0";
	var $cadena_busqueda = "";
	var $busqueda_folio = $('#barra_buscador').find('.tabla_buscador').find('input[name=folio]');
        var $busqueda_folio_origen = $('#barra_buscador').find('.tabla_buscador').find('input[name=folio_origen]');
        var $busqueda_tipo_origen = $('#barra_buscador').find('.tabla_buscador').find('select[name=tipo_origen]');
        
        
        
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Buscar]');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('input[value$=Limpiar]');
	
	
	if($busqueda_tipo_origen.val() == 0){
             bus_tipo_origen="";
        }else{
             bus_tipo_origen=$busqueda_tipo_origen.val();
        }
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "folio" + signo_separador + $busqueda_folio.val() + "|";
                valor_retorno += "folio_origen" + signo_separador + bus_tipo_origen + "|";
                
                valor_retorno += "tipo_origen" + signo_separador + $busqueda_tipo_origen.val() + "|";
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
                $busqueda_folio.val(' ');
                $busqueda_folio_origen.val(' ');
                $busqueda_tipo_origen.val(' ');
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
		
		$('#forma-etiquetas-window').find('#submit').mouseover(function(){
			$('#forma-etiquetas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-etiquetas-window').find('#submit').mouseout(function(){
			$('#forma-etiquetas-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-etiquetas-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-etiquetas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-etiquetas-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-etiquetas-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-etiquetas-window').find('#close').mouseover(function(){
			$('#forma-etiquetas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-etiquetas-window').find('#close').mouseout(function(){
			$('#forma-etiquetas-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-etiquetas-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-etiquetas-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-etiquetas-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-etiquetas-window').find("ul.pestanas li").click(function() {
			$('#forma-etiquetas-window').find(".contenidoPes").hide();
			$('#forma-etiquetas-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-etiquetas-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	
	
	//BUSCADOR DE ENTRADAS...
	$busca_entradas = function(tipo_origen){
                //limpiar_campos_grids();
                //alert("este es el tipo de origen cargado en el pluguinin:::::    "+tipo_origen);
		$(this).modalPanel_Buscaentradas();
		var $dialogoc =  $('#forma-buscaentradas-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
		$dialogoc.append($('div.buscador_entradas').find('table.formaBusqueda_entradas').clone());
		
		$('#forma-buscaentradas-window').css({"margin-left": -200, 	"margin-top": -180});
		
		var $tabla_resultados = $('#forma-buscaentradas-window').find('#tabla_resultado');
		
		var $campo_folio = $('#forma-buscaentradas-window').find('input[name=folio_origen]');
                var $fecha_inicial = $('#forma-buscaentradas-window').find('input[name=fecha_inicial]');
                var $fecha_final = $('#forma-buscaentradas-window').find('input[name=fecha_final]');
               
		
		
		var $boton_buscar = $('#forma-buscaentradas-window').find('#busca_producto_modalbox');
		var $cancelar_plugin_busca_entrada = $('#forma-buscaentradas-window').find('#cencela');
                
                
                $fecha_inicial.attr('readonly',true);
		$fecha_final.attr('readonly',true);
                
                //valida la fecha seleccionada
		function mayor(fecha, fecha2){
			var xMes=fecha.substring(5, 7);
			var xDia=fecha.substring(8, 10);
			var xAnio=fecha.substring(0,4);
			var yMes=fecha2.substring(5, 7);
			var yDia=fecha2.substring(8, 10);
			var yAnio=fecha2.substring(0,4);
			
			if (xAnio > yAnio){
				return(true);
			}else{
				if (xAnio == yAnio){
					if (xMes > yMes){
						return(true);
					}
					if (xMes == yMes){
						if (xDia > yDia){
							return(true);
						}else{
							return(false);
						}
					}else{
						return(false);
					}
				}else{
					return(false);
				}
			}
		}
		//muestra la fecha actual
		var mostrarFecha = function mostrarFecha(){
			var ahora = new Date();
			var anoActual = ahora.getFullYear();
			var mesActual = ahora.getMonth();
			mesActual = mesActual+1;
			mesActual = (mesActual <= 9)?"0" + mesActual : mesActual;
			var diaActual = ahora.getDate();
			diaActual = (diaActual <= 9)?"0" + diaActual : diaActual;
			var Fecha = anoActual + "-" + mesActual + "-" + diaActual;		
			return Fecha;
		}
		//----------------------------------------------------------------
			
			
		
		$fecha_inicial.DatePicker({
			format:'Y-m-d',
			date: $fecha_inicial.val(),
			current: $fecha_inicial.val(),
			starts: 1,
			position: 'bottom',
			locale: {
				days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
				daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
				daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
				months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
				monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
				weekMin: 'se'
			},
			onChange: function(formated, dates){
				var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
				$fecha_inicial.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_inicial.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_inicial.val(mostrarFecha());
					}else{
						$fecha_inicial.DatePickerHide();	
					}
				}
			}
		});
			
		$fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
		});
			
		mostrarFecha($fecha_inicial.val());
		
                
                //fecha final
			
			
		$fecha_final.DatePicker({
			format:'Y-m-d',
			date: $fecha_final.val(),
			current: $fecha_final.val(),
			starts: 1,
			position: 'bottom',
			locale: {
				days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
				daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
				daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
				months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
				monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
				weekMin: 'se'
			},
			onChange: function(formated, dates){
				var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
				$fecha_final.val(formated);
				if (formated.match(patron) ){
					var valida_fecha=mayor($fecha_final.val(),mostrarFecha());
					
					if (valida_fecha==true){
						jAlert("Fecha no valida",'! Atencion');
						$fecha_final.val(mostrarFecha());
					}else{
						$fecha_final.DatePickerHide();	
					}
				}
			}
		});
			
        
        
        $fecha_final.DatePicker({
		format:'Y-m-d',
		date: $fecha_final.val(),
		current: $fecha_final.val(),
		starts: 1,
		position: 'bottom',
		locale: {
			days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
			daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
			daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
			months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
			monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
			weekMin: 'se'
		},
		onChange: function(formated, dates){
			var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
			$fecha_final.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($fecha_final.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$fecha_final.val(mostrarFecha());
                                        
                                         
                                       
				}else{
					$fecha_final.DatePickerHide();	
				}
			}
		}
	});
        
	$fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
        
        
        mostrarFecha($fecha_final.val());
        
        
                
      
                
                
		
		//funcionalidad botones
		$boton_buscar.mouseover(function(){
			$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
		});
		$boton_buscar.mouseout(function(){
			$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
		});
		   
		$cancelar_plugin_busca_entrada.mouseover(function(){
			$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
		});
		$cancelar_plugin_busca_entrada.mouseout(function(){
			$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
		});
	
		//click buscar productos
		$boton_buscar.click(function(event){
			//event.preventDefault();
			var trr = '';
			var $folio = $('#forma-etiquetas-window').find('input[name=folio_or]');
			var $grid_entradas = $('#forma-etiquetas-window').find('#grid_entradas');
			
			if(tipo_origen == 1){
				$tabla_resultados.children().remove();
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorEntradas.json';
				$arreglo = {'folio':$campo_folio.val(),
                                            'fecha_inicial':$fecha_inicial.val(),
                                            'fecha_final':$fecha_final.val(),
                                            'tipo_origen':tipo_origen,
                                            'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					   }
                                           
                                        
	
				
				$.post(input_json,$arreglo,function(entry){
					$.each(entry['entradas'],function(entryIndex,entrada){
						trr = '<tr>';
							trr += '<td width="40">';
								trr += '<input type="hidden" id="id_oent" value="'+entrada['id']+'">';
								trr += '<span class="buscador_folio_oent">'+entrada['folio']+'</span>';
							trr += '</td>';
							
							
							trr += '<td width="200">';
								trr += '<span class="buscador_proveedor_oent">'+entrada['proveedor']+'</span>';
								//trr += '<span type="hidden" class="buscador_proveedor_id_oent">'+entrada['proveedor_id']+'</span>';
							trr += '</td>';

							trr += '<td width="100px" align="right">';
								trr += '<span class="buscador_momento_creacion_oent" style="display:none;">'+entrada['fecha_entrada']+'</span>';
								trr += '<span class="buscador_orden_compra_oent">'+entrada['orden_compra']+'</span>';
							trr += '</td>';

							trr += '<td width="100px" align="center">';
								trr += '<span class="buscador_fecha_documento_oent">'+entrada['fecha_documento']+'</span>';
							trr += '</td>';
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
				
				//seleccionar un elemento  del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					//asignar a los campos correspondientes al primer pliguin al seleccionar una entrada
					$folio.val($(this).find('span.buscador_folio_oent').html());
					
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaentradas-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-buscaentradas-window').find('input[name=folio]').focus();
					$cargando_lotes($folio.val(),$fecha_inicial.val(),$fecha_final.val(),tipo_origen,$grid_entradas);
				});
			});//termina llamada json		
		}
		
		
		if(tipo_origen == 2){
					$tabla_resultados.children().remove();
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorEntradas.json';
					$arreglo = {'folio':$campo_folio.val(),
								'fecha_inicial':$fecha_inicial.val(),
								'fecha_final':$fecha_final.val(),
								'tipo_origen':tipo_origen,
								'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
								}
					 
					$.post(input_json,$arreglo,function(entry){
						$.each(entry['entradas'],function(entryIndex,entrada){
							trr = '<tr>';
								trr += '<td width="40">';
									trr += '<input type="hidden" id="id_oent" value="'+entrada['id']+'">';
									trr += '<span class="buscador_folio_oent">'+entrada['folio_documento']+'</span>';
								trr += '</td>';
                                                                var prov='';
                                                                if(entrada['proveedor_id']='0'){
                                                                    prov='';
                                                                }else{
                                                                    prov=entrada['proveedor'];
                                                                }
								trr += '<td width="200">';
									trr += '<span class="buscador_proveedor_oent">'+prov+'</span>';
									trr += '<span class="buscador_proveedor_id_oent">'+entrada['proveedor']+'</span>';
								trr += '</td>';
								trr += '<td width="100px" align="right">';
									trr += '<span class="buscador_momento_creacion_oent" style="display:none;">'+entrada['momento_creacion']+'</span>';
									trr += '<span class="buscador_orden_compra_oent">'+entrada['orden_compra']+'</span>';
								trr += '</td>';
								trr += '<td width="100px" align="center">';
									trr += '<span class="buscador_fecha_documento_oent">'+entrada['momento_creacion']+'</span>';
								trr += '</td>';
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
	
						//seleccionar un elemento del grid de resultados
						$tabla_resultados.find('tr').click(function(){
							//asignar a los campos correspondientes al primer pliguin al seleccionar una entrada
							$folio.val($(this).find('span.buscador_folio_oent').html());

							//elimina la ventana de busqueda
							var remove = function() {$(this).remove();};
							$('#forma-buscaentradas-overlay').fadeOut(remove);
							//asignar el enfoque al campo sku del producto
							$('#forma-buscaentradas-window').find('input[name=folio]').focus();
							$cargando_lotes($folio.val(),$fecha_inicial.val(),$fecha_final.val(),tipo_origen,$grid_entradas);
						});
					
				});//termina llamada json
		
		}
		
		
		
		
		if(tipo_origen == 3){
			$tabla_resultados.children().remove();
			//alert("Esta entrando aqui donde el tipo origen es "+tipo_origen +"y estemetodo esta  en la parte de editar");
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getBuscadorEntradas.json';
			$arreglo = {'folio':$campo_folio.val(),
						'fecha_inicial':$fecha_inicial.val(),
						'fecha_final':$fecha_final.val(),
						'tipo_origen':tipo_origen,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					}
			 
			$.post(input_json,$arreglo,function(entry){
				$.each(entry['entradas'],function(entryIndex,entrada){
					trr = '<tr>';
						trr += '<td width="40">';
							//trr += '<input type="hidden" id="id_oent" value="'+entrada['folio_documento']+'">';
							trr += '<span class="buscador_folio_oent">'+entrada['folio_documento']+'</span>';
						trr += '</td>';
						trr += '<td width="200">';
                                                                var prov='';
                                                                if(entrada['proveedor']='0'){
                                                                    prov='';
                                                                }else{
                                                                    prov=entrada['proveedor'];
                                                                }
							trr += '<span class="buscador_proveedor_oent">'+prov+'</span>';
							trr += '<span class="buscador_proveedor_id_oent">'+prov+'</span>';
						trr += '</td>';
						trr += '<td width="100px" align="right">';
							//trr += '<span class="buscador_momento_creacion_oent" style="display:none;">'+entrada['fecha_entrada']+'</span>';
							trr += '<span class="buscador_orden_compra_oent">'+entrada['orden_compra']+'</span>';
						trr += '</td>';
						trr += '<td width="100px" align="center">';
							trr += '<span class="buscador_fecha_documento_oent">'+entrada['fecha_documento']+'</span>';
						trr += '</td>';
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

				//seleccionar un elemento del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					//asignar a los campos correspondientes al primer pliguin al seleccionar una entrada
					$folio.val($(this).find('span.buscador_folio_oent').html());
					//fin de agregar valores a campos del primer pluguin
					//elimina la ventana de busqueda
					var remove = function() {$(this).remove();};
					$('#forma-buscaentradas-overlay').fadeOut(remove);
					//asignar el enfoque al campo sku del producto
					$('#forma-buscaentradas-window').find('input[name=folio]').focus();
					$cargando_lotes($folio.val(),$fecha_inicial.val(),$fecha_final.val(),tipo_origen,$grid_entradas);
				});
			
			});//termina llamada json
		}
		});
		
		
		//si hay algo en el campo folio al cargar el buscador, ejecuta la busqueda
		if($campo_folio.val() != ''){
			$boton_buscar.trigger('click');
		}
		
		$cancelar_plugin_busca_entrada.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaentradas-overlay').fadeOut(remove);
		});
	}//Termina Buscador
	
	
	
	
        
	$cargando_lotes= function(folio,fecha_inicial,fecha_final,tipo_origen,$grid_entradas){
		if(tipo_origen == 1){
			 //alert("aqui se pinta los elementos del grid de acuerdo A folio::"+folio+"fecha_inicial::"+fecha_inicial+"fecha_final::"+fecha_final+"tipo_origen:"+tipo_origen);
			 //json para llenar el grid del primer pluguin                         
					
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getgridEntradas.json';
			$arreglo = {	'folio':folio,
                                        'fecha_inicial':fecha_inicial,
                                        'fecha_final':fecha_final,
                                        'tipo_origen':tipo_origen,
                                        'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
				   }
			$.post(input_json,$arreglo,function(entry){
				if(entry['grid_entradas'] != null){
					$.each(entry['grid_entradas'],function(entryIndex,entrads){
						trr = '<tr>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
								trr += '<input type="hidden" name="id_lote"  value="'+ entrads['id_lote'] +'">'; 
								trr += '<input type="hidden" name="id_producto"  value="'+ entrads['inv_prod_id'] +'">'; 
                                                                trr += '<input type="hidden" name="etiqueta_detalle_id"  value=0>';  
                                                                trr += '<input type="hidden" name="cantidad_produccion"  value="'+ entrads['cantidad_produccion'] +'">'; 
								trr += '<INPUT TYPE="text" name="cantidad" value="1" style="width:70px;">';
							trr += '</td>';
                                                        
                                                        trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                                                 trr += '<input type="hidden" name="inv_etiquetas_id"  value="'+ entrads['inv_etiquetas_id'] +'">';
                                                                 trr += '<input type="hidden" name="tipo_de_producto_id"  value="'+ entrads['tipo_de_producto_id'] +'">'; 
                                                                 trr += '<select name="inv_etiqueta_medidas_id" style="width:70px;">';
                                                                 //aqui se carga el select 
                                                                 $.each(entry['MedidasEtiqueta'],function(entryIndex,med){
                                                                         
                                                                                 trr += '<option  value="' + med['id'] + '"  >' + med['titulo'] + '</option>';
                                                                        
                                                                 });
                                                                 trr += '</select>';
                                                        trr += '</td>';
                                                        
                                                        
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="230px">';
								trr += '<INPUT TYPE="text" name="lote_interno" value="'+ entrads['lote_interno'] +'"  class="borde_oculto" readOnly="true"  style="width:230px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
								trr += '<INPUT TYPE="text" name="codigo"  value="'+ entrads['codigo'] +'"  class="borde_oculto" readOnly="true"  style="width:70px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200px">';
								trr += '<INPUT TYPE="text" name="sku"   value="'+ entrads['descripcion'] +'"  class="borde_oculto" readOnly="true"  style="width="296px";">';
							trr += '</td>';
						trr += '</tr>';
						
						$grid_entradas.append(trr);                 
					});
							 
				}
			});//termina llamada json que trae los datos del grid (primer pluguin)  CARGANDO DATOS DE ENTRADAS.
		}//FIN DEL TIPO DE ORIGEN 1 ( ENTRADAS )
		
		if(tipo_origen == 2){
			//alert("aqui se pinta los elementos del grid de acuerdo A folio::"+folio+"fecha_inicial::"+fecha_inicial+"fecha_final::"+fecha_final+"tipo_origen:"+tipo_origen);
			//json para llenar el grid del primer pluguin                         
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getgridEntradas.json';
			$arreglo = {	'folio':folio,
                                        'fecha_inicial':fecha_inicial,
                                        'fecha_final':fecha_final,
                                        'tipo_origen':tipo_origen,
                                        'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                    }
			$.post(input_json,$arreglo,function(entry){
				if(entry['grid_entradas'] != null){
					$.each(entry['grid_entradas'],function(entryIndex,entrads){
						trr = '<tr>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
								trr += '<input type="hidden" name="id_lote"  value="'+ entrads['id_lote'] +'">'; 
                                                                trr += '<input type="hidden" name="id_producto"  value="'+ entrads['inv_prod_id'] +'">'; 
                                                                trr += '<input type="hidden" name="etiqueta_detalle_id"  value=0>';  
                                                                trr += '<input type="hidden" name="cantidad_produccion"  value="'+ entrads['cantidad_produccion'] +'">'; 
								trr += '<INPUT TYPE="text" name="cantidad" value=" 1" style="width:70px;">';
							trr += '</td>';
                                                        trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                                        trr += '<input type="hidden" name="inv_etiquetas_id"  value="'+ entrads['inv_etiquetas_id'] +'">';
                                                        trr += '<input type="hidden" name="tipo_de_producto_id"  value="'+ entrads['tipo_de_producto_id'] +'">'; 
                                                                 trr += '<select name="inv_etiqueta_medidas_id" style="width:70px;">';
                                                                 //aqui se carga el select 
                                                                 $.each(entry['MedidasEtiqueta'],function(entryIndex,med){
                                                                         
                                                                                 trr += '<option  value="' + med['id'] + '"  >' + med['titulo'] + '</option>';
                                                                         
                                                                 });
                                                                 trr += '</select>';
                                                        trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="230px">';
								trr += '<INPUT TYPE="text" name="lote_interno" value="'+ entrads['folio'] +'"  class="borde_oculto" readOnly="true"  style="width:230px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
								trr += '<INPUT TYPE="text" name="codigo"  value="'+ entrads['codigo'] +'"  class="borde_oculto" readOnly="true"  style="width:70px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200px">';
								trr += '<INPUT TYPE="text" name="sku"   value="'+ entrads['descripcion'] +'"  class="borde_oculto" readOnly="true"  style="width="200px";">';
							trr += '</td>';
						trr += '</tr>';
						
						$grid_entradas.append(trr);                 
					});
							 
				}
			});//termina llamada json que trae los datos del grid (primer pluguin)  CARGANDO DATOS DE PRODUCCION
		}//fin del tipo_origen 2 (PRODUCCION)
                
                
                
                if(tipo_origen == 3){
			//alert("aqui se pinta los elementos del grid de acuerdo A folio::"+folio+"fecha_inicial::"+fecha_inicial+"fecha_final::"+fecha_final+"tipo_origen:"+tipo_origen);
			//json para llenar el grid del primer pluguin                         
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getgridEntradas.json';
			$arreglo = {	'folio':folio,
                                        'fecha_inicial':fecha_inicial,
                                        'fecha_final':fecha_final,
                                        'tipo_origen':tipo_origen,
                                        'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
                                    }
			$.post(input_json,$arreglo,function(entry){
				if(entry['grid_entradas'] != null){
					$.each(entry['grid_entradas'],function(entryIndex,entrads){
						trr = '<tr>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
								trr += '<input type="hidden" name="id_lote"  value="'+ entrads['id_lote'] +'">';
                                                                trr += '<input type="hidden" name="etiqueta_detalle_id"  value=0>'; 
                                                                trr += '<input type="hidden" name="id_producto"  value="'+ entrads['inv_prod_id'] +'">'; 
                                                                trr += '<input type="hidden" name="cantidad_produccion"  value="'+ entrads['cantidad_produccion'] +'">'; 
								trr += '<INPUT TYPE="text" name="cantidad" value="1" style="width:70px;">';
							trr += '</td>';
                                                        trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                                        trr += '<input type="hidden" name="inv_etiquetas_id"  value="'+ entrads['inv_etiquetas_id'] +'">';
                                                        trr += '<input type="hidden" name="tipo_de_producto_id"  value="'+ entrads['tipo_de_producto_id'] +'">'; 
                                                                 trr += '<select name="inv_etiqueta_medidas_id" style="width:70px;">';
                                                                 //aqui se carga el select 
                                                                 $.each(entry['MedidasEtiqueta'],function(entryIndex,med){
                                                                         trr += '<option  value="' + med['id'] + '"  >' + med['titulo'] + '</option>';
                                                                  });
                                                                 trr += '</select>';
                                                        trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="230px">';
								trr += '<INPUT TYPE="text" name="lote_interno" value="'+ entrads['lote_interno'] +'"  class="borde_oculto" readOnly="true"  style="width:230px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
								trr += '<INPUT TYPE="text" name="codigo"  value="'+ entrads['codigo'] +'"  class="borde_oculto" readOnly="true"  style="width:70px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200px">';
								trr += '<INPUT TYPE="text" name="sku"   value="'+ entrads['descripcion'] +'"  class="borde_oculto" readOnly="true"  style="width="200px";">';
							trr += '</td>';
						trr += '</tr>';
						
						$grid_entradas.append(trr);                 
					});
							 
				}
			});//termina llamada json que trae los datos del grid (primer pluguin)  CARGANDO DATOS DE REQUISICION.
		}//fin del tipo_origen 3 (REQUISISCIONES)
         }
	
        
        
        
	
	
        
        
	//nuevas etiquetas
	$new_etiquetas.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		$(this).modalPanel_etiquetas();   //llamada al plug in 
		
		var form_to_show = 'formaEtiquetas';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$('#forma-etiquetas-window').css({"margin-left": -300, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-etiquetas-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		var $campo_id = $('#forma-etiquetas-window').find('input[name=identificador]');                
		var $tipo_origen = $('#forma-etiquetas-window').find('input[name=tipo_origen]');
		
		var $folio_automatico = $('#forma-etiquetas-window').find('input[name=folio_automatico]');
		var $select_tipo_origen = $('#forma-etiquetas-window').find('select[name=tipo_origen]');
		var $folio_origen = $('#forma-etiquetas-window').find('input[name=folio_or]');
		var $busca_entrada = $('#forma-etiquetas-window').find('a[href*=busca_entradas]');
		
		var $descargar_pdf = $('#forma-etiquetas-window').find('#descargar_pdf');
		var $imprimir_pdf =  $('#forma-etiquetas-window').find('#imprimir_pdf');
		
		var $grid_entradas = $('#forma-etiquetas-window').find('#grid_entradas');
		
		//botones
		var $cerrar_plugin = $('#forma-etiquetas-window').find('#close');
		var $cancelar_plugin = $('#forma-etiquetas-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-etiquetas-window').find('#submit');
                
                var $accion = $('#forma-etiquetas-window').find('input[name=accion]');
                
                $accion.attr({'value' : "no_generando_xml_etiqueta"});
		
		$folio_automatico.attr('readonly',true);
		$folio_origen.attr('readonly',true);
                $descargar_pdf.attr('disabled','-1'); //deshabilitar
                $imprimir_pdf.attr('disabled','-1'); //deshabilitar
        
		$campo_id.attr({'value' : 0});
       
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				//jAlert("La Etiqueta fue dado de alta con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-etiquetas-overlay').fadeOut(remove);
				//refresh_table();
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-etiquetas-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					if( longitud.length > 1 ){
						$('#forma-etiquetas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
                //carga select con tipo de documento
		$select_tipo_origen.children().remove();
		var select_html = '';
		for(var i in arrayTiposOrigen){
			select_html += '<option value="' + i + '">' + arrayTiposOrigen[i] + '</option>';
		}
		$select_tipo_origen.append(select_html);
		
		
		//buscador de productos
		$busca_entrada.click(function(event){
			event.preventDefault();
			//alert("Este el el tipo de origene legido"+$select_tipo_origen.val());
			if($select_tipo_origen.val()!= 0){
			   $busca_entradas($select_tipo_origen.val());
			}else{
				jAlert("Elige in tipo de Origen para la  busqueda",'Atencion!!!');
			}
		});


		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-etiquetas-overlay').fadeOut(remove);
		});
		
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-etiquetas-overlay').fadeOut(remove);
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
			jConfirm('Realmente desea eliminar la Marca seleccionada', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La etiqueta fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La etiqueta no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formaEtiquetas';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_etiquetas();
			$('#forma-etiquetas-window').css({"margin-left": -350, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-etiquetas-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-etiquetas-window').find('input[name=identificador]');                
			
			
			var $folio_automatico = $('#forma-etiquetas-window').find('input[name=folio_automatico]');
			var $folio_origen = $('#forma-etiquetas-window').find('input[name=folio_or]');
			var $select_tipo_origen = $('#forma-etiquetas-window').find('select[name=tipo_origen]');
			var $busca_entrada = $('#forma-etiquetas-window').find('a[href*=busca_entradas]');
			var $descargar_pdf = $('#forma-etiquetas-window').find('#descargar_pdf');
			var $imprimir_pdf =  $('#forma-etiquetas-window').find('#imprimir_pdf');
			var $grid_entradas = $('#forma-etiquetas-window').find('#grid_entradas');
			var $cerrar_plugin = $('#forma-etiquetas-window').find('#close');
			var $cancelar_plugin = $('#forma-etiquetas-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-etiquetas-window').find('#submit').hide();
                        
                        var $accion = $('#forma-etiquetas-window').find('input[name=accion]');
                        
                        
                        
			
			$folio_automatico.attr('readonly',true);
			$folio_origen.attr('readonly',true);
			$busca_entrada.hide();
			
			
			if(accion_mode == 'edit'){
				//aqui es el post que envia los datos a getEtiquetas.json
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEtiquetas.json';
				$arreglo = {'id':id_to_show,
							'tipo_origen':0,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
							};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-etiquetas-overlay').fadeOut(remove);
						//jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-etiquetas-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-etiquetas-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					$campo_id.attr({'value' : entry['Etiquetas_header']['0']['id']});
					$folio_origen.attr({'value' : entry['Etiquetas_header']['0']['folio_origen']});
					$folio_automatico.attr({'value' : entry['Etiquetas_header']['0']['folio']});
					
					
					//carga select con tipo de origen
					$select_tipo_origen.children().remove();
					var select_html = '';
					for(var i in arrayTiposOrigen){
						if(parseInt(entry['Etiquetas_header']['0']['tipo_origen'])==parseInt(i)){
							select_html += '<option value="' + i + '" selected="yes">' + arrayTiposOrigen[i] + '</option>';
						}
					}
					$select_tipo_origen.append(select_html);
								
					//llamando al a funcion que carga el grid del primer pluguin 
					cargando_grid_lote($campo_id.val(),entry['Etiquetas_header']['0']['tipo_origen'],$grid_entradas);
				 },"json");//termina llamada json
				
				
                                
                                //imprimir Etiquetas
                               /*
                               $imprimir_pdf.click(function(event){
                                    event.preventDefault();
                                        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_imprime_Etiquetas/'+$campo_id.val()+'/'+$select_tipo_origen.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
                                        
                                        
                                });*/
                                
                                $imprimir_pdf.click(function(e){
					$accion.attr({'value' : "generando_xml_etiqueta"});
					//jConfirm('Confirmar impresion ?', 'Dialogo de Confirmacion', function(r) {
					// If they confirmed, manually trigger a form submission
					//	if (r) {
					//		$submit_actualizar.parents("FORM").submit();
					//	}else{
					//		$accion.attr({'value' : "edit"});
					//	}
					//});
					// Always return false here since we don't know what jConfirm is going to do
					//return false;
				});
                                
                                
				//descargar pdf de Impresion de Etiquetas
				$descargar_pdf.click(function(event){
					var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
					var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_etiquetas/'+$campo_id.val()+'/'+$select_tipo_origen.val()+'/'+iu+'/out.json';
					window.location.href=input_json;
				});
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-etiquetas-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-etiquetas-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
                                
				
			}
		}
	}
        
        
        cargando_grid_lote= function(id_etiqueta,tipo_origen,$grid_entradas){
            if(tipo_origen == 1){
                 //json para llenar el grid del primer pluguin                         
                 var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEtiquetas.json';
				$arreglo = {	'id':id_etiqueta,
                                                'tipo_origen':tipo_origen,
                                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					   }
                $.post(input_json,$arreglo,function(entry){
                    if(entry['Etiquetas_grid'] != null){
                        
                        $.each(entry['Etiquetas_grid'],function(entryIndex,entrads){
                            trr = '<tr>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                    trr += '<input type="hidden" name="id_lote"  value="'+ entrads['inv_lote_id'] +'">'; 
                                    trr += '<input type="hidden" name="id_producto"  value="'+ entrads['inv_prod_id'] +'">'; 
                                    trr += '<input type="hidden" name="etiqueta_detalle_id"  value="'+ entrads['etiqueta_detalle_id'] +'">'; 
                                    trr += '<INPUT TYPE="text" name="cantidad" value="'+ entrads['cantidad'] +'" style="width:70px;">';
                                
                                trr += '</td>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                   
                                    trr += '<input type="hidden" name="cantidad_produccion"  value="'+ entrads['cantidad_produccion'] +'">'; 
                                    trr += '<input type="hidden" name="tipo_de_producto_id"  value="'+ entrads['tipo_de_producto_id'] +'">'; 
                                    
                                    
                                    trr += '<select name="inv_etiqueta_medidas_id" style="width:70px;">';
                                         //aqui se carga el select 
                                        $.each(entry['MedidasEtiqueta'],function(entryIndex,etiqueta){
                                                if(etiqueta['id'] == entrads['inv_etiqueta_medidas_id']){
                                                        trr += '<option value="' + etiqueta['id'] + '"  selected="yes">' + etiqueta['titulo'] + '</option>';
                                                }else{
                                                        trr += '<option value="' + etiqueta['id'] + '"  >' + etiqueta['titulo'] + '</option>';
                                                }
                                        });
                                    trr += '</select>';
                                    
				trr += '</td>';
                                
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="230px">';
                                    trr += '<input type="hidden" name="inv_etiquetas_id"  value="'+ entrads['inv_etiquetas_id'] +'">'; 
                                    trr += '<INPUT TYPE="text" name="lote_interno" value="'+ entrads['lote_interno'] +'"  class="borde_oculto" readOnly="true"  style="width:230px;">';
                                trr += '</td>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                    trr += '<INPUT TYPE="text" name="codigo"  value="'+ entrads['codigo'] +'"  class="borde_oculto" readOnly="true"  style="width:70px;">';
                                trr += '</td>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200px">';
                                    trr += '<INPUT TYPE="text" name="sku"   value="'+ entrads['descripcion'] +'"  class="borde_oculto" readOnly="true"  style="width="200px";">';
                                trr += '</td>';
                            trr += '</tr>';
                            
                            $grid_entradas.append(trr);                 
                        });
                    }
                });//termina llamada json que trae los datos del grid (primer pluguin) de entradas.
            }//CARGANDO DATOS PARA EL GRID DEL PRIMER PLUGUIN (ENTRADAS)
            
            if(tipo_origen == 2){
                 //json para llenar el grid del primer pluguin                         
                 var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEtiquetas.json';
				$arreglo = {	'id':id_etiqueta,
                                                'tipo_origen':tipo_origen,
                                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					   }
                $.post(input_json,$arreglo,function(entry){
                    if(entry['Etiquetas_grid'] != null){
                        $.each(entry['Etiquetas_grid'],function(entryIndex,entrads){
                            trr = '<tr>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                            trr += '<input type="hidden" name="id_lote"  value="'+ entrads['inv_lote_id'] +'">'; 
                                            trr += '<input type="hidden" name="id_producto"  value="'+ entrads['inv_prod_id'] +'">'; 
                                            trr += '<input type="hidden" name="etiqueta_detalle_id"  value="'+ entrads['etiqueta_detalle_id'] +'">'; 
                                            trr += '<INPUT TYPE="text" name="cantidad" value="'+ entrads['cantidad'] +'" style="width:70px;">';
                                trr += '</td>';
                                
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                   trr += '<input type="hidden" name="inv_etiquetas_id"  value="'+ entrads['inv_etiquetas_id'] +'">'; 
                                    trr += '<input type="hidden" name="cantidad_produccion"  value="'+ entrads['cantidad_produccion'] +'">'; 
                                    trr += '<input type="hidden" name="tipo_de_producto_id"  value="'+ entrads['tipo_de_producto_id'] +'">'; 
                                    trr += '<select name="inv_etiqueta_medidas_id" style="width:70px;">';
                                         //aqui se carga el select 
                                        $.each(entry['MedidasEtiqueta'],function(entryIndex,etiqueta){
                                                if(etiqueta['id'] == entrads['inv_etiqueta_medidas_id']){
                                                        trr += '<option value="' + etiqueta['id'] + '"  selected="yes">' + etiqueta['titulo'] + '</option>';
                                                }else{
                                                        trr += '<option value="' + etiqueta['id'] + '"  >' + etiqueta['titulo'] + '</option>';
                                                }
                                        });
                                    trr += '</select>';
                                    
				trr += '</td>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="230px">';
                                            trr += '<INPUT TYPE="text" name="lote_interno" value="'+ entrads['lote_interno'] +'"  class="borde_oculto" readOnly="true"  style="width:230px;">';
                                trr += '</td>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                            trr += '<INPUT TYPE="text" name="codigo"  value="'+ entrads['codigo'] +'"  class="borde_oculto" readOnly="true"  style="width:70px;">';
                                trr += '</td>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200px">';
                                            trr += '<INPUT TYPE="text" name="sku"   value="'+ entrads['descripcion'] +'"  class="borde_oculto" readOnly="true"  style="width="200px";">';
                                trr += '</td>';
                            trr += '</tr>';
                            
                            $grid_entradas.append(trr);                 
                        });
                    }
                });//termina llamada json que trae los datos del grid para produccion
            }//CARGANDO LOS DATOS PARA EL GRID DELPRIMER PLUGIN (PRODUCCION)
            
            if(tipo_origen == 3){
                 //json para llenar el grid del primer pluguin                         
                 var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getEtiquetas.json';
				$arreglo = {	'id':id_etiqueta,
                                                'tipo_origen':tipo_origen,
                                                'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					   }
                $.post(input_json,$arreglo,function(entry){
                    //alert("Cargando los datos de requisicione para el grid del primer pluguin");
                    if(entry['Etiquetas_grid'] != null){
                        $.each(entry['Etiquetas_grid'],function(entryIndex,entrads){
                            trr = '<tr>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                        trr += '<input type="hidden" name="id_lote"  value="'+ entrads['inv_lote_id'] +'">'; 
                                        trr += '<input type="hidden" name="id_producto"  value="'+ entrads['inv_prod_id'] +'">'; 
                                        trr += '<input type="hidden" name="etiqueta_detalle_id"  value="'+ entrads['etiqueta_detalle_id'] +'">'; 
                                        trr += '<INPUT TYPE="text" name="cantidad" value="'+ entrads['cantidad'] +'" style="width:70px;">';
                                trr += '</td>';
                                
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                   trr += '<input type="hidden" name="inv_etiquetas_id"  value="'+ entrads['inv_etiquetas_id'] +'">'; 
                                    trr += '<input type="hidden" name="cantidad_produccion"  value="'+ entrads['cantidad_produccion'] +'">'; 
                                    trr += '<input type="hidden" name="tipo_de_producto_id"  value="'+ entrads['tipo_de_producto_id'] +'">'; 
                                    trr += '<select name="inv_etiqueta_medidas_id" style="width:70px;">';
                                         //aqui se carga el select 
                                        $.each(entry['MedidasEtiqueta'],function(entryIndex,etiqueta){
                                                if(etiqueta['id'] == entrads['inv_etiqueta_medidas_id']){
                                                        trr += '<option value="' + etiqueta['id'] + '"  selected="yes">' + etiqueta['titulo'] + '</option>';
                                                }else{
                                                        trr += '<option value="' + etiqueta['id'] + '"  >' + etiqueta['titulo'] + '</option>';
                                                }
                                        });
                                    trr += '</select>';
                                    
				trr += '</td>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="230px">';
                                        trr += '<INPUT TYPE="text" name="lote_interno" value="'+ entrads['lote_interno'] +'"  class="borde_oculto" readOnly="true"  style="width:230px;">';
                                trr += '</td>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="70px">';
                                        trr += '<INPUT TYPE="text" name="codigo"  value="'+ entrads['codigo'] +'"  class="borde_oculto" readOnly="true"  style="width:70px;">';
                                trr += '</td>';
                                trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="200px">';
                                        trr += '<INPUT TYPE="text" name="sku"   value="'+ entrads['descripcion'] +'"  class="borde_oculto" readOnly="true"  style="width="200px";">';
                                trr += '</td>';
                            trr += '</tr>';
                            
                            $grid_entradas.append(trr);                 
                        });
                    }
                });//termina llamada json que trae los datos del grid para produccion
            }//CARGANDO LOS DATOS PARA EL GRID DELPRIMER PLUGIN (MOSTRANDO LAS REQUISICIONES)
            
            
        }
        
        
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllEtiquetas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllEtiquetas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenable(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});
