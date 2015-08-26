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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * @author Frederico Silva <fredericosilva@ua.pt>
 * @todo
 */
public class WadoServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String uid = req.getParameter("uid");
		if (StringUtils.isEmpty(uid)) {
            resp.sendError(400, "No uid supplied!\n"
            		+ "/wado?uid=UID");
            return;
        }
		
		String query = "SOPInstanceUID:" + uid;
		
		HashMap<String, String> extraFields = new HashMap<>();
        extraFields.put("SOPInstanceUID", "SOPInstanceUID");
		
		PluginController pc = PluginController.getInstance();
        JointQueryTask task = new MyHolder();
        
        Iterable<SearchResult> results = null;
        try {
			results = pc.queryAll(task, query, extraFields).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
        
        
        String uri ="not";
        
        ArrayList<SearchResult> resultsArr = new ArrayList<>();
        for (SearchResult r : results) {
            resultsArr.add(r);
            
            uri = r.getURI().toURL().toString();
        }
        
        resp.getWriter().print(uri);
	}
	
	 private static class MyHolder extends JointQueryTask {
         
	        @Override
	        public void onCompletion() {
	        }

	        @Override
	        public void onReceive(Task<Iterable<SearchResult>> e) {
	        }
	    }    

}
