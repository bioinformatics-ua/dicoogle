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

package pt.ua.dicoogle.server.web.servlets.management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class ForceIndexing extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// System.out.println("Fetching Data");
		String uriArrayJson = req.getParameter("uris");
		String pluginName = req.getParameter("plugin");

		Gson gson = new Gson();

		List<String> uris = gson.fromJson(uriArrayJson,
				new TypeToken<ArrayList<String>>() {
				}.getType());
		if (uris == null || uris.size() == 0) {
			resp.sendError(400, "No uri provided");
			return;
		}

		PluginController pc = PluginController.getInstance();

		List<Task<Report>> reports = new ArrayList<>(uris.size());
		for (String uri : uris) {
			URI u = null;
			try {
				u = new URI(uri.replaceAll(" ", "%20"));
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
			}
			if (u != null) {
				if (pluginName == null)
					reports.addAll(pc.index(u));
				else
					reports.addAll(pc.index(pluginName, u));
			}
		}

		List<Report> done = new ArrayList<>(reports.size());
		StringBuilder builder = new StringBuilder();
		for (Task<Report> t : reports) {
			try {
				Report r = t.get();
				done.add(r);
				builder.append(r).append("\n");
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			}
		}
		
		resp.getWriter().write(
				new StringRepresentation(builder.toString(),
						MediaType.TEXT_PLAIN).toString());
	}

}
