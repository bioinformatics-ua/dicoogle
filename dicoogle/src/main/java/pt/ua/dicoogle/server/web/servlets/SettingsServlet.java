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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.core.XMLSupport;
import pt.ua.dicoogle.server.web.management.Dicoogle;
import pt.ua.dicoogle.server.web.management.Services;
import pt.ua.dicoogle.server.web.auth.Session;
import static pt.ua.dicoogle.sdk.settings.Utils.processAdvancedSettings;

/**
 * Handles requests for applying the general/advanced Dicoogle Settings.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
@Deprecated
public class SettingsServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Dicoogle dic;

	public static final String PARAM_ACTION = "action";

	public static final String ACTION_SET_ADVANCED_SETTINGS = "setadvancedsettings";
	public static final String ACTION_SET_ACCESS_LIST_SETTINGS = "setaccesslistsettings";
	public static final String ACTION_SET_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_SETTINGS = "settransferstorageglobalsettings";
	public static final String ACTION_SET_STORAGE_SERVICES = "setstorageservices";

	/*
	 * Action codes for internal use.
	 */
	private static final int ACTION_CODE_INVALID = 0;
	private static final int ACTION_CODE_SET_ADVANCED_SETTINGS = 1;
	private static final int ACTION_CODE_SET_ACCESS_LIST_SETTINGS = 2;
	private static final int ACTION_CODE_SET_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_SETTINGS = 3;
	private static final int ACTION_CODE_SET_STORAGE_SERVICES = 4;

	public SettingsServlet()
	{
		dic = Dicoogle.getInstance();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		// TODO validate user session credentials

		// get which action to take
		String actionStr = request.getParameter(PARAM_ACTION);

		// translate each action string to a action identification
		int action = ACTION_CODE_INVALID;
		if (actionStr != null)
		{
			if (actionStr.equalsIgnoreCase(ACTION_SET_ADVANCED_SETTINGS))
				action = ACTION_CODE_SET_ADVANCED_SETTINGS;
			else if (actionStr.equalsIgnoreCase(ACTION_SET_ACCESS_LIST_SETTINGS))
				action = ACTION_CODE_SET_ACCESS_LIST_SETTINGS;
			else if (actionStr.equalsIgnoreCase(ACTION_SET_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_SETTINGS))
				action = ACTION_CODE_SET_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_SETTINGS;
			else if (actionStr.equalsIgnoreCase(ACTION_SET_STORAGE_SERVICES))
				action = ACTION_CODE_SET_STORAGE_SERVICES;

		}

		// response to each action accordingly
		switch (action)
		{
			case ACTION_CODE_SET_ADVANCED_SETTINGS:
				HashMap<String, String[]> advSettings = new HashMap<String, String[]>();

				// get all the settings and their values
				Enumeration<String> params = request.getParameterNames();
				while (params.hasMoreElements())
				{
					String name = params.nextElement();

					// ignore the main params (the ones that go us here)
					if (name.equalsIgnoreCase(PARAM_ACTION))
						continue;

					String[] value = request.getParameterValues(name);
					advSettings.put(name, value);
				}

				HashMap<String, Object> settings = dic.getIndexingSettings();

				processAdvancedSettings(settings, advSettings);

				// try to apply the settings
				if (dic.tryIndexingSettings(settings))
				{
					dic.setIndexingSettings(settings);

					Services svcs = Services.getInstance();
					svcs.saveSettings();

					// send	the client back the to previous page
					response.sendRedirect(Session.getLastVisitedURL(request));
				}
				else
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters!");
			break;

			case ACTION_CODE_SET_ACCESS_LIST_SETTINGS:
				advSettings = new HashMap<String, String[]>();

				// get all the settings and their values
				params = request.getParameterNames();
				while (params.hasMoreElements())
				{
					String name = params.nextElement();

					// ignore the main params (the ones that go us here)
					if (name.equalsIgnoreCase(PARAM_ACTION))
						continue;

					String[] value = request.getParameterValues(name);

					advSettings.put(name, value);
				}

				settings = dic.getAccessListSettings();

				processAdvancedSettings(settings, advSettings);

				// try to apply the settings
				if (dic.tryAccessListSettings(settings))
				{
					dic.setAccessListSettings(settings);

					Services svcs = Services.getInstance();
					svcs.saveSettings();
					// TODO force the reload of the IAccessList implementation for the RMI interface

					// send	the client back the to previous page
					response.sendRedirect(Session.getLastVisitedURL(request));
				}
				else
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters!");
			break;

			case ACTION_CODE_SET_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_SETTINGS:
				advSettings = new HashMap<String, String[]>();

				// get all the settings and their values
				params = request.getParameterNames();
				while (params.hasMoreElements())
				{
					String name = params.nextElement();

					// ignore the main params (the ones that go us here)
					if (name.equalsIgnoreCase(PARAM_ACTION))
						continue;

					String[] value = request.getParameterValues(name);

					advSettings.put(name, value);
				}

				settings = dic.getSOPClassGlobalSettings();

				processAdvancedSettings(settings, advSettings);

				// try to apply the settings
				if (dic.trySOPClassGlobalSettings(settings))
				{
					dic.setSOPClassGlobalSettings(settings);

					Services svcs = Services.getInstance();
					svcs.saveSettings();

					// send	the client back the to previous page
					response.sendRedirect(Session.getLastVisitedURL(request));
				}
				else
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters!");
			break;
			
			case ACTION_CODE_SET_STORAGE_SERVICES:

				String requestParam = request.getParameter("MOVES");

				if(requestParam == null){
					response.sendError(401,"No Moves Defined");
					return;
				}
				
				ServerSettings set = ServerSettings.getInstance();
				ArrayList<MoveDestination> nmoves = new ArrayList<MoveDestination>();
				
				JSONArray moves = JSONArray.fromObject(requestParam);
				Iterator it = moves.iterator();
				while(it.hasNext()){
					JSONObject obj = (JSONObject) it.next();
					
					try{
						String aet = obj.getString("AETitle");
						String ip = obj.getString("ipAddrs");
						int port = obj.getInt("port");
						
						if( StringUtils.isNotBlank(aet) && StringUtils.isNotBlank(ip) && port > 0){
							nmoves.add(new MoveDestination(aet, ip, port));	
						}	
					}catch(JSONException ex){
						
					}			
				}
				
				set.setMoves(nmoves);
				
				XMLSupport _xml = new XMLSupport();
				_xml.printXML();
				_xml = null;
				
				break;

			default:
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action!");
				return;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		doGet(request, response);
	}

	/**
	 * Based on the request, returns a String containing a form with all the settings inputs boxes regarding the indexing options.
	 * These boxes are rendered/specified in accordance with the each setting value type reported by the plugin/service.
	 *
	 * @param request the servlet request object.
	 * @param brokerURL the URL of the broker that will apply the settings after receiving this forms post.
	 * @param elementID the ID of this HTML form, can be null.
	 * @return a String containing the HTML structure for the form with all the settings.
	 */
	public static String getHTMLIndexingAdvancedSettingsForm(HttpServletRequest request, String brokerURL, String elementID) throws IOException
	{
		String result = "";

		if ((elementID == null) || elementID.trim().isEmpty())
			result += "<form ";
		else
			result += "<form id=\"" + elementID + "\" ";
		result += "action=\"" + brokerURL + "\" method=\"get\">";

		result += "<input type=\"hidden\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_SET_ADVANCED_SETTINGS + "\" />";

		result += "<table class=\"table table-hover\"><tbody>";

		HashMap<String, Object> settings = dic.getIndexingSettings();
		HashMap<String, String> settingsHelp = dic.getIndexingSettingsHelp();

		Services svcs = Services.getInstance();

		// just to avoid any unwanted exceptions, in case a plugin misbehaves
		if (settings == null)
			settings = new HashMap<String, Object>();
		if (settingsHelp == null)
			settingsHelp = new HashMap<String, String>();

		// create a table row for each setting (includes name, value/type and help, if available)
		for (Map.Entry<String, Object> setting : settings.entrySet())
		{
			String key = setting.getKey();
			Object value = setting.getValue();
			String help = settingsHelp.get(key);

			result += svcs.getHTMLAdvancedSettingsFormRow(key, value, help);
		}

		result += "</tbody></table><br />";

		result += "<input type=\"submit\" value=\"Apply Settings\" class=\"btn btn-primary\"/>";
		result += "</form>";

		return result;
	}

	/**
	 * Based on the request, returns a String containing a form with all the settings inputs boxes regarding the Dicoogle Access List settings.
	 * These boxes are rendered/specified in accordance with the each setting value type reported by the plugin/service.
	 *
	 * @param request the servlet request object.
	 * @param brokerURL the URL of the broker that will apply the settings after receiving this forms post.
	 * @param elementID the ID of this HTML form, can be null.
	 * @return a String containing the HTML structure for the form with all the settings.
	 */
	public static String getHTMLAccessListSettingsForm(HttpServletRequest request, String brokerURL, String elementID) throws IOException
	{
		String result = "";

		if ((elementID == null) || elementID.trim().isEmpty())
			result += "<form ";
		else
			result += "<form id=\"" + elementID + "\" ";
		result += "action=\"" + brokerURL + "\" method=\"get\">";

		result += "<input type=\"hidden\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_SET_ACCESS_LIST_SETTINGS + "\" />";

		result += "<table class=\"table table-hover\"><tbody>";

		HashMap<String, Object> settings = dic.getAccessListSettings();
		HashMap<String, String> settingsHelp = dic.getAccessListSettingsHelp();

		Services svcs = Services.getInstance();

		// just to avoid any unwanted exceptions, in case a plugin misbehaves
		if (settings == null)
			settings = new HashMap<String, Object>();
		if (settingsHelp == null)
			settingsHelp = new HashMap<String, String>();

		// create a table row for each setting (includes name, value/type and help, if available)
		for (Map.Entry<String, Object> setting : settings.entrySet())
		{
			String key = setting.getKey();
			Object value = setting.getValue();
			String help = settingsHelp.get(key);

			result += svcs.getHTMLAdvancedSettingsFormRow(key, value, help);
		}

		result += "</tbody></table><br />";

		result += "<input type=\"submit\" value=\"Apply Settings\" class=\"btn btn-primary\"/>";
		result += "</form>";

		return result;
	}

	/**
	 * Based on the request, returns a String containing a form with all the settings inputs boxes regarding the Dicoogle Global SOP Classes Transfer Storage settings.
	 * These boxes are rendered/specified in accordance with the each setting value type reported by the plugin/service.
	 *
	 * @param request the servlet request object.
	 * @param brokerURL the URL of the broker that will apply the settings after receiving this forms post.
	 * @param elementID the ID of this HTML form, can be null.
	 * @return a String containing the HTML structure for the form with all the settings.
	 */
	public static String getHTMLGlobalTransferStorageSettingsForm(HttpServletRequest request, String brokerURL, String elementID) throws IOException
	{
		String result = "";

		if ((elementID == null) || elementID.trim().isEmpty())
			result += "<form ";
		else
			result += "<form id=\"" + elementID + "\" ";
		result += "action=\"" + brokerURL + "\" method=\"get\">";

		result += "<input type=\"hidden\" name=\"" + PARAM_ACTION + "\" value=\"" + ACTION_SET_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_SETTINGS + "\" />";

		result += "<table class=\"table table-condensed\"><tbody>";
		
		HashMap<String, Object> settings = dic.getSOPClassGlobalSettings();
		HashMap<String, String> settingsHelp = dic.getSOPClassGlobalSettingsHelp();

		Services svcs = Services.getInstance();

		// just to avoid any unwanted exceptions, in case a plugin misbehaves
		if (settings == null)
			settings = new HashMap<String, Object>();
		if (settingsHelp == null)
			settingsHelp = new HashMap<String, String>();

		// create a table row for each setting (includes name, value/type and help, if available)
		for (Map.Entry<String, Object> setting : settings.entrySet())
		{
			String key = setting.getKey();
			Object value = setting.getValue();
			String help = settingsHelp.get(key);
			
			result += svcs.getHTMLAdvancedSettingsFormRow(key, value, help);
		}
		
		result += "</tbody></table><br />";

		result += "<input type=\"submit\" value=\"Apply Settings\" class=\"btn btn-primary\"/>";
		result += "</form>";

		return result;
	}
}
