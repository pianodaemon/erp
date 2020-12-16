(function($){
    $.fn.extend({
        modalPanel_ProConfigProduccion: function() {
            
            //Create our overlay object
            var overlay = $("<div id='forma-proconfigproduccion-overlay'></div>");
            //Create our modal window
            var modalWindow = $("<div id='forma-proconfigproduccion-window'></div>");


            if (typeof document.body.style.maxHeight === "undefined") { //if IE 6
                    $("body","html").css({height: "100%", width: "100%"});
            }
            
            
            $("body").append(overlay);

            overlay.show();


            $("#forma-proconfigproduccion-overlay").append(modalWindow);
            //modalWindow.fadeIn(50);
        }
    });
})(jQuery);
