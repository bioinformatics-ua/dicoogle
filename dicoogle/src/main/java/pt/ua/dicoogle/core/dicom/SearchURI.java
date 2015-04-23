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
package pt.ua.dicoogle.core.dicom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 *
 * @author Luís A. Bastião Silva - <bastiao@ua.pt>
 */
public class SearchURI {

    public Iterable<SearchResult> search(String query) {

        HashMap<String, String> extraFields = new HashMap<String, String>();
        ArrayList<SearchResult> resultsArr = new ArrayList<SearchResult>();
        extraFields.put("SOPInstanceUID", "SOPInstanceUID");
        MyHolder holder = new MyHolder();
        PluginController.getInstance().queryAll(holder, query, extraFields);
        Iterable<SearchResult> results = null;
        try {
            results = holder.get();
        } catch (InterruptedException ex) {
            LoggerFactory.getLogger(SearchURI.class).error(ex.getMessage(), ex);
        } catch (ExecutionException ex) {
            LoggerFactory.getLogger(SearchURI.class).error(ex.getMessage(), ex);
        }
        if (results==null)
        {
            return results;
        }
        for (SearchResult r : results) {
            resultsArr.add(r);
        }

        return resultsArr;
        

    }

    class MyHolder extends JointQueryTask {

        @Override
        public void onCompletion() {
            
        }

        @Override
        public void onReceive(Task<Iterable<SearchResult>> e) {

        }

    }

}
