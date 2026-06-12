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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.StorageInterface;
import pt.ua.dicoogle.sdk.StorageInputStream;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

/**
 * Unified storage servlet.
 *
 * POST /storage?scheme=file  — store DICOM bytes
 * GET  /storage?uri=...     — retrieve file bytes
 *
 * GET parameters are read from the query string directly
 * (not via getParameter) to avoid Jetty parsing the binary POST body.
 */
public class StorageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(StorageServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String scheme = getQueryParam(req.getQueryString(), "scheme");
        if (scheme == null || scheme.isEmpty()) {
            scheme = "file";
        }

        URI schemeUri = URI.create(scheme + ":/");
        StorageInterface storage = PluginController.getInstance().getStorageForSchema(schemeUri);

        if (storage == null) {
            ResponseUtil.sendError(resp, 404, "No storage plugin found for scheme: " + scheme);
            return;
        }

        try (DicomInputStream dis = new DicomInputStream(req.getInputStream())) {
            DicomObject dicomObj = dis.readDicomObject();
            URI storedUri = storage.store(dicomObj);

            if (storedUri == null) {
                ResponseUtil.sendError(resp, 500, "Storage plugin returned null URI for scheme: " + scheme);
                return;
            }

            resp.setContentType("application/json");
            JSONObject result = new JSONObject();
            result.element("uri", storedUri.toString());
            resp.getWriter().print(result.toString());

        } catch (Exception ex) {
            logger.error("Failed to store DICOM via HTTP (scheme={})", scheme, ex);
            ResponseUtil.sendError(resp, 500, "Failed to store DICOM: " + ex.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String uriParam = getQueryParam(req.getQueryString(), "uri");
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

        StorageInputStream item = storage.get(location);
        if (item == null) {
            ResponseUtil.sendError(resp, 404, "File not found: " + uriParam);
            return;
        }

        resp.setContentType("application/octet-stream");

        try (InputStream in = item.getInputStream(); OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception ex) {
            logger.error("Failed to read file from storage: {}", uriParam, ex);
        }
    }

    private static String getQueryParam(String queryString, String key) {
        if (queryString == null || queryString.isEmpty()) {
            return null;
        }
        for (String param : queryString.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                try {
                    return java.net.URLDecoder.decode(kv[1], "UTF-8");
                } catch (Exception e) {
                    return kv[1];
                }
            }
        }
        return null;
    }
}
