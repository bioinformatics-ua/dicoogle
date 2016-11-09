/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.server.web.dicom;

import static pt.ua.dicoogle.server.web.utils.Query.addExtraQueryParam;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.utils.DictionaryAccess;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.settings.Utils;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * A simple class used by the Dicoogle Web interface that allow web users to
 * have the same searching capabilities of desktop application users. 
 * 
 * The new version of these class generates the query tasks. These tasks take into
 * account the multiple available providers. The search task is then placed in a SearchHolder
 * especially created to the issuing user. This holder is accessible via /search/holders.
 * 
 * @author Tiago Marques Godinho
 * @author António Novo <antonio.novo@ua.pt>
 */
public class Search {
	private List<String> selectedProviders;

	/**
	 * Is this an advanced search?
	 */
	private boolean isAdvanced;

	/**
	 * Simple query string.
	 */
	private String simpleQuery;
	/**
	 * If the simple query string is keyword based.
	 */
	private boolean keyworded;

	/**
	 * How long did the search procedure take to complete, in milliseconds.
	 */
	private long timeTaken;

	/**
	 * Advanced search params.
	 */
	private String patientName;
	private String patientID;
	private String patientGender;
	private String institutionName;
	private String physician;
	private String operatorName;
	private String studyDateFormat;
	private String exactDate;
	private boolean useStartDate;
	private boolean useEndDate;
	private String startDate;
	private String endDate;
	private boolean modCR;
	private boolean modMG;
	private boolean modPT;
	private boolean modXA;
	private boolean modES;
	private boolean modCT;
	private boolean modMR;
	private boolean modRF;
	private boolean modUS;
	private boolean modDX;
	private boolean modNM;
	private boolean modSC;
	private boolean modOT;
	private boolean modOthers;

	/**
	 * Final query string.
	 */
	private String finalQuery;

	/**
	 * Lists of search results in various forms.
	 */
	private Collection<SearchResult> searchResults;

	/**
	 * If the result list is supposed to have all the extra fields (servlet XML
	 * request) or just the minimum (webapp search).
	 */
	private boolean fullRequest;

	private HttpSession httpSession;

	private int searchID;

	@SuppressWarnings("unchecked")
	public Search(ServletRequest request) {
		this.searchID = -1;
		// reset all the internal properties
		isAdvanced = false;
		simpleQuery = null;
		patientName = null;
		patientID = null;
		patientGender = null;
		institutionName = null;
		physician = null;
		operatorName = null;
		studyDateFormat = null;
		exactDate = null;
		useStartDate = false;
		useEndDate = false;
		startDate = null;
		endDate = null;
		modCR = false;
		modMG = false;
		modPT = false;
		modXA = false;
		modES = false;
		modCT = false;
		modMR = false;
		modRF = false;
		modUS = false;
		modDX = false;
		modNM = false;
		modSC = false;
		modOT = false;
		modOthers = false;
		finalQuery = null;
		searchResults = null;
		fullRequest = false;

		// this ensures that any special characters won't be parsed incorrectly
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			// do nothing
		}

		// get the query method (either default or advanced)
		String method = request.getParameter("method");

		// see if we should parse the request as simple or advanced search
		if ((method != null) && (!method.isEmpty())) {
			if (method.equalsIgnoreCase("Advanced")) // advanced search
			{
				isAdvanced = true;
			}
		}

		// and now mount the proper query
		if (isAdvanced) {
			mountAdvancedSearch(request);
		} else {
			mountSimpleSearch(request);
		}

		String qp = request.getParameter("queryProviders");

		System.out.println("QueryProviders: " + qp);
		JSONArray arr = JSONArray.fromObject(qp);

