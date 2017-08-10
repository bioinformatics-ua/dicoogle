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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dcm4che2.data.DicomObject;

import net.sf.json.JSONObject;

/**
 * A decorator to the MappedDICOMObject which features a structural index to speed-up more complex queries.
 *
 * @author Tiago Marques Godinho, tmgodinho@ua.pt
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 *
 */
public class MappedDICOMObjectDecorator extends DICOMObject {

    private StructuralIndex structuralIndex;
    private MappedDICOMObject innerObject;

    public MappedDICOMObjectDecorator(MappedDICOMObject obj) {
        super();
        this.structuralIndex = new StructuralIndex();
        this.innerObject = obj;
        this.createStructuralIndex();
    }

    public static MappedDICOMObjectDecorator createWithIndex(DicomObject obj) {
        MappedDICOMObject ret = DicomObjectFactory.toMap(obj);
        return new MappedDICOMObjectDecorator(ret);
    }

    public void createStructuralIndex() {
        ArrayDeque<DICOMObject> nodes = new ArrayDeque<>();
        ArrayDeque<Integer> aux = new ArrayDeque<Integer>();
        nodes.push(this);
        aux.push(1);
        while (!nodes.isEmpty()) {
            DICOMObject node = nodes.remove();
            Integer pid = aux.remove();
            TreeSet<Map.Entry<String, Object>> children = new TreeSet<Map.Entry<String, Object>>(new Comparator<Map.Entry<String, Object>>() {

                @Override
                public int compare(java.util.Map.Entry<String, Object> o1,
                                   java.util.Map.Entry<String, Object> o2) {
                    // TODO Auto-generated method stub
                    return o1.getKey().compareTo(o2.getKey());
                }
            });
            children.addAll(node.entrySet());

            int nn = StructuralIndex.children(pid, 0);
            for (Map.Entry<String, Object> entry : children) {
                structuralIndex.insert(entry.getKey(), nn, entry.getValue());
                if (entry.getValue() instanceof DICOMObject) {
                    nodes.push((DICOMObject) entry.getValue());
                    aux.push(nn);
                }
                nn++;
            }
        }
    }

    @Override
    public Object getByPath(String path) {
        HashMap<String, Object> map = new HashMap<>();
        String[] nodes = path.split("/");

        //System.err.printf("NODES: %s\n", Arrays.toString(nodes));

        Collection<Integer> descriptorList = structuralIndex.getList("");
        //System.err.printf("DESCRIPTOR: %s\n", Arrays.toString(descriptorList.toArray()));
        for (int i = 0; i < nodes.length; i++) {
            descriptorList = ancestorDescendentJoin(descriptorList, structuralIndex.getList(nodes[i]));

            //System.err.printf("DESCRIPTOR: %s\n", Arrays.toString(descriptorList.toArray()));
        }

        if (descriptorList.size() > 1) {
            //System.err.println("More than one record found!!");
            return null;
        }

        for (Integer k : descriptorList) {
            StructuralIndex.AttributeDescriptor dscr = structuralIndex.pathFromRoot(k);
            return dscr.getValue();
        }
        return null;
    }

    @Override
    public Map<String, Object> getRelativeTag(String path) {
        HashMap<String, Object> map = new HashMap<>();
        String[] nodes = path.substring(2).split("/");

        //System.err.printf("NODES: %s\n", Arrays.toString(nodes));

        String cNode = nodes[0];
        Collection<Integer> descriptorList = structuralIndex.getList(cNode);
        //System.err.printf("DESCRIPTOR: %s\n", Arrays.toString(descriptorList.toArray()));
        for (int i = 1; i < nodes.length; i++) {
            descriptorList = ancestorDescendentJoin(descriptorList, structuralIndex.getList(nodes[i]));

            //System.err.printf("DESCRIPTOR: %s\n", Arrays.toString(descriptorList.toArray()));
        }

        for (Integer k : descriptorList) {
            StructuralIndex.AttributeDescriptor dscr = structuralIndex.pathFromRoot(k);
            map.put(dscr.getName(), dscr.getValue());
        }
        return map;
    }
//  
//  Path-join(Path):
//    nodes[] <- Path.nodes
//    Paths <- []
//    for(node in nodes):
//        a <- getLastNode(Path)
//        d <- getStructuralIndex(node)
//        r <- ancestor-descendent-join(a, d,node.mode)
//        Paths <- join(Path, r)
//    Return Paths

    private Collection<Integer> ancestorDescendentJoin(Collection<Integer> ancestorList, Collection<Integer> descendentList) {
        Collection<Integer> ret = new TreeSet<Integer>();
        Iterator<Integer> it = descendentList.iterator();
        while (it.hasNext()) {
            Integer cid = it.next();
            Integer pid = StructuralIndex.parent(cid);
            if (ancestorList.contains(pid))
                ret.add(cid);
        }
        return ret;
    }

    public int size() {
        return innerObject.size();
    }

    public boolean isEmpty() {
        return innerObject.isEmpty();
    }

    public boolean containsKey(Object key) {
        return innerObject.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return innerObject.containsValue(value);
    }

    public Object get(Object key) {
        return innerObject.get(key);
    }

    public Object put(String key, Object value) {
        return innerObject.put(key, value);
    }

    public Object remove(Object key) {
        return innerObject.remove(key);
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        innerObject.putAll(m);
    }

    public void clear() {
        innerObject.clear();
    }

    public Set<String> keySet() {
        return innerObject.keySet();
    }

    public Collection<Object> values() {
        return innerObject.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return innerObject.entrySet();
    }

    public boolean equals(Object o) {
        return innerObject.equals(o);
    }

    public int hashCode() {
        return innerObject.hashCode();
    }

    public List<String> getPaths() {
        return innerObject.getPaths();
    }

    public JSONObject toJSON() {
        return innerObject.toJSON();
    }

    public String toString() {
        return innerObject.toString();
    }

//  Ancestor-descendent-join(ancestors, descendent,mode):
//    Output <- []
//    For(a in ancestors):
//        Lvl <- getLevel(a)+1
//        Depth <- if (mode) lvl | 6
//        For(lvl to mode):
//        min <- child(a.id,0)
//        max <- min + attributesPerLevel(lvl)
//        Output[a] <- searchRange(descendents, min, max))
//    Return Output
}
