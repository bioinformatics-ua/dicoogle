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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.ua.dicoogle.plugins.PluginController;

/** Unindexer servlet.
 * 
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
public class UnindexServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(UnindexServlet.class.getName());
    
    /**
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Getting Parameters.
        String[] uris = req.getParameterValues("uri");
        String[] providerArr = req.getParameterValues("provider");

        List<String> providers = providerArr != null ? Arrays.asList(providerArr) : null;

        if (uris == null) {
            resp.sendError(400, "No uri provided");
            return;
        }

        URI[] effUris = new URI[uris.length];
        try {
            for (int i = 0 ; i < effUris.length ; i++) {
                effUris[i] = new URI(uris[i]);
            }
        } catch (URISyntaxException ex) {
            logger.log(Level.INFO, "Received bad URI", ex);
            resp.sendError(400, "Bad URI: " + ex.getInput());
            return;
        }

        // unindex
        for (URI uri : effUris) {
            try {
                PluginController.getInstance().unindex(uri, providers);
            } catch (RuntimeException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        logger.log(Level.INFO, "Finished unindexing {0}", Arrays.asList(uris));
        resp.setStatus(200);
    }
}
