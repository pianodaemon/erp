$(function() {
    var $nav_version = $(this).find('.navegadorcleinte');
    var $username = $(this).find("input#usernameField");
    var $password = $(this).find("input#passwordField");
    
    var $submit_actualizar = $(this).find('#submit');
    var nom= navigator.userAgent.match(/Firefox/i)
    /*
    if(nom != 'Firefox'){
        $nav_version.html('<div class="error_box">Navegador no soportado. Instale Firefox</div>');
        $submit_actualizar.bind('click',function(){
            if(!confirm("El navegador no es soportado. Inicie sesión en Navegador Firefox")) 
            {
                return false;
            }else{
                return false;
            }
        });
    }
    */
   
    $(this).click(function(event){
        if( !$(this).find("input").is(":focus") ){
            if($username.val() == ''){
                $username.focus();
            }else{
                $password.focus();
            }
            
        }
    });
    
    
   $username.focus();
   
});
