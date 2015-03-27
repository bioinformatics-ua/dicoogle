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
 * This is the class responsible for create a sandbox for the plugin.
 * The developer can use this interface to manager the implemented plugins
 * 
 * @author psytek
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public interface PluginSet extends Plugin {
    
    /**
     * Gets a list of Indexers
     * @return IndexPluginInterface returns a list of active index plugins
     */
    public Collection<IndexerInterface> getIndexPlugins();

    /**
     * Get graphical plugins. This plugins can interact with the GUI of Dicoogle
     * Moreover, they can change the 
     * @return 
     */
    public Collection<GraphicalInterface> getGraphicalPlugins() ;

    /**
     * Get Query Plugins. This plugins will allow to search over the 
     * @return List returns a list of QueryPluginInterface
     */
    public Collection<QueryInterface> getQueryPlugins() ;

    /**
     * Access to the RESTful resources
     * @return List returns a list of RESTful resources
     */
    public Collection<ServerResource> getRestPlugins() ;   
    
    /**
     * 
     * Returns Jetty plugins
     * This is still experimental...
     * @return Returns a list of JettyPluginInterface to the core application
     * 
     */
    public Collection<JettyPluginInterface> getJettyPlugins();
    
    /**
     * Get the plugin name
     * @return String returns the name of plugin
     */
    public String getName();
    
    
    /**
     * Sets the plugin settings
     */
    public void setSettings(ConfigurationHolder xmlSettings);
    
    
    /**
     * retrieves a plugin's settings
     * @return an xml string with the settings
     */
    public ConfigurationHolder getSettings();

    /**
     * Signals a plugin to stop, and save state if required
     */
    public void shutdown();
    

    
    /**
     * Gets the StoragePlugins enclosed in this plugin set.
     * @return Collection holding the StoragePlugins of this PluginSet
     */
    public Collection<StorageInterface> getStoragePlugins();
    
}
