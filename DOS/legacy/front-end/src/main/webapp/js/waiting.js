$(function(){
        
	$.blockUI.defaults.message='Procesando...';
	$(document).ajaxStart($.blockUI).ajaxStop($.unblockUI);
        /*
	$('body').ajaxStart(function() {
            
            
		var docHeight = $(document).height();		
		var $overlay = $("<div id='overlay-for-body'></div>");

		//Append the overlay to the document body
		$("body").append($overlay);

		$overlay.height(docHeight)
                        .css({
                                 'opacity' : 0.8,
                                 'position': 'absolute',
                                 'top': 0,
                                 'left': 0,
                                 'background-color': 'white',
                                 'width': '100%',
                                 'z-index': 1000
                });

		//Add a loader to our page
		$("body").append("<div id='ajax-load'></div>");
				

	}).ajaxStop(function() {
		$(this).find('#ajax-load').fadeOut().remove();
		$(this).find('#overlay-for-body').fadeOut().remove();
	});
        */
});
