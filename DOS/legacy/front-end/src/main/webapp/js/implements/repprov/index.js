$(function() {

    var config =  {
        empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
        sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
        tituloApp: 'Lista de Proveedores',
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
            return this.contextpath + "/controllers/repprov";
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

    //var $divreporte = $('#lienzo_recalculable').find('#table_proveedoress');
    var $divreporte = $('#lienzo_recalculable').find('#divreporte');
    var $folio = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=folio]');
    var $razon_proveedor = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[name=razon_proveedor]');
  // var $select_agentes = $('#lienzo_recalculable').find('select[name=select_agentes]');
	var $buscar_proveedor= $('#lienzo_recalculable').find('table#busqueda tr td').find('a[href*=buscar_proveedor]')


    var $boton_busqueda = $('#lienzo_recalculable').find('#boton_busqueda');
    var $boton_genera_pdf = $('#lienzo_recalculable').find('#boton_genera_pdf');


	$busca_proveedores = function(){
		$(this).modalPanel_Buscaprov();
		var $dialogoc =  $('#forma-buscaproveedor-window');
		$dialogoc.append($('div.buscador_proveedores').find('table.formaBusqueda_proveedores').clone());
		$('#forma-buscaproveedor-window').css({ "margin-left": -200, 	"margin-top": -200  });
		
		var $tabla_resultados = $('#forma-buscaproveedor-window').find('#tabla_resultado');
		var $campo_rfc = $('#forma-buscaproveedor-window').find('input[name=campo_rfc]');
		var $campo_email = $('#forma-buscaproveedor-window').find('input[name=campo_email]');
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
			$arreglo = {    rfc:$campo_rfc.val(),
							email:$campo_email.val(),
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
					$razon_proveedor.val($(this).find('#razon_social').html());
					
					//elimina la ventana de busqueda
					var remove = function() { $(this).remove(); };
					$('#forma-buscaproveedor-overlay').fadeOut(remove);
				});
			});
		});
		
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_rfc, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_email, $buscar_plugin_proveedor);
		$(this).aplicarEventoKeypressEjecutaTrigger($campo_nombre, $buscar_plugin_proveedor);
		
		$cancelar_plugin_busca_proveedor.click(function(event){
			//event.preventDefault();
			var remove = function() { $(this).remove(); };
			$('#forma-buscaproveedor-overlay').fadeOut(remove);
		});
	}//termina buscador de proveedores
	
	
	//*************************
    $buscar_proveedor.click(function(event){
        event.preventDefault();
        
        
        $busca_proveedores();//llamada a la funcion que busca proveedores
    });
    
//genera pdf del reporte de proveedores
	$boton_genera_pdf.click(function(event){
		event.preventDefault();
		
		
		var folio = '0';
		if($folio.val().trim()!='' ){
			folio=$folio.val();
			
		}
		
		var proveedor = '0';
		if($razon_proveedor.val().trim()!='' ){
			folio=$folio.val();
			proveedor=$razon_proveedor.val();
		}
		
		
		
		//var cadena = proveedor+folio;
		var cadena = folio+"___"+proveedor;
		
		
		//alert(cadena);
		//var id_proveedor=$id_proveedor_edo_cta.val();
		
		var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
		var input_json = config.getUrlForGetAndPost() + '/getReporteProveedores/'+cadena+'/'+config.getUi()+'/out.json';
		window.location.href=input_json;
		
	});//termina llamada json
	


    //ejecutar busqueda del reporte
    $boton_busqueda.click(function(event){


        $divreporte.children().remove();
        var input_json = config.getUrlForGetAndPost()+'/getReporteProveedores.json';




        $arreglo = {
          //'id_agente':$select_agentes.val(),
            'folio':$folio.val(),
			'proveedor':$razon_proveedor.val(),
            'iu': config.getUi()
        };
        var trr="";
        $.post(input_json,$arreglo,function(entry){
			
			trr = '<table id="table_rep">';

            trr +='<thead> <tr>';


                trr +='<td >No. de Proveedor</td>';
                trr +='<td >Razon&nbsp;Social</td>';
                trr +='<td >RFC</td>';
                trr +='<td >Direccion</td>';
                trr +='<td >Tel&eacute;fonos</td>';
                trr +='<td >Correo</td>';
                trr +='<td >Fecha de Creaci&oacute;n</td>';

            trr +='</tr> </thead>';
            if(entry['Proveedores'].length > 0 ){
                $.each(entry['Proveedores'],function(entryIndex,proveedores){
                    trr += '<tr>';
						trr += '<td width="50px">'+proveedores['folio']+'</td>';
                        trr += '<td width="350px">'+proveedores['razon_social']+'</td>';
                        trr += '<td width="100px">'+proveedores['rfc']+'</td>';
                        trr += '<td >'+proveedores['direccion_proveedor']+'</td>';
                        trr += '<td >'+proveedores['telefonos']+'</td>';
                        trr += '<td width="150px">'+proveedores['correo_electronico']+'</td>';
                        trr += '<td >'+proveedores['momento_creacion']+'</td>';
                    trr += '</tr>';
                });
              


            }else{
                jAlert("Esta consulta no genero Resultados",'Atencion!!!');
            }

			trr += '</table>';
            $divreporte.append(trr);
            
			var height2 = $('#cuerpo').css('height');
			var alto = parseInt(height2)-275;
			var pix_alto=alto+'px';
			$('#table_rep').tableScroll({height:parseInt(pix_alto)});

        });

    });

   // $aplicar_evento_keypress($select_agentes, $boton_busqueda);
   $aplicar_evento_keypress($boton_busqueda,$folio,$razon_proveedor);
   //$(this).aplicarEventoKeypressEjecutaTrigger($boton_busqueda);
    $folio.focus();
   //$select_agentes.focus();

});



