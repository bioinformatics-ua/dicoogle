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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.plugins.webui.WebUIPlugin;
import pt.ua.dicoogle.server.users.Role;
import pt.ua.dicoogle.server.users.RolesStruct;
import pt.ua.dicoogle.server.users.User;
import pt.ua.dicoogle.server.web.auth.Authentication;

/**
 * Retrieval of web UI plugins and respective packages/modules.
 * 
 * <b>This API is unstable. It is currently only compatible with dicoogle-webcore 0.12.x and 0.13.x</b>
 *
 * @author Eduardo Pinho
 */
public class WebUIServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(WebUIServlet.class);
    
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
            resp.getWriter().append(this.getPluginsBySlot(req, resp, slotIdArr));
        }
    }

    /** Retrieve plugins. */
    private String getPluginsBySlot(HttpServletRequest req,
                                    HttpServletResponse resp, String... slotIds) throws IOException {
        String token = req.getHeader("Authorization");
        User user = Authentication.getInstance().getUsername(token);

        Collection<WebUIPlugin> plugins = PluginController.getInstance().getWebUIPlugins(slotIds);
        List<String> pkgList = new ArrayList<>(plugins.size());
        for (WebUIPlugin plugin : plugins) {

            String pkg = PluginController.getInstance().getWebUIPackageJSON(plugin.getName());
            boolean hasUserAllowPlugin= false;
            for (String r:plugin.getRoles())
            {
                Role rr = RolesStruct.getInstance().getRole(r);

                hasUserAllowPlugin = RolesStruct.getInstance().hasRole(user, rr);
                if (hasUserAllowPlugin)
                    break;
            }


            if (pkg != null&&(hasUserAllowPlugin||(user!=null&&user.isAdmin()))) {
                pkgList.add(pkg);
            }
        }
        JSONObject o = new JSONObject();
        JSONArray pkgArr = new JSONArray();
        for (String n : pkgList) {
            pkgArr.add(n);
        }
        o.element("plugins", pkgArr);
        return o.toString();
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
        
        return processModule(name, js, process);
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

    /**
     * @deprecated Use the endpoint associated to the {@link WebUIModuleServlet} instead
     */
    @Deprecated
    public static String processModule(String name, String module, boolean process) {
        StringBuilder writer = new StringBuilder();
        if (process) {
            writer.append("(function(root,factory){\n");
            writer.append("var w=root.DicoogleWebcore||require(\"dicoogle-webcore\");");
            writer.append("if (w.__esModule)w=w.default;");
            writer.append("var c=(root.Dicoogle||require(\"dicoogle-client\"))();");
            writer.append("var m={exports:{}};");
            writer.append("factory(c,m,m.exports);");
            writer.append("var o=m.exports.__esModule?m.exports.default:m.exports;");
            writer.append("w.constructors[\"");
              writer.append(name);
              writer.append("\"]=o;");
            writer.append("w.onRegister(new o(),\"");
              writer.append(name);
              writer.append("\");");
            writer.append("})(this,function(Dicoogle,module,exports){\n");
        }
        writer.append(module);
        if (process) {
            writer.append("});\n");
        }
        return writer.toString();
    }
}
