/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk.
 *
 * Dicoogle/dicoogle-sdk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk;

import java.util.Collection;

import net.xeoh.plugins.base.Plugin;

import org.restlet.resource.ServerResource;

import pt.ua.dicoogle.sdk.settings.ConfigurationHolder;

/**
 * This is the class responsible for creating a Dicoolge plugin.
 * The developer may use this interface in order to manage and expose the implemented plugins. One instance
 * of each installed plugin set is created by injecting it as a {@link PluginImplementation}. All instances
 * are expected to be thread safe. It is highly recommended that provided collections are immutable, and
 * that no modifications are performed in getter methods.
 * 
 * @author psytek
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public interface PluginSet extends Plugin {
    
    /**
     * Get the indexer plugins enclosed in this plugin set.
     * This collection must be thread safe. 
     * @return IndexPluginInterface returns a list of active index plugins
     * @see IndexerInterface
     */
    public Collection<IndexerInterface> getIndexPlugins();

    /**
     * Get the graphical plugins enclosed in this plugin set.
     * This collection must be thread safe.
     * @return 
     * @deprecated the desktop-based remote user interface is deprecated
     */
    public Collection<GraphicalInterface> getGraphicalPlugins();

    /**
     * Get the query plugins enclosed in this plugin set.
     * This collection must be thread safe.
     * @return a collection of query plugins
     * @see QueryInterface
     */
    public Collection<QueryInterface> getQueryPlugins();
    
    /**
     * Get the storage plugins enclosed in this plugin set.
     * This collection must be thread safe.
     * @return Collection holding the StoragePlugins of this PluginSet
     */
    public Collection<StorageInterface> getStoragePlugins();
    
    /**
     * Obtain a collection of access to the RESTful resources. These plugins will be installed to the web service hierarchy
     * according to a name defined by the object's {@code toString()} method.
     * This collection must be thread safe.
     * @return a collection of Restlet-based server resources, implementing {@code toString()}
     * to provide the resource name
     */
    public Collection<ServerResource> getRestPlugins();
    
    /**
     * Obtain a collection of Jetty plugins, so as to implement web services via Dicoogle.
     * This collection must be thread safe.
     * @return a collection of Jetty plugins to the core application
     * @see JettyPluginInterface
     */
    public Collection<JettyPluginInterface> getJettyPlugins();
    
    /**
     * Get the plugin's name. This name will be used for identifying index/query/storage providers,
     * and should be unique among the total plugin sets installed.
     * @return the name of the plugin, never changes
     */
    public String getName();
    
    /**
     * Define the plugin's settings. This method will be called once after the plugin set was instantiated
     * with plugin-scoped settings. Dicoogle users can modify these settings by accessing the XML file with
     * the same name in the "Settings" folder. Developers may define such settings programmatically from the
     * plugin itself.
     * @param xmlSettings an XML-based configuration holder
     */
    public void setSettings(ConfigurationHolder xmlSettings);
    
    /**
     * Retrieve the plugin's settings.
     * @return an XML-based configuration holder
     */
    public ConfigurationHolder getSettings();

    /**
     * Signal a plugin to stop. Upon an invocation of this method, the plugin may clean allocated resources
     * and save state if required.
     */
    public void shutdown();
    
}
