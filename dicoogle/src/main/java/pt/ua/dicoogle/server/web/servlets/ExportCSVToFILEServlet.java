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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.core.query.ExportToCSVQueryTask;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

public class ExportCSVToFILEServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(ExportCSVToFILEServlet.class);

	private File tempDirectory;
	public ExportCSVToFILEServlet(File tempDirectory) {
		super();
		this.tempDirectory = tempDirectory;
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uid = req.getParameter("UID");
		if(uid == null){
			ResponseUtil.sendError(resp, 400, "No query UID Supplied: Please provide the field \"UID\"");
			return;
		}

		// validate UUID
		try {
			UUID.fromString(uid);
		} catch (IllegalArgumentException ex) {
			ResponseUtil.sendError(resp, 400, "Illegal UUID supplied");
			return;
		}	

		File tmpFile = new File(tempDirectory, "QueryResultsExport-"+uid);
		logger.debug("Reading temporary CSV file: {}", tmpFile);
		if(!tmpFile.exists()) {
			ResponseUtil.sendError(resp, 404, "The file for the given UID was not found.");
			return; 
		}
		
		// Find this file id in database to get file name, and file type

        // You must tell the browser the file type you are going to send
        // for example application/pdf, text/plain, text/html, image/jpg
        resp.setContentType("application/csv");
        // Make sure to show the download dialog
        resp.setHeader("Content-disposition","attachment; filename=QueryResultsExport.csv");		
		
		try (BufferedInputStream bi = new BufferedInputStream(new FileInputStream(tmpFile))) {

			IOUtils.copy(bi, resp.getOutputStream());
			resp.getOutputStream().flush();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		List<String> orderedFields = new ArrayList<>();
		Map<String, String> fields = new HashMap<>();
		String queryString = null;
		String[] arr;		
		boolean keyword;
		
		try {
			queryString = req.getParameter("query");
			if (queryString == null) {
				ResponseUtil.sendError(resp, 400,
						"QueryString not supplied: Please fill the field \"query\"");
				return;
			}

			logger.debug("{}", req.getParameter("fields"));
			JSONArray jsonObj = JSONArray.fromObject(req.getParameter("fields"));
			if (jsonObj.size()== 0) {
				ResponseUtil.sendError(resp, 400,
						"No fields supplied: Please fill the field \"extraFields\" in \"JSON\"");
				return;
			}

			for (Object f : jsonObj) {
				fields.put(f.toString(), f.toString());
				orderedFields.add(f.toString());
			}
			keyword = Boolean.parseBoolean(req.getParameter("keyword"));

			arr = req.getParameterValues("providers");
		} catch (JSONException ex) {
			ResponseUtil.sendError(resp, 400, "Invalid JSON content");
			return;
		}
		
		if (!keyword) {
            QueryExpressionBuilder q = new QueryExpressionBuilder(queryString);
            queryString = q.getQueryString();
        }

		String uid = UUID.randomUUID().toString();
		//File tempFile = File.createTempFile("QueryResultsExport-", uid, tempDirectory);
		File tempFile = new File(tempDirectory, "QueryResultsExport-"+uid);
		tempFile.deleteOnExit();
		logger.debug("UID: {}", uid);
		logger.debug("FilePath: {}", tempFile.getAbsolutePath());
		
		FileOutputStream outStream = new FileOutputStream(tempFile);
		BufferedOutputStream bos = new BufferedOutputStream(outStream);
		
		ExportToCSVQueryTask task = new ExportToCSVQueryTask(orderedFields,
				bos);
		
		if (arr == null || arr.length ==0) {
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
		
		JSONObject obj = new JSONObject();
		obj.put("uid", uid);
		resp.getWriter().write(obj.toString());
		resp.getWriter().flush();
	}

}
