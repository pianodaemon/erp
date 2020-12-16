$(document).ready(function(){
	
	var timeout         = 500;
	var closetimer		= 0;
	var ddmenuitem      = 0;

	function jsddm_open(){
		//jsddm_canceltimer();
		jsddm_close();
		ddmenuitem = $(this).find('ul').each(function( event ){
			$(this).css({'visibility':'visible'});
			//alert($(this).parent().parent().html());
		});
		
		//alert($(this).find('ul').eq(0).html());
	}
	
	function jsddm_close(){
		if(ddmenuitem) ddmenuitem.css('visibility', 'hidden');
	}
/*
	function jsddm_timer(){	
		closetimer = window.setTimeout(jsddm_close, timeout);
	}

	function jsddm_canceltimer()
	{	if(closetimer){
			window.clearTimeout(closetimer);
			closetimer = null;
		}
	}
*/
	$(document).ready(function(){
		$('#jsddm > li').bind('mouseover', jsddm_open);
		$('#jsddm > li').bind('mouseout',  jsddm_close);}
		//$('#jsddm > li').bind('mouseout',  jsddm_timer);}
	);
	
	document.onclick = jsddm_close;
	
    /*//Default Action
    $(".tab_content").hide(); //Hide all content
    $("ul.tabs li:first").addClass("active").show(); //Activate first tab
    $(".tab_content:first").show(); //Show first tab content
    //On Click Event
    $("ul.tabs li").click(function() {
        $("ul.tabs li").removeClass("active"); //Remove any "active" class
        $(this).addClass("active"); //Add "active" class to selected tab
        $(".tab_content").hide(); //Hide all tab content
        var activeTab = $(this).find("a").attr("href"); //Find the rel attribute value to identify the active tab + content
        $(activeTab).fadeIn(); //Fade in the active content
        return false;
    });*/
});
