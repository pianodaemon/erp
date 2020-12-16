$(function() {
          var config =  {
                    tituloApp: 'Pagos Diarios' ,                 
                    contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),
                    
                    userName : $('#lienzo_recalculable').find('input[name=user]').val(),
                    ui : $('#lienzo_recalculable').find('input[name=iu]').val(),
                    
                    
                    empresa:$('#lienzo_recalculable').find('input[name=emp]').val(),
                    sucursal:$('#lienzo_recalculable').find('input[name=suc]').val(),
                    
                   
                    getUrlForGetAndPost : function(){
                        var url = document.location.protocol + '//' + document.location.host + this.getController();
                        return url;
                    },

                    getUserName: function(){
                        return this.userName;
                    },
                    
                    getUi: function(){
                        return this.ui;
                    },
                    
                    getEmpresa: function(){
                        return this.empresa;
                    },
                    getSucursal: function(){
                        return this.sucursal;
                    },
                    
                    getTituloApp: function(){
                        return this.tituloApp;
                    },


                    getController: function(){
                        return this.contextpath + "/controllers/reppagosdiaria";
                        //  return this.controller;
                    }
                
          };
  
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
          
			$('#header').find('#header1').find('span.emp').text(config.getEmpresa());
			$('#header').find('#header1').find('span.suc').text(config.getSucursal());
			$('#header').find('#header1').find('span.username').text(config.getUserName());

			var $username = $('#header').find('#header1').find('span.username');
			$username.text($('#lienzo_recalculable').find('input[name=user]').val());

			//aqui va el titulo del catalogo
			$('#barra_titulo').find('#td_titulo').append(config.getTituloApp());
			$('#barra_acciones').hide();

			//barra para el buscador 
			$('#barra_buscador').hide();



			var $select_tipo = $('#lienzo_recalculable').find('div.reppagosdiaria').find('table#busqueda tr td').find('select[name=select_tipo]');
			var $proveedor = $('#lienzo_recalculable').find('div.reppagosdiaria').find('table#busqueda tr td').find('input[name=proveedor]');
			var $buscar_proveedor= $('#lienzo_recalculable').find('div.reppagosdiaria').find('table#busqueda tr td').find('a[href*=buscar_proveedor]');
			var $id_proveedor= $('#lienzo_recalculable').find('input[name=id_proveedor]');
			var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
			var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
			var $Buscar_Pagos= $('#lienzo_recalculable').find('div.reppagosdiaria').find('table#busqueda tr td').find('input[value$=Buscar]');
			var $genera_reporte_cobranzadiaria= $('#lienzo_recalculable').find('div.reppagosdiaria').find('table#busqueda tr td').find('input[value$=PDF]');
			var $div_pagosdiarios= $('#reppagosdiaria');
			
			
			//Aplicar evento Keypress para que al pulsar enter ejecute la busqueda
			$(this).aplicarEventoKeypressEjecutaTrigger($select_tipo, $Buscar_Pagos);
			$(this).aplicarEventoKeypressEjecutaTrigger($proveedor, $Buscar_Pagos);
			$(this).aplicarEventoKeypressEjecutaTrigger($fecha_inicial, $Buscar_Pagos);
			$(this).aplicarEventoKeypressEjecutaTrigger($fecha_final, $Buscar_Pagos);
			
			$fecha_inicial.attr({'readOnly':true});
			$fecha_final.attr({'readOnly':true});
			//$proveedor.attr({'readOnly':true});
			
			var tipo_prov_html='<option value="0" selected="yes">Todos</option>';
			tipo_prov_html += '<option value="1">Proveedores de Materia Prima</option>';
			tipo_prov_html += '<option value="2">Proveedores Otros</option>';
			$select_tipo.children().remove();
			$select_tipo.append(tipo_prov_html);
			
	
			
          $fecha_inicial.DatePicker({
                    format:'Y-m-d',
                    date: $(this).val(),
                    current: $(this).val(),
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

          $fecha_inicial.val(mostrarFecha());
	
          $fecha_final.DatePicker({
                    format:'Y-m-d',
                    date: $(this).val(),
                    current: $(this).val(),
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
        

			$fecha_final.val(mostrarFecha());
		
    
          
          
          
	//buscador de proveedores
	$busca_proveedores = function(){
		$(this).modalPanel_Buscaproveedor();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
		var $campo_email = $('#forma-buscaproveedor-window').find('input[name=campo_email]');
		var $campo_nombre = $('#forma-buscaproveedor-window').find('input[name=campo_nombre]');
		
		var $buscar_plugin_proveedor = $('#forma-buscaproveedor-window').find('#busca_proveedor_modalbox');
		var $cancelar_plugin_busca_proveedor = $('#forma-buscaproveedor-window').find('#cencela');
			
		$('#forma-entradamercancias-window').find('input[name=tipo_proveedor]').val('');
			
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
			var restful_json_service = config.getUrlForGetAndPost() + '/getBuscaProveedores.json'
			$arreglo = {    rfc:$campo_rfc.val(),
							email:$campo_email.val(),
							nombre:$campo_nombre.val(),
							iu:config.getUi()
						}
			
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(restful_json_service,$arreglo,function(entry){
				$.each(entry['Proveedores'],function(entryIndex,proveedor){
					
					trr = '<tr>';
						trr += '<td width="120">';
							trr += '<input type="hidden" id="id_prov" value="'+proveedor['id']+'">';
							trr += '<input type="hidden" id="tipo_prov" value="'+proveedor['proveedortipo_id']+'">';
							trr += '<span class="rfc">'+proveedor['rfc']+'</span>';
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
				
				//seleccionar un proveedor del grid de resultados
				$tabla_resultados.find('tr').click(function(){
					//asigna la razon social del proveedor al campo correspondiente
					$id_proveedor.val($(this).find('#id_prov').val());
					$proveedor.val($(this).find('#razon_social').html());
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
				});
			});
		});
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_rfc, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_email, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_nombre, $buscar_plugin_proveedor);
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
		});
	}//termina buscador de proveedores



	
	
   $buscar_proveedor.click(function(event){
        event.preventDefault();
        $busca_proveedores();//llamada a la funcion que busca proveedores
    });
          

         
          $Buscar_Pagos.click(function(event){
               event.preventDefault();
                  
               ///GENERA LA VISTA DE LA IMPRESION DEL REPORTE DE POR PROVEEDOR

                    $div_pagosdiarios.children().remove();



                    var usuario = config.getUi();
                    
                    if($fecha_inicial.val().trim() != "" && $fecha_final.val().trim() != ""){ 
                         var arreglo_parametros = {fecha_inicial : $fecha_inicial.val(), fecha_final:$fecha_final.val(), proveedor:$proveedor.val(), tipo_prov:$select_tipo.val(), iu:config.getUi()};
                         var restful_json_service = config.getUrlForGetAndPost() + '/getPagosDiaria/out.json';
                         var proveedor="";
                         var producto="";

						$.post(restful_json_service,arreglo_parametros,function(entry){
							var body_tabla = entry['Pagos'];
							var footer_tabla = entry['Totales'];
							var header_tabla = {
								factura       		:'Factura',
								fecha_factura  		:'Fecha',
								proveedor       		:"Proveedor",
								simbolo_moneda_fac 	:"",                                   
								monto_factura   	:"Monto Factura",
								simbolo_moneda_aplicado :"",                                   
								pago_aplicado   	:"Pago Aplicado",
								fecha_pago    		:"Fecha Pago",
								simbolo_moneda_pago :"", 
								monto_pago    		:"Monto Pago"
							};

								
                              var html_numero_kits = '<table id="pagos_diarios">';
                              var porcentaje = 0.0;
                              var numero_control=0.0; 
                              var proveedor=0.0;  
                              var venta_neta=0.0; 
                              var porciento=0.0;
                              var tmp= 0;
                              
              
								
                              html_numero_kits +='<thead> <tr>';
                                   for(var key in header_tabla){
                                        var attrValue = header_tabla[key];
                                        if(attrValue == "Factura"){
											html_numero_kits +='<td  align="CENTER" width="100px" >&nbsp;&nbsp;'+attrValue+'</td>'; 
                                        }
                                        
                                        if(attrValue == "Fecha"){
											html_numero_kits +='<td  align="CENTER" width="90px" >'+attrValue+'</td>'; 
                                        }
                                        
                                        if(attrValue == "Proveedor"){
											html_numero_kits +='<td  align="LEFT"  width="450px" >'+attrValue+'</td>'; 
                                        }
                                        
                                        if(attrValue == ""){
											html_numero_kits +='<td  align="center" width="10px" id="simbolo_moneda">'+attrValue+'</td>'; 
                                        }
                                        
                                        if(attrValue == "Monto Factura"){
											html_numero_kits +='<td  align="center" width="100px" id="monto">'+attrValue+'</td>'; 
                                        }
										
                                        if(attrValue == "Pago Aplicado"){
											html_numero_kits +='<td  align="center" width="100px" id="monto">'+attrValue+'</td>'; 
                                        }
                                        
                                        if(attrValue == "Fecha Pago"){
											html_numero_kits +='<td  align="center" width="90px" >'+attrValue+'</td>'; 
                                        }
                                        
                                        if(attrValue == "Monto Pago"){
											html_numero_kits +='<td  align="center"  width="100px" id="monto">'+attrValue+'</td>'; 
                                        }
                                   }

                              html_numero_kits +='</tr> </thead>';
								
                              for(var i=0; i<body_tabla.length; i++){
                                   html_numero_kits +='<tr>';
                                        html_numero_kits +='<td align="center"  width="100px">'+body_tabla[i]["factura"]+'</td>'; 
                                        html_numero_kits +='<td align="center" width="90px">'+body_tabla[i]["fecha_factura"]+'</td>'; 
                                        html_numero_kits +='<td align="left"  width="450px">'+body_tabla[i]["proveedor"]+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda_fac"]+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_factura"]).toFixed(2))+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda_aplicado"]+'</td>';
                                        html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["pago_aplicado"]).toFixed(2))+'</td>';  
                                        html_numero_kits +='<td align="center"  width="90px">'+body_tabla[i]["fecha_pago"]+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda_pago"]+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_pago"]).toFixed(2))+'</td>';
                                   html_numero_kits +='</tr>';
                              }
                             
                              html_numero_kits +='<tfoot>';
							 //esto es para imprimir totales de suma en la vista START
			
							if(parseFloat(footer_tabla[0]["suma_pesos_monto_total"])  > 0 || parseFloat(footer_tabla[0]["suma_pesos_monto_pago"]) > 0){	
								html_numero_kits +='<tr>';
								html_numero_kits +='<td align="right"  width="100px"></td>'; 
								html_numero_kits +='<td align="right"  width="90px"></td>'; 
								html_numero_kits +='<td align="right"  width="450px">Total M.N.</td>';
								html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda"></td>';
								html_numero_kits +='<td align="right"  width="100px" id="monto"></td>'; 
								html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">$</td>';
								html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_monto_total"]).toFixed(2))+'</td>';  
								html_numero_kits +='<td align="center"  width="90px"></td>'; 
								html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">$</td>';
								html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_pesos_monto_pago"]).toFixed(2))+'</td>';  
								html_numero_kits +='</tr>';
							}
							
							if(parseFloat(footer_tabla[0]["suma_dolares_monto_total"])  > 0 || parseFloat(footer_tabla[0]["suma_dolares_monto_pago"]) > 0){	
								html_numero_kits +='<tr>';
								html_numero_kits +='<td align="right"  width="100px"></td>'; 
								html_numero_kits +='<td align="right" width="90px"></td>'; 
								html_numero_kits +='<td align="right"  width="450px">Total USD</td>';
								html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda"></td>';
								html_numero_kits +='<td align="right"  width="100px" id="monto"></td>'; 
								html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">USD</td>';
								html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_monto_total"]).toFixed(2))+'</td>';  
								html_numero_kits +='<td align="center"  width="90px"></td>'; 
								html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">USD</td>';
								html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_dolares_monto_pago"]).toFixed(2))+'</td>';
								html_numero_kits +='</tr>';
							}
							
							if(parseFloat(footer_tabla[0]["suma_euros_monto_total"])  > 0 || parseFloat(footer_tabla[0]["suma_euros_monto_pago"]) > 0){	
								html_numero_kits +='<tr>';
								html_numero_kits +='<td align="right"  width="100px"></td>'; 
								html_numero_kits +='<td align="right" width="90px"></td>'; 
								html_numero_kits +='<td align="right"  width="450px">Total EUR</td>';
								html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda"></td>';
								html_numero_kits +='<td align="right"  width="100px" id="monto"></td>'; 
								html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">&#8364;</td>';
								html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_euros_monto_total"]).toFixed(2))+'</td>';  
								html_numero_kits +='<td align="center"  width="90px"></td>'; 
								html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">&#8364;</td>';
								html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(footer_tabla[0]["suma_euros_monto_pago"]).toFixed(2))+'</td>';
								html_numero_kits +='</tr>';
							}
										
                              html_numero_kits += '</table>';
								
                              $div_pagosdiarios.append(html_numero_kits); 
                              var height2 = $('#cuerpo').css('height');
                              var alto = parseInt(height2)-310;
                              var pix_alto=alto+'px';


                              $('#pagos_diarios').tableScroll({height:parseInt(pix_alto)});

                         });
               }else{
                    jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
               }            
          }); 
          
		//click generar reporte de pronostico de Cobranza
		$genera_reporte_cobranzadiaria.click(function(event){
			event.preventDefault();
			var proveedor = ($proveedor.val()=='')? '0':$proveedor.val();
			
			var fecha_inicial = $fecha_inicial.val();//fecha inicial
			var fecha_final = $fecha_final.val();//fecha final
			var usuario=config.getUi();//usuario
			
			var cadena = proveedor+"___"+fecha_inicial+"___"+fecha_final+"___"+usuario+"___"+$select_tipo.val();//cadena que incluye los 5 parametros anteriores

			if(fecha_inicial!='' && fecha_final!=''){
				var input_json = config.getUrlForGetAndPost() + '/getPagosDiaria/'+cadena+'/out.json';
				window.location.href=input_json;
			}else{
				jAlert("Debe elegir el rango la fecha inicial y su fecha final par la busqueda","Atencion!!!")
			}
		});
          
});
