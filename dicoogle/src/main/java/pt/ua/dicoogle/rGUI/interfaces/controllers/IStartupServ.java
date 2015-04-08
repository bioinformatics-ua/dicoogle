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
package pt.ua.dicoogle.rGUI.interfaces.controllers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * methods avaliable in Startup Services window
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
@Deprecated
public interface IStartupServ extends Remote {

    public void setP2P(boolean value) throws RemoteException;
    public boolean getP2P() throws RemoteException;
    
    public void setDICOMStorage(boolean value) throws RemoteException;
    public boolean getDICOMStorage() throws RemoteException;

    public void setDICOMStoragePort(int value) throws RemoteException;
    public int getDICOMStoragePort() throws RemoteException;

    public void setDICOMQR(boolean value) throws RemoteException;
    public boolean getDICOMQR() throws RemoteException;

    public void setDICOMQRPort(int value) throws RemoteException;
    public int getDICOMQRPort() throws RemoteException;

    public void setWebServer(boolean value) throws RemoteException;
    public boolean getWebServer() throws RemoteException;

    public void setWebServerPort(int value) throws RemoteException;
    public int getWebServerPort() throws RemoteException;

    public void setWebServices(boolean value) throws RemoteException;
    public boolean getWebServices() throws RemoteException;

    public void setWebServicesPort(int value) throws RemoteException;
    public int getWebServicesPort() throws RemoteException;

    public void setRemoteGUIPort(int value) throws RemoteException;
    public int getRemoteGUIPort() throws RemoteException;

    public void setRemoteGUIExtIP(String IP) throws RemoteException;
    public String getRemoteGUIExtIP() throws RemoteException;
}
