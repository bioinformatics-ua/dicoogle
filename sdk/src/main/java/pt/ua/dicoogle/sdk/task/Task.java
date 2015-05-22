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
package pt.ua.dicoogle.sdk.task;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 *
 * Type is the return type of the FutureTask
 * 
 * @author psytek
 */
public class Task<Type> extends FutureTask<Type> {

    private String taskName = "unnamed task";
    private Callable callable;
    ArrayList<Runnable> toRunWhenComplete;
    
    public Task(Callable<Type> c){
        super(c);
        callable = c;
        toRunWhenComplete = new ArrayList<>();//we could lazy initialize this in onCompletion
    }
    
    public Task(String name, Callable<Type> c){
        super(c);
        taskName = name;
        toRunWhenComplete = new ArrayList<>();//we could lazy initialize this in onCompletion
    }
    
    @Override
    protected void set(Type ret){
        super.set(ret);
        for(Runnable r : toRunWhenComplete){
            r.run();
        }
    }
    
    public void onCompletion(Runnable r){
        toRunWhenComplete.add(r);
    }
    
    /** Get the task's name
     * @return a task name, for presentation purposes
     */
    public String getName(){return this.taskName;}
    /** Set the task's name
     * @param name the new task's name, for presentation purposes
     */
    public void setName(String name){this.taskName = name;}
    
    /** Get the task's progress
     * @return the task's progress from 0 to 1, or -1 if the task is unbounded
     */
    public float getProgress(){
        if (callable instanceof ProgressCallable){
            return ((ProgressCallable)this.callable).getProgress();
        }
        return -1;
    };
    
}

