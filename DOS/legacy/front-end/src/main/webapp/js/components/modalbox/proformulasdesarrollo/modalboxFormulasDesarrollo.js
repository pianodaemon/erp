(function($){
    $.fn.extend({
        modalPanel_formulasdesarrollo: function() {
            
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
            var overlay = $("<div id='forma-proformulasdesarrollo-overlay'></div>");
            //Create our modal window
            var modalWindow = $("<div id='forma-proformulasdesarrollo-window'></div>");

            if (typeof document.body.style.maxHeight === "undefined") { //if IE 6
                $("body","html").css({height: "100%", width: "100%"});
            }

            $("body").append(overlay);

            //Set the css and fade in our overlay
            //overlay.css("opacity", 0.9);
            overlay.show();

            //modalWindow.css("opacity", 1.0);
            $("#forma-proformulasdesarrollo-overlay").append(modalWindow);
            //modalWindow.fadeIn(50);
        }
    });
    
})(jQuery);
