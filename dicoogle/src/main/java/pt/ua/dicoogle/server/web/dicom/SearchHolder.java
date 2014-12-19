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
package pt.ua.dicoogle.server.web.dicom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

public class SearchHolder {

	@SuppressWarnings("unused") //TODO: IMPLEMENT THIS
	private static int maxNumberOfTasks = 3;
	private Map<Integer, QueryHandler> tasks;
	
	public SearchHolder() {
		tasks = new HashMap<Integer, SearchHolder.QueryHandler>();
	}

	public synchronized int registerNewQuery(List<String> providers, String query, Object searchParam){
		QueryHandler task = new QueryHandler();
		
		task = (QueryHandler) PluginController.getInstance().query(task, providers, query, searchParam);
		if(task == null)
			return -1;
		
		tasks.put(task.hashCode(), task);
		return task.hashCode();
	}
	
	public synchronized void removeQuery(int id){
		tasks.remove(id);
	}
	
	public  synchronized Iterable<KeyValue> retrieveQueryOutput(int id){
		QueryHandler task = tasks.get(id);
	
		if(task == null)
			return null;
		
		return task;
	}
	
	private class QueryHandler extends JointQueryTask implements Iterable<KeyValue>{

		private StringBuffer buffer;	
		private List<KeyValue> tempResults;
		private List<InnerIterator> currentIterators;
		
		public QueryHandler() {
			super();
			buffer = new StringBuffer();
			currentIterators = new ArrayList<InnerIterator>();
			tempResults = new ArrayList<>();
		}

		@Override
		public void onCompletion() {
			// TODO Auto-generated method stub
			System.out.println("Completed Query");
		}

		@Override
		public void onReceive(Task<Iterable<SearchResult>> e) {
			// TODO Auto-generated method stub
			try {
				System.out.println("RECEIVED NEW ITERATION: ");
				
				Iterable<SearchResult> results = e.get();
				DefaultKeyValue keyValue = new DefaultKeyValue(e.getName(), results);
				
				appendResult(keyValue);
				
				/*for(SearchResult res : results ){
					
				}*/	
			} catch (InterruptedException | ExecutionException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			
			buffer.append("Received Query: "+e.getName());
		}

		@SuppressWarnings("unchecked")
		private synchronized void appendResult(DefaultKeyValue keyValue) {
			tempResults.add(keyValue);
			
			for(InnerIterator it : currentIterators){
				it.resultBuffer.add(keyValue);				
			}
		}
		
		private synchronized InnerIterator createIterator(){
			InnerIterator it = new InnerIterator(tempResults);
			currentIterators.add(it);
			
			return it; 
		}

		@Override
		public Iterator<KeyValue> iterator() {
			if(isDone() || isCancelled())
				return tempResults.iterator();
			
			return createIterator();
		}
		
		private class InnerIterator implements Iterator<KeyValue>{

			private Buffer resultBuffer;
			
			@SuppressWarnings("unchecked")
			public InnerIterator(List<KeyValue> tempResults) {
				int size = tempResults.size();
				if(size <= 0)
					size = 1;
				resultBuffer = BufferUtils.blockingBuffer( new UnboundedFifoBuffer(size) );
				resultBuffer.addAll(tempResults);
			}

			@Override
			public boolean hasNext() {
				if(!(isCancelled() || isDone()))
					return true;
				
				return !resultBuffer.isEmpty();
			}

			@Override
			public KeyValue next() {
				return (KeyValue) resultBuffer.remove();
			}

			@Override
			public void remove() {				
			}
			
		}
		
	}

	public boolean isDone(int id) {
		QueryHandler task = tasks.get(id);
		
		if(task == null)
			return true;
		
		return task.isDone() || task.isCancelled();
	}
		
}
