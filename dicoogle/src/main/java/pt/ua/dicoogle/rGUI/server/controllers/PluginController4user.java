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
import java.util.Collections;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IPluginControllerUser;

/**
 *
 * @author Carlos Ferreira
 */
@Deprecated
public class PluginController4user implements IPluginControllerUser
{
    private static PluginController4user instance = null;
    
    private PluginController4user()
    {
    }

    public static PluginController4user getInstance()
    {
        if(PluginController4user.instance == null)
        {
            PluginController4user.instance = new PluginController4user();
        }
        return PluginController4user.instance;
    }
    
    @Override
    public synchronized List<String> getPluginNames() throws RemoteException
    {
    	return PluginController.getInstance().getQueryProvidersName(true);
        //return PluginController.getSettings().getPluginsNames();
    	//TODO: DELETED
    }

    @Override
    public boolean isRunning(String PluginName) throws RemoteException
    {
    	return false;
//        
//        return PluginController.getSettings().isPluginRunning(PluginName);
//        TODO: DELETED
    }

    @Override
    public boolean isLocalPlugin(String PluginName) throws RemoteException
    {
    	return false;
        
//        return PluginController.getSettings().isLocalPlugin(PluginName);
//        TODO: DELETED
    }

    @Override
    public List<JPanel> getTabPanels() throws RemoteException{
        return PluginController.getInstance().getTabItems();
    }

    @Override
    public List<String> getGraphicPluginNames() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<JMenuItem> getRightButtonItems() throws RemoteException {
        return PluginController.getInstance().getRightButtonItems();
    }
    
    @Override
    public List<JMenuItem> getPluginMenus() throws RemoteException{
        return PluginController.getInstance().getMenuItems();
    }
}
