<%-- 
    Document   : index
    Created on : 1/11/2011, 03:49:35 PM
    Author     : marsan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error de Autenticaci&oacute;n</title>
        <style type="text/css">
            root { 
                display: block;
            }
            a:link {
                text-decoration: none;
                color: #ffcc33;
                font-family: monospace;
            }
            body{
              background: #8d8d8d;
            }

            #mensaje{
                background: transparent;
                border-radius: 0.5 em;
                margin: 99px 10px 120px 350px;
                padding: 10px ;
                text-align: center;
                width: 550px;
            }
            .button{
                background: #555555;
                border-radius: 80px;
                font-size: 30px;
            }
            .formato{
                font-family: fantasy;
                font-size: 45px;
                color: #ffcc33;
                text-shadow: black 0.1em 0.1em 0.2em
            }
            .fuente{
                font-family: fantasy;
                font-size: 20px;
                color: #ffcc33;
                text-shadow: black 0.1em 0.1em 0.2em
            }
        </style>
    </head>
    <body>
        
        <div id="fondo">
            <div id="mensaje">
                <table border="0" width="100%" >
                    <tr>
                        <td>
                            <img src="img/logo_erp.png" width="60%"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p class="formato">Error de autenticaci&oacute;n</p><br>
                            <p class="fuente">Vuelva a intentar con otro usuario</p>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <a href="javascript:history.back()" class="button">&nbsp;&nbsp;&nbsp;&nbsp;Regresar&nbsp;&nbsp;&nbsp;&nbsp;</a>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </body>
</html>
