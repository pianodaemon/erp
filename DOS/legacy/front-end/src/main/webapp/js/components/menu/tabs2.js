$(document).ready(function(){
    //Default Action
    $(".tab_content2").hide(); //Hide all content
    $("ul.tabs2 li:first").addClass("active").show(); //Activate first tab
    $(".tab_content2:first").show(); //Show first tab content
    //On Click Event
    $("ul.tabs2 li").click(function() {
        $("ul.tabs2 li").removeClass("active"); //Remove any "active" class
        $(this).addClass("active"); //Add "active" class to selected tab
        $(".tab_content2").hide(); //Hide all tab content
        var activeTab = $(this).find("a").attr("href"); //Find the rel attribute value to identify the active tab + content
        $(activeTab).fadeIn(); //Fade in the active content
        var input_json6 = document.location.protocol + '//' + document.location.host + '/' + activeTab;
		window.location.href=input_json6;
		return false;
    });
});
