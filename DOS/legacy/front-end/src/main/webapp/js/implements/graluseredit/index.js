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
	var controller = $contextpath.val()+"/controllers/graluseredit";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    //$('#barra_acciones').find('.table_acciones').css({'display':'block'});
	//var $new_graluseredit = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	//var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	$('#barra_acciones').hide();
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Cambiar Contrase&ntilde;a');
	
	//barra para el buscador 
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
        
    
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "iu" + signo_separador + $('#lienzo_recalculable').find('input[name=iu]').val() + "|";
		return valor_retorno;
	};
	
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	
	$tabs_li_funxionalidad = function(){
		
		$('#forma-graluseredit-window').find('#submit').mouseover(function(){
			$('#forma-graluseredit-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
		});
		$('#forma-graluseredit-window').find('#submit').mouseout(function(){
			$('#forma-graluseredit-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
		});
		$('#forma-graluseredit-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-graluseredit-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-graluseredit-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-graluseredit-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-graluseredit-window').find('#close').mouseover(function(){
			$('#forma-graluseredit-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-graluseredit-window').find('#close').mouseout(function(){
			$('#forma-graluseredit-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});
		
		
		$('#forma-graluseredit-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-graluseredit-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-graluseredit-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-graluseredit-window').find("ul.pestanas li").click(function() {
			$('#forma-graluseredit-window').find(".contenidoPes").hide();
			$('#forma-graluseredit-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-graluseredit-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	

        
	
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
							jAlert("La Marca fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La marca no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
                    
		}else{
			//aqui  entra para editar un registro
			var form_to_show = 'formagraluseredit';
			
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_graluseredit();
               
			$('#forma-graluseredit-window').css({"margin-left": -300, 	"margin-top": -200});
			
			$forma_selected.prependTo('#forma-graluseredit-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			var $campo_id = $('#forma-graluseredit-window').find('input[name=identificador]');                
			var $user = $('#forma-graluseredit-window').find('input[name=user]');                
			var $pass_ant = $('#forma-graluseredit-window').find('input[name=pass_ant]');                
			var $pass_new = $('#forma-graluseredit-window').find('input[name=pass_new]');                
			var $pass_new2 = $('#forma-graluseredit-window').find('input[name=pass_new2]');                
			
			var $cerrar_plugin = $('#forma-graluseredit-window').find('#close');
			var $cancelar_plugin = $('#forma-graluseredit-window').find('#boton_cancelar');
			var $submit_actualizar = $('#forma-graluseredit-window').find('#submit');
			
			$user.attr('readonly',true);
			$user.css({'background' : '#dddddd'});
			
			$campo_id.val(id_to_show);
			
			
			if(accion_mode == 'edit'){  
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getUser.json';
				$arreglo = {'id':id_to_show,
							'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
				};
				
				var respuestaProcesada = function(data){
					if ( data['success'] == 'true' ){
						var remove = function() {$(this).remove();};
						$('#forma-graluseredit-overlay').fadeOut(remove);
						jAlert("Los datos se han actualizado.", 'Atencion!');
						$get_datos_grid();
					}
					else{
						// Desaparece todas las interrogaciones si es que existen
						$('#forma-graluseredit-window').find('div.interrogacion').css({'display':'none'});
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');
							if( longitud.length > 1 ){
								$('#forma-graluseredit-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
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
					
					$user.val(entry['Data']['0']['usern']);
					
					$pass_ant.focus();
				},"json");//termina llamada json
				
				
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-graluseredit-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-graluseredit-overlay').fadeOut(remove);
					$buscar.trigger('click');
				});
			}
		}
	}
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllUsers.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':10,'pag_start':1,'display_pag':10,'input_json':'/'+controller+'/getAllUsers.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
        
        $.post(input_json,$arreglo,function(data){
            
            //pinta_grid
            $.fn.tablaOrdenableEdit(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaCC00_for_datagrid00);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }

    $get_datos_grid();
    
    
});



