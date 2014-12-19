<%@page trimDirectiveWhitespaces="true"%>
<%@page import="pt.ua.dicoogle.server.web.management.Services"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta charset="utf-8">
		<title>Dicoogle Web - Advanced Settings</title>
		<%@include file="jspf/header.jspf" %>
		<script type="text/javascript" src="utils.js" defer="defer"></script>
		<script type="text/javascript" src="scripts/xhr.js" defer="defer"></script>
		<script type="text/javascript" src="scripts/select.js" defer="defer"></script>
	</head>
	<body>
		<%@include file="jspf/mainbar.jspf" %>
		<%@include file="jspf/needsLoginBegin.jspf" %>
		<%@include file="jspf/needsAdminRightsBegin.jspf" %>
		<div class="container-fluid">
			<h1><%= StringEscapeUtils.escapeHtml4(Services.getInstance().getRequestServiceName(request)) %> - Advanced Settings:</h1>
			<%= Services.getInstance().getHTMLServiceAdvancedSettingsForm(request, "services.jsp", "settings") %>
		</div>
		<%@include file="jspf/needsAdminRightsEnd.jspf" %>
		<%@include file="jspf/needsLoginEnd.jspf" %>
		<%@include file="jspf/footer.jspf" %>
	</body>
</html>