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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import pt.ua.dicoogle.plugins.PluginController;

/**
 * Retrieval of web UI plugins.
 *
 * @author Eduardo Pinho
 */
public class WebUIServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(WebUIServlet.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String slotId = req.getParameter("slot-id");
        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(slotId)) {
            logger.info("name or slot-id not provided");
            resp.sendError(400);
        }

        if (name != null) {
            this.getPlugin(req, resp, name);
        } else {
            this.getPluginsBySlot(req, resp, slotId);
        }
 
    }

    private void getPluginsBySlot(HttpServletRequest req, HttpServletResponse resp, String slotId) throws IOException {
        PluginController.getInstance().getWebUIPlugins(slotId);
//        resp.setContentType("application/json");
//        resp.getWriter().append(json);
    }

    private void getPlugin(HttpServletRequest req, HttpServletResponse resp, String name) throws IOException {
        PluginController.getInstance().getWebUIPlugin(name);
        resp.setContentType("application/json");
        String json = "";
        resp.getWriter().append(json);
    }
}
