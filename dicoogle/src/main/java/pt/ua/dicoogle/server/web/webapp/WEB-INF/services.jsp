<%@page trimDirectiveWhitespaces="true"%>
<%@page import="pt.ua.dicoogle.server.web.management.Services"%>
<%@page import="pt.ua.dicoogle.server.web.auth.Session"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Dicoogle Web - Service and Plugin Broker</title>
	</head>
	<body>
		<%
			int result = Services.getInstance().processWebappRequest(request, response);

			if (result == Services.RES_OK)
			{
				// send	the client back the to previous page
				response.sendRedirect(Session.getLastVisitedURL(request));
			}
			else
			{
				// TODO parse the error into a more descriptive way with a switch-case
		%>
		<h1>
			An error (code: <%= result %>) has occurred!
		</h1>
		<%
				// TODO also add an "Go Back" link after the error (like the login page).
			}
		%>
	</body>
</html>