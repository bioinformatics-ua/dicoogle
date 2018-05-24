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
package pt.ua.dicoogle.sdk.datastructs.dim;

import java.io.StringWriter;
import java.net.URI;
import java.util.*;

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
 * This class is responsible to transform a list of SearchResult in a DIM model.
 * Historically, this class take times to render in large amounts of image has a linear complexity.
 * This also implemented in a type that we mainly focus in an archive image oriented.
 *
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class DIMGeneric {

    /**
     *
     * There are double space to save the results but it decrease the search
     * time and it is important because a querySearch should be little enough.
     */
    private ArrayList<Patient> patients = new ArrayList<>();
    private HashMap<String, Patient> patientsHash = new HashMap<>();

    private static String toTrimmedString(Object o, boolean allowNull) {
        if (o == null) {
            if (allowNull) {
                return null;
            } else {
                return "";
            }
        }

        if (allowNull) {
            return StringUtils.trimToNull(o.toString());
        }

        return StringUtils.trimToEmpty(o.toString());
    }

    private ConcatTags tags;

    private static final Logger logger = LoggerFactory.getLogger(DIMGeneric.class);

    public DIMGeneric(ConcatTags tags, Collection<SearchResult> arr) throws Exception {
        this.tags = tags;
        fill(arr);

    }

    /**
     * it is allow to handle a ArrayList of Strings or SearchResults
     *
     * @param arr
     */
    public DIMGeneric(Collection<SearchResult> arr) throws Exception {
        fill(arr);

    }

    /**
     * it is allow to build dimGeneric with an Map
     *
     * @param arr
     * @param uri
     */
    public DIMGeneric(Map<String, Object> arr,URI uri) throws Exception {
        fillWithMap(arr, uri);
    }

    /**
     * it is allow to handle a ArrayList of Strings or SearchResults
     *
     * @param arr
     */
    private void fill(Collection<SearchResult> arr) {
        for (SearchResult r : arr) {
            /**
             * Looking for SeachResults and put it in right side :)
             */
            //SearchResult r = (SearchResult) arr.get(i);
            HashMap<String, Object> extra = r.getExtraData();
            fillDim(extra, r.getURI());

        }

    }

    /**
     * it is allow to handle a map with keys and values
     *
     * @param arr
     * @param uri
     */
    private void fillWithMap(Map<String, Object> arr, URI uri) {
        fillDim(arr, uri);
    }

    /**
     * Generic function for fill dim object
     * @param extra
     * @param uri 
     */
    private void fillDim(Map<String, Object> extra, URI uri) {
        Map<String, String> descriptions = new HashMap<String, String>();
        /**
         * Get data from Study
         */
        String studyUID = toTrimmedString(extra.get("StudyInstanceUID"), false);
        String studyID = toTrimmedString(extra.get("StudyID"), false);
        String studyDate = toTrimmedString(extra.get("StudyDate"), false);
        String studyTime = toTrimmedString(extra.get("StudyTime"), true);
        String AccessionNumber = toTrimmedString(extra.get("AccessionNumber"), false);
        String StudyDescription = toTrimmedString(extra.get("StudyDescription"), false);
        String InstitutionName = toTrimmedString(extra.get("InstitutionName"), false);
        String operatorsName = toTrimmedString(extra.get("OperatorsName"), false);
        String RequestingPhysician = toTrimmedString(extra.get("RequestingPhysician"), false);

        /**
         * Get data to Series
         */
        String serieUID = toTrimmedString(extra.get("SeriesInstanceUID"), false);
        String BodyPartThickness = (String) extra.get("BodyPartThickness");
        //System.out.println("serieUID"+serieUID);
        String serieNumber = toTrimmedString(extra.get("SeriesNumber"), true);
        String serieDescription = toTrimmedString(extra.get("SeriesDescription"), false);
        String modality = toTrimmedString(extra.get("Modality"), false);
        String patientID = toTrimmedString(extra.get("PatientID"), false);

        String ViewPosition = toTrimmedString(extra.get("ViewPosition"), false);
        String ImageLaterality = toTrimmedString(extra.get("ImageLaterality"), false);
        String AcquisitionDeviceProcessingDescription = toTrimmedString(extra.get("AcquisitionDeviceProcessingDescription"), false);

        String ViewCodeSequence_CodeValue = toTrimmedString(extra.get("ViewCodeSequence_CodeValue"), false);
        String ViewCodeSequence_CodingSchemeDesignator = toTrimmedString(extra.get("ViewCodeSequence_CodingSchemeDesignator"), false);
        String ViewCodeSequence_CodingSchemeVersion = toTrimmedString(extra.get("ViewCodeSequence_CodingSchemeVersion"), false);
        String ViewCodeSequence_CodeMeaning = toTrimmedString(extra.get("ViewCodeSequence_CodeMeaning"), false);


        /* Get Patient Data */
        String patientSex = toTrimmedString(extra.get("PatientSex"), false);
        String patientBirthDate = toTrimmedString(extra.get("PatientBirthDate"), false);

        String patientName = toTrimmedString(extra.get("PatientName"), false);

        String SeriesDate = toTrimmedString(extra.get("SeriesDate"), false);
        String ProtocolName = toTrimmedString(extra.get("ProtocolName"), false);

        /**
         * Get data to Image
         */
        //TODO:Error checking here... but according to standard, all images
        //must have one of these...
        String sopInstUID = toTrimmedString(extra.get("SOPInstanceUID"), true);

        if (sopInstUID == null) {
            sopInstUID = "no uid";
        }

        logger.debug("StudyDescription: " + StudyDescription);

        // This is a quick fix for changing the parameters for the query.
        if ((StudyDescription == null || StudyDescription.equals("") || StudyDescription.toLowerCase().contains("fuji"))
                && this.tags != null) {
            logger.debug("Checking if the Rule applies." + studyUID);
            String description = "";

            if (descriptions.containsKey(studyUID)) {
                description = descriptions.get(studyUID);
            }

            for (ConcatTags.Rule rule : this.tags.getRules()) {
                logger.debug("Checking if the Rule applies." + modality);

                if (modality.equals(rule.getModality())) {

                    String valueTagToReplace = (String) extra.get(rule.getTagToReplace());
                    logger.debug("Checking if the Rule value." + valueTagToReplace);
                    logger.debug("Checking if the Rule value." + rule.getTagToReplace());

                    if (valueTagToReplace != null) {
                        // Required to production enviroment. I'm not changing to toTrimmedString until get a stable release.
                        // complete ported.
                        valueTagToReplace = valueTagToReplace.trim().replaceAll("[^a-zA-Z0-9\\. ÉéàÀÃ;,]+", "");
                        description = description + valueTagToReplace + "; ";

                    }

                }

            }
            StudyDescription = description;
            descriptions.put(studyUID, StudyDescription);

        }

        String patientIdentifier = "";
        if (patientID.equals("")) {
            patientIdentifier = patientName;
        } else {
            patientIdentifier = patientID;
        }

        /**
         * Verify if Patient already exists
         */
        if (this.patientsHash.containsKey(patientIdentifier)) {
            /**
             * Patient Already exists, let's check studys
             */
            // Real data does not have Patient Id - sometimes.
            Patient p = this.patientsHash.get(patientIdentifier);

            /**
             * Add Study It also verify if it exists In the last case Object
             * will be discarded and the data will be added for the Study that
             * already exists
             */
            Study s = new Study(p, studyUID, studyDate);

            s.setInstitutuionName(InstitutionName);
            s.setAccessionNumber(AccessionNumber);
            s.setStudyTime(studyTime);
            s.setStudyID(studyID);
            s.setStudyDescription(StudyDescription);

            s.setPatientName(patientName);

            s.setOperatorsName(operatorsName);
            s.setRequestingPhysician(RequestingPhysician);

            Series series = new Series(s, serieUID, modality);
            try {
                if (serieNumber != null) {
                    series.setSeriesNumber((int) Float.parseFloat(serieNumber));
                }
            } catch (Exception ex) {
                // nothing to do anyway
            }
            series.setSeriesDescription(serieDescription);
            series.setSeriesDescription(serieDescription);
            series.setProtocolName(ProtocolName);
            series.setSeriesDate(SeriesDate);
            series.setBodyPartThickness(BodyPartThickness);
            series.setViewPosition(ViewPosition);
            series.setImageLaterality(ImageLaterality);
            series.setAcquisitionDeviceProcessingDescription(AcquisitionDeviceProcessingDescription);
            series.setViewCodeSequence_CodeMeaning(ViewCodeSequence_CodeMeaning);
            series.setViewCodeSequence_CodeValue(ViewCodeSequence_CodeValue);
            series.setViewCodeSequence_CodingSchemeDesignator(ViewCodeSequence_CodingSchemeDesignator);
            series.setViewCodeSequence_CodingSchemeVersion(ViewCodeSequence_CodingSchemeVersion);

            series.addImage(uri, sopInstUID);
            s.addSerie(series);
            p.addStudy(s);

        } else {
            /**
             * Patient does not exist
             */
            Patient p = new Patient(patientID, patientName);
            p.setPatientSex(patientSex);
            p.setPatientBirthDate(patientBirthDate);

            /**
             * Create Study
             */
            Study s = new Study(p, studyUID, studyDate);
            s.setAccessionNumber(AccessionNumber);
            s.setStudyTime(studyTime);
            s.setStudyDescription(StudyDescription);
            s.setStudyID(studyID);
            s.setInstitutuionName(InstitutionName);
            s.setPatientName(patientName);

            s.setOperatorsName(operatorsName);
            s.setRequestingPhysician(RequestingPhysician);

            p.addStudy(s);

            /**
             * Create Series
             */
            Series series = new Series(s, serieUID, modality);
            if (serieNumber != null && !serieNumber.equals("")) {
                series.setSeriesNumber((int) Float.parseFloat(serieNumber));
            }
            series.setSeriesDescription(serieDescription);
            series.setProtocolName(ProtocolName);

            series.setSeriesDate(SeriesDate);
            series.setViewPosition(ViewPosition);
            series.setImageLaterality(ImageLaterality);
            series.setAcquisitionDeviceProcessingDescription(AcquisitionDeviceProcessingDescription);
            series.setViewCodeSequence_CodeMeaning(ViewCodeSequence_CodeMeaning);
            series.setViewCodeSequence_CodeValue(ViewCodeSequence_CodeValue);
            series.setViewCodeSequence_CodingSchemeDesignator(ViewCodeSequence_CodingSchemeDesignator);
            series.setViewCodeSequence_CodingSchemeVersion(ViewCodeSequence_CodingSchemeVersion);

            series.addImage(uri, sopInstUID);
            s.addSerie(series);
            this.patients.add(p);
            this.patientsHash.put(patientIdentifier, p);
        }
    }

    public String getJSON() {
        JSONObject result = new JSONObject();
        result.put("numResults", this.patients.size());
        JSONArray patients = new JSONArray();

        for (Patient p : this.patients) {
            JSONObject patient = new JSONObject();
            patient.put("id", p.getPatientID());
            patient.put("name", p.getPatientName());
            patient.put("gender", p.getPatientSex());
            patient.put("nStudies", p.getStudies().size());
            patient.put("birthdate", p.getPatientBirthDate());

            JSONArray studies = new JSONArray();
            for (Study s : p.getStudies()) {
                JSONObject study = new JSONObject();
                study.put("studyDate", s.getStudyData());
                study.put("studyDescription", s.getStudyDescription());
                study.put("institutionName", s.getInstitutuionName());

                JSONArray modalities = new JSONArray();
                JSONArray series = new JSONArray();

                Set<String> modalitiesSet = new HashSet<>();
                for (Series serie : s.getSeries()) {
                    modalitiesSet.add(serie.getModality());
                    modalities.add(serie.getModality());

                    JSONObject _serie = new JSONObject();
                    _serie.put("serieNumber", serie.getSeriesNumber());
                    _serie.put("serieInstanceUID", serie.getSeriesInstanceUID());
                    _serie.put("serieDescription", serie.getSeriesDescription());
                    _serie.put("serieModality", serie.getModality());

                    JSONArray _sopInstanceUID = new JSONArray();
                    for (int i = 0; i < serie.getSOPInstanceUIDList().size(); i++) {
                        JSONObject image = new JSONObject();
                        image.put("sopInstanceUID", serie.getSOPInstanceUIDList().get(i));
                        String rawPath = serie.getImageList().get(i).getRawPath();
                        image.put("rawPath", rawPath);
                        image.put("uri", serie.getImageList().get(i).toString());
                        image.put("filename", rawPath.substring(rawPath.lastIndexOf("/") + 1, rawPath.length()));

                        _sopInstanceUID.add(image);
                    }
                    _serie.put("images", _sopInstanceUID);

                    series.add(_serie);
                }
                study.put("modalities", StringUtils.join(modalitiesSet, ","));
                study.put("series", series);

                studies.add(study);

            }
            patient.put("studies", studies);

            patients.add(patient);
        }

        result.put("results", patients);

        return result.toString();
    }

    public String getXML() {

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

            for (Patient p : this.patients) {
                atts.clear();
                atts.addAttribute("", "", "name", "", p.getPatientName().trim());
                atts.addAttribute("", "", "id", "", p.getPatientID().trim());
                hd.startElement("", "", "Patient", atts);

                for (Study s : p.getStudies()) {
                    atts.clear();
                    atts.addAttribute("", "", "date", "", s.getStudyData().trim());
                    atts.addAttribute("", "", "id", "", s.getStudyInstanceUID().trim());

                    hd.startElement("", "", "Study", atts);

                    for (Series series : s.getSeries()) {
                        atts.clear();
                        atts.addAttribute("", "", "modality", "", series.getModality().trim());
                        atts.addAttribute("", "", "id", "", series.getSeriesInstanceUID().trim());

                        hd.startElement("", "", "Series", atts);

                        ArrayList<URI> img = series.getImageList();
                        ArrayList<String> uid = series.getSOPInstanceUIDList();
                        int size = img.size();
                        for (int i = 0; i < size; i++) {
                            atts.clear();
                            atts.addAttribute("", "", "path", "", img.get(i).toString().trim());
                            atts.addAttribute("", "", "uid", "", uid.get(i).trim());

                            hd.startElement("", "", "Image", atts);
                            hd.endElement("", "", "Image");
                        }
                        hd.endElement("", "", "Series");
                    }
                    hd.endElement("", "", "Study");
                }
                hd.endElement("", "", "Patient");
            }
            hd.endElement("", "", "DIM");

        } catch (SAXException ex) {
            LoggerFactory.getLogger(DIMGeneric.class.getName()).error(ex.getMessage(), ex);
        }

        return writer.toString();
    }

    /**
     * @return the patients
     */
    public ArrayList<Patient> getPatients() {
        return patients;
    }
}
