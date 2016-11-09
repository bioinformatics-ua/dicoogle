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
package pt.ua.dicoogle.server.web.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.core.dim.DIMGeneric;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.utils.DictionaryAccess;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 *
 * @author fmvalente
 */


//TODO:addMoveDestination type to file search
public class RestDimResource extends ServerResource{
    
	private static final Logger log = LoggerFactory.getLogger(RestDimResource.class);
	
    @Get
    public Representation represent(){
    	
        Form queryForm = getRequest().getResourceRef().getQueryAsForm();
    
        String search = queryForm.getValues("q");
        String advSearch = queryForm.getValues("advq");
        
        if(StringUtils.isNotEmpty(search)){
            QueryExpressionBuilder q = new QueryExpressionBuilder(search);
            search = q.getQueryString();
        }else if (StringUtils.isNotEmpty(advSearch)) {
			search = advSearch;
		}else{
			//SEND ERROR NO QUERY DEFINED
			setStatus(new Status(401), "No Query String Provided: Please provide a query either by ?advq, for advanced searches, or ?q for free text");
			return new EmptyRepresentation();
		}
        
        String type = queryForm.getValues("type");
        boolean dim = true;
        if(StringUtils.isNotEmpty(type)){
        	if(type.equalsIgnoreCase("DIM")){
        		dim = true;
        	}else if (type.equalsIgnoreCase("RAW")) {
				dim = false;
			}else{
				//SEND ERROR!! WRONG RETURN TYPE
				setStatus(new Status(402), "Wrong return type: Supported types, dim (default), raw.");
				
				return new EmptyRepresentation();
			}
        }
        
        List<String> knownProviders = new ArrayList<String>();
        String[] providers = queryForm.getValuesArray("provider");
        List<String> activeProviders = PluginController.getInstance().getQueryProvidersName(true);
        boolean queryAll = providers.length == 0;
    	for(String p : providers){
        	if(p.equalsIgnoreCase("all")){
        		queryAll = true;
    			break;
        	}else if(activeProviders.contains(p)){
        		knownProviders.add(p);
        	}
        }
        activeProviders = null;
                
        String[] extraFieldList = queryForm.getValuesArray("field");
        HashMap<String, String> extraFields = new HashMap<String, String>();
        //attaches the required extrafields
        if(extraFieldList.length == 0 || dim){
            extraFields.put("PatientName", "PatientName");
            extraFields.put("PatientID", "PatientID");
            extraFields.put("Modality", "Modality");
            extraFields.put("StudyDate", "StudyDate");
            extraFields.put("SeriesInstanceUID", "SeriesInstanceUID");
            extraFields.put("StudyID", "StudyID");
            extraFields.put("StudyInstanceUID", "StudyInstanceUID");
            extraFields.put("Thumbnail", "Thumbnail");
            extraFields.put("SOPInstanceUID", "SOPInstanceUID");
        }else{
            Map<String, String> availableTags = new HashMap<String, String>();
            for(String tag : DictionaryAccess.getInstance().getTagList().keySet()){
                availableTags.put(tag.toLowerCase(), tag);
            }            
            for(String f : extraFieldList){
                if(f.equalsIgnoreCase("all")){
                    for(String k : availableTags.values()){
                        extraFields.put(k, k);
                    }
                    break;
                }
                if(availableTags.containsKey(f)){
                    extraFields.put(availableTags.get(f), availableTags.get(f));
                }
            }
        }
                
        //System.err.println("ADVSEARCH: "+advSearch);
        //System.err.println("FINAL SEARCH QUERY: "+search);

        long startTime = System.currentTimeMillis();
        MyHolder holder = new MyHolder();
        if(queryAll)
        	PluginController.getInstance().queryAll(holder, search, extraFields);
        else
        	PluginController.getInstance().query(holder, knownProviders, search, extraFields);
        
    	ArrayList<SearchResult> resultsArr = new ArrayList<SearchResult>();
        
		try {
			Iterable<SearchResult> results = holder.get();
		
			for(SearchResult r : results)
	        	resultsArr.add(r);
	        
	        results = null;
	        
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startTime = System.currentTimeMillis() - startTime;
		
		String ret = "";
		MediaType mtype = null;
		if(dim){
			ret = processDIMResults(resultsArr);
			mtype = MediaType.APPLICATION_XML;			
		}else{
			ret = processJSON(resultsArr, startTime);
			mtype = MediaType.APPLICATION_JSON;
		}
		
		if(StringUtils.isEmpty(ret) && !resultsArr.isEmpty()){
			//RAISE ERROR
			setStatus(new Status(400), "Error parsing results data:");
			return new EmptyRepresentation();
		}
        
        //constructs our xml data struct
        return new StringRepresentation(ret, mtype);
    }
    
    private static String processDIMResults(Collection<SearchResult> results){
    	DIMGeneric dim = null;
        try{
            dim = new DIMGeneric(results);
            return dim.getXML();
        }
        catch (Exception ex) {
//            LoggerFactory.getLogger(RestDimResource.class).error(ex.getMessage(), ex);
        	ex.printStackTrace();
        }
        //and returns an xml version of our dim search
        return null;

    }
    
    private static String processJSON(Collection<SearchResult> results, long elapsedTime){
    	JSONObject resp = new JSONObject();
    	for(SearchResult r : results){
    		JSONObject rj = new JSONObject();
    		rj.put("uri", r.getURI().toString());
    		
    		JSONObject fields = new JSONObject();
    		fields.accumulateAll(r.getExtraData());
    		
    		rj.put("fields", fields);
    		
    		resp.accumulate("results", rj);
    	}
    	resp.put("numResults", results.size());
    	resp.put("elapsedTime", elapsedTime);
    	
    	return resp.toString();
    }
    
    private static class MyHolder extends JointQueryTask{

		@Override
		public void onCompletion() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReceive(Task<Iterable<SearchResult>> e) {
			// TODO Auto-generated method stub
			
		}
    	
    }
}
