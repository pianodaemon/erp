(function($){
	$.fn.extend({
		modalPanel_adicionalEmergentes: function() {
		    
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
                                    //modalHide();
                            }
			}
			
			//Create our overlay object
			var overlay = $("<div id='forma-adicionalemergentes-overlay'></div>");
			//Create our modal window
			var modalWindow = $("<div id='forma-adicionalemergentes-window'></div>");
			
					if (typeof document.body.style.maxHeight === "undefined") { //if IE 6
						$("body","html").css({height: "100%", width: "100%"});
					}
					

					$("body").append(overlay);
					
					//Set the css and fade in our overlay
					//overlay.css("opacity", 0.9);
					overlay.show();
					
					
					//Activate a listener 
					$(document).keydown(handleEscape);	

					
					$("#forma-adicionalemergentes-overlay").append(modalWindow);
					
		}
	});
})(jQuery);
