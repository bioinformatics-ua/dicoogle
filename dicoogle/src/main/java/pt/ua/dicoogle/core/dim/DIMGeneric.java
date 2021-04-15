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


import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.*;

import org.json.JSONException;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private ConcatTags tags;

    private static final Logger logger = LoggerFactory.getLogger(DIMGeneric.class);

    public DIMGeneric(ConcatTags tags, Iterable<SearchResult> arr) throws Exception
    {
        this.tags = tags;
        fill(arr, 4);

    }

    /**
     * it is allow to handle a ArrayList of Strings or SearchResults
     * @param arr
     */
    public DIMGeneric(Iterable<SearchResult> arr, int depth) throws Exception{
        fill(arr, depth);
    }

    public DIMGeneric(Iterable<SearchResult> arr) throws Exception{
        this(arr, 4);
    }

    /**
     * it is allow to handle a ArrayList of Strings or SearchResults
     * @param arr
     */
    private void fill(Iterable<SearchResult> arr, int depth) {
            //DebugManager.getInstance().debug("Looking search results: " + arr.size() );

            Map<String, String> descriptions = new HashMap<String, String>();

            for(SearchResult r : arr){

                /**
                 * Looking for SearchResults and put it in right side :)
                 */

                //SearchResult r = (SearchResult) arr.get(i);
                final HashMap<String, Object> extra = r.getExtraData();

                /** Get data from Study */
                final String studyUID = toTrimmedString(extra.get("StudyInstanceUID"),false);
                final String modality = toTrimmedString( extra.get("Modality"), false);
                final String patientID =  toTrimmedString( extra.get("PatientID"), false);
                final String patientName =  toTrimmedString(extra.get("PatientName"), false);
                String StudyDescription = toTrimmedString( extra.get("StudyDescription"), false);

                /**
                 * Get data to Image
                 */
                //TODO:Error checking here... but according to standard, all images
                //must have one of these...
                String sopInstUID =  toTrimmedString(extra.get("SOPInstanceUID"), true);
                         
                if(sopInstUID == null)
                    sopInstUID ="no uid";

                logger.debug("StudyDescription: {}", StudyDescription);
                
                // This is a quick fix for changing the parameters for the query.
                if ((StudyDescription==null||StudyDescription.equals("")||StudyDescription.toLowerCase().contains("fuji"))
                        &&
                        this.tags!=null) {
                    logger.debug("Checking if the Rule applies: {}", studyUID);
                    String description = "";

                    if (descriptions.containsKey(studyUID)) {
                        description = descriptions.get(studyUID);
                    }

                    for (ConcatTags.Rule rule : this.tags.getRules())
                    {
                        logger.debug("Checking if the Rule applies: {}", modality);

                        if (modality.equals(rule.getModality())) {
                            String valueTagToReplace = (String) extra.get(rule.getTagToReplace());
                            logger.debug("Checking if the Rule value. {}", valueTagToReplace);
                            logger.debug("Checking if the Rule value. {}", rule.getTagToReplace());

                            if (valueTagToReplace!=null)
                            {
                                // Required to production enviroment. I'm not changing to toTrimmedString until get a stable release.
                                // complete ported.
                                valueTagToReplace = valueTagToReplace.trim().replaceAll("[^a-zA-Z0-9\\. ÉéàÀÃ;,]+","");
                                description = description + valueTagToReplace + "; ";
                            }
                        }

                    }
                    StudyDescription = description;
                    descriptions.put(studyUID, StudyDescription);

                }

                String patientIdentifier = patientID;
                if (patientID.equals("")) {
                    patientIdentifier = patientName;
                }

                /** Verify if Patient already exists */

                if (this.patientsHash.containsKey(patientIdentifier))
                {
                    if (depth >= 1) {
                        /**
                         * Patient Already exists, let's check studys
                         */
                        // Real data does not have Patient Id - sometimes.
                        Patient p = this.patientsHash.get(patientIdentifier);

                        Study s = this.fillStudy(p, extra, StudyDescription);
                        if (depth > 2) {
                            Serie serie = this.fillSeries(s, extra);
                            if (depth > 3) {
                                serie.addImage(r.getURI(), sopInstUID);
                            }
                        }
                    }
                }
                else {
                    /**
                     * Patient does not exist
                     */
                    Patient p = new Patient(patientID, patientName);

                    /* Get Patient Data */
                    String patientSex = toTrimmedString(  extra.get("PatientSex"), false);
                    p.setPatientSex(patientSex);

                    String patientBirthDate = toTrimmedString(  extra.get("PatientBirthDate"), false);
                    p.setPatientBirthDate(patientBirthDate);

                    if (depth >= 1) {
                        /**
                         * Create Study
                         */
                        Study s = this.fillStudy(p, extra, StudyDescription);
                        if (depth > 2) {
                            Serie serie = this.fillSeries(s, extra);
                            if (depth > 3) {
                                serie.addImage(r.getURI(), sopInstUID);
                            }
                        }
                    }
                    this.patients.add(p);
                    this.patientsHash.put(patientIdentifier, p);
                }
            }

    }

    /**
     * Add Study
     * It also verify if it exists
     * In the last case Object will be discarded and the
     * data will be added for the Study that already exists
     */
    public Study fillStudy(Patient parent, Map<String, Object> extra, String StudyDescription) {
        /** Get data from Study */
        String studyUID = toTrimmedString(extra.get("StudyInstanceUID"),false);
        String studyID = toTrimmedString(extra.get("StudyID"), false);
        String studyDate = toTrimmedString( extra.get("StudyDate"), false);
        String studyTime = toTrimmedString( extra.get("StudyTime"), true);
        String AccessionNumber = toTrimmedString( extra.get("AccessionNumber"), false);
        String InstitutionName = toTrimmedString( extra.get("InstitutionName"), false);
        String operatorsName = toTrimmedString (extra.get("OperatorsName"), false);
        String RequestingPhysician = toTrimmedString (extra.get("RequestingPhysician"), false);
        String RequestedProcedurePriority = toTrimmedString (extra.get("RequestedProcedurePriority"), false);


        Study s = parent.getStudy(studyUID);
        if (s == null) {
            s = new Study(parent, studyUID, studyDate);
            parent.addStudy(s);
        }
        s.setInstitutuionName(InstitutionName);
        s.setAccessionNumber(AccessionNumber);
        s.setStudyTime(studyTime);
        s.setStudyID(studyID);
        s.setStudyDescription(StudyDescription);
        s.setPatientName(parent.getPatientName());
        s.setOperatorsName(operatorsName);
        s.setRequestingPhysician(RequestingPhysician);
        s.setRequestedProcedurePriority(RequestedProcedurePriority);
        return s;
    }

    public Serie fillSeries(Study parent, Map<String, Object> extra) {
        String seriesUID =  toTrimmedString( extra.get("SeriesInstanceUID"), false);
        String modality = toTrimmedString( extra.get("Modality"), false);
        Serie s = parent.getSeries(seriesUID);
        if (s == null) {
            s = new Serie(parent, seriesUID, modality);
            parent.addSerie(s);
        }

        String serieNumber = toTrimmedString(extra.get("SeriesNumber"), true);
        if (serieNumber != null && !serieNumber.equals("")) {
            s.setSerieNumber((int) Float.parseFloat(serieNumber));
        }

        String serieDescription = toTrimmedString(  extra.get("SeriesDescription"), false);
        s.setSeriesDescription(serieDescription);

        String SeriesDate =  toTrimmedString(extra.get("SeriesDate"), false);
        s.setSeriesDate(SeriesDate);

        String ProtocolName =  toTrimmedString(extra.get("ProtocolName"), false);
        s.setProtocolName(ProtocolName);

        String ViewPosition = toTrimmedString( extra.get("ViewPosition"), false);
        s.setViewPosition(ViewPosition);

        String ImageLaterality = toTrimmedString( extra.get("ImageLaterality"), false);
        s.setImageLaterality(ImageLaterality);

        String ViewCodeSequence_CodeValue =  toTrimmedString( extra.get("ViewCodeSequence_CodeValue"), false);
        s.setViewCodeSequence_CodeValue(ViewCodeSequence_CodeValue);

        String ViewCodeSequence_CodingSchemeDesignator =  toTrimmedString( extra.get("ViewCodeSequence_CodingSchemeDesignator"), false);
        s.setViewCodeSequence_CodingSchemeDesignator(ViewCodeSequence_CodingSchemeDesignator);

        String ViewCodeSequence_CodingSchemeVersion =  toTrimmedString( extra.get("ViewCodeSequence_CodingSchemeVersion"), false);
        s.setViewCodeSequence_CodingSchemeVersion(ViewCodeSequence_CodingSchemeVersion);

        String ViewCodeSequence_CodeMeaning =  toTrimmedString( extra.get("ViewCodeSequence_CodeMeaning"), false);
        s.setViewCodeSequence_CodeMeaning(ViewCodeSequence_CodeMeaning);

        String AcquisitionDeviceProcessingDescription = toTrimmedString( extra.get("AcquisitionDeviceProcessingDescription"), false);
        s.setAcquisitionDeviceProcessingDescription(AcquisitionDeviceProcessingDescription);

        return s;
    }

    public void writeJSON(Writer destinationWriter, long elapsedTime) throws IOException {
        this.writeJSON(destinationWriter, elapsedTime, 4, 0, Integer.MAX_VALUE);
    }

    public void writeJSON(Writer destinationWriter, long elapsedTime, int depth, int offset, int psize) throws IOException {
        JSONWriter writer = new JSONWriter(destinationWriter);
        try {
            writer.object()
                    .key("numResults").value(this.patients.size());
            if (depth > 0) {
                writer.key("results").array();

                for (Patient p : this.patients) {
                    if (offset-- > 0) continue;

                    writer.object()
                            .key("id").value(p.getPatientID())
                            .key("name").value(p.getPatientName())
                            .key("gender").value(p.getPatientSex())
                            .key("birthdate").value(p.getPatientBirthDate())
                            .key("nStudies").value(p.getStudies().size());

                    if (depth > 1) {
                        writer.key("studies").array(); // begin studies

                        for (Study study : p.getStudies()) {
                            writer.object()    // begin study
                                    .key("studyInstanceUID").value(study.getStudyInstanceUID())
                                    .key("studyDescription").value(study.getStudyDescription())
                                    .key("studyDate").value(study.getStudyData())
                                    .key("institutionName").value(study.getInstitutuionName())
                                    //.key("nSeries").value(study.getSeries().size())
                            ;
                            Set<String> modalities = new HashSet<>();

                            if (depth > 2) {
                                writer.key("series").array(); // begin series (array)
                                for (Serie series : study.getSeries()) {
                                    String modality = series.getModality();
                                    int nimages = series.getSOPInstanceUIDList().size();
                                    writer.object() // begin series
                                            .key("serieNumber").value(series.getSerieNumber())
                                            .key("serieInstanceUID").value(series.getSerieInstanceUID())
                                            .key("serieDescription").value(series.getSeriesDescription())
                                            .key("serieModality").value(modality)
                                            //.key("nImages").value(nimages)
                                    ;
                                    if (depth > 3) {
                                        writer.key("images").array(); // begin images
                                        for (int i = 0; i < nimages; i++) {
                                            String rawPath = series.getImageList().get(i).getRawPath();
                                            writer.object() // begin image
                                                    .key("sopInstanceUID").value(series.getSOPInstanceUIDList().get(i))
                                                    .key("rawPath").value(rawPath)
                                                    .key("uri").value(series.getImageList().get(i).toString())
                                                    .key("filename").value(rawPath.substring(rawPath.lastIndexOf("/")+1, rawPath.length()))
                                                    .endObject(); // end image
                                        }
                                        writer.endArray(); // end images
                                    }

                                    modalities.add(modality);
                                    writer.endObject(); // end series
                                }
                                writer.endArray(); // end series (array)
                                writer.key("modalities");
                                if (modalities.size() == 1) {
                                    writer.value(modalities.iterator().next());
                                } else {
                                    writer.array(); // begin modalities in study
                                    for (String m : modalities) {
                                        writer.value(m);
                                    }
                                    writer.endArray(); // end modalities in study
                                }
                            }
                            writer.endObject(); // end study
                        }
                        writer.endArray(); // end studies
                    }
                    writer.endObject(); // end patient
                    if (--psize <= 0) break;
                }
                writer.endArray(); // end patients
            }
            writer
                    .key("elapsedTime").value(elapsedTime)
                    .endObject(); // end output
        } catch (JSONException e) {
            throw new IOException("JSON serialization error", e);
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
    			study.put("studyInstanceUID", s.getStudyInstanceUID());
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
    					image.put("uri", serie.getImageList().get(i).toString());
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
            LoggerFactory.getLogger(DIMGeneric.class).error(ex.getMessage(), ex);
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
            LoggerFactory.getLogger(DIMGeneric.class.getName()).error(ex.getMessage(), ex);
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
