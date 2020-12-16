$(function() {
    var config =  {
					empresa:$('#lienzo_recalculable').find('input[name=emp]').val(),
					sucursal:$('#lienzo_recalculable').find('input[name=suc]').val(),
                    tituloApp: 'Ventas Netas por Cliente' ,
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
                        return this.contextpath + "/controllers/repventasnetasxcliente";
                        //  return this.controller;
                    }

    };


	$('#header').find('#header1').find('span.emp').text(config.getEmp());
	$('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
    //aqui va el titulo del catalogo
    $('#barra_titulo').find('#td_titulo').append(config.getTituloApp());

	$('#barra_acciones').hide();

	//barra para el buscador
	$('#barra_buscador').hide();


        var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
        var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
        var $genera_reporte_ventas_netasxcliente = $('#lienzo_recalculable').find('div.repventasnetasxcliente').find('table#fechas tr td').find('input[value$=Generar_PDF]');
        var $Buscar_ventas_netasxcliente= $('#lienzo_recalculable').find('div.repventasnetasxcliente').find('table#fechas tr td').find('input[value$=Buscar]');
        var $div_ventas_netas_porcliente= $('#ventasnetasxcliente');

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

		mostrarFecha($fecha_inicial.val());
		//fecha final


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
				$fecha_inicial.val(formated);
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



        mostrarFecha($fecha_final.val());







        //click generar reporte de pronostico de Cobranza
        $genera_reporte_ventas_netasxcliente.click(function(event){
            event.preventDefault();
            var fecha_inicial = $fecha_inicial.val();
            var fecha_final = $fecha_final.val();
            if(fecha_inicial != 0 && fecha_final !=0){
                                    var input_json = config.getUrlForGetAndPost() + '/getVentasNetasxCliente/'+fecha_inicial+'/'+fecha_final+'/'+config.getUi()+'/out.json';
                                    window.location.href=input_json;
            }else{
                jAlert("Debe elegir el rango la fecha inicial y su fecha final par la busqueda","Atencion!!!")
            }
        });


       $Buscar_ventas_netasxcliente.click(function(event){
        event.preventDefault();
        $div_ventas_netas_porcliente.children().remove();
        var fecha_inicial = $fecha_inicial.val();
        var fecha_final = $fecha_final.val();
        if(fecha_inicial != "" && fecha_final != ""){

                    var arreglo_parametros = {fecha_inicial : $fecha_inicial.val() , fecha_final : $fecha_final.val(), iu:config.getUi()};

                    var restful_json_service = config.getUrlForGetAndPost() + '/getVentasNetasxCliente.json'
                    var cliente="";
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;

                        var header_tabla = {
                                            numero_control:'No.Control',
                                            cliente  :'Cliente',
                                            moneda    :"",
                                            venta  :'venta',
                                            porcentaje    :'%'

                        };
                    var TPventa_neta=0.0;
                    var Sumatoriaventa_neta = 0.0;
                    var sumatotoriaporciento = 0.0;
                    var html_ventasnetas = '<table id="ventas" >';
                    var porcentaje = 0.0;


                    var numero_control=0.0;
                    var cliente=0.0;
                    var moneda="$";
                    var venta_neta=0.0;
                    var porciento=0.0;
                    var tmp= 0;





                    html_ventasnetas +='<thead> <tr>';
                        for(var key in header_tabla){
                            var attrValue = header_tabla[key];
                            if(attrValue == "No.Control"){
                                html_ventasnetas +='<td width="50px" align="left">'+attrValue+'</td>';
                            }
                            if(attrValue == "Cliente"){
                                html_ventasnetas +='<td width="400px" align="left">'+attrValue+'</td>';
                            }
                            if(attrValue == ""){
                                html_ventasnetas +='<td width="5px" align="right" >'+attrValue+'</td>';
                            }

                            if(attrValue == "venta"){
                                html_ventasnetas +='<td width="100px" align="right" >'+attrValue+'</td>';
                            }

                            if(attrValue == "%"){
                                html_ventasnetas +='<td width="100px" align="right" >'+attrValue+'</td>';
                            }
                        }
                    html_ventasnetas +='</tr> </thead>';

                    for(var i=0; i<body_tabla.length; i++){

                            Sumatoriaventa_neta= Sumatoriaventa_neta + parseFloat(body_tabla[i]["Tventa_neta"]);

                    }



                    for(var i=0; i<body_tabla.length; i++){
						TPventa_neta=body_tabla[i]["Tventa_neta"];
						porciento=((TPventa_neta/ Sumatoriaventa_neta ) * 100);
						sumatotoriaporciento = sumatotoriaporciento + ((TPventa_neta/ Sumatoriaventa_neta ) * 100);
						html_ventasnetas +='<tr>';
						html_ventasnetas +='<td align="left" >'+body_tabla[i]["numero_control"]+'</td>';
						html_ventasnetas +='<td align="left" >'+body_tabla[i]["cliente"]+'</td>';
						html_ventasnetas +='<td align="right" >'+"$"+'</td>';
						html_ventasnetas +='<td widht="50px" align="right">'+$(this).agregar_comas(parseFloat(TPventa_neta).toFixed(2))+'</td>';
						html_ventasnetas +='<td widht="50px" align="right">'+$(this).agregar_comas(parseFloat(porciento).toFixed(2)) +'%'+'</td>';
						html_ventasnetas +='</tr>';
                     }
					html_ventasnetas +='<tfoot>';
					html_ventasnetas +='<tr>';
					html_ventasnetas +='<td align="right" ></td>';
					html_ventasnetas +='<td align="right">'+"TOTAL GENERAL:   "+'</td>';
					html_ventasnetas +='<td align="right" >'+"$"+'</td>';
					html_ventasnetas +='<td widht="50px" align="right">'+$(this).agregar_comas(parseFloat(Sumatoriaventa_neta).toFixed(2))+'</td>';
					html_ventasnetas +='<td widht="50px" align="right">'+$(this).agregar_comas(parseFloat(sumatotoriaporciento).toFixed(2)) +'%'+'</td>';
					html_ventasnetas +='</tr>';
					html_ventasnetas +='</tfoot>';


                    html_ventasnetas += '</table>';

                    $div_ventas_netas_porcliente.append(html_ventasnetas);
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-275;
                    var pix_alto=alto+'px';


                    $('#ventas').tableScroll({height:parseInt(pix_alto)});
                    });
                }else{
                    jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');

                }

       });
});





