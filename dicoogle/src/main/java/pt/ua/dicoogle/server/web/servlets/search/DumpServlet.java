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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
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
    private static final Logger logger = LoggerFactory.getLogger(DumpServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        if (StringUtils.isEmpty(uid)) {
            resp.sendError(400, "No uid supplied");
        }
        
        String[] providerArr = req.getParameterValues("provider");
        List<String> providers = (providerArr == null)
                ? PluginController.getInstance().getQueryProvidersName(true)
                : Arrays.asList(providerArr);
        
        String query = "SOPInstanceUID:" + uid;

        DictionaryAccess da = DictionaryAccess.getInstance();

        HashMap<String, String> extraFields = new HashMap<>();
        for (String s : da.getTagList().keySet()) {
            extraFields.put(s, s);
        }

        JointQueryTask queryTaskHolder = new JointQueryTask() {
            @Override
            public void onCompletion() {
            }
            @Override
            public void onReceive(Task<Iterable<SearchResult>> e) {
            }
        };

        long tick = System.currentTimeMillis();
        Iterable<SearchResult> results;
        try {
            results = PluginController.getInstance().query(queryTaskHolder, providers, query, extraFields).get();
        } catch (InterruptedException | ExecutionException ex) {
            logger.warn("Failed to generate results", ex);
            resp.sendError(500, "Could not generate results!");
            return;
        }

        long time = System.currentTimeMillis()-tick;
        String json = processJSON(results, time);

        resp.setContentType("application/json");
        resp.getWriter().append(json);
    }

    private static String processJSON(Iterable<SearchResult> results, long elapsedTime) {
        JSONObject resp = new JSONObject();
        JSONObject rj = new JSONObject();
        JSONObject fields = new JSONObject();
        for (SearchResult r : results) {
            rj.put("uri", r.getURI().toString());
            for (Map.Entry<String,Object> e : r.getExtraData().entrySet()) {
                fields.put(e.getKey(), e.getValue());
            }
        }
        rj.put("fields", fields);
        resp.put("results", rj);
        resp.put("elapsedTime", elapsedTime);
        return resp.toString();
    }

}
