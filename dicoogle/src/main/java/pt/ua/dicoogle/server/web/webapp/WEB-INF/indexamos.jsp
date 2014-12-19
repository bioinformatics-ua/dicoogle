<%@page trimDirectiveWhitespaces="true"%>
<%@page import="pt.ua.dicoogle.server.web.management.Services"%>
<%@page import="pt.ua.dicoogle.server.web.management.Indexer" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<% System.err.prinln("Inside indexamos.jsp")%>

<!DOCTYPE html>
<html>

    <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta charset="utf-8">
            <title>Dicoogle Index</title>
            <%@include file="jspf/header.jspf" %>
            <style>
                .running{
                    background-color: rgb(223, 240, 216) !important;
                }
                .stopped{
                    background-color: rgb(242, 222, 222) !important;
                }
            </style>
            <script type="text/javascript" src="utils.js" defer="defer"></script>
            <script type="text/javascript" src="scripts/xhr.js" defer="defer"></script>
            <script type="text/javascript" src="scripts/select.js" defer="defer"></script>
            <script type="text/javascript" src="scripts/indexingStatus.js" defer="defer"></script>
    </head>

    <body onload="startStatusRequestChain(true);">
        <%@include file="jspf/mainbar.jspf" %>
        <%@include file="jspf/needsLoginBegin.jspf" %>
        <%@include file="jspf/needsAdminRightsBegin.jspf" %>

        <div class="container-fluid">
            <div class="well">
                <h1>Index Engine:</h1>
                
                <div class="container-fluid">
                <div class="control-group span6">
                    <label class="control-label">Indexing Status</label>
                    <div class="controls">
                        <form action="indexer" method="get">
                        <input type="hidden" id="indexingAction" name="action" value="<%= (Boolean) request.getAttribute("isIndexing")?"Stop":"Index" %>" />
                            <div class="input-append">
                            <div id="indexingProgressBar" class="progress progress-striped span4 <%= (Boolean) request.getAttribute("isIndexing")?"active" : "" %>" style="border-radius: 4px 0px 0px 4px; display: inline-block; position: relative; margin-bottom: 0px; margin-right: 1px; margin-left: 0px; height: 30px; max-width: 80%; min-width: 20%;">
                                <div class="bar" id="indexingProgress" style="line-height: 30px; vertical-align: middle; width: 
                                    <%= (Integer) request.getAttribute("indexingProgress") %>%;">
                                    <%= (Boolean) request.getAttribute("isIndexing") ?  (Integer) request.getAttribute("indexingProgress") + "%" : "" %>
                                </div>
                            </div>
                                <button type="submit" id="indexingActionButton" class="btn <%= (Boolean) request.getAttribute("isIndexing")?"btn-danger":"btn-success" %>">
                                    <i class="<%= (Boolean)request.getAttribute("isIndexing")? "icon-stop" : "icon-play" %>"></i>
                                        <%= (Boolean)request.getAttribute("isIndexing")?"Stop":"Start"%>
                                </button>
                            </div>
                        </form>
                    </div>
                    <script>
                    var act = document.getElementById("indexingAction");
                    var bar = document.getElementById("indexingProgressBar");
                    var pgs = document.getElementById("indexingProgress");
                    var btn = document.getElementById("indexingActionButton");

                    function recievedCurrentindexingStatus(isIndexing, progressPercentage){
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
                        else{
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

            <div class="control-group span6">
                <div class="controls">
                    <%= IndexerServlet.getHTMLServiceAdvancedSettingsForm(request, "indexer", null) %>
                </div>
            </div>
        </div>
    </div>
    
    <div class="well">
        <h1>Services and Plugins:</h1>
        <div class="container-fluid">
            <%= Services.getInstance().getHTMLServiceManagementTable("services.jsp", "advanced.jsp", "services") %>
        </div>
    </div>
    </div>
            
    <%@include file="jspf/needsAdminRightsEnd.jspf" %>
    <%@include file="jspf/needsLoginEnd.jspf" %>
    <%@include file="jspf/footer.jspf" %>
    </body>
</html>