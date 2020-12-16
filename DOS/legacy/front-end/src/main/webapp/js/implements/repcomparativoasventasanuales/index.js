$(function() {
     
        var config =  {
                empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
                sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
                tituloApp: 'Reporte Comparativo de Ventas Anuales por Cliente' ,                 
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
                        return this.contextpath + "/controllers/repcomparativoasventasanuales";
                        //  return this.controller;
                }
        };
	
		var array_meses = {
					0:"- Seleccionar -",  
					1:"Ene",  
					2:"Feb", 
					3:"Mar", 
					4:"Abr", 
					5:"May", 
					6:"Jun", 
					7:"Jul", 
					8:"Ago", 
					9:"Sep", 
					10:"Oct", 
					11:"Nov", 
					12:"Dic"
				};
				
	
        $('#header').find('#header1').find('span.emp').text(config.getEmp());
        $('#header').find('#header1').find('span.suc').text(config.getSuc());
        $('#header').find('#header1').find('span.username').text(config.getUserName());
        //aqui va el titulo del catalogo
        $('#barra_titulo').find('#td_titulo').append(config.getTituloApp());

        $('#barra_acciones').hide();
        //barra para el buscador 
        $('#barra_buscador').hide();
		

		var $select_ano= $('#lienzo_recalculable').find('div.repcomparativoventasanual').find('table#fechas tr td').find('select[name=select_ano]');
		var $select_anofin= $('#lienzo_recalculable').find('div.repcomparativoventasanual').find('table#fechas tr td').find('select[name=select_anofin]');
        //var $genera_reporte_estadistico = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_PDF]');
        var $div_reporte= $('#lienzo_recalculable').find('#div_reporte');
        var $Nombre_Cliente= $('#lienzo_recalculable').find('div.repcomparativoventasanual').find('table#fechas tr td').find('input[name=razon_cli]');
        var $Buscar_clientes= $('#lienzo_recalculable').find('div.repcomparativoventasanual').find('table#fechas tr td').find('a[href*=busca_cliente]');
        var $id_cliente= $('#lienzo_recalculable').find('input[name=id_cliente]');
        var $busqueda_reporte_estadistico= $('#lienzo_recalculable').find('div.repcomparativoventasanual').find('table#fechas tr td').find('input[value$=Buscar]');
        
      

	    $Nombre_Cliente.attr({'readOnly':true});
	    
	    $Buscar_clientes.click(function(event){
                    event.preventDefault();
                    busca_clientes();

          });
          
          busca_clientes=function(){
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
                    html+='<option value="1">No. de control</option>';
                    html+='<option value="2">RFC</option>';
                    html+='<option value="3">Razon social</option>';
                    html+='<option value="4">CURP</option>';
                    html+='<option value="5">Alias</option>';
                    $select_filtro_por.append(html);



                    //click buscar clientes
                    $busca_cliente_modalbox.click(function(event){
                              //var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_clientes.json';
							var restful_json_service = config.getUrlForGetAndPost()+'/get_buscador_clientes.json';
							var  arreglo_parametros = {'cadena':$cadena_buscar.val(),'filtro':$select_filtro_por.val(),  'iu': $('#lienzo_recalculable').find('input[name=iu]').val()}

                              var trr = '';
                              $tabla_resultados.children().remove();
                              //$.post(input_json,$arreglo,function(entry){
                              $.post(restful_json_service,arreglo_parametros,function(entry){
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
                                             //$('#forma-carteras-window').find('input[name=identificador_cliente]').val($(this).find('#idclient').val());
                                             //$('#forma-carteras-window').find('input[name=rfccliente]').val($(this).find('span.rfc').html());
                                             $('#forma-carteras-window').find('input[name=cliente]').val($(this).find('span.razon').html());

                                        $('#forma-carteras-window').find('select[name=tipo_mov]').removeAttr('disabled');//habilitar select

                                             $Nombre_Cliente.val($(this).find('span.razon').html());
                                             $id_cliente.val($(this).find('#idclient').val());
                                             //elimina la ventana de busqueda
                                             var remove = function() {$(this).remove();};
                                             $('#forma-buscacliente-overlay').fadeOut(remove);
                                             //asignar el enfoque al campo sku del producto
                                        });

                              });
                    });//termina llamada json

                    $cancelar_plugin_busca_cliente.click(function(event){
                              var remove = function() {$(this).remove();};
                              $('#forma-buscacliente-overlay').fadeOut(remove);
                    });
          }

	
		


		var arreglo_parametros = { 
			iu:config.getUi()
		};

		var restful_json_service = config.getUrlForGetAndPost() + '/getDatos.json';
		$.post(restful_json_service,arreglo_parametros,function(entry){
			//carga select de años
			$select_ano.children().remove();
			var html_anio = '';
			$.each(entry['Anios'],function(entryIndex,anio_in){
				if(parseInt(anio_in['valor']) == parseInt(entry['Dato'][0]['anioActual']) ){
					html_anio += '<option value="' + anio_in['valor'] + '" selected="yes">' + anio_in['valor'] + '</option>';
				}else{
					html_anio += '<option value="' + anio_in['valor'] + '"  >' + anio_in['valor'] + '</option>';
				}
			});
			$select_ano.append(html_anio);
			
			var valor_aniodos = $select_ano.val();
			
			var cont2=0;
			//carga select de años
			$select_anofin.children().remove();
			var html_anio2 = '';
			$.each(entry['Anios'],function(entryIndex,anio_fin){					
				if(parseInt(anio_fin['valor']) > parseInt(valor_aniodos) ){
					if((parseInt(valor_aniodos) + 1)==parseInt(anio_fin['valor'])){
						html_anio2 += '<option value="' + anio_fin['valor'] + '" selected="yes">' + anio_fin['valor'] + '</option>';
					}else{
						html_anio2 += '<option value="' + anio_fin['valor'] + '"  >' + anio_fin['valor'] + '</option>';
					}
					cont2++;
				}else{
					if(parseInt(cont2)<=0){
						if(parseInt(anio_fin['valor']) == parseInt(valor_aniodos) ){
							html_anio2 += '<option value="0"  >[----]</option>';
						}
					}
				}
			});
			$select_anofin.append(html_anio2);
		
			
			
			
			
			$select_ano.change(function(){
				var valor_anio = $(this).val();
				
				var cont=0;
				//carga select de años
				$select_anofin.children().remove();
				var html_anio = '';
				$.each(entry['Anios'],function(entryIndex,anio_fin){					
					if(parseInt(anio_fin['valor']) > parseInt(valor_anio) ){
						if((parseInt(valor_anio) + 1)==parseInt(anio_fin['valor'])){
							html_anio += '<option value="' + anio_fin['valor'] + '" selected="yes">' + anio_fin['valor'] + '</option>';
						}else{
							html_anio += '<option value="' + anio_fin['valor'] + '"  >' + anio_fin['valor'] + '</option>';
						}
						cont++;
					}else{
						if(parseInt(cont)<=0){
							if(parseInt(anio_fin['valor']) == parseInt(valor_anio) ){
								html_anio += '<option value="0"  >[----]</option>';
							}
						}
					}
				});
				$select_anofin.append(html_anio);
			});
	
		});
		
	
        
        //Accion del Boton Buscar
        $busqueda_reporte_estadistico.click(function(event){
            event.preventDefault();
            $div_reporte.children().remove();
                    var arreglo_parametros = {	
                        //mes_in:$mes_inicial.val(),
                        //mes_fin:$mes_final.val(),
                        anio_in:$select_ano.val(),
                        anio_fin:$select_anofin.val(),
                        razon_cli:$Nombre_Cliente.val(),
                        //cliente : $id_cliente.val(),
                        iu:config.getUi()
                    };

                    var restful_json_service = config.getUrlForGetAndPost() + '/getComparativo.json'
                    var cliente="";
                    
           
			
			 if(parseInt($select_anofin.val())<=0){
				// jAlert("El A&ntilde;o Inicial debe ser menor al  A&ntilde;o Final.",'Atencion!');
				  
				//if (parseInt($select_anofin.val())<parseInt($select_ano.val())) {
					 jAlert("El A&ntilde;o Inicial debe ser menor al  A&ntilde;o Final",'Atencion!');
					
				//}

				  }else{
					
                   // if(parseInt($select_ano.val())!=0 && parseInt($select_anofin.val())!=0 ){
                        $.post(restful_json_service,arreglo_parametros,function(entry){
                                var body_tabla = entry['Comparativo'];
                                var footer_tabla = entry['Totales'];
                                var header_tabla = {
                                        cliente	:'Cliente',
                                        enero   :'Enero',
                                        febrero :'Febrero',
                                        marzo   :'Marzo',
                                        abril   :'Abril',
                                        mayo    :'Mayo',
                                        junio   :'Junio',
                                        julio   :'Julio',
                                        agosto  :'Agosto',
                                        septiem :'Septiembre',
                                        octubre :'Octubre',
                                        noviembr:'Noviembre',
                                        diciem  :'Diciembre',
                                        total   :'Total&nbsp;Anterior',
                                        totaldos   :'Total&nbsp;Actual'
                                };



                                var TPventa_neta=0.0;
                                var Sumatoriaventa_neta = 0.0;
                                var sumatotoriaporciento = 0.0;
                                var html_reporte = '<table id="ventas">';
                                var porcentaje = 0.0;

                                var numero_control=0.0; 
                                var cliente=0.0; 
                                var moneda="$"; 
                                var venta_neta=0.0; 
                                var porciento=0.0;
                                var tmp= 0;
                                var html_footer="";
                                
                                
                                html_reporte +='<thead> <tr>';
                                var anio_ini=$select_ano.val();
                                var anio_fin=$select_anofin.val();
                                
                                html_reporte +='<td width="120" align="left">Cliente</td>'; 
                                
                               var primerColumna1=0;
							   for(var mes=1; mes<=12; mes++){
									var nombre_mes=array_meses[mes];
									if(parseInt(primerColumna1)>0){
										html_reporte +='<td width="10">&nbsp;</td>';
									}
									html_reporte +='<td width="100" align="left">'+nombre_mes+""+'&nbsp;'+anio_ini+'</td>';
									html_reporte +='<td width="100" align="left">'+nombre_mes+""+'&nbsp;'+anio_fin+'</td>';
									primerColumna1++;
								}
								html_reporte +='<td width="180" align="left">Anual&nbsp'+anio_ini+'</td>'; 
								html_reporte +='<td width="180" align="left">Anual&nbsp'+anio_fin+'</td>'; 
                                html_reporte +='</tr></thead>';


                                var totalmes=0.0;
                                var totalano=0.0;
                                var totalmes2=0.0;
                                var totalmes3=0.0;
                                var totalmes4=0.0;
                                var totalmes5=0.0;
                                var totalmes6=0.0;
                                var totalmes7=0.0;
                                var totalmes8=0.0;
                                var totalmes9=0.0;
                                var totalmes10=0.0;
                                var totalmes11=0.0;
                                var totalmes12=0.0;
                                
                                var totalano2=0.0;
                                var totalmes1=0.0;
                                var totalmes22=0.0;
                                var totalmes33=0.0;
                                var totalmes44=0.0;
                                var totalmes55=0.0;
                                var totalmes66=0.0;
                                var totalmes77=0.0;
                                var totalmes88=0.0;
                                var totalmes99=0.0;
                                var totalmes100=0.0;
                                var totalmes110=0.0;
                                var totalmes120=0.0;
                                var primerColumna2=0;
                                for(var i=0; i<body_tabla.length; i++){
									totalano=parseFloat(totalano)+parseFloat(body_tabla[i]["aniouno"]);
									totalano2=parseFloat(totalano2)+parseFloat(body_tabla[i]["aniodos"]);
									html_reporte +='<tr>';
									html_reporte +='<td width="120" align="left">'+body_tabla[i]["razon_social"]+'</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["enero"]).toFixed(2))+'</td>'; 
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["enero2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["febrero"]).toFixed(2))+'</td>'; 
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["febrero2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["marzo"]).toFixed(2))+'</td>'; 
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["marzo2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["abril"]).toFixed(2))+'</td>'; 
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["abril2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["mayo"]).toFixed(2))+'</td>'; 
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["mayo2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["junio"]).toFixed(2))+'</td>'; 
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["junio2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["julio"]).toFixed(2))+'</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["julio2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["agosto"]).toFixed(2))+'</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["agosto2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["septiembre"]).toFixed(2))+'</td>'; 
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["septiembre2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["octubre"]).toFixed(2))+'</td>'; 
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["octubre2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["noviembre"]).toFixed(2))+'</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["noviembre2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["diciembre"]).toFixed(2))+'</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["diciembre2"]).toFixed(2))+'</td>';
									html_reporte +='<td width="10">&nbsp;</td>';
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["aniouno"]).toFixed(2))+'</td>'; 
									html_reporte +='<td width="100" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["aniodos"]).toFixed(2))+'</td>';
									html_reporte +='</tr>';

									totalmes=parseFloat(totalmes)+parseFloat(body_tabla[i]["enero"]);
									totalmes2=parseFloat(totalmes2)+parseFloat(body_tabla[i]["febrero"]);
									totalmes3=parseFloat(totalmes3)+parseFloat(body_tabla[i]["marzo"]);
									totalmes4=parseFloat(totalmes4)+parseFloat(body_tabla[i]["abril"]);
									totalmes5=parseFloat(totalmes5)+parseFloat(body_tabla[i]["mayo"]);
									totalmes6=parseFloat(totalmes6)+parseFloat(body_tabla[i]["junio"]);
									totalmes7=parseFloat(totalmes7)+parseFloat(body_tabla[i]["julio"]);
									totalmes8=parseFloat(totalmes8)+parseFloat(body_tabla[i]["agosto"]);
									totalmes9=parseFloat(totalmes9)+parseFloat(body_tabla[i]["septiembre"]);
									totalmes10=parseFloat(totalmes10)+parseFloat(body_tabla[i]["octubre"]);
									totalmes11=parseFloat(totalmes11)+parseFloat(body_tabla[i]["noviembre"]);
									totalmes12=parseFloat(totalmes12)+parseFloat(body_tabla[i]["diciembre"]);
									
									totalmes1=parseFloat(totalmes1)+parseFloat(body_tabla[i]["enero2"]);
									totalmes22=parseFloat(totalmes22)+parseFloat(body_tabla[i]["febrero2"]);
									totalmes33=parseFloat(totalmes33)+parseFloat(body_tabla[i]["marzo2"]);
									totalmes44=parseFloat(totalmes44)+parseFloat(body_tabla[i]["abril2"]);
									totalmes55=parseFloat(totalmes55)+parseFloat(body_tabla[i]["mayo2"]);
									totalmes66=parseFloat(totalmes66)+parseFloat(body_tabla[i]["junio2"]);
									totalmes77=parseFloat(totalmes77)+parseFloat(body_tabla[i]["julio2"]);
									totalmes88=parseFloat(totalmes88)+parseFloat(body_tabla[i]["agosto2"]);
									totalmes99=parseFloat(totalmes99)+parseFloat(body_tabla[i]["septiembre2"]);
									totalmes100=parseFloat(totalmes100)+parseFloat(body_tabla[i]["octubre2"]);
									totalmes110=parseFloat(totalmes110)+parseFloat(body_tabla[i]["noviembre2"]);
									totalmes120=parseFloat(totalmes120)+parseFloat(body_tabla[i]["diciembre2"]);
                                }
								
                                html_reporte +='<tfoot>';
									/*sumando los meses**/
                                    html_reporte +='<tr>';
                                        html_reporte +='<td width="120" align="right">Total Mensual</td>'
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes).toFixed(2))+'</td>'; 
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes1).toFixed(2))+'</td>';
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes2).toFixed(2))+'</td>'; 
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes22).toFixed(2))+'</td>';
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes3).toFixed(2))+'</td>'; 
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes33).toFixed(2))+'</td>';
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes4).toFixed(2))+'</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes44).toFixed(2))+'</td>';
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes5).toFixed(2))+'</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes55).toFixed(2))+'</td>';
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes6).toFixed(2))+'</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes66).toFixed(2))+'</td>'; 
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes7).toFixed(2))+'</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes77).toFixed(2))+'</td>';
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes8).toFixed(2))+'</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes88).toFixed(2))+'</td>';
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes9).toFixed(2))+'</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes99).toFixed(2))+'</td>'; 
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes10).toFixed(2))+'</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes100).toFixed(2))+'</td>'; 
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes11).toFixed(2))+'</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes110).toFixed(2))+'</td>';
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes12).toFixed(2))+'</td>';
                                        html_reporte +='<td width="100" align="right">$'+$(this).agregar_comas(parseFloat(totalmes120).toFixed(2))+'</td>';
                                        html_reporte +='<td width="10">&nbsp;</td>';
                                        html_reporte +='<td width="180" align="right">'+$(this).agregar_comas(parseFloat(totalano).toFixed(2))+'</td>';
                                        html_reporte +='<td width="180" align="right">'+$(this).agregar_comas(parseFloat(totalano2).toFixed(2))+'</td>';
                                    html_reporte +='</tr>';
                                html_footer +='</tfoot>';
                                
                                html_reporte += '</table>';
                                
                                $div_reporte.append(html_reporte); 
                                var height2 = $('#cuerpo').css('height');
                                var alto = parseInt(height2)-300;
                                var pix_alto=alto+'px';
                                $('#ventas').tableScroll({height:parseInt(pix_alto)});
                                
								var width2 = $('#cuerpo').css('width');
                                var ancho = parseInt(width2);
								var pix_ancho=ancho+'px';
								//alert(pix_ancho);
								$('#div_reporte').css('width:'+pix_ancho+'px');
                              
                        });
                    
					}
                });	
});       

