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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipFile;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restlet.resource.ServerResource;

import pt.ua.dicoogle.core.ServerSettings;
import pt.ua.dicoogle.server.ControlServices;
import pt.ua.dicoogle.plugins.webui.WebUIPlugin;
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
import pt.ua.dicoogle.plugins.webui.WebUIPluginManager;
import pt.ua.dicoogle.sdk.DicooglePlugin;
import pt.ua.dicoogle.taskManager.RunningIndexTasks;
import pt.ua.dicoogle.taskManager.TaskManager;
import pt.ua.dicoogle.server.PluginRestletApplication;

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
 * @author Tiago Marques Godinho
 * @author Eduardo Pinho
 */
public class PluginController{

    private static final Logger logger = LoggerFactory.getLogger(PluginController.class);
    private static PluginController instance;

    public synchronized static PluginController getInstance() {
        if (instance == null) {
            instance = new PluginController(new File("Plugins"));
        }
        return instance;
    }
    private final Collection<PluginSet> pluginSets;
    private File pluginFolder;
    private TaskQueue tasks = null;

	private PluginSet remoteQueryPlugins = null;
    private final WebUIPluginManager webUI;
    private final DicooglePlatformProxy proxy;
    
    public PluginController(File pathToPluginDirectory) {
    	logger.info("Creating PluginController Instance");
        pluginFolder = pathToPluginDirectory;

        tasks = new TaskQueue();

        //the plugin directory does not exist. lets create it
        if (!pathToPluginDirectory.exists()) {
        	logger.info("Creating new Plugin Folder");
            pathToPluginDirectory.mkdirs();
        }

        //loads the plugins
        pluginSets = PluginFactory.getPlugins(pathToPluginDirectory);
        //load web UI plugins (they are not Java, so the process is delegated to another entity)
        this.webUI = new WebUIPluginManager();
        // loadByPluginName all at "WebPlugins"
        this.webUI.loadAll(new File("WebPlugins"));
        
        // go through each jar'd plugin and fetch their WebPlugins
        for (File j : FileUtils.listFiles(pluginFolder, new String[]{"jar", "zip"}, false)) {
            try {
                this.webUI.loadAllFromZip(new ZipFile(j));
            } catch (IOException ex) {
                // ignore
                logger.warn("Failed to load web UI plugins from {}: {}", j.getName(), ex.getMessage());
            }
        }
        
        logger.info("Loaded Local Plugins");

        //loads plugins' settings and passes them to the plugin
        File settingsFolder = new File(pluginFolder.getPath() + "/settings/");
        if (!settingsFolder.exists()) {
        	logger.info("Creating Local Settings Folder");
            settingsFolder.mkdir();
        }

        for (PluginSet plugin : pluginSets) {
            logger.info("Loading plugin: " + plugin.getName());
                        
            File pluginSettingsFile = new File(settingsFolder + "/" + plugin.getName() + ".xml");       
            try {
                ConfigurationHolder holder = new ConfigurationHolder(pluginSettingsFile);
                if(plugin.getName().equals("RemotePluginSet")){
                	this.remoteQueryPlugins = plugin;
                	holder.getConfiguration().setProperty("NodeName", ServerSettings.getInstance().getNodeName());
    	        	holder.getConfiguration().setProperty("TemporaryPath", ServerSettings.getInstance().getPath());
                	
                	logger.info("Started Remote Communications Manager");
                }
                applySettings(plugin, holder);
            }
            catch (ConfigurationException e){
                logger.error("Failed to create configuration holder", e);
			}
            catch (UnsupportedOperationException e){
                // TODO log this properly, remove plugin from plugin list
                logger.error(e.getMessage(),e);
            }
        }
        logger.info("Settings pushed to plugins");
        webUI.loadSettings(settingsFolder);
        logger.info("Settings pushed to web UI plugins");
        
        pluginSets.add(new DefaultFileStoragePlugin());
        logger.info("Added default storage plugin");
        
        this.proxy = new DicooglePlatformProxy(this);
        
        initializePlugins(pluginSets);
        initRestInterface(pluginSets);
        initJettyInterface(pluginSets);
        logger.info("Initialized plugins");
    }
    
