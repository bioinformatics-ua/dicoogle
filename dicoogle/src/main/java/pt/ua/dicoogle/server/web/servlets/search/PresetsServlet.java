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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import pt.ua.dicoogle.server.users.PresetsHandler;
import pt.ua.dicoogle.server.users.User;
import pt.ua.dicoogle.server.web.auth.Authentication;

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
            resp.sendError(401);
        }

        String presetName = req.getParameter("name");
        String presetContent = req.getParameter("content");
        if (presetName == null || presetContent == null) {
            resp.sendError(400, "Invalid parameters.");
        }

        if (!PresetsHandler.savePreset(user.getUsername(), presetName, presetContent)) {
            resp.sendError(500, "Could not save preset.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getHeader("Authorization");
        User user = Authentication.getInstance().getUsername(token);
        if (user == null) {
            resp.sendError(401);
        }

        Map<String, String> presets = PresetsHandler.getPresets(user.getUsername());
        if (presets == null) {
            resp.sendError(500, "Could not retrieve presets.");
        }

        JSONArray jsonPresets = new JSONArray();
        for (String presetName: presets.keySet()) {
            JSONObject jsonPreset = new JSONObject();
            jsonPreset.put("name", presetName);
            jsonPreset.put("content", presets.get(presetName));

            jsonPresets.add(jsonPreset);
        }

        resp.setContentType("application/json");
        resp.getWriter().write(jsonPresets.toString());
    }
}
