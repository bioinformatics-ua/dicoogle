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

import java.net.URI;
import java.util.ArrayList;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class Serie
{

    private Study parent;
    private String SerieInstanceUID ;
    private String SeriesDescription;
    
    private int SerieNumber ;
    private String Modality ; 

    private ArrayList<URI> imageList = new ArrayList<>();
    private ArrayList<String> UIDList = new ArrayList<>();

    public Serie(Study study, String SerieInstanceUID, String modality)
    {
        this.parent = study;
        this.Modality = modality;
        this.SerieInstanceUID = SerieInstanceUID ; 
        
    }

    public Serie(Study study, String SerieInstanceUID, int SerieNumber)
    {
        this.parent = study;
        this.SerieInstanceUID = SerieInstanceUID ;
        this.SerieNumber = SerieNumber ; 
        
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
    public String getSerieInstanceUID(){
        return SerieInstanceUID;
    }

    /**
     * @param SerieInstanceUID the SerieInstanceUID to set
     */
    public void setSerieInstanceUID(String SerieInstanceUID) {
        this.SerieInstanceUID = SerieInstanceUID;
    }

    /**
     * @return the SerieNumber
     */
    public int getSerieNumber() {
        return SerieNumber;
    }

    /**
     * @param SerieNumber the SerieNumber to set
     */
    public void setSerieNumber(int SerieNumber) {
        this.SerieNumber = SerieNumber;
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

    
    
    public String toString()
    {
    
        
        String result = "";
        result += "SeriesInstanceUID:" + SerieInstanceUID + "\n";
        result += "SeriesDescription:" + SeriesDescription + "\n";
        result += "SerieNumber:" + SerieNumber + "\n";
        result += "Modality:" + Modality + "\n";
        return result;
        
    }
    

}
