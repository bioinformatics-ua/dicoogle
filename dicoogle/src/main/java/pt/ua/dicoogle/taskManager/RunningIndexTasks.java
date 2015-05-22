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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.datastructs.IndexReport;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * Singleton that contains all running index tasks
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class RunningIndexTasks {
    private static final Logger logger = LoggerFactory.getLogger(RunningIndexTasks.class);
    
	public static RunningIndexTasks instance;

	private final Map<String, Task<Report>> taskRunningList;

	public static RunningIndexTasks getInstance() {
		if (instance == null)
			instance = new RunningIndexTasks();

		return instance;
	}

	public RunningIndexTasks() {
		taskRunningList = new HashMap<>();
	}

	public void addTask(String taskUid, Task<Report> task) {
		taskRunningList.put(taskUid, task);
	}

	public boolean removeTask(String taskUid) {
		return taskRunningList.remove(taskUid) != null;
	}

	public boolean stopTask(String taskUid) {
		Task<Report> task = taskRunningList.get(taskUid);
		if (task != null)
		{
			boolean canceled = task.cancel(true);
			if(canceled)
			{
				//removeTask(taskUid);
				return true;
			}
		}
        else {
            logger.warn("Attempt to stop unexistent task {}, ignoring", taskUid);
		}

		return false;
	}

	public Map<String, Task<Report>> getRunningTasks() {

		return taskRunningList;
	}

	public String toJson() {
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();

		Iterator<Map.Entry<String, Task<Report>>> it = taskRunningList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Task<Report>> pair = it.next();
			Task<Report> task = pair.getValue();
            JSONObject entry = new JSONObject();
			entry.put("taskUid", pair.getKey());
			entry.put("taskName", task.getName());
			entry.put("taskProgress", task.getProgress());
            
            if (task.isDone()) {
                entry.put("complete", true);
                try {
                    Report r = task.get();
                    if (r instanceof IndexReport) {
                        entry.put("elapsedTime", ((IndexReport)r).getElapsedTime());
                        entry.put("nIndexed", ((IndexReport)r).getNIndexed());
                        entry.put("nErrors", ((IndexReport)r).getNErrors());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    logger.warn("Could not retrieve task result, ignoring", ex);
                }
            }
            
			array.add(entry);
		}

        object.put("results", array);
		object.put("count", array.size());
		
		return object.toString();

	}
}
