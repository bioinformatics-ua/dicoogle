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
package pt.ua.dicoogle.server.web.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.core.query.ExportToCSVQueryTask;
import pt.ua.dicoogle.plugins.PluginController;

/**
 * @author fredericosilva@ua.pt
 */
public class ExportToCSVServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Content-disposition","attachment; filename=QueryResultsExport.csv");
		String queryString = req.getParameter("query");
		String[] fields = req.getParameterValues("fields");
		String[] providers = req.getParameterValues("providers");
		
		if(queryString == null)
			resp.sendError(401, "Query Parameters not found");
		
		if(fields == null || fields.length==0)
			resp.sendError(402, "Fields Parameters not found");
						    	
	    List<String> fieldList = new ArrayList<>(fields.length);
	    Map<String, String> fieldsMap = new HashMap<>();
	    for(String f : fields){
	    	fieldList.add(f);
	    	fieldsMap.put(f, f);
	    }
	    	    	    
    	ExportToCSVQueryTask task = new ExportToCSVQueryTask( fieldList,
				resp.getOutputStream());

    	if(providers == null || providers.length == 0) {
			PluginController.getInstance().queryAll(task, queryString, fieldsMap);
		} else {
			List<String> providersList = new ArrayList<>();
			for (String f : providers) {
				providersList.add(f);
			}
			PluginController.getInstance().query(task, providersList, queryString,
					fieldsMap);
		}

		task.await();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String dataString = req.getParameter("JSON-DATA");
		if (dataString == null) {
			resp.sendError(401,
					"No data suplied: Please fill the field \"JSON-DATA\"");
			return;
		}

		List<String> orderedFields = new ArrayList<>();
		Map<String, String> fields = new HashMap<>();
		String queryString = null;
		JSONArray arr;		
		
		try {
			JSONObject data = JSONObject.fromObject(dataString);
			queryString = data.getString("queryString");
			if (queryString == null) {
				resp.sendError(
						402,
						"QueryString no suplied: Please fill the field \"queryString\" in \"JSON-DATA\"");
				return;
			}

			arr = data.getJSONArray("extraFields");
			if (arr.isEmpty()) {
				resp.sendError(403,
						"No fields no suplied: Please fill the field \"extraFiekds\" in \"JSON-DATA\"");
				return;
			}

			for (Object f : arr) {
				fields.put(f.toString(), f.toString());
				orderedFields.add(f.toString());
			}

			arr = data.getJSONArray("providers");
		} catch (JSONException ex) {
			resp.sendError(400,
					"Error parsing the JSON String: " + ex.toString());
			return;
		}

		ExportToCSVQueryTask task = new ExportToCSVQueryTask(orderedFields,
				resp.getOutputStream());

		if (arr.isEmpty()) {
			PluginController.getInstance().queryAll(task, queryString, fields);
		} else {
			List<String> providers = new ArrayList<>();
			for (Object f : arr) {
				providers.add(f.toString());
			}
			PluginController.getInstance().query(task, providers, queryString,
					fields);
		}

		task.await();
		
	}

}
