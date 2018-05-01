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
import java.util.HashMap;
import java.util.List;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerAdmin;

/**
 *
 * @author Carlos Ferreira
 */
@Deprecated
public class PluginController4Admin implements IPluginControllerAdmin
{
    private static PluginController4Admin instance = null;

    private PluginController4Admin()
    {
    }

    public static PluginController4Admin getInstance()
    {
        if(PluginController4Admin.instance == null)
        {
            PluginController4Admin.instance = new PluginController4Admin();
        }
        return PluginController4Admin.instance;
    }

    @Override
    public synchronized List<String> getPluginNames() throws RemoteException
    {
        //ok, this desperatly needs fixing...
    	//return null;
        return new ArrayList();
        //TODO DELETED
        //return PluginController.getSettings().getPluginsNames();
    }

    @Override
    public synchronized void setSettings(HashMap<String, ArrayList> settings) throws RemoteException
    {

    	return ;
        //TODO DELETED
        //PluginController.getSettings().setSettings(settings);
    }

    @Override
    public void InitiatePlugin(String PluginName) throws RemoteException
    {

    	return ;
        //TODO DELETED
        //PluginController.getSettings().initializePlugin(PluginName);
    }

    @Override
    public void StopPlugin(String PluginName) throws RemoteException
    {
        PluginController.getInstance().stopPlugin(PluginName);
    }

    @Override
    public boolean isRunning(String PluginName) throws RemoteException
    {

    	return false;
        //TODO DELETED
        //return PluginController.getSettings().isPluginRunning(PluginName);
    }

    @Override
    public boolean isLocalPlugin(String PluginName) throws RemoteException
    {

    	return false;
        //TODO DELETED
       // return PluginController.getSettings().isLocalPlugin(PluginName);
    }

    @Override
    public HashMap<String, ArrayList> getInitializeParams() throws RemoteException
    {

    	return null;
        //TODO DELETED
       // return PluginController.getSettings().getPanelInitialParams();
    }

    @Override
    public byte[] getJarFile(String PluginName) throws RemoteException
    {
    	return null;
        //TODO DELETED
        //return PluginController.getSettings().getJarFile(PluginName);
    }

    @Override
    public void saveSettings() throws RemoteException
    {

    	return ;
        //TODO DELETED
       //PluginController.getSettings().saveSettings();
    }

}
