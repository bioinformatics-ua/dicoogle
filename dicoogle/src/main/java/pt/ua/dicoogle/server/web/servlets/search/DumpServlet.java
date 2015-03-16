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

package pt.ua.dicoogle.server.web.servlets.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.sdk.utils.DictionaryAccess;

/**
 * Dump of DICOM metadata
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class DumpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        if (StringUtils.isEmpty(uid)) {
            resp.sendError(400, "No uid supplied");
        }

        String query = "SOPInstanceUID:" + uid;

        DictionaryAccess da = DictionaryAccess.getInstance();

        HashMap<String, String> extraFields = new HashMap<>();
        for (String s : da.getTagList().keySet()) {
            extraFields.put(s, s);
        }

        JointQueryTask queryTaskHolder = new JointQueryTask() {

            @Override
            public void onCompletion() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onReceive(Task<Iterable<SearchResult>> e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        Iterable<SearchResult> results = null;
        try {
            results = PluginController.get().queryAll(queryTaskHolder, query, extraFields).get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(SearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (results == null) {
            resp.sendError(500, "Could not generate results!");
            //return;
        }

        ArrayList<SearchResult> resultsArr = new ArrayList<>();
        for (SearchResult r : results) {
            resultsArr.add(r);
        }
        String json = processJSON(resultsArr, 0);

        resp.setContentType("application/json");
        resp.getWriter().append(json);
    }

    private static String processJSON(Collection<SearchResult> results, long elapsedTime) {
        JSONObject resp = new JSONObject();
        for (SearchResult r : results) {
            JSONObject rj = new JSONObject();
            rj.put("uri", r.getURI().toString());

            JSONObject fields = new JSONObject();
            fields.accumulateAll(r.getExtraData());

            rj.put("fields", fields);

            resp.accumulate("results", rj);
        }
        resp.put("numResults", results.size());
        resp.put("elapsedTime", "NA");

        return resp.toString();
    }

}
