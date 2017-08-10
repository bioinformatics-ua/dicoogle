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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;
import org.dcm4che2.data.DicomObject;

/**
 * Simple implementation of the a memory mapped DICOM Object.
 * This class provides no structural index, 
 * therefore, operations other than retrieving attributes by their full path may be slow.
 *
 * @author Tiago Marques Godinho, tmgodinho@ua.pt
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 *
 */
public class MappedDICOMObject extends DICOMObject {

    public Map<String, Object> map;

    public MappedDICOMObject() {
        this.map = new HashMap<String, Object>();
    }

    public MappedDICOMObject(Path parent) {
        this.map = new HashMap<String, Object>();
    }

    public static MappedDICOMObject create(DicomObject obj) {
        return DicomObjectFactory.toMap(obj);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.get(key) != null;
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public Object get(Object key) {
        return this.getByPath((String) key);
    }

    public Object put(String key, Object value) {
        Path p = Path.fromString(key);

        MappedDICOMObject current = this;
        String[] nodes = p.getNodes();
        //System.out.println(Arrays.toString(nodes));
        int i;
        for (i = 0; i < nodes.length - 1; i++) {
            String node = nodes[i];
            Object o = current.map.get(node);
            if (o == null) {
                o = new MappedDICOMObject();
                current.map.put(node, o);
            }
            if (!(o instanceof MappedDICOMObject))
                throw new IllegalAccessError(node);
            current = (MappedDICOMObject) o;
        }
        current.map.put(nodes[i], value);
        return current;
    }

    public Object remove(Object key) {
        Path p = Path.fromString((String) key);

        MappedDICOMObject current = this;
        String[] nodes = p.getNodes();
        //System.out.println(Arrays.toString(nodes));
        int i;
        for (i = 0; i < nodes.length - 1; i++) {
            String node = nodes[i];
            Object o = current.map.get(node);
            if (o == null || !(o instanceof MappedDICOMObject)) {
                return null;
            }
            current = (MappedDICOMObject) o;
        }
        return current.map.remove(nodes[i]);
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        for (java.util.Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        map.clear();
    }

    public Set<String> keySet() {
        Set<String> keys = new HashSet<String>();

        for (String key : this.map.keySet()) {
            Object o = this.get(key);
            keys.add(key);

            if (o instanceof DICOMObject) {
                Set<String> temp = ((DICOMObject) o).keySet();
                for (String t : temp) {
                    keys.add(key + "." + t);
                }
            }
        }

        return keys;
    }

    public Collection<Object> values() {
        return map.values();
    }

    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public boolean equals(Object o) {
        return map.equals(o);
    }

    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public Object getByPath(String path) {
        Path p = Path.fromString(path);

        MappedDICOMObject current = this;
        String[] nodes = p.getNodes();
        //System.out.println(Arrays.toString(nodes));
        int i;
        for (i = 0; i < nodes.length - 1; i++) {
            String node = nodes[i];
            Object o = current.map.get(node);
            if (o == null || !(o instanceof MappedDICOMObject)) {
                return null;
            }
            current = (MappedDICOMObject) o;
        }
        return current.map.get(nodes[i]);
    }

    public List<String> getPaths() {
        List<String> paths = new ArrayList<>();

        Map<String, Object> node = null;
        ArrayDeque<DICOMObject> nodes = new ArrayDeque<>();
        ArrayDeque<String> aux = new ArrayDeque<String>();
        nodes.push(this);
        aux.push("");
        while (!nodes.isEmpty()) {
            node = nodes.pop();
            String parent = aux.pop();
            for (Map.Entry<String, Object> entry : node.entrySet()) {
                String np = parent + "/" + entry.getKey();
                paths.add(np);
                if (entry.getValue() instanceof DICOMObject) {
                    nodes.push((DICOMObject) entry.getValue());
                    aux.push(np);
                }
            }
        }
        return paths;
    }

    @Override
    public Map<String, Object> getRelativeTag(String path) {
        Map<String, Object> retMap = new HashMap<>();
        List<String> paths = this.getPaths();

        String test = path.substring(1);

        for (String p : paths) {
            if (p.endsWith(test))
                retMap.put(p, this.getByPath(p));
        }
        return retMap;
    }

    public JSONObject toJSON() {
        return JSONObject.fromObject(this.map);
    }
}
