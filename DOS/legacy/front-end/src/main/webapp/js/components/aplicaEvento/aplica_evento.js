(function($){
	$.fn.extend({
		//desencadena evento del $campo_ejecutar al pulsar Enter en $campo
		aplicarEventoKeypressEjecutaTrigger : function($campo, $campo_ejecutar){
			$campo.keypress(function(e){
				if(e.which == 13){
					$campo_ejecutar.trigger('click');
					//return false;
				}
			});
		}
	});
})(jQuery);
