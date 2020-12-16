$(function() {
     
        var config =  {
                empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
                sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
                tituloApp: 'Reporte de Proyectos' ,                 
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
                        return this.contextpath + "/controllers/crmreporteproyectos";
                        //  return this.controller;
                }
        };
	
				
				var array_meses2 = {
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
		
		var $select_ano = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_ano]');
        var $genera_reporte_estadistico = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Generar_EXCEL]');
        var $busqueda_reporte_estadistico= $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
        var $div_reporte= $('#lienzo_recalculable').find('#div_reporte');


		





		var arreglo_parametros = { 
			iu:config.getUi()
		};

		var restful_json_service = config.getUrlForGetAndPost() + '/getDatos.json';
		$.post(restful_json_service,arreglo_parametros,function(entry){
			//carga select de años
			$select_ano.children().remove();
			var html_anio = '';
			$.each(entry['Anios'],function(entryIndex,anio){
				if(parseInt(anio['valor']) == parseInt(entry['Dato'][0]['anioActual']) ){
					html_anio += '<option value="' + anio['valor'] + '" selected="yes">' + anio['valor'] + '</option>';
				}else{
					html_anio += '<option value="' + anio['valor'] + '"  >' + anio['valor'] + '</option>';
				}
			});
			$select_ano.append(html_anio);
		});





        //click generar reporte  Ventas Anuales por Cliente
        $genera_reporte_estadistico.click(function(event){
			event.preventDefault();
			if(parseInt($select_ano.val())!=0){
				var busqueda = $select_ano.val();
				var input_json = config.getUrlForGetAndPost() + '/get_genera_reporte_ventasanualesclientes/'+busqueda+'/'+config.getUi()+'/out.json';
				window.location.href=input_json;
			}else{
				jAlert("Es Necesario seleccionar el Año.",'Atencion!');
			}
        });
		

         //Accion del Boton Buscar
        $busqueda_reporte_estadistico.click(function(event){
            event.preventDefault();
            $div_reporte.children().remove();
                    var arreglo_parametros = {	
                        anio:$select_ano.val(),
                        iu:config.getUi()
                    };

                    var restful_json_service = config.getUrlForGetAndPost() + '/getVentasAnualesClientes.json'
                    var cliente="";
                    if(parseInt($select_ano.val())!=0){
                        $.post(restful_json_service,arreglo_parametros,function(entry){
                                var body_tabla = entry['VentasAnualesClientes'];
                           
                                var TPventa_neta=0.0;
                                var Sumatoriaventa_neta = 0.0;
                                var sumatotoriaporciento = 0.0;
                                var html_reporte = '<table class="table_main" width="4900">';
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
                                //var anio_fin=$select_anofin.val();
                                
                                 html_reporte +='<td class="grid_head" width="220">Clientes</td>'; 
                                //html_reporte +='<td width="120" align="left">Cliente</td>'; 
                                
                               var primerColumna1=0;
							   for(var mes=1; mes<=12; mes++){
									var nombre_mes=array_meses2[mes];
									html_reporte +='<td class="grid_head" colspan="3" width="360">'+nombre_mes+""+'&nbsp;'+anio_ini+'</td>';
									primerColumna1++;
								}
								html_reporte +='<td class="grid_head" colspan="3" width="360">Anual&nbsp'+anio_ini+'</td>';
                                html_reporte +='</tr>';
                                
                                
                                 html_reporte +='<tr>';
                                var anio_ini=$select_ano.val();
                                //var anio_fin=$select_anofin.val();
                                
                                html_reporte +='<td class="grid_head" width="220">&nbsp;</td>'; 
                                
                               var primerColumna2=0;
							   for(var mes=1; mes<=12; mes++){
									var nombre_mes=array_meses2[mes];
									html_reporte +='<td class="grid_head" width="120">Pesos</td>';
									html_reporte +='<td class="grid_head" width="120">Kgs/Lts</td>';
									html_reporte +='<td class="grid_head" width="120">MOP</td>';
									primerColumna2++;
								}
							  
								html_reporte +='<td class="grid_head" width="120">Pesos</td>';
								html_reporte +='<td class="grid_head" width="120">Kgs/Lts</td>'; 
								html_reporte +='<td class="grid_head" width="120">MOP</td>';  
                                html_reporte +='</tr></thead>';
                                
                                
								var totalano=0.0;
                                var totalmesenero=0.0;
                                var totalmesfebrero=0.0;
                                var totalmesmarzo=0.0;
                                var totalmesabril=0.0;
                                var totalmesmayo=0.0;
                                var totalmesjunio=0.0;
                                var totalmesjulio=0.0;
                                var totalmesagosto=0.0;
                                var totalmesseptiembre=0.0;
                                var totalmesoctubre=0.0;
                                var totalmesnoviembre=0.0;
                                var totalmesdiciembre=0.0;

                                var totalano2=0.0;
                                var totalmesenero1=0.0;
                                var totalmesfebrero2=0.0;
                                var totalmesmarzo3=0.0;
                                var totalmesabril4=0.0;
                                var totalmesmayo5=0.0;
                                var totalmesjunio6=0.0;
                                var totalmesjulio7=0.0;
                                var totalmesagosto8=0.0;
                                var totalmesseptiembre9=0.0;
                                var totalmesoctubre10=0.0;
                                var totalmesnoviembre11=0.0;
                                var totalmesdiciembre12=0.0;

                                var totalano3=0.0;
                                var totalmesenero11=0.0;
                                var totalmesfebrero12=0.0;
                                var totalmesmarzo13=0.0;
                                var totalmesabril14=0.0;
                                var totalmesmayo15=0.0;
                                var totalmesjunio16=0.0;
                                var totalmesjulio17=0.0;
                                var totalmesagosto18=0.0;
                                var totalmesseptiembre19=0.0;
                                var totalmesoctubre110=0.0;
                                var totalmesnoviembre111=0.0;
                                var totalmesdiciembre112=0.0;
                             
                             
                                var height2 = $('#cuerpo').css('height');
                                var alto = parseInt(height2)-270;
                                var pix_alto=alto+'px';
                                 html_reporte +='<tbody>';
								html_reporte +='<tr>';
								html_reporte +='<td colspan="40">';
								html_reporte +='<div id="reporte" style="background-color:#ffffff; overflow:scroll; overflow-x:hidden; overflow-y:auto; height:'+pix_alto+'; width=4900px; align=top;">';
								html_reporte +='<table class="table_reporte" >';

                                for(var i=0; i<body_tabla.length; i++){

                                        totalano=parseFloat(totalano)+parseFloat(body_tabla[i]["suma_total"]);
                                        totalano2=parseFloat(totalano2)+parseFloat(body_tabla[i]["suma_total2"]);
                                        totalano3=parseFloat(totalano3)+parseFloat(body_tabla[i]["suma_total3"]);

                                        html_reporte +='<tr>';
                                        html_reporte +='<td style="background-color:#ffffff; border:1px solid #ccc !important; border-right: 1px solid #ccc !important;" width="220" valign="top" align="left">'+body_tabla[i]["razon_social"]+'</td>'; 
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["enero"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["enero1"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td width="120" align="right">'+$(this).agregar_comas((parseFloat(body_tabla[i]["enero11"]) * 100 ).toFixed(2))+' %</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["enero11"]).toFixed(2))+'</td>';  
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["febrero"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["febrero2"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["febrero12"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["marzo"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["marzo3"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["marzo13"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["abril"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["abril4"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["abril14"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["mayo"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["mayo5"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["mayo15"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["junio"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["junio6"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["junio16"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["julio"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["julio7"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["julio17"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["agosto"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["agosto8"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["agosto18"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["septiembre"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["septiembre9"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["septiembre19"]).toFixed(2))+'</td>'; 
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["octubre"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["octubre10"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["octubre110"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["noviembre"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["noviembre11"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["noviembre111"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["diciembre"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["diciembre12"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["diciembre112"]).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["suma_total"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["suma_total2"]).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_datos" width="120" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["suma_total3"]).toFixed(2))+'</td>'; 
                                        html_reporte +='</tr>';
                                        
										totalmesenero=parseFloat(totalmesenero)+parseFloat(body_tabla[i]["enero"]);
										totalmesfebrero=parseFloat(totalmesfebrero)+parseFloat(body_tabla[i]["febrero"]);
										totalmesmarzo=parseFloat(totalmesmarzo)+parseFloat(body_tabla[i]["marzo"]);
										totalmesabril=parseFloat(totalmesabril)+parseFloat(body_tabla[i]["abril"]);
										totalmesmayo=parseFloat(totalmesmayo)+parseFloat(body_tabla[i]["mayo"]);
										totalmesjunio=parseFloat(totalmesjunio)+parseFloat(body_tabla[i]["junio"]);
										totalmesjulio=parseFloat(totalmesjulio)+parseFloat(body_tabla[i]["julio"]);
										totalmesagosto=parseFloat(totalmesagosto)+parseFloat(body_tabla[i]["agosto"]);
										totalmesseptiembre=parseFloat(totalmesseptiembre)+parseFloat(body_tabla[i]["septiembre"]);
										totalmesoctubre=parseFloat(totalmesoctubre)+parseFloat(body_tabla[i]["octubre"]);
										totalmesnoviembre=parseFloat(totalmesnoviembre)+parseFloat(body_tabla[i]["noviembre"]);
										totalmesdiciembre=parseFloat(totalmesdiciembre)+parseFloat(body_tabla[i]["diciembre"]);
										
										totalmesenero1=parseFloat(totalmesenero1)+parseFloat(body_tabla[i]["enero1"]);
										totalmesfebrero2=parseFloat(totalmesfebrero2)+parseFloat(body_tabla[i]["febrero2"]);
										totalmesmarzo3=parseFloat(totalmesmarzo3)+parseFloat(body_tabla[i]["marzo3"]);
										totalmesabril4=parseFloat(totalmesabril4)+parseFloat(body_tabla[i]["abril4"]);
										totalmesmayo5=parseFloat(totalmesmayo5)+parseFloat(body_tabla[i]["mayo5"]);
										totalmesjunio6=parseFloat(totalmesjunio6)+parseFloat(body_tabla[i]["junio6"]);
										totalmesjulio7=parseFloat(totalmesjulio7)+parseFloat(body_tabla[i]["julio7"]);
										totalmesagosto8=parseFloat(totalmesagosto8)+parseFloat(body_tabla[i]["agosto8"]);
										totalmesseptiembre9=parseFloat(totalmesseptiembre9)+parseFloat(body_tabla[i]["septiembre9"]);
										totalmesoctubre10=parseFloat(totalmesoctubre10)+parseFloat(body_tabla[i]["octubre10"]);
										totalmesnoviembre11=parseFloat(totalmesnoviembre11)+parseFloat(body_tabla[i]["noviembre11"]);
										totalmesdiciembre12=parseFloat(totalmesdiciembre12)+parseFloat(body_tabla[i]["diciembre12"]);
										
										totalmesenero11=parseFloat(totalmesenero11)+parseFloat(body_tabla[i]["enero11"]);
										totalmesfebrero12=parseFloat(totalmesfebrero12)+parseFloat(body_tabla[i]["febrero12"]);
										totalmesmarzo13=parseFloat(totalmesmarzo13)+parseFloat(body_tabla[i]["marzo13"]);
										totalmesabril14=parseFloat(totalmesabril14)+parseFloat(body_tabla[i]["abril14"]);
										totalmesmayo15=parseFloat(totalmesmayo15)+parseFloat(body_tabla[i]["mayo15"]);
										totalmesjunio16=parseFloat(totalmesjunio16)+parseFloat(body_tabla[i]["junio16"]);
										totalmesjulio17=parseFloat(totalmesjulio17)+parseFloat(body_tabla[i]["julio17"]);
										totalmesagosto18=parseFloat(totalmesagosto18)+parseFloat(body_tabla[i]["agosto18"]);
										totalmesseptiembre19=parseFloat(totalmesseptiembre19)+parseFloat(body_tabla[i]["septiembre19"]);
										totalmesoctubre110=parseFloat(totalmesoctubre110)+parseFloat(body_tabla[i]["octubre110"]);
										totalmesnoviembre111=parseFloat(totalmesnoviembre111)+parseFloat(body_tabla[i]["noviembre111"]);
										totalmesdiciembre112=parseFloat(totalmesdiciembre112)+parseFloat(body_tabla[i]["diciembre112"]);
                                }
                                
                                html_reporte +='</table>';
								html_reporte +='</div>';
								html_reporte +='</td>';
								html_reporte +='</tr>';
								html_reporte +='</tbody>';

                                html_reporte +='<tfoot>';
                                /*sumando los meses**/
                                    html_reporte +='<tr>';
                                        html_reporte +='<td class="grid_foot" width="220" align="right" id="">Total&nbsp;Mensual</td>'
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesenero).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesenero1).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesenero11).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';   
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesfebrero).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesfebrero2).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesfebrero12).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';  
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesmarzo).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesmarzo3).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesmarzo13).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';  
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesabril).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesabril4).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesabril14).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesmayo).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesmayo5).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesmayo15).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';  
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesjunio).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesjunio6).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesjunio16).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesjulio).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesjulio7).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesjulio17).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesagosto).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesagosto8).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesagosto18).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesseptiembre).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesseptiembre9).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesseptiembre19).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesoctubre).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesoctubre10).toFixed(2))+'</td>'; 
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesoctubre110).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>'; 
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesnoviembre).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesnoviembre11).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesnoviembre111).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesdiciembre).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesdiciembre12).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">$'+$(this).agregar_comas(parseFloat(totalmesdiciembre112).toFixed(2))+'</td>';
                                        //html_reporte +='<td style="width: 5px;" align="left">&nbsp;</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">'+$(this).agregar_comas(parseFloat(totalano).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">'+$(this).agregar_comas(parseFloat(totalano2).toFixed(2))+'</td>';
                                        html_reporte +='<td class="grid_foot" width="120" align="right" id="sin_borde">'+$(this).agregar_comas(parseFloat(totalano3).toFixed(2))+'</td>';

								html_reporte +='</tr>';
                                html_footer +='</tfoot>';
                                html_reporte += '</table>';
                                $div_reporte.append(html_reporte); 
                                $('#div_reporte').css('height:'+pix_alto+'px');
                                /*
                                $('#div_reporte').tableScroll({height:parseInt(pix_alto)});
                                */
                                
                                var width2 = $('#cuerpo').css('width');
                                var ancho = parseInt(width2);
								var pix_ancho=ancho+'px';
								//alert(pix_ancho);
								$('#div_reporte').css('width:'+pix_ancho+'px');
								
								
								
                        });
                   }else{
						jAlert("Es Necesario seleccionar el Año.",'Atencion!');
					}
                });	
});       
