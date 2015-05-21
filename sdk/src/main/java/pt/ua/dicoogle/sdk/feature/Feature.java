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
 * A simple class that names a feature and stores a values
 It serves to mediate data between feature extraction (by IndexTask)
 and feature storage by the FeatureDBManager
 * 
 * @author fmvalente
 * @author Eduardo Pinho
 */
public class Feature {

    public String getName() {return name;}
    public FeatureType getType() {return type;}
    public int getDimensionality() {return dimensionality;}
    
    private final String name;
    private final FeatureType type;
    private final int dimensionality;
    private final Object values;
    
    private Feature(String name, FeatureType type, int dimensionality, Object values) {
        this.name = name;
        this.type = type;
        this.dimensionality = dimensionality;
        this.values = values;
    }
    
    public static Feature createUnknown(String name, Object value) {return new Feature(name, FeatureType.TUnknown, 1, value);}
    public boolean isUnknownType() {return this.type == FeatureType.TUnknown;}
    
    public Feature(String featureName, int val){
        this.name = featureName;
        values = new int[1];
        ((int[])values)[0] = val;
        type = FeatureType.TInt;
        dimensionality = 1;
    }
    
    public Feature(String featureName, float val){
        this.name = featureName; 
        values = new float[1];
        ((float[])values)[0] = val;
        type = FeatureType.TFloat;
        dimensionality = 1;
    }
    public Feature(String featureName, double val){
        this.name = featureName;
        values = new double[1];
        ((double[])values)[0] = val;
        type = FeatureType.TDouble;
        dimensionality = 1;
    }
    public Feature(String featureName, String val){
        this.name = featureName;
        values = new String[1];
        ((String[])values)[0] = val;
        type = FeatureType.TString;
        dimensionality = 1;
    }
    
    //val must be an array, we take control of it!
    public Feature(String featureName, int[] val){
        this.name = featureName;
        this.type = FeatureType.TInt;
        this.values = val;
        dimensionality = val.length;
    }

    public Feature(String featureName, float[] val){
        this.name = featureName;
        this.type = FeatureType.TFloat;
        this.values = val;
        dimensionality = val.length;
    }
    
    public Feature(String featureName, double[] val){
        this.name = featureName;
        this.type = FeatureType.TDouble;
        this.values = val;
        dimensionality = val.length;
    }

    public Feature(String featureName, String[] val){
        this.name = featureName;
        this.type = FeatureType.TString;
        this.values = val;
        dimensionality = val.length;
    }
    
    //data is shared, there is no deep copy
    public Feature(Feature feat){
        this.name = feat.name;
        this.type = feat.type;
        this.values = feat.values;
        this.dimensionality = feat.dimensionality;
    }
        
    @Override
    public String toString(){
        String val;
        if (values == null) return "null value (uninitialized)";
        else if (type == FeatureType.TString) val = Arrays.toString((String[])values);
        else if (type == FeatureType.TInt) val = Arrays.toString(((int[])values));
        else if (type == FeatureType.TFloat) val = Arrays.toString(((float[])values));
        else if (type == FeatureType.TDouble) val = Arrays.toString(((double[])values));        
        else val = values.toString();
        
        return String.format("Feature[%s,%s,%s]",this.name, this.type, val);
    }
    
    public String toCsvString(){
        StringBuilder strb = new StringBuilder();
        if (values == null) return "undef";
        /*else if (type == FeatureType.TString) 
            for (int i=0;i<dimensionality;++i){
                strb.append(((String[])values)[i]);
                strb.append(", ");
            }*/ 
        else if (type == FeatureType.TInt) 
            for (int i=0;i<dimensionality;++i){
                strb.append(((int[])values)[i]);
                strb.append(",\t ");
            }
        else if (type == FeatureType.TFloat) 
            for (int i=0;i<dimensionality;++i){
                strb.append(((float[])values)[i]);
                strb.append(",\t ");
            }
        else if (type == FeatureType.TDouble) 
            for (int i=0;i<dimensionality;++i){
                strb.append(((double[])values)[i]);
                strb.append(",\t ");
            }        
        return strb.toString();
    }
    
