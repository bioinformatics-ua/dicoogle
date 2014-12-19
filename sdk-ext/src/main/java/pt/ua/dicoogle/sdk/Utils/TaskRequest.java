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
package pt.ua.dicoogle.sdk.Utils;

import java.util.Map;
import java.util.Observable;

/**
 *
 * @author Carlos Ferreira
 */
public class TaskRequest extends Observable
{
    private int Task;
    private String RequesterPlugin;
    private Map<Integer, Object> Parameters;
    private Map<Integer, Object> results;
    private boolean completed;

    public TaskRequest(int Task, String RequesterPlugin, Map<Integer, Object> Parameters)
    {
        this.Task = Task;
        this.RequesterPlugin = RequesterPlugin;
        this.Parameters = Parameters;
        this.completed = false;
        this.results = null;
    }

    public Map<Integer, Object> getParameters()
    {
        return Parameters;
    }

    public String getRequesterPlugin()
    {
        return RequesterPlugin;
    }

    public int getTask()
    {
        return Task;
    }

    public Map<Integer, Object> getResults()
    {
        return results;
    }

    
    public void completeTask()
    {
        this.completed = true;
    }

    public boolean isCompleted()
    {
        return this.completed;
    }
    
    public void setResults(Map<Integer, Object> results)
    {
        this.results = results;
        
        //notification of the observers.
        this.setChanged();
        this.notifyObservers();
    }


}
