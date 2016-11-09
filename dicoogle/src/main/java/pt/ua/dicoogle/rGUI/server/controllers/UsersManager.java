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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IUsersManager;
import pt.ua.dicoogle.server.users.HashService;
import pt.ua.dicoogle.server.users.User;
import pt.ua.dicoogle.server.users.UsersStruct;
import pt.ua.dicoogle.server.users.UsersXML;

/**
 * Controller of Users Manager Settings
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class UsersManager implements IUsersManager {
    private static UsersManager instance = null;

    private UsersStruct users;

    private boolean encryptUsersFile = false;

    //flag to indicate when setting are unsaved
    private boolean unsavedSettings;

    public static synchronized UsersManager getInstance() {
        if (instance == null) {
            instance = new UsersManager();
        }

        return instance;
    }

    private UsersManager(){
        users = UsersStruct.getInstance();

        unsavedSettings = false;
    }

    public void loadSettings(){
        unsavedSettings = false;

        UsersXML xml = new UsersXML();
        xml.getXML();
    }
    
    /**
     *
     * @return  true - if there are unsaved settings
     *          false - not
     */
    public boolean unsavedSettings(){
        return unsavedSettings;
    }

    /**
     * Save the current Users configuration
     *
     *  write in XML
     */
    public void saveSettings(){

        UsersXML xml = new UsersXML();
        xml.printXML();

        unsavedSettings = false;
    }

    @Override
    public ArrayList<String> getUsernames() throws RemoteException {

        Iterator<String> en = users.getUsernames().iterator();

        ArrayList<String> list = new ArrayList<String>();

        while(en.hasNext())
            list.add(en.next());

        list.trimToSize();

        return list;
    }

    @Override
    public boolean isAdmin(String username) throws RemoteException {
        User user = users.getUser(username);

        if(user != null && user.isAdmin())
            return true;

        return false;
    }

    /**
     * Remove one user
     *
     * @param username
     * @return  true if the user is removed
     *          false if username doesn't exist
     * @throws RemoteException
     */
    @Override
    public boolean deleteUser(String username) throws RemoteException {
        boolean temp = users.removeUser(username);

        if (temp){
            unsavedSettings = true;
            Logs.getInstance().addServerLog("User " + username + " deleted");
        }

        return temp;
    }

    /**
     * Adds one user to the user list
     * 
     * @param username
     * @param passwordHash
     * @param admin
     * 
     * @return  true if user is successfully addes to the list
     *          false if the username already exists or username or password are invalid
     * 
     * @throws RemoteException
     */
    @Override
    public boolean addUser(String username, String passwordHash, boolean admin) throws RemoteException {
        if(username == null || username.equals("") || passwordHash == null || username.equals("") || passwordHash.equals(""))
            return false;

        String Hash = HashService.getSHA1Hash(username + admin + passwordHash);   //user Hash

        User user = new User(username, Hash, admin);

        boolean temp = users.addUser(user);

        if(temp){
            unsavedSettings = true;
            Logs.getInstance().addServerLog("User " + username + " created");
        }


        return temp;
    }

    @Override
    public boolean getEncryptUsersFile() throws RemoteException {
        return encryptUsersFile;
    }

    @Override
    public void setEncryptUsersFile(boolean value) throws RemoteException {
        encryptUsersFile = value;
    }

    @Override
    public boolean resetPassword(String username, String passwordHash) throws RemoteException {
        if(username == null || username.equals("") || passwordHash == null || username.equals("") || passwordHash.equals(""))
            return false;

        User user = users.getUser(username);

        if(user != null && user.resetPassword(passwordHash)){
            unsavedSettings = true;

            return true;
        }

        return false;
    }

    
}
