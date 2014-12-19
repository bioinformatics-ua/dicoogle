/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle-sdk-ext.
 *
 * Dicoogle/dicoogle-sdk-ext is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle-sdk-ext is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.sdk;

import java.util.ArrayList;
import java.util.Collection;
import net.xeoh.plugins.base.Plugin;
import pt.ua.dicoogle.sdk.Utils.TaskQueue;
import pt.ua.dicoogle.sdk.Utils.TaskRequest;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.observables.FileObservable;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;

/**
 *
 * @author Carlos Ferreira
 */
public interface GenericPluginInterface extends Plugin
{
    public void attendTask(TaskRequest task);
    
    //public ListObservable<TaskRequest> getTaskRequestsList();

    //public ListObservable<SearchResult> getResults();

    public String getName();

    public void initialize(TaskQueue tasks);

    public void Stop();

    /**
     * Although these methods return an observable, it is mandatory that the local indexes return the array with the results...
     * @param query
     * @param extrafields
     * @return
     */
    
    public ListObservableSearch<SearchResult> search(String query, Collection<String> extrafields);
    public ListObservableSearch<SearchResult> searchOne(String query, Collection<String> Extrafields, String address);
    public FileObservable requestFile(String address, String name, String hash);

    public boolean isLocalPlugin();

    public void setDefaultSettings();

    public void setSettings(ArrayList<Object> settings);

    public ArrayList<Object> getPanelInitilizationParams();

    //public PluginPanel getConfigPanel();

    //public void setSettings(PluginPanel settings);

    public boolean isRunning();
}
