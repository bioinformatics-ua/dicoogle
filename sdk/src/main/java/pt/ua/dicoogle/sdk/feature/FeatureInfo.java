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

package pt.ua.dicoogle.sdk.feature;

/**
 *
 * @author fmvalente
 * 
 * Stores information concerning a feature
 * provides some conveniency methods (like none whatsoever, except a constructor)
 * 
 * 
 * TODO: use an update variable integer so that we may know if we are synchronized with
 * the index, or we are already out of date...
 * 
 */
public class FeatureInfo {
    public String featureName;
    public FeatureInfoElement[] elements;
    
    public FeatureInfo(String featureName, FeatureInfoElement[] elements){
        this.featureName = featureName;
        this.elements = elements;        
    }
    
    public int dimensionality(){return elements.length;}
    
    @Override public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("\t"+featureName).append("(").append(dimensionality()).append(")\n");
        
        for(FeatureInfoElement elem : elements)
            if(elem != null) str.append(elem.toString());
            else System.err.println("null featinfo element!");
        
        return str.toString();
    }
    
}
