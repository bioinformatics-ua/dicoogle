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
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.IndexReport;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;

/**
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class ForceIndexing extends HttpServlet {

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

    int expectedReports = uris.length * ((pluginsName == null) ? 1 : pluginsName.length);
    resp.setContentType("application/json");

    PluginController pc = PluginController.getInstance();
    
    //Firing Tasks.
    List<Task<Report>> reports = new ArrayList<>(expectedReports);
    for (String uri : uris) {
      URI u = null;
      try {
        u = new URI(uri.replaceAll(" ", "%20"));
      } catch (URISyntaxException ex) {
        // log.error("Could not create URI", ex);
        ex.printStackTrace();
      }
      if (u != null) {
        // log.info("Sent Index Request: {}, {}",pluginName, u.toString());
        if (pluginsName == null) {
          reports.addAll(pc.index(u));
        } else {
          for (String pluginName : pluginsName) {
            reports.addAll(pc.index(pluginName, u));
          }
        }

      }
    }
        
    //Waiting for results, construct the output.
    List<IndexReport> done = new ArrayList<>(reports.size());
    JSONArray ret = new JSONArray();
    for (Task<Report> t : reports) {
      try {
        IndexReport r = (IndexReport) t.get();
        JSONObject obj = convertReportToJSON(r);
        done.add(r);
        ret.add(obj);
      } catch (InterruptedException | ExecutionException ex) {
        // log.error("UNKNOW ERROR", ex);
        ex.printStackTrace();
      }
    }
    // log.info("Finished forced indexing procedure: {}", reports.size());

    resp.getWriter().write(ret.toString());
    resp.getWriter().flush();
  }
  
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
