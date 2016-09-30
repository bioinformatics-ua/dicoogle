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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.core.XMLSupport;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

/**
 * 
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class ServerStorageServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        resp.setContentType("application/json");
		resp.getWriter().write(JSONSerializer.toJSON(ServerSettings.getInstance().getMoves()).toString());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String type = req.getParameter("type");

		if (type == null) {
			sendError(resp, 400, "Missing argument \"type\": must be either \"add\" or \"remove\"");
			return;
		}

		String ip = req.getParameter("ip");
		String aetitle = req.getParameter("aetitle");
		String port = req.getParameter("port");
		String description = req.getParameter("description");
		String paramPublic = req.getParameter("public");
		boolean isPublic = paramPublic.isEmpty() || Boolean.parseBoolean(paramPublic);

		// validate
		if (aetitle == null) {
			sendError(resp, 400, "Missing argument \"aetitle\"");
			return;
		}

		switch(type){
		case "add": {

			if (ip == null) {
				sendError(resp, 400, "Missing argument \"ip\"");
				return;
 			}

			if (port == null) {
				sendError(resp, 400, "Missing argument \"port\"");
				return;
			}
			int _port = Integer.parseInt(port);
			if (_port < 1 || _port > 65535) {
				sendError(resp, 400, "Illegal argument \"port\": must be in network socket port range");
				return;
			}

			ServerSettings.getInstance().add(new MoveDestination(aetitle, ip, _port, isPublic, description));
			ResponseUtil.simpleResponse(resp, "added", true);
			break;
		}
		case "remove": {
			boolean removed = ServerSettings.getInstance().removeMoveDestination(aetitle);
			ResponseUtil.simpleResponse(resp, "removed", removed);
			break;
		}
		}
		
		  new XMLSupport().printXML();
	}

	private static void sendError(HttpServletResponse resp, int code, String message) throws IOException {
		resp.setStatus(code);
		resp.setContentType("application/json");
		JSONObject obj = new JSONObject();
		obj.put("error", message);
		resp.getWriter().append(obj.toString());
	}
}
