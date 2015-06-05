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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.resource.ServerResource;

import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.rGUI.server.controllers.ControlServices;
import pt.ua.dicoogle.sdk.GraphicalInterface;
import pt.ua.dicoogle.sdk.IndexerInterface;
import pt.ua.dicoogle.sdk.JettyPluginInterface;
import pt.ua.dicoogle.sdk.PluginSet;
import pt.ua.dicoogle.sdk.QueryInterface;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.Utils.TaskQueue;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.core.PlatformCommunicatorInterface;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.server.web.DicoogleWeb;
import pt.ua.dicoogle.taskManager.RunningIndexTasks;
import pt.ua.dicoogle.taskManager.TaskManager;
import pt.ua.dicoogle.webservices.DicoogleWebservice;

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
    
    private static PluginController instance;

    public synchronized static PluginController getInstance() {
        if (instance == null) {
            instance = new PluginController(new File("Plugins"));
        }
        return instance;
    }
    private Collection<PluginSet> pluginSets;
    private File pluginFolder;
    private TaskQueue tasks = null;

	private PluginSet remoteQueryPlugins = null;
    
    public PluginController(File pathToPluginDirectory) {
    	logger.info("Creating PluginController Instance");
        pluginFolder = pathToPluginDirectory;
        pluginSets = new ArrayList<>();

        tasks = new TaskQueue();

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
                if(plugin.getName().equals("RemotePluginSet")){
                	this.remoteQueryPlugins = plugin;
                	holder.getConfiguration().setProperty("NodeName", ServerSettings.getInstance().getNodeName());
    	        	holder.getConfiguration().setProperty("TemporaryPath", ServerSettings.getInstance().getPath());
                	
                	logger.info("Started Remote Communications Manager");
                }
                plugin.setSettings(holder);
            }
            catch (ConfigurationException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            catch (UnsupportedOperationException e){
                // TODO log this properly, remove plugin from plugin list
                e.printStackTrace();
            }
        }
        logger.info("Settings pushed to plugins");
        
        pluginSets.add(new DefaultFileStoragePlugin());
        logger.info("Added default storage plugin");
        
        initializePlugins(pluginSets);
        initRestInterface(pluginSets);
        initJettyInterface(pluginSets);
        logger.info("Initialized plugins");
    }
    
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
     */
    private void initRestInterface(Collection<PluginSet> plugins) {
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
    }

    private void initJettyInterface(Collection<PluginSet> plugins) {
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
            //lets the plugin know we are shutting down
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

    public void addTask(TaskRequest task) {
        this.tasks.addTask(task);
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
    
    //TODO: CONVENIENCE METHOD
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
    
    public JointQueryTask queryAll(JointQueryTask holder, final String query, final Object ... parameters)
    {
    	//logger.info("Querying all providers");
    	List<String> providers = this.getQueryProvidersName(true);
    	
    	return query(holder, providers, query, parameters);        
    }
    
    public Task<Iterable<SearchResult>> query(String querySource, final String query, final Object ... parameters){
        Task<Iterable<SearchResult>> t = getTaskForQuery(querySource, query, parameters);       
        taskManager.dispatch(t);
        //logger.info("Fired Query Task: "+querySource +" QueryString:"+query);
        
        return t;//returns the handler to obtain the computation results
    }
    
    public JointQueryTask query(JointQueryTask holder, List<String> querySources, final String query, final Object ... parameters){
        if(holder == null)
        	return null;
    	
    	List<Task<Iterable<SearchResult>>> tasks = new ArrayList<>();
        for(String p : querySources){
        	Task<Iterable<SearchResult>> task = getTaskForQuery(p, query, parameters);
        	tasks.add(task);
        	holder.addTask(task);
        }

        //and executes said task asynchronously
        for(Task<?> t : tasks)
        	taskManager.dispatch(t);

        //logger.info("Fired Query Tasks: "+Arrays.toString(querySources.toArray()) +" QueryString:"+query);
        return holder;//returns the handler to obtain the computation results
    }
    
    private Task<Iterable<SearchResult>> getTaskForQuery(String querySource, final String query, final Object ... parameters){
    	final QueryInterface queryEngine = getQueryProviderByName(querySource, true);
    	//returns a tasks that runs the query from the selected query engine
        Task<Iterable<SearchResult>> queryTask = new Task<>(querySource,
            new Callable<Iterable<SearchResult>>(){
            @Override public Iterable<SearchResult> call() throws Exception {
                if(queryEngine == null) return Collections.emptyList();
                return queryEngine.query(query, parameters);
            }
        });
        //logger.info("Prepared Query Task: QueryString");
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
            final String taskUniqueID = UUID.randomUUID().toString();
            if(task == null) continue;
            task.setName(String.format("[%s]index %s", indexer.getName(), path));
                task.onCompletion(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                        System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                        System.out.println("Task accomplished " + pathF);
                        System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                        System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                        
                        //RunningIndexTasks.getInstance().removeTask(taskUniqueID);
                    }
                });
                
            taskManager.dispatch(task);
            rettasks.add(task);
            RunningIndexTasks.getInstance().addTask(taskUniqueID, task);
        }
        logger.info("Finised firing all Indexing plugins for "+path.toString());
        
        return rettasks;    	
    }     
    
    //
    public List<Task<Report>> index(String pluginName, URI path) {
    	logger.info("Starting Indexing procedure for "+path.toString());
        StorageInterface store = getStorageForSchema(path);

        if(store==null){ 
        	logger.error("No storage plugin detected");
            return Collections.emptyList(); 
        }
        
        final String taskUniqueID = UUID.randomUUID().toString();
        
        IndexerInterface indexer = getIndexerByName(pluginName, true);
        ArrayList<Task<Report>> rettasks = new ArrayList<>();
        final  String pathF = path.toString();
    	Task<Report> task = indexer.index(store.at(path));
        task.setName(String.format("[%s]index %s", pluginName, path));
           task.onCompletion(new Runnable() {

                @Override
                public void run() {
                    System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                    System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                    System.out.println("Task accomplished " + pathF);
                    System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                    System.out.println("## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ## ");
                    
                    
                    //RunningIndexTasks.getInstance().removeTask(taskUniqueID);
                }
            });
            
            
        if(task != null){
	        taskManager.dispatch(task);
	        
	        rettasks.add(task);
	        RunningIndexTasks.getInstance().addTask(taskUniqueID, task);
	        
	        logger.info("FIRED INDEXER: {} FOR URI: {}", pluginName, path.toString());
        }
        logger.error("UNKOWN ERROR CALLING INDEXER: {}", pluginName);
        
        return rettasks;    	
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
        	
        	boolean result = indexer.unindex(path);
            
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

    //METHODs FOR PluginController4Users
    //TODO:this method is a workaround! we do get rightmenu items, but only for the search window
    //which should be moved to plugins and hence we are assuming too much in here!
 
	public List<JMenuItem> getRightButtonItems() {
        System.out.println("getRightButtonItems");
        System.out.println(pluginSets);
        ArrayList<JMenuItem> rightMenuItems = new ArrayList<>();
        
        
        for (PluginSet set : pluginSets) {
            System.out.println("Set plugins: ");
            System.out.println(set.getGraphicalPlugins());
            Collection<GraphicalInterface> graphicalPlugins = set.getGraphicalPlugins();
            if (graphicalPlugins == null) {
                continue;
            }
            System.out.println("Looking for plugin");
            for (GraphicalInterface gpi : graphicalPlugins) {
                System.out.println("GPI: " + gpi);
                ArrayList<JMenuItem> rbPanels = gpi.getRightButtonItems();
                if (rbPanels == null) {
                    continue;
                }
                rightMenuItems.addAll(rbPanels);
            }
        }
        return rightMenuItems;
    }

    //returns a list of tabs from all plugins
    public List<JPanel> getTabItems() {
        System.out.println("getTabItems");
        System.out.println(pluginSets);
        ArrayList<JPanel> panels = new ArrayList<>();

        for (PluginSet set : pluginSets) {
            Collection<GraphicalInterface> graphicalPlugins = set.getGraphicalPlugins();
            if (graphicalPlugins == null) {
                continue;
            }
            for (GraphicalInterface gpi : graphicalPlugins) {
                ArrayList<JPanel> tPanels = gpi.getTabPanels();
                if (tPanels == null) {
                    continue;
                }
                panels.addAll(tPanels);
            }
        }
        return panels;
    }

    public List<JMenuItem> getMenuItems() {
        System.out.println("getMenuItems");
        System.out.println(pluginSets);
        ArrayList<JMenuItem> items = new ArrayList<>();

        for (PluginSet set : pluginSets) {
            Collection<GraphicalInterface> graphicalPlugins = set.getGraphicalPlugins();
            if (graphicalPlugins == null) {
                continue;
            }

            for (GraphicalInterface gpi : graphicalPlugins) {
                Collection<JMenuItem> setItems = gpi.getMenuItems();
                if (setItems == null) {
                    continue;
                }
                items.addAll(setItems);
            }
        }
        return items;
    }
    
    //METHODS FOR SERVICE:JAVA
    /**
     *
     * TODO: REVIEW! BELOW
     *
     * Checks if the plugin exists and has advanced/internal settings.
     *
     * @param pluginName the name of the plugin.
     * @return true if the plugin exists and has at least one advance/internal
     * settings, false otherwise.
     */
    public boolean hasAdvancedSettings(String pluginName) {
        return false;
    }

    public HashMap<String, String> getAdvancedSettingsHelp(String pluginName) {
        return null;
    }
}
