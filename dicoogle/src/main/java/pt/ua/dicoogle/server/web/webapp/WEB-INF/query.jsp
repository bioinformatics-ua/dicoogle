<%@page trimDirectiveWhitespaces="true"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="pt.ua.dicoogle.server.web.dicom.Search"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Dicoogle Web</title>
		<%@include file="jspf/header.jspf" %>
		<link rel="stylesheet" href="style.css" />
		<script src="search.js" defer="defer"></script>
		<style>
			[class*="span"]
			{
				margin-left: 0px;
				margin-top: 20px;
			}

			[class*="span"]:first-child
			{
				margin-left: 0px;
				margin-top: 0px;
			}

			label
			{
				display: inline;
			}
		</style>
	</head>
	<body>
		<%@include file="jspf/mainbar.jspf" %>
		<%@include file="jspf/needsLoginBegin.jspf" %>
		<div class="container-fluid">
			<%
				// mount the search object
				Search search = new Search(request);
				// get list of available tags
				//HashMap<String, Integer> tags = DictionaryAccess.getInstance().getTagList();
                                HashMap<String, Integer> tags = new HashMap<String,Integer>();
			%>
			<section class="tabs well">
				<strong>Search Type:</strong>
				<input id="tab-1" type="radio" name="radio-set" class="tab-selector tab-selector-1" <%= ((! search.hasQuery()) || (search.hasQuery() && (! search.isAdvancedQuery()))) ? "checked=\"checked\"" : "" %> />
				<label for="tab-1" class="tab-label-1">Default Search</label>
				<input id="tab-2" type="radio" name="radio-set" class="tab-selector tab-selector-2" <%= (search.hasQuery() && (search.isAdvancedQuery())) ? "checked=\"checked\"" : "" %> />
				<label for="tab-2" class="tab-label-2">Advanced Search</label>
				<hr />
				<div class="contents">
					<div class="tab-content content-1">
						<form method='get' action="search.jsp">
							<input type="submit" class="btn btn-primary btn-large" value="Search" />
							<br />
							<br />
							<input type="hidden" name="method" value="default" />
							<label for="query" style="display: none;">Search Pattern: </label>
							<input id="query" name="query" type="text" list="tagslist" placeholder="Search Pattern" <%= (search.hasQuery() && (! search.isAdvancedQuery())) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getSimpleQuery()) + "\"" : "" %> />
							<datalist id="tagslist">
							<%
								if (tags != null)
									for (Map.Entry<String, Integer> tag : tags.entrySet())
									{
							%>
								<option value="<%= StringEscapeUtils.escapeHtml4(tag.getKey() + ":") %>" label="<%= StringEscapeUtils.escapeHtml4(tag.getKey()) %>" />
							<%
									}
							%>
							</datalist>
							<input id="keywords" type="checkbox" name="keywords" style="margin-left: 10px;" <%= ((! search.hasQuery()) || (search.hasQuery() && (((! search.isAdvancedQuery()) && search.isKeyworded()) || search.isAdvancedQuery()))) ? "checked=\"checked\"" : "" %> />
							<label for="keywords">Keywords</label>
						</form>
					</div>
					<div class="tab-content content-2">
						<form method="get" action="search.jsp">
							<input type="submit" class="btn btn-primary btn-large" value="Search" />
							<br />
							<br />
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
													<input id="patientName" name="patientName" type="text" placeholder="(All Patients)" <%= (search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getPatientName()) + "\"" : "" %> />
												</td>
											</tr>
											<tr>
												<td>
													<label for="patientID">Patient ID: </label>
												</td>
												<td>
													<input id="patientID" name="patientID" type="text" placeholder="(All IDs)" <%= (search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getPatientID()) + "\"" : "" %> />
												</td>
											</tr>
											<tr>
												<td>
													<label for="patientGender">Patient Gender: </label>
												</td>
												<td>
													<select id="patientGender" name="patientGender">
														<option value="all" <%= (search.hasQuery() && search.isAdvancedQuery() && search.isPatientGenderAll()) ? "selected" : "" %> >All</option>
														<option value="male" <%= (search.hasQuery() && search.isAdvancedQuery() && search.isPatientGenderMale()) ? "selected" : "" %> >Male</option>
														<option value="female" <%= (search.hasQuery() && search.isAdvancedQuery() && search.isPatientGenderFemale()) ? "selected" : "" %> >Female</option>
													</select>
												</td>
											</tr>
											<tr>
												<td>
													<label for="institutionName">Institution Name: </label>
												</td>
												<td>
													<input id="institutionName" name="institutionName" type="text" placeholder="(All Institutions)" <%= (search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getInstitutionName()) + "\"": "" %> />
												</td>
											</tr>
											<tr>
												<td>
													<label for="physician">Physician: </label>
												</td>
												<td>
													<input id="physician" name="physician" type="text" placeholder="(All Physicians)" <%= (search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getPhysician()) + "\"": "" %> />
												</td>
											</tr>
											<tr>
												<td>
													<label for="operatorName">Operator Name: </label>
												</td>
												<td>
													<input id="operatorName" name="operatorName" type="text" placeholder="(All Operators)" <%= (search.hasQuery() && search.isAdvancedQuery()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getOperatorName()) + "\"": "" %> />
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
													<input id="modCR" type="checkbox" name="modCR" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModCR()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modCR">CR</label>
												</td>
												<td>
													<input id="modMG" type="checkbox" name="modMG" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModMG()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modMG">MG</label>
												</td>
												<td>
													<input id="modPT" type="checkbox" name="modPT" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModPT()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modPT">PT</label>
												</td>
												<td>
													<input id="modXA" type="checkbox" name="modXA" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModXA()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modXA">XA</label>
												</td>
												<td>
													<input id="modES" type="checkbox" name="modES" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModES()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modES">ES</label>
												</td>
											</tr>
											<tr>
												<td>
													<input id="modCT" type="checkbox" name="modCT" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModCT()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modCT">CT</label>
												</td>
												<td>
													<input id="modMR" type="checkbox" name="modMR" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModMR()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modMR">MR</label>
												</td>
												<td>
													<input id="modRF" type="checkbox" name="modRF" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModRF()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modRF">RF</label>
												</td>
												<td>
													<input id="modUS" type="checkbox" name="modUS" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModUS()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modUS">US</label>
												</td>
												<td>
													<input id="modOthers" type="checkbox" name="modOthers" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModOthers()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modOthers">Others</label>
												</td>
											</tr>
											<tr>
												<td>
													<input id="modDX" type="checkbox" name="modDX" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModDX()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modDX">DX</label>
												</td>
												<td>
													<input id="modNM" type="checkbox" name="modNM" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModNM()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modNM">NM</label>
												</td>
												<td>
													<input id="modSC" type="checkbox" name="modSC" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModSC()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
													<label for="modSC">SC</label>
												</td>
												<td>
													<input id="modOT" type="checkbox" name="modOT" onclick="modalityCheckBoxToggle()" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isModOT()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
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
										<input id="tab-5" type="radio" name="studyDate" value="exact" class="tab-selector tab-selector-3" <%= ((! search.hasQuery()) || (search.hasQuery() && ((search.isAdvancedQuery() && search.isExactDate()) || (! search.isAdvancedQuery())))) ? "checked=\"checked\"" : "" %> />
										<label for="tab-5" class="tab-label-3">Exact Date</label>
										<input id="tab-6" type="radio" name="studyDate" value="range" class="tab-selector tab-selector-4" <%= (search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate()) ? "checked=\"checked\"" : "" %> />
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
																<input id="exactDate" name="exactDate" type="text" placeholder="(Any Date)" <%= (search.hasQuery() && search.isAdvancedQuery() && search.isExactDate()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getExactDate()) + "\"" : "" %> />
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
																		<input id="fromDate" type="checkbox" name="fromDate" <%= (search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate() && search.isUseStartDate()) ? "checked=\"checked\"" : "" %> />
																		<label for="fromDate">From:</label>
																	</td>
																</tr>
																<tr>
																	<td>
																		<label for="startDate" style="display: none;">Beginning: </label>
																		<input id="startDate" name="startDate" type="text" placeholder="(Beginning)" <%= (search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate() && search.isUseStartDate()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getStartDate()) + "\"" : "" %> />
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
																		<input id="toDate" type="checkbox" name="toDate" <%= (search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate() && search.isUseEndDate()) ? "checked=\"checked\"" : "" %> />
																		<label for="toDate">To:</label>
																	</td>
																</tr>
																<tr>
																	<td>
																		<label for="endDate" style="display: none;">Finish: </label>
																		<input id="endDate" name="endDate" type="text" placeholder="(Today)" <%= (search.hasQuery() && search.isAdvancedQuery() && search.isRangedDate() && search.isUseEndDate()) ? "value=\"" + StringEscapeUtils.escapeHtml4(search.getEndDate()) + "\"" : "" %> />
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
				<%= search.getResultHTMLTreeView("results") %>
			<%
				}
			%>
		</div>
		<%@include file="jspf/needsLoginEnd.jspf" %>
		<%@include file="jspf/footer.jspf" %>
	</body>
</html>