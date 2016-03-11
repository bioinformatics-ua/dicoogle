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
package pt.ua.dicoogle.server.web.servlets.search;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.core.dim.DIMGeneric;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * Search the DICOM metadata, perform queries on images. Returns the data in JSON.
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class SearchServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
    private final Collection<String> DEFAULT_FIELDS = Arrays.asList(
            "SOPInstanceUID", "StudyInstanceUID", "SeriesInstanceUID", "PatientID",
            "PatientName",    "PatientSex",       "Modality",          "StudyDate",
            "StudyID",        "StudyDescription", "SeriesNumber",      "SeriesDescription",
            "InstitutionName", "uri");
  
  	public enum SearchType {
      ALL, PATIENT;
  	}
  	private final SearchType searchType;
  	public SearchServlet(){
  		searchType = SearchType.ALL;
  	}
  	public SearchServlet(SearchType stype){
  		if(stype == null)
  			searchType = SearchType.ALL;
  		else
  		searchType = stype;
  	}
    //TODO: QIDO;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
         Example: http://localhost:8080/search?query=wrix&keyword=false&provicer=lucene
         */
        String query = request.getParameter("query");
        String providers[] = request.getParameterValues("provider");
        boolean keyword = Boolean.parseBoolean(request.getParameter("keyword"));
        String[] fields = request.getParameterValues("field");

        // retrieve desired fields
        Set<String> actualFields;
        if (fields == null || fields.length == 0) {
            actualFields = null;
        } else {
            actualFields = new HashSet<>(Arrays.asList(fields));
        }
      
        if (StringUtils.isEmpty(query)) {
            response.sendError(400, "No query supplied!");
            return;
        }
        
        if (!keyword) {
            QueryExpressionBuilder q = new QueryExpressionBuilder(query);
            query = q.getQueryString();
        }

        List<String> providerList;
        boolean queryAllProviders = false;
        if (providers == null || providers.length == 0) {
            queryAllProviders = true;
        } else {
            providerList = Arrays.asList(providers);
            if (providerList.isEmpty()) {
                queryAllProviders = true;
            }
        }

        List<String> knownProviders = null;
        if (!queryAllProviders) {
            knownProviders = new ArrayList<>();
            List<String> activeProviders = PluginController.getInstance().getQueryProvidersName(true);
            for (String p : providers) {
                if (activeProviders.contains(p)) {
                    knownProviders.add(p);
                }
            }
        }

        HashMap<String, String> extraFields = new HashMap<>();
        if (actualFields == null) {
            
            //attaches the required extrafields
            for (String field : DEFAULT_FIELDS) {
                extraFields.put(field, field);
            }
        } else {
            for (String f : actualFields) {
                extraFields.put(f, f);
            }
        }
        
        JointQueryTask queryTaskHolder = new JointQueryTask() {

            @Override
            public void onCompletion() {
            }

            @Override
            public void onReceive(Task<Iterable<SearchResult>> e) {
            }
        };

        Iterable<SearchResult> results = null;
        long elapsedTime = System.currentTimeMillis();
        try {
            if (queryAllProviders) {
                results = PluginController.getInstance().queryAll(queryTaskHolder, query, extraFields).get();
            } else {
                results = PluginController.getInstance().query(queryTaskHolder, knownProviders, query, extraFields).get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            LoggerFactory.getLogger(SearchServlet.class).error(ex.getMessage(), ex);
        }
        elapsedTime = System.currentTimeMillis()-elapsedTime;
        
        if (results == null) {
            response.sendError(500, "Could not generate results!");
            response.setContentType("application/json");
            response.getWriter().append("{\"results\":[],\"error\":\"Could not generate results\"}");
            return;
        }

        ArrayList<SearchResult> resultsArr = new ArrayList<>();
        for (SearchResult r : results) {
            resultsArr.add(r);
        }
                
        String json;
        if(searchType == SearchType.PATIENT) {
        	response.setContentType("application/json");
            response.getWriter().append(getDIM(resultsArr));
        	return;
        }
        json = processJSON(resultsArr, elapsedTime);

        response.setContentType("application/json");
        response.getWriter().append(json);
    }

    private static String processJSON(Collection<SearchResult> results, long elapsedTime) {
        JSONObject resp = new JSONObject();
        for (SearchResult r : results) {
            JSONObject rj = new JSONObject();
            rj.put("uri", String.valueOf(r.getURI()));

            JSONObject fields = new JSONObject();

            for (Entry<String,Object> f : r.getExtraData().entrySet()) {
                // remove padding from string representations before accumulating
                fields.accumulate(f.getKey(), String.valueOf(f.getValue()).trim());
            }
            
            rj.put("fields", fields);

            resp.accumulate("results", rj);
        }
        resp.put("numResults", results.size());
        resp.put("elapsedTime", elapsedTime);

        return resp.toString();
    }
    
    private static String getDIM(ArrayList<SearchResult> allresults){
    	try {
        	DIMGeneric dimModel = new DIMGeneric(allresults);
            return dimModel.getJSON();
		} catch (Exception e) {
            LoggerFactory.getLogger(SearchServlet.class).warn("failed to get DIM", e);
            return "";
		}
    }
}
