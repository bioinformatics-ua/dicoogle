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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.datastructs.IndexReport;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Singleton that contains all running index tasks
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class RunningIndexTasks {
    private static final Logger logger = LoggerFactory.getLogger(RunningIndexTasks.class);
    private static int SOFT_MAX_RUNNINGTASKS =
            Integer.parseInt(System.getProperty("dicoogle.tasks.softRemoveTasks", "50000"));
    private static int NUMBER_RUNNINGTASKS_TO_CLEAN =
            Integer.parseInt(System.getProperty("dicoogle.tasks.numberTaskClean", "2000"));
    private static boolean ENABLE_HOOK = Boolean.valueOf(System.getProperty("dicoogle.tasks.removedCompleted", "true"));
    public static RunningIndexTasks instance;

    private final Map<String, Task<Report>> taskRunningList;
    private AtomicBoolean cleaning = new AtomicBoolean(false); // if cleaning task is running or not.

    public static RunningIndexTasks getInstance() {
        if (instance == null)
            instance = new RunningIndexTasks();
        return instance;
    }

    public RunningIndexTasks() {
        taskRunningList = new ConcurrentHashMap<>(SOFT_MAX_RUNNINGTASKS, 0.75f, 4);
    }

    public void addTask(String taskUid, Task<Report> task) {
        taskRunningList.put(taskUid, task);
        if (ENABLE_HOOK && !cleaning.compareAndSet(false, true)){
            // will execute cleaning process
            hookRemoveRunningTasks();
            cleaning.set(false); // already cleaned
        }
    }

    public void addTask(Task<Report> task) {
        taskRunningList.put(task.getUid(), task);
    }

    public boolean removeTask(String taskUid) {
        return taskRunningList.remove(taskUid) != null;
    }

    public void hookRemoveRunningTasks() {

        if (this.taskRunningList.size() > SOFT_MAX_RUNNINGTASKS) {
            int removedTasks = 0;
            Iterator<String> iterator = this.taskRunningList.keySet().iterator();
            while (iterator.hasNext() && removedTasks < NUMBER_RUNNINGTASKS_TO_CLEAN) {
                String tId = iterator.next();
                Task<?> t = this.taskRunningList.get(tId);
                if (t != null && (t.isCancelled() || t.isDone())){
                    iterator.remove();
                    removedTasks++;
                }
            }
        }
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
        return Collections.unmodifiableMap(taskRunningList);
    }

    public String toJson() {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        int countComplete = 0;
        int countCancelled = 0;
        for (Task<Report> task : taskRunningList.values()) {
            JSONObject entry = asJSON(task);
            if (task.isDone() && !task.isCancelled()) {
                countComplete += 1;
            } else if (task.isCancelled()) {
                countCancelled += 1;
            }
            array.add(entry);
        }

        object.put("results", array);
        object.put("count", array.size() - countComplete - countCancelled);
        return object.toString();
    }

    public JSONObject asJSON(Task<? extends Report> task) {
        JSONObject entry = new JSONObject();
        entry.put("taskUid", task.getUid());
        entry.put("taskName", task.getName());
        entry.put("taskProgress", task.getProgress());

        if (task.isDone() && !task.isCancelled()) {
            entry.put("complete", true);
            try {
                Report r = task.get();
                if (r instanceof IndexReport) {
                    entry.put("elapsedTime", ((IndexReport) r).getElapsedTime());
                    entry.put("nIndexed", ((IndexReport) r).getNIndexed());
                    entry.put("nErrors", ((IndexReport) r).getNErrors());
                }
            } catch (InterruptedException | ExecutionException ex) {
                logger.warn("Could not retrieve task result, ignoring", ex);
            }
        }
        if (task.isCancelled()) {
            entry.put("canceled", true);
        }
        return entry;
    }
}
