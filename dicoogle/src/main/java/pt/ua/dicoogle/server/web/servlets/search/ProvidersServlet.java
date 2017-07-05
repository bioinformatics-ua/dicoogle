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
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import pt.ua.dicoogle.plugins.PluginController;
import pt.ua.dicoogle.sdk.DicooglePlugin;

/**
 * Retrieve active providers
 *
 * @author Frederico Silva <fredericosilva@ua.pt>
 */
public class ProvidersServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String paramType = req.getParameter("type");
        
        final String type = paramType != null ? paramType : "query";
		Collection<String> activeProviders;
        switch (type) {
            case "index":
                activeProviders = getIndexerNames();
                break;
            case "storage":
                activeProviders = getStorageNames();
                break;
            case "query":
                activeProviders = PluginController.getInstance().getQueryProvidersName(true);
                break;
            default:
                resp.sendError(400);
                return;
        }
		
        JSONArray json = new JSONArray();
        json.addAll(activeProviders);
		
        resp.setContentType("application/json");
		resp.getWriter().print(json.toString());
			
	}
	
    private static Collection<String> getIndexerNames() {
        return getPluginNames(PluginController.getInstance().getIndexingPlugins(true));
    }

    private Collection<String> getStorageNames() {
        return getPluginNames(PluginController.getInstance().getStoragePlugins(true));
    }
    
    private static Collection<String> getPluginNames(Collection<? extends DicooglePlugin> plugins) {
        Collection<String> c = new ArrayList<>(plugins.size());
        for (DicooglePlugin p : plugins) {
            c.add(p.getName());
        }
        return c;
    }
}
