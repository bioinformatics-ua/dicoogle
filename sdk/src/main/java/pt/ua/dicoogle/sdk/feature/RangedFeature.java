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

import java.util.Arrays;

/**
 *  A feature with a dimensional range attached
 * @author fmvalente
 */
public class RangedFeature extends Feature {

    public Interval[] featureRange;
    
    /*
     * gets a feature (which may be multidimensional) and for each of the
     * feature's dimensions it gets an element in the range array
     */
    public RangedFeature(Feature feat, Interval[] ranges){
        super(feat);
        this.featureRange = ranges;
    }
    
    /*
     * makes a copy of a rangedfeature
     */
    public RangedFeature(RangedFeature range){
        super(range);
        this.featureRange = Arrays.copyOf(range.featureRange, range.featureRange.length);
    }
    
    /*
     * given a rangedfeature centered at the origin and a feature point
     * this returns a new rangedfeature with the dimensions of the centered rfeat argument
     * but placed at the featureset point
     * 
     * TODO: handle doubles, ints, strings...
     */
    public RangedFeature(RangedFeature centered, Feature point){
        super(centered);
        this.featureRange = Arrays.copyOf(centered.featureRange, centered.featureRange.length);
        
        for(int i=0; i<featureRange.length; i++){
            float p = point.floatValue(i);
            
            this.featureRange[i].minimumRangeValue += p;
            this.featureRange[i].maximumRangeValue += p;
            
        }
    }
    
    @Override
    public String toOutputString(){
        StringBuilder str = new StringBuilder("RangedFeature of "+getName()+"\n");
        
        for(Interval interval : featureRange){
            str.append(getName()).append(" ").append(interval.minimumRangeValue).append(" ");
            str.append(interval.maximumRangeValue).append("\n");
        }
        str.append(super.toOutputString());

        return str.toString();
    }

    public Interval[] getIntervals() {
        return this.featureRange.clone();
    }
}
