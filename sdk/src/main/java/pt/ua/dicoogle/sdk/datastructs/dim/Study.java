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

import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class is a simple implementation of Study that will be returned in DIM.
 *
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 */
public class Study implements StudyInterface {

    private Patient parent;
    private String StudyInstanceUID;
    private String StudyID;
    private String StudyData;
    private String StudyTime;
    private String AccessionNumber;
    private String StudyDescription;
    private String InstitutuionName;

    private String operatorsName;
    private String RequestingPhysician;

    // Look in the cases where there same patient identifier with different
    // patient names
    private String patientName;


    private ArrayList<Series> series = new ArrayList<Series>();
    private Hashtable<String, Series> seriesHash = new Hashtable<String, Series>();

    public Study(Patient patient, String StudyInstanceUID, String StudyDate) {
        this.parent = patient;
        this.StudyInstanceUID = StudyInstanceUID;
        this.StudyData = StudyDate;

    }


    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }


    /**
     * @return the StudyInstanceUID
     */
    public String getStudyInstanceUID() {
        return StudyInstanceUID;
    }

    /**
     * @param StudyInstanceUID the StudyInstanceUID to set
     */
    public void setStudyInstanceUID(String StudyInstanceUID) {
        this.StudyInstanceUID = StudyInstanceUID;
    }

    /**
     * @return the StudyData
     */
    public String getStudyData() {
        return StudyData;
    }

    /**
     * @param StudyData the StudyData to set
     */
    public void setStudyData(String StudyData) {
        this.StudyData = StudyData;
    }


    /** Add or extend a DICOM series into this study.
     * 
     * @param s a series object
     */
    public void addSerie(Series s) {
        if (this.seriesHash.containsKey(s.getSeriesInstanceUID())) {

            Series existSeries = this.seriesHash.get(s.getSeriesInstanceUID());
            ArrayList<URI> img = s.getImageList();
            ArrayList<String> uid = s.getSOPInstanceUIDList();
            ArrayList<String> instanceNum = s.getInstanceNumberList();

            int size = img.size();
            for (int i = 0; i < size; i++) {
                existSeries.addImage(img.get(i), uid.get(i), instanceNum.get(i));
            }
        } else {
            this.series.add(s);
            this.seriesHash.put(s.getSeriesInstanceUID(), s);
        }

    }

    /**
     * @return the series
     */
    public ArrayList<Series> getSeries() {
        return series;
    }

    public Series getSeries(String seriesInstanceUID) {
        return this.seriesHash.get(seriesInstanceUID);
    }

    /**
     * @param series the series to set
     */
    public void setSeries(ArrayList<Series> series) {
        this.series = series;
    }

    /**
     * @return the parent
     */
    public Patient getParent() {
        return parent;
    }

    /**
     * @return the StudyTime
     */
    public String getStudyTime() {
        return StudyTime;
    }

    /**
     * @param StudyTime the StudyTime to set
     */
    public void setStudyTime(String StudyTime) {
        this.StudyTime = StudyTime;
    }

    /**
     * @return the AccessionNumber
     */
    public String getAccessionNumber() {
        return AccessionNumber;
    }

    /**
     * @param AccessionNumber the AccessionNumber to set
     */
    public void setAccessionNumber(String AccessionNumber) {
        this.AccessionNumber = AccessionNumber;
    }

    /**
     * @return the StudyDescription
     */
    public String getStudyDescription() {
        return StudyDescription;
    }

    /**
     * @param StudyDescription the StudyDescription to set
     */
    public void setStudyDescription(String StudyDescription) {
        this.StudyDescription = StudyDescription;
    }

    /**
     * @return the StudyID
     */
    public String getStudyID() {
        return StudyID;
    }

    /**
     * @param StudyID the StudyID to set
     */
    public void setStudyID(String StudyID) {
        this.StudyID = StudyID;
    }

    /**
     * @return the InstitutuionName
     */
    public String getInstitutuionName() {
        return InstitutuionName;
    }

    /**
     * @param InstitutuionName the InstitutuionName to set
     */
    public void setInstitutuionName(String InstitutuionName) {
        this.InstitutuionName = InstitutuionName;
    }


    /**
     * @return the operatorsName
     */
    public String getOperatorsName() {
        return operatorsName;
    }

    /**
     * @param operatorsName the operatorsName to set
     */
    public void setOperatorsName(String operatorsName) {
        this.operatorsName = operatorsName;
    }

    /**
     * @return the RequestingPhysician
     */
    public String getRequestingPhysician() {
        return RequestingPhysician;
    }

    /**
     * @param RequestingPhysician the RequestingPhysician to set
     */
    public void setRequestingPhysician(String RequestingPhysician) {
        this.RequestingPhysician = RequestingPhysician;
    }


    @Override
    public PatientInterface getPatient() {
        return this.parent;
    }
}
