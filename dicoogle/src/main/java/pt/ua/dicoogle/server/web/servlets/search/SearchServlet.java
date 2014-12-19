package pt.ua.dicoogle.server.web.servlets.search;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import pt.ua.dicoogle.core.QueryExpressionBuilder;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.JointQueryTask;
import pt.ua.dicoogle.sdk.task.Task;

/**
 * Search the DICOM metadata Perform queries on images. Return data in JSON
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class SearchServlet extends HttpServlet {
    //TODO: QIDO;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
         Example: http://localhost:8080/search?query=wrix&keyword=false&provicer=lucene
         */
        String query = request.getParameter("query");
        String provider = request.getParameter("provider");
        boolean keyword = Boolean.parseBoolean(request.getParameter("keyword"));

        if (!keyword) {
            QueryExpressionBuilder q = new QueryExpressionBuilder(query);
            query = q.getQueryString();
        }

        //Split provider string to list
        List<String> providerList = null;
        if (provider != null) {
            providerList = Arrays.asList(provider.split(";"));
        }

        if (StringUtils.isEmpty(query)) {
            response.sendError(400, "No query supplied!");
            return;
        }

        boolean queryAllProviders = false;
        if (providerList == null || providerList.isEmpty()) {
            queryAllProviders = true;
        }

        List<String> knownProviders = null;
        if (!queryAllProviders) {
            knownProviders = new ArrayList<>();
            List<String> activeProviders = PluginController.getInstance().getQueryProvidersName(true);
            for (String p : providerList) {
                if (activeProviders.contains(p)) {
                    knownProviders.add(p);
                }
            }
        }

        HashMap<String, String> extraFields = new HashMap<>();
        //attaches the required extrafields
        extraFields.put("PatientName", "PatientName");
        extraFields.put("PatientID", "PatientID");
        extraFields.put("Modality", "Modality");
        extraFields.put("StudyDate", "StudyDate");
        extraFields.put("SeriesInstanceUID", "SeriesInstanceUID");
        extraFields.put("StudyID", "StudyID");
        extraFields.put("StudyInstanceUID", "StudyInstanceUID");
        extraFields.put("Thumbnail", "Thumbnail");
        extraFields.put("SOPInstanceUID", "SOPInstanceUID");

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
            if (queryAllProviders) {
                results = PluginController.getInstance().queryAll(queryTaskHolder, query, extraFields).get();
            } else {
                results = PluginController.getInstance().query(queryTaskHolder, knownProviders, query, extraFields).get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(SearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (results == null) {
            response.sendError(500, "Could not generate results!");
            //return;
        }

        ArrayList<SearchResult> resultsArr = new ArrayList<>();
        for (SearchResult r : results) {
            resultsArr.add(r);
        }
        String json = processJSON(resultsArr, 0);

        response.setContentType("application/json");
        response.getWriter().append(json);
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
