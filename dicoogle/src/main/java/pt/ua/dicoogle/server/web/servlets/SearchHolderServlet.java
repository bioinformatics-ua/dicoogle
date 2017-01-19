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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.KeyValue;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.sdk.datastructs.dim.DIMGeneric;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.server.web.dicom.SearchHolder;
import pt.ua.dicoogle.server.web.utils.DIM2JSONConverter;

/**
 * Useful servlet for asynchronous queries.
 * 
 * @author Tiago Godinho.
 *
 */
public class SearchHolderServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session = request.getSession(true);
		if(session == null){
			response.sendError(401, "ERROR: NO SESSION FOUND");
			return;
		}
		SearchHolder holder = (SearchHolder) session.getAttribute("dicoogle.web.queryHolder");
		if(holder == null){
			response.sendError(402, "ERROR: NO SEARCH SESSION FOUND");
			return;
		}
		int id = Integer.parseInt(request.getParameter("id"));

		response.setHeader("pragma", "no-cache,no-store"); 
		response.setHeader("cache-control", "no-cache,no-store,max-age=0,max-stale=0"); 
		response.setHeader("connection", "keep-alive");
		response.setContentType("text/event-stream"); 		
		response.setCharacterEncoding("UTF-8"); // set the apropriate encoding type

		PrintWriter wr = response.getWriter();

		Iterable<KeyValue> it = holder.retrieveQueryOutput(id);

		wr.print("data: \n\n"); 
		wr.flush();

		int eventID = 0;
		for(KeyValue resp : it){
			List<SearchResult> results = new ArrayList<>();
			
			String provider = resp.getKey().toString();
			@SuppressWarnings("unchecked")
			Iterable<SearchResult> result = (Iterable<SearchResult>) resp.getValue();
			for(SearchResult r : result )
				results.add(r);
			
			try {
				DIMGeneric generic = new DIMGeneric(results);
				
				JSONObject obj = new JSONObject();
				obj.put("provider", provider);
				JSONArray arr = DIM2JSONConverter.convertToJSON(generic);		
				obj.put("rsp", arr);
				
				wr.print("id: "+ (eventID++)+"\n");
				wr.print("event: QueryResponse\n"); 
				wr.print("data: "+obj.toString()+"\n\n"); 
				wr.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		wr.print("event: close\n");
		wr.flush();
		wr.close();
	}
}
