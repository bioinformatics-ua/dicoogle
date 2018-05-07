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

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.datastructs.IndexReport;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.task.Task;
import pt.ua.dicoogle.taskManager.RunningIndexTasks;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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

    int expectedTasks = uris.length * ((pluginsName == null) ?
            pc.getIndexingPlugins(true).size() :
            pluginsName.length);
    
    //Firing Tasks.
    List<Task<Report>> tasks = new ArrayList<>(expectedTasks);
    List<JSONObject> taskObjs = new ArrayList<>(expectedTasks);
    for (String uri : uris) {
        try {
            URI u = encodeURI(uri);
            logger.debug("Request to index {}", u);
            if (pluginsName == null) {
              tasks.addAll(pc.index(u));
            } else {
              for (String pluginName : pluginsName) {
                tasks.addAll(pc.index(pluginName, u));
              }
            }
        } catch (URISyntaxException ex) {
            logger.debug("Client provided bad URI");
            resp.sendError(400);
            return;
        }
    }

    for (Task<Report> t : tasks) {
      taskObjs.add(RunningIndexTasks.getInstance().asJSON(t));
    }

    // waiting is bad, clearing all this and giving an ok
    resp.setStatus(200);
    JSONObject reply = new JSONObject()
       .element("tasks", tasks);
    resp.setContentType("application/json");
    resp.getWriter().print(reply.toString());
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

  private static URI encodeURI(String uri) throws URISyntaxException {
      return new URI(uri.replaceAll(" ", "%20"));
  }
}
