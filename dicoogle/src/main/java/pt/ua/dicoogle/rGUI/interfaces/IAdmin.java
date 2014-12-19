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

package pt.ua.dicoogle.rGUI.interfaces;

import pt.ua.dicoogle.rGUI.RFileBrowser.IRemoteFileSystem;
import java.rmi.Remote;
import java.rmi.RemoteException;

import pt.ua.dicoogle.rGUI.interfaces.controllers.ILogs;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IServices;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IStartupServ;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IQRServers;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IAccessList;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IActiveSessions;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDirectory;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IIndexOptions;
import pt.ua.dicoogle.rGUI.interfaces.controllers.INetworkInterfaces;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPendingMessages;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerAdmin;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerUser;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IQueryRetrieve;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISOPClass;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ITaskList;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IUsersManager;


/**
 * This interface contains methods that are features of administration
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public interface IAdmin extends Remote {

    //get the username of the current Administrator
    public String getUsername() throws RemoteException;

    public IUser getUser() throws RemoteException;

    public IServices getServices() throws RemoteException;

    public ILogs getLogs() throws RemoteException;

    public IQRServers getQRServers() throws RemoteException;

    public IStartupServ getStartupServ() throws RemoteException;

    public IQueryRetrieve getQueryRetrive() throws RemoteException;

    public IAccessList getAccessList() throws RemoteException;

    public IIndexOptions getIndexOptions() throws RemoteException;

    public ISOPClass getSOPClass() throws RemoteException;

    public IDirectory getDirectorySettings() throws RemoteException;

    public IUsersManager getUsersManager() throws RemoteException;

    public IActiveSessions getActiveSessions() throws RemoteException;
    
    public ITaskList getTaskList() throws RemoteException;

    public IPendingMessages getPendingMessages() throws RemoteException;

    public INetworkInterfaces getNetworkInterface() throws RemoteException;

    public IPluginControllerAdmin getPluginController() throws RemoteException;
    //public IStatus getStatus() throws RemoteException;

    public String getDefaultFilePath() throws RemoteException;
    public IRemoteFileSystem getRFS() throws RemoteException;

    //check if there are unsaved Settings
    public boolean unsavedSettings() throws RemoteException;
    
    //Save Settings (print XML)
    public void saveSettings() throws RemoteException;
    public void resetSettings() throws RemoteException;
  
    public void logout() throws RemoteException;

    public void shutdownServer() throws RemoteException;

    public void KeepAlive() throws RemoteException;

    /**
     * If the number is equal to the number randomly generated
     * in Main class, the timetout stops.
     *
     * @param number
     * @return if the number is equal or not
     * @throws RemoteException
     */
    public boolean shtudownTimeout(int number) throws RemoteException;
}
