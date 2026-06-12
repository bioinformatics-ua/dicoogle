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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

/**
 * Servlet for listing DICOM files in a storage directory.
 *
 * GET /storage/list?uri=storage://path/to/directory
 *
 * Resolves the storage plugin by the URI scheme, then lists the immediate
 * children of the given location. Directories have a trailing '/' in the URI.
 *
 * Returns:
 *   { "results": [ { "uri": "file:/path/to/file.dcm", "size": 12345 }, ... ] }
 *
 * @author Dicoogle
 */
public class StorageListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(StorageListServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String uriParam = req.getParameter("uri");
        if (uriParam == null || uriParam.isEmpty()) {
            ResponseUtil.sendError(resp, 400, "Missing required parameter: uri");
            return;
        }

        URI location;
        try {
            location = URI.create(uriParam);
        } catch (IllegalArgumentException ex) {
            ResponseUtil.sendError(resp, 400, "Invalid URI: " + uriParam);
            return;
        }

        StorageInterface storage = PluginController.getInstance().getStorageForSchema(location);
        if (storage == null) {
            ResponseUtil.sendError(resp, 404, "No storage plugin found for scheme: " + location.getScheme());
            return;
        }

        List<JSONObject> results = new ArrayList<>();

        try {
            // Use list() for non-recursive listing, collect to list
            List<URI> items = storage.list(location).collect(Collectors.toList());
            for (URI itemUri : items) {
                JSONObject item = new JSONObject();
                item.element("uri", itemUri.toString());

                // Try to get size via get() if it's a file (not a directory)
                if (!itemUri.toString().endsWith("/")) {
                    try {
                        StorageInputStream itemStream = storage.get(itemUri);
                        if (itemStream != null) {
                            item.element("size", itemStream.getSize());
                        }
                    } catch (Exception ex) {
                        // Size unavailable — omit it
                    }
                }

                results.add(item);
            }
        } catch (Exception ex) {
            logger.error("Failed to list storage at: {}", uriParam, ex);
            ResponseUtil.sendError(resp, 500, "Failed to list storage: " + ex.getMessage());
            return;
        }

        resp.setContentType("application/json");
        JSONObject response = new JSONObject();
        JSONArray resultsArray = new JSONArray();
        for (JSONObject item : results) {
            resultsArray.add(item);
        }
        response.element("results", resultsArray);
        resp.getWriter().print(response.toString());
    }
}
