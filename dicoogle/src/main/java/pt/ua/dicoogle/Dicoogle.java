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
package pt.ua.dicoogle;

import pt.ua.dicoogle.sdk.utils.SystemInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.dcm4che2.data.TransferSyntax;//must remove this dep from here
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.plugins.ServiceController;
import pt.ua.dicoogle.sdk.IndexerInterface;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * Main class for Dicoogle
 * @author Frederico Valente
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class Dicoogle implements Runnable
{   
    static Logger logger;
    
    PluginController pluginController = new PluginController(SystemInfo.getPluginDirectory());
    ServiceController serviceController = new ServiceController();
    
    boolean running = true;
    boolean isServer = false;
    boolean useWebServices = false;
    
    /**
     * Inits application
     * parses command line arguments, launches tasks and waits for their completion
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ExceptionHandler.registerExceptionHandler();
        initializeLogging();
        
        logger.info("home dir is:"+SystemInfo.getHomeDirectory());
        logger.info("dicoogle directory is:"+SystemInfo.getDicoogleDirectory());
        logger.info("plugin loading directory is:"+SystemInfo.getPluginDirectory());
        logger.info("plugin data storage directory is:"+SystemInfo.getPluginStorageDirectory());
        
        try{
            Dicoogle dicoogle = new Dicoogle();
            dicoogle.pluginController.plugins().forEach((p) -> logger.info("plugin loaded: "+p.getName()));

            
            /*
            *   Parses the command line to obtain tasks to execute and
            *   config values to override
            */
            Iterable<Task<Report>> tasks = dicoogle.parseCommandLine(args);
                        
            /*
            *   prepare the webservice controllers
            *   this will ask the plugin controller for any webplugin and initialize the services if so configured
            */
            dicoogle.serviceController.manageJettyPlugins(dicoogle.pluginController.getJettyPlugins());
            dicoogle.serviceController.manageRestPlugins(dicoogle.pluginController.getRestPlugins());
            
            /*
            *   execute each task specified on the command line
            */
            for(Task<Report> task : tasks){
                logger.info("running task:"+task.getName());
                task.run();
                System.out.println("task "+task.getName()+" complete.");
                System.out.println(task.get());
            }
            System.out.println("Finished running tasks...");
            
            dicoogle.run();            
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            System.err.println(ex);
        }
    }

    
    Dicoogle() throws Exception{
        
        //what is this? should be moved somewhere else... perhaps lucene indexing plugin?
        TransferSyntax.add(new TransferSyntax("1.2.826.0.1.3680043.2.682.1.40", false,false, false, true));
        TransferSyntax.add(new TransferSyntax("1.2.840.10008.1.2.4.70", true,false, false, true));
        TransferSyntax.add(new TransferSyntax("1.2.840.10008.1.2.5.50", false,false, false, true));    
        
        // Lauch Async Index 
        // It monitors a folder, and when a file is touched an event
        // triggers and index is updated.
        
        //TODO: This SHOULD be parametrized and created somewhere else depending
        //on commandline/config file paramenters
        //AsyncIndex asyncIndex = new AsyncIndex();
    }
    
    private static void initializeLogging() {
        //configures logging
        LogManager logManager = LogManager.getLogManager();
        File logConfigFile = new File(SystemInfo.getDicoogleDirectory()+"/logconfig.properties");
        if(logConfigFile.exists()){
            try{
                logManager.readConfiguration(new FileInputStream(logConfigFile));
            }
            catch(IOException | SecurityException e){
                logger.severe("io error when opening log config file");
            } //if the log file does not exists or cant be read its no biggie, we use java's default config
        }
        else{System.err.println("Unable to open config file: "+logConfigFile);}
        logger = Logger.getLogger("dicoogle");        
    }


    private Iterable<Task<Report>> parseCommandLine(String[] args) throws URISyntaxException {
        ArrayList<Task<Report>> actions = new ArrayList<>();
        int i=0;
        while(i< args.length){
            switch(args[i]){
                //set working directory
                //-w ./ for now
                
                //set loggers to verborse and output to stdout
                case "-v": break;
                
                
                //index
                case "-i":{
                    if(i+2 >= args.length) {
                        System.err.println("Query parameter requires an engine and a query expression");
                        System.exit(1);
                    }
                    //check if indexer exists
                    if(pluginController.getIndexerByName(args[i+1], true)==null){
                        System.err.println("Indexer:"+args[i+1]+" not found!");
                        System.err.println("Valid indexers are:");
                        for(IndexerInterface indexer: pluginController.getIndexingPlugins(true)){
                            System.err.print(indexer.getName()+", ");
                        }
                        System.err.println("");
                        System.exit(1);
                    }
                    
                    Task<Report> indexTask = pluginController.indexClosure(args[i+1], new URI(args[i+2]));
                    actions.add(indexTask);
                    i+=2;
                    break;
                }
                
                case "-iall":{
                    Task<Report> indexTask = pluginController.indexAllClosure(new URI(args[i+1]));
                    actions.add(indexTask);
                    i+=2;
                    break;
                }
                
                //index using specific plugin
                case "-in": break;
                    
                //execute query
                case "-q": {
                    if(i+2 >= args.length) {
                        System.err.println("Query parameter requires an engine and a query expression");
                        System.exit(1);
                    }
                    Task task = pluginController.queryClosure(args[i+1],args[i+2]);
                    actions.add(task);
                    i+=2;
                    break;
                }
                    
                //await previous instructions
                case "-barrier": break;
 
                //server mode
                case "-s": 
                    isServer = true;
                    i++;
                    break;
                
                case "-w": 
                    useWebServices = true;
                    isServer = true;
                    i++;
                    break;
                    
                //terminate after this instruction
                case "-e": break;
                
                //use this config file
                case "-c": break;
                
                default:
                    i++;
                    break;
                
            }
            
            
        }
        return actions;
    }

    @Override
    public synchronized void run(){
        //if we are not a server, there is no point in initiating webservices
        if(!isServer) return;
        
        if(useWebServices){
            try {
                serviceController.startEnabledServices();
            }
            catch (Exception ex) {
                logger.log(Level.SEVERE, "failure initiating services:", ex);
                return;
            }
        }

        
        //blocks 
        while(running){
            try {
                wait(1000);
            }
            catch (InterruptedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }   
    }
    
    void quit(){running = false;}
    
}