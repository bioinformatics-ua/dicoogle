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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Carlos Ferreira
 */
@Deprecated
public interface IPluginControllerAdmin extends Remote
{
    public List<String> getPluginNames() throws RemoteException;
    
    public void setSettings(HashMap<String, ArrayList> settings) throws RemoteException;

    public HashMap<String, ArrayList> getInitializeParams() throws RemoteException;
    
    public void InitiatePlugin(String PluginName) throws RemoteException;

    public void StopPlugin(String PluginName) throws RemoteException;

    public boolean isRunning(String PluginName) throws RemoteException;

    public boolean isLocalPlugin(String PluginName) throws RemoteException;

    public byte[] getJarFile(String PluginName) throws RemoteException;

    public void saveSettings() throws RemoteException;

}
