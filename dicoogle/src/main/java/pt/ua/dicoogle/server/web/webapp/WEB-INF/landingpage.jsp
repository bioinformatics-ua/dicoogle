<%@page trimDirectiveWhitespaces="true"%>
<%@page import="pt.ua.dicoogle.server.web.auth.Session"%>
<%@page import="pt.ua.dicoogle.server.web.auth.LoggedInStatus"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <%@include file="jspf/header.jspf" %>

    <link href="style.css" type="text/css" rel="stylesheet" />
    
    
    <div id="app">
        <!--    <header class="header">
                
        <%--<%@include file="jspf/mainbar.jspf" %>--%>
        
    </header> -->

        <section class="container row-fluid loginbox" style="width: 750px;display: block;margin-left: auto;margin-right: auto;margin-top: 60px;background-color:rgba(0,0,0,0.2)">

            <!-- content goes here -->
            <img src="assets/icone_dicoogle_small.png" alt="Smiley face" 
                 style="display:block;margin-left: auto; margin-right: auto; margin-top: 30px">

            <div >

                <h1 align="center" style="color: white">Dicoogle</h1>

            </div>
            <div >

                <h4 align="center" style="color: white">Medical Imaging Repositories using Indexing System and P2P mechanisms</41>

            </div>
            
            

            <div style="display:block;
                 width:40%;
                 margin: 0 auto;
                 text-align: center;
                 margin-top: 40px"
                 >
                
                    
                <form action="login.jsp" method="post" class="form-horizontal">

                    <input type="hidden" name="returnURL" value="<%= Session.getLastVisitedURL(request) %>" />

<!--                    <div class="control-group">
                        <label class="control-label" for="username">Username:</label>
                        <div class="controls">
                            <input type="text" id="username" name="username" placeholder="Username" />
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label" for="password">Password:</label>
                        <div class="controls">
                            <input type="password" id="password" name="password" placeholder="Password" />
                        </div>
                    </div>
                    <div class="control-group">
                        <div class="controls">
                            <button type="submit" class="btn submit">Login</button>
                        </div>
                    </div>-->
<p style="text-align: left; width: 100%;margin-left: 5px;color: white">Sign In</p>
<input type="text" id="username" name="username" placeholder="Username" style="width: 100%;margin: 5px;margin-top: 0px" autofocus />
<input type="password" id="password" name="password" placeholder="Password" style="width: 100%; margin: 5px" />
<button type="submit" class="btn submit myButton">Login</button>
                </form>

            </div>

            <!-- /content goes here -->

        </section>

        <footer><!-- footer --></footer>    
    </div>

    <%@include file="jspf/footer.jspf" %>
</body>
</html>