    public String stringValue() {
        if (type != FeatureType.TString) {
            throw new IllegalStateException("Feature does not contain a String");
        }
        return ((String[]) this.values)[0];
    }
    
    public float floatValue(){
        if(type == FeatureType.TInt) return (float)(((int[])values)[0]);
        if(type == FeatureType.TFloat) return (((float[])values)[0]);
        if(type == FeatureType.TDouble) return (float)(((double[])values)[0]);
        throw new IllegalStateException("Feature does not contain a number");
    }
    public int intValue(){
        if(type == FeatureType.TInt) return ((int[])values)[0];
        if(type == FeatureType.TFloat) return (int)(((float[])values)[0]);
        if(type == FeatureType.TDouble) return (int)(((double[])values)[0]);
        throw new IllegalStateException("Feature does not contain a number");
    
    }
    public double doubleValue(){
        if(type == FeatureType.TInt) return (double)((int[])values)[0];
        if(type == FeatureType.TFloat) return ((float[])values)[0];
        if(type == FeatureType.TDouble) return ((double[])values)[0];
        throw new IllegalStateException("Feature does not contain a number");
    }
    public float floatValue(int index){
        if(index > dimensionality-1 || index < 0){
            throw new IndexOutOfBoundsException("index out of bounds");
        }
        
        if(type == FeatureType.TInt) return ((int[])values)[index];
        if(type == FeatureType.TDouble) return (float)((double[])values)[index];
        if(type == FeatureType.TFloat) return (float)((float[])values)[index];
        throw new IllegalStateException("Feature is not of number type");
    }
     public double doubleValue(int index){
        if(index > dimensionality-1 || index < 0){
            throw new IndexOutOfBoundsException("index out of bounds");
        }
        
        if(type == FeatureType.TInt) return ((int[])values)[index];
        if(type == FeatureType.TDouble) return ((double[])values)[index];
        if(type == FeatureType.TFloat) return (double)((float[])values)[index];
        throw new IllegalStateException("Feature is not of number type");
    }
    
    //returns the number of elements in this feature
    public int size(){return dimensionality;}
    
    public String toOutputString(){
        StringBuilder str = new StringBuilder();
        str.append("Feature name: ").append(name).append(" : ").append(dimensionality);
        str.append(" of ").append(type).append(" <- ");
        if(values == null){str.append("null value...");return str.toString();}
        
        //TODO: check type and print element values...
        switch(type){
            case TFloat: for(float v : (float[]) values){str.append(v).append(" ");} break;
            case TInt: for(float v : (int[]) values){str.append(v).append(" ");}break;
            case TDouble: for(double v : (double[]) values){str.append(v).append(" ");}break;
            case TString: for(String v : (String[]) values){str.append(v).append(" ");}break;
            default: for(Object v : (Object[]) values){str.append(v).append(" ");}break;
        }
        
        return str.toString();
    }
    
    public int[] toIntArray() {
        if (this.type != FeatureType.TInt) {
            throw new IllegalStateException(String.format(
                    "feature %s does not hold an array of int", name));
        }
        return ((int[])values).clone();
    }
    public float[] toFloatArray() {
        if (this.type != FeatureType.TFloat) {
            throw new IllegalStateException(String.format(
                    "feature %s does not hold an array of float", name));
        }
        return ((float[])values).clone();
    }
    public double[] toDoubleArray() {
        if (this.type != FeatureType.TDouble) {
            throw new IllegalStateException(String.format(
                    "feature %s does not hold an array of double", name));
        }
        return ((double[])values).clone();
    }
    public String[] toStringArray() {
        if (this.type != FeatureType.TString) {
            throw new IllegalStateException(String.format(
                    "feature %s does not hold an array of string", name));
        }
        return ((String[])values).clone();
    }
}
