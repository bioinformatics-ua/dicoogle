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

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

import org.slf4j.LoggerFactory;

import java.rmi.server.UnicastRemoteObject;

import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.swing.JOptionPane;
import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.rGUI.MultihomeSslRMIClientSocketFactory;

import pt.ua.dicoogle.rGUI.interfaces.ILogin;
import pt.ua.dicoogle.utils.KeysManager;

/**
 * This class creates the GUI Server over RMI
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class GUIServer {

    private int port;
    private Registry reg;

    /**
     * Start the RMI GUI Server.
     * Creates an RMI Registry on localhost
     *
     * @param port - where the RMI Registry will be bound
     */
    public GUIServer(){
        
        setProperties();

        while(true){
            this.port = ServerSettings.getInstance().getRemoteGUIPort();


            try {
                //creates a Registrey for associating names with objects
                reg = LocateRegistry.createRegistry(this.port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());

                //reg = LocateRegistry.createRegistry(this.port);
                // creates the remote object
                ILogin stub = (ILogin) UnicastRemoteObject.exportObject(Login.getInstance(), this.port, new MultihomeSslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
                //ILogin stub = (ILogin) UnicastRemoteObject.exportObject(Login.getInstance(), 0);

                //Associates the Remote Object to its name in the Registry
                reg.rebind("login", stub);

                //DebugManager.getInstance().debug("GUI Server is running on port " + this.port +".");

                break;
            } catch(ExportException ex){
                /*
                 JOptionPane.showMessageDialog(null, "Another server process is already running on port " + this.port,
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                 */

                String sPort = JOptionPane.showInputDialog(null, "Another server process is already running on port " + this.port
                        +"!\nDo you want to try another port?", "Startup Error", JOptionPane.ERROR_MESSAGE);

                try{
                    int pt = Integer.valueOf(sPort);
                    ServerSettings.getInstance().setRemoteGUIPort(pt);
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Port Invalid!",
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(-4);
                }
                

            } catch (Exception ex) {
                //DebugManager.getInstance().debug("Error associating the remote object to the NameRegistry!");

                LoggerFactory.getLogger(GUIServer.class).error(ex.getMessage(), ex);

                break;
            }
        }
    }

    /**
     * Set the properties that are accurate to the RMI Server
     */
    private void setProperties(){
        String keyStorePath = KeysManager.getServerKeyPath();

        /**
         * Put the external IP address into the java.rmi.server.hostname property
         */
        String externIP = ServerSettings.getInstance().getRGUIExternalIP();
        if(externIP != null && !externIP.equals(""))
        {
            String hostname = System.getProperty("java.rmi.server.hostname");
            System.setProperty("java.rmi.server.hostname", hostname + "!" + externIP);
        }

        //define the System properties to use a SSLKeystore
        System.setProperty("javax.net.ssl.keyStore", keyStorePath);
        System.setProperty("javax.net.ssl.keyStorePassword", "dicooglepacs");

        if (Main.isFixedClient()) {
            String TrustStorePath = KeysManager.getClientKeyPath();

            //define the System property to use a SSL TrustStore
            System.setProperty("javax.net.ssl.trustStore", TrustStorePath);
        }
    }
}
