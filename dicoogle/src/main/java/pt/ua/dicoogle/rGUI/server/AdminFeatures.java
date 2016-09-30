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
package pt.ua.dicoogle.rGUI.server;

import java.rmi.NoSuchObjectException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import pt.ieeta.anonymouspatientdata.core.Anonymous;

import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.rGUI.interfaces.controllers.INetworkInterfaces;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerAdmin;

import pt.ua.dicoogle.server.ControlServices;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.core.XMLSupport;
import pt.ua.dicoogle.rGUI.MultihomeSslRMIClientSocketFactory;

import pt.ua.dicoogle.rGUI.RFileBrowser.IRemoteFileSystem;
import pt.ua.dicoogle.rGUI.RFileBrowser.RemoteFileSystemServer;

import pt.ua.dicoogle.rGUI.interfaces.IAdmin;
import pt.ua.dicoogle.rGUI.interfaces.IUser;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IAccessList;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDirectory;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IIndexOptions;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ILogs;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IQRServers;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IQueryRetrieve;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISOPClass;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IServices;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IStartupServ;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ITaskList;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IUsersManager;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IActiveSessions;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPendingMessages;

import pt.ua.dicoogle.rGUI.server.controllers.AccessList;
import pt.ua.dicoogle.rGUI.server.controllers.DirectorySettings;
import pt.ua.dicoogle.rGUI.server.controllers.IndexOptions;
import pt.ua.dicoogle.rGUI.server.controllers.Logs;
import pt.ua.dicoogle.rGUI.server.controllers.PendingMessages;
import pt.ua.dicoogle.rGUI.server.controllers.QRServers;
import pt.ua.dicoogle.rGUI.server.controllers.QueryRetrieve;
import pt.ua.dicoogle.rGUI.server.controllers.SOPClass;
import pt.ua.dicoogle.rGUI.server.controllers.StartupServices;
import pt.ua.dicoogle.rGUI.server.controllers.TaskList;
import pt.ua.dicoogle.rGUI.server.controllers.UsersManager;
import pt.ua.dicoogle.rGUI.server.controllers.NetworkInterfaces;
import pt.ua.dicoogle.rGUI.server.controllers.PluginController4Admin;
import pt.ua.dicoogle.server.users.User;
import pt.ua.dicoogle.server.users.UserSessions;

