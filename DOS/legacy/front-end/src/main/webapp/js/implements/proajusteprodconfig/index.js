$(function() {
	var config =  {
		empresa: $('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal: $('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Sustituci&oacute;n de productos en formulas' ,                 
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
			return this.contextpath + "/controllers/proajusteprodconfig";
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
	
	
	var $buscador_codigo = $('#lienzo_recalculable').find('input[name=buscador_codigo]');
        var $buscador_descripcion = $('#lienzo_recalculable').find('input[name=buscador_descripcion]');
        
        var $id_producto = $('#lienzo_recalculable').find('input[name=id_producto]');
        var $sustituto_codigo = $('#lienzo_recalculable').find('input[name=sustituto_codigo]');
        var $sustituto_descripcion = $('#lienzo_recalculable').find('input[name=sustituto_descripcion]');
        
	var $cambiar_productos = $('#lienzo_recalculable').find('table#busqueda tr td').find('#cambiar_productos');
	var $busqueda_formulas_con_productos = $('#lienzo_recalculable').find('table#busqueda tr td').find('input[value$=Buscar]');
        var $buscar_producto_sustituto = $('#lienzo_recalculable').find('#buscar_producto_sustituto');
        
        $id_producto.val(0);
        
	var $divlistaformulasconproducto= $('#lienzo_recalculable').find('#divlistaformulasconproducto');
	
        
        var arreglo_parametros = { 
            iu:config.getUi()
        };
        
        $buscar_producto_sustituto.click(function(event){
            event.preventDefault();
            $busca_productos(1);
        })
        
        //buscador de productos
	$busca_productos = function(tipo_busqueda){
            sku_buscar = "";
            
		//limpiar_campos_grids();
		$(this).modalPanel_Buscaproducto();
		var $dialogoc =  $('#forma-buscaproducto-window');
		//var $dialogoc.prependTo('#forma-buscaproduct-window');
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
		var input_json_tipos = config.getUrlForGetAndPost() +'/getProductoTipos.json';
		$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
		$.post(input_json_tipos,$arreglo,function(data){
                    
                    //Llena el select tipos de productos en el buscador
                    $select_tipo_producto.children().remove();
                    //<option value="0" selected="yes">[--Seleccionar Tipo--]</option>
                    var prod_tipos_html = '';
                    
                    $.each(data['prodTipos'],function(entryIndex,pt){
                        
                        
                        //para buscar elementos de el proceso
                        if(tipo_busqueda == 1){
                            if(pt['id'] == 2 || pt['id'] == 1  || pt['id'] == 7 ){
                                prod_tipos_html += '<option value="' + pt['id'] + '"  >' + pt['titulo'] + '</option>';
                            }
                        }
                       
                    });
                    $select_tipo_producto.append(prod_tipos_html);
		
		$campo_sku.val(sku_buscar);
		
		//click buscar productos
		$buscar_plugin_producto.click(function(event){
			event.preventDefault();
                        
			var input_json = config.getUrlForGetAndPost() +'/get_buscador_productos.json';
			$arreglo = {'sku':$campo_sku.val(),
                                        'tipo':$select_tipo_producto.val(),
                                        'descripcion':$campo_descripcion.val(),
                                        iu: config.getUi() 
                                }
			var trr = '';
			$tabla_resultados.children().remove();
			$.post(input_json,$arreglo,function(entry){
                            
				$.each(entry['productos'],function(entryIndex,producto){
                                    trr = '<tr>';
                                        trr += '<td width="120">';
                                            trr += '<span class="sku_prod_buscador">'+producto['sku']+'</span>';
                                            trr += '<input type="hidden" id="id_prod_buscador" value="'+producto['id']+'">';
                                        trr += '</td>';
                                        trr += '<td width="280"><span class="titulo_prod_buscador">'+producto['descripcion']+'</span></td>';
                                        trr += '<td width="90"><span class="unidad_prod_buscador">'+producto['unidad']+'</span></td>';
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
                                    var id_prod=$(this).find('#id_prod_buscador').val();
                                    var codigo=$(this).find('span.sku_prod_buscador').html();
                                    var descripcion=$(this).find('span.titulo_prod_buscador').html();
                                    var producto=$(this).find('span.tipo_prod_buscador').html();
                                    var unidad=$(this).find('span.unidad_prod_buscador').html();
                                    
                                    //buscador principal de el proceso
                                    if(tipo_busqueda == 1){
                                        
                                        //asignar a los campos correspondientes el sku y y descripcion
                                        $id_producto.val(id_prod);
                                        $sustituto_codigo.val(codigo);
                                        $sustituto_descripcion.val(descripcion);
                                        
                                    }
                                    
                                    //elimina la ventana de busqueda
                                    var remove = function() {$(this).remove();};
                                    $('#forma-buscaproducto-overlay').fadeOut(remove);
                                    //asignar el enfoque al campo sku del producto
                                });
                            });
			});
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
	
	//click generar reporte de facturacion
	$cambiar_productos.click(function(event){
            event.preventDefault();
            $id_formulas = "";
            $id_producto_anterior = 0;
            $selecciono = "0";
            $('#formulas').find('input[name=eliminar]').each(function(){
                
                if(this.checked){
                    tr_tmp = $(this).parent().parent();
                    $id_formulas += "'"+tr_tmp.find('span.id_form').html()+"',";
                    //$id_formulas.push(tr_tmp.find('span.id_form').html());
                    $id_producto_anterior = tr_tmp.find('span.inv_prod_id').html();
                    $selecciono = "1";
                }
            });
            
            if($selecciono == "0"){
                $selecciono = "Debe de seleccionar almenos un producto";
            }
            if($id_producto.val() == 0){
                $selecciono = "Debe de ingresar un producto sustituto";
            }
            
            $id_formulas = $id_formulas.substring(0,($id_formulas.length - 1));
            
            if($selecciono == "1"){
                var restful_json_service = config.getUrlForGetAndPost() + '/getActualizaProductoEnFormulas.json';
                $arreglo = {
                                'id_producto_existe':$id_producto_anterior,
                                'id_producto_sustituto':$id_producto.val(),
                                'id_formulas':$id_formulas,
                                'iu': config.getUi() 
                            };
                            
                $.post(restful_json_service,$arreglo,function(entry){
                    if ( entry['success'] == 'true' ){
                        jAlert("El producto fue sustituido.", 'Atencion!');
                    }else{
                        jAlert("Noi se pudo realizar el proceso.", 'Atencion!');
                    }
                });

            }else{
                jAlert($selecciono,'Atencion!');
            }
            //var busqueda = $select_opciones.val()+"___"+$agente.val()+"___"+$ciente.val()+"___"+$fecha_inicial.val()+"___"+$fecha_final.val();
            //var input_json = config.getUrlForGetAndPost() + '/get_genera_reporte_facturacion/'+busqueda+'/'+config.getUi()+'/out.json';
            //window.location.href=input_json;
	});
	
	
	$busqueda_formulas_con_productos.click(function(event){
            event.preventDefault();
            
            $divlistaformulasconproducto.children().remove();
            
            if($buscador_codigo.val()=="" && $buscador_descripcion.val()==""){
                jAlert("Introduzca un c&oacute;digo o descripci&oacute;n",'Atencion!');
                
                $('#lienzo_recalculable').find('.producto_sustituto').hide();
            }else{
                
                var arreglo_parametros = {
                    codigo: $buscador_codigo.val(),
                    descipcion: $buscador_descripcion.val(),
                    iu:config.getUi()
                };
                
                var restful_json_service = config.getUrlForGetAndPost() + '/getFormulasConEsteProducto.json'
                var cliente="";
                $.post(restful_json_service,arreglo_parametros,function(entry){
                    
                    
                    
                    var body_tabla = entry['ProdFormulas'];
                    var footer_tabla = entry['Totales'];
                    
                    if(body_tabla.length > 0){
                        $('#lienzo_recalculable').find('.producto_sustituto').show();
                    }else{
                        $('#lienzo_recalculable').find('.producto_sustituto').hide();
                    }
                    
                    var header_tabla = {
                        id		:'Todos',
                        sku            :'Clave Prod',
                        descripcion           :'Descripc&oacute;in Producto',
                        sku_salida            :'Clave Prod. Terminado',
                        descripcion_salida			:'Producto Formulado'
                    };
                    
                    
                    var TPventa_neta=0.0;
                    var Sumatoriaventa_neta = 0.0;
                    var sumatotoriaporciento = 0.0;
                    var html_reporte = '';
                    var porcentaje = 0.0;

                    var numero_control=0.0; 
                    var cliente=0.0; 
                    var moneda="$"; 
                    var venta_neta=0.0; 
                    var porciento=0.0;
                    var tmp= 0;
                    
                    
                    html_reporte += '<table id="formulas" >';
                    html_reporte +='<thead> <tr>';
                    for(var key in header_tabla){
                        var attrValue = header_tabla[key];
                        if(attrValue == "Todos"){
                            html_reporte +='<td width="90px" align="left">'+attrValue+'&nbsp;&nbsp;<input type="checkbox" name="eliminar_todos"  /></td>'; 
                        }
                        if(attrValue == "Clave Prod"){
                            html_reporte +='<td width="90px" align="left">'+attrValue+'</td>'; 
                        }
                        
                        if(attrValue == "Descripc&oacute;in Producto"){
                            html_reporte +='<td width="272px" align="left">'+attrValue+'</td>'; 
                        }
                        if(attrValue == "Clave Prod. Terminado"){
                            html_reporte +='<td width="90px" align="left">'+attrValue+'</td>'; 
                        }
                        if(attrValue == "Producto Formulado"){
                            html_reporte +='<td width="272px" align="left">'+attrValue+'</td>'; 
                        }
                    }
                    html_reporte +='</tr> </thead>';
                    
                    
                    var orden_compra="";
                    var simbolo_moneda="";
                    
                    html_reporte +='<tbody>';
                    for(var i=0; i<body_tabla.length; i++){
                        html_reporte +='<tr>';
                        html_reporte +='<td align="center" >'; 
                            html_reporte +='<span class="id_form" style="display:none;">'+body_tabla[i]["id"]+'</span>'; 
                            html_reporte +='<span class="inv_prod_id"  style="display:none;">'+body_tabla[i]["inv_prod_id"]+'</span>'; 
                            html_reporte +='<input type="checkbox" id="eliminar" name="eliminar" />'; 
                        html_reporte +='</td>'; 
                        html_reporte +='<td align="left" >'+body_tabla[i]["sku"]+'</td>'; 
                        html_reporte +='<td align="left" >'+body_tabla[i]["descripcion"]+'</td>'; 
                        html_reporte +='<td align="left" >'+body_tabla[i]["sku_salida"]+'</td>'; 
                        html_reporte +='<td align="left" >'+body_tabla[i]["descripcion_salida"]+'</td>';
                        html_reporte +='</tr>';
                    }
                    
                    html_reporte +='</tbody>';
                    
                    
                    
                    html_reporte += '</table>';
                    
                    
                    $divlistaformulasconproducto.append(html_reporte); 
                    var height2 = $('#cuerpo').css('height');
                    var alto = parseInt(height2)-300;
                    var pix_alto=alto+'px';
                    $('#formulas').tableScroll({height:parseInt(pix_alto)});
                    
                    
                    $divlistaformulasconproducto.find('input[name=eliminar_todos]').bind("change",function(){
                        
                        if($divlistaformulasconproducto.find('input[name=eliminar_todos]').is(':checked')){
                            
                            
                            //$divlistaformulasconproducto.find('tbody > tr').each(function (index){
                            $('#formulas').find('input[name=eliminar]').each(function(){
                                
                                if(!this.checked){
                                    this.checked = true;
                                }
                                
                                $(this).click(function(event){
                                    if(this.checked){
                                        this.checked = false;
                                    }else{
                                        this.checked = true;
                                    }
                                });
                                
                            });
                        }else{
                            //$divlistaformulasconproducto.find('tbody > tr').each(function (index){
                            $('#formulas').find('input[name=eliminar]').each(function(){
                                
                                if(this.checked){
                                    this.checked = false;
                                }
                                
                                $(this).click(function(event){
                                    if(this.checked){
                                        this.checked = false;
                                    }else{
                                        this.checked = true;
                                    }
                                });
                                
                            });
                        }
                    });
                    
                    
                    
                });
                
            }
             
	});
	
});   
        
        
        
        
    
