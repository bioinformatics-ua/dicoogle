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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.google.common.base.CharMatcher;
import java.util.List;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.utils.DictionaryAccess;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * Provides several helper functions for retrieving information about a DICOM file.
 *
 * @author António Novo <antonio.novo@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class Information
{
	/**
	 * Based on a SOP Instance UID, returns a File handler for the respective .dcm file. This method
     * issues all available query providers, see {@link Information#getFileFromSOPInstanceUID(java.lang.String, java.util.List)}
     * to select specific providers
	 *
	 * @param sopInstanceUID a String containing a valid/indexed SOP Instance UID.
	 * @return a File handler for the respective .dcm file if the SOP Instance UID is valid and indexed, null otherwise.
	 */
	public static StorageInputStream getFileFromSOPInstanceUID(String sopInstanceUID)
	{
        return getFileFromSOPInstanceUID(sopInstanceUID, null);
	}

    /**
	 * Based on a SOP Instance UID, returns a Dicoogle storage file handle for the respective resource file.
	 *
	 * @param sopInstanceUID a String containing a valid/indexed SOP Instance UID.
     * @param providers a list of query sources to issue the file handler (if null, all enabled providers are queried)
	 * @return a File handler for the respective .dcm file if the SOP Instance UID is valid and indexed, null otherwise.
	 */
	public static StorageInputStream getFileFromSOPInstanceUID(String sopInstanceUID, List<String> providers)
	{
        System.err.printf("getFileFromSOPInstanceUID(%s, %s)\n", sopInstanceUID, providers);
		if (sopInstanceUID == null)
			return null;
        
        if (providers == null) {
            providers = PluginController.getInstance().getQueryProvidersName(true);
        }

		String query = "SOPInstanceUID:" + sopInstanceUID;

		CountDownLatch latch = new CountDownLatch(1);	
		MyHolder holder= new MyHolder(latch);
		PluginController.getInstance().query(holder, providers, query, new HashMap<String, String>());

		try {
			latch.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return holder.getRet();
	}
	
	private static class MyHolder extends JointQueryTask{
		private StorageInputStream ret = null;
		private CountDownLatch latch;		
		
		@Override
		public void onReceive(Task<Iterable<SearchResult>> e) {
			try {		
				URI uri= null;
				 Iterable<SearchResult> itResults = e.get();
				 
				 for(SearchResult r : itResults){
			        	if(uri == null)
			        		uri = r.getURI();
			            System.out.println("URI: "+uri.toString());
			        }
			        
			        if(uri != null){
			        	StorageInterface str = PluginController.getInstance().getStorageForSchema(uri);
			            if(str != null){
			            	Iterable<StorageInputStream> stream = str.at(uri);
			            	for( StorageInputStream r : stream){
			            		ret = r;
			            		
			            		stopAllTaks();
			            		
			            		latch.countDown();
			            		
				            	return;
				            }
			            }
			        }
			} catch (InterruptedException | ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		public MyHolder(CountDownLatch latch) {
			super();
			this.latch = latch;
		}

		private void stopAllTaks() {
			this.cancel(true);
		}

		@Override
		public void onCompletion() {
			latch.countDown();
		}

		public StorageInputStream getRet() {
			return ret;
		}
	}
	/**
	 * Based on a SOP Instance UID returns a hash table containing all name and value tag pairs for the respective .dcm file.
     * This method issues all available query providers, see {@link Information#getFileFromSOPInstanceUID(java.lang.String, java.util.List)}
     * to select specific providers.
	 *
	 * @param sopInstanceUID a String containing a valid/indexed SOP Instance UID.
	 * @return a Hashtables containing all name and value tag pairs for the respective .dcm file if the SOP Instance UID is valid and indexed, null otherwise.
	 */
	public static HashMap<String, Object> searchForFileIndexedMetaData(String sopInstanceUID)
	{
        return searchForFileIndexedMetaData(sopInstanceUID, null);
    }

	/**
	 * Based on a SOP Instance UID returns a hash table containing all name and value tag pairs for the respective .dcm file.
	 *
	 * @param sopInstanceUID a String containing a valid/indexed SOP Instance UID.
     * @param providers a list of query sources to issue the tables
	 * @return a Hashtables containing all name and value tag pairs for the respective .dcm file if the SOP Instance UID is valid and indexed, null otherwise.
	 */
	public static HashMap<String, Object> searchForFileIndexedMetaData(String sopInstanceUID, List<String> providers) // XXX SOP Instance UID should always be set within a DICOM file, I think...
	{
		if (sopInstanceUID == null)
			return null;
        
        if (providers == null) {
            providers = PluginController.getInstance().getQueryProvidersName(true);
        }

		// addMoveDestination all those tags to the extra fields that will be retried on a search query
		HashMap<String, String> extraFields = new HashMap<>();
		// get all the tags that can possibly be used within the file
		HashMap<String, Integer> allTags = DictionaryAccess.getInstance().getTagList();

		for(String key : allTags.keySet()){
			extraFields.put(key, null);
		}
		/*
		HashMap<Integer, TagValue> mf = TagsStruct.getSettings().getManualFields();
		for (Integer i : mf.keySet())
		{
			extraFields.put(mf.get(i).getAlias(), null);
		}
		HashMap<Integer, TagValue> df = TagsStruct.getSettings().getDimFields();
		for (Integer i : df.keySet())
		{
			extraFields.put(df.get(i).getAlias(), null);
		}*/

		// build the query setring for the search
		String query = "SOPInstanceUID:" + sopInstanceUID;

		//execute search
        Iterable<SearchResult> itResults = null;
		try {
			
			JointQueryTask holder = new JointQueryTask() {
				
				@Override
				public void onReceive(Task<Iterable<SearchResult>> e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onCompletion() {
					// TODO Auto-generated method stub
					
				}
			};
			itResults = PluginController.getInstance().query(holder, providers, query, extraFields).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(itResults == null)
			return null;
		
		HashMap<String, Object> ret = new HashMap<>();
		// return the first result (which should be the only one, by the way) extra fields (which contains all the tags and their values for this file)
        for(SearchResult r : itResults){
            ret.putAll(r.getExtraData());
        }
        return ret;//no results
    }

	private static final char start = 0;
	private static final char end = 31;

   	/**
	 * Based on a SOP Instance UID returns a String containing a XML document filled with all name and value tag pairs for the respective .dcm file.
	 * This method will query all enabled sources.
     * 
	 * @param sopInstanceUID a String containing a valid/indexed SOP Instance UID.
	 * @return a String containing a XML document filled with all name and value tag pairs for the respective .dcm file if the SOP Instance UID is valid and indexed, null otherwise.
	 */
	public static String getXMLTagListFromFile(String sopInstanceUID) {
        return getXMLTagListFromFile(sopInstanceUID, null);
    }

    /**
	 * Based on a SOP Instance UID returns a String containing a XML document filled with all name and value tag pairs for the respective .dcm file.
	 *
	 * @param sopInstanceUID a String containing a valid/indexed SOP Instance UID.
     * @param providers a list of query sources to issue the document
	 * @return a String containing a XML document filled with all name and value tag pairs for the respective .dcm file if the SOP Instance UID is valid and indexed, null otherwise.
	 */
	public static String getXMLTagListFromFile(String sopInstanceUID, List<String> providers)
	{
		// get all the tags and their values present on the file
		HashMap<String, Object> tags = searchForFileIndexedMetaData(sopInstanceUID, providers);
		if (tags == null)
		{
			return null;
		}

		// create the XML string builder and open the xml document
		StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		xml.append("<tags>");
		
		Element rootElem = new Element("tags");
		
		// loop through all the tags set and addMoveDestination them and their values to the XML tree
		Iterator<String> it = tags.keySet().iterator();
		while (it.hasNext())
		{
			String key = it.next();
			String value = (String) tags.get(key);
			value = value.trim();
			
			value = CharMatcher.inRange(start, end).and(CharMatcher.noneOf("\t\r\n")).collapseFrom(value, ' ');
			
			Element tagElem = new Element("tag");
			tagElem.setAttribute("name", key);
			tagElem.setText(value);
			
			rootElem.addContent(tagElem);
		}
		
		XMLOutputter outStream = new XMLOutputter(Format.getCompactFormat());
		StringWriter wr = new StringWriter();
		try {
			outStream.output(new Document(rootElem), wr);
		} catch (IOException e) {
			return null;
		}
		
		return wr.toString();
	}

	public static float getFrameRateFromImage(String sopUID){
		HashMap<String, Object> tags = searchForFileIndexedMetaData(sopUID);
		if(tags == null)
			return 0;

		for(String key : tags.keySet()){
			System.out.println("Key: "+key+" Value: "+tags.get(key));
		}
		
		if(tags.containsKey("RecommendedDisplayFrameRateInFloat"))
			return Float.parseFloat("RecommendedDisplayFrameRateInFloat");
		
		if(tags.containsKey("RecommendedDisplayFrameRate"))
			return Float.parseFloat("RecommendedDisplayFrameRate");
		
		return 0;
	}
	
	public static int getNumberOfFramesInFile(String sopInstanceUID){
		StorageInputStream dcmFile = Information.getFileFromSOPInstanceUID(sopInstanceUID);
	
		if(dcmFile == null)
			return -1;
		
		return Convert2PNG.getNumberOfFrames(dcmFile);
	}
}
