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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

import java.util.concurrent.ExecutionException;

import com.google.common.collect.ImmutableList;
import net.sf.json.JSONArray;
import org.json.JSONException;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.sdk.datastructs.dim.DIMGeneric;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.sdk.utils.QueryParseException;

/**
 * Search the DICOM metadata, perform queries on images. Returns the data in JSON.
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class SearchServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);

    private static final long serialVersionUID = 1L;

    private final Collection<String> DEFAULT_FIELDS = Arrays.asList("SOPInstanceUID", "StudyInstanceUID",
            "SeriesInstanceUID", "PatientID", "PatientName", "PatientSex", "Modality", "StudyDate", "StudyID",
            "StudyDescription", "SeriesNumber", "SeriesDescription", "InstitutionName", "uri");

    public enum SearchType {
        ALL, PATIENT;
    }

    private final SearchType searchType;

    public SearchServlet() {
        searchType = SearchType.ALL;
    }

    public SearchServlet(SearchType stype) {
        if (stype == null)
            searchType = SearchType.ALL;
        else
            searchType = stype;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
         Example: http://localhost:8080/search?query=wrix&provider=lucene&psize=10&offset=10
         */
        response.setContentType("application/json");

        String query = request.getParameter("query");
        String[] providers = request.getParameterValues("provider");
        String[] fields = request.getParameterValues("field");
        String pExpand = request.getParameter("expand");
        boolean expand = pExpand != null && (pExpand.isEmpty() || Boolean.parseBoolean(pExpand));

        final int psize;
        final int offset;
        final int depth;
        try {
            psize = getReqParameter(request, "psize", Integer.MAX_VALUE);
            if (psize < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sendError(response, 400, "Invalid parameter psize: must be a non-negative integer");
            return;
        }
        try {
            offset = getReqParameter(request, "offset", 0);
            if (offset < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sendError(response, 400, "Invalid parameter offset: must be a non-negative integer");
            return;
        }
        String paramDepth = request.getParameter("depth");
        if (paramDepth != null) {
            if (this.searchType != SearchType.PATIENT) {
                sendError(response, 400, "Parameter depth is only applicable to /searchDIM endpoint");
                return;
            }
            switch (paramDepth.toLowerCase()) {
                case "none":
                    depth = 0;
                    break;
                case "patient":
                    depth = 1;
                    break;
                case "study":
                    depth = 2;
                    break;
                case "series":
                    depth = 3;
                    break;
                case "image":
                    depth = 4;
                    break;
                default:
                    sendError(response, 400, "Invalid parameter depth: must be a valid level: "
                            + "'none', 'patient', 'study', 'series' or 'image'");
                    return;
            }
        } else {
            depth = 4;
        }

        // retrieve desired fields
        final Set<String> actualFields;
        if (fields == null || fields.length == 0) {
            actualFields = null;
        } else {
            actualFields = new HashSet<>(Arrays.asList(fields));
        }

        if (StringUtils.isEmpty(query)) {
            sendError(response, 400, "No query supplied!");
            return;
        }

        if (expand) {
            QueryExpressionBuilder q = new QueryExpressionBuilder(query);
            query = q.getQueryString();
        }

        List<String> providerList = providers != null ? Arrays.asList(providers) : new ArrayList<>();
        providerList = PluginController.getInstance().filterDicomQueryProviders(providerList);
        if (providerList.size() == 0) {
            sendError(response, 400, "No valid DIM providers supplied.");
            return;
        }
        List<String> knownProviders = null;

        boolean queryAllProviders = false;
        if (providers == null || providers.length == 0) {
            queryAllProviders = true;
        } else {
            providerList = Arrays.asList(providers);
            if (providerList.isEmpty()) {
                queryAllProviders = true;
            }
        }

        if (!queryAllProviders) {
            knownProviders = new ArrayList<>();
            List<String> activeProviders = PluginController.getInstance().getQueryProvidersName(true);
            for (String p : providers) {
                if (activeProviders.contains(p)) {
                    knownProviders.add(p);
                } else {
                    response.setStatus(400);
                    JSONObject obj = new JSONObject();
                    obj.put("error", p.toString() + " is not a valid query provider");
                    response.getWriter().append(obj.toString());
                    return;
                }
            }
        }

        HashMap<String, String> extraFields = new HashMap<>();
        if (actualFields == null) {

            // attaches the required extrafields
            for (String field : DEFAULT_FIELDS) {
                extraFields.put(field, field);
            }
        } else {
            for (String f : actualFields) {
                extraFields.put(f, f);
            }
        }

        JointQueryTask queryTaskHolder = new JointQueryTask() {

            @Override
            public void onCompletion() {}

            @Override
            public void onReceive(Task<Iterable<SearchResult>> e) {}
        };

        try {
            long elapsedTime = System.currentTimeMillis();
            Iterable<SearchResult> results =
                    PluginController.getInstance().query(queryTaskHolder, providerList, query, extraFields).get();

            if (this.searchType == SearchType.PATIENT) {
                try {
                    DIMGeneric dimModel = new DIMGeneric(ImmutableList.copyOf(results));
                    elapsedTime = System.currentTimeMillis() - elapsedTime;
                    response.getWriter().write(dimModel.getJSON());
                    // dimModel.writeJSON(response.getWriter(), elapsedTime, depth, offset, psize);
                } catch (Exception e) {
                    logger.warn("Failed to get DIM", e);
                }
            } else {
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                this.writeResponse(response, results, elapsedTime, offset, psize);
            }

        } catch (QueryParseException ex) {
            sendError(response, 400, ex.getMessage());
            return;
        } catch (InterruptedException | ExecutionException | RuntimeException ex) {
            logger.error("Failed to retrieve results", ex);
            sendError(response, 500, "Could not generate results");
            return;
        } catch (JSONException e) {
            logger.error("Failed to serialize results", e);
            return;
        }
    }

    private void writeResponse(HttpServletResponse resp, Iterable<SearchResult> results, long elapsedTime, int offset,
            int psize) throws IOException, JSONException {
        JSONWriter writer = new JSONWriter(resp.getWriter());
        writer.object(); // begin output
        // results
        writer.key("results").array(); // begin results
        int count = 0;
        for (Iterator<SearchResult> it = results.iterator(); it.hasNext(); ++count) {
            SearchResult res = it.next();
            if (count < offset || count >= offset + psize)
                continue;
            writer.object() // begin result
                    .key("uri").value(res.getURI().toString()).key("fields").object();
            for (Map.Entry<String, Object> e : res.getExtraData().entrySet()) {
                writer.key(e.getKey()).value(String.valueOf(e.getValue()).trim());
            }
            writer.endObject().endObject(); // end result
        }
        // other fields
        writer.endArray() // end results
                .key("elapsedTime").value(elapsedTime).key("numResults").value(count);
        writer.endObject(); // end output
    }

    private int getReqParameter(HttpServletRequest req, String name, int defaultValue) throws NumberFormatException {
        String param = req.getParameter(name);
        int val = defaultValue;
        if (param != null) {
            val = Integer.parseInt(param);
        }
        return val;
    }

    private static void sendError(HttpServletResponse resp, int code, String message) throws IOException {
        resp.setStatus(code);
        JSONObject obj = new JSONObject();
        obj.put("results", new JSONArray());
        obj.put("error", message);
        resp.getWriter().append(obj.toString());
    }
}
