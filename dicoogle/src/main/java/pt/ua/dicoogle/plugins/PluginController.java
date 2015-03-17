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
package pt.ua.dicoogle.plugins;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.resource.ServerResource;

import pt.ua.dicoogle.sdk.IndexerInterface;
import pt.ua.dicoogle.sdk.JettyPluginInterface;
import pt.ua.dicoogle.sdk.PluginSet;
import pt.ua.dicoogle.sdk.QueryInterface;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.core.PlatformCommunicatorInterface;
import pt.ua.dicoogle.sdk.datastructs.QueryReport;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.taskManager.TaskManager;

/**
 *
 * PluginController is the core of the Plugins architecture.
 *
 * <p>
 * It loads the plugins, takes care of the list of active plugins and control
 * the tasks that are exchanged between plugins and core plugins
 *
 * @author Carlos Ferreira
 * @author Frederico Valente
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Tiago Marques Godinho.
 */
public class PluginController{

    private static final Logger logger = LogManager.getLogger(PluginController.class.getName());

    private Collection<PluginSet> pluginSets;
    private File pluginFolder;
    private static PluginController mainGlobalInstance;
 
    public static PluginController get(){return mainGlobalInstance;}
    
    public PluginController(File pathToPluginDirectory) {
    	logger.info("Creating PluginController Instance");
        pluginFolder = pathToPluginDirectory;
        pluginSets = new ArrayList<>();
        mainGlobalInstance = this;

        //the plugin directory does not exist. lets create it
        if (!pathToPluginDirectory.exists()) {
        	logger.info("Creating new Plugin Folder");
            pathToPluginDirectory.mkdirs();
        }

        //loads the plugins
        pluginSets = PluginFactory.getPlugins(pathToPluginDirectory);
        logger.info("Loaded Local Plugins");

        //loads plugins' settings and passes them to the plugin
        File settingsFolder = new File(pluginFolder.getPath() + "/settings/");
        if (!settingsFolder.exists()) {
        	logger.info("Creating Local Settings Folder");
            settingsFolder.mkdir();
        }

        for (PluginSet plugin : pluginSets) {
            System.err.println("LOADING:" + plugin.getName());
                        
            File pluginSettingsFile = new File(settingsFolder + "/" + plugin.getName() + ".xml");       
            try {
                ConfigurationHolder holder = new ConfigurationHolder(pluginSettingsFile);
                plugin.setSettings(holder);
            }
            catch (ConfigurationException e){
		logger.info(e);
            }
        }
        
        pluginSets.add(new DefaultFileStoragePlugin());
        logger.info("Added default storage plugin");
        
        initializePlugins(pluginSets);
        //initRestInterface(pluginSets);
        //initJettyInterface(pluginSets);
        logger.info("Initialized plugins");
    }
    
    /*
    *   Plugins may need to communicate with the platform.
    *   Plugins implementing the PlatformCommunicator interface have a setPlatformProxy
    *   which expects an implementation of the methods which it may call (given by PlatformCommunicatorInterface)
    *   This method insures all plugins implementing the interface now have
    *   a viable reference to those methods
    *
    */
    private void initializePlugins(Collection<PluginSet> plugins) {
        for (PluginSet set : plugins) {
            System.out.println("SetPlugins: " + set);
            if (set instanceof PlatformCommunicatorInterface) {
                ((PlatformCommunicatorInterface) set).setPlatformProxy(new DicooglePlatformProxy(this));
            }
        }
    }

    /**
     * Each pluginSet provides a collection of barebone rest interfaces Here we
     * check which interfaces are present and create a restlet component to
     * handle them. also we export them using common settings and security
     * profiles
     * @return 
     */
    /*private void initRestInterface(Collection<PluginSet> plugins) {
        System.err.println("Initialize plugin rest interfaces");

        ArrayList<ServerResource> restInterfaces = new ArrayList<>();
        for (PluginSet set : plugins) {
            Collection<ServerResource> restInterface = set.getRestPlugins();
            if (restInterface == null) {
                continue;
            }
            restInterfaces.addAll(restInterface);
        }

        for (ServerResource resource : restInterfaces) {
            DicoogleWebservice.attachRestPlugin(resource);
        }
        System.err.println("Finished initializing rest interfaces");
    }*/

    /*private void initJettyInterface(Collection<PluginSet> plugins) {
        System.err.println("initing jetty interface");
                
         ArrayList<JettyPluginInterface> jettyInterfaces = new ArrayList<>();
         for(PluginSet set : plugins){
        	 Collection<JettyPluginInterface> jettyInterface = set.getJettyPlugins();
        	 if(jettyInterface == null) continue;
        	 jettyInterfaces.addAll(jettyInterface);
         }
         
         DicoogleWeb jettyServer = ControlServices.getInstance().getWebServicePlatform();
         for(JettyPluginInterface resource : jettyInterfaces){
        	 jettyServer.addContextHandlers( resource.getJettyHandlers() );
         }
    }*/
    
