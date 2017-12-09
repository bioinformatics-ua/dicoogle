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

package pt.ua.dicoogle.server.web.servlets.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import pt.ua.dicoogle.server.users.UserExportPresets;
import pt.ua.dicoogle.server.users.User;
import pt.ua.dicoogle.server.web.auth.Authentication;
import pt.ua.dicoogle.server.web.utils.PathUtils;
import pt.ua.dicoogle.server.web.utils.ResponseUtil;

/**
 * Saves and lists user's presets for fields to be exported.
 *
 * @author Leonardo Oliveira <leonardooliveira@ua.pt>
 */
public class PresetsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getHeader("Authorization");
        User user = Authentication.getInstance().getUsername(token);
        if (user == null) {
            ResponseUtil.sendError(resp, 401, "Unauthorized access to this user.");
            return;
        }

        List<String> pathParts = PathUtils.sanitizedSubpathParts(req);

        // the format must be /<username>/<preset-name>
        if (pathParts.size() != 2) {
            ResponseUtil.sendError(resp, 400, "Illegal plugin request URI: wrong resource path size");
            return;
        }

        String username = pathParts.get(0);
        String presetName = pathParts.get(1);
        String[] fields = req.getParameterValues("field");

        if (username.isEmpty() || presetName.isEmpty() || fields == null) {
            ResponseUtil.sendError(resp, 400, "Invalid parameters.");
            return;
        }

        // if the specified user is not the requester, they must have admin privileges
        if (!username.equals(user.getUsername()) && !user.isAdmin()) {
            ResponseUtil.sendError(resp, 403, "Access denied to this user's presets.");
            return;
        }

        UserExportPresets.savePreset(username, presetName, fields);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getHeader("Authorization");
        User user = Authentication.getInstance().getUsername(token);
        if (user == null) {
            ResponseUtil.sendError(resp, 401, "Unauthorized access to this user.");
            return;
        }

        List<String> pathParts = PathUtils.sanitizedSubpathParts(req);

        // the format must be /<username>
        if (pathParts.size() != 1) {
            ResponseUtil.sendError(resp, 400, "Illegal plugin request URI: wrong resource path size");
            return;
        }

        String username = pathParts.get(0);

        // if the specified user is not the requester, they must have admin privileges
        if (!username.equals(user.getUsername()) && !user.isAdmin()) {
            ResponseUtil.sendError(resp, 403, "Access denied to this user's presets.");
            return;
        }

        Map<String, String[]> presets = UserExportPresets.getPresets(username);
        JSONArray jsonPresets = new JSONArray();
        for (String presetName: presets.keySet()) {
            JSONObject jsonPreset = new JSONObject();

            jsonPreset.put("name", presetName);

            JSONArray jsonFields = new JSONArray();
            String[] fields = presets.get(presetName);
            for (String field: fields) {
                jsonFields.add(field);
            }
            jsonPreset.put("fields", jsonFields);

            jsonPresets.add(jsonPreset);
        }

        resp.setContentType("application/json");
        resp.getWriter().write(jsonPresets.toString());
    }
}
