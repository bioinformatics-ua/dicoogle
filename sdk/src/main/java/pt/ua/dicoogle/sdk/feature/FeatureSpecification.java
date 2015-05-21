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

/** A class for specifying the characteristics of a feature.
 *
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class FeatureSpecification {
    private final String name;
    private final FeatureType type;
    private final int dimensionality;

    public FeatureSpecification(String name, FeatureType type, int dimensionality) {
        assert name != null;
        assert type != null;
        assert dimensionality > 0;
        
        this.name = name;
        this.type = type;
        this.dimensionality = dimensionality;
    }
    public FeatureSpecification(String name, FeatureType type) {
        this(name, type, 1);
    }

    public String getName() {
        return name;
    }

    public FeatureType getType() {
        return type;
    }

    public int getDimensionality() {
        return dimensionality;
    }
    
    public boolean isCompliant(Feature feature) {
        return this.name.equals(feature.getName())
                && this.type == feature.getType()
                && this.dimensionality == feature.getDimensionality();
    }
}