    public Collection<JettyPluginInterface> getJettyPlugins(){
         ArrayList<JettyPluginInterface> jettyInterfaces = new ArrayList<>();
         for(PluginSet pluginSet : pluginSets){
            Collection<JettyPluginInterface> jettyInterface = pluginSet.getJettyPlugins();
            if(jettyInterface == null) continue;
                jettyInterfaces.addAll(jettyInterface);
         }
         return jettyInterfaces;
    }
    
    public Collection<ServerResource> getRestPlugins(){
        ArrayList<ServerResource> restInterfaces = new ArrayList<>();
        for (PluginSet pluginSet : pluginSets) {
            Collection<ServerResource> restInterface = pluginSet.getRestPlugins();
            if (restInterface == null) continue;
            restInterfaces.addAll(restInterface);
        }
        return restInterfaces;         
    }

    /**
     * Stops the plugins and saves the settings
     *
     */
    public void shutdown() throws IOException {
        for (PluginSet plugin : pluginSets) {
            //TODO: I Think it is better to enable auto-save settings
            /*Settings settings = plugin.getSettings();
            if (settings != null) {
                settings.save();
            }
	*/
            //lets notify the plugin that we are shutting down
            plugin.shutdown();
        }
    }

    /**
     * stops a pluginset. this could be more efficient, however this is hardly a
     * bottleneck TODO: needs more granularity, we should be able to stop only
     * the indexers or the queryers
     *
     * @param pluginName
     */
    public void stopPlugin(String pluginName) {
        for (PluginSet pluginSet : pluginSets) {
            if (pluginSet.getName().compareTo(pluginName) == 0) {
                //pluginSet.stop();
                return;
            }
        }
    }

    public void startPlugin(String pluginName) {
        for (PluginSet pluginSet : pluginSets) {
            if (pluginSet.getName().compareTo(pluginName) == 0) {
                //pluginSet.stop();
                return;
            }
        }
    }

    public Collection<IndexerInterface> getIndexingPlugins(boolean onlyEnabled) {
        ArrayList<IndexerInterface> indexers = new ArrayList<>();
        for (PluginSet pSet : pluginSets) {
            for (IndexerInterface index : pSet.getIndexPlugins()) {
                if (!index.isEnabled() && onlyEnabled) {
                    continue;
                }
                indexers.add(index);
            }
        }
        return indexers;
    }

    public Collection<StorageInterface> getStoragePlugins(boolean onlyEnabled) {
        ArrayList<StorageInterface> storagePlugins = new ArrayList<>();
        for (PluginSet pSet : pluginSets) {
            for (StorageInterface store : pSet.getStoragePlugins()) {
                if (!store.isEnabled() && onlyEnabled) {
                    continue;
                }
                storagePlugins.add(store);
            }
        }
        return storagePlugins;
    }

    /**
     * Resolve a URI to a DicomInputStream
     * @param location
     * @return 
     */
    public Iterable<StorageInputStream> resolveURI(URI location)
    {
        Collection<StorageInterface> storages = getStoragePlugins(true);
        
        for (StorageInterface store : storages) {
            
            if (store.handles(location)) 
            {
            	logger.info("Resolving URI: "+location.toString()+" Storage: "+store.getName() );
                return store.at(location);
            }
        }

    	logger.error("Could not resolve uri: "+location.toString());
        return Collections.emptyList();    
    }
    
    /**
     * TODO: this can be heavily improved if we keep a map of scheme->indexer
     * However we are not supposed to call this every other cycle.
     *
     * returns null if no suitable plugin is found
     * TODO: we should return a proxy storage that always returns error
     * 
     * @param location only the scheme matters
     * @return
     */
    public StorageInterface getStorageForSchema(URI location) {
    	if(location == null){
    		logger.error("NULL URI");
    		return null;
    	}
        Collection<StorageInterface> storages = getStoragePlugins(false);
        //System.out.println("Number of Plugins: "+storages.size());
        
        for (StorageInterface store : storages) {
            //System.out.println("Testing Storage Plugin: "+store.getScheme());
            if (store.handles(location)) {
            	logger.info("Retrieved Storage For Schema: "+location.toString());
                return store;
            }
        }
        logger.error("Could not get storage for schema: "+location.toString());
        return null;
    }
    
