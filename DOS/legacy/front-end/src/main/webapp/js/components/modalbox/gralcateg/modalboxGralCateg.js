(function($){
	$.fn.extend({
		modalPanel_GralCateg: function() {                        
			//Create our overlay object
			var overlay = $("<div id='forma-gralcateg-overlay'></div>");
			//Create our modal window
			var modalWindow = $("<div id='forma-gralcateg-window'></div>");
					if (typeof document.body.style.maxHeight === "undefined") { //if IE 6
						$("body","html").css({height: "100%", width: "100%"});
					}

					$("body").append(overlay);

					overlay.show();
					$("#forma-gralcateg-overlay").append(modalWindow);
		}
	});
})(jQuery);
