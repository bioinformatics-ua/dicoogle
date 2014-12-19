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
package pt.ua.dicoogle.server.web;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

public class FirstResponserQueryHolder extends JointQueryTask {
	
	private Iterable<SearchResult> result;

	@Override
	public void onReceive(Task<Iterable<SearchResult>> e) {
		try {
			synchronized (this) {
				if(result == null){
					result = e.get();
					stopAllTaks();
				}
			}

			notifyAll();
			
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public FirstResponserQueryHolder(CountDownLatch latch) {
		super();
	}

	private void stopAllTaks() {
		this.cancel(true);
	}

	@Override
	public void onCompletion() {
		// TODO: MAY BE BUGGED
		notifyAll();
	}
	
	public Iterable<SearchResult> getResult() {
		synchronized (this) {
			while( !(this.isCancelled() || this.isDone())){
				if(result != null)
					return result;
				
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}


	
	

	
}