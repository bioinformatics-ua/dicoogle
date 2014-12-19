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
package pt.ua.dicoogle.sdk.datastructs;

public class DocumentIndexReport extends IndexReport {

	private Measurable retrieveObjectTime;
	private Measurable assembleDocumentTIme;
	private Measurable storeInDatabaseTime;
	private final Measurable totalTime;
	private boolean successfull;
	private final String id;
			
	public DocumentIndexReport(String id) {
		super();
		this.id = id;
		this.totalTime = new Measurable();
		this.successfull = true;
	}
	
	private static void lazyInit(Measurable obj){
		if(obj == null)
			obj = new Measurable();
	}	
	
	public Measurable getRetrieveObjectTime() {
		lazyInit(retrieveObjectTime);
		return retrieveObjectTime;
	}

	public Measurable getAssembleDocumentTIme() {
		lazyInit(assembleDocumentTIme);
		return assembleDocumentTIme;
	}

	public Measurable getStoreInDatabaseTime() {
		lazyInit(storeInDatabaseTime);
		return storeInDatabaseTime;
	}
	
	public boolean isSuccessfull() {
		return successfull;
	}

	public void setSuccessfull(boolean successfull) {
		this.successfull = successfull;
	}

	public String getId() {
		return id;
	}
	
	public void start() {
		totalTime.start();
	}

	public void stop() {
		totalTime.stop();
	}

	@Override
	public long getElapsedTime() {
		return totalTime.getTime();
	}

	@Override
	public int getNErrors() {
		if(isSuccessfull())
			return 0;
		return 1;
	}

	@Override
	public int getNIndexed() {		
		if(isSuccessfull())
			return 1;
		return 0;
	}

}
