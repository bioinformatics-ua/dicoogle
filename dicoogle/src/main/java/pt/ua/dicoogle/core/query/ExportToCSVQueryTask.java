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
package pt.ua.dicoogle.core.query;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

public class ExportToCSVQueryTask extends JointQueryTask {

	private static final Logger log = LoggerFactory.getLogger(ExportToCSVQueryTask.class);
	
	private static String[] searchChars = new String[]{"\n", ";"};
	private static String[] replaceChars = new String[]{"", ","};
	
	private List<String> tagsOrder;
	// private OutputStream outputStream;
	private PrintWriter writter;
	private CountDownLatch latch;
	
	private int nLines = 0;

	public ExportToCSVQueryTask(List<String> tagsOrder, OutputStream outputStream) {
		super();
		this.tagsOrder = tagsOrder;
		this.latch = new CountDownLatch(1);
		writter = new PrintWriter(outputStream);

		printFirstLine();
	}

	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub
		writter.flush();
		writter.close();
		latch.countDown();
		
		log.info("Exported CSV Table: ", tagsOrder.toString(), nLines);
	}

	@Override
	public void onReceive(Task<Iterable<SearchResult>> e) {
		// TODO Auto-generated method stub

		try {
			Iterable<SearchResult> it = e.get();
			for (SearchResult result : it) {
				printLine(result);
			}
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

	}

	/**
	 * Print the first line of the .csv file
	 * 
	 */
	private void printFirstLine() {
		StringBuilder builder = new StringBuilder();
		
		log.debug("Started, Printing first line: ", tagsOrder);
		
		for (String tag : tagsOrder) {
			builder.append("\"").append(tag).append("\";");
		}

		this.writter.println(builder.toString());
	}

	private void printLine(SearchResult result){
		StringBuilder builder = new StringBuilder();
		
		HashMap<String, Object> extraFields = result.getExtraData();
		
		for (String tag : tagsOrder) {
			Object temp1 = extraFields.get(tag);
			String temp1String = temp1.toString();
			
			if(NumberUtils.isNumber(temp1String)){
				temp1String = NumberUtils.createBigDecimal(temp1String).toPlainString();	
			}
			
			String s = (temp1 != null) ? StringUtils.trimToEmpty(temp1String) : "";
			
			if (s.length() > 0) {
				String temp = StringUtils.replaceEach(s, searchChars, replaceChars);
				builder.append('\"').append(temp).append("\";");
			} else {
				builder.append(";");
			}
		}
		
		log.debug("Printing Line: ", builder.toString());
		nLines++;
		this.writter.println(builder.toString());
	}

	public void await() {
		// TODO Auto-generated method stub
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
