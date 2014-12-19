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
	<div class="tabbable tabs-right">
		<ul class="nav nav-tabs">
			<li class="active"><a href="#tab1" data-toggle="tab">Indexer</a></li>
			<li><a href="#tab3" data-toggle="tab">Transference Options</a></li>
			<li><a href="#tab4" data-toggle="tab">Services and Plugins</a></li>
			<li><a href="#tab5" data-toggle="tab">Storage Servers</a></li>
		</ul>
		<div class="tab-content">
			<div class="tab-pane active" id="tab1">				 
					<div class="control-group">
						<h4>Indexing Status:</h4>
						<div class="controls">
							<form action="indexer" method="get">
								<input type="hidden" id="indexingAction" name="action" value="<%= Indexer.getInstance().isIndexing() ? IndexerServlet.ACTION_STOP_INDEXING : IndexerServlet.ACTION_START_INDEXING %>" />
								<div class="input-append">
									<div id="indexingProgressBar" class="progress progress-striped span4 <%= Indexer.getInstance().isIndexing() ? "active" : "" %>" style="border-radius: 4px 0px 0px 4px; display: inline-block; position: relative; margin-bottom: 0px; margin-right: 1px; margin-left: 0px; height: 30px; max-width: 80%; min-width: 20%;">
										<div class="bar" id="indexingProgress" style="line-height: 30px; vertical-align: middle; width: <%= Indexer.getInstance().indexingPercentCompleted() %>%;"><%= Indexer.getInstance().isIndexing() ? Indexer.getInstance().indexingPercentCompleted() + "%" : "" %></div>
									</div>
									<button type="submit" id="indexingActionButton" class="btn <%= Indexer.getInstance().isIndexing() ? "btn-danger" : "btn-success" %>">
										<i class="<%= Indexer.getInstance().isIndexing() ? "icon-stop" : "icon-play" %>"></i> <%= Indexer.getInstance().isIndexing() ? "Stop" : "Start" %>
									</button>
								</div>
							</form>
						</div>
						<script>
							var act = document.getElementById("indexingAction");
							var bar = document.getElementById("indexingProgressBar");
							var pgs = document.getElementById("indexingProgress");
							var btn = document.getElementById("indexingActionButton");

							function recievedCurrentindexingStatus(isIndexing, progressPercentage)
							{
								// set the propper label and class for the button and the progress bar accordingly to the status and progress of the indexing status reponse
								if (isIndexing)
								{
									// make progress bar active
									addClass(bar, "active");
									// set its percentage
									pgs.style.width = progressPercentage + "%";

									// set the action to stop
									act.value = "<%= IndexerServlet.ACTION_STOP_INDEXING %>";

									// transform the button into a stop one
									removeClass(btn, "btn-success");
									addClass(btn, "btn-danger");
									btn.innerHTML = "<i class='icon-stop'></i> Stop";
								}
								else
								{
									// set the percentage to completed
									pgs.style.width = "100%";
									// make progress bar inactive
									removeClass(bar, "active");

									// set the action to start
									act.value = "<%= IndexerServlet.ACTION_START_INDEXING %>";

									// transform the button into a start one
									removeClass(btn, "btn-danger");
									addClass(btn, "btn-success");
									btn.innerHTML = "<i class='icon-play'></i> Start";
								}

								// add a textual representation of the progress to the progress bar
								pgs.textContent = pgs.style.width;
							}
						</script>
					</div>
					<div class="control-group">
						<h4>Indexing Options:</h4>
						<div class="controls">
							<%= SettingsServlet.getHTMLIndexingAdvancedSettingsForm(request, "settings", null) %>
						</div>
					</div>
			</div>			
			<div class="tab-pane" id="tab3">
						<h4>SOP Class Global Transfer Storage Options:</h4>
						<div class="controls">
							<%= SettingsServlet.getHTMLGlobalTransferStorageSettingsForm(request, "settings", null) %>
						</div>
			</div>
			<div class="tab-pane" id="tab4">	
			<h4>Services and Plugins</h4>			
			  <%= Services.getInstance().getHTMLServiceManagementTable("services.jsp", "advanced.jsp", "services") %>
			  </div>
			  <div class="tab-pane" id="tab5">	
				<h4>Storage Services</h4>				
				<%@include file="plugin-settings/storage-plugin.jspf" %>			
			  </div>
		</div>
	</div>
	
	<div id="no">
	
	</div>
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