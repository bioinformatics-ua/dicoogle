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

/**
 * This class is a simple implementation of Series that will be returned in DIM.
 *
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 */
public class Series implements SeriesInterface
{

    private Study parent;
    private String SeriesInstanceUID ;
    private String SeriesDescription;

    private int SeriesNumber ;
    private String Modality ;
    private String SeriesDate = "";
    private String ProtocolName = "";

    private String BodyPartThickness = "";
    private String patientOrientation = "";
    private String compressionForce = "";
    // Only for MG
    private String ViewCodeSequence = "";
    private String ViewCodeSequence_CodeValue = "";
    private String ViewCodeSequence_CodingSchemeDesignator = "";
    private String ViewCodeSequence_CodingSchemeVersion = "";
    private String ViewCodeSequence_CodeMeaning = "";

    private String ViewPosition = "";
    private String ImageLaterality = "";
    private String AcquisitionDeviceProcessingDescription = "";
    
    
    private ArrayList<URI> imageList = new ArrayList<>();
    private ArrayList<String> UIDList = new ArrayList<>();

    public Series(Study study, String SeriesInstanceUID, String modality)
    {
        this.parent = study;
        this.Modality = modality;
        this.SeriesInstanceUID = SeriesInstanceUID ;
        
    }

    public Series(Study study, String SeriesInstanceUID, int SeriesNumber)
    {
        this.parent = study;
        this.SeriesInstanceUID = SeriesInstanceUID ;
        this.SeriesNumber = SeriesNumber ;
        
    }


    public void addImage(URI ImagePath, String sopUid){
        this.imageList.add(ImagePath);
        this.UIDList.add(sopUid);
    }

    public void removeImage(URI imagePath){
        this.imageList.remove(imagePath);
    }


    /**
     * @return the SerieInstanceUID
     */
    public String getSeriesInstanceUID(){
        return SeriesInstanceUID;
    }

    /**
     * @param SeriesInstanceUID the SerieInstanceUID to set
     */
    public void setSeriesInstanceUID(String SeriesInstanceUID) {
        this.SeriesInstanceUID = SeriesInstanceUID;
    }

    /**
     * @return the SeriesNumber
     */
    public int getSeriesNumber() {
        return SeriesNumber;
    }

    /**
     * @param SeriesNumber the SerieNumber to set
     */
    public void setSeriesNumber(int SeriesNumber) {
        this.SeriesNumber = SeriesNumber;
    }

    /**
     * @return the imageList
     */
    public ArrayList<URI> getImageList() {
        return imageList;
    }

    public ArrayList<String> getSOPInstanceUIDList(){
        return UIDList;
    }

    /**
     * @param imageList the imageList to set
     */
    public void setImageList(ArrayList<URI> imageList, ArrayList<String> sops) {
        this.imageList = imageList;
        this.UIDList = sops;
    }

    /**
     * @return the Modality
     */
    public String getModality() {
        return Modality;
    }

    /**
     * @param Modality the Modality to set
     */
    public void setModality(String Modality) {
        this.Modality = Modality;
    }

