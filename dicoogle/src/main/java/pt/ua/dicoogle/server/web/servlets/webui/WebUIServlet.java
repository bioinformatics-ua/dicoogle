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
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.plugins.webui.WebUIPlugin;

/**
 * Retrieval of web UI plugins.
 * 
 * <b>This API is unstable.</b>
 *
 * @author Eduardo Pinho
 */
public class WebUIServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(WebUIServlet.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String slotId = req.getParameter("slot-id");
        String module = req.getParameter("module");
        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(slotId) && StringUtils.isEmpty(module)) {
            logger.info("name, module or slot-id not provided");
            resp.sendError(400);
        }

        if (name != null) {
            this.getPlugin(req, resp, name);
        } else if (module != null) {
            this.getModule(req, resp, module);
        } else {
            this.getPluginsBySlot(req, resp, slotId);
        }
    }

    /** Retrieve plugins. */
    private void getPluginsBySlot(HttpServletRequest req, HttpServletResponse resp, String slotId) throws IOException {
        Collection<WebUIPlugin> plugins = PluginController.getInstance().getWebUIPlugins(slotId);
        String acc = "{ plugins: [";
        for (WebUIPlugin plugin : plugins) {
            acc += PluginController.getInstance().getWebUIPackageJSON(plugin.getName()) + ",";
        }
        acc = acc.substring(0,acc.length()-1) + "] }";
        resp.setContentType("application/json");
        resp.getWriter().append(acc);
    }

    private void getPlugin(HttpServletRequest req, HttpServletResponse resp, String name) throws IOException {
        resp.setContentType("application/json");
        String json = PluginController.getInstance().getWebUIPackageJSON(name);
        resp.getWriter().append(json);
    }

    private void getModule(HttpServletRequest req, HttpServletResponse resp, String name) throws IOException {
        resp.setContentType("application/json");
        String json = PluginController.getInstance().getWebUIModuleJS(name);
        resp.getWriter().append(json);
    }
}
