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
 *  Information about a single dimension of a feature
 * @author fmvalente
 */
public class FeatureInfoElement {
    
    public boolean isInitialized;
    public String featureName; //the feature name
    public String implementationName;//the name under which the feature is stored
    
    //statistic variables for the feature dimension
    public float minimum;//minimum and maximum values of the feature (if appliable)
    public float maximum;
    public float sum;
    public float sumsqr;//to help calculation of std
    public double average; //average, when appliable
    public double std; //standard deviation, when appliable
    public int count; //number of documents having this feature
    
    public double Q,M;
    public int K;
    
    public FeatureInfoElement(){
        isInitialized = false;
        featureName = "unnamed";
        implementationName = "unnamed";
        
        minimum = Float.MAX_VALUE;
        maximum = Float.MIN_VALUE;
        sum = 0;
        sumsqr = 0;
        std = 0;
        average = 0;
        count = 0;
        
        Q = 0.0;
        M = 0.0;
        K = 0;
    }
    
    @Override public String toString(){
//        StringBuilder str = new StringBuilder();
//        str.append("\t").append(featureName).append("(").append(implementationName).append(")\n");
//        str.append("\tmin,max,avg,std: (").append(minimum).append(", ").append(maximum).append(", ");
//        str.append(average).append(", ").append(std).append("\n");
        return String.format("\t%s(%s)\n\tmin,max,avg,std: (%f, %f, %f, %f)\n"
                , featureName, implementationName, minimum, maximum, average, std);
    }
}