    /**
     * @return the parent
     */
    public Study getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Study parent) {
        this.parent = parent;
    }

    /**
     * @return the SeriesDescription
     */
    public String getSeriesDescription() {
        return SeriesDescription;
    }

    /**
     * @param SeriesDescription the SeriesDescription to set
     */
    public void setSeriesDescription(String SeriesDescription) {
        this.SeriesDescription = SeriesDescription;
    }


    /**
     * @return the SeriesDate
     */
    public String getSeriesDate() {
        return SeriesDate;
    }

    /**
     * @param SeriesDate the SeriesDate to set
     */
    public void setSeriesDate(String SeriesDate) {
        this.SeriesDate = SeriesDate;
    }



    /**
     * @return the ProtocolName
     */
    public String getProtocolName() {
        return ProtocolName;
    }

    /**
     * @param ProtocolName the ProtocolName to set
     */
    public void setProtocolName(String ProtocolName) {
        this.ProtocolName = ProtocolName;
    }

    /**
     * @return the BodyPartThickness
     */
    public String getBodyPartThickness() {
        return BodyPartThickness;
    }

    /**
     * @param BodyPartThickness the BodyPartThickness to set
     */
    public void setBodyPartThickness(String BodyPartThickness) {
        this.BodyPartThickness = BodyPartThickness;
    }


    /**
     * @return the patientOrientation
     */
    public String getPatientOrientation() {
        return patientOrientation;
    }

    /**
     * @param patientOrientation the patientOrientation to set
     */
    public void setPatientOrientation(String patientOrientation) {
        this.patientOrientation = patientOrientation;
    }

    /**
     * @return the compressionForce
     */
    public String getCompressionForce() {
        return compressionForce;
    }

    /**
     * @param compressionForce the compressionForce to set
     */
    public void setCompressionForce(String compressionForce) {
        this.compressionForce = compressionForce;
    }

    /**
     * @return the ViewCodeSequence
     */
    public String getViewCodeSequence() {
        return ViewCodeSequence;
    }

    /**
     * @param ViewCodeSequence the ViewCodeSequence to set
     */
    public void setViewCodeSequence(String ViewCodeSequence) {
        this.ViewCodeSequence = ViewCodeSequence;
    }

    /**
     * @return the ViewCodeSequence_CodeValue
     */
    public String getViewCodeSequence_CodeValue() {
        return ViewCodeSequence_CodeValue;
    }

    /**
     * @param ViewCodeSequence_CodeValue the ViewCodeSequence_CodeValue to set
     */
    public void setViewCodeSequence_CodeValue(String ViewCodeSequence_CodeValue) {
        this.ViewCodeSequence_CodeValue = ViewCodeSequence_CodeValue;
    }

    /**
     * @return the ViewCodeSequence_CodingSchemeDesignator
     */
    public String getViewCodeSequence_CodingSchemeDesignator() {
        return ViewCodeSequence_CodingSchemeDesignator;
    }

    /**
     * @param ViewCodeSequence_CodingSchemeDesignator the ViewCodeSequence_CodingSchemeDesignator to set
     */
    public void setViewCodeSequence_CodingSchemeDesignator(String ViewCodeSequence_CodingSchemeDesignator) {
        this.ViewCodeSequence_CodingSchemeDesignator = ViewCodeSequence_CodingSchemeDesignator;
    }

    /**
     * @return the ViewCodeSequence_CodingSchemeVersion
     */
    public String getViewCodeSequence_CodingSchemeVersion() {
        return ViewCodeSequence_CodingSchemeVersion;
    }

    /**
     * @param ViewCodeSequence_CodingSchemeVersion the ViewCodeSequence_CodingSchemeVersion to set
     */
    public void setViewCodeSequence_CodingSchemeVersion(String ViewCodeSequence_CodingSchemeVersion) {
        this.ViewCodeSequence_CodingSchemeVersion = ViewCodeSequence_CodingSchemeVersion;
    }

    /**
     * @return the ViewCodeSequence_CodeMeaning
     */
    public String getViewCodeSequence_CodeMeaning() {
        return ViewCodeSequence_CodeMeaning;
    }

    /**
     * @param ViewCodeSequence_CodeMeaning the ViewCodeSequence_CodeMeaning to set
     */
    public void setViewCodeSequence_CodeMeaning(String ViewCodeSequence_CodeMeaning) {
        this.ViewCodeSequence_CodeMeaning = ViewCodeSequence_CodeMeaning;
    }

    /**
     * @return the ViewPosition
     */
    public String getViewPosition() {
        return ViewPosition;
    }

    /**
     * @param ViewPosition the ViewPosition to set
     */
    public void setViewPosition(String ViewPosition) {
        this.ViewPosition = ViewPosition;
    }

    /**
     * @return the ImageLaterality
     */
    public String getImageLaterality() {
        return ImageLaterality;
    }

    /**
     * @param ImageLaterality the ImageLaterality to set
     */
    public void setImageLaterality(String ImageLaterality) {
        this.ImageLaterality = ImageLaterality;
    }

    /**
     * @return the AcquisitionDeviceProcessingDescription
     */
    public String getAcquisitionDeviceProcessingDescription() {
        return AcquisitionDeviceProcessingDescription;
    }

    /**
     * @param AcquisitionDeviceProcessingDescription the AcquisitionDeviceProcessingDescription to set
     */
    public void setAcquisitionDeviceProcessingDescription(String AcquisitionDeviceProcessingDescription) {
        this.AcquisitionDeviceProcessingDescription = AcquisitionDeviceProcessingDescription;
    }
    
    public String toString()
    {
    
        
        String result = "";
        result += "SeriesInstanceUID:" + SeriesInstanceUID + "\n";
        result += "SeriesDescription:" + SeriesDescription + "\n";
        result += "SeriesNumber:" + SeriesNumber + "\n";
        result += "Modality:" + Modality + "\n";
        return result;
        
    }


    @Override
    public StudyInterface getStudy() {
        return this.parent;
    }
}
