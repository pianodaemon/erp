$(function() {
    var config =  {
        empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
        sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),

        tituloApp: 'Pronosticos de cobranza' ,
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
            return this.contextpath + "/controllers/reppronoscobranza";
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


    var $select_cobranza = $('#lienzo_recalculable').find('select[name=cobranza]');
    var $select_semanas = $('#lienzo_recalculable').find('select[name=semanas]');
    var $genera_reporte_pronostico_cobranza = $('#lienzo_recalculable').find('div.rep_pron_cobranza').find('table#opciones tr td').find('input[value$=Generar_PDF]');
    var $div_fechas = $('#fechas');
    var $div_pronostico_cobranza = $('#pronostico_cobranza');
    var $div_fechaspronostico_cobranza = $('#fechaspronostico_cobranza');
    var $div_pronostico_cobranza_semana = $('#pronostico_cobranza_semana');
    var $div_pronostico_cobranza_semanas = $('#pronostico_cobranza_semanas');

    //click generar reporte de pronostico de Cobranza
    $genera_reporte_pronostico_cobranza.click(function(event){
        event.preventDefault();
        var opcion_seleccionada = $select_cobranza.val();
        var numero_semanas = $select_semanas.val();
        if(numero_semanas != 0 && opcion_seleccionada !=0){
            var input_json = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza/'+opcion_seleccionada+'/'+numero_semanas+'/'+config.getUi()+'/out.json';
            window.location.href=input_json;
        }else{
            jAlert("Elija una opcion y una Semana","Atencion!!!")
        }
    });//termina llamada json


    $select_cobranza.change(function(event){
        event.preventDefault();

        var opcion_seleccionada = $select_cobranza.val();
        var numero_semanas = $select_semanas.val();

        var total_primer_semana=0;

        //alert("opcion_seleccionada:"+opcion_seleccionada + "   numero_semanas:"+numero_semanas);


        if(parseInt(opcion_seleccionada)==1 && parseInt(numero_semanas) != 0){
            $div_pronostico_cobranza_semanas.children().remove();
            var sumalunes = 0;
            var sumamartes = 0;
            var sumamiercoles = 0;
            var sumajueves = 0;
            var sumaviernes = 0;
            var sumatotal = 0;
            var sumatotal2=0.0;
            var total_segunda_semana=0.0;
            var total_tercera_semana=0.0;

            var html_footer ="";

            var primer_semana_lunes=0.0;
            var primer_semana_viernes=0.0;
            var segunda_semana_lunes=0.0;
            var segunda_semana_viernes=0.0;
            var tercer_semana_lunes=0.0;
            var tercer_semana_viernes=0.0;
            var cuarta_semana_lunes =0.0;
            var cuarta_semana_viernes =0.0;

            //$div_pronostico_cobranza.hide();
            //$div_pronostico_cobranza_semana.hide();
            //$div_pronostico_cobranza_semanas.show();


            if(parseInt(numero_semanas) == 1){
                var html_footer="";

                var arreglo_parametros = {
                    opcion_seleccionada : 1 ,
                    numero_semanas : 1,
                    iu:config.getUi()
                };

                var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;
                    var sumatotal=0.0;
                    for(var i=0; i<body_tabla.length; i++){
                        sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal +parseFloat( body_tabla[i]["total"]);
                        primer_semana_lunes=body_tabla[i]["lunes_proximo"];
                        primer_semana_viernes=body_tabla[i]["viernes_proximo"];
                    }

                    total_primer_semana =sumatotal;
                    var header_tabla = {
                        semana_1    :" ",
                        semana_2    :" ",
                        semana_3    :"",
                        semana_4    :'Semana 1',
                        semana_5    :'Semana 2',
                        semana_6    :'Semana 3',
                        semana_7    :'Semana 4',

                        total    :'Total x 4 semanas'
                    };
                    var html_pronostico_semanas = '<table id="pronosticos_semanas" width="60%" >';

                    html_pronostico_semanas +='<thead> <tr>';
                    for(var key in header_tabla){
                        var attrValue = header_tabla[key];
                        html_pronostico_semanas +='<td align="center">'+attrValue+'</td>';
                    }
                    html_pronostico_semanas +='</tr> </thead>';

                    html_pronostico_semanas +='<tr class="first">';
                    html_pronostico_semanas +='<td ALIGN="RIGHT">'+""+'</td>';
                    html_pronostico_semanas +='<td >'+""+'</td>';
                    html_pronostico_semanas +='<td >'+""+'</td>';
                    html_pronostico_semanas +='<td align="center">'+primer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AL&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+primer_semana_viernes+'</td>';
                    html_pronostico_semanas +='<td align="center">'+""+'</td>';
                    html_pronostico_semanas +='<td align="center">'+""+'</td>';
                    html_pronostico_semanas +='<td align="center">'+""+'</td>';
                    html_pronostico_semanas +='<td align="center">'+""+'</td>';
                    html_pronostico_semanas +='<tr>';

                    html_footer +='<tr class="first">';
                    html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                    html_footer +='<td align="center">'+""+'</td>';
                    html_footer +='<td align="right">'+""+'</td>';
                    html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';
                    html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                    html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                    html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                    html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';

                    //'+'<span id="total" style="display:none">'+body_tabla[i]["viernes"]+'</span>'+
                    html_pronostico_semanas +='<tfoot>';
                    html_pronostico_semanas += html_footer;
                    html_pronostico_semanas +='</tfoot>';
                    html_pronostico_semanas += '</table>';

                    $div_pronostico_cobranza_semanas.append(html_pronostico_semanas);
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-250;
                    var pix_alto=alto+'px';

                    $('#pronosticos_semanas').tableScroll({
                        height:parseInt(pix_alto)
                    });

                });
            }




            if(numero_semanas == 2){
                $div_pronostico_cobranza_semanas.children().remove();
                var html_footer="";
                var arreglo_parametros = {
                    opcion_seleccionada : 1 ,
                    numero_semanas : 1,
                    iu:config.getUi()
                };
                var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;
                    for(var i=0; i<body_tabla.length; i++){
                        sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal +parseFloat( body_tabla[i]["total"]);
                        primer_semana_lunes=body_tabla[i]["lunes_proximo"];
                        primer_semana_viernes=body_tabla[i]["viernes_proximo"];

                    }

                    var total_primer_semana =sumatotal;

                    union_de_dos_post(total_primer_semana,primer_semana_lunes,primer_semana_viernes);
                });



                var union_de_dos_post=function(total_primer_semana,primer_semana_lunes,primer_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 2,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;

                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal2 = sumatotal2 +parseFloat( body_tabla[i]["total"]);
                            segunda_semana_lunes=body_tabla[i]["lunes_proximo"];
                            segunda_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }
                        total_primer_semana=parseFloat(total_primer_semana);
                        total_segunda_semana=parseFloat(sumatotal2);
                        var  Tdossemanas =parseFloat(total_primer_semana) + parseFloat(total_segunda_semana);
                        var header_tabla = {
                            semana_1    :" ",
                            semana_2    :" ",
                            semana_3    :"",
                            semana_4    :'Semana 1',
                            semana_5    :'Semana 2',
                            semana_6    :'Semana 3',
                            semana_7    :'Semana 4',

                            total    :'Total x 4 semanas'
                        };
                        var html_pronostico_semanas = '<table id="pronosticos_semanas", width="60%">';

                        html_pronostico_semanas +='<thead> <tr>';
                        for(var key in header_tabla){
                            var attrValue = header_tabla[key];
                            html_pronostico_semanas +='<td align="center">'+attrValue+'</td>';
                        }
                        html_pronostico_semanas +='</tr></thead> ';

                        html_pronostico_semanas +='<tr  >';
                        html_pronostico_semanas +='<td ALIGN="RIGHT">'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+primer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AL&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+primer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+segunda_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AL&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+segunda_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='</tr>';

                        html_footer +='<tr class="first">';
                        html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                        html_footer +='<td align="center">'+""+'</td>';
                        html_footer +='<td align="right">'+""+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_segunda_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                        html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(Tdossemanas).toFixed(2))+'</td>';
                        html_footer +='</tr>';
                        //'+'<span id="total" style="display:none">'+body_tabla[i]["viernes"]+'</span>'+
                        html_pronostico_semanas +='<tfoot>';
                        html_pronostico_semanas += html_footer;
                        html_pronostico_semanas +='</tfoot>';
                        html_pronostico_semanas += '</table>';

                        $div_pronostico_cobranza_semanas.append(html_pronostico_semanas);
                        var height2 = $('#cuerpo').css('height');
                        var alto = parseInt(height2)-250;
                        var pix_alto=alto+'px';

                        $('#pronosticos_semanas').tableScroll({
                            height:parseInt(pix_alto)
                        });
                    });
                }
            }
            //finde si es semana2

            if(numero_semanas == 3){
                $div_pronostico_cobranza_semanas.children().remove();
                var html_footer="";
                var arreglo_parametros = {
                    opcion_seleccionada : 1 ,
                    numero_semanas : 1,
                    iu:config.getUi()
                };
                var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;
                    var sumatotal =0.0;
                    for(var i=0; i<body_tabla.length; i++){
                        sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal +parseFloat( body_tabla[i]["total"]);
                        primer_semana_lunes=body_tabla[i]["lunes_proximo"];
                        primer_semana_viernes=body_tabla[i]["viernes_proximo"];

                    }

                    var total_primer_semana =sumatotal;
                    union_de_dos_post(total_primer_semana,primer_semana_lunes,primer_semana_viernes);
                //alert("primer total: "+total_primer_semana);
                });
                var union_de_dos_post=function(total_primer_semana,primer_semana_lunes,primer_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 2,
                        iu:config.getUi()
                    };

                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;
                        var sumatotal2=0.0;
                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal2 = sumatotal2 +parseFloat( body_tabla[i]["total"]);
                            segunda_semana_lunes=body_tabla[i]["lunes_proximo"];
                            segunda_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }
                        //alert("reciviendo primer total"+total_primer_semana+"y enviando el segundo"+sumatotal2);
                        total_primer_semana=total_primer_semana;
                        total_segunda_semana =sumatotal2;
                        union_de_tres_post(total_primer_semana,total_segunda_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes);
                    });
                }

                var union_de_tres_post=function(total_primer_semana,total_segunda_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 3,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;


                        var Ttressemanas=0.0;
                        var sumatotal3=0.0;

                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal3 = sumatotal3 +parseFloat( body_tabla[i]["total"]);
                            tercer_semana_lunes=body_tabla[i]["lunes_proximo"];
                            tercer_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }
                        //alert("Recicibiendo el "+total_primer_semana+"Y el segundo"+total_segunda_semana+"y tercero"+sumatotal3);


                        total_primer_semana=parseFloat(total_primer_semana);
                        total_segunda_semana=parseFloat(total_segunda_semana);
                        total_tercera_semana =sumatotal3;
                        //alert("totalesxsemana"+total_primer_semana+"__"+total_segunda_semana+"___"+total_tercera_semana);
                        var  Ttressemanas =parseFloat(total_primer_semana) + parseFloat(total_segunda_semana)+ parseFloat(total_tercera_semana);
                        var header_tabla = {
                            semana_1    :" ",
                            semana_2    :" ",
                            semana_3    :"",
                            semana_4    :'Semana 1',
                            semana_5    :'Semana 2',
                            semana_6    :'Semana 3',
                            semana_7    :'Semana 4',

                            total    :'Total x 4 semanas'
                        };


                        var html_pronostico_semanas = '<table id="pronosticos_semanas", width="60%">';

                        html_pronostico_semanas +='<thead> <tr>';
                        for(var key in header_tabla){
                            var attrValue = header_tabla[key];
                            html_pronostico_semanas +='<td align="right">'+attrValue+'</td>';
                        }
                        html_pronostico_semanas +='</tr> </thead>';

                        html_pronostico_semanas +='<tr class="first">';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+primer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+primer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+segunda_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+segunda_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+tercer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+tercer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='</tr>';

                        html_footer +='<tr >';
                        html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                        html_footer +='<td align="center">'+""+'</td>';
                        html_footer +='<td align="right">'+""+'</td>';
                        html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(total_segunda_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(total_tercera_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(0.0).toFixed(2))+'</td>';
                        html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(Ttressemanas).toFixed(2))+'</td>';
                        html_footer +='</tr >';
                        //'+'<span id="total" style="display:none">'+body_tabla[i]["viernes"]+'</span>'+
                        html_pronostico_semanas +='<tfoot>';
                        html_pronostico_semanas += html_footer;
                        html_pronostico_semanas +='</tfoot>';
                        html_pronostico_semanas += '</table>';

                        $div_pronostico_cobranza_semanas.append(html_pronostico_semanas);
                        var height2 = $('#cuerpo').css('height');
                        var alto = parseInt(height2)-250;
                        var pix_alto=alto+'px';

                        $('#pronosticos_semanas').tableScroll({
                            height:parseInt(pix_alto)
                        });
                    });
                }
            }
            //finde tercer semna
            if(numero_semanas == 4){
                $div_pronostico_cobranza_semanas.children().remove();
                var html_footer="";
                var arreglo_parametros = {
                    opcion_seleccionada : 1 ,
                    numero_semanas : 1,
                    iu:config.getUi()
                };
                var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;


                    for(var i=0; i<body_tabla.length; i++){
                        sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal +parseFloat( body_tabla[i]["total"]);
                        primer_semana_lunes=body_tabla[i]["lunes_proximo"];
                        primer_semana_viernes=body_tabla[i]["viernes_proximo"];

                    }
                    total_primer_semana =sumatotal;
                    //alert("primer total"+total_primer_semana);
                    union_de_dos_post(total_primer_semana,primer_semana_lunes,primer_semana_viernes);
                });

                var union_de_dos_post=function(total_primer_semana,primer_semana_lunes,primer_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 2,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;

                        var total_segunda_semana=0.0;
                        var sumatotal2=0.0;

                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal2 = sumatotal2 +parseFloat( body_tabla[i]["total"]);
                            segunda_semana_lunes=body_tabla[i]["lunes_proximo"];
                            segunda_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }

                        total_segunda_semana =sumatotal2;
                        union_de_tres_post(total_primer_semana,total_segunda_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes);
                    //alert("recobiendo"+total_primer_semana+"enviando"+total_segunda_semana);
                    });
                }
                var union_de_tres_post=function(total_primer_semana,total_segunda_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 3,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;

                        var total_tercera_semana=0.0;
                        var sumatotal3=0.0;

                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal3 = sumatotal3 +parseFloat( body_tabla[i]["total"]);
                            tercer_semana_lunes=body_tabla[i]["lunes_proximo"];
                            tercer_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }
                        total_tercera_semana =sumatotal3
                        //alert("primera:"+total_primer_semana+"segunda"+total_segunda_semana+"y enviando"+total_tercera_semana);
                        union_de_cuatro_post(total_primer_semana,total_segunda_semana,total_tercera_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes,tercer_semana_lunes,tercer_semana_viernes );
                    });
                }
                var union_de_cuatro_post=function(total_primer_semana,total_segunda_semana,total_tercera_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes,tercer_semana_lunes,tercer_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 4,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;
                        var  total_cuarta_semana = 0.0;
                        var sumatotal4=0.0;
                        var  Tcuatrosemanas =0.0;

                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal4 = sumatotal4 +parseFloat( body_tabla[i]["total"]);
                            cuarta_semana_lunes=body_tabla[i]["lunes_proximo"];
                            cuarta_semana_viernes=body_tabla[i]["viernes_proximo"];
                        }

                        total_cuarta_semana =sumatotal4;

                        //alert("recibiendo primera: "+total_primer_semana+"segunda"+total_segunda_semana+"Tercera"+total_tercera_semana+"y generando"+total_cuarta_semana);

                        Tcuatrosemanas =parseFloat(total_primer_semana) + parseFloat(total_segunda_semana)+ parseFloat(total_tercera_semana) + parseFloat(total_cuarta_semana);
                        var header_tabla = {
                            semana_1    :" ",
                            semana_2    :" ",
                            semana_3    :"",
                            semana_4    :'Semana 1',
                            semana_5    :'Semana 2',
                            semana_6    :'Semana 3',
                            semana_7    :'Semana 4',

                            total    :'Total x 4 semanas'
                        };


                        var html_pronostico_semanas = '<table id="pronosticos_semanas", width="60%">';

                        html_pronostico_semanas +='<thead> <tr>';
                        for(var key in header_tabla){
                            var attrValue = header_tabla[key];
                            html_pronostico_semanas +='<td ALIGN="center">'+attrValue+'</td>';
                        }
                        html_pronostico_semanas +='</tr> </thead>';

                        html_pronostico_semanas +='<tr class="first">';
                        html_pronostico_semanas +='<td ALIGN="RIGHT">'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+primer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+primer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+segunda_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+segunda_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+tercer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+tercer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+cuarta_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+cuarta_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='</tr>';
                        //
                        html_footer +='<tr >';
                        html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                        html_footer +='<td align="center">'+""+'</td>';
                        html_footer +='<td align="right">'+""+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_segunda_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_tercera_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_cuarta_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(Tcuatrosemanas).toFixed(2))+'</td>';
                        html_footer +='</tr>';


                        html_pronostico_semanas +='<tfoot>';
                        html_pronostico_semanas += html_footer;
                        html_pronostico_semanas +='</tfoot>';
                        html_pronostico_semanas += '</table>';

                        $div_pronostico_cobranza_semanas.append(html_pronostico_semanas);
                        var height2 = $('#cuerpo').css('height');
                        var alto = parseInt(height2)-250;
                        var pix_alto=alto+'px';

                        $('#pronosticos_semanas').tableScroll({
                            height:parseInt(pix_alto)
                        });
                    });
                }
            }





        }






        if(opcion_seleccionada==2 && numero_semanas != 0){

            $div_pronostico_cobranza_semana.children().remove();
            $div_fechas.children().remove();
            var arreglo_parametros = {
                opcion_seleccionada : $select_cobranza.val() ,
                numero_semanas : $select_semanas.val(),
                iu:config.getUi()
            };
            var html_footer="";
            var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
            $.post(restful_json_service,arreglo_parametros,function(entry){
                var body_tabla = entry;
                var header_tabla = {
                    cliente  :"",
                    factura  :"",
                    dia_1    :'Lunes',
                    dia_2    :'Martes',
                    dia_3    :'Miercoles',
                    dia_4    :'Jueves',
                    dia_5    :'Viernes',
                    total    :'Total x semana'
                };

                var sumalunes = 0;
                var sumamartes = 0;
                var sumamiercoles = 0;
                var sumajueves = 0;
                var sumaviernes = 0;
                var sumatotal = 0;

                var html_pronostico_semanas="";

                var html_fechas = '<table id="fechas" width="60%">';
                var salto=0;
                for(var i=0; i<body_tabla.length; i++){
                    if(salto == 0){
                        html_fechas += '<tr align="right" >';
                        html_fechas += '<td>Del:&nbsp;&nbsp; &nbsp;</td>';
                        html_fechas += '<td align="left" width="100px">'+body_tabla[i]["lunes_proximo"]+'</td>';
                        html_fechas += '<td>Al:&nbsp;&nbsp; &nbsp;</td>';
                        html_fechas += '<td align="left" width="100px">'+ body_tabla[i]["viernes_proximo"]+'</td>';
                        html_fechas += '<td width="500px">&nbsp;&nbsp; &nbsp;</td>';

                        html_fechas += '</tr>';
                        salto=1;
                    }
                }

                html_fechas += '</table">';

                $div_fechas.append(html_fechas);


                var html_pronostico_semanas = '<table id="pronosticos_semana", width="60%">';
                //Contruiyendo el encabezado por iteraci
                html_pronostico_semanas +='<thead> <tr>';
                for(var key in header_tabla){
                    var attrValue = header_tabla[key];
                    html_pronostico_semanas +='<td align="right">'+attrValue+'</td>';
                }
                html_pronostico_semanas +='</tr> </thead>';

                for(var i=0; i<body_tabla.length; i++){
                    sumalunes = sumalunes + parseFloat(body_tabla[i]["lunes"]);
                    sumamartes = sumamartes + parseFloat(body_tabla[i]["martes"]);
                    sumamiercoles = sumamiercoles + parseFloat( body_tabla[i]["miercoles"]);
                    sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                    sumaviernes = sumaviernes + parseFloat(body_tabla[i]["viernes"]);
                    sumatotal = sumatotal + parseFloat( body_tabla[i]["total"]);

                }


                html_footer +='<tr>';
                html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                html_footer +='<td align="center">'+""+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumalunes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumamartes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumamiercoles).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumajueves).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumaviernes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumatotal).toFixed(2))+'</td>';
                html_footer +='</tr >';

                html_pronostico_semanas +='<tfoot>';
                html_pronostico_semanas += html_footer;
                html_pronostico_semanas +='</tfoot>';

                html_pronostico_semanas += '</table>';

                $div_pronostico_cobranza_semana.append(html_pronostico_semanas);
                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-250;
                var pix_alto=alto+'px';


                $('#pronosticos_semana').tableScroll({
                    height:parseInt(pix_alto)
                });
            });
        //fIN DELJON.
        }


        if(opcion_seleccionada==3 && numero_semanas != 0){
            //$div_pronostico_cobranza.show();
            //$div_pronostico_cobranza_semana.hide();
            //$div_pronostico_cobranza_semanas.hide();

            $div_pronostico_cobranza.children().remove();
            $div_fechas.children().remove();
            var arreglo_parametros = {
                opcion_seleccionada : $select_cobranza.val() ,
                numero_semanas : $select_semanas.val(),
                iu:config.getUi()
            };
            var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
            $.post(restful_json_service,arreglo_parametros,function(entry){

                var body_tabla = entry;

                var header_tabla = {
                    cliente  :'Cliente',
                    factura  :'Factura',
                    dia_1    :'Lunes',
                    dia_2    :'Martes',
                    dia_3    :'Miercoles',
                    dia_4    :'Jueves',
                    dia_5    :'Viernes',
                    total    :'Total'
                };

                //Contruir encabezado por iteracion
                var tmp = 0.0;
                var sumalunes = 0.0;
                var sumamartes = 0.0;
                var sumamiercoles = 0.0;
                var sumajueves = 0.0;
                var sumaviernes = 0.0;
                var sumatotal = 0.0;




                var total_c_lunes = 0;
                var total_c_martes = 0;
                var total_c_miercoles = 0;
                var total_c_jueves = 0;
                var total_c_viernes = 0;
                var total_c_total = 0;
                var html_fechas = '<table id="fechas" width="60%">';
                var salto=0;
                for(var i=0; i<body_tabla.length; i++){
                    if(salto == 0){
                        html_fechas += '<tr align="right" >';
                        html_fechas += '<td>Del:&nbsp;&nbsp; &nbsp;</td>';
                        html_fechas += '<td align="left" width="100px">'+body_tabla[i]["lunes_proximo"]+'</td>';
                        html_fechas += '<td>Al:&nbsp;&nbsp; &nbsp;</td>';
                        html_fechas += '<td align="left" width="100px">'+ body_tabla[i]["viernes_proximo"]+'</td>';
                        html_fechas += '<td width="500px">&nbsp;&nbsp; &nbsp;</td>';

                        html_fechas += '</tr>';
                        salto=1;
                    }
                }

                html_fechas += '</table">';

                $div_fechas.append(html_fechas);

                var html_pronostico = '<table id="pronosticos" width="60%">';

                html_pronostico +='<thead> <tr>';
                for(var key in header_tabla){
                    var attrValue = header_tabla[key];
                    html_pronostico +='<td>'+attrValue+'</td>';
                }
                html_pronostico +='</tr> </thead>';

                var cliente= body_tabla[0]["cliente"];
                html_pronostico +='<tr>';
                html_pronostico +='<td colspan ="8" >'+cliente+'</td>';
                html_pronostico +='<tr>';
                for(var i=0; i<body_tabla.length; i++){

                    if(body_tabla[i]["cliente"]==cliente){
                        html_pronostico +='<tr>';
                        html_pronostico +='<td  >'+""+'</td>';
                        html_pronostico +='<td widht="50px" align="center">'+body_tabla[i]["factura"]+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["lunes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["martes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["miercoles"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["jueves"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["viernes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["total"]).toFixed(2))+'</td>';
                        html_pronostico +='</tr>';

                        sumalunes = sumalunes + parseFloat(body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles + parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal + parseFloat( body_tabla[i]["total"]);


                        total_c_lunes =total_c_lunes +parseFloat(body_tabla[i]["lunes"]);
                        total_c_martes = total_c_martes + parseFloat(body_tabla[i]["martes"]);
                        total_c_miercoles = total_c_miercoles + parseFloat( body_tabla[i]["miercoles"]);
                        total_c_jueves = total_c_jueves + parseFloat(body_tabla[i]["jueves"]);
                        total_c_viernes = total_c_viernes + parseFloat(body_tabla[i]["viernes"]);
                        total_c_total = total_c_total + parseFloat(body_tabla[i]["total"]);
                    }else{

                        html_pronostico +='<tr>';
                        html_pronostico +='<td align="right">'+"Total x cliente"+'</td>';
                        html_pronostico +='<td widht="50px" align="center">'+""+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_lunes).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_martes).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_miercoles).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_jueves).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_viernes).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_total).toFixed(2))+'</td>';
                        html_pronostico +='</tr>';

                        html_pronostico +='<tr>';
                        html_pronostico +='<td colspan ="8" >'+""+'</td>';
                        html_pronostico +='</tr>';

                        total_c_lunes= 0;
                        total_c_martes= 0;
                        total_c_miercoles= 0;
                        total_c_jueves= 0;
                        total_c_viernes= 0;
                        total_c_total= 0;


                        cliente = body_tabla[i]["cliente"];
                        html_pronostico +='<tr>';
                        html_pronostico +='<td colspan ="8" >'+cliente+'</td>';
                        html_pronostico +='<tr>';

                        html_pronostico +='<tr>';
                        html_pronostico +='<td  >'+""+'</td>';
                        html_pronostico +='<td widht="50px" align="center">'+body_tabla[i]["factura"]+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["lunes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["martes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["miercoles"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["jueves"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["viernes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["total"]).toFixed(2))+'</td>';
                        html_pronostico +='</tr>';

                        total_c_lunes =total_c_lunes +parseFloat(body_tabla[i]["lunes"]);
                        total_c_martes = total_c_martes + parseFloat(body_tabla[i]["martes"]);
                        total_c_miercoles = total_c_miercoles + parseFloat( body_tabla[i]["miercoles"]);
                        total_c_jueves = total_c_jueves + parseFloat(body_tabla[i]["jueves"]);
                        total_c_viernes = total_c_viernes + parseFloat(body_tabla[i]["viernes"]);
                        total_c_total = total_c_total + parseFloat(body_tabla[i]["total"]);

                        sumalunes = sumalunes + parseFloat(body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles + parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal + parseFloat( body_tabla[i]["total"]);




                    }
                }
                html_pronostico +='<tr>';
                html_pronostico +='<td  >'+"Total x cliente"+'</td>';
                html_pronostico +='<td widht="50px" align="center">'+""+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_lunes).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_martes).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_miercoles).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_jueves).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_viernes).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_total).toFixed(2))+'</td>';
                html_pronostico +='</tr>';

                html_pronostico +='<tr>';
                html_pronostico +='<td colspan ="8" >'+""+'</td>';

                html_pronostico +='</tr>';



                html_footer +='<tr>';
                html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                html_footer +='<td align="center">'+""+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumalunes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumamartes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumamiercoles).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumajueves).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumaviernes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumatotal).toFixed(2))+'</td>';
                html_footer +='</tr>';

                //'+'<span id="total" style="display:none">'+body_tabla[i]["viernes"]+'</span>'+

                html_pronostico +='<tfoot>';
                html_pronostico += html_footer;
                html_pronostico +='</tfoot>';

                html_pronostico += '</table>';

                $div_pronostico_cobranza.append(html_pronostico);
                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-250;
                var pix_alto=alto+'px';


                $('#pronosticos').tableScroll({
                    height:parseInt(pix_alto)
                });
            });
        //fIN DEL JSON.
        }
    });





































































    $select_semanas.change(function(event){
        event.preventDefault();
        var opcion_seleccionada = $select_cobranza.val();
        var numero_semanas = $select_semanas.val();
        var sumalunes = 0.0;
        var sumamartes = 0.0;
        var sumamiercoles = 0.0;
        var sumajueves = 0.0;
        var sumaviernes = 0.0;
        var sumatotal = 0.0;
        var total_primer_semana=0.0;

        var primer_semana_lunes=0.0;
        var primer_semana_viernes=0.0;
        var segunda_semana_viernes=0.0;
        var segunda_semana_lunes=0.0;

        var tercer_semana_lunes=0.0;
        var tercer_semana_viernes=0.0;
        var cuarta_semana_lunes=0.0;
        var cuarta_semana_viernes=0.0;


        var html_pronostico_semanas ="";
        var html_footer=""

        if(opcion_seleccionada==1 && numero_semanas != 0){

            $div_pronostico_cobranza_semanas.children().remove();
            var html_footer="";

            if(numero_semanas == 1){


                var arreglo_parametros = {
                    opcion_seleccionada : 1 ,
                    numero_semanas : 1,
                    iu:config.getUi()
                };

                var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;
                    for(var i=0; i<body_tabla.length; i++){
                        sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal +parseFloat( body_tabla[i]["total"]);
                        primer_semana_lunes=body_tabla[i]["lunes_proximo"];
                        primer_semana_viernes=body_tabla[i]["viernes_proximo"];


                    }

                    total_primer_semana =sumatotal;

                    var header_tabla = {
                        semana_1    :" ",
                        semana_2    :" ",
                        semana_3    :"",
                        semana_4    :'Semana 1',
                        semana_5    :'Semana 2',
                        semana_6    :'Semana 3',
                        semana_7    :'Semana 4',
                        total    :'Total x 4 semanas'
                    };

                    html_pronostico_semanas = '<table id="pronosticos_semanas", width="60%">';

                    html_pronostico_semanas +='<thead> <tr>';
                    for(var key in header_tabla){
                        var attrValue = header_tabla[key];
                        html_pronostico_semanas +='<td align="center">'+attrValue+'</td>';
                    }
                    html_pronostico_semanas +='</tr> </thead>';

                    html_pronostico_semanas +='<tr class="first">';
                    html_pronostico_semanas +='<td ALIGN="RIGHT">'+""+'</td>';
                    html_pronostico_semanas +='<td >'+""+'</td>';
                    html_pronostico_semanas +='<td >'+""+'</td>';
                    html_pronostico_semanas +='<td align="center">'+primer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+primer_semana_viernes+'</td>';
                    html_pronostico_semanas +='<td align="center">'+""+'</td>';
                    html_pronostico_semanas +='<td align="center">'+""+'</td>';
                    html_pronostico_semanas +='<td align="center">'+""+'</td>';
                    html_pronostico_semanas +='<td align="center">'+""+'</td>';
                    html_pronostico_semanas +='</tr>';

                    html_footer +='<tr >';
                    html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                    html_footer +='<td align="center">'+""+'</td>';
                    html_footer +='<td align="right">'+""+'</td>';
                    html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';
                    html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                    html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                    html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                    html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';
                    html_footer +='</tr >';

                    html_pronostico_semanas +='<tfoot>';
                    html_pronostico_semanas += html_footer;
                    html_pronostico_semanas +='</tfoot>';

                    html_pronostico_semanas += '</table>';

                    $div_pronostico_cobranza_semanas.append(html_pronostico_semanas);
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-250;
                    var pix_alto=alto+'px';

                    $('#pronosticos_semanas').tableScroll({
                        height:parseInt(pix_alto)
                    });

                });
            }






            if(numero_semanas == 2){
                //si es semana 2
                var sumalunes= 0;
                var sumamartes = 0;
                var sumamiercoles = 0;
                var sumajueves = 0;
                var sumaviernes = 0;
                var sumatotal = 0;
                var sumatotal2 = 0;
                var sumatotal3 = 0;
                var sumatotal4 = 0;

                var segunda_semana_lunes="";
                var segunda_semana_viernes= "";

                var html_footer="";
                var arreglo_parametros = {
                    opcion_seleccionada : 1 ,
                    numero_semanas : 1,
                    iu:config.getUi()
                };
                var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;
                    for(var i=0; i<body_tabla.length; i++){
                        sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal +parseFloat( body_tabla[i]["total"]);
                        primer_semana_lunes=body_tabla[i]["lunes_proximo"];
                        primer_semana_viernes=body_tabla[i]["viernes_proximo"];


                    }

                    var total_primer_semana =sumatotal;

                    union_de_dos_post(total_primer_semana,primer_semana_lunes,primer_semana_viernes);
                });



                var union_de_dos_post=function(total_primer_semana,primer_semana_lunes,primer_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1,
                        numero_semanas : 2,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;
                        var total_segunda_semana=0.0;
                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal2 = sumatotal2 +parseFloat( body_tabla[i]["total"]);
                            segunda_semana_lunes=body_tabla[i]["lunes_proximo"];
                            segunda_semana_viernes=body_tabla[i]["viernes_proximo"];

                        // alert(body_tabla[i]["lunes_proximo"]);
                        }
                        total_primer_semana=parseFloat(total_primer_semana);
                        total_segunda_semana=parseFloat(sumatotal2);
                        var  Tdossemanas =parseFloat(total_primer_semana) + parseFloat(total_segunda_semana);
                        var header_tabla = {
                            semana_1    :" ",
                            semana_2    :" ",
                            semana_3    :"",
                            semana_4    :'Semana 1',
                            semana_5    :'Semana 2',
                            semana_6    :'Semana 3',
                            semana_7    :'Semana 4',

                            total    :'Total x 4 semanas'
                        };
                        var html_pronostico_semanas = '<table id="pronosticos_semanas", width="60%">';

                        html_pronostico_semanas +='<thead> <tr>';
                        for(var key in header_tabla){
                            var attrValue = header_tabla[key];
                            html_pronostico_semanas +='<td align="center">'+attrValue+'</td>';
                        }
                        html_pronostico_semanas +='</tr> </thead>';

                        html_pronostico_semanas +='<tr class="first">';
                        html_pronostico_semanas +='<td ALIGN="RIGHT">'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+primer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+primer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+segunda_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+segunda_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='</tr>';

                        html_footer +='<tr class="first">';
                        html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                        html_footer +='<td align="center">'+""+'</td>';
                        html_footer +='<td align="right">'+""+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_segunda_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(0.0.toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(Tdossemanas).toFixed(2))+'</td>';
                        html_footer +='</tr>';
                        //'+'<span id="total" style="display:none">'+body_tabla[i]["viernes"]+'</span>'+
                        html_pronostico_semanas +='<tfoot>';
                        html_pronostico_semanas += html_footer;
                        html_pronostico_semanas +='</tfoot>';
                        html_pronostico_semanas += '</table>';

                        $div_pronostico_cobranza_semanas.append(html_pronostico_semanas);
                        var height2 = $('#cuerpo').css('height');
                        var alto = parseInt(height2)-250;
                        var pix_alto=alto+'px';

                        $('#pronosticos_semanas').tableScroll({
                            height:parseInt(pix_alto)
                        });
                    });
                }
            }
            //finde si es semana2

            if(numero_semanas == 3){
                var html_footer="";

                var arreglo_parametros = {
                    opcion_seleccionada : 1 ,
                    numero_semanas : 1,
                    iu:config.getUi()
                };
                var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;
                    var sumatotal=0.0;
                    for(var i=0; i<body_tabla.length; i++){
                        sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal +parseFloat( body_tabla[i]["total"]);
                        primer_semana_lunes=body_tabla[i]["lunes_proximo"];
                        primer_semana_viernes=body_tabla[i]["viernes_proximo"];

                    }
                    var total_primer_semana =sumatotal;
                    union_de_dos_post(total_primer_semana,primer_semana_lunes,primer_semana_viernes);
                });
                var union_de_dos_post=function(total_primer_semana,primer_semana_lunes,primer_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 2,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;
                        var total_segunda_semana=0.0;
                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal2 = sumatotal +parseFloat( body_tabla[i]["total"]);
                            segunda_semana_lunes=body_tabla[i]["lunes_proximo"];
                            segunda_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }
                        total_primer_semana=total_primer_semana;
                        total_segunda_semana =sumatotal2;
                        union_de_tres_post(total_primer_semana,total_segunda_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes);
                    });
                }

                var union_de_tres_post=function(total_primer_semana,total_segunda_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 3,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;
                        var sumatotal3=0.0;
                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal3 = sumatotal3 +parseFloat( body_tabla[i]["total"]);
                            tercer_semana_lunes=body_tabla[i]["lunes_proximo"];
                            tercer_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }

                        total_primer_semana=total_primer_semana;
                        total_segunda_semana=total_segunda_semana;
                        var total_tercera_semana =sumatotal3;

                        total_primer_semana=parseFloat(total_primer_semana);
                        total_segunda_semana=parseFloat(sumatotal2);
                        var  Ttressemanas =parseFloat(total_primer_semana) + parseFloat(total_segunda_semana)+ parseFloat(total_tercera_semana);
                        var header_tabla = {
                            semana_1    :" ",
                            semana_2    :" ",
                            semana_3    :"",
                            semana_4    :'Semana 1',
                            semana_5    :'Semana 2',
                            semana_6    :'Semana 3',
                            semana_7    :'Semana 4',

                            total    :'Total x 4 semanas'
                        };


                        var html_pronostico_semanas = '<table id="pronosticos_semanas", width="60%">';

                        html_pronostico_semanas +='<thead> <tr>';
                        for(var key in header_tabla){
                            var attrValue = header_tabla[key];
                            html_pronostico_semanas +='<td align="center">'+attrValue+'</td>';
                        }
                        html_pronostico_semanas +='</tr> </thead>';


                        html_pronostico_semanas +='<tr >';
                        html_pronostico_semanas +='<td ALIGN="RIGHT">'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+primer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+primer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+segunda_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+segunda_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+tercer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+tercer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='</tr>';

                        html_footer +='<tr class="first">';
                        html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                        html_footer +='<td align="center">'+""+'</td>';
                        html_footer +='<td align="center">'+""+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_segunda_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_tercera_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(0.0).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(Ttressemanas).toFixed(2))+'</td>';
                        html_footer +='</tr >';
                        //'+'<span id="total" style="display:none">'+body_tabla[i]["viernes"]+'</span>'+
                        html_pronostico_semanas +='<tfoot>';
                        html_pronostico_semanas += html_footer;
                        html_pronostico_semanas +='</tfoot>';
                        html_pronostico_semanas += '</table>';

                        $div_pronostico_cobranza_semanas.append(html_pronostico_semanas);
                        var height2 = $('#cuerpo').css('height');
                        var alto = parseInt(height2)-250;
                        var pix_alto=alto+'px';

                        $('#pronosticos_semanas').tableScroll({
                            height:parseInt(pix_alto)
                        });
                    });
                }
            }
            //finde tercer semna
            if(numero_semanas == 4){
                var html_footer="";
                var total_primer_semana=0.0;

                var arreglo_parametros = {
                    opcion_seleccionada : 1 ,
                    numero_semanas : 1,
                    iu:config.getUi()
                };
                var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                $.post(restful_json_service,arreglo_parametros,function(entry){
                    var body_tabla = entry;

                    for(var i=0; i<body_tabla.length; i++){
                        sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal +parseFloat( body_tabla[i]["total"]);
                        primer_semana_lunes=body_tabla[i]["lunes_proximo"];
                        primer_semana_viernes=body_tabla[i]["viernes_proximo"];

                    }
                    total_primer_semana =sumatotal;
                    union_de_dos_post(total_primer_semana,primer_semana_lunes,primer_semana_viernes);
                });

                var union_de_dos_post=function(total_primer_semana,primer_semana_lunes,primer_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 2,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;
                        var sumatotal2=0.0;
                        var total_segunda_semana=0.0;
                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal2 = sumatotal2 +parseFloat( body_tabla[i]["total"]);
                            segunda_semana_lunes=body_tabla[i]["lunes_proximo"];
                            segunda_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }
                        total_primer_semana=total_primer_semana;
                        total_segunda_semana =sumatotal2;
                        union_de_tres_post(total_primer_semana,total_segunda_semana,total_primer_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes);
                    });
                }
                var union_de_tres_post=function(total_primer_semana,total_segunda_semana,total_primer_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 3,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;
                        var total_tercera_semana=0.0;
                        var sumatotal3=0.0;
                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal3 = sumatotal3 +parseFloat( body_tabla[i]["total"]);
                            tercer_semana_lunes=body_tabla[i]["lunes_proximo"];
                            tercer_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }
                        total_primer_semana=total_primer_semana;
                        total_segunda_semana =total_segunda_semana;
                        total_tercera_semana =sumatotal3

                        union_de_cuatro_post(total_primer_semana,total_segunda_semana,total_tercera_semana,primer_semana_lunes,primer_semana_viernes,segunda_semana_lunes,segunda_semana_viernes,tercer_semana_lunes,tercer_semana_viernes );
                    });
                }
                var union_de_cuatro_post=function(total_primer_semana,total_segunda_semana,total_tercera_semana){
                    var arreglo_parametros = {
                        opcion_seleccionada : 1 ,
                        numero_semanas : 4,
                        iu:config.getUi()
                    };
                    var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'

                    $.post(restful_json_service,arreglo_parametros,function(entry){
                        var body_tabla = entry;
                        var total_cuarta_semana=0.0;
                        var sumatotal4=0.0;
                        for(var i=0; i<body_tabla.length; i++){
                            sumalunes = sumalunes +parseFloat( body_tabla[i]["lunes"]);
                            sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                            sumamiercoles = sumamiercoles +parseFloat( body_tabla[i]["miercoles"]);
                            sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                            sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                            sumatotal4 = sumatotal4 +parseFloat( body_tabla[i]["total"]);
                            cuarta_semana_lunes=body_tabla[i]["lunes_proximo"];
                            cuarta_semana_viernes=body_tabla[i]["viernes_proximo"];

                        }

                        total_cuarta_semana =sumatotal4;
                        var  Tcuatrosemanas =parseFloat(total_primer_semana) + parseFloat(total_segunda_semana)+ parseFloat(total_tercera_semana) + parseFloat(total_cuarta_semana);
                        var header_tabla = {
                            semana_1    :" ",
                            semana_2    :" ",
                            semana_3    :"",
                            semana_4    :'Semana 1',
                            semana_5    :'Semana 2',
                            semana_6    :'Semana 3',
                            semana_7    :'Semana 4',

                            total    :'Total x 4 semanas'
                        };


                        var html_pronostico_semanas = '<table id="pronosticos_semanas", width="60%">';

                        html_pronostico_semanas +='<thead> <tr>';
                        for(var key in header_tabla){
                            var attrValue = header_tabla[key];
                            html_pronostico_semanas +='<td ALIGN="center">'+attrValue+'</td>';
                        }
                        html_pronostico_semanas +='</tr> </thead>';

                        html_pronostico_semanas +='<tr class="first">';
                        html_pronostico_semanas +='<td ALIGN="RIGHT">'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td >'+""+'</td>';
                        html_pronostico_semanas +='<td align="center">'+primer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+primer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+segunda_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+segunda_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+tercer_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+tercer_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+cuarta_semana_lunes+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Al&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+cuarta_semana_viernes+'</td>';
                        html_pronostico_semanas +='<td align="center">'+""+'</td>';
                        html_pronostico_semanas +='</tr>';

                        html_footer +='<tr class="first">';
                        html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                        html_footer +='<td align="center">'+""+'</td>';
                        html_footer +='<td align="center">'+""+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_primer_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_segunda_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_tercera_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center">'+$(this).agregar_comas(parseFloat(total_cuarta_semana).toFixed(2))+'</td>';
                        html_footer +='<td align="center"s>'+$(this).agregar_comas(parseFloat(Tcuatrosemanas).toFixed(2))+'</td>';
                        html_footer +='</tr>';
                        //'+'<span id="total" style="display:none">'+body_tabla[i]["viernes"]+'</span>'+
                        html_pronostico_semanas +='<tfoot>';
                        html_pronostico_semanas += html_footer;
                        html_pronostico_semanas +='</tfoot>';
                        html_pronostico_semanas += '</table>';

                        $div_pronostico_cobranza_semanas.append(html_pronostico_semanas);
                        var height2 = $('#cuerpo').css('height');
                        var alto = parseInt(height2)-250;
                        var pix_alto=alto+'px';

                        $('#pronosticos_semanas').tableScroll({
                            height:parseInt(pix_alto)
                        });
                    });
                }
            }
        }


        if(opcion_seleccionada==2 && numero_semanas != 0){
            $div_pronostico_cobranza_semana.children().remove();
            //$div_pronostico_cobranza_semanas.hide();
            //$div_pronostico_cobranza.hide();
            //$div_pronostico_cobranza_semana.show();
            var html_footer="";
            var arreglo_parametros = {
                opcion_seleccionada : $select_cobranza.val() ,
                numero_semanas : $select_semanas.val(),
                iu:config.getUi()
            };

            $div_fechas.children().remove();
            var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
            $.post(restful_json_service,arreglo_parametros,function(entry){
                var body_tabla = entry;
                var header_tabla = {
                    cliente  :"",
                    factura  :"",
                    dia_1    :'Lunes',
                    dia_2    :'Martes',
                    dia_3    :'Miercoles',
                    dia_4    :'Jueves',
                    dia_5    :'Viernes',
                    total    :'Total x semana'
                };

                var sumalunes = 0;
                var sumamartes = 0;
                var sumamiercoles = 0;
                var sumajueves = 0;
                var sumaviernes = 0;
                var sumatotal = 0;

                var html_footer ="";
                var html_pronostico_semana="";

                var html_fechas = '<table id="fechas" width="60%">';
                var salto=0;
                for(var i=0; i<body_tabla.length; i++){
                    if(salto == 0){
                        html_fechas += '<tr align="right" >';
                        html_fechas += '<td>Del:&nbsp;&nbsp; &nbsp;</td>';
                        html_fechas += '<td align="left" width="100px">'+body_tabla[i]["lunes_proximo"]+'</td>';
                        html_fechas += '<td>Al:&nbsp;&nbsp; &nbsp;</td>';
                        html_fechas += '<td align="left" width="100px">'+ body_tabla[i]["viernes_proximo"]+'</td>';
                        html_fechas += '<td width="500px">&nbsp;&nbsp; &nbsp;</td>';

                        html_fechas += '</tr>';
                        salto=1;
                    }
                }

                html_fechas += '</table">';

                $div_fechas.append(html_fechas);


                var html_pronostico_semana = '<table id="pronosticos_semana" width="60%">';

                html_pronostico_semana +='<thead> <tr>';
                //Contruiyendo el encabezado por iteracion
                for(var key in header_tabla){
                    var attrValue = header_tabla[key];
                    html_pronostico_semana +='<td align="right">'+attrValue+'</td>';
                }
                html_pronostico_semana +='</tr> </thead>';

                for(var i=0; i<body_tabla.length; i++){
                    sumalunes = sumalunes + parseFloat(body_tabla[i]["lunes"]);
                    sumamartes = sumamartes + parseFloat(body_tabla[i]["martes"]);
                    sumamiercoles = sumamiercoles + parseFloat( body_tabla[i]["miercoles"]);
                    sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                    sumaviernes = sumaviernes + parseFloat(body_tabla[i]["viernes"]);
                    sumatotal = sumatotal + parseFloat( body_tabla[i]["total"]);

                }

                html_pronostico_semana+='<tr ><td colspan="8">&nbsp;</td></tr>';
                html_footer +='<tr class="first">';
                html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';
                html_footer +='<td align="center">'+""+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumalunes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumamartes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumamiercoles).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumajueves).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumaviernes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumatotal).toFixed(2))+'</td>';
                html_footer+='</tr >';
                //'+'<span id="total" style="display:none">'+body_tabla[i]["viernes"]+'</span>'+
                html_pronostico_semana +='<tfoot>';
                html_pronostico_semana += html_footer;
                html_pronostico_semana +='</tfoot>';
                html_pronostico_semana += '</table>';

                $div_pronostico_cobranza_semana.append(html_pronostico_semana);
                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-250;
                var pix_alto=alto+'px';


                $('#pronosticos_semana').tableScroll({
                    height:parseInt(pix_alto)
                });
            });
        //fIN DELJON.
        }


        if(opcion_seleccionada==3 && numero_semanas != 0){
            $div_pronostico_cobranza.children().remove();
            $div_fechas.children().remove();
            var arreglo_parametros = {
                opcion_seleccionada : $select_cobranza.val() ,
                numero_semanas : $select_semanas.val(),
                iu:config.getUi()
            };
            var html_footer="";
            //$div_pronostico_cobranza.show();
            //
            //$div_pronostico_cobranza_semana.hide();
            //$div_pronostico_cobranza_semanas.hide();

            var header_tabla = {
                // cliente  :'Cliente',
                factura  :'Factura',
                dia_1    :'Lunes',
                dia_2    :'Martes',
                dia_3    :'Miercoles',
                dia_4    :'Jueves',
                dia_5    :'Viernes',
                total    :'Total'
            };
            var restful_json_service = config.getUrlForGetAndPost() + '/getPronosticoDeCobranza.json'
            $.post(restful_json_service,arreglo_parametros,function(entry){

                var body_tabla = entry;
                //Contruir encabezado por iteracion

                var sumalunes = 0.0;
                var sumamartes = 0.0;
                var sumamiercoles = 0.0;
                var sumajueves = 0.0;
                var sumaviernes = 0.0;
                var sumatotal = 0.0;


                var html_footer="";

                var total_c_lunes = 0;
                var total_c_martes = 0;
                var total_c_miercoles = 0;
                var total_c_jueves = 0;
                var total_c_viernes = 0;
                var total_c_total = 0;

                var html_fechas = '<table id="fechas" width="60%">';
                var salto=0;
                html_fechas += '<tr align="right" >';
                for(var i=0; i<body_tabla.length; i++){
                    if(salto == 0){

                        html_fechas += '<td>Del:&nbsp;&nbsp; &nbsp;</td>';
                        html_fechas += '<td align="left" width="100px">'+body_tabla[i]["lunes_proximo"]+'</td>';
                        html_fechas += '<td>Al:&nbsp;&nbsp; &nbsp;</td>';
                        html_fechas += '<td align="left" width="100px">'+ body_tabla[i]["viernes_proximo"]+'</td>';
                        html_fechas += '<td width="500px">&nbsp;&nbsp; &nbsp;</td>';
                        salto=1;
                    }
                }
                html_fechas += '</tr>';
                html_fechas += '</table">';

                $div_fechas.append(html_fechas);



                var html_pronostico = '<table id="pronosticos" width="60%">';

                html_pronostico +='<thead> <tr>';
                for(var key in header_tabla){
                    var attrValue = header_tabla[key];
                    html_pronostico +='<td align="right">'+attrValue+'</td>';
                }
                html_pronostico +='</tr> </thead>';

                var  cliente= body_tabla[0]["cliente"];
                html_pronostico +='<tr  >';
                html_pronostico +='<td  colspan="7">Cliente: '+cliente+'</td>';
                html_pronostico +='</tr >';
                for(var i=0; i<body_tabla.length; i++){

                    if(cliente== body_tabla[i]["cliente"]){
                        html_pronostico +='<tr>';

                        html_pronostico +='<td widht="50px" align="right">'+body_tabla[i]["factura"]+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["lunes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["martes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["miercoles"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["jueves"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["viernes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["total"]).toFixed(2))+'</td>';
                        html_pronostico +='</tr>';

                        sumalunes = sumalunes + parseFloat(body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles + parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal + parseFloat( body_tabla[i]["total"]);

                        total_c_lunes =total_c_lunes +parseFloat(body_tabla[i]["lunes"]);
                        total_c_martes = total_c_martes + parseFloat(body_tabla[i]["martes"]);
                        total_c_miercoles = total_c_miercoles + parseFloat( body_tabla[i]["miercoles"]);
                        total_c_jueves = total_c_jueves + parseFloat(body_tabla[i]["jueves"]);
                        total_c_viernes = total_c_viernes + parseFloat(body_tabla[i]["viernes"]);
                        total_c_total = total_c_total + parseFloat(body_tabla[i]["total"]);

                    }else{



                        html_pronostico +='<tr>';
                        html_pronostico +='<td align="left">'+"Total x cliente"+'</td>';

                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_lunes).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_martes).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_miercoles).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_jueves).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_viernes).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_total).toFixed(2))+'</td>';
                        html_pronostico +='</tr>';

                        html_pronostico +='<tr>';
                        html_pronostico +='<td colspan ="7" >'+""+'</td>';
                        html_pronostico +='</tr>';

                        total_c_lunes= 0;
                        total_c_martes= 0;
                        total_c_miercoles= 0;
                        total_c_jueves= 0;
                        total_c_viernes= 0;
                        total_c_total= 0;

                        html_pronostico +='<tr>';
                        html_pronostico +='<td colspan ="7" >Cliente: '+body_tabla[i]["cliente"]+'</td>';
                        html_pronostico +='</tr>';

                        html_pronostico +='<tr>';

                        html_pronostico +='<td widht="50px" align="right">'+body_tabla[i]["factura"]+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["lunes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["martes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["miercoles"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["jueves"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["viernes"]).toFixed(2))+'</td>';
                        html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(body_tabla[i]["total"]).toFixed(2))+'</td>';
                        html_pronostico +='</tr>';

                        total_c_lunes =total_c_lunes +parseFloat(body_tabla[i]["lunes"]);
                        total_c_martes = total_c_martes + parseFloat(body_tabla[i]["martes"]);
                        total_c_miercoles = total_c_miercoles + parseFloat( body_tabla[i]["miercoles"]);
                        total_c_jueves = total_c_jueves + parseFloat(body_tabla[i]["jueves"]);
                        total_c_viernes = total_c_viernes + parseFloat(body_tabla[i]["viernes"]);
                        total_c_total = total_c_total + parseFloat(body_tabla[i]["total"]);

                        sumalunes = sumalunes + parseFloat(body_tabla[i]["lunes"]);
                        sumamartes = sumamartes + parseFloat( body_tabla[i]["martes"]);
                        sumamiercoles = sumamiercoles + parseFloat( body_tabla[i]["miercoles"]);
                        sumajueves = sumajueves + parseFloat( body_tabla[i]["jueves"]);
                        sumaviernes = sumaviernes + parseFloat( body_tabla[i]["viernes"]);
                        sumatotal = sumatotal + parseFloat( body_tabla[i]["total"]);
                        cliente = body_tabla[i]["cliente"];
                    }
                }
                html_pronostico +='<tr>';
                html_pronostico +='<td  align="left">'+"Total x cliente"+'</td>';

                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_lunes).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_martes).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_miercoles).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_jueves).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_viernes).toFixed(2))+'</td>';
                html_pronostico +='<td widht="100px" align="right">'+$(this).agregar_comas(parseFloat(total_c_total).toFixed(2))+'</td>';
                html_pronostico +='</tr>';

                html_pronostico +='<tr>';
                html_pronostico +='<td colspan ="7" >'+""+'</td>';
                html_pronostico +='</tr>';

                html_footer +='<tr>';
                html_footer +='<td ALIGN="RIGHT">'+"TOTALES:"+'</td>';

                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumalunes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumamartes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumamiercoles).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumajueves).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumaviernes).toFixed(2))+'</td>';
                html_footer +='<td align="right">'+$(this).agregar_comas(parseFloat(sumatotal).toFixed(2))+'</td>';
                html_footer +='</tr>';

                html_pronostico +='<tfoot>';
                html_pronostico += html_footer;
                html_pronostico +='</tfoot>';
                html_pronostico += '</table>';

                $div_pronostico_cobranza.append(html_pronostico);
                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-350;
                var pix_alto=alto+'px';

                $('#pronosticos').tableScroll({
                    height:parseInt(pix_alto)
                });
            });
        //fIN DEL JSON.
        }
    });
});
