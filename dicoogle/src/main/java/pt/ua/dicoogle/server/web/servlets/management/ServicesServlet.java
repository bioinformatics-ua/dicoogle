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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.server.ControlServices;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;
import pt.ua.dicoogle.server.web.utils.ResponseUtil.Pair;

/**
 * 
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class ServicesServlet extends HttpServlet {
	private PluginController mPluginController;
	private ControlServices mControlServices;
	private ServerSettings mServerSettings;
	
	public final static int STORAGE = 0;
	public final static int PLUGIN = 1;
	public final static int QUERY = 2;
	
	private int mType;
	public ServicesServlet(int type) {
		mType = type;
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		mPluginController = PluginController.getInstance();
		mControlServices = ControlServices.getInstance();
		mServerSettings = ServerSettings.getInstance();
		
		
		boolean isRunning = false; 
		int port = 0;
		switch (mType) {
		case 0:
			isRunning = mControlServices.storageIsRunning();
			port = mServerSettings.getStoragePort();
			
			break;
		case 1:
			//TODO: RETRIEVE STATE OF PLUGIN
			
			break;
			
		case 2:
			isRunning = mControlServices.queryRetrieveIsRunning();
			port = mServerSettings.getWlsPort();
			break;

		default:
			break;
		}
		
		List<Pair> response = new ArrayList<>();
		response.add(new Pair("isRunning", isRunning));
		response.add(new Pair("port", port));
		
		ResponseUtil.objectResponse(resp, response);
		
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		mPluginController = PluginController.getInstance();
		mControlServices = ControlServices.getInstance();

        JSONObject obj = new JSONObject();
        
        
        String paramAutostart = req.getParameter("autostart");
        if (paramAutostart != null) {
            boolean autostart = Boolean.parseBoolean(paramAutostart);
            ServerSettings.getInstance().setStorage(autostart);
        }
        
        String paramRunning = req.getParameter("running");
        if (paramRunning == null) {
            resp.getWriter().print(obj.toString());
			ResponseUtil.simpleResponse(resp, "success", true);
            return;
        }
        
		boolean running = Boolean.parseBoolean(paramRunning); 
		switch (mType) {
		case STORAGE:
			boolean success = true;
			if(running)
				success = mControlServices.startStorage() == 0;
			else
				mControlServices.stopStorage();
			
			ResponseUtil.simpleResponse(resp, "success", success);
			break;
		case PLUGIN:
			//TODO: START AND STOP PLUGINS
			break;
			
		case QUERY:
			if(running)
				mControlServices.startQueryRetrieve();
			else
				mControlServices.stopQueryRetrieve();
			
			ResponseUtil.simpleResponse(resp, "success", true);
			break;

		default:
			break;
		}
	}

}
