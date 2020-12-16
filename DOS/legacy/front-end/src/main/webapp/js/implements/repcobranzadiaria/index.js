$(function() {
          var config =  {
                    tituloApp: 'Cobranza Diaria' ,                 
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
                        return this.contextpath + "/controllers/repcobranzadiaria";
                        //  return this.controller;
                    }
                
          };
  

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

          //var $select_tipo_reporte = $('#lienzo_recalculable').find('div.repcobranzadiaria').find('table#fechas tr td').find('select[name=ventas]');
          var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
          var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
          var $Nombre_Cliente= $('#lienzo_recalculable').find('div.repcobranzadiaria').find('table#fechas tr td').find('input[name=nombrecliente]');
          var $Buscar_clientes= $('#lienzo_recalculable').find('div.repcobranzadiaria').find('table#fechas tr td').find('a[href*=busca_cliente]');
          var $id_cliente= $('#lienzo_recalculable').find('input[name=id_cliente]');
          var $Buscar_cobranzadiaria= $('#lienzo_recalculable').find('div.repcobranzadiaria').find('table#fechas tr td').find('input[value$=Buscar]');
          var $genera_reporte_cobranzadiaria= $('#lienzo_recalculable').find('div.repcobranzadiaria').find('table#fechas tr td').find('input[value$=Generar_PDF]');

          var $div_cobranzadiaria= $('#repcobranzadiaria');



          $fecha_inicial.attr({'readOnly':true});
          $fecha_final.attr({'readOnly':true});
          $Nombre_Cliente.attr({'readOnly':true});
                   
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
         
         
         
         
         
         
          $Buscar_cobranzadiaria.click(function(event){
               event.preventDefault();
                  
               ///GENERA LA VISTA DE LA IMPRESION DEL REPORTE DE POR CLIENTE

                    $div_cobranzadiaria.children().remove();
//                    $fecha_inicial
//                    $fecha_final
//                    $Nombre_Cliente.val
//                    var fecha_inicial = $fecha_inicial.val();
//                    var fecha_final = $fecha_final.val();
//                    var cliente = $Nombre_Cliente.val();


                    var usuario = config.getUi();
                    
                    if($fecha_inicial.val() != "" && $fecha_final.val() != ""){ 
                         var arreglo_parametros = {fecha_inicial : $fecha_inicial.val() , fecha_final : $fecha_final.val(),cliente : $id_cliente.val() , iu:config.getUi()};
                         var restful_json_service = config.getUrlForGetAndPost() + '/getCobranzaDiaria/out.json';
                         var cliente="";
                         var producto="";

						$.post(restful_json_service,arreglo_parametros,function(entry){
							var body_tabla = entry['Cobranza'];
							var footer_tabla = entry['Totales'];
							var header_tabla = {
								factura       		:'Factura',
								fecha_factura  		:'Fecha',
								cliente       		:"Cliente",
								simbolo_moneda_fac 	:"",                                   
								monto_factura   	:"Monto Factura",
								simbolo_moneda_aplicado :"",                                   
								pago_aplicado   	:"Pago Aplicado",
								fecha_pago    		:"Fecha Pago",
								simbolo_moneda_pago :"", 
								monto_pago    		:"Monto Pago"
							};

								
                              var html_numero_kits = '<table id="ventas_diarias">';
                              var porcentaje = 0.0;
                              var numero_control=0.0; 
                              var cliente=0.0;  
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
                                        
                                        if(attrValue == "Cliente"){
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
                                        html_numero_kits +='<td align="left"  width="450px">'+body_tabla[i]["cliente"]+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda_fac"]+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_factura"]).toFixed(2))+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda_aplicado"]+'</td>';
                                        html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["pago_aplicado"]).toFixed(2))+'</td>';  
                                        html_numero_kits +='<td align="center"  width="90px">'+body_tabla[i]["fecha_pago"]+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="10px" id="simbolo_moneda">'+body_tabla[i]["simbolo_moneda_pago"]+'</td>'; 
                                        html_numero_kits +='<td align="right"  width="100px" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["monto_pago"]).toFixed(2))+'</td>';
                                   html_numero_kits +='</tr>';
                              }
                              
                              //* // esto es para imprimir totales de suma en la vista START
                              html_numero_kits +='<tfoot>';
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
								html_numero_kits +='</tfoot>';
                              //*/ //End sum of display
                              
                              
                              

                              html_numero_kits += '</table>';
								
                              $div_cobranzadiaria.append(html_numero_kits); 
                              var height2 = $('#cuerpo').css('height');
                              var alto = parseInt(height2)-275;
                              var pix_alto=alto+'px';


                              $('#ventas_diarias').tableScroll({height:parseInt(pix_alto)});

                         });
               }else{
                    jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
               }            
          }); 
          
                    //click generar reporte de pronostico de Cobranza
          $genera_reporte_cobranzadiaria.click(function(event){
                    event.preventDefault();


                    var cliente=$id_cliente.val();//nombre del cliente
                    var fecha_inicial = $fecha_inicial.val();//fecha inicial
                    var fecha_final = $fecha_final.val();//fecha final
                    var usuario=config.getUi();//usuario
                    var cadena = cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+usuario //cadena que incluye los 4 parametros anteriores

                    if(fecha_inicial != 0 && fecha_final !=0){
						var input_json = config.getUrlForGetAndPost() + '/getCobranzaDiaria/'+cadena+'/out.json';
						window.location.href=input_json;
                    }else{
						jAlert("Debe elegir el rango la fecha inicial y su fecha final par la busqueda","Atencion!!!")
                    }
          });
          
});
