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
import java.io.PrintWriter;
import java.util.Collection;
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
        String process = req.getParameter("process");
        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(slotId) && StringUtils.isEmpty(module)) {
            logger.info("name, module or slot-id not provided");
            resp.sendError(400);
        }

        if (name != null) {
            this.getPlugin(resp, name);
        } else if (module != null) {
            boolean doProcess = process == null || Boolean.parseBoolean(process);
            this.getModule(resp, module, doProcess);
        } else {
            this.getPluginsBySlot(resp, slotId);
        }
    }

    /** Retrieve plugins. */
    private void getPluginsBySlot(HttpServletResponse resp, String slotId) throws IOException {
        Collection<WebUIPlugin> plugins = PluginController.getInstance().getWebUIPlugins(slotId);
        String acc = "{ plugins: [";
        for (WebUIPlugin plugin : plugins) {
            acc += PluginController.getInstance().getWebUIPackageJSON(plugin.getName()) + ",";
        }
        acc = acc.substring(0,acc.length()-1) + "] }";
        resp.setContentType("application/json");
        resp.getWriter().append(acc);
    }

    private void getPlugin(HttpServletResponse resp, String name) throws IOException {
        resp.setContentType("application/json");
        String json = PluginController.getInstance().getWebUIPackageJSON(name);
        resp.getWriter().append(json);
    }

    private void getModule(HttpServletResponse resp, String name, boolean process) throws IOException {
        resp.setContentType("application/javascript");
        WebUIPlugin plugin = PluginController.getInstance().getWebUIPlugin(name);
        if (plugin == null) {
            resp.sendError(404);
            return;
        }
        String js = PluginController.getInstance().getWebUIModuleJS(name);
        
        PrintWriter writer = resp.getWriter();
        if (process) {
            writer.append("var module;var __Dicoogle_tmp__=module;module={};\n");
        }
        writer.append(js);
        if (process) {
            writer.printf("var __Dicoogle_plugin__=module.exports;module=__Dicoogle_tmp__;\n"
                    +   "DicoogleWeb.onRegister(new __Dicoogle_plugin__(),'%s','%s');",
                    plugin.getName(), plugin.getSlotId());
        }
    }
}
