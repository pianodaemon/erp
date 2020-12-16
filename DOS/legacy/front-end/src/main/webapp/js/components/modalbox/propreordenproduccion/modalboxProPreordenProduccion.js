(function($){
    $.fn.extend({
        modalPanel_ProPreordenProduccion: function() {
            
            //Create our overlay object
            var overlay = $("<div id='forma-propreordenproduccion-overlay'></div>");
            //Create our modal window
            var modalWindow = $("<div id='forma-propreordenproduccion-window'></div>");
            
            
            if (typeof document.body.style.maxHeight === "undefined") { //if IE 6
                    $("body","html").css({height: "100%", width: "100%"});
            }
            
            
            $("body").append(overlay);
            
            overlay.show();
            
            $("#forma-propreordenproduccion-overlay").append(modalWindow);
            //modalWindow.fadeIn(50);
        }
    });
})(jQuery);
