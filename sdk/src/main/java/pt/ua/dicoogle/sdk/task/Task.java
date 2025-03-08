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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;

/** An entity for describing an asynchronous task in Dicoogle.
 *
 * @param <Type> the return type of the FutureTask
 * 
 * @author psytek
 */
public class Task<Type> extends FutureTask<Type> {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Task.class);

    private final String uid;
    private String taskName;
    private Callable<Type> callable;
    private ArrayList<Runnable> toRunWhenComplete;
    private LocalDateTime timeCreated;

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
        toRunWhenComplete = new ArrayList<>(2);
        this.timeCreated = LocalDateTime.now();
    }

    /** When the task is done, run the runnables registered
     * then clean up references to reduce footprint.
     */
    @Override
    protected void done() {
        for (Runnable r : this.toRunWhenComplete) {
            try {
                r.run();
            } catch (Exception ex) {
                LOG.warn("Error running task completion hook", ex);
            }
        }
        this.toRunWhenComplete.clear();
        this.callable = null;
    }

    public String getUid() {
        return uid;
    }

    /** Add a completion hook to this task. */
    public void onCompletion(Runnable r) {
        toRunWhenComplete.add(r);
    }

    /** Gets the task's name
     * @return a task name, for presentation purposes
     */
    public String getName() {
        return this.taskName;
    }

    /** Sets the task's name
     * @param name the new task's name, for presentation purposes
     */
    public void setName(String name) {
        this.taskName = name;
    }

    /** Gets the task's progress
     * @return the task's progress from 0 to 1, or -1 if the task is unbounded
     */
    public float getProgress() {
        if (callable instanceof ProgressCallable) {
            return ((ProgressCallable<?>) this.callable).getProgress();
        }
        return -1;
    }

    /**
     * Gets the time this task was created.
     * @return Time the task was created.
     */
    public LocalDateTime getTimeCreated() {
        return this.timeCreated;
    }

    private static String generateUID() {
        return UUID.randomUUID().toString();
    }
}

