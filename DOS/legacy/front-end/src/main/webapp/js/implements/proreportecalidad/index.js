$(function() {
    var config =  {
        empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
        sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
        tituloApp: 'Reporte de Calidad',
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
            return this.contextpath + "/controllers/proreportecalidad";
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

    var $div_reporte = $('#lienzo_recalculable').find('#div_reporte');
    var $folio = $('#lienzo_recalculable').find('input[name=folio]');
    var $codigo = $('#lienzo_recalculable').find('input[name=codigo]');
    var $select_tipo = $('#lienzo_recalculable').find('select[name=select_tipo]');
	var $fecha_inicial = $('#lienzo_recalculable').find('input[name=fecha_inicial]');
	var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
	
    var $boton_busqueda = $('#lienzo_recalculable').find('#boton_busqueda');
    var $boton_genera_pdf = $('#lienzo_recalculable').find('#boton_genera_pdf');
	
    var input_json = config.getUrlForGetAndPost()+'/getDatosBusqueda.json';
    $arreglo = {
        'iu':iu = $('#lienzo_recalculable').find('input[name=iu]').val()
    };
	
    $.post(input_json,$arreglo,function(entry){
        //aqui van los campos que se cargan desde un principio.
        $select_tipo.children().remove();
        var tipo_hmtl = '<option value="0" selected="yes">[--Todos--]</option>';
        $.each(entry['ordenTipos'],function(entryIndex,tipo){
            tipo_hmtl += '<option value="' + tipo['id'] + '"  >' + tipo['titulo'] + '</option>';
        });
        $select_tipo.append(tipo_hmtl);
    });//termina llamada json



	//----------------------------------------------------------------
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
	
	
	$fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
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
	
	$fecha_inicial.val(mostrarFecha());
	
	
	
	$fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
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
	
	$fecha_final.val(mostrarFecha());






    $boton_genera_pdf.click(function(event){
		var folio=$folio.val();
		var codigo=$codigo.val();
		
		var fecha_inicial = $fecha_inicial.val();
		var fecha_final = $fecha_final.val();
		
		if(codigo==''){ codigo='0'; }
		if(folio==''){ folio='0'; }
		if(fecha_inicial==''){ fecha_inicial='0'; }
		if(fecha_final==''){ fecha_final='0'; }
		
		var cadena = folio +"___"+ codigo +"___"+$select_tipo.val()+"___"+fecha_inicial+"___"+fecha_final;
		
        var input_json = config.getUrlForGetAndPost() + '/getReporteCalidad/'+cadena+'/'+config.getUi()+'/out.json';
        window.location.href=input_json;
    });
    var height2 = $('#cuerpo').css('height');
    var alto = parseInt(height2)-240;
    var pix_alto=alto+'px';

    $('#table_clientes').tableScroll({
        height:parseInt(pix_alto)
    });

    //ejecutar busqueda del reporte
    $boton_busqueda.click(function(event){
        $div_reporte.children().remove();
        var input_json = config.getUrlForGetAndPost()+'/getProReporteCalidad.json';
        $arreglo = {
            'folio':$folio.val(),
            'codigo':$codigo.val(),
            'tipo':$select_tipo.val(),
            'f_inicial':$fecha_inicial.val(),
            'f_final':$fecha_final.val(),
            'iu': config.getUi()
        };

        var trr="";
        $.post(input_json,$arreglo,function(entry){
			trr +='<table id="prod" class="tablescrollv2" cellspacing="0">';
            trr +='<thead> <tr>';
            /*
            trr +='<td width="100">Producto</td>';
            trr +='<td width="100">Codigo</td>';
            */
            trr +='<td width="70">Cant. K.</td>';
            trr +='<td width="70">Cant. L.</td>';
            trr +='<td width="80">Folio</td>';
            trr +='<td width="70">Fecha</td>';
            trr +='<td width="45">Fineza</td>';
            trr +='<td width="65">Viscosidad</td>';
            trr +='<td width="55">Densidad</td>';
            trr +='<td width="35">PC&nbsp;%</td>';
            trr +='<td width="30">DE</td>';
            trr +='<td width="35">Brillo</td>';
            trr +='<td width="45">Dureza</td>';
            trr +='<td width="40">%&nbsp;N.V.</td>';
            trr +='<td width="35">PH</td>';
            trr +='<td width="60">Adhesion</td>';
            trr +='<td width="95">MP&nbsp;Deshabasto</td>';
            trr +='<td width="95">MP&nbsp;Contratipo</td>';
            trr +='<td width="95">MP&nbsp;Agregados</td>';
            trr +='<td width="75">Observ</td>';
            trr +='<td width="210">Comentarios</td>';
            trr +='</tr> </thead>';
            trr +='<tbody>';
			
			var sku='';
			var id_op=0;
			
            if(entry['Datos_R_Calidad'].length > 0 ){
                $.each(entry['Datos_R_Calidad'],function(entryIndex,R_Calidad){
					
					if(parseInt(id_op)!=parseInt(R_Calidad['id'])){
						trr += '<tr>';
						trr += '<td width="140" colspan="2" class="bordertopolid">'+R_Calidad['codigo']+'</td>';
						trr += '<td width="1165" colspan="17" class="bordertopolid">'+R_Calidad['descripcion']+'</td>';
						trr += '</tr>';
						
						sku=R_Calidad['codigo'];
						id_op=R_Calidad['id'];
					}
					
                    trr += '<tr>';
                    trr += '<td width="70" align="right" >'+R_Calidad['cantk']+'</td>';
                    trr += '<td width="70" align="right" >'+R_Calidad['cantl']+'</td>';
                    trr += '<td width="80" >'+R_Calidad['lote']+'</td>';
                    trr += '<td width="70" >'+R_Calidad['fecha']+'</td>';
                    trr += '<td width="45" >'+R_Calidad['fineza']+'</td>';//Fineza
                    trr += '<td width="65" >'+R_Calidad['viscosidad']+'</td>';//Viscosidad
                    trr += '<td width="55" >'+R_Calidad['densidad']+'</td>';//Densidad
                    trr += '<td width="35" >'+R_Calidad['pc']+'</td>';
                    trr += '<td width="30" >'+R_Calidad['de']+'</td>';
                    trr += '<td width="35" >'+R_Calidad['brillo']+'</td>';//Brillo
                    trr += '<td width="45" >'+R_Calidad['dureza']+'</td>';//Dureza
                    trr += '<td width="40" >'+R_Calidad['nv']+'</td>';//% N.V.
                    trr += '<td width="35" >'+R_Calidad['ph']+'</td>';
                    trr += '<td width="60" >'+R_Calidad['adhesion']+'</td>';
                    trr += '<td width="95" >'+R_Calidad['mp_deshabasto']+'</td>';
                    trr += '<td width="95" >'+R_Calidad['mp_contratipo']+'</td>';
                    trr += '<td width="95" >'+R_Calidad['mp_agregados']+'</td>';
                    trr += '<td width="75" >'+R_Calidad['observ']+'</td>';
                    trr += '<td width="210" >'+R_Calidad['comentarios']+'</td>';
                    trr += '</tr>';
                });
                
				trr += '<tr>';
				trr += '<td width="140" colspan="19" class="bordertopolid"></td>';
				trr += '</tr>';
						
                trr +='</tbody>';
                trr +='</table>';
                
                $div_reporte.append(trr); 
                
                
            }else{
                jAlert("Esta consulta no genero Resultados",'Atencion!!!');
            }
			
            var height2 = $('#cuerpo').css('height');
            var alto = parseInt(height2)-275;
            var pix_alto=alto+'px';
            
            $('#prod tbody').css({'height': pix_alto});
        });
    });
    
    
	$aplicar_evento_keypress($folio, $boton_busqueda);
	$aplicar_evento_keypress($codigo, $boton_busqueda);
	$aplicar_evento_keypress($select_tipo, $boton_busqueda);
	$aplicar_evento_keypress($fecha_inicial, $boton_busqueda);
	$aplicar_evento_keypress($fecha_final, $boton_busqueda);
    
    $folio.focus();
});
