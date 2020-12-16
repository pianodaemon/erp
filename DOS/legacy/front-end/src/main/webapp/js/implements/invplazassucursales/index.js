$(function() {
    var config =  {
					empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
					sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
                    tituloApp: 'Asignaci&oacute;n de Plazas a Sucursales' ,                 
                    contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),

                    userName : $('#lienzo_recalculable').find('input[name=user]').val(),
                    ui : $('#lienzo_recalculable').find('input[name=iu]').val(),

                    getUrlForGetAndPost : function(){
                        var url = document.location.protocol + '//' + document.location.host + this.getController();
                        return url;
                    },
					getEmp: function(){
						return this.empresa;
					},
					
					getSuc: function(){
						return this.sucursal;
					},

                    getUserName: function(){
                        return this.userName;
                    },
                    getUi: function(){
                        return this.ui;
                    },
                    getTituloApp: function(){
                        return this.tituloApp;
                    },
                    getController: function(){
                        return this.contextpath + "/controllers/invplazassucursales";
                        //  return this.controller;
                    }                
    };   
    
	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
    $('#barra_acciones').hide();

    //aqui va el titulo del catalogo
    $('#barra_titulo').find('#td_titulo').append(config.getTituloApp());

    //barra para el buscador 
    $('#barra_buscador').hide();

    var $select_sucursal = $('#lienzo_recalculable').find('select[name=sucursales]');
    var $select_plazaNoAsignadas= $('#lienzo_recalculable').find('select[name=plazaNoAsignadas]');
    var $select_PlazasAsignadas=$('#lienzo_recalculable').find('select[name=PlazasAsignadas]');
    
    //para mostrar las sucursales al cargar la pagina
    var $carga_select_sucursal = function(){
            var arreglo_parametros = { iu:config.getUi()};
            var restful_json_service = config.getUrlForGetAndPost() + '/getSucursales.json'
            $.post(restful_json_service,arreglo_parametros,function(entry){ 
               $select_sucursal.children().remove();
               var sucursal_hmtl = '';
               sucursal_hmtl += '<option value="0"  >---Elige una Sucursal</option>';
               $.each(entry['Sucursal'],function(entryIndex,sucursal){
                  sucursal_hmtl += '<option value="' + sucursal['id'] + '"  >' + sucursal['sucursal'] + '</option>';
                });
                $select_sucursal.append(sucursal_hmtl);                                                    //carga select de presentaciones disponibles
           });   
    };
    $carga_select_sucursal(); 
    
    //para mostrar las plazas no asignadas al cargar la pagina
    var $carga_select_plazaNoAsignadas = function(){
            var arreglo_parametros = { iu:config.getUi()};
            var restful_json_service = config.getUrlForGetAndPost() + '/getSucursales.json'
            $.post(restful_json_service,arreglo_parametros,function(entry){ 
               $select_plazaNoAsignadas.children().remove();
               var sucursal_hmtl = '';              
               $.each(entry['Plaza'],function(entryIndex,sucursal){
                  sucursal_hmtl += '<option value="' + sucursal['id'] + '"  >' + sucursal['plaza'] + '</option>';
                });
                $select_plazaNoAsignadas.append(sucursal_hmtl);                                                   
           });   
    };
    $carga_select_plazaNoAsignadas(); 
    
    //para cargar las plazas asignadas y no asignadas segun la sucursal seleccionada.
    $select_sucursal.change(function(){         
        var arreglo_parametros = { iu:config.getUi(),id_sucursal:$select_sucursal.val()};         
            var restful_json_service = config.getUrlForGetAndPost() + '/getFiltroPlazas.json'           
            $.post(restful_json_service,arreglo_parametros,function(entry){ 
               $select_PlazasAsignadas.children().remove();
               var sucursal_hmtl = '';              
               $.each(entry['Asignadas'],function(entryIndex,sucursal){
                  sucursal_hmtl += '<option value="' + sucursal['id'] + '"  >' + sucursal['plaza'] + '</option>';
                });
                $select_PlazasAsignadas.append(sucursal_hmtl);
                $select_plazaNoAsignadas.children().remove();
                var sucursal_hmtl = '';              
               $.each(entry['NoAsignadas'],function(entryIndex,sucursal){
                  sucursal_hmtl += '<option value="' + sucursal['id'] + '"  >' + sucursal['plaza'] + '</option>';
                });
                $select_plazaNoAsignadas.append(sucursal_hmtl);
       }); 
    })     
    
    var $agregar_plaza = $('#lienzo_recalculable').find('a[href*=agregar_plaza]');  
    var $campo_pres_on = $('#lienzo_recalculable').find('input[name=pres_on]');
    var $remover_plaza = $('#lienzo_recalculable').find('a[href*=remover_plaza]'); 
      
    //para agregar plazas no asignadas al campo de plazas asignadas
    $agregar_plaza.click(function(event){              
			event.preventDefault();
			var logica = false;
			var primero=0;
			logica = !$select_plazaNoAsignadas.find('option:selected').remove().appendTo( $select_PlazasAsignadas);
			var valor_campo = "";
			var ahora_seleccionados = $select_PlazasAsignadas.find('option').get();
			$.each( ahora_seleccionados , function(indice , seleccionado){
				if(primero==0){
					valor_campo += seleccionado.value;
					primero=1;
				}else{
					valor_campo += "," + seleccionado.value;
				}
			});			
			$campo_pres_on.attr({'value' : valor_campo });
			return logica; 
    });
    
    //para mover las plazas asignadas al campo de plazas no asignadas
    $remover_plaza.click(function(event){
            event.preventDefault();
            var logica = false;
            var primero=0;
            logica = !$select_PlazasAsignadas.find('option:selected').remove().appendTo($select_plazaNoAsignadas);
            var valor_campo = "";
            var ahora_seleccionados = $select_PlazasAsignadas.find('option').get();
            $.each( ahora_seleccionados , function(indice , seleccionado){
                    if(primero==0){
                            valor_campo += seleccionado.value;
                            primero=1;
                    }else{
                            valor_campo += "," + seleccionado.value;
                    }
            });
            $campo_pres_on.attr({'value' : valor_campo }); 
            return logica;
    });
    
    //para actualizar la base de datos en la tabla gral_suc_pza
    var $submit_actualizar = $('#lienzo_recalculable').find('#submit');    
    $submit_actualizar.bind('click',function(){  
        var primero=0;
        var valor_campo = "";
        var ahora_seleccionados = $select_PlazasAsignadas.find('option').get();
        
        $.each( ahora_seleccionados , function(indice , seleccionado){
                if(primero==0){
                        valor_campo += seleccionado.value; 
                        primero=1;
                }else{
                        valor_campo += "," + seleccionado.value;
                }
        });
       
        if($select_sucursal.val()!=0){
            var arreglo_parametros = {id_sucursal:$select_sucursal.val(),Agregadas:valor_campo};         
     
            var restful_json_service = config.getUrlForGetAndPost() + '/getActualizar.json'             
            $.post(restful_json_service,arreglo_parametros,function(entry){             
            });             
            jAlert("Se ha Actualizado la Sucursal:     "+$('#my-select option:selected').html(),"Atencion!!!");
        }
        else{
            jAlert("Elige una sucursal","Atencion!!!");
        }        
    })                                
});
