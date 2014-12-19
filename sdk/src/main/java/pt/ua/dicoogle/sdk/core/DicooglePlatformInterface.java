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
package pt.ua.dicoogle.sdk.core;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import pt.ua.dicoogle.sdk.IndexerInterface;
import pt.ua.dicoogle.sdk.QueryInterface;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 *
 * Provides access to external plugins' interfaces.
 * <p>
 * For instance, if a graphical plugin needs to query, it will able to access
 * to other plugins with this platform. 
 * 
 * 
 * @author: Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Carlos Ferreira
 * @author Frederico Valente
 * @see QueryPluginInterface
 * @see IndexPluginInterface
 * @see GraphicPluginInterface
 */
public interface DicooglePlatformInterface {
    
    
    
    /**
     * Get a implementation of the plugin name
     * @param name name of the plugin 
     * @return IndexerInterface class of the plugin that implements this interface
     */
    public IndexerInterface requestIndexPlugin(String name);
    
    /**
     * Get a implementation of the plugin name
     * @param name name of the plugin 
     * @return QueryInterface class of the plugin that implements this interface
     */
    public QueryInterface requestQueryPlugin(String name);
    
    
    /**
     * Access to list of active index plugins
     * @return Collection of active IndexPluginInterface
     */
    public Collection<IndexerInterface> getAllIndexPlugins();
    
    /**
     * Access to a list of active query plugins
     * @return Collection of active QueryPluginInterface
     */
    public Collection<QueryInterface> getAllQueryPlugins();
    
    /**
     * 
     * @param storage
     * @return 
     */
    public StorageInterface getStoragePluginForSchema(String storage);
    
    /**
     * 
     * @param location
     * @return 
     */
    public StorageInterface getStorageForSchema(URI location);
    
    public Iterable<StorageInputStream> resolveURI(URI location);
    
    public Collection<StorageInterface> getStoragePlugins(boolean onlyEnabled);

	public StorageInterface getStorageForSchema(String schema);

	public Collection<QueryInterface> getQueryPlugins(boolean onlyEnabled);

	public List<String> getQueryProvidersName(boolean enabled);

	public QueryInterface getQueryProviderByName(String name,
			boolean onlyEnabled);

	public JointQueryTask queryAll(JointQueryTask holder, String query,
			Object... parameters) ;

	public Task<Iterable<SearchResult>> query(String querySource, String query,
			Object... parameters);

	public JointQueryTask query(JointQueryTask holder,
			List<String> querySources, String query, Object... parameters);

	public List<Task<Report>> index(URI path);

	public List<Report> indexBlocking(URI path);
}
