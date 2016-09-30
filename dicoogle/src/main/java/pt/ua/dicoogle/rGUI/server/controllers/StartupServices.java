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
import java.util.concurrent.Semaphore;

import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.core.settings.ServerSettings;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IStartupServ;

/**
 * Controller of Startup Services Settings
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public class StartupServices implements IStartupServ {

    private static Semaphore sem = new Semaphore(1, true);
    private static StartupServices instance = null;

    private ServerSettings settings;
    
    private boolean p2p;
    private boolean storage;
    private boolean qr;
    private boolean web;
    private boolean webservices;
    private int storagePort;
    private int webPort;
    private int webservicesPort;
    private int QRPort;
    private int remoteGUIPort;
    private String remoteGUIExtIP;

    public static synchronized StartupServices getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new StartupServices();
            }
            sem.release();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(StartupServices.class).error(ex.getMessage(), ex);
        }

        return instance;
    }

    private StartupServices() {
        settings = ServerSettings.getInstance();
                
        loadSettings();
    }

    /**
     * Load Settings from ServerSettings
     */
    public void loadSettings() {
      //  p2p = settings.isP2P();
        storage = settings.isStorage();
        qr = settings.isQueryRetrive();
        web = settings.getWeb().isWebServer();
        webservices = settings.getWeb().isWebServices();

        storagePort = settings.getStoragePort();
        webPort = settings.getWeb().getServerPort();
        webservicesPort = settings.getWeb().getServicePort();
        QRPort = settings.getWlsPort();
        
        remoteGUIPort = settings.getRemoteGUIPort();
        remoteGUIExtIP = settings.getRGUIExternalIP();
    }

    /**
     * Save the settings related to Startup Services
     *
     * not write the settings in XML
     */
    public void saveSettings() {
   //     settings.setP2P(p2p);
        settings.setStorage(storage);
        settings.setQueryRetrive(qr);
        settings.getWeb().setWebServer(web);
        settings.getWeb().setWebServices(webservices);

        settings.setStoragePort(storagePort);
        settings.getWeb().setServerPort(webPort);
        settings.getWeb().setServicePort(webservicesPort);
        settings.setWlsPort(QRPort);
        settings.setRemoteGUIPort(remoteGUIPort);
        settings.setRGUIExternalIP(remoteGUIExtIP);
    }

    /**
     *
     * @return  true - if there are unsaved settings ( != ServerSettings)
     *          false - not
     */
    public boolean unsavedSettings() {
        if (/*p2p != settings.isP2P() ||*/ storage != settings.isStorage()
                || qr != settings.isQueryRetrive() || web != settings.getWeb().isWebServer()
                || webservices != settings.getWeb().isWebServices() || storagePort != settings.getStoragePort()
                || webPort != settings.getWeb().getServerPort() || webservicesPort != settings.getWeb().getServicePort()
                || remoteGUIPort != settings.getRemoteGUIPort() || QRPort != settings.getWlsPort()
                || (remoteGUIExtIP != null && !remoteGUIExtIP.equals(settings.getRGUIExternalIP()))) {
            return true;
        }

        return false;
    }

    @Override
    public void setP2P(boolean value) throws RemoteException {
        p2p = value;
    }

    @Override
    public boolean getP2P() throws RemoteException {
        return p2p;
    }

    @Override
    public void setDICOMStorage(boolean value) throws RemoteException {
        storage = value;
    }

    @Override
    public boolean getDICOMStorage() throws RemoteException {
        return storage;
    }

    @Override
    public void setDICOMStoragePort(int value) throws RemoteException {
        storagePort = value;
    }

    @Override
    public int getDICOMStoragePort() throws RemoteException {
        return storagePort;
    }

    @Override
    public void setDICOMQR(boolean value) throws RemoteException {
        qr = value;
    }

    @Override
    public boolean getDICOMQR() throws RemoteException {
        return qr;
    }

    @Override
    public void setWebServer(boolean value) throws RemoteException {
        web = value;
    }

    @Override
    public boolean getWebServer() throws RemoteException {
        return web;
    }

    @Override
    public void setWebServerPort(int value) throws RemoteException {
        webPort = value;
    }

    @Override
    public int getWebServerPort() throws RemoteException {
        return webPort;
    }

    @Override
    public void setWebServices(boolean value) throws RemoteException {
        webservices = value;
    }

    @Override
    public boolean getWebServices() throws RemoteException {
        return webservices;
    }

    @Override
    public void setWebServicesPort(int value) throws RemoteException {
        webservicesPort = value;
    }

    @Override
    public int getWebServicesPort() throws RemoteException {
        return webservicesPort;
    }

    @Override
    public void setRemoteGUIPort(int value) throws RemoteException {
        remoteGUIPort = value;
    }

    @Override
    public int getRemoteGUIPort() throws RemoteException {
        return remoteGUIPort;
    }

    @Override
    public void setDICOMQRPort(int value) throws RemoteException {
        QRPort = value;
    }

    @Override
    public int getDICOMQRPort() throws RemoteException {
        return QRPort;
    }

    @Override
    public void setRemoteGUIExtIP(String IP) throws RemoteException {
        remoteGUIExtIP = IP;
    }

    @Override
    public String getRemoteGUIExtIP() throws RemoteException {
        return remoteGUIExtIP;
    }
}
