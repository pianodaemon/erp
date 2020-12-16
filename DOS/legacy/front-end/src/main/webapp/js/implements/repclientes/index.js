$(function() {

    var config =  {
        empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
        sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
        tituloApp: 'Lista de Clientes',
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
            return this.contextpath + "/controllers/repclientes";
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

    //var $divreporte = $('#lienzo_recalculable').find('#table_clientes');
    var $divreporte = $('#lienzo_recalculable').find('#divreporte');
    var $select_agentes = $('#lienzo_recalculable').find('select[name=select_agentes]');
    
    var $boton_busqueda = $('#lienzo_recalculable').find('#boton_busqueda');
    var $boton_genera_pdf = $('#lienzo_recalculable').find('#boton_genera_pdf');
    
    var input_json = config.getUrlForGetAndPost()+'/getDatosBusqueda.json';
    $arreglo = {
        'iu':iu = $('#lienzo_recalculable').find('input[name=iu]').val()
    };
    $.post(input_json,$arreglo,function(entry){
        //aqui van los campos que se cargan desde un principio.
        $select_agentes.children().remove();
        var almacen_hmtl = '';
        var almacen_hmtl = '<option value="0" selected="yes">[--Todos--]</option>';
        $.each(entry['Agentes'],function(entryIndex,alm){
            almacen_hmtl += '<option value="' + alm['id'] + '"  >' + alm['nombre_agente'] + '</option>';
        });
        $select_agentes.append(almacen_hmtl);
    });//termina llamada json



    $boton_genera_pdf.click(function(event){
        //event.preventDefault();
        var cadena =  $select_agentes.val() ;
        var input_json = config.getUrlForGetAndPost() + '/getReporteClientes/'+cadena+'/'+config.getUi()+'/out.json';
        window.location.href=input_json;
    });
    
    
    //ejecutar busqueda del reporte
    $boton_busqueda.click(function(event){
		//Eliminar tabla anterior que se encuentra en el divreporte
        $divreporte.children().remove();
        var input_json = config.getUrlForGetAndPost()+'/getReporteClientes.json';
        
        $arreglo = {
            'id_agente':$select_agentes.val(),
            'iu': config.getUi()
        };
        var trr="";
        
        $.post(input_json,$arreglo,function(entry){
			trr = '<table id="table_rep">';
			trr +='<thead> <tr>';
				trr +='<td >Razon&nbsp;Social</td>';
				trr +='<td >Telefono(s)</td>';
				trr +='<td >Email</td>';
				trr +='<td >Direcci&oacute;n</td>';
			trr +='</tr> </thead>';
			
            if(entry['Clientes'].length > 0 ){
                $.each(entry['Clientes'],function(entryIndex,cliente){
                    trr += '<tr>';
                        trr += '<td width="200px">'+cliente['razon_social']+'</td>';
                        trr += '<td >'+cliente['telefonos']+'</td>';
                        trr += '<td width="150px">'+cliente['email']+'</td>';
                        trr += '<td >'+cliente['direccion_cliente']+'</td>';
                    trr += '</tr>';
                });
            }else{
                jAlert("Esta consulta no genero Resultados",'Atencion!!!');
            }
            /*
			var html_tfoot +='<tfoot>';
			html_tfoot +='</tfoot>';
            trr += html_tfoot;
            */
            trr += '</table>';
            $divreporte.append(trr);
            
			var height2 = $('#cuerpo').css('height');
			var alto = parseInt(height2)-275;
			var pix_alto=alto+'px';
			$('#table_rep').tableScroll({height:parseInt(pix_alto)});
        });

    });

    $aplicar_evento_keypress($select_agentes, $boton_busqueda);

    $select_agentes.focus();

});



