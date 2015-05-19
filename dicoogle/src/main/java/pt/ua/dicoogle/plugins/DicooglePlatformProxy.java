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

import java.net.URI;
import pt.ua.dicoogle.sdk.IndexerInterface;
import pt.ua.dicoogle.sdk.QueryInterface;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.core.DicooglePlatformInterface;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * Proxy to the implementations of Plugin Controller
 *
 * @author psytek
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class DicooglePlatformProxy implements DicooglePlatformInterface {

    private final PluginController pluginController;

    public DicooglePlatformProxy(PluginController pluginController) {
        this.pluginController = pluginController;
    }

    @Override
    public IndexerInterface requestIndexPlugin(String name) {
        Iterable<IndexerInterface> indexers = pluginController.getIndexingPlugins(true);
        for (IndexerInterface index : indexers) {
            if (index.getName().equals(name)) {
                return index;
            }
        }
        return null;
    }

    @Override
    public QueryInterface requestQueryPlugin(String name) {
        Iterable<QueryInterface> queriers = pluginController.getQueryPlugins(true);
        for (QueryInterface querier : queriers) {
            if (querier.getName().equals(name)) {
                return querier;
            }
        }

        return null;
    }

    @Override
    public Iterable<IndexerInterface> getAllIndexPlugins() {
        return pluginController.getIndexingPlugins(false);
    }

    @Override
    public Iterable<QueryInterface> getAllQueryPlugins() {
        return pluginController.getQueryPlugins(false);
    }

    @Override
    public StorageInterface getStoragePluginForSchema(String storage) {
        return pluginController.getStorageForSchema(storage);
    }

    @Override
    public StorageInterface getStorageForSchema(URI location) {
        return pluginController.getStorageForSchema(location);
    }

    @Override
    public Iterable<StorageInputStream> resolveURI(URI location) {
        return pluginController.resolveURI(location);
    }

    @Override
    public Iterable<StorageInterface> getStoragePlugins(boolean onlyEnabled) {
        return pluginController.getStoragePlugins(onlyEnabled);
    }

    @Override
    public StorageInterface getStorageForSchema(String schema) {
        return pluginController.getStorageForSchema(schema);
    }

    @Override
    public Iterable<QueryInterface> getQueryPlugins(boolean onlyEnabled) {
        return pluginController.getQueryPlugins(onlyEnabled);
    }

    @Override
    public Iterable<String> getQueryProvidersName(boolean enabled) {
        return pluginController.queryNames(enabled);
    }

    @Override
    public QueryInterface getQueryProviderByName(String name, boolean onlyEnabled) {
        return pluginController.getQueryProviderByName(name, onlyEnabled);
    }

    @Override
    public Task<Report> queryDispatch(Iterable<String> querySources, String query, Object ... parameters){
        return pluginController.queryDispatch(querySources, query, parameters);
    }
    @Override
    public Task<Report> queryDispatch(String querySource, String query, Object ... parameters){
        return pluginController.queryDispatch(querySource, query, parameters);
    }
    @Override
    public Task<Report> queryClosure(Iterable<String> querySources, String query, Object ... parameters){
        return pluginController.queryClosure(querySources, query, parameters);
    }
    @Override
    public Task<Report> queryClosure(String querySource, String query, Object ... parameters){
        return pluginController.queryClosure(querySource, query, parameters);
    }
    
    @Override
    public Task<Report> indexDispatch(String pluginName, URI path) {
        return pluginController.indexDispatch(pluginName, path);
    }
    
    @Override
    public Task<Report> indexClosure(String pluginName, URI path){
        return pluginController.indexClosure(pluginName, path);
    }

    @Override
    public Task<Report> indexAllDispatch(URI path) {
        return pluginController.indexAllDispatch(path);
    }

    @Override
    public Task<Report> indexAllClosure(URI path) {
        return pluginController.indexAllClosure(path);
    }    
}
