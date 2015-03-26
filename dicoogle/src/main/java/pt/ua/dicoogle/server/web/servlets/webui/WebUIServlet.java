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

package pt.ua.dicoogle.server.web.servlets.webui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.plugins.webui.WebUIPlugin;

/**
 * Retrieval of web UI plugins.
 * 
 * <b>This API is unstable. It is currently compatible with dicoogle-webcore 0.3.0</b>
 *
 * @author Eduardo Pinho
 */
public class WebUIServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(WebUIServlet.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String[] slotIdArr = req.getParameterValues("slot-id");
        String module = req.getParameter("module");
        String process = req.getParameter("process");

        if (name != null) {
            resp.setContentType("application/json");
            resp.getWriter().append(this.getPlugin(resp, name));
        } else if (module != null) {
            resp.setContentType("application/javascript");
            boolean doProcess = process == null || Boolean.parseBoolean(process);
            resp.getWriter().append(this.getModule(resp, module, doProcess));
        } else {
            resp.setContentType("application/json");
            resp.getWriter().append(this.getPluginsBySlot(resp, slotIdArr));
        }
    }

    /** Retrieve plugins. */
    private String getPluginsBySlot(HttpServletResponse resp, String... slotIds) throws IOException {
        Collection<WebUIPlugin> plugins = PluginController.getInstance().getWebUIPlugins(slotIds);
        List<String> pkgList = new ArrayList<>(plugins.size());
        for (WebUIPlugin plugin : plugins) {
            String pkg = PluginController.getInstance().getWebUIPackageJSON(plugin.getName());
            if (pkg != null) {
                pkgList.add(pkg);
            }
        }
        return "{\"plugins\":[" + String.join(",", pkgList) + "]}";
    }

    private String getPlugin(HttpServletResponse resp, String name) throws IOException {
        resp.setContentType("application/json");
        String json = PluginController.getInstance().getWebUIPackageJSON(name);
        return json;
    }

    private String getModule(HttpServletResponse resp, String name, boolean process) throws IOException {
        resp.setContentType("application/javascript");
        WebUIPlugin plugin = PluginController.getInstance().getWebUIPlugin(name);
        if (plugin == null) {
            return null;
        }
        String js = PluginController.getInstance().getWebUIModuleJS(name);
        
        StringBuilder writer = new StringBuilder();
        writer.append(js);
        if (process) {
            writer.append(String.format("var __Dicoogle_plugin__=%s;\n"
                    +   "DicoogleWeb.onRegister(new __Dicoogle_plugin__(), '%s');",
                    camelize(plugin.getName()), plugin.getName()));
        }
        return writer.toString();
    }
    
    /** Convert from dash-lowercase to camelCase
     * @param s a string in dash-lowercase
     * @return a string in camelCase
     */
    public static String camelize(String s) {
        String [] words = s.split("-");
        if (words.length == 0) return "";
        String t = words[0];
        for (int i = 1 ; i < words.length ; i++) {
            if (!words[i].isEmpty()) {
                t += Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
            }
        }
        return t;
    }
}
