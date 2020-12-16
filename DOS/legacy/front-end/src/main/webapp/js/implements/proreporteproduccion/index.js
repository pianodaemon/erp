$(function() {

    var config =  {
        tituloApp: 'Reporte Produccion' ,
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
            return this.contextpath + "/controllers/proreporteproduccion";
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


    var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
    var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
    var $genera_PDF_reporte_produccion = $('#lienzo_recalculable').find('div.repproduccion').find('table#filtros tr td').find('input[value$=Generar_PDF]');
    var $Buscar_reporte_produccion = $('#lienzo_recalculable').find('div.repproduccion').find('table#filtros tr td').find('input[value$=Buscar]');
    var $buscar_producto= $('#lienzo_recalculable').find('div.repproduccion').find('table#filtros tr td').find('a[href*=buscar_producto]');
    var $sku_producto = $('#lienzo_recalculable').find('div.repproduccion').find('table#filtros tr td').find('input[name=sku_producto]');
    var $producto = $('#lienzo_recalculable').find('div.repproduccion').find('table#filtros tr td').find('input[name=producto]');
    var $div_tabla_resultados= $('#tablaresultadosproduccion');
    var html_trs="";
    var $select_tipo_reporte = $('#lienzo_recalculable').find('div.repproduccion').find('table#filtros tr td').find('select[name=produccion]');
    var $tr_produccion_diaria = $('#lienzo_recalculable').find('#R_produccion_diaria');
    var $tr_produccionxproducto = $('#lienzo_recalculable').find('#R_produccion_por_producto').hide();
    var $tr_produccionxequipo = $('#lienzo_recalculable').find('#R_produccion_por_Equipo').hide();
    var $tr_produccionxoperario = $('#lienzo_recalculable').find('#R_produccion_por_Operario').hide();
    var $select_tipo_equipo= $('#lienzo_recalculable').find('div.repproduccion').find('table#filtros tr td').find('select[name=equipo]');
    var $select_Operario= $('#lienzo_recalculable').find('div.repproduccion').find('table#filtros tr td').find('select[name=Operario]');

    produccion_diaria=function(){

        $div_tabla_resultados.children().remove();
        var html_trs="";
        if($fecha_inicial.val() != "" && $fecha_final.val() != ""){
            var arreglo_parametros = {
                id_operario:$select_Operario.val(),
                id_equipo:$select_tipo_equipo.val(),
                tipo_reporte:$select_tipo_reporte.val(),
                fecha_inicial : $fecha_inicial.val() ,
                fecha_final : $fecha_final.val(),
                sku:$sku_producto.val(),
                sku_descripcion:$producto.val(),
                iu:config.getUi()
            };
            var restful_json_service = config.getUrlForGetAndPost() + '/getProduccion.json'

            $.post(restful_json_service,arreglo_parametros,function(entry){
                if(entry['Datos'].length > 0){

                    html_trs+='<table id="resultados" width="100%">'
                    html_trs+='<thead> <tr>'
                    html_trs+='<td >Folio Orden</td>'
                    html_trs+='<td >Lote</td>'
                    html_trs+='<td >F. Elaboracion</td>'
                    html_trs+='<td >codigo</td>'
                    html_trs+='<td >Descripcion</td>'
                    html_trs+='<td >Equipo</td>'
                    html_trs+='<td >Subproceso</td>'
                    html_trs+='<td >Operador</td>'
                    html_trs+='<td >cantidad</td>'
                    html_trs+='</tr> </thead>'
                    var folio_orden=entry['Datos'][0]["folio_orden"];
                    var suma_cantidad=0.0;
                    var tmp= 0;

                    for(var i=0; i<entry['Datos'].length; i++){
                        if(folio_orden == entry['Datos'][i]["folio_orden"]){
                            if(tmp==0){
                                html_trs+='<tr>'
                                html_trs+='<td >'+entry['Datos'][i]["folio_orden"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["numero_lote"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["fecha_elaboracion"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["codigo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["descripcion"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                                html_trs+='<td align="right">'+$(this).agregar_comas(parseFloat(entry['Datos'][i]["cantidad"]).toFixed(2))+'</td>'
                                html_trs+=' </tr>'
                                suma_cantidad=suma_cantidad+parseFloat(entry['Datos'][i]["cantidad"]);

                            }
                            if(tmp != 0){
                                html_trs+='<tr>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                                html_trs+='<td align="right">0.0</td>'
                                html_trs+=' </tr>'

                            }
                            tmp=1;

                        }else{

                            html_trs+='<tr>'
                            html_trs+='<td >'+entry['Datos'][i]["folio_orden"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["numero_lote"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["fecha_elaboracion"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["codigo"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["descripcion"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                            html_trs+='<td align="right">'+$(this).agregar_comas(parseFloat(entry['Datos'][i]["cantidad"]).toFixed(2))+'</td>'
                            html_trs+=' </tr>'

                            suma_cantidad=suma_cantidad+parseFloat(entry['Datos'][i]["cantidad"]);
                            folio_orden=entry['Datos'][i]["folio_orden"];
                        }

                    }

                    html_trs +='<tfoot>';
                    html_trs +='<tr>';
                    html_trs+='<td colspan="8">&nbsp;</td>'
                    html_trs +='<td  align="right">'+$(this).agregar_comas(parseFloat(suma_cantidad).toFixed(2))+'</td>';
                    html_trs +='</tr>';
                    html_trs +='</table>';

                    $div_tabla_resultados.append(html_trs);

                }else{
                    jAlert("Esta consulta no genero ningun Resultado pruebe ingresando otros Parametros",'Atencion!!!!')
                }


                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-275;
                var pix_alto=alto+'px';
                $('#resultados').tableScroll({
                    height:parseInt(pix_alto)
                });
            });

        }else{
            jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
        }

    }



    produccion_por_producto=function(){

        $div_tabla_resultados.children().remove();
        var fecha=mostrarFecha();
        //alert("fechasss:"+fecha);
        var html_trs="";
        if($fecha_inicial.val() != "" && $fecha_final.val() != ""){


            var arreglo_parametros = {
                fecha_inicial:fecha,
                fecha_final:fecha,
                id_operario:$select_Operario.val(),
                id_equipo:$select_tipo_equipo.val(),
                tipo_reporte:$select_tipo_reporte.val(),
                fecha_inicial : $fecha_inicial.val() ,
                fecha_final : $fecha_final.val(),
                sku:$sku_producto.val(),
                sku_descripcion:$producto.val(),
                iu:config.getUi()
            };
            var restful_json_service = config.getUrlForGetAndPost() + '/getProduccion.json'

            $.post(restful_json_service,arreglo_parametros,function(entry){
                if(entry['Datos'].length > 0){

                    html_trs+='<table id="resultados" width="100%">'
                    html_trs+='<thead> <tr>'
                    html_trs+='<td >Folio Orden</td>'
                    html_trs+='<td >Lote</td>'
                    html_trs+='<td >F. Elaboracion</td>'
                    html_trs+='<td >codigo</td>'
                    html_trs+='<td >Descripcion</td>'
                    //html_trs+='<td >Equipo</td>'
                    // html_trs+='<td >Subproceso</td>'
                    // html_trs+='<td >Operador</td>'
                    html_trs+='<td >cantidad</td>'
                    //html_trs+='<td >unidad</td>'
                    html_trs+='</tr> </thead>'

                    var folio_orden=entry['Datos'][0]["folio_orden"];
                    var suma_cantidad=0.0;
                    var tmp= 0;

                    for(var i=0; i<entry['Datos'].length; i++){
                        if(folio_orden == entry['Datos'][i]["folio_orden"]){
                            if(tmp==0){
                                html_trs+='<tr>'
                                html_trs+='<td >'+entry['Datos'][i]["folio_orden"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["numero_lote"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["fecha_elaboracion"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["codigo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["descripcion"]+'</td>'
                                //html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                                //html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                                //html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                                html_trs+='<td align="right">'+$(this).agregar_comas(parseFloat(entry['Datos'][i]["cantidad"]).toFixed(2))+'</td>'
                                html_trs+=' </tr>'
                                suma_cantidad=suma_cantidad+parseFloat(entry['Datos'][i]["cantidad"]);
                            }
                            if(tmp != 0){
                            /*html_trs+='<tr>'
                                                html_trs+='<td >'+""+'</td>'
                                                html_trs+='<td >'+""+'</td>'
                                                html_trs+='<td >'+""+'</td>'
                                                html_trs+='<td >'+""+'</td>'
                                                html_trs+='<td >'+""+'</td>'
                                                //html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                                                //html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                                                //html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                                                html_trs+='<td align="right">0.0</td>'
                                              html_trs+=' </tr>'
                                            */
                            }
                            tmp=1;
                        }else{

                            html_trs+='<tr>'
                            html_trs+='<td >'+entry['Datos'][i]["folio_orden"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["numero_lote"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["fecha_elaboracion"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["codigo"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["descripcion"]+'</td>'
                            //html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                            //html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                            //html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                            html_trs+='<td align="right">'+$(this).agregar_comas(parseFloat(entry['Datos'][i]["cantidad"]).toFixed(2))+'</td>'
                            html_trs+=' </tr>'

                            suma_cantidad=suma_cantidad+parseFloat(entry['Datos'][i]["cantidad"]);
                            folio_orden=entry['Datos'][i]["folio_orden"];
                        }
                    //alert("esta es la suma:"+suma_cantidad);
                    }
                    html_trs +='<tfoot>';
                    html_trs +='<tr>';
                    html_trs+='<td colspan="5">&nbsp;</td>'
                    html_trs +='<td  align="right">'+$(this).agregar_comas(parseFloat(suma_cantidad).toFixed(2))+'</td>';

                    html_trs +='</tr>';
                    html_trs +='</table>';

                    $div_tabla_resultados.append(html_trs);

                }else{
                    jAlert("Esta consulta no genero ningun Resultado pruebe ingresando otros Parametros",'Atencion!!!!')
                }


                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-275;
                var pix_alto=alto+'px';
                $('#resultados').tableScroll({
                    height:parseInt(pix_alto)
                });

            });
        }else{
            jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
        }

    }

    produccion_por_equipo=function(){

        $div_tabla_resultados.children().remove();
        var html_trs="";
        if($fecha_inicial.val() != "" && $fecha_final.val() != ""){
            var arreglo_parametros = {
                id_operario:$select_Operario.val(),
                id_equipo:$select_tipo_equipo.val(),
                tipo_reporte:$select_tipo_reporte.val(),
                fecha_inicial : $fecha_inicial.val() ,
                fecha_final : $fecha_final.val(),
                sku:$sku_producto.val(),
                sku_descripcion:$producto.val(),
                iu:config.getUi()
            };
            var restful_json_service = config.getUrlForGetAndPost() + '/getProduccion.json'

            $.post(restful_json_service,arreglo_parametros,function(entry){
                if(entry['Datos'].length > 0){

                    html_trs+='<table id="resultados" width="100%">'
                    html_trs+='<thead> <tr>'
                    html_trs+='<td >Folio Orden</td>'
                    html_trs+='<td >Lote</td>'
                    html_trs+='<td >F. Elaboracion</td>'
                    html_trs+='<td >codigo</td>'
                    html_trs+='<td >Descripcion</td>'
                    html_trs+='<td >Equipo</td>'
                    html_trs+='<td >Subproceso</td>'
                    //html_trs+='<td >Operador</td>'

                    html_trs+='<td >cantidad</td>'
                    html_trs+='</tr> </thead>'
                    var folio_orden=entry['Datos'][0]["folio_orden"];

                    var suma_cantidad=0.0;
                    var tmp= 0;
                    for(var i=0; i<entry['Datos'].length; i++){
                        if(folio_orden == entry['Datos'][i]["folio_orden"]){
                            if(tmp==0){
                                html_trs+='<tr>'
                                html_trs+='<td >'+entry['Datos'][i]["folio_orden"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["numero_lote"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["fecha_elaboracion"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["codigo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["descripcion"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                                //html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                                html_trs+='<td align="right">'+$(this).agregar_comas(parseFloat(entry['Datos'][i]["cantidad"]).toFixed(2))+'</td>'
                                html_trs+=' </tr>'
                                suma_cantidad=suma_cantidad+parseFloat(entry['Datos'][i]["cantidad"]);
                            }
                            if(tmp != 0){
                                html_trs+='<tr>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                                //html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                                html_trs+='<td align="right">0.0</td>'
                                html_trs+=' </tr>'
                            }
                            tmp=1;
                        }else{

                            html_trs+='<tr>'
                            html_trs+='<td >'+entry['Datos'][i]["folio_orden"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["numero_lote"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["fecha_elaboracion"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["codigo"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["descripcion"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                            //html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'

                            html_trs+='<td align="right">'+$(this).agregar_comas(parseFloat(entry['Datos'][i]["cantidad"]).toFixed(2))+'</td>'
                            //html_trs+='<td >'+entry['Datos'][i]["unidad"]+'</td>'
                            html_trs+=' </tr>'

                            suma_cantidad=suma_cantidad+parseFloat(entry['Datos'][i]["cantidad"]);
                            folio_orden=entry['Datos'][i]["folio_orden"];
                        }
                    //alert("esta es la suma:"+suma_cantidad);
                    }
                    html_trs +='<tfoot>';
                    html_trs +='<tr>';
                    html_trs+='<td colspan="7">&nbsp;</td>'
                    html_trs +='<td  align="right">'+$(this).agregar_comas(parseFloat(suma_cantidad).toFixed(2))+'</td>';
                    html_trs +='</tr>';
                    html_trs +='</table>';

                    $div_tabla_resultados.append(html_trs);

                }else{
                    jAlert("Esta consulta no genero ningun Resultado pruebe ingresando otros Parametros",'Atencion!!!!')
                }


                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-275;
                var pix_alto=alto+'px';
                $('#resultados').tableScroll({
                    height:parseInt(pix_alto)
                });
            });

        }else{
            jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
        }
    }

    produccion_por_operario=function(){

        $div_tabla_resultados.children().remove();
        var html_trs="";

        if($fecha_inicial.val() != "" && $fecha_final.val() != ""){
            var arreglo_parametros = {
                id_operario:$select_Operario.val(),
                id_equipo:$select_tipo_equipo.val(),
                tipo_reporte:$select_tipo_reporte.val(),
                fecha_inicial : $fecha_inicial.val() ,
                fecha_final : $fecha_final.val(),
                sku:$sku_producto.val(),
                sku_descripcion:$producto.val(),
                iu:config.getUi()
            };
            var restful_json_service = config.getUrlForGetAndPost() + '/getProduccion.json'

            $.post(restful_json_service,arreglo_parametros,function(entry){
                if(entry['Datos'].length > 0){

                    html_trs+='<table id="resultados" width="100%">'
                    html_trs+='<thead> <tr>'
                    html_trs+='<td >Folio Orden</td>'
                    html_trs+='<td >Lote</td>'
                    html_trs+='<td >F. Elaboracion</td>'
                    html_trs+='<td >codigo</td>'
                    html_trs+='<td >Descripcion</td>'
                    //html_trs+='<td >Equipo</td>'
                    html_trs+='<td >Subproceso</td>'
                    html_trs+='<td >Operador</td>'
                    html_trs+='<td >cantidad</td>'


                    html_trs+='</tr> </thead>'
                    var folio_orden=entry['Datos'][0]["folio_orden"];

                    var suma_cantidad=0.0;
                    var tmp= 0;
                    for(var i=0; i<entry['Datos'].length; i++){
                        if(folio_orden == entry['Datos'][i]["folio_orden"]){
                            if(tmp==0){
                                html_trs+='<tr>'
                                html_trs+='<td >'+entry['Datos'][i]["folio_orden"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["numero_lote"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["fecha_elaboracion"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["codigo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["descripcion"]+'</td>'
                                //          html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                                html_trs+='<td align="right">'+$(this).agregar_comas(parseFloat(entry['Datos'][i]["cantidad"]).toFixed(2))+'</td>'
                                html_trs+=' </tr>'
                                suma_cantidad=suma_cantidad+parseFloat(entry['Datos'][i]["cantidad"]);
                            }
                            if(tmp != 0){
                                html_trs+='<tr>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                html_trs+='<td >'+""+'</td>'
                                //            html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                                html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'
                                html_trs+='<td align="right">0.0</td>'
                                html_trs+=' </tr>'
                            }
                            tmp=1;
                        }else{

                            html_trs+='<tr>'
                            html_trs+='<td >'+entry['Datos'][i]["folio_orden"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["numero_lote"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["fecha_elaboracion"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["codigo"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["descripcion"]+'</td>'
                            //        html_trs+='<td >'+entry['Datos'][i]["nombre_equipo"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["subproceso"]+'</td>'
                            html_trs+='<td >'+entry['Datos'][i]["nombre_operador"]+'</td>'

                            html_trs+='<td align="right">'+$(this).agregar_comas(parseFloat(entry['Datos'][i]["cantidad"]).toFixed(2))+'</td>'
                            //html_trs+='<td >'+entry['Datos'][i]["unidad"]+'</td>'
                            html_trs+=' </tr>'

                            suma_cantidad=suma_cantidad+parseFloat(entry['Datos'][i]["cantidad"]);
                            folio_orden=entry['Datos'][i]["folio_orden"];
                        }
                    //alert("esta es la suma:"+suma_cantidad);
                    }
                    html_trs +='<tfoot>';
                    html_trs +='<tr>';
                    html_trs+='<td colspan="7">&nbsp;</td>'
                    html_trs +='<td  align="right">'+$(this).agregar_comas(parseFloat(suma_cantidad).toFixed(2))+'</td>';

                    html_trs +='</tr>';
                    html_trs +='</table>';

                    $div_tabla_resultados.append(html_trs);

                }else{
                    jAlert("Esta consulta no genero ningun Resultado pruebe ingresando otros Parametros",'Atencion!!!!')
                }


                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-275;
                var pix_alto=alto+'px';
                $('#resultados').tableScroll({
                    height:parseInt(pix_alto)
                });
            });
        }else{
            jAlert("Elija Una Fecha inicial y una Fecha Final",'! Atencion');
        }
    }



    $select_tipo_reporte.change(function(){
        if ($select_tipo_reporte.val() == 1){
            produccion_diaria();
        }

        if ($select_tipo_reporte.val() == 2){
            produccion_por_producto();
        }
        if ($select_tipo_reporte.val() == 3){
            produccion_por_equipo();
        }
        if ($select_tipo_reporte.val() == 4){
            produccion_por_operario();
        }

    });

    //Cargando los equipos disponibles
    var restful_json_service = config.getUrlForGetAndPost() + '/getEquipos.json'
    $arreglo = {
        iu:config.getUi()
    };
    $.post(restful_json_service,$arreglo,function(data,equipo){
        //Llena el select tipos de productos en el buscador
        $select_tipo_equipo.children().remove();
        var Equipos_html = '<option value="0" selected="yes">[--Seleccionar Equipo--]</option>';
        $.each(data['Equipos'],function(data,equipo){
            Equipos_html += '<option value="' + equipo['id'] + '"  >' + equipo['titulo'] + '</option>';
        });
        $select_tipo_equipo.append(Equipos_html);
    });

    //Cargando los operarios
    var restful_json_service = config.getUrlForGetAndPost() + '/getOperarios.json'
    $arreglo = {
        iu:config.getUi()
    };
    $.post(restful_json_service,$arreglo,function(data,Operario){
        //Llena el select tipos de productos en el buscador
        $select_Operario.children().remove();
        var Operario_html = '<option value="0" selected="yes">[--Seleccionar Operario--]</option>';
        $.each(data['Operarios'],function(data,Operario){
            Operario_html += '<option value="' + Operario['id'] + '"  >' + Operario['nombre_operario'] + '</option>';
        });
        $select_Operario.append(Operario_html);
    });



    //buscador de productos
    $busca_productos = function(sku_buscar,descripcion_buscar){
        //limpiar_campos_grids();
        $(this).modalPanel_Buscaproducto();
        var $dialogoc =  $('#forma-buscaproducto-window');
        //var $dialogoc.prependTo('#forma-buscaproduct-window');
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
                    $producto.val($(this).find('span.titulo_prod_buscador').html());
                    $sku_producto.val($(this).find('span.sku_prod_buscador').html());

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
            //event.preventDefault();
            var remove = function() {
                $(this).remove();
            };
            $('#forma-buscaproducto-overlay').fadeOut(remove);
        });
    }//termina buscador de productos

    $buscar_producto.click(function(event){
        event.preventDefault();
        $busca_productos($sku_producto.val(),$producto.val());//llamada a la funcion que busca productos
    });

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
        a.css({
            'z-index':100
        });
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
        a.css({
            'z-index':100
        });
    });



    mostrarFecha($fecha_final.val());

    //click generar reporte de visitas
    //genera pdf del reporte
    $genera_PDF_reporte_produccion.click(function(event){

        event.preventDefault();
        var fecha=mostrarFecha();
        var sku ="";
        var descripcion_sku="";
        var iu=config.getUi();
        if($producto.val()==""){
            descripcion_sku="0";
        }else{
            descripcion_sku=$producto.val();
        }

        if($sku_producto.val()==""){
            sku="0";
        }else{
            sku=$sku_producto.val();
        }

        if ($select_tipo_reporte.val() == 1){
            if($fecha_inicial.val() != "" && $fecha_final.val() !=""){
                //var arreglo_parametros = {id_operario:$select_Operario.val(),id_equipo:$select_tipo_equipo.val(), tipo_reporte:$select_tipo_reporte.val(),fecha_inicial : $fecha_inicial.val() , fecha_final : $fecha_final.val(),sku:$sku_producto.val(),sku_descripcion:$producto.val(), iu:config.getUi()};
                var cadena =$select_Operario.val()+"___"+$select_tipo_equipo.val()+"___"+$select_tipo_reporte.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val()+"___"+sku+"___"+descripcion_sku+"___"+iu;
                var input_json = config.getUrlForGetAndPost() + '/Crear_PDF_Produccion/'+cadena+'/'+config.getUi()+'/out.json';
                window.location.href=input_json;
            }else{
                jAlert("Debe elegir el rango la fecha inicial y su fecha final ","Atencion!!!")
            }
        }
        if ($select_tipo_reporte.val() == 2){
            if($fecha_inicial.val() != "" && $fecha_final.val() !=""){
                var cadena =$select_Operario.val()+"___"+$select_tipo_equipo.val()+"___"+$select_tipo_reporte.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val()+"___"+sku+"___"+descripcion_sku+"___"+iu;
                var input_json = config.getUrlForGetAndPost() + '/Crear_PDF_Produccion/'+cadena+'/'+config.getUi()+'/out.json';
                window.location.href=input_json;
            }else{
                jAlert("Debe elegir el rango la fecha inicial y su fecha final ","Atencion!!!")
            }
        }
        if ($select_tipo_reporte.val() == 3){
            if($fecha_inicial.val() != "" && $fecha_final.val() !=""){
                var cadena =$select_Operario.val()+"___"+$select_tipo_equipo.val()+"___"+$select_tipo_reporte.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val()+"___"+sku+"___"+descripcion_sku+"___"+iu;
                var input_json = config.getUrlForGetAndPost() + '/Crear_PDF_Produccion/'+cadena+'/'+config.getUi()+'/out.json';
                window.location.href=input_json;
            }else{
                jAlert("Debe elegir el rango la fecha inicial y su fecha final ","Atencion!!!")
            }
        }
        if ($select_tipo_reporte.val() == 4 ){
            if($fecha_inicial.val() != "" && $fecha_final.val() !=""){
                var cadena =$select_Operario.val()+"___"+$select_tipo_equipo.val()+"___"+$select_tipo_reporte.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val()+"___"+sku+"___"+descripcion_sku+"___"+iu;
                var input_json = config.getUrlForGetAndPost() + '/Crear_PDF_Produccion/'+cadena+'/'+config.getUi()+'/out.json';
                window.location.href=input_json;
            }else{
                jAlert("Debe elegir el rango la fecha inicial y su fecha final ","Atencion!!!")
            }
        }
    });//termina llamada json

    $Buscar_reporte_produccion.click(function(event){
        if ($select_tipo_reporte.val() == 1){
            produccion_diaria();
        }

        if ($select_tipo_reporte.val() == 2){
            produccion_por_producto();
        }
        if ($select_tipo_reporte.val() == 3){
            produccion_por_equipo();
        }
        if ($select_tipo_reporte.val() == 4){
            produccion_por_operario();
        }

    });

    $(this).aplicarEventoKeypressEjecutaTrigger($fecha_inicial, $Buscar_reporte_produccion);
    $(this).aplicarEventoKeypressEjecutaTrigger($fecha_final, $Buscar_reporte_produccion);
    $(this).aplicarEventoKeypressEjecutaTrigger($sku_producto, $Buscar_reporte_produccion);
    $(this).aplicarEventoKeypressEjecutaTrigger($producto, $Buscar_reporte_produccion);
    $(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_reporte, $Buscar_reporte_produccion);
    $(this).aplicarEventoKeypressEjecutaTrigger($select_tipo_equipo, $Buscar_reporte_produccion);
    $(this).aplicarEventoKeypressEjecutaTrigger($select_Operario, $Buscar_reporte_produccion);
	
    $select_tipo_reporte.focus();

});
