$(function() {
	String.prototype.toCharCode = function(){
		var str = this.split(''), len = str.length, work = new Array(len);
		for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
		}
		return work.join(',');
	};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
        
	var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/crmbigpicture";
        
	//Barra para las acciones
	$('#barra_acciones').hide();
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Resumen de Consultas');
	
	var $new_consulta = $('#consultas').find('input[name=buscar]');
	var $configurar_consultas = $('#consultas').find('input[name=configurar_consultas]');
	var $busqueda_agente =$('#consultas').find('select[name=busqueda_agente]');
	var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
	var $buscar_consulta =$('#consultas').find('input[name=buscar]');
	
	
        //esto se hace para reinicar los valores del select de agentes
        var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
        $arreglo2 = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
        
        $.post(input_json2,$arreglo2,function(data){
            //Alimentando los campos select_agente
            $busqueda_agente.children().remove();
            var agente_hmtl = '';
            if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
                agente_hmtl += '<option value="0" >[-- Selecionar Agente --]</option>';
            }

            $.each(data['Agentes'],function(entryIndex,agente){
                if(parseInt(agente['id'])==parseInt(data['Extra'][0]['id_agente'])){
                    agente_hmtl += '<option value="' + agente['id'] + '" selected="yes">' + agente['nombre_agente'] + '</option>';
                }else{
                    //si exis_rol_admin es mayor que cero, quiere decir que el usuario logueado es un administrador
                    if(parseInt(data['Extra'][0]['exis_rol_admin']) > 0){
                        agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_agente'] + '</option>';
                    }
                }
            });
            $busqueda_agente.append(agente_hmtl);
        });
        
        
        /*Programacion de los campos tipo fecha*/
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
	
	$tabs_li_funcionalidad = function(){
		$('#forma-registro-window').find('#submit').mouseover(function(){
			$('#forma-registro-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		});
		$('#forma-registro-window').find('#submit').mouseout(function(){
			$('#forma-registro-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-centrocostos-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		});
		$('#forma-registro-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-registro-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-registro-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-registro-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		});
		
		$('#forma-registro-window').find('#close').mouseover(function(){
			$('#forma-registro-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		});
		$('#forma-registro-window').find('#close').mouseout(function(){
			$('#forma-registro-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		});		
		
		$('#forma-registro-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-registro-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-registro-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-registro-window').find("ul.pestanas li").click(function() {
			$('#forma-registro-window').find(".contenidoPes").hide();
			$('#forma-registro-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-registro-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
        

        
        $buscar_consulta.click(function(event){
			var $content_results = $('.content_results');//Oportunidades
			
			var $tablaresultadosvisitas= $('#tablaresultadosvisitas');//visitas
			var $tablaresultadosllamadas= $('#tablaresultadosllamadas');//Llamadas
			var $tablaresultadoscasos= $('#tablaresultadoscasos');//Casos
			var $tablaresultadosoportunidades= $('#tablaresultadosoportunidades');//Oportunidades
			var $tablaresultadoscotizaciones= $('#tablaresultadoscotizaciones');//Cotizaciones
			
			var html_trs="";
			
            $tablaresultadosvisitas.children().remove();//visitas
            $tablaresultadosvisitas.text('');
            $tablaresultadosllamadas.children().remove();//Llamadas
            $tablaresultadosllamadas.text('');
            $tablaresultadoscasos.children().remove();//Casos
            $tablaresultadoscasos.text('');
            $tablaresultadosoportunidades.children().remove();//Oportunidades
            $tablaresultadosoportunidades.text('');
            $tablaresultadoscotizaciones.children().remove();//Cotizaciones
            $tablaresultadoscotizaciones.text('');
            
            $content_results.css({display : 'block'});
            
            var arreglo_parametros = { iu:$('#lienzo_recalculable').find('input[name=iu]').val(), agente:$busqueda_agente.val(),
            fecha_inicio:$fecha_inicial.val(), fecha_fin:$fecha_final.val()};
            var restful_json_service = controller + '/getResultadosConsulta.json';
            
            $.post(restful_json_service,arreglo_parametros,function(entry){
                if(entry['bigPicture'].length > 0){
                    trh_visitas = '';
                    tr_visitas = '';
                    if(entry['ConfigData'][0]['metas_visita'] == 'true'){
                        trh_visitas += '<th width="100">Meta</th>';
                        tr_visitas += '<td>'+entry['bigPicture'][0]['visita_meta']+'</td>';
                    }
                    if(entry['ConfigData'][0]['totales_visita'] == 'true'){
                        trh_visitas += '<th width="100">Total</th>';
                        tr_visitas += '<td>'+entry['bigPicture'][0]['visitas_totales']+'</td>';
                    }
                    if(entry['ConfigData'][0]['cumplido_visita'] == 'true'){
                        trh_visitas += '<th width="100">Cumplido</th>';
                        tr_visitas += '<td>'+entry['bigPicture'][0]['porcentaje_visitas']+'</td>';
                    }
                    if(entry['ConfigData'][0]['conexito_visita'] == 'true'){
                        trh_visitas += '<th width="100">Exitosas</th>';
                        tr_visitas += '<td>'+entry['bigPicture'][0]['visitas_con_exito']+'</td>';
                    }
                    if(entry['ConfigData'][0]['conoportunidad_visita'] == 'true'){
                        trh_visitas += '<th width="100">Con Oportunidad</th>';
                        tr_visitas += '<td>'+entry['bigPicture'][0]['oport_visitas']+'</td>';
                    }
                    if(entry['ConfigData'][0]['seguimiento_visita'] == 'true'){
                        trh_visitas += '<th width="100">Con Seguimiento</th>';
                        tr_visitas += '<td>'+entry['bigPicture'][0]['visitas_con_seguimiento']+'</td>';
                    }
                    if(entry['ConfigData'][0]['efectividad_visita'] == 'true'){
                        trh_visitas += '<th width="100">% Efectividad</th>';
                        tr_visitas += '<td>'+entry['bigPicture'][0]['efectividad_visita']+'</td>';
                    }
                    if(entry['ConfigData'][0]['gestion_visita'] == 'true'){
                        trh_visitas += '<th width="100">% Gestion</th>';
                        tr_visitas += '<td>'+entry['bigPicture'][0]['gestion_visitas']+'</td>';
                    }
                    if(entry['ConfigData'][0]['avance_visitas'] == 'true'){
                        trh_visitas += '<th width="100">% Avance</th>';
                        tr_visitas += '<td>'+entry['bigPicture'][0]['avance_visitas']+'</td>';
                    }
                    if(tr_visitas != ""){
                        var html_trs = 'Vistas<table id="resultadosvisitas"><thead><tr>'+trh_visitas+'</tr></thead><tbody><tr>'+tr_visitas+'</tr></tbody></table></br>';
                        $tablaresultadosvisitas.append(html_trs);
                    }
                    //Listo
                    
                    
                    trh_oportunidades = '';
                    tr_oportunidades = '';
                    if(entry['ConfigData'][0]['metas_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="100">Metas</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['metas_oport']+'</td>';
                    }
                    if(entry['ConfigData'][0]['montos_meta_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="100">Monto Meta</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['monto_metas_oport']+'</td>';
                    }
                    if(entry['ConfigData'][0]['total_metas_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="100">Total Metas</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['total_metas_oport']+'</td>';
                    }
                    if(entry['ConfigData'][0]['total_montos_oport'] == 'true'){
                        trh_oportunidades += '<th width="100">Total Montos</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['total_montos_oport']+'</td>';
                    }
                    
                    if(entry['ConfigData'][0]['metas_cumplidas_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="100">Metas Cumplidas</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['total_montos_oport']+'</td>';
                    }
                    if(entry['ConfigData'][0]['metas_cumplidas_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="100">Monto Cumplido</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['montos_cumplidos']+'</td>';
                    }
                    if(entry['ConfigData'][0]['inicial_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="100">Inicial</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['oport_inicial']+'</td>';
                    }
                    if(entry['ConfigData'][0]['seguimiento_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="120">Con Seguimiento</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['oport_seguimiento']+'</td>';
                    }
                    if(entry['ConfigData'][0]['conoportunidad_visita'] == 'true'){
                        trh_oportunidades += '<th width="120">Con Visitas</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['oport_visitas']+'</td>';
                    }
                    if(entry['ConfigData'][0]['cotizacion_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="120">Con Cotizaci&oacute;n</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['oport_cotizacion']+'</td>';
                    }
                    if(entry['ConfigData'][0]['negociacion_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="120">Con Negociaci&oacute;n</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['oport_negociacion']+'</td>';
                    }
                    if(entry['ConfigData'][0]['seguimiento_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="120">Con Cierre</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['oport_cierre']+'</td>';
                    }
                    if(entry['ConfigData'][0]['ganados_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="100">Ganadas</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['oport_ganados']+'</td>';
                    }
                    if(entry['ConfigData'][0]['perdidos_oportunidades'] == 'true'){
                        trh_oportunidades += '<th width="100">Perdidas</th>';
                        tr_oportunidades += '<td>'+entry['bigPicture'][0]['oport_perdidos']+'</td>';//falta en ConfigData
                    }
                    if(tr_oportunidades != ""){
                        var html_trs = 'Oportunidades<table id="resultadosoportunidades"><thead><tr>'+trh_oportunidades+'</tr></thead><tbody><tr>'+tr_oportunidades+'</tr></tbody></table></br></br>';
                        $tablaresultadosoportunidades.append(html_trs);
                        
                    }
                    
                    
                    
                    trh_casos = '';
                    tr_casos = '';
                    if(entry['ConfigData'][0]['facturacion_casos'] == 'true'){
                        trh_casos += '<th width="100">% Facturacion</th>';
                        tr_casos += '<td>'+entry['bigPicture'][0]['casos_facturacion']+'</td>';
                    }
                    if(entry['ConfigData'][0]['producto_casos'] == 'true'){
                        trh_casos += '<th width="100">% Producto</th>';
                        tr_casos += '<td>'+entry['bigPicture'][0]['casos_producto']+'</td>';
                    }
                    if(entry['ConfigData'][0]['garantia_casos'] == 'true'){
                        trh_casos += '<th width="100">% Garantia</th>';
                        tr_casos += '<td>'+entry['bigPicture'][0]['casos_garantia']+'</td>';
                    }
                    if(entry['ConfigData'][0]['distribucion_casos'] == 'true'){
                        trh_casos += '<th width="100">% Distribucion</th>';
                        tr_casos += '<td>'+entry['bigPicture'][0]['casos_distribucion']+'</td>';
                    }
                    if(entry['ConfigData'][0]['danos_casos'] == 'true'){
                        trh_casos += '<th width="100">% Da&ntilde;os</th>';
                        tr_casos += '<td>'+entry['bigPicture'][0]['casos_danos']+'</td>';
                    }
                    if(entry['ConfigData'][0]['devoluciones_casos'] == 'true'){
                        trh_casos += '<th width="100">% Devoluciones</th>';
                        tr_casos += '<td>'+entry['bigPicture'][0]['casos_devoluciones']+'</td>';
                    }
                    if(entry['ConfigData'][0]['cobranza_casos'] == 'true'){
                        trh_casos += '<th width="100">% Cobranza</th>';
                        tr_casos += '<td>'+entry['bigPicture'][0]['casos_cobranza']+'</td>';
                    }
                    if(entry['ConfigData'][0]['varios_casos'] == 'true'){
                        trh_casos += '<th width="100">% Varios</th>';
                        tr_casos += '<td>'+entry['bigPicture'][0]['casos_varios']+'</td>';
                    }
                    if(tr_casos != ""){
                        var html_trs = 'Casos<table id="resultadoscasos"><thead><tr>'+trh_casos+'</tr></thead><tbody><tr>'+tr_casos+'</tr></tbody></table></br>';
                        $tablaresultadoscasos.append(html_trs);
                    }
                    //Listo
                    
                    
                    
                    
                    
                    trh_llamadas = '';
                    tr_llamadas = '';
                    if(entry['ConfigData'][0]['metas_llamadas'] == 'true'){
                        trh_llamadas += '<th width="100">Metas</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['cantidad_llamadas']+'</td>';
                    }//
                    if(entry['ConfigData'][0]['total_llamadas'] == 'true'){
                        trh_llamadas += '<th width="100">Total</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['llamadas_totales']+'</td>';
                    }
                    if(entry['ConfigData'][0]['cumplido_llamadas'] == 'true'){
                        trh_llamadas += '<th width="100">Total</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['porcentaje_llamadas']+'</td>';
                    }
                    if(entry['ConfigData'][0]['entrantes_llamadas'] == 'true'){
                        trh_llamadas += '<th width="100">% Entrantes</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['llamadas_entrantes']+'</td>';
                    }
                    if(entry['ConfigData'][0]['salientes_llamadas'] == 'true'){
                        trh_llamadas += '<th width="100">% Salientes</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['llamadas_salientes']+'</td>';
                    }
                    if(entry['ConfigData'][0]['planeadas_llamadas'] == 'true'){
                        trh_llamadas += '<th width="100">% Planeadas</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['llamadas_planeadas']+'</td>';
                    }
                    if(entry['ConfigData'][0]['con_exito_llamadas'] == 'true'){
                        trh_llamadas += '<th width="120">% Con Exito</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['llamadas_con_exito']+'</td>';
                    }
                    if(entry['ConfigData'][0]['con_cita_llamadas'] == 'true'){
                        trh_llamadas += '<th width="120">% Con cita</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['llamadas_con_cita']+'</td>';
                    }
                    if(entry['ConfigData'][0]['conseguimiento_llamadas'] == 'true'){
                        trh_llamadas += '<th width="120">% Con Seguimiento</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['llamadas_con_seguimiento']+'</td>';
                    }
                    if(entry['ConfigData'][0]['efectividad_llamadas'] == 'true'){
                        trh_llamadas += '<th width="120">% Efectividad</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['efectividad_llamadas']+'</td>';
                    }
                    if(entry['ConfigData'][0]['gestion_llamadas'] == 'true'){
                        trh_llamadas += '<th width="100">% Gesti&oacute;n</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['gestion_llamadas']+'</td>';
                    }
                    if(entry['ConfigData'][0]['avance_llamadas'] == 'true'){
                        trh_llamadas += '<th width="100">% Avance</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['avance_llamadas']+'</td>';
                    }
                    if(entry['ConfigData'][0]['planeacion_llamadas'] == 'true'){
                        trh_llamadas += '<th width="100">% Planeaci&oacute;n</th>';
                        tr_llamadas += '<td>'+entry['bigPicture'][0]['planeacion']+'</td>';
                    }
                    if(tr_llamadas != ""){
                        var html_trs = 'Llamadas<table id="resultadosllamadas"><thead><tr>'+trh_llamadas+'</tr></thead><tbody><tr>'+tr_llamadas+'</tr></tbody></table></br>';
                        $tablaresultadosllamadas.append(html_trs);
                    }
                    
                    
                    
                    
                    //COTIZACIONES
                    var trh_cotizacion = '';
                    var tr_cot_prospecto = '';
                    var tr_cot_cliente = '';
                    
					trh_cotizacion += '<th width="100">Tipo</th>';
					trh_cotizacion += '<th width="100">Cantidad&nbsp;Meta</th>';
					trh_cotizacion += '<th width="100">Monto&nbsp;Meta</th>';
					trh_cotizacion += '<th width="110">Cantidad&nbsp;Cumplida</th>';
					trh_cotizacion += '<th width="100">Monto&nbsp;Cumplido</th>';
					trh_cotizacion += '<th width="100">%&nbsp;Cantidad</th>';
					trh_cotizacion += '<th width="100">%&nbsp;Monto</th>';
					
					tr_cot_prospecto += '<td>Prospecto</td>';
					tr_cot_prospecto += '<td align="right">'+entry['bigPicture'][0]['cant_met_cot_pros']+'</td>';
					tr_cot_prospecto += '<td align="right">'+entry['bigPicture'][0]['mont_met_cot_pros']+'</td>';
					tr_cot_prospecto += '<td align="right">'+entry['bigPicture'][0]['cant_cot_pros']+'</td>';
					tr_cot_prospecto += '<td align="right">'+entry['bigPicture'][0]['mont_cot_pros']+'</td>';
					tr_cot_prospecto += '<td align="right">'+entry['bigPicture'][0]['por_cant_cot_pros']+'</td>';
					tr_cot_prospecto += '<td align="right">'+entry['bigPicture'][0]['por_mont_cot_pros']+'</td>';
					
					tr_cot_cliente += '<td>Cliente</td>';
					tr_cot_cliente += '<td align="right">'+entry['bigPicture'][0]['cant_met_cot_cli']+'</td>';
					tr_cot_cliente += '<td align="right">'+entry['bigPicture'][0]['mont_met_cot_cli']+'</td>';
					tr_cot_cliente += '<td align="right">'+entry['bigPicture'][0]['cant_cot_cli']+'</td>';
					tr_cot_cliente += '<td align="right">'+entry['bigPicture'][0]['mont_cot_cli']+'</td>';
					tr_cot_cliente += '<td align="right">'+entry['bigPicture'][0]['por_cant_cot_cli']+'</td>';
					tr_cot_cliente += '<td align="right">'+entry['bigPicture'][0]['por_mont_cot_cli']+'</td>';
					
					var html_tr = 'Cotizaciones<table id="resultadoscotizaciones"><thead><tr>'+trh_cotizacion+'</tr></thead><tbody><tr>'+tr_cot_prospecto+'</tr><tr>'+tr_cot_cliente+'</tr></tbody></table></br>';
					$tablaresultadoscotizaciones.append(html_tr);
                    //listo
                    
                    
                }else{
                    jAlert("Esta consulta no genero ningun Resultado pruebe ingresando otros Parametros",'Atencion!!!!');
                }
                
                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-275;
                var pix_alto=alto+'px';
                $('#resultadosvisitas').tableScroll({height:parseInt(pix_alto)});
                
                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-275;
                var pix_alto=alto+'px';
                $('#resultadosoportunidades').tableScroll({height:parseInt(pix_alto)});
                
                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-275;
                var pix_alto=alto+'px';
                $('#resultadoscasos').tableScroll({height:parseInt(pix_alto)});
                
                var height2 = $('#cuerpo').css('height');
                var alto = parseInt(height2)-275;
                var pix_alto=alto+'px';
                $('#resultadosllamadas').tableScroll({height:parseInt(pix_alto)});
                
                $('#resultadoscotizaciones').tableScroll({height:100});
                
                
				 var height = $('#cuerpo').css('height');
				 //alert('height2: '+height2);
				 alto = parseInt(height)-220;
				 var pix_alto=alto+'px';
				 //alert('pix_alto: '+pix_alto);
				 $('#div_contenedor').css({'height': pix_alto});
				 //$('#content_results').tableScroll({height:parseInt(pix_alto)});
                
            });
            
        });
        
        
        /* Aqui empieza el codigo para este aplicativo */
        //Plugin de registro de Prospectos/Contactos/Clientes
            $registro_config_consultas = function(){
                
                
                var id_to_show = 0;
                $(this).modalPanelCrmConfigConsultas();
                var form_to_show = 'formCrmConfigConsultas';
                $('#' + form_to_show).each (function(){this.reset();});
                var $forma_selected = $('#' + form_to_show).clone();
                $forma_selected.attr({id : form_to_show + id_to_show});
                
                $('#forma-crmconfigconsultas-window').css({"margin-left": -400, 	"margin-top": -295});
                $forma_selected.prependTo('#forma-crmconfigconsultas-window');
                $forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
                $tabs_li_funcionalidad();
                
                $registro_casos  =   $('#forma-crmconfigconsultas-window').find('.registro_casos');
                $registro_oportunidades  =   $('#forma-crmconfigconsultas-window').find('.registro_oportunidades');
                $registro_llamadas   =   $('#forma-crmconfigconsultas-window').find('.registro_llamadas');
                $registro_visitas    =   $('#forma-crmconfigconsultas-window').find('.registro_visitas');
                $submit_actualizar = $('#forma-crmconfigconsultas-window').find('#submit');
                
                $identificador  =$('#forma-crmconfigconsultas-window').find('input[name=identificador]');
                
                $metas_visita  =$('#forma-crmconfigconsultas-window').find('input[name=metas_visita]');
                $totales_visita  =$('#forma-crmconfigconsultas-window').find('input[name=totales_visita]');
                $cumplido_visita  =$('#forma-crmconfigconsultas-window').find('input[name=cumplido_visita]');
                $conexito_visita =$('#forma-crmconfigconsultas-window').find('input[name=conexito_visita]');
                $conoportunidad_visita  =$('#forma-crmconfigconsultas-window').find('input[name=conoportunidad_visita]');
                $seguimiento_visita  =$('#forma-crmconfigconsultas-window').find('input[name=seguimiento_visita]');
                $efectividad_visita  =$('#forma-crmconfigconsultas-window').find('input[name=efectividad_visita]');
                $gestion_visita =$('#forma-crmconfigconsultas-window').find('input[name=gestion_visita]');
                $avance_visita =$('#forma-crmconfigconsultas-window').find('input[name=avance_visita]');
                
                $metas_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=metas_llamadas]');
                $total_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=total_llamadas]');
                $cumplido_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=cumplido_llamadas]');
                $entrantes_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=entrantes_llamadas]');
                $salientes_llamadas =$('#forma-regcrmconfigconsultasistro-window').find('input[name=salientes_llamadas]');
                $planeadas_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=planeadas_llamadas]');
                $con_exito_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=con_exito_llamadas]');
                $con_cita_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=con_cita_llamadas]');
                $conseguimiento_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=conseguimiento_llamadas]');
                $efectividad_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=efectividad_llamadas]');
                $gestion_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=gestion_llamadas]');
                $avance_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=avance_llamadas]');
                $planeacion_llamadas =$('#forma-crmconfigconsultas-window').find('input[name=planeacion_llamadas]');
                
                $facturacion_casos =$('#forma-crmconfigconsultas-window').find('input[name=facturacion_casos]');
                $producto_casos =$('#forma-crmconfigconsultas-window').find('input[name=producto_casos]');
                $garantia_casos =$('#forma-crmconfigconsultas-window').find('input[name=garantia_casos]');
                $distribucion_casos =$('#forma-crmconfigconsultas-window').find('input[name=distribucion_casos]');
                $danos_casos =$('#forma-crmconfigconsultas-window').find('input[name=danos_casos]');
                $devoluciones_casos =$('#forma-crmconfigconsultas-window').find('input[name=devoluciones_casos]');
                $cobranza_casos =$('#forma-crmconfigconsultas-window').find('input[name=cobranza_casos]');
                $varios_casos =$('#forma-crmconfigconsultas-window').find('input[name=varios_casos]');
                
                
                $metas_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=metas_oportunidades]');
                $total_metas_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=total_metas_oportunidades]');
                $montos_meta_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=montos_meta_oportunidades]');
                $total_montos_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=total_montos_oportunidades]');
                $metas_cumplidas_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=metas_cumplidas_oportunidades]');
                $montos_cumplidas_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=montos_cumplidas_oportunidades]');
                $inicial_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=inicial_oportunidades]');
                $seguimiento_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=seguimiento_oportunidades]');
                $visitas_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=visitas_oportunidades]');
                $cotizacion_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=cotizacion_oportunidades]');
                $negociacion_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=negociacion_oportunidades]');
                $cierre_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=cierre_oportunidades]');
                $ganados_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=ganados_oportunidades]');
                $perdidos_oportunidades =$('#forma-crmconfigconsultas-window').find('input[name=perdidos_oportunidades]');
                
                //funciona para seleccionar un checkbosx si en la base de datos indica que estara seleccionado
                $is_check_checkbox = function(campo, valor){
                    
                    if(valor == 'true'){
                        campo.attr('checked', true);
                    }
                }
                
                
                //alert("estoy aqui adentro");
                //var input_busqueda_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'obtener_facturas' + '/' + $identificador_cliente.val()  +'/out.json';
                var input_busqueda_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getDatosConfigConsulta.json';
                $arreglo = {'iu': $('#lienzo_recalculable').find('input[name=iu]').val()};
                
                $identificador.attr({'value': 0});
                $.post(input_busqueda_json,$arreglo,function(entry){
                    
                    if(entry['Config'][0] != null ){
                        $identificador.attr({'value': entry['Config']['0']['id']});
                        $is_check_checkbox($metas_visita, entry['Config']['0']['metas_visita']);
                        $is_check_checkbox($totales_visita, entry['Config']['0']['totales_visita']);
                        $is_check_checkbox($cumplido_visita, entry['Config']['0']['cumplido_visita']);
                        $is_check_checkbox($conexito_visita, entry['Config']['0']['conexito_visita']);
                        $is_check_checkbox($conoportunidad_visita, entry['Config']['0']['conoportunidad_visita']);
                        $is_check_checkbox($seguimiento_visita, entry['Config']['0']['seguimiento_visita']);
                        $is_check_checkbox($efectividad_visita, entry['Config']['0']['efectividad_visita']);
                        $is_check_checkbox($gestion_visita, entry['Config']['0']['gestion_visita']);
                        $is_check_checkbox($avance_visita, entry['Config']['0']['avance_visita']);
                        $is_check_checkbox($metas_llamadas, entry['Config']['0']['metas_llamadas']);
                        $is_check_checkbox($total_llamadas, entry['Config']['0']['total_llamadas']);
                        $is_check_checkbox($cumplido_llamadas, entry['Config']['0']['cumplido_llamadas']);
                        $is_check_checkbox($entrantes_llamadas, entry['Config']['0']['entrantes_llamadas']);
                        $is_check_checkbox($salientes_llamadas, entry['Config']['0']['salientes_llamadas']);
                        $is_check_checkbox($planeadas_llamadas, entry['Config']['0']['planeadas_llamadas']);
                        $is_check_checkbox($con_exito_llamadas, entry['Config']['0']['con_exito_llamadas']);
                        $is_check_checkbox($con_cita_llamadas, entry['Config']['0']['con_cita_llamadas']);
                        $is_check_checkbox($conseguimiento_llamadas, entry['Config']['0']['conseguimiento_llamadas']);
                        $is_check_checkbox($efectividad_llamadas, entry['Config']['0']['efectividad_llamadas']);
                        $is_check_checkbox($gestion_llamadas, entry['Config']['0']['gestion_llamadas']);
                        $is_check_checkbox($avance_llamadas, entry['Config']['0']['avance_llamadas']);
                        $is_check_checkbox($planeacion_llamadas, entry['Config']['0']['planeacion_llamadas']);
                        $is_check_checkbox($facturacion_casos, entry['Config']['0']['facturacion_casos']);
                        $is_check_checkbox($producto_casos, entry['Config']['0']['producto_casos']);
                        $is_check_checkbox($garantia_casos, entry['Config']['0']['garantia_casos']);
                        $is_check_checkbox($distribucion_casos, entry['Config']['0']['distribucion_casos']);
                        $is_check_checkbox($danos_casos, entry['Config']['0']['danos_casos']);
                        $is_check_checkbox($devoluciones_casos, entry['Config']['0']['devoluciones_casos']);
                        $is_check_checkbox($cobranza_casos, entry['Config']['0']['cobranza_casos']);
                        $is_check_checkbox($varios_casos, entry['Config']['0']['varios_casos']);
                        $is_check_checkbox($metas_oportunidades, entry['Config']['0']['metas_oportunidades']);
                        $is_check_checkbox($total_metas_oportunidades, entry['Config']['0']['total_metas_oportunidades']);
                        $is_check_checkbox($montos_meta_oportunidades, entry['Config']['0']['montos_meta_oportunidades']);
                        $is_check_checkbox($total_montos_oportunidades, entry['Config']['0']['total_montos_oportunidades']);
                        $is_check_checkbox($metas_cumplidas_oportunidades, entry['Config']['0']['metas_cumplidas_oportunidades']);
                        $is_check_checkbox($montos_cumplidas_oportunidades, entry['Config']['0']['montos_cumplidas_oportunidades']);
                        $is_check_checkbox($inicial_oportunidades, entry['Config']['0']['inicial_oportunidades']);
                        $is_check_checkbox($seguimiento_oportunidades, entry['Config']['0']['seguimiento_oportunidades']);
                        $is_check_checkbox($visitas_oportunidades, entry['Config']['0']['visitas_oportunidades']);
                        $is_check_checkbox($cotizacion_oportunidades, entry['Config']['0']['cotizacion_oportunidades']);
                        $is_check_checkbox($negociacion_oportunidades, entry['Config']['0']['negociacion_oportunidades']);
                        $is_check_checkbox($cierre_oportunidades, entry['Config']['0']['cierre_oportunidades']);
                        $is_check_checkbox($ganados_oportunidades, entry['Config']['0']['ganados_oportunidades']);
                        $is_check_checkbox($perdidos_oportunidades, entry['Config']['0']['perdidos_oportunidades']);
                    }else{
                        $identificador.attr({'value': 0});
                    }
                    
                });
                
                
                
                //Validar que no pueda seleccionar mas de 6 campos de cada tipo de registro
                $valida_checks_seleccionados = function($clase){
                    retorno = "";
                    contador = 0;
                    $clase.find('input[type=checkbox]').each(function(){
                        if (this.checked) {
                            contador ++;
                        }
                    });
                    
                    if(contador > 6){
                        retorno = "Esxede la cantidad de campos a seleccoinar";
                    }else{
                        retorno = "true";
                    }
                    
                    return retorno;
                }
                
                //Obtinen el total de campos seleccionados del tipo de registro
                $valida_cantidad_checks_seleccionados = function($clase){
                    contador = 0;
                    
                    $clase.find('input[type=checkbox]').each(function(){
                        if (this.checked) {
                            contador ++;
                        }
                    });
                    
                    return contador;
                }
                
                
                
                /*Validaciones para vicitas*/
                $metas_visita.click(function(){
                    valida = $valida_checks_seleccionados($registro_visitas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                
                $totales_visita.click(function(){
                    valida = $valida_checks_seleccionados($registro_visitas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                    
                });
                $cumplido_visita.click(function(){
                    valida = $valida_checks_seleccionados($registro_visitas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $conexito_visita.click(function(){
                    valida = $valida_checks_seleccionados($registro_visitas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $conoportunidad_visita.click(function(){
                    valida = $valida_checks_seleccionados($registro_visitas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $seguimiento_visita.click(function(){
                    valida = $valida_checks_seleccionados($registro_visitas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $efectividad_visita.click(function(){
                    valida = $valida_checks_seleccionados($registro_visitas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $gestion_visita.click(function(){
                    valida = $valida_checks_seleccionados($registro_visitas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                
                $avance_visita.click(function(){
                    valida = $valida_checks_seleccionados($registro_visitas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                
                
                
                /*Validaciones para llamadas*/
                $metas_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $total_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $cumplido_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $entrantes_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $salientes_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $planeadas_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $con_exito_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $con_cita_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $conseguimiento_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $efectividad_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $gestion_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $avance_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $planeacion_llamadas.click(function(){
                    valida = $valida_checks_seleccionados($registro_llamadas);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                
                
                /*Validaciones para Casos*/
                $facturacion_casos.click(function(){
                    valida = $valida_checks_seleccionados($registro_casos);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $producto_casos.click(function(){
                    valida = $valida_checks_seleccionados($registro_casos);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $garantia_casos.click(function(){
                    valida = $valida_checks_seleccionados($registro_casos);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $distribucion_casos.click(function(){
                    valida = $valida_checks_seleccionados($registro_casos);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $danos_casos.click(function(){
                    valida = $valida_checks_seleccionados($registro_casos);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $devoluciones_casos.click(function(){
                    valida = $valida_checks_seleccionados($registro_casos);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $cobranza_casos.click(function(){
                    valida = $valida_checks_seleccionados($registro_casos);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $varios_casos.click(function(){
                    valida = $valida_checks_seleccionados($registro_casos);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                
                
                
                
                /*Validaciones para Oportunidaddes*/
                $metas_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $total_metas_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $montos_meta_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $total_montos_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $metas_cumplidas_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $montos_cumplidas_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $inicial_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $seguimiento_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $visitas_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $cotizacion_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $negociacion_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $cierre_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $ganados_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($registro_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                $perdidos_oportunidades.click(function(){
                    valida = $valida_checks_seleccionados($perdidos_oportunidades);
                    if(valida != "true"){
                        this.checked = false;
                        jAlert(valida,'! Atencion');
                    }
                });
                
                
                $valida_cantidades_iguales_de_campos_seleccionados = function(){
                    
                    $registro_casos  =   $('#forma-crmconfigconsultas-window').find('.registro_casos');
                    $registro_oportunidades  =   $('#forma-crmconfigconsultas-window').find('.registro_oportunidades');
                    $registro_llamadas   =   $('#forma-crmconfigconsultas-window').find('.registro_llamadas');
                    $registro_visitas    =   $('#forma-crmconfigconsultas-window').find('.registro_visitas');

                    $cantidad_casos = $valida_cantidad_checks_seleccionados($registro_casos);
                    $cantidad_oportunidades = $valida_cantidad_checks_seleccionados($registro_oportunidades);
                    $cantidad_llamadas = $valida_cantidad_checks_seleccionados($registro_llamadas);
                    $cantidad_visitas = $valida_cantidad_checks_seleccionados($registro_visitas);
                    
                    if((parseInt($cantidad_casos) == parseInt($cantidad_casos)) && 
                            (parseInt($cantidad_casos) == parseInt($cantidad_casos)) && 
                            (parseInt($cantidad_casos) == parseInt($cantidad_casos)) && 
                            (parseInt($cantidad_casos) == parseInt($cantidad_casos)) && 
                            (parseInt($cantidad_casos) == parseInt($cantidad_casos)) ){
                        return true;
                    }else{
                        return false;
                    }
                    
                }
                
                
                
                var $cerrar_plugin = $('#forma-crmconfigconsultas-window').find('#close');
                var $cancelar_plugin = $('#forma-crmconfigconsultas-window').find('#boton_cancelar');
                
                    var respuestaProcesada = function(data){
                        if ( data['success'] == "true" ){
                            jAlert("Configuracion actualizada con exito", 'Atencion!');
                            var remove = function() {$(this).remove();};
                            $('#forma-crmconfigconsultas-overlay').fadeOut(remove);

                            //$get_datos_grid();
                        }else{
                            // Desaparece todas las interrogaciones si es que existen
                            $('#forma-crmconfigconsultas-window').find('div.interrogacion').css({'display':'none'});
                            
                            /*
                            var valor = data['success'].split('___');
                            //muestra las interrogaciones
                            for (var element in valor){
                                tmp = data['success'].split('___')[element];
                                longitud = tmp.split(':');
                                if( longitud.length > 1 ){
                                    $('#forma-crmcontactos-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
                                    .parent()
                                    .css({'display':'block'})
                                    .easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
                                }
                            }
                            */
                        }
                }
                var options = {dataType :  'json', success : respuestaProcesada};
                $forma_selected.ajaxForm(options);
                
                $submit_actualizar.bind('click',function(){
                    
                    if($valida_cantidades_iguales_de_campos_seleccionados() == true){
                        jConfirm('Son los campos correctos?' , 'Dialogo de confirmacion', function(r) {
                            if (r){
                                $submit_actualizar.parents("FORM").submit();
                            }
                        });
                    }else{
                        jAlert("Los campos seleccionados para cada configuracion, deben de ser la misma cantidad.", 'Atencion!');
                    }
                    // Always return false here since we don't know what jConfirm is going to do
                    return false;
                });
                
                $cerrar_plugin.bind('click',function(){
                    var remove = function() {$(this).remove();};
                    $('#forma-crmconfigconsultas-overlay').fadeOut(remove);
                });
                
                $cancelar_plugin.click(function(event){
                    var remove = function() {$(this).remove();};
                    $('#forma-crmconfigconsultas-overlay').fadeOut(remove);
                    
                });
                
        }
        //fin de la forma Registro Prospectos/Contactos/Clientes
        
        
        
        $configurar_consultas.bind('click', function(event){
            event.preventDefault();
            
            $registro_config_consultas();
            
        });
});
