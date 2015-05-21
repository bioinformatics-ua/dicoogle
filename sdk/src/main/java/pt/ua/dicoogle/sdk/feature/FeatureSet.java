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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains the values that we would fetch from a document.
 * 
 * Ok, for the number of features I'm currently using I have no idea whether is faster to scan sequentially
 * through a list, or to use a map. So I'm using a map... it should be faster if we end up using something like
 * one BILLION features! which we totally do! (not)... but it's neater to use
 * TODO: check speed differences for common values between map and list/array
 * 
 * @author fmvalente
 * @author Eduardo Pinho
 */
public class FeatureSet implements Iterable<Feature> {

    protected SortedMap<String,Feature> features;

    public static final FeatureSet EMPTY = new FeatureSet(Collections.EMPTY_MAP);
    
    public FeatureSet(){features = new TreeMap<>();}
    
    public FeatureSet(Feature ... featuresInSet){
        features = new TreeMap<>();
        for(Feature feat : featuresInSet){
            features.put(feat.getName(),feat);
        }
    }
    public FeatureSet(Map<String, Feature> features){
        this.features = new TreeMap<>(features);
    }
    public FeatureSet(Collection<Feature> features){
        this.features = new TreeMap<>();
        for(Feature feat : features){
            this.features.put(feat.getName(), feat);
        }
    }
    
    public int size() {return this.features.size();}
    
    public void add(Feature f){features.put(f.getName(),f);}
    public Feature get(String name){return features.get(name);}
    public Optional<Feature> getOptional(String name){return Optional.ofNullable(features.get(name));}
    public Map<String, Feature> getFeatureMap(){return Collections.unmodifiableMap(features);}

    
    /*For foreach loop support*/
    @Override
    public Iterator<Feature> iterator() {return features.values().iterator();}
    
    @Override
    public Spliterator<Feature> spliterator() {return features.values().spliterator();}
    
    /* Obtain a stream of features */
    public Stream<Feature> stream() {return features.values().stream();}
    
    /**
     * merges the features from the argument with the ones on this feature set
     * warning: if some features are already present in this, they will be overwritten
     * @param otherSet 
     */
    public void merge(FeatureSet otherSet){
        for(Feature f : otherSet ) {
            add(f);
        }
    }

    /**
     * merges the features of both feature sets into a new set. Both feature sets will not be modified,
     * unlike {@linkplain merge}.
     * When the sets have intersecting features, the second set will override them.
     * @param fs1
     * @param fs2
     * @return a new feature set with both sets merged
     */
    public static FeatureSet merged(FeatureSet fs1, FeatureSet fs2){
        FeatureSet res = new FeatureSet(fs1.features);
        for(Feature f : fs2) {
            res.features.putAll(fs2.features);
        }
        return res;
    }
    
    /*utility to parse an input feature string
    the feature string is composed by several lines in the form of:
    feature_name(datatype, dimensions){values#1, value2#, ..., valueN#} 
    
    two concrete examples:
    * 
    * entropy(float,1){1234.23)
    * histogram_xpto(double,16){1.23, 4.56, 7.89, ...}
    */
    public static FeatureSet parseString(String featureString){        
        //first we split the string by lines
        List<String> lines = Arrays.asList(featureString.split("\\r?\\n"));
        // then convert each line of input to a feature
        return new FeatureSet(lines.stream()
                .map(FeatureSet::parseFeature)
                .collect(Collectors.toSet()));
    }
    
    private static Feature parseFeature(String line) {
        int endNameIndex = line.indexOf('(');//we get the feature name
        String featureName = line.substring(0,endNameIndex).trim();

        int endDataTypeIndex = line.indexOf(',');//and the associated datatype
        String datatype = line.substring(endNameIndex+1, endDataTypeIndex).trim();

        int endDimIndex = line.indexOf(')');//plus the number of dimensions of the feature
        int dimensionality = Integer.parseInt(line.substring(endDataTypeIndex+1,endDimIndex).trim());

        //extracts an array of elements (the values of the feature components)
        int arrayStart = line.indexOf('{')+1;
        int arrayEnd = line.indexOf('}');

        //now we get the values and fill in the feature object
        //the object will then be added to the feature list to be returned
        List<String> values = Arrays.asList(line.substring(arrayStart,arrayEnd).split(","));

        FeatureType type = FeatureType.fromString(datatype);
        Feature feature;
        switch(type){
            case TInt :{
                feature = new Feature(featureName,values.stream().mapToInt(Integer::parseInt).toArray());
                break;
            }
            case TFloat:{
                // no mapToFloat :(
                float[] vals = new float[dimensionality];
                int k = 0;
                for(String val : values){
                    ((float[])vals)[k] = Float.parseFloat(val);
                    k++;
                }
                feature = new Feature(featureName, vals);
                break;
            }
            case TDouble:{
                feature = new Feature(featureName, values.stream().mapToDouble(Double::parseDouble).toArray());
                break;
            }
            case TString:{
                feature = new Feature(featureName, values.toArray(null));
                break;
            }
            default:
                feature = Feature.createUnknown(featureName, values);
        }
        //and creates a feature object to store them
        return feature;
    }
        
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("FeatureSet: (").append(features.size()).append(")\n");
        
        for(Feature elem : this)
            str.append(elem.toOutputString()).append("\n");
        
        return str.toString();
    }
}
