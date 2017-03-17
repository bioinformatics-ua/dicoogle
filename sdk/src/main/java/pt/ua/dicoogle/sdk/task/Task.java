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
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/** An entity for describing an asynchronous task in Dicoogle.
 *
 * @param <Type> the return type of the FutureTask
 * 
 * @author psytek
 */
public class Task<Type> extends FutureTask<Type> {

    private final String uid;
    private String taskName;
    private Callable callable;
    private ArrayList<Runnable> toRunWhenComplete;

    /** Create a new task with a randomly generated ID. */
    public Task(Callable<Type> c) {
        this("unnamed task", c);
    }

    /** Create a new task with a specific name and a randomly generated ID. */
    public Task(String name, Callable<Type> c) {
        this(generateUID(), name, c);
    }

    public Task(String uid, String name, Callable<Type> c) {
        super(c);
        this.callable = c;
        this.uid = uid;
        taskName = name;
        toRunWhenComplete = new ArrayList<>();
    }
    
    @Override
    protected void set(Type ret){
        super.set(ret);
        for(Runnable r : toRunWhenComplete){
            r.run();
        }
    }

    public String getUid() {
        return uid;
    }

    public void onCompletion(Runnable r){
        toRunWhenComplete.add(r);
    }
    
    /** Gets the task's name
     * @return a task name, for presentation purposes
     */
    public String getName(){return this.taskName;}
    
    /** Sets the task's name
     * @param name the new task's name, for presentation purposes
     */
    public void setName(String name){this.taskName = name;}
    
    /** Gets the task's progress
     * @return the task's progress from 0 to 1, or -1 if the task is unbounded
     */
    public float getProgress(){
        if (callable instanceof ProgressCallable){
            return ((ProgressCallable)this.callable).getProgress();
        }
        return -1;
    }

    private static String generateUID() {
        return UUID.randomUUID().toString();
    }
}

