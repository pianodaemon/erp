$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
    //arreglo para select status
    var arrayStatus = {
				1:"Activo", 
				2:"Inactivo"
			};
			
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/cotizacionsaludodesp";
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Edici&oacute;n de Saludo y Despedida');
	
    $('#barra_acciones').hide();
    $('#barra_buscador').hide();
    
    
	var $cadena_busqueda = "";
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	//$cadena_busqueda = cadena;
	
	$tabs_li_funxionalidad = function(){
		$('#forma-cotizacionsaludodesp-window').find('#submit').mouseover(function(){
			$('#forma-cotizacionsaludodesp-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-cotizacionsaludodesp-window').find('#submit').mouseout(function(){
			$('#forma-cotizacionsaludodesp-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-cotizacionsaludodesp-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-cotizacionsaludodesp-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-cotizacionsaludodesp-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-cotizacionsaludodesp-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-cotizacionsaludodesp-window').find('#close').mouseover(function(){
			$('#forma-cotizacionsaludodesp-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-cotizacionsaludodesp-window').find('#close').mouseout(function(){
			$('#forma-cotizacionsaludodesp-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-cotizacionsaludodesp-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-cotizacionsaludodesp-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-cotizacionsaludodesp-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-cotizacionsaludodesp-window').find("ul.pestanas li").click(function() {
			$('#forma-cotizacionsaludodesp-window').find(".contenidoPes").hide();
			$('#forma-cotizacionsaludodesp-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-cotizacionsaludodesp-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});

	}
	
	//Eventos del grid edicion,borrar!
	var carga_formaCC00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui  entra para editar un registro
		var form_to_show = 'formacotizacionsaludodesp';
		
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		
		$(this).modalPanel_cotizacionsaludodesp();
					
		$('#forma-cotizacionsaludodesp-window').css({"margin-left": -420, 	"margin-top": -200});
		$forma_selected.prependTo('#forma-cotizacionsaludodesp-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		$tabs_li_funxionalidad();
		
		//campos de la vista
		var $identificador = $('#forma-cotizacionsaludodesp-window').find('input[name=identificador]'); 
		var $tipo= $('#forma-cotizacionsaludodesp-window').find('input[name=tipo]');
		var $titulo = $('#forma-cotizacionsaludodesp-window').find('textarea[name=titulo]');
		var $select_status = $('#forma-cotizacionsaludodesp-window').find('select[name=select_status]');
		
		//alert($titulo.val());
		//botones                        
		var $cerrar_plugin = $('#forma-cotizacionsaludodesp-window').find('#close');
		var $cancelar_plugin = $('#forma-cotizacionsaludodesp-window').find('#boton_cancelar');
		var $submit_actualizar = $('#forma-cotizacionsaludodesp-window').find('#submit');
		
		if(accion_mode == 'edit'){
			
			//aqui es el post que envia los datos a getPuestos.json
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getSaludoDespedida.json';
			$arreglo = {
						'id':id_to_show,
						'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
						};
			
			var respuestaProcesada = function(data){
				if ( data['success'] == 'true' ){
					var remove = function() {$(this).remove();};
					$('#forma-cotizacionsaludodesp-overlay').fadeOut(remove);
					jAlert("Los datos se han actualizado.", 'Atencion!');
					$get_datos_grid();
				}else{
					// Desaparece todas las interrogaciones si es que existen
					$('#forma-cotizacionsaludodesp-window').find('div.interrogacion').css({'display':'none'});
					
					var valor = data['success'].split('___');
					//muestra las interrogaciones
					for (var element in valor){
						tmp = data['success'].split('___')[element];
						longitud = tmp.split(':');
						if( longitud.length > 1 ){
							$('#forma-cotizacionsaludodesp-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
				$('#forma-cotizacionsaludodesp-window').find('#titulo_pestana').text(entry['Datos']['0']['tipo']); 
				
				//carga select de Status
				$select_status.children().remove();
				var status_html = '';
				for(var i in arrayStatus){
					if(parseInt(i)==parseInt(entry['Datos']['0']['status'])){
						status_html += '<option value="' + i + '" selected="yes">' + arrayStatus[i] + '</option>';
					}else{
						status_html += '<option value="' + i + '" >' + arrayStatus[i] + '</option>';
					}
				}
				$select_status.append(status_html);
				
				// aqui van los campos de editar
				$identificador.attr({'value' : entry['Datos']['0']['id']});
				$tipo.attr({'value':entry['Datos']['0']['tipo']});
				$titulo.text(entry['Datos']['0']['titulo']);
			},"json");//termina llamada json
			
			
			//Ligamos el boton cancelar al evento click para eliminar la forma
			$cancelar_plugin.bind('click',function(){
				var remove = function() {$(this).remove();};
				$('#forma-cotizacionsaludodesp-overlay').fadeOut(remove);
			});
			
			$cerrar_plugin.bind('click',function(){
				var remove = function() {$(this).remove();};
				$('#forma-cotizacionsaludodesp-overlay').fadeOut(remove);
				$buscar.trigger('click');
			});
			
			$titulo.focus();
		}
		
	}
	
	
   $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllDatos.json';
		
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllDatos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
            //pinta_grid
            $.fn.tablaOrdenableEdit(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
});
