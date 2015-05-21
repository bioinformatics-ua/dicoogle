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
 * A feature specification with a dimensional range attached
 * @author Eduardo Pinho
 */
public class RangedFeatureSpecification extends FeatureSpecification {
    
    public Interval[] featureRange;
    
    /*
     * gets a feature (which may be multidimensional) and for each of the
     * feature's dimensions it gets an element in the range array
     */
    public RangedFeatureSpecification(FeatureSpecification spec, Interval[] ranges){
        super(spec.getName(), spec.getType(), spec.getDimensionality());
        this.featureRange = ranges;
    }
    
    /*
     * makes a copy of a rangedfeature
     */
    public RangedFeatureSpecification(RangedFeatureSpecification range){
        super(range.getName(), range.getType(), range.getDimensionality());
        this.featureRange = range.featureRange.clone();
    }

    @Override
    public String toString() {
        return "RangedFeatureSpecification{"
                + "name=" + this.getName() + ",type=" + this.getType()
                + ",dimensionality=" + this.getDimensionality()
                + ",featureRange=" + Arrays.toString(featureRange) + '}';
    }

    public Interval[] getIntervals() {
        return this.featureRange.clone();
    }
}