/**
 * This class serves to provide access to administration features
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class AdminFeatures implements IAdmin {

    private static AdminFeatures instance = null;

    private XMLSupport xmlSupport;

    private Timer timer;
    private static int timeoutTime = 15000; //15 seconds
    private TimerTask task;
    private boolean timeoutCanceled = false;

    private User user;

    private int port;

    private IUser userStub = null;     //reference to stub of UserFeatures

    public static synchronized AdminFeatures getInstance() {
        if (instance == null) {
            instance = new AdminFeatures();
        }

        return instance;
    }

    private AdminFeatures() {
        xmlSupport = new XMLSupport();

        port = ServerSettings.getInstance().getRemoteGUIPort();
    }

    @Override
    public IServices getServices() throws RemoteException {
        try{
            return (IServices) UnicastRemoteObject.toStub(ControlServices.getInstance());
        }catch(NoSuchObjectException ex){
            return (IServices) UnicastRemoteObject.exportObject(ControlServices.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public IQRServers getQRServers() throws RemoteException {
        try{
            return (IQRServers) UnicastRemoteObject.toStub(QRServers.getInstance());
        }catch(NoSuchObjectException ex){
            return (IQRServers) UnicastRemoteObject.exportObject(QRServers.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public ILogs getLogs() throws RemoteException {
        try{
            return (ILogs) UnicastRemoteObject.toStub(Logs.getInstance());
        }catch(NoSuchObjectException ex){
            return (ILogs) UnicastRemoteObject.exportObject(Logs.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public IStartupServ getStartupServ() throws RemoteException {
        try{
            return (IStartupServ) UnicastRemoteObject.toStub(StartupServices.getInstance());
        }catch(NoSuchObjectException ex){
            return (IStartupServ) UnicastRemoteObject.exportObject(StartupServices.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public IQueryRetrieve getQueryRetrive() throws RemoteException {
        try{
            return (IQueryRetrieve) UnicastRemoteObject.toStub(QueryRetrieve.getInstance());
        }catch(NoSuchObjectException ex){
            return (IQueryRetrieve) UnicastRemoteObject.exportObject(QueryRetrieve.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public IAccessList getAccessList() throws RemoteException {
        try{
            return (IAccessList) UnicastRemoteObject.toStub(AccessList.getInstance());
        }catch(NoSuchObjectException ex){
            return (IAccessList) UnicastRemoteObject.exportObject(AccessList.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public IIndexOptions getIndexOptions() throws RemoteException {
        try{
            return (IIndexOptions) UnicastRemoteObject.toStub(IndexOptions.getInstance());
        }catch(NoSuchObjectException ex){
            return (IIndexOptions) UnicastRemoteObject.exportObject(IndexOptions.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public ISOPClass getSOPClass() throws RemoteException {
        try{
            return (ISOPClass) UnicastRemoteObject.toStub(SOPClass.getInstance());
        }catch(NoSuchObjectException ex){
            return (ISOPClass) UnicastRemoteObject.exportObject(SOPClass.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public IDirectory getDirectorySettings() throws RemoteException {
        try{
            return (IDirectory) UnicastRemoteObject.toStub(DirectorySettings.getInstance());
        }catch(NoSuchObjectException ex){
            return (IDirectory) UnicastRemoteObject.exportObject(DirectorySettings.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public IUsersManager getUsersManager() throws RemoteException {
        try{
            return (IUsersManager) UnicastRemoteObject.toStub(UsersManager.getInstance());
        }catch(NoSuchObjectException ex){
            return (IUsersManager) UnicastRemoteObject.exportObject(UsersManager.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }


    @Override
    public IActiveSessions getActiveSessions() throws RemoteException {
        try{
            return (IActiveSessions) UnicastRemoteObject.toStub(UserSessions.getInstance());
        }catch(NoSuchObjectException ex){
            return (IActiveSessions) UnicastRemoteObject.exportObject(UserSessions.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }


    /**
     * @return the taskList
     */
    @Override
    public ITaskList getTaskList() throws RemoteException{
        try{
            return (ITaskList) UnicastRemoteObject.toStub(TaskList.getInstance());
        }catch(NoSuchObjectException ex){
            return (ITaskList) UnicastRemoteObject.exportObject(TaskList.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    /**
     * @return the pending messages controler remote reference
     */
    @Override
    public IPendingMessages getPendingMessages() throws RemoteException{
        try{
            return (IPendingMessages) UnicastRemoteObject.toStub(PendingMessages.getInstance());
        }catch(NoSuchObjectException ex){
            return (IPendingMessages) UnicastRemoteObject.exportObject(PendingMessages.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }

    @Override
    public IRemoteFileSystem getRFS() throws RemoteException {
        try{
            return (IRemoteFileSystem) UnicastRemoteObject.toStub(RemoteFileSystemServer.getInstance());
        }catch(NoSuchObjectException ex){
            return (IRemoteFileSystem) UnicastRemoteObject.exportObject(RemoteFileSystemServer.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }
    
    @Override
    public IPluginControllerAdmin getPluginController() throws RemoteException
    {
        try{
            return (IPluginControllerAdmin) UnicastRemoteObject.toStub(PluginController4Admin.getInstance());
        }catch(NoSuchObjectException ex){
            return (IPluginControllerAdmin) UnicastRemoteObject.exportObject(PluginController4Admin.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }

    }

    
    @Override
    public INetworkInterfaces getNetworkInterface() throws RemoteException {
        try{
            return (INetworkInterfaces) UnicastRemoteObject.toStub(NetworkInterfaces.getInstance());
        }catch(NoSuchObjectException ex){
            return (INetworkInterfaces) UnicastRemoteObject.exportObject(NetworkInterfaces.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
    }


    /**
     * Unexport all the remote objects related to the administration
     * 
     * @throws RemoteException
     */
    @Override
    public void logout() throws RemoteException {
        timer.cancel();

        // reset the signalBack object reference
        TaskList.getInstance().resetSignalBack();
        Logs.getInstance().resetSignalBack();
        PendingMessages.getInstance().resetSignalBack();

        try {
            UnicastRemoteObject.unexportObject(ControlServices.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(QRServers.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(Logs.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(StartupServices.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(QueryRetrieve.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(AccessList.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(IndexOptions.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(SOPClass.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(DirectorySettings.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(UsersManager.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 
        
        try {
            UnicastRemoteObject.unexportObject(UserSessions.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(RemoteFileSystemServer.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 
        
        
        try {
            UnicastRemoteObject.unexportObject(TaskList.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 
        
        try {
            UnicastRemoteObject.unexportObject(PendingMessages.getInstance(), true);
        } catch (NoSuchObjectException ex) {}; 

        try {
            UnicastRemoteObject.unexportObject(NetworkInterfaces.getInstance(), true);
        } catch (NoSuchObjectException ex) {};  
        
        try {
            UnicastRemoteObject.unexportObject(PluginController4Admin.getInstance(), true);
        } catch (NoSuchObjectException ex) {};  
        
        
        resetSettings();

        this.user = null;
        userStub = null;
        timeoutCanceled = false;

        //removes the administrator session
        UserSessions.getInstance().adminLogout();
        
        // unexport this object
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {};    
    }

    /**
     * Save ServerSettings
     *  This funcion has to call the other "save" functions of the administration object
     *  Finnaly it has to print XML with new ServerSettings
     *
     * @throws RemoteException
     */
    @Override
    public void saveSettings() throws RemoteException {

        StartupServices.getInstance().saveSettings();
        QueryRetrieve.getInstance().saveSettings();
        AccessList.getInstance().saveSettings();
        IndexOptions.getInstance().saveSettings();
        DirectorySettings.getInstance().saveSettings();
        QRServers.getInstance().saveSettings();
        UsersManager.getInstance().saveSettings();

        xmlSupport.printXML();
        /**
         * ATTENTION: this settings are stored appart from the others in the
         * filepath: /plugins/settings/PLUGIN_NAME.settings
         * They are also stored in binary format instead of XML.
         */
        PluginController4Admin.getInstance().saveSettings();
        
        //DebugManager.getInstance().debug("Settings saved!");
    }

    /**
     * check if there ara unsaved Settings
     *
     * @return true if there are unsaved settings
     * @throws RemoteException
     */
    @Override
    public boolean unsavedSettings() throws RemoteException {
        return StartupServices.getInstance().unsavedSettings() || QueryRetrieve.getInstance().unsavedSettings()
                || AccessList.getInstance().unsavedSettings() || IndexOptions.getInstance().unsavedSettings()
                || DirectorySettings.getInstance().unsavedSettings() || QRServers.getInstance().unsavedSettings()
                || UsersManager.getInstance().unsavedSettings();
    }

    

    @Override
    public String getDefaultFilePath() throws RemoteException {
        return ServerSettings.getInstance().getPath();
    }

    
    /**
     * Restore settings, discard all unsaved settings
     * 
     * @throws RemoteException
     */
    @Override
    public void resetSettings() throws RemoteException {
        if (StartupServices.getInstance().unsavedSettings()) {
            StartupServices.getInstance().loadSettings();
        }

        if (QueryRetrieve.getInstance().unsavedSettings()) {
            QueryRetrieve.getInstance().loadSettings();
        }

        if (AccessList.getInstance().unsavedSettings()) {
            AccessList.getInstance().loadSettings();
        }

        if (IndexOptions.getInstance().unsavedSettings()) {
            IndexOptions.getInstance().loadSettings();
        }

        if (DirectorySettings.getInstance().unsavedSettings()) {
            DirectorySettings.getInstance().loadSettings();
        }

        if (QRServers.getInstance().unsavedSettings()) {
            QRServers.getInstance().loadSettings();
        }

        if (UsersManager.getInstance().unsavedSettings()) {
            UsersManager.getInstance().loadSettings();
        }
    }

    @Override
    public String getUsername() throws RemoteException {
        if (user != null) {
            return user.getUsername();
        }

        return null;
    }

    @Override
    public void KeepAlive() throws RemoteException {
        if(!timeoutCanceled){
            task.cancel();
            timer.purge();

            task = new TimeOut();
            timer.schedule(task, timeoutTime, timeoutTime);
        }
    }

    @Override
    public IUser getUser() throws RemoteException {
        if (userStub == null) {
            UserFeatures userF = new UserFeatures(user);
            UserSessions.getInstance().setAdminUserFeatures(userF); //saves the UserFeatures of this admin

            userStub = (IUser) UnicastRemoteObject.exportObject(userF, 0, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }

        return userStub;
    }

    
    public void setUser(User user) {
        this.user = user;
        
        timer = new Timer();
        task = new TimeOut();

        timer.schedule(task, timeoutTime, timeoutTime); 
    }

    @Override
    public void shutdownServer() throws RemoteException {
        //shutdown the services
        ControlServices.getInstance().stopAllServices();

        //logout the admin
        this.logout();

        //logout the users
        UserSessions.getInstance().adminLogoutAllUsers();

        Anonymous.getInstance().stop();
        
        //DebugManager.getInstance().debug("The Server is Shutting Down!");
        
        //close Dicoogle Server
        System.exit(0);
    }

    
    /**
     * If the number is equal to the number randomly generated
     * in Main class, the timetout stops.
     *
     * @param number
     * @return if the number is equal or not
     * @throws RemoteException
     */
    @Override
    public boolean shtudownTimeout(int number) throws RemoteException {
        if(number == Main.randomInteger){
            timeoutCanceled = true;
            task.cancel();
            timer.purge();

            return true;
        }
        return false;
    }


    /**
     * This class extends TimerTask
     * and when the run method is called the admin logout
     */
    private class TimeOut extends TimerTask {

        @Override
        public void run() {
            try {
                logout();
            } catch (RemoteException ex) {
                LoggerFactory.getLogger(UserFeatures.class).error(ex.getMessage(), ex);
            }
        }
    }
}
