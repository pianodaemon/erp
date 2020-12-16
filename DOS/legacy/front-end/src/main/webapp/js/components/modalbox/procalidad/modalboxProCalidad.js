(function($){
    $.fn.extend({
        modalPanel_procalidad: function() {
            
            //Create our overlay object
            var overlay = $("<div id='forma-procalidad-overlay'></div>");
            //Create our modal window
            var modalWindow = $("<div id='forma-procalidad-window'></div>");
            
            
            if (typeof document.body.style.maxHeight === "undefined") { //if IE 6
                    $("body","html").css({height: "100%", width: "100%"});
            }
            
            
            $("body").append(overlay);
            
            overlay.show();
            
            $("#forma-procalidad-overlay").append(modalWindow);
            //modalWindow.fadeIn(50);
        }
    });
})(jQuery);
