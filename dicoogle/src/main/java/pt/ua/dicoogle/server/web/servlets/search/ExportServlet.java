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

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.core.query.ExportToCSVQueryTask;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.utils.DictionaryAccess;

public class ExportServlet extends HttpServlet{
	private static final Logger logger = LoggerFactory.getLogger(ExportServlet.class);

	public enum ExportType{
		LIST, EXPORT_CVS;
	}
	private ExportType type;
	
	public ExportServlet(ExportType type) {
		this.type = type;
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		switch(type){
		case LIST:
			doGetTagList(req, resp);
			break;
		case EXPORT_CVS:
			doGetExportCvs(req, resp);
			break;
		}
	}
	
	private void doGetTagList(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		HashMap< String, Integer> tagList = DictionaryAccess.getInstance().getTagList();
		Iterator iterator = tagList.entrySet().iterator();
		
		JSONArray array = new JSONArray();
		
		while(iterator.hasNext())
		{
			Map.Entry<String, Integer> entry = (Entry<String, Integer>) iterator.next();
			//JSONObject obj = new JSONObject();
			//obj.put("key", entry.getKey());
			//obj.put("value", entry.getValue());
			
			
			array.add(entry.getKey());
		}
		
		resp.getWriter().write(array.toString());
	}
	
	private void doGetExportCvs(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setHeader("Content-disposition","attachment; filename=QueryResultsExport.csv");
		String queryString = req.getParameter("query");
		String[] fields = req.getParameterValues("fields");
		String[] providers = req.getParameterValues("providers");
		boolean keyword = Boolean.parseBoolean(req.getParameter("keyword"));
		
		logger.debug("queryString: {}", queryString);
		logger.debug("fields: {}", Arrays.asList(fields));
		logger.debug("keyword: {}", keyword);
		
		if(queryString == null)
			resp.sendError(401, "Query Parameters not found");
		
		if(fields == null || fields.length==0)
			resp.sendError(402, "Fields Parameters not found");
		
		if (!keyword) {
            QueryExpressionBuilder q = new QueryExpressionBuilder(queryString);
            queryString = q.getQueryString();
        }

						    	
	    List<String> fieldList = Arrays.asList(fields);
	    Map<String, String> fieldsMap = new HashMap<>();
	    for(String f : fields){
	    	fieldsMap.put(f, f);
	    }
	    	    	    
    	ExportToCSVQueryTask task = new ExportToCSVQueryTask( fieldList,
				resp.getOutputStream());

    	if(providers == null || providers.length == 0) {
			PluginController.getInstance().queryAll(task, queryString, fieldsMap);
		} else {
			List<String> providersList = Arrays.asList(providers);
			PluginController.getInstance().query(task, providersList, queryString,
					fieldsMap);
		}

		task.await();
	}

	
}
