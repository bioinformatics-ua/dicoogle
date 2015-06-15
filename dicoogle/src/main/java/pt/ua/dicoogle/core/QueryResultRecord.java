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

package pt.ua.dicoogle.core;

/**
 *
 * @author carloscosta
 * @MarkedForDeath currently unused
 */
public class QueryResultRecord {
    private String patientName;
    private String PatientID;
    private String Modality;
    private String StudyDate;
    private String FilePath;
    private byte[] Thumbnail;

    public QueryResultRecord(String patientName, String PatientID, 
                             String Modality, String StudyDate, 
                             String FilePath, byte[] Thumbnail){
        this.patientName = patientName;
        this.PatientID = PatientID;
        this.Modality = Modality;
        this.StudyDate = StudyDate;
        this.FilePath = FilePath;
        this.Thumbnail = Thumbnail;
    }
    
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientID() {
        return PatientID;
    }

    public void setPatientID(String PatientID) {
        this.PatientID = PatientID;
    }

    public String getModality() {
        return Modality;
    }

    public void setModality(String Modality) {
        this.Modality = Modality;
    }

    public String getStudyDate() {
        return StudyDate;
    }

    public void setStudyDate(String StudyDate) {
        this.StudyDate = StudyDate;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String FilePath) {
        this.FilePath = FilePath;
    }

    public byte[] getThumbnail() {
        return Thumbnail;
    }
        
}
