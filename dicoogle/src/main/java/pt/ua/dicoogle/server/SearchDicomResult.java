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
/** 
 * Class SearchResult is responsible to get results from Lucene indexer
 * But no text results are returned instead we use names of DICOM file and 
 * instance a new DicomObject, so in theory this is an abstract to a list of 
 * DicomObject, neither list of *names* of DICOM file.
 */
package pt.ua.dicoogle.server;

import pt.ua.dicoogle.sdk.datastructs.dim.*;
import pt.ua.dicoogle.sdk.datastructs.dim.Series;

import java.io.*;

import org.dcm4che3.data.*;
import org.slf4j.Logger;
import pt.ua.dicoogle.core.settings.ServerSettingsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.LoggerFactory;

import org.dcm4che3.io.DicomInputStream;


import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 * @since 17 Fev 2009
 */
public class SearchDicomResult implements Iterator<Attributes> {

    private static final Logger LOG = LoggerFactory.getLogger(SearchDicomResult.class);

    public enum QUERYLEVEL {
        PATIENT, STUDY, SERIE, IMAGE
    }

    private final QUERYLEVEL queryLevel;


    /**
    * Get IndexCore
    */

    Collection<SearchResult> list = null;
    List<Patient> patientList = new ArrayList<>();
    List<Study> studyList = new ArrayList<>();
    List<Series> seriesList = new ArrayList<>();
    Iterator it = null;

    String currentFile;



    private static ConcatTags concatTags = null;
    private static boolean concatTagsCheck = true;


    private static final Logger logger = LoggerFactory.getLogger(SearchDicomResult.class);

    public SearchDicomResult(String searchQuery, boolean isNetwork, ArrayList<String> extrafields, QUERYLEVEL level) {

        queryLevel = level;

        /**
         * Get the array list of resulst match searchQuery
         */

        logger.info("QUERY: " + searchQuery);
        logger.info("QUERYLEVEL: " + queryLevel);

        if (concatTags == null && concatTagsCheck) {
            concatTags = new ConcatTags();
            try {
                concatTags.parseConfig(ConcatTags.FILENAME);
            } catch (FileNotFoundException ex) {
                logger.info("There is not file: " + ConcatTags.FILENAME);
                concatTags = null;
                concatTagsCheck = false;
            }
        }

        HashMap<String, String> extraFields = new HashMap<String, String>();
        for (String s : extrafields) {
            extraFields.put(s, s);
        }

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

        // The method will retrieve all DICOM active query plugins (with empty list at argument)
        List<String> dicomQueryProviders = PluginController.getInstance().filterDicomQueryProviders(new ArrayList<>());
        holder = PluginController.getInstance().query(holder, dicomQueryProviders, searchQuery, extraFields);

        try {
            it = holder.get().iterator();

            list = new ArrayList<SearchResult>();
            while (it.hasNext()) {
                list.add((SearchResult) it.next());
            }
        } catch (InterruptedException e1) {
            LOG.error("Failed to retrieve DICOM search results", e1);
        } catch (ExecutionException e1) {
            LOG.error("Failed to retrieve DICOM search results", e1.getCause());
        }

        if (list != null)
            it = list.iterator();

        if (level == QUERYLEVEL.PATIENT || level == QUERYLEVEL.STUDY) {
            DIMGeneric dimModel = null;
            try {
                if (concatTags == null)
                    dimModel = new DIMGeneric(list);
                else
                    dimModel = new DIMGeneric(concatTags, list);

            } catch (Exception ex) {
                LOG.error("Failed to create DIM tree", ex);
            }

            ArrayList<Patient> listPatients = dimModel.getPatients();

            for (Patient p : listPatients) {
                studyList.addAll(p.getStudies());
            }

            it = studyList.iterator();

        } else if (level == QUERYLEVEL.SERIE) {

            DIMGeneric dimModel = null;
            try {
                if (concatTags == null)
                    dimModel = new DIMGeneric(list);
                else
                    dimModel = new DIMGeneric(concatTags, list);
            } catch (Exception ex) {}


            ArrayList<Patient> listPatients = dimModel.getPatients();
            for (Patient p : listPatients) {
                studyList.addAll(p.getStudies());
                for (Study s : p.getStudies()) {
                    seriesList.addAll(s.getSeries());
                }
            }
            it = seriesList.iterator();

        }

    }

    @Override
    public boolean hasNext() {
        if (it != null) {
            return it.hasNext();
        } else {
            return false;
        }
    }

    public String getCurrentFile() {
        return this.currentFile;
    }

