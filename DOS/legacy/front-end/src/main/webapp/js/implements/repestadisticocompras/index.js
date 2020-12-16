$(function() {
        var config =  {
                    tituloApp: 'Estadistico de Compras' ,
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
                        return this.contextpath + "/controllers/repestadisticocompras";
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

		var $select_tipo_reporte = $('#lienzo_recalculable').find('div.repestadisticocompras').find('table#fechas tr td').find('select[name=ventas]');
		var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
		var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');

		var $genera_reporte_ventas_netasproductofactura= $('#lienzo_recalculable').find('div.repestadisticocompras').find('table#fechas tr td').find('input[value$=Generar_PDF]');
		var $Buscar_estadistico_compras= $('#lienzo_recalculable').find('div.repestadisticocompras').find('table#fechas tr td').find('input[value$=Buscar]');

		var $Nombre_Proveedor= $('#lienzo_recalculable').find('div.repestadisticocompras').find('table#fechas tr td').find('input[name=nombreproveedor]');
		var $Nombre_Producto= $('#lienzo_recalculable').find('div.repestadisticocompras').find('table#fechas tr td').find('input[name=nombreproducto]');
		var $id_proveedor_edo_cta = $('#lienzo_recalculable').find('div.repestadisticocompras').find('input[name=id_proveedor_edo_cta]');


		var $Buscar_proveedor= $('#lienzo_recalculable').find('div.repestadisticocompras').find('table#fechas tr td').find('a[href*=busca_proveedor]');
		var $Buscar_productos= $('#lienzo_recalculable').find('div.repestadisticocompras').find('table#fechas tr td').find('a[href*=busca_producto]');
		var $div_ventas_netas_productofactura= $('#ventasnetasproductofactura');

			$fecha_inicial.attr({'readOnly':true});
			$fecha_final.attr({'readOnly':true});
			$Nombre_Proveedor.attr({'readOnly':true});
			$Nombre_Producto.attr({'readOnly':true});

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
                //mostrarFecha.attr({ 'readOnly':true });

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

            //buscador de productos
            busca_productos = function(sku_buscar){
                $(this).modalPanel_Buscaproducto();
                var $dialogoc =  $('#forma-buscaproducto-window');
                $dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());

                $('#forma-buscaproducto-window').css({"margin-left": -200, 	"margin-top": -200});

                var $tabla_resultados = $('#forma-buscaproducto-window').find('#tabla_resultado');

                var $campo_sku = $('#forma-buscaproducto-window').find('input[name=campo_sku]');
                var $select_tipo_producto = $('#forma-buscaproducto-window').find('select[name=tipo_producto]');
                var $campo_descripcion = $('#forma-buscaproducto-window').find('input[name=campo_descripcion]');

                var $buscar_plugin_producto = $('#forma-buscaproducto-window').find('#busca_producto_modalbox');
                var $cancelar_plugin_busca_producto = $('#forma-buscaproducto-window').find('#cencela');

                //funcionalidad botones
                $buscar_plugin_producto.mouseover(function(){
                                $(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
                });
                $buscar_plugin_producto.mouseout(function(){
                                $(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
                });

                $cancelar_plugin_busca_producto.mouseover(function(){
                                $(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
                });
                $cancelar_plugin_busca_producto.mouseout(function(){
                                $(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
                });

                //buscar todos los tipos de productos
                var input_json_tipos = config.getUrlForGetAndPost() + '/getProductoTipos.json';
                $arreglo = { iu:config.getUi() 		};
                $.post(input_json_tipos,$arreglo,function(data){
                                //Llena el select tipos de productos en el buscador
                                $select_tipo_producto.children().remove();
                                var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
                                $.each(data['prodTipos'],function(entryIndex,pt){
                                                prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                                });
                                $select_tipo_producto.append(prod_tipos_html);
                });

                //Aqui asigno al campo sku del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
                $campo_sku.val(sku_buscar);

						//click buscar productos
                $buscar_plugin_producto.click(function(event){
                    //event.preventDefault();
                    $tabla_resultados.children().remove();
                    var restful_json_service = config.getUrlForGetAndPost()+'/get_buscador_productos.json';
                    arreglo_parametros = {
                            sku:$campo_sku.val(),
                            tipo:$select_tipo_producto.val(),
                            descripcion:$campo_descripcion.val(),
                            iu:config.getUi()
                        };
                    var trr = '';
                    $.post(restful_json_service,arreglo_parametros,function(entry){

                        $.each(entry['productos'],function(entryIndex,producto){
                            trr = '<tr>';
                                trr += '<td width="120">';
                                                trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
                                                trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
                                trr += '</td>';
                                trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
                                trr += '<td width="90">';
                                                trr += '<span class="unidad_id" style="display:none;">'+producto['unidad_id']+'</span>';
                                                trr += '<span class="utitulo">'+producto['unidad']+'</span>';
                                trr += '</td>';
                                trr += '<td width="90"><span class="tipo_prod_buscador">'+producto['tipo']+'</span></td>';
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
                            //asignar a los campos correspondientes el sku y y descripcion
                            $('#forma-cotizacions-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
                            $('#forma-cotizacions-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
                            //elimina la ventana de busqueda
                            var remove = function() {$(this).remove();};
                            $('#forma-buscaproducto-overlay').fadeOut(remove);
                            //asignar el enfoque al campo sku del producto
                            $('#forma-cotizacions-window').find('input[name=sku_producto]').focus();

                            $Nombre_Producto.val($(this).find('span.titulo_prod_buscador').html());
                        });

                    });//termina llamada json
                });

                //si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
                if($campo_sku.val() != ''){
                                $buscar_plugin_producto.trigger('click');
                }

                $cancelar_plugin_busca_producto.click(function(event){
                                //event.preventDefault();
                                var remove = function() {$(this).remove();};
                                $('#forma-buscaproducto-overlay').fadeOut(remove);
                });
            }//termina buscador de productos

            //click generar reporte de pronostico de Cobranza
            $genera_reporte_ventas_netasproductofactura.click(function(event){
                    event.preventDefault();

                    var tipo_reporte=$select_tipo_reporte.val();
                    var proveedor=$Nombre_Proveedor.val();
                    var producto=$Nombre_Producto.val();
                    var fecha_inicial = $fecha_inicial.val();
                    var fecha_final = $fecha_final.val();
                    var usuario=config.getUi();
                    var cadena = tipo_reporte+"___"+proveedor+"___"+producto+"___"+fecha_inicial+"___"+fecha_final+"___"+usuario

                    if(fecha_inicial != 0 && fecha_final !=0){
                            var input_json = config.getUrlForGetAndPost() + '/getrepestadisticocompras/'+cadena+'/out.json';
                            window.location.href=input_json;
                    }else{
                            jAlert("Debe elegir el rango la fecha inicial y su fecha final par la busqueda","Atencion!!!")
                    }
            });
			
            $Buscar_productos.click(function(event){
                    event.preventDefault();
                    busca_productos();
            });

			
            $busca_proveedores = function(){
                    $(this).modalPanel_Buscaprov();
                    var $dialogoc =  $('#forma-buscaproveedor-window');
                    $dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
                    $('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -200  });

                    var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
					var $campo_no_proveedor = $('#forma-buscaproveedor-window').find('input[name=campo_no_proveedor]');
					var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
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
                        $arreglo = {
                                        rfc:$campo_rfc.val(),
                                        no_proveedor:$campo_no_proveedor.val(),
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

                    //seleccionar un producto del grid de resultados
                    $tabla_resultados.find('tr').click(function(){
                            //asigna la razon social del proveedor al campo correspondiente
                            $Nombre_Proveedor.val($(this).find('#razon_social').html());
                            $id_proveedor_edo_cta.val($(this).find('#id_prov').val());
                            //elimina la ventana de busqueda
                            var remove = function() { $(this).remove(); };
                            $('#forma-buscaproveedor-overlay').fadeOut(remove);
                    });
                });
            });

            $cancelar_plugin_busca_proveedor.click(function(event){
                            //event.preventDefault();
                            var remove = function() { $(this).remove(); };
                            $('#forma-buscaproveedor-overlay').fadeOut(remove);
            });
        }//termina buscador de proveedores

        $Buscar_proveedor.click(function(event){
                //alert("aqui ando");
                event.preventDefault();
                $busca_proveedores();//llamada a la funcion que busca proveedores
        });


        $Buscar_estadistico_compras.click(function(event){
            event.preventDefault();
            if($fecha_inicial.val()=='' || $fecha_final.val()==''){

                jAlert("Seleccione una rango de fechas",'Atencion!');

            }else{
                $div_ventas_netas_productofactura.children().remove();
                if ($select_tipo_reporte.val() == 1 || $select_tipo_reporte.val() == 2){

                        var arreglo_parametros = {	tipo_reporte: $select_tipo_reporte.val(),
                                                        proveedor: $Nombre_Proveedor.val(),
                                                        producto: $Nombre_Producto.val(),
                                                        fecha_inicial: $fecha_inicial.val(),
                                                        fecha_final: $fecha_final.val(),
                                                        iu:config.getUi()
                                                 };

                        var restful_json_service = config.getUrlForGetAndPost() + '/getEstadisticoCompras/out.json'
                        var proveedor="";
                        $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;




			//var footer_tabla = entry['Totales'];
			var header_tabla = {
                                        clave_proveedor     :'Clave',
                                        proveedor           :'Proveedor',
                                        codigo_producto     :'Codigo',
                                        producto            :'Producto',
                                        factura             :'Factura',
                                        fecha               :'Fecha',
                                        unidad              :'Unidad',
                                        cantidad            :'Cantidad',
                                        moneda_costo        :'',
                                        costo_unitario      :'Costo Unitario',
                                        moneda              :'Moneda',
                                        moneda_tipo_cambio  :'',
                                        tipo_cambio         :'Tipo Cambio',
                                        moneda_compra_neta  :'',
                                        compra_neta_mn      :'Compra Neta en M.N.'
			};

			var header_clave_codigo="";
			var header_producto_proveedor="";

			if(parseInt($select_tipo_reporte.val())==1){
				header_clave_codigo="Clave";
				header_producto_proveedor="Proveedor";
			}

                        if(parseInt($select_tipo_reporte.val())==2){
				header_clave_codigo="Codigo";
				header_producto_proveedor="Producto";
			}

			var html_reporte = '<table id="reporte">';
			var html_fila_vacia='';
			var html_footer = '';

			html_reporte +='<thead> <tr>';
			for(var key in header_tabla){
				var attrValue = header_tabla[key];
				if(attrValue == header_clave_codigo){
					html_reporte +='<td width="80px" align="left">'+attrValue+'</td>';
				}

				if(attrValue == header_producto_proveedor){
					html_reporte +='<td width="200px" align="left">'+attrValue+'</td>';
				}

				if(attrValue == "Factura"){
					html_reporte +='<td width="90px" align="left">'+attrValue+'</td>';
				}

				if(attrValue == "Fecha"){
					html_reporte +='<td width="80px" align="left">'+attrValue+'</td>';
				}
				if(attrValue == "Unidad"){
					html_reporte +='<td width="70px" align="left">'+attrValue+'</td>';
				}
				if(attrValue == "Cantidad"){
					html_reporte +='<td width="80px" align="left">'+attrValue+'</td>';
				}

				if(attrValue == ''){
					html_reporte +='<td width="10px" align="right" >'+attrValue+'</td>';
				}

				if(attrValue == "Costo Unitario"){
					html_reporte +='<td width="90px" align="left" id="monto">'+attrValue+'</td>';
				}

				if(attrValue == "Moneda"){
					html_reporte +='<td width="60px" align="left">'+attrValue+'</td>';
				}

				if(attrValue == "Tipo Cambio"){
					html_reporte +='<td width="80px" align="left"  id="monto">'+attrValue+'</td>';
				}

				if(attrValue == "Compra Neta en M.N."){
					html_reporte +='<td width="130px" align="left" id="monto">'+attrValue+'</td>';
				}

			}
			html_reporte +='</tr> </thead>';

			html_fila_vacia +='<tr>';
			html_fila_vacia +='<td width="80px" align="left"  id="sin_borde" height="10"></td>';
			html_fila_vacia +='<td width="200px" align="left"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="left" id="sin_borde"></td>';
			html_fila_vacia +='<td width="80px" align="center" id="sin_borde"></td>';
			html_fila_vacia +='<td width="70px" align="left" id="sin_borde"></td>';
			html_fila_vacia +='<td width="80px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="90px" align="right"  id="sin_borde"></td>';
			html_fila_vacia +='<td width="60px" align="left" id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="80px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="10px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='<td width="130px" align="right" id="sin_borde"></td>';
			html_fila_vacia +='</tr>';

			var simbolo_moneda="";
			//esta variable toma el campo de ordenamiento dependiendo del tipo de reporte
			var campo_ordenamiento_actual="";

			//inicializar variables
			var suma_parcial=0.0;//esta variable es para las sumas parciales, puede ser por producto o proveedor dependiento del tipo de reporte
			var suma_general=0.0;
			var suma_cantidad_unidad=0.0;

                                if(parseInt(body_tabla.length)>0){

                                        campo_proveedor_producto_actual="";
                                        var unidad="";
                                        //estas dos variables indican el indice que debe tomar del arreglo dependiendo del timpo de reporte
                                        var campo_clave_codigo="";
                                        var campo_proveedor_producto="";
                                        var campo_comparador="";

                                        if(parseInt($select_tipo_reporte.val())==1){
                                                campo_proveedor_producto_actual=body_tabla[0]["producto"];
                                                unidad=body_tabla[0]["unidad"];
                                        }
                                        if(parseInt($select_tipo_reporte.val())==2){
                                                campo_proveedor_producto_actual=body_tabla[0]["razon_social"];
                                                unidad=body_tabla[0]["unidad"];
                                        }

                                        html_reporte +='<tr id="tr_totales"><td align="left" colspan="13">'+campo_proveedor_producto_actual+'</td></tr>';
                                        for(var i=0; i<body_tabla.length; i++){

                                                if(parseInt($select_tipo_reporte.val())==1){
                                                        campo_clave_codigo="folio";
                                                        campo_proveedor_producto="razon_social";
                                                        campo_comparador="producto";

                                                }
                                                if(parseInt($select_tipo_reporte.val())==2){
                                                        campo_clave_codigo="codigo";
                                                        campo_proveedor_producto="producto";
                                                        campo_comparador="razon_social";

                                                }

                                                if(body_tabla[i]["moneda"]=="M.N."){
                                                        simbolo_moneda="$";
                                                }else{
                                                        simbolo_moneda=body_tabla[i]["moneda"];
                                                }


                                                if(campo_proveedor_producto_actual == body_tabla[i][campo_comparador] && unidad == body_tabla[i]["unidad"] ){
                                                        html_reporte +='<tr>';
                                                        html_reporte +='<td width="80 px" align="left" >'+body_tabla[i][campo_clave_codigo]+'</td>';
                                                        html_reporte +='<td width="200px" align="left" >'+body_tabla[i][campo_proveedor_producto]+'</td>';
                                                        html_reporte +='<td width="90 px" align="left" >'+body_tabla[i]["factura"]+'</td>';
                                                        html_reporte +='<td width="80 px" align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
                                                        html_reporte +='<td width="70 px" align="left" >'+body_tabla[i]["unidad"]+'</td>';
                                                        html_reporte +='<td width="80 px" align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                        html_reporte +='<td width="10 px" align="left" id="simbolo_moneda">'+simbolo_moneda+'</td>';
                                                        html_reporte +='<td width="90 px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
                                                        html_reporte +='<td width="60 px" align="left" >'+body_tabla[i]["moneda"]+'</td>';
                                                        html_reporte +='<td width="10 px" align="left" id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td width="80 px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(4))+'</td>';
                                                        html_reporte +='<td width="10 px" align="left" id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td width="130px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
                                                        html_reporte +='</tr>';

                                                        suma_parcial=parseFloat(suma_parcial) + parseFloat(body_tabla[i]["total_pesos"]);
                                                        suma_general=parseFloat(suma_general) + parseFloat(body_tabla[i]["total_pesos"]);

                                                        suma_cantidad_unidad=parseFloat(suma_cantidad_unidad) + parseFloat(body_tabla[i]["cantidad"]);
                                                        unidad=body_tabla[i]["unidad"];


                                                }else{
                                                        //imprimir totales
                                                        html_reporte +='<tr id="tr_totales ">';
                                                        html_reporte +='<td align="left"   id="sin_borde_derecho" ></td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="right" id="sin_borde" >Total:</td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" >'+unidad+'</td>';
                                                        html_reporte +='<td align="right"   id="sin_borde" >'+$(this).agregar_comas(parseFloat(suma_cantidad_unidad).toFixed(2))+'</td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="right"  id="sin_borde" ></td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="left"   id="sin_borde" ></td>';
                                                        html_reporte +='<td align="right"  id="sin_borde" >Total</td>';
                                                        html_reporte +='<td align="left"   id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td align="right"  id="monto">'+$(this).agregar_comas(parseFloat(suma_parcial).toFixed(2))+'</td>';
                                                        html_reporte +='</tr>';

                                                        //fila vacia
                                                        html_reporte +=html_fila_vacia;

                                                        //reinicializar varibles
                                                        unidad="";
                                                        suma_parcial=0.0;
                                                        suma_cantidad_unidad=0.0;


                                                        //tomar razon social del proveedor o descripcion del producto, dependiendo del timpo de reporte
                                                        campo_proveedor_producto_actual=body_tabla[i][campo_comparador];

                                                        html_reporte +='<tr id="tr_totales"><td align="left" colspan="13">'+campo_proveedor_producto_actual+'</td></tr>';
                                                        //crear primer registro del nuevo proveedor
                                                        html_reporte +='<tr>';
                                                        html_reporte +='<td width="80px" align="left" >'+body_tabla[i][campo_clave_codigo]+'</td>';
                                                        html_reporte +='<td width="200px" align="left" >'+body_tabla[i][campo_proveedor_producto]+'</td>';
                                                        html_reporte +='<td width="90px" align="left" >'+body_tabla[i]["factura"]+'</td>';
                                                        html_reporte +='<td width="80px" align="center" >'+body_tabla[i]["fecha_factura"]+'</td>';
                                                        html_reporte +='<td width="70px" align="left" >'+body_tabla[i]["unidad"]+'</td>';
                                                        html_reporte +='<td width="80px" align="right" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]).toFixed(2))+'</td>';
                                                        html_reporte +='<td width="10px" align="left" id="simbolo_moneda">'+simbolo_moneda+'</td>';
                                                        html_reporte +='<td width="90px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>';
                                                        html_reporte +='<td width="60px" align="left" >'+body_tabla[i]["moneda"]+'</td>';
                                                        html_reporte +='<td width="10px" align="left" id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td width="80px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["tipo_cambio"]).toFixed(4))+'</td>';
                                                        html_reporte +='<td width="10px" align="left" id="simbolo_moneda">$</td>';
                                                        html_reporte +='<td width="130px" align="right" id="monto">'+$(this).agregar_comas(parseFloat(body_tabla[i]["total_pesos"]).toFixed(2))+'</td>';
                                                        html_reporte +='</tr>';

                                                        suma_parcial=parseFloat(suma_parcial) + parseFloat(body_tabla[i]["total_pesos"]);
                                                        suma_general=parseFloat(suma_general) + parseFloat(body_tabla[i]["total_pesos"]);
                                                        unidad=body_tabla[i]["unidad"]
                                                        suma_cantidad_unidad=parseFloat(suma_cantidad_unidad) + parseFloat(body_tabla[i]["cantidad"]);
                                                }
                                        }
                                        //imprimir total del ultimo provedor o producto
                                        html_reporte +='<tr id="tr_totales">';
                                        html_reporte +='<td align="left" id="sin_borde_derecho"></td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="right" id="sin_borde">Total:</td>';
                                        html_reporte +='<td align="left" id="sin_borde">'+unidad+'</td>';
                                        html_reporte +='<td align="right" id="sin_borde">'+$(this).agregar_comas(parseFloat(suma_cantidad_unidad).toFixed(2))+'</td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="right" id="sin_borde"></td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="left" id="sin_borde"></td>';
                                        html_reporte +='<td align="right" id="sin_borde">Total</td>';
                                        html_reporte +='<td align="left" id="simbolo_moneda">$</td>';
                                        html_reporte +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_parcial).toFixed(2))+'</td>';
                                        html_reporte +='</tr>';
                               }

                                //fila vacia
                                html_reporte +=html_fila_vacia;

                                html_footer +='<tr id="tr_totales">';
                                html_footer +='<td align="left" id="sin_borde_derecho"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="center" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="right" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="left" id="sin_borde"></td>';
                                html_footer +='<td align="right" id="sin_borde">Total General:</td>';
                                html_footer +='<td align="left" id="simbolo_moneda">$</td>';
                                html_footer +='<td align="right" id="monto">'+$(this).agregar_comas(parseFloat(suma_general).toFixed(2))+'</td>';
                                html_footer +='</tr>';

                                html_reporte +='<tfoot>';
                                        html_reporte += html_footer;
                                html_reporte +='</tfoot>';

                                html_reporte += '</table>';

                                $div_ventas_netas_productofactura.append(html_reporte);

                                var height2 = $('#cuerpo').css('height');
                                var alto = parseInt(height2)-350;
                                var pix_alto=alto+'px';
                                $('#reporte').tableScroll({height:parseInt(pix_alto)});

                        });//fin del jsonnnnn
                }


                //MUESTRA LOS PRODUCTOS SUMARIZADO
                       if ($select_tipo_reporte.val() == 3 || $select_tipo_reporte.val() == 4 ){
                        $div_ventas_netas_productofactura.children().remove();
                        var tipo_reporte= $select_tipo_reporte.val();
                        var proveedor = $Nombre_Proveedor.val();
                        var producto = $Nombre_Producto.val();
                        var fecha_inicial = $fecha_inicial.val();
                        var fecha_final = $fecha_final.val();
                        var usuario = config.getUi();

			if(fecha_inicial != "" && fecha_final != ""){
				var arreglo_parametros = {
                                    tipo_reporte: $select_tipo_reporte.val(),
                                    proveedor: $Nombre_Proveedor.val(),
                                    producto: $Nombre_Producto.val(),
                                    fecha_inicial: $fecha_inicial.val(),
                                    fecha_final: $fecha_final.val(),
                                    iu:config.getUi()
                                };

                                var restful_json_service = config.getUrlForGetAndPost() + '/getEstadisticoCompras/out.json'

                                var unidad="";
                                var cantidad=0.0;

                                var compra_neta=0.0;
                                var contador_costo_unitario=0;
                                var costo_unitario=0.0;
                                var costo_promedio=0.0;
                                var indice_extraido = "";

				$.post(restful_json_service,arreglo_parametros,function(entry){
					var body_tabla = entry;

					var header_tabla = {
                                                            Producto    :'Producto',
                                                            proveedor    :'Proveedor',
                                                            Unidad : 'Unidad',
                                                            Cantidad : 'Cantidad',
                                                            Monedapu    :"",
                                                            Precio_unitario : 'P.Promedio',
                                                            Monedavn    :"",
                                                            Venta_Neta : 'V.Total'
					};

					var totalpesos = 0.0;
					var totalxproducto= 0.0;
					var tmp = 0;

                                        var suma_general=0.0;
                                        var producto_proveedor = "";
                                        var html_footer ="";
					var html_comprasnetas = '<table id="ventas" width="100%" >';

                                                html_comprasnetas +='<thead> <tr>';


						for(var key in header_tabla){
							var attrValue = header_tabla[key];
                                                        //html_ventasnetas +='<td  align="left">'+attrValue+'</td>';
                                                        if($select_tipo_reporte.val() == 3){
                                                            if(attrValue == "Producto"){
                                                                    html_comprasnetas +='<td  width="50px" align="left" >'+attrValue+'</td>';
                                                            }
                                                        }
                                                        if($select_tipo_reporte.val() == 4){
                                                            if(attrValue == "Proveedor"){
                                                                    html_comprasnetas +='<td  width="50px" align="left" >'+attrValue+'</td>';
                                                            }
                                                        }

							if(attrValue == "Unidad"){
								html_comprasnetas +='<td  align="right" >'+attrValue+'</td>';
							}
							if(attrValue == "Cantidad"){
								html_comprasnetas +='<td  align="right" >'+attrValue+'</td>';
							}
							if(attrValue == ""){
								html_comprasnetas +='<td width="8px" align="right" >'+attrValue+'</td>';
							}
							if(attrValue == "P.Promedio"){
								html_comprasnetas +='<td   align="right" >'+attrValue+'</td>';
							}

							if(attrValue == "V.Total"){
								html_comprasnetas +='<td  align="right" >'+attrValue+'</td>';
							}
						}
                                                html_comprasnetas +='</tr> </thead>';


                                                if(parseInt(body_tabla.length) > 0){
                                                    if($select_tipo_reporte.val() == 3){
                                                            producto_proveedor = body_tabla[0]["producto"];
                                                            unidad = body_tabla[0]["unidad"];
                                                            indice_extraido="producto";
                                                    }

                                                    if($select_tipo_reporte.val() == 4){
                                                            producto_proveedor = body_tabla[0]["razon_social"];
                                                            indice_extraido="razon_social";
                                                            unidad = body_tabla[0]["unidad"];
                                                    }
                                                }



                                                for(var i=0; i<body_tabla.length; i++){
                                                    if(producto_proveedor ==  body_tabla[i][indice_extraido] ){
                                                        if(unidad == body_tabla[i]["unidad"]){

                                                            cantidad                =cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                            costo_unitario          =costo_unitario +parseFloat(body_tabla[i]["precio_unitario"]);
                                                            compra_neta             = compra_neta+parseFloat(body_tabla[i]["total_pesos"]);
                                                            contador_costo_unitario =contador_costo_unitario+1;
                                                            costo_promedio=costo_unitario/contador_costo_unitario;
                                                            suma_general =suma_general +parseFloat(body_tabla[i]["total_pesos"]);


                                                        }else{

                                                            html_comprasnetas +='<tr>';
                                                                        html_comprasnetas +='<td width="300px" align="left" >'+producto_proveedor+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                        html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(costo_promedio).toFixed(2))+'</td>';
                                                                        html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(compra_neta).toFixed(2))+'</td>';
                                                             html_comprasnetas +='</tr>';


                                                            //reinicio varibles
                                                            cantidad                =0.0;
                                                            costo_unitario          =0.0;
                                                            compra_neta             = 0.0;
                                                            contador_costo_unitario =0.0;
                                                            costo_promedio=0.0;

                                                            //producto_proveedor      =body_tabla[i][indice_extraido];
                                                            //unidad                  =body_tabla[i]["unidad"];
                                                            cantidad                =cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                            costo_unitario          =costo_unitario +parseFloat(body_tabla[i]["precio_unitario"]);
                                                            compra_neta             = compra_neta+parseFloat(body_tabla[i]["total_pesos"]);
                                                            contador_costo_unitario =contador_costo_unitario+1;
                                                            costo_promedio=costo_unitario/contador_costo_unitario;
                                                            suma_general =suma_general +parseFloat(body_tabla[i]["total_pesos"]);

                                                        }
                                                        producto_proveedor      =body_tabla[i][indice_extraido];
                                                        unidad                  =body_tabla[i]["unidad"];




                                                    }else{

                                                        //entra aqu cuando el proveedor es diferente   pintala sumatoria delproveedorr anterior
                                                       html_comprasnetas +='<tr>';
                                                                        html_comprasnetas +='<td width="300px" align="left" >'+producto_proveedor+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                        html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(costo_promedio).toFixed(2))+'</td>';
                                                                        html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>';
                                                                        html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(compra_neta).toFixed(2))+'</td>';
                                                        html_comprasnetas +='</tr>';
                                                        //fin del primer proveedor
                                                        //reinicio variables
                                                        cantidad                =0.0;
                                                        costo_unitario          =0.0;
                                                        compra_neta             = 0.0;
                                                        contador_costo_unitario =0.0;
                                                        costo_promedio=0.0;
                                                        //pint al nuevo proveedor


                                                        cantidad                =cantidad + parseFloat(body_tabla[i]["cantidad"]);
                                                        costo_unitario          =costo_unitario +parseFloat(body_tabla[i]["precio_unitario"]);
                                                        compra_neta             = compra_neta+parseFloat(body_tabla[i]["total_pesos"]);
                                                        contador_costo_unitario =contador_costo_unitario+1;
                                                        costo_promedio=costo_unitario/contador_costo_unitario;
                                                        suma_general =suma_general +parseFloat(body_tabla[i]["total_pesos"]);





                                                    }
                                                    producto_proveedor      =body_tabla[i][indice_extraido];
                                                    unidad                  =body_tabla[i]["unidad"];

                                                }
                                                        html_comprasnetas +='<tr>';
                                                                 html_comprasnetas +='<td width="300px" align="left" >'+producto_proveedor+'</td>';
                                                                 html_comprasnetas +='<td  align="right" >'+unidad+'</td>';
                                                                 html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
                                                                 html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>';
                                                                 html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(costo_promedio).toFixed(2))+'</td>';
                                                                 html_comprasnetas +='<td widht="5px" align="right">'+"$"+'</td>';
                                                                 html_comprasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(compra_neta).toFixed(2))+'</td>';
                                                        html_comprasnetas +='</tr>';

                                                        html_footer +='<tr id="tr_totales">';
                                                        html_footer +='<td width="500px" id="sin_borde_derecho">&nbsp;</td>';

                                                        html_footer +='<td lign="right" id="sin_borde">&nbsp;</td>';
                                                        html_footer +='<td lign="right" id="sin_borde">&nbsp;</td>';
                                                        html_footer +='<td widht="5px"  align="right" id="sin_borde"></td>';
                                                        html_footer +='<td align="left" id="sin_borde">&nbsp;</td>';
                                                        html_footer +='<td align="right" id="sin_borde">Total&nbsp;General:</td>';
                                                        html_footer +='<td widht="5px" align="right" id="simbolo_moneda">$</td>';
                                                        html_footer +='<td align="right" id="monto" id="sin_borde">'+$(this).agregar_comas(parseFloat(suma_general).toFixed(2))+'</td>';
                                                        html_footer +='</tr>';

                                                        html_comprasnetas +='<tfoot>';
                                                                html_comprasnetas += html_footer;
                                                        html_comprasnetas +='</tfoot>';

                                                        html_comprasnetas += '</table>';

                                                $div_ventas_netas_productofactura.append(html_comprasnetas);
                                                var height2 = $('#cuerpo').css('height');
                                                var alto = parseInt(height2)-350;
                                                var pix_alto=alto+'px';
                                                $('#ventas').tableScroll({height:parseInt(pix_alto)});
				});
			}else{
				jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
			}

                }//FIN DE LA VISTA DE PRODUCTO SUMARIZADOs

            }

	});//fin del evento click al mostrar el reporte en pantalla
});





