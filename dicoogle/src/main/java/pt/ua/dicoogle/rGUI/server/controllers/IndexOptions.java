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
package pt.ua.dicoogle.rGUI.server.controllers;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.core.TagsXML;
import pt.ua.dicoogle.core.dicom.PrivateDictionary;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IIndexOptions;
import pt.ua.dicoogle.rGUI.server.DicoogleScan;
import pt.ua.dicoogle.sdk.utils.TagValue;
import pt.ua.dicoogle.sdk.utils.TagsStruct;

/**
 * Controller of Index Options Settings
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class IndexOptions implements IIndexOptions {

    //private HashMap<Integer, TagValue> dimFields;
    //private ArrayList<String> modalities;
    //private HashMap<Integer, TagValue> manualFields;
    private boolean isIndexAllModalities;

    private static Semaphore sem = new Semaphore(1, true);
    private static IndexOptions instance = null;

    private TagsStruct tags = TagsStruct.getInstance();
    private boolean saved = true;

    public static synchronized IndexOptions getInstance()
    {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new IndexOptions();
            }
            sem.release();
        } catch (InterruptedException ex) {
        }
        return instance;
    }
    
    private IndexOptions(){
        loadSettings(); 
    }

    /**
     * Load settings from TagsStruct
     */
    public void loadSettings(){
        isIndexAllModalities = tags.isIndexAllModalitiesEnabled();
    }

    /**
     * Save the Settings related to IndexOptions (TagsStruct)
     *
     * Print TagsXML
     */
    public void saveSettings(){
    	saved = true;
        tags.enableIndexAllModalities(isIndexAllModalities);

        // Flush to XML file
        TagsXML ts = new TagsXML();
        ts.printXML();
    }


    /**
     *
     * @return  true - if there are unsaved settings ( != TagsStruct)
     *          false - not
     */
    public boolean unsavedSettings(){
    	return saved;
    }

    @Override
    public HashMap<String, ArrayList<TagValue>> getDIMFields() throws RemoteException {
        //DebugManager.getSettings().debug("Getting DIM Fields");
        /** Get groups **/
        HashMap<String, ArrayList<TagValue>> groupTable = new HashMap<String, ArrayList<TagValue>>();      
        for(TagValue tag : tags.getDIMFields()){
            if (groupTable.containsKey(tag.getGroup())) {
                groupTable.get(tag.getGroup()).add(tag);

            } else {
                ArrayList<TagValue> temp = new ArrayList<TagValue>();
                temp.add(tag);
                groupTable.put(tag.getGroup(), temp);
            }
        }
        
        return groupTable;
    }

    @Override
    public ArrayList<String> getModalities() throws RemoteException {
    	ArrayList<String> arr = new ArrayList<>(tags.getModalities());    	
        return arr;
    }

    @Override
    public boolean addModality(String modality) throws RemoteException {
    	saved = false;
    	boolean r = !tags.containsModality(modality);
    	tags.addModality(modality);
    	return r;
    }

    @Override
    public boolean removeModality(String modality) throws RemoteException {
    	saved = false;
    
        return tags.removeModality(modality);
    }

    @Override
    public HashMap<String, ArrayList<TagValue>> getManualFields() throws RemoteException {
        /** Get groups **/
        HashMap<String, ArrayList<TagValue>> groupTable = new HashMap<String, ArrayList<TagValue>>();
        for (TagValue tag : tags.getOtherFields()) {
            //DebugManager.getSettings().debug(">> Grouping the Others Tags in MainWindow Notebook");

            if (groupTable.containsKey(tag.getGroup())) {
                groupTable.get(String.valueOf(tag.getGroup())).add(tag);

            } else {
                ArrayList<TagValue> temp = new ArrayList<TagValue>();
                temp.add(tag);
                groupTable.put(tag.getGroup(), temp);
            }
        }

        return groupTable;
    }

    @Override
    public boolean addManualField(int group, int subGroup, String name) throws RemoteException {
    	saved = false;
        String tag = String.valueOf(group) + String.valueOf(subGroup);
        TagValue t = new TagValue(Integer.parseInt(tag, 16), name);       
        
        return tags.addPrivateField(t);
    }

    @Override
    public boolean removeManualField(int group, int subGroup) throws RemoteException {
    	saved = false;
        String tag = String.valueOf(group) + String.valueOf(subGroup);

        return tags.removePrivateField(Integer.parseInt(tag, 16));
    }

    @Override
    public boolean isIndexAllModalities() throws RemoteException {
        return isIndexAllModalities;
    }

    @Override
    public void setIndexAllModalities(boolean value) throws RemoteException {
    	saved = false;
        isIndexAllModalities = value;
    }




    /**
     * Index new file or folder
     *
     * @param Path - file or folter path
     * @throws RemoteException
     */
    @Override
    public void index(String Path, boolean resume) throws RemoteException {
        Logs.getInstance().addServerLog("Start indexing: " + Path);
        
        DicoogleScan scandir = new DicoogleScan(Path);
        scandir.scan(resume);
    }

    /**
     * Indexes even if the file is already indexed
     *
     * @param path
     * @throws RemoteException
     */
    @Override
    public void reIndex(ArrayList<String> list) throws RemoteException {
        Logs.getInstance().addServerLog("Start reIndexing " + list.size() + " file(s)");
        
        class REIndex extends Thread{
            private ArrayList<String> list;

            public REIndex(ArrayList<String> list){
                this.list = list;
            }

            @Override
            public void run(){
                for(String Path: list)
                    try {
                        PluginController.getInstance().index(new URI(Path));
                    } catch (URISyntaxException ex) {
                        LoggerFactory.getLogger(IndexOptions.class).error(ex.getMessage(), ex);
                    }
            }
        }

        //reIndex all files
        (new REIndex(list)).start();
    }

    @Override
    public void removeFilesFromIndexer(ArrayList<String> files, boolean deleteFiles) throws RemoteException {
        Logs.getInstance().addServerLog("Start removing " + files.size() + " file(s)");

        class DeleteFiles extends Thread{
            private ArrayList<String> list;
            private boolean delete;

            public DeleteFiles(ArrayList<String> list, boolean delete){
                this.list = list;
                this.delete = delete;
            }

            @Override
            public void run(){
                for(String Path: list)
                {
                    try {
                        PluginController.getInstance().index(new URI(Path));
                    } catch (URISyntaxException ex) {
                        LoggerFactory.getLogger(IndexOptions.class).error(ex.getMessage(), ex);
                    }
                
                    if (delete)
                    {
                        
                        File f = new File(Path);
                        f.delete();
                    }
                }
            }
        }

        (new DeleteFiles(files, deleteFiles)).start();

    }

    @Override
    public boolean removeDictionary(String dic) throws RemoteException {
        
        TagsStruct.getInstance().removeDicionary(dic);
        
        return true;
        
    }

    @Override
    public boolean addDictionary(String dic) throws RemoteException {
        
        TagsStruct.getInstance().addDicionary(dic);
        PrivateDictionary pd = new PrivateDictionary();
        pd.parse(dic);
        return true;
        
    }

}
