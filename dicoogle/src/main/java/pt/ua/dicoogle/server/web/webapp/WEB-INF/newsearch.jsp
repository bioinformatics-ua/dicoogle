<%@page import="pt.ua.dicoogle.sdk.utils.TagValue"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="pt.ua.dicoogle.plugins.PluginController"%>
<%@page trimDirectiveWhitespaces="true"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="pt.ua.dicoogle.server.web.dicom.Search"%>
<%@page import="pt.ua.dicoogle.sdk.utils.DictionaryAccess"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="pt.ua.dicoogle.sdk.utils.TagsStruct" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	 
    <%@include file="jspf/footer.jspf" %><%@include file="jspf/header.jspf" %>
	
        <div id="app">
        
            <header class="header">
                
                <%@include file="jspf/mainbar.jspf" %>
                
		<%@include file="jspf/needsLoginBegin.jspf" %>	
		<div id="search" class="container-fluid">
		
			<%
						// mount the search object
							Search search = new Search(request);
							// get list of available tags
							HashMap<String, Integer> tags = DictionaryAccess.getInstance().getTagList();				
							
							String qr = "";
							if(search.hasQuery() && !search.isAdvancedQuery())
								qr = "value=\""+search.getSimpleQuery() +"\"";
							else if(search.hasQuery())
								qr = "value=\""+search.getAdvancedQuery() +"\"";
					%>
			<section class="tabs well forceFirstContainer">
				<form id="search-form-normal" class="form-search" method='post' action="newsearch.jsp">		
					<input type="hidden" name="queryProviders" value="FFF" />			
					<input id="formMethod" type="hidden" name="method" value="default" />
					<input id="query" name="query" type="text" class="input-medium search-query" list="tagslist" placeholder="Search" <%=qr%> />
					<button id="searchButton0" type="button" class="btn btn-primary"  value="Search" onclick="submitForm()">Search</button>
					<datalist id="tagslist">
					<%
						if (tags != null)
										for (Map.Entry<String, Integer> tag : tags.entrySet())
										{
					%>
						<option value="<%=StringEscapeUtils.escapeHtml4(tag.getKey() + ":")%>" label="<%=StringEscapeUtils.escapeHtml4(tag.getKey())%>" />
					<%
						}
					%>
					</datalist>
					<input id="keywords" type="checkbox" name="keywords" style="margin-left: 10px;" <%=((! search.hasQuery()) || (search.hasQuery() && (((! search.isAdvancedQuery()) && search.isKeyworded()) || search.isAdvancedQuery()))) ? "checked=\"checked\"" : ""%> />
					<label for="keywords">Keywords</label>
				</form>
								    
				<label for="grp0" class="tab-label-1 a">Search Type:</label>
				<div id="grp0" class="btn-group" data-toggle="buttons-radio">					
  					<button id="a1" type="button" class="btn btn-info btn-small" onclick="setActive(a1)">Default</button>
  					<button id="a2" type="button" class="btn btn-info btn-small" onclick="setActive(a2)">Advanced</button> 				
				</div>
			    <div id="selectQueryProvidersBtn" class="btn-group">
   					<a class="btn dropdown-toggle btn-small btn-info" data-toggle="dropdown" href="#">
    				Select Query Providers
    				<span class="caret"></span>
    				</a>
    				<ul class="dropdown-menu">   
    					<li class="divider"></li>
   					 	<li><a href="javascript:void(0);" pid="-1">Select All</a></li>
   					 	<li><a href="javascript:void(0);" pid="-2">Select None</a></li>
    				</ul>
    			</div>
						<script>
							var providers =
						<%=search.getQueryProvidersJSON()%>
							;

							$(document)
									.ready(
											function() {
												setActive(a1);
												console.log(providers);
												$('.nav-tabs').button();

												//var app = "<li><a href=\"javascript:void(0);\">"+p+" <i class=\"icon-ok\"></i></a></li>";
												for (p in providers) {
													$(
															"#selectQueryProvidersBtn ul")
															.prepend(
																	"<li><a href=\"javascript:void(0);\" pid=\""
																			+ p
																			+ "\">"
																			+ providers[p].name
																			+ " <i class=\"icon-ok\"></i></a></li>");

													if (!providers[p].selected)
														$(
																"#selectQueryProvidersBtn ul a[pid="
																		+ p
																		+ "] i")
																.hide();
												}

												$(
														"#selectQueryProvidersBtn ul a")
														.click(
																function(a) {
																	var pid = $(
																			this)
																			.attr(
																					"pid");

																	if (pid < 0) {
																		if (pid == -1) {
																			val = true;
																			$(
																					"#selectQueryProvidersBtn ul a i")
																					.show();
																		} else {
																			val = false;
																			$(
																					"#selectQueryProvidersBtn ul a i")
																					.hide();
																		}
																		for (p in providers)
																			providers[p].selected = val;

																		return;
																	}
																	providers[pid].selected = !providers[pid].selected;

																	if (!providers[pid].selected)
																		$(this)
																				.children(
																						"i")
																				.hide();
																	else {
																		$(this)
																				.children(
																						"i")
																				.show();
																		$(
																				"#selectQueryProvidersBtn a.btn-warning")
																				.removeClass(
																						"btn-warning")
																				.addClass(
																						"btn-info");

																	}
																});

												$(
														"#search-form-adv,#search-form-normal")
														.submit(
																function() {
																	var pr = new Array();
																	var i = 0;
																	for (p in providers) {
																		console
																				.log(providers[p].name
																						+ " "
																						+ providers[p].selected);
																		if (providers[p].selected)
																			pr[i++] = providers[p].name;
																	}

																	if (pr.length == 0) {
																		$(
																				"#selectQueryProvidersBtn a")
																				.first()
																				.removeClass(
																						"btn-info");
																		$(
																				"#selectQueryProvidersBtn a")
																				.first()
																				.addClass(
																						"btn-warning");
																		return false;
																	}
																	var a = JSON
																			.stringify(pr);

																	$(this)
																			.children(
																					"input[name=queryProviders]")
																			.attr(
																					"value",
																					a);
																});

											});

							function toggleProvider() {
								$("div").children("i").hide();
							}

							function submitForm() {
								if ($("#formMethod").val() == "advanced") {
									$("#search-form-adv").submit();
									console.log("submited advanced");
								} else {
									$("#search-form-normal").submit();
									console.log("submited normal");
								}

							}

							function setActive(a) {
								$("#grp0 button").removeClass("active");
								$(a).addClass("active");

								if (a == a1) {
									$("#formMethod").attr("value", "default");
									$("#advancedSearchDiv").hide();
								} else {
									$("#formMethod").attr("value", "advanced");
									$('#advancedSearchDiv').fadeTo('slow', 1.0)
								}
							}
						</script>
			
				<hr />
				<div class="contents">
					<div id="advancedSearchDiv">
						<form id="search-form-adv" method="post" action="newsearch.jsp">
							<input type="hidden" name="queryProviders" value="RRR" />
							<input type="hidden" name="method" value="advanced" />
							<div class="advanced container-fluid" style="padding: 0;">
								<div class="span5" style="width: 380px;">
									<table>
										<tbody>
											<tr>
												<td>
													<label for="patientName">Patient Name: </label>
												</td>
												<td>
													<input id="patientName" name="patientName" type="text" placeholder="(All Patients)" <%=(search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getPatientName()) + "\"" : ""%> />
												</td>
											</tr>
											<tr>
												<td>
													<label for="patientID">Patient ID: </label>
												</td>
												<td>
													<input id="patientID" name="patientID" type="text" placeholder="(All IDs)" <%=(search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getPatientID()) + "\"" : ""%> />
												</td>
											</tr>
											<tr>
												<td>
													<label for="patientGender">Patient Gender: </label>
												</td>
												<td>
													<select id="patientGender" name="patientGender">
														<option value="all" <%=(search.hasQuery() && search.isAdvancedQuery() && search.isPatientGenderAll()) ? "selected" : ""%> >All</option>
														<option value="male" <%=(search.hasQuery() && search.isAdvancedQuery() && search.isPatientGenderMale()) ? "selected" : ""%> >Male</option>
														<option value="female" <%=(search.hasQuery() && search.isAdvancedQuery() && search.isPatientGenderFemale()) ? "selected" : ""%> >Female</option>
													</select>
												</td>
											</tr>
											<tr>
												<td>
													<label for="institutionName">Institution Name: </label>
												</td>
												<td>
													<input id="institutionName" name="institutionName" type="text" placeholder="(All Institutions)" <%=(search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getInstitutionName()) + "\"": ""%> />
												</td>
											</tr>
											<tr>
												<td>
													<label for="physician">Physician: </label>
												</td>
												<td>
													<input id="physician" name="physician" type="text" placeholder="(All Physicians)" <%=(search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getPhysician()) + "\"": ""%> />
												</td>
											</tr>
											<tr>
												<td>
													<label for="operatorName">Operator Name: </label>
												</td>
												<td>
													<input id="operatorName" name="operatorName" type="text" placeholder="(All Operators)" <%=(search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getOperatorName()) + "\"": ""%> />
												</td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="span4">
									<strong>Modality:</strong>
									<input id="checkAll" type="radio" name="check" onclick="modalitiesSelectAll()" />
									<label for="checkAll">Select All</label>
									<input id="checkNone" type="radio" name="check" onclick="modalitiesSelectNone()" />
									<label for="checkNone">Select None</label>
									<table class="modalities" id="modalities">
										<tbody>
											<tr>
												<td>
													<input id="modCR" type="checkbox" name="modCR" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModCR()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modCR">CR</label>
												</td>
												<td>
													<input id="modMG" type="checkbox" name="modMG" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModMG()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modMG">MG</label>
												</td>
												<td>
													<input id="modPT" type="checkbox" name="modPT" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModPT()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modPT">PT</label>
												</td>
												<td>
													<input id="modXA" type="checkbox" name="modXA" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModXA()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modXA">XA</label>
												</td>
												<td>
													<input id="modES" type="checkbox" name="modES" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModES()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modES">ES</label>
												</td>
											</tr>
											<tr>
												<td>
													<input id="modCT" type="checkbox" name="modCT" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModCT()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modCT">CT</label>
												</td>
												<td>
													<input id="modMR" type="checkbox" name="modMR" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModMR()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modMR">MR</label>
												</td>
												<td>
													<input id="modRF" type="checkbox" name="modRF" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModRF()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modRF">RF</label>
												</td>
												<td>
													<input id="modUS" type="checkbox" name="modUS" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModUS()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modUS">US</label>
												</td>
												<td>
													<input id="modOthers" type="checkbox" name="modOthers" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModOthers()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modOthers">Others</label>
												</td>
											</tr>
											<tr>
												<td>
													<input id="modDX" type="checkbox" name="modDX" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModDX()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modDX">DX</label>
												</td>
												<td>
													<input id="modNM" type="checkbox" name="modNM" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModNM()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modNM">NM</label>
												</td>
												<td>
													<input id="modSC" type="checkbox" name="modSC" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModSC()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modSC">SC</label>
												</td>
												<td>
													<input id="modOT" type="checkbox" name="modOT" onclick="modalityCheckBoxToggle()" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModOT()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
													<label for="modOT">OT</label>
												</td>
												<td></td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="span8" style="width: 100%; max-width: 750px;">
									<section class="tabs">
										<strong>Study Date:</strong>
										<input id="tab-5" type="radio" name="studyDate" value="exact" class="tab-selector tab-selector-3" <%=((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isExactDate()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : ""%> />
										<label for="tab-5" class="tab-label-3">Exact Date</label>
										<input id="tab-6" type="radio" name="studyDate" value="range" class="tab-selector tab-selector-4" <%=(search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate()) ? "checked=\"checked\"" : ""%> />
										<label for="tab-6" class="tab-label-4">Date Range</label>
										<div class="contents">
											<div class="tab-content content-3">
												<table class="dates">
													<tbody>
														<tr>
															<td>
																<label for="exactDate">Date: </label>
															</td>
														</tr>
														<tr>
															<td>
																<input id="exactDate" name="exactDate" type="text" placeholder="(Any Date)" <%=(search.hasQuery() && search.isAdvancedQuery() && search.isExactDate()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getExactDate()) + "\"" : ""%> />
																(yyyymmdd form)
															</td>
														</tr>
													</tbody>
												</table>
											</div>
											<div class="tab-content content-4">
												<div class="dates">
													<div class="span3">
														<table>
															<tbody>
																<tr>
																	<td>
																		<input id="fromDate" type="checkbox" name="fromDate" <%=(search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate() && search.isUseStartDate()) ? "checked=\"checked\"" : ""%> />
																		<label for="fromDate">From:</label>
																	</td>
																</tr>
																<tr>
																	<td>
																		<label for="startDate" style="display: none;">Beginning: </label>
																		<input id="startDate" name="startDate" type="text" placeholder="(Beginning)" <%=(search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate() && search.isUseStartDate()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getStartDate()) + "\"" : ""%> />
																	</td>
																</tr>
															</tbody>
														</table>
													</div>
													<div class="span5" style="margin-top: 0px;">
														<table>
															<tbody>
																<tr>
																	<td>
																		<input id="toDate" type="checkbox" name="toDate" <%=(search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate() && search.isUseEndDate()) ? "checked=\"checked\"" : ""%> />
																		<label for="toDate">To:</label>
																	</td>
																</tr>
																<tr>
																	<td>
																		<label for="endDate" style="display: none;">Finish: </label>
																		<input id="endDate" name="endDate" type="text" placeholder="(Today)" <%=(search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate() && search.isUseEndDate()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getEndDate()) + "\"" : ""%> />
																		(yyyymmdd form)
																	</td>
																</tr>
															</tbody>
														</table>
													</div>
												</div>
											</div>
										</div>
									</section>
								</div>
							</div>
						</form>
					</div>
				</div>
			</section>
			<hr />	
			<%
					// if there was a query passed then perform the search and show its results
						if (search.hasQuery())
						{
				%>
						
		<div class="row-fluid">
			<div class="span10" style="float: none;
     margin-left: auto;
     margin-right: auto;">
	
<script src="scripts/multiselectable.js" type="text/javascript"></script>
<script type="text/javascript">
	//Methods For DUMP MODE
	var tagData = <%=search.getAllTags()%>;
	var hashedTagData = null;
	
	var selectedTags = null;

	var stdViewer;
	var pts;
	var debug = 0;

	function registerSSE(id) {
		if (typeof (EventSource) !== "undefined") {
			var source = new EventSource("search/holders?id=" + id);

			source.addEventListener('close', function(e) {
				// Connection was closed.
				console.log("Closing:");
				source.close();
			}, false);

			source.addEventListener('QueryResponse', function(e) {
				//console.log(e);
				var json = $.parseJSON(e.data);

				$.extend(pts, json.rsp);

				//console.log(e.data);
				//console.log("----------");
				//console.log("PROVIDER: " + json.provider);
				//console.log("Response: " + json.rsp);
				//console.log(pts);

				stdViewer = studyViewer(pts);
				$(stdViewer).on("openViewer", function(event, data) {
					$.post("dic2png", {
						SOPInstanceUID : data.uid
					}, onPostResponse).always(function() {
						data.status.stop();
					});
				});
				
				$(stdViewer).on("export", function (event){	
					if(selectedTags == null ){
					selectedTags = $('#selectedTagsContainer');
					
					$('#selectedTagsContainer').multiSelect({
						selectableHeader: "<div class='custom-header'>Waiting Selection</div>",
						selectableFooter: "<div class='custom-header'><a class=\"btn btn-mini\" onclick=\"selectAll()\"><i class=\"icon-plus-sign\"></i>Select All</a></div>",
						selectionHeader: "<div class='custom-header'>Selected Items</div>",	
						selectionFooter: "<div class='custom-header'><a class=\"btn btn-mini\" onclick=\"deselectAll()\"><i class=\"icon-remove-sign\"></i>Deselect All</a></div>"
							
					});	
					//$('#selectedTagsContainer').multiselectable();
					$("#tagNameBox").autocomplete({
						source: tagData,
						disabled: false, minLength: 2, delay: 500, appendTo: "#invisibleDIV",
						response: function( e, ui ) {
							var output2 = [];
							var tempSelected = [];
							//tempSelected = $("#selectedTagsContainer option[selected]").map(function(a,b){
							tempSelected = $("div.ms-selection li.ms-selected span").map(function(a,b){
								
								return $(b).text();
							});

							
							for(i=0; i< (tempSelected.length); i++){
								output2.push('<option value="'+ tempSelected[i] +'" selected="selected">'+ tempSelected[i] +'</option>');
							}

							var cnt = ui.content;
							if(cnt.length > 0){
								for(var ic = 0; ic<cnt.length ;ic++){
									if( $.inArray(cnt[ic].value, tempSelected) == -1 )
										output2.push('<option value="'+ cnt[ic].value +'">'+ cnt[ic].value +'</option>');
								}
							}else{
								
								if(hashedTagData == null){
									hashedTagData = [];
								
									for(var i = 0 in tagData){
										hashedTagData[tagData[i].toLowerCase()] = tagData[i];
									}
								}
								
								
							
								var text = $("#tagNameBox").val();
								
								var words = text.split(/(,| )/);								
								for(var ic = 0; ic<words.length ;ic++){
									if(words[ic].length > 1){
										if( $.inArray(words[ic], tempSelected) == -1 ){
											var wr = hashedTagData[words[ic].toLowerCase()]	
											if(wr == undefined)
												wr = words[ic];
										
											output2.push('<option value="'+ wr +'">'+ wr +'</option>');
										}
									}
								}
							}
							
							$('#selectedTagsContainer').html(output2.join(""));
							$('#selectedTagsContainer').multiSelect("refresh");	
						}
					});	
					}
				
				$('#exportModal').modal("show");
			});
								
			}, false);
		}
	}

	$(document).ready(function() {
		// Smart Wizard
		var searchID =
<%=search.placeSearchOrder()%>
	;
		console.log("SearchID: " + searchID);
		if (searchID != -1) {
			pts = new Array();
			stdViewer = studyViewer(pts);
			registerSSE(searchID);
		}
		
		$('body').on('show', '.modal', function(){
			  $(this).css({'margin-top':($(window).height()-$(this).height())/2,'top':'0'});
			  $(this).css({'margin-left':($(window).width()-$(this).width())/2,'left':'0'});
		});

		$('#modalInfoPopover').popover({html: true , title: "How to select a tag!!", content: "You can select a Tag by writing the tag name in the top textbox, and then moving the desired tag into the right container.<br>You may also past a set of tags separated by commas, and then applying the same procedure.<br>Please note that only the tags in the right container will appear in the generated CSV file."});	
	
		
		
		return;
	});
	
	function saveExportData(){
		var tempSelected = [];
		tempSelected = $("div.ms-selection li.ms-selected span").map(function(a,b){
			return $(b).text();
		});
		
		var jsonData = new Object();
		<%
			String queryString = "\""+ search.getSimpleQuery()+"\"";
			String providers = search.getQueryProvidersJSON();
			
			if(search.isAdvancedQuery())
				queryString = "\""+search.getAdvancedQuery()+"\"";			
		%>
		
		jsonData.queryString = <%=queryString%>;
		var qrProviders = <%=providers%>;
		var selectedProviders = [];
		for(var p in qrProviders){
			if(qrProviders[p].selected)
				selectedProviders.push(qrProviders[p].name);
		}
		jsonData.providers = selectedProviders;
		jsonData.extraFields = [];
		
		for(var p=0; p< (tempSelected.length); p++){
			jsonData.extraFields.push(tempSelected[p]);
		}
		
		$.ajax({
              type: 'POST',
              url: '/exportFile',
              data: "JSON-DATA=" + JSON.stringify(jsonData),
              success: function (r) {
            	  	var data = $.parseJSON(r);            	    
                    
                	if (data) {
                        var iframe = $("<iframe/>").attr({
                            src: "exportFile?UID=" + data.uid,
                            style: "visibility:hidden;display:none"
                        }).appendTo( $("#invisibleDIV") );
                    } else {
						console.log("ERROR Downloading CSV File.");
                    }						
              }
          });
		
	}
	
	function selectAll(){
		$('#selectedTagsContainer').multiSelect("select_all");	
	}
	function deselectAll(){
		$('#selectedTagsContainer').multiSelect("deselect_all");	
	}
</script>

			<%@include file="jspf/studybrowser.jspf" %>
			</div>
		</div>
				<!-- MODAL VIEW FOR EXPORT -->
	    <div id="exportModal" class="modal hide fade" style="max-width: 90%; width: auto">
		    <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		    <h3>Export Search Results</h3>
		    </div>
		    <div class="modal-body">
		    			<table id="tabletestid">
		    				<tr>
		    					<td>
									    <div class="input-append">
    										<input type="text" class="input-xxlarge" id="tagNameBox" placeholder="Enter Tag Name" data-placement="right" data-toggle="tooltip" data-original-title="Tooltip on right">
    									</div>	
			    				</td>
		    				</tr>
		    				<tr>
		    					<td><select id="selectedTagsContainer" size="5" multiple="multiple">
                         			
                        	</select></td>
		    				</tr>
		    			</table>
		    </div>
		    <div class="modal-footer">
		    <a id="modalInfoPopover" href=# class="btn btn-info" data-placement="top" data-toggle="popover">info</a>	
		    <a href="#" class="btn" data-dismiss="modal">Close</a>
		    <a href="#" class="btn btn-primary" id="testBtnSave" onclick="saveExportData();">Save Export Data</a>
		    </div>
	    </div>
		
		<%
			}
		%>
		</div>
			
		<div id="viewer" class="row-fluid forceFirstContainer">
  		<div class="span12" id="loadable"> 		
  			<%@include file="jspf/simpleviewer.jspf" %>
		</div>
	</div>
	
	<script type="text/javascript">
		var v;
		$(document).ready(function() {
			$("#010").button();
			$("#viewer").hide();
		});

		function onPostResponse(data) {
			if (data.NumberOfFrames < 1)
				return;

			if (data.NumberOfFrames > 1 && data.FrameRate == 0)
				data.FrameRate = 15;

			$("#search").hide();
			$("#viewer").show();

			if (v == undefined || v.sopInstanceUID != data.SOPInstanceUID) {
				v = SimpleViewer({
					uid : data.SOPInstanceUID,
					nFrames : data.NumberOfFrames,
					frameRate : data.FrameRate
				});
				$(v).on("close", function() {
					$("#search").show();
					$("#viewer").hide();
				});
			}

		}
	</script>
		<%@include file="jspf/needsLoginEnd.jspf" %>
		<div id="invisibleDIV" style="display: none;">
		</div>
                
                
	</body>
</html>