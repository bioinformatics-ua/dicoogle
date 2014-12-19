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

import java.util.ArrayList;
import java.util.List;

public class TaskIndexReport extends IndexReport {

	private final List<DocumentIndexReport> successfulReports;
	private final List<DocumentIndexReport> errorReports;
	private boolean successful;
	private final Measurable elapsedTime;
	
	public TaskIndexReport() {
		// TODO Auto-generated constructor stub
		this.successfulReports = new ArrayList<>();
		this.errorReports = new ArrayList<>();
		this.elapsedTime = new Measurable();
		this.successful = true;
	}
	
	public void addReport(DocumentIndexReport report){
		if(report.isSuccessfull())
			successfulReports.add(report);
		else
			errorReports.add(report);
	}

	public void start() {
		elapsedTime.start();
	}

	public void stop() {
		elapsedTime.stop();
	}
	
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	/* (non-Javadoc)
	 * @see pt.ua.dicoogle.sdk.datastructs.IndexReport#getElapsedTime()
	 */
	@Override
	public long getElapsedTime() {
		return elapsedTime.getTime();
	}
	
	/* (non-Javadoc)
	 * @see pt.ua.dicoogle.sdk.datastructs.IndexReport#getNErrors()
	 */
	@Override
	public int getNErrors(){
		if(!successful)
			return -1;
		return this.errorReports.size();
	}

	/* (non-Javadoc)
	 * @see pt.ua.dicoogle.sdk.datastructs.IndexReport#getNIndexed()
	 */
	@Override
	public int getNIndexed(){
		if(!successful)
			return -1;
		return this.successfulReports.size();
	}
}
