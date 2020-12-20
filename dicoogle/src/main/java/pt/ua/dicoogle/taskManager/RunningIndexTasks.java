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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
    private static Integer SOFT_MAX_RUNNINGTASKS = Integer.parseInt(System.getProperty("dicoogle.tasks.softRemoveTasks", "50000"));
    private static Integer NUMBER_RUNNINGTASKS_TO_CLEAN = Integer.parseInt(System.getProperty("dicoogle.tasks.numberTaskClean", "2000"));
    private static Boolean ENABLE_HOOK = Boolean.getBoolean(System.getProperty("dicoogle.tasks.removedCompleted", "true"));
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
		if (ENABLE_HOOK){
            hookRemoveRunningTasks();
        }

	}

	public boolean removeTask(String taskUid) {
		return taskRunningList.remove(taskUid) != null;
	}


	public boolean hookRemoveRunningTasks(){
	    if (this.taskRunningList.size()>SOFT_MAX_RUNNINGTASKS){
	        int removedTasks = 0 ;
	        for (String taskUid : this.taskRunningList.keySet()){
                Task t = this.taskRunningList.get(taskUid);
                if (removedTasks<NUMBER_RUNNINGTASKS_TO_CLEAN &&(t.isCancelled() || t.isDone())){
                    this.removeTask(taskUid);
                    removedTasks++;
                }
            }
        }
	    return true;
    }

	public boolean stopTask(String taskUid) {
		Task<Report> task = taskRunningList.get(taskUid);
		if (task != null) {
			return task.cancel(true);
		} else {
			logger.info("Attempt to stop unexistent task {}, ignoring", taskUid);
		}

		return false;
	}

	public Map<String, Task<Report>> getRunningTasks() {

		return taskRunningList;
	}

    public String toJson() {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        int countComplete = 0;
        int countCancelled = 0;
        for (Map.Entry<String, Task<Report>> pair : taskRunningList.entrySet()) {
            Task<Report> task = pair.getValue();
            JSONObject entry = new JSONObject();
            entry.put("taskUid", pair.getKey());
            entry.put("taskName", task.getName());
            entry.put("taskProgress", task.getProgress());

            if (task.isDone() && !task.isCancelled()) {
                entry.put("complete", true);
                countComplete += 1;
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
            if (task.isCancelled()) {
                countCancelled += 1;
                entry.put("canceled", true);
            }
            array.add(entry);
        }

        object.put("results", array);
        object.put("count", array.size() - countComplete - countCancelled);
        return object.toString();
    }
}
