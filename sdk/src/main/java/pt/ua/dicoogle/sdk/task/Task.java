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
import java.util.logging.Logger;
import pt.ua.dicoogle.sdk.datastructs.Report;

/**
 *
 * Type is the return type of the FutureTask
 * 
 * @author psytek
 */
public class Task<Type> extends FutureTask<Type> {

    private static final Logger logger = Logger.getLogger("dicoogle");
    
    private String taskName = "unnamed task";
    private Callable callable;
    ArrayList<Runnable> toRunWhenComplete;
    
    public double elapsedTime = -1;
    private long tStart = 0;
    
    public Task(Callable<Type> c){
        super(c);
        callable = c;
        toRunWhenComplete = new ArrayList<>();//we could lazy initialize this in onCompletion
    }
    
    public Task(String name, Callable<Type> c){
        super(c);
        taskName = name;
        toRunWhenComplete = new ArrayList<>();
    }
    
    /*creates a task that throws an exception. for error reporting, i dislike nulls*/
    public static Task<Report> error(final String error) {
        return new Task("error", new Callable<Report>() 
            {
                @Override
                public Report call() throws Exception {
                    throw new Exception(error);
                }
            }
        );
    }

    @Override
    public void run(){
        //sets the timer
        tStart = System.currentTimeMillis();
        super.run();
    }
    
    
    @Override
    protected void set(Type ret){
        super.set(ret);
        for(Runnable r : toRunWhenComplete){
            r.run();
        }
        elapsedTime = (System.currentTimeMillis() - tStart)/1000.0;
        logger.info("task:"+getName()+" completed in: "+elapsedTime());
    }
    
    public void onCompletion(Runnable r){
        toRunWhenComplete.add(r);
    }
    
    public double elapsedTime(){
        return elapsedTime;
    }
    
    
    
    /**
     * a task name, for presentation purposes
     * @return the name given to the task
     */
    public String getName(){return taskName;}
    public void setName(String name){name = taskName;}
 
    /**
     * returns the task progress, goes from 0 to 1
     * however, if we have an unbounded task a -1 is returned
     * @return the task progress if defined in a subclass or -1 if undetermined
     */
    public float getProgress(){
        if (callable instanceof ProgressCallable){
            return ((ProgressCallable)this.callable).getProgress();
        }
        return -1;
    };
    
}

