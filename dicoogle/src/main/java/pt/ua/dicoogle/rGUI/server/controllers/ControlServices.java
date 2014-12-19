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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;
import pt.ua.dicoogle.server.RSIStorage;
import pt.ua.dicoogle.server.queryretrieve.QueryRetrieve;

import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IServices;
import pt.ua.dicoogle.server.SOPList;
import pt.ua.dicoogle.server.web.DicoogleWeb;
import pt.ua.dicoogle.taskManager.TaskManager;

/**
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class ControlServices implements IServices
{

    private static Semaphore sem = new Semaphore(1, true);
    private static ControlServices instance = null;
    // Services vars
    private RSIStorage storage = null;
    private boolean webservicesRunning = false;
    private boolean webServerRunning = false;
    private QueryRetrieve retrieve = null;
    
    private DicoogleWeb webServices;
    
    private ControlServices()
    {
        TaskManager taskManager = new TaskManager(4);

        startInicialServices();
    }

    public static synchronized ControlServices getInstance()
    {
        try
        {
            sem.acquire();
            if (instance == null)
            {
                instance = new ControlServices();
            }
            sem.release();
        } catch (InterruptedException ex)
        {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.FATAL, null, ex);
        }
        return instance;
    }


    /* Strats the inicial services based on ServerSettings */
    private void startInicialServices()
    {
        ServerSettings settings = ServerSettings.getInstance();

        try
        {
          /*  if (settings.isP2P())
            {
                startP2P();
            }*/

            if (settings.isStorage())
            {
                startStorage();
            }

            if (settings.isQueryRetrive())
            {
                startQueryRetrieve();
            }

            if (settings.getWeb().isWebServices())
            {
                startWebServices();
            }
            
            if(settings.getWeb().isWebServer()){
            	startWebServer();
            	
            }

        } catch (Exception ex)
        {
            Logger.getLogger(ControlServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* Stop all services that are running */
    @Override
    public boolean stopAllServices()
    {
        try
        {
        	//TODO: DELETED
            //PluginController.getInstance().stopAll();
           // stopP2P();
            stopStorage();
            stopQueryRetrieve();
            stopWebServices();
        } catch (Exception ex)
        {
            Logger.getLogger(ControlServices.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    /**
     *
     * @return   0 - if everything is fine and the service was started
     *          -1 - if the server's storage path is not defined
     *          -2 - service is already running
     * 
     * @throws IOException
     */
    @Override
    public int startStorage() throws IOException
    {
        if (storage == null)
        {
            ServerSettings settings = ServerSettings.getInstance();

            SOPList list = SOPList.getInstance();
            list.setDefaultSettings();

            int i;


            //System.out.println(settings.getPath());
            File f = new File(settings.getPath());

            // Storage path must be defined before running the program
            // Prompting the user if it isn't
            if (!f.exists() || f.isFile())
            {
                //DebugManager.getInstance().debug("DICOM Storage not started. The server's storage path is not defined.");

                return -1;
            }

            List l = list.getKeys();
            String[] keys = new String[l.size()];

            for (i = 0; i < l.size(); i++)
            {
                keys[i] = (String) l.get(i);
            }
            storage = new RSIStorage(keys, list);
            storage.start();

            //DebugManager.getInstance().debug("Starting DICOM Storage SCP");
            Logs.getInstance().addServerLog("Starting DICOM Storage SCP");

            return 0;
        }

        return -2;
    }

    @Override
    public void stopStorage()
    {
        if (storage != null)
        {
            storage.stop();
            storage = null;
            //DebugManager.getInstance().debug("Stopping DICOM Storage SCP");
            Logs.getInstance().addServerLog("Stopping DICOM Storage SCP");
        }
    }

    @Override
    public boolean storageIsRunning()
    {
        return storage != null;
    }

    @Override
    public void startQueryRetrieve()
    {
        if (retrieve == null)
        {
            retrieve = new QueryRetrieve();
            retrieve.startListening();
            //DebugManager.getInstance().debug("Starting DICOM QueryRetrive");
            Logs.getInstance().addServerLog("Starting DICOM QueryRetrive");
        }
    }

    @Override
    public void stopQueryRetrieve()
    {
        if (retrieve != null)
        {
            retrieve.stopListening();
            retrieve = null;
            //DebugManager.getInstance().debug("Stopping DICOM QueryRetrive");
            Logs.getInstance().addServerLog("Stopping DICOM QueryRetrive");
        }
    }

    @Override
    public boolean queryRetrieveIsRunning()
    {
        return retrieve != null;
    }

    @Override
    public boolean webServerIsRunning()
    {
        return webServerRunning;
    }

    @Override
    public void startWebServices() throws IOException
    {
        pt.ua.dicoogle.webservices.DicoogleWebservice.startWebservice();
        webservicesRunning = true;
        Logs.getInstance().addServerLog("Starting Dicoogle WebServices");
    }

    @Override
    public void stopWebServices() throws IOException
    {
        pt.ua.dicoogle.webservices.DicoogleWebservice.stopWebservice();
        webservicesRunning = false;
        Logs.getInstance().addServerLog("Stopping Dicoogle WebService");
    }

    @Override
    public boolean webServicesIsRunning()
    {
        return webservicesRunning;
    }

    @Override
    public void startPlugin(String pluginName)
    {
        //TODO:DELETED
    	//PluginController.getInstance().initializePlugin(pluginName);
        
    	//PeerEngine.getInstance().start();
        //DebugManager.getInstance().debug("Starting P2P network");
        Logs.getInstance().addServerLog("Starting " + pluginName);
    }

    @Override
    public void stopPlugin(String pluginName)
    {
    	//TODO: DELETED
    	/*
        if (PluginController.getInstance().isPluginRunning(pluginName))
        {
            PluginController.getInstance().stopPlugin(pluginName);
            Logs.getInstance().addServerLog("Stopping " + pluginName);
        }*/
    }

    @Override
    public boolean pluginIsRunning(String pluginName)
    {
    	//TODO: DELETED
        //return PluginController.getInstance().isPluginRunning(pluginName);
    	return false;
    }
    
    //TODO: Review those below!
    @Override
    public void startWebServer(){
        System.err.println("Starting WebServer");
        
        if(webServices == null){
            try {
                webServices = new DicoogleWeb( 8080);
                webServerRunning = true;
            } catch (Exception ex) {
                Logger.getLogger(ControlServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Logs.getInstance().addServerLog("Starting Dicoogle Web");
    }

    @Override
    public void stopWebServer(){
        System.err.println("Stopping WebServer");
        
        if(webServices != null){
            try { 
                webservicesRunning = false;
                
                webServices.stop();
                
                webServices = null;
            } catch (Exception ex) {
                Logger.getLogger(ControlServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Logs.getInstance().addServerLog("Stopping Dicoogle Web");
    }
    
    public DicoogleWeb getWebServicePlatform(){
    	return webServices;
    }
    
    
    
}
