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
package pt.ua.dicoogle.core.dim;


import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import pt.ua.dicoogle.sdk.datastructs.SearchResult;


/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class DIMGeneric
{
    /**
     * There are double space to save the results but it decrease
     * the search time and it is important because a querySearch should be
     * little enough.
     */
    private ArrayList<Patient> patients = new ArrayList<>();
    private HashMap<String, Patient> patientsHash  = new HashMap<>();
    
    private static String toTrimmedString(Object o, boolean allowNull){
    	if(o == null)
    		if(allowNull)
    			return null;
    		else return "";
    		
    	if(allowNull)
    		return StringUtils.trimToNull(o.toString());
    	
    	return StringUtils.trimToEmpty(o.toString());
    }
    
    /**
     * it is allow to handle a ArrayList of Strings or SearchResults
     * @param arr
     */
    public DIMGeneric(Collection<SearchResult> arr) throws Exception{
            HashMap<String, Object> extra = null;
            //DebugManager.getInstance().debug("Looking search results: " + arr.size() );

            //int size = arr.size();
            for(SearchResult r : arr){
            //for (int i = 0 ; i<size ; i++ ){
                //DebugManager.getInstance().debug("Adding new Image ");
            
                /**
                 * Looking for SeachResults and put it in right side :) 
                 */

                //SearchResult r = (SearchResult) arr.get(i);
                extra = r.getExtraData();

                /** Get data from Study */
                String studyUID = toTrimmedString(extra.get("StudyInstanceUID"),false);
                String studyID = toTrimmedString(extra.get("StudyID"), false);
                String studyDate = toTrimmedString( extra.get("StudyDate"), false);
                String studyTime = toTrimmedString( extra.get("StudyTime"), true);
                String AccessionNumber = toTrimmedString( extra.get("AccessionNumber"), false);
                String StudyDescription = toTrimmedString( extra.get("StudyDescription"), false);
                String InstitutionName = toTrimmedString( extra.get("InstitutionName"), false);
                /**
                 * Get data to Serie
                 */
                String serieUID =  toTrimmedString( extra.get("SeriesInstanceUID"), false);
                //System.out.println("serieUID"+serieUID);
                String serieNumber = toTrimmedString(extra.get("SeriesNumber"), true);
                String serieDescription = toTrimmedString(  extra.get("SeriesDescription"), false);
                String modality = toTrimmedString( extra.get("Modality"), false);
                String patientID =  toTrimmedString( extra.get("PatientID"), false);


                String patientSex = toTrimmedString(  extra.get("PatientSex"), false);
                String patientBirthDate = toTrimmedString(  extra.get("PatientBirthDate"), false);
                
                String patientName =  toTrimmedString(extra.get("PatientName"), false);
                                
                /**
                 * Get data to Image
                 */
                //TODO:Error checking here... but according to standard, all images
                //must have one of these...
                String sopInstUID =  toTrimmedString(extra.get("SOPInstanceUID"), true);
                         
                if(sopInstUID == null)
                    sopInstUID ="no uid";

                /** Verify if Patient already exists */

                if (this.patientsHash.containsKey(patientName))
                {
                    /**
                     * Patient Already exists, let's check studys
                     */

                    Patient p = this.patientsHash.get(patientName);


                    /**
                     * Add Study
                     * It also verify if it exists
                     * In the last case Object will be discarded and the
                     * data will be added for the Study that already exists
                     */

                    Study s = new Study(p,studyUID,studyDate) ;
                    s.setInstitutuionName(InstitutionName);
                    s.setAccessionNumber(AccessionNumber);
                    s.setStudyTime(studyTime);
                    s.setStudyID(studyID);
                    s.setStudyDescription(StudyDescription);
                    Serie serie = new Serie(s, serieUID, modality);
                    if (serieNumber!=null)
                        serie.setSerieNumber((int)Float.parseFloat(serieNumber));
                    serie.setSeriesDescription(serieDescription);
                    serie.addImage(r.getURI(),sopInstUID);
                    s.addSerie(serie) ;
                    p.addStudy(s);
                }
                else {
                    /**
                     * Patient does not exist
                     */
                     Patient p = new Patient(patientID, patientName);
                     p.setPatientSex(patientSex);
                     p.setPatientBirthDate(patientBirthDate);

                     /**
                      * Create Study
                      */
                     Study s = new Study(p, studyUID,studyDate) ;
                     s.setAccessionNumber(AccessionNumber);
                     s.setStudyTime(studyTime);
                     s.setStudyDescription(StudyDescription);
                     s.setStudyID(studyID);
                     s.setInstitutuionName(InstitutionName);
                     p.addStudy(s);

                     /**
                     * Create Serie
                     */
                     Serie serie = new Serie(s,serieUID, modality);
                     if (serieNumber!=null && !serieNumber.equals(""))
                        serie.setSerieNumber((int)Float.parseFloat(serieNumber));
                     serie.setSeriesDescription(serieDescription);
                     serie.addImage(r.getURI(),sopInstUID);
                     s.addSerie(serie);
                     this.patients.add(p);
                     this.patientsHash.put(patientName, p);
                }
            }

    }

    public String getJSON(){
    	JSONObject result = new JSONObject();
    	result.put("numResults", this.patients.size());
    	JSONArray patients = new JSONArray();
    	
    	for (Patient p : this.patients){
    		JSONObject patient = new JSONObject();
    		patient.put("id", p.getPatientID());
    		patient.put("name", p.getPatientName());
    		patient.put("gender", p.getPatientSex());
    		patient.put("nStudies", p.getStudies().size());
    		patient.put("birthdate", p.getPatientBirthDate());
    		
    		JSONArray studies = new JSONArray();
    		for(Study s: p.getStudies())
    		{
    			JSONObject study = new JSONObject();
    			study.put("studyDate", s.getStudyData());
    			study.put("studyDescription", s.getStudyDescription());
    			study.put("institutionName", s.getInstitutuionName());
    			
    			JSONArray modalities = new JSONArray();
    			JSONArray series = new JSONArray();
    			
    			Set<String> modalitiesSet = new HashSet<>();
    			for(Serie serie : s.getSeries())
    			{
    				modalitiesSet.add(serie.getModality());
    				modalities.add(serie.getModality());
    				
    				JSONObject _serie = new JSONObject();
    				_serie.put("serieNumber", serie.getSerieNumber());
    				_serie.put("serieInstanceUID", serie.getSerieInstanceUID());
    				_serie.put("serieDescription", serie.getSeriesDescription());
    				_serie.put("serieModality", serie.getModality());
    				
    				JSONArray _sopInstanceUID = new JSONArray();
    				for(int i=0; i<serie.getSOPInstanceUIDList().size();i++){
    					JSONObject image = new JSONObject();
    					image.put("sopInstanceUID", serie.getSOPInstanceUIDList().get(i));
    					String rawPath = serie.getImageList().get(i).getRawPath();
    					image.put("rawPath", rawPath);
    					image.put("filename", rawPath.substring(rawPath.lastIndexOf("/")+1, rawPath.length()));
    					
    					_sopInstanceUID.add(image);
    				}
    				_serie.put("images", _sopInstanceUID);
    
    				series.add(_serie);
    			}
    			study.put("modalities", StringUtils.join(modalitiesSet,","));
    			study.put("series", series);
    			
    			
    			
    			
    			studies.add(study);
    			
    		}
    		patient.put("studies", studies);
    		
    		
    		patients.add(patient);
    	}
    	
    	result.put("results", patients);
    	
    	return result.toString();
    }
    
    public String getXML()
    {

        StringWriter writer = new StringWriter();


        StreamResult streamResult = new StreamResult(writer);
        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        //      SAX2.0 ContentHandler.
        TransformerHandler hd = null;
        try {
            hd = tf.newTransformerHandler();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(DIMGeneric.class.getName()).log(Level.SEVERE, null, ex);
        }
        Transformer serializer = hd.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.METHOD, "xml");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        hd.setResult(streamResult);
        try {
            hd.startDocument();

            AttributesImpl atts = new AttributesImpl();
            hd.startElement("", "", "DIM", atts);

            for (Patient p : this.patients)
            {
                atts.clear();
                atts.addAttribute("", "", "name", "",p.getPatientName().trim());
                atts.addAttribute("", "", "id", "",p.getPatientID().trim());
                hd.startElement("", "", "Patient", atts);


                for (Study s: p.getStudies())
                {
                    atts.clear();
                    atts.addAttribute("", "", "date", "",s.getStudyData().trim());
                    atts.addAttribute("", "", "id", "", s.getStudyInstanceUID().trim());

                    hd.startElement("", "", "Study", atts);

                    for (Serie serie : s.getSeries()){
                        atts.clear();
                        atts.addAttribute("", "", "modality", "", serie.getModality().trim());
                        atts.addAttribute("", "", "id", "", serie.getSerieInstanceUID().trim());

                        hd.startElement("", "", "Serie", atts);

                        ArrayList<URI> img = serie.getImageList();
                        ArrayList<String> uid = serie.getSOPInstanceUIDList();
                        int size = img.size();
                        for (int i=0;i<size;i++){
                            atts.clear();
                            atts.addAttribute("", "", "path", "", img.get(i).toString().trim());
                            atts.addAttribute("", "", "uid", "", uid.get(i).trim());

                            hd.startElement("", "", "Image", atts);
                            hd.endElement("", "", "Image");
                        }
                        hd.endElement("", "", "Serie");
                    }
                    hd.endElement("", "", "Study");
                }
                hd.endElement("", "", "Patient");
            }
            hd.endElement("", "", "DIM");

        } catch (SAXException ex) {
            Logger.getLogger(DIMGeneric.class.getName()).log(Level.SEVERE, null, ex);
        }

        return writer.toString() ;
    }

    /**
     * @return the patients
     */
    public ArrayList<Patient> getPatients() {
        return patients;
    }
}
