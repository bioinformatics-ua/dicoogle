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
import net.sf.json.JSONObject;

import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.server.ControlServices;
import pt.ua.dicoogle.server.web.management.Services;

/**
 * 
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class ServicesServlet extends HttpServlet {
	
	public final static int STORAGE = 0;
	public final static int PLUGIN = 1;
	public final static int QUERY = 2;
	
	private final int mType;
	public ServicesServlet(int type) {
		mType = type;
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ControlServices controlServices = ControlServices.getInstance();
		ServerSettings serverSettings = ServerSettings.getInstance();
		
		
		boolean isRunning = false; 
		int port = -1;
        boolean autostart = false;
		switch (mType) {
		case STORAGE:
			isRunning = controlServices.storageIsRunning();
			port = serverSettings.getStoragePort();
			autostart = serverSettings.isStorage();
			break;
		case PLUGIN:
			//TODO: RETRIEVE STATE OF PLUGIN
			
			break;
			
		case QUERY:
			isRunning = controlServices.queryRetrieveIsRunning();
			port = serverSettings.getWlsPort();
			autostart = serverSettings.isQueryRetrive();
			break;

		default:
			break;
		}
		
        JSONObject obj = new JSONObject();
        obj.element("isRunning", isRunning);
        obj.element("port", port);
        obj.element("autostart", autostart);
        
        resp.setContentType("application/json");
        resp.getWriter().print(obj.toString());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ControlServices controlServices = ControlServices.getInstance();

        JSONObject obj = new JSONObject();
                
        String paramAutostart = req.getParameter("autostart");
        if (paramAutostart != null) {
            boolean autostart = Boolean.parseBoolean(paramAutostart);
            switch (mType) {
                case STORAGE:
                    ServerSettings.getInstance().setStorage(autostart);
                    obj.element("success", true);
                    obj.element("autostart", autostart);
                    break;
                case QUERY:
                    ServerSettings.getInstance().setQueryRetrive(autostart);
                    obj.element("success", true);
                    obj.element("autostart", autostart);
                    break;
                default:
                    obj.element("unsupported", true);
            }
            Services.getInstance().saveSettings();
        }
        
        String paramRunning = req.getParameter("running");
        if (paramRunning == null) {
            resp.getWriter().print(obj.toString());
            return;
        }
        
		boolean running = Boolean.parseBoolean(paramRunning); 
		switch (mType) {
		case STORAGE:
			boolean success = true;
            int result = 0;
			if(running) {
                result = controlServices.startStorage();
                success = (result == 0);
            }
			else
				controlServices.stopStorage();
			
            obj.element("success", success);
			break;
		case PLUGIN:
			//TODO: START AND STOP PLUGINS
			break;
			
		case QUERY:
			if(running)
				controlServices.startQueryRetrieve();
			else
				controlServices.stopQueryRetrieve();
			
            obj.element("success", true);
			break;

		default:
			break;
		}
        resp.setContentType("application/json");
        resp.getWriter().print(obj.toString());
	}

}
