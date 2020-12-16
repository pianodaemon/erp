$(function() {
    var config =  {
        tituloApp: document.title,
        contextpath : $('#lienzo_recalculable').find('input[name=contextpath]').val(),
        userName : $('#lienzo_recalculable').find('input[name=user]').val(),
        ui : $('#lienzo_recalculable').find('input[name=iu]').val(),
        empresa:$('#lienzo_recalculable').find('input[name=emp]').val(),
        sucursal:$('#lienzo_recalculable').find('input[name=suc]').val(),
        
        getUrlForGetAndPost : function(){
            var url = document.location.protocol + '//' + document.location.host + this.getController();
            return url;
        },
        
        getController: function(){
            return this.contextpath + "/controllers/cxcrepcomercial";
        //  return this.controller;
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
        }
    };

	//desencadena evento del $campo_ejecutar al pulsar Enter en $campo
	$aplicar_evento_keypress = function($campo, $campo_ejecutar){
		$campo.keypress(function(e){
			if(e.which == 13){
				$campo_ejecutar.trigger('click');
				return false;
			}
		});
	}
	
	
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
    var $select_agente       = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=select_agente]');
    //var $select_agente_id       = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=id_agente]');
    var $select_tipo_reporte = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=ventas]');
    var $select_tipo_costo = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=costo]');
    var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
    var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
    var $Nombre_Cliente= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('input[name=nombrecliente]');
    var $Nombre_Producto= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('input[name=nombreproducto]');	
	var $select_segmento = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=segmento]');
	var $select_mercado = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=mercado]');
	
    var $select_linea = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=linea]');
    var $select_marca = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=marca]');
    var $select_familia = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=familia]');
    var $select_subfamilia = $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('select[name=subfamilia]');
    
    var $genera_reporte_ventas_netasproductofactura= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('input[value$=PDF]');
    var $Buscar_ventas_netasproductofactura= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('input[value$=Buscar]');
    
    var agent = '<option value= "0" >[--Todos--]</option>';
    $select_agente.append(agent);

    var restful_json_service = config.getUrlForGetAndPost() +'/get_cargando_filtros.json';
    arreglo_parametros ={
        linea:$select_linea.val(),
        marca:$select_marca.val(),
        familia:$select_familia.val(),
        subfamilia:$select_subfamilia.val(),
        id_agente :$select_agente.val(),
        iu:config.getUi()
    };
    
    $.post(restful_json_service,arreglo_parametros,function(data){
		$select_segmento.children().remove();
        var segmento_html = '<option value="0" selected="yes">[-- Seleccionar --]</option>';
        $.each(data['Segmentos'],function(entryIndex,clas1){
            segmento_html += '<option value="' + clas1['id'] + '"  >' + clas1['clasificacion1'] + '</option>';
        });
        $select_segmento.append(segmento_html);
        
		$select_mercado.children().remove();
        var mercado_html = '<option value="0" selected="yes">[-- Seleccionar --]</option>';
        $.each(data['Mercados'],function(entryIndex,clas2){
            mercado_html += '<option value="' + clas2['id'] + '"  >' + clas2['clasificacion2'] + '</option>';
        });
        $select_mercado.append(mercado_html);
        
        //cargar select de agentes
		$select_agente.children().remove();
		var agente_hmtl = '<option value= "0" >[--Todos--]</option>';
		$.each(data['agentes'],function(entryIndex,data){
			agente_hmtl +='<option value= "' + data['id'] + '" >' + data['nombre_agente'] + '</option>';
		});
		$select_agente.append(agente_hmtl);
        
        //Llena el select lineas
        $select_linea.children().remove();

        var lineas_html = '<option value="0" selected="yes">[-- Seleccionar --]</option>';
        $.each(data['lineas'],function(entryIndex,pt){
            lineas_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
        });
        $select_linea.append(lineas_html);
        
        $select_marca.children().remove();
        var marcas_html = '<option value="0" selected="yes">[-- Seleccionar --]</option>';
        $.each(data['marcas'],function(entryIndex,pt){
            marcas_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
        });
        $select_marca.append(marcas_html);

        $select_familia.children().remove();
        var familias_html = '<option value="0" selected="yes">[-- Seleccionar --]</option>';
        $.each(data['familias'],function(entryIndex,pt){
            familias_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
        });
        $select_familia.append(familias_html);
    });

    $select_familia.change(function(){
        //var id_familia = $(this).val();
        //alert("id_Familia::  "+id_familia);
        var restful_json_service = config.getUrlForGetAndPost() +'/get_cargando_filtros.json';
        arreglo_parametros ={
            linea:$select_linea.val(),
            marca:$select_marca.val(),
            familia:$select_familia.val(),
            subfamilia:$select_subfamilia.val(),
            id_agente :$select_agente.val(),
            iu:config.getUi()
        };

        $.post(restful_json_service,arreglo_parametros,function(data){
            $select_subfamilia.children().remove();
            var subfamilias_html = '<option value="0" selected="yes">[--Seleccionar Familia--]</option>';
            $.each(data['subfamilias'],function(entryIndex,pt){
                subfamilias_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
            });
            $select_subfamilia.append(subfamilias_html);

        });
    });

    $fecha_inicial.attr({'readOnly':true});
    $fecha_final.attr({'readOnly':true});
    //$Nombre_Cliente.attr({'readOnly':true});
    //$Nombre_Producto.attr({'readOnly':true});
    
    var $Buscar_clientes= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('a[href*=busca_cliente]');
    var $Buscar_productos= $('#lienzo_recalculable').find('div.repventasnetasproductofactura').find('table#fechas tr td').find('a[href*=busca_producto]');
    var $div_ventas_netas_productofactura= $('#ventasnetasproductofactura');
    
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
        a.css({
            'z-index':100
        });
    });
	
	//Mostrar la fecha actual
    $fecha_inicial.val(mostrarFecha());
    
    
	$fecha_inicial.val(mostrarFecha());
	var fragmentoTexto1 = $fecha_inicial.val().split('-')[0];
	var fragmentoTexto2 = $fecha_inicial.val().split('-')[1];
	var fragmentoTexto4 = '01';
	var fechain =  fragmentoTexto1+"-"+fragmentoTexto2+"-"+fragmentoTexto4;
	
	//alert(fechain);
	$fecha_inicial.val(fechain);
    
    
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
	
	
	//Mostrar la fecha actual
	$fecha_final.val(mostrarFecha());
	
    $fecha_final.click(function (s){
        var a=$('div.datepicker');
        a.css({
            'z-index':100
        });
    });
    
    //buscador de productos
    busca_productos = function($Nombre_Producto){
        $(this).modalPanel_Buscaproducto();
        var $dialogoc =  $('#forma-buscaproducto-window');
        $dialogoc.append($('div.buscador_productos').find('table.formaBusqueda_productos').clone());

        $('#forma-buscaproducto-window').css({
            "margin-left": -200,
            "margin-top": -200
        });

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
        $arreglo = {
            iu:config.getUi()
        };
        $.post(input_json_tipos,$arreglo,function(data){
            //Llena el select tipos de productos en el buscador
            $select_tipo_producto.children().remove();
            var prod_tipos_html = '<option value="0" selected="yes">[--Seleccionar Tipo--]</option>';
            $.each(data['prodTipos'],function(entryIndex,pt){
                prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
            });
            $select_tipo_producto.append(prod_tipos_html);
        });

        //Aqui asigno al campo Descripcion del buscador si el usuario ingresó un sku antes de hacer clic en buscar en la ventana principal
        $campo_descripcion.val($Nombre_Producto.val());

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
                $tabla_resultados.find('tr:odd').find('td').css({
                    'background-color' : '#e7e8ea'
                });
                $tabla_resultados.find('tr:even').find('td').css({
                    'background-color' : '#FFFFFF'
                });

                $('tr:odd' , $tabla_resultados).hover(function () {
                    $(this).find('td').css({
                        background : '#FBD850'
                    });
                }, function() {
                    //$(this).find('td').css({'background-color':'#DDECFF'});
                    $(this).find('td').css({
                        'background-color':'#e7e8ea'
                    });
                });
                $('tr:even' , $tabla_resultados).hover(function () {
                    $(this).find('td').css({
                        'background-color':'#FBD850'
                    });
                }, function() {
                    $(this).find('td').css({
                        'background-color':'#FFFFFF'
                    });
                });

                //seleccionar un producto del grid de resultados
                $tabla_resultados.find('tr').click(function(){
                    //asignar a los campos correspondientes el sku y y descripcion
                    $('#forma-cotizacions-window').find('input[name=sku_producto]').val($(this).find('span.sku_prod_buscador').html());
                    $('#forma-cotizacions-window').find('input[name=nombre_producto]').val($(this).find('span.titulo_prod_buscador').html());
                    //elimina la ventana de busqueda
                    var remove = function() {
                        $(this).remove();
                    };
                    $('#forma-buscaproducto-overlay').fadeOut(remove);
                    //asignar el enfoque al campo sku del producto
                    $('#forma-cotizacions-window').find('input[name=sku_producto]').focus();

                    $Nombre_Producto.val($(this).find('span.titulo_prod_buscador').html());
                    
                    $Nombre_Producto.focus();
                });

            });//termina llamada json
        });

        //si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
        if($campo_descripcion.val() != ''){
            $buscar_plugin_producto.trigger('click');
        }
        
		$aplicar_evento_keypress($campo_sku, $buscar_plugin_producto);
		$aplicar_evento_keypress($select_tipo_producto, $buscar_plugin_producto);
		$aplicar_evento_keypress($campo_descripcion, $buscar_plugin_producto);
		
        $cancelar_plugin_busca_producto.click(function(event){
            //event.preventDefault();
            var remove = function() {
                $(this).remove();
            };
            $('#forma-buscaproducto-overlay').fadeOut(remove);
            $Nombre_Producto.focus();
        });
    }//termina buscador de productos
    
    
    
    
    busca_clientes=function($Nombre_Cliente){
        $(this).modalPanel_Buscacliente();
        var $dialogoc =  $('#forma-buscacliente-window');
        //var $dialogoc.prependTo('#forma-buscaproduct-window');
        $dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
        $('#forma-buscacliente-window').css({
            "margin-left": -200,
            "margin-top": -180
        });

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
        html+='<option value="3" selected="yes">Razon social</option>';
        html+='<option value="4">CURP</option>';
        html+='<option value="5">Alias</option>';
        $select_filtro_por.append(html);
        
        
        $cadena_buscar.val($Nombre_Cliente.val());
        
        //click buscar clientes
        $busca_cliente_modalbox.click(function(event){
            //var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_clientes.json';
            var restful_json_service = config.getUrlForGetAndPost()+'/get_buscador_clientes.json';
            var  arreglo_parametros = {
                'cadena':$cadena_buscar.val(),
                'filtro':$select_filtro_por.val(),
                'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                }

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

                $tabla_resultados.find('tr:odd').find('td').css({
                    'background-color' : '#e7e8ea'
                });
                $tabla_resultados.find('tr:even').find('td').css({
                    'background-color' : '#FFFFFF'
                });

                $('tr:odd' , $tabla_resultados).hover(function () {
                    $(this).find('td').css({
                        background : '#FBD850'
                    });
                }, function() {
                    //$(this).find('td').css({'background-color':'#DDECFF'});
                    $(this).find('td').css({
                        'background-color':'#e7e8ea'
                    });
                });
                $('tr:even' , $tabla_resultados).hover(function () {
                    $(this).find('td').css({
                        'background-color':'#FBD850'
                    });
                }, function() {
                    $(this).find('td').css({
                        'background-color':'#FFFFFF'
                    });
                });

                //seleccionar un producto del grid de resultados
                $tabla_resultados.find('tr').click(function(){
                    //$('#forma-carteras-window').find('input[name=identificador_cliente]').val($(this).find('#idclient').val());
                    //$('#forma-carteras-window').find('input[name=rfccliente]').val($(this).find('span.rfc').html());
                    $('#forma-carteras-window').find('input[name=cliente]').val($(this).find('span.razon').html());

                    $('#forma-carteras-window').find('select[name=tipo_mov]').removeAttr('disabled');//habilitar select

                    $Nombre_Cliente.val($(this).find('span.razon').html());

                    //elimina la ventana de busqueda
                    var remove = function() {
                        $(this).remove();
                    };
                    $('#forma-buscacliente-overlay').fadeOut(remove);
                    
                    $Nombre_Cliente.focus();
                //asignar el enfoque al campo sku del producto
                });

            });
        });//termina llamada json
		
		
		if($cadena_buscar.val() != ''){
			$busca_cliente_modalbox.trigger('click');
		}
		
		$aplicar_evento_keypress($cadena_buscar, $busca_cliente_modalbox);
		$aplicar_evento_keypress($select_filtro_por, $busca_cliente_modalbox);
		
        $cancelar_plugin_busca_cliente.click(function(event){
            var remove = function() {
                $(this).remove();
            };
            $('#forma-buscacliente-overlay').fadeOut(remove);
            
            $Nombre_Cliente.focus();
        });
    }




    $Buscar_clientes.click(function(event){
        event.preventDefault();
        busca_clientes($Nombre_Cliente);

    });


    $Buscar_productos.click(function(event){
        event.preventDefault();
        busca_productos($Nombre_Producto);
    });
    
    
    
    //click generar el PDF del reporte
    $genera_reporte_ventas_netasproductofactura.click(function(event){
        event.preventDefault();

        var tipo_reporte=$select_tipo_reporte.val();
        var tipo_costo =$select_tipo_costo.val();
        var cliente=$Nombre_Cliente.val();
        var producto=$Nombre_Producto.val();
        var fecha_inicial = $fecha_inicial.val();
        var fecha_final = $fecha_final.val();
        var id_linea= $select_linea.val();
        var id_marca= $select_marca.val();
        var id_familia= $select_familia.val();
        var id_subfamilia= $select_subfamilia.val();
        var id_agente = $select_agente.val();
        var usuario=config.getUi();
        
        //Sustituir el signo % porque generan problemas al enviar la peticion Get, esto porque algunos productos tienen este signo
        producto = producto.replace("%","");
        
        var cadena = tipo_reporte+"___"+cliente+"___"+producto+"___"+fecha_inicial+"___"+fecha_final+"___"+usuario+"___"+id_linea+"___"+id_marca+"___"+id_familia+"___"+id_subfamilia+"___"+tipo_costo+"___"+id_agente+"___"+$select_segmento.val()+"___"+$select_mercado.val();

        if(fecha_inicial != 0 && fecha_final !=0){
            var input_json = config.getUrlForGetAndPost() + '/getrepventasnetasproductofactura/'+cadena+'/out.json';
            window.location.href=input_json;
        }else{
			jAlert('Debe elegir el rango la fecha inicial y su fecha final par la busqueda.', 'Atencion!', function(r) { 
				//$fecha_inicial.focus();
				$('#lienzo_recalculable').find('input[name=fecha_inicial]').trigger('click');
			});
        }
    });
    
    
    obtiene_total_venta = function( tipo_reporte,arraysumatorias,cliente_producto){
        var total_venta = 0.0;
        $.each(arraysumatorias,function(entryIndex, reg){
            //alert("El registro cliente es igual Cliente ???...."+reg['cliente'] +"  =  "+ cliente);
            if(tipo_reporte == 1 || tipo_reporte == 4){
                if(reg['cliente'] == cliente_producto ){
                    total_venta = reg['venta'];
                }
            }
            if(tipo_reporte == 2  || tipo_reporte == 3 ){
                if(reg['producto'] == cliente_producto ){
                    total_venta = reg['venta'];
                }
            }
        });
        //alert("Este es le total de la venta del cliente: "+cliente+"venta:"+total_venta);
        return total_venta;
    }





    $Buscar_ventas_netasproductofactura.click(function(event){
        event.preventDefault();
        
        if($select_tipo_reporte.val()== 1 ){
            $div_ventas_netas_productofactura.children().remove();
            
            if($fecha_inicial.val() != "" && $fecha_final.val() != ""){
                var arreglo_parametros = {
                    tipo_reporte : $select_tipo_reporte.val() ,
                    cliente : $Nombre_Cliente.val() ,
                    producto : $Nombre_Producto.val(),
                    fecha_inicial : $fecha_inicial.val() ,
                    fecha_final : $fecha_final.val(),
                    linea:$select_linea.val(),
                    marca:$select_marca.val(),
                    familia:$select_familia.val(),
                    subfamilia:$select_subfamilia.val(),
                    tipo_costo : $select_tipo_costo.val(),
                    id_agente : $select_agente.val(),
                    segmento : $select_segmento.val(),
                    mercado : $select_mercado.val(),
                    iu:config.getUi()
				};
                var restful_json_service = config.getUrlForGetAndPost() + '/getVentasNetasProductoFactura/out.json';

                var clie="";
                html_footer="";
                var totalponderacion = 0.0;
                var totalmop = 0.0;
                var totalmediamop = 0.0;
                var html_ventasnetas="";
                var totalventa=0.0;
                var ventageneral =0.0;
                var costogeneral =0.0;
                var cant_general=0.0;
                
                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var header_tabla = {
                        Codigo         :'Codigo',
                        Producto       :'Producto',
                        Factura        :'Factura',
                        Fecha_factura  :'Fecha&nbsp;Factura',
                        Unidad         :'Unidad',
                        Cantidad       :'Cantidad',
                        Monedapu       :'&nbsp;',
                        Precio_unitario:'P.&nbsp;Unitario',
                        Monedavn       :'&nbsp;',
                        Venta_Neta     :'V.&nbsp;Neta',
                        denominacion   :'Denom.',
                        Tipo_Cambio    :"T.&nbsp;Cambio",//de aqui para bajo es nuevo
                        /*
                        Monedacosto    :'&nbsp;',
                        Costo          :'Costo',
                        Ponderacion    :'POND',
                        Tipo_MOP       :'MOP',
                        Medi_MOP       :'M_MOP'
                        */
                    };

                    html_ventasnetas = '<table id="ventas" width="1300">';
                    html_ventasnetas +='<thead> <tr>';
                    for(var key in header_tabla){
                        var attrValue = header_tabla[key];
                        if(attrValue == "Codigo"){
                            html_ventasnetas +='<td align="left" width="80px">'+attrValue+'</td>';
                        }
                        if(attrValue == "Producto"){
                            html_ventasnetas +='<td align="left" width="200px">'+attrValue+'</td>';
                        }
                        if(attrValue == "Factura"){
                            html_ventasnetas +='<td align="center" width="70px">'+attrValue+'</td>';
                        }
                        if(attrValue == "Fecha&nbsp;Factura"){
                            html_ventasnetas +='<td align="center" width="80px">'+attrValue+'</td>';
                        }

                        if(attrValue == "Unidad"){
                            html_ventasnetas +='<td align="center" width="80px">'+attrValue+'</td>';
                        }
                        if(attrValue == "Cantidad"){
                            html_ventasnetas +='<td  align="center" width="80px">'+attrValue+'</td>';
                        }
						if(attrValue == '&nbsp;'){
						   html_ventasnetas +='<td width="25px" align="center" >'+attrValue+'</td>';
						}
                        if(attrValue == "P.&nbsp;Unitario"){
                            html_ventasnetas +='<td  align="center" width="80px">'+attrValue+'</td>';
                        }

                        if(attrValue == "V.&nbsp;Neta"){
                            html_ventasnetas +='<td  align="center" width="80px">'+attrValue+'</td>';
                        }
                        if(attrValue == "Denom."){
                            html_ventasnetas +='<td  align="center" width="40px">'+attrValue+'</td>';
                        }
                        if(attrValue == "T.&nbsp;Cambio"){
                            html_ventasnetas +='<td align="center" width="80px">'+attrValue+'</td>';
                        }
                        /*
                        if(attrValue == "Costo"){
                            html_ventasnetas +='<td align="center" width="80px">'+attrValue+'</td>';
                        }
                        if(attrValue == "POND"){
                            html_ventasnetas +='<td align="center" width="80px">'+attrValue+'</td>';
                        }
                        if(attrValue == "MOP"){
                            html_ventasnetas +='<td align="center" width="80px">'+attrValue+'</td>';
                        }
                        if(attrValue == "M_MOP"){
                            html_ventasnetas +='<td align="center" width="80px">'+attrValue+'</td>';
                        }
                        */
                    }
                    html_ventasnetas +='</tr> </thead>';

					html_ventasnetas +='<tr>';
					html_ventasnetas +='<td align="left"  id="sin_borde" width="80px" height="2"></td>';
					html_ventasnetas +='<td align="left"  id="sin_borde" width="200px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="70px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="25px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="25px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="40px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					/*
					html_ventasnetas +='<td align="right" id="sin_borde" width="25px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					*/
					html_ventasnetas +='</tr>';
					
                    if(entry['datos_normales'].length > 0 ){
                        clie = entry['datos_normales'][0]["razon_social"];
                        html_ventasnetas +='<tr class="first">';
                        html_ventasnetas +='<td align="left" colspan ="8" width="695px"> <strong>'+clie+'</strong></td>'
						html_ventasnetas +='<td align="right" width="25px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="40px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						/*
						html_ventasnetas +='<td align="right" width="25px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						*/
                        html_ventasnetas +='</tr>';

                        totalventa=obtiene_total_venta($select_tipo_reporte.val(),entry['totales'],clie);

                        for(var i=0; i<entry['datos_normales'].length; i++){
                            if(clie == entry['datos_normales'][i]["razon_social"] ){
                                if(entry['datos_normales'][i]["tipo"]=='REG'){
                                    html_ventasnetas +='<tr>';
                                    html_ventasnetas +='<td  align="left" >'+entry['datos_normales'][i]["codigo"]+'</td>';
                                    html_ventasnetas +='<td  align="left" >'+entry['datos_normales'][i]["producto"]+'</td>';
                                    html_ventasnetas +='<td  align="center" >'+entry['datos_normales'][i]["factura"]+'</td>';
                                    html_ventasnetas +='<td  align="center" >'+entry['datos_normales'][i]["fecha_factura"]+'</td>';
                                    html_ventasnetas +='<td  align="right" >'+entry['datos_normales'][i]["unidad"]+'</td>';
                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["cantidad"]).toFixed(2))+'</td>';
                                    html_ventasnetas +='<td align="right">'+"$"+'</td>';
                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["precio_unitario"]).toFixed(2))+'</td>';
                                    html_ventasnetas +='<td align="right">'+"$"+'</td>';
                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["venta_pesos"]).toFixed(2))+'</td>';
                                    html_ventasnetas +='<td   align="right" >'+entry['datos_normales'][i]["moneda"]+'</td>';
                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["tipo_cambio"]).toFixed(4))+'</td>';
                                    /*
                                    html_ventasnetas +='<td align="right">'+"$"+'</td>';
                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["costo"]).toFixed(2))+'</td>';
                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas((parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa * 100 ).toFixed(2))+' % </td>';
                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(((parseFloat(entry['datos_normales'][i]["venta_pesos"]) - parseFloat(entry['datos_normales'][i]["costo"])  ) / parseFloat(entry['datos_normales'][i]["venta_pesos"]) * 100 ).toFixed(2))+' %</td>';
                                    html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(((parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) *  ((parseFloat(entry['datos_normales'][i]["venta_pesos"]) - parseFloat(entry['datos_normales'][i]["costo"])  ) / parseFloat(entry['datos_normales'][i]["venta_pesos"]) * 100 ) ).toFixed(2)  )+' %</td>';
                                    */
                                    html_ventasnetas +='</tr>';
                                    
                                    totalponderacion=totalponderacion + (parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) * 100 ;
                                    totalmop = totalmop + ((parseFloat(entry['datos_normales'][i]["venta_pesos"]) -parseFloat(entry['datos_normales'][i]["costo"])) / parseFloat(entry['datos_normales'][i]["venta_pesos"])) * 100 ;
                                    totalmediamop = totalmediamop +  (parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) * (parseFloat(entry['datos_normales'][i]["venta_pesos"])- parseFloat(entry['datos_normales'][i]["costo"]))/ parseFloat(entry['datos_normales'][i]["venta_pesos"])*100 ;
                                    
                                    ventageneral=ventageneral+ parseFloat(entry['datos_normales'][i]["venta_pesos"]);
                                    costogeneral=costogeneral+ parseFloat(entry['datos_normales'][i]["costo"]);
                                    cant_general=parseFloat(cant_general) + parseFloat(entry['datos_normales'][i]["total_cantidad"]);
                                }else{
									cant_general=parseFloat(cant_general) + parseFloat(entry['datos_normales'][i]["total_cantidad"]);
									
                                    html_ventasnetas +='<tr>';
                                    html_ventasnetas +='<td align="right" colspan="5" ><strong>'+"Total por Cliente"+'</strong></td>';
                                    html_ventasnetas +='<td align="right"><font color="Black"> <strong>'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["total_cantidad"]).toFixed(2))+'</strong></td>'
                                    html_ventasnetas +='<td align="right"></td>';
                                    html_ventasnetas +='<td align="right"></td>';
                                    html_ventasnetas +='<td align="right">'+"$"+'</td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(parseFloat(totalventa).toFixed(2))+'</strong></td>';
                                    html_ventasnetas +='<td align="right"></td>';
                                    html_ventasnetas +='<td align="right"></td>';
                                    /*
                                    html_ventasnetas +='<td align="right"></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["total_costo"]).toFixed(2))+'</strong></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(totalponderacion.toFixed(2) )+' %</strong></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(totalmop.toFixed(2) )+'</strong></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(totalmediamop.toFixed(2) )+'</strong></td>';
                                    */
                                    html_ventasnetas +='</tr>';
                                }
                            }else{
                                clie = entry['datos_normales'][i]["razon_social"];
                                totalventa=obtiene_total_venta($select_tipo_reporte.val(),entry['totales'],clie);
                                totalponderacion=0.0;
                                totalmop=0.0;
                                totalmediamop=0.0;

                                html_ventasnetas +='<tr>';
                                html_ventasnetas +='<td align="left" colspan ="8" width="695px"> <strong>'+clie+'</strong></td>'
								html_ventasnetas +='<td align="right" width="25px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="40px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								/*
								html_ventasnetas +='<td align="right" width="25px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								*/
                                html_ventasnetas +='</tr>';

                                html_ventasnetas +='<tr>';
                                html_ventasnetas +='<td  align="left" >'+entry['datos_normales'][i]["codigo"]+'</td>';
                                html_ventasnetas +='<td  align="left" >'+entry['datos_normales'][i]["producto"]+'</td>';
                                html_ventasnetas +='<td  align="center" >'+entry['datos_normales'][i]["factura"]+'</td>';
                                html_ventasnetas +='<td  align="center" >'+entry['datos_normales'][i]["fecha_factura"]+'</td>';
                                html_ventasnetas +='<td  align="right" >'+entry['datos_normales'][i]["unidad"]+'</td>';
                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["cantidad"]).toFixed(2))+'</td>';
                                html_ventasnetas +='<td  align="right">'+"$"+'</td>';
                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["precio_unitario"]).toFixed(2))+'</td>';
                                html_ventasnetas +='<td align="right">'+"$"+'</td>';
                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["venta_pesos"]).toFixed(2))+'</td>';
                                html_ventasnetas +='<td   align="right" >'+entry['datos_normales'][i]["moneda"]+'</td>';
                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["tipo_cambio"]).toFixed(2))+'</td>';
                                /*
                                html_ventasnetas +='<td align="right">'+"$"+'</td>';
                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["costo"]).toFixed(2))+'</td>';
                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas((parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa * 100 ).toFixed(2))+' % </td>';
                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(((parseFloat(entry['datos_normales'][i]["venta_pesos"]) - parseFloat(entry['datos_normales'][i]["costo"])  ) / parseFloat(entry['datos_normales'][i]["venta_pesos"]) * 100 ).toFixed(2))+' %</td>';
                                html_ventasnetas +='<td  align="right" >'+$(this).agregar_comas(((parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) *  ((parseFloat(entry['datos_normales'][i]["venta_pesos"]) - parseFloat(entry['datos_normales'][i]["costo"])  ) / parseFloat(entry['datos_normales'][i]["venta_pesos"]) * 100 ) ).toFixed(2)  )+' %</td>';
                                */
                                html_ventasnetas +='</tr>';
                                
                                totalponderacion=totalponderacion + (parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) * 100 ;
                                totalmop = totalmop + ((parseFloat(entry['datos_normales'][i]["venta_pesos"]) -parseFloat(entry['datos_normales'][i]["costo"])) / parseFloat(entry['datos_normales'][i]["venta_pesos"])) * 100 ;
                                totalmediamop = totalmediamop +  (parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) * (parseFloat(entry['datos_normales'][i]["venta_pesos"])- parseFloat(entry['datos_normales'][i]["costo"]))/ parseFloat(entry['datos_normales'][i]["venta_pesos"])*100 ;
                                
                                ventageneral=ventageneral+ parseFloat(entry['datos_normales'][i]["venta_pesos"]);
                                costogeneral=costogeneral+ parseFloat(entry['datos_normales'][i]["costo"]);
                                
                                cant_general=parseFloat(cant_general) + parseFloat(entry['datos_normales'][i]["total_cantidad"]);
                            }
                        }
							
						html_ventasnetas +='<tr>';
						html_ventasnetas +='<td align="left"  id="sin_borde" width="80px" height="2"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="left"  id="sin_borde" width="200px"><input type="text" name="col1" style="width:200px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="70px"><input type="text" name="col1" style="width:70px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="25px"><input type="text" name="col1" style="width:25px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="25px"><input type="text" name="col1" style="width:25px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="40px"><input type="text" name="col1" style="width:40px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						/*
						html_ventasnetas +='<td align="right" id="sin_borde" width="25px"><input type="text" name="col1" style="width:25px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						html_ventasnetas +='<td align="right" id="sin_borde" width="80px"><input type="text" name="col1" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
						*/
						html_ventasnetas +='</tr>';
						
						/*
                        html_footer +='<tr>';
                        html_footer +='<td align="right" colspan="8" ><strong>'+"TOTAL GENERAL:"+'</strong></td>';
                        html_footer +='<td align="right" >'+"$"+'</td>';
                        html_footer +='<td align="right" >'+$(this).agregar_comas(parseFloat(ventageneral).toFixed(2))+'</td>';
                        html_footer +='<td align="right" colspan="3" >'+""+'</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas(parseFloat(costogeneral).toFixed(2))+'</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas(((ventageneral/ventageneral)*100).toFixed(2) )+' %</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas((((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas(((ventageneral / ventageneral)*((((ventageneral-costogeneral)/ventageneral)))*100) .toFixed(2) )+'</td>';
                        html_footer +='</tr>';
                        */
						html_footer +='<tr>';
						html_footer +='<td align="left"  width="80px" height="10"></td>';
						html_footer +='<td align="left"  width="200px"></td>';
						html_footer +='<td align="right" width="70px"></td>';
						html_footer +='<td align="right" width="80px"></td>';
						html_footer +='<td align="right" width="80px">Total</td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(parseFloat(cant_general).toFixed(2))+'</td>';
						html_footer +='<td align="right" width="25px"></td>';
						html_footer +='<td align="right" width="80px"></td>';
						html_footer +='<td align="right" width="25px"></td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(parseFloat(ventageneral).toFixed(2))+'</td>';
						html_footer +='<td align="right" width="40px"></td>';
						html_footer +='<td align="right" width="80px"></td>';
						/*
						html_footer +='<td align="right" width="25px"></td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(parseFloat(costogeneral).toFixed(2))+'</td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(((ventageneral/ventageneral)*100).toFixed(2) )+' %</td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas((((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(((ventageneral / ventageneral)*((((ventageneral-costogeneral)/ventageneral)))*100) .toFixed(2) )+'</td>';
						*/
						html_footer +='</tr>';
                        
                    }else{
                        jAlert("Esta consulta no genero Resultados",'Atencion!!!');
                        
						html_ventasnetas +='<tr>';
								html_ventasnetas +='<td colspan="12">&nbsp;</td>';
						html_ventasnetas +='</tr>';
                    }
                    html_ventasnetas +='<tfoot>';
                            html_ventasnetas += html_footer;
                    html_ventasnetas +='</tfoot>';
                    html_ventasnetas += '</table>';
                    $div_ventas_netas_productofactura.append(html_ventasnetas);
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-350;
                    var pix_alto=alto+'px';

                    $('#ventas').tableScroll({
                        height:parseInt(pix_alto)
                        });
                });//fin del json
            }else{
				jAlert('Debe elegir el rango la Fecha Inicial y su Fecha Final par la Busqueda.', 'Atencion!', function(r) { 
					//$fecha_inicial.focus();
					$('#lienzo_recalculable').find('input[name=fecha_inicial]').trigger('click');
				});
            }
        }






        ///GENERA LA VISTA DE LA IMPRESION DEL REPORTE DE POR PRODUCTO
        if ($select_tipo_reporte.val() == 2 ){
            $div_ventas_netas_productofactura.children().remove();
            var prod="";
             html_footer="";
            var totalponderacion = 0.0;
            var totalmop = 0.0;
            var totalmediamop = 0.0;
            var html_ventasnetas="";
            var totalventa=0.0;
            var ventageneral =0.0;
            var costogeneral =0.0;
            var cant_general=0.0;

            if($fecha_inicial.val() != "" && $fecha_final.val() != ""){
                var arreglo_parametros = {
                    tipo_reporte : $select_tipo_reporte.val() ,
                    cliente : $Nombre_Cliente.val() ,
                    producto : $Nombre_Producto.val(),
                    fecha_inicial : $fecha_inicial.val() ,
                    fecha_final : $fecha_final.val(),
                    linea:$select_linea.val(),
                    marca:$select_marca.val(),
                    familia:$select_familia.val(),
                    subfamilia:$select_subfamilia.val(),
                    tipo_costo:$select_tipo_costo.val(),
                    id_agente : $select_agente.val(),
                    segmento : $select_segmento.val(),
                    mercado : $select_mercado.val(),
                    iu:config.getUi()
                    };
                var restful_json_service = config.getUrlForGetAndPost() + '/getVentasNetasProductoFactura/out.json';
                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var header_tabla = {
                        Codigo         :'N.Control',
                        Producto       :'Cliente',
                        Factura        :'Factura',
                        Fecha_Factura  :'F.Factura',
                        Unidad         :'Unidad',
                        Cantidad       :'Cantidad',
                        Monedapu       :'&nbsp;',
                        Precio_unitario:'P.Unitario',
                        Monedavn       :'&nbsp;',
                        Venta_Neta     :'V.Neta',
                        denominacion   :'Denom.',
                        Tipo_Cambio    :'T.Cambio',//de aqui adelante es nuevo
                        /*
                        monedacosto    :'&nbsp;',
                        Costo          :'Costo',
                        Ponderacion    :'POND',
                        Tipo_MOP       :'MOP',
                        Medi_MOP       :'M_MOP'
                        */
                    };
                    
                    html_ventasnetas = '<table id="ventas" width="100%">';

                    html_ventasnetas +='<thead> <tr>';
                    for(var key in header_tabla){
                        var attrValue = header_tabla[key]; //width="5px"
                        if(attrValue == "N.Control"){
                            html_ventasnetas +='<td  align="left"   >'+attrValue+'</td>';
                        }
                        if(attrValue == "Cliente"){
                            html_ventasnetas +='<td   align="left"  >'+attrValue+'</td>';
                        }
                        if(attrValue == "Factura"){
                            html_ventasnetas +='<td  align="center" >'+attrValue+'</td>';
                        }

                        if(attrValue == "F.Factura"){
                            html_ventasnetas +='<td  align="center"  >'+attrValue+'</td>';
                        }

                        if(attrValue == "Unidad"){
                            html_ventasnetas +='<td  align="right"   >'+attrValue+'</td>';
                        }
                        if(attrValue == "Cantidad"){
                            html_ventasnetas +='<td  align="right" >'+attrValue+'</td>';
                        }
                        if(attrValue == "&nbsp;"){
                            html_ventasnetas +='<td  align="right" >'+attrValue+'</td>';
                        }
                        if(attrValue == "P.Unitario"){
                            html_ventasnetas +='<td   align="right" >'+attrValue+'</td>';
                        }

                        if(attrValue == "V.Neta"){
                            html_ventasnetas +='<td  align="right"  >'+attrValue+'</td>';
                        }
                        if(attrValue == "Denom."){
                            html_ventasnetas +='<td  align="right" >'+attrValue+'</td>';
                        }

                        if(attrValue == "T.Cambio"){
                            html_ventasnetas +='<td align="right"  >'+attrValue+'</td>';
                        }
                        /*
                        //nuevo de aqui para abajo
                        if(attrValue == "Costo"){
                            html_ventasnetas +='<td align="right"  >'+attrValue+'</td>';
                        }
                        
                        if(attrValue == "POND"){
                            html_ventasnetas +='<td  align="right"  >'+attrValue+'</td>';
                        }
                        if(attrValue == "MOP"){
                            html_ventasnetas +='<td  align="right"  >'+attrValue+'</td>';
                        }
                        if(attrValue == "M_MOP"){
                            html_ventasnetas +='<td  align="right"  >'+attrValue+'</td>';
                        }
						*/
                    }
                    html_ventasnetas +='</tr> </thead>';
					
					html_ventasnetas +='<tr>';
					html_ventasnetas +='<td align="left"  id="sin_borde" width="80px" height="2"></td>';
					html_ventasnetas +='<td align="left"  id="sin_borde" width="200px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="70px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="25px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="25px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="40px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					/*
					html_ventasnetas +='<td align="right" id="sin_borde" width="25px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					html_ventasnetas +='<td align="right" id="sin_borde" width="80px"></td>';
					*/
					html_ventasnetas +='</tr>';
					
                    if(entry['datos_normales'].length > 0 ){
                        prod = entry['datos_normales'][0]["producto"];
                        html_ventasnetas +='<tr class="first">';
                        html_ventasnetas +='<td align="left" colspan ="8" width="695px"> <strong>'+prod+'</strong></td>'
						html_ventasnetas +='<td align="right" width="25px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="40px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						/*
						html_ventasnetas +='<td align="right" width="25px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
						*/
                        html_ventasnetas +='</tr>';
                        
                        totalventa=obtiene_total_venta($select_tipo_reporte.val(),entry['totales'],prod);

                        for(var i=0; i<entry['datos_normales'].length; i++){
                            if(prod == entry['datos_normales'][i]["producto"] ){
                                if(entry['datos_normales'][i]["tipo"]=='REG'){
                                    html_ventasnetas +='<tr>';
                                    html_ventasnetas +='<td  width="80px"  align="left" >'+entry['datos_normales'][i]["numero_control"]+'</td>';
                                    html_ventasnetas +='<td  width="200px" align="left" >'+entry['datos_normales'][i]["razon_social"]+'</td>';
                                    html_ventasnetas +='<td  width="70px" align="center" >'+entry['datos_normales'][i]["factura"]+'</td>';

                                    html_ventasnetas +='<td  width="80px" align="center" >'+entry['datos_normales'][i]["fecha_factura"]+'</td>';
                                    html_ventasnetas +='<td  width="80px" align="center" >'+entry['datos_normales'][i]["unidad"]+'</td>';
                                    html_ventasnetas +='<td  width="80px" align="right"  >'+entry['datos_normales'][i]["cantidad"]+'</td>';
                                    html_ventasnetas +='<td  width="25px" align="right"  >$</td>';
                                    html_ventasnetas +='<td  width="80px" align="right"  >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["precio_unitario"]).toFixed(2))+'</td>';

                                    html_ventasnetas +='<td  width="25px" align="right"  >$</td>';
                                    html_ventasnetas +='<td  width="80px" align="right"  >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["venta_pesos"]).toFixed(2))+'</td>';
                                    html_ventasnetas +='<td  width="40px" align="right"  >'+entry['datos_normales'][i]["moneda"]+'</td>';

                                    html_ventasnetas +='<td  width="80px" align="right"  >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["tipo_cambio"]).toFixed(2))+'</td>';
									/*
                                    html_ventasnetas +='<td  width="25px"  align="right">'+"$"+'</td>';
                                    html_ventasnetas +='<td  width="80px" align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["costo"]).toFixed(2))+'</td>';
                                    html_ventasnetas +='<td  width="80px" align="right" >'+$(this).agregar_comas((parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa * 100 ).toFixed(2))+' % </td>';
                                    html_ventasnetas +='<td  width="80px" align="right" >'+$(this).agregar_comas(((parseFloat(entry['datos_normales'][i]["venta_pesos"]) - parseFloat(entry['datos_normales'][i]["costo"])  ) / parseFloat(entry['datos_normales'][i]["venta_pesos"]) * 100 ).toFixed(2))+' %</td>';
                                    html_ventasnetas +='<td  width="80px" align="right" >'+$(this).agregar_comas(((parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) *  ((parseFloat(entry['datos_normales'][i]["venta_pesos"]) - parseFloat(entry['datos_normales'][i]["costo"])  ) / parseFloat(entry['datos_normales'][i]["venta_pesos"]) * 100 ) ).toFixed(2)  )+' %</td>';
                                    */
                                    html_ventasnetas +='</tr>';

                                    totalponderacion=totalponderacion + (parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) * 100 ;
                                    totalmop = totalmop + ((parseFloat(entry['datos_normales'][i]["venta_pesos"]) -parseFloat(entry['datos_normales'][i]["costo"])) / parseFloat(entry['datos_normales'][i]["venta_pesos"])) * 100 ;
                                    totalmediamop = totalmediamop +  (parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) * (parseFloat(entry['datos_normales'][i]["venta_pesos"])- parseFloat(entry['datos_normales'][i]["costo"]))/ parseFloat(entry['datos_normales'][i]["venta_pesos"])*100 ;

                                    ventageneral=ventageneral+ parseFloat(entry['datos_normales'][i]["venta_pesos"]);
                                    costogeneral=costogeneral+ parseFloat(entry['datos_normales'][i]["costo"]);
                                    cant_general=parseFloat(cant_general) + parseFloat(entry['datos_normales'][i]["total_cantidad"]);
                                }else{
									cant_general=parseFloat(cant_general) + parseFloat(entry['datos_normales'][i]["total_cantidad"]);
									
                                    html_ventasnetas +='<tr>';
                                    html_ventasnetas +='<td align="right" colspan="5" ><strong>Total por Producto</strong></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["total_cantidad"]).toFixed(2))+'</strong></td>'
                                    html_ventasnetas +='<td align="right"></td>';
                                    html_ventasnetas +='<td align="right"></td>';
                                    html_ventasnetas +='<td align="right"><strong>$</strong></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(parseFloat(totalventa).toFixed(2))+'</strong></td>';
                                    html_ventasnetas +='<td align="right"></td>';
                                    html_ventasnetas +='<td align="right"></td>';
                                    /*
                                    html_ventasnetas +='<td align="right"></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["total_costo"]).toFixed(2))+'</strong></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(totalponderacion.toFixed(2) )+' %</strong></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(totalmop.toFixed(2) )+'</strong></td>';
                                    html_ventasnetas +='<td align="right"><strong>'+$(this).agregar_comas(totalmediamop.toFixed(2) )+'</strong></td>';
                                    */
                                    html_ventasnetas +='</tr>';

                                }
                            }else{
                                prod = entry['datos_normales'][i]["producto"];
                                totalventa=obtiene_total_venta($select_tipo_reporte.val(),entry['totales'],prod);
                                totalponderacion=0.0;
                                totalmop=0.0;
                                totalmediamop=0.0;
                                
                                html_ventasnetas +='<tr>';
                                html_ventasnetas +='<td align="left" colspan ="8" width="695px"> <strong>'+prod+'</strong></td>'
								html_ventasnetas +='<td align="right" width="25px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="40px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								/*
								html_ventasnetas +='<td align="right" width="25px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								html_ventasnetas +='<td align="right" width="80px" id="sin_borde"></td>';
								*/
                                html_ventasnetas +='</tr>';
								
                                html_ventasnetas +='<tr>';
                                html_ventasnetas +='<td width="80px" align="left" >'+entry['datos_normales'][i]["numero_control"]+'</td>';
                                html_ventasnetas +='<td width="200px" align="left" >'+entry['datos_normales'][i]["razon_social"]+'</td>';
                                html_ventasnetas +='<td width="70px"align="center">'+entry['datos_normales'][i]["factura"]+'</td>';
                                html_ventasnetas +='<td width="80px" align="center">'+entry['datos_normales'][i]["fecha_factura"]+'</td>';
                                html_ventasnetas +='<td width="80px" align="center" >'+entry['datos_normales'][i]["unidad"]+'</td>';
                                html_ventasnetas +='<td width="80px" align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["cantidad"]).toFixed(2))+'</td>';
                                html_ventasnetas +='<td width="25px" align="right" >'+"$"+'</td>';
                                html_ventasnetas +='<td width="80px" align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["precio_unitario"]).toFixed(2))+'</td>';
                                html_ventasnetas +='<td width="25px" align="right" >'+"$"+'</td>';
                                html_ventasnetas +='<td width="80px" align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["venta_pesos"]).toFixed(2))+'</td>';
                                html_ventasnetas +='<td width="40px" align="right" >'+entry['datos_normales'][i]["moneda"]+'</td>';
                                html_ventasnetas +='<td width="80px" align="right" ">'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["tipo_cambio"]).toFixed(2))+'</td>';
                                /*
                                html_ventasnetas +='<td width="25px" align="right">'+"$"+'</td>';
                                html_ventasnetas +='<td width="80px" align="right" >'+$(this).agregar_comas(parseFloat(entry['datos_normales'][i]["costo"]).toFixed(2))+'</td>';
                                html_ventasnetas +='<td width="80px" align="right" >'+$(this).agregar_comas((parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa * 100 ).toFixed(2))+' % </td>';
                                html_ventasnetas +='<td width="80px" align="right" >'+$(this).agregar_comas(((parseFloat(entry['datos_normales'][i]["venta_pesos"]) - parseFloat(entry['datos_normales'][i]["costo"])  ) / parseFloat(entry['datos_normales'][i]["venta_pesos"]) * 100 ).toFixed(2))+' %</td>';
                                html_ventasnetas +='<td width="80px" align="right" >'+$(this).agregar_comas(((parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) *  ((parseFloat(entry['datos_normales'][i]["venta_pesos"]) - parseFloat(entry['datos_normales'][i]["costo"])  ) / parseFloat(entry['datos_normales'][i]["venta_pesos"]) * 100 ) ).toFixed(2)  )+' %</td>';
                                */
                                html_ventasnetas +='</tr>';

                                totalponderacion=totalponderacion + (parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) * 100 ;
                                totalmop = totalmop + ((parseFloat(entry['datos_normales'][i]["venta_pesos"]) -parseFloat(entry['datos_normales'][i]["costo"])) / parseFloat(entry['datos_normales'][i]["venta_pesos"])) * 100 ;
                                totalmediamop = totalmediamop +  (parseFloat(entry['datos_normales'][i]["venta_pesos"]) / totalventa) * (parseFloat(entry['datos_normales'][i]["venta_pesos"])- parseFloat(entry['datos_normales'][i]["costo"]))/ parseFloat(entry['datos_normales'][i]["venta_pesos"])*100 ;

                                ventageneral=ventageneral+ parseFloat(entry['datos_normales'][i]["venta_pesos"]);
                                costogeneral=costogeneral+ parseFloat(entry['datos_normales'][i]["costo"]);
                                cant_general=parseFloat(cant_general) + parseFloat(entry['datos_normales'][i]["total_cantidad"]);
                            }
                        }
                        /*
                        html_footer +='<tr>';
                        html_footer +='<td align="right" colspan="8" ><strong>'+"TOTAL GENERAL:"+'</strong></td>';
                        html_footer +='<td align="right" width="5px">'+"$"+'</td>';
                        html_footer +='<td align="right" >'+$(this).agregar_comas(parseFloat(ventageneral).toFixed(2))+'</td>';
                        html_footer +='<td align="right" colspan="3" >'+""+'</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas(parseFloat(costogeneral).toFixed(2))+'</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas(((ventageneral/ventageneral)*100).toFixed(2) )+' %</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas((((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas(((ventageneral / ventageneral)*((((ventageneral-costogeneral)/ventageneral)))*100) .toFixed(2) )+'</td>';
                        html_footer +='</tr>';
                        */
						html_footer +='<tr>';
						html_footer +='<td align="left"  width="80px" height="10"></td>';
						html_footer +='<td align="left"  width="200px"></td>';
						html_footer +='<td align="right" width="70px"></td>';
						html_footer +='<td align="right" width="80px"></td>';
						html_footer +='<td align="right" width="80px">Total</td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(parseFloat(cant_general).toFixed(2))+'</td>';
						html_footer +='<td align="right" width="25px"></td>';
						html_footer +='<td align="right" width="80px"></td>';
						html_footer +='<td align="right" width="25px"></td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(parseFloat(ventageneral).toFixed(2))+'</td>';
						html_footer +='<td align="right" width="40px"></td>';
						html_footer +='<td align="right" width="80px"></td>';
						/*
						html_footer +='<td align="right" width="25px"></td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(parseFloat(costogeneral).toFixed(2))+'</td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(((ventageneral/ventageneral)*100).toFixed(2) )+' %</td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas((((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
						html_footer +='<td align="right" width="80px">'+$(this).agregar_comas(((ventageneral / ventageneral)*((((ventageneral-costogeneral)/ventageneral)))*100) .toFixed(2) )+'</td>';
						*/
						html_footer +='</tr>';
                        
                        
                        
                    }else{
                        jAlert("Esta consulta no genero ningun Resultado",'Atencion!!!');
                        
						html_ventasnetas +='<tr>';
								html_ventasnetas +='<td colspan="12">&nbsp;</td>';
						html_ventasnetas +='</tr>';
                    }
                    html_ventasnetas +='<tfoot>';
						html_ventasnetas += html_footer;
					html_ventasnetas +='</tfoot>';
                    html_ventasnetas += '</table>';

                    $div_ventas_netas_productofactura.append(html_ventasnetas);
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-350;
                    var pix_alto=alto+'px';

                    $('#ventas').tableScroll({
                        height:parseInt(pix_alto)
					});
                });

            }else{
				jAlert('Debe elegir el rango la Fecha Inicial y su Fecha Final par la Busqueda.', 'Atencion!', function(r) { 
					//$fecha_inicial.focus();
					$('#lienzo_recalculable').find('input[name=fecha_inicial]').trigger('click');
				});
            }
        }
        
        

        /***VENTAS NETAS SUMARIZADO POR PRODUCTO***/
        if ($select_tipo_reporte.val() == 3 ){
            $div_ventas_netas_productofactura.children().remove();
            //producto ,  unidad, cantidad , $ ,precio_promedio,$ ,venta_neta
            var prod = "";
            var unidad ="";
            var html_footer ="";
            var cantidad = 0.0;
            var venta_neta= 0.0;
            var totalponderacion=0.0;
            var totalmop=0.0;
            var totalmediamop =0.0;

            var costoxproducto=0.0;
            var ventageneral=0.0;
            var costogeneral=0.0;

            var totalcosto=0.0;
            var totalventa=0.0;
            var Tventageneral=0.0;
            var Tcostogeneral=0.0;


            if($fecha_inicial.val() != "" && $fecha_final.val() != ""){
                var arreglo_parametros = {
                    tipo_reporte : $select_tipo_reporte.val() ,
                    cliente : $Nombre_Cliente.val() ,
                    producto : $Nombre_Producto.val(),
                    fecha_inicial : $fecha_inicial.val() ,
                    fecha_final : $fecha_final.val(),
                    linea:$select_linea.val(),
                    marca:$select_marca.val(),
                    familia:$select_familia.val(),
                    subfamilia:$select_subfamilia.val(),
                    tipo_costo:$select_tipo_costo.val(),
                    id_agente : $select_agente.val(),
                    segmento : $select_segmento.val(),
                    mercado : $select_mercado.val(),
                    iu:config.getUi()
                    };
                var restful_json_service = config.getUrlForGetAndPost() + '/getVentasNetasProductoFactura/out.json';

                $.post(restful_json_service,arreglo_parametros,function(entry){

                    var header_tabla = {
                        Producto  	:'Producto',
                        Unidad		:'Unidad',
                        Total 		:'Total',
                        Monedapu    :"",
                        Precio_unitario :'P.&nbsp;Promedio',
                        Monedavn    :"",
                        Venta_Neta 	:'V.&nbsp;Total',
                        /*
                        moneda_costo:"",
                        Costo 		:'Costo',
                        Ponderacion	:'POND',
                        Tipo_MOP	:'MOP',
                        Medi_MOP	:'M_MOP'
                        */
                    };


                    var html_ventasnetas = '<table id="ventas" >';
                    html_ventasnetas +='<thead> <tr>';
                    for(var key in header_tabla){
                        var attrValue = header_tabla[key];
                        if(attrValue == "Producto"){
                            html_ventasnetas +='<td  width="300" align="center" >'+attrValue+'</td>';
                        }
                        if(attrValue == "Unidad"){
                            html_ventasnetas +='<td  align="center" widht="50">'+attrValue+'</td>';
                        }
                        if(attrValue == "Total"){
                            html_ventasnetas +='<td  align="center" widht="80">'+attrValue+'</td>';
                        }

                        if(attrValue == ""){
                            html_ventasnetas +='<td width="15" align="center" >'+attrValue+'</td>';
                        }
                        
                        if(attrValue == "P.&nbsp;Promedio"){
                            html_ventasnetas +='<td   align="center" widht="100">'+attrValue+'</td>';
                        }

                        if(attrValue == "V.&nbsp;Total"){
                            html_ventasnetas +='<td  align="center" widht="100">'+attrValue+'</td>';
                        }
                        /*
                        //nuevo de aqui para abajo
                        if(attrValue == "Costo"){
                            html_ventasnetas +='<td align="center" widht="100">'+attrValue+'</td>';
                        }
                        if(attrValue == "POND"){
                            html_ventasnetas +='<td width="80" align="center"  >'+attrValue+'</td>';
                        }
                        if(attrValue == "MOP"){
                            html_ventasnetas +='<td width="80" align="center"  >'+attrValue+'</td>';
                        }
                        if(attrValue == "M_MOP"){
                            html_ventasnetas +='<td width="80" align="center"  >'+attrValue+'</td>';
                        }
                        */
                    }
                    html_ventasnetas +='</tr>';
                    html_ventasnetas +='</thead>';
                    
                    if(entry['datos_normales'].length > 0 ){
						
                        for(var j=0; j<entry['datos_normales'].length; j++){
                            Tventageneral = Tventageneral + parseFloat(entry['datos_normales'][j]["venta_pesos"]);
                            Tcostogeneral = Tcostogeneral + parseFloat(entry['datos_normales'][j]["costo"]);
                        }
                        
                        prod = entry['datos_normales'][0]["producto"];
                        totalventa=obtiene_total_venta($select_tipo_reporte.val(),entry['totales'],prod);
						
						var primer_registro=0;
						
                        for(var i=0; i<entry['datos_normales'].length; i++){
                            if(prod == entry['datos_normales'][i]["producto"] ){
                                if(entry['datos_normales'][i]["tipo"]=='REG'){
                                    cantidad = parseFloat(cantidad) + parseFloat(entry['datos_normales'][i]["cantidad"]);
                                    venta_neta = parseFloat(venta_neta) + parseFloat(entry['datos_normales'][i]["venta_pesos"]);

                                    prod = entry['datos_normales'][i]["producto"];
                                    unidad = entry['datos_normales'][i]["unidad"];
                                    costoxproducto = parseFloat(costoxproducto) + parseFloat(entry['datos_normales'][i]["costo"]);
                                    ventageneral = parseFloat(ventageneral) + parseFloat(entry['datos_normales'][i]["venta_pesos"]);
                                    costogeneral = parseFloat(costogeneral) + parseFloat(entry['datos_normales'][i]["costo"]);
                                    
                                    totalcosto = parseFloat(totalcosto) + parseFloat(entry['datos_normales'][i]["costo"]);

                                    totalponderacion=(parseFloat(venta_neta)/parseFloat(Tventageneral))*100;
                                    totalmop=(((parseFloat(venta_neta)-parseFloat(costoxproducto))/parseFloat(venta_neta))*100);
                                    totalmediamop=((parseFloat(venta_neta) / parseFloat(Tventageneral) )*100)*(((parseFloat(venta_neta)-parseFloat(costoxproducto))/parseFloat(venta_neta))*100);
                                }else{
									html_ventasnetas +='<tr>';
									html_ventasnetas +='<td width="300" align="left" >'+prod+'</td>';
									html_ventasnetas +='<td align="right" widht="50">'+unidad+'</td>';
									html_ventasnetas +='<td align="right" widht="80">'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
									html_ventasnetas +='<td widht="15" align="right">'+"$"+'</td>';
									html_ventasnetas +='<td align="right" widht="100">'+$(this).agregar_comas((parseFloat(venta_neta)/parseFloat(cantidad)).toFixed(2))+'</td>';
									html_ventasnetas +='<td widht="15" align="right">'+"$"+'</td>';
									html_ventasnetas +='<td align="right" widht="100">'+$(this).agregar_comas(parseFloat(venta_neta).toFixed(2))+'</td>';
									/*
									html_ventasnetas +='<td widht="15" align="right">'+"$"+'</td>';
									html_ventasnetas +='<td align="right" widht="100">'+$(this).agregar_comas(parseFloat(costoxproducto).toFixed(2))+'</td>';
									html_ventasnetas +='<td align="right" widht="80">'+$(this).agregar_comas(parseFloat(totalponderacion).toFixed(2) )+' %</td>';
									html_ventasnetas +='<td align="right" widht="80">'+$(this).agregar_comas(parseFloat(totalmop).toFixed(2) )+'</td>';
									html_ventasnetas +='<td align="right" widht="80">'+$(this).agregar_comas(parseFloat(totalmediamop).toFixed(2) )+'</td>';
									*/
									html_ventasnetas +='</tr>';
                                }
                            }else{
                                prod = entry['datos_normales'][i]["producto"];
                                unidad=entry['datos_normales'][i]["unidad"];
                                totalventa=obtiene_total_venta($select_tipo_reporte.val(),entry['totales'],prod);

                                //reinicio valores
                                cantidad=0.0;
                                venta_neta=0.0;
                                costoxproducto=0.0;
                                totalcosto=0.0;
                                totalponderacion=0.0;
                                totalmop=0.0;
                                totalmediamop=0.0;

                                cantidad = parseFloat(cantidad) + parseFloat(entry['datos_normales'][i]["cantidad"]);
                                venta_neta = parseFloat(venta_neta) + parseFloat(entry['datos_normales'][i]["venta_pesos"]);

                                prod = entry['datos_normales'][i]["producto"];
                                unidad = entry['datos_normales'][i]["unidad"];
                                costoxproducto = parseFloat(costoxproducto) + entry['datos_normales'][i]["costo"];
                                ventageneral = parseFloat(ventageneral) + parseFloat(entry['datos_normales'][i]["venta_pesos"]);
                                costogeneral = parseFloat(costogeneral) + parseFloat(entry['datos_normales'][i]["costo"]);

                                totalcosto = parseFloat(totalcosto) + parseFloat(entry['datos_normales'][i]["costo"]);

                                totalponderacion = (parseFloat(venta_neta)/parseFloat(Tventageneral))*100;
                                totalmop = (((parseFloat(venta_neta)-parseFloat(costoxproducto))/parseFloat(venta_neta))*100);
                                totalmediamop = ((parseFloat(venta_neta) / parseFloat(Tventageneral) )*100)*(((parseFloat(venta_neta)-parseFloat(costoxproducto))/parseFloat(venta_neta))*100);
                            }
                        }
                        
                        //Total general
                        html_footer +='<tr>';
						html_footer +='<td width="300" align="right">TOTAL GENERAL:</td>';
						html_footer +='<td align="right" widht="50"></td>';
						html_footer +='<td align="right" widht="80"></td>';
                        html_footer +='<td widht="15" align="right">'+"$"+'</td>';
						html_footer +='<td align="right" widht="100"></td>';
                        html_footer +='<td widht="15" align="right">'+"$"+'</td>';
                        html_footer +='<td align="right" widht="100">'+$(this).agregar_comas(parseFloat(ventageneral).toFixed(2))+'</td>';
                        /*
                        html_footer +='<td widht="15" align="right">'+"$"+'</td>';
                        html_footer +='<td align="right" widht="100">'+$(this).agregar_comas(parseFloat(costogeneral).toFixed(2))+'</td>';
                        html_footer +='<td align="right" widht="80">'+$(this).agregar_comas(((ventageneral/ventageneral)*100).toFixed(2) )+' %</td>';
                        html_footer +='<td align="right" widht="80">'+$(this).agregar_comas((((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
                        html_footer +='<td align="right" widht="80">'+$(this).agregar_comas((ventageneral/ventageneral)*(((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
                        */
                        html_footer +='</tr>';
                    }else{
                        jAlert("Esta consulta no genero ningun Resultado",'Atencion!!!');
                    }
                    
                    //Este tr es para poder alinear las columnas
					html_ventasnetas +='<tr>';
					html_ventasnetas +='<td width="300" height="0"><input type="text" name="col1" style="width:300px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="50"><input type="text" name="col2" style="width:50px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="80"><input type="text" name="col3" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="15"><input type="text" name="col4" style="width:15px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="100"><input type="text" name="col5" style="width:100px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="15"><input type="text" name="col6" style="width:15px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="100"><input type="text" name="col7" style="width:100px; height:0px; border-color:transparent; background:transparent;"></td>';
					/*
					html_ventasnetas +='<td widht="15"><input type="text" name="col8" style="width:15px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="100"><input type="text" name="col9" style="width:100px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="80"><input type="text" name="col10" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="80"><input type="text" name="col11" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="80"><input type="text" name="col12" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
					*/
					html_ventasnetas +='</tr>';
					
                    /*
                    html_ventasnetas +='<tr>';
					html_ventasnetas +='<td colspan="12">&nbsp;</td>';
                    html_ventasnetas +='</tr>';
                    */
                    html_ventasnetas +='<tfoot>';
					html_ventasnetas += html_footer;
                    html_ventasnetas +='</tfoot>';
                    
                    html_ventasnetas += '</table>';
                    
                    $div_ventas_netas_productofactura.append(html_ventasnetas);
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-350;
                    var pix_alto=alto+'px';
                    $('#ventas').tableScroll({
                        height:parseInt(pix_alto)
					});
                });
            }else{
				jAlert('Debe elegir el rango la Fecha Inicial y su Fecha Final par la Busqueda.', 'Atencion!', function(r) { 
					//$fecha_inicial.focus();
					$('#lienzo_recalculable').find('input[name=fecha_inicial]').trigger('click');
				});
            }
        }//FIN DE LA VISTA DE PRODUCTO SUMARIZADO






        /*** SUMARIZADO POR  CLIENTE ***/
        if ($select_tipo_reporte.val() == 4 ){
            $div_ventas_netas_productofactura.children().remove();
            //cliene ,  unidad, cantidad , $ ,precio_promedio,$ ,venta_neta
            var clie = "";
            var unidad ="";
            var html_footer = '';
            var cantidad = 0.0;
            var venta_neta= 0.0;
            var totalponderacion=0.0;
            var totalmop=0.0;
            var totalmediamop =0.0;

            var costoxproducto=0.0;
            var ventageneral=0.0;
            var costogeneral=0.0;

            var totalcosto=0.0;
            var totalventa=0.0;

            var Tventageneral=0.0;
            var Tcostogeneral=0.0;


            if($fecha_inicial.val() != "" && $fecha_final.val() != ""){
                var arreglo_parametros = {
                    tipo_reporte : $select_tipo_reporte.val() ,
                    cliente : $Nombre_Cliente.val() ,
                    producto : $Nombre_Producto.val(),
                    fecha_inicial : $fecha_inicial.val() ,
                    fecha_final : $fecha_final.val(),
                    linea:$select_linea.val(),
                    marca:$select_marca.val(),
                    familia:$select_familia.val(),
                    subfamilia:$select_subfamilia.val(),
                    tipo_costo:$select_tipo_costo.val(),
                    id_agente : $select_agente.val(),
                    segmento : $select_segmento.val(),
                    mercado : $select_mercado.val(),
                    iu:config.getUi()
                    };
                var restful_json_service = config.getUrlForGetAndPost() + '/getVentasNetasProductoFactura/out.json';

                $.post(restful_json_service,arreglo_parametros,function(entry){

                    var header_tabla = {
                        Producto  :'Cliente',
                        Unidad : 'Unidad',
                        Total : 'Total',
                        Monedapu    :"",
                        Precio_unitario : 'P.&nbsp;Promedio',
                        Monedavn    :"",
                        Venta_Neta : 'V.&nbsp;Total',
                        /*
                        moneda_costo    :"",
                        Costo          :'Costo',
                        Ponderacion    : 'POND',
                        Tipo_MOP       :'MOP',
                        Medi_MOP       :'M_MOP'
                        */
                    };
                    
                    var html_ventasnetas = '<table id="ventas">';
                    html_ventasnetas +='<thead> <tr>';
                    for(var key in header_tabla){
                        var attrValue = header_tabla[key];
                        if(attrValue == "Cliente"){
                            html_ventasnetas +='<td  width="50px" align="left" >'+attrValue+'</td>';
                        }
                        if(attrValue == "Unidad"){
                            html_ventasnetas +='<td  align="center" widht="50">'+attrValue+'</td>';
                        }
                        if(attrValue == "Total"){
                            html_ventasnetas +='<td  align="center" widht="80">'+attrValue+'</td>';
                        }

                        if(attrValue == ""){
                            html_ventasnetas +='<td width="15" align="center" >'+attrValue+'</td>';
                        }
                        
                        if(attrValue == "P.&nbsp;Promedio"){
                            html_ventasnetas +='<td   align="center" widht="100">'+attrValue+'</td>';
                        }
                        
                        if(attrValue == "V.&nbsp;Total"){
                            html_ventasnetas +='<td  align="center" widht="100">'+attrValue+'</td>';
                        }
                        /*
                        //nuevo de aqui para abajo
                        if(attrValue == "Costo"){
                            html_ventasnetas +='<td align="center" widht="100">'+attrValue+'</td>';
                        }
                        if(attrValue == "POND"){
                            html_ventasnetas +='<td width="80" align="center"  >'+attrValue+'</td>';
                        }
                        if(attrValue == "MOP"){
                            html_ventasnetas +='<td width="80" align="center"  >'+attrValue+'</td>';
                        }
                        if(attrValue == "M_MOP"){
                            html_ventasnetas +='<td width="80" align="center"  >'+attrValue+'</td>';
                        }
                        */
                    }
                    
                    html_ventasnetas +='</tr> </thead>';
                    if(entry['datos_normales'].length > 0 ){
						
                        for(var j=0; j<entry['datos_normales'].length; j++){
                            Tventageneral = Tventageneral + parseFloat(entry['datos_normales'][j]["venta_pesos"]);
                            Tcostogeneral = Tcostogeneral + parseFloat(entry['datos_normales'][j]["costo"]);
                        }
                        
                        clie = entry['datos_normales'][0]["razon_social"];
                        totalventa=obtiene_total_venta($select_tipo_reporte.val(),entry['totales'],clie);

                        for(var i=0; i<entry['datos_normales'].length; i++){
                            if(clie == entry['datos_normales'][i]["razon_social"] ){
                                if(entry['datos_normales'][i]["tipo"]=='REG'){
                                    cantidad= parseFloat(cantidad) + parseFloat(entry['datos_normales'][i]["cantidad"]);
                                    venta_neta = parseFloat(venta_neta) +parseFloat(entry['datos_normales'][i]["venta_pesos"]);
                                    
                                    prod=entry['datos_normales'][i]["producto"];
                                    unidad=entry['datos_normales'][i]["unidad"];
                                    costoxproducto = parseFloat(costoxproducto) + parseFloat(entry['datos_normales'][i]["costo"]);
                                    ventageneral = parseFloat(ventageneral) + parseFloat(entry['datos_normales'][i]["venta_pesos"]);
                                    costogeneral = parseFloat(costogeneral) + parseFloat(entry['datos_normales'][i]["costo"]);

                                    totalcosto = parseFloat(totalcosto) + parseFloat(entry['datos_normales'][i]["costo"]);
                                    totalponderacion = (parseFloat(venta_neta)/parseFloat(Tventageneral))*100;
                                    totalmop = (((parseFloat(venta_neta)-parseFloat(costoxproducto))/parseFloat(venta_neta))*100);
                                    totalmediamop = ((parseFloat(venta_neta) / parseFloat(Tventageneral) )*100)*(((parseFloat(venta_neta)-parseFloat(costoxproducto))/parseFloat(venta_neta))*100);
                                }else{
									html_ventasnetas +='<tr>';
									html_ventasnetas +='<td width="300" align="left" >'+clie+'</td>';
									html_ventasnetas +='<td  align="right" widht="50">'+unidad+'</td>';
									html_ventasnetas +='<td  align="right" widht="80">'+$(this).agregar_comas(parseFloat(cantidad).toFixed(2))+'</td>';
									html_ventasnetas +='<td widht="15" align="right">'+"$"+'</td>';
									html_ventasnetas +='<td align="right" widht="100">'+$(this).agregar_comas((parseFloat(venta_neta)/parseFloat(cantidad)).toFixed(2))+'</td>';
									html_ventasnetas +='<td widht="15" align="right">'+"$"+'</td>';
									html_ventasnetas +='<td align="right" widht="100">'+$(this).agregar_comas(parseFloat(venta_neta).toFixed(2))+'</td>';
									/*
									html_ventasnetas +='<td widht="15" align="right">'+"$"+'</td>';
									html_ventasnetas +='<td align="right" widht="100">'+$(this).agregar_comas(parseFloat(costoxproducto).toFixed(2))+'</td>';
									html_ventasnetas +='<td align="right" widht="80">'+$(this).agregar_comas(parseFloat(totalponderacion).toFixed(2) )+' %</td>';
									html_ventasnetas +='<td align="right" widht="80">'+$(this).agregar_comas(parseFloat(totalmop).toFixed(2) )+'</td>';
									html_ventasnetas +='<td align="right" widht="80">'+$(this).agregar_comas(parseFloat(totalmediamop).toFixed(2) )+'</td>';
									*/
									html_ventasnetas +='</tr>';
                                }
                            }else{
                                clie = entry['datos_normales'][i]["producto"];
                                unidad=entry['datos_normales'][i]["unidad"];
                                totalventa=obtiene_total_venta($select_tipo_reporte.val(),entry['totales'],clie);

                                //reinicio valores
                                cantidad=0.0;
                                venta_neta=0.0;
                                costoxproducto=0.0;

                                totalcosto=0.0;
                                totalponderacion=0.0;
                                totalmop=0.0;
                                totalmediamop=0.0;

                                cantidad = parseFloat(cantidad) + parseFloat(entry['datos_normales'][i]["cantidad"]);
                                venta_neta = parseFloat(venta_neta) + parseFloat(entry['datos_normales'][i]["venta_pesos"]);

                                clie=entry['datos_normales'][i]["razon_social"];
                                unidad=entry['datos_normales'][i]["unidad"];
                                costoxproducto = parseFloat(costoxproducto) + parseFloat( entry['datos_normales'][i]["costo"]);
                                ventageneral = parseFloat(ventageneral) + parseFloat(entry['datos_normales'][i]["venta_pesos"]);
                                costogeneral = parseFloat(costogeneral) + parseFloat(entry['datos_normales'][i]["costo"]);

                                totalcosto = parseFloat(totalcosto) + parseFloat(entry['datos_normales'][i]["costo"]);

                                totalponderacion=(parseFloat(venta_neta)/parseFloat(Tventageneral))*100;
                                totalmop=(((parseFloat(venta_neta)-parseFloat(costoxproducto))/parseFloat(venta_neta))*100);
                                totalmediamop=((parseFloat(venta_neta) / parseFloat(Tventageneral ))*100)*(((parseFloat(venta_neta)-parseFloat(costoxproducto))/parseFloat(venta_neta))*100);
                            }
                        }
						/*
                        //total general
                        html_footer +='<tr>';
                        html_footer +='<td  align="right" colspan="5">TOTAL GENERAL:</td>';
                        html_footer +='<td widht="5px" align="right">'+"$"+'</td>';
                        html_footer +='<td  align="right" >'+$(this).agregar_comas(parseFloat(ventageneral).toFixed(2))+'</td>';
                        html_footer +='<td widht="5px" align="right">'+"$"+'</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas(parseFloat(costogeneral).toFixed(2))+'</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas(((ventageneral/ventageneral)*100).toFixed(2) )+' %</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas((((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
                        html_footer +='<td align="right"  >'+$(this).agregar_comas((ventageneral/ventageneral)*(((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
                        html_footer +='</tr>';
                        */
                        //Total general
                        html_footer +='<tr>';
						html_footer +='<td width="300" align="right">TOTAL GENERAL:</td>';
						html_footer +='<td align="right" widht="50"></td>';
						html_footer +='<td align="right" widht="80"></td>';
                        html_footer +='<td widht="15" align="right">'+"$"+'</td>';
						html_footer +='<td align="right" widht="100"></td>';
                        html_footer +='<td widht="15" align="right">'+"$"+'</td>';
                        html_footer +='<td align="right" widht="100">'+$(this).agregar_comas(parseFloat(ventageneral).toFixed(2))+'</td>';
                        /*
                        html_footer +='<td widht="15" align="right">'+"$"+'</td>';
                        html_footer +='<td align="right" widht="100">'+$(this).agregar_comas(parseFloat(costogeneral).toFixed(2))+'</td>';
                        html_footer +='<td align="right" widht="80">'+$(this).agregar_comas(((ventageneral/ventageneral)*100).toFixed(2) )+' %</td>';
                        html_footer +='<td align="right" widht="80">'+$(this).agregar_comas((((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
                        html_footer +='<td align="right" widht="80">'+$(this).agregar_comas((ventageneral/ventageneral)*(((ventageneral-costogeneral)/ventageneral)*100).toFixed(2) )+'</td>';
                        */
                        html_footer +='</tr>';
                    }else{
                        jAlert("Esta consulta no genero ningun Resultado",'Atencion!!!');
                    }
                    
                    //Este tr es para poder alinear las columnas
					html_ventasnetas +='<tr>';
					html_ventasnetas +='<td width="300" height="0"><input type="text" name="col1" style="width:300px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="50"><input type="text" name="col2" style="width:50px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="80"><input type="text" name="col3" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="15"><input type="text" name="col4" style="width:15px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="100"><input type="text" name="col5" style="width:100px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="15"><input type="text" name="col6" style="width:15px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="100"><input type="text" name="col7" style="width:100px; height:0px; border-color:transparent; background:transparent;"></td>';
					/*
					html_ventasnetas +='<td widht="15"><input type="text" name="col8" style="width:15px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="100"><input type="text" name="col9" style="width:100px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="80"><input type="text" name="col10" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="80"><input type="text" name="col11" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
					html_ventasnetas +='<td widht="80"><input type="text" name="col12" style="width:80px; height:0px; border-color:transparent; background:transparent;"></td>';
					*/
					html_ventasnetas +='</tr>';
					
                    html_ventasnetas +='<tfoot>';
					html_ventasnetas += html_footer;
					html_ventasnetas +='</tfoot>';
                    html_ventasnetas += '</table>';
                    
                    $div_ventas_netas_productofactura.append(html_ventasnetas);
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-350;
                    var pix_alto=alto+'px';
                    $('#ventas').tableScroll({
                        height:parseInt(pix_alto)
					});
                });
            }else{
				jAlert('Debe elegir el rango la Fecha Inicial y su Fecha Final par la Busqueda.', 'Atencion!', function(r) { 
					//$fecha_inicial.focus();
					$('#lienzo_recalculable').find('input[name=fecha_inicial]').trigger('click');
				});
            }
        }//FIN DE LA VISTA DE  SUMARIZADO POR CLIENTE

    });
    
    
    
    $aplicar_evento_keypress($select_tipo_reporte, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($select_tipo_costo, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($fecha_inicial, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($fecha_final, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($Nombre_Cliente, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($Nombre_Producto, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($select_linea, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($select_marca, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($select_familia, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($select_subfamilia, $Buscar_ventas_netasproductofactura);
    $aplicar_evento_keypress($select_agente, $Buscar_ventas_netasproductofactura);
    
    var agent = '<option value= "0" >[--Todos--]</option>';
    $select_agente.append(agent);
    
    $select_tipo_reporte.focus();
});




