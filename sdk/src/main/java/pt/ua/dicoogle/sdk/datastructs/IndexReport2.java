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

public class IndexReport2 extends IndexReport {

	private int nIndexedFiles;
	private int nErrors;
	private long elapsedTime;
	
	public IndexReport2(int nIndexedFiles, int nErrors, long elapsedTime) {
		super();
		this.nIndexedFiles = nIndexedFiles;
		this.nErrors = nErrors;
		this.elapsedTime = elapsedTime;
	}
	
	public IndexReport2(int nIndexedFiles, int nErrors) {
		this(nIndexedFiles, nErrors, 0);
	}
	
	public IndexReport2(int nIndexedFiles) {
		this(nIndexedFiles, 0, 0);
	}
	
	public IndexReport2() {
		this(0,0,0);
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	@Override
	public String toString() {
		return "IndexReport [nIndexedFiles=" + nIndexedFiles + ", nErrors="
				+ nErrors + ", elapsedTime=" + elapsedTime + "]";
	}

	public void setnIndexedFiles(int nIndexedFiles) {
		this.nIndexedFiles = nIndexedFiles;
	}

	public void setnErrors(int nErrors) {
		this.nErrors = nErrors;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public void addIndexFile(){
		this.nIndexedFiles++;
	}
	
	public void addError(){
		this.nErrors++;
	}
	
	public void started(){
		this.elapsedTime = System.currentTimeMillis();
	}
	
	public void finished(){
		this.elapsedTime = System.currentTimeMillis() - elapsedTime;
	}

	@Override
	public int getNErrors() {
		return nErrors;
	}

	@Override
	public int getNIndexed() {
		return nIndexedFiles;
	}
	

}
