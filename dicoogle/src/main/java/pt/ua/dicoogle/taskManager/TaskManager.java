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

package pt.ua.dicoogle.taskManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * 
 * This is a task manager for Dicoogle.
 * Currently it is based on a fixed thread pool executor with a maximum of 4 threads executing simultaneously. 
 * 
 * TODO: change interface to use generics.
 * 
 * @author psytek
 */
public class TaskManager {
    ExecutorService taskExecutor;
    int nConcurrentThreads = 3;

    public TaskManager() {
        taskExecutor = Executors.newFixedThreadPool(nConcurrentThreads);
    }

    public TaskManager(int nConcurrentThreads) {
        this.nConcurrentThreads = nConcurrentThreads;
        taskExecutor = Executors.newFixedThreadPool(nConcurrentThreads);
    }

    /**asynch execution of a task*/
    public void dispatch(Task<?> t) {
        taskExecutor.submit(t);
    }



}
