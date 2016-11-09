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

package pt.ua.dicoogle.server.web.servlets.management;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.IndexReport;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class ForceIndexing extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ForceIndexing.class);

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    // Getting Parameters.
    String[] uris = req.getParameterValues("uri");
    String[] pluginsName = req.getParameterValues("plugin");

    if (uris == null) {
      resp.sendError(400, "No uri provided");
      return;
    }

    final PluginController pc = PluginController.getInstance();

    int expectedReports = uris.length * ((pluginsName == null) ?
            pc.getIndexingPlugins(true).size() :
            pluginsName.length);
    
    //Firing Tasks.
    List<Task<Report>> reports = new ArrayList<>(expectedReports);
    for (String uri : uris) {
        try {
            URI u = new URI(uri.replaceAll(" ", "%20"));
            // log.info("Sent Index Request: {}, {}",pluginName, u.toString());
            if (pluginsName == null) {
              reports.addAll(pc.index(u));
            } else {
              for (String pluginName : pluginsName) {
                reports.addAll(pc.index(pluginName, u));
              }
            }
        } catch (URISyntaxException ex) {
            logger.debug("Client provided bad URI");
            resp.sendError(400);
            return;
        }
    }

    // waiting is bad, clearing all this and giving an ok
    resp.setStatus(200);
    
    /*
    //Waiting for results, construct the output.
    List<Report> done = new ArrayList<>(reports.size());
    JSONArray ret = new JSONArray();
    for (Task<Report> t : reports) {
      try {
        Report report = t.get();
        done.addMoveDestination(report);
        JSONObject obj;
        if (report instanceof IndexReport) {
            IndexReport indexReport = (IndexReport) report;
            obj = convertReportToJSON(indexReport);
        } else {
            // no information is available
            obj = new JSONObject();
            obj.put("complete", true);
            obj.put("errors", 0);
        }
        ret.addMoveDestination(obj);
      } catch (InterruptedException | ExecutionException ex) {
        // log.error("UNKNOW ERROR", ex);
      }
    }
    // log.info("Finished forced indexing procedure: {}", reports.size());

    resp.setContentType("application/json");
    resp.getWriter().write(ret.toString());
    resp.getWriter().flush();
    */
  }
  
  @Deprecated
  private static JSONObject convertReportToJSON(IndexReport r){
    JSONObject obj = new JSONObject();
    obj.put("indexed", r.getNIndexed());
    obj.put("errors", r.getNErrors());
    obj.put("elapsedTime", r.getElapsedTime());
    
    JSONObject extraObjects = new JSONObject();
    
    Method[] methods = r.getClass().getDeclaredMethods();
    for(Method m : methods){
      if( Modifier.isPublic(m.getModifiers()) && m.getName().startsWith("get") && m.getParameterTypes().length == 0){
        Object ret = null;
        try {
          ret = m.invoke(r, null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        }
        if(ret != null)
          extraObjects.put(m.getName().substring(3), ret);
      }
    }    
    
    obj.put("extra", extraObjects);
    return obj;
  }
}
