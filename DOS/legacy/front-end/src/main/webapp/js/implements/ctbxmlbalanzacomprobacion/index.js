$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: document.title ,                 
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
			return this.contextpath + "/controllers/ctbxmlbalanzacomprobacion";
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
	
	var $select_tipo_envio = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_tipo_envio]');
	var $select_ano = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_ano]');
	var $select_mes = $('#lienzo_recalculable').find('table#busqueda tr td').find('select[name=select_mes]');
	
	var $div_rep= $('#lienzo_recalculable').find('#div_rep');
	
	var $div_busqueda= $('#lienzo_recalculable').find('#div_busqueda');
	
	var array_meses = {01:"Enero",  02:"Febrero", 03:"Marzo", 04:"Abirl", 05:"Mayo", 06:"Junio", 07:"Julio", 08:"Agosto", 09:"Septiembre", 10:"Octubre", 11:"Noviembre", 12:"Diciembre"};
	
	var arreglo_parametros = { iu:config.getUi() };
	var restful_json_service = config.getUrlForGetAndPost() + '/getDatos.json';
	$.post(restful_json_service,arreglo_parametros,function(entry){
		//Carga select de años
		$select_ano.children().remove();
		var html_anio = '';
		$.each(entry['Anios'],function(entryIndex,anio){
			if(parseInt(anio['valor']) == parseInt(entry['Dato'][0]['anioActual']) ){
				html_anio += '<option value="' + anio['valor'] + '" selected="yes">' + anio['valor'] + '</option>';
			}else{
				html_anio += '<option value="' + anio['valor'] + '"  >' + anio['valor'] + '</option>';
			}
		});
		$select_ano.append(html_anio);
		
		//Cargar select del Mes inicial
		$select_mes.children().remove();
		var select_html = '';
		for(var i in array_meses){
			if(parseInt(i) == parseInt(entry['Dato'][0]['mesActual']) ){
				select_html += '<option value="' + i + '" selected="yes">' + array_meses[i] + '</option>';	
			}else{
				select_html += '<option value="' + i + '"  >' + array_meses[i] + '</option>';	
			}
		}
		$select_mes.append(select_html);
	});
	
	
	
	//Descargar xml
	$('#lienzo_recalculable').find('table#busqueda tr td').find('#xml').click(function(event){
		event.preventDefault();
		
		var input_json = config.getUrlForGetAndPost() +'/getCreaXml.json';
		var $arreglo = {
			'tenvio':$select_tipo_envio.val(),
			'anio':$select_ano.val(),
			'mes':$select_mes.val(),
			'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
		}
		
		$.post(input_json,$arreglo,function(entry){
			if(entry['success']){
				var input_json = config.getUrlForGetAndPost() + '/getXml/'+ entry['name'] +'/'+ $('#lienzo_recalculable').find('input[name=iu]').val() +'/out.json';
				
				window.location.href=input_json;
			}else{
				//jAlert(entry['msj'], 'Atencion!');
				
				if(entry['errores']){
					if(entry['errores'].length > 0){
						var table_movs = '<div style="overflow-x:hidden; overflow-y:auto;width:100%; height:300px;">'
						table_movs +='<table border="1">';
						table_movs +='<thead>';
							table_movs +='<tr>';
								table_movs +='<th class="grid-head" width="70">* SAT</th>';
								table_movs +='<th class="grid-head" width="80">* Cuenta</th>';
								table_movs +='<th class="grid-head" width="80">SubCta. de</th>';
								table_movs +='<th class="grid-head" width="50">* Natur.</th>';
								table_movs +='<th class="grid-head" width="50">* Nivel</th>';
								table_movs +='<th class="grid-head" width="200">* Descripci&oacute;n</th>';
							table_movs +='</tr>';
						table_movs +='</thead>';
						table_movs +='<tbody>';
						$.each(entry['errores'],function(entryIndex,req){
							table_movs +='<tr>';
								table_movs +='<td width="70" align="left">'+ req['codAgrup'] +'</td>';
								table_movs +='<td width="80" align="left">'+ req['numCta'] +'</td>';
								table_movs +='<td width="80" align="left">'+ req['subCtaDe'] +'</td>';
								table_movs +='<td width="50" align="center">'+ req['natur'] +'</td>';
								table_movs +='<td width="50" align="center">'+ req['nivel'] +'</td>';
								table_movs +='<td width="200" align="left">'+ req['desc'] +'</td>';
							table_movs +='</tr>';
						});
						table_movs +='</tbody>';
						table_movs +='</table>';
						table_movs +='</div>';
						table_movs +='<div>* Datos requeridos</div>';
						jAlert(table_movs, entry['msj'], function(r) { 
							//$contenedor.find('a[href=ver_mas]').focus();
						});
					}
				}
			}
		});//termina llamada json
		
		
	});//termina llamada json
	
	
});
