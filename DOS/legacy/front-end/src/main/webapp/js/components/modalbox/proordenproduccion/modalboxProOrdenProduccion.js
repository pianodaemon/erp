(function($){
    $.fn.extend({
        modalPanel_ProOrdenProduccion: function() {
            
            //Create our overlay object
            var overlay = $("<div id='forma-proordenproduccion-overlay'></div>");
            //Create our modal window
            var modalWindow = $("<div id='forma-proordenproduccion-window'></div>");
            
            
            if (typeof document.body.style.maxHeight === "undefined") { //if IE 6
                    $("body","html").css({height: "100%", width: "100%"});
            }
            
            
            $("body").append(overlay);
            
            overlay.show();
            
            $("#forma-proordenproduccion-overlay").append(modalWindow);
            //modalWindow.fadeIn(50);
        }
    });
})(jQuery);
