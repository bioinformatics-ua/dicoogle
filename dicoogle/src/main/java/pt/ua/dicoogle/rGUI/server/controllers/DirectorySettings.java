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

//import com.sun.tools.javac.util.DefaultFileManager.ZipArchive;
import pt.ua.dicoogle.server.ControlServices;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDirectory;
import pt.ua.dicoogle.server.DicomDirCreator;

/**
 * Controller of Directory Settings
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class DirectorySettings implements IDirectory {

    private static final Logger logger = LoggerFactory.getLogger(DirectorySettings.class);
    
    private String storagePath;
    private String dicoogleDir;
    private int effort;
    private boolean saveTumbnails;
    private String thumbnailsMatrix;
    private boolean indezZip;
    private boolean gzipStorage;
    private boolean indexAnonymous;
    private boolean monitorWatcher;
    

    private ServerSettings settings;
    
    private static DirectorySettings instance ;

    public static synchronized DirectorySettings getInstance()
    {

        if (instance == null) 
            instance = new DirectorySettings();

        return instance;
    }

    private DirectorySettings()
    {
        settings = ServerSettings.getInstance();

        loadSettings();
    }

    /**
     * Load settings from ServerSettings
     */
    public void loadSettings(){
        storagePath = settings.getPath();
        dicoogleDir = settings.getDicoogleDir();
        effort = settings.getIndexerEffort();
        saveTumbnails = settings.getSaveThumbnails();
        thumbnailsMatrix = settings.getThumbnailsMatrix();
        indezZip = settings.isIndexZIPFiles();
        gzipStorage = settings.isGzipStorage();
        monitorWatcher = settings.isMonitorWatcher();
        this.indexAnonymous = settings.isIndexAnonymous();
    }

    /**
     * Save settings to ServerSettings
     */
    public void saveSettings(){
        settings.setPath(storagePath);
        settings.setDicoogleDir(dicoogleDir);
        settings.setIndexerEffort(effort);
        settings.setSaveThumbnails(saveTumbnails);
        settings.setThumbnailsMatrix(thumbnailsMatrix);
        settings.setIndexZIPFiles(saveTumbnails);
        settings.setIndexZIPFiles(indezZip);
        settings.setGzipStorage(gzipStorage);
        settings.setMonitorWatcher(isMonitorWatcher());
        settings.setIndexAnonymous(indexAnonymous);
    }


    /**
     *
     * @return  true - if there are unsaved settings ( != ServerSettings)
     *          false - not
     */
    public boolean unsavedSettings(){
        
        if(!storagePath.equals(settings.getPath()) || !dicoogleDir.equals(settings.getDicoogleDir()) ||
                effort != settings.getIndexerEffort() || saveTumbnails != settings.getSaveThumbnails() ||
                monitorWatcher != settings.isMonitorWatcher() || 
                indezZip != settings.isIndexZIPFiles() || 
                indexAnonymous != settings.isIndexAnonymous() || 
                !thumbnailsMatrix.equals(settings.getThumbnailsMatrix()))
            return true;

        return false;
    }

    /**
     *
     * @param path
     * @return  0 - Ok
     *          1 - Not a directory
     *          2 - Can't read
     *          3 - Can't write
     * @throws RemoteException
     */
    @Override
    public int setStoragePath(String path) throws RemoteException {
        File file = new File(path);

        if(!file.isDirectory())
            return 1;

        if(!file.canRead())
            return 2;

        if(!file.canWrite())
            return 3;

        storagePath = path;
        
        return 0;
    }

    @Override
    public String getStoragePath() throws RemoteException {
        return storagePath;
    }

    @Override
    public int setDicoogleDir(String path) throws RemoteException {
        File file = new File(path);

        if(!file.isDirectory())
            return 1;

        if(!file.canRead())
            return 2;

        if(!file.canWrite())
            return 3;

        dicoogleDir = path;

        return 0;
    }

    @Override
    public String getDicoogleDir() throws RemoteException {
        return dicoogleDir;
    }

    @Override
    public boolean setIndexerEffort(int effort) throws RemoteException {
        if(effort < 0 || effort > 100)
            return false;

        this.effort = effort;
        
        return true;
    }

    @Override
    public int getIndexerEffort() throws RemoteException {
        return effort;
    }

    @Override
    public void setSaveThumbnails(boolean value) throws RemoteException {
        saveTumbnails = value;
    }

    @Override
    public boolean getSaveThumbnails() throws RemoteException {
        return saveTumbnails;
    }

    @Override
    public void setThumbnailsMatrix(String ThumbnailsMatrix) throws RemoteException {
        thumbnailsMatrix = ThumbnailsMatrix;
    }

    @Override
    public String getThumbnailsMatrix() throws RemoteException {
        return thumbnailsMatrix;
    }


    /**
     *
     * @return  0 - OK
     *          1 - Defined storage path is invalid
     *          2 - Server running
     * @throws RemoteException
     */
    @Override
    public int rebuildIndex() throws RemoteException {
        saveSettings();
        
        ControlServices serv = ControlServices.getInstance();

        if (!serv.storageIsRunning()) {
            File f = new File(storagePath);
            //DebugManager.getInstance().debug("Rebuild Search Index on: " + f.getAbsolutePath());
            
            if (!f.exists()) {
                return 1;
            }

            try {
                PluginController.getInstance().index(new URI(f.getAbsolutePath()));
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage(), ex);
            }
            
        } else
            return 2;

        return 0;
    }


    /**
     *
     * @return  0 - OK
     *          1 - Defined storage path is invalid
     *          2 - Server running
     * @throws RemoteException
     */
    @Override
    public int rebuildDICOMDir() throws RemoteException {
        saveSettings();

        ControlServices serv = ControlServices.getInstance();

        if (!serv.storageIsRunning()) {
            File f = new File(dicoogleDir);
            //DebugManager.getInstance().debug(f.getAbsolutePath());

            if (!f.exists()) {
                return 1;
            }

            DicomDirCreator dirc = new DicomDirCreator(dicoogleDir, "Dicoogle");
            dirc.dicomdir_rebuild();
        } else
            return 2;

        return 0;
    }

    @Override
    public boolean isIndexZip() throws RemoteException
    {
        return indezZip;
    }


    @Override
    public void setIndexZip(boolean value) throws RemoteException {
        indezZip = value;
    }

    /**
     * @return the monitorWatcher
     */
    public boolean isMonitorWatcher() {
        return monitorWatcher;
    }

    /**
     * @param monitorWatcher the monitorWatcher to set
     */
    public void setMonitorWatcher(boolean monitorWatcher) {
        this.monitorWatcher = monitorWatcher;
    }

    @Override
    public boolean isIndexAnonymous() throws RemoteException {
        return this.indexAnonymous;
    }

    @Override
    public void setIndexAnonymous(boolean value) throws RemoteException {
        this.indexAnonymous = value;
    }

    @Override
    public boolean isGZipStorage() throws RemoteException {
        return this.gzipStorage;
    }

    @Override
    public void setGZipStorage(boolean value) throws RemoteException {
        this.gzipStorage = value;
    }

}
