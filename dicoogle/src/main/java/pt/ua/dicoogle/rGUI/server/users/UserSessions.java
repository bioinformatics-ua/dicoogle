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
package pt.ua.dicoogle.rGUI.server.users;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IActiveSessions;
import pt.ua.dicoogle.rGUI.server.UserFeatures;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class UserSessions implements IActiveSessions {

    //Active users lists
    private HashMap<Integer, UserON> usersTable;
    private HashMap<Integer, UserFeatures> usersFeatures;
    // Saves the current administrator's user
    private int adminID;
    // Actual ID in Table
    private int ID;
    private static UserSessions instance = null;

    public static synchronized UserSessions getInstance() {
        if (instance == null) {
            instance = new UserSessions();
        }

        return instance;
    }

    private UserSessions() {
        usersTable = new HashMap<Integer, UserON>();
        usersFeatures = new HashMap<Integer, UserFeatures>();

        ID = 0;
        adminID = - 1;
    }

    /**
     *
     * @param username
     * @param host
     * @return user session ID
     */
    public int userLogin(User user, String host, UserFeatures userF) {
        if (userF == null) {
            throw new NullPointerException("UserFeatures can't be null!");
        }

        int id = login(user, host, false);

        usersFeatures.put(id, userF);
        return id;
    }

    /**
     *
     * @param username
     * @param host
     * @return false if the administrator is already connected
     */
    public synchronized int adminLogin(User adminUser, String host) {
        if (adminID == -1 && adminUser.isAdmin()) {
            adminID = login(adminUser, host, true);

            return adminID;
        }

        return -1;
    }

    private int login(User user, String host, boolean admin) {
        int returnValue = ID;

        usersTable.put(ID, new UserON(ID, user.getUsername(), host));

        //write the log
        UserSessionsLog.getInstance().login(user.getUsername(), host, admin);

        //increase and mantain ID as a positive integer
        ID = (ID + 1) & Integer.MAX_VALUE;

        return returnValue;
    }

    /**
     * Set the UserFeatures related to admin
     * @param userF
     * @return
     */
    public boolean setAdminUserFeatures(UserFeatures userF) {
        if (!usersFeatures.containsKey(adminID)) {
            usersFeatures.put(adminID, userF);
            return true;
        }

        return false;
    }

    /**
     * Remove one user from active users list
     *
     * @param userID
     * @return  true if the user exists and has been removed from the list
     */
    public boolean userLogout(UserFeatures userF) {
        int id;
        Iterator<Integer> en = usersFeatures.keySet().iterator();

        while (en.hasNext()) {
            id = en.next();

            //this is a workarround, must be more efficient
            if (usersFeatures.get(id) == userF) {
                //usersFeatures.remove(id);
                en.remove();
                UserON user = usersTable.remove(id);

                if (user != null) {
                    UserSessionsLog.getInstance().logout(user.getUsername(), user.getHost(), false);
                }

                return true;
            }
        }

        return false;
    }

    public synchronized void adminLogout() {
        if (adminID != -1) {
            UserON user = usersTable.get(adminID);

            if (user != null) {
                UserSessionsLog.getInstance().logout(user.getUsername(), user.getHost(), true);
            }
        }
        adminID = -1;
    }

    /**
     *
     * @return the current usersTable
     * @throws RemoteException
     */
    @Override
    public HashMap<Integer, UserON> getUsersTable() throws RemoteException {
        return usersTable;
    }

    /**
     * Logout one user 
     *
     * @param userID
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean adminLogoutUser(int userID) throws RemoteException {
        boolean value = false;

        UserFeatures userF;

        if ((userF = usersFeatures.get(userID)) != null) {
            try {
                userF.logout();
                value = true;

            } catch (RemoteException ex) {
                LoggerFactory.getLogger(UserSessions.class).error(ex.getMessage(), ex);
            }
        }

        return value;
    }

    @Override
    public int getAdminID() throws RemoteException {
        return adminID;
    }


    /*
     * Logout All Users
     */
    public void adminLogoutAllUsers() throws RemoteException {
        Iterator<Integer> en = usersFeatures.keySet().iterator();
        UserFeatures userF;

        while (en.hasNext()) {
            userF = usersFeatures.get(en.next());
            if (userF != null) {
                try {
                    userF.logout();
                } catch (RemoteException ex) {
                    LoggerFactory.getLogger(UserSessions.class).error(ex.getMessage(), ex);
                }
            }
        }
    }

    public void loginFailed(String username, String host, boolean admin) {
        UserSessionsLog.getInstance().loginFailed(username, host, admin);
    }
}
