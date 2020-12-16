$.fn.tablaOrdenableCarteras = function(data,grid,loadForm){
    
    //
	var $theadgrid = grid.find('thead');
	var $tbodygrid = grid.find('tbody');
	
	var $item_por_pag = data['DataForGrid'][0]['items_por_pag'];
	var $total_items = data['DataForGrid'][0]['total_items'];
	var $total_paginas = data['DataForGrid'][0]['total_paginas'];
	var $start_pag = data['DataForGrid'][0]['pag_start'];
	var $display_pag = data['DataForGrid'][0]['display_pag'];
	//var $pintargrid = data['DatForControll'][0]['pintargrid'];
	var $desc = data['DataForGrid'][0]['desc'];
	var $orderby = data['DataForGrid'][0]['orderby'];
	var $input_json = data['DataForGrid'][0]['input_json'];
	var $cadena_busqueda = data['DataForGrid'][0]['cadena_busqueda'];
	var $iu = data['DataForGrid'][0]['iu'];
	
        
    
	var alternarColoresFilas = function($elemento_tabla){
		$('tbody tr:odd' , $elemento_tabla).removeClass('even').addClass('odd');
		$('tbody tr:even' , $elemento_tabla).removeClass('odd').addClass('even');
	};
	
	$mouseOverEditGrid = function(item, $tbodygrid){
		$tbodygrid.find('a[href*=cancelar_'+item+']').mouseover(function(event){
			$(this).find('#img_eliminar').removeClass("onmouseOutDelete").addClass("onmouseOverDelete");
		});
		$tbodygrid.find('a[href*=cancelar_'+item+']').mouseout(function(){
			$(this).find('#img_eliminar').removeClass("onmouseOverDelete").addClass("onmouseOutDelete");
		});
		
		$tbodygrid.find('a[href*=editar_'+item+']').mouseover(function(){
			$(this).find('#img_editar').removeClass("onmouseOutEdit").addClass("onmouseOverEdit");
		});
		
		$tbodygrid.find('a[href*=editar_'+item+']').mouseout(function(){
			$(this).find('#img_editar').removeClass("onmouseOverEdit").addClass("onmouseOutEdit");
		});
	};
	
	$ordenamiento_header = function($header){
		$header.find('tr th').each(function(i){
			$item_header = $(this).find('.header__').html();
			
			$header.find('.'+$item_header).click(function(event){
				$order_by = $(this).find('.header__').html();
				if($desc == 'DESC'){
					$desc = 'ASC';
				}else{
					$desc = 'DESC';
				}
				$orderby = $(this).find('.header__').html();
				$redrawgrid(grid);
			});
		});
	}
	$ordenamiento_header($theadgrid);
	
	$clicktr = function(item, $tbodygrid){
		//$tr_clickeado = $tbodygrid.find('a[href*=editar_'+item+'],a[href*=cancelar_'+item+']');
		//$tr_clickeado = $tbodygrid.find('a[href="editar_'+item+'"],a[href="cancelar_'+item+'"]');
		$tr_clickeado = $tbodygrid.find('input#pdf_'+item);
		
		var cont = 0;
		$tr_clickeado.click(function(event){
			event.preventDefault();
			$apuntador_tr = $(this).parent().parent();
			
				var llave = $(this).attr('id').split('_')[1];
				//alert(llave);
				loadForm(llave,'genera_pdf');	
			
			/*
			if ( $(this).is('.cancelar_item') ){
				var llave = $(this).attr('href').split('_')[1];
				//$(this).modalPanel();
				
				loadForm(llave,'cancel');
				//$redrawgrid($tbodygrid);
			};
			*/
			Elastic.reset(document.getElementById('lienzo_recalculable'));
		});
	}
	
        $clicktr2 = function(item, $tbodygrid){
            $tr_clickeado2 = $tbodygrid.find('input#pdfFactura_'+item);
            $tr_clickeado2.click(function(event){
                event.preventDefault();
                //alert("btn_event2");
                var llave = $(this).attr('id').split('_')[1];
		loadForm(llave,'genera_pdfFactura');	                
            });
        }

         $clicktr3 = function(item, $tbodygrid){
            $tr_clickeado3 = $tbodygrid.find('input#xmlFactura_'+item);
            $tr_clickeado3.click(function(event){
                event.preventDefault();
                //alert("btn_event3");
                var llave = $(this).attr('id').split('_')[1];
		loadForm(llave,'genera_xmlFactura');	                
            });
        }
        
	$pinta_grid = function(items, $tbodygrid){                
		$tbodygrid.children().remove();
		
		for ( iterador = 0; iterador < items.length; iterador++ ){
			$id_item = 0;
			$tr = "<tr height='20px'>";
			$theadgrid.find('tr th').each(function(i){
				$item_header = $(this).find('.header__').html();
				
				if($item_header == 'id'){
					//$tr += '<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="editar_' + items[iterador][$item_header] + '" class="editar_item" title="Editar" ><span id="img_editar" class="onmouseOutEdit" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> </a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="cancelar_' + items[iterador][$item_header] + '" class="cancelar_item" title="Eliminar"><span id="img_eliminar" class="onmouseOutDelete" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></a></td>';
					//$id_item = items[iterador][$item_header];
					
                                        //NLE cambio
					//$tr += '<td><INPUT TYPE="button" classs="genera_pdf" id="pdf_'+ items[iterador][$item_header]+'" value="PDF" style="width:50px; font-weight: bold;"></td>';
                                        $tr += '<td style="width:240px;">';
                                        $tr += '<INPUT TYPE="button" classs="genera_pdf" id="pdf_'+ items[iterador][$item_header]+'" value="PDF Cobranza" style="width:80px; font-weight: bold;">&nbsp;&nbsp;';
                                        $tr += '<INPUT TYPE="button" classs="genera_pdf" id="pdfFactura_'+ items[iterador][$item_header]+'" value="PDF Factura" data-selector="'+items[iterador][$item_header]+'" style="width:70px; font-weight: bold;">&nbsp;&nbsp;';
                                        $tr += '<INPUT TYPE="button" classs="genera_pdf" id="xmlFactura_'+ items[iterador][$item_header]+'" value="XML Factura" data-selector="'+items[iterador][$item_header]+'" style="width:70px; font-weight: bold;">&nbsp;&nbsp;';
                                        $tr += '</td>';
                                        
                                        
					$id_item = items[iterador][$item_header];
				}else{
					//	$tr += '<td>'+items[iterador][$item_header]+'</td>';
					if($item_header == 'cantidad'){
						$tr += '<td align="right">'+items[iterador][$item_header]+'</td>';
					}else{
						$tr += '<td>'+items[iterador][$item_header]+'</td>';
					}
				}
				
			});
			$tr += "</tr>";
			$tbodygrid.append($tr);
			$mouseOverEditGrid($id_item,$tbodygrid);
			$clicktr($id_item,$tbodygrid);
                        $clicktr2($id_item,$tbodygrid);
                        $clicktr3($id_item,$tbodygrid);
                        //$clicktr3($id_item,$tbodygrid);
		};
		
		alternarColoresFilas(grid);
	}
	
	$pinta_grid(data['Data'],$tbodygrid);
	
	$clonarTable = function(table, page_selected){
		$grid_parent = table.parent();
		var table_to_show = 'pag';
		var $table_selected = $grid_parent.find('#' + table_to_show + $start_pag);
		$table_selected.removeAttr("id");
		$table_selected.attr({ id : table_to_show + page_selected });
		
		return $table_selected;
	}
	
	$redrawgrid = function(grid){
		
            //var input_json = document.location.protocol + '//' + document.location.host + $input_json;
            //"descripcion":$descripcion,'estatus':$estatus,'divisa':$divisa,'fechainicio':$fechainicio,'fechafin':$fechafin,'slider':$slider,'pag_display':$display_pag,
            var input_json = document.location.protocol + '//' + document.location.host + $input_json;
            //"descripcion":$descripcion,'estatus':$estatus,'divisa':$divisa,'fechainicio':$fechainicio,'fechafin':$fechafin,'slider':$slider,'pag_display':$display_pag,
            $arreglo = {'orderby':$orderby,'desc':$desc,'items_por_pag':$item_por_pag,'pag_start':$start_pag,'display_pag':$display_pag,'input_json':$input_json, 'cadena_busqueda':$cadena_busqueda, 'iu':$iu}
           
            $.post(input_json,$arreglo,function(data){

                $pinta_grid(data['Data'],$tbodygrid);

                Elastic.reset(document.getElementById('lienzo_recalculable'));

            },"json");
	}
	
        
	if(data['Data'].length > 0 ){
		//paginator
		$grid_parent = grid.parent();
		$grid_parent.find('#gridpaginator_data').paginate({
			count 		: $total_paginas,
			start 		: $start_pag,
			display     : $display_pag,
			border					: false,
			border_color			: '#888',
			text_color  			: '#888',
			background_color    	: '#EEE',	
			border_hover_color		: '#ccc',
			text_hover_color  		: '#black',
			background_hover_color	: '#CFCFCF', 
			images					: true,
			mouse					: 'press',
			onChange     			: function(page){
                            $grid_parent.find('._current','#paginationdiv').removeClass('_current').hide();

                            grid = $clonarTable(grid,page);

                            $grid_parent.find('#pag'+page).addClass('_current').show();

                            $start_pag = page;
                            $redrawgrid(grid);

                      }
		});
		
               
		//datagrid
		$(function() {
			$grid_parent.find(".tablesorter")
				.tablesorter({widthFixed: false, widgets: ['zebra']});
				//.tablesorterPager({container: $("#pager")})
		});
	}
	
};
