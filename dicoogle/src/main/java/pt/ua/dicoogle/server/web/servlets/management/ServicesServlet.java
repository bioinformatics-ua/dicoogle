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

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

/**
 * 
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class ServicesServlet extends HttpServlet {
	private PluginController mPluginController;
	//private ControlServices mControlServices;
	
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
		mPluginController = PluginController.get();
		//mControlServices = ControlServices.getInstance();
		
		
		boolean isRunning = false; 
		switch (mType) {
		case 0:
			isRunning = true/*mControlServices.storageIsRunning()*/;
			
			break;
		case 1:
			//TODO: RETRIEVE STATE OF PLUGIN
			
			break;
			
		case 2:
			isRunning = true /*mControlServices.queryRetrieveIsRunning()*/;
			break;

		default:
			break;
		}
		
		ResponseUtil.simpleResponse(resp, "running",isRunning);
		
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		mPluginController = PluginController.get();
		//mControlServices = ControlServices.getInstance();
		
		boolean setState = Boolean.parseBoolean(req.getParameter("running")); 
		switch (mType) {
		case 0:
			boolean success = true;
			if(setState)
				success = true/*(mControlServices.startStorage() ==0)?true:false*/;
			else
				;//mControlServices.stopStorage();
			
			
			ResponseUtil.simpleResponse(resp, "success", success);
			
			break;
		case 1:
			//TODO: START AND STOP PLUGINS
			
			break;
			
		case 2:
			/*if(setState)
				mControlServices.startQueryRetrieve();
			else
				mControlServices.stopStorage();
			*/
			
			ResponseUtil.simpleResponse(resp, "success", true);
			
			break;

		default:
			break;
		}
	}

}