    @Override
    public Attributes next() {

        String path = ServerSettingsManager.getSettings().getArchiveSettings().getMainDirectory();

        // DebugManager.getSettings().debug("Path of DICOM: "+path);


        if (it != null && it.hasNext()) {
            Object next = it.next();
            if (queryLevel == QUERYLEVEL.IMAGE) {

                SearchResult sR = (SearchResult) next;

                path = sR.getURI().toString();
                currentFile = path;

                Attributes result = new Attributes();

                // Fill fields of study now
                result.setString(Tag.InstitutionName, VR.CS, (String) sR.get("InstitutionName"));

                result.setString(Tag.StudyInstanceUID, VR.UI, (String) sR.get("StudyInstanceUID"));
                result.setString(Tag.SeriesInstanceUID, VR.UI, (String) sR.get("SeriesInstanceUID"));
                result.setString(Tag.SOPInstanceUID, VR.UI, (String) sR.get("SOPInstanceUID"));
                result.setString(Tag.SeriesDescription, VR.LO, (String) sR.get("SeriesDescription"));
                result.setString(Tag.SeriesDate, VR.TM, (String) sR.get("SeriesDate"));
                result.setString(Tag.SeriesTime, VR.TM, (String) sR.get("SeriesTime"));
                result.setString(Tag.QueryRetrieveLevel, VR.LO, "IMAGE");

                result.setString(Tag.Modality, VR.CS, (String) sR.get("Modality"));

                String seriesNumber = String.valueOf(sR.get("SeriesNumber"));
                if (seriesNumber.endsWith(".0")) {
                    seriesNumber = seriesNumber.substring(0, seriesNumber.length() - 2);
                }
                result.setString(Tag.SeriesNumber, VR.IS, seriesNumber);


                return result;

            } else if (queryLevel == QUERYLEVEL.STUDY || queryLevel == QUERYLEVEL.PATIENT) {

                Study studyTmp = (Study) next;
                Attributes result = new Attributes();
                String patientName = studyTmp.getParent().getPatientName();

                try {
                    patientName = studyTmp.getParent().getPatientName();
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
                try {
                    result.setString(Tag.PatientName, VR.PN, patientName);
                } catch (Exception ex) {
                    LoggerFactory.getLogger(SearchDicomResult.class).error(ex.getMessage(), ex);
                }

                result.setString(Tag.PatientSex, VR.LO, studyTmp.getParent().getPatientSex());
                result.setString(Tag.PatientID, VR.LO, studyTmp.getParent().getPatientID());
                result.setString(Tag.PatientBirthDate, VR.DA, studyTmp.getParent().getPatientBirthDate());
                result.setString(Tag.StudyDate, VR.DA, studyTmp.getStudyData());
                result.setString(Tag.StudyID, VR.SH, studyTmp.getStudyID());
                result.setString(Tag.StudyTime, VR.TM, studyTmp.getStudyTime());
                result.setString(Tag.AccessionNumber, VR.SH, studyTmp.getAccessionNumber());
                result.setString(Tag.StudyInstanceUID, VR.UI, studyTmp.getStudyInstanceUID());
                result.setString(Tag.StudyDescription, VR.LO, studyTmp.getStudyDescription());
                String modality = studyTmp.getSeries().get(0).getModality(); // Point of Failure, fix me
                result.setString(Tag.ModalitiesInStudy, VR.CS, modality);
                result.setString(Tag.Modality, VR.CS, modality);
                result.setString(Tag.InstitutionName, VR.CS, studyTmp.getInstitutuionName());

                int instances = 0;
                for (Series seriesTmp : studyTmp.getSeries()) {
                    instances += seriesTmp.getImageList().size();
                }

                result.setString(Tag.NumberOfStudyRelatedInstances, VR.IS, "" + instances);
                result.setString(Tag.NumberOfSeriesRelatedInstances, VR.IS, "" + studyTmp.getSeries().size());


                return result;

            } else if (queryLevel == QUERYLEVEL.SERIE) {
                // Series

                Series seriesTmp = (Series) next;
                Attributes result = new Attributes();
                // System.out.println("Series : "+ seriesTmp);
                result.setString(Tag.InstitutionName, VR.CS, seriesTmp.getParent().getInstitutuionName());

                result.setString(Tag.StudyInstanceUID, VR.UI, seriesTmp.getParent().getStudyInstanceUID());
                result.setString(Tag.SeriesInstanceUID, VR.UI, seriesTmp.getSeriesInstanceUID());
                result.setString(Tag.SeriesDescription, VR.LO, seriesTmp.getSeriesDescription());
                result.setString(Tag.SeriesDate, VR.TM, seriesTmp.getSeriesDate());
                result.setString(Tag.QueryRetrieveLevel, VR.LO, "SERIES");
                String modality = seriesTmp.getModality(); // Point of Failure, fix me
                result.setString(Tag.Modality, VR.CS, modality);

                result.setString(Tag.SeriesNumber, VR.IS, "" + seriesTmp.getSeriesNumber());


                result.setString(Tag.Modality, VR.CS, modality);
                if (seriesTmp.getModality().equals("MG") || seriesTmp.getModality().equals("CR")) {

                    result.setString(Tag.ViewPosition, null, seriesTmp.getViewPosition());
                    result.setString(Tag.ImageLaterality, null, seriesTmp.getImageLaterality());
                    result.setString(Tag.AcquisitionDeviceProcessingDescription, VR.AE,
                            seriesTmp.getAcquisitionDeviceProcessingDescription());
                    Sequence viewCodeSequence = result.newSequence(Tag.ViewCodeSequence, 1);
                    Attributes viewCodeSequenceObj = new Attributes();
                    viewCodeSequenceObj.setString(Tag.CodeValue, null, seriesTmp.getViewCodeSequence_CodeValue());
                    viewCodeSequenceObj.setString(Tag.CodingSchemeDesignator, null,
                            seriesTmp.getViewCodeSequence_CodingSchemeDesignator());
                    viewCodeSequenceObj.setString(Tag.CodingSchemeVersion, null,
                            seriesTmp.getViewCodeSequence_CodingSchemeVersion());
                    viewCodeSequenceObj.setString(Tag.CodeMeaning, null, seriesTmp.getViewCodeSequence_CodeMeaning());
                    viewCodeSequence.add(viewCodeSequenceObj);
                }
                result.setString(Tag.NumberOfSeriesRelatedInstances, VR.IS, String.valueOf(seriesTmp.getImageList().size()));

                result.setString(Tag.SeriesNumber, VR.IS, String.valueOf(seriesTmp.getSeriesNumber()));
                result.setString(Tag.ProtocolName, VR.LO, seriesTmp.getProtocolName());
                result.setString(Tag.BodyPartThickness, VR.DS, String.valueOf(seriesTmp.getBodyPartThickness()));

                return result;

            } else {
                logger.warn("Wrong query level {}", queryLevel);
            }

        }
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported. Nobody use it.");
    }
}
