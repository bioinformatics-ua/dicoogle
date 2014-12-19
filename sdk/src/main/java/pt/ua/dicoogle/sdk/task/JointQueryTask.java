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
import java.util.List;
import java.util.concurrent.ExecutionException;

import pt.ua.dicoogle.sdk.datastructs.SearchResult;

/**
 * Advanced task which encompases multiple sub-tasks.
 * 
 * This class helps dicoogle to deal with multiple providers simultaneously.
 * 
 * @author Tiago Marques Godinho, tmgodinho@ua.pt
 *
 */
public abstract class JointQueryTask {

	private boolean cancelled;
	private int numberOfCompletedTasks;
	
	private List<Task<Iterable<SearchResult>>> searchTasks;
	
	public JointQueryTask() {
		this.searchTasks = new ArrayList<>();
		this.numberOfCompletedTasks = 0;
		this.cancelled = false;
	}

	public boolean addTask(final Task<Iterable<SearchResult>> e) {
		//Add hook
		e.onCompletion( new Runnable() {
			@Override
			public void run() {
				numberOfCompletedTasks++;
				onReceive(e);
				if(numberOfCompletedTasks == searchTasks.size())
					onCompletion();
			}
		} );
		
		return searchTasks.add(e);
	}
	
	public abstract void onCompletion();

	public abstract void onReceive(Task<Iterable<SearchResult>> e);
	
	public Iterable<SearchResult> get() throws InterruptedException, ExecutionException{
		List<SearchResult> list = new ArrayList<>();
		
		for(Task<Iterable<SearchResult>> task : searchTasks){
			Iterable<SearchResult> res = task.get();
			for(SearchResult i : res)
				list.add(i);
		}
		return list;
	}	

	public float getProgress() {
		if(isCancelled())
			return -1;
		
		if(isDone())
			return 1;
		
		return numberOfCompletedTasks / searchTasks.size();
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isDone() {
		return numberOfCompletedTasks == searchTasks.size();
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		boolean ret = true;
		for(Task<Iterable<SearchResult>> t : searchTasks){
			if(!t.isCancelled())
				ret = t.cancel(mayInterruptIfRunning) && ret;
		}
		return ret;
	}	
}
