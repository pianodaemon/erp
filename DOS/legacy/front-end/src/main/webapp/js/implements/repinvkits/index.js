$(function() {
          var config =  {
                    empresa:$('#lienzo_recalculable').find('input[name=emp]').val(),
                    sucursal:$('#lienzo_recalculable').find('input[name=suc]').val(),
                    tituloApp: 'Ventas Netas por Cliente' ,                 
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
                    return this.contextpath + "/controllers/repinvkits";
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
	
  
			var $codigo = $('#lienzo_recalculable').find('input[name=codigo]');
               var $descripcion = $('#lienzo_recalculable').find('input[name=descripcion]');
			//var $fecha_final = $('#lienzo_recalculable').find('input[name=fecha_final]');
			var $genera_reporte_kits_que_se_pueden_formar = $('#lienzo_recalculable').find('div.repkits').find('table#filtros tr td').find('input[value$=Generar_PDF]');
			var $Buscar_numero_de_kits= $('#lienzo_recalculable').find('div.repkits').find('table#filtros tr td').find('input[value$=Buscar]');
			var $kits_que_se_pueden_formar= $('#kits_que_se_pueden_formar');
               var $cod=$codigo.val();
               var $desc=$descripcion.val();
			//click generar reporte de pronostico de Cobranza
               $genera_reporte_kits_que_se_pueden_formar.click(function(event){
                    event.preventDefault();
                    
                    
                    var input_json = config.getUrlForGetAndPost()+'/PDFgetKits/'+config.getUi()+'/out.json';
                        window.location.href=input_json;
//                    if($cod != "" && $desc !=""){
//                         $cod=$codigo.val();
//                         $desc=$descripcion.val();
//                         var input_json = config.getUrlForGetAndPost() + '/PDFgetKits/'+$cod+'/'+$desc+'/'+config.getUi()+'/out.json';
//                        window.location.href=input_json;
//                    }else{
//                         if($cod != ""){
//                              $desc=$descripcion.val(0);
//                              var input_json = config.getUrlForGetAndPost() + '/PDFgetKits/'+$cod+'/'+$desc+'/'+config.getUi()+'/out.json';
//                              window.location.href=input_json;
//                         }
//                         if($desc != ""){
//                              $cod=$codigo.val(0);
//                              var input_json = config.getUrlForGetAndPost() + '/PDFgetKits/'+$cod+'/'+$desc+'/'+config.getUi()+'/out.json';
//                              window.location.href=input_json;
//                         }
                         
                         
                    
                    
//                    
//                    if($cod != "" && $desc !=""){
//                        
//var input_json = config.getUrlForGetAndPost()+'/PDFgetKits/'+config.getUi()+'/out.json';
//window.location.href=input_json;
//                    }else{
//                        alert("Esta entrando aqui cuando ponen algo en codigo o descvripcionj");
//
//                       var input_json = config.getUrlForGetAndPost() + '/PDFgetKits/'+$cod+'/'+$desc+'/'+config.getUi()+'/out.json';
//                        window.location.href=input_json;
//                         
//                    }
                    
                    
               });
		
               lista_de_kits_que_pueden_formar = function($cod,$desc){

                    var arreglo_parametros = {codigo:$cod,descripcion:$desc,iu:config.getUi()};
                    var restful_json_service = config.getUrlForGetAndPost() + '/getKitsVista.json'
                    var contador =0;
                    $.post(restful_json_service,arreglo_parametros,function(entry){
                              var body_tabla = entry;

                              var header_tabla = {
                                   codigo:'CODIGO',
                                   producto_kit  :'PRODUCTO KIT',
                                   cantidad    :"NUMERO KITS"
                              };

                              var html_numero_kits = '<table id="numero_kits" width="100%">';
                              var porcentaje = 0.0;
                              var numero_control=0.0; 
                              var cliente=0.0; 
                              var moneda="$"; 
                              var venta_neta=0.0; 
                              var porciento=0.0;
                              var tmp= 0;

                              html_numero_kits +='<thead> <tr>';

                                   for(var key in header_tabla){
                                        var attrValue = header_tabla[key];
                                        if(attrValue == "CODIGO"){
                                        html_numero_kits +='<td  align="left"  >&nbsp;&nbsp;'+attrValue+'</td>'; 
                                        }
                                        if(attrValue == "PRODUCTO KIT"){
                                        html_numero_kits +='<td  align="left"  >'+attrValue+'</td>'; 
                                        }
                                        if(attrValue == "NUMERO KITS"){
                                        html_numero_kits +='<td  align="right"  >'+attrValue+'</td>'; 
                                        }
                                   }

                              html_numero_kits +='</tr> </thead>';

                              for(var i=0; i<body_tabla.length; i++){
                                   TPventa_neta=body_tabla[i]["Tventa_neta"]; 
                                   html_numero_kits +='<tr>';
                                        html_numero_kits +='<td align="left"  >'+body_tabla[i]["codigo"]+'</td>'; 
                                        html_numero_kits +='<td align="left"  >'+body_tabla[i]["producto_kit"]+'</td>'; 
                                        html_numero_kits +='<td align="right"  >'+body_tabla[i]["cantidad_de_kits"]+'</td>'; 
                                   html_numero_kits +='</tr>';
                                   contador=contador+1;
                              }

								/*
                              html_numero_kits +='<tfoot>';
                                   html_numero_kits +='<tr>';
                                        html_numero_kits +='<td align="right" colspan="2"  >'+" Numero de Kist: "+'</td>'; 
                                        html_numero_kits +='<td  align="right" >'+contador+'</td>'; 
                                   html_numero_kits +='</tr>';
                              html_numero_kits +='</tfoot>';
                              */
                              //html_numero_kits += '</table>';

                              html_numero_kits += '</table>';
								
                              $kits_que_se_pueden_formar.append(html_numero_kits); 
                              var height2 = $('#cuerpo').css('height');
                              var alto = parseInt(height2)-275;
                              var pix_alto=alto+'px';


                              $('#numero_kits').tableScroll({height:parseInt(pix_alto)});

                         });

               }
				
				
               $Buscar_numero_de_kits.click(function(event){
                    event.preventDefault();
                    $kits_que_se_pueden_formar.children().remove();
                     $cod=$codigo.val();
                     $desc=$descripcion.val();
                     
                    lista_de_kits_que_pueden_formar($cod,$desc );
               });            
             
});   
        
        
        
        
    
