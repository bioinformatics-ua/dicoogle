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

import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.server.users.UserSessions;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.rGUI.MultihomeSslRMIClientSocketFactory;

import pt.ua.dicoogle.rGUI.interfaces.IUser;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IDicomSend;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerUser;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISearch;
import pt.ua.dicoogle.rGUI.server.controllers.DicomSend;
import pt.ua.dicoogle.rGUI.server.controllers.PluginController4user;
import pt.ua.dicoogle.rGUI.server.controllers.Search;
import pt.ua.dicoogle.server.users.User;
import pt.ua.dicoogle.server.users.UsersXML;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class UserFeatures implements IUser {
    private UserSessions userSessions;
    private User user;
    private int port;

    private Timer timer;
    private static int timeoutTime = 10000; //10 seconds
    private TimerTask task;
    private boolean timeoutCanceled = false;

    /*
     * These are the controlers that have been exported to RMI
     */
    private ISearch search = null, searchStub = null;
    private IPluginControllerUser pcontroller = null;
    private static IDicomSend dicomSendStub = null;


    public UserFeatures(User user) {
        userSessions = UserSessions.getInstance();

        this.user = user;

        timer = new Timer();
        task = new TimeOut();

        timer.schedule(task, timeoutTime, timeoutTime);

    }

    @Override
    public void logout() throws RemoteException {
        task.cancel();

        if(search != null){
            UnicastRemoteObject.unexportObject(search, true);
            search = null;
        }
        
        
        // unexport this object^M
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {}; 
        userSessions.userLogout(this);
    }

    
    @Override
    public boolean changePassword(String oldPassHash, String newPassHash) throws RemoteException {
        boolean result = user.changePassword(oldPassHash, newPassHash);

        // save the xml with new user informations
        if(result){
            UsersXML xml = new UsersXML();
            xml.printXML();
        }
        return result;
    }

    @Override
    public String getUsername() throws RemoteException {
        if(user != null)
            return user.getUsername();
        
        return "";
    }

    @Override
    public ISearch getSearch() throws RemoteException {
        if(search == null)
        {
            search = new Search();

            searchStub = (ISearch) UnicastRemoteObject.exportObject(search, port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }

        return searchStub;
    }


    @Override
    public IDicomSend getDicomSend() throws RemoteException {
        if(dicomSendStub == null)
            dicomSendStub = (IDicomSend) UnicastRemoteObject.exportObject(DicomSend.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());

        return dicomSendStub;
    }


    /**
     * Cancel the task and reschedule the task
     * @throws RemoteException
     */
    @Override
    public void KeepAlive() throws RemoteException {
        if(!timeoutCanceled){
            task.cancel();
            timer.purge();

            task = new TimeOut();
            timer.schedule(task, timeoutTime, timeoutTime);
        }
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

    @Override
    public IPluginControllerUser getPluginController() throws RemoteException
    {
        if(pcontroller == null)
        {
            pcontroller = (IPluginControllerUser) UnicastRemoteObject.exportObject(PluginController4user.getInstance(), port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        }
        return pcontroller;
    }

    
    /**
     * This class extends TimerTask
     * and when the run method is called the user logout
     */
    private class TimeOut extends TimerTask{

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
