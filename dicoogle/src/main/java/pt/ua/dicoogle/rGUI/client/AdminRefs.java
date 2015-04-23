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
package pt.ua.dicoogle.rGUI.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.rGUI.interfaces.IAdmin;
import pt.ua.dicoogle.rGUI.RFileBrowser.IRemoteFileSystem;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IAccessList;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IActiveSessions;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDirectory;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IIndexOptions;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ILogs;
import pt.ua.dicoogle.rGUI.interfaces.controllers.INetworkInterfaces;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPendingMessages;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerAdmin;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerUser;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IQRServers;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IQueryRetrieve;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISOPClass;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IServices;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IStartupServ;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ITaskList;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IUsersManager;

/**
 * Esta classe é responsável por ir buscar as referências para os objectos remotos da administração
 * e guardar as referências para esses objectos
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class AdminRefs {

    private ILogs logs;
    private IQRServers qrservers;
    private IServices services;
    private IStartupServ startupServ;
    private IQueryRetrieve queryRetrieve;
    private IAccessList accessList;
    private IIndexOptions indexOptions;
    private ISOPClass sopClass;
    private IDirectory directorySettings;
    private IUsersManager usersManager;
    private IActiveSessions activeSessions;
    private ITaskList taskList;
    private IPendingMessages pendingMessages;
    private IAdmin admin;
    private INetworkInterfaces networkInterfaces;
    private IPluginControllerAdmin pluginController;

    private static AdminRefs instance;
    private static Semaphore sem = new Semaphore(1, true);

    public static synchronized AdminRefs getAdminRefs(final IAdmin admin) {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new AdminRefs(admin);
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(ClientCore.class).error(ex.getMessage(), ex);
        }
        return instance;
    }

    public static AdminRefs getInstance() {
        return instance;
    }

    private AdminRefs(IAdmin admin) {
        this.admin = admin;

        getRefs();
    }

    /**
     * This function is responsible for obtaining references to Remote administration objects
     */
    private void getRefs() {
        class GetReferences {

            public void run() {
                try {
                    //System.out.println("Starting to adquire Admin Refs");

                    
                    qrservers = admin.getQRServers();
                    logs = admin.getLogs();
                    services = admin.getServices();
                    startupServ = admin.getStartupServ();
                    queryRetrieve = admin.getQueryRetrive();
                    accessList = admin.getAccessList();
                    indexOptions = admin.getIndexOptions();
                    sopClass = admin.getSOPClass();
                    directorySettings = admin.getDirectorySettings();
                    usersManager = admin.getUsersManager();
                    activeSessions = admin.getActiveSessions();
                    taskList = admin.getTaskList();
                    pendingMessages = admin.getPendingMessages();
                    networkInterfaces = admin.getNetworkInterface();
                    pluginController = admin.getPluginController();
                    
                    //System.out.println("Admin Refs Adquired");
                } catch (RemoteException ex) {
                    LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);

                    //TODO: se falhar, ter?? implica????es... o programa cliente deve parar
                }
            }
        }
        GetReferences getRefs = new GetReferences();

        //starts the Thread that will get the remote administrations object references
        getRefs.run();
    }

    /**
     * @return the logs
     */
    public ILogs getLogs() {
        return logs;
    }

    /**
     * @return the qrservers
     */
    public IQRServers getQRservers() {
        return qrservers;
    }

    /**
     * @return the services
     */
    public IServices getServices() {
        return services;
    }

    /**
     * @return the startup Services
     */
    public IStartupServ getStartupServices() {
        return startupServ;
    }

    /**
     * @return the QueryRetrieve configuration object reference
     */
    public IQueryRetrieve getQueryRetrieve() {
        return queryRetrieve;
    }

    /**
     * @return the Access List configuration object reference
     */
    public IAccessList getAccessList() {
        return accessList;
    }

    /**
     * @return the IndexOptions configuration object reference
     */
    public IIndexOptions getIndexOptions() {
        return indexOptions;
    }

    /**
     * @return the IndexOptions configuration object reference
     */
    public ISOPClass getSOPClass() {
        return sopClass;
    }

    /**
     * @return the Directory Settings configuration object reference
     */
    public IDirectory getDirectorySettings() {
        return directorySettings;
    }

    /**
     *
     * @return the UsersManager object reference
     */
    public IUsersManager getUsersManager(){
        return usersManager;
    }

    /**
     *
     * @return the ActiveSessions object reference
     */
    public IActiveSessions getActiveSessions(){
        return activeSessions;
    }

    /**
     *
     * @return the Pending Messages object reference
     */
    public IPendingMessages getPendingMessages(){
        return pendingMessages;
    }


    public INetworkInterfaces getNetworkInterfaces()
    {
        return networkInterfaces;
    }


    /**
     * Logout Admin from serv
     *
     * @return true if logout succeeded
     */
    public boolean logout() {
        try {
            admin.logout();

            return true;
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);

            return false;
        }
    }

    public boolean saveSettings(){
        try {
            admin.saveSettings();

            return true;
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);

            return false;
        }
    }
    public boolean unsavedSettings(){
        try {
            return admin.unsavedSettings();
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);
        }
        return false;
    }

    public IRemoteFileSystem getRFS(){
        try {
            return admin.getRFS();
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);
        }
        
        return null;
    }

    public String getDefaultFilePath(){
        try {
            return admin.getDefaultFilePath();
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Index new file or folder
     *
     * @param Path - file or folter path
     */
    public void index(String Path, boolean resume){
        try {
            indexOptions.index(Path, resume);
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);
        }
    }

    /**
     * Re-Index one file
     *
     * @param Path - file or folter path
     */
    public void reIndex(ArrayList<String> list){
        try {
            indexOptions.reIndex(list);
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);
        }
    }


    public void shutdownServer(){
        try {
            admin.shutdownServer();
        } catch (RemoteException ex) {
            //LoggerFactory.getLogger(AdminRefs.class).error(ex.getMessage(), ex);
        }
        //System.out.println("The server is Shutting Down");
    }

    /**
     * @return the taskList
     */
    public ITaskList getTaskList() {
        return taskList;
    }

    public IPluginControllerAdmin getPluginController()
    {
        return pluginController;
    }

       /*
     * Attention, we should not pass paths directly as arguments 
     * we should instead use a datasource, since the file can be stored somewhere else
     * or even in memory...
     * 
     */
    public void cbirIndex(String path) {
        //a list containing all indexation plugins
        if(pluginController == null) return;

        //try{
            //pluginController.cbirIndex(path);
        //}
       /* catch(RemoteException e){
            System.err.println("Oh dear...");
            System.err.println(e);
        }*/
    }    
}
