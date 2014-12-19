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

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;

/**
 * Simple holder class to store one hit from searches.
 * 
 * @author Marco
 * @author Carlos Ferreira
 * @author Pedro Bento
 * modified by: Frederico Valente
 */
public class SearchResult implements Serializable {
    private URI location; //a uri that allows us to fetch the result object
    private double score; //score given by the querier
    
    //stores extra data, placed by specific plugins or inserted along the way
    private HashMap<String, Object> extraData = new HashMap<>(); 

    public SearchResult(URI location, double score, HashMap<String,Object> data){
        this.location = location;
        this.score = score;
        
        if(data != null)
            this.extraData = data;
    }
    
    
    /**
     * Gets the location of the hit
     * @return URI pointing to dicom object
     */
    public URI getURI(){return location;}

    
    public Object get(String tag){
        return extraData.get(tag);
    }
    
    public void put(String tag, Object value){
        extraData.put(tag, value);
    }
    
    public <T> T getData(String extraFieldName){
        Object ret = extraData.get(extraFieldName);
        
        if(ret == null) return null;
         
        try{
            return (T) ret;
        }
        catch(ClassCastException e){
            return null;
        }
    }
    
    /**
     * Unlike what the name says, this does not returns the score.
     * Unless it does. Ok, it does... which is most definitely weird given the method name.
     * This score is a measure of *dissimilarity* 
     * that is, a score of 0 should mean an exact hit
     * 
     * @return score of the result;
     */
    public double getScore(){return score;}
    
    /**
     * Gets the extra documents fields
     * @return the table containing the extra fields
     */
    public HashMap<String, Object> getExtraData(){return extraData;}

    @Override
    public String toString(){
        return location.toString()+" "+score;
    }
    
    
    /*  Returns a tree made of hashmaps
     * the first key is the patientName, the second the study, followed by series and image uri
     * 
     * todo: move the goddamned hash train to a class,
     * just the definition of this makes me sick
     */
    public static HashMap<String, HashMap<String, HashMap<String, HashMap<String, SearchResult>>>> toTree(Iterable<SearchResult> results){
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, SearchResult>>>> tree = new HashMap<>();
        
        for(SearchResult r : results){
            URI fileName = r.getURI();
            String patientName = r.<String>getData("PatientName");
            String studyUID = r.<String>getData("StudyInstanceUID");
            String seriesUID = r.<String>getData("SeriesInstanceUID");
            //we could have a SOPInstanceUID here but we use the search result itself as last element

            if(patientName == null) patientName = "undefined PatientName";
            else{patientName = patientName.trim();}
            
            if(studyUID == null) studyUID = "undefined StudyInstanceUID";
            else{studyUID = studyUID.trim();}
            
            if(seriesUID == null) seriesUID = "undefined SeriesInstanceUID";
            else{seriesUID = seriesUID.trim();}
            
            //check patient
            if(tree.containsKey(patientName)){    
                if(tree.get(patientName).containsKey(studyUID)){
                    if(tree.get(patientName).get(studyUID).containsKey(seriesUID)){
                        tree.get(patientName).get(studyUID).get(seriesUID).put(fileName.toString(), r);
                    }
                    else{
                        HashMap<String, SearchResult> seriesResults = new HashMap<>();
                        seriesResults.put(fileName.toString(), r);
                        
                        tree.get(patientName).get(studyUID).put(seriesUID, seriesResults);
                    }
                }
                else{//no study
                    HashMap<String, SearchResult> seriesResults = new HashMap<>();
                    seriesResults.put(fileName.toString(), r);
                
                    HashMap<String, HashMap<String, SearchResult>> studySeries = new HashMap<>();
                    studySeries.put(seriesUID, seriesResults);
                
                    tree.get(patientName).put(studyUID, studySeries);
                }
            }
            else{//no patient name
                HashMap<String, SearchResult> seriesResults = new HashMap<>();
                seriesResults.put(fileName.toString(), r);
                
                HashMap<String, HashMap<String, SearchResult>> studySeries = new HashMap<>();
                studySeries.put(seriesUID, seriesResults);
                
                HashMap<String, HashMap<String, HashMap<String,SearchResult>>> patientStudies = new HashMap<>();
                patientStudies.put(studyUID, studySeries);
                
                tree.put(patientName, patientStudies);//add patient, study, series, image to tree
            }
        }
        return tree;
    }
    
}