		System.out.println("QueryProviders: " + qp);
		if (qp != null) {
			this.selectedProviders = new ArrayList<>();
			this.selectedProviders.addAll(JSONArray.toCollection(arr,
					String.class));
		} else {
			this.selectedProviders = PluginController.getInstance()
					.getQueryProvidersName(true);
		}
		this.httpSession = ((HttpServletRequest) request).getSession(false);
	}

	private void mountSimpleSearch(ServletRequest request) {
		// get the only param of this simple query
		simpleQuery = request.getParameter("query");
		keyworded = Utils.parseCheckBoxValue(request.getParameter("keywords"));

		// parse the user query into an expression
		if ((simpleQuery != null) && (!simpleQuery.trim().isEmpty())) // if the
																		// query
																		// is
																		// not
																		// empty...
		{
			// ... build a query expression based on it and get its resulting
			// query string
			if (!isKeyworded()) {
				// write the QueryString respecting BNF grammer defined
				// regarding Lucene documentation 2.4.X branch
				QueryExpressionBuilder exp = new QueryExpressionBuilder(
						simpleQuery);
				finalQuery = exp.getQueryString();
			} else {
				finalQuery = simpleQuery;
			}
		} else {
			finalQuery = "*:*"; // default query string
		}
	}

	private void mountAdvancedSearch(ServletRequest request) {
		isAdvanced = true;

		// get all the params defined on this advanced query
		patientName = request.getParameter("patientName");
		patientID = request.getParameter("patientID");
		patientGender = request.getParameter("patientGender");
		institutionName = request.getParameter("institutionName");
		physician = request.getParameter("physician");
		operatorName = request.getParameter("operatorName");
		studyDateFormat = request.getParameter("studyDate");
		exactDate = request.getParameter("exactDate");
		useStartDate = Utils.parseCheckBoxValue(request
				.getParameter("fromDate"));
		useEndDate = Utils.parseCheckBoxValue(request.getParameter("toDate"));
		startDate = request.getParameter("startDate");
		endDate = request.getParameter("endDate");
		modCR = Utils.parseCheckBoxValue(request.getParameter("modCR"));
		modMG = Utils.parseCheckBoxValue(request.getParameter("modMG"));
		modPT = Utils.parseCheckBoxValue(request.getParameter("modPT"));
		modXA = Utils.parseCheckBoxValue(request.getParameter("modXA"));
		modES = Utils.parseCheckBoxValue(request.getParameter("modES"));
		modCT = Utils.parseCheckBoxValue(request.getParameter("modCT"));
		modMR = Utils.parseCheckBoxValue(request.getParameter("modMR"));
		modRF = Utils.parseCheckBoxValue(request.getParameter("modRF"));
		modUS = Utils.parseCheckBoxValue(request.getParameter("modUS"));
		modDX = Utils.parseCheckBoxValue(request.getParameter("modDX"));
		modNM = Utils.parseCheckBoxValue(request.getParameter("modNM"));
		modSC = Utils.parseCheckBoxValue(request.getParameter("modSC"));
		modOT = Utils.parseCheckBoxValue(request.getParameter("modOT"));
		modOthers = Utils.parseCheckBoxValue(request.getParameter("modOthers"));

		// and form the query string
		finalQuery = getAdvancedQuery();
	}

	/**
	 * Returns a String with a valid query, that can be combined with others if
	 * needed, for searching for all modalities currently defined on the web
	 * interface (supported directly, minus the Others).
	 * 
	 * @return a String with a valid query, that can be combined with others if
	 *         needed, for searching for all modalities currently defined on the
	 *         web interface (supported directly, minus the Others).
	 */
	private List<String> getAllDefinedModalitiesQuery() {
		List<String> ret = new ArrayList<>(13);

                                ret.add("CR");
                                ret.add("CT");
                                ret.add("DX");
                                ret.add("ES");
                                ret.add("MG");
                                ret.add("MR");
                                ret.add("NM");
                                ret.add("OT");
                                ret.add("PT");
                                ret.add("RF");
                                ret.add("SC");
                                ret.add("US");
                                ret.add("XA");             

		return ret;
	}

	/**
	 * Based on the set of optional advanced search params, returns a advanced
	 * query string that reflects those params.
	 * 
	 * @return an advanced query string for the params that match their values.
	 */
	public String getAdvancedQuery() {
		String result = "";

		// patient name
		if ((patientName != null) && (!patientName.isEmpty())) {
			result = addExtraQueryParam(result, "PatientName:(" + patientName
					+ ")");
		}

		// patient id
		if ((patientID != null) && (!patientID.isEmpty())) {
			result = addExtraQueryParam(result, "PatientID:(" + patientID + ")");
		}

		// patient gender
		if ((patientGender != null) && (!patientGender.isEmpty())
				&& (!isPatientGenderAll())) {
			if (isPatientGenderMale()) {
				result = addExtraQueryParam(result, "PatientSex:M");
			} else {
				if (isPatientGenderFemale()) {
					result = addExtraQueryParam(result, "PatientSex:F");
				}
			}
		}

		// institution name
		if ((institutionName != null) && (!institutionName.isEmpty())) {
			result = addExtraQueryParam(result, "InstitutionName:("
					+ institutionName + ")");
		}

		// physician
		if ((physician != null) && (!physician.isEmpty())) {
			result = addExtraQueryParam(result, "(PerformingPhysicianName:("
					+ physician + ") OR ReferringPhysicianName:(" + physician
					+ "))");
		}

		// operator name
		if ((operatorName != null) && (!operatorName.isEmpty())) {
			result = addExtraQueryParam(result, "OperatorName:(" + operatorName
					+ ")");
		}

		// study date
		if ((studyDateFormat != null) && (!studyDateFormat.isEmpty())) {
			if (studyDateFormat.equalsIgnoreCase("Exact")) {
				if ((exactDate != null) && (!exactDate.isEmpty()))
					result = addExtraQueryParam(result, "StudyDate:("
							+ exactDate + ")");
			} else {
				if (studyDateFormat.equalsIgnoreCase("Range")) {
					String date = "StudyDate:[";

					if (useStartDate && (startDate != null)
							&& (!startDate.isEmpty())) {
						date += startDate;
					} else {
						date += "0000101";
					}
					date += " TO ";
					if (useEndDate && (endDate != null) && (!endDate.isEmpty())) {
						date += endDate;
					} else {
						Calendar cal = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

						date += sdf.format(cal.getTime());
					}

					date += "]";

					result = addExtraQueryParam(result, date);
				}
			}
		}

		// modalities
		{
			String modalities = "";
                                                Set<String> mods = new HashSet<String>();
                        
                        
                                                 if (modCR) {
                                                                mods.add("CR");
                                                    }
                                                        if (modCT) {
                                                                mods.add("CT");
                                                        }
                                                        if (modDX) {
                                                                mods.add("DX");
                                                        }
                                                        if (modES) {
                                                                mods.add("ES");
                                                        }
                                                        if (modMG) {
                                                                mods.add("MG");
                                                        }
                                                        if (modMR) {
                                                                mods.add("MR");
                                                        }
                                                        if (modNM) {
                                                                mods.add("NM");
                                                        }
                                                        if (modOT) {
                                                                mods.add("OT");
                                                        }
                                                        if (modPT) {
                                                                mods.add("PT");
                                                        }
                                                        if (modRF) {
                                                                mods.add("RF");
                                                        }
                                                        if (modSC) {
                                                                mods.add("SC");
                                                        }
                                                        if (modUS) {
                                                                mods.add("US");
                                                        }
                                                        if (modXA) {
                                                                mods.add("XA");
                                                        }
                                                                                
                                                        for(String s : mods){
                                                            modalities+=s +" ";
                                                        }
                                                        
                                                        if(modOthers){
                                                            for(String s : getAllDefinedModalitiesQuery()){
                                                                if(!mods.contains(s))
                                                                    modalities += "-"+s+" ";
                                                            }
                                                        }                                                       
                                                
			// and addMoveDestination the modalities to the resulting query
			if (!modalities.isEmpty()) {
				result = addExtraQueryParam(result, "Modality:(" + modalities + ")");
			}
		}

		// if no "options" were modified then use the default query string
		if (result.isEmpty()) {
			result = "*:*";
		}

		return result;
	}

	/**
	 * Returns if there is a query to be used (NOTE: an empty string is still a
	 * valid query!).
	 * 
	 * @return if there is a query to be used.
	 */
	public boolean hasQuery() {
		return ((!isAdvanced) && (simpleQuery != null)) || isAdvanced;
	}

	/**
	 * Returns the original query.
	 * 
	 * @return the original query.
	 */
	public String getSimpleQuery() {
		return simpleQuery;
	}

	/**
	 * Returns the final query.
	 * 
	 * @return the final query.
	 */
	public String getFinalQuery() {
		return finalQuery;
	}

	/**
	 * Returns a list with the search results for the query.
	 * 
	 * @return a list with the search results for the query.
	 */
	@Deprecated
	public Collection<SearchResult> getSearchResults() {
		// if there is no valid query then abort
		if (!hasQuery())
			return null;

		// if the value is not cached then do the search and cache it
		if (searchResults == null) {
			// perform the search
			long startime = System.nanoTime();
			searchResults = search(finalQuery, fullRequest);
			long endTime = System.nanoTime();
			// update the time taken for the search to complete
			timeTaken = (endTime - startime) / 1000000;
		}

		return searchResults;
	}

	/**
	 * Returns the Selected Query Providers in the JSON Format
	 * @return The selected Query Providers
	 */
	public String getQueryProvidersJSON() {

		JSONArray arr = new JSONArray();

		for (String p : getQueryProviders()) {
			JSONObject obj = new JSONObject();
			obj.put("name", p);
			boolean sel = this.selectedProviders.contains(p);
			obj.put("selected", sel);
			arr.add(obj);
		}

		return arr.toString();
	}

	/**
	 * Returns a List containing the Selected Query Providers
	 * @return The selected Query Providers 
	 */
	public List<String> getQueryProviders() {
		List<String> providers = PluginController.getInstance()
				.getQueryProvidersName(true);

		return providers;
	}

	/**
	 * @return
	 */
	public List<String> getSelectedProviders() {
		return this.selectedProviders;
	}

	/**
	 * Places a new search in the SearchHolder
	 * @param query The query String
	 * @return The Given Query Identifier
	 */
	private int fireSearch(String query) {
		if (selectedProviders.isEmpty())
			return -1;

		HashMap<String, String> searchParam = new HashMap<>();

		searchParam.put("PatientName", "PatientName");
		searchParam.put("Modality", "Modality");
		searchParam.put("StudyDate", "StudyDate");
		searchParam.put("SOPInstanceUID", "SOPInstanceUID");
		searchParam.put("Thumbnail", "Thumbnail");
		searchParam.put("StudyDescription", "StudyDescription");
		searchParam.put("InstitutionName", "InstitutionName");
		searchParam.put("SeriesDescription", "SeriesDescription");
		searchParam.put("PatientID", "PatientID");
		searchParam.put("PatientSex", "PatientSex");

		if (httpSession == null)
			return -1;

		SearchHolder holder = (SearchHolder) httpSession
				.getAttribute("dicoogle.web.queryHolder");
		if (holder == null) {
			holder = new SearchHolder();
			httpSession.setAttribute("dicoogle.web.queryHolder", holder);
		}

		int id = holder.registerNewQuery(selectedProviders, query, searchParam);

		return id;
	}

	/**
	 * Places the search in the Holder. If it has not been done before.
	 * @return
	 */
	public int placeSearchOrder() {
		// if there is no valid query then abort
		if (!hasQuery())
			return -1;

			
		// performs the search, if it hasn't already
		if(searchID == -1)
			searchID = fireSearch(finalQuery);

		return searchID;
	}

	/**
	 * Performs a simple search.
	 * 
	 * @param query
	 *            the user entered query.
	 * @return a list of SearchResult objects.
	 */
	@Deprecated
	private Collection<SearchResult> search(String query, boolean fullRequest) {
		if (selectedProviders.isEmpty())
			return Collections.emptyList();
		// get the search results for this query
		// results = idx.searchSync(query, (fullRequest ? extraFieldsFull :
		// extraFields));

		List<SearchResult> targetCollection = new ArrayList<SearchResult>();
		Iterable<SearchResult> itResults = null;

		HashMap<String, String> searchParam = new HashMap<>();

		searchParam.put("PatientName", "PatientName");
		searchParam.put("Modality", "Modality");
		searchParam.put("StudyDate", "StudyDate");
		searchParam.put("SOPInstanceUID", "SOPInstanceUID");
		searchParam.put("Thumbnail", "Thumbnail");
		searchParam.put("StudyDescription", "StudyDescription");
		searchParam.put("InstitutionName", "InstitutionName");
		searchParam.put("SeriesDescription", "SeriesDescription");
		searchParam.put("PatientID", "PatientID");
		searchParam.put("PatientSex", "PatientSex");
		try {
			// for(String provider : selectedProviders){
			// System.out.println("Searching in Provider: "+provider);
			JointQueryTask t = new JointQueryTask() {

				@Override
				public void onReceive(Task<Iterable<SearchResult>> e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onCompletion() {
					// TODO Auto-generated method stub

				}
			};
			itResults = PluginController.getInstance()
					.query(t, selectedProviders, query, searchParam).get();

			// }

		} catch (InterruptedException | ExecutionException ex) {
			LoggerFactory.getLogger(Search.class).error(ex.getMessage(), ex);
		}

		if (itResults == null)
			return Collections.emptyList();

		for (SearchResult s : itResults) {
			targetCollection.add(s);
		}

		// and return them
		return targetCollection;
	}

	/**
	 * @return the isAdvanced
	 */
	public boolean isAdvancedQuery() {
		return isAdvanced;
	}

	/**
	 * @return the patientName
	 */
	public String getPatientName() {
		return patientName;
	}

	/**
	 * @return the patientID
	 */
	public String getPatientID() {
		return patientID;
	}

	/**
	 * @return the patientGender
	 */
	public String getPatientGender() {
		return patientGender;
	}

	public boolean isPatientGenderAll() {
		return (patientGender == null)
				|| patientGender.isEmpty()
				|| patientGender.equalsIgnoreCase("All")
				|| ((!patientGender.equalsIgnoreCase("Male")) && (!patientGender
						.equalsIgnoreCase("Female")));
	}

	public boolean isPatientGenderMale() {
		return (patientGender != null)
				&& patientGender.equalsIgnoreCase("Male");
	}

	public boolean isPatientGenderFemale() {
		return (patientGender != null)
				&& patientGender.equalsIgnoreCase("Female");
	}

	/**
	 * @return the institutionName
	 */
	public String getInstitutionName() {
		return institutionName;
	}

	/**
	 * @return the physician
	 */
	public String getPhysician() {
		return physician;
	}

	/**
	 * @return the operatorName
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * @return the studyDateFormat
	 */
	public String getStudyDateFormat() {
		return studyDateFormat;
	}

	public boolean isExactDate() {
		return (studyDateFormat != null) && (!studyDateFormat.isEmpty())
				&& studyDateFormat.equalsIgnoreCase("Exact");
	}

	public boolean isRangedDate() {
		return (studyDateFormat != null) && (!studyDateFormat.isEmpty())
				&& studyDateFormat.equalsIgnoreCase("Range");
	}

	/**
	 * @return the exactDate
	 */
	public String getExactDate() {
		return exactDate;
	}

	/**
	 * @return the useStartDate
	 */
	public boolean isUseStartDate() {
		return useStartDate;
	}

	/**
	 * @return the useEndDate
	 */
	public boolean isUseEndDate() {
		return useEndDate;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @return the modCR
	 */
	public boolean isModCR() {
		return modCR;
	}

	/**
	 * @return the modMG
	 */
	public boolean isModMG() {
		return modMG;
	}

	/**
	 * @return the modPT
	 */
	public boolean isModPT() {
		return modPT;
	}

	/**
	 * @return the modXA
	 */
	public boolean isModXA() {
		return modXA;
	}

	/**
	 * @return the modES
	 */
	public boolean isModES() {
		return modES;
	}

	/**
	 * @return the modCT
	 */
	public boolean isModCT() {
		return modCT;
	}

	/**
	 * @return the modMR
	 */
	public boolean isModMR() {
		return modMR;
	}

	/**
	 * @return the modRF
	 */
	public boolean isModRF() {
		return modRF;
	}

	/**
	 * @return the modUS
	 */
	public boolean isModUS() {
		return modUS;
	}

	/**
	 * @return the modDX
	 */
	public boolean isModDX() {
		return modDX;
	}

	/**
	 * @return the modNM
	 */
	public boolean isModNM() {
		return modNM;
	}

	/**
	 * @return the modSC
	 */
	public boolean isModSC() {
		return modSC;
	}

	/**
	 * @return the modOT
	 */
	public boolean isModOT() {
		return modOT;
	}

	/**
	 * @return the modOthers
	 */
	public boolean isModOthers() {
		return modOthers;
	}

	/**
	 * @return the keyworded
	 */
	public boolean isKeyworded() {
		return keyworded;
	}

	/**
	 * @return the time taken for the search to complete in milliseconds.
	 */
	public long getTimeTaken() {
		return timeTaken;
	}
	
	public static String getAllTags(){
		List<String> tags = new ArrayList<String>( DictionaryAccess.getInstance().getTagList().keySet() );
		Collections.sort(tags);
		JSONArray arr = JSONArray.fromObject(tags);
		return arr.toString();		
	}
}