    private void initializePlugins(Collection<PluginSet> plugins) {
        for (PluginSet set : plugins) {
            logger.debug("SetPlugins: {}", set);
            
            // provide platform to each plugin interface
            final Collection<Collection<?>> all = Arrays.asList(
                    set.getStoragePlugins(),
                    set.getIndexPlugins(),
                    set.getQueryPlugins(),
                    set.getJettyPlugins(),
                    set.getRestPlugins()
            );
            for (Collection interfaces : all) {
                if (interfaces == null) {
                    logger.debug("Plugin set {} provided a null collection!");
                    continue;
                }
                for (Object o : interfaces) {
                    if (o instanceof PlatformCommunicatorInterface) {
                        ((PlatformCommunicatorInterface)o).setPlatformProxy(proxy);
                    }
                }
            }

            // and to the set itself
            if (set instanceof PlatformCommunicatorInterface) {
                ((PlatformCommunicatorInterface) set).setPlatformProxy(proxy);
            }
        }
    }
    
    private void applySettings(PluginSet set, ConfigurationHolder holder) {

        // provide platform to each plugin interface
        final Collection<Collection<? extends DicooglePlugin>> all = Arrays.asList(
                set.getStoragePlugins(),
                set.getIndexPlugins(),
                set.getQueryPlugins(),
                set.getJettyPlugins()
        );
        for (Collection<? extends DicooglePlugin> interfaces : all) {
            if (interfaces == null) continue;
            for (DicooglePlugin p : interfaces) {
                p.setSettings(holder);
            }
        }
        
        set.setSettings(holder);
        
    }
    
    /**
     * Each pluginSet provides a collection of barebone rest interfaces Here we
     * check which interfaces are present and create a restlet component to
     * handle them. also we export them using common settings and security
     * profiles
     */
    private void initRestInterface(Collection<PluginSet> plugins) {
        logger.info("Initializing plugin rest interfaces");

        ArrayList<ServerResource> restInterfaces = new ArrayList<>();
        for (PluginSet set : plugins) {
            Collection<ServerResource> restInterface = set.getRestPlugins();
            if (restInterface == null) {
                continue;
            }
            restInterfaces.addAll(restInterface);
        }

        for (ServerResource resource : restInterfaces) {
            PluginRestletApplication.attachRestPlugin(resource);
        }
        logger.info("Finished initializing rest interfaces");
    }

