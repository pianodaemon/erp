$(function() {

    var config =  {
        empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
        sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
        tituloApp: 'Movimientos de Inventario',
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
            return this.contextpath + "/controllers/repinvmovimientos";
        //  return this.controller;
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

    $('#header').find('#header1').find('span.emp').text(config.getEmp());
    $('#header').find('#header1').find('span.suc').text(config.getSuc());
    $('#header').find('#header1').find('span.username').text(config.getUserName());
    //aqui va el titulo del catalogo
    $('#barra_titulo').find('#td_titulo').append(config.getTituloApp());

    $('#barra_acciones').hide();
    //barra para el buscador
    $('#barra_buscador').hide();

    var $tabla_movimientos = $('#lienzo_recalculable').find('#table_movimientos');
    var $select_tipo_mov = $('#lienzo_recalculable').find('select[name=select_tipo_mov]');
    var $select_almacen = $('#lienzo_recalculable').find('select[name=select_almacen]');
    var $codigo = $('#lienzo_recalculable').find('input[name=codigo]');
    var $descripcion = $('#lienzo_recalculable').find('input[name=descripcion]');
    var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
    var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');

    var $buscar_producto = $('#lienzo_recalculable').find('#buscar_producto');
    var $boton_busqueda = $('#lienzo_recalculable').find('#boton_busqueda');
    var $boton_genera_pdf = $('#lienzo_recalculable').find('#boton_genera_pdf');


    //buscador de productos
    $busca_productos = function(sku_buscar,descripcion_buscar){
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
        var input_json_tipos = config.getUrlForGetAndPost() + '/getProductoTipos.json'
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


        $campo_sku.val(sku_buscar);
        $campo_descripcion.val(descripcion_buscar);

        //click buscar productos
        $buscar_plugin_producto.click(function(event){
            //event.preventDefault();
            $arreglo = {
                sku:$campo_sku.val(),
                tipo:$select_tipo_producto.val(),
                descripcion:$campo_descripcion.val(),
                iu:config.getUi()
            };

            var restful_json_service = config.getUrlForGetAndPost() + '/getBuscadorProductos.json'

            var trr = '';
            $tabla_resultados.children().remove();
            $.post(restful_json_service,$arreglo,function(entry){
                $.each(entry['Productos'],function(entryIndex,producto){
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
                    //asignar  descripcion
                    $codigo.val($(this).find('span.sku_prod_buscador').html());
                    $descripcion.val($(this).find('span.titulo_prod_buscador').html());

                    //elimina la ventana de busqueda
                    var remove = function() {
                        $(this).remove();
                    };
                    $('#forma-buscaproducto-overlay').fadeOut(remove);
                    //asignar el enfoque al campo sku del producto
                    $('#forma-entradamercancias-window').find('input[name=sku_producto]').focus();
                });

            });
        })


        //si hay algo en el campo sku al cargar el buscador, ejecuta la busqueda
        if($campo_descripcion.val() != ''){
            $buscar_plugin_producto.trigger('click');
        }

        if($campo_sku.val() != ''){
            $buscar_plugin_producto.trigger('click');
        }

        $cancelar_plugin_busca_producto.click(function(event){
            event.preventDefault();
            var remove = function() {
                $(this).remove();
            };
            $('#forma-buscaproducto-overlay').fadeOut(remove);
        });
    }//termina buscador de productos

    $buscar_producto.click(function(event){
        event.preventDefault();
        $busca_productos($codigo.val(),$descripcion.val());//llamada a la funcion que busca productos
    });







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
    //$fecha_inicial.val(mostrarFecha());

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
        a.css({
            'z-index':100
        });
    });

    //aplicar mascara a campos para entrada manual de fecha
    //9 indica que va a permitir captura de numeros solamante
    $fecha_inicial.mask('9999-99-99');
    $fecha_final.mask('9999-99-99');


    //obtiene los almacenes para el reporte
    var input_json = config.getUrlForGetAndPost()+'/getDatosBusqueda.json';
    $arreglo = {
        'iu':iu = $('#lienzo_recalculable').find('input[name=iu]').val()
        };
    $.post(input_json,$arreglo,function(entry){

        $select_tipo_mov.children().remove();
        var tm_hmtl = '<option value="0" >[--Todos--]</option>';
        $.each(entry['Tmov'],function(entryIndex,tm){
            tm_hmtl += '<option value="' + tm['id'] + '"  >' + tm['titulo'] + '</option>';
        });
        $select_tipo_mov.append(tm_hmtl);

        $select_almacen.children().remove();
        //var almacen_hmtl = '<option value="0" selected="yes">[--Seleccionar Almacen--]</option>';
        var almacen_hmtl = '';
        $.each(entry['Almacenes'],function(entryIndex,alm){
            almacen_hmtl += '<option value="' + alm['id'] + '"  >' + alm['titulo'] + '</option>';
        });
        $select_almacen.append(almacen_hmtl);

    });//termina llamada json



    $boton_genera_pdf.click(function(event){
        //event.preventDefault();
        var codigo="";
        var descripcion="";


        if($codigo.val()== ""){
            codigo="0";
        }else{
            codigo=$codigo.val();
        }
        if($descripcion.val()== ""){
            descripcion="0";
        }else{
            descripcion=$descripcion.val();
        }



        var cadena = $select_tipo_mov.val() +"___"+ $select_almacen.val() +"___"+ codigo+"___"+ descripcion + "___"+$fecha_inicial.val()+ "___"+$fecha_final.val();

        var input_json = config.getUrlForGetAndPost() + '/getReporteMovimientos/'+cadena+'/'+config.getUi()+'/out.json';


        if(parseInt($select_almacen.val()) > 0){
            if($codigo.val()!='' || $descripcion.val() !='' ){
                if($fecha_inicial.val()!= "" && $fecha_final.val() !=""){
                    window.location.href=input_json;
                }else{
                    jAlert("Se requieren ambas Fechas de movimiento.",'Atencion!!!!!');
                }
            }else{
                jAlert("Se requieren codigo o una descripcion",'Atencion!!!!!');
            }
        }else{
            jAlert("Selecciona un Almacen.",'Atencion!!!!!');
        }


    });



    var height2 = $('#cuerpo').css('height');
    var alto = parseInt(height2)-240;
    var pix_alto=alto+'px';

    $('#table_movimientos').tableScroll({
        height:parseInt(pix_alto)
        });



    //ejecutar busqueda del reporte
    $boton_busqueda.click(function(event){


        $tabla_movimientos.find('tbody').children().remove();
        var input_json = config.getUrlForGetAndPost()+'/getMovimientos.json';


        //if($select_tipo_mov.val()!=0){
        if(parseInt($select_almacen.val()) > 0){
            if($codigo.val()!='' || $descripcion.val() !='' ){
                if($fecha_inicial.val()!= "" && $fecha_final.val() !=""){

                    $arreglo = {
                        'id_tipo_movimiento':$select_tipo_mov.val(),
                        'id_almacen':$select_almacen.val(),
                        'codigo':$codigo.val(),
                        'descripcion':$descripcion.val(),
                        'fecha_inicial':$fecha_inicial.val(),
                        'fecha_final':$fecha_final.val(),
                        'iu': config.getUi()
                    };

                    $.post(input_json,$arreglo,function(entry){
                        //alert("entraa aqui");

                        var suma_cantidad  = 0.0;
                        var valor_unitario = 0.0;
                        var valor_entrada  = 0.0;
                        var valor_salida   = 0.0;
                        var tmp=0;
                        var trr='';
                        
                        //alert("este s el codigo:  "+codigo);
                        if(entry['Movimientos'].length > 0 ){
                            var codigo = entry['Movimientos']['0']['codigo'];

                            trr += '<tr>';
                            trr += '<td width="100px" colspan="2"><b>Codigo: '+codigo+'</b></td>';
                            trr += '<td width="300px" colspan="2"><b>Descripcion: '+entry['Movimientos']['0']['descripcion']+'</b></td>';
                            trr += '<td width="200px">&nbsp;</td>';
                            trr += '<td width="100px">&nbsp;</td>';
                            trr += '<td width="100px">&nbsp;</td>';
                            trr += '<td width="100px" align="right"><b>Existencia&nbsp;Inicial</b></td>';
                            trr += '<td width="100px" align="right"><b>'+entry['Movimientos']['0']['existencia']+'</b></td>';
                            trr += '</tr>';
                            
                            $.each(entry['Movimientos'],function(entryIndex,Movimentos){
                                trr += '<tr>';
                                trr += '<td width="100px" align="center">'+Movimentos['referencia']+'</td>';
                                trr += '<td width="300px" align="left">'+Movimentos['tipo_movimiento']+'</td>';
                                trr += '<td width="100px" align="center">'+Movimentos['fecha_movimiento']+'</td>';
                                trr += '<td width="200px" align="left">'+Movimentos['sucursal']+'</td>';
                                trr += '<td width="200px" align="left">'+Movimentos['almacen']+'</td>';
                                trr += '<td width="100px" align="left">'+Movimentos['unidad']+'</td>';
                                trr += '<td width="100px" align="right">'+Movimentos['cantidad']+'</td>';
                                trr += '<td width="100px" align="right">'+Movimentos['costo']+'</td>';
                                trr += '<td width="100px" align="right">'+Movimentos['existencia_actual']+'</td>';
                                trr += '</tr>';
                                
                                suma_cantidad  = parseFloat(suma_cantidad)  + parseFloat(Movimentos['cantidad']);
                                valor_unitario = parseFloat(valor_unitario) + parseFloat(Movimentos['costo']);
                                valor_entrada  = parseFloat(valor_entrada)  + parseFloat(Movimentos['costo']);
                                valor_salida   = parseFloat(valor_salida )  + parseFloat(Movimentos['costo']);
                            });


                            $tabla_movimientos.find('tbody').append(trr);


                        }else{
                            jAlert("Esta consulta no genero Resultados",'Atencion!!!');
                        }


                        var height2 = $('#cuerpo').css('height');
                        var alto = parseInt(height2)-240;
                        var pix_alto=alto+'px';

                        $('#table_movimientos').tableScroll({
                            height:parseInt(pix_alto)
                            });
                    });//termina llamada json

                }else{
                    jAlert("Se requieren ambas Fechas de movimiento.",'Atencion!!!!!');
                }
            }else{

                jAlert("Ingrese un codigo o una Descripcion.",'Atencion!!!!!');
            }

        }else{
            jAlert("Selecciona un Almacen.",'Atencion!!!!!');
        }
    //}else{
    //    jAlert("Selecciona un Tipo de Movimiento.",'Atencion!!!!!');
    //}







    });




    $aplicar_evento_keypress($select_tipo_mov, $boton_busqueda);
    $aplicar_evento_keypress($select_almacen, $boton_busqueda);
    $aplicar_evento_keypress($codigo, $boton_busqueda);
    $aplicar_evento_keypress($descripcion, $boton_busqueda);
    $aplicar_evento_keypress($fecha_inicial, $boton_busqueda);
    $aplicar_evento_keypress($fecha_final, $boton_busqueda);
    $select_tipo_mov.focus();

});



