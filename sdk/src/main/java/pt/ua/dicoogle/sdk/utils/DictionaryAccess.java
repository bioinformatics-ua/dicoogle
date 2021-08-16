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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import org.slf4j.LoggerFactory;
import org.dcm4che2.data.Tag;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@bmd-software.com>
 */
public class DictionaryAccess {

    private HashMap<String, Integer> tagList = new HashMap<String, Integer>();
    private HashMap<Integer, String> tagListByTag = new HashMap<Integer, String>();


    private static final DictionaryAccess instance = new DictionaryAccess();;

    public static DictionaryAccess getInstance() {
        return instance;
    }

    private DictionaryAccess() {

        Field[] tags = Tag.class.getFields();
        for (int i = 0; i < tags.length; i++) {
            try {
                tagList.put(tags[i].getName(), tags[i].getInt(null));
                tagListByTag.put(tags[i].getInt(null), tags[i].getName());
            } catch (IllegalArgumentException ex) {
                LoggerFactory.getLogger(DictionaryAccess.class).error(ex.getMessage(), ex);
            } catch (IllegalAccessException ex) {
                LoggerFactory.getLogger(DictionaryAccess.class).error(ex.getMessage(), ex);
            }
        }
    }

    public String tagName(int tag) {
        return this.tagListByTag.get(tag);
    }

    public String toString() {
        String str = "";
        Iterator<String> it = (Iterator<String>) getTagList().keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            str += "Name: " + key + " with value: " + Integer.toHexString(this.getTagList().get(key));
        }
        return str;
    }


    /*public static void  main(String args[]) throws IllegalArgumentException, IllegalAccessException
    {
        DictionaryAccess da = new DictionaryAccess();
        
    }*/

    /**
     * @return the tagList
     */
    public HashMap<String, Integer> getTagList() {
        return tagList;
    }

    /**
     * @param tagList the tagList to set
     */
    public void setTagList(HashMap<String, Integer> tagList) {
        this.tagList = tagList;
    }

}
