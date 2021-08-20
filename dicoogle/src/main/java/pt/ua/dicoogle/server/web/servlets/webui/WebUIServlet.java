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
 * <b>This API is unstable. It is currently only compatible with dicoogle-webcore >= 0.12.x && <= 0.15.x</b>
 *
 * @author Eduardo Pinho
 */
public class WebUIServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(WebUIServlet.class);
    private static final String PERMISSIVE_WEBUI_PROPERTY = System.getProperty("webui.permissive");
    private static final boolean PERMISSIVE_WEBUI =
            !(PERMISSIVE_WEBUI_PROPERTY == null || PERMISSIVE_WEBUI_PROPERTY.isEmpty()
                    || "0".equals(PERMISSIVE_WEBUI_PROPERTY) || "false".equalsIgnoreCase(PERMISSIVE_WEBUI_PROPERTY));

    static {
        if (PERMISSIVE_WEBUI) {
            logger.info("Web UI servlet loaded in permissive mode");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String[] slotIdArr = req.getParameterValues("slot-id");
        String module = req.getParameter("module");
        String process = req.getParameter("process");

        resp.setContentType("application/json");
        if (name != null) {
            resp.getWriter().append(this.getPlugin(resp, name));
        } else if (module != null) {
            /**
             * deprecated: Use {@link WebUIModuleServlet} instead
             */
            resp.setContentType("application/javascript");
            boolean doProcess = process == null || Boolean.parseBoolean(process);
            resp.sendRedirect("/webui/module/" + module + (!doProcess ? "?process=false" : ""));
        } else {
            resp.getWriter().append(this.getPluginsBySlot(req, slotIdArr));
        }
    }

    /** Retrieve plugins. */
    private String getPluginsBySlot(HttpServletRequest req, String... slotIds) throws IOException {
        String token = req.getHeader("Authorization");
        User user = Authentication.getInstance().getUsername(token);

        Collection<WebUIPlugin> plugins = PluginController.getInstance().getWebUIPlugins(slotIds);
        List<String> pkgList = new ArrayList<>(plugins.size());
        for (WebUIPlugin plugin : plugins) {

            String pkg = PluginController.getInstance().getWebUIPackageJSON(plugin.getName());
            if (pkg == null) {
                logger.warn("Ignoring web plugin {}", plugin.getName());
                continue;
            }

            final RolesStruct roles = RolesStruct.getInstance();
            boolean pluginOk = PERMISSIVE_WEBUI || user.isAdmin() || plugin.getRoles().isEmpty();
            if (!pluginOk) {
                for (String r : plugin.getRoles()) {
                    Role rr = roles.getRole(r);

                    if (roles.hasRole(user, rr)) {
                        pluginOk = true;
                        break;
                    }
                }
            }
            if (pluginOk) {
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

    /** Convert from dash-lowercase to camelCase
     * @param s a string in dash-lowercase
     * @return a string in camelCase
     */
    public static String camelize(String s) {
        String[] words = s.split("-");
        if (words.length == 0)
            return "";
        StringBuilder t = new StringBuilder();
        t.append(words[0]);
        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                t.append(Character.toUpperCase(words[i].charAt(0)));
                t.append(words[i].substring(1));
            }
        }
        return t.toString();
    }
}