    private void initJettyInterface(Collection<PluginSet> plugins) {
        logger.info("Initializing jetty interface");
                
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
     * Shut down all plugins.
     */
    public void shutdown() {
        for (PluginSet plugin : pluginSets) {
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
     * @param args
     * @return 
     */
    public Iterable<StorageInputStream> resolveURI(URI location, Object ...args)
    {
        Collection<StorageInterface> storages = getStoragePlugins(true);
        
        for (StorageInterface store : storages) {
            if (store.handles(location)) 
            {
            	logger.debug("Resolving URI: {} Storage: {}", location, store.getName());
                return store.at(location, args);
            }
        }

    	logger.error("Could not resolve uri: {}", location);
        return Collections.emptyList();    
    }
    
    /** Retrieve a storage interface capable of handling files on a given location.
     * 
     * TODO: this can be heavily improved if we keep a map of scheme->indexer
     * However we are not supposed to call this every other cycle.
     *
     * TODO: we should return a proxy storage that always returns error
     * 
     * @todo "schema" is a typo, should read "scheme"
     * 
     * @param location a URI of the location, only the scheme matters
     * @return a storage interface capable of handling the location, null if no suitable plugin is found
     */
    public StorageInterface getStorageForSchema(URI location) {
    	if(location == null){
            logger.warn("URI for retrieving storage interface is null, ignoring");
            return null;
    	}
        Collection<StorageInterface> storages = getStoragePlugins(false);
        
        for (StorageInterface store : storages) {
            if (store.handles(location)) {
            	logger.debug("Retrieved storage for scheme: {}", location);
                return store;
            }
        }
        logger.warn("Could not get storage for scheme: {}", location);
        return null;
    }
    
    /** Retrieve a storage interface capable of handling files with the given scheme.
     * 
     * TODO: this can be heavily improved if we keep a map of scheme->indexer
     * However we are not supposed to call this every other cycle.
     *
     * TODO: we should return a proxy storage that always returns error
     * 
     * @param scheme a URI of the location, only the scheme matters
     * @return a storage interface capable of handling the location, null if no suitable plugin is found
     */
    public StorageInterface getStorageForSchema(String scheme) {
        URI uri = URI.create(scheme + ":/");
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
    	logger.error("Could not retrive query provider {} for onlyEnabled = {}", name, onlyEnabled);
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
    	logger.error("No indexer matching name {} for onlyEnabled = {}", name, onlyEnabled);
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
    	
    	List<Task<Iterable<SearchResult>>> tasklist = new ArrayList<>();
        for(String p : querySources){
        	Task<Iterable<SearchResult>> task = getTaskForQuery(p, query, parameters);
        	tasklist.add(task);
        	holder.addTask(task);
        }

        //and executes said task asynchronously
        for(Task<?> t : tasklist)
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
     */
    public List<Task<Report>> index(URI path) {
    	logger.info("Starting Indexing procedure for {}", path.toString());
        StorageInterface store = getStorageForSchema(path);

        if(store==null) {
            logger.error("No storage plugin detected");
            return Collections.emptyList(); 
        }
        
        Collection<IndexerInterface> indexers= getIndexingPlugins(true);
        //Collection<IndexerInterface> indexers = getIndexingPluginsByMimeType(path);
        ArrayList<Task<Report>> rettasks = new ArrayList<>();
        final  String pathF = path.toString();
        for(IndexerInterface indexer : indexers){            
        	Task<Report> task = indexer.index(store.at(path));
            if(task == null) continue;
            final String taskUniqueID = UUID.randomUUID().toString();
            task.setName(String.format("[%s]index %s", indexer.getName(), path));
            task.onCompletion(new Runnable() {
                @Override
                public void run() {
                    logger.info("Task [{}] complete: {} is indexed", taskUniqueID, pathF);
                }
            });
                
            taskManager.dispatch(task);
            rettasks.add(task);
            RunningIndexTasks.getInstance().addTask(taskUniqueID, task);
        }
        logger.info("Finished firing all indexing plugins for {}", path);
        
        return rettasks;    	
    }     
    
    //
    public List<Task<Report>> index(String pluginName, URI path) {
    	logger.info("Starting Indexing procedure for {}", path);
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
        if(task != null) {
            task.setName(String.format("[%s]index %s", pluginName, path));
            task.onCompletion(new Runnable() {

                @Override
                public void run() {
                    logger.info("Task [{}] complete: {} is indexed", taskUniqueID, pathF);
                    
                    //RunningIndexTasks.getInstance().removeTask(taskUniqueID);
                }
            });
            
	        taskManager.dispatch(task);
	        
	        rettasks.add(task);
	        logger.info("Fired indexer {} for URI {}", pluginName, path.toString());
	        RunningIndexTasks.getInstance().addTask(taskUniqueID, task);
        }
        
        return rettasks;    	
    }

    public void unindex(URI path) {
    	logger.info("Starting unindexing procedure for {}", path.toString());
        this.doUnindex(path, this.getIndexingPlugins(true));
    }

    /** Issue the removal of indexed entries in a path from the given indexers.
     * 
     * @param path the URI of the directory or file to unindex
     * @param indexProviders a collection of providers
     */
    public void unindex(URI path, Collection<String> indexProviders) {
    	logger.info("Starting unindexing procedure for {}", path);
        
        if (indexProviders != null) {
            List<IndexerInterface> indexers = new ArrayList<>();
            for (String provider : indexProviders) {
                indexers.add(this.getIndexerByName(provider, true));
            }
            this.doUnindex(path, indexers);
        } else {
            this.doUnindex(path, this.getIndexingPlugins(true));
        }
    }
    
    /** Issue an unindexation procedure to the given indexers.
     * 
     * @param path the URI of the directory or file to unindex
     * @param indexProviders a collection of providers
     */
    private void doUnindex(URI path, Collection<IndexerInterface> indexers) {
        for (IndexerInterface indexer : indexers) {            
        	indexer.unindex(path);
        }
        logger.info("Finished unindexing {}", path);
    }
    
    public void remove(URI uri){
      StorageInterface si = getStorageForSchema(uri);
      if(si != null)
        doRemove(uri, si);
      else
        logger.error("Could not find storage plugin to handle URI: {}", uri);      
    }
    
    public void doRemove(URI uri, StorageInterface si) {
        if(si.handles(uri)){
            si.remove(uri); 
        } else {
            logger.warn("Storage Plugin does not handle URI: {},{}", uri, si);
        }
        logger.info("Finished removing {}", uri);
    }
    /*
     * Convinience method that calls index(URI) and runs the returned
     * tasks on the executing thread 
     */
    public List<Report> indexBlocking(URI path) {
    	logger.info("Starting indexing blocking procedure for {}", path);
        List<Task<Report>> ret = index(path);
        
        ArrayList<Report> reports = new ArrayList<>(ret.size());
        for(Task<Report> t : ret){
        	try {
				reports.add(t.get());
			}
            catch (InterruptedException | ExecutionException e) {
                logger.error(e.getMessage(), e);
			}
        }
        logger.info("Finished indexing {}", path);
        
        return reports;
    }

    //METHODs FOR PluginController4Users
    //TODO:this method is a workaround! we do get rightmenu items, but only for the search window
    //which should be moved to plugins and hence we are assuming too much in here!
 
    @Deprecated
	public List<JMenuItem> getRightButtonItems() {
        logger.info("getRightButtonItems()");
        ArrayList<JMenuItem> rightMenuItems = new ArrayList<>();
        
        for (PluginSet set : pluginSets) {
            logger.info("Set plugins: {}", set.getGraphicalPlugins());
            Collection<GraphicalInterface> graphicalPlugins = set.getGraphicalPlugins();
            if (graphicalPlugins == null) {
                continue;
            }
            logger.info("Looking for plugin");
            for (GraphicalInterface gpi : graphicalPlugins) {
                logger.info("GPI: {}", gpi);
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
    @Deprecated
    public List<JPanel> getTabItems() {
        logger.info("getTabItems");
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

    @Deprecated
    public List<JMenuItem> getMenuItems() {
        logger.info("getMenuItems");
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
    
    // Methods for Web UI 

    /** Retrieve all web UI plugin descriptors for the given slot id.
     * 
     * @param ids the slot id's for the plugin ("query", "result", "menu", ...), empty or null for any slot
     * @return a collection of web UI plugins.
     */
    public Collection<WebUIPlugin> getWebUIPlugins(String... ids) {
        logger.debug("getWebUIPlugins(slot ids: {})", ids != null ? Arrays.asList(ids) : "<any>");
        List<WebUIPlugin> plugins = new ArrayList<>();
        Set<String> idSet = Collections.EMPTY_SET;
        if (ids != null) {
            idSet = new HashSet<>(Arrays.asList(ids));
        }
        for (WebUIPlugin plugin : webUI.pluginSet()) {
            if (!plugin.isEnabled()) {
                continue;
            }
            if (idSet.isEmpty() || idSet.contains(plugin.getSlotId())) {
                plugins.add(plugin);
            }
        }
        return plugins;
    }
    
    /** Retrieve the web UI plugin descriptor of the plugin with the given name.
     * 
     * @param name the unique name of the plugin
     * @return a web UI plugin descriptor object, or null if no such plugin exists or is inactive
     */
    public WebUIPlugin getWebUIPlugin(String name) {
        logger.debug("getWebUIPlugin(name: {})", name);
        WebUIPlugin plugin = webUI.get(name);
        return plugin == null ? null
                : plugin.isEnabled() ? plugin : null;
    }

    /** Retrieve the web UI plugin descriptor package.json.
     * 
     * @param name the unique name of the plugin
     * @return the full contents of the package.json, null if the plugin is not available
     */
    public String getWebUIPackageJSON(String name) {
        logger.debug("getWebUIPackageJSON(name: {})", name);
        try {
            Object o = webUI.retrieveJSON(name);
            return (o != null)
                    ? o.toString()
                    : null;
        } catch (IOException ex) {
            logger.error("Failed to retrieve package JSON", ex);
            return null;
        }
    }

    /** Retrieve the web UI plugin module code.
     * 
     * @param name the unique name of the plugin
     * @return the full contents of the module file, null if the plugin is not available
     */
    public String getWebUIModuleJS(String name) {
        logger.debug("getWebUIModuleJS(name: {})", name);
        try {
            return webUI.retrieveModuleJS(name);
        } catch (IOException ex) {
            logger.error("Failed to retrieve module", ex);
            return null;
        }
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
