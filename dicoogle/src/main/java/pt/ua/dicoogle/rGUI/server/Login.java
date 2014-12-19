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

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.RemoteServer;

import pt.ua.dicoogle.rGUI.interfaces.ILogin;

import javax.rmi.ssl.SslRMIServerSocketFactory;
import pt.ua.dicoogle.rGUI.MultihomeSslRMIClientSocketFactory;
import pt.ua.dicoogle.rGUI.interfaces.IAdmin;
import pt.ua.dicoogle.rGUI.interfaces.IUser;

import pt.ua.dicoogle.rGUI.server.users.User;
import pt.ua.dicoogle.rGUI.server.users.UserSessions;
import pt.ua.dicoogle.rGUI.server.users.UsersStruct;
import pt.ua.dicoogle.rGUI.server.users.UsersXML;

/**
 * Login of users and administrator
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class Login implements ILogin {

    private static Login instance = null;

    private UsersStruct users;
    private UserSessions sessions;

    public static synchronized Login getInstance() {
        if (instance == null) {
            instance = new Login();
        }

        return instance;
    }

    private Login() {
        new UsersXML().getXML(); // read XML with users and set the UsersStruct object (singleton)

        sessions = UserSessions.getInstance();
        users = UsersStruct.getInstance();
    }

    /*
     * Public remote interface methods
     */
    @Override
    public IAdmin LoginAdmin(String username, String passwordHash) throws RemoteException {
        User user = users.getUser(username);
        String adminHost = "";
        
        if (user != null && user.verifyPassword(passwordHash) && user.isAdmin()) {            
            
            try {
                adminHost = RemoteServer.getClientHost();
            } catch (ServerNotActiveException ex){ }

            if (sessions.adminLogin(user, adminHost) != -1) {
                AdminFeatures admin = AdminFeatures.getInstance();
                admin.setUser(user);
                return (IAdmin) UnicastRemoteObject.exportObject(admin, 0, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
            }
            else{
                return null;
            }
        }

        sessions.loginFailed(username, adminHost, true);
        return null;
    }

    @Override
    public IUser LoginUser(String username, String passwordHash) throws RemoteException {
        User user = users.getUser(username);
        String userHost = "";

        try {
                userHost = RemoteServer.getClientHost();
            } catch (ServerNotActiveException ex){ }

        if (user != null && user.verifyPassword(passwordHash)){

            IUser userStub = null;

            UserFeatures userF = new UserFeatures(user);
            sessions.userLogin(user, userHost, userF);

            userStub = (IUser) UnicastRemoteObject.exportObject(userF, 0, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());

            return userStub;
        }

        sessions.loginFailed(username, userHost, false);

        return null;
    }
}