    public StorageInterface getStorageForSchema(String schema) {
        URI uri = null;
		try {
			uri = new URI(schema, "", "");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getStorageForSchema(uri);
    }

    public Collection<QueryInterface> getQueryPlugins(boolean onlyEnabled) {
        ArrayList<QueryInterface> queriers = new ArrayList<>();
        for (PluginSet pSet : pluginSets) {
            for (QueryInterface querier : pSet.getQueryPlugins()) {
                if (!querier.isEnabled() && onlyEnabled) {
                    continue;
                }
                queriers.add(querier);
            }
        }
        return queriers;
    }
   
    private TaskManager taskManager = new TaskManager(4);
    
    public List<String> getQueryProvidersName(boolean enabled){
    	Collection<QueryInterface> plugins = getQueryPlugins(enabled);
    	List<String> names = new ArrayList<>(plugins.size());
    	for(QueryInterface p : plugins){
    		names.add(p.getName());
    	}
    	//logger.info("Query Providers: "+Arrays.toString(names.toArray()) );
    	return names;
    }
    
    public QueryInterface getQueryProviderByName(String name, boolean onlyEnabled){
    	Collection<QueryInterface> plugins = getQueryPlugins(onlyEnabled);
    	for(QueryInterface p : plugins){
    		if(p.getName().equalsIgnoreCase(name)){
    			//logger.info("Retrived Query Provider: "+name);
    			return p;
    		}
    	}
    	logger.error("Could not retrive query provider:"+name+" OnlyEnabled: "+onlyEnabled);
    	return null;
    }
    
    // CONVENIENCE METHOD
    public IndexerInterface getIndexerByName(String name, boolean onlyEnabled){
    	Collection<IndexerInterface> plugins = getIndexingPlugins(onlyEnabled);
    	for(IndexerInterface p : plugins){
    		if(p.getName().equalsIgnoreCase(name)){
    			//logger.info("Retrived Query Provider: "+name);
    			return p;
    		}
    	}
    	logger.error("No Indexer Matching:"+name+" OnlyEnabled: "+onlyEnabled);
    	return null;
    }
    
    public Iterable<String> indexerNames(){
    	Collection<IndexerInterface> plugins = getIndexingPlugins(true);
        ArrayList<String> names = new ArrayList<>();
    	for(IndexerInterface p : plugins){
            names.add(p.getName());
    	}
    	return names;
    }
        
    public Task<QueryReport> queryDispatch(String querySource, final String query, final Object ... parameters){
        Task<QueryReport> t = queryClosure(querySource, query, parameters);       
        taskManager.dispatch(t);        
        return t;//returns the handler to obtain the computation results
    }
    
    public Task<QueryReport> queryDispatch(final Iterable<String> querySources, final String query, final Object ... parameters){        
        
        /*
        * Creates a task that dispatches several tasks, one per enabled query plugin,
        * waits their completion and merges the results into a single report.
        * A dispatched task object is returned.
        */
        
        Task<QueryReport> queryTask = new Task<>("multiple query",
            new Callable<QueryReport>(){
                @Override
                public QueryReport call() throws Exception {
                    ArrayList<Task<QueryReport>> tasks = new ArrayList<>();
                    for(String sourcePlugin : querySources){
                        Task<QueryReport> task = queryClosure(sourcePlugin, query, parameters);
                        tasks.add(task);
                        taskManager.dispatch(task);
                    }
                    
                    QueryReport q = new QueryReport();
                    for(Task<QueryReport> t:tasks){
                        q.merge(t.get());
                    }
                    return q;
                    
                }
            });


        //and executes said task asynchronously
        taskManager.dispatch(queryTask);
        return queryTask;
    }
    
    public Task<QueryReport> queryClosure(String querySource, final String query, final Object ... parameters){
    	final QueryInterface queryEngine = getQueryProviderByName(querySource, true);
    	//returns a tasks that runs the query from the selected query engine
        Task<QueryReport> queryTask = new Task<>(querySource,
            new Callable<QueryReport>(){
                @Override public QueryReport call() throws Exception {
                    if(queryEngine == null) return QueryReport.EmptyReport;
                    return queryEngine.query(query, parameters);
                }
            });
        //logger.info("Prepared Query Task: QueryString");
        return queryTask;
    }        
 
        //returns a task, that has not yet been dispatched
        //this means the task can run in blocking mode on the caller thread, or
        //be dispatched to the task manager
        public Task<QueryReport> queryClosure(final Iterable<String> querySources, final String query, final Object ... parameters){        
            /*
            * Creates a task that dispatches several tasks, one per enabled query plugin,
            * waits their completion and merges the results into a single report.
            * A dispatched task object is returned.
            */
            Task<QueryReport> queryTask = new Task<>("multiple query",
                new Callable<QueryReport>(){
                    @Override
                    public QueryReport call() throws Exception {
                        ArrayList<Task<QueryReport>> tasks = new ArrayList<>();
                        for(String sourcePlugin : querySources){
                            Task<QueryReport> task = queryClosure(sourcePlugin, query, parameters);
                            tasks.add(task);
                            taskManager.dispatch(task);
                        }

                        QueryReport q = new QueryReport();
                        for(Task<QueryReport> t:tasks){
                            q.merge(t.get());
                        }
                        return q;

                    }
                });
            return queryTask;
        }

    
    
    /*
     * Given an URI (which may be a path to a dir or file, a web resource or whatever)
     * this method creates a task that
     * calls the appropriate indexers and instructs them to index the data pointed to by the URI
     * it is up to the caller to run the task asynchronously by feeding it to an executor
     * or in a blocking way by calling the get() method of the task
     * 
     * 
     * 
     */
    public List<Task<Report>> index(URI path) {
    	logger.info("Starting Indexing procedure for "+path.toString());
        StorageInterface store = getStorageForSchema(path);

        if(store==null){ 
            logger.error("No storage plugin detected");
            return Collections.emptyList(); 
        }
        
        Collection<IndexerInterface> indexers = getIndexingPlugins(true);
        ArrayList<Task<Report>> rettasks = new ArrayList<>();
        final  String pathF = path.toString();
        for(IndexerInterface indexer : indexers){            	
        	Task<Report> task = indexer.index(store.at(path));
                task.onCompletion(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                        System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                        System.out.println("Task accomplished " + pathF);
                        System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                        System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                    }
                });
                
                
            if(task == null) continue;
            taskManager.dispatch(task);
            rettasks.add(task);
        }
        logger.info("Finised firing all Indexing plugins for "+path.toString());
        
