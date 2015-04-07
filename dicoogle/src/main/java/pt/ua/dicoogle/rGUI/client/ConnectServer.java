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


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.rGUI.MultihomeSslRMIClientSocketFactory;
import pt.ua.dicoogle.rGUI.client.windows.ConnectWindow;
import pt.ua.dicoogle.rGUI.client.windows.MainWindow;
import pt.ua.dicoogle.rGUI.client.UIHelper.TrayIconCreator;

import pt.ua.dicoogle.rGUI.interfaces.ILogin;
import pt.ua.dicoogle.rGUI.interfaces.IAdmin;
import pt.ua.dicoogle.rGUI.interfaces.IUser;
import pt.ua.dicoogle.rGUI.utils.KeysManager;

/**
 * This class is responsible for making the connection to the RMI GUI Server
 *
 * After the connection, the user can login to manage the server or to search
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class ConnectServer {
    private String host;
    private int port;
    private ILogin login;
    private Registry reg;
    private ClientCore core;

    /*
     * Falta propagar uma excepção que indique uma falha na conexão
     */
    public ConnectServer(String host, int port) throws RemoteException, NotBoundException, UnknownHostException{
        this.host = host;
        this.port = port;
        
        System.out.println("Host: "+host);
        System.out.println("Port: " + port);
        
        String TrustStorePath = KeysManager.getClientKeyPath();

        //define the System property to use a SSL TrustStore
        System.setProperty("javax.net.ssl.trustStore", TrustStorePath);

        if (Main.isFixedClient())
        {
            
            String keyStorePath = KeysManager.getServerKeyPath();

            //define the System properties to use a SSLKeystore
            System.setProperty("javax.net.ssl.keyStore", keyStorePath);
            System.setProperty("javax.net.ssl.keyStorePassword", "dicooglepacs");
        }

        System.setProperty("sun.rmi.activation.execTimeout", "200");
        

        //get the Registry with the Remote Objects
        reg = LocateRegistry.getRegistry(this.host, this.port, new MultihomeSslRMIClientSocketFactory());
        //reg = LocateRegistry.getRegistry(this.host, this.port);
        

        login = (ILogin) reg.lookup("login");

        
        
 
        core = ClientCore.getInstance();

        InetAddress serverAddr = InetAddress.getByName(host);
            
        core.setServerAddress(serverAddr);
        
    }
    
    /**
     * Try to login (Administration and UserSearch) in server
     *
     * @param username
     * @param pass
     */
    public boolean login(String username, String passwordHash) throws RemoteException{
            
            IAdmin admin = loginAdmin(username, passwordHash);
            IUser user;

            // if the user is administrator, automactly get the user
            if(admin != null){
                core.setAdmin(admin);
                user = admin.getUser();
            }
            else
                user = loginUser(username, passwordHash);

            if(user != null)
                core.setUser(user);


            if(admin == null && user == null)
                return false;
            
        TrayIconCreator.getInstance();

        MainWindow.getInstance().setVisible(true);

        ConnectWindow.getInstance().setVisible(false);
        
        MainWindow.getInstance().toFront();
        return true;
    }

    /**
     *
     * @param username
     * @param pass
     * @return the reference to the administration main object
     * @throws RemoteException
     */
    private IAdmin loginAdmin(String username, String pass) throws RemoteException{
        if (login != null)
            return login.LoginAdmin(username, pass);
        
        return null;
    }

    /**
     *
     * @param username
     * @param pass
     * @return the reference to the user search main object
     * @throws RemoteException
     */
    private IUser loginUser(String username, String pass) throws RemoteException{
        if (login != null)
            return login.LoginUser(username, pass);
        
        return null;
    }
}
