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
package pt.ua.dicoogle.server.web.management;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.LoggerFactory;


import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * A wrapper used to manage the indexer remotely through the web environment/app.
 *
 * @author António Novo <antonio.novo@ua.pt>
 */
public class Indexer{
	/**
	 * The current instance of this class.
	 */
	private static Indexer instance;

	/**
	 * Pointer to a object that maintain the IndexEngine information.
	 */

	private static ServerSettings cfg;
        
        private static List<Task> taskList;


	/**
	 * The name of the setting that indicates the Dicoogle Directory Monitorization.
	 */
	public static final String SETTING_DIRECTORY_MONITORIZATION_NAME = "Dicoogle Directory Monitorization";
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
	 * The help of the Indexing Effort setting.
	 */
	public static final String SETTING_INDEXING_EFFORT_HELP = "How agressive should the indexing effort be.\n\n0 - Lower\n100 - Intensive";

	private Indexer(){
		
		cfg = ServerSettings.getInstance();
                taskList = new ArrayList<Task>();
	}

	/**
	 * Returns the current instance of this class.
	 *
	 * @return the current instance of this class.
	 */
	public synchronized static Indexer getInstance(){
		if (instance == null)
			instance = new Indexer();

		return instance;
	}

	/**
	 * Returns a Map containing the current settings.
	 *
	 * @return a Map containing the current settings.
	 */
	public HashMap<String, Object> getSettings()
	{
		HashMap<String, Object> settings = new HashMap<String, Object>();

		//settings.put(SETTING_DIRECTORY_MONITORIZATION_NAME, new ServerDirectoryPath(cfg.getDicoogleDir()));
		//settings.put(SETTING_INDEXING_EFFORT_NAME, new RangeInteger(0, 100, cfg.getIndexerEffort()));
		settings.put(SETTING_THUMBNAILS_SIZE_NAME, cfg.getThumbnailsMatrix()); // TODO add more descritive type
		settings.put(SETTING_SAVE_THUMBNAILS_NAME, cfg.getSaveThumbnails());
		settings.put(SETTING_INDEX_ZIP_FILES_NAME, cfg.isIndexZIPFiles());

		return settings;
	}

	/**
	 * Tries to set the supplied settings as the new settings.
	 *
	 * @param settings a Map containing the new settings.
	 * @return true if all settings were successfully changed to the ones supplied, false otherwise (no settings changed).
	 */
	public boolean setSettings(HashMap<String, Object> settings)
	{
		// validate the settings
		boolean valid = false;
		try
		{
			valid = trySettings(settings);
		}
		catch (/*InvalidSettingValueException ex*/ Exception ex)
		{
			// at least one of the setting is invalid, abort
			return false;
		}
		if (! valid) //settings not valid due to unknown reason, also abort
			return false;

		// all the settings were correctly validated, so apply them all
		//cfg.setDicoogleDir(((ServerDirectoryPath) settings.get(SETTING_DIRECTORY_MONITORIZATION_NAME)).getPath());
		//cfg.setIndexerEffort(((RangeInteger) settings.get(SETTING_INDEXING_EFFORT_NAME)).getValue());
		cfg.setThumbnailsMatrix((String) settings.get(SETTING_THUMBNAILS_SIZE_NAME));
		cfg.setSaveThumbnails(((Boolean) settings.get(SETTING_SAVE_THUMBNAILS_NAME)).booleanValue());
		cfg.setIndexZIPFiles(((Boolean) settings.get(SETTING_INDEX_ZIP_FILES_NAME)).booleanValue());

		// return success
		return true;
	}

	/**
	 * Validates the settings supplied and checks if they are valid ones.
	 *
	 * @param map a Map containing the settings.
	 * @return true if all the settings are valid.
	 * @throws InvalidSettingValueException if at least one of the settings is invalid.
	 */
	public boolean trySettings(HashMap<String, Object> map) /*throws InvalidSettingValueException*/
	{
		// TODO remmember to check for null, all the params are supplied, all have valid types, and oly then all have valid values

		return true;
	}

	/**
	 * Returns the settings help.
	 *
	 * @return a HashMap containing the setting help.
	 */
	public HashMap<String, String> getSettingsHelp(){
		HashMap<String, String> settings = new HashMap<String, String>();
		// right now this is the only settingg that needs some help describing it
		settings.put(SETTING_INDEXING_EFFORT_NAME, SETTING_INDEXING_EFFORT_HELP);
		return settings;
	}

	/**
	 * Starts the indexing process.
	 */
	public void startIndexing(){
		File dir = new File(cfg.getDicoogleDir());
		if ((dir == null) || (! dir.isDirectory()))
			return;
		String path = dir.getAbsolutePath();
		if (path == null)
			return;
                
                URI uri = null;
                try {
                    uri = new URI(path);
                } catch (URISyntaxException ex) {
                    LoggerFactory.getLogger(Indexer.class).error(ex.getMessage(), ex);
                }
                if (uri != null)
                {
                    List<Task<Report>> tmpList = PluginController.getInstance().index(uri);
                    
                    for (Task _t : tmpList)
                    {
                        taskList.add(_t);
                    }
                    
                }
	}

	/**
	 * Stops the indexing process.
	 */
	public void stopIndexing()
	{
            
            for (Task _t : taskList)
            {
                        _t.cancel(true);
            }
                    
	}

	/**
	 * Returns if the Index Engine is currently indexing.
	 *
	 * @return if the Index Engine is currently indexing.
	 */
	public boolean isIndexing(){
		
            
            int numberOfActiveTasks = 0;
            for (Task _t : taskList)
            {
                if (_t.getProgress()<100)
                {
                    numberOfActiveTasks++;
                }
            }
            return numberOfActiveTasks!=0;
	}

	/**
	 * Returns the current progress (percentual) of the indexing operation.
	 *
	 * @return the current progress (percentual) of the indexing operation.
	 */
	public int indexingPercentCompleted(){
		
            
            int numberOfTasks = 0;
            float all=0;
            for (Task _t : taskList)
            {
                
                all = all + _t.getProgress();
                numberOfTasks++;
                
            }
            return (int)(all*100.0/(100*numberOfTasks));
            
	}

	/**
	 * Sets the Dicoogle indexing path.
	 *
	 * @param path the Dicoogle indexing path.
	 */
	public void setDicooglePath(String path){
		cfg.setDicoogleDir(path);
	}

	/**
	 * Gets the Dicoogle indexing path.
	 *
	 * @return the Dicoogle indexing path.
	 */
	public String getDicooglePath(){
		return cfg.getDicoogleDir();
	}
}
