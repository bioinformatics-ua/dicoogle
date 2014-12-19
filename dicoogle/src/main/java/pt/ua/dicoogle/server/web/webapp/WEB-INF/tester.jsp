<%@page import="pt.ua.dicoogle.sdk.StorageInputStream"%>
<%@page import="java.net.URI"%>
<%@page import="pt.ua.dicoogle.plugins.PluginController"%>
<%@page trimDirectiveWhitespaces="true"%>
<%@page import="pt.ua.dicoogle.server.web.management.Services"%>
<%@page import="pt.ua.dicoogle.server.web.management.Dicoogle"%>
<%@page import="pt.ua.dicoogle.server.web.management.Indexer"%>
<%@page import="pt.ua.dicoogle.server.web.SettingsServlet"%>
<%@page import="pt.ua.dicoogle.server.web.IndexerServlet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
	 	<link href="styles/bootstrap.min.css" rel="stylesheet" media="screen">
	 	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta charset="utf-8">
		<title>Dicoogle Web - Management</title>
		<%@include file="jspf/header.jspf" %>
		<style>
			.running
			{
				background-color: rgb(223, 240, 216) !important;
			}

			.stopped
			{
				background-color: rgb(242, 222, 222) !important;
			}
		</style>		
		<script type="text/javascript" src="utils.js" defer="defer"></script>
		<script type="text/javascript" src="scripts/xhr.js" defer="defer"></script>
		<script type="text/javascript" src="scripts/select.js" defer="defer"></script>
		<script type="text/javascript" src="scripts/indexingStatus.js" defer="defer"></script>
		<script src="http://code.jquery.com/jquery.js"></script>
		 <script src="scripts/bootstrap.js"></script>
	</head>
	<body onload="startStatusRequestChain(true);">		
	  
		<%@include file="jspf/mainbar.jspf" %>
		<%@include file="jspf/needsLoginBegin.jspf" %>
		<%@include file="jspf/needsAdminRightsBegin.jspf" %>
	
	<div class="container">
			<%
				//PluginController.getInstance().test();
				URI location = new URI("file-bardamerdas:///C:/dev/dataset/Thyroid%20Uptake%20Syringe/Thyroid%20Uptake%20Syringe/Thyroid%20Scan%20-%201/ThyroidTc_1/IM-0001-0003.dcm");
			    Iterable<StorageInputStream> it = PluginController.getInstance().resolveURI(location);
				%>
	</div>
	
	<div id="no">
	
	</div>

	<script type="text/javascript">
		$(document).ready(function ()  
		{ $(".btn-info").popover();  
		});
		</script>
	
	
		<%@include file="jspf/needsAdminRightsEnd.jspf" %>
		<%@include file="jspf/needsLoginEnd.jspf" %>
		<%@include file="jspf/footer.jspf" %>
	</body>
</html>