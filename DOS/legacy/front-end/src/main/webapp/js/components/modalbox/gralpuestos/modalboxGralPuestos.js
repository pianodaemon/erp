(function($){
	$.fn.extend({
		modalPanel_GralPuestos: function() {
		
			
			//Create our overlay object
			var overlay = $("<div id='forma-gralpuestos-overlay'></div>");
			//Create our modal window
			var modalWindow = $("<div id='forma-gralpuestos-window'></div>");
			
                            //Listen for clicks on objects passed to the plugin
					
                            if (typeof document.body.style.maxHeight === "undefined") { //if IE 6
                                    $("body","html").css({height: "100%", width: "100%"});
                            }

                            //Append the overlay to the document body
                            $("body").append(overlay);

                            //Add a loader to our page
                            overlay.show();

                            //Activate a listener 
                            $("#forma-gralpuestos-overlay").append(modalWindow);
					
		}
	});
})(jQuery);
