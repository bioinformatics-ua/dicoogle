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

package pt.ua.dicoogle.server.web.servlets.plugins;

import net.sf.json.JSONObject;
import org.json.JSONException;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.plugins.DeadPlugin;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Retrieval of Dicoogle plugin information.
 * 
 * <b>This API is currently unstable.</b>
 *
 * @author Eduardo Pinho
 */
public class PluginsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PluginsServlet.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        List<String> pathParts = sanitizedSubpathParts(req);

        if (pathParts.size() > 1) {
            sendError(resp, 400, "Illegal plugin request URI: resource path too deep");
            return;
        }

        Set<String> validTypes = new HashSet<>();
        Collections.addAll(validTypes, "query", "index", "storage", "servlet", "set", "dead");

        boolean all = pathParts.isEmpty();
        String type = !all ? pathParts.get(0) : null;

        if (type != null && !validTypes.contains(type)) {
            sendError(resp, 400, "Illegal plugin request type");
            return;
        }

        final PluginController pc = PluginController.getInstance();

        try {
            JSONWriter writer = new JSONWriter(resp.getWriter());

            writer.object(); // begin response

            if (!"dead".equals(type) && !"set".equals(type)) {
                writer.key("plugins").array(); // begin plugins

                if (all || "query".equals(type)) {
                    // Query Plugins
                    List<QueryInterface> qlist = sorted(pc.getQueryPlugins(false));
                    for (QueryInterface p : qlist) {
                        writeQueryPlugin(writer, p);
                    }
                }

                if (all || "index".equals(type)) {
                    // Indexer Plugins
                    List<IndexerInterface> ilist = sorted(pc.getIndexingPlugins(false));
                    for (IndexerInterface p : ilist) {
                        writeIndexPlugin(writer, p);
                    }
                }

                if (all || "storage".equals(type)) {
                    // Storage Plugins
                    List<StorageInterface> slist = sorted(pc.getStoragePlugins(false));
                    for (StorageInterface p : slist) {
                        writeStoragePlugin(writer, p);
                    }
                }

                if (all || "servlet".equals(type)) {
                    // Servlet Plugins
                    List<JettyPluginInterface> servlist = sorted(pc.getServletPlugins(false));
                    for (JettyPluginInterface p : servlist) {
                        writeServletPlugin(writer, p);
                    }
                }

                writer.endArray(); // end plugins
            }

            if (all || "set".equals(type)) {
                writer.key("sets").array(); // begin plugin sets
                for (String p : pc.getPluginSetNames()) {
                    writer.value(p);
                }
                writer.endArray(); // end plugin sets
            }

            if (all || "dead".equals(type)) {
                writer.key("dead").array(); // begin dead plugins
                for (DeadPlugin p : pc.getDeadPluginSets()) {
                    writer.object()
                          .key("name").value(p.getName())
                          .key("cause").object()
                            .key("class").value(p.getCause().getClass().getSimpleName())
                            .key("message").value(p.getCause().getMessage())
                            .endObject()
                          .endObject();
                }
                writer.endArray(); // end dead plugins
            }

            writer.endObject(); // end response
        } catch (JSONException ex) {
            throw new IOException("Failed JSON serialization", ex);
        }
    }

    private static <T extends DicooglePlugin> List<T> sorted(Collection<T> col) {
        final Comparator<DicooglePlugin> byEnabledThenName = new Comparator<DicooglePlugin>() {
            @Override
            public int compare(DicooglePlugin p1, DicooglePlugin p2) {
                int c1 = Boolean.compare(p1.isEnabled(), p2.isEnabled());
                if (c1 != 0) return c1;
                return p1.getName().compareTo(p2.getName());
            }
        };
        List<T> l = new ArrayList<>(col);
        Collections.sort(l, byEnabledThenName);
        return l;
    }

    private static JSONWriter writeBaseProps(JSONWriter writer, DicooglePlugin plugin, String type) throws JSONException {
        return writer.key("name").value(plugin.getName())
                .key("type").value(type)
                .key("enabled").value(plugin.isEnabled());
    }

    private static JSONWriter writeQueryPlugin(JSONWriter writer, QueryInterface plugin) throws JSONException {
        return writeBaseProps(writer.object(), plugin, "query")
                .key("dim").value(null)
                .endObject();
    }

    private static JSONWriter writeIndexPlugin(JSONWriter writer, IndexerInterface plugin) throws JSONException {
        return writeBaseProps(writer.object(), plugin, "index")
                .key("dim").value(null)
                .endObject();
    }

    private static JSONWriter writeStoragePlugin(JSONWriter writer, StorageInterface plugin) throws JSONException {
        return writeBaseProps(writer.object(), plugin, "storage")
                .key("scheme").value(plugin.getScheme())
                .key("default").value(null)
                .endObject();
    }

    private static JSONWriter writeServletPlugin(JSONWriter writer, JettyPluginInterface plugin) throws JSONException {
        return writeBaseProps(writer.object(), plugin, "servlet")
                .key("endpoints").value(null)
                .endObject();
    }

    private List<String> sanitizedSubpathParts(HttpServletRequest req) {
        String subpath = req.getRequestURI().substring(req.getServletPath().length());

        String[] subpathParts = subpath.split("/");

        List<String> l = new ArrayList<>();
        for (String s : subpathParts) {
            if (!s.isEmpty()) {
                l.add(s);
            }
        }
        return l;
    }

    private static void sendError(HttpServletResponse resp, int code, String message) throws IOException {
        resp.setStatus(code);
        JSONObject obj = new JSONObject();
        obj.put("error", message);
        resp.getWriter().append(obj.toString());
    }
}
