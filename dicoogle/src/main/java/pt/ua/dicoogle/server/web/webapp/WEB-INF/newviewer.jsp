<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="pt.ua.dicoogle.server.web.dicom.Information"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dicoogle Web - Viewer</title>

<link rel="stylesheet" href="styles/bootstrap.css" />
<%@include file="jspf/header.jspf"%>
<link rel="stylesheet" href="style.css" />
<link rel="stylesheet" href="styles/style_utils.css" />
<%@include file="jspf/footer.jspf"%>
</head>
<body>
	<%@include file="jspf/mainbar.jspf"%>
	<%@include file="jspf/needsLoginBegin.jspf"%>

	<div class="row-fluid forceFirstContainer">
  		<div class="span12">
  			<%
				String sop = request.getParameter("SOPInstanceUID");
  				if (sop != null) {
					int numberOfFrames = Information
							.getNumberOfFramesInFile(request
									.getParameter("SOPInstanceUID"));
					String frameRateStr = "";
					if(numberOfFrames > 0){
						float frameRate = Information.getFrameRateFromImage(sop);
						if(numberOfFrames > 1 && frameRate == 0) frameRate = 15;
						frameRateStr = ", frameRate: "+frameRate;
			%>
  		
			<%@include file="jspf/simpleviewer.jspf" %>
			<script type="text/javascript">
				$(document).ready(function(){
					simpleViewer({uid: "<%=sop%>", nFrames: <%=numberOfFrames%><%=frameRateStr%>});					
				});
			</script>
			<% }} %>
		</div>
	</div>
	<%@include file="jspf/needsLoginEnd.jspf"%>

</body>
</html>