        return rettasks;    	
    }
    
    //
    public Task<Report> index(String pluginName, URI path) {
    	logger.info("Starting Indexing procedure for "+path.toString());
        StorageInterface store = getStorageForSchema(path);

        if(store==null){ 
            logger.error("No storage plugin detected");
            return null;
        }
        
        IndexerInterface indexer = getIndexerByName(pluginName, true);
        final  String pathF = path.toString();
        
    	Task<Report> task = indexer.index(store.at(path));
        task.onCompletion(new Runnable() {
            @Override
            public void run() {
                System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                System.out.println("Index Task accomplished: " + pathF);
                System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
            }
        });
            
            
	taskManager.dispatch(task);
	logger.info("FIRED INDEXER: {} FOR URI: {}", pluginName, path.toString());
        
        return task;    	
    }
    
        public Task<Report> indexClosure(String pluginName, URI path) {
    	logger.info("Starting Indexing procedure from Closure for "+path.toString());
        StorageInterface store = getStorageForSchema(path);

        if(store==null){ 
            logger.error("No storage plugin detected");
            return null;
        }
        
        IndexerInterface indexer = getIndexerByName(pluginName, true);
        if(indexer == null){
            String names = "";
            for(String s : indexerNames()) names += s+" ";
            logger.error("Indexer not found:"+pluginName+"\n available:"+names);
            return null;
        }
        
        final  String pathF = path.toString();
        
    	Task<Report> task = indexer.index(store.at(path));
        task.onCompletion(new Runnable() {
            @Override
            public void run() {
                System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                System.out.println("Index Task accomplished: " + pathF);
                System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
            }
        });
        
        return task;    	
    }

    
    
    
    public void unindex(URI path) {
    	logger.info("Starting Indexing procedure for "+path.toString());
        StorageInterface store = getStorageForSchema(path);

        if(store==null){ 
            logger.error("No storage plugin detected");
        }
        
        Collection<IndexerInterface> indexers = getIndexingPlugins(true);
        ArrayList<Task<Report>> rettasks = new ArrayList<>();
        
        for(IndexerInterface indexer : indexers){            
        	indexer.unindex(path);
        }
        logger.info("Finised firing all undexing plugins for "+path.toString());
        
    }       
    
    
    /*
     * Convinience method that calls index(URI) and runs the returned
     * tasks on the executing thread 
     */
    public List<Report> indexBlocking(URI path) {
    	logger.info("Starting Indexing Blocking procedure for "+path.toString());
        List<Task<Report>> ret = index(path);
        
        ArrayList<Report> reports = new ArrayList<>(ret.size());
        for(Task<Report> t : ret){
            try {
                reports.add(t.get());
            }
            catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        logger.info("Finished Indexing Blocking procedure for "+path.toString());
        
        return reports;
    }

}
