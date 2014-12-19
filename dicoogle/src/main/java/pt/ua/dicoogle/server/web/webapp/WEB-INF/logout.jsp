<%@page trimDirectiveWhitespaces="true"%>
<%@page import="pt.ua.dicoogle.server.web.auth.Session"%>
<%@page import="pt.ua.dicoogle.server.web.auth.LoggedInStatus"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");

	boolean loggedOut = Session.logout(request);

	response.sendRedirect(Session.getLastVisitedURL(request));
%>