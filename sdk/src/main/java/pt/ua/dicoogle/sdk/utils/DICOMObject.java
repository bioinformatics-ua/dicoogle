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

import net.sf.json.JSONObject;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * An interface that maps a DICOM Object into a Map. 
 * The idea is to have an in-memory model of the DICOM Objects document tree.
 *
 * @author Tiago Marques Godinho, tmgodinho@ua.pt
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 */
public abstract class DICOMObject implements Map<String, Object> {

    /**
     * Retrieves the Attribute denoted by its full path.
     * @param path ex /PatientName
     * @return The attributes value or null if no attribute is found.
     */
    public abstract Object getByPath(String path);

    /**
     * Retrieves the attributes that match the specified query.
     * This method allows more relations to be specified in the query, other than parent child.
     *
     * For instance, it is possible to query //ContentSequence/CodeValue or //PatientName
     *
     * @param path The query
     * @return a Map matching the attributes content to their full path.
     */
    public abstract Map<String, Object> getRelativeTag(String path);

    /**
     *
     * @return A JSON Representation of this DICOM Object.
     */
    public abstract JSONObject toJSON();

    public static String[] getPathFromString(String key) {
        return key.split("/");
    }

    public static class Path implements Iterable<String> {

        public static Path NULL_PATH = new Path(new String[0]);
        private String[] nodes;

        public Path(String[] nodes) {
            super();
            this.nodes = nodes;
        }

        public String[] getNodes() {
            return nodes;
        }

        public static Path fromString(String path) {
            return new Path(path.split("/"));
        }

        public Path parent() {
            if (nodes.length <= 1)
                return NULL_PATH;
            return new Path(Arrays.copyOfRange(nodes, 0, nodes.length - 2));
        }

        public Path getAttributePath(String nodeName) {
            if (this.nodes.length == 0)
                return NULL_PATH;
            String[] n = Arrays.copyOfRange(this.nodes, 0, nodes.length);
            n[this.nodes.length] = nodeName;
            return new Path(n);
        }

        public String getAttributePathString(String nodeName) {
            if (nodes.length == 0)
                return "";
            return toString() + "/" + nodeName;
        }

        @Override
        public String toString() {
            if (nodes.length == 0)
                return "";

            return StringUtils.join(nodes, "/");
        }

        public String getAttributeName() {
            return this.nodes[nodes.length - 1];
        }

        @Override
        public Iterator<String> iterator() {
            return IteratorUtils.arrayIterator(nodes, 0);
        }

        public Iterator<String> parentIterator() {
            if (this.hasParent())
                return IteratorUtils.arrayIterator(nodes, 0, nodes.length - 2);
            return IteratorUtils.EMPTY_ITERATOR;
        }

        public String getBaseName() {
            return this.nodes[0];
        }

        public boolean hasParent() {
            return this.nodes.length > 1;
        }

        public boolean hasAttribute() {
            return this.nodes.length >= 1;
        }

    }
}
