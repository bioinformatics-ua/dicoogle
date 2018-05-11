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
package pt.ua.dicoogle.sdk.utils;

import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections4.map.MultiValueMap;

/**
 * A structural index implementation to be used in MappedDICOMObjectDecorator
 * 
 * @author Tiago Marques Godinho, tmgodinho@ua.pt
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 *
 */
public class StructuralIndex {

  private Map<Integer, AttributeDescriptor> valuesMap;
  private MultiValueMap<String, Integer> map;
  
  public StructuralIndex() {
    this.valuesMap = new TreeMap<>();
    this.map = new MultiValueMap<>();
  }

  public void insert(String key, int nn, Object value) {
    this.map.put(key, nn);
    this.valuesMap.put(nn, new AttributeDescriptor(key, value));
    //System.err.printf("Inserted: %s, %d, %s\n", key, nn, value);
  }

  public Collection<Integer> getList(String key) {
    if(key.isEmpty()){
      Collection<Integer> c = new ArrayList<>(1);
      c.add(1);
      return c;
    }
    Collection<Integer> r = this.map.getCollection(key);
    return (r==null) ? Collections.EMPTY_LIST : r;    
  }
  
  public Object getValue(int key){
    return this.valuesMap.get(key);    
  }

  static int[][] lookupTable = new int[][]{
    new int[]{1,1,1},
    new int[]{2,130,128},
    new int[]{131,8323,64},
    new int[]{8324,270468,32},
    new int[]{270469,4464773,16},
    new int[]{4464774,71573638,16},
    new int[]{71573639,1145315463,16},
};

  
  public static int children(int p, int j){
    int i = 0;
    for(;i < lookupTable.length-1 ;i++) {
      if(p<=lookupTable[i][1]){
        return lookupTable[i+1][0] + lookupTable[i][2]*(p-lookupTable[i][0]) + j; 
      }
    }
    return -1;
  }
  
  public static int parent(int c){
    int i = 1;
    for(;i < lookupTable.length ;i++) {
      if(c<=lookupTable[i][1]){
        return (i>1) ? lookupTable[i-1][0]+((c-lookupTable[i][0])/lookupTable[i-1][2]): 1; 
      }
    }
    return -1;
  }
  
  public static Deque<Integer> pathToRoot(int c){
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(c);
    for(int i = lookupTable.length-1;i > 1 ;i--) {
      if(c >= lookupTable[i][0]){
        int pid = (i>1) ? lookupTable[i-1][0]+((c-lookupTable[i][0])/lookupTable[i-1][2]): 1; 
        stack.addFirst(pid);
        c = pid;
      }
    }
    return stack;
    
  }
  
  public AttributeDescriptor pathFromRoot(int node){        
    AttributeDescriptor ret = new AttributeDescriptor();
    //return valuesMap.get(node);
    ret.value = valuesMap.get(node).value;
    
    Deque<Integer> pathQueue = pathToRoot(node);
    
    StringWriter wr = new StringWriter();
    for(Integer pid : pathQueue){
      AttributeDescriptor descr = valuesMap.get(pid);
      wr.append("/").append(descr.name);      
    }
    ret.name = wr.toString();
    return ret;
  }
  
  public static class AttributeDescriptor{
    private String name;
    private Object value;
    
    public AttributeDescriptor(String name, Object value) {
      super();
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public Object getValue() {
      return value;
    }

    public AttributeDescriptor() {
      // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
      return "AttributeDescriptor [name=" + name + ", value=" + value + "]";
    }
    
    
  }
  
}
