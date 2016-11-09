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

import net.sf.json.JSONSerializer;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
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
		resp.getWriter().write(JSONSerializer.toJSON(ServerSettingsManager.getSettings().getDicomServicesSettings().getMoveDestinations()).toString());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String type = req.getParameter("type");
		String ip = req.getParameter("ip");
		String aetitle = req.getParameter("aetitle");
		String port = req.getParameter("port");
		int _port= Integer.parseInt(port);
		
		switch(type){
		case "addMoveDestination":
			ServerSettingsManager.getSettings().getDicomServicesSettings().addMoveDestination(new MoveDestination(aetitle, ip, _port));
			ResponseUtil.simpleResponse(resp, "added", true);
			break;
		case "remove":
			ResponseUtil.simpleResponse(resp, "removed", ServerSettingsManager.getSettings().getDicomServicesSettings().removeMoveDestination(aetitle));
			break;
		}

		ServerSettingsManager.saveSettings();
	}

}
