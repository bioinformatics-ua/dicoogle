<%@page trimDirectiveWhitespaces="true"%>
<%@page import="pt.ua.dicoogle.server.web.auth.Session"%>
<%@page import="pt.ua.dicoogle.server.web.auth.LoggedInStatus"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");

	LoggedInStatus login = Session.webappLogin(request, response, false);

	if (login.loggedInSuccessfully() || login.wasAlreadyLoggedIn())
	{
		String returnURL = request.getParameter("returnURL");

		if (returnURL != null)
			response.sendRedirect(returnURL);
		else
			response.sendRedirect("/index.jsp");
	}
	else
	{
%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Dicoogle Web - Login</title>
		<%@include file="jspf/header.jspf" %>
	</head>
	<body>
		<%@include file="jspf/mainbar.jspf" %>
		<div class="container-fluid">
			<%
				if (login.invalidCredentialsSupplied())
				{
			%>
			<h1>Incorrect user name and/or password.</h1>
			<%
				}
			%>
			<form action="login.jsp" method="post" class="form-horizontal">
				<input type="hidden" name="returnURL" value="<%= Session.getLastVisitedURL(request) %>" />

				<div class="control-group">
					<label class="control-label" for="username">Username:</label>
					<div class="controls">
						<input type="text" id="username" name="username" placeholder="Username" autofocus/>
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
				</div>
			</form>
		</div>
		<%@include file="jspf/footer.jspf" %>
	</body>
</html>
<%
	}
%>