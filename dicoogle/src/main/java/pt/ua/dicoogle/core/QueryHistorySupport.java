/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

import pt.ua.dicoogle.sdk.Utils.Platform;

/**
 *
 * @author Samuel da Costa Campos <samuelcampos@ua.pt>
 */
public class QueryHistorySupport extends Observable {

    //consider replacing this data type "ArrayList<SimpleEntry<String, Boolean>>"
    private ArrayList<QueryHistoryEntry<String, Boolean>> queryHistory;

    private static String fileName = Platform.homePath() + "QueryHistory.ser";

    private static QueryHistorySupport instance = null;

    public static synchronized QueryHistorySupport getInstance()
    {
        if (instance == null) {
            instance = new QueryHistorySupport();
        }
        return instance;
    }

    private QueryHistorySupport(){
        if(!loadQueryHistory())
            queryHistory = new ArrayList<QueryHistoryEntry<String, Boolean>>();

    }

    private boolean loadQueryHistory(){
        try {
            ObjectInput in = new ObjectInputStream(new FileInputStream(fileName));
            queryHistory = (ArrayList<QueryHistoryEntry<String, Boolean>>) in.readObject();
            in.close();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Iterator<QueryHistoryEntry<String, Boolean>> getQueryHistory(){
        return queryHistory.iterator();
    }

    public boolean saveQueryHistory(){
        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(queryHistory);
            out.close();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void addQuery(String query, boolean keywords){
        QueryHistoryEntry<String, Boolean> entry = new QueryHistoryEntry<String, Boolean>(query, keywords);

        if(!queryHistory.contains(entry))
            queryHistory.add(entry);
        
        setChanged();
        notifyObservers();
    }

    public boolean deleteQuery(QueryHistoryEntry<String, Boolean> entry){
        boolean result = queryHistory.remove(entry);
        
        setChanged();
        notifyObservers();

        return result;
    }

    public void deleteAll(){
        queryHistory.clear();

        setChanged();
        notifyObservers();
    }
}
