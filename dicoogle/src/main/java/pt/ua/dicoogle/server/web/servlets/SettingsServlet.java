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
import java.util.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;
import pt.ua.dicoogle.sdk.settings.server.ServerSettings;
import pt.ua.dicoogle.sdk.settings.types.*;
import pt.ua.dicoogle.server.TransfersStorage;
import pt.ua.dicoogle.server.web.auth.Session;
import pt.ua.dicoogle.server.web.management.SOPClassSettings;

import static pt.ua.dicoogle.sdk.settings.Utils.parseCheckBoxValue;
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
				HashMap<String, String[]> advSettings = new HashMap<>();

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

				HashMap<String, Object> settings = getIndexingSettings();

				processAdvancedSettings(settings, advSettings);

				// try to apply the settings
				setIndexingSettings(settings);

				ServerSettingsManager.saveSettings();

				// send	the client back the to previous page
				response.sendRedirect(Session.getLastVisitedURL(request));
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

				settings = getAccessListSettings();

				processAdvancedSettings(settings, advSettings);

				// try to apply the settings
				setAccessListSettings(settings);

				ServerSettingsManager.saveSettings();
				// TODO force the reload of the IAccessList implementation for the RMI interface

				// send	the client back the to previous page
				response.sendRedirect(Session.getLastVisitedURL(request));
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

				settings = getSOPClassGlobalSettings();

				processAdvancedSettings(settings, advSettings);

				setSOPClassGlobalSettings(settings);

				ServerSettingsManager.saveSettings();

				// send	the client back the to previous page
				response.sendRedirect(Session.getLastVisitedURL(request));
			break;
			
			case ACTION_CODE_SET_STORAGE_SERVICES:

				String requestParam = request.getParameter("MOVES");

				if(requestParam == null){
					response.sendError(401,"No Moves Defined");
					return;
				}
				
				ServerSettings set = ServerSettingsManager.getSettings();
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
				
				set.getDicomServicesSettings().setMoveDestinations(nmoves);
				ServerSettingsManager.saveSettings();
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
	 * Tries to set the supplied settings as the new settings.
	 *
	 * @param settings a Map containing the new settings.
	 * @return true if all settings were successfully changed to the ones supplied, false otherwise (no settings changed).
	 */
	public void setIndexingSettings(HashMap<String, Object> settings)
	{
		ServerSettings.Archive cfg = ServerSettingsManager.getSettings().getArchiveSettings();

		// all the settings were correctly validated, so apply them all
		cfg.setWatchDirectory(((ServerDirectoryPath) settings.get(SETTING_DIRECTORY_MONITORIZATION_NAME)).getPath());
		cfg.setDirectoryWatcherEnabled(((Boolean) settings.get(SETTING_DIRECTORY_WATCHER_NAME)).booleanValue());
		cfg.setIndexerEffort(((RangeInteger) settings.get(SETTING_INDEXING_EFFORT_NAME)).getValue());
		cfg.setThumbnailSize((Integer) settings.get(SETTING_THUMBNAILS_SIZE_NAME));
		cfg.setSaveThumbnails(((Boolean) settings.get(SETTING_SAVE_THUMBNAILS_NAME)).booleanValue());
		//cfg.setIndexZIPFiles(((Boolean) settings.get(SETTING_INDEX_ZIP_FILES_NAME)).booleanValue());
	}

	/**
	 * Returns a Map containing the current settings.
	 *
	 * @return a Map containing the current settings.
	 */
	public HashMap<String, Object> getIndexingSettings()
	{
		ServerSettings.Archive cfg = ServerSettingsManager.getSettings().getArchiveSettings();
		HashMap<String, Object> settings = new LinkedHashMap<String, Object>();

		settings.put(SETTING_DIRECTORY_WATCHER_NAME, new Boolean(cfg.isDirectoryWatcherEnabled()));
		settings.put(SETTING_DIRECTORY_MONITORIZATION_NAME, new ServerDirectoryPath(cfg.getMainDirectory()));
		//settings.put(SETTING_INDEX_ZIP_FILES_NAME, cfg.isIndexZIPFiles());
		settings.put(SETTING_INDEXING_EFFORT_NAME, new RangeInteger(0, 100, cfg.getIndexerEffort()));
		settings.put(SETTING_SAVE_THUMBNAILS_NAME, cfg.getSaveThumbnails());
		settings.put(SETTING_THUMBNAILS_SIZE_NAME, cfg.getThumbnailSize());

		return settings;
	}

	/**
	 * Returns a Map containing the current settings.
	 *
	 * @return a Map containing the current settings.
	 */
	public HashMap<String, Object> getSOPClassGlobalSettings()
	{
		HashMap<String, Object> settings = new LinkedHashMap<String, Object>();

		Object[] set = getTransferSettingsForAllSOPClasses();

		ComboBox tristate = new ComboBox();
		tristate.addElement("As Is (No Change)", "");
		tristate.addElement("Allow All", "On"); // try to emulate a regular checkbox :)
		tristate.addElement("Forbid All", "Off");
		tristate.setCurrentByValue(""); // FIXME default is "no change", always (for the time being)
		settings.put(SETTING_SOP_CLASS_ACCEPT_ALL_NAME, tristate);
		settings.put(SETTING_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_NAME, set[1]);

		return settings;
	}

	/**
	 * Returns a Map containing the current settings.
	 *
	 * @return a Map containing the current settings.
	 */
	public HashMap<String, Object> getAccessListSettings()
	{
		ServerSettings cfg = ServerSettingsManager.getSettings();

		HashMap<String, Object> settings = new HashMap<>();

		settings.put(SETTING_SERVER_AE_TITLE_NAME, cfg.getDicomServicesSettings().getAETitle());
		settings.put(SETTING_PERMIT_ALL_AE_TITLES_NAME, new Boolean(cfg.getDicomServicesSettings().getAllowedAETitles().isEmpty()));
		Collection<String> allowedAETitles = cfg.getDicomServicesSettings().getAllowedAETitles();
		String[] caet = new String[allowedAETitles.size()];
		allowedAETitles.toArray(caet);
		DataTable clientPermitACL = new DataTable(1, caet.length);
		clientPermitACL.setColumnName(0, "Client AE Title");
		if (caet.length < 1)
		{
			clientPermitACL.addRow();
			clientPermitACL.setCellData(0, 0, "");
		}
		else
		{
			for (int i = 0; i < caet.length; i++)
				clientPermitACL.setCellData(i, 0, caet[i]);
		}
		settings.put(SETTING_CLIENT_PERMIT_ACCESS_LIST_NAME, clientPermitACL);

		return settings;
	}

	/**
	 * Tries to set the supplied settings as the new settings.
	 *
	 * @param settings a Map containing the new settings.
	 * @return true if all settings were successfully changed to the ones supplied, false otherwise (no settings changed).
	 */
	public boolean setAccessListSettings(HashMap<String, Object> settings)
	{
		ServerSettings cfg = ServerSettingsManager.getSettings();
		// all the settings were correctly validated, so apply them all
		cfg.getDicomServicesSettings().setAETitle((String) settings.get(SETTING_SERVER_AE_TITLE_NAME));
		DataTable clientPermitACL = (DataTable) settings.get(SETTING_CLIENT_PERMIT_ACCESS_LIST_NAME);
		List<String> caet = new ArrayList<>();
		if ((clientPermitACL.getRowCount() != 1) || (clientPermitACL.getCellData(0, 0) != ""))
			for (int i = 0; i < clientPermitACL.getRowCount(); i++)
				caet.add((String) clientPermitACL.getCellData(i, 0));
		cfg.getDicomServicesSettings().setAllowedAETitles(caet);

		// return success
		return true;
	}

	/**
	 * Returns the settings help.
	 *
	 * @return a HashMap containing the setting help.
	 */
	public HashMap<String, String> getSOPClassGlobalSettingsHelp()
	{
		HashMap<String, String> settings = new HashMap<String, String>();

		// TODO

		return settings;
	}

	/**
	 * Tries to set the supplied settings as the new settings.
	 *
	 * @param settings a Map containing the new settings.
	 * @return true if all settings were successfully changed to the ones supplied, false otherwise (no settings changed).
	 */
	public boolean setSOPClassGlobalSettings(HashMap<String, Object> settings)
	{
		// all the settings were correctly validated, so apply them all
		ComboBox tristate = (ComboBox) settings.get(SETTING_SOP_CLASS_ACCEPT_ALL_NAME);
		Boolean accepted = null;
		if (! tristate.getCurrentValue().isEmpty()) // NOTE: empty ("") means no change, see the getter for this settings
			accepted = new Boolean(parseCheckBoxValue(tristate.getCurrentValue()));
		StaticDataTable table = (StaticDataTable) settings.get(SETTING_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_NAME);

		setTransferSettingsForAllSOPClasses(accepted, table);

		// return success
		return true;
	}
	/**
	 * Returns a StaticDataTable containing the TransferStorages for a
	 * specific SOP Class UID.
	 *
	 * @param sopClassUID a SOP Class UID.
	 * @return an Object array containing as first elements a Boolean object
	 * indicating if this SOP Class is accepted and as second element a
	 * StaticDataTable that contains the TransferStorages for this specific
	 * SOP Class UID.
	 */
	public Object[] getTransferSettingsForSOPClass(String sopClassUID)
	{
		SOPClassSettings sops = SOPClassSettings.getInstance();
		TransfersStorage ts = sops.getSOPClassSettings(sopClassUID);

		// this settings will be returned on a StaticDataTable, to guarantee that the user does not change the entry count and only their values
		StaticDataTable table = new StaticDataTable(1, sops.getTransferSettings().size());
		// for each setting on the transfer storage settings...
		int i = 0;
		for (Map.Entry<String, String> entry : sops.getTransferSettings().entrySet())
		{
			// get its UID, "visual/regular" name and index (relative to the TS settings array)
			String uid = entry.getKey();
			String name = entry.getValue();
			int index = sops.getTransferSettingsIndex().get(uid).intValue();

			// create a HintedCheckbox with the info gotten above and the current value of the setting
			CheckboxWithHint box = new CheckboxWithHint(ts.getTS()[index], name, uid);
			// and addMoveDestination the checkbox to the data table
			table.setCellData(i, 0, box);

			i++;
		}

		// mount the return object array
		Object[] result = new Object[2];
		// and addMoveDestination the proper objects to it
		result[0] = new Boolean(ts.getAccepted());
		result[1] = table;

		return result;
	}

	/**
	 * Returns a StaticDataTable containing the TransferStorages settings
	 * for all SOP Classes UIDs.
	 *
	 * @return an Object array containing as first elements a Boolean object
	 * indicating if all SOP Classes are accepted and as second element a
	 * StaticDataTable that contains the TransferStorages for all SOP Classes
	 * UIDs.
	 */
	public Object[] getTransferSettingsForAllSOPClasses()
	{
		SOPClassSettings sops = SOPClassSettings.getInstance();

		// get the first SOP Class UID // FIXME using the first SOP Class settings as global does not accurately represent the common settings for all SOP classes, but it's close enough (if they get applied :) )
		String sopClassUID = sops.getSOPClasses().entrySet().iterator().next().getKey();
		// and its TransferStorage settings
		TransfersStorage ts = sops.getSOPClassSettings(sopClassUID);

		// this settings will be returned on a StaticDataTable, to guarantee that the user does not change the entry count and only their values
		StaticDataTable table = new StaticDataTable(1, sops.getTransferSettings().size());
		table.setColumnName(0, "Transfer Storage");
		// for each setting on the transfer storage settings...
		int i = 0;
		for (Map.Entry<String, String> entry : sops.getTransferSettings().entrySet())
		{
			// get its UID, "visual/regular" name and index (relative to the TS settings array)
			String uid = entry.getKey();
			String name = entry.getValue();
			int index = sops.getTransferSettingsIndex().get(uid).intValue();

			// create a HintedCheckbox with the info gotten above and the current value of the setting
			CheckboxWithHint box = new CheckboxWithHint(ts.getTS()[index], name, uid);
			// and addMoveDestination the checkbox to the data table
			table.setCellData(i, 0, box);

			i++;
		}

		// mount the return object array
		Object[] result = new Object[2];
		// and addMoveDestination the proper objects to it
		result[0] = new Boolean(ts.getAccepted());
		result[1] = table;

		return result;
	}

	/**
	 * Applies the Transfer Storage settings present on a StaticDataTable to
	 * a specific SOP Class UID.
	 *
	 * @param sopClassUID the target SOP Class UID.
	 * @param accepted if the SOP Class UID is accepted. Can be null and if so, the target accepted value will remain the same.
	 * @param table a StaticDataTable with the Transfer Storage settings.
	 */
	public void setTransferSettingsForSOPClass(String sopClassUID, Boolean accepted, StaticDataTable table)
	{
		SOPClassSettings sops = SOPClassSettings.getInstance();
		TransfersStorage ts = sops.getSOPClassSettings(sopClassUID);

		// for each transfer storage setting on the datatable...
		for (int i = 0 ; i < table.getRowCount(); i++)
		{
			// get the current row/cell setting
			CheckboxWithHint box = (CheckboxWithHint) table.getCellData(i, 0);

			// get its UID and index (relative to the TS settings array)
			String uid = box.getHint();
			int index = sops.getTransferSettingsIndex().get(uid).intValue();

			// set the transfer storge setting value
			ts.setTS(box.isChecked(), index);
			if (accepted != null)
				ts.setAccepted(accepted.booleanValue());
		}
	}

	/**
	 * Applies the Transfer Storage settings present on a StaticDataTable to
	 * all SOP Classes UIDs.
	 *
	 * @param accepted if all SOP Classes UIDs are accepted. Can be null and if so, the target accepted value will remain the same.
	 * @param table a StaticDataTable with the Transfer Storage settings.
	 */
	public void setTransferSettingsForAllSOPClasses(Boolean accepted, StaticDataTable table)
	{
		SOPClassSettings sops = SOPClassSettings.getInstance();

		HashMap<String, Boolean> allowed = new HashMap<String, Boolean>();

		// for each transfer storage setting on the datatable...
		for (int i = 0 ; i < table.getRowCount(); i++)
		{
			// get the current row/cell setting
			CheckboxWithHint box = (CheckboxWithHint) table.getCellData(i, 0);

			// get its UID
			String uid = box.getHint();

			allowed.put(uid, new Boolean(box.isChecked()));
		}

		// set the transfer storage settings
		sops.setAllSOPClassesSettings(accepted, allowed);
	}

	/**
	 * The name of the setting that indicates the Dicoogle Directory Monitorization.
	 */
	public static final String SETTING_DIRECTORY_MONITORIZATION_NAME = "Dicoogle Directory Monitorization";
	/**
	 * The name of the setting that indicates the Dicoogle Enable Directory Watcher.
	 */
	public static final String SETTING_DIRECTORY_WATCHER_NAME = "Enable Dicoogle Directory Watcher";
	/**
	 * The name of the setting that indicates the Indexing Effort.
	 */
	public static final String SETTING_INDEXING_EFFORT_NAME = "Indexing Effort";
	/**
	 * The name of the setting that indicates the Thumbnails Size.
	 */
	public static final String SETTING_THUMBNAILS_SIZE_NAME = "Thumbnails Size";
	/**
	 * The name of the setting that indicates if Thumbnails are to be Saved.
	 */
	public static final String SETTING_SAVE_THUMBNAILS_NAME = "Save Thumbnails";
	/**
	 * The name of the setting that indicates if ZIP files are to be Indexed.
	 */
	public static final String SETTING_INDEX_ZIP_FILES_NAME = "Index ZIP Files";

	/**
	 * The name of the setting that indicates the Server AE Title.
	 */
	public static final String SETTING_SERVER_AE_TITLE_NAME = "Server AE Title";
	/**
	 * The name of the setting that indicates the Permit All AE Titles.
	 */
	public static final String SETTING_PERMIT_ALL_AE_TITLES_NAME = "Permit All AE Titles";
	/**
	 * The name of the setting that indicates the Client Permit Access List.
	 */
	public static final String SETTING_CLIENT_PERMIT_ACCESS_LIST_NAME = "Client Permit Access List";

	/**
	 * SOP Class/Transfer Storage.
	 */

	/**
	 * The name of the setting that indicates if Accept All SOP Classes.
	 */
	public static final String SETTING_SOP_CLASS_ACCEPT_ALL_NAME = "Accept All";
	/**
	 * The name of the setting that indicates the Global Transfer Storage settings.
	 */
	public static final String SETTING_SOP_CLASS_GLOBAL_TRANSFER_STORAGE_NAME = "Global Transfer Storage";

}
