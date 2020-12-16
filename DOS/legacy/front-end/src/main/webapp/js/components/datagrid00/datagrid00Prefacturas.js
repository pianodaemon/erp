$.fn.tablaOrdenablePrefacturas = function(data,grid,loadForm){
    
    //
	var $theadgrid = grid.find('thead');
	var $tbodygrid = grid.find('tbody');
	
	var $item_por_pag = data['DataForGrid'][0]['items_por_pag'];
	var $total_items = data['DataForGrid'][0]['total_items'];
	var $total_paginas = data['DataForGrid'][0]['total_paginas'];
	var $start_pag = data['DataForGrid'][0]['pag_start'];
	var $display_pag = data['DataForGrid'][0]['display_pag'];
//	var $pintargrid = data['DatForControll'][0]['pintargrid'];
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
		$tr_clickeado = $tbodygrid.find('a[href="editar_'+item+'"],a[href="cancelar_'+item+'"]');
		var cont = 0;
		$tr_clickeado.click(function(event){
			event.preventDefault();
			$apuntador_tr = $(this).parent().parent();
			if ( $(this).is('.editar_item') ){
				var llave = $(this).attr('href').split('_')[1];
				//$(this).modalPanel();
				loadForm(llave,'edit');	
				//$redrawgrid($tbodygrid);
			};
			if ( $(this).is('.cancelar_item') ){
				var llave = $(this).attr('href').split('_')[1];
				//$(this).modalPanel();
				
				loadForm(llave,'cancel');
				//$redrawgrid($tbodygrid);
			};
			Elastic.reset(document.getElementById('lienzo_recalculable'));
		});
	}
	
	$pinta_grid = function(items, $tbodygrid){
		$tbodygrid.children().remove();
		
		for ( iterador = 0; iterador < items.length; iterador++ ){
			$id_item = 0;
			$tr = "<tr height='20px'>";
			$theadgrid.find('tr th').each(function(i){
				$item_header = $(this).find('.header__').html();
				//<img src="../img/datagrid00/editar1.png" border=0 id="img_grid" >
				//<img src="../img/datagrid00/borrar1.png" border=0 id="img_grid" title="Eliminar">
				if($item_header == 'id'){
					//$tr += '<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="editar_' + items[iterador][$item_header] + '" class="editar_item" title="Editar" ><span id="img_editar" class="onmouseOutEdit" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> </a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="cancelar_' + items[iterador][$item_header] + '" class="cancelar_item" title="Eliminar"><span id="img_eliminar" class="onmouseOutDelete" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></a></td>';
					$tr += '<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="editar_' + items[iterador][$item_header] + '" class="editar_item" title="Editar" ><span id="img_editar" class="onmouseOutEdit" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> </a></td>';
					$id_item = items[iterador][$item_header];
				}else{
					if($item_header == 'total'){
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
		
            var input_json = document.location.protocol + '//' + document.location.host + $input_json;
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
			//background_hover_color	: '#CFCFCF', 
			//background_hover_color	: '#969696', 
			background_hover_color	: '#2E2E2E', 
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
