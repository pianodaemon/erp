$(function() {
	var config =  {
		empresa:$('#lienzo_recalculable').find('input[name=emp]').val(),
		sucursal:$('#lienzo_recalculable').find('input[name=suc]').val(),
		tituloApp: 'Art&iacute;culos Reservados' ,                 
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
		return this.contextpath + "/controllers/reparticulosreservados";
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

	
	var $folio_pedido = $('#lienzo_recalculable').find('input[name=folio_pedido]');
	var $codigo = $('#lienzo_recalculable').find('input[name=codigo]');
	var $descripcion = $('#lienzo_recalculable').find('input[name=descripcion]');
	var $genera_reporte_articulos_resevados = $('#lienzo_recalculable').find('div.articulosreservados').find('table#filtros tr td').find('input[value$=Generar_PDF]');
	var $Buscar= $('#lienzo_recalculable').find('div.articulosreservados').find('table#filtros tr td').find('input[value$=Buscar]');
	var $articulos_reservados= $('#articulos_reservados');

	
	//click generar reporte de pronostico de Cobranza
	$genera_reporte_articulos_resevados.click(function(event){
		event.preventDefault();
		
		var folio='0'
		var cod='0';
		var desc='0';
		
		if($folio_pedido.val()!=''){
			folio=$folio_pedido.val();
		}
		if($codigo.val()!=''){
			cod=$codigo.val();
		}
		if($descripcion.val()!=''){
			desc=$descripcion.val();
		}
		
		var input_json = config.getUrlForGetAndPost()+'/getallArticulosReservados/'+folio+'/'+cod+'/'+desc+'/'+config.getUi()+'/out.json';
		window.location.href=input_json;
	});
		
   articulos_reservados = function($folio,$cod,$desc){
		var arreglo_parametros = {
			folio:$folio,
			codigo:$cod,
			descripcion:$desc,
			iu:config.getUi()
		};
		var restful_json_service = config.getUrlForGetAndPost() + '/getallArticulosReservados.json'
		var contador =0;
		$.post(restful_json_service,arreglo_parametros,function(entry){
				  var body_tabla = entry;
				  var header_tabla = {
					   num_pedido   :'NO. PEDIDO',
					   fecha_pedido :'FECHA',
					   cliente      :"CLIENTE",
					   cantidad     :"CANTIDAD",
					   moneda     :"",
					   precio_venta :"P.UNITARIO",
					   moneda_importe     :"",
					   importe      :"IMPORTE"
				  };
					
				  var html_articulosreservados = '<table id="reservados" width="100%">';
				  var porcentaje = 0.0;
				  var numero_control=0.0; 
				  var cliente=0.0; 
				  var moneda="$"; 
				  var venta_neta=0.0; 
				  var porciento=0.0;
				  var tmp= 0;
				  var cantidad=0;
				  var importe=0;
				  var Producto="";
				  var tmp=0;
				  var gral_importe=0;
				  html_articulosreservados +='<thead> <tr>';

					   for(var key in header_tabla){
							var attrValue = header_tabla[key];
							if(attrValue == "NO. PEDIDO"){
							html_articulosreservados +='<td  align="center"  width="90">&nbsp;&nbsp;'+attrValue+'</td>'; 
							}
							if(attrValue == "FECHA"){
							html_articulosreservados +='<td  align="center" width="67px"  >'+attrValue+'</td>'; 
							}
							if(attrValue == "CLIENTE"){
							html_articulosreservados +='<td  align="left"  width="280px" >'+attrValue+'</td>'; 
							}
							if(attrValue == "CANTIDAD"){
							html_articulosreservados +='<td  align="center"  >'+attrValue+'</td>'; 
							}
						   
							
							if(attrValue == "P.UNITARIO"){
							html_articulosreservados +='<td  align="center" colspan="2" >'+attrValue+'</td>'; 
							}
							
							if(attrValue == "IMPORTE"){
							html_articulosreservados +='<td  align="center" colspan="2"   >'+attrValue+'</td>'; 
							}
					   }

				  html_articulosreservados +='</tr> </thead>';

				  for(var i=0; i<body_tabla.length; i++){
					   if(Producto != body_tabla[i]["descripcion"] ){
							 if (tmp == 0){
								 html_articulosreservados +='<tr>';
								 html_articulosreservados +='<td align="left" >Producto:</td>'; 
								 html_articulosreservados +='<td align="left" colspan ="7"><strong>'+body_tabla[i]["sku"]+'</strong>&nbsp;&nbsp;&nbsp;&nbsp;'+body_tabla[i]["descripcion"]+'</td>'; 
								 html_articulosreservados +='</tr>'; 

								 html_articulosreservados +='<tr>';
								 html_articulosreservados +='<td align="center" width="70px" >'+body_tabla[i]["pedido"]+'</td>'; 
								 html_articulosreservados +='<td align="center" width="50px" >'+body_tabla[i]["fecha"]+'</td>'; 
								 html_articulosreservados +='<td align="left" width="400px">'+body_tabla[i]["cliente"]+'</td>'; 
								 html_articulosreservados +='<td align="right"  width="100px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]))+'</td>'; 
								 html_articulosreservados +='<td  align="right" width="5px">'+"$"+'</td>';
								 html_articulosreservados +='<td align="right"  width="100px">'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>'; 
								 html_articulosreservados +='<td  align="right" width="5px" >'+"$"+'</td>';
								 html_articulosreservados +='<td align="right" width="100px" >'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe"]).toFixed(2))+'</td>'; 
								 html_articulosreservados +='</tr>';
							}
							if(tmp !=0){
								 html_articulosreservados +='<tr>';
								 html_articulosreservados +='<td align="right" colspan="3"  >'+" Total : "+'</td>'; 
								 html_articulosreservados +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad))+'</td>'; 
								 html_articulosreservados +='<td  align="right" ></td>'; 
								 html_articulosreservados +='<td  align="right" ></td>';
								 html_articulosreservados +='<td align="right"  >'+"$"+'</td>'; 
								 html_articulosreservados +='<td  align="right" >'+$(this).agregar_comas(parseFloat(importe).toFixed(2))+'</td>'; 
								 html_articulosreservados +='</tr>';
								 
								 cantidad=0;
								 importe=0;
								 
								 html_articulosreservados +='<tr>';
								 html_articulosreservados +='<td align="left" >Producto:</td>'; 
								 html_articulosreservados +='<td align="left" colspan ="7"><strong>'+body_tabla[i]["sku"]+'</strong>&nbsp;&nbsp;&nbsp;&nbsp;'+body_tabla[i]["descripcion"]+'</td>'; 
								 html_articulosreservados +='</tr>'; 

								 html_articulosreservados +='<tr>';
								 html_articulosreservados +='<td align="center"  >'+body_tabla[i]["pedido"]+'</td>'; 
								 html_articulosreservados +='<td align="center"  >'+body_tabla[i]["fecha"]+'</td>'; 
								 html_articulosreservados +='<td align="left"  >'+body_tabla[i]["cliente"]+'</td>'; 
								 html_articulosreservados +='<td align="right"  >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]))+'</td>'; 
								 html_articulosreservados +='<td align="right"  >'+"$"+'</td>'; 
								 html_articulosreservados +='<td align="right"  >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>'; 
								 html_articulosreservados +='<td align="right"  >'+"$"+'</td>'; 
								 html_articulosreservados +='<td align="right"  >'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe"]).toFixed(2))+'</td>'; 
								 html_articulosreservados +='</tr>';
								 
							}
							tmp=1;
							Producto=body_tabla[i]["descripcion"];
							cantidad=cantidad+  parseFloat(body_tabla[i]["cantidad"]);
							importe =importe +  parseFloat(body_tabla[i]["importe"]);
							gral_importe=gral_importe+  parseFloat(body_tabla[i]["importe"]);
							
					   }else{
							html_articulosreservados +='<tr>';
							html_articulosreservados +='<td align="center"  >'+body_tabla[i]["pedido"]+'</td>'; 
							html_articulosreservados +='<td align="center"  >'+body_tabla[i]["fecha"]+'</td>'; 
							html_articulosreservados +='<td align="left"  >'+body_tabla[i]["cliente"]+'</td>'; 
							html_articulosreservados +='<td align="right"  >'+$(this).agregar_comas(parseFloat(body_tabla[i]["cantidad"]))+'</td>'; 
							html_articulosreservados +='<td align="right"  >'+"$"+'</td>'; 
							html_articulosreservados +='<td align="right"  >'+$(this).agregar_comas(parseFloat(body_tabla[i]["precio_unitario"]).toFixed(2))+'</td>'; 
							html_articulosreservados +='<td align="right"  >'+"$"+'</td>'; 
							html_articulosreservados +='<td align="right"  >'+$(this).agregar_comas(parseFloat(body_tabla[i]["importe"]).toFixed(2))+'</td>'; 
							html_articulosreservados +='</tr>'; 
							
							cantidad=cantidad+  parseFloat(body_tabla[i]["cantidad"]);
							importe =importe +  parseFloat(body_tabla[i]["importe"]);
							gral_importe=gral_importe+  parseFloat(body_tabla[i]["importe"]);
					   }
				  }
				  html_articulosreservados +='<tr>';
				  html_articulosreservados +='<td align="right" colspan="3"  >'+" Total : "+'</td>'; 
				  html_articulosreservados +='<td  align="right" >'+$(this).agregar_comas(parseFloat(cantidad))+'</td>'; 
				  html_articulosreservados +='<td  align="right" colspan="2"></td>'; 
				  html_articulosreservados +='<td align="right"  >'+"$"+'</td>'; 
				  html_articulosreservados +='<td  align="right" >'+$(this).agregar_comas(parseFloat(importe).toFixed(2))+'</td>'; 
				  html_articulosreservados +='</tr>';
				  
				  html_articulosreservados +='<tfoot>';
					   html_articulosreservados +='<tr>';
							html_articulosreservados +='<td align="right" colspan="7">'+" Total General : "+'</td>'; 
							html_articulosreservados +='<td  align="right" >'+$(this).agregar_comas(parseFloat(gral_importe).toFixed(2))+'</td>'; 
					   html_articulosreservados +='</tr>';
				  html_articulosreservados +='</tfoot>';
				  
				  html_articulosreservados += '</table>';
					
				  $articulos_reservados.append(html_articulosreservados); 
				  
				  var height2 = $('#cuerpo').css('height');
				  var alto = parseInt(height2)-275;
				  var pix_alto=alto+'px';
				  $('#reservados').tableScroll({height:parseInt(pix_alto)});
			 });

   }
   
   	
	$Buscar.click(function(event){
		event.preventDefault();
		$articulos_reservados.children().remove();
		var $folio=$folio_pedido.val();
		var $cod=$codigo.val();
		var $desc=$descripcion.val();
		articulos_reservados($folio,$cod,$desc );
	});
	
	
	$(this).aplicarEventoKeypressEjecutaTrigger($folio_pedido, $Buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($codigo, $Buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($descripcion, $Buscar);
	
	$folio_pedido.focus();
	
});
        
        
        
        
    
