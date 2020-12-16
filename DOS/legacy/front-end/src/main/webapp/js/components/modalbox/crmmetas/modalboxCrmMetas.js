(function($){
	$.fn.extend({
		modalPanel_CrmMetas: function() {
                        
                        
			
			//Create our overlay object
			var overlay = $("<div id='forma-metas-overlay'></div>");
			//Create our modal window
			var modalWindow = $("<div id='forma-metas-window'></div>");
			
                            //Listen for clicks on objects passed to the plugin
					
                            if (typeof document.body.style.maxHeight === "undefined") { //if IE 6
                                    $("body","html").css({height: "100%", width: "100%"});
                            }

                            //Append the overlay to the document body
                            $("body").append(overlay);

                            //Add a loader to our page
                            overlay.show();

                            //Activate a listener 
                            $("#forma-metas-overlay").append(modalWindow);
                            
                            //Our function for hiding the modalbox
			function modalHide() {
				$(document).unbind("keydown", handleEscape);
				var remove = function() { $(this).remove(); };
				overlay.fadeOut(remove);
				modalWindow
					.fadeOut(remove)
					.empty();
			}
			
			//Our function that listens for escape key.
			function handleEscape(e) {
				if (e.keyCode == 27) {
					modalHide();
				}
			}
					
		}
	});
})(jQuery);
