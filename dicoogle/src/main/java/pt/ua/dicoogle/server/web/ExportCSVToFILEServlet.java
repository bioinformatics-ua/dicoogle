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
package pt.ua.dicoogle.server.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.core.query.ExportToCSVQueryTask;
import pt.ua.dicoogle.plugins.PluginController;

public class ExportCSVToFILEServlet extends HttpServlet {

	private File tempDirectory;
	public ExportCSVToFILEServlet(File tempDirectory) {
		super();
		this.tempDirectory = tempDirectory;
		new AtomicLong(0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String uid = req.getParameter("UID");
		if(uid == null){
			resp.sendError(401, "No Query UID Suplied: Please fill the field \"UID\"");
			return;
		}
		
		File tmp = new File(tempDirectory, "QueryResultsExport-"+uid);
		if(!tmp.exists()){
			resp.sendError(402, "The file for the given uid was not found. Please try again...");
			return; 
		}
		
		// Find this file id in database to get file name, and file type

        // You must tell the browser the file type you are going to send
        // for example application/pdf, text/plain, text/html, image/jpg
        resp.setContentType("application/csv");
        // Make sure to show the download dialog
        resp.setHeader("Content-disposition","attachment; filename=QueryResultsExport.csv");		
		
		FileInputStream fi = new FileInputStream(tmp);
		BufferedInputStream bi = new BufferedInputStream(fi);
		
		IOUtils.copy(bi, resp.getOutputStream());
		resp.getOutputStream().flush();
	
		IOUtils.closeQuietly(fi);
		IOUtils.closeQuietly(bi);
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

		String uid = UUID.randomUUID().toString();
		//File tempFile = File.createTempFile("QueryResultsExport-", uid, tempDirectory);
		File tempFile = new File(tempDirectory, "QueryResultsExport-"+uid);
		tempFile.deleteOnExit();
		System.out.println("UID: "+uid);
		System.out.println("FilePath: "+tempFile.getAbsolutePath());
		
		FileOutputStream outStream = new FileOutputStream(tempFile);
		BufferedOutputStream bos = new BufferedOutputStream(outStream);
		
		ExportToCSVQueryTask task = new ExportToCSVQueryTask(orderedFields,
				bos);
		
		if (arr.isEmpty()) {
			PluginController.get().queryAll(task, queryString, fields);
		} else {
			List<String> providers = new ArrayList<>();
			for (Object f : arr) {
				providers.add(f.toString());
			}
			PluginController.get().query(task, providers, queryString,
					fields);
		}

		task.await();
		
		JSONObject obj = new JSONObject();
		obj.put("uid", uid);
		resp.getWriter().write(obj.toString());
		resp.getWriter().flush();
	}

}
