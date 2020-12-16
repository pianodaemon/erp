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
        <title>Login</title>
        <link href="css/login.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="js/jquery.js" charset="utf-8"></script>
        <script type="text/javascript" src="js/login.js" ></script>
        <style type="text/css">
            .Estilo1 {
                font-size: 16px;
                font-weight: bold;
            }
        </style>
    </head>
    <body>

        
        <div class="tabla">
            <form id="loginForm" name="loginForm" action="j_spring_security_check" method="post">
                
                    <table width="300" height="230" border="0" align="center">
                        <tr>
                            <td height="54" colspan="3" align="left"><span class="Estilo1">Acceso a usuarios</span></td>
                        </tr>
                        <tr>
                            <td height="25" colspan="3" align="left">Usuario:</td>
                        </tr>
                        <tr>
                            <td height="39" colspan="3" align="left">
                                <input id="usernameField" type="text" name="j_username"  size="30" style="border-width: 1px; border-style: groove; font-size:12pt; color: #8d8d8d;"/>
                            </td>
                        </tr>
                            <tr>
                                <td height="18" colspan="3" align="left">Contrase&ntilde;a</td>
                            </tr>
                        <tr>
                            <td colspan="3" align="left">
                                <input id="passwordField" type="password" name="j_password" size="30" style="border-width: 1px; border-style: groove; font-size:12pt; color: #8d8d8d;"/>
                            </td>
                        </tr>
                        <!--
                        <tr>
                            <td width="211">&nbsp;</td>
                            <td width="66">&nbsp;</td>
                            <td width="51" align="right">
                                <input type="submit" value="Login" />
                            </td>
                        </tr>
                        -->
                        <tr>
                            <td colspan="3" align="center">
                                <input type="submit" id="submit" value="Login" />
                            </td>
                        </tr>
                        
                    </table>
                
            </form>
        </div>
        
        <div class="navegadorcleinte" align="center" width="100%">
            
        </div>
        
    </body>
</html>


